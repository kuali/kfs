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
package org.kuali.module.gl;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.kuali.module.gl.batch.poster.impl.PostBalanceTest;
import org.kuali.module.gl.batch.poster.impl.PostEncumbranceTest;
import org.kuali.module.gl.batch.poster.impl.PostExpenditureTransactionTest;
import org.kuali.module.gl.batch.poster.impl.PostGlAccountBalanceTest;
import org.kuali.module.gl.batch.poster.impl.PostGlEntryTest;
import org.kuali.module.gl.batch.poster.impl.PostReversalTest;
import org.kuali.module.gl.batch.poster.impl.PostSufficientFundBalancesTest;
import org.kuali.module.gl.dao.ojb.TestUnitTestSqlDao;
import org.kuali.module.gl.dao.ojb.TestUniversityDateDao;
import org.kuali.module.gl.service.GeneralLedgerPendingEntryServiceTest;
import org.kuali.module.gl.service.NightlyOutServiceTest;

/**
 * @author jsissom
 *
 */
public class AllTests {

  public AllTests() {
    super();
  }

  public static Test suite() {
    // -------------------------------------------------------
    // NOTE:  Set this to true if you just want fast tests (tests that don't touch the database).
    // Set it to false if you want all tests
    // -------------------------------------------------------
    boolean fastTests = true;

    TestSuite suite = new TestSuite();

    // When you are adding tests, put the test in an if statement checking fastTests if
    // the test you are adding touches the database or workflow.

    // org.kuali.module.gl.batch.poster.impl
    suite.addTestSuite(PostBalanceTest.class);
    suite.addTestSuite(PostEncumbranceTest.class);
    suite.addTestSuite(PostExpenditureTransactionTest.class);
    suite.addTestSuite(PostGlEntryTest.class);
    suite.addTestSuite(PostReversalTest.class);
    suite.addTestSuite(PostSufficientFundBalancesTest.class);
    suite.addTestSuite(PostGlAccountBalanceTest.class);
    
    if ( ! fastTests ) {
      suite.addTestSuite(NightlyOutServiceTest.class);
    }

    // org.kuali.module.gl.dao.ojb
    if ( ! fastTests ) {
      suite.addTestSuite(TestUniversityDateDao.class);
      suite.addTestSuite(TestUnitTestSqlDao.class);
    }

    // org.kuali.module.gl.service
    if ( ! fastTests ) {
      suite.addTestSuite(GeneralLedgerPendingEntryServiceTest.class);
    }

    return suite;
  }

  public static void main(String[] args) {
    junit.textui.TestRunner.run(suite());
  }
}
