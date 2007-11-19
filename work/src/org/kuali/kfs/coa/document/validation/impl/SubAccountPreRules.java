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
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.document.authorization.MaintenanceDocumentAuthorizations;
import org.kuali.core.document.authorization.MaintenanceDocumentAuthorizer;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.module.chart.bo.A21SubAccount;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.SubAccount;

/**
 * PreRules checks for the {@link SubAccount} that needs to occur while still in the Struts processing. This includes defaults, confirmations,
 * etc.
 */
public class SubAccountPreRules extends MaintenancePreRulesBase {

    private SubAccount newSubAccount;

    // private SubAccount copyAccount;

    public SubAccountPreRules() {

    }

    /**
     * This checks to see if a continuation account is necessary and then copies the ICR data from the Account 
     * associated with this SubAccount (if necessary)
     * @see org.kuali.module.chart.rules.MaintenancePreRulesBase#doCustomPreRules(org.kuali.core.document.MaintenanceDocument)
     */
    protected boolean doCustomPreRules(MaintenanceDocument document) {
        setupConvenienceObjects(document);
        checkForContinuationAccounts(document.getNewMaintainableObject().getMaintenanceAction()); // run this first to avoid side
                                                                                                    // effects

        LOG.debug("done with continuation account, proceeeding with remaining pre rules");

        copyICRFromAccount(document);

        return true;
    }

    /**
     * 
     * This looks for the SubAccount's account number and then sets the values to the continuation account value if it exists
     * @param maintenanceAction
     */
    private void checkForContinuationAccounts(String maintenanceAction) {
        LOG.debug("entering checkForContinuationAccounts()");

        /*
         * KULCOA-734 - The check for continuation account for main Account Number on sub-account has been modified to only occur
         * for a New and Copy Action. This cannot happen on an Edit as the primary key will change.
         */
        if (KFSConstants.MAINTENANCE_NEW_ACTION.equals(maintenanceAction) || KFSConstants.MAINTENANCE_COPY_ACTION.equals(maintenanceAction)) {

            if (StringUtils.isNotBlank(newSubAccount.getAccountNumber())) {
                Account account = checkForContinuationAccount("Account Number", newSubAccount.getChartOfAccountsCode(), newSubAccount.getAccountNumber(), "");
                if (ObjectUtils.isNotNull(account)) { // override old user inputs
                    newSubAccount.setAccountNumber(account.getAccountNumber());
                    newSubAccount.setChartOfAccountsCode(account.getChartOfAccountsCode());
                }
            }
        }
    }

    /**
     * This method sets the convenience objects like newSubAccount, so you have short and easy handles to the new and
     * old objects contained in the maintenance document. It also calls the BusinessObjectBase.refresh(), which will attempt to load
     * all sub-objects from the DB by their primary keys, if available.
     * @param document
     */
    private void setupConvenienceObjects(MaintenanceDocument document) {

        // setup newAccount convenience objects, make sure all possible sub-objects are populated
        newSubAccount = (SubAccount) document.getNewMaintainableObject().getBusinessObject();
        // copyAccount = (SubAccount) ObjectUtils.deepCopy(newAccount);
        // copyAccount.refresh();
    }

    /**
     * 
     * This copies the Indirect Cost Rate (ICR) from the account if the SubAccount is a specific type - determined 
     * as "EX" from {@link SubAccountRule#CG_A21_TYPE_ICR}
     * <p>
     * If it is "EX" it will then copy over the ICR information from the Account specified for this SubAccount
     * @param document
     */
    private void copyICRFromAccount(MaintenanceDocument document) {
        UniversalUser user = GlobalVariables.getUserSession().getUniversalUser();

        // get the correct documentAuthorizer for this document
        MaintenanceDocumentAuthorizer documentAuthorizer = (MaintenanceDocumentAuthorizer) getDocumentAuthorizationService().getDocumentAuthorizer(document);

        // get a new instance of MaintenanceDocumentAuthorizations for this context
        MaintenanceDocumentAuthorizations auths = documentAuthorizer.getFieldAuthorizations(document, user);

        // don't need to copy if the user does not have the authority to edit the fields
        if (!auths.getAuthFieldAuthorization("a21SubAccount.financialIcrSeriesIdentifier").isReadOnly()) {
            // only need to do this of the account sub type is EX
            A21SubAccount a21SubAccount = newSubAccount.getA21SubAccount();
            Account account = newSubAccount.getAccount();
            if (SubAccountRule.CG_A21_TYPE_ICR.equals(a21SubAccount.getSubAccountTypeCode())) {
                if (account == null || StringUtils.isBlank(account.getAccountNumber())) {
                    account = getAccountService().getByPrimaryId(newSubAccount.getChartOfAccountsCode(), newSubAccount.getAccountNumber());
                    if (ObjectUtils.isNotNull(account)) {
                        if (StringUtils.isBlank(a21SubAccount.getIndirectCostRecoveryAccountNumber())) {
                            a21SubAccount.setIndirectCostRecoveryAccountNumber(account.getIndirectCostRecoveryAcctNbr());
                            a21SubAccount.setIndirectCostRecoveryChartOfAccountsCode(account.getIndirectCostRcvyFinCoaCode());
                        }
                        if (StringUtils.isBlank(a21SubAccount.getFinancialIcrSeriesIdentifier())) {
                            a21SubAccount.setFinancialIcrSeriesIdentifier(account.getFinancialIcrSeriesIdentifier());
                            a21SubAccount.setOffCampusCode(account.isAccountOffCampusIndicator());
                        }
                        if (StringUtils.isBlank(a21SubAccount.getIndirectCostRecoveryTypeCode())) {
                            a21SubAccount.setIndirectCostRecoveryTypeCode(account.getAcctIndirectCostRcvyTypeCd());
                        }
                    }
                }
            }
        }
    }
}
