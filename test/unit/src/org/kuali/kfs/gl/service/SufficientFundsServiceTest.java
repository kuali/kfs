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

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.Message;
import org.kuali.kfs.sys.businessobject.SufficientFundsItem;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.kfs.sys.dataaccess.UnitTestSqlDao;
import org.kuali.rice.core.api.datetime.DateTimeService;

/**
 * Tests the sufficient funds service
 * @see org.kuali.kfs.gl.service.SufficientFundsService
 */
@ConfigureContext
public class SufficientFundsServiceTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SufficientFundsServiceTest.class);

    private SufficientFundsService sufficientFundsService = null;
    private UnitTestSqlDao unitTestSqlDao = null;
    private DateTimeService dateTimeService;

    /**
     * Initializes the services needed by this test
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        sufficientFundsService = SpringContext.getBean(SufficientFundsService.class);
        unitTestSqlDao = SpringContext.getBean(UnitTestSqlDao.class);
        dateTimeService = SpringContext.getBean(DateTimeService.class);
    }

    /**
     * Puts test sufficient funds balances into the database
     *
     * @param accountNumber the account number for sf balances to use
     * @param sfType the type of the sf balance
     * @param sfObjCd the object code of the sf balance
     * @param budgetAmt the budget amount of the sf balance
     * @param actualAmt the actual amount of the sf balance
     * @param encAmt the encumbrance amount of the sf balance
     * @param createPles true if pending ledger entries should also be created, false otherwise
     */
    private void prepareSufficientFundsData(String accountNumber, String sfType, String sfObjCd, Integer budgetAmt, Integer actualAmt, Integer encAmt, boolean createPles) {
        unitTestSqlDao.sqlCommand("delete from GL_PENDING_ENTRY_T");

        if (createPles)
            insertPendingLedgerEntries(accountNumber, sfObjCd);


        final Integer currentFiscalYear = TestUtils.getFiscalYearForTesting();
        unitTestSqlDao.sqlCommand("delete from GL_SF_BALANCES_T where univ_fiscal_yr = '" + currentFiscalYear + "' and fin_coa_cd = 'BL' and account_nbr = '" + accountNumber + "'");
        unitTestSqlDao.sqlCommand("delete from GL_SF_BALANCES_T where univ_fiscal_yr = '"+ (currentFiscalYear-1) +"' and fin_coa_cd = 'BL' and account_nbr = '" + accountNumber + "'");
        unitTestSqlDao.sqlCommand("insert into GL_SF_BALANCES_T (UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, FIN_OBJECT_CD, ACCT_SF_CD, CURR_BDGT_BAL_AMT, ACCT_ACTL_XPND_AMT, ACCT_ENCUM_AMT, TIMESTAMP) values ("+currentFiscalYear+", 'BL', '" + accountNumber + "', '" + sfObjCd + "', '" + sfType + "', " + budgetAmt + ", " + actualAmt + ", " + encAmt + ", null)");
        unitTestSqlDao.sqlCommand("update CA_ACCOUNT_T set ACCT_SF_CD = '" + sfType + "', ACCT_PND_SF_CD = 'Y' where FIN_COA_CD = 'BL' and ACCOUNT_NBR = '" + accountNumber + "'");

    }

    /**
     * Inserts pending ledger entries into the database
     *
     * @param accountNumber the account number of pending entries to save
     * @param sfObjCd the object code of pending entries to save
     */
    private void insertPendingLedgerEntries(String accountNumber, String sfObjCd) {
        String documentHeaderId = "1";
        final String currentFiscalYear = TestUtils.getFiscalYearForTesting().toString();
        unitTestSqlDao.sqlCommand("delete from KRNS_DOC_HDR_T where doc_hdr_id = '" + documentHeaderId + "'");
        unitTestSqlDao.sqlCommand("delete from FS_DOC_HEADER_T where fdoc_nbr = '" + documentHeaderId + "'");
        unitTestSqlDao.sqlCommand("insert into KRNS_DOC_HDR_T (DOC_HDR_ID, OBJ_ID, VER_NBR, FDOC_DESC, ORG_DOC_HDR_ID, TMPL_DOC_HDR_ID) values ('" + documentHeaderId + "','" + java.util.UUID.randomUUID().toString() + "', 1, 'test', '', '')");
        unitTestSqlDao.sqlCommand("insert into FS_DOC_HEADER_T (FDOC_NBR, FDOC_STATUS_CD, FDOC_TOTAL_AMT, FDOC_IN_ERR_NBR, TEMP_DOC_FNL_DT) values ('" + documentHeaderId + "', 'A', 0, '', " + unitTestSqlDao.getDbPlatform().getCurTimeFunction() + ")");
        unitTestSqlDao.sqlCommand("insert into GL_PENDING_ENTRY_T (FS_ORIGIN_CD, FDOC_NBR, TRN_ENTR_SEQ_NBR, OBJ_ID, VER_NBR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FIN_BALANCE_TYP_CD, FIN_OBJ_TYP_CD, UNIV_FISCAL_YR, UNIV_FISCAL_PRD_CD, TRN_LDGR_ENTR_DESC, TRN_LDGR_ENTR_AMT, TRN_DEBIT_CRDT_CD, TRANSACTION_DT, FDOC_TYP_CD, ORG_DOC_NBR, PROJECT_CD, ORG_REFERENCE_ID, FDOC_REF_TYP_CD, FS_REF_ORIGIN_CD, FDOC_REF_NBR, FDOC_REVERSAL_DT, TRN_ENCUM_UPDT_CD, FDOC_APPROVED_CD, ACCT_SF_FINOBJ_CD, TRN_ENTR_OFST_CD, TRNENTR_PROCESS_TM) values ('01','" + documentHeaderId + "',1,'" + java.util.UUID.randomUUID().toString() + "',2,'BL','" + accountNumber + "','-----','5000','---','AC','EX',"+currentFiscalYear+",null,               'test',500,'C'," + unitTestSqlDao.getDbPlatform().getCurTimeFunction() + ",'DI',null,'----------',null,'','','',null,'','N','" + sfObjCd + "','N'," + unitTestSqlDao.getDbPlatform().getCurTimeFunction() + ")");
        unitTestSqlDao.sqlCommand("insert into GL_PENDING_ENTRY_T (FS_ORIGIN_CD, FDOC_NBR, TRN_ENTR_SEQ_NBR, OBJ_ID, VER_NBR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FIN_BALANCE_TYP_CD, FIN_OBJ_TYP_CD, UNIV_FISCAL_YR, UNIV_FISCAL_PRD_CD, TRN_LDGR_ENTR_DESC, TRN_LDGR_ENTR_AMT, TRN_DEBIT_CRDT_CD, TRANSACTION_DT, FDOC_TYP_CD, ORG_DOC_NBR, PROJECT_CD, ORG_REFERENCE_ID, FDOC_REF_TYP_CD, FS_REF_ORIGIN_CD, FDOC_REF_NBR, FDOC_REVERSAL_DT, TRN_ENCUM_UPDT_CD, FDOC_APPROVED_CD, ACCT_SF_FINOBJ_CD, TRN_ENTR_OFST_CD, TRNENTR_PROCESS_TM) values ('01','" + documentHeaderId + "',3,'" + java.util.UUID.randomUUID().toString() + "',2,'BL','4631638','-----','5000','---','AC','EX',"+currentFiscalYear+",null,               'test',500,'D'," + unitTestSqlDao.getDbPlatform().getCurTimeFunction() + ",'DI',null,'----------',null,'','','',null,'','N','N/A' ,'N'," + unitTestSqlDao.getDbPlatform().getCurTimeFunction() + ")");
        unitTestSqlDao.sqlCommand("insert into GL_PENDING_ENTRY_T (FS_ORIGIN_CD, FDOC_NBR, TRN_ENTR_SEQ_NBR, OBJ_ID, VER_NBR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FIN_BALANCE_TYP_CD, FIN_OBJ_TYP_CD, UNIV_FISCAL_YR, UNIV_FISCAL_PRD_CD, TRN_LDGR_ENTR_DESC, TRN_LDGR_ENTR_AMT, TRN_DEBIT_CRDT_CD, TRANSACTION_DT, FDOC_TYP_CD, ORG_DOC_NBR, PROJECT_CD, ORG_REFERENCE_ID, FDOC_REF_TYP_CD, FS_REF_ORIGIN_CD, FDOC_REF_NBR, FDOC_REVERSAL_DT, TRN_ENCUM_UPDT_CD, FDOC_APPROVED_CD, ACCT_SF_FINOBJ_CD, TRN_ENTR_OFST_CD, TRNENTR_PROCESS_TM) values ('01','" + documentHeaderId + "',2,'" + java.util.UUID.randomUUID().toString() + "',2,'BL','" + accountNumber + "','-----','8000','---','AC','AS',"+currentFiscalYear+",null,'TP Generated Offset',500,'D'," + unitTestSqlDao.getDbPlatform().getCurTimeFunction() + ",'DI',null,'----------',null,'','','',null,'','N','ASST','Y'," + unitTestSqlDao.getDbPlatform().getCurTimeFunction() + ")");
        unitTestSqlDao.sqlCommand("insert into GL_PENDING_ENTRY_T (FS_ORIGIN_CD, FDOC_NBR, TRN_ENTR_SEQ_NBR, OBJ_ID, VER_NBR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FIN_BALANCE_TYP_CD, FIN_OBJ_TYP_CD, UNIV_FISCAL_YR, UNIV_FISCAL_PRD_CD, TRN_LDGR_ENTR_DESC, TRN_LDGR_ENTR_AMT, TRN_DEBIT_CRDT_CD, TRANSACTION_DT, FDOC_TYP_CD, ORG_DOC_NBR, PROJECT_CD, ORG_REFERENCE_ID, FDOC_REF_TYP_CD, FS_REF_ORIGIN_CD, FDOC_REF_NBR, FDOC_REVERSAL_DT, TRN_ENCUM_UPDT_CD, FDOC_APPROVED_CD, ACCT_SF_FINOBJ_CD, TRN_ENTR_OFST_CD, TRNENTR_PROCESS_TM) values ('01','" + documentHeaderId + "',4,'" + java.util.UUID.randomUUID().toString() + "',2,'BL','4631638','-----','8000','---','AC','AS',"+currentFiscalYear+",null,'TP Generated Offset',500,'C'," + unitTestSqlDao.getDbPlatform().getCurTimeFunction() + ",'DI',null,'----------',null,'','','',null,'','N','N/A' ,'Y'," + unitTestSqlDao.getDbPlatform().getCurTimeFunction() + ")");
    }

    /**
     * Converts the given Strings into a List of OriginEntryFull objects, printing out error messages as they occur
     * @param stringInput the String input to convert to OriginEntryFull records
     * @return a List of OriginEntryFull records
     */
    protected List<OriginEntryFull> convertStringInputsToOriginEntries(String[] stringInput) {
        List<OriginEntryFull> transactions = new ArrayList<OriginEntryFull>();

        // Add inputs to expected output ...
        for (int i = 0; i < stringInput.length; i++) {
            OriginEntryFull oe = new OriginEntryFull();
            List<Message> messages = oe.setFromTextFileForBatch(stringInput[i], i);
            for (Message message: messages) {
                LOG.warn(message);
            }
            transactions.add(oe);
        }
        return transactions;
    }


    /**
     * Tests the basic consolidation sufficient funds checking
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testSufficientFunds_ConsolidationSufficientFunds() throws Exception {

        final String currentFiscalYear = TestUtils.getFiscalYearForTesting().toString();
        prepareSufficientFundsData("0211101", "C", "GENX", 1000, 300, 100, false);

        String[] stringInput = new String[] { currentFiscalYear+"BL0211101-----5000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                         500.00D2006-01-05          ----------                                                                            ", currentFiscalYear+"BL4631638-----5000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                         500.00C2006-01-05          ----------                                                                            " };


        List transactions = convertStringInputsToOriginEntries(stringInput);

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(0, insufficientFunds.size());

    }

    /**
     * Tests consolidation sufficient funds checking on a negative sufficient funds balance and a credit expense transaction
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testSufficientFunds_ConsolidationNegativeBalanceCreditExpense() throws Exception {

        prepareSufficientFundsData("0211101", "C", "GENX", 100, 300, 200, false);

        final String currentFiscalYear = TestUtils.getFiscalYearForTesting().toString();
        String[] stringInput = new String[] { currentFiscalYear+"BL0211101-----5000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                      300.00C2006-01-05          ----------                                                                            ", currentFiscalYear+"BL4631638-----5000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                      300.00D2006-01-05          ----------                                                                            " };


        List transactions = convertStringInputsToOriginEntries(stringInput);

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(0, insufficientFunds.size());

    }

    /**
     * Tests consolidation sufficient funds checking on a negative sufficient funds balance and a debit expense transaction
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testSufficientFunds_ConsolidationNegativeBalanceDebitExpense() throws Exception {

        prepareSufficientFundsData("0211101", "C", "GENX", -1000, 300, 200, false);

        final String currentFiscalYear = TestUtils.getFiscalYearForTesting().toString();
        String[] stringInput = new String[] {
                currentFiscalYear+"BL0211101-----5000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                      500.00D2006-01-05          ----------                                                                            ",
                currentFiscalYear+"BL4631638-----5000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                      500.00C2006-01-05          ----------                                                                            "
        };

        List transactions = convertStringInputsToOriginEntries(stringInput);

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(1, insufficientFunds.size());

    }

    /**
     * Tests consolidation sufficient funds checking on a positive sufficient funds balance and two transactions that will cancel each other out
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testSufficientFunds_ConsolidationSameAccountPositiveBalanceNetZeroChange() throws Exception {

        prepareSufficientFundsData("0211101", "C", "GENX", 1000, 300, 200, false);

        final String currentFiscalYear = TestUtils.getFiscalYearForTesting().toString();
        String[] stringInput = new String[] { currentFiscalYear+"BL0211101-----5000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                     1500.00D2006-01-05          ----------                                                                            ", currentFiscalYear+"BL0211101-----5000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                     1500.00C2006-01-05          ----------                                                                            " };


        List transactions = convertStringInputsToOriginEntries(stringInput);

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(0, insufficientFunds.size());

    }

    /**
     * Tests consolidation sufficient funds checking on a negative sufficient funds balance and two transactions that will cancel each other out
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testSufficientFunds_ConsolidationSameAccountNegativeBalanceNetZeroChange() throws Exception {

        prepareSufficientFundsData("0211101", "C", "GENX", 100, 300, 200, false);

        final String currentFiscalYear = TestUtils.getFiscalYearForTesting().toString();
        String[] stringInput = new String[] { currentFiscalYear+"BL0211101-----5000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                     1500.00D2006-01-05          ----------                                                                            ", currentFiscalYear+"BL0211101-----5000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                     1500.00C2006-01-05          ----------                                                                            " };


        List transactions = convertStringInputsToOriginEntries(stringInput);

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(0, insufficientFunds.size());

    }

    /**
     * Tests consolidation sufficient funds checking on a sufficient funds balance that do not have sufficient funds for a transaction
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testSufficientFunds_ConsolidationInsufficientFunds() throws Exception {

        prepareSufficientFundsData("0211101", "C", "GENX", 1000, 300, 200, false);

        final String currentFiscalYear = TestUtils.getFiscalYearForTesting().toString();
        String[] stringInput = new String[] { currentFiscalYear+"BL0211101-----5000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                      500.01D2006-01-05          ----------                                                                            ", currentFiscalYear+"BL4631638-----5000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                      500.01C2006-01-05          ----------                                                                            " };


        List transactions = convertStringInputsToOriginEntries(stringInput);

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(1, insufficientFunds.size());

    }

    /**
     * Tests consolidation sufficient funds checking on a sufficient funds balance where pending entries will provide sufficient funds for a transaction
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testSufficientFunds_ConsolidationPendingLedgerEntriesSufficientFunds() throws Exception {

        prepareSufficientFundsData("0211101", "C", "GENX", 1000, 100, 100, true);

        final String currentFiscalYear = TestUtils.getFiscalYearForTesting().toString();
        String[] stringInput = new String[] { currentFiscalYear+"BL0211101-----5000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                     1300.00D2006-01-05          ----------                                                                       ", currentFiscalYear+"BL4631638-----5000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                     1300.00C2006-01-05          ----------                                                                       " };


        List transactions = convertStringInputsToOriginEntries(stringInput);

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(0, insufficientFunds.size());

    }

    /**
     * Tests consolidation sufficient funds checking on a sufficient funds balance where pending entries will not provide sufficient funds for a transaction
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testSufficientFunds_ConsolidationPendingLedgerEntriesInsufficientFunds() throws Exception {

        prepareSufficientFundsData("0211101", "C", "GENX", 1000, 100, 100, true);

        final String currentFiscalYear = TestUtils.getFiscalYearForTesting().toString();
        String[] stringInput = new String[] { currentFiscalYear+"BL0211101-----5000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                     1300.01D2006-01-05          ----------                                                                       ", currentFiscalYear+"BL4631638-----5000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                     1300.01C2006-01-05          ----------                                                                       " };


        List transactions = convertStringInputsToOriginEntries(stringInput);

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(1, insufficientFunds.size());

    }

    /**
     * Tests basic cash sufficient funds checking
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testSufficientFunds_CashSufficientFunds() throws Exception {

        prepareSufficientFundsData("0211301", "H", "    ", 1000, 300, 200, false);

        final String currentFiscalYear = TestUtils.getFiscalYearForTesting().toString();
        String[] stringInput = new String[] { currentFiscalYear+"BL0211301-----8000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                      500.00C2006-01-05          ----------                                                                       ", currentFiscalYear+"BL4631638-----8000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                      500.00D2006-01-05          ----------                                                                       " };


        List transactions = convertStringInputsToOriginEntries(stringInput);

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(0, insufficientFunds.size());

    }

    /**
     * Tests cash sufficient funds checking on a negative sufficient funds balance and a transaction that is a credit expense
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testSufficientFunds_CashNegativeBalanceCreditExpense() throws Exception {

        prepareSufficientFundsData("0211301", "H", "    ", 100, 300, 200, false);

        final String currentFiscalYear = TestUtils.getFiscalYearForTesting().toString();
        String[] stringInput = new String[] { currentFiscalYear+"BL0211301-----8000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                      300.00D2006-01-05          ----------                                                                       ", currentFiscalYear+"BL4631638-----8000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                      300.00C2006-01-05          ----------                                                                       " };


        List transactions = convertStringInputsToOriginEntries(stringInput);

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(0, insufficientFunds.size());

    }

    /**
     * Tests cash sufficient funds checking on a negative sufficient funds balance and a transaction that is a debit expense
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testSufficientFunds_CashNegativeBalanceDebitExpense() throws Exception {

        prepareSufficientFundsData("0211301", "H", "    ", -1000, 300, 200, false);

        final String currentFiscalYear = TestUtils.getFiscalYearForTesting().toString();
        String[] stringInput = new String[] { currentFiscalYear+"BL0211301-----8000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                      500.00C2006-01-05          ----------                                                                       ", currentFiscalYear+"BL4631638-----8000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                      500.00D2006-01-05          ----------                                                                       " };


        List transactions = convertStringInputsToOriginEntries(stringInput);

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(1, insufficientFunds.size());

    }

    /**
     * Tests cash sufficient funds checking on a positive sufficient funds balance and two transactions that cancel each other out
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testSufficientFunds_CashSameAccountPositiveBalanceNetZeroChange() throws Exception {

        prepareSufficientFundsData("0211301", "H", "    ", 1000, 300, 200, false);

        final String currentFiscalYear = TestUtils.getFiscalYearForTesting().toString();
        String[] stringInput = new String[] { currentFiscalYear+"BL0211301-----8000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                     1500.00C2006-01-05          ----------                                                                       ", currentFiscalYear+"BL0211301-----8000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                     1500.00D2006-01-05          ----------                                                                       " };


        List transactions = convertStringInputsToOriginEntries(stringInput);

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(0, insufficientFunds.size());

    }

    /**
     * Tests cash sufficient funds checking on a negative sufficient funds balance and two transactions that cancel each other out
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testSufficientFunds_CashSameAccountNegativeBalanceNetZeroChange() throws Exception {

        prepareSufficientFundsData("0211301", "H", "    ", 100, 300, 200, false);

        final String currentFiscalYear = TestUtils.getFiscalYearForTesting().toString();
        String[] stringInput = new String[] { currentFiscalYear+"BL0211301-----8000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                     1500.00C2006-01-05          ----------                                                                       ", currentFiscalYear+"BL0211301-----8000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                     1500.00D2006-01-05          ----------                                                                       " };


        List transactions = convertStringInputsToOriginEntries(stringInput);

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(0, insufficientFunds.size());

    }

    /**
     * Tests cash sufficient funds checking on a sufficient funds balance with insufficient funds for a transaction
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testSufficientFunds_CashInsufficientFunds() throws Exception {

        prepareSufficientFundsData("0211301", "H", "    ", 1000, 0, 500, false);

        final String currentFiscalYear = TestUtils.getFiscalYearForTesting().toString();
        String[] stringInput = new String[] { currentFiscalYear+"BL0211301-----8000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                      500.01C2006-01-05          ----------                                                                       ", currentFiscalYear+"BL4631638-----8000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                      500.01D2006-01-05          ----------                                                                       " };


        List transactions = convertStringInputsToOriginEntries(stringInput);

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(1, insufficientFunds.size());

    }

    /**
     * Tests cash sufficient funds checking on a sufficient funds balance where pending entries will provide sufficient funds for a transaction
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testSufficientFunds_CashPendingLedgerEntriesSufficientFunds() throws Exception {

        prepareSufficientFundsData("0211301", "H", "    ", 1000, 100, 100, true);

        final String currentFiscalYear = TestUtils.getFiscalYearForTesting().toString();
        String[] stringInput = new String[] { currentFiscalYear+"BL0211301-----8000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                     1300.00C2006-01-05          ----------                                                                       ", currentFiscalYear+"BL4631638-----8000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                     1300.00D2006-01-05          ----------                                                                       " };


        List transactions = convertStringInputsToOriginEntries(stringInput);

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(0, insufficientFunds.size());

    }

    /**
     * Tests cash sufficient funds checking on a sufficient funds balance where pending entries will not provide sufficient funds for a transaction
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testSufficientFunds_CashPendingLedgerEntriesInsufficientFunds() throws Exception {

        prepareSufficientFundsData("0211301", "H", "    ", 1000, 0, 200, true);

        final String currentFiscalYear = TestUtils.getFiscalYearForTesting().toString();
        String[] stringInput = new String[] { currentFiscalYear+"BL0211301-----8000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                     1300.01C2006-01-05          ----------                                                                       ", currentFiscalYear+"BL4631638-----8000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                     1300.01D2006-01-05          ----------                                                                       " };


        List transactions = convertStringInputsToOriginEntries(stringInput);

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(1, insufficientFunds.size());

    }

    /**
     * Tests basic level sufficient funds checking
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testSufficientFunds_LevelSufficientFunds() throws Exception {

        prepareSufficientFundsData("0211501", "L", "S&E", 1000, 300, 100, false);

        final String currentFiscalYear = TestUtils.getFiscalYearForTesting().toString();
        String[] stringInput = new String[] { currentFiscalYear+"BL0211501-----5000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                      500.00D2006-01-05          ----------                                                                       ", currentFiscalYear+"BL4631638-----5000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                      500.00C2006-01-05          ----------                                                                       " };


        List transactions = convertStringInputsToOriginEntries(stringInput);

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(0, insufficientFunds.size());

    }

    /**
     * Tests level sufficient funds checking on a negative sufficient funds balance and a transaction that is a credit expense
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testSufficientFunds_LevelNegativeBalanceCreditExpense() throws Exception {

        prepareSufficientFundsData("0211501", "L", "S&E", 100, 300, 200, false);

        final String currentFiscalYear = TestUtils.getFiscalYearForTesting().toString();
        String[] stringInput = new String[] { currentFiscalYear+"BL0211501-----5000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                      300.00C2006-01-05          ----------                                                                       ", currentFiscalYear+"BL4631638-----5000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                      300.00D2006-01-05          ----------                                                                       " };


        List transactions = convertStringInputsToOriginEntries(stringInput);

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(0, insufficientFunds.size());

    }

    /**
     * Tests level sufficient funds checking on a negative sufficient funds balance and a transaction that is a debit expense
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testSufficientFunds_LevelNegativeBalanceDebitExpense() throws Exception {

        prepareSufficientFundsData("0211501", "L", "S&E", -1000, 300, 200, false);

        final String currentFiscalYear = TestUtils.getFiscalYearForTesting().toString();
        String[] stringInput = new String[] { currentFiscalYear+"BL0211501-----5000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                      500.00D2006-01-05          ----------                                                                       ", currentFiscalYear+"BL4631638-----5000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                      500.00C2006-01-05          ----------                                                                       " };


        List transactions = convertStringInputsToOriginEntries(stringInput);

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(1, insufficientFunds.size());

    }

    /**
     * Tests level sufficient funds checking on a positive sufficient funds balance and two transactions that cancel each other out
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testSufficientFunds_LevelSameAccountPositiveBalanceNetZeroChange() throws Exception {

        prepareSufficientFundsData("0211501", "L", "S&E", 1000, 300, 200, false);

        final String currentFiscalYear = TestUtils.getFiscalYearForTesting().toString();
        String[] stringInput = new String[] { currentFiscalYear+"BL0211501-----5000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                     1500.00D2006-01-05          ----------                                                                       ", currentFiscalYear+"BL0211501-----5000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                     1500.00C2006-01-05          ----------                                                                       " };


        List transactions = convertStringInputsToOriginEntries(stringInput);

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(0, insufficientFunds.size());

    }

    /**
     * Tests level sufficient funds checking on a negative sufficient funds balance and two transactions that cancel each other out
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testSufficientFunds_LevelSameAccountNegativeBalanceNetZeroChange() throws Exception {

        prepareSufficientFundsData("0211501", "L", "S&E", 100, 300, 200, false);

        final String currentFiscalYear = TestUtils.getFiscalYearForTesting().toString();
        String[] stringInput = new String[] { currentFiscalYear+"BL0211501-----5000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                     1500.00D2006-01-05          ----------                                                                       ", currentFiscalYear+"BL0211501-----5000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                     1500.00C2006-01-05          ----------                                                                       " };


        List transactions = convertStringInputsToOriginEntries(stringInput);

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(0, insufficientFunds.size());

    }

    /**
     * Tests level sufficient funds checking on a sufficient funds balance with insufficient funds for a transaction
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testSufficientFunds_LevelInsufficientFunds() throws Exception {

        prepareSufficientFundsData("0211501", "L", "S&E", 1000, 300, 200, false);

        final String currentFiscalYear = TestUtils.getFiscalYearForTesting().toString();
        String[] stringInput = new String[] { currentFiscalYear+"BL0211501-----5000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                      500.01D2006-01-05          ----------                                                                       ", currentFiscalYear+"BL4631638-----5000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                      500.01C2006-01-05          ----------                                                                       " };


        List transactions = convertStringInputsToOriginEntries(stringInput);

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(1, insufficientFunds.size());

    }

    /**
     * Tests level sufficient funds checking on a sufficient funds balance where pending entries will provide the sufficient funds for a transaction
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testSufficientFunds_LevelPendingLedgerEntriesSufficientFunds() throws Exception {

        prepareSufficientFundsData("0211501", "L", "S&E", 1000, 100, 100, true);

        final String currentFiscalYear = TestUtils.getFiscalYearForTesting().toString();
        String[] stringInput = new String[] { currentFiscalYear+"BL0211501-----5000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                     1300.00D2006-01-05          ----------                                                                       ", currentFiscalYear+"BL4631638-----5000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                     1300.00C2006-01-05          ----------                                                                       " };


        List transactions = convertStringInputsToOriginEntries(stringInput);

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(0, insufficientFunds.size());

    }

    /**
     * Tests level sufficient funds checking on a sufficient funds balance where pending entries will not provide sufficient funds for a transaction
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testSufficientFunds_LevelPendingLedgerEntriesInsufficientFunds() throws Exception {

        prepareSufficientFundsData("0211501", "L", "S&E", 1000, 100, 100, true);

        final String currentFiscalYear = TestUtils.getFiscalYearForTesting().toString();
        String[] stringInput = new String[] { currentFiscalYear+"BL0211501-----5000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                     1300.01D2006-01-05          ----------                                                                       ", currentFiscalYear+"BL4631638-----5000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                     1300.01C2006-01-05          ----------                                                                       " };


        List transactions = convertStringInputsToOriginEntries(stringInput);

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(1, insufficientFunds.size());

    }

    /**
     * Tests basic object sufficient funds checking
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testSufficientFunds_ObjectSufficientFunds() throws Exception {

        prepareSufficientFundsData("0211701", "O", "5000", 1000, 300, 100, false);

        final String currentFiscalYear = TestUtils.getFiscalYearForTesting().toString();
        String[] stringInput = new String[] { currentFiscalYear+"BL0211701-----5000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                      500.00D2006-01-05          ----------                                                                       ", currentFiscalYear+"BL4631638-----5000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                      500.00C2006-01-05          ----------                                                                       " };


        List transactions = convertStringInputsToOriginEntries(stringInput);

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(0, insufficientFunds.size());

    }

    /**
     * Tests object sufficient funds checking on a negative sufficient funds balance and a credit expense transaction
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testSufficientFunds_ObjectNegativeBalanceCreditExpense() throws Exception {

        prepareSufficientFundsData("0211701", "O", "5000", 100, 300, 200, false);

        final String currentFiscalYear = TestUtils.getFiscalYearForTesting().toString();
        String[] stringInput = new String[] { currentFiscalYear+"BL0211701-----5000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                      300.00C2006-01-05          ----------                                                                       ", currentFiscalYear+"BL4631638-----5000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                      300.00D2006-01-05          ----------                                                                       " };


        List transactions = convertStringInputsToOriginEntries(stringInput);

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(0, insufficientFunds.size());

    }

    /**
     * Tests object sufficient funds checking on a negative sufficient funds balance and a debit expense transaction
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testSufficientFunds_ObjectNegativeBalanceDebitExpense() throws Exception {

        prepareSufficientFundsData("0211701", "O", "5000", -1000, 300, 200, false);

        final String currentFiscalYear = TestUtils.getFiscalYearForTesting().toString();
        String[] stringInput = new String[] { currentFiscalYear+"BL0211701-----5000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                      500.00D2006-01-05          ----------                                                                       ", currentFiscalYear+"BL4631638-----5000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                      500.00C2006-01-05          ----------                                                                       " };


        List transactions = convertStringInputsToOriginEntries(stringInput);

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(1, insufficientFunds.size());

    }

    /**
     * Tests object sufficient funds checking on a positive sufficient funds balance and two transactions that cancel each other out
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testSufficientFunds_ObjectSameAccountPositiveBalanceNetZeroChange() throws Exception {

        prepareSufficientFundsData("0211701", "O", "5000", 1000, 300, 200, false);

        final String currentFiscalYear = TestUtils.getFiscalYearForTesting().toString();
        String[] stringInput = new String[] { currentFiscalYear+"BL0211701-----5000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                     1500.00D2006-01-05          ----------                                                                       ", currentFiscalYear+"BL0211701-----5000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                     1500.00C2006-01-05          ----------                                                                       " };


        List transactions = convertStringInputsToOriginEntries(stringInput);

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(0, insufficientFunds.size());

    }

    /**
     * Tests object sufficient funds checking on a negative sufficient funds balance and two transactions that cancel each other out
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testSufficientFunds_ObjectSameAccountNegativeBalanceNetZeroChange() throws Exception {

        prepareSufficientFundsData("0211701", "O", "5000", 100, 300, 200, false);

        final String currentFiscalYear = TestUtils.getFiscalYearForTesting().toString();
        String[] stringInput = new String[] { currentFiscalYear+"BL0211701-----5000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                     1500.00D2006-01-05          ----------                                                                       ", currentFiscalYear+"BL0211701-----5000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                     1500.00C2006-01-05          ----------                                                                       " };


        List transactions = convertStringInputsToOriginEntries(stringInput);

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(0, insufficientFunds.size());

    }

    /**
     * Tests object sufficient funds checking on a sufficient funds balance with insufficient funds for a transaction
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testSufficientFunds_ObjectInsufficientFunds() throws Exception {

        prepareSufficientFundsData("0211701", "O", "5000", 1000, 300, 200, false);

        final String currentFiscalYear = TestUtils.getFiscalYearForTesting().toString();
        String[] stringInput = new String[] { currentFiscalYear+"BL0211701-----5000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                      500.01D2006-01-05          ----------                                                                       ", currentFiscalYear+"BL4631638-----5000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                      500.01C2006-01-05          ----------                                                                       " };


        List transactions = convertStringInputsToOriginEntries(stringInput);

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(1, insufficientFunds.size());

    }

    /**
     * Tests object sufficient funds checking on a sufficient funds balance where pending entries will provide sufficent funds for a transaction
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testSufficientFunds_ObjectPendingLedgerEntriesSufficientFunds() throws Exception {

        prepareSufficientFundsData("0211701", "O", "5000", 1000, 100, 100, true);

        final String currentFiscalYear = TestUtils.getFiscalYearForTesting().toString();
        String[] stringInput = new String[] { currentFiscalYear+"BL0211701-----5000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                     1300.00D2006-01-05          ----------                                                                       ", currentFiscalYear+"BL4631638-----5000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                     1300.00C2006-01-05          ----------                                                                       " };


        List transactions = convertStringInputsToOriginEntries(stringInput);

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(0, insufficientFunds.size());

    }

    /**
     * Tests object sufficient funds checking on a sufficient funds balance where pending entries will not provide sufficient funds for a transaction
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testSufficientFunds_ObjectPendingLedgerEntriesInsufficientFunds() throws Exception {

        prepareSufficientFundsData("0211701", "O", "5000", 1000, 100, 100, true);

        final String currentFiscalYear = TestUtils.getFiscalYearForTesting().toString();
        String[] stringInput = new String[] { currentFiscalYear+"BL0211701-----5000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                     1300.01D2006-01-05          ----------                                                                       ", currentFiscalYear+"BL4631638-----5000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                     1300.01C2006-01-05          ----------                                                                       " };


        List transactions = convertStringInputsToOriginEntries(stringInput);

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(1, insufficientFunds.size());

    }

    /**
     * Tests basic account sufficient funds checking
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testSufficientFunds_AccountSufficientFunds() throws Exception {

        prepareSufficientFundsData("0211901", "A", "    ", 1000, 300, 100, false);

        final String currentFiscalYear = TestUtils.getFiscalYearForTesting().toString();
        String[] stringInput = new String[] { currentFiscalYear+"BL0211901-----5000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                      500.00D2006-01-05          ----------                                                                       ", currentFiscalYear+"BL4631638-----5000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                      500.00C2006-01-05          ----------                                                                       " };


        List transactions = convertStringInputsToOriginEntries(stringInput);

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(0, insufficientFunds.size());

    }

    /**
     * Tests account sufficient funds checking on a negative sufficient funds balance and a credit expense transaction
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testSufficientFunds_AccountNegativeBalanceCreditExpense() throws Exception {

        prepareSufficientFundsData("0211901", "A", "    ", 100, 300, 200, false);

        final String currentFiscalYear = TestUtils.getFiscalYearForTesting().toString();
        String[] stringInput = new String[] { currentFiscalYear+"BL0211901-----5000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                      300.00C2006-01-05          ----------                                                                       ", currentFiscalYear+"BL4631638-----5000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                      300.00D2006-01-05          ----------                                                                       " };


        List transactions = convertStringInputsToOriginEntries(stringInput);

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(0, insufficientFunds.size());

    }

    /**
     * Tests account sufficient funds checking on a negative sufficient funds balance and a debit expense transaction
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testSufficientFunds_AccountNegativeBalanceDebitExpense() throws Exception {

        prepareSufficientFundsData("0211901", "A", "    ", -1000, 300, 200, false);

        final String currentFiscalYear = TestUtils.getFiscalYearForTesting().toString();
        String[] stringInput = new String[] { currentFiscalYear+"BL0211901-----5000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                      500.00D2006-01-05          ----------                                                                       ", currentFiscalYear+"BL4631638-----5000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                      500.00C2006-01-05          ----------                                                                       " };


        List transactions = convertStringInputsToOriginEntries(stringInput);

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(1, insufficientFunds.size());

    }

    /**
     * Tests account sufficient funds checking on a positive sufficient funds balance and two transactions that will cancel each other out
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testSufficientFunds_AccountSameAccountPositiveBalanceNetZeroChange() throws Exception {

        prepareSufficientFundsData("0211901", "A", "    ", 1000, 300, 200, false);

        final String currentFiscalYear = TestUtils.getFiscalYearForTesting().toString();
        String[] stringInput = new String[] { currentFiscalYear+"BL0211901-----5000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                     1500.00D2006-01-05          ----------                                                                       ", currentFiscalYear+"BL0211901-----5000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                     1500.00C2006-01-05          ----------                                                                       " };


        List transactions = convertStringInputsToOriginEntries(stringInput);

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(0, insufficientFunds.size());

    }

    /**
     * Tests account sufficient funds checking on a negative sufficient funds balance and two transactions that will cancel each other out
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testSufficientFunds_AccountSameAccountNegativeBalanceNetZeroChange() throws Exception {

        prepareSufficientFundsData("0211901", "A", "    ", 100, 300, 200, false);

        final String currentFiscalYear = TestUtils.getFiscalYearForTesting().toString();
        String[] stringInput = new String[] { currentFiscalYear+"BL0211901-----5000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                     1500.00D2006-01-05          ----------                                                                       ", currentFiscalYear+"BL0211901-----5000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                     1500.00C2006-01-05          ----------                                                                       " };


        List transactions = convertStringInputsToOriginEntries(stringInput);

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(0, insufficientFunds.size());

    }

    /**
     * Tests account sufficient funds checking on a sufficient funds balance without sufficient funds for a transaction
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testSufficientFunds_AccountInsufficientFunds() throws Exception {

        prepareSufficientFundsData("0211901", "A", "    ", 1000, 300, 200, false);

        final String currentFiscalYear = TestUtils.getFiscalYearForTesting().toString();
        String[] stringInput = new String[] { currentFiscalYear+"BL0211901-----5000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                      500.01D2006-01-05          ----------                                                                       ", currentFiscalYear+"BL4631638-----5000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                      500.01C2006-01-05          ----------                                                                       " };


        List transactions = convertStringInputsToOriginEntries(stringInput);

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(1, insufficientFunds.size());

    }

    /**
     * Tests account sufficient funds checking on a sufficient funds balance where pending ledger entries will provide sufficient funds for a transaction
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testSufficientFunds_AccountPendingLedgerEntriesSufficientFunds() throws Exception {

        prepareSufficientFundsData("0211901", "A", "    ", 1000, 100, 100, true);

        final String currentFiscalYear = TestUtils.getFiscalYearForTesting().toString();
        String[] stringInput = new String[] { currentFiscalYear+"BL0211901-----5000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                     1300.00D2006-01-05          ----------                                                                       ", currentFiscalYear+"BL4631638-----5000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                     1300.00C2006-01-05          ----------                                                                       " };


        List transactions = convertStringInputsToOriginEntries(stringInput);

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(0, insufficientFunds.size());

    }

    /**
     * Tests account sufficient funds checking on a sufficient funds balance where pending ledger entries will not provide sufficient funds for a transaction
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testSufficientFunds_AccountPendingLedgerEntriesInsufficientFunds() throws Exception {

        prepareSufficientFundsData("0211901", "A", "    ", 1000, 100, 100, true);

        final String currentFiscalYear = TestUtils.getFiscalYearForTesting().toString();
        String[] stringInput = new String[] { currentFiscalYear+"BL0211901-----5000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                     1300.01D2006-01-05          ----------                                                                       ", currentFiscalYear+"BL4631638-----5000---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                     1300.01C2006-01-05          ----------                                                                       " };


        List transactions = convertStringInputsToOriginEntries(stringInput);

        List<SufficientFundsItem> insufficientFunds = sufficientFundsService.checkSufficientFunds(transactions);

        assertEquals(1, insufficientFunds.size());

    }
}
