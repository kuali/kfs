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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.module.gl.bo.GlSummary;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.gl.service.OriginEntryService;
import org.kuali.module.gl.service.impl.scrubber.DemergerReportData;
import org.kuali.module.gl.service.impl.scrubber.ScrubberReportData;
import org.kuali.module.gl.util.BalanceEncumbranceReport;
import org.kuali.module.gl.util.BalanceReport;
import org.kuali.module.gl.util.GeneralLedgerPendingEntryReport;
import org.kuali.module.gl.util.LedgerEntryHolder;
import org.kuali.module.gl.util.LedgerReport;
import org.kuali.module.gl.util.Message;
import org.kuali.module.gl.util.PosterOutputSummaryEntry;
import org.kuali.module.gl.util.PosterOutputSummaryReport;
import org.kuali.module.gl.util.Summary;
import org.kuali.module.gl.util.TransactionListingReport;
import org.kuali.module.gl.util.TransactionReport;
import org.kuali.module.labor.bo.LaborBalanceSummary;
import org.kuali.module.labor.document.LaborCorrectionDocument;
import org.kuali.module.labor.report.LaborCorrectionOnlineReport;
import org.kuali.module.labor.report.TransactionSummaryReport;
import org.kuali.module.labor.service.LaborLedgerBalanceService;
import org.kuali.module.labor.service.LaborOriginEntryService;
import org.kuali.module.labor.service.LaborReportService;
import org.kuali.module.labor.util.ReportRegistry;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class provides a set of facilities to generate reports.
 */
@Transactional
public class LaborReportServiceImpl implements LaborReportService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborReportServiceImpl.class);

    private LaborOriginEntryService laborOriginEntryService;
    private OriginEntryService originEntryService;
    private OriginEntryGroupService originEntryGroupService;
    private LaborLedgerBalanceService laborLedgerBalanceService;

    /**
     * @see org.kuali.module.labor.service.LaborReportService#generateInputSummaryReport(java.util.Collection,
     *      org.kuali.module.labor.util.ReportRegistry, java.lang.String, java.util.Date)
     */
    public void generateInputSummaryReport(Collection<OriginEntryGroup> groups, ReportRegistry reportInfo, String reportsDirectory, Date runDate) {
        LOG.info("generateInputSummaryReport() started");

        LedgerEntryHolder ledgerEntries;
        ledgerEntries = groups.size() > 0 ? laborOriginEntryService.getSummariedEntriesByGroups(groups) : new LedgerEntryHolder();

        LedgerReport ledgerReport = new LedgerReport();
        ledgerReport.generateReport(ledgerEntries, runDate, reportInfo.reportTitle(), reportInfo.reportFilename(), reportsDirectory);
    }

    /**
     * @see org.kuali.module.labor.service.LaborReportService#generateInputSummaryReport(org.kuali.module.gl.bo.OriginEntryGroup,
     *      org.kuali.module.labor.util.ReportRegistry, java.lang.String, java.util.Date)
     */
    public void generateInputSummaryReport(OriginEntryGroup group, ReportRegistry reportInfo, String reportsDirectory, Date runDate) {
        LOG.info("generateInputSummaryReport() started");

        List<OriginEntryGroup> groups = new ArrayList<OriginEntryGroup>();
        groups.add(group);

        this.generateInputSummaryReport(groups, reportInfo, reportsDirectory, runDate);
    }

    /**
     * @see org.kuali.module.labor.service.LaborReportService#generateErrorTransactionListing(org.kuali.module.gl.bo.OriginEntryGroup,
     *      org.kuali.module.labor.util.ReportRegistry, java.lang.String, java.util.Date)
     */
    public void generateErrorTransactionListing(OriginEntryGroup group, ReportRegistry reportInfo, String reportsDirectory, Date runDate) {
        LOG.info("generateErrorTransactionListing() started");

        Iterator entries = laborOriginEntryService.getEntriesByGroup(group);
        TransactionListingReport transactionListingReport = new TransactionListingReport();
        transactionListingReport.generateReport(entries, runDate, reportInfo.reportTitle(), reportInfo.reportFilename(), reportsDirectory);
    }

    /**
     * @see org.kuali.module.labor.service.LaborReportService#generateStatisticsReport(java.util.List, java.util.Map,
     *      org.kuali.module.labor.util.ReportRegistry, java.lang.String, java.util.Date)
     */
    public void generateStatisticsReport(List<Summary> reportSummary, Map<Transaction, List<Message>> errors, ReportRegistry reportInfo, String reportsDirectory, Date runDate) {
        LOG.info("generateStatisticsReport() started");

        TransactionReport transactionReport = new TransactionReport();
        transactionReport.generateReport(errors, reportSummary, runDate, reportInfo.reportTitle(), reportInfo.reportFilename(), reportsDirectory);
    }

    /**
     * @see org.kuali.module.labor.service.LaborReportService#generateStatisticsReport(java.util.List,
     *      org.kuali.module.labor.util.ReportRegistry, java.lang.String, java.util.Date)
     */
    public void generateStatisticsReport(List<String> reportSummary, ReportRegistry reportInfo, String reportsDirectory, Date runDate) {
        LOG.info("generateStatisticsReport() started");

        TransactionSummaryReport transactionSummaryReport = new TransactionSummaryReport();
        transactionSummaryReport.generateReport(reportSummary, runDate, reportInfo.reportTitle(), reportInfo.reportFilename(), reportsDirectory);
    }

    /**
     * @see org.kuali.module.labor.service.LaborReportService#generateOutputSummaryReport(java.util.Collection,
     *      org.kuali.module.labor.util.ReportRegistry, java.lang.String, java.util.Date)
     */
    public void generateOutputSummaryReport(Collection<OriginEntryGroup> groups, ReportRegistry reportInfo, String reportsDirectory, Date runDate) {
        LOG.info("generateOutputSummaryReport() started");

        PosterOutputSummaryReport posterOutputSummaryReport = new PosterOutputSummaryReport();
        Map<String, PosterOutputSummaryEntry> posterOutputSummary = laborOriginEntryService.getPosterOutputSummaryByGroups(groups);
        posterOutputSummaryReport.generateReport(posterOutputSummary, runDate, reportInfo.reportTitle(), reportInfo.reportFilename(), reportsDirectory);
    }

    /**
     * @see org.kuali.module.labor.service.LaborReportService#generateOutputSummaryReport(org.kuali.module.gl.bo.OriginEntryGroup,
     *      org.kuali.module.labor.util.ReportRegistry, java.lang.String, java.util.Date)
     */
    public void generateOutputSummaryReport(OriginEntryGroup group, ReportRegistry reportInfo, String reportsDirectory, Date runDate) {
        LOG.info("generateOutputSummaryReport() started");

        List<OriginEntryGroup> groups = new ArrayList<OriginEntryGroup>();
        groups.add(group);
        this.generateOutputSummaryReport(groups, reportInfo, reportsDirectory, runDate);
    }

    /**
     * @see org.kuali.module.labor.service.LaborReportService#generateMonthlyBalanceSummaryReport(java.lang.Integer, java.util.List,
     *      org.kuali.module.labor.util.ReportRegistry, java.lang.String, java.util.Date)
     */
    public void generateMonthlyBalanceSummaryReport(Integer fiscalYear, List<String> balanceTypes, ReportRegistry reportInfo, String reportsDirectory, Date runDate) {
        LOG.info("generateMonthlyBalanceSummaryReport() started");

        List<LaborBalanceSummary> balanceSummary = laborLedgerBalanceService.findBalanceSummary(fiscalYear, balanceTypes);
        List<GlSummary> summary = new ArrayList<GlSummary>(balanceSummary);

        BalanceReport report = new BalanceReport();
        String filePrefix = reportInfo.reportFilename() + "_" + fiscalYear;
        String reportTitle = reportInfo.reportTitle() + " for Fiscal Year " + fiscalYear;
        report.generateReport(summary, balanceTypes, runDate, reportTitle, filePrefix, reportsDirectory);
    }

    /**
     * @see org.kuali.module.labor.service.LaborReportService#generateBalanceSummaryReport(java.lang.Integer, java.util.List,
     *      org.kuali.module.labor.util.ReportRegistry, java.lang.String, java.util.Date)
     */
    public void generateBalanceSummaryReport(Integer fiscalYear, List<String> balanceTypes, ReportRegistry reportInfo, String reportsDirectory, Date runDate) {
        LOG.info("generateBalanceSummaryReport() started");

        List<LaborBalanceSummary> balanceSummary = laborLedgerBalanceService.findBalanceSummary(fiscalYear, balanceTypes);
        List<GlSummary> summary = new ArrayList<GlSummary>(balanceSummary);

        BalanceEncumbranceReport report = new BalanceEncumbranceReport();
        String filePrefix = reportInfo.reportFilename() + "_" + fiscalYear;
        String reportTitle = reportInfo.reportTitle() + " for Fiscal Year " + fiscalYear;
        report.generateReport(summary, balanceTypes, runDate, reportTitle, filePrefix, reportsDirectory);
    }

    /**
     * @see org.kuali.module.labor.service.LaborReportService#generateFeedSummaryReport(org.kuali.module.gl.bo.OriginEntryGroup,
     *      org.kuali.module.labor.util.ReportRegistry, java.lang.String, java.util.Date)
     */
    public void generateFeedSummaryReport(OriginEntryGroup group, ReportRegistry reportInfo, String reportsDirectory, Date runDate) {
        LOG.info("generateFeedSummaryReport() started");
        
        List<OriginEntryGroup> groups = new ArrayList<OriginEntryGroup>();
        groups.add(group);

        LedgerEntryHolder ledgerEntries;
        ledgerEntries = groups.size() > 0 ? originEntryService.getSummaryByGroupId(groups) : new LedgerEntryHolder();

        LedgerReport ledgerReport = new LedgerReport();
        ledgerReport.generateReport(ledgerEntries, runDate, reportInfo.reportTitle(), reportInfo.reportFilename(), reportsDirectory);
    }

    /**
     * @see org.kuali.module.gl.service.ReportService#generateScrubberLedgerSummaryReportBatch(java.util.Date, java.util.Collection)
     */
    public void generateScrubberLedgerSummaryReportBatch(Collection groups, String reportsDirectory, Date runDate) {
        // LOG.debug("generateScrubberLedgerSummaryReport() started");

        LedgerReport ledgerReport = new LedgerReport();
        LedgerEntryHolder ledgerEntries = new LedgerEntryHolder();
        if (groups.size() > 0) {
            ledgerEntries = laborOriginEntryService.getSummaryByGroupId(groups);
        }

        ledgerReport.generateReport(ledgerEntries, runDate, "Labor Ledger Report", "labor_scrubber_ledger", reportsDirectory);
    }

    /**
     * @see org.kuali.module.gl.service.ReportService#generateScrubberLedgerSummaryReportOnline(java.util.Date,
     *      org.kuali.module.gl.bo.OriginEntryGroup)
     */
    public void generateScrubberLedgerSummaryReportOnline(OriginEntryGroup group, String documentNumber, String reportsDirectory, Date runDate) {
        // LOG.debug("generateScrubberLedgerSummaryReport() started");

        LedgerReport ledgerReport = new LedgerReport();
        LedgerEntryHolder ledgerEntries = new LedgerEntryHolder();

        Collection g = new ArrayList();
        g.add(group);

        ledgerEntries = laborOriginEntryService.getSummaryByGroupId(g);

        ledgerReport.generateReport(ledgerEntries, runDate, "Labor Ledger Report", "labor_scrubber_ledger_" + documentNumber, reportsDirectory);
    }

    /**
     * @see org.kuali.module.gl.service.ReportService#generateScrubberStatisticsReport(java.util.Date,
     *      org.kuali.module.gl.service.impl.scrubber.ScrubberReportData, java.util.Map)
     */
    public void generateBatchScrubberStatisticsReport(ScrubberReportData scrubberReport, Map<Transaction, List<Message>> scrubberReportErrors, String reportsDirectory, Date runDate) {
        // LOG.debug("generateScrubberStatisticsReport() started");

        List tranKeys = new ArrayList();
        tranKeys.addAll(scrubberReportErrors.keySet());

        Collections.sort(tranKeys, new Comparator<Transaction>() {
            public int compare(Transaction t1, Transaction t2) {
                StringBuffer sb1 = new StringBuffer();
                sb1.append(t1.getFinancialDocumentTypeCode());
                sb1.append(t1.getFinancialSystemOriginationCode());
                sb1.append(t1.getDocumentNumber());
                sb1.append(t1.getChartOfAccountsCode());
                sb1.append(t1.getAccountNumber());
                sb1.append(t1.getSubAccountNumber());
                sb1.append(t1.getFinancialBalanceTypeCode());

                StringBuffer sb2 = new StringBuffer();
                sb2.append(t2.getFinancialDocumentTypeCode());
                sb2.append(t2.getFinancialSystemOriginationCode());
                sb2.append(t2.getDocumentNumber());
                sb2.append(t2.getChartOfAccountsCode());
                sb2.append(t2.getAccountNumber());
                sb2.append(t2.getSubAccountNumber());
                sb2.append(t2.getFinancialBalanceTypeCode());
                return sb1.toString().compareTo(sb2.toString());
            }
        });

        List summary = buildScrubberReportSummary(scrubberReport);

        TransactionReport transactionReport = new TransactionReport();
        transactionReport.generateReport(tranKeys, scrubberReportErrors, summary, runDate, "Labor Scrubber Report ", "labor_scrubber_errors_", reportsDirectory);
    }

    /**
     * @see org.kuali.module.gl.service.ReportService#generateScrubberStatisticsReport(java.util.Date,
     *      org.kuali.module.gl.service.impl.scrubber.ScrubberReportData, java.util.Map)
     */
    public void generateOnlineScrubberStatisticsReport(Integer groupId, ScrubberReportData scrubberReport, Map<Transaction, List<Message>> scrubberReportErrors, String documentNumber, String reportsDirectory, Date runDate) {
        // log.debug("generateScrubberStatisticsReport() started");

        List summary = buildScrubberReportSummary(scrubberReport);

        TransactionReport transactionReport = new TransactionReport();
        transactionReport.generateReport(scrubberReportErrors, summary, runDate, "Labor Scrubber Report ", "labor_scrubber_errors_" + documentNumber, reportsDirectory);
    }

    /**
     * @see org.kuali.module.gl.service.ReportService#generateScrubberDemergerStatisticsReports(java.util.Date,
     *      org.kuali.module.gl.service.impl.scrubber.DemergerReportData)
     */
    public void generateScrubberDemergerStatisticsReports(DemergerReportData demergerReport, String reportsDirectory, Date runDate) {
        // log.debug("generateScrubberDemergerStatisticsReports() started");

        List summary = buildDemergerReportSummary(demergerReport);

        Map<Transaction, List<Message>> empty = new HashMap<Transaction, List<Message>>();

        TransactionReport transactionReport = new TransactionReport();
        transactionReport.generateReport(empty, summary, runDate, "Labor Demerger Report ", "labor_demerger", reportsDirectory);
    }

    /**
     * @see org.kuali.module.gl.service.ReportService#generateScrubberBadBalanceTypeListingReport(java.util.Date,
     *      java.util.Collection)
     */
    public void generateScrubberBadBalanceTypeListingReport(Collection groups, String reportsDirectory, Date runDate) {
        // log.debug("generateScrubberBadBalanceTypeListingReport() started");

        Iterator i = null;
        if (groups.size() > 0) {
            i = laborOriginEntryService.getBadBalanceEntries(groups);
        }

        TransactionListingReport rept = new TransactionListingReport();
        rept.generateReport(i, runDate, "Labor Scrubber Input Transactions with Bad Balance Types", "labor_scrubber_badbal", reportsDirectory);
    }

    public void generateScrubberTransactionsOnline(OriginEntryGroup validGroup, String documentNumber, String reportsDirectory, Date runDate) {
        // log.debug("generateScrubberTransactionsOnline() started");

        Iterator ti = laborOriginEntryService.getEntriesByGroupAccountOrder(validGroup);

        TransactionListingReport rept = new TransactionListingReport();
        rept.generateReport(ti, runDate, "Labor Output Transaction Listing From the Scrubber", "labor_scrubber_listing_" + documentNumber, reportsDirectory);
    }

    /**
     * @see org.kuali.module.gl.service.ReportService#generateScrubberRemovedTransactions(java.util.Date,
     *      org.kuali.module.gl.bo.OriginEntryGroup)
     */
    public void generateScrubberRemovedTransactions(OriginEntryGroup errorGroup, String reportsDirectory, Date runDate) {
        // log.debug("generateScrubberRemovedTransactions() started");

        Iterator ti = laborOriginEntryService.getEntriesByGroupListingReportOrder(errorGroup);

        TransactionListingReport rept = new TransactionListingReport();
        rept.generateReport(ti, runDate, "Labor Error Listing - Transactions Removed From the Scrubber", "labor_scrubber_error_listing_", reportsDirectory);
    }


    /**
     * @see org.kuali.module.labor.service.LaborReportService#generateCorrectionOnlineReport(org.kuali.module.labor.document.LaborCorrectionDocument,
     *      java.lang.String, java.util.Date)
     */
    public void generateCorrectionOnlineReport(LaborCorrectionDocument cDocument, String reportsDirectory, Date runDate) {
        LOG.debug("correctionOnlineReport() started");
        LaborCorrectionOnlineReport correctionOnlineReport = new LaborCorrectionOnlineReport();
        correctionOnlineReport.generateReport(cDocument, reportsDirectory, runDate);
    }

    /**
     * Sets the originEntryGroupService attribute value.
     * 
     * @param originEntryGroupService The originEntryGroupService to set.
     */
    public void setOriginEntryGroupService(OriginEntryGroupService originEntryGroupService) {
        this.originEntryGroupService = originEntryGroupService;
    }

    /**
     * Sets the laborOriginEntryService attribute value.
     * 
     * @param laborOriginEntryService The laborOriginEntryService to set.
     */
    public void setLaborOriginEntryService(LaborOriginEntryService laborOriginEntryService) {
        this.laborOriginEntryService = laborOriginEntryService;
    }


    /**
     * Generate the header for the scrubber status report.
     * 
     * @param scrubberReport
     * @return list of report summaries to be printed
     */
    private List<Summary> buildScrubberReportSummary(ScrubberReportData scrubberReport) {
        List<Summary> reportSummary = new ArrayList<Summary>();

        reportSummary.add(new Summary(2, "UNSCRUBBED RECORDS READ", new Integer(scrubberReport.getNumberOfUnscrubbedRecordsRead())));
        reportSummary.add(new Summary(3, "SCRUBBED RECORDS WRITTEN", new Integer(scrubberReport.getNumberOfScrubbedRecordsWritten())));
        reportSummary.add(new Summary(4, "ERROR RECORDS WRITTEN", new Integer(scrubberReport.getNumberOfErrorRecordsWritten())));
        reportSummary.add(new Summary(11, "TOTAL OUTPUT RECORDS WRITTEN", new Integer(scrubberReport.getTotalNumberOfRecordsWritten())));
        reportSummary.add(new Summary(12, "EXPIRED ACCOUNTS FOUND", new Integer(scrubberReport.getNumberOfExpiredAccountsFound())));

        return reportSummary;
    }


    /**
     * Generate the header for the demerger status report.
     * 
     * @param demergerReport
     * @return list of report summaries to be printed
     */
    private List<Summary> buildDemergerReportSummary(DemergerReportData demergerReport) {
        List<Summary> reportSummary = new ArrayList<Summary>();

        reportSummary.add(new Summary(1, "SCRUBBER ERROR TRANSACTIONS READ", new Integer(demergerReport.getErrorTransactionsRead())));
        reportSummary.add(new Summary(3, "DEMERGER ERRORS SAVED", new Integer(demergerReport.getErrorTransactionsSaved())));
        reportSummary.add(new Summary(4, "DEMERGER VALID TRANSACTIONS SAVED", new Integer(demergerReport.getValidTransactionsSaved())));

        return reportSummary;
    }

    /**
     * Sets the laborLedgerBalanceService attribute value.
     * 
     * @param laborLedgerBalanceService The laborLedgerBalanceService to set.
     */
    public void setLaborLedgerBalanceService(LaborLedgerBalanceService laborLedgerBalanceService) {
        this.laborLedgerBalanceService = laborLedgerBalanceService;
    }

    /**
     * Sets the originEntryService attribute value.
     * 
     * @param originEntryService The originEntryService to set.
     */
    public void setOriginEntryService(OriginEntryService originEntryService) {
        this.originEntryService = originEntryService;
    }
}