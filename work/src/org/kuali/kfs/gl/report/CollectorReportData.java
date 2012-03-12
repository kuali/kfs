/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.gl.report;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;

import org.kuali.kfs.gl.batch.CollectorBatch;
import org.kuali.kfs.gl.batch.service.impl.DocumentGroupData;
import org.kuali.kfs.gl.batch.service.impl.OriginEntryTotals;
import org.kuali.kfs.gl.businessobject.CollectorDetail;
import org.kuali.kfs.gl.businessobject.DemergerReportData;
import org.kuali.kfs.gl.businessobject.Transaction;
import org.kuali.kfs.gl.service.ScrubberReportData;
import org.kuali.kfs.sys.Message;
import org.kuali.rice.krad.util.MessageMap;

/**
 * This class aggregates all of the status information together from all of the collector-related processes. Note: this code assumes
 * that each batch is identified by a different collector batch name.
 */
public class CollectorReportData {
    private Map<String, String> emailSendingStatus;

    private Set<String> unparsableFileNames;
    private Map<String, List<Message>> validationErrorsForBatchName;
    private Map<String, CollectorBatch> addedBatches;
    private Map<String, Map<CollectorDetail, List<Message>>> detailScrubberErrors;
    private Map<String, Map<Transaction, List<Message>>> originEntryScrubberErrors;
    private Map<String, ScrubberReportData> scrubberReportDataForBatchName;
    private Map<String, DemergerReportData> demergerReportDataForBatchName;
    private Map<String, Integer> numDetailAccountValuesChangedForBatchName;

    private Map<String, Integer> numDetailDeletedForBatchName;
    private Map<String, Map<DocumentGroupData, OriginEntryTotals>> totalsOnInputOriginEntriesAssociatedWithErrorGroupForBatchName;
    private Map<String, Integer> numInputDetailsForBatchName;
    private Map<String, Integer> numSavedDetailsForBatchName;
    private Map<String, Boolean> validationStatuses;
    private SortedMap<String, MessageMap> messageMapForFileName;

    private LedgerSummaryReport ledgerSummaryReport;
    private PreScrubberReportData preScrubberReportData;
    
    private int numPersistedBatches;
    private int numNotPersistedBatches;
    private int numNotPersistedOriginEntryRecords;
    private int numNotPersistedCollectorDetailRecords;
    private Collection<String> loadedfileNames;

    public CollectorReportData() {
        emailSendingStatus = new HashMap<String, String>();
        unparsableFileNames = new TreeSet<String>();
        validationErrorsForBatchName = new HashMap<String, List<Message>>();
        addedBatches = new LinkedHashMap<String, CollectorBatch>();
        detailScrubberErrors = new HashMap<String, Map<CollectorDetail, List<Message>>>();
        originEntryScrubberErrors = new HashMap<String, Map<Transaction, List<Message>>>();
        scrubberReportDataForBatchName = new HashMap<String, ScrubberReportData>();
        demergerReportDataForBatchName = new HashMap<String, DemergerReportData>();
        numDetailAccountValuesChangedForBatchName = new HashMap<String, Integer>();
        numDetailDeletedForBatchName = new HashMap<String, Integer>();
        totalsOnInputOriginEntriesAssociatedWithErrorGroupForBatchName = new HashMap<String, Map<DocumentGroupData, OriginEntryTotals>>();
        numInputDetailsForBatchName = new HashMap<String, Integer>();
        numSavedDetailsForBatchName = new HashMap<String, Integer>();
        validationStatuses = new HashMap<String, Boolean>();
        ledgerSummaryReport = new LedgerSummaryReport();
        preScrubberReportData = new PreScrubberReportData(0, 0, new TreeSet<String>(), new TreeSet<String>());
        messageMapForFileName = new TreeMap<String, MessageMap>();
        numPersistedBatches = 0;
        numNotPersistedBatches = 0;
        numNotPersistedOriginEntryRecords = 0;
        loadedfileNames = new ArrayList<String>();
    }

    /**
     * Adds a batch to this report data object. If the batch (identified using batch.getBatchName()) has already been added, then an
     * exception is thrown.
     * 
     * @param batch collector batch from xml input
     */
    public void addBatch(CollectorBatch batch) {
        if (isBatchAdded(batch)) {
            throw new RuntimeException("Can't add a batch twice");
        }
        addedBatches.put(batch.getBatchName(), batch);
    }

    /**
     * Returns whether a batch has already been added
     * 
     * @param batch collector batch from xml input
     * @return true if batch has already been added
     */
    public boolean isBatchAdded(CollectorBatch batch) {
        return addedBatches.containsKey(batch.getBatchName());
    }

    /**
     * Returns the number of batches that have been added using the {@link #addBatch(CollectorBatch)} method
     * 
     * @return number of added batches
     */
    public int getNumberOfAddedBatches() {
        return addedBatches.size();
    }

    /**
     * Throws exception if batch has not been added
     * 
     * @param batch
     */
    protected void throwExceptionIfBatchNotAdded(CollectorBatch batch) {
        if (!isBatchAdded(batch)) {
            throw new RuntimeException("Batch must be added first");
        }
    }

    /**
     * Stores the errors encountered trying to scrub the InterDepartmentalBilling records in the given batch. This method must be
     * called after addBatch has been called with the same batch. Previously saved errors for this batch will be overwritten.
     * 
     * @param batch collector batch from input xml
     * @param errorsMap contains a map of all errors encountered while trying to scrub InterDepartmentalBilling records
     */
    public void setBatchDetailScrubberErrors(CollectorBatch batch, Map<CollectorDetail, List<Message>> errorsMap) {
        throwExceptionIfBatchNotAdded(batch);

        detailScrubberErrors.put(batch.getBatchName(), errorsMap);
    }

    /**
     * Stores the errors encountered trying to scrub the InterDepartmentalBilling records in the given batch. This method must be
     * called after addBatch has been called with the same batch. Previously saved errors for this batch will be overwritten.
     * 
     * @param batch collector batch from input xml
     * @param errorsMap
     */
    public void setBatchOriginEntryScrubberErrors(CollectorBatch batch, Map<Transaction, List<Message>> errorsMap) {
        throwExceptionIfBatchNotAdded(batch);

        originEntryScrubberErrors.put(batch.getBatchName(), errorsMap);
    }

    /**
     * Returns the scrubber errors related to a batch
     * 
     * @param batch collector batch from input xml
     * @return Map returns a map containing a list of error messages for each transaction
     */
    public Map<Transaction, List<Message>> getBatchOriginEntryScrubberErrors(CollectorBatch batch) {
        throwExceptionIfBatchNotAdded(batch);

        return originEntryScrubberErrors.get(batch.getBatchName());
    }

    public void setScrubberReportData(CollectorBatch batch, ScrubberReportData scrubberReportData) {
        throwExceptionIfBatchNotAdded(batch);

        scrubberReportDataForBatchName.put(batch.getBatchName(), scrubberReportData);
    }

    public ScrubberReportData getScrubberReportData(CollectorBatch batch) {
        throwExceptionIfBatchNotAdded(batch);

        return scrubberReportDataForBatchName.get(batch.getBatchName());
    }

    public void setDemergerReportData(CollectorBatch batch, DemergerReportData demergerReportData) {
        throwExceptionIfBatchNotAdded(batch);

        demergerReportDataForBatchName.put(batch.getBatchName(), demergerReportData);
    }

    public DemergerReportData getDemergerReportData(CollectorBatch batch) {
        throwExceptionIfBatchNotAdded(batch);

        return demergerReportDataForBatchName.get(batch.getBatchName());
    }

    public void markUnparsableFileNames(String fileName) {
        unparsableFileNames.add(fileName);
    }

    public Set<String> getAllUnparsableFileNames() {
        return Collections.unmodifiableSet(unparsableFileNames);
    }

    public void setEmailSendingStatusForParsedBatch(CollectorBatch batch, String emailStatus) {
        throwExceptionIfBatchNotAdded(batch);

        emailSendingStatus.put(batch.getBatchName(), emailStatus);
    }

    public Iterator<CollectorBatch> getAddedBatches() {
        return addedBatches.values().iterator();
    }
    
    public Map<String, String> getEmailSendingStatus() {
        return emailSendingStatus;
    }

    /**
     * Sets the number of times the details in a batch have had their account numbers changed
     * 
     * @param batch collector batch from input xml
     */
    public void setNumDetailAccountValuesChanged(CollectorBatch batch, Integer numDetailAccountValuesChanged) {
        throwExceptionIfBatchNotAdded(batch);

        numDetailAccountValuesChangedForBatchName.put(batch.getBatchName(), numDetailAccountValuesChanged);
    }

    public Integer getNumDetailAccountValuesChanged(CollectorBatch batch) {
        throwExceptionIfBatchNotAdded(batch);

        return numDetailAccountValuesChangedForBatchName.get(batch.getBatchName());
    }

    public void setNumDetailDeleted(CollectorBatch batch, Integer numDetailDeleted) {
        throwExceptionIfBatchNotAdded(batch);

        numDetailDeletedForBatchName.put(batch.getBatchName(), numDetailDeleted);
    }

    public Integer getNumDetailDeleted(CollectorBatch batch) {
        throwExceptionIfBatchNotAdded(batch);

        return numDetailDeletedForBatchName.get(batch.getBatchName());
    }

    /**
     * Stores the totals or all origin entries in the input group that match the document group (doc #, doc type, origination code)
     * of at least one origin entry in the error group, which is generated by the scrubber
     * 
     * @param batch collector batch from input xml
     * @param totals a map such that the key is a document group (doc #, doc type, origination code) and the value is the totals of
     *        the origin entry of all those
     */
    public void setTotalsOnInputOriginEntriesAssociatedWithErrorGroup(CollectorBatch batch, Map<DocumentGroupData, OriginEntryTotals> totals) {
        throwExceptionIfBatchNotAdded(batch);

        totalsOnInputOriginEntriesAssociatedWithErrorGroupForBatchName.put(batch.getBatchName(), totals);
    }

    /**
     * Returns the totals or all origin entries in the input group that match the document group (doc #, doc type, origination code)
     * of at least one origin entry in the error group, which is generated by the scrubber
     * 
     * @param batch return a map such that the key is a document group (doc #, doc type, origination code) and the value is the
     *        totals of the origin entry of all those
     */
    public Map<DocumentGroupData, OriginEntryTotals> getTotalsOnInputOriginEntriesAssociatedWithErrorGroup(CollectorBatch batch) {
        throwExceptionIfBatchNotAdded(batch);

        return totalsOnInputOriginEntriesAssociatedWithErrorGroupForBatchName.get(batch.getBatchName());
    }

    public void setNumInputDetails(CollectorBatch batch) {
        throwExceptionIfBatchNotAdded(batch);

        numInputDetailsForBatchName.put(batch.getBatchName(), batch.getCollectorDetails().size());
    }

    public Integer getNumInputDetails(CollectorBatch batch) {
        throwExceptionIfBatchNotAdded(batch);

        return numInputDetailsForBatchName.get(batch.getBatchName());
    }

    public void setNumSavedDetails(CollectorBatch batch, Integer numSavedDetails) {
        throwExceptionIfBatchNotAdded(batch);

        numSavedDetailsForBatchName.put(batch.getBatchName(), numSavedDetails);
    }

    public Integer getNumSavedDetails(CollectorBatch batch) {
        throwExceptionIfBatchNotAdded(batch);

        return numSavedDetailsForBatchName.get(batch.getBatchName());
    }

    public void incrementNumPersistedBatches() {
        numPersistedBatches++;
    }


    /**
     * Gets the numPersistedBatches attribute.
     * 
     * @return Returns the numPersistedBatches.
     */
    public int getNumPersistedBatches() {
        return numPersistedBatches;
    }

    public void incrementNumNonPersistedBatches() {
        numNotPersistedBatches++;
    }

    /**
     * Gets the numNotPersistedBatches attribute.
     * 
     * @return Returns the numNotPersistedBatches.
     */
    public int getNumNotPersistedBatches() {
        return numNotPersistedBatches;
    }

    public void incrementNumNotPersistedOriginEntryRecords(int records) {
        numNotPersistedOriginEntryRecords += records;
    }
    
    public int getNumNotPersistedOriginEntryRecords() {
        return numNotPersistedOriginEntryRecords;
    }

    public void incrementNumNotPersistedCollectorDetailRecords(int records) {
        numNotPersistedCollectorDetailRecords += records;
    }
    
    public int getNumNotPersistedCollectorDetailRecords() {
        return numNotPersistedCollectorDetailRecords;
    }
    
    /**
     * Marks whether or not a batch is valid or not
     * 
     * @param batch collector batch from input xml
     * @param validStatus valid status fro batch
     */
    public void markValidationStatus(CollectorBatch batch, boolean validStatus) {
        throwExceptionIfBatchNotAdded(batch);

        validationStatuses.put(batch.getBatchName(), Boolean.valueOf(validStatus));
    }

    /**
     * Returns true if batch is valid; False if invalid
     * 
     * @param batch collector batch from input xml
     * @return true if batch is valid
     */
    public boolean isBatchValid(CollectorBatch batch) {
        throwExceptionIfBatchNotAdded(batch);

        return (Boolean) validationStatuses.get(batch.getBatchName()).booleanValue();
    }

    /**
     * Gets the ledgerSummaryReport attribute. 
     * @return Returns the ledgerSummaryReport.
     */
    public LedgerSummaryReport getLedgerSummaryReport() {
        return ledgerSummaryReport;
    }

    public PreScrubberReportData getPreScrubberReportData() {
        return preScrubberReportData;
    }

    public MessageMap getMessageMapForFileName(String fileName) {
        MessageMap messageMap = messageMapForFileName.get(fileName);
        if (messageMap == null) {
            messageMap = new MessageMap();
            messageMapForFileName.put(fileName, messageMap);
        }
        return messageMap;
    }
    
    public Collection<String> getLoadedfileNames() {
        return loadedfileNames;
    }
}
