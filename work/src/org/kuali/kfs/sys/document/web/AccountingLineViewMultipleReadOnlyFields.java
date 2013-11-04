/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.sys.document.web;

import java.util.List;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.datadictionary.AccountingLineViewMultipleReadOnlyFieldsDefinition;
import org.kuali.kfs.sys.document.web.renderers.MultipleReadOnlyFieldsRenderer;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.util.FieldUtils;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Represents multiple fields displaying with their values in a single cell
 */
public class AccountingLineViewMultipleReadOnlyFields extends FieldTableJoiningWithHeader {
    private AccountingLineViewMultipleReadOnlyFieldsDefinition definition;
    private List<Field> fields;
    private static DataDictionaryService dataDictionaryService;
    
    /**
     * Constructs a AccountingLineViewMultipleReadOnlyFields
     * @param definition data dictionary definition which created this
     * @param fields the fields to render as read only 
     * 
     * KRAD Conversion: Customization of the fields - No use of data dictionary
     */
    public AccountingLineViewMultipleReadOnlyFields(AccountingLineViewMultipleReadOnlyFieldsDefinition definition, List<Field> fields) {
        this.definition = definition;
        this.fields = fields;
    }

    /**
     * 
     * @see org.kuali.kfs.sys.document.web.TableJoiningWithHeader#createHeaderLabel()
     */
    public HeaderLabel createHeaderLabel() {
        return new LiteralHeaderLabel(KFSConstants.BLANK_SPACE);
    }

    /**
     * Returns the top field name given in the definition
     * @see org.kuali.kfs.sys.document.web.ElementNamable#getName()
     */
    public String getName() {
        return definition.getFieldNames().get(0);
    }

    /**
     * None of the read only fields will be associated with quickfinders, so this method does nothing
     * @see org.kuali.kfs.sys.document.web.RenderableElement#appendFields(java.util.List)
     */
    public void appendFields(List<Field> fields) {}

    /**
     * There are no input fields here, so no need to set tab indices
     * @see org.kuali.kfs.sys.document.web.RenderableElement#populateWithTabIndexIfRequested(int)
     */
    public void populateWithTabIndexIfRequested(int reallyHighIndex) {}

    /**
     * @return the fields associated with this Multiple read only fields
     * 
     * KRAD Conversion: Gets the fields - No use of data dictionary
     */
    public List<Field> getFields() {
        return fields;
    }

    /**
     * 
     * @see org.kuali.kfs.sys.document.web.RenderableElement#renderElement(javax.servlet.jsp.PageContext, javax.servlet.jsp.tagext.Tag, org.kuali.kfs.sys.document.web.AccountingLineRenderingContext)
     * 
     * KRAD Conversion: Customization of the fields - No use of data dictionary
     */
    public void renderElement(PageContext pageContext, Tag parentTag, AccountingLineRenderingContext renderingContext) throws JspException {
        final org.kuali.rice.krad.datadictionary.BusinessObjectEntry boEntry = getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(renderingContext.getAccountingLine().getClass().getName());
        if (fields != null && !fields.isEmpty()) {
            for (Field field : fields) {
                setShortLabelsForFields(field, boEntry);
                setValueForField(field, renderingContext.getAccountingLine());
                setInquiryUrlForField(field, renderingContext.getAccountingLine());
            }
        }
        
        MultipleReadOnlyFieldsRenderer renderer = new MultipleReadOnlyFieldsRenderer();
        renderer.setFields(getFields());
        renderer.render(pageContext, parentTag);
        renderer.clear();
    }
    
    /**
     * For each field, set the short label, or, failing that, set the label
     * @param boEntry the business object entry for the accounting line
     * 
     * KRAD Conversion: Customization of the fields - Uses data dictionary
     * 
     */
    protected void setShortLabelsForFields(Field field, org.kuali.rice.krad.datadictionary.BusinessObjectEntry boEntry) {
        final AttributeDefinition propertyDefinition = boEntry.getAttributeDefinition(field.getPropertyName());
        final String label = (propertyDefinition == null) ? "" : (!StringUtils.isBlank(propertyDefinition.getShortLabel()) ? propertyDefinition.getShortLabel() : propertyDefinition.getLabel());
        field.setFieldLabel(label);
    }
    
    /**
     * Sets the value for the field before rendering
     * @param field the field to set the value of
     * @param accountingLine the accounting line the field is associated with, which holds the value
     * 
     * KRAD Conversion: Setting the property value of the field - No use of data dictionary
     */
    protected void setValueForField(Field field, AccountingLine accountingLine) {
        field.setPropertyValue(ObjectUtils.getPropertyValue(accountingLine, field.getPropertyName()));
    }
    
    /**
     * Populates the inquiry url on the field if possible
     * @param field the field to set the inquiry url on
     * @param accountingLine the accounting line holding values for the field
     * 
     * KRAD Conversion: Setting inquiry url for the fields - No use of data dictionary
     */
    protected void setInquiryUrlForField(Field field, AccountingLine accountingLine) {
        if (!StringUtils.isBlank(field.getPropertyValue())) {
            FieldUtils.setInquiryURL(field, accountingLine, field.getPropertyName());
        }
    }
    
    /**
     * @return the implementation of the DataDictionaryService
     */
    protected DataDictionaryService getDataDictionaryService() {
        if (dataDictionaryService == null) {
            dataDictionaryService = SpringContext.getBean(DataDictionaryService.class);
        }
        return dataDictionaryService;
    }

}
