/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.gl.batch;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.batch.service.impl.DocumentGroupData;
import org.kuali.kfs.gl.batch.service.impl.OriginEntryFileIterator;
import org.kuali.kfs.gl.batch.service.impl.OriginEntryTotals;
import org.kuali.kfs.gl.businessobject.CollectorDetail;
import org.kuali.kfs.gl.businessobject.OriginEntry;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.gl.report.CollectorReportData;
import org.kuali.kfs.gl.service.OriginEntryGroupService;
import org.kuali.kfs.gl.service.OriginEntryService;
import org.kuali.kfs.gl.service.ScrubberService;
import org.kuali.kfs.gl.service.impl.CollectorScrubberStatus;
import org.kuali.kfs.gl.service.impl.ScrubberStatus;
import org.kuali.kfs.sys.Message;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.PersistenceService;

/**
 * This class scrubs the billing details in a collector batch. Note that all services used by this class are passed in as parameters
 * to the constructor. NOTE: IT IS IMPERATIVE that a new instance of this class is constructed and used to parse each batch. Sharing
 * instances to scrub multiple batches may lead to unpredictable results.
 */
public class CollectorScrubberProcess {
    protected CollectorBatch batch;
    protected String inputFileName;
    protected String validFileName;
    protected String errorFileName;
    protected String expiredFileName;
    protected OriginEntryService originEntryService;
    protected OriginEntryGroupService originEntryGroupService;
    protected KualiConfigurationService kualiConfigurationService;
    protected PersistenceService persistenceService;
    protected CollectorReportData collectorReportData;
    protected ScrubberService scrubberService;

    protected Set<DocumentGroupData> errorDocumentGroups;
    protected String collectorFileDirectoryName;
    
    /**
     * Constructs a CollectorScrubberProcess.java.
     * 
     * @param batch the batch to scrub
     * @param inputGroup the origin entry group that holds all of the origin entries coming from the parsed input groups in the
     *        given batch
     * @param validGroup the origin entry group that holds all of the origin entries coming that are in the origin entry scrubber
     *        valid group
     * @param errorGroup the origin entry group that holds all of the origin entries coming that are in the origin entry scrubber
     *        error group
     * @param expiredGroup are in the origin entry scrubber valid group that are in the origin entry scrubber expired group
     * @param originEntryService the origin entry service that holds the origin entries in the batch (not necessarily the default
     *        implementation)
     * @param originEntryGroupService the origin entry group service that holds the 3 group parameters (not necessarily the default
     *        implementation)
     * @param kualiConfigurationService the config service
     * @param persistenceService the persistence service
     */
    public CollectorScrubberProcess(CollectorBatch batch, OriginEntryService originEntryService, OriginEntryGroupService originEntryGroupService, KualiConfigurationService kualiConfigurationService, PersistenceService persistenceService, ScrubberService scrubberService, CollectorReportData collectorReportData, String collectorFileDirectoryName) {
        this.batch = batch;
        this.originEntryService = originEntryService;
        this.originEntryGroupService = originEntryGroupService;
        this.kualiConfigurationService = kualiConfigurationService;
        this.persistenceService = persistenceService;
        this.collectorReportData = collectorReportData;
        this.scrubberService = scrubberService;
        this.collectorFileDirectoryName = collectorFileDirectoryName;
    }

    /**
     * Scrubs the entries read in by the Collector
     * 
     * @return a CollectorScrubberStatus object encapsulating the results of the scrubbing process
     */
    public CollectorScrubberStatus scrub() {
        // for the collector origin entry group scrub, we make sure that we're using a custom impl of the origin entry service and
        // group service.
        ScrubberStatus scrubberStatus = scrubberService.scrubCollectorBatch(batch, collectorReportData, originEntryService, originEntryGroupService, collectorFileDirectoryName);
        
        CollectorScrubberStatus collectorScrubberStatus = new CollectorScrubberStatus();

        // extract the group BOs form the scrubber

        //TODO: Shawn - need to change for file name here....instead of groups... 
        // the FileName that contains all of the origin entries from the collector file
        inputFileName = scrubberStatus.getInputFileName();
        collectorScrubberStatus.setInputFileName(inputFileName);

        // the FileName that contains all of the origin entries from the scrubber valid FileName
        validFileName = scrubberStatus.getValidFileName();
        collectorScrubberStatus.setValidFileName(validFileName);

        // the FileName that contains all of the origin entries from the scrubber error FileName
        errorFileName = scrubberStatus.getErrorFileName();
        collectorScrubberStatus.setErrorFileName(errorFileName);

        // the FileName that contains all of the origin entries from the scrubber expired FileName (expired accounts)
        expiredFileName = scrubberStatus.getExpiredFileName();
        collectorScrubberStatus.setExpiredFileName(expiredFileName);

        collectorScrubberStatus.setOriginEntryGroupService(originEntryGroupService);
        collectorScrubberStatus.setOriginEntryService(originEntryService);

        // TODO: Shawn - I will have scrberr1, so run with this file
        retrieveErrorDocumentGroups();
        
        // TODO: -- Jeff
        //retrieveTotalsOnInputOriginEntriesAssociatedWithErrorGroup();
        
        //TODO: Shawn - need to change using file -- scrberr1 file 
        removeInterDepartmentalBillingAssociatedWithErrorGroup();

        applyChangesToDetailsFromScrubberEdits(scrubberStatus.getUnscrubbedToScrubbedEntries());
        
        //TODO: Shawn - don't need this method
        //retainBatchValidEntriesOnly();

        return collectorScrubberStatus;
    }

    /**
     * Removes Collector IB details not associated with entries in the Collector data
     */
    protected void removeInterDepartmentalBillingNotAssociatedWithInputEntries() {
        Iterator<CollectorDetail> detailIter = batch.getCollectorDetails().iterator();
        while (detailIter.hasNext()) {
            CollectorDetail detail = detailIter.next();
            for (OriginEntryFull inputEntry : batch.getOriginEntries()) {
                if (!isOriginEntryRelatedToDetailRecord(inputEntry, detail)) {
                    // TODO: add reporting data
                    detailIter.remove();
                }
            }
        }
    }

    /**
     * This method's purpose is similar to the scrubber's demerger. This method scans through all of the origin entries and removes
     * those billing details that share the same doc number, doc type, and origination code
     */
    protected void removeInterDepartmentalBillingAssociatedWithErrorGroup() {
        int numDetailDeleted = 0;
        Iterator<CollectorDetail> detailIter = batch.getCollectorDetails().iterator();
        while (detailIter.hasNext()) {
            CollectorDetail detail = detailIter.next();
            for (DocumentGroupData errorDocumentGroup : errorDocumentGroups) {
                if (errorDocumentGroup.matchesCollectorDetail(detail)) {
                    numDetailDeleted++;
                    detailIter.remove();
                }
            }
        }

        collectorReportData.setNumDetailDeleted(batch, new Integer(numDetailDeleted));
    }

    /**
     * Determines if an origin entry is related to the given Collector detail record
     * 
     * @param originEntry the origin entry to check
     * @param detail the Collector detail to check against
     * @return true if the origin entry is related, false otherwise
     */
    protected boolean isOriginEntryRelatedToDetailRecord(OriginEntry originEntry, CollectorDetail detail) {
        return StringUtils.equals(originEntry.getUniversityFiscalPeriodCode(), detail.getUniversityFiscalPeriodCode()) && originEntry.getUniversityFiscalYear() != null && originEntry.getUniversityFiscalYear().equals(detail.getUniversityFiscalYear()) && StringUtils.equals(originEntry.getChartOfAccountsCode(), detail.getChartOfAccountsCode()) && StringUtils.equals(originEntry.getAccountNumber(), detail.getAccountNumber()) && StringUtils.equals(originEntry.getSubAccountNumber(), detail.getSubAccountNumber()) && StringUtils.equals(originEntry.getFinancialObjectCode(), detail.getFinancialObjectCode()) && StringUtils.equals(originEntry.getFinancialSubObjectCode(), detail.getFinancialSubObjectCode()) && StringUtils.equals(originEntry.getFinancialSystemOriginationCode(), detail.getFinancialSystemOriginationCode()) && StringUtils.equals(originEntry.getFinancialDocumentTypeCode(), detail.getFinancialDocumentTypeCode())
                && StringUtils.equals(originEntry.getDocumentNumber(), detail.getDocumentNumber()) && StringUtils.equals(originEntry.getFinancialBalanceTypeCode(), detail.getFinancialBalanceTypeCode()) && StringUtils.equals(originEntry.getFinancialObjectTypeCode(), detail.getFinancialObjectTypeCode());
    }

    /**
     * Determines if one of the messages in the given list of errors is a fatal message
     * 
     * @param errors a List of errors generated by the scrubber
     * @return true if one of the errors was fatal, false otherwise
     */
    private boolean hasFatal(List<Message> errors) {
        for (Iterator<Message> iter = errors.iterator(); iter.hasNext();) {
            Message element = iter.next();
            if (element.getType() == Message.TYPE_FATAL) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if any of the error messages in the given list are warnings
     * 
     * @param errors a list of errors generated by the Scrubber
     * @return true if there are any warnings in the list, false otherwise
     */
    private boolean hasWarning(List<Message> errors) {
        for (Iterator<Message> iter = errors.iterator(); iter.hasNext();) {
            Message element = iter.next();
            if (element.getType() == Message.TYPE_WARNING) {
                return true;
            }
        }
        return false;
    }

    /**
     * Updates the Collector detail with the data from a scrubbed entry
     * 
     * @param originEntry a scrubbed origin entry
     * @param detail a Collector detail to update
     */
    protected void applyScrubberEditsToDetail(OriginEntry originEntry, CollectorDetail detail) {
        detail.setUniversityFiscalPeriodCode(originEntry.getUniversityFiscalPeriodCode());
        detail.setUniversityFiscalYear(originEntry.getUniversityFiscalYear());
        detail.setChartOfAccountsCode(originEntry.getChartOfAccountsCode());
        detail.setAccountNumber(originEntry.getAccountNumber());
        detail.setSubAccountNumber(originEntry.getSubAccountNumber());
        detail.setFinancialObjectCode(originEntry.getFinancialObjectCode());
        detail.setFinancialSubObjectCode(originEntry.getFinancialSubObjectCode());
        detail.setFinancialSystemOriginationCode(originEntry.getFinancialSystemOriginationCode());
        detail.setFinancialDocumentTypeCode(originEntry.getFinancialDocumentTypeCode());
        detail.setDocumentNumber(originEntry.getDocumentNumber());
        detail.setFinancialBalanceTypeCode(originEntry.getFinancialBalanceTypeCode());
        detail.setFinancialObjectTypeCode(originEntry.getFinancialObjectTypeCode());
    }

    /**
     * Updates all Collector details with the data from scrubbed origin entries
     * 
     * @param unscrubbedToScrubbedEntries a Map relating original origin entries to scrubbed origin entries
     */
    protected void applyChangesToDetailsFromScrubberEdits(Map<OriginEntry, OriginEntry> unscrubbedToScrubbedEntries) {
        Set<Entry<OriginEntry, OriginEntry>> mappings = unscrubbedToScrubbedEntries.entrySet();

        for (CollectorDetail detail : batch.getCollectorDetails()) {
            int numDetailAccountValuesChanged = 0;
            for (Entry<OriginEntry, OriginEntry> mapping : mappings) {
                OriginEntry originalEntry = mapping.getKey();
                OriginEntry scrubbedEntry = mapping.getValue();
                if (isOriginEntryRelatedToDetailRecord(originalEntry, detail)) {
                    if (!StringUtils.equals(originalEntry.getChartOfAccountsCode(), scrubbedEntry.getChartOfAccountsCode()) && !StringUtils.equals(originalEntry.getAccountNumber(), scrubbedEntry.getAccountNumber())) {
                        // TODO: determine whether the account was closed/expired before incrementing?
                        numDetailAccountValuesChanged++;
                    }
                    applyScrubberEditsToDetail(scrubbedEntry, detail);
                    break;
                }
            }
            collectorReportData.setNumDetailAccountValuesChanged(batch, numDetailAccountValuesChanged);
        }
    }

    /**
     * Updates the origin entries read in by the collector are only the entries the scrubber declared valid
     */
//    protected void retainBatchValidEntriesOnly() {
//        Iterator<OriginEntryFull> validEntriesIter = originEntryService.getEntriesByGroup(validGroup);
//        List<OriginEntryFull> validEntries = new ArrayList<OriginEntryFull>();
//        while (validEntriesIter.hasNext()) {
//            OriginEntryFull entry = validEntriesIter.next();
//
//            // clear out the entry ID, (which is the PK) because the origin entry service we're using for the scrubber
//            // may not assign a correct entry ID that should be used to persist in the DB, causing optimistic locking
//            // exceptions.
//            entry.setEntryId(null);
//            validEntries.add(entry);
//        }
//        batch.setOriginEntries(validEntries);
//    }

    /**
     * Based on the origin entries in the origin entry scrubber-produced error group, creates a set of all {@link DocumentGroupData}s
     * represented by those origin entries and initializes the {@link #errorDocumentGroups} variable
     */
    protected void retrieveErrorDocumentGroups() {
        //List<OriginEntryFull> errorEntries = originEntryService.getEntriesByGroupId(errorGroup.getId());
        File errorFile = new File(collectorFileDirectoryName + File.separator + errorFileName);
        OriginEntryFileIterator entryIterator = new OriginEntryFileIterator(errorFile);
        //errorDocumentGroups = DocumentGroupData.getDocumentGroupDatasForTransactions(errorEntries.iterator());
        errorDocumentGroups = DocumentGroupData.getDocumentGroupDatasForTransactions(entryIterator);
    }

    /**
     * Computes the totals of the input entries that were associated with the entries in the error group, which is created in the
     * scrubber. For the purposes of this method, an input origin entry associated with the error group is (1) an origin entry that
     * was parsed from the collector file (i.e. in the input group), and (2) these exists an origin entry in the error group such
     * that the input origin entry and the error origin entry have equal doc number, doc type, and origination code. These totals
     * are reflected in the collector report data object.
     */
//    protected void retrieveTotalsOnInputOriginEntriesAssociatedWithErrorGroup() {
//        Map<DocumentGroupData, OriginEntryTotals> inputDocumentTotals = new HashMap<DocumentGroupData, OriginEntryTotals>();
//
//        for (DocumentGroupData errorDocumentGroupData : errorDocumentGroups) {
//            Iterator<OriginEntryFull> associatedInputEntries = originEntryService.getEntriesByDocument(inputGroup, errorDocumentGroupData.getDocumentNumber(), errorDocumentGroupData.getFinancialDocumentTypeCode(), errorDocumentGroupData.getFinancialSystemOriginationCode());
//            OriginEntryTotals originEntryTotals = new OriginEntryTotals();
//            originEntryTotals.addToTotals(associatedInputEntries);
//            inputDocumentTotals.put(errorDocumentGroupData, originEntryTotals);
//        }
//
//        collectorReportData.setTotalsOnInputOriginEntriesAssociatedWithErrorGroup(batch, inputDocumentTotals);
//    }
}
