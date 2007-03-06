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
    LABOR_POSTER_GL_SUMMARY("poster_gl_summary", "Poster Labor General Ledger Summary");
    
    private String reportFilename;
    private String reportTitle;
    
    private ReportRegistry(String reportFilename, String reportTitle){
        this.reportFilename = reportFilename;
        this.reportTitle = reportTitle;
    }
    
    public String reportFilename(){
        return this.reportFilename;
    }
    
    public String reportTitle(){
        return this.reportTitle;
    }
}