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

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.dbplatform.RawSQL;
import org.kuali.core.util.Guid;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.gl.OriginEntryTestBase;
import org.kuali.module.gl.bo.OriginEntrySource;
import org.kuali.test.ConfigureContext;

/**
 * Tests the PosterService
 */
@ConfigureContext
@RawSQL
public class PosterServiceTest extends OriginEntryTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PosterServiceTest.class);

    private PosterService posterService;

    /**
     * 
     * @see org.kuali.module.gl.OriginEntryTestBase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.MONTH, Calendar.JANUARY);
        c.set(Calendar.YEAR, 2004);

        // because of the cutoff time implementation, assume a specific time of day after the cutoff (10:00 am, see RunDateService
        // for details)
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 50);
        c.set(Calendar.SECOND, 0);
        date = c.getTime();

        // Set the run date of the job
        dateTimeService.setCurrentDate(date);

        posterService = SpringContext.getBean(PosterService.class);
        posterService.setDateTimeService(dateTimeService);
    }

    ///**
    // * Check invalid entries
    // * 
    // * @throws Exception
    // */
    //
    // This test succeeds in Eclipse, but fails in Anthill
    // public void testInvalidEntries() throws Exception {
    // LOG.debug("testInvalidEntries() started");
    //
    // /*
    // * These transactions are invalid for one reason or another:
    // * 0 - bad chartOfAccountsCode
    // * 1 - bad accountNumber
    // * 2 - bad objectTypeCode
    // * 3 - bad balanceTypeCode
    // * 4 - bad univFiscalYear
    // * 5 - bad debitCreditCode
    // * 6 - bad debitCreditCode
    // * 7 - 19 empty key field
    // */
    // String[] inputTransactions = {
    // "2007XX6044900-----5300---ACEE07CHKDPDBLANKFISC 12345214090047 EVERETT J PRESCOTT INC.
    // 1445.00D2006-01-05ABCDEFGHIJ----------12345678 ",
    // "2007BA9999999-----8000---ACAS07CHKDPDBLANKFISC 12345214090047 EVERETT J PRESCOTT INC.
    // 1445.00C2006-01-05ABCDEFGHIG----------12345678 ",
    // "2007BA6044900-----8000---ACZZ07CHKDPDBLANKFISC 12345214090047 EVERETT J PRESCOTT INC.
    // 1445.00C2006-01-05ABCDEFGHIG----------12345678 ",
    // "2007BA6044900-----8000---ZZEE07CHKDPDBLANKFISC 12345214090047 EVERETT J PRESCOTT INC.
    // 1445.00C2006-01-05ABCDEFGHIG----------12345678 ",
    // "9999BA6044900-----5300---ACEE07CHKDPDBLANKFISC 12345214090047 EVERETT J PRESCOTT INC.
    // 1445.00D2006-01-05ABCDEFGHIJ----------12345678 ",
    // "2007BA6044900-----5300---ACEE07CHKDPDBLANKFISC 12345214090047 EVERETT J PRESCOTT INC. 1445.00
    // 2006-01-05ABCDEFGHIJ----------12345678 ",
    // "2007BA6044900-----5300---ACEE07CHKDPDBLANKFISC 12345214090047 EVERETT J PRESCOTT INC.
    // 1445.00X2006-01-05ABCDEFGHIJ----------12345678 ",
    //
    // " BA6044900-----5300---ACEE07CHKDPDBLANKFISC 12345214090047 EVERETT J PRESCOTT INC.
    // 1445.00D2006-01-05ABCDEFGHIJ----------12345678 ",
    // "2007 6044900-----5300---ACEE07CHKDPDBLANKFISC 12345214090047 EVERETT J PRESCOTT INC.
    // 1445.00D2006-01-05ABCDEFGHIJ----------12345678 ",
    // "2007BA -----5300---ACEE07CHKDPDBLANKFISC 12345214090047 EVERETT J PRESCOTT INC.
    // 1445.00D2006-01-05ABCDEFGHIJ----------12345678 ",
    // "2007BA6044900 5300---ACEE07CHKDPDBLANKFISC 12345214090047 EVERETT J PRESCOTT INC.
    // 1445.00D2006-01-05ABCDEFGHIJ----------12345678 ",
    // "2007BA6044900----- ---ACEE07CHKDPDBLANKFISC 12345214090047 EVERETT J PRESCOTT INC.
    // 1445.00D2006-01-05ABCDEFGHIJ----------12345678 ",
    // "2007BA6044900-----5300 ACEE07CHKDPDBLANKFISC 12345214090047 EVERETT J PRESCOTT INC.
    // 1445.00D2006-01-05ABCDEFGHIJ----------12345678 ",
    // "2007BA6044900-----5300--- EE07CHKDPDBLANKFISC 12345214090047 EVERETT J PRESCOTT INC.
    // 1445.00D2006-01-05ABCDEFGHIJ----------12345678 ",
    // "2007BA6044900-----5300---AC 07CHKDPDBLANKFISC 12345214090047 EVERETT J PRESCOTT INC.
    // 1445.00D2006-01-05ABCDEFGHIJ----------12345678 ",
    // "2007BA6044900-----5300---ACEE CHKDPDBLANKFISC 12345214090047 EVERETT J PRESCOTT INC.
    // 1445.00D2006-01-05ABCDEFGHIJ----------12345678 ",
    // "2007BA6044900-----5300---ACEE07 PDBLANKFISC 12345214090047 EVERETT J PRESCOTT INC.
    // 1445.00D2006-01-05ABCDEFGHIJ----------12345678 ",
    // "2007BA6044900-----5300---ACEE07CHKD BLANKFISC 12345214090047 EVERETT J PRESCOTT INC.
    // 1445.00D2006-01-05ABCDEFGHIJ----------12345678 ",
    // "2007BA6044900-----5300---ACEE07CHKDPD 12345214090047 EVERETT J PRESCOTT INC. 1445.00D2006-01-05ABCDEFGHIJ----------12345678
    // ",
    // "2007BA6044900-----5300---ACEE07CHKDPDBLANKFISC 00000214090047 EVERETT J PRESCOTT INC.
    // 1445.00D2006-01-05ABCDEFGHIJ----------12345678 ", };
    //
    // EntryHolder[] outputTransactions = new EntryHolder[] {
    // new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007XX6044900-----5300---ACEE07CHKDPDBLANKFISC 12345214090047 EVERETT J
    // PRESCOTT INC. 1445.00D2006-01-05ABCDEFGHIJ----------12345678 "),
    // new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA9999999-----8000---ACAS07CHKDPDBLANKFISC 12345214090047 EVERETT J
    // PRESCOTT INC. 1445.00C2006-01-05ABCDEFGHIG----------12345678 "),
    // new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044900-----8000---ACZZ07CHKDPDBLANKFISC 12345214090047 EVERETT J
    // PRESCOTT INC. 1445.00C2006-01-05ABCDEFGHIG----------12345678 "),
    // new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044900-----5300---ACEE07CHKDPDBLANKFISC 12345214090047 EVERETT J
    // PRESCOTT INC. 1445.00 2006-01-05ABCDEFGHIJ----------12345678 "),
    // new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "9999BA6044900-----5300---ACEE07CHKDPDBLANKFISC 12345214090047 EVERETT J
    // PRESCOTT INC. 1445.00D2006-01-05ABCDEFGHIJ----------12345678 "),
    // new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044900-----8000---ZZEE07CHKDPDBLANKFISC 12345214090047 EVERETT J
    // PRESCOTT INC. 1445.00C2006-01-05ABCDEFGHIG----------12345678 "),
    // new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044900-----5300---ACEE07CHKDPDBLANKFISC 12345214090047 EVERETT J
    // PRESCOTT INC. 1445.00X2006-01-05ABCDEFGHIJ----------12345678 "),
    // new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007 6044900-----5300---ACEE07CHKDPDBLANKFISC 12345214090047 EVERETT J
    // PRESCOTT INC. 1445.00D2006-01-05ABCDEFGHIJ----------12345678 "),
    // new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044900 5300---ACEE07CHKDPDBLANKFISC 12345214090047 EVERETT J
    // PRESCOTT INC. 1445.00D2006-01-05ABCDEFGHIJ----------12345678 "),
    // new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044900-----5300---ACEE07CHKDPDBLANKFISC 00000214090047 EVERETT J
    // PRESCOTT INC. 1445.00D2006-01-05ABCDEFGHIJ----------12345678 "),
    // new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044900-----5300---ACEE07CHKDPD 12345214090047 EVERETT J PRESCOTT
    // INC. 1445.00D2006-01-05ABCDEFGHIJ----------12345678 "),
    // new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044900-----5300---ACEE07CHKD BLANKFISC 12345214090047 EVERETT J
    // PRESCOTT INC. 1445.00D2006-01-05ABCDEFGHIJ----------12345678 "),
    // new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044900-----5300---ACEE07 PDBLANKFISC 12345214090047 EVERETT J
    // PRESCOTT INC. 1445.00D2006-01-05ABCDEFGHIJ----------12345678 "),
    // new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044900-----5300---ACEE CHKDPDBLANKFISC 12345214090047 EVERETT J
    // PRESCOTT INC. 1445.00D2006-01-05ABCDEFGHIJ----------12345678 "),
    // new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044900-----5300---AC 07CHKDPDBLANKFISC 12345214090047 EVERETT J
    // PRESCOTT INC. 1445.00D2006-01-05ABCDEFGHIJ----------12345678 "),
    // new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044900-----5300--- EE07CHKDPDBLANKFISC 12345214090047 EVERETT J
    // PRESCOTT INC. 1445.00D2006-01-05ABCDEFGHIJ----------12345678 "),
    // new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044900-----5300 ACEE07CHKDPDBLANKFISC 12345214090047 EVERETT J
    // PRESCOTT INC. 1445.00D2006-01-05ABCDEFGHIJ----------12345678 "),
    // new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044900----- ---ACEE07CHKDPDBLANKFISC 12345214090047 EVERETT J
    // PRESCOTT INC. 1445.00D2006-01-05ABCDEFGHIJ----------12345678 "),
    // new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA -----5300---ACEE07CHKDPDBLANKFISC 12345214090047 EVERETT J PRESCOTT
    // INC. 1445.00D2006-01-05ABCDEFGHIJ----------12345678 "),
    // new EntryHolder(OriginEntrySource.SCRUBBER_VALID, " BA6044900-----5300---ACEE07CHKDPDBLANKFISC 12345214090047 EVERETT J
    // PRESCOTT INC. 1445.00D2006-01-05ABCDEFGHIJ----------12345678 "),
    // new EntryHolder(OriginEntrySource.MAIN_POSTER_ERROR, "2007XX6044900-----5300---ACEE07CHKDPDBLANKFISC 12345214090047 EVERETT J
    // PRESCOTT INC. 1445.00D2006-01-05ABCDEFGHIJ----------12345678 "),
    // new EntryHolder(OriginEntrySource.MAIN_POSTER_ERROR, "2007 6044900-----5300---ACEE07CHKDPDBLANKFISC 12345214090047 EVERETT J
    // PRESCOTT INC. 1445.00D2006-01-05ABCDEFGHIJ----------12345678 "),
    // new EntryHolder(OriginEntrySource.MAIN_POSTER_ERROR, "2007BA6044900-----5300---ACEE07CHKDPDBLANKFISC 12345214090047 EVERETT J
    // PRESCOTT INC. 1445.00X2006-01-05ABCDEFGHIJ----------12345678 "),
    // new EntryHolder(OriginEntrySource.MAIN_POSTER_ERROR, "2007BA6044900-----5300---AC 07CHKDPDBLANKFISC 12345214090047 EVERETT J
    // PRESCOTT INC. 1445.00D2006-01-05ABCDEFGHIJ----------12345678 "),
    // new EntryHolder(OriginEntrySource.MAIN_POSTER_ERROR, "2007BA6044900-----8000---ACZZ07CHKDPDBLANKFISC 12345214090047 EVERETT J
    // PRESCOTT INC. 1445.00C2006-01-05ABCDEFGHIG----------12345678 "),
    // new EntryHolder(OriginEntrySource.MAIN_POSTER_ERROR, "9999BA6044900-----5300---ACEE07CHKDPDBLANKFISC 12345214090047 EVERETT J
    // PRESCOTT INC. 1445.00D2006-01-05ABCDEFGHIJ----------12345678 "),
    // new EntryHolder(OriginEntrySource.MAIN_POSTER_ERROR, "2007BA -----5300---ACEE07CHKDPDBLANKFISC 12345214090047 EVERETT J
    // PRESCOTT INC. 1445.00D2006-01-05ABCDEFGHIJ----------12345678 "),
    // new EntryHolder(OriginEntrySource.MAIN_POSTER_ERROR, "2007BA9999999-----8000---ACAS07CHKDPDBLANKFISC 12345214090047 EVERETT J
    // PRESCOTT INC. 1445.00C2006-01-05ABCDEFGHIG----------12345678 "),
    // new EntryHolder(OriginEntrySource.MAIN_POSTER_ERROR, "2007BA6044900 5300---ACEE07CHKDPDBLANKFISC 12345214090047 EVERETT J
    // PRESCOTT INC. 1445.00D2006-01-05ABCDEFGHIJ----------12345678 "),
    // new EntryHolder(OriginEntrySource.MAIN_POSTER_ERROR, "2007BA6044900-----5300--- EE07CHKDPDBLANKFISC 12345214090047 EVERETT J
    // PRESCOTT INC. 1445.00D2006-01-05ABCDEFGHIJ----------12345678 "),
    // new EntryHolder(OriginEntrySource.MAIN_POSTER_ERROR, "2007BA6044900-----8000---ZZEE07CHKDPDBLANKFISC 12345214090047 EVERETT J
    // PRESCOTT INC. 1445.00C2006-01-05ABCDEFGHIG----------12345678 "),
    // new EntryHolder(OriginEntrySource.MAIN_POSTER_ERROR, "2007BA6044900-----5300---ACEE CHKDPDBLANKFISC 12345214090047 EVERETT J
    // PRESCOTT INC. 1445.00D2006-01-05ABCDEFGHIJ----------12345678 "),
    // new EntryHolder(OriginEntrySource.MAIN_POSTER_ERROR, " BA6044900-----5300---ACEE07CHKDPDBLANKFISC 12345214090047 EVERETT J
    // PRESCOTT INC. 1445.00D2006-01-05ABCDEFGHIJ----------12345678 "),
    // new EntryHolder(OriginEntrySource.MAIN_POSTER_ERROR, "2007BA6044900----- ---ACEE07CHKDPDBLANKFISC 12345214090047 EVERETT J
    // PRESCOTT INC. 1445.00D2006-01-05ABCDEFGHIJ----------12345678 "),
    // new EntryHolder(OriginEntrySource.MAIN_POSTER_ERROR, "2007BA6044900-----5300 ACEE07CHKDPDBLANKFISC 12345214090047 EVERETT J
    // PRESCOTT INC. 1445.00D2006-01-05ABCDEFGHIJ----------12345678 "),
    // new EntryHolder(OriginEntrySource.MAIN_POSTER_ERROR, "2007BA6044900-----5300---ACEE07CHKDPDBLANKFISC 00000214090047 EVERETT J
    // PRESCOTT INC. 1445.00D2006-01-05ABCDEFGHIJ----------12345678 "),
    // new EntryHolder(OriginEntrySource.MAIN_POSTER_ERROR, "2007BA6044900-----5300---ACEE07CHKDPDBLANKFISC 12345214090047 EVERETT J
    // PRESCOTT INC. 1445.00 2006-01-05ABCDEFGHIJ----------12345678 "),
    // new EntryHolder(OriginEntrySource.MAIN_POSTER_ERROR, "2007BA6044900-----5300---ACEE07CHKDPD 12345214090047 EVERETT J PRESCOTT
    // INC. 1445.00D2006-01-05ABCDEFGHIJ----------12345678 "),
    // new EntryHolder(OriginEntrySource.MAIN_POSTER_ERROR, "2007BA6044900-----5300---ACEE07CHKD BLANKFISC 12345214090047 EVERETT J
    // PRESCOTT INC. 1445.00D2006-01-05ABCDEFGHIJ----------12345678 "),
    // new EntryHolder(OriginEntrySource.MAIN_POSTER_ERROR, "2007BA6044900-----5300---ACEE07 PDBLANKFISC 12345214090047 EVERETT J
    // PRESCOTT INC. 1445.00D2006-01-05ABCDEFGHIJ----------12345678 "), };
    //
    // clearOriginEntryTables();
    // loadInputTransactions(OriginEntrySource.SCRUBBER_VALID, inputTransactions);
    //
    // posterService.postMainEntries();
    //
    // assertOriginEntries(3, outputTransactions);
    // }
    
    /**
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        // return the run date to the current date
        dateTimeService.setCurrentDate(new java.util.Date());
    }

    /**
     * Covers the posting of GL entries
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testPostGlEntry() throws Exception {
        LOG.debug("testPostGlEntry() started");

        String[] inputTransactions = { "2007BA6044909-----5300---ACEX07CHKDPDBLANKFISC     12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                       ", "2007BA6044909-----5300---ACEX07CHKDPDBLANKFISC     12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                       " };

        EntryHolder[] outputTransactions = { new EntryHolder(OriginEntrySource.SCRUBBER_VALID, inputTransactions[0]), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, inputTransactions[1]), new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID, inputTransactions[0]), new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID, inputTransactions[1]) };

        clearOriginEntryTables();
        clearGlEntryTable("BA", "6044909");
        loadInputTransactions(OriginEntrySource.SCRUBBER_VALID, inputTransactions);

        posterService.postMainEntries();

        assertOriginEntries(3, outputTransactions);

        List glEntries = unitTestSqlDao.sqlSelect("select * from gl_entry_t where fin_coa_cd = 'BA' and account_nbr = '6044909'");
        assertEquals("Should be 2 GL entries", 2, glEntries.size());
        Map glEntry = (Map) glEntries.get(0);

        BigDecimal ufy = (BigDecimal) glEntry.get("UNIV_FISCAL_YR");
        assertEquals("univ_fiscal_yr wrong", 2007, ufy.intValue());
        assertEquals("fin_coa_cd wrong", "BA", (String) glEntry.get("FIN_COA_CD"));
        assertEquals("account_nbr wrong", "6044909", (String) glEntry.get("ACCOUNT_NBR"));
        assertEquals("sub_acct_nbr wrong", "-----", (String) glEntry.get("SUB_ACCT_NBR"));
        assertEquals("fin_object_cd wrong", "5300", (String) glEntry.get("FIN_OBJECT_CD"));
        assertEquals("fin_sub_obj_cd wrong", "---", (String) glEntry.get("FIN_SUB_OBJ_CD"));
        assertEquals("FIN_BALANCE_TYP_CD wrong", "AC", (String) glEntry.get("FIN_BALANCE_TYP_CD"));
        assertEquals("FIN_OBJ_TYP_CD wrong", "EX", (String) glEntry.get("FIN_OBJ_TYP_CD"));
        assertEquals("UNIV_FISCAL_PRD_CD wrong", "07", (String) glEntry.get("UNIV_FISCAL_PRD_CD"));
        assertEquals("FDOC_TYP_CD wrong", "CHKD", (String) glEntry.get("FDOC_TYP_CD"));
        assertEquals("FS_ORIGIN_CD wrong", "PD", (String) glEntry.get("FS_ORIGIN_CD"));
        assertEquals("FDOC_NBR wrong", "BLANKFISC", (String) glEntry.get("FDOC_NBR"));
        BigDecimal tesq = (BigDecimal) glEntry.get("TRN_ENTR_SEQ_NBR");
        assertEquals("TRN_ENTR_SEQ_NBR wrong", 1, tesq.intValue());
        assertEquals("TRN_LDGR_ENTR_DESC wrong", "214090047 EVERETT J PRESCOTT INC.", (String) glEntry.get("TRN_LDGR_ENTR_DESC"));
        assertEquals("TRN_LDGR_ENTR_AMT wrong", 1445.00, getAmount(glEntry, "TRN_LDGR_ENTR_AMT"), 0.01);
        assertEquals("TRN_DEBIT_CRDT_CD wrong", "D", (String) glEntry.get("TRN_DEBIT_CRDT_CD"));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        assertEquals("TRANSACTION_DT wrong", "2006-01-05", sdf.format((Date) glEntry.get("TRANSACTION_DT")));
        assertEquals("ORG_DOC_NBR wrong", "ABCDEFGHIJ", (String) glEntry.get("ORG_DOC_NBR"));
        assertEquals("PROJECT_CD wrong", "----------", (String) glEntry.get("PROJECT_CD"));
        assertEquals("ORG_REFERENCE_ID wrong", "12345678", (String) glEntry.get("ORG_REFERENCE_ID"));
        assertTrue("FDOC_REF_TYP_CD is not blank, was: '" + glEntry.get("FDOC_REF_TYP_CD") + "'", StringUtils.isEmpty((String) glEntry.get("FDOC_REF_TYP_CD")));
        assertTrue("FS_REF_ORIGIN_CD is not blank, was: '" + glEntry.get("FS_REF_ORIGIN_CD") + "'", StringUtils.isEmpty((String) glEntry.get("FS_REF_ORIGIN_CD")));
        assertTrue("FDOC_REF_NBR is not blank, was: '" + glEntry.get("FDOC_REF_NBR") + "'", StringUtils.isEmpty((String) glEntry.get("FDOC_REF_NBR")));
        assertNull("FDOC_REVERSAL_DT is not null, was: '" + glEntry.get("FDOC_REVERSAL_DT") + "'", glEntry.get("FDOC_REVERSAL_DT"));
        assertEquals("TRN_ENCUM_UPDT_CD wrong", " ", (String) glEntry.get("TRN_ENCUM_UPDT_CD"));

        // The 2nd one should have a different sequence number
        glEntry = (Map) glEntries.get(1);
        tesq = (BigDecimal) glEntry.get("TRN_ENTR_SEQ_NBR");
        assertEquals("TRN_ENTR_SEQ_NBR wrong", 2, tesq.intValue());
    }

    /**
     * Check valid and invalid reversal posting
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testPostReversal() throws Exception {
        LOG.debug("testPostReversal() started");

        // if this test fails, ensure that the cutoff time is set to 10am.

        String[] inputTransactions = { "2007BA6044900-----5300---ACEX07CHKDPDREVTEST01     12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                    2006-03-01    ", "2007BA6044900-----5300---ACEX07CHKDPDREVTEST01     12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                    2006-03-01    ", "2007BA6044900-----5300---ACEX07CHKDPDREVTEST02     12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                  " };

        EntryHolder[] outputTransactions = { new EntryHolder(OriginEntrySource.SCRUBBER_VALID, inputTransactions[0]), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, inputTransactions[1]), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, inputTransactions[2]), new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID, inputTransactions[0]), new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID, inputTransactions[1]), new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID, inputTransactions[2]) };

        clearOriginEntryTables();
        clearReversalTable();
        loadInputTransactions(OriginEntrySource.SCRUBBER_VALID, inputTransactions);

        posterService.postMainEntries();

        assertOriginEntries(3, outputTransactions);

        List reversalEntries = unitTestSqlDao.sqlSelect("select * from gl_reversal_t");
        assertEquals("Should be 1 reversal row", 2, reversalEntries.size());
        Map reversalEntry = (Map) reversalEntries.get(0);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        assertEquals("FDOC_REVERSAL_DT wrong", "2006-03-01", sdf.format((Date) reversalEntry.get("FDOC_REVERSAL_DT")));
    }

    /**
     * Covers entry posting's effects on balances
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testPostBalance() throws Exception {
        LOG.debug("testPostBalance() started");

        String[] inputTransactions = { "2007BA6044900-----4166---ACEX01CHKDPDBALTEST01     12345214090047 EVERETT J PRESCOTT INC.                   10.01D2006-01-05ABCDEFGHIJ----------12345678                                  ", "2007BA6044900-----4166---ACEX02CHKDPDBALTEST02     12345214090047 EVERETT J PRESCOTT INC.                   20.02D2006-01-05ABCDEFGHIJ----------12345678                                  ", "2007BA6044900-----4166---ACEX03CHKDPDBALTEST03     12345214090047 EVERETT J PRESCOTT INC.                   30.03D2006-01-05ABCDEFGHIJ----------12345678                                  ", "2007BA6044900-----4166---ACEX04CHKDPDBALTEST04     12345214090047 EVERETT J PRESCOTT INC.                   40.04D2006-01-05ABCDEFGHIJ----------12345678                                  ", "2007BA6044900-----4166---ACEX05CHKDPDBALTEST05     12345214090047 EVERETT J PRESCOTT INC.                   50.05D2006-01-05ABCDEFGHIJ----------12345678                                  ",
                "2007BA6044900-----4166---ACEX06CHKDPDBALTEST06     12345214090047 EVERETT J PRESCOTT INC.                   60.06D2006-01-05ABCDEFGHIJ----------12345678                                  ", "2007BA6044900-----4166---ACEX07CHKDPDBALTEST07     12345214090047 EVERETT J PRESCOTT INC.                   70.07D2006-01-05ABCDEFGHIJ----------12345678                                  ", "2007BA6044900-----4166---ACEX08CHKDPDBALTEST08     12345214090047 EVERETT J PRESCOTT INC.                   80.08D2006-01-05ABCDEFGHIJ----------12345678                                  ", "2007BA6044900-----4166---ACEX09CHKDPDBALTEST09     12345214090047 EVERETT J PRESCOTT INC.                   90.09D2006-01-05ABCDEFGHIJ----------12345678                                  ", "2007BA6044900-----4166---ACEX10CHKDPDBALTEST10     12345214090047 EVERETT J PRESCOTT INC.                  100.10D2006-01-05ABCDEFGHIJ----------12345678                                  ",
                "2007BA6044900-----4166---ACEX11CHKDPDBALTEST11     12345214090047 EVERETT J PRESCOTT INC.                  110.11D2006-01-05ABCDEFGHIJ----------12345678                                  ", "2007BA6044900-----4166---ACEX12CHKDPDBALTEST12     12345214090047 EVERETT J PRESCOTT INC.                  120.12D2006-01-05ABCDEFGHIJ----------12345678                                  ", "2007BA6044900-----4166---ACEX13CHKDPDBALTEST12     12345214090047 EVERETT J PRESCOTT INC.                  130.13D2006-01-05ABCDEFGHIJ----------12345678                                  ", "2007BA6044900-----4166---ACEXABCHKDPDBALTEST12     12345214090047 EVERETT J PRESCOTT INC.                  140.14D2006-01-05ABCDEFGHIJ----------12345678                                  ", "2007BA6044900-----4166---ACEXBBCHKDPDBALTEST12     12345214090047 EVERETT J PRESCOTT INC.                  150.15D2006-01-05ABCDEFGHIJ----------12345678                                  ",
                "2007BA6044900-----4166---ACEXCBCHKDPDBALTEST12     12345214090047 EVERETT J PRESCOTT INC.                  160.16D2006-01-05ABCDEFGHIJ----------12345678                                  " };

        EntryHolder[] outputTransactions = new EntryHolder[inputTransactions.length * 2];
        for (int i = 0; i < inputTransactions.length; i++) {
            outputTransactions[i] = new EntryHolder(OriginEntrySource.SCRUBBER_VALID, inputTransactions[i]);
            outputTransactions[i + inputTransactions.length] = new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID, inputTransactions[i]);
        }

        clearOriginEntryTables();
        clearGlBalanceTable();
        loadInputTransactions(OriginEntrySource.SCRUBBER_VALID, inputTransactions);

        posterService.postMainEntries();

        assertOriginEntries(3, outputTransactions);

        List balances = unitTestSqlDao.sqlSelect("select * from gl_balance_t");
        assertEquals("Should be 1 row", 1, balances.size());
        Map balance = (Map) balances.get(0);

        BigDecimal a = (BigDecimal) balance.get("UNIV_FISCAL_YR");
        assertEquals("UNIV_FISCAL_YR is wrong", 2007, a.intValue());
        assertEquals("FIN_COA_CD is wrong", "BA", balance.get("FIN_COA_CD"));
        assertEquals("ACCOUNT_NBR is wrong", "6044900", balance.get("ACCOUNT_NBR"));
        assertEquals("SUB_ACCT_NBR is wrong", "-----", balance.get("SUB_ACCT_NBR"));
        assertEquals("FIN_OBJECT_CD is wrong", "4166", balance.get("FIN_OBJECT_CD"));
        assertEquals("FIN_SUB_OBJ_CD is wrong", "---", balance.get("FIN_SUB_OBJ_CD"));
        assertEquals("FIN_BALANCE_TYP_CD is wrong", "AC", balance.get("FIN_BALANCE_TYP_CD"));
        assertEquals("FIN_OBJ_TYP_CD is wrong", "EX", balance.get("FIN_OBJ_TYP_CD"));
        assertEquals("ACLN_ANNL_BAL_AMT is wrong", 1051.05, getAmount(balance, "ACLN_ANNL_BAL_AMT"), 0.01);
        assertEquals("FIN_BEG_BAL_LN_AMT is wrong", 150.15, getAmount(balance, "FIN_BEG_BAL_LN_AMT"), 0.01);
        assertEquals("CONTR_GR_BB_AC_AMT is wrong", 160.16, getAmount(balance, "CONTR_GR_BB_AC_AMT"), 0.01);
        assertEquals("MO1_ACCT_LN_AMT is wrong", 10.01, getAmount(balance, "MO1_ACCT_LN_AMT"), 0.01);
        assertEquals("MO2_ACCT_LN_AMT is wrong", 20.02, getAmount(balance, "MO2_ACCT_LN_AMT"), 0.01);
        assertEquals("MO3_ACCT_LN_AMT is wrong", 30.03, getAmount(balance, "MO3_ACCT_LN_AMT"), 0.01);
        assertEquals("MO4_ACCT_LN_AMT is wrong", 40.04, getAmount(balance, "MO4_ACCT_LN_AMT"), 0.01);
        assertEquals("MO5_ACCT_LN_AMT is wrong", 50.05, getAmount(balance, "MO5_ACCT_LN_AMT"), 0.01);
        assertEquals("MO6_ACCT_LN_AMT is wrong", 60.06, getAmount(balance, "MO6_ACCT_LN_AMT"), 0.01);
        assertEquals("MO7_ACCT_LN_AMT is wrong", 70.07, getAmount(balance, "MO7_ACCT_LN_AMT"), 0.01);
        assertEquals("MO8_ACCT_LN_AMT is wrong", 80.08, getAmount(balance, "MO8_ACCT_LN_AMT"), 0.01);
        assertEquals("MO9_ACCT_LN_AMT is wrong", 90.09, getAmount(balance, "MO9_ACCT_LN_AMT"), 0.01);
        assertEquals("MO10_ACCT_LN_AMT is wrong", 100.10, getAmount(balance, "MO10_ACCT_LN_AMT"), 0.01);
        assertEquals("MO11_ACCT_LN_AMT is wrong", 110.11, getAmount(balance, "MO11_ACCT_LN_AMT"), 0.01);
        assertEquals("MO12_ACCT_LN_AMT is wrong", 120.12, getAmount(balance, "MO12_ACCT_LN_AMT"), 0.01);
        assertEquals("MO13_ACCT_LN_AMT is wrong", 130.13, getAmount(balance, "MO13_ACCT_LN_AMT"), 0.01);

        String[] inputTransactions2 = { "2007BA6044900-----4166---ACEX01CHKDPDBALTEST01     12345214090047 EVERETT J PRESCOTT INC.                    0.01C2006-01-05ABCDEFGHIJ----------12345678                             ", "2007BA6044900-----4166---ACEX02CHKDPDBALTEST02     12345214090047 EVERETT J PRESCOTT INC.                    0.02C2006-01-05ABCDEFGHIJ----------12345678                             ", "2007BA6044900-----4166---ACEX03CHKDPDBALTEST03     12345214090047 EVERETT J PRESCOTT INC.                    0.03C2006-01-05ABCDEFGHIJ----------12345678                             ", "2007BA6044900-----4166---ACEX04CHKDPDBALTEST04     12345214090047 EVERETT J PRESCOTT INC.                    0.04C2006-01-05ABCDEFGHIJ----------12345678                             ", "2007BA6044900-----4166---ACEX05CHKDPDBALTEST05     12345214090047 EVERETT J PRESCOTT INC.                    0.05C2006-01-05ABCDEFGHIJ----------12345678                             ",
                "2007BA6044900-----4166---ACEX06CHKDPDBALTEST06     12345214090047 EVERETT J PRESCOTT INC.                    0.06C2006-01-05ABCDEFGHIJ----------12345678                             ", "2007BA6044900-----4166---ACEX07CHKDPDBALTEST07     12345214090047 EVERETT J PRESCOTT INC.                    0.07C2006-01-05ABCDEFGHIJ----------12345678                             ", "2007BA6044900-----4166---ACEX08CHKDPDBALTEST08     12345214090047 EVERETT J PRESCOTT INC.                    0.08C2006-01-05ABCDEFGHIJ----------12345678                             ", "2007BA6044900-----4166---ACEX09CHKDPDBALTEST09     12345214090047 EVERETT J PRESCOTT INC.                    0.09C2006-01-05ABCDEFGHIJ----------12345678                             ", "2007BA6044900-----4166---ACEX10CHKDPDBALTEST10     12345214090047 EVERETT J PRESCOTT INC.                    0.10C2006-01-05ABCDEFGHIJ----------12345678                             ",
                "2007BA6044900-----4166---ACEX11CHKDPDBALTEST11     12345214090047 EVERETT J PRESCOTT INC.                    0.11C2006-01-05ABCDEFGHIJ----------12345678                             ", "2007BA6044900-----4166---ACEX12CHKDPDBALTEST12     12345214090047 EVERETT J PRESCOTT INC.                    0.12C2006-01-05ABCDEFGHIJ----------12345678                             ", "2007BA6044900-----4166---ACEX13CHKDPDBALTEST12     12345214090047 EVERETT J PRESCOTT INC.                    0.13C2006-01-05ABCDEFGHIJ----------12345678                             ", };

        EntryHolder[] outputTransactions2 = new EntryHolder[inputTransactions2.length * 2];
        for (int i = 0; i < inputTransactions2.length; i++) {
            outputTransactions2[i] = new EntryHolder(OriginEntrySource.SCRUBBER_VALID, inputTransactions2[i]);
            outputTransactions2[i + inputTransactions2.length] = new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID, inputTransactions2[i]);
        }

        clearOriginEntryTables();
        loadInputTransactions(OriginEntrySource.SCRUBBER_VALID, inputTransactions2);

        posterService.postMainEntries();

        assertOriginEntries(3, outputTransactions2);

        balances = unitTestSqlDao.sqlSelect("select * from gl_balance_t");
        assertEquals("Should be 1 row", 1, balances.size());
        balance = (Map) balances.get(0);

        a = (BigDecimal) balance.get("UNIV_FISCAL_YR");
        assertEquals("2 UNIV_FISCAL_YR is wrong", 2007, a.intValue());
        assertEquals("2 FIN_COA_CD is wrong", "BA", balance.get("FIN_COA_CD"));
        assertEquals("2 ACCOUNT_NBR is wrong", "6044900", balance.get("ACCOUNT_NBR"));
        assertEquals("2 SUB_ACCT_NBR is wrong", "-----", balance.get("SUB_ACCT_NBR"));
        assertEquals("2 FIN_OBJECT_CD is wrong", "4166", balance.get("FIN_OBJECT_CD"));
        assertEquals("2 FIN_SUB_OBJ_CD is wrong", "---", balance.get("FIN_SUB_OBJ_CD"));
        assertEquals("2 FIN_BALANCE_TYP_CD is wrong", "AC", balance.get("FIN_BALANCE_TYP_CD"));
        assertEquals("2 FIN_OBJ_TYP_CD is wrong", "EX", balance.get("FIN_OBJ_TYP_CD"));
        assertEquals("2 ACLN_ANNL_BAL_AMT is wrong", 1050.14, getAmount(balance, "ACLN_ANNL_BAL_AMT"), 0.01);
        assertEquals("2 FIN_BEG_BAL_LN_AMT is wrong", 150.15, getAmount(balance, "FIN_BEG_BAL_LN_AMT"), 0.01);
        assertEquals("2 CONTR_GR_BB_AC_AMT is wrong", 160.16, getAmount(balance, "CONTR_GR_BB_AC_AMT"), 0.01);
        assertEquals("2 MO1_ACCT_LN_AMT is wrong", 10.00, getAmount(balance, "MO1_ACCT_LN_AMT"), 0.01);
        assertEquals("2 MO2_ACCT_LN_AMT is wrong", 20.00, getAmount(balance, "MO2_ACCT_LN_AMT"), 0.01);
        assertEquals("2 MO3_ACCT_LN_AMT is wrong", 30.00, getAmount(balance, "MO3_ACCT_LN_AMT"), 0.01);
        assertEquals("2 MO4_ACCT_LN_AMT is wrong", 40.00, getAmount(balance, "MO4_ACCT_LN_AMT"), 0.01);
        assertEquals("2 MO5_ACCT_LN_AMT is wrong", 50.00, getAmount(balance, "MO5_ACCT_LN_AMT"), 0.01);
        assertEquals("2 MO6_ACCT_LN_AMT is wrong", 60.00, getAmount(balance, "MO6_ACCT_LN_AMT"), 0.01);
        assertEquals("2 MO7_ACCT_LN_AMT is wrong", 70.00, getAmount(balance, "MO7_ACCT_LN_AMT"), 0.01);
        assertEquals("2 MO8_ACCT_LN_AMT is wrong", 80.00, getAmount(balance, "MO8_ACCT_LN_AMT"), 0.01);
        assertEquals("2 MO9_ACCT_LN_AMT is wrong", 90.00, getAmount(balance, "MO9_ACCT_LN_AMT"), 0.01);
        assertEquals("2 MO10_ACCT_LN_AMT is wrong", 100.00, getAmount(balance, "MO10_ACCT_LN_AMT"), 0.01);
        assertEquals("2 MO11_ACCT_LN_AMT is wrong", 110.00, getAmount(balance, "MO11_ACCT_LN_AMT"), 0.01);
        assertEquals("2 MO12_ACCT_LN_AMT is wrong", 120.00, getAmount(balance, "MO12_ACCT_LN_AMT"), 0.01);
        assertEquals("2 MO13_ACCT_LN_AMT is wrong", 130.00, getAmount(balance, "MO13_ACCT_LN_AMT"), 0.01);
    }

    /**
     * Covers entry posting's effects on encumbrances
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testPostEncumbrance() throws Exception {
        LOG.debug("testPostEncumbrance() started");

        String[] inputTransactions = { "2007BA6044900-----4166---IEEX06CHKDPDENCTEST01     12345214090047 EVERETT J PRESCOTT INC.                  100.01D2006-01-05ABCDEFGHIJ----------12345678                              D    ", "2007BA6044900-----5215---IEEX06CHKDPDENCTEST01     12345214090047 EVERETT J PRESCOTT INC.                  200.02D2006-01-05ABCDEFGHIJ----------12345678                              D    ", "2007BA6044900-----4166---IEEX06CHKDPDENCTEST02     12345214090047 EVERETT J PRESCOTT INC.                   50.00C2006-01-05ABCDEFGHIJ----------12345678CHKDPDENCTEST01               R    ", "2007BA6044900-----5215---IEEX06CHKDPDENCTEST02     12345214090047 EVERETT J PRESCOTT INC.                   60.00C2006-01-05ABCDEFGHIJ----------12345678CHKDPDENCTEST01               R    ", "2007BA6044900-----5215---ACEX06CHKDPDENCTEST02     12345214090047 EVERETT J PRESCOTT INC.                   60.00C2006-01-05ABCDEFGHIJ----------12345678                                   ", };

        EntryHolder[] outputTransactions = new EntryHolder[] { new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044900-----4166---IEEX06CHKDPDENCTEST01     12345214090047 EVERETT J PRESCOTT INC.                  100.01D2006-01-05ABCDEFGHIJ----------12345678                              D    "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044900-----5215---IEEX06CHKDPDENCTEST01     12345214090047 EVERETT J PRESCOTT INC.                  200.02D2006-01-05ABCDEFGHIJ----------12345678                              D    "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044900-----4166---IEEX06CHKDPDENCTEST02     12345214090047 EVERETT J PRESCOTT INC.                   50.00C2006-01-05ABCDEFGHIJ----------12345678CHKDPDENCTEST01               R    "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044900-----5215---IEEX06CHKDPDENCTEST02     12345214090047 EVERETT J PRESCOTT INC.                   60.00C2006-01-05ABCDEFGHIJ----------12345678CHKDPDENCTEST01               R    "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044900-----5215---ACEX06CHKDPDENCTEST02     12345214090047 EVERETT J PRESCOTT INC.                   60.00C2006-01-05ABCDEFGHIJ----------12345678                                   "),

                new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID, "2007BA6044900-----4166---IEEX06CHKDPDENCTEST01     12345214090047 EVERETT J PRESCOTT INC.                  100.01D2006-01-05ABCDEFGHIJ----------12345678                              D    "), new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID, "2007BA6044900-----5215---IEEX06CHKDPDENCTEST01     12345214090047 EVERETT J PRESCOTT INC.                  200.02D2006-01-05ABCDEFGHIJ----------12345678                              D    "), new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID, "2007BA6044900-----5215---ACEX06CHKDPDENCTEST02     12345214090047 EVERETT J PRESCOTT INC.                   60.00C2006-01-05ABCDEFGHIJ----------12345678                                   "), new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID, "2007BA6044900-----4166---IEEX06CHKDPDENCTEST02     12345214090047 EVERETT J PRESCOTT INC.                   50.00C2006-01-05ABCDEFGHIJ----------12345678CHKDPDENCTEST01               R    "),
                new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID, "2007BA6044900-----5215---IEEX06CHKDPDENCTEST02     12345214090047 EVERETT J PRESCOTT INC.                   60.00C2006-01-05ABCDEFGHIJ----------12345678CHKDPDENCTEST01               R    "), };

        clearOriginEntryTables();
        clearEncumbranceTable();
        loadInputTransactions(OriginEntrySource.SCRUBBER_VALID, inputTransactions);

        posterService.postMainEntries();

        assertOriginEntries(3, outputTransactions);

        List encumbrances = unitTestSqlDao.sqlSelect("select * from gl_encumbrance_t order by fin_object_cd");
        assertEquals("Should be 2 encumbrances", 2, encumbrances.size());
        Map enc4166 = (Map) encumbrances.get(0);
        Map enc5215 = (Map) encumbrances.get(1);

        BigDecimal a = (BigDecimal) enc4166.get("UNIV_FISCAL_YR");
        assertEquals("UNIV_FISCAL_YR is wrong", 2007, a.intValue());
        assertEquals("FIN_COA_CD is wrong", "BA", enc4166.get("FIN_COA_CD"));
        assertEquals("ACCOUNT_NBR is wrong", "6044900", enc4166.get("ACCOUNT_NBR"));
        assertEquals("SUB_ACCT_NBR is wrong", "-----", enc4166.get("SUB_ACCT_NBR"));
        assertEquals("FIN_OBJECT_CD is wrong", "4166", enc4166.get("FIN_OBJECT_CD"));
        assertEquals("FIN_SUB_OBJ_CD is wrong", "---", enc4166.get("FIN_SUB_OBJ_CD"));
        assertEquals("FIN_BALANCE_TYP_CD is wrong", "IE", enc4166.get("FIN_BALANCE_TYP_CD"));
        assertEquals("FDOC_TYP_CD is wrong", "CHKD", enc4166.get("FDOC_TYP_CD"));
        assertEquals("FS_ORIGIN_CD is wrong", "PD", enc4166.get("FS_ORIGIN_CD"));
        assertEquals("FDOC_NBR is wrong", "ENCTEST01", enc4166.get("FDOC_NBR"));
        assertEquals("TRN_ENCUM_DESC is wrong", "214090047 EVERETT J PRESCOTT INC.", enc4166.get("TRN_ENCUM_DESC"));
        assertEquals("ACLN_ENCUM_AMT is wrong", 100.01, getAmount(enc4166, "ACLN_ENCUM_AMT"), 0.01);
        assertEquals("ACLN_ENCUM_CLS_AMT is wrong", 50, getAmount(enc4166, "ACLN_ENCUM_CLS_AMT"), 0.01);

        a = (BigDecimal) enc5215.get("UNIV_FISCAL_YR");
        assertEquals("UNIV_FISCAL_YR is wrong", 2007, a.intValue());
        assertEquals("FIN_COA_CD is wrong", "BA", enc5215.get("FIN_COA_CD"));
        assertEquals("ACCOUNT_NBR is wrong", "6044900", enc5215.get("ACCOUNT_NBR"));
        assertEquals("SUB_ACCT_NBR is wrong", "-----", enc5215.get("SUB_ACCT_NBR"));
        assertEquals("FIN_OBJECT_CD is wrong", "5215", enc5215.get("FIN_OBJECT_CD"));
        assertEquals("FIN_SUB_OBJ_CD is wrong", "---", enc5215.get("FIN_SUB_OBJ_CD"));
        assertEquals("FIN_BALANCE_TYP_CD is wrong", "IE", enc5215.get("FIN_BALANCE_TYP_CD"));
        assertEquals("FDOC_TYP_CD is wrong", "CHKD", enc5215.get("FDOC_TYP_CD"));
        assertEquals("FS_ORIGIN_CD is wrong", "PD", enc5215.get("FS_ORIGIN_CD"));
        assertEquals("FDOC_NBR is wrong", "ENCTEST01", enc5215.get("FDOC_NBR"));
        assertEquals("TRN_ENCUM_DESC is wrong", "214090047 EVERETT J PRESCOTT INC.", enc5215.get("TRN_ENCUM_DESC"));
        assertEquals("ACLN_ENCUM_AMT is wrong", 200.02, getAmount(enc5215, "ACLN_ENCUM_AMT"), 0.01);
        assertEquals("ACLN_ENCUM_CLS_AMT is wrong", 60, getAmount(enc5215, "ACLN_ENCUM_CLS_AMT"), 0.01);
    }

    /**
     * Covers posting entry's effects on account balances
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testPostGlAccountBalance() throws Exception {
        LOG.debug("testPostGlAccountBalance() started");

        String[] inputTransactions = { "2007BA6044900-----4166---TREX07CHKDPDGLACCTBA1     12345DESCRIPTION                                        123.45D2006-01-05ABCDEFGHIJ----------12345678                                                                       ", "2007BA6044900-----4166---ACEX07CHKDPDGLACCTBA1     12345DESCRIPTION                                       1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                       ", "2007BA6044900-----4166---EXEX07CHKDPDGLACCTBA1     12345DESCRIPTION                                        345.00D2006-01-05ABCDEFGHIJ----------12345678                                                                       ", "2007BA6044900-----4166---CBEX07CHKDPDGLACCTBA1     12345DESCRIPTION                                        222.00 2006-01-05ABCDEFGHIJ----------12345678                                                                       ",
                "2007BA6044900-----4166---ACEX07CHKDPDGLACCTBA1     12345DESCRIPTION                                          5.00C2006-01-05ABCDEFGHIJ----------12345678                                                                       ", "2007BA6044900-----4166---EXEX07CHKDPDGLACCTBA1     12345DESCRIPTION                                          5.00C2006-01-05ABCDEFGHIJ----------12345678                                                                       ", "2007BA6044900-----4166---CBEX07CHKDPDGLACCTBA1     12345DESCRIPTION                                         -2.00 2006-01-05ABCDEFGHIJ----------12345678                                                                       " };

        EntryHolder[] outputTransactions = new EntryHolder[] { new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044900-----4166---TREX07CHKDPDGLACCTBA1     12345DESCRIPTION                                        123.45D2006-01-05ABCDEFGHIJ----------12345678                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044900-----4166---ACEX07CHKDPDGLACCTBA1     12345DESCRIPTION                                       1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044900-----4166---EXEX07CHKDPDGLACCTBA1     12345DESCRIPTION                                        345.00D2006-01-05ABCDEFGHIJ----------12345678                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044900-----4166---CBEX07CHKDPDGLACCTBA1     12345DESCRIPTION                                        222.00 2006-01-05ABCDEFGHIJ----------12345678                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044900-----4166---ACEX07CHKDPDGLACCTBA1     12345DESCRIPTION                                          5.00C2006-01-05ABCDEFGHIJ----------12345678                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044900-----4166---EXEX07CHKDPDGLACCTBA1     12345DESCRIPTION                                          5.00C2006-01-05ABCDEFGHIJ----------12345678                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044900-----4166---CBEX07CHKDPDGLACCTBA1     12345DESCRIPTION                                         -2.00 2006-01-05ABCDEFGHIJ----------12345678                                                                       "),

                new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID, "2007BA6044900-----4166---ACEX07CHKDPDGLACCTBA1     12345DESCRIPTION                                          5.00C2006-01-05ABCDEFGHIJ----------12345678                                                                       "), new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID, "2007BA6044900-----4166---ACEX07CHKDPDGLACCTBA1     12345DESCRIPTION                                       1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                       "), new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID, "2007BA6044900-----4166---CBEX07CHKDPDGLACCTBA1     12345DESCRIPTION                                         -2.00 2006-01-05ABCDEFGHIJ----------12345678                                                                       "),
                new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID, "2007BA6044900-----4166---CBEX07CHKDPDGLACCTBA1     12345DESCRIPTION                                        222.00 2006-01-05ABCDEFGHIJ----------12345678                                                                       "), new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID, "2007BA6044900-----4166---EXEX07CHKDPDGLACCTBA1     12345DESCRIPTION                                          5.00C2006-01-05ABCDEFGHIJ----------12345678                                                                       "), new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID, "2007BA6044900-----4166---EXEX07CHKDPDGLACCTBA1     12345DESCRIPTION                                        345.00D2006-01-05ABCDEFGHIJ----------12345678                                                                       "),
                new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID, "2007BA6044900-----4166---TREX07CHKDPDGLACCTBA1     12345DESCRIPTION                                        123.45D2006-01-05ABCDEFGHIJ----------12345678                                                                       "), };

        clearOriginEntryTables();
        clearGlAccountBalanceTable();
        loadInputTransactions(OriginEntrySource.SCRUBBER_VALID, inputTransactions);
        posterService.postMainEntries();

        assertOriginEntries(3, outputTransactions);

        List balances = unitTestSqlDao.sqlSelect("select * from gl_acct_balances_t");
        assertEquals("Should be 1 balance", 1, balances.size());
        Map bal = (Map) balances.get(0);

        BigDecimal a = (BigDecimal) bal.get("UNIV_FISCAL_YR");
        assertEquals("UNIV_FISCAL_YR is wrong", 2007, a.intValue());
        assertEquals("FIN_COA_CD is wrong", "BA", bal.get("FIN_COA_CD"));
        assertEquals("ACCOUNT_NBR is wrong", "6044900", bal.get("ACCOUNT_NBR"));
        assertEquals("SUB_ACCT_NBR is wrong", "-----", bal.get("SUB_ACCT_NBR"));
        assertEquals("FIN_OBJECT_CD is wrong", "4166", bal.get("FIN_OBJECT_CD"));
        assertEquals("FIN_SUB_OBJ_CD is wrong", "---", bal.get("FIN_SUB_OBJ_CD"));
        assertEquals("CURR_BDLN_BAL_AMT is wrong", 220.00, getAmount(bal, "CURR_BDLN_BAL_AMT"), 0.01);
        assertEquals("ACLN_ACTLS_BAL_AMT is wrong", 1440.00, getAmount(bal, "ACLN_ACTLS_BAL_AMT"), 0.01);
        assertEquals("ACLN_ENCUM_BAL_AMT is wrong", 340.00, getAmount(bal, "ACLN_ENCUM_BAL_AMT"), 0.01);
    }

    /**
     * Covers posting entry's effects on expenditure transactions
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    // @RelatesTo(RelatesTo.JiraIssue.KULRNE4797)
    public void testPostExpenditureTransaction() throws Exception {
        LOG.debug("testPostExpenditureTransaction() started");

        String[] inputTransactions = {
        // Not posted because icr type cd = 10
                "2007BL2231499-----4166---ACEX07CHKDPDET0000011     12345DESCRIPTION                                      11000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                       ",

                // Not posted because icr type cd is null
                "2007BA9019993-----4166---ACEX07CHKDPDET0000011     12345DESCRIPTION                                      11000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                       ",

                // Not posted because the period code is AB, BB or CB
                "2007BL4031407-----4166---ACEXABCHKDPDET0000011     12345DESCRIPTION                                      12000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                       ", "2007BL4031407-----4166---ACEXBBCHKDPDET0000011     12345DESCRIPTION                                          0.12C2006-01-05ABCDEFGHIJ----------12345678                                                                       ", "2007BL4031407-----4166---ACEXCBCHKDPDET0000011     12345DESCRIPTION                                      12000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                       ",

                // Posted
                "2007BL4031407-----4166---ACEX07CHKDPDET0000011     12345DESCRIPTION                                      12000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                       ", "2007BL4031407-----4166---ACEX07CHKDPDET0000011     12345DESCRIPTION                                          0.12C2006-01-05ABCDEFGHIJ----------12345678                                                                       ",

                // Posted
                "2007BL4131406-----2400---EXEX07CHKDPDET0000011     12345DESCRIPTION                                         12.00C2006-01-05ABCDEFGHIJ----------12345678                                                                       ", "2007BL4131406-----2400---EXEX07CHKDPDET0000011     12345DESCRIPTION                                          2.00D2006-01-05ABCDEFGHIJ----------12345678                                                                       ",

                // Not posted - excluded account
                "2007BL4431406-----2400---EXEX07CHKDPDET0000011     12345DESCRIPTION                                         33.00C2006-01-05ABCDEFGHIJ----------12345678                                                                       ", "2007BL4431406-----2400---EXEX07CHKDPDET0000011     12345DESCRIPTION                                          4.00D2006-01-05ABCDEFGHIJ----------12345678                                                                       ",

                // Not posted - excluded type (23)
                "2007BL4431407-----2400---EXEX07CHKDPDET0000011     12345DESCRIPTION                                         44.00C2006-01-05ABCDEFGHIJ----------12345678                                                                       ", "2007BL4431407-----2400---EXEX07CHKDPDET0000011     12345DESCRIPTION                                          5.00D2006-01-05ABCDEFGHIJ----------12345678                                                                       ",

                // Posted, non-CS sub acct
                "2007BL4631464XXX  4166---ACEX07CHKDPDET0000021     12345DESCRIPTION                                         25.00D2006-01-05ABCDEFGHIJ----------12345678                                                                       ", "2007BL4631464XXX  4166---ACEX07CHKDPDET0000021     12345DESCRIPTION                                          2.00C2006-01-05ABCDEFGHIJ----------12345678                                                                       ", };

        EntryHolder[] outputTransactions = new EntryHolder[] { new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL2231499-----4166---ACEX07CHKDPDET0000011     12345DESCRIPTION                                      11000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA9019993-----4166---ACEX07CHKDPDET0000011     12345DESCRIPTION                                      11000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4031407-----4166---ACEXABCHKDPDET0000011     12345DESCRIPTION                                      12000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4031407-----4166---ACEXBBCHKDPDET0000011     12345DESCRIPTION                                          0.12C2006-01-05ABCDEFGHIJ----------12345678                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4031407-----4166---ACEXCBCHKDPDET0000011     12345DESCRIPTION                                      12000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4031407-----4166---ACEX07CHKDPDET0000011     12345DESCRIPTION                                      12000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4031407-----4166---ACEX07CHKDPDET0000011     12345DESCRIPTION                                          0.12C2006-01-05ABCDEFGHIJ----------12345678                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4131406-----2400---EXEX07CHKDPDET0000011     12345DESCRIPTION                                         12.00C2006-01-05ABCDEFGHIJ----------12345678                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4131406-----2400---EXEX07CHKDPDET0000011     12345DESCRIPTION                                          2.00D2006-01-05ABCDEFGHIJ----------12345678                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4431406-----2400---EXEX07CHKDPDET0000011     12345DESCRIPTION                                         33.00C2006-01-05ABCDEFGHIJ----------12345678                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4431406-----2400---EXEX07CHKDPDET0000011     12345DESCRIPTION                                          4.00D2006-01-05ABCDEFGHIJ----------12345678                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4431407-----2400---EXEX07CHKDPDET0000011     12345DESCRIPTION                                         44.00C2006-01-05ABCDEFGHIJ----------12345678                                                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4431407-----2400---EXEX07CHKDPDET0000011     12345DESCRIPTION                                          5.00D2006-01-05ABCDEFGHIJ----------12345678                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631464XXX  4166---ACEX07CHKDPDET0000021     12345DESCRIPTION                                         25.00D2006-01-05ABCDEFGHIJ----------12345678                                                                       "), new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL4631464XXX  4166---ACEX07CHKDPDET0000021     12345DESCRIPTION                                          2.00C2006-01-05ABCDEFGHIJ----------12345678                                                                       "),

                new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID, "2007BA9019993-----4166---ACEX07CHKDPDET0000011     12345DESCRIPTION                                      11000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                       "), new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID, "2007BL2231499-----4166---ACEX07CHKDPDET0000011     12345DESCRIPTION                                      11000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                       "), new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID, "2007BL4031407-----4166---ACEX07CHKDPDET0000011     12345DESCRIPTION                                          0.12C2006-01-05ABCDEFGHIJ----------12345678                                                                       "),
                new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID, "2007BL4031407-----4166---ACEX07CHKDPDET0000011     12345DESCRIPTION                                      12000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                       "), new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID, "2007BL4031407-----4166---ACEXABCHKDPDET0000011     12345DESCRIPTION                                      12000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                       "), new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID, "2007BL4031407-----4166---ACEXBBCHKDPDET0000011     12345DESCRIPTION                                          0.12C2006-01-05ABCDEFGHIJ----------12345678                                                                       "),
                new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID, "2007BL4031407-----4166---ACEXCBCHKDPDET0000011     12345DESCRIPTION                                      12000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                       "), new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID, "2007BL4131406-----2400---EXEX07CHKDPDET0000011     12345DESCRIPTION                                          2.00D2006-01-05ABCDEFGHIJ----------12345678                                                                       "), new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID, "2007BL4131406-----2400---EXEX07CHKDPDET0000011     12345DESCRIPTION                                         12.00C2006-01-05ABCDEFGHIJ----------12345678                                                                       "),
                new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID, "2007BL4431406-----2400---EXEX07CHKDPDET0000011     12345DESCRIPTION                                          4.00D2006-01-05ABCDEFGHIJ----------12345678                                                                       "), new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID, "2007BL4431406-----2400---EXEX07CHKDPDET0000011     12345DESCRIPTION                                         33.00C2006-01-05ABCDEFGHIJ----------12345678                                                                       "), new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID, "2007BL4431407-----2400---EXEX07CHKDPDET0000011     12345DESCRIPTION                                          5.00D2006-01-05ABCDEFGHIJ----------12345678                                                                       "),
                new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID, "2007BL4431407-----2400---EXEX07CHKDPDET0000011     12345DESCRIPTION                                         44.00C2006-01-05ABCDEFGHIJ----------12345678                                                                       "), new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID, "2007BL4631464XXX  4166---ACEX07CHKDPDET0000021     12345DESCRIPTION                                          2.00C2006-01-05ABCDEFGHIJ----------12345678                                                                       "), new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID, "2007BL4631464XXX  4166---ACEX07CHKDPDET0000021     12345DESCRIPTION                                         25.00D2006-01-05ABCDEFGHIJ----------12345678                                                                       "), };

        clearOriginEntryTables();
        clearExpenditureTable();

        // Add sub account for testing
        unitTestSqlDao.sqlCommand("delete from ca_sub_acct_t where fin_coa_cd = 'BL' and account_nbr = '4631464' and sub_acct_nbr = 'XXX'");
        unitTestSqlDao.sqlCommand("delete from ca_a21_sub_acct_t where fin_coa_cd = 'BL' and account_nbr = '4631464' and sub_acct_nbr = 'XXX'");
        unitTestSqlDao.sqlCommand("insert into ca_sub_acct_t (FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,OBJ_ID,VER_NBR,SUB_ACCT_NM,SUB_ACCT_ACTV_CD,FIN_RPT_CHRT_CD,FIN_RPT_ORG_CD,FIN_RPT_CD) values ('BL','4631464','XXX','" + new Guid().toString() + "',1,'XXX','N',null,null,null)");
        unitTestSqlDao.sqlCommand("INSERT INTO CA_A21_SUB_ACCT_T (FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, OBJ_ID, VER_NBR, SUB_ACCT_TYP_CD, ICR_TYP_CD, FIN_SERIES_ID, ICR_FIN_COA_CD, ICR_ACCOUNT_NBR, OFF_CMP_CD, CST_SHR_COA_CD, CST_SHRSRCACCT_NBR, CST_SRCSUBACCT_NBR) VALUES ('BL','4631464','XXX','" + new Guid().toString() + "',1,'EX',null,'000',null,null,null,'BL','1031400',null)");

        // Modify account for testing
        unitTestSqlDao.sqlCommand("update ca_account_t set fin_series_id = '11' where account_nbr = '9019993'");

        // Exclude account
        unitTestSqlDao.sqlCommand("delete from ca_icr_excl_acct_t where account_nbr = '4431406'");
        unitTestSqlDao.sqlCommand("insert into CA_ICR_EXCL_ACCT_T (FIN_COA_CD, ACCOUNT_NBR, FIN_OBJ_COA_CD, FIN_OBJECT_CD, OBJ_ID, VER_NBR) values ('BL','4431406','BL','2400','" + new Guid().toString() + "',1)");

        // Exclude type
        unitTestSqlDao.sqlCommand("delete from ca_icr_excl_type_t where acct_icr_typ_cd = '23' and fin_coa_cd = 'BL'");
        unitTestSqlDao.sqlCommand("insert into CA_ICR_EXCL_TYPE_T (ACCT_ICR_TYP_CD, FIN_COA_CD, FIN_OBJECT_CD, OBJ_ID, VER_NBR, ACCT_ICR_EXCL_TYP_ACTV_IND) values ('23','BL','2401','" + new Guid().toString() + "',1,'Y')");

        loadInputTransactions(OriginEntrySource.SCRUBBER_VALID, inputTransactions);
        posterService.postMainEntries();

        assertOriginEntries(3, outputTransactions);

        List trans = unitTestSqlDao.sqlSelect("select * from GL_EXPEND_TRN_T order by account_nbr");

        assertEquals("Wrong number of transactions", 4, trans.size());
        Map acct4031407 = (Map) trans.get(0);
        assertEquals("Account wrong", "4031407", acct4031407.get("ACCOUNT_NBR"));
        assertEquals("Amount wrong", 11999.88, getAmount(acct4031407, "ACCT_OBJ_DCST_AMT"), 0.01);
        Map acct4131406 = (Map) trans.get(1);
        assertEquals("Account wrong", "4131406", acct4131406.get("ACCOUNT_NBR"));
        assertEquals("Amount wrong", -10.00, getAmount(acct4131406, "ACCT_OBJ_DCST_AMT"), 0.01);
        Map acct4631464 = (Map) trans.get(3);
        assertEquals("Account wrong", "4631464", acct4631464.get("ACCOUNT_NBR"));
        assertEquals("Amount wrong", 23.00, getAmount(acct4631464, "ACCT_OBJ_DCST_AMT"), 0.01);
    }

    /**
     * Covers the reversal poster
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testReversalPoster() throws Exception {
        LOG.debug("testPostReversalPosting() started");

        // First post these entries to the reversal table
        String[] inputTransactions = { "2007BL2231408-----5300---ACEX07CHKDPDREVTEST01     12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                    2003-10-01    ", "2007BL2231408-----5300---ACEX07CHKDPDREVTEST02     12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                    2003-12-31    ", "2007BL2231408-----5300---ACEX07CHKDPDREVTEST03     12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                    2004-01-01    ", "2007BL2231408-----5300---ACEX07CHKDPDREVTEST04     12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                    2004-01-02    ", "2007BL2231408-----5300---ACEX07CHKDPDREVTEST05     12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                    2005-03-01    " };

        clearOriginEntryTables();
        clearReversalTable();
        loadInputTransactions(OriginEntrySource.SCRUBBER_VALID, inputTransactions);
        posterService.postMainEntries();

        unitTestSqlDao.clearCache();

        // Now post the reversal entries
        clearGlEntryTable("BL", "2231408");
        posterService.postReversalEntries();

        List results = unitTestSqlDao.sqlSelect("select * from gl_entry_t where account_nbr = '2231408' order by fdoc_nbr");
        assertEquals("Wrong number of posted entries", 3, results.size());
        Map row1 = (Map) results.get(0);
        Map row2 = (Map) results.get(1);
        Map row3 = (Map) results.get(2);

        assertEquals("Wrong doc nbr", "REVTEST01", row1.get("FDOC_NBR"));
        assertEquals("Wrong doc nbr", "REVTEST02", row2.get("FDOC_NBR"));
        assertEquals("Wrong doc nbr", "REVTEST03", row3.get("FDOC_NBR"));
    }

    /**
     * Covers poster ICR generation
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testIcrGeneration() throws Exception {
        LOG.debug("testIcrGeneration() started");
        // Load the expenditure table
        unitTestSqlDao.sqlCommand("delete from gl_expend_trn_t");

        // This one shouldn't generate any entries
        unitTestSqlDao.sqlCommand("INSERT INTO GL_EXPEND_TRN_T (UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FIN_BALANCE_TYP_CD, FIN_OBJ_TYP_CD, UNIV_FISCAL_PRD_CD, PROJECT_CD, ORG_REFERENCE_ID, ACCT_OBJ_DCST_AMT) VALUES ('2004', 'BL', '1031400', '-----', '4166', '---', 'AC', 'EX', '07', '----------', '12345678', 10000)");

        // This one is fin_series_id 001 3.13% to 1 account (2 gl entries)
        unitTestSqlDao.sqlCommand("INSERT INTO GL_EXPEND_TRN_T (UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FIN_BALANCE_TYP_CD, FIN_OBJ_TYP_CD, UNIV_FISCAL_PRD_CD, PROJECT_CD, ORG_REFERENCE_ID, ACCT_OBJ_DCST_AMT) VALUES ('2004', 'BL', '4531407', '-----', '4166', '---', 'AC', 'EX', '07', '----------', '12345678', 10000)");

        // This one is fin_series_id 002 3.8% to 2 accounts (2.0% & 1.8%)
        unitTestSqlDao.sqlCommand("INSERT INTO GL_EXPEND_TRN_T (UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FIN_BALANCE_TYP_CD, FIN_OBJ_TYP_CD, UNIV_FISCAL_PRD_CD, PROJECT_CD, ORG_REFERENCE_ID, ACCT_OBJ_DCST_AMT) VALUES ('2004', 'BL', '4531408', '-----', '4166', '---', 'AC', 'EX', '07', '----------', '12345678', 10000)");

        // Clear origin entry & origin entry group
        clearOriginEntryTables();

        posterService.generateIcrTransactions();

        List results = unitTestSqlDao.sqlSelect("select * from gl_expend_trn_t");
        assertEquals("Should be no expenditure rows left", 0, results.size());

        results = unitTestSqlDao.sqlSelect("select * from gl_origin_entry_t order by origin_entry_id");
        assertEquals("Wrong number of transactions generated", 10, results.size());

        Map row = (Map) results.get(0);
        assertEquals("0 account number wrong", "4531407", row.get("ACCOUNT_NBR"));
        assertEquals("0 object code wrong", "5500", row.get("FIN_OBJECT_CD"));
        assertEquals("0 amount wrong", 313.0, getAmount(row, "TRN_LDGR_ENTR_AMT"), 0.01);

        row = (Map) results.get(1);
        assertEquals("1 account number wrong", "4531407", row.get("ACCOUNT_NBR"));
        assertEquals("1 object code wrong", "8000", row.get("FIN_OBJECT_CD"));
        assertEquals("1 amount wrong", 313.0, getAmount(row, "TRN_LDGR_ENTR_AMT"), 0.01);

        row = (Map) results.get(2);
        assertEquals("2 account number wrong", "1023287", row.get("ACCOUNT_NBR"));
        assertEquals("2 object code wrong", "1803", row.get("FIN_OBJECT_CD"));
        assertEquals("2 amount wrong", 313.0, getAmount(row, "TRN_LDGR_ENTR_AMT"), 0.01);

        row = (Map) results.get(3);
        assertEquals("3 account number wrong", "1023287", row.get("ACCOUNT_NBR"));
        assertEquals("3 object code wrong", "8000", row.get("FIN_OBJECT_CD"));
        assertEquals("3 amount wrong", 313.0, getAmount(row, "TRN_LDGR_ENTR_AMT"), 0.01);

        row = (Map) results.get(4);
        assertEquals("4 account number wrong", "4531408", row.get("ACCOUNT_NBR"));
        assertEquals("4 object code wrong", "5500", row.get("FIN_OBJECT_CD"));
        assertEquals("4 amount wrong", 380.0, getAmount(row, "TRN_LDGR_ENTR_AMT"), 0.01);

        row = (Map) results.get(5);
        assertEquals("5 account number wrong", "4531408", row.get("ACCOUNT_NBR"));
        assertEquals("5 object code wrong", "8000", row.get("FIN_OBJECT_CD"));
        assertEquals("5 amount wrong", 380.0, getAmount(row, "TRN_LDGR_ENTR_AMT"), 0.01);

        row = (Map) results.get(6);
        assertEquals("6 account number wrong", "1023287", row.get("ACCOUNT_NBR"));
        assertEquals("6 object code wrong", "1803", row.get("FIN_OBJECT_CD"));
        assertEquals("6 amount wrong", 200.0, getAmount(row, "TRN_LDGR_ENTR_AMT"), 0.01);

        row = (Map) results.get(7);
        assertEquals("7 account number wrong", "1023287", row.get("ACCOUNT_NBR"));
        assertEquals("7 object code wrong", "8000", row.get("FIN_OBJECT_CD"));
        assertEquals("7 amount wrong", 200.0, getAmount(row, "TRN_LDGR_ENTR_AMT"), 0.01);

        row = (Map) results.get(8);
        assertEquals("8 account number wrong", "1031400", row.get("ACCOUNT_NBR"));
        assertEquals("8 object code wrong", "1803", row.get("FIN_OBJECT_CD"));
        assertEquals("8 amount wrong", 180.0, getAmount(row, "TRN_LDGR_ENTR_AMT"), 0.01);

        row = (Map) results.get(9);
        assertEquals("9 account number wrong", "1031400", row.get("ACCOUNT_NBR"));
        assertEquals("9 object code wrong", "8000", row.get("FIN_OBJECT_CD"));
        assertEquals("9 amount wrong", 180.0, getAmount(row, "TRN_LDGR_ENTR_AMT"), 0.01);
    }

    /**
     * Converts an amount in a Map to a double (to make it easier to compare)
     * 
     * @param map the Map with values in it
     * @param field the key of the Map with a double in it
     * @return a double from that map
     */
    private double getAmount(Map map, String field) {
        BigDecimal amt = (BigDecimal) map.get(field);
        if (amt == null) {
            return Double.NaN;
        }
        else {
            return amt.doubleValue();
        }
    }
}
