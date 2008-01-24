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
package org.kuali.module.labor.service;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.util.Message;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.service.impl.scrubber.DemergerReportData;
import org.kuali.module.gl.service.impl.scrubber.ScrubberReportData;
import org.kuali.module.gl.util.Summary;
import org.kuali.module.labor.document.LaborCorrectionDocument;
import org.kuali.module.labor.util.ReportRegistry;

/**
 * This defines a set of reporting generation facilities
 */
public interface LaborReportService {

    /**
     * Generate input summary report with the given information
     * 
     * @param groups the given origin entry groups
     * @param reportInfo the primary elements of a report, such as report title and report file name
     * @param reportsDirectory the directory in file system that is used to contain reports
     * @param runDate the datetime of the repor generation
     */
    public void generateInputSummaryReport(Collection<OriginEntryGroup> groups, ReportRegistry reportInfo, String reportsDirectory, Date runDate);

    /**
     * Generate input summary report with the given information
     * 
     * @param group the given origin entry group
     * @param reportInfo the primary elements of a report, such as report title and report file name
     * @param reportsDirectory the directory in file system that is used to contain reports
     * @param runDate the datetime of the repor generation
     */
    public void generateInputSummaryReport(OriginEntryGroup group, ReportRegistry reportInfo, String reportsDirectory, Date runDate);

    /**
     * Generate error transaction listing as a report
     * 
     * @param group the given origin entry group
     * @param reportInfo the primary elements of a report, such as report title and report file name
     * @param reportsDirectory the directory in file system that is used to contain reports
     * @param runDate the datetime of the repor generation
     */
    public void generateErrorTransactionListing(OriginEntryGroup group, ReportRegistry reportInfo, String reportsDirectory, Date runDate);

    /**
     * Generate statistics report with the given information
     * 
     * @param reportSummary a list of report <code>Summary<code> objects
     * @param errors the tansactions with problems and the descriptions of the problems
     * @param reportInfo the primary elements of a report, such as report title and report file name
     * @param reportsDirectory the directory in file system that is used to contain reports
     * @param runDate the datetime of the repor generation
     */
    public void generateStatisticsReport(List<Summary> reportSummary, Map<Transaction, List<Message>> errors, ReportRegistry reportInfo, String reportsDirectory, Date runDate);

    /**
     * Generate statistics report with the given information
     * 
     * @param reportSummary a list of report String objects
     * @param reportInfo the primary elements of a report, such as report title and report file name
     * @param reportsDirectory the directory in file system that is used to contain reports
     * @param runDate the datetime of the repor generation
     */
    public void generateStatisticsReport(List<String> reportSummary, ReportRegistry reportInfo, String reportsDirectory, Date runDate);

    /**
     * Generate output summary report with the given information
     * 
     * @param groups the given origin entry groups
     * @param reportInfo the primary elements of a report, such as report title and report file name
     * @param reportsDirectory the directory in file system that is used to contain reports
     * @param runDate the datetime of the repor generation
     */
    public void generateOutputSummaryReport(Collection<OriginEntryGroup> groups, ReportRegistry reportInfo, String reportsDirectory, Date runDate);

    /**
     * Generate output summary report with the given information
     * 
     * @param group the given origin entry group
     * @param reportInfo the primary elements of a report, such as report title and report file name
     * @param reportsDirectory the directory in file system that is used to contain reports
     * @param runDate the datetime of the repor generation
     */
    public void generateOutputSummaryReport(OriginEntryGroup group, ReportRegistry reportInfo, String reportsDirectory, Date runDate);

    /**
     * Generate the balance summary report with the given information in the monthly level
     * 
     * @param fiscalYear the given fiscal year
     * @param balanceTypes the given balance type codes
     * @param reportInfo the primary elements of a report, such as report title and report file name
     * @param reportsDirectory the directory in file system that is used to contain reports
     * @param runDate the datetime of the repor generation
     */
    public void generateMonthlyBalanceSummaryReport(Integer fiscalYear, List<String> balanceTypes, ReportRegistry reportInfo, String reportsDirectory, Date runDate);

    /**
     * Generate the balance summary report with the given information in a simple format
     * 
     * @param fiscalYear the given fiscal year
     * @param balanceTypes the given balance type codes
     * @param reportInfo the primary elements of a report, such as report title and report file name
     * @param reportsDirectory the directory in file system that is used to contain reports
     * @param runDate the datetime of the repor generation
     */
    public void generateBalanceSummaryReport(Integer fiscalYear, List<String> balanceTypes, ReportRegistry reportInfo, String reportsDirectory, Date runDate);

    /**
     * Generate GL summary report with the given information
     * 
     * @param group the given origin entry group into which the entries have copied to GL
     * @param reportInfo the primary elements of a report, such as report title and report file name
     * @param reportsDirectory the directory in file system that is used to contain reports
     * @param runDate the datetime of the repor generation
     */
    public void generateGLSummaryReport(OriginEntryGroup group, ReportRegistry reportInfo, String reportsDirectory, Date runDate);

    /**
     * LLCP document info report
     * 
     * @param cDocument
     * @param runDate
     */
    public void generateCorrectionOnlineReport(LaborCorrectionDocument cDocument, String reportsDirectory, Date runDate);

    /**
     * Scrubber General Ledger Transaction Summary report
     * 
     * @param runDate Run date of the report
     * @param groups Groups to summarize for the report
     */
    public void generateScrubberLedgerSummaryReportBatch(Collection groups, String reportsDirectory, Date runDate);

    /**
     * Scrubber General Ledger Transaction Summary report
     * 
     * @param runDate Run date of the report
     * @param groups Groups to summarize for the report
     */
    public void generateScrubberLedgerSummaryReportOnline(OriginEntryGroup group, String documentNumber, String reportsDirectory, Date runDate);

    /**
     * Scrubber Statistics report for batch reports
     * 
     * @param runDate Run date of the report
     * @param scrubberReport Summary information
     * @param scrubberReportErrors Map of transactions with errors or warnings
     */
    public void generateBatchScrubberStatisticsReport(ScrubberReportData scrubberReport, Map<Transaction, List<Message>> scrubberReportErrors, String reportsDirectory, Date runDate);

    /**
     * Scrubber Statistics report for online reports
     * 
     * @param runDate Run date of the report
     * @param scrubberReport Summary information
     * @param scrubberReportErrors Map of transactions with errors or warnings
     */
    public void generateOnlineScrubberStatisticsReport(Integer groupId, ScrubberReportData scrubberReport, Map<Transaction, List<Message>> scrubberReportErrors, String documentNumber, String reportsDirectory, Date runDate);

    /**
     * Scrubber Demerger Statistics report
     * 
     * @param runDate Run date of the report
     * @param demergerReport Summary information
     */
    public void generateScrubberDemergerStatisticsReports(DemergerReportData demergerReport, String reportsDirectory, Date runDate);

    /**
     * Scrubber Bad Balance listing report
     * 
     * @param runDate Run date of the report
     * @param groups Groups to summarize for the report
     */
    public void generateScrubberBadBalanceTypeListingReport(Collection groups, String reportsDirectory, Date runDate);

    /**
     * Scrubber Transaction Listing report
     * 
     * @param runDate Run date of the report
     * @param validGroup Group with transactions
     */
    public void generateScrubberTransactionsOnline(OriginEntryGroup validGroup, String documentNumber, String reportsDirectory, Date runDate);

    /**
     * Scrubber Removed Transactions report
     * 
     * @param runDate Run date of the report
     * @param errorGroup Group with error transactions
     */
    public void generateScrubberRemovedTransactions(OriginEntryGroup errorGroup, String reportsDirectory, Date runDate);
}