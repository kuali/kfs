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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.fp.document.BudgetAdjustmentDocument;
import org.kuali.kfs.fp.document.web.struts.BudgetAdjustmentAction;
import org.kuali.kfs.module.external.kc.KcConstants;
import org.kuali.kfs.module.external.kc.businessobject.AccountAutoCreateDefaults;
import org.kuali.kfs.module.external.kc.dto.BudgetAdjustmentCreationStatusDTO;
import org.kuali.kfs.module.external.kc.dto.BudgetAdjustmentParametersDTO;
import org.kuali.kfs.module.external.kc.service.BudgetAdjustmentService;
import org.kuali.kfs.module.external.kc.service.UnitService;
import org.kuali.kfs.module.purap.businessobject.BulkReceivingView;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.kfs.sys.KFSConstants.DocumentTypeAttributes;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.resourceloader.GlobalResourceLoader;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.KIMServiceLocator;
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
import org.kuali.rice.kns.util.ObjectUtils;

public class BudgetAdjustmentServiceImpl implements BudgetAdjustmentService {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetAdjustmentServiceImpl.class);
    
    private DocumentService documentService;
    private ParameterService parameterService;
    private DataDictionaryService dataDictionaryService;
    private BusinessObjectService businessObjectService;
       
    /**
     * This is the web service method that facilitates budget adjustment  
     * 1. Creates an account object using the parameters from KC and the default Account table
     * 2. Creates an account automatic maintenance document and puts the account object into it
     * 3. Returns the status object
     * 
     * @param AccountAutoCreateDefaults
     * @return AccountCreationStatusDTO
     */
    public BudgetAdjustmentCreationStatusDTO createBudgetAdjustment(BudgetAdjustmentParametersDTO budgetAdjustmentParameters) {
        
        BudgetAdjustmentCreationStatusDTO budgetAdjustmentCreationStatus = new BudgetAdjustmentCreationStatusDTO();
        budgetAdjustmentCreationStatus.setErrorMessages(new ArrayList<String>());
        budgetAdjustmentCreationStatus.setStatus(KcConstants.AccountCreationService.STATUS_KC_ACCOUNT_SUCCESS);
                
        // check to see if the user has the permission to create account
        String principalId = budgetAdjustmentParameters.getPrincipalId();
        if (!isValidUser(principalId)) {
            budgetAdjustmentCreationStatus.getErrorMessages().add(KcConstants.AccountCreationService.ERROR_KC_DOCUMENT_NOT_ALLOWED_TO_CREATE_CG_MAINTENANCE_DOCUMENT);
            budgetAdjustmentCreationStatus.setStatus(KcConstants.AccountCreationService.STATUS_KC_ACCOUNT_FAILURE);
            return budgetAdjustmentCreationStatus;
        }
                
        // create an Budget Adjustment object        
        BudgetAdjustmentDocument budgetAdjustmentDoc = createBudgetAdjustmentObject(budgetAdjustmentParameters, budgetAdjustmentCreationStatus);
          
        // set required values to AccountCreationStatus
        if (budgetAdjustmentCreationStatus.getStatus().equals(KcConstants.AccountCreationService.STATUS_KC_ACCOUNT_SUCCESS)) {
//            budgetAdjustmentCreationStatus.setAccountNumber(budgetAdjustmentParameters.getAccountNumber());
//            budgetAdjustmentCreationStatus.setChartOfAccountsCode(defaults.getChartOfAccountsCode());          
        }
        
        return budgetAdjustmentCreationStatus;
    }
    
    /**
     * 
     * This method creates an account to be used for automatic maintenance document
     * @param AccountParametersDTO
     * @return Account
     */
    protected BudgetAdjustmentDocument createBudgetAdjustmentObject(BudgetAdjustmentParametersDTO parameters, BudgetAdjustmentCreationStatusDTO budgetAdjustmentCreationStatus) {
        BudgetAdjustmentDocument budgetAdjustmentDocument= new BudgetAdjustmentDocument();
        
        //also populates posting year
        budgetAdjustmentDocument.initiateDocument();
        
        //budgetAdjustmentDocument.getDocumentHeader().setDocumentNumber(documentNumber)
        budgetAdjustmentDocument.getDocumentHeader().setDocumentDescription(parameters.getDescription());
        budgetAdjustmentDocument.getDocumentHeader().setExplanation(parameters.getExplanation());
        budgetAdjustmentDocument.setPostingPeriodCode("");
        budgetAdjustmentDocument.getDocumentHeader().setOrganizationDocumentNumber("");
        
        //setup accounting lines
//            for (Iterator<BulkReceivingView> iterator = bulkViews.iterator(); iterator.hasNext();) {

        //for(Iterator<AccountingLine> iterator = parameters.getAccountingLines().iterator() ; iterator.hasNext();){
        //    budgetAdjustmentDocument.addSourceAccountingLine(iterator.next());
        //}
        
        return budgetAdjustmentDocument;
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
    protected boolean routeBudgetAdjustmentDocument(MaintenanceDocument maintenanceAccountDocument, BudgetAdjustmentCreationStatusDTO budgetAdjustmentCreationStatus) {
            /*
        try {
            String accountAutoCreateRouteValue = getParameterService().getParameterValue(Account.class, KcConstants.AccountCreationService.PARAMETER_KC_ACCOUNT_ADMIN_AUTO_CREATE_ACCOUNT_WORKFLOW_ACTION);

            // if the accountAutoCreateRouteValue is not save or submit or blanketApprove then put an error message and quit.
            if (!accountAutoCreateRouteValue.equalsIgnoreCase(KFSConstants.WORKFLOW_DOCUMENT_SAVE) && 
                !accountAutoCreateRouteValue.equalsIgnoreCase(KFSConstants.WORKFLOW_DOCUMENT_ROUTE) &&
                !accountAutoCreateRouteValue.equalsIgnoreCase(KFSConstants.WORKFLOW_DOCUMENT_BLANKET_APPROVE)) 
            {                
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
            return true;
            
        }  catch (WorkflowException wfe) { 
            LOG.error(KcConstants.AccountCreationService.ERROR_KC_DOCUMENT_WORKFLOW_EXCEPTION_DOCUMENT_ACTIONS +  wfe.getMessage()); 
            accountCreationStatus.getErrorMessages().add(KcConstants.AccountCreationService.WARNING_KC_DOCUMENT_WORKFLOW_EXCEPTION_DOCUMENT_ACTIONS +  wfe.getMessage());
            try {
                // save it even though it fails to route or blanket approve the document
                getDocumentService().saveDocument(maintenanceAccountDocument);
                accountCreationStatus.setStatus(KcConstants.AccountCreationService.STATUS_KC_ACCOUNT_WARNING);
            } catch (WorkflowException e) {
                LOG.error(KcConstants.AccountCreationService.WARNING_KC_DOCUMENT_WORKFLOW_EXCEPTION_DOCUMENT_ACTIONS +  e.getMessage()); 
                accountCreationStatus.getErrorMessages().add(KcConstants.AccountCreationService.ERROR_KC_DOCUMENT_WORKFLOW_EXCEPTION_DOCUMENT_ACTIONS +  e.getMessage());
                accountCreationStatus.setStatus(KcConstants.AccountCreationService.STATUS_KC_ACCOUNT_FAILURE);
            } 
            return false;
        } */
        return true;
    }
    
    
    /**
     * This method check to see if the user can create the account maintenance document and set the user session
     * @param String principalId
     * @return boolean
     */
    protected boolean isValidUser(String principalId) {
        
        PersonService<Person> personService = KIMServiceLocator.getPersonService();
        Person user = personService.getPerson(principalId);
        DocumentAuthorizer documentAuthorizer = new MaintenanceDocumentAuthorizerBase();
        if (documentAuthorizer.canInitiate(DocumentTypeAttributes.ACCOUNTING_DOCUMENT_TYPE_NAME, user)) {
            // set the user session so that the user name can be displayed in the saved document        
            GlobalVariables.setUserSession(new UserSession(user.getPrincipalName()));
            return true;
        } else {
            return false;
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
