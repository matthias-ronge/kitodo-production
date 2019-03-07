package org.kitodo.production.forms.dataeditor;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.kitodo.api.MetadataEntry;
import org.kitodo.api.dataformat.Structure;
import org.kitodo.api.dataformat.View;
import org.kitodo.api.dataformat.Workpiece;
import org.kitodo.production.helper.metadata.pagination.Paginator;

public class MetadataEditor {

    /**
     * Creates a given number of new structures and inserts them into the
     * workpiece. The insertion position is given relative to an existing
     * structure. In addition, you can specify meta-data, which is assigned to
     * the structures consecutively with a counter.
     *
     * @param number
     *            number of structures to create
     * @param type
     *            type of new structure
     * @param workpiece
     *            workpiece to which the new structure is to be added
     * @param structure
     *            structure relative to which the new structure is to be
     *            inserted
     * @param position
     *            relative insertion position
     * @param metadataKey
     *            key of the meta-data to create
     * @param metadataValue
     *            value of the first meta-data entry
     */
    public static void addMultipleStructures(int number, String type, Workpiece workpiece, Structure structure,
            InsertionPosition position, String metadataKey, String metadataValue) {

        Paginator metadataValues = new Paginator(metadataValue);
        for (int i = 1; i < number; i++) {
            Structure newStructure = addStructure(type, workpiece, structure, position, Collections.emptyList());
            MetadataEntry metadataEntry = new MetadataEntry();
            metadataEntry.setKey(metadataKey);
            metadataEntry.setValue(metadataValues.next());
            newStructure.getMetadata().add(metadataEntry);
        }
    }

    /**
     * Creates a new structure and inserts it into a workpiece. The insertion
     * position is determined by the specified structure and mode. The given
     * views are assigned to the structure and all its parent structures.
     *
     * @param type
     *            type of new structure
     * @param workpiece
     *            workpiece to which the new structure is to be added
     * @param structure
     *            structure relative to which the new structure is to be
     *            inserted
     * @param position
     *            relative insertion position
     * @param viewsToAdd
     *            views to be assigned to the structure
     * @return the newly created structure
     */
    public static Structure addStructure(String type, Workpiece workpiece, Structure structure,
            InsertionPosition position, List<View> viewsToAdd) {

        LinkedList<Structure> parents = getParentsOfStructure(structure, workpiece.getStructure(), null);
        Structure newStructure = new Structure();
        newStructure.setType(type);
        List<Structure> siblings = parents.getLast().getChildren();
        LinkedList<Structure> structuresToAddViews = new LinkedList<>(parents);
        switch (position) {
            case AFTER_CURRENT_ELEMENT: {
                int index = siblings.indexOf(structure) + 1;
                siblings.add(index, newStructure);
                break;
            }
            case BEFOR_CURRENT_ELEMENT: {
                int index = siblings.indexOf(structure);
                siblings.add(index, newStructure);
                break;
            }
            case FIRST_CHILD_OF_CURRENT_ELEMENT: {
                structuresToAddViews.add(structure);
                structure.getChildren().add(0, newStructure);
                break;
            }
            case LAST_CHILD_OF_CURRENT_ELEMENT: {
                structuresToAddViews.add(structure);
                structure.getChildren().add(newStructure);
                break;
            }
            case PARENT_OF_CURRENT_ELEMENT: {
                structuresToAddViews.removeLast();
                newStructure.getChildren().add(structure);
                if (parents.isEmpty()) {
                    workpiece.setStructure(newStructure);
                } else {
                    int index = siblings.indexOf(structure);
                    siblings.set(index, newStructure);
                }
                break;
            }
            default:
                throw new IllegalStateException("complete switch");
        }
        for (Structure structuree : structuresToAddViews) {
            structuree.getViews().addAll(viewsToAdd);
        }
        return newStructure;
    }

    /**
     * Determines the parent node to a tree node.
     *
     * @param searched
     *            node whose parent node is to be found
     * @param position
     *            node to be searched recursively
     * @param parent
     *            parent node of the node to be searched
     * @return the parent node, if one is found, {@code null} otherwise
     */
    public static LinkedList<Structure> getParentsOfStructure(Structure searched, Structure position,
            Structure parent) {
        if (position.equals(searched)) {
            LinkedList<Structure> result = new LinkedList<>();
            result.add(parent);
            return result;
        }
        for (Structure child : position.getChildren()) {
            LinkedList<Structure> maybeFound = getParentsOfStructure(searched, child, position);
            if (!maybeFound.isEmpty()) {
                maybeFound.addFirst(parent);
                return maybeFound;
            }
        }
        return (LinkedList<Structure>) Collections.<Structure>emptyList();
    }

    public static void moveView(View view, Structure from, Structure to) {
        from.getViews().remove(view);
        to.getViews().add(view);
    }
}
