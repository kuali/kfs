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
package org.kuali.kfs.fp.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.fp.businessobject.BudgetAdjustmentAccountingLine;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Validation that checks Budget Adjustment accounting lines to make sure that non-zero adjustments have related income stream accounts.
 */
public class BudgetAdjustmentAccountingLineAccountIncomeStreamValidation extends GenericValidation {
    private BudgetAdjustmentAccountingLine accountingLineForValidation;

    /**
     * Validate that, if current adjustment amount is non zero, account has an associated income stream chart and account
     * @see org.kuali.kfs.sys.document.validation.Validation#validate(org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent)
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean accountNumberAllowed = true;
        if (getAccountingLineForValidation().getCurrentBudgetAdjustmentAmount().isNonZero()) {
            getAccountingLineForValidation().refreshReferenceObject("account");
            
            if (!ObjectUtils.isNull(getAccountingLineForValidation().getAccount())) {
                //KFSMI-4877: if fund group is in system parameter values then income stream account number must exist.
                String fundGroupCode = getAccountingLineForValidation().getAccount().getSubFundGroup().getFundGroupCode();
                String incomeStreamRequiringFundGroupCode = SpringContext.getBean(ParameterService.class).getParameterValueAsString(Account.class, KFSConstants.ChartApcParms.INCOME_STREAM_ACCOUNT_REQUIRING_FUND_GROUPS);
                if (StringUtils.containsIgnoreCase(fundGroupCode, incomeStreamRequiringFundGroupCode)) {
                    if (ObjectUtils.isNull(getAccountingLineForValidation().getAccount().getIncomeStreamAccount())) {
                        GlobalVariables.getMessageMap().putError(KFSPropertyConstants.ACCOUNT_NUMBER, KFSKeyConstants.ERROR_DOCUMENT_BA_NO_INCOME_STREAM_ACCOUNT, getAccountingLineForValidation().getAccountNumber());
                        accountNumberAllowed = false;
                    }
                }
            }
        }
        
        return accountNumberAllowed;
    }

    /**
     * Gets the accountingLineForValidation attribute. 
     * @return Returns the accountingLineForValidation.
     */
    public BudgetAdjustmentAccountingLine getAccountingLineForValidation() {
        return accountingLineForValidation;
    }

    /**
     * Sets the accountingLineForValidation attribute value.
     * @param accountingLineForValidation The accountingLineForValidation to set.
     */
    public void setAccountingLineForValidation(BudgetAdjustmentAccountingLine accountingLineForValidation) {
        this.accountingLineForValidation = accountingLineForValidation;
    }
}
