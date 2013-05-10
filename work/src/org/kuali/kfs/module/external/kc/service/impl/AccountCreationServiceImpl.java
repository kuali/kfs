/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.external.kc.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.AccountGuideline;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryAccount;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.coa.service.ChartService;
import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleService;
import org.kuali.kfs.integration.cg.dto.AccountCreationStatusDTO;
import org.kuali.kfs.integration.cg.dto.AccountParametersDTO;
import org.kuali.kfs.module.external.kc.KcConstants;
import org.kuali.kfs.module.external.kc.businessobject.AccountAutoCreateDefaults;
import org.kuali.kfs.module.external.kc.businessobject.IndirectCostRecoveryAutoDefAccount;
import org.kuali.kfs.module.external.kc.service.AccountCreationService;
import org.kuali.kfs.module.external.kc.util.GlobalVariablesExtractHelper;
import org.kuali.kfs.module.external.kc.util.KcUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kns.datadictionary.validation.charlevel.AlphaNumericValidationPattern;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.document.authorization.DocumentAuthorizer;
import org.kuali.rice.kns.document.authorization.MaintenanceDocumentAuthorizerBase;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.MaintenanceDocumentDictionaryService;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.kuali.rice.krad.datadictionary.validation.ValidationPattern;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.rules.rule.event.BlanketApproveDocumentEvent;
import org.kuali.rice.krad.rules.rule.event.RouteDocumentEvent;
import org.kuali.rice.krad.rules.rule.event.SaveDocumentEvent;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KualiRuleService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

public class AccountCreationServiceImpl implements AccountCreationService {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountCreationServiceImpl.class);

    protected static final String ACCT_PREFIX_RESTRICTION = "PREFIXES";
    
    private DocumentService documentService;
    private ParameterService parameterService;
    private DataDictionaryService dataDictionaryService;
    private BusinessObjectService businessObjectService;

    /**
     * This is the web service method that creates a new account 1. Creates an account object using the parameters from KC and the
     * default Account table 2. Creates an account automatic maintenance document and puts the account object into it 3. Returns the
     * status object
     * 
     * @param AccountAutoCreateDefaults
     * @return AccountCreationStatusDTO
     */
    public AccountCreationStatusDTO createAccount(AccountParametersDTO accountParameters) {

        AccountCreationStatusDTO accountCreationStatus = new AccountCreationStatusDTO();
        accountCreationStatus.setErrorMessages(new ArrayList<String>());
        accountCreationStatus.setStatus(KcConstants.KcWebService.STATUS_KC_SUCCESS);

        // check to see if the user has the permission to create account
        String principalId = accountParameters.getPrincipalId();
        if (!isValidUser(principalId)) {
            this.setFailStatus(accountCreationStatus, KcUtils.getErrorMessage(KcConstants.AccountCreationService.ERROR_KC_DOCUMENT_INVALID_USER, new String[]{principalId}));
            return accountCreationStatus;
        }
        
            
        // get the defaults table
        String unitNumber = accountParameters.getUnit();
        AccountAutoCreateDefaults defaults = getAccountDefaults(unitNumber);

        if (defaults == null) {
            this.setFailStatus(accountCreationStatus, KcConstants.AccountCreationService.ERROR_KC_ACCOUNT_PARAMS_UNIT_NOT_DEFINED);
            return accountCreationStatus;
        }

        try {
            // create an account object
            Account account = createAccountObject(accountParameters, defaults);
            
            //if invalid chart/account number, failure status and return to KC
            if (! isValidAccount(account, accountCreationStatus)) {
                return accountCreationStatus;
            }
            // create an account automatic maintenance document
            createAutomaticCGAccountMaintenanceDocument(account, accountCreationStatus);

        } catch (Exception ex ) {
            this.setFailStatus(accountCreationStatus, KcConstants.AccountCreationService.ERROR_KC_DOCUMENT_ACCOUNT_GENERATION_PROBLEM);
            return accountCreationStatus;
        }
           
        // set required values to AccountCreationStatus
        if (accountCreationStatus.getStatus().equals(KcConstants.KcWebService.STATUS_KC_SUCCESS)) {
            accountCreationStatus.setAccountNumber(accountParameters.getAccountNumber());
            accountCreationStatus.setChartOfAccountsCode(defaults.getChartOfAccountsCode());
        }

        return accountCreationStatus;
    }

     /**
     * This method creates an account to be used for automatic maintenance document
     * 
     * @param AccountParametersDTO
     * @return Account
     */
    public Account createAccountObject(AccountParametersDTO parameters, AccountAutoCreateDefaults defaults) {

        Account account = new Account();

        // * Account: required but off campus indicator, closed, fringe benefit indicator, fringe benefit COA, endowment
        account.setChartOfAccountsCode(defaults.getChartOfAccountsCode());
        account.setOrganizationCode(defaults.getOrganizationCode());
        account.setAccountNumber(parameters.getAccountNumber()); // what if account number is null?
        account.setAccountName(parameters.getAccountName());
        account.setAccountPhysicalCampusCode(defaults.getAccountPhysicalCampusCode());
        if (parameters.getExpirationDate() != null) account.setAccountExpirationDate(new java.sql.Date(parameters.getExpirationDate().getTime()));
        if (parameters.getEffectiveDate() != null) account.setAccountEffectiveDate(new java.sql.Date(parameters.getEffectiveDate().getTime()));
        boolean isKCOverrideKFS = parameterService.getParameterValueAsBoolean(Account.class, KcConstants.AccountCreationService.PARAMETER_KC_OVERRIDES_KFS_DEFAULT_ACCOUNT_IND);
        if (isKCOverrideKFS) {
            // set the right address based on the system parameter RESEARCH_ADMIN_ACCOUNT_ADDRESS_TYPE
            List<String> addressTypes = new ArrayList<String>( parameterService.getParameterValuesAsString(Account.class, KcConstants.AccountCreationService.PARAMETER_KC_ACCOUNT_ADDRESS_TYPE) );
            for (String addressType : addressTypes) {
                if (addressType.equals(KcConstants.AccountCreationService.PI_ADDRESS_TYPE) && (!StringUtils.isBlank(parameters.getDefaultAddressStreetAddress()))) {
                    account.setAccountStreetAddress(parameters.getDefaultAddressStreetAddress());
                    account.setAccountCityName(parameters.getDefaultAddressCityName());
                    account.setAccountStateCode(parameters.getDefaultAddressStateCode());
                    account.setAccountZipCode(parameters.getDefaultAddressZipCode());
                    break;
                }
                else if (addressType.equals(KcConstants.AccountCreationService.ADMIN_ADDRESS_TYPE) && (!StringUtils.isBlank(parameters.getAdminContactAddressStreetAddress()))) {
                    account.setAccountStreetAddress(parameters.getAdminContactAddressStreetAddress());
                    account.setAccountCityName(parameters.getAdminContactAddressCityName());
                    account.setAccountStateCode(parameters.getAdminContactAddressStateCode());
                    account.setAccountZipCode(parameters.getAdminContactAddressZipCode());
                    break;
                }
             }
            
        } else {
            // use default address
            account.setAccountStreetAddress(defaults.getAccountStreetAddress());
            account.setAccountCityName(defaults.getAccountCityName());
            account.setAccountStateCode(defaults.getAccountStateCode());
            account.setAccountZipCode(defaults.getAccountZipCode());            
        }

        //set the following from parameters
        account.setAccountOffCampusIndicator(parameters.isOffCampusIndicator());        
        account.setFinancialHigherEdFunctionCd(parameters.getHigherEdFunctionCode());
        account.setAcctIndirectCostRcvyTypeCd(parameters.getIndirectCostTypeCode());
        account.setFinancialIcrSeriesIdentifier(parameters.getIndirectCostRate());
        account.setAccountGuideline(new AccountGuideline());
        account.getAccountGuideline().setAccountExpenseGuidelineText(parameters.getExpenseGuidelineText());
        account.getAccountGuideline().setAccountIncomeGuidelineText(parameters.getIncomeGuidelineText());
        account.getAccountGuideline().setAccountPurposeText(parameters.getPurposeText());

        account.setClosed(false);
        account.setAccountTypeCode(defaults.getAccountTypeCode());
        account.setSubFundGroupCode(defaults.getSubFundGroupCode());

        account.setAccountsFringesBnftIndicator(defaults.isAccountsFringesBnftIndicator());

        account.setReportsToAccountNumber(defaults.getReportsToAccountNumber());
        account.setReportsToChartOfAccountsCode(defaults.getReportsToChartOfAccountsCode());

        account.setAccountRestrictedStatusCode("R");
        account.setAccountRestrictedStatusDate(null);

        account.setEndowmentIncomeChartOfAccounts(null);
        account.setEndowmentIncomeAccountNumber(null);

        // * Accounts Responsibility: required - fiscal officer principal name, account supervisor principal name, account manager
        // principal name, budget record level, account sufficient funds
        account.setAccountFiscalOfficerSystemIdentifier(defaults.getAccountFiscalOfficerSystemIdentifier());

        account.setAccountsSupervisorySystemsIdentifier(defaults.getAccountsSupervisorySystemsIdentifier());
        account.setAccountManagerSystemIdentifier(defaults.getAccountManagerSystemIdentifier());

        account.setContinuationFinChrtOfAcctCd(defaults.getContinuationFinChrtOfAcctCd());
        account.setContinuationAccountNumber(defaults.getContinuationAccountNumber());

        account.setIncomeStreamAccountNumber(defaults.getIncomeStreamAccountNumber());
        account.setIncomeStreamChartOfAccounts(defaults.getIncomeStreamChartOfAccounts());
        account.setIncomeStreamFinancialCoaCode(defaults.getIncomeStreamFinancialCoaCode());

        account.setBudgetRecordingLevelCode(defaults.getBudgetRecordingLevelCode());
        account.setAccountSufficientFundsCode(defaults.getAccountSufficientFundsCode());

        account.setPendingAcctSufficientFundsIndicator(defaults.isPendingAcctSufficientFundsIndicator());
        account.setExtrnlFinEncumSufficntFndIndicator(defaults.isExtrnlFinEncumSufficntFndIndicator());
        account.setIntrnlFinEncumSufficntFndIndicator(defaults.isIntrnlFinEncumSufficntFndIndicator());        
        account.setFinPreencumSufficientFundIndicator(defaults.isFinPreencumSufficientFundIndicator());
        account.setFinancialObjectivePrsctrlIndicator(defaults.isFinancialObjectivePrsctrlIndicator());

        // * Contract and Grants: not required
        account.setContractControlFinCoaCode(null);
        account.setContractControlAccountNumber(null);        
        account.setContractsAndGrantsAccountResponsibilityId(defaults.getContractsAndGrantsAccountResponsibilityId());
        account.setAccountCfdaNumber(parameters.getCfdaNumber());

        // set up ICR distribution
        for (IndirectCostRecoveryAutoDefAccount indirectCostRecoveryAutoDefAccount : defaults.getIndirectCostRecoveryAutoDefAccounts()) {
            account.getIndirectCostRecoveryAccounts().add(createIndirectCostRecoveryAccount(indirectCostRecoveryAutoDefAccount, account.getChartOfAccountsCode(), account.getAccountNumber()));
        }
        return account;
    }    
  
    /** create an indirect cost recovery distribution account based on default
     * 
     */
    protected IndirectCostRecoveryAccount createIndirectCostRecoveryAccount(IndirectCostRecoveryAutoDefAccount indirectCostRecoveryAutoDefAccount, String chartCode, String acctNumber) {
        IndirectCostRecoveryAccount indirectCostRecoveryAccount = new IndirectCostRecoveryAccount();
        indirectCostRecoveryAccount.setChartOfAccountsCode(chartCode);
        indirectCostRecoveryAccount.setAccountNumber(acctNumber);
        indirectCostRecoveryAccount.setIndirectCostRecoveryAccount(indirectCostRecoveryAutoDefAccount.getIndirectCostRecoveryAccount());
        indirectCostRecoveryAccount.setIndirectCostRecoveryChartOfAccounts(indirectCostRecoveryAutoDefAccount.getIndirectCostRecoveryChartOfAccounts());
        indirectCostRecoveryAccount.setIndirectCostRecoveryAccountNumber(indirectCostRecoveryAutoDefAccount.getIndirectCostRecoveryAccountNumber());
        indirectCostRecoveryAccount.setIndirectCostRecoveryFinCoaCode(indirectCostRecoveryAutoDefAccount.getIndirectCostRecoveryFinCoaCode());
        indirectCostRecoveryAccount.setAccountLinePercent(indirectCostRecoveryAutoDefAccount.getAccountLinePercent());
        indirectCostRecoveryAccount.setActive(indirectCostRecoveryAutoDefAccount.isActive());
        return indirectCostRecoveryAccount;
    }
    
     protected void setFailStatus(AccountCreationStatusDTO accountCreationStatus, String message) {
        accountCreationStatus.getErrorMessages().add(message);
        accountCreationStatus.setStatus(KcConstants.KcWebService.STATUS_KC_FAILURE);
    }    
    
    /**
     * This method will create a maintenance document for CG account create, set its description and then sets the account business
     * object in it. The document will then be tried to route, save or blanket approve automatically based on the system parameter.
     * If successful, the method returns the newly created document number to the caller.
     * 
     * @return documentNumber returns the documentNumber
     * @see org.kuali.kfs.coa.document.service.CreateAccountService#createAutomaticCGAccountMaintenanceDocument()
     */
    protected void createAutomaticCGAccountMaintenanceDocument(Account account, AccountCreationStatusDTO accountCreationStatus) {

        // create a new maintenance document
        MaintenanceDocument maintenanceAccountDocument = (MaintenanceDocument) createCGAccountMaintenanceDocument(accountCreationStatus);

        if (ObjectUtils.isNotNull(maintenanceAccountDocument)) {
            // set document header description...
            maintenanceAccountDocument.getDocumentHeader().setDocumentDescription(KcConstants.AccountCreationService.AUTOMATCICG_ACCOUNT_MAINTENANCE_DOCUMENT_DESCRIPTION);

            // set the account object in the maintenance document.
            maintenanceAccountDocument.getNewMaintainableObject().setBusinessObject(account);
            maintenanceAccountDocument.getNewMaintainableObject().setMaintenanceAction(KRADConstants.MAINTENANCE_NEW_ACTION);
            // the maintenance document will now be routed based on the system parameter value for routing.
            createRouteAutomaticCGAccountDocument(maintenanceAccountDocument, accountCreationStatus);
        }
    }
    
    /**
     * This method processes the workflow document actions like save, route and blanket approve depending on the
     * ACCOUNT_AUTO_CREATE_ROUTE system parameter value. If the system parameter value is not of save or submit or blanketapprove,
     * put an error message and quit. Throws an document WorkflowException if the specific document action fails to perform.
     * 
     * @param maintenanceAccountDocument, errorMessages
     * @return
     */
    protected void createRouteAutomaticCGAccountDocument(MaintenanceDocument maintenanceAccountDocument, AccountCreationStatusDTO accountCreationStatus) {

        try {
            String accountAutoCreateRouteValue = getParameterService().getParameterValueAsString(Account.class, KcConstants.AccountCreationService.PARAMETER_KC_ACCOUNT_ADMIN_AUTO_CREATE_ACCOUNT_WORKFLOW_ACTION);

            // if the accountAutoCreateRouteValue is not save or submit or blanketApprove then put an error message and quit.
            if (!accountAutoCreateRouteValue.equalsIgnoreCase(KFSConstants.WORKFLOW_DOCUMENT_SAVE) && !accountAutoCreateRouteValue.equalsIgnoreCase("submit") && !accountAutoCreateRouteValue.equalsIgnoreCase(KFSConstants.WORKFLOW_DOCUMENT_BLANKET_APPROVE)) {
                this.setFailStatus( accountCreationStatus, KcConstants.AccountCreationService.ERROR_KC_DOCUMENT_SYSTEM_PARAMETER_INCORRECT_DOCUMENT_ACTION_VALUE);
                return;
            }

            if (accountAutoCreateRouteValue.equalsIgnoreCase(KFSConstants.WORKFLOW_DOCUMENT_SAVE)) {
                
                //attempt to save if apply rules were successful and there are no errors
                boolean rulesPassed = SpringContext.getBean(KualiRuleService.class).applyRules(new SaveDocumentEvent(maintenanceAccountDocument));
                
                if( rulesPassed && GlobalVariables.getMessageMap().hasNoErrors()){
                    getDocumentService().saveDocument(maintenanceAccountDocument);
                }else{
                    //get errors from apply rules invocation, also clears global variables
                    accountCreationStatus.setErrorMessages(GlobalVariablesExtractHelper.extractGlobalVariableErrors());                        
                    try{
                        //save document, and catch VE's as we want to do this silently
                        getDocumentService().saveDocument(maintenanceAccountDocument);
                    }catch(ValidationException ve){}
                    
                    accountCreationStatus.setStatus(KcConstants.KcWebService.STATUS_KC_SUCCESS);                    
                    LOG.error( KcUtils.getErrorMessage(KcConstants.AccountCreationService.ERROR_KC_DOCUMENT_ACCOUNT_RULES_EXCEPTION, new String[]{maintenanceAccountDocument.getDocumentNumber()}));
                }
                
            }
            else if (accountAutoCreateRouteValue.equalsIgnoreCase(KFSConstants.WORKFLOW_DOCUMENT_BLANKET_APPROVE)) {
                                
                //attempt to blanket approve if apply rules were successful and there are no errors
                boolean rulesPassed = SpringContext.getBean(KualiRuleService.class).applyRules(new BlanketApproveDocumentEvent(maintenanceAccountDocument));
                
                if( rulesPassed && GlobalVariables.getMessageMap().hasNoErrors()){
                    getDocumentService().blanketApproveDocument(maintenanceAccountDocument, "", null);
                }else{
                    //get errors from apply rules invocation, also clears global variables
                    accountCreationStatus.setErrorMessages(GlobalVariablesExtractHelper.extractGlobalVariableErrors());                        
                    try{
                        //save document, and catch VE's as we want to do this silently
                        getDocumentService().saveDocument(maintenanceAccountDocument);
                    }catch(ValidationException ve){}
                    
                    accountCreationStatus.setStatus(KcConstants.KcWebService.STATUS_KC_SUCCESS);                    
                    LOG.error( KcUtils.getErrorMessage(KcConstants.AccountCreationService.ERROR_KC_DOCUMENT_ACCOUNT_RULES_EXCEPTION, new String[]{maintenanceAccountDocument.getDocumentNumber()}));
                }

            }
            else if (accountAutoCreateRouteValue.equalsIgnoreCase("submit")) {

                //attempt to route if apply rules were successful and there are no errors
                boolean rulesPassed = SpringContext.getBean(KualiRuleService.class).applyRules(new RouteDocumentEvent(maintenanceAccountDocument));
                
                if( rulesPassed && GlobalVariables.getMessageMap().hasNoErrors()){
                    getDocumentService().routeDocument(maintenanceAccountDocument, "", null);
                }else{
                    //get errors from apply rules invocation, also clears global variables
                    accountCreationStatus.setErrorMessages(GlobalVariablesExtractHelper.extractGlobalVariableErrors());                        
                    try{
                        //save document, and catch VE's as we want to do this silently
                        getDocumentService().saveDocument(maintenanceAccountDocument);
                    }catch(ValidationException ve){}
                    
                    accountCreationStatus.setStatus(KcConstants.KcWebService.STATUS_KC_SUCCESS);                    
                    LOG.error( KcUtils.getErrorMessage(KcConstants.AccountCreationService.ERROR_KC_DOCUMENT_ACCOUNT_RULES_EXCEPTION, new String[]{maintenanceAccountDocument.getDocumentNumber()}));
                }

            }

            // set the document number
            accountCreationStatus.setDocumentNumber(maintenanceAccountDocument.getDocumentNumber());

        }
        catch (WorkflowException wfe) {

            LOG.error( KcUtils.getErrorMessage(KcConstants.AccountCreationService.ERROR_KC_DOCUMENT_WORKFLOW_EXCEPTION_DOCUMENT_ACTIONS, null) + ": " + wfe.getMessage());
            accountCreationStatus.setStatus(KcConstants.KcWebService.STATUS_KC_FAILURE);
            accountCreationStatus.getErrorMessages().add( KcUtils.getErrorMessage(KcConstants.AccountCreationService.WARNING_KC_DOCUMENT_WORKFLOW_EXCEPTION_DOCUMENT_ACTIONS, null) + ": " + wfe.getMessage());
            
            try {
                // save it even though it fails to route or blanket approve the document
                try{
                    getDocumentService().saveDocument(maintenanceAccountDocument);
                }catch(ValidationException ve){
                    //ok to catch validation exceptions at this point
                }
                accountCreationStatus.setDocumentNumber(maintenanceAccountDocument.getDocumentNumber());
                accountCreationStatus.setStatus(KcConstants.KcWebService.STATUS_KC_SUCCESS);
            }
            catch (WorkflowException e) {
                LOG.error( KcUtils.getErrorMessage(KcConstants.AccountCreationService.WARNING_KC_DOCUMENT_WORKFLOW_EXCEPTION_DOCUMENT_ACTIONS, null) + ": " + e.getMessage());
                accountCreationStatus.setErrorMessages(GlobalVariablesExtractHelper.extractGlobalVariableErrors());
                accountCreationStatus.setStatus(KcConstants.KcWebService.STATUS_KC_FAILURE);
            }

        }
        catch (Exception ex) {

            LOG.error("Unknown exception occurred: " + ex.getMessage());
            accountCreationStatus.setStatus(KcConstants.KcWebService.STATUS_KC_FAILURE);
            accountCreationStatus.setErrorMessages(GlobalVariablesExtractHelper.extractGlobalVariableErrors());
            accountCreationStatus.getErrorMessages().add(KcUtils.getErrorMessage(KcConstants.AccountCreationService.WARNING_KC_DOCUMENT_WORKFLOW_EXCEPTION_DOCUMENT_ACTIONS, null) + ": " + ex.getMessage());

            try {
                // save it even though it fails to route or blanket approve the document
                try{
                    getDocumentService().saveDocument(maintenanceAccountDocument);
                }catch(ValidationException ve){
                    //ok to catch validation exceptions at this point
                }
                accountCreationStatus.setDocumentNumber(maintenanceAccountDocument.getDocumentNumber());
                accountCreationStatus.setStatus(KcConstants.KcWebService.STATUS_KC_SUCCESS);
            }
            catch (WorkflowException e) {
                LOG.error( KcUtils.getErrorMessage(KcConstants.AccountCreationService.WARNING_KC_DOCUMENT_WORKFLOW_EXCEPTION_DOCUMENT_ACTIONS, null) + ": " + e.getMessage());
                accountCreationStatus.setErrorMessages(GlobalVariablesExtractHelper.extractGlobalVariableErrors());
                accountCreationStatus.setStatus(KcConstants.KcWebService.STATUS_KC_FAILURE);
            }
        }
    }

    /**
     * This method will use the DocumentService to create a new document. The documentTypeName is gathered by using
     * MaintenanceDocumentDictionaryService which uses Account class to get the document type name.
     * 
     * @param AccountCreationStatusDTO
     * @return document returns a new document for the account document type or null if there is an exception thrown.
     */
    public Document createCGAccountMaintenanceDocument(AccountCreationStatusDTO accountCreationStatus) {

        boolean internalUserSession = false;
        try {
            if (GlobalVariables.getUserSession() == null) {
                internalUserSession = true;
                GlobalVariables.setUserSession(new UserSession(KFSConstants.SYSTEM_USER));
                GlobalVariables.clear();
            }
            Document document = getDocumentService().getNewDocument(SpringContext.getBean(MaintenanceDocumentDictionaryService.class).getDocumentTypeName(Account.class));
            return document;

        }
        catch (Exception e) {
            accountCreationStatus.setErrorMessages(GlobalVariablesExtractHelper.extractGlobalVariableErrors());
            accountCreationStatus.setStatus(KcConstants.KcWebService.STATUS_KC_FAILURE);
            return null;
        }
        finally {
            // if a user session was established for this call, clear it our
            if (internalUserSession) {
                GlobalVariables.clear();
                GlobalVariables.setUserSession(null);
            }
        }
    }

    /**
     * This method looks up the default table
     * 
     * @param String unitNumber
     * @return AccountAutoCreateDefaults
     */
    protected AccountAutoCreateDefaults getAccountDefaults(String unitNumber) {

        AccountAutoCreateDefaults defaults = null;

        if (unitNumber == null || unitNumber.isEmpty()) {
            return null;
        }

        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("kcUnit", unitNumber);
        defaults = (AccountAutoCreateDefaults) businessObjectService.findByPrimaryKey(AccountAutoCreateDefaults.class, criteria);

        // if the matching defaults is null, try the parents in the hierarchy
        if (defaults == null) {

            List<String> parentUnits = null;
            try {
                parentUnits = SpringContext.getBean(ContractsAndGrantsModuleService.class).getParentUnits(unitNumber);
            }
            catch (Exception ex) {
                LOG.error( KcUtils.getErrorMessage(KcConstants.AccountCreationService.ERROR_KC_ACCOUNT_PARAMS_UNIT_NOTFOUND, null) + ": " + ex.getMessage());

                GlobalVariables.getMessageMap().putError(KcConstants.AccountCreationService.ERROR_KC_ACCOUNT_PARAMS_UNIT_NOTFOUND, "kcUnit", ex.getMessage());

            }

            if (parentUnits != null) {
                for (String unit : parentUnits) {
                    criteria.put("kcUnit", unit);
                    defaults = (AccountAutoCreateDefaults) businessObjectService.findByPrimaryKey(AccountAutoCreateDefaults.class, criteria);
                    if (defaults != null)
                        break;
                }
            }

        }

        return defaults;
    }
    
    /**
     * Check to see if the main link between KFS and KC is valid, namely the chart and account number.
     * If these two values have some kind of error, then we don't want to generate an Account document
     * and we'll want to return a failure to KC.
     * 
     * 
     * @param account
     * @param accountCreationStatus
     * @return
     */
    protected boolean isValidAccount(Account account, AccountCreationStatusDTO accountCreationStatus) {
        boolean isValid = true;
        String errorMessage = "";
        String strSize = "";
        
        if (account == null) {
            //account was not created
            setFailStatus(accountCreationStatus, KcConstants.AccountCreationService.ERROR_KC_DOCUMENT_ACCOUNT_GENERATION_PROBLEM);
            return false;
        }
        
        if (StringUtils.isBlank(account.getChartOfAccountsCode()) || StringUtils.isBlank(account.getAccountNumber())){
            //chart of accounts or account number blank            
            setFailStatus(accountCreationStatus, KcConstants.AccountCreationService.ERROR_KC_DOCUMENT_ACCOUNT_MISSING_CHART_OR_ACCT_NBR);
            return false;
        }

        if (!isValidChartCode(account.getChartOfAccountsCode())) {
            //the chart of accounts code is not valid
            setFailStatus( accountCreationStatus, KcConstants.AccountCreationService.AUTOMATCICG_ACCOUNT_MAINTENANCE_CHART_NOT_DEFINED);
            return false;
        }
        
        if (!isValidAccountNumberLength(account.getAccountNumber(), accountCreationStatus)){            
            //the account number is an inappropriate length
            //error set in method
            return false;            
        }                
        
        if (!checkUniqueAccountNumber(account.getAccountNumber())){
            //account is not unique
            setFailStatus( accountCreationStatus, KcUtils.getErrorMessage(KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_ACCT_NMBR_NOT_UNIQUE, new String[]{account.getAccountNumber()}));
            return false;
        }

        if (isValidChartAccount(account.getChartOfAccountsCode(), account.getAccountNumber())) {
            //the chart and account already exist
            setFailStatus( accountCreationStatus, KcConstants.AccountCreationService.AUTOMATCICG_ACCOUNT_MAINTENANCE_ACCT_ALREADY_DEFINED);
            return false;
        }
        
        if (!checkAccountNumberPrefix(account.getAccountNumber(), accountCreationStatus)){
            //account begins with invalid prefix
            //error set in method            
            return false;
        }
         
         return isValid;
    }

    public boolean accountsCanCrossCharts() {
        return SpringContext.getBean(AccountService.class).accountsCanCrossCharts();
    }

    public boolean isValidAccount(String accountNumber) {        
        Collection<Account> accounts = SpringContext.getBean(AccountService.class).getAccountsForAccountNumber(accountNumber);
        return (accounts != null && !accounts.isEmpty());
    }

    public boolean isValidChartCode(String chartOfAccountsCode) {        
        Chart chart = SpringContext.getBean(ChartService.class).getByPrimaryId(chartOfAccountsCode);
        return (chart != null);
    }


    public boolean isValidChartAccount(String chartOfAccountsCode, String accountNumber) {
        AccountService accountService = SpringContext.getBean(AccountService.class);
        Account account = accountService.getByPrimaryId(chartOfAccountsCode, accountNumber);
        return (account != null);
    }

    /**
     * Checks an account numbers exact length
     * 
     * @param accountNumber
     * @param size to be returned
     * @return
     */
    protected boolean isValidAccountNumberLength(String accountNumber, AccountCreationStatusDTO accountCreationStatus){
        
        boolean isValid = false;
        int fieldSize = -1;
        
        //grab account number length from DD and set size
        final org.kuali.rice.krad.datadictionary.BusinessObjectEntry entry = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(Account.class.getName());
        AttributeDefinition attributeDefinition = entry.getAttributeDefinition(KFSPropertyConstants.ACCOUNT_NUMBER);
        
        if(ObjectUtils.isNotNull(attributeDefinition)){
            final ValidationPattern validationPattern = attributeDefinition.getValidationPattern();
            
            if(ObjectUtils.isNotNull(validationPattern) && validationPattern instanceof AlphaNumericValidationPattern){
                AlphaNumericValidationPattern alphaPattern = (AlphaNumericValidationPattern)validationPattern;
                fieldSize = alphaPattern.getExactLength();
            }
        }
        
        //skip if account number null
        if(ObjectUtils.isNotNull(accountNumber)){
            
            //data dictionary defined size must equal length of incoming value
            if(fieldSize == accountNumber.length()){
                isValid = true;
            }
        }
        
        if(isValid == false){
            setFailStatus( accountCreationStatus, KcUtils.getErrorMessage(KcConstants.AccountCreationService.ERROR_KR_ALPHANUMERIC_VALIDATION_EXACT_LENGTH, new String[]{"account number", String.valueOf(fieldSize)}));
        }
        
        return isValid;        
    }
    
    /**
     * This method tests whether the accountNumber passed in is prefixed with an allowed prefix, or an illegal one. The illegal
     * prefixes are passed in as an array of strings.
     * 
     * @param accountNumber - The Account Number to be tested. 
     * @return false if the accountNumber starts with any of the illegalPrefixes, true otherwise
     */
    protected boolean checkAccountNumberPrefix(String accountNumber, AccountCreationStatusDTO accountCreationStatus){
    
        boolean success = true;        
        
        // Enforce institutionally specified restrictions on account number prefixes
        // (e.g. the account number cannot begin with a 3 or with 00.)
        // Only bother trying if there is an account string to test
        if (!StringUtils.isBlank(accountNumber)) {

            List<String> illegalValues = new ArrayList<String>( getParameterService().getParameterValuesAsString(Account.class, ACCT_PREFIX_RESTRICTION) );
            
            for (String illegalValue : illegalValues) {
                if (accountNumber.startsWith(illegalValue)) {
                    success = false;                                        
                    setFailStatus( accountCreationStatus, KcUtils.getErrorMessage(KFSKeyConstants.ERROR_DOCUMENT_ACCMAINT_ACCT_NMBR_NOT_ALLOWED, new String[] { accountNumber, illegalValue }));
                }
            }
        }
        
        return success;
    }

    /**
     * If accounts can't cross charts, then we need to make sure the account number is unique.
     * 
     * @param accountNumber
     * @return
     */
    protected boolean checkUniqueAccountNumber(String accountNumber) {
        boolean success = true;        
        
        // while account is not allowed to cross chart
        //and with an account number that already exists
        if (!SpringContext.getBean(AccountService.class).accountsCanCrossCharts() &&                
            !SpringContext.getBean(AccountService.class).getAccountsForAccountNumber(accountNumber).isEmpty()) {            
            success = false;            
        }
        
        return success;
    }

    /**
     * This method check to see if the user can create the account maintenance document and set the user session
     * 
     * @param String principalId
     * @return boolean
     */
    protected boolean isValidUser(String principalId) {

        PersonService personService = SpringContext.getBean(PersonService.class);
        if (principalId == null) return false;
        Person user = personService.getPerson(principalId);
        if (user == null) return false;
        DocumentAuthorizer documentAuthorizer = new MaintenanceDocumentAuthorizerBase();
        if (documentAuthorizer.canInitiate(SpringContext.getBean(MaintenanceDocumentDictionaryService.class).getDocumentTypeName(Account.class), user)) {
            // set the user session so that the user name can be displayed in the saved document
            GlobalVariables.setUserSession(new UserSession(user.getPrincipalName()));
            return true;
        }

        LOG.error(KcUtils.getErrorMessage(KcConstants.AccountCreationService.ERROR_KC_DOCUMENT_INVALID_USER, new String[]{principalId}));
        
        return false;
    }

    /**
     * Gets the documentService attribute.
     * 
     * @return Current value of documentService.
     */
    protected DocumentService getDocumentService() {
        return documentService;
    }

    /**
     * Sets the documentService attribute value.
     * 
     * @param documentService
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * Gets the parameterService attribute.
     * 
     * @return Returns the parameterService.
     */
    protected ParameterService getParameterService() {
        return parameterService;
    }

    /**
     * Sets the parameterService attribute value.
     * 
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    protected DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Gets the businessObjectService attribute.
     * 
     * @return Returns the businessObjectService.
     */
    protected BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }
    
}
