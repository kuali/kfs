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
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.kfs.sys.KFSConstants;

/**
 * This class is the default implementation of the AccountCreateDocumentService
 */

public class AccountCreateDocumentServiceImpl implements AccountCreateDocumentService {

    private DocumentService documentService;
    
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
        
        return maintenanceAccountDocument.getDocumentNumber();
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
            throw new RuntimeException("WorkFlowException: createAccountDocument has failed.  Unable to get a new document");   
        }
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
    
    
}
