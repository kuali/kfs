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
import org.kuali.kfs.module.ar.businessobject.ContractsGrantsLOCAmountsNotDrawnReport;
import org.kuali.kfs.module.ar.report.ContractsGrantsLOCAmountsNotDrawnReportDetailDataHolder;
import org.kuali.kfs.module.ar.report.ContractsGrantsReportDataHolder;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsReportDataBuilderService;
import org.kuali.kfs.module.ar.report.service.ContractsGrantsReportHelperService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Implementation of ContractsGrantsReportDataBuilderService which helps with the Contracts & Grants LOC Amounts Not Drawn Report
 */
public class ContractsGrantsLOCAmountsNotDrawnReportBuilderServiceImpl implements ContractsGrantsReportDataBuilderService<ContractsGrantsLOCAmountsNotDrawnReport> {
    protected ContractsGrantsReportHelperService contractsGrantsReportHelperService;

    /**
     * Builds the report
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsReportDataBuilderService#buildReportDataHolder(java.util.List, java.lang.String)
     */
    @Override
    public ContractsGrantsReportDataHolder buildReportDataHolder(List<ContractsGrantsLOCAmountsNotDrawnReport> displayList, String sortPropertyName) {
        // check field is valid for subtotal
        boolean isFieldSubtotalRequired = ArConstants.ReportsConstants.cgLOCAmountsNotDrawnReportSubtotalFieldsList.contains(sortPropertyName);
        Map<String, KualiDecimal> subTotalMap = new HashMap<String, KualiDecimal>();

        if (isFieldSubtotalRequired) {
            subTotalMap = buildSubTotalMap(displayList, sortPropertyName);
        }

        ContractsGrantsReportDataHolder cgLOCAmountsNotDrawnReportDataHolder = new ContractsGrantsReportDataHolder();
        List<ContractsGrantsLOCAmountsNotDrawnReportDetailDataHolder> details = cgLOCAmountsNotDrawnReportDataHolder.getDetails();

        for (ContractsGrantsLOCAmountsNotDrawnReport cgLOCAmountsNotDrawnReportEntry : displayList) {
            ContractsGrantsLOCAmountsNotDrawnReportDetailDataHolder reportDetail = new ContractsGrantsLOCAmountsNotDrawnReportDetailDataHolder();
            // set report data
            setReportDate(cgLOCAmountsNotDrawnReportEntry, reportDetail);

            if (isFieldSubtotalRequired) {
                // set sortedFieldValue for grouping in the report
                reportDetail.setSortedFieldValue(getContractsGrantsReportHelperService().getPropertyValue(cgLOCAmountsNotDrawnReportEntry, sortPropertyName));
                reportDetail.setDisplaySubtotalInd(true);
                // set subTotal from subTotalMap
                reportDetail.setSubTotal(subTotalMap.get(getContractsGrantsReportHelperService().getPropertyValue(cgLOCAmountsNotDrawnReportEntry, sortPropertyName)).bigDecimalValue());
            }
            else {
                // set this to empty string for not displaying subtotal
                reportDetail.setDisplaySubtotalInd(false);
            }
            details.add(reportDetail);
        }
        cgLOCAmountsNotDrawnReportDataHolder.setDetails(details);
        return cgLOCAmountsNotDrawnReportDataHolder;
    }

    /**
     * Returns the class of ContractsGrantsLOCAmountsNotDrawnReport
     * @see org.kuali.kfs.module.ar.report.service.ContractsGrantsReportDataBuilderService#getDetailsClass()
     */
    @Override
    public Class<ContractsGrantsLOCAmountsNotDrawnReport> getDetailsClass() {
        return ContractsGrantsLOCAmountsNotDrawnReport.class;
    }

    /**
     * @param displayList
     * @param sortPropertyName
     * @return
     */
    protected Map<String, KualiDecimal> buildSubTotalMap(List<ContractsGrantsLOCAmountsNotDrawnReport> displayList, String sortPropertyName) {
        Map<String, KualiDecimal> returnSubTotalMap = new HashMap<String, KualiDecimal>();
        // get list of sort fields
        List<String> valuesOfsortProperty = getContractsGrantsReportHelperService().getListOfValuesSortedProperties(displayList, sortPropertyName);

        // calculate sub_total and build subTotalMap
        for (String value : valuesOfsortProperty) {
            KualiDecimal subTotal = KualiDecimal.ZERO;
            for (ContractsGrantsLOCAmountsNotDrawnReport cgLOCAmountsNotDrawnReportEntry : displayList) {
                // set fieldValue as "" when it is null
                if (value.equals(getContractsGrantsReportHelperService().getPropertyValue(cgLOCAmountsNotDrawnReportEntry, sortPropertyName))) {
                    subTotal = subTotal.add(cgLOCAmountsNotDrawnReportEntry.getAmountToDraw());
                }
            }
            returnSubTotalMap.put(value, subTotal);
        }
        return returnSubTotalMap;
    }

    /**
     * @param cgLOCAmountsNotDrawnReportEntry
     * @param reportDetail
     */
    protected void setReportDate(ContractsGrantsLOCAmountsNotDrawnReport cgLOCAmountsNotDrawnReportEntry, ContractsGrantsLOCAmountsNotDrawnReportDetailDataHolder reportDetail) {
        reportDetail.setDocumentNumber(cgLOCAmountsNotDrawnReportEntry.getDocumentNumber());
        reportDetail.setLetterOfCreditFundCode(cgLOCAmountsNotDrawnReportEntry.getLetterOfCreditFundCode());
        reportDetail.setLetterOfCreditFundGroupCode(cgLOCAmountsNotDrawnReportEntry.getLetterOfCreditFundGroupCode());
        reportDetail.setLetterOfCreditReviewCreateDate(cgLOCAmountsNotDrawnReportEntry.getLetterOfCreditReviewCreateDate());
        BigDecimal amountAvailableToDraw = (ObjectUtils.isNull(cgLOCAmountsNotDrawnReportEntry.getAmountAvailableToDraw())) ? BigDecimal.ZERO : cgLOCAmountsNotDrawnReportEntry.getAmountAvailableToDraw().bigDecimalValue();
        reportDetail.setAmountAvailableToDraw(amountAvailableToDraw);
        BigDecimal claimOnCashBalance = (ObjectUtils.isNull(cgLOCAmountsNotDrawnReportEntry.getClaimOnCashBalance())) ? BigDecimal.ZERO : cgLOCAmountsNotDrawnReportEntry.getClaimOnCashBalance().bigDecimalValue();
        reportDetail.setClaimOnCashBalance(claimOnCashBalance);
        BigDecimal amountToDraw = (ObjectUtils.isNull(cgLOCAmountsNotDrawnReportEntry.getAmountToDraw())) ? BigDecimal.ZERO : cgLOCAmountsNotDrawnReportEntry.getAmountToDraw().bigDecimalValue();
        reportDetail.setAmountToDraw(amountToDraw);
        BigDecimal fundsNotDrawn = (ObjectUtils.isNull(cgLOCAmountsNotDrawnReportEntry.getFundsNotDrawn())) ? BigDecimal.ZERO : cgLOCAmountsNotDrawnReportEntry.getFundsNotDrawn().bigDecimalValue();
        reportDetail.setFundsNotDrawn(fundsNotDrawn);
    }

    public ContractsGrantsReportHelperService getContractsGrantsReportHelperService() {
        return contractsGrantsReportHelperService;
    }

    public void setContractsGrantsReportHelperService(ContractsGrantsReportHelperService contractsGrantsReportHelperService) {
        this.contractsGrantsReportHelperService = contractsGrantsReportHelperService;
    }
}
