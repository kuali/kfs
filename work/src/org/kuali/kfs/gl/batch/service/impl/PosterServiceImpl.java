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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.Constants;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.PersistenceService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.chart.bo.AccountingPeriod;
import org.kuali.module.chart.bo.IcrAutomatedEntry;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.dao.IcrAutomatedEntryDao;
import org.kuali.module.chart.service.AccountingPeriodService;
import org.kuali.module.chart.service.ObjectCodeService;
import org.kuali.module.gl.batch.poster.PostTransaction;
import org.kuali.module.gl.batch.poster.PosterReport;
import org.kuali.module.gl.batch.poster.VerifyTransaction;
import org.kuali.module.gl.bo.ExpenditureTransaction;
import org.kuali.module.gl.bo.OriginEntry;
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
import org.kuali.module.gl.util.Summary;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;

/**
 * @author jsissom
 * @version $Id: PosterServiceImpl.java,v 1.31 2006-06-14 12:26:36 abyrne Exp $
 */
public class PosterServiceImpl implements PosterService, BeanFactoryAware {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PosterServiceImpl.class);

    private BeanFactory beanFactory;
    private List transactionPosters;
    private VerifyTransaction verifyTransaction;
    private PosterReport posterReportService;
    private PosterReport icrGenerationReportService;
    private OriginEntryService originEntryService;
    private OriginEntryGroupService originEntryGroupService;
    private DateTimeService dateTimeService;
    private ReversalDao reversalDao;
    private UniversityDateDao universityDateDao;
    private AccountingPeriodService accountingPeriodService;
    private ExpenditureTransactionDao expenditureTransactionDao;
    private IcrAutomatedEntryDao icrAutomatedEntryDao;
    private ObjectCodeService objectCodeService;

    // private IndirectCostRecoveryThresholdDao indirectCostRecoveryThresholdDao;

    /**
     * 
     */
    public PosterServiceImpl() {
        super();
    }

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

    /*
     * Actually post the entries. The mode variable decides which entries to post.
     */
    private void postEntries(int mode) {
        LOG.debug("postEntries() started");

        String validEntrySourceCode = OriginEntrySource.MAIN_POSTER_VALID;
        String invalidEntrySourceCode = OriginEntrySource.MAIN_POSTER_ERROR;
        OriginEntryGroup validGroup = null;
        OriginEntryGroup invalidGroup = null;

        Date runDate = new Date(dateTimeService.getCurrentDate().getTime());
        UniversityDate runUniversityDate = universityDateDao.getByPrimaryKey(runDate);

        Collection groups = null;
        Iterator reversalTransactions = null;
        switch (mode) {
            case PosterService.MODE_ENTRIES:
                validEntrySourceCode = OriginEntrySource.MAIN_POSTER_VALID;
                invalidEntrySourceCode = OriginEntrySource.MAIN_POSTER_ERROR;
                groups = originEntryGroupService.getGroupsToPost();
                break;
            case PosterService.MODE_REVERSAL:
                validEntrySourceCode = OriginEntrySource.REVERSAL_POSTER_VALID;
                invalidEntrySourceCode = OriginEntrySource.REVERSAL_POSTER_ERROR;
                reversalTransactions = reversalDao.getByDate(runDate);
                break;
            case PosterService.MODE_ICR:
                validEntrySourceCode = OriginEntrySource.ICR_POSTER_VALID;
                invalidEntrySourceCode = OriginEntrySource.ICR_POSTER_ERROR;
                groups = originEntryGroupService.getIcrGroupsToPost();
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
            reportSummary.put(poster.getDestinationName() + ",D", new Integer(0));
            reportSummary.put(poster.getDestinationName() + ",I", new Integer(0));
            reportSummary.put(poster.getDestinationName() + ",U", new Integer(0));
        }

        // Process all the groups or reversalTransactions
        // NOTE (laran): this seems like a wierd check. I think what you're doing would
        // be more clearly written as:
        // if(PosterService.MODE_ENTRIES == mode || PosterService.MODE_ICR == mode) {
        if (groups != null) {
            LOG.debug("postEntries() Processing groups");
            for (Iterator iter = groups.iterator(); iter.hasNext();) {
                OriginEntryGroup group = (OriginEntryGroup) iter.next();

                Iterator entries = originEntryService.getEntriesByGroup(group);
                while (entries.hasNext()) {
                    Transaction tran = (Transaction) entries.next();

                    postTransaction(tran, mode, reportSummary, reportError, invalidGroup, validGroup, runUniversityDate);
                }

                // Mark this group so we don't process it again next time the poster runs
                group.setProcess(Boolean.FALSE);
                originEntryGroupService.save(group);
            }
        }
        else {
            LOG.debug("postEntries() Processing reversal transactions");
            while (reversalTransactions.hasNext()) {
                Transaction tran = (Transaction) reversalTransactions.next();

                postTransaction(tran, mode, reportSummary, reportError, invalidGroup, validGroup, runUniversityDate);
            }
        }

        // Generate the report

        // Convert our summary to a list of items for the report
        List summary = new ArrayList();
        summary.add(new Summary(1, "Number of GL_ORIGIN_ENTRY_T records selected:", (Integer) reportSummary.get("GL_ORIGIN_ENTRY_T,S")));
        summary.add(new Summary(2, "", 0));

        int count = 10;
        for (Iterator posterIter = transactionPosters.iterator(); posterIter.hasNext();) {
            PostTransaction poster = (PostTransaction) posterIter.next();
            String table = poster.getDestinationName();
            summary.add(new Summary(count++, "Number of " + table + " records deleted:", (Integer) reportSummary.get(table + ",D")));
            summary.add(new Summary(count++, "Number of " + table + " records inserted:", (Integer) reportSummary.get(table + ",I")));
            summary.add(new Summary(count++, "Number of " + table + " records updated:", (Integer) reportSummary.get(table + ",U")));
            summary.add(new Summary(count++, "", 0));
        }

        summary.add(new Summary(10000, "", 0));
        summary.add(new Summary(10001, "Number of WARNING records selected:", (Integer) reportSummary.get("WARNING,S")));

        posterReportService.generateReport(reportError, summary, runDate, mode);
    }

    private void postTransaction(Transaction tran, int mode, Map reportSummary, Map reportError, OriginEntryGroup invalidGroup, OriginEntryGroup validGroup, UniversityDate runUniversityDate) {

        List errors = new ArrayList();

        Transaction originalTransaction = tran;

        // Update select count in the report
        if (mode == PosterService.MODE_ENTRIES) {
            addReporting(reportSummary, "GL_ORIGIN_ENTRY_T", "S");
        }
        else if (mode == PosterService.MODE_REVERSAL) {
            addReporting(reportSummary, "GL_REVERSAL_T", "S");
        }
        else {
            addReporting(reportSummary, "GL_ORIGIN_ENTRY_T (ICR)", "S");
        }

        // If these are reversal entries, we need to reverse the entry and
        // modify a few fields
        if (mode == PosterService.MODE_REVERSAL) {
            Reversal reversal = new Reversal(tran);

            // Reverse the debit/credit code
            if (Constants.GL_DEBIT_CODE.equals(reversal.getTransactionDebitCreditCode())) {
                reversal.setTransactionDebitCreditCode(Constants.GL_CREDIT_CODE);
            }
            else if (Constants.GL_CREDIT_CODE.equals(reversal.getTransactionDebitCreditCode())) {
                reversal.setTransactionDebitCreditCode(Constants.GL_DEBIT_CODE);
            }

            UniversityDate udate = universityDateDao.getByPrimaryKey(reversal.getFinancialDocumentReversalDate());
            if (udate != null) {
                reversal.setUniversityFiscalYear(udate.getUniversityFiscalYear());
                reversal.setUniversityFiscalPeriodCode(udate.getUniversityFiscalAccountingPeriod());

                AccountingPeriod ap = accountingPeriodService.getByPeriod(reversal.getUniversityFiscalPeriodCode(), reversal.getUniversityFiscalYear());
                if (ap != null) {
                    if (Constants.ACCOUNTING_PERIOD_STATUS_CLOSED.equals(ap.getUniversityFiscalPeriodStatusCode())) {
                        reversal.setUniversityFiscalYear(runUniversityDate.getUniversityFiscalYear());
                        reversal.setUniversityFiscalPeriodCode(runUniversityDate.getUniversityFiscalAccountingPeriod());
                    }
                    reversal.setFinancialDocumentReversalDate(null);
                    String newDescription = Constants.GL_REVERSAL_DESCRIPTION_PREFIX + reversal.getTransactionLedgerEntryDescription();
                    if (newDescription.length() > 40) {
                        newDescription = newDescription.substring(0, 39);
                    }
                    reversal.setTransactionLedgerEntryDescription(newDescription);
                }
                else {
                    errors.add("Date from university date not in AccountingPeriod table");
                }
            }
            else {
                errors.add("Date from reversal not in UniversityDate table");
            }

            PersistenceService ps = SpringServiceLocator.getPersistenceService();
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
            addReporting(reportSummary, "WARNING", "S");

            originEntryService.createEntry(tran, invalidGroup);
        }
        else {
            // No error so post it
            for (Iterator posterIter = transactionPosters.iterator(); posterIter.hasNext();) {
                PostTransaction poster = (PostTransaction) posterIter.next();
                String actionCode = poster.post(tran, mode, runUniversityDate.getUniversityDate());

                if (actionCode.startsWith("E")) {
                    errors = new ArrayList();
                    errors.add(actionCode);
                    reportError.put(tran, errors);
                }
                else if (actionCode.indexOf("I") >= 0) {
                    addReporting(reportSummary, poster.getDestinationName(), "I");
                }
                else if (actionCode.indexOf("U") >= 0) {
                    addReporting(reportSummary, poster.getDestinationName(), "U");
                }
                else if (actionCode.indexOf("D") >= 0) {
                    addReporting(reportSummary, poster.getDestinationName(), "D");
                }
                else if (actionCode.indexOf("S") >= 0) {
                    addReporting(reportSummary, poster.getDestinationName(), "S");
                }
            }

            if (errors.size() == 0) {
                originEntryService.createEntry(tran, validGroup);

                // Delete the reversal entry
                if (mode == PosterService.MODE_REVERSAL) {
                    reversalDao.delete((Reversal) originalTransaction);
                }
            }
        }
    }

    private static KualiDecimal ONEHUNDRED = new KualiDecimal("100");

    /**
     * This step reads the expenditure table and uses the data to generate Indirect Cost Recovery transactions.
     */
    public void generateIcrTransactions() {
        LOG.debug("generateIcrTransactions() started");

        Date runDate = new Date(dateTimeService.getCurrentDate().getTime());

        OriginEntryGroup group = originEntryGroupService.createGroup(runDate, OriginEntrySource.ICR_TRANSACTIONS, true, true, false);

        Map reportErrors = new HashMap();

        int reportExpendTranRetrieved = 0;
        int reportExpendTranDeleted = 0;
        int reportExpendTranKept = 0;
        int reportOriginEntryGenerated = 0;

        KualiDecimal warningMaxDifference = new KualiDecimal("0.05"); // TODO Put this in APC

        Iterator expenditureTransactions = expenditureTransactionDao.getAllExpenditureTransactions();
        while (expenditureTransactions.hasNext()) {
            ExpenditureTransaction et = (ExpenditureTransaction) expenditureTransactions.next();
            reportExpendTranRetrieved++;

            KualiDecimal transactionAmount = et.getAccountObjectDirectCostAmount();
            KualiDecimal distributionPercent = KualiDecimal.ZERO;
            KualiDecimal distributionAmount = KualiDecimal.ZERO;
            KualiDecimal distributedAmount = KualiDecimal.ZERO;

            Collection automatedEntries = icrAutomatedEntryDao.getEntriesBySeries(et.getUniversityFiscalYear(), et.getAccount().getFinancialIcrSeriesIdentifier(), et.getBalanceTypeCode());
            int automatedEntriesCount = automatedEntries.size();
            if (automatedEntriesCount > 0) {
                int count = 0;
                for (Iterator icrIter = automatedEntries.iterator(); icrIter.hasNext();) {
                    IcrAutomatedEntry icrEntry = (IcrAutomatedEntry) icrIter.next();
                    count++;

                    KualiDecimal generatedTransactionAmount = null;

                    if (icrEntry.getAwardIndrCostRcvyEntryNbr().intValue() == 1) {
                        // Line 1 must have the total percentage of the transaction to distribute
                        distributionPercent = icrEntry.getAwardIndrCostRcvyRatePct().divide(ONEHUNDRED);
                        distributionAmount = transactionAmount.multiply(new KualiDecimal(distributionPercent.toString())).divide(ONEHUNDRED);

                        generatedTransactionAmount = distributionAmount;
                    }
                    else {
                        generatedTransactionAmount = transactionAmount.multiply(new KualiDecimal(icrEntry.getAwardIndrCostRcvyRatePct().divide(ONEHUNDRED).toString())).divide(ONEHUNDRED);
                        distributedAmount = distributedAmount.add(generatedTransactionAmount);

                        // Do we need to round? Round on the last one
                        if (automatedEntriesCount == (count + 1)) {
                            KualiDecimal difference = distributionAmount.subtract(distributedAmount);

                            if (difference.compareTo(KualiDecimal.ZERO) != 0) {
                                if (difference.abs().compareTo(warningMaxDifference) >= 0) {
                                    // TODO Rounding warning
                                }
                                distributedAmount.add(difference);
                            }
                        }
                    }

                    generateTransaction(et, icrEntry, generatedTransactionAmount, runDate, group, reportErrors);
                    reportOriginEntryGenerated = reportOriginEntryGenerated + 2;
                }
            }

            // Delete expenditure record
            expenditureTransactionDao.delete(et);
            reportExpendTranDeleted++;
        }

        List summary = new ArrayList();
        summary.add(new Summary(1, "Number of GL_EXPEND_TRAN_T records retrieved:", reportExpendTranRetrieved));
        summary.add(new Summary(2, "Number of GL_EXPEND_TRAN_T records deleted:", reportExpendTranDeleted));
        summary.add(new Summary(3, "Number of GL_EXPEND_TRAN_T records kept due to errors:", reportExpendTranKept));
        summary.add(new Summary(4, "", 0));
        summary.add(new Summary(3, "Number of GL_ORIGIN_ENTRY_T records generated:", reportOriginEntryGenerated));
        icrGenerationReportService.generateReport(reportErrors, summary, runDate, 0);
    }

    /**
     * 
     * @param et
     * @param icrEntry
     * @param generatedTransactionAmount
     * @param runDate
     * @param group
     */
    private void generateTransaction(ExpenditureTransaction et, IcrAutomatedEntry icrEntry, KualiDecimal generatedTransactionAmount, Date runDate, OriginEntryGroup group, Map reportErrors) {
        OriginEntry e = new OriginEntry();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-DD");

        // @ means we use the field from the expenditure entry, # means we use the ICR field from the account record, otherwise, use
        // the field in the icrEntry
        if ("@".equals(icrEntry.getFinancialObjectCode()) || "#".equals(icrEntry.getFinancialObjectCode())) {
            e.setFinancialObjectCode(et.getObjectCode());
            e.setFinancialSubObjectCode(et.getSubObjectCode());
        }
        else {
            e.setFinancialObjectCode(icrEntry.getFinancialObjectCode());
            if ("@".equals(icrEntry.getFinancialSubObjectCode())) {
                e.setFinancialSubObjectCode(et.getSubObjectCode());
            }
            else {
                e.setFinancialSubObjectCode(et.getSubObjectCode());
            }
        }

        if ("@".equals(icrEntry.getAccountNumber())) {
            e.setAccountNumber(et.getAccountNumber());
            e.setChartOfAccountsCode(et.getChartOfAccountsCode());
            e.setSubAccountNumber(et.getSubAccountNumber());
        }
        else if ("#".equals(icrEntry.getAccountNumber())) {
            e.setAccountNumber(et.getAccount().getIndirectCostRecoveryAcctNbr());
            e.setChartOfAccountsCode(et.getAccount().getChartOfAccountsCode());
            e.setSubAccountNumber(Constants.DASHES_SUB_ACCOUNT_NUMBER);
        }
        else {
            e.setAccountNumber(icrEntry.getAccountNumber());
            e.setSubAccountNumber(icrEntry.getSubAccountNumber());
            e.setChartOfAccountsCode(icrEntry.getChartOfAccountsCode());
        }

        e.setFinancialDocumentTypeCode("ICR");
        e.setFinancialSystemOriginationCode("MF");
        e.setFinancialDocumentNumber(sdf.format(runDate));
        if (Constants.GL_DEBIT_CODE.equals(icrEntry.getTransactionDebitIndicator())) {
            e.setTransactionLedgerEntryDescription(getChargeDescription(icrEntry.getAwardIndrCostRcvyRatePct().divide(ONEHUNDRED), et.getObjectCode(), et.getAccount().getAcctIndirectCostRcvyTypeCd(), et.getAccountObjectDirectCostAmount()));
        }
        else {
            e.setTransactionLedgerEntryDescription(getOffsetDescription(icrEntry.getAwardIndrCostRcvyRatePct().divide(ONEHUNDRED), et.getAccountObjectDirectCostAmount(), et.getChartOfAccountsCode(), et.getAccountNumber()));
        }
        e.setTransactionDate(new java.sql.Date(runDate.getTime()));
        e.setTransactionDebitCreditCode(icrEntry.getTransactionDebitIndicator());
        e.setFinancialBalanceTypeCode(et.getBalanceTypeCode());
        e.setUniversityFiscalYear(et.getUniversityFiscalYear());
        e.setUniversityFiscalPeriodCode(et.getUniversityFiscalAccountingPeriod());

        ObjectCode oc = objectCodeService.getByPrimaryId(e.getUniversityFiscalYear(), e.getChartOfAccountsCode(), e.getFinancialObjectCode());
        if (oc == null) {
            throw new IllegalArgumentException("Unable to find object code in table for " + e.getUniversityFiscalYear() + "," + e.getChartOfAccountsCode() + "," + e.getFinancialObjectCode());
        }
        e.setFinancialObjectTypeCode(oc.getFinancialObjectTypeCode());

        e.setTransactionLedgerEntryAmount(generatedTransactionAmount);

        if (et.getBalanceTypeCode().equals(et.getOption().getExtrnlEncumFinBalanceTypCd()) || et.getBalanceTypeCode().equals(et.getOption().getIntrnlEncumFinBalanceTypCd()) || et.getBalanceTypeCode().equals(et.getOption().getPreencumbranceFinBalTypeCd()) || et.getBalanceTypeCode().equals("CE")) {
            e.setFinancialDocumentNumber("ICR");
        }
        e.setProjectCode(et.getProjectCode());
        if ("--------".equals(et.getOrganizationReferenceId())) {
            e.setOrganizationReferenceId(null);
        }
        else {
            e.setOrganizationReferenceId(et.getOrganizationReferenceId());
        }

        originEntryService.createEntry(e, group);

        // Now generate Offset
        e = new OriginEntry(e);
        if (Constants.GL_DEBIT_CODE.equals(e.getTransactionDebitCreditCode())) {
            e.setTransactionDebitCreditCode(Constants.GL_CREDIT_CODE);
        }
        else {
            e.setTransactionDebitCreditCode(Constants.GL_DEBIT_CODE);
        }
        e.setFinancialSubObjectCode(Constants.DASHES_SUB_OBJECT_CODE);
        e.setFinancialObjectCode(icrEntry.getOffsetBalanceSheetObjectCodeNumber());

        /*
         * Cannot use icrEntry reference objects because Chart, Account, Sub Account, etc may have special values.
         * 
         * if ( icrEntry.getOffsetBalanceSheetObjectCode() == null ) { List warnings = new ArrayList(); warnings.add("Offset Object
         * Code is invalid"); reportErrors.put(e,warnings); } else {
         * e.setFinancialObjectTypeCode(icrEntry.getOffsetBalanceSheetObjectCode().getFinancialObjectTypeCode()); }
         * 
         */
        if (Constants.GL_DEBIT_CODE.equals(icrEntry.getTransactionDebitIndicator())) {
            e.setTransactionLedgerEntryDescription(getChargeDescription(icrEntry.getAwardIndrCostRcvyRatePct().divide(ONEHUNDRED), et.getObjectCode(), et.getAccount().getAcctIndirectCostRcvyTypeCd(), et.getAccountObjectDirectCostAmount()));
        }
        else {
            e.setTransactionLedgerEntryDescription(getOffsetDescription(icrEntry.getAwardIndrCostRcvyRatePct().divide(ONEHUNDRED), et.getAccountObjectDirectCostAmount(), et.getChartOfAccountsCode(), et.getAccountNumber()));
        }

        originEntryService.createEntry(e, group);
    }

    // I don't understand thresholds so I removed this code.
    //
    // Collection thresholds = indirectCostRecoveryThresholdDao.getByAccount(et.getChartOfAccountsCode(),et.getAccountNumber());
    // for (Iterator iter = thresholds.iterator(); iter.hasNext();) {
    // IndirectCostRecoveryThreshold threshold = (IndirectCostRecoveryThreshold)iter.next();
    // if ( threshold.getAwardThresholdAmount().compareTo(threshold.getAwardAccumulatedCostAmount()) > 0 ) {
    // // 1596 - 1654
    // KualiDecimal availableCostAmount = threshold.getAwardThresholdAmount().subtract(threshold.getAwardAccumulatedCostAmount());
    // // subtract previous amount? What is that?
    //
    // if ( amountRemaining.compareTo(availableCostAmount) <= 0 ) {
    //
    // }
    // } else {
    // // 1656 - 1687
    // }
    // }

    private String getChargeDescription(KualiDecimal rate, String objectCode, String type, KualiDecimal amount) {
        NumberFormat nf = NumberFormat.getInstance();

        StringBuffer desc = new StringBuffer("CHG ");
        nf.setMaximumFractionDigits(3);
        nf.setMinimumFractionDigits(3);
        nf.setMaximumIntegerDigits(2);
        nf.setMinimumIntegerDigits(1);
        desc.append(nf.format(rate));
        desc.append("% ON ");
        desc.append(objectCode);
        desc.append(" (");
        desc.append(type);
        desc.append(") ");
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);
        nf.setMaximumIntegerDigits(11);
        nf.setMinimumIntegerDigits(1);
        desc.append(nf.format(amount));
        return desc.toString();
    }

    private String getOffsetDescription(KualiDecimal rate, KualiDecimal amount, String chartOfAccountsCode, String accountNumber) {
        NumberFormat nf = NumberFormat.getInstance();

        StringBuffer desc = new StringBuffer("RCV ");
        nf.setMaximumFractionDigits(3);
        nf.setMinimumFractionDigits(3);
        nf.setMaximumIntegerDigits(2);
        nf.setMinimumIntegerDigits(1);
        desc.append(nf.format(rate));
        desc.append("% ON ");
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);
        nf.setMaximumIntegerDigits(11);
        nf.setMinimumIntegerDigits(1);
        desc.append(nf.format(amount));
        desc.append(" FRM ");
        desc.append(chartOfAccountsCode);
        desc.append("-");
        desc.append(accountNumber);
        return desc.toString();
    }

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

    public void setPosterReport(PosterReport prs) {
        posterReportService = prs;
    }

    public void setIcrGenerationReport(PosterReport prs) {
        icrGenerationReportService = prs;
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

    public void setBeanFactory(BeanFactory bf) throws BeansException {
        beanFactory = bf;
    }

    public void init() {
        LOG.debug("init() started");

        // If we are in test mode
        if (beanFactory.containsBean("testDateTimeService")) {
            dateTimeService = (DateTimeService) beanFactory.getBean("testDateTimeService");
            posterReportService = (PosterReport) beanFactory.getBean("testPosterReport");
        }
    }

    // public void setIndirectCostRecoveryThresholdDao(IndirectCostRecoveryThresholdDao icrtd) {
    // indirectCostRecoveryThresholdDao = icrtd;
    // }
}
