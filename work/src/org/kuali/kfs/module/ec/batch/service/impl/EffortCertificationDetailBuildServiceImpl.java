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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.module.chart.bo.A21SubAccount;
import org.kuali.module.effort.EffortConstants;
import org.kuali.module.effort.EffortConstants.SystemParameters;
import org.kuali.module.effort.bo.EffortCertificationDetailBuild;
import org.kuali.module.effort.bo.EffortCertificationReportDefinition;
import org.kuali.module.effort.service.EffortCertificationDetailBuildService;
import org.kuali.module.effort.util.LedgerBalanceConsolidationHelper;
import org.kuali.module.integration.bo.LaborLedgerBalance;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class provides the facilities that can generate detail line (build) for effort certification from the given labor ledger
 * balance record
 */
@Transactional
public class EffortCertificationDetailBuildServiceImpl implements EffortCertificationDetailBuildService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EffortCertificationDetailBuildServiceImpl.class);

    /**
     * @see org.kuali.module.effort.service.EffortCertificationDetailBuildService#generateDetailBuild(java.lang.Integer,
     *      org.kuali.module.labor.bo.LedgerBalance, org.kuali.module.effort.bo.EffortCertificationReportDefinition, java.util.Map)
     */
    public EffortCertificationDetailBuild generateDetailBuild(Integer postingYear, LaborLedgerBalance ledgerBalance, EffortCertificationReportDefinition reportDefinition, Map<String, List<String>> parameters) {
        EffortCertificationDetailBuild detailLine = new EffortCertificationDetailBuild();

        detailLine.setUniversityFiscalYear(postingYear);
        detailLine.setAccountNumber(ledgerBalance.getAccountNumber());
        detailLine.setChartOfAccountsCode(ledgerBalance.getChartOfAccountsCode());

        detailLine.setPositionNumber(ledgerBalance.getPositionNumber());
        detailLine.setFinancialObjectCode(ledgerBalance.getFinancialObjectCode());

        Map<Integer, Set<String>> reportPeriods = reportDefinition.getReportPeriods();
        KualiDecimal payrollAmount = LedgerBalanceConsolidationHelper.calculateTotalAmountWithinReportPeriod(ledgerBalance, reportPeriods);

        detailLine.setEffortCertificationPayrollAmount(payrollAmount);
        detailLine.setEffortCertificationOriginalPayrollAmount(payrollAmount);

        detailLine.setEffortCertificationCalculatedOverallPercent(0);
        detailLine.setEffortCertificationUpdatedOverallPercent(0);

        populateCostShareRelatedFields(detailLine, ledgerBalance, parameters);

        return detailLine;
    }

    /**
     * populate the cost share related fields in the given detail line
     * 
     * @param detailLine the given detail line
     * @param ledgerBalance the given ledger balance
     * @param parameters the given parameters setup in the calling client
     */
    private void populateCostShareRelatedFields(EffortCertificationDetailBuild detailLine, LaborLedgerBalance ledgerBalance, Map<String, List<String>> parameters) {
        List<String> expenseSubAccountTypeCodes = parameters.get(SystemParameters.EXPENSE_SUB_ACCOUNT_TYPE_CODE);
        List<String> costShareSubAccountTypeCodes = parameters.get(SystemParameters.COST_SHARE_SUB_ACCOUNT_TYPE_CODE);

        A21SubAccount A21SubAccount = this.getA21SubAccount(ledgerBalance);
        String subAccountTypeCode = ObjectUtils.isNull(A21SubAccount) ? null : A21SubAccount.getSubAccountTypeCode();

        if (subAccountTypeCode == null || expenseSubAccountTypeCodes.contains(subAccountTypeCode)) {
            detailLine.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
            detailLine.setSourceChartOfAccountsCode(EffortConstants.DASH_CHART_OF_ACCOUNTS_CODE);
            detailLine.setSourceAccountNumber(EffortConstants.DASH_ACCOUNT_NUMBER);
            detailLine.setCostShareSourceSubAccountNumber(null);
        }
        else if (costShareSubAccountTypeCodes.contains(subAccountTypeCode)) {
            detailLine.setSubAccountNumber(ledgerBalance.getSubAccountNumber());
            detailLine.setSourceChartOfAccountsCode(A21SubAccount.getCostShareChartOfAccountCode());
            detailLine.setSourceAccountNumber(A21SubAccount.getCostShareSourceAccountNumber());
            detailLine.setCostShareSourceSubAccountNumber(A21SubAccount.getCostShareSourceSubAccountNumber());
        }
        else {
            detailLine.setSubAccountNumber(ledgerBalance.getSubAccountNumber());
            detailLine.setSourceChartOfAccountsCode(EffortConstants.DASH_CHART_OF_ACCOUNTS_CODE);
            detailLine.setSourceAccountNumber(EffortConstants.DASH_ACCOUNT_NUMBER);
            detailLine.setCostShareSourceSubAccountNumber(null);
        }
    }

    /**
     * get the A21 sub account associated with the given ledger balance
     * 
     * @param ledgerBalance the given ledger balance
     * @return the A21 sub account associated with the given ledger balance; return null if not found
     */
    private A21SubAccount getA21SubAccount(LaborLedgerBalance ledgerBalance) {
        A21SubAccount a21SubAccount = null;
        try {
            a21SubAccount = ledgerBalance.getSubAccount().getA21SubAccount();
        }
        catch (NullPointerException npe) {
            LOG.debug(npe);
        }
        return a21SubAccount;
    }
}
