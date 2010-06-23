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

import javax.xml.namespace.QName;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.module.external.kc.KcConstants;
import org.kuali.kfs.module.external.kc.businessobject.AccountAutoCreateDefaults;
import org.kuali.kfs.module.external.kc.dto.AccountCreationStatusDTO;
import org.kuali.kfs.module.external.kc.dto.AccountParametersDTO;
import org.kuali.kfs.module.external.kc.service.AccountCreationService;
import org.kuali.kfs.module.external.kc.service.UnitService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.kfs.sys.KFSConstants.DocumentTypeAttributes;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.document.authorization.DocumentAuthorizer;
import org.kuali.rice.kns.document.authorization.MaintenanceDocumentAuthorizerBase;
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
     * @param AccountAutoCreateDefaults
     * @return AccountCreationStatusDTO
     */
    public AccountCreationStatusDTO createAccount(AccountParametersDTO accountParameters) {
        
        AccountCreationStatusDTO accountCreationStatus = new AccountCreationStatusDTO();
        accountCreationStatus.setErrorMessages(new ArrayList<String>());
        accountCreationStatus.setStatus(KcConstants.AccountCreationService.STATUS_KC_ACCOUNT_SUCCESS);
                
        // check to see if the user has the permission to create account
        String principalId = accountParameters.getPrincipalId();
        if (!canCreate(principalId)) {
            accountCreationStatus.getErrorMessages().add(KcConstants.AccountCreationService.ERROR_KC_DOCUMENT_NOT_ALLOWED_TO_CREATE_CG_MAINTENANCE_DOCUMENT);
            accountCreationStatus.setStatus(KcConstants.AccountCreationService.STATUS_KC_ACCOUNT_FAILURE);
            return accountCreationStatus;
        }
        
        // get the defaults table
        String unitNumber = accountParameters.getUnit();
        AccountAutoCreateDefaults defaults = getAccountDetaults(unitNumber);
        
        if (defaults == null) {
            accountCreationStatus.getErrorMessages().add(KcConstants.AccountCreationService.ERROR_KC_ACCOUNT_PARAMS_UNIT_NOTFOUND);
            accountCreationStatus.setStatus(KcConstants.AccountCreationService.STATUS_KC_ACCOUNT_FAILURE);
            return accountCreationStatus;
        } 
        
        // create an account object        
        Account account = createAccountObject(accountParameters, defaults, accountCreationStatus);
        
        // create an account automatic maintenance document
        String documentNumber = createAutomaticCGAccountMaintenanceDocument(account, accountCreationStatus, principalId);
        
        // create AccountCreationStatus to be returned            
        accountCreationStatus.setAccountNumber(accountParameters.getAccountNumber());
        accountCreationStatus.setChartOfAccountsCode(defaults.getChartOfAccountsCode());
        accountCreationStatus.setDocumentNumber(documentNumber);          

        return accountCreationStatus;
    }
    
    /**
     * 
     * This method creates an account to be used for automatic maintenance document
     * @param AccountParametersDTO
     * @return Account
     */
    protected Account createAccountObject(AccountParametersDTO accountParameters, AccountAutoCreateDefaults defaults, AccountCreationStatusDTO accountCreationStatus) {
                
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
    protected String createAutomaticCGAccountMaintenanceDocument(Account account, AccountCreationStatusDTO accountCreationStatus, String principalId) {
        
        //create a new maintenance document
        MaintenanceDocument maintenanceAccountDocument = (MaintenanceDocument) createCGAccountMaintenanceDocument(accountCreationStatus, principalId);
        
        if (ObjectUtils.isNull(maintenanceAccountDocument)) {
            // if unable to get the document, put an error message and return an empty string
            accountCreationStatus.getErrorMessages().add(KcConstants.AccountCreationService.ERROR_KC_DOCUMENT_UNABLE_TO_CREATE_CG_MAINTENANCE_DOCUMENT);
            accountCreationStatus.setStatus(KcConstants.AccountCreationService.STATUS_KC_ACCOUNT_FAILURE);
            return (KFSConstants.EMPTY_STRING);  // no documentNumber
        }
        
        // set document header description...
        maintenanceAccountDocument.getDocumentHeader().setDocumentDescription(KcConstants.AccountCreationService.AUTOMATCICG_ACCOUNT_MAINTENANCE_DOCUMENT_DESCRIPTION);
        
        //set the account object in the maintenance document.
        maintenanceAccountDocument.getNewMaintainableObject().setBusinessObject(account);
        
        // the maintenance document will now be routed based on the system parameter value for routing.
        //this.processAutomaticCGAccountMaintenanceDocument(maintenanceAccountDocument, accountCreationStatus);
        createRouteAutomaticCGAccountDocument(maintenanceAccountDocument, accountCreationStatus);
        
        return maintenanceAccountDocument.getDocumentNumber();
    }
    
    /**
     * This method will call routing process method to either save, route or blanket approve the document.
     * If the routing process fails, then adds an error message for the calling service.
     * @param maintenanceAccountDocument, errorMessages
     */
//    protected void processAutomaticCGAccountMaintenanceDocument(MaintenanceDocument maintenanceAccountDocument, AccountCreationStatusDTO accountCreationStatus) {
//        if (!createRouteAutomaticCGAccountDocument(maintenanceAccountDocument, accountCreationStatus)) {
//            accountCreationStatus.getErrorMessages().add(KcConstants.AccountCreationService.ERROR_KC_DOCUMENT_UNABLE_TO_PROCESS_ROUTING  + maintenanceAccountDocument.getDocumentNumber());
//            accountCreationStatus.setStatus(KcConstants.AccountCreationService.STATUS_KC_ACCOUNT_FAILURE);
//        }        
//    }

    /**
     * This method processes the workflow document actions like save, route and blanket approve depending on the 
     * ACCOUNT_AUTO_CREATE_ROUTE system parameter value.
     * If the system parameter value is not of save or submit or blanketapprove, put an error message and quit.
     * Throws an document WorkflowException if the specific document action fails to perform.
     * 
     * @param maintenanceAccountDocument, errorMessages
     * @return success returns true if the workflow document action is successful else return false.
     */
    protected boolean createRouteAutomaticCGAccountDocument(MaintenanceDocument maintenanceAccountDocument, AccountCreationStatusDTO accountCreationStatus) {
        boolean success = true;
        
        try {
            String accountAutoCreateRouteValue = getParameterService().getParameterValue(Account.class, KFSParameterKeyConstants.RESEARCH_ADMIN_AUTO_CREATE_ACCOUNT_WORKFLOW_ACTION);

            // if the accountAutoCreateRouteValue is not save or submit or blanketApprove then put an error message and quit.
            if (!accountAutoCreateRouteValue.equalsIgnoreCase(KFSConstants.WORKFLOW_DOCUMENT_SAVE) && 
                    !accountAutoCreateRouteValue.equalsIgnoreCase(KFSConstants.WORKFLOW_DOCUMENT_SAVE) &&
                    !accountAutoCreateRouteValue.equalsIgnoreCase(KFSConstants.WORKFLOW_DOCUMENT_BLANKET_APPROVE)) {
                
                accountCreationStatus.getErrorMessages().add(KcConstants.AccountCreationService.ERROR_KC_DOCUMENT_SYSTEM_PARAMETER_INCORRECT_DOCUMENT_ACTION_VALUE);
                accountCreationStatus.setStatus(KcConstants.AccountCreationService.STATUS_KC_ACCOUNT_FAILURE);
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
            accountCreationStatus.getErrorMessages().add(KcConstants.AccountCreationService.ERROR_KC_DOCUMENT_WORKFLOW_EXCEPTION_DOCUMENT_ACTIONS +  wfe.getMessage());
            try {
                // save it even though it fails to route or blanket approve the document
                getDocumentService().saveDocument(maintenanceAccountDocument);
            } catch (WorkflowException we) {
                LOG.error(KcConstants.AccountCreationService.WARNING_KC_DOCUMENT_WORKFLOW_EXCEPTION_DOCUMENT_ACTIONS +  we.getMessage()); 
                accountCreationStatus.getErrorMessages().add(KcConstants.AccountCreationService.WARNING_KC_DOCUMENT_WORKFLOW_EXCEPTION_DOCUMENT_ACTIONS +  we.getMessage());
            } finally {
                accountCreationStatus.setStatus(KcConstants.AccountCreationService.STATUS_KC_ACCOUNT_FAILURE);
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
    protected Document createCGAccountMaintenanceDocument(AccountCreationStatusDTO accountCreationStatus, String principalId) {
        try {
            Document document = getDocumentService().getNewDocument(SpringContext.getBean(MaintenanceDocumentDictionaryService.class).getDocumentTypeName(Account.class));
            
            // save the KC user
            document.getDocumentHeader().getWorkflowDocument().getRouteHeader().setInitiatorPrincipalId(principalId);
            
            return document;            
        }
        catch (Exception excp) {
            accountCreationStatus.getErrorMessages().add(KcConstants.AccountCreationService.ERROR_KC_DOCUMENT_WORKFLOW_EXCEPTION_UNABLE_TO_CREATE_DOCUMENT + excp.getMessage());
            accountCreationStatus.setStatus(KcConstants.AccountCreationService.STATUS_KC_ACCOUNT_FAILURE);
            return null;
        }
    }
       
    
    /**
     * This method check to see if the user can create the account maintenance document
     * @param String principalId
     * @return boolean
     */
    protected boolean canCreate(String principalId) {
        
        PersonService<Person> personService = KIMServiceLocator.getPersonService();
        Person user = personService.getPerson(principalId);
        DocumentAuthorizer auth = new MaintenanceDocumentAuthorizerBase();
               
        return auth.canInitiate(DocumentTypeAttributes.ACCOUNTING_DOCUMENT_TYPE_NAME, user);
    }
    
    /**
     * This method looks up the default table
     * @param String unitNumber
     * @return AccountAutoCreateDefaults
     */
    protected AccountAutoCreateDefaults getAccountDetaults(String unitNumber) {
        
        AccountAutoCreateDefaults defaults = null;
        
        if (unitNumber == null || unitNumber.isEmpty()) {
            return null;
        }

        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("kcUnit", unitNumber);   
        defaults = (AccountAutoCreateDefaults) businessObjectService.findByPrimaryKey(AccountAutoCreateDefaults.class, criteria);

        // if the matching defaults is null, try the parents in the hierarchy
        if (defaults == null) {
            //TODO: needs to get the KC service name and name space URI from KC
            UnitService unitService = (UnitService) GlobalResourceLoader.getService(new QName("KC", "unitServiceSOAP"));
            List<String> parentUnits = unitService.getParentUnits(unitNumber);
                       
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
