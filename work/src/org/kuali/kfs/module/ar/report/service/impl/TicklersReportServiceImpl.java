/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.report.service.impl;

import java.io.ByteArrayOutputStream;

import org.kuali.kfs.module.ar.report.ContractsGrantsReportDataHolder;
import org.kuali.kfs.module.ar.report.service.TicklersReportService;
import org.kuali.kfs.sys.report.ReportInfo;

/**
 * The implementation class for Ticklers Report.
 */
public class TicklersReportServiceImpl extends ContractsGrantsReportServiceImplBase implements TicklersReportService {

    private ReportInfo ticklersReportInfo;

    /**
     * Gets the ticklersReportInfo attribute.
     * 
     * @return Returns the ticklersReportInfo object.
     */
    public ReportInfo getTicklersReportInfo() {
        return ticklersReportInfo;
    }

    /**
     * Sets the ticklersReportInfo attribute.
     * 
     * @param ticklersReportInfo The ticklersReportInfo object to set.
     */
    public void setTicklersReportInfo(ReportInfo ticklersReportInfo) {
        this.ticklersReportInfo = ticklersReportInfo;
    }

    /**
     * @see org.kuali.kfs.module.ar.report.service.TicklersReportService#generateReport(org.kuali.kfs.module.ar.report.ContractsGrantsReportDataHolder,
     *      java.io.ByteArrayOutputStream)
     */
    @Override
    public String generateReport(ContractsGrantsReportDataHolder reportDataHolder, ByteArrayOutputStream baos) {
        return generateReport(reportDataHolder, ticklersReportInfo, baos);
    }

}
