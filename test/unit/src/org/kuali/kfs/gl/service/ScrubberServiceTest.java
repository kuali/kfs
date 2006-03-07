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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.kuali.core.service.PersistenceService;
import org.kuali.module.gl.OriginEntryTestBase;
import org.kuali.module.gl.TestScrubberReport;
import org.kuali.module.gl.bo.OriginEntrySource;
import org.kuali.module.gl.bo.Transaction;

public class ScrubberServiceTest extends OriginEntryTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ScrubberServiceTest.class);

    private ScrubberService scrubberService = null;

    private TestScrubberReport scrubberReport = null;

    protected void setUp() throws Exception {
        super.setUp();

        LOG.debug("setUp() started");

        scrubberService = (ScrubberService) beanFactory.getBean("glScrubberService");
        persistenceService = (PersistenceService)beanFactory.getBean("persistenceService");

        // Get the test date time service so we can specify the date/time of the run
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.MONTH, Calendar.JANUARY);
        c.set(Calendar.YEAR, 2004);
        date = c.getTime();
        dateTimeService.currentDate = date;

        // get the test scrubber report so we can read the summary and error information
        // in the unit test
        scrubberReport = (TestScrubberReport) beanFactory.getBean("testScrubberReport");
    }

    public void testClosedAccount01() throws Exception {
        
        // Inputs.
        String[] stringInput = new String[] {
            "2004BL2131401-----4100---ACEX07DI  01CLOSACT0100000Rite Quality Office Supplies Inc.                   94.35D2006-01-05          ----------                                                                  ",
            "2004BL2131401-----9041---ACLI07DI  01CLOSACT0100000Rite Quality Office Supplies Inc.                   94.35C2006-01-05          ----------                                                                  "
        };
        
        String[] convertedStringInput = new String[] {
                "2004BL2131401-----4100---ACEX07DI  01CLOSACT0100000Rite Quality Office Supplies Inc.                   94.35D2006-01-05          ----------                                                                  ",
                "2004BL2131401-----9041---ACLI07DI  01CLOSACT0100000Rite Quality Office Supplies Inc.                   94.35C2006-01-05          ----------                                                                  "
            };

        // Add inputs to expected output ...
        Vector expectedOutput = new Vector();
        for(int i = 0; i < stringInput.length; i++) {
            expectedOutput.add(new EntryHolder(OriginEntrySource.EXTERNAL, convertedStringInput[i]));
        }
        
        // ... add expected output ...
        expectedOutput.add(new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,
            "2004BL2131401-----4100---ACEX07DI  01CLOSACT0100000Rite Quality Office Supplies Inc.                   94.35D2006-01-05          ----------                                                                  "));
        expectedOutput.add(new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,
            "2004BL2131401-----9041---ACLI07DI  01CLOSACT0100000Rite Quality Office Supplies Inc.                   94.35C2006-01-05          ----------                                                                  "));

        // ... and run the test.
        scrub(stringInput);
        assertOriginEntries(4,(EntryHolder[]) expectedOutput.toArray(new EntryHolder[0]));
    }
    
    public void dontRunTestClosedAccount() throws Exception {

        // Inputs.
        String[] stringInput = new String[] {
                "2004BA6044909-----1800---ACIN07CR  UBCLOSACCT 00000Poplars Garage Fees                                 20.00C2006-01-05          ----------                                                                          ",
                "2004BA6044909-----8000---ACAS07CR  UBCLOSACCT 00000TP Generated Offset                                 20.00D2006-01-05          ----------                                                                          "
        };
        
        // Add inputs to expected output ...
        Vector expectedOutput = new Vector();
        for(int i = 0; i < stringInput.length; i++) {
            expectedOutput.add(new EntryHolder(OriginEntrySource.EXTERNAL, stringInput[i]));
        }
        
        // ... add expected output ...
        expectedOutput.add(new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
                "2004BA6044900-----1800---ACIN07CR  UBCLOSACCT 00000AUTO FR BA6044909Poplars Garage Fees                20.00C2006-01-05          ----------                                                                          "));
        expectedOutput.add(new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
                "2004BA6044900-----8000---ACAS07CR  UBCLOSACCT 00000GENERATED OFFSET                                    20.00D2006-02-21          ----------                                                                          "));
        expectedOutput.add(new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
                "2004BA6044900-----8000---ACAS07CR  UBCLOSACCT 00000AUTO FR BA6044909TP Generated Offset                20.00D2006-01-05          ----------                                                                          "));
        expectedOutput.add(new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
                "2004BA6044900-----8000---ACAS07CR  UBCLOSACCT 00000GENERATED OFFSET                                    20.00C2006-02-21          ----------                                                                          "));
                
        // ... and run the test.
        scrub(stringInput);
        assertOriginEntries(4,(EntryHolder[]) expectedOutput.toArray(new EntryHolder[0]));
    }

    public void testExpiredAccountByDocumentType() throws Exception {
        
        // Inputs.
        String[] stringInput = new String[] {
                "2004BL4631557-----4100---ACEX07LOCRLGEXPRACTLC00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                          ",
                "2004BL4631557-----9041---ACLI07LOCRLGEXPRACTLC00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                          "
        };
        
        // Add inputs to expected output ...
        Vector expectedOutput = new Vector();
        for(int i = 0; i < stringInput.length; i++) {
            expectedOutput.add(new EntryHolder(OriginEntrySource.EXTERNAL, stringInput[i]));
        }
        
        // ... add expected output ...
        expectedOutput.add(new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
                "2004BL4631557-----4100---ACEX07LOCRLGEXPRACTLC00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                          "));
        expectedOutput.add(new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
                "2004BL4631557-----9041---ACLI07LOCRLGEXPRACTLC00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                          "));

        // ... and run the test.
        scrub(stringInput);
        assertOriginEntries(4,(EntryHolder[]) expectedOutput.toArray(new EntryHolder[0]));
        
    }
    
    public void failingTestExpiredAccountByBalanceType() throws Exception {
        
        // Inputs.
        String[] stringInput = new String[] {
                "2004BL4131407-----4100---EXEX07TOPSLGEXPRACTEX     CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                 D                                        ",
                "2004BL4131407-----9041---EXLI07TOPSLGEXPRACTEX     CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                 D                                        "
        };
        
        // Add inputs to expected output ...
        Vector expectedOutput = new Vector();
        for(int i = 0; i < stringInput.length; i++) {
            expectedOutput.add(new EntryHolder(OriginEntrySource.EXTERNAL, stringInput[i]));
        }
        
        // ... add expected output ...
        expectedOutput.add(new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
                "2004BL4131407-----4100---EXEX07TOPSLGEXPRACTEX00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                 D                                        "));
        expectedOutput.add(new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
                "2004BL4131407-----9041---EXLI07TOPSLGEXPRACTEX00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                 D                                        "));

        // ... and run the test.
        scrub(stringInput);
        assertOriginEntries(4,(EntryHolder[]) expectedOutput.toArray(new EntryHolder[0]));

    }
    
    public void testExpiredAccountByOriginCode() throws Exception {
        
        // Inputs.
        String[] stringInput = new String[] {
                "2004BL1031467-----5300---ACEE07DD  01EXPRACT0112345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
                "2004BL1031467-----8000---ACAS07DD  01EXPRACT0112345214090047 EVERETT J PRESCOTT INC.                 1445.00C2006-01-05ABCDEFGHIG----------12345679                                                                  ",
        };
        
        // Add inputs to expected output ...
        Vector expectedOutput = new Vector();
        for(int i = 0; i < stringInput.length; i++) {
            expectedOutput.add(new EntryHolder(OriginEntrySource.EXTERNAL, stringInput[i]));
        }
        
        // ... add expected output ...
        expectedOutput.add(new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
                "2004BL1031467-----5300---ACEE07DD  01EXPRACT0112345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "));
        expectedOutput.add(new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
                "2004BL1031467-----8000---ACAS07DD  01EXPRACT0112345214090047 EVERETT J PRESCOTT INC.                 1445.00C2006-01-05ABCDEFGHIG----------12345679                                                                  "));

        // ... and run the test.
        scrub(stringInput);
        assertOriginEntries(4,(EntryHolder[]) expectedOutput.toArray(new EntryHolder[0]));
        
    }
    
    public void FIXMEtestExpiredContractAndGrantAccountWithinThirtyDayGracePeriod() throws Exception {
        
        // FIXME apparently didn't find data for this in the Kuali dev environment. So, can't test this now
        // Need to test at some point however.
        
    }
    
    public void testExpiredContractAndGrantAccount() throws Exception {
        
        // Inputs.
        String[] stringInput = new String[] {
            "2004BL4131407-----4100---ACEX07TOPSLGEXPIRCGAC00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                          ",
            "2004BL4131407-----9041---ACLI07TOPSLGEXPIRCGAC00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                          "
        };
        
        // Add inputs to expected output ...
        Vector expectedOutput = new Vector();
        for(int i = 0; i < stringInput.length; i++) {
            expectedOutput.add(new EntryHolder(OriginEntrySource.EXTERNAL, stringInput[i]));
        }
        
        // ... add expected output ...
        expectedOutput.add(new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
                "2004BL4131407-----4100---ACEX07TOPSLGEXPIRCGAC00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                          "));
        expectedOutput.add(new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
                "2004BL4131407-----9041---ACLI07TOPSLGEXPIRCGAC00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                          "));

        // ... and run the test.
        scrub(stringInput);
        assertOriginEntries(4,(EntryHolder[]) expectedOutput.toArray(new EntryHolder[0]));

    }
    
    public void testExpiredAccount() throws Exception {
        
        // Inputs.
        String[] stringInput = new String[] {
            "2004BL1031467-----5300---ACEE07CD  PDEXPIRACCT12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
            "2004BL1031467-----8000---ACAS07CD  PDEXPIRACCT12345214090047 EVERETT J PRESCOTT INC.                 1445.00C2006-01-05ABCDEFGHIG----------12345679                                                                  "
        };
        
        // Add inputs to expected output ...
        Vector expectedOutput = new Vector();
        for(int i = 0; i < stringInput.length; i++) {
            expectedOutput.add(new EntryHolder(OriginEntrySource.EXTERNAL, stringInput[i]));
        }
        
        // ... add expected output ...
        expectedOutput.add(new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
            "2004BL1031467-----5300---ACEE07CD  PDEXPIRACCT12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "));
        expectedOutput.add(new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
            "2004BL1031467-----8000---ACAS07CD  PDEXPIRACCT12345214090047 EVERETT J PRESCOTT INC.                 1445.00C2006-01-05ABCDEFGHIG----------12345679                                                                  "));

        // ... and run the test.
        scrub(stringInput);
        assertOriginEntries(4,(EntryHolder[]) expectedOutput.toArray(new EntryHolder[0]));
    }
    
    public void testOffsetGenerationAcrossMultipleDocumentTypes() throws Exception {
    }
    
    public void testOffsetGenerationAcrossMultipleOriginCodes() throws Exception {
    }
    
    // ************************************************************** Tests for error conditions below.
    
    public void testInvalidEncumbranceUpdateCode() throws Exception {
        
        String[] inputTransactions = {
                "2004BL1031420-----4110---IEEX07PAYEEUINVALENCC00000NOV-05 IMU Business Office          2224           241.75C2005-11-30          ----------                                 X                                        ",
                "2004BL1031420-----9892---IEAS07PAYEEUINVALENCC00000NOV-05 IMU Business Office          2237           241.75D2005-11-30          ----------                                 X                                        "
        };
        
        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };
        
        scrub(inputTransactions);
        assertOriginEntries(4,outputTransactions);
    }

    public void testBlankEncumbranceUpdateCodeOnEncumbranceRecord()
            throws Exception {

        String[] inputTransactions = {
                "2004BL1031400-----4100---PEEX07TF  01BLANKENCC00000Rite Quality Office Supplies Inc.                   94.35D2006-01-05          ----------                                                                          ",
                "2004BL1031400-----9891---PEFB07TF  01BLANKENCC00000Rite Quality Office Supplies Inc.                   94.35C2006-01-05          ----------                                                                          "
        };
        
        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions);
        assertOriginEntries(4,outputTransactions);
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
        assertOriginEntries(4,outputTransactions);
    }

    public void testBlankReferenceDocumentNumberWithEncumbranceUpdateCodeOfR()
            throws Exception {

        String[] inputTransactions = {
                "2004BA6044900-----1599---EXIN07TOPSLGBLANKRDOC00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------        CR  01                   R                                        ",
                "2004BA6044900-----9041---EXLI07TOPSLDBLANKRDOC00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------        CR  01                   R                                        "
        };
        
        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions);
        assertOriginEntries(4,outputTransactions);
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
        assertOriginEntries(4,outputTransactions);
    }

    public void testBlankReferenceOriginCodeWithEncumbranceUpdateCodeOfR()
            throws Exception {

        String[] inputTransactions = {
                "2004BL9120656-----5000---ACEX07INV EUBLANKRORG00000BALDWIN WALLACE COLLEGE                           3375.00C2006-01-05          ----------        DI    123456789                                                   ",
                "2004BL9120656-----8000---ACAS07INV EUBLANKRORG00000TP Generated Offset                               3375.00D2006-01-05          ----------        DI    123456789                                                   "
        };
        
        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions, true);
        assertOriginEntries(4,outputTransactions);
    }

    public void testInvalidReferenceOriginCode() throws Exception {

        String[] inputTransactions = {
                "2004BL2231411-----2400---ACEX07ST  EUINVALRORG00000PAYROLL EXPENSE TRANSFERS                          620.00C2006-01-05          ----------        CD  XX123456789                                                   ",
                "2004BL2231411-----8000---ACAS07ST  EUINVALRORG00000PAYROLL EXPENSE TRANSFERS                          620.00D2006-01-05          ----------        CD  XX123456789                                                   "
        };
        
        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions);
        assertOriginEntries(4,outputTransactions);
    }

    public void testBlankReferenceDocumentTypeWithEncumbranceUpdateCodeOfR()
            throws Exception {

        String[] inputTransactions = {
                "2004BL2231408-----4035---ACEX07SB  01BLANKRDTP00000Biology Stockroom                                   13.77D2006-01-05          ----------            LG123456789                                                   ",
                "2004BL2231408-----8000---ACAS07SB  01BLANKRDTP00000TP Generated Offset                                 13.77C2006-01-05          ----------            LG123456789                                                   "
        };
        
        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions);
        assertOriginEntries(4,outputTransactions);
    }

    public void testInvalidReferenceDocumentType() throws Exception {
        String[] inputTransactions = {
                "2004BL1031497-----4190---ACEX07GEC 01INVALRDTP00000THOMAS BUSEY/NEWEGG COMPUTERS                       40.72C2006-01-05          ----------        XXXXLG123456789                                                   ",
                "2004BL1031497-----8000---ACAS07GEC 01INVALRDTP00000TP Generated Offset                                 40.72D2006-01-05          ----------        XXXXLG123456789                                                   "
        };
        
        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions);
        assertOriginEntries(4,outputTransactions);
    }

    public void testInvalidProjectCode() throws Exception {
        String[] inputTransactions = {
                "2004BL9120656-----4035---ACEX07CR  01INVALPROJ00000pymts recd 12/28/05                                 25.15C2006-01-05          XXXXXXXXX                                                                           ",
                "2004BL9120656-----8000---ACAS07CR  01INVALPROJ00000TP Generated Offset                                 25.15D2006-01-05          XXXXXXXXX                                                                           "
        };
        
        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions, true);
        assertOriginEntries(4,outputTransactions);
    }
    
    public void testInvalidTransactionDate() throws Exception {
        String[] inputTransactions = {
                "2004BL1031497-----4100---ACEX07PO  LGINVALDATE00000Rite Quality Office Supplies Inc.                   43.42D2096-02-11          ----------                                                                          ",
                "2004BL1031497-----9892---ACFB07PO  LGINVALDATE00000Rite Quality Office Supplies Inc.                   43.42C1006-12-23          ----------                                                                          "
        };

        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions);
        assertOriginEntries(4,outputTransactions);
    }
    
    public void testInvalidDebitCreditCode() throws Exception {
        String[] inputTransactions = {
                "2004BL1031420-----4110---ACEX07ID33EUINVALDBCR00000NOV-05 IMU Business Office          2224           241.75X2005-11-30          ----------                                                                          ",
                "2004BL1031420-----8000---ACAS07ID33EUINVALDBCR00000NOV-05 IMU Business Office          2237           241.75X2005-11-30          ----------                                                                          "
        };
        
        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions);
        assertOriginEntries(4,outputTransactions);
    }

    public void testDebitCreditCodeOnTransactionNotRequiringOffset()
            throws Exception {
        String[] inputTransactions = {
                "2004BL1031400-----4100---MBEX07BA  01WRONGDBCR00000Rite Quality Office Supplies Inc.                   94.35D2006-01-05          ----------                                                                          ",
                "2004BL1031400-----1800---MBLI07BA  01WRONGDBCR00000Rite Quality Office Supplies Inc.                   94.35C2006-01-05          ----------                                                                          "
        };
        
        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions);
        assertOriginEntries(4,outputTransactions);
    }

    public void testBlankDebitCreditCodeOnTransactionRequiringOffset()
            throws Exception {
        String[] inputTransactions = {
                "2004BA6044913-----1470---ACIN07CR  01BLANKDBCR00000Poplars Garage Fees                                 20.00 2006-01-05          ----------                                                                          ",
                "2004BA6044913-----8000---ACAS07CR  01BLANKDBCR00000TP Generated Offset                                 20.00 2006-01-05          ----------                                                                          "
        };
        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions);
        assertOriginEntries(4,outputTransactions);
    }
    
    public void testBlankDocumentNumber() throws Exception {
        String[] inputTransactions = {
                "2004BL2231423-----1800---ACIN  CR  PL         00000FRICKA FRACKA                                    45995.84C2006-01-05          ----------                                                                          ",
                "2004BL2231423-----8000---ACAS  CR  PL         00000TP Generated Offset                              45995.84D2006-01-05          ----------                                                                          "
        };
        
        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions);
        assertOriginEntries(4,outputTransactions);
    }

    public void testInvalidOriginCode() throws Exception {
        String[] inputTransactions = {
                "2004BA9120656-----5000---ACEX07INV XXINVALORIG00000BALDWIN WALLACE COLLEGE                           3375.00C2006-01-05          ----------                                                                          ",
                "2004BA9120656-----8000---ACAS07INV EUINVALORIG00000TP Generated Offset                               3375.00D2006-01-05          ----------                                                                          "
        };

        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions);
        assertOriginEntries(4,outputTransactions);
    }

    public void testBlankOriginCode() throws Exception {
        String[] inputTransactions = {
                "2004BL2231411-----2400---ACEX07ST    BLANKORIG00000PAYROLL EXPENSE TRANSFERS                          620.00C2006-01-05          ----------                                                                          ",
                "2004BL2231411-----8000---ACAS07ST  01BLANKORIG00000PAYROLL EXPENSE TRANSFERS                          620.00D2006-01-05          ----------                                                                          "
        };

        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions);
        assertOriginEntries(4,outputTransactions);
    }

    public void testInvalidDocumentType() throws Exception {
        String[] inputTransactions = {
                "2004BL2231408-----4035---ACEX07XXX 01INVALDTYP00000Biology Stockroom                                   13.77D2006-01-05          ----------                                                                          ",
                "2004BL2231408-----8000---ACAS07XXX 01INVALDTYP00000TP Generated Offset                                 13.77C2006-01-05          ----------                                                                          "
        };
        
        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions);
        assertOriginEntries(4,outputTransactions);
    }

    public void testBlankDocumentType() throws Exception {
        String[] inputTransactions = {
                "2004BA6044900-----8000---ACAS07    01BLANKDTYP00000TP Generated Offset                               1650.00C2006-01-05          ----------                                                                          ",
                "2004BL6044900-----4866---ACEX07    01BLANKDTYP00000Correction to: 01-PU3355206                       1650.00D2006-01-05          ----------                                                                          "
        };

        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions);
        assertOriginEntries(4,outputTransactions);
    }

    public void testInvalidFiscalPeriod() throws Exception {
        String[] inputTransactions = {
                "2004BL1031497-----4190---ACEX14GEC 01INVALPER 00000THOMAS BUSEY/NEWEGG COMPUTERS                       40.72C2006-01-05          ----------                                                                          ",
                "2004BL1031497-----8000---ACASXXGEC 01INVALPER 00000TP Generated Offset                                 40.72D2006-01-05          ----------                                                                          "
        };

        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions);
        assertOriginEntries(4,outputTransactions);
    }

    public void testClosedFiscalPeriod() throws Exception {
        String[] inputTransactions = {
                "2003BA9120656-----4035---ACEX01CR  01CLOSEPER 00000pymts recd 12/28/05                                 25.15C2006-01-05          ----------                                                                          ",
                "2003BA9120656-----8000---ACAS01CR  01CLOSEPER 00000TP Generated Offset                                 25.15D2006-01-05          ----------                                                                          "
        };

        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions);
        assertOriginEntries(4,outputTransactions);
    }
    
    public void testInvalidObjectType() throws Exception {
        String[] inputTransactions = {
                "2004BL1031467-----4100---ACXX07PO  LGINVALOBTY00000Rite Quality Office Supplies Inc.                   43.42D2006-01-05          ----------                                                                          ",
                "2004BL1031467-----9892---ACFB07PO  LGINVALOBTY00000Rite Quality Office Supplies Inc.                   43.42C2006-01-05          ----------                                                                          "
        };

        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions);
        assertOriginEntries(4,outputTransactions);
    }
    
    public void testInvalidBalanceType() throws Exception {
        String[] inputTransactions = {
                "2004BL1031420-----4110---XXEX07ID33EUINVALBALT00000NOV-05 IMU Business Office          2224           241.75D2005-11-30          ----------                                                                          ",
                "2004BL1031420-----8000---ACAS07ID33EUINVALBALT00000NOV-05 IMU Business Office          2237           241.75C2005-11-30          ----------                                                                          "
        };

        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions);
        assertOriginEntries(4,outputTransactions);
    }
    
    public void testInvalidObjectCode() throws Exception {
        String[] inputTransactions = {
                "2004BL2231423-----XXXX---ACIN  CR  PLINVALOBJ 00000FRICKA FRACKA                                    45995.84C2006-01-05          ----------                                                                          ",
                "2004BL2231423-----8000---ACAS  CR  PLINVALOBJ 00000TP Generated Offset                              45995.84D2006-01-05          ----------                                                                          "
        };

        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions);
        assertOriginEntries(4,outputTransactions);
    }

    public void testInactiveObjectCode() throws Exception {
        String[] inputTransactions = {
                "2004BL2231411-----2001---ACEX07INV EUINACTOBJ 00000BALDWIN WALLACE COLLEGE                           3375.00C2006-01-05          ----------                                                                          ",
                "2004BL2231411-----8000---ACAS07INV EUINACTOBJ 00000TP Generated Offset                               3375.00D2006-01-05          ----------                                                                          "
        };

        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions);
        assertOriginEntries(4,outputTransactions);
    }

    public void testBlankObjectCode() throws Exception {
        String[] inputTransactions = {
                "2004BL2231411-----    ---ACEX07ST  01BLANKOBJ 00000PAYROLL EXPENSE TRANSFERS                          620.00C2006-01-05          ----------                                                                          ",
                "2004BL2231411-----8000---ACAS07ST  01BLANKOBJ 00000PAYROLL EXPENSE TRANSFERS                          620.00D2006-01-05          ----------                                                                          "
        };

        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions, true);
        assertOriginEntries(4,outputTransactions);
    }

    public void testInvalidSubAccountNumber() throws Exception {
        String[] inputTransactions = {
                "2004BL2231408XXXX 4035---ACEX07SB  01INVALSACT00000Biology Stockroom                                   13.77D2006-01-05          ----------                                                                          ",
                "2004BL2231408XXXX 8000---ACAS07SB  01INVALSACT00000TP Generated Offset                                 13.77C2006-01-05          ----------                                                                          "
        };

        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions);
        assertOriginEntries(4,outputTransactions);
    }
    
    public void testInactiveSubAccountNumber() throws Exception {
        String[] inputTransactions = {
                "2004BA6044900ARREC8000---ACAS07IB  01INACTSACT00000TP Generated Offset                               1650.00C2006-01-05          ----------                                                                          ",
                "2004BL6044900ARREC4866---ACEX07IB  01INACTSACT00000Correction to: 01-PU3355206                       1650.00D2006-01-05          ----------                                                                          "
        };

        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions, true);
        assertOriginEntries(4,outputTransactions);
    }
    
    public void testInvalidAccountNumber() throws Exception {
        String[] inputTransactions = {
                "2004EA1234567-----4035---ACEX07CR  01INVALACCT00000pymts recd 12/28/05                                 25.15C2006-01-05          ----------                                                                          ",
                "2004EA1234567-----8000---ACAS07CR  01INVALACCT00000TP Generated Offset                                 25.15D2006-01-05          ----------                                                                          "
        };

        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions, true);
        assertOriginEntries(4,outputTransactions);
    }
    
    public void testBlankAccountNumber() throws Exception {
        String[] inputTransactions = {
                "2004IN       -----5000---ACEX07PO  LGBLANKACCT00000225050007 WILLIAMS DOTSON ASSOCIATES IN           1200.00D2006-01-05          ----------                                                                          ",
                "2004IN       -----9041---ACLI07PO  LGBLANKACCT00000225050007 WILLIAMS DOTSON ASSOCIATES IN           1200.00C2006-01-05          ----------                                                                          "
        };
        
        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions, true);
        assertOriginEntries(4,outputTransactions);
    }
    
    public void testInvalidChart() throws Exception {
        String[] inputTransactions = {
                "2004XX1031420-----4110---ACEX07ID33EUINVALCHAR00000NOV-05 IMU Business Office          2224           241.75D2005-11-30          ----------                                                                          ",
                "2004XX1031420-----8000---ACAS07ID33EUINVALCHAR00000NOV-05 IMU Business Office          2237           241.75C2005-11-30          ----------                                                                          "
        };

        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions);
        assertOriginEntries(4,outputTransactions);
    }

    public void testInvalidFiscalYear() throws Exception {
        String[] inputTransactions = {
                "2020BA6044913-----1470---ACIN07CR  01INVALFISC00000Poplars Garage Fees                                 20.00C2006-01-05          ----------                                                                          ",
                "2020BA6044913-----8000---ACAS07CR  01INVALFISC00000TP Generated Offset                                 20.00D2006-01-05          ----------                                                                          "
        };

        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions);
        assertOriginEntries(4,outputTransactions);
    }

    /**
     * Entry with a closed fiscal period/year.  These transactions should be marked as errors.
     * 
     * @throws Exception
     */
    public void testClosedFiscalYear() throws Exception {
        String[] inputTransactions = {
                "2003BA6044906-----4100---ACEX07TOPSLGCLOSEFISC00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                          ",
                "2003BA6044906-----9041---ACLI07TOPSLGCLOSEFISC00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                          "
        };

        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,inputTransactions[1])
        };

        scrub(inputTransactions);
        assertOriginEntries(4,outputTransactions);
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
        assertOriginEntries(4,outputTransactions);
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

    private void scrub(String[] inputTransactions) {
        scrub(inputTransactions, false);
    }
    
    private void scrub(String[] inputTransactions, boolean makeTransactionDateCurrent) {
        clearOriginEntryTables();
        loadInputTransactions(OriginEntrySource.EXTERNAL,inputTransactions);
        scrubberService.scrubEntries();

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

    /*
    public void testEncumbranceUpdateCodeOnNonEncumbrancePeriod()
            throws Exception {

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
        assertOriginEntries(4,outputTransactions);
    }

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
        assertOriginEntries(4,outputTransactions);
    }

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
        assertOriginEntries(4,outputTransactions);
    }

    */
}
