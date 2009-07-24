/*
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
package org.kuali.kfs.gl.batch.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.metadata.MetadataManager;
import org.kuali.kfs.coa.businessobject.A21SubAccount;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryRate;
import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryRateDetail;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.OffsetDefinition;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.dataaccess.IndirectCostRecoveryRateDetailDao;
import org.kuali.kfs.coa.service.AccountingPeriodService;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.coa.service.OffsetDefinitionService;
import org.kuali.kfs.coa.service.SubAccountService;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.batch.PosterIndirectCostRecoveryEntriesStep;
import org.kuali.kfs.gl.batch.service.AccountingCycleCachingService;
import org.kuali.kfs.gl.batch.service.PostTransaction;
import org.kuali.kfs.gl.batch.service.PosterService;
import org.kuali.kfs.gl.batch.service.RunDateService;
import org.kuali.kfs.gl.batch.service.VerifyTransaction;
import org.kuali.kfs.gl.businessobject.ExpenditureTransaction;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.gl.businessobject.OriginEntryInformation;
import org.kuali.kfs.gl.businessobject.Reversal;
import org.kuali.kfs.gl.businessobject.Transaction;
import org.kuali.kfs.gl.dataaccess.ExpenditureTransactionDao;
import org.kuali.kfs.gl.dataaccess.ReversalDao;
import org.kuali.kfs.gl.report.LedgerSummaryReport;
import org.kuali.kfs.gl.report.TransactionListingReport;
import org.kuali.kfs.gl.service.OriginEntryGroupService;
import org.kuali.kfs.gl.service.OriginEntryService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.Message;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.businessobject.UniversityDate;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.dataaccess.UniversityDateDao;
import org.kuali.kfs.sys.exception.InvalidFlexibleOffsetException;
import org.kuali.kfs.sys.service.FlexibleOffsetAccountService;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.service.PersistenceService;
import org.kuali.rice.kns.service.PersistenceStructureService;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * The base implementation of PosterService
 */
@Transactional
public class PosterServiceImpl implements PosterService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PosterServiceImpl.class);

    public static final KualiDecimal WARNING_MAX_DIFFERENCE = new KualiDecimal("0.03");
    public static final String DATE_FORMAT_STRING = "yyyyMMdd";

    private List transactionPosters;
    private VerifyTransaction verifyTransaction;
    private OriginEntryService originEntryService;
    private OriginEntryGroupService originEntryGroupService;
    private DateTimeService dateTimeService;
    private ReversalDao reversalDao;
    private UniversityDateDao universityDateDao;
    private AccountingPeriodService accountingPeriodService;
    private ExpenditureTransactionDao expenditureTransactionDao;
    private IndirectCostRecoveryRateDetailDao indirectCostRecoveryRateDetailDao;
    private ObjectCodeService objectCodeService;
    private ParameterService parameterService;
    private KualiConfigurationService configurationService;
    private FlexibleOffsetAccountService flexibleOffsetAccountService;
    private RunDateService runDateService;
    private SubAccountService subAccountService;
    private OffsetDefinitionService offsetDefinitionService;
    private DataDictionaryService dataDictionaryService;
    private BusinessObjectService businessObjectService;
    private PersistenceStructureService persistenceStructureService;
    private ReportWriterService reportWriterService;
    private ReportWriterService errorListingReportWriterService;
    private ReportWriterService reversalReportWriterService;
    private ReportWriterService ledgerSummaryReportWriterService;
    
    //private File OUTPUT_ERR_FILE;
    //private PrintStream OUTPUT_ERR_FILE_ps;
    //private PrintStream OUTPUT_GLE_FILE_ps;
    private String batchFileDirectoryName;
    //private BufferedReader INPUT_GLE_FILE_br = null;
    //private FileReader INPUT_GLE_FILE = null;
    private AccountingCycleCachingService accountingCycleCachingService;

    /**
     * Post scrubbed GL entries to GL tables.
     */
    public void postMainEntries() {
        LOG.debug("postMainEntries() started");
        Date runDate = dateTimeService.getCurrentSqlDate();
        try{
            FileReader INPUT_GLE_FILE = new FileReader(batchFileDirectoryName + File.separator +  GeneralLedgerConstants.BatchFileSystem.POSTER_INPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION);
            File OUTPUT_ERR_FILE = new File(batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.POSTER_ERROR_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION);    
            
            postEntries(PosterService.MODE_ENTRIES, INPUT_GLE_FILE, null, OUTPUT_ERR_FILE);
            
            INPUT_GLE_FILE.close();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
            throw new RuntimeException("PosterMainEntries Stopped: " + e1.getMessage(), e1);
        } catch (IOException ioe) {
            LOG.error("postMainEntries stopped due to: " + ioe.getMessage(), ioe);
            throw new RuntimeException(ioe);
        }
    }

    /**
     * Post reversal GL entries to GL tables.
     */
    public void postReversalEntries() {
        LOG.debug("postReversalEntries() started");
        Date runDate = dateTimeService.getCurrentSqlDate();
        try{
            PrintStream OUTPUT_GLE_FILE_ps = new PrintStream(batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.REVERSAL_POSTER_VALID_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION);
            File OUTPUT_ERR_FILE = new File(batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.REVERSAL_POSTER_ERROR_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION);
            
            postEntries(PosterService.MODE_REVERSAL, null, OUTPUT_GLE_FILE_ps, OUTPUT_ERR_FILE);
            
            OUTPUT_GLE_FILE_ps.close();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
            throw new RuntimeException("PosterReversalEntries Stopped: " + e1.getMessage(), e1);
        }
    }

    /**
     * Post ICR GL entries to GL tables.
     */
    public void postIcrEntries() {
        LOG.debug("postIcrEntries() started");
        Date runDate = dateTimeService.getCurrentSqlDate();
        try{
            FileReader INPUT_GLE_FILE = new FileReader(batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.ICR_POSTER_INPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION);
            File OUTPUT_ERR_FILE = new File(batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.ICR_POSTER_ERROR_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION);
        
            postEntries(PosterService.MODE_ICR, INPUT_GLE_FILE, null, OUTPUT_ERR_FILE);
            
            INPUT_GLE_FILE.close();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
            throw new RuntimeException("PosterIcrEntries Stopped: " + e1.getMessage(), e1);
        } catch (IOException ioe) {
            LOG.error("postIcrEntries stopped due to: " + ioe.getMessage(), ioe);
            throw new RuntimeException(ioe);
        }
    }

    /**
     * Actually post the entries. The mode variable decides which entries to post.
     * 
     * @param mode the poster's current run mode
     */
    private void postEntries(int mode, FileReader INPUT_GLE_FILE, PrintStream OUTPUT_GLE_FILE_ps, File OUTPUT_ERR_FILE) throws FileNotFoundException {
        if (LOG.isDebugEnabled()) {
            LOG.debug("postEntries() started");
        }
        
        PrintStream OUTPUT_ERR_FILE_ps = new PrintStream(OUTPUT_ERR_FILE);
        BufferedReader INPUT_GLE_FILE_br = null;
        if (INPUT_GLE_FILE != null) {
            INPUT_GLE_FILE_br = new BufferedReader(INPUT_GLE_FILE);
        }
        
        String GLEN_RECORD;
        Date executionDate = new Date(dateTimeService.getCurrentDate().getTime());
        Date runDate = new Date(runDateService.calculateRunDate(executionDate).getTime());
        UniversityDate runUniversityDate = universityDateDao.getByPrimaryKey(runDate);
        LedgerSummaryReport ledgerSummaryReport = new LedgerSummaryReport();

        // Build the summary map so all the possible combinations of destination & operation
        // are included in the summary part of the report.
        Map reportSummary = new HashMap();
        for (Iterator posterIter = transactionPosters.iterator(); posterIter.hasNext();) {
            PostTransaction poster = (PostTransaction) posterIter.next();
            reportSummary.put(poster.getDestinationName() + "," + GeneralLedgerConstants.DELETE_CODE, new Integer(0));
            reportSummary.put(poster.getDestinationName() + "," + GeneralLedgerConstants.INSERT_CODE, new Integer(0));
            reportSummary.put(poster.getDestinationName() + "," + GeneralLedgerConstants.UPDATE_CODE, new Integer(0));
        }
        int ecount = 0;
        try {
            if ((mode == PosterService.MODE_ENTRIES) || (mode == PosterService.MODE_ICR)) {
                LOG.debug("postEntries() Processing groups");
                while ((GLEN_RECORD = INPUT_GLE_FILE_br.readLine()) != null) {
                    if (!org.apache.commons.lang.StringUtils.isEmpty(GLEN_RECORD) && !org.apache.commons.lang.StringUtils.isBlank(GLEN_RECORD.trim())) {
                        ecount++;

                        GLEN_RECORD = org.apache.commons.lang.StringUtils.rightPad(GLEN_RECORD, 183, ' ');
                        OriginEntryFull tran = new OriginEntryFull();

                        // checking parsing process and stop poster when it has errors. 
                        List<Message> parsingError = new ArrayList();
                        parsingError = tran.setFromTextFileForBatch(GLEN_RECORD, ecount);
                        if (parsingError.size() > 0) {
                            String messages = "";
                            for(Message msg : parsingError) {messages += msg + " ";}
                            throw new RuntimeException("Exception happened from parsing process: " + messages);
                        }
                        // need to pass ecount for building better message
                        addReporting(reportSummary, "SEQUENTIAL", GeneralLedgerConstants.SELECT_CODE);
                        postTransaction(tran, mode, reportSummary, ledgerSummaryReport, OUTPUT_ERR_FILE_ps, runUniversityDate, GLEN_RECORD, OUTPUT_GLE_FILE_ps);
                        
                        if (ecount % 1000 == 0) {
                            LOG.info("postEntries() Posted Entry " + ecount);
                        }
                    }
                }
                if (INPUT_GLE_FILE_br != null) {    
                    INPUT_GLE_FILE_br.close();
                }
                OUTPUT_ERR_FILE_ps.close();
                reportWriterService.writeStatisticLine("SEQUENTIAL RECORDS READ                    %,9d", reportSummary.get("SEQUENTIAL,S"));
            }
            else {
                LOG.debug("postEntries() Processing reversal transactions");
                
                final String GL_REVERSAL_T = MetadataManager.getInstance().getGlobalRepository().getDescriptorFor(Reversal.class).getFullTableName();
                Iterator reversalTransactions = reversalDao.getByDate(runDate);
                TransactionListingReport reversalListingReport = new TransactionListingReport();
                while (reversalTransactions.hasNext()) {
                    ecount++;
                    Transaction tran = (Transaction) reversalTransactions.next();
                    addReporting(reportSummary, GL_REVERSAL_T, GeneralLedgerConstants.SELECT_CODE);

                    boolean posted = postTransaction(tran, mode, reportSummary, ledgerSummaryReport, OUTPUT_ERR_FILE_ps, runUniversityDate, GL_REVERSAL_T, OUTPUT_GLE_FILE_ps);
                    
                    if (posted) {
                        reversalListingReport.generateReport(reversalReportWriterService, tran);
                    }
                    
                    if (ecount % 1000 == 0) {
                        LOG.info("postEntries() Posted Entry " + ecount);
                    }
                }
                
                OUTPUT_ERR_FILE_ps.close();
                reportWriterService.writeStatisticLine("GLRV RECORDS READ (GL_REVERSAL_T)          %,9d", reportSummary.get("GL_REVERSAL_T,S"));
                reversalListingReport.generateStatistics(reversalReportWriterService);
            }
            
            //PDF version had this abstracted to print I/U/D for each table in 7 posters, but some statistics are meaningless (i.e. GLEN is never updated), so un-abstracted here
            reportWriterService.writeStatisticLine("GLEN RECORDS INSERTED (GL_ENTRY_T)         %,9d", reportSummary.get("GL_ENTRY_T,I"));
            reportWriterService.writeStatisticLine("GLBL RECORDS INSERTED (GL_BALANCE_T)       %,9d", reportSummary.get("GL_BALANCE_T,I"));
            reportWriterService.writeStatisticLine("GLBL RECORDS UPDATED  (GL_BALANCE_T)       %,9d", reportSummary.get("GL_BALANCE_T,U"));
            reportWriterService.writeStatisticLine("GLEX RECORDS INSERTED (GL_EXPEND_TRN_T)    %,9d", reportSummary.get("GL_EXPEND_TRN_T,I"));
            reportWriterService.writeStatisticLine("GLEX RECORDS UPDATED  (GL_EXPEND_TRN_T)    %,9d", reportSummary.get("GL_EXPEND_TRN_T,U"));
            reportWriterService.writeStatisticLine("GLEC RECORDS INSERTED (GL_ENCUMBRANCE_T)   %,9d", reportSummary.get("GL_ENCUMBRANCE_T,I"));
            reportWriterService.writeStatisticLine("GLEC RECORDS UPDATED  (GL_ENCUMBRANCE_T)   %,9d", reportSummary.get("GL_ENCUMBRANCE_T,U"));
            reportWriterService.writeStatisticLine("GLRV RECORDS INSERTED (GL_REVERSAL_T)      %,9d", reportSummary.get("GL_REVERSAL_T,I"));
            reportWriterService.writeStatisticLine("GLRV RECORDS DELETED  (GL_REVERSAL_T)      %,9d", reportSummary.get("GL_REVERSAL_T,D"));
            reportWriterService.writeStatisticLine("SFBL RECORDS INSERTED (GL_SF_BALANCES_T)   %,9d", reportSummary.get("GL_SF_BALANCES_T,I"));
            reportWriterService.writeStatisticLine("SFBL RECORDS UPDATED  (GL_SF_BALANCES_T)   %,9d", reportSummary.get("GL_SF_BALANCES_T,U"));
            reportWriterService.writeStatisticLine("ACBL RECORDS INSERTED (GL_ACCT_BALANCES_T) %,9d", reportSummary.get("GL_ACCT_BALANCES_T,I"));
            reportWriterService.writeStatisticLine("ACBL RECORDS UPDATED  (GL_ACCT_BALANCES_T) %,9d", reportSummary.get("GL_ACCT_BALANCES_T,U"));
            reportWriterService.writeStatisticLine("WARNING RECORDS WRITTEN                    %,9d", reportSummary.get("WARNING,I"));
        }
        catch (RuntimeException re) {
            LOG.error("postEntries stopped due to: " + re.getMessage() + " on line number : " + ecount, re);
            throw new RuntimeException("PosterService Stopped: " + re.getMessage(), re);
        }
        catch (IOException e) {
            LOG.error("postEntries stopped due to: " + e.getMessage(), e);
            throw new RuntimeException(e);
        }
        catch (Exception e) {
            // do nothing - handled in postTransaction method.
        }

        LOG.info("postEntries() done, total count = " + ecount);
        // Generate the reports
        ledgerSummaryReport.writeReport(ledgerSummaryReportWriterService);
        new TransactionListingReport().generateReport(errorListingReportWriterService, new OriginEntryFileIterator(OUTPUT_ERR_FILE));
    }

    /**
     * Runs the given transaction through each transaction posting algorithms associated with this instance
     * 
     * @param tran a transaction to post
     * @param mode the mode the poster is running in
     * @param reportSummary a Map of summary counts generated by the posting process
     * @param ledgerSummaryReport for summary reporting
     * @param invalidGroup the group to save invalid entries to
     * @param runUniversityDate the university date of this poster run
     * @param line
     * @return whether the transaction was posted or not. Useful if calling class attempts to report on the transaction
     */
    private boolean postTransaction(Transaction tran, int mode, Map<String,Integer> reportSummary, LedgerSummaryReport ledgerSummaryReport, PrintStream invalidGroup, UniversityDate runUniversityDate, String line, PrintStream OUTPUT_GLE_FILE_ps) {

        List<Message> errors = new ArrayList();
        Transaction originalTransaction = tran;

        try {
            final String GL_ORIGIN_ENTRY_T = MetadataManager.getInstance().getGlobalRepository().getDescriptorFor(OriginEntryFull.class).getFullTableName();

            // Update select count in the report
            if ((mode == PosterService.MODE_ENTRIES) || (mode == PosterService.MODE_ICR)) {
                addReporting(reportSummary, GL_ORIGIN_ENTRY_T, GeneralLedgerConstants.SELECT_CODE);
            }
            // If these are reversal entries, we need to reverse the entry and
            // modify a few fields
            if (mode == PosterService.MODE_REVERSAL) {
                Reversal reversal = new Reversal(tran);
                // Reverse the debit/credit code
                if (KFSConstants.GL_DEBIT_CODE.equals(reversal.getTransactionDebitCreditCode())) {
                    reversal.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
                }
                else if (KFSConstants.GL_CREDIT_CODE.equals(reversal.getTransactionDebitCreditCode())) {
                    reversal.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
                }
                UniversityDate udate = universityDateDao.getByPrimaryKey(reversal.getFinancialDocumentReversalDate());
                if (udate != null) {
                    reversal.setUniversityFiscalYear(udate.getUniversityFiscalYear());
                    reversal.setUniversityFiscalPeriodCode(udate.getUniversityFiscalAccountingPeriod());
                    AccountingPeriod ap = accountingPeriodService.getByPeriod(reversal.getUniversityFiscalPeriodCode(), reversal.getUniversityFiscalYear());
                    if (ap != null) {
                        if (!ap.isActive()) { // Make sure accounting period is closed
                            reversal.setUniversityFiscalYear(runUniversityDate.getUniversityFiscalYear());
                            reversal.setUniversityFiscalPeriodCode(runUniversityDate.getUniversityFiscalAccountingPeriod());
                        }
                        reversal.setFinancialDocumentReversalDate(null);
                        String newDescription = KFSConstants.GL_REVERSAL_DESCRIPTION_PREFIX + reversal.getTransactionLedgerEntryDescription();
                        if (newDescription.length() > 40) {
                            newDescription = newDescription.substring(0, 40);
                        }
                        reversal.setTransactionLedgerEntryDescription(newDescription);
                    }
                    else {
                        errors.add(new Message(configurationService.getPropertyString(KFSKeyConstants.ERROR_UNIV_DATE_NOT_IN_ACCOUNTING_PERIOD_TABLE), Message.TYPE_WARNING));
                    }
                }
                else {
                    errors.add(new Message (configurationService.getPropertyString(KFSKeyConstants.ERROR_REVERSAL_DATE_NOT_IN_UNIV_DATE_TABLE) , Message.TYPE_WARNING));
                }
                // Make sure the row will be unique when adding to the entries table by adjusting the transaction sequence id
                int maxSequenceId = accountingCycleCachingService.getMaxSequenceNumber(reversal);
                reversal.setTransactionLedgerEntrySequenceNumber(new Integer(maxSequenceId + 1));

                PersistenceService ps = SpringContext.getBean(PersistenceService.class);
                ps.retrieveNonKeyFields(reversal);
                tran = reversal;
            }
            else {
                tran.setChart(accountingCycleCachingService.getChart(tran.getChartOfAccountsCode()));
                tran.setAccount(accountingCycleCachingService.getAccount(tran.getChartOfAccountsCode(), tran.getAccountNumber()));
                tran.setObjectType(accountingCycleCachingService.getObjectType(tran.getFinancialObjectTypeCode()));
                tran.setBalanceType(accountingCycleCachingService.getBalanceType(tran.getFinancialBalanceTypeCode()));
                tran.setOption(accountingCycleCachingService.getSystemOptions(tran.getUniversityFiscalYear()));
                tran.setFinancialObject(accountingCycleCachingService.getObjectCode(tran.getUniversityFiscalYear(), tran.getChartOfAccountsCode(), tran.getFinancialObjectCode()));
                // Make sure the row will be unique when adding to the entries table by adjusting the transaction sequence id
                int maxSequenceId = accountingCycleCachingService.getMaxSequenceNumber(tran);
                ((OriginEntryFull) tran).setTransactionLedgerEntrySequenceNumber(new Integer(maxSequenceId + 1));
            }

            if (errors.size() == 0) {
                try {
                    errors = verifyTransaction.verifyTransaction(tran);
                }
                catch (Exception e) {
                    errors.add(new Message(e.toString() + " occurred for this record.", Message.TYPE_FATAL));
                }
            }

            //init warning entry number
            reportSummary.put("WARNING" + "," + GeneralLedgerConstants.INSERT_CODE, new Integer(0));

            if (errors.size() > 0) {
                // Error on this transaction
                reportWriterService.writeError(tran, errors);
                addReporting(reportSummary, "WARNING", GeneralLedgerConstants.INSERT_CODE);
                try {
                    writeErrorEntry(line, invalidGroup);
                }
                catch (IOException ioe) {
                    LOG.error("PosterServiceImpl Stopped: " + ioe.getMessage());
                    throw new RuntimeException("PosterServiceImpl Stopped: " + ioe.getMessage(), ioe);
                }
            }
            else {
                // No error so post it
                for (Iterator posterIter = transactionPosters.iterator(); posterIter.hasNext();) {
                    PostTransaction poster = (PostTransaction) posterIter.next();
                    String actionCode = poster.post(tran, mode, runUniversityDate.getUniversityDate());

                    if (actionCode.startsWith(GeneralLedgerConstants.ERROR_CODE)) {
                        errors = new ArrayList<Message>();
                        errors.add(new Message(actionCode, Message.TYPE_WARNING));
                        reportWriterService.writeError(tran, errors);
                    }
                    else if (actionCode.indexOf(GeneralLedgerConstants.INSERT_CODE) >= 0) {
                        addReporting(reportSummary, poster.getDestinationName(), GeneralLedgerConstants.INSERT_CODE);
                    }
                    else if (actionCode.indexOf(GeneralLedgerConstants.UPDATE_CODE) >= 0) {
                        addReporting(reportSummary, poster.getDestinationName(), GeneralLedgerConstants.UPDATE_CODE);
                    }
                    else if (actionCode.indexOf(GeneralLedgerConstants.DELETE_CODE) >= 0) {
                        addReporting(reportSummary, poster.getDestinationName(), GeneralLedgerConstants.DELETE_CODE);
                    }
                    else if (actionCode.indexOf(GeneralLedgerConstants.SELECT_CODE) >= 0) {
                        addReporting(reportSummary, poster.getDestinationName(), GeneralLedgerConstants.SELECT_CODE);
                    }
                }
                if (errors.size() == 0) {
                    // Delete the reversal entry
                    if (mode == PosterService.MODE_REVERSAL) {
                        createOutputEntry(tran, OUTPUT_GLE_FILE_ps);
                        reversalDao.delete((Reversal) originalTransaction);
                        addReporting(reportSummary, MetadataManager.getInstance().getGlobalRepository().getDescriptorFor(Reversal.class).getFullTableName(), GeneralLedgerConstants.DELETE_CODE);
                    }
                    
                    ledgerSummaryReport.summarizeEntry(new OriginEntryFull(tran));
                    return true;
                }
            }
            
            return false;
        }
        catch (IOException ioe) {
            LOG.error("PosterServiceImpl Stopped: " + ioe.getMessage());
            throw new RuntimeException("PosterServiceImpl Stopped: " + ioe.getMessage(), ioe);

        }
        catch (RuntimeException re) {
            LOG.error("PosterServiceImpl Stopped: " + re.getMessage());
            throw new RuntimeException("PosterServiceImpl Stopped: " + re.getMessage(), re);
        }
    }

    /**
     * This step reads the expenditure table and uses the data to generate Indirect Cost Recovery transactions.
     */
    public void generateIcrTransactions() {
        LOG.debug("generateIcrTransactions() started");

        Date executionDate = dateTimeService.getCurrentSqlDate();
        Date runDate = new Date(runDateService.calculateRunDate(executionDate).getTime());

        try {
            PrintStream OUTPUT_GLE_FILE_ps = new PrintStream(batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.ICR_TRANSACTIONS_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION);
            
            int reportExpendTranRetrieved = 0;
            int reportExpendTranDeleted = 0;
            int reportExpendTranKept = 0;
            int reportOriginEntryGenerated = 0;
            Iterator expenditureTransactions;
            
            try {
                expenditureTransactions = expenditureTransactionDao.getAllExpenditureTransactions();
            }
            catch (RuntimeException re) {
                LOG.error("generateIcrTransactions Stopped: " + re.getMessage());
                throw new RuntimeException("generateIcrTransactions Stopped: " + re.getMessage(), re);
            }

            while (expenditureTransactions.hasNext()) {
                ExpenditureTransaction et = new ExpenditureTransaction();
                try {
                    et = (ExpenditureTransaction) expenditureTransactions.next();
                    reportExpendTranRetrieved++;

                    KualiDecimal transactionAmount = et.getAccountObjectDirectCostAmount();
                    KualiDecimal distributionAmount = KualiDecimal.ZERO;

                    if (shouldIgnoreExpenditureTransaction(et)) {
                        continue;
                    }
                    
                    IndirectCostRecoveryGenerationMetadata icrGenerationMetadata = retrieveSubAccountIndirectCostRecoveryMetadata(et);
                    if (icrGenerationMetadata == null) {
                        // ICR information was not set up properly for sub-account, default to using ICR information from the account
                        icrGenerationMetadata = retrieveAccountIndirectCostRecoveryMetadata(et);
                    }

                    Collection<IndirectCostRecoveryRateDetail> automatedEntries = indirectCostRecoveryRateDetailDao.getActiveRateDetailsByRate(et.getUniversityFiscalYear(), icrGenerationMetadata.getFinancialIcrSeriesIdentifier());
                    int automatedEntriesCount = automatedEntries.size();

                    if (automatedEntriesCount > 0) {
                        for (Iterator icrIter = automatedEntries.iterator(); icrIter.hasNext();) {
                            IndirectCostRecoveryRateDetail icrEntry = (IndirectCostRecoveryRateDetail) icrIter.next();
                            KualiDecimal generatedTransactionAmount = null;

                            if (!icrIter.hasNext()) {
                                generatedTransactionAmount = distributionAmount;

                                // Log differences that are over WARNING_MAX_DIFFERENCE
                                if (getPercentage(transactionAmount, icrEntry.getAwardIndrCostRcvyRatePct()).subtract(distributionAmount).abs().isGreaterThan(WARNING_MAX_DIFFERENCE)) {
                                    List<Message> warnings = new ArrayList<Message>();
                                    warnings.add(new Message("ADJUSTMENT GREATER THAN " + WARNING_MAX_DIFFERENCE, Message.TYPE_WARNING));
                                    reportWriterService.writeError(et, warnings);
                                }
                            }
                            else if (icrEntry.getTransactionDebitIndicator().equals(KFSConstants.GL_DEBIT_CODE)) {
                                generatedTransactionAmount = getPercentage(transactionAmount, icrEntry.getAwardIndrCostRcvyRatePct());
                                distributionAmount = distributionAmount.add(generatedTransactionAmount);
                            }
                            else if (icrEntry.getTransactionDebitIndicator().equals(KFSConstants.GL_CREDIT_CODE)) {
                                generatedTransactionAmount = getPercentage(transactionAmount, icrEntry.getAwardIndrCostRcvyRatePct());
                                distributionAmount = distributionAmount.subtract(generatedTransactionAmount);
                            }
                            else {
                                // Log if D / C code not found
                                List<Message> warnings = new ArrayList<Message>();
                                warnings.add(new Message("DEBIT OR CREDIT CODE NOT FOUND", Message.TYPE_FATAL));
                                reportWriterService.writeError(et, warnings);
                            }

                            generateTransactions(et, icrEntry, generatedTransactionAmount, runDate, OUTPUT_GLE_FILE_ps, icrGenerationMetadata);
                            
                            reportOriginEntryGenerated = reportOriginEntryGenerated + 2;
                        }
                    }
                    // Delete expenditure record
                    expenditureTransactionDao.delete(et);
                    reportExpendTranDeleted++;

                }
                catch (RuntimeException re) {
                    LOG.error("generateIcrTransactions Stopped: " + re.getMessage());
                    throw new RuntimeException("generateIcrTransactions Stopped: " + re.getMessage(), re);
                }
                catch (Exception e) {
                    List errorList = new ArrayList();
                    errorList.add(new Message(e.toString() + " occurred for this record.", Message.TYPE_FATAL));
                    reportWriterService.writeError(et, errorList);
                }
            }
            OUTPUT_GLE_FILE_ps.close();
            reportWriterService.writeStatisticLine("GLEX RECORDS READ               (GL_EXPEND_TRN_T) %,9d", reportExpendTranRetrieved);
            reportWriterService.writeStatisticLine("GLEX RECORDS DELETED            (GL_EXPEND_TRN_T) %,9d", reportExpendTranDeleted);
            reportWriterService.writeStatisticLine("GLEX RECORDS KEPT DUE TO ERRORS (GL_EXPEND_TRN_T) %,9d", reportExpendTranKept);
            reportWriterService.writeStatisticLine("TRANSACTIONS GENERATED                            %,9d", reportOriginEntryGenerated);
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException("generateIcrTransactions Stopped: " + e.getMessage(), e);
        }
    }

    /**
     * Generate a transfer transaction and an offset transaction
     * 
     * @param et an expenditure transaction
     * @param icrEntry the indirect cost recovery entry
     * @param generatedTransactionAmount the amount of the transaction
     * @param runDate the transaction date for the newly created origin entry
     * @param group the group to save the origin entry to
     */
    private void generateTransactions(ExpenditureTransaction et, IndirectCostRecoveryRateDetail icrRateDetail, KualiDecimal generatedTransactionAmount, Date runDate, PrintStream group, IndirectCostRecoveryGenerationMetadata icrGenerationMetadata) {

        BigDecimal pct = new BigDecimal(icrRateDetail.getAwardIndrCostRcvyRatePct().toString());
        pct = pct.divide(BDONEHUNDRED);

        OriginEntryFull e = new OriginEntryFull();
        e.setTransactionLedgerEntrySequenceNumber(0);

        // SYMBOL_USE_EXPENDITURE_ENTRY means we use the field from the expenditure entry, SYMBOL_USE_IRC_FROM_ACCOUNT
        // means we use the ICR field from the account record, otherwise, use the field in the icrRateDetail
        if (GeneralLedgerConstants.PosterService.SYMBOL_USE_EXPENDITURE_ENTRY.equals(icrRateDetail.getFinancialObjectCode()) || GeneralLedgerConstants.PosterService.SYMBOL_USE_ICR_FROM_ACCOUNT.equals(icrRateDetail.getFinancialObjectCode())) {
            e.setFinancialObjectCode(et.getObjectCode());
            e.setFinancialSubObjectCode(et.getSubObjectCode());
        }
        else {
            e.setFinancialObjectCode(icrRateDetail.getFinancialObjectCode());
                e.setFinancialSubObjectCode(icrRateDetail.getFinancialSubObjectCode());
            }

        if (GeneralLedgerConstants.PosterService.SYMBOL_USE_EXPENDITURE_ENTRY.equals(icrRateDetail.getAccountNumber())) {
            e.setAccountNumber(et.getAccountNumber());
            e.setChartOfAccountsCode(et.getChartOfAccountsCode());
            e.setSubAccountNumber(et.getSubAccountNumber());
        }
        else if (GeneralLedgerConstants.PosterService.SYMBOL_USE_ICR_FROM_ACCOUNT.equals(icrRateDetail.getAccountNumber())) {
            e.setAccountNumber(icrGenerationMetadata.getIndirectCostRecoveryAccountNumber());
            e.setChartOfAccountsCode(icrGenerationMetadata.getIndirectCostRecoveryChartOfAccountsCode());
            e.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
        }
        else {
            e.setAccountNumber(icrRateDetail.getAccountNumber());
            e.setSubAccountNumber(icrRateDetail.getSubAccountNumber());
            e.setChartOfAccountsCode(icrRateDetail.getChartOfAccountsCode());
            // TODO Reporting thing line 1946
        }

        e.setFinancialDocumentTypeCode(parameterService.getParameterValue(PosterIndirectCostRecoveryEntriesStep.class, KFSConstants.SystemGroupParameterNames.GL_INDIRECT_COST_RECOVERY));
        e.setFinancialSystemOriginationCode(parameterService.getParameterValue(KfsParameterConstants.GENERAL_LEDGER_BATCH.class, KFSConstants.SystemGroupParameterNames.GL_ORIGINATION_CODE));
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_STRING);
        e.setDocumentNumber(sdf.format(runDate));
        if (KFSConstants.GL_DEBIT_CODE.equals(icrRateDetail.getTransactionDebitIndicator())) {
            e.setTransactionLedgerEntryDescription(getChargeDescription(pct, et.getObjectCode(), icrGenerationMetadata.getIndirectCostRecoveryTypeCode(), et.getAccountObjectDirectCostAmount().abs()));
        }
        else {
            e.setTransactionLedgerEntryDescription(getOffsetDescription(pct, et.getAccountObjectDirectCostAmount().abs(), et.getChartOfAccountsCode(), et.getAccountNumber()));
        }
        e.setTransactionDate(new java.sql.Date(runDate.getTime()));
        e.setTransactionDebitCreditCode(icrRateDetail.getTransactionDebitIndicator());
        e.setFinancialBalanceTypeCode(et.getBalanceTypeCode());
        e.setUniversityFiscalYear(et.getUniversityFiscalYear());
        e.setUniversityFiscalPeriodCode(et.getUniversityFiscalAccountingPeriod());

        ObjectCode oc = objectCodeService.getByPrimaryId(e.getUniversityFiscalYear(), e.getChartOfAccountsCode(), e.getFinancialObjectCode());
        if (oc == null) {
            LOG.warn(configurationService.getPropertyString(KFSKeyConstants.ERROR_OBJECT_CODE_NOT_FOUND_FOR) + e.getUniversityFiscalYear() + "," + e.getChartOfAccountsCode() + "," + e.getFinancialObjectCode());
            e.setFinancialObjectCode(icrRateDetail.getFinancialObjectCode()); // this will be written out the ICR file. Then, when that file attempts to post, the transaction won't validate and will end up in the icr error file
        } else {
            e.setFinancialObjectTypeCode(oc.getFinancialObjectTypeCode());
        }

        if (generatedTransactionAmount.isNegative()) {
            if (KFSConstants.GL_DEBIT_CODE.equals(icrRateDetail.getTransactionDebitIndicator())) {
                e.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
            }
            else {
                e.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
            }
            e.setTransactionLedgerEntryAmount(generatedTransactionAmount.negated());
        }
        else {
            e.setTransactionLedgerEntryAmount(generatedTransactionAmount);
        }

        if (et.getBalanceTypeCode().equals(et.getOption().getExtrnlEncumFinBalanceTypCd()) || et.getBalanceTypeCode().equals(et.getOption().getIntrnlEncumFinBalanceTypCd()) || et.getBalanceTypeCode().equals(et.getOption().getPreencumbranceFinBalTypeCd()) || et.getBalanceTypeCode().equals(et.getOption().getCostShareEncumbranceBalanceTypeCd())) {
            e.setDocumentNumber(parameterService.getParameterValue(PosterIndirectCostRecoveryEntriesStep.class, KFSConstants.SystemGroupParameterNames.GL_INDIRECT_COST_RECOVERY));
        }
        e.setProjectCode(et.getProjectCode());
        if (GeneralLedgerConstants.getDashOrganizationReferenceId().equals(et.getOrganizationReferenceId())) {
            e.setOrganizationReferenceId(null);
        }
        else {
            e.setOrganizationReferenceId(et.getOrganizationReferenceId());
        }
        // TODO 2031-2039
        try {
            createOutputEntry(e, group);
        }
        catch (IOException ioe) {
            LOG.error("generateTransactions Stopped: " + ioe.getMessage());
            throw new RuntimeException("generateTransactions Stopped: " + ioe.getMessage(), ioe);
        }

        // Now generate Offset
        e = new OriginEntryFull(e);
        if (KFSConstants.GL_DEBIT_CODE.equals(e.getTransactionDebitCreditCode())) {
            e.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
        }
        else {
            e.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
        }
        e.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());

        String offsetBalanceSheetObjectCodeNumber = determineIcrOffsetBalanceSheetObjectCodeNumber(e, et, icrRateDetail);
        e.setFinancialObjectCode(offsetBalanceSheetObjectCodeNumber);
        ObjectCode balSheetObjectCode = objectCodeService.getByPrimaryId(icrRateDetail.getUniversityFiscalYear(), e.getChartOfAccountsCode(), offsetBalanceSheetObjectCodeNumber);
        if (balSheetObjectCode == null) {
            List<Message> warnings = new ArrayList<Message>();
            warnings.add(new Message(configurationService.getPropertyString(KFSKeyConstants.ERROR_INVALID_OFFSET_OBJECT_CODE) + icrRateDetail.getUniversityFiscalYear() + "-" + e.getChartOfAccountsCode() + "-" +offsetBalanceSheetObjectCodeNumber, Message.TYPE_WARNING));
            reportWriterService.writeError(et, warnings);
            
        }
        else {
            e.setFinancialObjectTypeCode(balSheetObjectCode.getFinancialObjectTypeCode());
        }

        if (KFSConstants.GL_DEBIT_CODE.equals(icrRateDetail.getTransactionDebitIndicator())) {
            e.setTransactionLedgerEntryDescription(getChargeDescription(pct, et.getObjectCode(), icrGenerationMetadata.getIndirectCostRecoveryTypeCode(), et.getAccountObjectDirectCostAmount().abs()));
        }
        else {
            e.setTransactionLedgerEntryDescription(getOffsetDescription(pct, et.getAccountObjectDirectCostAmount().abs(), et.getChartOfAccountsCode(), et.getAccountNumber()));
        }

        try {
            flexibleOffsetAccountService.updateOffset(e);
        }
        catch (InvalidFlexibleOffsetException ex) {
            List<Message> warnings = new ArrayList<Message>();
            warnings.add(new Message("FAILED TO GENERATE FLEXIBLE OFFSETS " + ex.getMessage(), Message.TYPE_WARNING));
            reportWriterService.writeError(et, warnings);
            LOG.warn("FAILED TO GENERATE FLEXIBLE OFFSETS FOR EXPENDITURE TRANSACTION " + et.toString(), ex);
        }

        try {
            createOutputEntry(e, group);
        }
        catch (IOException ioe) {
            LOG.error("generateTransactions Stopped: " + ioe.getMessage());
            throw new RuntimeException("generateTransactions Stopped: " + ioe.getMessage(), ioe);
        }
    }

    private static KualiDecimal ONEHUNDRED = new KualiDecimal("100");
    private static DecimalFormat DFPCT = new DecimalFormat("#0.000");
    private static DecimalFormat DFAMT = new DecimalFormat("##########.00");
    private static BigDecimal BDONEHUNDRED = new BigDecimal("100");


    /**
     * Returns ICR Generation Metadata based on SubAccount information if the SubAccount on the expenditure transaction is properly
     * set up for ICR
     * 
     * @param et
     * @param reportErrors
     * @return null if the ET does not have a SubAccount properly set up for ICR
     */
    protected IndirectCostRecoveryGenerationMetadata retrieveSubAccountIndirectCostRecoveryMetadata(ExpenditureTransaction et) {
        SubAccount subAccount = accountingCycleCachingService.getSubAccount(et.getChartOfAccountsCode(), et.getAccountNumber(), et.getSubAccountNumber());
        if (ObjectUtils.isNotNull(subAccount)) {
            subAccount.setA21SubAccount(accountingCycleCachingService.getA21SubAccount(et.getChartOfAccountsCode(), et.getAccountNumber(), et.getSubAccountNumber()));
        }

        if (ObjectUtils.isNotNull(subAccount) && ObjectUtils.isNotNull(subAccount.getA21SubAccount())) {
            A21SubAccount a21SubAccount = subAccount.getA21SubAccount();
            if (StringUtils.isBlank(a21SubAccount.getIndirectCostRecoveryTypeCode()) && StringUtils.isBlank(a21SubAccount.getFinancialIcrSeriesIdentifier()) && StringUtils.isBlank(a21SubAccount.getIndirectCostRecoveryChartOfAccountsCode()) && StringUtils.isBlank(a21SubAccount.getIndirectCostRecoveryAccountNumber())) {
                // all ICR fields were blank, therefore, this sub account was not set up for ICR
                return null;
            }
            // refresh the indirect cost recovery account, accounting cycle style!
            if (!StringUtils.isBlank(a21SubAccount.getChartOfAccountsCode()) && !StringUtils.isBlank(a21SubAccount.getAccountNumber())) {
                a21SubAccount.setIndirectCostRecoveryAccount(accountingCycleCachingService.getAccount(a21SubAccount.getChartOfAccountsCode(), a21SubAccount.getAccountNumber()));
            }

            // these fields will be used to construct warning messages
            String warningMessagePattern = configurationService.getPropertyString(KFSKeyConstants.WARNING_ICR_GENERATION_PROBLEM_WITH_A21SUBACCOUNT_FIELD_BLANK_INVALID);
            String subAccountBOLabel = dataDictionaryService.getDataDictionary().getBusinessObjectEntry(SubAccount.class.getName()).getObjectLabel();
            String subAccountValue = subAccount.getChartOfAccountsCode() + "-" + subAccount.getAccountNumber() + "-" + subAccount.getSubAccountNumber();
            String accountBOLabel = dataDictionaryService.getDataDictionary().getBusinessObjectEntry(Account.class.getName()).getObjectLabel();
            String accountValue = et.getChartOfAccountsCode() + "-" + et.getAccountNumber();

            boolean subAccountOK = true;

            // there were some ICR fields that were filled in, make sure they're all filled in and are valid values
            a21SubAccount.setIndirectCostRecoveryType(accountingCycleCachingService.getIndirectCostRecoveryType(a21SubAccount.getIndirectCostRecoveryTypeCode()));
            if (StringUtils.isBlank(a21SubAccount.getIndirectCostRecoveryTypeCode()) || ObjectUtils.isNull(a21SubAccount.getIndirectCostRecoveryType())) {
                String errorFieldName = dataDictionaryService.getAttributeShortLabel(A21SubAccount.class, KFSPropertyConstants.INDIRECT_COST_RECOVERY_TYPE_CODE);
                String warningMessage = MessageFormat.format(warningMessagePattern, errorFieldName, subAccountBOLabel, subAccountValue, accountBOLabel, accountValue);
                reportWriterService.writeError(et, new Message(warningMessage, Message.TYPE_WARNING));
                subAccountOK = false;
            }

            if (StringUtils.isBlank(a21SubAccount.getFinancialIcrSeriesIdentifier())) {
                Map<String, Object> icrRatePkMap = new HashMap<String, Object>();
                icrRatePkMap.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, et.getUniversityFiscalYear());
                icrRatePkMap.put(KFSPropertyConstants.FINANCIAL_ICR_SERIES_IDENTIFIER, a21SubAccount.getFinancialIcrSeriesIdentifier());
                IndirectCostRecoveryRate indirectCostRecoveryRate = (IndirectCostRecoveryRate) businessObjectService.findByPrimaryKey(IndirectCostRecoveryRate.class, icrRatePkMap);
                if (indirectCostRecoveryRate == null) {
                    String errorFieldName = dataDictionaryService.getAttributeShortLabel(A21SubAccount.class, KFSPropertyConstants.FINANCIAL_ICR_SERIES_IDENTIFIER);
                    String warningMessage = MessageFormat.format(warningMessagePattern, errorFieldName, subAccountBOLabel, subAccountValue, accountBOLabel, accountValue);
                    reportWriterService.writeError(et, new Message(warningMessage, Message.TYPE_WARNING));
                    subAccountOK = false;
                }
            }

            if (StringUtils.isBlank(a21SubAccount.getIndirectCostRecoveryChartOfAccountsCode()) || StringUtils.isBlank(a21SubAccount.getIndirectCostRecoveryAccountNumber()) || ObjectUtils.isNull(a21SubAccount.getIndirectCostRecoveryAccount())) {
                String errorFieldName = dataDictionaryService.getAttributeShortLabel(A21SubAccount.class, KFSPropertyConstants.INDIRECT_COST_RECOVERY_CHART_OF_ACCOUNTS_CODE) + "/" + dataDictionaryService.getAttributeShortLabel(A21SubAccount.class, KFSPropertyConstants.INDIRECT_COST_RECOVERY_ACCOUNT_NUMBER);
                String warningMessage = MessageFormat.format(warningMessagePattern, errorFieldName, subAccountBOLabel, subAccountValue, accountBOLabel, accountValue);
                reportWriterService.writeError(et, new Message(warningMessage, Message.TYPE_WARNING));
                subAccountOK = false;
            }

            if (subAccountOK) {
                IndirectCostRecoveryGenerationMetadata metadata = new IndirectCostRecoveryGenerationMetadata();
                metadata.setFinancialIcrSeriesIdentifier(a21SubAccount.getFinancialIcrSeriesIdentifier());
                metadata.setIndirectCostRecoveryTypeCode(a21SubAccount.getIndirectCostRecoveryTypeCode());
                metadata.setIndirectCostRecoveryChartOfAccountsCode(a21SubAccount.getIndirectCostRecoveryChartOfAccountsCode());
                metadata.setIndirectCostRecoveryAccountNumber(a21SubAccount.getIndirectCostRecoveryAccountNumber());
                metadata.setIndirectCostRecoveryAccount(a21SubAccount.getIndirectCostRecoveryAccount());
                return metadata;
            }
        }
        return null;
    }


    protected IndirectCostRecoveryGenerationMetadata retrieveAccountIndirectCostRecoveryMetadata(ExpenditureTransaction et) {
        Account account = et.getAccount();

        IndirectCostRecoveryGenerationMetadata metadata = new IndirectCostRecoveryGenerationMetadata();
        
        metadata.setFinancialIcrSeriesIdentifier(account.getFinancialIcrSeriesIdentifier());
        metadata.setIndirectCostRecoveryTypeCode(account.getAcctIndirectCostRcvyTypeCd());
        metadata.setIndirectCostRecoveryChartOfAccountsCode(account.getIndirectCostRcvyFinCoaCode());
        metadata.setIndirectCostRecoveryAccountNumber(account.getIndirectCostRecoveryAcctNbr());
        metadata.setIndirectCostRecoveryAccount(account.getIndirectCostRecoveryAcct());

        return metadata;
    }

    /**
     * Generates a percent of a KualiDecimal amount (great for finding out how much of an origin entry should be recouped by
     * indirect cost recovery)
     * 
     * @param amount the original amount
     * @param percent the percentage of that amount to calculate
     * @return the percent of the amount
     */
    private KualiDecimal getPercentage(KualiDecimal amount, BigDecimal percent) {
        BigDecimal result = amount.bigDecimalValue().multiply(percent).divide(BDONEHUNDRED, 2, BigDecimal.ROUND_DOWN);
        return new KualiDecimal(result);
    }

    /**
     * Generates the description of a charge
     * 
     * @param rate the ICR rate for this entry
     * @param objectCode the object code of this entry
     * @param type the ICR type code of this entry's account
     * @param amount the amount of this entry
     * @return a description for the charge entry
     */
    private String getChargeDescription(BigDecimal rate, String objectCode, String type, KualiDecimal amount) {
        BigDecimal newRate = rate.multiply(PosterServiceImpl.BDONEHUNDRED);

        StringBuffer desc = new StringBuffer("CHG ");
        if (newRate.doubleValue() < 10) {
            desc.append(" ");
        }
        desc.append(DFPCT.format(newRate));
        desc.append("% ON ");
        desc.append(objectCode);
        desc.append(" (");
        desc.append(type);
        desc.append(")  ");
        String amt = DFAMT.format(amount);
        while (amt.length() < 13) {
            amt = " " + amt;
        }
        desc.append(amt);
        return desc.toString();
    }

    /**
     * Returns the description of a debit origin entry created by generateTransactions
     * 
     * @param rate the ICR rate that relates to this entry
     * @param amount the amount of this entry
     * @param chartOfAccountsCode the chart codce of the debit entry
     * @param accountNumber the account number of the debit entry
     * @return a description for the debit entry
     */
    private String getOffsetDescription(BigDecimal rate, KualiDecimal amount, String chartOfAccountsCode, String accountNumber) {
        BigDecimal newRate = rate.multiply(PosterServiceImpl.BDONEHUNDRED);

        StringBuffer desc = new StringBuffer("RCV ");
        if (newRate.doubleValue() < 10) {
            desc.append(" ");
        }
        desc.append(DFPCT.format(newRate));
        desc.append("% ON ");
        String amt = DFAMT.format(amount);
        while (amt.length() < 13) {
            amt = " " + amt;
        }
        desc.append(amt);
        desc.append(" FRM ");
        // desc.append(chartOfAccountsCode);
        // desc.append("-");
        desc.append(accountNumber);
        return desc.toString();
    }

    /**
     * Increments a named count holding statistics about posted transactions
     * 
     * @param reporting a Map of counts generated by this process
     * @param destination the destination of a given transaction
     * @param operation the operation being performed on the transaction
     */
    private void addReporting(Map reporting, String destination, String operation) {
        String key = destination + "," + operation;
        if (reporting.containsKey(key)) {
            Integer c = (Integer) reporting.get(key);
            reporting.put(key, new Integer(c.intValue() + 1));
        }
        else {
            reporting.put(key, new Integer(1));
        }
    }

    protected String determineIcrOffsetBalanceSheetObjectCodeNumber(OriginEntryInformation offsetEntry, ExpenditureTransaction et, IndirectCostRecoveryRateDetail icrRateDetail) {
        String icrEntryDocumentType = parameterService.getParameterValue(PosterIndirectCostRecoveryEntriesStep.class, KFSConstants.SystemGroupParameterNames.GL_INDIRECT_COST_RECOVERY);
        OffsetDefinition offsetDefinition = offsetDefinitionService.getByPrimaryId(offsetEntry.getUniversityFiscalYear(), offsetEntry.getChartOfAccountsCode(), icrEntryDocumentType, et.getBalanceTypeCode());
        if (!ObjectUtils.isNull(offsetDefinition)) {
            return offsetDefinition.getFinancialObjectCode();
        } else {
            return null;
        }
    }
    
    public void setVerifyTransaction(VerifyTransaction vt) {
        verifyTransaction = vt;
    }

    public void setTransactionPosters(List p) {
        transactionPosters = p;
    }

    public void setOriginEntryService(OriginEntryService oes) {
        originEntryService = oes;
    }

    public void setOriginEntryGroupService(OriginEntryGroupService oes) {
        originEntryGroupService = oes;
    }

    public void setDateTimeService(DateTimeService dts) {
        dateTimeService = dts;
    }

    public void setReversalDao(ReversalDao red) {
        reversalDao = red;
    }

    public void setUniversityDateDao(UniversityDateDao udd) {
        universityDateDao = udd;
    }

    public void setAccountingPeriodService(AccountingPeriodService aps) {
        accountingPeriodService = aps;
    }

    public void setExpenditureTransactionDao(ExpenditureTransactionDao etd) {
        expenditureTransactionDao = etd;
    }

    public void setIndirectCostRecoveryRateDetailDao(IndirectCostRecoveryRateDetailDao iaed) {
        indirectCostRecoveryRateDetailDao = iaed;
    }

    public void setObjectCodeService(ObjectCodeService ocs) {
        objectCodeService = ocs;
    }

    public void setConfigurationService(KualiConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setFlexibleOffsetAccountService(FlexibleOffsetAccountService flexibleOffsetAccountService) {
        this.flexibleOffsetAccountService = flexibleOffsetAccountService;
    }

    public RunDateService getRunDateService() {
        return runDateService;
    }

    public void setRunDateService(RunDateService runDateService) {
        this.runDateService = runDateService;
    }

    private void createOutputEntry(Transaction entry, PrintStream group) throws IOException {
        OriginEntryFull oef = new OriginEntryFull();
        oef.copyFieldsFromTransaction(entry);
        try {
            group.printf("%s\n", oef.getLine());
        }
        catch (Exception e) {
            throw new IOException(e.toString());
        }
    }

    private void writeErrorEntry(String line, PrintStream invaliGroup) throws IOException {
        try {
            invaliGroup.printf("%s\n", line);
        } catch (Exception e) {
            throw new IOException(e.toString());
        }
    }
    
    public AccountingCycleCachingService getAccountingCycleCachingService() {
        return accountingCycleCachingService;
    }

    public void setAccountingCycleCachingService(AccountingCycleCachingService accountingCycleCachingService) {
        this.accountingCycleCachingService = accountingCycleCachingService;
    }

    public void setSubAccountService(SubAccountService subAccountService) {
        this.subAccountService = subAccountService;
    }

    public void setOffsetDefinitionService(OffsetDefinitionService offsetDefinitionService) {
        this.offsetDefinitionService = offsetDefinitionService;
    }

    protected DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    protected BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    protected boolean shouldIgnoreExpenditureTransaction(ExpenditureTransaction et) {
        if (ObjectUtils.isNotNull(et.getOption())) {
            SystemOptions options = et.getOption();
            return StringUtils.isNotBlank(options.getActualFinancialBalanceTypeCd()) && !options.getActualFinancialBalanceTypeCd().equals(et.getBalanceTypeCode());
        }
        return true;
    }

    public void setBatchFileDirectoryName(String batchFileDirectoryName) {
        this.batchFileDirectoryName = batchFileDirectoryName;
    }

    public void setPersistenceStructureService(PersistenceStructureService persistenceStructureService) {
        this.persistenceStructureService = persistenceStructureService;
    }

    public void setReportWriterService(ReportWriterService reportWriterService) {
        this.reportWriterService = reportWriterService;
    }

    public void setErrorListingReportWriterService(ReportWriterService errorListingReportWriterService) {
        this.errorListingReportWriterService = errorListingReportWriterService;
    }

    public void setReversalReportWriterService(ReportWriterService reversalReportWriterService) {
        this.reversalReportWriterService = reversalReportWriterService;
    }

    public void setLedgerSummaryReportWriterService(ReportWriterService ledgerSummaryReportWriterService) {
        this.ledgerSummaryReportWriterService = ledgerSummaryReportWriterService;
    }
}
