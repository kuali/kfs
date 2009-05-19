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
package org.kuali.kfs.gl.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.gl.businessobject.OriginEntryGroup;
import org.kuali.kfs.gl.report.YearEndTransactionReport;
import org.kuali.kfs.gl.service.ReportService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kns.service.KualiConfigurationService;

/**
 * The base implementation of ReportService
 */
public class ReportServiceImpl implements ReportService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ReportServiceImpl.class);

    private String reportsDirectory;
    private KualiConfigurationService kualiConfigurationService;

    /**
     * Constructs a ReportServiceImpl instance
     */
    public ReportServiceImpl() {
        super();
    }

    /**
     * initializes this service
     */
    public void init() {
        reportsDirectory = kualiConfigurationService.getPropertyString(KFSConstants.REPORTS_DIRECTORY_KEY);
    }

    /**
     * Generates the Balance Forward Year-End job Report
     * 
     * @param reportSummary a List of summarized statistics to report
     * @param runDate the date of the balance forward run
     * @param openAccountOriginEntryGroup the origin entry group with balance forwarding origin entries with open accounts
     * @param closedAccountOriginEntryGroup the origin entry group with balance forwarding origin entries with closed accounts
     * @see org.kuali.kfs.gl.service.ReportService#generateBalanceForwardStatisticsReport(java.util.List, java.util.Date)
     */
    public void generateBalanceForwardStatisticsReport(List reportSummary, Date runDate, OriginEntryGroup openAccountOriginEntryGroup, OriginEntryGroup closedAccountOriginEntryGroup) {
        LOG.debug("generateBalanceForwardStatisticsReport() started");

        YearEndTransactionReport transactionReport = new YearEndTransactionReport(YearEndTransactionReport.YearEndReportType.FORWARD_BALANCES_REPORT);
        String title = "Balance Forward Report ";
        transactionReport.generateReport(new HashMap(), new HashMap(), reportSummary, runDate, title, "year_end_balance_forward", reportsDirectory, new Object[] { new Object[] { openAccountOriginEntryGroup, "Open Account Balance Forwards Statistics" }, new Object[] { closedAccountOriginEntryGroup, "Closed Account Balance Fowards Statistics" } });
    }

    /**
     * Generates the encumbrance foward year end job report
     * 
     * @param jobParameters the parameters that were used by the encumbrance forward job
     * @param reportSummary a List of summarized statistics to report
     * @param runDate the date of the encumbrance forward run
     * @param originEntryGroup the origin entry group that the job placed encumbrance forwarding origin entries into
     * @see org.kuali.kfs.gl.service.ReportService#generateEncumbranceClosingStatisticsReport(java.util.List, java.util.Date)
     */
    public void generateEncumbranceClosingStatisticsReport(Map jobParameters, List reportSummary, Date runDate, OriginEntryGroup originEntryGroup) {
        LOG.debug("generateEncumbranceForwardStatisticsReport() started");

        YearEndTransactionReport transactionReport = new YearEndTransactionReport(YearEndTransactionReport.YearEndReportType.FORWARD_ENCUMBERANCES_REPORT);
        String title = "Encumbrance Closing Report ";
        transactionReport.generateReport(jobParameters, new HashMap(), reportSummary, runDate, title, "year_end_encumbrance_closing", reportsDirectory, new Object[] { new Object[] { originEntryGroup, "Encumbrance Forwards Statistics" } });
    }

    /**
     * Generates the Nominal Activity Closing Report
     * 
     * @param jobParameters the parameters that were used by the nominal activity closing job
     * @param reportSummary a List of summarized statistics to report
     * @param runDate the date of the nominal activity closing job run
     * @param originEntryGroup the origin entry group that the job placed nominal activity closing origin entries into
     * @see org.kuali.kfs.gl.service.ReportService#generateNominalActivityClosingStatisticsReport(java.util.Map, java.util.List,
     *      java.util.Date)
     */
    public void generateNominalActivityClosingStatisticsReport(Map jobParameters, List reportSummary, Date runDate, OriginEntryGroup originEntryGroup) {
        LOG.debug("generateNominalActivityClosingStatisticsReport() started");

        YearEndTransactionReport transactionReport = new YearEndTransactionReport(YearEndTransactionReport.YearEndReportType.NOMINAL_ACTIVITY_CLOSE_REPORT);
        String title = "Nominal Activity Closing Report ";
        transactionReport.generateReport(jobParameters, null, reportSummary, runDate, title, "year_end_nominal_activity_closing", reportsDirectory, new Object[] { new Object[] { originEntryGroup, "Nominal Activity Closing Statistics" } });
    }

    /**
     * This method generates the statistics report of the organization reversion process.
     * 
     * @param jobParameters the parameters the org reversion process was run with
     * @param reportSummary a list of various counts the job went through
     * @param runDate the date the report was run
     * @param orgReversionOriginEntryGroup the origin entry group that contains the reversion origin entries
     * @see org.kuali.kfs.gl.service.ReportService#generateOrgReversionStatisticsReport(java.util.Map, java.util.List,
     *      java.util.Date, org.kuali.kfs.gl.businessobject.OriginEntryGroup)
     */
    public void generateOrgReversionStatisticsReport(Map jobParameters, List reportSummary, Date runDate, OriginEntryGroup orgReversionOriginEntryGroup) {
        LOG.debug("generateOrgReversionStatisticsReport() started");

        YearEndTransactionReport transactionReport = new YearEndTransactionReport(YearEndTransactionReport.YearEndReportType.ORGANIZATION_REVERSION_PROCESS_REPORT);
        String title = "Organization Reversion Process Report ";
        transactionReport.generateReport(jobParameters, null, reportSummary, runDate, title, "year_end_org_reversion_process", reportsDirectory, new Object[] { new Object[] { orgReversionOriginEntryGroup, "Organization Reversion Statistics" } });
    }
    
    public void setKualiConfigurationService(KualiConfigurationService kcs) {
        kualiConfigurationService = kcs;
    }
}
