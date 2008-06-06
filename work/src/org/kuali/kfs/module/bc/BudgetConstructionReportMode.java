/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.budget;

import static org.kuali.module.budget.BCConstants.Report.BuildMode.PBGL;
import static org.kuali.module.budget.BCConstants.Report.BuildMode.BCAF;
import static org.kuali.module.budget.BCConstants.Report.BuildMode.MONTH;
import static org.kuali.module.budget.BCConstants.Report.ReportSelectMode.ACCOUNT;
import static org.kuali.module.budget.BCConstants.Report.ReportSelectMode.SUBFUND;
import static org.kuali.module.budget.BCConstants.Report.ReportSelectMode.OBJECT_CODE;
import static org.kuali.module.budget.BCConstants.Report.ReportSelectMode.REASON;

import java.util.EnumSet;

import org.kuali.module.budget.BCConstants.Report.BuildMode;
import org.kuali.module.budget.BCConstants.Report.ReportSelectMode;

/**
 * Contains properties related to a budget construction report.
 */
public enum BudgetConstructionReportMode {
    ACCOUNT_FUNDING_DETAIL_REPORT("AccountFundingDetailReport", BCAF, OBJECT_CODE, "BudgetOrgAccountFundingDetail", true), 
    ACCOUNT_OBJECT_DETAIL_REPORT("AccountObjectDetailReport", PBGL, SUBFUND, "BudgetOrgAccountObjectDetail", true),
    ACCOUNT_SUMMARY_REPORT("AccountSummaryReport", PBGL, SUBFUND, "BudgetOrgAccountSummary", true), 
    LEVEL_SUMMARY_REPORT("LevelSummaryReport", PBGL, SUBFUND, "BudgetOrgLevelSummary", true),
    MONTH_SUMMARY_REPORT("MonthSummaryReport", MONTH, SUBFUND, "BudgetOrgMonthSummary", true),
    OBJECT_SUMMARY_REPORT("ObjectSummaryReport", PBGL, SUBFUND, "BudgetOrgObjectSummary", true),    
    POSITION_FUNDING_DETAIL_REPORT("PositionFundingDetailReport", BCAF, OBJECT_CODE, "BudgetOrgPositionFundingDetail", true), 
    REASON_STATISTICS_REPORT("ReasonStatisticsReport", BCAF, REASON, "BudgetOrgReasonStatistics", false),
    REASON_SUMMARY_REPORT("ReasonSummaryReport", BCAF, REASON, "BudgetOrgReasonSummary", false), 
    SALARY_STATISTICS_REPORT("SalaryStatisticsReport", BCAF, OBJECT_CODE, "BudgetOrgSalaryStatistics", true),
    SALARY_SUMMARY_REPORT("SalarySummaryReport", BCAF, OBJECT_CODE, "BudgetOrgSalarySummary", false), 
    SUBFUND_SUMMARY_REPORT("SubFundSummaryReport", PBGL, SUBFUND, "BudgetOrgSubFundSummary", true),
    SYNCHRONIZATION_PROBLEMS_REPORT("SynchronizationProblemsReport", PBGL, ACCOUNT, "BudgetOrgSynchronizationProblems", true), 
    TWOPLG_LIST_REPORT("TwoPLGListReport", PBGL, ACCOUNT, "BudgetOrgTwoPLGList", true),
    ACCOUNT_EXPORT("AccountExport", PBGL, SUBFUND, true),
    MONTHLY_EXPORT("MonthlyExport", MONTH, SUBFUND, true),
    FUNDING_EXPORT("FundingExport", BCAF, SUBFUND, true);


    public final String reportModeName;
    public final BuildMode reportBuildMode;
    public final ReportSelectMode reportSelectMode;
    public final String jasperFileName;
    public final boolean lockThreshold;
    public final boolean export;

    /**
     * Constructs a BudgetConstructionReportMode.java.
     */
    private BudgetConstructionReportMode(final String reportModeName, final BuildMode reportBuildMode, final ReportSelectMode reportSelectMode, final String jasperFileName, final boolean lockThreshold) {
        this.reportModeName = reportModeName;
        this.reportBuildMode = reportBuildMode;
        this.reportSelectMode = reportSelectMode;
        this.jasperFileName = jasperFileName;
        this.lockThreshold = lockThreshold;
        this.export = false;
    }
    
    /**
     * Constructs a BudgetConstructionReportMode.java.
     */
    private BudgetConstructionReportMode(final String reportModeName, final BuildMode reportBuildMode, final ReportSelectMode reportSelectMode, final boolean export) {
        this.reportModeName = reportModeName;
        this.reportBuildMode = reportBuildMode;
        this.reportSelectMode = reportSelectMode;
        this.lockThreshold = false;
        this.export = export;
        this.jasperFileName = "";
    }

    /**
     * Returns the BudgetConstructionReportMode with name that matches given report mode name.
     * 
     * @param reportModeName - report name to find BudgetConstructionReportMode for
     * @return BudgetConstructionReportMode if found, or null
     */
    public static BudgetConstructionReportMode getBudgetConstructionReportModeByName(String reportModeName) {
        BudgetConstructionReportMode foundReportMode = null;
        
        for(BudgetConstructionReportMode reportMode : EnumSet.allOf(BudgetConstructionReportMode.class)) {
            if (reportMode.reportModeName.equals(reportModeName)) {
                foundReportMode = reportMode;
                break;
            }
        }
        
        return foundReportMode;
    }
}
