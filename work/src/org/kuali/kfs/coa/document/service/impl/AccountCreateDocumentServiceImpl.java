/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.coa.document.service.impl;

import java.util.List;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.AccountAutoCreateDefaults;
import org.kuali.kfs.coa.document.service.AccountCreateDocumentService;
import org.kuali.kfs.coa.service.AccountAutoCreateDefaultsService;
import org.kuali.kfs.module.external.kc.dto.AccountCreationStatus;
import org.kuali.kfs.module.external.kc.dto.AccountParameters;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.ParameterService;

/**
 * This class is the default implementation of the AccountCreateDocumentService
 */

public class AccountCreateDocumentServiceImpl implements AccountCreateDocumentService {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountCreateDocumentServiceImpl.class);
    
    private DocumentService documentService;
    private ParameterService parameterService;
    private AccountAutoCreateDefaultsService accountAutoCreateDefaultsService;
    private DataDictionaryService dataDictionaryService;
    
    private AccountAutoCreateDefaults defaults;
    private List<String> errorCodes;

    public AccountCreationStatus createAccountForCGMaintenanceDocument(AccountParameters accountParameters) {
        
        defaults = accountAutoCreateDefaultsService.getByUnit(accountParameters.getUnit());
            
        // create a account
        String documentNumber = createAutomaticCGAccountMaintenanceDocument(createAccount(accountParameters));
        
        // create AccountCreationStatus to be returned
        AccountCreationStatus accountCreationStatus = new AccountCreationStatus();
        accountCreationStatus.setAccountNumber(accountParameters.getAccountNumber());
        accountCreationStatus.setChartOfAccountsCode(defaults.getChartOfAccountsCode());
        accountCreationStatus.setDocumentNumber(documentNumber);         
        accountCreationStatus.setErrorCodes(errorCodes);  //TODO: figure out how to return error codes better
        accountCreationStatus.setSuccess(true);
        
        return accountCreationStatus;
    }
    
    /**
     * 
     * This method creates an account to be used for automatic maintenance document
     * @param accountParameters
     * @return Account
     */
    private Account createAccount(AccountParameters accountParameters) {
                
        Account account = new Account();
        
        account.setChartOfAccountsCode(defaults.getChartOfAccountsCode());
        account.setOrganizationCode(defaults.getOrganizationCode());
        account.setAccountNumber(accountParameters.getAccountNumber());
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
        
        account.setAccountsFringesBnftIndicator(true);
        account.setFringeBenefitsChartOfAccount(defaults.getFringeBenefitsChartOfAccount());  
        //account.set??(defaults.getFringeBenefitAccountNumber());  // fringe benefit account number
        account.setFinancialHigherEdFunctionCd(defaults.getFinancialHigherEdFunctionCd());
        
        account.setAccountRestrictedStatusCode("R");
        account.setAccountRestrictedStatusDate(null);
        account.setEndowmentIncomeChartOfAccounts(null);
        account.setEndowmentIncomeAccountNumber(null);
        
        account.setAccountFiscalOfficerSystemIdentifier(defaults.getAccountFiscalOfficerUser().getName());  // fiscal officer principal name ?
        account.setAccountsSupervisorySystemsIdentifier(defaults.getAccountSupervisoryUser().getName());  //account supervisor principal name ?
        account.setAccountManagerSystemIdentifier(defaults.getAccountManagerUser().getName()); // account manager principal name ?
        account.getContinuationChartOfAccount().setChartOfAccountsCode(defaults.getContinuationChartOfAccount().getCode());
        account.setContinuationAccountNumber(defaults.getContinuationAccountNumber());

        account.setIncomeStreamChartOfAccounts(defaults.getIncomeStreamChartOfAccounts());
        account.getIncomeStreamAccount().setChartOfAccountsCode(defaults.getIncomeStreamChartOfAccounts().getCode()); // income stream account code ?
        
        account.setBudgetRecordingLevelCode(defaults.getBudgetRecordingLevelCode());
        account.setAccountSufficientFundsCode(defaults.getAccountSufficientFundsCode());
        
        account.setPendingAcctSufficientFundsIndicator(defaults.isPendingAcctSufficientFundsIndicator()); //Transaction processing sufficient funds check ?
        
        account.setExtrnlFinEncumSufficntFndIndicator(defaults.isExtrnlFinEncumSufficntFndIndicator());
        account.setIntrnlFinEncumSufficntFndIndicator(defaults.isIntrnlFinEncumSufficntFndIndicator());
        account.setPendingAcctSufficientFundsIndicator(defaults.isPendingAcctSufficientFundsIndicator());
        account.setFinPreencumSufficientFundIndicator(defaults.isFinPreencumSufficientFundIndicator());
        account.setFinancialObjectivePrsctrlIndicator(defaults.isFinancialObjectivePrsctrlIndicator());  // Object presence control indicator ?

        account.getContractControlChartOfAccounts().setChartOfAccountsCode(""); // contract control chart of accounts code
        account.setContractControlAccountNumber("");   // contract control account number
        account.setAcctIndirectCostRcvyTypeCd(defaults.getIndirectCostRcvyFinCoaCode());
        //account.getIndirectCostRecoveryAcct();   // indirect cost rate - accountParameters.getIndirectCostRate();
        
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
    private String createAutomaticCGAccountMaintenanceDocument(Account account) {

        //create a new maintenance document
        MaintenanceDocument maintenanceAccountDocument = (MaintenanceDocument) createCGAccountMaintenanceDocument();
        
        //set the account object in the maintenance document.
        maintenanceAccountDocument.getNewMaintainableObject().setBusinessObject(account);
        
        this.processAutomaticCGAccountMaintenanceDocument(maintenanceAccountDocument);
        
        return maintenanceAccountDocument.getDocumentNumber();
    }
    
    /**
     * This method will check the system parameter and takes appropriate workflow routing action
     * @param maintenanceAccountDocument
     */
    protected void processAutomaticCGAccountMaintenanceDocument(MaintenanceDocument maintenanceAccountDocument) {
       
        if (!checkIfAccountAutoCreateRouteExists()) {
            // error message since there is no system parameter has been setup yet....
            LOG.warn("ParseException: System Parameter ACCOUNTING_DOCUMENT_TYPE_NAME can not be determined.");
            throw new RuntimeException("System Parameter Exception: ACCOUNTING_DOCUMENT_TYPE_NAME system parameter does not exist");   
        }
        String accountAutoCreateRoute = getParameterService().getParameterValue(Account.class, KFSParameterKeyConstants.ACCOUNT_AUTO_CREATE_ROUTE);
        
        createRouteAutomaticCGAccountDocument(maintenanceAccountDocument, accountAutoCreateRoute);
    }

    /**
     * This method create and route automatic CG account maintenance document based on system parameter
     * @param maintenanceAccountDocument
     * @param accountAutoCreateRoute
     */
    private void createRouteAutomaticCGAccountDocument(MaintenanceDocument maintenanceAccountDocument, String accountAutoCreateRoute) {
        
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
            throw new RuntimeException("WorkflowException: createRouteAutomaticDocument failed");   
            
        }
    }
    
    /**
     * This method will use the DocumentService to create a new document....
     * @return document  returns a new document for the account document type
     */
    protected Document createCGAccountMaintenanceDocument() {
        try {
            Document document = documentService.getNewDocument(dataDictionaryService.getDocumentTypeNameByClass(Account.class));
            return document;            
        }
        catch (WorkflowException wfe) {
            throw new RuntimeException("WorkflowException: createAccountDocument has failed.  Unable to get a new document");   
        }
    }
    
    /**
     * This method checks for the system parameter for ACCOUNT_AUTO_CREATE_ROUTE
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
}
