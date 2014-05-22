/*
 * Copyright 2014 The Kuali Foundation.
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.ar.businessobject.ContractsGrantsSuspendedInvoiceSummaryReport;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportDataHolder;
import org.kuali.kfs.module.ar.report.ContractsGrantsSuspendedInvoiceSummaryReportDetailDataHolder;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsReportDataBuilderService;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Implementation of ContractsGrantsReportDataBuilderService for the Contracts & Grants Suspended Invoice Summary Report
 */
public class ContractsGrantsSuspendedInvoiceReportBuilderServiceImpl implements ContractsGrantsReportDataBuilderService<ContractsGrantsSuspendedInvoiceSummaryReport> {

    /**
     * Builds a report from the given list of details
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsReportDataBuilderService#buildReportDataHolder(java.util.List, java.lang.String)
     */
    @Override
    public ContractsGrantsReportDataHolder buildReportDataHolder(List<ContractsGrantsSuspendedInvoiceSummaryReport> displayList, String sortPropertyName) {
        // check field is valid for subtotal
        // this report doesn't have subtotal
        boolean isFieldSubtotalRequired = false;
        Map<String, KualiDecimal> subTotalMap = new HashMap<String, KualiDecimal>();

        // build report
        ContractsGrantsReportDataHolder cgSuspendedInvoiceSummaryReportDataHolder = new ContractsGrantsReportDataHolder();
        List<ContractsGrantsSuspendedInvoiceSummaryReportDetailDataHolder> details = cgSuspendedInvoiceSummaryReportDataHolder.getDetails();

        for (ContractsGrantsSuspendedInvoiceSummaryReport cgSuspendedInvoiceSummaryReportEntry : displayList) {
            ContractsGrantsSuspendedInvoiceSummaryReportDetailDataHolder reportDetail = new ContractsGrantsSuspendedInvoiceSummaryReportDetailDataHolder();
            // set report data
            setReportDate(cgSuspendedInvoiceSummaryReportEntry, reportDetail);

            reportDetail.setDisplaySubtotalInd(false);

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
        reportDetail.setCategoryDescription(cgSuspendedInvoiceSummaryReportEntry.getCategoryDescription());
        reportDetail.setTotalInvoicesSuspended(cgSuspendedInvoiceSummaryReportEntry.getTotalInvoicesSuspended());
    }

    /**
     * Returns ContractsGrantsSuspendedInvoiceSummaryReport.class
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsReportDataBuilderService#getDetailsClass()
     */
    @Override
    public Class<ContractsGrantsSuspendedInvoiceSummaryReport> getDetailsClass() {
        return ContractsGrantsSuspendedInvoiceSummaryReport.class;
    }

}
