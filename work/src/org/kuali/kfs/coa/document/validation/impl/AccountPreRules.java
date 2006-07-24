/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.chart.rules;

import java.sql.Timestamp;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.core.bo.PostalZipCode;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.SubFundGroup;
import org.kuali.module.chart.service.AccountService;
import org.kuali.workflow.KualiConstants;

/**
 * 
 * PreRules checks for the Account that needs to occur while still in the Struts processing. This includes defaults, confirmations,
 * etc.
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 * 
 */
public class AccountPreRules extends MaintenancePreRulesBase {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountPreRules.class);

    private static final String CHART_MAINTENANCE_EDOC = "ChartMaintenanceEDoc";
    private static final String DEFAULT_STATE_CODE = "Account.Defaults.StateCode";
    private static final String DEFAULT_ACCOUNT_TYPE_CODE = "Account.Defaults.AccountType";

    private KualiConfigurationService configService;
    private AccountService accountService;
    private Account newAccount;
    private Account copyAccount;

    private static final String CONTRACTS_GRANTS_CD = "CG";
    private static final String GENERAL_FUND_CD = "GF";
    private static final String RESTRICTED_FUND_CD = "RF";
    private static final String ENDOWMENT_FUND_CD = "EN";
    private static final String PLANT_FUND_CD = "PF";

    private static final String RESTRICTED_CD_RESTRICTED = "R";
    private static final String RESTRICTED_CD_UNRESTRICTED = "U";
    private static final String RESTRICTED_CD_TEMPORARILY_RESTRICTED = "T";
    private static final String RESTRICTED_CD_NOT_APPLICABLE = "N";


    public AccountPreRules() {
        accountService = SpringServiceLocator.getAccountService();
        configService = SpringServiceLocator.getKualiConfigurationService();
    }

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
     * 
     * This method sets a default restricted status on an account if and only if the status code
     * in SubFundGroup has been set and the user answers in the affirmative that they definitely
     * want to use this SubFundGroup.
     */
    private void checkForDefaultSubFundGroupStatus() {
        String restrictedStatusCode = "";

        // if subFundGroupCode was not entered, then we have nothing
        // to do here, so exit
        if (ObjectUtils.isNull(copyAccount.getSubFundGroup()) || StringUtils.isBlank(copyAccount.getSubFundGroupCode())) {
            return;
        }
        SubFundGroup subFundGroup = copyAccount.getSubFundGroup();
        
        boolean useSubFundGroup = false;
        if (StringUtils.isNotBlank(subFundGroup.getAccountRestrictedStatusCode())) {
            restrictedStatusCode = subFundGroup.getAccountRestrictedStatusCode().trim();
            String subFundGroupCd = subFundGroup.getSubFundGroupCode();
            useSubFundGroup = askOrAnalyzeYesNoQuestion("SubFundGroup" + subFundGroupCd, buildSubFundGroupConfirmationQuestion(subFundGroupCd, restrictedStatusCode));
            if(useSubFundGroup) {
                //then set defaults for account based on this
                newAccount.setAccountRestrictedStatusCode(restrictedStatusCode);
            } else {
                // the user did not want to use this sub fund group so we wipe it out
                newAccount.setSubFundGroupCode(Constants.EMPTY_STRING);
            }
        }
        
    }
    
    /**
     * 
     * This method builds up the message string that gets sent to the user regarding using
     * this SubFundGroup
     * @param subFundGroupCd
     * @param restrictedStatusCd
     * @return
     */
    protected String buildSubFundGroupConfirmationQuestion(String subFundGroupCd, String restrictedStatusCd) {
        String result = configService.getPropertyString(KeyConstants.QUESTION_ACCT_SUB_FUND_RESTRICTED_STATUS);
        result = StringUtils.replace(result, "{0}", subFundGroupCd);
        result = StringUtils.replace(result, "{1}", restrictedStatusCd);
        return result;
    }
    
    /**
     * 
     * This method checks for continuation accounts and presents the user with a question
     * regarding their use on this account.
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
                newAccount.setContractControlFinCoaCode(account.getAccountNumber());
                newAccount.setContractControlAccountNumber(account.getChartOfAccountsCode());
            }
        }

        if (StringUtils.isNotBlank(newAccount.getIndirectCostRecoveryAcctNbr())) {
            Account account = checkForContinuationAccount("Indirect Cost Recovery Account", newAccount.getIndirectCostRcvyFinCoaCode(), newAccount.getIndirectCostRecoveryAcctNbr(), "");
            if (ObjectUtils.isNotNull(account)) { // override old user inputs
                newAccount.setIndirectCostRcvyFinCoaCode(account.getAccountNumber());
                newAccount.setIndirectCostRecoveryAcctNbr(account.getChartOfAccountsCode());
            }
        }


    }

    /**
     * 
     * This method sets the convenience objects like newAccount and oldAccount, so you have short and easy handles to the new and
     * old objects contained in the maintenance document.
     * 
     * It also calls the BusinessObjectBase.refresh(), which will attempt to load all sub-objects from the DB by their primary keys,
     * if available.
     * 
     * @param document - the maintenanceDocument being evaluated
     * 
     */
    private void setupConvenienceObjects(MaintenanceDocument document) {

        // setup newAccount convenience objects, make sure all possible sub-objects are populated
        newAccount = (Account) document.getNewMaintainableObject().getBusinessObject();
        copyAccount = (Account) ObjectUtils.deepCopy(newAccount);
        copyAccount.refresh();
    }

    /**
     * 
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
        // On new Accounts acct_state_cd is defaulted to the value of "IN"
        // TODO: this is not needed any more, is in maintdoc xml defaults
        if (StringUtils.isBlank(copyAccount.getAccountStateCode())) {
            String defaultStateCode = configService.getApplicationParameterValue(CHART_MAINTENANCE_EDOC, DEFAULT_STATE_CODE);
            newAccount.setAccountStateCode(defaultStateCode);
        }

        // if the account type code is left blank it will default to NA.
        // TODO: this is not needed any more, is in maintdoc xml defaults
        if (StringUtils.isBlank(copyAccount.getAccountTypeCode())) {
            String defaultAccountTypeCode = configService.getApplicationParameterValue(CHART_MAINTENANCE_EDOC, DEFAULT_ACCOUNT_TYPE_CODE);
            newAccount.setAccountTypeCode(defaultAccountTypeCode);
        }
    }

    // lookup state and city from populated zip, set the values on the form
    private void setStateFromZip(MaintenanceDocument maintenanceDocument) {

        // acct_zip_cd, acct_state_cd, acct_city_nm all are populated by looking up
        // the zip code and getting the state and city from that
        if (!StringUtils.isBlank(copyAccount.getAccountZipCode())) {

            HashMap primaryKeys = new HashMap();
            primaryKeys.put("postalZipCode", copyAccount.getAccountZipCode());
            PostalZipCode zip = (PostalZipCode) SpringServiceLocator.getBusinessObjectService().findByPrimaryKey(PostalZipCode.class, primaryKeys);

            // If user enters a valid zip code, override city name and state code entered by user
            if (ObjectUtils.isNotNull(zip)) { // override old user inputs
                newAccount.setAccountCityName(zip.getPostalCityName());
                newAccount.setAccountStateCode(zip.getPostalStateCode());
            }
        }
    }


}
