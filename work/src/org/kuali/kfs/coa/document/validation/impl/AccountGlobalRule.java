/*
 * Copyright 2007 The Kuali Foundation.
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DictionaryValidationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.AccountGlobal;
import org.kuali.module.chart.bo.AccountGlobalDetail;
import org.kuali.module.chart.bo.ChartUser;
import org.kuali.module.chart.bo.SubFundGroup;
import org.kuali.module.chart.service.OrganizationService;
import org.kuali.module.chart.service.SubFundGroupService;

/**
 * This class represents the business rules for the maintenance of {@link AccountGlobal} business objects
 */
public class AccountGlobalRule extends GlobalDocumentRuleBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountGlobalRule.class);

    private static final String GENERAL_FUND_CD = "GF";
    private static final String RESTRICTED_FUND_CD = "RF";
    private static final String ENDOWMENT_FUND_CD = "EN";
    private static final String PLANT_FUND_CD = "PF";

    private static final String RESTRICTED_CD_RESTRICTED = "R";
    private static final String RESTRICTED_CD_UNRESTRICTED = "U";
    private static final String RESTRICTED_CD_TEMPORARILY_RESTRICTED = "T";

    private static final String SUB_FUND_GROUP_MEDICAL_PRACTICE_FUNDS = "MPRACT";

    private AccountGlobal newAccountGlobal;
    private Timestamp today;

    public AccountGlobalRule() {
        super();
    }

    /**
     * This method sets the convenience objects like newAccountGlobal and oldAccount, so you have short and easy handles to the new
     * and old objects contained in the maintenance document. It also calls the BusinessObjectBase.refresh(), which will attempt to
     * load all sub-objects from the DB by their primary keys, if available.
     */
    @Override
    public void setupConvenienceObjects() {

        // setup newDelegateGlobal convenience objects, make sure all possible sub-objects are populated
        newAccountGlobal = (AccountGlobal) super.getNewBo();
        today = getDateTimeService().getCurrentTimestamp();
        today.setTime(DateUtils.truncate(today, Calendar.DAY_OF_MONTH).getTime()); // remove any time components
    }

    /**
     * This method checks the following rules: checkEmptyValues checkGeneralRules checkContractsAndGrants checkExpirationDate
     * checkOnlyOneChartErrorWrapper checkFiscalOfficerIsValidKualiUser but does not fail if any of them fail (this only happens on
     * routing)
     * 
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {

        LOG.info("processCustomSaveDocumentBusinessRules called");
        setupConvenienceObjects();

        checkEmptyValues();
        checkGeneralRules(document);
        checkOrganizationValidity(newAccountGlobal);
        checkContractsAndGrants();
        checkExpirationDate(document);
        checkOnlyOneChartErrorWrapper(newAccountGlobal.getAccountGlobalDetails());
        checkFiscalOfficerIsValidKualiUser(newAccountGlobal.getAccountFiscalOfficerSystemIdentifier());
        // checkFundGroup(document);
        // checkSubFundGroup(document);

        // Save always succeeds, even if there are business rule failures
        return true;
    }

    /**
     * This method checks the following rules: checkEmptyValues checkGeneralRules checkContractsAndGrants checkExpirationDate
     * checkOnlyOneChartErrorWrapper checkFiscalOfficerIsValidKualiUser but does fail if any of these rule checks fail
     * 
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {

        LOG.info("processCustomRouteDocumentBusinessRules called");
        setupConvenienceObjects();

        // default to success
        boolean success = true;

        success &= checkEmptyValues();
        success &= checkGeneralRules(document);
        success &= checkContractsAndGrants();
        success &= checkExpirationDate(document);
        success &= checkAccountDetails(document, newAccountGlobal.getAccountGlobalDetails());
        if (!StringUtils.isEmpty(newAccountGlobal.getAccountFiscalOfficerSystemIdentifier())) {
            success &= checkFiscalOfficerIsValidKualiUser(newAccountGlobal.getAccountFiscalOfficerSystemIdentifier());
        }
        // success &= checkFundGroup(document);
        // success &= checkSubFundGroup(document);

        return success;
    }

    /**
     * This method loops through the list of {@link AccountGlobalDetail}s and passes them off to checkAccountDetails for further
     * rule analysis One rule it does check is checkOnlyOneChartErrorWrapper
     * 
     * @param document
     * @param details
     * @return true if the collection of {@link AccountGlobalDetail}s passes the sub-rules
     */
    public boolean checkAccountDetails(MaintenanceDocument document, List<AccountGlobalDetail> details) {
        boolean success = true;

        // check if there are any accounts
        if (details.size() == 0) {

            putFieldError(KFSConstants.MAINTENANCE_ADD_PREFIX + "accountGlobalDetails.accountNumber", KFSKeyConstants.ERROR_DOCUMENT_GLOBAL_ACCOUNT_NO_ACCOUNTS);

            success = false;
        }
        else {
            // check each account
            int index = 0;
            for (AccountGlobalDetail dtl : details) {
                String errorPath = MAINTAINABLE_ERROR_PREFIX + "accountGlobalDetails[" + index + "]";
                GlobalVariables.getErrorMap().addToErrorPath(errorPath);
                success &= checkAccountDetails(dtl);
                GlobalVariables.getErrorMap().removeFromErrorPath(errorPath);
                index++;
            }
            success &= checkOnlyOneChartErrorWrapper(details);
        }

        return success;
    }

    /**
     * This method ensures that each {@link AccountGlobalDetail} is valid and has a valid account number
     * 
     * @param dtl
     * @return true if the detail object contains a valid account
     */
    public boolean checkAccountDetails(AccountGlobalDetail dtl) {
        boolean success = true;
        int originalErrorCount = GlobalVariables.getErrorMap().getErrorCount();
        getDictionaryValidationService().validateBusinessObject(dtl);
        if (StringUtils.isNotBlank(dtl.getAccountNumber()) && StringUtils.isNotBlank(dtl.getChartOfAccountsCode())) {
            dtl.refreshReferenceObject("account");
            if (dtl.getAccount() == null) {
                GlobalVariables.getErrorMap().putError("accountNumber", KFSKeyConstants.ERROR_DOCUMENT_GLOBAL_ACCOUNT_INVALID_ACCOUNT, new String[] { dtl.getChartOfAccountsCode(), dtl.getAccountNumber() });
            }
        }
        success &= GlobalVariables.getErrorMap().getErrorCount() == originalErrorCount;

        return success;
    }

    /**
     * This method checks the basic rules for empty reference key values on a continuation account and an income stream account
     * 
     * @return true if no empty values or partially filled out reference keys
     */
    protected boolean checkEmptyValues() {

        LOG.info("checkEmptyValues called");

        boolean success = true;

        // this set confirms that all fields which are grouped (ie, foreign keys of a referenc
        // object), must either be none filled out, or all filled out.
        success &= checkForPartiallyFilledOutReferenceForeignKeys("continuationAccount");
        success &= checkForPartiallyFilledOutReferenceForeignKeys("incomeStreamAccount");

        return success;
    }

    /**
     * This method checks some of the general business rules associated with this document Such as: valid user for fiscal officer,
     * supervisor or account manager (and not the same individual) are they trying to use an expired continuation account
     * 
     * @param maintenanceDocument
     * @return false on rules violation
     */
    protected boolean checkGeneralRules(MaintenanceDocument maintenanceDocument) {

        LOG.info("checkGeneralRules called");
        UniversalUser fiscalOfficer = newAccountGlobal.getAccountFiscalOfficerUser();
        UniversalUser accountManager = newAccountGlobal.getAccountManagerUser();
        UniversalUser accountSupervisor = newAccountGlobal.getAccountSupervisoryUser();

        boolean success = true;

        // the employee type for fiscal officer, account manager, and account supervisor must be 'P' – professional.
        success &= checkUserStatusAndType("accountFiscalOfficerUser.personUserIdentifier", fiscalOfficer);
        success &= checkUserStatusAndType("accountSupervisoryUser.personUserIdentifier", accountSupervisor);
        success &= checkUserStatusAndType("accountManagerUser.personUserIdentifier", accountManager);

        // the supervisor cannot be the same as the fiscal officer or account manager.
        if (isSupervisorSameAsFiscalOfficer(newAccountGlobal)) {
            success &= false;
            putFieldError("accountsSupervisorySystemsIdentifier", KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_ACCT_SUPER_CANNOT_BE_FISCAL_OFFICER);
        }
        if (isSupervisorSameAsManager(newAccountGlobal)) {
            success &= false;
            putFieldError("accountManagerSystemIdentifier", KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_ACCT_SUPER_CANNOT_BE_ACCT_MGR);
        }

        // disallow continuation account being expired
        if (isContinuationAccountExpired(newAccountGlobal)) {
            success &= false;
            putFieldError("continuationAccountNumber", KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_ACCOUNT_EXPIRED_CONTINUATION);
        }

        // loop over change detail objects to test if the supervisor/FO/mgr restrictions are in place
        // only need to do this check if the entered information does not already violate the rules
        if (!isSupervisorSameAsFiscalOfficer(newAccountGlobal) && !isSupervisorSameAsManager(newAccountGlobal)) {
            success &= checkAllAccountUsers(newAccountGlobal, fiscalOfficer, accountManager, accountSupervisor);
        }

        return success;
    }

    /**
     * This method checks to make sure that if the users are filled out (fiscal officer, supervisor, manager) that they are not the
     * same individual Only need to check this if these are new users that override existing users on the {@link Account} object
     * 
     * @param doc
     * @param newFiscalOfficer
     * @param newManager
     * @param newSupervisor
     * @return true if the users are either not changed or pass the sub-rules
     */
    protected boolean checkAllAccountUsers(AccountGlobal doc, UniversalUser newFiscalOfficer, UniversalUser newManager, UniversalUser newSupervisor) {
        boolean success = true;

        if (LOG.isDebugEnabled()) {
            LOG.debug("newSupervisor: " + newSupervisor);
            LOG.debug("newFiscalOfficer: " + newFiscalOfficer);
            LOG.debug("newManager: " + newManager);
        }
        // only need to do this check if at least one of the user fields is
        // non null
        if (newSupervisor != null || newFiscalOfficer != null || newManager != null) {
            // loop over all AccountGlobalDetail records
            int index = 0;
            for (AccountGlobalDetail detail : doc.getAccountGlobalDetails()) {
                success &= checkAccountUsers(detail, newFiscalOfficer, newManager, newSupervisor, index);
                index++;
            }
        }

        return success;
    }

    /**
     * This method checks that the new users (fiscal officer, supervisor, manager) are not the same individual for the
     * {@link Account} being changed (contained in the {@link AccountGlobalDetail})
     * 
     * @param detail - where the Account information is stored
     * @param newFiscalOfficer
     * @param newManager
     * @param newSupervisor
     * @param index - for storing the error line
     * @return true if the new users pass this sub-rule
     */
    protected boolean checkAccountUsers(AccountGlobalDetail detail, UniversalUser newFiscalOfficer, UniversalUser newManager, UniversalUser newSupervisor, int index) {
        boolean success = true;

        // only need to do this check if at least one of the user fields is non null
        if (newSupervisor != null || newFiscalOfficer != null || newManager != null) {
            // loop over all AccountGlobalDetail records
            detail.refreshReferenceObject("account");
            Account account = detail.getAccount();
            if (account != null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("old-Supervisor: " + account.getAccountSupervisoryUser());
                    LOG.debug("old-FiscalOfficer: " + account.getAccountFiscalOfficerUser());
                    LOG.debug("old-Manager: " + account.getAccountManagerUser());
                }
                // only need to check if they are not being overridden by the change document
                if (newSupervisor != null && newSupervisor.getPersonUniversalIdentifier() != null) {
                    if (areTwoUsersTheSame(newSupervisor, account.getAccountFiscalOfficerUser())) {
                        success = false;
                        putFieldError("accountGlobalDetails[" + index + "].accountNumber", KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_ACCT_SUPER_CANNOT_EQUAL_EXISTING_FISCAL_OFFICER, new String[] { account.getAccountFiscalOfficerUserPersonUserIdentifier(), "Fiscal Officer", detail.getAccountNumber() });
                    }
                    if (areTwoUsersTheSame(newSupervisor, account.getAccountManagerUser())) {
                        success = false;
                        putFieldError("accountGlobalDetails[" + index + "].accountNumber", KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_ACCT_SUPER_CANNOT_EQUAL_EXISTING_ACCT_MGR, new String[] { account.getAccountManagerUserPersonUserIdentifier(), "Account Manager", detail.getAccountNumber() });
                    }
                }
                if (newManager != null && newManager.getPersonUniversalIdentifier() != null) {
                    if (areTwoUsersTheSame(newManager, account.getAccountSupervisoryUser())) {
                        success = false;
                        putFieldError("accountGlobalDetails[" + index + "].accountNumber", KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_ACCT_MGR_CANNOT_EQUAL_EXISTING_ACCT_SUPERVISOR, new String[] { account.getAccountSupervisoryUserPersonUserIdentifier(), "Account Supervisor", detail.getAccountNumber() });
                    }
                }
                if (newFiscalOfficer != null && newFiscalOfficer.getPersonUniversalIdentifier() != null) {
                    if (areTwoUsersTheSame(newFiscalOfficer, account.getAccountSupervisoryUser())) {
                        success = false;
                        putFieldError("accountGlobalDetails[" + index + "].accountNumber", KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_FISCAL_OFFICER_CANNOT_EQUAL_EXISTING_ACCT_SUPERVISOR, new String[] { account.getAccountSupervisoryUserPersonUserIdentifier(), "Account Supervisor", detail.getAccountNumber() });
                    }
                }
            }
            else {
                LOG.warn("AccountGlobalDetail object has null account object:" + detail.getChartOfAccountsCode() + "-" + detail.getAccountNumber());
            }
        }

        return success;
    }

    /**
     * This method is a helper method for checking if the supervisor user is the same as the fiscal officer Calls
     * {@link AccountGlobalRule#areTwoUsersTheSame(UniversalUser, UniversalUser)}
     * 
     * @param accountGlobals
     * @return true if the two users are the same
     */
    protected boolean isSupervisorSameAsFiscalOfficer(AccountGlobal accountGlobals) {
        return areTwoUsersTheSame(accountGlobals.getAccountSupervisoryUser(), accountGlobals.getAccountFiscalOfficerUser());
    }

    /**
     * This method is a helper method for checking if the supervisor user is the same as the manager Calls
     * {@link AccountGlobalRule#areTwoUsersTheSame(UniversalUser, UniversalUser)}
     * 
     * @param accountGlobals
     * @return true if the two users are the same
     */
    protected boolean isSupervisorSameAsManager(AccountGlobal accountGlobals) {
        return areTwoUsersTheSame(accountGlobals.getAccountSupervisoryUser(), accountGlobals.getAccountManagerUser());
    }

    /**
     * This method checks to see if two users are the same BusinessObject using their identifiers
     * 
     * @param user1
     * @param user2
     * @return true if these two users are the same
     */
    protected boolean areTwoUsersTheSame(UniversalUser user1, UniversalUser user2) {
        if (ObjectUtils.isNull(user1)) {
            return false;
        }
        if (ObjectUtils.isNull(user2)) {
            return false;
        }
        // not a real person object - fail the comparison
        if (user1.getPersonUniversalIdentifier() == null || user2.getPersonUniversalIdentifier() == null) {
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
     * This method checks to see if the user passed in is of the type requested. If so, it returns true. If not, it returns false,
     * and adds an error to the GlobalErrors.
     * 
     * @param propertyName
     * @param user - UniversalUser to be tested
     * @return true if user is of the requested employee type, false if not, true if the user object is null
     */
    protected boolean checkUserStatusAndType(String propertyName, UniversalUser user) {

        boolean success = true;

        // if the user isnt populated, exit with success
        // the actual existence check is performed in the general rules so not testing here
        if (ObjectUtils.isNull(user) || user.getPersonUniversalIdentifier() == null) {
            return success;
        }

        // user must be of the allowable statuses (A - Active)
        if (!SpringContext.getBean(ParameterService.class).getParameterEvaluator(Account.class, KFSConstants.ChartApcParms.ACCOUNT_USER_EMP_STATUSES, user.getEmployeeStatusCode()).evaluationSucceeds()) {
            success = false;
            putFieldError(propertyName, KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_ACTIVE_REQD_FOR_EMPLOYEE, getDdService().getAttributeLabel(Account.class, propertyName));
        }

        // user must be of the allowable types (P - Professional)
        if (!SpringContext.getBean(ParameterService.class).getParameterEvaluator(Account.class, KFSConstants.ChartApcParms.ACCOUNT_USER_EMP_TYPES, user.getEmployeeTypeCode()).evaluationSucceeds()) {
            success = false;
            putFieldError(propertyName, KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_PRO_TYPE_REQD_FOR_EMPLOYEE, getDdService().getAttributeLabel(Account.class, propertyName));
        }

        return success;
    }

    /**
     * This method insures the fiscal officer is a valid Kuali User
     * 
     * @param fiscalOfficerUserId
     * @return true if fiscal officer is a valid KualiUser
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
     * This method checks to see if any expiration date field rules were violated Loops through each detail object and calls
     * {@link AccountGlobalRule#checkExpirationDate(MaintenanceDocument, AccountGlobalDetail)}
     * 
     * @param maintenanceDocument
     * @return false on rules violation
     */
    protected boolean checkExpirationDate(MaintenanceDocument maintenanceDocument) {
        LOG.info("checkExpirationDate called");

        boolean success = true;
        Timestamp newExpDate = newAccountGlobal.getAccountExpirationDate();

        // If creating a new account if acct_expiration_dt is set and the fund_group is not "CG" then
        // the acct_expiration_dt must be changed to a date that is today or later
        if (ObjectUtils.isNotNull(newExpDate)) {
            if (ObjectUtils.isNotNull(newAccountGlobal.getSubFundGroup())) {
                if (!SpringContext.getBean(SubFundGroupService.class).isForContractsAndGrants(newAccountGlobal.getSubFundGroup())) {
                    if (!newExpDate.after(today) && !newExpDate.equals(today)) {
                        putGlobalError(KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_EXP_DATE_TODAY_LATER);
                        success &= false;
                    }
                }
            }
        }

        // a continuation account is required if the expiration date is completed.
        success &= checkContinuationAccount(maintenanceDocument, newExpDate);

        for (AccountGlobalDetail detail : newAccountGlobal.getAccountGlobalDetails()) {
            success &= checkExpirationDate(maintenanceDocument, detail);
        }
        return success;
    }

    /**
     * This method checks to see if any expiration date field rules were violated in relation to the given detail record
     * 
     * @param maintenanceDocument
     * @param detail - the account detail we are investigating
     * @return false on rules violation
     */
    protected boolean checkExpirationDate(MaintenanceDocument maintenanceDocument, AccountGlobalDetail detail) {
        boolean success = true;
        Timestamp newExpDate = newAccountGlobal.getAccountExpirationDate();

        // load the object by keys
        Account account = (Account) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Account.class, detail.getPrimaryKeys());
        if (account != null) {
            Timestamp oldExpDate = account.getAccountExpirationDate();

            // When updating an account expiration date, the date must be today or later
            // (except for C&G accounts). Only run this test if this maint doc
            // is an edit doc
            if (isUpdatedExpirationDateInvalid(account, newAccountGlobal)) {
                putFieldError("accountExpirationDate", KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_EXP_DATE_TODAY_LATER);
                success &= false;
            }

            // If creating a new account if acct_expiration_dt is set and the fund_group is not "CG" then
            // the acct_expiration_dt must be changed to a date that is today or later
            if (ObjectUtils.isNotNull(newExpDate) && ObjectUtils.isNull(newAccountGlobal.getSubFundGroup())) {
                if (ObjectUtils.isNotNull(account.getSubFundGroup())) {
                    if (!account.isForContractsAndGrants()) {
                        if (!newExpDate.after(today) && !newExpDate.equals(today)) {
                            putGlobalError(KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_EXP_DATE_TODAY_LATER);
                            success &= false;
                        }
                    }
                }
            }
            // acct_expiration_dt can not be before acct_effect_dt
            Timestamp effectiveDate = account.getAccountEffectiveDate();
            if (ObjectUtils.isNotNull(effectiveDate) && ObjectUtils.isNotNull(newExpDate)) {
                if (newExpDate.before(effectiveDate)) {
                    putGlobalError(KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_EXP_DATE_CANNOT_BE_BEFORE_EFFECTIVE_DATE);
                    success &= false;
                }
            }
        }

        return success;
    }

    /*
     * protected boolean checkAccountExpirationDateValidTodayOrEarlier(Account newAccount) { // get today's date, with no time
     * component Timestamp todaysDate = getDateTimeService().getCurrentTimestamp();
     * todaysDate.setTime(DateUtils.truncate(todaysDate, Calendar.DAY_OF_MONTH).getTime()); // TODO: convert this to using Wes'
     * kuali DateUtils once we're using Date's instead of Timestamp // get the expiration date, if any Timestamp expirationDate =
     * newAccount.getAccountExpirationDate(); if (ObjectUtils.isNull(expirationDate)) { putFieldError("accountExpirationDate",
     * KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_ACCT_CANNOT_BE_CLOSED_EXP_DATE_INVALID); return false; } // when closing an account,
     * the account expiration date must be the current date or earlier expirationDate.setTime(DateUtils.truncate(expirationDate,
     * Calendar.DAY_OF_MONTH).getTime()); if (expirationDate.after(todaysDate)) { putFieldError("accountExpirationDate",
     * KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_ACCT_CANNOT_BE_CLOSED_EXP_DATE_INVALID); return false; } return true; }
     */

    /**
     * This method checks to see if the updated expiration is not a valid one Only gets checked for specific {@link SubFundGroup}s
     * 
     * @param oldAccount
     * @param newAccountGlobal
     * @return true if date has changed and is invalid
     */
    protected boolean isUpdatedExpirationDateInvalid(Account oldAccount, AccountGlobal newAccountGlobal) {

        Timestamp oldExpDate = oldAccount.getAccountExpirationDate();
        Timestamp newExpDate = newAccountGlobal.getAccountExpirationDate();

        // When updating an account expiration date, the date must be today or later
        // (except for C&G accounts). Only run this test if this maint doc
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

        // if a subFundGroup isnt present, we cannot continue the testing
        SubFundGroup subFundGroup = newAccountGlobal.getSubFundGroup();
        if (ObjectUtils.isNull(subFundGroup)) {
            return false;
        }

        // get the fundGroup code
        String fundGroupCode = newAccountGlobal.getSubFundGroup().getFundGroupCode().trim();

        // if the account is part of the CG fund group, then this rule does not
        // apply, so we're done
        if (SpringContext.getBean(SubFundGroupService.class).isForContractsAndGrants(newAccountGlobal.getSubFundGroup())) {
            return false;
        }

        // at this point, we know its not a CG fund group, so we must apply the rule

        // expirationDate must be today or later than today (cannot be before today)
        if (newExpDate.equals(today) || newExpDate.after(today)) {
            return false;
        }
        else
            return true;
    }


    /**
     * This method tests whether the continuation account entered (if any) has expired or not.
     * 
     * @param accountGlobals
     * @return true if the continuation account has expired
     */
    protected boolean isContinuationAccountExpired(AccountGlobal accountGlobals) {

        boolean result = false;

        String chartCode = accountGlobals.getContinuationFinChrtOfAcctCd();
        String accountNumber = accountGlobals.getContinuationAccountNumber();

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

    /**
     * This method checks to see if any Contracts and Grants business rules were violated
     * 
     * @return false on rules violation
     */
    protected boolean checkContractsAndGrants() {

        LOG.info("checkContractsAndGrants called");

        boolean success = true;

        // Income Stream account is required if this account is CG fund group,
        // or GF (general fund) fund group (with some exceptions)
        success &= checkCgIncomeStreamRequired(newAccountGlobal);

        return success;
    }

    /**
     * This method checks to see if the contracts and grants income stream account is required
     * 
     * @param accountGlobals
     * @return false if it is required (and not entered) or invalid/inactive
     */
    protected boolean checkCgIncomeStreamRequired(AccountGlobal accountGlobals) {

        boolean result = true;
        boolean required = false;

        // if the subFundGroup object is null, we cant test, so exit
        if (ObjectUtils.isNull(accountGlobals.getSubFundGroup())) {
            return result;
        }

        // retrieve the subfundcode and fundgroupcode
        String subFundGroupCode = accountGlobals.getSubFundGroupCode().trim();
        String fundGroupCode = accountGlobals.getSubFundGroup().getFundGroupCode().trim();

        // if this is a CG fund group, then its required
        if (SpringContext.getBean(SubFundGroupService.class).isForContractsAndGrants(accountGlobals.getSubFundGroup())) {
            required = true;
        }

        // if this is a general fund group, then its required
        else if (GENERAL_FUND_CD.equalsIgnoreCase(fundGroupCode)) {
            // unless its part of the MPRACT subfundgroup
            if (!SUB_FUND_GROUP_MEDICAL_PRACTICE_FUNDS.equalsIgnoreCase(subFundGroupCode)) {
                required = true;
            }
        }

        // if the income stream account is not required, then we're done
        if (!required) {
            return result;
        }

        // make sure both coaCode and accountNumber are filled out
        result &= checkEmptyBOField("incomeStreamAccountNumber", accountGlobals.getIncomeStreamAccountNumber(), "When Fund Group is CG or GF, Income Stream Account Number");
        result &= checkEmptyBOField("incomeStreamFinancialCoaCode", accountGlobals.getIncomeStreamFinancialCoaCode(), "When Fund Group is CG or GF, Income Stream Chart Of Accounts Code");

        // if both fields arent present, then we're done
        if (result == false) {
            return result;
        }

        // do an existence/active test
        DictionaryValidationService dvService = super.getDictionaryValidationService();
        boolean referenceExists = dvService.validateReferenceExists(accountGlobals, "incomeStreamAccount");
        if (!referenceExists) {
            putFieldError("incomeStreamAccount", KFSKeyConstants.ERROR_EXISTENCE, "Income Stream Account: " + accountGlobals.getIncomeStreamFinancialCoaCode() + "-" + accountGlobals.getIncomeStreamAccountNumber());
            result &= false;
        }

        return result;
    }

    /**
     * This method calls checkAccountDetails checkExpirationDate checkOnlyOneChartAddLineErrorWrapper whenever a new
     * {@link AccountGlobalDetail} is added to this global
     * 
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomAddCollectionLineBusinessRules(org.kuali.core.document.MaintenanceDocument,
     *      java.lang.String, org.kuali.core.bo.PersistableBusinessObject)
     */
    public boolean processCustomAddCollectionLineBusinessRules(MaintenanceDocument document, String collectionName, PersistableBusinessObject bo) {
        AccountGlobalDetail detail = (AccountGlobalDetail) bo;
        boolean success = true;

        success &= checkAccountDetails(detail);
        success &= checkExpirationDate(document, detail);
        success &= checkOnlyOneChartAddLineErrorWrapper(detail, newAccountGlobal.getAccountGlobalDetails());

        return success;
    }

    /**
     * This method validates that a continuation account is required and that the values provided exist
     * 
     * @param document An instance of the maintenance document being validated.
     * @param newExpDate The expiration date assigned to the account being validated for submission.
     * @return True if the continuation account values are valid for the associated account, false otherwise.
     */
    protected boolean checkContinuationAccount(MaintenanceDocument document, Timestamp newExpDate) {
        LOG.info("checkContinuationAccount called");

        boolean result = true;
        boolean continuationAccountIsValid = true;

        // make sure both coaCode and accountNumber are filled out
        if (ObjectUtils.isNotNull(newExpDate)) {
            if (!checkEmptyValue(newAccountGlobal.getContinuationAccountNumber())) {
                putFieldError("continuationAccountNumber", KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_CONTINUATION_ACCT_REQD_IF_EXP_DATE_COMPLETED);
                continuationAccountIsValid = false;
            }
            if (!checkEmptyValue(newAccountGlobal.getContinuationFinChrtOfAcctCd())) {
                putFieldError("continuationFinChrtOfAcctCd", KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_CONTINUATION_FINCODE_REQD_IF_EXP_DATE_COMPLETED);
                continuationAccountIsValid = false;
            }
        }

        // if both fields aren't present, then we're done
        if (continuationAccountIsValid && ObjectUtils.isNotNull(newAccountGlobal.getContinuationAccountNumber()) && ObjectUtils.isNotNull(newAccountGlobal.getContinuationFinChrtOfAcctCd())) {
            // do an existence/active test
            DictionaryValidationService dvService = super.getDictionaryValidationService();
            boolean referenceExists = dvService.validateReferenceExists(newAccountGlobal, "continuationAccount");
            if (!referenceExists) {
                putFieldError("continuationAccountNumber", KFSKeyConstants.ERROR_EXISTENCE, "Continuation Account: " + newAccountGlobal.getContinuationFinChrtOfAcctCd() + "-" + newAccountGlobal.getContinuationAccountNumber());
                continuationAccountIsValid = false;
            }
        }

        if (continuationAccountIsValid) {
            result = true;
        }
        else {
            List<AccountGlobalDetail> gAcctDetails = newAccountGlobal.getAccountGlobalDetails();
            for (AccountGlobalDetail detail : gAcctDetails) {
                if (null != detail.getAccountNumber() && null != newAccountGlobal.getContinuationAccountNumber()) {
                    result &= detail.getAccountNumber().equals(newAccountGlobal.getContinuationAccountNumber());
                    result &= detail.getChartOfAccountsCode().equals(newAccountGlobal.getContinuationFinChrtOfAcctCd());
                }
            }
        }

        return result;
    }
    
    /**
     * Validate that the object code on the form (if entered) is valid for all charts used in the detail sections.
     * 
     * @param acctGlobal
     * @return
     */
    protected boolean checkOrganizationValidity( AccountGlobal acctGlobal ) {
        boolean result = true;
        
        // check that an org has been entered
        if ( StringUtils.isNotBlank( acctGlobal.getOrganizationCode() ) ) {           
            // get all distinct charts
            HashSet<String> charts = new HashSet<String>(10); 
            for ( AccountGlobalDetail acct : acctGlobal.getAccountGlobalDetails() ) {
                charts.add( acct.getChartOfAccountsCode() );
            }
            OrganizationService orgService = SpringContext.getBean(OrganizationService.class);
            // test for an invalid organization
            for ( String chartCode : charts ) {
                if ( StringUtils.isNotBlank(chartCode) ) {
                    if ( null == orgService.getByPrimaryIdWithCaching( chartCode, acctGlobal.getOrganizationCode() ) ) {
                        result = false;
                        putFieldError("organizationCode", KFSKeyConstants.ERROR_DOCUMENT_GLOBAL_ACCOUNT_INVALID_ORG, new String[] { chartCode, acctGlobal.getOrganizationCode() } );
                        break;
                    }
                }
            }
        }
                
        return result;
    }
}
