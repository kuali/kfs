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
 * PreRules checks for the {@link IcrAutomatedEntry} that needs to occur while still in the Struts processing. This includes
 * defaults, confirmations, etc.
 */
public class IcrAutomatedEntryPreRules extends MaintenancePreRulesBase {


    private IcrAutomatedEntry icrAutomatedEntry;


    public IcrAutomatedEntryPreRules() {

    }

    /**
     * Executes the following pre rules
     * <ul>
     * <li>{@link IcrAutomatedEntryPreRules#setSubAccountToDashesIfBlank()}</li>
     * <li>{@link IcrAutomatedEntryPreRules#setSubObjectToDashesIfBlank()}</li>
     * </ul>
     * 
     * @see org.kuali.module.chart.rules.MaintenancePreRulesBase#doCustomPreRules(org.kuali.core.document.MaintenanceDocument)
     */
    protected boolean doCustomPreRules(MaintenanceDocument document) {
        setupConvenienceObjects(document);
        checkForContinuationAccounts(); // run this first to avoid side effects

        LOG.debug("done with continuation account, proceeeding with remaining pre rules");

        setSubAccountToDashesIfBlank();
        setSubObjectToDashesIfBlank();

        return true;
    }

    /**
     * This method checks for continuation accounts and presents the user with a question regarding their use on this account.
     */
    private void checkForContinuationAccounts() {
        LOG.debug("entering checkForContinuationAccounts()");

        if (StringUtils.isNotBlank(icrAutomatedEntry.getAccountNumber())) {
            Account account = checkForContinuationAccount("Account Number", icrAutomatedEntry.getChartOfAccountsCode(), icrAutomatedEntry.getAccountNumber(), "");
            if (ObjectUtils.isNotNull(account)) { // override old user inputs
                icrAutomatedEntry.setAccountNumber(account.getAccountNumber());
                icrAutomatedEntry.setChartOfAccountsCode(account.getChartOfAccountsCode());
            }
        }
    }

    /**
     * This sets the {@link SubAccount} number to padded dashes ("-") if blank
     */
    protected void setSubAccountToDashesIfBlank() {
        String newSubAccount = icrAutomatedEntry.getSubAccountNumber();
        if (StringUtils.isBlank(newSubAccount)) {
            icrAutomatedEntry.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
        }
    }

    /**
     * This sets the {@link org.kuali.module.chart.bo.SubObjCd} code to padded dashes ("-") if blank
     */
    protected void setSubObjectToDashesIfBlank() {
        String newSubObject = icrAutomatedEntry.getFinancialSubObjectCode();
        if (StringUtils.isBlank(newSubObject)) {
            icrAutomatedEntry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        }
    }

    /**
     * This method sets the convenience objects like newAccount and oldAccount, so you have short and easy handles to the new and
     * old objects contained in the maintenance document. It also calls the BusinessObjectBase.refresh(), which will attempt to load
     * all sub-objects from the DB by their primary keys, if available.
     */
    private void setupConvenienceObjects(MaintenanceDocument document) {

        // setup newAccount convenience objects, make sure all possible sub-objects are populated
        icrAutomatedEntry = (IcrAutomatedEntry) document.getNewMaintainableObject().getBusinessObject();
    }
}
