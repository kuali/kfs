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
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsInvoiceReport;
import org.kuali.kfs.module.ar.report.ContractsGrantsInvoiceReportDetailDataHolder;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportDataHolder;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsReportDataBuilderService;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsReportHelperService;
import org.kuali.kfs.sys.report.ReportInfo;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Implementation of ContractsGrantsReportBuilderService to build a report data holder for ContractsGrantsInvoiceReport records
 */
public class ContractsGrantsInvoiceReportBuilderServiceImpl implements ContractsGrantsReportDataBuilderService {
    protected ReportInfo reportInfo;
    protected ContractsGrantsReportHelperService contractsGrantsReportHelperService;

    /**
     * Builds the report for ContractsGrantsInvoiceReport records
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsReportDataBuilderService#buildReportDataHolder(java.util.List, java.lang.String)
     */
    @Override
    public ContractsGrantsReportDataHolder buildReportDataHolder(List<? extends BusinessObject> displayList, String sortPropertyName) {
     // check field is valid for subtotal
        boolean isFieldSubtotalRequired = ArConstants.ReportsConstants.cgInvoiceReportSubtotalFieldsList.contains(sortPropertyName);
        Map<String, List<KualiDecimal>> subTotalMap = new HashMap<String, List<KualiDecimal>>();

        if (isFieldSubtotalRequired) {
            subTotalMap = buildSubTotalMap((List<ContractsGrantsInvoiceReport>)displayList, sortPropertyName);
        }

        // build report
        ContractsGrantsReportDataHolder cgInvoiceReportDataHolder = new ContractsGrantsReportDataHolder();
        List<ContractsGrantsInvoiceReportDetailDataHolder> details = cgInvoiceReportDataHolder.getDetails();

        for (ContractsGrantsInvoiceReport cgInvoiceReportEntry : (List<ContractsGrantsInvoiceReport>)displayList) {
            ContractsGrantsInvoiceReportDetailDataHolder reportDetail = new ContractsGrantsInvoiceReportDetailDataHolder();
            // set report data
            setReportDate(cgInvoiceReportEntry, reportDetail);

            if (isFieldSubtotalRequired) {
                // set sortedFieldValue for grouping in the report
                reportDetail.setSortedFieldValue(getContractsGrantsReportHelperService().getPropertyValue(cgInvoiceReportEntry, sortPropertyName));
                reportDetail.setDisplaySubtotal(true);
                // set subTotal from subTotalMap
                reportDetail.setInvoiceSubTotal(subTotalMap.get(getContractsGrantsReportHelperService().getPropertyValue(cgInvoiceReportEntry, sortPropertyName)).get(0).bigDecimalValue());
                reportDetail.setPaymentSubTotal(subTotalMap.get(getContractsGrantsReportHelperService().getPropertyValue(cgInvoiceReportEntry, sortPropertyName)).get(1).bigDecimalValue());
                reportDetail.setRemainingSubTotal(subTotalMap.get(getContractsGrantsReportHelperService().getPropertyValue(cgInvoiceReportEntry, sortPropertyName)).get(2).bigDecimalValue());

            }
            else {
                // set this to empty string for not displaying subtotal
                reportDetail.setDisplaySubtotal(false);
            }
            details.add(reportDetail);
        }
        cgInvoiceReportDataHolder.setDetails(details);

        return cgInvoiceReportDataHolder;
    }

    /**
     * Returns ContractsGrantsInvoiceReport.class
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsReportDataBuilderService#getDetailsClass()
     */
    @Override
    public Class<? extends BusinessObject> getDetailsClass() {
        return ContractsGrantsInvoiceReport.class;
    }

    /**
     * @param displayList
     * @param sortPropertyName
     * @return
     */
    protected Map<String, List<KualiDecimal>> buildSubTotalMap(List<ContractsGrantsInvoiceReport> displayList, String sortPropertyName) {
        Map<String, List<KualiDecimal>> returnSubTotalMap = new HashMap<String, List<KualiDecimal>>();
        // get list of sort fields
        List<String> valuesOfsortProperty = getContractsGrantsReportHelperService().getListOfValuesSortedProperties(displayList, sortPropertyName);

        // calculate sub_total and build subTotalMap
        for (String value : valuesOfsortProperty) {
            KualiDecimal invoiceSubTotal = KualiDecimal.ZERO;
            KualiDecimal paymentSubTotal = KualiDecimal.ZERO;
            KualiDecimal remainingSubTotal = KualiDecimal.ZERO;

            for (ContractsGrantsInvoiceReport cgInvoiceReportEntry : displayList) {
                // set fieldValue as "" when it is null
                if (value.equals(getContractsGrantsReportHelperService().getPropertyValue(cgInvoiceReportEntry, sortPropertyName))) {
                    invoiceSubTotal = invoiceSubTotal.add(cgInvoiceReportEntry.getInvoiceAmount());
                    paymentSubTotal = paymentSubTotal.add(cgInvoiceReportEntry.getPaymentAmount());
                    remainingSubTotal = remainingSubTotal.add(cgInvoiceReportEntry.getRemainingAmount());
                }
            }
            List<KualiDecimal> allSubTotal = new ArrayList<KualiDecimal>();
            allSubTotal.add(0, invoiceSubTotal);
            allSubTotal.add(1, paymentSubTotal);
            allSubTotal.add(2, remainingSubTotal);

            returnSubTotalMap.put(value, allSubTotal);
        }
        return returnSubTotalMap;
    }

    /**
     * @param cgInvoiceReportEntry
     * @param reportDetail
     */
    protected void setReportDate(ContractsGrantsInvoiceReport cgInvoiceReportEntry, ContractsGrantsInvoiceReportDetailDataHolder reportDetail) {
        reportDetail.setProposalNumber(cgInvoiceReportEntry.getProposalNumber());
        reportDetail.setDocumentNumber(cgInvoiceReportEntry.getDocumentNumber());
        reportDetail.setInvoiceType(cgInvoiceReportEntry.getInvoiceType());
        reportDetail.setInvoiceDate(cgInvoiceReportEntry.getInvoiceDate());
        reportDetail.setInvoiceDueDate(cgInvoiceReportEntry.getInvoiceDueDate());
        reportDetail.setOpenInvoiceIndicator(cgInvoiceReportEntry.getOpenInvoiceIndicator());
        reportDetail.setCustomerNumber(cgInvoiceReportEntry.getCustomerNumber());
        reportDetail.setCustomerName(cgInvoiceReportEntry.getCustomerName());
        BigDecimal invoiceAmount = (ObjectUtils.isNull(cgInvoiceReportEntry.getInvoiceAmount())) ? BigDecimal.ZERO : cgInvoiceReportEntry.getInvoiceAmount().bigDecimalValue();
        reportDetail.setInvoiceAmount(invoiceAmount);
        BigDecimal paymentAmount = (ObjectUtils.isNull(cgInvoiceReportEntry.getPaymentAmount())) ? BigDecimal.ZERO : cgInvoiceReportEntry.getPaymentAmount().bigDecimalValue();
        reportDetail.setPaymentAmount(paymentAmount);
        BigDecimal remainingAmount = (ObjectUtils.isNull(cgInvoiceReportEntry.getRemainingAmount())) ? BigDecimal.ZERO : cgInvoiceReportEntry.getRemainingAmount().bigDecimalValue();
        reportDetail.setRemainingAmount(remainingAmount);
        reportDetail.setAgeInDays(cgInvoiceReportEntry.getAgeInDays());
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
