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

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsSuspendedInvoiceDetailReport;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportDataHolder;
import org.kuali.kfs.module.ar.report.ContractsGrantsSuspendedInvoiceDetailReportDetailDataHolder;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsReportDataBuilderService;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsReportHelperService;
import org.kuali.kfs.sys.report.ReportInfo;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Implementation of ContractsGrantsReportDataBuilderService to help the Contracts & Grants Suspended Invoice Detail Report
 */
public class ContractsGrantsSuspendedInvoiceDetailReportBuilderServiceImpl implements ContractsGrantsReportDataBuilderService {
    protected ReportInfo reportInfo;
    protected ContractsGrantsReportHelperService contractsGrantsReportHelperService;

    /**
     * Builds the report
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsReportDataBuilderService#buildReportDataHolder(java.util.List, java.lang.String)
     */
    @Override
    public ContractsGrantsReportDataHolder buildReportDataHolder(List<? extends BusinessObject> displayList, String sortPropertyName) {
     // check field is valid for subtotal
        boolean isFieldSubtotalRequired = ArConstants.ReportsConstants.cgSuspendedInvoiceDetailReportSubtotalFieldsList.contains(sortPropertyName);
        Map<String, KualiDecimal> subTotalMap = new HashMap<String, KualiDecimal>();

        if (isFieldSubtotalRequired) {
            subTotalMap = buildSubTotalMap((List<ContractsGrantsSuspendedInvoiceDetailReport>)displayList, sortPropertyName);
        }

        // build report
        ContractsGrantsReportDataHolder cgPaymentHistoryReportDataHolder = new ContractsGrantsReportDataHolder();
        List<ContractsGrantsSuspendedInvoiceDetailReportDetailDataHolder> details = cgPaymentHistoryReportDataHolder.getDetails();

        for (ContractsGrantsSuspendedInvoiceDetailReport cgSuspendedInvoiceDetailReportEntry : (List<ContractsGrantsSuspendedInvoiceDetailReport>)displayList) {
            ContractsGrantsSuspendedInvoiceDetailReportDetailDataHolder reportDetail = new ContractsGrantsSuspendedInvoiceDetailReportDetailDataHolder();
            // set report data
            setReportDate(cgSuspendedInvoiceDetailReportEntry, reportDetail);

            if (isFieldSubtotalRequired) {
                // set sortedFieldValue for grouping in the report
                reportDetail.setSortedFieldValue(getContractsGrantsReportHelperService().getPropertyValue(cgSuspendedInvoiceDetailReportEntry, sortPropertyName));
                reportDetail.setDisplaySubtotal(true);
                // set subTotal from subTotalMap
                reportDetail.setSubTotal(subTotalMap.get(getContractsGrantsReportHelperService().getPropertyValue(cgSuspendedInvoiceDetailReportEntry, sortPropertyName)).bigDecimalValue());
            }
            else {
                // set this to empty string for not displaying subtotal
                reportDetail.setDisplaySubtotal(false);
            }
            details.add(reportDetail);
        }
        cgPaymentHistoryReportDataHolder.setDetails(details);
        return cgPaymentHistoryReportDataHolder;
    }

    /**
     * Returns the class of ContractsGrantsSuspendedInvoiceDetailReport
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsReportDataBuilderService#getDetailsClass()
     */
    @Override
    public Class<? extends BusinessObject> getDetailsClass() {
        return ContractsGrantsSuspendedInvoiceDetailReport.class;
    }

    /**
     * @param displayList
     * @param sortPropertyName
     * @return
     */
    protected Map<String, KualiDecimal> buildSubTotalMap(List<ContractsGrantsSuspendedInvoiceDetailReport> displayList, String sortPropertyName) {
        Map<String, KualiDecimal> returnSubTotalMap = new HashMap<String, KualiDecimal>();
        // get list of sort fields
        List<String> valuesOfsortProperty = getContractsGrantsReportHelperService().getListOfValuesSortedProperties(displayList, sortPropertyName);

        // calculate sub_total and build subTotalMap

        for (String value : valuesOfsortProperty) {
            KualiDecimal subTotal = KualiDecimal.ZERO;
            for (ContractsGrantsSuspendedInvoiceDetailReport cgSuspendedInvoiceDetailReportEntry : displayList) {
                // set fieldValue as "" when it is null
                if (value.equals(getContractsGrantsReportHelperService().getPropertyValue(cgSuspendedInvoiceDetailReportEntry, sortPropertyName))) {
                    subTotal = subTotal.add(cgSuspendedInvoiceDetailReportEntry.getAwardTotal());
                }
            }
            returnSubTotalMap.put(value, subTotal);
        }
        return returnSubTotalMap;
    }

    /**
     * @param cgSuspendedInvoiceDetailReportEntry
     * @param reportDetail
     */
    protected void setReportDate(ContractsGrantsSuspendedInvoiceDetailReport cgSuspendedInvoiceDetailReportEntry, ContractsGrantsSuspendedInvoiceDetailReportDetailDataHolder reportDetail) {
        reportDetail.setSuspenseCategory(cgSuspendedInvoiceDetailReportEntry.getSuspensionCategoryCode());
        reportDetail.setDocumentNumber(cgSuspendedInvoiceDetailReportEntry.getDocumentNumber());
        reportDetail.setLetterOfCreditFundGroupCode(cgSuspendedInvoiceDetailReportEntry.getLetterOfCreditFundGroupCode());
        reportDetail.setFundManager(cgSuspendedInvoiceDetailReportEntry.getFundManagerPrincipalName());
        reportDetail.setProjectDirector(cgSuspendedInvoiceDetailReportEntry.getProjectDirectorPrincipalName());
        BigDecimal awardTotal = (ObjectUtils.isNull(cgSuspendedInvoiceDetailReportEntry.getAwardTotal())) ? BigDecimal.ZERO : cgSuspendedInvoiceDetailReportEntry.getAwardTotal().bigDecimalValue();
        reportDetail.setAwardTotal(awardTotal);
    }

    @Override
    public ReportInfo getReportInfo() {
        return reportInfo;
    }

    public void setReportInfo(ReportInfo reportInfo) {
        this.reportInfo = reportInfo;
    }

    public ContractsGrantsReportHelperService getContractsGrantsReportHelperService() {
        return contractsGrantsReportHelperService;
    }

    public void setContractsGrantsReportHelperService(ContractsGrantsReportHelperService contractsGrantsReportHelperService) {
        this.contractsGrantsReportHelperService = contractsGrantsReportHelperService;
    }
}
