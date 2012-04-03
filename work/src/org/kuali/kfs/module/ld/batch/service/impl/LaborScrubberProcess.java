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
package org.kuali.kfs.module.ld.batch.service.impl;

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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.A21SubAccount;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.BalanceType;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.coa.service.OffsetDefinitionService;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.ObjectHelper;
import org.kuali.kfs.gl.batch.BatchSortUtil;
import org.kuali.kfs.gl.batch.ScrubberStep;
import org.kuali.kfs.gl.businessobject.DemergerReportData;
import org.kuali.kfs.gl.businessobject.OriginEntryGroup;
import org.kuali.kfs.gl.businessobject.OriginEntryStatistics;
import org.kuali.kfs.gl.businessobject.Transaction;
import org.kuali.kfs.gl.report.LedgerSummaryReport;
import org.kuali.kfs.gl.report.PreScrubberReport;
import org.kuali.kfs.gl.report.PreScrubberReportData;
import org.kuali.kfs.gl.report.TransactionListingReport;
import org.kuali.kfs.gl.service.OriginEntryGroupService;
import org.kuali.kfs.gl.service.PreScrubberService;
import org.kuali.kfs.gl.service.ScrubberReportData;
import org.kuali.kfs.gl.service.ScrubberValidator;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.batch.LaborScrubberSortComparator;
import org.kuali.kfs.module.ld.batch.LaborScrubberStep;
import org.kuali.kfs.module.ld.batch.service.LaborAccountingCycleCachingService;
import org.kuali.kfs.module.ld.businessobject.LaborOriginEntry;
import org.kuali.kfs.module.ld.businessobject.LaborOriginEntryFieldUtil;
import org.kuali.kfs.module.ld.service.LaborOriginEntryService;
import org.kuali.kfs.module.ld.util.FilteringLaborOriginEntryFileIterator;
import org.kuali.kfs.module.ld.util.FilteringLaborOriginEntryFileIterator.LaborOriginEntryFilter;
import org.kuali.kfs.module.ld.util.LaborOriginEntryFileIterator;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSParameterKeyConstants.LdParameterConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.Message;
import org.kuali.kfs.sys.batch.service.WrappingBatchService;
import org.kuali.kfs.sys.businessobject.UniversityDate;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.dataaccess.UniversityDateDao;
import org.kuali.kfs.sys.service.DocumentNumberAwareReportWriterService;
import org.kuali.kfs.sys.service.FlexibleOffsetAccountService;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.parameter.ParameterEvaluator;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.PersistenceService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class has the logic for the scrubber. It is required because the scrubber process needs instance variables. Instance
 * variables in a spring service are shared between all code calling the service. This will make sure each run of the scrubber has
 * it's own instance variables instead of being shared.
 */
public class LaborScrubberProcess {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborScrubberProcess.class);

    // 40 spaces - used for filling in descriptions with spaces
    protected static String SPACES = "                                        ";

    /* Services required */
    protected FlexibleOffsetAccountService flexibleOffsetAccountService;
    protected LaborOriginEntryService laborOriginEntryService;
    protected OriginEntryGroupService originEntryGroupService;
    protected DateTimeService dateTimeService;
    protected OffsetDefinitionService offsetDefinitionService;
    protected ObjectCodeService objectCodeService;
    protected ConfigurationService kualiConfigurationService;
    protected UniversityDateDao universityDateDao;
    protected PersistenceService persistenceService;
    protected ScrubberValidator scrubberValidator;
    protected LaborAccountingCycleCachingService laborAccountingCycleCachingService;
    protected PreScrubberService laborPreScrubberService;

    protected DocumentNumberAwareReportWriterService laborMainReportWriterService;
    protected DocumentNumberAwareReportWriterService laborLedgerReportWriterService;
    protected ReportWriterService laborBadBalanceTypeReportWriterService;
    protected ReportWriterService laborErrorListingReportWriterService;
    protected DocumentNumberAwareReportWriterService laborGeneratedTransactionsReportWriterService;
    protected ReportWriterService laborDemergerReportWriterService;
    protected DocumentNumberAwareReportWriterService laborPreScrubberReportWriterService;
    protected ParameterService parameterService;

    protected String batchFileDirectoryName;

    enum GROUP_TYPE {
        VALID, ERROR, EXPIRED
    }

    /* These are all different forms of the run date for this job */
    protected Date runDate;
    protected Calendar runCal;
    protected UniversityDate universityRunDate;
    protected String offsetString;

    /*
     * These fields are used to control whether the job was run before some set time, if so, the rundate of the job will be set to
     * 11:59 PM of the previous day
     */
    protected Integer cutoffHour;
    protected Integer cutoffMinute;
    protected Integer cutoffSecond;

    /* These are the output groups */
    protected OriginEntryGroup validGroup;
    protected OriginEntryGroup errorGroup;
    protected OriginEntryGroup expiredGroup;

    /* Unit Of Work info */
    protected UnitOfWorkInfo unitOfWork;
    protected KualiDecimal scrubCostShareAmount;
    protected ScrubberReportData scrubberReport;

    /* Description names */
    protected String offsetDescription;
    protected String capitalizationDescription;
    protected String liabilityDescription;
    protected String transferDescription;
    protected String costShareDescription;

    protected String inputFile;
    protected String validFile;
    protected String errorFile;
    protected String expiredFile;

    /**
     * These parameters are all the dependencies.
     */
    public LaborScrubberProcess(FlexibleOffsetAccountService flexibleOffsetAccountService,
                                LaborAccountingCycleCachingService laborAccountingCycleCachingService,
                                LaborOriginEntryService laborOriginEntryService,
                                OriginEntryGroupService originEntryGroupService,
                                DateTimeService dateTimeService,
                                OffsetDefinitionService offsetDefinitionService,
                                ObjectCodeService objectCodeService,
                                ConfigurationService kualiConfigurationService,
                                UniversityDateDao universityDateDao,
                                PersistenceService persistenceService,
                                ScrubberValidator scrubberValidator,
                                String batchFileDirectoryName,
                                DocumentNumberAwareReportWriterService laborMainReportWriterService,
                                DocumentNumberAwareReportWriterService laborLedgerReportWriterService,
                                ReportWriterService laborBadBalanceTypeReportWriterService,
                                ReportWriterService laborErrorListingReportWriterService,
                                DocumentNumberAwareReportWriterService laborGeneratedTransactionsReportWriterService,
                                ReportWriterService laborDemergerReportWriterService,
                                PreScrubberService laborPreScrubberService,
                                DocumentNumberAwareReportWriterService laborPreScrubberReportWriterService,
                                ParameterService parameterService) {
        super();
        this.flexibleOffsetAccountService = flexibleOffsetAccountService;
        this.laborAccountingCycleCachingService = laborAccountingCycleCachingService;
        this.laborOriginEntryService = laborOriginEntryService;
        this.originEntryGroupService = originEntryGroupService;
        this.dateTimeService = dateTimeService;
        this.offsetDefinitionService = offsetDefinitionService;
        this.objectCodeService = objectCodeService;
        this.kualiConfigurationService = kualiConfigurationService;
        this.universityDateDao = universityDateDao;
        this.persistenceService = persistenceService;
        this.scrubberValidator = scrubberValidator;
        this.batchFileDirectoryName = batchFileDirectoryName;
        this.laborMainReportWriterService = laborMainReportWriterService;
        this.laborLedgerReportWriterService = laborLedgerReportWriterService;
        this.laborBadBalanceTypeReportWriterService = laborBadBalanceTypeReportWriterService;
        this.laborErrorListingReportWriterService = laborErrorListingReportWriterService;
        this.laborGeneratedTransactionsReportWriterService = laborGeneratedTransactionsReportWriterService;
        this.laborDemergerReportWriterService = laborDemergerReportWriterService;
        this.laborPreScrubberService = laborPreScrubberService;
        this.laborPreScrubberReportWriterService = laborPreScrubberReportWriterService;
        this.parameterService = parameterService;

        cutoffHour = null;
        cutoffMinute = null;
        cutoffSecond = null;

        initCutoffTime();
    }

    /**
     * Scrub this single group read only. This will only output the scrubber report. It won't output any other groups.
     *
     * @param group
     */
    public void scrubGroupReportOnly(String fileName, String documentNumber) {
        String unsortedFile = fileName;
        this.inputFile = fileName + ".sort";
        this.validFile = batchFileDirectoryName + File.separator + LaborConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        this.errorFile = batchFileDirectoryName + File.separator + LaborConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        this.expiredFile = batchFileDirectoryName + File.separator + LaborConstants.BatchFileSystem.SCRUBBER_EXPIRED_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        String prescrubOutput = batchFileDirectoryName + File.separator + LaborConstants.BatchFileSystem.PRE_SCRUBBER_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;

        PreScrubberReportData preScrubberReportData = null;
        // run pre-scrubber on the raw input into the sort process
        LineIterator inputEntries = null;
        try {
            inputEntries = FileUtils.lineIterator(new File(unsortedFile));
            preScrubberReportData = laborPreScrubberService.preprocessOriginEntries(inputEntries, prescrubOutput);
        }
        catch (IOException e1) {
            LOG.error("Error encountered trying to prescrub GLCP/LLCP document", e1);
            throw new RuntimeException("Error encountered trying to prescrub GLCP/LLCP document", e1);
        }
        finally {
            LineIterator.closeQuietly(inputEntries);
        }
        if (preScrubberReportData != null) {
            laborPreScrubberReportWriterService.setDocumentNumber(documentNumber);
            ((WrappingBatchService)laborPreScrubberReportWriterService).initialize();
            try {
                new PreScrubberReport().generateReport(preScrubberReportData, laborPreScrubberReportWriterService);
            }
            finally {
                ((WrappingBatchService)laborPreScrubberReportWriterService).destroy();
            }
        }
        BatchSortUtil.sortTextFileWithFields(prescrubOutput, inputFile, new LaborScrubberSortComparator());

        scrubEntries(true, documentNumber);

        File deleteSortFile = new File(inputFile);
        File deleteValidFile = new File(validFile);
        File deleteErrorFile = new File(errorFile);
        File deleteExpiredFile = new File(expiredFile);
        try {
            deleteSortFile.delete();
            deleteValidFile.delete();
            deleteErrorFile.delete();
            deleteExpiredFile.delete();
        }
        catch (Exception e) {
            LOG.error("scrubGroupReportOnly delete output files process Stopped: " + e.getMessage());
            throw new RuntimeException("scrubGroupReportOnly delete output files process Stopped: " + e.getMessage(), e);
        }
    }

    public void scrubEntries() {
        this.inputFile = batchFileDirectoryName + File.separator + LaborConstants.BatchFileSystem.SCRUBBER_INPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        this.validFile = batchFileDirectoryName + File.separator + LaborConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        this.errorFile = batchFileDirectoryName + File.separator + LaborConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        this.expiredFile = batchFileDirectoryName + File.separator + LaborConstants.BatchFileSystem.SCRUBBER_EXPIRED_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;

        scrubEntries(false, null);
    }

    /**
     * Scrub all entries that need it in origin entry. Put valid scrubbed entries in a scrubber valid group, put errors in a
     * scrubber error group, and transactions with an expired account in the scrubber expired account group.
     */
    public void scrubEntries(boolean reportOnlyMode, String documentNumber) {
        LOG.debug("scrubEntries() started");

        if (reportOnlyMode) {
            laborMainReportWriterService.setDocumentNumber(documentNumber);
            laborLedgerReportWriterService.setDocumentNumber(documentNumber);
            laborGeneratedTransactionsReportWriterService.setDocumentNumber(documentNumber);
        }

        // setup an object to hold the "default" date information
        runDate = calculateRunDate(dateTimeService.getCurrentDate());
        runCal = Calendar.getInstance();
        runCal.setTime(runDate);

        universityRunDate = laborAccountingCycleCachingService.getUniversityDate(runDate);
        if (universityRunDate == null) {
            throw new IllegalStateException(kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.ERROR_UNIV_DATE_NOT_FOUND));
        }
        setOffsetString();
        setDescriptions();

        try {
            ((WrappingBatchService) laborMainReportWriterService).initialize();
            ((WrappingBatchService) laborLedgerReportWriterService).initialize();
            if (reportOnlyMode) {
                ((WrappingBatchService) laborGeneratedTransactionsReportWriterService).initialize();
            }

            scrubberReport = new ScrubberReportData();
            processGroup();

            // Run the reports
            if (reportOnlyMode) {
                generateScrubberTransactionsOnline();
            }
            else {
                generateScrubberBadBalanceTypeListingReport();
            }
        }
        finally {
            ((WrappingBatchService) laborMainReportWriterService).destroy();
            ((WrappingBatchService) laborLedgerReportWriterService).destroy();
            if (reportOnlyMode) {
                ((WrappingBatchService) laborGeneratedTransactionsReportWriterService).destroy();
            }
        }
    }

    /**
     * Determine the type of the transaction by looking at attributes
     *
     * @param transaction Transaction to identify
     * @return CE (Cost share encumbrance, O (Offset), C (apitalization), L (Liability), T (Transfer), CS (Cost Share), X (Other)
     */
    protected String getTransactionType(LaborOriginEntry transaction) {
        if ("CE".equals(transaction.getFinancialBalanceTypeCode())) {
            return "CE";
        }
        String desc = transaction.getTransactionLedgerEntryDescription();

        if (desc == null) {
            return "X";
        }

        if (desc.startsWith(offsetDescription) && desc.indexOf("***") > -1) {
            return "CS";
        }
        if (desc.startsWith(costShareDescription) && desc.indexOf("***") > -1) {
            return "CS";
        }
        if (desc.startsWith(offsetDescription)) {
            return "O";
        }
        if (desc.startsWith(capitalizationDescription)) {
            return "C";
        }
        if (desc.startsWith(liabilityDescription)) {
            return "L";
        }
        if (desc.startsWith(transferDescription)) {
            return "T";
        }
        return "X";
    }

    /**
     * This will process a group of origin entries. The COBOL code was refactored a lot to get this so there isn't a 1 to 1 section
     * of Cobol relating to this.
     *
     * @param originEntryGroup Group to process
     */
    protected void processGroup() {
        LaborOriginEntry lastEntry = null;
        scrubCostShareAmount = KualiDecimal.ZERO;
        unitOfWork = new UnitOfWorkInfo();
        FileReader INPUT_GLE_FILE = null;
        String GLEN_RECORD;
        BufferedReader INPUT_GLE_FILE_br;
        PrintStream OUTPUT_GLE_FILE_ps;
        PrintStream OUTPUT_ERR_FILE_ps;
        PrintStream OUTPUT_EXP_FILE_ps;
        try {
            INPUT_GLE_FILE = new FileReader(inputFile);
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException("Unable to find input file: " + inputFile, e);
        }
        try {
            OUTPUT_GLE_FILE_ps = new PrintStream(validFile);
            OUTPUT_ERR_FILE_ps = new PrintStream(errorFile);
            OUTPUT_EXP_FILE_ps = new PrintStream(expiredFile);
        }
        catch (IOException e) {
            throw new RuntimeException("Problem opening output files", e);
        }

        INPUT_GLE_FILE_br = new BufferedReader(INPUT_GLE_FILE);
        LOG.debug("Starting Scrubber Process process group...");

        int lineNumber = 0;
        int loadedCount = 0;
        boolean errorsLoading = false;

        LedgerSummaryReport laborLedgerSummaryReport = new LedgerSummaryReport();
        LaborOriginEntry unscrubbedEntry = new LaborOriginEntry();
        List<Message> tmperrors = new ArrayList<Message>();
        try {
            String currentLine = INPUT_GLE_FILE_br.readLine();

            while (currentLine != null) {
                boolean saveErrorTransaction = false;
                boolean saveValidTransaction = false;
                LaborOriginEntry scrubbedEntry = new LaborOriginEntry();
                try {
                    lineNumber++;

                    if (!StringUtils.isEmpty(currentLine) && !StringUtils.isBlank(currentLine.trim())) {
                        unscrubbedEntry = new LaborOriginEntry();
                        tmperrors = unscrubbedEntry.setFromTextFileForBatch(currentLine, lineNumber);
                        loadedCount++;

                        // just test entry with the entry loaded above
                        scrubberReport.incrementUnscrubbedRecordsRead();
                        List<Message> transactionErrors = new ArrayList<Message>();

                        // This is done so if the code modifies this row, then saves it, it will be an insert,
                        // and it won't touch the original. The Scrubber never modifies input rows/groups.
                        unscrubbedEntry.setGroup(null);
                        unscrubbedEntry.setVersionNumber(null);
                        unscrubbedEntry.setEntryId(null);
                        saveErrorTransaction = false;
                        saveValidTransaction = false;

                        // Build a scrubbed entry
                        // Labor has more fields
                        buildScrubbedEntry(unscrubbedEntry, scrubbedEntry);

                        // For Labor Scrubber
                        boolean laborIndicator = true;
                        laborLedgerSummaryReport.summarizeEntry(unscrubbedEntry);

                        try {
                            tmperrors.addAll(scrubberValidator.validateTransaction(unscrubbedEntry, scrubbedEntry, universityRunDate, laborIndicator, laborAccountingCycleCachingService));
                        }
                        catch (Exception e) {
                            transactionErrors.add(new Message(e.toString() + " occurred for this record.", Message.TYPE_FATAL));
                            saveValidTransaction = false;
                        }
                        transactionErrors.addAll(tmperrors);

                        // Expired account?
                        Account unscrubbedEntryAccount = laborAccountingCycleCachingService.getAccount(unscrubbedEntry.getChartOfAccountsCode(), unscrubbedEntry.getAccountNumber());
                        if (ObjectUtils.isNotNull(unscrubbedEntry.getAccount()) && (scrubberValidator.isAccountExpired(unscrubbedEntryAccount, universityRunDate) || unscrubbedEntryAccount.isClosed())) {
                            // Make a copy of it so OJB doesn't just update the row in the original
                            // group. It needs to make a new one in the expired group
                            LaborOriginEntry expiredEntry = new LaborOriginEntry(scrubbedEntry);

                            createOutputEntry(expiredEntry, OUTPUT_EXP_FILE_ps);
                            scrubberReport.incrementExpiredAccountFound();
                        }

                        if (!isFatal(transactionErrors)) {
                            saveValidTransaction = true;

                            // See if unit of work has changed
                            if (!unitOfWork.isSameUnitOfWork(scrubbedEntry)) {
                                // Generate offset for last unit of work
                                unitOfWork = new UnitOfWorkInfo(scrubbedEntry);
                            }
                            KualiDecimal transactionAmount = scrubbedEntry.getTransactionLedgerEntryAmount();
                            ParameterEvaluator offsetFiscalPeriods = /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.OFFSET_FISCAL_PERIOD_CODES, scrubbedEntry.getUniversityFiscalPeriodCode());
                            BalanceType scrubbedEntryBalanceType = laborAccountingCycleCachingService.getBalanceType(scrubbedEntry.getFinancialBalanceTypeCode());
                            if (scrubbedEntryBalanceType.isFinancialOffsetGenerationIndicator() && offsetFiscalPeriods.evaluationSucceeds()) {
                                if (scrubbedEntry.isDebit()) {
                                    unitOfWork.offsetAmount = unitOfWork.offsetAmount.add(transactionAmount);
                                }
                                else {
                                    unitOfWork.offsetAmount = unitOfWork.offsetAmount.subtract(transactionAmount);
                                }
                            }

                            // The sub account type code will only exist if there is a valid sub account
                            // TODO: GLConstants.getSpaceSubAccountTypeCode();
                            String subAccountTypeCode = "  ";

                            A21SubAccount scrubbedEntryA21SubAccount = laborAccountingCycleCachingService.getA21SubAccount(scrubbedEntry.getChartOfAccountsCode(), scrubbedEntry.getAccountNumber(), scrubbedEntry.getSubAccountNumber());
                            if (ObjectUtils.isNotNull(scrubbedEntryA21SubAccount)) {
                                subAccountTypeCode = scrubbedEntryA21SubAccount.getSubAccountTypeCode();
                            }

                            if (transactionErrors.size() > 0) {
                                this.laborMainReportWriterService.writeError(unscrubbedEntry, transactionErrors);
                            }

                            lastEntry = scrubbedEntry;
                        }
                        else {
                            // Error transaction
                            saveErrorTransaction = true;
                            this.laborMainReportWriterService.writeError(unscrubbedEntry, transactionErrors);
                        }


                        if (saveValidTransaction) {
                            scrubbedEntry.setTransactionScrubberOffsetGenerationIndicator(false);
                            createOutputEntry(scrubbedEntry, OUTPUT_GLE_FILE_ps);
                            scrubberReport.incrementScrubbedRecordWritten();
                        }

                        if (saveErrorTransaction) {
                            // Make a copy of it so OJB doesn't just update the row in the original
                            // group. It needs to make a new one in the error group
                            LaborOriginEntry errorEntry = new LaborOriginEntry(unscrubbedEntry);
                            errorEntry.setTransactionScrubberOffsetGenerationIndicator(false);
                            createOutputEntry(currentLine, OUTPUT_ERR_FILE_ps);
                            scrubberReport.incrementErrorRecordWritten();
                        }
                    }
                    currentLine = INPUT_GLE_FILE_br.readLine();

                }
                catch (IOException ioe) {
                    // catch here again, it should be from postSingleEntryIntoLaborLedger
                    LOG.error("processGroup() stopped due to: " + ioe.getMessage() + " on line number : " + loadedCount, ioe);
                    throw new RuntimeException("processGroup() stopped due to: " + ioe.getMessage() + " on line number : " + loadedCount, ioe);
                }
            }
            INPUT_GLE_FILE_br.close();
            INPUT_GLE_FILE.close();
            OUTPUT_GLE_FILE_ps.close();
            OUTPUT_ERR_FILE_ps.close();
            OUTPUT_EXP_FILE_ps.close();

            this.laborMainReportWriterService.writeStatisticLine("UNSCRUBBED RECORDS READ              %,9d", scrubberReport.getNumberOfUnscrubbedRecordsRead());
            this.laborMainReportWriterService.writeStatisticLine("SCRUBBED RECORDS WRITTEN             %,9d", scrubberReport.getNumberOfScrubbedRecordsWritten());
            this.laborMainReportWriterService.writeStatisticLine("ERROR RECORDS WRITTEN                %,9d", scrubberReport.getNumberOfErrorRecordsWritten());
            this.laborMainReportWriterService.writeStatisticLine("TOTAL OUTPUT RECORDS WRITTEN         %,9d", scrubberReport.getTotalNumberOfRecordsWritten());
            this.laborMainReportWriterService.writeStatisticLine("EXPIRED ACCOUNTS FOUND               %,9d", scrubberReport.getNumberOfExpiredAccountsFound());

            laborLedgerSummaryReport.writeReport(this.laborLedgerReportWriterService);
        }
        catch (IOException ioe) {
            LOG.error("processGroup() stopped due to: " + ioe.getMessage(), ioe);
            throw new RuntimeException("processGroup() stopped due to: " + ioe.getMessage(), ioe);
        }
    }


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
     * Get all the transaction descriptions from the param table
     */
    protected void setDescriptions() {
        offsetDescription = kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.MSG_GENERATED_OFFSET);
        capitalizationDescription = kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.MSG_GENERATED_CAPITALIZATION);
        liabilityDescription = kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.MSG_GENERATED_LIABILITY);
        costShareDescription = kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.MSG_GENERATED_COST_SHARE);
        transferDescription = kualiConfigurationService.getPropertyValueAsString(KFSKeyConstants.MSG_GENERATED_TRANSFER);
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

        offsetString = "***" + nf.format(runCal.get(Calendar.MONTH) + 1) + nf.format(runCal.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * Generate the offset message with the flag at the end
     *
     * @return Offset message
     */
    protected String getOffsetMessage() {
        String msg = offsetDescription + SPACES;

        return msg.substring(0, 33) + offsetString;
    }

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

        public UnitOfWorkInfo() {
        }

        public UnitOfWorkInfo(LaborOriginEntry e) {
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

        public boolean isSameUnitOfWork(LaborOriginEntry e) {
            // Compare the key fields
            return univFiscalYr.equals(e.getUniversityFiscalYear()) && finCoaCd.equals(e.getChartOfAccountsCode()) && accountNbr.equals(e.getAccountNumber()) && subAcctNbr.equals(e.getSubAccountNumber()) && finBalanceTypCd.equals(e.getFinancialBalanceTypeCode()) && fdocTypCd.equals(e.getFinancialDocumentTypeCode()) && fsOriginCd.equals(e.getFinancialSystemOriginationCode()) && fdocNbr.equals(e.getDocumentNumber()) && ObjectHelper.isEqual(fdocReversalDt, e.getFinancialDocumentReversalDate()) && univFiscalPrdCd.equals(e.getUniversityFiscalPeriodCode());
        }

        @Override
        public String toString() {
            return univFiscalYr + finCoaCd + accountNbr + subAcctNbr + finBalanceTypCd + fdocTypCd + fsOriginCd + fdocNbr + fdocReversalDt + univFiscalPrdCd;
        }

        public LaborOriginEntry getOffsetTemplate() {
            LaborOriginEntry e = new LaborOriginEntry();
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

    class TransactionError {
        public Transaction transaction;
        public Message message;

        public TransactionError(Transaction t, Message m) {
            transaction = t;
            message = m;
        }
    }

    protected void setCutoffTimeForPreviousDay(int hourOfDay, int minuteOfDay, int secondOfDay) {
        this.cutoffHour = hourOfDay;
        this.cutoffMinute = minuteOfDay;
        this.cutoffSecond = secondOfDay;

        LOG.info("Setting cutoff time to hour: " + hourOfDay + ", minute: " + minuteOfDay + ", second: " + secondOfDay);
    }

    protected void setCutoffTime(String cutoffTime) {
        if (StringUtils.isBlank(cutoffTime)) {
            LOG.debug("Cutoff time is blank");
            unsetCutoffTimeForPreviousDay();
        }
        else {
            cutoffTime = cutoffTime.trim();
            if (LOG.isDebugEnabled()) {
                LOG.debug("Cutoff time value found: " + cutoffTime);
            }
            StringTokenizer st = new StringTokenizer(cutoffTime, ":", false);

            try {
                String hourStr = st.nextToken();
                String minuteStr = st.nextToken();
                String secondStr = st.nextToken();

                int hourInt = Integer.parseInt(hourStr, 10);
                int minuteInt = Integer.parseInt(minuteStr, 10);
                int secondInt = Integer.parseInt(secondStr, 10);

                if (hourInt < 0 || hourInt > 23 || minuteInt < 0 || minuteInt > 59 || secondInt < 0 || secondInt > 59) {
                    throw new IllegalArgumentException("Cutoff time must be in the format \"HH:mm:ss\", where HH, mm, ss are defined in the java.text.SimpleDateFormat class.  In particular, 0 <= hour <= 23, 0 <= minute <= 59, and 0 <= second <= 59");
                }
                setCutoffTimeForPreviousDay(hourInt, minuteInt, secondInt);
            }
            catch (Exception e) {
                throw new IllegalArgumentException("Cutoff time should either be null, or in the format \"HH:mm:ss\", where HH, mm, ss are defined in the java.text.SimpleDateFormat class.",e);
            }
        }
    }


    public void unsetCutoffTimeForPreviousDay() {
        this.cutoffHour = null;
        this.cutoffMinute = null;
        this.cutoffSecond = null;
    }

    /**
     * This method modifies the run date if it is before the cutoff time specified by calling the setCutoffTimeForPreviousDay
     * method. See KULRNE-70 This method is public to facilitate unit testing
     *
     * @param currentDate
     * @return
     */
    public java.sql.Date calculateRunDate(java.util.Date currentDate) {
        Calendar currentCal = Calendar.getInstance();
        currentCal.setTime(currentDate);

        if (isCurrentDateBeforeCutoff(currentCal)) {
            // time to set the date to the previous day's last minute/second
            currentCal.add(Calendar.DAY_OF_MONTH, -1);
            // per old COBOL code (see KULRNE-70),
            // the time is set to 23:59:59 (assuming 0 ms)
            currentCal.set(Calendar.HOUR_OF_DAY, 23);
            currentCal.set(Calendar.MINUTE, 59);
            currentCal.set(Calendar.SECOND, 59);
            currentCal.set(Calendar.MILLISECOND, 0);
            return new java.sql.Date(currentCal.getTimeInMillis());
        }
        return new java.sql.Date(currentDate.getTime());
    }

    protected boolean isCurrentDateBeforeCutoff(Calendar currentCal) {
        if (cutoffHour != null && cutoffMinute != null && cutoffSecond != null) {
            // if cutoff date is not properly defined
            // 24 hour clock (i.e. hour is 0 - 23)

            // clone the calendar so we get the same month, day, year
            // then change the hour, minute, second fields
            // then see if the cutoff is before or after
            Calendar cutoffTime = (Calendar) currentCal.clone();
            cutoffTime.setLenient(false);
            cutoffTime.set(Calendar.HOUR_OF_DAY, cutoffHour);
            cutoffTime.set(Calendar.MINUTE, cutoffMinute);
            cutoffTime.set(Calendar.SECOND, cutoffSecond);
            cutoffTime.set(Calendar.MILLISECOND, 0);

            return currentCal.before(cutoffTime);
        }
        // if cutoff date is not properly defined, then it is considered to be after the cutoff
        return false;
    }

    protected void initCutoffTime() {
        String cutoffTime = parameterService.getParameterValueAsString(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupParameters.SCRUBBER_CUTOFF_TIME);
        if (StringUtils.isBlank(cutoffTime)) {
            LOG.debug("Cutoff time system parameter not found");
            unsetCutoffTimeForPreviousDay();
            return;
        }
        setCutoffTime(cutoffTime);
    }

    protected void buildScrubbedEntry(LaborOriginEntry unscrubbedEntry, LaborOriginEntry scrubbedEntry) {
        scrubbedEntry.setDocumentNumber(unscrubbedEntry.getDocumentNumber());
        scrubbedEntry.setOrganizationDocumentNumber(unscrubbedEntry.getOrganizationDocumentNumber());
        scrubbedEntry.setOrganizationReferenceId(unscrubbedEntry.getOrganizationReferenceId());
        scrubbedEntry.setReferenceFinancialDocumentNumber(unscrubbedEntry.getReferenceFinancialDocumentNumber());

        Integer transactionNumber = unscrubbedEntry.getTransactionLedgerEntrySequenceNumber();
        scrubbedEntry.setTransactionLedgerEntrySequenceNumber(null == transactionNumber ? new Integer(0) : transactionNumber);
        scrubbedEntry.setTransactionLedgerEntryDescription(unscrubbedEntry.getTransactionLedgerEntryDescription());
        scrubbedEntry.setTransactionLedgerEntryAmount(unscrubbedEntry.getTransactionLedgerEntryAmount());
        scrubbedEntry.setTransactionDebitCreditCode(unscrubbedEntry.getTransactionDebitCreditCode());

        // For Labor's more fields
        // It might be changed based on Labor Scrubber's business rule
        scrubbedEntry.setPositionNumber(unscrubbedEntry.getPositionNumber());
        scrubbedEntry.setTransactionPostingDate(unscrubbedEntry.getTransactionPostingDate());
        scrubbedEntry.setPayPeriodEndDate(unscrubbedEntry.getPayPeriodEndDate());
        scrubbedEntry.setTransactionTotalHours(unscrubbedEntry.getTransactionTotalHours());
        scrubbedEntry.setPayrollEndDateFiscalYear(unscrubbedEntry.getPayrollEndDateFiscalYear());
        scrubbedEntry.setPayrollEndDateFiscalPeriodCode(unscrubbedEntry.getPayrollEndDateFiscalPeriodCode());
        scrubbedEntry.setFinancialDocumentApprovedCode(unscrubbedEntry.getFinancialDocumentApprovedCode());
        scrubbedEntry.setTransactionEntryOffsetCode(unscrubbedEntry.getTransactionEntryOffsetCode());
        scrubbedEntry.setTransactionEntryProcessedTimestamp(unscrubbedEntry.getTransactionEntryProcessedTimestamp());
        scrubbedEntry.setEmplid(unscrubbedEntry.getEmplid());
        scrubbedEntry.setEmployeeRecord(unscrubbedEntry.getEmployeeRecord());
        scrubbedEntry.setEarnCode(unscrubbedEntry.getEarnCode());
        scrubbedEntry.setPayGroup(unscrubbedEntry.getPayGroup());
        scrubbedEntry.setSalaryAdministrationPlan(unscrubbedEntry.getSalaryAdministrationPlan());
        scrubbedEntry.setGrade(unscrubbedEntry.getGrade());
        scrubbedEntry.setRunIdentifier(unscrubbedEntry.getRunIdentifier());
        scrubbedEntry.setLaborLedgerOriginalChartOfAccountsCode(unscrubbedEntry.getLaborLedgerOriginalChartOfAccountsCode());
        scrubbedEntry.setLaborLedgerOriginalAccountNumber(unscrubbedEntry.getLaborLedgerOriginalAccountNumber());
        scrubbedEntry.setLaborLedgerOriginalSubAccountNumber(unscrubbedEntry.getLaborLedgerOriginalSubAccountNumber());
        scrubbedEntry.setLaborLedgerOriginalFinancialObjectCode(unscrubbedEntry.getLaborLedgerOriginalFinancialObjectCode());
        scrubbedEntry.setLaborLedgerOriginalFinancialSubObjectCode(unscrubbedEntry.getLaborLedgerOriginalFinancialSubObjectCode());
        scrubbedEntry.setHrmsCompany(unscrubbedEntry.getHrmsCompany());
        scrubbedEntry.setSetid(unscrubbedEntry.getSetid());
        scrubbedEntry.setTransactionDateTimeStamp(unscrubbedEntry.getTransactionDateTimeStamp());
        scrubbedEntry.setReferenceFinancialDocumentTypeCode(unscrubbedEntry.getReferenceFinancialDocumentTypeCode());
        scrubbedEntry.setReferenceFinancialSystemOrigination(unscrubbedEntry.getReferenceFinancialSystemOrigination());
        scrubbedEntry.setPayrollEndDateFiscalPeriod(unscrubbedEntry.getPayrollEndDateFiscalPeriod());
    }

    /**
     * The demerger process reads all of the documents in the error group, then moves all of the original entries for that document
     * from the valid group to the error group. It does not move generated entries to the error group. Those are deleted. It also
     * modifies the doc number and origin code of cost share transfers.
     *
     * @param errorGroup
     * @param validGroup
     */
    public void performDemerger() {
        LOG.debug("performDemerger() started");
        LaborOriginEntryFieldUtil loefu = new LaborOriginEntryFieldUtil();
        Map<String, Integer> pMap = loefu.getFieldBeginningPositionMap();

        String validOutputFilename = batchFileDirectoryName + File.separator + LaborConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        String errorOutputFilename = batchFileDirectoryName + File.separator + LaborConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        runDate = calculateRunDate(dateTimeService.getCurrentDate());

        // Without this step, the job fails with Optimistic Lock Exceptions
      //  persistenceService.clearCache();

        DemergerReportData demergerReport = new DemergerReportData();

        OriginEntryStatistics eOes = laborOriginEntryService.getStatistics(errorOutputFilename);
        demergerReport.setErrorTransactionsRead(eOes.getRowCount());

        //
        // Get the list of document type codes from the parameter.  If the
        // current document type matches any of parameter defined type codes,
        // then demerge all other entries for this document; otherwise, only
        // pull the entry with the error and don't demerge anything else.
        //
        Collection<String> demergeDocumentTypes = parameterService.getParameterValuesAsString(
                LaborScrubberStep.class,
                LdParameterConstants.DEMERGE_DOCUMENT_TYPES);

        // Read all the documents from the error group and move all non-generated
        // transactions for these documents from the valid group into the error group

        FileReader INPUT_GLE_FILE = null;
        FileReader INPUT_ERR_FILE = null;
        BufferedReader INPUT_GLE_FILE_br;
        BufferedReader INPUT_ERR_FILE_br;
        PrintStream OUTPUT_DEMERGER_GLE_FILE_ps;
        PrintStream OUTPUT_DEMERGER_ERR_FILE_ps;
        PrintStream reportPrintStream;

        String demergerValidOutputFilename = batchFileDirectoryName + File.separator + LaborConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        String demergerErrorOutputFilename = batchFileDirectoryName + File.separator + LaborConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;

        try {
            INPUT_GLE_FILE = new FileReader(validOutputFilename);
            INPUT_ERR_FILE = new FileReader(errorOutputFilename);
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException("Unable to open input files", e);
        }
        try {
            OUTPUT_DEMERGER_GLE_FILE_ps = new PrintStream(demergerValidOutputFilename);
            OUTPUT_DEMERGER_ERR_FILE_ps = new PrintStream(demergerErrorOutputFilename);
        }
        catch (IOException e) {
            throw new RuntimeException("Unable to open output files",e);
        }

        Collection<LaborOriginEntry> validEntryCollection = new ArrayList<LaborOriginEntry>();

        int validRead = 0;
        int errorRead = 0;

        int validSaved = 0;
        int errorSaved = 0;

        boolean errorsLoading = false;
        LaborOriginEntry laborOriginEntry = null;
        INPUT_GLE_FILE_br = new BufferedReader(INPUT_GLE_FILE);
        INPUT_ERR_FILE_br = new BufferedReader(INPUT_ERR_FILE);

        try {
            String currentValidLine = INPUT_GLE_FILE_br.readLine();
            String currentErrorLine = INPUT_ERR_FILE_br.readLine();

            boolean meetFlag = false;
            while (currentValidLine != null || currentErrorLine != null) {
                // validLine is null means that errorLine is not null
                if (StringUtils.isEmpty(currentValidLine)) {
                    createOutputEntry(currentErrorLine, OUTPUT_DEMERGER_ERR_FILE_ps);
                    currentErrorLine = INPUT_ERR_FILE_br.readLine();
                    errorRead++;
                    errorSaved++;

                    continue;
                }

                // errorLine is null means that validLine is not null
                if (StringUtils.isEmpty(currentErrorLine)) {
                    createOutputEntry(currentValidLine, OUTPUT_DEMERGER_GLE_FILE_ps);
                    currentValidLine = INPUT_GLE_FILE_br.readLine();
                    validRead++;
                    validSaved++;

                    continue;
                }

                String documentTypeCode = currentErrorLine.substring(pMap.get(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE), pMap.get(KFSPropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE));
                if (documentTypeCode != null) {
                    documentTypeCode = documentTypeCode.trim();
                }

                if (demergeDocumentTypes.contains(documentTypeCode)) {
                    String compareStringFromValidEntry = currentValidLine.substring(pMap.get(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE), pMap.get(KFSPropertyConstants.TRANSACTION_ENTRY_SEQUENCE_NUMBER));
                    String compareStringFromErrorEntry = currentErrorLine.substring(pMap.get(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE), pMap.get(KFSPropertyConstants.TRANSACTION_ENTRY_SEQUENCE_NUMBER));

                    if (compareStringFromValidEntry.compareTo(compareStringFromErrorEntry) < 0) {
                        createOutputEntry(currentValidLine, OUTPUT_DEMERGER_GLE_FILE_ps);
                        currentValidLine = INPUT_GLE_FILE_br.readLine();
                        validRead++;
                        validSaved++;

                    }
                    else if (compareStringFromValidEntry.compareTo(compareStringFromErrorEntry) > 0) {
                        createOutputEntry(currentErrorLine, OUTPUT_DEMERGER_ERR_FILE_ps);
                        currentErrorLine = INPUT_ERR_FILE_br.readLine();
                        errorRead++;
                        errorSaved++;
                    }
                    else {
                        createOutputEntry(currentValidLine, OUTPUT_DEMERGER_ERR_FILE_ps);
                        currentValidLine = INPUT_GLE_FILE_br.readLine();
                        validRead++;
                        errorSaved++;
                    }

                    continue;
                }
                createOutputEntry(currentErrorLine, OUTPUT_DEMERGER_ERR_FILE_ps);
                currentErrorLine = INPUT_ERR_FILE_br.readLine();
                errorRead++;
                errorSaved++;
            }

        }
        catch (Exception e) {
            LOG.error("performDemerger() s" + "topped due to: " + e.getMessage(), e);
            throw new RuntimeException("performDemerger() stopped due to: " + e.getMessage(), e);
        }
        finally {

            try {
                if (INPUT_GLE_FILE_br != null) {
                    INPUT_GLE_FILE_br.close();
                }
            }
            catch (IOException e) {
                LOG.error("performDemerger() s" + "Failed to close resources due to: " + e.getMessage(), e);
            }

            try {
                if (INPUT_ERR_FILE_br != null) {
                    INPUT_ERR_FILE_br.close();
                }
            }
            catch (IOException e) {
                LOG.error("performDemerger() s" + "Failed to close resources due to: " + e.getMessage(), e);
            }

            OUTPUT_DEMERGER_GLE_FILE_ps.close();
            OUTPUT_DEMERGER_ERR_FILE_ps.close();
        }

        demergerReport.setValidTransactionsRead(validRead);
        demergerReport.setValidTransactionsSaved(validSaved);
        demergerReport.setErrorTransactionsRead(errorRead);
        demergerReport.setErrorTransactionWritten(errorSaved);

        this.laborDemergerReportWriterService.writeStatisticLine("SCRUBBER ERROR TRANSACTIONS READ       %,9d", demergerReport.getErrorTransactionsRead());
        this.laborDemergerReportWriterService.writeStatisticLine("SCRUBBER VALID TRANSACTIONS READ       %,9d", demergerReport.getValidTransactionsRead());
        this.laborDemergerReportWriterService.writeStatisticLine("DEMERGER ERRORS SAVED                  %,9d", demergerReport.getErrorTransactionsSaved());
        this.laborDemergerReportWriterService.writeStatisticLine("DEMERGER VALID TRANSACTIONS SAVED      %,9d", demergerReport.getValidTransactionsSaved());

        this.generateScrubberErrorListingReport(demergerErrorOutputFilename);
    }


    protected void createOutputEntry(LaborOriginEntry entry, PrintStream ps) throws IOException {
        try {
            ps.printf("%s\n", entry.getLine());
        }
        catch (Exception e) {
            throw new IOException(e.toString(),e);
        }
    }

    protected void createOutputEntry(String line, PrintStream ps) throws IOException {
        try {
            ps.printf("%s\n", line);
        }
        catch (Exception e) {
            throw new IOException(e.toString(),e);
        }
    }

    protected boolean checkEntry(LaborOriginEntry validEntry, LaborOriginEntry errorEntry, String documentTypeCode) {
        String documentNumber = errorEntry.getDocumentNumber();
        String originationCode = errorEntry.getFinancialSystemOriginationCode();

        if (validEntry.getDocumentNumber().equals(documentNumber) && validEntry.getFinancialDocumentTypeCode().equals(documentTypeCode) && validEntry.getFinancialSystemOriginationCode().equals(originationCode)) {
            return true;
        }
        return false;
    }

    /**
     * Generates a transaction listing report for labor origin entries that were valid
     */
    protected void generateScrubberTransactionsOnline() {
        try {
            Iterator<LaborOriginEntry> generatedTransactions = new LaborOriginEntryFileIterator(new File(inputFile));

            ((WrappingBatchService) laborGeneratedTransactionsReportWriterService).initialize();
            new TransactionListingReport().generateReport(laborGeneratedTransactionsReportWriterService, generatedTransactions);
        }
        finally {
            ((WrappingBatchService) laborGeneratedTransactionsReportWriterService).destroy();
        }
    }

    /**
     * Generates a transaction listing report for labor origin entries with bad balance types
     */
    protected void generateScrubberBadBalanceTypeListingReport() {
        LaborOriginEntryFilter blankBalanceTypeFilter = new LaborOriginEntryFilter() {
            @Override
            public boolean accept(LaborOriginEntry originEntry) {
                BalanceType originEntryBalanceType = laborAccountingCycleCachingService.getBalanceType(originEntry.getFinancialBalanceTypeCode());
                return ObjectUtils.isNull(originEntryBalanceType);
            }
        };
        try {
            ((WrappingBatchService) laborBadBalanceTypeReportWriterService).initialize();
            Iterator<LaborOriginEntry> blankBalanceOriginEntries = new FilteringLaborOriginEntryFileIterator(new File(inputFile), blankBalanceTypeFilter);
            new TransactionListingReport().generateReport(laborBadBalanceTypeReportWriterService, blankBalanceOriginEntries);
        }
        finally {
            ((WrappingBatchService) laborBadBalanceTypeReportWriterService).destroy();
        }
    }

    /**
     * Generates a transaction listing report for labor origin entries with errors
     */
    protected void generateScrubberErrorListingReport(String errorFileName) {
        try {
            ((WrappingBatchService) laborErrorListingReportWriterService).initialize();
            Iterator<LaborOriginEntry> removedTransactions = new LaborOriginEntryFileIterator(new File(errorFileName));
            new TransactionListingReport().generateReport(laborErrorListingReportWriterService, removedTransactions);
        }
        finally {
            ((WrappingBatchService) laborErrorListingReportWriterService).destroy();
        }
    }
}
