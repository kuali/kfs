/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.sys.document.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.document.FinancialSystemTransactionalDocument;
import org.kuali.kfs.sys.document.dataaccess.FinancialSystemDocumentDao;
import org.kuali.kfs.sys.document.service.FinancialSystemDocumentService;
import org.kuali.kfs.sys.document.validation.event.DocumentSystemSaveEvent;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.document.attribute.DocumentAttributeIndexingQueue;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.DocumentAdHocService;
import org.kuali.rice.krad.service.DocumentService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class is a Financial System specific Document Service class to allow for the
 * {@link #findByDocumentHeaderStatusCode(Class, String)} method.
 */
@Transactional
public class FinancialSystemDocumentServiceImpl implements FinancialSystemDocumentService {
    private FinancialSystemDocumentDao financialSystemDocumentDao;
    private DocumentService documentService;
    protected DocumentAdHocService documentAdHocService;

    /**
     * @see org.kuali.kfs.sys.document.service.FinancialSystemDocumentService#saveDocumentNoValidation(org.kuali.rice.krad.document.Document)
     */
    public void saveDocumentNoValidation(Document document) {
        try {
            // FIXME The following code of refreshing document header is a temporary fix for the issue that
            // in some cases (seem random) the doc header fields are null; and if doc header is refreshed, the workflow doc becomes null.
            // The root cause of this is that when some docs are retrieved manually using OJB criteria, ref objs such as doc header or workflow doc
            // aren't retrieved; the solution would be to add these refreshing when documents are retrieved in those OJB methods.
            if (document.getDocumentHeader() != null && document.getDocumentHeader().getDocumentNumber() == null) {
                WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
                document.refreshReferenceObject("documentHeader");               
                document.getDocumentHeader().setWorkflowDocument(workflowDocument);
            }
            documentService.saveDocument(document, DocumentSystemSaveEvent.class);
            
            // At this point, the work-flow status will not change for the current document, but the document status will.
            // This causes the search indices for the document to become out of synch, and will show a different status type
            // in the RICE lookup results screen.
            final DocumentAttributeIndexingQueue documentAttributeIndexingQueue = KewApiServiceLocator.getDocumentAttributeIndexingQueue();                            
            
            documentAttributeIndexingQueue.indexDocument(document.getDocumentNumber());
        }
        catch (WorkflowException we) {
            String errorMsg = "Workflow error saving document # " + document.getDocumentHeader().getDocumentNumber() + " " + we.getMessage();
            throw new RuntimeException(errorMsg, we);
        }
        catch (NumberFormatException ne) {
            String errorMsg = "Invalid document number format for document # " + document.getDocumentHeader().getDocumentNumber() + " " + ne.getMessage();
            throw new RuntimeException(errorMsg, ne);
        }
    }
    
    /**
     * @see org.kuali.kfs.sys.document.service.FinancialSystemDocumentService#findByDocumentHeaderStatusCode(java.lang.Class,
     *      java.lang.String)
     */
    @Override
    public <T extends Document> Collection<T> findByDocumentHeaderStatusCode(Class<T> clazz, String statusCode) throws WorkflowException {
        Collection<T> foundDocuments = getFinancialSystemDocumentDao().findByDocumentHeaderStatusCode(clazz, statusCode);
        for (Document doc : foundDocuments)
            documentAdHocService.addAdHocs(doc);

        Collection<T> returnDocuments = new ArrayList<T>();
        for (Iterator<T> iter = foundDocuments.iterator(); iter.hasNext();) {
            Document doc = (Document) iter.next();
            returnDocuments.add((T) getDocumentService().getByDocumentHeaderId(doc.getDocumentNumber()));
        }
        return returnDocuments;
    }

    public void prepareToCopy(FinancialSystemDocumentHeader oldDocumentHeader, FinancialSystemTransactionalDocument document) {
        // This method serves as a plugin to add logic to the copy functionality, when needed.
    }

    public FinancialSystemDocumentDao getFinancialSystemDocumentDao() {
        return financialSystemDocumentDao;
    }

    public void setFinancialSystemDocumentDao(FinancialSystemDocumentDao financialSystemDocumentDao) {
        this.financialSystemDocumentDao = financialSystemDocumentDao;
    }

    public DocumentService getDocumentService() {
        return documentService;
    }

    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    public void setDocumentAdHocService(DocumentAdHocService documentAdHocService) {
        this.documentAdHocService = documentAdHocService;
    }


}
