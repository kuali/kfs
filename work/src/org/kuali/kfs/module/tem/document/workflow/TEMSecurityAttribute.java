/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
