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
