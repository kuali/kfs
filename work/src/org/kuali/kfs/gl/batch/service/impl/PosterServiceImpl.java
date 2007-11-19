/*
 * Copyright 2005-2007 The Kuali Foundation.
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

import java.math.BigDecimal;
import java.sql.Date;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.ojb.broker.metadata.MetadataManager;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.PersistenceService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ParameterService;
import org.kuali.kfs.service.impl.ParameterConstants;
import org.kuali.module.chart.bo.AccountingPeriod;
import org.kuali.module.chart.bo.IcrAutomatedEntry;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.dao.IcrAutomatedEntryDao;
import org.kuali.module.chart.service.AccountingPeriodService;
import org.kuali.module.chart.service.ObjectCodeService;
import org.kuali.module.financial.exceptions.InvalidFlexibleOffsetException;
import org.kuali.module.financial.service.FlexibleOffsetAccountService;
import org.kuali.module.gl.GLConstants;
import org.kuali.module.gl.batch.PosterIndirectCostRecoveryEntriesStep;
import org.kuali.module.gl.batch.poster.PostTransaction;
import org.kuali.module.gl.batch.poster.VerifyTransaction;
import org.kuali.module.gl.bo.ExpenditureTransaction;
import org.kuali.module.gl.bo.OriginEntryFull;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.OriginEntrySource;
import org.kuali.module.gl.bo.Reversal;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.bo.UniversityDate;
import org.kuali.module.gl.dao.ExpenditureTransactionDao;
import org.kuali.module.gl.dao.ReversalDao;
import org.kuali.module.gl.dao.UniversityDateDao;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.gl.service.OriginEntryService;
import org.kuali.module.gl.service.PosterService;
import org.kuali.module.gl.service.ReportService;
import org.kuali.module.gl.service.RunDateService;
import org.kuali.module.gl.util.Message;
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
    private IcrAutomatedEntryDao icrAutomatedEntryDao;
    private ObjectCodeService objectCodeService;
    private ReportService reportService;
    private ParameterService parameterService;
    private KualiConfigurationService configurationService;
    private FlexibleOffsetAccountService flexibleOffsetAccountService;
    private RunDateService runDateService;

    /**
     * Post scrubbed GL entries to GL tables.
     */
    public void postMainEntries() {
        LOG.debug("postMainEntries() started");
        postEntries(PosterService.MODE_ENTRIES);
    }

    /**
     * Post reversal GL entries to GL tables.
     */
    public void postReversalEntries() {
        LOG.debug("postReversalEntries() started");
        postEntries(PosterService.MODE_REVERSAL);
    }

    /**
     * Post ICR GL entries to GL tables.
     */
    public void postIcrEntries() {
        LOG.debug("postIcrEntries() started");
        postEntries(PosterService.MODE_ICR);
    }

    /**
     * Actually post the entries. The mode variable decides which entries to post.
     * 
     * @param mode the poster's current run mode
     */
    private void postEntries(int mode) {
        LOG.debug("postEntries() started");

        String validEntrySourceCode = OriginEntrySource.MAIN_POSTER_VALID;
        String invalidEntrySourceCode = OriginEntrySource.MAIN_POSTER_ERROR;
        OriginEntryGroup validGroup = null;
        OriginEntryGroup invalidGroup = null;

        Date executionDate = new Date(dateTimeService.getCurrentDate().getTime());
        Date runDate = new Date(runDateService.calculateRunDate(executionDate).getTime());

        UniversityDate runUniversityDate = universityDateDao.getByPrimaryKey(runDate);

        Collection groups = null;
        Iterator reversalTransactions = null;
        switch (mode) {
            case PosterService.MODE_ENTRIES:
                validEntrySourceCode = OriginEntrySource.MAIN_POSTER_VALID;
                invalidEntrySourceCode = OriginEntrySource.MAIN_POSTER_ERROR;
                groups = originEntryGroupService.getGroupsToPost();
                reportService.generatePosterMainLedgerSummaryReport(executionDate, runDate, groups);
                break;
            case PosterService.MODE_REVERSAL:
                validEntrySourceCode = OriginEntrySource.REVERSAL_POSTER_VALID;
                invalidEntrySourceCode = OriginEntrySource.REVERSAL_POSTER_ERROR;
                reversalTransactions = reversalDao.getByDate(runDate);
                reportService.generatePosterReversalLedgerSummaryReport(executionDate, runDate, reversalTransactions);
                break;
            case PosterService.MODE_ICR:
                validEntrySourceCode = OriginEntrySource.ICR_POSTER_VALID;
                invalidEntrySourceCode = OriginEntrySource.ICR_POSTER_ERROR;
                groups = originEntryGroupService.getIcrGroupsToPost();
                reportService.generatePosterIcrLedgerSummaryReport(executionDate, runDate, groups);
                break;
            default:
                throw new IllegalArgumentException("Invalid poster mode " + mode);
        }

        // Create new Groups for output transactions
        validGroup = originEntryGroupService.createGroup(runDate, validEntrySourceCode, true, true, false);
        invalidGroup = originEntryGroupService.createGroup(runDate, invalidEntrySourceCode, false, true, false);

        Map reportError = new HashMap();

        // Build the summary map so all the possible combinations of destination &
        // operation
        // are included in the summary part of the report.
        Map reportSummary = new HashMap();
        for (Iterator posterIter = transactionPosters.iterator(); posterIter.hasNext();) {
            PostTransaction poster = (PostTransaction) posterIter.next();
            reportSummary.put(poster.getDestinationName() + "," + GLConstants.DELETE_CODE, new Integer(0));
            reportSummary.put(poster.getDestinationName() + "," + GLConstants.INSERT_CODE, new Integer(0));
            reportSummary.put(poster.getDestinationName() + "," + GLConstants.UPDATE_CODE, new Integer(0));
        }

        int ecount = 0;
        try {
            if ((mode == PosterService.MODE_ENTRIES) || (mode == PosterService.MODE_ICR)) {
                LOG.debug("postEntries() Processing groups");
                for (Iterator iter = groups.iterator(); iter.hasNext();) {
                    OriginEntryGroup group = (OriginEntryGroup) iter.next();

                    Iterator entries = originEntryService.getEntriesByGroup(group);
                    while (entries.hasNext()) {
                        Transaction tran = (Transaction) entries.next();

                        postTransaction(tran, mode, reportSummary, reportError, invalidGroup, validGroup, runUniversityDate);

                        if (++ecount % 1000 == 0) {
                            LOG.info("postEntries() Posted Entry " + ecount);
                        }
                    }

                    // Mark this group so we don't process it again next time the poster runs
                    group.setProcess(Boolean.FALSE);
                    originEntryGroupService.save(group);
                }
            }
            else {
                LOG.debug("postEntries() Processing reversal transactions");

                final String GL_REVERSAL_T = MetadataManager.getInstance().getGlobalRepository().getDescriptorFor(Reversal.class).getFullTableName();

                while (reversalTransactions.hasNext()) {
                    Transaction tran = (Transaction) reversalTransactions.next();
                    addReporting(reportSummary, GL_REVERSAL_T, GLConstants.SELECT_CODE);

                    postTransaction(tran, mode, reportSummary, reportError, invalidGroup, validGroup, runUniversityDate);

                    if (++ecount % 1000 == 0) {
                        LOG.info("postEntries() Posted Entry " + ecount);
                    }
                }

                // Report Reversal poster valid transactions
                reportService.generatePosterReversalTransactionsListing(executionDate, runDate, validGroup);
            }
        }
        catch (RuntimeException e) {
            LOG.info("postEntries() failed posting Entry " + ecount);
            throw e;
        }

        LOG.info("postEntries() done, total count = " + ecount);

        // Generate the reports
        reportService.generatePosterStatisticsReport(executionDate, runDate, reportSummary, transactionPosters, reportError, mode);
        reportService.generatePosterErrorTransactionListing(executionDate, runDate, invalidGroup, mode);
    }

    /**
     * Runs the given transaction through each transaction posting algorithms associated with this instance
     * 
     * @param tran a transaction to post
     * @param mode the mode the poster is running in
     * @param reportSummary a Map of summary counts generated by the posting process
     * @param reportError a Map of errors encountered during posting
     * @param invalidGroup the group to save invalid entries to
     * @param validGroup the gorup to save valid posted entries into
     * @param runUniversityDate the university date of this poster run
     */
    private void postTransaction(Transaction tran, int mode, Map reportSummary, Map reportError, OriginEntryGroup invalidGroup, OriginEntryGroup validGroup, UniversityDate runUniversityDate) {

        List errors = new ArrayList();

        Transaction originalTransaction = tran;

        final String GL_ORIGIN_ENTRY_T = MetadataManager.getInstance().getGlobalRepository().getDescriptorFor(OriginEntryFull.class).getFullTableName();

        // Update select count in the report
        if (mode == PosterService.MODE_ENTRIES) {
            addReporting(reportSummary, GL_ORIGIN_ENTRY_T, GLConstants.SELECT_CODE);
        }
        else {
            addReporting(reportSummary, GL_ORIGIN_ENTRY_T + " (ICR)", GLConstants.SELECT_CODE);
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
                    if (KFSConstants.ACCOUNTING_PERIOD_STATUS_CLOSED.equals(ap.getUniversityFiscalPeriodStatusCode())) {
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
                    errors.add(configurationService.getPropertyString(KFSKeyConstants.ERROR_UNIV_DATE_NOT_IN_ACCOUNTING_PERIOD_TABLE));
                }
            }
            else {
                errors.add(configurationService.getPropertyString(KFSKeyConstants.ERROR_REVERSAL_DATE_NOT_IN_UNIV_DATE_TABLE));
            }

            PersistenceService ps = SpringContext.getBean(PersistenceService.class);
            ps.retrieveNonKeyFields(reversal);
            tran = reversal;
        }

        if (errors.size() == 0) {
            errors = verifyTransaction.verifyTransaction(tran);
        }

        // Now check each poster to see if it needs to verify the transaction. If
        // it returns errors, we won't post it
        for (Iterator posterIter = transactionPosters.iterator(); posterIter.hasNext();) {
            PostTransaction poster = (PostTransaction) posterIter.next();
            if (poster instanceof VerifyTransaction) {
                VerifyTransaction vt = (VerifyTransaction) poster;

                errors.addAll(vt.verifyTransaction(tran));
            }
        }

        if (errors.size() > 0) {
            // Error on this transaction
            reportError.put(tran, errors);
            addReporting(reportSummary, "WARNING", GLConstants.SELECT_CODE);

            originEntryService.createEntry(tran, invalidGroup);
        }
        else {
            // No error so post it
            for (Iterator posterIter = transactionPosters.iterator(); posterIter.hasNext();) {
                PostTransaction poster = (PostTransaction) posterIter.next();
                String actionCode = poster.post(tran, mode, runUniversityDate.getUniversityDate());

                if (actionCode.startsWith(GLConstants.ERROR_CODE)) {
                    errors = new ArrayList();
                    errors.add(actionCode);
                    reportError.put(tran, errors);
                }
                else if (actionCode.indexOf(GLConstants.INSERT_CODE) >= 0) {
                    addReporting(reportSummary, poster.getDestinationName(), GLConstants.INSERT_CODE);
                }
                else if (actionCode.indexOf(GLConstants.UPDATE_CODE) >= 0) {
                    addReporting(reportSummary, poster.getDestinationName(), GLConstants.UPDATE_CODE);
                }
                else if (actionCode.indexOf(GLConstants.DELETE_CODE) >= 0) {
                    addReporting(reportSummary, poster.getDestinationName(), GLConstants.DELETE_CODE);
                }
                else if (actionCode.indexOf(GLConstants.SELECT_CODE) >= 0) {
                    addReporting(reportSummary, poster.getDestinationName(), GLConstants.SELECT_CODE);
                }
            }

            if (errors.size() == 0) {
                originEntryService.createEntry(tran, validGroup);

                // Delete the reversal entry
                if (mode == PosterService.MODE_REVERSAL) {
                    reversalDao.delete((Reversal) originalTransaction);
                    addReporting(reportSummary, MetadataManager.getInstance().getGlobalRepository().getDescriptorFor(Reversal.class).getFullTableName(), GLConstants.DELETE_CODE);
                }
            }
        }
    }

    /**
     * This step reads the expenditure table and uses the data to generate Indirect Cost Recovery transactions.
     */
    public void generateIcrTransactions() {
        LOG.debug("generateIcrTransactions() started");

        Date executionDate = dateTimeService.getCurrentSqlDate();
        Date runDate = new Date(runDateService.calculateRunDate(executionDate).getTime());

        OriginEntryGroup group = originEntryGroupService.createGroup(runDate, OriginEntrySource.ICR_TRANSACTIONS, true, true, false);

        Map<ExpenditureTransaction, List<Message>> reportErrors = new HashMap();

        int reportExpendTranRetrieved = 0;
        int reportExpendTranDeleted = 0;
        int reportExpendTranKept = 0;
        int reportOriginEntryGenerated = 0;

        Iterator expenditureTransactions = expenditureTransactionDao.getAllExpenditureTransactions();
        while (expenditureTransactions.hasNext()) {
            ExpenditureTransaction et = (ExpenditureTransaction) expenditureTransactions.next();
            reportExpendTranRetrieved++;

            KualiDecimal transactionAmount = et.getAccountObjectDirectCostAmount();
            KualiDecimal distributionAmount = KualiDecimal.ZERO;

            Collection automatedEntries = icrAutomatedEntryDao.getEntriesBySeries(et.getUniversityFiscalYear(), et.getAccount().getFinancialIcrSeriesIdentifier(), et.getBalanceTypeCode());
            int automatedEntriesCount = automatedEntries.size();

            if (automatedEntriesCount > 0) {
                for (Iterator icrIter = automatedEntries.iterator(); icrIter.hasNext();) {
                    IcrAutomatedEntry icrEntry = (IcrAutomatedEntry) icrIter.next();
                    KualiDecimal generatedTransactionAmount = null;

                    if (!icrIter.hasNext()) {
                        generatedTransactionAmount = distributionAmount;

                        // Log differences that are over WARNING_MAX_DIFFERENCE
                        if (getPercentage(transactionAmount, icrEntry.getAwardIndrCostRcvyRatePct()).subtract(distributionAmount).abs().isGreaterThan(WARNING_MAX_DIFFERENCE)) {
                            List warnings = new ArrayList();
                            warnings.add("ADJUSTMENT GREATER THAN " + WARNING_MAX_DIFFERENCE);
                            reportErrors.put(et, warnings);
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
                        List warnings = new ArrayList();
                        warnings.add("DEBIT OR CREDIT CODE NOT FOUND");
                        reportErrors.put(et, warnings);
                    }

                    generateTransactions(et, icrEntry, generatedTransactionAmount, runDate, group, reportErrors);
                    reportOriginEntryGenerated = reportOriginEntryGenerated + 2;
                }
            }

            // Delete expenditure record
            expenditureTransactionDao.delete(et);
            reportExpendTranDeleted++;
        }

        reportService.generatePosterIcrStatisticsReport(executionDate, runDate, reportErrors, reportExpendTranRetrieved, reportExpendTranDeleted, reportExpendTranKept, reportOriginEntryGenerated);
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
    private void generateTransactions(ExpenditureTransaction et, IcrAutomatedEntry icrEntry, KualiDecimal generatedTransactionAmount, Date runDate, OriginEntryGroup group, Map reportErrors) {
        BigDecimal pct = new BigDecimal(icrEntry.getAwardIndrCostRcvyRatePct().toString());
        pct = pct.divide(BDONEHUNDRED);

        OriginEntryFull e = new OriginEntryFull();
        e.setTransactionLedgerEntrySequenceNumber(0);

        // SYMBOL_USE_EXPENDITURE_ENTRY means we use the field from the expenditure entry, SYMBOL_USE_IRC_FROM_ACCOUNT
        // means we use the ICR field from the account record, otherwise, use the field in the icrEntry
        if (GLConstants.PosterService.SYMBOL_USE_EXPENDITURE_ENTRY.equals(icrEntry.getFinancialObjectCode()) || GLConstants.PosterService.SYMBOL_USE_IRC_FROM_ACCOUNT.equals(icrEntry.getFinancialObjectCode())) {
            e.setFinancialObjectCode(et.getObjectCode());
            e.setFinancialSubObjectCode(et.getSubObjectCode());
        }
        else {
            e.setFinancialObjectCode(icrEntry.getFinancialObjectCode());
            if (GLConstants.PosterService.SYMBOL_USE_EXPENDITURE_ENTRY.equals(icrEntry.getFinancialSubObjectCode())) {
                e.setFinancialSubObjectCode(et.getSubObjectCode());
            }
            else {
                e.setFinancialSubObjectCode(icrEntry.getFinancialSubObjectCode());
            }
        }

        if (GLConstants.PosterService.SYMBOL_USE_EXPENDITURE_ENTRY.equals(icrEntry.getAccountNumber())) {
            e.setAccountNumber(et.getAccountNumber());
            e.setChartOfAccountsCode(et.getChartOfAccountsCode());
            e.setSubAccountNumber(et.getSubAccountNumber());
        }
        else if (GLConstants.PosterService.SYMBOL_USE_IRC_FROM_ACCOUNT.equals(icrEntry.getAccountNumber())) {
            e.setAccountNumber(et.getAccount().getIndirectCostRecoveryAcctNbr());
            e.setChartOfAccountsCode(et.getAccount().getIndirectCostRcvyFinCoaCode());
            e.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
        }
        else {
            e.setAccountNumber(icrEntry.getAccountNumber());
            e.setSubAccountNumber(icrEntry.getSubAccountNumber());
            e.setChartOfAccountsCode(icrEntry.getChartOfAccountsCode());
            // TODO Reporting thing line 1946
        }

        e.setFinancialDocumentTypeCode(parameterService.getParameterValue(PosterIndirectCostRecoveryEntriesStep.class, KFSConstants.SystemGroupParameterNames.GL_INDIRECT_COST_RECOVERY));
        e.setFinancialSystemOriginationCode(parameterService.getParameterValue(ParameterConstants.GENERAL_LEDGER_BATCH.class, KFSConstants.SystemGroupParameterNames.GL_ORIGINATION_CODE));
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_STRING);
        e.setDocumentNumber(sdf.format(runDate));
        if (KFSConstants.GL_DEBIT_CODE.equals(icrEntry.getTransactionDebitIndicator())) {
            e.setTransactionLedgerEntryDescription(getChargeDescription(pct, et.getObjectCode(), et.getAccount().getAcctIndirectCostRcvyTypeCd(), et.getAccountObjectDirectCostAmount().abs()));
        }
        else {
            e.setTransactionLedgerEntryDescription(getOffsetDescription(pct, et.getAccountObjectDirectCostAmount().abs(), et.getChartOfAccountsCode(), et.getAccountNumber()));
        }
        e.setTransactionDate(new java.sql.Date(runDate.getTime()));
        e.setTransactionDebitCreditCode(icrEntry.getTransactionDebitIndicator());
        e.setFinancialBalanceTypeCode(et.getBalanceTypeCode());
        e.setUniversityFiscalYear(et.getUniversityFiscalYear());
        e.setUniversityFiscalPeriodCode(et.getUniversityFiscalAccountingPeriod());

        ObjectCode oc = objectCodeService.getByPrimaryId(e.getUniversityFiscalYear(), e.getChartOfAccountsCode(), e.getFinancialObjectCode());
        if (oc == null) {
            // TODO This should be a report thing, not an exception
            throw new IllegalArgumentException(configurationService.getPropertyString(KFSKeyConstants.ERROR_OBJECT_CODE_NOT_FOUND_FOR) + e.getUniversityFiscalYear() + "," + e.getChartOfAccountsCode() + "," + e.getFinancialObjectCode());
        }
        e.setFinancialObjectTypeCode(oc.getFinancialObjectTypeCode());

        if (generatedTransactionAmount.isNegative()) {
            if (KFSConstants.GL_DEBIT_CODE.equals(icrEntry.getTransactionDebitIndicator())) {
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
        if (GLConstants.getDashOrganizationReferenceId().equals(et.getOrganizationReferenceId())) {
            e.setOrganizationReferenceId(null);
        }
        else {
            e.setOrganizationReferenceId(et.getOrganizationReferenceId());
        }

        // TODO 2031-2039
        originEntryService.createEntry(e, group);

        // Now generate Offset
        e = new OriginEntryFull(e);
        if (KFSConstants.GL_DEBIT_CODE.equals(e.getTransactionDebitCreditCode())) {
            e.setTransactionDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
        }
        else {
            e.setTransactionDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
        }
        e.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        e.setFinancialObjectCode(icrEntry.getOffsetBalanceSheetObjectCodeNumber());

        ObjectCode balSheetObjectCode = objectCodeService.getByPrimaryId(icrEntry.getUniversityFiscalYear(), e.getChartOfAccountsCode(), icrEntry.getOffsetBalanceSheetObjectCodeNumber());
        if (balSheetObjectCode == null) {
            List warnings = new ArrayList();
            warnings.add(configurationService.getPropertyString(KFSKeyConstants.ERROR_INVALID_OFFSET_OBJECT_CODE) + icrEntry.getUniversityFiscalYear() + "-" + e.getChartOfAccountsCode() + "-" + icrEntry.getOffsetBalanceSheetObjectCodeNumber());
            reportErrors.put(e, warnings);
        }
        else {
            e.setFinancialObjectTypeCode(balSheetObjectCode.getFinancialObjectTypeCode());
        }

        if (KFSConstants.GL_DEBIT_CODE.equals(icrEntry.getTransactionDebitIndicator())) {
            e.setTransactionLedgerEntryDescription(getChargeDescription(pct, et.getObjectCode(), et.getAccount().getAcctIndirectCostRcvyTypeCd(), et.getAccountObjectDirectCostAmount().abs()));
        }
        else {
            e.setTransactionLedgerEntryDescription(getOffsetDescription(pct, et.getAccountObjectDirectCostAmount().abs(), et.getChartOfAccountsCode(), et.getAccountNumber()));
        }

        try {
            flexibleOffsetAccountService.updateOffset(e);
        }
        catch (InvalidFlexibleOffsetException ex) {
            List warnings = new ArrayList();
            warnings.add("FAILED TO GENERATE FLEXIBLE OFFSETS " + ex.getMessage());
            reportErrors.put(e, warnings);
        }

        originEntryService.createEntry(e, group);
    }

    private static KualiDecimal ONEHUNDRED = new KualiDecimal("100");
    private static DecimalFormat DFPCT = new DecimalFormat("#0.000");
    private static DecimalFormat DFAMT = new DecimalFormat("##########.00");
    private static BigDecimal BDONEHUNDRED = new BigDecimal("100");

    /**
     * Generates a percent of a KualiDecimal amount (great for finding out how much of an origin entry should be recouped by indirect cost recovery)
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

    public void setIcrAutomatedEntryDao(IcrAutomatedEntryDao iaed) {
        icrAutomatedEntryDao = iaed;
    }

    public void setObjectCodeService(ObjectCodeService ocs) {
        objectCodeService = ocs;
    }

    public void setReportService(ReportService rs) {
        reportService = rs;
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
}
