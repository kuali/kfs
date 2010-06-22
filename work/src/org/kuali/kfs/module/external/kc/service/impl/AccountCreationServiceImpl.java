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
import org.kuali.kfs.module.external.kc.dto.AccountCreationStatusDTO;
import org.kuali.kfs.module.external.kc.dto.AccountParametersDTO;
import org.kuali.kfs.module.external.kc.service.AccountCreationService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.MaintenanceDocumentDictionaryService;
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
    public AccountCreationStatusDTO createAccount(AccountParametersDTO accountParameters) {
        
        List<String> errorMessages = new ArrayList<String>();
        AccountCreationStatusDTO accountCreationStatus = new AccountCreationStatusDTO();
                
        // get the CGAD using unit code
        //TODO: check the units in the hierarchy if unit is not found        
        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("kcUnit", accountParameters.getUnit());   
        AccountAutoCreateDefaults defaults = (AccountAutoCreateDefaults) businessObjectService.findByPrimaryKey(AccountAutoCreateDefaults.class, criteria);
        
        if (defaults == null) {
            errorMessages.add(KcConstants.AccountCreationService.ERROR_KC_ACCOUNT_PARAMS_UNIT_NOTFOUND);
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
    protected Account createAccountObject(AccountParametersDTO accountParameters, AccountAutoCreateDefaults defaults, List<String> errorMessage) {
                
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
            account.getFringeBenefitsChartOfAccount().setChartOfAccountsCode(defaults.getFringeBenefitsChartOfAccount().getChartOfAccountsCode());
            account.setReportsToAccountNumber(defaults.getReportsToAccountNumber());    // fringe benefit account number
        } else {
            account.setAccountsFringesBnftIndicator(false);   
            account.getFringeBenefitsChartOfAccount().setChartOfAccountsCode(null);  
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
        account.getContinuationChartOfAccount().setChartOfAccountsCode(defaults.getContinuationChartOfAccount().getCode());
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
     * This method will create a maintenance document for CG account create, set its description and then sets the 
     * account business object in it.  The document will then be tried to route, save or blanket approve automatically 
     * based on the system parameter.  If successful, the method returns the newly created document number to
     * the caller.
     * @return documentNumber returns the documentNumber
     * @see org.kuali.kfs.coa.document.service.CreateAccountService#createAutomaticCGAccountMaintenanceDocument()
     */
    protected String createAutomaticCGAccountMaintenanceDocument(Account account, List<String> errorMessages) {
        //create a new maintenance document
        MaintenanceDocument maintenanceAccountDocument = (MaintenanceDocument) createCGAccountMaintenanceDocument(errorMessages);
        
        if (ObjectUtils.isNull(maintenanceAccountDocument)) {
            // if unable to get the document, put an error message and return an empty string
            errorMessages.add(KcConstants.AccountCreationService.ERROR_KC_DOCUMENT_UNABLE_TO_CREATE_CG_MAINTENANCE_DOCUMENT);
            return (KFSConstants.EMPTY_STRING);
        }
        
        // set document header description...
        maintenanceAccountDocument.getDocumentHeader().setDocumentDescription(KcConstants.AccountCreationService.AUTOMATCICG_ACCOUNT_MAINTENANCE_DOCUMENT_DESCRIPTION);
        
        //set the account object in the maintenance document.
        maintenanceAccountDocument.getNewMaintainableObject().setBusinessObject(account);
        
        // the maintenance document will now be routed based on the system parameter value for routing.
        this.processAutomaticCGAccountMaintenanceDocument(maintenanceAccountDocument, errorMessages);
        
        return maintenanceAccountDocument.getDocumentNumber();
    }
    
    /**
     * This method will call routing process method to either save, route or blanket approve the document.
     * If the routing process fails, then adds an error message for the calling service.
     * @param maintenanceAccountDocument, errorMessages
     */
    protected void processAutomaticCGAccountMaintenanceDocument(MaintenanceDocument maintenanceAccountDocument, List<String> errorMessages) {
            if (!createRouteAutomaticCGAccountDocument(maintenanceAccountDocument, errorMessages)) {
                errorMessages.add(KcConstants.AccountCreationService.ERROR_KC_DOCUMENT_UNABLE_TO_PROCESS_ROUTING  + maintenanceAccountDocument.getDocumentNumber());
            }
    }

    /**
     * This method processes the workflow document actions like save, route and blanket approve depending on the 
     * ACCOUNT_AUTO_CREATE_ROUTE system parameter value.
     * If the system parameter value is not of save or submit or blanketapprove, put an error message and quit.
     * Throws an document WorkflowException if the specific document action fails to perform.
     * 
     * @param maintenanceAccountDocument, errorMessages
     * @return success returns true if the workflow document action is successful else return false.
     */
    protected boolean createRouteAutomaticCGAccountDocument(MaintenanceDocument maintenanceAccountDocument, List<String> errorMessages) {
        boolean success = true;
        
        try {
            String accountAutoCreateRouteValue = getParameterService().getParameterValue(Account.class, KFSParameterKeyConstants.RESEARCH_ADMIN_AUTO_CREATE_ACCOUNT_WORKFLOW_ACTION);

            // if the accountAutoCreateRouteValue is not save or submit or blanketApprove then put an error message and quit.
            if (!accountAutoCreateRouteValue.equalsIgnoreCase(KFSConstants.WORKFLOW_DOCUMENT_SAVE) && 
                    !accountAutoCreateRouteValue.equalsIgnoreCase(KFSConstants.WORKFLOW_DOCUMENT_SAVE) &&
                    !accountAutoCreateRouteValue.equalsIgnoreCase(KFSConstants.WORKFLOW_DOCUMENT_BLANKET_APPROVE)) {
                errorMessages.add(KcConstants.AccountCreationService.ERROR_KC_DOCUMENT_SYSTEM_PARAMETER_INCORRECT_DOCUMENT_ACTION_VALUE);
                return false;
            }
            
            if (accountAutoCreateRouteValue.equalsIgnoreCase(KFSConstants.WORKFLOW_DOCUMENT_SAVE)) {
                getDocumentService().saveDocument(maintenanceAccountDocument);
            }
            else if (accountAutoCreateRouteValue.equalsIgnoreCase(KFSConstants.WORKFLOW_DOCUMENT_BLANKET_APPROVE)) {
                getDocumentService().blanketApproveDocument(maintenanceAccountDocument, "", null); 
            }
            else if (accountAutoCreateRouteValue.equalsIgnoreCase(KFSConstants.WORKFLOW_DOCUMENT_ROUTE)) {
                getDocumentService().approveDocument(maintenanceAccountDocument, "", null);
            }
            return success;
        }
        catch (WorkflowException wfe) {
            LOG.error(KcConstants.AccountCreationService.ERROR_KC_DOCUMENT_WORKFLOW_EXCEPTION_DOCUMENT_ACTIONS +  wfe.getMessage()); 
            errorMessages.add(KcConstants.AccountCreationService.ERROR_KC_DOCUMENT_WORKFLOW_EXCEPTION_DOCUMENT_ACTIONS +  wfe.getMessage());
            try {
                // save it even though it fails to route or blanket approve the document
                //TODO: need to save the KC user as well
                getDocumentService().saveDocument(maintenanceAccountDocument);
            } catch (WorkflowException we) {
                LOG.error(KcConstants.AccountCreationService.ERROR_KC_DOCUMENT_WORKFLOW_EXCEPTION_DOCUMENT_ACTIONS +  we.getMessage()); 
                errorMessages.add(KcConstants.AccountCreationService.ERROR_KC_DOCUMENT_WORKFLOW_EXCEPTION_DOCUMENT_ACTIONS +  we.getMessage());
            }
            return false;
        }
    }
    
    /**
     * This method will use the DocumentService to create a new document.  The documentTypeName is gathered by
     * using MaintenanceDocumentDictionaryService which uses Account class to get the document type name.
     * 
     * @param errorMessages list of error messages
     * @return document  returns a new document for the account document type or null if there is an exception thrown.
     */
    protected Document createCGAccountMaintenanceDocument(List<String> errorMessages) {
        try {
            Document document = getDocumentService().getNewDocument(SpringContext.getBean(MaintenanceDocumentDictionaryService.class).getDocumentTypeName(Account.class)); 
            
            return document;            
        }
        catch (Exception excp) {
            errorMessages.add(KcConstants.AccountCreationService.ERROR_KC_DOCUMENT_WORKFLOW_EXCEPTION_UNABLE_TO_CREATE_DOCUMENT + excp.getMessage());
            return null;
        }
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
