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
