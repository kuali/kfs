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
import java.util.Vector;

import org.kuali.core.service.PersistenceService;
import org.kuali.module.gl.OriginEntryTestBase;
import org.kuali.module.gl.bo.OriginEntrySource;

// testNoCostShareEncumbracesForBudget
// testNoCostShareTransfersForBudgetTransactions
// testNoIndeptednessForBudgetransacions
// testNoCapitalizationForBugetTransactions
// testClosedAccount
// testInvalidBalanceType

public class ScrubberServiceTest extends OriginEntryTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ScrubberServiceTest.class);

    private ScrubberService scrubberService = null;

    protected void setUp() throws Exception {
        super.setUp();

        LOG.debug("setUp() started");

        scrubberService = (ScrubberService) beanFactory.getBean("glScrubberService");
        persistenceService = (PersistenceService)beanFactory.getBean("persistenceService");

        // Get the test date time service so we can specify the date/time of the run
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.MONTH, Calendar.JANUARY);
        c.set(Calendar.YEAR, 2006);
        date = c.getTime();
        dateTimeService.currentDate = date;
    }

    public void testMiscellaneousBlankFields() throws Exception {

        String[] stringInput = new String[] {
                "2004  6044900-----5300---ACEE07CHKDPDBLANKCHAR12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
                "2004BA       -----5300---ACEE07CHKDPDBLANKACCT12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
                "2004BA6044900-----    ---ACEE07CHKDPDBLANKOBJ 12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
                "2004BA6044900-----5300---ACEE07    PDBLANKDOCT12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
                "2004BA6044900-----5300---ACEE07CHKD  BLANKORIG12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
                "2004BA6044900-----5300---ACEE07CHKDPD         12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
        };

        // Add inputs to expected output ...
        EntryHolder output[] = new EntryHolder[12];
        for(int i = 0; i < stringInput.length; i++) {
            output[i] = new EntryHolder(OriginEntrySource.EXTERNAL, stringInput[i]);
        }

        int c = stringInput.length;
        output[c++] = new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, "2004BA       -----5300---ACEE07CHKDPDBLANKACCT12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ");
        output[c++] = new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, "2004  6044900-----5300---ACEE07CHKDPDBLANKCHAR12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ");
        output[c++] = new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, "2004BA6044900-----    ---ACEE07CHKDPDBLANKOBJ 12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ");
        output[c++] = new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, "2004BA6044900-----5300---ACEE07CHKDPD         12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ");
        output[c++] = new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, "2004BA6044900-----5300---ACEE07CHKD  BLANKORIG12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ");
        output[c++] = new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, "2004BA6044900-----5300---ACEE07    PDBLANKDOCT12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ");

        scrub(stringInput);
        assertOriginEntries(4,output);
    }

    public void testCostShareEncumbrancesForPreEncumbrances() throws Exception {

        // Inputs.
        String[] stringInput = new String[] {
                "2004BL4631625CS0018000---PEAS07PE  01CSENCPE  00000TP Generated Offset                               1650.00C2006-01-05          ----------                                 D                                ",
                "2004BL4631625CS0014866---PEEX07PE  01CSENCPE  00000Correction to: 01-PU3355206                       1650.00D2006-01-05          ----------                                 D                                "
        };

        // Add inputs to expected output ...
        EntryHolder output[] = new EntryHolder[6];
        for(int i = 0; i < stringInput.length; i++) {
            output[i] = new EntryHolder(OriginEntrySource.EXTERNAL, stringInput[i]);
        }

        // ... add expected output ...
        output[2] = new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
            "2004BL1031400-----9940---CEEX07PE  01CSENCPE  00000Correction to: 01-PU3355206 FR-BL4631625          1650.00D2006-01-01          ----------                                 D                                ");
        output[3] = new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
            "2004BL1031400-----9893---CEFB07PE  01CSENCPE  00000GENERATED OFFSET                                  1650.00C2006-01-01          ----------                                                                  ");
        output[4] = new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
            "2004BL4631625CS0014866---PEEX07PE  01CSENCPE  00000Correction to: 01-PU3355206                       1650.00D2006-01-05          ----------                                 D                                ");
        output[5] = new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
            "2004BL4631625CS0018000---PEAS07PE  01CSENCPE  00000TP Generated Offset                               1650.00C2006-01-05          ----------                                 D                                ");

        scrub(stringInput);

        assertOriginEntries(4,output);
    }

    public void testCostShareEncumbrancesForInternalEncumbrances() throws Exception {

        String[] stringInput = new String[] {
                "2004BL4631618CS0014190---IEEX07PAYE01CSENCIE  00000THOMAS BUSEY/NEWEGG COMPUTERS                       40.72C2006-01-05          ----------                                 D                                ",
                "2004BL4631618CS0018000---IEAS07PAYE01CSENCIE  00000TP Generated Offset                                 40.72D2006-01-05          ----------                                 D                                "
        };

        // Add inputs to expected output ...
        EntryHolder[] output = new EntryHolder[6];
        for(int i = 0; i < stringInput.length; i++) {
            output[i] = new EntryHolder(OriginEntrySource.EXTERNAL, stringInput[i]);
        }

        // ... add expected output ...
        output[2] = new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
                "2004BL1031400-----9940---CEEX07PAYE01CSENCIE  00000THOMAS BUSEY/NEWEGG COMPUTERFR-BL4631618            40.72C2006-01-01          ----------                                 D                                ");
        output[3] = new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
                "2004BL1031400-----9893---CEFB07PAYE01CSENCIE  00000GENERATED OFFSET                                    40.72D2006-01-01          ----------                                                                  ");
        output[4] = new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
                "2004BL4631618CS0014190---IEEX07PAYE01CSENCIE  00000THOMAS BUSEY/NEWEGG COMPUTERS                       40.72C2006-01-05          ----------                                 D                                ");
        output[5] = new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
                "2004BL4631618CS0018000---IEAS07PAYE01CSENCIE  00000TP Generated Offset                                 40.72D2006-01-05          ----------                                 D                                ");

        // ... and run the test.
        scrub(stringInput);
        assertOriginEntries(4,output);
    }

    public void testCostShareEncumbrancesForExternalEncumbrances() throws Exception {

        String[] stringInput = new String[] {
                "2004BL4631601CS0011800---EXIN07EXENLGCSENCEX  00000225050007 WILLIAMS DOTSON ASSOCIATES IN           1200.00D2006-01-05          ----------                                 D                                ",
                "2004BL4631601CS0019041---EXLI07EXENLGCSENCEX  00000225050007 WILLIAMS DOTSON ASSOCIATES IN           1200.00C2006-01-05          ----------                                 D                                "
        };

        // Add inputs to expected output ...
        EntryHolder output[] = new EntryHolder[4];
        for(int i = 0; i < stringInput.length; i++) {
            output[i] = new EntryHolder(OriginEntrySource.EXTERNAL, stringInput[i]);
        }

        int c = stringInput.length;
        output[c++] = new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
                "2004BL4631601CS0011800---EXIN07EXENLGCSENCEX  00000225050007 WILLIAMS DOTSON ASSOCIATES IN           1200.00D2006-01-05          ----------                                 D                                ");
        output[c++] = new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
                "2004BL4631601CS0019041---EXLI07EXENLGCSENCEX  00000225050007 WILLIAMS DOTSON ASSOCIATES IN           1200.00C2006-01-05          ----------                                 D                                ");

        scrub(stringInput);
        assertOriginEntries(4,output);
    }

    public void testNoCostShareEncumbrancesForCostShareEncumbrances() throws Exception {

        String[] stringInput = new String[] {
                "2004BL4631625CS0018000---CEAS07EXEN01NOCSENCE 00000TP Generated Offset                               1650.00C2006-01-05          ----------                                 D                                ",
                "2004BL4631625CS0014866---CEEX07EXEN01NOCSENCE 00000Correction to: 01-PU3355206                       1650.00D2006-01-05          ----------                                 D                                "
        };

        EntryHolder output[] = new EntryHolder[] {
                new EntryHolder(OriginEntrySource.EXTERNAL, stringInput[0]),
                new EntryHolder(OriginEntrySource.EXTERNAL, stringInput[1]),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
                    "2004BL4631625CS0014866---CEEX07EXEN01NOCSENCE 00000Correction to: 01-PU3355206                       1650.00D2006-01-05          ----------                                 D                                "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
                    "2004BL4631625CS0018000---CEAS07EXEN01NOCSENCE 00000TP Generated Offset                               1650.00C2006-01-05          ----------                                 D                                ")
        };

        scrub(stringInput);
        assertOriginEntries(4,output);

    }

    public void testNoCostShareEncumbrancesForJournalVoucher() throws Exception {

        String[] input = new String[] {
                "2004BL4631618CS0014190---EXEX07JV  01NOCSENJV 00000THOMAS BUSEY/NEWEGG COMPUTERS                       40.72C2006-01-05          ----------                                 D                                ",
                "2004BL4631618CS0018000---EXAS07JV  01NOCSENJV 00000TP Generated Offset                                 40.72D2006-01-05          ----------                                 D                                "
        };

        EntryHolder[] output = new EntryHolder[] {
                new EntryHolder(OriginEntrySource.EXTERNAL,"2004BL4631618CS0014190---EXEX07JV  01NOCSENJV 00000THOMAS BUSEY/NEWEGG COMPUTERS                       40.72C2006-01-05          ----------                                 D                                "),
                new EntryHolder(OriginEntrySource.EXTERNAL,"2004BL4631618CS0018000---EXAS07JV  01NOCSENJV 00000TP Generated Offset                                 40.72D2006-01-05          ----------                                 D                                "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL4631618CS0014190---EXEX07JV  01NOCSENJV 00000THOMAS BUSEY/NEWEGG COMPUTERS                       40.72C2006-01-05          ----------                                 D                                "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL4631618CS0018000---EXAS07JV  01NOCSENJV 00000TP Generated Offset                                 40.72D2006-01-05          ----------                                 D                                "),
        };

        scrub(input);
        assertOriginEntries(4,output);
    }

    public void testNoCostShareEncumbrancesForBeginningBalances() throws Exception {

        String[] input = new String[] {
                "2004BL4631601CS0011800---EXINCBTOPSLGNOCSENCB 00000225050007 WILLIAMS DOTSON ASSOCIATES IN           1200.00D2006-01-05          ----------                                 D                                ",
                "2004BL4631601CS0019041---EXLICBTOPSLGNOCSENCB 00000225050007 WILLIAMS DOTSON ASSOCIATES IN           1200.00C2006-01-05          ----------                                 D                                "
        };

        EntryHolder[] output = new EntryHolder[] {
                new EntryHolder(OriginEntrySource.EXTERNAL,"2004BL4631601CS0011800---EXINCBTOPSLGNOCSENCB 00000225050007 WILLIAMS DOTSON ASSOCIATES IN           1200.00D2006-01-05          ----------                                 D                                "),
                new EntryHolder(OriginEntrySource.EXTERNAL,"2004BL4631601CS0019041---EXLICBTOPSLGNOCSENCB 00000225050007 WILLIAMS DOTSON ASSOCIATES IN           1200.00C2006-01-05          ----------                                 D                                "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL4631601CS0011800---EXINCBTOPSLGNOCSENCB 00000225050007 WILLIAMS DOTSON ASSOCIATES IN           1200.00D2006-01-05          ----------                                 D                                "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL4631601CS0019041---EXLICBTOPSLGNOCSENCB 00000225050007 WILLIAMS DOTSON ASSOCIATES IN           1200.00C2006-01-05          ----------                                 D                                ")
        };

        scrub(input);
        assertOriginEntries(4,output);
    }

    public void testNoCostShareEncumbrancesForActuals() throws Exception {

        String[] input = new String[] {
                "2004BL4631625CS0018000---ACAS07IB  01NOCSENAC 00000TP Generated Offset                               1650.00C2006-01-05          ----------                                 D                                ",
                "2004BL4631625CS0014866---ACEX07IB  01NOCSENAC 00000Correction to: 01-PU3355206                       1650.00D2006-01-05          ----------                                 D                                "
        };

        EntryHolder[] output = new EntryHolder[] {
                new EntryHolder(OriginEntrySource.EXTERNAL,"2004BL4631625CS0018000---ACAS07IB  01NOCSENAC 00000TP Generated Offset                               1650.00C2006-01-05          ----------                                 D                                "),
                new EntryHolder(OriginEntrySource.EXTERNAL,"2004BL4631625CS0014866---ACEX07IB  01NOCSENAC 00000Correction to: 01-PU3355206                       1650.00D2006-01-05          ----------                                 D                                "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL4631625CS0019915---ACTE07TF  CSCSHR01/0100000GENERATED COST SHARE FROM 4631625                 1650.00C2006-01-01          ----------                                                                  "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL4631625CS0018000---ACAS07TF  CSCSHR01/0100000GENERATED OFFSET                                  1650.00D2006-01-01          ----------                                                                  "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL1031400-----9940---ACTE07TF  CSCSHR01/0100000GENERATED COST SHARE FROM 4631625                 1650.00D2006-01-01          ----------                                                                  "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL1031400-----8000---ACAS07TF  CSCSHR01/0100000GENERATED OFFSET                                  1650.00C2006-01-01          ----------                                                                  "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL4631625CS0014866---ACEX07IB  01NOCSENAC 00000Correction to: 01-PU3355206                       1650.00D2006-01-05          ----------                                                                  "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL4631625CS0018000---ACAS07IB  01NOCSENAC 00000TP Generated Offset                               1650.00C2006-01-05          ----------                                                                  ")
        };

        scrub(input);
        assertOriginEntries(4,output);
    }

    public void testNoCostShareEncumbrancesForBudget() throws Exception {

        String[] input = new String[] {
                "2004BL4631618CS0014190---BBEX07GEC 01NOCSENBB 00000THOMAS BUSEY/NEWEGG COMPUTERS                       40.72 2006-01-05          ----------                                 D                                ",
                "2004BL4631618CS0018000---BBAS07GEC 01NOCSENBB 00000TP Generated Offset                                 40.72 2006-01-05          ----------                                 D                                "
        };

        EntryHolder[] output = new EntryHolder[] {
                new EntryHolder(OriginEntrySource.EXTERNAL,"2004BL4631618CS0014190---BBEX07GEC 01NOCSENBB 00000THOMAS BUSEY/NEWEGG COMPUTERS                       40.72 2006-01-05          ----------                                 D                                "),
                new EntryHolder(OriginEntrySource.EXTERNAL,"2004BL4631618CS0018000---BBAS07GEC 01NOCSENBB 00000TP Generated Offset                                 40.72 2006-01-05          ----------                                 D                                "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL4631618CS0014190---BBEX07GEC 01NOCSENBB 00000THOMAS BUSEY/NEWEGG COMPUTERS                       40.72 2006-01-05          ----------                                                                  "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL4631618CS0018000---BBAS07GEC 01NOCSENBB 00000TP Generated Offset                                 40.72 2006-01-05          ----------                                                                  ")
        };

        scrub(input);
        assertOriginEntries(4,output);
    }

    public void testNoCostShareForEncumbrancesNonExpenses() throws Exception {

        String[] input = new String[] {
                "2004BL4631601CS0011800---EXIN07TOPSLGNOCSENIN 00000225050007 WILLIAMS DOTSON ASSOCIATES IN           1200.00D2006-01-05          ----------                                 D                                ",
                "2004BL4631601CS0019041---EXLI07TOPSLGNOCSENIN 00000225050007 WILLIAMS DOTSON ASSOCIATES IN           1200.00C2006-01-05          ----------                                 D                                ",
        };

        EntryHolder[] output = new EntryHolder[] {
                new EntryHolder(OriginEntrySource.EXTERNAL,"2004BL4631601CS0011800---EXIN07TOPSLGNOCSENIN 00000225050007 WILLIAMS DOTSON ASSOCIATES IN           1200.00D2006-01-05          ----------                                 D                                "),
                new EntryHolder(OriginEntrySource.EXTERNAL,"2004BL4631601CS0019041---EXLI07TOPSLGNOCSENIN 00000225050007 WILLIAMS DOTSON ASSOCIATES IN           1200.00C2006-01-05          ----------                                 D                                "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL4631601CS0011800---EXIN07TOPSLGNOCSENIN 00000225050007 WILLIAMS DOTSON ASSOCIATES IN           1200.00D2006-01-05          ----------                                 D                                "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL4631601CS0019041---EXLI07TOPSLGNOCSENIN 00000225050007 WILLIAMS DOTSON ASSOCIATES IN           1200.00C2006-01-05          ----------                                 D                                ")
        };

        scrub(input);
        assertOriginEntries(4,output);
    }

    public void testCostShareOther() throws Exception {

        String[] input = new String[] {
                "2004BL4631625CS0014000---ACEX07ID33EUCSHROTHER00000NOV-05 IMU Business Office          2224           241.75D2005-11-30          ----------                                                                  ",
                "2004BL4631625CS0018000---ACAS07ID33EUCSHROTHER00000NOV-05 IMU Business Office          2237           241.75C2005-11-30          ----------                                                                  "
        };

        EntryHolder[] output = new EntryHolder[] {
                new EntryHolder(OriginEntrySource.EXTERNAL,"2004BL4631625CS0014000---ACEX07ID33EUCSHROTHER00000NOV-05 IMU Business Office          2224           241.75D2005-11-30          ----------                                                                  "),
                new EntryHolder(OriginEntrySource.EXTERNAL,"2004BL4631625CS0018000---ACAS07ID33EUCSHROTHER00000NOV-05 IMU Business Office          2237           241.75C2005-11-30          ----------                                                                  "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL4631625CS0019915---ACTE07TF  CSCSHR01/0100000GENERATED COST SHARE FROM 4631625                  241.75C2006-01-01          ----------                                                                  "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL4631625CS0018000---ACAS07TF  CSCSHR01/0100000GENERATED OFFSET                                   241.75D2006-01-01          ----------                                                                  "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL1031400-----9940---ACTE07TF  CSCSHR01/0100000GENERATED COST SHARE FROM 4631625                  241.75D2006-01-01          ----------                                                                  "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL1031400-----8000---ACAS07TF  CSCSHR01/0100000GENERATED OFFSET                                   241.75C2006-01-01          ----------                                                                  "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL4631625CS0014000---ACEX07ID33EUCSHROTHER00000NOV-05 IMU Business Office          2224           241.75D2005-11-30          ----------                                                                  "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL4631625CS0018000---ACAS07ID33EUCSHROTHER00000NOV-05 IMU Business Office          2237           241.75C2005-11-30          ----------                                                                  ")
        };

        scrub(input);
        assertOriginEntries(4,output);
    }

    public void testCostShareForLevelTrin() throws Exception {

        String[] input = new String[] {
                "2004BL4631618CS0019915---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                   94.35D2006-01-05          ----------                                                                  ",
                "2004BL4631618CS0019041---ACLI07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                   94.35C2006-01-05          ----------                                                                  "
        };

        EntryHolder[] output = new EntryHolder[] {
                new EntryHolder(OriginEntrySource.EXTERNAL,"2004BL4631618CS0019915---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                   94.35D2006-01-05          ----------                                                                  "),
                new EntryHolder(OriginEntrySource.EXTERNAL,"2004BL4631618CS0019041---ACLI07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                   94.35C2006-01-05          ----------                                                                  "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL4631618CS0019041---ACLI07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                   94.35C2006-01-05          ----------                                                                  "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL4631618CS0019915---ACTE07TF  CSCSHR01/0100000GENERATED COST SHARE FROM 4631618                   94.35C2006-01-01          ----------                                                                  "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL4631618CS0018000---ACAS07TF  CSCSHR01/0100000GENERATED OFFSET                                    94.35D2006-01-01          ----------                                                                  "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL1031400-----9915---ACTE07TF  CSCSHR01/0100000GENERATED COST SHARE FROM 4631618                   94.35D2006-01-01          ----------                                                                  "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL1031400-----8000---ACAS07TF  CSCSHR01/0100000GENERATED OFFSET                                    94.35C2006-01-01          ----------                                                                  "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL4631618CS0019915---ACEX07DI  01CSHRTRIN 00000Rite Quality Office Supplies Inc.                   94.35D2006-01-05          ----------                                                                  "),
        };

        scrub(input);
        assertOriginEntries(4,output);
    }

    public void testCostShareForLevelTrex() throws Exception {

        String[] input = new String[] {
                "2004BL4631601CS0019900---ACEX07CR  01CSHRTREX 00000Poplars Garage Fees                                 20.00C2006-01-05          ----------                                                                  ",
                "2004BL4631601CS0018000---ACAS07CR  01CSHRTREX 00000TP Generated Offset                                 20.00D2006-01-05          ----------                                                                  "
        };

        EntryHolder[] output = new EntryHolder[] {
                new EntryHolder(OriginEntrySource.EXTERNAL,"2004BL4631601CS0019900---ACEX07CR  01CSHRTREX 00000Poplars Garage Fees                                 20.00C2006-01-05          ----------                                                                  "),
                new EntryHolder(OriginEntrySource.EXTERNAL,"2004BL4631601CS0018000---ACAS07CR  01CSHRTREX 00000TP Generated Offset                                 20.00D2006-01-05          ----------                                                                  "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL4631601CS0018000---ACAS07CR  01CSHRTREX 00000TP Generated Offset                                 20.00D2006-01-05          ----------                                                                  "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL4631601CS0019915---ACTE07TF  CSCSHR01/0100000GENERATED COST SHARE FROM 4631601                   20.00D2006-01-01          ----------                                                                  "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL4631601CS0018000---ACAS07TF  CSCSHR01/0100000GENERATED OFFSET                                    20.00C2006-01-01          ----------                                                                  "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL1031400-----9959---ACTE07TF  CSCSHR01/0100000GENERATED COST SHARE FROM 4631601                   20.00C2006-01-01          ----------                                                                  "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL1031400-----8000---ACAS07TF  CSCSHR01/0100000GENERATED OFFSET                                    20.00D2006-01-01          ----------                                                                  "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL4631601CS0019900---ACEX07CR  01CSHRTREX 00000Poplars Garage Fees                                 20.00C2006-01-05          ----------                                                                  "),
        };

        scrub(input);
        assertOriginEntries(4,output);
    }

    public void testCostShareForLevelTrav() throws Exception {

        String[] input = new String[] {
                "2004BL4631625CS0016000---ACEX07ID33EUCSHRTRAV 00000NOV-05 IMU Business Office          2224           241.75D2005-11-30          ----------                                                                  ",
                "2004BL4631625CS0018000---ACAS07ID33EUCSHRTRAV 00000NOV-05 IMU Business Office          2237           241.75C2005-11-30          ----------                                                                  "
        };

        EntryHolder[] output = new EntryHolder[] {
                new EntryHolder(OriginEntrySource.EXTERNAL,"2004BL4631625CS0016000---ACEX07ID33EUCSHRTRAV 00000NOV-05 IMU Business Office          2224           241.75D2005-11-30          ----------                                                                  "),
                new EntryHolder(OriginEntrySource.EXTERNAL,"2004BL4631625CS0018000---ACAS07ID33EUCSHRTRAV 00000NOV-05 IMU Business Office          2237           241.75C2005-11-30          ----------                                                                  "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL4631625CS0019915---ACTE07TF  CSCSHR01/0100000GENERATED COST SHARE FROM 4631625                  241.75C2006-01-01          ----------                                                                  "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL4631625CS0018000---ACAS07TF  CSCSHR01/0100000GENERATED OFFSET                                   241.75D2006-01-01          ----------                                                                  "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL1031400-----9960---ACTE07TF  CSCSHR01/0100000GENERATED COST SHARE FROM 4631625                  241.75D2006-01-01          ----------                                                                  "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL1031400-----8000---ACAS07TF  CSCSHR01/0100000GENERATED OFFSET                                   241.75C2006-01-01          ----------                                                                  "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL4631625CS0016000---ACEX07ID33EUCSHRTRAV 00000NOV-05 IMU Business Office          2224           241.75D2005-11-30          ----------                                                                  "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID,"2004BL4631625CS0018000---ACAS07ID33EUCSHRTRAV 00000NOV-05 IMU Business Office          2237           241.75C2005-11-30          ----------                                                                  ")
        };

        scrub(input);
        assertOriginEntries(4,output);
    }

//    public void testCostShareForLevelTran() throws Exception {
//
//        // Inputs.
//        String[] stringInput = new String[] {
//                "2004BL4631618CS0015199---ACEX07DI  01CSHRTRAN      Rite Quality Office Supplies Inc.                   94.35D2006-01-05          ----------                                                                  ",
//                "2004BL4631618CS0019041---ACLI07DI  01CSHRTRAN      Rite Quality Office Supplies Inc.                   94.35C2006-01-05          ----------                                                                  "
//        };
//
//        String[] convertedStringInput = new String[] {
//                "2004BL4631618CS0015199---ACEX07DI  01CSHRTRAN 00000Rite Quality Office Supplies Inc.                   94.35D2006-01-05          ----------                                                                  ",
//                "2004BL4631618CS0019041---ACLI07DI  01CSHRTRAN 00000Rite Quality Office Supplies Inc.                   94.35C2006-01-05          ----------                                                                  "
//        };
//
//        // Add inputs to expected output ...
//        Vector expectedOutput = new Vector();
//        for(int i = 0; i < stringInput.length; i++) {
//            new EntryHolder(OriginEntrySource.EXTERNAL, convertedStringInput[i]));
//        }
//
//        // ... add expected output ...
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631618CS0019915---ACTE07TF  CSCSHR03/1300000GENERATED COST SHARE FROM 4631618                   94.35C2006-03-13          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631618CS0018000---ACAS07TF  CSCSHR03/1300000GENERATED OFFSET                                    94.35D2006-03-13          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL1031400-----9959---ACTE07TF  CSCSHR03/1300000GENERATED COST SHARE FROM 4631618                   94.35D2006-03-13          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL1031400-----8000---ACAS07TF  CSCSHR03/1300000GENERATED OFFSET                                    94.35C2006-03-13          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631618CS0015199---ACEX07DI  01CSHRTRAN 00000Rite Quality Office Supplies Inc.                   94.35D2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631618CS0019041---ACLI07DI  01CSHRTRAN 00000Rite Quality Office Supplies Inc.                   94.35C2006-01-05          ----------                                                                  "));
//
//        // ... and run the test.
//        scrub(stringInput);
//        assertOriginEntries(4,(EntryHolder[]) expectedOutput.toArray(new EntryHolder[0]));
//    }
//
//    public void testCostShareForLevelSaap() throws Exception {
//
//        // Inputs.
//        String[] stringInput = new String[] {
//                "2004BL4631601CS0012350---ACEX07CR  01CSHRSAAP      Poplars Garage Fees                                 20.00C2006-01-05          ----------                                                                  ",
//                "2004BL4631601CS0018000---ACAS07CR  01CSHRSAAP      TP Generated Offset                                 20.00D2006-01-05          ----------                                                                  "
//        };
//
//        String[] convertedStringInput = new String[] {
//                "2004BL4631601CS0012350---ACEX07CR  01CSHRSAAP 00000Poplars Garage Fees                                 20.00C2006-01-05          ----------                                                                  ",
//                "2004BL4631601CS0018000---ACAS07CR  01CSHRSAAP 00000TP Generated Offset                                 20.00D2006-01-05          ----------                                                                  "
//        };
//
//        // Add inputs to expected output ...
//        Vector expectedOutput = new Vector();
//        for(int i = 0; i < stringInput.length; i++) {
//            new EntryHolder(OriginEntrySource.EXTERNAL, convertedStringInput[i]));
//        }
//
//        // ... add expected output ...
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631601CS0019915---ACTE07TF  CSCSHR03/1300000GENERATED COST SHARE FROM 4631601                   20.00D2006-03-13          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631601CS0018000---ACAS07TF  CSCSHR03/1300000GENERATED OFFSET                                    20.00C2006-03-13          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL1031400-----9923---ACTE07TF  CSCSHR03/1300000GENERATED COST SHARE FROM 4631601                   20.00C2006-03-13          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL1031400-----8000---ACAS07TF  CSCSHR03/1300000GENERATED OFFSET                                    20.00D2006-03-13          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631601CS0012350---ACEX07CR  01CSHRSAAP 00000Poplars Garage Fees                                 20.00C2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631601CS0018000---ACAS07CR  01CSHRSAAP 00000TP Generated Offset                                 20.00D2006-01-05          ----------                                                                  "));
//
//        // ... and run the test.
//        scrub(stringInput);
//        assertOriginEntries(4,(EntryHolder[]) expectedOutput.toArray(new EntryHolder[0]));
//    }
//
//    public void testCostShareForLevelResv() throws Exception {
//
//        // Inputs.
//        String[] stringInput = new String[] {
//                "2004BL4631625CS0017900---ACEX07ID33EUCSHRRESV      NOV-05 IMU Business Office          2224           241.75D2005-11-30          ----------                                                                  ",
//                "2004BL4631625CS0018000---ACAS07ID33EUCSHRRESV      NOV-05 IMU Business Office          2237           241.75C2005-11-30          ----------                                                                  "
//        };
//
//        String[] convertedStringInput = new String[] {
//                "2004BL4631625CS0017900---ACEX07ID33EUCSHRRESV 00000NOV-05 IMU Business Office          2224           241.75D2005-11-30          ----------                                                                  ",
//                "2004BL4631625CS0018000---ACAS07ID33EUCSHRRESV 00000NOV-05 IMU Business Office          2237           241.75C2005-11-30          ----------                                                                  "
//        };
//
//        // Add inputs to expected output ...
//        Vector expectedOutput = new Vector();
//        for(int i = 0; i < stringInput.length; i++) {
//            new EntryHolder(OriginEntrySource.EXTERNAL, convertedStringInput[i]));
//        }
//
//        // ... add expected output ...
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631625CS0019915---ACTE07TF  CSCSHR03/1300000GENERATED COST SHARE FROM 4631625                  241.75C2006-03-13          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631625CS0018000---ACAS07TF  CSCSHR03/1300000GENERATED OFFSET                                   241.75D2006-03-13          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL1031400-----9979---ACTE07TF  CSCSHR03/1300000GENERATED COST SHARE FROM 4631625                  241.75D2006-03-13          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL1031400-----8000---ACAS07TF  CSCSHR03/1300000GENERATED OFFSET                                   241.75C2006-03-13          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631625CS0017900---ACEX07ID33EUCSHRRESV 00000NOV-05 IMU Business Office          2224           241.75D2005-11-30          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631625CS0018000---ACAS07ID33EUCSHRRESV 00000NOV-05 IMU Business Office          2237           241.75C2005-11-30          ----------                                                                  "));
//
//        // ... and run the test.
//        scrub(stringInput);
//        assertOriginEntries(4,(EntryHolder[]) expectedOutput.toArray(new EntryHolder[0]), dateTimeService.currentDate, true);
//
//    }
//
//    public void testCostShareForLevelPrsa() throws Exception {
//
//        // Inputs.
//        String[] stringInput = new String[] {
//                "2004BL4631618CS0012400---ACEX07DI  01CSHRPRSA      Rite Quality Office Supplies Inc.                   94.35D2006-01-05          ----------                                                                  ",
//                "2004BL4631618CS0019041---ACLI07DI  01CSHRPRSA      Rite Quality Office Supplies Inc.                   94.35C2006-01-05          ----------                                                                  "
//        };
//
//        String[] convertedStringInput = new String[] {
//                "2004BL4631618CS0012400---ACEX07DI  01CSHRPRSA 00000Rite Quality Office Supplies Inc.                   94.35D2006-01-05          ----------                                                                  ",
//                "2004BL4631618CS0019041---ACLI07DI  01CSHRPRSA 00000Rite Quality Office Supplies Inc.                   94.35C2006-01-05          ----------                                                                  "
//        };
//
//        // Add inputs to expected output ...
//        Vector expectedOutput = new Vector();
//        for(int i = 0; i < stringInput.length; i++) {
//            new EntryHolder(OriginEntrySource.EXTERNAL, convertedStringInput[i]));
//        }
//
//        // ... add expected output ...
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631618CS0019915---ACTE07TF  CSCSHR03/1300000GENERATED COST SHARE FROM 4631618                   94.35C2006-03-13          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631618CS0018000---ACAS07TF  CSCSHR03/1300000GENERATED OFFSET                                    94.35D2006-03-13          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL1031400-----9924---ACTE07TF  CSCSHR03/1300000GENERATED COST SHARE FROM 4631618                   94.35D2006-03-13          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL1031400-----8000---ACAS07TF  CSCSHR03/1300000GENERATED OFFSET                                    94.35C2006-03-13          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631618CS0012400---ACEX07DI  01CSHRPRSA 00000Rite Quality Office Supplies Inc.                   94.35D2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631618CS0019041---ACLI07DI  01CSHRPRSA 00000Rite Quality Office Supplies Inc.                   94.35C2006-01-05          ----------                                                                  "));
//
//        // ... and run the test.
//        scrub(stringInput);
//        assertOriginEntries(4,(EntryHolder[]) expectedOutput.toArray(new EntryHolder[0]), dateTimeService.currentDate, true);
//
//    }
//
//    public void testCostShareForLevelPart() throws Exception {
//
//        // Inputs.
//        String[] stringInput = new String[] {
//                "2004BL4631601CS0012300---ACEX07CR  01CSHRPART      Poplars Garage Fees                                 20.00C2006-01-05          ----------                                                                  ",
//                "2004BL4631601CS0018000---ACAS07CR  01CSHRPART      TP Generated Offset                                 20.00D2006-01-05          ----------                                                                  "
//        };
//
//        String[] convertedStringInput = new String[] {
//                "2004BL4631601CS0012300---ACEX07CR  01CSHRPART 00000Poplars Garage Fees                                 20.00C2006-01-05          ----------                                                                  ",
//                "2004BL4631601CS0018000---ACAS07CR  01CSHRPART 00000TP Generated Offset                                 20.00D2006-01-05          ----------                                                                  "
//        };
//
//        // Add inputs to expected output ...
//        Vector expectedOutput = new Vector();
//        for(int i = 0; i < stringInput.length; i++) {
//            new EntryHolder(OriginEntrySource.EXTERNAL, convertedStringInput[i]));
//        }
//
//        // ... add expected output ...
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631601CS0019915---ACTE07TF  CSCSHR03/1300000GENERATED COST SHARE FROM 4631601                   20.00D2006-03-13          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631601CS0018000---ACAS07TF  CSCSHR03/1300000GENERATED OFFSET                                    20.00C2006-03-13          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL1031400-----9923---ACTE07TF  CSCSHR03/1300000GENERATED COST SHARE FROM 4631601                   20.00C2006-03-13          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL1031400-----8000---ACAS07TF  CSCSHR03/1300000GENERATED OFFSET                                    20.00D2006-03-13          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631601CS0012300---ACEX07CR  01CSHRPART 00000Poplars Garage Fees                                 20.00C2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631601CS0018000---ACAS07CR  01CSHRPART 00000TP Generated Offset                                 20.00D2006-01-05          ----------                                                                  "));
//
//        // ... and run the test.
//        scrub(stringInput);
//        assertOriginEntries(4,(EntryHolder[]) expectedOutput.toArray(new EntryHolder[0]), dateTimeService.currentDate, true);
//
//    }
//
//    public void testCostShareForLevelIcoe() throws Exception {
//
//        // Inputs.
//        String[] stringInput = new String[] {
//                "2004BL4631625CS0015500---ACEX07ID33EUCSHRICOE      NOV-05 IMU Business Office          2224           241.75D2005-11-30          ----------                                                                  ",
//                "2004BL4631625CS0018000---ACAS07ID33EUCSHRICOE      NOV-05 IMU Business Office          2237           241.75C2005-11-30          ----------                                                                  "
//        };
//
//        String[] convertedStringInput = new String[] {
//                "2004BL4631625CS0015500---ACEX07ID33EUCSHRICOE 00000NOV-05 IMU Business Office          2224           241.75D2005-11-30          ----------                                                                  ",
//                "2004BL4631625CS0018000---ACAS07ID33EUCSHRICOE 00000NOV-05 IMU Business Office          2237           241.75C2005-11-30          ----------                                                                  "
//        };
//
//        // Add inputs to expected output ...
//        Vector expectedOutput = new Vector();
//        for(int i = 0; i < stringInput.length; i++) {
//            new EntryHolder(OriginEntrySource.EXTERNAL, convertedStringInput[i]));
//        }
//
//        // ... add expected output ...
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631625CS0019915---ACTE07TF  CSCSHR03/1300000GENERATED COST SHARE FROM 4631625                  241.75C2006-03-13          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631625CS0018000---ACAS07TF  CSCSHR03/1300000GENERATED OFFSET                                   241.75D2006-03-13          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL1031400-----9955---ACTE07TF  CSCSHR03/1300000GENERATED COST SHARE FROM 4631625                  241.75D2006-03-13          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL1031400-----8000---ACAS07TF  CSCSHR03/1300000GENERATED OFFSET                                   241.75C2006-03-13          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631625CS0015500---ACEX07ID33EUCSHRICOE 00000NOV-05 IMU Business Office          2224           241.75D2005-11-30          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631625CS0018000---ACAS07ID33EUCSHRICOE 00000NOV-05 IMU Business Office          2237           241.75C2005-11-30          ----------                                                                  "));
//
//        // ... and run the test.
//        scrub(stringInput);
//        assertOriginEntries(4,(EntryHolder[]) expectedOutput.toArray(new EntryHolder[0]), dateTimeService.currentDate, true);
//
//    }
//
//    public void testCostShareForLevelHrco() throws Exception {
//
//        // Inputs.
//        String[] stringInput = new String[] {
//                "2004BL4631618CS0013000---ACEX07DI  01CSHRHRCO      Rite Quality Office Supplies Inc.                   94.35D2006-01-05          ----------                                                                  ",
//                "2004BL4631618CS0019041---ACLI07DI  01CSHRHRCO      Rite Quality Office Supplies Inc.                   94.35C2006-01-05          ----------                                                                  "
//        };
//
//        String[] convertedStringInput = new String[] {
//                "2004BL4631618CS0013000---ACEX07DI  01CSHRHRCO 00000Rite Quality Office Supplies Inc.                   94.35D2006-01-05          ----------                                                                  ",
//                "2004BL4631618CS0019041---ACLI07DI  01CSHRHRCO 00000Rite Quality Office Supplies Inc.                   94.35C2006-01-05          ----------                                                                  "
//        };
//
//        // Add inputs to expected output ...
//        Vector expectedOutput = new Vector();
//        for(int i = 0; i < stringInput.length; i++) {
//            new EntryHolder(OriginEntrySource.EXTERNAL, convertedStringInput[i]));
//        }
//
//        // ... add expected output ...
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631618CS0019915---ACTE07TF  CSCSHR03/1300000GENERATED COST SHARE FROM 4631618                   94.35C2006-03-13          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631618CS0018000---ACAS07TF  CSCSHR03/1300000GENERATED OFFSET                                    94.35D2006-03-13          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL1031400-----9930---ACTE07TF  CSCSHR03/1300000GENERATED COST SHARE FROM 4631618                   94.35D2006-03-13          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL1031400-----8000---ACAS07TF  CSCSHR03/1300000GENERATED OFFSET                                    94.35C2006-03-13          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631618CS0013000---ACEX07DI  01CSHRHRCO 00000Rite Quality Office Supplies Inc.                   94.35D2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631618CS0019041---ACLI07DI  01CSHRHRCO 00000Rite Quality Office Supplies Inc.                   94.35C2006-01-05          ----------                                                                  "));
//
//        // ... and run the test.
//        scrub(stringInput);
//        assertOriginEntries(4,(EntryHolder[]) expectedOutput.toArray(new EntryHolder[0]), dateTimeService.currentDate, true);
//
//    }
//
//    public void testCostShareForLevelFina2() throws Exception {
//
//        // Inputs.
//        String[] stringInput = new String[] {
//                "2004BL4631601CS0015800---ACEX07CR  01CSHRFINA2     Poplars Garage Fees                                 20.00C2006-01-05          ----------                                                                  ",
//                "2004BL4631601CS0018000---ACAS07CR  01CSHRFINA2     TP Generated Offset                                 20.00D2006-01-05          ----------                                                                  "
//        };
//
//        String[] convertedStringInput = new String[] {
//                "2004BL4631601CS0015800---ACEX07CR  01CSHRFINA200000Poplars Garage Fees                                 20.00C2006-01-05          ----------                                                                  ",
//                "2004BL4631601CS0018000---ACAS07CR  01CSHRFINA200000TP Generated Offset                                 20.00D2006-01-05          ----------                                                                  "
//        };
//
//        // Add inputs to expected output ...
//        Vector expectedOutput = new Vector();
//        for(int i = 0; i < stringInput.length; i++) {
//            new EntryHolder(OriginEntrySource.EXTERNAL, convertedStringInput[i]));
//        }
//
//        // ... add expected output ...
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631601CS0019915---ACTE07TF  CSCSHR03/1300000GENERATED COST SHARE FROM 4631601                   20.00D2006-03-13          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631601CS0018000---ACAS07TF  CSCSHR03/1300000GENERATED OFFSET                                    20.00C2006-03-13          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL1031400-----9958---ACTE07TF  CSCSHR03/1300000GENERATED COST SHARE FROM 4631601                   20.00C2006-03-13          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL1031400-----8000---ACAS07TF  CSCSHR03/1300000GENERATED OFFSET                                    20.00D2006-03-13          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631601CS0015800---ACEX07CR  01CSHRFINA200000Poplars Garage Fees                                 20.00C2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631601CS0018000---ACAS07CR  01CSHRFINA200000TP Generated Offset                                 20.00D2006-01-05          ----------                                                                  "));
//
//        // ... and run the test.
//        scrub(stringInput);
//        assertOriginEntries(4,(EntryHolder[]) expectedOutput.toArray(new EntryHolder[0]), dateTimeService.currentDate, true);
//
//    }
//
//    public void testCostShareForLevelFina1() throws Exception {
//
//        // Inputs.
//        String[] stringInput = new String[] {
//                "2004BL4631625CS0015400---ACEX07ID33EUCSHRFINA1     NOV-05 IMU Business Office          2224           241.75D2005-11-30          ----------                                                                  ",
//                "2004BL4631625CS0018000---ACAS07ID33EUCSHRFINA1     NOV-05 IMU Business Office          2237           241.75C2005-11-30          ----------                                                                  "
//        };
//
//        String[] convertedStringInput = new String[] {
//                "2004BL4631625CS0015400---ACEX07ID33EUCSHRFINA100000NOV-05 IMU Business Office          2224           241.75D2005-11-30          ----------                                                                  ",
//                "2004BL4631625CS0018000---ACAS07ID33EUCSHRFINA100000NOV-05 IMU Business Office          2237           241.75C2005-11-30          ----------                                                                  "
//        };
//
//        // Add inputs to expected output ...
//        Vector expectedOutput = new Vector();
//        for(int i = 0; i < stringInput.length; i++) {
//            new EntryHolder(OriginEntrySource.EXTERNAL, convertedStringInput[i]));
//        }
//
//        // ... add expected output ...
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631625CS0019915---ACTE07TF  CSCSHR03/1300000GENERATED COST SHARE FROM 4631625                  241.75C2006-03-13          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631625CS0018000---ACAS07TF  CSCSHR03/1300000GENERATED OFFSET                                   241.75D2006-03-13          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL1031400-----9954---ACTE07TF  CSCSHR03/1300000GENERATED COST SHARE FROM 4631625                  241.75D2006-03-13          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL1031400-----8000---ACAS07TF  CSCSHR03/1300000GENERATED OFFSET                                   241.75C2006-03-13          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631625CS0015400---ACEX07ID33EUCSHRFINA100000NOV-05 IMU Business Office          2224           241.75D2005-11-30          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631625CS0018000---ACAS07ID33EUCSHRFINA100000NOV-05 IMU Business Office          2237           241.75C2005-11-30          ----------                                                                  "));
//
//        // ... and run the test.
//        scrub(stringInput);
//        assertOriginEntries(4,(EntryHolder[]) expectedOutput.toArray(new EntryHolder[0]), dateTimeService.currentDate, true);
//
//    }
//
//    public void testCostShareForLevelCori() throws Exception {
//
//        // Inputs.
//        String[] stringInput = new String[] {
//                "2004BL4631618CS0019912---ACEX07DI  01CSHRCORI      Rite Quality Office Supplies Inc.                   94.35D2006-01-05          ----------                                                                  ",
//                "2004BL4631618CS0019041---ACLI07DI  01CSHRCORI      Rite Quality Office Supplies Inc.                   94.35C2006-01-05          ----------                                                                  "
//        };
//
//        String[] convertedStringInput = new String[] {
//                "2004BL4631618CS0019912---ACEX07DI  01CSHRCORI 00000Rite Quality Office Supplies Inc.                   94.35D2006-01-05          ----------                                                                  ",
//                "2004BL4631618CS0019041---ACLI07DI  01CSHRCORI 00000Rite Quality Office Supplies Inc.                   94.35C2006-01-05          ----------                                                                  "
//        };
//
//        // Add inputs to expected output ...
//        Vector expectedOutput = new Vector();
//        for(int i = 0; i < stringInput.length; i++) {
//            new EntryHolder(OriginEntrySource.EXTERNAL, convertedStringInput[i]));
//        }
//
//        // ... add expected output ...
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631618CS0019915---ACTE07TF  CSCSHR03/1300000GENERATED COST SHARE FROM 4631618                   94.35C2006-03-13          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631618CS0018000---ACAS07TF  CSCSHR03/1300000GENERATED OFFSET                                    94.35D2006-03-13          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL1031400-----9912---ACTE07TF  CSCSHR03/1300000GENERATED COST SHARE FROM 4631618                   94.35D2006-03-13          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL1031400-----8000---ACAS07TF  CSCSHR03/1300000GENERATED OFFSET                                    94.35C2006-03-13          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631618CS0019912---ACEX07DI  01CSHRCORI 00000Rite Quality Office Supplies Inc.                   94.35D2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631618CS0019041---ACLI07DI  01CSHRCORI 00000Rite Quality Office Supplies Inc.                   94.35C2006-01-05          ----------                                                                  "));
//
//        // ... and run the test.
//        scrub(stringInput);
//        assertOriginEntries(4,(EntryHolder[]) expectedOutput.toArray(new EntryHolder[0]), dateTimeService.currentDate, true);
//
//    }
//
//    public void testCostShareForLevelCore() throws Exception {
//
//        // Inputs.
//        String[] stringInput = new String[] {
//                "2004BL4631601CS0019951---ACEX07CR  01CSHRCORE      Poplars Garage Fees                                 20.00C2006-01-05          ----------                                                                  ",
//                "2004BL4631601CS0018000---ACAS07CR  01CSHRCORE      TP Generated Offset                                 20.00D2006-01-05          ----------                                                                  "
//        };
//
//        String[] convertedStringInput = new String[] {
//                "2004BL4631601CS0019951---ACEX07CR  01CSHRCORE 00000Poplars Garage Fees                                 20.00C2006-01-05          ----------                                                                  ",
//                "2004BL4631601CS0018000---ACAS07CR  01CSHRCORE 00000TP Generated Offset                                 20.00D2006-01-05          ----------                                                                  "
//        };
//
//        // Add inputs to expected output ...
//        Vector expectedOutput = new Vector();
//        for(int i = 0; i < stringInput.length; i++) {
//            new EntryHolder(OriginEntrySource.EXTERNAL, convertedStringInput[i]));
//        }
//
//        // ... add expected output ...
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631601CS0019915---ACTE07TF  CSCSHR03/1300000GENERATED COST SHARE FROM 4631601                   20.00D2006-03-13          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631601CS0018000---ACAS07TF  CSCSHR03/1300000GENERATED OFFSET                                    20.00C2006-03-13          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL1031400-----9951---ACTE07TF  CSCSHR03/1300000GENERATED COST SHARE FROM 4631601                   20.00C2006-03-13          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL1031400-----8000---ACAS07TF  CSCSHR03/1300000GENERATED OFFSET                                    20.00D2006-03-13          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631601CS0019951---ACEX07CR  01CSHRCORE 00000Poplars Garage Fees                                 20.00C2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631601CS0018000---ACAS07CR  01CSHRCORE 00000TP Generated Offset                                 20.00D2006-01-05          ----------                                                                  "));
//
//        // ... and run the test.
//        scrub(stringInput);
//        assertOriginEntries(4,(EntryHolder[]) expectedOutput.toArray(new EntryHolder[0]), dateTimeService.currentDate, true);
//
//    }
//
//    public void testCostShareForLevelCap() throws Exception {
//
//        // Inputs.
//        String[] stringInput = new String[] {
//                "2004BL4631625CS0017000---ACEX07ID33EUCSHRCAP       NOV-05 IMU Business Office          2224           241.75D2005-11-30          ----------                                                                  ",
//                "2004BL4631625CS0018000---ACAS07ID33EUCSHRCAP       NOV-05 IMU Business Office          2237           241.75C2005-11-30          ----------                                                                  "
//        };
//
//        String[] convertedStringInput = new String[] {
//                "2004BL4631625CS0017000---ACEX07ID33EUCSHRCAP  00000NOV-05 IMU Business Office          2224           241.75D2005-11-30          ----------                                                                  ",
//                "2004BL4631625CS0018000---ACAS07ID33EUCSHRCAP  00000NOV-05 IMU Business Office          2237           241.75C2005-11-30          ----------                                                                  "
//        };
//
//        // Add inputs to expected output ...
//        Vector expectedOutput = new Vector();
//        for(int i = 0; i < stringInput.length; i++) {
//            new EntryHolder(OriginEntrySource.EXTERNAL, convertedStringInput[i]));
//        }
//
//        // ... add expected output ...
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL9520004-----8610---ACAS07ID33EUCSHRCAP  00000GENERATED CAPITALIZATION                           241.75D2005-11-30          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL9520004-----9899---ACFB07ID33EUCSHRCAP  00000GENERATED CAPITALIZATION                           241.75C2005-11-30          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631625CS0019915---ACTE07TF  CSCSHR03/1300000GENERATED COST SHARE FROM 4631625                  241.75C2006-03-13          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631625CS0018000---ACAS07TF  CSCSHR03/1300000GENERATED OFFSET                                   241.75D2006-03-13          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL1031400-----9970---ACTE07TF  CSCSHR03/1300000GENERATED COST SHARE FROM 4631625                  241.75D2006-03-13          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL1031400-----8000---ACAS07TF  CSCSHR03/1300000GENERATED OFFSET                                   241.75C2006-03-13          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631625CS0017000---ACEX07ID33EUCSHRCAP  00000NOV-05 IMU Business Office          2224           241.75D2005-11-30          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631625CS0018000---ACAS07ID33EUCSHRCAP  00000NOV-05 IMU Business Office          2237           241.75C2005-11-30          ----------                                                                  "));
//
//        // ... and run the test.
//        scrub(stringInput);
//        assertOriginEntries(4,(EntryHolder[]) expectedOutput.toArray(new EntryHolder[0]), dateTimeService.currentDate, true);
//
//    }
//
//    public void testCostShareForLevelBisa() throws Exception {
//
//        // Inputs.
//        String[] stringInput = new String[] {
//                "2004BL4631618CS0012500---ACEX07DI  01CSHRBISA      Rite Quality Office Supplies Inc.                   94.35D2006-01-05          ----------                                                                  ",
//                "2004BL4631618CS0019041---ACLI07DI  01CSHRBISA      Rite Quality Office Supplies Inc.                   94.35C2006-01-05          ----------                                                                  "
//        };
//
//        String[] convertedStringInput = new String[] {
//                "2004BL4631618CS0012500---ACEX07DI  01CSHRBISA 00000Rite Quality Office Supplies Inc.                   94.35D2006-01-05          ----------                                                                  ",
//                "2004BL4631618CS0019041---ACLI07DI  01CSHRBISA 00000Rite Quality Office Supplies Inc.                   94.35C2006-01-05          ----------                                                                  "
//        };
//
//        // Add inputs to expected output ...
//        Vector expectedOutput = new Vector();
//        for(int i = 0; i < stringInput.length; i++) {
//            new EntryHolder(OriginEntrySource.EXTERNAL, convertedStringInput[i]));
//        }
//
//        // ... add expected output ...
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631618CS0019915---ACTE07TF  CSCSHR03/1300000GENERATED COST SHARE FROM 4631618                   94.35C2006-03-13          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631618CS0018000---ACAS07TF  CSCSHR03/1300000GENERATED OFFSET                                    94.35D2006-03-13          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL1031400-----9925---ACTE07TF  CSCSHR03/1300000GENERATED COST SHARE FROM 4631618                   94.35D2006-03-13          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL1031400-----8000---ACAS07TF  CSCSHR03/1300000GENERATED OFFSET                                    94.35C2006-03-13          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631618CS0012500---ACEX07DI  01CSHRBISA 00000Rite Quality Office Supplies Inc.                   94.35D2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631618CS0019041---ACLI07DI  01CSHRBISA 00000Rite Quality Office Supplies Inc.                   94.35C2006-01-05          ----------                                                                  "));
//
//        // ... and run the test.
//        scrub(stringInput);
//        assertOriginEntries(4,(EntryHolder[]) expectedOutput.toArray(new EntryHolder[0]), dateTimeService.currentDate, true);
//
//    }
//
//    public void testCostShareForLevelBenf7() throws Exception {
//
//        // Inputs.
//        String[] stringInput = new String[] {
//                "2004BL4631601CS0015760---ACEX07CR  01CSHRBENF7     Poplars Garage Fees                                 20.00C2006-01-05          ----------                                                                  ",
//                "2004BL4631601CS0018000---ACAS07CR  01CSHRBENF7     TP Generated Offset                                 20.00D2006-01-05          ----------                                                                  "
//        };
//
//        String[] convertedStringInput = new String[] {
//                "2004BL4631601CS0015760---ACEX07CR  01CSHRBENF700000Poplars Garage Fees                                 20.00C2006-01-05          ----------                                                                  ",
//                "2004BL4631601CS0018000---ACAS07CR  01CSHRBENF700000TP Generated Offset                                 20.00D2006-01-05          ----------                                                                  "
//        };
//
//        // Add inputs to expected output ...
//        Vector expectedOutput = new Vector();
//        for(int i = 0; i < stringInput.length; i++) {
//            new EntryHolder(OriginEntrySource.EXTERNAL, convertedStringInput[i]));
//        }
//
//        // ... add expected output ...
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631601CS0019915---ACTE07TF  CSCSHR03/1300000GENERATED COST SHARE FROM 4631601                   20.00D2006-03-13          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631601CS0018000---ACAS07TF  CSCSHR03/1300000GENERATED OFFSET                                    20.00C2006-03-13          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL1031400-----9957---ACTE07TF  CSCSHR03/1300000GENERATED COST SHARE FROM 4631601                   20.00C2006-03-13          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL1031400-----8000---ACAS07TF  CSCSHR03/1300000GENERATED OFFSET                                    20.00D2006-03-13          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631601CS0015760---ACEX07CR  01CSHRBENF700000Poplars Garage Fees                                 20.00C2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631601CS0018000---ACAS07CR  01CSHRBENF700000TP Generated Offset                                 20.00D2006-01-05          ----------                                                                  "));
//
//        // ... and run the test.
//        scrub(stringInput);
//        assertOriginEntries(4,(EntryHolder[]) expectedOutput.toArray(new EntryHolder[0]), dateTimeService.currentDate, true);
//
//    }
//
//    public void testCostShareForLevelBenf6() throws Exception {
//
//        // Inputs.
//        String[] stringInput = new String[] {
//                "2004BL4631625CS0015625---ACEX07ID33EUCSHRBENF6     NOV-05 IMU Business Office          2224           241.75D2005-11-30          ----------                                                                  ",
//                "2004BL4631625CS0018000---ACAS07ID33EUCSHRBENF6     NOV-05 IMU Business Office          2237           241.75C2005-11-30          ----------                                                                  "
//        };
//
//        String[] convertedStringInput = new String[] {
//                "2004BL4631625CS0015625---ACEX07ID33EUCSHRBENF600000NOV-05 IMU Business Office          2224           241.75D2005-11-30          ----------                                                                  ",
//                "2004BL4631625CS0018000---ACAS07ID33EUCSHRBENF600000NOV-05 IMU Business Office          2237           241.75C2005-11-30          ----------                                                                  "
//        };
//
//        // Add inputs to expected output ...
//        Vector expectedOutput = new Vector();
//        for(int i = 0; i < stringInput.length; i++) {
//            new EntryHolder(OriginEntrySource.EXTERNAL, convertedStringInput[i]));
//        }
//
//        // ... add expected output ...
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631625CS0019915---ACTE07TF  CSCSHR03/1300000GENERATED COST SHARE FROM 4631625                  241.75C2006-03-13          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631625CS0018000---ACAS07TF  CSCSHR03/1300000GENERATED OFFSET                                   241.75D2006-03-13          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL1031400-----9956---ACTE07TF  CSCSHR03/1300000GENERATED COST SHARE FROM 4631625                  241.75D2006-03-13          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL1031400-----8000---ACAS07TF  CSCSHR03/1300000GENERATED OFFSET                                   241.75C2006-03-13          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631625CS0015625---ACEX07ID33EUCSHRBENF600000NOV-05 IMU Business Office          2224           241.75D2005-11-30          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631625CS0018000---ACAS07ID33EUCSHRBENF600000NOV-05 IMU Business Office          2237           241.75C2005-11-30          ----------                                                                  "));
//
//        // ... and run the test.
//        scrub(stringInput);
//        assertOriginEntries(4,(EntryHolder[]) expectedOutput.toArray(new EntryHolder[0]), dateTimeService.currentDate, true);
//
//    }
//
//    public void testCostShareForLevelBase() throws Exception {
//
//        // Inputs.
//        String[] stringInput = new String[] {
//                "2004BL4631618CS0015509---ACEX07DI  01CSHRBASE      Rite Quality Office Supplies Inc.                   94.35D2006-01-05          ----------                                                                  ",
//                "2004BL4631618CS0019041---ACLI07DI  01CSHRBASE      Rite Quality Office Supplies Inc.                   94.35C2006-01-05          ----------                                                                  "
//        };
//
//        String[] convertedStringInput = new String[] {
//                "2004BL4631618CS0015509---ACEX07DI  01CSHRBASE 00000Rite Quality Office Supplies Inc.                   94.35D2006-01-05          ----------                                                                  ",
//                "2004BL4631618CS0019041---ACLI07DI  01CSHRBASE 00000Rite Quality Office Supplies Inc.                   94.35C2006-01-05          ----------                                                                  "
//        };
//
//        // Add inputs to expected output ...
//        Vector expectedOutput = new Vector();
//        for(int i = 0; i < stringInput.length; i++) {
//            new EntryHolder(OriginEntrySource.EXTERNAL, convertedStringInput[i]));
//        }
//
//        // ... add expected output ...
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631618CS0019915---ACTE07TF  CSCSHR03/1300000GENERATED COST SHARE FROM 4631618                   94.35C2006-03-13          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631618CS0018000---ACAS07TF  CSCSHR03/1300000GENERATED OFFSET                                    94.35D2006-03-13          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL1031400-----9959---ACTE07TF  CSCSHR03/1300000GENERATED COST SHARE FROM 4631618                   94.35D2006-03-13          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL1031400-----8000---ACAS07TF  CSCSHR03/1300000GENERATED OFFSET                                    94.35C2006-03-13          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631618CS0015509---ACEX07DI  01CSHRBASE 00000Rite Quality Office Supplies Inc.                   94.35D2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631618CS0019041---ACLI07DI  01CSHRBASE 00000Rite Quality Office Supplies Inc.                   94.35C2006-01-05          ----------                                                                  "));
//
//        // ... and run the test.
//        scrub(stringInput);
//        assertOriginEntries(4,(EntryHolder[]) expectedOutput.toArray(new EntryHolder[0]), dateTimeService.currentDate, true);
//
//    }
//
//    public void testCostShareForLevelAcsa() throws Exception {
//
//        // Inputs.
//        String[] stringInput = new String[] {
//                "2004BL4631601CS0012000---ACEX07CR  01CSHRACSA      Poplars Garage Fees                                 20.00C2006-01-05          ----------                                                                  ",
//                "2004BL4631601CS0018000---ACAS07CR  01CSHRACSA      TP Generated Offset                                 20.00D2006-01-05          ----------                                                                  "
//        };
//
//        String[] convertedStringInput = new String[] {
//                "2004BL4631601CS0012000---ACEX07CR  01CSHRACSA 00000Poplars Garage Fees                                 20.00C2006-01-05          ----------                                                                  ",
//                "2004BL4631601CS0018000---ACAS07CR  01CSHRACSA 00000TP Generated Offset                                 20.00D2006-01-05          ----------                                                                  "
//        };
//
//        // Add inputs to expected output ...
//        Vector expectedOutput = new Vector();
//        for(int i = 0; i < stringInput.length; i++) {
//            new EntryHolder(OriginEntrySource.EXTERNAL, convertedStringInput[i]));
//        }
//
//        // ... add expected output ...
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631601CS0019915---ACTE07TF  CSCSHR02/2100000GENERATED COST SHARE FROM 4631601                   20.00D2006-02-21          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631601CS0018000---ACAS07TF  CSCSHR02/2100000GENERATED OFFSET                                    20.00C2006-02-21          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL1031400-----9920---ACTE07TF  CSCSHR02/2100000GENERATED COST SHARE FROM 4631601                   20.00C2006-02-21          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL1031400-----8000---ACAS07TF  CSCSHR02/2100000GENERATED OFFSET                                    20.00D2006-02-21          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631601CS0012000---ACEX07CR  01CSHRACSA 00000Poplars Garage Fees                                 20.00C2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631601CS0018000---ACAS07CR  01CSHRACSA 00000TP Generated Offset                                 20.00D2006-01-05          ----------                                                                  "));
//
//        // ... and run the test.
//        scrub(stringInput);
//        assertOriginEntries(4,(EntryHolder[]) expectedOutput.toArray(new EntryHolder[0]), dateTimeService.currentDate, true);
//
//    }
//
//    public void testNoCostShareTransfersForCertainDocumentTypes() throws Exception {
//
//        // Inputs.
//        String[] stringInput = new String[] {
//                "2004BL4631618CS0015000---ACEX07JV  01NOCSHRJV      Rite Quality Office Supplies Inc.                   94.35D2006-01-05          ----------                                                                  ",
//                "2004BL4631618CS0019041---ACLI07JV  01NOCSHRJV      Rite Quality Office Supplies Inc.                   94.35C2006-01-05          ----------                                                                  "
//        };
//
//        String[] convertedStringInput = new String[] {
//                "2004BL4631618CS0015000---ACEX07JV  01NOCSHRJV 00000Rite Quality Office Supplies Inc.                   94.35D2006-01-05          ----------                                                                  ",
//                "2004BL4631618CS0019041---ACLI07JV  01NOCSHRJV 00000Rite Quality Office Supplies Inc.                   94.35C2006-01-05          ----------                                                                  "
//        };
//
//        // Add inputs to expected output ...
//        Vector expectedOutput = new Vector();
//        for(int i = 0; i < stringInput.length; i++) {
//            new EntryHolder(OriginEntrySource.EXTERNAL, convertedStringInput[i]));
//        }
//
//        // ... add expected output ...
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631618CS0015000---ACEX07JV  01NOCSHRJV 00000Rite Quality Office Supplies Inc.                   94.35D2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631618CS0019041---ACLI07JV  01NOCSHRJV 00000Rite Quality Office Supplies Inc.                   94.35C2006-01-05          ----------                                                                  "));
//
//        // ... and run the test.
//        scrub(stringInput);
//        assertOriginEntries(4,(EntryHolder[]) expectedOutput.toArray(new EntryHolder[0]), dateTimeService.currentDate);
//
//    }
//
//    public void testNoCostShareTransfersForBeginningBalanceTransactions() throws Exception {
//
//        // Inputs.
//        String[] stringInput = new String[] {
//                "2004BL4631601CS0015000---ACEXCBCR  01NOCSHRCB      Poplars Garage Fees                                 20.00C2006-01-05          ----------                                                                  ",
//                "2004BL4631601CS0018000---ACASCBCR  01NOCSHRCB      TP Generated Offset                                 20.00D2006-01-05          ----------                                                                  "
//        };
//
//        String[] convertedStringInput = new String[] {
//                "2004BL4631601CS0015000---ACEXCBCR  01NOCSHRCB 00000Poplars Garage Fees                                 20.00C2006-01-05          ----------                                                                  ",
//                "2004BL4631601CS0018000---ACASCBCR  01NOCSHRCB 00000TP Generated Offset                                 20.00D2006-01-05          ----------                                                                  "
//        };
//
//        // Add inputs to expected output ...
//        Vector expectedOutput = new Vector();
//        for(int i = 0; i < stringInput.length; i++) {
//            new EntryHolder(OriginEntrySource.EXTERNAL, convertedStringInput[i]));
//        }
//
//        // ... add expected output ...
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631601CS0015000---ACEXCBCR  01NOCSHRCB 00000Poplars Garage Fees                                 20.00C2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631601CS0018000---ACASCBCR  01NOCSHRCB 00000TP Generated Offset                                 20.00D2006-01-05          ----------                                                                  "));
//
//        // ... and run the test.
//        scrub(stringInput);
//        assertOriginEntries(4,(EntryHolder[]) expectedOutput.toArray(new EntryHolder[0]), dateTimeService.currentDate);
//
//    }
//
//    public void testNoCostShareTransfersForEncumbranceTransactions() throws Exception {
//
//        // Inputs.
//        String[] stringInput = new String[] {
//                "2004BL4631625CS0014110---EXEX07EXENEUNOCSHREX      NOV-05 IMU Business Office          2224           241.75D2005-11-30          ----------                                 D                                ",
//                "2004BL4631625CS0018000---EXAS07EXENEUNOCSHREX      NOV-05 IMU Business Office          2237           241.75C2005-11-30          ----------                                 D                                "
//        };
//
//        String[] convertedStringInput = new String[] {
//                "2004BL4631625CS0014110---EXEX07EXENEUNOCSHREX 00000NOV-05 IMU Business Office          2224           241.75D2005-11-30          ----------                                 D                                ",
//                "2004BL4631625CS0018000---EXAS07EXENEUNOCSHREX 00000NOV-05 IMU Business Office          2237           241.75C2005-11-30          ----------                                 D                                "
//        };
//
//        // Add inputs to expected output ...
//        Vector expectedOutput = new Vector();
//        for(int i = 0; i < stringInput.length; i++) {
//            new EntryHolder(OriginEntrySource.EXTERNAL, convertedStringInput[i]));
//        }
//
//        // ... add expected output ...
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL1031400-----9940---CEEX07EXENEUNOCSHREX 00000NOV-05 IMU Business Office  FR-BL4631625           241.75D2006-03-22          ----------                                 D                                "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL1031400-----9893---CEFB07EXENEUNOCSHREX 00000GENERATED OFFSET                                   241.75C2006-03-22          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631625CS0014110---EXEX07EXENEUNOCSHREX 00000NOV-05 IMU Business Office          2224           241.75D2005-11-30          ----------                                 D                                "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631625CS0018000---EXAS07EXENEUNOCSHREX 00000NOV-05 IMU Business Office          2237           241.75C2005-11-30          ----------                                 D                                "));
//
//        // ... and run the test.
//        scrub(stringInput);
//        assertOriginEntries(4,(EntryHolder[]) expectedOutput.toArray(new EntryHolder[0]), dateTimeService.currentDate);
//
//    }
//
//    public void testNoCostShareTransfersForBudgetTransactions() throws Exception {
//
//        // Inputs.
//        String[] stringInput = new String[] {
//                "2004BL4631618CS0015000---BBEX07DI  01NOCSHRBB      Rite Quality Office Supplies Inc.                   94.35 2006-01-05          ----------                                                                  ",
//                "2004BL4631618CS0019041---BBLI07DI  01NOCSHRBB      Rite Quality Office Supplies Inc.                   94.35 2006-01-05          ----------                                                                  "
//        };
//
//        String[] convertedStringInput = new String[] {
//                "2004BL4631618CS0015000---BBEX07DI  01NOCSHRBB 00000Rite Quality Office Supplies Inc.                   94.35 2006-01-05          ----------                                                                  ",
//                "2004BL4631618CS0019041---BBLI07DI  01NOCSHRBB 00000Rite Quality Office Supplies Inc.                   94.35 2006-01-05          ----------                                                                  "
//        };
//
//        // Add inputs to expected output ...
//        Vector expectedOutput = new Vector();
//        for(int i = 0; i < stringInput.length; i++) {
//            new EntryHolder(OriginEntrySource.EXTERNAL, convertedStringInput[i]));
//        }
//
//        // ... add expected output ...
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631618CS0015000---BBEX07DI  01NOCSHRBB 00000Rite Quality Office Supplies Inc.                   94.35 2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631618CS0019041---BBLI07DI  01NOCSHRBB 00000Rite Quality Office Supplies Inc.                   94.35 2006-01-05          ----------                                                                  "));
//
//        // ... and run the test.
//        scrub(stringInput);
//        assertOriginEntries(4,(EntryHolder[]) expectedOutput.toArray(new EntryHolder[0]), dateTimeService.currentDate);
//
//    }
//
//    public void testNoCostShareTransfersForNonExpense() throws Exception {
//
//        // Inputs.
//        String[] stringInput = new String[] {
//                "2004BL4631601CS0011800---ACIN07CR  01NOCSHRIN      Poplars Garage Fees                                 20.00C2006-01-05          ----------                                                                  ",
//                "2004BL4631601CS0018000---ACAS07CR  01NOCSHRIN      TP Generated Offset                                 20.00D2006-01-05          ----------                                                                  "
//        };
//
//        String[] convertedStringInput = new String[] {
//                "2004BL4631601CS0011800---ACIN07CR  01NOCSHRIN 00000Poplars Garage Fees                                 20.00C2006-01-05          ----------                                                                  ",
//                "2004BL4631601CS0018000---ACAS07CR  01NOCSHRIN 00000TP Generated Offset                                 20.00D2006-01-05          ----------                                                                  "
//        };
//
//        // Add inputs to expected output ...
//        Vector expectedOutput = new Vector();
//        for(int i = 0; i < stringInput.length; i++) {
//            new EntryHolder(OriginEntrySource.EXTERNAL, convertedStringInput[i]));
//        }
//
//        // ... add expected output ...
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631601CS0011800---ACIN07CR  01NOCSHRIN 00000Poplars Garage Fees                                 20.00C2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631601CS0018000---ACAS07CR  01NOCSHRIN 00000TP Generated Offset                                 20.00D2006-01-05          ----------                                                                  "));
//
//        // ... and run the test.
//        scrub(stringInput);
//        assertOriginEntries(4,(EntryHolder[]) expectedOutput.toArray(new EntryHolder[0]), dateTimeService.currentDate);
//
//    }
//
//    public void testPlantIndebtedness() throws Exception {
//
//        // Inputs.
//        String[] stringInput = new String[] {
//                "2004BA9020204-----9100---ACLI07SB  01DEBTEDNES     Biology Stockroom                                   13.77D2006-01-05          ----------                                                                  ",
//                "2004BA9020204-----8000---ACAS07SB  01DEBTEDNES     TP Generated Offset                                 13.77C2006-01-05          ----------                                                                  ",
//                "2004BA9120657-----9120---ACLI07ST  EUDEBTEDNES     PAYROLL EXPENSE TRANSFERS                          620.00C2006-01-05          ----------                                                                  ",
//                "2004BA9120657-----8000---ACAS07ST  EUDEBTEDNES     PAYROLL EXPENSE TRANSFERS                          620.00D2006-01-05          ----------                                                                  "
//        };
//
//        String[] convertedStringInput = new String[] {
//                "2004BA9020204-----9100---ACLI07SB  01DEBTEDNES00000Biology Stockroom                                   13.77D2006-01-05          ----------                                                                  ",
//                "2004BA9020204-----8000---ACAS07SB  01DEBTEDNES00000TP Generated Offset                                 13.77C2006-01-05          ----------                                                                  ",
//                "2004BA9120657-----9120---ACLI07ST  EUDEBTEDNES00000PAYROLL EXPENSE TRANSFERS                          620.00C2006-01-05          ----------                                                                  ",
//                "2004BA9120657-----8000---ACAS07ST  EUDEBTEDNES00000PAYROLL EXPENSE TRANSFERS                          620.00D2006-01-05          ----------                                                                  "
//        };
//
//        // Add inputs to expected output ...
//        Vector expectedOutput = new Vector();
//        for(int i = 0; i < stringInput.length; i++) {
//            new EntryHolder(OriginEntrySource.EXTERNAL, convertedStringInput[i]));
//        }
//
//        // ... add expected output ...
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA9020204-----9100---ACLI07SB  01DEBTEDNES00000GENERATED TRANSFER TO NET PLANT                     13.77C2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA9020204-----9899---ACFB07SB  01DEBTEDNES00000GENERATED TRANSFER TO NET PLANT                     13.77D2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA9544900-----9100---ACLI07SB  01DEBTEDNES00000GENERATED TRANSFER FROM BA 9020204                  13.77D2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA9544900-----9899---ACFB07SB  01DEBTEDNES00000GENERATED TRANSFER FROM BA 9020204                  13.77C2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA9020204-----9100---ACLI07SB  01DEBTEDNES00000Biology Stockroom                                   13.77D2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA9020204-----8000---ACAS07SB  01DEBTEDNES00000TP Generated Offset                                 13.77C2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA9120657-----9120---ACLI07ST  EUDEBTEDNES00000GENERATED TRANSFER TO NET PLANT                    620.00D2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA9120657-----9899---ACFB07ST  EUDEBTEDNES00000GENERATED TRANSFER TO NET PLANT                    620.00C2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA9544900-----9120---ACLI07ST  EUDEBTEDNES00000GENERATED TRANSFER FROM BA 9120657                 620.00C2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA9544900-----9899---ACFB07ST  EUDEBTEDNES00000GENERATED TRANSFER FROM BA 9120657                 620.00D2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA9120657-----9120---ACLI07ST  EUDEBTEDNES00000PAYROLL EXPENSE TRANSFERS                          620.00C2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA9120657-----8000---ACAS07ST  EUDEBTEDNES00000PAYROLL EXPENSE TRANSFERS                          620.00D2006-01-05          ----------                                                                  "));
//
//        // ... and run the test.
//        scrub(stringInput);
//        assertOriginEntries(4,(EntryHolder[]) expectedOutput.toArray(new EntryHolder[0]), dateTimeService.currentDate);
//
//    }
//
//    public void testNoIndebtednessForObjectSubTypeP2() throws Exception {
//
//        // Inputs.
//        String[] stringInput = new String[] {
//                "2004BA9120657-----9100---ACLI07INV EUNODEBTP2      BALDWIN WALLACE COLLEGE                           3375.00C2006-01-05          ----------                                                                  ",
//                "2004BA9120657-----8000---ACAS07INV EUNODEBTP2      TP Generated Offset                               3375.00D2006-01-05          ----------                                                                  "
//        };
//
//        String[] convertedStringInput = new String[] {
//                "2004BA9120657-----9100---ACLI07INV EUNODEBTP2 00000BALDWIN WALLACE COLLEGE                           3375.00C2006-01-05          ----------                                                                  ",
//                "2004BA9120657-----8000---ACAS07INV EUNODEBTP2 00000TP Generated Offset                               3375.00D2006-01-05          ----------                                                                  "
//        };
//
//        // Add inputs to expected output ...
//        Vector expectedOutput = new Vector();
//        for(int i = 0; i < stringInput.length; i++) {
//            new EntryHolder(OriginEntrySource.EXTERNAL, convertedStringInput[i]));
//        }
//
//        // ... add expected output ...
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA9120657-----9100---ACLI07INV EUNODEBTP2 00000GENERATED TRANSFER TO NET PLANT                   3375.00D2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA9120657-----9899---ACFB07INV EUNODEBTP2 00000GENERATED TRANSFER TO NET PLANT                   3375.00C2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA9544900-----9100---ACLI07INV EUNODEBTP2 00000GENERATED TRANSFER FROM BA 9120657                3375.00C2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA9544900-----9899---ACFB07INV EUNODEBTP2 00000GENERATED TRANSFER FROM BA 9120657                3375.00D2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA9120657-----9100---ACLI07INV EUNODEBTP2 00000BALDWIN WALLACE COLLEGE                           3375.00C2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA9120657-----8000---ACAS07INV EUNODEBTP2 00000TP Generated Offset                               3375.00D2006-01-05          ----------                                                                  "));
//
//        // ... and run the test.
//        scrub(stringInput);
//        assertOriginEntries(4,(EntryHolder[]) expectedOutput.toArray(new EntryHolder[0]), dateTimeService.currentDate);
//
//    }
//
//    public void testNoIndebtednessForObjectSubTypeP1() throws Exception {
//
//        // Inputs.
//        String[] stringInput = new String[] {
//                "2004BL2231423-----9100---ACIN  CR  PLNODEBTP1      FRICKA FRACKA                                    45995.84C2006-01-05          ----------                                                                  ",
//                "2004BL2231423-----8000---ACAS  CR  PLNODEBTP1      TP Generated Offset                              45995.84D2006-01-05          ----------                                                                  "
//        };
//
//        String[] convertedStringInput = new String[] {
//                "2004BL2231423-----9100---ACIN  CR  PLNODEBTP1 00000FRICKA FRACKA                                    45995.84C2006-01-05          ----------                                                                  ",
//                "2004BL2231423-----8000---ACAS  CR  PLNODEBTP1 00000TP Generated Offset                              45995.84D2006-01-05          ----------                                                                  "
//        };
//
//        // Add inputs to expected output ...
//        Vector expectedOutput = new Vector();
//        for(int i = 0; i < stringInput.length; i++) {
//            new EntryHolder(OriginEntrySource.EXTERNAL, convertedStringInput[i]));
//        }
//
//        // ... add expected output ...
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL2231423-----9100---ACIN07CR  PLNODEBTP1 00000FRICKA FRACKA                                    45995.84C2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL2231423-----8000---ACAS07CR  PLNODEBTP1 00000TP Generated Offset                              45995.84D2006-01-05          ----------                                                                  "));
//
//        // ... and run the test.
//        scrub(stringInput);
//        assertOriginEntries(4,(EntryHolder[]) expectedOutput.toArray(new EntryHolder[0]), dateTimeService.currentDate);
//
//    }
//
//    public void testNoIndebtednessForEncumbranceEntries() throws Exception {
//
//        // Inputs.
//        String[] stringInput = new String[] {
//                "2004BA9021004-----9120---EXLI07TOPSEUNODEBTEX      PAYROLL EXPENSE TRANSFERS                          620.00C2006-01-05          ----------                                 D                                ",
//                "2004BA9021004-----8000---EXAS07TOPSEUNODEBTEX      PAYROLL EXPENSE TRANSFERS                          620.00D2006-01-05          ----------                                 D                                "
//        };
//
//        String[] convertedStringInput = new String[] {
//                "2004BA9021004-----9120---EXLI07TOPSEUNODEBTEX 00000PAYROLL EXPENSE TRANSFERS                          620.00C2006-01-05          ----------                                 D                                ",
//                "2004BA9021004-----8000---EXAS07TOPSEUNODEBTEX 00000PAYROLL EXPENSE TRANSFERS                          620.00D2006-01-05          ----------                                 D                                "
//        };
//
//        // Add inputs to expected output ...
//        Vector expectedOutput = new Vector();
//        for(int i = 0; i < stringInput.length; i++) {
//            new EntryHolder(OriginEntrySource.EXTERNAL, convertedStringInput[i]));
//        }
//
//        // ... add expected output ...
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA9021004-----9120---EXLI07TOPSEUNODEBTEX 00000PAYROLL EXPENSE TRANSFERS                          620.00C2006-01-05          ----------                                 D                                "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA9021004-----8000---EXAS07TOPSEUNODEBTEX 00000PAYROLL EXPENSE TRANSFERS                          620.00D2006-01-05          ----------                                 D                                "));
//
//        // ... and run the test.
//        scrub(stringInput);
//        assertOriginEntries(4,(EntryHolder[]) expectedOutput.toArray(new EntryHolder[0]), dateTimeService.currentDate);
//
//    }
//
//    public void testNoIndebtednessForBudgetTransactions() throws Exception {
//
//        // Inputs.
//        String[] stringInput = new String[] {
//                "2004BA9020204-----9100---BBLI07SB  01NODEBTBB      Biology Stockroom                                   13.77 2006-01-05          ----------                                                                  ",
//                "2004BA9020204-----8000---BBAS07SB  01NODEBTBB      TP Generated Offset                                 13.77 2006-01-05          ----------                                                                  "
//        };
//
//        String[] convertedStringInput = new String[] {
//                "2004BA9020204-----9100---BBLI07SB  01NODEBTBB 00000Biology Stockroom                                   13.77 2006-01-05          ----------                                                                  ",
//                "2004BA9020204-----8000---BBAS07SB  01NODEBTBB 00000TP Generated Offset                                 13.77 2006-01-05          ----------                                                                  "
//        };
//
//        // Add inputs to expected output ...
//        Vector expectedOutput = new Vector();
//        for(int i = 0; i < stringInput.length; i++) {
//            new EntryHolder(OriginEntrySource.EXTERNAL, convertedStringInput[i]));
//        }
//
//        // ... add expected output ...
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA9020204-----9100---BBLI07SB  01NODEBTBB 00000Biology Stockroom                                   13.77 2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA9020204-----8000---BBAS07SB  01NODEBTBB 00000TP Generated Offset                                 13.77 2006-01-05          ----------                                                                  "));
//
//        // ... and run the test.
//        scrub(stringInput);
//        assertOriginEntries(4,(EntryHolder[]) expectedOutput.toArray(new EntryHolder[0]), dateTimeService.currentDate);
//
//    }
//
//    public void testCapitalizationForObjectSubTypeCL() throws Exception {
//
//        // Inputs.
//        String[] stringInput = new String[] {
//                "2004BA6044900-----7099---ACEE07CD  PDCAPITALCL     214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05          ----------                                                                  ",
//                "2004BA6044900-----8000---ACAS07CD  PDCAPITALCL     214090047 EVERETT J PRESCOTT INC.                 1445.00C2006-01-05          ----------                                                                  "
//        };
//
//        String[] convertedStringInput = new String[] {
//                "2004BA6044900-----7099---ACEE07CD  PDCAPITALCL00000214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05          ----------                                                                  ",
//                "2004BA6044900-----8000---ACAS07CD  PDCAPITALCL00000214090047 EVERETT J PRESCOTT INC.                 1445.00C2006-01-05          ----------                                                                  "
//        };
//
//        // Add inputs to expected output ...
//        Vector expectedOutput = new Vector();
//        for(int i = 0; i < stringInput.length; i++) {
//            new EntryHolder(OriginEntrySource.EXTERNAL, convertedStringInput[i]));
//        }
//
//        // ... add expected output ...
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA9544900-----9603---ACLI07CD  PDCAPITALCL00000GENERATED LIABILITY                               1445.00D2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA9544900-----9899---ACFB07CD  PDCAPITALCL00000GENERATED LIABILITY                               1445.00C2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA6044900-----7099---ACEE07CD  PDCAPITALCL00000214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA6044900-----8000---ACAS07CD  PDCAPITALCL00000214090047 EVERETT J PRESCOTT INC.                 1445.00C2006-01-05          ----------                                                                  "));
//
//        // ... and run the test.
//        scrub(stringInput);
//        assertOriginEntries(4,(EntryHolder[]) expectedOutput.toArray(new EntryHolder[0]), dateTimeService.currentDate);
//
//    }
//
//    public void testCapitalizationForObjectSubTypeLR() throws Exception {
//
//        // Inputs.
//        String[] stringInput = new String[] {
//                "2004BA6044913-----7465---ACEE07GEC 01CAPITALLR     CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                  ",
//                "2004BA6044913-----9041---ACLI07GEC 01CAPITALLR     CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                  "
//        };
//
//        String[] convertedStringInput = new String[] {
//                "2004BA6044913-----7465---ACEE07GEC 01CAPITALLR00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                  ",
//                "2004BA6044913-----9041---ACLI07GEC 01CAPITALLR00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                  "
//        };
//
//        // Add inputs to expected output ...
//        Vector expectedOutput = new Vector();
//        for(int i = 0; i < stringInput.length; i++) {
//            new EntryHolder(OriginEntrySource.EXTERNAL, convertedStringInput[i]));
//        }
//
//        // ... add expected output ...
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA9544900-----8665---ACAS07GEC 01CAPITALLR00000GENERATED CAPITALIZATION                            48.53C2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA9544900-----9899---ACFB07GEC 01CAPITALLR00000GENERATED CAPITALIZATION                            48.53D2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA6044913-----7465---ACEE07GEC 01CAPITALLR00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA6044913-----9041---ACLI07GEC 01CAPITALLR00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                  "));
//
//        // ... and run the test.
//        scrub(stringInput);
//        assertOriginEntries(4,(EntryHolder[]) expectedOutput.toArray(new EntryHolder[0]), dateTimeService.currentDate);
//
//    }
//
//    public void testCapitalizationForObjectSubTypeLI() throws Exception {
//
//        // Inputs.
//        String[] stringInput = new String[] {
//                "2004BA6044906-----7100---ACEE07TOPS01CAPITALLI     CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                  ",
//                "2004BA6044906-----9041---ACLI07TOPS01CAPITALLI     CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                  "
//        };
//
//        String[] convertedStringInput = new String[] {
//                "2004BA6044906-----7100---ACEE07TOPS01CAPITALLI00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                  ",
//                "2004BA6044906-----9041---ACLI07TOPS01CAPITALLI00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                  "
//        };
//
//        // Add inputs to expected output ...
//        Vector expectedOutput = new Vector();
//        for(int i = 0; i < stringInput.length; i++) {
//            new EntryHolder(OriginEntrySource.EXTERNAL, convertedStringInput[i]));
//        }
//
//        // ... add expected output ...
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA9544900-----8613---ACAS07TOPS01CAPITALLI00000GENERATED CAPITALIZATION                            48.53C2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA9544900-----9899---ACFB07TOPS01CAPITALLI00000GENERATED CAPITALIZATION                            48.53D2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA6044906-----7100---ACEE07TOPS01CAPITALLI00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA6044906-----9041---ACLI07TOPS01CAPITALLI00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                  "));
//
//        // ... and run the test.
//        scrub(stringInput);
//        assertOriginEntries(4,(EntryHolder[]) expectedOutput.toArray(new EntryHolder[0]), dateTimeService.currentDate);
//
//    }
//
//    public void testCapitalizationForObjectSubTypeLE() throws Exception {
//
//        // Inputs.
//        String[] stringInput = new String[] {
//                "2004BA6044900-----7800---ACEE07CD  PDCAPITALLE     214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05          ----------                                                                  ",
//                "2004BA6044900-----8000---ACAS07CD  PDCAPITALLE     214090047 EVERETT J PRESCOTT INC.                 1445.00C2006-01-05          ----------                                                                  "
//        };
//
//        String[] convertedStringInput = new String[] {
//                "2004BA6044900-----7800---ACEE07CD  PDCAPITALLE00000214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05          ----------                                                                  ",
//                "2004BA6044900-----8000---ACAS07CD  PDCAPITALLE00000214090047 EVERETT J PRESCOTT INC.                 1445.00C2006-01-05          ----------                                                                  "
//        };
//
//        // Add inputs to expected output ...
//        Vector expectedOutput = new Vector();
//        for(int i = 0; i < stringInput.length; i++) {
//            new EntryHolder(OriginEntrySource.EXTERNAL, convertedStringInput[i]));
//        }
//
//        // ... add expected output ...
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA9544900-----8608---ACAS07CD  PDCAPITALLE00000GENERATED CAPITALIZATION                          1445.00D2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA9544900-----9899---ACFB07CD  PDCAPITALLE00000GENERATED CAPITALIZATION                          1445.00C2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA6044900-----7800---ACEE07CD  PDCAPITALLE00000214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA6044900-----8000---ACAS07CD  PDCAPITALLE00000214090047 EVERETT J PRESCOTT INC.                 1445.00C2006-01-05          ----------                                                                  "));
//
//        // ... and run the test.
//        scrub(stringInput);
//        assertOriginEntries(4,(EntryHolder[]) expectedOutput.toArray(new EntryHolder[0]), dateTimeService.currentDate);
//
//    }
//
//    public void testCapitalizationForObjectSubTypeLA() throws Exception {
//
//        // Inputs.
//        String[] stringInput = new String[] {
//                "2004BA6044913-----7200---ACEE07GEC 01CAPITALLA     CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                  ",
//                "2004BA6044913-----9041---ACLI07GEC 01CAPITALLA     CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                  "
//        };
//
//        String[] convertedStringInput = new String[] {
//                "2004BA6044913-----7200---ACEE07GEC 01CAPITALLA00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                  ",
//                "2004BA6044913-----9041---ACLI07GEC 01CAPITALLA00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                  "
//        };
//
//        // Add inputs to expected output ...
//        Vector expectedOutput = new Vector();
//        for(int i = 0; i < stringInput.length; i++) {
//            new EntryHolder(OriginEntrySource.EXTERNAL, convertedStringInput[i]));
//        }
//
//        // ... add expected output ...
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA9544900-----8603---ACAS07GEC 01CAPITALLA00000GENERATED CAPITALIZATION                            48.53C2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA9544900-----9899---ACFB07GEC 01CAPITALLA00000GENERATED CAPITALIZATION                            48.53D2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA6044913-----7200---ACEE07GEC 01CAPITALLA00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA6044913-----9041---ACLI07GEC 01CAPITALLA00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                  "));
//
//        // ... and run the test.
//        scrub(stringInput);
//        assertOriginEntries(4,(EntryHolder[]) expectedOutput.toArray(new EntryHolder[0]), dateTimeService.currentDate);
//
//    }
//
//    public void testCapitalizationForObjectSubTypeEF() throws Exception {
//
//        // Inputs.
//        String[] stringInput = new String[] {
//                "2004BA6044906-----7400---ACEE07TOPS01CAPITALIF     CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                  ",
//                "2004BA6044906-----9041---ACLI07TOPS01CAPITALIF     CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                  "
//        };
//
//        String[] convertedStringInput = new String[] {
//                "2004BA6044906-----7400---ACEE07TOPS01CAPITALIF00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                  ",
//                "2004BA6044906-----9041---ACLI07TOPS01CAPITALIF00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                  "
//        };
//
//        // Add inputs to expected output ...
//        Vector expectedOutput = new Vector();
//        for(int i = 0; i < stringInput.length; i++) {
//            new EntryHolder(OriginEntrySource.EXTERNAL, convertedStringInput[i]));
//        }
//
//        // ... add expected output ...
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA9544900-----8604---ACAS07TOPS01CAPITALIF00000GENERATED CAPITALIZATION                            48.53C2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA9544900-----9899---ACFB07TOPS01CAPITALIF00000GENERATED CAPITALIZATION                            48.53D2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA6044906-----7400---ACEE07TOPS01CAPITALIF00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA6044906-----9041---ACLI07TOPS01CAPITALIF00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                  "));
//
//        // ... and run the test.
//        scrub(stringInput);
//        assertOriginEntries(4,(EntryHolder[]) expectedOutput.toArray(new EntryHolder[0]), dateTimeService.currentDate);
//
//    }
//
//    public void testCapitalizationForObjectSubTypeES() throws Exception {
//
//        // Inputs.
//        String[] stringInput = new String[] {
//                "2004BA6044900-----7098---ACEE07CD  PDCAPITALES     214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05          ----------                                                                  ",
//                "2004BA6044900-----8000---ACAS07CD  PDCAPITALES     214090047 EVERETT J PRESCOTT INC.                 1445.00C2006-01-05          ----------                                                                  "
//        };
//
//        String[] convertedStringInput = new String[] {
//                "2004BA6044900-----7098---ACEE07CD  PDCAPITALES00000214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05          ----------                                                                  ",
//                "2004BA6044900-----8000---ACAS07CD  PDCAPITALES00000214090047 EVERETT J PRESCOTT INC.                 1445.00C2006-01-05          ----------                                                                  "
//        };
//
//        // Add inputs to expected output ...
//        Vector expectedOutput = new Vector();
//        for(int i = 0; i < stringInput.length; i++) {
//            new EntryHolder(OriginEntrySource.EXTERNAL, convertedStringInput[i]));
//        }
//
//        // ... add expected output ...
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA9544900-----8630---ACAS07CD  PDCAPITALES00000GENERATED CAPITALIZATION                          1445.00D2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA9544900-----9899---ACFB07CD  PDCAPITALES00000GENERATED CAPITALIZATION                          1445.00C2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA6044900-----7098---ACEE07CD  PDCAPITALES00000214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA6044900-----8000---ACAS07CD  PDCAPITALES00000214090047 EVERETT J PRESCOTT INC.                 1445.00C2006-01-05          ----------                                                                  "));
//
//        // ... and run the test.
//        scrub(stringInput);
//        assertOriginEntries(4,(EntryHolder[]) expectedOutput.toArray(new EntryHolder[0]), dateTimeService.currentDate);
//
//    }
//
//    public void testCapitalizationForObjectSubTypeCF() throws Exception {
//
//        // Inputs.
//        String[] stringInput = new String[] {
//                "2004BA6044913-----7030---ACEE07GEC 01CAPITALCF     CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                  ",
//                "2004BA6044913-----9041---ACLI07GEC 01CAPITALCF     CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                  "
//        };
//
//        String[] convertedStringInput = new String[] {
//                "2004BA6044913-----7030---ACEE07GEC 01CAPITALCF00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                  ",
//                "2004BA6044913-----9041---ACLI07GEC 01CAPITALCF00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                  "
//        };
//
//        // Add inputs to expected output ...
//        Vector expectedOutput = new Vector();
//        for(int i = 0; i < stringInput.length; i++) {
//            new EntryHolder(OriginEntrySource.EXTERNAL, convertedStringInput[i]));
//        }
//
//        // ... add expected output ...
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA9544900-----8611---ACAS07GEC 01CAPITALCF00000GENERATED CAPITALIZATION                            48.53C2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA9544900-----9899---ACFB07GEC 01CAPITALCF00000GENERATED CAPITALIZATION                            48.53D2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA6044913-----7030---ACEE07GEC 01CAPITALCF00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA6044913-----9041---ACLI07GEC 01CAPITALCF00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                  "));
//
//        // ... and run the test.
//        scrub(stringInput);
//        assertOriginEntries(4,(EntryHolder[]) expectedOutput.toArray(new EntryHolder[0]), dateTimeService.currentDate);
//
//    }
//
//    public void testCapitalizationForObjectSubTypeCM() throws Exception {
//
//        // Inputs.
//        String[] stringInput = new String[] {
//                "2004BA6044906-----7000---ACEE07TOPS01CAPITALCM     CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                  ",
//                "2004BA6044906-----9041---ACLI07TOPS01CAPITALCM     CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                  "
//        };
//
//        String[] convertedStringInput = new String[] {
//                "2004BA6044906-----7000---ACEE07TOPS01CAPITALCM00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                  ",
//                "2004BA6044906-----9041---ACLI07TOPS01CAPITALCM00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                  "
//        };
//
//        // Add inputs to expected output ...
//        Vector expectedOutput = new Vector();
//        for(int i = 0; i < stringInput.length; i++) {
//            new EntryHolder(OriginEntrySource.EXTERNAL, convertedStringInput[i]));
//        }
//
//        // ... add expected output ...
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA9544900-----8610---ACAS07TOPS01CAPITALCM00000GENERATED CAPITALIZATION                            48.53C2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA9544900-----9899---ACFB07TOPS01CAPITALCM00000GENERATED CAPITALIZATION                            48.53D2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA6044906-----7000---ACEE07TOPS01CAPITALCM00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA6044906-----9041---ACLI07TOPS01CAPITALCM00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                  "));
//
//        // ... and run the test.
//        scrub(stringInput);
//        assertOriginEntries(4,(EntryHolder[]) expectedOutput.toArray(new EntryHolder[0]), dateTimeService.currentDate);
//
//    }
//
//    public void testCapitalizationForObjectSubTypeBF() throws Exception {
//
//        // Inputs.
//        String[] stringInput = new String[] {
//                "2004BA6044913-----7305---ACEE07GEC 01CAPITALBF     CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                  ",
//                "2004BA6044913-----9041---ACLI07GEC 01CAPITALBF     CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                  "
//        };
//
//        String[] convertedStringInput = new String[] {
//                "2004BA6044913-----7305---ACEE07GEC 01CAPITALBF00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                  ",
//                "2004BA6044913-----9041---ACLI07GEC 01CAPITALBF00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                  "
//        };
//
//        // Add inputs to expected output ...
//        Vector expectedOutput = new Vector();
//        for(int i = 0; i < stringInput.length; i++) {
//            new EntryHolder(OriginEntrySource.EXTERNAL, convertedStringInput[i]));
//        }
//
//        // ... add expected output ...
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA9544900-----8605---ACAS07GEC 01CAPITALBF00000GENERATED CAPITALIZATION                            48.53C2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA9544900-----9899---ACFB07GEC 01CAPITALBF00000GENERATED CAPITALIZATION                            48.53D2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA6044913-----7305---ACEE07GEC 01CAPITALBF00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA6044913-----9041---ACLI07GEC 01CAPITALBF00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                  "));
//
//        // ... and run the test.
//        scrub(stringInput);
//        assertOriginEntries(4,(EntryHolder[]) expectedOutput.toArray(new EntryHolder[0]), dateTimeService.currentDate);
//
//    }
//
//    public void testCapitalizationForObjectSubTypeBD() throws Exception {
//
//        // Inputs.
//        String[] stringInput = new String[] {
//                "2004BA6044906-----7300---ACEE07TOPS01CAPITALBD     CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                  ",
//                "2004BA6044906-----9041---ACLI07TOPS01CAPITALBD     CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                  "
//        };
//
//        String[] convertedStringInput = new String[] {
//                "2004BA6044906-----7300---ACEE07TOPS01CAPITALBD00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                  ",
//                "2004BA6044906-----9041---ACLI07TOPS01CAPITALBD00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                  "
//        };
//
//        // Add inputs to expected output ...
//        Vector expectedOutput = new Vector();
//        for(int i = 0; i < stringInput.length; i++) {
//            new EntryHolder(OriginEntrySource.EXTERNAL, convertedStringInput[i]));
//        }
//
//        // ... add expected output ...
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA9544900-----8601---ACAS07TOPS01CAPITALBD00000GENERATED CAPITALIZATION                            48.53C2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA9544900-----9899---ACFB07TOPS01CAPITALBD00000GENERATED CAPITALIZATION                            48.53D2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA6044906-----7300---ACEE07TOPS01CAPITALBD00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA6044906-----9041---ACLI07TOPS01CAPITALBD00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                  "));
//
//        // ... and run the test.
//        scrub(stringInput);
//        assertOriginEntries(4,(EntryHolder[]) expectedOutput.toArray(new EntryHolder[0]), dateTimeService.currentDate);
//
//    }
//
//    public void testCapitalizationForObjectSubTypeAM() throws Exception {
//
//        // Inputs.
//        String[] stringInput = new String[] {
//                "2004BA6044900-----7677---ACEE07CD  PDCAPITALAM     214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05          ----------                                                                  ",
//                "2004BA6044900-----8000---ACAS07CD  PDCAPITALAM     214090047 EVERETT J PRESCOTT INC.                 1445.00C2006-01-05          ----------                                                                  "
//        };
//
//        String[] convertedStringInput = new String[] {
//                "2004BA6044900-----7677---ACEE07CD  PDCAPITALAM00000214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05          ----------                                                                  ",
//                "2004BA6044900-----8000---ACAS07CD  PDCAPITALAM00000214090047 EVERETT J PRESCOTT INC.                 1445.00C2006-01-05          ----------                                                                  "
//        };
//
//        // Add inputs to expected output ...
//        Vector expectedOutput = new Vector();
//        for(int i = 0; i < stringInput.length; i++) {
//            new EntryHolder(OriginEntrySource.EXTERNAL, convertedStringInput[i]));
//        }
//
//        // ... add expected output ...
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA9544900-----8615---ACAS07CD  PDCAPITALAM00000GENERATED CAPITALIZATION                          1445.00D2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA9544900-----9899---ACFB07CD  PDCAPITALAM00000GENERATED CAPITALIZATION                          1445.00C2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA6044900-----7677---ACEE07CD  PDCAPITALAM00000214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA6044900-----8000---ACAS07CD  PDCAPITALAM00000214090047 EVERETT J PRESCOTT INC.                 1445.00C2006-01-05          ----------                                                                  "));
//
//        // ... and run the test.
//        scrub(stringInput);
//        assertOriginEntries(4,(EntryHolder[]) expectedOutput.toArray(new EntryHolder[0]), dateTimeService.currentDate);
//
//    }
//
//    public void testNoCapitalizationForCertainFiscalPeriods() throws Exception {
//
//        // Inputs.
//        String[] stringInput = new String[] {
//                "2004BA6044900-----7000---ACEECBCD  PDNOCAPCB       214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ACCDEFGHIJ----------12345678                                                          ",
//                "2004BA6044900-----8000---ACASCBCD  PDNOCAPCB       214090047 EVERETT J PRESCOTT INC.                 1445.00C2006-01-05ABCDEFGHIG----------12345679                                                          "
//        };
//
//        String[] convertedStringInput = new String[] {
//                "2004BA6044900-----7000---ACEECBCD  PDNOCAPCB  00000214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ACCDEFGHIJ----------12345678                                                          ",
//                "2004BA6044900-----8000---ACASCBCD  PDNOCAPCB  00000214090047 EVERETT J PRESCOTT INC.                 1445.00C2006-01-05ABCDEFGHIG----------12345679                                                          "
//        };
//
//        // Add inputs to expected output ...
//        Vector expectedOutput = new Vector();
//        for(int i = 0; i < stringInput.length; i++) {
//            new EntryHolder(OriginEntrySource.EXTERNAL, convertedStringInput[i]));
//        }
//
//        // ... add expected output ...
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA6044900-----7000---ACEECBCD  PDNOCAPCB  00000214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ACCDEFGHIJ----------12345678                                                          "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA6044900-----8000---ACASCBCD  PDNOCAPCB  00000214090047 EVERETT J PRESCOTT INC.                 1445.00C2006-01-05ABCDEFGHIG----------12345679                                                          "));
//
//        // ... and run the test.
//        scrub(stringInput);
//        assertOriginEntries(4,(EntryHolder[]) expectedOutput.toArray(new EntryHolder[0]), dateTimeService.currentDate);
//
//    }
//
//    public void testNoCapitalizationForCertainDocumentTypes() throws Exception {
//
//        // Inputs.
//        String[] stringInput = new String[] {
//                "2004BA6044913-----7300---ACEE07TF  LGNOCAPTF       CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                  ",
//                "2004BA6044913-----9041---ACLI07TF  LGNOCAPTF       CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                  "
//        };
//
//        String[] convertedStringInput = new String[] {
//                "2004BA6044913-----7300---ACEE07TF  LGNOCAPTF  00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                  ",
//                "2004BA6044913-----9041---ACLI07TF  LGNOCAPTF  00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                  "
//        };
//
//        // Add inputs to expected output ...
//        Vector expectedOutput = new Vector();
//        for(int i = 0; i < stringInput.length; i++) {
//            new EntryHolder(OriginEntrySource.EXTERNAL, convertedStringInput[i]));
//        }
//
//        // ... add expected output ...
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA6044913-----7300---ACEE07TF  LGNOCAPTF  00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA6044913-----9041---ACLI07TF  LGNOCAPTF  00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                  "));
//
//        // ... and run the test.
//        scrub(stringInput);
//        assertOriginEntries(4,(EntryHolder[]) expectedOutput.toArray(new EntryHolder[0]), dateTimeService.currentDate);
//
//    }
//
//    public void testNoCapitalizationForEncumbranceEntry() throws Exception {
//
//        // Inputs.
//        String[] stringInput = new String[] {
//                "2004BA6044906-----7300---EXEE07TOPSLGNOCAPEX       CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                 D                                ",
//                "2004BA6044906-----9041---EXLI07TOPSLGNOCAPEX       CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                 D                                "
//        };
//
//        String[] convertedStringInput = new String[] {
//                "2004BA6044906-----7300---EXEE07TOPSLGNOCAPEX  00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                 D                                ",
//                "2004BA6044906-----9041---EXLI07TOPSLGNOCAPEX  00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                 D                                "
//        };
//
//        // Add inputs to expected output ...
//        Vector expectedOutput = new Vector();
//        for(int i = 0; i < stringInput.length; i++) {
//            new EntryHolder(OriginEntrySource.EXTERNAL, convertedStringInput[i]));
//        }
//
//        // ... add expected output ...
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA6044906-----7300---EXEE07TOPSLGNOCAPEX  00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                 D                                "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA6044906-----9041---EXLI07TOPSLGNOCAPEX  00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                 D                                "));
//
//        // ... and run the test.
//        scrub(stringInput);
//        assertOriginEntries(4,(EntryHolder[]) expectedOutput.toArray(new EntryHolder[0]), dateTimeService.currentDate);
//
//    }
//
//    public void testNoCapitalizationForBudgetTransaction() throws Exception {
//
//        // Inputs.
//        String[] stringInput = new String[] {
//                "2004BA6044900-----7000---BBEE07CD  PDNOCAPBB       214090047 EVERETT J PRESCOTT INC.                 1445.00 2006-01-05ACCDEFGHIJ----------12345678                                                          ",
//                "2004BA6044900-----8000---BBAS07CD  PDNOCAPBB       214090047 EVERETT J PRESCOTT INC.                 1445.00 2006-01-05ABCDEFGHIG----------12345679                                                          "
//        };
//
//        String[] convertedStringInput = new String[] {
//                "2004BA6044900-----7000---BBEE07CD  PDNOCAPBB  00000214090047 EVERETT J PRESCOTT INC.                 1445.00 2006-01-05ACCDEFGHIJ----------12345678                                                          ",
//                "2004BA6044900-----8000---BBAS07CD  PDNOCAPBB  00000214090047 EVERETT J PRESCOTT INC.                 1445.00 2006-01-05ABCDEFGHIG----------12345679                                                          "
//        };
//
//        // Add inputs to expected output ...
//        Vector expectedOutput = new Vector();
//        for(int i = 0; i < stringInput.length; i++) {
//            new EntryHolder(OriginEntrySource.EXTERNAL, convertedStringInput[i]));
//        }
//
//        // ... add expected output ...
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA6044900-----7000---BBEE07CD  PDNOCAPBB  00000214090047 EVERETT J PRESCOTT INC.                 1445.00 2006-01-05ACCDEFGHIJ----------12345678                                                          "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA6044900-----8000---BBAS07CD  PDNOCAPBB  00000214090047 EVERETT J PRESCOTT INC.                 1445.00 2006-01-05ABCDEFGHIG----------12345679                                                          "));
//
//        // ... and run the test.
//        scrub(stringInput);
//        assertOriginEntries(4,(EntryHolder[]) expectedOutput.toArray(new EntryHolder[0]), dateTimeService.currentDate);
//    }
//
//    public void testOffsetGenerationAcrossMultipleFiscalPeriods() throws Exception {
//
//        // Inputs.
//        String[] stringInput = new String[] {
//                "2004BL1031497-----4190---ACEX07GEC 01OFFSETPER     THOMAS BUSEY/NEWEGG COMPUTERS                       40.72C2006-01-05          ----------                                                                  ",                                    
//                "2004BL1031497-----8000---ACAS08GEC 01OFFSETPER     TP Generated Offset                                 40.72D2006-01-05          ----------                                                                  "
//        };
//
//        String[] convertedStringInput = new String[] {
//                "2004BL1031497-----4190---ACEX07GEC 01OFFSETPER00000THOMAS BUSEY/NEWEGG COMPUTERS                       40.72C2006-01-05          ----------                                                                  ",                                    
//                "2004BL1031497-----8000---ACAS08GEC 01OFFSETPER00000TP Generated Offset                                 40.72D2006-01-05          ----------                                                                  "
//        };
//
//        // Add inputs to expected output ...
//        Vector expectedOutput = new Vector();
//        for(int i = 0; i < stringInput.length; i++) {
//            new EntryHolder(OriginEntrySource.EXTERNAL, convertedStringInput[i]));
//        }
//
//        // ... add expected output ...
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL1031497-----4190---ACEX07GEC 01OFFSETPER00000THOMAS BUSEY/NEWEGG COMPUTERS                       40.72C2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL1031497-----8000---ACAS07GEC 01OFFSETPER00000GENERATED OFFSET                                    40.72D2006-02-21          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL1031497-----8000---ACAS08GEC 01OFFSETPER00000TP Generated Offset                                 40.72D2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL1031497-----8000---ACAS08GEC 01OFFSETPER00000GENERATED OFFSET                                    40.72C2006-02-21          ----------                                                                  "));
//
//        // ... and run the test.
//        scrub(stringInput);
//        assertOriginEntries(4,(EntryHolder[]) expectedOutput.toArray(new EntryHolder[0]), dateTimeService.currentDate);
//
//    }
//
//    public void testOffsetGenerationAcrossMultipleReversalDates() throws Exception {
//
//        // Inputs.
//        String[] stringInput = new String[] {
//                "2004BA6044913-----1800---ACIN07CR  01OFFSETREV     Poplars Garage Fees                                 20.00D2006-01-05          ----------                       2005-01-31                                 ",
//                "2004BA6044913-----8000---ACAS07CR  01OFFSETREV     TP Generated Offset                                 20.00C2006-01-05          ----------                       2005-02-01                                 "
//        };
//
//        String[] convertedStringInput = new String[] {
//                "2004BA6044913-----1800---ACIN07CR  01OFFSETREV00000Poplars Garage Fees                                 20.00D2006-01-05          ----------                       2005-01-31                                 ",
//                "2004BA6044913-----8000---ACAS07CR  01OFFSETREV00000TP Generated Offset                                 20.00C2006-01-05          ----------                       2005-02-01                                 "
//        };
//
//        // Add inputs to expected output ...
//        Vector expectedOutput = new Vector();
//        for(int i = 0; i < stringInput.length; i++) {
//            new EntryHolder(OriginEntrySource.EXTERNAL, convertedStringInput[i]));
//        }
//
//        // ... add expected output ...
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA6044913-----1800---ACIN07CR  01OFFSETREV00000Poplars Garage Fees                                 20.00D2006-01-05          ----------                       2005-01-31                                 "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA6044913-----8000---ACAS07CR  01OFFSETREV00000GENERATED OFFSET                                    20.00C2006-02-21          ----------                       2005-01-31                                 "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA6044913-----8000---ACAS07CR  01OFFSETREV00000TP Generated Offset                                 20.00C2006-01-05          ----------                       2005-02-01                                 "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA6044913-----8000---ACAS07CR  01OFFSETREV00000GENERATED OFFSET                                    20.00D2006-02-21          ----------                       2005-02-01                                 "));
//
//        // ... and run the test.
//        scrub(stringInput);
//        assertOriginEntries(4,(EntryHolder[]) expectedOutput.toArray(new EntryHolder[0]), dateTimeService.currentDate);
//
//    }
//
//    public void testOffsetGenerationAcrossMultipleBalanceTypes() throws Exception {
//
//        // Inputs.
//        String[] stringInput = new String[] {
//                "2004BA9120656-----4035---EXEX07EXEN01OFFSETBAL     pymts recd 12/28/05                                 25.15C2006-01-05          ----------                                 D                                ",
//                "2004BA9120656-----8000---ACAS07TOPS01OFFSETBAL     TP Generated Offset                                 25.15D2006-01-05          ----------                                                                  "
//        };
//
//        String[] convertedStringInput = new String[] {
//                "2004BA9120656-----4035---EXEX07EXEN01OFFSETBAL00000pymts recd 12/28/05                                 25.15C2006-01-05          ----------                                 D                                ",
//                "2004BA9120656-----8000---ACAS07TOPS01OFFSETBAL00000TP Generated Offset                                 25.15D2006-01-05          ----------                                                                  "
//        };
//
//        // Add inputs to expected output ...
//        Vector expectedOutput = new Vector();
//        for(int i = 0; i < stringInput.length; i++) {
//            new EntryHolder(OriginEntrySource.EXTERNAL, convertedStringInput[i]));
//        }
//
//        // ... add expected output ...
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA9120656-----4035---EXEX07EXEN01OFFSETBAL00000pymts recd 12/28/05                                 25.15C2006-01-05          ----------                                 D                                "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA9120656-----9892---EXFB07EXEN01OFFSETBAL00000GENERATED OFFSET                                    25.15D2006-02-21          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA9120656-----8000---ACAS07TOPS01OFFSETBAL00000TP Generated Offset                                 25.15D2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA9120656-----8000---ACAS07TOPS01OFFSETBAL00000GENERATED OFFSET                                    25.15C2006-02-21          ----------                                                                  "));
//
//        // ... and run the test.
//        scrub(stringInput);
//        assertOriginEntries(4,(EntryHolder[]) expectedOutput.toArray(new EntryHolder[0]), dateTimeService.currentDate);
//
//    }
//
//    public void testOffsetGenerationAcrossMultipleSubAccountNumbers() throws Exception {
//
//        // Inputs.
//        String[] stringInput = new String[] {
//                "2004BL1031400ADV  5000---ACEX07TOPSLGOFFSETSAC     225050007 WILLIAMS DOTSON ASSOCIATES IN           1200.00D2006-01-05          ----------                                                                  ",
//                "2004BL1031400AHD  9041---ACLI07TOPSLGOFFSETSAC     225050007 WILLIAMS DOTSON ASSOCIATES IN           1200.00C2006-01-05          ----------                                                                  "
//        };
//
//        String[] convertedStringInput = new String[] {
//                "2004BL1031400ADV  5000---ACEX07TOPSLGOFFSETSAC00000225050007 WILLIAMS DOTSON ASSOCIATES IN           1200.00D2006-01-05          ----------                                                                  ",
//                "2004BL1031400AHD  9041---ACLI07TOPSLGOFFSETSAC00000225050007 WILLIAMS DOTSON ASSOCIATES IN           1200.00C2006-01-05          ----------                                                                  "
//        };
//
//        // Add inputs to expected output ...
//        Vector expectedOutput = new Vector();
//        for(int i = 0; i < stringInput.length; i++) {
//            new EntryHolder(OriginEntrySource.EXTERNAL, convertedStringInput[i]));
//        }
//
//        // ... add expected output ...
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL1031400ADV  5000---ACEX07TOPSLGOFFSETSAC00000225050007 WILLIAMS DOTSON ASSOCIATES IN           1200.00D2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL1031400ADV  8000---ACAS07TOPSLGOFFSETSAC00000GENERATED OFFSET                                  1200.00C2006-02-21          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL1031400AHD  9041---ACLI07TOPSLGOFFSETSAC00000225050007 WILLIAMS DOTSON ASSOCIATES IN           1200.00C2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL1031400AHD  8000---ACAS07TOPSLGOFFSETSAC00000GENERATED OFFSET                                  1200.00D2006-02-21          ----------                                                                  "));
//
//        // ... and run the test.
//        scrub(stringInput);
//        assertOriginEntries(4,(EntryHolder[]) expectedOutput.toArray(new EntryHolder[0]), dateTimeService.currentDate);
//
//    }
//
//    public void testOffsetGenerationAcrossMultipleAccountNumbers() throws Exception {
//
//        // Inputs.
//        String[] stringInput = new String[] {
//                "2004BL1031420-----4190---ACEX07GEC 01OFFSETACT     THOMAS BUSEY/NEWEGG COMPUTERS                       40.72C2006-01-05          ----------                                                                  ",
//                "2004BL1031497-----8000---ACAS07GEC 01OFFSETACT     TP Generated Offset                                 40.72D2006-01-05          ----------                                                                  "
//        };
//
//        String[] convertedStringInput = new String[] {
//                "2004BL1031420-----4190---ACEX07GEC 01OFFSETACT00000THOMAS BUSEY/NEWEGG COMPUTERS                       40.72C2006-01-05          ----------                                                                  ",
//                "2004BL1031497-----8000---ACAS07GEC 01OFFSETACT00000TP Generated Offset                                 40.72D2006-01-05          ----------                                                                  "
//        };
//
//        // Add inputs to expected output ...
//        Vector expectedOutput = new Vector();
//        for(int i = 0; i < stringInput.length; i++) {
//            new EntryHolder(OriginEntrySource.EXTERNAL, convertedStringInput[i]));
//        }
//
//        // ... add expected output ...
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL1031420-----4190---ACEX07GEC 01OFFSETACT00000THOMAS BUSEY/NEWEGG COMPUTERS                       40.72C2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL1031420-----8000---ACAS07GEC 01OFFSETACT00000GENERATED OFFSET                                    40.72D2006-02-21          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL1031497-----8000---ACAS07GEC 01OFFSETACT00000TP Generated Offset                                 40.72D2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL1031497-----8000---ACAS07GEC 01OFFSETACT00000GENERATED OFFSET                                    40.72C2006-02-21          ----------                                                                  "));
//
//        // ... and run the test.
//        scrub(stringInput);
//        assertOriginEntries(4,(EntryHolder[]) expectedOutput.toArray(new EntryHolder[0]), dateTimeService.currentDate);
//
//    }
//
//    public void testOffsetGenerationAcrossMultipleDocumentNumbers() throws Exception {
//
//        // Inputs.
//        String[] stringInput = new String[] {
//                "2004BA6044913-----1466---ACIC07AVAD01OFFSETDC1     online permit sales for 01/03/06                   240.00D2006-01-05          ----------                                                                  ",
//                "2004BA6044913-----5000---ACEX07AVAD01OFFSETDC1     online permit sales for 01/03/06                  3880.00C2006-01-05          ----------                                                                  ",
//                "2004BA6044913-----4100---ACEX07AVAD01OFFSETDC2     online permit sales for 01/03/06                   725.00C2006-01-05          ----------                                                                  ",
//                "2004BA6044913-----1800---ACIC07AVAD01OFFSETDC2     online permit sales for 01/03/06                  3395.00D2006-01-05          ----------                                                                  "
//        };
//
//        String[] convertedStringInput = new String[] {
//                "2004BA6044913-----1466---ACIC07AVAD01OFFSETDC100000online permit sales for 01/03/06                   240.00D2006-01-05          ----------                                                                  ",
//                "2004BA6044913-----5000---ACEX07AVAD01OFFSETDC100000online permit sales for 01/03/06                  3880.00C2006-01-05          ----------                                                                  ",
//                "2004BA6044913-----4100---ACEX07AVAD01OFFSETDC200000online permit sales for 01/03/06                   725.00C2006-01-05          ----------                                                                  ",
//                "2004BA6044913-----1800---ACIC07AVAD01OFFSETDC200000online permit sales for 01/03/06                  3395.00D2006-01-05          ----------                                                                  "
//        };
//
//        // Add inputs to expected output ...
//        Vector expectedOutput = new Vector();
//        for(int i = 0; i < stringInput.length; i++) {
//            new EntryHolder(OriginEntrySource.EXTERNAL, convertedStringInput[i]));
//        }
//
//        // ... add expected output ...
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA6044913-----1466---ACIC07AVAD01OFFSETDC100000online permit sales for 01/03/06                   240.00D2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA6044913-----5000---ACEX07AVAD01OFFSETDC100000online permit sales for 01/03/06                  3880.00C2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA6044913-----9897---ACFB07AVAD01OFFSETDC100000GENERATED OFFSET                                  3640.00D2006-02-21          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA6044913-----4100---ACEX07AVAD01OFFSETDC200000online permit sales for 01/03/06                   725.00C2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA6044913-----1800---ACIC07AVAD01OFFSETDC200000online permit sales for 01/03/06                  3395.00D2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA6044913-----9897---ACFB07AVAD01OFFSETDC200000GENERATED OFFSET                                  2670.00C2006-02-21          ----------                                                                  "));
//
//        // ... and run the test.
//        scrub(stringInput);
//        assertOriginEntries(4,(EntryHolder[]) expectedOutput.toArray(new EntryHolder[0]), dateTimeService.currentDate);
//
//    }
//
//    public void testOffsetGenerationAcrossMultipleOriginCodes() throws Exception {
//
//        // Inputs.
//        String[] stringInput = new String[] {
//                "2004BA6044906-----4010---ACEX07DI  01OFFSETORG     OFFICE SUPPLY CHARGEBACKS                          294.64D2006-01-05          ----------                                                                  ",
//                "2004BA6044906-----5000---ACEX07DI  EUOFFSETORG     OFFICE SUPPLY CHARGEBACKS                          294.64D2006-01-05          ----------                                                                  "
//        };
//
//        String[] convertedStringInput = new String[] {
//                "2004BA6044906-----4010---ACEX07DI  01OFFSETORG00000OFFICE SUPPLY CHARGEBACKS                          294.64D2006-01-05          ----------                                                                  ",
//                "2004BA6044906-----5000---ACEX07DI  EUOFFSETORG00000OFFICE SUPPLY CHARGEBACKS                          294.64D2006-01-05          ----------                                                                  "
//        };
//
//        // Add inputs to expected output ...
//        Vector expectedOutput = new Vector();
//        for(int i = 0; i < stringInput.length; i++) {
//            new EntryHolder(OriginEntrySource.EXTERNAL, convertedStringInput[i]));
//        }
//
//        // ... add expected output ...
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA6044906-----4010---ACEX07DI  01OFFSETORG00000OFFICE SUPPLY CHARGEBACKS                          294.64D2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA6044906-----8000---ACAS07DI  01OFFSETORG00000GENERATED OFFSET                                   294.64C2006-02-21          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA6044906-----5000---ACEX07DI  EUOFFSETORG00000OFFICE SUPPLY CHARGEBACKS                          294.64D2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BA6044906-----8000---ACAS07DI  EUOFFSETORG00000GENERATED OFFSET                                   294.64C2006-02-21          ----------                                                                  "));
//
//        // ... and run the test.
//        scrub(stringInput);
//        assertOriginEntries(4,(EntryHolder[]) expectedOutput.toArray(new EntryHolder[0]), dateTimeService.currentDate);
//
//    }
//
//    public void testOffsetGenerationAcrossMultipleDocumentTypes() throws Exception {
//
//        // Inputs.
//        String[] stringInput = new String[] {
//            "2004BL1031400-----4190---ACEX07PCDO01OFFSETDTP     SOM/MUSIC GENERAL/INSIGHT CABLE                     44.95C2006-01-05          ----------                                                                  ",
//            "2004BL1031400-----4190---ACEX07PCDO01OFFSETDTP     SOM/MUSIC GENERAL/SULLIVAN S FASHIONS FO           540.00C2006-01-05          ----------                                                                  ",
//            "2004BL1031400-----4021---ACEX07GEC 01OFFSETDTP     SOM/MUSIC GENERAL/INSIGHT CABLE                     44.95C2006-01-05          ----------                                                                  ",
//            "2004BL1031400-----1800---ACIN07GEC 01OFFSETDTP     SOM/MUSIC GENERAL/SULLIVAN S FASHIONS FO           547.00D2006-01-05          ----------                                                                  "
//        };
//
//        String[] convertedStringInput = new String[] {
//                "2004BL1031400-----4190---ACEX07PCDO01OFFSETDTP00000SOM/MUSIC GENERAL/INSIGHT CABLE                     44.95C2006-01-05          ----------                                                                  ",
//                "2004BL1031400-----4190---ACEX07PCDO01OFFSETDTP00000SOM/MUSIC GENERAL/SULLIVAN S FASHIONS FO           540.00C2006-01-05          ----------                                                                  ",
//                "2004BL1031400-----4021---ACEX07GEC 01OFFSETDTP00000SOM/MUSIC GENERAL/INSIGHT CABLE                     44.95C2006-01-05          ----------                                                                  ",
//                "2004BL1031400-----1800---ACIN07GEC 01OFFSETDTP00000SOM/MUSIC GENERAL/SULLIVAN S FASHIONS FO           547.00D2006-01-05          ----------                                                                  "
//            };
//
//        // Add inputs to expected output ...
//        Vector expectedOutput = new Vector();
//        for(int i = 0; i < stringInput.length; i++) {
//            new EntryHolder(OriginEntrySource.EXTERNAL, convertedStringInput[i]));
//        }
//
//        // ... add expected output ...
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//            "2004BL1031400-----4190---ACEX07PCDO01OFFSETDTP00000SOM/MUSIC GENERAL/INSIGHT CABLE                     44.95C2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//            "2004BL1031400-----4190---ACEX07PCDO01OFFSETDTP00000SOM/MUSIC GENERAL/SULLIVAN S FASHIONS FO           540.00C2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//            "2004BL1031400-----8000---ACAS07PCDO01OFFSETDTP00000GENERATED OFFSET                                   584.95D2006-02-21          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//            "2004BL1031400-----4021---ACEX07GEC 01OFFSETDTP00000SOM/MUSIC GENERAL/INSIGHT CABLE                     44.95C2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//            "2004BL1031400-----1800---ACIN07GEC 01OFFSETDTP00000SOM/MUSIC GENERAL/SULLIVAN S FASHIONS FO           547.00D2006-01-05          ----------                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//            "2004BL1031400-----8000---ACAS07GEC 01OFFSETDTP00000GENERATED OFFSET                                   502.05C2006-02-21          ----------                                                                  "));
//
//        // ... and run the test.
//        scrub(stringInput);
//        assertOriginEntries(4,(EntryHolder[]) expectedOutput.toArray(new EntryHolder[0]), dateTimeService.currentDate);
//
//    }
//
//    public void testClosedAccount01() throws Exception {
//
//        // Inputs.
//        String[] input = new String[] {
//                "2004BL2131401-----4100---ACEX07DI  01CLOSACT0100000Rite Quality Office Supplies Inc.                   94.35D2006-01-05          ----------                                                                  ",
//                "2004BL2131401-----9041---ACLI07DI  01CLOSACT0100000Rite Quality Office Supplies Inc.                   94.35C2006-01-05          ----------                                                                  "
//        };
//
//        EntryHolder[] output = new EntryHolder[] {
//                new EntryHolder(OriginEntrySource.EXTERNAL,"2004BL2131401-----4100---ACEX07DI  01CLOSACT0100000Rite Quality Office Supplies Inc.                   94.35D2006-01-05          ----------                                                                  "),
//                new EntryHolder(OriginEntrySource.EXTERNAL,"2004BL2131401-----9041---ACLI07DI  01CLOSACT0100000Rite Quality Office Supplies Inc.                   94.35C2006-01-05          ----------                                                                  "),
//                new EntryHolder(OriginEntrySource.SCRUBBER_EXPIRED,"2004BL2131401-----4100---ACEX07DI  01CLOSACT0100000Rite Quality Office Supplies Inc.                   94.35D2006-01-05          ----------                                                                  "),
//                new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,"2004BL2131401-----9041---ACLI07DI  01CLOSACT0100000Rite Quality Office Supplies Inc.                   94.35C2006-01-05          ----------                                                                  "),
//                new EntryHolder(OriginEntrySource.SCRUBBER_EXPIRED,"2004BL2131401-----4100---ACEX07DI  01CLOSACT0100000Rite Quality Office Supplies Inc.                   94.35D2006-01-05          ----------                                                                  "),
//                new EntryHolder(OriginEntrySource.SCRUBBER_ERROR,"2004BL2131401-----9041---ACLI07DI  01CLOSACT0100000Rite Quality Office Supplies Inc.                   94.35C2006-01-05          ----------                                                                  ")
//        };
//
//        scrub(input);
//        assertOriginEntries(4,output);
//    }
//
//    public void testClosedAccount() throws Exception {
//
//        // Inputs.
//        String[] stringInput = new String[] {
//                "2004BA6044909-----1800---ACIN07CR  UBCLOSACCT 00000Poplars Garage Fees                                 20.00C2006-01-05          ----------                                                                          ",
//                "2004BA6044909-----8000---ACAS07CR  UBCLOSACCT 00000TP Generated Offset                                 20.00D2006-01-05          ----------                                                                          "
//        };
//
//        // Add inputs to expected output ...
//        Vector expectedOutput = new Vector();
//        for(int i = 0; i < stringInput.length; i++) {
//            new EntryHolder(OriginEntrySource.EXTERNAL, stringInput[i]));
//        }
//
//        // ... add expected output ...
//        new EntryHolder(OriginEntrySource.SCRUBBER_EXPIRED,
//                "2004BA6044900-----1800---ACIN07CR  UBCLOSACCT 00000AUTO FR BA6044909Poplars Garage Fees                20.00C2006-01-05          ----------                                                                          "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_EXPIRED,
//                "2004BA6044900-----8000---ACAS07CR  UBCLOSACCT 00000AUTO FR BA6044909TP Generated Offset                20.00D2006-01-05          ----------                                                                          "));
//
//        // ... and run the test.
//        scrub(stringInput);
//        assertOriginEntries(4,(EntryHolder[]) expectedOutput.toArray(new EntryHolder[0]));
//    }
//
//    public void testExpiredAccountByDocumentType() throws Exception {
//
//        // Inputs.
//        String[] stringInput = new String[] {
//                "2004BL4631557-----4100---ACEX07LOCRLGEXPRACTLC00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                          ",
//                "2004BL4631557-----9041---ACLI07LOCRLGEXPRACTLC00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                          "
//        };
//
//        // Add inputs to expected output ...
//        Vector expectedOutput = new Vector();
//        for(int i = 0; i < stringInput.length; i++) {
//            new EntryHolder(OriginEntrySource.EXTERNAL, stringInput[i]));
//        }
//
//        // ... add expected output ...
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631557-----4100---ACEX07LOCRLGEXPRACTLC00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                          "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4631557-----9041---ACLI07LOCRLGEXPRACTLC00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                          "));
//
//        // ... and run the test.
//        scrub(stringInput);
//        assertOriginEntries(4,(EntryHolder[]) expectedOutput.toArray(new EntryHolder[0]));
//
//    }
//
//    public void failingTestExpiredAccountByBalanceType() throws Exception {
//
//        // Inputs.
//        String[] stringInput = new String[] {
//                "2004BL4131407-----4100---EXEX07TOPSLGEXPRACTEX     CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                 D                                        ",
//                "2004BL4131407-----9041---EXLI07TOPSLGEXPRACTEX     CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                 D                                        "
//        };
//
//        // Add inputs to expected output ...
//        Vector expectedOutput = new Vector();
//        for(int i = 0; i < stringInput.length; i++) {
//            new EntryHolder(OriginEntrySource.EXTERNAL, stringInput[i]));
//        }
//
//        // ... add expected output ...
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4131407-----4100---EXEX07TOPSLGEXPRACTEX00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                 D                                        "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4131407-----9041---EXLI07TOPSLGEXPRACTEX00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                 D                                        "));
//
//        // ... and run the test.
//        scrub(stringInput);
//        assertOriginEntries(4,(EntryHolder[]) expectedOutput.toArray(new EntryHolder[0]));
//
//    }
//
//    public void testExpiredAccountByOriginCode() throws Exception {
//
//        // Inputs.
//        String[] stringInput = new String[] {
//                "2004BL1031467-----5300---ACEE07DD  01EXPRACT0112345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
//                "2004BL1031467-----8000---ACAS07DD  01EXPRACT0112345214090047 EVERETT J PRESCOTT INC.                 1445.00C2006-01-05ABCDEFGHIG----------12345679                                                                  ",
//        };
//
//        // Add inputs to expected output ...
//        Vector expectedOutput = new Vector();
//        for(int i = 0; i < stringInput.length; i++) {
//            new EntryHolder(OriginEntrySource.EXTERNAL, stringInput[i]));
//        }
//
//        // ... add expected output ...
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL1031467-----5300---ACEE07DD  01EXPRACT0112345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL1031467-----8000---ACAS07DD  01EXPRACT0112345214090047 EVERETT J PRESCOTT INC.                 1445.00C2006-01-05ABCDEFGHIG----------12345679                                                                  "));
//
//        // ... and run the test.
//        scrub(stringInput);
//        assertOriginEntries(4,(EntryHolder[]) expectedOutput.toArray(new EntryHolder[0]));
//
//    }
//
//    public void FIXMEtestExpiredContractAndGrantAccountWithinThirtyDayGracePeriod() throws Exception {
//
//        // FIXME apparently didn't find data for this in the Kuali dev environment. So, can't test this now
//        // Need to test at some point however.
//
//    }
//
//    public void testExpiredContractAndGrantAccount() throws Exception {
//
//        // Inputs.
//        String[] stringInput = new String[] {
//            "2004BL4131407-----4100---ACEX07TOPSLGEXPIRCGAC00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                          ",
//            "2004BL4131407-----9041---ACLI07TOPSLGEXPIRCGAC00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                          "
//        };
//
//        // Add inputs to expected output ...
//        Vector expectedOutput = new Vector();
//        for(int i = 0; i < stringInput.length; i++) {
//            new EntryHolder(OriginEntrySource.EXTERNAL, stringInput[i]));
//        }
//
//        // ... add expected output ...
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4131407-----4100---ACEX07TOPSLGEXPIRCGAC00000CONCERTO OFFICE PRODUCTS                            48.53C2006-01-05          ----------                                                                          "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//                "2004BL4131407-----9041---ACLI07TOPSLGEXPIRCGAC00000CONCERTO OFFICE PRODUCTS                            48.53D2006-01-05          ----------                                                                          "));
//
//        // ... and run the test.
//        scrub(stringInput);
//        assertOriginEntries(4,(EntryHolder[]) expectedOutput.toArray(new EntryHolder[0]));
//
//    }
//
//    public void testExpiredAccount() throws Exception {
//
//        // Inputs.
//        String[] stringInput = new String[] {
//            "2004BL1031467-----5300---ACEE07CD  PDEXPIRACCT12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  ",
//            "2004BL1031467-----8000---ACAS07CD  PDEXPIRACCT12345214090047 EVERETT J PRESCOTT INC.                 1445.00C2006-01-05ABCDEFGHIG----------12345679                                                                  "
//        };
//
//        // Add inputs to expected output ...
//        Vector expectedOutput = new Vector();
//        for(int i = 0; i < stringInput.length; i++) {
//            new EntryHolder(OriginEntrySource.EXTERNAL, stringInput[i]));
//        }
//
//        // ... add expected output ...
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//            "2004BL1031467-----5300---ACEE07CD  PDEXPIRACCT12345214090047 EVERETT J PRESCOTT INC.                 1445.00D2006-01-05ABCDEFGHIJ----------12345678                                                                  "));
//        new EntryHolder(OriginEntrySource.SCRUBBER_VALID,
//            "2004BL1031467-----8000---ACAS07CD  PDEXPIRACCT12345214090047 EVERETT J PRESCOTT INC.                 1445.00C2006-01-05ABCDEFGHIG----------12345679                                                                  "));
//
//        // ... and run the test.
//        scrub(stringInput);
//        assertOriginEntries(4,(EntryHolder[]) expectedOutput.toArray(new EntryHolder[0]));
//    }

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

        String[] outputs = {
                "2004BL2231411-----2400---ACEX07ST    BLANKORIG00000PAYROLL EXPENSE TRANSFERS                          620.00C2006-01-05          ----------                                                                          ",
                "2004BL2231411-----8000---ACAS07ST  01BLANKORIG00000PAYROLL EXPENSE TRANSFERS                          620.00D2006-01-05          ----------                                                                          ",
                "2004BL2231411-----8000---ACAS07ST  01BLANKORIG00000GENERATED OFFSET                                   620.00C2006-02-21          ----------                                                                          "
        };

        EntryHolder[] outputTransactions = {
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[0]),
            new EntryHolder(OriginEntrySource.EXTERNAL,inputTransactions[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_ERROR, outputs[0]),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID, outputs[1]),
            new EntryHolder(OriginEntrySource.SCRUBBER_VALID, outputs[2])
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
        assertOriginEntries(4,outputTransactions);
    }

    private void scrub(String[] inputTransactions) {
        scrub(inputTransactions, false);
    }

    private void scrub(String[] inputTransactions,boolean makeTransactionDateCurrent) {
        clearOriginEntryTables();
        loadInputTransactions(OriginEntrySource.EXTERNAL,inputTransactions,date);
        persistenceService.getPersistenceBroker().clearCache();
        scrubberService.scrubEntries();
    }
}
