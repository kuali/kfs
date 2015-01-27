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
package org.kuali.kfs.module.ar.report.service.impl;

import java.util.List;

import org.kuali.kfs.module.ar.businessobject.ContractsGrantsInvoiceDocumentErrorLog;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportDataHolder;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsReportDataBuilderService;
import org.kuali.kfs.sys.report.ReportInfo;

/**
 * Implementation of ContractsGrantsReportDataBuilderService to help build the Contracts & Grants Invoice Document Error Log Report
 */
public class ContractsGrantsInvoiceDocumentErrorLogReportBuilderServiceImpl implements ContractsGrantsReportDataBuilderService {
    protected ReportInfo reportInfo;

    /**
     * Builds the report
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsReportDataBuilderService#buildReportDataHolder(java.util.List, java.lang.String)
     */
    @Override
    public ContractsGrantsReportDataHolder buildReportDataHolder(List<? extends BusinessObject> displayList, String sortPropertyName) {
        ContractsGrantsReportDataHolder cgInvoiceDocumentErrorLogReportDataHolder = new ContractsGrantsReportDataHolder();
        List<ContractsGrantsInvoiceDocumentErrorLog> details = cgInvoiceDocumentErrorLogReportDataHolder.getDetails();
        for (ContractsGrantsInvoiceDocumentErrorLog contractsGrantsInvoiceDocumentErrorLog : (List<ContractsGrantsInvoiceDocumentErrorLog>)displayList) {
            details.add(contractsGrantsInvoiceDocumentErrorLog);
        }
        return cgInvoiceDocumentErrorLogReportDataHolder;
    }

    /**
     * Returns the class of ContractsGrantsMilestoneReport
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsReportDataBuilderService#getDetailsClass()
     */
    @Override
    public Class<? extends BusinessObject> getDetailsClass() {
        return ContractsGrantsInvoiceDocumentErrorLog.class;
    }

    @Override
    public ReportInfo getReportInfo() {
        return reportInfo;
    }

    public void setReportInfo(ReportInfo reportInfo) {
        this.reportInfo = reportInfo;
    }
}
