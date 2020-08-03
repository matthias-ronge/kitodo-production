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

import java.io.Serializable;
import java.util.Objects;

import org.kitodo.api.dataformat.LogicalStructure;
import org.kitodo.api.dataformat.PhysicalStructure;
import org.kitodo.api.dataformat.View;

public class StructureTreeNode implements Serializable {

    private final Object dataObject;
    private final String label;
    private final boolean linked;
    private final boolean undefined;

    StructureTreeNode(String label, boolean undefined, boolean linked,
            Object dataObject) {
        this.label = label;
        this.undefined = undefined;
        this.linked = linked;
        this.dataObject = dataObject;
    }

    /**
     * Returns the data object this structure tree node is related to.
     *
     * @return the data object
     */
    public Object getDataObject() {
        return dataObject;
    }

    /**
     * Returns the label of this structure tree node.
     *
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Returns whether this structure tree node represents a link.
     *
     * @return whether this structure tree node represents a link
     */
    public boolean isLinked() {
        return linked;
    }

    /**
     * Returns whether this structure tree node has a type that is undefined in
     * the rule set.
     *
     * @return whether this structure tree node has an undefined type
     */
    public boolean isUndefined() {
        return undefined;
    }

    /**
     * Return label of dataObject if dataObject is instance of PhysicalStructure or LogicalStructure.
     *
     * @return label
     */
    public String getOrderLabel() {
        if (this.dataObject instanceof PhysicalStructure) {
            return ((PhysicalStructure) this.dataObject).getOrderlabel();
        } else if (this.dataObject instanceof LogicalStructure) {
            return ((LogicalStructure) this.dataObject).getOrderlabel();
        } else {
            return "";
        }
    }

    /**
     * Check if the StructureTreeNode's PhysicalStructure is assigned to several
     * LogicalStructures.
     *
     * @return {@code true} when the PhysicalStructure is assigned to more than one
     *         logical element
     */
    public boolean isAssignedSeveralTimes() {
        if (Objects.nonNull(this.dataObject)) {
            if (this.dataObject instanceof View) {
                View view = (View) this.dataObject;
                return Objects.nonNull(view.getPhysicalStructure()) && view.getPhysicalStructure().getLogicalStructures().size() > 1;
            } else if (this.dataObject instanceof PhysicalStructure) {
                return ((PhysicalStructure) this.dataObject).getLogicalStructures().size() > 1;
            }
        }
        return false;
    }
}
