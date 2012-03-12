/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.module.ec.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.kuali.kfs.coa.businessobject.A21SubAccount;
import org.kuali.kfs.integration.ld.LaborLedgerBalance;
import org.kuali.kfs.module.ec.EffortConstants;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationDetailBuild;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationReportDefinition;
import org.kuali.kfs.module.ec.service.EffortCertificationDetailBuildService;
import org.kuali.kfs.module.ec.util.LedgerBalanceConsolidationHelper;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

/**
 * This class provides the facilities that can generate detail line (build) for effort certification from the given labor ledger
 * balance record
 */
@Transactional
public class EffortCertificationDetailBuildServiceImpl implements EffortCertificationDetailBuildService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EffortCertificationDetailBuildServiceImpl.class);

    /**
     * @see org.kuali.kfs.module.ec.service.EffortCertificationDetailBuildService#generateDetailBuild(java.lang.Integer,
     *      org.kuali.kfs.module.ld.businessobject.LedgerBalance, org.kuali.kfs.module.ec.businessobject.EffortCertificationReportDefinition)
     */
    public EffortCertificationDetailBuild generateDetailBuild(Integer postingYear, LaborLedgerBalance ledgerBalance, EffortCertificationReportDefinition reportDefinition) {
        EffortCertificationDetailBuild detailLine = new EffortCertificationDetailBuild();

        detailLine.setUniversityFiscalYear(postingYear);
        detailLine.setAccountNumber(ledgerBalance.getAccountNumber());
        detailLine.setChartOfAccountsCode(ledgerBalance.getChartOfAccountsCode());

        detailLine.setPositionNumber(ledgerBalance.getPositionNumber());
        detailLine.setFinancialObjectCode(ledgerBalance.getFinancialObjectCode());

        Map<Integer, Set<String>> reportPeriods = reportDefinition.getReportPeriods();
        KualiDecimal payrollAmount = LedgerBalanceConsolidationHelper.calculateTotalAmountWithinReportPeriod(ledgerBalance, reportPeriods, true);

        detailLine.setEffortCertificationPayrollAmount(payrollAmount);
        detailLine.setEffortCertificationOriginalPayrollAmount(payrollAmount);

        detailLine.setEffortCertificationCalculatedOverallPercent(0);
        detailLine.setEffortCertificationUpdatedOverallPercent(0);

        populateCostShareRelatedFields(detailLine, ledgerBalance);

        return detailLine;
    }

    /**
     * populate the cost share related fields in the given detail line
     * 
     * @param detailLine the given detail line
     * @param ledgerBalance the given ledger balance
     * @param parameters the given parameters setup in the calling client
     */
    protected void populateCostShareRelatedFields(EffortCertificationDetailBuild detailLine, LaborLedgerBalance ledgerBalance) {
        List<String> expenseSubAccountTypeCodes = EffortConstants.ELIGIBLE_EXPENSE_SUB_ACCOUNT_TYPE_CODES;
        List<String> costShareSubAccountTypeCodes = EffortConstants.ELIGIBLE_COST_SHARE_SUB_ACCOUNT_TYPE_CODES;

        A21SubAccount A21SubAccount = this.getA21SubAccount(ledgerBalance);
        String subAccountTypeCode = ObjectUtils.isNull(A21SubAccount) ? null : A21SubAccount.getSubAccountTypeCode();

        if (ObjectUtils.isNull(subAccountTypeCode) || expenseSubAccountTypeCodes.contains(subAccountTypeCode)) {
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
    protected A21SubAccount getA21SubAccount(LaborLedgerBalance ledgerBalance) {
        A21SubAccount a21SubAccount = null;
        try {
            if (ObjectUtils.isNotNull(ledgerBalance.getSubAccount())) {
                a21SubAccount = ledgerBalance.getSubAccount().getA21SubAccount();
            }
        }
        catch (NullPointerException npe) {
            LOG.debug(npe);
        }
        return a21SubAccount;
    }
}
