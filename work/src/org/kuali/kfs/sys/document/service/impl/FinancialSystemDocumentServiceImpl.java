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
