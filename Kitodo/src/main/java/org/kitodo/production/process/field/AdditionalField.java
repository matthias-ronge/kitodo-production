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

package org.kitodo.production.process.field;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.faces.model.SelectItem;

import org.apache.commons.lang3.StringUtils;

public class AdditionalField {

    private String title;
    private String value = "";
    private boolean required = false;
    private String from = "process";
    private List<SelectItem> selectList;
    private boolean ughBinding = false;
    private String docStruct;
    private String metadata;
    private String isDocType = "";
    private String isNotDoctype = "";
    private String initStart = ""; // defined in kitodo_projects.xml
    private String initEnd = "";
    private boolean autogenerated = false;
    private final String docType;

    /**
     * Public constructor with setting document type for additional field.
     *
     * @param docType
     *            document type for additional field
     */
    public AdditionalField(String docType) {
        this.docType = docType;
    }

    /**
     * Get title.
     *
     * @return title
     */
    public String getTitle() {
        return this.title;
    }

    /**
     * Set title.
     *
     * @param title
     *            of additional field
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get value.
     *
     * @return value
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Set value.
     *
     * @param value
     *            String
     */
    public void setValue(String value) {
        if (Objects.isNull(value) || value.equals(this.initStart)) {
            value = "";
        }
        if (value.startsWith(this.initStart)) {
            this.value = value + this.initEnd;
        } else {
            this.value = this.initStart + value + this.initEnd;
        }
    }

    /**
     * Get from.
     *
     * @return from
     */
    public String getFrom() {
        return this.from;
    }

    /**
     * Set from.
     *
     * @param from
     *            input from as String
     */
    public void setFrom(String from) {
        if (!StringUtils.isEmpty(from)) {
            this.from = from;
        }
    }

    /**
     * Get list of select items.
     *
     * @return list of select items.
     */
    public List<SelectItem> getSelectList() {
        if (Objects.isNull(this.selectList)) {
            this.selectList = new ArrayList<>();
        }
        return this.selectList;
    }

    /**
     * Set list of select items.
     *
     * @param selectList
     *            as List of SelectItem objects
     */
    public void setSelectList(List<SelectItem> selectList) {
        this.selectList = selectList;
    }

    /**
     * Get information if additional field is required.
     *
     * @return true or false
     */
    public boolean isRequired() {
        return this.required;
    }

    /**
     * Set information if additional field is required.
     *
     * @param required
     *            true or false
     */
    public void setRequired(boolean required) {
        this.required = required;
    }

    /**
     * Get information if additional field is UGH binding.
     *
     * @return true or false
     */
    public boolean isUghBinding() {
        return this.ughBinding;
    }

    /**
     * Set information if additional field is UGH binding.
     *
     * @param ughBinding
     *            true or false
     */
    public void setUghBinding(boolean ughBinding) {
        this.ughBinding = ughBinding;
    }

    /**
     * Get document structure.
     *
     * @return document structure
     */
    public String getDocStruct() {
        return this.docStruct;
    }

    /**
     * Set document structure.
     *
     * @param docStruct
     *            String
     */
    public void setDocStruct(String docStruct) {
        this.docStruct = docStruct;
        if (Objects.isNull(this.docStruct)) {
            this.docStruct = "topstruct";
        }
    }

    /**
     * Get metadata.
     *
     * @return metadata name
     */
    public String getMetadata() {
        return this.metadata;
    }

    /**
     * Set metadata.
     *
     * @param metadata
     *            as String name
     */
    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    /**
     * Get is document type.
     *
     * @return types for document
     */
    public String getIsDocType() {
        return this.isDocType;
    }

    /**
     * Set is document type.
     *
     * @param isDocType
     *            String
     */
    public void setIsDocType(String isDocType) {
        this.isDocType = isDocType;
        if (Objects.isNull(this.isDocType)) {
            this.isDocType = "";
        }
    }

    /**
     * Get is not document type.
     *
     * @return type of documents which this field is not
     */
    public String getIsNotDoctype() {
        return this.isNotDoctype;
    }

    /**
     * Set is not document type.
     *
     * @param isNotDoctype
     *            String with list of not document types
     */
    public void setIsNotDoctype(String isNotDoctype) {
        this.isNotDoctype = isNotDoctype;
        if (Objects.isNull(this.isNotDoctype)) {
            this.isNotDoctype = "";
        }
    }

    /**
     * Get init start.
     *
     * @return init start
     */
    public String getInitStart() {
        return this.initStart;
    }

    /**
     * Set init start.
     *
     * @param newValue
     *            String
     */
    public void setInitStart(String newValue) {
        this.initStart = newValue;
        if (Objects.isNull(this.initStart)) {
            this.initStart = "";
        }
        this.value = this.initStart + this.value;
    }

    /**
     * Get init end.
     *
     * @return String
     */
    public String getInitEnd() {
        return this.initEnd;
    }

    /**
     * Set init end.
     *
     * @param newValue
     *            String
     */
    public void setInitEnd(String newValue) {
        this.initEnd = newValue;
        if (Objects.isNull(this.initEnd)) {
            this.initEnd = "";
        }
        this.value = this.value + this.initEnd;
    }

    /**
     * Set auto generated.
     *
     * @param autogenerated
     *            the autogenerated to set
     */
    public void setAutogenerated(boolean autogenerated) {
        this.autogenerated = autogenerated;
    }

    /**
     * Get auto generated.
     *
     * @return the autogenerated
     */
    public boolean isAutogenerated() {
        return this.autogenerated;
    }

    /**
     * Get show depending on document type.
     *
     * @return true or false
     */
    public boolean showDependingOnDoctype() {
        // if nothing was specified, then show
        if (this.isDocType.isEmpty() && this.isNotDoctype.isEmpty()) {
            return true;
        }

        // if obligatory was specified
        if (!this.isDocType.isEmpty() && !StringUtils.containsIgnoreCase(this.isDocType, this.docType)) {
            return false;
        }

        // if only "may not" was specified
        return !(!this.isNotDoctype.isEmpty() && StringUtils.containsIgnoreCase(this.isNotDoctype, this.docType));
    }
}
