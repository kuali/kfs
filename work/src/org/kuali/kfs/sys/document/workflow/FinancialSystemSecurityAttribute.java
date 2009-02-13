/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.sys.document.workflow;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.purap.businessobject.SensitiveData;
import org.kuali.kfs.module.purap.service.SensitiveDataService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.doctype.DocumentTypeSecurity;
import org.kuali.rice.kew.doctype.SecurityAttribute;
import org.kuali.rice.kew.doctype.SecuritySession;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.service.WorkflowUtility;
import org.kuali.rice.kew.web.session.Authentication;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.authorization.DocumentAuthorizer;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.service.DocumentService;

/**
 * This class...
 */
public class FinancialSystemSecurityAttribute implements SecurityAttribute {

    private WorkflowUtility workflowUtils = SpringContext.getBean(WorkflowUtility.class);
    private DocumentHelperService docHelperService = SpringContext.getBean(DocumentHelperService.class);
    private DocumentService docService = SpringContext.getBean(DocumentService.class);

    /**
     * @see org.kuali.rice.kew.doctype.SecurityAttribute#docSearchAuthorized(org.kuali.rice.kew.doctype.DocumentTypeSecurity, org.kuali.rice.kim.bo.Person, java.util.List, java.lang.String, java.lang.Long, java.lang.String, org.kuali.rice.kew.doctype.SecuritySession)
     */
    public final Boolean docSearchAuthorized(DocumentTypeSecurity security, Person currentUser, List<Authentication> authentications, String docTypeName, Long documentId, String initiatorWorkflowId, SecuritySession session) {

        List<String> sensitiveDataCode = workflowUtils.getSearchableAttributeStringValuesByKey(documentId, "purchaseOrderSensitiveData");
        if (sensitiveDataCode != null) {

            DocumentAuthorizer docAuthorizer = docHelperService.getDocumentAuthorizer(docTypeName);
            Document doc = null;
            try {
                doc = docService.getByDocumentHeaderIdSessionless(documentId.toString());
            } catch(WorkflowException we) {
                throw new RuntimeException(we);
            }
            return docAuthorizer.canOpen(doc, currentUser);
        } else {
            return true;
        }

    }

    /**
     * @see org.kuali.rice.kew.doctype.SecurityAttribute#routeLogAuthorized(org.kuali.rice.kew.doctype.DocumentTypeSecurity, org.kuali.rice.kim.bo.Person, java.util.List, java.lang.String, java.lang.Long, java.lang.String, org.kuali.rice.kew.doctype.SecuritySession)
     */
    public final Boolean routeLogAuthorized(DocumentTypeSecurity security, Person currentUser, List<Authentication> authentications, String docTypeName, Long documentId, String initiatorWorkflowId, SecuritySession session) {

        List<String> sensitiveDataCode = workflowUtils.getSearchableAttributeStringValuesByKey(documentId, "purchaseOrderSensitiveData");
        if (sensitiveDataCode != null) {

            DocumentAuthorizer docAuthorizer = docHelperService.getDocumentAuthorizer(docTypeName);
            Document doc = null;
            try {
                doc = docService.getByDocumentHeaderIdSessionless(documentId.toString());
            } catch(WorkflowException we) {
                throw new RuntimeException(we);
            }
            return docAuthorizer.canOpen(doc, currentUser);
        } else {
            return true;
        }
    }

}
