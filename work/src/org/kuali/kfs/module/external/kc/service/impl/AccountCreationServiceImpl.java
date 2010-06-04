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
import org.kuali.kfs.module.external.kc.KcConstants;
import org.kuali.kfs.module.external.kc.businessobject.AccountAutoCreateDefaults;
import org.kuali.kfs.module.external.kc.dto.AccountCreationStatus;
import org.kuali.kfs.module.external.kc.dto.AccountParameters;
import org.kuali.kfs.module.external.kc.service.AccountCreationService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.ParameterService;
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
     * @param accountParameters
     * @return AccountCreationStatus
     */
    public AccountCreationStatus createAccount(AccountParameters accountParameters) {
        
        List<String> errorMessages = new ArrayList<String>();
        AccountCreationStatus accountCreationStatus = new AccountCreationStatus();
                
        // get the CGAD using unit code
        //TODO: check the units in the hierarchy if unit is not found        
        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("kcUnit", accountParameters.getUnit());   
        AccountAutoCreateDefaults defaults = (AccountAutoCreateDefaults) businessObjectService.findByPrimaryKey(AccountAutoCreateDefaults.class, criteria);
        
        if (defaults == null) {
            errorMessages.add(KcConstants.AccountCreationService.ERROR_ACCOUNT_PARAMS_UNIT_NOTFOUND);
            accountCreationStatus.setErrorMessages(errorMessages); 
        } else {        
            // create an account object        
            Account account = createAccountObject(accountParameters, defaults, errorMessages);
            
            // create an account automatic maintenance document
            String documentNumber = createAutomaticCGAccountMaintenanceDocument(account, errorMessages);
            
            // create AccountCreationStatus to be returned            
            accountCreationStatus.setAccountNumber(accountParameters.getAccountNumber());
            accountCreationStatus.setChartOfAccountsCode(defaults.getChartOfAccountsCode());
            accountCreationStatus.setDocumentNumber(documentNumber);         
            accountCreationStatus.setErrorMessages(errorMessages); 
        }

        accountCreationStatus.setSuccess(errorMessages.isEmpty() ? true : false);

        return accountCreationStatus;
    }
    
    /**
     * 
     * This method creates an account to be used for automatic maintenance document
     * @param accountParameters
     * @return Account
     */
    protected Account createAccountObject(AccountParameters accountParameters, AccountAutoCreateDefaults defaults, List<String> errorMessage) {
                
        Account account = new Account();
        
        account.setChartOfAccountsCode(defaults.getChartOfAccountsCode());
        account.setOrganizationCode(defaults.getOrganizationCode());
        account.setAccountNumber(accountParameters.getAccountNumber());  // what if account number is null? 
        account.setAccountName(accountParameters.getAccountName());
        account.setAccountPhysicalCampusCode(defaults.getAccountPhysicalCampusCode());
        account.setAccountExpirationDate(new java.sql.Date(accountParameters.getExpirationDate().getTime()));
        account.setAccountEffectiveDate(new java.sql.Date(accountParameters.getEffectiveDate().getTime()));
        
        account.setAccountZipCode(defaults.getAccountZipCode()); 
        account.setAccountCityName(defaults.getAccountCityName());
        account.setAccountStateCode(defaults.getAccountStateCode());
        account.setAccountStreetAddress(defaults.getAccountStreetAddress());
        account.setAccountOffCampusIndicator(accountParameters.isOffCampusIndicator());
        
        account.setClosed(false);
        account.setAccountTypeCode(defaults.getAccountTypeCode());        
        account.setSubFundGroupCode(defaults.getSubFundGroupCode());
        
        if (defaults.isAccountsFringesBnftIndicator()) {
            account.setAccountsFringesBnftIndicator(true);   
            account.getFringeBenefitsChartOfAccounts().setChartOfAccountsCode(defaults.getFringeBenefitsChartOfAccount().getChartOfAccountsCode());
            account.setReportsToAccountNumber(defaults.getFringeBenefitAccountNumber());    // fringe benefit account number
        } else {
            account.setAccountsFringesBnftIndicator(false);   
            account.getFringeBenefitsChartOfAccounts().setChartOfAccountsCode(null);  
            account.setReportsToAccountNumber(null);
        }

        account.setFinancialHigherEdFunctionCd(defaults.getFinancialHigherEdFunctionCd());
        
        account.setAccountRestrictedStatusCode(KFSConstants.ACCOUNT_RESTRICTED_STATUS_CODE);
        account.setAccountRestrictedStatusDate(null);
        account.setEndowmentIncomeChartOfAccounts(null);
        account.setEndowmentIncomeAccountNumber(null);
        
        account.setAccountFiscalOfficerSystemIdentifier(defaults.getAccountFiscalOfficerSystemIdentifier()); 
        account.setAccountsSupervisorySystemsIdentifier(defaults.getAccountsSupervisorySystemsIdentifier());
        account.setAccountManagerSystemIdentifier(defaults.getAccountManagerSystemIdentifier());
        account.getContinuationChartOfAccounts().setChartOfAccountsCode(defaults.getContinuationChartOfAccount().getCode());
        account.setContinuationAccountNumber(defaults.getContinuationAccountNumber());

        account.getIncomeStreamChartOfAccounts().setChartOfAccountsCode(defaults.getIncomeStreamChartOfAccounts().getChartOfAccountsCode());  
        account.getIncomeStreamAccount().setAccountNumber(defaults.getIncomeStreamAccountNumber()); 
        
        account.setBudgetRecordingLevelCode(defaults.getBudgetRecordingLevelCode());
        account.setAccountSufficientFundsCode(defaults.getAccountSufficientFundsCode());
        
        account.setPendingAcctSufficientFundsIndicator(defaults.isPendingAcctSufficientFundsIndicator());         
        account.setExtrnlFinEncumSufficntFndIndicator(defaults.isExtrnlFinEncumSufficntFndIndicator());
        account.setIntrnlFinEncumSufficntFndIndicator(defaults.isIntrnlFinEncumSufficntFndIndicator());
        account.setPendingAcctSufficientFundsIndicator(defaults.isPendingAcctSufficientFundsIndicator());
        account.setFinPreencumSufficientFundIndicator(defaults.isFinPreencumSufficientFundIndicator());
        account.setFinancialObjectivePrsctrlIndicator(defaults.isFinancialObjectivePrsctrlIndicator());  

        account.getContractControlChartOfAccounts().setChartOfAccountsCode(KFSConstants.EMPTY_STRING); //TODO: contract control chart of accounts code ?
        account.setContractControlAccountNumber(KFSConstants.EMPTY_STRING);                            //TODO: contract control account number ?
        account.setAcctIndirectCostRcvyTypeCd(defaults.getIndirectCostRcvyFinCoaCode());
        //account.setFinancialIcrSeriesIdentifier();  // TODO: indirect cost rate
        
        account.setIndirectCostRecoveryChartOfAccountsCode(defaults.getIndirectCostRcvyFinCoaCode());
        account.setIndirectCostRecoveryAccountNumber(defaults.getIndirectCostRecoveryAcctNbr());
        account.setContractsAndGrantsAccountResponsibilityId(defaults.getContractsAndGrantsAccountResponsibilityId());
        
        account.setAccountCfdaNumber(accountParameters.getCfdaNumber());
        
        account.getAccountGuideline().setAccountExpenseGuidelineText(accountParameters.getExpenseGuidelineText());
        account.getAccountGuideline().setAccountIncomeGuidelineText(accountParameters.getIncomeGuidelineText());
        account.getAccountGuideline().setAccountPurposeText(accountParameters.getPurposeText());
       
        account.getAccountDescription().setCampusDescription(null);
        account.getAccountDescription().setOrganizationDescription(null);
        account.getAccountDescription().setResponsibilityCenterDescription(null);
        account.getAccountDescription().getBuilding().setCampusCode(null);
        account.getAccountDescription().setBuildingCode(null);
        
        return account;
    }
    
    /**
     * This method will create an account automatic maintenance document and put the account object into it
     * @return documentNumber returns the documentNumber
     * 
     * @see org.kuali.kfs.coa.document.service.CreateAccountService#createAutomaticCGAccountMaintenanceDocument()
     */
    protected String createAutomaticCGAccountMaintenanceDocument(Account account, List<String> errorMessages) {

        //create a new maintenance document
        MaintenanceDocument maintenanceAccountDocument = (MaintenanceDocument) createCGAccountMaintenanceDocument(errorMessages);
        
        if (ObjectUtils.isNull(maintenanceAccountDocument)) {
            //TODO: put the error message
            errorMessages.add("some error message here");
            return (KFSConstants.EMPTY_STRING);
        }
        
        // set document header description...
        maintenanceAccountDocument.getDocumentHeader().setDocumentDescription("Automatic CG Account Document Creation");
        
        //set the account object in the maintenance document.
        maintenanceAccountDocument.getNewMaintainableObject().setBusinessObject(account);
        
        // the maintenance document will now be routed based on the system parameter value for routing.
        this.processAutomaticCGAccountMaintenanceDocument(maintenanceAccountDocument, errorMessages);
        
        return maintenanceAccountDocument.getDocumentNumber();
    }
    
    /**
     * This method will check the system parameter and takes appropriate workflow routing action
     * @param maintenanceAccountDocument, errorMessages
     */
    protected void processAutomaticCGAccountMaintenanceDocument(MaintenanceDocument maintenanceAccountDocument, List<String> errorMessages) {
       
        if (!checkIfAccountAutoCreateRouteExists()) {
            // error message since there is no system parameter has been setup yet....
            LOG.warn("ParseException: System Parameter ACCOUNT_AUTO_CREATE_ROUTE can not be determined.");
            errorMessages.add("System Parameter Exception: ACCOUNT_AUTO_CREATE_ROUTE system parameter does not exist");
        }
        else {
            String accountAutoCreateRouteValue = getParameterService().getParameterValue(Account.class, KFSParameterKeyConstants.ACCOUNT_AUTO_CREATE_ROUTE);
            if (!createRouteAutomaticCGAccountDocument(maintenanceAccountDocument, accountAutoCreateRouteValue, errorMessages)) {
                errorMessages.add("Unable to process routing of the document: " + maintenanceAccountDocument.getDocumentNumber());
            }
        }
    }

    /**
     * This method create and route automatic CG account maintenance document based on system parameter
     * @param maintenanceAccountDocument, maintenanceAccountDocument, errorMessages
     * @param accountAutoCreateRoute
     */
    protected boolean createRouteAutomaticCGAccountDocument(MaintenanceDocument maintenanceAccountDocument, String accountAutoCreateRouteValue, List<String> errorMessages) {
        boolean success = true;
        
        try {
            if (accountAutoCreateRouteValue.equals(KFSConstants.WORKFLOW_DOCUMENT_NO_SUBMIT)) {
                documentService.saveDocument(maintenanceAccountDocument);
                return success;
            }
            else if (accountAutoCreateRouteValue.equals(KFSConstants.WORKFLOW_DOCUMENT_BLANKET_APPROVE)) {
                documentService.blanketApproveDocument(maintenanceAccountDocument, "", null); 
                return success;
            }
            else if (accountAutoCreateRouteValue.equals(KFSConstants.WORKFLOW_DOCUMENT_SUBMIT)) {
                documentService.approveDocument(maintenanceAccountDocument, "", null);
                return success;
            }
            else {
                return false;
            }
        }
        catch (WorkflowException wfe) {
            LOG.error("Account Auto Create Route process failed - " +  wfe.getMessage()); 
            errorMessages.add("WorkflowException: createRouteAutomaticDocument failed" +  wfe.getMessage());
            return false;
        }
    }
    
    /**
     * This method will use the DocumentService to create a new document....
     * @param errorMessages
     * @return document  returns a new document for the account document type or null if there is an exception thrown.
     */
    protected Document createCGAccountMaintenanceDocument(List<String> errorMessages) {
        try {
             Document document = documentService.getNewDocument(dataDictionaryService.getDataDictionary().getDocumentEntry(KFSConstants.ACCOUNT_MAINTENANCE_DOCUMENT_TYPE_DD_KEY).getDocumentTypeName());     
             return document;            
        }
        catch (Exception excp) {
            errorMessages.add("WorkflowException: createCGAccountMaintenanceDocument has failed.  Unable to get a new document" + excp.getMessage());
            return null;
        }
    }
    
    /**
     * This method checks for the system parameter ACCOUNT_AUTO_CREATE_ROUTE
     * @return true if ACCOUNT_AUTO_CREATE_ROUTE exists else false
     */
    protected boolean checkIfAccountAutoCreateRouteExists() {
        boolean parameterExists = true;
        
        // check to make sure the system parameter for run date check has already been setup...
        if (!getParameterService().parameterExists(Account.class, KFSParameterKeyConstants.ACCOUNT_AUTO_CREATE_ROUTE)) {
            return false;
        }
        
        return parameterExists;
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
