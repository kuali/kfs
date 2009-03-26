/*
 * Copyright 2008 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.sys.document.web;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.authorization.AccountingLineAuthorizer;
import org.kuali.kfs.sys.web.struts.KualiAccountingDocumentFormBase;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.util.FieldUtils;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.web.ui.Field;

/**
 * A container which holds a single accounting line and the elements which will render it
 */
public class RenderableAccountingLineContainer implements RenderableElement, AccountingLineRenderingContext {
    private List<AccountingLineTableRow> rows;
    private List<AccountingLineViewAction> actions;
    private AccountingLine accountingLine;
    private String accountingLineProperty;
    private List<Field> fields;
    private List<String> fieldNames;
    private KualiAccountingDocumentFormBase form;
    private String groupLabel;
    private Integer lineCount;
    private List errors;
    private AccountingLineAuthorizer accountingLineAuthorizer;
    private boolean editableLine;
    private boolean deletable = false;
    
    /**
     * Constructs a RenderableAccountingLineContainer
     * @param form the form being rendered
     * @param accountingLine the accounting line this container will render
     * @param accountingLineProperty the property to that accounting line
     * @param rows the rows to render
     * @param newLine whether this is a new accounting line or not
     * @param groupLabel the label for the group this accounting line is being rendered part of
     * @param errors the set of errors currently on the document
     * @param accountingLineAuthorizer the accounting line authorizer for the document
     * @param editableLine whether this line, as a whole _line_ is editable
     */
    public RenderableAccountingLineContainer(KualiAccountingDocumentFormBase form, AccountingLine accountingLine, String accountingLineProperty, List<AccountingLineTableRow> rows, Integer lineCount, String groupLabel, List errors, AccountingLineAuthorizer accountingLineAuthorizer, boolean editableLine) {
        this.form = form;
        this.accountingLine = accountingLine;
        this.accountingLineProperty = accountingLineProperty;
        this.rows = rows;
        this.lineCount = lineCount;
        this.groupLabel = groupLabel;
        this.errors = errors;
        this.accountingLineAuthorizer = accountingLineAuthorizer;
        this.editableLine = editableLine;
    }
    
    /**
     * Gets the accountingLine attribute. 
     * @return Returns the accountingLine.
     */
    public AccountingLine getAccountingLine() {
        return accountingLine;
    }

    /**
     * Gets the accountingLineProperty attribute. 
     * @return Returns the accountingLineProperty.
     */
    public String getAccountingLineProperty() {
        return accountingLineProperty;
    }

    /**
     * Gets the actions attribute. 
     * @return Returns the actions.
     */
    public List<AccountingLineViewAction> getActionsForLine() {
        if (actions == null) {
            actions = accountingLineAuthorizer.getActions(form.getFinancialDocument(), this, accountingLineProperty, lineCount, GlobalVariables.getUserSession().getPerson(), groupLabel);
        }
        return actions;
    }

    /**
     * Gets the newLine attribute. 
     * @return Returns the newLine.
     */
    public boolean isNewLine() {
        return lineCount == null;
    }

    /**
     * @see org.kuali.kfs.sys.document.web.AccountingLineRenderingContext#getCurrentLineCount()
     */
    public Integer getCurrentLineCount() {
        return lineCount;
    }

    /**
     * Gets the rows attribute. 
     * @return Returns the rows.
     */
    public List<AccountingLineTableRow> getRows() {
        return rows;
    }
    
    /**
     * @return the number of cells this accounting line container will render
     */
    public int getCellCount() {
        int maxCells = 0;
        for (AccountingLineTableRow row : rows) {
            final int maxRowCellCount = row.getChildCellCount();
            if (maxCells < maxRowCellCount) {
                maxCells = maxRowCellCount;
            }
        }
        return maxCells;
    }
    
    /**
     * Adds empty cells to a table row
     * @param cellCount the number of cells we should be rendering
     * @param row the row to pad out
     */
    protected void padOutRow(int cellCount, AccountingLineTableRow row) {
        while ((cellCount - row.getChildCellCount()) > 0) {
            row.addCell(new AccountingLineTableCell());
        }
    }
    
    /**
     * While holding an action block, this is not an action block
     * @see org.kuali.kfs.sys.document.web.RenderableElement#isActionBlock()
     */
    public boolean isActionBlock() {
        return false;
    }
    
    /**
     * This is never empty
     * @see org.kuali.kfs.sys.document.web.RenderableElement#isEmpty()
     */
    public boolean isEmpty() {
        return false;
    }
    
    /**
     * This is not hidden 
     * @see org.kuali.kfs.sys.document.web.RenderableElement#isHidden()
     */
    public boolean isHidden() {
        return false;
    }
    
    /**
     * Renders all the rows
     * @see org.kuali.kfs.sys.document.web.RenderableElement#renderElement(javax.servlet.jsp.PageContext, javax.servlet.jsp.tagext.Tag, org.kuali.kfs.sys.document.web.AccountingLineRenderingContext)
     */
    public void renderElement(PageContext pageContext, Tag parentTag, AccountingLineRenderingContext renderingContext) throws JspException {
        for (AccountingLineTableRow row : rows) {
            row.renderElement(pageContext, parentTag, renderingContext);
        }
    }
    
    /**
     * @see org.kuali.kfs.sys.document.web.AccountingLineRenderingContext#getAccountingLinePropertyPath()
     */
    public String getAccountingLinePropertyPath() {
        return accountingLineProperty;
    }
    
    /**
     * Appends all fields from rows that this contains
     * @see org.kuali.kfs.sys.document.web.RenderableElement#appendFieldNames(java.util.List)
     */
    public void appendFields(List<Field> fields) {
        for (AccountingLineTableRow row : rows) {
            row.appendFields(fields);
        }
    }
    
    /**
     * Returns all of the field names within the accounting line to render
     * @return a List of field names with the accounting line property prefixed
     */
    public List<Field> getFieldsForAccountingLine() {
        if (fields == null) {
            fields = new ArrayList<Field>();
            appendFields(fields);
        }
        return fields;
    }
    
    /**
     * @see org.kuali.kfs.sys.document.web.AccountingLineRenderingContext#getFieldNamesForAccountingLine()
     */
    public List<String> getFieldNamesForAccountingLine() {
        if (fieldNames == null) {
            fieldNames = new ArrayList<String>();
            for (Field field : getFieldsForAccountingLine()) {
                fieldNames.add(accountingLineProperty+"."+field.getPropertyName());
            }
        }
        return fieldNames;
    }
    
    /**
     * @see org.kuali.kfs.sys.document.web.RenderableElement#populateWithTabIndexIfRequested(int[], int)
     */
    public void populateWithTabIndexIfRequested( int reallyHighIndex) {
        for (AccountingLineTableRow row : rows) {
            row.populateWithTabIndexIfRequested(reallyHighIndex);
        }
    }
    
    /**
     * Returns the unconvertedValues for the current form
     * @see org.kuali.kfs.sys.document.web.AccountingLineRenderingContext#getUnconvertedValues()
     */
    public Map getUnconvertedValues() {
        return form.getUnconvertedValues();
    }
    
    /**
     * @see org.kuali.kfs.sys.document.web.AccountingLineRenderingContext#populateValuesForFields()
     */
    public void populateValuesForFields() {
        FieldUtils.populateFieldsFromBusinessObject(getFieldsForAccountingLine(), accountingLine);
        
        BusinessObjectEntry boDDEntry = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(getAccountingLine().getClass().getName());
        
        for (Field field : getFieldsForAccountingLine()) {
            setUnconvertedValueIfNecessary(field);
            setShouldShowSecure(field, boDDEntry);
        }
    }
    
    /**
     * Sees if the given field has an unconverted value living in the unconverted value map and if so,
     * changes the value to that
     * @param field the field to possibly set an unconverted value on
     */
    protected void setUnconvertedValueIfNecessary(Field field) {
        String propertyName = accountingLineProperty+"."+field.getPropertyName();
        if (getUnconvertedValues().get(propertyName) != null) {
            field.setPropertyValue((String)getUnconvertedValues().get(propertyName));
        }
    }
    
    /**
     * Sets the masked value equal to the value if the current user can see the unmasked value for a secure field
     * @param field the field to possible change the value for
     * @param boDDEntry the data dictionary entry for the accounting line
     */
    protected void setShouldShowSecure(Field field, BusinessObjectEntry boDDEntry) {
        // TODO: FIX
        
        // from Warren:  k... org.kuali.rice.kns.service.BusinessObjectAuthorizationService.getMaintenanceDocumentRestrictions(MaintenanceDocument, Person) has the determination of what restrictions there should be
        // org.kuali.rice.kns.util.FieldUtils.applyAuthorization(Field, String, MaintenanceDocumentRestrictions) applies those restrictions
    }
    /**
     * @see org.kuali.kfs.sys.document.web.AccountingLineRenderingContext#getAccountingDocument()
     */
    public AccountingDocument getAccountingDocument() {
        return form.getFinancialDocument();
    }
    
    /**
     * @see org.kuali.kfs.sys.document.web.AccountingLineRenderingContext#fieldsCanRenderDynamicLabels()
     */
    public boolean fieldsCanRenderDynamicLabels() {
        return !form.isHideDetails();
    }
    /**
     * @see org.kuali.kfs.sys.document.web.AccountingLineRenderingContext#fieldsShouldRenderHelp()
     */
    public boolean fieldsShouldRenderHelp() {
        return form.isFieldLevelHelpEnabled();
    }
    /**
     * @see org.kuali.kfs.sys.document.web.AccountingLineRenderingContext#getTabState(java.lang.String)
     */
    public String getTabState(String tabKey) {
        return form.getTabState(tabKey);
    }
    /**
     * @see org.kuali.kfs.sys.document.web.AccountingLineRenderingContext#incrementTabIndex()
     */
    public void incrementTabIndex() {
        form.incrementTabIndex();
    }
    
    /**
     * @see org.kuali.kfs.sys.document.web.AccountingLineRenderingContext#getGroupLabel()
     */
    public String getGroupLabel() {
       return this.groupLabel; 
    }

    /**
     * Gets the errors attribute. 
     * @return Returns the errors.
     */
    public List getErrors() {
        return errors;
    }

    /**
     * @see org.kuali.kfs.sys.document.web.AccountingLineRenderingContext#getForm()
     */
    public KualiAccountingDocumentFormBase getForm() {
        return form;
    }

    /**
     * @see org.kuali.kfs.sys.document.web.AccountingLineRenderingContext#getAccountingLineContainingObjectPropertyName()
     */
    public String getAccountingLineContainingObjectPropertyName() {
        return StringUtils.substringBeforeLast(this.getAccountingLinePropertyPath(), String.valueOf(PropertyUtils.NESTED_DELIM));
    }

    /**
     * @see org.kuali.kfs.sys.document.web.AccountingLineRenderingContext#isFieldModifyable(org.kuali.kfs.sys.document.web.AccountingLineViewField)
     */
    public boolean isFieldModifyable(AccountingLineViewField field) {
        Person currentUser = GlobalVariables.getUserSession().getPerson();
        return getForm().getDocumentActions().containsKey(KNSConstants.KUALI_ACTION_CAN_EDIT) && accountingLineAuthorizer.hasEditPermissionOnField(getAccountingDocument(), accountingLine, this.accountingLineProperty, field.getName(), currentUser);
    }

    /**
     * Gets the editableLine attribute. 
     * @return Returns the editableLine.
     */
    public boolean isEditableLine() {
        return editableLine;
    }

    /**
     * Determines whether the line within this rendering context can be deleted.
     * @see org.kuali.kfs.sys.document.web.AccountingLineRenderingContext#allowDelete()
     */
    public boolean allowDelete() {
        return deletable;
    }
    
    /**
     * Makes the line within this accounting line context deletable
     */
    public void makeDeletable() {
        deletable = true;
    }
}

