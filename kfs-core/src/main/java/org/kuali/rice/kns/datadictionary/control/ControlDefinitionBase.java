/**
 * Copyright 2005-2014 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.rice.kns.datadictionary.control;

import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.kuali.rice.core.api.util.ClassLoaderUtils;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.datadictionary.DataDictionaryDefinitionBase;
import org.kuali.rice.krad.datadictionary.control.ControlDefinition;
import org.kuali.rice.krad.datadictionary.exception.ClassValidationException;
import org.kuali.rice.krad.datadictionary.exception.CompletionException;
import org.kuali.rice.krad.keyvalues.KeyValuesFinder;

/**
 * A single HTML control definition in the DataDictionary, which contains information relating to the HTML control used to realize a
 * specific attribute. All types of controls are represented by an instance of this class; you have to call one of the is* methods
 * to figure out which of the other accessors should return useful values.
 *
 *
 */
@Deprecated
public class ControlDefinitionBase extends DataDictionaryDefinitionBase implements ControlDefinition {
    private static final long serialVersionUID = 4372435175782501152L;

    protected boolean datePicker;
    protected boolean expandedTextArea;
    protected String script;
    protected String valuesFinderClass;
    protected String businessObjectClass;
    protected String keyAttribute;
    protected String labelAttribute;
    protected Boolean includeBlankRow;
    protected Boolean includeKeyInLabel;
    protected Integer size;
    protected Integer rows;
    protected Integer cols;
    protected boolean ranged;
    protected ControlDefinitionType type;
    protected String imageSrc;
    protected String styleClass;
    protected Integer formattedMaxLength;
    protected String personNameAttributeName;
    protected String userIdAttributeName;
    protected String universalIdAttributeName;
    protected String target;
    protected String hrefText;


    public ControlDefinitionBase() {
        ranged = true;
    }

    public boolean isDatePicker() {
        return datePicker;
    }

    /** Whether this control should have a date picker button next to the field.
     *  Valid for text fields.
     *
     * @see org.kuali.rice.krad.datadictionary.control.ControlDefinition#setDatePicker(boolean)
     */
    public void setDatePicker(boolean datePicker) {
        this.datePicker=datePicker;
    }

    public boolean isExpandedTextArea() {
        return expandedTextArea;
    }

    /** Whether this control should have a expanded text area button next to the field.
     *  Valid for textarea fields.
     *
     * @see org.kuali.rice.krad.datadictionary.control.ControlDefinition#setExpandedTextArea(boolean)
     */
    public void setExpandedTextArea(boolean eTextArea) {
        this.expandedTextArea=eTextArea;
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.control.ControlDefinition#isCheckbox()
     */
    public boolean isCheckbox() {
        return ControlDefinitionType.CHECKBOX.equals(type);
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.control.ControlDefinition#isHidden()
     */
    public boolean isHidden() {
        return ControlDefinitionType.HIDDEN.equals(type);
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.control.ControlDefinition#isRadio()
     */
    public boolean isRadio() {
        return ControlDefinitionType.RADIO.equals(type);
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.control.ControlDefinition#isSelect()
     */
    public boolean isSelect() {
        return ControlDefinitionType.SELECT.equals(type);
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.control.ControlDefinition#isSelect()
     */
    public boolean isMultiselect() {
        return ControlDefinitionType.MULTISELECT.equals(type);
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.control.ControlDefinition#isText()
     */
    public boolean isText() {
        return ControlDefinitionType.TEXT.equals(type);
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.control.ControlDefinition#isTextarea()
     */
    public boolean isTextarea() {
        return ControlDefinitionType.TEXTAREA.equals(type);
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.control.ControlDefinition#isCurrency()
     */
    public boolean isCurrency() {
        return ControlDefinitionType.CURRENCY.equals(type);
    }

    /**
     *
     * @see org.kuali.rice.krad.datadictionary.control.ControlDefinition#isKualiUser()
     */
    public boolean isKualiUser() {
        return ControlDefinitionType.KUALI_USER.equals(type);
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.control.ControlDefinition#isWorkflowWorkgroup()
     */
    public boolean isWorkflowWorkgroup() {
        return ControlDefinitionType.WORKFLOW_WORKGROUP.equals(type);
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.control.ControlDefinition#isFile()
     */
    public boolean isFile() {
        return ControlDefinitionType.FILE.equals(type);
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.control.ControlDefinition#isLookupHidden()
     */
    public boolean isLookupHidden() {
        return ControlDefinitionType.LOOKUP_HIDDEN.equals(type);
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.control.ControlDefinition#isLookupReadonly()
     */
    public boolean isLookupReadonly() {
        return ControlDefinitionType.LOOKUP_READONLY.equals(type);
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.control.ControlDefinition#isButton()
     */
    public boolean isButton() {
        return ControlDefinitionType.BUTTON.equals(type);
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.control.ControlDefinition#isLink()
     */
    public boolean isLink() {
        return ControlDefinitionType.LINK.equals(type);
    }


    /**
     * @see org.kuali.rice.krad.datadictionary.control.ControlDefinition#setKeyValuesFinder(java.lang.String)
     */
    public void setValuesFinderClass(String valuesFinderClass) {
        if (valuesFinderClass == null) {
            throw new IllegalArgumentException("invalid (null) valuesFinderClass");
        }

        this.valuesFinderClass = valuesFinderClass;
    }

    /**
     * @return the dataObjectClass
     */
    public String getBusinessObjectClass() {
        return this.businessObjectClass;
    }

    /**
     * Used by a PersistableBusinessObjectValuesFinder to automatically query and display a list
     * of business objects as part of a select list or set of radio buttons.
     *
     * The keyAttribute, labelAttribute, and includeKeyInLabel are used with this property.
     *
     * @param businessObjectClass the dataObjectClass to set
     */
    public void setBusinessObjectClass(String businessObjectClass) {
        if (businessObjectClass == null) {
            throw new IllegalArgumentException("invalid (null) dataObjectClass");
        }

        this.businessObjectClass = businessObjectClass;
    }

    /**
     * @return the includeBlankRow
     */
    public Boolean getIncludeBlankRow() {
        return this.includeBlankRow;
    }

    /**
     * @return the includeBlankRow
     */
    public void setIncludeBlankRow(Boolean includeBlankRow) {
        this.includeBlankRow = includeBlankRow;
    }

    /**
     * @return the includeKeyInLabel
     */
    public Boolean getIncludeKeyInLabel() {
        return this.includeKeyInLabel;
    }

    /**
     * Whether to include the key in the label for select lists and radio buttons.
     */
    public void setIncludeKeyInLabel(Boolean includeKeyInLabel) {
        this.includeKeyInLabel = includeKeyInLabel;
    }

    /**
     * @return the keyAttribute
     */
    public String getKeyAttribute() {
        return this.keyAttribute;
    }

    /**
     * Attribute of the given dataObjectClass to use as the value of a select list
     * or set of radio buttons. 
     */
    public void setKeyAttribute(String keyAttribute) {
        this.keyAttribute = keyAttribute;
    }

    /**
     * @return the labelAttribute
     */
    public String getLabelAttribute() {
        return this.labelAttribute;
    }

    /**
     * Attribute of the given dataObjectClass to use as the displayed label on a select list
     * or set of radio buttons. 
     */
    public void setLabelAttribute(String labelAttribute) {
        this.labelAttribute = labelAttribute;
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.control.ControlDefinition#getKeyValuesFinder()
     */
    public String getValuesFinderClass() {
        return valuesFinderClass;
    }

    /**
     * Size of a text control.
     *
     * @see org.kuali.rice.krad.datadictionary.control.ControlDefinition#setSize(int)
     */
    public void setSize(Integer size) {
        this.size = size;
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.control.ControlDefinition#getSize()
     */
    public Integer getSize() {
        return size;
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.control.ControlDefinition#hasScript()
     */
    public boolean hasScript() {
        return false;
    }

    /**
     * Number of rows to display on a text-area widget.
     *
     * @see org.kuali.rice.krad.datadictionary.control.ControlDefinition#setRows(int)
     */
    public void setRows(Integer rows) {
        this.rows = rows;
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.control.ControlDefinition#getRows()
     */
    public Integer getRows() {
        return rows;
    }

    /**
     * Number of columns to display on a text-area widget.
     *
     * @see org.kuali.rice.krad.datadictionary.control.ControlDefinition#setCols(int)
     */
    public void setCols(Integer cols) {
        this.cols = cols;
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.control.ControlDefinition#getCols()
     */
    public Integer getCols() {
        return cols;
    }

    /**
     * Directly validate simple fields.
     *
     * @see org.kuali.rice.krad.datadictionary.DataDictionaryDefinition#completeValidation(java.lang.Class, java.lang.Object)
     */
    public void completeValidation(Class rootBusinessObjectClass, Class otherBusinessObjectClass) {
        if (!isCheckbox() && !isHidden() && !isRadio() && !isSelect() && !isMultiselect() && !isText() && !isTextarea() && !isCurrency() && !isKualiUser() && !isLookupHidden() && !isLookupReadonly() && !isWorkflowWorkgroup() && !isFile()&& !isButton() && !isLink()) {
            throw new CompletionException("error validating " + rootBusinessObjectClass.getName() + " control: unknown control type in control definition (" + "" + ")");
        }
        if (valuesFinderClass != null) {
            try {
                Class valuesFinderClassObject = ClassUtils.getClass(ClassLoaderUtils.getDefaultClassLoader(), getValuesFinderClass());
                if (!KeyValuesFinder.class.isAssignableFrom(valuesFinderClassObject)) {
                    throw new ClassValidationException("valuesFinderClass is not a valid instance of " + KeyValuesFinder.class.getName() + " instead was: " + valuesFinderClassObject.getName());
                }
            } catch (ClassNotFoundException e) {
                throw new ClassValidationException("valuesFinderClass could not be found: " + getValuesFinderClass(), e);
            }
        }
        if (businessObjectClass != null) {
            try {
                Class businessObjectClassObject = ClassUtils.getClass(ClassLoaderUtils.getDefaultClassLoader(), getBusinessObjectClass());
                if (!BusinessObject.class.isAssignableFrom(businessObjectClassObject)) {
                    throw new ClassValidationException("dataObjectClass is not a valid instance of " + BusinessObject.class.getName() + " instead was: " + businessObjectClassObject.getName());
                }
            } catch (ClassNotFoundException e) {
                throw new ClassValidationException("dataObjectClass could not be found: " + getBusinessObjectClass(), e);
            }
        }
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.control.ControlDefinition#getScript()
     */
    public String getScript() {
        return script;
    }

    /**
     * JavaScript script to run when a select control's value is changed.
     *
     * @see org.kuali.rice.krad.datadictionary.control.ControlDefinition#setScript()
     */
    public void setScript(String script) {
        this.script = script;
    }

    /**
     * @see org.kuali.rice.krad.datadictionary.control.ControlDefinition#isRanged()
     */
    public boolean isRanged() {
        return this.ranged;
    }

    /**
     * Sets the control as a ranged (from and to) date field if true, or a single date field if false
     *
     * @param ranged boolean true for a ranged control, false for a single date field
     */
    public void setRanged(boolean ranged) {
        this.ranged = ranged;
    }

    /**
     * @see java.lang.Object#equals(Object)
     */
    public boolean equals(Object object) {
        if ( !(object instanceof ControlDefinitionBase) ) {
            return false;
        }
        ControlDefinitionBase rhs = (ControlDefinitionBase)object;
        return new EqualsBuilder()
                .append( this.cols, rhs.cols )
                .append( this.businessObjectClass, rhs.businessObjectClass )
                .append( this.valuesFinderClass, rhs.valuesFinderClass )
                .append( this.rows, rhs.rows )
                .append( this.script, rhs.script )
                .append( this.size, rhs.size )
                .append( this.datePicker, rhs.datePicker )
                .append( this.ranged, rhs.ranged )
                .append( this.labelAttribute,rhs.labelAttribute )
                .append( this.includeKeyInLabel, rhs.includeKeyInLabel )
                .append( this.keyAttribute, rhs.keyAttribute )
                .isEquals();
    }

    /**
     * @return the mageSrc
     */
    public String getImageSrc() {
        return this.imageSrc;
    }

    /**
     * @param imageSrc the mageSrc to set
     */
    public void setImageSrc(String imageSrc) {
        this.imageSrc = imageSrc;
    }

    /**
     * @return the styleClass
     */
    public String getStyleClass() {
        return this.styleClass;
    }

    /**
     * @param styleClass the styleClass to set
     */
    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    /**
     * @return Returns the formattedMaxLength parameter for currency controls.
     */
    public Integer getFormattedMaxLength() {
        return formattedMaxLength;
    }

    /**
     * the maxLength for text that has been formatted. ie if maxLength=5. [12345]. but after going through the formatter the value
     * is [12,345.00] and will no longer fit in a field whos maxLength=5. formattedMaxLength solves this problem.
     */
    public void setFormattedMaxLength(Integer formattedMaxLength) {
        this.formattedMaxLength = formattedMaxLength;
    }

    /**
     * Gets the personNameAttributeName attribute.
     *
     * @return Returns the personNameAttributeName.
     */
    public String getPersonNameAttributeName() {
        return personNameAttributeName;
    }

    /**
     * personNameAttributeName -
     attribute that provides the User Name - e.g. JONES,JOHN P
     */
    public void setPersonNameAttributeName(String personNameAttributeName) {
        if (StringUtils.isBlank(personNameAttributeName)) {
            throw new IllegalArgumentException("invalid (blank) personNameAttributeName");
        }
        this.personNameAttributeName = personNameAttributeName;
    }

    /**
     * Gets the universalIdAttributeName attribute.
     *
     * @return Returns the universalIdAttributeName.
     */
    public String getUniversalIdAttributeName() {
        return universalIdAttributeName;
    }

    /**
     * universalIdAttributeName  -
     attribute that provides the Universal User Id - e.g. 3583663872
     */
    public void setUniversalIdAttributeName(String universalIdAttributeName) {
        if (StringUtils.isBlank(universalIdAttributeName)) {
            throw new IllegalArgumentException("invalid (blank) universalIdAttributeName");
        }
        this.universalIdAttributeName = universalIdAttributeName;
    }

    /**
     * Gets the userIdAttributeName attribute.
     *
     * @return Returns the userIdAttributeName.
     */
    public String getUserIdAttributeName() {
        return userIdAttributeName;
    }

    /**
     * userIdAttributeName -
     attribute that provides the User Id - e.g. JPJONES
     */
    public void setUserIdAttributeName(String userIdAttributeName) {
        if (StringUtils.isBlank(userIdAttributeName)) {
            throw new IllegalArgumentException("invalid (blank) userIdAttributeName");
        }
        this.userIdAttributeName = userIdAttributeName;
    }

    /**
     * @return the target
     */
    public String getTarget() {
        return this.target;
    }

    /**
     * @param target the target to set
     */
    public void setTarget(String target) {
        this.target = target;
    }

    /**
     * @return the hrefText
     */
    public String getHrefText() {
        return this.hrefText;
    }

    /**
     * @param hrefText the hrefText to set
     */
    public void setHrefText(String hrefText) {
        this.hrefText = hrefText;
    }

    public ControlDefinitionType getType() {
        return type;
    }

    public void setType(ControlDefinitionType type) {
        this.type = type;
    }
}
