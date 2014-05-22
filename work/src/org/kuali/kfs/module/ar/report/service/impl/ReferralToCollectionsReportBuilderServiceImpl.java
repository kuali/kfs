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

import org.kuali.kfs.module.ar.businessobject.ReferralToCollectionsReport;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportDataHolder;
import org.kuali.kfs.module.ar.report.ReferralToCollectionsReportDetailDataHolder;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsReportDataBuilderService;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsReportHelperService;
import org.kuali.kfs.sys.report.ReportInfo;
import org.kuali.rice.krad.bo.BusinessObject;

/**
 * Implementation of ContractsGrantsReportDataBuilderService which helps the Referral to Collections report
 */
public class ReferralToCollectionsReportBuilderServiceImpl implements ContractsGrantsReportDataBuilderService {
    protected ReportInfo reportInfo;
    protected ContractsGrantsReportHelperService contractsGrantsReportHelperService;

    /**
     * Builds the report
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsReportDataBuilderService#buildReportDataHolder(java.util.List, java.lang.String)
     */
    @Override
    public ContractsGrantsReportDataHolder buildReportDataHolder(List<? extends BusinessObject> displayList, String sortPropertyName) {
     // check field is valid for subtotal
        boolean isFieldSubtotalRequired = true;
        Map<String, List<BigDecimal>> subTotalMap = new HashMap<String, List<BigDecimal>>();

        if (isFieldSubtotalRequired) {
            subTotalMap = buildSubTotalMap((List<ReferralToCollectionsReport>)displayList, sortPropertyName);
        }

        BigDecimal invoiceTotal = BigDecimal.ZERO;
        BigDecimal openTotal = BigDecimal.ZERO;

        ContractsGrantsReportDataHolder cgInvoiceReportDataHolder = new ContractsGrantsReportDataHolder();
        List<ReferralToCollectionsReportDetailDataHolder> details = cgInvoiceReportDataHolder.getDetails();

        for (ReferralToCollectionsReport refToCollections : (List<ReferralToCollectionsReport>)displayList) {
            ReferralToCollectionsReportDetailDataHolder reportDetail = new ReferralToCollectionsReportDetailDataHolder();
            // set report data
            reportDetail = new ReferralToCollectionsReportDetailDataHolder(refToCollections);

            if (isFieldSubtotalRequired) {
                // set sortedFieldValue for grouping in the report
                reportDetail.setSortedFieldValue(getContractsGrantsReportHelperService().getPropertyValue(refToCollections, sortPropertyName));
                reportDetail.setDisplaySubtotalInd(true);
                // set subTotal from subTotalMap
                reportDetail.setInvoiceSubTotal(subTotalMap.get(getContractsGrantsReportHelperService().getPropertyValue(refToCollections, sortPropertyName)).get(0));
                reportDetail.setOpenSubTotal(subTotalMap.get(getContractsGrantsReportHelperService().getPropertyValue(refToCollections, sortPropertyName)).get(1));

            }
            else {
                // set this to empty string for not displaying subtotal
                reportDetail.setDisplaySubtotalInd(false);
            }

            invoiceTotal = invoiceTotal.add(reportDetail.getInvoiceAmount());
            openTotal = openTotal.add(reportDetail.getOpenAmount());

            details.add(reportDetail);
        }

        cgInvoiceReportDataHolder.setDetails(details);
        return cgInvoiceReportDataHolder;
    }

    /**
     * Returns the Class of ReferralToCollectionsReport
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsReportDataBuilderService#getDetailsClass()
     */
    @Override
    public Class<? extends BusinessObject> getDetailsClass() {
        return ReferralToCollectionsReport.class;
    }

    /**
     * This method is used to build map for total according to sort property
     *
     * @param displayList
     * @param sortPropertyName
     * @return
     */
    protected Map<String, List<BigDecimal>> buildSubTotalMap(List<ReferralToCollectionsReport> displayList, String sortPropertyName) {
        Map<String, List<BigDecimal>> returnSubTotalMap = new HashMap<String, List<BigDecimal>>();
        // get list of sort fields
        List<String> valuesOfsortProperty = getContractsGrantsReportHelperService().getListOfValuesSortedProperties(displayList, sortPropertyName);

        // calculate sub_total and build subTotalMap
        for (String value : valuesOfsortProperty) {
            BigDecimal invoiceSubTotal = BigDecimal.ZERO;
            BigDecimal openSubTotal = BigDecimal.ZERO;

            for (ReferralToCollectionsReport refToCollReport : displayList) {
                if (value.equals(getContractsGrantsReportHelperService().getPropertyValue(refToCollReport, sortPropertyName))) {
                    BigDecimal totalAmount = refToCollReport.getInvoiceAmount();
                    BigDecimal openAmount = refToCollReport.getOpenAmount();
                    invoiceSubTotal = invoiceSubTotal.add(totalAmount);
                    openSubTotal = openSubTotal.add(openAmount);
                }
            }
            List<BigDecimal> allSubTotal = new ArrayList<BigDecimal>();
            allSubTotal.add(0, invoiceSubTotal);
            allSubTotal.add(1, openSubTotal);

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
}