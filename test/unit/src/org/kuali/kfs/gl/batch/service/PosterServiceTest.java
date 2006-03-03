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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.ojb.broker.query.Query;
import org.apache.ojb.broker.query.QueryBySQL;
import org.apache.ojb.broker.query.SqlCriteria;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.gl.TestDateTimeService;
import org.kuali.module.gl.bo.Entry;
import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.OriginEntrySource;
import org.kuali.module.gl.dao.OriginEntryDao;
import org.kuali.module.gl.dao.OriginEntryGroupDao;
import org.kuali.module.gl.dao.UnitTestSqlDao;
import org.kuali.test.KualiTestBaseWithSpringOnly;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;

public class PosterServiceTest extends KualiTestBaseWithSpringOnly {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PosterServiceTest.class);

  private BeanFactory beanFactory;
  private PosterService posterService;
  private TestDateTimeService dateTimeService;
  private UnitTestSqlDao unitTestSqlDao = null;
  private OriginEntryDao originEntryDao = null;
  private OriginEntryGroupDao originEntryGroupDao = null;
  private Date d = null;

  protected PlatformTransactionManager transactionManager;
  protected TransactionStatus transactionStatus;

  /* (non-Javadoc)
   * @see org.springframework.test.AbstractTransactionalSpringContextTests#onSetUpInTransaction()
   */
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
  
  public void testTrans() throws Exception {
//	  String sql = "INSERT INTO KULDEV.EN_USR_OPTN_T(PRSN_EN_ID, PRSN_OPTN_ID, PRSN_OPTN_VAL, DB_LOCK_VER_NBR) VALUES('a', 'b', 'c', 0)";
//	  Connection con = SpringServiceLocator.getPersistenceService().getPersistenceBroker().serviceConnectionManager().getConnection();
//	  PreparedStatement ps = con.prepareStatement(sql);
//	  int i = ps.executeUpdate(sql);
//	  assertEquals(1, i);
	  
      Collection c = originEntryDao.testingGetAllEntries();

      int count = 0;
      for (Iterator iter = c.iterator(); iter.hasNext();) {
        OriginEntry foundTransaction = (OriginEntry)iter.next();
        foundTransaction.getFinancialDocumentNumber();
      }	  
  }

  /**
   * Check GL Entry inserts
   * 
   * @throws Exception
   */
  public void testPostGlEntry() throws Exception {
    LOG.debug("testPostGlEntry() started");

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

//    List glEntries = unitTestSqlDao.sqlSelect("select * from gl_entry_t");
    Query query = new QueryBySQL(Entry.class, "select * from gl_entry_t");
    List glEntries = (List)SpringServiceLocator.getPersistenceService().getPersistenceBroker().getCollectionByQuery(query);
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
    loadInputTransactions(inputTransactions);

    posterService.postMainEntries();

    assertOriginEntries(outputTransactions);

    List reversalEntries = unitTestSqlDao.sqlSelect("select * from gl_reversal_t");
    assertEquals("Should be 1 reversal row",1,reversalEntries.size());
    Map reversalEntry = (Map)reversalEntries.get(0);

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    assertEquals("FDOC_REVERSAL_DT wrong","2006-03-01",sdf.format((Date)reversalEntry.get("FDOC_REVERSAL_DT")));
  }

  public void xtestPostBalance() throws Exception {
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

    EntryHolder[] outputTransactions = {
        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,inputTransactions[0]),
        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,inputTransactions[1]),
        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,inputTransactions[2]),
        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,inputTransactions[3]),
        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,inputTransactions[4]),
        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,inputTransactions[5]),
        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,inputTransactions[6]),
        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,inputTransactions[7]),
        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,inputTransactions[8]),
        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,inputTransactions[9]),
        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,inputTransactions[10]),
        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,inputTransactions[11]),
        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,inputTransactions[12]),
        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,inputTransactions[13]),
        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,inputTransactions[14]),
        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,inputTransactions[15]),
        new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,inputTransactions[0]),
        new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,inputTransactions[1]),
        new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,inputTransactions[2]),
        new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,inputTransactions[3]),
        new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,inputTransactions[4]),
        new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,inputTransactions[5]),
        new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,inputTransactions[6]),
        new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,inputTransactions[7]),
        new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,inputTransactions[8]),
        new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,inputTransactions[9]),
        new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,inputTransactions[10]),
        new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,inputTransactions[11]),
        new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,inputTransactions[12]),
        new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,inputTransactions[13]),
        new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,inputTransactions[14]),
        new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,inputTransactions[15])
    };

    clearOriginEntryTables();
    clearGlBalanceTable();
    loadInputTransactions(inputTransactions);

    posterService.postMainEntries();

    assertOriginEntries(outputTransactions);

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
    a = (BigDecimal)balance.get("ACLN_ANNL_BAL_AMT");
    assertEquals("ACLN_ANNL_BAL_AMT is wrong",140.14,a.doubleValue(),0.01);
    a = (BigDecimal)balance.get("FIN_BEG_BAL_LN_AMT");
    assertEquals("FIN_BEG_BAL_LN_AMT is wrong",150.15,a.doubleValue(),0.01);
    a = (BigDecimal)balance.get("CONTR_GR_BB_AC_AMT");
    assertEquals("CONTR_GR_BB_AC_AMT is wrong",160.16,a.doubleValue(),0.01);    
    a = (BigDecimal)balance.get("MO1_ACCT_LN_AMT");
    assertEquals("MO1_ACCT_LN_AMT is wrong",10.01,a.doubleValue(),0.01);    
    a = (BigDecimal)balance.get("MO2_ACCT_LN_AMT");
    assertEquals("MO2_ACCT_LN_AMT is wrong",20.02,a.doubleValue(),0.01);    
    a = (BigDecimal)balance.get("MO3_ACCT_LN_AMT");
    assertEquals("MO3_ACCT_LN_AMT is wrong",30.03,a.doubleValue(),0.01);    
    a = (BigDecimal)balance.get("MO4_ACCT_LN_AMT");
    assertEquals("MO4_ACCT_LN_AMT is wrong",40.04,a.doubleValue(),0.01);    
    a = (BigDecimal)balance.get("MO5_ACCT_LN_AMT");
    assertEquals("MO5_ACCT_LN_AMT is wrong",50.05,a.doubleValue(),0.01);    
    a = (BigDecimal)balance.get("MO6_ACCT_LN_AMT");
    assertEquals("MO6_ACCT_LN_AMT is wrong",60.06,a.doubleValue(),0.01);    
    a = (BigDecimal)balance.get("MO7_ACCT_LN_AMT");
    assertEquals("MO7_ACCT_LN_AMT is wrong",70.07,a.doubleValue(),0.01);    
    a = (BigDecimal)balance.get("MO8_ACCT_LN_AMT");
    assertEquals("MO8_ACCT_LN_AMT is wrong",80.08,a.doubleValue(),0.01);    
    a = (BigDecimal)balance.get("MO9_ACCT_LN_AMT");
    assertEquals("MO9_ACCT_LN_AMT is wrong",90.09,a.doubleValue(),0.01);    
    a = (BigDecimal)balance.get("MO10_ACCT_LN_AMT");
    assertEquals("MO10_ACCT_LN_AMT is wrong",100.10,a.doubleValue(),0.01);    
    a = (BigDecimal)balance.get("MO11_ACCT_LN_AMT");
    assertEquals("MO11_ACCT_LN_AMT is wrong",110.11,a.doubleValue(),0.01);    
    a = (BigDecimal)balance.get("MO12_ACCT_LN_AMT");
    assertEquals("MO12_ACCT_LN_AMT is wrong",120.12,a.doubleValue(),0.01);    
    a = (BigDecimal)balance.get("MO13_ACCT_LN_AMT");
    assertEquals("MO13_ACCT_LN_AMT is wrong",130.13,a.doubleValue(),0.01);

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

    EntryHolder[] outputTransactions2 = {
        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,inputTransactions2[0]),
        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,inputTransactions2[1]),
        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,inputTransactions2[2]),
        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,inputTransactions2[3]),
        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,inputTransactions2[4]),
        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,inputTransactions2[5]),
        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,inputTransactions2[6]),
        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,inputTransactions2[7]),
        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,inputTransactions2[8]),
        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,inputTransactions2[9]),
        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,inputTransactions2[10]),
        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,inputTransactions2[11]),
        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,inputTransactions2[12]),
        new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,inputTransactions2[0]),
        new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,inputTransactions2[1]),
        new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,inputTransactions2[2]),
        new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,inputTransactions2[3]),
        new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,inputTransactions2[4]),
        new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,inputTransactions2[5]),
        new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,inputTransactions2[6]),
        new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,inputTransactions2[7]),
        new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,inputTransactions2[8]),
        new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,inputTransactions2[9]),
        new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,inputTransactions2[10]),
        new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,inputTransactions2[11]),
        new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,inputTransactions2[12])
    };

    clearOriginEntryTables();
    loadInputTransactions(inputTransactions2);

    posterService.postMainEntries();

    assertOriginEntries(outputTransactions2);

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
    a = (BigDecimal)balance.get("ACLN_ANNL_BAL_AMT");
    assertEquals("2 ACLN_ANNL_BAL_AMT is wrong",140.14,a.doubleValue(),0.01);
    a = (BigDecimal)balance.get("FIN_BEG_BAL_LN_AMT");
    assertEquals("2 FIN_BEG_BAL_LN_AMT is wrong",150.15,a.doubleValue(),0.01);
    a = (BigDecimal)balance.get("CONTR_GR_BB_AC_AMT");
    assertEquals("2 CONTR_GR_BB_AC_AMT is wrong",160.16,a.doubleValue(),0.01);    
    a = (BigDecimal)balance.get("MO1_ACCT_LN_AMT");
    assertEquals("2 MO1_ACCT_LN_AMT is wrong",10.00,a.doubleValue(),0.01);    
    a = (BigDecimal)balance.get("MO2_ACCT_LN_AMT");
    assertEquals("2 MO2_ACCT_LN_AMT is wrong",20.00,a.doubleValue(),0.01);    
    a = (BigDecimal)balance.get("MO3_ACCT_LN_AMT");
    assertEquals("2 MO3_ACCT_LN_AMT is wrong",30.00,a.doubleValue(),0.01);    
    a = (BigDecimal)balance.get("MO4_ACCT_LN_AMT");
    assertEquals("2 MO4_ACCT_LN_AMT is wrong",40.00,a.doubleValue(),0.01);    
    a = (BigDecimal)balance.get("MO5_ACCT_LN_AMT");
    assertEquals("2 MO5_ACCT_LN_AMT is wrong",50.00,a.doubleValue(),0.01);    
    a = (BigDecimal)balance.get("MO6_ACCT_LN_AMT");
    assertEquals("2 MO6_ACCT_LN_AMT is wrong",60.00,a.doubleValue(),0.01);    
    a = (BigDecimal)balance.get("MO7_ACCT_LN_AMT");
    assertEquals("2 MO7_ACCT_LN_AMT is wrong",70.00,a.doubleValue(),0.01);    
    a = (BigDecimal)balance.get("MO8_ACCT_LN_AMT");
    assertEquals("2 MO8_ACCT_LN_AMT is wrong",80.00,a.doubleValue(),0.01);    
    a = (BigDecimal)balance.get("MO9_ACCT_LN_AMT");
    assertEquals("2 MO9_ACCT_LN_AMT is wrong",90.00,a.doubleValue(),0.01);    
    a = (BigDecimal)balance.get("MO10_ACCT_LN_AMT");
    assertEquals("2 MO10_ACCT_LN_AMT is wrong",100.00,a.doubleValue(),0.01);    
    a = (BigDecimal)balance.get("MO11_ACCT_LN_AMT");
    assertEquals("2 MO11_ACCT_LN_AMT is wrong",110.00,a.doubleValue(),0.01);    
    a = (BigDecimal)balance.get("MO12_ACCT_LN_AMT");
    assertEquals("2 MO12_ACCT_LN_AMT is wrong",120.00,a.doubleValue(),0.01);    
    a = (BigDecimal)balance.get("MO13_ACCT_LN_AMT");
    assertEquals("2 MO13_ACCT_LN_AMT is wrong",130.00,a.doubleValue(),0.01);
  }

  public void testPostEcnumbrance() throws Exception {
    LOG.debug("testPostEcnumbrance() started");

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

    EntryHolder[] outputTransactions = {
        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,inputTransactions[0]),
        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,inputTransactions[1]),
        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,inputTransactions[2]),
        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,inputTransactions[3]),
        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,inputTransactions[4]),
        new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,inputTransactions[0]),
        new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,inputTransactions[1]),
        new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,inputTransactions[2]),
        new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,inputTransactions[3]),
        new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,inputTransactions[4])        
    };

    clearOriginEntryTables();
    clearEncumbranceTable();
    loadInputTransactions(inputTransactions);

    posterService.postMainEntries();

    assertOriginEntries(outputTransactions);

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
    a = (BigDecimal)enc4166.get("ACLN_ENCUM_AMT");
    assertEquals("ACLN_ENCUM_AMT is wrong",100.01,a.doubleValue(),0.01);
    a = (BigDecimal)enc4166.get("ACLN_ENCUM_CLS_AMT");
    assertEquals("ACLN_ENCUM_CLS_AMT is wrong",50,a.doubleValue(),0.01);

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
    a = (BigDecimal)enc5215.get("ACLN_ENCUM_AMT");
    assertEquals("ACLN_ENCUM_AMT is wrong",200.02,a.doubleValue(),0.01);
    a = (BigDecimal)enc5215.get("ACLN_ENCUM_CLS_AMT");
    assertEquals("ACLN_ENCUM_CLS_AMT is wrong",60,a.doubleValue(),0.01);
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

    EntryHolder[] outputTransactions = {
        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,inputTransactions[0]),
        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,inputTransactions[1]),
        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,inputTransactions[2]),
        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,inputTransactions[3]),
        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,inputTransactions[4]),
        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,inputTransactions[5]),
        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,inputTransactions[6]),
        new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,inputTransactions[0]),
        new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,inputTransactions[1]),
        new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,inputTransactions[2]),
        new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,inputTransactions[3]),
        new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,inputTransactions[4]),
        new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,inputTransactions[5]),
        new EntryHolder(OriginEntrySource.MAIN_POSTER_VALID,inputTransactions[6])
    };

    clearOriginEntryTables();
    clearGlAccountBalanceTable();
    loadInputTransactions(inputTransactions);
    posterService.postMainEntries();

    assertOriginEntries(outputTransactions);

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
    a = (BigDecimal)bal.get("CURR_BDLN_BAL_AMT");
    assertEquals("CURR_BDLN_BAL_AMT is wrong",220.00,a.doubleValue(),0.01);
    a = (BigDecimal)bal.get("ACLN_ACTLS_BAL_AMT");
    assertEquals("ACLN_ACTLS_BAL_AMT is wrong",1440.00,a.doubleValue(),0.01);
    a = (BigDecimal)bal.get("ACLN_ENCUM_BAL_AMT");
    assertEquals("ACLN_ENCUM_BAL_AMT is wrong",340.00,a.doubleValue(),0.01);
  }

  /**
   *  PostExpenditureTransaction
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

  private void clearEncumbranceTable() {
    unitTestSqlDao.sqlCommand("delete from gl_encumbrance_t");    
  }

  private void clearGlAccountBalanceTable() {
    unitTestSqlDao.sqlCommand("delete from gl_acct_balances_t");
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