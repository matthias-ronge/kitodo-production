/*
 * (c) Kitodo. Key to digital objects e. V. <contact@kitodo.org>
 *
 * This file is part of the Kitodo project.
 *
 * It is licensed under GNU General Public License version 3 or later.
 *
 * For the full copyright and license information, please read the
 * GPL3-License.txt file that was distributed with this source code.
 */

package org.kitodo.production.services.command;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.URI;
import java.nio.file.Paths;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kitodo.api.dataformat.LogicalDivision;
import org.kitodo.api.dataformat.MediaVariant;
import org.kitodo.api.dataformat.PhysicalDivision;
import org.kitodo.api.dataformat.ProcessingNote;
import org.kitodo.api.dataformat.View;
import org.kitodo.api.dataformat.Workpiece;
import org.kitodo.config.ConfigCore;
import org.kitodo.data.database.beans.Batch;
import org.kitodo.data.database.beans.Comment;
import org.kitodo.data.database.beans.Folder;
import org.kitodo.data.database.beans.Process;
import org.kitodo.data.database.beans.Property;
import org.kitodo.data.database.beans.Task;
import org.kitodo.data.database.exceptions.DAOException;
import org.kitodo.data.exceptions.DataException;
import org.kitodo.exceptions.CommandException;
import org.kitodo.exceptions.ProcessGenerationException;
import org.kitodo.production.forms.ProcessForm;
import org.kitodo.production.metadata.MetadataEditor;
import org.kitodo.production.model.Subfolder;
import org.kitodo.production.services.ServiceManager;
import org.kitodo.production.services.data.BatchService;
import org.kitodo.production.services.data.CommentService;
import org.kitodo.production.services.data.FolderService;
import org.kitodo.production.services.data.ProcessService;
import org.kitodo.production.services.data.ProjectService;
import org.kitodo.production.services.data.PropertyService;
import org.kitodo.production.services.data.TaskService;
import org.kitodo.production.services.dataformat.MetsService;
import org.kitodo.production.services.file.FileService;
import org.kitodo.production.version.KitodoVersion;

/**
 * Creates an exploded copy of a process. The original process is renamed to
 * {@code ..._orig}. A new process of the previous title is created parent
 * process, and the first logical hierarchy level is split off into child
 * processes. Apart from the name change, the original process is not changed.
 * It can be deleted manually if you are satisfied with the result.
 */
public class ExplodeService implements Runnable {

    private static final Logger logger = LogManager.getLogger(ExplodeService.class);

    private final BatchService batchService = BatchService.getInstance();
    private final CommentService commentService = CommentService.getInstance();
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
    private final FileService fileService = ServiceManager.getFileService();
    private final MetsService metsService = ServiceManager.getMetsService();
    private final ProcessForm processForm = new ProcessForm();
    private final ProcessService processService = ServiceManager.getProcessService();
    private final PropertyService propertyService = PropertyService.getInstance();
    private final TaskService taskService = TaskService.getInstance();

    // process to be exploded
    private Process originProcess;

    // total work steps, denominator for percentage display
    private int steps = Integer.MAX_VALUE;

    // work steps completed, numerator for percentage display
    private final MutableInt went = new MutableInt(0);

    /**
     * Create a new service to explode processes.
     *
     * @param originProcess
     *            process to be exploded
     */
    public ExplodeService(Process originProcess) {
        this.originProcess = originProcess;
    }

    /* Starts program execution. I haven't decided yet whether we should just do
     * this directly or whether we put it in the task manager. */
    @Override
    public void run() {
        // cannot run if process is currently opened
        if (Objects.nonNull(this.originProcess.getBlockedUser())) {
            throw new IllegalStateException("Process blocked by user: " + this.originProcess.getBlockedUser()
                    .getFullName());
        }
        // cannot run on hierarchical process
        if (Objects.nonNull(this.originProcess.getChildren()) && this.originProcess.getChildren().size() > 0) {
            throw new IllegalStateException("Cannot explode parent process");
        }

        try {
            URI originWorkpieceFilePath = fileService.getMetadataFilePath(this.originProcess);
            Workpiece originWorkpiece = metsService.loadWorkpiece(originWorkpieceFilePath);
            List<LogicalDivision> children = originWorkpiece.getLogicalStructure().getChildren();
            this.steps = 4 + (3 * children.size()) + 1; // calculate number of steps for percent
            this.went.increment(); // one step is completed for percent

            String originTitle = renameOriginProcess(this.originProcess);
            this.went.increment();

            Process parentProcess = createPartialProcess(originTitle, this.originProcess, originWorkpiece
                    .getLogicalStructure().getType(), null);
            this.went.increment();

            createPartialWorkpiece(this.originProcess, parentProcess, originWorkpiece, originWorkpiece
                    .getLogicalStructure(), false);
            this.went.increment();

            Map<String, MutableInt> appendixCounts = initializeAppendixCounts(children);
            Collection<PhysicalDivision> copiedMedia = new ArrayList<>();
            for (int i = 0; i < children.size(); i++) {
                LogicalDivision childDivision = children.get(i);
                String childTitle = formChildTitle(originTitle, childDivision, appendixCounts);
                Process childProcess = createPartialProcess(childTitle, this.originProcess, childDivision.getType(),
                    parentProcess);
                parentProcess.getChildren().add(childProcess);
                this.went.increment();

                Workpiece childWorkpiece = createPartialWorkpiece(this.originProcess, childProcess, originWorkpiece,
                    childDivision, true);
                MetadataEditor.addLink(parentProcess, Integer.toString(i), childProcess.getId());
                this.went.increment();

                copiedMedia.addAll(copyMedia(childWorkpiece, this.originProcess, childProcess));
                this.went.increment();
            }
            copyRemainingMedia(copiedMedia, this.originProcess, parentProcess);
            this.went.increment();

        // error barrier
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        } catch (CommandException | DAOException | DataException e) {
            throw new UndeclaredThrowableException(e);
        }
    }

    /**
     * Renames the original process to "..._orig".
     * 
     * @param originProcess
     *            process to rename
     * 
     * @return the original process title, which is used for the new parent
     *         process
     * @throws IOException
     *             if a file system operation fails
     * @throws DataException
     *             if an error occurs when accessing ElasticSearch
     */
    String renameOriginProcess(Process originProcess) throws IOException, DataException {
        String originTitle = originProcess.getTitle();
        String newProcessTitle = originTitle + "_orig";
        logger.info("Starting to rename process \"{}\" [{}] to \"{}\" ...", originTitle, originProcess.getId(),
            newProcessTitle);
        processForm.renameProcess(this.originProcess, newProcessTitle);
        ServiceManager.getProcessService().save(originProcess);
        logger.info("Successfully renamed [{}] to \"{}\"", originProcess.getId(), newProcessTitle);
        return originTitle;
    }

    /**
     * Creates a new process object, for the parent process, or one of the new
     * child processes. It gets the same project. The parent gets no production
     * template and no tasks. The children get the production template and tasks
     * from the original process.
     * 
     * @param title
     *            title for the new process
     * @param originProcess
     *            the process to copy from
     * @param baseType
     * @param parent
     *            if a child is created, the new parent process for the child.
     *            {@code null} for creation of the parent process.
     * @return the created process
     * @throws ProcessGenerationException
     *             if the workflow is erroneous
     * @throws DataException
     *             in case of an error in communication with the search engine
     * @throws DAOException
     *             in case of a database error
     * @throws CommandException
     *             if the script_createDirMeta fails
     * @throws IOException
     *             in case of a file system error state
     */
    Process createPartialProcess(String title, Process originProcess, String baseType, Process parent)
            throws CommandException, DAOException, DataException, IOException {

        logger.info("Starting to create new process with name \"{}\" ...", title);
        Process createdProcess = new Process();
        createdProcess.setTitle(title);
        createdProcess.setBaseType(baseType);
        createdProcess.setExported(false);
        createdProcess.setInChoiceListShown(false);
        createdProcess.setNumberOfImages(0); // will be set in copyMedia()
        createdProcess.setOrdering(originProcess.getOrdering());
        createdProcess.setParent(parent);
        createdProcess.setProject(originProcess.getProject());
        createdProcess.setRuleset(originProcess.getRuleset());
        createdProcess.setSortHelperImages(0); // will be set in copyMedia()
        createdProcess.setWikiField(originProcess.getWikiField());

        /* the remaining entities require an ID for the process, so first save
         * and refresh */
        processService.save(createdProcess);
        processService.refresh(createdProcess);

        copyProperties(originProcess.getProperties(), Property::setProcesses, createdProcess,
            createdProcess::setProperties);
        copyProperties(originProcess.getTemplates(), Property::setTemplates, createdProcess,
            createdProcess::setTemplates);
        copyProperties(originProcess.getWorkpieces(), Property::setWorkpieces, createdProcess,
            createdProcess::setWorkpieces);

        if (Objects.isNull(parent)) {
            duplicateComments(originProcess, createdProcess);
        } else {
            for (Batch batch : originProcess.getBatches()) {
                batch.getProcesses().add(createdProcess);
                createdProcess.getBatches().add(batch);
                batchService.save(batch);
            }
            createdProcess.setDocket(originProcess.getDocket());
            createdProcess.setTasks(copyTasks(originProcess, createdProcess));
            createdProcess.setTemplate(originProcess.getTemplate());
        }

        processService.save(createdProcess);

        fileService.createProcessLocation(createdProcess);

        if (Objects.isNull(createdProcess.getParent())) {
            logger.info("Successfully created stand-alone process \"{}\" [{}]", createdProcess.getTitle(),
                createdProcess.getId());
        } else {
            logger.info("Successfully created process \"{}\" [{}] as child of \"{}\" [{}]", createdProcess.getTitle(),
                createdProcess.getId(), createdProcess.getParent().getTitle(), createdProcess.getParent().getId());
        }
        return createdProcess;
    }

    private void copyProperties(List<Property> copyProperties, BiConsumer<Property, List<Process>> setProcessOnProperty,
            Process processUnderCreation, Consumer<List<Property>> setPropertiesWithConsumer) throws DAOException {
        List<Property> kopiert = new ArrayList<>();
        for (Property copyProperty : copyProperties) {
            Property newProperty = new Property();
            newProperty.setTitle(copyProperty.getTitle());
            newProperty.setValue(copyProperty.getValue());
            newProperty.setObligatory(copyProperty.isObligatory());
            newProperty.setDataType(copyProperty.getDataType());
            newProperty.setChoice(copyProperty.getChoice());
            newProperty.setCreationDate(copyProperty.getCreationDate());
            setProcessOnProperty.accept(newProperty, Collections.singletonList(processUnderCreation));
            propertyService.saveToDatabase(newProperty);
            kopiert.add(newProperty);
        }
        setPropertiesWithConsumer.accept(kopiert);
    }

    private void duplicateComments(Process originProcess, Process createdProcess) throws DAOException {
        ArrayList<Comment> newCommentsList = new ArrayList<>();
        for (Comment comment : originProcess.getComments()) {
            Comment duplicatedComment = new Comment();
            duplicatedComment.setMessage(comment.getMessage());
            duplicatedComment.setType(comment.getType());
            duplicatedComment.setCreationDate(comment.getCreationDate());
            duplicatedComment.setCorrectionDate(comment.getCorrectionDate());
            duplicatedComment.setAuthor(comment.getAuthor());
            duplicatedComment.setProcess(createdProcess);

            /* correction comments are converted to normal comments, because the
             * parent no longer has tasks */
            duplicatedComment.setCorrected(false);
            duplicatedComment.setCurrentTask(null);
            duplicatedComment.setCorrectionTask(null);
            newCommentsList.add(duplicatedComment);
        }
        commentService.saveList(newCommentsList);
    }

    private List<Task> copyTasks(Process originProcess, Process processUnderCreation) throws DataException {
        List<Task> newTasks = new ArrayList<>();
        for (Task task : originProcess.getTasks()) {
            Task taskCopy = new Task();
            taskCopy.setTitle(task.getTitle());
            taskCopy.setOrdering(task.getOrdering());
            taskCopy.setProcessingStatus(task.getProcessingStatus());
            taskCopy.setProcessingTime(task.getProcessingTime());
            taskCopy.setProcessingBegin(task.getProcessingBegin());
            taskCopy.setProcessingEnd(task.getProcessingEnd());
            taskCopy.setEditType(task.getEditType());
            taskCopy.setHomeDirectory(task.getHomeDirectory());
            taskCopy.setConcurrent(task.isConcurrent());
            taskCopy.setLast(task.isLast());
            taskCopy.setCorrection(task.isCorrection());
            taskCopy.setTypeMetadata(task.isTypeMetadata());
            taskCopy.setTypeAutomatic(task.isTypeAutomatic());
            taskCopy.setTypeImagesRead(task.isTypeImagesRead());
            taskCopy.setTypeImagesWrite(task.isTypeImagesWrite());
            taskCopy.setTypeGenerateImages(task.isTypeGenerateImages());
            taskCopy.setTypeExportDMS(task.isTypeExportDMS());
            taskCopy.setTypeAcceptClose(task.isTypeAcceptClose());
            taskCopy.setScriptName(task.getScriptName());
            taskCopy.setScriptPath(task.getScriptPath());
            taskCopy.setTypeCloseVerify(task.isTypeCloseVerify());
            taskCopy.setBatchStep(task.isBatchStep());
            taskCopy.setRepeatOnCorrection(task.isRepeatOnCorrection());
            taskCopy.setWorkflowId(task.getWorkflowId());
            taskCopy.setWorkflowCondition(task.getWorkflowCondition());
            taskCopy.setProcessingUser(task.getProcessingUser());
            taskCopy.setTemplate(task.getTemplate());
            taskCopy.setProcess(processUnderCreation);
            taskCopy.setRoles(task.getRoles());
            taskService.save(taskCopy);
            newTasks.add(taskCopy);
        }
        return newTasks;
    }

    /**
     * Creates a partial workpiece file, for the parent or a child. For the
     * parent, the logical hierarchy is flat, and no physical structures are
     * copied initially. For the children, it is logically hierarchical, and the
     * physical structures are copied as long as they are linked. No media files
     * are copied here yet.
     * 
     * @param forProcess
     *            process to which the work piece file should be saved (this is
     *            needed to collect the process directory)
     * @param originWorkpiece
     *            original workpiece from which copying is made
     * @param logicalRoot
     *            new root of the logical structure, from here on it is copied
     * @param deep
     *            if true, the logical structure is processed recursively, and
     *            physical structures and links are copied, otherwise not.
     * @return the new workpiece
     * @throws IOException
     *             if the METS file could not be written
     */
    Workpiece createPartialWorkpiece(Process from, Process forProcess, Workpiece originWorkpiece,
            LogicalDivision logicalRoot, boolean deep) throws IOException {

        logger.info("Starting to create workpiece for process \"{}\" [{}] as {} ...", forProcess.getTitle(),
                forProcess.getId(), deep ? "child" : "parent");

        Workpiece created = new Workpiece();
        created.setCreationDate(new GregorianCalendar());

        created.setId(forProcess.getId().toString());
        for (ProcessingNote existingNote : originWorkpiece.getEditHistory()) {
            created.getEditHistory().add(existingNote);
        }
        ProcessingNote explodeNote = new ProcessingNote();
        explodeNote.setName(String.format("Kitodo Production v. %s, %s", KitodoVersion.getVersion(),
                getClass().getName()));
        explodeNote.setNote(String.format("Process was exploded from %s [%d] created %s. Exploded on %s",
                from.getTitle(), from.getId(), dateFormat.format(originWorkpiece.getCreationDate().getTime()),
                dateFormat.format(new Date())));
        explodeNote.setRole("CUSTODIAN");
        explodeNote.setType("SOFTWARE");
        created.getEditHistory().add(explodeNote);

        Set<PhysicalDivision> containedPhysicalDivisions = copyLogicalStructure(originWorkpiece, created, logicalRoot,
            deep);
        if (deep) {
            copyPhysicalStructure(originWorkpiece, created, containedPhysicalDivisions);
        }

        URI createdWorkpieceFilePath = fileService.getMetadataFilePath(forProcess);
        metsService.saveWorkpiece(created, createdWorkpieceFilePath);

        logger.info(
            "Successfully created workpiece [{}] for process \"{}\" [{}] with {} logical and {} physical division(s)",
            created.getId(), forProcess.getTitle(), forProcess.getId(), created.getAllLogicalDivisions().size(), created
                    .getAllPhysicalDivisions().size());
        return created;
    }

    private Set<PhysicalDivision> copyLogicalStructure(Workpiece fromWorkpiece, Workpiece toWorkpiece,
            LogicalDivision where, boolean kinder) {
        Set<PhysicalDivision> containedPhysicalDivisions = new HashSet<>();
        toWorkpiece.setLogicalStructure(copyLogicalStructureRecursive(where, kinder, containedPhysicalDivisions));
        return containedPhysicalDivisions;
    }

    private LogicalDivision copyLogicalStructureRecursive(LogicalDivision in, boolean deep,
            Set<PhysicalDivision> containedPhysicalDivisions) {
        LogicalDivision out = new LogicalDivision();
        out.setType(in.getType());
        out.setLabel(in.getLabel());
        out.setOrderlabel(in.getOrderlabel());
        out.getContentIds().addAll(in.getContentIds());
        out.setOrder(in.getOrder());
        out.getMetadata().addAll(in.getMetadata());

        if (deep) {
            for (View view : in.getViews()) {
                containedPhysicalDivisions.add(view.getPhysicalDivision());
                out.getViews().add(view);
            }
            for (LogicalDivision child : in.getChildren()) {
                out.getChildren().add(copyLogicalStructureRecursive(child, true, containedPhysicalDivisions));
            }
        }
        return out;
    }

    private void copyPhysicalStructure(Workpiece fromWorkpiece, Workpiece toWorkpiece,
            Set<PhysicalDivision> containedPhysicalDivisions) {
        PhysicalDivision maybeChild = copyPhyisicalStructureRecursive(fromWorkpiece.getPhysicalStructure(),
            containedPhysicalDivisions);
        if (maybeChild != null) {
            toWorkpiece.setPhysicalStructure(maybeChild);
        }
    }

    private PhysicalDivision copyPhyisicalStructureRecursive(PhysicalDivision in, Set<PhysicalDivision> include) {
        // copy contained divs with media
        if (!in.getMediaFiles().isEmpty()) {
            return include.contains(in) ? in : null;
        }

        // copy physical divs without media if they have children
        PhysicalDivision out = new PhysicalDivision();
        for (PhysicalDivision child : in.getChildren()) {
            PhysicalDivision maybeChild = copyPhyisicalStructureRecursive(child, include);
            if (maybeChild != null) {
                out.getChildren().add(maybeChild);
            }
        }
        if (out.getChildren().isEmpty()) {
            return null;
        }
        out.setDivId(in.getDivId());
        out.setType(in.getType());
        out.setOrder(in.getOrder());
        out.setOrderlabel(in.getOrderlabel());
        out.getMetadata().addAll(in.getMetadata());
        out.setLabel(in.getLabel());
        out.getContentIds().addAll(in.getContentIds());
        return out;
    }

    /**
     * Creates a map for index counting children's names. For children types
     * that occur more than once, the entry is initialized with "1". (For types
     * that occur more than once, the first occurrence will have a count added,
     * like "chapter-1", "chapter-2", "chapter-3".) For children types that
     * occur only once, no entry is contained. (For types that occur only once,
     * the first (and only) occurrence will not have a count added, like
     * "tableOfContents", not "tableOfContents-1".)
     * 
     * @param children
     *            the children
     * @return the map
     */
    private Map<String, MutableInt> initializeAppendixCounts(List<LogicalDivision> children) {
        Map<String, MutableInt> appendixCount = new HashMap<>();
        for (LogicalDivision child : children) {
            appendixCount.computeIfAbsent(child.getType(), (type) -> new MutableInt()).increment();
        }
        appendixCount.entrySet().removeIf(entry -> entry.getValue().getValue().equals(1));
        appendixCount.entrySet().forEach(entry -> entry.getValue().setValue(1));
        return appendixCount;
    }

    /**
     * Composes the title for the child process.
     * 
     * @param titleBase
     *            the title of the parent process
     * @param childDivision
     *            division of the root of the new child process
     * @param appendixCounts
     *            table for keeping track of child numbers
     * @return the title for the child process
     */
    private String formChildTitle(String titleBase, LogicalDivision childDivision,
            Map<String, MutableInt> appendixCounts) {

        String appendix = "";
        MutableInt counter = appendixCounts.get(childDivision.getType());
        if (Objects.nonNull(counter)) {
            appendix = "-" + counter.getValue();
            counter.increment();
        }
        String childTitle = titleBase + '_' + childDivision.getType() + appendix;
        return childTitle;
    }

    /**
     * Copies the media files linked in the child from the original process to
     * the child process.
     * 
     * @param whatToCopy
     *            what is linked in the workpiece is copied
     * @param from
     *            process from whose subdirectories the files are copied
     * @param to
     *            process into whose subdirectories the files are copied
     * @return the physical divisions whose media were copied
     * @throws DataException
     *             if the indexer has an error
     */
    Collection<PhysicalDivision> copyMedia(Workpiece whatToCopy, Process from, Process to) throws DataException {
        URI inBase = processService.getProcessDataDirectory(from);
        URI outBase = processService.getProcessDataDirectory(to);
        logger.info("Starting to copy media files for process \"{}\" [{}] from {} to {} ...", to.getTitle(), to.getId(),
            inBase, outBase);

        String dataDirectory = ConfigCore.getKitodoDataDirectory();
        URI fromBaseUri = Paths.get(dataDirectory, inBase.getPath()).toUri();
        URI toBaseUri = Paths.get(dataDirectory, outBase.getPath()).toUri();
        List<PhysicalDivision> copiedDivisions = new ArrayList<>();
        MutableInt copiedFiles = new MutableInt(0);
        copiedDivisions.addAll(copyMediaRecursive(whatToCopy.getPhysicalStructure(), fromBaseUri, toBaseUri,
            copiedFiles));

        to.setSortHelperImages(copiedDivisions.size());
        to.setNumberOfImages(copiedDivisions.size());
        processService.save(to);

        logger.info("Successfully copied {} files for {} media units to {}", copiedFiles.getValue(), copiedDivisions
                .size(), outBase);
        return copiedDivisions;
    }

    private List<PhysicalDivision> copyMediaRecursive(PhysicalDivision where, URI from,
            URI to, MutableInt counter) {

        List<PhysicalDivision> copiedDivisions = new ArrayList<>();
        if(!where.getMediaFiles().isEmpty()) {
            for(Entry<MediaVariant, URI> mediaFile:where.getMediaFiles().entrySet()) {
                
                throw new UnsupportedOperationException("not yet implemented"); // from here downwards // FIXME
                
                from.resolve(to)
                
                logger.info("Copying {} file {} to {}", mediaFile.getKey().getUse(), );
                
                counter.increment();
            }
            copiedDivisions.add(where);
        }
        // recursion
        for(PhysicalDivision child:where.getChildren()) {
            copiedDivisions.addAll(copyMediaRecursive(child, from, to, counter));
        }
        
        return null;
    }

    private Folder getFolderForMediaVariant(Process from, MediaVariant mediaVariant) {
        Folder fol = null;
        for (Folder f : from.getProject().getFolders()) {
            if (Objects.equals(f.getFileGroup(), mediaVariant.getUse())) {
                fol = f;
            }
        }
        if (fol == null) {
            throw new IllegalArgumentException("No folder for USE: " + mediaVariant.getUse());
        }
        return fol;
    }

    /**
     * Copies any media files not linked in any child from the original process
     * to the parent process.
     * 
     * @param alreadyCopiedSomewhere
     *            what has already been copied to at least one child will not be
     *            copied again
     * @param from
     *            process from whose subdirectories the files are copied
     * @param to
     *            process into whose subdirectories the files are copied
     */
    void copyRemainingMedia(Collection<PhysicalDivision> alreadyCopiedSomewhere, Process from, Process to) {
        URI inBase = processService.getProcessDataDirectory(from);
        URI outBase = processService.getProcessDataDirectory(to);
        logger.info("Starting to copy media structures not referenced in child processes from {} to {} ...", inBase,
            outBase);

        MutableInt withMediaDivs = new MutableInt(0);
        MutableInt noMediaDivs = new MutableInt(0);
        MutableInt skippedMediaDivs = new MutableInt(0);
        MutableInt allCoppiedDivs = new MutableInt(0);
        MutableInt coppiedMediaDivs = new MutableInt(0);
        MutableInt filesCopyed = new MutableInt(0);

        throw new UnsupportedOperationException("not yet implemented");

        logger.info("Found {} physical division(s) without media and {} physical division(s) with media", noMediaDivs,
            withMediaDivs);
        logger.info("Skipped {}, copied {}, thereof {} with media, {} files copied", skippedMediaDivs, allCoppiedDivs,
            coppiedMediaDivs, filesCopyed);
    }

    /* Percentual progress getter. For use in Task Manager percentage display.
     * This is currently not in use.
     * 
     * @return an integer from 0 to 100. 0 at the beginning, 100 when it is
     *         complete.
     */
    @SuppressWarnings("unused")
    public int getProgress() {
        return (100 * went.getValue()) / steps;
    }
}
