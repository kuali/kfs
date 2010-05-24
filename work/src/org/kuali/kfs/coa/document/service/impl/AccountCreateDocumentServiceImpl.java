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

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.AccountAutoCreateDefaults;
import org.kuali.kfs.coa.document.service.AccountCreateDocumentService;
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
    private DataDictionaryService dataDictionaryService;
    public Account createAccountForCGMaintenanceDocument(AccountParameters accountParams) {
        Account account = new Account();
        //TODO: get AccountAutoCreateDefaults using Spring
        AccountAutoCreateDefaults defaults = null;
        
        account.setChartOfAccountsCode(defaults.getChartOfAccountsCode());
        account.setOrganizationCode(defaults.getOrganizationCode());
        //account.setAccountNumber(accountParams.getAccountNumber());
        account.setAccountName(account.getAccountName());
        //account.setAccountPhysicalCampusCode(defaults.getAccountPhysicalCampusCode());
        account.setAccountExpirationDate(accountParams.getExpirationDate());
        account.setAccountEffectiveDate(accountParams.getEffectiveDate());
        //account.setPostalZipCode(defaults.getAccountPostalCode());
        account.setAccountCityName(defaults.getAccountCityName());
        account.setAccountStateCode(defaults.getAccountStateCode());
        account.setAccountStreetAddress(defaults.getAccountStreetAddress());
        account.setAccountOffCampusIndicator(accountParams.isOffCampusIndicator());
        account.setAccountTypeCode(defaults.getAccountTypeCode());
        account.setSubFundGroupCode(defaults.getSubFundGroupCode());
        account.setAccountsFringesBnftIndicator(true);
        account.setFringeBenefitsChartOfAccount(defaults.getFringeBenefitsChartOfAccount());
        //defaults.getFringeBenefitAccountNumber();
        account.setFinancialHigherEdFunctionCd(defaults.getFinancialHigherEdFunctionCd());
        account.setAccountRestrictedStatusCode("R");
        
        //account responsibility
        //account.setAccountFiscalOfficerSystemIdentifier(defaults.getFiscalOfficerPrincipalName());
        //account.setAccountsSupervisorySystemsIdentifier(defaults.getAccountSupervisorPrincipalName());
        //account.setAccountManagerSystemIdentifier(defaults.getAccountManagerPrincipalName());
        //account.setContinuationChartOfAccount(continuationChartOfAccount);
        account.setContinuationAccountNumber(defaults.getContinuationAccountNumber());
        //account.setIncomeStreamChartOfAccounts(defaults.getIncomeStreamAccountNumber());
        account.setIncomeStreamAccountNumber(defaults.getIncomeStreamAccountNumber());
        account.setBudgetRecordingLevelCode(defaults.getBudgetRecordingLevelCode());
        account.setAccountSufficientFundsCode(defaults.getAccountSufficientFundsCode());
        

        // contracts & grants
        //account.setContractControlAccountNumber(defaults.getCon)
        //contract control account number
        //account.setAcctIndirectCostRcvyTypeCd(defaults.getAccountI);
        //indirect cost rate
        account.setIndirectCostRcvyFinCoaCode(defaults.getIndirectCostRcvyFinCoaCode());
        account.setIndirectCostRecoveryAcctNbr(defaults.getIndirectCostRecoveryAcctNbr());
        account.setContractsAndGrantsAccountResponsibilityId(defaults.getContractsAndGrantsAccountResponsibilityId());
        
        account.setAccountCfdaNumber(accountParams.getCfdaNumber());
        //account.setAccountE
        
        accountParams.getIndirectCostRate();
        accountParams.getIndirectCostTypeCode();
        account.setAccountCfdaNumber(accountParams.getCfdaNumber());
        
        //account expense guideline text  accountParams.getExpenseGuidelineText();
        // account income guideline text accountParams.getIncomeGuidelineText();
        account.setGuidelinesAndPurposeSection("");
        
        //campus description
        //organziation description
        //responsibility center description
        //building campuse code
        //building code
        
        return account;
    }
    
    /**
     * This method will create an account automatic maintenance document and put the account object into it
     * @return documentNumber returns the documentNumber
     * 
     * @see org.kuali.kfs.coa.document.service.CreateAccountService#createAutomaticCGAccountMaintenanceDocument()
     */
    public String createAutomaticCGAccountMaintenanceDocument(Account account) {

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
