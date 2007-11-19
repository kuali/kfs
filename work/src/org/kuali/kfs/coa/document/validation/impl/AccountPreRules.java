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

import java.sql.Timestamp;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.bo.PostalZipCode;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.SubFundGroup;
import org.kuali.module.chart.service.AccountService;

/**
 * PreRules checks for the Account that needs to occur while still in the Struts processing. This includes defaults, confirmations,
 * etc.
 */
public class AccountPreRules extends MaintenancePreRulesBase {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountPreRules.class);

    private static final String DEFAULT_STATE_CODE = "Account.Defaults.StateCode";
    private static final String DEFAULT_ACCOUNT_TYPE_CODE = "Account.Defaults.AccountType";

    private KualiConfigurationService configService;
    private AccountService accountService;
    private Account newAccount;
    private Account copyAccount;

    private static final String GENERAL_FUND_CD = "GF";
    private static final String RESTRICTED_FUND_CD = "RF";
    private static final String ENDOWMENT_FUND_CD = "EN";
    private static final String PLANT_FUND_CD = "PF";

    private static final String RESTRICTED_CD_RESTRICTED = "R";
    private static final String RESTRICTED_CD_UNRESTRICTED = "U";
    private static final String RESTRICTED_CD_TEMPORARILY_RESTRICTED = "T";
    private static final String RESTRICTED_CD_NOT_APPLICABLE = "N";


    public AccountPreRules() {
        accountService = SpringContext.getBean(AccountService.class);
        configService = SpringContext.getBean(KualiConfigurationService.class);
    }

    /**
     * Executes the following pre rules
     * <ul>
     * <li>{@link AccountPreRules#checkForContinuationAccount(String, String, String, String)}</li>
     * <li>{@link AccountPreRules#checkForDefaultSubFundGroupStatus()}</li>
     * <li>{@link AccountPreRules#newAccountDefaults(MaintenanceDocument)}</li>
     * <li>{@link AccountPreRules#setStateFromZip}</li>
     * </ul>
     * This does not fail on rule failures
     * @see org.kuali.module.chart.rules.MaintenancePreRulesBase#doCustomPreRules(org.kuali.core.document.MaintenanceDocument)
     */
    protected boolean doCustomPreRules(MaintenanceDocument document) {
        setupConvenienceObjects(document);
        checkForContinuationAccounts(); // run this first to avoid side effects
        checkForDefaultSubFundGroupStatus();

        LOG.debug("done with continuation account, proceeeding with remaining pre rules");

        newAccountDefaults(document);
        setStateFromZip(document);

        return true;
    }

    /**
     * This method sets a default restricted status on an account if and only if the status code in SubFundGroup has been set and
     * the user answers in the affirmative that they definitely want to use this SubFundGroup.
     */
    private void checkForDefaultSubFundGroupStatus() {
        String restrictedStatusCode = "";

        // if subFundGroupCode was not entered, then we have nothing
        // to do here, so exit
        if (ObjectUtils.isNull(copyAccount.getSubFundGroup()) || StringUtils.isBlank(copyAccount.getSubFundGroupCode())) {
            return;
        }
        SubFundGroup subFundGroup = copyAccount.getSubFundGroup();

        // KULCOA-1112 : if the sub fund group has a restriction code, override whatever the user selected
        if (StringUtils.isNotBlank(subFundGroup.getAccountRestrictedStatusCode())) {
            restrictedStatusCode = subFundGroup.getAccountRestrictedStatusCode().trim();
            String subFundGroupCd = subFundGroup.getSubFundGroupCode();
            newAccount.setAccountRestrictedStatusCode(restrictedStatusCode);
        }

    }

    /**
     * This method checks for continuation accounts and presents the user with a question regarding their use on this account.
     */
    private void checkForContinuationAccounts() {
        LOG.debug("entering checkForContinuationAccounts()");

        if (StringUtils.isNotBlank(newAccount.getReportsToAccountNumber())) {
            Account account = checkForContinuationAccount("Fringe Benefit Account", newAccount.getReportsToChartOfAccountsCode(), newAccount.getReportsToAccountNumber(), "");
            if (ObjectUtils.isNotNull(account)) { // override old user inputs
                newAccount.setReportsToAccountNumber(account.getAccountNumber());
                newAccount.setReportsToChartOfAccountsCode(account.getChartOfAccountsCode());
            }
        }

        if (StringUtils.isNotBlank(newAccount.getEndowmentIncomeAccountNumber())) {
            Account account = checkForContinuationAccount("Endowment Account", newAccount.getEndowmentIncomeAcctFinCoaCd(), newAccount.getEndowmentIncomeAccountNumber(), "");
            if (ObjectUtils.isNotNull(account)) { // override old user inputs
                newAccount.setEndowmentIncomeAccountNumber(account.getAccountNumber());
                newAccount.setEndowmentIncomeAcctFinCoaCd(account.getChartOfAccountsCode());
            }
        }

        if (StringUtils.isNotBlank(newAccount.getIncomeStreamAccountNumber())) {
            Account account = checkForContinuationAccount("Income Stream Account", newAccount.getIncomeStreamFinancialCoaCode(), newAccount.getIncomeStreamAccountNumber(), "");
            if (ObjectUtils.isNotNull(account)) { // override old user inputs
                newAccount.setIncomeStreamAccountNumber(account.getAccountNumber());
                newAccount.setIncomeStreamFinancialCoaCode(account.getChartOfAccountsCode());
            }
        }

        if (StringUtils.isNotBlank(newAccount.getContractControlAccountNumber())) {
            Account account = checkForContinuationAccount("Contract Control Account", newAccount.getContractControlFinCoaCode(), newAccount.getContractControlAccountNumber(), "");
            if (ObjectUtils.isNotNull(account)) { // override old user inputs
                newAccount.setContractControlAccountNumber(account.getAccountNumber());
                newAccount.setContractControlFinCoaCode(account.getChartOfAccountsCode());
            }
        }

        if (StringUtils.isNotBlank(newAccount.getIndirectCostRecoveryAcctNbr())) {
            Account account = checkForContinuationAccount("Indirect Cost Recovery Account", newAccount.getIndirectCostRcvyFinCoaCode(), newAccount.getIndirectCostRecoveryAcctNbr(), "");
            if (ObjectUtils.isNotNull(account)) { // override old user inputs
                newAccount.setIndirectCostRecoveryAcctNbr(account.getAccountNumber());
                newAccount.setIndirectCostRcvyFinCoaCode(account.getChartOfAccountsCode());
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
        newAccount = (Account) document.getNewMaintainableObject().getBusinessObject();
        copyAccount = (Account) ObjectUtils.deepCopy(newAccount);
        copyAccount.refresh();
    }

    /**
     * This method sets up some defaults for new Account
     * 
     * @param maintenanceDocument
     */
    private void newAccountDefaults(MaintenanceDocument maintenanceDocument) {


        /*
         * GlobalVariables.getErrorMap().put("document.newMaintainableObject.accountEffectiveDate" ,
         * "error.document.accountMaintenance.emptyAccountEffectiveDate", "Account Effective Date");
         */

        // TODO: this is not needed any more, is in maintdoc xml defaults
        Timestamp ts = maintenanceDocument.getDocumentHeader().getWorkflowDocument().getCreateDate();
        // Set nano as zero, to prevent an error related on maximum character numbers.
        ts.setNanos(0);
        if (ts != null) {
            // On new Accounts AccountCreateDate is defaulted to the doc creation date
            if (newAccount.getAccountCreateDate() == null) {
                newAccount.setAccountCreateDate(ts);
            }
            // On new Accounts acct_effect_date is defaulted to the doc creation date
            if (copyAccount.getAccountEffectiveDate() == null) {
                newAccount.setAccountEffectiveDate(ts);
            }
        }
    }

    /**
     * This method lookups state and city from populated zip, set the values on the form
     * 
     * @param maintenanceDocument
     */
    private void setStateFromZip(MaintenanceDocument maintenanceDocument) {

        // acct_zip_cd, acct_state_cd, acct_city_nm all are populated by looking up
        // the zip code and getting the state and city from that
        if (!StringUtils.isBlank(copyAccount.getAccountZipCode())) {

            HashMap primaryKeys = new HashMap();
            primaryKeys.put("postalZipCode", copyAccount.getAccountZipCode());
            PostalZipCode zip = (PostalZipCode) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(PostalZipCode.class, primaryKeys);

            // If user enters a valid zip code, override city name and state code entered by user
            if (ObjectUtils.isNotNull(zip)) { // override old user inputs
                newAccount.setAccountCityName(zip.getPostalCityName());
                newAccount.setAccountStateCode(zip.getPostalStateCode());
            }
        }
    }


}
