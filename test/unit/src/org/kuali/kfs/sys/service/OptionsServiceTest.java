/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.sys.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;

/**
 * This class tests the Options service.
 */
@ConfigureContext
public class OptionsServiceTest extends KualiTestBase {
    private static final Log LOG = LogFactory.getLog(OptionsServiceTest.class);

    public void testGetOptions() {
        SystemOptions options = SpringContext.getBean(OptionsService.class).getCurrentYearOptions();

        assertNotNull(options);

        LOG.debug("getUniversityFiscalYear = " + options.getUniversityFiscalYear());
        LOG.debug("getActualFinancialBalanceTypeCd = " + options.getActualFinancialBalanceTypeCd());
        LOG.debug("getBudgetCheckingBalanceTypeCd = " + options.getBudgetCheckingBalanceTypeCd());
        // LOG.debug("getBudgetCheckingOptionsCode = " + options.getBudgetCheckingOptionsCode());
        LOG.debug("getUniversityFiscalYearStartYr = " + options.getUniversityFiscalYearStartYr());
        LOG.debug("getUniversityFiscalYearStartMo = " + options.getUniversityFiscalYearStartMo());
        LOG.debug("getFinObjectTypeIncomecashCode = " + options.getFinObjectTypeIncomecashCode());
        LOG.debug("getFinObjTypeExpenditureexpCd = " + options.getFinObjTypeExpenditureexpCd());
        LOG.debug("getFinObjTypeExpendNotExpCode = " + options.getFinObjTypeExpendNotExpCode());
        LOG.debug("getFinObjTypeExpNotExpendCode = " + options.getFinObjTypeExpNotExpendCode());
        LOG.debug("getFinancialObjectTypeAssetsCd = " + options.getFinancialObjectTypeAssetsCd());
        LOG.debug("getFinObjectTypeLiabilitiesCode = " + options.getFinObjectTypeLiabilitiesCode());
        LOG.debug("getFinObjectTypeFundBalanceCd = " + options.getFinObjectTypeFundBalanceCd());
        LOG.debug("getExtrnlEncumFinBalanceTypCd = " + options.getExtrnlEncumFinBalanceTypCd());
        LOG.debug("getIntrnlEncumFinBalanceTypCd = " + options.getIntrnlEncumFinBalanceTypCd());
        LOG.debug("getPreencumbranceFinBalTypeCd = " + options.getPreencumbranceFinBalTypeCd());
        LOG.debug("getEliminationsFinBalanceTypeCd = " + options.getEliminationsFinBalanceTypeCd());
        LOG.debug("getFinObjTypeIncomeNotCashCd = " + options.getFinObjTypeIncomeNotCashCd());
        LOG.debug("getFinObjTypeCshNotIncomeCd = " + options.getFinObjTypeCshNotIncomeCd());
        LOG.debug("getUniversityFiscalYearName = " + options.getUniversityFiscalYearName());
        LOG.debug("getUniversityFinChartOfAcctCd = " + options.getUniversityFinChartOfAcctCd());
        LOG.debug("getCostShareEncumbranceBalanceTypeCd = " + options.getCostShareEncumbranceBalanceTypeCd());
        LOG.debug("getBaseBudgetFinancialBalanceTypeCd = " + options.getBaseBudgetFinancialBalanceTypeCd());
        LOG.debug("getMonthlyBudgetFinancialBalanceTypeCd = " + options.getMonthlyBudgetFinancialBalanceTypeCd());
        LOG.debug("getFinancialObjectTypeTransferIncomeCd = " + options.getFinancialObjectTypeTransferIncomeCd());
        LOG.debug("getFinancialObjectTypeTransferExpenseCd = " + options.getFinancialObjectTypeTransferExpenseCd());
        LOG.debug("getNominalFinancialBalanceTypeCd = " + options.getNominalFinancialBalanceTypeCd());
    }

    public boolean doRollback() {
        return false;
    }
}
