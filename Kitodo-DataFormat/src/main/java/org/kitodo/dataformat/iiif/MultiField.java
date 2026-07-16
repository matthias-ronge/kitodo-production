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

package org.kitodo.dataformat.iiif;

/**
 * Contains a record field in multiple languages. It consists of a pair of
 * {@code label} and {@code value}, where both individually, can hold different
 * languages, and a list of text lines for each language.
 *
 * <P>
 * The IIIF content is internationalizable, where labels and values may be
 * available in different languages.
 */
public class MultiField {

    private MultiText label = new MultiText();
    private MultiText value = new MultiText();

    /**
     * Creates an empty MultiField instance. This un-hides the default constructor.
     */
    public MultiField() {
    }

    /**
     * Creates a MultiField instance with label and value without language
     * information. This is a convenience constructor.
     *
     * <P>
     * Both strings are wrapped to a string list at line breaks, and
     * stored with the undefined language ("und").
     * 
     * @param label field label. Must not be {@code null}. Must not be blank.
     * @param value field value. Must not be {@code null}. Must not be blank.
     */
    public MultiField(String label, String value) {
        assert label != null : "label must not be null";
        assert !label.isBlank() : "label must not be blank";
        assert value != null : "value must not be null";
        assert !value.isBlank() : "value must not be blank";

        throw new UnsupportedOperationException("not yet implemented");
    }

    /**
     * &#x1F511; Returns the label. Is initialized with an empty {@code
     * MultiText} at class creation. Never returns {@code null}.
     *
     * <P>
     * Field has a label.
     * 
     * @return the label
     */
    public MultiText getLabel() {
        return label;
    }

    /**
     * &#x1F511; Sets the label.
     * 
     * @param label label to set. Must not be {@code null}.
     */
    public void setLabel(MultiText label) {
        assert label != null : "label must not be null";

        this.label = label;
    }

    /**
     * &#x1F511; Returns the value. Is initialized with an empty {@code
     * MultiText} at class creation. Never returns {@code null}.
     *
     * <P>
     * Field has a value.
     * 
     * @return the value
     */
    public MultiText getValue() {
        return value;
    }

    /**
     * &#x1F511; Sets the value.
     * 
     * @param value value to set. Must not be {@code null}.
     */
    public void setValue(MultiText value) {
        assert value != null : "value must not be null";

        this.value = value;
    }
}
