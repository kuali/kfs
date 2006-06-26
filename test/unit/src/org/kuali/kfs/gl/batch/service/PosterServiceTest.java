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

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.module.gl.OriginEntryTestBase;
import org.kuali.module.gl.bo.OriginEntrySource;
import org.kuali.module.gl.bo.Transaction;

public class PosterServiceTest extends OriginEntryTestBase {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PosterServiceTest.class);

  private PosterService posterService;

  /* (non-Javadoc)
   * @see org.springframework.test.AbstractTransactionalSpringContextTests#onSetUpInTransaction()
   */
  protected void setUp() throws Exception {
    super.setUp();

    Calendar c = Calendar.getInstance();
    c.set(Calendar.DAY_OF_MONTH,1);
    c.set(Calendar.MONTH,Calendar.JANUARY);
    c.set(Calendar.YEAR,2004);
    date = c.getTime();

    // Set the run date of the job
    dateTimeService.currentDate = date;

    posterService = (PosterService)beanFactory.getBean("glPosterService");
  }

  /**
   * Check invalid entries
   * 
   * @throws Exception
   */
  public void testInvalidEntries() throws Exception {
    LOG.debug("testInvalidEntries() started");

    /*
     *  These transactions are invalid for one reason or another:
     *    0 - bad chartOfAccountsCode
     *    1 - bad accountNumber
     *    2 - bad objectTypeCode
     *    3 - bad balanceTypeCode
     *    4 - bad univFiscalYear
     *    5 - bad debitCreditCode
     *    6 - bad debitCreditCode
     *    7 - 19 null key field
     */
    String[] inputTransactions = {
        "2004XX6044900-----5300---ACEE07CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
        "2004BA9999999-----8000---ACAS07CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00C2006-01-05ABCDEFGHIG----------12345678                                                                  ",
        "2004BA6044900-----8000---ACZZ07CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00C2006-01-05ABCDEFGHIG----------12345678                                                                  ",
        "2004BA6044900-----8000---ZZEE07CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00C2006-01-05ABCDEFGHIG----------12345678                                                                  ",
        "9999BA6044900-----5300---ACEE07CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
        "2004BA6044900-----5300---ACEE07CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00 2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
        "2004BA6044900-----5300---ACEE07CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00X2006-01-05ABCDEFGHIJ----------12345678                                                                  ",

        "    BA6044900-----5300---ACEE07CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
        "2004  6044900-----5300---ACEE07CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
        "2004BA       -----5300---ACEE07CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
        "2004BA6044900     5300---ACEE07CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
        "2004BA6044900-----    ---ACEE07CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
        "2004BA6044900-----5300   ACEE07CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
        "2004BA6044900-----5300---  EE07CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
        "2004BA6044900-----5300---AC  07CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
        "2004BA6044900-----5300---ACEE  CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
        "2004BA6044900-----5300---ACEE07    PDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
        "2004BA6044900-----5300---ACEE07CHKD  BLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
        "2004BA6044900-----5300---ACEE07CHKDPD         12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
        "2004BA6044900-----5300---ACEE07CHKDPDBLANKFISC00000214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
    };

    EntryHolder[] outputTransactions = new EntryHolder[] {
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004XX6044900-----5300---ACEE07CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BA9999999-----8000---ACAS07CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00C2006-01-05ABCDEFGHIG----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BA6044900-----8000---ACZZ07CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00C2006-01-05ABCDEFGHIG----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BA6044900-----8000---ZZEE07CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00C2006-01-05ABCDEFGHIG----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"9999BA6044900-----5300---ACEE07CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BA6044900-----5300---ACEE07CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00 2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BA6044900-----5300---ACEE07CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00X2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"    BA6044900-----5300---ACEE07CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004  6044900-----5300---ACEE07CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BA       -----5300---ACEE07CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BA6044900     5300---ACEE07CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BA6044900-----    ---ACEE07CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BA6044900-----5300   ACEE07CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BA6044900-----5300---  EE07CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BA6044900-----5300---AC  07CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BA6044900-----5300---ACEE  CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BA6044900-----5300---ACEE07    PDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BA6044900-----5300---ACEE07CHKD  BLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BA6044900-----5300---ACEE07CHKDPD         12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BA6044900-----5300---ACEE07CHKDPDBLANKFISC00000214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),

            new EntryHolder(OriginEntrySource.MAIN_POSTER_ERROR,"2004BA6044900-----5300---ACEE07CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00 2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_ERROR,"2004BA6044900-----5300---ACEE07CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00X2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_ERROR,"2004BA6044900-----5300---AC  07CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_ERROR,"2004BA6044900-----5300---ACEE07CHKDPDBLANKFISC00000214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_ERROR,"2004BA6044900-----5300   ACEE07CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_ERROR,"2004BA6044900-----8000---ACZZ07CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00C2006-01-05ABCDEFGHIG----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_ERROR,"2004BA6044900-----    ---ACEE07CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_ERROR,"9999BA6044900-----5300---ACEE07CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_ERROR,"    BA6044900-----5300---ACEE07CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_ERROR,"2004BA6044900-----5300---ACEE  CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_ERROR,"2004BA6044900-----8000---ZZEE07CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00C2006-01-05ABCDEFGHIG----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_ERROR,"2004BA6044900-----5300---  EE07CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_ERROR,"2004BA6044900     5300---ACEE07CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_ERROR,"2004BA9999999-----8000---ACAS07CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00C2006-01-05ABCDEFGHIG----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_ERROR,"2004BA       -----5300---ACEE07CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_ERROR,"2004XX6044900-----5300---ACEE07CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_ERROR,"2004  6044900-----5300---ACEE07CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_ERROR,"2004BA6044900-----5300---ACEE07CHKDPD         12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_ERROR,"2004BA6044900-----5300---ACEE07CHKD  BLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_ERROR,"2004BA6044900-----5300---ACEE07    PDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
    };

    clearOriginEntryTables();
    loadInputTransactions(OriginEntrySource.SCRUBBER_VALID,inputTransactions);

    posterService.postMainEntries();

    assertOriginEntries(3,outputTransactions);
  }

  /**
   * Check GL Entry inserts
   * 
   * @throws Exception
   */
  public void testPostGlEntry() throws Exception {
    LOG.debug("testPostGlEntry() started");

    String[] inputTransactions = {
        "2004BA6044909-----5300---ACEX07CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
        "2004BA6044909-----5300---ACEX07CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "
    };

    EntryHolder[] outputTransactions = {
        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,inputTransactions[0]),
        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,inputTransactions[1]),
        new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,inputTransactions[0]),
        new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,inputTransactions[1])
    };

    clearOriginEntryTables();
    clearGlEntryTable("BA","6044909");
    loadInputTransactions(OriginEntrySource.SCRUBBER_VALID,inputTransactions);

    posterService.postMainEntries();

    assertOriginEntries(3,outputTransactions);
    
    List glEntries = unitTestSqlDao.sqlSelect("select * from gl_entry_t where fin_coa_cd = 'BA' and account_nbr = '6044909'");
    assertEquals("Should be 2 GL entries",2,glEntries.size());
    Map glEntry = (Map)glEntries.get(0);

    BigDecimal ufy = (BigDecimal)glEntry.get("UNIV_FISCAL_YR");
    assertEquals("univ_fiscal_yr wrong",2004,ufy.intValue());
    assertEquals("fin_coa_cd wrong","BA",(String)glEntry.get("FIN_COA_CD"));
    assertEquals("account_nbr wrong","6044909",(String)glEntry.get("ACCOUNT_NBR"));
    assertEquals("sub_acct_nbr wrong","-----",(String)glEntry.get("SUB_ACCT_NBR"));
    assertEquals("fin_object_cd wrong","5300",(String)glEntry.get("FIN_OBJECT_CD"));
    assertEquals("fin_sub_obj_cd wrong","---",(String)glEntry.get("FIN_SUB_OBJ_CD"));
    assertEquals("FIN_BALANCE_TYP_CD wrong","AC",(String)glEntry.get("FIN_BALANCE_TYP_CD"));
    assertEquals("FIN_OBJ_TYP_CD wrong","EX",(String)glEntry.get("FIN_OBJ_TYP_CD"));
    assertEquals("UNIV_FISCAL_PRD_CD wrong","07",(String)glEntry.get("UNIV_FISCAL_PRD_CD"));
    assertEquals("FDOC_TYP_CD wrong","CHKD",(String)glEntry.get("FDOC_TYP_CD"));
    assertEquals("FS_ORIGIN_CD wrong","PD",(String)glEntry.get("FS_ORIGIN_CD"));
    assertEquals("FDOC_NBR wrong","BLANKFISC",(String)glEntry.get("FDOC_NBR"));
    BigDecimal tesq = (BigDecimal)glEntry.get("TRN_ENTR_SEQ_NBR");
    assertEquals("TRN_ENTR_SEQ_NBR wrong",1,tesq.intValue());
    assertEquals("TRN_LDGR_ENTR_DESC wrong","214090047 EVERETT J PRESCOTT INC.",(String)glEntry.get("TRN_LDGR_ENTR_DESC"));
    assertEquals("TRN_LDGR_ENTR_AMT wrong",1445.00,getAmount(glEntry,"TRN_LDGR_ENTR_AMT"),0.01);
    assertEquals("TRN_DEBIT_CRDT_CD wrong","D",(String)glEntry.get("TRN_DEBIT_CRDT_CD"));
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    assertEquals("TRANSACTION_DT wrong","2006-01-05",sdf.format((Date)glEntry.get("TRANSACTION_DT")));
    assertEquals("ORG_DOC_NBR wrong","ABCDEFGHIJ",(String)glEntry.get("ORG_DOC_NBR"));
    assertEquals("PROJECT_CD wrong","----------",(String)glEntry.get("PROJECT_CD"));
    assertEquals("ORG_REFERENCE_ID wrong","12345678",(String)glEntry.get("ORG_REFERENCE_ID"));
    assertNull("FDOC_REF_TYP_CD wrong",glEntry.get("FDOC_REF_TYP_CD"));
    assertNull("FS_REF_ORIGIN_CD wrong",glEntry.get("FS_REF_ORIGIN_CD"));
    assertNull("FDOC_REF_NBR wrong",glEntry.get("FDOC_REF_NBR"));
    assertNull("FDOC_REVERSAL_DT wrong",glEntry.get("FDOC_REVERSAL_DT"));
    assertEquals("TRN_ENCUM_UPDT_CD wrong"," ",(String)glEntry.get("TRN_ENCUM_UPDT_CD"));
    assertNull("BDGT_YR wrong",glEntry.get("BDGT_YR"));

    // The 2nd one should have a different sequence number
    glEntry = (Map)glEntries.get(1);
    tesq = (BigDecimal)glEntry.get("TRN_ENTR_SEQ_NBR");
    assertEquals("TRN_ENTR_SEQ_NBR wrong",2,tesq.intValue());
  }

  /**
   * Check valid and invalid reversal posting
   * 
   * @throws Exception
   */
  public void testPostReversal() throws Exception {
    LOG.debug("testPostReversalPosting() started");

    String[] inputTransactions = {
        "2004BA6044900-----5300---ACEX07CHKDPDREVTEST0112345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678               2006-03-01    ",
        "2004BA6044900-----5300---ACEX07CHKDPDREVTEST0112345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678               2006-03-01    ",
        "2004BA6044900-----5300---ACEX07CHKDPDREVTEST0212345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                             "
    };

    EntryHolder[] outputTransactions = {
        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,inputTransactions[0]),
        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,inputTransactions[1]),
        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,inputTransactions[2]),
        new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,inputTransactions[0]),
        new EntryHolder(OriginEntrySource.MAIN_POSTER_ERROR,inputTransactions[1]),
        new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,inputTransactions[2])
    };

    clearOriginEntryTables();
    clearReversalTable();
    loadInputTransactions(OriginEntrySource.SCRUBBER_VALID,inputTransactions);

    posterService.postMainEntries();

    assertOriginEntries(3,outputTransactions);

    List reversalEntries = unitTestSqlDao.sqlSelect("select * from gl_reversal_t");
    assertEquals("Should be 1 reversal row",1,reversalEntries.size());
    Map reversalEntry = (Map)reversalEntries.get(0);

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    assertEquals("FDOC_REVERSAL_DT wrong","2006-03-01",sdf.format((Date)reversalEntry.get("FDOC_REVERSAL_DT")));
  }

  public void testPostBalance() throws Exception {
    LOG.debug("testPostBalance() started");

    String[] inputTransactions = {
        // 23456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890
        //         1         2         3         4         5         6         7         8         9         0         1         2         3         4         5         6         7
        //                                                                                                   1
        "2004BA6044900-----4166---ACEX01CHKDPDBALTEST0112345214090047 EVERETT J PRESCOTT INC.                   10.01D2006-01-05ABCDEFGHIJ----------12345678                             ",
        "2004BA6044900-----4166---ACEX02CHKDPDBALTEST0212345214090047 EVERETT J PRESCOTT INC.                   20.02D2006-01-05ABCDEFGHIJ----------12345678                             ",
        "2004BA6044900-----4166---ACEX03CHKDPDBALTEST0312345214090047 EVERETT J PRESCOTT INC.                   30.03D2006-01-05ABCDEFGHIJ----------12345678                             ",
        "2004BA6044900-----4166---ACEX04CHKDPDBALTEST0412345214090047 EVERETT J PRESCOTT INC.                   40.04D2006-01-05ABCDEFGHIJ----------12345678                             ",
        "2004BA6044900-----4166---ACEX05CHKDPDBALTEST0512345214090047 EVERETT J PRESCOTT INC.                   50.05D2006-01-05ABCDEFGHIJ----------12345678                             ",
        "2004BA6044900-----4166---ACEX06CHKDPDBALTEST0612345214090047 EVERETT J PRESCOTT INC.                   60.06D2006-01-05ABCDEFGHIJ----------12345678                             ",
        "2004BA6044900-----4166---ACEX07CHKDPDBALTEST0712345214090047 EVERETT J PRESCOTT INC.                   70.07D2006-01-05ABCDEFGHIJ----------12345678                             ",
        "2004BA6044900-----4166---ACEX08CHKDPDBALTEST0812345214090047 EVERETT J PRESCOTT INC.                   80.08D2006-01-05ABCDEFGHIJ----------12345678                             ",
        "2004BA6044900-----4166---ACEX09CHKDPDBALTEST0912345214090047 EVERETT J PRESCOTT INC.                   90.09D2006-01-05ABCDEFGHIJ----------12345678                             ",
        "2004BA6044900-----4166---ACEX10CHKDPDBALTEST1012345214090047 EVERETT J PRESCOTT INC.                  100.10D2006-01-05ABCDEFGHIJ----------12345678                             ",
        "2004BA6044900-----4166---ACEX11CHKDPDBALTEST1112345214090047 EVERETT J PRESCOTT INC.                  110.11D2006-01-05ABCDEFGHIJ----------12345678                             ",
        "2004BA6044900-----4166---ACEX12CHKDPDBALTEST1212345214090047 EVERETT J PRESCOTT INC.                  120.12D2006-01-05ABCDEFGHIJ----------12345678                             ",
        "2004BA6044900-----4166---ACEX13CHKDPDBALTEST1212345214090047 EVERETT J PRESCOTT INC.                  130.13D2006-01-05ABCDEFGHIJ----------12345678                             ",
        "2004BA6044900-----4166---ACEXABCHKDPDBALTEST1212345214090047 EVERETT J PRESCOTT INC.                  140.14D2006-01-05ABCDEFGHIJ----------12345678                             ",
        "2004BA6044900-----4166---ACEXBBCHKDPDBALTEST1212345214090047 EVERETT J PRESCOTT INC.                  150.15D2006-01-05ABCDEFGHIJ----------12345678                             ",
        "2004BA6044900-----4166---ACEXCBCHKDPDBALTEST1212345214090047 EVERETT J PRESCOTT INC.                  160.16D2006-01-05ABCDEFGHIJ----------12345678                             "
    };

    EntryHolder[] outputTransactions = new EntryHolder[inputTransactions.length * 2];
    for (int i = 0; i < inputTransactions.length; i++) {
      outputTransactions[i] = new EntryHolder(OriginEntrySource.SCRUBBER_VALID,inputTransactions[i]);
      outputTransactions[i + inputTransactions.length] = new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,inputTransactions[i]);
    }

    clearOriginEntryTables();
    clearGlBalanceTable();
    loadInputTransactions(OriginEntrySource.SCRUBBER_VALID,inputTransactions);

    posterService.postMainEntries();

    assertOriginEntries(3,outputTransactions);

    List balances = unitTestSqlDao.sqlSelect("select * from gl_balance_t");
    assertEquals("Should be 1 row",1,balances.size());
    Map balance = (Map)balances.get(0);

    BigDecimal a = (BigDecimal)balance.get("UNIV_FISCAL_YR");
    assertEquals("UNIV_FISCAL_YR is wrong",2004,a.intValue());
    assertEquals("FIN_COA_CD is wrong","BA",balance.get("FIN_COA_CD"));
    assertEquals("ACCOUNT_NBR is wrong","6044900",balance.get("ACCOUNT_NBR"));
    assertEquals("SUB_ACCT_NBR is wrong","-----",balance.get("SUB_ACCT_NBR"));
    assertEquals("FIN_OBJECT_CD is wrong","4166",balance.get("FIN_OBJECT_CD"));
    assertEquals("FIN_SUB_OBJ_CD is wrong","---",balance.get("FIN_SUB_OBJ_CD"));
    assertEquals("FIN_BALANCE_TYP_CD is wrong","AC",balance.get("FIN_BALANCE_TYP_CD"));
    assertEquals("FIN_OBJ_TYP_CD is wrong","EX",balance.get("FIN_OBJ_TYP_CD"));
    assertEquals("ACLN_ANNL_BAL_AMT is wrong",140.14,getAmount(balance,"ACLN_ANNL_BAL_AMT"),0.01);
    assertEquals("FIN_BEG_BAL_LN_AMT is wrong",150.15,getAmount(balance,"FIN_BEG_BAL_LN_AMT"),0.01);
    assertEquals("CONTR_GR_BB_AC_AMT is wrong",160.16,getAmount(balance,"CONTR_GR_BB_AC_AMT"),0.01);    
    assertEquals("MO1_ACCT_LN_AMT is wrong",10.01,getAmount(balance,"MO1_ACCT_LN_AMT"),0.01);
    assertEquals("MO2_ACCT_LN_AMT is wrong",20.02,getAmount(balance,"MO2_ACCT_LN_AMT"),0.01);
    assertEquals("MO3_ACCT_LN_AMT is wrong",30.03,getAmount(balance,"MO3_ACCT_LN_AMT"),0.01);
    assertEquals("MO4_ACCT_LN_AMT is wrong",40.04,getAmount(balance,"MO4_ACCT_LN_AMT"),0.01);
    assertEquals("MO5_ACCT_LN_AMT is wrong",50.05,getAmount(balance,"MO5_ACCT_LN_AMT"),0.01);
    assertEquals("MO6_ACCT_LN_AMT is wrong",60.06,getAmount(balance,"MO6_ACCT_LN_AMT"),0.01);
    assertEquals("MO7_ACCT_LN_AMT is wrong",70.07,getAmount(balance,"MO7_ACCT_LN_AMT"),0.01);
    assertEquals("MO8_ACCT_LN_AMT is wrong",80.08,getAmount(balance,"MO8_ACCT_LN_AMT"),0.01);
    assertEquals("MO9_ACCT_LN_AMT is wrong",90.09,getAmount(balance,"MO9_ACCT_LN_AMT"),0.01);
    assertEquals("MO10_ACCT_LN_AMT is wrong",100.10,getAmount(balance,"MO10_ACCT_LN_AMT"),0.01);
    assertEquals("MO11_ACCT_LN_AMT is wrong",110.11,getAmount(balance,"MO11_ACCT_LN_AMT"),0.01);
    assertEquals("MO12_ACCT_LN_AMT is wrong",120.12,getAmount(balance,"MO12_ACCT_LN_AMT"),0.01);
    assertEquals("MO13_ACCT_LN_AMT is wrong",130.13,getAmount(balance,"MO13_ACCT_LN_AMT"),0.01);

    String[] inputTransactions2 = {
        "2004BA6044900-----4166---ACEX01CHKDPDBALTEST0112345214090047 EVERETT J PRESCOTT INC.                    0.01C2006-01-05ABCDEFGHIJ----------12345678                             ",
        "2004BA6044900-----4166---ACEX02CHKDPDBALTEST0212345214090047 EVERETT J PRESCOTT INC.                    0.02C2006-01-05ABCDEFGHIJ----------12345678                             ",
        "2004BA6044900-----4166---ACEX03CHKDPDBALTEST0312345214090047 EVERETT J PRESCOTT INC.                    0.03C2006-01-05ABCDEFGHIJ----------12345678                             ",
        "2004BA6044900-----4166---ACEX04CHKDPDBALTEST0412345214090047 EVERETT J PRESCOTT INC.                    0.04C2006-01-05ABCDEFGHIJ----------12345678                             ",
        "2004BA6044900-----4166---ACEX05CHKDPDBALTEST0512345214090047 EVERETT J PRESCOTT INC.                    0.05C2006-01-05ABCDEFGHIJ----------12345678                             ",
        "2004BA6044900-----4166---ACEX06CHKDPDBALTEST0612345214090047 EVERETT J PRESCOTT INC.                    0.06C2006-01-05ABCDEFGHIJ----------12345678                             ",
        "2004BA6044900-----4166---ACEX07CHKDPDBALTEST0712345214090047 EVERETT J PRESCOTT INC.                    0.07C2006-01-05ABCDEFGHIJ----------12345678                             ",
        "2004BA6044900-----4166---ACEX08CHKDPDBALTEST0812345214090047 EVERETT J PRESCOTT INC.                    0.08C2006-01-05ABCDEFGHIJ----------12345678                             ",
        "2004BA6044900-----4166---ACEX09CHKDPDBALTEST0912345214090047 EVERETT J PRESCOTT INC.                    0.09C2006-01-05ABCDEFGHIJ----------12345678                             ",
        "2004BA6044900-----4166---ACEX10CHKDPDBALTEST1012345214090047 EVERETT J PRESCOTT INC.                    0.10C2006-01-05ABCDEFGHIJ----------12345678                             ",
        "2004BA6044900-----4166---ACEX11CHKDPDBALTEST1112345214090047 EVERETT J PRESCOTT INC.                    0.11C2006-01-05ABCDEFGHIJ----------12345678                             ",
        "2004BA6044900-----4166---ACEX12CHKDPDBALTEST1212345214090047 EVERETT J PRESCOTT INC.                    0.12C2006-01-05ABCDEFGHIJ----------12345678                             ",
        "2004BA6044900-----4166---ACEX13CHKDPDBALTEST1212345214090047 EVERETT J PRESCOTT INC.                    0.13C2006-01-05ABCDEFGHIJ----------12345678                             ",
    };

    EntryHolder[] outputTransactions2 = new EntryHolder[inputTransactions2.length * 2];
    for (int i = 0; i < inputTransactions2.length; i++) {
      outputTransactions2[i] = new EntryHolder(OriginEntrySource.SCRUBBER_VALID,inputTransactions2[i]);
      outputTransactions2[i + inputTransactions2.length] = new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,inputTransactions2[i]);
    }

    clearOriginEntryTables();
    loadInputTransactions(OriginEntrySource.SCRUBBER_VALID,inputTransactions2);

    posterService.postMainEntries();

    assertOriginEntries(3,outputTransactions2);

    balances = unitTestSqlDao.sqlSelect("select * from gl_balance_t");
    assertEquals("Should be 1 row",1,balances.size());
    balance = (Map)balances.get(0);

    a = (BigDecimal)balance.get("UNIV_FISCAL_YR");
    assertEquals("2 UNIV_FISCAL_YR is wrong",2004,a.intValue());
    assertEquals("2 FIN_COA_CD is wrong","BA",balance.get("FIN_COA_CD"));
    assertEquals("2 ACCOUNT_NBR is wrong","6044900",balance.get("ACCOUNT_NBR"));
    assertEquals("2 SUB_ACCT_NBR is wrong","-----",balance.get("SUB_ACCT_NBR"));
    assertEquals("2 FIN_OBJECT_CD is wrong","4166",balance.get("FIN_OBJECT_CD"));
    assertEquals("2 FIN_SUB_OBJ_CD is wrong","---",balance.get("FIN_SUB_OBJ_CD"));
    assertEquals("2 FIN_BALANCE_TYP_CD is wrong","AC",balance.get("FIN_BALANCE_TYP_CD"));
    assertEquals("2 FIN_OBJ_TYP_CD is wrong","EX",balance.get("FIN_OBJ_TYP_CD"));
    assertEquals("2 ACLN_ANNL_BAL_AMT is wrong",140.14,getAmount(balance,"ACLN_ANNL_BAL_AMT"),0.01);
    assertEquals("2 FIN_BEG_BAL_LN_AMT is wrong",150.15,getAmount(balance,"FIN_BEG_BAL_LN_AMT"),0.01);
    assertEquals("2 CONTR_GR_BB_AC_AMT is wrong",160.16,getAmount(balance,"CONTR_GR_BB_AC_AMT"),0.01);
    assertEquals("2 MO1_ACCT_LN_AMT is wrong",10.00,getAmount(balance,"MO1_ACCT_LN_AMT"),0.01);
    assertEquals("2 MO2_ACCT_LN_AMT is wrong",20.00,getAmount(balance,"MO2_ACCT_LN_AMT"),0.01);
    assertEquals("2 MO3_ACCT_LN_AMT is wrong",30.00,getAmount(balance,"MO3_ACCT_LN_AMT"),0.01);
    assertEquals("2 MO4_ACCT_LN_AMT is wrong",40.00,getAmount(balance,"MO4_ACCT_LN_AMT"),0.01);
    assertEquals("2 MO5_ACCT_LN_AMT is wrong",50.00,getAmount(balance,"MO5_ACCT_LN_AMT"),0.01);
    assertEquals("2 MO6_ACCT_LN_AMT is wrong",60.00,getAmount(balance,"MO6_ACCT_LN_AMT"),0.01);
    assertEquals("2 MO7_ACCT_LN_AMT is wrong",70.00,getAmount(balance,"MO7_ACCT_LN_AMT"),0.01);
    assertEquals("2 MO8_ACCT_LN_AMT is wrong",80.00,getAmount(balance,"MO8_ACCT_LN_AMT"),0.01);
    assertEquals("2 MO9_ACCT_LN_AMT is wrong",90.00,getAmount(balance,"MO9_ACCT_LN_AMT"),0.01);
    assertEquals("2 MO10_ACCT_LN_AMT is wrong",100.00,getAmount(balance,"MO10_ACCT_LN_AMT"),0.01);
    assertEquals("2 MO11_ACCT_LN_AMT is wrong",110.00,getAmount(balance,"MO11_ACCT_LN_AMT"),0.01);
    assertEquals("2 MO12_ACCT_LN_AMT is wrong",120.00,getAmount(balance,"MO12_ACCT_LN_AMT"),0.01);
    assertEquals("2 MO13_ACCT_LN_AMT is wrong",130.00,getAmount(balance,"MO13_ACCT_LN_AMT"),0.01);
  }

  public void testPostEncumbrance() throws Exception {
    LOG.debug("testPostEncumbrance() started");

    String[] inputTransactions = {
        // 23456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890
        //         1         2         3         4         5         6         7         8         9         0         1         2         3         4         5         6         7
        //                                                                                                   1
        "2004BA6044900-----4166---IEEX06CHKDPDENCTEST0112345214090047 EVERETT J PRESCOTT INC.                  100.01D2006-01-05ABCDEFGHIJ----------12345678                         D    ",
        "2004BA6044900-----5215---IEEX06CHKDPDENCTEST0112345214090047 EVERETT J PRESCOTT INC.                  200.02D2006-01-05ABCDEFGHIJ----------12345678                         D    ",
        "2004BA6044900-----4166---IEEX06CHKDPDENCTEST0212345214090047 EVERETT J PRESCOTT INC.                   50.00C2006-01-05ABCDEFGHIJ----------12345678CHKDPDENCTEST01          R    ",
        "2004BA6044900-----5215---IEEX06CHKDPDENCTEST0212345214090047 EVERETT J PRESCOTT INC.                   60.00C2006-01-05ABCDEFGHIJ----------12345678CHKDPDENCTEST01          R    ",
        "2004BA6044900-----5215---ACEX06CHKDPDENCTEST0212345214090047 EVERETT J PRESCOTT INC.                   60.00C2006-01-05ABCDEFGHIJ----------12345678                              ",
    };

    EntryHolder[] outputTransactions = new EntryHolder[] {
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BA6044900-----4166---IEEX06CHKDPDENCTEST0112345214090047 EVERETT J PRESCOTT INC.                  100.01D2006-01-05ABCDEFGHIJ----------12345678                         D    "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BA6044900-----5215---IEEX06CHKDPDENCTEST0112345214090047 EVERETT J PRESCOTT INC.                  200.02D2006-01-05ABCDEFGHIJ----------12345678                         D    "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BA6044900-----4166---IEEX06CHKDPDENCTEST0212345214090047 EVERETT J PRESCOTT INC.                   50.00C2006-01-05ABCDEFGHIJ----------12345678CHKDPDENCTEST01          R    "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BA6044900-----5215---IEEX06CHKDPDENCTEST0212345214090047 EVERETT J PRESCOTT INC.                   60.00C2006-01-05ABCDEFGHIJ----------12345678CHKDPDENCTEST01          R    "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BA6044900-----5215---ACEX06CHKDPDENCTEST0212345214090047 EVERETT J PRESCOTT INC.                   60.00C2006-01-05ABCDEFGHIJ----------12345678                              "),

            new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,"2004BA6044900-----4166---IEEX06CHKDPDENCTEST0112345214090047 EVERETT J PRESCOTT INC.                  100.01D2006-01-05ABCDEFGHIJ----------12345678                         D    "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,"2004BA6044900-----5215---IEEX06CHKDPDENCTEST0112345214090047 EVERETT J PRESCOTT INC.                  200.02D2006-01-05ABCDEFGHIJ----------12345678                         D    "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,"2004BA6044900-----5215---ACEX06CHKDPDENCTEST0212345214090047 EVERETT J PRESCOTT INC.                   60.00C2006-01-05ABCDEFGHIJ----------12345678                              "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,"2004BA6044900-----4166---IEEX06CHKDPDENCTEST0212345214090047 EVERETT J PRESCOTT INC.                   50.00C2006-01-05ABCDEFGHIJ----------12345678CHKDPDENCTEST01          R    "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,"2004BA6044900-----5215---IEEX06CHKDPDENCTEST0212345214090047 EVERETT J PRESCOTT INC.                   60.00C2006-01-05ABCDEFGHIJ----------12345678CHKDPDENCTEST01          R    "),
    };

    clearOriginEntryTables();
    clearEncumbranceTable();
    loadInputTransactions(OriginEntrySource.SCRUBBER_VALID,inputTransactions);

    posterService.postMainEntries();

    assertOriginEntries(3,outputTransactions);

    List encumbrances = unitTestSqlDao.sqlSelect("select * from gl_encumbrance_t order by fin_object_cd");
    assertEquals("Should be 2 encumbrances",2,encumbrances.size());
    Map enc4166 = (Map)encumbrances.get(0);
    Map enc5215 = (Map)encumbrances.get(1);

    BigDecimal a = (BigDecimal)enc4166.get("UNIV_FISCAL_YR");
    assertEquals("UNIV_FISCAL_YR is wrong",2004,a.intValue());
    assertEquals("FIN_COA_CD is wrong","BA",enc4166.get("FIN_COA_CD"));
    assertEquals("ACCOUNT_NBR is wrong","6044900",enc4166.get("ACCOUNT_NBR"));
    assertEquals("SUB_ACCT_NBR is wrong","-----",enc4166.get("SUB_ACCT_NBR"));
    assertEquals("FIN_OBJECT_CD is wrong","4166",enc4166.get("FIN_OBJECT_CD"));
    assertEquals("FIN_SUB_OBJ_CD is wrong","---",enc4166.get("FIN_SUB_OBJ_CD"));
    assertEquals("FIN_BALANCE_TYP_CD is wrong","IE",enc4166.get("FIN_BALANCE_TYP_CD"));
    assertEquals("FDOC_TYP_CD is wrong","CHKD",enc4166.get("FDOC_TYP_CD"));
    assertEquals("FS_ORIGIN_CD is wrong","PD",enc4166.get("FS_ORIGIN_CD"));
    assertEquals("FDOC_NBR is wrong","ENCTEST01",enc4166.get("FDOC_NBR"));
    assertEquals("TRN_ENCUM_DESC is wrong","214090047 EVERETT J PRESCOTT INC.",enc4166.get("TRN_ENCUM_DESC"));
    assertEquals("ACLN_ENCUM_AMT is wrong",100.01,getAmount(enc4166,"ACLN_ENCUM_AMT"),0.01);
    assertEquals("ACLN_ENCUM_CLS_AMT is wrong",50,getAmount(enc4166,"ACLN_ENCUM_CLS_AMT"),0.01);

    a = (BigDecimal)enc5215.get("UNIV_FISCAL_YR");
    assertEquals("UNIV_FISCAL_YR is wrong",2004,a.intValue());
    assertEquals("FIN_COA_CD is wrong","BA",enc5215.get("FIN_COA_CD"));
    assertEquals("ACCOUNT_NBR is wrong","6044900",enc5215.get("ACCOUNT_NBR"));
    assertEquals("SUB_ACCT_NBR is wrong","-----",enc5215.get("SUB_ACCT_NBR"));
    assertEquals("FIN_OBJECT_CD is wrong","5215",enc5215.get("FIN_OBJECT_CD"));
    assertEquals("FIN_SUB_OBJ_CD is wrong","---",enc5215.get("FIN_SUB_OBJ_CD"));
    assertEquals("FIN_BALANCE_TYP_CD is wrong","IE",enc5215.get("FIN_BALANCE_TYP_CD"));
    assertEquals("FDOC_TYP_CD is wrong","CHKD",enc5215.get("FDOC_TYP_CD"));
    assertEquals("FS_ORIGIN_CD is wrong","PD",enc5215.get("FS_ORIGIN_CD"));
    assertEquals("FDOC_NBR is wrong","ENCTEST01",enc5215.get("FDOC_NBR"));
    assertEquals("TRN_ENCUM_DESC is wrong","214090047 EVERETT J PRESCOTT INC.",enc5215.get("TRN_ENCUM_DESC"));
    assertEquals("ACLN_ENCUM_AMT is wrong",200.02,getAmount(enc5215,"ACLN_ENCUM_AMT"),0.01);
    assertEquals("ACLN_ENCUM_CLS_AMT is wrong",60,getAmount(enc5215,"ACLN_ENCUM_CLS_AMT"),0.01);
  }

  public void testPostGlAccountBalance() throws Exception {
    LOG.debug("testPostGlAccountBalance() started");

    String[] inputTransactions = {
        "2004BA6044900-----4166---TREX07CHKDPDGLACCTBA112345DESCRIPTION                                        123.45D2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
        "2004BA6044900-----4166---ACEX07CHKDPDGLACCTBA112345DESCRIPTION                                       1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
        "2004BA6044900-----4166---EXEX07CHKDPDGLACCTBA112345DESCRIPTION                                        345.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
        "2004BA6044900-----4166---CBEX07CHKDPDGLACCTBA112345DESCRIPTION                                        222.00 2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
        "2004BA6044900-----4166---ACEX07CHKDPDGLACCTBA112345DESCRIPTION                                          5.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
        "2004BA6044900-----4166---EXEX07CHKDPDGLACCTBA112345DESCRIPTION                                          5.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
        "2004BA6044900-----4166---CBEX07CHKDPDGLACCTBA112345DESCRIPTION                                         -2.00 2006-01-05ABCDEFGHIJ----------12345678                                                                  "
    };

    EntryHolder[] outputTransactions = new EntryHolder[] {
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BA6044900-----4166---TREX07CHKDPDGLACCTBA112345DESCRIPTION                                        123.45D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BA6044900-----4166---ACEX07CHKDPDGLACCTBA112345DESCRIPTION                                       1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BA6044900-----4166---EXEX07CHKDPDGLACCTBA112345DESCRIPTION                                        345.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BA6044900-----4166---CBEX07CHKDPDGLACCTBA112345DESCRIPTION                                        222.00 2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BA6044900-----4166---ACEX07CHKDPDGLACCTBA112345DESCRIPTION                                          5.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BA6044900-----4166---EXEX07CHKDPDGLACCTBA112345DESCRIPTION                                          5.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BA6044900-----4166---CBEX07CHKDPDGLACCTBA112345DESCRIPTION                                         -2.00 2006-01-05ABCDEFGHIJ----------12345678                                                                  "),

            new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,"2004BA6044900-----4166---ACEX07CHKDPDGLACCTBA112345DESCRIPTION                                       1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,"2004BA6044900-----4166---ACEX07CHKDPDGLACCTBA112345DESCRIPTION                                          5.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,"2004BA6044900-----4166---CBEX07CHKDPDGLACCTBA112345DESCRIPTION                                        222.00 2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,"2004BA6044900-----4166---CBEX07CHKDPDGLACCTBA112345DESCRIPTION                                         -2.00 2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,"2004BA6044900-----4166---EXEX07CHKDPDGLACCTBA112345DESCRIPTION                                        345.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,"2004BA6044900-----4166---EXEX07CHKDPDGLACCTBA112345DESCRIPTION                                          5.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,"2004BA6044900-----4166---TREX07CHKDPDGLACCTBA112345DESCRIPTION                                        123.45D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
    };

    clearOriginEntryTables();
    clearGlAccountBalanceTable();
    loadInputTransactions(OriginEntrySource.SCRUBBER_VALID,inputTransactions);
    posterService.postMainEntries();

    assertOriginEntries(3,outputTransactions);

    List balances = unitTestSqlDao.sqlSelect("select * from gl_acct_balances_t");
    assertEquals("Should be 1 balance",1,balances.size());
    Map bal = (Map)balances.get(0);

    BigDecimal a = (BigDecimal)bal.get("UNIV_FISCAL_YR");
    assertEquals("UNIV_FISCAL_YR is wrong",2004,a.intValue());
    assertEquals("FIN_COA_CD is wrong","BA",bal.get("FIN_COA_CD"));
    assertEquals("ACCOUNT_NBR is wrong","6044900",bal.get("ACCOUNT_NBR"));
    assertEquals("SUB_ACCT_NBR is wrong","-----",bal.get("SUB_ACCT_NBR"));
    assertEquals("FIN_OBJECT_CD is wrong","4166",bal.get("FIN_OBJECT_CD"));
    assertEquals("FIN_SUB_OBJ_CD is wrong","---",bal.get("FIN_SUB_OBJ_CD"));
    assertEquals("CURR_BDLN_BAL_AMT is wrong",220.00,getAmount(bal,"CURR_BDLN_BAL_AMT"),0.01);
    assertEquals("ACLN_ACTLS_BAL_AMT is wrong",1440.00,getAmount(bal,"ACLN_ACTLS_BAL_AMT"),0.01);
    assertEquals("ACLN_ENCUM_BAL_AMT is wrong",340.00,getAmount(bal,"ACLN_ENCUM_BAL_AMT"),0.01);
  }

  public void testPostSufficientFundBalances() throws Exception {
    LOG.debug("testPostSufficientFundBalances() started");

    String[] inputTransactions = {
        // N code
        "2004BL2231428-----4166---ACEX07CHKDPDSFN00001112345DESCRIPTION                                      11000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ",

        // A code
        "2004BL2831410-----4166---ACEX07CHKDPDSFA00001112345DESCRIPTION                                      12000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
        "2004BL2831410-----4166---ACEX07CHKDPDSFA00001112345DESCRIPTION                                         15.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
        "2004BL2831410-----4166---EXEX07CHKDPDSFA00001112345DESCRIPTION                                      13000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
        "2004BL2831410-----4166---EXEX07CHKDPDSFA00001112345DESCRIPTION                                         16.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  ",

        // C code
        "2004BL2931477-----4166---EXEX07CHKDPDSFC00001112345DESCRIPTION                                       1000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
        "2004BL2931477-----2401---EXEX07CHKDPDSFC00001112345DESCRIPTION                                       2000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
        "2004BL2931477-----4166---ACEX07CHKDPDSFC00001112345DESCRIPTION                                        200.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
        "2004BL2931477-----2401---ACEX07CHKDPDSFC00001112345DESCRIPTION                                        300.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
        "2004BL2931477-----4166---EXEX07CHKDPDSFC00001112345DESCRIPTION                                          1.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
        "2004BL2931477-----2401---EXEX07CHKDPDSFC00001112345DESCRIPTION                                          2.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
        "2004BL2931477-----4166---ACEX07CHKDPDSFC00001112345DESCRIPTION                                          3.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
        "2004BL2931477-----2401---ACEX07CHKDPDSFC00001112345DESCRIPTION                                          4.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  ",

        // H code?
        "2004BL2931409-----8000---ACEX07CHKDPDSFH00001112345DESCRIPTION                                       9000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
        "2004BL2931409-----8000---ACEX07CHKDPDSFH00001112345DESCRIPTION                                         13.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
        "2004BL2931409-----4166---EXEX07CHKDPDSFH00001112345DESCRIPTION                                      10000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
        "2004BL2931409-----4166---EXEX07CHKDPDSFH00001112345DESCRIPTION                                         14.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  ",

        // O code
        "2004BL2231429-----4166---EXEX07CHKDPDSFO00001112345DESCRIPTION                                       3000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
        "2004BL2231429-----2401---EXEX07CHKDPDSFO00001112345DESCRIPTION                                       4000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
        "2004BL2231429-----4166---ACEX07CHKDPDSFO00001112345DESCRIPTION                                        400.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
        "2004BL2231429-----2401---ACEX07CHKDPDSFO00001112345DESCRIPTION                                        500.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
        "2004BL2231429-----4166---EXEX07CHKDPDSFO00001112345DESCRIPTION                                          5.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
        "2004BL2231429-----2401---EXEX07CHKDPDSFO00001112345DESCRIPTION                                          6.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
        "2004BL2231429-----4166---ACEX07CHKDPDSFO00001112345DESCRIPTION                                          7.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
        "2004BL2231429-----2401---ACEX07CHKDPDSFO00001112345DESCRIPTION                                          8.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  ",

        // L code
        "2004BL2231432-----4166---ACEX07CHKDPDSFL00001112345DESCRIPTION                                       5000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
        "2004BL2231432-----4166---ACEX07CHKDPDSFL00001112345DESCRIPTION                                          9.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
        "2004BL2231432-----2401---ACEX07CHKDPDSFO00001112345DESCRIPTION                                       6000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
        "2004BL2231432-----2401---ACEX07CHKDPDSFO00001112345DESCRIPTION                                         10.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
        "2004BL2231432-----4166---EXEX07CHKDPDSFL00001112345DESCRIPTION                                       7000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
        "2004BL2231432-----4166---EXEX07CHKDPDSFL00001112345DESCRIPTION                                         11.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
        "2004BL2231432-----2401---EXEX07CHKDPDSFO00001112345DESCRIPTION                                       8000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
        "2004BL2231432-----2401---EXEX07CHKDPDSFO00001112345DESCRIPTION                                         12.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
    };

    EntryHolder[] outputTransactions = new EntryHolder[] {
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL2231428-----4166---ACEX07CHKDPDSFN00001112345DESCRIPTION                                      11000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL2831410-----4166---ACEX07CHKDPDSFA00001112345DESCRIPTION                                      12000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL2831410-----4166---ACEX07CHKDPDSFA00001112345DESCRIPTION                                         15.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL2831410-----4166---EXEX07CHKDPDSFA00001112345DESCRIPTION                                      13000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL2831410-----4166---EXEX07CHKDPDSFA00001112345DESCRIPTION                                         16.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL2931477-----4166---EXEX07CHKDPDSFC00001112345DESCRIPTION                                       1000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL2931477-----2401---EXEX07CHKDPDSFC00001112345DESCRIPTION                                       2000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL2931477-----4166---ACEX07CHKDPDSFC00001112345DESCRIPTION                                        200.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL2931477-----2401---ACEX07CHKDPDSFC00001112345DESCRIPTION                                        300.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL2931477-----4166---EXEX07CHKDPDSFC00001112345DESCRIPTION                                          1.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL2931477-----2401---EXEX07CHKDPDSFC00001112345DESCRIPTION                                          2.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL2931477-----4166---ACEX07CHKDPDSFC00001112345DESCRIPTION                                          3.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL2931477-----2401---ACEX07CHKDPDSFC00001112345DESCRIPTION                                          4.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL2931409-----8000---ACEX07CHKDPDSFH00001112345DESCRIPTION                                       9000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL2931409-----8000---ACEX07CHKDPDSFH00001112345DESCRIPTION                                         13.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL2931409-----4166---EXEX07CHKDPDSFH00001112345DESCRIPTION                                      10000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL2931409-----4166---EXEX07CHKDPDSFH00001112345DESCRIPTION                                         14.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL2231429-----4166---EXEX07CHKDPDSFO00001112345DESCRIPTION                                       3000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL2231429-----2401---EXEX07CHKDPDSFO00001112345DESCRIPTION                                       4000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL2231429-----4166---ACEX07CHKDPDSFO00001112345DESCRIPTION                                        400.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL2231429-----2401---ACEX07CHKDPDSFO00001112345DESCRIPTION                                        500.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL2231429-----4166---EXEX07CHKDPDSFO00001112345DESCRIPTION                                          5.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL2231429-----2401---EXEX07CHKDPDSFO00001112345DESCRIPTION                                          6.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL2231429-----4166---ACEX07CHKDPDSFO00001112345DESCRIPTION                                          7.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL2231429-----2401---ACEX07CHKDPDSFO00001112345DESCRIPTION                                          8.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL2231432-----4166---ACEX07CHKDPDSFL00001112345DESCRIPTION                                       5000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL2231432-----4166---ACEX07CHKDPDSFL00001112345DESCRIPTION                                          9.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL2231432-----2401---ACEX07CHKDPDSFO00001112345DESCRIPTION                                       6000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL2231432-----2401---ACEX07CHKDPDSFO00001112345DESCRIPTION                                         10.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL2231432-----4166---EXEX07CHKDPDSFL00001112345DESCRIPTION                                       7000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL2231432-----4166---EXEX07CHKDPDSFL00001112345DESCRIPTION                                         11.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL2231432-----2401---EXEX07CHKDPDSFO00001112345DESCRIPTION                                       8000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL2231432-----2401---EXEX07CHKDPDSFO00001112345DESCRIPTION                                         12.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  "),

            new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,"2004BL2831410-----4166---ACEX07CHKDPDSFA00001112345DESCRIPTION                                      12000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,"2004BL2831410-----4166---ACEX07CHKDPDSFA00001112345DESCRIPTION                                         15.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,"2004BL2831410-----4166---EXEX07CHKDPDSFA00001112345DESCRIPTION                                      13000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,"2004BL2831410-----4166---EXEX07CHKDPDSFA00001112345DESCRIPTION                                         16.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,"2004BL2931477-----2401---ACEX07CHKDPDSFC00001112345DESCRIPTION                                        300.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,"2004BL2931477-----2401---ACEX07CHKDPDSFC00001112345DESCRIPTION                                          4.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,"2004BL2931477-----4166---ACEX07CHKDPDSFC00001112345DESCRIPTION                                        200.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,"2004BL2931477-----4166---ACEX07CHKDPDSFC00001112345DESCRIPTION                                          3.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,"2004BL2931477-----2401---EXEX07CHKDPDSFC00001112345DESCRIPTION                                       2000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,"2004BL2931477-----2401---EXEX07CHKDPDSFC00001112345DESCRIPTION                                          2.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,"2004BL2931477-----4166---EXEX07CHKDPDSFC00001112345DESCRIPTION                                       1000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,"2004BL2931477-----4166---EXEX07CHKDPDSFC00001112345DESCRIPTION                                          1.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,"2004BL2931409-----8000---ACEX07CHKDPDSFH00001112345DESCRIPTION                                       9000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,"2004BL2931409-----8000---ACEX07CHKDPDSFH00001112345DESCRIPTION                                         13.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,"2004BL2931409-----4166---EXEX07CHKDPDSFH00001112345DESCRIPTION                                      10000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,"2004BL2931409-----4166---EXEX07CHKDPDSFH00001112345DESCRIPTION                                         14.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,"2004BL2231432-----4166---ACEX07CHKDPDSFL00001112345DESCRIPTION                                       5000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,"2004BL2231432-----4166---ACEX07CHKDPDSFL00001112345DESCRIPTION                                          9.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,"2004BL2231432-----4166---EXEX07CHKDPDSFL00001112345DESCRIPTION                                       7000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,"2004BL2231432-----4166---EXEX07CHKDPDSFL00001112345DESCRIPTION                                         11.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,"2004BL2231428-----4166---ACEX07CHKDPDSFN00001112345DESCRIPTION                                      11000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,"2004BL2231429-----2401---ACEX07CHKDPDSFO00001112345DESCRIPTION                                        500.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,"2004BL2231429-----2401---ACEX07CHKDPDSFO00001112345DESCRIPTION                                          8.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,"2004BL2231429-----4166---ACEX07CHKDPDSFO00001112345DESCRIPTION                                        400.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,"2004BL2231429-----4166---ACEX07CHKDPDSFO00001112345DESCRIPTION                                          7.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,"2004BL2231429-----2401---EXEX07CHKDPDSFO00001112345DESCRIPTION                                       4000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,"2004BL2231429-----2401---EXEX07CHKDPDSFO00001112345DESCRIPTION                                          6.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  "),            
            new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,"2004BL2231429-----4166---EXEX07CHKDPDSFO00001112345DESCRIPTION                                       3000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,"2004BL2231429-----4166---EXEX07CHKDPDSFO00001112345DESCRIPTION                                          5.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,"2004BL2231432-----2401---ACEX07CHKDPDSFO00001112345DESCRIPTION                                       6000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,"2004BL2231432-----2401---ACEX07CHKDPDSFO00001112345DESCRIPTION                                         10.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,"2004BL2231432-----2401---EXEX07CHKDPDSFO00001112345DESCRIPTION                                       8000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,"2004BL2231432-----2401---EXEX07CHKDPDSFO00001112345DESCRIPTION                                         12.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
    };

    clearOriginEntryTables();
    clearSufficientFundBalanceTable();
    loadInputTransactions(OriginEntrySource.SCRUBBER_VALID,inputTransactions);
    posterService.postMainEntries();

    assertOriginEntries(3,outputTransactions);

    // Check N code accounts
    List balances = unitTestSqlDao.sqlSelect("select * from gl_sf_balances_t where account_nbr = '2231428'");
    assertEquals("Should be 0 balances for code N accounts",0,balances.size());

    // Check A code accounts
    balances = unitTestSqlDao.sqlSelect("select * from gl_sf_balances_t where account_nbr = '2831410'");
    assertEquals("Should be 1 balance for code A accounts",1,balances.size());
    Map bal0 = (Map)balances.get(0);
    assertEquals("A Balance 0 budget amount",0.00,getAmount(bal0,"CURR_BDGT_BAL_AMT"),0.01);
    assertEquals("A Balance 0 actual amount",11985.00,getAmount(bal0,"ACCT_ACTL_XPND_AMT"),0.01);
    assertEquals("A Balance 0 encumbrance amount",12984.00,getAmount(bal0,"ACCT_ENCUM_AMT"),0.01);

    // Check C code accounts
    balances = unitTestSqlDao.sqlSelect("select * from gl_sf_balances_t where account_nbr = '2931477' order by fin_object_cd");
    assertEquals("Should be 2 balances for code C accounts",2,balances.size());
    bal0 = (Map)balances.get(0);
    Map bal1 = (Map)balances.get(1);
    assertEquals("C Balance 0 object code","CMPN",bal0.get("FIN_OBJECT_CD"));
    assertEquals("C Balance 0 budget amount",0.00,getAmount(bal0,"CURR_BDGT_BAL_AMT"),0.01);
    assertEquals("C Balance 0 actual amount",296.00,getAmount(bal0,"ACCT_ACTL_XPND_AMT"),0.01);
    assertEquals("C Balance 0 encumbrance amount",1998.00,getAmount(bal0,"ACCT_ENCUM_AMT"),0.01);

    assertEquals("C Balance 1 object code","GENX",bal1.get("FIN_OBJECT_CD"));
    assertEquals("C Balance 1 budget amount",0.00,getAmount(bal1,"CURR_BDGT_BAL_AMT"),0.01);
    assertEquals("C Balance 1 actual amount",197.00,getAmount(bal1,"ACCT_ACTL_XPND_AMT"),0.01);
    assertEquals("C Balance 1 encumbrance amount",999.00,getAmount(bal1,"ACCT_ENCUM_AMT"),0.01);

    // Check H code accounts
    balances = unitTestSqlDao.sqlSelect("select * from gl_sf_balances_t where account_nbr = '2931409'");
    assertEquals("Should be 1 balance for code H accounts",1,balances.size());
    bal0 = (Map)balances.get(0);
    assertEquals("H Balance 0 budget amount",8987.00,getAmount(bal0,"CURR_BDGT_BAL_AMT"),0.01);
    assertEquals("H Balance 0 actual amount",0.00,getAmount(bal0,"ACCT_ACTL_XPND_AMT"),0.01);
    assertEquals("H Balance 0 encumbrance amount",9986.00,getAmount(bal0,"ACCT_ENCUM_AMT"),0.01);

    // Check O code accounts
    balances = unitTestSqlDao.sqlSelect("select * from gl_sf_balances_t where account_nbr = '2231429' order by fin_object_cd");
    assertEquals("Should be 2 balance for code O accounts",2,balances.size());
    bal0 = (Map)balances.get(0);
    bal1 = (Map)balances.get(1);
    assertEquals("O Balance 0 object code","2401",bal0.get("FIN_OBJECT_CD"));
    assertEquals("O Balance 0 budget amount",0.00,getAmount(bal0,"CURR_BDGT_BAL_AMT"),0.01);
    assertEquals("O Balance 0 actual amount",492.00,getAmount(bal0,"ACCT_ACTL_XPND_AMT"),0.01);
    assertEquals("O Balance 0 encumbrance amount",3994.00,getAmount(bal0,"ACCT_ENCUM_AMT"),0.01);

    assertEquals("O Balance 1 object code","4166",bal1.get("FIN_OBJECT_CD"));
    assertEquals("O Balance 1 budget amount",0.00,getAmount(bal1,"CURR_BDGT_BAL_AMT"),0.01);
    assertEquals("O Balance 1 actual amount",393.00,getAmount(bal1,"ACCT_ACTL_XPND_AMT"),0.01);
    assertEquals("O Balance 1 encumbrance amount",2995.00,getAmount(bal1,"ACCT_ENCUM_AMT"),0.01);

    // Check L code accounts
    balances = unitTestSqlDao.sqlSelect("select * from gl_sf_balances_t where account_nbr = '2231432'");
    assertEquals("Should be 2 balance for code L accounts",2,balances.size());
    bal0 = (Map)balances.get(0);
    bal1 = (Map)balances.get(1);
    assertEquals("L Balance 0 object code","PRIN",bal0.get("FIN_OBJECT_CD"));
    assertEquals("L Balance 0 budget amount",0.00,getAmount(bal0,"CURR_BDGT_BAL_AMT"),0.01);
    assertEquals("L Balance 0 actual amount",4991.00,getAmount(bal0,"ACCT_ACTL_XPND_AMT"),0.01);
    assertEquals("L Balance 0 encumbrance amount",6989.00,getAmount(bal0,"ACCT_ENCUM_AMT"),0.01);

    assertEquals("L Balance 1 object code","PRSA",bal1.get("FIN_OBJECT_CD"));
    assertEquals("L Balance 1 budget amount",0.00,getAmount(bal1,"CURR_BDGT_BAL_AMT"),0.01);
    assertEquals("L Balance 1 actual amount",5990.00,getAmount(bal1,"ACCT_ACTL_XPND_AMT"),0.01);
    assertEquals("L Balance 1 encumbrance amount",7988.00,getAmount(bal1,"ACCT_ENCUM_AMT"),0.01);

    balances = unitTestSqlDao.sqlSelect("select * from gl_sf_balances_t");
    assertEquals("Wrong number of balances in the table",8,balances.size());
  }

  public void testPostExpenditureTransaction() throws Exception {
    LOG.debug("testPostExpenditureTransaction() started");

    String[] inputTransactions = {
        // Not posted because icr type cd = 10
        "2004BL2231499-----4166---ACEX07CHKDPDET000001112345DESCRIPTION                                      11000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ",

        // Not posted because icr type cd is null
        "2004BA9019993-----4166---ACEX07CHKDPDET000001112345DESCRIPTION                                      11000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ",

        // Not posted because the period code is AB, BB or CB
        "2004BL4031407-----4166---ACEXABCHKDPDET000001112345DESCRIPTION                                      12000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
        "2004BL4031407-----4166---ACEXBBCHKDPDET000001112345DESCRIPTION                                          0.12C2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
        "2004BL4031407-----4166---ACEXCBCHKDPDET000001112345DESCRIPTION                                      12000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ",

        // Posted
        "2004BL4031407-----4166---ACEX07CHKDPDET000001112345DESCRIPTION                                      12000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
        "2004BL4031407-----4166---ACEX07CHKDPDET000001112345DESCRIPTION                                          0.12C2006-01-05ABCDEFGHIJ----------12345678                                                                  ",

        // Posted
        "2004BL4131406-----2401---EXEX07CHKDPDET000001112345DESCRIPTION                                         12.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
        "2004BL4131406-----2401---EXEX07CHKDPDET000001112345DESCRIPTION                                          2.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ",

        // Not posted - excluded account
        "2004BL4431406-----2401---EXEX07CHKDPDET000001112345DESCRIPTION                                         33.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
        "2004BL4431406-----2401---EXEX07CHKDPDET000001112345DESCRIPTION                                          4.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ",

        // Not posted - excluded type (23)
        "2004BL4431407-----2401---EXEX07CHKDPDET000001112345DESCRIPTION                                         44.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
        "2004BL4431407-----2401---EXEX07CHKDPDET000001112345DESCRIPTION                                          5.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ",

        // Not posted, CS sub acct
        "2004BL4631464CS0014166---ACEX07CHKDPDET000002112345DESCRIPTION                                         25.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ",

        // Posted, non-CS sub acct
        "2004BL4631464XXX  4166---ACEX07CHKDPDET000002112345DESCRIPTION                                         25.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
        "2004BL4631464XXX  4166---ACEX07CHKDPDET000002112345DESCRIPTION                                          2.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
    };

    EntryHolder[] outputTransactions = new EntryHolder[] {
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL2231499-----4166---ACEX07CHKDPDET000001112345DESCRIPTION                                      11000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BA9019993-----4166---ACEX07CHKDPDET000001112345DESCRIPTION                                      11000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL4031407-----4166---ACEXABCHKDPDET000001112345DESCRIPTION                                      12000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL4031407-----4166---ACEXBBCHKDPDET000001112345DESCRIPTION                                          0.12C2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL4031407-----4166---ACEXCBCHKDPDET000001112345DESCRIPTION                                      12000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL4031407-----4166---ACEX07CHKDPDET000001112345DESCRIPTION                                      12000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL4031407-----4166---ACEX07CHKDPDET000001112345DESCRIPTION                                          0.12C2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL4131406-----2401---EXEX07CHKDPDET000001112345DESCRIPTION                                         12.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL4131406-----2401---EXEX07CHKDPDET000001112345DESCRIPTION                                          2.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL4431406-----2401---EXEX07CHKDPDET000001112345DESCRIPTION                                         33.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL4431406-----2401---EXEX07CHKDPDET000001112345DESCRIPTION                                          4.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL4431407-----2401---EXEX07CHKDPDET000001112345DESCRIPTION                                         44.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL4431407-----2401---EXEX07CHKDPDET000001112345DESCRIPTION                                          5.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL4631464CS0014166---ACEX07CHKDPDET000002112345DESCRIPTION                                         25.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL4631464XXX  4166---ACEX07CHKDPDET000002112345DESCRIPTION                                         25.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL4631464XXX  4166---ACEX07CHKDPDET000002112345DESCRIPTION                                          2.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  "),

            new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,"2004BA9019993-----4166---ACEX07CHKDPDET000001112345DESCRIPTION                                      11000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,"2004BL2231499-----4166---ACEX07CHKDPDET000001112345DESCRIPTION                                      11000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,"2004BL4031407-----4166---ACEX07CHKDPDET000001112345DESCRIPTION                                      12000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,"2004BL4031407-----4166---ACEX07CHKDPDET000001112345DESCRIPTION                                          0.12C2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,"2004BL4031407-----4166---ACEXABCHKDPDET000001112345DESCRIPTION                                      12000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,"2004BL4031407-----4166---ACEXBBCHKDPDET000001112345DESCRIPTION                                          0.12C2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,"2004BL4031407-----4166---ACEXCBCHKDPDET000001112345DESCRIPTION                                      12000.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,"2004BL4131406-----2401---EXEX07CHKDPDET000001112345DESCRIPTION                                         12.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,"2004BL4131406-----2401---EXEX07CHKDPDET000001112345DESCRIPTION                                          2.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,"2004BL4431406-----2401---EXEX07CHKDPDET000001112345DESCRIPTION                                         33.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,"2004BL4431406-----2401---EXEX07CHKDPDET000001112345DESCRIPTION                                          4.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,"2004BL4431407-----2401---EXEX07CHKDPDET000001112345DESCRIPTION                                         44.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,"2004BL4431407-----2401---EXEX07CHKDPDET000001112345DESCRIPTION                                          5.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,"2004BL4631464CS0014166---ACEX07CHKDPDET000002112345DESCRIPTION                                         25.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,"2004BL4631464XXX  4166---ACEX07CHKDPDET000002112345DESCRIPTION                                         25.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,"2004BL4631464XXX  4166---ACEX07CHKDPDET000002112345DESCRIPTION                                          2.00C2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
    };

    clearOriginEntryTables();
    clearExpenditureTable();
    
    // Add sub account for testing
    unitTestSqlDao.sqlCommand("delete from ca_sub_acct_t where fin_coa_cd = 'BL' and account_nbr = '4631464' and sub_acct_nbr = 'XXX'");
    unitTestSqlDao.sqlCommand("delete from ca_a21_sub_acct_t where fin_coa_cd = 'BL' and account_nbr = '4631464' and sub_acct_nbr = 'XXX'");
    unitTestSqlDao.sqlCommand("insert into ca_sub_acct_t (FIN_COA_CD,ACCOUNT_NBR,SUB_ACCT_NBR,OBJ_ID,VER_NBR,SUB_ACCT_NM,SUB_ACCT_ACTV_CD,FIN_RPT_CHRT_CD,FIN_RPT_ORG_CD,FIN_RPT_CD) values ('BL','4631464','XXX',sys_guid(),1,'XXX','N',null,null,null)");
    unitTestSqlDao.sqlCommand("INSERT INTO CA_A21_SUB_ACCT_T (FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, OBJ_ID, VER_NBR, SUB_ACCT_TYP_CD, ICR_TYP_CD, FIN_SERIES_ID, ICR_FIN_COA_CD, ICR_ACCOUNT_NBR, OFF_CMP_CD, CST_SHR_COA_CD, CST_SHRSRCACCT_NBR, CST_SRCSUBACCT_NBR) VALUES ('BL','4631464','XXX',sys_guid(),1,'EX',null,'000',null,null,null,'BL','1031400',null)");

    // Modify account for testing
    unitTestSqlDao.sqlCommand("update ca_account_t set fin_series_id = '11' where account_nbr = '9019993'");

    // Exclude account
    unitTestSqlDao.sqlCommand("delete from ca_icr_excl_acct_t where account_nbr = '4431406'");
    unitTestSqlDao.sqlCommand("insert into CA_ICR_EXCL_ACCT_T (FIN_COA_CD, ACCOUNT_NBR, FIN_OBJ_COA_CD, FIN_OBJECT_CD, OBJ_ID, VER_NBR) values ('BL','4431406','IU','2400',sys_guid(),1)");

    // Exclude type
    unitTestSqlDao.sqlCommand("delete from ca_icr_excl_type_t where acct_icr_typ_cd = '23' and fin_coa_cd = 'BL'");
    unitTestSqlDao.sqlCommand("insert into CA_ICR_EXCL_TYPE_T (ACCT_ICR_TYP_CD, FIN_COA_CD, FIN_OBJECT_CD, OBJ_ID, VER_NBR) values ('23','BL','2401',sys_guid(),1)");
    
    loadInputTransactions(OriginEntrySource.SCRUBBER_VALID,inputTransactions);
    posterService.postMainEntries();

    assertOriginEntries(3,outputTransactions);

    List trans = unitTestSqlDao.sqlSelect("select * from GL_EXPEND_TRN_T order by account_nbr");
    assertEquals("Wrong number of transactions",3,trans.size());
    Map acct4031407 = (Map)trans.get(0);
    assertEquals("Account wrong","4031407",acct4031407.get("ACCOUNT_NBR"));
    assertEquals("Amount wrong",11999.88,getAmount(acct4031407,"ACCT_OBJ_DCST_AMT"),0.01);
    Map acct4131406 = (Map)trans.get(1);
    assertEquals("Account wrong","4131406",acct4131406.get("ACCOUNT_NBR"));
    assertEquals("Amount wrong",-10.00,getAmount(acct4131406,"ACCT_OBJ_DCST_AMT"),0.01);
    Map acct4631464 = (Map)trans.get(2);
    assertEquals("Account wrong","4631464",acct4631464.get("ACCOUNT_NBR"));
    assertEquals("Amount wrong",23.00,getAmount(acct4631464,"ACCT_OBJ_DCST_AMT"),0.01);    
  }

  public void testReversalPoster() throws Exception {
    LOG.debug("testPostReversalPosting() started");

    // First post these entries to the reversal table
    String[] inputTransactions = {
        "2004BL2231408-----5300---ACEX07CHKDPDREVTEST0112345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678               2003-10-01    ",
        "2004BL2231408-----5300---ACEX07CHKDPDREVTEST0212345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678               2003-12-31    ",
        "2004BL2231408-----5300---ACEX07CHKDPDREVTEST0312345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678               2004-01-01    ",
        "2004BL2231408-----5300---ACEX07CHKDPDREVTEST0412345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678               2004-01-02    ",
        "2004BL2231408-----5300---ACEX07CHKDPDREVTEST0512345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678               2005-03-01    "
    };

    clearOriginEntryTables();
    clearReversalTable();
    loadInputTransactions(OriginEntrySource.SCRUBBER_VALID,inputTransactions);
    posterService.postMainEntries();

    unitTestSqlDao.clearCache();

    // Now post the reversal entries
    clearGlEntryTable("BL","2231408");
    posterService.postReversalEntries();

    List results = unitTestSqlDao.sqlSelect("select * from gl_entry_t where account_nbr = '2231408' order by fdoc_nbr");
    assertEquals("Wrong number of posted entries",3,results.size());
    Map row1 = (Map)results.get(0);
    Map row2 = (Map)results.get(1);
    Map row3 = (Map)results.get(2);

    assertEquals("Wrong doc nbr","REVTEST01",row1.get("FDOC_NBR"));
    assertEquals("Wrong doc nbr","REVTEST02",row2.get("FDOC_NBR"));
    assertEquals("Wrong doc nbr","REVTEST03",row3.get("FDOC_NBR"));
  }

  public void testIcrGeneration() throws Exception {
    LOG.debug("testIcrGeneration() started");

    setRollback(false);

    // Load the expenditure table
    unitTestSqlDao.sqlCommand("delete from gl_expend_trn_t");
    
    // This one shouldn't generate any entries
    unitTestSqlDao.sqlCommand("INSERT INTO GL_EXPEND_TRN_T (UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FIN_BALANCE_TYP_CD, FIN_OBJ_TYP_CD, UNIV_FISCAL_PRD_CD, PROJECT_CD, ORG_REFERENCE_ID, OBJ_ID, VER_NBR, ACCT_OBJ_DCST_AMT) VALUES ('2004', 'BL', '1031400', '-----', '4166', '---', 'AC', 'EX', '07', '----------', '12345678', sys_guid(), 1, 10000)");

    // This one is fin_series_id 001 3.13% to 1 account (2 gl entries)
    unitTestSqlDao.sqlCommand("INSERT INTO GL_EXPEND_TRN_T (UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FIN_BALANCE_TYP_CD, FIN_OBJ_TYP_CD, UNIV_FISCAL_PRD_CD, PROJECT_CD, ORG_REFERENCE_ID, OBJ_ID, VER_NBR, ACCT_OBJ_DCST_AMT) VALUES ('2004', 'BL', '4531407', '-----', '4166', '---', 'AC', 'EX', '07', '----------', '12345678', sys_guid(), 1, 10000)");

    // This one is fin_series_id 002 3.8% to 2 accounts (2.0% & 1.8%)
    unitTestSqlDao.sqlCommand("INSERT INTO GL_EXPEND_TRN_T (UNIV_FISCAL_YR, FIN_COA_CD, ACCOUNT_NBR, SUB_ACCT_NBR, FIN_OBJECT_CD, FIN_SUB_OBJ_CD, FIN_BALANCE_TYP_CD, FIN_OBJ_TYP_CD, UNIV_FISCAL_PRD_CD, PROJECT_CD, ORG_REFERENCE_ID, OBJ_ID, VER_NBR, ACCT_OBJ_DCST_AMT) VALUES ('2004', 'BL', '4531408', '-----', '4166', '---', 'AC', 'EX', '07', '----------', '12345678', sys_guid(), 1, 10000)");    

    // Clear origin entry & origin entry group
    clearOriginEntryTables();

    posterService.generateIcrTransactions();

    List results = unitTestSqlDao.sqlSelect("select * from gl_expend_trn_t");
    assertEquals("Should be no expenditure rows left",0,results.size());

    results = unitTestSqlDao.sqlSelect("select * from gl_origin_entry_t order by origin_entry_id");
    assertEquals("Wrong number of transactions generated",10,results.size());

    Map row = (Map)results.get(0);
    assertEquals("0 account number wrong","4531407",row.get("ACCOUNT_NBR"));
    assertEquals("0 object code wrong","5500",row.get("FIN_OBJECT_CD"));
    assertEquals("0 amount wrong",313.0,getAmount(row,"TRN_LDGR_ENTR_AMT"),0.01);

    row = (Map)results.get(1);
    assertEquals("1 account number wrong","4531407",row.get("ACCOUNT_NBR"));
    assertEquals("1 object code wrong","8000",row.get("FIN_OBJECT_CD"));
    assertEquals("1 amount wrong",313.0,getAmount(row,"TRN_LDGR_ENTR_AMT"),0.01);
    
    row = (Map)results.get(2);
    assertEquals("2 account number wrong","1023287",row.get("ACCOUNT_NBR"));
    assertEquals("2 object code wrong","1803",row.get("FIN_OBJECT_CD"));
    assertEquals("2 amount wrong",313.0,getAmount(row,"TRN_LDGR_ENTR_AMT"),0.01);
    
    row = (Map)results.get(3);
    assertEquals("3 account number wrong","1023287",row.get("ACCOUNT_NBR"));
    assertEquals("3 object code wrong","8000",row.get("FIN_OBJECT_CD"));
    assertEquals("3 amount wrong",313.0,getAmount(row,"TRN_LDGR_ENTR_AMT"),0.01);

    row = (Map)results.get(4);
    assertEquals("4 account number wrong","4531408",row.get("ACCOUNT_NBR"));
    assertEquals("4 object code wrong","5500",row.get("FIN_OBJECT_CD"));
    assertEquals("4 amount wrong",380.0,getAmount(row,"TRN_LDGR_ENTR_AMT"),0.01);

    row = (Map)results.get(5);
    assertEquals("5 account number wrong","4531408",row.get("ACCOUNT_NBR"));
    assertEquals("5 object code wrong","8000",row.get("FIN_OBJECT_CD"));
    assertEquals("5 amount wrong",380.0,getAmount(row,"TRN_LDGR_ENTR_AMT"),0.01);

    row = (Map)results.get(6);
    assertEquals("6 account number wrong","1023287",row.get("ACCOUNT_NBR"));
    assertEquals("6 object code wrong","1803",row.get("FIN_OBJECT_CD"));
    assertEquals("6 amount wrong",200.0,getAmount(row,"TRN_LDGR_ENTR_AMT"),0.01);

    row = (Map)results.get(7);
    assertEquals("7 account number wrong","1023287",row.get("ACCOUNT_NBR"));
    assertEquals("7 object code wrong","8000",row.get("FIN_OBJECT_CD"));
    assertEquals("7 amount wrong",200.0,getAmount(row,"TRN_LDGR_ENTR_AMT"),0.01);

    row = (Map)results.get(8);
    assertEquals("8 account number wrong","1031400",row.get("ACCOUNT_NBR"));
    assertEquals("8 object code wrong","1803",row.get("FIN_OBJECT_CD"));
    assertEquals("8 amount wrong",180.0,getAmount(row,"TRN_LDGR_ENTR_AMT"),0.01);

    row = (Map)results.get(9);
    assertEquals("9 account number wrong","1031400",row.get("ACCOUNT_NBR"));
    assertEquals("9 object code wrong","8000",row.get("FIN_OBJECT_CD"));
    assertEquals("9 amount wrong",180.0,getAmount(row,"TRN_LDGR_ENTR_AMT"),0.01);
  }

  private double getAmount(Map map,String field) {
    BigDecimal amt = (BigDecimal)map.get(field);
    if ( amt == null ) {
      return Double.NaN;
    } else {
      return amt.doubleValue();
    }
  }
}
