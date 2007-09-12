/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.gl.service;

import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.bo.Options;
import org.kuali.module.gl.batch.poster.PostTransaction;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.document.CorrectionDocument;
import org.kuali.module.gl.service.impl.scrubber.DemergerReportData;
import org.kuali.module.gl.service.impl.scrubber.ScrubberReportData;
import org.kuali.module.gl.util.Message;

/**
 */

public interface ReportService {
    /**
     * Generated during the nightly out step.
     * 
     * @param runDate
     * @param groups
     */
    public void generatePendingEntryLedgerSummaryReport(Date runDate, OriginEntryGroup group);

    /**
     * Pending entry report.
     */
    public void generatePendingEntryReport(Date runDate,OriginEntryGroup group);

    /**
     * Sufficient Funds Summary Report
     * 
     * @param reportErrors
     * @param reportSummary
     * @param runDate
     * @param mode
     */
    public void generateSufficientFundsReport(Map reportErrors, List reportSummary, Date runDate, int mode);

    /**
     * Scrubber General Ledger Transaction Summary report
     * 
     * @param runDate Run date of the report
     * @param group Group to summarize for the report
     */
    public void generateScrubberLedgerSummaryReportOnline(Date runDate, OriginEntryGroup group,String documentNumber);

    /**
     * Scrubber General Ledger Transaction Summary report
     * 
     * @param runDate Run date of the report
     * @param groups Groups to summarize for the report
     */
    public void generateScrubberLedgerSummaryReportBatch(Date runDate, Collection groups);

    /**
     * Scrubber Statistics report for batch reports
     * 
     * @param runDate Run date of the report
     * @param scrubberReport Summary information
     * @param scrubberReportErrors Map of transactions with errors or warnings
     */
    public void generateBatchScrubberStatisticsReport(Date runDate, ScrubberReportData scrubberReport, Map<Transaction, List<Message>> scrubberReportErrors);

    /**
     * Scrubber Statistics report for online reports
     * 
     * @param runDate Run date of the report
     * @param scrubberReport Summary information
     * @param scrubberReportErrors Map of transactions with errors or warnings
     */
    public void generateOnlineScrubberStatisticsReport(Integer groupId, Date runDate, ScrubberReportData scrubberReport, Map<Transaction, List<Message>> scrubberReportErrors,String documentNumber);

    /**
     * Scrubber Demerger Statistics report
     * 
     * @param runDate Run date of the report
     * @param demergerReport Summary information
     */
    public void generateScrubberDemergerStatisticsReports(Date runDate, DemergerReportData demergerReport);

    /**
     * Scrubber Bad Balance listing report
     * 
     * @param runDate Run date of the report
     * @param groups Groups to summarize for the report
     */
    public void generateScrubberBadBalanceTypeListingReport(Date runDate, Collection groups);

    /**
     * Scrubber Transaction Listing report
     * 
     * @param runDate Run date of the report
     * @param validGroup Group with transactions
     */
    public void generateScrubberTransactionsOnline(Date runDate, OriginEntryGroup validGroup,String documentNumber);

    /**
     * Scrubber Removed Transactions report
     * 
     * @param runDate Run date of the report
     * @param errorGroup Group with error transactions
     */
    public void generateScrubberRemovedTransactions(Date runDate, OriginEntryGroup errorGroup);

    /**
     * GL Summary report
     * 
     * @param runDate
     * @param yearOffset
     * @param balanceTypeCodes
     */
    public void generateGlSummary(Date runDate, Options year, String reportType);

    /**
     * GL Encumbrance Summary report
     * 
     * @param runDate
     * @param yearOffset
     * @param balanceTypeCodes
     */
    public void generateGlEncumbranceSummary(Date runDate, Options year, String reportType);

    /**
     * Poster Statistics report
     * 
     * @param executionDate the actual time of poster execution
     * @param runDate the time assumed by the poster (sometimes the poster can use a transaction date back 
     * @param reportSummary
     * @param reportErrors
     * @param mode
     */
    public void generatePosterStatisticsReport(Date executionDate, Date runDate, Map<String, Integer> reportSummary, List<PostTransaction> transactionPosters, Map<Transaction, List<Message>> reportErrors, int mode);

    /**
     * Poster ICR Statistics report
     * 
     * @param executionDate the actual time of poster execution
     * @param runDate the time assumed by the poster (sometimes the poster can use a transaction date back 
     * @param reportErrors
     * @param reportExpendTranRetrieved
     * @param reportExpendTranDeleted
     * @param reportExpendTranKept
     * @param reportOriginEntryGenerated
     */
    public void generatePosterIcrStatisticsReport(Date executionDate, Date runDate, Map<Transaction, List<Message>> reportErrors, int reportExpendTranRetrieved, int reportExpendTranDeleted, int reportExpendTranKept, int reportOriginEntryGenerated);

    /**
     * ICR Encumbrance Statistics report
     * 
     * @param runDate
     * @param totalOfIcrEncumbrances
     * @param totalOfEntriesGenerated
     */
    public void generateIcrEncumbranceStatisticsReport(Date runDate, int totalOfIcrEncumbrances, int totalOfEntriesGenerated);

    /**
     * Main Poster Input Transaction Report
     * 
     * @param executionDate the actual time of poster execution
     * @param runDate the time assumed by the poster (sometimes the poster can use a transaction date back 
     * in time to redo a failed poster run)
     * @param groups
     */
    public void generatePosterMainLedgerSummaryReport(Date executionDate, Date runDate, Collection groups);

    /**
     * Icr Poster Input Transaction Report
     * 
     * @param executionDate the actual time of poster execution
     * @param runDate the time assumed by the poster (sometimes the poster can use a transaction date back 
     * in time to redo a failed poster run)
     * @param groups
     */
    public void generatePosterIcrLedgerSummaryReport(Date executionDate, Date runDate, Collection groups);

    /**
     * Reversal Poster Input Transaction Report
     * 
     * @param executionDate the actual time of poster execution
     * @param runDate the time assumed by the poster (sometimes the poster can use a transaction date back 
     * in time to redo a failed poster run)
     * @param groups
     */
    public void generatePosterReversalLedgerSummaryReport(Date executionDate, Date runDate, Iterator groups);

    /**
     * Balance Forward Year-End job Report
     * 
     * @param reportSummary
     * @param runDate
     */
    public void generateBalanceForwardStatisticsReport(List reportSummary, Date runDate, OriginEntryGroup openAccountOriginEntryGroup, OriginEntryGroup closedAccountOriginEntryGroup);

    /**
     * Encumbrance Closing Report
     * 
     * @param reportSummary
     * @param runDate
     */
    public void generateEncumbranceClosingStatisticsReport(Map jobParameters, List reportSummary, Date runDate, OriginEntryGroup originEntryGroup);

    /**
     * Nominal Activity Closing Report
     * 
     * @param jobParameters
     * @param reportSummary
     * @param runDate
     */
    public void generateNominalActivityClosingStatisticsReport(Map jobParameters, List reportSummary, Date runDate, OriginEntryGroup originEntryGroup);
    
    /**
     * 
     * This method generates the statistics report of the organization reversion process.
     * 
     * @param jobParameters the parameters the org reversion process was run with
     * @param reportSummary a list of various counts the job went through
     * @param runDate the date the report was run
     * @param orgReversionOriginEntryGroup the origin entry group that contains the reversion origin entries
     */
    public void generateOrgReversionStatisticsReport(Map jobParameters, List reportSummary, Date runDate, OriginEntryGroup orgReversionOriginEntryGroup);
    
    /**
     * Poster Reversal Transactions Listing
     * 
     * @param executionDate the actual time of poster execution
     * @param runDate the time assumed by the poster (sometimes the poster can use a transaction date back 
     * in time to redo a failed poster run)
     * @param group Group with valid transactions
     */
    public void generatePosterReversalTransactionsListing(Date executionDate, Date runDate, OriginEntryGroup group);

    /**
     * Poster Error transaction listing
     * 
     * @param executionDate the actual time of poster execution
     * @param runDate the time assumed by the poster (sometimes the poster can use a transaction date back 
     * in time to redo a failed poster run)
     * @param group Group with error transactions
     * @param posterMode Mode the poster is running
     */
    public void generatePosterErrorTransactionListing(Date executionDate, Date runDate, OriginEntryGroup group, int posterMode);
    
    /**
     * GLCP document info report
     * 
     * @param cDocument
     * @param runDate
     */
    public void correctionOnlineReport(CorrectionDocument cDocument, Date runDate);
    
    /**
     * Poster output Summary Report: a summary of the three poster runs 
     * (pulling in the  transactions from the main, reversal, and ICR posters) which we use for balancing.
     * 
     * @param runDate
     * @param groups
     */
    public void generatePosterOutputTransactionSummaryReport(Date runDate, Collection groups);
}
