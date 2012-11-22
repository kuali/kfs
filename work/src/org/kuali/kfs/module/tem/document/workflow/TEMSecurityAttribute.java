/*
 * Copyright 2012 The Kuali Foundation
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
package org.kuali.kfs.module.tem.document.workflow;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.workflow.SensitiveDataSecurityAttribute;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.kns.document.authorization.DocumentAuthorizer;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.krad.service.DocumentService;

/**
 * TEM Security Attribute restrict doc search results and view route log
 */
public class TEMSecurityAttribute extends SensitiveDataSecurityAttribute {

    private DocumentHelperService documentHelperService; 
    private DocumentService documentService; 

    /**
     * @see org.kuali.kfs.sys.document.workflow.SensitiveDataSecurityAttribute#docSearchAuthorized(org.kuali.rice.kim.bo.Person, java.lang.String, java.lang.Long, java.lang.String)
     */
    public Boolean docSearchAuthorized(Person currentUser, String docTypeName, Long documentId, String initiatorPrincipalId) {
        boolean authorized = super.docSearchAuthorized(currentUser, docTypeName, documentId, initiatorPrincipalId) && canOpen(currentUser, docTypeName, documentId);
        return authorized; 
    }

    /**
     * @see org.kuali.kfs.sys.document.workflow.SensitiveDataSecurityAttribute#routeLogAuthorized(org.kuali.rice.kim.bo.Person, java.lang.String, java.lang.Long, java.lang.String)
     */
    public Boolean routeLogAuthorized(Person currentUser, String docTypeName, Long documentId, String initiatorPrincipalId) {
        boolean authorized = super.routeLogAuthorized(currentUser, docTypeName, documentId, initiatorPrincipalId) && canOpen(currentUser, docTypeName, documentId);
        return authorized;
    }

    /**
     * Check the authorizer on the document type if user can open document
     * 
     * @param currentUser
     * @param docTypeName
     * @param documentId
     * @return
     */
    public final Boolean canOpen(Person currentUser, String docTypeName, Long documentId) {
        DocumentAuthorizer docAuthorizer = getDocumentHelperService().getDocumentAuthorizer(docTypeName);
        Document doc = null;
        try {
            doc = getDocumentService().getByDocumentHeaderIdSessionless(documentId.toString());
        }
        catch (WorkflowException we) {
            throw new RuntimeException(we);
        }
        return docAuthorizer.canOpen(doc, currentUser);
    }

    public DocumentHelperService getDocumentHelperService() {
        return SpringContext.getBean(DocumentHelperService.class);
    }

    public DocumentService getDocumentService() {
        return SpringContext.getBean(DocumentService.class);
    }

}
