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

import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.rice.KNSServiceLocator;

/**
 * This is a registry of the reports. The registry typically holds the key elements of a report: its file name
 * and title.
 */
public enum ReportRegistry {
    LABOR_POSTER_INPUT("poster_main_ledger", "Main Poster Input Transactions"),
    LABOR_POSTER_STATISTICS("poster_main", "Poster Report"),    
    LABOR_POSTER_ERROR("poster_main_error_list", "Main Poster Error Transaction Listing"),
    LABOR_POSTER_OUTPUT("poster_output", "Poster Output Summary"),
    LABOR_POSTER_OUTPUT_BY_SINGLE_GROUP("poster_output_single_group", "Poster Output Summary"),
    LABOR_POSTER_GL_SUMMARY("poster_gl_summary", "Poster Labor General Ledger Summary"),
    
    LABOR_YEAR_END_OUTPUT("year_end_output", "Year-End Output Summary"),
    LABOR_YEAR_END_STATISTICS("year_end_main", "Ledger Report"),
    
    LABOR_PENDING_ENTRY_REPORT("pending_entry_report", "Pending Ledger Entry Report"),
    LABOR_PENDING_ENTRY_SUMMARY("pending_entry_summary", "Pending Ledger Entry Summary"),
    
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
        return KNSServiceLocator.getKualiConfigurationService().getPropertyString(KFSConstants.REPORTS_DIRECTORY_KEY);
    }
}