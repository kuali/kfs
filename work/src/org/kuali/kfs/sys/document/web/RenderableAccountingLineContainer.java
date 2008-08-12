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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import org.kuali.kfs.sys.businessobject.AccountingLine;

/**
 * A container which holds a single accounting line and the elements which will render it
 */
public class RenderableAccountingLineContainer implements RenderableElement, AccountingLineRenderingContext {
    private List<AccountingLineTableRow> rows;
    private List<AccountingLineViewAction> actions;
    private boolean newLine;
    private AccountingLine accountingLine;
    private String accountingLineProperty;
    private boolean renderHelp = false;
    private boolean showDetails = true;
    private List<String> fieldNames;
    
    /**
     * Gets the accountingLine attribute. 
     * @return Returns the accountingLine.
     */
    public AccountingLine getAccountingLine() {
        return accountingLine;
    }
    /**
     * Sets the accountingLine attribute value.
     * @param accountingLine The accountingLine to set.
     */
    public void setAccountingLine(AccountingLine accountingLine) {
        this.accountingLine = accountingLine;
    }
    /**
     * Gets the accountingLineProperty attribute. 
     * @return Returns the accountingLineProperty.
     */
    public String getAccountingLineProperty() {
        return accountingLineProperty;
    }
    /**
     * Sets the accountingLineProperty attribute value.
     * @param accountingLineProperty The accountingLineProperty to set.
     */
    public void setAccountingLineProperty(String accountingLineProperty) {
        this.accountingLineProperty = accountingLineProperty;
    }
    /**
     * Gets the actions attribute. 
     * @return Returns the actions.
     */
    public List<AccountingLineViewAction> getActions() {
        return actions;
    }
    /**
     * Sets the actions attribute value.
     * @param actions The actions to set.
     */
    public void setActions(List<AccountingLineViewAction> actions) {
        this.actions = actions;
    }
    /**
     * Gets the newLine attribute. 
     * @return Returns the newLine.
     */
    public boolean isNewLine() {
        return newLine;
    }
    /**
     * Sets the newLine attribute value.
     * @param newLine The newLine to set.
     */
    public void setNewLine(boolean newLine) {
        this.newLine = newLine;
    }
    /**
     * Gets the rows attribute. 
     * @return Returns the rows.
     */
    public List<AccountingLineTableRow> getRows() {
        return rows;
    }
    /**
     * Sets the rows attribute value.
     * @param rows The rows to set.
     */
    public void setRows(List<AccountingLineTableRow> rows) {
        this.rows = rows;
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
     * @see org.kuali.kfs.sys.document.web.AccountingLineRenderingContext#getActionsForLine()
     */
    public List<AccountingLineViewAction> getActionsForLine() {
        return this.actions;
    }
    
    /**
     * Gets the renderHelp attribute. 
     * @return Returns the renderHelp.
     */
    public boolean isRenderHelp() {
        return renderHelp;
    }
    /**
     * Sets the renderHelp attribute value.
     * @param renderHelp The renderHelp to set.
     */
    public void setRenderHelp(boolean renderHelp) {
        this.renderHelp = renderHelp;
    }
    /**
     * Gets the showDetails attribute. 
     * @return Returns the showDetails.
     */
    public boolean isShowDetails() {
        return showDetails;
    }
    /**
     * Sets the showDetails attribute value.
     * @param showDetails The showDetails to set.
     */
    public void setShowDetails(boolean showDetails) {
        this.showDetails = showDetails;
    }
    /**
     * @see org.kuali.kfs.sys.document.web.AccountingLineRenderingContext#fieldsShouldRenderHelp()
     */
    public boolean fieldsShouldRenderHelp() {
        return renderHelp;
    }
    
    /**
     * @see org.kuali.kfs.sys.document.web.AccountingLineRenderingContext#fieldsCanRenderDynamicLabels()
     */
    public boolean fieldsCanRenderDynamicLabels() {
        return showDetails;
    }
    
    /**
     * Appends all field names from rows that this contains
     * @see org.kuali.kfs.sys.document.web.RenderableElement#appendFieldNames(java.util.List)
     */
    public void appendFieldNames(List<String> fieldNames) {
        for (AccountingLineTableRow row : rows) {
            row.appendFieldNames(fieldNames);
        }
    }
    
    /**
     * Returns all of the field names within the accounting line to render
     * @return a List of field names with the accounting line property prefixed
     */
    public List<String> getFieldNamesForAccountingLine() {
        if (fieldNames == null) {
            fieldNames = new ArrayList<String>();
            appendFieldNames(fieldNames);
            fieldNames = prefixPropertyFieldNames(fieldNames);
        }
        return fieldNames;
    }
    
    /**
     * Prefixes given field names with the name of the accounting line property
     * @param fieldNames the field names to prefix
     * @return a list of prefixed field names
     */
    protected List<String> prefixPropertyFieldNames(List<String> fieldNames) {
        List<String> fixedFieldNames = new ArrayList<String>(fieldNames.size() * 2);
        for (String fieldName : fieldNames) {
            fixedFieldNames.add(accountingLineProperty + "." + fieldName);
        }
        return fixedFieldNames;
    }
    
    /**
     * @see org.kuali.kfs.sys.document.web.RenderableElement#populateWithTabIndexIfRequested(int[], int)
     */
    public void populateWithTabIndexIfRequested(int[] passIndexes, int reallyHighIndex) {
        for (AccountingLineTableRow row : rows) {
            row.populateWithTabIndexIfRequested(passIndexes, reallyHighIndex);
        }
    }
}
