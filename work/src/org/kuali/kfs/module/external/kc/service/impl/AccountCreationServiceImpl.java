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
import java.util.List;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.AccountAutoCreateDefaults;
import org.kuali.kfs.module.external.kc.dto.AccountCreationStatus;
import org.kuali.kfs.module.external.kc.dto.AccountParameters;
import org.kuali.kfs.module.external.kc.service.AccountAutoCreateDefaultsService;
import org.kuali.kfs.module.external.kc.service.AccountCreationService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.ObjectUtils;

public class AccountCreationServiceImpl implements AccountCreationService {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountCreationServiceImpl.class);
    
    private DocumentService documentService;
    private ParameterService parameterService;
    private AccountAutoCreateDefaultsService accountAutoCreateDefaultsService;
    private DataDictionaryService dataDictionaryService;
       
    public AccountCreationStatus createAccount(AccountParameters accountParameters) {
        
        List<String> errorMessages = new ArrayList<String>();
        
        //TODO: need to check the hierarchy if unit is null and to see if defaults is null
        AccountAutoCreateDefaults defaults = accountAutoCreateDefaultsService.getByUnit(accountParameters.getUnit());
            
        // create an account object        
        Account account = createAccountObject(accountParameters, defaults, errorMessages);
        
        // create an account automatic maintenance document
        //should check for empty string for documentNumber if failed to create a document in the calling method...
        String documentNumber = createAutomaticCGAccountMaintenanceDocument(account, errorMessages);
        
        // create AccountCreationStatus to be returned
        AccountCreationStatus accountCreationStatus = new AccountCreationStatus();
        accountCreationStatus.setAccountNumber(accountParameters.getAccountNumber());
        accountCreationStatus.setChartOfAccountsCode(defaults.getChartOfAccountsCode());
        accountCreationStatus.setDocumentNumber(documentNumber);         
        accountCreationStatus.setErrorMessages(errorMessages); 
        accountCreationStatus.setSuccess(errorMessages.size() < 1 ? true : false);
          
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
        
        account.setClosed(false); // null?
        account.setAccountTypeCode(defaults.getAccountTypeCode());        
        account.setSubFundGroupCode(defaults.getSubFundGroupCode());
        
        account.setAccountsFringesBnftIndicator(true);  // TODO: check the value from CGAD
        account.getFringeBenefitsChartOfAccount().setChartOfAccountsCode(defaults.getFringeBenefitsChartOfAccount().getChartOfAccountsCode());  // according to the indicator
        //account.set??(defaults.getFringeBenefitAccountNumber());  // TODO: fringe benefit account number does not exist in account ***

        account.setFinancialHigherEdFunctionCd(defaults.getFinancialHigherEdFunctionCd());
        
        account.setAccountRestrictedStatusCode(KFSConstants.ACCOUNT_RESTRICTED_STATUS_CODE);
        account.setAccountRestrictedStatusDate(null);
        account.setEndowmentIncomeChartOfAccounts(null);
        account.setEndowmentIncomeAccountNumber(null);
        
        account.setAccountFiscalOfficerSystemIdentifier(defaults.getAccountFiscalOfficerUser().getName()); //TODO: fiscal officer principal name ***
        account.setAccountsSupervisorySystemsIdentifier(defaults.getAccountSupervisoryUser().getName());   //TODO: account supervisor principal name ***
        account.setAccountManagerSystemIdentifier(defaults.getAccountManagerUser().getName());             //TODO: account manager principal name ***
        account.getContinuationChartOfAccount().setChartOfAccountsCode(defaults.getContinuationChartOfAccount().getCode());
        account.setContinuationAccountNumber(defaults.getContinuationAccountNumber());

        account.getIncomeStreamChartOfAccounts().setChartOfAccountsCode(defaults.getIncomeStreamChartOfAccounts().getChartOfAccountsCode());  // not object, but code ?
        account.getIncomeStreamAccount().setAccountNumber(defaults.getIncomeStreamAccountNumber()); // income stream account code or number ?
        
        account.setBudgetRecordingLevelCode(defaults.getBudgetRecordingLevelCode());
        account.setAccountSufficientFundsCode(defaults.getAccountSufficientFundsCode());
        
        account.setPendingAcctSufficientFundsIndicator(defaults.isPendingAcctSufficientFundsIndicator()); //TODO: transaction processing sufficient funds indicator is missing *** ?        
        account.setExtrnlFinEncumSufficntFndIndicator(defaults.isExtrnlFinEncumSufficntFndIndicator());
        account.setIntrnlFinEncumSufficntFndIndicator(defaults.isIntrnlFinEncumSufficntFndIndicator());
        account.setPendingAcctSufficientFundsIndicator(defaults.isPendingAcctSufficientFundsIndicator());
        account.setFinPreencumSufficientFundIndicator(defaults.isFinPreencumSufficientFundIndicator());
        account.setFinancialObjectivePrsctrlIndicator(defaults.isFinancialObjectivePrsctrlIndicator());  

        account.getContractControlChartOfAccounts().setChartOfAccountsCode(KFSConstants.EMPTY_STRING); //TODO: contract control chart of accounts code ?
        account.setContractControlAccountNumber(KFSConstants.EMPTY_STRING);                            //TODO: contract control account number ?
        account.setAcctIndirectCostRcvyTypeCd(defaults.getIndirectCostRcvyFinCoaCode());
        //account.getIndirectCostRecoveryAcct();   // TODO: indirect cost rate - accountParameters.getIndirectCostRate(); *** 
        
        account.setIndirectCostRcvyFinCoaCode(defaults.getIndirectCostRcvyFinCoaCode());
        account.setIndirectCostRecoveryAcctNbr(defaults.getIndirectCostRecoveryAcctNbr());
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
            return (KFSConstants.EMPTY_STRING);
        }
        
        //set the account object in the maintenance document.
        maintenanceAccountDocument.getNewMaintainableObject().setBusinessObject(account);
        
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
            String accountAutoCreateRoute = getParameterService().getParameterValue(Account.class, KFSParameterKeyConstants.ACCOUNT_AUTO_CREATE_ROUTE);
            createRouteAutomaticCGAccountDocument(maintenanceAccountDocument, accountAutoCreateRoute, errorMessages);
        }
    }

    /**
     * This method create and route automatic CG account maintenance document based on system parameter
     * @param maintenanceAccountDocument, maintenanceAccountDocument, errorMessages
     * @param accountAutoCreateRoute
     */
    protected void createRouteAutomaticCGAccountDocument(MaintenanceDocument maintenanceAccountDocument, String accountAutoCreateRoute, List<String> errorMessages) {
        
        try {
            if (accountAutoCreateRoute.equals(KFSConstants.WORKFLOW_DOCUMENT_NO_SUBMIT)) {
                documentService.saveDocument(maintenanceAccountDocument);
            }
            else if (accountAutoCreateRoute.equals(KFSConstants.WORKFLOW_DOCUMENT_BLANKET_APPROVE)) {
                documentService.blanketApproveDocument(maintenanceAccountDocument, "", null);                
            }
            else if (accountAutoCreateRoute.equals(KFSConstants.WORKFLOW_DOCUMENT_SUBMIT)) {
                documentService.approveDocument(maintenanceAccountDocument, "", null);
            }
        }
        catch (WorkflowException wfe) {
            LOG.error("Account Auto Create Route process failed - " +  wfe.getMessage()); 
            errorMessages.add("WorkflowException: createRouteAutomaticDocument failed" +  wfe.getMessage());
        }
    }
    
    /**
     * This method will use the DocumentService to create a new document....
     * @param errorMessages
     * @return document  returns a new document for the account document type or null if there is an exception thrown.
     */
    protected Document createCGAccountMaintenanceDocument(List<String> errorMessages) {
        try {
             Document document = documentService.getNewDocument(dataDictionaryService.getDocumentTypeNameByClass(Account.class));
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
    public DocumentService getDocumentService() {
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

    public DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    /**
     * Gets the accountAutoCreateDefaultsService attribute. 
     * @return Returns the accountAutoCreateDefaultsService.
     */
    public AccountAutoCreateDefaultsService getAccountAutoCreateDefaultsService() {
        return accountAutoCreateDefaultsService;
    }

    /**
     * Sets the accountAutoCreateDefaultsService attribute value.
     * @param accountAutoCreateDefaultsService The accountAutoCreateDefaultsService to set.
     */
    public void setAccountAutoCreateDefaultsService(AccountAutoCreateDefaultsService accountAutoCreateDefaultsService) {
        this.accountAutoCreateDefaultsService = accountAutoCreateDefaultsService;
    }
}
