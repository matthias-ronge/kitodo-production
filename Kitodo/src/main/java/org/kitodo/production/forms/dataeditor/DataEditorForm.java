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

package org.kitodo.production.forms.dataeditor;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale.LanguageRange;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Named;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.kitodo.api.Metadata;
import org.kitodo.api.MetadataEntry;
import org.kitodo.api.dataeditor.rulesetmanagement.RulesetManagementInterface;
import org.kitodo.api.dataformat.LogicalStructure;
import org.kitodo.api.dataformat.PhysicalStructure;
import org.kitodo.api.dataformat.View;
import org.kitodo.api.dataformat.Workpiece;
import org.kitodo.api.validation.State;
import org.kitodo.api.validation.ValidationResult;
import org.kitodo.data.database.beans.Process;
import org.kitodo.data.database.beans.Ruleset;
import org.kitodo.data.database.beans.User;
import org.kitodo.data.database.exceptions.DAOException;
import org.kitodo.exceptions.InvalidImagesException;
import org.kitodo.exceptions.InvalidMetadataValueException;
import org.kitodo.exceptions.NoSuchMetadataFieldException;
import org.kitodo.exceptions.RulesetNotFoundException;
import org.kitodo.production.forms.createprocess.ProcessDetail;
import org.kitodo.production.helper.Helper;
import org.kitodo.production.interfaces.RulesetSetupInterface;
import org.kitodo.production.metadata.MetadataLock;
import org.kitodo.production.services.ServiceManager;
import org.kitodo.production.services.dataeditor.DataEditorService;
import org.primefaces.PrimeFaces;

@Named("DataEditorForm")
@SessionScoped
public class DataEditorForm implements RulesetSetupInterface, Serializable {

    private static final Logger logger = LogManager.getLogger(DataEditorForm.class);

    /**
     * A filter on the rule set depending on the workflow step. So far this is
     * not configurable anywhere and is therefore on “edit”.
     */
    private final String acquisitionStage;

    /**
     * Backing bean for the add doc struc type dialog.
     */
    private final AddDocStrucTypeDialog addDocStrucTypeDialog;

    /**
     * Backing bean for the add PhysicalStructure dialog.
     */
    private final AddPhysicalStructureDialog addPhysicalStructureDialog;

    /**
     * Backing bean for the change doc struc type dialog.
     */
    private final ChangeDocStrucTypeDialog changeDocStrucTypeDialog;

    /**
     * Backing bean for the edit pages dialog.
     */
    private final EditPagesDialog editPagesDialog;

    /**
     * Backing bean for the gallery panel.
     */
    private final GalleryPanel galleryPanel;

    /**
     * The current process children.
     */
    private final Set<Process> currentChildren = new HashSet<>();

    /**
     * The path to the main file, to save it later.
     */
    private URI mainFileUri;

    /**
     * Backing bean for the metadata panel.
     */
    private final MetadataPanel metadataPanel;

    /**
     * Backing bean for the pagination panel.
     */
    private final PaginationPanel paginationPanel;

    /**
     * The language preference list of the editing user for displaying the
     * metadata labels. We cache this because it’s used thousands of times and
     * otherwise the access would always go through the search engine, which
     * would delay page creation.
     */
    private List<LanguageRange> priorityList;

    /**
     * Process whose workpiece is under edit.
     */
    private Process process;

    private String referringView = "desktop";

    /**
     * The ruleset that the file is based on.
     */
    private RulesetManagementInterface ruleset;

    /**
     * Backing bean for the structure panel.
     */
    private final StructurePanel structurePanel;

    /**
     * User sitting in front of the editor.
     */
    private User user;

    /**
     * The file content.
     */
    private Workpiece workpiece;

    /**
     * Original state of workpiece. Used to check whether any unsaved changes exist when leaving the editor.
     */
    private Workpiece workpieceOriginalState;

    /**
     * This List of Pairs stores all selected physical elements and the logical elements in which the physical element was selected.
     * It is necessary to store the logical elements as well, because a physical element can be assigned to multiple logical elements.
     */
    private List<Pair<PhysicalStructure, LogicalStructure>> selectedMedia;

    private static final String DESKTOP_LINK = "/pages/desktop.jsf";

    /**
     * Public constructor.
     */
    public DataEditorForm() {
        this.structurePanel = new StructurePanel(this);
        this.metadataPanel = new MetadataPanel(this);
        this.galleryPanel = new GalleryPanel(this);
        this.paginationPanel = new PaginationPanel(this);
        this.addDocStrucTypeDialog = new AddDocStrucTypeDialog(this);
        this.addPhysicalStructureDialog = new AddPhysicalStructureDialog(this);
        this.changeDocStrucTypeDialog = new ChangeDocStrucTypeDialog(this);
        this.editPagesDialog = new EditPagesDialog(this);
        acquisitionStage = "edit";
    }

    /**
     * Checks if the process is correctly set. Otherwise redirect to desktop,
     * because metadataeditor doesn't work without a process.
     */
    public void initMetadataEditor() {
        if (Objects.isNull(process)) {
            try {
                Helper.setErrorMessage("noProcessSelected");
                FacesContext context = FacesContext.getCurrentInstance();
                String path = context.getExternalContext().getRequestContextPath() + DESKTOP_LINK;
                context.getExternalContext().redirect(path);
            } catch (IOException e) {
                Helper.setErrorMessage("noProcessSelected");
            }
        }
    }

    /**
     * Open the metadata file of the process with the given ID in the metadata editor.
     *
     * @param processID
     *            ID of the process that is opened
     * @param referringView
     *            JSF page the user came from
     */
    public String open(String processID, String referringView) {
        try {
            this.referringView = referringView;
            this.process = ServiceManager.getProcessService().getById(Integer.parseInt(processID));
            this.currentChildren.addAll(process.getChildren());
            this.user = ServiceManager.getUserService().getCurrentUser();

            User blockedUser = MetadataLock.getLockUser(process.getId());
            if (Objects.nonNull(blockedUser) && !blockedUser.equals(this.user)) {
                Helper.setErrorMessage("blocked", blockedUser.getFullName());
                return referringView;
            }

            ruleset = openRuleset(process.getRuleset());
            openMetsFile();
            if (!workpiece.getId().equals(process.getId().toString())) {
                Helper.setErrorMessage("metadataConfusion", new Object[] {process.getId(), workpiece.getId() });
                return referringView;
            }
            selectedMedia = new LinkedList<>();
            init();
            MetadataLock.setLocked(process.getId(), user);
        } catch (IOException | DAOException | InvalidImagesException | NoSuchElementException | RulesetNotFoundException e) {
            Helper.setErrorMessage(e.getLocalizedMessage(), logger, e);
            return referringView;
        }
        return "/pages/metadataEditor?faces-redirect=true";
    }

    /**
     * Opens the METS file.
     *
     * @throws IOException
     *             if filesystem I/O fails
     */
    private void openMetsFile() throws IOException, InvalidImagesException {
        mainFileUri = ServiceManager.getProcessService().getMetadataFileUri(process);
        workpiece = ServiceManager.getMetsService().loadWorkpiece(mainFileUri);
        workpieceOriginalState = ServiceManager.getMetsService().loadWorkpiece(mainFileUri);
        if (Objects.isNull(workpiece.getId())) {
            logger.warn("Workpiece has no ID. Cannot verify workpiece ID. Setting workpiece ID.");
            workpiece.setId(process.getId().toString());
        }
        ServiceManager.getFileService().searchForMedia(process, workpiece);
    }

    private RulesetManagementInterface openRuleset(Ruleset ruleset) throws IOException, RulesetNotFoundException {
        final long begin = System.nanoTime();
        String metadataLanguage = user.getMetadataLanguage();
        priorityList = LanguageRange.parse(metadataLanguage.isEmpty() ? "en" : metadataLanguage);
        RulesetManagementInterface openRuleset = ServiceManager.getRulesetService().openRuleset(ruleset);
        if (logger.isTraceEnabled()) {
            logger.trace("Reading ruleset took {} ms", TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - begin));
        }
        return openRuleset;
    }

    private void init() {
        final long begin = System.nanoTime();

        List<PhysicalStructure> severalAssignments = new LinkedList<>();
        initSeveralAssignments(workpiece.getPhysicalStructureRoot(), severalAssignments);
        structurePanel.getSeveralAssignments().addAll(severalAssignments);

        structurePanel.show();
        structurePanel.getSelectedLogicalNode().setSelected(true);
        structurePanel.getSelectedPhysicalNode().setSelected(true);
        metadataPanel.showLogical(getSelectedLogicalStructure());
        metadataPanel.showPhysical(getSelectedPhysicalStructure());
        galleryPanel.show();
        paginationPanel.show();

        editPagesDialog.prepare();

        if (logger.isTraceEnabled()) {
            logger.trace("Initializing editor beans took {} ms",
                TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - begin));
        }
    }

    /**
     * Clears all remaining content from the data editor form.
     *
     * @return the referring view, to return there
     */
    public String close() {
        metadataPanel.clear();
        structurePanel.clear();
        workpiece = null;
        workpieceOriginalState = null;
        mainFileUri = null;
        ruleset = null;
        currentChildren.clear();
        selectedMedia.clear();
        MetadataLock.setFree(process.getId());
        process = null;
        user = null;
        if (referringView.contains("?")) {
            return referringView + "&faces-redirect=true";
        } else {
            return referringView + "?faces-redirect=true";
        }
    }

    /**
     * Validate the structure and metadata.
     *
     * @return whether the validation was successful or not
     */
    public boolean validate() {
        try {
            ValidationResult validationResult = ServiceManager.getMetadataValidationService().validate(workpiece,
                ruleset);
            State state = validationResult.getState();
            switch (state) {
                case ERROR:
                    Helper.setErrorMessage(Helper.getTranslation("dataEditor.validation.state.error"));
                    for (String message : validationResult.getResultMessages()) {
                        Helper.setErrorMessage(message);
                    }
                    return false;
                case WARNING:
                    Helper.setWarnMessage(Helper.getTranslation("dataEditor.validation.state.warning"));
                    for (String message : validationResult.getResultMessages()) {
                        Helper.setWarnMessage(message);
                    }
                    return true;
                default:
                    Helper.setMessage(Helper.getTranslation("dataEditor.validation.state.success"));
                    for (String message : validationResult.getResultMessages()) {
                        Helper.setMessage(message);
                    }
                    return true;
            }
        } catch (DAOException e) {
            Helper.setErrorMessage(e.getLocalizedMessage(), logger, e);
            return false;
        }
    }

    /**
     * Save the structure and metadata.
     */
    public void save() {
        metadataPanel.preserve();
        try {
            structurePanel.preserve();
            try (OutputStream out = ServiceManager.getFileService().write(mainFileUri)) {
                ServiceManager.getMetsService().save(workpiece, out);
                ServiceManager.getProcessService().saveToIndex(process,false);
                PrimeFaces.current().executeScript("PF('notifications').renderMessage({'summary':'"
                        + Helper.getTranslation("metadataSaved") + "','severity':'info'})");
            } catch (IOException e) {
                Helper.setErrorMessage(e.getLocalizedMessage(), logger, e);
            }
        } catch (Exception e) {
            Helper.setErrorMessage(e.getLocalizedMessage(), logger, e);
        }
        PrimeFaces.current().executeScript("PF('sticky-notifications').removeAll();");
        PrimeFaces.current().ajax().update("notifications");
    }

    /**
     * Save the structure and metadata.
     *
     * @return navigation target
     */
    public String saveAndExit() {
        metadataPanel.preserve();
        try {
            structurePanel.preserve();
            try (OutputStream out = ServiceManager.getFileService().write(mainFileUri)) {
                ServiceManager.getMetsService().save(workpiece, out);
                ServiceManager.getProcessService().saveToIndex(process,false);
                return close();
            } catch (IOException e) {
                Helper.setErrorMessage(e.getLocalizedMessage(), logger, e);
            }
        } catch (Exception e) {
            Helper.setErrorMessage(e.getLocalizedMessage(), logger, e);
        }
        PrimeFaces.current().executeScript("PF('sticky-notifications').removeAll();");
        PrimeFaces.current().ajax().update("notifications");
        return null;
    }

    private void initSeveralAssignments(PhysicalStructure physicalStructure, List<PhysicalStructure> severalAssignments) {
        if (physicalStructure.getLogicalStructures().size() > 1) {
            severalAssignments.add(physicalStructure);
        }
        for (PhysicalStructure child : physicalStructure.getChildren()) {
            initSeveralAssignments(child, severalAssignments);
        }
    }

    /**
     * Deletes the selected outline point from the logical outline. This method
     * is called by PrimeFaces to inform the application that the user has
     * clicked on the shortcut menu entry to clear the outline point.
     */
    public void deleteStructure() {
        structurePanel.deleteSelectedStructure();
    }

    /**
     * Deletes the selected physical structure from the media list. The
     * associated files on the drive are not deleted. The next time the editor
     * is started, files that are not yet in the media list will be inserted
     * there again. This method is called by PrimeFaces to inform the
     * application that the user clicked on the context menu entry to delete the
     * physical structure.
     */
    public void deletePhysicalStructure() {
        structurePanel.deleteSelectedPhysicalStructure();
    }

    @Override
    public String getAcquisitionStage() {
        return acquisitionStage;
    }

    /**
     * Returns the backing bean for the add doc struc type dialog. This function
     * is used by PrimeFaces to access the elements of the add doc struc type
     * dialog.
     *
     * @return the backing bean for the add doc struc type dialog
     */
    public AddDocStrucTypeDialog getAddDocStrucTypeDialog() {
        return addDocStrucTypeDialog;
    }

    /**
     * Returns the backing bean for the add media dialog. This function is used
     * by PrimeFaces to access the elements of the add media dialog.
     *
     * @return the backing bean for the add media dialog
     */
    public AddPhysicalStructureDialog getAddPhysicalStructureDialog() {
        return addPhysicalStructureDialog;
    }

    /**
     * Returns the backing bean for the change doc struc type dialog. This
     * function is used by PrimeFaces to access the elements of the change doc
     * struc type dialog.
     *
     * @return the backing bean for the change doc struc type dialog
     */
    public ChangeDocStrucTypeDialog getChangeDocStrucTypeDialog() {
        return changeDocStrucTypeDialog;
    }

    /**
     * Returns the backing bean for the edit pages dialog. This function is used
     * by PrimeFaces to access the elements of the edit pages dialog.
     *
     * @return the backing bean for the edit pages dialog
     */
    public EditPagesDialog getEditPagesDialog() {
        return editPagesDialog;
    }

    /**
     * Returns the backing bean for the gallery panel. This function is used by
     * PrimeFaces to access the elements of the gallery panel.
     *
     * @return the backing bean for the gallery panel
     */
    public GalleryPanel getGalleryPanel() {
        return galleryPanel;
    }

    Set<Process> getCurrentChildren() {
        return currentChildren;
    }

    /**
     * Returns the backing bean for the metadata panel. This function is used
     * by PrimeFaces to access the elements of the metadata panel.
     *
     * @return the backing bean for the metadata panel
     */
    public MetadataPanel getMetadataPanel() {
        return metadataPanel;
    }

    /**
     * Returns the backing bean for the pagination panel. This function is used
     * by PrimeFaces to access the elements of the pagination panel.
     *
     * @return the backing bean for the pagination panel
     */
    public PaginationPanel getPaginationPanel() {
        return paginationPanel;
    }

    @Override
    public List<LanguageRange> getPriorityList() {
        return priorityList;
    }

    /**
     * Get process.
     *
     * @return value of process
     */
    public Process getProcess() {
        return process;
    }

    /**
     * Get process title.
     *
     * @return value of process title
     */
    public String getProcessTitle() {
        return process.getTitle();
    }

    @Override
    public RulesetManagementInterface getRuleset() {
        return ruleset;
    }

    Optional<LogicalStructure> getSelectedLogicalStructure() {
        return structurePanel.getSelectedStructure();
    }

    Optional<PhysicalStructure> getSelectedPhysicalStructure() {
        return structurePanel.getSelectedPhysicalStructure();
    }

    /**
     * Check if the passed LogicalStructure is part of the selection.
     * @param structure LogicalStructure to be checked
     * @return boolean representing selection status
     */
    public boolean isStripeSelected(LogicalStructure structure) {
        Optional<LogicalStructure> selectedStructure = structurePanel.getSelectedStructure();
        return selectedStructure.filter(logicalStructure -> Objects.equals(structure, logicalStructure)).isPresent();
    }

    /**
     * Return structurePanel.
     *
     * @return structurePanel
     */
    public StructurePanel getStructurePanel() {
        return structurePanel;
    }

    Workpiece getWorkpiece() {
        return workpiece;
    }

    void refreshStructurePanel() {
        structurePanel.show(true);
        galleryPanel.updateStripes();
    }

    void setProcess(Process process) {
        this.process = process;
    }

    /**
     * Get selectedMedia.
     *
     * @return value of selectedMedia
     */
    List<Pair<PhysicalStructure, LogicalStructure>> getSelectedMedia() {
        return selectedMedia;
    }

    void setSelectedMedia(List<Pair<PhysicalStructure, LogicalStructure>> media) {
        this.selectedMedia = media;
    }

    /**
     * Check if the passed PhysicalStructure is selected.
     *
     * @param physicalStructure
     *            PhysicalStructure object to check for selection
     * @param logicalStructure
     *            object to check whether the PhysicalStructure is selected as a child
     *            of this LogicalStructure. A PhysicalStructure can be assigned to
     *            multiple LogicalStructures but can be selected in one of these
     *            LogicalStructures.
     * @return boolean whether the PhysicalStructure is selected at the specified
     *         position
     */
    public boolean isSelected(PhysicalStructure physicalStructure, LogicalStructure logicalStructure) {
        if (Objects.nonNull(physicalStructure) && Objects.nonNull(logicalStructure)) {
            return selectedMedia.contains(new ImmutablePair<>(physicalStructure, logicalStructure));
        }
        return false;
    }

    void switchStructure(Object treeNodeData, boolean updateGalleryAndPhysicalTree) throws NoSuchMetadataFieldException {
        try {
            metadataPanel.preserveLogical();
        } catch (InvalidMetadataValueException e) {
            logger.info(e.getLocalizedMessage(), e);
        }

        Optional<LogicalStructure> selectedStructure = structurePanel.getSelectedStructure();

        metadataPanel.showLogical(selectedStructure);
        if (treeNodeData instanceof StructureTreeNode) {
            StructureTreeNode structureTreeNode = (StructureTreeNode) treeNodeData;
            if (Objects.nonNull(structureTreeNode.getDataObject())) {
                if (structureTreeNode.getDataObject() instanceof LogicalStructure
                        && selectedStructure.isPresent()) {
                    // Logical structure element selected
                    if (structurePanel.isSeparateMedia()) {
                        LogicalStructure structuralElement = selectedStructure.get();
                        if (!structuralElement.getViews().isEmpty()) {
                            ArrayList<View> views = new ArrayList<>(structuralElement.getViews());
                            if (Objects.nonNull(views.get(0)) && updateGalleryAndPhysicalTree) {
                                updatePhysicalStructureTree(views.get(0));
                                updateGallery(views.get(0));
                            }
                        } else {
                            updatePhysicalStructureTree(null);
                        }
                    } else {
                        getSelectedMedia().clear();
                    }
                } else if (structureTreeNode.getDataObject() instanceof View) {
                    // Page selected in logical tree
                    View view = (View) structureTreeNode.getDataObject();
                    metadataPanel.showPageInLogical(view.getPhysicalStructure());
                    if (updateGalleryAndPhysicalTree) {
                        updateGallery(view);
                    }
                    // no need to update physical tree because pages can only be clicked in logical tree if physical tree is hidden!
                }
            }
        }
    }

    void switchPhysicalStructure() throws NoSuchMetadataFieldException {
        try {
            metadataPanel.preservePhysical();
        } catch (InvalidMetadataValueException e) {
            logger.info(e.getLocalizedMessage(), e);
        }

        Optional<PhysicalStructure> selectedPhysicalStructure = structurePanel.getSelectedPhysicalStructure();

        metadataPanel.showPhysical(selectedPhysicalStructure);
        if (selectedPhysicalStructure.isPresent()) {
            // update gallery
            galleryPanel.updateSelection(selectedPhysicalStructure.get(), null);
            // update logical tree
            for (GalleryMediaContent galleryMediaContent : galleryPanel.getMedias()) {
                if (Objects.nonNull(galleryMediaContent.getView())
                        && Objects.equals(selectedPhysicalStructure.get(),
                            galleryMediaContent.getView().getPhysicalStructure())) {
                    structurePanel.updateLogicalNodeSelection(galleryMediaContent, null);
                    break;
                }
            }
        }
    }

    private void updatePhysicalStructureTree(View view) {
        GalleryMediaContent galleryMediaContent = this.galleryPanel.getGalleryMediaContent(view);
        structurePanel.updatePhysicalNodeSelection(galleryMediaContent);
    }

    private void updateGallery(View view) {
        PhysicalStructure physicalStructure = view.getPhysicalStructure();
        if (Objects.nonNull(physicalStructure)) {
            galleryPanel.updateSelection(physicalStructure, structurePanel.getPageStructure(view, workpiece.getLogicalStructureRoot()));
        }
    }

    void assignView(LogicalStructure logicalStructure, View view, Integer index) {
        if (Objects.nonNull(index) && index >= 0 && index < logicalStructure.getViews().size()) {
            logicalStructure.getViews().add(index, view);
        } else {
            logicalStructure.getViews().add(view);
        }
        view.getPhysicalStructure().getLogicalStructures().add(logicalStructure);
    }

    void unassignView(LogicalStructure logicalStructure, View view, boolean removeLast) {
        // if View was moved within one element, we need to distinguish two possible directions it could have been moved
        if (removeLast) {
            logicalStructure.getViews().removeLastOccurrence(view);
        } else {
            logicalStructure.getViews().removeFirstOccurrence(view);
        }
        view.getPhysicalStructure().getLogicalStructures().remove(logicalStructure);
    }

    /**
     * Retrieve and return 'title' value of given Object 'dataObject' if Object is instance of
     * 'LogicalStructure' and if it does have a title. Uses a configurable list of metadata keys to determine
     * which metadata keys should be considered.
     * Return empty string otherwise.
     *
     * @param dataObject
     *          StructureTreeNode containing the LogicalStructure whose title is returned
     * @return 'title' value of the LogicalStructure contained in the given StructureTreeNode 'treeNode'
     */
    public String getStructureElementTitle(Object dataObject) {
        if (dataObject instanceof LogicalStructure) {
            LogicalStructure element = (LogicalStructure) dataObject;
            List<Metadata> titleMetadata = element.getMetadata().stream()
                    .filter(m -> DataEditorService.getTitleKeys().contains(m.getKey())).collect(Collectors.toList());
            for (Metadata metadata : titleMetadata) {
                if (metadata instanceof MetadataEntry && Objects.nonNull(((MetadataEntry) metadata).getValue())
                        && !((MetadataEntry) metadata).getValue().isEmpty()) {
                    return " - " + ((MetadataEntry) metadata).getValue();
                }
            }
        }
        return "";
    }

    /**
     * Get referringView.
     *
     * @return value of referringView
     */
    public String getReferringView() {
        return referringView;
    }

    /**
     * Check and return whether the given ProcessDetail 'processDetail' is contained in the current list of addable
     * metadata types in the addDocStrucTypeDialog.
     *
     * @param processDetail ProcessDetail to be added
     * @return whether the given ProcessDetail can be added or not
     */
    public boolean canBeAdded(ProcessDetail processDetail) {
        if (Objects.nonNull(this.getAddDocStrucTypeDialog().getSelectAddableMetadataTypesItems())) {
            return this.getAddDocStrucTypeDialog().getSelectAddableMetadataTypesItems().stream()
                    .map(SelectItem::getValue).collect(Collectors.toList()).contains(processDetail.getMetadataID());
        }
        else {
            return true;
        }
    }

    /**
     * Check for changes in workpiece.
     */
    public void checkForChanges() {
        if (Objects.nonNull(PrimeFaces.current())) {
            boolean unsavedChanges = !this.workpiece.equals(workpieceOriginalState);
            PrimeFaces.current().executeScript("setConfirmUnload(" + unsavedChanges + ");");
        }
    }

    /**
     * Get the shortcuts for the current user.
     *
     * @return shortcuts as java.lang.String
     */
    public String getShortcuts() {
        try {
            return ServiceManager.getUserService().getShortcuts(user.getId());
        } catch (DAOException e) {
            Helper.setErrorMessage(e.getLocalizedMessage(), logger, e);
            return "{}";
        }
    }
}
