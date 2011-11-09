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
