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
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.kuali.KeyConstants;
import org.kuali.core.bo.Building;
import org.kuali.core.bo.user.KualiUser;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.SubFundGroup;
import org.kuali.module.financial.rules.KualiParameterRule;
import org.kuali.module.gl.service.BalanceService;
import org.kuali.module.gl.service.GeneralLedgerPendingEntryService;

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
    
    GeneralLedgerPendingEntryService generalLedgerPendingEntryService;
    
    private KualiParameterRule validBudgetRule;
    private Account oldAccount;
    private Account newAccount;
    private boolean ruleValuesSetup;
    private BalanceService balanceService;
    private DateTimeService dateTimeService;
    
    public AccountRule() {
        ruleValuesSetup = false;
        
        // Pseudo-inject some services.
        //
        // This approach is being used to make it simpler to convert the Rule classes 
        // to spring-managed with these services injected by Spring at some later date.  
        // When this happens, just remove these calls to the setters with 
        // SpringServiceLocator, and configure the bean defs for spring.
        this.setConfigService(SpringServiceLocator.getKualiConfigurationService());
        this.setGeneralLedgerPendingEntryService(SpringServiceLocator.getGeneralLedgerPendingEntryService());
        this.setBalanceService(SpringServiceLocator.getGeneralLedgerBalanceService());
        this.setDateTimeService(SpringServiceLocator.getDateTimeService());
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
        
        LOG.info("processCustomSaveDocumentBusinessRules called");
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
        
        LOG.info("checkEmptyValues called");

        boolean success = true;
        
//	Commented out because the DD validation should be doing this
//        success &= checkEmptyBOField("chartOfAccountsCode", newAccount.getChartOfAccountsCode(), "Chart of Accounts Code");
//        success &= checkEmptyBOField("accountNumber", newAccount.getAccountNumber(), "Account Number");
//        success &= checkEmptyBOField("accountName", newAccount.getAccountName(), "Account Name");
//        success &= checkEmptyBOField("organizationCode", newAccount.getOrganizationCode(), "Organization Code");
//        success &= checkEmptyBOField("accountPhysicalCampusCode", newAccount.getAccountPhysicalCampusCode(), "Campus Code");
//        success &= checkEmptyBOField("accountEffectiveDate", newAccount.getAccountEffectiveDate(), "Effective Date");
//        success &= checkEmptyBOField("accountCityName", newAccount.getAccountCityName(), "City Name");
//        success &= checkEmptyBOField("accountStateCode", newAccount.getAccountStateCode(), "State Code");
//        success &= checkEmptyBOField("accountStreetAddress", newAccount.getAccountStreetAddress(), "Address");
//        success &= checkEmptyBOField("accountZipCode", newAccount.getAccountZipCode(), "Zip Code");
//        success &= checkEmptyBOField("budgetRecordingLevelCode", newAccount.getBudgetRecordingLevelCode(), "Budget Recording Level");
//        success &= checkEmptyBOField("accountSufficientFundsCode", newAccount.getAccountSufficientFundsCode(), "Sufficient Funds Code");
//        success &= checkEmptyBOField("subFundGroupCode", newAccount.getSubFundGroupCode(), "Sub Fund Group");
//        success &= checkEmptyBOField("financialHigherEdFunctionCd", newAccount.getFinancialHigherEdFunctionCd(), "Higher Ed Function Code");
//        success &= checkEmptyBOField("accountRestrictedStatusCode", newAccount.getAccountRestrictedStatusCode(), "Restricted Status Code");
        
        
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
        
//    	Commented out because the DD validation should be doing this
//        if (StringUtils.isEmpty(newAccount.getAccountFiscalOfficerSystemIdentifier())) {
//            success &= checkEmptyBOField("accountFiscalOfficerSystemIdentifier", 
//                    					newAccount.getAccountFiscalOfficerSystemIdentifier(), 
//                    					"Account Fiscal Officer");
//        }
//        if (StringUtils.isEmpty(newAccount.getAccountManagerSystemIdentifier())) {
//            success &= checkEmptyBOField("accountManagerSystemIdentifier", 
//                    					newAccount.getAccountManagerSystemIdentifier(), 
//                    					"Account Manager");
//        }
//        if (ObjectUtils.isNotNull(newAccount.getAccountsSupervisorySystemsIdentifier())) {
//            success &= checkEmptyBOField("accountsSupervisorySystemsIdentifier", 
//                    					newAccount.getAccountsSupervisorySystemsIdentifier(), 
//                    					"Account Supervisor");
//        }

        //	if the expiration date is earlier than today, account guidelines are not required.
        if (ObjectUtils.isNotNull(newAccount.getAccountGuideline())) {
            if (newAccount.getAccountExpirationDate() != null) {
                Timestamp today = dateTimeService.getCurrentTimestamp();
                today.setTime(DateUtils.truncate(today, Calendar.DAY_OF_MONTH).getTime());
	            if (newAccount.getAccountExpirationDate().after(today) || newAccount.getAccountExpirationDate().equals(today)) {
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

        LOG.info("checkGeneralRules called");
        KualiUser fiscalOfficer = newAccount.getAccountFiscalOfficerUser();
        KualiUser accountManager = newAccount.getAccountManagerUser();
        KualiUser accountSupervisor = newAccount.getAccountSupervisoryUser();
        
        boolean success = true;

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
                                KeyConstants.ERROR_DOCUMENT_ACCMAINT_ACCT_NMBR_NOT_ALLOWED, 
                                new String[] {newAccount.getAccountNumber(),illegalValues[i]});
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
            if (StringUtils.isEmpty(newAccount.getReportsToAccountNumber()) || StringUtils.isEmpty(newAccount.getReportsToChartOfAccountsCode())) {
                success &= false;
                putFieldError("reportsToAccountNumber", KeyConstants.ERROR_DOCUMENT_ACCMAINT_RPTS_TO_ACCT_REQUIRED_IF_FRINGEBENEFIT_FALSE);
                putFieldError("reportsToChartOfAccountsCode", KeyConstants.ERROR_DOCUMENT_ACCMAINT_RPTS_TO_ACCT_REQUIRED_IF_FRINGEBENEFIT_FALSE);
            }
            else {
                Account reportsToAccount = newAccount.getReportsToAccount();
                //	if the reportsToChartCode and reportsToAccountNbr dont map to any in the db
                if (ObjectUtils.isNull(reportsToAccount)) {
                    success &= false;
                    putFieldError("reportsToAccountNumber", KeyConstants.ERROR_EXISTENCE, 
                            		"Fringe Benefit Account: " + newAccount.getReportsToChartOfAccountsCode() + "-" + 
                            		newAccount.getReportsToAccountNumber());
                    putFieldError("reportsToChartOfAccountsCode", KeyConstants.ERROR_EXISTENCE, 
                                    "Fringe Benefit Account: " + newAccount.getReportsToChartOfAccountsCode() + "-" + 
                                    newAccount.getReportsToAccountNumber());
                }
                else {
                    //	otherwise, make sure this account is flagged as a fringe benefits acct
                    if (!reportsToAccount.isAccountsFringesBnftIndicator()) {
                        success &= false;
                        putFieldError("reportsToAccountNumber", KeyConstants.ERROR_DOCUMENT_ACCMAINT_RPTS_TO_ACCT_MUST_BE_FLAGGED_FRINGEBENEFIT, newAccount.getReportsToAccountNumber());
                        putFieldError("reportsToChartOfAccountsCode", KeyConstants.ERROR_DOCUMENT_ACCMAINT_RPTS_TO_ACCT_MUST_BE_FLAGGED_FRINGEBENEFIT, newAccount.getReportsToAccountNumber());
                    }
                }
            }
        }
        
        //the employee type for fiscal officer, account manager, and account supervisor must be 'P' – professional.
        success &= checkUserType("accountFiscalOfficerSystemIdentifier", fiscalOfficer, EMPLOYEE_TYPE_PROFESSIONAL, "Fiscal Officer");
        success &= checkUserType("accountManagerSystemIdentifier", accountManager, EMPLOYEE_TYPE_PROFESSIONAL, "Account Manager");
        success &= checkUserType("accountsSupervisorySystemsIdentifier", accountSupervisor, EMPLOYEE_TYPE_PROFESSIONAL, "Account Supervisor");
        
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
        if (ObjectUtils.isNull(newAccount.getBudgetRecordingLevel())) {
            if (validBudgetRule.failsRule(newAccount.getBudgetRecordingLevelCode())) {
                success &= false;
                putFieldError("budgetRecordingLevelCode", KeyConstants.ERROR_DOCUMENT_ACCMAINT_INVALID_BUDGET_RECORD_LVL_CD, newAccount.getBudgetRecordingLevelCode());
            }
        } else if (!newAccount.getBudgetRecordingLevel().isActive()) { 
            success &= false;
            putFieldError("budgetRecordingLevelCode", KeyConstants.ERROR_DOCUMENT_ACCMAINT_INACTIVE_BUDGET_RECORD_LVL_CD, newAccount.getBudgetRecordingLevelCode());
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
            putFieldError("organizationCode", KeyConstants.ERROR_DOCUMENT_ACCMAINT_INVALID_ORG);
        }
        else if (!newAccount.getOrganization().isOrganizationActiveIndicator()) {
            success &= false;
            putFieldError("organizationCode", KeyConstants.ERROR_DOCUMENT_ACCMAINT_INACTIVE_ORG);
        }
        
        //acct_phys_cmp_cd must be valid campus in the acct_phys_cmp_cd table
        if (ObjectUtils.isNull(newAccount.getAccountPhysicalCampus())) {
            success &= false;
            putFieldError("accountPhysicalCampusCode", KeyConstants.ERROR_DOCUMENT_ACCMAINT_INVALID_CAMPUS_CD);
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
        
        //existence check on account type
        if (ObjectUtils.isNull(newAccount.getAccountType())) {
            success &= false;
            putFieldError("accountTypeCode", KeyConstants.ERROR_DOCUMENT_ACCMAINT_INVALID_ACCT_TYPE);
        } 
        
        // existence check on financial higher ed function
        if (ObjectUtils.isNull(newAccount.getFinancialHigherEdFunction())) {
            success &= false;
            putFieldError("financialHigherEdFunctionCd", KeyConstants.ERROR_DOCUMENT_ACCMAINT_INVALID_HIGHER_ED_CD);
        }
        
        // existence check on reportsTo/fringeBenefit account
        // NOTE: that both FringeBenefit Account and ReportsTo Account are the same thing.
        if(StringUtils.isNotEmpty(newAccount.getReportsToAccountNumber()) && StringUtils.isNotEmpty(newAccount.getReportsToChartOfAccountsCode())) {
            if(ObjectUtils.isNull(newAccount.getReportsToAccount())) {
                success &= false;
                putFieldError("reportsToAccountNumber", KeyConstants.ERROR_DOCUMENT_ACCMAINT_INVALID_FRINGE_BENEFIT_ACCOUNT);
            }
            else {
                Account fringeBenefitAccount = newAccount.getReportsToAccount();
                if (fringeBenefitAccount.isAccountClosedIndicator()) {
                    success &= false;
                    putFieldError("reportsToAccountNumber", KeyConstants.ERROR_DOCUMENT_ACCMAINT_ACCOUNT_CLOSED_FRINGE_BENEFIT);
                }
                else if (fringeBenefitAccount.isExpired()) {
                    //TODO: see KULCOA-337 - how to handle "asking the user if they want to use the Continuation Account"
                }
            }
        }
        
        //existence check on continuation account
        if(StringUtils.isNotEmpty(newAccount.getContinuationAccountNumber()) && StringUtils.isNotEmpty(newAccount.getContinuationFinChrtOfAcctCd())) {
            if(ObjectUtils.isNull(newAccount.getContinuationAccount())) {
                success &= false;
                putFieldError("continuationAccountNumber", KeyConstants.ERROR_DOCUMENT_ACCMAINT_INVALID_CONTINUATION_ACCOUNT);
            }
            else {
                Account continuationAccount = newAccount.getContinuationAccount();
                if (continuationAccount.isAccountClosedIndicator()) {
                    success &= false;
                    putFieldError("continuationAccountNumber", KeyConstants.ERROR_DOCUMENT_ACCMAINT_ACCOUNT_CLOSED_CONTINUATION);
                }
                else if (continuationAccount.isExpired()) {
                    success &= false;
                    putFieldError("continuationAccountNumber", KeyConstants.ERROR_DOCUMENT_ACCMAINT_ACCOUNT_EXPIRED_CONTINUATION);
                }
            }
        }
        
        //existence check on endowment account
        if(StringUtils.isNotEmpty(newAccount.getEndowmentIncomeAccountNumber()) && StringUtils.isNotEmpty(newAccount.getEndowmentIncomeAcctFinCoaCd())) {
            if(ObjectUtils.isNull(newAccount.getEndowmentIncomeAccount())) {
                success &= false;
                putFieldError("endowmentIncomeAccountNumber", KeyConstants.ERROR_DOCUMENT_ACCMAINT_INVALID_ENDOWMENT_ACCOUNT);
            }
            else {
                Account endowmentAccount = newAccount.getEndowmentIncomeAccount();
                if (endowmentAccount.isAccountClosedIndicator()) {
                    success &= false;
                    putFieldError("endowmentIncomeAccountNumber", KeyConstants.ERROR_DOCUMENT_ACCMAINT_ACCOUNT_CLOSED_ENDOWMENT);
                }
                else if (endowmentAccount.isExpired()) {
                    //TODO: see KULCOA-337 - how to handle "asking the user if they want to use the Continuation Account"
                }
            }
        }
        
        //existence check on contractControl account
        if(StringUtils.isNotEmpty(newAccount.getContractControlAccountNumber()) && StringUtils.isNotEmpty(newAccount.getContractControlFinCoaCode())) {
            if(ObjectUtils.isNull(newAccount.getContractControlAccount())) {
                success &= false;
                putFieldError("contractControlAccountNumber", KeyConstants.ERROR_DOCUMENT_ACCMAINT_INVALID_CONTRACT_CONTROL_ACCOUNT);
            }
            else {
                Account contractControlAccount = newAccount.getContractControlAccount();
                if (contractControlAccount.isAccountClosedIndicator()) {
                    success &= false;
                    putFieldError("contractControlAccountNumber", KeyConstants.ERROR_DOCUMENT_ACCMAINT_ACCOUNT_CLOSED_CONTRACT_CONTROL);
                }
                else if (contractControlAccount.isExpired()) {
                    //TODO: see KULCOA-337 - how to handle "asking the user if they want to use the Continuation Account"
                }
            }
        }
        
        //existence check on income stream
        if(StringUtils.isNotEmpty(newAccount.getIncomeStreamAccountNumber()) && StringUtils.isNotEmpty(newAccount.getIncomeStreamFinancialCoaCode())) {
            if(ObjectUtils.isNull(newAccount.getIncomeStreamAccount())) {
                success &= false;
                putFieldError("incomeStreamAccountNumber", KeyConstants.ERROR_DOCUMENT_ACCMAINT_INVALID_INCOME_STREAM_ACCOUNT);
            }
            else {
                Account incomeStreamAccount = newAccount.getIncomeStreamAccount();
                if (incomeStreamAccount.isAccountClosedIndicator()) {
                    success &= false;
                    putFieldError("incomeStreamAccountNumber", KeyConstants.ERROR_DOCUMENT_ACCMAINT_ACCOUNT_CLOSED_INCOME_STREAM);
                }
                else if (incomeStreamAccount.isExpired()) {
                    //TODO: see KULCOA-337 - how to handle "asking the user if they want to use the Continuation Account"
                }
            }
        }
        
        //existence check on indirect cost recovery
        if(StringUtils.isNotEmpty(newAccount.getIndirectCostRecoveryAcctNbr()) && StringUtils.isNotEmpty(newAccount.getIndirectCostRcvyFinCoaCode())) {
            if(ObjectUtils.isNull(newAccount.getIndirectCostRecoveryAcct())) {
                success &= false;
                putFieldError("indirectCostRecoveryAcctNbr", KeyConstants.ERROR_DOCUMENT_ACCMAINT_INVALID_ICR_ACCOUNT);
            }
            else {
                Account icrAccount = newAccount.getIndirectCostRecoveryAcct();
                if (icrAccount.isAccountClosedIndicator()) {
                    success &= false;
                    putFieldError("indirectCostRecoveryAcctNbr", KeyConstants.ERROR_DOCUMENT_ACCMAINT_ACCOUNT_CLOSED_ICR);
                }
                else if (icrAccount.isExpired()) {
                    //TODO: see KULCOA-337 - how to handle "asking the user if they want to use the Continuation Account"
                }
            }
        }
        
        //existence check on account sufficient funds code
        if(StringUtils.isNotEmpty(newAccount.getAccountSufficientFundsCode())) {
            if(ObjectUtils.isNull(newAccount.getSufficientFundsCode())) {
                success &= false;
                putFieldError("accountSufficientFundsCode", KeyConstants.ERROR_DOCUMENT_ACCMAINT_INVALID_SUFFICIENT_FUNDS_CODE);
            }
        }
        
        //existence check on fiscal officer
        if(StringUtils.isNotEmpty(newAccount.getAccountFiscalOfficerSystemIdentifier())) {
            if(ObjectUtils.isNull(newAccount.getAccountFiscalOfficerUser())) {
                success &= false;
                putFieldError("accountFiscalOfficerSystemIdentifier", KeyConstants.ERROR_DOCUMENT_ACCMAINT_INVALID_PERSON_FISCAL_OFFICER);
            }
        }
        
        //existence check on supervisory system manager
        if(StringUtils.isNotEmpty(newAccount.getAccountsSupervisorySystemsIdentifier())) {
            if(ObjectUtils.isNull(newAccount.getAccountSupervisoryUser())) {
                success &= false;
                putFieldError("accountsSupervisorySystemsIdentifier", KeyConstants.ERROR_DOCUMENT_ACCMAINT_INVALID_PERSON_SUPERVISOR);
            }
        }
        
        //existence check on manager system
        if(StringUtils.isNotEmpty(newAccount.getAccountManagerSystemIdentifier())) {
            if(ObjectUtils.isNull(newAccount.getAccountManagerUser())) {
                success &= false;
                putFieldError("accountManagerSystemIdentifier", KeyConstants.ERROR_DOCUMENT_ACCMAINT_INVALID_PERSON_MANAGER);
            }
        }
        
        //existence check on account restricted status code
        if(StringUtils.isNotEmpty(newAccount.getAccountRestrictedStatusCode())) {
            if(ObjectUtils.isNull(newAccount.getAccountRestrictedStatus())) {
                success &= false;
                putFieldError("accountRestrictedStatusCode", KeyConstants.ERROR_DOCUMENT_ACCMAINT_INVALID_RESTRICTED_STATUS_CODE);
            }
        }
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

        LOG.info("checkCloseAccount called");

        boolean success = true;
        boolean isBeingClosed = false;

        //	if the account isnt being closed, then dont bother processing the rest of 
        // the method
        if(!oldAccount.isAccountClosedIndicator() && newAccount.isAccountClosedIndicator()) {
            isBeingClosed = true;
        } 

        if (!isBeingClosed) {
            return true;
        }
        
        //	get the two dates, and remove any time-components from the dates
        Timestamp expirationDate = newAccount.getAccountExpirationDate();
        Timestamp todaysDate = dateTimeService.getCurrentTimestamp();
        expirationDate.setTime(DateUtils.truncate(expirationDate, Calendar.DAY_OF_MONTH).getTime());
        todaysDate.setTime(DateUtils.truncate(todaysDate, Calendar.DAY_OF_MONTH).getTime());
        
        if (ObjectUtils.isNotNull(expirationDate)) {

            //when closing an account, the account expiration date must be the current date or earlier
            if (expirationDate.before(todaysDate) || expirationDate.equals(todaysDate)) {
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
        
        // must have no pending ledger entries
        if (!generalLedgerPendingEntryService.hasPendingGeneralLedgerEntry(newAccount)) {
            putGlobalError(KeyConstants.ERROR_DOCUMENT_ACCOUNT_BALANCE);
            success &= false;
        }

        // beginning balance must be loaded in order to close account
        if (!balanceService.beginningBalanceLoaded(newAccount)) {
            putGlobalError(KeyConstants.ERROR_DOCUMENT_ACCOUNT_BALANCE);
            success &= false;
        }
        
        // must have no base budget,  must have no open encumbrances, must have no asset, liability or fund balance balances other than object code 9899 
        //      (9899 is fund balance for us), and the process of closing income and expense into 9899 must take the 9899 balance to zero.

        if (balanceService.hasAssetLiabilityFundBalanceBalances(newAccount)) {
            putGlobalError(KeyConstants.ERROR_DOCUMENT_ACCOUNT_BALANCE);
            success &= false;
        }

        // TODO:  must have no pending labor ledger entries (depends on labor: KULLAB-1) 

        return success;
    }
    
    /**
     * 
     * This method checks to see if any Contracts and Grants business rules were violated
     * @param maintenanceDocument
     * @return false on rules violation
     */
    private boolean checkContractsAndGrants(MaintenanceDocument maintenanceDocument) {

        LOG.info("checkContractsAndGrants called");

        boolean success = true;
        
        //	an income stream account is required for accounts in the C&G (CG) and General Fund (GF) fund groups 
        // (except for the MPRACT sub-fund group in the general fund fund group).
        if (ObjectUtils.isNotNull(newAccount.getSubFundGroup())) {
            String fundGroupCode = newAccount.getSubFundGroup().getFundGroupCode();
            if (fundGroupCode.equalsIgnoreCase(CONTRACTS_GRANTS_CD) || 
               (fundGroupCode.equalsIgnoreCase(GENERAL_FUND_CD) && 
               !newAccount.getSubFundGroupCode().equalsIgnoreCase(SUB_FUND_GROUP_MEDICAL_PRACTICE_FUNDS))) {
                
                if(StringUtils.isEmpty(newAccount.getIncomeStreamAccountNumber())) {
                    putFieldError("incomeStreamAccountNumber", KeyConstants.ERROR_DOCUMENT_ACCMAINT_INCOME_STREAM_ACCT_NBR_CANNOT_BE_NULL);
                    success &= false;
                }
                if(StringUtils.isEmpty(newAccount.getIncomeStreamFinancialCoaCode())) {
                    putFieldError("incomeStreamFinancialCoaCode", KeyConstants.ERROR_DOCUMENT_ACCMAINT_INCOME_STREAM_ACCT_COA_CANNOT_BE_NULL);
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

        LOG.info("checkExpirationDate called");

        boolean success = true;

        Timestamp oldExpDate = oldAccount.getAccountExpirationDate();
        Timestamp newExpDate = newAccount.getAccountExpirationDate();
        Timestamp today = dateTimeService.getCurrentTimestamp();
        today.setTime(DateUtils.truncate(today, Calendar.DAY_OF_MONTH).getTime()); // remove any time components
        
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
        
        LOG.info("checkFundGroup called");

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
                        putFieldError("accountRestrictedStatusCode", KeyConstants.ERROR_DOCUMENT_ACCMAINT_ACCT_RESTRICTED_STATUS_CD_MUST_BE_R);
                        success &= false;
                    }
                }

                //	If the fund group is EN (endowment) or PF (plant fund) the value is not set by the system and 
                // must be set by the user 
                else if (fundGroupCode.equalsIgnoreCase(ENDOWMENT_FUND_CD) || fundGroupCode.equalsIgnoreCase(PLANT_FUND_CD)) {
                    if (StringUtils.isEmpty(restrictedStatusCode) || 
                       (!restrictedStatusCode.equalsIgnoreCase(RESTRICTED_CD_RESTRICTED) && !restrictedStatusCode.equalsIgnoreCase(RESTRICTED_CD_UNRESTRICTED))) {
                       
                        putFieldError("accountRestrictedStatusCode", KeyConstants.ERROR_DOCUMENT_ACCMAINT_ACCT_RESTRICTED_STATUS_CD_MUST_BE_U_OR_R);
                        success &= false;
                    }
                }
                
                //	for all other fund groups the value is set to 'U'. R being restricted,U being unrestricted.
                else {
                    if (!restrictedStatusCode.equalsIgnoreCase(RESTRICTED_CD_UNRESTRICTED)) {
                        putFieldError("accountRestrictedStatusCode", KeyConstants.ERROR_DOCUMENT_ACCMAINT_ACCT_RESTRICTED_STATUS_CD_MUST_BE_U);
        				success &= false;
                    }
                }
            }
            
            //	an account in the general fund fund group cannot have a budget recording level of mixed.
            if (fundGroupCode.equalsIgnoreCase(GENERAL_FUND_CD)) {
                String budgetRecordingLevelCode = newAccount.getBudgetRecordingLevelCode();
                if (StringUtils.isNotEmpty(budgetRecordingLevelCode)) {
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
        
        LOG.info("checkSubFundGroup called");

        boolean success = true;
        
        //	sub_fund_grp_cd on the account must be set to a valid sub_fund_grp_cd that exists in the ca_sub_fund_grp_t table
        //	assuming here that since we did the PersistenceService.refreshNonKeyFields() at beginning of rule that if the 
        // SubFundGroup object would be populated.
        if (StringUtils.isEmpty(newAccount.getSubFundGroupCode()) || ObjectUtils.isNull(newAccount.getSubFundGroup())) {
            putFieldError("subFundGroupCode", KeyConstants.ERROR_DOCUMENT_ACCMAINT_INVALID_SUBFUNDGROUP);
            success &= false;
        } //no active indicator
        
        //	PFCMD (Plant Fund, Construction and Major Remodeling) SubFundCode checks
        else {
            
            //	Attempt to get the right SubFundGroup code to check the following logic with.  If the value isn't available, go ahead 
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

    public void setGeneralLedgerPendingEntryService(GeneralLedgerPendingEntryService generalLedgerPendingEntryService) {
        this.generalLedgerPendingEntryService = generalLedgerPendingEntryService;
    }

    public void setBalanceService(BalanceService balanceService) {
        this.balanceService = balanceService;
    }
    
    /**
     * Sets the configService attribute value.
     * @param configService The configService to set.
     */
    public void setConfigService(KualiConfigurationService configService) {
        this.configService = configService;
    }
    
    /**
     * Sets the dateTimeService attribute value.
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }
    
}