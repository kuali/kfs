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
import org.kuali.module.effort.bo.EffortCertificationDetailBuild;
import org.kuali.module.effort.bo.EffortCertificationDocumentBuild;
import org.kuali.module.effort.bo.EffortCertificationReportDefinition;
import org.kuali.module.effort.service.EffortCertificationDetailBuildService;
import org.kuali.module.effort.service.EffortCertificationDocumentBuildService;
import org.kuali.module.effort.util.LedgerBalanceConsolidationHelper;
import org.kuali.module.effort.util.PayrollAmountHolder;
import org.kuali.module.financial.service.UniversityDateService;
import org.kuali.module.labor.bo.LedgerBalance;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class Provide the facility used to generate documents (build) from the labor ledger balances
 */
@Transactional
public class EffortCertificationDocumentBuildServiceImpl implements EffortCertificationDocumentBuildService {
    private UniversityDateService universityDateService;
    private EffortCertificationDetailBuildService effortCertificationDetailBuildService;

    /**
     * @see org.kuali.module.effort.service.EffortCertificationDocumentBuildService#generateDocumentBuild(org.kuali.module.effort.bo.EffortCertificationReportDefinition,
     *      java.util.Collection, java.util.Map)
     */
    public List<EffortCertificationDocumentBuild> generateDocumentBuild(EffortCertificationReportDefinition reportDefinition, Collection<LedgerBalance> ledgerBalances, Map<String, List<String>> parameters) {
        List<EffortCertificationDocumentBuild> documentList = new ArrayList<EffortCertificationDocumentBuild>();

        Integer postingYear = universityDateService.getCurrentFiscalYear();
        Map<Integer, Set<String>> reportPeriods = reportDefinition.getReportPeriods();
        KualiDecimal totalAmount = LedgerBalanceConsolidationHelper.calculateTotalAmountWithinReportPeriod(ledgerBalances, reportPeriods);
        PayrollAmountHolder payrollAmountHolder = new PayrollAmountHolder(totalAmount, KualiDecimal.ZERO, 0);

        Map<String, List<LedgerBalance>> ledgerBalanceGroups = buildLedgerBalanceGroups(ledgerBalances);
        for (String key : ledgerBalanceGroups.keySet()) {
            List<LedgerBalance> balanceList = ledgerBalanceGroups.get(key);
            LedgerBalance headOfBalanceList = balanceList.get(0);

            EffortCertificationDocumentBuild document = populateDocument(reportDefinition, headOfBalanceList);
            List<EffortCertificationDetailBuild> detailLineList = document.getEffortCertificationDetailLinesBuild();

            for (LedgerBalance balance : balanceList) {
                EffortCertificationDetailBuild detailLine = effortCertificationDetailBuildService.generateDetailBuild(postingYear, balance, reportDefinition, parameters);
                detailLine.setEffortCertificationBuildNumber(document.getEffortCertificationBuildNumber());

                payrollAmountHolder.setPayrollAmount(detailLine.getEffortCertificationPayrollAmount());
                calculatePayrollPercent(payrollAmountHolder);

                detailLine.setEffortCertificationCalculatedOverallPercent(payrollAmountHolder.getPayrollPercent());
                detailLine.setEffortCertificationUpdatedOverallPercent(payrollAmountHolder.getPayrollPercent());

                detailLineList.add(detailLine);
            }
            documentList.add(document);
        }

        return documentList;
    }

    /**
     * populate a dument build object through the given information
     * 
     * @param reportDefinition the given report definition
     * @param ledgerBalance the given ledger balance
     * @return a dument build object populated with the given information
     */
    private static EffortCertificationDocumentBuild populateDocument(EffortCertificationReportDefinition reportDefinition, LedgerBalance ledgerBalance) {
        EffortCertificationDocumentBuild document = new EffortCertificationDocumentBuild();

        document.setEffortCertificationBuildNumber(null);
        document.setEffortCertificationDocumentCode(null);
        document.setEffortCertificationReportApprovedDate(null);
        document.setEffortCertificationReportPrintedDate(null);

        document.setEffortCertificationReportNumber(reportDefinition.getEffortCertificationReportNumber());
        document.setUniversityFiscalYear(reportDefinition.getUniversityFiscalYear());
        document.setEmplid(ledgerBalance.getEmplid());
        document.setChartOfAccountsCode(ledgerBalance.getChartOfAccountsCode());
        document.setOrganizationCode(ledgerBalance.getAccount().getOrganizationCode());

        return document;
    }

    /**
     * group the given ledger balances according to the combination of the values in the specified fields
     * 
     * @param ledgerBalances the given ledger balances
     * @return the map holding ledger balance groups
     */
    private Map<String, List<LedgerBalance>> buildLedgerBalanceGroups(Collection<LedgerBalance> ledgerBalances) {
        Map<String, List<LedgerBalance>> ledgerBalanceGroups = new HashMap<String, List<LedgerBalance>>();

        for (LedgerBalance balance : ledgerBalances) {
            String consolidationKeys = balance.getChartOfAccountsCode() + "," + balance.getAccount().getOrganizationCode();
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
        payrollAmountHolder.setAccumulatedAmount(accumulatedAmount.add(payrollAmount));

        int accumulatedPercent = payrollAmountHolder.getAccumulatedPercent();
        int quotientOne = Math.round(payrollAmount.multiply(PayrollAmountHolder.oneHundred).divide(totalAmount).floatValue());
        accumulatedPercent = accumulatedPercent + quotientOne;

        int quotientTwo = Math.round(accumulatedAmount.multiply(PayrollAmountHolder.oneHundred).divide(totalAmount).floatValue());
        quotientTwo = quotientTwo - accumulatedPercent;

        payrollAmountHolder.setAccumulatedPercent(accumulatedPercent + quotientTwo);
        payrollAmountHolder.setPayrollPercent(quotientOne + quotientTwo);
    }

    /**
     * Sets the universityDateService attribute value.
     * 
     * @param universityDateService The universityDateService to set.
     */
    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
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
