/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.external.kc.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.AccountGuideline;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.integration.cg.ContractsAndGrantsConstants;
import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleService;
import org.kuali.kfs.integration.cg.dto.AccountCreationStatusDTO;
import org.kuali.kfs.integration.cg.dto.AccountParametersDTO;
import org.kuali.kfs.integration.cg.service.AccountCreationService;
import org.kuali.kfs.module.external.kc.businessobject.AccountAutoCreateDefaults;
import org.kuali.kfs.module.external.kc.util.GlobalVariablesExtractHelper;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.UserSession;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.document.authorization.DocumentAuthorizer;
import org.kuali.rice.kns.document.authorization.MaintenanceDocumentAuthorizerBase;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.MaintenanceDocumentDictionaryService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.ObjectUtils;

public class AccountCreationServiceImpl implements AccountCreationService {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountCreationServiceImpl.class);
    
    private DocumentService documentService;
    private ParameterService parameterService;
    private DataDictionaryService dataDictionaryService;
    private BusinessObjectService businessObjectService;
       
    /**
     * This is the web service method that creates a new account  
     * 1. Creates an account object using the parameters from KC and the default Account table
     * 2. Creates an account automatic maintenance document and puts the account object into it
     * 3. Returns the status object
     * 
     * @param AccountAutoCreateDefaults
     * @return AccountCreationStatusDTO
     */
    public AccountCreationStatusDTO createAccount(AccountParametersDTO accountParameters) {
        
        AccountCreationStatusDTO accountCreationStatus = new AccountCreationStatusDTO();
        accountCreationStatus.setErrorMessages(new ArrayList<String>());
        accountCreationStatus.setStatus(ContractsAndGrantsConstants.KcWebService.STATUS_KC_SUCCESS);
                
        // check to see if the user has the permission to create account
        String principalId = accountParameters.getPrincipalId();
        if (!isValidUser(principalId)) {
            accountCreationStatus.getErrorMessages().add(ContractsAndGrantsConstants.AccountCreationService.ERROR_KC_DOCUMENT_NOT_ALLOWED_TO_CREATE_CG_MAINTENANCE_DOCUMENT);
            accountCreationStatus.setStatus(ContractsAndGrantsConstants.KcWebService.STATUS_KC_FAILURE);
            return accountCreationStatus;
        }
        
        // get the defaults table
        String unitNumber = accountParameters.getUnit();
        AccountAutoCreateDefaults defaults = getAccountDefaults(unitNumber);
        
        if (defaults == null) {
            accountCreationStatus.getErrorMessages().add(ContractsAndGrantsConstants.AccountCreationService.ERROR_KC_ACCOUNT_PARAMS_UNIT_NOTFOUND);
            accountCreationStatus.setStatus(ContractsAndGrantsConstants.KcWebService.STATUS_KC_FAILURE);
            return accountCreationStatus;
        } 
        
        // create an account object        
        Account account = createAccountObject(accountParameters, defaults);
        
        // create an account automatic maintenance document
        createAutomaticCGAccountMaintenanceDocument(account, accountCreationStatus);
        
        // set required values to AccountCreationStatus
        if (accountCreationStatus.getStatus().equals(ContractsAndGrantsConstants.KcWebService.STATUS_KC_SUCCESS)) {
            accountCreationStatus.setAccountNumber(accountParameters.getAccountNumber());
            accountCreationStatus.setChartOfAccountsCode(defaults.getChartOfAccountsCode());          
        }
                
        return accountCreationStatus;
    }
    
    /**
     * 
     * This method creates an account to be used for automatic maintenance document
     * @param AccountParametersDTO
     * @return Account
     */
    public Account createAccountObject(AccountParametersDTO parameters, AccountAutoCreateDefaults defaults) {
                
        Account account = new Account();
        
        //* Account: required but off campus indicator, closed, fringe benefit indicator, fringe benefit COA, endowment                 
        account.setChartOfAccountsCode(defaults.getChartOfAccountsCode());
        account.setOrganizationCode(defaults.getOrganizationCode());
        account.setAccountNumber(parameters.getAccountNumber());  // what if account number is null? 
        account.setAccountName(parameters.getAccountName());
        account.setAccountPhysicalCampusCode(defaults.getAccountPhysicalCampusCode());
        account.setAccountExpirationDate(new java.sql.Date(parameters.getExpirationDate().getTime()));
        account.setAccountEffectiveDate(new java.sql.Date(parameters.getEffectiveDate().getTime()));
        
        // set the right address based on the system parameter ACCOUNT_ADDRESS_TYPE
        List<String> addressTypes = parameterService.getParameterValues(Account.class, ContractsAndGrantsConstants.AccountCreationService.PARAMETER_KC_ACCOUNT_ADDRESS_TYPE);
        for (String addressType : addressTypes) {
            if (addressType.equals(ContractsAndGrantsConstants.AccountCreationService.ADMIN_ADDRESS_TYTPE) && !parameters.getDefaultAddressStreetAddress().isEmpty()) {         
                account.setAccountStreetAddress(parameters.getDefaultAddressStreetAddress());
                account.setAccountCityName(parameters.getDefaultAddressCityName());
                account.setAccountStateCode(parameters.getDefaultAddressStateCode());            
                account.setAccountZipCode(parameters.getDefaultAddressZipCode());
                break;
            } else if (addressType.equals(ContractsAndGrantsConstants.AccountCreationService.PAYMENT_ADDRESS_TYTPE) && !parameters.getAdminContactAddressStreetAddress().isEmpty()) { 
                account.setAccountStreetAddress(parameters.getAdminContactAddressStreetAddress());
                account.setAccountCityName(parameters.getAdminContactAddressCityName());
                account.setAccountStateCode(parameters.getAdminContactAddressStateCode());            
                account.setAccountZipCode(parameters.getAdminContactAddressZipCode());
                break;
            } else if (addressType.equals(ContractsAndGrantsConstants.AccountCreationService.DEFAULT_ADDRESS_TYTPE) && !parameters.getPaymentAddressStreetAddress().isEmpty()) { 
                account.setAccountStreetAddress(parameters.getPaymentAddressStreetAddress());
                account.setAccountCityName(parameters.getPaymentAddressCityName());
                account.setAccountStateCode(parameters.getPaymentAddressStateCode());            
                account.setAccountZipCode(parameters.getPaymentAddressZipCode());
                break;
            } 
        }
        
        account.setAccountOffCampusIndicator(parameters.isOffCampusIndicator());
        
        account.setClosed(false);
        account.setAccountTypeCode(defaults.getAccountTypeCode());        
        account.setSubFundGroupCode(defaults.getSubFundGroupCode());
        
        account.setAccountsFringesBnftIndicator(defaults.isAccountsFringesBnftIndicator());

        account.setReportsToAccountNumber(defaults.getReportsToAccountNumber());              
        account.setReportsToChartOfAccountsCode(defaults.getReportsToChartOfAccountsCode());

        account.setFinancialHigherEdFunctionCd(defaults.getFinancialHigherEdFunctionCd());
        
        account.setAccountRestrictedStatusCode("R");
        account.setAccountRestrictedStatusDate(null);
        
        account.setEndowmentIncomeChartOfAccounts(null);
        account.setEndowmentIncomeAccountNumber(null);
        
        //* Accounts Responsibility: required - fiscal officer principal name, account supervisor principal name, account manager principla name, budget record level, account sufficient funds         
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
        account.setPendingAcctSufficientFundsIndicator(defaults.isPendingAcctSufficientFundsIndicator());
        account.setFinPreencumSufficientFundIndicator(defaults.isFinPreencumSufficientFundIndicator());
        account.setFinancialObjectivePrsctrlIndicator(defaults.isFinancialObjectivePrsctrlIndicator());  

        //* Contract and Grants: not required 
        account.setContractControlChartOfAccounts(new Chart());
        account.getContractControlChartOfAccounts().setChartOfAccountsCode(defaults.getContractControlFinCoaCode());        
        account.setContractControlAccountNumber(defaults.getContractControlAccountNumber());                            
        account.setAcctIndirectCostRcvyTypeCd(defaults.getAcctIndirectCostRcvyTypeCd());
        account.setFinancialIcrSeriesIdentifier(defaults.getFinancialIcrSeriesIdentifier());         
        account.setIndirectCostRcvyFinCoaCode(defaults.getIndirectCostRcvyFinCoaCode());
        account.setIndirectCostRecoveryAcctNbr(defaults.getIndirectCostRecoveryAcctNbr());
        account.setContractsAndGrantsAccountResponsibilityId(defaults.getContractsAndGrantsAccountResponsibilityId());        
        account.setAccountCfdaNumber(parameters.getCfdaNumber());
        
        //* Guidelines and Purpose: required 
        account.setAccountGuideline(new AccountGuideline());
        account.getAccountGuideline().setAccountExpenseGuidelineText(parameters.getExpenseGuidelineText());
        account.getAccountGuideline().setAccountIncomeGuidelineText(parameters.getIncomeGuidelineText());
        account.getAccountGuideline().setAccountPurposeText(parameters.getPurposeText());
        return account;
    }
    
    /**
     * This method will create a maintenance document for CG account create, set its description and then sets the 
     * account business object in it.  The document will then be tried to route, save or blanket approve automatically 
     * based on the system parameter.  If successful, the method returns the newly created document number to
     * the caller.
     * @return documentNumber returns the documentNumber
     * @see org.kuali.kfs.coa.document.service.CreateAccountService#createAutomaticCGAccountMaintenanceDocument()
     */
    protected void createAutomaticCGAccountMaintenanceDocument(Account account, AccountCreationStatusDTO accountCreationStatus) {
        
        //create a new maintenance document
        MaintenanceDocument maintenanceAccountDocument = (MaintenanceDocument) createCGAccountMaintenanceDocument(accountCreationStatus);
 
        if (ObjectUtils.isNotNull(maintenanceAccountDocument)) {           
            // set document header description...
            maintenanceAccountDocument.getDocumentHeader().setDocumentDescription(ContractsAndGrantsConstants.AccountCreationService.AUTOMATCICG_ACCOUNT_MAINTENANCE_DOCUMENT_DESCRIPTION);
                                   
            // set the account object in the maintenance document.         
            maintenanceAccountDocument.getNewMaintainableObject().setBusinessObject(account);
            maintenanceAccountDocument.getNewMaintainableObject().setMaintenanceAction(KNSConstants.MAINTENANCE_NEW_ACTION);             
            // the maintenance document will now be routed based on the system parameter value for routing.
            createRouteAutomaticCGAccountDocument(maintenanceAccountDocument, accountCreationStatus);
        }
    }
    
    /**
     * This method processes the workflow document actions like save, route and blanket approve depending on the 
     * ACCOUNT_AUTO_CREATE_ROUTE system parameter value.
     * If the system parameter value is not of save or submit or blanketapprove, put an error message and quit.
     * Throws an document WorkflowException if the specific document action fails to perform.
     * 
     * @param maintenanceAccountDocument, errorMessages
     * @return 
     */
    protected void createRouteAutomaticCGAccountDocument(MaintenanceDocument maintenanceAccountDocument, AccountCreationStatusDTO accountCreationStatus) {
            
        try {            
            String accountAutoCreateRouteValue = getParameterService().getParameterValue(Account.class, ContractsAndGrantsConstants.AccountCreationService.PARAMETER_KC_ACCOUNT_ADMIN_AUTO_CREATE_ACCOUNT_WORKFLOW_ACTION);

            // if the accountAutoCreateRouteValue is not save or submit or blanketApprove then put an error message and quit.
            if (!accountAutoCreateRouteValue.equalsIgnoreCase(KFSConstants.WORKFLOW_DOCUMENT_SAVE) && 
                !accountAutoCreateRouteValue.equalsIgnoreCase(KFSConstants.WORKFLOW_DOCUMENT_ROUTE) &&
                !accountAutoCreateRouteValue.equalsIgnoreCase(KFSConstants.WORKFLOW_DOCUMENT_BLANKET_APPROVE)) 
            {                
                accountCreationStatus.getErrorMessages().add(ContractsAndGrantsConstants.AccountCreationService.ERROR_KC_DOCUMENT_SYSTEM_PARAMETER_INCORRECT_DOCUMENT_ACTION_VALUE);
                accountCreationStatus.setStatus(ContractsAndGrantsConstants.KcWebService.STATUS_KC_FAILURE);
                return;
            }
            
            if (accountAutoCreateRouteValue.equalsIgnoreCase(KFSConstants.WORKFLOW_DOCUMENT_SAVE)) {
                getDocumentService().saveDocument(maintenanceAccountDocument);
            }
            else if (accountAutoCreateRouteValue.equalsIgnoreCase(KFSConstants.WORKFLOW_DOCUMENT_BLANKET_APPROVE)) {
                getDocumentService().blanketApproveDocument(maintenanceAccountDocument, "", null); 
            }
            else if (accountAutoCreateRouteValue.equalsIgnoreCase(KFSConstants.WORKFLOW_DOCUMENT_ROUTE)) {
                getDocumentService().routeDocument(maintenanceAccountDocument, "", null);
            }         
            // set the document number
            accountCreationStatus.setDocumentNumber(maintenanceAccountDocument.getDocumentNumber());        
            
        }   catch (WorkflowException wfe) {

            LOG.error(ContractsAndGrantsConstants.AccountCreationService.ERROR_KC_DOCUMENT_WORKFLOW_EXCEPTION_DOCUMENT_ACTIONS +  wfe.getMessage()); 
            accountCreationStatus.setStatus(ContractsAndGrantsConstants.KcWebService.STATUS_KC_WARNING);
            accountCreationStatus.getErrorMessages().add(ContractsAndGrantsConstants.AccountCreationService.WARNING_KC_DOCUMENT_WORKFLOW_EXCEPTION_DOCUMENT_ACTIONS +  wfe.getMessage());
            try {
                // save it even though it fails to route or blanket approve the document
                getDocumentService().saveDocument(maintenanceAccountDocument);
                accountCreationStatus.setStatus(ContractsAndGrantsConstants.KcWebService.STATUS_KC_WARNING);
            } catch (WorkflowException e) {
                LOG.error(ContractsAndGrantsConstants.AccountCreationService.WARNING_KC_DOCUMENT_WORKFLOW_EXCEPTION_DOCUMENT_ACTIONS +  e.getMessage()); 
                accountCreationStatus.setErrorMessages(GlobalVariablesExtractHelper.extractGlobalVariableErrors());
                accountCreationStatus.setStatus(ContractsAndGrantsConstants.KcWebService.STATUS_KC_FAILURE);
            }             
            
        }  catch (Exception ex) {

            LOG.error(ContractsAndGrantsConstants.AccountCreationService.ERROR_KC_DOCUMENT_NOT_ALLOWED_TO_CREATE_CG_MAINTENANCE_DOCUMENT +  ex.getMessage()); 
            accountCreationStatus.setStatus(ContractsAndGrantsConstants.KcWebService.STATUS_KC_WARNING);
            accountCreationStatus.setErrorMessages(GlobalVariablesExtractHelper.extractGlobalVariableErrors());
            accountCreationStatus.getErrorMessages().add(ContractsAndGrantsConstants.AccountCreationService.WARNING_KC_DOCUMENT_WORKFLOW_EXCEPTION_DOCUMENT_ACTIONS+  ex.getMessage());
            try {
                // save it even though it fails to route or blanket approve the document
                getDocumentService().saveDocument(maintenanceAccountDocument);
                accountCreationStatus.setStatus(ContractsAndGrantsConstants.KcWebService.STATUS_KC_WARNING);
            } catch (WorkflowException e) {
                LOG.error(ContractsAndGrantsConstants.AccountCreationService.WARNING_KC_DOCUMENT_WORKFLOW_EXCEPTION_DOCUMENT_ACTIONS +  e.getMessage()); 
                accountCreationStatus.setErrorMessages(GlobalVariablesExtractHelper.extractGlobalVariableErrors());
                accountCreationStatus.setStatus(ContractsAndGrantsConstants.KcWebService.STATUS_KC_FAILURE);
            }             
        }
    }
    
    /**
     * This method will use the DocumentService to create a new document.  The documentTypeName is gathered by
     * using MaintenanceDocumentDictionaryService which uses Account class to get the document type name.
     * 
     * @param AccountCreationStatusDTO
     * @return document  returns a new document for the account document type or null if there is an exception thrown.
     */
    public Document createCGAccountMaintenanceDocument(AccountCreationStatusDTO accountCreationStatus) {
      
        boolean internalUserSession = false;
        try {
            if (GlobalVariables.getUserSession() == null) {
                internalUserSession = true;
                GlobalVariables.setUserSession(new UserSession(KNSConstants.SYSTEM_USER));
                GlobalVariables.clear();
            }
            Document document = getDocumentService().getNewDocument(SpringContext.getBean(MaintenanceDocumentDictionaryService.class).getDocumentTypeName(Account.class));                  
            return document;      
            
        } catch (Exception e) {
            accountCreationStatus.setErrorMessages(GlobalVariablesExtractHelper.extractGlobalVariableErrors());
            accountCreationStatus.setStatus(ContractsAndGrantsConstants.KcWebService.STATUS_KC_FAILURE);
            return null;
        } finally {
            // if a user session was established for this call, clear it our
            if ( internalUserSession ) {
                GlobalVariables.clear();
                GlobalVariables.setUserSession(null);
            }            
        }
    }       
    
    /**
     * This method check to see if the user can create the account maintenance document and set the user session
     * @param String principalId
     * @return boolean
     */
    protected boolean isValidUser(String principalId) {
        
        PersonService<Person> personService = SpringContext.getBean(PersonService.class);
        Person user = personService.getPerson(principalId);
        DocumentAuthorizer documentAuthorizer = new MaintenanceDocumentAuthorizerBase();
        if (documentAuthorizer.canInitiate(SpringContext.getBean(MaintenanceDocumentDictionaryService.class).getDocumentTypeName(Account.class), user)) {
            // set the user session so that the user name can be displayed in the saved document        
            GlobalVariables.setUserSession(new UserSession(user.getPrincipalName()));
            return true;
        } 
        
        return false;                
    }
    
    /**
     * This method looks up the default table
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
      
            List <String> parentUnits = null;
            try {
                parentUnits = SpringContext.getBean(ContractsAndGrantsModuleService.class).getParentUnits(unitNumber);
            } catch (Exception ex) {
                LOG.error(ContractsAndGrantsConstants.AccountCreationService.ERROR_KC_ACCOUNT_PARAMS_UNIT_NOTFOUND +  ex.getMessage()); 
                
                GlobalVariables.getMessageMap().putError(ContractsAndGrantsConstants.AccountCreationService.ERROR_KC_ACCOUNT_PARAMS_UNIT_NOTFOUND,"kcUnit", ex.getMessage());

            }

            if (parentUnits != null) {
                for (String unit : parentUnits) {
                    criteria.put("kcUnit", unit);    
                    defaults = (AccountAutoCreateDefaults) businessObjectService.findByPrimaryKey(AccountAutoCreateDefaults.class, criteria);
                    if (defaults != null) break;
                }
            }
 
        }        
                   
        return defaults;
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
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Gets the businessObjectService attribute. 
     * @return Returns the businessObjectService.
     */
    protected BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }
    
}
