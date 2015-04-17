/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
     * @param baos the output stream for sending back contents
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
