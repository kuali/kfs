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
import org.kuali.kfs.module.ar.report.service.ContractsGrantsInvoiceSuspenseActivityReportService;
import org.kuali.kfs.sys.report.ReportInfo;

/**
 * This class generates reports for Contracts Grants Invoice Suspense Activity.
 */
public class ContractsGrantsInvoiceSuspenseActivityReportServiceImpl extends ContractsGrantsReportServiceImplBase implements ContractsGrantsInvoiceSuspenseActivityReportService {

    private ReportInfo contractsGrantsInvoiceSuspenseActivityReportInfo;

    /**
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsInvoiceSuspenseActivityReportService#generateReport(org.kuali.kfs.module.ar.report.ContractsGrantsReportDataHolder,
     *      java.io.ByteArrayOutputStream)
     */
    public String generateReport(ContractsGrantsReportDataHolder reportDataHolder, ByteArrayOutputStream baos) {

        return generateReport(reportDataHolder, contractsGrantsInvoiceSuspenseActivityReportInfo, baos);
    }

    /**
     * @return contractsGrantsInvoiceSuspenseActivityReportInfo
     */
    public ReportInfo getContractsGrantsInvoiceSuspenseActivityReportInfo() {
        return contractsGrantsInvoiceSuspenseActivityReportInfo;
    }

    /**
     * @param contractsGrantsInvoiceSuspenseActivityReportInfo
     */
    public void setContractsGrantsInvoiceSuspenseActivityReportInfo(ReportInfo contractsGrantsInvoiceSuspenseActivityReportInfo) {
        this.contractsGrantsInvoiceSuspenseActivityReportInfo = contractsGrantsInvoiceSuspenseActivityReportInfo;
    }

}
