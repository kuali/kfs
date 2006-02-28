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
import org.kuali.test.KualiTestBaseWithSpringOnly;
import org.springframework.beans.factory.BeanFactory;

public class ScrubberServiceTest extends KualiTestBaseWithSpringOnly {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger
            .getLogger(ScrubberServiceTest.class);

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
        scrubberService = (ScrubberService) beanFactory
                .getBean("glScrubberService");

        // Get the test date time service so we can specify the date/time of the run
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.MONTH, Calendar.JANUARY);
        c.set(Calendar.YEAR, 2004);
        d = c.getTime();
        dateTimeService = (TestDateTimeService) beanFactory
                .getBean("testDateTimeService");
        dateTimeService.currentDate = d;

        // get the test scrubber report so we can read the summary and error information
        // in the unit test
        scrubberReport = (TestScrubberReport) beanFactory
                .getBean("testScrubberReport");

        // get the sql DAO
        unitTestSqlDao = (UnitTestSqlDao) beanFactory
                .getBean("glUnitTestSqlDao");

        // get origin entry 
        originEntryDao = (OriginEntryDao) beanFactory
                .getBean("glOriginEntryDao");
        originEntryGroupDao = (OriginEntryGroupDao) beanFactory
                .getBean("glOriginEntryGroupDao");
    }

    public void testNothing() {
      assertTrue(true);
    }

    /*
    public void testOrgReferenceId() throws Exception {
        // FIXME found no input records to test this
        // Setup
        String[] inputTransactions = {};
        String[] outputTransactions = {};

        // Scrub
        scrub(inputTransactions);

        // Do assertions
        // ...

        // Report errors
        reportErrors();
    }

    public void testOrgDocumentNumber() throws Exception {
        // FIXME couldn't find input entries for this test
        // Setup
        String[] inputTransactions = {};
        String[] outputTransactions = {};

        // Scrub
        scrub(inputTransactions);

        // Do assertions
        // ...

        // Report errors
        reportErrors();
    }

    public void testNegativeAmountOnNonBudgetTransaction() throws Exception {
        // Setup
        // FIXME negative amount in input line must be handled
        String[] inputTransactions = {
                "2004BA6044900-----1599---ACIN07TOPSLGNEGAMTNBT     CONCERTO OFFICE PRODUCTS                -0000000000048.53C2006-01-05          ----------                                                                          ",
                "2004BA6044900-----9041---ACLI07TOPSLDNEGAMTNBT     CONCERTO OFFICE PRODUCTS                -0000000000048.53D2006-01-05          ----------                                                                          " };
        String[] outputTransactions = {};

        // Scrub
        scrub(inputTransactions);

        // Do assertions
        // ...

        // Report errors
        reportErrors();
    }

    */

    public void dontRunTestEncumbranceUpdateCodeOnNonEncumbrancePeriod()
            throws Exception {
        // Setup
        String[] inputTransactions = {
        		"2004BL1031420-----4110---ACEX07ID33EUCHECKENCC     NOV-05 IMU Business Office          2224           241.75C2005-11-30          ----------                                 D                                        ",
        		"2004BL1031420-----9892---ACFB07ID33EUCHECKENCC     NOV-05 IMU Business Office          2237           241.75D2005-11-30          ----------                                 D                                        "
        };
        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions);
        assertOriginEntries(outputTransactions);
    }

    public void dontRunTestInvalidEncumbranceUpdateCode() throws Exception {
        // Setup
        String[] inputTransactions = {
                "2004BL1031420-----4110---IEEX07PAYEEUINVALENCC     NOV-05 IMU Business Office          2224           241.75C2005-11-30          ----------                                 X                                        ",
                "2004BL1031420-----9892---IEAS07PAYEEUINVALENCC     NOV-05 IMU Business Office          2237           241.75D2005-11-30          ----------                                 X                                        "
        };
        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions);
        assertOriginEntries(outputTransactions);
    }

    public void dontRunTestBlankEncumbranceUpdateCodeOnEncumbranceRecord()
            throws Exception {
        // Setup
        String[] inputTransactions = {
                "2004BL1031400-----4100---PEEX07TF  01BLANKENCC     Rite Quality Office Supplies Inc.                   94.35D2006-01-05          ----------                                                                          ",
                "2004BL1031400-----9891---PEFB07TF  01BLANKENCC     Rite Quality Office Supplies Inc.                   94.35C2006-01-05          ----------                                                                          "
        };
        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions);
        assertOriginEntries(outputTransactions);
    }

    public void dontRunTestInvalidReversalDate() throws Exception {

        String[] inputTransactions = {
                "2004BA6044913-----1470---ACIN07CR  01INVALREVD     Poplars Garage Fees                                 20.00D2006-01-05          ----------                       2005-02-30                                         ",
                "2004BA6044913-----8000---ACAS07CR  01INVALREVD     TP Generated Offset                                 20.00C2006-01-05          ----------                       2005-12-32                                         "
        };
        
        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions);
        assertOriginEntries(outputTransactions);
    }

    public void testBlankReferenceDocumentNumberWithEncumbranceUpdateCodeOfR()
            throws Exception {

        String[] inputTransactions = {
                "2004BA6044900-----1599---EXIN07TOPSLGBLANKRDOC     CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------        CR  01                   R                                        ",
                "2004BA6044900-----9041---EXLI07TOPSLDBLANKRDOC     CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------        CR  01                   R                                        "
        };
        
        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions);
        assertOriginEntries(outputTransactions);
    }

    public void testReferenceDocumentNumberPresentWithoutOtherFields()
            throws Exception {

        String[] inputTransactions = {
                "2004BA6044906-----5300---ACEE07CHKDPDLONERDOC 12345TEST KUALI SCRUBBER EDITS                         1445.00D2006-01-05ABCDEFGHIJ----------12345678      123456789                                                   ",
                "2004BA6044906-----8000---ACAS07CHKDPDLONERDOC 12345TEST KUALI SCRUBBER EDITS                         1445.00C2006-01-05ABCDEFGHIG----------12345678      123456789                                                   "
        };
        
        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions);
        assertOriginEntries(outputTransactions);
    }

    public void testBlankReferenceOriginCodeWithEncumbranceUpdateCodeOfR()
            throws Exception {

        String[] inputTransactions = {
                "2004BL9120656-----5000---ACEX07INV EUBLANKRORG     BALDWIN WALLACE COLLEGE                           3375.00C2006-01-05          ----------        DI    123456789                                                   ",
                "2004BL9120656-----8000---ACAS07INV EUBLANKRORG     TP Generated Offset                               3375.00D2006-01-05          ----------        DI    123456789                                                   "
        };
        
        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions, true);
        assertOriginEntries(outputTransactions);
    }

    /*
    public void testReferenceOriginCodePresentWithoutOtherReferenceFields()
            throws Exception {

        String[] inputTransactions = {
                "2004BL2231423-----1800---ACIN  CR  PLLONERORG      FRICKA FRACKA                                    45995.84C2006-01-05          ----------            01                                                            ",
                "2004BL2231423-----8000---ACAS  CR  PLLONERORG      TP Generated Offset                              45995.84D2006-01-05          ----------            01                                                            "
        };
        
        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,inputTransactions[1])
        };

        scrub(inputTransactions);
        assertOriginEntries(outputTransactions);
    }
    */

    public void testInvalidReferenceOriginCode() throws Exception {

        String[] inputTransactions = {
                "2004BL2231411-----2400---ACEX07ST  EUINVALRORG     PAYROLL EXPENSE TRANSFERS                          620.00C2006-01-05          ----------        CD  XX123456789                                                   ",
                "2004BL2231411-----8000---ACAS07ST  EUINVALRORG     PAYROLL EXPENSE TRANSFERS                          620.00D2006-01-05          ----------        CD  XX123456789                                                   "
        };
        
        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions);
        assertOriginEntries(outputTransactions);
    }

    public void testBlankReferenceDocumentTypeWithEncumbranceUpdateCodeOfR()
            throws Exception {

        String[] inputTransactions = {
                "2004BL2231408-----4035---ACEX07SB  01BLANKRDTP     Biology Stockroom                                   13.77D2006-01-05          ----------            LG123456789                                                   ",
                "2004BL2231408-----8000---ACAS07SB  01BLANKRDTP     TP Generated Offset                                 13.77C2006-01-05          ----------            LG123456789                                                   "
        };
        
        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions);
        assertOriginEntries(outputTransactions);
    }

    /*
    public void testReferenceDocumentTypePresentWithoutOtherReferenceFields()
            throws Exception {
        
        String[] inputTransactions = {
                "2004BA6044900-----8000---ACAS07IB  01LONERDTP      TP Generated Offset                               1650.00C2006-01-05          ----------        TOPS                                                              ",
                "2004BL6044900-----4866---ACEX07IB  01LONERDTP      Correction to: 01-PU3355206                       1650.00D2006-01-05          ----------        TOPS                                                              "
        };
        
        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions, true);
        assertOriginEntries(outputTransactions);
    }
    */
    
    public void testInvalidReferenceDocumentType() throws Exception {
        String[] inputTransactions = {
                "2004BL1031497-----4190---ACEX07GEC 01INVALRDTP     THOMAS BUSEY/NEWEGG COMPUTERS                       40.72C2006-01-05          ----------        XXXXLG123456789                                                   ",
                "2004BL1031497-----8000---ACAS07GEC 01INVALRDTP     TP Generated Offset                                 40.72D2006-01-05          ----------        XXXXLG123456789                                                   "
        };
        
        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions);
        assertOriginEntries(outputTransactions);
    }

    public void testInvalidProjectCode() throws Exception {
        String[] inputTransactions = {
                "2004BL9120656-----4035---ACEX07CR  01INVALPROJ     pymts recd 12/28/05                                 25.15C2006-01-05          XXXXXXXXX                                                                           ",
                "2004BL9120656-----8000---ACAS07CR  01INVALPROJ     TP Generated Offset                                 25.15D2006-01-05          XXXXXXXXX                                                                           "
        };
        
        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions, true);
        assertOriginEntries(outputTransactions);
    }
    
    /*
    public void testBlankProjectCode() throws Exception {
        // Setup
        String[] inputTransactions = {
                "2004BA6044900-----5000---ACEX07ACHDPDBLANKPROJ     H P PRODUCTS CORPORATION        10252845           744.00D2006-01-05                                                                                              ",          
                "2004BA6044900-----8000---ACAS07ACHDPDBLANKPROJ     H P PRODUCTS CORPORATION        10252882           744.00C2006-01-05                                                                                              "
        };
        String[] outputTransactions = {};

        // Scrub
        scrub(inputTransactions);

        // Do assertions
        // ...

        // Report errors
        reportErrors();
    }
    */
    
    public void testInvalidTransactionDate() throws Exception {
        String[] inputTransactions = {
                "2004BL1031497-----4100---ACEX07PO  LGINVALDATE     Rite Quality Office Supplies Inc.                   43.42D2096-02-11          ----------                                                                          ",
                "2004BL1031497-----9892---ACFB07PO  LGINVALDATE     Rite Quality Office Supplies Inc.                   43.42C1006-12-23          ----------                                                                          "
        };

        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions);
        assertOriginEntries(outputTransactions);
    }
    
    /*
    public void testBlankTransactionDate() throws Exception {
        // Setup
        String[] inputTransactions = {
                "2004BL2231440-----5000---ACEX07PO  LGBLANKDATE     225050007 WILLIAMS DOTSON ASSOCIATES IN           1200.00D                    ----------                                                                          ",
                "2004BL2231440-----9041---ACLI07PO  LGBLANKDATE     225050007 WILLIAMS DOTSON ASSOCIATES IN           1200.00C                    ----------                                                                          "
        };
        String[] outputTransactions = {};

        // Scrub
        scrub(inputTransactions);

        // Do assertions
        // ...

        // Report errors
        reportErrors();
    }
    */
    
    public void testInvalidDebitCreditCode() throws Exception {
        String[] inputTransactions = {
                "2004BL1031420-----4110---ACEX07ID33EUINVALDBCR     NOV-05 IMU Business Office          2224           241.75X2005-11-30          ----------                                                                          ",
                "2004BL1031420-----8000---ACAS07ID33EUINVALDBCR     NOV-05 IMU Business Office          2237           241.75X2005-11-30          ----------                                                                          "
        };
        
        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions);
        assertOriginEntries(outputTransactions);
    }

    public void testDebitCreditCodeOnTransactionNotRequiringOffset()
            throws Exception {
        String[] inputTransactions = {
                "2004BL1031400-----4100---MBEX07BA  01WRONGDBCR     Rite Quality Office Supplies Inc.                   94.35D2006-01-05          ----------                                                                          ",
                "2004BL1031400-----1800---MBLI07BA  01WRONGDBCR     Rite Quality Office Supplies Inc.                   94.35C2006-01-05          ----------                                                                          "
        };
        
        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions);
        assertOriginEntries(outputTransactions);
    }

    public void testBlankDebitCreditCodeOnTransactionRequiringOffset()
            throws Exception {
        String[] inputTransactions = {
                "2004BA6044913-----1470---ACIN07CR  01BLANKDBCR     Poplars Garage Fees                                 20.00 2006-01-05          ----------                                                                          ",
                "2004BA6044913-----8000---ACAS07CR  01BLANKDBCR     TP Generated Offset                                 20.00 2006-01-05          ----------                                                                          "
        };
        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions);
        assertOriginEntries(outputTransactions);
    }
    
    /*
    public void testBlankTransactionsDescription() throws Exception {
        // Setup
        String[] inputTransactions = {
                "2004BA6044906-----5300---ACEE07CHKDPDBLANKDESC12345                                                  1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
                "2004BA6044906-----8000---ACAS07CHKDPDBLANKDESC12345                                                  1445.00C2006-01-05ABCDEFGHIG----------12345678                                                                  " };
        String[] outputTransactions = {};

        // Scrub
        scrub(inputTransactions);

        // Do assertions
        // ...

        // Report errors
        reportErrors();
    }
    */
    
    public void testBlankDocumentNumber() throws Exception {
        String[] inputTransactions = {
                "2004BL2231423-----1800---ACIN  CR  PL              FRICKA FRACKA                                    45995.84C2006-01-05          ----------                                                                          ",
                "2004BL2231423-----8000---ACAS  CR  PL              TP Generated Offset                              45995.84D2006-01-05          ----------                                                                          "
        };
        
        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions);
        assertOriginEntries(outputTransactions);
    }

    public void testInvalidOriginCode() throws Exception {
        String[] inputTransactions = {
                "2004BA9120656-----5000---ACEX07INV XXINVALORIG     BALDWIN WALLACE COLLEGE                           3375.00C2006-01-05          ----------                                                                          ",
                "2004BA9120656-----8000---ACAS07INV EUINVALORIG     TP Generated Offset                               3375.00D2006-01-05          ----------                                                                          "
        };

        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions);
        assertOriginEntries(outputTransactions);
    }

    public void testBlankOriginCode() throws Exception {
        String[] inputTransactions = {
                "2004BL2231411-----2400---ACEX07ST    BLANKORIG     PAYROLL EXPENSE TRANSFERS                          620.00C2006-01-05          ----------                                                                          ",
                "2004BL2231411-----8000---ACAS07ST  01BLANKORIG     PAYROLL EXPENSE TRANSFERS                          620.00D2006-01-05          ----------                                                                          "
        };

        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions);
        assertOriginEntries(outputTransactions);
    }

    public void testInvalidDocumentType() throws Exception {
        String[] inputTransactions = {
                "2004BL2231408-----4035---ACEX07XXX 01INVALDTYP     Biology Stockroom                                   13.77D2006-01-05          ----------                                                                          ",
                "2004BL2231408-----8000---ACAS07XXX 01INVALDTYP     TP Generated Offset                                 13.77C2006-01-05          ----------                                                                          "
        };
        
        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions);
        assertOriginEntries(outputTransactions);
    }

    public void testBlankDocumentType() throws Exception {
        String[] inputTransactions = {
                "2004BA6044900-----8000---ACAS07    01BLANKDTYP     TP Generated Offset                               1650.00C2006-01-05          ----------                                                                          ",
                "2004BL6044900-----4866---ACEX07    01BLANKDTYP     Correction to: 01-PU3355206                       1650.00D2006-01-05          ----------                                                                          "
        };

        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions);
        assertOriginEntries(outputTransactions);
    }

    public void testInvalidFiscalPeriod() throws Exception {
        String[] inputTransactions = {
                "2004BL1031497-----4190---ACEX14GEC 01INVALPER      THOMAS BUSEY/NEWEGG COMPUTERS                       40.72C2006-01-05          ----------                                                                          ",
                "2004BL1031497-----8000---ACASXXGEC 01INVALPER      TP Generated Offset                                 40.72D2006-01-05          ----------                                                                          "
        };

        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions);
        assertOriginEntries(outputTransactions);
    }

    public void testClosedFiscalPeriod() throws Exception {
        String[] inputTransactions = {
                "2003BA9120656-----4035---ACEX01CR  01CLOSEPER      pymts recd 12/28/05                                 25.15C2006-01-05          ----------                                                                          ",
                "2003BA9120656-----8000---ACAS01CR  01CLOSEPER      TP Generated Offset                                 25.15D2006-01-05          ----------                                                                          "
        };

        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions);
        assertOriginEntries(outputTransactions);
    }
    
    /*
    public void testBlankFiscalPeriod() throws Exception {
        // Setup
        String[] inputTransactions = {
                "2004BA6044900-----5000---ACEX  ACHDPDBLANKPER      H P PRODUCTS CORPORATION        10252845           744.00D2006-01-05          ----------                                                                          ",
                "2004BA6044900-----8000---ACAS  ACHDPDBLANKPER      H P PRODUCTS CORPORATION        10252882           744.00C2006-01-05          ----------                                                                          " };
        String[] outputTransactions = {};

        // Scrub
        scrub(inputTransactions);

        // Do assertions
        // ...

        // Report errors
        reportErrors();
    }
    */
    
    public void testInvalidObjectType() throws Exception {
        String[] inputTransactions = {
                "2004BL1031467-----4100---ACXX07PO  LGINVALOBTY     Rite Quality Office Supplies Inc.                   43.42D2006-01-05          ----------                                                                          ",
                "2004BL1031467-----9892---ACFB07PO  LGINVALOBTY     Rite Quality Office Supplies Inc.                   43.42C2006-01-05          ----------                                                                          "
        };

        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions);
        assertOriginEntries(outputTransactions);
    }
    
    /*
    public void testBlankObjectType() throws Exception {
        // Setup
        String[] inputTransactions = {
                "2004BL2231440-----5000---AC  07PO  LGBLANKOBTY     225050007 WILLIAMS DOTSON ASSOCIATES IN           1200.00D2006-01-05          ----------                                                                          ",
                "2004BL2231440-----9041---AC  07PO  LGBLANKOBTY     225050007 WILLIAMS DOTSON ASSOCIATES IN           1200.00C2006-01-05          ----------                                                                          " };
        String[] outputTransactions = {};

        // Scrub
        scrub(inputTransactions);

        // Do assertions
        // ...

        // Report errors
        reportErrors();
    }
    */
    
    public void testInvalidBalanceType() throws Exception {
        String[] inputTransactions = {
                "2004BL1031420-----4110---XXEX07ID33EUINVALBALT     NOV-05 IMU Business Office          2224           241.75D2005-11-30          ----------                                                                          ",
                "2004BL1031420-----8000---ACAS07ID33EUINVALBALT     NOV-05 IMU Business Office          2237           241.75C2005-11-30          ----------                                                                          "
        };

        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions);
        assertOriginEntries(outputTransactions);
    }
    
    /*
    public void testBlankBalanceType() throws Exception {
        // Setup
        String[] inputTransactions = {
                "2004BL1031400-----4100---  EX07DI  01BLANKBALT     Rite Quality Office Supplies Inc.                   94.35D2006-01-05          ----------                                                                          ",
                "2004BL1031400-----9041---ACLI07DI  01BLANKBALT     Rite Quality Office Supplies Inc.                   94.35C2006-01-05          ----------                                                                          " };
        String[] outputTransactions = {};

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
                "2004BA6044913-----1470XXXACIN07CR  01INVALSOBJ     Poplars Garage Fees                                 20.00C2006-01-05          ----------                                                                          ",
                "2004BA6044913-----8000---ACAS07CR  01INVALSOBJ     TP Generated Offset                                 20.00D2006-01-05          ----------                                                                          " };
        String[] outputTransactions = {};

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
                "2004BA6044900-----1599LLBACIN07TOPSLGINACTSOBJ     CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                          ",
                "2004BA6044900-----9041---ACLI07TOPSLGINACTSOBJ     CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                          " };
        String[] outputTransactions = {};

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
                "2004BA6044906-----5300   ACEE07CHKDPDBLANKSOBJ12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
                "2004BA6044906-----8000   ACAS07CHKDPDBLANKSOBJ12345214090047 EVERETT J PRESCOTT INC.                 1445.00C2006-01-05ABCDEFGHIG----------12345678                                                                  " };
        String[] outputTransactions = {};

        // Scrub
        scrub(inputTransactions);

        // Do assertions
        // ...

        // Report errors
        reportErrors();
    }
    */
    
    public void testInvalidObjectCode() throws Exception {
        String[] inputTransactions = {
                "2004BL2231423-----XXXX---ACIN  CR  PLINVALOBJ      FRICKA FRACKA                                    45995.84C2006-01-05          ----------                                                                          ",
                "2004BL2231423-----8000---ACAS  CR  PLINVALOBJ      TP Generated Offset                              45995.84D2006-01-05          ----------                                                                          "
        };

        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions);
        assertOriginEntries(outputTransactions);
    }

    public void testInactiveObjectCode() throws Exception {
        String[] inputTransactions = {
                "2004BL2231411-----2001---ACEX07INV EUINACTOBJ      BALDWIN WALLACE COLLEGE                           3375.00C2006-01-05          ----------                                                                          ",
                "2004BL2231411-----8000---ACAS07INV EUINACTOBJ      TP Generated Offset                               3375.00D2006-01-05          ----------                                                                          "
        };

        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions);
        assertOriginEntries(outputTransactions);
    }

    public void testBlankObjectCode() throws Exception {
        String[] inputTransactions = {
                "2004BL2231411-----    ---ACEX07ST  01BLANKOBJ      PAYROLL EXPENSE TRANSFERS                          620.00C2006-01-05          ----------                                                                          ",
                "2004BL2231411-----8000---ACAS07ST  01BLANKOBJ      PAYROLL EXPENSE TRANSFERS                          620.00D2006-01-05          ----------                                                                          "
        };

        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions, true);
        assertOriginEntries(outputTransactions);
    }

    public void testInvalidSubAccountNumber() throws Exception {
        String[] inputTransactions = {
                "2004BL2231408XXXX 4035---ACEX07SB  01INVALSACT     Biology Stockroom                                   13.77D2006-01-05          ----------                                                                          ",
                "2004BL2231408XXXX 8000---ACAS07SB  01INVALSACT     TP Generated Offset                                 13.77C2006-01-05          ----------                                                                          "
        };

        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions);
        assertOriginEntries(outputTransactions);
    }
    
    public void testInactiveSubAccountNumber() throws Exception {
        String[] inputTransactions = {
                "2004BA6044900ARREC8000---ACAS07IB  01INACTSACT     TP Generated Offset                               1650.00C2006-01-05          ----------                                                                          ",
                "2004BL6044900ARREC4866---ACEX07IB  01INACTSACT     Correction to: 01-PU3355206                       1650.00D2006-01-05          ----------                                                                          "
        };

        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions, true);
        assertOriginEntries(outputTransactions);
    }
    
	/*
    public void testBlankSubAccountNumber() throws Exception {
        // Setup
        String[] inputTransactions = {
                "2004BL1031497     4190---ACEX07GEC 01BLANKSACT     THOMAS BUSEY/NEWEGG COMPUTERS                       40.72C2006-01-05          ----------                                                                          ",
                "2004BL1031497     8000---ACAS07GEC 01BLANKSACT     TP Generated Offset                                 40.72D2006-01-05          ----------                                                                          " };
        String[] outputTransactions = {};

        // Scrub
        scrub(inputTransactions);

        // Do assertions
        // ...

        // Report errors
        reportErrors();
    }
    */
    
    public void testInvalidAccountNumber() throws Exception {
        String[] inputTransactions = {
                "2004EA1234567-----4035---ACEX07CR  01INVALACCT     pymts recd 12/28/05                                 25.15C2006-01-05          ----------                                                                          ",
                "2004EA1234567-----8000---ACAS07CR  01INVALACCT     TP Generated Offset                                 25.15D2006-01-05          ----------                                                                          "
        };

        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions, true);
        assertOriginEntries(outputTransactions);
    }
    
    /*
    public void testClosedAccountNumber() throws Exception {
        // Setup
        String[] inputTransactions = {
                "2004BA6044901-----5000---ACEX07ACHDPDCLOSEACCT     H P PRODUCTS CORPORATION        10252845           744.00D2006-01-05          ----------                                                                          ",
                "2004BA6044901-----8000---ACAS07ACHDPDCLOSEACCT     H P PRODUCTS CORPORATION        10252882           744.00C2006-01-05          ----------                                                                          " };
        String[] outputTransactions = {};

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
                "2004BL1031467-----4100---ACEX07PO  LGEXPIRACCT     Rite Quality Office Supplies Inc.                   43.42D2006-01-05          ----------                                                                          ",
                "2004BL1031467-----9892---ACFB07PO  LGEXPIRACCT     Rite Quality Office Supplies Inc.                   43.42C2006-01-05          ----------                                                                          " };
        String[] outputTransactions = {};

        // Scrub
        scrub(inputTransactions);

        // Do assertions
        // ...

        // Report errors
        reportErrors();
    }
    */

    public void testBlankAccountNumber() throws Exception {
        String[] inputTransactions = {
                "2004IN       -----5000---ACEX07PO  LGBLANKACCT     225050007 WILLIAMS DOTSON ASSOCIATES IN           1200.00D2006-01-05          ----------                                                                          ",
                "2004IN       -----9041---ACLI07PO  LGBLANKACCT     225050007 WILLIAMS DOTSON ASSOCIATES IN           1200.00C2006-01-05          ----------                                                                          "
        };
        
        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions, true);
        assertOriginEntries(outputTransactions);
    }
    
    public void testInvalidChart() throws Exception {
        String[] inputTransactions = {
                "2004XX1031420-----4110---ACEX07ID33EUINVALCHAR     NOV-05 IMU Business Office          2224           241.75D2005-11-30          ----------                                                                          ",
                "2004XX1031420-----8000---ACAS07ID33EUINVALCHAR     NOV-05 IMU Business Office          2237           241.75C2005-11-30          ----------                                                                          "
        };

        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions);
        assertOriginEntries(outputTransactions);
    }

	/*
    public void testBlankChart() throws Exception {
        // Setup
        String[] inputTransactions = {
                "2004  1031400-----4100---ACEX07DI  01BLANKCHAR     Rite Quality Office Supplies Inc.                   94.35D2006-01-05          ----------                                                                          ",
                "2004  1031400-----9041---ACLI07DI  01BLANKCHAR     Rite Quality Office Supplies Inc.                   94.35C2006-01-05          ----------                                                                          " };
        String[] outputTransactions = {};

        // Scrub
        scrub(inputTransactions);

        // Do assertions
        // ...

        // Report errors
        reportErrors();
    }

    */
    public void testInvalidFiscalYear() throws Exception {
        String[] inputTransactions = {
                "2020BA6044913-----1470---ACIN07CR  01INVALFISC     Poplars Garage Fees                                 20.00C2006-01-05          ----------                                                                          ",
                "2020BA6044913-----8000---ACAS07CR  01INVALFISC     TP Generated Offset                                 20.00D2006-01-05          ----------                                                                          "
        };

        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions);
        assertOriginEntries(outputTransactions);
    }

    /**
     * Entry with a closed fiscal period/year.  These transactions should be marked as errors.
     * 
     * @throws Exception
     */
    public void testClosedFiscalYear() throws Exception {
        String[] inputTransactions = {
                "2003BA6044906-----4100---ACEX07TOPSLGCLOSEFISC     CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                          ",
                "2003BA6044906-----9041---ACLI07TOPSLGCLOSEFISC     CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                          "
        };

        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions);
        assertOriginEntries(outputTransactions);
    }

    /**
     * Entry with a null fiscal year.  The fiscal year should be replaced with the default fiscal year.  They should
     * not be errors.
     * 
     * @throws Exception
     */
    public void dontRunTestDefaultFiscalYear() throws Exception {
        String[] inputTransactions = {
                "    BA6044900-----5300---ACEE07CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
                "    BA6044900-----8000---ACAS07CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00C2006-01-05ABCDEFGHIG----------12345678                                                                  "
        };

        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BA6044900-----5300---ACEE07CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BA6044900-----8000---ACAS07CHKDPDBLANKFISC12345214090047 EVERETT J PRESCOTT INC.                 1445.00C2006-01-05ABCDEFGHIG----------12345678                                                                  ")
        };

        scrub(inputTransactions);
        reportErrors();
        assertOriginEntries(outputTransactions);
    }

    /**
     * Check all the entries in gl_origin_entry_t against the data passed in EntryHolder[].  If any of them
     * are different, assert an error.
     * 
     * @param requiredEntries
     */
    private void assertOriginEntries(EntryHolder[] requiredEntries) {
        List groups = unitTestSqlDao.sqlSelect("select * from gl_origin_entry_grp_t order by origin_entry_grp_src_cd");
        assertEquals("Number of groups is wrong", 4, groups.size());

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

    private void reportErrors() {
        Map errors = scrubberReport.reportErrors;
        for (Iterator i = errors.keySet().iterator(); i.hasNext();) {
            Transaction key = (Transaction) i.next();
            List msgs = (List) errors.get(key);
            for (Iterator iterator = msgs.iterator(); iterator.hasNext();) {
                String msg = (String) iterator.next();
                System.err.println(msg);
            }
        }
    }

    class EntryHolder {
      public String groupCode;
      public String transactionLine;
      public EntryHolder(String groupCode,String transactionLine) {
        this.groupCode = groupCode;
        this.transactionLine = transactionLine;
      }
    }

    private void scrub(String[] inputTransactions) {
    	scrub(inputTransactions, false);
    }
    
    private void scrub(String[] inputTransactions, boolean makeTransactionDateCurrent) {
        clearOriginEntryTables();
        OriginEntryGroup group = createNewGroup(OriginEntrySource.EXTERNAL);

        // TODO This code can be uncommented if needed once we figure out what's going on with the dates.
//        if(makeTransactionDateCurrent) {
//        	// Get the rundate minus 1 day
//        	Date validDate = new Date(dateTimeService.currentDate.getTime() - 86400000);
//        	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
//        	//String runDateFormatted = simpleDateFormat.format(dateTimeService.currentDate);
//        	String currentDateFormatted = simpleDateFormat.format(validDate);
//        	
//        	for(int i = 0; i < inputTransactions.length; i++) {
//        		
//        		String pre = inputTransactions[i].substring(0, 109);
//        		String post = inputTransactions[i].substring(119);
//        		
//        		// splice in the valid date
//        		inputTransactions[i] = pre + currentDateFormatted + post;
//        	}
//        }
        
        loadTransactions(inputTransactions, group);
        scrubberService.scrubEntries();
    }
    
    private void loadTransactions(String[] transactions, OriginEntryGroup group) {
        for (int i = 0; i < transactions.length; i++) {
            createEntry(transactions[i], group);
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

    private OriginEntry createEntry(String line, OriginEntryGroup group) {
        OriginEntry entry = new OriginEntry(line);
        entry.setGroup(group);
        originEntryDao.saveOriginEntry(entry);
        return entry;
    }
}
