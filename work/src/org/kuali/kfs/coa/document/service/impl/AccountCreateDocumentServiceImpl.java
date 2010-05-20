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
import org.kuali.kfs.coa.businessobject.KCAward;
import org.kuali.kfs.coa.document.service.AccountCreateDocumentService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.ParameterService;

/**
 * This class is the default implementation of the AccountCreateDocumentService
 */

public class AccountCreateDocumentServiceImpl implements AccountCreateDocumentService {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountCreateDocumentServiceImpl.class);
    
    private DocumentService documentService;
    private ParameterService parameterService;
    
    /**
     * This method will use the data from kc award and create a document with the account
     */
    public Account createAccountForCGMaintenanceDocument(KCAward kCAward) {
       Account account = new Account();
       
        return account;
    }
    
    /**
     * This method will create a maintenance document and put the account object
     * @return documentNumber returns the documentNumber
     * 
     * @see org.kuali.kfs.coa.document.service.CreateAccountService#createAutomaticCGAccountMaintenanceDocument()
     */
    public String createAutomaticCGAccountMaintenanceDocument(Account account) {

        //create a new maintenance document
        MaintenanceDocument maintenanceAccountDocument = (MaintenanceDocument)createCGMaintenanceDocument();
        
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
        }
        String accountAutoCreateRoute = getParameterService().getParameterValue(Account.class, KFSParameterKeyConstants.ACCOUNT_AUTO_CREATE_ROUTE);
        
        createRouteAutomaticCGAccountDocument(maintenanceAccountDocument, accountAutoCreateRoute);
    }

    /**
     * This method create and route automatic CG account maint. document based on system parameter
     * @param maintenanceAccountDocument
     * @param accountAutoCreateRoute
     */
    private void createRouteAutomaticCGAccountDocument(MaintenanceDocument maintenanceAccountDocument, String accountAutoCreateRoute) {
        
        try {
            if (accountAutoCreateRoute.equals(KFSConstants.WORKFLOW_DOCUMENT_NO_SUBMIT)) {
                maintenanceAccountDocument.getDocumentHeader().getWorkflowDocument().saveDocument("");
            }
            else if (accountAutoCreateRoute.equals(KFSConstants.WORKFLOW_DOCUMENT_BLANKET_APPROVE)) {
                maintenanceAccountDocument.getDocumentHeader().getWorkflowDocument().blanketApprove("");
            }
            else if (accountAutoCreateRoute.equals(KFSConstants.WORKFLOW_DOCUMENT_SUBMIT)) {
                maintenanceAccountDocument.getDocumentHeader().getWorkflowDocument().routeDocument("");
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
    protected Document createCGMaintenanceDocument() {
        try {
            Document document = documentService.getNewDocument(KFSConstants.DocumentTypeAttributes.ACCOUNTING_DOCUMENT_TYPE_NAME);
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
    
    
}
