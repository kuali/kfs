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

import static org.kuali.kfs.sys.businessobject.AccountingLineOverride.CODE.EXPIRED_ACCOUNT;
import static org.kuali.kfs.sys.businessobject.AccountingLineOverride.CODE.EXPIRED_ACCOUNT_AND_NON_FRINGE_ACCOUNT_USED;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * determine whether the given accounting line has already been in the given document
 * 
 * @param accountingDocument the given document
 * @param accountingLine the given accounting line
 * @return true if the given accounting line has already been in the given document; otherwise, false
 */
public class LaborExpenseTransferExpiredAccountBeUsedValidation extends GenericValidation {
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
               
        // determine if an expired account can be used to accept amount transfer
        boolean canExpiredAccountBeUsed = canExpiredAccountBeUsed(accountingLine);
        
        // not allow the duplicate source accounting line in the document
        if (!canExpiredAccountBeUsed) {
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.ACCOUNT, KFSKeyConstants.ERROR_ACCOUNT_EXPIRED );
            return false;
        }
        
        return result;
    }

    /**
     * determine whether the expired account in the target accounting line can be used.
     * 
     * @param accountingDocument the given accounting line
     * @return true if the expired account in the target accounting line can be used; otherwise, false
     */
    protected boolean canExpiredAccountBeUsed(AccountingLine accountingLine) {

        Account account = accountingLine.getAccount();
        if (ObjectUtils.isNotNull(account) && account.isExpired() && !account.isClosed()) {
            String overrideCode = accountingLine.getOverrideCode();
            boolean canExpiredAccountUsed = EXPIRED_ACCOUNT.equals(overrideCode);
            canExpiredAccountUsed = canExpiredAccountUsed || EXPIRED_ACCOUNT_AND_NON_FRINGE_ACCOUNT_USED.equals(overrideCode);

            if (!canExpiredAccountUsed) {
                return false;
            }
        }
        return true;
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
