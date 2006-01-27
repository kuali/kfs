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
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.KeyConstants;
import org.kuali.core.bo.Building;
import org.kuali.core.bo.user.KualiUser;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.SubFundGroup;
import org.kuali.module.financial.rules.KualiParameterRule;

/**
 * Business rule(s) applicable to AccountMaintenance documents.
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class AccountRule extends MaintenanceDocumentRuleBase {
    
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountRule.class);
    
    private static final String ACCT_BUDGET_CODES_RESTRICT = "Account.BudgetCodesRestriction";
    private static final String ACCT_PREFIX_RESTRICTION = "Account.PrefixRestriction";
    private static final String ACCT_CAPITAL_SUBFUNDGROUP = "Account.CapitalSubFundGroup";

    private static final String CONTRACTS_GRANTS_CD = "CG";
    private static final String GENERAL_FUND_CD = "GF";
    private static final String RESTRICTED_FUND_CD = "RF";
    private static final String ENDOWMENT_FUND_CD = "EN";
    private static final String PLANT_FUND_CD = "PF";
    
    private static final String RESTRICTED_CD_RESTRICTED = "R";
    private static final String RESTRICTED_CD_UNRESTRICTED = "U";
    private static final String RESTRICTED_CD_TEMPORARILY_RESTRICTED = "T";
    private static final String RESTRICTED_CD_NOT_APPLICABLE = "N";
    
    private static final String EMPLOYEE_TYPE_PROFESSIONAL = "P";
    
    private static final String SUB_FUND_GROUP_MEDICAL_PRACTICE_FUNDS = "MPRACT";
    
    private static final String BUDGET_RECORDING_LEVEL_MIXED = "M";
    
    private KualiConfigurationService configService;
    private BusinessObjectService boService;
    private KualiParameterRule validBudgetRule;
    private Account oldAccount;
    private Account newAccount;
    private boolean ruleValuesSetup;
    
    public AccountRule() {
        ruleValuesSetup = false;
        configService = SpringServiceLocator.getKualiConfigurationService();
        boService = SpringServiceLocator.getBusinessObjectService();
    }
    
    /**
     * This method initializes our rule values for Account Maintenance
     * @param document
     */
    private void initializeRuleValues(MaintenanceDocument document) {
        if(!ruleValuesSetup) {
            validBudgetRule = configService.getApplicationParameterRule(CHART_MAINTENANCE_EDOC, ACCT_BUDGET_CODES_RESTRICT);
        }
    }
    
    /**
     * 
     * This method sets the convenience objects like newAccount and oldAccount, so you
     * have short and easy handles to the new and old objects contained in the 
     * maintenance document.
     * 
     * It also calls the BusinessObjectBase.refresh(), which will attempt to load 
     * all sub-objects from the DB by their primary keys, if available.
     * 
     * @param document - the maintenanceDocument being evaluated
     * 
     */
    private void setupConvenienceObjects(MaintenanceDocument document) {
        
        //	setup oldAccount convenience objects, make sure all possible sub-objects are populated
        oldAccount = (Account) document.getOldMaintainableObject().getBusinessObject();
        oldAccount.refresh();

        //	setup newAccount convenience objects, make sure all possible sub-objects are populated
        newAccount = (Account) document.getNewMaintainableObject().getBusinessObject();
        newAccount.refresh();
    }
    
    /**
     * 
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.core.document.MaintenanceDocument)
     */
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        
        setupConvenienceObjects(document);
        initializeRuleValues(document);
        
        checkEmptyValues(document);
        
        //	Save always succeeds, even if there are business rule failures
        return true;
    }
    
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        LOG.info("processCustomRouteDocumentBusinessRules called");
        setupConvenienceObjects(document);
        initializeRuleValues(document);
        
        //	default to success
        boolean success = true;
        
        success &= checkEmptyValues(document);
        success &= checkGeneralRules(document);
        success &= checkCloseAccount(document);
        success &= checkContractsAndGrants(document);
        success &= checkExpirationDate(document);
        success &= checkFundGroup(document);
        success &= checkSubFundGroup(document);
        
        return success;
    }

    /**
     * 
     * This method checks the basic rules for empty values in an account and associated
     * objects with this account
     * @param maintenanceDocument
     * @return
     */
    private boolean checkEmptyValues(MaintenanceDocument maintenanceDocument) {
        boolean success = true;
        
        success &= checkEmptyBOField("chartOfAccountsCode", newAccount.getChartOfAccountsCode(), "Chart of Accounts Code");
        success &= checkEmptyBOField("accountNumber", newAccount.getAccountNumber(), "Account Number");
        success &= checkEmptyBOField("accountName", newAccount.getAccountName(), "Account Name");
        success &= checkEmptyBOField("organizationCode", newAccount.getOrganizationCode(), "Organization Code");
        success &= checkEmptyBOField("accountPhysicalCampusCode", newAccount.getAccountPhysicalCampusCode(), "Campus Code");
        success &= checkEmptyBOField("accountEffectiveDate", newAccount.getAccountEffectiveDate(), "Effective Date");
        success &= checkEmptyBOField("accountCityName", newAccount.getAccountCityName(), "City Name");
        success &= checkEmptyBOField("accountStateCode", newAccount.getAccountStateCode(), "State Code");
        success &= checkEmptyBOField("accountStreetAddress", newAccount.getAccountStreetAddress(), "Address");
        success &= checkEmptyBOField("accountZipCode", newAccount.getAccountZipCode(), "Zip Code");
        success &= checkEmptyBOField("budgetRecordingLevelCode", newAccount.getBudgetRecordingLevelCode(), "Budget Recording Level");
        success &= checkEmptyBOField("accountSufficientFundsCode", newAccount.getAccountSufficientFundsCode(), "Sufficient Funds Code");
        success &= checkEmptyBOField("subFundGroupCode", newAccount.getSubFundGroupCode(), "Sub Fund Group");
        success &= checkEmptyBOField("financialHigherEdFunctionCd", newAccount.getFinancialHigherEdFunctionCd(), "Higher Ed Function Code");
        success &= checkEmptyBOField("accountRestrictedStatusCode", newAccount.getAccountRestrictedStatusCode(), "Restricted Status Code");
        
        
        //	Certain C&G fields are required if the Account belongs to the CG Fund Group
        if (ObjectUtils.isNotNull(newAccount.getSubFundGroup())) {
	        if (newAccount.getSubFundGroup().getFundGroupCode().equalsIgnoreCase(CONTRACTS_GRANTS_CD)) {
		        success &= checkEmptyBOField("acctIndirectCostRcvyTypeCd", newAccount.getAcctIndirectCostRcvyTypeCd(), "ICR Type Code");
		        success &= checkEmptyBOField("financialIcrSeriesIdentifier", newAccount.getFinancialIcrSeriesIdentifier(), "ICR Series Identifier");
		        success &= checkEmptyBOField("indirectCostRcvyFinCoaCode", newAccount.getIndirectCostRcvyFinCoaCode(), "ICR Cost Recovery Chart of Accounts Code");
		        success &= checkEmptyBOField("indirectCostRecoveryAcctNbr", newAccount.getIndirectCostRecoveryAcctNbr(), "ICR Cost Recovery Account");
		        success &= checkEmptyBOField("cgCatlfFedDomestcAssistNbr", newAccount.getCgCatlfFedDomestcAssistNbr(), "C&G Domestic Assistance Number");
	        }
        }
        
        //	ObjectUtils.isNull() avoids null sub-objects that may or may not be proxied
        if (StringUtils.isEmpty(newAccount.getAccountFiscalOfficerSystemIdentifier())) {
            success &= checkEmptyBOField("accountFiscalOfficerSystemIdentifier", 
                    					newAccount.getAccountFiscalOfficerSystemIdentifier(), 
                    					"Account Fiscal Officer");
        }
        if (StringUtils.isEmpty(newAccount.getAccountManagerSystemIdentifier())) {
            success &= checkEmptyBOField("accountManagerSystemIdentifier", 
                    					newAccount.getAccountManagerSystemIdentifier(), 
                    					"Account Manager");
        }
        if (ObjectUtils.isNotNull(newAccount.getAccountsSupervisorySystemsIdentifier())) {
            success &= checkEmptyBOField("accountsSupervisorySystemsIdentifier", 
                    					newAccount.getAccountsSupervisorySystemsIdentifier(), 
                    					"Account Supervisor");
        }

        //	if the expiration date is earlier than today, account guidelines are not required.
        if (ObjectUtils.isNotNull(newAccount.getAccountGuideline())) {
            if (newAccount.getAccountExpirationDate() != null) {
	            if (newAccount.getAccountExpirationDate().after(new Timestamp(Calendar.getInstance().getTimeInMillis()))) {
		            success &= checkEmptyBOField("accountGuideline.accountExpenseGuidelineText", newAccount.getAccountGuideline().getAccountExpenseGuidelineText(), "Expense Guideline");
		            success &= checkEmptyBOField("accountGuideline.accountIncomeGuidelineText", newAccount.getAccountGuideline().getAccountIncomeGuidelineText(), "Income Guideline");
		            success &= checkEmptyBOField("accountGuideline.accountPurposeText", newAccount.getAccountGuideline().getAccountPurposeText(), "Account Purpose");
	            }
            }
        }
        return success;
    }
    
    /**
     * 
     * This method checks some of the general business rules associated with this document
     * @param maintenanceDocument
     * @return false on rules violation
     */
    private boolean checkGeneralRules(MaintenanceDocument maintenanceDocument) {

        KualiUser fiscalOfficer = newAccount.getAccountFiscalOfficerUser();
        KualiUser accountManager = newAccount.getAccountManagerUser();
        KualiUser accountSupervisor = newAccount.getAccountSupervisoryUser();
        
        boolean success = true;

        GlobalVariables.getErrorMap().addToErrorPath("newMaintainableObject");
        
           
        // Enforce institutionally specified restrictions on account number prefixes
        // (e.g. the account number cannot begin with a 3 or with 00.)
        // Only bother trying if there is an account string to test
        if (!StringUtils.isEmpty(newAccount.getAccountNumber())) {
            String[] illegalValues = configService.getApplicationParameterValues(CHART_MAINTENANCE_EDOC, ACCT_PREFIX_RESTRICTION);
            
            if (illegalValues != null) {
                for (int i = 0; i < illegalValues.length; i++) {
                    if (newAccount.getAccountNumber().startsWith(illegalValues[i])) {
                        success &= false;
                        putFieldError("accountNumber", 
                                KeyConstants.ERROR_DOCUMENT_ACCMAINT_ACCT_NMBR_NOT_ALLOWED + "(" + illegalValues[i] + ")", 
                                newAccount.getAccountNumber());
                    }
                }
            } else {
                LOG.warn("No Financial System Parameter found for CHART_MAINTENANCE_EDOC/ACCT_PREFIX_RESTRICTION");
            }
        }
        
        //only a FIS supervisor can reopen a closed account. (This is the central super user, not an account supervisor).
        //we need to get the old maintanable doc here
        if (maintenanceDocument.isEdit()) {
            if (oldAccount.isAccountClosedIndicator()) {
                if (!newAccount.isAccountClosedIndicator()) {
                    KualiUser thisUser = GlobalVariables.getUserSession().getKualiUser();
                    if (!thisUser.isSupervisorUser()) {
                        success &= false;
                        putFieldError("accountClosedIndicator", KeyConstants.ERROR_DOCUMENT_ACCMAINT_ONLY_SUPERVISORS_CAN_REOPEN);
                    }
                }
            }
        }
        
        //when a restricted status code of 'T' (temporarily restricted) is selected, a restricted status date must be supplied.
        if (!StringUtils.isEmpty(newAccount.getAccountRestrictedStatusCode())) {
	        if (newAccount.getAccountRestrictedStatusCode().equalsIgnoreCase(RESTRICTED_CD_TEMPORARILY_RESTRICTED)) {
	            if (newAccount.getAccountRestrictedStatusDate() == null) {
		            success &= false;
		            putFieldError("accountRestrictedStatusDate", KeyConstants.ERROR_DOCUMENT_ACCMAINT_RESTRICTED_STATUS_DT_REQ, newAccount.getAccountNumber());
	            }
	        }
        }
        
        // the fringe benefit account (otherwise known as the reportsToAccount) is required if 
        // the fringe benefit code is set to N. 
        // The fringe benefit code of the account designated to accept the fringes must be Y.
        if (!newAccount.isAccountsFringesBnftIndicator()) {
            if (StringUtils.isEmpty(newAccount.getReportsToAccountNumber())) {
                success &= false;
                putFieldError("reportsToAccount.accountNumber", KeyConstants.ERROR_DOCUMENT_ACCMAINT_RPTS_TO_ACCT_REQUIRED_IF_FRINGEBENEFIT_FALSE);
            }
            else {
                Account reportsToAccount = newAccount.getReportsToAccount();
                if (ObjectUtils.isNotNull(reportsToAccount)) {
                    if (!reportsToAccount.isAccountsFringesBnftIndicator()) {
                        success &= false;
                        putFieldError("reportsToAccount.accountNumber", KeyConstants.ERROR_DOCUMENT_ACCMAINT_RPTS_TO_ACCT_MUST_BE_FLAGGED_FRINGEBENEFIT, newAccount.getReportsToAccountNumber());
                    }
                }
            }
        }
        
        //the employee type for fiscal officer, account manager, and account supervisor must be 'P' – professional.
        success &= checkUserType("accountFiscalOfficerUser.personName", fiscalOfficer, EMPLOYEE_TYPE_PROFESSIONAL, "Fiscal Officer");
        success &= checkUserType("accountManagerUser.personName", accountManager, EMPLOYEE_TYPE_PROFESSIONAL, "Account Manager");
        success &= checkUserType("accountSupervisoryUser.personName", accountSupervisor, EMPLOYEE_TYPE_PROFESSIONAL, "Account Supervisor");
        
        //the supervisor cannot be the same as the fiscal officer or account manager.
        if (ObjectUtils.isNotNull(accountSupervisor)) {
            if (ObjectUtils.isNotNull(fiscalOfficer)) {
                if (accountSupervisor.equals(fiscalOfficer)) {
                    success &= false;
                    putGlobalError(KeyConstants.ERROR_DOCUMENT_ACCMAINT_ACCT_SUPER_CANNOT_BE_FISCAL_OFFICER);
                }
            }
            if (ObjectUtils.isNotNull(accountManager)) {
                if (accountSupervisor.equals(accountManager)) {
                    success &= false;
                    putGlobalError(KeyConstants.ERROR_DOCUMENT_ACCMAINT_ACCT_SUPER_CANNOT_BE_ACCT_MGR);
                }
            }
        }
        
        //valid values for the budget code are account, consolidation, level, object code, mixed, sub-account and no budget.
        if (ObjectUtils.isNotNull(newAccount.getBudgetRecordingLevelCode())) {
            if (validBudgetRule.failsRule(newAccount.getBudgetRecordingLevelCode())) {
                success &= false;
                putFieldError("budgetRecordingLevelCode", KeyConstants.ERROR_DOCUMENT_ACCMAINT_INVALID_BUDGET_RECORD_LVL_CD, newAccount.getBudgetRecordingLevelCode());
            }
        }
        
        // IMPORTANT NOTE: In this whole stretch of tests which follows where we're testing to make sure that subObject X 
        // is valid (ie, present in the its table) and active, we're cheating a little bit, to 
        // simplify the code.  This assumes that we've run the PersistenceService.retrieveNonKeyFields() 
        // on the object.  After this happens, if the String value for the subObject is valid, the 
        // subObject itself will be populated.  So if its null, then its not present in the DB.
        // This is quite a bundle of assumptions, so be aware that if any of these assumptions become 
        // untrue, then this whole section breaks down.
        
        //org_cd must be a valid org and active in the ca_org_t table
        if (ObjectUtils.isNull(newAccount.getOrganization())) {
            success &= false;
            putFieldError("organization.organizationCode", KeyConstants.ERROR_DOCUMENT_ACCMAINT_INVALID_ORG);
        }
        else if (!newAccount.getOrganization().isOrganizationActiveIndicator()) {
            success &= false;
            putFieldError("organization.organizationCode", KeyConstants.ERROR_DOCUMENT_ACCMAINT_INACTIVE_ORG);
        }
        
        //acct_phys_cmp_cd must be valid campus in the acct_phys_cmp_cd table
        if (ObjectUtils.isNull(newAccount.getAccountPhysicalCampus())) {
            success &= false;
            putFieldError("accountPhysicalCampus.campusCode", KeyConstants.ERROR_DOCUMENT_ACCMAINT_INVALID_CAMPUS_CD);
            putGlobalError("Physical Campus Code entered must be a valid Physical Campus Code that exists in the system.");
        } // campus doesnt have an Active code, so this isnt checked
        
        //acct_state_cd must be valid in the sh_state_t table
        if (ObjectUtils.isNull(newAccount.getAccountState())) {
            success &= false;
            putFieldError("accountStateCode", KeyConstants.ERROR_DOCUMENT_ACCMAINT_INVALID_STATE_CD);
        } // state doesnt have an Active code, so this isnt checked
        
        //acct_zip_cd must be a valid in the sh_zip_code_t table
        if (ObjectUtils.isNull(newAccount.getPostalZipCode())) {
            success &= false;
            putFieldError("accountZipCode", KeyConstants.ERROR_DOCUMENT_ACCMAINT_INVALID_ZIP_CD);
        } // zipcode doesnt have an Active code, so this isnt checked
        
        return success;
    }

    /**
     * 
     * This method checks to see if the user passed in is of the type requested.  
     * 
     * If so, it returns true.  If not, it returns false, and adds an error to 
     * the GlobalErrors.
     * @param user - KualiUser to be tested
     * @param employeeType - String value expected for Employee Type 
     * @param userRoleDescription - User Role being tested, to be passed into an error message
     * 
     * @return - true if user is of the requested employee type, false if not or if 
     *           user object is null
     * 
     */
    private boolean checkUserType(String propertyName, KualiUser user, String employeeType, String userRoleDescription) {
        
        //	if the user isnt populated, it will fail
        if (ObjectUtils.isNull(user)) {
            return false;
        }
        
        //	if the KualiUser record is not properly setup with this value, this will fail
        if (StringUtils.isEmpty(user.getEmployeeTypeCode())) {
            return false;
        }

        if (!StringUtils.isEmpty(user.getEmployeeTypeCode())) {
            if (user.getEmployeeTypeCode().equalsIgnoreCase(employeeType)) {
	            return true;
            }
            else {
                return false;
            }
        }
        else {
            putFieldError(propertyName, KeyConstants.ERROR_DOCUMENT_ACCMAINT_PRO_TYPE_REQD_FOR_EMPLOYEE, userRoleDescription);
            return false;
        }
    }
    
    /**
     * 
     * This method checks to see if the user is trying to close the account and if so if any 
     * rules are being violated
     * @param maintenanceDocument
     * @return false on rules violation
     */
    private boolean checkCloseAccount(MaintenanceDocument maintenanceDocument) {

        boolean success = true;
        boolean isBeingClosed;

        //	if the account isnt being closed, then dont bother processing the rest of 
        // the method
        if(!oldAccount.isAccountClosedIndicator() && newAccount.isAccountClosedIndicator()) {
            isBeingClosed = true;
        } else {
            isBeingClosed = false;
        }
        if (!isBeingClosed) {
            return true;
        }
        
        //	this business is to get two Calendar objects, one the current date (with no 
        // time component) and one a Calendar version of AccountExpirationDate (with no 
        // time component).
        Timestamp closeTimestamp = newAccount.getAccountExpirationDate();
        Calendar todaysDate = Calendar.getInstance();
        Calendar closeDate = new GregorianCalendar();
        
        if (ObjectUtils.isNotNull(closeTimestamp)) {
            //	convert java.sql.Timestamp to java.util.Calendar, and make sure there is no time-component.
            // there may be better ways to do this, but there appears to be a bit of an impedence mismatch 
            // between java.sql.Timestamp (of which a bunch of stuff is deprecated) and java.util.Calendar.
            // if there's a better way to do this, and still avoid contamination by time-components to the 
            // date comparisons, feel free to fix it up
            closeDate.setTimeInMillis(closeTimestamp.getTime() + (closeTimestamp.getNanos() / 1000000));
            closeDate.set(closeDate.get(Calendar.YEAR), closeDate.get(Calendar.MONTH), closeDate.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
            
            //when closing an account, the account expiration date must be the current date or earlier
            if (closeDate.before(todaysDate) || closeDate.equals(todaysDate)) {
                putGlobalError(KeyConstants.ERROR_DOCUMENT_ACCMAINT_ACCT_CANNOT_BE_CLOSED_EXP_DATE_INVALID);
                success &= false;
            }
        }
        
        // when closing an account, a continuation account is required 
        // error message - "When closing an Account a Continuation Account Number entered on the Responsibility screen is required."
        if (StringUtils.isEmpty(newAccount.getContinuationAccountNumber())) {
            putGlobalError(KeyConstants.ERROR_DOCUMENT_ACCMAINT_ACCT_CLOSE_CONTINUATION_ACCT_REQD);
            success &= false;
        }
        
        //TODO: KULCOA-310 - must have no base budget, must have no pending ledger entries or pending labor ledger entries, 
        //      must have no open encumbrances, must have no asset, liability or fund balance balances other than object code 9899 
        //      (9899 is fund balance for us), and the process of closing income and expense into 9899 must take the 9899 balance to zero.
        //
        //NOTES:
        //budget first - no idea (maybe through Options? AccountBalance?)
        //definitely looks like we need to pull AccountBalance
        //pending ledger entries or pending labor ledger entries
        //possibly use GeneralLedgerPendingEntryService to find, but what keys are used?
        //no clue on how to check for balances in the other areas (encumbrances, asset, liability, fund balance [other than 9899])
        //accounts can only be closed if they dont have balances or any pending ledger entries

        return true;
    }
    
    /**
     * 
     * This method checks to see if any Contracts and Grants business rules were violated
     * @param maintenanceDocument
     * @return false on rules violation
     */
    private boolean checkContractsAndGrants(MaintenanceDocument maintenanceDocument) {

        boolean success = true;
        
        //	an income stream account is required for accounts in the C&G (CG) and General Fund (GF) fund groups 
        // (except for the MPRACT sub-fund group in the general fund fund group).
        if (ObjectUtils.isNotNull(newAccount.getSubFundGroup())) {
            String fundGroupCode = newAccount.getSubFundGroup().getFundGroupCode();
            if (fundGroupCode.equalsIgnoreCase(CONTRACTS_GRANTS_CD) || 
               (fundGroupCode.equalsIgnoreCase(GENERAL_FUND_CD) && 
               !newAccount.getSubFundGroupCode().equalsIgnoreCase(SUB_FUND_GROUP_MEDICAL_PRACTICE_FUNDS))) {
                
                if(StringUtils.isEmpty(newAccount.getIncomeStreamAccountNumber())) {
                    putFieldError("incomeStreamAccount.accountNumber", KeyConstants.ERROR_DOCUMENT_ACCMAINT_INCOME_STREAM_ACCT_NBR_CANNOT_BE_NULL);
                    success &= false;
                }
                if(StringUtils.isEmpty(newAccount.getIncomeStreamFinancialCoaCode())) {
                    putFieldError("incomeStreamAccount.chartOfAccounts.chartOfAccountsCode", KeyConstants.ERROR_DOCUMENT_ACCMAINT_INCOME_STREAM_ACCT_COA_CANNOT_BE_NULL);
                    success &= false;
                }
            }
        }
        return success;
    }
    
    /**
     * 
     * This method checks to see if any expiration date field rules were violated
     * @param maintenanceDocument
     * @return false on rules violation
     */
    private boolean checkExpirationDate(MaintenanceDocument maintenanceDocument) {

        boolean success = true;

        Timestamp oldExpDate = oldAccount.getAccountExpirationDate();
        Timestamp newExpDate = newAccount.getAccountExpirationDate();
        Timestamp today = new Timestamp(Calendar.getInstance().getTimeInMillis());
        
        //	When updating an account expiration date, the date must be today or later 
        // (except for C&G accounts).  Only run this test if this maint doc 
        // is an edit doc
        if (maintenanceDocument.isEdit()) {
            
            boolean expDateHasChanged = false;
            
            //	if the old version of the account had no expiration date, and the new 
            // one has a date
            if (ObjectUtils.isNull(oldExpDate) && ObjectUtils.isNotNull(newExpDate)) {
                expDateHasChanged = true;
            }
            
            //	 if there was an old and a new expDate, but they're different
            else if (ObjectUtils.isNotNull(oldExpDate) && ObjectUtils.isNotNull(newExpDate)) {
                if (!oldExpDate.equals(newExpDate)) {
                    expDateHasChanged = true;
                }
            }

            //	if the dates are different
            if (expDateHasChanged) {
                
                //	If we have a subFundGroup value.  Normally, this would never be allowed 
                // to be null, but it could be in this case, which will trigger a different 
                // validation error.  But if it is null, we want to silently not bother to 
                // make the test, as it'll run the test for real once the user gets the 
                // subFundGroupCode entered and correct.
                if (ObjectUtils.isNotNull(newAccount.getSubFundGroup())) {
                    String fundGroupCode = newAccount.getSubFundGroup().getFundGroupCode();
                    
                    //	If this is NOT a CG Fund Group account, then Expiration Date 
                    // must be later than today.  If its not, add a business rule error.
                    if (!fundGroupCode.equalsIgnoreCase(CONTRACTS_GRANTS_CD)) {
                        if (!newExpDate.after(today) || newExpDate.equals(today)) {
                            putGlobalError(KeyConstants.ERROR_DOCUMENT_ACCMAINT_EXP_DATE_TODAY_LATER_EXCEPT_CANDG_ACCT);
                            success &= false;
                        }
                    }
                }
            }
        }
        
        //	a continuation account is required if the expiration date is completed.
        if (ObjectUtils.isNotNull(newExpDate)) {
            if (StringUtils.isEmpty(newAccount.getContinuationAccountNumber()) || 
                StringUtils.isEmpty(newAccount.getContinuationFinChrtOfAcctCd())) {
                
                putGlobalError(KeyConstants.ERROR_DOCUMENT_ACCMAINT_CONTINUATION_ACCT_REQD_IF_EXP_DATE_COMPLETED);
                success &= false;
            }
        }
        
        //	If creating a new account if acct_expiration_dt is set and the fund_group is not "CG" then 
        // the acct_expiration_dt must be changed to a date that is today or later
        if(maintenanceDocument.isNew() && ObjectUtils.isNotNull(newExpDate) ) {
            if(ObjectUtils.isNotNull(newAccount.getSubFundGroup())) {
                if(!newAccount.getSubFundGroup().getFundGroupCode().equalsIgnoreCase(CONTRACTS_GRANTS_CD)) {
                    if(!newExpDate.after(today) || newExpDate.equals(today) ) {
                        putGlobalError(KeyConstants.ERROR_DOCUMENT_ACCMAINT_EXP_DATE_TODAY_LATER_EXCEPT_CANDG_ACCT);
                        success &= false;
                    }
                }
            }
        }
        
        //	acct_expiration_dt can not be before acct_effect_dt
        Timestamp effectiveDate = newAccount.getAccountEffectiveDate();
        if (ObjectUtils.isNotNull(effectiveDate) && ObjectUtils.isNotNull(newExpDate)) {
            if (newExpDate.before(effectiveDate)) {
                putGlobalError(KeyConstants.ERROR_DOCUMENT_ACCMAINT_EXP_DATE_CANNOT_BE_BEFORE_EFFECTIVE_DATE);
                success &= false;
            }
        }

        return success;
    }
    
    /**
     * 
     * This method checks to see if any Fund Group rules were violated
     * @param maintenanceDocument
     * @return false on rules violation
     * 
     */
    private boolean checkFundGroup(MaintenanceDocument maintenanceDocument) {
        
        boolean success = true;
        SubFundGroup subFundGroup = newAccount.getSubFundGroup();
        
        if (ObjectUtils.isNotNull(subFundGroup)) {

            String fundGroupCode = subFundGroup.getFundGroupCode();
            String restrictedStatusCode = newAccount.getAccountRestrictedStatusCode();

            if (ObjectUtils.isNotNull(restrictedStatusCode)) {
                //	on the account screen, if the fund group of the account is CG (contracts & grants) or 
                // RF (restricted funds), the restricted status code is set to 'R'.
                if (fundGroupCode.equalsIgnoreCase(CONTRACTS_GRANTS_CD) || fundGroupCode.equalsIgnoreCase(RESTRICTED_FUND_CD)) {
                    if (!restrictedStatusCode.equalsIgnoreCase(RESTRICTED_CD_RESTRICTED)) {
                        putGlobalError(KeyConstants.ERROR_DOCUMENT_ACCMAINT_ACCT_RESTRICTED_STATUS_CD_MUST_BE_R);
                        success &= false;
                    }
                }

                //	If the fund group is EN (endowment) or PF (plant fund) the value is not set by the system and 
                // must be set by the user 
                else if (fundGroupCode.equalsIgnoreCase(ENDOWMENT_FUND_CD) || fundGroupCode.equalsIgnoreCase(PLANT_FUND_CD)) {
                    if (StringUtils.isEmpty(restrictedStatusCode) || 
                       (!restrictedStatusCode.equalsIgnoreCase(RESTRICTED_CD_RESTRICTED) && !restrictedStatusCode.equalsIgnoreCase(RESTRICTED_CD_UNRESTRICTED))) {
                       
                        putGlobalError(KeyConstants.ERROR_DOCUMENT_ACCMAINT_ACCT_RESTRICTED_STATUS_CD_MUST_BE_U_OR_R);
                        success &= false;
                    }
                }
                
                //	for all other fund groups the value is set to 'U'. R being restricted,U being unrestricted.
                else {
                    if (!restrictedStatusCode.equalsIgnoreCase(RESTRICTED_CD_UNRESTRICTED)) {
        				putGlobalError(KeyConstants.ERROR_DOCUMENT_ACCMAINT_ACCT_RESTRICTED_STATUS_CD_MUST_BE_U);
        				success &= false;
                    }
                }
            }
            
            //	an account in the general fund fund group cannot have a budget recording level of mixed.
            if (fundGroupCode.equalsIgnoreCase(GENERAL_FUND_CD)) {
                String budgetRecordingLevelCode = newAccount.getBudgetRecordingLevelCode();
                if (StringUtils.isEmpty(budgetRecordingLevelCode)) {
                    if (budgetRecordingLevelCode.equalsIgnoreCase(BUDGET_RECORDING_LEVEL_MIXED)) {
                        putFieldError("budgetRecordingLevelCode", KeyConstants.ERROR_DOCUMENT_ACCMAINT_ACCT_GF_BUDGET_RECORD_LVL_MIXED);
                        success &= false;
                    }
                }
            }
        }

        return success;
    }
    
    /**
     * 
     * This method checks to see if any SubFund Group rules were violated
     * @param maintenanceDocument
     * @return false on rules violation
     * 
     */
    private boolean checkSubFundGroup(MaintenanceDocument maintenanceDocument) {
        
        boolean success = true;
        
        //	sub_fund_grp_cd on the account must be set to a valid sub_fund_grp_cd that exists in the ca_sub_fund_grp_t table
        //	assuming here that since we did the PersistenceService.refreshNonKeyFields() at beginning of rule that if the 
        // SubFundGroup object would be populated.
        if (StringUtils.isEmpty(newAccount.getSubFundGroupCode())) {
            putFieldError("subFundGroup", KeyConstants.ERROR_DOCUMENT_ACCMAINT_INVALID_SUBFUNDGROUP);
            success &= false;
        }
        
        //	PFCMD (Plant Fund, Construction and Major Remodeling) SubFundCode checks
        else {
            
            //	Attempt to get the right SubFundGroup code to check the following logic with.  If the value isnt available, go ahead 
            // and die, as this indicates a misconfigured app, and important business rules wont be implemented without it.
            String capitalSubFundGroup = configService.getApplicationParameterValue(CHART_MAINTENANCE_EDOC, ACCT_CAPITAL_SUBFUNDGROUP);
            if (StringUtils.isEmpty(capitalSubFundGroup)) {
                throw new RuntimeException("Expected ConfigurationService.ApplicationParameterValue was not found " + 
                        					"for ScriptName = '" + CHART_MAINTENANCE_EDOC + "' and " + 
                        					"Parameter = '" + ACCT_CAPITAL_SUBFUNDGROUP + "'");
            }

            if (newAccount.getSubFundGroupCode().equalsIgnoreCase(capitalSubFundGroup)) {
                
                String campusCode = newAccount.getAccountDescription().getCampusCode();
                String buildingCode = newAccount.getAccountDescription().getBuildingCode();
                
                //	if sub_fund_grp_cd is 'PFCMR' then campus_cd must be entered
                if (StringUtils.isEmpty(campusCode)) {
        	        putFieldError("accountDescription.campusCode", KeyConstants.ERROR_DOCUMENT_ACCMAINT_CAMS_SUBFUNDGROUP_WITH_MISSING_CAMPUS_CD_FOR_BLDG);
                    success &= false;
                }
            
            	//	if sub_fund_grp_cd is 'PFCMR' then bldg_cd must be entered
            	if (StringUtils.isEmpty(buildingCode)) {
        	        putFieldError("accountDescription.campusCode", KeyConstants.ERROR_DOCUMENT_ACCMAINT_CAMS_SUBFUNDGROUP_WITH_MISSING_BUILDING_CD);
                    success &= false;
            	} 
            	
            	//	the building object (campusCode & buildingCode) must exist in the DB
            	if (!StringUtils.isEmpty(campusCode) && !StringUtils.isEmpty(buildingCode)) {
            	    Map pkMap = new HashMap();
            	    pkMap.put("campusCode", campusCode);
            	    pkMap.put("buildingCode", buildingCode);

            	    Building building = (Building) boService.findByPrimaryKey(Building.class, pkMap);
            	    if (building == null) {
            	        putFieldError("accountDescription.campusCode", KeyConstants.ERROR_EXISTENCE, campusCode);
            	        putFieldError("accountDescription.buildingCode", KeyConstants.ERROR_EXISTENCE, buildingCode);
            	        success &= false;
            	    }
            	}
            }
        }

        return success;
    }
    

}