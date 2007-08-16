/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.core.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.kfs.bo.Options;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.OptionsService;
import org.kuali.test.ConfigureContext;

/**
 * This class tests the Options service.
 * 
 * 
 */
@ConfigureContext
public class OptionsServiceTest extends KualiTestBase {
    private static final Log LOG = LogFactory.getLog(OptionsServiceTest.class);

    public void testGetOptions() {
        Options options = SpringContext.getBean(OptionsService.class).getCurrentYearOptions();

        assertNotNull(options);

        LOG.debug("getUniversityFiscalYear = " + options.getUniversityFiscalYear());
        LOG.debug("getActualFinancialBalanceTypeCd = " + options.getActualFinancialBalanceTypeCd());
        LOG.debug("getBudgetCheckingBalanceTypeCd = " + options.getBudgetCheckingBalanceTypeCd());
//        LOG.debug("getBudgetCheckingOptionsCode = " + options.getBudgetCheckingOptionsCode());
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
