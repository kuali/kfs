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
package org.kuali.kfs.gl.batch;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.batch.service.impl.DocumentGroupData;
import org.kuali.kfs.gl.batch.service.impl.OriginEntryFileIterator;
import org.kuali.kfs.gl.batch.service.impl.OriginEntryTotals;
import org.kuali.kfs.gl.businessobject.CollectorDetail;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.gl.businessobject.OriginEntryInformation;
import org.kuali.kfs.gl.report.CollectorReportData;
import org.kuali.kfs.gl.service.ScrubberService;
import org.kuali.kfs.gl.service.impl.CollectorScrubberStatus;
import org.kuali.kfs.gl.service.impl.ScrubberStatus;
import org.kuali.kfs.sys.Message;
import org.kuali.kfs.sys.batch.BatchSpringContext;
import org.kuali.kfs.sys.batch.Step;
import org.kuali.kfs.sys.context.ProxyUtils;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.service.PersistenceService;


/**
 * This class scrubs the billing details in a collector batch. Note that all services used by this class are passed in as parameters
 * to the constructor. NOTE: IT IS IMPERATIVE that a new instance of this class is constructed and used to parse each batch. Sharing
 * instances to scrub multiple batches may lead to unpredictable results.
 */
public class CollectorScrubberProcess {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CollectorScrubberProcess.class);
    
    protected CollectorBatch batch;
    protected String inputFileName;
    protected String validFileName;
    protected String errorFileName;
    protected String expiredFileName;
    protected ConfigurationService kualiConfigurationService;
    protected PersistenceService persistenceService;
    protected CollectorReportData collectorReportData;
    protected ScrubberService scrubberService;
    protected DateTimeService dateTimeService;

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
    public CollectorScrubberProcess(CollectorBatch batch, ConfigurationService kualiConfigurationService, PersistenceService persistenceService, ScrubberService scrubberService, CollectorReportData collectorReportData, DateTimeService dateTimeService, String collectorFileDirectoryName) {
        this.batch = batch;
        this.kualiConfigurationService = kualiConfigurationService;
        this.persistenceService = persistenceService;
        this.collectorReportData = collectorReportData;
        this.scrubberService = scrubberService;
        this.dateTimeService = dateTimeService;
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
        ScrubberStatus scrubberStatus = new ScrubberStatus();
        Step step = BatchSpringContext.getStep(CollectorScrubberStep.STEP_NAME);
        CollectorScrubberStep collectorStep = (CollectorScrubberStep) ProxyUtils.getTargetIfProxied(step);
        collectorStep.setScrubberStatus(scrubberStatus);
        collectorStep.setBatch(batch);
        collectorStep.setCollectorReportData(collectorReportData);

        try {
            step.execute(getClass().getName(), dateTimeService.getCurrentDate());
        }
        catch (Exception e) {
            LOG.error("Exception occured executing step", e);
            throw new RuntimeException("Exception occured executing step", e);
        }
        
        CollectorScrubberStatus collectorScrubberStatus = new CollectorScrubberStatus();
        // extract the group BOs form the scrubber

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

        retrieveErrorDocumentGroups();
        
        retrieveTotalsOnInputOriginEntriesAssociatedWithErrorGroup();
        
        removeInterDepartmentalBillingAssociatedWithErrorGroup();

        applyChangesToDetailsFromScrubberEdits(scrubberStatus.getUnscrubbedToScrubbedEntries());
        
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
    protected boolean isOriginEntryRelatedToDetailRecord(OriginEntryInformation originEntry, CollectorDetail detail) {
        return StringUtils.equals(originEntry.getUniversityFiscalPeriodCode(), detail.getUniversityFiscalPeriodCode()) 
                && originEntry.getUniversityFiscalYear() != null && originEntry.getUniversityFiscalYear().equals(detail.getUniversityFiscalYear()) 
                && StringUtils.equals(originEntry.getChartOfAccountsCode(), detail.getChartOfAccountsCode()) 
                && StringUtils.equals(originEntry.getAccountNumber(), detail.getAccountNumber()) 
                && StringUtils.equals(originEntry.getSubAccountNumber(), detail.getSubAccountNumber()) 
                && StringUtils.equals(originEntry.getFinancialObjectCode(), detail.getFinancialObjectCode()) 
                && StringUtils.equals(originEntry.getFinancialSubObjectCode(), detail.getFinancialSubObjectCode()) 
                && StringUtils.equals(originEntry.getFinancialSystemOriginationCode(), detail.getFinancialSystemOriginationCode()) 
                && StringUtils.equals(originEntry.getFinancialDocumentTypeCode(), detail.getFinancialDocumentTypeCode())
                && StringUtils.equals(originEntry.getDocumentNumber(), detail.getDocumentNumber()) 
                && StringUtils.equals(originEntry.getFinancialObjectTypeCode(), detail.getFinancialObjectTypeCode());
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
    protected void applyScrubberEditsToDetail(OriginEntryInformation originEntry, CollectorDetail detail) {
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
    protected void applyChangesToDetailsFromScrubberEdits(Map<OriginEntryInformation, OriginEntryInformation> unscrubbedToScrubbedEntries) {
        Set<Entry<OriginEntryInformation, OriginEntryInformation>> mappings = unscrubbedToScrubbedEntries.entrySet();

        int numDetailAccountValuesChanged = 0;
        for (CollectorDetail detail : batch.getCollectorDetails()) {
            for (Entry<OriginEntryInformation, OriginEntryInformation> mapping : mappings) {
                OriginEntryInformation originalEntry = mapping.getKey();
                OriginEntryInformation scrubbedEntry = mapping.getValue();
                // TODO: this algorithm could be made faster using a lookup table instead of a nested loop
                if (isOriginEntryRelatedToDetailRecord(originalEntry, detail)) {
                    if (!StringUtils.equals(originalEntry.getChartOfAccountsCode(), scrubbedEntry.getChartOfAccountsCode()) || !StringUtils.equals(originalEntry.getAccountNumber(), scrubbedEntry.getAccountNumber())) {
                        numDetailAccountValuesChanged++;
                    }
                    applyScrubberEditsToDetail(scrubbedEntry, detail);
                    break;
                }
            }
        }
        
        collectorReportData.setNumDetailAccountValuesChanged(batch, numDetailAccountValuesChanged);
    }

    /**
     * Based on the origin entries in the origin entry scrubber-produced error group, creates a set of all {@link DocumentGroupData}s
     * represented by those origin entries and initializes the {@link #errorDocumentGroups} variable
     */
    protected void retrieveErrorDocumentGroups() {
        File errorFile = new File( collectorFileDirectoryName + File.separator + errorFileName);
        OriginEntryFileIterator entryIterator = new OriginEntryFileIterator(errorFile);
        errorDocumentGroups = DocumentGroupData.getDocumentGroupDatasForTransactions(entryIterator);
    }

    /**
     * Computes the totals of the input entries that were associated with the entries in the error group, which is created in the
     * scrubber. These totals are reflected in the collector report data object.
     */
    protected void retrieveTotalsOnInputOriginEntriesAssociatedWithErrorGroup() {
        Map<DocumentGroupData, OriginEntryTotals> inputDocumentTotals = new HashMap<DocumentGroupData, OriginEntryTotals>();

        File inputFile = new File(collectorFileDirectoryName + File.separator + inputFileName);
        OriginEntryFileIterator inputIterator = new OriginEntryFileIterator(inputFile);

        while (inputIterator.hasNext()) {
            OriginEntryFull originEntryFull = (OriginEntryFull) inputIterator.next();

            DocumentGroupData inputGroupData = new DocumentGroupData(originEntryFull.getDocumentNumber(), originEntryFull.getFinancialDocumentTypeCode(), originEntryFull.getFinancialSystemOriginationCode());
            if (errorDocumentGroups.contains(inputGroupData)) {
                OriginEntryTotals originEntryTotals = new OriginEntryTotals();
                if (inputDocumentTotals.containsKey(inputGroupData)) {
                    originEntryTotals = inputDocumentTotals.get(inputGroupData);
                }

                originEntryTotals.addToTotals(originEntryFull);
                inputDocumentTotals.put(inputGroupData, originEntryTotals);
            }
        }

        collectorReportData.setTotalsOnInputOriginEntriesAssociatedWithErrorGroup(batch, inputDocumentTotals);
    }
}
