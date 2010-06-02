/*
 * Copyright 2005 The Kuali Foundation
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
package org.kuali.kfs.module.external.kc.document.validation.impl;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.kuali.kfs.module.external.kc.businessobject.AccountAutoCreateDefaults;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.FundGroup;
import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryRateDetail;
import org.kuali.kfs.coa.businessobject.SubFundGroup;
import org.kuali.kfs.coa.service.SubFundGroupService;
import org.kuali.kfs.gl.service.BalanceService;
import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleService;
import org.kuali.kfs.integration.ld.LaborModuleService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.KFSConstants.SystemGroupParameterNames;
import org.kuali.kfs.sys.businessobject.Building;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.impl.KfsMaintenanceDocumentRuleBase;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.kns.service.ParameterEvaluator;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.MessageMap;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * Business rule(s) applicable to AccountMaintenance documents.
 */
public class AccountAutoCreateDefaultsRule extends KfsMaintenanceDocumentRuleBase {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountAutoCreateDefaultsRule.class);
/*
    protected static final String ACCT_PREFIX_RESTRICTION = "PREFIXES";
    protected static final String ACCT_CAPITAL_SUBFUNDGROUP = "CAPITAL_SUB_FUND_GROUPS";

    protected static final String GENERAL_FUND_CD = "GF";
    protected static final String RESTRICTED_FUND_CD = "RF";
    protected static final String ENDOWMENT_FUND_CD = "EN";
    protected static final String PLANT_FUND_CD = "PF";

    protected static final String RESTRICTED_CD_RESTRICTED = "R";
    protected static final String RESTRICTED_CD_UNRESTRICTED = "U";
    protected static final String RESTRICTED_CD_TEMPORARILY_RESTRICTED = "T";
    protected static final String BUDGET_RECORDING_LEVEL_MIXED = "M";
*/
    protected static SubFundGroupService subFundGroupService;
    protected static ParameterService parameterService;    
    
    protected BalanceService balanceService;
     protected ContractsAndGrantsModuleService contractsAndGrantsModuleService;

    protected AccountAutoCreateDefaults oldAccountAutoCreateDefaults;
    protected AccountAutoCreateDefaults newAccountAutoCreateDefaults;

    public AccountAutoCreateDefaultsRule() {

        // Pseudo-inject some services.
        //
        // This approach is being used to make it simpler to convert the Rule classes
        // to spring-managed with these services injected by Spring at some later date.
        // When this happens, just remove these calls to the setters with
        // SpringContext, and configure the bean defs for spring.
         this.setContractsAndGrantsModuleService(SpringContext.getBean(ContractsAndGrantsModuleService.class));
    }

    /**
     * This method sets the convenience objects like newAccountAutoCreateDefaults and oldAccountAutoCreateDefaults, so you have short and easy handles to the new and
     * old objects contained in the maintenance document. It also calls the BusinessObjectBase.refresh(), which will attempt to load
     * all sub-objects from the DB by their primary keys, if available.
     */
    public void setupConvenienceObjects() {

        // setup oldAccountAutoCreateDefaults convenience objects, make sure all possible sub-objects are populated
        oldAccountAutoCreateDefaults = (AccountAutoCreateDefaults) super.getOldBo();

        // setup newAccountAutoCreateDefaults convenience objects, make sure all possible sub-objects are populated
        newAccountAutoCreateDefaults = (AccountAutoCreateDefaults) super.getNewBo();
    }

    /**
     * This method calls the route rules but does not fail if any of them fail (this only happens on routing)
     * 
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {

        LOG.info("processCustomSaveDocumentBusinessRules called");
        // call the route rules to report all of the messages, but ignore the result
        processCustomRouteDocumentBusinessRules(document);

        // Save always succeeds, even if there are business rule failures
        return true;
    }

    /**
     * This method calls the following rules: checkAccountGuidelinesValidation checkEmptyValues checkGeneralRules checkCloseAccount
     * checkContractsAndGrants checkExpirationDate checkFundGroup checkSubFundGroup checkFiscalOfficerIsValidKualiUser this rule
     * will fail on routing
     * 
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {

        LOG.info("processCustomRouteDocumentBusinessRules called");
        setupConvenienceObjects();

        // default to success
        boolean success = true;

        // validate the embedded AccountGuideline object
        success &= checkEmptyValues(document);
        success &= checkGeneralRules(document);
        success &= checkContractsAndGrants(document);
        success &= checkIncomeStreamAccountRule();
        return success;
    }

    /**
     * This method checks the basic rules for empty values in an account and associated objects with this account If guidelines are
     * required for this Business Object it checks to make sure that it is filled out It also checks for partially filled out
     * reference keys on the following: continuationAccount incomeStreamAccount endowmentIncomeAccount reportsToAccount
     * contractControlAccount indirectCostRecoveryAccount
     * 
     * @param maintenanceDocument
     * @return false if any of these are empty
     */
    protected boolean checkEmptyValues(MaintenanceDocument maintenanceDocument) {

        LOG.info("checkEmptyValues called");

        boolean success = true;

   
        // this set confirms that all fields which are grouped (ie, foreign keys of a reference
        // object), must either be none filled out, or all filled out.
        success &= checkForPartiallyFilledOutReferenceForeignKeys("continuationAccount");
        success &= checkForPartiallyFilledOutReferenceForeignKeys("incomeStreamAccount");
        success &= checkForPartiallyFilledOutReferenceForeignKeys("indirectCostRecoveryAccount");

        return success;
    }

 
   
    /**
     * This method checks some of the general business rules associated with this document Calls the following rules:
     * accountNumberStartsWithAllowedPrefix isNonSystemSupervisorEditingAClosedAccount
     * hasTemporaryRestrictedStatusCodeButNoRestrictedStatusDate checkFringeBenefitAccountRule checkUserStatusAndType (on fiscal
     * officer, supervisor and manager) ensures that the fiscal officer, supervisor and manager are not the same
     * isContinuationAccountExpired
     * 
     * @param maintenanceDocument
     * @return false on rules violation
     */
    protected boolean checkGeneralRules(MaintenanceDocument maintenanceDocument) {

        LOG.info("checkGeneralRules called");
        Person fiscalOfficer = newAccountAutoCreateDefaults.getAccountFiscalOfficerUser();
        Person accountManager = newAccountAutoCreateDefaults.getAccountManagerUser();
        Person accountSupervisor = newAccountAutoCreateDefaults.getAccountSupervisoryUser();

        boolean success = true;
        
        // check FringeBenefit account rules
        success &= checkFringeBenefitAccountRule(newAccountAutoCreateDefaults);

        if (!getDocumentHelperService().getDocumentAuthorizer(maintenanceDocument).isAuthorized(maintenanceDocument, KFSConstants.ParameterNamespaces.CHART, KFSConstants.PermissionNames.SERVE_AS_FISCAL_OFFICER, fiscalOfficer.getPrincipalId())) {
            super.putFieldError("accountFiscalOfficerUser.principalName", KFSKeyConstants.ERROR_USER_MISSING_PERMISSION, new String[] {fiscalOfficer.getName(), KFSConstants.ParameterNamespaces.CHART, KFSConstants.PermissionNames.SERVE_AS_FISCAL_OFFICER});
			success = false;
        }
        if (!getDocumentHelperService().getDocumentAuthorizer(maintenanceDocument).isAuthorized(maintenanceDocument, KFSConstants.ParameterNamespaces.CHART, KFSConstants.PermissionNames.SERVE_AS_ACCOUNT_SUPERVISOR, accountSupervisor.getPrincipalId())) {
            super.putFieldError("accountSupervisoryUser.principalName", KFSKeyConstants.ERROR_USER_MISSING_PERMISSION, new String[] {accountSupervisor.getName(), KFSConstants.ParameterNamespaces.CHART, KFSConstants.PermissionNames.SERVE_AS_ACCOUNT_SUPERVISOR});
			success = false;
        }
        if (!getDocumentHelperService().getDocumentAuthorizer(maintenanceDocument).isAuthorized(maintenanceDocument, KFSConstants.ParameterNamespaces.CHART, KFSConstants.PermissionNames.SERVE_AS_ACCOUNT_MANAGER, accountManager.getPrincipalId())) {
            super.putFieldError("accountManagerUser.principalName", KFSKeyConstants.ERROR_USER_MISSING_PERMISSION, new String[] {accountManager.getName(), KFSConstants.ParameterNamespaces.CHART, KFSConstants.PermissionNames.SERVE_AS_ACCOUNT_MANAGER});
			success = false;
        }
        return success;
    }

   
    /**
     * the fringe benefit account (otherwise known as the reportsToAccount) is required if the fringe benefit code is set to N. The
     * fringe benefit code of the account designated to accept the fringes must be Y.
     * 
     * @param newAccountAutoCreateDefaults
     * @return
     */
    protected boolean checkFringeBenefitAccountRule(AccountAutoCreateDefaults newAccountAutoCreateDefaults) {

        boolean result = true;

        // if this account is selected as a Fringe Benefit Account, then we have nothing
        // to test, so exit
        if (newAccountAutoCreateDefaults.isAccountsFringesBnftIndicator()) {
            return true;
        }

   
        // fringe benefit chart of accounts code is required
        if (StringUtils.isBlank(newAccountAutoCreateDefaults.getReportsToChartOfAccountsCode())) {
            putFieldError("reportsToChartOfAccountsCode", KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_RPTS_TO_ACCT_REQUIRED_IF_FRINGEBENEFIT_FALSE);
            result &= false;
        }

        // if either of the fringe benefit account fields are not present, then we're done
        if (result == false) {
            return result;
        }

        return result;
    }

    
    /**
     * This method checks to see if any Contracts and Grants business rules were violated Calls the following sub-rules:
     * checkCgRequiredFields checkCgIncomeStreamRequired
     * 
     * @param maintenanceDocument
     * @return false on rules violation
     */
    protected boolean checkContractsAndGrants(MaintenanceDocument maintenanceDocument) {

        LOG.info("checkContractsAndGrants called");

        boolean success = true;

        // Certain C&G fields are required if the Account belongs to the CG Fund Group
        success &= checkCgRequiredFields(newAccountAutoCreateDefaults);
        return success;
    }

   
    /**
     * This method checks to make sure that if the contracts and grants fields are required they are entered correctly
     * 
     * @param newAccountAutoCreateDefaults
     * @return
     */
    protected boolean checkCgRequiredFields(AccountAutoCreateDefaults newAccountAutoCreateDefaults) {

        boolean result = true;

        // Certain C&G fields are required if the Account belongs to the CG Fund Group
        if (ObjectUtils.isNotNull(newAccountAutoCreateDefaults.getSubFundGroup())) {
            if (getSubFundGroupService().isForContractsAndGrants(newAccountAutoCreateDefaults.getSubFundGroup())) {
                result &= checkEmptyBOField("acctIndirectCostRcvyTypeCd", newAccountAutoCreateDefaults.getAcctIndirectCostRcvyTypeCd(), replaceTokens(KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_ICR_TYPE_CODE_CANNOT_BE_EMPTY));
                result &= checkEmptyBOField("indirectCostRecoveryChartOfAccountsCode", newAccountAutoCreateDefaults.getIndirectCostRecoveryChartOfAccountsCode(), replaceTokens(KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_ICR_CHART_CODE_CANNOT_BE_EMPTY));
                result &= checkEmptyBOField("indirectCostRecoveryAccountNumber", newAccountAutoCreateDefaults.getIndirectCostRecoveryAccountNumber(), replaceTokens(KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_ICR_ACCOUNT_CANNOT_BE_EMPTY));
             }
            else {
                // this is not a C&G fund group. So users should not fill in any fields in the C&G tab.
             }
        }
        return result;
    }

    /**
     * the income stream account is required if account's sub fund group code's fund group code is either GF or CG.
     * 
     * @param newAccount
     * @return true if fund group code (obtained through sub fund group) is in the system parameter INCOME_STREAM_ACCOUNT_REQUIRING_FUND_GROUPS (values GF;CG)
     * else return false.
     */
    protected boolean checkIncomeStreamAccountRule() {

        boolean result = true;
        // KFSMI-4877: if fund group is in system parameter values then income stream account number must exist.
        String fundGroupCode = newAccountAutoCreateDefaults.getSubFundGroup().getFundGroupCode();
        String incomeStreamRequiringFundGroupCode = SpringContext.getBean(ParameterService.class).getParameterValue(AccountAutoCreateDefaults.class, KFSConstants.ChartApcParms.INCOME_STREAM_ACCOUNT_REQUIRING_FUND_GROUPS);
        if (StringUtils.containsIgnoreCase(fundGroupCode, incomeStreamRequiringFundGroupCode)) {
            if (ObjectUtils.isNull(newAccountAutoCreateDefaults.getIncomeStreamAccount())) {
                GlobalVariables.getMessageMap().putError(KFSPropertyConstants.ACCOUNT_NUMBER, KFSKeyConstants.ERROR_DOCUMENT_BA_NO_INCOME_STREAM_ACCOUNT, newAccountAutoCreateDefaults.getIncomeStreamAccountNumber());
                result = false;
            }
        }

        return result;
    }
    
    /**
     * This method is a helper method that replaces error tokens with values for contracts and grants labels
     * 
     * @param errorConstant
     * @return error string that has had tokens "{0}" and "{1}" replaced
     */
    protected String replaceTokens(String errorConstant) {
        String cngLabel = getSubFundGroupService().getContractsAndGrantsDenotingAttributeLabel();
        String cngValue = getSubFundGroupService().getContractsAndGrantsDenotingValueForMessage();
        String result = getKualiConfigurationService().getPropertyString(errorConstant);
        result = StringUtils.replace(result, "{0}", cngLabel);
        result = StringUtils.replace(result, "{1}", cngValue);
        return result;
    }

 
    /**
     * Sets the contractsAndGrantsModuleService attribute value.
     * @param contractsAndGrantsModuleService The contractsAndGrantsModuleService to set.
     */
    public void setContractsAndGrantsModuleService(ContractsAndGrantsModuleService contractsAndGrantsModuleService) {
        this.contractsAndGrantsModuleService = contractsAndGrantsModuleService;
    }

    public SubFundGroupService getSubFundGroupService() {
        if ( subFundGroupService == null ) {
            subFundGroupService = SpringContext.getBean(SubFundGroupService.class);
        }
        return subFundGroupService;
    }

    public ParameterService getParameterService() {
        if ( parameterService == null ) {
            parameterService = SpringContext.getBean(ParameterService.class);
        }
        return parameterService;
    }

}

