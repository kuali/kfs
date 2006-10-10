/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.core.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.core.bo.user.Options;
import static org.kuali.core.util.SpringServiceLocator.*;
import org.kuali.test.KualiTestBase;
import org.kuali.test.WithTestSpringContext;

/**
 * This class tests the Options service.
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
@WithTestSpringContext
public class OptionsServiceTest extends KualiTestBase {
    private static final Log LOG = LogFactory.getLog(OptionsServiceTest.class);

    public void testGetOptions() {
        Options options = getOptionsService().getCurrentYearOptions();

        assertNotNull(options);

        LOG.debug("getUniversityFiscalYear = " + options.getUniversityFiscalYear());
        LOG.debug("getActualFinancialBalanceTypeCd = " + options.getActualFinancialBalanceTypeCd());
        LOG.debug("getBudgetCheckingBalanceTypeCd = " + options.getBudgetCheckingBalanceTypeCd());
        LOG.debug("getBudgetCheckingOptionsCode = " + options.getBudgetCheckingOptionsCode());
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
        LOG.debug("getCostShareEncumbranceBalanceTypeCode = " + options.getCostShareEncumbranceBalanceTypeCode());
        LOG.debug("getBaseBudgetFinancialBalanceTypeCode = " + options.getBaseBudgetFinancialBalanceTypeCode());
        LOG.debug("getMonthlyBudgetFinancialBalanceTypeCode = " + options.getMonthlyBudgetFinancialBalanceTypeCode());
        LOG.debug("getFinancialObjectTypeTransferIncomeCode = " + options.getFinancialObjectTypeTransferIncomeCode());
        LOG.debug("getFinancialObjectTypeTransferExpenseCode = " + options.getFinancialObjectTypeTransferExpenseCode());
        LOG.debug("getNominalFinancialBalanceTypeCode = " + options.getNominalFinancialBalanceTypeCode());
    }

    public boolean doRollback() {
        return false;
    }
}
