/*
 * Copyright 2005-2007 The Kuali Foundation.
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.DictionaryValidationService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.ErrorMap;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.Building;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.GeneralLedgerPendingEntryService;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.AccountDescription;
import org.kuali.module.chart.bo.AccountGuideline;
import org.kuali.module.chart.bo.ChartUser;
import org.kuali.module.chart.bo.FundGroup;
import org.kuali.module.chart.bo.IcrAutomatedEntry;
import org.kuali.module.chart.bo.SubFundGroup;
import org.kuali.module.chart.service.AccountService;
import org.kuali.module.chart.service.SubFundGroupService;
import org.kuali.module.gl.service.BalanceService;
import org.kuali.module.labor.service.LaborLedgerPendingEntryService;

/**
 * Business rule(s) applicable to AccountMaintenance documents.
 * 
 * 
 */
public class AccountRule extends MaintenanceDocumentRuleBase {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountRule.class);

    private static final String ACCT_PREFIX_RESTRICTION = "PREFIXES";
    private static final String ACCT_CAPITAL_SUBFUNDGROUP = "CAPITAL_SUB_FUND_GROUP";

    private static final String GENERAL_FUND_CD = "GF";
    private static final String RESTRICTED_FUND_CD = "RF";
    private static final String ENDOWMENT_FUND_CD = "EN";
    private static final String PLANT_FUND_CD = "PF";

    private static final String RESTRICTED_CD_RESTRICTED = "R";
    private static final String RESTRICTED_CD_UNRESTRICTED = "U";
    private static final String RESTRICTED_CD_TEMPORARILY_RESTRICTED = "T";
    private static final String SUB_FUND_GROUP_MEDICAL_PRACTICE_FUNDS = "MPRACT";
    private static final String BUDGET_RECORDING_LEVEL_MIXED = "M";

    private GeneralLedgerPendingEntryService generalLedgerPendingEntryService;
    private LaborLedgerPendingEntryService laborLedgerPendingEntryService;
    private BalanceService balanceService;
    private AccountService accountService;

    private Account oldAccount;
    private Account newAccount;

    public AccountRule() {

        // Pseudo-inject some services.
        //
        // This approach is being used to make it simpler to convert the Rule classes
        // to spring-managed with these services injected by Spring at some later date.
        // When this happens, just remove these calls to the setters with
        // SpringContext, and configure the bean defs for spring.
        this.setGeneralLedgerPendingEntryService(SpringContext.getBean(GeneralLedgerPendingEntryService.class));
        this.setBalanceService(SpringContext.getBean(BalanceService.class));
        this.setAccountService(SpringContext.getBean(AccountService.class));
        this.setLaborLedgerPendingEntryService(SpringContext.getBean(LaborLedgerPendingEntryService.class));
    }

    /**
     * 
     * This method sets the convenience objects like newAccount and oldAccount, so you have short and easy handles to the new and
     * old objects contained in the maintenance document.
     * 
     * It also calls the BusinessObjectBase.refresh(), which will attempt to load all sub-objects from the DB by their primary keys,
     * if available.
     * 
     */
    public void setupConvenienceObjects() {

        // setup oldAccount convenience objects, make sure all possible sub-objects are populated
        oldAccount = (Account) super.getOldBo();

        // setup newAccount convenience objects, make sure all possible sub-objects are populated
        newAccount = (Account) super.getNewBo();
    }

    /**
     * 
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {

        LOG.info("processCustomSaveDocumentBusinessRules called");
        // call the route rules to report all of the messages, but ignore the result
        processCustomRouteDocumentBusinessRules(document);

        // Save always succeeds, even if there are business rule failures
        return true;
    }

    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {

        LOG.info("processCustomRouteDocumentBusinessRules called");
        setupConvenienceObjects();

        // default to success
        boolean success = true;

		// validate the embedded AccountGuideline object
        success &= checkAccountGuidelinesValidation( newAccount.getAccountGuideline() ); 
        
        success &= checkEmptyValues(document);
        success &= checkGeneralRules(document);
        success &= checkCloseAccount(document);
        success &= checkContractsAndGrants(document);
        success &= checkExpirationDate(document);
        success &= checkFundGroup(document);
        success &= checkSubFundGroup(document);
        success &= checkFiscalOfficerIsValidKualiUser(newAccount.getAccountFiscalOfficerSystemIdentifier());

        return success;
    }

    /**
     * 
     * This method checks the basic rules for empty values in an account and associated objects with this account
     * 
     * @param maintenanceDocument
     * @return
     */
    protected boolean checkEmptyValues(MaintenanceDocument maintenanceDocument) {

        LOG.info("checkEmptyValues called");

        boolean success = true;

        // guidelines are always required, except when the expirationDate is set, and its
        // earlier than today
        boolean guidelinesRequired = areGuidelinesRequired((Account) maintenanceDocument.getNewMaintainableObject().getBusinessObject());

        // confirm that required guidelines are entered, if required
        if (guidelinesRequired) {
            success &= checkEmptyBOField("accountGuideline.accountExpenseGuidelineText", newAccount.getAccountGuideline().getAccountExpenseGuidelineText(), "Expense Guideline");
            success &= checkEmptyBOField("accountGuideline.accountIncomeGuidelineText", newAccount.getAccountGuideline().getAccountIncomeGuidelineText(), "Income Guideline");
            success &= checkEmptyBOField("accountGuideline.accountPurposeText", newAccount.getAccountGuideline().getAccountPurposeText(), "Account Purpose");
        }

        // this set confirms that all fields which are grouped (ie, foreign keys of a referenc
        // object), must either be none filled out, or all filled out.
        success &= checkForPartiallyFilledOutReferenceForeignKeys("continuationAccount");
        success &= checkForPartiallyFilledOutReferenceForeignKeys("incomeStreamAccount");
        success &= checkForPartiallyFilledOutReferenceForeignKeys("endowmentIncomeAccount");
        success &= checkForPartiallyFilledOutReferenceForeignKeys("reportsToAccount");
        success &= checkForPartiallyFilledOutReferenceForeignKeys("contractControlAccount");
        success &= checkForPartiallyFilledOutReferenceForeignKeys("indirectCostRecoveryAcct");

        return success;
    }

    protected boolean checkAccountGuidelinesValidation( AccountGuideline accountGuideline ) {
        ErrorMap map = GlobalVariables.getErrorMap();
        int errorCount = map.getErrorCount();
        GlobalVariables.getErrorMap().addToErrorPath("document.newMaintainableObject.accountGuideline");
        dictionaryValidationService.validateBusinessObject( accountGuideline, false ); 
        GlobalVariables.getErrorMap().removeFromErrorPath("document.newMaintainableObject.accountGuideline");
        return map.getErrorCount() == errorCount; 
    }
    
    /**
     * 
     * This method determines whether the guidelines are required, based on business rules.
     * 
     * @param account - the populated Account bo to be evaluated
     * @return true if guidelines are required, false otherwise
     * 
     */
    protected boolean areGuidelinesRequired(Account account) {

        boolean result = true;

        if (account.getAccountExpirationDate() != null) {
            Timestamp today = getDateTimeService().getCurrentTimestamp();
            today.setTime(DateUtils.truncate(today, Calendar.DAY_OF_MONTH).getTime());
            if (account.getAccountExpirationDate().before(today)) {
                result = false;
            }
        }
        return result;
    }

    /**
     * 
     * This method tests whether the accountNumber passed in is prefixed with an allowed prefix, or an illegal one.
     * 
     * The illegal prefixes are passed in as an array of strings.
     * 
     * @param accountNumber - The Account Number to be tested.
     * @param illegalValues - An Array of Strings of the unallowable prefixes.
     * @return false if the accountNumber starts with any of the illegalPrefixes, true otherwise
     * 
     */
    protected boolean accountNumberStartsWithAllowedPrefix(String accountNumber, String[] illegalValues) {

        boolean result = true;

        // for each disallowed value, make sure the account doesnt start with it
        for (int i = 0; i < illegalValues.length; i++) {
            if (accountNumber.startsWith(illegalValues[i])) {
                result = false;
                putFieldError("accountNumber", KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_ACCT_NMBR_NOT_ALLOWED, new String[] { accountNumber, illegalValues[i] });
            }
        }

        return result;
    }

    /**
     * 
     * This method tests whether an account is being ReOpened by anyone except a system supervisor. Only system supervisors may
     * reopen closed accounts.
     * 
     * @param document - populated document containing the old and new accounts
     * @param user - the user who is trying to possibly reopen the account
     * @return true if: document is an edit document, old was closed and new is open, and the user is not one of the System
     *         Supervisors
     * 
     */
    protected boolean isNonSystemSupervisorEditingAClosedAccount(MaintenanceDocument document, UniversalUser user) {

        boolean result = false;

        if (document.isEdit()) {

            // get local references
            Account oldAccount = (Account) document.getOldMaintainableObject().getBusinessObject();
            Account newAccount = (Account) document.getNewMaintainableObject().getBusinessObject();

            // do the test
            if (oldAccount.isAccountClosedIndicator() && !user.isSupervisorUser()) {
                result = true;
            }
        }
        return result;
    }

    /**
     * 
     * This method tests whether a given account has the T - Temporary value for Restricted Status Code, but does not have a
     * Restricted Status Date, which is required when the code is T.
     * 
     * @param account
     * @return
     */
    protected boolean hasTemporaryRestrictedStatusCodeButNoRestrictedStatusDate(Account account) {

        boolean result = false;

        if (StringUtils.isNotBlank(account.getAccountRestrictedStatusCode())) {
            if (RESTRICTED_CD_TEMPORARILY_RESTRICTED.equalsIgnoreCase(account.getAccountRestrictedStatusCode().trim())) {
                if (account.getAccountRestrictedStatusDate() == null) {
                    result = true;
                }
            }
        }
        return result;
    }

    /**
     * Checks whether the account restricted status code is the default from the sub fund group.
     * 
     * @param account
     * @return
     */
    protected boolean hasDefaultRestrictedStatusCode(Account account) {
        boolean result = false;

        if (StringUtils.isNotBlank(account.getAccountRestrictedStatusCode())) {
            result = account.getAccountRestrictedStatusCode().equals(account.getSubFundGroup().getAccountRestrictedStatusCode());
        }

        return result;
    }

    /**
     * 
     * This method checks some of the general business rules associated with this document
     * 
     * @param maintenanceDocument
     * @return false on rules violation
     */
    protected boolean checkGeneralRules(MaintenanceDocument maintenanceDocument) {

        LOG.info("checkGeneralRules called");
        UniversalUser fiscalOfficer = newAccount.getAccountFiscalOfficerUser();
        UniversalUser accountManager = newAccount.getAccountManagerUser();
        UniversalUser accountSupervisor = newAccount.getAccountSupervisoryUser();

        boolean success = true;

        // Enforce institutionally specified restrictions on account number prefixes
        // (e.g. the account number cannot begin with a 3 or with 00.)
        // Only bother trying if there is an account string to test
        if (!StringUtils.isBlank(newAccount.getAccountNumber())) {
            String[] illegalValues = getConfigService().getParameterValues(KFSConstants.CHART_NAMESPACE, KFSConstants.Components.ACCOUNT, ACCT_PREFIX_RESTRICTION);
            // test the number
            success &= accountNumberStartsWithAllowedPrefix(newAccount.getAccountNumber(), illegalValues);
        }

        // only a FIS supervisor can reopen a closed account. (This is the central super user, not an account supervisor).
        // we need to get the old maintanable doc here
        if (isNonSystemSupervisorEditingAClosedAccount(maintenanceDocument, GlobalVariables.getUserSession().getUniversalUser())) {
            success &= false;
            putFieldError("accountClosedIndicator", KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_ONLY_SUPERVISORS_CAN_EDIT);
        }

        // when a restricted status code of 'T' (temporarily restricted) is selected, a restricted status
        // date must be supplied.
        if (hasTemporaryRestrictedStatusCodeButNoRestrictedStatusDate(newAccount)) {
            success &= false;
            putFieldError("accountRestrictedStatusDate", KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_RESTRICTED_STATUS_DT_REQ, newAccount.getAccountNumber());
        }

        // check FringeBenefit account rules
        success &= checkFringeBenefitAccountRule(newAccount);

        // the employee type for fiscal officer, account manager, and account supervisor must be 'P' – professional.
        success &= checkUserStatusAndType("accountFiscalOfficerUser.personUserIdentifier", fiscalOfficer);
        success &= checkUserStatusAndType("accountSupervisoryUser.personUserIdentifier", accountSupervisor);
        success &= checkUserStatusAndType("accountManagerUser.personUserIdentifier", accountManager);

        // the supervisor cannot be the same as the fiscal officer or account manager.
        if (isSupervisorSameAsFiscalOfficer(newAccount)) {
            success &= false;
            putFieldError("accountsSupervisorySystemsIdentifier", KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_ACCT_SUPER_CANNOT_BE_FISCAL_OFFICER);
        }
        if (isSupervisorSameAsManager(newAccount)) {
            success &= false;
            putFieldError("accountManagerSystemIdentifier", KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_ACCT_SUPER_CANNOT_BE_ACCT_MGR);
        }

        // disallow continuation account being expired
        if (isContinuationAccountExpired(newAccount)) {
            success &= false;
            putFieldError("continuationAccountNumber", KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_ACCOUNT_EXPIRED_CONTINUATION);
        }

        return success;
    }

    /**
     * 
     * This method tests whether the continuation account entered (if any) has expired or not.
     * 
     * @param newAccount
     * @return
     * 
     */
    protected boolean isContinuationAccountExpired(Account newAccount) {

        boolean result = false;

        String chartCode = newAccount.getContinuationFinChrtOfAcctCd();
        String accountNumber = newAccount.getContinuationAccountNumber();

        // if either chartCode or accountNumber is not entered, then we
        // cant continue, so exit
        if (StringUtils.isBlank(chartCode) || StringUtils.isBlank(accountNumber)) {
            return result;
        }

        // attempt to retrieve the continuation account from the DB
        Account continuation = null;
        Map pkMap = new HashMap();
        pkMap.put("chartOfAccountsCode", chartCode);
        pkMap.put("accountNumber", accountNumber);
        continuation = (Account) super.getBoService().findByPrimaryKey(Account.class, pkMap);

        // if the object doesnt exist, then we cant continue, so exit
        if (ObjectUtils.isNull(continuation)) {
            return result;
        }

        // at this point, we have a valid continuation account, so we just need to
        // know whether its expired or not
        result = continuation.isExpired();

        return result;
    }

    // the fringe benefit account (otherwise known as the reportsToAccount) is required if
    // the fringe benefit code is set to N.
    // The fringe benefit code of the account designated to accept the fringes must be Y.
    protected boolean checkFringeBenefitAccountRule(Account newAccount) {

        boolean result = true;

        // if this account is selected as a Fringe Benefit Account, then we have nothing
        // to test, so exit
        if (newAccount.isAccountsFringesBnftIndicator()) {
            return true;
        }

        // if fringe benefit is not selected ... continue processing

        // fringe benefit account number is required
        if (StringUtils.isBlank(newAccount.getReportsToAccountNumber())) {
            putFieldError("reportsToAccountNumber", KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_RPTS_TO_ACCT_REQUIRED_IF_FRINGEBENEFIT_FALSE);
            result &= false;
        }

        // fringe benefit chart of accounts code is required
        if (StringUtils.isBlank(newAccount.getReportsToChartOfAccountsCode())) {
            putFieldError("reportsToChartOfAccountsCode", KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_RPTS_TO_ACCT_REQUIRED_IF_FRINGEBENEFIT_FALSE);
            result &= false;
        }

        // if either of the fringe benefit account fields are not present, then we're done
        if (result == false) {
            return result;
        }

        // attempt to load the fringe benefit account
        Account fringeBenefitAccount = accountService.getByPrimaryId(newAccount.getReportsToChartOfAccountsCode(), newAccount.getReportsToAccountNumber());

        // fringe benefit account must exist
        if (fringeBenefitAccount == null) {
            putFieldError("reportsToAccountNumber", KFSKeyConstants.ERROR_EXISTENCE, getFieldLabel(Account.class, "reportsToAccountNumber"));
            return false;
        }

        // fringe benefit account must be active
        if (fringeBenefitAccount.isAccountClosedIndicator()) {
            putFieldError("reportsToAccountNumber", KFSKeyConstants.ERROR_INACTIVE, getFieldLabel(Account.class, "reportsToAccountNumber"));
            result &= false;
        }

        // make sure the fringe benefit account specified is set to fringe benefits = Y
        if (!fringeBenefitAccount.isAccountsFringesBnftIndicator()) {
            putFieldError("reportsToAccountNumber", KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_RPTS_TO_ACCT_MUST_BE_FLAGGED_FRINGEBENEFIT, fringeBenefitAccount.getChartOfAccountsCode() + "-" + fringeBenefitAccount.getAccountNumber());
            result &= false;
        }

        return result;
    }

    protected boolean isSupervisorSameAsFiscalOfficer(Account account) {
        return areTwoUsersTheSame(account.getAccountSupervisoryUser(), account.getAccountFiscalOfficerUser());
    }

    protected boolean isSupervisorSameAsManager(Account account) {
        return areTwoUsersTheSame(account.getAccountSupervisoryUser(), account.getAccountManagerUser());
    }

    protected boolean areTwoUsersTheSame(UniversalUser user1, UniversalUser user2) {
        if (ObjectUtils.isNull(user1)) {
            return false;
        }
        if (ObjectUtils.isNull(user2)) {
            return false;
        }
        if (ObjectUtils.equalByKeys(user1, user2)) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * 
     * This method checks to see if the user passed in is of the type requested.
     * 
     * If so, it returns true. If not, it returns false, and adds an error to the GlobalErrors.
     *
     * @param propertyName -
     * @param user - UniversalUser to be tested
     *
     * @return true if user is of the requested employee type, false if not, true if the user object is null
     * 
     */
    protected boolean checkUserStatusAndType(String propertyName, UniversalUser user) {

        boolean success = true;

        // if the user isnt populated, exit with success
        // the actual existence check is performed in the general rules so not testing here
        if (ObjectUtils.isNull(user)) {
            return success;
        }

        // user must be of the allowable statuses (A - Active)
        if (apcRuleFails(KFSConstants.CHART_NAMESPACE, KFSConstants.Components.ACCOUNT, KFSConstants.ChartApcParms.ACCOUNT_USER_EMP_STATUSES, user.getEmployeeStatusCode())) {
            success &= false;
            putFieldError(propertyName, KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_ACTIVE_REQD_FOR_EMPLOYEE, getDdService().getAttributeLabel(Account.class, propertyName));
        }

        // user must be of the allowable types (P - Professional)
        if (apcRuleFails(KFSConstants.CHART_NAMESPACE, KFSConstants.Components.ACCOUNT, KFSConstants.ChartApcParms.ACCOUNT_USER_EMP_TYPES, user.getEmployeeTypeCode())) {
            success &= false;
            putFieldError(propertyName, KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_PRO_TYPE_REQD_FOR_EMPLOYEE, getDdService().getAttributeLabel(Account.class, propertyName));
        }

        return success;
    }

    /**
     * 
     * This method checks to see if the user is trying to close the account and if so if any rules are being violated
     * 
     * @param maintenanceDocument
     * @return false on rules violation
     */
    protected boolean checkCloseAccount(MaintenanceDocument maintenanceDocument) {

        LOG.info("checkCloseAccount called");

        boolean success = true;
        boolean isBeingClosed = false;

        // if the account isnt being closed, then dont bother processing the rest of
        // the method
        if (!oldAccount.isAccountClosedIndicator() && newAccount.isAccountClosedIndicator()) {
            isBeingClosed = true;
        }

        if (!isBeingClosed) {
            return true;
        }

        // on an account being closed, the expiration date must be
        success &= checkAccountExpirationDateValidTodayOrEarlier(newAccount);

        // when closing an account, a continuation account is required
        if (StringUtils.isBlank(newAccount.getContinuationAccountNumber())) {
            putFieldError("continuationAccountNumber", KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_ACCT_CLOSE_CONTINUATION_ACCT_REQD);
            success &= false;
        }
        if (StringUtils.isBlank(newAccount.getContinuationFinChrtOfAcctCd())) {
            putFieldError("continuationFinChrtOfAcctCd", KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_ACCT_CLOSE_CONTINUATION_ACCT_REQD);
            success &= false;
        }

        // must have no pending ledger entries
        if (generalLedgerPendingEntryService.hasPendingGeneralLedgerEntry(newAccount)) {
            putGlobalError(KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_ACCOUNT_CLOSED_PENDING_LEDGER_ENTRIES);
            success &= false;
        }
        
        // beginning balance must be loaded in order to close account
        if (!balanceService.beginningBalanceLoaded(newAccount)) {
            putGlobalError(KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_ACCOUNT_CLOSED_NO_LOADED_BEGINNING_BALANCE);
            success &= false;
        }

        // must have no base budget, must have no open encumbrances, must have no asset, liability or fund balance balances other
        // than object code 9899
        // (9899 is fund balance for us), and the process of closing income and expense into 9899 must take the 9899 balance to
        // zero.
        if (balanceService.hasAssetLiabilityFundBalanceBalances(newAccount)) {
            putGlobalError(KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_ACCOUNT_CLOSED_NO_FUND_BALANCES);
            success &= false;
        }

        // We must not have any pending labor ledger entries
        if (laborLedgerPendingEntryService.hasPendingLaborLedgerEntry(newAccount)) {
        putGlobalError(KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_ACCOUNT_CLOSED_PENDING_LABOR_LEDGER_ENTRIES);
        success &= false;
        }

        return success;
    }

    protected boolean checkAccountExpirationDateValidTodayOrEarlier(Account newAccount) {

        // get today's date, with no time component
        Timestamp todaysDate = getDateTimeService().getCurrentTimestamp();
        todaysDate.setTime(DateUtils.truncate(todaysDate, Calendar.DAY_OF_MONTH).getTime());
        // TODO: convert this to using Wes' kuali DateUtils once we're using Date's instead of Timestamp

        // get the expiration date, if any
        Timestamp expirationDate = newAccount.getAccountExpirationDate();
        if (ObjectUtils.isNull(expirationDate)) {
            putFieldError("accountExpirationDate", KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_ACCT_CANNOT_BE_CLOSED_EXP_DATE_INVALID);
            return false;
        }

        // when closing an account, the account expiration date must be the current date or earlier
        expirationDate.setTime(DateUtils.truncate(expirationDate, Calendar.DAY_OF_MONTH).getTime());
        if (expirationDate.after(todaysDate)) {
            putFieldError("accountExpirationDate", KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_ACCT_CANNOT_BE_CLOSED_EXP_DATE_INVALID);
            return false;
        }

        return true;
    }

    /**
     * 
     * This method checks to see if any Contracts and Grants business rules were violated
     * 
     * @param maintenanceDocument
     * @return false on rules violation
     */
    protected boolean checkContractsAndGrants(MaintenanceDocument maintenanceDocument) {

        LOG.info("checkContractsAndGrants called");

        boolean success = true;

        // Certain C&G fields are required if the Account belongs to the CG Fund Group
        success &= checkCgRequiredFields(newAccount);

        // Income Stream account is required if this account is CG fund group,
        // or GF (general fund) fund group (with some exceptions)
        success &= checkCgIncomeStreamRequired(newAccount);

        return success;
    }

    protected boolean checkCgIncomeStreamRequired(Account newAccount) {

        boolean result = true;
        boolean required = false;

        // if the subFundGroup object is null, we cant test, so exit
        if (ObjectUtils.isNull(newAccount.getSubFundGroup())) {
            return result;
        }

        // retrieve the subfundcode and fundgroupcode
        String subFundGroupCode = newAccount.getSubFundGroupCode().trim();
        String fundGroupCode = newAccount.getSubFundGroup().getFundGroupCode().trim();

        String requiredByValue = "";
        String requiredByLabel = "";
        
        // if this is a CG fund group, then its required
        if (SpringContext.getBean(SubFundGroupService.class).isForContractsAndGrants(newAccount.getSubFundGroup())) {
            required = true;
            requiredByLabel = SpringContext.getBean(SubFundGroupService.class).getContractsAndGrantsDenotingAttributeLabel();
            requiredByValue = SpringContext.getBean(SubFundGroupService.class).getContractsAndGrantsDenotingValue();
        }

        // if this is a general fund group, then its required
        else if (GENERAL_FUND_CD.equalsIgnoreCase(fundGroupCode)) {
            // unless its part of the MPRACT subfundgroup
            if (!SUB_FUND_GROUP_MEDICAL_PRACTICE_FUNDS.equalsIgnoreCase(subFundGroupCode)) {
                required = true;
                requiredByLabel = getDdService().getAttributeLabel(FundGroup.class, KFSConstants.FUND_GROUP_CODE_PROPERTY_NAME);
                requiredByValue = GENERAL_FUND_CD;
            }
        }

        // if the income stream account is not required, then we're done
        if (!required) {
            return result;
        }

        // make sure both coaCode and accountNumber are filled out
        boolean incomeStreamAccountIsValid = true;
        if ( !checkEmptyValue( newAccount.getIncomeStreamFinancialCoaCode() ) ) {
        	putFieldError( "incomeStreamFinancialCoaCode", KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_INCOME_STREAM_ACCT_COA_CANNOT_BE_EMPTY, new String[] { requiredByLabel, requiredByValue } );
        	incomeStreamAccountIsValid = false;
        }
        if ( !checkEmptyValue( newAccount.getIncomeStreamAccountNumber() ) ) {
        	putFieldError( "incomeStreamAccountNumber", KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_INCOME_STREAM_ACCT_NBR_CANNOT_BE_EMPTY, new String[] { requiredByLabel, requiredByValue } );
        	incomeStreamAccountIsValid = false;
        }

        // if both fields arent present, then we're done
        if (incomeStreamAccountIsValid) {
            // do an existence/active test
            DictionaryValidationService dvService = super.getDictionaryValidationService();
            boolean referenceExists = dvService.validateReferenceExists(newAccount, "incomeStreamAccount");
            if (!referenceExists) {
                putFieldError("incomeStreamAccountNumber", KFSKeyConstants.ERROR_EXISTENCE, "Income Stream Account: " + newAccount.getIncomeStreamFinancialCoaCode() + "-" + newAccount.getIncomeStreamAccountNumber());
                incomeStreamAccountIsValid = false;
            }
        }

        if(incomeStreamAccountIsValid) {
            result = true;
        } else {
            result = null != newAccount.getAccountNumber() && null != newAccount.getIncomeStreamAccountNumber();
            if(result) {
                result &= newAccount.getAccountNumber().equals(newAccount.getIncomeStreamAccountNumber());
                result &= newAccount.getChartOfAccountsCode().equals(newAccount.getIncomeStreamFinancialCoaCode());
            }
        }

        return result;
    }

    protected boolean checkCgRequiredFields(Account newAccount) {

        boolean result = true;

        // Certain C&G fields are required if the Account belongs to the CG Fund Group
        if (ObjectUtils.isNotNull(newAccount.getSubFundGroup())) {
            if (SpringContext.getBean(SubFundGroupService.class).isForContractsAndGrants(newAccount.getSubFundGroup())) {
                result &= checkEmptyBOField("acctIndirectCostRcvyTypeCd", newAccount.getAcctIndirectCostRcvyTypeCd(), replaceTokens(KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_ICR_TYPE_CODE_CANNOT_BE_EMPTY));
                result &= checkEmptyBOField("financialIcrSeriesIdentifier", newAccount.getFinancialIcrSeriesIdentifier(), replaceTokens(KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_ICR_SERIES_IDENTIFIER_CANNOT_BE_EMPTY));
                
                // Validation for financialIcrSeriesIdentifier
                if (checkEmptyBOField("financialIcrSeriesIdentifier", newAccount.getFinancialIcrSeriesIdentifier(), replaceTokens(KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_ICR_SERIES_IDENTIFIER_CANNOT_BE_EMPTY))){
                    Map pkMap = new HashMap();
                    pkMap.put("financialIcrSeriesIdentifier", newAccount.getFinancialIcrSeriesIdentifier());
                    if (getBoService().countMatching(IcrAutomatedEntry.class, pkMap) == 0){
                        putFieldError("financialIcrSeriesIdentifier", KFSKeyConstants.ERROR_EXISTENCE, "financialIcrSeriesIdentifier");
                        result &= false;
                    }
                }
                
                result &= checkEmptyBOField("indirectCostRcvyFinCoaCode", newAccount.getIndirectCostRcvyFinCoaCode(), replaceTokens(KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_ICR_CHART_CODE_CANNOT_BE_EMPTY));
                result &= checkEmptyBOField("indirectCostRecoveryAcctNbr", newAccount.getIndirectCostRecoveryAcctNbr(), replaceTokens(KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_ICR_ACCOUNT_CANNOT_BE_EMPTY));
                result &= checkContractControlAccountNumberRequired(newAccount);
            } else {
                // this is not a C&G fund group.  So users should not fill in any fields in the C&G tab.
                result &= checkCGFieldNotFilledIn(newAccount, "acctIndirectCostRcvyTypeCd");
                result &= checkCGFieldNotFilledIn(newAccount, "financialIcrSeriesIdentifier");
                result &= checkCGFieldNotFilledIn(newAccount, "indirectCostRcvyFinCoaCode");
                result &= checkCGFieldNotFilledIn(newAccount, "indirectCostRecoveryAcctNbr");
            }
        }
        return result;
    }

    private String replaceTokens(String errorConstant){
        String cngLabel = SpringContext.getBean(SubFundGroupService.class).getContractsAndGrantsDenotingAttributeLabel();
        String cngValue = SpringContext.getBean(SubFundGroupService.class).getContractsAndGrantsDenotingValue();
        String result = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(errorConstant);
        result = StringUtils.replace(result, "{0}", cngLabel);
        result = StringUtils.replace(result, "{1}", cngValue);
        return result;
    }

    protected boolean checkContractControlAccountNumberRequired(Account newAccount) {

        boolean result = true;

        // Contract Control account must either exist or be the same as account being maintained

        if (ObjectUtils.isNull(newAccount.getContractControlFinCoaCode())) {
            return result;
        }
        if (ObjectUtils.isNull(newAccount.getContractControlAccountNumber())) {
            return result;
        }
        if ((newAccount.getContractControlFinCoaCode().equals(newAccount.getChartOfAccountsCode())) && (newAccount.getContractControlAccountNumber().equals(newAccount.getAccountNumber()))) {
            return result;
        }

        // do an existence/active test
        DictionaryValidationService dvService = super.getDictionaryValidationService();
        boolean referenceExists = dvService.validateReferenceExists(newAccount, "contractControlAccount");
        if (!referenceExists) {
            putFieldError("contractControlAccountNumber", KFSKeyConstants.ERROR_EXISTENCE, "Contract Control Account: " + newAccount.getContractControlFinCoaCode() + "-" + newAccount.getContractControlAccountNumber());
            result &= false;
        }

        return result;
    }

    /**
     * 
     * This method checks to see if any expiration date field rules were violated
     * 
     * @param maintenanceDocument
     * @return false on rules violation
     */
    protected boolean checkExpirationDate(MaintenanceDocument maintenanceDocument) {

        LOG.info("checkExpirationDate called");

        boolean success = true;

        Timestamp oldExpDate = oldAccount.getAccountExpirationDate();
        Timestamp newExpDate = newAccount.getAccountExpirationDate();
        Timestamp today = getDateTimeService().getCurrentTimestamp();
        today.setTime(DateUtils.truncate(today, Calendar.DAY_OF_MONTH).getTime()); // remove any time components

        // When updating an account expiration date, the date must be today or later
        // Only run this test if this maint doc
        // is an edit doc
        if (isUpdatedExpirationDateInvalid(maintenanceDocument)) {
            putFieldError("accountExpirationDate", KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_EXP_DATE_TODAY_LATER);
            success &= false;
        }

        // a continuation account is required if the expiration date is completed.
        if (ObjectUtils.isNotNull(newExpDate)) {
            if (StringUtils.isBlank(newAccount.getContinuationAccountNumber())) {
                putFieldError("continuationAccountNumber", KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_CONTINUATION_ACCT_REQD_IF_EXP_DATE_COMPLETED);
            }
            if (StringUtils.isBlank(newAccount.getContinuationFinChrtOfAcctCd())) {
                putFieldError("continuationFinChrtOfAcctCd", KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_CONTINUATION_FINCODE_REQD_IF_EXP_DATE_COMPLETED);
                // putGlobalError(KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_CONTINUATION_ACCT_REQD_IF_EXP_DATE_COMPLETED);
                success &= false;
            }
        }

        // If creating a new account if acct_expiration_dt is set then
        // the acct_expiration_dt must be changed to a date that is today or later
        if (maintenanceDocument.isNew() && ObjectUtils.isNotNull(newExpDate)) {
            if (!newExpDate.after(today) && !newExpDate.equals(today)) {
                putFieldError("accountExpirationDate", KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_EXP_DATE_TODAY_LATER);
                // putGlobalError(KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_EXP_DATE_TODAY_LATER);
                success &= false;
            }
        }

        // acct_expiration_dt can not be before acct_effect_dt
        Timestamp effectiveDate = newAccount.getAccountEffectiveDate();
        if (ObjectUtils.isNotNull(effectiveDate) && ObjectUtils.isNotNull(newExpDate)) {
            if (newExpDate.before(effectiveDate)) {
                putFieldError("accountExpirationDate", KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_EXP_DATE_CANNOT_BE_BEFORE_EFFECTIVE_DATE);
                // putGlobalError(KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_EXP_DATE_CANNOT_BE_BEFORE_EFFECTIVE_DATE);
                success &= false;
            }
        }

        return success;
    }

    protected boolean isUpdatedExpirationDateInvalid(MaintenanceDocument maintDoc) {

        // if this isnt an Edit document, we're not interested
        if (!maintDoc.isEdit()) {
            return false;
        }

        Timestamp oldExpDate = oldAccount.getAccountExpirationDate();
        Timestamp newExpDate = newAccount.getAccountExpirationDate();
        Timestamp today = getDateTimeService().getCurrentTimestamp();
        today.setTime(DateUtils.truncate(today, Calendar.DAY_OF_MONTH).getTime()); // remove any time components

        // When updating an account expiration date, the date must be today or later
        // Only run this test if this maint doc
        // is an edit doc
        boolean expDateHasChanged = false;

        // if the old version of the account had no expiration date, and the new
        // one has a date
        if (ObjectUtils.isNull(oldExpDate) && ObjectUtils.isNotNull(newExpDate)) {
            expDateHasChanged = true;
        }

        // if there was an old and a new expDate, but they're different
        else if (ObjectUtils.isNotNull(oldExpDate) && ObjectUtils.isNotNull(newExpDate)) {
            if (!oldExpDate.equals(newExpDate)) {
                expDateHasChanged = true;
            }
        }

        // if the expiration date hasnt changed, we're not interested
        if (!expDateHasChanged) {
            return false;
        }

        // make a shortcut to the newAccount
        Account newAccount = (Account) maintDoc.getNewMaintainableObject().getBusinessObject();

        // expirationDate must be today or later than today (cannot be before today)
        if (newExpDate.equals(today) || newExpDate.after(today)) {
            return false;
        }
        else
            return true;
    }

    /**
     * 
     * This method checks to see if any Fund Group rules were violated
     * 
     * @param maintenanceDocument
     * @return false on rules violation
     * 
     */
    protected boolean checkFundGroup(MaintenanceDocument maintenanceDocument) {

        LOG.info("checkFundGroup called");

        boolean success = true;
        SubFundGroup subFundGroup = newAccount.getSubFundGroup();

        if (ObjectUtils.isNotNull(subFundGroup)) {

            // get values for fundGroupCode and restrictedStatusCode
            String fundGroupCode = "";
            String restrictedStatusCode = "";
            if (StringUtils.isNotBlank(subFundGroup.getFundGroupCode())) {
                fundGroupCode = subFundGroup.getFundGroupCode().trim();
            }
            if (StringUtils.isNotBlank(newAccount.getAccountRestrictedStatusCode())) {
                restrictedStatusCode = newAccount.getAccountRestrictedStatusCode().trim();
            }

            // an account in the general fund fund group cannot have a budget recording level of mixed.
            if (fundGroupCode.equalsIgnoreCase(GENERAL_FUND_CD)) {
                String budgetRecordingLevelCode = newAccount.getBudgetRecordingLevelCode();
                if (StringUtils.isNotEmpty(budgetRecordingLevelCode)) {
                    if (budgetRecordingLevelCode.equalsIgnoreCase(BUDGET_RECORDING_LEVEL_MIXED)) {
                        putFieldError("budgetRecordingLevelCode", KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_ACCT_GF_BUDGET_RECORD_LVL_MIXED);
                        success &= false;
                    }
                }
            }
        }

        return success;
    }

    /**
     * 
     * This method insures the fiscal officer is a valid Kuali User
     * @param fiscalOfficerUserId
     * @return
     */
    protected boolean checkFiscalOfficerIsValidKualiUser(String fiscalOfficerUserId) {
        boolean result = true;
        try {
            UniversalUser fiscalOfficer = getUniversalUserService().getUniversalUser(fiscalOfficerUserId);
            if (fiscalOfficer != null && !fiscalOfficer.isActiveForModule(ChartUser.MODULE_ID)) {
                result = false;
                putFieldError("accountFiscalOfficerUser.personUserIdentifier", KFSKeyConstants.ERROR_DOCUMENT_ACCOUNT_FISCAL_OFFICER_MUST_BE_KUALI_USER);
            }
        }
        catch (UserNotFoundException e) {
            result = false;
        }

        return result;        
    }
    
    /**
     * 
     * This method checks to see if any SubFund Group rules were violated
     * 
     * @param maintenanceDocument
     * @return false on rules violation
     * 
     */
    protected boolean checkSubFundGroup(MaintenanceDocument maintenanceDocument) {

        LOG.info("checkSubFundGroup called");

        boolean success = true;
        
        String subFundGroupCode = newAccount.getSubFundGroupCode();
        
        if( newAccount.getAccountDescription() != null ){
            
            String campusCode = newAccount.getAccountDescription().getCampusCode();
            String buildingCode = newAccount.getAccountDescription().getBuildingCode();

            //check if sub fund group code is blank
            if (StringUtils.isBlank(subFundGroupCode)) {
                
                // check if campus code and building code are NOT blank
                if( !StringUtils.isBlank(campusCode) || !StringUtils.isBlank(buildingCode)){
                    
                    // if sub_fund_grp_cd is blank, campus code should NOT be entered
                    if (!StringUtils.isBlank(campusCode)) {
                        putFieldError("accountDescription.campusCode", KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_BLANK_SUBFUNDGROUP_WITH_CAMPUS_CD_FOR_BLDG, subFundGroupCode );
                        success &= false;
                    }
    
                    // if sub_fund_grp_cd is blank, then bldg_cd should NOT be entered
                    if (!StringUtils.isBlank(buildingCode)) {
                        putFieldError("accountDescription.buildingCode", KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_BLANK_SUBFUNDGROUP_WITH_BUILDING_CD, subFundGroupCode);
                        success &= false;
                    }                   
                    
                } else {
                    
                    //if all sub fund group, campus code, building code are all blank return true
                    return success;
                }

            } else if( !StringUtils.isBlank(subFundGroupCode) && !ObjectUtils.isNull(newAccount.getSubFundGroup()) ){

        // Attempt to get the right SubFundGroup code to check the following logic with. If the value isn't available, go ahead
        // and die, as this indicates a misconfigured app, and important business rules wont be implemented without it.
        String capitalSubFundGroup = getConfigService().getParameterValue(KFSConstants.CHART_NAMESPACE, KFSConstants.Components.ACCOUNT, ACCT_CAPITAL_SUBFUNDGROUP);

        if (capitalSubFundGroup.equalsIgnoreCase(subFundGroupCode.trim())) {

            // if sub_fund_grp_cd is 'PFCMR' then campus_cd must be entered
            if (StringUtils.isBlank(campusCode)) {
                putFieldError("accountDescription.campusCode", KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_CAMS_SUBFUNDGROUP_WITH_MISSING_CAMPUS_CD_FOR_BLDG);
                success &= false;
            }

            // if sub_fund_grp_cd is 'PFCMR' then bldg_cd must be entered
            if (StringUtils.isBlank(buildingCode)) {
                        putFieldError("accountDescription.buildingCode", KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_CAMS_SUBFUNDGROUP_WITH_MISSING_BUILDING_CD);
                success &= false;
            }

            // the building object (campusCode & buildingCode) must exist in the DB
            if (!StringUtils.isBlank(campusCode) && !StringUtils.isBlank(buildingCode)) {
                                
                //make sure that primary key fields are upper case
                DataDictionaryService dds = SpringContext.getBean(DataDictionaryService.class);
                Boolean buildingCodeForceUppercase = dds.getAttributeForceUppercase(AccountDescription.class, KFSPropertyConstants.BUILDING_CODE );
                if (StringUtils.isNotBlank(buildingCode) && buildingCodeForceUppercase != null && buildingCodeForceUppercase.booleanValue() == true ) {
                    buildingCode = buildingCode.toUpperCase();
                }
                
                Boolean campusCodeForceUppercase = dds.getAttributeForceUppercase(AccountDescription.class, KFSPropertyConstants.CAMPUS_CODE );
                if (StringUtils.isNotBlank(campusCode) && campusCodeForceUppercase != null && campusCodeForceUppercase.booleanValue() == true ) {
                    campusCode = campusCode.toUpperCase();
                }   
                
                Map pkMap = new HashMap();
                pkMap.put("campusCode", campusCode);
                pkMap.put("buildingCode", buildingCode);                

                Building building = (Building) getBoService().findByPrimaryKey(Building.class, pkMap);
                if (building == null) {
                    putFieldError("accountDescription.campusCode", KFSKeyConstants.ERROR_EXISTENCE, campusCode);
                    putFieldError("accountDescription.buildingCode", KFSKeyConstants.ERROR_EXISTENCE, buildingCode);
                    success &= false;
                }
            }
                } else {
                    
                    // if sub_fund_grp_cd is NOT 'PFCMR', campus code should NOT be entered
                    if (!StringUtils.isBlank(campusCode)) {
                        putFieldError("accountDescription.campusCode", KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_NONCAMS_SUBFUNDGROUP_WITH_CAMPUS_CD_FOR_BLDG, subFundGroupCode );
                        success &= false;
                    }
    
                    // if sub_fund_grp_cd is NOT 'PFCMR' then bldg_cd should NOT be entered
                    if (!StringUtils.isBlank(buildingCode)) {
                        putFieldError("accountDescription.buildingCode", KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_NONCAMS_SUBFUNDGROUP_WITH_BUILDING_CD, subFundGroupCode);
                        success &= false;
                    }   
                }            
            }
        
        }

        return success;
    }

    public void setGeneralLedgerPendingEntryService(GeneralLedgerPendingEntryService generalLedgerPendingEntryService) {
        this.generalLedgerPendingEntryService = generalLedgerPendingEntryService;
    }

    public void setLaborLedgerPendingEntryService(LaborLedgerPendingEntryService laborLedgerPendingEntryService) {
        this.laborLedgerPendingEntryService = laborLedgerPendingEntryService;
    }

    public void setBalanceService(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    /**
     * Sets the accountService attribute value.
     * 
     * @param accountService The accountService to set.
     */
    public final void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }
    
    protected boolean checkCGFieldNotFilledIn(Account account, String propertyName) {
        boolean success = true;
        Object value = ObjectUtils.getPropertyValue(account, propertyName);
        if ((value instanceof String && !StringUtils.isBlank(value.toString())) || (value != null)) {
            success = false;
            putFieldError(propertyName, KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_CG_FIELDS_FILLED_FOR_NON_CG_ACCOUNT, new String[] { account.getSubFundGroupCode() });
        }
        
        return success;
    }

}