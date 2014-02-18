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
import org.kuali.kfs.module.ar.report.service.ContractsGrantsSuspendedInvoiceDetailReportService;
import org.kuali.kfs.sys.report.ReportInfo;

/**
 * This class implements the service interface for Contracts Grants Suspended Invoice Detail Report.
 */
public class ContractsGrantsSuspendedInvoiceDetailReportServiceImpl extends ContractsGrantsReportServiceImplBase implements ContractsGrantsSuspendedInvoiceDetailReportService {

    private ReportInfo contractsGrantsSuspendedInvoiceDetailReportInfo;

    /**
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsSuspendedInvoiceDetailReportService#generateReport(org.kuali.kfs.module.ar.report.ContractsGrantsReportDataHolder, java.io.ByteArrayOutputStream)
     */
    @Override
    public String generateReport(ContractsGrantsReportDataHolder reportDataHolder, ByteArrayOutputStream baos) {

        return generateReport(reportDataHolder, contractsGrantsSuspendedInvoiceDetailReportInfo, baos);
    }

    /**
     * @return contractsGrantsSuspendedInvoiceDetailReportInfo
     */
    public ReportInfo getContractsGrantsSuspendedInvoiceDetailReportInfo() {
        return contractsGrantsSuspendedInvoiceDetailReportInfo;
    }

    /**
     * @param contractsGrantsSuspendedInvoiceDetailReportInfo
     */
    public void setContractsGrantsSuspendedInvoiceDetailReportInfo(ReportInfo contractsGrantsSuspendedInvoiceDetailReportInfo) {
        this.contractsGrantsSuspendedInvoiceDetailReportInfo = contractsGrantsSuspendedInvoiceDetailReportInfo;
    }

}
