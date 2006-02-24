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
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.gl.TestDateTimeService;
import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.OriginEntrySource;
import org.kuali.module.gl.dao.OriginEntryDao;
import org.kuali.module.gl.dao.OriginEntryGroupDao;
import org.kuali.module.gl.dao.UnitTestSqlDao;
import org.kuali.test.KualiTestBaseWithSpringOnly;
import org.springframework.beans.factory.BeanFactory;

public class PosterServiceTest extends KualiTestBaseWithSpringOnly {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PosterServiceTest.class);

  private BeanFactory beanFactory;
  private PosterService posterService;
  private TestDateTimeService dateTimeService;
  private UnitTestSqlDao unitTestSqlDao = null;
  private OriginEntryDao originEntryDao = null;
  private OriginEntryGroupDao originEntryGroupDao = null;
  private Date d = null;

  protected void setUp() throws Exception {
    super.setUp();

    beanFactory = SpringServiceLocator.getBeanFactory();

    Calendar c = Calendar.getInstance();
    c.set(Calendar.DAY_OF_MONTH,1);
    c.set(Calendar.MONTH,Calendar.JANUARY);
    c.set(Calendar.YEAR,2004);
    d = c.getTime();

    dateTimeService = (TestDateTimeService)beanFactory.getBean("testDateTimeService");
    dateTimeService.currentDate = d;
    posterService = (PosterService)beanFactory.getBean("glPosterService");

    // get the sql DAO
    unitTestSqlDao = (UnitTestSqlDao) beanFactory.getBean("glUnitTestSqlDao");

    // Origin Entry DAO's
    originEntryDao = (OriginEntryDao) beanFactory.getBean("glOriginEntryDao");
    originEntryGroupDao = (OriginEntryGroupDao) beanFactory.getBean("glOriginEntryGroupDao");
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
     */
    String[] inputTransactions = {
        "2004XX6044900-----5300---ACEE07CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
        "2004BA9999999-----8000---ACAS07CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00C2006-01-05ABCDEFGHIG----------12345678                                                                  ",
        "2004BA6044900-----8000---ACZZ07CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00C2006-01-05ABCDEFGHIG----------12345678                                                                  ",
        "2004BA6044900-----8000---ZZEE07CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00C2006-01-05ABCDEFGHIG----------12345678                                                                  ",
        "9999BA6044900-----5300---ACEE07CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
        "2004BA6044900-----5300---ACEE07CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00 2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
        "2004BA6044900-----5300---ACEE07CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00X2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
    };

    EntryHolder[] outputTransactions = {
        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,inputTransactions[0]),
        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,inputTransactions[1]),
        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,inputTransactions[2]),
        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,inputTransactions[3]),
        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,inputTransactions[4]),
        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,inputTransactions[5]),
        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,inputTransactions[6]),
        new EntryHolder(OriginEntrySource.MAIN_POSTER_ERROR,inputTransactions[0]),
        new EntryHolder(OriginEntrySource.MAIN_POSTER_ERROR,inputTransactions[1]),
        new EntryHolder(OriginEntrySource.MAIN_POSTER_ERROR,inputTransactions[2]),
        new EntryHolder(OriginEntrySource.MAIN_POSTER_ERROR,inputTransactions[3]),
        new EntryHolder(OriginEntrySource.MAIN_POSTER_ERROR,inputTransactions[4]),
        new EntryHolder(OriginEntrySource.MAIN_POSTER_ERROR,inputTransactions[5]),
        new EntryHolder(OriginEntrySource.MAIN_POSTER_ERROR,inputTransactions[6]),
    };

    clearOriginEntryTables();
    loadInputTransactions(inputTransactions);

    posterService.postMainEntries();

    assertOriginEntries(outputTransactions);
  }

  /**
   * Check GL Entry inserts
   * 
   * @throws Exception
   */
  public void testGlEntryInsert() throws Exception {
    LOG.debug("testGlEntryInsert() started");

    String[] inputTransactions = {
        "2004BA6044900-----5300---ACEX07CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
        "2004BA6044900-----5300---ACEX07CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "
    };

    EntryHolder[] outputTransactions = {
        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,inputTransactions[0]),
        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,inputTransactions[1]),
        new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,inputTransactions[0]),
        new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,inputTransactions[1])
    };

    clearOriginEntryTables();
    clearGlEntryTable();
    loadInputTransactions(inputTransactions);

    posterService.postMainEntries();

    assertOriginEntries(outputTransactions);

    List glEntries = unitTestSqlDao.sqlSelect("select * from gl_entry_t");
    assertEquals("Should be 2 GL entries",2,glEntries.size());
    Map glEntry = (Map)glEntries.get(0);

    BigDecimal ufy = (BigDecimal)glEntry.get("UNIV_FISCAL_YR");
    assertEquals("univ_fiscal_yr wrong",2004,ufy.intValue());
    assertEquals("fin_coa_cd wrong","BA",(String)glEntry.get("FIN_COA_CD"));
    assertEquals("account_nbr wrong","6044900",(String)glEntry.get("ACCOUNT_NBR"));
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
    BigDecimal tlea = (BigDecimal)glEntry.get("TRN_LDGR_ENTR_AMT");
    assertEquals("TRN_LDGR_ENTR_AMT wrong",1445.00,tlea.doubleValue(),0.01);
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
  public void testReversalPosting() throws Exception {
    LOG.debug("testReversalPosting() started");

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
    loadInputTransactions(inputTransactions);

    posterService.postMainEntries();

    assertOriginEntries(outputTransactions);

    List reversalEntries = unitTestSqlDao.sqlSelect("select * from gl_reversal_t");
    assertEquals("Should be 1 reversal row",1,reversalEntries.size());
    Map reversalEntry = (Map)reversalEntries.get(0);

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    assertEquals("FDOC_REVERSAL_DT wrong","2006-03-01",sdf.format((Date)reversalEntry.get("FDOC_REVERSAL_DT")));
  }

  public void xtestPostBalance() {
    LOG.debug("testPostBalance() started");

    String[] inputTransactions = {
        // 23456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890123456789012345678901234567890
        //         1         2         3         4         5         6         7         8         9         0         1         2         3         4         5         6         7
        //                                                                                                   1
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
    clearGlBalanceTable();
    loadInputTransactions(inputTransactions);

    posterService.postMainEntries();

    assertOriginEntries(outputTransactions);
  }

  /**
   *  PostEncumbrance
   *  PostExpenditureTransaction
   *  PostGlAccountBalance
   *  PostSufficientFundBalances
   **/

  /**
   * Check all the entries in gl_origin_entry_t against the data passed in EntryHolder[].  If any of them
   * are different, assert an error.
   * 
   * @param requiredEntries
   */
  private void assertOriginEntries(EntryHolder[] requiredEntries) {
      List groups = unitTestSqlDao.sqlSelect("select * from gl_origin_entry_grp_t order by origin_entry_grp_src_cd");
      assertEquals("Number of groups is wrong", 3, groups.size());

      Collection c = originEntryDao.testingGetAllEntries();
      assertEquals("Wrong number of transactions in Origin Entry",requiredEntries.length,c.size());

      int count = 0;
      for (Iterator iter = c.iterator(); iter.hasNext();) {
        OriginEntry foundTransaction = (OriginEntry)iter.next();

        // Check group
        int group = getGroup(groups,requiredEntries[count].groupCode);
        assertEquals("Group for transaction " + foundTransaction.getEntryId() + " is wrong",group,foundTransaction.getEntryGroupId().intValue());

        // Check transaction - this is done this way so that Anthill prints the two transactions to make
        // resolving the issue easier.
        if ( ! foundTransaction.getLine().trim().equals(requiredEntries[count].transactionLine.trim()) ) {
          System.err.println("Expected transaction: " + requiredEntries[count].transactionLine);
          System.err.println("Found transaction:    " + foundTransaction.getLine());
          fail("Transaction " + foundTransaction.getEntryId() + " doesn't match expected output");
        }
        count++;
      }
  }

  private int getGroup(List groups,String groupCode) {
    for (Iterator iter = groups.iterator(); iter.hasNext();) {
      Map element = (Map)iter.next();

      String sourceCode = (String)element.get("ORIGIN_ENTRY_GRP_SRC_CD");
      if ( groupCode.equals(sourceCode) ) {
        BigDecimal groupId = (BigDecimal)element.get("ORIGIN_ENTRY_GRP_ID");
        return groupId.intValue();
      }
    }
    return -1;
  }

  class EntryHolder {
    public String groupCode;
    public String transactionLine;
    public EntryHolder(String groupCode,String transactionLine) {
      this.groupCode = groupCode;
      this.transactionLine = transactionLine;
    }
  }

  private void loadInputTransactions(String[] transactions) {
    OriginEntryGroup group = createNewGroup(OriginEntrySource.SCRUBBER_VALID);
    loadTransactions(transactions,group);
  }

  private void loadTransactions(String[] transactions, OriginEntryGroup group) {
    for (int i = 0; i < transactions.length; i++) {
        createEntry(transactions[i], group);
    }
  }

  private void clearGlEntryTable() {
    unitTestSqlDao.sqlCommand("delete from gl_entry_t");
  }

  private void clearReversalTable() {
    unitTestSqlDao.sqlCommand("delete from gl_reversal_t");
  }

  private void clearGlBalanceTable() {
    unitTestSqlDao.sqlCommand("delete from gl_balance_t");
  }

  private void clearOriginEntryTables() {
    unitTestSqlDao.sqlCommand("delete from gl_origin_entry_t");
    unitTestSqlDao.sqlCommand("delete from gl_origin_entry_grp_t");
  }

  private OriginEntryGroup createNewGroup(String code) {
    OriginEntryGroup group = new OriginEntryGroup();
    group.setDate(new java.sql.Date(d.getTime()));
    group.setProcess(Boolean.TRUE);
    group.setScrub(Boolean.FALSE);
    group.setValid(Boolean.TRUE);
    group.setSourceCode(code);
    originEntryGroupDao.save(group);
    return group;
  }

  private OriginEntry createEntry(String line, OriginEntryGroup group) {
    OriginEntry entry = new OriginEntry(line);
    entry.setGroup(group);
    originEntryDao.saveOriginEntry(entry);
    return entry;
  }
}
