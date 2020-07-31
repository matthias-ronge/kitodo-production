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

import java.util.LinkedList;
import java.util.Objects;

import org.kitodo.api.dataformat.mets.LinkedMetsResource;

/**
 * The tree-like outline included structural element for digital representation.
 * This structuring included structural element can be subdivided into arbitrary
 * finely granular {@link #subincludedStructuralElements}. It can be described
 * by {@link #metadata}.
 */
public class IncludedStructuralElement extends Division<IncludedStructuralElement> {
    /**
     * The label for this included structural element. The label is displayed in
     * the graphical representation of the included structural element tree for
     * this level.
     */
    private String label;

    /**
     * Specifies the link if there is one.
     */
    private LinkedMetsResource link;

    /**
     * The views on {@link MediaUnit}s that this included structural element
     * level comprises.
     */
    private final LinkedList<View> views;

    /**
     * Creates a new included structural element.
     */
    public IncludedStructuralElement() {
        views = new LinkedList<>();
    }

    /**
     * Creates a new subclass of included structural element from an existing
     * included structural element.
     *
     * @param source
     *            included structural element that serves as data source
     */
    protected IncludedStructuralElement(IncludedStructuralElement source) {
        super(source);
        label = source.label;
        link = source.link;
        views = source.views;
    }

    /**
     * Returns the label of this included structural element.
     *
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Sets the label of this included structural element.
     *
     * @param label
     *            label to set
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * Returns the link of this included structural element.
     *
     * @return the link
     */
    public LinkedMetsResource getLink() {
        return link;
    }

    /**
     * Sets the link of this included structural element.
     *
     * @param link
     *            link to set
     */
    public void setLink(LinkedMetsResource link) {
        this.link = link;
    }

    /**
     * Returns the views associated with this included structural element.
     *
     * @return the views
     */
    public LinkedList<View> getViews() {
        return views;
    }

    @Override
    public String toString() {
        return getType() + " \"" + label + "\"";
    }

    @Override
    public boolean equals(Object compared) {
        if (this == compared) {
            return true;
        }
        if (!super.equals(compared)) {
            return false;
        }
        if (!(compared instanceof IncludedStructuralElement)) {
            return false;
        }
        IncludedStructuralElement other = (IncludedStructuralElement) compared;
        return Objects.equals(label, other.label) && Objects.equals(link, other.link)
                && Objects.equals(views, other.views);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((label == null) ? 0 : label.hashCode());
        result = prime * result + ((link == null) ? 0 : link.hashCode());
        result = prime * result + ((views == null) ? 0 : views.hashCode());
        return result;
    }
}
