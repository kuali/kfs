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
package org.kuali.module.gl.service;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.module.gl.dao.UnitTestSqlDao;
import org.kuali.module.gl.util.SufficientFundsItem;
import org.kuali.test.KualiTestBase;
import org.kuali.test.WithTestSpringContext;

@WithTestSpringContext
public class SufficientFundsServiceTest extends KualiTestBase {

    private SufficientFundsService sufficientFundsService = null;
    private UnitTestSqlDao unitTestSqlDao = null;

    protected void setUp() throws Exception {
        super.setUp();

        sufficientFundsService = SpringServiceLocator.getSufficientFundsService();
        unitTestSqlDao = (UnitTestSqlDao)SpringServiceLocator.getBeanFactory().getBean("glUnitTestSqlDao");
    }

    private void prepareSufficientFundsData(String accountNumber, String sfType, String sfObjCd, Integer budgetAmt, Integer actualAmt, Integer encAmt, boolean createPles) {
        unitTestSqlDao.sqlCommand("delete from gl_pending_entry_t");

        if (createPles) insertPendingLedgerEntries(accountNumber, sfObjCd);

        unitTestSqlDao.sqlCommand("delete from gl_sf_balances_t where univ_fiscal_yr = '2007' and fin_coa_cd = 'BL' and account_nbr = '" + accountNumber +"'");
        unitTestSqlDao.sqlCommand("insert into GL_SF_BALANCES_T (UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, FIN_OBJECT_CD, OBJ_ID, VER_NBR, ACCT_SF_CD, CURR_BDGT_BAL_AMT, ACCT_ACTL_XPND_AMT, ACCT_ENCUM_AMT, TIMESTAMP) values (2007, 'BL', '" + accountNumber +"', '" + sfObjCd + "', SYS_GUID(), 0, '" + sfType + "', " + budgetAmt + ", " + actualAmt + ", " + encAmt + ", null)");
        unitTestSqlDao.sqlCommand("update ca_account_t set ACCT_SF_CD = '" + sfType + "', ACCT_PND_SF_CD = 'Y' where FIN_COA_CD = 'BL' and ACCOUNT_NBR = '" + accountNumber +"'");

    }

    /**
     * This method...
     */
    private void insertPendingLedgerEntries(String accountNumber, String sfObjCd) {
        unitTestSqlDao.sqlCommand("delete from fp_doc_header_t where fdoc_nbr = '1'");
        unitTestSqlDao.sqlCommand("insert into FP_DOC_HEADER_T (FDOC_NBR, OBJ_ID, VER_NBR, FDOC_STATUS_CD, FDOC_DESC, FDOC_TOTAL_AMT, ORG_DOC_NBR, FDOC_IN_ERR_NBR, FDOC_TMPL_NBR, TEMP_DOC_FNL_DT) values ('1', sys_guid(), 1, 'A', 'test', 0, '', '', '', SYSDATE)");
        unitTestSqlDao.sqlCommand("insert into GL_PENDING_ENTRY_T (FS_ORIGIN_CD, FDOC_NBR, TRN_ENTR_SEQ_NBR, OBJ_ID, VER_NBR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FIN_BALANCE_TYP_CD, FIN_OBJ_TYP_CD, UNIV_FISCAL_YR, UNIV_FISCAL_PRD_CD, TRN_LDGR_ENTR_DESC, TRN_LDGR_ENTR_AMT, TRN_DEBIT_CRDT_CD, TRANSACTION_DT, FDOC_TYP_CD, ORG_DOC_NBR, PROJECT_CD, ORG_REFERENCE_ID, FDOC_REF_TYP_CD, FS_REF_ORIGIN_CD, FDOC_REF_NBR, FDOC_REVERSAL_DT, TRN_ENCUM_UPDT_CD, FDOC_APPROVED_CD, ACCT_SF_FINOBJ_CD, TRN_ENTR_OFST_CD, TRNENTR_PROCESS_TM, BDGT_YR) values ('01','1',1,sys_guid(),2,'BL','" + accountNumber + "','-----','5000','---','AC','EX',2007,null,               'test',500,'C',sysdate,'DI',null,'----------',null,'','','',null,'','N','" + sfObjCd + "','N',sysdate,null)");
        unitTestSqlDao.sqlCommand("insert into GL_PENDING_ENTRY_T (FS_ORIGIN_CD, FDOC_NBR, TRN_ENTR_SEQ_NBR, OBJ_ID, VER_NBR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FIN_BALANCE_TYP_CD, FIN_OBJ_TYP_CD, UNIV_FISCAL_YR, UNIV_FISCAL_PRD_CD, TRN_LDGR_ENTR_DESC, TRN_LDGR_ENTR_AMT, TRN_DEBIT_CRDT_CD, TRANSACTION_DT, FDOC_TYP_CD, ORG_DOC_NBR, PROJECT_CD, ORG_REFERENCE_ID, FDOC_REF_TYP_CD, FS_REF_ORIGIN_CD, FDOC_REF_NBR, FDOC_REVERSAL_DT, TRN_ENCUM_UPDT_CD, FDOC_APPROVED_CD, ACCT_SF_FINOBJ_CD, TRN_ENTR_OFST_CD, TRNENTR_PROCESS_TM, BDGT_YR) values ('01','1',3,sys_guid(),2,'BL','4631638','-----','5000','---','AC','EX',2007,null,               'test',500,'D',sysdate,'DI',null,'----------',null,'','','',null,'','N','N/A' ,'N',sysdate,null)");
        unitTestSqlDao.sqlCommand("insert into GL_PENDING_ENTRY_T (FS_ORIGIN_CD, FDOC_NBR, TRN_ENTR_SEQ_NBR, OBJ_ID, VER_NBR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FIN_BALANCE_TYP_CD, FIN_OBJ_TYP_CD, UNIV_FISCAL_YR, UNIV_FISCAL_PRD_CD, TRN_LDGR_ENTR_DESC, TRN_LDGR_ENTR_AMT, TRN_DEBIT_CRDT_CD, TRANSACTION_DT, FDOC_TYP_CD, ORG_DOC_NBR, PROJECT_CD, ORG_REFERENCE_ID, FDOC_REF_TYP_CD, FS_REF_ORIGIN_CD, FDOC_REF_NBR, FDOC_REVERSAL_DT, TRN_ENCUM_UPDT_CD, FDOC_APPROVED_CD, ACCT_SF_FINOBJ_CD, TRN_ENTR_OFST_CD, TRNENTR_PROCESS_TM, BDGT_YR) values ('01','1',2,sys_guid(),2,'BL','" + accountNumber + "','-----','8000','---','AC','AS',2007,null,'TP Generated Offset',500,'D',sysdate,'DI',null,'----------',null,'','','',null,'','N','ASST','Y',sysdate,null)");
        unitTestSqlDao.sqlCommand("insert into GL_PENDING_ENTRY_T (FS_ORIGIN_CD, FDOC_NBR, TRN_ENTR_SEQ_NBR, OBJ_ID, VER_NBR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FIN_BALANCE_TYP_CD, FIN_OBJ_TYP_CD, UNIV_FISCAL_YR, UNIV_FISCAL_PRD_CD, TRN_LDGR_ENTR_DESC, TRN_LDGR_ENTR_AMT, TRN_DEBIT_CRDT_CD, TRANSACTION_DT, FDOC_TYP_CD, ORG_DOC_NBR, PROJECT_CD, ORG_REFERENCE_ID, FDOC_REF_TYP_CD, FS_REF_ORIGIN_CD, FDOC_REF_NBR, FDOC_REVERSAL_DT, TRN_ENCUM_UPDT_CD, FDOC_APPROVED_CD, ACCT_SF_FINOBJ_CD, TRN_ENTR_OFST_CD, TRNENTR_PROCESS_TM, BDGT_YR) values ('01','1',4,sys_guid(),2,'BL','4631638','-----','8000','---','AC','AS',2007,null,'TP Generated Offset',500,'C',sysdate,'DI',null,'----------',null,'','','',null,'','N','N/A' ,'Y',sysdate,null)");
    }


    public void testSufficientFunds_ConsolidationSufficientFunds() throws Exception {

        prepareSufficientFundsData("0211101","C", "GENX", 1000, 300, 100, false);

        String[] stringInput = new String[] {
                "2007BL0211101-----5000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                  500.00D2006-01-05          ----------                                                                  ",
                "2007BL4631638-----5000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                  500.00C2006-01-05          ----------                                                                  " };


        List transactions = new ArrayList();

        // Add inputs to expected output ...
        for (int i = 0; i < stringInput.length; i++) {
            OriginEntry oe = new OriginEntry(stringInput[i]);
            transactions.add(oe);
        }

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(0, insufficientFunds.size());

    }

    public void testSufficientFunds_ConsolidationNegativeBalanceCreditExpense() throws Exception {

        prepareSufficientFundsData("0211101","C", "GENX", 100, 300, 200, false);

        String[] stringInput = new String[] {
                "2007BL0211101-----5000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                  300.00C2006-01-05          ----------                                                                  ",
                "2007BL4631638-----5000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                  300.00D2006-01-05          ----------                                                                  " };


        List transactions = new ArrayList();

        // Add inputs to expected output ...
        for (int i = 0; i < stringInput.length; i++) {
            OriginEntry oe = new OriginEntry(stringInput[i]);
            transactions.add(oe);
        }

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(0, insufficientFunds.size());

    }

    public void testSufficientFunds_ConsolidationNegativeBalanceDebitExpense() throws Exception {

        prepareSufficientFundsData("0211101","C", "GENX", -1000, 300, 200, false);

        String[] stringInput = new String[] {
                "2007BL0211101-----5000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                  500.00D2006-01-05          ----------                                                                  ",
                "2007BL4631638-----5000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                  500.00C2006-01-05          ----------                                                                  " };


        List transactions = new ArrayList();

        // Add inputs to expected output ...
        for (int i = 0; i < stringInput.length; i++) {
            OriginEntry oe = new OriginEntry(stringInput[i]);
            transactions.add(oe);
        }

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(1, insufficientFunds.size());

    }


    public void testSufficientFunds_ConsolidationSameAccountPositiveBalanceNetZeroChange() throws Exception {

        prepareSufficientFundsData("0211101","C", "GENX", 1000, 300, 200, false);

        String[] stringInput = new String[] {
                "2007BL0211101-----5000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                 1500.00D2006-01-05          ----------                                                                  ",
                "2007BL0211101-----5000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                 1500.00C2006-01-05          ----------                                                                  " };


        List transactions = new ArrayList();

        // Add inputs to expected output ...
        for (int i = 0; i < stringInput.length; i++) {
            OriginEntry oe = new OriginEntry(stringInput[i]);
            transactions.add(oe);
        }

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(0, insufficientFunds.size());

    }

    public void testSufficientFunds_ConsolidationSameAccountNegativeBalanceNetZeroChange() throws Exception {

        prepareSufficientFundsData("0211101","C", "GENX", 100, 300, 200, false);

        String[] stringInput = new String[] {
                "2007BL0211101-----5000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                 1500.00D2006-01-05          ----------                                                                  ",
                "2007BL0211101-----5000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                 1500.00C2006-01-05          ----------                                                                  " };


        List transactions = new ArrayList();

        // Add inputs to expected output ...
        for (int i = 0; i < stringInput.length; i++) {
            OriginEntry oe = new OriginEntry(stringInput[i]);
            transactions.add(oe);
        }

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(0, insufficientFunds.size());

    }

    public void testSufficientFunds_ConsolidationInsufficientFunds() throws Exception {

        prepareSufficientFundsData("0211101","C", "GENX", 1000, 300, 200, false);

        String[] stringInput = new String[] {
                "2007BL0211101-----5000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                  500.01D2006-01-05          ----------                                                                  ",
                "2007BL4631638-----5000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                  500.01C2006-01-05          ----------                                                                  " };


        List transactions = new ArrayList();

        // Add inputs to expected output ...
        for (int i = 0; i < stringInput.length; i++) {
            OriginEntry oe = new OriginEntry(stringInput[i]);
            transactions.add(oe);
        }

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(1, insufficientFunds.size());

    }

    public void testSufficientFunds_ConsolidationPendingLedgerEntriesSufficientFunds() throws Exception {

        prepareSufficientFundsData("0211101","C", "GENX", 1000, 100, 100, true);

        String[] stringInput = new String[] {
                "2007BL0211101-----5000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                 1300.00D2006-01-05          ----------                                                                  ",
                "2007BL4631638-----5000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                 1300.00C2006-01-05          ----------                                                                  " };


        List transactions = new ArrayList();

        // Add inputs to expected output ...
        for (int i = 0; i < stringInput.length; i++) {
            OriginEntry oe = new OriginEntry(stringInput[i]);
            transactions.add(oe);
        }

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(0, insufficientFunds.size());

    }

    public void testSufficientFunds_ConsolidationPendingLedgerEntriesInsufficientFunds() throws Exception {

        prepareSufficientFundsData("0211101","C", "GENX", 1000, 100, 100, true);

        String[] stringInput = new String[] {
                "2007BL0211101-----5000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                 1300.01D2006-01-05          ----------                                                                  ",
                "2007BL4631638-----5000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                 1300.01C2006-01-05          ----------                                                                  " };


        List transactions = new ArrayList();

        // Add inputs to expected output ...
        for (int i = 0; i < stringInput.length; i++) {
            OriginEntry oe = new OriginEntry(stringInput[i]);
            transactions.add(oe);
        }

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(1, insufficientFunds.size());

    }

    public void testSufficientFunds_CashSufficientFunds() throws Exception {

        prepareSufficientFundsData("0211301","H", "    ", 1000, 300, 200, false);

        String[] stringInput = new String[] {
                "2007BL0211301-----8000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                  500.00C2006-01-05          ----------                                                                  ",
                "2007BL4631638-----8000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                  500.00D2006-01-05          ----------                                                                  " };


        List transactions = new ArrayList();

        // Add inputs to expected output ...
        for (int i = 0; i < stringInput.length; i++) {
            OriginEntry oe = new OriginEntry(stringInput[i]);
            transactions.add(oe);
        }

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(0, insufficientFunds.size());

    }

    public void testSufficientFunds_CashNegativeBalanceCreditExpense() throws Exception {

        prepareSufficientFundsData("0211301","H", "    ", 100, 300, 200, false);

        String[] stringInput = new String[] {
                "2007BL0211301-----8000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                  300.00D2006-01-05          ----------                                                                  ",
                "2007BL4631638-----8000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                  300.00C2006-01-05          ----------                                                                  " };


        List transactions = new ArrayList();

        // Add inputs to expected output ...
        for (int i = 0; i < stringInput.length; i++) {
            OriginEntry oe = new OriginEntry(stringInput[i]);
            transactions.add(oe);
        }

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(0, insufficientFunds.size());

    }

    public void testSufficientFunds_CashNegativeBalanceDebitExpense() throws Exception {

        prepareSufficientFundsData("0211301","H", "    ", -1000, 300, 200, false);

        String[] stringInput = new String[] {
                "2007BL0211301-----8000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                  500.00C2006-01-05          ----------                                                                  ",
                "2007BL4631638-----8000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                  500.00D2006-01-05          ----------                                                                  " };


        List transactions = new ArrayList();

        // Add inputs to expected output ...
        for (int i = 0; i < stringInput.length; i++) {
            OriginEntry oe = new OriginEntry(stringInput[i]);
            transactions.add(oe);
        }

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(1, insufficientFunds.size());

    }


    public void testSufficientFunds_CashSameAccountPositiveBalanceNetZeroChange() throws Exception {

        prepareSufficientFundsData("0211301", "H", "    ", 1000, 300, 200, false);

        String[] stringInput = new String[] {
                "2007BL0211301-----8000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                 1500.00C2006-01-05          ----------                                                                  ",
                "2007BL0211301-----8000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                 1500.00D2006-01-05          ----------                                                                  " };


        List transactions = new ArrayList();

        // Add inputs to expected output ...
        for (int i = 0; i < stringInput.length; i++) {
            OriginEntry oe = new OriginEntry(stringInput[i]);
            transactions.add(oe);
        }

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(0, insufficientFunds.size());

    }

    public void testSufficientFunds_CashSameAccountNegativeBalanceNetZeroChange() throws Exception {

        prepareSufficientFundsData("0211301","H", "    ", 100, 300, 200, false);

        String[] stringInput = new String[] {
                "2007BL0211301-----8000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                 1500.00C2006-01-05          ----------                                                                  ",
                "2007BL0211301-----8000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                 1500.00D2006-01-05          ----------                                                                  " };


        List transactions = new ArrayList();

        // Add inputs to expected output ...
        for (int i = 0; i < stringInput.length; i++) {
            OriginEntry oe = new OriginEntry(stringInput[i]);
            transactions.add(oe);
        }

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(0, insufficientFunds.size());

    }

    public void testSufficientFunds_CashInsufficientFunds() throws Exception {

        prepareSufficientFundsData("0211301","H", "    ", 1000, 0, 500, false);

        String[] stringInput = new String[] {
                "2007BL0211301-----8000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                  500.01C2006-01-05          ----------                                                                  ",
                "2007BL4631638-----8000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                  500.01D2006-01-05          ----------                                                                  " };


        List transactions = new ArrayList();

        // Add inputs to expected output ...
        for (int i = 0; i < stringInput.length; i++) {
            OriginEntry oe = new OriginEntry(stringInput[i]);
            transactions.add(oe);
        }

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(1, insufficientFunds.size());

    }

    public void testSufficientFunds_CashPendingLedgerEntriesSufficientFunds() throws Exception {

        prepareSufficientFundsData("0211301","H", "    ", 1000, 100, 100, true);

        String[] stringInput = new String[] {
                "2007BL0211301-----8000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                 1300.00C2006-01-05          ----------                                                                  ",
                "2007BL4631638-----8000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                 1300.00D2006-01-05          ----------                                                                  " };


        List transactions = new ArrayList();

        // Add inputs to expected output ...
        for (int i = 0; i < stringInput.length; i++) {
            OriginEntry oe = new OriginEntry(stringInput[i]);
            transactions.add(oe);
        }

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(0, insufficientFunds.size());

    }

    public void testSufficientFunds_CashPendingLedgerEntriesInsufficientFunds() throws Exception {

        prepareSufficientFundsData("0211301","H", "    ", 1000, 0, 200, true);

        String[] stringInput = new String[] {
                "2007BL0211301-----8000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                 1300.01C2006-01-05          ----------                                                                  ",
                "2007BL4631638-----8000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                 1300.01D2006-01-05          ----------                                                                  " };


        List transactions = new ArrayList();

        // Add inputs to expected output ...
        for (int i = 0; i < stringInput.length; i++) {
            OriginEntry oe = new OriginEntry(stringInput[i]);
            transactions.add(oe);
        }

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(1, insufficientFunds.size());

    }

    public void testSufficientFunds_LevelSufficientFunds() throws Exception {

        prepareSufficientFundsData("0211501","L", "S&E", 1000, 300, 100, false);

        String[] stringInput = new String[] {
                "2007BL0211501-----5000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                  500.00D2006-01-05          ----------                                                                  ",
                "2007BL4631638-----5000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                  500.00C2006-01-05          ----------                                                                  " };


        List transactions = new ArrayList();

        // Add inputs to expected output ...
        for (int i = 0; i < stringInput.length; i++) {
            OriginEntry oe = new OriginEntry(stringInput[i]);
            transactions.add(oe);
        }

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(0, insufficientFunds.size());

    }

    public void testSufficientFunds_LevelNegativeBalanceCreditExpense() throws Exception {

        prepareSufficientFundsData("0211501","L", "S&E", 100, 300, 200, false);

        String[] stringInput = new String[] {
                "2007BL0211501-----5000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                  300.00C2006-01-05          ----------                                                                  ",
                "2007BL4631638-----5000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                  300.00D2006-01-05          ----------                                                                  " };


        List transactions = new ArrayList();

        // Add inputs to expected output ...
        for (int i = 0; i < stringInput.length; i++) {
            OriginEntry oe = new OriginEntry(stringInput[i]);
            transactions.add(oe);
        }

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(0, insufficientFunds.size());

    }

    public void testSufficientFunds_LevelNegativeBalanceDebitExpense() throws Exception {

        prepareSufficientFundsData("0211501","L", "S&E", -1000, 300, 200, false);

        String[] stringInput = new String[] {
                "2007BL0211501-----5000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                  500.00D2006-01-05          ----------                                                                  ",
                "2007BL4631638-----5000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                  500.00C2006-01-05          ----------                                                                  " };


        List transactions = new ArrayList();

        // Add inputs to expected output ...
        for (int i = 0; i < stringInput.length; i++) {
            OriginEntry oe = new OriginEntry(stringInput[i]);
            transactions.add(oe);
        }

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(1, insufficientFunds.size());

    }


    public void testSufficientFunds_LevelSameAccountPositiveBalanceNetZeroChange() throws Exception {

        prepareSufficientFundsData("0211501","L", "S&E", 1000, 300, 200, false);

        String[] stringInput = new String[] {
                "2007BL0211501-----5000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                 1500.00D2006-01-05          ----------                                                                  ",
                "2007BL0211501-----5000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                 1500.00C2006-01-05          ----------                                                                  " };


        List transactions = new ArrayList();

        // Add inputs to expected output ...
        for (int i = 0; i < stringInput.length; i++) {
            OriginEntry oe = new OriginEntry(stringInput[i]);
            transactions.add(oe);
        }

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(0, insufficientFunds.size());

    }

    public void testSufficientFunds_LevelSameAccountNegativeBalanceNetZeroChange() throws Exception {

        prepareSufficientFundsData("0211501","L", "S&E", 100, 300, 200, false);

        String[] stringInput = new String[] {
                "2007BL0211501-----5000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                 1500.00D2006-01-05          ----------                                                                  ",
                "2007BL0211501-----5000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                 1500.00C2006-01-05          ----------                                                                  " };


        List transactions = new ArrayList();

        // Add inputs to expected output ...
        for (int i = 0; i < stringInput.length; i++) {
            OriginEntry oe = new OriginEntry(stringInput[i]);
            transactions.add(oe);
        }

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(0, insufficientFunds.size());

    }

    public void testSufficientFunds_LevelInsufficientFunds() throws Exception {

        prepareSufficientFundsData("0211501","L", "S&E", 1000, 300, 200, false);

        String[] stringInput = new String[] {
                "2007BL0211501-----5000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                  500.01D2006-01-05          ----------                                                                  ",
                "2007BL4631638-----5000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                  500.01C2006-01-05          ----------                                                                  " };


        List transactions = new ArrayList();

        // Add inputs to expected output ...
        for (int i = 0; i < stringInput.length; i++) {
            OriginEntry oe = new OriginEntry(stringInput[i]);
            transactions.add(oe);
        }

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(1, insufficientFunds.size());

    }

    public void testSufficientFunds_LevelPendingLedgerEntriesSufficientFunds() throws Exception {

        prepareSufficientFundsData("0211501","L", "S&E", 1000, 100, 100, true);

        String[] stringInput = new String[] {
                "2007BL0211501-----5000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                 1300.00D2006-01-05          ----------                                                                  ",
                "2007BL4631638-----5000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                 1300.00C2006-01-05          ----------                                                                  " };


        List transactions = new ArrayList();

        // Add inputs to expected output ...
        for (int i = 0; i < stringInput.length; i++) {
            OriginEntry oe = new OriginEntry(stringInput[i]);
            transactions.add(oe);
        }

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(0, insufficientFunds.size());

    }

    public void testSufficientFunds_LevelPendingLedgerEntriesInsufficientFunds() throws Exception {

        prepareSufficientFundsData("0211501","L", "S&E", 1000, 100, 100, true);

        String[] stringInput = new String[] {
                "2007BL0211501-----5000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                 1300.01D2006-01-05          ----------                                                                  ",
                "2007BL4631638-----5000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                 1300.01C2006-01-05          ----------                                                                  " };


        List transactions = new ArrayList();

        // Add inputs to expected output ...
        for (int i = 0; i < stringInput.length; i++) {
            OriginEntry oe = new OriginEntry(stringInput[i]);
            transactions.add(oe);
        }

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(1, insufficientFunds.size());

    }

    public void testSufficientFunds_ObjectSufficientFunds() throws Exception {

        prepareSufficientFundsData("0211701","O", "5000", 1000, 300, 100, false);

        String[] stringInput = new String[] {
                "2007BL0211701-----5000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                  500.00D2006-01-05          ----------                                                                  ",
                "2007BL4631638-----5000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                  500.00C2006-01-05          ----------                                                                  " };


        List transactions = new ArrayList();

        // Add inputs to expected output ...
        for (int i = 0; i < stringInput.length; i++) {
            OriginEntry oe = new OriginEntry(stringInput[i]);
            transactions.add(oe);
        }

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(0, insufficientFunds.size());

    }

    public void testSufficientFunds_ObjectNegativeBalanceCreditExpense() throws Exception {

        prepareSufficientFundsData("0211701","O", "5000", 100, 300, 200, false);

        String[] stringInput = new String[] {
                "2007BL0211701-----5000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                  300.00C2006-01-05          ----------                                                                  ",
                "2007BL4631638-----5000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                  300.00D2006-01-05          ----------                                                                  " };


        List transactions = new ArrayList();

        // Add inputs to expected output ...
        for (int i = 0; i < stringInput.length; i++) {
            OriginEntry oe = new OriginEntry(stringInput[i]);
            transactions.add(oe);
        }

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(0, insufficientFunds.size());

    }

    public void testSufficientFunds_ObjectNegativeBalanceDebitExpense() throws Exception {

        prepareSufficientFundsData("0211701","O", "5000", -1000, 300, 200, false);

        String[] stringInput = new String[] {
                "2007BL0211701-----5000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                  500.00D2006-01-05          ----------                                                                  ",
                "2007BL4631638-----5000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                  500.00C2006-01-05          ----------                                                                  " };


        List transactions = new ArrayList();

        // Add inputs to expected output ...
        for (int i = 0; i < stringInput.length; i++) {
            OriginEntry oe = new OriginEntry(stringInput[i]);
            transactions.add(oe);
        }

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(1, insufficientFunds.size());

    }


    public void testSufficientFunds_ObjectSameAccountPositiveBalanceNetZeroChange() throws Exception {

        prepareSufficientFundsData("0211701","O", "5000", 1000, 300, 200, false);

        String[] stringInput = new String[] {
                "2007BL0211701-----5000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                 1500.00D2006-01-05          ----------                                                                  ",
                "2007BL0211701-----5000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                 1500.00C2006-01-05          ----------                                                                  " };


        List transactions = new ArrayList();

        // Add inputs to expected output ...
        for (int i = 0; i < stringInput.length; i++) {
            OriginEntry oe = new OriginEntry(stringInput[i]);
            transactions.add(oe);
        }

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(0, insufficientFunds.size());

    }

    public void testSufficientFunds_ObjectSameAccountNegativeBalanceNetZeroChange() throws Exception {

        prepareSufficientFundsData("0211701","O", "5000", 100, 300, 200, false);

        String[] stringInput = new String[] {
                "2007BL0211701-----5000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                 1500.00D2006-01-05          ----------                                                                  ",
                "2007BL0211701-----5000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                 1500.00C2006-01-05          ----------                                                                  " };


        List transactions = new ArrayList();

        // Add inputs to expected output ...
        for (int i = 0; i < stringInput.length; i++) {
            OriginEntry oe = new OriginEntry(stringInput[i]);
            transactions.add(oe);
        }

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(0, insufficientFunds.size());

    }

    public void testSufficientFunds_ObjectInsufficientFunds() throws Exception {

        prepareSufficientFundsData("0211701","O", "5000", 1000, 300, 200, false);

        String[] stringInput = new String[] {
                "2007BL0211701-----5000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                  500.01D2006-01-05          ----------                                                                  ",
                "2007BL4631638-----5000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                  500.01C2006-01-05          ----------                                                                  " };


        List transactions = new ArrayList();

        // Add inputs to expected output ...
        for (int i = 0; i < stringInput.length; i++) {
            OriginEntry oe = new OriginEntry(stringInput[i]);
            transactions.add(oe);
        }

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(1, insufficientFunds.size());

    }

    public void testSufficientFunds_ObjectPendingLedgerEntriesSufficientFunds() throws Exception {

        prepareSufficientFundsData("0211701","O", "5000", 1000, 100, 100, true);

        String[] stringInput = new String[] {
                "2007BL0211701-----5000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                 1300.00D2006-01-05          ----------                                                                  ",
                "2007BL4631638-----5000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                 1300.00C2006-01-05          ----------                                                                  " };


        List transactions = new ArrayList();

        // Add inputs to expected output ...
        for (int i = 0; i < stringInput.length; i++) {
            OriginEntry oe = new OriginEntry(stringInput[i]);
            transactions.add(oe);
        }

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(0, insufficientFunds.size());

    }

    public void testSufficientFunds_ObjectPendingLedgerEntriesInsufficientFunds() throws Exception {

        prepareSufficientFundsData("0211701","O", "5000", 1000, 100, 100, true);

        String[] stringInput = new String[] {
                "2007BL0211701-----5000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                 1300.01D2006-01-05          ----------                                                                  ",
                "2007BL4631638-----5000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                 1300.01C2006-01-05          ----------                                                                  " };


        List transactions = new ArrayList();

        // Add inputs to expected output ...
        for (int i = 0; i < stringInput.length; i++) {
            OriginEntry oe = new OriginEntry(stringInput[i]);
            transactions.add(oe);
        }

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(1, insufficientFunds.size());

    }


    public void testSufficientFunds_AccountSufficientFunds() throws Exception {

        prepareSufficientFundsData("0211901","A", "    ", 1000, 300, 100, false);

        String[] stringInput = new String[] {
                "2007BL0211901-----5000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                  500.00D2006-01-05          ----------                                                                  ",
                "2007BL4631638-----5000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                  500.00C2006-01-05          ----------                                                                  " };


        List transactions = new ArrayList();

        // Add inputs to expected output ...
        for (int i = 0; i < stringInput.length; i++) {
            OriginEntry oe = new OriginEntry(stringInput[i]);
            transactions.add(oe);
        }

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(0, insufficientFunds.size());

    }

    public void testSufficientFunds_AccountNegativeBalanceCreditExpense() throws Exception {

        prepareSufficientFundsData("0211901","A", "    ", 100, 300, 200, false);

        String[] stringInput = new String[] {
                "2007BL0211901-----5000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                  300.00C2006-01-05          ----------                                                                  ",
                "2007BL4631638-----5000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                  300.00D2006-01-05          ----------                                                                  " };


        List transactions = new ArrayList();

        // Add inputs to expected output ...
        for (int i = 0; i < stringInput.length; i++) {
            OriginEntry oe = new OriginEntry(stringInput[i]);
            transactions.add(oe);
        }

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(0, insufficientFunds.size());

    }

    public void testSufficientFunds_AccountNegativeBalanceDebitExpense() throws Exception {

        prepareSufficientFundsData("0211901","A", "    ", -1000, 300, 200, false);

        String[] stringInput = new String[] {
                "2007BL0211901-----5000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                  500.00D2006-01-05          ----------                                                                  ",
                "2007BL4631638-----5000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                  500.00C2006-01-05          ----------                                                                  " };


        List transactions = new ArrayList();

        // Add inputs to expected output ...
        for (int i = 0; i < stringInput.length; i++) {
            OriginEntry oe = new OriginEntry(stringInput[i]);
            transactions.add(oe);
        }

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(1, insufficientFunds.size());

    }


    public void testSufficientFunds_AccountSameAccountPositiveBalanceNetZeroChange() throws Exception {

        prepareSufficientFundsData("0211901","A", "    ", 1000, 300, 200, false);

        String[] stringInput = new String[] {
                "2007BL0211901-----5000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                 1500.00D2006-01-05          ----------                                                                  ",
                "2007BL0211901-----5000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                 1500.00C2006-01-05          ----------                                                                  " };


        List transactions = new ArrayList();

        // Add inputs to expected output ...
        for (int i = 0; i < stringInput.length; i++) {
            OriginEntry oe = new OriginEntry(stringInput[i]);
            transactions.add(oe);
        }

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(0, insufficientFunds.size());

    }

    public void testSufficientFunds_AccountSameAccountNegativeBalanceNetZeroChange() throws Exception {

        prepareSufficientFundsData("0211901","A", "    ", 100, 300, 200, false);

        String[] stringInput = new String[] {
                "2007BL0211901-----5000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                 1500.00D2006-01-05          ----------                                                                  ",
                "2007BL0211901-----5000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                 1500.00C2006-01-05          ----------                                                                  " };


        List transactions = new ArrayList();

        // Add inputs to expected output ...
        for (int i = 0; i < stringInput.length; i++) {
            OriginEntry oe = new OriginEntry(stringInput[i]);
            transactions.add(oe);
        }

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(0, insufficientFunds.size());

    }

    public void testSufficientFunds_AccountInsufficientFunds() throws Exception {

        prepareSufficientFundsData("0211901","A", "    ", 1000, 300, 200, false);

        String[] stringInput = new String[] {
                "2007BL0211901-----5000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                  500.01D2006-01-05          ----------                                                                  ",
                "2007BL4631638-----5000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                  500.01C2006-01-05          ----------                                                                  " };


        List transactions = new ArrayList();

        // Add inputs to expected output ...
        for (int i = 0; i < stringInput.length; i++) {
            OriginEntry oe = new OriginEntry(stringInput[i]);
            transactions.add(oe);
        }

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(1, insufficientFunds.size());

    }

    public void testSufficientFunds_AccountPendingLedgerEntriesSufficientFunds() throws Exception {

        prepareSufficientFundsData("0211901","A", "    ", 1000, 100, 100, true);

        String[] stringInput = new String[] {
                "2007BL0211901-----5000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                 1300.00D2006-01-05          ----------                                                                  ",
                "2007BL4631638-----5000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                 1300.00C2006-01-05          ----------                                                                  " };


        List transactions = new ArrayList();

        // Add inputs to expected output ...
        for (int i = 0; i < stringInput.length; i++) {
            OriginEntry oe = new OriginEntry(stringInput[i]);
            transactions.add(oe);
        }

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(0, insufficientFunds.size());

    }

    public void testSufficientFunds_AccountPendingLedgerEntriesInsufficientFunds() throws Exception {

        prepareSufficientFundsData("0211901","A", "    ", 1000, 100, 100, true);

        String[] stringInput = new String[] {
                "2007BL0211901-----5000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                 1300.01D2006-01-05          ----------                                                                  ",
                "2007BL4631638-----5000---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                 1300.01C2006-01-05          ----------                                                                  " };


        List transactions = new ArrayList();

        // Add inputs to expected output ...
        for (int i = 0; i < stringInput.length; i++) {
            OriginEntry oe = new OriginEntry(stringInput[i]);
            transactions.add(oe);
        }

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(1, insufficientFunds.size());

    }
}
