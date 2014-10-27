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
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.kfs.module.ar.businessobject.CollectionEvent;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.report.ContractsGrantsAgingReportDetailDataHolder;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportDataHolder;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsAgingReportService;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsReportDataBuilderService;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsReportHelperService;
import org.kuali.kfs.sys.report.ReportInfo;
import org.kuali.kfs.sys.util.KfsDateUtils;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Builds the DataHolder for the Contracts & Grants Invoice Aging report
 */
public class ContractsGrantsAgingReportBuilderServiceImpl implements ContractsGrantsReportDataBuilderService {
    protected ReportInfo reportInfo;
    protected ContractsGrantsReportHelperService contractsGrantsReportHelperService;
    protected ContractsGrantsAgingReportService contractsGrantsAgingReportService;

    /**
     *
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsReportDataBuilderService#buildReportDataHolder(java.util.List, java.lang.String)
     */
    @Override
    public ContractsGrantsReportDataHolder buildReportDataHolder(List<? extends BusinessObject> displayList, String sortPropertyName) {
        // check field is valid for subtotal
        boolean isFieldSubtotalRequired = true;
        Map<String, List<KualiDecimal>> subTotalMap = new HashMap<String, List<KualiDecimal>>();

        if (isFieldSubtotalRequired) {
            subTotalMap = buildSubTotalMap((List<ContractsGrantsInvoiceDocument>)displayList, sortPropertyName);
        }

        BigDecimal invoiceTotal = BigDecimal.ZERO;
        BigDecimal paymentTotal = BigDecimal.ZERO;
        BigDecimal remainingTotal = BigDecimal.ZERO;

        // build report
        ContractsGrantsReportDataHolder cgInvoiceReportDataHolder = new ContractsGrantsReportDataHolder();
        List<ContractsGrantsAgingReportDetailDataHolder> details = cgInvoiceReportDataHolder.getDetails();

        for (ContractsGrantsInvoiceDocument cgInvoiceEntry : (List<ContractsGrantsInvoiceDocument>)displayList) {
            ContractsGrantsAgingReportDetailDataHolder reportDetail = new ContractsGrantsAgingReportDetailDataHolder();
            // set report data
            setReportData(cgInvoiceEntry, reportDetail);

            if (isFieldSubtotalRequired) {
                // set sortedFieldValue for grouping in the report
                reportDetail.setSortedFieldValue(getContractsGrantsReportHelperService().getPropertyValue(cgInvoiceEntry, sortPropertyName));
                reportDetail.setDisplaySubtotal(true);
                // set subTotal from subTotalMap
                reportDetail.setInvoiceSubTotal(subTotalMap.get(getContractsGrantsReportHelperService().getPropertyValue(cgInvoiceEntry, sortPropertyName)).get(0).bigDecimalValue());
                reportDetail.setPaymentSubTotal(subTotalMap.get(getContractsGrantsReportHelperService().getPropertyValue(cgInvoiceEntry, sortPropertyName)).get(1).bigDecimalValue());
                reportDetail.setRemainingSubTotal(subTotalMap.get(getContractsGrantsReportHelperService().getPropertyValue(cgInvoiceEntry, sortPropertyName)).get(2).bigDecimalValue());


            }
            else {
                // set this to empty string for not displaying subtotal
                reportDetail.setDisplaySubtotal(false);
            }

            invoiceTotal = invoiceTotal.add(reportDetail.getInvoiceAmount());
            paymentTotal = paymentTotal.add(reportDetail.getPaymentAmount());
            remainingTotal = remainingTotal.add(reportDetail.getRemainingAmount());
            details.add(reportDetail);
        }

        // set total field
        ContractsGrantsAgingReportDetailDataHolder reportDetail = new ContractsGrantsAgingReportDetailDataHolder();
        reportDetail.setDisplayTotal(true);
        reportDetail.setInvoiceTotal(invoiceTotal);
        reportDetail.setPaymentTotal(paymentTotal);
        reportDetail.setRemainingTotal(remainingTotal);

        details.add(reportDetail);

        cgInvoiceReportDataHolder.setDetails(details);
        return cgInvoiceReportDataHolder;
    }

    /**
     * Returns the class of ContractsGrantsInvoiceDocument
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsReportDataBuilderService#getDetailsClass()
     */
    @Override
    public Class<? extends BusinessObject> getDetailsClass() {
        return ContractsGrantsInvoiceDocument.class;
    }

    /**
     * @param cgInvoiceReportEntry
     * @param reportDetail
     */
    protected void setReportData(ContractsGrantsInvoiceDocument cgInvoiceReportEntry, ContractsGrantsAgingReportDetailDataHolder reportDetail) {
        java.util.Date today = new java.util.Date();
        Date sqlToday = new java.sql.Date(today.getTime());
        reportDetail.setAgencyNumber(cgInvoiceReportEntry.getInvoiceGeneralDetail().getAward().getAgency().getAgencyNumber());
        reportDetail.setAgencyName(cgInvoiceReportEntry.getInvoiceGeneralDetail().getAward().getAgency().getReportingName());
        reportDetail.setCustomerNumber(cgInvoiceReportEntry.getCustomerNumber());
        reportDetail.setProposalNumber(cgInvoiceReportEntry.getInvoiceGeneralDetail().getProposalNumber().toString());
        reportDetail.setAwardEndDate(cgInvoiceReportEntry.getInvoiceGeneralDetail().getAward().getAwardEndingDate());
        reportDetail.setDocumentNumber(cgInvoiceReportEntry.getDocumentNumber());

        WorkflowDocument workflowDocument = cgInvoiceReportEntry.getDocumentHeader().getWorkflowDocument();
        Date docCreateDate = new Date(workflowDocument.getDateCreated().toDate().getTime());
        reportDetail.setInvoiceDate(docCreateDate);

        // last event date
        List<CollectionEvent> events = cgInvoiceReportEntry.getCollectionEvents();
        if (ObjectUtils.isNotNull(events) && CollectionUtils.isNotEmpty(events)) {
            Collections.sort(events, new Comparator<CollectionEvent>() {

                @Override
                public int compare(CollectionEvent o1, CollectionEvent o2) {
                    if (KfsDateUtils.isSameDay(o1.getActivityDate(), o2.getActivityDate())) {
                        return 0;
                    }
                    return o2.getActivityDate().compareTo(o1.getActivityDate());
                }
            });
            reportDetail.setLastEventDate(events.get(0).getActivityDate());
        }

        // calculate ageInDays : current date - created date
        final long MILLSECS_PER_DAY = 24 * 60 * 60 * 1000;
        reportDetail.setAgeInDays((sqlToday.getTime() - docCreateDate.getTime()) / MILLSECS_PER_DAY);

        BigDecimal invoiceAmount = cgInvoiceReportEntry.getTotalDollarAmount().bigDecimalValue();
        reportDetail.setInvoiceAmount(invoiceAmount);

        BigDecimal paymentAmount = cgInvoiceReportEntry.getPaymentAmount().bigDecimalValue();
        reportDetail.setPaymentAmount(paymentAmount);

        BigDecimal remainingAmount = invoiceAmount.subtract(paymentAmount);
        reportDetail.setRemainingAmount(remainingAmount);
    }

    /**
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsAgingReportService#buildSubTotalMap(java.util.List, java.lang.String)
     */
    protected Map<String, List<KualiDecimal>> buildSubTotalMap(List<ContractsGrantsInvoiceDocument> displayList, String sortPropertyName) {
        Map<String, List<KualiDecimal>> returnSubTotalMap = new HashMap<String, List<KualiDecimal>>();
        // get list of sort fields
        List<String> valuesOfsortProperty = getContractsGrantsReportHelperService().getListOfValuesSortedProperties(displayList, sortPropertyName);

        // calculate sub_total and build subTotalMap
        for (String value : valuesOfsortProperty) {
            KualiDecimal invoiceSubTotal = KualiDecimal.ZERO;
            KualiDecimal paymentSubTotal = KualiDecimal.ZERO;
            KualiDecimal remainingSubTotal = KualiDecimal.ZERO;

            for (ContractsGrantsInvoiceDocument cgInvoiceReportEntry : displayList) {
                // set fieldValue as "" when it is null
                if (value.equals(getContractsGrantsReportHelperService().getPropertyValue(cgInvoiceReportEntry, sortPropertyName))) {
                    KualiDecimal sourceTotal = cgInvoiceReportEntry.getSourceTotal();
                    KualiDecimal paymentAmount = cgInvoiceReportEntry.getPaymentAmount();
                    invoiceSubTotal = invoiceSubTotal.add(sourceTotal);
                    paymentSubTotal = paymentSubTotal.add(paymentAmount);
                    remainingSubTotal = remainingSubTotal.add(sourceTotal.subtract(paymentSubTotal));
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

    public ContractsGrantsAgingReportService getContractsGrantsAgingReportService() {
        return contractsGrantsAgingReportService;
    }

    public void setContractsGrantsAgingReportService(ContractsGrantsAgingReportService contractsGrantsAgingReportService) {
        this.contractsGrantsAgingReportService = contractsGrantsAgingReportService;
    }
}
