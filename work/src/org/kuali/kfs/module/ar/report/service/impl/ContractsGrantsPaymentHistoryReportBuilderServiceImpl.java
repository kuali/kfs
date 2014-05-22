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
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * An implementation of ContractsGrantsReportDataBuilderService for the Contracts & Grants Payment History Report
 */
public class ContractsGrantsPaymentHistoryReportBuilderServiceImpl implements ContractsGrantsReportDataBuilderService<ContractsGrantsPaymentHistoryReport> {
    protected ContractsGrantsReportHelperService contractsGrantsReportHelperService;

    /**
     * Builds the report
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsReportDataBuilderService#buildReportDataHolder(java.util.List, java.lang.String)
     */
    @Override
    public ContractsGrantsReportDataHolder buildReportDataHolder(List<ContractsGrantsPaymentHistoryReport> displayList, String sortPropertyName) {
     // check field is valid for subtotal
        boolean isFieldSubtotalRequired = ArConstants.ReportsConstants.cgPaymentHistoryReportSubtotalFieldsList.contains(sortPropertyName);
        Map<String, List<KualiDecimal>> subTotalMap = new HashMap<String, List<KualiDecimal>>();

        if (isFieldSubtotalRequired) {
            subTotalMap = buildSubTotalMap(displayList, sortPropertyName);
        }

        ContractsGrantsReportDataHolder cgPaymentHistoryReportDataHolder = new ContractsGrantsReportDataHolder();
        List<ContractsGrantsPaymentHistoryReportDetailDataHolder> details = cgPaymentHistoryReportDataHolder.getDetails();

        for (ContractsGrantsPaymentHistoryReport cgPaymentHistoryReportEntry : displayList) {
            ContractsGrantsPaymentHistoryReportDetailDataHolder reportDetail = new ContractsGrantsPaymentHistoryReportDetailDataHolder();
            // set report data
            setReportDate(cgPaymentHistoryReportEntry, reportDetail);

            if (isFieldSubtotalRequired) {
                // set sortedFieldValue for grouping in the report
                reportDetail.setSortedFieldValue(getContractsGrantsReportHelperService().getPropertyValue(cgPaymentHistoryReportEntry, sortPropertyName));
                reportDetail.setDisplaySubtotalInd(true);
                // set subTotal from subTotalMap
                reportDetail.setInvoiceSubTotal(subTotalMap.get(getContractsGrantsReportHelperService().getPropertyValue(cgPaymentHistoryReportEntry, sortPropertyName)).get(0).bigDecimalValue());
                reportDetail.setPaymentSubTotal(subTotalMap.get(getContractsGrantsReportHelperService().getPropertyValue(cgPaymentHistoryReportEntry, sortPropertyName)).get(1).bigDecimalValue());
            }
            else {
                // set this to empty string for not displaying subtotal
                reportDetail.setDisplaySubtotalInd(false);
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
    public Class<ContractsGrantsPaymentHistoryReport> getDetailsClass() {
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

    public ContractsGrantsReportHelperService getContractsGrantsReportHelperService() {
        return contractsGrantsReportHelperService;
    }

    public void setContractsGrantsReportHelperService(ContractsGrantsReportHelperService contractsGrantsReportHelperService) {
        this.contractsGrantsReportHelperService = contractsGrantsReportHelperService;
    }
}