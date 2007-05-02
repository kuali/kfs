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
package org.kuali.module.labor.service.impl;

import java.math.BigDecimal;
import java.sql.Date;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.bo.DocumentType;
import org.kuali.core.bo.FinancialSystemParameter;
import org.kuali.core.rule.KualiParameterRule;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.DocumentTypeService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.PersistenceService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.bo.OriginationCode;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.AccountingPeriod;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.module.chart.bo.SubObjCd;
import org.kuali.module.chart.service.ObjectCodeService;
import org.kuali.module.chart.service.OffsetDefinitionService;
import org.kuali.module.financial.service.FlexibleOffsetAccountService;
import org.kuali.module.gl.GLConstants;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.OriginEntrySource;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.bo.UniversityDate;
import org.kuali.module.gl.dao.UniversityDateDao;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.gl.service.ReportService;
import org.kuali.module.gl.service.ScrubberValidator;
//TODO: should be changed to RaborScrubberReportData
import org.kuali.module.gl.service.impl.scrubber.ScrubberReportData;
import org.kuali.module.gl.util.Message;
import org.kuali.module.gl.util.ObjectHelper;
import org.kuali.module.labor.bo.LaborOriginEntry;
import org.kuali.module.labor.service.LaborOriginEntryService;
import org.kuali.module.labor.service.LaborReportService;
import org.kuali.module.labor.util.ReportRegistry;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.util.StringUtils;

/**
 * This class has the logic for the scrubber. It is required because the scrubber process needs instance variables. Instance
 * variables in a spring service are shared between all code calling the service. This will make sure each run of the scrubber has
 * it's own instance variables instead of being shared.
 * 
 * 
 */
public class LaborScrubberProcess {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborScrubberProcess.class);

    // 40 spaces - used for filling in descriptions with spaces
    private static String SPACES = "                                        ";

    /* Services required */
    private FlexibleOffsetAccountService flexibleOffsetAccountService;
    private DocumentTypeService documentTypeService;
    private BeanFactory beanFactory;
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

    private Map<String, FinancialSystemParameter> parameters;
    private Map<String, KualiParameterRule> rules;

    /* These are all different forms of the run date for this job */
    private Date runDate;
    private Calendar runCal;
    private UniversityDate universityRunDate;
    private String offsetString;

    /* These fields are used to control whether the job was run before some set time,
     * if so, the rundate of the job will be set to 11:59 PM of the previous day
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
    private boolean reportOnlyMode;

    /**
     * These parameters are all the dependencies.
     */
    public LaborScrubberProcess(FlexibleOffsetAccountService flexibleOffsetAccountService, DocumentTypeService documentTypeService, BeanFactory beanFactory, LaborOriginEntryService laborOriginEntryService, OriginEntryGroupService originEntryGroupService, DateTimeService dateTimeService, OffsetDefinitionService offsetDefinitionService, ObjectCodeService objectCodeService, KualiConfigurationService kualiConfigurationService, UniversityDateDao universityDateDao, PersistenceService persistenceService, LaborReportService laborReportService, ScrubberValidator scrubberValidator) {
        super();
        this.flexibleOffsetAccountService = flexibleOffsetAccountService;
        this.documentTypeService = documentTypeService;
        this.beanFactory = beanFactory;
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

        parameters = kualiConfigurationService.getParametersByGroup(GLConstants.GL_SCRUBBER_GROUP);
        rules = kualiConfigurationService.getRulesByGroup(GLConstants.GL_SCRUBBER_GROUP);
        
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
    public void scrubGroupReportOnly(OriginEntryGroup group,String documentNumber) {
        LOG.debug("scrubGroupReportOnly() started");

        scrubEntries(group,documentNumber);
    }

    public void scrubEntries() {
        scrubEntries(null,null);
    }

    /**
     * Scrub all entries that need it in origin entry. Put valid scrubbed entries in a scrubber valid group, put errors in a
     * scrubber error group, and transactions with an expired account in the scrubber expired account group.
     */
    public void scrubEntries(OriginEntryGroup group,String documentNumber) {
        LOG.debug("scrubEntries() started");

        // We are in report only mode if we pass a group to this method.
        // if not, we are in batch mode and we scrub the backup group
        reportOnlyMode = (group != null);
        
        //get reportDirectory
        String reportsDirectory = ReportRegistry.getReportsDirectory();

        scrubberReportErrors = new HashMap<Transaction, List<Message>>();

        // setup an object to hold the "default" date information
        runDate = calculateRunDate(dateTimeService.getCurrentDate());
        runCal = Calendar.getInstance();
        runCal.setTime(runDate);

        universityRunDate = universityDateDao.getByPrimaryKey(runDate);
        if (universityRunDate == null) {
            throw new IllegalStateException(kualiConfigurationService.getPropertyString(KFSKeyConstants.ERROR_UNIV_DATE_NOT_FOUND));
        }

        setOffsetString();
        setDescriptions();

        // Create the groups that will store the valid and error entries that come out of the scrubber
        // We don't need groups for the reportOnlyMode
        if (!reportOnlyMode) {
            validGroup = originEntryGroupService.createGroup(runDate, OriginEntrySource.LABOR_SCRUBBER_VALID, true, true, false);
            errorGroup = originEntryGroupService.createGroup(runDate, OriginEntrySource.LABOR_SCRUBBER_ERROR, false, true, false);
            expiredGroup = originEntryGroupService.createGroup(runDate, OriginEntrySource.LABOR_SCRUBBER_EXPIRED, false, true, false);
        }

        // get the origin entry groups to be processed by Scrubber
        Collection groupsToScrub = null;
        if (reportOnlyMode) {
            groupsToScrub = new ArrayList();
            groupsToScrub.add(group);
        }
        else {
            groupsToScrub = originEntryGroupService.getLaborBackupGroups(runDate);
        }
        LOG.debug("scrubEntries() number of groups to scrub: " + groupsToScrub.size());

        // generate the reports based on the origin entries to be processed by scrubber
        if (reportOnlyMode) {
            laborReportService.generateScrubberLedgerSummaryReportOnline(group,documentNumber, reportsDirectory, runDate);
        } else {
            laborReportService.generateScrubberLedgerSummaryReportBatch(groupsToScrub, reportsDirectory, runDate);
        }

        // Scrub all of the OriginEntryGroups waiting to be scrubbed as of runDate.
        scrubberReport = new ScrubberReportData();
        for (Iterator iteratorOverGroups = groupsToScrub.iterator(); iteratorOverGroups.hasNext();) {
            OriginEntryGroup originEntryGroup = (OriginEntryGroup) iteratorOverGroups.next();
            LOG.debug("scrubEntries() Scrubbing group " + originEntryGroup.getId());

            processGroup(originEntryGroup);

            if (!reportOnlyMode) {
                // Mark the origin entry group as being processed ...
                originEntryGroup.setProcess(Boolean.FALSE);

                // ... and save the origin entry group with the new process flag.
                originEntryGroupService.save(originEntryGroup);
            }
        }

        // generate the scrubber status summary report
        if (reportOnlyMode) {
            laborReportService.generateOnlineScrubberStatisticsReport(group.getId(), scrubberReport, scrubberReportErrors,documentNumber, reportsDirectory, runDate);
        }
        else {
            laborReportService.generateBatchScrubberStatisticsReport(scrubberReport, scrubberReportErrors, reportsDirectory, runDate);
        }

        // run the demerger
        
       //TODO: Check
       /* if (!reportOnlyMode) {
            performDemerger(errorGroup, validGroup);
        }
*/
        // Run the reports
        if ( reportOnlyMode ) {
            // Run transaction list
            laborReportService.generateScrubberTransactionsOnline(group,documentNumber, reportsDirectory, runDate);
        } else {
            // Run bad balance type report and removed transaction report
            laborReportService.generateScrubberBadBalanceTypeListingReport(groupsToScrub, reportsDirectory, runDate);

            laborReportService.generateScrubberRemovedTransactions(errorGroup, reportsDirectory, runDate);
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
     * This will process a group of origin entries.
     * 
     * The COBOL code was refactored a lot to get this so there isn't a 1 to 1 section of Cobol relating to this.
     * 
     * @param originEntryGroup Group to process
     */
    private void processGroup(OriginEntryGroup originEntryGroup) {

        LaborOriginEntry lastEntry = null;
        scrubCostShareAmount = KualiDecimal.ZERO;
        unitOfWork = new UnitOfWorkInfo();

        Iterator entries = laborOriginEntryService.getEntriesByGroup(originEntryGroup);
        while (entries.hasNext()) {
            LaborOriginEntry unscrubbedEntry = (LaborOriginEntry) entries.next();
            scrubberReport.incrementUnscrubbedRecordsRead();

            transactionErrors = new ArrayList<Message>();

            // This is done so if the code modifies this row, then saves it, it will be an insert,
            // and it won't touch the original. The Scrubber never modifies input rows/groups.
            unscrubbedEntry.setGroup(null);
            unscrubbedEntry.setVersionNumber(null);
            unscrubbedEntry.setEntryId(null);

            boolean saveErrorTransaction = false;
            boolean saveValidTransaction = false;

            // Build a scrubbed entry
            // Labor has more fields
            LaborOriginEntry scrubbedEntry = new LaborOriginEntry();
            buildScrubbedEntry(unscrubbedEntry, scrubbedEntry);
            
            //For Labor Scrubber
            boolean validateAccountIndicator = false;
            
            List<Message> tmperrors = scrubberValidator.validateTransaction(unscrubbedEntry, scrubbedEntry, universityRunDate, validateAccountIndicator);
            transactionErrors.addAll(tmperrors);

            // Expired account?
            if ((unscrubbedEntry.getAccount() != null) && (unscrubbedEntry.getAccount().isAccountClosedIndicator())) {
                // Make a copy of it so OJB doesn't just update the row in the original
                // group. It needs to make a new one in the expired group
                LaborOriginEntry expiredEntry = new LaborOriginEntry(scrubbedEntry);

                createOutputEntry(expiredEntry, expiredGroup);
                scrubberReport.incrementExpiredAccountFound();
            }

            if (!isFatal(transactionErrors)) {
                saveValidTransaction = true;

                // See if unit of work has changed
                
                //TODO: Check it in Unit test
                if (!unitOfWork.isSameUnitOfWork(scrubbedEntry)) {
                    // Generate offset for last unit of work
                    
                    //generateOffset(lastEntry);

                    unitOfWork = new UnitOfWorkInfo(scrubbedEntry);
                }

                KualiDecimal transactionAmount = scrubbedEntry.getTransactionLedgerEntryAmount();

                KualiParameterRule offsetFiscalPeriods = getRule(GLConstants.GlScrubberGroupRules.OFFSET_FISCAL_PERIOD_CODES);

                if (scrubbedEntry.getBalanceType().isFinancialOffsetGenerationIndicator() && offsetFiscalPeriods.succeedsRule(scrubbedEntry.getUniversityFiscalPeriodCode())) {
                    if (scrubbedEntry.isDebit()) {
                        unitOfWork.offsetAmount = unitOfWork.offsetAmount.add(transactionAmount);
                    }
                    else {
                        unitOfWork.offsetAmount = unitOfWork.offsetAmount.subtract(transactionAmount);
                    }
                }

                // The sub account type code will only exist if there is a valid sub account
                String subAccountTypeCode = "  ";
                if (scrubbedEntry.getA21SubAccount() != null) {
                    subAccountTypeCode = scrubbedEntry.getA21SubAccount().getSubAccountTypeCode();
                }

                KualiParameterRule costShareObjectTypeCodes = getRule(GLConstants.GlScrubberGroupRules.COST_SHARE_OBJ_TYPE_CODES);
                KualiParameterRule costShareEncBalanceTypeCodes = getRule(GLConstants.GlScrubberGroupRules.COST_SHARE_ENC_BAL_TYP_CODES);
                KualiParameterRule costShareEncFiscalPeriodCodes = getRule(GLConstants.GlScrubberGroupRules.COST_SHARE_ENC_FISCAL_PERIOD_CODES);
                KualiParameterRule costShareEncDocTypeCodes = getRule(GLConstants.GlScrubberGroupRules.COST_SHARE_ENC_DOC_TYPE_CODES);
                KualiParameterRule costShareFiscalPeriodCodes = getRule(GLConstants.GlScrubberGroupRules.COST_SHARE_FISCAL_PERIOD_CODES);

                if (costShareObjectTypeCodes.succeedsRule(scrubbedEntry.getFinancialObjectTypeCode()) && 
                        costShareEncBalanceTypeCodes.succeedsRule(scrubbedEntry.getFinancialBalanceTypeCode()) && 
                        scrubbedEntry.getAccount().isForContractsAndGrants() && KFSConstants.COST_SHARE.equals(subAccountTypeCode) && 
                        costShareEncFiscalPeriodCodes.succeedsRule(scrubbedEntry.getUniversityFiscalPeriodCode()) && 
                        costShareEncDocTypeCodes.succeedsRule(scrubbedEntry.getFinancialDocumentTypeCode().trim())) {

                    //TODO: Check 
                    /*TransactionError te1 = generateCostShareEncumbranceEntries(scrubbedEntry);
                    if (te1 != null) {
                        List errors = new ArrayList();
                        errors.add(te1.message);
                        scrubberReportErrors.put(te1.transaction, errors);

                        saveValidTransaction = false;
                        saveErrorTransaction = true;
                    }
*/                }

                if (costShareObjectTypeCodes.succeedsRule(scrubbedEntry.getFinancialObjectTypeCode()) && 
                        scrubbedEntry.getOption().getActualFinancialBalanceTypeCd().equals(scrubbedEntry.getFinancialBalanceTypeCode()) && 
                        scrubbedEntry.getAccount().isForContractsAndGrants() && 
                        KFSConstants.COST_SHARE.equals(subAccountTypeCode) && 
                        costShareFiscalPeriodCodes.succeedsRule(scrubbedEntry.getUniversityFiscalPeriodCode()) && 
                        costShareEncDocTypeCodes.succeedsRule(scrubbedEntry.getFinancialDocumentTypeCode().trim())) {
                    if (scrubbedEntry.isDebit()) {
                        scrubCostShareAmount = scrubCostShareAmount.subtract(transactionAmount);
                    }
                    else {
                        scrubCostShareAmount = scrubCostShareAmount.add(transactionAmount);
                    }
                }

                KualiParameterRule otherDocTypeCodes = getRule(GLConstants.GlScrubberGroupRules.CAP_LIAB_PLANT_DOC_TYPE_CODES);

                //TODO: Check
                /*if (otherDocTypeCodes.succeedsRule(scrubbedEntry.getFinancialDocumentTypeCode())) {
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
*/
                
                //TODO: Check
/*                if (!scrubCostShareAmount.isZero()) {
                    TransactionError te = generateCostShareEntries(scrubbedEntry);

                    if (te != null) {
                        saveValidTransaction = false;
                        saveErrorTransaction = false;

                        // Make a copy of it so OJB doesn't just update the row in the original
                        // group. It needs to make a new one in the error group
                        LaborOriginEntry errorEntry = new LaborOriginEntry(te.transaction);
                        errorEntry.setTransactionScrubberOffsetGenerationIndicator(false);
                        createOutputEntry(errorEntry, errorGroup);
                        scrubberReport.incrementErrorRecordWritten();

                        List messages = new ArrayList();
                        messages.add(te.message);
                        scrubberReportErrors.put(errorEntry, messages);
                    }
                    scrubCostShareAmount = KualiDecimal.ZERO;
                }*/

                if (transactionErrors.size() > 0) {
                    scrubberReportErrors.put(scrubbedEntry, transactionErrors);
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
                createOutputEntry(scrubbedEntry, validGroup);
                scrubberReport.incrementScrubbedRecordWritten();
            }

            if (saveErrorTransaction) {
                // Make a copy of it so OJB doesn't just update the row in the original
                // group. It needs to make a new one in the error group
                LaborOriginEntry errorEntry = new LaborOriginEntry(unscrubbedEntry);
                errorEntry.setTransactionScrubberOffsetGenerationIndicator(false);
                createOutputEntry(errorEntry, errorGroup);
                scrubberReport.incrementErrorRecordWritten();
            }
        }

        // Generate last offset (if necessary)
        //TODO: check
        //generateOffset(lastEntry);
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

    private KualiParameterRule getRule(String rule) {
        KualiParameterRule r = rules.get(rule);
        if (r == null) {
            throw new IllegalArgumentException("Business Rule: " + GLConstants.GL_SCRUBBER_GROUP + "/" + rule + " does not exist");
        }
        return r;
    }

    private FinancialSystemParameter getParameter(String param) {
        FinancialSystemParameter p = parameters.get(param);
        if (p == null) {
            throw new IllegalArgumentException("Financial System Parameter: " + GLConstants.GL_SCRUBBER_GROUP + "/" + param + " does not exist");
        }
        return p;
    }

    /**
     * Get all the transaction descriptions from the param table
     * 
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
     * 
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
     * 
     * @return Offset message
     */
    private String getOffsetMessage() {
        String msg = offsetDescription + SPACES;

        return msg.substring(0, 33) + offsetString;
    }

    /**
     * Save an entry in origin entry
     * 
     * @param entry Entry to save
     * @param group Group to save it in
     */
    private void createOutputEntry(LaborOriginEntry entry, OriginEntryGroup group) {
        // Write the entry if we aren't running in report only mode.
        if ( reportOnlyMode ) {
            // If the group is null don't write it because the error and expired groups aren't created in reportOnlyMode 
            if ( group != null ) {
            entry.setGroup(group);
            laborOriginEntryService.save(entry);
        }
        } else {
            entry.setGroup(group);
            laborOriginEntryService.save(entry);
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
        if (!StringUtils.hasText(cutoffTime)) {
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
     * This method modifies the run date if it is before the cutoff time specified by calling
     * the setCutoffTimeForPreviousDay method.
     * 
     * See https://test.kuali.org/jira/browse/KULRNE-70
     * 
     * This method is public to facilitate unit testing
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
            // per old COBOL code (see https://test.kuali.org/jira/browse/KULRNE-70),
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
        FinancialSystemParameter cutoffParam = parameters.get(GLConstants.GlScrubberGroupParameters.SCRUBBER_CUTOFF_TIME);
        String cutoffTime = null;
        if (cutoffParam == null) {
            LOG.debug("Cutoff time system parameter not found");
            unsetCutoffTimeForPreviousDay();
            return;
        }
        cutoffTime = cutoffParam.getFinancialSystemParameterText();
        setCutoffTime(cutoffTime);
    }
    
    protected void buildScrubbedEntry(LaborOriginEntry unscrubbedEntry, LaborOriginEntry scrubbedEntry){
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
        scrubbedEntry.setFinancialDocument(unscrubbedEntry.getFinancialDocument());
        scrubbedEntry.setReferenceFinancialDocumentType(unscrubbedEntry.getReferenceFinancialDocumentType());
        scrubbedEntry.setReferenceFinancialSystemOrigination(unscrubbedEntry.getReferenceFinancialSystemOrigination());
        scrubbedEntry.setPayrollEndDateFiscalPeriod(unscrubbedEntry.getPayrollEndDateFiscalPeriod());
        
    }
}
