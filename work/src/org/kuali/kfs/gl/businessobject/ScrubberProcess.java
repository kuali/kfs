/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.gl.service.impl;

import java.sql.Date;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.core.bo.DocumentType;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.DocumentTypeService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.PersistenceService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.Options;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ParameterEvaluator;
import org.kuali.kfs.service.ParameterService;
import org.kuali.kfs.util.Message;
import org.kuali.module.chart.bo.A21SubAccount;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.OffsetDefinition;
import org.kuali.module.chart.bo.codes.BalanceTyp;
import org.kuali.module.chart.service.ObjectCodeService;
import org.kuali.module.chart.service.OffsetDefinitionService;
import org.kuali.module.financial.exceptions.InvalidFlexibleOffsetException;
import org.kuali.module.financial.service.FlexibleOffsetAccountService;
import org.kuali.module.gl.GLConstants;
import org.kuali.module.gl.batch.ScrubberStep;
import org.kuali.module.gl.batch.collector.CollectorBatch;
import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.module.gl.bo.OriginEntryFull;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.OriginEntryLite;
import org.kuali.module.gl.bo.OriginEntrySource;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.bo.UniversityDate;
import org.kuali.module.gl.dao.UniversityDateDao;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.gl.service.OriginEntryLiteService;
import org.kuali.module.gl.service.OriginEntryLookupService;
import org.kuali.module.gl.service.OriginEntryService;
import org.kuali.module.gl.service.ReportService;
import org.kuali.module.gl.service.RunDateService;
import org.kuali.module.gl.service.ScrubberProcessObjectCodeOverride;
import org.kuali.module.gl.service.ScrubberValidator;
import org.kuali.module.gl.service.impl.scrubber.DemergerReportData;
import org.kuali.module.gl.service.impl.scrubber.ScrubberReportData;
import org.kuali.module.gl.util.CachingLookup;
import org.kuali.module.gl.util.CollectorReportData;
import org.kuali.module.gl.util.ObjectHelper;
import org.kuali.module.gl.util.OriginEntryStatistics;
import org.kuali.module.gl.util.ScrubberStatus;
import org.kuali.module.gl.util.StringHelper;
import org.springframework.util.StringUtils;

/**
 * This class has the logic for the scrubber. It is required because the scrubber process needs instance variables. Instance
 * variables in a spring service are shared between all code calling the service. This will make sure each run of the scrubber has
 * it's own instance variables instead of being shared.
 */
public class ScrubberProcess {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ScrubberProcess.class);

    private static final String TRANSACTION_TYPE_COST_SHARE_ENCUMBRANCE = "CE";
    private static final String TRANSACTION_TYPE_OFFSET = "O";
    private static final String TRANSACTION_TYPE_CAPITALIZATION = "C";
    private static final String TRANSACTION_TYPE_LIABILITY = "L";
    private static final String TRANSACTION_TYPE_TRANSFER = "T";
    private static final String TRANSACTION_TYPE_COST_SHARE = "CS";
    private static final String TRANSACTION_TYPE_OTHER = "X";

    private static final String COST_SHARE_CODE = "CSHR";

    private static final String COST_SHARE_TRANSFER_ENTRY_IND = "***";

    // These lengths are different then database field lengths, hence they are not from the DD
    private static final int COST_SHARE_ENCUMBRANCE_ENTRY_MAXLENGTH = 28;
    private static final int DEMERGER_TRANSACTION_LEDGET_ENTRY_DESCRIPTION = 33;
    private static final int OFFSET_MESSAGE_MAXLENGTH = 33;

    /* Services required */
    private FlexibleOffsetAccountService flexibleOffsetAccountService;
    private DocumentTypeService documentTypeService;
    private OriginEntryService originEntryService;
    private OriginEntryLiteService originEntryLiteService;
    private OriginEntryGroupService originEntryGroupService;
    private DateTimeService dateTimeService;
    private OffsetDefinitionService offsetDefinitionService;
    private ObjectCodeService objectCodeService;
    private KualiConfigurationService configurationService;
    private UniversityDateDao universityDateDao;
    private PersistenceService persistenceService;
    private ReportService reportService;
    private ScrubberValidator scrubberValidator;
    private ScrubberProcessObjectCodeOverride scrubberProcessObjectCodeOverride;
    private RunDateService runDateService;
    private ThreadLocal<OriginEntryLookupService> referenceLookup = new ThreadLocal<OriginEntryLookupService>();

    // this will only be populated when in collector mode, otherwise the memory requirements will be huge
    private Map<OriginEntry, OriginEntry> unscrubbedToUnscrubbedEntries;

    /* These are all different forms of the run date for this job */
    private Date runDate;
    private Calendar runCal;
    private UniversityDate universityRunDate;
    private String offsetString;

    /* These are the output groups */
    private OriginEntryGroup validGroup;
    private OriginEntryGroup errorGroup;
    private OriginEntryGroup expiredGroup;

    /* Unit Of Work info */
    private UnitOfWorkInfo unitOfWork;
    private KualiDecimal scrubCostShareAmount;

    /* Statistics for the reports */
    private ScrubberReportData scrubberReport;
    private Map<Transaction, List<Message>> scrubberReportErrors;
    private List<Message> transactionErrors;
    private DemergerReportData demergerReport;

    /* Description names */
    private String offsetDescription;
    private String capitalizationDescription;
    private String liabilityDescription;
    private String transferDescription;
    private String costShareDescription;

    private ParameterService parameterService;

    /* Misc stuff */
    private boolean reportOnlyMode;
    /**
     * Whether this instance is being used to support the scrubbing of a collector batch
     */
    private boolean collectorMode;

    /**
     * These parameters are all the dependencies.
     */
    public ScrubberProcess(FlexibleOffsetAccountService flexibleOffsetAccountService, DocumentTypeService documentTypeService, OriginEntryService originEntryService, OriginEntryGroupService originEntryGroupService, DateTimeService dateTimeService, OffsetDefinitionService offsetDefinitionService, ObjectCodeService objectCodeService, KualiConfigurationService configurationService, UniversityDateDao universityDateDao, PersistenceService persistenceService, ReportService reportService, ScrubberValidator scrubberValidator, ScrubberProcessObjectCodeOverride scrubberProcessObjectCodeOverride, RunDateService runDateService, OriginEntryLiteService originEntryLiteService) {
        super();
        this.flexibleOffsetAccountService = flexibleOffsetAccountService;
        this.documentTypeService = documentTypeService;
        this.originEntryService = originEntryService;
        this.originEntryLiteService = originEntryLiteService;
        this.originEntryGroupService = originEntryGroupService;
        this.dateTimeService = dateTimeService;
        this.offsetDefinitionService = offsetDefinitionService;
        this.objectCodeService = objectCodeService;
        this.configurationService = configurationService;
        this.universityDateDao = universityDateDao;
        this.persistenceService = persistenceService;
        this.reportService = reportService;
        this.scrubberValidator = scrubberValidator;
        this.unscrubbedToUnscrubbedEntries = new HashMap<OriginEntry, OriginEntry>();
        this.scrubberProcessObjectCodeOverride = scrubberProcessObjectCodeOverride;
        this.runDateService = runDateService;
        collectorMode = false;
        parameterService = SpringContext.getBean(ParameterService.class);
    }

    /**
     * Scrub this single group read only. This will only output the scrubber report. It won't output any other groups.
     * 
     * @param group the origin entry group that should be scrubbed
     * @param the document number of any specific entries to scrub
     */
    public void scrubGroupReportOnly(OriginEntryGroup group, String documentNumber) {
        LOG.debug("scrubGroupReportOnly() started");

        scrubEntries(group, documentNumber);
    }

    /**
     * Scrubs all entries in all groups and documents.
     */
    public void scrubEntries() {
        scrubEntries(null, null);
    }

    /**
     * Scrubs the origin entry and ID billing details if the given batch. Store all scrubber output into the collectorReportData
     * parameter. NOTE: DO NOT CALL ANY OF THE scrub* METHODS OF THIS CLASS AFTER CALLING THIS METHOD FOR EVERY UNIQUE INSTANCE OF
     * THIS CLASS, OR THE COLLECTOR REPORTS MAY BE CORRUPTED
     * 
     * @param batch the data gathered from a Collector file
     * @param collectorReportData the statistics generated by running the Collector
     */
    public ScrubberStatus scrubCollectorBatch(CollectorBatch batch, CollectorReportData collectorReportData) {
        collectorMode = true;

        OriginEntryGroup group = originEntryGroupService.createGroup(batch.getTransmissionDate(), OriginEntrySource.COLLECTOR, false, false, false);
        for (OriginEntryFull originEntry : batch.getOriginEntries()) {
            originEntry.setGroup(group);
            originEntryService.save(originEntry);
        }

        // first, scrub the origin entries
        scrubEntries(group, null);
        // the scrubber process has just updated several member variables of this class. Store these values for the collector report
        collectorReportData.setBatchOriginEntryScrubberErrors(batch, scrubberReportErrors);
        collectorReportData.setScrubberReportData(batch, scrubberReport);
        collectorReportData.setDemergerReportData(batch, demergerReport);

        ScrubberStatus scrubberStatus = new ScrubberStatus();
        scrubberStatus.setInputGroup(group);
        scrubberStatus.setValidGroup(validGroup);
        scrubberStatus.setErrorGroup(errorGroup);
        scrubberStatus.setExpiredGroup(expiredGroup);
        scrubberStatus.setUnscrubbedToScrubbedEntries(unscrubbedToUnscrubbedEntries);
        return scrubberStatus;
    }

    /**
     * Scrub all entries that need it in origin entry. Put valid scrubbed entries in a scrubber valid group, put errors in a
     * scrubber error group, and transactions with an expired account in the scrubber expired account group.
     * @param group the specific origin entry group to scrub
     * @param documentNumber the number of the document with entries to scrub
     */
    public void scrubEntries(OriginEntryGroup group, String documentNumber) {
        LOG.debug("scrubEntries() started");

        // We are in report only mode if we pass a group to this method.
        // if not, we are in batch mode and we scrub the backup group
        reportOnlyMode = (group != null) && !collectorMode;

        scrubberReportErrors = new HashMap<Transaction, List<Message>>();

        // setup an object to hold the "default" date information
        runDate = calculateRunDate(dateTimeService.getCurrentDate());
        runCal = Calendar.getInstance();
        runCal.setTime(runDate);

        universityRunDate = universityDateDao.getByPrimaryKey(runDate);
        if (universityRunDate == null) {
            throw new IllegalStateException(configurationService.getPropertyString(KFSKeyConstants.ERROR_UNIV_DATE_NOT_FOUND));
        }

        setOffsetString();
        setDescriptions();

        // Create the groups that will store the valid and error entries that come out of the scrubber
        // We don't need groups for the reportOnlyMode
        if (!reportOnlyMode) {
            if (collectorMode) {
                // for collector mode, these groups are not meant to be permanently persisted.
                // after the collector is done, it will delete these groups, but in case there's a failure and the following groups
                // aren't created,
                // we set all of the group flags to false to prevent these groups from entering the nightly cycle
                validGroup = originEntryGroupService.createGroup(runDate, OriginEntrySource.SCRUBBER_VALID, false, false, false);
                errorGroup = originEntryGroupService.createGroup(runDate, OriginEntrySource.SCRUBBER_ERROR, false, false, false);
                expiredGroup = originEntryGroupService.createGroup(runDate, OriginEntrySource.SCRUBBER_EXPIRED, false, false, false);
            }
            else {
                validGroup = originEntryGroupService.createGroup(runDate, OriginEntrySource.SCRUBBER_VALID, true, true, false);
                errorGroup = originEntryGroupService.createGroup(runDate, OriginEntrySource.SCRUBBER_ERROR, false, true, false);
                expiredGroup = originEntryGroupService.createGroup(runDate, OriginEntrySource.SCRUBBER_EXPIRED, false, true, false);
            }
        }

        // get the origin entry groups to be processed by Scrubber
        Collection<OriginEntryGroup> groupsToScrub = null;
        if (reportOnlyMode || collectorMode) {
            groupsToScrub = new ArrayList<OriginEntryGroup>();
            groupsToScrub.add(group);
        }
        else {
            groupsToScrub = originEntryGroupService.getAllScrubbableBackupGroups();
        }
        LOG.debug("scrubEntries() number of groups to scrub: " + groupsToScrub.size());

        // generate the reports based on the origin entries to be processed by scrubber
        if (reportOnlyMode) {
            reportService.generateScrubberLedgerSummaryReportOnline(runDate, group, documentNumber);
        }
        else if (collectorMode) {
            // defer report generation for later
        }
        else {
            reportService.generateScrubberLedgerSummaryReportBatch(runDate, groupsToScrub);
        }

        // Scrub all of the OriginEntryGroups waiting to be scrubbed as of runDate.
        scrubberReport = new ScrubberReportData();
        for (Iterator iteratorOverGroups = groupsToScrub.iterator(); iteratorOverGroups.hasNext();) {
            OriginEntryGroup originEntryGroup = (OriginEntryGroup) iteratorOverGroups.next();
            LOG.debug("scrubEntries() Scrubbing group " + originEntryGroup.getId());

            processGroup(originEntryGroup);

            if (!reportOnlyMode && !collectorMode) {
                // Mark the origin entry group as being processed ...
                originEntryGroup.setProcess(Boolean.FALSE);

                // ... and save the origin entry group with the new process flag.
                originEntryGroupService.save(originEntryGroup);
            }
        }

        // generate the scrubber status summary report
        if (reportOnlyMode) {
            reportService.generateOnlineScrubberStatisticsReport(group.getId(), runDate, scrubberReport, scrubberReportErrors, documentNumber);
        }
        else if (collectorMode) {
            // defer report generation for later
        }
        else {
            reportService.generateBatchScrubberStatisticsReport(runDate, scrubberReport, scrubberReportErrors);
        }

        // run the demerger if during regular nightly processing and collector processing
        if (!reportOnlyMode) {
            performDemerger(errorGroup, validGroup);
        }


        // Run the reports
        if (reportOnlyMode) {
            // Run transaction list
            reportService.generateScrubberTransactionsOnline(runDate, group, documentNumber);
        }
        else if (collectorMode) {
            // defer report generation for later
        }
        else {
            // Run bad balance type report and removed transaction report
            reportService.generateScrubberBadBalanceTypeListingReport(runDate, groupsToScrub);
            reportService.generateScrubberRemovedTransactions(runDate, errorGroup);
        }
    }

    /**
     * The demerger process reads all of the documents in the error group, then moves all of the original entries for that document
     * from the valid group to the error group. It does not move generated entries to the error group. Those are deleted. It also
     * modifies the doc number and origin code of cost share transfers.
     * 
     * @param errorGroup this scrubber run's error group
     * @param validGroup this scrubber run's valid group
     */
    private void performDemerger(OriginEntryGroup errorGroup, OriginEntryGroup validGroup) {
        LOG.debug("performDemerger() started");

        // Without this step, the job fails with Optimistic Lock Exceptions
        persistenceService.clearCache();

        demergerReport = new DemergerReportData();

        OriginEntryStatistics eOes = originEntryService.getStatistics(errorGroup.getId());
        demergerReport.setErrorTransactionsRead(eOes.getRowCount());

        // Read all the documents from the error group and move all non-generated
        // transactions for these documents from the valid group into the error group
        Collection<OriginEntryFull> errorDocuments = originEntryService.getDocumentsByGroup(errorGroup);
        Iterator<OriginEntryFull> i = errorDocuments.iterator();
        while (i.hasNext()) {
            OriginEntryFull document = i.next();

            // Get all the transactions for the document in the valid group
            Iterator<OriginEntryLite> transactions = originEntryLiteService.getEntriesByDocument(validGroup, document.getDocumentNumber(), document.getFinancialDocumentTypeCode(), document.getFinancialSystemOriginationCode());

            while (transactions.hasNext()) {
                OriginEntryLite transaction = transactions.next();

                String transactionType = getTransactionType(transaction);

                if (TRANSACTION_TYPE_COST_SHARE_ENCUMBRANCE.equals(transactionType)) {
                    demergerReport.incrementCostShareEncumbranceTransactionsBypassed();
                    originEntryLiteService.delete(transaction);
                }
                else if (TRANSACTION_TYPE_OFFSET.equals(transactionType)) {
                    demergerReport.incrementOffsetTransactionsBypassed();
                    originEntryLiteService.delete(transaction);
                }
                else if (TRANSACTION_TYPE_CAPITALIZATION.equals(transactionType)) {
                    demergerReport.incrementCapitalizationTransactionsBypassed();
                    originEntryLiteService.delete(transaction);
                }
                else if (TRANSACTION_TYPE_LIABILITY.equals(transactionType)) {
                    demergerReport.incrementLiabilityTransactionsBypassed();
                    originEntryLiteService.delete(transaction);
                }
                else if (TRANSACTION_TYPE_TRANSFER.equals(transactionType)) {
                    demergerReport.incrementTransferTransactionsBypassed();
                    originEntryLiteService.delete(transaction);
                }
                else if (TRANSACTION_TYPE_COST_SHARE.equals(transactionType)) {
                    demergerReport.incrementCostShareTransactionsBypassed();
                    originEntryLiteService.delete(transaction);
                }
                else {
                    demergerReport.incrementErrorTransactionsSaved();
                    transaction.setEntryGroupId(errorGroup.getId());
                    originEntryLiteService.save(transaction);
                }
            }
        }

        // Read all the transactions in the error group and delete the generated ones
        Iterator<OriginEntryLite> ie = originEntryLiteService.getEntriesByGroup(errorGroup);
        while (ie.hasNext()) {
            OriginEntryLite transaction = ie.next();

            String transactionType = getTransactionType(transaction);

            if (TRANSACTION_TYPE_COST_SHARE_ENCUMBRANCE.equals(transactionType)) {
                demergerReport.incrementCostShareEncumbranceTransactionsBypassed();
                originEntryLiteService.delete(transaction);
            }
            else if (TRANSACTION_TYPE_OFFSET.equals(transactionType)) {
                demergerReport.incrementOffsetTransactionsBypassed();
                originEntryLiteService.delete(transaction);
            }
            else if (TRANSACTION_TYPE_CAPITALIZATION.equals(transactionType)) {
                demergerReport.incrementCapitalizationTransactionsBypassed();
                originEntryLiteService.delete(transaction);
            }
            else if (TRANSACTION_TYPE_LIABILITY.equals(transactionType)) {
                demergerReport.incrementLiabilityTransactionsBypassed();
                originEntryLiteService.delete(transaction);
            }
            else if (TRANSACTION_TYPE_TRANSFER.equals(transactionType)) {
                demergerReport.incrementTransferTransactionsBypassed();
                originEntryLiteService.delete(transaction);
            }
            else if (TRANSACTION_TYPE_COST_SHARE.equals(transactionType)) {
                demergerReport.incrementCostShareTransactionsBypassed();
                originEntryLiteService.delete(transaction);
            }
        }

        // Read all the transactions in the valid group and update the cost share transactions
        Iterator<OriginEntryLite> it = originEntryLiteService.getEntriesByGroup(validGroup);
        while (it.hasNext()) {
            OriginEntryLite transaction = it.next();
            demergerReport.incrementValidTransactionsSaved();

            String transactionType = getTransactionType(transaction);
            if (TRANSACTION_TYPE_COST_SHARE.equals(transactionType)) {
                transaction.setFinancialDocumentTypeCode(KFSConstants.TRANSFER_FUNDS);
                transaction.setFinancialSystemOriginationCode(KFSConstants.COST_SHARE);
                StringBuffer docNbr = new StringBuffer(COST_SHARE_CODE);

                String desc = transaction.getTransactionLedgerEntryDescription();

                docNbr.append(desc.substring(36, 38));
                docNbr.append("/");
                docNbr.append(desc.substring(38, 40));
                transaction.setDocumentNumber(docNbr.toString());

                transaction.setTransactionLedgerEntryDescription(desc.substring(0, DEMERGER_TRANSACTION_LEDGET_ENTRY_DESCRIPTION));

                originEntryLiteService.save(transaction);
            }
        }

        eOes = originEntryService.getStatistics(errorGroup.getId());
        demergerReport.setErrorTransactionWritten(eOes.getRowCount());

        if (!collectorMode) {
            reportService.generateScrubberDemergerStatisticsReports(runDate, demergerReport);
        }
    }

    /**
     * Determine the type of the transaction by looking at attributes
     * 
     * @param transaction Transaction to identify
     * @return CE (Cost share encumbrance, O (Offset), C (apitalization), L (Liability), T (Transfer), CS (Cost Share), X (Other)
     */
    private String getTransactionType(OriginEntry transaction) {
        if (TRANSACTION_TYPE_COST_SHARE_ENCUMBRANCE.equals(transaction.getFinancialBalanceTypeCode())) {
            return TRANSACTION_TYPE_COST_SHARE_ENCUMBRANCE;
        }
        String desc = transaction.getTransactionLedgerEntryDescription();

        if (desc == null) {
            return TRANSACTION_TYPE_OTHER;
        }

        if (desc.startsWith(offsetDescription) && desc.indexOf(COST_SHARE_TRANSFER_ENTRY_IND) > -1) {
            return TRANSACTION_TYPE_COST_SHARE;
        }
        if (desc.startsWith(costShareDescription) && desc.indexOf(COST_SHARE_TRANSFER_ENTRY_IND) > -1) {
            return TRANSACTION_TYPE_COST_SHARE;
        }
        if (desc.startsWith(offsetDescription)) {
            return TRANSACTION_TYPE_OFFSET;
        }
        if (desc.startsWith(capitalizationDescription)) {
            return TRANSACTION_TYPE_CAPITALIZATION;
        }
        if (desc.startsWith(liabilityDescription)) {
            return TRANSACTION_TYPE_LIABILITY;
        }
        if (desc.startsWith(transferDescription)) {
            return TRANSACTION_TYPE_TRANSFER;
        }
        return TRANSACTION_TYPE_OTHER;
    }

    /**
     * This will process a group of origin entries. The COBOL code was refactored a lot to get this so there isn't a 1 to 1 section
     * of Cobol relating to this.
     * 
     * @param originEntryGroup Group to process
     */
    private void processGroup(OriginEntryGroup originEntryGroup) {
        this.referenceLookup.get().setLookupService(SpringContext.getBean(CachingLookup.class));

        OriginEntry lastEntry = null;
        scrubCostShareAmount = KualiDecimal.ZERO;
        unitOfWork = new UnitOfWorkInfo();

        LOG.info("Starting Scrubber Process process group...");
        Iterator entries = originEntryLiteService.getEntriesByGroup(originEntryGroup);
        while (entries.hasNext()) {
            OriginEntry unscrubbedEntry = (OriginEntry) entries.next();
            scrubberReport.incrementUnscrubbedRecordsRead();

            transactionErrors = new ArrayList<Message>();

            // This is done so if the code modifies this row, then saves it, it will be an insert,
            // and it won't touch the original. The Scrubber never modifies input rows/groups.
            unscrubbedEntry.setEntryGroupId(null);
            unscrubbedEntry.resetEntryId();

            boolean saveErrorTransaction = false;
            boolean saveValidTransaction = false;

            // Build a scrubbed entry
            OriginEntry scrubbedEntry = new OriginEntryLite();
            scrubbedEntry.setDocumentNumber(unscrubbedEntry.getDocumentNumber());
            scrubbedEntry.setOrganizationDocumentNumber(unscrubbedEntry.getOrganizationDocumentNumber());
            scrubbedEntry.setOrganizationReferenceId(unscrubbedEntry.getOrganizationReferenceId());
            scrubbedEntry.setReferenceFinancialDocumentNumber(unscrubbedEntry.getReferenceFinancialDocumentNumber());

            Integer transactionNumber = unscrubbedEntry.getTransactionLedgerEntrySequenceNumber();
            scrubbedEntry.setTransactionLedgerEntrySequenceNumber(null == transactionNumber ? new Integer(0) : transactionNumber);
            scrubbedEntry.setTransactionLedgerEntryDescription(unscrubbedEntry.getTransactionLedgerEntryDescription());
            scrubbedEntry.setTransactionLedgerEntryAmount(unscrubbedEntry.getTransactionLedgerEntryAmount());
            scrubbedEntry.setTransactionDebitCreditCode(unscrubbedEntry.getTransactionDebitCreditCode());

            // For Labor Scrubber
            boolean laborIndicator = false;

            List<Message> tmperrors = scrubberValidator.validateTransaction(unscrubbedEntry, scrubbedEntry, universityRunDate, laborIndicator);
            transactionErrors.addAll(tmperrors);

            // Expired account?
            Account unscrubbedEntryAccount = referenceLookup.get().getAccount(unscrubbedEntry);
            if ((unscrubbedEntryAccount != null) && (unscrubbedEntryAccount.isAccountClosedIndicator())) {
                // Make a copy of it so OJB doesn't just update the row in the original
                // group. It needs to make a new one in the expired group
                OriginEntryFull expiredEntry = OriginEntryFull.copyFromOriginEntryable(scrubbedEntry);

                createOutputEntry(expiredEntry, expiredGroup);
                scrubberReport.incrementExpiredAccountFound();
            }

            if (!isFatal(transactionErrors)) {
                saveValidTransaction = true;

                if (collectorMode) {
                    // only populate this map in collector mode because it's only needed for the collector

                    // the collector scrubber uses this map to apply the same changes made on an origin entry during scrubbing to
                    // the collector detail record
                    unscrubbedToUnscrubbedEntries.put(unscrubbedEntry, scrubbedEntry);

                    // for the collector, we don't need further processing, since we're going to rescrub all of the origin entries
                    // anyways during the nightly process
                }
                else {

                    // See if unit of work has changed
                    if (!unitOfWork.isSameUnitOfWork(scrubbedEntry)) {
                        // Generate offset for last unit of work
                        generateOffset(lastEntry);

                        unitOfWork = new UnitOfWorkInfo(scrubbedEntry);
                    }

                    KualiDecimal transactionAmount = scrubbedEntry.getTransactionLedgerEntryAmount();

                    ParameterEvaluator offsetFiscalPeriods = parameterService.getParameterEvaluator(ScrubberStep.class, GLConstants.GlScrubberGroupRules.OFFSET_FISCAL_PERIOD_CODES, scrubbedEntry.getUniversityFiscalPeriodCode());

                    BalanceTyp scrubbedEntryBalanceType = referenceLookup.get().getBalanceType(scrubbedEntry);
                    if (scrubbedEntryBalanceType.isFinancialOffsetGenerationIndicator() && offsetFiscalPeriods.evaluationSucceeds()) {
                        if (scrubbedEntry.isDebit()) {
                            unitOfWork.offsetAmount = unitOfWork.offsetAmount.add(transactionAmount);
                        }
                        else {
                            unitOfWork.offsetAmount = unitOfWork.offsetAmount.subtract(transactionAmount);
                        }
                    }

                    // The sub account type code will only exist if there is a valid sub account
                    String subAccountTypeCode = GLConstants.getSpaceSubAccountTypeCode();
                    // major assumption: the a21 subaccount is proxied, so we don't want to query the database if the subacct
                    // number is dashes
                    A21SubAccount scrubbedEntryA21SubAccount = referenceLookup.get().getA21SubAccount(scrubbedEntry);
                    if (!KFSConstants.getDashSubAccountNumber().equals(scrubbedEntry.getSubAccountNumber()) && scrubbedEntryA21SubAccount != null) {
                        subAccountTypeCode = scrubbedEntryA21SubAccount.getSubAccountTypeCode();

                    }

                    ParameterEvaluator costShareObjectTypeCodes = parameterService.getParameterEvaluator(ScrubberStep.class, GLConstants.GlScrubberGroupRules.COST_SHARE_OBJ_TYPE_CODES, scrubbedEntry.getFinancialObjectTypeCode());
                    ParameterEvaluator costShareEncBalanceTypeCodes = parameterService.getParameterEvaluator(ScrubberStep.class, GLConstants.GlScrubberGroupRules.COST_SHARE_ENC_BAL_TYP_CODES, scrubbedEntry.getFinancialBalanceTypeCode());
                    ParameterEvaluator costShareEncFiscalPeriodCodes = parameterService.getParameterEvaluator(ScrubberStep.class, GLConstants.GlScrubberGroupRules.COST_SHARE_ENC_FISCAL_PERIOD_CODES, scrubbedEntry.getUniversityFiscalPeriodCode());
                    ParameterEvaluator costShareEncDocTypeCodes = parameterService.getParameterEvaluator(ScrubberStep.class, GLConstants.GlScrubberGroupRules.COST_SHARE_ENC_DOC_TYPE_CODES, scrubbedEntry.getFinancialDocumentTypeCode().trim());
                    ParameterEvaluator costShareFiscalPeriodCodes = parameterService.getParameterEvaluator(ScrubberStep.class, GLConstants.GlScrubberGroupRules.COST_SHARE_FISCAL_PERIOD_CODES, scrubbedEntry.getUniversityFiscalPeriodCode());

                    Account scrubbedEntryAccount = referenceLookup.get().getAccount(scrubbedEntry);
                    if (costShareObjectTypeCodes.evaluationSucceeds() && costShareEncBalanceTypeCodes.evaluationSucceeds() && scrubbedEntryAccount.isForContractsAndGrants() && KFSConstants.COST_SHARE.equals(subAccountTypeCode) && costShareEncFiscalPeriodCodes.evaluationSucceeds() && costShareEncDocTypeCodes.evaluationSucceeds()) {
                        TransactionError te1 = generateCostShareEncumbranceEntries(scrubbedEntry);
                        if (te1 != null) {
                            List errors = new ArrayList();
                            errors.add(te1.message);
                            scrubberReportErrors.put(te1.transaction, errors);

                            saveValidTransaction = false;
                            saveErrorTransaction = true;
                        }
                    }

                    Options scrubbedEntryOption = referenceLookup.get().getOption(scrubbedEntry);
                    if (costShareObjectTypeCodes.evaluationSucceeds() && scrubbedEntryOption.getActualFinancialBalanceTypeCd().equals(scrubbedEntry.getFinancialBalanceTypeCode()) && scrubbedEntryAccount.isForContractsAndGrants() && KFSConstants.COST_SHARE.equals(subAccountTypeCode) && costShareFiscalPeriodCodes.evaluationSucceeds() && costShareEncDocTypeCodes.evaluationSucceeds()) {
                        if (scrubbedEntry.isDebit()) {
                            scrubCostShareAmount = scrubCostShareAmount.subtract(transactionAmount);
                        }
                        else {
                            scrubCostShareAmount = scrubCostShareAmount.add(transactionAmount);
                        }
                    }

                    ParameterEvaluator otherDocTypeCodes = parameterService.getParameterEvaluator(ScrubberStep.class, GLConstants.GlScrubberGroupRules.OFFSET_DOC_TYPE_CODES, scrubbedEntry.getFinancialDocumentTypeCode());

                    if (otherDocTypeCodes.evaluationSucceeds()) {
                        String m = processCapitalization(scrubbedEntry);
                        if (m != null) {
                            saveValidTransaction = false;
                            saveErrorTransaction = false;
                            addTransactionError(m, "", Message.TYPE_FATAL);
                        }

                        m = processLiabilities(scrubbedEntry);
                        if (m != null) {
                            saveValidTransaction = false;
                            saveErrorTransaction = false;
                            addTransactionError(m, "", Message.TYPE_FATAL);
                        }

                        m = processPlantIndebtedness(scrubbedEntry);
                        if (m != null) {
                            saveValidTransaction = false;
                            saveErrorTransaction = false;
                            addTransactionError(m, "", Message.TYPE_FATAL);
                        }
                    }

                    if (!scrubCostShareAmount.isZero()) {
                        TransactionError te = generateCostShareEntries(scrubbedEntry);

                        if (te != null) {
                            saveValidTransaction = false;
                            saveErrorTransaction = false;

                            // Make a copy of it so OJB doesn't just update the row in the original
                            // group. It needs to make a new one in the error group
                            OriginEntryFull errorEntry = new OriginEntryFull(te.transaction);
                            errorEntry.setTransactionScrubberOffsetGenerationIndicator(false);
                            createOutputEntry(errorEntry, errorGroup);
                            scrubberReport.incrementErrorRecordWritten();

                            List messages = new ArrayList();
                            messages.add(te.message);
                            scrubberReportErrors.put(errorEntry, messages);
                        }
                        scrubCostShareAmount = KualiDecimal.ZERO;
                    }

                    if (transactionErrors.size() > 0) {
                        scrubberReportErrors.put(OriginEntryFull.copyFromOriginEntryable(scrubbedEntry), transactionErrors);
                    }

                    lastEntry = scrubbedEntry;

                }
            }
            else {
                // Error transaction
                saveErrorTransaction = true;

                scrubberReportErrors.put(OriginEntryFull.copyFromOriginEntryable(unscrubbedEntry), transactionErrors);
            }

            if (saveValidTransaction) {
                scrubbedEntry.setTransactionScrubberOffsetGenerationIndicator(false);
                createOutputEntry(scrubbedEntry, validGroup);
                scrubberReport.incrementScrubbedRecordWritten();
            }

            if (saveErrorTransaction) {
                // Make a copy of it so OJB doesn't just update the row in the original
                // group. It needs to make a new one in the error group
                OriginEntryFull errorEntry = OriginEntryFull.copyFromOriginEntryable(unscrubbedEntry);
                errorEntry.setTransactionScrubberOffsetGenerationIndicator(false);
                createOutputEntry(errorEntry, errorGroup);
                scrubberReport.incrementErrorRecordWritten();
            }
        }

        if (!collectorMode) {
            // Generate last offset (if necessary)
            generateOffset(lastEntry);
        }

        this.referenceLookup.get().setLookupService(null);
    }

    /**
     * Determines if a given error is fatal and should stop this scrubber run
     * 
     * @param errors errors from a scrubber run
     * @return true if the run should be abended, false otherwise
     */
    private boolean isFatal(List<Message> errors) {
        for (Iterator<Message> iter = errors.iterator(); iter.hasNext();) {
            Message element = iter.next();
            if (element.getType() == Message.TYPE_FATAL) {
                return true;
            }
        }
        return false;
    }

    /**
     * Generates a cost share entry and offset for the given entry and saves both to the valid group
     * 
     * @param scrubbedEntry the originEntry that was scrubbed
     * @return a TransactionError initialized with any error encounted during entry generation, or (hopefully) null
     */
    private TransactionError generateCostShareEntries(OriginEntry scrubbedEntry) {
        // 3000-COST-SHARE to 3100-READ-OFSD in the cobol Generate Cost Share Entries
        LOG.debug("generateCostShareEntries() started");

        OriginEntryFull costShareEntry = OriginEntryFull.copyFromOriginEntryable(scrubbedEntry);

        Options scrubbedEntryOption = referenceLookup.get().getOption(scrubbedEntry);
        A21SubAccount scrubbedEntryA21SubAccount = referenceLookup.get().getA21SubAccount(scrubbedEntry);

        costShareEntry.setFinancialObjectCode(parameterService.getParameterValue(ScrubberStep.class, GLConstants.GlScrubberGroupParameters.COST_SHARE_OBJECT_CODE_PARM_NM));
        costShareEntry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        costShareEntry.setFinancialObjectTypeCode(scrubbedEntryOption.getFinancialObjectTypeTransferExpenseCd());
        costShareEntry.setTransactionLedgerEntrySequenceNumber(new Integer(0));

        StringBuffer description = new StringBuffer();
        description.append(costShareDescription);
        description.append(" ").append(scrubbedEntry.getAccountNumber());
        description.append(offsetString);
        costShareEntry.setTransactionLedgerEntryDescription(description.toString());

        costShareEntry.setTransactionLedgerEntryAmount(scrubCostShareAmount);
        if (scrubCostShareAmount.isPositive()) {
            costShareEntry.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
        }
        else {
            costShareEntry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
            costShareEntry.setTransactionLedgerEntryAmount(scrubCostShareAmount.negated());
        }

        costShareEntry.setTransactionDate(runDate);
        costShareEntry.setOrganizationDocumentNumber(null);
        costShareEntry.setProjectCode(KFSConstants.getDashProjectCode());
        costShareEntry.setOrganizationReferenceId(null);
        costShareEntry.setReferenceFinancialDocumentTypeCode(null);
        costShareEntry.setReferenceFinancialSystemOriginationCode(null);
        costShareEntry.setReferenceFinancialDocumentNumber(null);
        costShareEntry.setFinancialDocumentReversalDate(null);
        costShareEntry.setTransactionEncumbranceUpdateCode(null);

        createOutputEntry(costShareEntry, validGroup);
        scrubberReport.incrementCostShareEntryGenerated();

        OriginEntryFull costShareOffsetEntry = new OriginEntryFull(costShareEntry);
        costShareOffsetEntry.setTransactionLedgerEntryDescription(getOffsetMessage());

        OffsetDefinition offsetDefinition = offsetDefinitionService.getByPrimaryId(scrubbedEntry.getUniversityFiscalYear(), scrubbedEntry.getChartOfAccountsCode(), KFSConstants.TRANSFER_FUNDS, scrubbedEntry.getFinancialBalanceTypeCode());
        if (offsetDefinition != null) {
            if (offsetDefinition.getFinancialObject() == null) {
                StringBuffer objectCodeKey = new StringBuffer();
                objectCodeKey.append(offsetDefinition.getUniversityFiscalYear());
                objectCodeKey.append("-").append(offsetDefinition.getChartOfAccountsCode());
                objectCodeKey.append("-").append(offsetDefinition.getFinancialObjectCode());

                Message m = new Message(configurationService.getPropertyString(KFSKeyConstants.ERROR_OFFSET_DEFINITION_OBJECT_CODE_NOT_FOUND) + " (" + objectCodeKey.toString() + ")", Message.TYPE_FATAL);
                LOG.debug("generateCostShareEntries() Error 1 object not found");
                return new TransactionError(costShareEntry, m);
            }

            costShareOffsetEntry.setFinancialObjectCode(offsetDefinition.getFinancialObjectCode());
            costShareOffsetEntry.setFinancialObject(offsetDefinition.getFinancialObject());
            costShareOffsetEntry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());

        }
        else {
            Map<Transaction, List<Message>> errors = new HashMap<Transaction, List<Message>>();

            StringBuffer offsetKey = new StringBuffer("cost share transfer ");
            offsetKey.append(scrubbedEntry.getUniversityFiscalYear());
            offsetKey.append("-");
            offsetKey.append(scrubbedEntry.getChartOfAccountsCode());
            offsetKey.append("-TF-");
            offsetKey.append(scrubbedEntry.getFinancialBalanceTypeCode());

            Message m = new Message(configurationService.getPropertyString(KFSKeyConstants.ERROR_OFFSET_DEFINITION_NOT_FOUND) + " (" + offsetKey.toString() + ")", Message.TYPE_FATAL);

            LOG.debug("generateCostShareEntries() Error 2 offset not found");
            return new TransactionError(costShareEntry, m);
        }

        costShareOffsetEntry.setFinancialObjectTypeCode(offsetDefinition.getFinancialObject().getFinancialObjectTypeCode());

        if (costShareEntry.isCredit()) {
            costShareOffsetEntry.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
        }
        else {
            costShareOffsetEntry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
        }

        try {
            flexibleOffsetAccountService.updateOffset(costShareOffsetEntry);
        }
        catch (InvalidFlexibleOffsetException e) {
            Message m = new Message(e.getMessage(), Message.TYPE_FATAL);
            LOG.debug("generateCostShareEntries() Cost Share Transfer Flexible Offset Error: " + e.getMessage());
            return new TransactionError(costShareEntry, m);
        }

        createOutputEntry(costShareOffsetEntry, validGroup);
        scrubberReport.incrementCostShareEntryGenerated();

        OriginEntryFull costShareSourceAccountEntry = new OriginEntryFull(costShareEntry);

        description = new StringBuffer();
        description.append(costShareDescription);
        description.append(" ").append(scrubbedEntry.getAccountNumber());
        description.append(offsetString);
        costShareSourceAccountEntry.setTransactionLedgerEntryDescription(description.toString());

        costShareSourceAccountEntry.setChartOfAccountsCode(scrubbedEntryA21SubAccount.getCostShareChartOfAccountCode());
        costShareSourceAccountEntry.setAccountNumber(scrubbedEntryA21SubAccount.getCostShareSourceAccountNumber());

        setCostShareObjectCode(costShareSourceAccountEntry, scrubbedEntry);
        costShareSourceAccountEntry.setSubAccountNumber(scrubbedEntryA21SubAccount.getCostShareSourceSubAccountNumber());

        if (StringHelper.isNullOrEmpty(costShareSourceAccountEntry.getSubAccountNumber())) {
            costShareSourceAccountEntry.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
        }

        costShareSourceAccountEntry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        costShareSourceAccountEntry.setFinancialObjectTypeCode(scrubbedEntryOption.getFinancialObjectTypeTransferExpenseCd());
        costShareSourceAccountEntry.setTransactionLedgerEntrySequenceNumber(new Integer(0));

        costShareSourceAccountEntry.setTransactionLedgerEntryAmount(scrubCostShareAmount);
        if (scrubCostShareAmount.isPositive()) {
            costShareSourceAccountEntry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
        }
        else {
            costShareSourceAccountEntry.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
            costShareSourceAccountEntry.setTransactionLedgerEntryAmount(scrubCostShareAmount.negated());
        }

        costShareSourceAccountEntry.setTransactionDate(runDate);
        costShareSourceAccountEntry.setOrganizationDocumentNumber(null);
        costShareSourceAccountEntry.setProjectCode(KFSConstants.getDashProjectCode());
        costShareSourceAccountEntry.setOrganizationReferenceId(null);
        costShareSourceAccountEntry.setReferenceFinancialDocumentTypeCode(null);
        costShareSourceAccountEntry.setReferenceFinancialSystemOriginationCode(null);
        costShareSourceAccountEntry.setReferenceFinancialDocumentNumber(null);
        costShareSourceAccountEntry.setFinancialDocumentReversalDate(null);
        costShareSourceAccountEntry.setTransactionEncumbranceUpdateCode(null);

        createOutputEntry(costShareSourceAccountEntry, validGroup);
        scrubberReport.incrementCostShareEntryGenerated();

        OriginEntryFull costShareSourceAccountOffsetEntry = new OriginEntryFull(costShareSourceAccountEntry);
        costShareSourceAccountOffsetEntry.setTransactionLedgerEntryDescription(getOffsetMessage());

        // Lookup the new offset definition.
        offsetDefinition = offsetDefinitionService.getByPrimaryId(scrubbedEntry.getUniversityFiscalYear(), scrubbedEntry.getChartOfAccountsCode(), KFSConstants.TRANSFER_FUNDS, scrubbedEntry.getFinancialBalanceTypeCode());
        if (offsetDefinition != null) {
            if (offsetDefinition.getFinancialObject() == null) {
                Map<Transaction, List<Message>> errors = new HashMap<Transaction, List<Message>>();

                StringBuffer objectCodeKey = new StringBuffer();
                objectCodeKey.append(costShareEntry.getUniversityFiscalYear());
                objectCodeKey.append("-").append(scrubbedEntry.getChartOfAccountsCode());
                objectCodeKey.append("-").append(scrubbedEntry.getFinancialObjectCode());

                Message m = new Message(configurationService.getPropertyString(KFSKeyConstants.ERROR_OFFSET_DEFINITION_OBJECT_CODE_NOT_FOUND) + " (" + objectCodeKey.toString() + ")", Message.TYPE_FATAL);

                LOG.debug("generateCostShareEntries() Error 3 object not found");
                return new TransactionError(costShareSourceAccountEntry, m);
            }

            costShareSourceAccountOffsetEntry.setFinancialObjectCode(offsetDefinition.getFinancialObjectCode());
            costShareSourceAccountOffsetEntry.setFinancialObject(offsetDefinition.getFinancialObject());
            costShareSourceAccountOffsetEntry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        }
        else {
            Map<Transaction, List<Message>> errors = new HashMap<Transaction, List<Message>>();

            StringBuffer offsetKey = new StringBuffer("cost share transfer source ");
            offsetKey.append(scrubbedEntry.getUniversityFiscalYear());
            offsetKey.append("-");
            offsetKey.append(scrubbedEntry.getChartOfAccountsCode());
            offsetKey.append("-TF-");
            offsetKey.append(scrubbedEntry.getFinancialBalanceTypeCode());

            Message m = new Message(configurationService.getPropertyString(KFSKeyConstants.ERROR_OFFSET_DEFINITION_NOT_FOUND) + " (" + offsetKey.toString() + ")", Message.TYPE_FATAL);

            LOG.debug("generateCostShareEntries() Error 4 offset not found");
            return new TransactionError(costShareSourceAccountEntry, m);
        }

        costShareSourceAccountOffsetEntry.setFinancialObjectTypeCode(offsetDefinition.getFinancialObject().getFinancialObjectTypeCode());

        if (scrubbedEntry.isCredit()) {
            costShareSourceAccountOffsetEntry.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
        }
        else {
            costShareSourceAccountOffsetEntry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
        }

        try {
            flexibleOffsetAccountService.updateOffset(costShareSourceAccountOffsetEntry);
        }
        catch (InvalidFlexibleOffsetException e) {
            Message m = new Message(e.getMessage(), Message.TYPE_FATAL);
            LOG.debug("generateCostShareEntries() Cost Share Transfer Account Flexible Offset Error: " + e.getMessage());
            return new TransactionError(costShareEntry, m);
        }

        createOutputEntry(costShareSourceAccountOffsetEntry, validGroup);
        scrubberReport.incrementCostShareEntryGenerated();

        scrubCostShareAmount = KualiDecimal.ZERO;

        LOG.debug("generateCostShareEntries() successful");
        return null;
    }

    /**
     * Get all the transaction descriptions from the param table
     */
    private void setDescriptions() {
        offsetDescription = configurationService.getPropertyString(KFSKeyConstants.MSG_GENERATED_OFFSET);
        capitalizationDescription = configurationService.getPropertyString(KFSKeyConstants.MSG_GENERATED_CAPITALIZATION);
        liabilityDescription = configurationService.getPropertyString(KFSKeyConstants.MSG_GENERATED_LIABILITY);
        costShareDescription = configurationService.getPropertyString(KFSKeyConstants.MSG_GENERATED_COST_SHARE);
        transferDescription = configurationService.getPropertyString(KFSKeyConstants.MSG_GENERATED_TRANSFER);
    }

    /**
     * Generate the flag for the end of specific descriptions. This will be used in the demerger step
     */
    private void setOffsetString() {

        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(0);
        nf.setMaximumIntegerDigits(2);
        nf.setMinimumFractionDigits(0);
        nf.setMinimumIntegerDigits(2);

        offsetString = COST_SHARE_TRANSFER_ENTRY_IND + nf.format(runCal.get(Calendar.MONTH) + 1) + nf.format(runCal.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * Generate the offset message with the flag at the end
     * 
     * @return a generated offset message
     */
    private String getOffsetMessage() {
        String msg = offsetDescription + GLConstants.getSpaceTransactionLedgetEntryDescription();

        return msg.substring(0, OFFSET_MESSAGE_MAXLENGTH) + offsetString;
    }

    /**
     * Generates capitalization entries if necessary
     * 
     * @param scrubbedEntry the entry to generate capitalization entries (possibly) for
     * @return null if no error, message if error
     */
    private String processCapitalization(OriginEntry scrubbedEntry) {
        // Lines 4694 - 4798 of the Pro Cobol listing on Confluence
        if (!parameterService.getIndicatorParameter(ScrubberStep.class, GLConstants.GlScrubberGroupParameters.CAPITALIZATION_IND)) {
            return null;
        }

        OriginEntryFull capitalizationEntry = OriginEntryFull.copyFromOriginEntryable(scrubbedEntry);

        Options scrubbedEntryOption = referenceLookup.get().getOption(scrubbedEntry);
        ObjectCode scrubbedEntryObjectCode = referenceLookup.get().getFinancialObject(scrubbedEntry);
        Chart scrubbedEntryChart = referenceLookup.get().getChart(scrubbedEntry);
        Account scrubbedEntryAccount = referenceLookup.get().getAccount(scrubbedEntry);

        ParameterEvaluator documentTypeCodes = parameterService.getParameterEvaluator(ScrubberStep.class, GLConstants.GlScrubberGroupRules.CAPITALIZATION_DOC_TYPE_CODES, scrubbedEntry.getFinancialDocumentTypeCode());
        ParameterEvaluator fiscalPeriodCodes = parameterService.getParameterEvaluator(ScrubberStep.class, GLConstants.GlScrubberGroupRules.CAPITALIZATION_FISCAL_PERIOD_CODES, scrubbedEntry.getUniversityFiscalPeriodCode());
        ParameterEvaluator objectSubTypeCodes = parameterService.getParameterEvaluator(ScrubberStep.class, GLConstants.GlScrubberGroupRules.CAPITALIZATION_OBJ_SUB_TYPE_CODES, scrubbedEntryObjectCode.getFinancialObjectSubTypeCode());
        ParameterEvaluator subFundGroupCodes = parameterService.getParameterEvaluator(ScrubberStep.class, GLConstants.GlScrubberGroupRules.CAPITALIZATION_SUB_FUND_GROUP_CODES, scrubbedEntryAccount.getSubFundGroupCode());
        ParameterEvaluator chartCodes = parameterService.getParameterEvaluator(ScrubberStep.class, GLConstants.GlScrubberGroupRules.CAPITALIZATION_CHART_CODES, scrubbedEntry.getChartOfAccountsCode());

        if (scrubbedEntry.getFinancialBalanceTypeCode().equals(scrubbedEntryOption.getActualFinancialBalanceTypeCd()) && scrubbedEntry.getUniversityFiscalYear().intValue() > 1995 && documentTypeCodes.evaluationSucceeds() && fiscalPeriodCodes.evaluationSucceeds() && objectSubTypeCodes.evaluationSucceeds() && subFundGroupCodes.evaluationSucceeds() && chartCodes.evaluationSucceeds()) {

            String objectSubTypeCode = scrubbedEntryObjectCode.getFinancialObjectSubTypeCode();

            String capitalizationObjectCode = parameterService.getParameterValue(ScrubberStep.class, GLConstants.GlScrubberGroupParameters.CAPITALIZATION_SUBTYPE_OBJECT, objectSubTypeCode);
            if (capitalizationObjectCode != null) {
                capitalizationEntry.setFinancialObjectCode(capitalizationObjectCode);
                persistenceService.retrieveReferenceObject(capitalizationEntry, KFSPropertyConstants.FINANCIAL_OBJECT);
            }

            capitalizationEntry.setFinancialObjectTypeCode(scrubbedEntryOption.getFinancialObjectTypeAssetsCd());
            persistenceService.retrieveReferenceObject(capitalizationEntry, KFSPropertyConstants.OBJECT_TYPE);
            capitalizationEntry.setTransactionLedgerEntryDescription(capitalizationDescription);

            plantFundAccountLookup(scrubbedEntry, capitalizationEntry);

            capitalizationEntry.setUniversityFiscalPeriodCode(scrubbedEntry.getUniversityFiscalPeriodCode());

            createOutputEntry(capitalizationEntry, validGroup);
            scrubberReport.incrementCapitalizationEntryGenerated();

            // Clear out the id & the ojb version number to make sure we do an insert on the next one
            capitalizationEntry.setVersionNumber(null);
            capitalizationEntry.setEntryId(null);

            capitalizationEntry.setFinancialObjectCode(scrubbedEntryChart.getFundBalanceObjectCode());
            if (ObjectUtils.isNotNull(scrubbedEntryChart.getFundBalanceObject())) {
                capitalizationEntry.setFinancialObjectTypeCode(scrubbedEntryChart.getFundBalanceObject().getFinancialObjectTypeCode());
            }
            else {
                capitalizationEntry.setFinancialObjectTypeCode(scrubbedEntryOption.getFinObjectTypeFundBalanceCd());
            }

            if (scrubbedEntry.isDebit()) {
                capitalizationEntry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
            }
            else {
                capitalizationEntry.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
            }

            try {
                flexibleOffsetAccountService.updateOffset(capitalizationEntry);
            }
            catch (InvalidFlexibleOffsetException e) {
                LOG.debug("processCapitalization() Capitalization Flexible Offset Error: " + e.getMessage());
                return e.getMessage();
            }

            createOutputEntry(capitalizationEntry, validGroup);
            scrubberReport.incrementCapitalizationEntryGenerated();
        }
        return null;
    }

    /**
     * Generates the plant indebtedness entries
     * 
     * @param scrubbedEntry the entry to generated plant indebtedness entries for if necessary
     * @return null if no error, message if error
     */
    private String processPlantIndebtedness(OriginEntry scrubbedEntry) {
        // Lines 4855 - 4979 of the Pro Cobol listing on Confluence 
        if (!parameterService.getIndicatorParameter(ScrubberStep.class, GLConstants.GlScrubberGroupParameters.PLANT_INDEBTEDNESS_IND)) {
            return null;
        }

        OriginEntryFull plantIndebtednessEntry = OriginEntryFull.copyFromOriginEntryable(scrubbedEntry);

        Options scrubbedEntryOption = referenceLookup.get().getOption(scrubbedEntry);
        ObjectCode scrubbedEntryObjectCode = referenceLookup.get().getFinancialObject(scrubbedEntry);
        Account scrubbedEntryAccount = referenceLookup.get().getAccount(scrubbedEntry);
        Chart scrubbedEntryChart = referenceLookup.get().getChart(scrubbedEntry);

        ParameterEvaluator objectSubTypeCodes = parameterService.getParameterEvaluator(ScrubberStep.class, GLConstants.GlScrubberGroupRules.PLANT_INDEBTEDNESS_OBJ_SUB_TYPE_CODES, scrubbedEntryObjectCode.getFinancialObjectSubTypeCode());
        ParameterEvaluator subFundGroupCodes = parameterService.getParameterEvaluator(ScrubberStep.class, GLConstants.GlScrubberGroupRules.PLANT_INDEBTEDNESS_SUB_FUND_GROUP_CODES, scrubbedEntryAccount.getSubFundGroupCode());

        if (scrubbedEntry.getFinancialBalanceTypeCode().equals(scrubbedEntryOption.getActualFinancialBalanceTypeCd()) && subFundGroupCodes.evaluationSucceeds() && objectSubTypeCodes.evaluationSucceeds()) {

            plantIndebtednessEntry.setTransactionLedgerEntryDescription(KFSConstants.PLANT_INDEBTEDNESS_ENTRY_DESCRIPTION);

            if (scrubbedEntry.isDebit()) {
                plantIndebtednessEntry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
            }
            else {
                plantIndebtednessEntry.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
            }

            plantIndebtednessEntry.setTransactionScrubberOffsetGenerationIndicator(true);
            createOutputEntry(plantIndebtednessEntry, validGroup);
            scrubberReport.incrementPlantIndebtednessEntryGenerated();

            // Clear out the id & the ojb version number to make sure we do an insert on the next one
            plantIndebtednessEntry.setVersionNumber(null);
            plantIndebtednessEntry.setEntryId(null);

            plantIndebtednessEntry.setFinancialObjectCode(scrubbedEntryChart.getFundBalanceObjectCode());
            plantIndebtednessEntry.setFinancialObjectTypeCode(scrubbedEntryOption.getFinObjectTypeFundBalanceCd());
            plantIndebtednessEntry.setTransactionDebitCreditCode(scrubbedEntry.getTransactionDebitCreditCode());

            plantIndebtednessEntry.setTransactionScrubberOffsetGenerationIndicator(true);
            plantIndebtednessEntry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());

            try {
                flexibleOffsetAccountService.updateOffset(plantIndebtednessEntry);
            }
            catch (InvalidFlexibleOffsetException e) {
                LOG.error("processPlantIndebtedness() Flexible Offset Exception (1)", e);
                LOG.debug("processPlantIndebtedness() Plant Indebtedness Flexible Offset Error: " + e.getMessage());
                return e.getMessage();
            }

            createOutputEntry(plantIndebtednessEntry, validGroup);
            scrubberReport.incrementPlantIndebtednessEntryGenerated();

            // Clear out the id & the ojb version number to make sure we do an insert on the next one
            plantIndebtednessEntry.setVersionNumber(null);
            plantIndebtednessEntry.setEntryId(null);

            plantIndebtednessEntry.setFinancialObjectCode(scrubbedEntry.getFinancialObjectCode());
            plantIndebtednessEntry.setFinancialObjectTypeCode(scrubbedEntry.getFinancialObjectTypeCode());
            plantIndebtednessEntry.setTransactionDebitCreditCode(scrubbedEntry.getTransactionDebitCreditCode());

            plantIndebtednessEntry.setTransactionLedgerEntryDescription(scrubbedEntry.getTransactionLedgerEntryDescription());

            plantIndebtednessEntry.setAccountNumber(scrubbedEntry.getAccountNumber());
            plantIndebtednessEntry.setSubAccountNumber(scrubbedEntry.getSubAccountNumber());

            if (scrubbedEntry.getChartOfAccountsCode().equals(scrubbedEntryAccount.getOrganization().getChartOfAccountsCode()) && scrubbedEntryAccount.getOrganizationCode().equals(scrubbedEntryAccount.getOrganizationCode()) && scrubbedEntry.getAccountNumber().equals(scrubbedEntryAccount.getAccountNumber()) && scrubbedEntry.getChartOfAccountsCode().equals(scrubbedEntryAccount.getChartOfAccountsCode())) {
                plantIndebtednessEntry.setAccountNumber(scrubbedEntryAccount.getOrganization().getCampusPlantAccountNumber());
                plantIndebtednessEntry.setChartOfAccountsCode(scrubbedEntryAccount.getOrganization().getCampusPlantChartCode());
            }

            plantIndebtednessEntry.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
            plantIndebtednessEntry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());

            StringBuffer litGenPlantXferFrom = new StringBuffer();
            litGenPlantXferFrom.append(transferDescription + " ");
            litGenPlantXferFrom.append(scrubbedEntry.getChartOfAccountsCode()).append(" ");
            litGenPlantXferFrom.append(scrubbedEntry.getAccountNumber());
            plantIndebtednessEntry.setTransactionLedgerEntryDescription(litGenPlantXferFrom.toString());

            createOutputEntry(plantIndebtednessEntry, validGroup);
            scrubberReport.incrementPlantIndebtednessEntryGenerated();

            // Clear out the id & the ojb version number to make sure we do an insert on the next one
            plantIndebtednessEntry.setVersionNumber(null);
            plantIndebtednessEntry.setEntryId(null);

            plantIndebtednessEntry.setFinancialObjectCode(scrubbedEntryChart.getFundBalanceObjectCode());
            plantIndebtednessEntry.setFinancialObjectTypeCode(scrubbedEntryOption.getFinObjectTypeFundBalanceCd());
            plantIndebtednessEntry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());

            if (scrubbedEntry.isDebit()) {
                plantIndebtednessEntry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
            }
            else {
                plantIndebtednessEntry.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
            }

            try {
                flexibleOffsetAccountService.updateOffset(plantIndebtednessEntry);
            }
            catch (InvalidFlexibleOffsetException e) {
                LOG.error("processPlantIndebtedness() Flexible Offset Exception (2)", e);
                LOG.debug("processPlantIndebtedness() Plant Indebtedness Flexible Offset Error: " + e.getMessage());
                return e.getMessage();
            }

            createOutputEntry(plantIndebtednessEntry, validGroup);
            scrubberReport.incrementPlantIndebtednessEntryGenerated();
        }

        return null;
    }

    /**
     * Generate the liability entries for the entry if necessary
     * 
     * @param scrubbedEntry the entry to generate liability entries for if necessary
     * @return null if no error, message if error
     */
    private String processLiabilities(OriginEntry scrubbedEntry) {
        // Lines 4799 to 4839 of the Pro Cobol list of the scrubber on Confluence
        if (!parameterService.getIndicatorParameter(ScrubberStep.class, GLConstants.GlScrubberGroupParameters.LIABILITY_IND)) {
            return null;
        }

        OriginEntryFull liabilityEntry = OriginEntryFull.copyFromOriginEntryable(scrubbedEntry);

        Chart scrubbedEntryChart = referenceLookup.get().getChart(scrubbedEntry);
        Options scrubbedEntryOption = referenceLookup.get().getOption(scrubbedEntry);
        ObjectCode scrubbedEntryFinancialObject = referenceLookup.get().getFinancialObject(scrubbedEntry);
        Account scrubbedEntryAccount = referenceLookup.get().getAccount(scrubbedEntry);

        ParameterEvaluator chartCodes = parameterService.getParameterEvaluator(ScrubberStep.class, GLConstants.GlScrubberGroupRules.LIABILITY_CHART_CODES, scrubbedEntry.getChartOfAccountsCode());
        ParameterEvaluator docTypeCodes = parameterService.getParameterEvaluator(ScrubberStep.class, GLConstants.GlScrubberGroupRules.LIABILITY_DOC_TYPE_CODES, scrubbedEntry.getFinancialDocumentTypeCode());
        ParameterEvaluator fiscalPeriods = parameterService.getParameterEvaluator(ScrubberStep.class, GLConstants.GlScrubberGroupRules.LIABILITY_FISCAL_PERIOD_CODES, scrubbedEntry.getUniversityFiscalPeriodCode());
        ParameterEvaluator objSubTypeCodes = parameterService.getParameterEvaluator(ScrubberStep.class, GLConstants.GlScrubberGroupRules.LIABILITY_OBJ_SUB_TYPE_CODES, scrubbedEntryFinancialObject.getFinancialObjectSubTypeCode());
        ParameterEvaluator subFundGroupCodes = parameterService.getParameterEvaluator(ScrubberStep.class, GLConstants.GlScrubberGroupRules.LIABILITY_SUB_FUND_GROUP_CODES, scrubbedEntryAccount.getSubFundGroupCode());

        if (scrubbedEntry.getFinancialBalanceTypeCode().equals(scrubbedEntryOption.getActualFinancialBalanceTypeCd()) && scrubbedEntry.getUniversityFiscalYear().intValue() > 1995 && docTypeCodes.evaluationSucceeds() && fiscalPeriods.evaluationSucceeds() && objSubTypeCodes.evaluationSucceeds() && subFundGroupCodes.evaluationSucceeds() && chartCodes.evaluationSucceeds()) {

            liabilityEntry.setFinancialObjectCode(parameterService.getParameterValue(ScrubberStep.class, GLConstants.GlScrubberGroupParameters.LIABILITY_OBJECT_CODE));
            liabilityEntry.setFinancialObjectTypeCode(scrubbedEntryOption.getFinObjectTypeLiabilitiesCode());

            liabilityEntry.setTransactionDebitCreditCode(scrubbedEntry.getTransactionDebitCreditCode());
            liabilityEntry.setTransactionLedgerEntryDescription(liabilityDescription);
            plantFundAccountLookup(scrubbedEntry, liabilityEntry);

            createOutputEntry(liabilityEntry, validGroup);
            scrubberReport.incrementLiabilityEntryGenerated();

            // Clear out the id & the ojb version number to make sure we do an insert on the next one
            liabilityEntry.setVersionNumber(null);
            liabilityEntry.setEntryId(null);

            // ... and now generate the offset half of the liability entry
            liabilityEntry.setFinancialObjectCode(scrubbedEntryChart.getFundBalanceObjectCode());
            if (ObjectUtils.isNotNull(scrubbedEntryChart.getFundBalanceObject())) {
                liabilityEntry.setFinancialObjectTypeCode(scrubbedEntryChart.getFundBalanceObject().getFinancialObjectTypeCode());
            }
            else {
                liabilityEntry.setFinancialObjectTypeCode(scrubbedEntryOption.getFinObjectTypeFundBalanceCd());
            }

            if (liabilityEntry.isDebit()) {
                liabilityEntry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
            }
            else {
                liabilityEntry.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
            }

            try {
                flexibleOffsetAccountService.updateOffset(liabilityEntry);
            }
            catch (InvalidFlexibleOffsetException e) {
                LOG.debug("processLiabilities() Liability Flexible Offset Error: " + e.getMessage());
                return e.getMessage();
            }

            createOutputEntry(liabilityEntry, validGroup);
            scrubberReport.incrementLiabilityEntryGenerated();
        }
        return null;
    }

    /**
     * Updates the entries with the proper chart and account for the plant fund
     * 
     * @param scrubbedEntry basis for plant fund entry
     * @param liabilityEntry liability entry
     */
    private void plantFundAccountLookup(OriginEntry scrubbedEntry, OriginEntryFull liabilityEntry) {
        // 4000-PLANT-FUND-ACCT to 4000-PLANT-FUND-ACCT-EXIT in cobol
        
        liabilityEntry.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
        persistenceService.retrieveReferenceObject(liabilityEntry, KFSPropertyConstants.ACCOUNT);

        ObjectCode scrubbedEntryObjectCode = referenceLookup.get().getFinancialObject(scrubbedEntry);
        Account scrubbedEntryAccount = referenceLookup.get().getAccount(scrubbedEntry);

        if (liabilityEntry.getChartOfAccountsCode().equals(liabilityEntry.getAccount().getOrganization().getChartOfAccountsCode()) && scrubbedEntryAccount.getOrganizationCode().equals(liabilityEntry.getAccount().getOrganization().getOrganizationCode()) && liabilityEntry.getAccountNumber().equals(liabilityEntry.getAccount().getAccountNumber()) && liabilityEntry.getChartOfAccountsCode().equals(liabilityEntry.getAccount().getChartOfAccountsCode())) {
            persistenceService.retrieveReferenceObject(liabilityEntry, KFSPropertyConstants.FINANCIAL_OBJECT);
            persistenceService.retrieveReferenceObject(liabilityEntry.getAccount(), KFSPropertyConstants.ORGANIZATION);

            String objectSubTypeCode = scrubbedEntryObjectCode.getFinancialObjectSubTypeCode();
            ParameterEvaluator campusObjSubTypeCodes = parameterService.getParameterEvaluator(ScrubberStep.class, GLConstants.GlScrubberGroupRules.PLANT_FUND_CAMPUS_OBJECT_SUB_TYPE_CODES, objectSubTypeCode);
            ParameterEvaluator orgObjSubTypeCodes = parameterService.getParameterEvaluator(ScrubberStep.class, GLConstants.GlScrubberGroupRules.PLANT_FUND_ORG_OBJECT_SUB_TYPE_CODES, objectSubTypeCode);

            if (campusObjSubTypeCodes.evaluationSucceeds()) {
                liabilityEntry.setAccountNumber(scrubbedEntryAccount.getOrganization().getCampusPlantAccountNumber());
                liabilityEntry.setChartOfAccountsCode(scrubbedEntryAccount.getOrganization().getCampusPlantChartCode());

                persistenceService.retrieveReferenceObject(liabilityEntry, KFSPropertyConstants.ACCOUNT);
                persistenceService.retrieveReferenceObject(liabilityEntry, KFSPropertyConstants.CHART);
            }
            else if (orgObjSubTypeCodes.evaluationSucceeds()) {
                liabilityEntry.setAccountNumber(scrubbedEntryAccount.getOrganization().getOrganizationPlantAccountNumber());
                liabilityEntry.setChartOfAccountsCode(scrubbedEntryAccount.getOrganization().getOrganizationPlantChartCode());

                persistenceService.retrieveReferenceObject(liabilityEntry, KFSPropertyConstants.ACCOUNT);
                persistenceService.retrieveReferenceObject(liabilityEntry, KFSPropertyConstants.CHART);
            }
        }
    }

    /**
     * The purpose of this method is to generate a "Cost Share Encumbrance"
     * transaction for the current transaction and its offset. The cost share chart and account for current transaction are obtained
     * from the CA_A21_SUB_ACCT_T table. This method calls the method SET-OBJECT-2004 to get the Cost Share Object Code. It then
     * writes out the cost share transaction. Next it read the GL_OFFSET_DEFN_T table for the offset object code that corresponds to
     * the cost share object code. In addition to the object code it needs to get subobject code. It then reads the CA_OBJECT_CODE_T
     * table to make sure the offset object code found in the GL_OFFSET_DEFN_T is valid and to get the object type code associated
     * with this object code. It writes out the offset transaction and returns.
     * 
     * @param scrubbedEntry the entry to perhaps create a cost share encumbrance for 
     * @return a message if there was an error encountered generating the entries, or (hopefully) null if no errors were encountered
     */
    private TransactionError generateCostShareEncumbranceEntries(OriginEntry scrubbedEntry) {
        // 3200-COST-SHARE-ENC to 3200-CSE-EXIT in the COBOL
        LOG.debug("generateCostShareEncumbranceEntries() started");

        OriginEntryFull costShareEncumbranceEntry = OriginEntryFull.copyFromOriginEntryable(scrubbedEntry);

        // First 28 characters of the description, padding to 28 if shorter)
        StringBuffer buffer = new StringBuffer((scrubbedEntry.getTransactionLedgerEntryDescription() + GLConstants.getSpaceTransactionLedgetEntryDescription()).substring(0, COST_SHARE_ENCUMBRANCE_ENTRY_MAXLENGTH));

        buffer.append("FR-");
        buffer.append(costShareEncumbranceEntry.getChartOfAccountsCode());
        buffer.append(costShareEncumbranceEntry.getAccountNumber());

        costShareEncumbranceEntry.setTransactionLedgerEntryDescription(buffer.toString());

        A21SubAccount scrubbedEntryA21SubAccount = referenceLookup.get().getA21SubAccount(scrubbedEntry);
        Options scrubbedEntryOption = referenceLookup.get().getOption(scrubbedEntry);

        costShareEncumbranceEntry.setChartOfAccountsCode(scrubbedEntryA21SubAccount.getCostShareChartOfAccountCode());
        costShareEncumbranceEntry.setAccountNumber(scrubbedEntryA21SubAccount.getCostShareSourceAccountNumber());
        costShareEncumbranceEntry.setSubAccountNumber(scrubbedEntryA21SubAccount.getCostShareSourceSubAccountNumber());

        if (!StringUtils.hasText(costShareEncumbranceEntry.getSubAccountNumber())) {
            costShareEncumbranceEntry.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
        }

        costShareEncumbranceEntry.setFinancialBalanceTypeCode(scrubbedEntryOption.getCostShareEncumbranceBalanceTypeCd());
        setCostShareObjectCode(costShareEncumbranceEntry, scrubbedEntry);
        costShareEncumbranceEntry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        costShareEncumbranceEntry.setTransactionLedgerEntrySequenceNumber(new Integer(0));

        if (!StringUtils.hasText(scrubbedEntry.getTransactionDebitCreditCode())) {
            if (scrubbedEntry.getTransactionLedgerEntryAmount().isPositive()) {
                costShareEncumbranceEntry.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
            }
            else {
                costShareEncumbranceEntry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
                costShareEncumbranceEntry.setTransactionLedgerEntryAmount(scrubbedEntry.getTransactionLedgerEntryAmount().negated());
            }
        }

        costShareEncumbranceEntry.setTransactionDate(runDate);

        costShareEncumbranceEntry.setTransactionScrubberOffsetGenerationIndicator(true);
        createOutputEntry(costShareEncumbranceEntry, validGroup);
        scrubberReport.incrementCostShareEncumbranceGenerated();

        OriginEntryFull costShareEncumbranceOffsetEntry = new OriginEntryFull(costShareEncumbranceEntry);

        costShareEncumbranceOffsetEntry.setTransactionLedgerEntryDescription(offsetDescription);

        OffsetDefinition offset = offsetDefinitionService.getByPrimaryId(costShareEncumbranceEntry.getUniversityFiscalYear(), costShareEncumbranceEntry.getChartOfAccountsCode(), costShareEncumbranceEntry.getFinancialDocumentTypeCode(), costShareEncumbranceEntry.getFinancialBalanceTypeCode());

        if (offset != null) {
            if (offset.getFinancialObject() == null) {
                StringBuffer offsetKey = new StringBuffer();
                offsetKey.append(offset.getUniversityFiscalYear());
                offsetKey.append("-");
                offsetKey.append(offset.getChartOfAccountsCode());
                offsetKey.append("-");
                offsetKey.append(offset.getFinancialObjectCode());

                LOG.debug("generateCostShareEncumbranceEntries() object code not found");
                return new TransactionError(costShareEncumbranceEntry, new Message(configurationService.getPropertyString(KFSKeyConstants.ERROR_NO_OBJECT_FOR_OBJECT_ON_OFSD) + "(" + offsetKey.toString() + ")", Message.TYPE_FATAL));
            }
            costShareEncumbranceOffsetEntry.setFinancialObjectCode(offset.getFinancialObjectCode());
            costShareEncumbranceOffsetEntry.setFinancialObject(offset.getFinancialObject());
            costShareEncumbranceOffsetEntry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        }
        else {
            StringBuffer offsetKey = new StringBuffer("Cost share encumbrance ");
            offsetKey.append(costShareEncumbranceEntry.getUniversityFiscalYear());
            offsetKey.append("-");
            offsetKey.append(costShareEncumbranceEntry.getChartOfAccountsCode());
            offsetKey.append("-");
            offsetKey.append(costShareEncumbranceEntry.getFinancialDocumentTypeCode());
            offsetKey.append("-");
            offsetKey.append(costShareEncumbranceEntry.getFinancialBalanceTypeCode());

            LOG.debug("generateCostShareEncumbranceEntries() offset not found");
            return new TransactionError(costShareEncumbranceEntry, new Message(configurationService.getPropertyString(KFSKeyConstants.ERROR_OFFSET_DEFINITION_NOT_FOUND) + "(" + offsetKey.toString() + ")", Message.TYPE_FATAL));
        }

        costShareEncumbranceOffsetEntry.setFinancialObjectTypeCode(offset.getFinancialObject().getFinancialObjectTypeCode());

        if (costShareEncumbranceEntry.isCredit()) {
            costShareEncumbranceOffsetEntry.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
        }
        else {
            costShareEncumbranceOffsetEntry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
        }

        costShareEncumbranceOffsetEntry.setTransactionDate(runDate);
        costShareEncumbranceOffsetEntry.setOrganizationDocumentNumber(null);
        costShareEncumbranceOffsetEntry.setProjectCode(KFSConstants.getDashProjectCode());
        costShareEncumbranceOffsetEntry.setOrganizationReferenceId(null);
        costShareEncumbranceOffsetEntry.setReferenceFinancialDocumentTypeCode(null);
        costShareEncumbranceOffsetEntry.setReferenceFinancialSystemOriginationCode(null);
        costShareEncumbranceOffsetEntry.setReferenceFinancialDocumentNumber(null);
        costShareEncumbranceOffsetEntry.setReversalDate(null);
        costShareEncumbranceOffsetEntry.setTransactionEncumbranceUpdateCode(null);

        costShareEncumbranceOffsetEntry.setTransactionScrubberOffsetGenerationIndicator(true);

        try {
            flexibleOffsetAccountService.updateOffset(costShareEncumbranceOffsetEntry);
        }
        catch (InvalidFlexibleOffsetException e) {
            Message m = new Message(e.getMessage(), Message.TYPE_FATAL);
            LOG.debug("generateCostShareEncumbranceEntries() Cost Share Encumbrance Flexible Offset Error: " + e.getMessage());
            return new TransactionError(costShareEncumbranceOffsetEntry, m);
        }

        createOutputEntry(costShareEncumbranceOffsetEntry, validGroup);
        scrubberReport.incrementCostShareEncumbranceGenerated();

        LOG.debug("generateCostShareEncumbranceEntries() returned successfully");
        return null;
    }

    /**
     * Sets the proper cost share object code in an entry and its offset
     * 
     * @param costShareEntry GL Entry for cost share
     * @param originEntry Scrubbed GL Entry that this is based on
     */
    private void setCostShareObjectCode(OriginEntryFull costShareEntry, OriginEntry originEntry) {
        // This code is SET-OBJECT-2004 to 2520-INIT-SCRB-AREA in the Cobol
        ObjectCode originEntryFinancialObject = referenceLookup.get().getFinancialObject(originEntry);

        if (originEntryFinancialObject == null) {
            addTransactionError(configurationService.getPropertyString(KFSKeyConstants.ERROR_OBJECT_CODE_NOT_FOUND), originEntry.getFinancialObjectCode(), Message.TYPE_FATAL);
        }

        String originEntryObjectLevelCode = (originEntryFinancialObject == null) ? "" : originEntryFinancialObject.getFinancialObjectLevelCode();

        String financialOriginEntryObjectCode = originEntry.getFinancialObjectCode();
        String originEntryObjectCode = scrubberProcessObjectCodeOverride.getOriginEntryObjectCode(originEntryObjectLevelCode, financialOriginEntryObjectCode);

        // General rules
        if (originEntryObjectCode.equals(financialOriginEntryObjectCode)) {
            String param = parameterService.getParameterValue(ScrubberStep.class, GLConstants.GlScrubberGroupParameters.COST_SHARE_OBJECT_CODE_BY_LEVEL_PARM_NM, originEntryObjectLevelCode);
            if (param == null) {
                param = parameterService.getParameterValue(ScrubberStep.class, GLConstants.GlScrubberGroupParameters.COST_SHARE_OBJECT_CODE_BY_LEVEL_PARM_NM, "DEFAULT");
                if (param == null) {
                    throw new RuntimeException("Unable to determine cost sharing object code from object level.  Default entry missing.");
                }
            }
            originEntryObjectCode = param;
        }

        // Lookup the new object code
        ObjectCode objectCode = objectCodeService.getByPrimaryId(costShareEntry.getUniversityFiscalYear(), costShareEntry.getChartOfAccountsCode(), originEntryObjectCode);
        if (objectCode != null) {
            costShareEntry.setFinancialObjectTypeCode(objectCode.getFinancialObjectTypeCode());
            costShareEntry.setFinancialObjectCode(originEntryObjectCode);
        }
        else {
            addTransactionError(configurationService.getPropertyString(KFSKeyConstants.ERROR_COST_SHARE_OBJECT_NOT_FOUND), costShareEntry.getFinancialObjectCode(), Message.TYPE_FATAL);
        }
    }

    /**
     * The purpose of this method is to build the actual offset transaction. It does this by performing the following steps: 1.
     * Getting the offset object code and offset subobject code from the GL Offset Definition Table. 2. For the offset object code
     * it needs to get the associated object type, object subtype, and object active code. 
     * 
     * @param scrubbedEntry entry to determine if an offset is needed for
     * @return true if an offset would be needed for this entry, false otherwise
     */
    private boolean generateOffset(OriginEntry scrubbedEntry) {
        // This code is 3000-OFFSET to SET-OBJECT-2004 in the Cobol
        LOG.debug("generateOffset() started");

        // There was no previous unit of work so we need no offset
        if (scrubbedEntry == null) {
            return true;
        }

        // If the offset amount is zero, don't bother to lookup the offset definition ...
        if (unitOfWork.offsetAmount.isZero()) {
            return true;
        }

        ParameterEvaluator docTypeRule = parameterService.getParameterEvaluator(ScrubberStep.class, GLConstants.GlScrubberGroupRules.OFFSET_DOC_TYPE_CODES, scrubbedEntry.getFinancialDocumentTypeCode());
        if (!docTypeRule.evaluationSucceeds()) {
            return true;
        }

        // do nothing if flexible offset is enabled and scrubber offset indicator of the document
        // type code is turned off in the document type table
        String documentTypeCode = scrubbedEntry.getFinancialDocumentTypeCode();
        DocumentType documentType = documentTypeService.getDocumentTypeByCode(documentTypeCode);
        if ((!documentType.isTransactionScrubberOffsetGenerationIndicator()) && flexibleOffsetAccountService.getEnabled()) {
            return true;
        }

        // Create an offset
        OriginEntryFull offsetEntry = OriginEntryFull.copyFromOriginEntryable(scrubbedEntry);
        offsetEntry.setTransactionLedgerEntryDescription(offsetDescription);

        OffsetDefinition offsetDefinition = offsetDefinitionService.getByPrimaryId(scrubbedEntry.getUniversityFiscalYear(), scrubbedEntry.getChartOfAccountsCode(), scrubbedEntry.getFinancialDocumentTypeCode(), scrubbedEntry.getFinancialBalanceTypeCode());
        if (offsetDefinition != null) {
            if (offsetDefinition.getFinancialObject() == null) {
                StringBuffer offsetKey = new StringBuffer(offsetDefinition.getUniversityFiscalYear());
                offsetKey.append("-");
                offsetKey.append(offsetDefinition.getChartOfAccountsCode());
                offsetKey.append("-");
                offsetKey.append(offsetDefinition.getFinancialObjectCode());

                putTransactionError(offsetEntry, configurationService.getPropertyString(KFSKeyConstants.ERROR_OFFSET_DEFINITION_OBJECT_CODE_NOT_FOUND), offsetKey.toString(), Message.TYPE_FATAL);

                createOutputEntry(offsetEntry, errorGroup);
                scrubberReport.incrementErrorRecordWritten();
                return false;
            }

            offsetEntry.setFinancialObject(offsetDefinition.getFinancialObject());
            offsetEntry.setFinancialObjectCode(offsetDefinition.getFinancialObjectCode());

            offsetEntry.setFinancialSubObject(null);
            offsetEntry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        }
        else {
            StringBuffer sb = new StringBuffer("Unit of work offset ");
            sb.append(scrubbedEntry.getUniversityFiscalYear());
            sb.append("-");
            sb.append(scrubbedEntry.getChartOfAccountsCode());
            sb.append("-");
            sb.append(scrubbedEntry.getFinancialDocumentTypeCode());
            sb.append("-");
            sb.append(scrubbedEntry.getFinancialBalanceTypeCode());

            putTransactionError(offsetEntry, configurationService.getPropertyString(KFSKeyConstants.ERROR_OFFSET_DEFINITION_NOT_FOUND), sb.toString(), Message.TYPE_FATAL);

            createOutputEntry(offsetEntry, errorGroup);
            scrubberReport.incrementErrorRecordWritten();
            return false;
        }

        offsetEntry.setFinancialObjectTypeCode(offsetEntry.getFinancialObject().getFinancialObjectTypeCode());
        offsetEntry.setTransactionLedgerEntryAmount(unitOfWork.offsetAmount);

        if (unitOfWork.offsetAmount.isPositive()) {
            offsetEntry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
        }
        else {
            offsetEntry.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
            offsetEntry.setTransactionLedgerEntryAmount(unitOfWork.offsetAmount.negated());
        }

        offsetEntry.setOrganizationDocumentNumber(null);
        offsetEntry.setOrganizationReferenceId(null);
        offsetEntry.setReferenceFinancialDocumentTypeCode(null);
        offsetEntry.setReferenceDocumentType(null);
        offsetEntry.setReferenceFinancialSystemOriginationCode(null);
        offsetEntry.setReferenceFinancialDocumentNumber(null);
        offsetEntry.setTransactionEncumbranceUpdateCode(null);
        offsetEntry.setProjectCode(KFSConstants.getDashProjectCode());
        offsetEntry.setTransactionDate(runDate);

        try {
            flexibleOffsetAccountService.updateOffset(offsetEntry);
        }
        catch (InvalidFlexibleOffsetException e) {
            LOG.debug("generateOffset() Offset Flexible Offset Error: " + e.getMessage());
            putTransactionError(offsetEntry, e.getMessage(), "", Message.TYPE_FATAL);
            return true;
        }

        createOutputEntry(offsetEntry, validGroup);
        scrubberReport.incrementOffsetEntryGenerated();
        return true;
    }

    /**
     * Save an entry in origin entry
     * 
     * @param entry Entry to save
     * @param group Group to save it in
     */
    private void createOutputEntry(OriginEntry entry, OriginEntryGroup group) {
        // Write the entry if we aren't running in report only or collector mode.
        if (reportOnlyMode || collectorMode) {
            // If the group is null don't write it because the error and expired groups aren't created in reportOnlyMode
            if (group != null) {
                entry.setEntryGroupId(group.getId());
                originEntryLiteService.save((OriginEntryLite) entry);
            }
        }
        else {
            entry.setEntryGroupId(group.getId());
            originEntryLiteService.save((OriginEntryLite) entry);
        }
    }

    /**
     * If object is null, generate an error
     * 
     * @param glObject object to test
     * @param errorMessage error message if glObject is null
     * @param errorValue value of glObject to print in the error message
     * @param type Type of message (fatal or warning)
     * @return true of glObject is null
     */
    private boolean ifNullAddTransactionErrorAndReturnFalse(Object glObject, String errorMessage, String errorValue, int type) {
        if (glObject == null) {
            if (StringUtils.hasText(errorMessage)) {
                addTransactionError(errorMessage, errorValue, type);
            }
            else {
                addTransactionError("Unexpected null object", glObject.getClass().getName(), type);
            }
            return false;
        }
        return true;
    }

    /**
     * Add an error message to the list of messages for this transaction
     * 
     * @param errorMessage Error message
     * @param errorValue Value that is in error
     * @param type Type of error (Fatal or Warning)
     */
    private void addTransactionError(String errorMessage, String errorValue, int type) {
        transactionErrors.add(new Message(errorMessage + " (" + errorValue + ")", type));
    }

    /**
     * Puts a transaction error into this instance's collection of errors
     * 
     * @param s a transaction that caused a scrubber error
     * @param errorMessage the message of what caused the error
     * @param errorValue the value in error
     * @param type the type of error
     */
    private void putTransactionError(Transaction s, String errorMessage, String errorValue, int type) {
        List te = new ArrayList();
        te.add(new Message(errorMessage + "(" + errorValue + ")", type));
        scrubberReportErrors.put(s, te);
    }

    /**
     * A class to hold the current unit of work the scrubber is using
     */
    class UnitOfWorkInfo {
        // Unit of work key
        public Integer univFiscalYr = 0;
        public String finCoaCd = "";
        public String accountNbr = "";
        public String subAcctNbr = "";
        public String finBalanceTypCd = "";
        public String fdocTypCd = "";
        public String fsOriginCd = "";
        public String fdocNbr = "";
        public Date fdocReversalDt = new Date(dateTimeService.getCurrentDate().getTime());
        public String univFiscalPrdCd = "";

        // Data about unit of work
        public boolean entryMode = true;
        public KualiDecimal offsetAmount = KualiDecimal.ZERO;
        public String scrbFinCoaCd;
        public String scrbAccountNbr;

        /**
         * Constructs a ScrubberProcess.UnitOfWorkInfo instance
         */
        public UnitOfWorkInfo() {
        }

        /**
         * Constructs a ScrubberProcess.UnitOfWorkInfo instance
         * @param e an origin entry belonging to this unit of work
         */
        public UnitOfWorkInfo(OriginEntry e) {
            univFiscalYr = e.getUniversityFiscalYear();
            finCoaCd = e.getChartOfAccountsCode();
            accountNbr = e.getAccountNumber();
            subAcctNbr = e.getSubAccountNumber();
            finBalanceTypCd = e.getFinancialBalanceTypeCode();
            fdocTypCd = e.getFinancialDocumentTypeCode();
            fsOriginCd = e.getFinancialSystemOriginationCode();
            fdocNbr = e.getDocumentNumber();
            fdocReversalDt = e.getFinancialDocumentReversalDate();
            univFiscalPrdCd = e.getUniversityFiscalPeriodCode();
        }

        /**
         * Determines if an entry belongs to this unit of work
         * 
         * @param e the entry to check
         * @return true if it belongs to this unit of work, false otherwise
         */
        public boolean isSameUnitOfWork(OriginEntry e) {
            // Compare the key fields
            return univFiscalYr.equals(e.getUniversityFiscalYear()) && finCoaCd.equals(e.getChartOfAccountsCode()) && accountNbr.equals(e.getAccountNumber()) && subAcctNbr.equals(e.getSubAccountNumber()) && finBalanceTypCd.equals(e.getFinancialBalanceTypeCode()) && fdocTypCd.equals(e.getFinancialDocumentTypeCode()) && fsOriginCd.equals(e.getFinancialSystemOriginationCode()) && fdocNbr.equals(e.getDocumentNumber()) && ObjectHelper.isEqual(fdocReversalDt, e.getFinancialDocumentReversalDate()) && univFiscalPrdCd.equals(e.getUniversityFiscalPeriodCode());
        }

        /**
         * Converts this unit of work info to a String
         * @return a String representation of this UnitOfWorkInfo
         * @see java.lang.Object#toString()
         */
        public String toString() {
            return univFiscalYr + finCoaCd + accountNbr + subAcctNbr + finBalanceTypCd + fdocTypCd + fsOriginCd + fdocNbr + fdocReversalDt + univFiscalPrdCd;
        }

        /**
         * Generates the beginning of an OriginEntryFull, based on the unit of work info
         * 
         * @return a partially initialized OriginEntryFull
         */
        public OriginEntryFull getOffsetTemplate() {
            OriginEntryFull e = new OriginEntryFull();
            e.setUniversityFiscalYear(univFiscalYr);
            e.setChartOfAccountsCode(finCoaCd);
            e.setAccountNumber(accountNbr);
            e.setSubAccountNumber(subAcctNbr);
            e.setFinancialBalanceTypeCode(finBalanceTypCd);
            e.setFinancialDocumentTypeCode(fdocTypCd);
            e.setFinancialSystemOriginationCode(fsOriginCd);
            e.setDocumentNumber(fdocNbr);
            e.setFinancialDocumentReversalDate(fdocReversalDt);
            e.setUniversityFiscalPeriodCode(univFiscalPrdCd);
            return e;
        }
    }

    /**
     * An internal class to hold errors encountered by the scrubber
     */
    class TransactionError {
        public Transaction transaction;
        public Message message;

        /**
         * Constructs a ScrubberProcess.TransactionError instance
         * @param t the transaction that had the error
         * @param m a message about the error
         */
        public TransactionError(Transaction t, Message m) {
            transaction = t;
            message = m;
        }
    }

    /**
     * This method modifies the run date if it is before the cutoff time specified by the RunTimeService See
     * KULRNE-70 This method is public to facilitate unit testing
     * 
     * @param currentDate the date the scrubber should report as having run on
     * @return the run date
     */
    public Date calculateRunDate(java.util.Date currentDate) {
        return new Date(runDateService.calculateRunDate(currentDate).getTime());
    }

    /**
     * Sets the referenceLookup attribute value.
     * 
     * @param referenceLookup The referenceLookup to set.
     */
    public void setReferenceLookup(OriginEntryLookupService referenceLookup) {
        this.referenceLookup.set(referenceLookup);
        this.scrubberValidator.setReferenceLookup(referenceLookup);
    }


}
