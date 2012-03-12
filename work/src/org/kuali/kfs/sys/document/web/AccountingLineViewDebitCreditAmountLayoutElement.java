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

import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.datadictionary.AccountingLineViewDebitCreditAmountFieldDefinition;
import org.kuali.kfs.sys.document.datadictionary.AccountingLineViewFieldDefinition;
import org.kuali.kfs.sys.document.service.AccountingLineFieldRenderingTransformation;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kns.web.ui.Field;

/**
 * A table joining element which adds two fields to an amount: debit amount and credit amount
 */
public class AccountingLineViewDebitCreditAmountLayoutElement implements TableJoiningWithHeader, ReadOnlyable {
    private Field debitAmountField;
    private Field creditAmountField;
    private AccountingLineViewDebitCreditAmountFieldDefinition definition;
    private AccountingLineViewFieldDefinition debitFieldDefinition;
    private AccountingLineViewFieldDefinition creditFieldDefinition;
    
    /**
     * Returns whether the debit and the credit amount fields are both read only 
     * @see org.kuali.kfs.sys.document.web.ReadOnlyable#isReadOnly()
     */
    public boolean isReadOnly() {
        return (debitAmountField == null || debitAmountField.isReadOnly()) && (creditAmountField == null || creditAmountField.isReadOnly());
    }
    
    /**
     * Read onlyizes both the credit and the debit amount fields
     * @see org.kuali.kfs.sys.document.web.ReadOnlyable#readOnlyize()
     */
    public void readOnlyize() {
        if (debitAmountField != null) {
            debitAmountField.setReadOnly(true);
        }
        if (creditAmountField != null) {
            creditAmountField.setReadOnly(true);
        }
    }
    
    /**
     * We don't generate headers
     * @see org.kuali.kfs.sys.document.web.TableJoiningWithHeader#createHeaderLabel()
     */
    public HeaderLabel createHeaderLabel() {
        return null;
    }
    
    /**
     * This isn't hidden
     * @see org.kuali.kfs.sys.document.web.TableJoiningWithHeader#isHidden()
     */
    public boolean isHidden() {
        return false;
    }
    
    /**
     * This renderable element...it ain't got no single name! 
     * @see org.kuali.kfs.sys.document.web.TableJoining#getName()
     */
    public String getName() {
        return null;
    }
    
    /**
     * Request two rows - one for the header, one for the field
     * @see org.kuali.kfs.sys.document.web.TableJoining#getRequestedRowCount()
     */
    public int getRequestedRowCount() {
        return 2;
    }
    
    /**
     * @see org.kuali.kfs.sys.document.web.TableJoining#joinRow(org.kuali.kfs.sys.document.web.AccountingLineTableRow, org.kuali.kfs.sys.document.web.AccountingLineTableRow)
     */
    public void joinRow(AccountingLineTableRow headerLabelRow, AccountingLineTableRow row) {
        if (debitAmountField != null) {
            headerLabelRow.addCell(createHeaderCellForField(true));
            row.addCell(createCellForField(debitAmountField, debitFieldDefinition, true));
        }
        if (creditAmountField != null) {
            headerLabelRow.addCell(createHeaderCellForField(false));
            row.addCell(createCellForField(creditAmountField, creditFieldDefinition, false));
        }
    }
    
    /**
     * @see org.kuali.kfs.sys.document.web.TableJoining#joinTable(java.util.List)
     */
    public void joinTable(List<AccountingLineTableRow> rows) {
        final int remainingRowCount = rows.size() - 1;
        
        if (debitAmountField != null) {
            rows.get(0).addCell(createHeaderCellForField(true));
            
            AccountingLineTableCell currentCell = createCellForField(debitAmountField, debitFieldDefinition, true);
            currentCell.setRowSpan(remainingRowCount);
            rows.get(1).addCell(currentCell);
        }
        if (creditAmountField != null) {
            rows.get(0).addCell(createHeaderCellForField(false));
            
            AccountingLineTableCell baseCell = createCellForField(creditAmountField, creditFieldDefinition, false);
            baseCell.setRowSpan(remainingRowCount);
            rows.get(1).addCell(baseCell);
        }
    }
    
    /**
     * Creates a table cell with a renderable field inside
     * @param field the field to create a cell for
     * @return a cell that wraps the given field 
     * 
     * KRAD Conversion: Customization of the fields - No use of data dictionary
     */
    protected AccountingLineTableCell createCellForField(Field field, AccountingLineViewFieldDefinition definition, boolean isDebit) {
        AccountingLineTableCell cell = new AccountingLineTableCell();
        AccountingLineViewDebitCreditAmountField renderableField = new AccountingLineViewDebitCreditAmountField(field, definition, isDebit, (isDebit ? this.definition.getNewLineDebitAmountProperty() : this.definition.getNewLineCreditAmountProperty()), this.definition.getVoucherLineHelperProperty());
        cell.addRenderableElement(renderableField);
        return cell;
    }
    
    /**
     * Creates a header cell for the given field
     * @param field the field to create a header cell for
     * @return a header cell
     */
    protected AccountingLineTableCell createHeaderCellForField(boolean isDebit) {
        AccountingLineTableCell headerCell = new AccountingLineTableCell();
        headerCell.setRendersAsHeader(true);
        final String propertyName = isDebit ? getDebitPropertyName() : getCreditPropertyName();
        final String label = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(propertyName); 
        headerCell.addRenderableElement(new LiteralHeaderLabel(label));
        return headerCell;
    }
    
    /**
     * @return the property name for debit labels
     */
    protected String getDebitPropertyName() {
        return "label.document.journalVoucher.accountingLine.debit";
    }
    
    /**
     * @return the property name for credit labels
     */
    protected String getCreditPropertyName() {
        return "label.document.journalVoucher.accountingLine.credit";
    }
    
    /**
     * @see org.kuali.kfs.sys.document.web.TableJoining#performFieldTransformations(java.util.List, org.kuali.kfs.sys.businessobject.AccountingLine, java.util.Map, java.util.Map)
     */
    public void performFieldTransformations(List<AccountingLineFieldRenderingTransformation> fieldTransformations, AccountingLine accountingLine, Map unconvertedValues) {
        for (AccountingLineFieldRenderingTransformation fieldTransformation : fieldTransformations) {
            fieldTransformation.transformField(accountingLine, getDebitAmountField(), getDebitFieldDefinition(), unconvertedValues);
            fieldTransformation.transformField(accountingLine, getCreditAmountField(), getCreditFieldDefinition(), unconvertedValues);
        }
    }
    
    /**
     * @see org.kuali.kfs.sys.document.web.TableJoining#readOnlyizeReadOnlyBlocks(java.util.Set)
     */
    public void readOnlyizeReadOnlyBlocks(Set<String> readOnlyBlocks) {
        if (readOnlyBlocks.contains(KFSPropertyConstants.AMOUNT)) {
            if (debitAmountField != null) {
                debitAmountField.setReadOnly(true);
            }
            if (creditAmountField != null) {
                creditAmountField.setReadOnly(true);
            }
        }
    }
    
    /**
     * Does nothing - we don't have action blocks, like, ever
     * @see org.kuali.kfs.sys.document.web.TableJoining#removeAllActionBlocks()
     */
    public void removeAllActionBlocks() {}
    
    /**
     * @see org.kuali.kfs.sys.document.web.TableJoining#removeUnviewableBlocks(java.util.Set)
     */
    public void removeUnviewableBlocks(Set<String> unviewableBlocks) {
        if (unviewableBlocks.contains(KFSPropertyConstants.AMOUNT)) {
            if (debitAmountField != null) {
                debitAmountField = null;
            }
            if (creditAmountField != null) {
                creditAmountField = null;
            }
        }
    }
    
    /**
     * Gets the creditAmountField attribute. 
     * @return Returns the creditAmountField.
     * 
     * KRAD Conversion: getting the field value - No use of data dictionary
     * 
     */
    public Field getCreditAmountField() {
        return creditAmountField;
    }
    /**
     * Sets the creditAmountField attribute value.
     * @param creditAmountField The creditAmountField to set.
     * 
     * KRAD Conversion: setting up the value of the fields - No use of data dictionary
     */
    public void setCreditAmountField(Field creditAmountField) {
        this.creditAmountField = creditAmountField;
    }
    /**
     * Gets the creditFieldDefinition attribute. 
     * @return Returns the creditFieldDefinition.
     */
    public AccountingLineViewFieldDefinition getCreditFieldDefinition() {
        return creditFieldDefinition;
    }
    /**
     * Sets the creditFieldDefinition attribute value.
     * @param creditFieldDefinition The creditFieldDefinition to set.
     */
    public void setCreditFieldDefinition(AccountingLineViewFieldDefinition creditFieldDefinition) {
        this.creditFieldDefinition = creditFieldDefinition;
    }
    /**
     * Gets the debitAmountField attribute. 
     * @return Returns the debitAmountField.
     */
    public Field getDebitAmountField() {
        return debitAmountField;
    }
    /**
     * Sets the debitAmountField attribute value.
     * @param debitAmountField The debitAmountField to set.
     */
    public void setDebitAmountField(Field debitAmountField) {
        this.debitAmountField = debitAmountField;
    }
    /**
     * Gets the debitFieldDefinition attribute. 
     * @return Returns the debitFieldDefinition.
     */
    public AccountingLineViewFieldDefinition getDebitFieldDefinition() {
        return debitFieldDefinition;
    }
    /**
     * Sets the debitFieldDefinition attribute value.
     * @param debitFieldDefinition The debitFieldDefinition to set.
     */
    public void setDebitFieldDefinition(AccountingLineViewFieldDefinition debitFieldDefinition) {
        this.debitFieldDefinition = debitFieldDefinition;
    }
    /**
     * Gets the definition attribute. 
     * @return Returns the definition.
     */
    public AccountingLineViewDebitCreditAmountFieldDefinition getDefinition() {
        return definition;
    }
    /**
     * Sets the definition attribute value.
     * @param definition The definition to set.
     */
    public void setDefinition(AccountingLineViewDebitCreditAmountFieldDefinition definition) {
        this.definition = definition;
    }

    /**
     * @see org.kuali.kfs.sys.document.web.TableJoining#setEditableBlocks(java.util.Set)
     */
    public void setEditableBlocks(Set<String> editableBlocks) {
        if (editableBlocks.contains(KFSPropertyConstants.AMOUNT)) {
            if (debitAmountField != null) {
                debitAmountField.setReadOnly(false);
            }
            if (creditAmountField != null) {
                creditAmountField.setReadOnly(false);
            }
        }
    }

    /**
     * @see org.kuali.kfs.sys.document.web.ReadOnlyable#setEditable()
     */
    public void setEditable() {
        if (debitAmountField != null) {
            debitAmountField.setReadOnly(false);
        }
        
        if (creditAmountField != null) {
            creditAmountField.setReadOnly(false);
        }
    }
}
