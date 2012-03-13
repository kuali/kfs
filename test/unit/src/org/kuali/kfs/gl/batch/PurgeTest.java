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
package org.kuali.kfs.gl.batch;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.batch.BatchSpringContext;
import org.kuali.kfs.sys.batch.Step;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.ProxyUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.kfs.sys.dataaccess.UnitTestSqlDao;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.springframework.aop.support.AopUtils;

/**
 * Tests the PurgeStep
 */
@ConfigureContext
public class PurgeTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurgeTest.class);

    private UnitTestSqlDao unitTestSqlDao;
    private DateTimeService dateTimeService;

    /**
     * Sets up this method by getting the needed services from the SpringContext
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        unitTestSqlDao = SpringContext.getBean(UnitTestSqlDao.class);
        dateTimeService = SpringContext.getBean(DateTimeService.class);
    }

    /**
     * Tests that entries created before 2002 are purged
     *
     * @throws Exception thrown if something (likely a SQL issue) goes wrong
     */
    public void testPurgeEntry() throws Exception {
        LOG.debug("testPurgeEntry() started");
        final int currentFiscalYear = TestUtils.getFiscalYearForTesting().intValue();
        final int pastFiscalYear = currentFiscalYear - 1;

        // The data keeps changing, so this will make sure the test always succeeds
        unitTestSqlDao.sqlCommand("delete from GL_ENTRY_T");

        // Shouldn't be deleted
        unitTestSqlDao.sqlCommand("insert into GL_ENTRY_T (UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FIN_BALANCE_TYP_CD, FIN_OBJ_TYP_CD, UNIV_FISCAL_PRD_CD, FDOC_TYP_CD," + "FS_ORIGIN_CD, FDOC_NBR, TRN_ENTR_SEQ_NBR, TRN_LDGR_ENTR_DESC, TRN_LDGR_ENTR_AMT, TRN_DEBIT_CRDT_CD, TRANSACTION_DT," + "ORG_DOC_NBR, PROJECT_CD, ORG_REFERENCE_ID, FDOC_REF_TYP_CD, FS_REF_ORIGIN_CD, FDOC_REF_NBR, FDOC_REVERSAL_DT, TRN_ENCUM_UPDT_CD, TRN_POST_DT, TIMESTAMP) " + "values ("+currentFiscalYear+", 'BL', '1031400', '-----', '5000', '---', 'AC', 'EX', '01', 'JV', '01', 'XXX', 1,'YYY', 0, 'D', " + unitTestSqlDao.getDbPlatform().getCurTimeFunction() + ", " + "'XX', '----------', 'X', null,null,null,null,' '," + unitTestSqlDao.getDbPlatform().getCurTimeFunction() + "," + unitTestSqlDao.getDbPlatform().getCurTimeFunction() + ")");

        // Should be deleted
        unitTestSqlDao.sqlCommand("insert into GL_ENTRY_T (UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FIN_BALANCE_TYP_CD, FIN_OBJ_TYP_CD, UNIV_FISCAL_PRD_CD, FDOC_TYP_CD," + "FS_ORIGIN_CD, FDOC_NBR, TRN_ENTR_SEQ_NBR, TRN_LDGR_ENTR_DESC, TRN_LDGR_ENTR_AMT, TRN_DEBIT_CRDT_CD, TRANSACTION_DT," + "ORG_DOC_NBR, PROJECT_CD, ORG_REFERENCE_ID, FDOC_REF_TYP_CD, FS_REF_ORIGIN_CD, FDOC_REF_NBR, FDOC_REVERSAL_DT, TRN_ENCUM_UPDT_CD, TRN_POST_DT, TIMESTAMP) " + "values ("+pastFiscalYear+", 'BL', '1031400', '-----', '5000', '---', 'AC', 'EX', '01', 'JV', '01', 'XXX', 1,'YYY', 0, 'D', " + unitTestSqlDao.getDbPlatform().getCurTimeFunction() + ", " + "'XX', '----------', 'X', null,null,null,null,' '," + unitTestSqlDao.getDbPlatform().getCurTimeFunction() + "," + unitTestSqlDao.getDbPlatform().getCurTimeFunction() + ")");

        Step purgeStep = SpringContext.getBean(PurgeEntryStep.class);
        TestUtils.setSystemParameter(AopUtils.getTargetClass(purgeStep), KFSConstants.SystemGroupParameterNames.PURGE_GL_ENTRY_T_BEFORE_YEAR, Integer.toString(currentFiscalYear));

        // Run the purge
        assertTrue("Should return true", purgeStep.execute(getClass().getName(), dateTimeService.getCurrentDate()));

        // Check the results (should be 1 row for current fiscal year)
        List counts = unitTestSqlDao.sqlSelect("select univ_fiscal_yr,count(*) from GL_ENTRY_T group by univ_fiscal_yr order by univ_fiscal_yr");
        assertEquals("Wrong number of years found in gl_entry_t", 1, counts.size());

        Map count2002 = (Map) counts.get(0);
        assertEquals("Selected year is wrong", currentFiscalYear, getInt(count2002, "UNIV_FISCAL_YR"));
        assertEquals("Wrong count for year found", 1, getInt(count2002, "COUNT(*)"));
    }

    /**
     * Tests that balances are purged before 1999
     *
     * @throws Exception thrown if something (likely a SQL issue) goes wrong
     */
    public void testPurgeBalance() throws Exception {
        LOG.debug("testPurgeBalance() started");

        final int currentFiscalYear = TestUtils.getFiscalYearForTesting().intValue();
        final int pastFiscalYear = currentFiscalYear - 1;

        // The data keeps changing, so this will make sure the test always succeeds
        unitTestSqlDao.sqlCommand("delete from GL_BALANCE_T");

        // Should be deleted
        unitTestSqlDao.sqlCommand("insert into GL_BALANCE_T (UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FIN_BALANCE_TYP_CD, FIN_OBJ_TYP_CD, " + "ACLN_ANNL_BAL_AMT, FIN_BEG_BAL_LN_AMT, CONTR_GR_BB_AC_AMT, MO1_ACCT_LN_AMT, MO2_ACCT_LN_AMT, MO3_ACCT_LN_AMT, MO4_ACCT_LN_AMT, MO5_ACCT_LN_AMT, MO6_ACCT_LN_AMT, " + "MO7_ACCT_LN_AMT, MO8_ACCT_LN_AMT, MO9_ACCT_LN_AMT, MO10_ACCT_LN_AMT, MO11_ACCT_LN_AMT, MO12_ACCT_LN_AMT, MO13_ACCT_LN_AMT, TIMESTAMP) " + "values ("+pastFiscalYear+", 'BL', '1031400', '-----', '5000', '---', 'AC', 'EX'," + " 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, " + unitTestSqlDao.getDbPlatform().getCurTimeFunction() + ")");

        // Shouldn't be deleted
        unitTestSqlDao.sqlCommand("insert into GL_BALANCE_T (UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FIN_BALANCE_TYP_CD, FIN_OBJ_TYP_CD, " + "ACLN_ANNL_BAL_AMT, FIN_BEG_BAL_LN_AMT, CONTR_GR_BB_AC_AMT, MO1_ACCT_LN_AMT, MO2_ACCT_LN_AMT, MO3_ACCT_LN_AMT, MO4_ACCT_LN_AMT, MO5_ACCT_LN_AMT, MO6_ACCT_LN_AMT, " + "MO7_ACCT_LN_AMT, MO8_ACCT_LN_AMT, MO9_ACCT_LN_AMT, MO10_ACCT_LN_AMT, MO11_ACCT_LN_AMT, MO12_ACCT_LN_AMT, MO13_ACCT_LN_AMT, TIMESTAMP) " + "values ("+currentFiscalYear+", 'BL', '1031400', '-----', '5000', '---', 'AC', 'EX'," + " 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, " + unitTestSqlDao.getDbPlatform().getCurTimeFunction() + ")");

        Step purgeStep = BatchSpringContext.getStep("purgeBalanceStep");
        TestUtils.setSystemParameter(AopUtils.getTargetClass(purgeStep), KFSConstants.SystemGroupParameterNames.PURGE_GL_ENTRY_T_BEFORE_YEAR, Integer.toString(currentFiscalYear));

        // Run the purge
        assertTrue("Should return true", purgeStep.execute(getClass().getName(), dateTimeService.getCurrentDate()));

        // Check the results (should be 1 row for 1999)
        List counts = unitTestSqlDao.sqlSelect("select univ_fiscal_yr,count(*) from GL_BALANCE_T group by univ_fiscal_yr order by univ_fiscal_yr");
        assertEquals("Wrong number of years found in GL_BALANCE_T", 1, counts.size());

        Map count1999 = (Map) counts.get(0);
        assertEquals("Selected year is wrong", currentFiscalYear, getInt(count1999, "UNIV_FISCAL_YR"));
        assertEquals("Wrong count for year found", 1, getInt(count1999, "COUNT(*)"));
    }

    /**
     * Tests that account balances are purged before 1999
     *
     * @throws Exception thrown if something (likely a SQL issue) goes wrong
     */
    public void testPurgeAccountBalances() throws Exception {
        LOG.debug("testPurgeAccountBalances() started");

        final int currentFiscalYear = TestUtils.getFiscalYearForTesting().intValue();
        final int pastFiscalYear = currentFiscalYear - 1;

        // Clear out the table to start with
        unitTestSqlDao.sqlCommand("DELETE FROM GL_ACCT_BALANCES_T");

        // Should be deleted
        unitTestSqlDao.sqlCommand("INSERT INTO GL_ACCT_BALANCES_T (UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, " + "CURR_BDLN_BAL_AMT, ACLN_ACTLS_BAL_AMT, ACLN_ENCUM_BAL_AMT, TIMESTAMP) VALUES ("+pastFiscalYear+", 'BL', '1031400', '-----', '5000', " + "'---'," + " 0, 0, 0, " + unitTestSqlDao.getDbPlatform().getCurTimeFunction() + ")");

        // Shouldn't be deleted
        unitTestSqlDao.sqlCommand("INSERT INTO GL_ACCT_BALANCES_T (UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, " + "CURR_BDLN_BAL_AMT, ACLN_ACTLS_BAL_AMT, ACLN_ENCUM_BAL_AMT, TIMESTAMP) VALUES ("+currentFiscalYear+", 'BL', '1031400', '-----', '5000', " + "'---'," + " 0, 0, 0, " + unitTestSqlDao.getDbPlatform().getCurTimeFunction() + ")");

        Step purgeStep = BatchSpringContext.getStep("purgeAccountBalancesStep");
        Class purgeStepClass = ProxyUtils.getTargetIfProxied(purgeStep).getClass();
        TestUtils.setSystemParameter(purgeStepClass, KFSConstants.SystemGroupParameterNames.PURGE_GL_ENTRY_T_BEFORE_YEAR, Integer.toString(currentFiscalYear));

        // Run the purge
        assertTrue("Should return true", purgeStep.execute(getClass().getName(), dateTimeService.getCurrentDate()));

        // Check the results (should be 1 row for 1999)
        List counts = unitTestSqlDao.sqlSelect("select univ_fiscal_yr,count(*) from GL_ACCT_BALANCES_T group by univ_fiscal_yr order by univ_fiscal_yr");
        assertEquals("Wrong number of years found in gl_acct_balances_t", 1, counts.size());

        Map count1999 = (Map) counts.get(0);
        assertEquals("Selected year is wrong", currentFiscalYear, getInt(count1999, "UNIV_FISCAL_YR"));
        assertEquals("Wrong count for year found", 1, getInt(count1999, "COUNT(*)"));
    }

    /**
     * Tests that encumbrances are purged before 2002
     *
     * @throws Exception thrown if something (likely a SQL issue) goes wrong
     */
    public void testPurgeEncumbrance() throws Exception {
        LOG.debug("testPurgeEncumbrance() started");

        final int currentFiscalYear = TestUtils.getFiscalYearForTesting().intValue();
        final int pastFiscalYear = currentFiscalYear - 1;

        // Clear out the table
        unitTestSqlDao.sqlCommand("DELETE FROM GL_ENCUMBRANCE_T");

        // Should be deleted
        unitTestSqlDao.sqlCommand("insert into GL_ENCUMBRANCE_T (UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, " + "FIN_BALANCE_TYP_CD, FDOC_TYP_CD, FS_ORIGIN_CD, FDOC_NBR, TRN_ENCUM_DESC, TRN_ENCUM_DT, ACLN_ENCUM_AMT, ACLN_ENCUM_CLS_AMT, " + "ACLN_ENCUM_PRG_CD, TIMESTAMP) values ("+pastFiscalYear+", 'BL', '1031400', '-----', '5000', '---', 'AC', 'JV', '01', 'XXX','Desc'," + unitTestSqlDao.getDbPlatform().getCurTimeFunction() + ", 0, 0, 'N', " + unitTestSqlDao.getDbPlatform().getCurTimeFunction() + ")");

        // Shouldn't be deleted
        unitTestSqlDao.sqlCommand("insert into GL_ENCUMBRANCE_T (UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, " + "FIN_BALANCE_TYP_CD, FDOC_TYP_CD, FS_ORIGIN_CD, FDOC_NBR, TRN_ENCUM_DESC, TRN_ENCUM_DT, ACLN_ENCUM_AMT, ACLN_ENCUM_CLS_AMT, " + "ACLN_ENCUM_PRG_CD, TIMESTAMP) values ("+currentFiscalYear+", 'BL', '1031400', '-----', '5000', '---', 'AC', 'JV', '01', 'XXX', 'Desc', " + unitTestSqlDao.getDbPlatform().getCurTimeFunction() + ", " + "0, 0, 'N', " + unitTestSqlDao.getDbPlatform().getCurTimeFunction() + ")");

        Step purgeStep = BatchSpringContext.getStep("purgeEncumbranceStep");
        Class purgeStepClass = ProxyUtils.getTargetIfProxied(purgeStep).getClass();
        TestUtils.setSystemParameter(purgeStepClass, KFSConstants.SystemGroupParameterNames.PURGE_GL_ENTRY_T_BEFORE_YEAR, Integer.toString(currentFiscalYear));

        // Run the purge
        assertTrue("Should return true", purgeStep.execute(getClass().getName(), dateTimeService.getCurrentDate()));

        // Check the results (should be 1 row for 2002)
        List counts = unitTestSqlDao.sqlSelect("select univ_fiscal_yr,count(*) from GL_ENCUMBRANCE_T group by univ_fiscal_yr order by univ_fiscal_yr");
        assertEquals("Wrong number of years found in gl_encumbrance_t", 1, counts.size());

        Map count2002 = (Map) counts.get(0);
        assertEquals("Selected year is wrong", currentFiscalYear, getInt(count2002, "UNIV_FISCAL_YR"));
        assertEquals("Wrong count for year found", 1, getInt(count2002, "COUNT(*)"));
    }

    /**
     * Tests that collector details are purged before 2002
     *
     * @throws Exception thrown if something (likely a SQL issue) goes wrong
     */
    public void testPurgeCollectorDetail() throws Exception {
        LOG.debug("testPurgeCollectorDetail() started");

        final int currentFiscalYear = TestUtils.getFiscalYearForTesting().intValue();
        final int pastFiscalYear = currentFiscalYear - 1;

        // Clear out the table
        unitTestSqlDao.sqlCommand("DELETE FROM GL_ID_BILL_T");

        // Should be deleted
        unitTestSqlDao.sqlCommand("insert into GL_ID_BILL_T (FS_ORIGIN_CD,UNIV_FISCAL_PRD_CD, UNIV_FISCAL_YR, CREATE_DT, FIN_COA_CD, ACCOUNT_NBR, " + "SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FDOC_IDBIL_SEQ_NBR, FDOC_TYP_CD, FDOC_NBR, OBJ_ID, VER_NBR, FDOC_IDBIL_ITM_AMT, " + "FDOC_IDBIL_NTE_TXT, FIN_OBJ_TYP_CD, FIN_BALANCE_TYP_CD, TRN_ENTR_SEQ_NBR) values ('01','01', "+pastFiscalYear+", " + unitTestSqlDao.getDbPlatform().getCurTimeFunction() + ", 'BL', '1031400', '-----', '5000', '---', '1', 'ID22', 'XXX','" + java.util.UUID.randomUUID().toString() + "', 1, 0, 'x', 'EX', 'AC', 1)");

        // Shouldn't be deleted
        unitTestSqlDao.sqlCommand("insert into GL_ID_BILL_T (FS_ORIGIN_CD,UNIV_FISCAL_PRD_CD, UNIV_FISCAL_YR, CREATE_DT, FIN_COA_CD, ACCOUNT_NBR, " + "SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FDOC_IDBIL_SEQ_NBR, FDOC_TYP_CD, FDOC_NBR, OBJ_ID, VER_NBR, FDOC_IDBIL_ITM_AMT, " + "FDOC_IDBIL_NTE_TXT, FIN_OBJ_TYP_CD, FIN_BALANCE_TYP_CD, TRN_ENTR_SEQ_NBR) values ('01','01', "+currentFiscalYear+", " + unitTestSqlDao.getDbPlatform().getCurTimeFunction() + ", 'BL', '1031400', '-----', '5000', '---', '1', 'ID22', 'XXX','" + java.util.UUID.randomUUID().toString() + "', 1, 0, 'x', 'EX', 'AC', 2)");

        Step purgeStep = BatchSpringContext.getStep("purgeCollectorDetailStep");
        Class purgeStepClass = ProxyUtils.getTargetIfProxied(purgeStep).getClass();
        TestUtils.setSystemParameter(purgeStepClass, KFSConstants.SystemGroupParameterNames.PURGE_GL_ENTRY_T_BEFORE_YEAR, Integer.toString(currentFiscalYear));

        // Run the purge
        assertTrue("Should return true", purgeStep.execute(getClass().getName(), dateTimeService.getCurrentDate()));

        // Check the results (should be 1 row for 2002)
        List counts = unitTestSqlDao.sqlSelect("select univ_fiscal_yr,count(*) from GL_ID_BILL_T group by univ_fiscal_yr order by univ_fiscal_yr");
        assertEquals("Wrong number of years found in gl_id_bill_t", 1, counts.size());

        Map count2002 = (Map) counts.get(0);
        assertEquals("Selected year is wrong", currentFiscalYear, getInt(count2002, "UNIV_FISCAL_YR"));
        assertEquals("Wrong count for year found", 1, getInt(count2002, "COUNT(*)"));
    }

    /**
     * Tests that sufficient funds balances are purged before 1999
     *
     * @throws Exception thrown if something (likely a SQL issue) goes wrong
     */
    public void testPurgeSufficientFundsBalances() throws Exception {
        LOG.debug("testPurgeSufficientFundsBalances() started");

        final int currentFiscalYear = TestUtils.getFiscalYearForTesting().intValue();
        final int pastFiscalYear = currentFiscalYear - 1;

        // Clear out the table
        unitTestSqlDao.sqlCommand("DELETE FROM GL_SF_BALANCES_T");

        // Should be deleted
        unitTestSqlDao.sqlCommand("insert into GL_SF_BALANCES_T (UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, FIN_OBJECT_CD, ACCT_SF_CD, " + "CURR_BDGT_BAL_AMT, ACCT_ACTL_XPND_AMT, ACCT_ENCUM_AMT, TIMESTAMP) values ("+pastFiscalYear+", 'BL', '1031400', '5000','H', 0, 0, 0, " + unitTestSqlDao.getDbPlatform().getCurTimeFunction() + ")");

        // Shouldn't be deleted
        unitTestSqlDao.sqlCommand("insert into GL_SF_BALANCES_T (UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, FIN_OBJECT_CD, ACCT_SF_CD, " + "CURR_BDGT_BAL_AMT, ACCT_ACTL_XPND_AMT, ACCT_ENCUM_AMT, TIMESTAMP) values ("+currentFiscalYear+", 'BL', '1031400', '5000','H', 0, 0, 0, " + unitTestSqlDao.getDbPlatform().getCurTimeFunction() + ")");

        Step purgeStep = BatchSpringContext.getStep("purgeSufficientFundBalancesStep");
        Class purgeStepClass = ProxyUtils.getTargetIfProxied(purgeStep).getClass();
        TestUtils.setSystemParameter(purgeStepClass, KFSConstants.SystemGroupParameterNames.PURGE_GL_ENTRY_T_BEFORE_YEAR, Integer.toString(currentFiscalYear));

        // Run the purge
        assertTrue("Should return true", purgeStep.execute(getClass().getName(), dateTimeService.getCurrentDate()));

        // Check the results (should be 1 row for 1999)
        List counts = unitTestSqlDao.sqlSelect("select univ_fiscal_yr,count(*) from GL_SF_BALANCES_T group by univ_fiscal_yr order by univ_fiscal_yr");
        assertEquals("Wrong number of years found in GL_SF_BALANCES_T", 1, counts.size());

        Map count1999 = (Map) counts.get(0);
        assertEquals("Selected year is wrong", currentFiscalYear, getInt(count1999, "UNIV_FISCAL_YR"));
        assertEquals("Wrong count for year found", 1, getInt(count1999, "COUNT(*)"));
    }

    /**
     * Prints the contents of a List of Maps to System.err
     *
     * @param maps a List of Maps
     */
    private void printList(List maps) {
        for (Iterator iter = maps.iterator(); iter.hasNext();) {
            Map element = (Map) iter.next();
            StringBuffer sb = new StringBuffer();
            for (Iterator iterator = element.keySet().iterator(); iterator.hasNext();) {
                String field = (String) iterator.next();
                sb.append(field);
                sb.append(" = ");
                sb.append(element.get(field));
                sb.append(",");
            }
            System.err.println(sb);
        }
    }

    /**
     * Attempts to convert the value of a MapEntry with the given key into an int
     *
     * @param values a Map of values
     * @param field the key of the value to convert
     * @return the converted value or -1 if conversion was unsuccessful
     */
    private int getInt(Map values, String field) {
        Object o = values.get(field);
        if (o == null) {
            return -1;
        }
        else if (o instanceof BigDecimal) {
            BigDecimal number = (BigDecimal) o;
            return number.intValue();
        }
        else if (o instanceof Long) {
            Long number = (Long) o;
            return number.intValue();
        }
        else {
            return -1;
        }
    }
}
