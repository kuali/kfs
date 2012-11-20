/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.batch.service;

import java.io.PrintStream;
import java.util.List;

import org.kuali.kfs.sys.report.BusinessObjectReportHelper;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.util.ErrorMessage;

public interface DataReportService {
    
    /**
     * Write report to report stream
     * 
     * @param reportDataStream
     * @param tableData
     * @param errors
     * @param reportHelper
     */
    public <T extends BusinessObject> void writeToReport(PrintStream reportDataStream,T tableData, List<ErrorMessage> errors, BusinessObjectReportHelper reportHelper);
    
    /**
     * Write the report header
     * 
     * @param reportDataStream
     * @param fileName
     * @param reportHeader
     * @param reportHelper
     */
    public void writeReportHeader(PrintStream reportDataStream, String fileName, String reportHeader, BusinessObjectReportHelper reportHelper);
    
    /**
     * 
     * @param errorMessages
     * @return
     */
    public String getMessageAsString(List<ErrorMessage> errorMessages);
    
    /**
     * get print stream for report
     */
    public PrintStream getReportPrintStream(String directory, String filePrefix);
}
