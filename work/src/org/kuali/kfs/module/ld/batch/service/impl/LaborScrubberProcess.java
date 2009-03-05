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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.A21SubAccount;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.BalanceType;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.coa.service.OffsetDefinitionService;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.ObjectHelper;
import org.kuali.kfs.gl.batch.ScrubberStep;
import org.kuali.kfs.gl.batch.service.OriginEntryLookupService;
import org.kuali.kfs.gl.businessobject.DemergerReportData;
import org.kuali.kfs.gl.businessobject.OriginEntryGroup;
import org.kuali.kfs.gl.businessobject.OriginEntryStatistics;
import org.kuali.kfs.gl.businessobject.Transaction;
import org.kuali.kfs.gl.exception.LoadException;
import org.kuali.kfs.gl.service.OriginEntryGroupService;
import org.kuali.kfs.gl.service.ScrubberReportData;
import org.kuali.kfs.gl.service.ScrubberValidator;
import org.kuali.kfs.gl.service.impl.CachingLookup;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.batch.service.LaborReportService;
import org.kuali.kfs.module.ld.businessobject.LaborOriginEntry;
import org.kuali.kfs.module.ld.service.LaborOriginEntryService;
import org.kuali.kfs.module.ld.util.ReportRegistry;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.Message;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.businessobject.UniversityDate;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.dataaccess.UniversityDateDao;
import org.kuali.kfs.sys.service.FlexibleOffsetAccountService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.ParameterEvaluator;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.service.PersistenceService;
import org.kuali.rice.kns.util.BeanPropertyComparator;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * This class has the logic for the scrubber. It is required because the scrubber process needs instance variables. Instance
 * variables in a spring service are shared between all code calling the service. This will make sure each run of the scrubber has
 * it's own instance variables instead of being shared.
 */
public class LaborScrubberProcess {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborScrubberProcess.class);

    // 40 spaces - used for filling in descriptions with spaces
    private static String SPACES = "                                        ";

    /* Services required */
    private FlexibleOffsetAccountService flexibleOffsetAccountService;
    private LaborOriginEntryService laborOriginEntryService;
    private OriginEntryGroupService originEntryGroupService;
    private DateTimeService dateTimeService;
    private OffsetDefinitionService offsetDefinitionService;
    private ObjectCodeService objectCodeService;
    private KualiConfigurationService kualiConfigurationService;
    private UniversityDateDao universityDateDao;
    private PersistenceService persistenceService;
    private LaborReportService laborReportService;
    private ScrubberValidator scrubberValidator;
    
    private String batchFileDirectoryName;

    enum GROUP_TYPE {
        VALID, ERROR, EXPIRED
    }

    /* These are all different forms of the run date for this job */
    private Date runDate;
    private Calendar runCal;
    private UniversityDate universityRunDate;
    private String offsetString;

    /*
     * These fields are used to control whether the job was run before some set time, if so, the rundate of the job will be set to
     * 11:59 PM of the previous day
     */
    private Integer cutoffHour;
    private Integer cutoffMinute;
    private Integer cutoffSecond;

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

    /* Description names */
    private String offsetDescription;
    private String capitalizationDescription;
    private String liabilityDescription;
    private String transferDescription;
    private String costShareDescription;

    /* Misc stuff */
    private ThreadLocal<OriginEntryLookupService> referenceLookup = new ThreadLocal<OriginEntryLookupService>();
    
    //TODO: change to FIS
    private String inputFile;
    private String validFile;
    private String errorFile; 
    private String expiredFile;

//    private String inputFileName = "c:/scrubberEntries.txt";
//    private String validOutputFilename = "c:/SCRBOUT1";
//    private String errorOutputFilename = "c:/SCRBERR1";
//    private String expiredOutputFilename = "c:/EXPACCTS";
 
    
    
    /**
     * These parameters are all the dependencies.
     */
    public LaborScrubberProcess(FlexibleOffsetAccountService flexibleOffsetAccountService, LaborOriginEntryService laborOriginEntryService, OriginEntryGroupService originEntryGroupService, DateTimeService dateTimeService, OffsetDefinitionService offsetDefinitionService, ObjectCodeService objectCodeService, KualiConfigurationService kualiConfigurationService, UniversityDateDao universityDateDao, PersistenceService persistenceService, LaborReportService laborReportService, ScrubberValidator scrubberValidator, String batchFileDirectoryName) {
        super();
        this.flexibleOffsetAccountService = flexibleOffsetAccountService;
        this.laborOriginEntryService = laborOriginEntryService;
        this.originEntryGroupService = originEntryGroupService;
        this.dateTimeService = dateTimeService;
        this.offsetDefinitionService = offsetDefinitionService;
        this.objectCodeService = objectCodeService;
        this.kualiConfigurationService = kualiConfigurationService;
        this.universityDateDao = universityDateDao;
        this.persistenceService = persistenceService;
        this.laborReportService = laborReportService;
        this.scrubberValidator = scrubberValidator;
        this.batchFileDirectoryName = batchFileDirectoryName;

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
        LOG.debug("scrubGroupReportOnly() started");
        this.inputFile = fileName;
        this.validFile = batchFileDirectoryName + File.separator + LaborConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION; 
        this.errorFile = batchFileDirectoryName + File.separator + LaborConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION; 
        this.expiredFile = batchFileDirectoryName + File.separator + LaborConstants.BatchFileSystem.SCRUBBER_EXPIRED_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION; 

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
        // We are in report only mode if reportOnlyMode is true.
        // if not, we are in batch mode and we scrub the backup group

//        LaborOriginEntryLookupService refLookup = SpringContext.getBean(LaborOriginEntryLookupService.class);
//        refLookup.setLookupService(SpringContext.getBean(CachingLookup.class));
//        scrubberValidator.setReferenceLookup(refLookup);

        String reportsDirectory = ReportRegistry.getReportsDirectory();

        scrubberReportErrors = new HashMap<Transaction, List<Message>>();

        // setup an object to hold the "default" date information
        runDate = calculateRunDate(dateTimeService.getCurrentDate());
        runCal = Calendar.getInstance();
        runCal.setTime(runDate);

        // FOR FIS batch
        // universityRunDate = universityDateDao.getByPrimaryKey(runDate);
        universityRunDate = referenceLookup.get().getUniversityDate(runDate);
        if (universityRunDate == null) {
            throw new IllegalStateException(kualiConfigurationService.getPropertyString(KFSKeyConstants.ERROR_UNIV_DATE_NOT_FOUND));
        }

        setOffsetString();
        setDescriptions();
        scrubberReport = new ScrubberReportData();
        processGroup();

        laborReportService.generateBatchScrubberStatisticsReport(scrubberReport, scrubberReportErrors, reportsDirectory, runDate);
        
        // run the demerger
        
        // Shawn - made it seperate batch step
//        if (!reportOnlyMode) {
//            performDemerger(errorOutputFilename, validOutputFilename);
//        }

        // Run the reports
        if (reportOnlyMode) {
            // Run transaction list
            //laborReportService.generateScrubberTransactionsOnline(group, documentNumber, reportsDirectory, runDate);
        }
        else {
            // Run bad balance type report and removed transaction report
            Collection badBalanceEntries = getBadBalanceEntries(inputFile);
            
            //laborReportService.generateScrubberBadBalanceTypeListingReport(badBalanceEntries, reportsDirectory, runDate);
            
            Collection removedTransactions = getRemovedTransactions(errorFile);
            //laborReportService.generateScrubberRemovedTransactions(errorGroup, reportsDirectory, runDate);
            //laborReportService.generateScrubberRemovedTransactions(removedTransactions, reportsDirectory, runDate);
        }

        
    }

    /**
     * Determine the type of the transaction by looking at attributes
     * 
     * @param transaction Transaction to identify
     * @return CE (Cost share encumbrance, O (Offset), C (apitalization), L (Liability), T (Transfer), CS (Cost Share), X (Other)
     */
    private String getTransactionType(LaborOriginEntry transaction) {
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
    private void processGroup() {
        this.referenceLookup.get().setLookupService(SpringContext.getBean(CachingLookup.class));
        ParameterService parameterService = SpringContext.getBean(ParameterService.class);

        LaborOriginEntry lastEntry = null;
        scrubCostShareAmount = KualiDecimal.ZERO;
        unitOfWork = new UnitOfWorkInfo();
        
        // TEMPORARY CHANGES JUST EXPERIMENTING WITH STUFF - DON'T CHECK IN LIKE THIS!
        FileReader INPUT_GLE_FILE = null;
        // FileWriter OUTPUT_GLE_FILE = null;
        // FIXME still have to make error file(s)
        String GLEN_RECORD;
        BufferedReader INPUT_GLE_FILE_br;
        PrintStream OUTPUT_GLE_FILE_ps;
        PrintStream OUTPUT_ERR_FILE_ps;
        PrintStream OUTPUT_EXP_FILE_ps;
        
        // BufferedWriter OUTPUT_GLE_FILE_bw;
        try {
            INPUT_GLE_FILE = new FileReader(inputFile);
            // FIXME TEMP FOR TESTING!!

        }
        catch (FileNotFoundException e) {
            // FIXME: do whatever is supposed to be done here
            throw new RuntimeException(e);
        }
        try {
            // OUTPUT_GLE_FILE = new FileWriter("scrbout1.txt");
            

            OUTPUT_GLE_FILE_ps = new PrintStream(validFile);
            OUTPUT_ERR_FILE_ps = new PrintStream(errorFile);
            OUTPUT_EXP_FILE_ps = new PrintStream(expiredFile);
        }
        catch (IOException e) {
            // FIXME: do whatever is supposed to be done here
            throw new RuntimeException(e);
        }

        INPUT_GLE_FILE_br = new BufferedReader(INPUT_GLE_FILE);
        // OUTPUT_GLE_FILE_bw = new BufferedWriter(OUTPUT_GLE_FILE);

        LOG.info("Starting Scrubber Process process group...");

        int lineNumber = 0;
        int loadedCount = 0;
        boolean errorsLoading = false;
        LaborOriginEntry unscrubbedEntry = new LaborOriginEntry();
        List<Message> tmperrors = new ArrayList(); 
        try {
            String currentLine = INPUT_GLE_FILE_br.readLine();

            while (currentLine != null) {
                boolean saveErrorTransaction = false;
                boolean saveValidTransaction = false;
                LaborOriginEntry scrubbedEntry = new LaborOriginEntry();
                
                // shawn - won't need this try catch block since above try will catch all IOexception
                try {
                    lineNumber++;
                    
                    if (!StringUtils.isEmpty(currentLine) && !StringUtils.isBlank(currentLine.trim())) {
                        try {
                            unscrubbedEntry = new LaborOriginEntry();
                            tmperrors = unscrubbedEntry.setFromTextFileForBatch(currentLine, lineNumber);
                            loadedCount++;
                        }
                        catch (LoadException e) {
                            errorsLoading = true;
                        }

                    // just test entry with the entry loaded above
                    scrubberReport.incrementUnscrubbedRecordsRead();

                    transactionErrors = new ArrayList<Message>();

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

                    try {
                        tmperrors.addAll(scrubberValidator.validateTransaction(unscrubbedEntry, scrubbedEntry, universityRunDate, laborIndicator));
                    } catch (Exception e){ 
                        transactionErrors.add(new Message(e.toString() + " occurred for this record.", Message.TYPE_FATAL));
                        saveValidTransaction = false;
                    }   
                    
                    transactionErrors.addAll(tmperrors);

                    // Expired account?
                    Account unscrubbedEntryAccount = referenceLookup.get().getAccount(unscrubbedEntry);
                    if (ObjectUtils.isNotNull(unscrubbedEntry.getAccount()) && !unscrubbedEntry.getAccount().isActive()) {
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

                            // generateOffset(lastEntry);

                            unitOfWork = new UnitOfWorkInfo(scrubbedEntry);
                        }

                        KualiDecimal transactionAmount = scrubbedEntry.getTransactionLedgerEntryAmount();

                        ParameterEvaluator offsetFiscalPeriods = SpringContext.getBean(ParameterService.class).getParameterEvaluator(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.OFFSET_FISCAL_PERIOD_CODES, scrubbedEntry.getUniversityFiscalPeriodCode());


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
                        // TODO: GLConstants.getSpaceSubAccountTypeCode();
                        String subAccountTypeCode = "  ";

                        A21SubAccount scrubbedEntryA21SubAccount = referenceLookup.get().getA21SubAccount(scrubbedEntry);
                        if (ObjectUtils.isNotNull(scrubbedEntryA21SubAccount)) {
                            subAccountTypeCode = scrubbedEntryA21SubAccount.getSubAccountTypeCode();
                        }

                        ParameterEvaluator costShareObjectTypeCodes = parameterService.getParameterEvaluator(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.COST_SHARE_OBJ_TYPE_CODES, scrubbedEntry.getFinancialObjectTypeCode());
                        ParameterEvaluator costShareEncBalanceTypeCodes = parameterService.getParameterEvaluator(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.COST_SHARE_ENC_BAL_TYP_CODES, scrubbedEntry.getFinancialBalanceTypeCode());
                        ParameterEvaluator costShareEncFiscalPeriodCodes = parameterService.getParameterEvaluator(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.COST_SHARE_ENC_FISCAL_PERIOD_CODES, scrubbedEntry.getUniversityFiscalPeriodCode());
                        ParameterEvaluator costShareEncDocTypeCodes = parameterService.getParameterEvaluator(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.COST_SHARE_ENC_DOC_TYPE_CODES, scrubbedEntry.getFinancialDocumentTypeCode().trim());
                        ParameterEvaluator costShareFiscalPeriodCodes = parameterService.getParameterEvaluator(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.COST_SHARE_FISCAL_PERIOD_CODES, scrubbedEntry.getUniversityFiscalPeriodCode());

                        Account scrubbedEntryAccount = referenceLookup.get().getAccount(scrubbedEntry);
                        if (scrubbedEntryAccount != null) {
                            SystemOptions scrubbedEntryOption = referenceLookup.get().getSystemOptions(scrubbedEntry);
                            if (costShareObjectTypeCodes.evaluationSucceeds() && scrubbedEntryOption.getActualFinancialBalanceTypeCd().equals(scrubbedEntry.getFinancialBalanceTypeCode()) && scrubbedEntryAccount.isForContractsAndGrants() && KFSConstants.SubAccountType.COST_SHARE.equals(subAccountTypeCode) && costShareFiscalPeriodCodes.evaluationSucceeds() && costShareEncDocTypeCodes.evaluationSucceeds()) {
                                if (scrubbedEntry.isDebit()) {
                                    scrubCostShareAmount = scrubCostShareAmount.subtract(transactionAmount);
                                }
                                else {
                                    scrubCostShareAmount = scrubCostShareAmount.add(transactionAmount);
                                }
                            }
                        }

                        if (transactionErrors.size() > 0) {
                            scrubberReportErrors.put(unscrubbedEntry, transactionErrors);
                        }

                        lastEntry = scrubbedEntry;
                    }
                    else {
                        // Error transaction
                        saveErrorTransaction = true;

                        scrubberReportErrors.put(unscrubbedEntry, transactionErrors);
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
                
                } catch (IOException ioe) {
                    //catch here again, it should be from postSingleEntryIntoLaborLedger
                    LOG.error("processGroup() stopped due to: " + ioe.getMessage() + " on line number : " + loadedCount, ioe);
                    throw new RuntimeException("processGroup() stopped due to: " + ioe.getMessage() + " on line number : " + loadedCount , ioe);
                    
                } 

            }
            INPUT_GLE_FILE_br.close();
            INPUT_GLE_FILE.close();
            OUTPUT_GLE_FILE_ps.close();
            OUTPUT_ERR_FILE_ps.close();
            OUTPUT_EXP_FILE_ps.close();


        }
        catch (IOException ioe) {
            LOG.error("processGroup() stopped due to: " + ioe.getMessage(), ioe);
            throw new RuntimeException("processGroup() stopped due to: " + ioe.getMessage(), ioe);
        }


    }


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
     * Get all the transaction descriptions from the param table
     */
    private void setDescriptions() {
        offsetDescription = kualiConfigurationService.getPropertyString(KFSKeyConstants.MSG_GENERATED_OFFSET);
        capitalizationDescription = kualiConfigurationService.getPropertyString(KFSKeyConstants.MSG_GENERATED_CAPITALIZATION);
        liabilityDescription = kualiConfigurationService.getPropertyString(KFSKeyConstants.MSG_GENERATED_LIABILITY);
        costShareDescription = kualiConfigurationService.getPropertyString(KFSKeyConstants.MSG_GENERATED_COST_SHARE);
        transferDescription = kualiConfigurationService.getPropertyString(KFSKeyConstants.MSG_GENERATED_TRANSFER);
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

        offsetString = "***" + nf.format(runCal.get(Calendar.MONTH) + 1) + nf.format(runCal.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * Generate the offset message with the flag at the end
     * 
     * @return Offset message
     */
    private String getOffsetMessage() {
        String msg = offsetDescription + SPACES;

        return msg.substring(0, 33) + offsetString;
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
            if (StringUtils.isNotBlank(errorMessage)) {
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

    private void putTransactionError(Transaction s, String errorMessage, String errorValue, int type) {
        List te = new ArrayList();
        te.add(new Message(errorMessage + "(" + errorValue + ")", type));
        scrubberReportErrors.put(s, te);
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
            LOG.debug("Cutoff time value found: " + cutoffTime);
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
                throw new IllegalArgumentException("Cutoff time should either be null, or in the format \"HH:mm:ss\", where HH, mm, ss are defined in the java.text.SimpleDateFormat class.");
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
        String cutoffTime = SpringContext.getBean(ParameterService.class).getParameterValue(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupParameters.SCRUBBER_CUTOFF_TIME);
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
        scrubbedEntry.setReferenceFinancialSystemDocumentTypeCode(unscrubbedEntry.getReferenceFinancialSystemDocumentTypeCode());
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
        String validOutputFilename = batchFileDirectoryName + File.separator + LaborConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION; 
        String errorOutputFilename = batchFileDirectoryName + File.separator + LaborConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        
        // Without this step, the job fails with Optimistic Lock Exceptions
        persistenceService.clearCache();

        DemergerReportData demergerReport = new DemergerReportData();
        
        OriginEntryStatistics eOes = laborOriginEntryService.getStatistics(errorOutputFilename);
        demergerReport.setErrorTransactionsRead(eOes.getRowCount());
        //TODO: Change to constants?
        String[] documentTypesBeProcessed = { "BT", "YEBT", "ST", "YEST"};

        // Read all the documents from the error group and move all non-generated
        // transactions for these documents from the valid group into the error group
        
        FileReader INPUT_GLE_FILE = null;
        FileReader INPUT_ERR_FILE = null;
        BufferedReader INPUT_GLE_FILE_br;
        BufferedReader INPUT_ERR_FILE_br;
        PrintStream OUTPUT_DEMERGER_GLE_FILE_ps;
        PrintStream OUTPUT_DEMERGER_ERR_FILE_ps;
        
        //TODO: change file name for FIS
        String demergerValidOutputFilename = batchFileDirectoryName + File.separator + LaborConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION; 
        String demergerErrorOutputFilename = batchFileDirectoryName + File.separator + LaborConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION; 
        
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

        Collection<LaborOriginEntry> validEntryCollection = new ArrayList();
        int validLine = 0;
        int errorLine = 0;
        
        
        boolean errorsLoading = false;
        LaborOriginEntry laborOriginEntry = null;
        INPUT_GLE_FILE_br = new BufferedReader(INPUT_GLE_FILE);
        INPUT_ERR_FILE_br = new BufferedReader(INPUT_ERR_FILE);
        
        try {
            String currentValidLine = INPUT_GLE_FILE_br.readLine();
            String currentErrorLine = INPUT_ERR_FILE_br.readLine();

            boolean meetFlag = false;
            
            while (currentValidLine != null || currentErrorLine != null) {
                
                //validLine is null means that errorLine is not null 
                if (StringUtils.isEmpty(currentValidLine)) {
                    createOutputEntry(currentErrorLine, OUTPUT_DEMERGER_ERR_FILE_ps);
                    currentErrorLine = INPUT_ERR_FILE_br.readLine();
                    errorLine++;
                    
                    continue;
                }
                
                //errorLine is null means that validLine is not null
                if (StringUtils.isEmpty(currentErrorLine)) {
                    createOutputEntry(currentValidLine, OUTPUT_DEMERGER_GLE_FILE_ps);
                    currentValidLine = INPUT_GLE_FILE_br.readLine();
                    validLine++;
                    
                    continue;
                }
                
                String documentTypeCode = currentErrorLine.substring(31, 35);
                if (documentTypeCode != null) {
                    //org.springframework.util.StringUtils.trimAllWhitespace(documentTypeCode);
                    documentTypeCode = documentTypeCode.trim();
                }
                
                if (ArrayUtils.contains(documentTypesBeProcessed, documentTypeCode)) {
                    String compareStringFromValidEntry = currentValidLine.substring(31, 46); 
                    String compareStringFromErrorEntry = currentErrorLine.substring(31, 46);
                    
                    if (compareStringFromValidEntry.compareTo(compareStringFromErrorEntry) < 0){
                        createOutputEntry(currentValidLine, OUTPUT_DEMERGER_GLE_FILE_ps);
                        currentValidLine = INPUT_GLE_FILE_br.readLine();
                        validLine++;
                        
                    } else if (compareStringFromValidEntry.compareTo(compareStringFromErrorEntry) > 0) {
                        createOutputEntry(currentErrorLine, OUTPUT_DEMERGER_ERR_FILE_ps);
                        currentErrorLine = INPUT_ERR_FILE_br.readLine();
                        errorLine++;
                    } else {
                        createOutputEntry(currentValidLine, OUTPUT_DEMERGER_ERR_FILE_ps);
                        currentValidLine = INPUT_GLE_FILE_br.readLine();
                        errorLine++;
                    }
                    
                    continue;
                }
                createOutputEntry(currentErrorLine, OUTPUT_DEMERGER_ERR_FILE_ps);
                currentErrorLine = INPUT_ERR_FILE_br.readLine();
                errorLine++;
            }
            
            INPUT_GLE_FILE_br.close();    
            INPUT_ERR_FILE_br.close();
            OUTPUT_DEMERGER_GLE_FILE_ps.close();
            OUTPUT_DEMERGER_ERR_FILE_ps.close();
            
        } catch (Exception e) {
            LOG.error("performDemerger() stopped due to: " + e.getMessage(), e);
            throw new RuntimeException("performDemerger() stopped due to: " + e.getMessage(), e);
        }
        
        
        demergerReport.setValidTransactionsSaved(validLine);

        //TODO: shawn - need to change to use file
        //shawn - commented out because if report use just number of error entries written, then use it for errorLine 
        //eOes = laborOriginEntryService.getStatistics(demergerErrorOutputFilename);
        demergerReport.setErrorTransactionWritten(errorLine);

        String reportsDirectory = ReportRegistry.getReportsDirectory();
        
        //shawn - set runDate here again, because demerger is calling outside from scrubber
        runDate = calculateRunDate(dateTimeService.getCurrentDate());
        runCal = Calendar.getInstance();
        runCal.setTime(runDate);
        
        laborReportService.generateScrubberDemergerStatisticsReports(demergerReport, reportsDirectory, runDate);

        
}


    private void createOutputEntry(LaborOriginEntry entry, PrintStream ps) throws IOException {
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

    public void setReferenceLookup(OriginEntryLookupService referenceLookup) {
        this.referenceLookup.set(referenceLookup);
        this.scrubberValidator.setReferenceLookup(referenceLookup);
    }
    
    
        
    private boolean checkEntry(LaborOriginEntry validEntry, LaborOriginEntry errorEntry, String documentTypeCode){
        String documentNumber = errorEntry.getDocumentNumber();
        String originationCode = errorEntry.getFinancialSystemOriginationCode();

        if (validEntry.getDocumentNumber().equals(documentNumber) && validEntry.getFinancialDocumentTypeCode().equals(documentTypeCode) && validEntry.getFinancialSystemOriginationCode().equals(originationCode)){
            return true;
        }
            return false;
        }

    private Collection <LaborOriginEntry> getBadBalanceEntries (String fileName){
        Collection returnCollection = new ArrayList();
        FileReader INPUT_GLE_FILE = null;
        BufferedReader INPUT_FILE_br;
        try {
            INPUT_GLE_FILE = new FileReader(fileName);
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        int lineNumber = 0;
        boolean errorsLoading = false;
        LaborOriginEntry laborOriginEntry = null;
        INPUT_FILE_br = new BufferedReader(INPUT_GLE_FILE);
        try {
            String currentLine = INPUT_FILE_br.readLine();

            while (currentLine != null) {
                lineNumber++;
                if (!StringUtils.isEmpty(currentLine)) {
                    try {
                        laborOriginEntry = new LaborOriginEntry();
                        laborOriginEntry.setFromTextFileForBatch(currentLine, lineNumber);
                    }
                    catch (LoadException e) {
                        errorsLoading = true;
                    }
                if (isBadBalanceEntry(laborOriginEntry)){
                    returnCollection.add(laborOriginEntry);
                }
                }
                currentLine = INPUT_FILE_br.readLine();
            }
            INPUT_FILE_br.close();    
        } catch (IOException e) {
            // FIXME: do whatever should be done here
            throw new RuntimeException(e);
        }
        
        List sortColumns = new ArrayList();

        sortColumns.add(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        sortColumns.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        sortColumns.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        sortColumns.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        sortColumns.add(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE);
        sortColumns.add(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE);
        sortColumns.add(KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE);
        sortColumns.add(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE);
        sortColumns.add(KFSPropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE);
        sortColumns.add(KFSPropertyConstants.DOCUMENT_NUMBER);
        
        Collections.sort((List) returnCollection, new BeanPropertyComparator(sortColumns, true));
            
        return returnCollection;
    }
    
    private boolean isBadBalanceEntry(LaborOriginEntry laborOriginEntry){
        if (laborOriginEntry.getFinancialBalanceTypeCode() == null || laborOriginEntry.getFinancialBalanceTypeCode().endsWith("  ")){
            return true;
        }
        return false; 
    }
    
    private Collection <LaborOriginEntry> getRemovedTransactions (String fileName){
        
        Collection returnCollection = new ArrayList();
        
        FileReader INPUT_GLE_FILE = null;
        BufferedReader INPUT_FILE_br;
        try {
            INPUT_GLE_FILE = new FileReader(fileName);
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        int lineNumber = 0;
        boolean errorsLoading = false;
        LaborOriginEntry laborOriginEntry = null;
        INPUT_FILE_br = new BufferedReader(INPUT_GLE_FILE);
        try {
            String currentLine = INPUT_FILE_br.readLine();

            while (currentLine != null) {
                lineNumber++;
                if (!StringUtils.isEmpty(currentLine)) {
                    try {
                        laborOriginEntry = new LaborOriginEntry();
                        laborOriginEntry.setFromTextFileForBatch(currentLine, lineNumber);
                    }
                    catch (LoadException e) {
                        errorsLoading = true;
                    }
                }
                currentLine = INPUT_FILE_br.readLine();
            }
            INPUT_FILE_br.close();    
        } catch (IOException e) {
            // FIXME: do whatever should be done here
            throw new RuntimeException(e);
        }
        
        List sortColumns = new ArrayList();
        sortColumns.add(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        sortColumns.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        sortColumns.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        sortColumns.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        sortColumns.add(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE);
        sortColumns.add(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE);
        sortColumns.add(KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE);
        sortColumns.add(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE);
        sortColumns.add(KFSPropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE);
        sortColumns.add(KFSPropertyConstants.DOCUMENT_NUMBER);
        sortColumns.add(KFSPropertyConstants.TRANSACTION_LEDGER_ENTRY_DESC);
        Collections.sort((List) returnCollection, new BeanPropertyComparator(sortColumns, true));
        return returnCollection;
    }
}

