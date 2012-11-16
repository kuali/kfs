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

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.module.ld.LaborKeyConstants;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferSourceAccountingLine;
import org.kuali.kfs.module.ld.document.LaborExpenseTransferDocumentBase;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Validates that the given accounting line and source lines are the same
 */
public class BenefitExpenseTransferSameAccountValidation extends GenericValidation {
    private Document documentForValidation;
    private AccountingLine accountingLineForValidation;
    
    /**
     * Validates that the given accounting lines in the accounting document have 
     * the same account as the source accounting lines. 
     * <strong>Expects an accounting document as the first a parameter</strong>
     * @see org.kuali.kfs.validation.Validation#validate(java.lang.Object[])
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean result = true;
        
        Document documentForValidation = getDocumentForValidation() ;
        AccountingLine accountingLine = getAccountingLineForValidation();
        
        boolean isTargetLine = accountingLine.isTargetAccountingLine();
        if (!isTargetLine) {
            if (!hasSameAccount(documentForValidation, accountingLine)) {
                GlobalVariables.getMessageMap().putError(KFSPropertyConstants.TARGET_ACCOUNTING_LINES, LaborKeyConstants.ERROR_ACCOUNT_NOT_SAME);
                result = false;
            }
        }
        return result ;    
    }

    /**
     * Determines whether the given accounting line has the same account as the source accounting lines
     * 
     * @param document the given document
     * @param accountingLine the given accounting line
     * @return true if the given accounting line has the same account as the source accounting lines; otherwise, false
     */  
    public boolean hasSameAccount(Document document, AccountingLine accountingLine) {
        LaborExpenseTransferDocumentBase expenseTransferDocument = (LaborExpenseTransferDocumentBase) document;
        List<ExpenseTransferSourceAccountingLine> sourceAccountingLines = expenseTransferDocument.getSourceAccountingLines();

        accountingLine.refreshReferenceObject(KFSPropertyConstants.ACCOUNT);
        
        Account cachedAccount = accountingLine.getAccount();
        for (AccountingLine sourceAccountingLine : sourceAccountingLines) {
            Account account = sourceAccountingLine.getAccount();

            // account number was not retrieved correctly, so the two statements are used to populate the fields manually
            account.setChartOfAccountsCode(sourceAccountingLine.getChartOfAccountsCode());
            account.setAccountNumber(sourceAccountingLine.getAccountNumber());

            if (!account.equals(cachedAccount)) {
                return false;
            }
        }

        return true;
     }

    /**
     * Sets the accountingDocumentForValidation attribute value.
     * @param accountingDocumentForValidation The accountingDocumentForValidation to set.
     */
    public void setDocumentForValidation(Document documentForValidation) {
        this.documentForValidation = documentForValidation;
    }
    
    /**
     * Gets the DocumentForValidation attribute. 
     * @return Returns the documentForValidation.
     */
    public Document getDocumentForValidation() {
        return documentForValidation;
    }

    /**
     * Gets the accountingLineForValidation attribute. 
     * @return Returns the accountingLineForValidation.
     */
    public AccountingLine getAccountingLineForValidation() {
        return accountingLineForValidation;
    }    
    
    /**
     * Sets the accountingLineForValidation attribute value.
     * @param accountingDocumentForValidation The accountingDocumentForValidation to set.
     */
    public void setAccountingLineForValidation(AccountingLine accountingLineForValidation) {
        this.accountingLineForValidation = accountingLineForValidation;
    }
}
