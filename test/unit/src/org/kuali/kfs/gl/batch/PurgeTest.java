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
package org.kuali.module.gl.batch;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.core.batch.Step;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.gl.dao.UnitTestSqlDao;
import org.kuali.test.KualiTestBaseWithSpringOnly;

public class PurgeTest extends KualiTestBaseWithSpringOnly {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PurgeTest.class);

    private UnitTestSqlDao unitTestSqlDao;

    protected void setUp() throws Exception {
        super.setUp();

        unitTestSqlDao = (UnitTestSqlDao) SpringServiceLocator.getBeanFactory().getBean("glUnitTestSqlDao");
    }

    // This will purge entries before 2002
    public void testPurgeEntry() throws Exception {
        LOG.debug("testPurgeEntry() started");

        // Shouldn't be deleted
        unitTestSqlDao.sqlCommand("insert into GL_ENTRY_T (UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FIN_BALANCE_TYP_CD, FIN_OBJ_TYP_CD, UNIV_FISCAL_PRD_CD, FDOC_TYP_CD," + "FS_ORIGIN_CD, FDOC_NBR, TRN_ENTR_SEQ_NBR, OBJ_ID, VER_NBR, TRN_LDGR_ENTR_DESC, TRN_LDGR_ENTR_AMT, TRN_DEBIT_CRDT_CD, TRANSACTION_DT," + "ORG_DOC_NBR, PROJECT_CD, ORG_REFERENCE_ID, FDOC_REF_TYP_CD, FS_REF_ORIGIN_CD, FDOC_REF_NBR, FDOC_REVERSAL_DT, TRN_ENCUM_UPDT_CD, TRN_POST_DT, TIMESTAMP) " + "values (2002, 'BL', '1031400', '-----', '5000', '---', 'AC', 'EX', '01', 'JV', '01', 'XXX', 1, sys_guid(), 0, 'YYY', 0, 'D', SYSDATE, " + "'XX', '----------', 'X', null,null,null,null,' ',sysdate,sysdate)");

        // Should be deleted
        unitTestSqlDao.sqlCommand("insert into GL_ENTRY_T (UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FIN_BALANCE_TYP_CD, FIN_OBJ_TYP_CD, UNIV_FISCAL_PRD_CD, FDOC_TYP_CD," + "FS_ORIGIN_CD, FDOC_NBR, TRN_ENTR_SEQ_NBR, OBJ_ID, VER_NBR, TRN_LDGR_ENTR_DESC, TRN_LDGR_ENTR_AMT, TRN_DEBIT_CRDT_CD, TRANSACTION_DT," + "ORG_DOC_NBR, PROJECT_CD, ORG_REFERENCE_ID, FDOC_REF_TYP_CD, FS_REF_ORIGIN_CD, FDOC_REF_NBR, FDOC_REVERSAL_DT, TRN_ENCUM_UPDT_CD, TRN_POST_DT, TIMESTAMP) " + "values (2001, 'BL', '1031400', '-----', '5000', '---', 'AC', 'EX', '01', 'JV', '01', 'XXX', 1, sys_guid(), 0, 'YYY', 0, 'D', SYSDATE, " + "'XX', '----------', 'X', null,null,null,null,' ',sysdate,sysdate)");

        Step purgeStep = (Step) SpringServiceLocator.getBeanFactory().getBean("glPurgeEntryStep");

        // For 100% code coverage
        purgeStep.getName();

        // Run the purge
        assertTrue("Should return true", purgeStep.performStep());

        // Check the results (should be 1 row for 2002 and a lot for 2004)
        List counts = unitTestSqlDao.sqlSelect("select univ_fiscal_yr,count(*) from gl_entry_t group by univ_fiscal_yr order by univ_fiscal_yr");
        assertEquals("Wrong number of years found in gl_entry_t", 2, counts.size());

        Map count2002 = (Map) counts.get(0);
        assertEquals("Selected year is wrong", 2002, getInt(count2002, "UNIV_FISCAL_YR"));
        assertEquals("Wrong count for year found", 1, getInt(count2002, "COUNT(*)"));

        Map count2004 = (Map) counts.get(1);
        assertEquals("Selected year is wrong", 2004, getInt(count2004, "UNIV_FISCAL_YR"));
    }

    // This will purge entries before 1999
    public void testPurgeBalance() throws Exception {
        LOG.debug("testPurgeBalance() started");

        // Should be deleted
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FIN_BALANCE_TYP_CD, FIN_OBJ_TYP_CD, OBJ_ID, " + "VER_NBR, ACLN_ANNL_BAL_AMT, FIN_BEG_BAL_LN_AMT, CONTR_GR_BB_AC_AMT, MO1_ACCT_LN_AMT, MO2_ACCT_LN_AMT, MO3_ACCT_LN_AMT, MO4_ACCT_LN_AMT, MO5_ACCT_LN_AMT, MO6_ACCT_LN_AMT, " + "MO7_ACCT_LN_AMT, MO8_ACCT_LN_AMT, MO9_ACCT_LN_AMT, MO10_ACCT_LN_AMT, MO11_ACCT_LN_AMT, MO12_ACCT_LN_AMT, MO13_ACCT_LN_AMT, TIMESTAMP) " + "values (1998, 'BL', '1031400', '-----', '5000', '---', 'AC', 'EX', sys_guid(), 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, SYSDATE)");

        // Shouldn't be deleted
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FIN_BALANCE_TYP_CD, FIN_OBJ_TYP_CD, OBJ_ID, " + "VER_NBR, ACLN_ANNL_BAL_AMT, FIN_BEG_BAL_LN_AMT, CONTR_GR_BB_AC_AMT, MO1_ACCT_LN_AMT, MO2_ACCT_LN_AMT, MO3_ACCT_LN_AMT, MO4_ACCT_LN_AMT, MO5_ACCT_LN_AMT, MO6_ACCT_LN_AMT, " + "MO7_ACCT_LN_AMT, MO8_ACCT_LN_AMT, MO9_ACCT_LN_AMT, MO10_ACCT_LN_AMT, MO11_ACCT_LN_AMT, MO12_ACCT_LN_AMT, MO13_ACCT_LN_AMT, TIMESTAMP) " + "values (1999, 'BL', '1031400', '-----', '5000', '---', 'AC', 'EX', sys_guid(), 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, SYSDATE)");

        Step purgeStep = (Step) SpringServiceLocator.getBeanFactory().getBean("glPurgeBalanceStep");

        // For 100% code coverage
        purgeStep.getName();

        // Run the purge
        assertTrue("Should return true", purgeStep.performStep());

        // Check the results (should be 1 row for 1999 and a lot for 2004)
        List counts = unitTestSqlDao.sqlSelect("select univ_fiscal_yr,count(*) from gl_balance_t group by univ_fiscal_yr order by univ_fiscal_yr");
        assertEquals("Wrong number of years found in gl_balance_t", 2, counts.size());

        Map count1999 = (Map) counts.get(0);
        assertEquals("Selected year is wrong", 1999, getInt(count1999, "UNIV_FISCAL_YR"));
        assertEquals("Wrong count for year found", 1, getInt(count1999, "COUNT(*)"));

        Map count2004 = (Map) counts.get(1);
        assertEquals("Selected year is wrong", 2004, getInt(count2004, "UNIV_FISCAL_YR"));
    }

    // This will purge entries before 1999
    public void testPurgeAccountBalances() throws Exception {
        LOG.debug("testPurgeAccountBalances() started");

        // Clear out the table to start with
        unitTestSqlDao.sqlCommand("DELETE FROM GL_ACCT_BALANCES_T");

        // Should be deleted
        unitTestSqlDao.sqlCommand("INSERT INTO GL_ACCT_BALANCES_T (UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, " + "OBJ_ID, VER_NBR, CURR_BDLN_BAL_AMT, ACLN_ACTLS_BAL_AMT, ACLN_ENCUM_BAL_AMT, TIMESTAMP) VALUES (1998, 'BL', '1031400', '-----', '5000', " + "'---', sys_guid(), 0, 0, 0, 0, SYSDATE)");

        // Shouldn't be deleted
        unitTestSqlDao.sqlCommand("INSERT INTO GL_ACCT_BALANCES_T (UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, " + "OBJ_ID, VER_NBR, CURR_BDLN_BAL_AMT, ACLN_ACTLS_BAL_AMT, ACLN_ENCUM_BAL_AMT, TIMESTAMP) VALUES (1999, 'BL', '1031400', '-----', '5000', " + "'---', sys_guid(), 0, 0, 0, 0, SYSDATE)");

        Step purgeStep = (Step) SpringServiceLocator.getBeanFactory().getBean("glPurgeAccountBalancesStep");

        // For 100% code coverage
        purgeStep.getName();

        // Run the purge
        assertTrue("Should return true", purgeStep.performStep());

        // Check the results (should be 1 row for 1999)
        List counts = unitTestSqlDao.sqlSelect("select univ_fiscal_yr,count(*) from gl_acct_balances_t group by univ_fiscal_yr order by univ_fiscal_yr");
        assertEquals("Wrong number of years found in gl_acct_balances_t", 1, counts.size());

        Map count1999 = (Map) counts.get(0);
        assertEquals("Selected year is wrong", 1999, getInt(count1999, "UNIV_FISCAL_YR"));
        assertEquals("Wrong count for year found", 1, getInt(count1999, "COUNT(*)"));
    }

    // This will purge entries before 2002
    public void testPurgeEncumbrance() throws Exception {
        LOG.debug("testPurgeEncumbrance() started");

        // Clear out the table
        unitTestSqlDao.sqlCommand("DELETE FROM GL_ENCUMBRANCE_T");

        // Should be deleted
        unitTestSqlDao.sqlCommand("insert into GL_ENCUMBRANCE_T (UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, " + "FIN_BALANCE_TYP_CD, FDOC_TYP_CD, FS_ORIGIN_CD, FDOC_NBR, OBJ_ID, VER_NBR, TRN_ENCUM_DESC, TRN_ENCUM_DT, ACLN_ENCUM_AMT, ACLN_ENCUM_CLS_AMT, " + "ACLN_ENCUM_PRG_IND, TIMESTAMP) values (2001, 'BL', '1031400', '-----', '5000', '---', 'AC', 'JV', '01', 'XXX', sys_guid(), 0, 'Desc', SYSDATE, " + "0, 0, 'N', SYSDATE)");

        // Shouldn't be deleted
        unitTestSqlDao.sqlCommand("insert into GL_ENCUMBRANCE_T (UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, " + "FIN_BALANCE_TYP_CD, FDOC_TYP_CD, FS_ORIGIN_CD, FDOC_NBR, OBJ_ID, VER_NBR, TRN_ENCUM_DESC, TRN_ENCUM_DT, ACLN_ENCUM_AMT, ACLN_ENCUM_CLS_AMT, " + "ACLN_ENCUM_PRG_IND, TIMESTAMP) values (2002, 'BL', '1031400', '-----', '5000', '---', 'AC', 'JV', '01', 'XXX', sys_guid(), 0, 'Desc', SYSDATE, " + "0, 0, 'N', SYSDATE)");

        Step purgeStep = (Step) SpringServiceLocator.getBeanFactory().getBean("glPurgeEncumbranceStep");

        // For 100% code coverage
        purgeStep.getName();

        // Run the purge
        assertTrue("Should return true", purgeStep.performStep());

        // Check the results (should be 1 row for 2002)
        List counts = unitTestSqlDao.sqlSelect("select univ_fiscal_yr,count(*) from gl_encumbrance_t group by univ_fiscal_yr order by univ_fiscal_yr");
        assertEquals("Wrong number of years found in gl_encumbrance_t", 1, counts.size());

        Map count2002 = (Map) counts.get(0);
        assertEquals("Selected year is wrong", 2002, getInt(count2002, "UNIV_FISCAL_YR"));
        assertEquals("Wrong count for year found", 1, getInt(count2002, "COUNT(*)"));
    }

    // This will purge entries before 2002
    public void testPurgeIdBill() throws Exception {
        LOG.debug("testPurgeIdBill() started");

        // Clear out the table
        unitTestSqlDao.sqlCommand("DELETE FROM GL_ID_BILL_T");

        // Should be deleted
        unitTestSqlDao.sqlCommand("insert into GL_ID_BILL_T (FS_ORIGIN_CD,UNIV_FISCAL_PRD_CD, UNIV_FISCAL_YR, CREATE_DT, CREATE_SEQ, FIN_COA_CD, ACCOUNT_NBR, " + "SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FDOC_IDBIL_SEQ_NBR, FDOC_TYP_CD, FDOC_NBR, OBJ_ID, VER_NBR, FDOC_IDBIL_ITM_AMT, " + "FDOC_IDBIL_NTE_TXT) values ('01','01', 2001, SYSDATE, '1', 'BL', '1031400', '-----', '5000', '---', '1', 'ID22', 'XXX', sys_guid(), 1, 0, 'x')");

        // Shouldn't be deleted
        unitTestSqlDao.sqlCommand("insert into GL_ID_BILL_T (FS_ORIGIN_CD,UNIV_FISCAL_PRD_CD, UNIV_FISCAL_YR, CREATE_DT, CREATE_SEQ, FIN_COA_CD, ACCOUNT_NBR, " + "SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FDOC_IDBIL_SEQ_NBR, FDOC_TYP_CD, FDOC_NBR, OBJ_ID, VER_NBR, FDOC_IDBIL_ITM_AMT, " + "FDOC_IDBIL_NTE_TXT) values ('01','01', 2002, SYSDATE, '1', 'BL', '1031400', '-----', '5000', '---', '1', 'ID22', 'XXX', sys_guid(), 1, 0, 'x')");

        Step purgeStep = (Step) SpringServiceLocator.getBeanFactory().getBean("glPurgeInterDepartmentalBillingStep");

        // For 100% code coverage
        purgeStep.getName();

        // Run the purge
        assertTrue("Should return true", purgeStep.performStep());

        // Check the results (should be 1 row for 2002)
        List counts = unitTestSqlDao.sqlSelect("select univ_fiscal_yr,count(*) from gl_id_bill_t group by univ_fiscal_yr order by univ_fiscal_yr");
        assertEquals("Wrong number of years found in gl_id_bill_t", 1, counts.size());

        Map count2002 = (Map) counts.get(0);
        assertEquals("Selected year is wrong", 2002, getInt(count2002, "UNIV_FISCAL_YR"));
        assertEquals("Wrong count for year found", 1, getInt(count2002, "COUNT(*)"));
    }

    // This will purge entries before 1999
    public void testPurgeSufficientFundsBalances() throws Exception {
        LOG.debug("testPurgeSufficientFundsBalances() started");

        // Clear out the table
        unitTestSqlDao.sqlCommand("DELETE FROM GL_SF_BALANCES_T");

        // Should be deleted
        unitTestSqlDao.sqlCommand("insert into gl_sf_balances_t (UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, FIN_OBJECT_CD, OBJ_ID, VER_NBR, ACCT_SF_CD, " + "CURR_BDGT_BAL_AMT, ACCT_ACTL_XPND_AMT, ACCT_ENCUM_AMT, TIMESTAMP) values (1998, 'BL', '1031400', '5000', sys_guid(), 0, 'H', 0, 0, 0, SYSDATE)");

        // Shouldn't be deleted
        unitTestSqlDao.sqlCommand("insert into gl_sf_balances_t (UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, FIN_OBJECT_CD, OBJ_ID, VER_NBR, ACCT_SF_CD, " + "CURR_BDGT_BAL_AMT, ACCT_ACTL_XPND_AMT, ACCT_ENCUM_AMT, TIMESTAMP) values (1999, 'BL', '1031400', '5000', sys_guid(), 0, 'H', 0, 0, 0, SYSDATE)");

        Step purgeStep = (Step) SpringServiceLocator.getBeanFactory().getBean("glPurgeSufficientFundBalancesStep");

        // For 100% code coverage
        purgeStep.getName();

        // Run the purge
        assertTrue("Should return true", purgeStep.performStep());

        // Check the results (should be 1 row for 1999)
        List counts = unitTestSqlDao.sqlSelect("select univ_fiscal_yr,count(*) from gl_sf_balances_t group by univ_fiscal_yr order by univ_fiscal_yr");
        assertEquals("Wrong number of years found in gl_sf_balances_t", 1, counts.size());

        Map count1999 = (Map) counts.get(0);
        assertEquals("Selected year is wrong", 1999, getInt(count1999, "UNIV_FISCAL_YR"));
        assertEquals("Wrong count for year found", 1, getInt(count1999, "COUNT(*)"));
    }

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

    private int getInt(Map values, String field) {
        BigDecimal number = (BigDecimal) values.get(field);
        if (number == null) {
            return -1;
        }
        else {
            return number.intValue();
        }
    }
}
