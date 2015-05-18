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
public abstract class ControlDefinitionBase extends DataDictionaryDefinitionBase implements ControlDefinition {
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


    public ControlDefinitionBase() {
    	ranged = true;
    }

    public boolean isDatePicker() {
        return datePicker;
    }

    /** Whether this control should have a date picker button next to the field.
     *  Valid for text fields.
     *  
     * @see ControlDefinition#setDatePicker(boolean)
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
     * @see ControlDefinition#setExpandedTextArea(boolean)
     */
    public void setExpandedTextArea(boolean eTextArea) {
        this.expandedTextArea=eTextArea;
    }

    /**
     * @see ControlDefinition#isCheckbox()
     */
    public boolean isCheckbox() {
        return false;
    }

    /**
     * @see ControlDefinition#isHidden()
     */
    public boolean isHidden() {
        return false;
    }

    /**
     * @see ControlDefinition#isRadio()
     */
    public boolean isRadio() {
        return false;
    }

    /**
     * @see ControlDefinition#isSelect()
     */
    public boolean isSelect() {
        return false;
    }
    
    /**
     * @see ControlDefinition#isSelect()
     */
    public boolean isMultiselect() {
        return false;
    }

    /**
     * @see ControlDefinition#isText()
     */
    public boolean isText() {
        return false;
    }

    /**
     * @see ControlDefinition#isTextarea()
     */
    public boolean isTextarea() {
        return false;
    }

    /**
     * @see ControlDefinition#isCurrency()
     */
    public boolean isCurrency() {
        return false;
    }

    /**
     *
     * @see ControlDefinition#isKualiUser()
     */
    public boolean isKualiUser() {
        return false;
    }

    /**
     * @see ControlDefinition#isWorkgroup()
     */
    public boolean isWorkflowWorkgroup() {
        return false;
    }

    /**
     * @see ControlDefinition#isFile()
     */
    public boolean isFile() {
        return false;
    }

    /**
     * @see ControlDefinition#isLookupHidden()
     */
    public boolean isLookupHidden() {
        return false;
    }

    /**
     * @see ControlDefinition#isLookupReadonly()
     */
    public boolean isLookupReadonly() {
        return false;
    }
    
    /**
     * @see ControlDefinition#isButton()
     */
    public boolean isButton() {
        return false;
    }
    
    /**
     * @see ControlDefinition#isLink()
     */
    public boolean isLink() {
        return false;
    }
    

    /**
     * @see ControlDefinition#setKeyValuesFinder(String)
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
     * @see ControlDefinition#getKeyValuesFinder()
     */
    public String getValuesFinderClass() {
        return valuesFinderClass;
    }

    /**
     * Size of a text control.
     * 
     * @see ControlDefinition#setSize(int)
     */
    public void setSize(Integer size) {
        this.size = size;
    }

    /**
     * @see ControlDefinition#getSize()
     */
    public Integer getSize() {
        return size;
    }

    /**
     * @see ControlDefinition#hasScript()
     */
    public boolean hasScript() {
        return false;
    }

    /**
     * Number of rows to display on a text-area widget.
     * 
     * @see ControlDefinition#setRows(int)
     */
    public void setRows(Integer rows) {
        this.rows = rows;
    }

    /**
     * @see ControlDefinition#getRows()
     */
    public Integer getRows() {
        return rows;
    }

    /**
     * Number of columns to display on a text-area widget.
     * 
     * @see ControlDefinition#setCols(int)
     */
    public void setCols(Integer cols) {
        this.cols = cols;
    }

    /**
     * @see ControlDefinition#getCols()
     */
    public Integer getCols() {
        return cols;
    }

    /**
     * Directly validate simple fields.
     *
     * @see org.kuali.rice.krad.datadictionary.DataDictionaryDefinition#completeValidation(Class, Object)
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
     * @see ControlDefinition#getScript()
     */
    public String getScript() {
        return script;
    }

    /**
     * JavaScript script to run when a select control's value is changed.
     * 
     * @see ControlDefinition#setScript()
     */
    public void setScript(String script) {
        this.script = script;
    }
    
    /**
     * @see ControlDefinition#isRanged()
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
     * @see Object#equals(Object)
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
    
    
}
