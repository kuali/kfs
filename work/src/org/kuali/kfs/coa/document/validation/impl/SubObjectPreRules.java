/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.coa.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.SubObjectCode;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * PreRules checks for the {@link SubObjCd} that needs to occur while still in the Struts processing. This includes defaults, confirmations,
 * etc.
 */
public class SubObjectPreRules extends MaintenancePreRulesBase {
    protected SubObjectCode newSubObjectCode;


    public SubObjectPreRules() {

    }
    
    /**
     * Executes the following pre rules
     * <ul>
     * <li>{@link SubObjectPreRules#checkForContinuationAccounts()}</li>
     * </ul>
     * This does not fail on rule failures
     * @see org.kuali.kfs.coa.document.validation.impl.MaintenancePreRulesBase#doCustomPreRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    protected boolean doCustomPreRules(MaintenanceDocument document) {

        setupConvenienceObjects(document);
        checkForContinuationAccounts(); // run this first to avoid side effects

        LOG.debug("done with continuation account, proceeeding with remaining pre rules");


        return true;
    }

    /**
     * This method checks for continuation accounts and presents the user with a question regarding their use on this account.
     * 
     */
    protected void checkForContinuationAccounts() {
        LOG.debug("entering checkForContinuationAccounts()");

        if (StringUtils.isNotBlank(newSubObjectCode.getAccountNumber())) {
            Account account = checkForContinuationAccount("Account Number", newSubObjectCode.getChartOfAccountsCode(), newSubObjectCode.getAccountNumber(), "");
            if (ObjectUtils.isNotNull(account)) { // override old user inputs
                newSubObjectCode.setAccountNumber(account.getAccountNumber());
                newSubObjectCode.setChartOfAccountsCode(account.getChartOfAccountsCode());
            }
        }
    }

    /**
     * This method sets the convenience objects like newSubObjectCode and copySubObjectCode, so you have short and easy handles to the new and
     * old objects contained in the maintenance document. It also calls the BusinessObjectBase.refresh(), which will attempt to load
     * all sub-objects from the DB by their primary keys, if available.
     * @param document
     */
    protected void setupConvenienceObjects(MaintenanceDocument document) {

        // setup newAccount convenience objects, make sure all possible sub-objects are populated
        newSubObjectCode = (SubObjectCode) document.getNewMaintainableObject().getBusinessObject();
    }
}
