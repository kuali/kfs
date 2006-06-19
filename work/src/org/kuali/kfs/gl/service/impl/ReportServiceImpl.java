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
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.service.OriginEntryService;
import org.kuali.module.gl.service.PosterService;
import org.kuali.module.gl.service.ReportService;
import org.kuali.module.gl.service.impl.scrubber.DemergerReportData;
import org.kuali.module.gl.service.impl.scrubber.Message;
import org.kuali.module.gl.service.impl.scrubber.ScrubberReportData;
import org.kuali.module.gl.util.LedgerEntryHolder;
import org.kuali.module.gl.util.LedgerReport;
import org.kuali.module.gl.util.Summary;
import org.kuali.module.gl.util.TransactionReportGenerator;
import org.kuali.module.gl.util.TransactionReport;

/**
 * @author Laran Evans <lc278@cornell.edu>
 * @version $Id$
 */
public class ReportServiceImpl implements ReportService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ReportServiceImpl.class);

    String reportsDirectory;
    private OriginEntryService originEntryService;

    public ReportServiceImpl() {
        super();

        ResourceBundle rb = ResourceBundle.getBundle("configuration");
        reportsDirectory = rb.getString("htdocs.directory") + "/reports";
    }

    /**
     * 
     * @see org.kuali.module.gl.service.ReportService#generateScrubberledgerSummaryReport(java.util.Date, java.util.Collection, java.lang.String)
     */
    public void generateScrubberLedgerSummaryReport(Date runDate, Collection groups, String title) {
        LOG.debug("generateScrubberLedgerSummaryReport() started");

        LedgerReport ledgerReport = new LedgerReport();
        LedgerEntryHolder ledgerEntries = originEntryService.getSummaryByGroupId(groups);
        ledgerReport.generateReport(ledgerEntries, runDate, "Ledger Report", "ledger", reportsDirectory);
    }

    /**
     * 
     * @see org.kuali.module.gl.service.ReportService#generateScrubberStatisticsReport(java.util.Date, org.kuali.module.gl.service.impl.scrubber.ScrubberReportData, java.util.Map)
     */
    public void generateScrubberStatisticsReport(Date runDate, ScrubberReportData scrubberReport, Map<Transaction,List<Message>> scrubberReportErrors) {
        LOG.debug("generateScrubberStatisticsReport() started");

        List summary = buildScrubberReportSummary(scrubberReport);
        
        String title = "Scrubber Report ";
        TransactionReport transactionReport = new TransactionReport();
        transactionReport.generateReport(scrubberReportErrors, summary, runDate, title, "scrubber", reportsDirectory);
    }

    /**
     * 
     * @see org.kuali.module.gl.service.ReportService#generateScrubberDemergerStatisticsReports(java.util.Date, org.kuali.module.gl.service.impl.scrubber.DemergerReportData)
     */
    public void generateScrubberDemergerStatisticsReports(Date runDate, DemergerReportData demergerReport) {
        LOG.debug("generateScrubberDemergerStatisticsReports() started");

        List summary = buildDemergerReportSummary(demergerReport);

        Map<Transaction,List<Message>> empty = new HashMap<Transaction,List<Message>>();

        String title = "Demerger Report ";
        TransactionReport transactionReport = new TransactionReport();
        transactionReport.generateReport(empty, summary, runDate, title, "demerger", reportsDirectory);
    }

    /**
     * 
     * @see org.kuali.module.gl.service.ReportService#generateScrubberBadBalanceTypeListingReport(java.util.Date, java.util.Collection)
     */
    public void generateScrubberBadBalanceTypeListingReport(Date runDate, Collection groups) {
        LOG.debug("generateScrubberBadBalanceTypeListingReport() started");
        // TODO Write this
    }

    /**
     * 
     * @see org.kuali.module.gl.service.ReportService#generateScrubberRemovedTransactions(java.util.Date, org.kuali.module.gl.bo.OriginEntryGroup)
     */
    public void generateScrubberRemovedTransactions(Date runDate,OriginEntryGroup errorGroup) {
        LOG.debug("generateScrubberRemovedTransactions() started");
        // TODO Write this
    }
    
    public void generateIcrReports(Date runDate, List reportSummary, Map reportErrors, Map ledgerEntries) {
        LOG.debug("Entering generateIcrReports()");
        TransactionReport tr = new TransactionReport();
        String title = "ICR Generation Report";
        tr.generateReport(reportErrors, reportSummary, runDate, title, "icr_generation", reportsDirectory);
    }

    public void generatePosterReports(Date runDate, List reportSummary, Map reportErrors, Map ledgerEntries, int mode) {
        LOG.debug("Entering generatePosterReports()");

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

        tr.generateReport(reportErrors, reportSummary, runDate, title, filename, reportsDirectory);
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
    
    /**
     * @see org.kuali.module.gl.service.ReportService#generateScrubberReports(java.util.Date, java.util.List, java.lang.String)
     */
    public void generateScrubberReports(Date runDate, List reportSummary, String reportNamePrefix) {
        LOG.debug("Entering generateScrubberReports()");

        String title = "Scrubber Report ";
        TransactionReportGenerator transactionReport = new TransactionReportGenerator();
        transactionReport.generateSummaryReport(reportSummary, runDate, title, reportNamePrefix, reportsDirectory);
    }

    /**
     * @see org.kuali.module.gl.service.ReportService#generateErrorReports(java.util.Date, java.util.Map, java.lang.String)
     */
    public void generateErrorReports(Date runDate, Map<Transaction, List<Message>> reportErrors, String reportNamePrefix) {
        LOG.debug("Entering generateErrorReports()");

        String title = "Error Report ";
        TransactionReportGenerator transactionReport = new TransactionReportGenerator();
        transactionReport.generateErrorReport(reportErrors, runDate, title, reportNamePrefix, reportsDirectory);
    }

    /**
     * Generate the header for the scrubber status report. This should be moved into the report service impl
     * 
     * @param scrubberReport
     * @return list of report summaries to be printed
     */
    private List<Summary> buildScrubberReportSummary(ScrubberReportData scrubberReport) {
        List<Summary> reportSummary = new ArrayList<Summary>();

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
     * Generate the header for the demerger status report. This should be moved into the report service impl
     * 
     * @param demergerReport
     * @return list of report summaries to be printed
     */
    private List<Summary> buildDemergerReportSummary(DemergerReportData demergerReport) {
        List<Summary> reportSummary = new ArrayList<Summary>();

        reportSummary.add(new Summary(1, "SCRUBBER ERROR TRANSACTIONS READ", new Integer(demergerReport.getErrorTransactionsRead())));
        reportSummary.add(new Summary(2, "SCRUBBER VALID TRANSACTIONS READ", new Integer(demergerReport.getValidTransactionsRead())));
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
}
