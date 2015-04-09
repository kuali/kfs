/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.ld.document.validation.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.LaborKeyConstants;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferSourceAccountingLine;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferTargetAccountingLine;
import org.kuali.kfs.module.ld.document.LaborExpenseTransferDocumentBase;
import org.kuali.kfs.module.ld.document.SalaryExpenseTransferDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Validates that an accounting document's accounting lines have the same Employee ID 
 */
public class SalaryExpenseTransferAccountingLinesSameEmployeeValidation extends GenericValidation {
    private Document documentForValidation;
    
    /**
     * Validates that the accounting lines in the accounting document have the same employee id 
     * <strong>Expects an accounting document as the first a parameter</strong>
     * @see org.kuali.kfs.validation.Validation#validate(java.lang.Object[])
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean result = true;
        
        Document documentForValidation = getDocumentForValidation();
        
        SalaryExpenseTransferDocument salaryExpenseTransferDocument = (SalaryExpenseTransferDocument) documentForValidation;
                
        String employeeID = salaryExpenseTransferDocument.getEmplid() ;
        
        if (StringUtils.isBlank(employeeID)) {
            GlobalVariables.getMessageMap().putError(LaborConstants.DOCUMENT_EMPLOYEE_ID_ERRORS, LaborKeyConstants.MISSING_EMPLOYEE_ID) ;
            result = false ;
        }
        
        // ensure the employee ids in the source accounting lines are same
        AccountingDocument accountingDocument = (AccountingDocument) documentForValidation;
        if (!hasAccountingLinesSameEmployee(accountingDocument)) {
            return false;
        }
        
        return result ;    
    }

    protected boolean hasAccountingLinesSameEmployee(AccountingDocument accountingDocument) {
        
        LaborExpenseTransferDocumentBase expenseTransferDocument = (LaborExpenseTransferDocumentBase) accountingDocument;
        List<ExpenseTransferSourceAccountingLine> sourceAccountingLines = expenseTransferDocument.getSourceAccountingLines();
        List<ExpenseTransferTargetAccountingLine> targetAccountingLines = expenseTransferDocument.getTargetAccountingLines();

        boolean sourceAccountingLinesValidationResult = true;
        boolean targetAccountingLinesValidationResult = true;

        String employeeID = expenseTransferDocument.getEmplid();
        String accountingLineEmplID = null;

        // Source Lines
        for (ExpenseTransferSourceAccountingLine sourceAccountingLine : sourceAccountingLines) {
            accountingLineEmplID = sourceAccountingLine.getEmplid();
            if (accountingLineEmplID == null || !StringUtils.equals(employeeID, accountingLineEmplID)) {
                sourceAccountingLinesValidationResult = false;
                break;
            }
        }

        // Target lines
        for (ExpenseTransferTargetAccountingLine targetAccountingLine : targetAccountingLines) {
            accountingLineEmplID = targetAccountingLine.getEmplid();
            if (accountingLineEmplID == null || !StringUtils.equals(employeeID, accountingLineEmplID)) {
                targetAccountingLinesValidationResult = false;
                break;
            }
        }

        if (!sourceAccountingLinesValidationResult) {
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.SOURCE_ACCOUNTING_LINES, LaborKeyConstants.ERROR_EMPLOYEE_ID_NOT_SAME);
        }

        if (!targetAccountingLinesValidationResult) {
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.TARGET_ACCOUNTING_LINES, LaborKeyConstants.ERROR_EMPLOYEE_ID_NOT_SAME_IN_TARGET);
        }

        return (sourceAccountingLinesValidationResult && targetAccountingLinesValidationResult);
    }

    /**
     * Gets the accountingDocumentForValidation attribute. 
     * @return Returns the accountingDocumentForValidation.
     */
    public Document getDocumentForValidation() {
        return documentForValidation;
    }

    /**
     * Sets the accountingDocumentForValidation attribute value.
     * @param accountingDocumentForValidation The accountingDocumentForValidation to set.
     */
    public void setDocumentForValidation(Document documentForValidation) {
        this.documentForValidation = documentForValidation;
    } 
}
