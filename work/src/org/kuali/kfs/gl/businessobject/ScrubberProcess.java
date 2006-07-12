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
import java.text.NumberFormat;
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
import org.kuali.module.gl.service.impl.scrubber.DemergerReportData;
import org.kuali.module.gl.service.impl.scrubber.Message;
import org.kuali.module.gl.service.impl.scrubber.ScrubberReportData;
import org.kuali.module.gl.util.ObjectHelper;
import org.kuali.module.gl.util.StringHelper;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.util.StringUtils;

/**
 * This class has the logic for the scrubber. It is required because the scrubber process needs instance variables. Instance
 * variables in a spring service are shared between all code calling the service. This will make sure each run of the scrubber has
 * it's own instance variables instead of being shared.
 * 
 * @author Kuali
 */
public class ScrubberProcess {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ScrubberProcess.class);

    // 40 spaces - used for filling in descriptions with spaces
    private static String SPACES = "                                        ";

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

    static private String[] validPlantIndebtednessSubFundGroupCodes = new String[] { "PFCMR", "PFRI" };

    static private String[] campusObjectSubTypeCodesForPlantFundAccountLookups = new String[] { "AM", "AF", "BD", "BF", "BI", "BR", "BX", "BY", "IF", "LA", "LE", "LF", "LI", "LR" };

    static private String[] organizationObjectSubTypeCodesForPlantFundAccountLookups = new String[] { "CL", "CM", "CF", "C1", "C2", "C3", "ES", "UC", "UF" };

    static private String[] badDocumentTypeCodesForCapitalization = new String[] { "TF", "YETF", "AV", "AVAC", "AVAE", "AVRC" };

    static private String[] goodObjectSubTypeCodesForCapitalization = new String[] { "AM", "AF", "BD", "BF", "BI", "BR", "BX", "BY", "CM", "CF", "C1", "C2", "C3", "ES", "IF", "LA", "LE", "LF", "LI", "LR", "UC", "UF" };

    static private String[] invalidDocumentTypesForLiabilities = new String[] { "TF", "YETF", "AV", "AVAC", "AVAE", "AVRC" };

    static private String[] invalidFiscalPeriodCodesForOffsetGeneration = new String[] { "BB", "CB" };

    static private String[] badUniversityFiscalPeriodCodesForCapitalization = new String[] { "BB", "CB" };

    static private String[] invalidDocumentTypeCodesForCostShareEncumbrances = new String[] { "JV", "AA" };

    static private String[] validObjectTypeCodesForCostSharing = new String[] { "EE", "EX", "ES", "TE" };

    static private String[] validBalanceTypeCodesForCostSharing = new String[] { "AC", "EX", "IE", "PE" };

    static private String[] validBalanceTypeCodesForCostShareEncumbrances = new String[] { "EX", "IE", "PE" };

    /* Services required */
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
    public ScrubberProcess(FlexibleOffsetAccountService flexibleOffsetAccountService, DocumentTypeService documentTypeService, BeanFactory beanFactory, OriginEntryService originEntryService, OriginEntryGroupService originEntryGroupService, DateTimeService dateTimeService, OffsetDefinitionService offsetDefinitionService, ObjectCodeService objectCodeService, KualiConfigurationService kualiConfigurationService, UniversityDateDao universityDateDao, PersistenceService persistenceService, ReportService reportService, ScrubberValidator scrubberValidator) {
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
     * Scrub this single group read only. This will only output the scrubber report. It won't output any other groups.
     * 
     * @param group
     */
    public void scrubGroupReportOnly(OriginEntryGroup group) {
        LOG.debug("scrubGroupReportOnly() started");

        scrubEntries(group);
    }

    public void scrubEntries() {
        scrubEntries(null);
    }

    /**
     * Scrub all entries that need it in origin entry. Put valid scrubbed entries in a scrubber valid group, put errors in a
     * scrubber error group, and transactions with an expired account in the scrubber expired account group.
     */
    public void scrubEntries(OriginEntryGroup group) {
        LOG.debug("scrubEntries() started");

        reportOnlyMode = (group != null);

        scrubberReportErrors = new HashMap<Transaction, List<Message>>();

        // setup an object to hold the "default" date information
        runDate = new Date(dateTimeService.getCurrentDate().getTime());
        runCal = Calendar.getInstance();
        runCal.setTime(runDate);

        universityRunDate = universityDateDao.getByPrimaryKey(runDate);
        if (universityRunDate == null) {
            throw new IllegalStateException(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_UNIV_DATE_NOT_FOUND));
        }

        setOffsetString();
        setDescriptions();

        // Create the groups that will store the valid and error entries that come out of the scrubber
        if (!reportOnlyMode) {
            validGroup = originEntryGroupService.createGroup(runDate, OriginEntrySource.SCRUBBER_VALID, true, true, false);
            errorGroup = originEntryGroupService.createGroup(runDate, OriginEntrySource.SCRUBBER_ERROR, false, true, false);
            expiredGroup = originEntryGroupService.createGroup(runDate, OriginEntrySource.SCRUBBER_EXPIRED, false, true, false);
        }

        // get the origin entry groups to be processed by Scrubber
        Collection groupsToScrub = null;
        if (reportOnlyMode) {
            groupsToScrub = new ArrayList();
            groupsToScrub.add(group);
        }
        else {
            groupsToScrub = originEntryGroupService.getBackupGroups(runDate);
        }
        LOG.debug("scrubEntries() number of groups to scrub: " + groupsToScrub.size());

        // generate the reports based on the origin entries to be processed by scrubber
        if (!reportOnlyMode) {
            reportService.generateScrubberLedgerSummaryReport(runDate, groupsToScrub, "General Ledger Input Transactions");
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
        reportService.generateScrubberStatisticsReport(runDate, scrubberReport, scrubberReportErrors);

        // run the demerger and generate the demerger report
        if (!reportOnlyMode) {
            performDemerger(errorGroup, validGroup);

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
     * @param errorGroup
     * @param validGroup
     */
    private void performDemerger(OriginEntryGroup errorGroup, OriginEntryGroup validGroup) {
        LOG.debug("performDemerger() started");

        DemergerReportData demergerReport = new DemergerReportData();

        // Read all the documents from the error group and move all non-generated
        // transactions for these documents from the valid group into the error group
        Iterator<OriginEntry> errorDocuments = originEntryService.getDocumentsByGroup(errorGroup);
        while (errorDocuments.hasNext()) {
            OriginEntry document = errorDocuments.next();
            demergerReport.incrementErrorTransactionsRead();
            demergerReport.incrementErrorTransactionsSaved();

            // Get all the transactions for the document in the valid group
            Iterator<OriginEntry> transactions = originEntryService.getEntriesByDocument(validGroup, document.getFinancialDocumentNumber(), document.getFinancialDocumentTypeCode(), document.getFinancialSystemOriginationCode());

            while (transactions.hasNext()) {
                OriginEntry transaction = transactions.next();

                String transactionType = getTransactionType(transaction);
                if ("CE".equals(transactionType)) {
                    demergerReport.incrementCostShareEncumbranceTransactionsBypassed();
                    originEntryService.delete(transaction);
                }
                else if ("O".equals(transactionType)) {
                    demergerReport.incrementOffsetTransactionsBypassed();
                    originEntryService.delete(transaction);
                }
                else if ("C".equals(transactionType)) {
                    demergerReport.incrementCapitalizationTransactionsBypassed();
                    originEntryService.delete(transaction);
                }
                else if ("L".equals(transactionType)) {
                    demergerReport.incrementLiabilityTransactionsBypassed();
                    originEntryService.delete(transaction);
                }
                else if ("T".equals(transactionType)) {
                    demergerReport.incrementTransferTransactionsBypassed();
                    originEntryService.delete(transaction);
                }
                else if ("CS".equals(transactionType)) {
                    demergerReport.incrementCostShareTransactionsBypassed();
                    originEntryService.delete(transaction);
                }
                else {
                    demergerReport.incrementErrorTransactionsSaved();
                    transaction.setGroup(errorGroup);
                    originEntryService.save(transaction);
                }
            }
        }

        // Read all the transactions in the valid group and update the cost share transactions
        Iterator<OriginEntry> validTransactions = originEntryService.getDocumentsByGroup(validGroup);
        while (validTransactions.hasNext()) {
            OriginEntry transaction = validTransactions.next();
            demergerReport.incrementValidTransactionsSaved();

            String transactionType = getTransactionType(transaction);
            if ("CS".equals(transactionType)) {
                transaction.setFinancialDocumentTypeCode("TF");
                transaction.setFinancialSystemOriginationCode("CS");
                StringBuffer docNbr = new StringBuffer("CSHR");

                String desc = transaction.getTransactionLedgerEntryDescription();

                docNbr.append(desc.substring(36, 38));
                docNbr.append("/");
                docNbr.append(desc.substring(38, 40));
                transaction.setFinancialDocumentNumber(docNbr.toString());

                transaction.setTransactionLedgerEntryDescription(desc.substring(0, 33));

                originEntryService.save(transaction);
            }
        }

        reportService.generateScrubberDemergerStatisticsReports(runDate, demergerReport);
    }

    /**
     * Determine the type of the transaction by looking at attributes
     * 
     * @param transaction Transaction to identify
     * @return CE (Cost share encumbrance, O (Offset), C (apitalization), L (Liability), T (Transfer), CS (Cost Share), X (Other)
     */
    private String getTransactionType(OriginEntry transaction) {
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

        OriginEntry lastEntry = null;
        scrubCostShareAmount = KualiDecimal.ZERO;
        unitOfWork = new UnitOfWorkInfo();

        Iterator entries = originEntryService.getEntriesByGroup(originEntryGroup);
        while (entries.hasNext()) {
            OriginEntry unscrubbedEntry = (OriginEntry) entries.next();

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

            List<Message> tmperrors = scrubberValidator.validateTransaction(unscrubbedEntry, scrubbedEntry, universityRunDate);
            transactionErrors.addAll(tmperrors);

            // Expired account?
            if ((unscrubbedEntry.getAccount() != null) && (unscrubbedEntry.getAccount().isAccountClosedIndicator())) {
                // Make a copy of it so OJB doesn't just update the row in the original
                // group. It needs to make a new one in the expired group
                OriginEntry expiredEntry = new OriginEntry(scrubbedEntry);

                createOutputEntry(expiredEntry, expiredGroup);
                scrubberReport.incrementExpiredAccountFound();
            }

            if ( ! isFatal(transactionErrors) ) {
                saveValidTransaction = true;

                // See if unit of work has changed
                if (!unitOfWork.isSameUnitOfWork(scrubbedEntry)) {
                    // Generate offset for last unit of work
                    generateOffset(lastEntry);

                    unitOfWork = new UnitOfWorkInfo(scrubbedEntry);
                }

                KualiDecimal transactionAmount = scrubbedEntry.getTransactionLedgerEntryAmount();

                if (scrubbedEntry.getBalanceType().isFinancialOffsetGenerationIndicator() && !"ACLO".equals(scrubbedEntry.getFinancialDocumentTypeCode()) && 
                        !ObjectHelper.isOneOf(scrubbedEntry.getUniversityFiscalPeriodCode(), invalidFiscalPeriodCodesForOffsetGeneration)) {
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

                if (ObjectHelper.isOneOf(scrubbedEntry.getFinancialObjectTypeCode(), validObjectTypeCodesForCostSharing) && 
                        ObjectHelper.isOneOf(scrubbedEntry.getFinancialBalanceTypeCode(), validBalanceTypeCodesForCostShareEncumbrances) && 
                        scrubbedEntry.getAccount().isInCg() && "CS".equals(subAccountTypeCode) && 
                        !ObjectHelper.isOneOf(scrubbedEntry.getUniversityFiscalPeriodCode(), invalidFiscalPeriodCodesForOffsetGeneration) && 
                        (StringHelper.isEmpty(scrubbedEntry.getFinancialDocumentTypeCode()) || !ObjectHelper.isOneOf(scrubbedEntry.getFinancialDocumentTypeCode().trim(), invalidDocumentTypeCodesForCostShareEncumbrances))) {
                    TransactionError te1 = generateCostShareEncumbranceEntries(scrubbedEntry);
                    if ( te1 != null ) {
                        List errors = new ArrayList();
                        errors.add(te1.message);
                        scrubberReportErrors.put(te1.transaction, errors);

                        saveValidTransaction = false;
                        saveErrorTransaction = true;
                    }
                }

                if (ObjectHelper.isOneOf(scrubbedEntry.getFinancialObjectTypeCode(), validObjectTypeCodesForCostSharing) && 
                        "AC".equals(scrubbedEntry.getFinancialBalanceTypeCode()) && scrubbedEntry.getAccount().isInCg() && "CS".equals(subAccountTypeCode) && 
                        !ObjectHelper.isOneOf(scrubbedEntry.getUniversityFiscalPeriodCode(), invalidFiscalPeriodCodesForOffsetGeneration) && 
                        !ObjectHelper.isOneOf(scrubbedEntry.getFinancialDocumentTypeCode().trim(), invalidDocumentTypeCodesForCostShareEncumbrances)) {
                    if (scrubbedEntry.isDebit()) {
                        scrubCostShareAmount = scrubCostShareAmount.subtract(transactionAmount);
                    }
                    else {
                        scrubCostShareAmount = scrubCostShareAmount.add(transactionAmount);
                    }
                }

                if ( ! ObjectHelper.isOneOf(scrubbedEntry.getFinancialDocumentTypeCode(), new String[] { "JV", "ACLO" }) ) {
                    processCapitalization(scrubbedEntry);
                    processLiabilities(scrubbedEntry);
                    processPlantIndebtedness(scrubbedEntry);
                }

                if ( ! scrubCostShareAmount.isZero() ) {
                    TransactionError te = generateCostShareEntries(scrubbedEntry);

                    if ( te != null ) {
                        saveValidTransaction = false;
                        saveErrorTransaction = false;

                        // Make a copy of it so OJB doesn't just update the row in the original
                        // group. It needs to make a new one in the error group
                        OriginEntry errorEntry = new OriginEntry(te.transaction);
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
                    scrubberReportErrors.put(scrubbedEntry, transactionErrors);
                }

                lastEntry = scrubbedEntry;
            }
            else {
                // Error transaction
                saveErrorTransaction = true;

                scrubberReportErrors.put(unscrubbedEntry, transactionErrors);
            }

            if ( saveValidTransaction ) {
                scrubbedEntry.setTransactionScrubberOffsetGenerationIndicator(false);
                createOutputEntry(scrubbedEntry, validGroup);
                scrubberReport.incrementScrubbedRecordWritten();
            }

            if ( saveErrorTransaction ) {
                // Make a copy of it so OJB doesn't just update the row in the original
                // group. It needs to make a new one in the error group
                OriginEntry errorEntry = new OriginEntry(unscrubbedEntry);
                errorEntry.setTransactionScrubberOffsetGenerationIndicator(false);
                createOutputEntry(errorEntry, errorGroup);
                scrubberReport.incrementErrorRecordWritten();
            }
        }

        // Generate last offset (if necessary)
        generateOffset(lastEntry);
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
     * 3000-COST-SHARE to 3100-READ-OFSD in the cobol
     * 
     * Generate Cost Share Entries
     * 
     * @param scrubbedEntry
     */
    private TransactionError generateCostShareEntries(OriginEntry scrubbedEntry) {
        LOG.debug("generateCostShareEntries() started");

        OriginEntry costShareEntry = new OriginEntry(scrubbedEntry);

        costShareEntry.setFinancialObjectCode("9915");
        costShareEntry.setFinancialSubObjectCode(Constants.DASHES_SUB_OBJECT_CODE);
        costShareEntry.setFinancialObjectTypeCode("TE");
        costShareEntry.setTransactionLedgerEntrySequenceNumber(new Integer(0));

        StringBuffer description = new StringBuffer();
        description.append(costShareDescription);
        description.append(" ").append(scrubbedEntry.getAccountNumber());
        description.append(offsetString);
        costShareEntry.setTransactionLedgerEntryDescription(description.toString());

        costShareEntry.setTransactionLedgerEntryAmount(scrubCostShareAmount);
        if (scrubCostShareAmount.isPositive()) {
            costShareEntry.setTransactionDebitCreditCode(Constants.GL_DEBIT_CODE);
        }
        else {
            costShareEntry.setTransactionDebitCreditCode(Constants.GL_CREDIT_CODE);
            costShareEntry.setTransactionLedgerEntryAmount(scrubCostShareAmount.negated());
        }

        costShareEntry.setTransactionDate(runDate);
        costShareEntry.setOrganizationDocumentNumber(null);
        costShareEntry.setProjectCode(Constants.DASHES_PROJECT_CODE);
        costShareEntry.setOrganizationReferenceId(null);
        costShareEntry.setReferenceFinancialDocumentTypeCode(null);
        costShareEntry.setReferenceFinancialSystemOriginationCode(null);
        costShareEntry.setReferenceFinancialDocumentNumber(null);
        costShareEntry.setFinancialDocumentReversalDate(null);
        costShareEntry.setTransactionEncumbranceUpdateCode(null);

        createOutputEntry(costShareEntry, validGroup);
        scrubberReport.incrementCostShareEntryGenerated();

        OriginEntry costShareOffsetEntry = new OriginEntry(costShareEntry);
        costShareOffsetEntry.setTransactionLedgerEntryDescription(getOffsetMessage());

        OffsetDefinition offsetDefinition = offsetDefinitionService.getByPrimaryId(scrubbedEntry.getUniversityFiscalYear(), scrubbedEntry.getChartOfAccountsCode(), "TF", scrubbedEntry.getFinancialBalanceTypeCode());
        if (offsetDefinition != null) {
            if ( offsetDefinition.getFinancialObject() ==  null ) {
                StringBuffer objectCodeKey = new StringBuffer();
                objectCodeKey.append(costShareEntry.getUniversityFiscalYear());
                objectCodeKey.append("-").append(scrubbedEntry.getChartOfAccountsCode());
                objectCodeKey.append("-").append(scrubbedEntry.getFinancialObjectCode());

                Message m = new Message(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_OFFSET_DEFINITION_OBJECT_CODE_NOT_FOUND) + " (" + objectCodeKey.toString() + ")", Message.TYPE_FATAL);
                LOG.debug("generateCostShareEntries() Error 1 object not found");
                return new TransactionError(costShareEntry,m);
            }

            costShareOffsetEntry.setFinancialObjectCode(offsetDefinition.getFinancialObjectCode());
            costShareOffsetEntry.setFinancialObject(offsetDefinition.getFinancialObject());

            if (!StringUtils.hasText(offsetDefinition.getFinancialSubObjectCode())) {
                costShareOffsetEntry.setFinancialSubObjectCode(Constants.DASHES_SUB_OBJECT_CODE);
            }
            else {
                costShareOffsetEntry.setFinancialSubObjectCode(offsetDefinition.getFinancialSubObjectCode());
            }
        }
        else {
            Map<Transaction,List<Message>> errors = new HashMap<Transaction,List<Message>>();

            StringBuffer offsetKey = new StringBuffer(" c ");
            offsetKey.append(scrubbedEntry.getUniversityFiscalYear());
            offsetKey.append("-");
            offsetKey.append(scrubbedEntry.getChartOfAccountsCode());
            offsetKey.append("-TF-");
            offsetKey.append(scrubbedEntry.getFinancialBalanceTypeCode());

            Message m = new Message(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_OFFSET_DEFINITION_NOT_FOUND) + " (" + offsetKey.toString() + ")", Message.TYPE_FATAL);

            LOG.debug("generateCostShareEntries() Error 2 offset not found");
            return new TransactionError(costShareEntry,m);
        }

        costShareOffsetEntry.setFinancialObjectTypeCode(offsetDefinition.getFinancialObject().getFinancialObjectTypeCode());

        if (costShareEntry.isCredit()) {
            costShareOffsetEntry.setTransactionDebitCreditCode(Constants.GL_DEBIT_CODE);
        }
        else {
            costShareOffsetEntry.setTransactionDebitCreditCode(Constants.GL_CREDIT_CODE);
        }

        createOutputEntry(costShareOffsetEntry, validGroup);
        scrubberReport.incrementCostShareEntryGenerated();

        OriginEntry costShareSourceAccountEntry = new OriginEntry(costShareEntry);

        description = new StringBuffer();
        description.append(costShareDescription);
        description.append(" ").append(scrubbedEntry.getAccountNumber());
        description.append(offsetString);
        costShareSourceAccountEntry.setTransactionLedgerEntryDescription(description.toString());

        costShareSourceAccountEntry.setChartOfAccountsCode(scrubbedEntry.getA21SubAccount().getCostShareChartOfAccountCode());
        costShareSourceAccountEntry.setAccountNumber(scrubbedEntry.getA21SubAccount().getCostShareSourceAccountNumber());

        setCostShareObjectCode(costShareSourceAccountEntry, scrubbedEntry);
        costShareSourceAccountEntry.setSubAccountNumber(scrubbedEntry.getA21SubAccount().getCostShareSourceSubAccountNumber());

        if (StringHelper.isNullOrEmpty(costShareSourceAccountEntry.getSubAccountNumber())) {
            costShareSourceAccountEntry.setSubAccountNumber(Constants.DASHES_SUB_ACCOUNT_NUMBER);
        }

        costShareSourceAccountEntry.setFinancialSubObjectCode(Constants.DASHES_SUB_OBJECT_CODE);
        costShareSourceAccountEntry.setFinancialObjectTypeCode("TE");
        costShareSourceAccountEntry.setTransactionLedgerEntrySequenceNumber(new Integer(0));

        costShareSourceAccountEntry.setTransactionLedgerEntryAmount(scrubCostShareAmount);
        if (scrubCostShareAmount.isPositive()) {
            costShareSourceAccountEntry.setTransactionDebitCreditCode(Constants.GL_CREDIT_CODE);
        }
        else {
            costShareSourceAccountEntry.setTransactionDebitCreditCode(Constants.GL_DEBIT_CODE);
            costShareSourceAccountEntry.setTransactionLedgerEntryAmount(scrubCostShareAmount.negated());
        }

        costShareSourceAccountEntry.setTransactionDate(runDate);
        costShareSourceAccountEntry.setOrganizationDocumentNumber(null);
        costShareSourceAccountEntry.setProjectCode(Constants.DASHES_PROJECT_CODE);
        costShareSourceAccountEntry.setOrganizationReferenceId(null);
        costShareSourceAccountEntry.setReferenceFinancialDocumentTypeCode(null);
        costShareSourceAccountEntry.setReferenceFinancialSystemOriginationCode(null);
        costShareSourceAccountEntry.setReferenceFinancialDocumentNumber(null);
        costShareSourceAccountEntry.setFinancialDocumentReversalDate(null);
        costShareSourceAccountEntry.setTransactionEncumbranceUpdateCode(null);

        createOutputEntry(costShareSourceAccountEntry, validGroup);
        scrubberReport.incrementCostShareEntryGenerated();

        OriginEntry costShareSourceAccountOffsetEntry = new OriginEntry(costShareSourceAccountEntry);
        costShareSourceAccountOffsetEntry.setTransactionLedgerEntryDescription(getOffsetMessage());

        // Lookup the new offset definition.
        offsetDefinition = offsetDefinitionService.getByPrimaryId(scrubbedEntry.getUniversityFiscalYear(), scrubbedEntry.getChartOfAccountsCode(), "TF", scrubbedEntry.getFinancialBalanceTypeCode());
        if (offsetDefinition != null) {
            if ( offsetDefinition.getFinancialObject() ==  null ) {
                Map<Transaction,List<Message>> errors = new HashMap<Transaction,List<Message>>();

                StringBuffer objectCodeKey = new StringBuffer();
                objectCodeKey.append(costShareEntry.getUniversityFiscalYear());
                objectCodeKey.append("-").append(scrubbedEntry.getChartOfAccountsCode());
                objectCodeKey.append("-").append(scrubbedEntry.getFinancialObjectCode());

                Message m = new Message(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_OFFSET_DEFINITION_OBJECT_CODE_NOT_FOUND) + " (" + objectCodeKey.toString() + ")", Message.TYPE_FATAL);

                LOG.debug("generateCostShareEntries() Error 3 object not found");
                return new TransactionError(costShareSourceAccountEntry,m);
            }

            costShareSourceAccountOffsetEntry.setFinancialObjectCode(offsetDefinition.getFinancialObjectCode());
            costShareSourceAccountOffsetEntry.setFinancialObject(offsetDefinition.getFinancialObject());

            if (!StringUtils.hasText(offsetDefinition.getFinancialSubObjectCode())) {
                costShareSourceAccountOffsetEntry.setFinancialSubObjectCode(Constants.DASHES_SUB_OBJECT_CODE);
            }
            else {
                costShareSourceAccountOffsetEntry.setFinancialSubObjectCode(offsetDefinition.getFinancialSubObjectCode());
            }
        }
        else {
            Map<Transaction,List<Message>> errors = new HashMap<Transaction,List<Message>>();

            StringBuffer offsetKey = new StringBuffer(" d ");
            offsetKey.append(scrubbedEntry.getUniversityFiscalYear());
            offsetKey.append("-");
            offsetKey.append(scrubbedEntry.getChartOfAccountsCode());
            offsetKey.append("-TF-");
            offsetKey.append(scrubbedEntry.getFinancialBalanceTypeCode());

            Message m = new Message(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_OFFSET_DEFINITION_NOT_FOUND) + " (" + offsetKey.toString() + ")", Message.TYPE_FATAL);

            LOG.debug("generateCostShareEntries() Error 4 offset not found");
            return new TransactionError(costShareSourceAccountEntry,m);
        }

        costShareSourceAccountOffsetEntry.setFinancialObjectTypeCode(offsetDefinition.getFinancialObject().getFinancialObjectTypeCode());

        if (scrubbedEntry.isCredit()) {
            costShareSourceAccountOffsetEntry.setTransactionDebitCreditCode(Constants.GL_DEBIT_CODE);
        }
        else {
            costShareSourceAccountOffsetEntry.setTransactionDebitCreditCode(Constants.GL_CREDIT_CODE);
        }

        createOutputEntry(costShareSourceAccountOffsetEntry, validGroup);
        scrubberReport.incrementCostShareEntryGenerated();

        scrubCostShareAmount = KualiDecimal.ZERO;

        LOG.debug("generateCostShareEntries() successful");
        return null;
    }

    /**
     * Get all the transaction descriptions from the param table
     * 
     */
    private void setDescriptions() {
        offsetDescription = kualiConfigurationService.getPropertyString(KeyConstants.MSG_GENERATED_OFFSET);
        capitalizationDescription = kualiConfigurationService.getPropertyString(KeyConstants.MSG_GENERATED_CAPITALIZATION);
        liabilityDescription = kualiConfigurationService.getPropertyString(KeyConstants.MSG_GENERATED_LIABILITY);
        costShareDescription = kualiConfigurationService.getPropertyString(KeyConstants.MSG_GENERATED_COST_SHARE);
        transferDescription = kualiConfigurationService.getPropertyString(KeyConstants.MSG_GENERATED_TRANSFER);
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
     * Lines 4694 - 4798 of the Pro Cobol listing on Confluence
     * 
     * Generate capitalization entries if necessary
     * 
     * @param scrubbedEntry
     */
    private void processCapitalization(OriginEntry scrubbedEntry) {
        OriginEntry capitalizationEntry = new OriginEntry(scrubbedEntry);

        if (scrubbedEntry.getFinancialBalanceTypeCode().equals(scrubbedEntry.getOption().getActualFinancialBalanceTypeCd()) && scrubbedEntry.getUniversityFiscalYear().intValue() > 1995 && !ObjectHelper.isOneOf(scrubbedEntry.getFinancialDocumentTypeCode(), badDocumentTypeCodesForCapitalization) && !ObjectHelper.isOneOf(scrubbedEntry.getUniversityFiscalPeriodCode(), badUniversityFiscalPeriodCodesForCapitalization) && !"ACLO".equals(scrubbedEntry.getFinancialDocumentTypeCode()) && ObjectHelper.isOneOf(scrubbedEntry.getFinancialObject().getFinancialObjectSubTypeCode(), goodObjectSubTypeCodesForCapitalization) && !"EXTAGY".equals(scrubbedEntry.getAccount().getSubFundGroupCode()) && !"HO".equals(scrubbedEntry.getChartOfAccountsCode())) {
            String objectSubTypeCode = scrubbedEntry.getFinancialObject().getFinancialObjectSubTypeCode();
            if (objectSubTypeCodesToObjectCodesForCapitalization.containsKey(objectSubTypeCode)) {
                capitalizationEntry.setFinancialObjectCode((String) objectSubTypeCodesToObjectCodesForCapitalization.get(objectSubTypeCode));
                persistenceService.retrieveReferenceObject(capitalizationEntry, "financialObject");
            }

            capitalizationEntry.setFinancialObjectTypeCode("AS");
            persistenceService.retrieveReferenceObject(capitalizationEntry, "objectType");
            capitalizationEntry.setTransactionLedgerEntryDescription(capitalizationDescription);

            plantFundAccountLookup(scrubbedEntry, capitalizationEntry);

            capitalizationEntry.setUniversityFiscalPeriodCode(scrubbedEntry.getUniversityFiscalPeriodCode());

            createOutputEntry(capitalizationEntry, validGroup);
            scrubberReport.incrementCapitalizationEntryGenerated();

            // Clear out the id & the ojb version number to make sure we do an insert on the next one
            capitalizationEntry.setVersionNumber(null);
            capitalizationEntry.setEntryId(null);

            capitalizationEntry.setFinancialObjectCode("9899");
            capitalizationEntry.setFinancialObjectTypeCode("FB");

            if (scrubbedEntry.isDebit()) {
                capitalizationEntry.setTransactionDebitCreditCode(Constants.GL_CREDIT_CODE);
            }
            else {
                capitalizationEntry.setTransactionDebitCreditCode(Constants.GL_DEBIT_CODE);
            }

            createOutputEntry(capitalizationEntry, validGroup);
            scrubberReport.incrementCapitalizationEntryGenerated();
        }
    }

    /**
     * Lines 4855 - 4979 of the Pro Cobol listing on Confluence
     * 
     * Generate the plant indebtedness entries
     * 
     * @param scrubbedEntry
     */
    private void processPlantIndebtedness(OriginEntry scrubbedEntry) {

        OriginEntry plantIndebtednessEntry = new OriginEntry(scrubbedEntry);

        if (scrubbedEntry.getFinancialBalanceTypeCode().equals(scrubbedEntry.getOption().getActualFinancialBalanceTypeCd()) && ObjectHelper.isOneOf(scrubbedEntry.getAccount().getSubFundGroupCode(), validPlantIndebtednessSubFundGroupCodes) && "PI".equals(scrubbedEntry.getFinancialObject().getFinancialObjectSubTypeCode())) {
            plantIndebtednessEntry.setTransactionLedgerEntryDescription(Constants.PLANT_INDEBTEDNESS_ENTRY_DESCRIPTION);

            if (scrubbedEntry.isDebit()) {
                plantIndebtednessEntry.setTransactionDebitCreditCode(Constants.GL_CREDIT_CODE);
            }
            else {
                plantIndebtednessEntry.setTransactionDebitCreditCode(Constants.GL_DEBIT_CODE);
            }

            plantIndebtednessEntry.setTransactionScrubberOffsetGenerationIndicator(true);
            createOutputEntry(plantIndebtednessEntry, validGroup);
            scrubberReport.incrementPlantIndebtednessEntryGenerated();

            // Clear out the id & the ojb version number to make sure we do an insert on the next one
            plantIndebtednessEntry.setVersionNumber(null);
            plantIndebtednessEntry.setEntryId(null);

            plantIndebtednessEntry.setFinancialObjectCode("9899"); // FUND_BALANCE
            plantIndebtednessEntry.setFinancialObjectTypeCode("FB"); // FUND_BALANCE
            plantIndebtednessEntry.setTransactionDebitCreditCode(scrubbedEntry.getTransactionDebitCreditCode());

            plantIndebtednessEntry.setTransactionScrubberOffsetGenerationIndicator(true);
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

            // TODO Check this logic
            if (scrubbedEntry.getChartOfAccountsCode().equals(scrubbedEntry.getAccount().getOrganization().getChartOfAccountsCode()) && scrubbedEntry.getAccount().getOrganizationCode().equals(scrubbedEntry.getAccount().getOrganization().getOrganizationCode()) && scrubbedEntry.getAccountNumber().equals(scrubbedEntry.getAccount().getAccountNumber()) && scrubbedEntry.getChartOfAccountsCode().equals(scrubbedEntry.getAccount().getChartOfAccountsCode())) {
                plantIndebtednessEntry.setAccountNumber(scrubbedEntry.getAccount().getOrganization().getCampusPlantAccountNumber());
                plantIndebtednessEntry.setChartOfAccountsCode(scrubbedEntry.getAccount().getOrganization().getCampusPlantChartCode());
            }
            else {
            }

            plantIndebtednessEntry.setSubAccountNumber(Constants.DASHES_SUB_ACCOUNT_NUMBER);

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

            plantIndebtednessEntry.setFinancialObjectCode("9899");
            plantIndebtednessEntry.setFinancialObjectTypeCode("FB");

            if (scrubbedEntry.isDebit()) {
                plantIndebtednessEntry.setTransactionDebitCreditCode(Constants.GL_CREDIT_CODE);
            }
            else {
                plantIndebtednessEntry.setTransactionDebitCreditCode(Constants.GL_DEBIT_CODE);
            }

            createOutputEntry(plantIndebtednessEntry, validGroup);
            scrubberReport.incrementPlantIndebtednessEntryGenerated();
        }
    }

    /**
     * Lines 4799 to 4839 of the Pro Cobol list of the scrubber on Confluence
     * 
     * Generate the liability entries
     * 
     * @param scrubbedEntry
     */
    private void processLiabilities(OriginEntry scrubbedEntry) {
        OriginEntry liabilityEntry = new OriginEntry(scrubbedEntry);

        if (scrubbedEntry.getFinancialBalanceTypeCode().equals(scrubbedEntry.getOption().getActualFinancialBalanceTypeCd()) && scrubbedEntry.getUniversityFiscalYear().intValue() > 1995 && !ObjectHelper.isOneOf(scrubbedEntry.getFinancialDocumentTypeCode(), invalidDocumentTypesForLiabilities) && !ObjectHelper.isOneOf(scrubbedEntry.getUniversityFiscalPeriodCode(), invalidFiscalPeriodCodesForOffsetGeneration) && !"ACLO".equals(scrubbedEntry.getFinancialDocumentTypeCode()) && "CL".equals(scrubbedEntry.getFinancialObject().getFinancialObjectSubTypeCode()) && !"EXTAGY".equals(scrubbedEntry.getAccount().getSubFundGroupCode()) && !"HO".equals(scrubbedEntry.getChartOfAccountsCode())) {
            liabilityEntry.setFinancialObjectCode("9603"); // NOTES_PAYABLE_CAPITAL_LEASE
            liabilityEntry.setFinancialObjectTypeCode("LI"); // LIABILITY

            liabilityEntry.setTransactionDebitCreditCode(scrubbedEntry.getTransactionDebitCreditCode());
            liabilityEntry.setTransactionLedgerEntryDescription(liabilityDescription);
            plantFundAccountLookup(scrubbedEntry, liabilityEntry);

            createOutputEntry(liabilityEntry, validGroup);
            scrubberReport.incrementLiabilityEntryGenerated();

            // Clear out the id & the ojb version number to make sure we do an insert on the next one
            liabilityEntry.setVersionNumber(null);
            liabilityEntry.setEntryId(null);

            // ... and now generate the other half of the liability entry
            liabilityEntry.setFinancialObjectCode("9899"); // FUND_BALANCE
            liabilityEntry.setFinancialObjectTypeCode("FB"); // FUND_BALANCE

            if (liabilityEntry.isDebit()) {
                liabilityEntry.setTransactionDebitCreditCode(Constants.GL_CREDIT_CODE);
            }
            else {
                liabilityEntry.setTransactionDebitCreditCode(Constants.GL_DEBIT_CODE);
            }

            createOutputEntry(liabilityEntry, validGroup);
            scrubberReport.incrementLiabilityEntryGenerated();
        }
    }

    /**
     * 4000-PLANT-FUND-ACCT to 4000-PLANT-FUND-ACCT-EXIT in cobol
     * 
     * @param scrubbedEntry basis for plant fund entry
     * @param liabilityEntry liability entry
     */
    private void plantFundAccountLookup(OriginEntry scrubbedEntry, OriginEntry liabilityEntry) {

        liabilityEntry.setSubAccountNumber(Constants.DASHES_SUB_ACCOUNT_NUMBER);
        persistenceService.retrieveReferenceObject(liabilityEntry, "account");

        if (liabilityEntry.getChartOfAccountsCode().equals(liabilityEntry.getAccount().getOrganization().getChartOfAccountsCode()) && scrubbedEntry.getAccount().getOrganizationCode().equals(liabilityEntry.getAccount().getOrganization().getOrganizationCode()) && liabilityEntry.getAccountNumber().equals(liabilityEntry.getAccount().getAccountNumber()) && liabilityEntry.getChartOfAccountsCode().equals(liabilityEntry.getAccount().getChartOfAccountsCode())) {
            persistenceService.retrieveReferenceObject(liabilityEntry, "financialObject");
            persistenceService.retrieveReferenceObject(liabilityEntry.getAccount(), "organization");

            String objectSubTypeCode = scrubbedEntry.getFinancialObject().getFinancialObjectSubTypeCode();

            if (ObjectHelper.isOneOf(objectSubTypeCode, campusObjectSubTypeCodesForPlantFundAccountLookups)) {
                liabilityEntry.setAccountNumber(scrubbedEntry.getAccount().getOrganization().getCampusPlantAccountNumber());
                liabilityEntry.setChartOfAccountsCode(scrubbedEntry.getAccount().getOrganization().getCampusPlantChartCode());

                persistenceService.retrieveReferenceObject(liabilityEntry, "account");
                persistenceService.retrieveReferenceObject(liabilityEntry, "chart");
            }
            else if (ObjectHelper.isOneOf(objectSubTypeCode, organizationObjectSubTypeCodesForPlantFundAccountLookups)) {
                liabilityEntry.setAccountNumber(scrubbedEntry.getAccount().getOrganization().getOrganizationPlantAccountNumber());
                liabilityEntry.setChartOfAccountsCode(scrubbedEntry.getAccount().getOrganization().getOrganizationPlantChartCode());

                persistenceService.retrieveReferenceObject(liabilityEntry, "account");
                persistenceService.retrieveReferenceObject(liabilityEntry, "chart");
            }
        }
    }

    /**
     * 3200-COST-SHARE-ENC to 3200-CSE-EXIT in the COBOL
     * 
     * The purpose of this method is to generate a "Cost Share Encumbrance" transaction for the current transaction and its offset.
     * 
     * The cost share chart and account for current transaction are obtained from the CA_A21_SUB_ACCT_T table. This method calls the
     * method SET-OBJECT-2004 to get the Cost Share Object Code. It then writes out the cost share transaction. Next it read the
     * GL_OFFSET_DEFN_T table for the offset object code that corresponds to the cost share object code. In addition to the object
     * code it needs to get subobject code. It then reads the CA_OBJECT_CODE_T table to make sure the offset object code found in
     * the GL_OFFSET_DEFN_T is valid and to get the object type code associated with this object code. It writes out the offset
     * transaction and returns.
     * 
     * @param scrubbedEntry
     */
    private TransactionError generateCostShareEncumbranceEntries(OriginEntry scrubbedEntry) {
        LOG.debug("generateCostShareEncumbranceEntries() started");

        OriginEntry costShareEncumbranceEntry = new OriginEntry(scrubbedEntry);

        // First 28 characters of the description, padding to 28 if shorter)
        StringBuffer buffer = new StringBuffer((scrubbedEntry.getTransactionLedgerEntryDescription() + SPACES).substring(0, 28));

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
            }
            else {
                costShareEncumbranceEntry.setTransactionDebitCreditCode(Constants.GL_CREDIT_CODE);
                costShareEncumbranceEntry.setTransactionLedgerEntryAmount(scrubbedEntry.getTransactionLedgerEntryAmount().negated());
            }
        }

        costShareEncumbranceEntry.setTransactionDate(runDate);

        setCostShareObjectCode(costShareEncumbranceEntry, scrubbedEntry);

        costShareEncumbranceEntry.setTransactionScrubberOffsetGenerationIndicator(true);
        createOutputEntry(costShareEncumbranceEntry, validGroup);
        scrubberReport.incrementCostShareEncumbranceGenerated();

        OriginEntry costShareEncumbranceOffsetEntry = new OriginEntry(costShareEncumbranceEntry);

        costShareEncumbranceOffsetEntry.setTransactionLedgerEntryDescription(offsetDescription);

        OffsetDefinition offset = offsetDefinitionService.getByPrimaryId(costShareEncumbranceEntry.getUniversityFiscalYear(), costShareEncumbranceEntry.getChartOfAccountsCode(), costShareEncumbranceEntry.getFinancialDocumentTypeCode(), costShareEncumbranceEntry.getFinancialBalanceTypeCode());

        if (offset != null) {
            if ( costShareEncumbranceOffsetEntry.getFinancialObject() == null ) {
                    StringBuffer offsetKey = new StringBuffer();
                    offsetKey.append(costShareEncumbranceEntry.getUniversityFiscalYear());
                    offsetKey.append("-");
                    offsetKey.append(costShareEncumbranceEntry.getChartOfAccountsCode());
                    offsetKey.append("-");
                    offsetKey.append(costShareEncumbranceEntry.getFinancialObjectCode());

                    LOG.debug("generateCostShareEncumbranceEntries() object code not found");
                    return new TransactionError(costShareEncumbranceEntry,new Message(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_NO_OBJECT_FOR_OBJECT_ON_OFSD) + "(" + offsetKey.toString() + ")", Message.TYPE_FATAL));
            }
            costShareEncumbranceOffsetEntry.setFinancialObjectCode(offset.getFinancialObjectCode());
            costShareEncumbranceOffsetEntry.setFinancialObject(offset.getFinancialObject());

            if (offset.getFinancialSubObjectCode() == null) {
                costShareEncumbranceOffsetEntry.setFinancialSubObjectCode(Constants.DASHES_SUB_OBJECT_CODE);
            }
            else {
                costShareEncumbranceOffsetEntry.setFinancialSubObjectCode(offset.getFinancialSubObjectCode());
            }
        }
        else {
            StringBuffer offsetKey = new StringBuffer(" e ");
            offsetKey.append(costShareEncumbranceEntry.getUniversityFiscalYear());
            offsetKey.append("-");
            offsetKey.append(costShareEncumbranceEntry.getChartOfAccountsCode());
            offsetKey.append("-");
            offsetKey.append(costShareEncumbranceEntry.getFinancialDocumentTypeCode());
            offsetKey.append("-");
            offsetKey.append(costShareEncumbranceEntry.getFinancialBalanceTypeCode());

            LOG.debug("generateCostShareEncumbranceEntries() offset not found");
            return new TransactionError(costShareEncumbranceEntry,new Message(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_OFFSET_DEFINITION_NOT_FOUND) + "(" + offsetKey.toString() + ")", Message.TYPE_FATAL));
        }

        costShareEncumbranceOffsetEntry.setFinancialObjectTypeCode(offset.getFinancialObject().getFinancialObjectTypeCode());

        if (costShareEncumbranceEntry.isCredit()) {
            costShareEncumbranceOffsetEntry.setTransactionDebitCreditCode(Constants.GL_DEBIT_CODE);
        }
        else {
            costShareEncumbranceOffsetEntry.setTransactionDebitCreditCode(Constants.GL_CREDIT_CODE);
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
        scrubberReport.incrementCostShareEncumbranceGenerated();

        LOG.debug("generateCostShareEncumbranceEntries() returned successfully");
        return null;
    }

    /**
     * This code is SET-OBJECT-2004 to 2520-INIT-SCRB-AREA in the Cobol
     * 
     * @param costShareEntry GL Entry for cost share
     * @param originEntry Scrubbed GL Entry that this is based on
     */
    private void setCostShareObjectCode(OriginEntry costShareEntry, OriginEntry originEntry) {

        if (originEntry.getFinancialObject() == null) {
            persistenceService.retrieveReferenceObject(originEntry, "financialObject");
        }

        if (originEntry.getFinancialObject() == null) {
            addTransactionError(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_OBJECT_CODE_NOT_FOUND), originEntry.getFinancialObjectCode(), Message.TYPE_FATAL);
        }

        String originEntryObjectCode = originEntry.getFinancialObjectCode();
        String originEntryObjectLevelCode = (null == originEntry.getFinancialObject() ? "" : originEntry.getFinancialObject().getFinancialObjectLevelCode());

        // TODO This logic should be moved somewhere out of the code
        if ("ACSA".equals(originEntryObjectLevelCode)) { // ACADEMIC SALARIES
            originEntryObjectCode = "9920"; // TRSFRS_OF_FUNDS_ACAD_SAL
        }
        else if ("BASE".equals(originEntryObjectLevelCode)) { // ASSESMENTS_EXPENDITURES
            originEntryObjectCode = "9959"; // TRANSFER_OUT_20_REALLOCATION
        }
        else if ("BENF".equals(originEntryObjectLevelCode) && ("9956".equals(originEntryObjectCode) || 5700 > Integer.valueOf(originEntryObjectCode).intValue())) { // BENEFITS
            originEntryObjectCode = "9956"; // TRSFRS_OF_FUNDS_FRINGE_BENF
        }
        else if ("BENF".equals(originEntryObjectLevelCode)) { // BENEFITS
            originEntryObjectCode = "9957"; // TRSFRS_OF_FUNDS_RETIREMENT
        }
        else if ("BISA".equals(originEntryObjectLevelCode)) { // BI-WEEKLY_SALARY
            originEntryObjectCode = "9925"; // TRSFRS_OF_FUNDS_CLER_SAL
        }
        else if ("CAP".equals(originEntryObjectLevelCode)) { // CAPITAL_ASSETS
            originEntryObjectCode = "9970"; // TRSFRS_OF_FUNDS_CAPITAL
        }
        else if ("CORE".equals(originEntryObjectLevelCode)) { // ALLOTMENTS_AND_CHARGES_OUT
            // Do nothing
        }
        else if ("CORI".equals(originEntryObjectLevelCode)) { // ALLOTMENTS_AND_CHARGES_IN
            // Do nothing
        }
        else if ("FINA".equals(originEntryObjectLevelCode) && ("9954".equals(originEntryObjectCode) || "5400".equals(originEntryObjectCode))) {
            // STUDENT_FINANCIAL_AID - TRSFRS_OF_FUNDS_FEE_REM - GRADUATE_FEE_REMISSIONS
            originEntryObjectCode = "9954"; // TRSFRS_OF_FUNDS_CAPITAL
        }
        else if ("FINA".equals(originEntryObjectLevelCode)) { // STUDENT_FINANCIAL_AID
            originEntryObjectCode = "9958"; // TRSFRS_OF_FUNDS_FELL_AND_SCHO
        }
        else if ("HRCO".equals(originEntryObjectLevelCode)) { // HOURLY_COMPENSATION
            originEntryObjectCode = "9930"; // TRSFRS_OF_FUNDS_WAGES
        }
        else if ("ICOE".equals(originEntryObjectLevelCode)) { // INDIRECT_COST_RECOVERY_EXPENSE
            originEntryObjectCode = "9955"; // TRSFRS_OF_FUNDS_INDRCT_COST
        }
        else if ("PART".equals(originEntryObjectLevelCode)) { // PART_TIME_INSTRUCTION_NON_STUDENT
            originEntryObjectCode = "9923"; // TRSFRS_OF_FUNDS_ACAD_ASSIST
        }
        else if ("PRSA".equals(originEntryObjectLevelCode)) { // PROFESSIONAL_SALARIES
            originEntryObjectCode = "9924"; // TRSF_OF_FUNDS_PROF_SAL
        }
        else if ("RESV".equals(originEntryObjectLevelCode)) { // RESERVES
            originEntryObjectCode = "9979"; // TRSFRS_OF_FUNDS_UNAPP_BAL
        }
        else if ("SAAP".equals(originEntryObjectLevelCode)) { // SALARY_ACCRUAL_EXPENSE
            originEntryObjectCode = "9923"; // TRSFRS_OF_FUNDS_ACAD_ASSIST
        }
        else if ("TRAN".equals(originEntryObjectLevelCode)) { // TRANSFER_EXPENSE
            originEntryObjectCode = "9959"; // TRANSFER_OUT_20_REALLOCATION
        }
        else if ("TRAV".equals(originEntryObjectLevelCode)) { // TRAVEL
            originEntryObjectCode = "9960"; // TRSFRS_OF_FUNDS_TRAVEL
        }
        else if ("TREX".equals(originEntryObjectLevelCode)) { // TRANSFER_5199_EXPENSE
            originEntryObjectCode = "9959"; // TRANSFER_OUT_20_REALLOCATION
        }
        else if ("TRIN".equals(originEntryObjectLevelCode)) { // TRANSFER_1699_INCOME
            originEntryObjectCode = "9915"; // TRSFRS_OF_FUNDS_REVENUE
        }
        else {
            originEntryObjectCode = "9940"; // TRSFRS_OF_FUNDS_SUP_AND_EXP
        }

        // Lookup the new object code
        ObjectCode objectCode = objectCodeService.getByPrimaryId(costShareEntry.getUniversityFiscalYear(), costShareEntry.getChartOfAccountsCode(), originEntryObjectCode);
        if (objectCode != null) {
            costShareEntry.setFinancialObjectTypeCode(objectCode.getFinancialObjectTypeCode());
            costShareEntry.setFinancialObjectCode(originEntryObjectCode);
        }
        else {
            addTransactionError(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_COST_SHARE_OBJECT_NOT_FOUND), costShareEntry.getFinancialObjectCode(), Message.TYPE_FATAL);
        }
    }

    /**
     * The purpose of this method is to build the actual offset transaction. It does this by performing the following steps: 1.
     * Getting the offset object code and offset subobject code from the GL Offset Definition Table. 2. For the offset object code
     * it needs to get the associated object type, object subtype, and object active code.
     * 
     * This code is 3000-OFFSET to SET-OBJECT-2004 in the Cobol
     * 
     * @param scrubbedEntry
     */
    private void generateOffset(OriginEntry scrubbedEntry) {
        LOG.debug("generateOffset() started");

        // There was no previous unit of work so we need no offset
        if (scrubbedEntry == null) {
            return;
        }

        // If the offset amount is zero, don't bother to lookup the offset definition ...
        if (unitOfWork.offsetAmount.isZero()) {
            return;
        }

        // No offsets if it is a JV
        if ( "JV".equals(scrubbedEntry.getFinancialDocumentTypeCode()) ) {
            return;
        }

        // do nothing if flexible offset is enabled and scrubber offset indicator of the document
        // type code is turned off in the document type table
        String documentTypeCode = scrubbedEntry.getFinancialDocumentTypeCode();
        DocumentType documentType = documentTypeService.getDocumentTypeByCode(documentTypeCode);
        if ((!documentType.isTransactionScrubberOffsetGenerationIndicator()) && flexibleOffsetAccountService.getEnabled()) {
            return;
        }

        // Create an offset
        OriginEntry offsetEntry = new OriginEntry(scrubbedEntry);
        offsetEntry.setTransactionLedgerEntryDescription(offsetDescription);

        OffsetDefinition offsetDefinition = offsetDefinitionService.getByPrimaryId(scrubbedEntry.getUniversityFiscalYear(), scrubbedEntry.getChartOfAccountsCode(), scrubbedEntry.getFinancialDocumentTypeCode(), scrubbedEntry.getFinancialBalanceTypeCode());
        if (offsetDefinition != null) {
            if ( offsetDefinition.getFinancialObject() == null ) {
                StringBuffer offsetKey = new StringBuffer(offsetDefinition.getUniversityFiscalYear());
                offsetKey.append("-");
                offsetKey.append(offsetDefinition.getChartOfAccountsCode());
                offsetKey.append("-");
                offsetKey.append(offsetDefinition.getFinancialObjectCode());

                addTransactionError(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_OFFSET_DEFINITION_OBJECT_CODE_NOT_FOUND), offsetKey.toString(), Message.TYPE_FATAL);
                return;
            }

            // Flexible Offset Enhancement
            applyFlexibleOffsetGeneration(offsetEntry);

            offsetEntry.setFinancialObject(offsetDefinition.getFinancialObject());
            offsetEntry.setFinancialObjectCode(offsetDefinition.getFinancialObjectCode());

            if (!StringUtils.hasText(offsetDefinition.getFinancialSubObjectCode())) {
                offsetEntry.setFinancialSubObject(null);
                offsetEntry.setFinancialSubObjectCode(Constants.DASHES_SUB_OBJECT_CODE);
            }
            else {
                offsetEntry.setFinancialSubObject(offsetDefinition.getFinancialSubObject());
                offsetEntry.setFinancialSubObjectCode(offsetDefinition.getFinancialSubObjectCode());
            }
        }
        else {
            StringBuffer sb = new StringBuffer("a "); // This is to help identifiy problems since this error appears in many places
            sb.append(scrubbedEntry.getUniversityFiscalYear());
            sb.append("-");
            sb.append(scrubbedEntry.getChartOfAccountsCode());
            sb.append("-");
            sb.append(scrubbedEntry.getFinancialDocumentTypeCode());
            sb.append("-");
            sb.append(scrubbedEntry.getFinancialBalanceTypeCode());

            addTransactionError(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_OFFSET_DEFINITION_NOT_FOUND), sb.toString(), Message.TYPE_FATAL);
            return;
        }

        offsetEntry.setFinancialObjectTypeCode(offsetEntry.getFinancialObject().getFinancialObjectTypeCode());
        offsetEntry.setTransactionLedgerEntryAmount(unitOfWork.offsetAmount);

        if (unitOfWork.offsetAmount.isPositive()) {
            offsetEntry.setTransactionDebitCreditCode(Constants.GL_CREDIT_CODE);
        }
        else {
            offsetEntry.setTransactionDebitCreditCode(Constants.GL_DEBIT_CODE);
            offsetEntry.setTransactionLedgerEntryAmount(unitOfWork.offsetAmount.negated());
        }

        offsetEntry.setOrganizationDocumentNumber(null);
        offsetEntry.setOrganizationReferenceId(null);
        offsetEntry.setReferenceFinancialDocumentTypeCode(null);
        offsetEntry.setReferenceDocumentType(null);
        offsetEntry.setReferenceFinancialSystemOriginationCode(null);
        offsetEntry.setReferenceFinancialDocumentNumber(null);
        offsetEntry.setTransactionEncumbranceUpdateCode(null);
        offsetEntry.setProjectCode(Constants.DASHES_PROJECT_CODE);
        offsetEntry.setTransactionDate(runDate);

        createOutputEntry(offsetEntry, validGroup);
        scrubberReport.incrementOffsetEntryGenerated();
    }

    /**
     * This method determines if the flexible offsets need to be generated against the given transaction entry. If any, replace the
     * chart and account with the offset chart of accounts code and offset account number from the offset table (FP_ODST_ACC_T).
     * Then, pass the control to the module of the ordinary offset generation.
     * 
     * This code was not in the original COBOL. It is a Kuali enhancement
     * 
     * @param originEntry the transaction entry being processed
     */
    private void applyFlexibleOffsetGeneration(OriginEntry originEntry) {
        String keyOfErrorMessage = "";

        Integer fiscalYear = originEntry.getUniversityFiscalYear();
        String chartOfAccountsCode = originEntry.getChartOfAccountsCode();
        String accountNumber = originEntry.getAccountNumber();

        String balanceTypeCode = originEntry.getFinancialBalanceTypeCode();
        String documentTypeCode = originEntry.getFinancialDocumentTypeCode();

        // do nothing if the global flexible offset indicator is off
        if (!flexibleOffsetAccountService.getEnabled()) {
            return;
        }

        // do nothing if scrubber offset indicator is turned off in the document type table
        DocumentType documentType = documentTypeService.getDocumentTypeByCode(documentTypeCode);
        if (!documentType.isTransactionScrubberOffsetGenerationIndicator()) {
            return;
        }

        // look up the offset definition table for the offset object code
        OffsetDefinition offsetDefinition = offsetDefinitionService.getByPrimaryId(fiscalYear, chartOfAccountsCode, documentTypeCode, balanceTypeCode);
        if (offsetDefinition == null) {
            StringBuffer errorValues = new StringBuffer("b ");
            errorValues.append(fiscalYear);
            errorValues.append("-");
            errorValues.append(chartOfAccountsCode);
            errorValues.append("-");
            errorValues.append(documentTypeCode);
            errorValues.append("-");
            errorValues.append(balanceTypeCode);

            keyOfErrorMessage = kualiConfigurationService.getPropertyString(KeyConstants.ERROR_OFFSET_DEFINITION_NOT_FOUND);
            addTransactionError(keyOfErrorMessage, errorValues.toString(), Message.TYPE_FATAL);
            return;
        }

        // get the offset object code from the offset definition record searched above
        String offsetObjectCode = offsetDefinition.getFinancialObjectCode();
        if (offsetObjectCode == null) {
            keyOfErrorMessage = kualiConfigurationService.getPropertyString(KeyConstants.ERROR_OFFSET_DEFINITION_OBJECT_CODE_NOT_FOUND);
            addTransactionError(keyOfErrorMessage, "", Message.TYPE_FATAL);
            return;
        }

        // do nothing if there is no the offset account with the given chart of accounts code,
        // account number and offset object code in the offset table.
        OffsetAccount offsetAccount = flexibleOffsetAccountService.getByPrimaryIdIfEnabled(chartOfAccountsCode, accountNumber, offsetObjectCode);
        if (offsetAccount == null) {
            StringBuffer errorValues = new StringBuffer();
            errorValues.append("chart of account code = " + chartOfAccountsCode + "; ");
            errorValues.append("account number = " + accountNumber + "; ");
            errorValues.append("offset object code = " + offsetObjectCode + "; ");

            keyOfErrorMessage = kualiConfigurationService.getPropertyString(KeyConstants.ERROR_OFFSET_ACCOUNT_NOT_FOUND);
            addTransactionError(keyOfErrorMessage, errorValues.toString(), Message.TYPE_FATAL);
            return;
        }
        String offserAccountNumber = offsetAccount.getFinancialOffsetAccountNumber();
        String offsetChartOfAccountsCode = offsetAccount.getFinancialOffsetChartOfAccountCode();

        // validate if there is a record with the offset chart and offset object code in the object code table
        if (!chartOfAccountsCode.equals(offsetChartOfAccountsCode)) {
            ObjectCode objectCode = objectCodeService.getByPrimaryId(fiscalYear, offsetChartOfAccountsCode, offsetObjectCode);
            if (objectCode == null) {
                StringBuffer errorValues = new StringBuffer();
                errorValues.append("chart of account code = " + chartOfAccountsCode + "; ");
                errorValues.append("object code = " + offsetObjectCode + "; ");

                keyOfErrorMessage = kualiConfigurationService.getPropertyString(KeyConstants.ERROR_OBJECT_CODE_NOT_FOUND);
                addTransactionError(keyOfErrorMessage, errorValues.toString(), Message.TYPE_FATAL);
                return;
            }
        }

        // replace the chart and account of the given transaction with those of the offset account obtained above
        originEntry.setAccountNumber(offserAccountNumber);
        originEntry.setChartOfAccountsCode(offsetChartOfAccountsCode);

        // blank out the sub account and sub object since the account has been replaced
        originEntry.setSubAccountNumber(Constants.DASHES_SUB_ACCOUNT_NUMBER);
        originEntry.setFinancialSubObjectCode(Constants.DASHES_SUB_OBJECT_CODE);

        // indicate that the entry is a flexible offset with the meaningful description
        originEntry.setTransactionLedgerEntryDescription(offsetDescription);

        return;
    }

    /**
     * Save an entry in origin entry
     * 
     * @param entry Entry to save
     * @param group Group to save it in
     */
    private void createOutputEntry(OriginEntry entry, OriginEntryGroup group) {
        if (!reportOnlyMode) {
            entry.setGroup(group);
            originEntryService.save(entry);
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
        public Date fdocReversalDt = new Date(new java.util.Date().getTime());
        public String univFiscalPrdCd = "";

        // Data about unit of work
        public boolean entryMode = true;
        public KualiDecimal offsetAmount = KualiDecimal.ZERO;
        public String scrbFinCoaCd;
        public String scrbAccountNbr;

        public UnitOfWorkInfo() {
        }

        public UnitOfWorkInfo(OriginEntry e) {
            univFiscalYr = e.getUniversityFiscalYear();
            finCoaCd = e.getChartOfAccountsCode();
            accountNbr = e.getAccountNumber();
            subAcctNbr = e.getSubAccountNumber();
            finBalanceTypCd = e.getFinancialBalanceTypeCode();
            fdocTypCd = e.getFinancialDocumentTypeCode();
            fsOriginCd = e.getFinancialSystemOriginationCode();
            fdocNbr = e.getFinancialDocumentNumber();
            fdocReversalDt = e.getFinancialDocumentReversalDate();
            univFiscalPrdCd = e.getUniversityFiscalPeriodCode();
        }

        public boolean isSameUnitOfWork(OriginEntry e) {
            // Compare the key fields
            return univFiscalYr.equals(e.getUniversityFiscalYear()) && finCoaCd.equals(e.getChartOfAccountsCode()) && accountNbr.equals(e.getAccountNumber()) && subAcctNbr.equals(e.getSubAccountNumber()) && finBalanceTypCd.equals(e.getFinancialBalanceTypeCode()) && fdocTypCd.equals(e.getFinancialDocumentTypeCode()) && fsOriginCd.equals(e.getFinancialSystemOriginationCode()) && fdocNbr.equals(e.getFinancialDocumentNumber()) && ObjectHelper.isEqual(fdocReversalDt, e.getFinancialDocumentReversalDate()) && univFiscalPrdCd.equals(e.getUniversityFiscalPeriodCode());
        }

        public String toString() {
            return univFiscalYr + finCoaCd + accountNbr + subAcctNbr + finBalanceTypCd + fdocTypCd + fsOriginCd + fdocNbr + fdocReversalDt + univFiscalPrdCd;
        }

        public OriginEntry getOffsetTemplate() {
            OriginEntry e = new OriginEntry();
            e.setUniversityFiscalYear(univFiscalYr);
            e.setChartOfAccountsCode(finCoaCd);
            e.setAccountNumber(accountNbr);
            e.setSubAccountNumber(subAcctNbr);
            e.setFinancialBalanceTypeCode(finBalanceTypCd);
            e.setFinancialDocumentTypeCode(fdocTypCd);
            e.setFinancialSystemOriginationCode(fsOriginCd);
            e.setFinancialDocumentNumber(fdocNbr);
            e.setFinancialDocumentReversalDate(fdocReversalDt);
            e.setUniversityFiscalPeriodCode(univFiscalPrdCd);
            return e;
        }
    }

    class TransactionError {
        public Transaction transaction;
        public Message message;
        public TransactionError(Transaction t,Message m) {
            transaction = t;
            message = m;
        }
    }
}
