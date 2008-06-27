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
package org.kuali.kfs.module.ld.document.validation.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.List;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.module.ld.LaborConstants ;
import org.kuali.kfs.module.ld.LaborKeyConstants; 
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.module.ld.document.SalaryExpenseTransferDocument ;
import org.kuali.kfs.module.ld.document.LaborExpenseTransferDocumentBase ;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferAccountingLine;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferSourceAccountingLine;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferTargetAccountingLine;
import org.kuali.kfs.module.ld.service.LaborLedgerPendingEntryService;
import org.kuali.kfs.module.ld.util.LaborPendingEntryGenerator;

/**
 * Validates that an accounting document does not have any pending
 * labor ledger entries with the same emplID, periodCode, accountNumber, objectCode 
 */
public class SalaryExpenseTransferPendingLegerEntryValidation extends GenericValidation {
    private SalaryExpenseTransferDocument accountingDocumentForValidation;
    
    /**
     * Validates that the accounting lines in the accounting document does not have 
     * any pending labor ledger entries with the same emplID, periodCode, accountNumber, objectCode 
     * <strong>Expects an accounting document as the first a parameter</strong>
     * @see org.kuali.kfs.validation.Validation#validate(java.lang.Object[])
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean result = true;
        SalaryExpenseTransferDocument salaryExpenseTransferDocument = getAccountingDocumentForValidation() ;
        
        if (!hasPendingLedgerEntry(salaryExpenseTransferDocument)) {
            GlobalVariables.getErrorMap().putError(LaborConstants.EMPLOYEE_LOOKUP_ERRORS, LaborKeyConstants.PENDING_SALARY_TRANSFER_ERROR);
            result = false ;
        }
        
        return result ;    
    }

    /**
     * Checks whether amounts by object codes are unchanged
     * 
     * @param accountingDocumentForValidation The accounting document from which the amounts by objects codes are checked
     * @return True if the given accounting documents amounts by object code are unchanged, false otherwise.
     */ 
    private boolean hasPendingLedgerEntry(SalaryExpenseTransferDocument accountingDocument) {
        boolean entriesExist = true ;
        
        LaborExpenseTransferDocumentBase expenseTransferDocument = (LaborExpenseTransferDocumentBase) accountingDocument;
        List<ExpenseTransferAccountingLine> sourceAccountingLines = expenseTransferDocument.getSourceAccountingLines();

        if (sourceAccountingLines.isEmpty())
            return false ;
        
        Map<String, String> fieldValues = new HashMap<String, String>();
        for (ExpenseTransferAccountingLine sourceAccountingLine : sourceAccountingLines)
        {           
            String payPeriodCode = sourceAccountingLine.getPayrollEndDateFiscalPeriodCode();
            String accountNumber = sourceAccountingLine.getAccountNumber();
            String objectCode = sourceAccountingLine.getFinancialObjectCode();
            String emplID = sourceAccountingLine.getEmplid();
            String documentNumber = sourceAccountingLine.getDocumentNumber();
            
            if ((payPeriodCode == null || payPeriodCode.trim().length() == 0) || 
                 (accountNumber == null || accountNumber.trim().length() == 0) ||
                 (objectCode == null || objectCode.trim().length() == 0) ||
                 (emplID == null || emplID.trim().length() == 0) ||
                 (documentNumber == null || documentNumber.trim().length() == 0))
                return false ; 
            
            fieldValues.put(KFSPropertyConstants.PAYROLL_END_DATE_FISCAL_YEAR, payPeriodCode);
            fieldValues.put(KFSPropertyConstants.ACCOUNT_NUMBER, accountNumber);
            fieldValues.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, objectCode);
            fieldValues.put(KFSPropertyConstants.EMPLID, emplID);
            fieldValues.put(KFSPropertyConstants.DOCUMENT_NUMBER, documentNumber);
            
            if(!SpringContext.getBean(LaborLedgerPendingEntryService.class).hasPendingLaborLedgerEntry(fieldValues)) {
               return false ;
            }
            
        }
        return entriesExist ;
    }

    /**
     * Gets the accountingDocumentForValidation attribute. 
     * @return Returns the accountingDocumentForValidation.
     */
    public SalaryExpenseTransferDocument getAccountingDocumentForValidation() {
        return accountingDocumentForValidation;
    }

    /**
     * Sets the accountingDocumentForValidation attribute value.
     * @param accountingDocumentForValidation The accountingDocumentForValidation to set.
     */
    public void setAccountingLineForValidation(SalaryExpenseTransferDocument accountingDocumentForValidation) {
        this.accountingDocumentForValidation = accountingDocumentForValidation;
    }
}
