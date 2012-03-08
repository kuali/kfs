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

import java.util.List;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.datadictionary.FinancialSystemTransactionalDocumentEntry;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.document.WorkflowDocumentService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kew.framework.document.security.DocumentSecurityAttribute;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.krad.datadictionary.DocumentEntry;
import org.kuali.rice.krad.document.DocumentAuthorizer;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;

/**
 * This class...
 */
public class SensitiveDataSecurityAttribute implements DocumentSecurityAttribute {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SensitiveDataSecurityAttribute.class);
    

    @Override
    public boolean isAuthorizedForDocument(String principalId, org.kuali.rice.kew.api.document.Document document) {
        String docTypeName = document.getDocumentTypeName();
        DocumentEntry docEntry = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getDocumentEntry(docTypeName);
        if (docEntry instanceof FinancialSystemTransactionalDocumentEntry) {
            if (((FinancialSystemTransactionalDocumentEntry)docEntry).isPotentiallySensitive()) {

                WorkflowDocumentService workflowDocService = KewApiServiceLocator.getWorkflowDocumentService();
                List<String> sensitiveDataCodeArray = workflowDocService.getSearchableAttributeStringValuesByKey(document.getDocumentId(),"sensitive");
                if (sensitiveDataCodeArray != null && sensitiveDataCodeArray.size() > 0) {
                    List<String> sensitiveDataCode = sensitiveDataCodeArray;
                    if ( sensitiveDataCode != null && sensitiveDataCode.contains("Y")) {

                        DocumentAuthorizer docAuthorizer = SpringContext.getBean(DocumentHelperService.class).getDocumentAuthorizer(docTypeName);
                        try {
                            return docAuthorizer.canOpen(KRADServiceLocatorWeb.getDocumentService().getByDocumentHeaderIdSessionless(document.getDocumentId()), KimApiServiceLocator.getPersonService().getPerson(principalId));
                        }
                        catch (WorkflowException ex) {
                            LOG.error( "Exception while testing if user can open document: " + document, ex);
                            return false;
                        }
                    }
                }
            }
        }
        return true;

    }

}
