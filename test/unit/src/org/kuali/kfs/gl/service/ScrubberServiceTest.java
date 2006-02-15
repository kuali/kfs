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

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.gl.TestDateTimeService;
import org.kuali.module.gl.TestScrubberReport;
import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.OriginEntrySource;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.dao.OriginEntryDao;
import org.kuali.module.gl.dao.OriginEntryGroupDao;
import org.kuali.module.gl.dao.UnitTestSqlDao;
import org.kuali.test.KualiTestBaseWithSpring;
import org.springframework.beans.factory.BeanFactory;

public class ScrubberServiceTest extends KualiTestBaseWithSpring {
  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ScrubberServiceTest.class);

  private BeanFactory beanFactory;
  private ScrubberService scrubberService = null;
  private TestDateTimeService dateTimeService = null;
  private TestScrubberReport scrubberReport = null;
  private UnitTestSqlDao unitTestSqlDao = null;
  private OriginEntryDao originEntryDao = null;
  private OriginEntryGroupDao originEntryGroupDao = null;
  private Date d = null;

  protected void setUp() throws Exception {
    super.setUp();

    LOG.debug("setUp() started");

    beanFactory = SpringServiceLocator.getBeanFactory();
    scrubberService = (ScrubberService)beanFactory.getBean("glScrubberService");

    // Get the test date time service so we can specify the date/time of the run
    d = new Date();
    Calendar c = Calendar.getInstance();
    c.set(Calendar.DAY_OF_MONTH,1);
    c.set(Calendar.MONTH,1);
    c.set(Calendar.YEAR,2004);
    d = c.getTime();
    dateTimeService = (TestDateTimeService)beanFactory.getBean("testDateTimeService");
    dateTimeService.currentDate = d;

    // get the test scrubber report so we can read the summary and error information
    // in the unit test
    scrubberReport = (TestScrubberReport)beanFactory.getBean("testScrubberReport");

    // get the sql DAO
    unitTestSqlDao = (UnitTestSqlDao)beanFactory.getBean("glUnitTestSqlDao");

    // get origin entry 
    originEntryDao = (OriginEntryDao)beanFactory.getBean("glOriginEntryDao");
    originEntryGroupDao = (OriginEntryGroupDao)beanFactory.getBean("glOriginEntryGroupDao");
  }

  /*
  public void testEncumbranceUpdateCodeOnNonEncumbrancePeriod() throws Exception {
	    // Setup
	    String[] inputTransactions = {
	    };
	    String[] outputTransactions = {
	    };
	    
	    // Scrub
	    scrub(inputTransactions);
	    
	    // Do assertions
	    // ...
	    
	    // Report errors
	    reportErrors();
  }

  public void testInvalidEncumbranceUpdateCode() throws Exception {
	    // Setup
	    String[] inputTransactions = {
	    };
	    String[] outputTransactions = {
	    };
	    
	    // Scrub
	    scrub(inputTransactions);
	    
	    // Do assertions
	    // ...
	    
	    // Report errors
	    reportErrors();
  }

  public void testBlankEncumbranceUpdateCodeOnEncumbranceRecord() throws Exception {
	    // Setup
	    String[] inputTransactions = {
	    };
	    String[] outputTransactions = {
	    };
	    
	    // Scrub
	    scrub(inputTransactions);
	    
	    // Do assertions
	    // ...
	    
	    // Report errors
	    reportErrors();
  }

  public void testInvalidReversalDate() throws Exception {
	    // Setup
	    String[] inputTransactions = {
	    };
	    String[] outputTransactions = {
	    };
	    
	    // Scrub
	    scrub(inputTransactions);
	    
	    // Do assertions
	    // ...
	    
	    // Report errors
	    reportErrors();
  }

  public void testBlankReferenceDocumentNumberWithEncumbranceUpdateCodeOfR() throws Exception {
	    // Setup
	    String[] inputTransactions = {
	    };
	    String[] outputTransactions = {
	    };
	    
	    // Scrub
	    scrub(inputTransactions);
	    
	    // Do assertions
	    // ...
	    
	    // Report errors
	    reportErrors();
  }

  public void testReferenceDocumentNumberPresentWithoutOtherFields() throws Exception {
	    // Setup
	    String[] inputTransactions = {
	    };
	    String[] outputTransactions = {
	    };
	    
	    // Scrub
	    scrub(inputTransactions);
	    
	    // Do assertions
	    // ...
	    
	    // Report errors
	    reportErrors();
  }

  public void testBlankReferenceOriginCodeWithEncumbranceUpdateCodeOfR() throws Exception {
	    // Setup
	    String[] inputTransactions = {
	    };
	    String[] outputTransactions = {
	    };
	    
	    // Scrub
	    scrub(inputTransactions);
	    
	    // Do assertions
	    // ...
	    
	    // Report errors
	    reportErrors();
  }

  public void testReferenceOriginCodePresentWithoutOtherReferenceFields() throws Exception {
	    // Setup
	    String[] inputTransactions = {
	    };
	    String[] outputTransactions = {
	    };
	    
	    // Scrub
	    scrub(inputTransactions);
	    
	    // Do assertions
	    // ...
	    
	    // Report errors
	    reportErrors();
  }

  public void testInvalidReferenceOriginCode() throws Exception {
	    // Setup
	    String[] inputTransactions = {
	    };
	    String[] outputTransactions = {
	    };
	    
	    // Scrub
	    scrub(inputTransactions);
	    
	    // Do assertions
	    // ...
	    
	    // Report errors
	    reportErrors();
  }

  public void testBlankReferenceDocumentTypeWithEncumbranceUpdateCodeOfR() throws Exception {
	    // Setup
	    String[] inputTransactions = {
	    };
	    String[] outputTransactions = {
	    };
	    
	    // Scrub
	    scrub(inputTransactions);
	    
	    // Do assertions
	    // ...
	    
	    // Report errors
	    reportErrors();
  }

  public void testReferenceDocumentTypePresentWithoutOtherReferenceFields() throws Exception {
	    // Setup
	    String[] inputTransactions = {
	    };
	    String[] outputTransactions = {
	    };
	    
	    // Scrub
	    scrub(inputTransactions);
	    
	    // Do assertions
	    // ...
	    
	    // Report errors
	    reportErrors();
  }

  public void testInvalidReferenceDocumentType() throws Exception {
	    // Setup
	    String[] inputTransactions = {
	    };
	    String[] outputTransactions = {
	    };
	    
	    // Scrub
	    scrub(inputTransactions);
	    
	    // Do assertions
	    // ...
	    
	    // Report errors
	    reportErrors();
  }

  public void testOrgReferenceId() throws Exception {
	    // Setup
	    String[] inputTransactions = {
	    };
	    String[] outputTransactions = {
	    };
	    
	    // Scrub
	    scrub(inputTransactions);
	    
	    // Do assertions
	    // ...
	    
	    // Report errors
	    reportErrors();
  }

  public void testInvalidProjectCode() throws Exception {
	    // Setup
	    String[] inputTransactions = {
	    };
	    String[] outputTransactions = {
	    };
	    
	    // Scrub
	    scrub(inputTransactions);
	    
	    // Do assertions
	    // ...
	    
	    // Report errors
	    reportErrors();
  }

  public void testInactiveProjectCode() throws Exception {
	    // Setup
	    String[] inputTransactions = {
	    };
	    String[] outputTransactions = {
	    };
	    
	    // Scrub
	    scrub(inputTransactions);
	    
	    // Do assertions
	    // ...
	    
	    // Report errors
	    reportErrors();
  }

  public void testBlankProjectCode() throws Exception {
	    // Setup
	    String[] inputTransactions = {
	    };
	    String[] outputTransactions = {
	    };
	    
	    // Scrub
	    scrub(inputTransactions);
	    
	    // Do assertions
	    // ...
	    
	    // Report errors
	    reportErrors();
  }

  public void testOrgDocumentNumber() throws Exception {
	    // Setup
	    String[] inputTransactions = {
	    };
	    String[] outputTransactions = {
	    };
	    
	    // Scrub
	    scrub(inputTransactions);
	    
	    // Do assertions
	    // ...
	    
	    // Report errors
	    reportErrors();
  }

  public void testInvalidTransactionDate() throws Exception {
	    // Setup
	    String[] inputTransactions = {
	    };
	    String[] outputTransactions = {
	    };
	    
	    // Scrub
	    scrub(inputTransactions);
	    
	    // Do assertions
	    // ...
	    
	    // Report errors
	    reportErrors();
  }

  public void testBlankTransactionDate() throws Exception {
	    // Setup
	    String[] inputTransactions = {
	    };
	    String[] outputTransactions = {
	    };
	    
	    // Scrub
	    scrub(inputTransactions);
	    
	    // Do assertions
	    // ...
	    
	    // Report errors
	    reportErrors();
  }

  public void testInvalidDebitCreditCode() throws Exception {
	    // Setup
	    String[] inputTransactions = {
	    };
	    String[] outputTransactions = {
	    };
	    
	    // Scrub
	    scrub(inputTransactions);
	    
	    // Do assertions
	    // ...
	    
	    // Report errors
	    reportErrors();
  }

  public void testDebitCreditCodeOnTransactionNotRequiringOffset() throws Exception {
	    // Setup
	    String[] inputTransactions = {
	    };
	    String[] outputTransactions = {
	    };
	    
	    // Scrub
	    scrub(inputTransactions);
	    
	    // Do assertions
	    // ...
	    
	    // Report errors
	    reportErrors();
  }

  public void testBlankDebitCreditCodeOnTransactionRequiringOffset() throws Exception {
	    // Setup
	    String[] inputTransactions = {
	    };
	    String[] outputTransactions = {
	    };
	    
	    // Scrub
	    scrub(inputTransactions);
	    
	    // Do assertions
	    // ...
	    
	    // Report errors
	    reportErrors();
  }

  public void testNegativeAmountOnNonBudgetTransaction() throws Exception {
	    // Setup
	    String[] inputTransactions = {
	    };
	    String[] outputTransactions = {
	    };
	    
	    // Scrub
	    scrub(inputTransactions);
	    
	    // Do assertions
	    // ...
	    
	    // Report errors
	    reportErrors();
  }

  public void testBlankTransactionsDescription() throws Exception {
	    // Setup
	    String[] inputTransactions = {
	    };
	    String[] outputTransactions = {
	    };
	    
	    // Scrub
	    scrub(inputTransactions);
	    
	    // Do assertions
	    // ...
	    
	    // Report errors
	    reportErrors();
  }

  public void testSequenceNumbers() throws Exception {
	    // Setup
	    String[] inputTransactions = {
	    };
	    String[] outputTransactions = {
	    };
	    
	    // Scrub
	    scrub(inputTransactions);
	    
	    // Do assertions
	    // ...
	    
	    // Report errors
	    reportErrors();
  }

  public void testBlankDocumentNumber() throws Exception {
	    // Setup
	    String[] inputTransactions = {
	    };
	    String[] outputTransactions = {
	    };
	    
	    // Scrub
	    scrub(inputTransactions);
	    
	    // Do assertions
	    // ...
	    
	    // Report errors
	    reportErrors();
  }

  public void testInvalidOriginCode() throws Exception {
	    // Setup
	    String[] inputTransactions = {
	    };
	    String[] outputTransactions = {
	    };
	    
	    // Scrub
	    scrub(inputTransactions);
	    
	    // Do assertions
	    // ...
	    
	    // Report errors
	    reportErrors();
  }

  public void testBlankOriginCode() throws Exception {
	    // Setup
	    String[] inputTransactions = {
	    };
	    String[] outputTransactions = {
	    };
	    
	    // Scrub
	    scrub(inputTransactions);
	    
	    // Do assertions
	    // ...
	    
	    // Report errors
	    reportErrors();
  }

  public void testInvalidDocumentType() throws Exception {
	    // Setup
	    String[] inputTransactions = {
	    };
	    String[] outputTransactions = {
	    };
	    
	    // Scrub
	    scrub(inputTransactions);
	    
	    // Do assertions
	    // ...
	    
	    // Report errors
	    reportErrors();
  }

  public void testBlankDocumentType() throws Exception {
	    // Setup
	    String[] inputTransactions = {
	    };
	    String[] outputTransactions = {
	    };
	    
	    // Scrub
	    scrub(inputTransactions);
	    
	    // Do assertions
	    // ...
	    
	    // Report errors
	    reportErrors();
  }

  public void testInvalidFiscalPeriod() throws Exception {
	    // Setup
	    String[] inputTransactions = {
	    };
	    String[] outputTransactions = {
	    };
	    
	    // Scrub
	    scrub(inputTransactions);
	    
	    // Do assertions
	    // ...
	    
	    // Report errors
	    reportErrors();
  }
  
  public void testClosedFiscalPeriod() throws Exception {
	    // Setup
	    String[] inputTransactions = {
	    };
	    String[] outputTransactions = {
	    };
	    
	    // Scrub
	    scrub(inputTransactions);
	    
	    // Do assertions
	    // ...
	    
	    // Report errors
	    reportErrors();
  }

  public void testBlankFiscalPeriod() throws Exception {
	    // Setup
	    String[] inputTransactions = {
	    };
	    String[] outputTransactions = {
	    };
	    
	    // Scrub
	    scrub(inputTransactions);
	    
	    // Do assertions
	    // ...
	    
	    // Report errors
	    reportErrors();
  }

  public void testInvalidObjectType() throws Exception {
	    // Setup
	    String[] inputTransactions = {
	    };
	    String[] outputTransactions = {
	    };
	    
	    // Scrub
	    scrub(inputTransactions);
	    
	    // Do assertions
	    // ...
	    
	    // Report errors
	    reportErrors();
  }

  public void testBlankObjectType() throws Exception {
	    // Setup
	    String[] inputTransactions = {
	    };
	    String[] outputTransactions = {
	    };
	    
	    // Scrub
	    scrub(inputTransactions);
	    
	    // Do assertions
	    // ...
	    
	    // Report errors
	    reportErrors();
  }

  public void testInvalidBalanceType() throws Exception {
	    // Setup
	    String[] inputTransactions = {
	    };
	    String[] outputTransactions = {
	    };
	    
	    // Scrub
	    scrub(inputTransactions);
	    
	    // Do assertions
	    // ...
	    
	    // Report errors
	    reportErrors();
  }

  public void testBlankBalanceType() throws Exception {
	    // Setup
	    String[] inputTransactions = {
	    };
	    String[] outputTransactions = {
	    };
	    
	    // Scrub
	    scrub(inputTransactions);
	    
	    // Do assertions
	    // ...
	    
	    // Report errors
	    reportErrors();
  }

  public void testInvalidSubObjectCode() throws Exception {
	    // Setup
	    String[] inputTransactions = {
	    };
	    String[] outputTransactions = {
	    };
	    
	    // Scrub
	    scrub(inputTransactions);
	    
	    // Do assertions
	    // ...
	    
	    // Report errors
	    reportErrors();
  }

  public void testInactiveSubObjectCode() throws Exception {
	    // Setup
	    String[] inputTransactions = {
	    };
	    String[] outputTransactions = {
	    };
	    
	    // Scrub
	    scrub(inputTransactions);
	    
	    // Do assertions
	    // ...
	    
	    // Report errors
	    reportErrors();
  }

  public void testBlankInactiveSubObjectCode() throws Exception {
	    // Setup
	    String[] inputTransactions = {
	    };
	    String[] outputTransactions = {
	    };
	    
	    // Scrub
	    scrub(inputTransactions);
	    
	    // Do assertions
	    // ...
	    
	    // Report errors
	    reportErrors();
  }

  public void testBlankSubObjectCode() throws Exception {
	    // Setup
	    String[] inputTransactions = {
	    };
	    String[] outputTransactions = {
	    };
	    
	    // Scrub
	    scrub(inputTransactions);
	    
	    // Do assertions
	    // ...
	    
	    // Report errors
	    reportErrors();
  }

  public void testInvalidObjectCode() throws Exception {
	    // Setup
	    String[] inputTransactions = {
	    };
	    String[] outputTransactions = {
	    };
	    
	    // Scrub
	    scrub(inputTransactions);
	    
	    // Do assertions
	    // ...
	    
	    // Report errors
	    reportErrors();
  }

  public void testInactiveObjectCode() throws Exception {
	    // Setup
	    String[] inputTransactions = {
	    };
	    String[] outputTransactions = {
	    };
	    
	    // Scrub
	    scrub(inputTransactions);
	    
	    // Do assertions
	    // ...
	    
	    // Report errors
	    reportErrors();
  }

  public void testBlankObjectCode() throws Exception {
	    // Setup
	    String[] inputTransactions = {
	    };
	    String[] outputTransactions = {
	    };
	    
	    // Scrub
	    scrub(inputTransactions);
	    
	    // Do assertions
	    // ...
	    
	    // Report errors
	    reportErrors();
  }

  public void testInvalidSubAccountNumber() throws Exception {
	    // Setup
	    String[] inputTransactions = {
	    };
	    String[] outputTransactions = {
	    };
	    
	    // Scrub
	    scrub(inputTransactions);
	    
	    // Do assertions
	    // ...
	    
	    // Report errors
	    reportErrors();
  }

  public void testInactiveSubAccountNumber() throws Exception {
	    // Setup
	    String[] inputTransactions = {
	    };
	    String[] outputTransactions = {
	    };
	    
	    // Scrub
	    scrub(inputTransactions);
	    
	    // Do assertions
	    // ...
	    
	    // Report errors
	    reportErrors();
  }

  public void testBlankSubAccountNumber() throws Exception {
	    // Setup
	    String[] inputTransactions = {
	    };
	    String[] outputTransactions = {
	    };
	    
	    // Scrub
	    scrub(inputTransactions);
	    
	    // Do assertions
	    // ...
	    
	    // Report errors
	    reportErrors();
  }

  public void testInvalidAccountNumber() throws Exception {
	    // Setup
	    String[] inputTransactions = {
	    };
	    String[] outputTransactions = {
	    };
	    
	    // Scrub
	    scrub(inputTransactions);
	    
	    // Do assertions
	    // ...
	    
	    // Report errors
	    reportErrors();
  }

  public void testClosedAccountNumber() throws Exception {
	    // Setup
	    String[] inputTransactions = {
	    };
	    String[] outputTransactions = {
	    };
	    
	    // Scrub
	    scrub(inputTransactions);
	    
	    // Do assertions
	    // ...
	    
	    // Report errors
	    reportErrors();
  }

  public void testExpiredAccountNumber() throws Exception {
	    // Setup
	    String[] inputTransactions = {
	    };
	    String[] outputTransactions = {
	    };
	    
	    // Scrub
	    scrub(inputTransactions);
	    
	    // Do assertions
	    // ...
	    
	    // Report errors
	    reportErrors();
  }

  public void testBlankAccountNumber() throws Exception {
	    // Setup
	    String[] inputTransactions = {
	    };
	    String[] outputTransactions = {
	    };
	    
	    // Scrub
	    scrub(inputTransactions);
	    
	    // Do assertions
	    // ...
	    
	    // Report errors
	    reportErrors();
  }
  
  public void testInvalidChart() throws Exception {
	    // Setup
	    String[] inputTransactions = {
	    };
	    String[] outputTransactions = {
	    };
	    
	    // Scrub
	    scrub(inputTransactions);
	    
	    // Do assertions
	    // ...
	    
	    // Report errors
	    reportErrors();
  }

  public void testInactiveChart() throws Exception {
	    // Setup
	    String[] inputTransactions = {
	    };
	    String[] outputTransactions = {
	    };
	    
	    // Scrub
	    scrub(inputTransactions);
	    
	    // Do assertions
	    // ...
	    
	    // Report errors
	    reportErrors();
  }

  public void testBlankChart() throws Exception {
	    // Setup
	    String[] inputTransactions = {
	    };
	    String[] outputTransactions = {
	    };
	    
	    // Scrub
	    scrub(inputTransactions);
	    
	    // Do assertions
	    // ...
	    
	    // Report errors
	    reportErrors();
  }
  
  public void testInvalidFiscalYear() throws Exception {
	    // Setup
	    String[] inputTransactions = {
	    };
	    String[] outputTransactions = {
	    };
	    
	    // Scrub
	    scrub(inputTransactions);
	    
	    // Do assertions
	    // ...
	    
	    // Report errors
	    reportErrors();
  }
  */
  
  public void testClosedFiscalYear() throws Exception {
    // Setup
    String[] inputTransactions = {
      "2003BA6044906-----4100---ACEX07TOPSLGCLOSEFISC     CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                          ",
      "2003BA6044906-----9041---ACLI07TOPSLGCLOSEFISC     CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                          "
    };
    String[] outputTransactions = {
      "2003BA6044906-----4100---ACEX07TOPSLGCLOSEFISC     CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                          ",
      "2003BA6044906-----9041---ACLI07TOPSLGCLOSEFISC     CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                          "
    };

    scrub(inputTransactions);

    // Check to see if we got the results we wanted
    List groups = unitTestSqlDao.sqlSelect("select * from gl_origin_entry_grp_t order by origin_entry_grp_src_cd");
    assertEquals("Number of groups is wrong",4,groups.size());

    Collection c = originEntryDao.testingGetAllEntries();
    Iterator iter = c.iterator();

    // Transactions 1 & 2 are the entries we scrubbed
    assertTrue("Not enough transactions 1",iter.hasNext());
    iter.next();
    assertTrue("Not enough transactions 2",iter.hasNext());
    iter.next();

    assertTrue("Not enough transactions 3",iter.hasNext());
    OriginEntry entry = (OriginEntry)iter.next();
    assertEquals("Transaction " + entry.getEntryId() + " wrong",outputTransactions[0].trim(),entry.getLine().trim());

    assertTrue("Not enough transactions 4",iter.hasNext());
    entry = (OriginEntry)iter.next();
    assertEquals("Transaction " + entry.getEntryId() + " wrong",outputTransactions[1].trim(),entry.getLine().trim());

    assertFalse("Too many transactions",iter.hasNext());
    
    reportErrors();
  }
  
  public void testDefaultFiscalYear() throws Exception {
    // Setup
    String[] inputTransactions = {
      "    BA6044900-----5300---ACEE07CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
      "    BA6044900-----8000---ACAS07CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00C2006-01-05ABCDEFGHIG----------12345678                                                                  "
    };
    String[] outputTransactions = {
      "2004BA6044900-----5300---ACEE07CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
      "2004BA6044900-----8000---ACAS07CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00C2006-01-05ABCDEFGHIG----------12345678                                                                  "
    };

    scrub(inputTransactions);

    // Check to see if we got the results we wanted
    List groups = unitTestSqlDao.sqlSelect("select * from gl_origin_entry_grp_t order by origin_entry_grp_src_cd");
    assertEquals("Number of groups is wrong",4,groups.size());

    Collection c = originEntryDao.testingGetAllEntries();
    Iterator iter = c.iterator();

    // Transactions 1 & 2 are the entries we scrubbed
    assertTrue("Not enough transactions 1",iter.hasNext());
    iter.next();
    assertTrue("Not enough transactions 2",iter.hasNext());
    iter.next();

    assertTrue("Not enough transactions 3",iter.hasNext());
    OriginEntry entry = (OriginEntry)iter.next();
//    assertEquals("Transaction " + entry.getEntryId() + " wrong",outputTransactions[0].trim(),entry.getLine().trim());

    assertTrue("Not enough transactions 4",iter.hasNext());
    entry = (OriginEntry)iter.next();
//    assertEquals("Transaction " + entry.getEntryId() + " wrong",outputTransactions[1].trim(),entry.getLine().trim());

    assertFalse("Too many transactions",iter.hasNext());

//     reportErrors();
  }

  private void reportErrors() {
	Map errors = scrubberReport.reportErrors;
	for (Iterator i = errors.keySet().iterator(); i.hasNext();) {
	  Transaction key = (Transaction)i.next();
	  List msgs = (List)errors.get(key);
	  for (Iterator iterator = msgs.iterator(); iterator.hasNext();) {
	    String msg = (String)iterator.next();
	    System.err.println(msg);
	  }
	}
  }

  private void scrub(String[] inputTransactions) {
	clearOriginEntryTables();
	OriginEntryGroup group = createNewGroup(OriginEntrySource.EXTERNAL);
	loadTransactions(inputTransactions,group);
    scrubberService.scrubEntries();
  }
	  
  private void loadTransactions(String[] transactions,OriginEntryGroup group) {
    for (int i = 0; i < transactions.length; i++) {
      createEntry(transactions[i],group);
    }
  }

  private void clearOriginEntryTables() {
    unitTestSqlDao.sqlCommand("delete from gl_origin_entry_t");
    unitTestSqlDao.sqlCommand("delete from gl_origin_entry_grp_t");
  }

  private OriginEntryGroup createNewGroup(String code) {
    OriginEntryGroup group = new OriginEntryGroup();
    group.setDate(new java.sql.Date(d.getTime()));
    group.setProcess(Boolean.TRUE);
    group.setScrub(Boolean.TRUE);
    group.setValid(Boolean.TRUE);
    group.setSourceCode(code);
    originEntryGroupDao.save(group);
    return group;
  }

  private OriginEntry createEntry(String line,OriginEntryGroup group) {
    OriginEntry entry = new OriginEntry(line);
    entry.setGroup(group);
    originEntryDao.saveOriginEntry(entry);
    return entry;
  }
}
