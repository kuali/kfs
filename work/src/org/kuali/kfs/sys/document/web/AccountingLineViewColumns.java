/*
 * Copyright 2008 The Kuali Foundation
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.datadictionary.AccountingLineViewColumnsDefinition;
import org.kuali.kfs.sys.document.service.AccountingLineFieldRenderingTransformation;

/**
 * A layout element that renders elements 
 */
public class AccountingLineViewColumns implements AccountingLineViewLineFillingElement {
    private List<AccountingLineViewField> fields;
    private AccountingLineViewColumnsDefinition definition;
    
    /**
     * Constructs a AccountingLineViewColumns
     * @param definition the data dictionary validation of this columns layout element
     * @param fields the fields to render within this columns layout element
     */
    public AccountingLineViewColumns(AccountingLineViewColumnsDefinition definition, List<AccountingLineViewField> fields) {
        this.definition = definition;
        this.fields = fields;
    }

    /**
     * Returns the name of this element
     * @see org.kuali.kfs.sys.document.web.TableJoining#getName()
     */
    public String getName() {
        return definition.getName();
    }

    /**
     * 
     * @see org.kuali.kfs.sys.document.web.TableJoining#getRequestedRowCount()
     */
    public int getRequestedRowCount() {
        return 1;
    }

    /**
     * This element should be stretched
     * @see org.kuali.kfs.sys.document.web.AccountingLineViewLineFillingElement#stretchToFillLine()
     */
    public boolean shouldStretchToFillLine() {
        return true;
    }

    /**
     * Joins the header row with a line filling cell, which includes within it an inner table that shows all the child fields
     * @see org.kuali.kfs.sys.document.web.TableJoining#joinRow(org.kuali.kfs.sys.document.web.AccountingLineTableRow, org.kuali.kfs.sys.document.web.AccountingLineTableRow)
     */
    public void joinRow(AccountingLineTableRow headerLabelRow, AccountingLineTableRow row) {
        AccountingLineTableCell cell = new AccountingLineTableCell();

        AccountingLineTable columnsTable = new AccountingLineTable();

        List<AccountingLineTableRow> rows = createRowsForFields();
        
        columnsTable.setRows(rows);
        cell.addRenderableElement(columnsTable);
        headerLabelRow.addCell(cell);
    }
    
    /**
     * Creates rows for the inner tables for each field inside this columsn definition
     * @return a List of created AccountingLineTableRows
     */
    protected List<AccountingLineTableRow> createRowsForFields() {
        List<AccountingLineTableRow> rows = new ArrayList<AccountingLineTableRow>();
        
        int countForThisRow = 0;
        AccountingLineTableRow row = new AccountingLineTableRow();
        for (AccountingLineViewField field : fields) {
            row.addCell(createHeaderCellForField(field));
            row.addCell(createCellForField(field));
            countForThisRow += 1;
            
            if (countForThisRow == definition.getColumnCount()) {
                rows.add(row);
                countForThisRow = 0;
                row = new AccountingLineTableRow();
            }
        }
        if (countForThisRow > 0) { // oops! we stopped mid-row and now need to fill it out
            while (countForThisRow < definition.getColumnCount()) {
                row.addCell(createPaddingCell());
                countForThisRow += 1;
            }
            rows.add(row);
        }
        
        return rows;
    }
    
    /**
     * Creates a header cell for for the given field 
     * @param field the field to create a header cell for
     * @return a header cell
     */
    protected AccountingLineTableCell createHeaderCellForField(AccountingLineViewField field) {
        AccountingLineTableCell headerCell = new AccountingLineTableCell();
        headerCell.setRendersAsHeader(true);
        headerCell.addRenderableElement(field.createHeaderLabel());
        return headerCell;
    }
    
    /**
     * Creates the "field" cell for the given field
     * @param field the field to create a cell for
     * @return the cell withe field in it
     */
    protected AccountingLineTableCell createCellForField(AccountingLineViewField field) {
        AccountingLineTableCell cell = new AccountingLineTableCell();
        cell.addRenderableElement(field);
        return cell;
    }
    
    /**
     * Creates an empty cell to pad out the place typically held for a cell
     * @return an empty table cell that spans two columns
     */
    protected AccountingLineTableCell createPaddingCell() {
        AccountingLineTableCell cell = new AccountingLineTableCell();
        cell.setColSpan(2);
        cell.setNeverEmpty(true);
        return cell;
    }

    /**
     * An exception state; line filling elements can only join tables through lines 
     * @see org.kuali.kfs.sys.document.web.TableJoining#joinTable(java.util.List)
     */
    public void joinTable(List<AccountingLineTableRow> rows) {
        throw new IllegalStateException("Line elements may not join a table directly; the specified rendering is incorrect");
    }

    /**
     * Has fields perform the transformations
     * @see org.kuali.kfs.sys.document.web.TableJoining#performFieldTransformations(java.util.List, org.kuali.kfs.sys.businessobject.AccountingLine, java.util.Map, java.util.Map)
     */
    public void performFieldTransformations(List<AccountingLineFieldRenderingTransformation> fieldTransformations, AccountingLine accountingLine, Map unconvertedValues) {
        int count = 0;
        for (AccountingLineViewField field : fields) {
            for (AccountingLineFieldRenderingTransformation transformation : fieldTransformations) {
                transformation.transformField(accountingLine, field.getField(), field.getDefinition(), unconvertedValues);
            }
        }
    }

    /**
     * Removes any child action blocks; surviving blocks are instructed to remove child blocks they have
     * @see org.kuali.kfs.sys.document.web.TableJoining#removeAllActionBlocks()
     */
    public void removeAllActionBlocks() {
        List<AccountingLineViewField> fieldsToRemove = new ArrayList<AccountingLineViewField>();
        for (AccountingLineViewField field : fields) {
            if (field.isActionBlock()) {
                fieldsToRemove.add(field);
            } else {
                field.removeAllActionBlocks();
            }
        }
        fields.removeAll(fieldsToRemove);
    }

    /**
     * Goes through all child fields; removes any fields which match unviewable blocks or otherwise, has the field remove unviewable blocks
     * @see org.kuali.kfs.sys.document.web.TableJoining#removeUnviewableBlocks(java.util.Set)
     */
    public void removeUnviewableBlocks(Set<String> unviewableBlocks) {
        List<AccountingLineViewField> unviewableFields = new ArrayList<AccountingLineViewField>();
        for (AccountingLineViewField field : fields) {
            if (unviewableBlocks.contains(field.getName())) {
                unviewableFields.add(field);
            } else {
                field.removeUnviewableBlocks(unviewableBlocks);
            }
        }
        fields.removeAll(unviewableFields);
    }

    /**
     * Has each field readOnlyize
     * @see org.kuali.kfs.sys.document.web.TableJoining#readOnlyizeReadOnlyBlocks(java.util.Set)
     */
    public void readOnlyizeReadOnlyBlocks(Set<String> readOnlyBlocks) {
        for (AccountingLineViewField field : fields) {
            field.readOnlyizeReadOnlyBlocks(readOnlyBlocks);
        }
    }

    /**
     * Gets the fields attribute. 
     * @return Returns the fields.
     */
    public List<AccountingLineViewField> getFields() {
        return fields;
    }

    /**
     * Sets the fields attribute value.
     * @param fields The fields to set.
     */
    public void setFields(List<AccountingLineViewField> fields) {
        this.fields = fields;
    }

    /**
     * @see org.kuali.kfs.sys.document.web.ReadOnlyable#isReadOnly()
     */
    public boolean isReadOnly() {
        for (AccountingLineViewField field : fields) {
            if (!field.isReadOnly()) return false;
        }
        return true;
    }

    /**
     * @see org.kuali.kfs.sys.document.web.ReadOnlyable#readOnlyize()
     */
    public void readOnlyize() {
        for (AccountingLineViewField field : fields) {
            field.readOnlyize();
        }
    }

    /**
     * Always returns 1; this will build an inner table in one cell
     * @see org.kuali.kfs.sys.document.web.AccountingLineViewLineFillingElement#getDisplayingFieldWidth()
     */
    public int getDisplayingFieldWidth() {
        return 1;
    }

    /**
     * @see org.kuali.kfs.sys.document.web.TableJoining#setEditableBlocks(java.util.Set)
     */
    public void setEditableBlocks(Set<String> editableBlocks) {
        for (AccountingLineViewField field : fields) {
            field.setEditableBlocks(editableBlocks);
        }
    }

    /**
     * @see org.kuali.kfs.sys.document.web.ReadOnlyable#setEditable()
     */
    public void setEditable() {
        for (AccountingLineViewField field : fields) {
            field.setEditable();
        }
    }

}
