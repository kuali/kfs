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