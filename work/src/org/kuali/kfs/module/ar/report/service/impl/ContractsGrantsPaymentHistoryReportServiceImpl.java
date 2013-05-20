/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.report.service.impl;

import java.io.ByteArrayOutputStream;

import org.kuali.kfs.module.ar.report.ContractsGrantsReportDataHolder;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsPaymentHistoryReportService;
import org.kuali.kfs.sys.report.ReportInfo;

/**
 * This class is used to generate reports for ContractsGrantsPaymentHistoryReportServiceImpl
 */
public class ContractsGrantsPaymentHistoryReportServiceImpl extends ContractsGrantsReportServiceImplBase implements ContractsGrantsPaymentHistoryReportService {

    private ReportInfo contractsGrantsPaymentHistoryReportInfo;

    /**
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsPaymentHistoryReportService#generateReport(org.kuali.kfs.module.ar.report.ContractsGrantsReportDataHolder,
     *      java.io.ByteArrayOutputStream)
     */
    public String generateReport(ContractsGrantsReportDataHolder reportDataHolder, ByteArrayOutputStream baos) {

        return generateReport(reportDataHolder, contractsGrantsPaymentHistoryReportInfo, baos);
    }

    /**
     * @return contractsGrantsPaymentHistoryReportInfo
     */
    public ReportInfo getContractsGrantsPaymentHistoryReportInfo() {
        return contractsGrantsPaymentHistoryReportInfo;
    }

    /**
     * @param contractsGrantsPaymentHistoryReportInfo
     */
    public void setContractsGrantsPaymentHistoryReportInfo(ReportInfo contractsGrantsPaymentHistoryReportInfo) {
        this.contractsGrantsPaymentHistoryReportInfo = contractsGrantsPaymentHistoryReportInfo;
    }

}
