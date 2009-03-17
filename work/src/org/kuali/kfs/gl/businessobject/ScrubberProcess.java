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
package org.kuali.kfs.gl.businessobject;

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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.A21SubAccount;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.BalanceType;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.OffsetDefinition;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.coa.service.OffsetDefinitionService;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.ObjectHelper;
import org.kuali.kfs.gl.batch.BatchSortUtil;
import org.kuali.kfs.gl.batch.CollectorBatch;
import org.kuali.kfs.gl.batch.ScrubberStep;
import org.kuali.kfs.gl.batch.DemergerSortStep.DemergerSortComparator;
import org.kuali.kfs.gl.batch.ScrubberSortStep.ScrubberSortComparator;
import org.kuali.kfs.gl.batch.service.OriginEntryLookupService;
import org.kuali.kfs.gl.batch.service.RunDateService;
import org.kuali.kfs.gl.batch.service.ScrubberProcessObjectCodeOverride;
import org.kuali.kfs.gl.report.CollectorReportData;
import org.kuali.kfs.gl.service.OriginEntryGroupService;
import org.kuali.kfs.gl.service.OriginEntryLiteService;
import org.kuali.kfs.gl.service.OriginEntryService;
import org.kuali.kfs.gl.service.ReportService;
import org.kuali.kfs.gl.service.ScrubberReportData;
import org.kuali.kfs.gl.service.ScrubberValidator;
import org.kuali.kfs.gl.service.impl.CachingLookup;
import org.kuali.kfs.gl.service.impl.ScrubberStatus;
import org.kuali.kfs.gl.service.impl.ScrubberValidatorImpl;
import org.kuali.kfs.gl.service.impl.StringHelper;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.Message;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.businessobject.UniversityDate;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.dataaccess.UniversityDateDao;
import org.kuali.kfs.sys.exception.InvalidFlexibleOffsetException;
import org.kuali.kfs.sys.service.FlexibleOffsetAccountService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.ParameterEvaluator;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.service.PersistenceService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
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

enum GROUP_TYPE {VALID, ERROR, EXPIRED}
    
    private static final String COST_SHARE_CODE = "CSHR";

    private static final String COST_SHARE_TRANSFER_ENTRY_IND = "***";

    // These lengths are different then database field lengths, hence they are not from the DD
    private static final int COST_SHARE_ENCUMBRANCE_ENTRY_MAXLENGTH = 28;
    private static final int DEMERGER_TRANSACTION_LEDGET_ENTRY_DESCRIPTION = 33;
    private static final int OFFSET_MESSAGE_MAXLENGTH = 33;

    /* Services required */
    private FlexibleOffsetAccountService flexibleOffsetAccountService;
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
    private ScrubberValidatorImpl scrubberValidator;
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

    /**
     * Whether this instance is being used to support the scrubbing of a collector batch
     */
    private boolean collectorMode;
    private String batchFileDirectoryName;
    
    PrintStream OUTPUT_GLE_FILE_ps;
    PrintStream OUTPUT_ERR_FILE_ps;
    PrintStream OUTPUT_EXP_FILE_ps;

    private String inputFile;
    private String validFile;
    private String errorFile;
    private String expiredFile;

    /**
     * These parameters are all the dependencies.
     */
    public ScrubberProcess(FlexibleOffsetAccountService flexibleOffsetAccountService, OriginEntryService originEntryService, OriginEntryGroupService originEntryGroupService, DateTimeService dateTimeService, OffsetDefinitionService offsetDefinitionService, ObjectCodeService objectCodeService, KualiConfigurationService configurationService, UniversityDateDao universityDateDao, PersistenceService persistenceService, ReportService reportService, ScrubberValidator scrubberValidator, ScrubberProcessObjectCodeOverride scrubberProcessObjectCodeOverride, RunDateService runDateService, OriginEntryLiteService originEntryLiteService, String batchFileDirectoryName) {
        super();
        this.flexibleOffsetAccountService = flexibleOffsetAccountService;
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
        this.scrubberValidator = (ScrubberValidatorImpl) scrubberValidator;
        this.unscrubbedToUnscrubbedEntries = new HashMap<OriginEntry, OriginEntry>();
        this.scrubberProcessObjectCodeOverride = scrubberProcessObjectCodeOverride;
        this.runDateService = runDateService;
        collectorMode = false;
        parameterService = SpringContext.getBean(ParameterService.class);
        this.batchFileDirectoryName = batchFileDirectoryName;
    }

    /**
     * Scrub this single group read only. This will only output the scrubber report. It won't output any other groups.
     * 
     * @param group the origin entry group that should be scrubbed
     * @param the document number of any specific entries to scrub
     */
    public void scrubGroupReportOnly(String fileName, String documentNumber) {
        LOG.debug("scrubGroupReportOnly() started");
        this.inputFile = fileName;
        this.validFile = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        this.errorFile = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        this.expiredFile = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.SCRUBBER_EXPIRED_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        
        scrubEntries(true, documentNumber);
        
        File deleteValidFile = new File(validFile);
        File deleteErrorFile = new File(errorFile);
        File deleteExpiredFile = new File(expiredFile);
        try {
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
    public void scrubEntries() {
        this.inputFile = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        this.validFile = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        this.errorFile = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        this.expiredFile = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.SCRUBBER_EXPIRED_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;

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
    public ScrubberStatus scrubCollectorBatch(CollectorBatch batch, CollectorReportData collectorReportData) {
        collectorMode = true;

        this.inputFile = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        this.validFile = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        this.errorFile = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        this.expiredFile = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.SCRUBBER_EXPIRED_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
       
        // sort input file
        String scrubberSortInputFile = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.BACKUP_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        String scrubberSortOutputFile = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        BatchSortUtil.sortTextFileWithFields(scrubberSortInputFile, scrubberSortOutputFile, new ScrubberSortComparator());
        
        scrubEntries(false, null);
        
        //sort scrubber error file for demerger
        String demergerSortInputFile = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION; 
        String demergerSortOutputFile = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION; 
        BatchSortUtil.sortTextFileWithFields(demergerSortInputFile, demergerSortOutputFile, new DemergerSortComparator());
        
        performDemerger();
        
        // the scrubber process has just updated several member variables of this class. Store these values for the collector report
        collectorReportData.setBatchOriginEntryScrubberErrors(batch, scrubberReportErrors);
        collectorReportData.setScrubberReportData(batch, scrubberReport);
        collectorReportData.setDemergerReportData(batch, demergerReport);

        ScrubberStatus scrubberStatus = new ScrubberStatus();
        scrubberStatus.setUnscrubbedToScrubbedEntries(unscrubbedToUnscrubbedEntries);
        
        // report purpose - commented out.  If we need, the put string values for fileNames.
        scrubberStatus.setInputFileName(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE);
        scrubberStatus.setValidFileName(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE);
        scrubberStatus.setErrorFileName(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE);
        scrubberStatus.setExpiredFileName(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_EXPIRED_OUTPUT_FILE);
        scrubberStatus.setUnscrubbedToScrubbedEntries(unscrubbedToUnscrubbedEntries);

        return scrubberStatus;
    }

    /**
     * Scrub all entries that need it in origin entry. Put valid scrubbed entries in a scrubber valid group, put errors in a
     * scrubber error group, and transactions with an expired account in the scrubber expired account group.
     * @param group the specific origin entry group to scrub
     * @param documentNumber the number of the document with entries to scrub
     */
    public void scrubEntries(boolean reportOnlyMode, String documentNumber) {
        LOG.debug("scrubEntries() started");

        // We are in report only mode if reportOnlyMode is true.
        // if not, we are in batch mode and we scrub the backup group

        scrubberReportErrors = new HashMap<Transaction, List<Message>>();

        // setup an object to hold the "default" date information
        runDate = calculateRunDate(dateTimeService.getCurrentDate());
        runCal = Calendar.getInstance();
        runCal.setTime(runDate);

        universityRunDate = referenceLookup.get().getUniversityDate(runDate);
        if (universityRunDate == null) {
            throw new IllegalStateException(configurationService.getPropertyString(KFSKeyConstants.ERROR_UNIV_DATE_NOT_FOUND));
        }

        setOffsetString();
        setDescriptions();

        // Create the groups that will store the valid and error entries that come out of the scrubber
        // We don't need groups for the reportOnlyMode

        scrubberReport = new ScrubberReportData();
        //TODO: TEMPORARY TESTING!
        processGroup(reportOnlyMode);
        reportService.generateBatchScrubberStatisticsReport(runDate, scrubberReport, scrubberReportErrors);
    }

    /**
     * The demerger process reads all of the documents in the error group, then moves all of the original entries for that document
     * from the valid group to the error group. It does not move generated entries to the error group. Those are deleted. It also
     * modifies the doc number and origin code of cost share transfers.
     * 
     * @param errorGroup this scrubber run's error group
     * @param validGroup this scrubber run's valid group
     */
    public void performDemerger() {
        LOG.debug("performDemerger() started");

        // Without this step, the job fails with Optimistic Lock Exceptions
        persistenceService.clearCache();

        demergerReport = new DemergerReportData();
        
        //shawn - set runDate here again, because demerger is calling outside from scrubber
        runDate = calculateRunDate(dateTimeService.getCurrentDate());
        runCal = Calendar.getInstance();
        runCal.setTime(runDate);
        
        // demerger called by outside from scrubber, so reset those values
        setOffsetString();
        setDescriptions();
        
        // new demerger starts
        
        String validOutputFilename = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION; 
        String errorOutputFilename = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;

        // Without this step, the job fails with Optimistic Lock Exceptions
        persistenceService.clearCache();
        
        FileReader INPUT_GLE_FILE = null;
        FileReader INPUT_ERR_FILE = null;
        BufferedReader INPUT_GLE_FILE_br;
        BufferedReader INPUT_ERR_FILE_br;
        PrintStream OUTPUT_DEMERGER_GLE_FILE_ps;
        PrintStream OUTPUT_DEMERGER_ERR_FILE_ps;
        
        String demergerValidOutputFilename = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION; 
        String demergerErrorOutputFilename = batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;

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

        Collection<OriginEntryFull> validEntryCollection = new ArrayList();
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
               
                //shawn - Demerger only catch IOexception since demerger report doesn't display
                // detail error message.
                try{
                   //validLine is null means that errorLine is not null 
                   if (org.apache.commons.lang.StringUtils.isEmpty(currentValidLine)) {
                       String errorDesc = currentErrorLine.substring(51, 91);
                       String errorFinancialBalanceTypeCode = currentErrorLine.substring(25, 27);
                       
                       if (!checkingBypassEntry(errorFinancialBalanceTypeCode, errorDesc, demergerReport)){
                           createOutputEntry(currentErrorLine, OUTPUT_DEMERGER_ERR_FILE_ps);
                           errorSaved++;    
                       }
                       currentErrorLine = INPUT_ERR_FILE_br.readLine();
                       errorReadLine++;
                       continue;
                   }
                   
                   String financialBalanceTypeCode = currentValidLine.substring(25, 27);
                   String desc = currentValidLine.substring(51, 91);
                   
                   //errorLine is null means that validLine is not null
                   if (org.apache.commons.lang.StringUtils.isEmpty(currentErrorLine)) {
                       // Read all the transactions in the valid group and update the cost share transactions
                       String updatedValidLine = checkAndSetTransactionTypeCostShare(financialBalanceTypeCode, desc, currentValidLine);
                       createOutputEntry(updatedValidLine, OUTPUT_DEMERGER_GLE_FILE_ps);
                       validSaved++;
                       currentValidLine = INPUT_GLE_FILE_br.readLine();
                       validReadLine++;
                       continue;
                   }
                   
                   String compareStringFromValidEntry = currentValidLine.substring(31, 46); 
                   String compareStringFromErrorEntry = currentErrorLine.substring(31, 46);
                   
                   String errorDesc = currentErrorLine.substring(51, 91);
                   String errorFinancialBalanceTypeCode = currentErrorLine.substring(25, 27);
                   
                   if (compareStringFromValidEntry.compareTo(compareStringFromErrorEntry) < 0){
                       // Read all the transactions in the valid group and update the cost share transactions
                       String updatedValidLine = checkAndSetTransactionTypeCostShare(financialBalanceTypeCode, desc, currentValidLine);
                       createOutputEntry(updatedValidLine, OUTPUT_DEMERGER_GLE_FILE_ps);
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
            // FIXME: do whatever should be done here
            LOG.error("performDemerger Stopped: " + e.getMessage());
            throw new RuntimeException("performDemerger Stopped: " + e.getMessage(), e);
        }
        demergerReport.setErrorTransactionWritten(errorSaved);
        demergerReport.setErrorTransactionsRead(errorReadLine);
        demergerReport.setValidTransactionsSaved(validSaved);
        
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
    
    
    private String getTransactionType(String financialBalanceTypeCode, String desc) {
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
    private void processGroup(boolean reportOnlyMode) {
        this.referenceLookup.get().setLookupService(SpringContext.getBean(CachingLookup.class));

        OriginEntryFull lastEntry = null;
        scrubCostShareAmount = KualiDecimal.ZERO;
        unitOfWork = new UnitOfWorkInfo();

        FileReader INPUT_GLE_FILE = null;
        String GLEN_RECORD;
        BufferedReader INPUT_GLE_FILE_br;
        try {
            INPUT_GLE_FILE = new FileReader(inputFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            OUTPUT_GLE_FILE_ps = new PrintStream(validFile);
            OUTPUT_ERR_FILE_ps = new PrintStream(errorFile);
            OUTPUT_EXP_FILE_ps = new PrintStream(expiredFile);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        INPUT_GLE_FILE_br = new BufferedReader(INPUT_GLE_FILE);
        int line = 0;
        LOG.info("Starting Scrubber Process process group...");
        try {
            while ((GLEN_RECORD = INPUT_GLE_FILE_br.readLine()) != null) {
                if (!org.apache.commons.lang.StringUtils.isEmpty(GLEN_RECORD) && !org.apache.commons.lang.StringUtils.isBlank(GLEN_RECORD.trim())) {
                line++;
                OriginEntryFull unscrubbedEntry = new OriginEntryFull();
                List<Message> tmperrors = unscrubbedEntry.setFromTextFileForBatch(GLEN_RECORD, line);
                scrubberReport.incrementUnscrubbedRecordsRead();
                transactionErrors = new ArrayList<Message>();
                // This is done so if the code modifies this row, then saves it, it will be an insert,
                // and it won't touch the original. The Scrubber never modifies input rows/groups.
                // not relevant for file version
    
                boolean saveErrorTransaction = false;
                boolean saveValidTransaction = false;
    
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
    
                // For Labor Scrubber
                boolean laborIndicator = false;
                tmperrors.addAll(scrubberValidator.validateTransaction(unscrubbedEntry, scrubbedEntry, universityRunDate, laborIndicator));
                transactionErrors.addAll(tmperrors);
                
                
                Account unscrubbedEntryAccount = referenceLookup.get().getAccount(unscrubbedEntry);
                if ((unscrubbedEntryAccount != null) && (!unscrubbedEntryAccount.isActive())) {
                    // Make a copy of it so OJB doesn't just update the row in the original
                    // group. It needs to make a new one in the expired group
                    OriginEntryFull expiredEntry = OriginEntryFull.copyFromOriginEntryable(scrubbedEntry);
                    createOutputEntry(expiredEntry, OUTPUT_EXP_FILE_ps);
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
                            // pass the String line for generating error files
                            generateOffset(lastEntry);
    
                            unitOfWork = new UnitOfWorkInfo(scrubbedEntry);
                        }
    
                        KualiDecimal transactionAmount = scrubbedEntry.getTransactionLedgerEntryAmount();
    
                        ParameterEvaluator offsetFiscalPeriods = parameterService.getParameterEvaluator(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.OFFSET_FISCAL_PERIOD_CODES, scrubbedEntry.getUniversityFiscalPeriodCode());
    
                        BalanceType scrubbedEntryBalanceType = referenceLookup.get().getBalanceType(scrubbedEntry);
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
                        // major assumption: the a21 subaccount is proxied, so we don't want to query the database if the subacct
                        // number is dashes
                        if (!KFSConstants.getDashSubAccountNumber().equals(scrubbedEntry.getSubAccountNumber())) {
                            A21SubAccount scrubbedEntryA21SubAccount = referenceLookup.get().getA21SubAccount(scrubbedEntry);
                            if (ObjectUtils.isNotNull(scrubbedEntryA21SubAccount)) {
                                subAccountTypeCode = scrubbedEntryA21SubAccount.getSubAccountTypeCode();
                            }
                        }
    
                        ParameterEvaluator costShareObjectTypeCodes = parameterService.getParameterEvaluator(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.COST_SHARE_OBJ_TYPE_CODES, scrubbedEntry.getFinancialObjectTypeCode());
                        ParameterEvaluator costShareEncBalanceTypeCodes = parameterService.getParameterEvaluator(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.COST_SHARE_ENC_BAL_TYP_CODES, scrubbedEntry.getFinancialBalanceTypeCode());
                        ParameterEvaluator costShareEncFiscalPeriodCodes = parameterService.getParameterEvaluator(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.COST_SHARE_ENC_FISCAL_PERIOD_CODES, scrubbedEntry.getUniversityFiscalPeriodCode());
                        ParameterEvaluator costShareEncDocTypeCodes = parameterService.getParameterEvaluator(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.COST_SHARE_ENC_DOC_TYPE_CODES, scrubbedEntry.getFinancialDocumentTypeCode().trim());
                        ParameterEvaluator costShareFiscalPeriodCodes = parameterService.getParameterEvaluator(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.COST_SHARE_FISCAL_PERIOD_CODES, scrubbedEntry.getUniversityFiscalPeriodCode());
                        Account scrubbedEntryAccount = referenceLookup.get().getAccount(scrubbedEntry);

                        if (costShareObjectTypeCodes.evaluationSucceeds() && costShareEncBalanceTypeCodes.evaluationSucceeds() && scrubbedEntryAccount.isForContractsAndGrants() && KFSConstants.SubAccountType.COST_SHARE.equals(subAccountTypeCode) && costShareEncFiscalPeriodCodes.evaluationSucceeds() && costShareEncDocTypeCodes.evaluationSucceeds()) {
                            TransactionError te1 = generateCostShareEncumbranceEntries(scrubbedEntry);
                            if (te1 != null) {
                                List errors = new ArrayList();
                                errors.add(te1.message);
                                scrubberReportErrors.put(te1.transaction, errors);
    
                                saveValidTransaction = false;
                                saveErrorTransaction = true;
                            }
                        }
    
                        SystemOptions scrubbedEntryOption = referenceLookup.get().getSystemOptions(scrubbedEntry);
                        if (costShareObjectTypeCodes.evaluationSucceeds() && scrubbedEntryOption.getActualFinancialBalanceTypeCd().equals(scrubbedEntry.getFinancialBalanceTypeCode()) && scrubbedEntryAccount.isForContractsAndGrants() && KFSConstants.SubAccountType.COST_SHARE.equals(subAccountTypeCode) && costShareFiscalPeriodCodes.evaluationSucceeds() && costShareEncDocTypeCodes.evaluationSucceeds()) {
                            if (scrubbedEntry.isDebit()) {
                                scrubCostShareAmount = scrubCostShareAmount.subtract(transactionAmount);
                            }
                            else {
                                scrubCostShareAmount = scrubCostShareAmount.add(transactionAmount);
                            }
                        }
    
                        ParameterEvaluator otherDocTypeCodes = parameterService.getParameterEvaluator(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.OFFSET_DOC_TYPE_CODES, scrubbedEntry.getFinancialDocumentTypeCode());
    
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
                                createOutputEntry(GLEN_RECORD, OUTPUT_ERR_FILE_ps);    
                                scrubberReport.incrementErrorRecordWritten();
                                unitOfWork.errorsFound = true;
    
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
                    unitOfWork.errorsFound = true;
                }
                } 
            }
    
            if (!collectorMode) {
                // Generate last offset (if necessary)
                generateOffset(lastEntry);
            }
    
            INPUT_GLE_FILE_br.close();
            INPUT_GLE_FILE.close();
            OUTPUT_GLE_FILE_ps.close();
            OUTPUT_ERR_FILE_ps.close();
            OUTPUT_EXP_FILE_ps.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
        try {
            OriginEntryFull costShareEntry = OriginEntryFull.copyFromOriginEntryable(scrubbedEntry);

            SystemOptions scrubbedEntryOption = referenceLookup.get().getSystemOptions(scrubbedEntry);
            A21SubAccount scrubbedEntryA21SubAccount = referenceLookup.get().getA21SubAccount(scrubbedEntry);

            costShareEntry.setFinancialObjectCode(parameterService.getParameterValue(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupParameters.COST_SHARE_OBJECT_CODE_PARM_NM));
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
            OffsetDefinition offsetDefinition = referenceLookup.get().getOffsetDefinition(scrubbedEntry.getUniversityFiscalYear(), scrubbedEntry.getChartOfAccountsCode(), KFSConstants.TRANSFER_FUNDS, scrubbedEntry.getFinancialBalanceTypeCode());
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
            offsetDefinition = referenceLookup.get().getOffsetDefinition(scrubbedEntry.getUniversityFiscalYear(), scrubbedEntry.getChartOfAccountsCode(), KFSConstants.TRANSFER_FUNDS, scrubbedEntry.getFinancialBalanceTypeCode());
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
    private void setDescriptions() {
        //TODO: Shawn - might need to use constants
        offsetDescription = "GENERATED OFFSET";
        capitalizationDescription = "GENERATED CAPITALIZATION";
        liabilityDescription = "GENERATED LIABILITY";
        costShareDescription = "GENERATED COST SHARE FROM";
        transferDescription = "GENERATED TRANSFER FROM";
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
        String msg = offsetDescription + GeneralLedgerConstants.getSpaceTransactionLedgetEntryDescription();

        return msg.substring(0, OFFSET_MESSAGE_MAXLENGTH) + offsetString;
    }

    /**
     * Generates capitalization entries if necessary
     * 
     * @param scrubbedEntry the entry to generate capitalization entries (possibly) for
     * @return null if no error, message if error
     */
    private String processCapitalization(OriginEntry scrubbedEntry) {
        
        try {
         // Lines 4694 - 4798 of the Pro Cobol listing on Confluence
            if (!parameterService.getIndicatorParameter(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupParameters.CAPITALIZATION_IND)) {
                return null;
            }

            OriginEntryFull capitalizationEntry = OriginEntryFull.copyFromOriginEntryable(scrubbedEntry);
            SystemOptions scrubbedEntryOption = referenceLookup.get().getSystemOptions(scrubbedEntry);
            ObjectCode scrubbedEntryObjectCode = referenceLookup.get().getFinancialObject(scrubbedEntry);
            Chart scrubbedEntryChart = referenceLookup.get().getChart(scrubbedEntry);
            Account scrubbedEntryAccount = referenceLookup.get().getAccount(scrubbedEntry);

            ParameterEvaluator documentTypeCodes = parameterService.getParameterEvaluator(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.CAPITALIZATION_DOC_TYPE_CODES, scrubbedEntry.getFinancialDocumentTypeCode());
            ParameterEvaluator fiscalPeriodCodes = parameterService.getParameterEvaluator(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.CAPITALIZATION_FISCAL_PERIOD_CODES, scrubbedEntry.getUniversityFiscalPeriodCode());
            ParameterEvaluator objectSubTypeCodes = parameterService.getParameterEvaluator(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.CAPITALIZATION_OBJ_SUB_TYPE_CODES, scrubbedEntryObjectCode.getFinancialObjectSubTypeCode());
            ParameterEvaluator subFundGroupCodes = parameterService.getParameterEvaluator(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.CAPITALIZATION_SUB_FUND_GROUP_CODES, scrubbedEntryAccount.getSubFundGroupCode());
            ParameterEvaluator chartCodes = parameterService.getParameterEvaluator(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.CAPITALIZATION_CHART_CODES, scrubbedEntry.getChartOfAccountsCode());

            if (scrubbedEntry.getFinancialBalanceTypeCode().equals(scrubbedEntryOption.getActualFinancialBalanceTypeCd()) && scrubbedEntry.getUniversityFiscalYear().intValue() > 1995 && documentTypeCodes.evaluationSucceeds() && fiscalPeriodCodes.evaluationSucceeds() && objectSubTypeCodes.evaluationSucceeds() && subFundGroupCodes.evaluationSucceeds() && chartCodes.evaluationSucceeds()) {

                String objectSubTypeCode = scrubbedEntryObjectCode.getFinancialObjectSubTypeCode();

                String capitalizationObjectCode = parameterService.getParameterValue(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupParameters.CAPITALIZATION_SUBTYPE_OBJECT, objectSubTypeCode);
                if (capitalizationObjectCode != null) {
                    capitalizationEntry.setFinancialObjectCode(capitalizationObjectCode);
                    capitalizationEntry.setFinancialObject(referenceLookup.get().getObjectCode(capitalizationEntry.getUniversityFiscalYear(), capitalizationEntry.getChartOfAccountsCode(), capitalizationEntry.getFinancialObjectCode()));
                }

                capitalizationEntry.setFinancialObjectTypeCode(scrubbedEntryOption.getFinancialObjectTypeAssetsCd());
                capitalizationEntry.setTransactionLedgerEntryDescription(capitalizationDescription);

                plantFundAccountLookup(scrubbedEntry, capitalizationEntry);

                capitalizationEntry.setUniversityFiscalPeriodCode(scrubbedEntry.getUniversityFiscalPeriodCode());

                createOutputEntry(capitalizationEntry, OUTPUT_GLE_FILE_ps);
                scrubberReport.incrementCapitalizationEntryGenerated();

                // Clear out the id & the ojb version number to make sure we do an insert on the next one
                capitalizationEntry.setVersionNumber(null);
                capitalizationEntry.setEntryId(null);

                capitalizationEntry.setFinancialObjectCode(scrubbedEntryChart.getFundBalanceObjectCode());
                //TODO: check to see if COBOL does this - seems weird - is this saying if the object code doesn't exist use the value from options?  Shouldn't it always come from one or the other?
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
    private String processPlantIndebtedness(OriginEntry scrubbedEntry) {
        try{
            // Lines 4855 - 4979 of the Pro Cobol listing on Confluence 
            if (!parameterService.getIndicatorParameter(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupParameters.PLANT_INDEBTEDNESS_IND)) {
                return null;
            }

            OriginEntryFull plantIndebtednessEntry = OriginEntryFull.copyFromOriginEntryable(scrubbedEntry);

            SystemOptions scrubbedEntryOption = referenceLookup.get().getSystemOptions(scrubbedEntry);
            ObjectCode scrubbedEntryObjectCode = referenceLookup.get().getFinancialObject(scrubbedEntry);
            Account scrubbedEntryAccount = referenceLookup.get().getAccount(scrubbedEntry);
            Chart scrubbedEntryChart = referenceLookup.get().getChart(scrubbedEntry);
            scrubbedEntryAccount.setOrganization(referenceLookup.get().getOrg(scrubbedEntryAccount.getChartOfAccountsCode(), scrubbedEntryAccount.getOrganizationCode()));
            
            ParameterEvaluator objectSubTypeCodes = parameterService.getParameterEvaluator(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.PLANT_INDEBTEDNESS_OBJ_SUB_TYPE_CODES, scrubbedEntryObjectCode.getFinancialObjectSubTypeCode());
            ParameterEvaluator subFundGroupCodes = parameterService.getParameterEvaluator(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.PLANT_INDEBTEDNESS_SUB_FUND_GROUP_CODES, scrubbedEntryAccount.getSubFundGroupCode());

            if (scrubbedEntry.getFinancialBalanceTypeCode().equals(scrubbedEntryOption.getActualFinancialBalanceTypeCd()) && subFundGroupCodes.evaluationSucceeds() && objectSubTypeCodes.evaluationSucceeds()) {

                plantIndebtednessEntry.setTransactionLedgerEntryDescription(KFSConstants.PLANT_INDEBTEDNESS_ENTRY_DESCRIPTION);

                if (scrubbedEntry.isDebit()) {
                    plantIndebtednessEntry.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
                }
                else {
                    plantIndebtednessEntry.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
                }

                plantIndebtednessEntry.setTransactionScrubberOffsetGenerationIndicator(true);
                createOutputEntry(plantIndebtednessEntry, OUTPUT_GLE_FILE_ps);
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
    private String processLiabilities(OriginEntry scrubbedEntry) {
        try{
            // Lines 4799 to 4839 of the Pro Cobol list of the scrubber on Confluence
            if (!parameterService.getIndicatorParameter(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupParameters.LIABILITY_IND)) {
                return null;
            }
            OriginEntryFull liabilityEntry = OriginEntryFull.copyFromOriginEntryable(scrubbedEntry);
            Chart scrubbedEntryChart = referenceLookup.get().getChart(scrubbedEntry);
            SystemOptions scrubbedEntryOption = referenceLookup.get().getSystemOptions(scrubbedEntry);
            ObjectCode scrubbedEntryFinancialObject = referenceLookup.get().getFinancialObject(scrubbedEntry);
            Account scrubbedEntryAccount = referenceLookup.get().getAccount(scrubbedEntry);

            ParameterEvaluator chartCodes = parameterService.getParameterEvaluator(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.LIABILITY_CHART_CODES, scrubbedEntry.getChartOfAccountsCode());
            ParameterEvaluator docTypeCodes = parameterService.getParameterEvaluator(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.LIABILITY_DOC_TYPE_CODES, scrubbedEntry.getFinancialDocumentTypeCode());
            ParameterEvaluator fiscalPeriods = parameterService.getParameterEvaluator(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.LIABILITY_FISCAL_PERIOD_CODES, scrubbedEntry.getUniversityFiscalPeriodCode());
            ParameterEvaluator objSubTypeCodes = parameterService.getParameterEvaluator(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.LIABILITY_OBJ_SUB_TYPE_CODES, scrubbedEntryFinancialObject.getFinancialObjectSubTypeCode());
            ParameterEvaluator subFundGroupCodes = parameterService.getParameterEvaluator(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.LIABILITY_SUB_FUND_GROUP_CODES, scrubbedEntryAccount.getSubFundGroupCode());

            if (scrubbedEntry.getFinancialBalanceTypeCode().equals(scrubbedEntryOption.getActualFinancialBalanceTypeCd()) && scrubbedEntry.getUniversityFiscalYear().intValue() > 1995 && docTypeCodes.evaluationSucceeds() && fiscalPeriods.evaluationSucceeds() && objSubTypeCodes.evaluationSucceeds() && subFundGroupCodes.evaluationSucceeds() && chartCodes.evaluationSucceeds()) {

                liabilityEntry.setFinancialObjectCode(parameterService.getParameterValue(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupParameters.LIABILITY_OBJECT_CODE));
                liabilityEntry.setFinancialObjectTypeCode(scrubbedEntryOption.getFinObjectTypeLiabilitiesCode());

                liabilityEntry.setTransactionDebitCreditCode(scrubbedEntry.getTransactionDebitCreditCode());
                liabilityEntry.setTransactionLedgerEntryDescription(liabilityDescription);
                plantFundAccountLookup(scrubbedEntry, liabilityEntry);

                createOutputEntry(liabilityEntry, OUTPUT_GLE_FILE_ps);
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
     * Updates the entries with the proper chart and account for the plant fund
     * 
     * @param scrubbedEntry basis for plant fund entry
     * @param liabilityEntry liability entry
     */
    private void plantFundAccountLookup(OriginEntry scrubbedEntry, OriginEntryFull liabilityEntry) {
        // 4000-PLANT-FUND-ACCT to 4000-PLANT-FUND-ACCT-EXIT in cobol
        
        liabilityEntry.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
        ObjectCode scrubbedEntryObjectCode = referenceLookup.get().getFinancialObject(scrubbedEntry);
        Account scrubbedEntryAccount = referenceLookup.get().getAccount(scrubbedEntry);
        scrubbedEntryAccount.setOrganization(referenceLookup.get().getOrg(scrubbedEntryAccount.getChartOfAccountsCode(), scrubbedEntryAccount.getOrganizationCode()));

            String objectSubTypeCode = scrubbedEntryObjectCode.getFinancialObjectSubTypeCode();
            ParameterEvaluator campusObjSubTypeCodes = parameterService.getParameterEvaluator(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.PLANT_FUND_CAMPUS_OBJECT_SUB_TYPE_CODES, objectSubTypeCode);
            ParameterEvaluator orgObjSubTypeCodes = parameterService.getParameterEvaluator(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.PLANT_FUND_ORG_OBJECT_SUB_TYPE_CODES, objectSubTypeCode);

            if (campusObjSubTypeCodes.evaluationSucceeds()) {
                liabilityEntry.setAccountNumber(scrubbedEntryAccount.getOrganization().getCampusPlantAccountNumber());
                liabilityEntry.setChartOfAccountsCode(scrubbedEntryAccount.getOrganization().getCampusPlantChartCode());
            }
            else if (orgObjSubTypeCodes.evaluationSucceeds()) {
                liabilityEntry.setAccountNumber(scrubbedEntryAccount.getOrganization().getOrganizationPlantAccountNumber());
                liabilityEntry.setChartOfAccountsCode(scrubbedEntryAccount.getOrganization().getOrganizationPlantChartCode());
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

            A21SubAccount scrubbedEntryA21SubAccount = referenceLookup.get().getA21SubAccount(scrubbedEntry);
            SystemOptions scrubbedEntryOption = referenceLookup.get().getSystemOptions(scrubbedEntry);

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
            OffsetDefinition offset = referenceLookup.get().getOffsetDefinition(costShareEncumbranceEntry.getUniversityFiscalYear(), costShareEncumbranceEntry.getChartOfAccountsCode(), costShareEncumbranceEntry.getFinancialDocumentTypeCode(), costShareEncumbranceEntry.getFinancialBalanceTypeCode());

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
            String param = parameterService.getParameterValue(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupParameters.COST_SHARE_OBJECT_CODE_BY_LEVEL_PARM_NM, originEntryObjectLevelCode);
            if (param == null) {
                param = parameterService.getParameterValue(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupParameters.COST_SHARE_OBJECT_CODE_BY_LEVEL_PARM_NM, "DEFAULT");
                if (param == null) {
                    throw new RuntimeException("Unable to determine cost sharing object code from object level.  Default entry missing.");
                }
            }
            originEntryObjectCode = param;
        }

        // Lookup the new object code
        ObjectCode objectCode = referenceLookup.get().getObjectCode(costShareEntry.getUniversityFiscalYear(), costShareEntry.getChartOfAccountsCode(), originEntryObjectCode);
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

            ParameterEvaluator docTypeRule = parameterService.getParameterEvaluator(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.OFFSET_DOC_TYPE_CODES, scrubbedEntry.getFinancialDocumentTypeCode());
            if (!docTypeRule.evaluationSucceeds()) {
                return true;
            }

            // do nothing if flexible offset is enabled and scrubber offset indicator of the document
            // type code is turned off in the document type table
            if ((!shouldScrubberGenerateOffsetsForDocType(scrubbedEntry.getFinancialDocumentTypeCode())) && flexibleOffsetAccountService.getEnabled()) {
                return true;
            }
            
            // Create an offset
            offsetEntry = OriginEntryFull.copyFromOriginEntryable(scrubbedEntry);
            offsetEntry.setTransactionLedgerEntryDescription(offsetDescription);

            //of course this method should go elsewhere, not in ScrubberValidator!
            OffsetDefinition offsetDefinition = referenceLookup.get().getOffsetDefinition(scrubbedEntry.getUniversityFiscalYear(), scrubbedEntry.getChartOfAccountsCode(), scrubbedEntry.getFinancialDocumentTypeCode(), scrubbedEntry.getFinancialBalanceTypeCode());
            if (offsetDefinition != null) {
                if (offsetDefinition.getFinancialObject() == null) {
                    StringBuffer offsetKey = new StringBuffer(offsetDefinition.getUniversityFiscalYear());
                    offsetKey.append("-");
                    offsetKey.append(offsetDefinition.getChartOfAccountsCode());
                    offsetKey.append("-");
                    offsetKey.append(offsetDefinition.getFinancialObjectCode());

                    putTransactionError(offsetEntry, configurationService.getPropertyString(KFSKeyConstants.ERROR_OFFSET_DEFINITION_OBJECT_CODE_NOT_FOUND), offsetKey.toString(), Message.TYPE_FATAL);

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

                putTransactionError(offsetEntry, configurationService.getPropertyString(KFSKeyConstants.ERROR_OFFSET_DEFINITION_NOT_FOUND), sb.toString(), Message.TYPE_FATAL);

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
            offsetEntry.setTransactionDate(runDate);

            try {
                flexibleOffsetAccountService.updateOffset(offsetEntry);
            }
            catch (InvalidFlexibleOffsetException e) {
                LOG.debug("generateOffset() Offset Flexible Offset Error: " + e.getMessage());
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

    
    private void createOutputEntry(OriginEntry entry, PrintStream ps) throws IOException {
        try {
            ps.printf("%s\n", entry.getLine());
        } catch (Exception e) {
            throw new IOException(e.toString());
        }
    }
    
    private void createOutputEntry(String line, PrintStream ps) throws IOException {
        try {
            ps.printf("%s\n", line);
        } catch (Exception e) {
            throw new IOException(e.toString());
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
     * Determines if the scrubber should generate offsets for the given document type
     * @param docTypeCode the document type code to check if it generates scrubber offsets
     * @return true if the scrubber should generate offsets for this doc type, false otherwise
     */
    private boolean shouldScrubberGenerateOffsetsForDocType(String docTypeCode) {
        // TODO check param!!!
        return true;
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
    
    private boolean checkingBypassEntry (String financialBalanceTypeCode, String desc, DemergerReportData demergerReport){
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
    
    
    private String checkAndSetTransactionTypeCostShare (String financialBalanceTypeCode, String desc, String currentValidLine){
        
        // Read all the transactions in the valid group and update the cost share transactions
        String transactionType = getTransactionType(financialBalanceTypeCode, desc);
        if (TRANSACTION_TYPE_COST_SHARE.equals(transactionType)) {
            OriginEntryLite transaction = new OriginEntryLite();
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
    
}

