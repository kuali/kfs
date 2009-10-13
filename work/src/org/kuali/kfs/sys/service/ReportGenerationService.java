/*
 * Copyright 2008 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.sys.service;

import java.io.ByteArrayOutputStream;
import java.util.Date;
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
    
    /**
     * generate a report as PDF file and outputs to stream
     * 
     * @param reportData the data being written into the PDF report file
     * @param dataSource the data source being used for the PDF report
     * @param template the report template full file name
     * @param reportFileName the output stream for sending back contents
     */
    public void generateReportToOutputStream(Map<String, Object> reportData, Object dataSource, String template, ByteArrayOutputStream baos);

    /**
     * build a full file name with the given information. The format of the file name is <absolute path><filename>_<timestamp>.<extension> 
     * 
     * @param directory the directory where the file would be located
     * @param fileName the given file name without file extension
     * @param extension the given file extension
     * @param runDate the run date which is used to generate a timestamp
     * @return a full file name built from the given information.
     */
    public String buildFullFileName(Date runDate, String directory, String fileName, String extension);
}
