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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.ar.businessobject.ContractsGrantsSuspendedInvoiceSummaryReport;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportDataHolder;
import org.kuali.kfs.module.ar.report.ContractsGrantsSuspendedInvoiceSummaryReportDetailDataHolder;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsReportDataBuilderService;
import org.kuali.kfs.sys.report.ReportInfo;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.BusinessObject;

/**
 * Implementation of ContractsGrantsReportDataBuilderService for the Contracts & Grants Suspended Invoice Summary Report
 */
public class ContractsGrantsSuspendedInvoiceSummaryReportBuilderServiceImpl implements ContractsGrantsReportDataBuilderService {
    protected ReportInfo reportInfo;

    /**
     * Builds a report from the given list of details
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsReportDataBuilderService#buildReportDataHolder(java.util.List, java.lang.String)
     */
    @Override
    public ContractsGrantsReportDataHolder buildReportDataHolder(List<? extends BusinessObject> displayList, String sortPropertyName) {
        // check field is valid for subtotal
        // this report doesn't have subtotal
        boolean isFieldSubtotalRequired = false;
        Map<String, KualiDecimal> subTotalMap = new HashMap<String, KualiDecimal>();

        // build report
        ContractsGrantsReportDataHolder cgSuspendedInvoiceSummaryReportDataHolder = new ContractsGrantsReportDataHolder();
        List<ContractsGrantsSuspendedInvoiceSummaryReportDetailDataHolder> details = cgSuspendedInvoiceSummaryReportDataHolder.getDetails();

        for (ContractsGrantsSuspendedInvoiceSummaryReport cgSuspendedInvoiceSummaryReportEntry : (List<ContractsGrantsSuspendedInvoiceSummaryReport>)displayList) {
            ContractsGrantsSuspendedInvoiceSummaryReportDetailDataHolder reportDetail = new ContractsGrantsSuspendedInvoiceSummaryReportDetailDataHolder();
            // set report data
            setReportDate(cgSuspendedInvoiceSummaryReportEntry, reportDetail);

            reportDetail.setDisplaySubtotal(false);

            details.add(reportDetail);
        }
        cgSuspendedInvoiceSummaryReportDataHolder.setDetails(details);
        return cgSuspendedInvoiceSummaryReportDataHolder;
    }

    /**
     * @param cgSuspendedInvoiceSummaryReportEntry
     * @param reportDetail
     */
    protected void setReportDate(ContractsGrantsSuspendedInvoiceSummaryReport cgSuspendedInvoiceSummaryReportEntry, ContractsGrantsSuspendedInvoiceSummaryReportDetailDataHolder reportDetail) {
        reportDetail.setSuspenseCategory(cgSuspendedInvoiceSummaryReportEntry.getSuspensionCategoryCode());
        reportDetail.setCategoryDescription(cgSuspendedInvoiceSummaryReportEntry.getSuspensionCategoryDescription());
        reportDetail.setTotalInvoicesSuspended(cgSuspendedInvoiceSummaryReportEntry.getTotalInvoicesSuspended());
    }

    /**
     * Returns ContractsGrantsSuspendedInvoiceSummaryReport.class
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsReportDataBuilderService#getDetailsClass()
     */
    @Override
    public Class<? extends BusinessObject> getDetailsClass() {
        return ContractsGrantsSuspendedInvoiceSummaryReport.class;
    }

    @Override
    public ReportInfo getReportInfo() {
        return reportInfo;
    }

    public void setReportInfo(ReportInfo reportInfo) {
        this.reportInfo = reportInfo;
    }
}
