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

import static org.kuali.kfs.sys.businessobject.AccountingLineOverride.CODE.EXPIRED_ACCOUNT_AND_NON_FRINGE_ACCOUNT_USED;
import static org.kuali.kfs.sys.businessobject.AccountingLineOverride.CODE.NON_FRINGE_ACCOUNT_USED;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.module.ld.LaborKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * verify if the accounts in target accounting lines accept fringe benefits
 * 
 * @param accountingLine the given accounting line
 * @return true if the accounts in the target accounting lines accept fringe benefits; otherwise, false
 */
public class LaborExpenseTransferAccountAcceptFringeBenefitValidation extends GenericValidation {
    private AccountingLine accountingLineForValidation;
    
    /**
     * Validates that an accounting line whether the expired account in the target accounting line 
     * can be used.
     * <strong>Expects an accounting line as the first a parameter</strong>
     * @see org.kuali.kfs.validation.Validation#validate(java.lang.Object[])
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean result = true;
        AccountingLine accountingLine = getAccountingLineForValidation();
          
        // verify if the accounts in target accounting lines accept fringe benefits
        if (!isAccountAcceptFringeBenefit(accountingLine)) {
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.TARGET_ACCOUNTING_LINES, LaborKeyConstants.ERROR_ACCOUNT_NOT_ACCEPT_FRINGES, accountingLine.getAccount().getReportsToChartOfAccountsCode(), accountingLine.getAccount().getReportsToAccountNumber());
            return false;
        }
                        
        return result;
    }

    /**
     * Determines whether the account in the target line accepts fringe benefits.
     * 
     * @param accountingLine the line to check
     * @return true if the accounts in the target accounting lines accept fringe benefits; otherwise, false
     */
    protected boolean isAccountAcceptFringeBenefit(AccountingLine accountingLine) {
        boolean acceptsFringeBenefits = true;

 //       accountingLine.refreshReferenceObject(KFSPropertyConstants.ACCOUNT);
        Account account = accountingLine.getAccount();
        if (ObjectUtils.isNotNull(account) && !account.isAccountsFringesBnftIndicator()) {
            String overrideCode = accountingLine.getOverrideCode();
            boolean canNonFringeAccountUsed = NON_FRINGE_ACCOUNT_USED.equals(overrideCode);
            canNonFringeAccountUsed = canNonFringeAccountUsed || EXPIRED_ACCOUNT_AND_NON_FRINGE_ACCOUNT_USED.equals(overrideCode);

            if (!canNonFringeAccountUsed) {
                acceptsFringeBenefits = false;
            }
        }

        return acceptsFringeBenefits;
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
