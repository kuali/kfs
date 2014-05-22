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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsLOCDrawDetailsReport;
import org.kuali.kfs.module.ar.report.ContractsGrantsLOCDrawDetailsReportDetailDataHolder;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportDataHolder;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsReportDataBuilderService;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsReportHelperService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Implementation of ContractsGrantsReportDataBuilderService to build the Contracts & Grants LOC Draw Details Report
 */
public class ContractsGrantsLOCDrawDetailsReportBuilderServiceImpl implements ContractsGrantsReportDataBuilderService<ContractsGrantsLOCDrawDetailsReport> {
    protected ContractsGrantsReportHelperService contractsGrantsReportHelperService;

    /**
     * Builds the report
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsReportDataBuilderService#buildReportDataHolder(java.util.List, java.lang.String)
     */
    @Override
    public ContractsGrantsReportDataHolder buildReportDataHolder(List<ContractsGrantsLOCDrawDetailsReport> displayList, String sortPropertyName) {
        // check field is valid for subtotal
        boolean isFieldSubtotalRequired = ArConstants.ReportsConstants.cgLOCDrawDetailsReportSubtotalFieldsList.contains(sortPropertyName);
        Map<String, KualiDecimal> subTotalMap = new HashMap<String, KualiDecimal>();

        if (isFieldSubtotalRequired) {
            subTotalMap = buildSubTotalMap(displayList, sortPropertyName);
        }

        ContractsGrantsReportDataHolder cgLOCDrawDetailsReportDataHolder = new ContractsGrantsReportDataHolder();
        List<ContractsGrantsLOCDrawDetailsReportDetailDataHolder> details = cgLOCDrawDetailsReportDataHolder.getDetails();

        for (ContractsGrantsLOCDrawDetailsReport cgLOCDrawDetailsReportEntry : displayList) {
            ContractsGrantsLOCDrawDetailsReportDetailDataHolder reportDetail = new ContractsGrantsLOCDrawDetailsReportDetailDataHolder();
            // set report data
            setReportDate(cgLOCDrawDetailsReportEntry, reportDetail);

            if (isFieldSubtotalRequired) {
                // set sortedFieldValue for grouping in the report
                reportDetail.setSortedFieldValue(getContractsGrantsReportHelperService().getPropertyValue(cgLOCDrawDetailsReportEntry, sortPropertyName));
                reportDetail.setDisplaySubtotalInd(true);
                // set subTotal from subTotalMap
                reportDetail.setSubTotal(subTotalMap.get(getContractsGrantsReportHelperService().getPropertyValue(cgLOCDrawDetailsReportEntry, sortPropertyName)).bigDecimalValue());
            }
            else {
                // set this to empty string for not displaying subtotal
                reportDetail.setDisplaySubtotalInd(false);
            }
            details.add(reportDetail);
        }
        cgLOCDrawDetailsReportDataHolder.setDetails(details);
        return cgLOCDrawDetailsReportDataHolder;
    }

    /**
     * Returns the class of ContractsGrantsLOCDrawDetailsReport
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsReportDataBuilderService#getDetailsClass()
     */
    @Override
    public Class<ContractsGrantsLOCDrawDetailsReport> getDetailsClass() {
        return ContractsGrantsLOCDrawDetailsReport.class;
    }

    /**
     * @param displayList
     * @param sortPropertyName
     * @return
     */
    protected Map<String, KualiDecimal> buildSubTotalMap(List<ContractsGrantsLOCDrawDetailsReport> displayList, String sortPropertyName) {
        Map<String, KualiDecimal> returnSubTotalMap = new HashMap<String, KualiDecimal>();
        // get list of sort fields
        List<String> valuesOfsortProperty = getContractsGrantsReportHelperService().getListOfValuesSortedProperties(displayList, sortPropertyName);

        // calculate sub_total and build subTotalMap
        for (String value : valuesOfsortProperty) {
            KualiDecimal subTotal = KualiDecimal.ZERO;
            for (ContractsGrantsLOCDrawDetailsReport cgLOCDrawDetailsReportEntry : displayList) {
                // set fieldValue as "" when it is null
                if (value.equals(getContractsGrantsReportHelperService().getPropertyValue(cgLOCDrawDetailsReportEntry, sortPropertyName))) {
                    subTotal = subTotal.add(cgLOCDrawDetailsReportEntry.getAmountToDraw());
                }
            }
            returnSubTotalMap.put(value, subTotal);
        }
        return returnSubTotalMap;
    }

    /**
     * @param cgLOCDrawDetailsReportEntry
     * @param reportDetail
     */
    protected void setReportDate(ContractsGrantsLOCDrawDetailsReport cgLOCDrawDetailsReportEntry, ContractsGrantsLOCDrawDetailsReportDetailDataHolder reportDetail) {
        reportDetail.setDocumentNumber(cgLOCDrawDetailsReportEntry.getDocumentNumber());
        reportDetail.setLetterOfCreditFundCode(cgLOCDrawDetailsReportEntry.getLetterOfCreditFundCode());
        reportDetail.setLetterOfCreditFundGroupCode(cgLOCDrawDetailsReportEntry.getLetterOfCreditFundGroupCode());
        reportDetail.setLetterOfCreditReviewCreateDate(cgLOCDrawDetailsReportEntry.getLetterOfCreditReviewCreateDate());
        BigDecimal amountAvailableToDraw = (ObjectUtils.isNull(cgLOCDrawDetailsReportEntry.getAmountAvailableToDraw())) ? BigDecimal.ZERO : cgLOCDrawDetailsReportEntry.getAmountAvailableToDraw().bigDecimalValue();
        reportDetail.setAmountAvailableToDraw(amountAvailableToDraw);
        BigDecimal claimOnCashBalance = (ObjectUtils.isNull(cgLOCDrawDetailsReportEntry.getClaimOnCashBalance())) ? BigDecimal.ZERO : cgLOCDrawDetailsReportEntry.getClaimOnCashBalance().bigDecimalValue();
        reportDetail.setClaimOnCashBalance(claimOnCashBalance);
        BigDecimal amountToDraw = (ObjectUtils.isNull(cgLOCDrawDetailsReportEntry.getAmountToDraw())) ? BigDecimal.ZERO : cgLOCDrawDetailsReportEntry.getAmountToDraw().bigDecimalValue();
        reportDetail.setAmountToDraw(amountToDraw);
        BigDecimal fundsNotDrawn = (ObjectUtils.isNull(cgLOCDrawDetailsReportEntry.getFundsNotDrawn())) ? BigDecimal.ZERO : cgLOCDrawDetailsReportEntry.getFundsNotDrawn().bigDecimalValue();
        reportDetail.setFundsNotDrawn(fundsNotDrawn);
    }

    public ContractsGrantsReportHelperService getContractsGrantsReportHelperService() {
        return contractsGrantsReportHelperService;
    }

    public void setContractsGrantsReportHelperService(ContractsGrantsReportHelperService contractsGrantsReportHelperService) {
        this.contractsGrantsReportHelperService = contractsGrantsReportHelperService;
    }
}
