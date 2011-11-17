/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.sys.document.workflow;

import java.util.Arrays;
import java.util.List;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.datadictionary.FinancialSystemTransactionalDocumentEntry;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.datadictionary.DocumentEntry;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.document.authorization.DocumentAuthorizer;
import org.kuali.rice.krad.service.DocumentHelperService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.workflow.WorkflowUtils;
import org.omg.Security.SecurityAttribute;

/**
 * This class...
 */
public class SensitiveDataSecurityAttribute implements SecurityAttribute {

    private WorkflowUtils workflowUtils = SpringContext.getBean(WorkflowUtils.class);
    private DocumentHelperService docHelperService = SpringContext.getBean(DocumentHelperService.class);
    private DocumentService docService = SpringContext.getBean(DocumentService.class);

    /**
     * @see org.kuali.rice.kew.doctype.SecurityAttribute#docSearchAuthorized(org.kuali.rice.kew.doctype.DocumentTypeSecurity, org.kuali.rice.kim.api.identity.Person, java.util.List, java.lang.String, java.lang.Long, java.lang.String, org.kuali.rice.kew.doctype.SecuritySession)
     */
    public Boolean docSearchAuthorized(Person currentUser, String docTypeName, Long documentId, String initiatorPrincipalId) {

        return isVisable(currentUser, docTypeName, documentId);
        
    }

    /**
     * @see org.kuali.rice.kew.doctype.SecurityAttribute#routeLogAuthorized(org.kuali.rice.kew.doctype.DocumentTypeSecurity, org.kuali.rice.kim.api.identity.Person, java.util.List, java.lang.String, java.lang.Long, java.lang.String, org.kuali.rice.kew.doctype.SecuritySession)
     */
    public Boolean routeLogAuthorized(Person currentUser, String docTypeName, Long documentId, String initiatorPrincipalId) {

        return isVisable(currentUser, docTypeName, documentId);
    }
    
    private final Boolean isVisable(Person currentUser, String docTypeName, Long documentId) {
        
        final DocumentEntry docEntry = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getDocumentEntry(docTypeName);
        if (docEntry instanceof FinancialSystemTransactionalDocumentEntry) {
            if (((FinancialSystemTransactionalDocumentEntry)docEntry).isPotentiallySensitive()) {
                String[] sensitiveDataCodeArray = workflowUtils.getSearchableAttributeStringValuesByKey(documentId, "sensitive");
                if (sensitiveDataCodeArray != null && sensitiveDataCodeArray.length > 0) {
                    List<String> sensitiveDataCode = Arrays.asList(sensitiveDataCodeArray);
                    if ( sensitiveDataCode != null && sensitiveDataCode.contains("Y")) {
    
                        DocumentAuthorizer docAuthorizer = docHelperService.getDocumentAuthorizer(docTypeName);
                        Document doc = null;
                        try {
                            doc = docService.getByDocumentHeaderIdSessionless(documentId.toString());
                        } catch(WorkflowException we) {
                            throw new RuntimeException(we);
                        }
                        return docAuthorizer.canOpen(doc, currentUser);
                    }
                }
            }
        }
        return true;
        
    }

}
