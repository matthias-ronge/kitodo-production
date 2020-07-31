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

package org.kitodo.api.dataformat;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The administrative structure of the product of an element that passes through
 * a Production workflow.
 */
public class Workpiece {

    // TODO: we probably need a way to configure MediaUnit types to be considered for renumbering/pagination!
    private static final String PAGE = "page";

    /**
     * The time this file was first created.
     */
    private GregorianCalendar creationDate = new GregorianCalendar();

    /**
     * The processing history.
     */
    private List<ProcessingNote> editHistory = new ArrayList<>();

    /**
     * The identifier of the workpiece.
     */
    private String id;

    /**
     * The root element of the logical structure.
     */
    private LogicalStructure logicalStructureRoot = new LogicalStructure();

    /**
     * The root element of the physical structure.
     */
    private MediaUnit physicalStructureRoot = new MediaUnit();

    /**
     * Returns the creation date of the workpiece.
     *
     * @return the creation date
     */
    public GregorianCalendar getCreationDate() {
        return creationDate;
    }

    /**
     * Sets the creation date of the workpiece.
     *
     * @param creationDate
     *            creation date to set
     */
    public void setCreationDate(GregorianCalendar creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Returns the edit history.
     *
     * @return the edit history
     */
    public List<ProcessingNote> getEditHistory() {
        return editHistory;
    }

    /**
     * Returns the ID of the workpiece.
     *
     * @return the ID
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the ID of the workpiece.
     *
     * @param id
     *            ID to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Returns the root element of the logical structure of the workpiece.
     *
     * @return root element of the logical structure
     */
    public LogicalStructure getLogicalStructureRoot() {
        return logicalStructureRoot;
    }

    /**
     * Sets the root element of the logical structure of the workpiece.
     *
     * @param logicalStructureRoot
     *            logical structure to set
     */
    public void setLogicalStructureRoot(LogicalStructure logicalStructureRoot) {
        this.logicalStructureRoot = logicalStructureRoot;
    }

    /**
     * Returns the root element of the physical structure of the workpiece.
     *
     * @return root element of the physical structure
     */
    public MediaUnit getPhysicalStructureRoot() {
        return physicalStructureRoot;
    }

    /**
     * Returns the media units of this workpiece.
     *
     * @return the media units
     * @deprecated Use {@code getMediaUnit().getChildren()}.
     */
    @Deprecated
    public List<MediaUnit> getMediaUnits() {
        return physicalStructureRoot.getChildren();
    }

    /**
     * Sets the root element of the physical structure of the workpiece.
     *
     * @param physicalStructureRoot
     *            logical structure to set
     */
    public void setPhysicalStructureRoot(MediaUnit physicalStructureRoot) {
        this.physicalStructureRoot = physicalStructureRoot;
    }

    @Override
    public String toString() {
        return id + ", " + logicalStructureRoot;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int hashCode = 1;
        hashCode = prime * hashCode + ((id == null) ? 0 : id.hashCode());
        return hashCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Workpiece workpiece = (Workpiece) o;
        return Objects.equals(creationDate, workpiece.creationDate)
                && Objects.equals(editHistory, workpiece.editHistory)
                && Objects.equals(id, workpiece.id)
                && Objects.equals(physicalStructureRoot, workpiece.physicalStructureRoot)
                && Objects.equals(logicalStructureRoot, workpiece.logicalStructureRoot);
    }

    /**
     * Recursively search for all logical elements.
     *
     * @return list of all logical elements
     */
    public List<LogicalStructure> getAllLogicalStructures() {
        List<LogicalStructure> logicalStructures = new LinkedList<>();
        logicalStructures.add(logicalStructureRoot);
        logicalStructures.addAll(getAllLocicalStructuresRecursive(logicalStructureRoot));
        return logicalStructures;
    }

    private List<LogicalStructure> getAllLocicalStructuresRecursive(LogicalStructure parent) {
        List<LogicalStructure> logicalStructures = new LinkedList<>(parent.getChildren());
        for (LogicalStructure child : parent.getChildren()) {
            if (Objects.nonNull(child)) {
                logicalStructures.addAll(getAllLocicalStructuresRecursive(child));
            }
        }
        return logicalStructures;
    }

    /**
     * Recursively search for all media units with type "page".
     *
     * @return list of all media units with type "page", sorted by their "ORDER" attribute.
     */
    public List<MediaUnit> getAllMediaUnitsSorted() {
        List<MediaUnit> mediaUnits = getAllMediaUnits();
        mediaUnits.sort(Comparator.comparing(MediaUnit::getOrder));
        return mediaUnits.stream().filter(m -> m.getType().equals(PAGE)).collect(Collectors.toList());
    }

    /**
     * Recursively search for all media units with type "page".
     *
     * @return list of all media units with type "page".
     */
    public List<MediaUnit> getAllMediaUnits() {
        List<MediaUnit> mediaUnits = new LinkedList<>(physicalStructureRoot.getChildren());
        for (MediaUnit mediaUnit : physicalStructureRoot.getChildren()) {
            if (Objects.nonNull(mediaUnit)) {
                mediaUnits = getAllMediaUnitsRecursive(mediaUnit, mediaUnits);
            }
        }
        return mediaUnits;
    }

    private List<MediaUnit> getAllMediaUnitsRecursive(MediaUnit parent, List<MediaUnit> mediaUnits) {
        List<MediaUnit> allMediaUnits = mediaUnits;
        for (MediaUnit mediaUnit : parent.getChildren()) {
            if (Objects.nonNull(mediaUnit)) {
                allMediaUnits.add(mediaUnit);
                if (!mediaUnit.getChildren().isEmpty()) {
                    allMediaUnits = getAllMediaUnitsRecursive(mediaUnit, mediaUnits);
                }
            }
        }
        return allMediaUnits;
    }
}
