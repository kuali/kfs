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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsPaymentHistoryReport;
import org.kuali.kfs.module.ar.report.ContractsGrantsPaymentHistoryReportDetailDataHolder;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportDataHolder;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsReportDataBuilderService;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsReportHelperService;
import org.kuali.kfs.sys.report.ReportInfo;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * An implementation of ContractsGrantsReportDataBuilderService for the Contracts & Grants Payment History Report
 */
public class ContractsGrantsPaymentHistoryReportBuilderServiceImpl implements ContractsGrantsReportDataBuilderService {
    protected ReportInfo reportInfo;
    protected ContractsGrantsReportHelperService contractsGrantsReportHelperService;

    /**
     * Builds the report
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsReportDataBuilderService#buildReportDataHolder(java.util.List, java.lang.String)
     */
    @Override
    public ContractsGrantsReportDataHolder buildReportDataHolder(List<? extends BusinessObject> displayList, String sortPropertyName) {
     // check field is valid for subtotal
        boolean isFieldSubtotalRequired = ArConstants.ReportsConstants.cgPaymentHistoryReportSubtotalFieldsList.contains(sortPropertyName);
        Map<String, List<KualiDecimal>> subTotalMap = new HashMap<String, List<KualiDecimal>>();

        if (isFieldSubtotalRequired) {
            subTotalMap = buildSubTotalMap((List<ContractsGrantsPaymentHistoryReport>)displayList, sortPropertyName);
        }

        ContractsGrantsReportDataHolder cgPaymentHistoryReportDataHolder = new ContractsGrantsReportDataHolder();
        List<ContractsGrantsPaymentHistoryReportDetailDataHolder> details = cgPaymentHistoryReportDataHolder.getDetails();

        for (ContractsGrantsPaymentHistoryReport cgPaymentHistoryReportEntry : (List<ContractsGrantsPaymentHistoryReport>)displayList) {
            ContractsGrantsPaymentHistoryReportDetailDataHolder reportDetail = new ContractsGrantsPaymentHistoryReportDetailDataHolder();
            // set report data
            setReportDate(cgPaymentHistoryReportEntry, reportDetail);

            if (isFieldSubtotalRequired) {
                // set sortedFieldValue for grouping in the report
                reportDetail.setSortedFieldValue(getContractsGrantsReportHelperService().getPropertyValue(cgPaymentHistoryReportEntry, sortPropertyName));
                reportDetail.setDisplaySubtotal(true);
                // set subTotal from subTotalMap
                reportDetail.setInvoiceSubTotal(subTotalMap.get(getContractsGrantsReportHelperService().getPropertyValue(cgPaymentHistoryReportEntry, sortPropertyName)).get(0).bigDecimalValue());
                reportDetail.setPaymentSubTotal(subTotalMap.get(getContractsGrantsReportHelperService().getPropertyValue(cgPaymentHistoryReportEntry, sortPropertyName)).get(1).bigDecimalValue());
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
     * Returns the class of ContractsGrantsPaymentHistoryReport
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsReportDataBuilderService#getDetailsClass()
     */
    @Override
    public Class<? extends BusinessObject> getDetailsClass() {
        return ContractsGrantsPaymentHistoryReport.class;
    }

    /**
     * @param displayList
     * @param sortPropertyName
     * @return
     */
    protected Map<String, List<KualiDecimal>> buildSubTotalMap(List<ContractsGrantsPaymentHistoryReport> displayList, String sortPropertyName) {
        Map<String, List<KualiDecimal>> returnSubTotalMap = new HashMap<String, List<KualiDecimal>>();
        // get list of sort fields
        List<String> valuesOfsortProperty = getContractsGrantsReportHelperService().getListOfValuesSortedProperties(displayList, sortPropertyName);

        // calculate sub_total and build subTotalMap

        for (String value : valuesOfsortProperty) {
            KualiDecimal invoiceSubTotal = KualiDecimal.ZERO;
            KualiDecimal paymentSubTotal = KualiDecimal.ZERO;

            for (ContractsGrantsPaymentHistoryReport cgPaymentHistoryReportEntry : displayList) {
                // set fieldValue as "" when it is null
                if (value.equals(getContractsGrantsReportHelperService().getPropertyValue(cgPaymentHistoryReportEntry, sortPropertyName))) {
                    invoiceSubTotal = invoiceSubTotal.add(cgPaymentHistoryReportEntry.getInvoiceAmount());
                    paymentSubTotal = paymentSubTotal.add(cgPaymentHistoryReportEntry.getPaymentAmount());
                }
            }

            List<KualiDecimal> allSubTotal = new ArrayList<KualiDecimal>();
            allSubTotal.add(0, invoiceSubTotal);
            allSubTotal.add(1, paymentSubTotal);
            returnSubTotalMap.put(value, allSubTotal);
        }
        return returnSubTotalMap;
    }

    /**
     * @param cgPaymentHistoryReportEntry
     * @param reportDetail
     */
    protected void setReportDate(ContractsGrantsPaymentHistoryReport cgPaymentHistoryReportEntry, ContractsGrantsPaymentHistoryReportDetailDataHolder reportDetail) {
        reportDetail.setPaymentNumber(cgPaymentHistoryReportEntry.getPaymentNumber());
        reportDetail.setPaymentDate(cgPaymentHistoryReportEntry.getPaymentDate());
        reportDetail.setCustomerNumber(cgPaymentHistoryReportEntry.getCustomerNumber());
        reportDetail.setCustomerName(cgPaymentHistoryReportEntry.getCustomerName());

        BigDecimal paymentAmount = (ObjectUtils.isNull(cgPaymentHistoryReportEntry.getPaymentAmount())) ? BigDecimal.ZERO : cgPaymentHistoryReportEntry.getPaymentAmount().bigDecimalValue();
        reportDetail.setPaymentAmount(paymentAmount);


        reportDetail.setInvoiceNumber(cgPaymentHistoryReportEntry.getInvoiceNumber());

        BigDecimal invoiceAmount = (ObjectUtils.isNull(cgPaymentHistoryReportEntry.getInvoiceAmount())) ? BigDecimal.ZERO : cgPaymentHistoryReportEntry.getInvoiceAmount().bigDecimalValue();
        reportDetail.setInvoiceAmount(invoiceAmount);


        reportDetail.setAwardNumber(cgPaymentHistoryReportEntry.getAwardNumber().toString());
        reportDetail.setReversedIndicator(cgPaymentHistoryReportEntry.isReversedIndicator() ? "Yes" : "No");
        reportDetail.setAppliedIndicator(cgPaymentHistoryReportEntry.isAppliedIndicator() ? "Yes" : "No");
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
