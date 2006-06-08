/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.gl.service.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.core.document.DocumentType;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.DocumentTypeService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.PersistenceService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.OffsetDefinition;
import org.kuali.module.chart.service.ObjectCodeService;
import org.kuali.module.chart.service.OffsetDefinitionService;
import org.kuali.module.financial.bo.OffsetAccount;
import org.kuali.module.financial.service.FlexibleOffsetAccountService;
import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.OriginEntrySource;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.bo.UniversityDate;
import org.kuali.module.gl.dao.UniversityDateDao;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.gl.service.OriginEntryService;
import org.kuali.module.gl.service.ReportService;
import org.kuali.module.gl.service.ScrubberValidator;
import org.kuali.module.gl.service.impl.helper.ScrubberReportData;
import org.kuali.module.gl.service.impl.helper.UnitOfWorkInfo;
import org.kuali.module.gl.service.impl.scrubber.Message;
import org.kuali.module.gl.util.ObjectHelper;
import org.kuali.module.gl.util.StringHelper;
import org.kuali.module.gl.util.Summary;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.util.StringUtils;

/**
 * This class has the logic for the scrubber.  It is required because the scrubber
 * process needs instance variables.  Instance variables in a spring service are shared
 * between all code calling the service.  This will make sure each run of the scrubber
 * has it's own instance variables instead of being shared.
 *
 * @author Kuali
 */
public class ScrubberProcess {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ScrubberProcess.class);
    
    // Initialize mappings for capitalization. They only need to be initialized once.
    static private Map objectSubTypeCodesToObjectCodesForCapitalization = new TreeMap();
    static {
        objectSubTypeCodesToObjectCodesForCapitalization.put("AM", "8615");
        objectSubTypeCodesToObjectCodesForCapitalization.put("AF", "8616");
        objectSubTypeCodesToObjectCodesForCapitalization.put("BD", "8601");
        objectSubTypeCodesToObjectCodesForCapitalization.put("BF", "8605");
        objectSubTypeCodesToObjectCodesForCapitalization.put("BI", "8629");
        objectSubTypeCodesToObjectCodesForCapitalization.put("BR", "8601");
        objectSubTypeCodesToObjectCodesForCapitalization.put("BX", "8640");
        objectSubTypeCodesToObjectCodesForCapitalization.put("BY", "8641");
        objectSubTypeCodesToObjectCodesForCapitalization.put("CM", "8610");
        objectSubTypeCodesToObjectCodesForCapitalization.put("CF", "8611");
        objectSubTypeCodesToObjectCodesForCapitalization.put("C1", "8627");
        objectSubTypeCodesToObjectCodesForCapitalization.put("C2", "8628");
        objectSubTypeCodesToObjectCodesForCapitalization.put("C3", "9607");
        objectSubTypeCodesToObjectCodesForCapitalization.put("ES", "8630");
        objectSubTypeCodesToObjectCodesForCapitalization.put("IF", "8604");
        objectSubTypeCodesToObjectCodesForCapitalization.put("LA", "8603");
        objectSubTypeCodesToObjectCodesForCapitalization.put("LE", "8608");
        objectSubTypeCodesToObjectCodesForCapitalization.put("LF", "8614");
        objectSubTypeCodesToObjectCodesForCapitalization.put("LI", "8613");
        objectSubTypeCodesToObjectCodesForCapitalization.put("LR", "8665");
        objectSubTypeCodesToObjectCodesForCapitalization.put("UC", "8618");
        objectSubTypeCodesToObjectCodesForCapitalization.put("UF", "8619");
    }

    static private String[] validPlantIndebtednessSubFundGroupCodes = new String[] {"PFCMR", "PFRI"};

    static private String[] campusObjectSubTypeCodesForPlantFundAccountLookups = new String[] {
        "AM", "AF", "BD", "BF", "BI", "BR", "BX",
        "BY", "IF", "LA", "LE", "LF", "LI", "LR"
    };

    static private String[] organizationObjectSubTypeCodesForPlantFundAccountLookups = new String[] {
        "CL", "CM", "CF", "C1", "C2", "C3", "ES", 
        "UC", "UF"
    };

    static private String[] badDocumentTypeCodesForCapitalization = new String[] {"TF", "YETF", "AV", "AVAC", "AVAE", "AVRC"};

    static private String[] goodObjectSubTypeCodesForCapitalization = new String[] {
        "AM", "AF", "BD", "BF", "BI", "BR", "BX", 
        "BY", "CM", "CF", "C1", "C2", "C3", "ES", 
        "IF", "LA", "LE", "LF", "LI", "LR", "UC",
        "UF"
    };

    static private String[] invalidDocumentTypesForLiabilities = new String[] {"TF", "YETF", "AV", "AVAC", "AVAE", "AVRC"};

    static private String[] invalidFiscalPeriodCodesForOffsetGeneration = new String[] {"BB", "CB"};

    static private String[] badUniversityFiscalPeriodCodesForCapitalization = new String[] {"BB", "CB"};

    static private String[] invalidDocumentTypeCodesForCostShareEncumbrances = new String[] {"JV", "AA"};

    static private String[] validObjectTypeCodesForCostSharing = new String[] {"EE", "EX", "ES", "TE"};

    static private String[] validBalanceTypeCodesForCostSharing = new String[] {"AC", "EX", "IE", "PE"};

    static private String[] validBalanceTypeCodesForCostShareEncumbrances = new String[] {"EX", "IE", "PE"};

    private FlexibleOffsetAccountService flexibleOffsetAccountService;
    private DocumentTypeService documentTypeService;    
    private BeanFactory beanFactory;
    private OriginEntryService originEntryService;
    private OriginEntryGroupService originEntryGroupService;
    private DateTimeService dateTimeService;
    private OffsetDefinitionService offsetDefinitionService;
    private ObjectCodeService objectCodeService;
    private KualiConfigurationService kualiConfigurationService;
    private UniversityDateDao universityDateDao;
    private PersistenceService persistenceService;
    private ReportService reportService;
    private ScrubberValidator scrubberValidator;

    /* These are all different forms of the run date for this job */
    private Date runDate;
    private Calendar runCal;
    private UniversityDate universityRunDate;

    /* These are the output groups */
    private OriginEntryGroup validGroup;
    private OriginEntryGroup errorGroup;
    private OriginEntryGroup expiredGroup;
    private Map<Transaction,List<Message>> scrubberReportErrors;

    /* Unit Of Work info */
    private UnitOfWorkInfo unitOfWork;
    private KualiDecimal scrubCostShareAmount;

    /* Statistics for the scrubber report */
    private ScrubberReportData scrubberReport;

    /**
     * These parameters are all the dependencies.
     */
    public ScrubberProcess(FlexibleOffsetAccountService flexibleOffsetAccountService,DocumentTypeService documentTypeService,    
            BeanFactory beanFactory,OriginEntryService originEntryService,OriginEntryGroupService originEntryGroupService,
            DateTimeService dateTimeService,OffsetDefinitionService offsetDefinitionService,ObjectCodeService objectCodeService,
            KualiConfigurationService kualiConfigurationService,UniversityDateDao universityDateDao,
            PersistenceService persistenceService,ReportService reportService,ScrubberValidator scrubberValidator) {
      super();
      this.flexibleOffsetAccountService = flexibleOffsetAccountService;
      this.documentTypeService = documentTypeService;
      this.beanFactory = beanFactory;
      this.originEntryService = originEntryService;
      this.originEntryGroupService = originEntryGroupService;
      this.dateTimeService = dateTimeService;
      this.offsetDefinitionService = offsetDefinitionService;
      this.objectCodeService = objectCodeService;
      this.kualiConfigurationService = kualiConfigurationService;
      this.universityDateDao = universityDateDao;
      this.persistenceService = persistenceService;
      this.reportService = reportService;
      this.scrubberValidator = scrubberValidator;
    }

    /**
     * Scrub all entries that need it in origin entry.  Put valid scrubbed entries in
     * a scrubber valid group, put errors in a scrubber error group, and transactions
     * with an expired account in the scrubber expired account group.
     */
    public void scrubEntries() {
        LOG.debug("scrubEntries() started");

        scrubberReportErrors = new HashMap<Transaction,List<Message>>();

        // setup an object to hold the "default" date information
        runDate = new Date(dateTimeService.getCurrentDate().getTime());
        runCal = Calendar.getInstance();
        runCal.setTime(runDate);

        universityRunDate = universityDateDao.getByPrimaryKey(runDate);
        if (universityRunDate == null) {
            throw new IllegalStateException(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_UNIV_DATE_NOT_FOUND));
        }

        // Create the groups that will store the valid and error entries that come out of the scrubber
        validGroup = originEntryGroupService.createGroup(runDate, OriginEntrySource.SCRUBBER_VALID, true, true, false);
        errorGroup = originEntryGroupService.createGroup(runDate, OriginEntrySource.SCRUBBER_ERROR, false, true, false);
        expiredGroup = originEntryGroupService.createGroup(runDate, OriginEntrySource.SCRUBBER_EXPIRED, false, true, false);

        Collection groupsToScrub = originEntryGroupService.getGroupsToScrub(runDate);

        LOG.debug("scrubEntries() number of groups to scrub: " + groupsToScrub.size());

        scrubberReport = new ScrubberReportData();

        /* MAIN LOOP: -------------------------------------------------
         * Scrub all of the OriginEntryGroups waiting to be scrubbed as
         * of runDate.
         */
        for (Iterator iteratorOverGroups = groupsToScrub.iterator(); iteratorOverGroups.hasNext();) {
            OriginEntryGroup originEntryGroup = (OriginEntryGroup) iteratorOverGroups.next();
            LOG.debug("scrubEntries() Scrubbing group " + originEntryGroup.getId());
            scrubberReport.groupsRead();

            processGroup(originEntryGroup);

            // Mark the origin entry group as being processed ...
            originEntryGroup.setProcess(Boolean.FALSE);

            // ... and save the origin entry group with the new process flag.
            originEntryGroupService.save(originEntryGroup);
        }

        runScrubberReports();
        
        performDemerger(errorGroup, validGroup);

        // runDemergerReport();
    }

    private void runScrubberReports() {
        // write out report and errors
        List reportSummary = buildReportSummary();

        // TODO This should be implemented with a ReportQuery so the database summarizes some of the data
        // Also, this logic should be moved into the report service impl

        Map ledgerEntries = new HashMap();

        // This is commented out for now because it takes too long to run
//        for (Iterator iterator = originEntryService.getEntriesByGroup(validGroup); iterator.hasNext();) {
//            OriginEntry entry = (OriginEntry) iterator.next();
//
//            String key = entry.getFinancialBalanceTypeCode() + entry.getFinancialSystemOriginationCode() +
//                entry.getUniversityFiscalYear() + entry.getUniversityFiscalPeriodCode();
//
//            LedgerEntry ledgerEntry = null;
//            if ( ledgerEntries.containsKey(key) ) {
//                ledgerEntry = (LedgerEntry)ledgerEntries.get(key);
//            } else {
//                ledgerEntry = new LedgerEntry(entry.getFinancialBalanceTypeCode(),entry.getFinancialSystemOriginationCode(),
//                        entry.getUniversityFiscalYear(),entry.getUniversityFiscalPeriodCode());
//                ledgerEntries.put(key, ledgerEntry);
//            }
//
//            ++ledgerEntry.recordCount;
//            if (entry.isCredit()) {
//                ++ledgerEntry.creditCount;
//                ledgerEntry.creditAmount = ledgerEntry.creditAmount.add(entry.getTransactionLedgerEntryAmount());
//            } else if (entry.isDebit()) {
//                ++ledgerEntry.debitCount;
//                ledgerEntry.debitAmount = ledgerEntry.debitAmount.add(entry.getTransactionLedgerEntryAmount());
//            } else {
//                ++ledgerEntry.noDCCount;
//                ledgerEntry.noDCAmount = ledgerEntry.noDCAmount.add(entry.getTransactionLedgerEntryAmount());
//            }
//        }

        // These should probably just be sorted in place rather than copied to a new map
        Map sortedLedgerEntries = new TreeMap(ledgerEntries);

        reportService.generateScrubberReports(runDate, reportSummary, scrubberReportErrors, sortedLedgerEntries);
    }

    /**
     * The demerger process reads all of the documents in the error group, then moves all of the original entries for that document
     * from the valid group to the error group.  It does not move generated entries to the error group.  Those are deleted.
     * 
     * @param errorGroup
     * @param validGroup
     */
    private void performDemerger(OriginEntryGroup errorGroup, OriginEntryGroup validGroup) {
        LOG.debug("performDemerger() started");

        // TODO Write this
        //        originEntryService.removeScrubberDocumentEntries(validGroup, errorGroup, expiredGroup, documentNumber, documentTypeCode, originCode);
//
//        Iterator entryIterator = originEntryService.getEntriesByDocument(oeg, documentNumber, documentTypeCode, originCode);
//
//        while(entryIterator.hasNext()) {
//            OriginEntry entry = (OriginEntry) entryIterator.next();
//            originEntryService.createEntry(entry, errorGroup);
//        }
//
    }

    /**
     * 
     * @param scrubberReport
     * @return
     */
    private List buildReportSummary() {
        List reportSummary = new ArrayList();

        reportSummary.add(new Summary(1, "GROUPS READ", new Integer(scrubberReport.getNumberOfGroupsRead())));
        reportSummary.add(new Summary(2, "UNSCRUBBED RECORDS READ", new Integer(scrubberReport.getNumberOfUnscrubbedRecordsRead())));
        reportSummary.add(new Summary(3, "SCRUBBED RECORDS WRITTEN", new Integer(scrubberReport.getNumberOfScrubbedRecordsWritten())));
        reportSummary.add(new Summary(4, "ERROR RECORDS WRITTEN", new Integer(scrubberReport.getNumberOfErrorRecordsWritten())));
        reportSummary.add(new Summary(5, "OFFSET ENTRIES GENERATED", new Integer(scrubberReport.getNumberOfOffsetEntriesGenerated())));
        reportSummary.add(new Summary(6, "CAPITALIZATION ENTRIES GENERATED", new Integer(scrubberReport.getNumberOfCapitalizationEntriesGenerated())));
        reportSummary.add(new Summary(7, "LIABILITY ENTRIES GENERATED", new Integer(scrubberReport.getNumberOfLiabilityEntriesGenerated())));
        reportSummary.add(new Summary(8, "PLANT INDEBTEDNESS ENTRIES GENERATED", new Integer(scrubberReport.getNumberOfPlantIndebtednessEntriesGenerated())));
        reportSummary.add(new Summary(9, "COST SHARE ENTRIES GENERATED", new Integer(scrubberReport.getNumberOfCostShareEntriesGenerated())));
        reportSummary.add(new Summary(10, "COST SHARE ENC ENTRIES GENERATED", new Integer(scrubberReport.getNumberOfCostShareEncumbrancesGenerated())));
        reportSummary.add(new Summary(11, "TOTAL OUTPUT RECORDS WRITTEN", new Integer(scrubberReport.getTotalNumberOfRecordsWritten())));
        reportSummary.add(new Summary(12, "EXPIRED ACCOUNTS FOUND", new Integer(scrubberReport.getNumberOfExpiredAccountsFound())));

        return reportSummary;
    }

    /**
     * 
     * @param originEntryGroup
     * @param iteratorOverEntries
     * @param scrubberReport
     * @return
     */
    private void processGroup(OriginEntryGroup originEntryGroup) {

        OriginEntry lastEntry = null;
        scrubCostShareAmount = KualiDecimal.ZERO;
        unitOfWork = new UnitOfWorkInfo();

        Iterator entries = originEntryService.getEntriesByGroup(originEntryGroup);
        while ( entries.hasNext() ) {
            OriginEntry unscrubbedEntry = (OriginEntry)entries.next();
            scrubberReport.unscrubbedRecordsRead();

            // This is done so if the code modifies this row, then saves it, it will be an insert,
            // and it won't touch the original.  The Scrubber never modifies input rows/groups.
            unscrubbedEntry.setGroup(null);
            unscrubbedEntry.setVersionNumber(null);
            unscrubbedEntry.setEntryId(null);

            OriginEntry scrubbedEntry = new OriginEntry();
            scrubbedEntry.setFinancialDocumentNumber(unscrubbedEntry.getFinancialDocumentNumber());
            scrubbedEntry.setOrganizationDocumentNumber(unscrubbedEntry.getOrganizationDocumentNumber());
            scrubbedEntry.setOrganizationReferenceId(unscrubbedEntry.getOrganizationReferenceId());
            scrubbedEntry.setReferenceFinancialDocumentNumber(unscrubbedEntry.getReferenceFinancialDocumentNumber());

            Integer transactionNumber = unscrubbedEntry.getTransactionLedgerEntrySequenceNumber();
            scrubbedEntry.setTransactionLedgerEntrySequenceNumber(null == transactionNumber ? new Integer(0) : transactionNumber);
            scrubbedEntry.setTransactionLedgerEntryDescription(unscrubbedEntry.getTransactionLedgerEntryDescription());
            scrubbedEntry.setTransactionLedgerEntryAmount(unscrubbedEntry.getTransactionLedgerEntryAmount());
            scrubbedEntry.setTransactionDebitCreditCode(unscrubbedEntry.getTransactionDebitCreditCode());

            List<Message> errors = scrubberValidator.validateTransaction(unscrubbedEntry, scrubbedEntry, universityRunDate);

            // Expired account?
            if ( (unscrubbedEntry.getAccount() != null) && (unscrubbedEntry.getAccount().isAccountClosedIndicator()) ) {
                // Make a copy of it so OJB doesn't just update the row in the original
                // group.  It needs to make a new one in the error group
                OriginEntry errorEntry = new OriginEntry(unscrubbedEntry);

                errorEntry.setTransactionScrubberOffsetGenerationIndicator(false);
                createOutputEntry(errorEntry, expiredGroup);
                scrubberReport.expiredAccountFound();
            }

            if ( errors.size() == 0 ) {
                // See if unit of work has changed
                if ( ! unitOfWork.isSameUnitOfWork(scrubbedEntry) ) {
                    // Generate offset for last unit of work
                    generateOffset(lastEntry);

                    unitOfWork = new UnitOfWorkInfo(scrubbedEntry);
                }

                // Valid transaction
                processEntry(scrubbedEntry);

                if ( ! ObjectHelper.isOneOf(scrubbedEntry.getFinancialDocumentTypeCode(), new String[]{"JV","ACLO"}) ) {
                    processCapitalization(scrubbedEntry);
                    processLiabilities(scrubbedEntry);
                    processPlantIndebtedness(scrubbedEntry);
                }

                if ( ! scrubCostShareAmount.isZero() ) {
                    // TODO This returns a list of errors
                    List<Message> e = generateCostShareEntries(scrubbedEntry);
                    scrubCostShareAmount = KualiDecimal.ZERO;
                }
                scrubbedEntry.setTransactionScrubberOffsetGenerationIndicator(false);
                createOutputEntry(scrubbedEntry,validGroup);
                scrubberReport.scrubbedRecordWritten();

                lastEntry = scrubbedEntry;
            } else {
                // Error transaction

                // Make a copy of it so OJB doesn't just update the row in the original
                // group.  It needs to make a new one in the error group
                OriginEntry errorEntry = new OriginEntry(unscrubbedEntry);
                errorEntry.setTransactionScrubberOffsetGenerationIndicator(false);
                scrubberReportErrors.put(errorEntry,errors);
                createOutputEntry(errorEntry, errorGroup);
                scrubberReport.errorRecordWritten();
            }
        }

        // Generate last offset (if necessary)
        generateOffset(lastEntry);
    }

    /**
     * @param originEntry
     * @param workingEntry
     */
    private void processEntry(OriginEntry workingEntry) {

//              3342  022680     IF (FIN-OBJ-TYP-CD OF ALT-GLEN-RECORD = 'EE'
//              3343  022690         OR FIN-OBJ-TYP-CD OF ALT-GLEN-RECORD = 'EX'
//              3344  022700         OR FIN-OBJ-TYP-CD OF ALT-GLEN-RECORD = 'ES'
//              3345  022710         OR FIN-OBJ-TYP-CD OF ALT-GLEN-RECORD = 'TE')
//              3346  022720         AND (FIN-BALANCE-TYP-CD OF ALT-GLEN-RECORD = 'AC'
//              3347  022730           OR FIN-BALANCE-TYP-CD OF ALT-GLEN-RECORD = 'EX'
//              3348  022740           OR FIN-BALANCE-TYP-CD OF ALT-GLEN-RECORD = 'IE'
//              3349  022750           OR FIN-BALANCE-TYP-CD OF ALT-GLEN-RECORD = 'PE')
//              3350  022760         AND WS-FUND-GRP-CD = 'CG'

        if(ObjectHelper.isOneOf(workingEntry.getFinancialObjectTypeCode(), validObjectTypeCodesForCostSharing)
           && ObjectHelper.isOneOf(workingEntry.getFinancialBalanceTypeCode(), validBalanceTypeCodesForCostSharing)
           && workingEntry.getAccount().isInCg()) {

//              3351  022770         MOVE FIN-COA-CD OF ALT-GLEN-RECORD
//              3352  022780           TO CASA21-FIN-COA-CD
//              3353  022790         MOVE ACCOUNT-NBR OF ALT-GLEN-RECORD
//              3354  022800           TO CASA21-ACCOUNT-NBR
//              3355  022810         MOVE SUB-ACCT-NBR OF ALT-GLEN-RECORD
//              3356  022820           TO CASA21-SUB-ACCT-NBR
//              3357  022830         EXEC SQL
//              3358  022840           SELECT SUB_ACCT_TYP_CD,
//              3359  022850                  CST_SHR_COA_CD,
//              3360  022860                  CST_SHRSRCACCT_NBR,
//              3361  022870                  CST_SRCSUBACCT_NBR
//              3362  022880           INTO :CASA21-SUB-ACCT-TYP-CD :CASA21-SATC-I,
//              3363  022890                :CASA21-CST-SHR-COA-CD :CASA21-CSCC-I,
//              3364  022900                :CASA21-CST-SHRSRCACCT-NBR :CASA21-CSN-I,
//              3365  022910                :CASA21-CST-SRCSUBACCT-NBR :CASA21-CSN2-I
//              3366  022920           FROM CA_A21_SUB_ACCT_T
//              3367  022930           WHERE FIN_COA_CD = RTRIM(:CASA21-FIN-COA-CD)
//              3368  022940             AND ACCOUNT_NBR = RTRIM(:CASA21-ACCOUNT-NBR)
//              3369  022950             AND SUB_ACCT_NBR = RTRIM(:CASA21-SUB-ACCT-NBR)
//              3370  022960         END-EXEC
//              3371                  IF CASA21-SATC-I < ZERO
//              3372                    MOVE SPACES TO CASA21-SUB-ACCT-TYP-CD
//              3373                  END-IF
//              3374                  IF CASA21-CSCC-I < ZERO
//              3375                    MOVE SPACES TO CASA21-CST-SHR-COA-CD
//              3376                  END-IF
//              3377                  IF CASA21-CSN-I < ZERO
//              3378                    MOVE SPACES TO CASA21-CST-SHRSRCACCT-NBR
//              3379                  END-IF
//              3380                  IF CASA21-CSN2-I < ZERO
//              3381                    MOVE SPACES TO CASA21-CST-SRCSUBACCT-NBR
//              3382                  END-IF
//              3383  022970         EVALUATE SQLCODE
//              3384  022980           WHEN 0
//              3385  022990             MOVE CASA21-SUB-ACCT-TYP-CD TO WS-SUB-ACCT-TYP-CD
//              3386  023000             MOVE ACCOUNT-NBR OF ALT-GLEN-RECORD
//              3387  023010               TO COSTSHARE-ACCOUNT
//              3388  023020           WHEN +100
//              3389  023030           WHEN +1403
//              3390  023040               MOVE SPACES TO WS-SUB-ACCT-TYP-CD
//              3391  023050           WHEN OTHER
//              3392  023060               DISPLAY 'ERROR ACCESSING A21 SUB ACCOUNT TABLE'
//              3393  023070                       ' SQL CODE IS ' SQLCODE
//              3394  023080               MOVE 'Y' TO WS-FATAL-ERROR-FLAG
//              3395  023090               GO TO 2000-ENTRY-EXIT
//              3396  023100         END-EVALUATE
//              3397  023110     ELSE
//              3398  023120         MOVE SPACES TO WS-SUB-ACCT-TYP-CD
//              3399  023130     END-IF.

        }

//              3400  023200     IF CABTYP-FIN-OFFST-GNRTN-CD = 'Y'
//              3401  023210        AND (FDOC-TYP-CD OF GLEN-RECORD NOT = 'ACLO'
//              3402  023220             AND UNIV-FISCAL-PRD-CD OF GLEN-RECORD NOT
//              3403  023230             = 'BB' AND UNIV-FISCAL-PRD-CD OF GLEN-RECORD
//              3404  023240             NOT = 'CB')



        KualiDecimal transactionAmount = workingEntry.getTransactionLedgerEntryAmount();

        if (workingEntry.getBalanceType().isFinancialOffsetGenerationIndicator()
                && !"ACLO".equals(workingEntry.getFinancialDocumentTypeCode())
                && !ObjectHelper.isOneOf(workingEntry.getUniversityFiscalPeriodCode(),invalidFiscalPeriodCodesForOffsetGeneration)) {

//          3405  023250        IF TRN-DEBIT-CRDT-CD OF ALT-GLEN-RECORD = DEBIT

            if(workingEntry.isDebit()) {

//          3406  023260           ADD      TRN-LDGR-ENTR-AMT OF GLEN-RECORD
//          3407  023270            TO      SCRB-OFFSET-AMOUNT

                unitOfWork.offsetAmount = unitOfWork.offsetAmount.add(transactionAmount);

//          3408  023280        ELSE

            } else {

//          3409  023290           SUBTRACT TRN-LDGR-ENTR-AMT OF GLEN-RECORD
//          3410  023300               FROM SCRB-OFFSET-AMOUNT

                unitOfWork.offsetAmount = unitOfWork.offsetAmount.subtract(transactionAmount);

//          3411  023310        END-IF

            }

//          3412  023320     END-IF

        }

        // These are never used
//      3413  023360     EVALUATE TRN-DEBIT-CRDT-CD OF ALT-GLEN-RECORD
//      3414  023370        WHEN 'D'
//      3415  023380           ADD TRN-LDGR-ENTR-AMT   OF GLEN-RECORD
//      3416  023390            TO SCRB-DEBIT-ACCUM-AMOUNT
//      3417  023400        WHEN 'C'
//      3418  023410           ADD TRN-LDGR-ENTR-AMT   OF GLEN-RECORD
//      3419  023420            TO SCRB-CREDIT-ACCUM-AMOUNT
//      3420  023430     END-EVALUATE
        // End

//      3427  023590     IF (FIN-OBJ-TYP-CD OF ALT-GLEN-RECORD = 'EE'
//      3428  023600         OR FIN-OBJ-TYP-CD OF ALT-GLEN-RECORD = 'EX'
//      3429  023610         OR FIN-OBJ-TYP-CD OF ALT-GLEN-RECORD = 'ES'
//      3430  023620         OR FIN-OBJ-TYP-CD OF ALT-GLEN-RECORD = 'TE')
//      3431  023630         AND (FIN-BALANCE-TYP-CD OF ALT-GLEN-RECORD = 'EX'
//      3432  023640           OR FIN-BALANCE-TYP-CD OF ALT-GLEN-RECORD = 'IE'
//      3433  023650           OR FIN-BALANCE-TYP-CD OF ALT-GLEN-RECORD = 'PE')
//      3434  023660         AND WS-FUND-GRP-CD     = 'CG'
//      3435  023670         AND WS-SUB-ACCT-TYP-CD     = 'CS'
//      3436  023680         AND UNIV-FISCAL-PRD-CD OF GLEN-RECORD NOT = 'BB'
//      3437  023690         AND UNIV-FISCAL-PRD-CD OF GLEN-RECORD NOT = 'CB'
//      3438  023700         AND FDOC-TYP-CD OF GLEN-RECORD NOT = 'JV  '
//      3439  023710         AND FDOC-TYP-CD OF GLEN-RECORD NOT = 'AA  '

        
        // The sub account type code will only exist if there is a valid sub account
        String subAccountTypeCode = "  ";
        if ( workingEntry.getA21SubAccount() != null ) {
            subAccountTypeCode = workingEntry.getA21SubAccount().getSubAccountTypeCode();
        }

        if(ObjectHelper.isOneOf(workingEntry.getFinancialObjectTypeCode(), validObjectTypeCodesForCostSharing)
           && ObjectHelper.isOneOf(workingEntry.getFinancialBalanceTypeCode(), validBalanceTypeCodesForCostShareEncumbrances)
           && workingEntry.getAccount().isInCg()
           && "CS".equals(subAccountTypeCode)
           && !ObjectHelper.isOneOf(workingEntry.getUniversityFiscalPeriodCode(),invalidFiscalPeriodCodesForOffsetGeneration)
           && (StringHelper.isEmpty(workingEntry.getFinancialDocumentTypeCode())
             || !ObjectHelper.isOneOf(workingEntry.getFinancialDocumentTypeCode().trim(),invalidDocumentTypeCodesForCostShareEncumbrances))) {

//      3440  023720         PERFORM 3200-COST-SHARE-ENC THRU 3200-CSE-EXIT

            // TODO Do something with the errors
            List errors = generateCostShareEncumbranceEntries(workingEntry);

//      3441  023730     END-IF

        }

//      3442  023740     IF (FIN-OBJ-TYP-CD OF ALT-GLEN-RECORD = 'EE'
//      3443  023750         OR FIN-OBJ-TYP-CD OF ALT-GLEN-RECORD = 'EX'
//      3444  023760         OR FIN-OBJ-TYP-CD OF ALT-GLEN-RECORD = 'ES'
//      3445  023770         OR FIN-OBJ-TYP-CD OF ALT-GLEN-RECORD = 'TE')
//      3446  023780         AND FIN-BALANCE-TYP-CD OF ALT-GLEN-RECORD = 'AC'
//      3447  023790         AND WS-FUND-GRP-CD     = 'CG'
//      3448  023800         AND WS-SUB-ACCT-TYP-CD     = 'CS'
//      3449  023810         AND UNIV-FISCAL-PRD-CD OF GLEN-RECORD NOT = 'BB'
//      3450  023820         AND UNIV-FISCAL-PRD-CD OF GLEN-RECORD NOT = 'CB'
//      3451  023830         AND FDOC-TYP-CD OF GLEN-RECORD NOT = 'JV  '
//      3452  023840         AND FDOC-TYP-CD OF GLEN-RECORD NOT = 'AA  '

        // The sub account type code will only exist if there is a valid sub account
        subAccountTypeCode = "  ";
        if ( workingEntry.getA21SubAccount() != null ) {
            subAccountTypeCode = workingEntry.getA21SubAccount().getSubAccountTypeCode();
        }

        if (ObjectHelper.isOneOf(workingEntry.getFinancialObjectTypeCode(), validObjectTypeCodesForCostSharing)
           && "AC".equals(workingEntry.getFinancialBalanceTypeCode())
           && workingEntry.getAccount().isInCg()
           && "CS".equals(subAccountTypeCode)
           && !ObjectHelper.isOneOf(workingEntry.getUniversityFiscalPeriodCode(),invalidFiscalPeriodCodesForOffsetGeneration)
           && !ObjectHelper.isOneOf(workingEntry.getFinancialDocumentTypeCode().trim(),invalidDocumentTypeCodesForCostShareEncumbrances)) {

//      3453  023850        IF TRN-DEBIT-CRDT-CD OF ALT-GLEN-RECORD = DEBIT

            if(workingEntry.isDebit()) {

//      3454  023860        SUBTRACT    TRN-LDGR-ENTR-AMT OF GLEN-RECORD
//      3455  023870            FROM    SCRB-COST-SHARE-AMOUNT

                scrubCostShareAmount = scrubCostShareAmount.subtract(transactionAmount);

//      3456  023880        ELSE

            } else {

//      3457  023890           ADD      TRN-LDGR-ENTR-AMT OF GLEN-RECORD
//      3458  023900               TO   SCRB-COST-SHARE-AMOUNT

                scrubCostShareAmount = scrubCostShareAmount.add(transactionAmount);

//      3459  023910        END-IF

            }

//      3460  023930      END-IF.            

        }
    }

    /**
     * 
     * @param originEntry
     * @param workingEntryInfo
     */
    private List generateCostShareEntries(OriginEntry scrubbedEntry) {

        List errors = new ArrayList();

        OriginEntry costShareEntry = new OriginEntry(scrubbedEntry);

        // objectCodeKey and offsetKey are used in error messages in the event that either an object
        // code or an offset definition respectively cannot be found in the database. They're defined
        // at method level scope because they're used in multiple places.

        StringBuffer objectCodeKey = new StringBuffer();
        objectCodeKey.append("Fiscal Year: ").append(costShareEntry.getUniversityFiscalYear());
        objectCodeKey.append(", ").append("Chart Code: ").append(scrubbedEntry.getChartOfAccountsCode());
        objectCodeKey.append(", ").append("Object Code: ").append(scrubbedEntry.getFinancialObjectCode());

        StringBuffer offsetKey = new StringBuffer();
        offsetKey.append("Fiscal Year: ").append(scrubbedEntry.getUniversityFiscalYear());
        offsetKey.append(", ").append("Chart Code: ").append(scrubbedEntry.getChartOfAccountsCode());
        offsetKey.append(", ").append("Document Type: ").append("TF");
        offsetKey.append(", ").append("Balance Type: ").append(scrubbedEntry.getFinancialBalanceTypeCode());

//        4094  031200     SET FROM-CSHR TO TRUE.
//        4095  031210
//        4096  031220     MOVE ALT-GLEN-RECORD TO COST-SHARE-RECORD.

        // NOTE (laran) This line makes a copy of the originEntry so that lookupCostShareObjectCode
        // can lookup the object code level from the object code of the originEntry. The object
        // code of the costShareEntry is changed before that method is called so a reference to
        // the original value is necessary.

//        4097  031230
//        4098  031240     MOVE '9915' TO FIN-OBJECT-CD OF ALT-GLEN-RECORD.

        costShareEntry.setFinancialObjectCode("9915");

//        4099  031250     MOVE FIN-SUB-OBJ-CD-DASHES
//        4100  031260       TO FIN-SUB-OBJ-CD OF ALT-GLEN-RECORD

        costShareEntry.setFinancialSubObjectCode(Constants.DASHES_SUB_OBJECT_CODE);

//        4101  031340     MOVE 'TE' TO FIN-OBJ-TYP-CD OF ALT-GLEN-RECORD.

        costShareEntry.setFinancialObjectTypeCode("TE");

//        4102  031350     MOVE +0              TO TRN-ENTR-SEQ-NBR
//        4103  031360       OF ALT-GLEN-RECORD.

        costShareEntry.setTransactionLedgerEntrySequenceNumber(new Integer(0));

//        4104  031370     MOVE COSTSHARE-DESCRIPTION
//        4105  031380       TO TRN-LDGR-ENTR-DESC OF ALT-GLEN-RECORD.

        StringBuffer description = new StringBuffer();
        description.append(kualiConfigurationService.getPropertyString(KeyConstants.MSG_GENERATED_COST_SHARE));
        description.append(" ").append(scrubbedEntry.getAccountNumber());

        // TODO Remove after testing
        description.append("***0531");
        costShareEntry.setTransactionLedgerEntryDescription(description.toString());


//        4114  031500     IF SCRB-COST-SHARE-AMOUNT > ZEROES
//        4115  031510        MOVE DEBIT
//        4116  031520          TO TRN-DEBIT-CRDT-CD OF ALT-GLEN-RECORD
//        4117  031530     ELSE
//        4118  031540        MOVE CREDIT
//        4119  031550          TO TRN-DEBIT-CRDT-CD OF ALT-GLEN-RECORD
//        4120  031560          COMPUTE TRN-LDGR-ENTR-AMT OF ALT-GLEN-RECORD
//        4121  031570          = SCRB-COST-SHARE-AMOUNT * -1.

        costShareEntry.setTransactionLedgerEntryAmount(scrubCostShareAmount);
        if (scrubCostShareAmount.isPositive()) {
            costShareEntry.setTransactionDebitCreditCode(Constants.GL_DEBIT_CODE);
        } else {
            costShareEntry.setTransactionDebitCreditCode(Constants.GL_CREDIT_CODE);
            costShareEntry.setTransactionLedgerEntryAmount(scrubCostShareAmount.negated());
        }

//        4122  031590     MOVE SCRB-TODAYS-DATE
//        4123  031600       TO TRANSACTION-DT OF ALT-GLEN-RECORD.

        costShareEntry.setTransactionDate(runDate);

//        4124  031620     MOVE SPACES TO ORG-DOC-NBR       OF ALT-GLEN-RECORD.

        costShareEntry.setOrganizationDocumentNumber(null);

//        4125  031630     MOVE PROJECT-CD-DASHES
//        4126  031640                 TO PROJECT-CD        OF ALT-GLEN-RECORD.

        costShareEntry.setProjectCode(Constants.DASHES_PROJECT_CODE);

//        4127  031650     MOVE SPACES TO ORG-REFERENCE-ID  OF ALT-GLEN-RECORD
//        4128  031660                    FDOC-REF-TYP-CD   OF ALT-GLEN-RECORD
//        4129  031670                    FS-REF-ORIGIN-CD  OF ALT-GLEN-RECORD
//        4130  031680                    FDOC-REF-NBR      OF ALT-GLEN-RECORD
//        4131  031690                    FDOC-REVERSAL-DT  OF ALT-GLEN-RECORD
//        4132  031700                    TRN-ENCUM-UPDT-CD OF ALT-GLEN-RECORD.

        costShareEntry.setOrganizationReferenceId(null);
        costShareEntry.setReferenceFinancialDocumentTypeCode(null);
        costShareEntry.setReferenceFinancialSystemOriginationCode(null);
        costShareEntry.setReferenceFinancialDocumentNumber(null);
        costShareEntry.setFinancialDocumentReversalDate(null);
        costShareEntry.setTransactionEncumbranceUpdateCode(null);

//        4133  031720     PERFORM 8210-WRITE-ALT-GLEN
//        4134  031730        THRU 8210-WRITE-ALT-GLEN-EXIT.

        costShareEntry.setTransactionScrubberOffsetGenerationIndicator(true);
        createOutputEntry(costShareEntry, validGroup);
        scrubberReport.costShareEntryGenerated();

        OriginEntry costShareOffsetEntry = new OriginEntry(costShareEntry);

//        4135  031750     MOVE OFFSET-DESCRIPTION
//        4136  031760       TO TRN-LDGR-ENTR-DESC OF ALT-GLEN-RECORD.

        // costShareOffsetEntry.setTransactionLedgerEntryDescription(kualiConfigurationService.getPropertyString(KeyConstants.MSG_GENERATED_OFFSET));
        //                                                         1234567890123456789012345678901234567890
        costShareOffsetEntry.setTransactionLedgerEntryDescription("GENERATED OFFSET                 ***0531");
        // NOTE (laran) these three move steps were already done above.

//        4143  031860     MOVE UNIV-FISCAL-YR     OF ALT-GLEN-RECORD
//        4144  031870       TO GLOFSD-UNIV-FISCAL-YR.
//        4145  031880     MOVE FIN-COA-CD         OF ALT-GLEN-RECORD
//        4146  031890       TO GLOFSD-FIN-COA-CD.
//        4147  031900     MOVE 'TF' TO GLOFSD-FDOC-TYP-CD.
//        4148  031910     MOVE FIN-BALANCE-TYP-CD OF ALT-GLEN-RECORD
//        4149  031920       TO GLOFSD-FIN-BALANCE-TYP-CD.
//        4150  031930     EXEC SQL
//        4151  031940          SELECT FIN_OBJECT_CD,
//        4152  031950                 FIN_SUB_OBJ_CD
//        4153  031960          INTO   :GLOFSD-FIN-OBJECT-CD :GLOFSD-FOC-I,
//        4154  031970                 :GLOFSD-FIN-SUB-OBJ-CD :GLOFSD-FSOC-I
//        4155  031980          FROM   GL_OFFSET_DEFN_T
//        4156  031990          WHERE UNIV_FISCAL_YR = RTRIM(:GLOFSD-UNIV-FISCAL-YR)
//        4157  032000            AND FIN_COA_CD = RTRIM(:GLOFSD-FIN-COA-CD)
//        4158  032010            AND FDOC_TYP_CD = RTRIM(:GLOFSD-FDOC-TYP-CD)
//        4159  032020            AND FIN_BALANCE_TYP_CD
//        4160  032030                = RTRIM(:GLOFSD-FIN-BALANCE-TYP-CD)
//        4161  032040     END-EXEC
//        4162             IF GLOFSD-FOC-I < ZERO
//        4163                MOVE SPACES TO GLOFSD-FIN-OBJECT-CD
//        4164             END-IF
//        4165             IF GLOFSD-FSOC-I < ZERO
//        4166                MOVE SPACES TO GLOFSD-FIN-SUB-OBJ-CD
//        4167             END-IF

        OffsetDefinition offsetDefinition = offsetDefinitionService.getByPrimaryId(scrubbedEntry.getUniversityFiscalYear(), 
                scrubbedEntry.getChartOfAccountsCode(), "TF", scrubbedEntry.getFinancialBalanceTypeCode());

//        4168  032050     EVALUATE SQLCODE
//        4169  032060          WHEN 0

        if ( offsetDefinition != null ) {

//        4170  032070              MOVE GLOFSD-FIN-OBJECT-CD TO
//        4171  032080                   FIN-OBJECT-CD OF ALT-GLEN-RECORD

            costShareOffsetEntry.setFinancialObjectCode(offsetDefinition.getFinancialObjectCode());
            costShareOffsetEntry.setFinancialObject(offsetDefinition.getFinancialObject());

//        4172  032090              IF GLOFSD-FIN-SUB-OBJ-CD = SPACES

            if ( ! StringUtils.hasText(offsetDefinition.getFinancialSubObjectCode()) ) {

//        4173  032100                   MOVE FIN-SUB-OBJ-CD-DASHES
//        4174  032110                       TO FIN-SUB-OBJ-CD OF ALT-GLEN-RECORD

                costShareOffsetEntry.setFinancialSubObjectCode(Constants.DASHES_SUB_OBJECT_CODE);

//        4175  032120              ELSE

            } else {

//        4176  032130                   MOVE GLOFSD-FIN-SUB-OBJ-CD TO
//        4177  032140                          FIN-SUB-OBJ-CD OF ALT-GLEN-RECORD

                costShareOffsetEntry.setFinancialSubObjectCode(offsetDefinition.getFinancialSubObjectCode());

//        4178  032150              END-IF

            }
        } else {

//        4179  032160          WHEN +100
//        4180  032170          WHEN +1403
//        4181  032180              MOVE ALT-GLEN-RECORD (1:51) TO RP-TABLE-KEY
//        4182                      MOVE SPACES TO RP-DATA-ERROR
//        4183  032190              MOVE 'OFFSET DEFINITION NOT FOUND' TO RP-MSG-ERROR
//        4184  032200              PERFORM WRITE-ERROR-LINE THRU WRITE-ERROR-LINE-EXIT
//        4185  032210              MOVE SPACES TO DCLGL-OFFSET-DEFN-T

            addTransactionError(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_OFFSET_DEFINITION_NOT_FOUND), 
                    offsetKey.toString(), errors);

//        4186  032220          WHEN OTHER
//        4187  032230              DISPLAY ' ERROR ACCESSING OFSD TABLE '
//        4188  032240                      'SQL CODE IS ' SQLCODE
//        4189  032250              MOVE 'Y' TO WS-FATAL-ERROR-FLAG
//        4190  032260              GO TO 2000-ENTRY-EXIT
//        4191  032270     END-EVALUATE

        }

//        4192  032320     MOVE UNIV-FISCAL-YR OF ALT-GLEN-RECORD
//        4193  032330       TO CAOBJT-UNIV-FISCAL-YR
//        4194  032340     MOVE FIN-COA-CD     OF ALT-GLEN-RECORD
//        4195  032350       TO CAOBJT-FIN-COA-CD
//        4196  032360     MOVE FIN-OBJECT-CD  OF ALT-GLEN-RECORD
//        4197  032370       TO CAOBJT-FIN-OBJECT-CD
//        4198  032380     EXEC SQL
//        4199  032390       SELECT    FIN_OBJ_TYP_CD,
//        4200  032400                 FIN_OBJ_SUB_TYP_CD,
//        4201  032410                 FIN_OBJ_ACTIVE_CD,
//        4202  032420                 FOBJ_MNXFR_ELIM_CD
//        4203  032430       INTO      :CAOBJT-FIN-OBJ-TYP-CD :CAOBJT-FOTC-I,
//        4204  032440                 :CAOBJT-FIN-OBJ-SUB-TYP-CD :CAOBJT-FOSTC-I,
//        4205  032450                 :CAOBJT-FIN-OBJ-ACTIVE-CD :CAOBJT-FOAC-I,
//        4206  032460                 :CAOBJT-FOBJ-MNXFR-ELIM-CD :CAOBJT-FMEC-I
//        4207  032470       FROM      CA_OBJECT_CODE_T
//        4208  032480       WHERE     UNIV_FISCAL_YR= RTRIM(:CAOBJT-UNIV-FISCAL-YR)
//        4209  032490         AND     FIN_COA_CD=     RTRIM(:CAOBJT-FIN-COA-CD)
//        4210  032500         AND     FIN_OBJECT_CD=  RTRIM(:CAOBJT-FIN-OBJECT-CD)
//        4211  032510     END-EXEC

//        4212              IF CAOBJT-FOTC-I < ZERO
//        4213                 MOVE SPACES TO CAOBJT-FIN-OBJ-TYP-CD
//        4214              END-IF
//        4215              IF CAOBJT-FOSTC-I < ZERO
//        4216                 MOVE SPACES TO CAOBJT-FIN-OBJ-SUB-TYP-CD
//        4217              END-IF
//        4218              IF CAOBJT-FOAC-I < ZERO
//        4219                 MOVE SPACE TO CAOBJT-FIN-OBJ-ACTIVE-CD
//        4220              END-IF
//        4221              IF CAOBJT-FMEC-I < ZERO
//        4222                 MOVE SPACES TO CAOBJT-FOBJ-MNXFR-ELIM-CD
//        4223              END-IF
//        4224  032520        EVALUATE SQLCODE
//        4225  032530           WHEN 0

        if( offsetDefinition != null ) {

//        4226  032540            MOVE CAOBJT-FIN-OBJ-TYP-CD TO FIN-OBJ-TYP-CD
//        4227  032550              OF ALT-GLEN-RECORD

            costShareOffsetEntry.setFinancialObjectTypeCode(offsetDefinition.getFinancialObject().getFinancialObjectTypeCode());

        } else {

//        4228  032560           WHEN +100
//        4229  032570           WHEN +1403
//        4230  032580              MOVE GLEN-RECORD (1:51) TO RP-TABLE-KEY
//        4231  032590              MOVE CAOBJT-FIN-OBJECT-CD TO RP-DATA-ERROR
//        4232  032600                   FIN-OBJECT-CD OF ALT-GLEN-RECORD
//        4233  032610              MOVE 'NO OBJECT FOR OBJECT ON OFSD' TO RP-MSG-ERROR

            addTransactionError(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_OFFSET_DEFINITION_OBJECT_CODE_NOT_FOUND), 
                    objectCodeKey.toString(), errors);

//        4234  032620              PERFORM WRITE-ERROR-LINE THRU WRITE-ERROR-LINE-EXIT
//        4235  032630              MOVE SPACES TO DCLCA-OBJECT-CODE-T
//        4236  032640           WHEN OTHER
//        4237  032650               DISPLAY 'ERROR ACCESSING OBJECT TABLE'
//        4238  032660                       ' SQL CODE IS ' SQLCODE
//        4239  032670               MOVE 'Y' TO WS-FATAL-ERROR-FLAG
//        4240  032680               GO TO 2000-ENTRY-EXIT
//        4241  032690        END-EVALUATE

        }

//        4242  032730     IF TRN-DEBIT-CRDT-CD OF ALT-GLEN-RECORD = CREDIT

        if (costShareEntry.isCredit()) {

//        4243  032740        MOVE DEBIT
//        4244  032750          TO TRN-DEBIT-CRDT-CD OF ALT-GLEN-RECORD

            costShareOffsetEntry.setTransactionDebitCreditCode(Constants.GL_DEBIT_CODE);

//        4245  032760     ELSE

        } else {

//        4246  032770        MOVE CREDIT
//        4247  032780          TO TRN-DEBIT-CRDT-CD OF ALT-GLEN-RECORD.

            costShareOffsetEntry.setTransactionDebitCreditCode(Constants.GL_CREDIT_CODE);
        }

//        4248  032800     PERFORM 8210-WRITE-ALT-GLEN
//        4249  032810        THRU 8210-WRITE-ALT-GLEN-EXIT.

        costShareOffsetEntry.setTransactionScrubberOffsetGenerationIndicator(true);
        createOutputEntry(costShareOffsetEntry, validGroup);
        scrubberReport.costShareEntryGenerated();

        OriginEntry costShareSourceAccountEntry = new OriginEntry(costShareEntry);

//        4250  032830     MOVE COSTSHARE-DESCRIPTION
//        4251  032840       TO TRN-LDGR-ENTR-DESC OF ALT-GLEN-RECORD.
//        4252  032850     MOVE '***' TO TRN-LDGR-ENTR-DESC
//        4253  032860          OF ALT-GLEN-RECORD (34:3).
//        4254  032870     MOVE CS-MONTH TO TRN-LDGR-ENTR-DESC
//        4255  032880          OF ALT-GLEN-RECORD (37:2).
//        4256  032890     MOVE CS-DAY TO TRN-LDGR-ENTR-DESC
//        4257  032900          OF ALT-GLEN-RECORD (39:2).

        description = new StringBuffer();
        description.append(kualiConfigurationService.getPropertyString(KeyConstants.MSG_GENERATED_COST_SHARE));
        description.append(" ").append(scrubbedEntry.getAccountNumber());

        // TODO Remove after testing
        description.append("***0531");
        costShareSourceAccountEntry.setTransactionLedgerEntryDescription(description.toString());
        
        // NOTE (laran) document number and description already set above. Copied via copy constructor.

//        4258  032920     MOVE CASA21-CST-SHR-COA-CD TO FIN-COA-CD OF ALT-GLEN-RECORD.

        costShareSourceAccountEntry.setChartOfAccountsCode(scrubbedEntry.getA21SubAccount().getChartOfAccountsCode());

//        4259  032930     MOVE CASA21-CST-SHRSRCACCT-NBR TO ACCOUNT-NBR
//        4260  032940       OF ALT-GLEN-RECORD.

        costShareSourceAccountEntry.setAccountNumber(scrubbedEntry.getA21SubAccount().getCostShareSourceAccountNumber());

//        4261             PERFORM SET-OBJECT-2004 THRU SET-OBJECT-2004-EXIT.

        setCostShareObjectCode(costShareSourceAccountEntry, scrubbedEntry, errors);

//        4262  032970     MOVE CASA21-CST-SRCSUBACCT-NBR TO SUB-ACCT-NBR
//        4263  032980       OF ALT-GLEN-RECORD.

        costShareSourceAccountEntry.setSubAccountNumber(scrubbedEntry.getA21SubAccount().getCostShareSourceSubAccountNumber());

//        4264  032990     IF SUB-ACCT-NBR OF ALT-GLEN-RECORD = SPACES
//        4265  033000         MOVE SUB-ACCT-NBR-DASHES
//        4266  033010         TO SUB-ACCT-NBR OF ALT-GLEN-RECORD.

        if(StringHelper.isNullOrEmpty(costShareSourceAccountEntry.getSubAccountNumber())) {
            costShareSourceAccountEntry.setSubAccountNumber(Constants.DASHES_SUB_ACCOUNT_NUMBER);
        }

//        4267  033020     MOVE FIN-SUB-OBJ-CD-DASHES
//        4268  033030       TO FIN-SUB-OBJ-CD OF ALT-GLEN-RECORD

        costShareSourceAccountEntry.setFinancialSubObjectCode(Constants.DASHES_SUB_OBJECT_CODE);

//        4269  033090     MOVE 'TE' TO FIN-OBJ-TYP-CD OF ALT-GLEN-RECORD.

        costShareSourceAccountEntry.setFinancialObjectTypeCode("TE");

//        4270  033100     MOVE +0              TO TRN-ENTR-SEQ-NBR
//        4271  033110       OF ALT-GLEN-RECORD.

        costShareSourceAccountEntry.setTransactionLedgerEntrySequenceNumber(new Integer(0));

//        4272  033120     MOVE COSTSHARE-DESCRIPTION
//        4273  033130       TO TRN-LDGR-ENTR-DESC OF ALT-GLEN-RECORD.
//        4274  033140     MOVE '***' TO TRN-LDGR-ENTR-DESC
//        4275  033150          OF ALT-GLEN-RECORD (34:3).
//        4276  033160     MOVE CS-MONTH TO TRN-LDGR-ENTR-DESC
//        4277  033170          OF ALT-GLEN-RECORD (37:2).
//        4278  033180     MOVE CS-DAY TO TRN-LDGR-ENTR-DESC
//        4279  033190          OF ALT-GLEN-RECORD (39:2).

        // NOTE (laran) document number and description already set above. Copied via copy constructor.

//        4280  033200     MOVE SCRB-COST-SHARE-AMOUNT
//        4281  033210       TO TRN-LDGR-ENTR-AMT OF ALT-GLEN-RECORD.

        costShareSourceAccountEntry.setTransactionLedgerEntryAmount(scrubCostShareAmount);

//        4282  033230     IF SCRB-COST-SHARE-AMOUNT > ZEROES

        if (scrubCostShareAmount.isPositive()) {

//        4283  033240        MOVE CREDIT
//        4284  033250          TO TRN-DEBIT-CRDT-CD OF ALT-GLEN-RECORD

            costShareSourceAccountEntry.setTransactionDebitCreditCode(Constants.GL_CREDIT_CODE);

//        4285  033260     ELSE

        } else {

//        4286  033270        MOVE DEBIT
//        4287  033280          TO TRN-DEBIT-CRDT-CD OF ALT-GLEN-RECORD

            costShareSourceAccountEntry.setTransactionDebitCreditCode(Constants.GL_DEBIT_CODE);

//        4288  033290          COMPUTE TRN-LDGR-ENTR-AMT OF ALT-GLEN-RECORD
//        4289  033300          = SCRB-COST-SHARE-AMOUNT * -1.

            costShareSourceAccountEntry.setTransactionLedgerEntryAmount(scrubCostShareAmount.negated());

        }

//        4290  033320     MOVE SCRB-TODAYS-DATE
//        4291  033330       TO TRANSACTION-DT OF ALT-GLEN-RECORD.

        costShareSourceAccountEntry.setTransactionDate(runDate);

//        4292  033350     MOVE SPACES TO ORG-DOC-NBR       OF ALT-GLEN-RECORD.

        costShareSourceAccountEntry.setOrganizationDocumentNumber(null);

//        4293  033360     MOVE PROJECT-CD-DASHES
//        4294  033370                 TO PROJECT-CD        OF ALT-GLEN-RECORD.

        costShareSourceAccountEntry.setProjectCode(Constants.DASHES_PROJECT_CODE);

//        4295  033380     MOVE SPACES TO ORG-REFERENCE-ID  OF ALT-GLEN-RECORD
//        4296  033390                    FDOC-REF-TYP-CD   OF ALT-GLEN-RECORD
//        4297  033400                    FS-REF-ORIGIN-CD  OF ALT-GLEN-RECORD
//        4298  033410                    FDOC-REF-NBR      OF ALT-GLEN-RECORD
//        4299  033420                    FDOC-REVERSAL-DT  OF ALT-GLEN-RECORD
//        4300  033430                    TRN-ENCUM-UPDT-CD OF ALT-GLEN-RECORD.

        costShareSourceAccountEntry.setOrganizationReferenceId(null);
        costShareSourceAccountEntry.setReferenceFinancialDocumentTypeCode(null);
        costShareSourceAccountEntry.setReferenceFinancialSystemOriginationCode(null);
        costShareSourceAccountEntry.setReferenceFinancialDocumentNumber(null);
        costShareSourceAccountEntry.setFinancialDocumentReversalDate(null);
        costShareSourceAccountEntry.setTransactionEncumbranceUpdateCode(null);

//        4301  033450     PERFORM 8210-WRITE-ALT-GLEN
//        4302  033460        THRU 8210-WRITE-ALT-GLEN-EXIT.

        costShareSourceAccountEntry.setTransactionScrubberOffsetGenerationIndicator(true);
        createOutputEntry(costShareSourceAccountEntry, validGroup);
        scrubberReport.costShareEntryGenerated();

        OriginEntry costShareSourceAccountOffsetEntry = new OriginEntry(costShareSourceAccountEntry);

//        4303  033480     MOVE OFFSET-DESCRIPTION
//        4304  033490       TO TRN-LDGR-ENTR-DESC OF ALT-GLEN-RECORD.

        // costShareSourceAccountOffsetEntry.setTransactionLedgerEntryDescription(kualiConfigurationService.getPropertyString(KeyConstants.MSG_GENERATED_OFFSET));
        // TODO
        costShareSourceAccountOffsetEntry.setTransactionLedgerEntryDescription("GENERATED OFFSET                 ***0531");

        //  
//        4305  033500     MOVE '***' TO TRN-LDGR-ENTR-DESC
//        4306  033510          OF ALT-GLEN-RECORD (34:3).
//        4307  033520     MOVE CS-MONTH TO TRN-LDGR-ENTR-DESC
//        4308  033530          OF ALT-GLEN-RECORD (37:2).
//        4309  033540     MOVE CS-DAY TO TRN-LDGR-ENTR-DESC
//        4310  033550          OF ALT-GLEN-RECORD (39:2).

        // NOTE (laran) document number and description already set above. Copied via copy constructor.

//        4311  033590     MOVE UNIV-FISCAL-YR     OF ALT-GLEN-RECORD
//        4312  033600       TO GLOFSD-UNIV-FISCAL-YR.

        // NOTE (laran) Copied via copy constructor.

//        4313  033610     MOVE FIN-COA-CD         OF ALT-GLEN-RECORD
//        4314  033620       TO GLOFSD-FIN-COA-CD.

        // NOTE (laran) Copied via copy constructor.

//        4315  033630     MOVE 'TF' TO GLOFSD-FDOC-TYP-CD.
//        4316  033640     MOVE FIN-BALANCE-TYP-CD OF ALT-GLEN-RECORD
//        4317  033650       TO GLOFSD-FIN-BALANCE-TYP-CD.
//        4318  033660     EXEC SQL
//        4319  033670          SELECT FIN_OBJECT_CD,
//        4320  033680                 FIN_SUB_OBJ_CD
//        4321  033690          INTO   :GLOFSD-FIN-OBJECT-CD :GLOFSD-FOC-I,
//        4322  033700                 :GLOFSD-FIN-SUB-OBJ-CD :GLOFSD-FSOC-I
//        4323  033710          FROM   GL_OFFSET_DEFN_T
//        4324  033720          WHERE  UNIV_FISCAL_YR = RTRIM(:GLOFSD-UNIV-FISCAL-YR)
//        4325  033730            AND  FIN_COA_CD = RTRIM(:GLOFSD-FIN-COA-CD)
//        4326  033740            AND  FDOC_TYP_CD = RTRIM(:GLOFSD-FDOC-TYP-CD)
//        4327  033750            AND  FIN_BALANCE_TYP_CD
//        4328  033760                  = RTRIM(:GLOFSD-FIN-BALANCE-TYP-CD)
//        4329  033770     END-EXEC
//        4330              IF GLOFSD-FOC-I < ZERO
//        4331                MOVE SPACES TO GLOFSD-FIN-OBJECT-CD
//        4332              END-IF
//        4333              IF GLOFSD-FSOC-I < ZERO
//        4334                MOVE SPACES TO GLOFSD-FIN-SUB-OBJ-CD
//        4335              END-IF

        // Lookup the new offset definition.

        offsetDefinition = offsetDefinitionService.getByPrimaryId(scrubbedEntry.getUniversityFiscalYear(), 
                scrubbedEntry.getChartOfAccountsCode(), "TF", scrubbedEntry.getFinancialBalanceTypeCode());

//        4336  033780     EVALUATE SQLCODE
//        4337  033790          WHEN 0

        if (offsetDefinition != null) {

//        4338  033800              MOVE GLOFSD-FIN-OBJECT-CD TO
//        4339  033810                   FIN-OBJECT-CD OF ALT-GLEN-RECORD

            costShareSourceAccountOffsetEntry.setFinancialObjectCode(offsetDefinition.getFinancialObjectCode());
            costShareSourceAccountOffsetEntry.setFinancialObject(offsetDefinition.getFinancialObject());

//        4340  033820              IF GLOFSD-FIN-SUB-OBJ-CD = SPACES

            if ( ! StringUtils.hasText(offsetDefinition.getFinancialSubObjectCode())) {

//        4341  033830                   MOVE FIN-SUB-OBJ-CD-DASHES
//        4342  033840                       TO FIN-SUB-OBJ-CD OF ALT-GLEN-RECORD

                costShareSourceAccountOffsetEntry.setFinancialSubObjectCode(Constants.DASHES_SUB_OBJECT_CODE);

//        4343  033850              ELSE

            } else {

//        4344  033860                   MOVE GLOFSD-FIN-SUB-OBJ-CD TO
//        4345  033870                          FIN-SUB-OBJ-CD OF ALT-GLEN-RECORD

                costShareSourceAccountOffsetEntry.setFinancialSubObjectCode(offsetDefinition.getFinancialSubObjectCode());

//        4346  033880              END-IF

            }

//        4347  033890          WHEN +100
//        4348  033900          WHEN +1403

        } else {

//        4349  033910              MOVE ALT-GLEN-RECORD (1:51) TO RP-TABLE-KEY
//        4350                      MOVE SPACES TO RP-DATA-ERROR
//        4351  033920              MOVE 'OFFSET DEFINITION NOT FOUND' TO RP-MSG-ERROR
//        4352  033930              PERFORM WRITE-ERROR-LINE THRU WRITE-ERROR-LINE-EXIT
//        4353  033940              MOVE SPACES TO DCLGL-OFFSET-DEFN-T
//        4354  033950          WHEN OTHER
//        4355  033960              DISPLAY ' ERROR ACCESSING OFSD TABLE '
//        4356  033970                      'SQL CODE IS ' SQLCODE
//        4357  033980              MOVE 'Y' TO WS-FATAL-ERROR-FLAG
//        4358  033990               GO TO 2000-ENTRY-EXIT

            addTransactionError(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_OFFSET_DEFINITION_NOT_FOUND), 
                    offsetKey.toString(), errors);

//        4359  034000     END-EVALUATE

        }

//        4360  034050     MOVE UNIV-FISCAL-YR OF ALT-GLEN-RECORD
//        4361  034060       TO CAOBJT-UNIV-FISCAL-YR
//        4362  034070     MOVE FIN-COA-CD     OF ALT-GLEN-RECORD
//        4363  034080       TO CAOBJT-FIN-COA-CD
//        4364  034090     MOVE FIN-OBJECT-CD  OF ALT-GLEN-RECORD
//        4365  034100       TO CAOBJT-FIN-OBJECT-CD
//        4366  034110     EXEC SQL
//        4367  034120       SELECT    FIN_OBJ_TYP_CD,
//        4368  034130                 FIN_OBJ_SUB_TYP_CD,
//        4369  034140                 FIN_OBJ_ACTIVE_CD,
//        4370  034150                 FOBJ_MNXFR_ELIM_CD
//        4371  034160       INTO      :CAOBJT-FIN-OBJ-TYP-CD :CAOBJT-FOTC-I,
//        4372  034170                 :CAOBJT-FIN-OBJ-SUB-TYP-CD :CAOBJT-FOSTC-I,
//        4373  034180                 :CAOBJT-FIN-OBJ-ACTIVE-CD :CAOBJT-FOAC-I,
//        4374  034190                 :CAOBJT-FOBJ-MNXFR-ELIM-CD :CAOBJT-FMEC-I
//        4375  034200       FROM      CA_OBJECT_CODE_T
//        4376  034210       WHERE     UNIV_FISCAL_YR= RTRIM(:CAOBJT-UNIV-FISCAL-YR)
//        4377  034220         AND     FIN_COA_CD=     RTRIM(:CAOBJT-FIN-COA-CD)
//        4378  034230         AND     FIN_OBJECT_CD=  RTRIM(:CAOBJT-FIN-OBJECT-CD)
//        4379  034240     END-EXEC

        // NOTE (laran) Object code was looked up with the offset definition above.

//        4380               IF CAOBJT-FOTC-I < ZERO
//        4381                 MOVE SPACES TO CAOBJT-FIN-OBJ-TYP-CD
//        4382               END-IF
//        4383               IF CAOBJT-FOSTC-I < ZERO
//        4384                 MOVE SPACES TO CAOBJT-FIN-OBJ-SUB-TYP-CD
//        4385               END-IF
//        4386               IF CAOBJT-FOAC-I < ZERO
//        4387                 MOVE SPACE TO CAOBJT-FIN-OBJ-ACTIVE-CD
//        4388               END-IF
//        4389               IF CAOBJT-FMEC-I < ZERO
//        4390                 MOVE SPACES TO CAOBJT-FOBJ-MNXFR-ELIM-CD
//        4391               END-IF
//        4392  034250        EVALUATE SQLCODE
//        4393  034260           WHEN 0

//        4394  034270            MOVE CAOBJT-FIN-OBJ-TYP-CD TO FIN-OBJ-TYP-CD
//        4395  034280              OF ALT-GLEN-RECORD

            costShareSourceAccountOffsetEntry.setFinancialObjectTypeCode(offsetDefinition.getFinancialObject().getFinancialObjectTypeCode());

//        4396  034290           WHEN +100
//        4397  034300           WHEN +1403

//        4398  034310              MOVE GLEN-RECORD (1:51) TO RP-TABLE-KEY
//        4399  034320              MOVE CAOBJT-FIN-OBJECT-CD TO RP-DATA-ERROR
//        4400  034330                   FIN-OBJECT-CD OF ALT-GLEN-RECORD
//        4401  034340              MOVE 'NO OBJECT FOR OBJECT ON OFSD' TO RP-MSG-ERROR
//        4402  034350              PERFORM WRITE-ERROR-LINE THRU WRITE-ERROR-LINE-EXIT
//        4403  034360              MOVE SPACES TO DCLCA-OBJECT-CODE-T
//        4404  034370           WHEN OTHER
//        4405  034380               DISPLAY 'ERROR ACCESSING OBJECT TABLE'
//        4406  034390                       ' SQL CODE IS ' SQLCODE
//        4407  034400               MOVE 'Y' TO WS-FATAL-ERROR-FLAG
//        4408  034410               GO TO 2000-ENTRY-EXIT

//        4409  034420        END-EVALUATE

//        4410  034460     IF TRN-DEBIT-CRDT-CD OF ALT-GLEN-RECORD = CREDIT

        if (scrubbedEntry.isCredit()) {

//        4411  034470        MOVE DEBIT
//        4412  034480          TO TRN-DEBIT-CRDT-CD OF ALT-GLEN-RECORD

            costShareSourceAccountOffsetEntry.setTransactionDebitCreditCode(Constants.GL_DEBIT_CODE);

//        4413  034490     ELSE

        } else {

//        4414  034500        MOVE CREDIT
//        4415  034510          TO TRN-DEBIT-CRDT-CD OF ALT-GLEN-RECORD.

            costShareSourceAccountOffsetEntry.setTransactionDebitCreditCode(Constants.GL_CREDIT_CODE);

        }

//        4416  034530     PERFORM 8210-WRITE-ALT-GLEN
//        4417  034540        THRU 8210-WRITE-ALT-GLEN-EXIT.
//        4418  034550

        costShareSourceAccountOffsetEntry.setTransactionScrubberOffsetGenerationIndicator(true);
        createOutputEntry(costShareSourceAccountOffsetEntry, validGroup);
        scrubberReport.costShareEntryGenerated();

//        4419  034560     MOVE COST-SHARE-RECORD TO ALT-GLEN-RECORD.
//        4420  034570     MOVE +0           TO SCRB-COST-SHARE-AMOUNT.

        scrubCostShareAmount = KualiDecimal.ZERO;

        return errors;
    }

    /**
     * @param workingEntryInfo
     * @param accountNumber
     * @param chartCode
     */
    private void processCapitalization(OriginEntry scrubbedEntry)  {
        OriginEntry capitalizationEntry = new OriginEntry(scrubbedEntry);

//            4694  036670     IF (FIN-BALANCE-TYP-CD OF ALT-GLEN-RECORD =
//            4695  036680         FSSOPT-ACT-FIN-BAL-TYP-CD)
//            4696  036690        AND PERFORM-CAP
//            4697  036700        AND UNIV-FISCAL-YR OF ALT-GLEN-RECORD > 1995
//            4698  036710        AND FDOC-TYP-CD OF ALT-GLEN-RECORD NOT = 'TF  '
//            4699  036720        AND FDOC-TYP-CD OF ALT-GLEN-RECORD NOT = 'YETF'
//            4700  036730        AND FDOC-TYP-CD OF ALT-GLEN-RECORD NOT = 'AV  '
//            4701  036740        AND FDOC-TYP-CD OF ALT-GLEN-RECORD NOT = 'AVAC'
//            4702  036750        AND FDOC-TYP-CD OF ALT-GLEN-RECORD NOT = 'AVAE'
//            4703  036760        AND FDOC-TYP-CD OF ALT-GLEN-RECORD NOT = 'AVRC'
//            4704  036770        AND (UNIV-FISCAL-PRD-CD OF GLEN-RECORD NOT = 'BB'
//            4705  036780        AND  UNIV-FISCAL-PRD-CD OF GLEN-RECORD NOT = 'CB'
//            4706  036790        AND  FDOC-TYP-CD OF GLEN-RECORD NOT = 'ACLO')
//            4707  036800        AND (CAOBJT-FIN-OBJ-SUB-TYP-CD = 'AM' OR
//            4708  036810             CAOBJT-FIN-OBJ-SUB-TYP-CD = 'AF' OR
//            4709  036820             CAOBJT-FIN-OBJ-SUB-TYP-CD = 'BD' OR
//            4710  036830             CAOBJT-FIN-OBJ-SUB-TYP-CD = 'BF' OR
//            4711                     CAOBJT-FIN-OBJ-SUB-TYP-CD = 'BI' OR
//            4712  036840             CAOBJT-FIN-OBJ-SUB-TYP-CD = 'BR' OR
//            4713  036850             CAOBJT-FIN-OBJ-SUB-TYP-CD = 'BX' OR
//            4714  036860             CAOBJT-FIN-OBJ-SUB-TYP-CD = 'BY' OR
//            4715  036870             CAOBJT-FIN-OBJ-SUB-TYP-CD = 'CM' OR
//            4716  036880             CAOBJT-FIN-OBJ-SUB-TYP-CD = 'CF' OR
//            4717                     CAOBJT-FIN-OBJ-SUB-TYP-CD = 'C1' OR
//            4718                     CAOBJT-FIN-OBJ-SUB-TYP-CD = 'C2' OR
//            4719                     CAOBJT-FIN-OBJ-SUB-TYP-CD = 'C3' OR
//            4720  036890             CAOBJT-FIN-OBJ-SUB-TYP-CD = 'ES' OR
//            4721  036900             CAOBJT-FIN-OBJ-SUB-TYP-CD = 'IF' OR
//            4722  036910             CAOBJT-FIN-OBJ-SUB-TYP-CD = 'LA' OR
//            4723  036920             CAOBJT-FIN-OBJ-SUB-TYP-CD = 'LE' OR
//            4724  036930             CAOBJT-FIN-OBJ-SUB-TYP-CD = 'LF' OR
//            4725  036940             CAOBJT-FIN-OBJ-SUB-TYP-CD = 'LI' OR
//            4726  036950             CAOBJT-FIN-OBJ-SUB-TYP-CD = 'LR' OR
//            4727  036960             CAOBJT-FIN-OBJ-SUB-TYP-CD = 'UC' OR
//            4728  036970             CAOBJT-FIN-OBJ-SUB-TYP-CD = 'UF')
//            4729  036980        AND (CAACCT-SUB-FUND-GRP-CD NOT = 'EXTAGY')
//            4730  036990        AND (FIN-COA-CD OF GLEN-RECORD NOT = 'HO')

        if (scrubbedEntry.getFinancialBalanceTypeCode().equals(scrubbedEntry.getOption().getActualFinancialBalanceTypeCd())
                && scrubbedEntry.getUniversityFiscalYear().intValue() > 1995
                && !ObjectHelper.isOneOf(scrubbedEntry.getFinancialDocumentTypeCode(), badDocumentTypeCodesForCapitalization)
                && !ObjectHelper.isOneOf(scrubbedEntry.getUniversityFiscalPeriodCode(), badUniversityFiscalPeriodCodesForCapitalization)
                && !"ACLO".equals(scrubbedEntry.getFinancialDocumentTypeCode())  
                && ObjectHelper.isOneOf(scrubbedEntry.getFinancialObject().getFinancialObjectSubTypeCode(), goodObjectSubTypeCodesForCapitalization)
                && !"EXTAGY".equals(scrubbedEntry.getAccount().getSubFundGroupCode())
                && !"HO".equals(scrubbedEntry.getChartOfAccountsCode())) {

//            4731  037000        SET FROM-CAMS TO TRUE
//            4732  037010        EVALUATE CAOBJT-FIN-OBJ-SUB-TYP-CD
//            4733  037020           WHEN 'AM'
//            4734  037030              MOVE '8615' TO FIN-OBJECT-CD OF ALT-GLEN-RECORD
//                                   ...
//            4777  037380        END-EVALUATE

            String objectSubTypeCode = scrubbedEntry.getFinancialObject().getFinancialObjectSubTypeCode();
            if (objectSubTypeCodesToObjectCodesForCapitalization.containsKey(objectSubTypeCode)) {
                capitalizationEntry.setFinancialObjectCode( (String)objectSubTypeCodesToObjectCodesForCapitalization.get(objectSubTypeCode));
                persistenceService.retrieveReferenceObject(capitalizationEntry, "financialObject");
            }

//            4778  037440        MOVE 'AS'
//            4779  037450          TO FIN-OBJ-TYP-CD OF ALT-GLEN-RECORD

            capitalizationEntry.setFinancialObjectTypeCode("AS");
            persistenceService.retrieveReferenceObject(capitalizationEntry, "objectType");

//            4780  037460        MOVE LIT-GEN-CAPITALIZATION
//            4781  037470          TO TRN-LDGR-ENTR-DESC OF ALT-GLEN-RECORD

            capitalizationEntry.setTransactionLedgerEntryDescription(kualiConfigurationService.getPropertyString(KeyConstants.MSG_GENERATED_CAPITALIZATION)); 

//            4782  037480        PERFORM 4000-PLANT-FUND-ACCT
//            4783  037490           THRU 4000-PLANT-FUND-ACCT-EXIT

            plantFundAccountLookup(scrubbedEntry, capitalizationEntry);

//            4784  037500        PERFORM 8210-WRITE-ALT-GLEN
//            4785  037510           THRU 8210-WRITE-ALT-GLEN-EXIT

            // This should fix KULGL-54.
            capitalizationEntry.setUniversityFiscalPeriodCode(scrubbedEntry.getUniversityFiscalPeriodCode());

            capitalizationEntry.setTransactionScrubberOffsetGenerationIndicator(true);
            createOutputEntry(capitalizationEntry, validGroup);
            scrubberReport.capitalizationEntryGenerated();

            // Clear out the id & the ojb version number to make sure we do an insert on the next one
            capitalizationEntry.setVersionNumber(null);
            capitalizationEntry.setEntryId(null);

//            4786  037570          MOVE '9899'
//            4787  037580          TO FIN-OBJECT-CD  OF ALT-GLEN-RECORD

            capitalizationEntry.setFinancialObjectCode("9899");

//            4788  037600        MOVE 'FB'
//            4789  037610          TO FIN-OBJ-TYP-CD OF ALT-GLEN-RECORD

            capitalizationEntry.setFinancialObjectTypeCode("FB");

//            4790  037620        IF TRN-DEBIT-CRDT-CD OF WS-SAVED-FIELDS = DEBIT
//            4791  037630           MOVE CREDIT
//            4792  037640             TO TRN-DEBIT-CRDT-CD OF ALT-GLEN-RECORD
//            4793  037650        ELSE
//            4794  037660           MOVE DEBIT
//            4795  037670             TO TRN-DEBIT-CRDT-CD OF ALT-GLEN-RECORD
//            4796  037680        END-IF

            if (scrubbedEntry.isDebit()) {
                capitalizationEntry.setTransactionDebitCreditCode(Constants.GL_CREDIT_CODE);
            } else {
                capitalizationEntry.setTransactionDebitCreditCode(Constants.GL_DEBIT_CODE);
            }

//            4797  037690        PERFORM 8210-WRITE-ALT-GLEN
//            4798  037700           THRU 8210-WRITE-ALT-GLEN-EXIT.        

            capitalizationEntry.setTransactionScrubberOffsetGenerationIndicator(true);
            createOutputEntry(capitalizationEntry, validGroup);
            scrubberReport.capitalizationEntryGenerated();
        }
    }

    /**
     * @param originEntry
     * @param workingEntryInfo
     */
    private void processPlantIndebtedness(OriginEntry scrubbedEntry) {

        OriginEntry plantIndebtednessEntry = new OriginEntry(scrubbedEntry);

//            4855  038410     IF (FIN-BALANCE-TYP-CD OF ALT-GLEN-RECORD =
//            4856  038420         FSSOPT-ACT-FIN-BAL-TYP-CD)
//            4857  038430      AND (CAACCT-SUB-FUND-GRP-CD = 'PFCMR ' OR 'PFRI  ')
//            4858  038440      AND (CAOBJT-FIN-OBJ-SUB-TYP-CD =  'PI')
//            4859  038450      AND PERFORM-PLANT

        if (scrubbedEntry.getFinancialBalanceTypeCode().equals(scrubbedEntry.getOption().getActualFinancialBalanceTypeCd())
            && ObjectHelper.isOneOf(scrubbedEntry.getAccount().getSubFundGroupCode(),validPlantIndebtednessSubFundGroupCodes)
            && "PI".equals(scrubbedEntry.getFinancialObject().getFinancialObjectSubTypeCode())) {

//            4860  038460        SET FROM-PLANT-INDEBTEDNESS TO TRUE
//            4861  038470        MOVE LIT-GEN-XFER-TO-PLANT
//            4862  038480          TO TRN-LDGR-ENTR-DESC OF ALT-GLEN-RECORD

            plantIndebtednessEntry.setTransactionLedgerEntryDescription(Constants.PLANT_INDEBTEDNESS_ENTRY_DESCRIPTION);

//            4863  038490        IF TRN-DEBIT-CRDT-CD OF WS-SAVED-FIELDS = DEBIT
//            4864  038500           MOVE CREDIT
//            4865  038510             TO TRN-DEBIT-CRDT-CD OF ALT-GLEN-RECORD
//            4866  038520        ELSE
//            4867  038530           MOVE DEBIT
//            4868  038540             TO TRN-DEBIT-CRDT-CD OF ALT-GLEN-RECORD
//            4869  038550        END-IF

            if (scrubbedEntry.isDebit()) {
                plantIndebtednessEntry.setTransactionDebitCreditCode(Constants.GL_CREDIT_CODE);
            } else {
                plantIndebtednessEntry.setTransactionDebitCreditCode(Constants.GL_DEBIT_CODE);
            }

//            4870  038560        PERFORM 8210-WRITE-ALT-GLEN
//            4871  038570           THRU 8210-WRITE-ALT-GLEN-EXIT

            plantIndebtednessEntry.setTransactionScrubberOffsetGenerationIndicator(true);
            createOutputEntry(plantIndebtednessEntry, validGroup);
            scrubberReport.plantIndebtednessEntryGenerated();

            // Clear out the id & the ojb version number to make sure we do an insert on the next one
            plantIndebtednessEntry.setVersionNumber(null);
            plantIndebtednessEntry.setEntryId(null);

//            4872  038630        MOVE '9899'
//            4873  038640          TO FIN-OBJECT-CD  OF ALT-GLEN-RECORD

            plantIndebtednessEntry.setFinancialObjectCode("9899"); // FUND_BALANCE

//            4874  038660        MOVE 'FB'
//            4875  038670          TO FIN-OBJ-TYP-CD OF ALT-GLEN-RECORD

            plantIndebtednessEntry.setFinancialObjectTypeCode("FB"); // FUND_BALANCE

//            4876  038680        MOVE TRN-DEBIT-CRDT-CD OF WS-SAVED-FIELDS
//            4877  038690          TO TRN-DEBIT-CRDT-CD OF ALT-GLEN-RECORD

            plantIndebtednessEntry.setTransactionDebitCreditCode(scrubbedEntry.getTransactionDebitCreditCode());

//            4878  038700        PERFORM 8210-WRITE-ALT-GLEN
//            4879  038710           THRU 8210-WRITE-ALT-GLEN-EXIT

            plantIndebtednessEntry.setTransactionScrubberOffsetGenerationIndicator(true);
            createOutputEntry(plantIndebtednessEntry, validGroup);
            scrubberReport.plantIndebtednessEntryGenerated();

            // Clear out the id & the ojb version number to make sure we do an insert on the next one
            plantIndebtednessEntry.setVersionNumber(null);
            plantIndebtednessEntry.setEntryId(null);

//            4880  038750        MOVE FIN-OBJECT-CD      OF WS-SAVED-FIELDS
//            4881  038760          TO FIN-OBJECT-CD      OF ALT-GLEN-RECORD

            plantIndebtednessEntry.setFinancialObjectCode(scrubbedEntry.getFinancialObjectCode());

//            4882  038770        MOVE FIN-OBJ-TYP-CD     OF WS-SAVED-FIELDS
//            4883  038780          TO FIN-OBJ-TYP-CD     OF ALT-GLEN-RECORD

            plantIndebtednessEntry.setFinancialObjectTypeCode(scrubbedEntry.getFinancialObjectTypeCode());

//            4884  038790        MOVE TRN-DEBIT-CRDT-CD  OF WS-SAVED-FIELDS
//            4885  038800          TO TRN-DEBIT-CRDT-CD  OF ALT-GLEN-RECORD

            plantIndebtednessEntry.setTransactionDebitCreditCode(scrubbedEntry.getTransactionDebitCreditCode());

//            4886  038810        MOVE TRN-LDGR-ENTR-DESC OF WS-SAVED-FIELDS
//            4887  038820          TO TRN-LDGR-ENTR-DESC OF ALT-GLEN-RECORD

            // NOTE (laran) Used this to debug KULGL-54. Not positive it's resolve so leaving it here commented out.
            plantIndebtednessEntry.setTransactionLedgerEntryDescription(scrubbedEntry.getTransactionLedgerEntryDescription());
            // plantIndebtednessEntry.setTransactionLedgerEntryDescription("PI ENTRY: I MIGHT BE BROKEN");

//            4888  038830        MOVE ACCOUNT-NBR        OF WS-SAVED-FIELDS
//            4889  038840          TO ACCOUNT-NBR        OF ALT-GLEN-RECORD

            plantIndebtednessEntry.setAccountNumber(scrubbedEntry.getAccountNumber());

//            4890  038850        MOVE SUB-ACCT-NBR       OF WS-SAVED-FIELDS
//            4891                  TO SUB-ACCT-NBR       OF ALT-GLEN-RECORD        

            plantIndebtednessEntry.setSubAccountNumber(scrubbedEntry.getSubAccountNumber());

//          5163  041480     IF CAORGN-FIN-COA-CD = CAACCT-FIN-COA-CD
//          5164  041490       AND
//          5165  041500        CAORGN-ORG-CD = CAACCT-ORG-CD
//          5166  041510       AND
//          5167  041520        ACCOUNT-NBR OF WS-SAVED-FIELDS = CAACCT-ACCOUNT-NBR
//          5168  041530       AND
//          5169  041540        FIN-COA-CD OF WS-SAVED-FIELDS = CAACCT-FIN-COA-CD

            // TODO Check this logic
            if (scrubbedEntry.getChartOfAccountsCode().equals(scrubbedEntry.getAccount().getOrganization().getChartOfAccountsCode())
                && scrubbedEntry.getAccount().getOrganizationCode().equals(scrubbedEntry.getAccount().getOrganization().getOrganizationCode())
                && scrubbedEntry.getAccountNumber().equals(scrubbedEntry.getAccount().getAccountNumber())
                && scrubbedEntry.getChartOfAccountsCode().equals(scrubbedEntry.getAccount().getChartOfAccountsCode())) {

//            4899  038970        MOVE CAORGN-CMP-PLNT-ACCT-NBR
//            4900  038980            TO ACCOUNT-NBR OF ALT-GLEN-RECORD
//            4901  038990        MOVE CAORGN-CMP-PLNT-COA-CD
//            4902  039000            TO FIN-COA-CD OF ALT-GLEN-RECORD

                plantIndebtednessEntry.setAccountNumber(scrubbedEntry.getAccount().getOrganization().getCampusPlantAccountNumber());
                plantIndebtednessEntry.setChartOfAccountsCode(scrubbedEntry.getAccount().getOrganization().getCampusPlantChartCode());

//            4903  039010      ELSE

            } else {

//            4904  039020        MOVE FIN-COA-CD OF WS-SAVED-FIELDS
//            4905  039030          TO CAORGN-FIN-COA-CD
//            4906  039040        MOVE CAACCT-ORG-CD TO CAORGN-ORG-CD
//            4907  039050        EXEC SQL
//            4908  039060           SELECT ORG_ACTIVE_CD,
//            4909  039070                  ORG_PLNT_ACCT_NBR,
//            4910  039080                  CMP_PLNT_ACCT_NBR,
//            4911  039090                  ORG_PLNT_COA_CD,
//            4912  039100                  CMP_PLNT_COA_CD
//            4913  039110           INTO  :CAORGN-ORG-ACTIVE-CD :CAORGN-OAC-I,
//            4914  039120                 :CAORGN-ORG-PLNT-ACCT-NBR :CAORGN-OPAN-I,
//            4915  039130                 :CAORGN-CMP-PLNT-ACCT-NBR :CAORGN-CPAN-I,
//            4916  039140                 :CAORGN-ORG-PLNT-COA-CD :CAORGN-OPCC-I,
//            4917  039150                 :CAORGN-CMP-PLNT-COA-CD :CAORGN-CPCC-I
//            4918  039160           FROM  CA_ORG_T
//            4919  039170           WHERE FIN_COA_CD = RTRIM(:CAORGN-FIN-COA-CD)
//            4920  039180           AND   ORG_CD     = RTRIM(:CAORGN-ORG-CD)
//            4921  039190        END-EXEC
//            4922                 IF CAORGN-OAC-I < ZERO
//            4923                   MOVE SPACE TO CAORGN-ORG-ACTIVE-CD
//            4924                 END-IF
//            4925                 IF CAORGN-OPAN-I < ZERO
//            4926                   MOVE SPACES TO CAORGN-ORG-PLNT-ACCT-NBR
//            4927                 END-IF
//            4928                 IF CAORGN-CPAN-I < ZERO
//            4929                   MOVE SPACES TO CAORGN-CMP-PLNT-ACCT-NBR
//            4930                 END-IF
//            4931                 IF CAORGN-OPCC-I < ZERO
//            4932                   MOVE SPACES TO CAORGN-ORG-PLNT-COA-CD
//            4933                 END-IF
//            4934                 IF CAORGN-CPCC-I < ZERO
//            4935                   MOVE SPACES TO CAORGN-CMP-PLNT-COA-CD
//            4936                 END-IF
//            4937  039200        EVALUATE SQLCODE
//            4938  039210           WHEN 0
//            4939  039220                MOVE CAORGN-CMP-PLNT-ACCT-NBR TO
//            4940  039230                     ACCOUNT-NBR OF ALT-GLEN-RECORD
//            4941  039240                MOVE CAORGN-CMP-PLNT-COA-CD TO
//            4942  039250                     FIN-COA-CD OF ALT-GLEN-RECORD
//            4943  039260           WHEN +100
//            4944  039270           WHEN +1403
//            4945  039280                MOVE ALT-GLEN-RECORD (1:51) TO RP-TABLE-KEY
//            4946                        MOVE SPACES TO RP-DATA-ERROR
//            4947  039290                MOVE 'INVALID ORG CODE FOR PLANT FUND'
//            4948  039300                     TO RP-MSG-ERROR
//            4949  039310                PERFORM WRITE-ERROR-LINE THRU
//            4950  039320                        WRITE-ERROR-LINE-EXIT
//            4951  039330           WHEN OTHER
//            4952  039340                DISPLAY ' ERROR ACCESSING ORGN TABLE'
//            4953  039350                    ' SQL CODE IS ' SQLCODE
//            4954  039360                MOVE 'Y' TO WS-FATAL-ERROR-FLAG
//            4955  039370                GO TO 3000-USER-PROCESSING-EXIT
//            4956  039380     END-IF

            }

//            4957  039390        MOVE '-----' TO SUB-ACCT-NBR OF ALT-GLEN-RECORD

            plantIndebtednessEntry.setSubAccountNumber(Constants.DASHES_SUB_ACCOUNT_NUMBER);

            StringBuffer litGenPlantXferFrom = new StringBuffer();
            litGenPlantXferFrom.append("GENERATED TRANSFER FROM ");

//            4958  039400        MOVE FIN-COA-CD  OF WS-SAVED-FIELDS
//            4959  039410          TO FIN-COA-CD  OF LIT-GEN-PLANT-XFER-FROM

            litGenPlantXferFrom.append(scrubbedEntry.getChartOfAccountsCode()).append(" ");

//            4960  039420        MOVE ACCOUNT-NBR OF WS-SAVED-FIELDS
//            4961  039430          TO ACCOUNT-NBR OF LIT-GEN-PLANT-XFER-FROM

            litGenPlantXferFrom.append(scrubbedEntry.getAccountNumber());

//            4962  039440        MOVE                LIT-GEN-PLANT-XFER-FROM
//            4963  039450          TO TRN-LDGR-ENTR-DESC OF ALT-GLEN-RECORD

            plantIndebtednessEntry.setTransactionLedgerEntryDescription(litGenPlantXferFrom.toString());

//            4964  039460        PERFORM 8210-WRITE-ALT-GLEN
//            4965  039470           THRU 8210-WRITE-ALT-GLEN-EXIT

            plantIndebtednessEntry.setTransactionScrubberOffsetGenerationIndicator(true);
            createOutputEntry(plantIndebtednessEntry, validGroup);
            scrubberReport.plantIndebtednessEntryGenerated();

            // Clear out the id & the ojb version number to make sure we do an insert on the next one
            plantIndebtednessEntry.setVersionNumber(null);
            plantIndebtednessEntry.setEntryId(null);

//            4966  039490        MOVE '9899'
//            4967  039500          TO FIN-OBJECT-CD  OF ALT-GLEN-RECORD

            plantIndebtednessEntry.setFinancialObjectCode("9899");

//            4968  039510        MOVE 'FB'
//            4969  039520          TO FIN-OBJ-TYP-CD OF ALT-GLEN-RECORD

            plantIndebtednessEntry.setFinancialObjectTypeCode("FB");

//            4970  039530        IF TRN-DEBIT-CRDT-CD OF WS-SAVED-FIELDS = DEBIT
//            4971  039540           MOVE CREDIT
//            4972  039550             TO TRN-DEBIT-CRDT-CD OF ALT-GLEN-RECORD
//            4973  039560        ELSE
//            4974  039570           MOVE DEBIT
//            4975  039580             TO TRN-DEBIT-CRDT-CD OF ALT-GLEN-RECORD
//            4976  039590        END-IF

            if (scrubbedEntry.isDebit()) {
                plantIndebtednessEntry.setTransactionDebitCreditCode(Constants.GL_CREDIT_CODE);
            } else {
                plantIndebtednessEntry.setTransactionDebitCreditCode(Constants.GL_DEBIT_CODE);
            }

//            4977  039600        PERFORM 8210-WRITE-ALT-GLEN
//            4978  039610           THRU 8210-WRITE-ALT-GLEN-EXIT

            plantIndebtednessEntry.setTransactionScrubberOffsetGenerationIndicator(true);
            createOutputEntry(plantIndebtednessEntry, validGroup);
            scrubberReport.plantIndebtednessEntryGenerated();
        }
    }

    /**
     * @param workingEntryInfo
     * @param debitOrCreditCode
     * @param accountNumber
     * @param chartCode
     */
    private void processLiabilities(OriginEntry scrubbedEntry) {
        OriginEntry liabilityEntry = new OriginEntry(scrubbedEntry);

//            4799  037740     IF (FIN-BALANCE-TYP-CD OF ALT-GLEN-RECORD =
//            4800  037750         FSSOPT-ACT-FIN-BAL-TYP-CD)
//            4801  037760        AND PERFORM-LIAB
//            4802  037770        AND UNIV-FISCAL-YR OF ALT-GLEN-RECORD > 1995
//            4803  037780        AND FDOC-TYP-CD OF ALT-GLEN-RECORD NOT = 'TF  '
//            4804  037790        AND FDOC-TYP-CD OF ALT-GLEN-RECORD NOT = 'YETF'
//            4805  037800        AND FDOC-TYP-CD OF ALT-GLEN-RECORD NOT = 'AV  '
//            4806  037810        AND FDOC-TYP-CD OF ALT-GLEN-RECORD NOT = 'AVAC'
//            4807  037820        AND FDOC-TYP-CD OF ALT-GLEN-RECORD NOT = 'AVAE'
//            4808  037830        AND FDOC-TYP-CD OF ALT-GLEN-RECORD NOT = 'AVRC'
//            4809  037840        AND (UNIV-FISCAL-PRD-CD OF GLEN-RECORD NOT = 'BB'
//            4810  037850        AND  UNIV-FISCAL-PRD-CD OF GLEN-RECORD NOT = 'CB'
//            4811  037860        AND  FDOC-TYP-CD OF GLEN-RECORD NOT = 'ACLO')
//            4812  037870        AND CAOBJT-FIN-OBJ-SUB-TYP-CD =  'CL'
//            4813  037880        AND (CAACCT-SUB-FUND-GRP-CD NOT = 'EXTAGY')
//            4814  037890        AND (FIN-COA-CD OF GLEN-RECORD NOT = 'HO')

        if ( scrubbedEntry.getFinancialBalanceTypeCode().equals(scrubbedEntry.getOption().getActualFinancialBalanceTypeCd())
                && scrubbedEntry.getUniversityFiscalYear().intValue() > 1995 
                && !ObjectHelper.isOneOf(scrubbedEntry.getFinancialDocumentTypeCode(),invalidDocumentTypesForLiabilities)
                && !ObjectHelper.isOneOf(scrubbedEntry.getUniversityFiscalPeriodCode(),invalidFiscalPeriodCodesForOffsetGeneration)
                && !"ACLO".equals(scrubbedEntry.getFinancialDocumentTypeCode())
                && "CL".equals(scrubbedEntry.getFinancialObject().getFinancialObjectSubTypeCode())
                && !"EXTAGY".equals(scrubbedEntry.getAccount().getSubFundGroupCode())
                && !"HO".equals(scrubbedEntry.getChartOfAccountsCode())) {

//          4815  037900        MOVE '9603' TO FIN-OBJECT-CD OF ALT-GLEN-RECORD

            liabilityEntry.setFinancialObjectCode("9603"); // NOTES_PAYABLE_CAPITAL_LEASE

//          4816  037910        SET FROM-LIAB TO TRUE
//          4817  037920        MOVE 'LI'
//          4818  037930          TO FIN-OBJ-TYP-CD OF ALT-GLEN-RECORD

            liabilityEntry.setFinancialObjectTypeCode("LI"); // LIABILITY

//          4819  037940        MOVE TRN-DEBIT-CRDT-CD OF WS-SAVED-FIELDS
//          4820  037950          TO TRN-DEBIT-CRDT-CD OF ALT-GLEN-RECORD

            liabilityEntry.setTransactionDebitCreditCode(scrubbedEntry.getTransactionDebitCreditCode());

//          4821  037960        MOVE LIT-GEN-LIABILITY
//          4822  037970          TO TRN-LDGR-ENTR-DESC OF ALT-GLEN-RECORD

            liabilityEntry.setTransactionLedgerEntryDescription(kualiConfigurationService.getPropertyString(KeyConstants.MSG_GENERATED_LIABILITY));

//          4823  037980        PERFORM 4000-PLANT-FUND-ACCT
//          4824  037990           THRU 4000-PLANT-FUND-ACCT-EXIT

            plantFundAccountLookup(scrubbedEntry, liabilityEntry);

//          4825  038000        PERFORM 8210-WRITE-ALT-GLEN
//          4826  038010           THRU 8210-WRITE-ALT-GLEN-EXIT

            liabilityEntry.setTransactionScrubberOffsetGenerationIndicator(true);
            createOutputEntry(liabilityEntry, validGroup);
            scrubberReport.liabilityEntryGenerated();

            // Clear out the id & the ojb version number to make sure we do an insert on the next one
            liabilityEntry.setVersionNumber(null);
            liabilityEntry.setEntryId(null);

            // ... and now generate the other half of the liability entry

//          4827  038020         MOVE '9899'
//          4828  038030          TO FIN-OBJECT-CD  OF ALT-GLEN-RECORD

            liabilityEntry.setFinancialObjectCode("9899"); // FUND_BALANCE

//          4829  038040        MOVE 'FB'
//          4830  038050          TO FIN-OBJ-TYP-CD OF ALT-GLEN-RECORD

            liabilityEntry.setFinancialObjectTypeCode("FB"); // FUND_BALANCE

//          4831  038060        IF TRN-DEBIT-CRDT-CD OF WS-SAVED-FIELDS = DEBIT
//          4832  038070           MOVE CREDIT
//          4833  038080             TO TRN-DEBIT-CRDT-CD OF ALT-GLEN-RECORD
//          4834  038090        ELSE
//          4835  038100           MOVE DEBIT
//          4836  038110             TO TRN-DEBIT-CRDT-CD OF ALT-GLEN-RECORD
//          4837  038120        END-IF

            if (liabilityEntry.isDebit()) {
                liabilityEntry.setTransactionDebitCreditCode(Constants.GL_CREDIT_CODE);
            } else {
                liabilityEntry.setTransactionDebitCreditCode(Constants.GL_DEBIT_CODE);
            }

//          4838  038130        PERFORM 8210-WRITE-ALT-GLEN
//          4839  038140           THRU 8210-WRITE-ALT-GLEN-EXIT

            liabilityEntry.setTransactionScrubberOffsetGenerationIndicator(true);
            createOutputEntry(liabilityEntry, validGroup);
            scrubberReport.liabilityEntryGenerated();
        }
    }

    /**
     * @param workingEntry
     * @param chartCode
     * @param accountNumber
     */
    private void plantFundAccountLookup(OriginEntry scrubbedEntry, OriginEntry liabilityEntry) {
        // TODO Check this logic

//        5162  041470     MOVE '-----' TO SUB-ACCT-NBR OF ALT-GLEN-RECORD.

        liabilityEntry.setSubAccountNumber(Constants.DASHES_SUB_ACCOUNT_NUMBER);

//        5163  041480     IF CAORGN-FIN-COA-CD = CAACCT-FIN-COA-CD
//        5164  041490       AND
//        5165  041500        CAORGN-ORG-CD = CAACCT-ORG-CD
//        5166  041510       AND
//        5167  041520        ACCOUNT-NBR OF WS-SAVED-FIELDS = CAACCT-ACCOUNT-NBR
//        5168  041530       AND
//        5169  041540        FIN-COA-CD OF WS-SAVED-FIELDS = CAACCT-FIN-COA-CD

        persistenceService.retrieveReferenceObject(liabilityEntry, "account");

        if (liabilityEntry.getChartOfAccountsCode().equals(liabilityEntry.getAccount().getOrganization().getChartOfAccountsCode())
            && scrubbedEntry.getAccount().getOrganizationCode().equals(liabilityEntry.getAccount().getOrganization().getOrganizationCode())
            && liabilityEntry.getAccountNumber().equals(liabilityEntry.getAccount().getAccountNumber())
            && liabilityEntry.getChartOfAccountsCode().equals(liabilityEntry.getAccount().getChartOfAccountsCode())) {

//        5170  041550      EVALUATE CAOBJT-FIN-OBJ-SUB-TYP-CD
//        5171  041560       WHEN 'AM'
//        5172  041570       WHEN 'AF'
//        5173  041580       WHEN 'BD'
//        5174  041590       WHEN 'BF'
//        5175               WHEN 'BI'
//        5176  041600       WHEN 'BR'
//        5177  041610       WHEN 'BX'
//        5178  041620       WHEN 'BY'
//        5179  041630       WHEN 'IF'
//        5180  041640       WHEN 'LA'
//        5181  041650       WHEN 'LE'
//        5182  041660       WHEN 'LF'
//        5183  041670       WHEN 'LI'
//        5184  041680       WHEN 'LR'
//        5185  041690        MOVE CAORGN-CMP-PLNT-ACCT-NBR
//        5186  041700          TO ACCOUNT-NBR OF ALT-GLEN-RECORD
//        5187  041710        MOVE CAORGN-CMP-PLNT-COA-CD
//        5188  041720          TO FIN-COA-CD OF ALT-GLEN-RECORD
//        5189  041730       WHEN 'CL'
//        5190  041740       WHEN 'CM'
//        5191  041750       WHEN 'CF'
//        5192               WHEN 'C1'
//        5193               WHEN 'C2'
//        5194               WHEN 'C3'
//        5195  041760       WHEN 'ES'
//        5196  041770       WHEN 'UC'
//        5197  041780       WHEN 'UF'
//        5198  041790        MOVE CAORGN-ORG-PLNT-ACCT-NBR
//        5199  041800          TO ACCOUNT-NBR OF ALT-GLEN-RECORD
//        5200  041810        MOVE CAORGN-ORG-PLNT-COA-CD
//        5201  041820          TO FIN-COA-CD OF ALT-GLEN-RECORD
//        5202  041830      END-EVALUATE        

            persistenceService.retrieveReferenceObject(liabilityEntry, "financialObject");
            persistenceService.retrieveReferenceObject(liabilityEntry.getAccount(), "organization");

            String objectSubTypeCode = scrubbedEntry.getFinancialObject().getFinancialObjectSubTypeCode();

            if (ObjectHelper.isOneOf(objectSubTypeCode, campusObjectSubTypeCodesForPlantFundAccountLookups)) {
                liabilityEntry.setAccountNumber(scrubbedEntry.getAccount().getOrganization().getCampusPlantAccountNumber());
                liabilityEntry.setChartOfAccountsCode(scrubbedEntry.getAccount().getOrganization().getCampusPlantChartCode());

                persistenceService.retrieveReferenceObject(liabilityEntry, "account");
                persistenceService.retrieveReferenceObject(liabilityEntry, "chart");
            } else if (ObjectHelper.isOneOf(objectSubTypeCode, organizationObjectSubTypeCodesForPlantFundAccountLookups)) {
                liabilityEntry.setAccountNumber(scrubbedEntry.getAccount().getOrganization().getOrganizationPlantAccountNumber());
                liabilityEntry.setChartOfAccountsCode(scrubbedEntry.getAccount().getOrganization().getOrganizationPlantChartCode());

                persistenceService.retrieveReferenceObject(liabilityEntry, "account");
                persistenceService.retrieveReferenceObject(liabilityEntry, "chart");
            }
        }
    }

    private static String SPACES = "                                        ";

    /**
     * 3200-COST-SHARE-ENC
     * 
     * The purpose of this method is to generate a "Cost Share Encumbrance" transaction
     * for the current transaction and its offset.
     * 
     * The cost share chart and account for current transaction are obtained from the
     * CA_A21_SUB_ACCT_T table. This method calls the method SET-OBJECT-2004 to get
     * the Cost Share Object Code. It then writes out the cost share transaction.
     * Next it read the GL_OFFSET_DEFN_T table for the offset object code that
     * corresponds to the cost share object code. In addition to the object code it
     * needs to get subobject code. It then reads the CA_OBJECT_CODE_T table to make
     * sure the offset object code found in the GL_OFFSET_DEFN_T is valid  and to
     * get the object type code associated with this object code. It writes out the
     * offset transaction and returns.
     * 
     * @param scrubbedEntry
     */
    private List generateCostShareEncumbranceEntries(OriginEntry scrubbedEntry) {

        List errors = new ArrayList();

        OriginEntry costShareEncumbranceEntry = new OriginEntry(scrubbedEntry);

        // First 28 characters of the description, padding to 28 if shorter)
        StringBuffer buffer = new StringBuffer( (scrubbedEntry.getTransactionLedgerEntryDescription() + SPACES).substring(0, 28));

        buffer.append("FR-");
        buffer.append(costShareEncumbranceEntry.getChartOfAccountsCode());
        buffer.append(costShareEncumbranceEntry.getAccountNumber());

        costShareEncumbranceEntry.setTransactionLedgerEntryDescription(buffer.toString());

        costShareEncumbranceEntry.setChartOfAccountsCode(scrubbedEntry.getA21SubAccount().getCostShareChartOfAccountCode());
        costShareEncumbranceEntry.setAccountNumber(scrubbedEntry.getA21SubAccount().getCostShareSourceAccountNumber());
        costShareEncumbranceEntry.setSubAccountNumber(scrubbedEntry.getA21SubAccount().getCostShareSourceSubAccountNumber());

        if (!StringUtils.hasText(costShareEncumbranceEntry.getSubAccountNumber())) {
            costShareEncumbranceEntry.setSubAccountNumber(Constants.DASHES_SUB_ACCOUNT_NUMBER);
        }

        costShareEncumbranceEntry.setFinancialBalanceTypeCode("CE");
        costShareEncumbranceEntry.setFinancialSubObjectCode(Constants.DASHES_SUB_OBJECT_CODE);
        costShareEncumbranceEntry.setTransactionLedgerEntrySequenceNumber(new Integer(0));

        if (!StringUtils.hasText(scrubbedEntry.getTransactionDebitCreditCode())) {
            if (scrubbedEntry.getTransactionLedgerEntryAmount().isPositive()) {
                costShareEncumbranceEntry.setTransactionDebitCreditCode(Constants.GL_DEBIT_CODE);
            } else {
                costShareEncumbranceEntry.setTransactionDebitCreditCode(Constants.GL_CREDIT_CODE);
                costShareEncumbranceEntry.setTransactionLedgerEntryAmount(scrubbedEntry.getTransactionLedgerEntryAmount().negated());
            }
        }

        costShareEncumbranceEntry.setTransactionDate(runDate);

        setCostShareObjectCode(costShareEncumbranceEntry, scrubbedEntry, errors);

        costShareEncumbranceEntry.setTransactionScrubberOffsetGenerationIndicator(true);
        createOutputEntry(costShareEncumbranceEntry, validGroup);
        scrubberReport.costShareEncumbranceGenerated();

        OriginEntry costShareEncumbranceOffsetEntry = new OriginEntry(costShareEncumbranceEntry);

        costShareEncumbranceOffsetEntry.setTransactionLedgerEntryDescription(kualiConfigurationService.getPropertyString(KeyConstants.MSG_GENERATED_OFFSET));

        OffsetDefinition offset = offsetDefinitionService.getByPrimaryId(
                costShareEncumbranceEntry.getUniversityFiscalYear(), 
                costShareEncumbranceEntry.getChartOfAccountsCode(),
                costShareEncumbranceEntry.getFinancialDocumentTypeCode(),
                costShareEncumbranceEntry.getFinancialBalanceTypeCode());

        if (offset != null) {
            costShareEncumbranceOffsetEntry.setFinancialObjectCode(offset.getFinancialObjectCode());
            costShareEncumbranceOffsetEntry.setFinancialObject(offset.getFinancialObject());

            if(offset.getFinancialSubObjectCode() == null) {
                costShareEncumbranceOffsetEntry.setFinancialSubObjectCode(Constants.DASHES_SUB_OBJECT_CODE);
            } else {
                costShareEncumbranceOffsetEntry.setFinancialSubObjectCode(offset.getFinancialSubObjectCode());
            }
        } else {
            StringBuffer offsetKey = new StringBuffer();
            offsetKey.append("Fiscal Year: ").append(costShareEncumbranceEntry.getUniversityFiscalYear());
            offsetKey.append("Chart of Accounts Code: ").append(costShareEncumbranceEntry.getChartOfAccountsCode());
            offsetKey.append("Document Type Code: ").append(costShareEncumbranceEntry.getFinancialDocumentTypeCode());
            offsetKey.append("Balance Type Code: ").append(costShareEncumbranceEntry.getFinancialBalanceTypeCode());

            addTransactionError(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_OFFSET_DEFINITION_NOT_FOUND),offsetKey.toString(),errors);
        }

        ObjectCode objectCode = objectCodeService.getByPrimaryId(
                costShareEncumbranceOffsetEntry.getUniversityFiscalYear(), 
                costShareEncumbranceOffsetEntry.getChartOfAccountsCode(),
                costShareEncumbranceOffsetEntry.getFinancialObjectCode());

        if (ifNullAddTransactionErrorAndReturnFalse(objectCode, errors,
                kualiConfigurationService.getPropertyString(KeyConstants.ERROR_NO_OBJECT_FOR_OBJECT_ON_OFSD), 
                costShareEncumbranceOffsetEntry.getFinancialObjectCode())) {

            costShareEncumbranceOffsetEntry.setFinancialObjectTypeCode(objectCode.getFinancialObjectTypeCode());

            if (costShareEncumbranceEntry.isCredit()) {
                costShareEncumbranceOffsetEntry.setTransactionDebitCreditCode(Constants.GL_DEBIT_CODE);
            } else {
                costShareEncumbranceOffsetEntry.setTransactionDebitCreditCode(Constants.GL_CREDIT_CODE);
            }
        }

        costShareEncumbranceOffsetEntry.setTransactionDate(runDate);
        costShareEncumbranceOffsetEntry.setOrganizationDocumentNumber(null);
        costShareEncumbranceOffsetEntry.setProjectCode(Constants.DASHES_PROJECT_CODE);
        costShareEncumbranceOffsetEntry.setOrganizationReferenceId(null);
        costShareEncumbranceOffsetEntry.setReferenceFinancialDocumentTypeCode(null);
        costShareEncumbranceOffsetEntry.setReferenceFinancialSystemOriginationCode(null);
        costShareEncumbranceOffsetEntry.setReferenceFinancialDocumentNumber(null);
        costShareEncumbranceOffsetEntry.setReversalDate(null);
        costShareEncumbranceOffsetEntry.setTransactionEncumbranceUpdateCode(null);

        costShareEncumbranceOffsetEntry.setTransactionScrubberOffsetGenerationIndicator(true);
        createOutputEntry(costShareEncumbranceOffsetEntry, validGroup);
        scrubberReport.costShareEncumbranceGenerated();

        return errors;
    }

    /**
     * @param costShareEntry
     */
    private void setCostShareObjectCode(OriginEntry costShareEntry, OriginEntry originEntry, List errorList) {

//      4480         SET-OBJECT-2004.

        persistenceService.retrieveReferenceObject(originEntry,"financialObject");

        // NOTE (laran) COST-SHARE-RECORD refers to the original entry. See line 4096 in the COBOL.
        // I disagree.  ALT-GLEN-RECORD is the scrubbed entry.  GLEN-RECORD is the origin entry

//        4481             MOVE FIN-OBJECT-CD OF COST-SHARE-RECORD
//        4482                  TO CAOBJT-FIN-OBJECT-CD.
//        4483             MOVE UNIV-FISCAL-YR OF COST-SHARE-RECORD
//        4484                  TO CAOBJT-UNIV-FISCAL-YR.
//        4485             MOVE FIN-COA-CD OF COST-SHARE-RECORD
//        4486                  TO CAOBJT-FIN-COA-CD.
//        4487             EXEC SQL
//        4488               SELECT  FIN_OBJ_LEVEL_CD
//        4489               INTO   :CAOBJT-FIN-OBJ-LEVEL-CD
//        4490               FROM   CA_OBJECT_CODE_T
//        4491               WHERE  UNIV_FISCAL_YR= RTRIM(:CAOBJT-UNIV-FISCAL-YR)
//        4492                 AND  FIN_COA_CD=     RTRIM(:CAOBJT-FIN-COA-CD)
//        4493                 AND  FIN_OBJECT_CD=  RTRIM(:CAOBJT-FIN-OBJECT-CD)
//        4494             END-EXEC.
//        4495             EVALUATE SQLCODE
//        4496               WHEN 0
//        4497                  CONTINUE
//        4498               WHEN +100
//        4499               WHEN +1403

        if( originEntry.getFinancialObject() == null) {

//        4500                  MOVE COST-SHARE-RECORD (1:51) TO RP-TABLE-KEY
//        4501                  MOVE SPACES TO RP-DATA-ERROR
//        4502                  MOVE 'OBJECT NOT ON OBJECT TABLE - SET OBJECT 2004'
//        4503                       TO RP-MSG-ERROR
//        4504                  PERFORM WRITE-ERROR-LINE THRU WRITE-ERROR-LINE-EXIT
            
            addTransactionError(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_OBJECT_CODE_NOT_FOUND), 
                    originEntry.getFinancialObjectCode(), errorList);

//        4505               WHEN OTHER
//        4506                  DISPLAY 'ERROR ACCESSING OBJECT TABLE AT SET-OBJECT-'
//        4507                          '2004 SQLCODE IS ' SQLCODE
//        4508                  MOVE 'Y' TO WS-FATAL-ERROR-FLAG
//        4509                  GO TO SET-OBJECT-2004-EXIT

        }

//      4510               END-EVALUATE.

        String originEntryObjectCode = originEntry.getFinancialObjectCode();
        String originEntryObjectLevelCode = (null == originEntry.getFinancialObject() ? "" : originEntry.getFinancialObject().getFinancialObjectLevelCode());

//      4511              IF CAOBJT-FIN-OBJ-LEVEL-CD = 'ACSA'
//      4512                 MOVE '9920' TO CAOBJT-FIN-OBJECT-CD
//      4513                 GO TO SET-OBJECT-VERIFY.

        if("ACSA".equals(originEntryObjectLevelCode)) {                     // ACADEMIC SALARIES
            originEntryObjectCode = "9920";                                      // TRSFRS_OF_FUNDS_ACAD_SAL

//      4514              IF CAOBJT-FIN-OBJ-LEVEL-CD = 'BASE'
//      4515                 MOVE '9959' TO CAOBJT-FIN-OBJECT-CD
//      4516                 GO TO SET-OBJECT-VERIFY.

        } else if("BASE".equals(originEntryObjectLevelCode)) {              // ASSESMENTS_EXPENDITURES
            originEntryObjectCode = "9959";                                      // TRANSFER_OUT_20_REALLOCATION

//      4517              IF CAOBJT-FIN-OBJ-LEVEL-CD = 'BENF'
//      4518                 IF CAOBJT-FIN-OBJECT-CD = '9956' OR
//      4519                    CAOBJT-FIN-OBJECT-CD < '5700'
//      4520                    MOVE '9956' TO CAOBJT-FIN-OBJECT-CD
//      4521                    GO TO SET-OBJECT-VERIFY.

        } else if("BENF".equals(originEntryObjectLevelCode) && ("9956".equals(originEntryObjectCode) || 5700 > Integer.valueOf(originEntryObjectCode).intValue())) { // BENEFITS
            originEntryObjectCode = "9956";                                      // TRSFRS_OF_FUNDS_FRINGE_BENF

//      4522              IF CAOBJT-FIN-OBJ-LEVEL-CD = 'BENF'
//      4523                 MOVE '9957' TO CAOBJT-FIN-OBJECT-CD
//      4524                 GO TO SET-OBJECT-VERIFY.

        } else if("BENF".equals(originEntryObjectLevelCode)) {              // BENEFITS
            originEntryObjectCode = "9957";                                      // TRSFRS_OF_FUNDS_RETIREMENT 

//      4525              IF CAOBJT-FIN-OBJ-LEVEL-CD = 'BISA'
//      4526                 MOVE '9925' TO CAOBJT-FIN-OBJECT-CD
//      4527                 GO TO SET-OBJECT-VERIFY.

        } else if("BISA".equals(originEntryObjectLevelCode)) {              // BI-WEEKLY_SALARY
            originEntryObjectCode = "9925";                                      // TRSFRS_OF_FUNDS_CLER_SAL 

//      4528              IF CAOBJT-FIN-OBJ-LEVEL-CD = 'CAP '
//      4529                 MOVE '9970' TO CAOBJT-FIN-OBJECT-CD
//      4530                 GO TO SET-OBJECT-VERIFY.

        } else if("CAP".equals(originEntryObjectLevelCode)) {               // CAPITAL_ASSETS
            originEntryObjectCode = "9970";                                      // TRSFRS_OF_FUNDS_CAPITAL  

//      4531              IF CAOBJT-FIN-OBJ-LEVEL-CD = 'CORE'
//      4532                 GO TO SET-OBJECT-VERIFY.

        } else if("CORE".equals(originEntryObjectLevelCode)) {              // ALLOTMENTS_AND_CHARGES_OUT
            // Do nothing

//      4533              IF CAOBJT-FIN-OBJ-LEVEL-CD = 'CORI'
//      4534                 GO TO SET-OBJECT-VERIFY.

        } else if("CORI".equals(originEntryObjectLevelCode)) {              // ALLOTMENTS_AND_CHARGES_IN
            // Do nothing

//      4535              IF CAOBJT-FIN-OBJ-LEVEL-CD = 'FINA'
//      4536                 IF CAOBJT-FIN-OBJECT-CD = '9954' OR '5400'
//      4537                    MOVE '9954' TO CAOBJT-FIN-OBJECT-CD
//      4538                    GO TO SET-OBJECT-VERIFY.

        } else if("FINA".equals(originEntryObjectLevelCode) && ("9954".equals(originEntryObjectCode) 
                        || "5400".equals(originEntryObjectCode))) {         // STUDENT_FINANCIAL_AID - TRSFRS_OF_FUNDS_FEE_REM  - GRADUATE_FEE_REMISSIONS
            originEntryObjectCode = "9954";                                      // TRSFRS_OF_FUNDS_CAPITAL  

//      4539              IF CAOBJT-FIN-OBJ-LEVEL-CD = 'FINA'
//      4540                 MOVE '9958' TO CAOBJT-FIN-OBJECT-CD
//      4541                 GO TO SET-OBJECT-VERIFY.

        } else if("FINA".equals(originEntryObjectLevelCode)) {              // STUDENT_FINANCIAL_AID
            originEntryObjectCode = "9958";                                      // TRSFRS_OF_FUNDS_FELL_AND_SCHO 

//      4542              IF CAOBJT-FIN-OBJ-LEVEL-CD = 'HRCO'
//      4543                 MOVE '9930' TO CAOBJT-FIN-OBJECT-CD
//      4544                 GO TO SET-OBJECT-VERIFY.

        } else if("HRCO".equals(originEntryObjectLevelCode)) {              // HOURLY_COMPENSATION
            originEntryObjectCode = "9930";                                      // TRSFRS_OF_FUNDS_WAGES 

//      4545              IF CAOBJT-FIN-OBJ-LEVEL-CD = 'ICOE'
//      4546                 MOVE '9955' TO CAOBJT-FIN-OBJECT-CD
//      4547                 GO TO SET-OBJECT-VERIFY.

        } else if("ICOE".equals(originEntryObjectLevelCode)) {              // INDIRECT_COST_RECOVERY_EXPENSE
            originEntryObjectCode = "9955";                                      // TRSFRS_OF_FUNDS_INDRCT_COST 

//      4548              IF CAOBJT-FIN-OBJ-LEVEL-CD = 'PART'
//      4549                 MOVE '9923' TO CAOBJT-FIN-OBJECT-CD
//      4550                 GO TO SET-OBJECT-VERIFY.

        } else if("PART".equals(originEntryObjectLevelCode)) {              // PART_TIME_INSTRUCTION_NON_STUDENT
            originEntryObjectCode = "9923";                                      // TRSFRS_OF_FUNDS_ACAD_ASSIST 

//      4551              IF CAOBJT-FIN-OBJ-LEVEL-CD = 'PRSA'
//      4552                 MOVE '9924' TO CAOBJT-FIN-OBJECT-CD
//      4553                 GO TO SET-OBJECT-VERIFY.

        } else if("PRSA".equals(originEntryObjectLevelCode)) {              // PROFESSIONAL_SALARIES
            originEntryObjectCode = "9924";                                      // TRSF_OF_FUNDS_PROF_SAL 

//      4554              IF CAOBJT-FIN-OBJ-LEVEL-CD = 'RESV'
//      4555                 MOVE '9979' TO CAOBJT-FIN-OBJECT-CD
//      4556                 GO TO SET-OBJECT-VERIFY.

        } else if("RESV".equals(originEntryObjectLevelCode)) {              // RESERVES
            originEntryObjectCode = "9979";                                      // TRSFRS_OF_FUNDS_UNAPP_BAL

//      4557              IF CAOBJT-FIN-OBJ-LEVEL-CD = 'SAAP'
//      4558                 MOVE '9923' TO CAOBJT-FIN-OBJECT-CD
//      4559                 GO TO SET-OBJECT-VERIFY.

        } else if("SAAP".equals(originEntryObjectLevelCode)) {              // SALARY_ACCRUAL_EXPENSE
            originEntryObjectCode = "9923";                                      // TRSFRS_OF_FUNDS_ACAD_ASSIST

//      4560              IF CAOBJT-FIN-OBJ-LEVEL-CD = 'TRAN'
//      4561                 MOVE '9959' TO CAOBJT-FIN-OBJECT-CD
//      4562                 GO TO SET-OBJECT-VERIFY.

        } else if("TRAN".equals(originEntryObjectLevelCode)) {              // TRANSFER_EXPENSE
            originEntryObjectCode = "9959";                                      // TRANSFER_OUT_20_REALLOCATION

//      4563              IF CAOBJT-FIN-OBJ-LEVEL-CD = 'TRAV'
//      4564                 MOVE '9960' TO CAOBJT-FIN-OBJECT-CD
//      4565                 GO TO SET-OBJECT-VERIFY.

        } else if("TRAV".equals(originEntryObjectLevelCode)) {              // TRAVEL
            originEntryObjectCode = "9960";                                      // TRSFRS_OF_FUNDS_TRAVEL

//      4566              IF CAOBJT-FIN-OBJ-LEVEL-CD = 'TREX'
//      4567                 MOVE '9959' TO CAOBJT-FIN-OBJECT-CD
//      4568                 GO TO SET-OBJECT-VERIFY.

        } else if("TREX".equals(originEntryObjectLevelCode)) {              // TRANSFER_5199_EXPENSE
            originEntryObjectCode = "9959";                                      // TRANSFER_OUT_20_REALLOCATION

//      4569              IF CAOBJT-FIN-OBJ-LEVEL-CD = 'TRIN'
//      4570                 MOVE '9915' TO CAOBJT-FIN-OBJECT-CD
//      4571                 GO TO SET-OBJECT-VERIFY.

        } else if("TRIN".equals(originEntryObjectLevelCode)) {              // TRANSFER_1699_INCOME
            originEntryObjectCode = "9915";                                      // TRSFRS_OF_FUNDS_REVENUE  

//      4572              MOVE '9940' TO CAOBJT-FIN-OBJECT-CD.

        } else {
            originEntryObjectCode = "9940";                                      // TRSFRS_OF_FUNDS_SUP_AND_EXP 
        }

//        4573         SET-OBJECT-VERIFY.
//        4574              EXEC SQL
//        4575                  SELECT   FIN_OBJ_TYP_CD
//        4576                  INTO     :CAOBJT-FIN-OBJ-TYP-CD :CAOBJT-FOTC-I
//        4577                  FROM     CA_OBJECT_CODE_T
//        4578                  WHERE    UNIV_FISCAL_YR= RTRIM(:CAOBJT-UNIV-FISCAL-YR)
//        4579                    AND    FIN_COA_CD    = RTRIM(:CAOBJT-FIN-COA-CD)
//        4580                    AND    FIN_OBJECT_CD = RTRIM(:CAOBJT-FIN-OBJECT-CD)
//        4581              END-EXEC.

        // Lookup the new object code
        ObjectCode objectCode = objectCodeService.getByPrimaryId(costShareEntry.getUniversityFiscalYear(),costShareEntry.getChartOfAccountsCode(),originEntryObjectCode);

//        4582              IF CAOBJT-FOTC-I < ZERO
//        4583                 MOVE SPACES TO CAOBJT-FIN-OBJ-TYP-CD.
//        4584              EVALUATE SQLCODE
//        4585                 WHEN 0

        if( objectCode != null) {

//        4586                    MOVE CAOBJT-FIN-OBJ-TYP-CD TO FIN-OBJ-TYP-CD
//        4587                         OF ALT-GLEN-RECORD
//        4588                    MOVE CAOBJT-FIN-OBJECT-CD TO FIN-OBJECT-CD
//        4589                         OF ALT-GLEN-RECORD

            costShareEntry.setFinancialObjectTypeCode(objectCode.getFinancialObjectTypeCode());
            costShareEntry.setFinancialObjectCode(originEntryObjectCode);

//        4590                 WHEN +100
//        4591                 WHEN +1403

        } else {

//        4592                    MOVE COST-SHARE-RECORD (1:51) TO RP-TABLE-KEY
//        4593                    MOVE SPACES TO RP-DATA-ERROR
//        4594                    MOVE 'ERROR DETERMINING COST SHARE OBJECT '
//        4595                        TO RP-MSG-ERROR
//        4596                    PERFORM WRITE-ERROR-LINE THRU WRITE-ERROR-LINE-EXIT
//        4597                    MOVE SPACES TO DCLCA-OBJECT-CODE-T

            addTransactionError(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_COST_SHARE_OBJECT_NOT_FOUND),costShareEntry.getFinancialObjectCode(), errorList);

//        4598                 WHEN OTHER
//        4599                    DISPLAY 'ERROR ACCESSING OBJECT TABLE TO GET TYPE FOR'
//        4600                            ' COST SHARE'
//        4601                            ' SQL CODE IS ' SQLCODE
//        4602                    MOVE 'Y' TO WS-FATAL-ERROR-FLAG
//        4603               END-EVALUATE.
//        4604         SET-OBJECT-2004-EXIT.
//        4605             EXIT.        

        }
    }

    /**
     * 3000-Offset.
     * 
     * The purpose of this method is to build the actual offset transaction.
     * It does this by performing the following steps:
     * 1. Getting the offset object code and offset subobject code from the
     *    GL Offset Definition Table.
     * 2. For the offset object code it needs to get the associated object type,
     *    object subtype, and object active code.
     *    
     * @param scrubbedEntry
     */
    private List generateOffset(OriginEntry scrubbedEntry) {
        LOG.debug("generateOffset() started");

        // Temporary storage for any errors.
        ArrayList errors = new ArrayList();

        // There was no previous unit of work so we need no offset
        if ( scrubbedEntry == null ) {
            return errors;
        }

        // If the offset amount is zero, don't bother to lookup the offset definition ...
        // NOTE (laran) this is a rule that Sterling suggested.
        if(unitOfWork.offsetAmount.isZero()) {
            return errors;
        }
        // do nothing if scrubber offset indicator of the ducument type code is turned off in the document type table

        String documentTypeCode = workingEntry.getFinancialDocumentTypeCode(); 
        DocumentType documentType = documentTypeService.getDocumentTypeByCode(documentTypeCode);
        if ( ! documentType.isTransactionScrubberOffsetGenerationIndicator() ) {
            return errors;
        }

        OriginEntry offsetEntry = new OriginEntry(workingEntry);

//        3970  029760 3000-OFFSET.
//        3971  029840     MOVE OFFSET-DESCRIPTION
//        3972  029850       TO TRN-LDGR-ENTR-DESC OF ALT-GLEN-RECORD.

        offsetEntry.setTransactionLedgerEntryDescription(kualiConfigurationService.getPropertyString(KeyConstants.MSG_GENERATED_OFFSET));

//        3973  029890     MOVE UNIV-FISCAL-YR     OF ALT-GLEN-RECORD
//        3974  029900       TO GLOFSD-UNIV-FISCAL-YR.
//        3975  029910     MOVE FIN-COA-CD         OF ALT-GLEN-RECORD
//        3976  029920       TO GLOFSD-FIN-COA-CD.
//        3977  029930     MOVE FDOC-TYP-CD        OF ALT-GLEN-RECORD
//        3978  029940       TO GLOFSD-FDOC-TYP-CD.
//        3979  029950     MOVE FIN-BALANCE-TYP-CD OF ALT-GLEN-RECORD
//        3980  029960       TO GLOFSD-FIN-BALANCE-TYP-CD.
//        3981  029970     EXEC SQL
//        3982  029980          SELECT FIN_OBJECT_CD,
//        3983  029990                 FIN_SUB_OBJ_CD
//        3984  030000          INTO   :GLOFSD-FIN-OBJECT-CD :GLOFSD-FOC-I,
//        3985  030010                 :GLOFSD-FIN-SUB-OBJ-CD :GLOFSD-FSOC-I
//        3986  030020          FROM   GL_OFFSET_DEFN_T
//        3987  030030       WHERE  UNIV_FISCAL_YR = RTRIM(:GLOFSD-UNIV-FISCAL-YR)
//        3988  030040        AND  FIN_COA_CD = RTRIM(:GLOFSD-FIN-COA-CD)
//        3989  030050        AND  FDOC_TYP_CD = RTRIM(:GLOFSD-FDOC-TYP-CD)
//        3990  030060        AND  FIN_BALANCE_TYP_CD
//        3991  030070             = RTRIM(:GLOFSD-FIN-BALANCE-TYP-CD)
//        3992  030080     END-EXEC

        // ... otherwise lookup the offset definition appropriate for this entry.
        // We need the offset object code from it.

        OffsetDefinition offsetDefinition = offsetDefinitionService.getByPrimaryId(scrubbedEntry.getUniversityFiscalYear(),
            scrubbedEntry.getChartOfAccountsCode(),scrubbedEntry.getFinancialDocumentTypeCode(),scrubbedEntry.getFinancialBalanceTypeCode());

//        3993             IF GLOFSD-FOC-I < ZERO
//        3994                MOVE SPACES TO GLOFSD-FIN-OBJECT-CD
//        3995             END-IF
//        3996             IF GLOFSD-FSOC-I < ZERO
//        3997                MOVE SPACES TO GLOFSD-FIN-SUB-OBJ-CD
//        3998             END-IF
//        3999  030090     EVALUATE SQLCODE
//        4000  030100          WHEN 0

        if (offsetDefinition != null) {

            // Flexible Offset Enhancement
            List errorsOfFlexibleOffsetGeneration = this.applyFlexibleOffsetGeneration(offsetEntry);
            if(errorsOfFlexibleOffsetGeneration.size() > 0){
                return errorsOfFlexibleOffsetGeneration;
            }

//        4001  030110              MOVE GLOFSD-FIN-OBJECT-CD TO
//        4002  030120                   FIN-OBJECT-CD OF ALT-GLEN-RECORD

            offsetEntry.setFinancialObject(offsetDefinition.getFinancialObject());
            offsetEntry.setFinancialObjectCode(offsetDefinition.getFinancialObjectCode());

//        4003  030130              IF GLOFSD-FIN-SUB-OBJ-CD = SPACES
//        4004  030140                   MOVE FIN-SUB-OBJ-CD-DASHES
//        4005  030150                       TO FIN-SUB-OBJ-CD OF ALT-GLEN-RECORD

            if (! StringUtils.hasText(offsetDefinition.getFinancialSubObjectCode())) {
                offsetEntry.setFinancialSubObject(null);
                offsetEntry.setFinancialSubObjectCode(Constants.DASHES_SUB_OBJECT_CODE);

//        4006  030160              ELSE
//        4007  030170                   MOVE GLOFSD-FIN-SUB-OBJ-CD TO
//        4008  030180                          FIN-SUB-OBJ-CD OF ALT-GLEN-RECORD
//        4009  030190              END-IF

            } else {
                offsetEntry.setFinancialSubObject(offsetDefinition.getFinancialSubObject());
                offsetEntry.setFinancialSubObjectCode(offsetDefinition.getFinancialSubObjectCode());
            }
        } else {

//        4010  030200          WHEN +100
//        4011  030210          WHEN +1403
//        4012  030220              MOVE ALT-GLEN-RECORD (1:51) TO RP-TABLE-KEY
//        4013                      MOVE SPACES TO RP-DATA-ERROR
//        4014  030230              MOVE 'OFFSET DEFINITION NOT FOUND' TO RP-MSG-ERROR
//        4015  030240              PERFORM WRITE-ERROR-LINE THRU WRITE-ERROR-LINE-EXIT
//        4016  030250              MOVE SPACES TO DCLGL-OFFSET-DEFN-T
//        4017  030260          WHEN OTHER
//        4018  030270              DISPLAY ' ERROR ACCESSING OFSD TABLE '
//        4019  030280                      'SQL CODE IS ' SQLCODE
//        4020  030290              MOVE 'Y' TO WS-FATAL-ERROR-FLAG
//        4021  030300              GO TO 2000-ENTRY-EXIT

            StringBuffer sb = new StringBuffer();
            sb.append("Fiscal Year: ").append(scrubbedEntry.getUniversityFiscalYear());
            sb.append(", Chart of Accounts: ").append(scrubbedEntry.getChartOfAccountsCode());
            sb.append(", Document Type: ").append(scrubbedEntry.getFinancialDocumentTypeCode());
            sb.append(", Balance Type: ").append(scrubbedEntry.getFinancialBalanceTypeCode());

            addTransactionError(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_OFFSET_DEFINITION_NOT_FOUND),
                    sb.toString(), errors);

//          4022  030310     END-EVALUATE
        }

//        4023  030360     MOVE UNIV-FISCAL-YR OF ALT-GLEN-RECORD
//        4024  030370       TO CAOBJT-UNIV-FISCAL-YR
//        4025  030380     MOVE FIN-COA-CD     OF ALT-GLEN-RECORD
//        4026  030390       TO CAOBJT-FIN-COA-CD
//        4027  030400     MOVE FIN-OBJECT-CD  OF ALT-GLEN-RECORD
//        4028  030410       TO CAOBJT-FIN-OBJECT-CD
//        4029  030420     EXEC SQL
//        4030  030430       SELECT    FIN_OBJ_TYP_CD,
//        4031  030440                 FIN_OBJ_SUB_TYP_CD,
//        4032  030450                 FIN_OBJ_ACTIVE_CD,
//        4033  030460                 FOBJ_MNXFR_ELIM_CD
//        4034  030470       INTO      :CAOBJT-FIN-OBJ-TYP-CD :CAOBJT-FOTC-I,
//        4035  030480                 :CAOBJT-FIN-OBJ-SUB-TYP-CD :CAOBJT-FOSTC-I,
//        4036  030490                 :CAOBJT-FIN-OBJ-ACTIVE-CD :CAOBJT-FOAC-I,
//        4037  030500                 :CAOBJT-FOBJ-MNXFR-ELIM-CD :CAOBJT-FMEC-I
//        4038  030510       FROM      CA_OBJECT_CODE_T
//        4039  030520       WHERE     UNIV_FISCAL_YR= RTRIM(:CAOBJT-UNIV-FISCAL-YR)
//        4040  030530         AND     FIN_COA_CD=     RTRIM(:CAOBJT-FIN-COA-CD)
//        4041  030540         AND     FIN_OBJECT_CD=  RTRIM(:CAOBJT-FIN-OBJECT-CD)
//        4042  030550     END-EXEC

        if (offsetEntry.getFinancialObject() == null) {
            persistenceService.retrieveReferenceObject(offsetEntry, "financialObject");
        }

//        4043              IF CAOBJT-FOTC-I < ZERO
//        4044                 MOVE SPACES TO CAOBJT-FIN-OBJ-TYP-CD
//        4045              END-IF
//        4046              IF CAOBJT-FOSTC-I < ZERO
//        4047                 MOVE SPACES TO CAOBJT-FIN-OBJ-SUB-TYP-CD
//        4048              END-IF
//        4049              IF CAOBJT-FOAC-I < ZERO
//        4050                 MOVE SPACES TO CAOBJT-FIN-OBJ-ACTIVE-CD
//        4051              END-IF
//        4052              IF CAOBJT-FMEC-I < ZERO
//        4053                 MOVE SPACES TO CAOBJT-FOBJ-MNXFR-ELIM-CD
//        4054              END-IF
//        4055  030560        EVALUATE SQLCODE
//        4056  030570           WHEN 0
//        4057  030580            MOVE CAOBJT-FIN-OBJ-TYP-CD TO FIN-OBJ-TYP-CD
//        4058  030590              OF ALT-GLEN-RECORD

        if (null != offsetEntry.getFinancialObject()) {
            offsetEntry.setFinancialObjectTypeCode(offsetEntry.getFinancialObject().getFinancialObjectTypeCode());
        } else {

//        4059  030600           WHEN +100
//        4060  030610           WHEN +1403
//        4061  030620              MOVE GLEN-RECORD (1:51) TO RP-TABLE-KEY
//        4062  030630              MOVE CAOBJT-FIN-OBJECT-CD TO RP-DATA-ERROR
//        4063  030640                   FIN-OBJECT-CD OF ALT-GLEN-RECORD
//        4064  030650              MOVE 'NO OBJECT FOR OBJECT ON OFSD' TO RP-MSG-ERROR
//        4065  030660              PERFORM WRITE-ERROR-LINE THRU WRITE-ERROR-LINE-EXIT
//        4066  030670              MOVE SPACES TO DCLCA-OBJECT-CODE-T
//        4067  030680           WHEN OTHER
//        4068  030690               DISPLAY 'ERROR ACCESSING OBJECT TABLE'
//        4069  030700                       ' SQL CODE IS ' SQLCODE
//        4070  030710               MOVE 'Y' TO WS-FATAL-ERROR-FLAG
//        4071  030720               GO TO 2000-ENTRY-EXIT
//        4072  030730        END-EVALUATE

            addTransactionError(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_OFFSET_DEFINITION_OBJECT_CODE_NOT_FOUND),
                    scrubbedEntry.getFinancialObjectCode(), errors);
        }

//        4073  030780     MOVE SCRB-OFFSET-AMOUNT
//        4074  030790       TO TRN-LDGR-ENTR-AMT OF ALT-GLEN-RECORD.
//        4075  030800

        offsetEntry.setTransactionLedgerEntryAmount(unitOfWork.offsetAmount);

//        4076  030810     IF SCRB-OFFSET-AMOUNT > ZEROES

        if (unitOfWork.offsetAmount.isPositive()) {

//        4077  030820        MOVE CREDIT
//        4078  030830          TO TRN-DEBIT-CRDT-CD OF ALT-GLEN-RECORD

            offsetEntry.setTransactionDebitCreditCode(Constants.GL_CREDIT_CODE);

//        4079  030840     ELSE

        } else {

//        4080  030850        MOVE DEBIT
//        4081  030860          TO TRN-DEBIT-CRDT-CD OF ALT-GLEN-RECORD

            offsetEntry.setTransactionDebitCreditCode(Constants.GL_DEBIT_CODE);

//        4082  030870        COMPUTE TRN-LDGR-ENTR-AMT OF ALT-GLEN-RECORD =
//        4083  030880                SCRB-OFFSET-AMOUNT * NEGATIVE-ONE.

            offsetEntry.setTransactionLedgerEntryAmount(unitOfWork.offsetAmount.negated());
        }

//        4084  030940     MOVE SPACES TO ORG-DOC-NBR       OF ALT-GLEN-RECORD
//        4085  030950                    ORG-REFERENCE-ID  OF ALT-GLEN-RECORD
//        4086  030960                    FDOC-REF-TYP-CD   OF ALT-GLEN-RECORD
//        4087  030970                    FS-REF-ORIGIN-CD  OF ALT-GLEN-RECORD
//        4088  030980                    FDOC-REF-NBR      OF ALT-GLEN-RECORD
//        4089  030990                    TRN-ENCUM-UPDT-CD OF ALT-GLEN-RECORD.

        offsetEntry.setOrganizationDocumentNumber(null);
        offsetEntry.setOrganizationReferenceId(null);
        offsetEntry.setReferenceFinancialDocumentTypeCode(null);
        offsetEntry.setReferenceDocumentType(null);
        offsetEntry.setReferenceFinancialSystemOriginationCode(null);
        offsetEntry.setReferenceFinancialDocumentNumber(null);
        offsetEntry.setTransactionEncumbranceUpdateCode(null);

//        4090  031010     MOVE PROJECT-CD-DASHES TO PROJECT-CD OF ALT-GLEN-RECORD.

        offsetEntry.setProjectCode(Constants.DASHES_PROJECT_CODE);

//        4091  031060     MOVE SCRB-TODAYS-DATE
//        4092  031070       TO TRANSACTION-DT OF ALT-GLEN-RECORD.

        offsetEntry.setTransactionDate(runDate);

        if (errors.size() > 0) {
            // TODO Is this right?
            scrubberReportErrors.put(offsetEntry, errors);
            createOutputEntry(offsetEntry, errorGroup);
            scrubberReport.errorRecordWritten();
        } else {
            offsetEntry.setTransactionScrubberOffsetGenerationIndicator(true);
            createOutputEntry(offsetEntry, validGroup);
            scrubberReport.offsetEntryGenerated();
        }
        return errors;
    }

    /**
     * This method determines if the flexible offsets need to be generated against the given transaction entry. 
     * If any, replace the chart and account with the offset chart of accounts code and offset account number from 
     * the offset table (FP_ODST_ACC_T). Then, pass the control to the module of the ordinary offset generation.
     * 
     * @param originEntry the transaction entry being processed
     */
    private List applyFlexibleOffsetGeneration(OriginEntry originEntry) {
        List errors = new ArrayList();
        String keyOfErrorMessage = "";
        StringBuffer errorValues = new StringBuffer();

        Integer fiscalYear = originEntry.getUniversityFiscalYear();
        String chartOfAccountsCode = originEntry.getChartOfAccountsCode();
        String accountNumber = originEntry.getAccountNumber();

        String balanceTypeCode = originEntry.getFinancialBalanceTypeCode();
        String documentTypeCode = originEntry.getFinancialDocumentTypeCode();

        // do nothing if scrubber offset indicator is turned off in the document type table
        DocumentType documentType = documentTypeService.getDocumentTypeByCode(documentTypeCode);
        if ( ! documentType.isTransactionScrubberOffsetGenerationIndicator() ) {
            return errors;
        }

        // do nothing if the global flexible offset indicator is off
        if ( ! flexibleOffsetAccountService.getEnabled() ) {
            return errors;
        }

        // look up the offset definition table for the offset object code
        OffsetDefinition offsetDefinition = offsetDefinitionService.getByPrimaryId(
                fiscalYear, chartOfAccountsCode, documentTypeCode, balanceTypeCode);
        if (offsetDefinition == null) {
            errorValues.append("fiscal year = " + fiscalYear + "; ");
            errorValues.append("chart of account code = " + chartOfAccountsCode + "; ");
            errorValues.append("document type code = " + documentTypeCode + "; ");
            errorValues.append("balance type code = " + balanceTypeCode + "; ");
            
            keyOfErrorMessage = kualiConfigurationService.getPropertyString(KeyConstants.ERROR_OFFSET_DEFINITION_NOT_FOUND);
            addTransactionError(keyOfErrorMessage, errorValues.toString(), errors);
            return errors;
        }

        // get the offset object code from the offset definition record searched above
        String offsetObjectCode = offsetDefinition.getFinancialObjectCode();
        if (offsetObjectCode == null) {
            errorValues.append("offset object code = " + offsetObjectCode + "; ");
            keyOfErrorMessage = kualiConfigurationService.getPropertyString(KeyConstants.ERROR_OFFSET_DEFINITION_OBJECT_CODE_NOT_FOUND);
            addTransactionError(keyOfErrorMessage, "", errors);
            return errors;
        }

        // do nothing if there is no the offset account with the given chart of accounts code, 
        // account number and offset object code in the offset table.
        OffsetAccount offsetAccount = flexibleOffsetAccountService.getByPrimaryIdIfEnabled(
                chartOfAccountsCode, accountNumber, offsetObjectCode);
        if (offsetAccount == null) {
            errorValues.append("chart of account code = " + chartOfAccountsCode + "; ");
            errorValues.append("account number = " + accountNumber + "; ");
            errorValues.append("offset object code = " + offsetObjectCode + "; ");           
            
            keyOfErrorMessage = kualiConfigurationService.getPropertyString(KeyConstants.ERROR_OFFSET_ACCOUNT_NOT_FOUND);
            addTransactionError(keyOfErrorMessage, errorValues.toString(), errors);
            return errors;
        }
        String offserAccountNumber = offsetAccount.getFinancialOffsetAccountNumber();
        String offsetChartOfAccountsCode = offsetAccount.getFinancialOffsetChartOfAccountCode();

        // validate if there is a record with the offset chart and offset object code in the object code table 
        if ( ! chartOfAccountsCode.equals(offsetChartOfAccountsCode)) {
            ObjectCode objectCode = objectCodeService.getByPrimaryId(fiscalYear, offsetChartOfAccountsCode, offsetObjectCode);          
            if(objectCode == null){
                errorValues.append("chart of account code = " + chartOfAccountsCode + "; ");
                errorValues.append("object code = " + offsetObjectCode + "; ");   

                keyOfErrorMessage = kualiConfigurationService.getPropertyString(KeyConstants.ERROR_OBJECT_CODE_NOT_FOUND);
                addTransactionError(keyOfErrorMessage, errorValues.toString(), errors);
                return errors;
        }
        }

        // replace the chart and account of the given transaction with those of the offset account obtained above
        originEntry.setAccountNumber(offserAccountNumber);
        originEntry.setChartOfAccountsCode(offsetChartOfAccountsCode);
        
        // blank out the sub account and sub object since the account has been replaced
        originEntry.setSubAccountNumber(Constants.DASHES_SUB_ACCOUNT_NUMBER);
        originEntry.setFinancialSubObjectCode(Constants.DASHES_SUB_OBJECT_CODE);

        // indicate that the entry is a flexible offset with the meaningful description
        String description = kualiConfigurationService.getPropertyString(KeyConstants.MSG_GENERATED_FLEXIBLE_OFFSET);
        originEntry.setTransactionLedgerEntryDescription(description);

        return errors;
    }

    private void checkForNullKeys(OriginEntry entry) {
        if(null != entry) {
            if(null == entry.getFinancialDocumentTypeCode()) {
                LOG.debug("DocumentTypeCode is NULL for: " + entry.toString());
            }
            if(null == entry.getFinancialSystemOriginationCode()) {
                LOG.debug("OriginationCode is NULL for: " + entry.toString());
            }
            if(null == entry.getFinancialDocumentNumber()) {
                LOG.debug("DocumentNumber is NULL for: " + entry.toString());
            }
            if(null == entry.getChartOfAccountsCode()) {
                LOG.debug("ChartCode is NULL for: " + entry.toString());
            }
            if(null == entry.getAccountNumber()) {
                LOG.debug("AccountNumber is NULL for: " + entry.toString());
            }
            if(null == entry.getSubAccountNumber()) {
                LOG.debug("SubAccountNumber is NULL for: " + entry.toString());
            }
            if(null == entry.getFinancialBalanceTypeCode()) {
                LOG.debug("BalanceTypeCode is NULL for: " + entry.toString());
            }
            if(null == entry.getUniversityFiscalYear()) {
                LOG.debug("UniversityFiscalYear is NULL for: " + entry.toString());
            }
            if(null == entry.getUniversityFiscalPeriodCode()) {
                LOG.debug("UniversityFiscalPeriodCode is NULL for: " + entry.toString());
            }
        }
    }

    private void createOutputEntry(OriginEntry entry, OriginEntryGroup group) {
        // Guard for KULGL-54. Just logs cases in which primary key fields happen to be null for
        // an entry being written to ScrubberValid.
        if ( group.getValid().booleanValue() ) {
            checkForNullKeys(entry);
        }
        entry.setGroup(group);
        originEntryService.save(entry);
    }

    /**
     * 
     * @param glObject
     * @param errorMessage
     * @return
     */
    private boolean ifNullAddTransactionErrorAndReturnFalse(Object glObject, List errors, String errorMessage, String errorValue) {
        if (glObject == null) {
            if (StringUtils.hasText(errorMessage)) {
                addTransactionError(errorMessage, errorValue, errors);
            } else {
                addTransactionError("Unexpected null object", glObject.getClass().getName(), errors);
            }
            return false;
        }
        return true;
    }

    /**
     * @param errorMessage
     */
    private void addTransactionError(String errorMessage, String errorValue, List errors) {
        errors.add(errorMessage + " (" + errorValue + ")");
    }
}
