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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.kuali.core.bo.user.Options;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.OptionsService;
import org.kuali.module.gl.batch.poster.PostTransaction;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.bo.UniversityDate;
import org.kuali.module.gl.service.BalanceService;
import org.kuali.module.gl.service.OriginEntryService;
import org.kuali.module.gl.service.PosterService;
import org.kuali.module.gl.service.ReportService;
import org.kuali.module.gl.service.impl.scrubber.DemergerReportData;
import org.kuali.module.gl.service.impl.scrubber.Message;
import org.kuali.module.gl.service.impl.scrubber.ScrubberReportData;
import org.kuali.module.gl.util.BalanceEncumbranceReport;
import org.kuali.module.gl.util.BalanceReport;
import org.kuali.module.gl.util.LedgerEntryHolder;
import org.kuali.module.gl.util.LedgerReport;
import org.kuali.module.gl.util.Summary;
import org.kuali.module.gl.util.TransactionListingReport;
import org.kuali.module.gl.util.TransactionReport;

/**
 * @author Laran Evans <lc278@cornell.edu>
 * @version $Id$
 */
public class ReportServiceImpl implements ReportService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ReportServiceImpl.class);

    String reportsDirectory;
    private OriginEntryService originEntryService;
    private DateTimeService dateTimeService;
    private BalanceService balanceService;
    private OptionsService optionsService;

    public ReportServiceImpl() {
        super();

        ResourceBundle rb = ResourceBundle.getBundle("configuration");
        reportsDirectory = rb.getString("htdocs.directory") + "/reports";
    }

    /**
     * 
     * @see org.kuali.module.gl.service.ReportService#generatePosterStatisticsReport(java.util.Date, java.util.Map, java.util.Map, int)
     */
    public void generatePosterStatisticsReport(Date runDate, Map<String,Integer> reportSummary, List<PostTransaction> transactionPosters, Map<Transaction,List<Message>> reportErrors, int mode) {
        LOG.debug("generatePosterStatisticsReport() started");

        // Convert our summary to a list of items for the report
        List summary = new ArrayList();
        if ( mode == PosterService.MODE_REVERSAL ) {
            summary.add(new Summary(1, "Number of GL_REVERSAL_T records selected:", (Integer) reportSummary.get("GL_REVERSAL_T,S")));            
        } else {
            summary.add(new Summary(1, "Number of GL_ORIGIN_ENTRY_T records selected:", (Integer) reportSummary.get("GL_ORIGIN_ENTRY_T,S")));
        }
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

        TransactionReport tr = new TransactionReport();

        String title = "Poster Report ";
        String filename;
        if (mode == PosterService.MODE_ENTRIES) {
            title = title + "(Post pending entries)";
            filename = "poster_main";
        }
        else if (mode == PosterService.MODE_ICR) {
            title = title + "(Post ICR entries)";
            filename = "poster_icr";
        }
        else {
            title = title + "(Post reversal entries)";
            filename = "poster_reversal";
        }

        tr.generateReport(reportErrors, summary, runDate, title, filename, reportsDirectory);
    }

    /**
     * 
     * @see org.kuali.module.gl.service.ReportService#generateIcrEncumrbanceStatisticsReport(java.util.Date, int, int)
     */
    public void generateIcrEncumbranceStatisticsReport(Date runDate,int totalOfIcrEncumbrances,int totalOfEntriesGenerated) {
        LOG.debug("generateIcrEncumrbanceStatisticsReport() started");

        List reportSummaryList = new ArrayList();
        reportSummaryList.add(new Summary(1, "Number of ICR Encumbrances retrived:", totalOfIcrEncumbrances));
        reportSummaryList.add(new Summary(2, "Number of Origin Entries generated:", totalOfEntriesGenerated));

        Map<Transaction,List<Message>> errors = new HashMap<Transaction,List<Message>>();

        TransactionReport tr = new TransactionReport();
        tr.generateReport(errors, reportSummaryList, runDate, "ICR Encumbrance Report", "icr_encumbrance", reportsDirectory);
    }

    /**
     * 
     * @see org.kuali.module.gl.service.ReportService#generatePosterIcrStatisticsReport(java.util.Date, java.util.Map, int, int, int, int)
     */
    public void generatePosterIcrStatisticsReport(Date runDate, Map<Transaction,List<Message>> reportErrors, int reportExpendTranRetrieved,int reportExpendTranDeleted,int reportExpendTranKept,int reportOriginEntryGenerated) {
        LOG.debug("generatePosterIcrStatisticsReport() started");

        List summary = new ArrayList();
        summary.add(new Summary(1, "Number of GL_EXPEND_TRAN_T records retrieved:", reportExpendTranRetrieved));
        summary.add(new Summary(2, "Number of GL_EXPEND_TRAN_T records deleted:", reportExpendTranDeleted));
        summary.add(new Summary(3, "Number of GL_EXPEND_TRAN_T records kept due to errors:", reportExpendTranKept));
        summary.add(new Summary(4, "", 0));
        summary.add(new Summary(3, "Number of GL_ORIGIN_ENTRY_T records generated:", reportOriginEntryGenerated));

        TransactionReport tr = new TransactionReport();
        tr.generateReport(reportErrors, summary, runDate, "ICR Generation Report", "icr_generation", reportsDirectory);
    }

    /**
     * 
     * @see org.kuali.module.gl.service.ReportService#generateScrubberledgerSummaryReport(java.util.Date, java.util.Collection, java.lang.String)
     */
    public void generateScrubberLedgerSummaryReport(Date runDate, Collection groups, String title) {
        LOG.debug("generateScrubberLedgerSummaryReport() started");

        LedgerReport ledgerReport = new LedgerReport();
        LedgerEntryHolder ledgerEntries = new LedgerEntryHolder();
        if ( groups.size() > 0 ) {
            ledgerEntries = originEntryService.getSummaryByGroupId(groups);
        }

        ledgerReport.generateReport(ledgerEntries, runDate, "Ledger Report", "scrubber_ledger", reportsDirectory);
    }

    /**
     * 
     * @see org.kuali.module.gl.service.ReportService#generateScrubberStatisticsReport(java.util.Date, org.kuali.module.gl.service.impl.scrubber.ScrubberReportData, java.util.Map)
     */
    public void generateScrubberStatisticsReport(Date runDate, ScrubberReportData scrubberReport, Map<Transaction,List<Message>> scrubberReportErrors) {
        LOG.debug("generateScrubberStatisticsReport() started");

        List summary = buildScrubberReportSummary(scrubberReport);
        
        TransactionReport transactionReport = new TransactionReport();
        transactionReport.generateReport(scrubberReportErrors, summary, runDate, "Scrubber Report ", "scrubber", reportsDirectory);
    }

    /**
     * 
     * @see org.kuali.module.gl.service.ReportService#generateScrubberDemergerStatisticsReports(java.util.Date, org.kuali.module.gl.service.impl.scrubber.DemergerReportData)
     */
    public void generateScrubberDemergerStatisticsReports(Date runDate, DemergerReportData demergerReport) {
        LOG.debug("generateScrubberDemergerStatisticsReports() started");

        List summary = buildDemergerReportSummary(demergerReport);

        Map<Transaction,List<Message>> empty = new HashMap<Transaction,List<Message>>();

        TransactionReport transactionReport = new TransactionReport();
        transactionReport.generateReport(empty, summary, runDate, "Demerger Report ", "demerger", reportsDirectory);
    }

    /**
     * 
     * @see org.kuali.module.gl.service.ReportService#generateScrubberBadBalanceTypeListingReport(java.util.Date, java.util.Collection)
     */
    public void generateScrubberBadBalanceTypeListingReport(Date runDate, Collection groups) {
        LOG.debug("generateScrubberBadBalanceTypeListingReport() started");

        Iterator i = null;
        if ( groups.size() > 0 ) {
            i = originEntryService.getBadBalanceEntries(groups);
        }

        TransactionListingReport rept = new TransactionListingReport();
        rept.generateReport(i, runDate, "Scrubber Input Transactions with Bad Balance Types", "scrubber_badbal", reportsDirectory);
    }

    /**
     * 
     * @see org.kuali.module.gl.service.ReportService#generateScrubberRemovedTransactions(java.util.Date, org.kuali.module.gl.bo.OriginEntryGroup)
     */
    public void generateScrubberRemovedTransactions(Date runDate,OriginEntryGroup errorGroup) {
        LOG.debug("generateScrubberRemovedTransactions() started");

        Iterator ti = originEntryService.getEntriesByGroupAccountOrder(errorGroup);

        TransactionListingReport rept = new TransactionListingReport();
        rept.generateReport(ti, runDate, "Error Listing - Transactions Remove From the Scrubber", "scrubber_errors", reportsDirectory);
    }

    /**
     * 
     * @see org.kuali.module.gl.service.ReportService#generateGlSummary(java.util.Date, int, java.util.List)
     */
    public void generateGlSummary(Date runDate,Options year,String reportType) {
        LOG.debug("generateGlSummary() started");

        List balanceTypeCodes = new ArrayList();
        if ( "act".equals(reportType) ) {
            balanceTypeCodes.add(year.getActualFinancialBalanceTypeCd());
        } else {
            balanceTypeCodes.add(year.getBudgetCheckingBalanceTypeCd());

            // TODO these may need fields in the fs_option_t table
            balanceTypeCodes.add("BB");
            balanceTypeCodes.add("MB");
        }

        List balances = balanceService.getGlSummary(year.getUniversityFiscalYear(), balanceTypeCodes);

        BalanceReport rept = new BalanceReport();
        rept.generateReport(runDate,balances,year.getUniversityFiscalYearName(),balanceTypeCodes,"glsummary_" + year.getUniversityFiscalYear() + "_" + reportType,reportsDirectory);
    }

    /**
     * 
     * @see org.kuali.module.gl.service.ReportService#generateGlEncumbranceSummary(java.util.Date, int, java.util.List, java.lang.String)
     */
    public void generateGlEncumbranceSummary(Date runDate,Options year,String reportType) {
        LOG.debug("generateGlEncumbranceSummary() started");

        List balanceTypeCodes = new ArrayList();
        balanceTypeCodes.add(year.getExtrnlEncumFinBalanceTypCd());
        balanceTypeCodes.add(year.getIntrnlEncumFinBalanceTypCd());
        balanceTypeCodes.add(year.getPreencumbranceFinBalTypeCd());
        balanceTypeCodes.add(year.getCostShareEncumbranceBalanceTypeCode());

        List balances = balanceService.getGlSummary(year.getUniversityFiscalYear(), balanceTypeCodes);

        BalanceEncumbranceReport rept = new BalanceEncumbranceReport();
        rept.generateReport(runDate,balances,year.getUniversityFiscalYearName(),balanceTypeCodes,"glsummary_" + year.getUniversityFiscalYear() + "_" + reportType,reportsDirectory);
    }

    public void generateYearEndEncumbranceForwardReports(Date runDate, List reportSummary, Map reportErrors, Map ledgerEntries) {
        LOG.debug("Entering generateYearEndEncumbranceReports()");
        TransactionReport transactionReport = new TransactionReport();
        String title = "Encumbrance Closing Report ";
        transactionReport.generateReport(null, reportSummary, runDate, title, "year_end_encumbrance_closing", reportsDirectory);
    }

    public void generateYearEndBalanceForwardReports(Date runDate, List reportSummary, Map reportErrors, Map ledgerEntries) {
        LOG.debug("Entering generateYearEndBalanceForwardReports()");
        TransactionReport transactionReport = new TransactionReport();
        String title = "Balance Forward Report ";
        transactionReport.generateReport(null, reportSummary, runDate, title, "year_end_balance_forward", reportsDirectory);
    }

    public void generatePosterMainLedgerSummaryReport(Date runDate, Collection groups) {
        LOG.debug("generatePosterMainLedgerSummaryReport() started");

        LedgerEntryHolder ledgerEntries = new LedgerEntryHolder();
        if ( groups.size() > 0) {
            ledgerEntries = originEntryService.getSummaryByGroupId(groups);
        }

        LedgerReport ledgerReport = new LedgerReport();
        ledgerReport.generateReport(ledgerEntries, runDate, "Main Poster Input Transactions", "poster_main_ledger", reportsDirectory);
    }

    public void generatePosterIcrLedgerSummaryReport(Date runDate, Collection groups) {
        LOG.debug("generatePosterIcrLedgerSummaryReport() started");

        LedgerEntryHolder ledgerEntries = new LedgerEntryHolder();
        if ( groups.size() > 0) {
            ledgerEntries = originEntryService.getSummaryByGroupId(groups);
        }

        LedgerReport ledgerReport = new LedgerReport();
        ledgerReport.generateReport(ledgerEntries, runDate, "Icr Poster Input Transactions", "poster_icr_ledger", reportsDirectory);
    }

    public void generatePosterReversalLedgerSummaryReport(Date runDate, Collection groups) {
        LOG.debug("generatePosterReversalLedgerSummaryReport() started");

        LedgerEntryHolder ledgerEntries = new LedgerEntryHolder();
        if ( groups.size() > 0) {
            ledgerEntries = originEntryService.getSummaryByGroupId(groups);
        }

        LedgerReport ledgerReport = new LedgerReport();
        ledgerReport.generateReport(ledgerEntries, runDate, "Reversal Poster Input Transactions", "poster_reversal_ledger", reportsDirectory);
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
        reportSummary.add(new Summary(5, "OFFSET TRANSACTIONS BYPASSED", new Integer(demergerReport.getOffsetTransactionsBypassed())));
        reportSummary.add(new Summary(6, "CAPITALIZATION TRANSACTIONS BYPASSED", new Integer(demergerReport.getCapitalizationTransactionsBypassed())));
        reportSummary.add(new Summary(7, "LIABILITY TRANSACTIONS BYPASSED", new Integer(demergerReport.getLiabilityTransactionsBypassed())));
        reportSummary.add(new Summary(8, "TRANSFER TRANSACTIONS BYPASSED", new Integer(demergerReport.getTransferTransactionsBypassed())));
        reportSummary.add(new Summary(9, "COST SHARE TRANSACTIONS BYPASSED", new Integer(demergerReport.getCostShareTransactionsBypassed())));
        reportSummary.add(new Summary(10, "COST SHARE ENC TRANSACTIONS BYPASSED", new Integer(demergerReport.getCostShareEncumbranceTransactionsBypassed())));

        return reportSummary;
    }

    /**
     * Sets the originEntryService attribute value.
     * 
     * @param originEntryService The originEntryService to set.
     */
    public void setOriginEntryService(OriginEntryService originEntryService) {
        this.originEntryService = originEntryService;
    }
    public void setBalanceService(BalanceService bs) {
        balanceService = bs;
    }
    public void setOptionsService(OptionsService os) {
        optionsService = os;
    }
    public void setDateTimeService(DateTimeService dts) {
        dateTimeService = dts;
    }
}
