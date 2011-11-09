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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.datadictionary.AccountingLineViewCurrentBaseAmountFieldDefinition;
import org.kuali.kfs.sys.document.datadictionary.AccountingLineViewFieldDefinition;
import org.kuali.kfs.sys.document.service.AccountingLineFieldRenderingTransformation;
import org.kuali.rice.kns.web.ui.Field;

/**
 * Represents a current/base amount for an accounting line
 */
public class AccountingLineViewCurrentBaseAmount implements TableJoiningWithHeader, ReadOnlyable {
    private Field currentAmountField;
    private Field baseAmountField;
    private AccountingLineViewCurrentBaseAmountFieldDefinition definition;
    private AccountingLineViewFieldDefinition currentAmountFieldDefinition;
    private AccountingLineViewFieldDefinition baseAmountFieldDefinition;

    /**
     * Returns null; we don't want to participate in normal naming schemes
     * @see org.kuali.kfs.sys.document.web.TableJoining#getName()
     */
    public String getName() {
        return null;
    }

    /**
     * Checks if either the current amount field or base amount field need to be set to read only
     * @see org.kuali.kfs.sys.document.web.FieldTableJoining#readOnlyizeReadOnlyBlocks(java.util.Set)
     */
    public void readOnlyizeReadOnlyBlocks(Set<String> readOnlyBlocks) {
        if (currentAmountField != null) {
            readOnlyizeField(currentAmountField, readOnlyBlocks);
        }
        if (baseAmountField != null) {
            readOnlyizeField(baseAmountField, readOnlyBlocks);
        }
        if (baseAmountField != null && currentAmountField != null) {
            if (baseAmountField.isReadOnly() && !currentAmountField.isReadOnly()) {
                currentAmountField.setFieldRequired(true);
            } else if (currentAmountField.isReadOnly() && !baseAmountField.isReadOnly()) {
                baseAmountField.setFieldRequired(true);
            }
        }
    }
    
    /**
     * @see org.kuali.kfs.sys.document.web.ReadOnlyable#isReadOnly()
     */
    public boolean isReadOnly() {
        boolean readOnly = true;
        if (currentAmountField != null) {
            readOnly &= currentAmountField.isReadOnly();
        }
        if (baseAmountField != null) {
            readOnly &= baseAmountField.isReadOnly();
        }
        return readOnly;
    }

    /**
     * @see org.kuali.kfs.sys.document.web.ReadOnlyable#readOnlyize()
     */
    public void readOnlyize() {
        if (currentAmountField != null) {
            currentAmountField.setReadOnly(true);
        }
        if (baseAmountField != null) {
            baseAmountField.setReadOnly(true);
        }
    }

    /**
     * Checks if the given field is named as a readOnlyBlock; if so, makes it read only
     * @param field the field to check
     * @param readOnlyBlocks the names of all read only blocks
     * 
     * KRAD Conversion: Customization of the fields by setting them to read only - No use of data dictionary
     */
    protected void readOnlyizeField(Field field, Set<String> readOnlyBlocks) {
        if (field != null && readOnlyBlocks.contains(field.getPropertyName())) {
            field.setReadOnly(true);
        }
    }

    /**
     * Always returns 2 - one line for the header, one line for the fields
     * @see org.kuali.kfs.sys.document.web.TableJoining#getRequestedRowCount()
     */
    public int getRequestedRowCount() {
        return 2;
    }

    /**
     * Adds the header cell to the first row, and the regular cell to the second row
     * @see org.kuali.kfs.sys.document.web.TableJoining#joinRow(org.kuali.kfs.sys.document.web.AccountingLineTableRow, org.kuali.kfs.sys.document.web.AccountingLineTableRow)
     */
    public void joinRow(AccountingLineTableRow headerLabelRow, AccountingLineTableRow row) {
        if (currentAmountField != null) {
            headerLabelRow.addCell(createHeaderCellForField(currentAmountField));
            row.addCell(createCellForField(currentAmountField, currentAmountFieldDefinition));
        }
        if (baseAmountField != null) {
            headerLabelRow.addCell(createHeaderCellForField(baseAmountField));
            row.addCell(createCellForField(baseAmountField, baseAmountFieldDefinition));
        }
    }

    /**
     * Adds the header cell to the first row and adds to the second row a cell that spans all remaining rows
     * @see org.kuali.kfs.sys.document.web.TableJoining#joinTable(java.util.List)
     */
    public void joinTable(List<AccountingLineTableRow> rows) {
        final int remainingRowCount = rows.size() - 1;
        
        if (currentAmountField != null) {
            rows.get(0).addCell(createHeaderCellForField(currentAmountField));
            
            AccountingLineTableCell currentCell = createCellForField(currentAmountField, currentAmountFieldDefinition);
            currentCell.setRowSpan(remainingRowCount);
            rows.get(1).addCell(currentCell);
        }
        if (baseAmountField != null) {
            rows.get(0).addCell(createHeaderCellForField(baseAmountField));
            
            AccountingLineTableCell baseCell = createCellForField(baseAmountField, baseAmountFieldDefinition);
            baseCell.setRowSpan(remainingRowCount);
            rows.get(1).addCell(baseCell);
        }
        
    }

    /**
     * Does nothing - we don't have action blocks, like, ever
     * @see org.kuali.kfs.sys.document.web.TableJoining#removeAllActionBlocks()
     */
    public void removeAllActionBlocks() {}

    /**
     * Checks to see if either the current amount or the base amount are unviewable; if so, sets them to null
     * @see org.kuali.kfs.sys.document.web.FieldTableJoining#removeUnviewableBlocks(java.util.Set)
     */
    public void removeUnviewableBlocks(Set<String> unviewableBlocks) {
        if (isFieldUnviewable(currentAmountField, unviewableBlocks)) {
            currentAmountField = null;
        }
        if (isFieldUnviewable(baseAmountField, unviewableBlocks)) {
            baseAmountField = null;
        }
    }
    
    /**
     * Determines if the given field is among the blocks which should not be viewable
     * @param field the field to check for unviewability
     * @param unviewableBlocks the names of all unviewable blocks
     * @return true if the field should not be viewable, false if we can see it
     */
    protected boolean isFieldUnviewable(Field field, Set<String> unviewableBlocks) {
        return field != null && unviewableBlocks.contains(field.getPropertyName());
    }
    
    /**
     * Creates a table cell with a renderable field inside
     * @param field the field to create a cell for
     * @return a cell that wraps the given field 
     * 
     * KRAD Conversion: Performs setting up the field for the cell
     */
    protected AccountingLineTableCell createCellForField(Field field, AccountingLineViewFieldDefinition definition) {
        AccountingLineTableCell cell = new AccountingLineTableCell();
        AccountingLineViewField renderableField = new AccountingLineViewField();
        renderableField.setField(field);
        renderableField.setDefinition(definition);
        cell.addRenderableElement(renderableField);
        return cell;
    }
    
    /**
     * Creates a header cell for the given field
     * @param field the field to create a header cell for
     * @return a header cell
     * 
     * KRAD Conversion: Customization of the header cell with label from fields - No use of data dictionary
     * 
     */
    protected AccountingLineTableCell createHeaderCellForField(Field field) {
        AccountingLineTableCell headerCell = new AccountingLineTableCell();
        headerCell.setRendersAsHeader(true);
        headerCell.addRenderableElement(new LiteralHeaderLabel(field.getFieldLabel()));
        return headerCell;
    }

    /**
     * @see org.kuali.kfs.sys.document.web.FieldTableJoining#performFieldTransformations(java.util.List, org.kuali.kfs.sys.businessobject.AccountingLine, java.util.Map, java.util.Map)
     */
    public void performFieldTransformations(List<AccountingLineFieldRenderingTransformation> fieldTransformations, AccountingLine accountingLine, Map unconvertedValues) {
        for (AccountingLineFieldRenderingTransformation fieldTransformation : fieldTransformations) {
            fieldTransformation.transformField(accountingLine, getCurrentAmountField(), getCurrentAmountFieldDefinition(), unconvertedValues);
            fieldTransformation.transformField(accountingLine, getBaseAmountField(), getBaseAmountFieldDefinition(), unconvertedValues);
        }
    }

    /**
     * Not used; returns null.
     * @see org.kuali.kfs.sys.document.web.TableJoiningWithHeader#createHeaderLabel()
     */
    public HeaderLabel createHeaderLabel() {
        return null;
    }

    /**
     * This field is never hidden
     * @see org.kuali.kfs.sys.document.web.TableJoiningWithHeader#isHidden()
     */
    public boolean isHidden() {
        return false;
    }

    /**
     * Gets the baseAmountField attribute. 
     * @return Returns the baseAmountField.
     * 
     * KRAD Conversion: Retrieving the field
     */
    public Field getBaseAmountField() {
        return baseAmountField;
    }

    /**
     * Sets the baseAmountField attribute value.
     * @param baseAmountField The baseAmountField to set.
     * 
     * KRAD Conversion: Setting up field's amount
     */
    public void setBaseAmountField(Field baseAmountField) {
        this.baseAmountField = baseAmountField;
    }

    /**
     * Gets the currentAmountField attribute. 
     * @return Returns the currentAmountField.
     */
    public Field getCurrentAmountField() {
        return currentAmountField;
    }

    /**
     * Sets the currentAmountField attribute value.
     * @param currentAmountField The currentAmountField to set.
     */
    public void setCurrentAmountField(Field currentAmountField) {
        this.currentAmountField = currentAmountField;
    }

    /**
     * Gets the definition attribute. 
     * @return Returns the definition.
     */
    public AccountingLineViewCurrentBaseAmountFieldDefinition getDefinition() {
        return definition;
    }

    /**
     * Sets the definition attribute value.
     * @param definition The definition to set.
     */
    public void setDefinition(AccountingLineViewCurrentBaseAmountFieldDefinition definition) {
        this.definition = definition;
    }

    /**
     * Gets the baseAmountFieldDefinition attribute. 
     * @return Returns the baseAmountFieldDefinition.
     */
    public AccountingLineViewFieldDefinition getBaseAmountFieldDefinition() {
        return baseAmountFieldDefinition;
    }

    /**
     * Sets the baseAmountFieldDefinition attribute value.
     * @param baseAmountFieldDefinition The baseAmountFieldDefinition to set.
     */
    public void setBaseAmountFieldDefinition(AccountingLineViewFieldDefinition baseAmountFieldDefinition) {
        this.baseAmountFieldDefinition = baseAmountFieldDefinition;
    }

    /**
     * Gets the currentAmountFieldDefinition attribute. 
     * @return Returns the currentAmountFieldDefinition.
     */
    public AccountingLineViewFieldDefinition getCurrentAmountFieldDefinition() {
        return currentAmountFieldDefinition;
    }

    /**
     * Sets the currentAmountFieldDefinition attribute value.
     * @param currentAmountFieldDefinition The currentAmountFieldDefinition to set.
     */
    public void setCurrentAmountFieldDefinition(AccountingLineViewFieldDefinition currentAmountFieldDefinition) {
        this.currentAmountFieldDefinition = currentAmountFieldDefinition;
    }

    /**
     * @see org.kuali.kfs.sys.document.web.TableJoining#setEditableBlocks(java.util.Set)
     */
    public void setEditableBlocks(Set<String> editableBlocks) {
        if (currentAmountField != null) {
            setEditableField(currentAmountField, editableBlocks);
        }
        if (baseAmountField != null) {
            setEditableField(baseAmountField, editableBlocks);
        }
        
        if (baseAmountField != null && currentAmountField != null) {
            if (baseAmountField.isReadOnly() && !currentAmountField.isReadOnly()) {
                currentAmountField.setReadOnly(false);
            } 
            else if (currentAmountField.isReadOnly() && !baseAmountField.isReadOnly()) {
                baseAmountField.setReadOnly(false);
            }
        }
    }

    /**
     * @see org.kuali.kfs.sys.document.web.ReadOnlyable#setEditable()
     */
    public void setEditable() {
        if (currentAmountField != null) {
            currentAmountField.setReadOnly(false);
        }
        
        if (baseAmountField != null) {
            baseAmountField.setReadOnly(false);
        }
    }
    
    /**
     * Checks if the given field is named as an editableBlocks; if so, makes it editable
     * @param field the field to check
     * @param editableBlocks the names of all editable blocks
     * 
     * KRAD Conversion: Customization of the fields - No use of data dictionary
     */
    protected void setEditableField(Field field, Set<String> editableBlocks) {
        if (field != null && editableBlocks.contains(field.getPropertyName())) {
            field.setReadOnly(false);
        }
    }    
}
