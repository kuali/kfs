/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.gl.batch.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Date;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.kuali.kfs.coa.businessobject.A21SubAccount;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.BalanceType;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.OffsetDefinition;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.ObjectHelper;
import org.kuali.kfs.gl.batch.BatchSortUtil;
import org.kuali.kfs.gl.batch.CollectorBatch;
import org.kuali.kfs.gl.batch.DemergerSortComparator;
import org.kuali.kfs.gl.batch.ScrubberSortComparator;
import org.kuali.kfs.gl.batch.ScrubberStep;
import org.kuali.kfs.gl.batch.service.AccountingCycleCachingService;
import org.kuali.kfs.gl.batch.service.RunDateService;
import org.kuali.kfs.gl.batch.service.ScrubberProcess;
import org.kuali.kfs.gl.batch.service.impl.FilteringOriginEntryFileIterator.OriginEntryFilter;
import org.kuali.kfs.gl.businessobject.DemergerReportData;
import org.kuali.kfs.gl.businessobject.OriginEntryFieldUtil;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.gl.businessobject.OriginEntryInformation;
import org.kuali.kfs.gl.businessobject.Transaction;
import org.kuali.kfs.gl.report.CollectorReportData;
import org.kuali.kfs.gl.report.LedgerSummaryReport;
import org.kuali.kfs.gl.report.PreScrubberReport;
import org.kuali.kfs.gl.report.PreScrubberReportData;
import org.kuali.kfs.gl.report.TransactionListingReport;
import org.kuali.kfs.gl.service.PreScrubberService;
import org.kuali.kfs.gl.service.ScrubberReportData;
import org.kuali.kfs.gl.service.ScrubberValidator;
import org.kuali.kfs.gl.service.impl.ScrubberStatus;
import org.kuali.kfs.gl.service.impl.StringHelper;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.Message;
import org.kuali.kfs.sys.KFSParameterKeyConstants.GlParameterConstants;
import org.kuali.kfs.sys.batch.service.WrappingBatchService;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.businessobject.UniversityDate;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.exception.InvalidFlexibleOffsetException;
import org.kuali.kfs.sys.service.DocumentNumberAwareReportWriterService;
import org.kuali.kfs.sys.service.FlexibleOffsetAccountService;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.parameter.ParameterEvaluator;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.PersistenceService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * This class has the logic for the scrubber. It is required because the scrubber process needs instance variables. Instance
 * variables in a spring service are shared between all code calling the service. This will make sure each run of the scrubber has
 * it's own instance variables instead of being shared.
 */
public class ScrubberProcessImpl implements ScrubberProcess {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ScrubberProcessImpl.class);

    protected static final String TRANSACTION_TYPE_COST_SHARE_ENCUMBRANCE = "CE";
    protected static final String TRANSACTION_TYPE_OFFSET = "O";
    protected static final String TRANSACTION_TYPE_CAPITALIZATION = "C";
    protected static final String TRANSACTION_TYPE_LIABILITY = "L";
    protected static final String TRANSACTION_TYPE_TRANSFER = "T";
    protected static final String TRANSACTION_TYPE_COST_SHARE = "CS";
    protected static final String TRANSACTION_TYPE_OTHER = "X";

    enum GROUP_TYPE {VALID, ERROR, EXPIRED}

    protected static final String COST_SHARE_CODE = "CSHR";

    protected static final String COST_SHARE_TRANSFER_ENTRY_IND = "***";

    // These lengths are different then database field lengths, hence they are not from the DD
    protected static final int COST_SHARE_ENCUMBRANCE_ENTRY_MAXLENGTH = 28;
    protected static final int DEMERGER_TRANSACTION_LEDGET_ENTRY_DESCRIPTION = 33;
    protected static final int OFFSET_MESSAGE_MAXLENGTH = 33;

    /* Services required */
    protected FlexibleOffsetAccountService flexibleOffsetAccountService;
    protected DateTimeService dateTimeService;
    protected ConfigurationService configurationService;
    protected PersistenceService persistenceService;
    protected ScrubberValidator scrubberValidator;
    protected RunDateService runDateService;
    protected AccountingCycleCachingService accountingCycleCachingService;
    protected DocumentNumberAwareReportWriterService scrubberReportWriterService;
    protected DocumentNumberAwareReportWriterService scrubberLedgerReportWriterService;
    protected DocumentNumberAwareReportWriterService scrubberListingReportWriterService;
    protected DocumentNumberAwareReportWriterService preScrubberReportWriterService;
    protected ReportWriterService scrubberBadBalanceListingReportWriterService;
    protected ReportWriterService demergerRemovedTransactionsListingReportWriterService;
    protected ReportWriterService demergerReportWriterService;
    protected PreScrubberService preScrubberService;

    // these three members will only be populated when in collector mode, otherwise the memory requirements will be huge
    protected Map<OriginEntryInformation, OriginEntryInformation> unscrubbedToScrubbedEntries = new HashMap<OriginEntryInformation, OriginEntryInformation>();
    protected Map<Transaction, List<Message>> scrubberReportErrors = new IdentityHashMap<Transaction, List<Message>>();
    protected LedgerSummaryReport ledgerSummaryReport = new LedgerSummaryReport();

    protected ScrubberReportData scrubberReport;
    protected DemergerReportData demergerReport;

    /* These are all different forms of the run date for this job */
    protected Date runDate;
    protected Calendar runCal;
    protected UniversityDate universityRunDate;
    protected String offsetString;

    /* Unit Of Work info */
    protected UnitOfWorkInfo unitOfWork;
    protected KualiDecimal scrubCostShareAmount;

    /* Statistics for the reports */
    protected List<Message> transactionErrors;

    /* Description names */
    protected String offsetDescription;
    protected String capitalizationDescription;
    protected String liabilityDescription;
    protected String transferDescription;
    protected String costShareDescription;

    protected ParameterService parameterService;
    protected BusinessObjectService businessObjectService;

    /**
     * Whether this instance is being used to support the scrubbing of a collector batch
     */
    protected boolean collectorMode;
    protected String batchFileDirectoryName;

    protected PrintStream OUTPUT_GLE_FILE_ps;
    protected PrintStream OUTPUT_ERR_FILE_ps;
    protected PrintStream OUTPUT_EXP_FILE_ps;

    protected String inputFile;
    protected String validFile;
    protected String errorFile;
    protected String expiredFile;

    /**
     * Scrub this single group read only. This will only output the scrubber report. It won't output any other groups.
     *
     * @param group the origin entry group that should be scrubbed
     * @param the document number of any specific entries to scrub
     */
    @Override
    public void scrubGroupReportOnly(String fileName, String documentNumber) {
        LOG.debug("scrubGroupReportOnly() started");
        String unsortedFile = fileName;
        this.inputFile = fileName + ".sort";
        this.validFile = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        this.errorFile = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        this.expiredFile = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.SCRUBBER_EXPIRED_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        String prescrubOutput = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.PRE_SCRUBBER_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        this.ledgerSummaryReport = new LedgerSummaryReport();
        runDate = calculateRunDate(dateTimeService.getCurrentDate());

        PreScrubberReportData preScrubberReportData = null;

        // run pre-scrubber on the raw input into the sort process
        LineIterator inputEntries = null;
        try {
            inputEntries = FileUtils.lineIterator(new File(unsortedFile));
            preScrubberReportData = preScrubberService.preprocessOriginEntries(inputEntries, prescrubOutput);
        }
        catch (IOException e1) {
            LOG.error("Error encountered trying to prescrub GLCP/LLCP document", e1);
            throw new RuntimeException("Error encountered trying to prescrub GLCP/LLCP document", e1);
        }
        finally {
            LineIterator.closeQuietly(inputEntries);
        }
        if (preScrubberReportData != null) {
            preScrubberReportWriterService.setDocumentNumber(documentNumber);
            ((WrappingBatchService)preScrubberReportWriterService).initialize();
            try {
                new PreScrubberReport().generateReport(preScrubberReportData, preScrubberReportWriterService);
            }
            finally {
                ((WrappingBatchService)preScrubberReportWriterService).destroy();
            }
        }
        BatchSortUtil.sortTextFileWithFields(prescrubOutput, inputFile, new ScrubberSortComparator());

        scrubEntries(true, documentNumber);

        // delete files
        File deleteSortFile = new File(inputFile);
        File deleteValidFile = new File(validFile);
        File deleteErrorFile = new File(errorFile);
        File deleteExpiredFile = new File(expiredFile);
        try {
            deleteSortFile.delete();
            deleteValidFile.delete();
            deleteErrorFile.delete();
            deleteExpiredFile.delete();
        } catch (Exception e){
            LOG.error("scrubGroupReportOnly delete output files process Stopped: " + e.getMessage());
            throw new RuntimeException("scrubGroupReportOnly delete output files process Stopped: " + e.getMessage(), e);
        }
    }

    /**
     * Scrubs all entries in all groups and documents.
     */
    @Override
    public void scrubEntries() {
        this.inputFile = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        this.validFile = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        this.errorFile = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        this.expiredFile = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.SCRUBBER_EXPIRED_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        runDate = calculateRunDate(dateTimeService.getCurrentDate());

        scrubEntries(false, null);
    }

    /**
     * Scrubs the origin entry and ID billing details if the given batch. Store all scrubber output into the collectorReportData
     * parameter. NOTE: DO NOT CALL ANY OF THE scrub* METHODS OF THIS CLASS AFTER CALLING THIS METHOD FOR EVERY UNIQUE INSTANCE OF
     * THIS CLASS, OR THE COLLECTOR REPORTS MAY BE CORRUPTED
     *
     * @param batch the data gathered from a Collector file
     * @param collectorReportData the statistics generated by running the Collector
     */
    @Override
    public void scrubCollectorBatch(ScrubberStatus scrubberStatus, CollectorBatch batch, CollectorReportData collectorReportData) {
        collectorMode = true;

        this.inputFile = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.COLLECTOR_SCRUBBER_INPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        this.validFile = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.COLLECTOR_SCRUBBER_VALID_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        this.errorFile = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.COLLECTOR_SCRUBBER_ERROR_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        this.expiredFile = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.COLLECTOR_SCRUBBER_EXPIRED_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        runDate = calculateRunDate(dateTimeService.getCurrentDate());

        this.ledgerSummaryReport = collectorReportData.getLedgerSummaryReport();

        // sort input file
        String scrubberSortInputFile = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.COLLECTOR_BACKUP_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        String scrubberSortOutputFile = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.COLLECTOR_SCRUBBER_INPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        BatchSortUtil.sortTextFileWithFields(scrubberSortInputFile, scrubberSortOutputFile, new ScrubberSortComparator());

        scrubEntries(false, null);

        //sort scrubber error file for demerger
        String demergerSortInputFile = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.COLLECTOR_SCRUBBER_ERROR_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        String demergerSortOutputFile = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.COLLECTOR_SCRUBBER_ERROR_SORTED_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        BatchSortUtil.sortTextFileWithFields(demergerSortInputFile, demergerSortOutputFile, new DemergerSortComparator());

        performDemerger();

        // the scrubber process has just updated several member variables of this class. Store these values for the collector report
        collectorReportData.setBatchOriginEntryScrubberErrors(batch, scrubberReportErrors);
        collectorReportData.setScrubberReportData(batch, scrubberReport);
        collectorReportData.setDemergerReportData(batch, demergerReport);

        // report purpose - commented out.  If we need, the put string values for fileNames.
        scrubberStatus.setInputFileName(GeneralLedgerConstants.BatchFileSystem.COLLECTOR_SCRUBBER_INPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION);
        scrubberStatus.setValidFileName(GeneralLedgerConstants.BatchFileSystem.COLLECTOR_DEMERGER_VAILD_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION);
        scrubberStatus.setErrorFileName(GeneralLedgerConstants.BatchFileSystem.COLLECTOR_DEMERGER_ERROR_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION);
        scrubberStatus.setExpiredFileName(GeneralLedgerConstants.BatchFileSystem.COLLECTOR_SCRUBBER_EXPIRED_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION);
        scrubberStatus.setUnscrubbedToScrubbedEntries(unscrubbedToScrubbedEntries);
    }

    /**
     * Scrub all entries that need it in origin entry. Put valid scrubbed entries in a scrubber valid group, put errors in a
     * scrubber error group, and transactions with an expired account in the scrubber expired account group.
     * @param group the specific origin entry group to scrub
     * @param documentNumber the number of the document with entries to scrub
     */
    @Override
    public void scrubEntries(boolean reportOnlyMode, String documentNumber) {
        LOG.debug("scrubEntries() started");

        if (reportOnlyMode) {
            scrubberReportWriterService.setDocumentNumber(documentNumber);
            scrubberLedgerReportWriterService.setDocumentNumber(documentNumber);
        }

        // setup an object to hold the "default" date information
        runDate = calculateRunDate(dateTimeService.getCurrentDate());
        runCal = Calendar.getInstance();
        runCal.setTime(runDate);

        universityRunDate = accountingCycleCachingService.getUniversityDate(runDate);
        if (universityRunDate == null) {
            throw new IllegalStateException(configurationService.getPropertyValueAsString(KFSKeyConstants.ERROR_UNIV_DATE_NOT_FOUND));
        }

        setOffsetString();
        setDescriptions();
        scrubberReport = new ScrubberReportData();

        try {
            if (!collectorMode) {
                ((WrappingBatchService) scrubberReportWriterService).initialize();
                ((WrappingBatchService) scrubberLedgerReportWriterService).initialize();
            }

            processGroup(reportOnlyMode, scrubberReport);

            /* temp log for debugging output file delay issue
            if (new File(validFile).canRead()) {
                LOG.info("Valid file " + validFile + " is ready for read.");
            }
            else {
                LOG.error("Valid file " + validFile + " is not readable.");
            }
            if (new File(errorFile).canRead()) {
                LOG.info("Error file " + errorFile + " is ready for read.");
            }
            else {
                LOG.error("Error file " + errorFile + " is not readable.");
            }
            if (new File(expiredFile).canRead()) {
                LOG.info("Expired file " + expiredFile + " is ready for read.");
            }
            else {
                LOG.error("Expired file " + expiredFile + " is not readable.");
            }
            */

            if (reportOnlyMode) {
                generateScrubberTransactionListingReport(documentNumber, inputFile);
            }
            else if (collectorMode) {
                // defer report generation for later
            }
            else {
                generateScrubberBlankBalanceTypeCodeReport(inputFile);
            }
        }
        finally {
            if (!collectorMode) {
                ((WrappingBatchService) scrubberReportWriterService).destroy();
                ((WrappingBatchService) scrubberLedgerReportWriterService).destroy();
            }
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
    @Override
    public void performDemerger() {
        LOG.debug("performDemerger() started");

        OriginEntryFieldUtil oefu = new OriginEntryFieldUtil();
        Map<String, Integer> pMap = oefu.getFieldBeginningPositionMap();

        // Without this step, the job fails with Optimistic Lock Exceptions
     //   persistenceService.clearCache();

        demergerReport = new DemergerReportData();

        // set runDate here again, because demerger is calling outside from scrubber
        runDate = calculateRunDate(dateTimeService.getCurrentDate());
        runCal = Calendar.getInstance();
        runCal.setTime(runDate);

        // demerger called by outside from scrubber, so reset those values
        setOffsetString();
        setDescriptions();

        // new demerger starts

        String validOutputFilename = null;
        String errorOutputFilename = null;

        String demergerValidOutputFilename = null;
        String demergerErrorOutputFilename = null;

        if(!collectorMode){
            validOutputFilename = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
            errorOutputFilename = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;

            demergerValidOutputFilename = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
            demergerErrorOutputFilename = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;

        } else {

            validOutputFilename = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.COLLECTOR_SCRUBBER_VALID_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
            errorOutputFilename = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.COLLECTOR_SCRUBBER_ERROR_SORTED_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;

            demergerValidOutputFilename = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.COLLECTOR_DEMERGER_VAILD_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
            demergerErrorOutputFilename = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.COLLECTOR_DEMERGER_ERROR_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        }

        // Without this step, the job fails with Optimistic Lock Exceptions
    //    persistenceService.clearCache();

        FileReader INPUT_GLE_FILE = null;
        FileReader INPUT_ERR_FILE = null;
        BufferedReader INPUT_GLE_FILE_br;
        BufferedReader INPUT_ERR_FILE_br;
        PrintStream OUTPUT_DEMERGER_GLE_FILE_ps;
        PrintStream OUTPUT_DEMERGER_ERR_FILE_ps;

        try {
            INPUT_GLE_FILE = new FileReader(validOutputFilename);
            INPUT_ERR_FILE = new FileReader(errorOutputFilename);
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            OUTPUT_DEMERGER_GLE_FILE_ps = new PrintStream(demergerValidOutputFilename);
            OUTPUT_DEMERGER_ERR_FILE_ps = new PrintStream(demergerErrorOutputFilename);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        int validSaved = 0;
        int errorSaved = 0;

        int validReadLine = 0;
        int errorReadLine = 0;

        boolean errorsLoading = false;
        INPUT_GLE_FILE_br = new BufferedReader(INPUT_GLE_FILE);
        INPUT_ERR_FILE_br = new BufferedReader(INPUT_ERR_FILE);

        try {
            String currentValidLine = INPUT_GLE_FILE_br.readLine();
            String currentErrorLine = INPUT_ERR_FILE_br.readLine();

            boolean meetFlag = false;

            while (currentValidLine != null || currentErrorLine != null) {

                // Demerger only catch IOexception since demerger report doesn't display
                // detail error message.
                try{
                   //validLine is null means that errorLine is not null
                   if (org.apache.commons.lang.StringUtils.isEmpty(currentValidLine)) {
                       String errorDesc = currentErrorLine.substring(pMap.get(KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_DESC), pMap.get(KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT));
                       String errorFinancialBalanceTypeCode = currentErrorLine.substring(pMap.get(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE), pMap.get(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE));

                       if (!checkingBypassEntry(errorFinancialBalanceTypeCode, errorDesc, demergerReport)){
                           createOutputEntry(currentErrorLine, OUTPUT_DEMERGER_ERR_FILE_ps);
                           errorSaved++;
                       }
                       currentErrorLine = INPUT_ERR_FILE_br.readLine();
                       errorReadLine++;
                       continue;
                   }

                   String financialBalanceTypeCode = currentValidLine.substring(pMap.get(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE), pMap.get(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE));
                   String desc = currentValidLine.substring(pMap.get(KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_DESC), pMap.get(KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT));

                   //errorLine is null means that validLine is not null
                   if (org.apache.commons.lang.StringUtils.isEmpty(currentErrorLine)) {
                       // Read all the transactions in the valid group and update the cost share transactions
                       String updatedValidLine = checkAndSetTransactionTypeCostShare(financialBalanceTypeCode, desc, currentValidLine);
                       createOutputEntry(updatedValidLine, OUTPUT_DEMERGER_GLE_FILE_ps);
                       handleDemergerSaveValidEntry(updatedValidLine);
                       validSaved++;
                       currentValidLine = INPUT_GLE_FILE_br.readLine();
                       validReadLine++;
                       continue;
                   }

                   String compareStringFromValidEntry = currentValidLine.substring(pMap.get(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE), pMap.get(KFSPropertyConstants.TRANSACTION_ENTRY_SEQUENCE_NUMBER));
                   String compareStringFromErrorEntry = currentErrorLine.substring(pMap.get(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE), pMap.get(KFSPropertyConstants.TRANSACTION_ENTRY_SEQUENCE_NUMBER));

                   String errorDesc = currentErrorLine.substring(pMap.get(KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_DESC), pMap.get(KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_AMOUNT));
                   String errorFinancialBalanceTypeCode = currentErrorLine.substring(pMap.get(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE), pMap.get(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE));

                   if (compareStringFromValidEntry.compareTo(compareStringFromErrorEntry) < 0){
                       // Read all the transactions in the valid group and update the cost share transactions
                       String updatedValidLine = checkAndSetTransactionTypeCostShare(financialBalanceTypeCode, desc, currentValidLine);
                       createOutputEntry(updatedValidLine, OUTPUT_DEMERGER_GLE_FILE_ps);
                       handleDemergerSaveValidEntry(updatedValidLine);
                       validSaved++;
                       currentValidLine = INPUT_GLE_FILE_br.readLine();
                       validReadLine++;

                   } else if (compareStringFromValidEntry.compareTo(compareStringFromErrorEntry) > 0) {
                       if (!checkingBypassEntry(errorFinancialBalanceTypeCode, errorDesc, demergerReport)){
                           createOutputEntry(currentErrorLine, OUTPUT_DEMERGER_ERR_FILE_ps);
                           errorSaved++;
                       }
                       currentErrorLine = INPUT_ERR_FILE_br.readLine();
                       errorReadLine++;

                   } else {
                       if (!checkingBypassEntry(financialBalanceTypeCode, desc, demergerReport)){
                           createOutputEntry(currentValidLine, OUTPUT_DEMERGER_ERR_FILE_ps);
                           errorSaved++;
                       }
                       currentValidLine = INPUT_GLE_FILE_br.readLine();
                       validReadLine++;
                   }

                   continue;

               } catch (RuntimeException re) {
                   LOG.error("performDemerger Stopped: " + re.getMessage());
                   throw new RuntimeException("performDemerger Stopped: " + re.getMessage(), re);
               }
            }
            INPUT_GLE_FILE_br.close();
            INPUT_ERR_FILE_br.close();
            OUTPUT_DEMERGER_GLE_FILE_ps.close();
            OUTPUT_DEMERGER_ERR_FILE_ps.close();

        } catch (IOException e) {
            LOG.error("performDemerger Stopped: " + e.getMessage());
            throw new RuntimeException("performDemerger Stopped: " + e.getMessage(), e);
        }
        demergerReport.setErrorTransactionWritten(errorSaved);
        demergerReport.setErrorTransactionsRead(errorReadLine);
        demergerReport.setValidTransactionsRead(validReadLine);
        demergerReport.setValidTransactionsSaved(validSaved);

        if (!collectorMode) {
            demergerReportWriterService.writeStatisticLine("SCRUBBER ERROR TRANSACTIONS READ       %,9d", demergerReport.getErrorTransactionsRead());
            demergerReportWriterService.writeStatisticLine("SCRUBBER VALID TRANSACTIONS READ       %,9d", demergerReport.getValidTransactionsRead());
            demergerReportWriterService.writeNewLines(1);
            demergerReportWriterService.writeStatisticLine("DEMERGER ERRORS SAVED                  %,9d", demergerReport.getErrorTransactionsSaved());
            demergerReportWriterService.writeStatisticLine("DEMERGER VALID TRANSACTIONS SAVED      %,9d", demergerReport.getValidTransactionsSaved());
            demergerReportWriterService.writeStatisticLine("OFFSET TRANSACTIONS BYPASSED           %,9d", demergerReport.getOffsetTransactionsBypassed());
            demergerReportWriterService.writeStatisticLine("CAPITALIZATION TRANSACTIONS BYPASSED   %,9d", demergerReport.getCapitalizationTransactionsBypassed());
            demergerReportWriterService.writeStatisticLine("LIABILITY TRANSACTIONS BYPASSED        %,9d", demergerReport.getLiabilityTransactionsBypassed());
            demergerReportWriterService.writeStatisticLine("TRANSFER TRANSACTIONS BYPASSED         %,9d", demergerReport.getTransferTransactionsBypassed());
            demergerReportWriterService.writeStatisticLine("COST SHARE TRANSACTIONS BYPASSED       %,9d", demergerReport.getCostShareTransactionsBypassed());
            demergerReportWriterService.writeStatisticLine("COST SHARE ENC TRANSACTIONS BYPASSED   %,9d", demergerReport.getCostShareEncumbranceTransactionsBypassed());

            generateDemergerRemovedTransactionsReport(demergerErrorOutputFilename);
        }
    }

    /**
     * Determine the type of the transaction by looking at attributes
     *
     * @param transaction Transaction to identify
     * @return CE (Cost share encumbrance, O (Offset), C (apitalization), L (Liability), T (Transfer), CS (Cost Share), X (Other)
     */
    protected String getTransactionType(OriginEntryInformation transaction) {
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


    protected String getTransactionType(String financialBalanceTypeCode, String desc) {
        if (TRANSACTION_TYPE_COST_SHARE_ENCUMBRANCE.equals(financialBalanceTypeCode)) {
            return TRANSACTION_TYPE_COST_SHARE_ENCUMBRANCE;
        }
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
    protected void processGroup(boolean reportOnlyMode, ScrubberReportData scrubberReport) {
        OriginEntryFull lastEntry = null;
        scrubCostShareAmount = KualiDecimal.ZERO;
        unitOfWork = new UnitOfWorkInfo();

        FileReader INPUT_GLE_FILE = null;
        String GLEN_RECORD;
        BufferedReader INPUT_GLE_FILE_br;
        try {
            INPUT_GLE_FILE = new FileReader(inputFile);
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            OUTPUT_GLE_FILE_ps = new PrintStream(validFile);
            OUTPUT_ERR_FILE_ps = new PrintStream(errorFile);
            OUTPUT_EXP_FILE_ps = new PrintStream(expiredFile);
            LOG.info("Successfully opened " + validFile + ", " + errorFile + ", " + expiredFile + " for writing.");
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }

        INPUT_GLE_FILE_br = new BufferedReader(INPUT_GLE_FILE);
        int line = 0;
        LOG.debug("Starting Scrubber Process process group...");
        try {
            while ((GLEN_RECORD = INPUT_GLE_FILE_br.readLine()) != null) {
                if (!org.apache.commons.lang.StringUtils.isEmpty(GLEN_RECORD) && !org.apache.commons.lang.StringUtils.isBlank(GLEN_RECORD.trim())) {
                    line++;
                    OriginEntryFull unscrubbedEntry = new OriginEntryFull();
                    List<Message> tmperrors = unscrubbedEntry.setFromTextFileForBatch(GLEN_RECORD, line);
                    scrubberReport.incrementUnscrubbedRecordsRead();
                    transactionErrors = new ArrayList<Message>();

                    //
                    // This is done so if the code modifies this row, then saves it, it will be an insert,
                    // and it won't touch the original. The Scrubber never modifies input rows/groups.
                    // not relevant for file version

                    boolean saveErrorTransaction = false;
                    boolean saveValidTransaction = false;
                    boolean fatalErrorOccurred = false;

                    // Build a scrubbed entry
                    OriginEntryFull scrubbedEntry = new OriginEntryFull();
                    scrubbedEntry.setDocumentNumber(unscrubbedEntry.getDocumentNumber());
                    scrubbedEntry.setOrganizationDocumentNumber(unscrubbedEntry.getOrganizationDocumentNumber());
                    scrubbedEntry.setOrganizationReferenceId(unscrubbedEntry.getOrganizationReferenceId());
                    scrubbedEntry.setReferenceFinancialDocumentNumber(unscrubbedEntry.getReferenceFinancialDocumentNumber());

                    Integer transactionNumber = unscrubbedEntry.getTransactionLedgerEntrySequenceNumber();
                    scrubbedEntry.setTransactionLedgerEntrySequenceNumber(null == transactionNumber ? new Integer(0) : transactionNumber);
                    scrubbedEntry.setTransactionLedgerEntryDescription(unscrubbedEntry.getTransactionLedgerEntryDescription());
                    scrubbedEntry.setTransactionLedgerEntryAmount(unscrubbedEntry.getTransactionLedgerEntryAmount());
                    scrubbedEntry.setTransactionDebitCreditCode(unscrubbedEntry.getTransactionDebitCreditCode());

                    if (!collectorMode) {
                        ledgerSummaryReport.summarizeEntry(unscrubbedEntry);
                    }

                    // For Labor Scrubber
                    boolean laborIndicator = false;
                    tmperrors.addAll(scrubberValidator.validateTransaction(unscrubbedEntry, scrubbedEntry, universityRunDate, laborIndicator, accountingCycleCachingService));
                    transactionErrors.addAll(tmperrors);


                    Account unscrubbedEntryAccount = accountingCycleCachingService.getAccount(unscrubbedEntry.getChartOfAccountsCode(), unscrubbedEntry.getAccountNumber());
                    // KFSMI-173: both the expired and closed accounts rows are put in the expired account
                    if ((unscrubbedEntryAccount != null) && (scrubberValidator.isAccountExpired(unscrubbedEntryAccount, universityRunDate) || unscrubbedEntryAccount.isClosed())) {
                        // Make a copy of it so OJB doesn't just update the row in the original
                        // group. It needs to make a new one in the expired group
                        OriginEntryFull expiredEntry = OriginEntryFull.copyFromOriginEntryable(scrubbedEntry);
                        createOutputEntry(expiredEntry, OUTPUT_EXP_FILE_ps);
                        scrubberReport.incrementExpiredAccountFound();
                    }

                    // the collector scrubber uses this map to apply the same changes made on an origin entry during scrubbing to
                    // the collector detail record
                    if (collectorMode) {
                        unscrubbedToScrubbedEntries.put(unscrubbedEntry, scrubbedEntry);
                    }

                    if (!isFatal(transactionErrors)) {
                        saveValidTransaction = true;

                        if (!collectorMode) {

                            // See if unit of work has changed
                            if (!unitOfWork.isSameUnitOfWork(scrubbedEntry)) {
                                // Generate offset for last unit of work
                                // pass the String line for generating error files
                                generateOffset(lastEntry, scrubberReport);

                                unitOfWork = new UnitOfWorkInfo(scrubbedEntry);
                            }

                            KualiDecimal transactionAmount = scrubbedEntry.getTransactionLedgerEntryAmount();

                            ParameterEvaluator offsetFiscalPeriods = /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.OFFSET_FISCAL_PERIOD_CODES, scrubbedEntry.getUniversityFiscalPeriodCode());

                            BalanceType scrubbedEntryBalanceType = accountingCycleCachingService.getBalanceType(scrubbedEntry.getFinancialBalanceTypeCode());
                            if (scrubbedEntryBalanceType.isFinancialOffsetGenerationIndicator() && offsetFiscalPeriods.evaluationSucceeds()) {
                                if (scrubbedEntry.isDebit()) {
                                    unitOfWork.offsetAmount = unitOfWork.offsetAmount.add(transactionAmount);
                                }
                                else {
                                    unitOfWork.offsetAmount = unitOfWork.offsetAmount.subtract(transactionAmount);
                                }
                            }

                            // The sub account type code will only exist if there is a valid sub account
                            String subAccountTypeCode = GeneralLedgerConstants.getSpaceSubAccountTypeCode();
                            // major assumption: the a21 subaccount is proxied, so we don't want to query the database if the
                            // subacct
                            // number is dashes
                            if (!KFSConstants.getDashSubAccountNumber().equals(scrubbedEntry.getSubAccountNumber())) {
                                A21SubAccount scrubbedEntryA21SubAccount = accountingCycleCachingService.getA21SubAccount(scrubbedEntry.getChartOfAccountsCode(), scrubbedEntry.getAccountNumber(), scrubbedEntry.getSubAccountNumber());
                                if (ObjectUtils.isNotNull(scrubbedEntryA21SubAccount)) {
                                    subAccountTypeCode = scrubbedEntryA21SubAccount.getSubAccountTypeCode();
                                }
                            }

                            ParameterEvaluator costShareObjectTypeCodes = /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.COST_SHARE_OBJ_TYPE_CODES, scrubbedEntry.getFinancialObjectTypeCode());
                            ParameterEvaluator costShareEncBalanceTypeCodes = /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.COST_SHARE_ENC_BAL_TYP_CODES, scrubbedEntry.getFinancialBalanceTypeCode());
                            ParameterEvaluator costShareEncFiscalPeriodCodes = /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.COST_SHARE_ENC_FISCAL_PERIOD_CODES, scrubbedEntry.getUniversityFiscalPeriodCode());
                            ParameterEvaluator costShareEncDocTypeCodes = /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.COST_SHARE_ENC_DOC_TYPE_CODES, scrubbedEntry.getFinancialDocumentTypeCode().trim());
                            ParameterEvaluator costShareFiscalPeriodCodes = /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.COST_SHARE_FISCAL_PERIOD_CODES, scrubbedEntry.getUniversityFiscalPeriodCode());
                            Account scrubbedEntryAccount = accountingCycleCachingService.getAccount(scrubbedEntry.getChartOfAccountsCode(), scrubbedEntry.getAccountNumber());

                            if (costShareObjectTypeCodes.evaluationSucceeds() && costShareEncBalanceTypeCodes.evaluationSucceeds() && scrubbedEntryAccount.isForContractsAndGrants() && KFSConstants.SubAccountType.COST_SHARE.equals(subAccountTypeCode) && costShareEncFiscalPeriodCodes.evaluationSucceeds() && costShareEncDocTypeCodes.evaluationSucceeds()) {
                                TransactionError te1 = generateCostShareEncumbranceEntries(scrubbedEntry, scrubberReport);
                                if (te1 != null) {
                                    List errors = new ArrayList();
                                    errors.add(te1.message);
                                    handleTransactionErrors(te1.transaction, errors);
                                    saveValidTransaction = false;
                                    saveErrorTransaction = true;
                                }
                            }

                            SystemOptions scrubbedEntryOption = accountingCycleCachingService.getSystemOptions(scrubbedEntry.getUniversityFiscalYear());
                            if (costShareObjectTypeCodes.evaluationSucceeds() && scrubbedEntryOption.getActualFinancialBalanceTypeCd().equals(scrubbedEntry.getFinancialBalanceTypeCode()) && scrubbedEntryAccount.isForContractsAndGrants() && KFSConstants.SubAccountType.COST_SHARE.equals(subAccountTypeCode) && costShareFiscalPeriodCodes.evaluationSucceeds() && costShareEncDocTypeCodes.evaluationSucceeds()) {
                                if (scrubbedEntry.isDebit()) {
                                    scrubCostShareAmount = scrubCostShareAmount.subtract(transactionAmount);
                                }
                                else {
                                    scrubCostShareAmount = scrubCostShareAmount.add(transactionAmount);
                                }
                            }

                            ParameterEvaluator otherDocTypeCodes = /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.OFFSET_DOC_TYPE_CODES, scrubbedEntry.getFinancialDocumentTypeCode());

                            if (otherDocTypeCodes.evaluationSucceeds()) {
                                String m = processCapitalization(scrubbedEntry, scrubberReport);
                                if (m != null) {
                                    saveValidTransaction = false;
                                    saveErrorTransaction = false;
                                    addTransactionError(m, "", Message.TYPE_FATAL);
                                }

                                m = processLiabilities(scrubbedEntry, scrubberReport);
                                if (m != null) {
                                    saveValidTransaction = false;
                                    saveErrorTransaction = false;
                                    addTransactionError(m, "", Message.TYPE_FATAL);
                                }

                                m = processPlantIndebtedness(scrubbedEntry, scrubberReport);
                                if (m != null) {
                                    saveValidTransaction = false;
                                    saveErrorTransaction = false;
                                    addTransactionError(m, "", Message.TYPE_FATAL);
                                }
                            }

                            if (!scrubCostShareAmount.isZero()) {
                                TransactionError te = generateCostShareEntries(scrubbedEntry, scrubberReport);

                                if (te != null) {
                                    saveValidTransaction = false;
                                    saveErrorTransaction = false;

                                    // Make a copy of it so OJB doesn't just update the row in the original
                                    // group. It needs to make a new one in the error group
                                    OriginEntryFull errorEntry = new OriginEntryFull(te.transaction);
                                    errorEntry.setTransactionScrubberOffsetGenerationIndicator(false);
                                    createOutputEntry(GLEN_RECORD, OUTPUT_ERR_FILE_ps);
                                    scrubberReport.incrementErrorRecordWritten();
                                    unitOfWork.errorsFound = true;

                                    handleTransactionError(te.transaction, te.message);
                                }
                                scrubCostShareAmount = KualiDecimal.ZERO;
                            }

                            lastEntry = scrubbedEntry;
                        }
                    }
                    else {
                        // Error transaction
                        saveErrorTransaction = true;
                        fatalErrorOccurred = true;
                    }
                    handleTransactionErrors(OriginEntryFull.copyFromOriginEntryable(unscrubbedEntry), transactionErrors);

                    if (saveValidTransaction) {
                        scrubbedEntry.setTransactionScrubberOffsetGenerationIndicator(false);
                        createOutputEntry(scrubbedEntry, OUTPUT_GLE_FILE_ps);
                        scrubberReport.incrementScrubbedRecordWritten();
                    }

                    if (saveErrorTransaction) {
                        // Make a copy of it so OJB doesn't just update the row in the original
                        // group. It needs to make a new one in the error group
                        OriginEntryFull errorEntry = OriginEntryFull.copyFromOriginEntryable(unscrubbedEntry);
                        errorEntry.setTransactionScrubberOffsetGenerationIndicator(false);
                        createOutputEntry(GLEN_RECORD, OUTPUT_ERR_FILE_ps);
                        scrubberReport.incrementErrorRecordWritten();
                        if (!fatalErrorOccurred) {
                            // if a fatal error occurred, the creation of a new unit of work was by-passed;
                            // therefore, it shouldn't ruin the previous unit of work
                            unitOfWork.errorsFound = true;
                        }
                    }
                }
            }

            if (!collectorMode) {
                // Generate last offset (if necessary)
                generateOffset(lastEntry, scrubberReport);
            }

            INPUT_GLE_FILE_br.close();
            INPUT_GLE_FILE.close();
            OUTPUT_GLE_FILE_ps.close();
            OUTPUT_ERR_FILE_ps.close();
            OUTPUT_EXP_FILE_ps.close();
            LOG.info("Successfully writen and closed " + validFile + ", " + errorFile + ", " + expiredFile + ".");

            handleEndOfScrubberReport(scrubberReport);

            if (!collectorMode) {
                ledgerSummaryReport.writeReport(this.scrubberLedgerReportWriterService);
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Determines if a given error is fatal and should stop this scrubber run
     *
     * @param errors errors from a scrubber run
     * @return true if the run should be abended, false otherwise
     */
    protected boolean isFatal(List<Message> errors) {
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
    protected TransactionError generateCostShareEntries(OriginEntryInformation scrubbedEntry, ScrubberReportData scrubberReport) {
        // 3000-COST-SHARE to 3100-READ-OFSD in the cobol Generate Cost Share Entries
        LOG.debug("generateCostShareEntries() started");
        try {
            OriginEntryFull costShareEntry = OriginEntryFull.copyFromOriginEntryable(scrubbedEntry);

            SystemOptions scrubbedEntryOption = accountingCycleCachingService.getSystemOptions(scrubbedEntry.getUniversityFiscalYear());
            A21SubAccount scrubbedEntryA21SubAccount = accountingCycleCachingService.getA21SubAccount(scrubbedEntry.getChartOfAccountsCode(), scrubbedEntry.getAccountNumber(), scrubbedEntry.getSubAccountNumber());

            costShareEntry.setFinancialObjectCode(parameterService.getParameterValueAsString(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupParameters.COST_SHARE_OBJECT_CODE_PARM_NM));
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

            createOutputEntry(costShareEntry, OUTPUT_GLE_FILE_ps);
            scrubberReport.incrementCostShareEntryGenerated();

            OriginEntryFull costShareOffsetEntry = new OriginEntryFull(costShareEntry);
            costShareOffsetEntry.setTransactionLedgerEntryDescription(getOffsetMessage());
            OffsetDefinition offsetDefinition = accountingCycleCachingService.getOffsetDefinition(scrubbedEntry.getUniversityFiscalYear(), scrubbedEntry.getChartOfAccountsCode(), KFSConstants.TRANSFER_FUNDS, scrubbedEntry.getFinancialBalanceTypeCode());
            if (offsetDefinition != null) {
                if (offsetDefinition.getFinancialObject() == null) {
                    StringBuffer objectCodeKey = new StringBuffer();
                    objectCodeKey.append(offsetDefinition.getUniversityFiscalYear());
                    objectCodeKey.append("-").append(offsetDefinition.getChartOfAccountsCode());
                    objectCodeKey.append("-").append(offsetDefinition.getFinancialObjectCode());

                    Message m = new Message(configurationService.getPropertyValueAsString(KFSKeyConstants.ERROR_OFFSET_DEFINITION_OBJECT_CODE_NOT_FOUND) + " (" + objectCodeKey.toString() + ")", Message.TYPE_FATAL);
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

                Message m = new Message(configurationService.getPropertyValueAsString(KFSKeyConstants.ERROR_OFFSET_DEFINITION_NOT_FOUND) + " (" + offsetKey.toString() + ")", Message.TYPE_FATAL);

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
                if (LOG.isDebugEnabled()) {
                    LOG.debug("generateCostShareEntries() Cost Share Transfer Flexible Offset Error: " + e.getMessage());
                }
                return new TransactionError(costShareEntry, m);
            }

            createOutputEntry(costShareOffsetEntry, OUTPUT_GLE_FILE_ps);
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

            createOutputEntry(costShareSourceAccountEntry, OUTPUT_GLE_FILE_ps);
            scrubberReport.incrementCostShareEntryGenerated();

            OriginEntryFull costShareSourceAccountOffsetEntry = new OriginEntryFull(costShareSourceAccountEntry);
            costShareSourceAccountOffsetEntry.setTransactionLedgerEntryDescription(getOffsetMessage());

            // Lookup the new offset definition.
            offsetDefinition = accountingCycleCachingService.getOffsetDefinition(scrubbedEntry.getUniversityFiscalYear(), scrubbedEntry.getChartOfAccountsCode(), KFSConstants.TRANSFER_FUNDS, scrubbedEntry.getFinancialBalanceTypeCode());
            if (offsetDefinition != null) {
                if (offsetDefinition.getFinancialObject() == null) {
                    Map<Transaction, List<Message>> errors = new HashMap<Transaction, List<Message>>();

                    StringBuffer objectCodeKey = new StringBuffer();
                    objectCodeKey.append(costShareEntry.getUniversityFiscalYear());
                    objectCodeKey.append("-").append(scrubbedEntry.getChartOfAccountsCode());
                    objectCodeKey.append("-").append(scrubbedEntry.getFinancialObjectCode());

                    Message m = new Message(configurationService.getPropertyValueAsString(KFSKeyConstants.ERROR_OFFSET_DEFINITION_OBJECT_CODE_NOT_FOUND) + " (" + objectCodeKey.toString() + ")", Message.TYPE_FATAL);

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

                Message m = new Message(configurationService.getPropertyValueAsString(KFSKeyConstants.ERROR_OFFSET_DEFINITION_NOT_FOUND) + " (" + offsetKey.toString() + ")", Message.TYPE_FATAL);

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
                if (LOG.isDebugEnabled()) {
                    LOG.debug("generateCostShareEntries() Cost Share Transfer Account Flexible Offset Error: " + e.getMessage());
                }
                return new TransactionError(costShareEntry, m);
            }

            createOutputEntry(costShareSourceAccountOffsetEntry, OUTPUT_GLE_FILE_ps);
            scrubberReport.incrementCostShareEntryGenerated();

            scrubCostShareAmount = KualiDecimal.ZERO;
        } catch (IOException ioe) {
            LOG.error("generateCostShareEntries() Stopped: " + ioe.getMessage());
            throw new RuntimeException("generateCostShareEntries() Stopped: " + ioe.getMessage(), ioe);
        }
        LOG.debug("generateCostShareEntries() successful");
        return null;
    }

    /**
     * Get all the transaction descriptions from the param table
     */
    protected void setDescriptions() {
        //TODO: move to constants class?
        offsetDescription = "GENERATED OFFSET";
        capitalizationDescription = "GENERATED CAPITALIZATION";
        liabilityDescription = "GENERATED LIABILITY";
        costShareDescription = "GENERATED COST SHARE FROM";
        transferDescription = "GENERATED TRANSFER FROM";
    }

    /**
     * Generate the flag for the end of specific descriptions. This will be used in the demerger step
     */
    protected void setOffsetString() {

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
    protected String getOffsetMessage() {
        String msg = offsetDescription + GeneralLedgerConstants.getSpaceTransactionLedgetEntryDescription();

        return msg.substring(0, OFFSET_MESSAGE_MAXLENGTH) + offsetString;
    }

    /**
     * Generates capitalization entries if necessary
     *
     * @param scrubbedEntry the entry to generate capitalization entries (possibly) for
     * @return null if no error, message if error
     */
    protected String processCapitalization(OriginEntryInformation scrubbedEntry, ScrubberReportData scrubberReport) {

        try {
         // Lines 4694 - 4798 of the Pro Cobol listing on Confluence
            if (!parameterService.getParameterValueAsBoolean(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupParameters.CAPITALIZATION_IND)) {
                return null;
            }

            OriginEntryFull capitalizationEntry = OriginEntryFull.copyFromOriginEntryable(scrubbedEntry);
            SystemOptions scrubbedEntryOption = accountingCycleCachingService.getSystemOptions(scrubbedEntry.getUniversityFiscalYear());
            ObjectCode scrubbedEntryObjectCode = accountingCycleCachingService.getObjectCode(scrubbedEntry.getUniversityFiscalYear(), scrubbedEntry.getChartOfAccountsCode(), scrubbedEntry.getFinancialObjectCode());
            Chart scrubbedEntryChart = accountingCycleCachingService.getChart(scrubbedEntry.getChartOfAccountsCode());
            Account scrubbedEntryAccount = accountingCycleCachingService.getAccount(scrubbedEntry.getChartOfAccountsCode(), scrubbedEntry.getAccountNumber());

            ParameterEvaluator documentTypeCodes = (!ObjectUtils.isNull(scrubbedEntry)) ? /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.CAPITALIZATION_DOC_TYPE_CODES, scrubbedEntry.getFinancialDocumentTypeCode()) : null;
            ParameterEvaluator fiscalPeriodCodes = (!ObjectUtils.isNull(scrubbedEntry)) ? /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.CAPITALIZATION_FISCAL_PERIOD_CODES, scrubbedEntry.getUniversityFiscalPeriodCode()) : null;
            ParameterEvaluator objectSubTypeCodes = (!ObjectUtils.isNull(scrubbedEntryObjectCode)) ? /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.CAPITALIZATION_OBJ_SUB_TYPE_CODES, scrubbedEntryObjectCode.getFinancialObjectSubTypeCode()) : null;
            ParameterEvaluator subFundGroupCodes = (!ObjectUtils.isNull(scrubbedEntryAccount)) ? /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.CAPITALIZATION_SUB_FUND_GROUP_CODES, scrubbedEntryAccount.getSubFundGroupCode()) : null;
            ParameterEvaluator chartCodes = (!ObjectUtils.isNull(scrubbedEntry)) ? /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.CAPITALIZATION_CHART_CODES, scrubbedEntry.getChartOfAccountsCode()) : null;

            if (scrubbedEntry.getFinancialBalanceTypeCode().equals(scrubbedEntryOption.getActualFinancialBalanceTypeCd()) && scrubbedEntry.getUniversityFiscalYear().intValue() > 1995 && (documentTypeCodes != null && documentTypeCodes.evaluationSucceeds()) && (fiscalPeriodCodes != null && fiscalPeriodCodes.evaluationSucceeds()) && (objectSubTypeCodes != null && objectSubTypeCodes.evaluationSucceeds()) && (subFundGroupCodes != null && subFundGroupCodes.evaluationSucceeds()) && (chartCodes != null && chartCodes.evaluationSucceeds())) {

                String objectSubTypeCode = scrubbedEntryObjectCode.getFinancialObjectSubTypeCode();

                String capitalizationObjectCode = parameterService.getSubParameterValueAsString(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupParameters.CAPITALIZATION_SUBTYPE_OBJECT, objectSubTypeCode);
                if ( org.apache.commons.lang.StringUtils.isNotBlank( capitalizationObjectCode ) ) {
                    capitalizationEntry.setFinancialObjectCode(capitalizationObjectCode);
                    capitalizationEntry.setFinancialObject(accountingCycleCachingService.getObjectCode(capitalizationEntry.getUniversityFiscalYear(), capitalizationEntry.getChartOfAccountsCode(), capitalizationEntry.getFinancialObjectCode()));
                }

                // financialSubObjectCode should always be changed to dashes for capitalization entries
                capitalizationEntry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());

                capitalizationEntry.setFinancialObjectTypeCode(scrubbedEntryOption.getFinancialObjectTypeAssetsCd());
                capitalizationEntry.setTransactionLedgerEntryDescription(capitalizationDescription);

                plantFundAccountLookup(scrubbedEntry, capitalizationEntry);

                capitalizationEntry.setUniversityFiscalPeriodCode(scrubbedEntry.getUniversityFiscalPeriodCode());

                createOutputEntry(capitalizationEntry, OUTPUT_GLE_FILE_ps);
                scrubberReport.incrementCapitalizationEntryGenerated();

                // Clear out the id & the ojb version number to make sure we do an insert on the next one
                capitalizationEntry.setVersionNumber(null);
                capitalizationEntry.setEntryId(null);

                // Check system parameters for overriding fund balance object code; otherwise, use
                // the chart fund balance object code.
                String fundBalanceCode    = parameterService.getParameterValueAsString(
                        ScrubberStep.class,
                        GlParameterConstants.CAPITALIZATION_OFFSET_CODE);

                ObjectCode fundObjectCode = getFundBalanceObjectCode(fundBalanceCode, capitalizationEntry);

                if (fundObjectCode != null) {
                    capitalizationEntry.setFinancialObjectTypeCode(fundObjectCode.getFinancialObjectTypeCode());
                    capitalizationEntry.setFinancialObjectCode(fundBalanceCode);
                }
                else {
                    capitalizationEntry.setFinancialObjectCode(scrubbedEntryChart.getFundBalanceObjectCode());
                    //TODO: check to see if COBOL does this - seems weird - is this saying if the object code doesn't exist use the value from options?  Shouldn't it always come from one or the other?
                    if (ObjectUtils.isNotNull(scrubbedEntryChart.getFundBalanceObject())) {
                        capitalizationEntry.setFinancialObjectTypeCode(scrubbedEntryChart.getFundBalanceObject().getFinancialObjectTypeCode());
                    }
                    else {
                        capitalizationEntry.setFinancialObjectTypeCode(scrubbedEntryOption.getFinObjectTypeFundBalanceCd());
                    }
                }

                populateTransactionDebtCreditCode(scrubbedEntry, capitalizationEntry);

                try {
                    flexibleOffsetAccountService.updateOffset(capitalizationEntry);
                }
                catch (InvalidFlexibleOffsetException e) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("processCapitalization() Capitalization Flexible Offset Error: " + e.getMessage());
                    }
                    return e.getMessage();
                }

                createOutputEntry(capitalizationEntry, OUTPUT_GLE_FILE_ps);
                scrubberReport.incrementCapitalizationEntryGenerated();
            }
        } catch (IOException ioe) {
            LOG.error("processCapitalization() Stopped: " + ioe.getMessage());
            throw new RuntimeException("processCapitalization() Stopped: " + ioe.getMessage(), ioe);
        }
        return null;
    }

    /**
     * Generates the plant indebtedness entries
     *
     * @param scrubbedEntry the entry to generated plant indebtedness entries for if necessary
     * @return null if no error, message if error
     */
    protected String processPlantIndebtedness(OriginEntryInformation scrubbedEntry, ScrubberReportData scrubberReport) {
        try{
            // Lines 4855 - 4979 of the Pro Cobol listing on Confluence
            if (!parameterService.getParameterValueAsBoolean(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupParameters.PLANT_INDEBTEDNESS_IND)) {
                return null;
            }

            OriginEntryFull plantIndebtednessEntry = OriginEntryFull.copyFromOriginEntryable(scrubbedEntry);

            SystemOptions scrubbedEntryOption = accountingCycleCachingService.getSystemOptions(scrubbedEntry.getUniversityFiscalYear());
            ObjectCode scrubbedEntryObjectCode = accountingCycleCachingService.getObjectCode(scrubbedEntry.getUniversityFiscalYear(), scrubbedEntry.getChartOfAccountsCode(), scrubbedEntry.getFinancialObjectCode());
            Account scrubbedEntryAccount = accountingCycleCachingService.getAccount(scrubbedEntry.getChartOfAccountsCode(), scrubbedEntry.getAccountNumber());
            Chart scrubbedEntryChart = accountingCycleCachingService.getChart(scrubbedEntry.getChartOfAccountsCode());
            if (!ObjectUtils.isNull(scrubbedEntryAccount)) {
                scrubbedEntryAccount.setOrganization(accountingCycleCachingService.getOrganization(scrubbedEntryAccount.getChartOfAccountsCode(), scrubbedEntryAccount.getOrganizationCode()));
            }

            ParameterEvaluator objectSubTypeCodes = (!ObjectUtils.isNull(scrubbedEntryObjectCode)) ? /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.PLANT_INDEBTEDNESS_OBJ_SUB_TYPE_CODES, scrubbedEntryObjectCode.getFinancialObjectSubTypeCode()) : null;
            ParameterEvaluator subFundGroupCodes = (!ObjectUtils.isNull(scrubbedEntryAccount)) ? /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.PLANT_INDEBTEDNESS_SUB_FUND_GROUP_CODES, scrubbedEntryAccount.getSubFundGroupCode()) : null;

            if (scrubbedEntry.getFinancialBalanceTypeCode().equals(scrubbedEntryOption.getActualFinancialBalanceTypeCd()) && (subFundGroupCodes != null && subFundGroupCodes.evaluationSucceeds()) && (objectSubTypeCodes != null && objectSubTypeCodes.evaluationSucceeds())) {

                plantIndebtednessEntry.setTransactionLedgerEntryDescription(KFSConstants.PLANT_INDEBTEDNESS_ENTRY_DESCRIPTION);
                populateTransactionDebtCreditCode(scrubbedEntry, plantIndebtednessEntry);

                plantIndebtednessEntry.setTransactionScrubberOffsetGenerationIndicator(true);
                createOutputEntry(plantIndebtednessEntry, OUTPUT_GLE_FILE_ps);
                scrubberReport.incrementPlantIndebtednessEntryGenerated();

                // Clear out the id & the ojb version number to make sure we do an insert on the next one
                plantIndebtednessEntry.setVersionNumber(null);
                plantIndebtednessEntry.setEntryId(null);

                // Check system parameters for overriding fund balance object code; otherwise, use
                // the chart fund balance object code.
                String fundBalanceCode    = parameterService.getParameterValueAsString(
                        ScrubberStep.class,
                        GlParameterConstants.PLANT_INDEBTEDNESS_OFFSET_CODE);

                ObjectCode fundObjectCode = getFundBalanceObjectCode(fundBalanceCode, plantIndebtednessEntry);
                if (fundObjectCode != null) {
                    plantIndebtednessEntry.setFinancialObjectTypeCode(fundObjectCode.getFinancialObjectTypeCode());
                    plantIndebtednessEntry.setFinancialObjectCode(fundBalanceCode);
                }
                else {
                    plantIndebtednessEntry.setFinancialObjectTypeCode(scrubbedEntryOption.getFinObjectTypeFundBalanceCd());
                    plantIndebtednessEntry.setFinancialObjectCode(scrubbedEntryChart.getFundBalanceObjectCode());
                }

                plantIndebtednessEntry.setTransactionDebitCreditCode(scrubbedEntry.getTransactionDebitCreditCode());

                plantIndebtednessEntry.setTransactionScrubberOffsetGenerationIndicator(true);
                plantIndebtednessEntry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());

                try {
                    flexibleOffsetAccountService.updateOffset(plantIndebtednessEntry);
                }
                catch (InvalidFlexibleOffsetException e) {
                    LOG.error("processPlantIndebtedness() Flexible Offset Exception (1)", e);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("processPlantIndebtedness() Plant Indebtedness Flexible Offset Error: " + e.getMessage());
                    }
                    return e.getMessage();
                }

                createOutputEntry(plantIndebtednessEntry, OUTPUT_GLE_FILE_ps);
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

                plantIndebtednessEntry.setAccountNumber(scrubbedEntryAccount.getOrganization().getCampusPlantAccountNumber());
                plantIndebtednessEntry.setChartOfAccountsCode(scrubbedEntryAccount.getOrganization().getCampusPlantChartCode());

                plantIndebtednessEntry.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
                plantIndebtednessEntry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());

                StringBuffer litGenPlantXferFrom = new StringBuffer();
                litGenPlantXferFrom.append(transferDescription + " ");
                litGenPlantXferFrom.append(scrubbedEntry.getChartOfAccountsCode()).append(" ");
                litGenPlantXferFrom.append(scrubbedEntry.getAccountNumber());
                plantIndebtednessEntry.setTransactionLedgerEntryDescription(litGenPlantXferFrom.toString());

                createOutputEntry(plantIndebtednessEntry, OUTPUT_GLE_FILE_ps);
                scrubberReport.incrementPlantIndebtednessEntryGenerated();

                // Clear out the id & the ojb version number to make sure we do an insert on the next one
                plantIndebtednessEntry.setVersionNumber(null);
                plantIndebtednessEntry.setEntryId(null);

                plantIndebtednessEntry.setFinancialObjectCode(scrubbedEntryChart.getFundBalanceObjectCode());
                plantIndebtednessEntry.setFinancialObjectTypeCode(scrubbedEntryOption.getFinObjectTypeFundBalanceCd());
                plantIndebtednessEntry.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());

                populateTransactionDebtCreditCode(scrubbedEntry, plantIndebtednessEntry);

                try {
                    flexibleOffsetAccountService.updateOffset(plantIndebtednessEntry);
                }
                catch (InvalidFlexibleOffsetException e) {
                    LOG.error("processPlantIndebtedness() Flexible Offset Exception (2)", e);
                    LOG.debug("processPlantIndebtedness() Plant Indebtedness Flexible Offset Error: " + e.getMessage());
                    return e.getMessage();
                }

                createOutputEntry(plantIndebtednessEntry, OUTPUT_GLE_FILE_ps);
                scrubberReport.incrementPlantIndebtednessEntryGenerated();
            }
        } catch (IOException ioe) {
            LOG.error("processPlantIndebtedness() Stopped: " + ioe.getMessage());
            throw new RuntimeException("processPlantIndebtedness() Stopped: " + ioe.getMessage(), ioe);
        }
        return null;
    }

    /**
     * Generate the liability entries for the entry if necessary
     *
     * @param scrubbedEntry the entry to generate liability entries for if necessary
     * @return null if no error, message if error
     */
    protected String processLiabilities(OriginEntryInformation scrubbedEntry, ScrubberReportData scrubberReport) {
        try{
            // Lines 4799 to 4839 of the Pro Cobol list of the scrubber on Confluence
            if (!parameterService.getParameterValueAsBoolean(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupParameters.LIABILITY_IND)) {
                return null;
            }

            Chart scrubbedEntryChart = accountingCycleCachingService.getChart(scrubbedEntry.getChartOfAccountsCode());
            SystemOptions scrubbedEntryOption = accountingCycleCachingService.getSystemOptions(scrubbedEntry.getUniversityFiscalYear());
            ObjectCode scrubbedEntryFinancialObject = accountingCycleCachingService.getObjectCode(scrubbedEntry.getUniversityFiscalYear(), scrubbedEntry.getChartOfAccountsCode(), scrubbedEntry.getFinancialObjectCode());
            Account scrubbedEntryAccount = accountingCycleCachingService.getAccount(scrubbedEntry.getChartOfAccountsCode(), scrubbedEntry.getAccountNumber());

            ParameterEvaluator chartCodes = (!ObjectUtils.isNull(scrubbedEntry)) ? /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.LIABILITY_CHART_CODES, scrubbedEntry.getChartOfAccountsCode()) : null;
            ParameterEvaluator docTypeCodes = (!ObjectUtils.isNull(scrubbedEntry)) ? /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.LIABILITY_DOC_TYPE_CODES, scrubbedEntry.getFinancialDocumentTypeCode()) : null;
            ParameterEvaluator fiscalPeriods = (!ObjectUtils.isNull(scrubbedEntry)) ? /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.LIABILITY_FISCAL_PERIOD_CODES, scrubbedEntry.getUniversityFiscalPeriodCode()) : null;
            ParameterEvaluator objSubTypeCodes = (!ObjectUtils.isNull(scrubbedEntryFinancialObject)) ? /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.LIABILITY_OBJ_SUB_TYPE_CODES, scrubbedEntryFinancialObject.getFinancialObjectSubTypeCode()) : null;
            ParameterEvaluator subFundGroupCodes = (!ObjectUtils.isNull(scrubbedEntryAccount)) ? /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.LIABILITY_SUB_FUND_GROUP_CODES, scrubbedEntryAccount.getSubFundGroupCode()) : null;

            if (scrubbedEntry.getFinancialBalanceTypeCode().equals(scrubbedEntryOption.getActualFinancialBalanceTypeCd()) && scrubbedEntry.getUniversityFiscalYear().intValue() > 1995 && (docTypeCodes != null && docTypeCodes.evaluationSucceeds()) && (fiscalPeriods != null && fiscalPeriods.evaluationSucceeds()) && (objSubTypeCodes != null && objSubTypeCodes.evaluationSucceeds()) && (subFundGroupCodes != null && subFundGroupCodes.evaluationSucceeds()) && (chartCodes != null && chartCodes.evaluationSucceeds())) {
                OriginEntryFull liabilityEntry = OriginEntryFull.copyFromOriginEntryable(scrubbedEntry);

                liabilityEntry.setFinancialObjectCode(parameterService.getParameterValueAsString(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupParameters.LIABILITY_OBJECT_CODE));
                liabilityEntry.setFinancialObjectTypeCode(scrubbedEntryOption.getFinObjectTypeLiabilitiesCode());

                liabilityEntry.setTransactionDebitCreditCode(scrubbedEntry.getTransactionDebitCreditCode());
                liabilityEntry.setTransactionLedgerEntryDescription(liabilityDescription);
                plantFundAccountLookup(scrubbedEntry, liabilityEntry);

                createOutputEntry(liabilityEntry, OUTPUT_GLE_FILE_ps);
                scrubberReport.incrementLiabilityEntryGenerated();

                // Clear out the id & the ojb version number to make sure we do an insert on the next one
                liabilityEntry.setVersionNumber(null);
                liabilityEntry.setEntryId(null);

                // Check system parameters for overriding fund balance object code; otherwise, use
                // the chart fund balance object code.
                String fundBalanceCode    = parameterService.getParameterValueAsString(
                        ScrubberStep.class,
                        GlParameterConstants.LIABILITY_OFFSET_CODE);

                ObjectCode fundObjectCode = getFundBalanceObjectCode(fundBalanceCode, liabilityEntry);
                if (fundObjectCode != null) {
                    liabilityEntry.setFinancialObjectTypeCode(fundObjectCode.getFinancialObjectTypeCode());
                    liabilityEntry.setFinancialObjectCode(fundBalanceCode);
                }
                else {
                    // ... and now generate the offset half of the liability entry
                    liabilityEntry.setFinancialObjectCode(scrubbedEntryChart.getFundBalanceObjectCode());
                    if (ObjectUtils.isNotNull(scrubbedEntryChart.getFundBalanceObject())) {
                        liabilityEntry.setFinancialObjectTypeCode(scrubbedEntryChart.getFundBalanceObject().getFinancialObjectTypeCode());
                    }
                    else {
                        liabilityEntry.setFinancialObjectTypeCode(scrubbedEntryOption.getFinObjectTypeFundBalanceCd());
                    }
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
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("processLiabilities() Liability Flexible Offset Error: " + e.getMessage());
                    }
                    return e.getMessage();
                }

                createOutputEntry(liabilityEntry, OUTPUT_GLE_FILE_ps);
                scrubberReport.incrementLiabilityEntryGenerated();
            }
        } catch (IOException ioe) {
            LOG.error("processLiabilities() Stopped: " + ioe.getMessage());
            throw new RuntimeException("processLiabilities() Stopped: " + ioe.getMessage(), ioe);
        }
        return null;
    }

    /**
     *
     * This method...
     * @param fundBalanceCodeParameter
     * @param originEntryFull
     * @return
     */
    protected ObjectCode getFundBalanceObjectCode(String fundBalanceCode, OriginEntryFull originEntryFull)
    {
        ObjectCode fundBalanceObjectCode = null;
        if (fundBalanceCode != null) {
            Map<String, Object> criteriaMap = new HashMap<String, Object>();
            criteriaMap.put("universityFiscalYear", originEntryFull.getUniversityFiscalYear());
            criteriaMap.put("chartOfAccountsCode", originEntryFull.getChartOfAccountsCode());
            criteriaMap.put("financialObjectCode",  fundBalanceCode);

            fundBalanceObjectCode = businessObjectService.findByPrimaryKey(ObjectCode.class, criteriaMap);            
        }

        return fundBalanceObjectCode;
    }

    /**
     *
     * This method...
     * @param scrubbedEntry
     * @param fullEntry
     */
    protected void populateTransactionDebtCreditCode(OriginEntryInformation scrubbedEntry, OriginEntryFull fullEntry)
    {
        if (scrubbedEntry.isDebit()) {
            fullEntry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
        }
        else {
            fullEntry.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
        }
    }

    /**
     * Updates the entries with the proper chart and account for the plant fund
     *
     * @param scrubbedEntry basis for plant fund entry
     * @param liabilityEntry liability entry
     */
    protected void plantFundAccountLookup(OriginEntryInformation scrubbedEntry, OriginEntryFull liabilityEntry) {
        // 4000-PLANT-FUND-ACCT to 4000-PLANT-FUND-ACCT-EXIT in cobol

        liabilityEntry.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
        ObjectCode scrubbedEntryObjectCode = accountingCycleCachingService.getObjectCode(scrubbedEntry.getUniversityFiscalYear(), scrubbedEntry.getChartOfAccountsCode(), scrubbedEntry.getFinancialObjectCode());
        Account scrubbedEntryAccount = accountingCycleCachingService.getAccount(scrubbedEntry.getChartOfAccountsCode(), scrubbedEntry.getAccountNumber());
        scrubbedEntryAccount.setOrganization(accountingCycleCachingService.getOrganization(scrubbedEntryAccount.getChartOfAccountsCode(), scrubbedEntryAccount.getOrganizationCode()));

        if (!ObjectUtils.isNull(scrubbedEntryAccount) && !ObjectUtils.isNull(scrubbedEntryObjectCode)) {
            String objectSubTypeCode = scrubbedEntryObjectCode.getFinancialObjectSubTypeCode();
            ParameterEvaluator campusObjSubTypeCodes = /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.PLANT_FUND_CAMPUS_OBJECT_SUB_TYPE_CODES, objectSubTypeCode);
            ParameterEvaluator orgObjSubTypeCodes = /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.PLANT_FUND_ORG_OBJECT_SUB_TYPE_CODES, objectSubTypeCode);

            if (campusObjSubTypeCodes.evaluationSucceeds()) {
                liabilityEntry.setAccountNumber(scrubbedEntryAccount.getOrganization().getCampusPlantAccountNumber());
                liabilityEntry.setChartOfAccountsCode(scrubbedEntryAccount.getOrganization().getCampusPlantChartCode());
            }
            else if (orgObjSubTypeCodes.evaluationSucceeds()) {
                liabilityEntry.setAccountNumber(scrubbedEntryAccount.getOrganization().getOrganizationPlantAccountNumber());
                liabilityEntry.setChartOfAccountsCode(scrubbedEntryAccount.getOrganization().getOrganizationPlantChartCode());
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
    protected TransactionError generateCostShareEncumbranceEntries(OriginEntryInformation scrubbedEntry, ScrubberReportData scrubberReport) {
        try{
            // 3200-COST-SHARE-ENC to 3200-CSE-EXIT in the COBOL
            LOG.debug("generateCostShareEncumbranceEntries() started");

            OriginEntryFull costShareEncumbranceEntry = OriginEntryFull.copyFromOriginEntryable(scrubbedEntry);

            // First 28 characters of the description, padding to 28 if shorter)
            StringBuffer buffer = new StringBuffer((scrubbedEntry.getTransactionLedgerEntryDescription() + GeneralLedgerConstants.getSpaceTransactionLedgetEntryDescription()).substring(0, COST_SHARE_ENCUMBRANCE_ENTRY_MAXLENGTH));

            buffer.append("FR-");
            buffer.append(costShareEncumbranceEntry.getChartOfAccountsCode());
            buffer.append(costShareEncumbranceEntry.getAccountNumber());

            costShareEncumbranceEntry.setTransactionLedgerEntryDescription(buffer.toString());

            A21SubAccount scrubbedEntryA21SubAccount = accountingCycleCachingService.getA21SubAccount(scrubbedEntry.getChartOfAccountsCode(), scrubbedEntry.getAccountNumber(), scrubbedEntry.getSubAccountNumber());
            SystemOptions scrubbedEntryOption = accountingCycleCachingService.getSystemOptions(scrubbedEntry.getUniversityFiscalYear());

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
            createOutputEntry(costShareEncumbranceEntry, OUTPUT_GLE_FILE_ps);
            scrubberReport.incrementCostShareEncumbranceGenerated();

            OriginEntryFull costShareEncumbranceOffsetEntry = new OriginEntryFull(costShareEncumbranceEntry);
            costShareEncumbranceOffsetEntry.setTransactionLedgerEntryDescription(offsetDescription);
            OffsetDefinition offset = accountingCycleCachingService.getOffsetDefinition(costShareEncumbranceEntry.getUniversityFiscalYear(), costShareEncumbranceEntry.getChartOfAccountsCode(), costShareEncumbranceEntry.getFinancialDocumentTypeCode(), costShareEncumbranceEntry.getFinancialBalanceTypeCode());

            if (offset != null) {
                if (offset.getFinancialObject() == null) {
                    StringBuffer offsetKey = new StringBuffer();
                    offsetKey.append(offset.getUniversityFiscalYear());
                    offsetKey.append("-");
                    offsetKey.append(offset.getChartOfAccountsCode());
                    offsetKey.append("-");
                    offsetKey.append(offset.getFinancialObjectCode());

                    LOG.debug("generateCostShareEncumbranceEntries() object code not found");
                    return new TransactionError(costShareEncumbranceEntry, new Message(configurationService.getPropertyValueAsString(KFSKeyConstants.ERROR_NO_OBJECT_FOR_OBJECT_ON_OFSD) + "(" + offsetKey.toString() + ")", Message.TYPE_FATAL));
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
                return new TransactionError(costShareEncumbranceEntry, new Message(configurationService.getPropertyValueAsString(KFSKeyConstants.ERROR_OFFSET_DEFINITION_NOT_FOUND) + "(" + offsetKey.toString() + ")", Message.TYPE_FATAL));
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
                if (LOG.isDebugEnabled()) {
                    LOG.debug("generateCostShareEncumbranceEntries() Cost Share Encumbrance Flexible Offset Error: " + e.getMessage());
                }
                return new TransactionError(costShareEncumbranceOffsetEntry, m);
            }

            createOutputEntry(costShareEncumbranceOffsetEntry, OUTPUT_GLE_FILE_ps);
            scrubberReport.incrementCostShareEncumbranceGenerated();
        } catch (IOException ioe) {
            LOG.error("generateCostShareEncumbranceEntries() Stopped: " + ioe.getMessage());
            throw new RuntimeException("generateCostShareEncumbranceEntries() Stopped: " + ioe.getMessage(), ioe);
        }
        LOG.debug("generateCostShareEncumbranceEntries() returned successfully");
        return null;
    }

    /**
     * Sets the proper cost share object code in an entry and its offset
     *
     * @param costShareEntry GL Entry for cost share
     * @param originEntry Scrubbed GL Entry that this is based on
     */
    @Override
    public void setCostShareObjectCode(OriginEntryFull costShareEntry, OriginEntryInformation originEntry) {
        ObjectCode originEntryFinancialObject = accountingCycleCachingService.getObjectCode(originEntry.getUniversityFiscalYear(), originEntry.getChartOfAccountsCode(), originEntry.getFinancialObjectCode());

        if (originEntryFinancialObject == null) {
            addTransactionError(configurationService.getPropertyValueAsString(KFSKeyConstants.ERROR_OBJECT_CODE_NOT_FOUND), originEntry.getFinancialObjectCode(), Message.TYPE_FATAL);
        }

        String originEntryObjectLevelCode = (originEntryFinancialObject == null) ? "" : originEntryFinancialObject.getFinancialObjectLevelCode();

        String financialOriginEntryObjectCode = originEntry.getFinancialObjectCode();
        //String originEntryObjectCode = scrubberProcessObjectCodeOverride.getOriginEntryObjectCode(originEntryObjectLevelCode, financialOriginEntryObjectCode);

        // General rules
        String param = parameterService.getSubParameterValueAsString(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupParameters.COST_SHARE_OBJECT_CODE_BY_LEVEL_PARM_NM, originEntryObjectLevelCode);
        if (param == null) {
            param = parameterService.getSubParameterValueAsString(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupParameters.COST_SHARE_OBJECT_CODE_BY_LEVEL_PARM_NM, "DEFAULT");
            if (param == null) {
                throw new RuntimeException("Unable to determine cost sharing object code from object level.  Default entry missing.");
            }
        }
        financialOriginEntryObjectCode = param;

        // Lookup the new object code
        ObjectCode objectCode = accountingCycleCachingService.getObjectCode(costShareEntry.getUniversityFiscalYear(), costShareEntry.getChartOfAccountsCode(), financialOriginEntryObjectCode);
        if (objectCode != null) {
            costShareEntry.setFinancialObjectTypeCode(objectCode.getFinancialObjectTypeCode());
            costShareEntry.setFinancialObjectCode(financialOriginEntryObjectCode);
        }
        else {
            addTransactionError(configurationService.getPropertyValueAsString(KFSKeyConstants.ERROR_COST_SHARE_OBJECT_NOT_FOUND), costShareEntry.getFinancialObjectCode(), Message.TYPE_FATAL);
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
    protected boolean generateOffset(OriginEntryInformation scrubbedEntry, ScrubberReportData scrubberReport) {
        OriginEntryFull offsetEntry = new OriginEntryFull();
        try{
         // This code is 3000-OFFSET to SET-OBJECT-2004 in the Cobol
            LOG.debug("generateOffset() started");

            // There was no previous unit of work so we need no offset
            if (scrubbedEntry == null) {
                return true;
            }

            // If there was an error, don't generate an offset since the record was pulled
            // and the rest of the document's records will be demerged
            if (unitOfWork.errorsFound == true) {
                return true;
            }

            // If the offset amount is zero, don't bother to lookup the offset definition ...
            if (unitOfWork.offsetAmount.isZero()) {
                return true;
            }

            ParameterEvaluator docTypeRule = /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.OFFSET_DOC_TYPE_CODES, scrubbedEntry.getFinancialDocumentTypeCode());
            if (!docTypeRule.evaluationSucceeds()) {
                return true;
            }

            // do nothing if flexible offset is enabled and scrubber offset indicator of the document
            // type code is turned off in the document type table
            if (flexibleOffsetAccountService.getEnabled() && !shouldScrubberGenerateOffsetsForDocType(scrubbedEntry.getFinancialDocumentTypeCode())) {
                return true;
            }

            // Create an offset
            offsetEntry = OriginEntryFull.copyFromOriginEntryable(scrubbedEntry);
            offsetEntry.setTransactionLedgerEntryDescription(offsetDescription);

            //of course this method should go elsewhere, not in ScrubberValidator!
            OffsetDefinition offsetDefinition = accountingCycleCachingService.getOffsetDefinition(scrubbedEntry.getUniversityFiscalYear(), scrubbedEntry.getChartOfAccountsCode(), scrubbedEntry.getFinancialDocumentTypeCode(), scrubbedEntry.getFinancialBalanceTypeCode());
            if (offsetDefinition != null) {
                if (offsetDefinition.getFinancialObject() == null) {
                    StringBuffer offsetKey = new StringBuffer(offsetDefinition.getUniversityFiscalYear());
                    offsetKey.append("-");
                    offsetKey.append(offsetDefinition.getChartOfAccountsCode());
                    offsetKey.append("-");
                    offsetKey.append(offsetDefinition.getFinancialObjectCode());

                    putTransactionError(offsetEntry, configurationService.getPropertyValueAsString(KFSKeyConstants.ERROR_OFFSET_DEFINITION_OBJECT_CODE_NOT_FOUND), offsetKey.toString(), Message.TYPE_FATAL);

                    createOutputEntry(offsetEntry, OUTPUT_ERR_FILE_ps);
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

                putTransactionError(offsetEntry, configurationService.getPropertyValueAsString(KFSKeyConstants.ERROR_OFFSET_DEFINITION_NOT_FOUND), sb.toString(), Message.TYPE_FATAL);

                createOutputEntry(offsetEntry, OUTPUT_ERR_FILE_ps);
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
            offsetEntry.setReferenceFinancialSystemOriginationCode(null);
            offsetEntry.setReferenceFinancialDocumentNumber(null);
            offsetEntry.setTransactionEncumbranceUpdateCode(null);
            offsetEntry.setProjectCode(KFSConstants.getDashProjectCode());
            offsetEntry.setTransactionDate(getTransactionDateForOffsetEntry(scrubbedEntry));

            try {
                flexibleOffsetAccountService.updateOffset(offsetEntry);
            }
            catch (InvalidFlexibleOffsetException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("generateOffset() Offset Flexible Offset Error: " + e.getMessage());
                }
                putTransactionError(offsetEntry, e.getMessage(), "", Message.TYPE_FATAL);
                return true;
            }

            createOutputEntry(offsetEntry, OUTPUT_GLE_FILE_ps);
            scrubberReport.incrementOffsetEntryGenerated();

        } catch (IOException ioe) {
            LOG.error("generateOffset() Stopped: " + ioe.getMessage());
            throw new RuntimeException("generateOffset() Stopped: " + ioe.getMessage(), ioe);
        }

        return true;
    }


    protected void createOutputEntry(OriginEntryInformation entry, PrintStream ps) throws IOException {
        try {
            ps.printf("%s\n", entry.getLine());
        } catch (Exception e) {
            throw new IOException(e.toString());
        }
    }

    protected void createOutputEntry(String line, PrintStream ps) throws IOException {
        try {
            ps.printf("%s\n", line);
        } catch (Exception e) {
            throw new IOException(e.toString());
        }
    }

    /**
     * Add an error message to the list of messages for this transaction
     *
     * @param errorMessage Error message
     * @param errorValue Value that is in error
     * @param type Type of error (Fatal or Warning)
     */
    protected void addTransactionError(String errorMessage, String errorValue, int type) {
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
    protected void putTransactionError(Transaction s, String errorMessage, String errorValue, int type) {
        Message m = new Message(errorMessage + "(" + errorValue + ")", type);
        scrubberReportWriterService.writeError(s, m);
    }

    /**
     * Determines if the scrubber should generate offsets for the given document type
     * @param docTypeCode the document type code to check if it generates scrubber offsets
     * @return true if the scrubber should generate offsets for this doc type, false otherwise
     */
    protected boolean shouldScrubberGenerateOffsetsForDocType(String docTypeCode) {
        return /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.DOCUMENT_TYPES_REQUIRING_FLEXIBLE_OFFSET_BALANCING_ENTRIES, docTypeCode).evaluationSucceeds();
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
        public boolean errorsFound = false;
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
        public UnitOfWorkInfo(OriginEntryInformation e) {
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
        public boolean isSameUnitOfWork(OriginEntryInformation e) {
            // Compare the key fields
            return univFiscalYr.equals(e.getUniversityFiscalYear()) && finCoaCd.equals(e.getChartOfAccountsCode()) && accountNbr.equals(e.getAccountNumber()) && subAcctNbr.equals(e.getSubAccountNumber()) && finBalanceTypCd.equals(e.getFinancialBalanceTypeCode()) && fdocTypCd.equals(e.getFinancialDocumentTypeCode()) && fsOriginCd.equals(e.getFinancialSystemOriginationCode()) && fdocNbr.equals(e.getDocumentNumber()) && ObjectHelper.isEqual(fdocReversalDt, e.getFinancialDocumentReversalDate()) && univFiscalPrdCd.equals(e.getUniversityFiscalPeriodCode());
        }

        /**
         * Converts this unit of work info to a String
         * @return a String representation of this UnitOfWorkInfo
         * @see java.lang.Object#toString()
         */
        @Override
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
    @Override
    public Date calculateRunDate(java.util.Date currentDate) {
        return new Date(runDateService.calculateRunDate(currentDate).getTime());
    }

    protected boolean checkingBypassEntry (String financialBalanceTypeCode, String desc, DemergerReportData demergerReport){
        String transactionType = getTransactionType(financialBalanceTypeCode, desc);

        if (TRANSACTION_TYPE_COST_SHARE_ENCUMBRANCE.equals(transactionType)) {
            demergerReport.incrementCostShareEncumbranceTransactionsBypassed();
            return true;
        }
        else if (TRANSACTION_TYPE_OFFSET.equals(transactionType)) {
            demergerReport.incrementOffsetTransactionsBypassed();
            return true;
        }
        else if (TRANSACTION_TYPE_CAPITALIZATION.equals(transactionType)) {
            demergerReport.incrementCapitalizationTransactionsBypassed();
            return true;
        }
        else if (TRANSACTION_TYPE_LIABILITY.equals(transactionType)) {
            demergerReport.incrementLiabilityTransactionsBypassed();
            return true;
        }
        else if (TRANSACTION_TYPE_TRANSFER.equals(transactionType)) {
            demergerReport.incrementTransferTransactionsBypassed();
            return true;
        }
        else if (TRANSACTION_TYPE_COST_SHARE.equals(transactionType)) {
            demergerReport.incrementCostShareTransactionsBypassed();
            return true;
        }

        return false;
    }


    protected String checkAndSetTransactionTypeCostShare (String financialBalanceTypeCode, String desc, String currentValidLine){

        // Read all the transactions in the valid group and update the cost share transactions
        String transactionType = getTransactionType(financialBalanceTypeCode, desc);
        if (TRANSACTION_TYPE_COST_SHARE.equals(transactionType)) {
            OriginEntryFull transaction = new OriginEntryFull();
            transaction.setFromTextFileForBatch(currentValidLine, 0);

            transaction.setFinancialDocumentTypeCode(KFSConstants.TRANSFER_FUNDS);
            transaction.setFinancialSystemOriginationCode(KFSConstants.SubAccountType.COST_SHARE);
            StringBuffer docNbr = new StringBuffer(COST_SHARE_CODE);

            docNbr.append(desc.substring(36, 38));
            docNbr.append("/");
            docNbr.append(desc.substring(38, 40));
            transaction.setDocumentNumber(docNbr.toString());
            transaction.setTransactionLedgerEntryDescription(desc.substring(0, DEMERGER_TRANSACTION_LEDGET_ENTRY_DESCRIPTION));

            currentValidLine = transaction.getLine();
       }

        return currentValidLine;

    }


    /**
     * Generates the scrubber listing report for the GLCP document
     * @param documentNumber the document number of the GLCP document
     */
    protected void generateScrubberTransactionListingReport(String documentNumber, String inputFileName) {
        try {
            scrubberListingReportWriterService.setDocumentNumber(documentNumber);
        ((WrappingBatchService) scrubberListingReportWriterService).initialize();
        new TransactionListingReport().generateReport(scrubberListingReportWriterService, new OriginEntryFileIterator(new File(inputFileName)));
        } finally {
        ((WrappingBatchService) scrubberListingReportWriterService).destroy();
    }
    }

    /**
     * Generates the scrubber report that lists out the input origin entries with blank balance type codes.
     */
    protected void generateScrubberBlankBalanceTypeCodeReport(String inputFileName) {
        OriginEntryFilter blankBalanceTypeFilter = new OriginEntryFilter() {
            /**
             * @see org.kuali.kfs.gl.batch.service.impl.FilteringOriginEntryFileIterator.OriginEntryFilter#accept(org.kuali.kfs.gl.businessobject.OriginEntryFull)
             */
            @Override
            public boolean accept(OriginEntryFull originEntry) {
                boolean acceptFlag = false;
                String financialBalancetype = originEntry.getFinancialBalanceTypeCode();
                BalanceType originEntryBalanceType = accountingCycleCachingService.getBalanceType(financialBalancetype);
                if (ObjectUtils.isNull(originEntryBalanceType)) {
                    acceptFlag = true;
                    for (int i= 0; i < financialBalancetype.length(); i++) {
                        if (financialBalancetype.charAt(i) != ' ') { acceptFlag = false; break;}
                    }
                }
                return acceptFlag;
            }
        };
        Iterator<OriginEntryFull> blankBalanceOriginEntries = new FilteringOriginEntryFileIterator(new File(inputFileName), blankBalanceTypeFilter);
        new TransactionListingReport().generateReport(scrubberBadBalanceListingReportWriterService, blankBalanceOriginEntries);
    }

    protected void generateDemergerRemovedTransactionsReport(String errorFileName) {
        OriginEntryFileIterator removedTransactions = new OriginEntryFileIterator(new File(errorFileName));
        new TransactionListingReport().generateReport(demergerRemovedTransactionsListingReportWriterService, removedTransactions);
    }

    protected void handleTransactionError(Transaction errorTransaction, Message message) {
        if (collectorMode) {
            List<Message> messages = scrubberReportErrors.get(errorTransaction);
            if (messages == null) {
                messages = new ArrayList<Message>();
                scrubberReportErrors.put(errorTransaction, messages);
            }
            messages.add(message);
        }
        else {
            scrubberReportWriterService.writeError(errorTransaction, message);
        }
    }

    protected void handleTransactionErrors(Transaction errorTransaction, List<Message> messages) {
        if (collectorMode) {
            for (Message message : messages) {
                handleTransactionError(errorTransaction, message);
            }
        }
        else {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Errors on transaction: "+errorTransaction);
                for (Message message: messages) {
                    LOG.debug(message);
                }
            }
            scrubberReportWriterService.writeError(errorTransaction, messages);
        }
    }

    protected void handleEndOfScrubberReport(ScrubberReportData scrubberReport) {
        if (!collectorMode) {
            scrubberReportWriterService.writeStatisticLine("UNSCRUBBED RECORDS READ              %,9d", scrubberReport.getNumberOfUnscrubbedRecordsRead());
            scrubberReportWriterService.writeStatisticLine("SCRUBBED RECORDS WRITTEN             %,9d", scrubberReport.getNumberOfScrubbedRecordsWritten());
            scrubberReportWriterService.writeStatisticLine("ERROR RECORDS WRITTEN                %,9d", scrubberReport.getNumberOfErrorRecordsWritten());
            scrubberReportWriterService.writeStatisticLine("OFFSET ENTRIES GENERATED             %,9d", scrubberReport.getNumberOfOffsetEntriesGenerated());
            scrubberReportWriterService.writeStatisticLine("CAPITALIZATION ENTRIES GENERATED     %,9d", scrubberReport.getNumberOfCapitalizationEntriesGenerated());
            scrubberReportWriterService.writeStatisticLine("LIABILITY ENTRIES GENERATED          %,9d", scrubberReport.getNumberOfLiabilityEntriesGenerated());
            scrubberReportWriterService.writeStatisticLine("PLANT INDEBTEDNESS ENTRIES GENERATED %,9d", scrubberReport.getNumberOfPlantIndebtednessEntriesGenerated());
            scrubberReportWriterService.writeStatisticLine("COST SHARE ENTRIES GENERATED         %,9d", scrubberReport.getNumberOfCostShareEntriesGenerated());
            scrubberReportWriterService.writeStatisticLine("COST SHARE ENC ENTRIES GENERATED     %,9d", scrubberReport.getNumberOfCostShareEncumbrancesGenerated());
            scrubberReportWriterService.writeStatisticLine("TOTAL OUTPUT RECORDS WRITTEN         %,9d", scrubberReport.getTotalNumberOfRecordsWritten());
            scrubberReportWriterService.writeStatisticLine("EXPIRED ACCOUNTS FOUND               %,9d", scrubberReport.getNumberOfExpiredAccountsFound());
        }
    }

    protected void handleDemergerSaveValidEntry(String entryString) {
        if (collectorMode) {
            OriginEntryInformation tempEntry = new OriginEntryFull(entryString);
            ledgerSummaryReport.summarizeEntry(tempEntry);
        }
    }

    /**
     * Sets the batchFileDirectoryName attribute value.
     * @param batchFileDirectoryName The batchFileDirectoryName to set.
     */
    public void setBatchFileDirectoryName(String batchFileDirectoryName) {
        this.batchFileDirectoryName = batchFileDirectoryName;
    }

    /**
     * Gets the transferDescription attribute.
     * @return Returns the transferDescription.
     */
    public String getTransferDescription() {
        return transferDescription;
    }

    /**
     * Sets the transferDescription attribute value.
     * @param transferDescription The transferDescription to set.
     */
    public void setTransferDescription(String transferDescription) {
        this.transferDescription = transferDescription;
    }

    /**
     * Sets the dateTimeService attribute value.
     * @param dateTimeService The dateTimeService to set.
     */
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    /**
     * Sets the flexibleOffsetAccountService attribute value.
     * @param flexibleOffsetAccountService The flexibleOffsetAccountService to set.
     */
    public void setFlexibleOffsetAccountService(FlexibleOffsetAccountService flexibleOffsetAccountService) {
        this.flexibleOffsetAccountService = flexibleOffsetAccountService;
    }

    /**
     * Sets the configurationService attribute value.
     * @param configurationService The configurationService to set.
     */
    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    /**
     * Sets the persistenceService attribute value.
     * @param persistenceService The persistenceService to set.
     */
    public void setPersistenceService(PersistenceService persistenceService) {
        this.persistenceService = persistenceService;
    }

    /**
     * Sets the scrubberValidator attribute value.
     * @param scrubberValidator The scrubberValidator to set.
     */
    public void setScrubberValidator(ScrubberValidator scrubberValidator) {
        this.scrubberValidator = scrubberValidator;
    }

    /**
     * Sets the accountingCycleCachingService attribute value.
     * @param accountingCycleCachingService The accountingCycleCachingService to set.
     */
    public void setAccountingCycleCachingService(AccountingCycleCachingService accountingCycleCachingService) {
        this.accountingCycleCachingService = accountingCycleCachingService;
    }

    /**
     * Sets the scrubberReportWriterService attribute value.
     * @param scrubberReportWriterService The scrubberReportWriterService to set.
     */
    public void setScrubberReportWriterService(DocumentNumberAwareReportWriterService scrubberReportWriterService) {
        this.scrubberReportWriterService = scrubberReportWriterService;
    }

    /**
     * Sets the scrubberLedgerReportWriterService attribute value.
     * @param scrubberLedgerReportWriterService The scrubberLedgerReportWriterService to set.
     */
    public void setScrubberLedgerReportWriterService(DocumentNumberAwareReportWriterService scrubberLedgerReportWriterService) {
        this.scrubberLedgerReportWriterService = scrubberLedgerReportWriterService;
    }

    /**
     * Sets the scrubberListingReportWriterService attribute value.
     * @param scrubberListingReportWriterService The scrubberListingReportWriterService to set.
     */
    public void setScrubberListingReportWriterService(DocumentNumberAwareReportWriterService scrubberListingReportWriterService) {
        this.scrubberListingReportWriterService = scrubberListingReportWriterService;
    }

    /**
     * Sets the scrubberBadBalanceListingReportWriterService attribute value.
     * @param scrubberBadBalanceListingReportWriterService The scrubberBadBalanceListingReportWriterService to set.
     */
    public void setScrubberBadBalanceListingReportWriterService(ReportWriterService scrubberBadBalanceListingReportWriterService) {
        this.scrubberBadBalanceListingReportWriterService = scrubberBadBalanceListingReportWriterService;
    }

    /**
     * Sets the demergerRemovedTransactionsListingReportWriterService attribute value.
     * @param demergerRemovedTransactionsListingReportWriterService The demergerRemovedTransactionsListingReportWriterService to set.
     */
    public void setDemergerRemovedTransactionsListingReportWriterService(ReportWriterService demergerRemovedTransactionsListingReportWriterService) {
        this.demergerRemovedTransactionsListingReportWriterService = demergerRemovedTransactionsListingReportWriterService;
    }

    /**
     * Sets the demergerReportWriterService attribute value.
     * @param demergerReportWriterService The demergerReportWriterService to set.
     */
    public void setDemergerReportWriterService(ReportWriterService demergerReportWriterService) {
        this.demergerReportWriterService = demergerReportWriterService;
    }

    /**
     * Sets the preScrubberService attribute value.
     * @param preScrubberService The preScrubberService to set.
     */
    public void setPreScrubberService(PreScrubberService preScrubberService) {
        this.preScrubberService = preScrubberService;
    }

    /**
     * Sets the parameterService attribute value.
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Sets the runDateService attribute value.
     * @param runDateService The runDateService to set.
     */
    public void setRunDateService(RunDateService runDateService) {
        this.runDateService = runDateService;
    }

    /**
     * Gets the flexibleOffsetAccountService attribute.
     * @return Returns the flexibleOffsetAccountService.
     */
    public FlexibleOffsetAccountService getFlexibleOffsetAccountService() {
        return flexibleOffsetAccountService;
    }

    /**
     * Gets the dateTimeService attribute.
     * @return Returns the dateTimeService.
     */
    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    /**
     * Gets the configurationService attribute.
     * @return Returns the configurationService.
     */
    public ConfigurationService getConfigurationService() {
        return configurationService;
    }

    /**
     * Gets the persistenceService attribute.
     * @return Returns the persistenceService.
     */
    public PersistenceService getPersistenceService() {
        return persistenceService;
    }

    /**
     * Gets the scrubberValidator attribute.
     * @return Returns the scrubberValidator.
     */
    public ScrubberValidator getScrubberValidator() {
        return scrubberValidator;
    }

    /**
     * Gets the runDateService attribute.
     * @return Returns the runDateService.
     */
    public RunDateService getRunDateService() {
        return runDateService;
    }

    /**
     * Gets the accountingCycleCachingService attribute.
     * @return Returns the accountingCycleCachingService.
     */
    public AccountingCycleCachingService getAccountingCycleCachingService() {
        return accountingCycleCachingService;
    }

    /**
     * Gets the scrubberReportWriterService attribute.
     * @return Returns the scrubberReportWriterService.
     */
    public DocumentNumberAwareReportWriterService getScrubberReportWriterService() {
        return scrubberReportWriterService;
    }

    /**
     * Gets the scrubberLedgerReportWriterService attribute.
     * @return Returns the scrubberLedgerReportWriterService.
     */
    public DocumentNumberAwareReportWriterService getScrubberLedgerReportWriterService() {
        return scrubberLedgerReportWriterService;
    }

    /**
     * Gets the scrubberListingReportWriterService attribute.
     * @return Returns the scrubberListingReportWriterService.
     */
    public DocumentNumberAwareReportWriterService getScrubberListingReportWriterService() {
        return scrubberListingReportWriterService;
    }

    /**
     * Gets the scrubberBadBalanceListingReportWriterService attribute.
     * @return Returns the scrubberBadBalanceListingReportWriterService.
     */
    public ReportWriterService getScrubberBadBalanceListingReportWriterService() {
        return scrubberBadBalanceListingReportWriterService;
    }

    /**
     * Gets the demergerRemovedTransactionsListingReportWriterService attribute.
     * @return Returns the demergerRemovedTransactionsListingReportWriterService.
     */
    public ReportWriterService getDemergerRemovedTransactionsListingReportWriterService() {
        return demergerRemovedTransactionsListingReportWriterService;
    }

    /**
     * Gets the demergerReportWriterService attribute.
     * @return Returns the demergerReportWriterService.
     */
    public ReportWriterService getDemergerReportWriterService() {
        return demergerReportWriterService;
    }

    /**
     * Gets the preScrubberService attribute.
     * @return Returns the preScrubberService.
     */
    public PreScrubberService getPreScrubberService() {
        return preScrubberService;
    }

    /**
     * Gets the parameterService attribute.
     * @return Returns the parameterService.
     */
    public ParameterService getParameterService() {
        return parameterService;
    }

    /**
     * Sets the preScrubberReportWriterService attribute value.
     * @param preScrubberReportWriterService The preScrubberReportWriterService to set.
     */
    public void setPreScrubberReportWriterService(DocumentNumberAwareReportWriterService preScrubberReportWriterService) {
        this.preScrubberReportWriterService = preScrubberReportWriterService;
    }

    /**
     * Sets the businessObjectService attribute value.
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }


    // Offset entry to have the same transaction date as the original transaction for Payroll Posting
    protected Date getTransactionDateForOffsetEntry(OriginEntryInformation scrubbedEntry) {
        if (getParameterService().parameterExists(ScrubberStep.class, KFSParameterKeyConstants.GeneralLedgerSysParmeterKeys.TRANSACTION_DATE_BYPASS_ORIGINATIONS)) {
            Collection<String> transactionDateAutoAssignmentBypassOriginCodes = getParameterService().getParameterValuesAsString(ScrubberStep.class, KFSParameterKeyConstants.GeneralLedgerSysParmeterKeys.TRANSACTION_DATE_BYPASS_ORIGINATIONS);
            String originationCode = scrubbedEntry.getFinancialSystemOriginationCode();
            if (transactionDateAutoAssignmentBypassOriginCodes.contains(originationCode)) {
                return scrubbedEntry.getTransactionDate();
            }
        }

        return this.runDate;
    }

}
