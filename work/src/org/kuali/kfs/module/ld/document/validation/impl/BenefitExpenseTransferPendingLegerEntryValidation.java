/*
 * Copyright 2008-2009 The Kuali Foundation
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.LaborKeyConstants;
import org.kuali.kfs.module.ld.LaborPropertyConstants;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferAccountingLine;
import org.kuali.kfs.module.ld.document.LaborExpenseTransferDocumentBase;
import org.kuali.kfs.module.ld.service.LaborLedgerPendingEntryService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.search.SearchOperator;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Validates that an accounting document does not have any pending
 * labor ledger entries with the same emplID, periodCode, accountNumber, objectCode 
 */
public class BenefitExpenseTransferPendingLegerEntryValidation extends GenericValidation {
    private Document documentForValidation;
    
    /**
     * Validates that the accounting lines in the accounting document does not have 
     * any pending labor ledger entries with the same account, sub-account, object, sub-object, fiscal year, fiscal period  
     * @see org.kuali.kfs.validation.Validation#validate(java.lang.Object[])
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean result = true;
        
        Document documentForValidation = getDocumentForValidation();
        AccountingDocument accountingDocument = (AccountingDocument) documentForValidation;
        
        result = !hasPendingLedgerEntry(accountingDocument);
        return result ;    
    }

    /**
     * Checks whether amounts by object codes are unchanged
     * 
     * @param accountingDocumentForValidation The accounting document from which the Pending Entries are checked
     * @return True if the given accounting documents Pending Entries do not conflict with current PEs, false otherwise.
     */ 
    protected boolean hasPendingLedgerEntry(AccountingDocument accountingDocument) {
        boolean entriesExist = false ;
        
        LaborExpenseTransferDocumentBase expenseTransferDocument = (LaborExpenseTransferDocumentBase) accountingDocument;
        List<ExpenseTransferAccountingLine> sourceAccountingLines = expenseTransferDocument.getSourceAccountingLines();

        Map<String, String> fieldValues = new HashMap<String, String>();
        for (ExpenseTransferAccountingLine sourceAccountingLine : sourceAccountingLines) {
            //account, sub-account, object and sub-object and the fiscal year and fiscal period 
            
            String subAccountNumber = sourceAccountingLine.getSubAccountNumber();
            String accountNumber = sourceAccountingLine.getAccountNumber();
            String objectCode = sourceAccountingLine.getFinancialObjectCode();
            String subObjectCode = sourceAccountingLine.getFinancialSubObjectCode();
            Integer fiscalYear = sourceAccountingLine.getPayrollEndDateFiscalYear();
            String periodCode = sourceAccountingLine.getPayrollEndDateFiscalPeriodCode();
            String documentNumber = accountingDocument.getDocumentNumber();

            fieldValues.put(LaborPropertyConstants.PAYROLL_END_DATE_FISCAL_PERIOD_CODE, periodCode);
            fieldValues.put(LaborPropertyConstants.PAYROLL_END_DATE_FISCAL_YEAR, fiscalYear+"");
            fieldValues.put(KFSPropertyConstants.ACCOUNT_NUMBER, accountNumber);
            subAccountNumber = StringUtils.isBlank(subAccountNumber) ? KFSConstants.getDashSubAccountNumber() : subAccountNumber;
            
            subObjectCode = StringUtils.isBlank(subObjectCode) ? KFSConstants.getDashFinancialSubObjectCode() : subObjectCode;
            fieldValues.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, subAccountNumber);
            fieldValues.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, objectCode);
            fieldValues.put(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, subObjectCode);
            fieldValues.put(KFSPropertyConstants.DOCUMENT_NUMBER, SearchOperator.NOT.op()  + documentNumber);
            
            if (SpringContext.getBean(LaborLedgerPendingEntryService.class).hasPendingLaborLedgerEntry(fieldValues)) {
                GlobalVariables.getMessageMap().putError(LaborConstants.EMPLOYEE_LOOKUP_ERRORS, LaborKeyConstants.PENDING_BENEFIT_TRANSFER_ERROR, accountNumber, objectCode, periodCode, fiscalYear+"");
                return true;
            }                        
        }
        return entriesExist ;
    }

    /**
     * Gets the documentForValidation attribute. 
     * @return Returns the documentForValidation.
     */
    public Document getDocumentForValidation() {
        return documentForValidation;
    }

    /**
     * Sets the accountingDocumentForValidation attribute value.
     * @param documentForValidation The documentForValidation to set.
     */
    public void setDocumentForValidation(Document documentForValidation) {
        this.documentForValidation = documentForValidation;
    }    
}
