/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.effort.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.bo.LaborLedgerBalance;
import org.kuali.module.effort.EffortConstants;
import org.kuali.module.effort.bo.EffortCertificationDetailBuild;
import org.kuali.module.effort.bo.EffortCertificationDocumentBuild;
import org.kuali.module.effort.bo.EffortCertificationReportDefinition;
import org.kuali.module.effort.service.EffortCertificationDetailBuildService;
import org.kuali.module.effort.service.EffortCertificationDocumentBuildService;
import org.kuali.module.effort.util.LedgerBalanceConsolidationHelper;
import org.kuali.module.effort.util.PayrollAmountHolder;
import org.kuali.module.financial.service.UniversityDateService;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class Provide the facility used to generate documents (build) from the labor ledger balances
 */
@Transactional
public class EffortCertificationDocumentBuildServiceImpl implements EffortCertificationDocumentBuildService {
    private EffortCertificationDetailBuildService effortCertificationDetailBuildService;

    /**
     * @see org.kuali.module.effort.service.EffortCertificationDocumentBuildService#generateDocumentBuild(org.kuali.module.effort.bo.EffortCertificationReportDefinition,
     *      java.util.List, java.util.Map)
     */
    public List<EffortCertificationDocumentBuild> generateDocumentBuildList(Integer postingYear, EffortCertificationReportDefinition reportDefinition, List<LaborLedgerBalance> ledgerBalances, Map<String, List<String>> parameters) {
        List<EffortCertificationDocumentBuild> documentList = new ArrayList<EffortCertificationDocumentBuild>();

        Map<String, List<LaborLedgerBalance>> ledgerBalanceGroups = buildLedgerBalanceGroups(ledgerBalances);
        for (String key : ledgerBalanceGroups.keySet()) {
            List<LaborLedgerBalance> balanceList = ledgerBalanceGroups.get(key);

            EffortCertificationDocumentBuild document = this.generateDocumentBuild(postingYear, reportDefinition, balanceList, parameters);
            documentList.add(document);
        }

        return documentList;
    }

    /**
     * @see org.kuali.module.effort.service.EffortCertificationDocumentBuildService#generateDocumentBuild(org.kuali.module.effort.bo.EffortCertificationReportDefinition,
     *      java.util.List, java.util.Map)
     */
    public EffortCertificationDocumentBuild generateDocumentBuild(Integer postingYear, EffortCertificationReportDefinition reportDefinition, List<LaborLedgerBalance> ledgerBalances, Map<String, List<String>> parameters) {
        Map<Integer, Set<String>> reportPeriods = reportDefinition.getReportPeriods();

        KualiDecimal totalAmount = LedgerBalanceConsolidationHelper.calculateTotalAmountWithinReportPeriod(ledgerBalances, reportPeriods);
        PayrollAmountHolder payrollAmountHolder = new PayrollAmountHolder(totalAmount, KualiDecimal.ZERO, 0);

        LaborLedgerBalance headOfBalanceList = ledgerBalances.get(0);
        EffortCertificationDocumentBuild document = populateDocument(reportDefinition, headOfBalanceList);
        List<EffortCertificationDetailBuild> detailLineList = document.getEffortCertificationDetailLinesBuild();

        for (LaborLedgerBalance balance : ledgerBalances) {
            EffortCertificationDetailBuild detailLine = effortCertificationDetailBuildService.generateDetailBuild(postingYear, balance, reportDefinition, parameters);
            detailLine.setEffortCertificationBuildNumber(document.getEffortCertificationBuildNumber());

            payrollAmountHolder.setPayrollAmount(detailLine.getEffortCertificationPayrollAmount());
            calculatePayrollPercent(payrollAmountHolder);

            detailLine.setEffortCertificationCalculatedOverallPercent(payrollAmountHolder.getPayrollPercent());
            detailLine.setEffortCertificationUpdatedOverallPercent(payrollAmountHolder.getPayrollPercent());
            
            this.updateDetailLineList(detailLineList, detailLine);
        }

        return document;
    }

    /**
     * populate a dument build object through the given information
     * 
     * @param reportDefinition the given report definition
     * @param ledgerBalance the given ledger balance
     * @return a dument build object populated with the given information
     */
    private static EffortCertificationDocumentBuild populateDocument(EffortCertificationReportDefinition reportDefinition, LaborLedgerBalance ledgerBalance) {
        EffortCertificationDocumentBuild document = new EffortCertificationDocumentBuild();

        document.setEffortCertificationBuildNumber(null);
        document.setEffortCertificationDocumentCode(EffortConstants.ExtractProcess.DEFAULT_DOCUMENT_CODE);

        document.setEffortCertificationReportNumber(reportDefinition.getEffortCertificationReportNumber());
        document.setUniversityFiscalYear(reportDefinition.getUniversityFiscalYear());
        document.setEmplid(ledgerBalance.getEmplid());

        return document;
    }

    /**
     * group the given ledger balances according to the combination of the values in the specified fields
     * 
     * @param ledgerBalances the given ledger balances
     * @return the map holding ledger balance groups
     */
    private Map<String, List<LaborLedgerBalance>> buildLedgerBalanceGroups(List<LaborLedgerBalance> ledgerBalances) {
        Map<String, List<LaborLedgerBalance>> ledgerBalanceGroups = new HashMap<String, List<LaborLedgerBalance>>();

        for (LaborLedgerBalance balance : ledgerBalances) {
            String consolidationKeys = balance.getEmplid();
            LedgerBalanceConsolidationHelper.groupLedgerBalancesByKeys(ledgerBalanceGroups, balance, consolidationKeys);
        }
        return ledgerBalanceGroups;
    }

    /**
     * calculate the payroll percentage based on the given information in payroll amount holder
     * 
     * @param payrollAmountHolder the given payroll amount holder containing relating information
     */
    private void calculatePayrollPercent(PayrollAmountHolder payrollAmountHolder) {
        KualiDecimal totalAmount = payrollAmountHolder.getTotalAmount();
        if (totalAmount.isZero()) {
            return;
        }

        KualiDecimal payrollAmount = payrollAmountHolder.getPayrollAmount();
        KualiDecimal accumulatedAmount = payrollAmountHolder.getAccumulatedAmount();
        accumulatedAmount = accumulatedAmount.add(payrollAmount);

        int accumulatedPercent = payrollAmountHolder.getAccumulatedPercent();
        int quotientOne = Math.round(payrollAmount.multiply(PayrollAmountHolder.oneHundred).divide(totalAmount).floatValue());
        accumulatedPercent = accumulatedPercent + quotientOne;

        int quotientTwo = Math.round(accumulatedAmount.multiply(PayrollAmountHolder.oneHundred).divide(totalAmount).floatValue());
        quotientTwo = quotientTwo - accumulatedPercent;

        payrollAmountHolder.setAccumulatedAmount(accumulatedAmount);
        payrollAmountHolder.setAccumulatedPercent(accumulatedPercent + quotientTwo);
        payrollAmountHolder.setPayrollPercent(quotientOne + quotientTwo);
    }
    
    private void updateDetailLineList(List<EffortCertificationDetailBuild> detailLineList, EffortCertificationDetailBuild detailLine) {
        int index = detailLineList.indexOf(detailLine);
        if(index >= 0) {
            EffortCertificationDetailBuild existingDetailLine = detailLineList.get(index);
            
            int calculatedOverallPercent = existingDetailLine.getEffortCertificationCalculatedOverallPercent() + detailLine.getEffortCertificationCalculatedOverallPercent();
            existingDetailLine.setEffortCertificationCalculatedOverallPercent(calculatedOverallPercent);
            
            int updatedOverallPercent = existingDetailLine.getEffortCertificationUpdatedOverallPercent() + detailLine.getEffortCertificationUpdatedOverallPercent();
            existingDetailLine.setEffortCertificationUpdatedOverallPercent(updatedOverallPercent);
            
            KualiDecimal originalPayrollAmount = existingDetailLine.getEffortCertificationOriginalPayrollAmount().add(detailLine.getEffortCertificationOriginalPayrollAmount());
            existingDetailLine.setEffortCertificationOriginalPayrollAmount(originalPayrollAmount);
            
            KualiDecimal payrollAmount = existingDetailLine.getEffortCertificationPayrollAmount().add(detailLine.getEffortCertificationPayrollAmount());
            existingDetailLine.setEffortCertificationPayrollAmount(payrollAmount);
            
        }
        else {
            detailLineList.add(detailLine);
        }
    }

    /**
     * Sets the effortCertificationDetailBuildService attribute value.
     * 
     * @param effortCertificationDetailBuildService The effortCertificationDetailBuildService to set.
     */
    public void setEffortCertificationDetailBuildService(EffortCertificationDetailBuildService effortCertificationDetailBuildService) {
        this.effortCertificationDetailBuildService = effortCertificationDetailBuildService;
    }
}
