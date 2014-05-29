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
package org.kuali.kfs.sys.batch.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.batch.dataaccess.FinancialSystemDocumentHeaderPopulationDao;
import org.kuali.kfs.sys.batch.service.FinancialSystemDocumentHeaderPopulationService;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeader;
import org.kuali.kfs.sys.businessobject.FinancialSystemDocumentHeaderMissingFromWorkflow;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.kew.api.document.Document;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.api.document.WorkflowDocumentService;
import org.kuali.rice.kew.api.document.search.DocumentSearchCriteria;
import org.kuali.rice.kim.api.identity.IdentityService;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.springframework.transaction.annotation.Transactional;

/**
 * The base implementation of the FinancialSystemDocumentHeaderPopulationService
 */
public class FinancialSystemDocumentHeaderPopulationServiceImpl implements FinancialSystemDocumentHeaderPopulationService {
    org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FinancialSystemDocumentHeaderPopulationServiceImpl.class);

    protected WorkflowDocumentService workflowDocumentService;
    protected BusinessObjectService businessObjectService;
    protected IdentityService identityService;
    protected FinancialSystemDocumentHeaderPopulationDao financialSystemDocumentHeaderPopulationDao;

    protected volatile String systemUserPrincipalId;

    /**
     * Populates financial system document header records, at a count of batchSize at a time, until the jobRunSize number of records have been processed, skipping document headers that
     * are included in documentStatusesToPopulate if the given Set has any members at all
     * @see org.kuali.kfs.sys.batch.service.FinancialSystemDocumentHeaderPopulationService#populateFinancialSystemDocumentHeadersFromKew(int, int, Set<DocumentStatus>)
     */
    @Override
    @NonTransactional
    public void populateFinancialSystemDocumentHeadersFromKew(int batchSize, Integer jobRunSize, Set<DocumentStatus> documentStatusesToPopulate) {
        final long startTime = System.currentTimeMillis();
        for (Collection<FinancialSystemDocumentHeader> documentHeaderBatch : getFinancialSystemDocumentHeaderBatchIterable(batchSize, jobRunSize)) {
            Map<String, FinancialSystemDocumentHeader> documentHeaderMap = convertDocumentHeaderBatchToMap(documentHeaderBatch);
            handleBatch(documentHeaderMap, documentStatusesToPopulate);
        }
        final long endTime = System.currentTimeMillis();
        final double runTimeSeconds = (endTime - startTime) / 1000.0;
        LOG.info("Run time: "+runTimeSeconds);
    }

    /**
     * Reads in the matching KEW document headers for the given batch of FinancialSystemDocumentHeader records and updates
     * @see org.kuali.kfs.sys.batch.service.FinancialSystemDocumentHeaderPopulationService#handleBatch(java.util.Map, Set<DocumentStatus>)
     */
    @Override
    @Transactional
    public void handleBatch(Map<String, FinancialSystemDocumentHeader> documentHeaders, Set<DocumentStatus> documentStatusesToPopulate) {
        List<Document> workflowDocuments = getWorkflowDocuments(documentHeaders, documentStatusesToPopulate);
        List<FinancialSystemDocumentHeader> documentHeadersToSave = new ArrayList<FinancialSystemDocumentHeader>();

        for (Document kewDocHeader : workflowDocuments) {
            final FinancialSystemDocumentHeader fsDocHeader = documentHeaders.get(kewDocHeader.getDocumentId());
            if (fsDocHeader != null) {
                updateDocumentHeader(fsDocHeader, kewDocHeader);
                documentHeadersToSave.add(fsDocHeader);
            } else {
                // how would this even happen????
                LOG.error("Document ID: "+kewDocHeader.getDocumentId()+" was returned from search but no financial system document header could be found in the map.  And it's freaking me out, man!");

            }
        }
        // save the changes
        getBusinessObjectService().save(documentHeadersToSave);
    }

    /**
     * Returns a List of KEW document headers to match the given FinancialSystemDocumentHeader records.  If a workflow document header cannot be found
     * for a financial system document header, the document number will be saved as a FinancialSystemDocumentHeaderMissingFromWorkflow record and skipped
     * from subsequent runs of the job
     * @param documentHeaders a Map of FS document headers
     * @param documentStatusesToPopulate if the given Set has any members, only documents in the given statuses will have their FinancialSystemDocumentHeader records populated
     * @return a List of matching workflow document header records, skipping any records included in the documentStatusesToPopulate Set if there are any members in it at all
     */
    protected List<Document> getWorkflowDocuments(Map<String, FinancialSystemDocumentHeader> documentHeaders, Set<DocumentStatus> documentStatusesToPopulate) {
        List<Document> workflowDocuments = new ArrayList<Document>();
        List<FinancialSystemDocumentHeaderMissingFromWorkflow> missingWorkflowHeaders = new ArrayList<FinancialSystemDocumentHeaderMissingFromWorkflow>();

        for (String documentNumber : documentHeaders.keySet()) {
            final Document workflowDoc = getWorkflowDocumentService().getDocument(documentNumber);
            if (workflowDoc != null && (documentStatusesToPopulate.isEmpty() || documentStatusesToPopulate.contains(workflowDoc.getStatus()))) {
                workflowDocuments.add(workflowDoc);
            } else if (workflowDoc == null) { // only record the error if we weren't supposed to skip the record...ie, if the workflow document is null
                LOG.error("Could not find a workflow document record for financial system document header #"+documentNumber);
                FinancialSystemDocumentHeaderMissingFromWorkflow missingWorkflowHeader = new FinancialSystemDocumentHeaderMissingFromWorkflow();
                missingWorkflowHeader.setDocumentNumber(documentNumber);
                missingWorkflowHeaders.add(missingWorkflowHeader);
            }
        }

        if (!missingWorkflowHeaders.isEmpty()) {
            getBusinessObjectService().save(missingWorkflowHeaders);
        }
        return workflowDocuments;
    }

    /**
     * Joins the given Set of document numbers with a pipe "|" character
     * @param documentIds the document numbers to join
     * @return the joined document numbers, ready to be handed to the document search
     */
    protected String pipeDocumentIds(Set<String> documentIds) {
        return StringUtils.join(documentIds,'|');
    }

    /**
     * Creates a DocumentSearchCriteria which will look up the documents identified by the ids piped into the documentIdForSearch
     * @return a DocumentSearchCriteria to look up the document search results
     */
    protected DocumentSearchCriteria buildDocumentSearchCriteria(String documentIdForSearch) {
        DocumentSearchCriteria.Builder criteria = DocumentSearchCriteria.Builder.create();
        criteria.setDocumentId(documentIdForSearch);
        return criteria.build();
    }

    /**
     * @return the principal id of the system user
     */
    protected String getSystemUserPrincipalId() {
        if (StringUtils.isBlank(systemUserPrincipalId)) {
            final Principal principal = getIdentityService().getPrincipalByPrincipalName(KFSConstants.SYSTEM_USER);
            systemUserPrincipalId = principal.getPrincipalId();
        }
        return systemUserPrincipalId;
    }

    /**
     * Writes to the passed in log a message detailing the changes which will occur when the job is run outside of log mode,
     * ie what the updated document status, document type, initiator principal id, and application document status will be updated to
     * @param financialSystemDocumentHeader the financial system document header which would have been updated
     * @param kewDocumentHeader the workflow document header with the information to update with
     * @param log the log to write to
     */
    protected void logChanges(FinancialSystemDocumentHeader financialSystemDocumentHeader, Document kewDocumentHeader, Logger log) {
        log.info("Financial System Document Header "+financialSystemDocumentHeader.getDocumentNumber()+" KEW document header "+kewDocumentHeader.getDocumentId()+
                " Initiator Principal Id: "+kewDocumentHeader.getInitiatorPrincipalId()+" Document Type Name: "+kewDocumentHeader.getDocumentTypeName()+
                " Document Status: "+kewDocumentHeader.getStatus().getLabel()+" Application Document Status: "+kewDocumentHeader.getApplicationDocumentStatus());
    }

    /**
     * Updates the financial system document header with values from the workflow document header
     * @param financialSystemDocumentHeader the financial system document header to update
     * @param kewDocumentHeader the workflow document header with values to update the financial system document header with
     */
    protected void updateDocumentHeader(FinancialSystemDocumentHeader financialSystemDocumentHeader, Document kewDocumentHeader) {
        financialSystemDocumentHeader.setInitiatorPrincipalId(kewDocumentHeader.getInitiatorPrincipalId());
        financialSystemDocumentHeader.setWorkflowDocumentTypeName(kewDocumentHeader.getDocumentTypeName());
        financialSystemDocumentHeader.setWorkflowDocumentStatusCode(kewDocumentHeader.getStatus().getCode());
        financialSystemDocumentHeader.setApplicationDocumentStatus(kewDocumentHeader.getApplicationDocumentStatus());
    }

    /**
     * Convenience iterator to get batches of FinancialSystemDocumentHeader by batch size
     */
    protected class FinancialSystemDocumentHeaderBatchIterator implements Iterator<Collection<FinancialSystemDocumentHeader>> {
        protected int batchSize;
        protected int currentStartIndex = 1;
        protected int documentHeaderCount;

        protected FinancialSystemDocumentHeaderBatchIterator(int batchSize, Integer jobRunSize) {
            this.batchSize = batchSize;
            Map<String, Object> fieldValues = new HashMap<String, Object>(); // there's no "countAll" so we'll just pass in an empty Map for the count
            this.documentHeaderCount = getFinancialSystemDocumentHeaderCount();
            if (jobRunSize != null && jobRunSize.intValue() > 0 && jobRunSize.intValue() < this.documentHeaderCount) {
                this.documentHeaderCount = jobRunSize; // use jobRunSize to limit
            }
        }

        @Override
        public boolean hasNext() {
            return currentStartIndex <= documentHeaderCount;
        }

        @Override
        public Collection<FinancialSystemDocumentHeader> next() {
            int endIndex = currentStartIndex + batchSize - 1;
            if (endIndex > documentHeaderCount) {
                endIndex = documentHeaderCount;
            }

            // nota bene: it was discussed that it might be helpful to have a parameter with a specific list of document numbers to read in and convert.
            // if such a parameter were implemented, this would likely be a good place to use that logic....  The DAO shouldn't read the parameter directly, I think....
            Collection<FinancialSystemDocumentHeader> docHeaderBatch = readBatchOfFinancialSystemDocumentHeaders(currentStartIndex, endIndex);

            currentStartIndex = endIndex + 1;
            return docHeaderBatch;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("This iterator is read only; remove should not be called against it");
        }
    }

    /**
     * Counts the number of Financial System Document Header records without initiator principal id's set
     * @see org.kuali.kfs.sys.batch.service.FinancialSystemDocumentHeaderPopulationService#getFinancialSystemDocumentHeaderCount()
     */
    @Transactional
    @Override
    public int getFinancialSystemDocumentHeaderCount() {
        return getFinancialSystemDocumentHeaderPopulationDao().countTotalFinancialSystemDocumentHeadersToProcess();
    }

    /**
     * Uses the DAO to retrieve the specified batch
     * @see org.kuali.kfs.sys.batch.service.FinancialSystemDocumentHeaderPopulationService#readBatchOfFinancialSystemDocumentHeaders(int, int)
     */
    @Transactional
    @Override
    public Collection<FinancialSystemDocumentHeader> readBatchOfFinancialSystemDocumentHeaders(int startIndex, int endIndex) {
        return getFinancialSystemDocumentHeaderPopulationDao().getFinancialSystemDocumentHeadersForBatch(startIndex, endIndex);
    }

    /**
     * Returns a new Iterable to iterate over batches of FinancialSystemDocumentHeaders
     * @param batchSize the size of the batches to build
     * @param jobRunSize the number of records
     * @return the newly created Iterable
     */
    protected Iterable<Collection<FinancialSystemDocumentHeader>> getFinancialSystemDocumentHeaderBatchIterable(final int batchSize, final Integer jobRunSize) {
        return new Iterable<Collection<FinancialSystemDocumentHeader>>() {
            @Override
            public Iterator<Collection<FinancialSystemDocumentHeader>> iterator() {
                return new FinancialSystemDocumentHeaderBatchIterator(batchSize, jobRunSize);
            }
        };
    }

    /**
     * Converts a Collection of FinancialSystemDocumentHeader records into a Map keyed by the document number
     * @param documentHeaderBatch a Collection of FinancialSystemDocumentHeader records
     * @return the Map of FinancialSystemDocumentHeader records keyed by document number
     */
    protected Map<String, FinancialSystemDocumentHeader> convertDocumentHeaderBatchToMap(Collection<FinancialSystemDocumentHeader> documentHeaderBatch) {
        Map<String, FinancialSystemDocumentHeader> documentHeaderMap = new HashMap<String, FinancialSystemDocumentHeader>();
        for (FinancialSystemDocumentHeader docHeader : documentHeaderBatch) {
            documentHeaderMap.put(docHeader.getDocumentNumber(), docHeader);
        }
        return documentHeaderMap;
    }

    @NonTransactional
    public WorkflowDocumentService getWorkflowDocumentService() {
        return workflowDocumentService;
    }

    @NonTransactional
    public void setWorkflowDocumentService(WorkflowDocumentService workflowDocumentService) {
        this.workflowDocumentService = workflowDocumentService;
    }

    @NonTransactional
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    @NonTransactional
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    @NonTransactional
    public IdentityService getIdentityService() {
        return identityService;
    }

    @NonTransactional
    public void setIdentityService(IdentityService identityService) {
        this.identityService = identityService;
    }

    @NonTransactional
    public FinancialSystemDocumentHeaderPopulationDao getFinancialSystemDocumentHeaderPopulationDao() {
        return financialSystemDocumentHeaderPopulationDao;
    }

    @NonTransactional
    public void setFinancialSystemDocumentHeaderPopulationDao(FinancialSystemDocumentHeaderPopulationDao financialSystemDocumentHeaderPopulationDao) {
        this.financialSystemDocumentHeaderPopulationDao = financialSystemDocumentHeaderPopulationDao;
    }
}
