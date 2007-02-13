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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.gl.util.LedgerEntryHolder;
import org.kuali.module.gl.util.LedgerReport;
import org.kuali.module.gl.util.Message;
import org.kuali.module.gl.util.PosterOutputSummaryEntry;
import org.kuali.module.gl.util.PosterOutputSummaryReport;
import org.kuali.module.gl.util.Summary;
import org.kuali.module.gl.util.TransactionListingReport;
import org.kuali.module.gl.util.TransactionReport;
import org.kuali.module.labor.service.LaborOriginEntryService;
import org.kuali.module.labor.service.LaborReportService;
import org.kuali.module.labor.util.ReportRegistry;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class provides a set of facilities to generate reports.
 */
@Transactional
public class LaborReportServiceImpl implements LaborReportService {

    private LaborOriginEntryService laborOriginEntryService;
    private OriginEntryGroupService originEntryGroupService;

    /**
     * @see org.kuali.module.labor.service.LaborReportService#generate(java.util.Collection, org.kuali.module.labor.util.ReportRegistry,
     *      java.util.Date)
     */
    public void generatePosterInputSummaryReport(Collection<OriginEntryGroup> groups, ReportRegistry reportInfo, String reportsDirectory, Date runDate) {
        LedgerEntryHolder ledgerEntries;
        ledgerEntries = groups.size() > 0 ? laborOriginEntryService.getSummariedEntriesByGroups(groups) : new LedgerEntryHolder();

        LedgerReport ledgerReport = new LedgerReport();
        ledgerReport.generateReport(ledgerEntries, runDate, reportInfo.reportTitle(), reportInfo.reportFilename(), reportsDirectory);
    }

    /**
     * @see org.kuali.module.labor.service.LaborReportService#generate(org.kuali.module.gl.bo.OriginEntryGroup,
     *      org.kuali.module.labor.util.ReportRegistry, java.util.Date)
     */
    public void generatePosterErrorTransactionListing(OriginEntryGroup group, ReportRegistry reportInfo, String reportsDirectory, Date runDate) {
        Iterator entries = laborOriginEntryService.getEntriesByGroup(group);
        TransactionListingReport transactionListingReport = new TransactionListingReport();
        transactionListingReport.generateReport(entries, runDate, reportInfo.reportTitle(), reportInfo.reportFilename(), reportsDirectory);
    }

    /**
     * @see org.kuali.module.labor.service.LaborReportService#generatePosterStatisticsReport(java.util.Map, java.util.Map,
     *      org.kuali.module.labor.util.ReportRegistry, java.util.Date)
     */
    public void generatePosterStatisticsReport(List<Summary> reportSummary, Map<Transaction, List<Message>> errors, ReportRegistry reportInfo, String reportsDirectory, Date runDate) {
        TransactionReport transactionReport = new TransactionReport();
        transactionReport.generateReport(errors, reportSummary, runDate, reportInfo.reportTitle(), reportInfo.reportFilename(), reportsDirectory);
    }

    /**
     * @see org.kuali.module.labor.service.LaborReportService#generatePosterOutputSummaryReport(java.util.Collection,
     *      org.kuali.module.labor.util.ReportRegistry, java.util.Date)
     */
    public void generatePosterOutputSummaryReport(Collection<OriginEntryGroup> groups, ReportRegistry reportInfo, String reportsDirectory, Date runDate) {
        PosterOutputSummaryReport posterOutputSummaryReport = new PosterOutputSummaryReport();
        Map<String, PosterOutputSummaryEntry> posterOutputSummary = laborOriginEntryService.getPosterOutputSummaryByGroups(groups);
        posterOutputSummaryReport.generateReport(posterOutputSummary, runDate, reportInfo.reportTitle(), reportInfo.reportFilename(), reportsDirectory);
    }

    /**
     * @see org.kuali.module.labor.service.LaborReportService#generatePosterOutputSummaryReport(org.kuali.module.gl.bo.OriginEntryGroup,
     *      org.kuali.module.labor.util.ReportRegistry, java.util.Date)
     */
    public void generatePosterOutputSummaryReport(OriginEntryGroup group, ReportRegistry reportInfo, String reportsDirectory, Date runDate) {
        List<OriginEntryGroup> groups = new ArrayList<OriginEntryGroup>();
        groups.add(group);
        this.generatePosterOutputSummaryReport(groups, reportInfo, reportsDirectory, runDate);
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
}