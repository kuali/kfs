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
package org.kuali.module.gl.service;

import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.kuali.core.dbplatform.RawSQL;
import org.kuali.core.service.PersistenceService;
import org.kuali.core.util.Guid;
import org.kuali.core.util.UnitTestSqlDao;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.context.TestUtils;
import org.kuali.kfs.service.impl.ParameterConstants;
import org.kuali.module.gl.GLConstants;
import org.kuali.module.gl.bo.SufficientFundBalances;
import org.kuali.module.gl.bo.SufficientFundRebuild;
import org.kuali.module.gl.dao.SufficientFundBalancesDao;
import org.kuali.module.gl.dao.SufficientFundRebuildDao;
import org.kuali.test.ConfigureContext;

/**
 * Tests the SufficientFundsRebuilderService
 */
@ConfigureContext
@RawSQL
public class SufficientFundsRebuilderServiceTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SufficientFundsRebuilderServiceTest.class);

    private SufficientFundsRebuilderService sufficientFundsRebuilderService = null;
    private SufficientFundRebuildDao sufficientFundRebuildDao = null;
    private SufficientFundBalancesDao sufficientFundBalancesDao = null;
    protected PersistenceService persistenceService;
    protected UnitTestSqlDao unitTestSqlDao = null;

    /**
     * Initializes the services needed for the test
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        LOG.debug("setUp() started");

        sufficientFundsRebuilderService = SpringContext.getBean(SufficientFundsRebuilderService.class);
        sufficientFundRebuildDao = SpringContext.getBean(SufficientFundRebuildDao.class);
        sufficientFundBalancesDao = SpringContext.getBean(SufficientFundBalancesDao.class);
        persistenceService = SpringContext.getBean(PersistenceService.class);
        unitTestSqlDao = SpringContext.getBean(UnitTestSqlDao.class);
        
        // and reset the closing fiscal year
        TestUtils.setSystemParameter(ParameterConstants.GENERAL_LEDGER_BATCH.class, GLConstants.ANNUAL_CLOSING_FISCAL_YEAR_PARM, "2007");
    }

    /**
     * Tests SF fund rebuilding conversion
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testConversion() throws Exception {
        String[] expectedOutput = new String[] { "2007BL2220090    H                1                1                1", "2007BL2231406PRINL                0           180.35                0", "2007BL2231406S&E L            12000             9.55                0", "2007BL2231406TRAVL                0           2558.9                0", "2007BL2231407GENXC                1                1                1", "2007BL22314084938O                0           348.27                0", "2007BL22314085215O                0              100                0", };

        updateAccount();
        clearSufficientFundBalanceTable();
        clearSufficientFundRebuildTable();
        populateGLSFRebuildTableForConversion();
        populateGLSFBalanceTableForConversion();
        prepareGLBalancesTable();
        sufficientFundsRebuilderService.rebuildSufficientFunds();
        assertSFRBEmpty();
        assertSFBLEntries(expectedOutput);
    }

    /**
     * Tests that added SF balance records exist after all SF balances are rebuilt
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testAddedSFBLRecords() throws Exception {
        String[] expectedOutput = new String[] { "2007BL2220090    H         10756.57                0            503.5", "2007BL2231406PRINL                0           180.35                0", "2007BL2231406S&E L            12000             9.55                0", "2007BL2231406TRAVL                0           2558.9                0", "2007BL2231407GENXC                0          -984.12                0", "2007BL22314084938O                0           348.27                0", "2007BL22314085215O                0              100                0", "2007BL2231415    A            12000           2748.8                0" };

        updateAccount();
        clearSufficientFundBalanceTable();
        clearSufficientFundRebuildTable();
        populateGLSFRebuildTable();
        prepareGLBalancesTable();
        sufficientFundsRebuilderService.rebuildSufficientFunds();
        assertSFRBEmpty();
        assertSFBLEntries(expectedOutput);
    }

    /**
     * This converts an array of String-transactions to SF balances in the database
     * 
     * @param transactions an array of String-formatted sufficient fund rebuild record
     */
    protected void loadInputTransactions(String[] transactions) {
        for (int i = 0; i < transactions.length; i++) {
            createSFRB(transactions[i]);
        }
    }

    /**
     * Inserts a String-formatted sufficient fund rebuild record into the database as a sufficient fund rebuild record
     * 
     * @param line the String-formatted sufficient fund rebuild record to convert and save
     * @return the converted sufficient fund rebuild record
     */
    protected SufficientFundRebuild createSFRB(String line) {
        SufficientFundRebuild sfrb = new SufficientFundRebuild(line);

        // This is being done to fool the caching. If it isn't done, when
        // we try to retrieve this entry later, none of the referenced tables will
        // be loaded.
        persistenceService.retrieveNonKeyFields(sfrb);

        sufficientFundRebuildDao.save(sfrb);
        return sfrb;
    }

    /**
     * Deletes all records in the sufficient funds balances table
     */
    protected void clearSufficientFundBalanceTable() {
        unitTestSqlDao.sqlCommand("delete from gl_sf_balances_t");
    }

    /**
     * Deletes all records in the sufficient funds rebuild table
     */
    protected void clearSufficientFundRebuildTable() {
        unitTestSqlDao.sqlCommand("delete from gl_sf_rebuild_t");
    }

    /**
     * Check that the SFRB table is empty.
     */
    protected void assertSFRBEmpty() {
        List l = unitTestSqlDao.sqlSelect("select * from GL_SF_REBUILD_T");
        assertEquals("GL_SF_REBUILD_T should be empty", 0, l.size());
    }

    /**
     * Check all the entries in gl_sf_balances_t against the data passed in. If any of them are different, assert an error.
     * 
     * @param requiredSFBLs an array of expected sufficient fund balance records, formatted as Strings
     */
    protected void assertSFBLEntries(String[] requiredSFBLs) {

        Collection c = sufficientFundBalancesDao.testingGetAllEntries();

        assertEquals("Wrong number of SFBL", requiredSFBLs.length, c.size());

        if (requiredSFBLs.length == c.size()) {
            int count = 0;
            for (Iterator iter = c.iterator(); iter.hasNext();) {
                SufficientFundBalances foundSFBL = (SufficientFundBalances) iter.next();
                if (!requiredSFBLs[count].equals(foundSFBL.getLine())) {
                    System.err.println("Found:     " + foundSFBL.getLine());
                    System.err.println("Should be: " + requiredSFBLs[count]);
                    fail("SF balance doesn't match");
                }
                ++count;
            }
        }
    }

    /**
     * Updates the accounts used by this test, so that each algorithm of sufficient funds checking is used
     */
    private void updateAccount() {
        unitTestSqlDao.sqlCommand("update ca_account_t set acct_sf_cd = 'H' where account_nbr = '2220090'");
        unitTestSqlDao.sqlCommand("update ca_account_t set acct_sf_cd = 'L' where account_nbr = '2231406'");
        unitTestSqlDao.sqlCommand("update ca_account_t set acct_sf_cd = 'C' where account_nbr = '2231407'");
        unitTestSqlDao.sqlCommand("update ca_account_t set acct_sf_cd = 'O' where account_nbr = '2231408'");
        unitTestSqlDao.sqlCommand("update ca_account_t set acct_sf_cd = 'A' where account_nbr = '2231415'");
    }

    /**
     * Rebuilds the SF rebuild table completely, to have a set of expected data for testAddedSFBLRecords()
     */
    private void populateGLSFRebuildTable() {
        unitTestSqlDao.sqlCommand("delete from GL_SF_REBUILD_T");
        unitTestSqlDao.sqlCommand("insert into GL_SF_REBUILD_T (fin_coa_cd,acct_fobj_typ_cd,acct_nbr_fobj_cd,obj_id,ver_nbr) values ('BA','A','6044913','" + new Guid().toString() + "',1)");
        unitTestSqlDao.sqlCommand("insert into GL_SF_REBUILD_T (fin_coa_cd,acct_fobj_typ_cd,acct_nbr_fobj_cd,obj_id,ver_nbr) values ('BL','A','1031420','" + new Guid().toString() + "',1)");
        unitTestSqlDao.sqlCommand("insert into GL_SF_REBUILD_T (fin_coa_cd,acct_fobj_typ_cd,acct_nbr_fobj_cd,obj_id,ver_nbr) values ('BL','A','2220090','" + new Guid().toString() + "',1)");
        unitTestSqlDao.sqlCommand("insert into GL_SF_REBUILD_T (fin_coa_cd,acct_fobj_typ_cd,acct_nbr_fobj_cd,obj_id,ver_nbr) values ('BL','A','2231406','" + new Guid().toString() + "',1)");
        unitTestSqlDao.sqlCommand("insert into GL_SF_REBUILD_T (fin_coa_cd,acct_fobj_typ_cd,acct_nbr_fobj_cd,obj_id,ver_nbr) values ('BL','A','2231407','" + new Guid().toString() + "',1)");
        unitTestSqlDao.sqlCommand("insert into GL_SF_REBUILD_T (fin_coa_cd,acct_fobj_typ_cd,acct_nbr_fobj_cd,obj_id,ver_nbr) values ('BL','A','2231408','" + new Guid().toString() + "',1)");
        unitTestSqlDao.sqlCommand("insert into GL_SF_REBUILD_T (fin_coa_cd,acct_fobj_typ_cd,acct_nbr_fobj_cd,obj_id,ver_nbr) values ('BL','A','2231415','" + new Guid().toString() + "',1)");
    }

    /**
     * Populates the SF rebuild table for testConversion()
     */
    private void populateGLSFRebuildTableForConversion() {
        unitTestSqlDao.sqlCommand("delete from GL_SF_REBUILD_T");
        unitTestSqlDao.sqlCommand("insert into GL_SF_REBUILD_T (fin_coa_cd,acct_fobj_typ_cd,acct_nbr_fobj_cd,obj_id,ver_nbr) values ('BL','O','PRIN','" + new Guid().toString() + "',1)");
        unitTestSqlDao.sqlCommand("insert into GL_SF_REBUILD_T (fin_coa_cd,acct_fobj_typ_cd,acct_nbr_fobj_cd,obj_id,ver_nbr) values ('BL','O','4938','" + new Guid().toString() + "',1)");
    }

    /**
     * Populates the SF balances table with expected values for testConversion()
     */
    private void populateGLSFBalanceTableForConversion() {
        unitTestSqlDao.sqlCommand("delete from GL_SF_BALANCES_T");
        unitTestSqlDao.sqlCommand("insert into GL_SF_BALANCES_T (UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, FIN_OBJECT_CD, ACCT_SF_CD, CURR_BDGT_BAL_AMT, ACCT_ACTL_XPND_AMT, ACCT_ENCUM_AMT) values ('2007','BL','2220090','    ','H',1,1,1)");
        unitTestSqlDao.sqlCommand("insert into GL_SF_BALANCES_T (UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, FIN_OBJECT_CD, ACCT_SF_CD, CURR_BDGT_BAL_AMT, ACCT_ACTL_XPND_AMT, ACCT_ENCUM_AMT) values ('2007','BL','2231406','PRIN','L',1,1,1)");
        unitTestSqlDao.sqlCommand("insert into GL_SF_BALANCES_T (UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, FIN_OBJECT_CD, ACCT_SF_CD, CURR_BDGT_BAL_AMT, ACCT_ACTL_XPND_AMT, ACCT_ENCUM_AMT) values ('2007','BL','2231406','S&E ','H',1,1,1)");
        unitTestSqlDao.sqlCommand("insert into GL_SF_BALANCES_T (UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, FIN_OBJECT_CD, ACCT_SF_CD, CURR_BDGT_BAL_AMT, ACCT_ACTL_XPND_AMT, ACCT_ENCUM_AMT) values ('2007','BL','2231406','TRAV','H',1,1,1)");
        unitTestSqlDao.sqlCommand("insert into GL_SF_BALANCES_T (UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, FIN_OBJECT_CD, ACCT_SF_CD, CURR_BDGT_BAL_AMT, ACCT_ACTL_XPND_AMT, ACCT_ENCUM_AMT) values ('2007','BL','2231407','GENX','C',1,1,1)");
        unitTestSqlDao.sqlCommand("insert into GL_SF_BALANCES_T (UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, FIN_OBJECT_CD, ACCT_SF_CD, CURR_BDGT_BAL_AMT, ACCT_ACTL_XPND_AMT, ACCT_ENCUM_AMT) values ('2007','BL','2231408','4938','H',1,1,1)");
        unitTestSqlDao.sqlCommand("insert into GL_SF_BALANCES_T (UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, FIN_OBJECT_CD, ACCT_SF_CD, CURR_BDGT_BAL_AMT, ACCT_ACTL_XPND_AMT, ACCT_ENCUM_AMT) values ('2007','BL','2231408','5215','H',1,1,1)");
    }

    /**
     * Populates the SF balances table with expected values for testAddedSFBLRecords()
     */
    private void prepareGLBalancesTable() {
        unitTestSqlDao.sqlCommand("delete from gl_balance_t where account_nbr in ('6044913','1031420','2220090','2231406','2231407','2231408','2231415')");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','1031420','-----','8000','---','AC','AS',0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2220090','-----','5050','---','AC','EX',466,0,0,0,0,0,0,0,0,40,60,32,74,0,260,0)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2220090','-----','8000','---','AC','AS',10756.57,0,0,0,0,0,0,0,0,-330.88,-1119.2,17391.01,-1068.19,-2828.47,-1287.7,0)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2220090','-----','1699','---','AC','TI',20000,0,0,0,0,0,0,0,0,0,0,20000,0,0,0,0)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2220090','-----','4308','---','AC','EX',1.48,0,0,0,0,0,0,0,0,1.48,0,0,0,0,0,0)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2220090','-----','4166','---','AC','EX',215,0,0,0,0,0,0,0,0,0,0,0,0,0,215,0)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2220090','-----','5000','---','NB','EX',-9243.43,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-9243.43)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2220090','-----','5055','---','AC','EX',110,0,0,0,0,0,0,0,0,0,0,110,0,0,0,0)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2220090','-----','9892','---','EX','FB',503.5,0,0,0,0,0,0,0,0,657.65,956.75,-1110.9,-453.25,1031.5,-578.25,0)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2220090','-----','6100','---','EX','EX',503.5,0,0,0,0,0,0,0,0,657.65,956.75,-1110.9,-453.25,1031.5,-578.25,0)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2220090','-----','9899','---','NB','FB',10756.57,0,0,0,0,0,0,0,0,0,0,0,0,0,0,10756.57)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2220090','-----','1800','---','NB','TI',-20000,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-20000)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2220090','-----','6200','---','AC','EX',1445.86,0,0,0,0,0,0,0,0,0,0,0,0,1445.86,0,0)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2220090','-----','5000','---','AC','EX',302.95,0,0,0,0,0,0,0,0,50,215,0,0,0,37.95,0)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2220090','-----','6140','---','AC','EX',25.5,0,0,0,0,0,0,0,0,0,25.5,0,0,0,0,0)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2220090','-----','6100','---','AC','EX',6676.64,0,0,0,0,0,0,0,0,239.4,818.7,2466.99,994.19,1382.61,774.75,0)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2231406','-----','0110','---','BB','IN',-12000,12000,0,0,0,0,0,0,0,-12000,0,0,0,0,0,0)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2231406','-----','9899','---','NB','FB',-2748.8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-2748.8)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2231406','-----','5000','---','NB','EX',-2748.8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-2748.8)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2231406','-----','5027','---','AC','EX',9.55,0,0,0,0,0,0,0,0,0,0,0,0,0,9.55,0)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2231406','-----','4110','---','AC','EX',92.55,0,0,0,0,0,0,0,0,0,0,0,0,0,92.55,0)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2231406','-----','5000','---','CB','EX',0,12000,0,0,0,0,0,0,0,0,0,0,0,0,0,0)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2231406','-----','0110','---','CB','IN',0,12000,0,0,0,0,0,0,0,0,0,0,0,0,0,0)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2231406','-----','9899','---','AC','FB',0,12007.47,0,0,0,0,0,0,0,0,0,0,0,0,0,0)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2231406','-----','9892','---','EX','FB',0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2231406','-----','4055','---','AC','EX',87.8,0,0,62.31,0,0,0,25.49,0,0,0,0,0,0,0,0)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2231406','-----','8000','---','AC','AS',-2748.8,12007.47,0,-62.31,0,0,-2558.9,-25.49,0,0,0,0,0,0,-102.1,0)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2231406','-----','6200','---','EX','EX',0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2231406','-----','6200','---','AC','EX',2558.9,0,0,0,0,0,2558.9,0,0,0,0,0,0,0,0,0)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2231406','-----','5000','---','BB','EX',-12000,12000,0,0,0,0,0,0,0,-12000,0,0,0,0,0,0)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2231407','-----','1800','---','AC','IN',49.7,0,0,0,0,0,0,0,0,0,0,0,49.7,0,0,0)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2231407','-----','9899','---','NB','FB',1033.82,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1033.82)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2231407','-----','5000','---','NB','EX',984.12,0,0,0,0,0,0,0,0,0,0,0,0,0,0,984.12)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2231407','-----','1800','---','NB','IN',-49.7,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-49.7)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2231407','-----','9041','---','AC','LI',0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2231407','-----','5200','---','AC','EX',1305,0,0,0,0,0,0,0,0,0,0,0,0,0,1305,0)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2231407','-----','9892','---','EX','FB',0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2231407','-----','5200','---','EX','EX',0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2231407','-----','9899','---','AC','FB',0,597.82,0,0,0,0,0,0,0,0,0,0,0,0,0,0)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2231407','-----','4938','---','AC','EX',18.04,0,0,0,8.99,9.05,0,0,0,0,0,0,0,0,0,0)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2231407','-----','5000','---','AC','EX',12.34,0,0,0,0,0,12.34,0,0,0,0,0,0,0,0,0)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2231407','-----','8000','---','AC','AS',1033.82,597.82,0,0,-348.69,-9.05,-12.34,0,2894.2,0,-235,0,49.7,0,-1305,0)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2231407','-----','4061','---','AC','EX',-2554.5,0,0,0,339.7,0,0,0,-2894.2,0,0,0,0,0,0,0)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2231407','-----','4616','---','AC','EX',235,0,0,0,0,0,0,0,0,0,235,0,0,0,0,0)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2231408','-----','1800','---','AC','IN',50,0,0,0,0,0,0,0,0,0,50,0,0,0,0,0)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2231408','-----','8000','---','AC','AS',-398.27,1918.46,0,0,-291.66,-56.61,0,0,0,0,50,0,0,0,-100,0)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2231408','-----','9899','---','NB','FB',-398.27,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-398.27)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2231408','-----','5000','---','NB','EX',-448.27,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-448.27)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2231408','-----','1800','---','NB','IN',-50,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-50)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2231408','-----','9041','---','AC','LI',0,0,0,0,0,0,0,0,0,0,0,0,0,100,-100,0)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2231408','-----','5215','---','AC','EX',100,0,0,0,0,0,0,0,0,0,0,0,0,100,0,0)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2231408','-----','9892','---','EX','FB',0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2231408','-----','5215','---','EX','EX',0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2231408','-----','4938','---','AC','EX',348.27,0,0,0,291.66,56.61,0,0,0,0,0,0,0,0,0,0)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2231408','-----','9899','---','AC','FB',0,1918.46,0,0,0,0,0,0,0,0,0,0,0,0,0,0)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2231415','-----','0110','---','BB','IN',-12000,12000,0,0,0,0,0,0,0,-12000,0,0,0,0,0,0)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2231415','-----','9899','---','NB','FB',-2748.8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-2748.8)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2231415','-----','5000','---','NB','EX',-2748.8,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-2748.8)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2231415','-----','5027','---','AC','EX',9.55,0,0,0,0,0,0,0,0,0,0,0,0,0,9.55,0)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2231415','-----','4110','---','AC','EX',92.55,0,0,0,0,0,0,0,0,0,0,0,0,0,92.55,0)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2231415','-----','5000','---','CB','EX',0,12000,0,0,0,0,0,0,0,0,0,0,0,0,0,0)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2231415','-----','0110','---','CB','IN',0,12000,0,0,0,0,0,0,0,0,0,0,0,0,0,0)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2231415','-----','9899','---','AC','FB',0,12007.47,0,0,0,0,0,0,0,0,0,0,0,0,0,0)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2231415','-----','9892','---','EX','FB',0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2231415','-----','4055','---','AC','EX',87.8,0,0,62.31,0,0,0,25.49,0,0,0,0,0,0,0,0)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2231415','-----','8000','---','AC','AS',-2748.8,12007.47,0,-62.31,0,0,-2558.9,-25.49,0,0,0,0,0,0,-102.1,0)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2231415','-----','6200','---','EX','EX',0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2231415','-----','6200','---','AC','EX',2558.9,0,0,0,0,0,2558.9,0,0,0,0,0,0,0,0,0)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BL','2231415','-----','5000','---','BB','EX',-12000,12000,0,0,0,0,0,0,0,-12000,0,0,0,0,0,0)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BA','6044913','-----','1476','---','AC','IC',18675,0,0,2700,900,1425,0,1050,0,0,0,0,0,0,12600,0)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BA','6044913','-----','9899','---','AC','FB',0,49525.04,0,0,0,0,0,0,0,0,0,0,0,0,0,0)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BA','6044913','-----','8000','---','AC','AS',2700,49525.04,0,0,0,0,0,0,0,0,0,0,0,0,2700,0)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BA','6044913','-----','1800','---','NB','IC',-18675,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-18675)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BA','6044913','-----','9899','---','NB','FB',21375,0,0,0,0,0,0,0,0,0,0,0,0,0,0,21375)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BA','6044913','-----','1800','---','NB','IN',-2700,0,0,0,0,0,0,0,0,0,0,0,0,0,0,-2700)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BA','6044913','-----','1476','---','AC','IN',2700,0,0,0,0,0,0,0,0,0,0,0,0,0,2700,0)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BA','6044913','-----','4768','---','IE','EX',0,0.03,0,0,0,0,0,0,0,0,0,0,0,0,0,0)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BA','6044913','-----','9891','---','IE','FB',0,0.03,0,0,0,0,0,0,0,0,0,0,0,0,0,0)");
        unitTestSqlDao.sqlCommand("insert into gl_balance_t (UNIV_FISCAL_YR,FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,FIN_OBJECT_CD,FIN_SUB_OBJ_CD,FIN_BALANCE_TYP_CD,FIN_OBJ_TYP_CD,ACLN_ANNL_BAL_AMT,FIN_BEG_BAL_LN_AMT,CONTR_GR_BB_AC_AMT,MO1_ACCT_LN_AMT,MO2_ACCT_LN_AMT,MO3_ACCT_LN_AMT,MO4_ACCT_LN_AMT,MO5_ACCT_LN_AMT,MO6_ACCT_LN_AMT,MO7_ACCT_LN_AMT,MO8_ACCT_LN_AMT,MO9_ACCT_LN_AMT,MO10_ACCT_LN_AMT,MO11_ACCT_LN_AMT,MO12_ACCT_LN_AMT,MO13_ACCT_LN_AMT) values ('2007','BA','6044913','-----','9897','---','AC','FB',-18675,0,0,-2700,-900,-1425,0,-1050,0,0,0,0,0,0,-12600,0)");
    }
}
