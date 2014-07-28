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

import java.util.concurrent.Callable;

import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.workflow.SensitiveDataSecurityAttribute;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.IdentityService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.document.authorization.DocumentAuthorizer;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.krad.UserSession;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * TEM Security Attribute restrict doc search results and view route log
 */
public class TEMSecurityAttribute extends SensitiveDataSecurityAttribute {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TEMSecurityAttribute.class);

    private DocumentHelperService documentHelperService;
    private DocumentService documentService;
    protected IdentityService identityService;

    /**
     * @see org.kuali.kfs.sys.document.workflow.SensitiveDataSecurityAttribute#isAuthorizedForDocument(java.lang.String, org.kuali.rice.kew.api.document.Document)
     */
    @Override
    public boolean isAuthorizedForDocument(final String principalId, final org.kuali.rice.kew.api.document.Document document) {
        boolean authorized = false;

        authorized = super.isAuthorizedForDocument(principalId, document);
        if (authorized) {
            try {
                final String principalName = getIdentityService().getPrincipal(principalId).getPrincipalName();
                Boolean canOpen = GlobalVariables.doInNewGlobalVariables(new UserSession(principalName), new Callable<Boolean>(){
                    @Override
                    public Boolean call() {
                        return canOpen(GlobalVariables.getUserSession().getPerson() , document.getDocumentTypeName(), document.getDocumentId());
                    }
                });
                return ObjectUtils.isNotNull(canOpen) && canOpen ;
            } catch (Exception ex) {
                LOG.error( "Exception while testing if user can open document: document.getDocumentId()=" + document.getDocumentId(), ex);
                return false;
            }
        }

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
    public Boolean canOpen(Person currentUser, String docTypeName, String documentId) {
        DocumentAuthorizer docAuthorizer = getDocumentHelperService().getDocumentAuthorizer(docTypeName);
        final TravelDocument doc = getDocument(documentId);
        if (ObjectUtils.isNull(doc)) {
            LOG.error("KFS document is null but exists in rice, returning false from isAuthorizedForDocument. documentId=" + documentId);
            return false;
        }
        return docAuthorizer.canOpen(doc, currentUser);
    }

    /**
     * @param documentNumber
     * @return
     * @throws WorkflowException
     */
    public TravelDocument getDocument(String documentNumber) {
        TravelDocument document = null;
        try {
            document = (TravelDocument) getDocumentService().getByDocumentHeaderIdSessionless(documentNumber);
        }
        catch (WorkflowException ex) {
            throw new RuntimeException(ex);
        }
        return document;
    }

    public DocumentHelperService getDocumentHelperService() {
        return SpringContext.getBean(DocumentHelperService.class);
    }

    public DocumentService getDocumentService() {
        return SpringContext.getBean(DocumentService.class);
    }

    /**
     * @return the default implementation of IdentityService
     */
    protected IdentityService getIdentityService() {
        if (identityService == null) {
            identityService = SpringContext.getBean(IdentityService.class);
        }
        return identityService;
    }

}
