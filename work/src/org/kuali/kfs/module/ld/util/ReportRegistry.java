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
package org.kuali.module.labor.util;

import org.kuali.core.service.KualiConfigurationService;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;

/**
 * This is a registry of the reports. The registry typically holds the key elements of a report: its file name
 * and title.
 */
public enum ReportRegistry {
    LABOR_POSTER_INPUT("labor_poster_main_ledger", "Main Labor Poster Input Transactions"),
    LABOR_POSTER_STATISTICS("labor_poster_main", "Labor Poster Report"),    
    LABOR_POSTER_ERROR("labor_poster_main_error_list", "Main Labor Poster Error Transaction Listing"),
    LABOR_POSTER_OUTPUT("labor_poster_output_summary", "Labor Poster Output Summary"),
    LABOR_POSTER_OUTPUT_BY_SINGLE_GROUP("labor_poster_single_group_output_summary", "Labor Poster Output Summary"),
    LABOR_POSTER_GL_SUMMARY("labor_poster_gl_summary", "Labor Poster General Ledger Summary"),
    LABOR_POSTER_GL_SUMMARY_INPUT("labor_poster_gl_summary_ledger", "Labor Poster General Ledger Summary Input Transactions"),
    
    LABOR_ACTUAL_BALANCE_SUMMARY("labor_summary_act", "Labor Actual Balance Summary"),
    LABOR_BUDGET_BALANCE_SUMMARY("labor_summary_bud", "Labor Budget Balance Summary"),
    LABOR_ENCUMBRANCE_SUMMARY("labor_summary_enc", "Labor Encumbrance Summary"),
    
    LABOR_YEAR_END_OUTPUT("labor_year_end_output", "Labor Year-End Output Summary"),
    LABOR_YEAR_END_STATISTICS("labor_year_end_main", "Labor Ledger Report"),
    
    LABOR_PENDING_ENTRY_REPORT("labor_pending_entry_report", "Labor Pending Ledger Entry Report"),
    LABOR_PENDING_ENTRY_SUMMARY("labor_pending_entry_summary", "Labor Pending Ledger Entry Summary"),
    LABOR_FEED_ENTRY_SUMMARY("labor_feed_entry_summary", "Labor Feed GL Entry Summary"),
    
    PAYROLL_ACCRUAL_REPORT("payroll_accrual_report", "Payroll Accrual Entry Report"),
    PAYROLL_ACCRUAL_STATISTICS("payroll_accrual_summary", "Payroll Accrual Summary Report");
    
    private String reportFilename;
    private String reportTitle;
    
    /**
     * Constructs a ReportRegistry.java.
     * @param reportFilename the report file name
     * @param reportTitle the report title
     */
    private ReportRegistry(String reportFilename, String reportTitle){
        this.reportFilename = reportFilename;
        this.reportTitle = reportTitle;
    }
    
    /**
     * @return report file name
     */
    public String reportFilename(){
        return this.reportFilename;
    }
    
    /**
     * @return report title
     */
    public String reportTitle(){
        return this.reportTitle;
    }
    
    /**
     * get the directory where the reports can be stored
     */ 
    public static String getReportsDirectory() {      
        return SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSConstants.REPORTS_DIRECTORY_KEY);
    }
}