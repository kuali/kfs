/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.gl.service;

import java.util.List;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.dataaccess.UnitTestSqlDao;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * various tests for BalanceService, especially as it supports Account business rules; using hardcoded SQL for bootstrapping
 */
@ConfigureContext
public class BalanceServiceTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BalanceServiceTest.class);
    private final static String ACCOUNT_NUMBER = "6812735";
    private final static String CHART = "UA";
    private final static String SUB_ACCT_NUMBER = "sub";
    private final static String SUB_OBJECT_CODE = "123";

    private static String DELETE_BALANCES = "delete from GL_BALANCE_T where ";
    private static String RAW_BALANCES = "select * from GL_BALANCE_T where ";
    private static String INSERT_BALANCE = "insert into GL_BALANCE_T(FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,UNIV_FISCAL_YR,FIN_SUB_OBJ_CD,FIN_OBJECT_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,FIN_BEG_BAL_LN_AMT,ACLN_ANNL_BAL_AMT) values('" + CHART + "','" + ACCOUNT_NUMBER + "','" + SUB_ACCT_NUMBER + "',";

    private static boolean runOnce = true;

    private static Account account = new Account();
    static {
        account.setAccountNumber(ACCOUNT_NUMBER);
        account.setChartOfAccountsCode(CHART);
        // jkeller: added Chart object since now used by the BalanceServiceImpl class.
        Chart chart = new Chart();
        chart.setFundBalanceObjectCode("9899");
        chart.setChartOfAccountsCode(CHART);
        account.setChartOfAccounts(chart);
    }


    private UnitTestSqlDao unitTestSqlDao;

    /**
     * This method performs all the setup steps necessary to run the tests within this test case.
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        unitTestSqlDao = SpringContext.getBean(UnitTestSqlDao.class);
        Integer fiscalYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();

        if (runOnce) {
            DELETE_BALANCES += "UNIV_FISCAL_YR=" + fiscalYear + " AND ACCOUNT_NBR='" + ACCOUNT_NUMBER + "'";
            RAW_BALANCES += "UNIV_FISCAL_YR=" + fiscalYear + " AND ACCOUNT_NBR='" + ACCOUNT_NUMBER + "'";
            INSERT_BALANCE += fiscalYear + ",";
            runOnce = false; // do not run again
        }

    }

    /**
     * 
     * This method creates and makes and SQL command call to perform and insert passing in the provided parameters.
     * @param objectTypeCode The object type code to be inserted.
     * @param balanceTypeCode The balance type code to be inserted.
     * @param objectCode The object code to be inserted.
     * @param beginningAmount The beginning amount to be inserted.
     * @param finalAmount The final amount to be inserted.
     */
    private void insertBalance(String objectTypeCode, String balanceTypeCode, String objectCode, KualiDecimal beginningAmount, KualiDecimal finalAmount) {
        unitTestSqlDao.sqlCommand(INSERT_BALANCE + "'" + SUB_OBJECT_CODE + "','" + objectCode + "','" + balanceTypeCode + "','" + objectTypeCode + "'," + beginningAmount + "," + finalAmount + ")");
    }

    /**
     * 
     * This method generates and calls and SQL command to remove all test data from the database.
     */
    public void purgeTestData() {
        unitTestSqlDao.sqlCommand(DELETE_BALANCES);

        List results = unitTestSqlDao.sqlSelect(RAW_BALANCES);
        assertNotNull("List shouldn't be null", results);
        assertEquals("Should return 0 results", 0, results.size());

    }

    /**
     * 
     * This method tests that the net result of of balance inserts is zero for appropriate balance type codes.
     */
    public void testNetToZero() {
        List results;
        purgeTestData();

        assertTrue("should net to zero when no rows exist", SpringContext.getBean(BalanceService.class).fundBalanceWillNetToZero(account));

        insertBalance("EE", "TR", "9899", new KualiDecimal(1.5), new KualiDecimal(2.5));
        results = unitTestSqlDao.sqlSelect(RAW_BALANCES);
        assertNotNull("List shouldn't be null", results);
        assertEquals("Should return 1 result", 1, results.size());

        assertTrue("should net to zero with non-AC balance Type Code", SpringContext.getBean(BalanceService.class).fundBalanceWillNetToZero(account));

        insertBalance("CH", "AC", "9899", new KualiDecimal(1.5), new KualiDecimal(2.5));
        assertFalse(SpringContext.getBean(BalanceService.class).fundBalanceWillNetToZero(account));

        // Negate the income balance with an equal expense balance
        insertBalance("EE", "AC", "9899", new KualiDecimal(2), new KualiDecimal(2));
        assertTrue("should net to zero after adding corresponding expenses", SpringContext.getBean(BalanceService.class).fundBalanceWillNetToZero(account));
        purgeTestData();
    }

    /**
     *
     * This method tests that appropriate asset object codes yield asset liability fund balances while non-asset codes do not.
     */
    public void testHasAssetLiabilityFundBalanceBalances() {
        List results;
        purgeTestData();
        assertFalse("no rows means no balances", SpringContext.getBean(BalanceService.class).hasAssetLiabilityFundBalanceBalances(account));
        String fundBalanceObjectCode = "9899"; // TODO - get this from Service? Or System Options?
        insertBalance("LI", "AC", "9899", new KualiDecimal(1.5), new KualiDecimal(2.5));
        assertFalse("should ignore 9899 balance", SpringContext.getBean(BalanceService.class).hasAssetLiabilityFundBalanceBalances(account));
        insertBalance("LI", "AC", "9900", new KualiDecimal(1.5), new KualiDecimal(2.5));
        assertTrue("expect nonzero balance for non-9899 balance", SpringContext.getBean(BalanceService.class).hasAssetLiabilityFundBalanceBalances(account));


    }

}
