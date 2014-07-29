/*
 * Copyright 2014 The Kuali Foundation.
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


import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.service.FinancialSystemDocumentHeaderService;
import org.kuali.rice.core.api.criteria.PredicateFactory;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.criteria.QueryResults;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.data.DataObjectService;
import org.kuali.rice.krad.data.PersistenceOption;

public class FinancialSystemDocumentHeaderServiceImpl implements FinancialSystemDocumentHeaderService {
    protected DataObjectService dataObjectService;
    protected Class<? extends DocumentHeader> documentHeaderClass;

    @Override
    public DocumentHeader getDocumentHeaderById(String documentHeaderId) {
        return dataObjectService.find(getDocumentHeaderClass(), documentHeaderId);
    }

    @Override
    public DocumentHeader saveDocumentHeader(DocumentHeader documentHeader) {
        WorkflowDocument workflowDocument = documentHeader.getWorkflowDocument();
        DocumentHeader savedDocumentHeader = dataObjectService.save(documentHeader, PersistenceOption.FLUSH);
        savedDocumentHeader.setWorkflowDocument( workflowDocument );
        return savedDocumentHeader;
    }

    @Override
    public void deleteDocumentHeader(DocumentHeader documentHeader) {
        dataObjectService.delete(documentHeader);
    }

    @Override
    public DocumentHeader getCorrectingDocumentHeader(String documentId) {
        final QueryByCriteria correctedByCriteria = QueryByCriteria.Builder.fromPredicates(PredicateFactory.equal(KFSPropertyConstants.FINANCIAL_DOCUMENT_IN_ERROR_NUMBER, documentId));
        final QueryResults<? extends DocumentHeader> docHeaders = getDataObjectService().findMatching(getDocumentHeaderClass(), correctedByCriteria);
        if (docHeaders.getTotalRowCount() > 0) {
            throw new RuntimeException("Found more than one document header correcting document #"+documentId);
        }
        return docHeaders.getResults().get(0);
    }

    public DataObjectService getDataObjectService() {
        return this.dataObjectService;
    }

    public void setDataObjectService(DataObjectService dataObjectService) {
        this.dataObjectService = dataObjectService;
    }

    public Class<? extends DocumentHeader> getDocumentHeaderClass() {
        return documentHeaderClass;
    }

    public void setDocumentHeaderClass(Class<? extends DocumentHeader> documentHeaderClass) {
        this.documentHeaderClass = documentHeaderClass;
    }
}