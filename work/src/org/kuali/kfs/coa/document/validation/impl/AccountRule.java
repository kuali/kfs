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

import org.apache.commons.lang.StringUtils;
import org.kuali.KeyConstants;
import org.kuali.core.bo.PostalZipCode;
import org.kuali.core.bo.State;
import org.kuali.core.bo.user.KualiUser;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.maintenance.Maintainable;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.AccountGuideline;
import org.kuali.module.chart.bo.Campus;
import org.kuali.module.financial.rules.KualiParameterRule;

/**
 * Business rule(s) applicable to AccountMaintenance documents.
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class AccountRule extends MaintenanceDocumentRuleBase {
    
    private KualiParameterRule validBudgetRule;
    private boolean ruleValuesSetup;
    
    public static String CHART_MAINTENANCE_EDOC = "ChartMaintenanceEDoc";
    public static String ACCT_BUDGET_CODES_RESTRICT = "Account.BudgetCodesRestriction";
    
    Account oldAccount;
    Account newAccount;
    AccountGuideline oldAccountGuideline;
    AccountGuideline newAccountGuideline;
    MaintenanceDocument maintenanceDocument;
    
    public AccountRule() {
        ruleValuesSetup = false;
    }
    
    private void initializeRuleValues(MaintenanceDocument document) {
        if(!ruleValuesSetup) {
            validBudgetRule = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterRule(
                    CHART_MAINTENANCE_EDOC, ACCT_BUDGET_CODES_RESTRICT);
        }
    }
    
    /**
     * 
     * This method sets the convenience objects like newAccount and oldAccount, so you
     * have short and easy handles to the new and old objects contained in the 
     * maintenance document.
     * 
     * @param document - the maintenanceDocument being evaluated
     * 
     */
    private void setupConvenienceObjects(MaintenanceDocument document) {
        
        //	set the global reference of the maint doc
        maintenanceDocument = document;
        
        //	setup oldAccount convenience objects
        oldAccount = (Account) document.getOldMaintainableObject().getBusinessObject();
        oldAccountGuideline = oldAccount.getAccountGuideline();

        //	setup newAccount convenience objects
        newAccount = (Account) document.getNewMaintainableObject().getBusinessObject();
        newAccountGuideline = newAccount.getAccountGuideline();
    }
    
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        
        setupConvenienceObjects(document);
        
        //	default to success
        boolean success = true;
        initializeRuleValues(document);
        success &= checkEmptyValues(document);
        return success;
    }
    
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {

        setupConvenienceObjects(document);
        
        //	default to success
        boolean success = true;
        initializeRuleValues(document);
        
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
        
        //TODO: all of these strings need to be changed to propertyNames, 
        //      ie:  Chart of Accounts Code -> chartOfAccountsCode
        success &= checkEmptyValue("Financial Document Description", 
                	maintenanceDocument.getDocumentHeader().getFinancialDocumentDescription());
        success &= checkEmptyValue("Chart of Accounts Code", newAccount.getChartOfAccountsCode());
        success &= checkEmptyValue("Account Number", newAccount.getAccountNumber());
        success &= checkEmptyValue("Account Name", newAccount.getAccountName());
        success &= checkEmptyValue("OrganizationFromRules", newAccount.getOrganizationCode());
        success &= checkEmptyValue("Campus Code", newAccount.getAccountPhysicalCampusCode());
        success &= checkEmptyValue("Effective Date", newAccount.getAccountEffectiveDate());
        success &= checkEmptyValue("City Name", newAccount.getAccountCityName());
        success &= checkEmptyValue("State Code", newAccount.getAccountStateCode());
        success &= checkEmptyValue("Address", newAccount.getAccountStreetAddress());
        success &= checkEmptyValue("ZIP Code", newAccount.getAccountZipCode());
        success &= checkEmptyValue("Account Manager", newAccount.getAccountManagerUser().getPersonUniversalIdentifier());
        success &= checkEmptyValue("Account Supervisor", newAccount.getAccountSupervisoryUser().getPersonUniversalIdentifier());
        success &= checkEmptyValue("Budget Recording Level", newAccount.getBudgetRecordingLevelCode());
        success &= checkEmptyValue("Sufficient Funds Code", newAccount.getAccountSufficientFundsCode());
        success &= checkEmptyValue("Sub Fund Group", newAccount.getSubFundGroupCode());
        success &= checkEmptyValue("Higher Ed Function Code", newAccount.getFinancialHigherEdFunctionCd());
        success &= checkEmptyValue("Restricted Status Code", newAccount.getAccountRestrictedStatusCode());
        success &= checkEmptyValue("ICR Type Code", newAccount.getAcctIndirectCostRcvyTypeCd());
        success &= checkEmptyValue("ICR Series Identifier", newAccount.getFinancialIcrSeriesIdentifier());
        success &= checkEmptyValue("ICR Cost Recovery Account", newAccount.getIndirectCostRecoveryAcctNbr());
        success &= checkEmptyValue("C&G Domestic Assistance Number", newAccount.getCgCatlfFedDomestcAssistNbr());
        
        //	Guidelines are only required on a 'new' maint doc
        if (maintenanceDocument.isNew()) {
            success &= checkEmptyValue("Expense Guideline", newAccount.getAccountGuideline().getAccountExpenseGuidelineText());
            success &= checkEmptyValue("Income Guideline", newAccount.getAccountGuideline().getAccountIncomeGuidelineText());
            success &= checkEmptyValue("Account Purpose", newAccount.getAccountGuideline().getAccountPurposeText());
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
        boolean success = true;

        GlobalVariables.getErrorMap().addToErrorPath("newMaintainableObject");
        
           
        //TODO: IU-specific rule?
        //the account number cannot begin with a 3, or with 00.
        if(newAccount.getAccountNumber().startsWith("3") || newAccount.getAccountNumber().startsWith("00")) {
            success &= false;
            putFieldError("accountNumber", KeyConstants.ERROR_DOCUMENT_ACCMAINT_ACCT_NMBR_NOT_ALLOWED, newAccount.getAccountNumber());
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
        if(newAccount.getAccountRestrictedStatusCode().equalsIgnoreCase("T") && newAccount.getAccountRestrictedStatusDate() == null) {
            success &= false;
            putFieldError("accountNumber", KeyConstants.ERROR_DOCUMENT_ACCMAINT_ACCT_NMBR_NOT_ALLOWED, newAccount.getAccountNumber());
        }
        
        // the fringe benefit account (otherwise known as the reportsToAccount) is required if 
        // the fringe benefit code is set to N. 
        // The fringe benefit code of the account designated to accept the fringes must be Y.
        if(!newAccount.isAccountsFringesBnftIndicator()) {
            if (StringUtils.isEmpty(newAccount.getReportsToAccountNumber()) || 
                	ObjectUtils.isNull(newAccount.getReportsToAccount())) { // proxy-safe null test
                success &= false;
                putFieldError("reportsToAccountNumber", KeyConstants.ERROR_DOCUMENT_ACCMAINT_RPTS_TO_ACCT_REQUIRED_IF_FRINGEBENEFIT_FALSE);
            }
            else {
                Account reportsToAccount = newAccount.getReportsToAccount();
                if (!reportsToAccount.isAccountsFringesBnftIndicator()) {
                    success &= false;
                    putFieldError("reportsToAccountNumber", KeyConstants.ERROR_DOCUMENT_ACCMAINT_RPTS_TO_ACCT_MUST_BE_FLAGGED_FRINGEBENEFIT, newAccount.getReportsToAccountNumber());
                }
            }
        }
        
        //the employee type for fiscal officer, account manager, and account supervisor must be 'P' – professional.
        KualiUser fiscalOfficer = newAccount.getAccountFiscalOfficerUser();
        KualiUser acctMgr = newAccount.getAccountManagerUser();
        KualiUser acctSvr = newAccount.getAccountSupervisoryUser();
        
        if(fiscalOfficer == null || acctMgr == null || acctSvr == null) {
            SpringServiceLocator.getPersistenceService().retrieveNonKeyFields(newAccount);
            if(fiscalOfficer == null || acctMgr == null || acctSvr == null) {
                success &= false;
                putFieldError("accountNumber", KeyConstants.ERROR_DOCUMENT_ACCMAINT_ACCT_NMBR_NOT_ALLOWED, newAccount.getAccountNumber());
            }
            String fiscalOfficerEmpType = fiscalOfficer.getEmployeeTypeCode();
            String acctMgrEmpType = acctMgr.getEmployeeTypeCode();
            String acctSvrEmpType = acctSvr.getEmployeeTypeCode();
            if(!fiscalOfficerEmpType.equalsIgnoreCase("P") || 
                    !acctMgrEmpType.equalsIgnoreCase("P") ||
                    !acctSvrEmpType.equalsIgnoreCase("P")) {
                success &= false;
                putFieldError("accountNumber", "GenericError", newAccount.getAccountNumber());
            
            }
        }
        
        //the supervisor cannot be the same as the fiscal officer or account manager.
        if(fiscalOfficer.equals(acctMgr) || 
                fiscalOfficer.equals(acctSvr) ||
                acctMgr.equals(acctSvr)) {
            success &= false;
            putFieldError("accountNumber", "GenericError", newAccount.getAccountNumber());
        }
        
        //valid values for the budget code are account, consolidation, level, object code, mixed, sub-account and no budget.
        if (validBudgetRule.failsRule(newAccount.getBudgetRecordingLevelCode())) {
            success &= false;
            putFieldError("accountNumber", "GenericError", newAccount.getAccountNumber());
        }
        
        //	If a document is enroute that affects the filled in account number then give the user an error indicating that 
        // the current account is locked for editing
        //TODO: do it
        
        //If acct_off_cmp_ind is not set when they route the document then default it to "N"
        //if(account.isAccountOffCampusIndicator())
        //removed - no longer a valid rule as it can only be one of two values True or False, not null
        
        //org_cd must be a valid org and active in the ca_org_t table
        if(!newAccount.getOrganization().isOrganizationActiveIndicator()) {
            success &= false;
            putFieldError("accountNumber", KeyConstants.ERROR_DOCUMENT_ACCMAINT_ACCT_NMBR_NOT_ALLOWED, newAccount.getAccountNumber());
        }
        
        //acct_phys_cmp_cd must be valid campus in the acct_phys_cmp_cd table
        String physicalCampusCode = newAccount.getAccountPhysicalCampusCode();
        Campus campus = newAccount.getAccountPhysicalCampus();
        if(campus == null && !physicalCampusCode.equals("")) {
            SpringServiceLocator.getPersistenceService().retrieveNonKeyFields(newAccount);
            campus = newAccount.getAccountPhysicalCampus();
            if(campus == null) {
                success &= false;
                putFieldError("accountNumber", KeyConstants.ERROR_DOCUMENT_ACCMAINT_ACCT_NMBR_NOT_ALLOWED, newAccount.getAccountNumber());
            }
        }
        
        //acct_state_cd must be valid in the sh_state_t table
        String stateCode = newAccount.getAccountStateCode();
        State state = newAccount.getAccountState();
        if(state == null && !stateCode.equals("")) {
            SpringServiceLocator.getPersistenceService().retrieveNonKeyFields(newAccount);
            state = newAccount.getAccountState();
            if(state == null) {
                success &= false;
                putFieldError("accountNumber", KeyConstants.ERROR_DOCUMENT_ACCMAINT_ACCT_NMBR_NOT_ALLOWED, newAccount.getAccountNumber());
            }
        }
        
        //acct_zip_cd must be a valid in the sh_zip_code_t table
        

        return success;
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
        
        //TODO - should we move this up to the calling method?
        boolean isClosed = newAccount.isAccountClosedIndicator();
        if(!isClosed) {
            return true;
        }
        //when closing an account, the account expiration date must be the current date or earlier
        Timestamp closeDate = newAccount.getAccountExpirationDate();
        Timestamp today = new Timestamp(Calendar.getInstance().getTimeInMillis());
        if(isClosed && !closeDate.before(today)) {
            //TODO - error message
            success &= false;
        }
        
        //when closing an account, a continuation account is required error message - "When closing an Account a Continuation Account Number entered on the Responsibility screen is required."
        if(isClosed && newAccount.getContinuationAccountNumber().equals("") ){
            //TODO - error message
            success &= false;
        }
        
        //in order to close an account, the account must either expire today or already be expired, 
        //must have no base budget, must have no pending ledger entries or pending labor ledger entries, 
        //must have no open encumbrances, must have no asset, liability or fund balance balances other than object code 9899 
        //(9899 is fund balance for us), and the process of closing income and expense into 9899 must take the 9899 balance to zero.
        
        //budget first - no idea (maybe through Options? AccountBalance?)
        //definitely looks like we need to pull AccountBalance
        /*<field-descriptor name="universityFiscalYear" column="UNIV_FISCAL_YR" jdbc-type="INTEGER" primarykey="true"/>
        <field-descriptor name="chartOfAccountsCode" column="FIN_COA_CD" jdbc-type="VARCHAR" primarykey="true" />
        <field-descriptor name="accountNumber" column="ACCOUNT_NBR" jdbc-type="VARCHAR" primarykey="true" />
        <field-descriptor name="subAccountNumber" column="SUB_ACCT_NBR" jdbc-type="VARCHAR" primarykey="true" />
        <field-descriptor name="objectCode" column="FIN_OBJECT_CD" jdbc-type="VARCHAR" primarykey="true" />
        <field-descriptor name="subObjectCode" column="FIN_SUB_OBJ_CD" jdbc-type="VARCHAR" primarykey="true" />
        */
        String fiscalYear = "2005";
        String coaCode = newAccount.getChartOfAccountsCode();
        String acctNumber = newAccount.getAccountNumber();
        String subAcctNumber = "";
        String objectCode = ""; //not sure which object code it wants
        String subObjectCode = "";
        BusinessObjectService busObjService = SpringServiceLocator.getBusinessObjectService();
        
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

        //When updating an account expiration date, the date must be today or later (except for C&G accounts).
        
        //a continuation account is required if the expiration date is completed.
        
        //if the expiration date is earlier than today, guidelines are not required.
        
        //If creating a new account if acct_expiration_dt is set and the fund_group is not "CG" then the acct_expiration_dt must be changed to a date that is today or later
        
        //acct_expiration_dt can not be before acct_effect_dt

        return success;
    }
    /**
     * 
     * This method checks to see if any Fund Group rules were violated
     * @param maintenanceDocument
     * @return false on rules violation
     */
    private boolean checkFundGroup(MaintenanceDocument maintenanceDocument) {
        boolean success = true;

        //on the account screen, if the fund group of the account is CG (contracts & grants) or 
        //RF (restricted funds), the restricted status code is set to 'R'. 
        //If the fund group is EN (endowment) or PF (plant fund) the value is not set by the system and 
        //must be set by the user, for all other fund groups the value is set to 'U'. R being restricted, 
        //U being unrestricted.
        //TODO - not sure how to get the Fund Group
        
        //an account in the general fund fund group cannot have a budget recording level of mixed.

        return success;
    }
    
    /**
     * 
     * This method checks to see if any SubFund Group rules were violated
     * @param maintenanceDocument
     * @return false on rules violation
     */
    private boolean checkSubFundGroup(MaintenanceDocument maintenanceDocument) {
        boolean success = true;
        
        //sub_fund_grp_cd on the account must be set to a valid sub_fund_grp_cd that exists in the ca_sub_fund_grp_t table
        newAccount.getSubFundGroupCode();
        
        //if the sub fund group code is plant fund, construction and major remodeling (PFCMR), the campus and building are required on the description screen for CAMS.
        newAccount.getSubFundGroupCode();
                
        //if sub_fund_grp_cd is 'PFCMR' then campus_cd must be entered
        //if sub_fund_grp_cd is 'PFCMR' then bldg_cd must be entered
        if(newAccount.getSubFundGroupCode().equalsIgnoreCase("PFCMR")) {
            if(newAccount.getAccountPhysicalCampusCode() == null ||
                    newAccount.getAccountPhysicalCampusCode().equals("")) {
                //TODO - error message
                success &= false;
            }
            if(newAccount.getAccountZipCode() != null && !newAccount.getAccountZipCode().equals("")) {
                HashMap primaryKeys = new HashMap();
                primaryKeys.put("postalZipCode", newAccount.getAccountZipCode());
                PostalZipCode zip = (PostalZipCode)SpringServiceLocator.getBusinessObjectService().findByPrimaryKey(PostalZipCode.class, primaryKeys);
                //now we can check the building code
                if(zip.getBuildingCode() == null || zip.getBuildingCode().equals("")) {
                    //TODO - error message
                    success &= false;
                }
            }
        }

        return success;
    }
    

}