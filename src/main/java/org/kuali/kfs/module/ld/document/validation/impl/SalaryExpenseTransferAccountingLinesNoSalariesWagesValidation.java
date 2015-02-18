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

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.module.ld.LaborKeyConstants;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferAccountingLine;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Validates that the target accounting lines in the accounting document has accounts which allow wages
 */
public class SalaryExpenseTransferAccountingLinesNoSalariesWagesValidation extends GenericValidation {
    private Document documentForValidation;
    

    private AccountingLine accountingLineForValidation;

    /**
     * Validates that a target accounting line has an account with a sub fund that allows wages and salaries
     * <strong>Expects an accounting line as the first a parameter</strong>
     * @see org.kuali.kfs.validation.Validation#validate(java.lang.Object[])
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean result = true;
        AccountingLine accountingLine = getAccountingLineForValidation();
        if (accountingLine.isTargetAccountingLine()) {
            Account account = accountFromLine(accountingLine);
            if (!isValidSubjectFund(account)) {     
                GlobalVariables.getMessageMap().putError(KFSPropertyConstants.TARGET_ACCOUNTING_LINES, LaborKeyConstants.INVALID_SALARY_ACCOUNT_SUB_FUND_ERROR, account.getChartOfAccountsCode(), account.getAccountNumber());
                result = false;
            }
        }
        return result;
    }

    /**
     * Checks whether the given Account has a sub fund which accepts salaries and wages
     * @param Account 
     * @return True if the given accounting line's account sub fund accepts wages, false otherwise.
     */ 
    protected boolean isValidSubjectFund(Account account) {        
        return (account.getSubFundGroup().isSubFundGroupWagesIndicator());
    }

   /**
    * extract Account number from accounting line
    * 
     * @param accountingLine The accounting line the account code will be retrieved from.
    * @return account
    */
    protected Account accountFromLine(AccountingLine accountingLine) {
         
        ExpenseTransferAccountingLine expenseTransferAccountingLine = (ExpenseTransferAccountingLine) accountingLine;
        expenseTransferAccountingLine.refreshReferenceObject(KFSPropertyConstants.LABOR_OBJECT);          
        return (expenseTransferAccountingLine.getAccount());
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
     * @param accountingLineForValidation The accountingLineForValidation to set.
     */
    public void setAccountingLineForValidation(AccountingLine accountingLineForValidation) {
        this.accountingLineForValidation = accountingLineForValidation;
    }
}
