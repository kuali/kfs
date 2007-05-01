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
import org.kuali.kfs.KFSConstants;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.IcrAutomatedEntry;

/**
 * This class...
 * 
 * 
 */
public class IcrAutomatedEntryPreRules extends MaintenancePreRulesBase {


    private IcrAutomatedEntry newAccount;
    private IcrAutomatedEntry copyAccount;


    public IcrAutomatedEntryPreRules() {

    }

    protected boolean doCustomPreRules(MaintenanceDocument document) {
        setupConvenienceObjects(document);
        checkForContinuationAccounts(); // run this first to avoid side effects
        
        LOG.debug("done with continuation account, proceeeding with remaining pre rules");

        setSubAccountToDashesIfBlank();
        setSubObjectToDashesIfBlank();
        
        return true;
    }

    private void checkForContinuationAccounts() {
        LOG.debug("entering checkForContinuationAccounts()");

        if (StringUtils.isNotBlank(newAccount.getAccountNumber())) {
            Account account = checkForContinuationAccount("Account Number", newAccount.getChartOfAccountsCode(), newAccount.getAccountNumber(), "");
            if (ObjectUtils.isNotNull(account)) { // override old user inputs
                newAccount.setAccountNumber(account.getAccountNumber());
                newAccount.setChartOfAccountsCode(account.getChartOfAccountsCode());
            }
        }
    }

    protected void setSubAccountToDashesIfBlank() {
        String newSubAccount = newAccount.getSubAccountNumber();
        if (StringUtils.isBlank(newSubAccount)) {
            newAccount.setSubAccountNumber(KFSConstants.DASHES_SUB_ACCOUNT_NUMBER);
        }
    }
    
    protected void setSubObjectToDashesIfBlank() {
        String newSubObject = newAccount.getFinancialSubObjectCode();
        if (StringUtils.isBlank(newSubObject)) {
            newAccount.setFinancialSubObjectCode(KFSConstants.DASHES_SUB_OBJECT_CODE);
        }
    }
    
    private void setupConvenienceObjects(MaintenanceDocument document) {

        // setup newAccount convenience objects, make sure all possible sub-objects are populated
        newAccount = (IcrAutomatedEntry) document.getNewMaintainableObject().getBusinessObject();
        copyAccount = (IcrAutomatedEntry) ObjectUtils.deepCopy(newAccount);
        copyAccount.refresh();
    }
}
