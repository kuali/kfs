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
package org.kuali.kfs.service;

import java.util.Map;

/**
 * To provide utilities that can generate reports
 */
public interface ReportGenerationService {

    /**
     * generate a report as PDF file with the given file name
     * 
     * @param reportData the data being written into the PDF report file
     * @param template the report template full file name
     * @param reportFileName the full name of the generated PDF file
     */
    public void generateReportToPdfFile(Map<String, Object> reportData, String template, String reportFileName);

    /**
     * generate a report as PDF file with the given file name
     * 
     * @param reportData the data being written into the PDF report file
     * @param dataSource the data source being used for the PDF report
     * @param template the report template full file name
     * @param reportFileName the full name of the generated PDF file
     */
    public void generateReportToPdfFile(Map<String, Object> reportData, Object dataSource, String template, String reportFileName);
}
