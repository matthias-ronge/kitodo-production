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

import java.util.Arrays;

import javax.management.InvalidAttributeValueException;

import org.kitodo.production.helper.Helper;

/**
 * An error message that can be thrown if a meta-data value is invalid.
 */
public class InvalidMetadataValueException extends InvalidAttributeValueException {
    private static final long serialVersionUID = 1L;

    /**
     * An identifier for the key whose value is incorrect.
     */
    private String key;

    /**
     * The wrong value.
     */
    private String value;

    /**
     * Creates a new invalid meta-data value exception.
     * 
     * @param key
     *            the (translated) key whose value is incorrect
     * @param value
     *            the wrong value
     */
    public InvalidMetadataValueException(String key, String value) {
        this.key = key;
        this.value = value;
    }

    /**
     * When the error occurs on a child meta-data element of a parent, while
     * bubbling up, the exception can be added the name of the parent, so that
     * the user knows what input is wrong.
     *
     * @param parentLabel
     *            name of the parent which is wrong
     */
    public void addParent(String parentLabel) {
        key = parentLabel + " » " + key;
    }

    @Override
    public String getLocalizedMessage() {
        return Helper.getTranslation("dataEditor.invalidMetadataValue", Arrays.asList(key, value));
    }

    @Override
    public String getMessage() {
        return "Cannot store \"" + key + "\": The value is invalid. Value: " + value;
    }
}
