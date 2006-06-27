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
package org.kuali.module.gl.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.kuali.core.bo.user.Options;
import org.kuali.module.gl.batch.poster.PostTransaction;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.service.impl.scrubber.DemergerReportData;
import org.kuali.module.gl.service.impl.scrubber.Message;
import org.kuali.module.gl.service.impl.scrubber.ScrubberReportData;
import org.kuali.module.gl.util.LedgerEntryHolder;
import org.kuali.module.gl.util.LedgerReport;
import org.kuali.module.gl.util.Summary;

/**
 * @author Laran Evans <lc278@cornell.edu>
 * @version $Id$
 */

public interface ReportService {
    /**
     * Scrubber General Ledger Transaction Summary report
     * 
     * @param - runDate Run date of the report
     * @param - groups Groups to summarize for the report
     * @param - title Title of the report
     */
    public void generateScrubberLedgerSummaryReport(Date runDate, Collection groups, String title);

    /**
     * Scrubber Statistics report
     * 
     * @param - runDate Run date of the report
     * @param - scrubberReport Summary information
     * @param - scrubberReportErrors Map of transactions with errors or warnings
     */
    public void generateScrubberStatisticsReport(Date runDate, ScrubberReportData scrubberReport, Map<Transaction,List<Message>> scrubberReportErrors);

    /** 
     * Scrubber Demerger Statistics report
     * 
     * @param - runDate Run date of the report
     * @param - demergerReport Summary information
     */
    public void generateScrubberDemergerStatisticsReports(Date runDate, DemergerReportData demergerReport);

    /**
     * Scrubber Bad Balance listing report
     * 
     * @param - runDate Run date of the report
     * @param - groups Groups to summarize for the report
     */
    public void generateScrubberBadBalanceTypeListingReport(Date runDate, Collection groups);

    /**
     * Scrubber Removed Transactions report
     * 
     * @param - runDate Run date of the report
     * @param errorGroup Group with error transactions
     */
    public void generateScrubberRemovedTransactions(Date runDate,OriginEntryGroup errorGroup);

    /**
     * GL Summary report
     * 
     * @param runDate
     * @param yearOffset
     * @param balanceTypeCodes
     */
    public void generateGlSummary(Date runDate,Options year,String reportType);

    /**
     * GL Encumbrance Summary report
     * 
     * @param runDate
     * @param yearOffset
     * @param balanceTypeCodes
     */
    public void generateGlEncumbranceSummary(Date runDate,Options year,String reportType);

    /**
     * Poster Statistics report
     * 
     * @param runDate
     * @param reportSummary
     * @param reportErrors
     * @param mode
     */
    public void generatePosterStatisticsReport(Date runDate, Map<String,Integer> reportSummary, List<PostTransaction> transactionPosters, Map<Transaction,List<Message>> reportErrors, int mode);

    /**
     * Poster ICR Statistics report
     * 
     * @param runDate
     * @param reportErrors
     * @param reportExpendTranRetrieved
     * @param reportExpendTranDeleted
     * @param reportExpendTranKept
     * @param reportOriginEntryGenerated
     */
    public void generatePosterIcrStatisticsReport(Date runDate, Map<Transaction,List<Message>> reportErrors, int reportExpendTranRetrieved,int reportExpendTranDeleted,int reportExpendTranKept,int reportOriginEntryGenerated);

    /**
     * ICR Encumbrance Statistics report
     * 
     * @param runDate
     * @param totalOfIcrEncumbrances
     * @param totalOfEntriesGenerated
     */
    public void generateIcrEncumbranceStatisticsReport(Date runDate,int totalOfIcrEncumbrances,int totalOfEntriesGenerated);

    /**
     * Main Poster Input Transaction Report
     * 
     * @param runDate
     * @param groups
     */
    public void generatePosterMainLedgerSummaryReport(Date runDate, Collection groups);

    /**
     * Icr Poster Input Transaction Report
     * 
     * @param runDate
     * @param groups
     */
    public void generatePosterIcrLedgerSummaryReport(Date runDate, Collection groups);

    /**
     * Reversal Poster Input Transaction Report
     * 
     * @param runDate
     * @param groups
     */
    public void generatePosterReversalLedgerSummaryReport(Date runDate, Collection groups);
}
