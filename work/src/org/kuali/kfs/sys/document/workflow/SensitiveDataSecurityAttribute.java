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
import org.kuali.rice.krad.util.ObjectUtils;

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
                            org.kuali.rice.krad.document.Document kfsDocument = KRADServiceLocatorWeb.getDocumentService().getByDocumentHeaderIdSessionless(document.getDocumentId());

                            if (ObjectUtils.isNull(kfsDocument)) {
                                LOG.error("KFS document is null but exists in rice, returning false from isAuthorizedForDocument. document.getDocumentId()=" + document.getDocumentId());
                                return false;
                            } else {
                                return docAuthorizer.canOpen(kfsDocument, KimApiServiceLocator.getPersonService().getPerson(principalId));
                            }
                        }
                        catch (WorkflowException ex) {
                            LOG.error( "Exception while testing if user can open document: document.getDocumentId()=" + document.getDocumentId(), ex);
                            return false;
                        }
                    }
                }
            }
        }
        return true;

    }

}
