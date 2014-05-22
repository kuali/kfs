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
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsInvoiceReport;
import org.kuali.kfs.module.ar.report.ContractsGrantsInvoiceReportDetailDataHolder;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportDataHolder;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsReportDataBuilderService;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsReportHelperService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Implementation of ContractsGrantsReportBuilderService to build a report data holder for ContractsGrantsInvoiceReport records
 */
public class ContractsGrantsInvoiceReportBuilderServiceImpl implements ContractsGrantsReportDataBuilderService<ContractsGrantsInvoiceReport> {
    protected ContractsGrantsReportHelperService contractsGrantsReportHelperService;

    /**
     * Builds the report for ContractsGrantsInvoiceReport records
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsReportDataBuilderService#buildReportDataHolder(java.util.List, java.lang.String)
     */
    @Override
    public ContractsGrantsReportDataHolder buildReportDataHolder(List<ContractsGrantsInvoiceReport> displayList, String sortPropertyName) {
     // check field is valid for subtotal
        boolean isFieldSubtotalRequired = ArConstants.ReportsConstants.cgInvoiceReportSubtotalFieldsList.contains(sortPropertyName);
        Map<String, List<KualiDecimal>> subTotalMap = new HashMap<String, List<KualiDecimal>>();

        if (isFieldSubtotalRequired) {
            subTotalMap = buildSubTotalMap(displayList, sortPropertyName);
        }

        // build report
        ContractsGrantsReportDataHolder cgInvoiceReportDataHolder = new ContractsGrantsReportDataHolder();
        List<ContractsGrantsInvoiceReportDetailDataHolder> details = cgInvoiceReportDataHolder.getDetails();

        for (ContractsGrantsInvoiceReport cgInvoiceReportEntry : displayList) {
            ContractsGrantsInvoiceReportDetailDataHolder reportDetail = new ContractsGrantsInvoiceReportDetailDataHolder();
            // set report data
            setReportDate(cgInvoiceReportEntry, reportDetail);

            if (isFieldSubtotalRequired) {
                // set sortedFieldValue for grouping in the report
                reportDetail.setSortedFieldValue(getContractsGrantsReportHelperService().getPropertyValue(cgInvoiceReportEntry, sortPropertyName));
                reportDetail.setDisplaySubtotalInd(true);
                // set subTotal from subTotalMap
                reportDetail.setInvoiceSubTotal(subTotalMap.get(getContractsGrantsReportHelperService().getPropertyValue(cgInvoiceReportEntry, sortPropertyName)).get(0).bigDecimalValue());
                reportDetail.setPaymentSubTotal(subTotalMap.get(getContractsGrantsReportHelperService().getPropertyValue(cgInvoiceReportEntry, sortPropertyName)).get(1).bigDecimalValue());
                reportDetail.setRemainingSubTotal(subTotalMap.get(getContractsGrantsReportHelperService().getPropertyValue(cgInvoiceReportEntry, sortPropertyName)).get(2).bigDecimalValue());

            }
            else {
                // set this to empty string for not displaying subtotal
                reportDetail.setDisplaySubtotalInd(false);
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
    public Class<ContractsGrantsInvoiceReport> getDetailsClass() {
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

    public ContractsGrantsReportHelperService getContractsGrantsReportHelperService() {
        return contractsGrantsReportHelperService;
    }

    public void setContractsGrantsReportHelperService(ContractsGrantsReportHelperService contractsGrantsReportHelperService) {
        this.contractsGrantsReportHelperService = contractsGrantsReportHelperService;
    }
}