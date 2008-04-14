/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.chart.rules;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.util.ObjectUtils;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.IndirectCostRecoveryExclusionAccount;

/**
 * PreRules checks for the {@link IndirectCostRecoveryExclusionAccount} that needs to occur while still in the Struts processing.
 * This checks for continuation accounts
 */
public class IndirectCostRecoveryExclusionAccountPreRules extends MaintenancePreRulesBase {

    private IndirectCostRecoveryExclusionAccount indirectCostRecoveryExclusionAccount;


    public IndirectCostRecoveryExclusionAccountPreRules() {

    }

    /**
     * This sets up the convenience objects and calls
     * {@link IndirectCostRecoveryExclusionAccountPreRules#checkForContinuationAccounts()}
     * 
     * @see org.kuali.module.chart.rules.MaintenancePreRulesBase#doCustomPreRules(org.kuali.core.document.MaintenanceDocument)
     */
    protected boolean doCustomPreRules(MaintenanceDocument document) {
        setupConvenienceObjects(document);
        checkForContinuationAccounts(); // run this first to avoid side effects

        LOG.debug("done with continuation account, proceeeding with remaining pre rules");


        return true;
    }

    /**
     * This method checks for continuation accounts and presents the user with a question regarding their use on this account.
     */
    private void checkForContinuationAccounts() {
        LOG.debug("entering checkForContinuationAccounts()");

        if (StringUtils.isNotBlank(indirectCostRecoveryExclusionAccount.getAccountNumber())) {
            Account account = checkForContinuationAccount("Account Number", indirectCostRecoveryExclusionAccount.getChartOfAccountsCode(), indirectCostRecoveryExclusionAccount.getAccountNumber(), "");
            if (ObjectUtils.isNotNull(account)) { // override old user inputs
                indirectCostRecoveryExclusionAccount.setAccountNumber(account.getAccountNumber());
                indirectCostRecoveryExclusionAccount.setChartOfAccountsCode(account.getChartOfAccountsCode());
            }
        }
    }

    /**
     * This method sets the convenience objects like newAccount and oldAccount, so you have short and easy handles to the new and
     * old objects contained in the maintenance document. It also calls the BusinessObjectBase.refresh(), which will attempt to load
     * all sub-objects from the DB by their primary keys, if available.
     * 
     * @param document - the maintenanceDocument being evaluated
     */
    private void setupConvenienceObjects(MaintenanceDocument document) {

        // setup newAccount convenience objects, make sure all possible sub-objects are populated
        indirectCostRecoveryExclusionAccount = (IndirectCostRecoveryExclusionAccount) document.getNewMaintainableObject().getBusinessObject();
    }
}
