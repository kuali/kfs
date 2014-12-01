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
package org.kuali.kfs.module.tem.batch.service;

import java.io.PrintStream;
import java.util.List;

import org.kuali.kfs.sys.report.BusinessObjectReportHelper;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.ErrorMessage;

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
