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

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.coa.businessobject.BalanceType;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.ObjectType;
import org.kuali.kfs.coa.service.AccountingPeriodService;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.batch.BatchSortUtil;
import org.kuali.kfs.gl.batch.DemergerSortComparator;
import org.kuali.kfs.gl.batch.service.RunDateService;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.gl.businessobject.OriginEntryTestBase;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.Message;
import org.kuali.kfs.sys.businessobject.OriginationCode;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * Tests the ScrubberService
 */
/**
 * This class...
 */
@ConfigureContext
public class ScrubberServiceTest extends OriginEntryTestBase {
    protected static final String DATE_FORMAT = "yyyy-MM-dd";

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ScrubberServiceTest.class);

    protected ScrubberService scrubberService = null;
    protected BusinessObjectService businessObjectService;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        LOG.debug("setUp() started");

        scrubberService = SpringContext.getBean(ScrubberService.class);
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);

        // Get the test date time service so we can specify the date/time of the run
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.MONTH, Calendar.JANUARY);
        c.set(Calendar.YEAR, TestUtils.getFiscalYearForTesting());

        // since the cutoff time is set to 10am (KFSP1/Scrubber+cutoff+time+configuration)
        // we want to ensure that the time is always after that time so the cutoff algorithm is not invoked
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);

        date = c.getTime();
        dateTimeService.setCurrentDate(date);
    }

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
     * Tests the scrubber considers entries with certain fields blank as errors
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testMiscellaneousBlankFields() throws Exception {

        String[] stringInput = new String[] { testingYear + "  6044900-----5300---ACEE07CHKDPDBLANKCHAR     12345214090047 EVERETT J PRESCOTT INC.                  1445.00D" + testingYear + "-01-05ABCDEFGHIJ----------12345678                                                                       ",
                testingYear + "BA       -----5300---ACEE07CHKDPDBLANKACCT     12345214090047 EVERETT J PRESCOTT INC.                  1445.00D" + testingYear + "-01-05ABCDEFGHIJ----------12345678                                                                       ",
                testingYear + "BA6044900-----    ---ACEE07CHKDPDBLANKOBJ      12345214090047 EVERETT J PRESCOTT INC.                  1445.00D" + testingYear + "-01-05ABCDEFGHIJ----------12345678                                                                       ",
                testingYear + "BA6044900-----5300---ACEE07    PDBLANKDOCT     12345214090047 EVERETT J PRESCOTT INC.                  1445.00D" + testingYear + "-01-05ABCDEFGHIJ----------12345678                                                                       ",
                testingYear + "BA6044900-----5300---ACEE07CHKD  BLANKORIG     12345214090047 EVERETT J PRESCOTT INC.                  1445.00D" + testingYear + "-01-05ABCDEFGHIJ----------12345678                                                                       ",
                testingYear + "BA6044900-----5300---ACEE07CHKDPD              12345214090047 EVERETT J PRESCOTT INC.                  1445.00D" + testingYear + "-01-05ABCDEFGHIJ----------12345678                                                                  ", };

        // Add inputs to expected output ...
        EntryHolder output[] = new EntryHolder[24];
        for (int i = 0; i < stringInput.length; i++) {
            output[i] = new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, stringInput[i]);
        }

        int c = stringInput.length;
        output[c++] = new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, testingYear + "BA       -----5300---ACEE07CHKDPDBLANKACCT     12345214090047 EVERETT J PRESCOTT INC.                  1445.00D" + testingYear + "-01-05ABCDEFGHIJ----------12345678                                                                       ");
        output[c++] = new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, testingYear + "  6044900-----5300---ACEE07CHKDPDBLANKCHAR     12345214090047 EVERETT J PRESCOTT INC.                  1445.00D" + testingYear + "-01-05ABCDEFGHIJ----------12345678                                                                       ");
        output[c++] = new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, testingYear + "BA6044900-----    ---ACEE07CHKDPDBLANKOBJ      12345214090047 EVERETT J PRESCOTT INC.                  1445.00D" + testingYear + "-01-05ABCDEFGHIJ----------12345678                                                                       ");
        output[c++] = new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, testingYear + "BA6044900-----5300---ACEE07CHKDPD              12345214090047 EVERETT J PRESCOTT INC.                  1445.00D" + testingYear + "-01-05ABCDEFGHIJ----------12345678                                                                       ");
        output[c++] = new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, testingYear + "BA6044900-----5300---ACEE07CHKD  BLANKORIG     12345214090047 EVERETT J PRESCOTT INC.                  1445.00D" + testingYear + "-01-05ABCDEFGHIJ----------12345678                                                                       ");
        output[c++] = new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, testingYear + "BA6044900-----5300---ACEE07    PDBLANKDOCT     12345214090047 EVERETT J PRESCOTT INC.                  1445.00D" + testingYear + "-01-05ABCDEFGHIJ----------12345678                                                                       ");

        output[c++] = new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, testingYear + "BA       -----5300---ACEE07CHKDPDBLANKACCT     12345214090047 EVERETT J PRESCOTT INC.                  1445.00D" + testingYear + "-01-05ABCDEFGHIJ----------12345678                                                                       ");
        output[c++] = new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, testingYear + "  6044900-----5300---ACEE07CHKDPDBLANKCHAR     12345214090047 EVERETT J PRESCOTT INC.                  1445.00D" + testingYear + "-01-05ABCDEFGHIJ----------12345678                                                                       ");
        output[c++] = new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, testingYear + "BA6044900-----    ---ACEE07CHKDPDBLANKOBJ      12345214090047 EVERETT J PRESCOTT INC.                  1445.00D" + testingYear + "-01-05ABCDEFGHIJ----------12345678                                                                       ");
        output[c++] = new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, testingYear + "BA6044900-----5300---ACEE07CHKDPD              12345214090047 EVERETT J PRESCOTT INC.                  1445.00D" + testingYear + "-01-05ABCDEFGHIJ----------12345678                                                                       ");
        output[c++] = new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, testingYear + "BA6044900-----5300---ACEE07CHKD  BLANKORIG     12345214090047 EVERETT J PRESCOTT INC.                  1445.00D" + testingYear + "-01-05ABCDEFGHIJ----------12345678                                                                       ");
        output[c++] = new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, testingYear + "BA6044900-----5300---ACEE07    PDBLANKDOCT     12345214090047 EVERETT J PRESCOTT INC.                  1445.00D" + testingYear + "-01-05ABCDEFGHIJ----------12345678                                                                       ");

        output[c++] = new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE, testingYear + "BA       -----5300---ACEE07CHKDPDBLANKACCT     12345214090047 EVERETT J PRESCOTT INC.                  1445.00D" + testingYear + "-01-05ABCDEFGHIJ----------12345678                                                                       ");
        output[c++] = new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE, testingYear + "  6044900-----5300---ACEE07CHKDPDBLANKCHAR     12345214090047 EVERETT J PRESCOTT INC.                  1445.00D" + testingYear + "-01-05ABCDEFGHIJ----------12345678                                                                       ");
        output[c++] = new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE, testingYear + "BA6044900-----    ---ACEE07CHKDPDBLANKOBJ      12345214090047 EVERETT J PRESCOTT INC.                  1445.00D" + testingYear + "-01-05ABCDEFGHIJ----------12345678                                                                       ");
        output[c++] = new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE, testingYear + "BA6044900-----5300---ACEE07CHKDPD              12345214090047 EVERETT J PRESCOTT INC.                  1445.00D" + testingYear + "-01-05ABCDEFGHIJ----------12345678                                                                       ");
        output[c++] = new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE, testingYear + "BA6044900-----5300---ACEE07CHKD  BLANKORIG     12345214090047 EVERETT J PRESCOTT INC.                  1445.00D" + testingYear + "-01-05ABCDEFGHIJ----------12345678                                                                       ");
        output[c++] = new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE, testingYear + "BA6044900-----5300---ACEE07    PDBLANKDOCT     12345214090047 EVERETT J PRESCOTT INC.                  1445.00D" + testingYear + "-01-05ABCDEFGHIJ----------12345678                                                                       ");

        scrub(stringInput);
        assertOriginEntries(7, output);
    }

    /**
     * Tests that the scrubber generates cost share encumbrances for pre-encumbrance entries
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testCostShareEncumbrancesForPreEncumbrances() throws Exception {
        final java.sql.Date scrubberDate = new java.sql.Date((SpringContext.getBean(RunDateService.class).calculateRunDate(dateTimeService.getCurrentDate())).getTime());
        final String formattedRunDate = new SimpleDateFormat(DATE_FORMAT).format(scrubberDate);

        // Inputs.
        String[] stringInput = new String[] { testingYear + "BL4831496CS0018000---PEAS07PE  01CSENCPE       00000TP Generated Offset                                   1650.00C" + testingYear + "-01-05          ----------                                      D                                ",
                testingYear + "BL4831496CS0014866---PEEX07PE  01CSENCPE       00000Correction to: 01-PU3355206                           1650.00D" + testingYear + "-01-05          ----------                                      D                                " };

        // Add inputs to expected output ...
        EntryHolder output[] = new EntryHolder[10];
        for (int i = 0; i < stringInput.length; i++) {
            output[i] = new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, stringInput[i]);
        }

        // ... add expected output ...
        output[2] = new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL1031400-----9940---CEEX07PE  01CSENCPE       00000Correction to: 01-PU3355206 FR-BL4831496+00000000000001650.00D" + formattedRunDate + "          ----------                                      D                                ");
        output[3] = new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL1031400-----9893---CEFB07PE  01CSENCPE       00000GENERATED OFFSET                        +00000000000001650.00C" + formattedRunDate + "          ----------                                                                       ");
        output[4] = new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4831496CS0014866---PEEX07PE  01CSENCPE       00000Correction to: 01-PU3355206             +00000000000001650.00D" + testingYear + "-01-05          ----------                                      D                                ");
        output[5] = new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4831496CS0018000---PEAS07PE  01CSENCPE       00000TP Generated Offset                     +00000000000001650.00C" + testingYear + "-01-05          ----------                                      D                                ");

        output[6] = new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL1031400-----9940---CEEX07PE  01CSENCPE       00000Correction to: 01-PU3355206 FR-BL4831496+00000000000001650.00D" + formattedRunDate + "          ----------                                      D                                ");
        output[7] = new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL1031400-----9893---CEFB07PE  01CSENCPE       00000GENERATED OFFSET                        +00000000000001650.00C" + formattedRunDate + "          ----------                                                                       ");
        output[8] = new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4831496CS0014866---PEEX07PE  01CSENCPE       00000Correction to: 01-PU3355206             +00000000000001650.00D" + testingYear + "-01-05          ----------                                      D                                ");
        output[9] = new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4831496CS0018000---PEAS07PE  01CSENCPE       00000TP Generated Offset                     +00000000000001650.00C" + testingYear + "-01-05          ----------                                      D                                ");

        scrub(stringInput);

        assertOriginEntries(7, output);
    }

    /**
     * Tests that the scrubber generates cost share encumbrances for internal encumbrances entries
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testCostShareEncumbrancesForInternalEncumbrances() throws Exception {
        final java.sql.Date scrubberDate = new java.sql.Date((SpringContext.getBean(RunDateService.class).calculateRunDate(dateTimeService.getCurrentDate())).getTime());
        final String formattedRunDate = new SimpleDateFormat(DATE_FORMAT).format(scrubberDate);

        String[] stringInput = new String[] { testingYear + "BL4831496CS0014190---IEEX07PE  01CSENCIE       00000THOMAS BUSEY/NEWEGG COMPUTERS                           40.72C" + testingYear + "-01-05          ----------                                      D                                ",
                testingYear + "BL4831496CS0018000---IEAS07PE  01CSENCIE       00000TP Generated Offset                                     40.72D" + testingYear + "-01-05          ----------                                      D                                " };

        // Add inputs to expected output ...
        EntryHolder[] output = new EntryHolder[10];
        for (int i = 0; i < stringInput.length; i++) {
            output[i] = new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, stringInput[i]);
        }

        // ... add expected output ...
        output[2] = new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL1031400-----9940---CEEX07PE  01CSENCIE       00000THOMAS BUSEY/NEWEGG COMPUTERFR-BL4831496+00000000000000040.72C" + formattedRunDate + "          ----------                                      D                                ");
        output[3] = new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL1031400-----9893---CEFB07PE  01CSENCIE       00000GENERATED OFFSET                        +00000000000000040.72D" + formattedRunDate + "          ----------                                                                       ");
        output[4] = new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4831496CS0014190---IEEX07PE  01CSENCIE       00000THOMAS BUSEY/NEWEGG COMPUTERS           +00000000000000040.72C" + testingYear + "-01-05          ----------                                      D                                ");
        output[5] = new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4831496CS0018000---IEAS07PE  01CSENCIE       00000TP Generated Offset                     +00000000000000040.72D" + testingYear + "-01-05          ----------                                      D                                ");

        output[6] = new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL1031400-----9940---CEEX07PE  01CSENCIE       00000THOMAS BUSEY/NEWEGG COMPUTERFR-BL4831496+00000000000000040.72C" + formattedRunDate + "          ----------                                      D                                ");
        output[7] = new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL1031400-----9893---CEFB07PE  01CSENCIE       00000GENERATED OFFSET                        +00000000000000040.72D" + formattedRunDate + "          ----------                                                                       ");
        output[8] = new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4831496CS0014190---IEEX07PE  01CSENCIE       00000THOMAS BUSEY/NEWEGG COMPUTERS           +00000000000000040.72C" + testingYear + "-01-05          ----------                                      D                                ");
        output[9] = new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4831496CS0018000---IEAS07PE  01CSENCIE       00000TP Generated Offset                     +00000000000000040.72D" + testingYear + "-01-05          ----------                                      D                                ");

        // ... and run the test.
        scrub(stringInput);
        assertOriginEntries(7, output);
    }

    /**
     * Tests that the scrubber generates cost share encumbrances for external encumbrance entries
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testCostShareEncumbrancesForExternalEncumbrances() throws Exception {

        String[] stringInput = new String[] { testingYear + "BL4831496CS0011800---EXIN07IB  LGCSENCEX       00000225050007 WILLIAMS DOTSON ASSOCIATES IN               1200.00D" + testingYear + "-01-05          ----------                                      D                                ",
                testingYear + "BL4831496CS0019041---EXLI07IB  LGCSENCEX       00000225050007 WILLIAMS DOTSON ASSOCIATES IN               1200.00C" + testingYear + "-01-05          ----------                                      D                                " };

        // Add inputs to expected output ...
        EntryHolder output[] = new EntryHolder[6];
        for (int i = 0; i < stringInput.length; i++) {
            output[i] = new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, stringInput[i]);
        }

        int c = stringInput.length;
        output[c++] = new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4831496CS0011800---EXIN07IB  LGCSENCEX       00000225050007 WILLIAMS DOTSON ASSOCIATES IN +00000000000001200.00D" + testingYear + "-01-05          ----------                                      D                                ");
        output[c++] = new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4831496CS0019041---EXLI07IB  LGCSENCEX       00000225050007 WILLIAMS DOTSON ASSOCIATES IN +00000000000001200.00C" + testingYear + "-01-05          ----------                                      D                                ");

        output[c++] = new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4831496CS0011800---EXIN07IB  LGCSENCEX       00000225050007 WILLIAMS DOTSON ASSOCIATES IN +00000000000001200.00D" + testingYear + "-01-05          ----------                                      D                                ");
        output[c++] = new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4831496CS0019041---EXLI07IB  LGCSENCEX       00000225050007 WILLIAMS DOTSON ASSOCIATES IN +00000000000001200.00C" + testingYear + "-01-05          ----------                                      D                                ");

        scrub(stringInput);
        assertOriginEntries(7, output);
    }

    /**
     * Tests that the scrubber does not generate cost share encumbrances for entries with cost share encumbrances
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testNoCostShareEncumbrancesForCostShareEncumbrances() throws Exception {

        String[] stringInput = new String[] { testingYear + "BL4831496CS0018000---CEAS07IB  01NOCSENCE      00000TP Generated Offset                                   1650.00C" + testingYear + "-01-05          ----------                                      D                                ",
                testingYear + "BL4831496CS0014866---CEEX07IB  01NOCSENCE      00000Correction to: 01-PU3355206                           1650.00D" + testingYear + "-01-05          ----------                                      D                                " };

        EntryHolder output[] = new EntryHolder[] { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, stringInput[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, stringInput[1]),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4831496CS0014866---CEEX07IB  01NOCSENCE      00000Correction to: 01-PU3355206             +00000000000001650.00D" + testingYear + "-01-05          ----------                                      D                                "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4831496CS0018000---CEAS07IB  01NOCSENCE      00000TP Generated Offset                     +00000000000001650.00C" + testingYear + "-01-05          ----------                                      D                                "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4831496CS0014866---CEEX07IB  01NOCSENCE      00000Correction to: 01-PU3355206             +00000000000001650.00D" + testingYear + "-01-05          ----------                                      D                                "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4831496CS0018000---CEAS07IB  01NOCSENCE      00000TP Generated Offset                     +00000000000001650.00C" + testingYear + "-01-05          ----------                                      D                                ") };

        scrub(stringInput);
        assertOriginEntries(7, output);

    }

    /**
     * Tests that the scrubber does not generate cost share encumbrances for entries created by the journal voucher document
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testNoCostShareEncumbrancesForJournalVoucher() throws Exception {

        String[] input = new String[] { testingYear + "BL4831496CS0014190---EXEX07JV  01NOCSENJV      00000THOMAS BUSEY/NEWEGG COMPUTERS                           40.72C" + testingYear + "-01-05          ----------                                      D                                ",
                testingYear + "BL4831496CS0018000---EXAS07JV  01NOCSENJV      00000TP Generated Offset                                     40.72D" + testingYear + "-01-05          ----------                                      D                                " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BL4831496CS0014190---EXEX07JV  01NOCSENJV      00000THOMAS BUSEY/NEWEGG COMPUTERS                           40.72C" + testingYear + "-01-05          ----------                                      D                                "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BL4831496CS0018000---EXAS07JV  01NOCSENJV      00000TP Generated Offset                                     40.72D" + testingYear + "-01-05          ----------                                      D                                "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4831496CS0014190---EXEX07JV  01NOCSENJV      00000THOMAS BUSEY/NEWEGG COMPUTERS           +00000000000000040.72C" + testingYear + "-01-05          ----------                                      D                                "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4831496CS0018000---EXAS07JV  01NOCSENJV      00000TP Generated Offset                     +00000000000000040.72D" + testingYear + "-01-05          ----------                                      D                                "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4831496CS0014190---EXEX07JV  01NOCSENJV      00000THOMAS BUSEY/NEWEGG COMPUTERS           +00000000000000040.72C" + testingYear + "-01-05          ----------                                      D                                "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4831496CS0018000---EXAS07JV  01NOCSENJV      00000TP Generated Offset                     +00000000000000040.72D" + testingYear + "-01-05          ----------                                      D                                ") };

        scrub(input);
        assertOriginEntries(7, output);
    }

    /**
     * Tests that the scrubber does not generate cost share encumbrances for beginning balance entries
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testNoCostShareEncumbrancesForBeginningBalances() throws Exception {

        String[] input = new String[] { testingYear + "BL4831496CS0011800---EXINCBIB  LGNOCSENCB      00000225050007 WILLIAMS DOTSON ASSOCIATES IN               1200.00D" + testingYear + "-01-05          ----------                                      D                                ",
                testingYear + "BL4831496CS0019041---EXLICBIB  LGNOCSENCB      00000225050007 WILLIAMS DOTSON ASSOCIATES IN               1200.00C" + testingYear + "-01-05          ----------                                      D                                " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BL4831496CS0011800---EXINCBIB  LGNOCSENCB      00000225050007 WILLIAMS DOTSON ASSOCIATES IN               1200.00D" + testingYear + "-01-05          ----------                                      D                                "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BL4831496CS0019041---EXLICBIB  LGNOCSENCB      00000225050007 WILLIAMS DOTSON ASSOCIATES IN               1200.00C" + testingYear + "-01-05          ----------                                      D                                "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4831496CS0011800---EXINCBIB  LGNOCSENCB      00000225050007 WILLIAMS DOTSON ASSOCIATES IN +00000000000001200.00D" + testingYear + "-01-05          ----------                                      D                                "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4831496CS0019041---EXLICBIB  LGNOCSENCB      00000225050007 WILLIAMS DOTSON ASSOCIATES IN +00000000000001200.00C" + testingYear + "-01-05          ----------                                      D                                "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4831496CS0011800---EXINCBIB  LGNOCSENCB      00000225050007 WILLIAMS DOTSON ASSOCIATES IN +00000000000001200.00D" + testingYear + "-01-05          ----------                                      D                                "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4831496CS0019041---EXLICBIB  LGNOCSENCB      00000225050007 WILLIAMS DOTSON ASSOCIATES IN +00000000000001200.00C" + testingYear + "-01-05          ----------                                      D                                ") };

        scrub(input);
        assertOriginEntries(7, output);
    }

    /**
     * Tests that the scrubber does not generate cost share encumbrances for entries with a balance type of "actual"
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testNoCostShareEncumbrancesForActuals() throws Exception {
        final java.sql.Date scrubberDate = new java.sql.Date((SpringContext.getBean(RunDateService.class).calculateRunDate(dateTimeService.getCurrentDate())).getTime());
        final String formattedRunDate = new SimpleDateFormat(DATE_FORMAT).format(scrubberDate);
        final String formattedOffsetDate = new SimpleDateFormat("MMdd").format(scrubberDate);
        final String docTypeOffsetDate = new SimpleDateFormat("MM/dd").format(scrubberDate);

        String[] input = new String[] { testingYear + "BL4831496CS0018000---ACAS07IB  01NOCSENAC      00000TP Generated Offset                                   1650.00C" + testingYear + "-01-05          ----------                                      D                                ",
                testingYear + "BL4831496CS0014866---ACEX07IB  01NOCSENAC      00000Correction to: 01-PU3355206                           1650.00D" + testingYear + "-01-05          ----------                                      D                                " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BL4831496CS0018000---ACAS07IB  01NOCSENAC      00000TP Generated Offset                                   1650.00C" + testingYear + "-01-05          ----------                                      D                                "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BL4831496CS0014866---ACEX07IB  01NOCSENAC      00000Correction to: 01-PU3355206                           1650.00D" + testingYear + "-01-05          ----------                                      D                                "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4831496CS0019915---ACTE07IB  01NOCSENAC      00000GENERATED COST SHARE FROM 4831496***" + formattedOffsetDate + "+00000000000001650.00C" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4831496CS0018000---ACAS07IB  01NOCSENAC      00000GENERATED OFFSET                 ***" + formattedOffsetDate + "+00000000000001650.00D" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL1031400-----9940---ACTE07IB  01NOCSENAC      00000GENERATED COST SHARE FROM 4831496***" + formattedOffsetDate + "+00000000000001650.00D" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL1031400-----8000---ACAS07IB  01NOCSENAC      00000GENERATED OFFSET                 ***" + formattedOffsetDate + "+00000000000001650.00C" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4831496CS0014866---ACEX07IB  01NOCSENAC      00000Correction to: 01-PU3355206             +00000000000001650.00D" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4831496CS0018000---ACAS07IB  01NOCSENAC      00000TP Generated Offset                     +00000000000001650.00C" + testingYear + "-01-05          ----------                                                                       "),

                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4831496CS0019915---ACTE07TF  CSCSHR" + docTypeOffsetDate + "     00000GENERATED COST SHARE FROM 4831496       +00000000000001650.00C" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4831496CS0018000---ACAS07TF  CSCSHR" + docTypeOffsetDate + "     00000GENERATED OFFSET                        +00000000000001650.00D" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL1031400-----9940---ACTE07TF  CSCSHR" + docTypeOffsetDate + "     00000GENERATED COST SHARE FROM 4831496       +00000000000001650.00D" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL1031400-----8000---ACAS07TF  CSCSHR" + docTypeOffsetDate + "     00000GENERATED OFFSET                        +00000000000001650.00C" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4831496CS0014866---ACEX07IB  01NOCSENAC      00000Correction to: 01-PU3355206             +00000000000001650.00D" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4831496CS0018000---ACAS07IB  01NOCSENAC      00000TP Generated Offset                     +00000000000001650.00C" + testingYear + "-01-05          ----------                                                                       ") };

        scrub(input);
        assertOriginEntries(7, output);
    }

    /**
     * Tests that the scrubber does not generate cost share encumbrances for entries with budget balance types
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testNoCostShareEncumbrancesForBudget() throws Exception {

        String[] input = new String[] { testingYear + "BL4831496CS0014190---BBEX07GEC 01NOCSENBB      00000THOMAS BUSEY/NEWEGG COMPUTERS                           40.72 " + testingYear + "-01-05          ----------                                      D                                ",
                testingYear + "BL4831496CS0018000---BBAS07GEC 01NOCSENBB      00000TP Generated Offset                                     40.72 " + testingYear + "-01-05          ----------                                      D                                " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BL4831496CS0014190---BBEX07GEC 01NOCSENBB      00000THOMAS BUSEY/NEWEGG COMPUTERS                           40.72 " + testingYear + "-01-05          ----------                                      D                                "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BL4831496CS0018000---BBAS07GEC 01NOCSENBB      00000TP Generated Offset                                     40.72 " + testingYear + "-01-05          ----------                                      D                                "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4831496CS0014190---BBEX07GEC 01NOCSENBB      00000THOMAS BUSEY/NEWEGG COMPUTERS           +00000000000000040.72 " + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4831496CS0018000---BBAS07GEC 01NOCSENBB      00000TP Generated Offset                     +00000000000000040.72 " + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4831496CS0014190---BBEX07GEC 01NOCSENBB      00000THOMAS BUSEY/NEWEGG COMPUTERS           +00000000000000040.72 " + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4831496CS0018000---BBAS07GEC 01NOCSENBB      00000TP Generated Offset                     +00000000000000040.72 " + testingYear + "-01-05          ----------                                                                       ") };

        scrub(input);
        assertOriginEntries(7, output);
    }

    /**
     * Tests that the scrubber does not generate cost share encumbrances for entries that do not represent expenses
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testNoCostShareForEncumbrancesNonExpenses() throws Exception {

        String[] input = new String[] { testingYear + "BL4831496CS0011800---EXIN07IB  LGNOCSENIN      00000225050007 WILLIAMS DOTSON ASSOCIATES IN               1200.00D" + testingYear + "-01-05          ----------                                      D                                ",
                testingYear + "BL4831496CS0019041---EXLI07IB  LGNOCSENIN      00000225050007 WILLIAMS DOTSON ASSOCIATES IN               1200.00C" + testingYear + "-01-05          ----------                                      D                                ", };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BL4831496CS0011800---EXIN07IB  LGNOCSENIN      00000225050007 WILLIAMS DOTSON ASSOCIATES IN               1200.00D" + testingYear + "-01-05          ----------                                      D                                "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BL4831496CS0019041---EXLI07IB  LGNOCSENIN      00000225050007 WILLIAMS DOTSON ASSOCIATES IN               1200.00C" + testingYear + "-01-05          ----------                                      D                                "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4831496CS0011800---EXIN07IB  LGNOCSENIN      00000225050007 WILLIAMS DOTSON ASSOCIATES IN +00000000000001200.00D" + testingYear + "-01-05          ----------                                      D                                "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4831496CS0019041---EXLI07IB  LGNOCSENIN      00000225050007 WILLIAMS DOTSON ASSOCIATES IN +00000000000001200.00C" + testingYear + "-01-05          ----------                                      D                                "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4831496CS0011800---EXIN07IB  LGNOCSENIN      00000225050007 WILLIAMS DOTSON ASSOCIATES IN +00000000000001200.00D" + testingYear + "-01-05          ----------                                      D                                "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4831496CS0019041---EXLI07IB  LGNOCSENIN      00000225050007 WILLIAMS DOTSON ASSOCIATES IN +00000000000001200.00C" + testingYear + "-01-05          ----------                                      D                                ") };

        scrub(input);
        assertOriginEntries(7, output);
    }

    /**
     * Tests that the scrubber generates miscellaneous cost share entries
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testCostShareOther() throws Exception {
        final java.sql.Date scrubberDate = new java.sql.Date((SpringContext.getBean(RunDateService.class).calculateRunDate(dateTimeService.getCurrentDate())).getTime());
        final String formattedRunDate = new SimpleDateFormat(DATE_FORMAT).format(scrubberDate);
        final String formattedOffsetDate = new SimpleDateFormat("MMdd").format(scrubberDate);
        final String docTypeOffsetDate = new SimpleDateFormat("MM/dd").format(scrubberDate);

        String[] input = new String[] { testingYear + "BL4831496CS0014000---ACEX07DI  EUCSHROTHER     00000NOV-05 IMU Business Office          2224               241.75D" + testingYear + "-05-30          ----------                                                                       ",
                testingYear + "BL4831496CS0018000---ACAS07DI  EUCSHROTHER     00000NOV-05 IMU Business Office          2237               241.75C" + testingYear + "-05-30          ----------                                                                       " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BL4831496CS0014000---ACEX07DI  EUCSHROTHER     00000NOV-05 IMU Business Office          2224               241.75D" + testingYear + "-05-30          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BL4831496CS0018000---ACAS07DI  EUCSHROTHER     00000NOV-05 IMU Business Office          2237               241.75C" + testingYear + "-05-30          ----------                                                                       "),

                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4831496CS0019915---ACTE07DI  EUCSHROTHER     00000GENERATED COST SHARE FROM 4831496***" + formattedOffsetDate + "+00000000000000241.75C" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4831496CS0018000---ACAS07DI  EUCSHROTHER     00000GENERATED OFFSET                 ***" + formattedOffsetDate + "+00000000000000241.75D" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL1031400-----9940---ACTE07DI  EUCSHROTHER     00000GENERATED COST SHARE FROM 4831496***" + formattedOffsetDate + "+00000000000000241.75D" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL1031400-----8000---ACAS07DI  EUCSHROTHER     00000GENERATED OFFSET                 ***" + formattedOffsetDate + "+00000000000000241.75C" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4831496CS0014000---ACEX07DI  EUCSHROTHER     00000NOV-05 IMU Business Office          2224+00000000000000241.75D" + testingYear + "-05-30          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4831496CS0018000---ACAS07DI  EUCSHROTHER     00000NOV-05 IMU Business Office          2237+00000000000000241.75C" + testingYear + "-05-30          ----------                                                                       "),

                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4831496CS0019915---ACTE07TF  CSCSHR" + docTypeOffsetDate + "     00000GENERATED COST SHARE FROM 4831496       +00000000000000241.75C" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4831496CS0018000---ACAS07TF  CSCSHR" + docTypeOffsetDate + "     00000GENERATED OFFSET                        +00000000000000241.75D" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL1031400-----9940---ACTE07TF  CSCSHR" + docTypeOffsetDate + "     00000GENERATED COST SHARE FROM 4831496       +00000000000000241.75D" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL1031400-----8000---ACAS07TF  CSCSHR" + docTypeOffsetDate + "     00000GENERATED OFFSET                        +00000000000000241.75C" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4831496CS0014000---ACEX07DI  EUCSHROTHER     00000NOV-05 IMU Business Office          2224+00000000000000241.75D" + testingYear + "-05-30          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4831496CS0018000---ACAS07DI  EUCSHROTHER     00000NOV-05 IMU Business Office          2237+00000000000000241.75C" + testingYear + "-05-30          ----------                                                                       ") };

        scrub(input);
        assertOriginEntries(7, output);
    }

    /**
     * Tests that the scrubber generates cost share entries for entries with object code level == "TRIN"
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testCostShareForLevelTrin() throws Exception {
        final java.sql.Date scrubberDate = new java.sql.Date((SpringContext.getBean(RunDateService.class).calculateRunDate(dateTimeService.getCurrentDate())).getTime());
        final String formattedRunDate = new SimpleDateFormat(DATE_FORMAT).format(scrubberDate);
        final String formattedOffsetDate = new SimpleDateFormat("MMdd").format(scrubberDate);
        final String docTypeOffsetDate = new SimpleDateFormat("MM/dd").format(scrubberDate);

        String[] input = new String[] { testingYear + "BL4831496CS0019915---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                       94.35D" + testingYear + "-01-05          ----------                                                                       ",
                testingYear + "BL4831496CS0019041---ACLI07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                       94.35C" + testingYear + "-01-05          ----------                                                                       " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BL4831496CS0019915---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                       94.35D" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BL4831496CS0019041---ACLI07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.                       94.35C" + testingYear + "-01-05          ----------                                                                       "),

                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4831496CS0019041---ACLI07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.       +00000000000000094.35C" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4831496CS0019915---ACTE07DI  01CSHRTRIN      00000GENERATED COST SHARE FROM 4831496***" + formattedOffsetDate + "+00000000000000094.35C" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4831496CS0018000---ACAS07DI  01CSHRTRIN      00000GENERATED OFFSET                 ***" + formattedOffsetDate + "+00000000000000094.35D" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL1031400-----9915---ACTE07DI  01CSHRTRIN      00000GENERATED COST SHARE FROM 4831496***" + formattedOffsetDate + "+00000000000000094.35D" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL1031400-----8000---ACAS07DI  01CSHRTRIN      00000GENERATED OFFSET                 ***" + formattedOffsetDate + "+00000000000000094.35C" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4831496CS0019915---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.       +00000000000000094.35D" + testingYear + "-01-05          ----------                                                                       "),

                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4831496CS0019041---ACLI07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.       +00000000000000094.35C" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4831496CS0019915---ACTE07TF  CSCSHR" + docTypeOffsetDate + "     00000GENERATED COST SHARE FROM 4831496       +00000000000000094.35C" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4831496CS0018000---ACAS07TF  CSCSHR" + docTypeOffsetDate + "     00000GENERATED OFFSET                        +00000000000000094.35D" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL1031400-----9915---ACTE07TF  CSCSHR" + docTypeOffsetDate + "     00000GENERATED COST SHARE FROM 4831496       +00000000000000094.35D" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL1031400-----8000---ACAS07TF  CSCSHR" + docTypeOffsetDate + "     00000GENERATED OFFSET                        +00000000000000094.35C" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4831496CS0019915---ACEX07DI  01CSHRTRIN      00000Rite Quality Office Supplies Inc.       +00000000000000094.35D" + testingYear + "-01-05          ----------                                                                       "), };

        scrub(input);
        assertOriginEntries(7, output);
    }

    /**
     * Tests that the scrubber generates cost share entries for entries with object code level == "TREX"
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testCostShareForLevelTrex() throws Exception {
        final java.sql.Date scrubberDate = new java.sql.Date((SpringContext.getBean(RunDateService.class).calculateRunDate(dateTimeService.getCurrentDate())).getTime());
        final String formattedRunDate = new SimpleDateFormat(DATE_FORMAT).format(scrubberDate);
        final String formattedOffsetDate = new SimpleDateFormat("MMdd").format(scrubberDate);
        final String docTypeOffsetDate = new SimpleDateFormat("MM/dd").format(scrubberDate);

        String[] input = new String[] { testingYear + "BL4831496CS0019900---ACEX07CR  01CSHRTREX      00000Poplars Garage Fees                                     20.00C" + formattedRunDate + "          ----------                                                                       ",
                testingYear + "BL4831496CS0018000---ACAS07CR  01CSHRTREX      00000TP Generated Offset                                     20.00D" + formattedRunDate + "          ----------                                                                       " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BL4831496CS0019900---ACEX07CR  01CSHRTREX      00000Poplars Garage Fees                                     20.00C" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BL4831496CS0018000---ACAS07CR  01CSHRTREX      00000TP Generated Offset                                     20.00D" + formattedRunDate + "          ----------                                                                       "),

                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4831496CS0018000---ACAS07CR  01CSHRTREX      00000TP Generated Offset                     +00000000000000020.00D" + formattedRunDate + "          ----------                                                                      "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4831496CS0019915---ACTE07CR  01CSHRTREX      00000GENERATED COST SHARE FROM 4831496***" + formattedOffsetDate + "+00000000000000020.00D" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4831496CS0018000---ACAS07CR  01CSHRTREX      00000GENERATED OFFSET                 ***" + formattedOffsetDate + "+00000000000000020.00C" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL1031400-----9959---ACTE07CR  01CSHRTREX      00000GENERATED COST SHARE FROM 4831496***" + formattedOffsetDate + "+00000000000000020.00C" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL1031400-----8000---ACAS07CR  01CSHRTREX      00000GENERATED OFFSET                 ***" + formattedOffsetDate + "+00000000000000020.00D" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4831496CS0019900---ACEX07CR  01CSHRTREX      00000Poplars Garage Fees                     +00000000000000020.00C" + formattedRunDate + "          ----------                                                                       "),

                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4831496CS0018000---ACAS07CR  01CSHRTREX      00000TP Generated Offset                     +00000000000000020.00D" + formattedRunDate + "          ----------                                                                      "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4831496CS0019915---ACTE07TF  CSCSHR" + docTypeOffsetDate + "     00000GENERATED COST SHARE FROM 4831496       +00000000000000020.00D" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4831496CS0018000---ACAS07TF  CSCSHR" + docTypeOffsetDate + "     00000GENERATED OFFSET                        +00000000000000020.00C" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL1031400-----9959---ACTE07TF  CSCSHR" + docTypeOffsetDate + "     00000GENERATED COST SHARE FROM 4831496       +00000000000000020.00C" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL1031400-----8000---ACAS07TF  CSCSHR" + docTypeOffsetDate + "     00000GENERATED OFFSET                        +00000000000000020.00D" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4831496CS0019900---ACEX07CR  01CSHRTREX      00000Poplars Garage Fees                     +00000000000000020.00C" + formattedRunDate + "          ----------                                                                       "), };

        scrub(input);
        assertOriginEntries(7, output);
    }

    /**
     * Tests that the scrubber generates cost share entries for entries with object code level == "TRAV"
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testCostShareForLevelTrav() throws Exception {
        final java.sql.Date scrubberDate = new java.sql.Date((SpringContext.getBean(RunDateService.class).calculateRunDate(dateTimeService.getCurrentDate())).getTime());
        final String formattedRunDate = new SimpleDateFormat(DATE_FORMAT).format(scrubberDate);
        final String formattedOffsetDate = new SimpleDateFormat("MMdd").format(scrubberDate);
        final String docTypeOffsetDate = new SimpleDateFormat("MM/dd").format(scrubberDate);

        final int testingYearAsInt = Integer.parseInt(testingYear);
        final String previousTestingYear = new Integer(testingYearAsInt - 1).toString();

        String[] input = new String[] { testingYear + "BL4631625CS0016000---ACEX07DI  EUCSHRTRAV      00000NOV-05 IMU Business Office          2224               241.75D" + previousTestingYear + "-11-30          ----------                                                                       ",
                testingYear + "BL4631625CS0018000---ACAS07DI  EUCSHRTRAV      00000NOV-05 IMU Business Office          2237               241.75C" + previousTestingYear + "-11-30          ----------                                                                       " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_EXPIRED_OUTPUT_FILE, testingYear + "BL4631625CS0016000---ACEX07DI  EUCSHRTRAV      00000NOV-05 IMU Business Office          2224+00000000000000241.75D" + previousTestingYear + "-11-30          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_EXPIRED_OUTPUT_FILE, testingYear + "BL4631625CS0018000---ACAS07DI  EUCSHRTRAV      00000NOV-05 IMU Business Office          2237+00000000000000241.75C" + previousTestingYear + "-11-30          ----------                                                                       "),

                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BL4631625CS0016000---ACEX07DI  EUCSHRTRAV      00000NOV-05 IMU Business Office          2224               241.75D" + previousTestingYear + "-11-30          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BL4631625CS0018000---ACAS07DI  EUCSHRTRAV      00000NOV-05 IMU Business Office          2237               241.75C" + previousTestingYear + "-11-30          ----------                                                                       "),

                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4631625CS0019915---ACTE07DI  EUCSHRTRAV      00000GENERATED COST SHARE FROM 4631625***" + formattedOffsetDate + "+00000000000000241.75C" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4631625CS0018000---ACAS07DI  EUCSHRTRAV      00000GENERATED OFFSET                 ***" + formattedOffsetDate + "+00000000000000241.75D" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL1031400-----9960---ACTE07DI  EUCSHRTRAV      00000GENERATED COST SHARE FROM 4631625***" + formattedOffsetDate + "+00000000000000241.75D" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL1031400-----8000---ACAS07DI  EUCSHRTRAV      00000GENERATED OFFSET                 ***" + formattedOffsetDate + "+00000000000000241.75C" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4631625CS0016000---ACEX07DI  EUCSHRTRAV      00000NOV-05 IMU Business Office          2224+00000000000000241.75D" + previousTestingYear + "-11-30          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4631625CS0018000---ACAS07DI  EUCSHRTRAV      00000NOV-05 IMU Business Office          2237+00000000000000241.75C" + previousTestingYear + "-11-30          ----------                                                                       "),

                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4631625CS0019915---ACTE07TF  CSCSHR" + docTypeOffsetDate + "     00000GENERATED COST SHARE FROM 4631625       +00000000000000241.75C" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4631625CS0018000---ACAS07TF  CSCSHR" + docTypeOffsetDate + "     00000GENERATED OFFSET                        +00000000000000241.75D" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL1031400-----9960---ACTE07TF  CSCSHR" + docTypeOffsetDate + "     00000GENERATED COST SHARE FROM 4631625       +00000000000000241.75D" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL1031400-----8000---ACAS07TF  CSCSHR" + docTypeOffsetDate + "     00000GENERATED OFFSET                        +00000000000000241.75C" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4631625CS0016000---ACEX07DI  EUCSHRTRAV      00000NOV-05 IMU Business Office          2224+00000000000000241.75D" + previousTestingYear + "-11-30          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4631625CS0018000---ACAS07DI  EUCSHRTRAV      00000NOV-05 IMU Business Office          2237+00000000000000241.75C" + previousTestingYear + "-11-30          ----------                                                                       ") };

        scrub(input);
        assertOriginEntries(7, output);
    }

    /**
     * Tests that the scrubber generates cost share entries for entries with object code level == "TRAN"
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testCostShareForLevelTran() throws Exception {
        final java.sql.Date scrubberDate = new java.sql.Date((SpringContext.getBean(RunDateService.class).calculateRunDate(dateTimeService.getCurrentDate())).getTime());
        final String formattedRunDate = new SimpleDateFormat(DATE_FORMAT).format(scrubberDate);
        final String formattedOffsetDate = new SimpleDateFormat("MMdd").format(scrubberDate);
        final String docTypeOffsetDate = new SimpleDateFormat("MM/dd").format(scrubberDate);

        String[] input = new String[] { testingYear + "BL4631618CS0015199---ACEX07DI  01CSHRTRAN      00000Rite Quality Office Supplies Inc.                       94.35D" + testingYear + "-01-05          ----------                                                                       ",
                testingYear + "BL4631618CS0019041---ACLI07DI  01CSHRTRAN      00000Rite Quality Office Supplies Inc.                       94.35C" + testingYear + "-01-05          ----------                                                                       " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_EXPIRED_OUTPUT_FILE, testingYear + "BL4631618CS0015199---ACEX07DI  01CSHRTRAN      00000Rite Quality Office Supplies Inc.       +00000000000000094.35D" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_EXPIRED_OUTPUT_FILE, testingYear + "BL4631618CS0019041---ACLI07DI  01CSHRTRAN      00000Rite Quality Office Supplies Inc.       +00000000000000094.35C" + testingYear + "-01-05          ----------                                                                       "),

                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BL4631618CS0015199---ACEX07DI  01CSHRTRAN      00000Rite Quality Office Supplies Inc.                       94.35D" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BL4631618CS0019041---ACLI07DI  01CSHRTRAN      00000Rite Quality Office Supplies Inc.                       94.35C" + testingYear + "-01-05          ----------                                                                       "),

                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4631618CS0019915---ACTE07DI  01CSHRTRAN      00000GENERATED COST SHARE FROM 4631618***" + formattedOffsetDate + "+00000000000000094.35C" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4631618CS0018000---ACAS07DI  01CSHRTRAN      00000GENERATED OFFSET                 ***" + formattedOffsetDate + "+00000000000000094.35D" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL1031400-----9959---ACTE07DI  01CSHRTRAN      00000GENERATED COST SHARE FROM 4631618***" + formattedOffsetDate + "+00000000000000094.35D" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL1031400-----8000---ACAS07DI  01CSHRTRAN      00000GENERATED OFFSET                 ***" + formattedOffsetDate + "+00000000000000094.35C" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4631618CS0015199---ACEX07DI  01CSHRTRAN      00000Rite Quality Office Supplies Inc.       +00000000000000094.35D" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4631618CS0019041---ACLI07DI  01CSHRTRAN      00000Rite Quality Office Supplies Inc.       +00000000000000094.35C" + testingYear + "-01-05          ----------                                                                       "),

                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4631618CS0019915---ACTE07TF  CSCSHR" + docTypeOffsetDate + "     00000GENERATED COST SHARE FROM 4631618       +00000000000000094.35C" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4631618CS0018000---ACAS07TF  CSCSHR" + docTypeOffsetDate + "     00000GENERATED OFFSET                        +00000000000000094.35D" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL1031400-----9959---ACTE07TF  CSCSHR" + docTypeOffsetDate + "     00000GENERATED COST SHARE FROM 4631618       +00000000000000094.35D" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL1031400-----8000---ACAS07TF  CSCSHR" + docTypeOffsetDate + "     00000GENERATED OFFSET                        +00000000000000094.35C" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4631618CS0015199---ACEX07DI  01CSHRTRAN      00000Rite Quality Office Supplies Inc.       +00000000000000094.35D" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4631618CS0019041---ACLI07DI  01CSHRTRAN      00000Rite Quality Office Supplies Inc.       +00000000000000094.35C" + testingYear + "-01-05          ----------                                                                       ")

        };

        scrub(input);
        assertOriginEntries(7, output);
    }

    /**
     * Tests that the scrubber generates cost share entries for entries with object code level == "SAAP"
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testCostShareForLevelSaap() throws Exception {
        final java.sql.Date scrubberDate = new java.sql.Date((SpringContext.getBean(RunDateService.class).calculateRunDate(dateTimeService.getCurrentDate())).getTime());
        final String formattedRunDate = new SimpleDateFormat(DATE_FORMAT).format(scrubberDate);
        final String formattedOffsetDate = new SimpleDateFormat("MMdd").format(scrubberDate);
        final String docTypeOffsetDate = new SimpleDateFormat("MM/dd").format(scrubberDate);

        String[] input = new String[] { testingYear + "BL4831496CS0012350---ACEX07CR  01CSHRSAAP      00000Poplars Garage Fees                                     20.00C" + testingYear + "-01-05          ----------                                                                       ",
                testingYear + "BL4831496CS0018000---ACAS07CR  01CSHRSAAP      00000TP Generated Offset                                     20.00D" + testingYear + "-01-05          ----------                                                                       " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BL4831496CS0012350---ACEX07CR  01CSHRSAAP      00000Poplars Garage Fees                                     20.00C" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BL4831496CS0018000---ACAS07CR  01CSHRSAAP      00000TP Generated Offset                                     20.00D" + testingYear + "-01-05          ----------                                                                       "),

                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4831496CS0019915---ACTE07CR  01CSHRSAAP      00000GENERATED COST SHARE FROM 4831496***" + formattedOffsetDate + "+00000000000000020.00D" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4831496CS0018000---ACAS07CR  01CSHRSAAP      00000GENERATED OFFSET                 ***" + formattedOffsetDate + "+00000000000000020.00C" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL1031400-----9923---ACTE07CR  01CSHRSAAP      00000GENERATED COST SHARE FROM 4831496***" + formattedOffsetDate + "+00000000000000020.00C" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL1031400-----8000---ACAS07CR  01CSHRSAAP      00000GENERATED OFFSET                 ***" + formattedOffsetDate + "+00000000000000020.00D" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4831496CS0012350---ACEX07CR  01CSHRSAAP      00000Poplars Garage Fees                     +00000000000000020.00C" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4831496CS0018000---ACAS07CR  01CSHRSAAP      00000TP Generated Offset                     +00000000000000020.00D" + testingYear + "-01-05          ----------                                                                       "),

                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4831496CS0019915---ACTE07TF  CSCSHR" + docTypeOffsetDate + "     00000GENERATED COST SHARE FROM 4831496       +00000000000000020.00D" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4831496CS0018000---ACAS07TF  CSCSHR" + docTypeOffsetDate + "     00000GENERATED OFFSET                        +00000000000000020.00C" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL1031400-----9923---ACTE07TF  CSCSHR" + docTypeOffsetDate + "     00000GENERATED COST SHARE FROM 4831496       +00000000000000020.00C" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL1031400-----8000---ACAS07TF  CSCSHR" + docTypeOffsetDate + "     00000GENERATED OFFSET                        +00000000000000020.00D" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4831496CS0012350---ACEX07CR  01CSHRSAAP      00000Poplars Garage Fees                     +00000000000000020.00C" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4831496CS0018000---ACAS07CR  01CSHRSAAP      00000TP Generated Offset                     +00000000000000020.00D" + testingYear + "-01-05          ----------                                                                       ")

        };

        scrub(input);
        assertOriginEntries(7, output);
    }

    /**
     * Tests that the scrubber generates cost share entries for entries with object code level == "RESV"
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testCostShareForLevelResv() throws Exception {
        final java.sql.Date scrubberDate = new java.sql.Date((SpringContext.getBean(RunDateService.class).calculateRunDate(dateTimeService.getCurrentDate())).getTime());
        final String formattedRunDate = new SimpleDateFormat(DATE_FORMAT).format(scrubberDate);
        final String formattedOffsetDate = new SimpleDateFormat("MMdd").format(scrubberDate);
        final String docTypeOffsetDate = new SimpleDateFormat("MM/dd").format(scrubberDate);

        final int testingYearAsInt = Integer.parseInt(testingYear);
        final String previousTestingYear = new Integer(testingYearAsInt - 1).toString();

        String[] input = new String[] { testingYear + "BL4631625CS0017900---ACEX07DI  EUCSHRRESV      00000NOV-05 IMU Business Office          2224               241.75D" + previousTestingYear + "-11-30          ----------                                                                       ",
                testingYear + "BL4631625CS0018000---ACAS07DI  EUCSHRRESV      00000NOV-05 IMU Business Office          2237               241.75C" + previousTestingYear + "-11-30          ----------                                                                       " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_EXPIRED_OUTPUT_FILE, testingYear + "BL4631625CS0017900---ACEX07DI  EUCSHRRESV      00000NOV-05 IMU Business Office          2224+00000000000000241.75D" + previousTestingYear + "-11-30          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_EXPIRED_OUTPUT_FILE, testingYear + "BL4631625CS0018000---ACAS07DI  EUCSHRRESV      00000NOV-05 IMU Business Office          2237+00000000000000241.75C" + previousTestingYear + "-11-30          ----------                                                                       "),

                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BL4631625CS0017900---ACEX07DI  EUCSHRRESV      00000NOV-05 IMU Business Office          2224               241.75D" + previousTestingYear + "-11-30          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BL4631625CS0018000---ACAS07DI  EUCSHRRESV      00000NOV-05 IMU Business Office          2237               241.75C" + previousTestingYear + "-11-30          ----------                                                                       "),

                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4631625CS0019915---ACTE07DI  EUCSHRRESV      00000GENERATED COST SHARE FROM 4631625***" + formattedOffsetDate + "+00000000000000241.75C" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4631625CS0018000---ACAS07DI  EUCSHRRESV      00000GENERATED OFFSET                 ***" + formattedOffsetDate + "+00000000000000241.75D" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL1031400-----9979---ACTE07DI  EUCSHRRESV      00000GENERATED COST SHARE FROM 4631625***" + formattedOffsetDate + "+00000000000000241.75D" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL1031400-----8000---ACAS07DI  EUCSHRRESV      00000GENERATED OFFSET                 ***" + formattedOffsetDate + "+00000000000000241.75C" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4631625CS0017900---ACEX07DI  EUCSHRRESV      00000NOV-05 IMU Business Office          2224+00000000000000241.75D" + previousTestingYear + "-11-30          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4631625CS0018000---ACAS07DI  EUCSHRRESV      00000NOV-05 IMU Business Office          2237+00000000000000241.75C" + previousTestingYear + "-11-30          ----------                                                                       "),

                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4631625CS0019915---ACTE07TF  CSCSHR" + docTypeOffsetDate + "     00000GENERATED COST SHARE FROM 4631625       +00000000000000241.75C" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4631625CS0018000---ACAS07TF  CSCSHR" + docTypeOffsetDate + "     00000GENERATED OFFSET                        +00000000000000241.75D" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL1031400-----9979---ACTE07TF  CSCSHR" + docTypeOffsetDate + "     00000GENERATED COST SHARE FROM 4631625       +00000000000000241.75D" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL1031400-----8000---ACAS07TF  CSCSHR" + docTypeOffsetDate + "     00000GENERATED OFFSET                        +00000000000000241.75C" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4631625CS0017900---ACEX07DI  EUCSHRRESV      00000NOV-05 IMU Business Office          2224+00000000000000241.75D" + previousTestingYear + "-11-30          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4631625CS0018000---ACAS07DI  EUCSHRRESV      00000NOV-05 IMU Business Office          2237+00000000000000241.75C" + previousTestingYear + "-11-30          ----------                                                                       ")

        };

        scrub(input);
        assertOriginEntries(7, output);
    }

    /**
     * Tests that the scrubber generates cost share entries for entries with object code level == "PRSA"
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testCostShareForLevelPrsa() throws Exception {
        final java.sql.Date scrubberDate = new java.sql.Date((SpringContext.getBean(RunDateService.class).calculateRunDate(dateTimeService.getCurrentDate())).getTime());
        final String formattedRunDate = new SimpleDateFormat(DATE_FORMAT).format(scrubberDate);
        final String formattedOffsetDate = new SimpleDateFormat("MMdd").format(scrubberDate);
        final String docTypeOffsetDate = new SimpleDateFormat("MM/dd").format(scrubberDate);

        String[] input = new String[] { testingYear + "BL4631618CS0012400---ACEX07DI  01CSHRPRSA      00000Rite Quality Office Supplies Inc.                       94.35D" + testingYear + "-01-05          ----------                                                                       ",
                testingYear + "BL4631618CS0019041---ACLI07DI  01CSHRPRSA      00000Rite Quality Office Supplies Inc.                       94.35C" + testingYear + "-01-05          ----------                                                                       " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_EXPIRED_OUTPUT_FILE, testingYear + "BL4631618CS0012400---ACEX07DI  01CSHRPRSA      00000Rite Quality Office Supplies Inc.       +00000000000000094.35D" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_EXPIRED_OUTPUT_FILE, testingYear + "BL4631618CS0019041---ACLI07DI  01CSHRPRSA      00000Rite Quality Office Supplies Inc.       +00000000000000094.35C" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BL4631618CS0012400---ACEX07DI  01CSHRPRSA      00000Rite Quality Office Supplies Inc.                       94.35D" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BL4631618CS0019041---ACLI07DI  01CSHRPRSA      00000Rite Quality Office Supplies Inc.                       94.35C" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4631618CS0019915---ACTE07DI  01CSHRPRSA      00000GENERATED COST SHARE FROM 4631618***" + formattedOffsetDate + "+00000000000000094.35C" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4631618CS0018000---ACAS07DI  01CSHRPRSA      00000GENERATED OFFSET                 ***" + formattedOffsetDate + "+00000000000000094.35D" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL1031400-----9924---ACTE07DI  01CSHRPRSA      00000GENERATED COST SHARE FROM 4631618***" + formattedOffsetDate + "+00000000000000094.35D" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL1031400-----8000---ACAS07DI  01CSHRPRSA      00000GENERATED OFFSET                 ***" + formattedOffsetDate + "+00000000000000094.35C" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4631618CS0012400---ACEX07DI  01CSHRPRSA      00000Rite Quality Office Supplies Inc.       +00000000000000094.35D" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4631618CS0019041---ACLI07DI  01CSHRPRSA      00000Rite Quality Office Supplies Inc.       +00000000000000094.35C" + testingYear + "-01-05          ----------                                                                       "),

                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4631618CS0019915---ACTE07TF  CSCSHR" + docTypeOffsetDate + "     00000GENERATED COST SHARE FROM 4631618       +00000000000000094.35C" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4631618CS0018000---ACAS07TF  CSCSHR" + docTypeOffsetDate + "     00000GENERATED OFFSET                        +00000000000000094.35D" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL1031400-----9924---ACTE07TF  CSCSHR" + docTypeOffsetDate + "     00000GENERATED COST SHARE FROM 4631618       +00000000000000094.35D" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL1031400-----8000---ACAS07TF  CSCSHR" + docTypeOffsetDate + "     00000GENERATED OFFSET                        +00000000000000094.35C" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4631618CS0012400---ACEX07DI  01CSHRPRSA      00000Rite Quality Office Supplies Inc.       +00000000000000094.35D" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4631618CS0019041---ACLI07DI  01CSHRPRSA      00000Rite Quality Office Supplies Inc.       +00000000000000094.35C" + testingYear + "-01-05          ----------                                                                       ")

        };

        scrub(input);
        assertOriginEntries(7, output);
    }

    /**
     * Tests that the scrubber generates cost share entries for entries with object code level == "PART"
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testCostShareForLevelPart() throws Exception {
        final java.sql.Date scrubberDate = new java.sql.Date((SpringContext.getBean(RunDateService.class).calculateRunDate(dateTimeService.getCurrentDate())).getTime());
        final String formattedRunDate = new SimpleDateFormat(DATE_FORMAT).format(scrubberDate);
        final String formattedOffsetDate = new SimpleDateFormat("MMdd").format(scrubberDate);
        final String docTypeOffsetDate = new SimpleDateFormat("MM/dd").format(scrubberDate);

        String[] input = new String[] { testingYear + "BL4831496CS0012300---ACEX07CR  01CSHRPART      00000Poplars Garage Fees                                     20.00C" + testingYear + "-01-05          ----------                                                                       ",
                testingYear + "BL4831496CS0018000---ACAS07CR  01CSHRPART      00000TP Generated Offset                                     20.00D" + testingYear + "-01-05          ----------                                                                       " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BL4831496CS0012300---ACEX07CR  01CSHRPART      00000Poplars Garage Fees                                     20.00C" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BL4831496CS0018000---ACAS07CR  01CSHRPART      00000TP Generated Offset                                     20.00D" + testingYear + "-01-05          ----------                                                                       "),

                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4831496CS0019915---ACTE07CR  01CSHRPART      00000GENERATED COST SHARE FROM 4831496***" + formattedOffsetDate + "+00000000000000020.00D" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4831496CS0018000---ACAS07CR  01CSHRPART      00000GENERATED OFFSET                 ***" + formattedOffsetDate + "+00000000000000020.00C" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL1031400-----9923---ACTE07CR  01CSHRPART      00000GENERATED COST SHARE FROM 4831496***" + formattedOffsetDate + "+00000000000000020.00C" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL1031400-----8000---ACAS07CR  01CSHRPART      00000GENERATED OFFSET                 ***" + formattedOffsetDate + "+00000000000000020.00D" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4831496CS0012300---ACEX07CR  01CSHRPART      00000Poplars Garage Fees                     +00000000000000020.00C" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4831496CS0018000---ACAS07CR  01CSHRPART      00000TP Generated Offset                     +00000000000000020.00D" + testingYear + "-01-05          ----------                                                                       "),

                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4831496CS0019915---ACTE07TF  CSCSHR" + docTypeOffsetDate + "     00000GENERATED COST SHARE FROM 4831496       +00000000000000020.00D" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4831496CS0018000---ACAS07TF  CSCSHR" + docTypeOffsetDate + "     00000GENERATED OFFSET                        +00000000000000020.00C" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL1031400-----9923---ACTE07TF  CSCSHR" + docTypeOffsetDate + "     00000GENERATED COST SHARE FROM 4831496       +00000000000000020.00C" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL1031400-----8000---ACAS07TF  CSCSHR" + docTypeOffsetDate + "     00000GENERATED OFFSET                        +00000000000000020.00D" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4831496CS0012300---ACEX07CR  01CSHRPART      00000Poplars Garage Fees                     +00000000000000020.00C" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4831496CS0018000---ACAS07CR  01CSHRPART      00000TP Generated Offset                     +00000000000000020.00D" + testingYear + "-01-05          ----------                                                                       ") };

        scrub(input);
        assertOriginEntries(7, output);
    }

    /**
     * Tests that the scrubber generates cost share entries for entries with object code level == "ICOE"
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testCostShareForLevelIcoe() throws Exception {
        final java.sql.Date scrubberDate = new java.sql.Date((SpringContext.getBean(RunDateService.class).calculateRunDate(dateTimeService.getCurrentDate())).getTime());
        final String formattedRunDate = new SimpleDateFormat(DATE_FORMAT).format(scrubberDate);
        final String formattedOffsetDate = new SimpleDateFormat("MMdd").format(scrubberDate);
        final String docTypeOffsetDate = new SimpleDateFormat("MM/dd").format(scrubberDate);

        final int testingYearAsInt = Integer.parseInt(testingYear);
        final String previousTestingYear = new Integer(testingYearAsInt - 1).toString();

        String[] input = new String[] { testingYear + "BL4631625CS0015500---ACEX07DI  EUCSHRICOE      00000NOV-05 IMU Business Office          2224               241.75D" + previousTestingYear + "-11-30          ----------                                                                       ",
                testingYear + "BL4631625CS0018000---ACAS07DI  EUCSHRICOE      00000NOV-05 IMU Business Office          2237               241.75C" + previousTestingYear + "-11-30          ----------                                                                       " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_EXPIRED_OUTPUT_FILE, testingYear + "BL4631625CS0015500---ACEX07DI  EUCSHRICOE      00000NOV-05 IMU Business Office          2224+00000000000000241.75D" + previousTestingYear + "-11-30          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_EXPIRED_OUTPUT_FILE, testingYear + "BL4631625CS0018000---ACAS07DI  EUCSHRICOE      00000NOV-05 IMU Business Office          2237+00000000000000241.75C" + previousTestingYear + "-11-30          ----------                                                                       "),

                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BL4631625CS0015500---ACEX07DI  EUCSHRICOE      00000NOV-05 IMU Business Office          2224               241.75D" + previousTestingYear + "-11-30          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BL4631625CS0018000---ACAS07DI  EUCSHRICOE      00000NOV-05 IMU Business Office          2237               241.75C" + previousTestingYear + "-11-30          ----------                                                                       "),

                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4631625CS0019915---ACTE07DI  EUCSHRICOE      00000GENERATED COST SHARE FROM 4631625***" + formattedOffsetDate + "+00000000000000241.75C" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4631625CS0018000---ACAS07DI  EUCSHRICOE      00000GENERATED OFFSET                 ***" + formattedOffsetDate + "+00000000000000241.75D" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL1031400-----9955---ACTE07DI  EUCSHRICOE      00000GENERATED COST SHARE FROM 4631625***" + formattedOffsetDate + "+00000000000000241.75D" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL1031400-----8000---ACAS07DI  EUCSHRICOE      00000GENERATED OFFSET                 ***" + formattedOffsetDate + "+00000000000000241.75C" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4631625CS0015500---ACEX07DI  EUCSHRICOE      00000NOV-05 IMU Business Office          2224+00000000000000241.75D" + previousTestingYear + "-11-30          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4631625CS0018000---ACAS07DI  EUCSHRICOE      00000NOV-05 IMU Business Office          2237+00000000000000241.75C" + previousTestingYear + "-11-30          ----------                                                                       "),

                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4631625CS0019915---ACTE07TF  CSCSHR" + docTypeOffsetDate + "     00000GENERATED COST SHARE FROM 4631625       +00000000000000241.75C" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4631625CS0018000---ACAS07TF  CSCSHR" + docTypeOffsetDate + "     00000GENERATED OFFSET                        +00000000000000241.75D" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL1031400-----9955---ACTE07TF  CSCSHR" + docTypeOffsetDate + "     00000GENERATED COST SHARE FROM 4631625       +00000000000000241.75D" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL1031400-----8000---ACAS07TF  CSCSHR" + docTypeOffsetDate + "     00000GENERATED OFFSET                        +00000000000000241.75C" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4631625CS0015500---ACEX07DI  EUCSHRICOE      00000NOV-05 IMU Business Office          2224+00000000000000241.75D" + previousTestingYear + "-11-30          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4631625CS0018000---ACAS07DI  EUCSHRICOE      00000NOV-05 IMU Business Office          2237+00000000000000241.75C" + previousTestingYear + "-11-30          ----------                                                                       ")

        };

        scrub(input);
        assertOriginEntries(7, output);
    }

    /**
     * Tests that the scrubber does not generate cost share entries for encumbrance entries
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testNoCostShareTransfersForEncumbranceTransactions() throws Exception {
        final java.sql.Date scrubberDate = new java.sql.Date((SpringContext.getBean(RunDateService.class).calculateRunDate(dateTimeService.getCurrentDate())).getTime());
        final String formattedRunDate = new SimpleDateFormat(DATE_FORMAT).format(scrubberDate);
        final String formattedOffsetDate = new SimpleDateFormat("MMdd").format(scrubberDate);
        final String docTypeOffsetDate = new SimpleDateFormat("MM/dd").format(scrubberDate);

        final int testingYearAsInt = Integer.parseInt(testingYear);
        final String previousTestingYear = new Integer(testingYearAsInt - 1).toString();

        String[] input = new String[] { testingYear + "BL4631625CS0014110---EXEX07PE  EUNOCSHREX      00000NOV-05 IMU Business Office          2224               241.75D" + previousTestingYear + "-11-30          ----------                                      D                                ",
                testingYear + "BL4631625CS0018000---EXAS07PE  EUNOCSHREX      00000NOV-05 IMU Business Office          2237               241.75C" + previousTestingYear + "-11-30          ----------                                      D                                " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_EXPIRED_OUTPUT_FILE, testingYear + "BL4631625CS0014110---EXEX07PE  EUNOCSHREX      00000NOV-05 IMU Business Office          2224+00000000000000241.75D" + previousTestingYear + "-11-30          ----------                                      D                                "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_EXPIRED_OUTPUT_FILE, testingYear + "BL4631625CS0018000---EXAS07PE  EUNOCSHREX      00000NOV-05 IMU Business Office          2237+00000000000000241.75C" + previousTestingYear + "-11-30          ----------                                      D                                "),

                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BL4631625CS0014110---EXEX07PE  EUNOCSHREX      00000NOV-05 IMU Business Office          2224               241.75D" + previousTestingYear + "-11-30          ----------                                      D                                "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BL4631625CS0018000---EXAS07PE  EUNOCSHREX      00000NOV-05 IMU Business Office          2237               241.75C" + previousTestingYear + "-11-30          ----------                                      D                                "),

                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL1031400-----9940---CEEX07PE  EUNOCSHREX      00000NOV-05 IMU Business Office  FR-BL4631625+00000000000000241.75D" + formattedRunDate + "          ----------                                      D                                "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL1031400-----9893---CEFB07PE  EUNOCSHREX      00000GENERATED OFFSET                        +00000000000000241.75C" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4631625CS0014110---EXEX07PE  EUNOCSHREX      00000NOV-05 IMU Business Office          2224+00000000000000241.75D" + previousTestingYear + "-11-30          ----------                                      D                                "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4631625CS0018000---EXAS07PE  EUNOCSHREX      00000NOV-05 IMU Business Office          2237+00000000000000241.75C" + previousTestingYear + "-11-30          ----------                                      D                                "),

                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL1031400-----9940---CEEX07PE  EUNOCSHREX      00000NOV-05 IMU Business Office  FR-BL4631625+00000000000000241.75D" + formattedRunDate + "          ----------                                      D                                "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL1031400-----9893---CEFB07PE  EUNOCSHREX      00000GENERATED OFFSET                        +00000000000000241.75C" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4631625CS0014110---EXEX07PE  EUNOCSHREX      00000NOV-05 IMU Business Office          2224+00000000000000241.75D" + previousTestingYear + "-11-30          ----------                                      D                                "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4631625CS0018000---EXAS07PE  EUNOCSHREX      00000NOV-05 IMU Business Office          2237+00000000000000241.75C" + previousTestingYear + "-11-30          ----------                                      D                                ")

        };

        scrub(input);
        assertOriginEntries(7, output);
    }

    /**
     * Tests that the scrubber does not generate cost share entries for entries with budget balance types
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testNoCostShareTransfersForBudgetTransactions() throws Exception {

        String[] input = new String[] { testingYear + "BL4631618CS0015000---BBEX07DI  01NOCSHRBB      00000Rite Quality Office Supplies Inc.                       94.35 " + testingYear + "-01-05          ----------                                                                       ",
                testingYear + "BL4631618CS0019041---BBLI07DI  01NOCSHRBB      00000Rite Quality Office Supplies Inc.                       94.35 " + testingYear + "-01-05          ----------                                                                       " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_EXPIRED_OUTPUT_FILE, testingYear + "BL4631618CS0015000---BBEX07DI  01NOCSHRBB      00000Rite Quality Office Supplies Inc.       +00000000000000094.35 " + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_EXPIRED_OUTPUT_FILE, testingYear + "BL4631618CS0019041---BBLI07DI  01NOCSHRBB      00000Rite Quality Office Supplies Inc.       +00000000000000094.35 " + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BL4631618CS0015000---BBEX07DI  01NOCSHRBB      00000Rite Quality Office Supplies Inc.                       94.35 " + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BL4631618CS0019041---BBLI07DI  01NOCSHRBB      00000Rite Quality Office Supplies Inc.                       94.35 " + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4631618CS0015000---BBEX07DI  01NOCSHRBB      00000Rite Quality Office Supplies Inc.       +00000000000000094.35 " + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4631618CS0019041---BBLI07DI  01NOCSHRBB      00000Rite Quality Office Supplies Inc.       +00000000000000094.35 " + testingYear + "-01-05          ----------                                                                       "),

                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4631618CS0015000---BBEX07DI  01NOCSHRBB      00000Rite Quality Office Supplies Inc.       +00000000000000094.35 " + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4631618CS0019041---BBLI07DI  01NOCSHRBB      00000Rite Quality Office Supplies Inc.       +00000000000000094.35 " + testingYear + "-01-05          ----------                                                                       ") };

        scrub(input);
        assertOriginEntries(7, output);
    }

    /**
     * Tests that the scrubber generates a plant endebtedness entries
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testPlantIndebtedness() throws Exception {

        String[] input = new String[] {
                testingYear + "BA9020204-----9100---ACLI07GEC 01DEBTEDNES     00000Biology Stockroom                                       13.77D" + testingYear + "-01-05          ----------                                                                       ",
                testingYear + "BA9020204-----8000---ACAS07GEC 01DEBTEDNES     00000TP Generated Offset                                     13.77C" + testingYear + "-01-05          ----------                                                                       ",
                testingYear + "BA9120657-----9120---ACLI07DI  EUDEBTEDNES     00000PAYROLL EXPENSE TRANSFERS                              620.00C" + testingYear + "-01-05          ----------                                                                       ",
                testingYear + "BA9120657-----8000---ACAS07DI  EUDEBTEDNES     00000PAYROLL EXPENSE TRANSFERS                              620.00D" + testingYear + "-01-05          ----------                                                                       " };

        EntryHolder[] output = new EntryHolder[] {
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BA9020204-----9100---ACLI07GEC 01DEBTEDNES     00000Biology Stockroom                                       13.77D" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BA9020204-----8000---ACAS07GEC 01DEBTEDNES     00000TP Generated Offset                                     13.77C" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BA9120657-----9120---ACLI07DI  EUDEBTEDNES     00000PAYROLL EXPENSE TRANSFERS                              620.00C" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BA9120657-----8000---ACAS07DI  EUDEBTEDNES     00000PAYROLL EXPENSE TRANSFERS                              620.00D" + testingYear + "-01-05          ----------                                                                       "),

                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BA9020204-----8000---ACAS07GEC 01DEBTEDNES     00000TP Generated Offset                     +00000000000000013.77C" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BA9020204-----9100---ACLI07GEC 01DEBTEDNES     00000GENERATED TRANSFER TO NET PLANT         +00000000000000013.77C" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BA9020204-----9899---ACFB07GEC 01DEBTEDNES     00000GENERATED TRANSFER TO NET PLANT         +00000000000000013.77D" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BA9544900-----9100---ACLI07GEC 01DEBTEDNES     00000GENERATED TRANSFER FROM BA 9020204      +00000000000000013.77D" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BA9544900-----9899---ACFB07GEC 01DEBTEDNES     00000GENERATED TRANSFER FROM BA 9020204      +00000000000000013.77C" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BA9020204-----9100---ACLI07GEC 01DEBTEDNES     00000Biology Stockroom                       +00000000000000013.77D" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BA9120657-----8000---ACAS07DI  EUDEBTEDNES     00000PAYROLL EXPENSE TRANSFERS               +00000000000000620.00D" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BA9120657-----9120---ACLI07DI  EUDEBTEDNES     00000GENERATED TRANSFER TO NET PLANT         +00000000000000620.00D" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BA9120657-----9899---ACFB07DI  EUDEBTEDNES     00000GENERATED TRANSFER TO NET PLANT         +00000000000000620.00C" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BA9544900-----9120---ACLI07DI  EUDEBTEDNES     00000GENERATED TRANSFER FROM BA 9120657      +00000000000000620.00C" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BA9544900-----9899---ACFB07DI  EUDEBTEDNES     00000GENERATED TRANSFER FROM BA 9120657      +00000000000000620.00D" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BA9120657-----9120---ACLI07DI  EUDEBTEDNES     00000PAYROLL EXPENSE TRANSFERS               +00000000000000620.00C" + testingYear + "-01-05          ----------                                                                       "),

                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BA9020204-----8000---ACAS07GEC 01DEBTEDNES     00000TP Generated Offset                     +00000000000000013.77C" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BA9020204-----9100---ACLI07GEC 01DEBTEDNES     00000GENERATED TRANSFER TO NET PLANT         +00000000000000013.77C" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BA9020204-----9899---ACFB07GEC 01DEBTEDNES     00000GENERATED TRANSFER TO NET PLANT         +00000000000000013.77D" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BA9544900-----9100---ACLI07GEC 01DEBTEDNES     00000GENERATED TRANSFER FROM BA 9020204      +00000000000000013.77D" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BA9544900-----9899---ACFB07GEC 01DEBTEDNES     00000GENERATED TRANSFER FROM BA 9020204      +00000000000000013.77C" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BA9020204-----9100---ACLI07GEC 01DEBTEDNES     00000Biology Stockroom                       +00000000000000013.77D" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BA9120657-----8000---ACAS07DI  EUDEBTEDNES     00000PAYROLL EXPENSE TRANSFERS               +00000000000000620.00D" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BA9120657-----9120---ACLI07DI  EUDEBTEDNES     00000GENERATED TRANSFER TO NET PLANT         +00000000000000620.00D" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BA9120657-----9899---ACFB07DI  EUDEBTEDNES     00000GENERATED TRANSFER TO NET PLANT         +00000000000000620.00C" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BA9544900-----9120---ACLI07DI  EUDEBTEDNES     00000GENERATED TRANSFER FROM BA 9120657      +00000000000000620.00C" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BA9544900-----9899---ACFB07DI  EUDEBTEDNES     00000GENERATED TRANSFER FROM BA 9120657      +00000000000000620.00D" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BA9120657-----9120---ACLI07DI  EUDEBTEDNES     00000PAYROLL EXPENSE TRANSFERS               +00000000000000620.00C" + testingYear + "-01-05          ----------                                                                       "),

        };

        scrub(input);
        assertOriginEntries(7, output);
    }

    /**
     * Tests that the scrubber does not generate a plant endebtedness entry for encumbrance entries
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testNoIndebtednessForEncumbranceEntries() throws Exception {

        String[] input = new String[] { testingYear + "BA9021004-----9120---EXLI07YEBAEUNODEBTEX      00000PAYROLL EXPENSE TRANSFERS                              620.00C" + testingYear + "-01-05          ----------                                      D                                ",
                testingYear + "BA9021004-----8000---EXAS07YEBAEUNODEBTEX      00000PAYROLL EXPENSE TRANSFERS                              620.00D" + testingYear + "-01-05          ----------                                      D                                " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BA9021004-----9120---EXLI07YEBAEUNODEBTEX      00000PAYROLL EXPENSE TRANSFERS                              620.00C" + testingYear + "-01-05          ----------                                      D                                "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BA9021004-----8000---EXAS07YEBAEUNODEBTEX      00000PAYROLL EXPENSE TRANSFERS                              620.00D" + testingYear + "-01-05          ----------                                      D                                "),

                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BA9021004-----8000---EXAS07YEBAEUNODEBTEX      00000PAYROLL EXPENSE TRANSFERS               +00000000000000620.00D" + testingYear + "-01-05          ----------                                      D                                "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BA9021004-----9120---EXLI07YEBAEUNODEBTEX      00000PAYROLL EXPENSE TRANSFERS               +00000000000000620.00C" + testingYear + "-01-05          ----------                                      D                                "),

                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BA9021004-----8000---EXAS07YEBAEUNODEBTEX      00000PAYROLL EXPENSE TRANSFERS               +00000000000000620.00D" + testingYear + "-01-05          ----------                                      D                                "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BA9021004-----9120---EXLI07YEBAEUNODEBTEX      00000PAYROLL EXPENSE TRANSFERS               +00000000000000620.00C" + testingYear + "-01-05          ----------                                      D                                "),

        };

        scrub(input);
        assertOriginEntries(7, output);
    }

    /**
     * Tests that the scrubber generates a capitalization entry for entries with object sub type == "CL"
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testCapitalizationForObjectSubTypeCL() throws Exception {

        String[] input = new String[] { testingYear + "BA6044900-----7099---ACEE07CR  PDCAPITALCL     00000214090047 EVERETT J PRESCOTT INC.                     1445.00D" + testingYear + "-01-05          ----------                                                                       ",
                testingYear + "BA6044900-----8000---ACAS07CR  PDCAPITALCL     00000214090047 EVERETT J PRESCOTT INC.                     1445.00C" + testingYear + "-01-05          ----------                                                                       " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BA6044900-----7099---ACEE07CR  PDCAPITALCL     00000214090047 EVERETT J PRESCOTT INC.                     1445.00D" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BA6044900-----8000---ACAS07CR  PDCAPITALCL     00000214090047 EVERETT J PRESCOTT INC.                     1445.00C" + testingYear + "-01-05          ----------                                                                       "),

                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BA9544900-----9603---ACLI07CR  PDCAPITALCL     00000GENERATED LIABILITY                     +00000000000001445.00D" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BA9544900-----9899---ACFB07CR  PDCAPITALCL     00000GENERATED LIABILITY                     +00000000000001445.00C" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BA6044900-----7099---ACEE07CR  PDCAPITALCL     00000214090047 EVERETT J PRESCOTT INC.       +00000000000001445.00D" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BA6044900-----8000---ACAS07CR  PDCAPITALCL     00000214090047 EVERETT J PRESCOTT INC.       +00000000000001445.00C" + testingYear + "-01-05          ----------                                                                       "),

                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BA9544900-----9603---ACLI07CR  PDCAPITALCL     00000GENERATED LIABILITY                     +00000000000001445.00D" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BA9544900-----9899---ACFB07CR  PDCAPITALCL     00000GENERATED LIABILITY                     +00000000000001445.00C" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BA6044900-----7099---ACEE07CR  PDCAPITALCL     00000214090047 EVERETT J PRESCOTT INC.       +00000000000001445.00D" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BA6044900-----8000---ACAS07CR  PDCAPITALCL     00000214090047 EVERETT J PRESCOTT INC.       +00000000000001445.00C" + testingYear + "-01-05          ----------                                                                       ")

        };

        scrub(input);
        assertOriginEntries(7, output);
    }

    /**
     * Tests that the scrubber generates a capitalization entry for entries with object sub type == "LR"
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testCapitalizationForObjectSubTypeLR() throws Exception {

        String[] input = new String[] { testingYear + "BA6044913-----7465---ACEE07GEC 01CAPITALLR     00000CONCERTO OFFICE PRODUCTS                                48.53C" + testingYear + "-01-05          ----------                                                                       ",
                testingYear + "BA6044913-----9041---ACLI07GEC 01CAPITALLR     00000CONCERTO OFFICE PRODUCTS                                48.53D" + testingYear + "-01-05          ----------                                                                       " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BA6044913-----7465---ACEE07GEC 01CAPITALLR     00000CONCERTO OFFICE PRODUCTS                                48.53C" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BA6044913-----9041---ACLI07GEC 01CAPITALLR     00000CONCERTO OFFICE PRODUCTS                                48.53D" + testingYear + "-01-05          ----------                                                                       "),

                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BA9544900-----8665---ACAS07GEC 01CAPITALLR     00000GENERATED CAPITALIZATION                +00000000000000048.53C" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BA9544900-----9899---ACFB07GEC 01CAPITALLR     00000GENERATED CAPITALIZATION                +00000000000000048.53D" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BA6044913-----7465---ACEE07GEC 01CAPITALLR     00000CONCERTO OFFICE PRODUCTS                +00000000000000048.53C" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BA6044913-----9041---ACLI07GEC 01CAPITALLR     00000CONCERTO OFFICE PRODUCTS                +00000000000000048.53D" + testingYear + "-01-05          ----------                                                                       "),

                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BA9544900-----8665---ACAS07GEC 01CAPITALLR     00000GENERATED CAPITALIZATION                +00000000000000048.53C" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BA9544900-----9899---ACFB07GEC 01CAPITALLR     00000GENERATED CAPITALIZATION                +00000000000000048.53D" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BA6044913-----7465---ACEE07GEC 01CAPITALLR     00000CONCERTO OFFICE PRODUCTS                +00000000000000048.53C" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BA6044913-----9041---ACLI07GEC 01CAPITALLR     00000CONCERTO OFFICE PRODUCTS                +00000000000000048.53D" + testingYear + "-01-05          ----------                                                                       "),

        };

        scrub(input);
        assertOriginEntries(7, output);
    }

    /**
     * Tests that the scrubber does not generate a capitalization entry for entries with certain document types
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testNoCapitalizationForCertainDocumentTypes() throws Exception {

        String[] input = new String[] { testingYear + "BA6044913-----7300---ACEE07TF  LGNOCAPTF       00000CONCERTO OFFICE PRODUCTS                                48.53C" + testingYear + "-01-05          ----------                                                                       ",
                testingYear + "BA6044913-----9041---ACLI07TF  LGNOCAPTF       00000CONCERTO OFFICE PRODUCTS                                48.53D" + testingYear + "-01-05          ----------                                                                       " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BA6044913-----7300---ACEE07TF  LGNOCAPTF       00000CONCERTO OFFICE PRODUCTS                                48.53C" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BA6044913-----9041---ACLI07TF  LGNOCAPTF       00000CONCERTO OFFICE PRODUCTS                                48.53D" + testingYear + "-01-05          ----------                                                                       "),

                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BA6044913-----7300---ACEE07TF  LGNOCAPTF       00000CONCERTO OFFICE PRODUCTS                +00000000000000048.53C" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BA6044913-----9041---ACLI07TF  LGNOCAPTF       00000CONCERTO OFFICE PRODUCTS                +00000000000000048.53D" + testingYear + "-01-05          ----------                                                                       "),

                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BA6044913-----7300---ACEE07TF  LGNOCAPTF       00000CONCERTO OFFICE PRODUCTS                +00000000000000048.53C" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BA6044913-----9041---ACLI07TF  LGNOCAPTF       00000CONCERTO OFFICE PRODUCTS                +00000000000000048.53D" + testingYear + "-01-05          ----------                                                                       "),

        };

        scrub(input);
        assertOriginEntries(7, output);
    }

    /**
     * Tests that the scrubber does not generate a capitalization entry for encumbrance entries
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testNoCapitalizationForEncumbranceEntry() throws Exception {

        String[] input = new String[] { testingYear + "BA6044906-----7300---EXEE07YEBALGNOCAPEX       00000CONCERTO OFFICE PRODUCTS                                48.53C" + testingYear + "-01-05          ----------                                      D                                ",
                testingYear + "BA6044906-----9041---EXLI07YEBALGNOCAPEX       00000CONCERTO OFFICE PRODUCTS                                48.53D" + testingYear + "-01-05          ----------                                      D                                " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BA6044906-----7300---EXEE07YEBALGNOCAPEX       00000CONCERTO OFFICE PRODUCTS                                48.53C" + testingYear + "-01-05          ----------                                      D                                "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BA6044906-----9041---EXLI07YEBALGNOCAPEX       00000CONCERTO OFFICE PRODUCTS                                48.53D" + testingYear + "-01-05          ----------                                      D                                "),

                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BA6044906-----7300---EXEE07YEBALGNOCAPEX       00000CONCERTO OFFICE PRODUCTS                +00000000000000048.53C" + testingYear + "-01-05          ----------                                      D                                "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BA6044906-----9041---EXLI07YEBALGNOCAPEX       00000CONCERTO OFFICE PRODUCTS                +00000000000000048.53D" + testingYear + "-01-05          ----------                                      D                                "),

                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BA6044906-----7300---EXEE07YEBALGNOCAPEX       00000CONCERTO OFFICE PRODUCTS                +00000000000000048.53C" + testingYear + "-01-05          ----------                                      D                                "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BA6044906-----9041---EXLI07YEBALGNOCAPEX       00000CONCERTO OFFICE PRODUCTS                +00000000000000048.53D" + testingYear + "-01-05          ----------                                      D                                "),

        };

        scrub(input);
        assertOriginEntries(7, output);
    }

    /**
     * Tests that the scrubber generates the correct offset entries, even when there are mulitple period codes involved
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testOffsetGenerationAcrossMultipleFiscalPeriods() throws Exception {
        final java.sql.Date scrubberDate = new java.sql.Date((SpringContext.getBean(RunDateService.class).calculateRunDate(dateTimeService.getCurrentDate())).getTime());
        final String formattedRunDate = new SimpleDateFormat(DATE_FORMAT).format(scrubberDate);
        final String formattedOffsetDate = new SimpleDateFormat("MMdd").format(scrubberDate);
        final String docTypeOffsetDate = new SimpleDateFormat("MM/dd").format(scrubberDate);

        String[] input = new String[] { testingYear + "BL1031497-----4190---ACEX07GEC 01OFFSETPER     00000THOMAS BUSEY/NEWEGG COMPUTERS                           40.72C" + testingYear + "-01-05          ----------                                                                       ",
                testingYear + "BL1031497-----8000---ACAS08GEC 01OFFSETPER     00000TP Generated Offset                                     40.72D" + testingYear + "-01-05          ----------                                                                       " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BL1031497-----4190---ACEX07GEC 01OFFSETPER     00000THOMAS BUSEY/NEWEGG COMPUTERS                           40.72C" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BL1031497-----8000---ACAS08GEC 01OFFSETPER     00000TP Generated Offset                                     40.72D" + testingYear + "-01-05          ----------                                                                       "),

                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL1031497-----4190---ACEX07GEC 01OFFSETPER     00000THOMAS BUSEY/NEWEGG COMPUTERS           +00000000000000040.72C" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL1031497-----8000---ACAS07GEC 01OFFSETPER     00000GENERATED OFFSET                        +00000000000000040.72D" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL1031497-----8000---ACAS08GEC 01OFFSETPER     00000TP Generated Offset                     +00000000000000040.72D" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL1031497-----8000---ACAS08GEC 01OFFSETPER     00000GENERATED OFFSET                        +00000000000000040.72C" + formattedRunDate + "          ----------                                                                       "),

                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL1031497-----4190---ACEX07GEC 01OFFSETPER     00000THOMAS BUSEY/NEWEGG COMPUTERS           +00000000000000040.72C" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL1031497-----8000---ACAS07GEC 01OFFSETPER     00000GENERATED OFFSET                        +00000000000000040.72D" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL1031497-----8000---ACAS08GEC 01OFFSETPER     00000TP Generated Offset                     +00000000000000040.72D" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL1031497-----8000---ACAS08GEC 01OFFSETPER     00000GENERATED OFFSET                        +00000000000000040.72C" + formattedRunDate + "          ----------                                                                       "),

        };

        scrub(input);
        assertOriginEntries(7, output);
    }

    /**
     * Tests that the scrubber generates the correct offset entries, even when there are mulitple reversal dates involved
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testOffsetGenerationAcrossMultipleReversalDates() throws Exception {
        final java.sql.Date scrubberDate = new java.sql.Date((SpringContext.getBean(RunDateService.class).calculateRunDate(dateTimeService.getCurrentDate())).getTime());
        final String formattedRunDate = new SimpleDateFormat(DATE_FORMAT).format(scrubberDate);
        final String formattedOffsetDate = new SimpleDateFormat("MMdd").format(scrubberDate);
        final String docTypeOffsetDate = new SimpleDateFormat("MM/dd").format(scrubberDate);

        final int testingYearAsInt = Integer.parseInt(testingYear);
        final String previousTestingYear = new Integer(testingYearAsInt - 1).toString();

        String[] input = new String[] { testingYear + "BA6044913-----1800---ACIN07CR  01OFFSETREV     00000Poplars Garage Fees                                     20.00D" + testingYear + "-01-05          ----------                            " + previousTestingYear + "-01-31                                 ",
                testingYear + "BA6044913-----8000---ACAS07CR  01OFFSETREV     00000TP Generated Offset                                     20.00C" + testingYear + "-01-05          ----------                            " + previousTestingYear + "-02-01                                 " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BA6044913-----1800---ACIN07CR  01OFFSETREV     00000Poplars Garage Fees                                     20.00D" + testingYear + "-01-05          ----------                            " + previousTestingYear + "-01-31                                 "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BA6044913-----8000---ACAS07CR  01OFFSETREV     00000TP Generated Offset                                     20.00C" + testingYear + "-01-05          ----------                            " + previousTestingYear + "-02-01                                 "),

                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BA6044913-----1800---ACIN07CR  01OFFSETREV     00000Poplars Garage Fees                     +00000000000000020.00D" + testingYear + "-01-05          ----------                            " + previousTestingYear + "-01-31                                 "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BA6044913-----8000---ACAS07CR  01OFFSETREV     00000GENERATED OFFSET                        +00000000000000020.00C" + formattedRunDate + "          ----------                            " + previousTestingYear + "-01-31                                 "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BA6044913-----8000---ACAS07CR  01OFFSETREV     00000TP Generated Offset                     +00000000000000020.00C" + testingYear + "-01-05          ----------                            " + previousTestingYear + "-02-01                                 "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BA6044913-----8000---ACAS07CR  01OFFSETREV     00000GENERATED OFFSET                        +00000000000000020.00D" + formattedRunDate + "          ----------                            " + previousTestingYear + "-02-01                                 "),

                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BA6044913-----1800---ACIN07CR  01OFFSETREV     00000Poplars Garage Fees                     +00000000000000020.00D" + testingYear + "-01-05          ----------                            " + previousTestingYear + "-01-31                                 "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BA6044913-----8000---ACAS07CR  01OFFSETREV     00000GENERATED OFFSET                        +00000000000000020.00C" + formattedRunDate + "          ----------                            " + previousTestingYear + "-01-31                                 "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BA6044913-----8000---ACAS07CR  01OFFSETREV     00000TP Generated Offset                     +00000000000000020.00C" + testingYear + "-01-05          ----------                            " + previousTestingYear + "-02-01                                 "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BA6044913-----8000---ACAS07CR  01OFFSETREV     00000GENERATED OFFSET                        +00000000000000020.00D" + formattedRunDate + "          ----------                            " + previousTestingYear + "-02-01                                 "),

        };

        scrub(input);
        assertOriginEntries(7, output);
    }

    /**
     * Tests that the scrubber considers entries with closed accounts to be valid
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testClosedAccount() throws Exception {

        String[] input = new String[] { testingYear + "BA6044909-----1800---ACIN07CR  UBCLOSACCT      00000Poplars Garage Fees                                     20.00C" + testingYear + "-01-05          ----------                                                                               ",
                testingYear + "BA6044909-----8000---ACAS07CR  UBCLOSACCT      00000TP Generated Offset                                     20.00D" + testingYear + "-01-05          ----------                                                                               " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BA6044909-----1800---ACIN07CR  UBCLOSACCT      00000Poplars Garage Fees                                     20.00C" + testingYear + "-01-05          ----------                                                                               "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BA6044909-----8000---ACAS07CR  UBCLOSACCT      00000TP Generated Offset                                     20.00D" + testingYear + "-01-05          ----------                                                                               "),

                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BA6044900-----1800---ACIN07CR  UBCLOSACCT      00000AUTO FR BA6044909Poplars Garage Fees    +00000000000000020.00C" + testingYear + "-01-05          ----------                                                                               "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BA6044900-----8000---ACAS07CR  UBCLOSACCT      00000AUTO FR BA6044909TP Generated Offset    +00000000000000020.00D" + testingYear + "-01-05          ----------                                                                               "),

                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BA6044900-----1800---ACIN07CR  UBCLOSACCT      00000AUTO FR BA6044909Poplars Garage Fees    +00000000000000020.00C" + testingYear + "-01-05          ----------                                                                               "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BA6044900-----8000---ACAS07CR  UBCLOSACCT      00000AUTO FR BA6044909TP Generated Offset    +00000000000000020.00D" + testingYear + "-01-05          ----------                                                                               "),

                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_EXPIRED_OUTPUT_FILE, testingYear + "BA6044900-----1800---ACIN07CR  UBCLOSACCT      00000AUTO FR BA6044909Poplars Garage Fees    +00000000000000020.00C" + testingYear + "-01-05          ----------                                                                               "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_EXPIRED_OUTPUT_FILE, testingYear + "BA6044900-----8000---ACAS07CR  UBCLOSACCT      00000AUTO FR BA6044909TP Generated Offset    +00000000000000020.00D" + testingYear + "-01-05          ----------                                                                               "),

        };

        scrub(input);
        assertOriginEntries(7, output);
    }

    /**
     * Tests that the scrubber considers entries with accounts, expired by the balance type, to be valid
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testExpiredAccountByBalanceType() throws Exception {

        String[] input = new String[] { testingYear + "BL4131407-----4100---EXEX07YEBALGEXPRACTEX     00000CONCERTO OFFICE PRODUCTS                                48.53C" + testingYear + "-01-05          ----------                                      D                                        ",
                testingYear + "BL4131407-----9041---EXLI07YEBALGEXPRACTEX     00000CONCERTO OFFICE PRODUCTS                                48.53D" + testingYear + "-01-05          ----------                                      D                                        " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_EXPIRED_OUTPUT_FILE, testingYear + "BL4131407-----4100---EXEX07YEBALGEXPRACTEX     00000CONCERTO OFFICE PRODUCTS                +00000000000000048.53C" + testingYear + "-01-05          ----------                                      D                                        "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_EXPIRED_OUTPUT_FILE, testingYear + "BL4131407-----9041---EXLI07YEBALGEXPRACTEX     00000CONCERTO OFFICE PRODUCTS                +00000000000000048.53D" + testingYear + "-01-05          ----------                                      D                                        "),

                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BL4131407-----4100---EXEX07YEBALGEXPRACTEX     00000CONCERTO OFFICE PRODUCTS                                48.53C" + testingYear + "-01-05          ----------                                      D                                        "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BL4131407-----9041---EXLI07YEBALGEXPRACTEX     00000CONCERTO OFFICE PRODUCTS                                48.53D" + testingYear + "-01-05          ----------                                      D                                        "),

                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4131407-----4100---EXEX07YEBALGEXPRACTEX     00000CONCERTO OFFICE PRODUCTS                +00000000000000048.53C" + testingYear + "-01-05          ----------                                      D                                        "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4131407-----9041---EXLI07YEBALGEXPRACTEX     00000CONCERTO OFFICE PRODUCTS                +00000000000000048.53D" + testingYear + "-01-05          ----------                                      D                                        "),

                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4131407-----4100---EXEX07YEBALGEXPRACTEX     00000CONCERTO OFFICE PRODUCTS                +00000000000000048.53C" + testingYear + "-01-05          ----------                                      D                                        "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4131407-----9041---EXLI07YEBALGEXPRACTEX     00000CONCERTO OFFICE PRODUCTS                +00000000000000048.53D" + testingYear + "-01-05          ----------                                      D                                        "), };

        scrub(input);
        assertOriginEntries(7, output);
    }

    /**
     * Tests that the scrubber considers entries with expired accounts to be valid
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testExpiredAccount() throws Exception {

        String[] input = new String[] { testingYear + "BL1031467-----5300---ACEE07CR  PDEXPIRACCT     12345214090047 EVERETT J PRESCOTT INC.                     1445.00D" + testingYear + "-01-05ABCDEFGHIJ----------12345678                                                                       ",
                testingYear + "BL1031467-----8000---ACAS07CR  PDEXPIRACCT     12345214090047 EVERETT J PRESCOTT INC.                     1445.00C" + testingYear + "-01-05ABCDEFGHIG----------12345679                                                                       " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_EXPIRED_OUTPUT_FILE, testingYear + "BL2331489-----5300---ACEE07CR  PDEXPIRACCT     12345AUTO FR BL1031467214090047 EVERETT J PRE+00000000000001445.00D" + testingYear + "-01-05ABCDEFGHIJ----------12345678                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_EXPIRED_OUTPUT_FILE, testingYear + "BL2331489-----8000---ACAS07CR  PDEXPIRACCT     12345AUTO FR BL1031467214090047 EVERETT J PRE+00000000000001445.00C" + testingYear + "-01-05ABCDEFGHIG----------12345679                                                                       "),

                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BL1031467-----5300---ACEE07CR  PDEXPIRACCT     12345214090047 EVERETT J PRESCOTT INC.                     1445.00D" + testingYear + "-01-05ABCDEFGHIJ----------12345678                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BL1031467-----8000---ACAS07CR  PDEXPIRACCT     12345214090047 EVERETT J PRESCOTT INC.                     1445.00C" + testingYear + "-01-05ABCDEFGHIG----------12345679                                                                       "),

                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL2331489-----5300---ACEE07CR  PDEXPIRACCT     12345AUTO FR BL1031467214090047 EVERETT J PRE+00000000000001445.00D" + testingYear + "-01-05ABCDEFGHIJ----------12345678                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL2331489-----8000---ACAS07CR  PDEXPIRACCT     12345AUTO FR BL1031467214090047 EVERETT J PRE+00000000000001445.00C" + testingYear + "-01-05ABCDEFGHIG----------12345679                                                                       "),

                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL2331489-----5300---ACEE07CR  PDEXPIRACCT     12345AUTO FR BL1031467214090047 EVERETT J PRE+00000000000001445.00D" + testingYear + "-01-05ABCDEFGHIJ----------12345678                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL2331489-----8000---ACAS07CR  PDEXPIRACCT     12345AUTO FR BL1031467214090047 EVERETT J PRE+00000000000001445.00C" + testingYear + "-01-05ABCDEFGHIG----------12345679                                                                       "), };

        scrub(input);
        assertOriginEntries(7, output);
    }

    // ************************************************************** Tests for error conditions below.

    /**
     * Tests that the scrubber considers invalid encumbrance update codes to be errors
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testInvalidEncumbranceUpdateCode() throws Exception {
        final int testingYearAsInt = Integer.parseInt(testingYear);
        final String previousTestingYear = new Integer(testingYearAsInt - 1).toString();

        String[] inputTransactions = { testingYear + "BL1031420-----4110---IEEX07PAYEEUINVALENCC     00000NOV-05 IMU Business Office          2224               241.75C" + previousTestingYear + "-11-30          ----------                                      X                                        ",
                testingYear + "BL1031420-----9892---IEAS07PAYEEUINVALENCC     00000NOV-05 IMU Business Office          2237               241.75D" + previousTestingYear + "-11-30          ----------                                      X                                        " };

        EntryHolder[] outputTransactions = { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[1]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[1]),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, inputTransactions[1]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE, inputTransactions[1]) };

        scrub(inputTransactions);
        assertOriginEntries(7, outputTransactions);
    }

    /**
     * Tests that the scrubber considers entries with blank reference numbers but an encumbrance update code that requires a
     * reference document to be errors
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testBlankReferenceDocumentNumberWithEncumbranceUpdateCodeOfR() throws Exception {

        String[] inputTransactions = { testingYear + "BA6044900-----1599---EXIN07TOPSLGBLANKRDOC     00000CONCERTO OFFICE PRODUCTS                                48.53C" + testingYear + "-01-05          ----------        CR  01                   R                                        ",
                testingYear + "BA6044900-----9041---EXLI07TOPSLDBLANKRDOC     00000CONCERTO OFFICE PRODUCTS                                48.53D" + testingYear + "-01-05          ----------        CR  01                   R                                        " };

        EntryHolder[] outputTransactions = { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[1]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[1]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[0]),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, inputTransactions[1]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE, inputTransactions[1]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE, inputTransactions[0]) };

        scrub(inputTransactions);
        assertOriginEntries(7, outputTransactions);
    }

    /**
     * Tests that the scrubber considers entries with a document number present but no other document data to be errors
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testReferenceDocumentNumberPresentWithoutOtherFields() throws Exception {

        String[] inputTransactions = { testingYear + "BA6044906-----5300---ACEE07CHKDPDLONERDOC      12345TEST KUALI SCRUBBER EDITS                             1445.00D" + testingYear + "-01-05ABCDEFGHIJ----------12345678      123456789                                                        ",
                testingYear + "BA6044906-----8000---ACAS07CHKDPDLONERDOC      12345TEST KUALI SCRUBBER EDITS                             1445.00C" + testingYear + "-01-05ABCDEFGHIG----------12345678      123456789                                                        " };

        EntryHolder[] outputTransactions = { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[1]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[1]),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, inputTransactions[1]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE, inputTransactions[1]) };

        scrub(inputTransactions);
        assertOriginEntries(7, outputTransactions);
    }

    /**
     * Tests that the scrubber considers blank reference origin codes, in an entry with the encumbrance update code requiring
     * reference documents, to be errors
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testBlankReferenceOriginCodeWithEncumbranceUpdateCodeOfR() throws Exception {

        String[] inputTransactions = { testingYear + "BL9120656-----5000---ACEX07INV EUBLANKRORG     00000BALDWIN WALLACE COLLEGE                               3375.00C" + testingYear + "-01-05          ----------        DI    123456789                                                        ",
                testingYear + "BL9120656-----8000---ACAS07INV EUBLANKRORG     00000TP Generated Offset                                   3375.00D" + testingYear + "-01-05          ----------        DI    123456789                                                        " };

        EntryHolder[] outputTransactions = { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[1]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[1]),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, inputTransactions[1]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE, inputTransactions[1]) };

        scrub(inputTransactions);
        assertOriginEntries(7, outputTransactions);
    }

    /**
     * Tests that the scrubber considers invalid reference origin codes to be errors
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testInvalidReferenceOriginCode() throws Exception {

        String[] inputTransactions = { testingYear + "BL2231411-----2400---ACEX07ST  EUINVALRORG     00000PAYROLL EXPENSE TRANSFERS                              620.00C" + testingYear + "-01-05          ----------        CD  XX123456789                                                        ",
                testingYear + "BL2231411-----8000---ACAS07ST  EUINVALRORG     00000PAYROLL EXPENSE TRANSFERS                              620.00D" + testingYear + "-01-05          ----------        CD  XX123456789                                                        " };

        EntryHolder[] outputTransactions = { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[1]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[1]),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, inputTransactions[1]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE, inputTransactions[1]) };

        scrub(inputTransactions);
        assertOriginEntries(7, outputTransactions);
    }

    /**
     * Tests that the scrubber considers blank reference document types, in an entry with the encumbrance update code that requiring
     * reference documents, to be errors
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testBlankReferenceDocumentTypeWithEncumbranceUpdateCodeOfR() throws Exception {

        String[] inputTransactions = { testingYear + "BL2231408-----4035---ACEX07SB  01BLANKRDTP     00000Biology Stockroom                                       13.77D" + testingYear + "-01-05          ----------            LG123456789                                                        ",
                testingYear + "BL2231408-----8000---ACAS07SB  01BLANKRDTP     00000TP Generated Offset                                     13.77C" + testingYear + "-01-05          ----------            LG123456789                                                        " };

        EntryHolder[] outputTransactions = { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[1]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[1]),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, inputTransactions[1]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE, inputTransactions[1]) };

        scrub(inputTransactions);
        assertOriginEntries(7, outputTransactions);
    }

    /**
     * Tests that the scrubber considers invalid reference document types to be errors
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testInvalidReferenceDocumentType() throws Exception {
        String[] inputTransactions = { testingYear + "BL1031497-----4190---ACEX07GEC 01INVALRDTP     00000THOMAS BUSEY/NEWEGG COMPUTERS                           40.72C" + testingYear + "-01-05          ----------        XXXXLG123456789                                                        ",
                testingYear + "BL1031497-----8000---ACAS07GEC 01INVALRDTP     00000TP Generated Offset                                     40.72D" + testingYear + "-01-05          ----------        XXXXLG123456789                                                        " };

        EntryHolder[] outputTransactions = { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[1]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[1]),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, inputTransactions[1]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE, inputTransactions[1]) };

        scrub(inputTransactions);
        assertOriginEntries(7, outputTransactions);
    }

    /**
     * Tests that the scrubber considers invalid project codes to be errors
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testInvalidProjectCode() throws Exception {
        String[] inputTransactions = { testingYear + "BL9120656-----4035---ACEX07CR  01INVALPROJ     00000pymts recd 12/28/05                                     25.15C" + testingYear + "-01-05          XXXXXXXXX                                                                                ",
                testingYear + "BL9120656-----8000---ACAS07CR  01INVALPROJ     00000TP Generated Offset                                     25.15D" + testingYear + "-01-05          XXXXXXXXX                                                                                " };

        EntryHolder[] outputTransactions = { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[1]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[1]),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, inputTransactions[1]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE, inputTransactions[1]) };

        scrub(inputTransactions);
        assertOriginEntries(7, outputTransactions);
    }

    /**
     * Tests that the scrubber considers invalid transaction dates to be errors.
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testInvalidTransactionDate() throws Exception {
        String[] inputTransactions = { testingYear + "BL1031497-----4100---ACEX07DI  LGINVALDATE     00000Rite Quality Office Supplies Inc.                       43.42D2096-02-11          ----------                                                                               ",
                testingYear + "BL1031497-----9892---ACFB07DI  LGINVALDATE     00000Rite Quality Office Supplies Inc.                       43.42C1006-12-23          ----------                                                                               " };

        // A change to ScrubberValidatorImp.validateTransactionDate() for KFSMI-5441 changed the game on this test. It is now fixing the date
        // by setting it equal to the current date. This test was not updated, and has been broken for a while. I am going to fix the test
        // by updating the expected results.

        DateFormat df = new SimpleDateFormat(DATE_FORMAT);
        String strToday = df.format(dateTimeService.getCurrentDate());

        String[] fixedTransactionDatesAndAmounts = { testingYear + "BL1031497-----4100---ACEX07DI  LGINVALDATE     00000Rite Quality Office Supplies Inc.       +00000000000000043.42D" + strToday + "          ----------                                                                               ",
                testingYear + "BL1031497-----9892---ACFB07DI  LGINVALDATE     00000Rite Quality Office Supplies Inc.       +00000000000000043.42C" + strToday + "          ----------                                                                               " };

        EntryHolder[] outputTransactions = { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[0]), //
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[1]), //
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, fixedTransactionDatesAndAmounts[0]), //
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, fixedTransactionDatesAndAmounts[1]), //
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, fixedTransactionDatesAndAmounts[0]), //
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, fixedTransactionDatesAndAmounts[1]) };

        // there were garbage files in the batch directory left over that were destroying the validity of this and other tests... clean them up
        cleanupBatchDirectory();
        scrub(inputTransactions);
        assertOriginEntries(7, outputTransactions);
        cleanupBatchDirectory();

    }

    /**
     * Tests that the scrubber considers invalid debit/credit codes to be errors
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testInvalidDebitCreditCode() throws Exception {
        final int testingYearAsInt = Integer.parseInt(testingYear);
        final String previousTestingYear = new Integer(testingYearAsInt - 1).toString();

        String[] inputTransactions = { testingYear + "BL1031420-----4110---ACEX07DI  EUINVALDBCR     00000NOV-05 IMU Business Office          2224               241.75X" + previousTestingYear + "-11-30          ----------                                                                               ",
                testingYear + "BL1031420-----8000---ACAS07DI  EUINVALDBCR     00000NOV-05 IMU Business Office          2237               241.75X" + previousTestingYear + "-11-30          ----------                                                                               " };

        EntryHolder[] outputTransactions = { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[1]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[1]),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, inputTransactions[1]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE, inputTransactions[1]) };

        scrub(inputTransactions);
        assertOriginEntries(7, outputTransactions);
    }

    /**
     * Tests that the scrubber considers non blank debit/credit codes on entries not requiring offsets to be errors
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testDebitCreditCodeOnTransactionNotRequiringOffset() throws Exception {
        String[] inputTransactions = { testingYear + "BL1031400-----4100---MBEX07BA  01WRONGDBCR     00000Rite Quality Office Supplies Inc.                       94.35D" + testingYear + "-01-05          ----------                                                                               ",
                testingYear + "BL1031400-----1800---MBLI07BA  01WRONGDBCR     00000Rite Quality Office Supplies Inc.                       94.35C" + testingYear + "-01-05          ----------                                                                               " };

        EntryHolder[] outputTransactions = { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[1]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[1]),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, inputTransactions[1]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE, inputTransactions[1]) };

        scrub(inputTransactions);
        assertOriginEntries(7, outputTransactions);
    }

    /**
     * Tests that the scrubber considers blank debit/credit codes on entries requiring offsets to be errors
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testBlankDebitCreditCodeOnTransactionRequiringOffset() throws Exception {
        String[] inputTransactions = { testingYear + "BA6044913-----1470---ACIN07CR  01BLANKDBCR     00000Poplars Garage Fees                                     20.00 " + testingYear + "-01-05          ----------                                                                               ",
                testingYear + "BA6044913-----8000---ACAS07CR  01BLANKDBCR     00000TP Generated Offset                                     20.00 " + testingYear + "-01-05          ----------                                                                               " };
        EntryHolder[] outputTransactions = { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[1]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[1]),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, inputTransactions[1]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE, inputTransactions[1]) };

        scrub(inputTransactions);
        assertOriginEntries(7, outputTransactions);
    }

    /**
     * Tests that the scrubber considers blank document numbers to be errors
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testBlankDocumentNumber() throws Exception {
        String[] inputTransactions = { testingYear + "BL2231423-----1800---ACIN  CR  PL              00000FRICKA FRACKA                                        45995.84C" + testingYear + "-01-05          ----------                                                                               ",
                testingYear + "BL2231423-----8000---ACAS  CR  PL              00000TP Generated Offset                                  45995.84D" + testingYear + "-01-05          ----------                                                                               " };

        EntryHolder[] outputTransactions = { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[1]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[1]),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, inputTransactions[1]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE, inputTransactions[1]) };

        scrub(inputTransactions);
        assertOriginEntries(7, outputTransactions);
    }

    /**
     * Tests that the scrubber considers invalid origin codes to be errors
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testInvalidOriginCode() throws Exception {
        String[] inputTransactions = { testingYear + "BA9120656-----5000---ACEX07INV XXINVALORIG     00000BALDWIN WALLACE COLLEGE                               3375.00C" + testingYear + "-01-05          ----------                                                                               ", };

        EntryHolder[] outputTransactions = { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE, inputTransactions[0]) };

        scrub(inputTransactions);
        assertOriginEntries(7, outputTransactions);
    }

    /**
     * Tests that the scrubber considers blank origin codes to be errors
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testBlankOriginCode() throws Exception {

        String[] inputTransactions = { testingYear + "BL2231411-----2400---ACEX07ST    BLANKORIG     00000PAYROLL EXPENSE TRANSFERS                              620.00C" + testingYear + "-01-05          ----------                                                                               ", };

        EntryHolder[] outputTransactions = { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE, inputTransactions[0]) };

        scrub(inputTransactions);
        assertOriginEntries(7, outputTransactions);
    }

    /**
     * Tests that the scrubber considers invalid document types to be errors
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testInvalidDocumentType() throws Exception {
        String[] inputTransactions = { testingYear + "BL2231408-----4035---ACEX07XXX 01INVALDTYP     00000Biology Stockroom                                       13.77D" + testingYear + "-01-05          ----------                                                                               ",
                testingYear + "BL2231408-----8000---ACAS07XXX 01INVALDTYP     00000TP Generated Offset                                     13.77C" + testingYear + "-01-05          ----------                                                                               " };

        EntryHolder[] outputTransactions = { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[1]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[1]),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, inputTransactions[1]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE, inputTransactions[1]) };

        scrub(inputTransactions);
        assertOriginEntries(7, outputTransactions);
    }

    /**
     * Tests that the scrubber considers blank document types to be errors
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testBlankDocumentType() throws Exception {
        String[] inputTransactions = { testingYear + "BA6044900-----8000---ACAS07    01BLANKDTYP     00000TP Generated Offset                                   1650.00C" + testingYear + "-01-05          ----------                                                                               ",
                testingYear + "BL6044900-----4866---ACEX07    01BLANKDTYP     00000Correction to: 01-PU3355206                           1650.00D" + testingYear + "-01-05          ----------                                                                               " };

        EntryHolder[] outputTransactions = { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[1]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[1]),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, inputTransactions[1]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE, inputTransactions[1]) };

        scrub(inputTransactions);
        assertOriginEntries(7, outputTransactions);
    }

    /**
     * Tests that the scrubber considers invalid fiscal periods to be errors
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testInvalidFiscalPeriod() throws Exception {
        String[] inputTransactions = { testingYear + "BL1031497-----4190---ACEX14GEC 01INVALPER      00000THOMAS BUSEY/NEWEGG COMPUTERS                           40.72C" + testingYear + "-01-05          ----------                                                                               ",
                testingYear + "BL1031497-----8000---ACASXXGEC 01INVALPER      00000TP Generated Offset                                     40.72D" + testingYear + "-01-05          ----------                                                                               " };

        EntryHolder[] outputTransactions = { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[1]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[1]),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, inputTransactions[1]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE, inputTransactions[1]) };

        scrub(inputTransactions);
        assertOriginEntries(7, outputTransactions);
    }

    /**
     * Tests that the scrubber considers closed fiscal periods to be errors
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testClosedFiscalPeriod() throws Exception {
        String[] inputTransactions = { "2003BA9120656-----4035---ACEX01CR  01CLOSEPER      00000pymts recd 12/28/05                                     25.15C" + testingYear + "-01-05          ----------                                                                               ",
                "2003BA9120656-----8000---ACAS01CR  01CLOSEPER      00000TP Generated Offset                                     25.15D" + testingYear + "-01-05          ----------                                                                               " };

        EntryHolder[] outputTransactions = { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[1]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[1]),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, inputTransactions[1]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE, inputTransactions[1]) };

        scrub(inputTransactions);
        assertOriginEntries(7, outputTransactions);
    }

    /**
     * Tests that the scrubber considers invalid object type codes to be errors
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testInvalidObjectType() throws Exception {
        String[] inputTransactions = {
                testingYear + "BL1031400-----4100---EXXX07DI  LGINVALOBTY     00000Rite Quality Office Supplies Inc.                       43.42D" + testingYear + "-01-05          ----------                                                                               ",
                testingYear + "BL1031400-----9892---EXFB07DI  LGINVALOBTY     00000Rite Quality Office Supplies Inc.                       43.42C" + testingYear + "-01-05          ----------                                                                               " };

        EntryHolder[] outputTransactions = {
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[0]),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[1]),

                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[0]),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, testingYear + "BL1031400-----9892---EXFB07DI  LGINVALOBTY     00000GENERATED OFFSET                        +00000000000000043.42C" + testingYear + "-01-05          ----------                                       "),

                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL1031400-----9892---EXFB07DI  LGINVALOBTY     00000Rite Quality Office Supplies Inc.       +00000000000000043.42C" + testingYear + "-01-05          ----------                                       "),

                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, inputTransactions[0]),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, testingYear + "BL1031400-----9892---EXFB07DI  LGINVALOBTY     00000Rite Quality Office Supplies Inc.       +00000000000000043.42C" + testingYear + "-01-05          ----------                                       "),

                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE, inputTransactions[0]),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE, testingYear + "BL1031400-----9892---EXFB07DI  LGINVALOBTY     00000GENERATED OFFSET                        +00000000000000043.42C" + testingYear + "-01-05          ----------                                       ")
                };

        scrub(inputTransactions);
        assertOriginEntries(7, outputTransactions);
    }

    /**
     * Tests that the scrubber considers invalid balance type codes to be errors
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testInvalidBalanceType() throws Exception {
        final int testingYearAsInt = Integer.parseInt(testingYear);
        final String previousTestingYear = new Integer(testingYearAsInt - 1).toString();

        final java.sql.Date scrubberDate = new java.sql.Date((SpringContext.getBean(RunDateService.class).calculateRunDate(dateTimeService.getCurrentDate())).getTime());
        final String formattedRunDate = new SimpleDateFormat(DATE_FORMAT).format(scrubberDate);

        String[] inputTransactions = { testingYear + "BL1031420-----4110---XXEX07DI  EUINVALBALT     00000NOV-05 IMU Business Office          2224               241.75D" + previousTestingYear + "-11-30          ----------                                                                               ",
                testingYear + "BL1031420-----8000---ACAS07DI  EUINVALBALT     00000NOV-05 IMU Business Office          2237               241.75C" + previousTestingYear + "-11-30          ----------                                                                               " };

        EntryHolder[] outputTransactions = { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[1]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[0]),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL1031420-----8000---ACAS07DI  EUINVALBALT     00000GENERATED OFFSET                        +00000000000000241.75D" + formattedRunDate + "          ----------                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL1031420-----8000---ACAS07DI  EUINVALBALT     00000NOV-05 IMU Business Office          2237+00000000000000241.75C" + previousTestingYear + "-11-30          ----------                                       "), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, inputTransactions[0]),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, testingYear + "BL1031420-----8000---ACAS07DI  EUINVALBALT     00000NOV-05 IMU Business Office          2237+00000000000000241.75C" + previousTestingYear + "-11-30          ----------                                       "), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE, inputTransactions[0]) };

        scrub(inputTransactions);
        assertOriginEntries(7, outputTransactions);
    }

    /**
     * Tests that the scrubber considers invalid financial object codes to be errors
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void PATCHFIX_testInvalidObjectCode() throws Exception {
        String[] inputTransactions = { testingYear + "BL2231423-----XXXX---ACIN  CR  PLINVALOBJ      00000FRICKA FRACKA                                        45995.84C" + testingYear + "-01-05          ----------                                                                               ",
                testingYear + "BL2231423-----8000---ACAS  CR  PLINVALOBJ      00000TP Generated Offset                                  45995.84D" + testingYear + "-01-05          ----------                                                                               " };

        final java.sql.Date scrubberDate = new java.sql.Date((SpringContext.getBean(RunDateService.class).calculateRunDate(dateTimeService.getCurrentDate())).getTime());
        final String formattedRunDate = new SimpleDateFormat(DATE_FORMAT).format(scrubberDate);

        EntryHolder[] outputTransactions = { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[1]),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, testingYear + "BL2231423-----8000---ACAS04CR  PLINVALOBJ      00000TP Generated Offset                     +00000000000045995.84D" + testingYear + "-01-05          ----------                                       "), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, inputTransactions[0]),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL2231423-----8000---ACAS04CR  PLINVALOBJ      00000GENERATED OFFSET                        +00000000000045995.84C" + formattedRunDate + "          ----------                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL2231423-----8000---ACAS04CR  PLINVALOBJ      00000TP Generated Offset                     +00000000000045995.84D" + testingYear + "-01-05          ----------                                       "), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[0]),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE, inputTransactions[0]) };

        scrub(inputTransactions);
        assertOriginEntries(7, outputTransactions);
    }

    /**
     * Tests that the scrubber considers entries with invalid sub accounts to be errors
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testInvalidSubAccountNumber() throws Exception {
        String[] inputTransactions = { testingYear + "BL2231408XXXX 4035---ACEX07SB  01INVALSACT     00000Biology Stockroom                                       13.77D" + testingYear + "-01-05          ----------                                                                               ",
                testingYear + "BL2231408XXXX 8000---ACAS07SB  01INVALSACT     00000TP Generated Offset                                     13.77C" + testingYear + "-01-05          ----------                                                                               " };

        EntryHolder[] outputTransactions = { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[1]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[1]),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, inputTransactions[1]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE, inputTransactions[1]) };

        scrub(inputTransactions);
        assertOriginEntries(7, outputTransactions);
    }

    /**
     * Tests that the scrubber considers entries with inactive sub accounts to be errors
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testInactiveSubAccountNumber() throws Exception {
        String[] inputTransactions = { testingYear + "BA6044900ARREC8000---ACAS07IB  01INACTSACT     00000TP Generated Offset                                   1650.00C" + testingYear + "-01-05          ----------                                                                               ",
                testingYear + "BL6044900ARREC4866---ACEX07IB  01INACTSACT     00000Correction to: 01-PU3355206                           1650.00D" + testingYear + "-01-05          ----------                                                                               " };

        EntryHolder[] outputTransactions = { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[1]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[1]),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, inputTransactions[1]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE, inputTransactions[1]) };

        scrub(inputTransactions);
        assertOriginEntries(7, outputTransactions);
    }

    /**
     * Tests that the scrubber considers invalid account numbers to be errors
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testInvalidAccountNumber() throws Exception {
        String[] inputTransactions = { testingYear + "EA1234567-----4035---ACEX07CR  01INVALACCT     00000pymts recd 12/28/05                                     25.15C" + testingYear + "-01-05          ----------                                                                               ",
                testingYear + "EA1234567-----8000---ACAS07CR  01INVALACCT     00000TP Generated Offset                                     25.15D" + testingYear + "-01-05          ----------                                                                               " };

        EntryHolder[] outputTransactions = { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[1]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[1]),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, inputTransactions[1]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE, inputTransactions[1]) };

        scrub(inputTransactions);
        assertOriginEntries(7, outputTransactions);
    }

    /**
     * Tests that the scrubber considers blank account numbers to be errors
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testBlankAccountNumber() throws Exception {
        String[] inputTransactions = { testingYear + "IN       -----5000---ACEX07PO  LGBLANKACCT     00000225050007 WILLIAMS DOTSON ASSOCIATES IN               1200.00D" + testingYear + "-01-05          ----------                                                                               ",
                testingYear + "IN       -----9041---ACLI07PO  LGBLANKACCT     00000225050007 WILLIAMS DOTSON ASSOCIATES IN               1200.00C" + testingYear + "-01-05          ----------                                                                               " };

        EntryHolder[] outputTransactions = { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[1]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[1]),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, inputTransactions[1]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE, inputTransactions[1]) };

        scrub(inputTransactions);
        assertOriginEntries(7, outputTransactions);
    }

    /**
     * Tests that the scrubber considers invalid charts to be errors
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testInvalidChart() throws Exception {
        final int testingYearAsInt = Integer.parseInt(testingYear);
        final String previousTestingYear = new Integer(testingYearAsInt - 1).toString();

        String[] inputTransactions = { testingYear + "XX1031420-----4110---ACEX07DI  EUINVALCHAR     00000NOV-05 IMU Business Office          2224               241.75D" + previousTestingYear + "-11-30          ----------                                                                               ",
                testingYear + "XX1031420-----8000---ACAS07DI  EUINVALCHAR     00000NOV-05 IMU Business Office          2237               241.75C" + previousTestingYear + "-11-30          ----------                                                                               " };

        EntryHolder[] outputTransactions = { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[1]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[1]),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, inputTransactions[1]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE, inputTransactions[1]) };

        scrub(inputTransactions);
        assertOriginEntries(7, outputTransactions);
    }

    /**
     * Tests that the scrubber considers invalid fiscal years to be errors. Note: this test will malfunction sometime in the year
     * 2019
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testInvalidFiscalYear() throws Exception {
        String[] inputTransactions = { "2020BA6044913-----1470---ACIN07CR  01INVALFISC     00000Poplars Garage Fees                                     20.00C" + testingYear + "-01-05          ----------                                                                               ",
                "2020BA6044913-----8000---ACAS07CR  01INVALFISC     00000TP Generated Offset                                     20.00D" + testingYear + "-01-05          ----------                                                                               " };

        EntryHolder[] outputTransactions = { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[1]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[1]),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, inputTransactions[1]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE, inputTransactions[1]) };

        scrub(inputTransactions);
        assertOriginEntries(7, outputTransactions);
    }

    /**
     * Entry with a closed fiscal period/year. These transactions should be marked as errors.
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testClosedFiscalYear() throws Exception {

        String[] inputTransactions = { "2003BA6044906-----4100---ACEX07TOPSLGCLOSEFISC     00000CONCERTO OFFICE PRODUCTS                                48.53C" + testingYear + "-01-05          ----------                                                                               ",
                "2003BA6044906-----9041---ACLI07TOPSLGCLOSEFISC     00000CONCERTO OFFICE PRODUCTS                                48.53D" + testingYear + "-01-05          ----------                                                                               " };

        EntryHolder[] outputTransactions = { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[1]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[1]),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, inputTransactions[1]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE, inputTransactions[1]) };

        scrub(inputTransactions);
        assertOriginEntries(7, outputTransactions);

    }

    /**
     * Entry with a null fiscal year. The fiscal year should be replaced with the default fiscal year. They should not be errors.
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testDefaultFiscalYear() throws Exception {

        String[] inputTransactions = { "    BA6044900-----5300---ACEE07CHKDPDBLANKFISC     12345214090047 EVERETT J PRESCOTT INC.                     1445.00D" + testingYear + "-01-05ABCDEFGHIJ----------12345678                                                                       ",
                "    BA6044900-----8000---ACAS07CHKDPDBLANKFISC     12345214090047 EVERETT J PRESCOTT INC.                     1445.00C" + testingYear + "-01-05ABCDEFGHIG----------12345678                                                                       " };

        final AccountingPeriod currentAccountingPeriod = SpringContext.getBean(AccountingPeriodService.class).getByDate(dateTimeService.getCurrentSqlDate());

        EntryHolder[] outputTransactions = { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[1]),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BA6044900-----5300---ACEE" + currentAccountingPeriod.getUniversityFiscalPeriodCode() + "CHKDPDBLANKFISC     12345214090047 EVERETT J PRESCOTT INC.       +00000000000001445.00D" + testingYear + "-01-05ABCDEFGHIJ----------12345678                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BA6044900-----8000---ACAS" + currentAccountingPeriod.getUniversityFiscalPeriodCode() + "CHKDPDBLANKFISC     12345214090047 EVERETT J PRESCOTT INC.       +00000000000001445.00C" + testingYear + "-01-05ABCDEFGHIG----------12345678                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BA6044900-----5300---ACEE" + currentAccountingPeriod.getUniversityFiscalPeriodCode() + "CHKDPDBLANKFISC     12345214090047 EVERETT J PRESCOTT INC.       +00000000000001445.00D" + testingYear + "-01-05ABCDEFGHIJ----------12345678                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BA6044900-----8000---ACAS" + currentAccountingPeriod.getUniversityFiscalPeriodCode() + "CHKDPDBLANKFISC     12345214090047 EVERETT J PRESCOTT INC.       +00000000000001445.00C" + testingYear + "-01-05ABCDEFGHIG----------12345678                                                                       ") };

        scrub(inputTransactions);
        assertOriginEntries(7, outputTransactions);
    }

    /**
     * Tests that the scrubber considers entries with inactive balance types to be errors
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testInactiveBalanceType() throws Exception {
        Map<String, Object> primaryKeys = new HashMap<String, Object>();
        primaryKeys.put(KFSPropertyConstants.CODE, "AC");
        this.deactivate(BalanceType.class, primaryKeys);

        final int testingYearAsInt = Integer.parseInt(testingYear);
        final String previousTestingYear = new Integer(testingYearAsInt - 1).toString();

        String[] inputTransactions = { testingYear + "BL1031420-----4110---ACEX07DI  EUINVALBALT     00000NOV-05 IMU Business Office          2224               241.75D" + previousTestingYear + "-11-30          ----------                                                                               ",
                testingYear + "BL1031420-----8000---ACAS07DI  EUINVALBALT     00000NOV-05 IMU Business Office          2237               241.75C" + previousTestingYear + "-11-30          ----------                                                                               " };

        EntryHolder[] outputTransactions = { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[1]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[1]),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, inputTransactions[1]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE, inputTransactions[1]) };

        scrub(inputTransactions);
        assertOriginEntries(7, outputTransactions);
    }

    /**
     * Tests that the scrubber considers entries with inactive origination codes to be errors
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testInactiveOriginCode() throws Exception {
        Map<String, Object> primaryKeys = new HashMap<String, Object>();
        primaryKeys.put(KFSPropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE, "EU");
        this.deactivate(OriginationCode.class, primaryKeys);

        final int testingYearAsInt = Integer.parseInt(testingYear);
        final String previousTestingYear = new Integer(testingYearAsInt - 1).toString();

        String[] inputTransactions = { testingYear + "BL1031420-----4110---ACEX07DI  EUINVALBALT     00000NOV-05 IMU Business Office          2224               241.75D" + previousTestingYear + "-11-30          ----------                                                                               ",
                testingYear + "BL1031420-----8000---ACAS07DI  EUINVALBALT     00000NOV-05 IMU Business Office          2237               241.75C" + previousTestingYear + "-11-30          ----------                                                                               " };

        EntryHolder[] outputTransactions = { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[1]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[1]),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, inputTransactions[1]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE, inputTransactions[1]) };

        scrub(inputTransactions);
        assertOriginEntries(7, outputTransactions);
    }

    /**
     * Tests that the scrubber considers entries with inactive origination codes to be errors
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testInactiveObjectCode() throws Exception {
        Map<String, Object> primaryKeys = new HashMap<String, Object>();
        primaryKeys.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, testingYear + "");
        primaryKeys.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, "BL");
        primaryKeys.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, "4110");
        this.deactivate(ObjectCode.class, primaryKeys);

        final int testingYearAsInt = Integer.parseInt(testingYear);
        final String previousTestingYear = new Integer(testingYearAsInt - 1).toString();

        String[] inputTransactions = { testingYear + "BL1031420-----4110---ACEX07DI  EUINVALBALT     00000NOV-05 IMU Business Office          2224               241.75D" + previousTestingYear + "-11-30          ----------                                                                               ",
                testingYear + "BL1031420-----8000---ACAS07DI  EUINVALBALT     00000NOV-05 IMU Business Office          2237               241.75C" + previousTestingYear + "-11-30          ----------                                                                               " };

        final java.sql.Date scrubberDate = new java.sql.Date((SpringContext.getBean(RunDateService.class).calculateRunDate(dateTimeService.getCurrentDate())).getTime());
        final String formattedRunDate = new SimpleDateFormat(DATE_FORMAT).format(scrubberDate);

        EntryHolder[] outputTransactions = { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[1]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[0]),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL1031420-----8000---ACAS07DI  EUINVALBALT     00000GENERATED OFFSET                        +00000000000000241.75D" + formattedRunDate + "          ----------                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL1031420-----8000---ACAS07DI  EUINVALBALT     00000NOV-05 IMU Business Office          2237+00000000000000241.75C" + previousTestingYear + "-11-30          ----------                                       "), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, inputTransactions[0]),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, testingYear + "BL1031420-----8000---ACAS07DI  EUINVALBALT     00000NOV-05 IMU Business Office          2237+00000000000000241.75C" + previousTestingYear + "-11-30          ----------                                       "), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE, inputTransactions[0]) };

        scrub(inputTransactions);
        assertOriginEntries(7, outputTransactions);
    }

    /**
     * Tests that the scrubber considers entries with inactive origination codes to be errors
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testInactiveObjectType() throws Exception {
        Map<String, Object> primaryKeys = new HashMap<String, Object>();
        primaryKeys.put(KFSPropertyConstants.CODE, "EX");
        this.deactivate(ObjectType.class, primaryKeys);

        final int testingYearAsInt = Integer.parseInt(testingYear);
        final String previousTestingYear = new Integer(testingYearAsInt - 1).toString();

        String[] inputTransactions = { testingYear + "BL1031420-----4110---ACEX07DI  EUINVALBALT     00000NOV-05 IMU Business Office          2224               241.75D" + previousTestingYear + "-11-30          ----------                                                                               ",
                testingYear + "BL1031420-----8000---ACAS07DI  EUINVALBALT     00000NOV-05 IMU Business Office          2237               241.75C" + previousTestingYear + "-11-30          ----------                                                                               " };

        final java.sql.Date scrubberDate = new java.sql.Date((SpringContext.getBean(RunDateService.class).calculateRunDate(dateTimeService.getCurrentDate())).getTime());
        final String formattedRunDate = new SimpleDateFormat(DATE_FORMAT).format(scrubberDate);

        EntryHolder[] outputTransactions = { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[0]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[1]), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[0]),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL1031420-----8000---ACAS07DI  EUINVALBALT     00000GENERATED OFFSET                        +00000000000000241.75D" + formattedRunDate + "          ----------                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL1031420-----8000---ACAS07DI  EUINVALBALT     00000NOV-05 IMU Business Office          2237+00000000000000241.75C" + previousTestingYear + "-11-30          ----------                                       "), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, inputTransactions[0]),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE, testingYear + "BL1031420-----8000---ACAS07DI  EUINVALBALT     00000NOV-05 IMU Business Office          2237+00000000000000241.75C" + previousTestingYear + "-11-30          ----------                                       "), new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE, inputTransactions[0]) };

        scrub(inputTransactions);
        assertOriginEntries(7, outputTransactions);
    }

    /**
     * Tests that the scrubber does not generate cost share entries for entries that are beginning balance transactions
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testNoCostShareTransfersForBeginningBalanceTransactions() throws Exception {

        String[] input = new String[] { testingYear + "BL4831496CS0015000---ACEXCBCR  01NOCSHRCB      00000Poplars Garage Fees                                     20.00C" + testingYear + "-01-05          ----------                                                                       ",
                testingYear + "BL4831496CS0018000---ACASCBCR  01NOCSHRCB      00000TP Generated Offset                                     20.00D" + testingYear + "-01-05          ----------                                                                       " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BL4831496CS0015000---ACEXCBCR  01NOCSHRCB      00000Poplars Garage Fees                                     20.00C" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BL4831496CS0018000---ACASCBCR  01NOCSHRCB      00000TP Generated Offset                                     20.00D" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4831496CS0015000---ACEXCBCR  01NOCSHRCB      00000Poplars Garage Fees                     +00000000000000020.00C" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL4831496CS0018000---ACASCBCR  01NOCSHRCB      00000TP Generated Offset                     +00000000000000020.00D" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4831496CS0015000---ACEXCBCR  01NOCSHRCB      00000Poplars Garage Fees                     +00000000000000020.00C" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL4831496CS0018000---ACASCBCR  01NOCSHRCB      00000TP Generated Offset                     +00000000000000020.00D" + testingYear + "-01-05          ----------                                                                       ") };

        scrub(input);
        assertOriginEntries(7, output);
    }

    /**
     * Tests that the scrubber does not generate a plant endebtedness entry for entries with financial sub object == "P2"
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testNoIndebtednessForObjectSubTypeP2() throws Exception {

        String[] input = new String[] { testingYear + "BA9120657-----9100---ACLI07DI  EUNODEBTP2      00000BALDWIN WALLACE COLLEGE                               3375.00C" + testingYear + "-01-05          ----------                                                                       ",
                testingYear + "BA9120657-----8000---ACAS07DI  EUNODEBTP2      00000TP Generated Offset                                   3375.00D" + testingYear + "-01-05          ----------                                                                       " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BA9120657-----9100---ACLI07DI  EUNODEBTP2      00000BALDWIN WALLACE COLLEGE                               3375.00C" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BA9120657-----8000---ACAS07DI  EUNODEBTP2      00000TP Generated Offset                                   3375.00D" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BA9120657-----8000---ACAS07DI  EUNODEBTP2      00000TP Generated Offset                     +00000000000003375.00D" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BA9120657-----9100---ACLI07DI  EUNODEBTP2      00000GENERATED TRANSFER TO NET PLANT         +00000000000003375.00D" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BA9120657-----9899---ACFB07DI  EUNODEBTP2      00000GENERATED TRANSFER TO NET PLANT         +00000000000003375.00C" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BA9544900-----9100---ACLI07DI  EUNODEBTP2      00000GENERATED TRANSFER FROM BA 9120657      +00000000000003375.00C" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BA9544900-----9899---ACFB07DI  EUNODEBTP2      00000GENERATED TRANSFER FROM BA 9120657      +00000000000003375.00D" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BA9120657-----9100---ACLI07DI  EUNODEBTP2      00000BALDWIN WALLACE COLLEGE                 +00000000000003375.00C" + testingYear + "-01-05          ----------                                                                       "),

                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BA9120657-----8000---ACAS07DI  EUNODEBTP2      00000TP Generated Offset                     +00000000000003375.00D" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BA9120657-----9100---ACLI07DI  EUNODEBTP2      00000GENERATED TRANSFER TO NET PLANT         +00000000000003375.00D" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BA9120657-----9899---ACFB07DI  EUNODEBTP2      00000GENERATED TRANSFER TO NET PLANT         +00000000000003375.00C" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BA9544900-----9100---ACLI07DI  EUNODEBTP2      00000GENERATED TRANSFER FROM BA 9120657      +00000000000003375.00C" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BA9544900-----9899---ACFB07DI  EUNODEBTP2      00000GENERATED TRANSFER FROM BA 9120657      +00000000000003375.00D" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BA9120657-----9100---ACLI07DI  EUNODEBTP2      00000BALDWIN WALLACE COLLEGE                 +00000000000003375.00C" + testingYear + "-01-05          ----------                                                                       ") };

        scrub(input);
        assertOriginEntries(7, output);
    }

    /**
     * Tests that the scrubber does not generate a plant endebtedness entry for entries with financial sub object == "P1"
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testNoIndebtednessForObjectSubTypeP1() throws Exception {

        String[] input = new String[] { testingYear + "BL2231423-----9100---ACIN  CR  PLNODEBTP1      00000FRICKA FRACKA                                        45995.84C" + testingYear + "-01-05          ----------                                                                       ",
                testingYear + "BL2231423-----8000---ACAS  CR  PLNODEBTP1      00000TP Generated Offset                                  45995.84D" + testingYear + "-01-05          ----------                                                                       " };

        final AccountingPeriod currentAccountingPeriod = SpringContext.getBean(AccountingPeriodService.class).getByDate(dateTimeService.getCurrentSqlDate());

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BL2231423-----9100---ACIN  CR  PLNODEBTP1      00000FRICKA FRACKA                                        45995.84C" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BL2231423-----8000---ACAS  CR  PLNODEBTP1      00000TP Generated Offset                                  45995.84D" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL2231423-----8000---ACAS" + currentAccountingPeriod.getUniversityFiscalPeriodCode() + "CR  PLNODEBTP1      00000TP Generated Offset                     +00000000000045995.84D" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL2231423-----9100---ACIN" + currentAccountingPeriod.getUniversityFiscalPeriodCode() + "CR  PLNODEBTP1      00000FRICKA FRACKA                           +00000000000045995.84C" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL2231423-----8000---ACAS" + currentAccountingPeriod.getUniversityFiscalPeriodCode() + "CR  PLNODEBTP1      00000TP Generated Offset                     +00000000000045995.84D" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL2231423-----9100---ACIN" + currentAccountingPeriod.getUniversityFiscalPeriodCode() + "CR  PLNODEBTP1      00000FRICKA FRACKA                           +00000000000045995.84C" + testingYear + "-01-05          ----------                                                                       "), };

        scrub(input);
        assertOriginEntries(7, output);
    }

    /**
     * Tests that the scrubber does not generate a plant endebtedness entry for entries with budget balance types
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testNoIndebtednessForBudgetTransactions() throws Exception {

        String[] input = new String[] { testingYear + "BA9020204-----9100---BBLI07SB  01NODEBTBB      00000Biology Stockroom                                       13.77 " + testingYear + "-01-05          ----------                                                                       ",
                testingYear + "BA9020204-----8000---BBAS07SB  01NODEBTBB      00000TP Generated Offset                                     13.77 " + testingYear + "-01-05          ----------                                                                       " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BA9020204-----9100---BBLI07SB  01NODEBTBB      00000Biology Stockroom                                       13.77 " + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BA9020204-----8000---BBAS07SB  01NODEBTBB      00000TP Generated Offset                                     13.77 " + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BA9020204-----8000---BBAS07SB  01NODEBTBB      00000TP Generated Offset                     +00000000000000013.77 " + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BA9020204-----9100---BBLI07SB  01NODEBTBB      00000Biology Stockroom                       +00000000000000013.77 " + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BA9020204-----8000---BBAS07SB  01NODEBTBB      00000TP Generated Offset                     +00000000000000013.77 " + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BA9020204-----9100---BBLI07SB  01NODEBTBB      00000Biology Stockroom                       +00000000000000013.77 " + testingYear + "-01-05          ----------                                                                       ") };

        scrub(input);
        assertOriginEntries(7, output);
    }

    /**
     * Tests that the scrubber does not generate a capitalization entry for entries that occurred in certain periods
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testNoCapitalizationForCertainFiscalPeriods() throws Exception {

        String[] input = new String[] { testingYear + "BA6044900-----7000---ACEECBCR  PDNOCAPCB       00000214090047 EVERETT J PRESCOTT INC.                     1445.00D" + testingYear + "-01-05ACCDEFGHIJ----------12345678                                                               ",
                testingYear + "BA6044900-----8000---ACASCBCR  PDNOCAPCB       00000214090047 EVERETT J PRESCOTT INC.                     1445.00C" + testingYear + "-01-05ABCDEFGHIG----------12345679                                                               " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BA6044900-----7000---ACEECBCR  PDNOCAPCB       00000214090047 EVERETT J PRESCOTT INC.                     1445.00D" + testingYear + "-01-05ACCDEFGHIJ----------12345678                                                               "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BA6044900-----8000---ACASCBCR  PDNOCAPCB       00000214090047 EVERETT J PRESCOTT INC.                     1445.00C" + testingYear + "-01-05ABCDEFGHIG----------12345679                                                               "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BA6044900-----7000---ACEECBCR  PDNOCAPCB       00000214090047 EVERETT J PRESCOTT INC.       +00000000000001445.00D" + testingYear + "-01-05ACCDEFGHIJ----------12345678                                                               "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BA6044900-----8000---ACASCBCR  PDNOCAPCB       00000214090047 EVERETT J PRESCOTT INC.       +00000000000001445.00C" + testingYear + "-01-05ABCDEFGHIG----------12345679                                                               "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BA6044900-----7000---ACEECBCR  PDNOCAPCB       00000214090047 EVERETT J PRESCOTT INC.       +00000000000001445.00D" + testingYear + "-01-05ACCDEFGHIJ----------12345678                                                               "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BA6044900-----8000---ACASCBCR  PDNOCAPCB       00000214090047 EVERETT J PRESCOTT INC.       +00000000000001445.00C" + testingYear + "-01-05ABCDEFGHIG----------12345679                                                               ") };

        scrub(input);
        assertOriginEntries(7, output);
    }

    /**
     * Tests that the scrubber generates the correct offset entries, even with there are multiple origin codes involved
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testOffsetGenerationAcrossMultipleOriginCodes() throws Exception {
        final java.sql.Date scrubberDate = new java.sql.Date((SpringContext.getBean(RunDateService.class).calculateRunDate(dateTimeService.getCurrentDate())).getTime());
        final String formattedRunDate = new SimpleDateFormat(DATE_FORMAT).format(scrubberDate);

        String[] input = new String[] { testingYear + "BA6044906-----4010---ACEX07DI  01OFFSETORG     00000OFFICE SUPPLY CHARGEBACKS                              294.64D" + testingYear + "-01-05          ----------                                                                       ",
                testingYear + "BA6044906-----5000---ACEX07DI  EUOFFSETORG     00000OFFICE SUPPLY CHARGEBACKS                              294.64D" + testingYear + "-01-05          ----------                                                                       " };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BA6044906-----4010---ACEX07DI  01OFFSETORG     00000OFFICE SUPPLY CHARGEBACKS                              294.64D" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BA6044906-----5000---ACEX07DI  EUOFFSETORG     00000OFFICE SUPPLY CHARGEBACKS                              294.64D" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BA6044906-----4010---ACEX07DI  01OFFSETORG     00000OFFICE SUPPLY CHARGEBACKS               +00000000000000294.64D" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BA6044906-----8000---ACAS07DI  01OFFSETORG     00000GENERATED OFFSET                        +00000000000000294.64C" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BA6044906-----5000---ACEX07DI  EUOFFSETORG     00000OFFICE SUPPLY CHARGEBACKS               +00000000000000294.64D" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BA6044906-----8000---ACAS07DI  EUOFFSETORG     00000GENERATED OFFSET                        +00000000000000294.64C" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BA6044906-----4010---ACEX07DI  01OFFSETORG     00000OFFICE SUPPLY CHARGEBACKS               +00000000000000294.64D" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BA6044906-----8000---ACAS07DI  01OFFSETORG     00000GENERATED OFFSET                        +00000000000000294.64C" + formattedRunDate + "          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BA6044906-----5000---ACEX07DI  EUOFFSETORG     00000OFFICE SUPPLY CHARGEBACKS               +00000000000000294.64D" + testingYear + "-01-05          ----------                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BA6044906-----8000---ACAS07DI  EUOFFSETORG     00000GENERATED OFFSET                        +00000000000000294.64C" + formattedRunDate + "          ----------                                                                       "), };

        scrub(input);
        assertOriginEntries(7, output);
    }

    /**
     * Tests that the scrubber considers entries with accounts expired by origin code to be valid
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testExpiredAccountByOriginCode() throws Exception {

        String[] input = new String[] { testingYear + "BL1031467-----5300---ACEE07BA  01EXPRACT01     12345214090047 EVERETT J PRESCOTT INC.                     1445.00D" + testingYear + "-01-05ABCDEFGHIJ----------12345678                                                                       ",
                testingYear + "BL1031467-----8000---ACAS07BA  01EXPRACT01     12345214090047 EVERETT J PRESCOTT INC.                     1445.00C" + testingYear + "-01-05ABCDEFGHIG----------12345679                                                                       ", };

        EntryHolder[] output = new EntryHolder[] { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BL1031467-----5300---ACEE07BA  01EXPRACT01     12345214090047 EVERETT J PRESCOTT INC.                     1445.00D" + testingYear + "-01-05ABCDEFGHIJ----------12345678                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BL1031467-----8000---ACAS07BA  01EXPRACT01     12345214090047 EVERETT J PRESCOTT INC.                     1445.00C" + testingYear + "-01-05ABCDEFGHIG----------12345679                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL1031467-----5300---ACEE07BA  01EXPRACT01     12345214090047 EVERETT J PRESCOTT INC.       +00000000000001445.00D" + testingYear + "-01-05ABCDEFGHIJ----------12345678                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL1031467-----8000---ACAS07BA  01EXPRACT01     12345214090047 EVERETT J PRESCOTT INC.       +00000000000001445.00C" + testingYear + "-01-05ABCDEFGHIG----------12345679                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL1031467-----5300---ACEE07BA  01EXPRACT01     12345214090047 EVERETT J PRESCOTT INC.       +00000000000001445.00D" + testingYear + "-01-05ABCDEFGHIJ----------12345678                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL1031467-----8000---ACAS07BA  01EXPRACT01     12345214090047 EVERETT J PRESCOTT INC.       +00000000000001445.00C" + testingYear + "-01-05ABCDEFGHIG----------12345679                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_EXPIRED_OUTPUT_FILE, testingYear + "BL1031467-----5300---ACEE07BA  01EXPRACT01     12345214090047 EVERETT J PRESCOTT INC.       +00000000000001445.00D" + testingYear + "-01-05ABCDEFGHIJ----------12345678                                                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_EXPIRED_OUTPUT_FILE, testingYear + "BL1031467-----8000---ACAS07BA  01EXPRACT01     12345214090047 EVERETT J PRESCOTT INC.       +00000000000001445.00C" + testingYear + "-01-05ABCDEFGHIG----------12345679                                                                       "), };

        scrub(input);
        assertOriginEntries(7, output);
    }

    /**
     * Tests that the scrubber considers entries with expired c&g accounts to be valid
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testExpiredContractAndGrantAccount() throws Exception {

        String[] input = new String[] { testingYear + "BL4131407-----4100---ACEX07BA  LGEXPIRCGAC     00000CONCERTO OFFICE PRODUCTS                                48.53C" + testingYear + "-01-05          ----------                                                                               ",
                testingYear + "BL4131407-----9041---ACLI07BA  LGEXPIRCGAC     00000CONCERTO OFFICE PRODUCTS                                48.53D" + testingYear + "-01-05          ----------                                                                               " };
        EntryHolder[] output = new EntryHolder[] { new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BL4131407-----4100---ACEX07BA  LGEXPIRCGAC     00000CONCERTO OFFICE PRODUCTS                                48.53C" + testingYear + "-01-05          ----------                                                                               "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, testingYear + "BL4131407-----9041---ACLI07BA  LGEXPIRCGAC     00000CONCERTO OFFICE PRODUCTS                                48.53D" + testingYear + "-01-05          ----------                                                                               "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL2331489-----4100---ACEX07BA  LGEXPIRCGAC     00000AUTO FR BL4131407CONCERTO OFFICE PRODUCT+00000000000000048.53C" + testingYear + "-01-05          ----------                                                                               "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, testingYear + "BL2331489-----9041---ACLI07BA  LGEXPIRCGAC     00000AUTO FR BL4131407CONCERTO OFFICE PRODUCT+00000000000000048.53D" + testingYear + "-01-05          ----------                                                                               "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL2331489-----4100---ACEX07BA  LGEXPIRCGAC     00000AUTO FR BL4131407CONCERTO OFFICE PRODUCT+00000000000000048.53C" + testingYear + "-01-05          ----------                                                                               "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE, testingYear + "BL2331489-----9041---ACLI07BA  LGEXPIRCGAC     00000AUTO FR BL4131407CONCERTO OFFICE PRODUCT+00000000000000048.53D" + testingYear + "-01-05          ----------                                                                               "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_EXPIRED_OUTPUT_FILE, testingYear + "BL2331489-----4100---ACEX07BA  LGEXPIRCGAC     00000AUTO FR BL4131407CONCERTO OFFICE PRODUCT+00000000000000048.53C" + testingYear + "-01-05          ----------                                                                               "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_EXPIRED_OUTPUT_FILE, testingYear + "BL2331489-----9041---ACLI07BA  LGEXPIRCGAC     00000AUTO FR BL4131407CONCERTO OFFICE PRODUCT+00000000000000048.53D" + testingYear + "-01-05          ----------                                                                               ") };

        cleanupBatchDirectory();
        scrub(input);
        assertOriginEntries(7, output);
        cleanupBatchDirectory();
    }

    /**
     * Loads an array of String-formatted entries into the database, and then runs the scrubber on those entries
     *
     * @param inputTransactions an array of String-formatted entries to scrub
     */
    private void scrub(String[] inputTransactions) {
        clearBatchFiles();

        int count = 1;
        for (String transaction : inputTransactions) {
            OriginEntryFull entry = new OriginEntryFull();
            List<Message> messages = entry.setFromTextFileForBatch(transaction, count);
            if (messages.size() > 0) {
                LOG.warn("Transaction " + transaction + "could not be parsed correctly");
                for (Message message : messages) {
                    LOG.warn(message.getMessage());
                }
            }
            count += 1;
        }

        loadInputTransactions(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions);

        //we do not need to call clearCache() since no dao and jdbc calls mixted in this method.
        //refer to KFSMI-7637
        // persistenceService.clearCache();
        scrubberService.scrubEntries();

        String inputFile = batchDirectory + File.separator + GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        String outputFile = batchDirectory + File.separator + GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;

        BatchSortUtil.sortTextFileWithFields(inputFile, outputFile, new DemergerSortComparator());

        scrubberService.performDemerger();
    }

    /**
     * Cleans up (empties) the batch directory from residual files that can mess up the tests.
     */
    private void cleanupBatchDirectory() {
        File dir = new File(batchDirectory);
        File[] files = dir.listFiles();
        for (File oneFile : files) {
            boolean result = oneFile.delete();
            if (!result) {
                LOG.warn(oneFile.getName() + " was not deleted from directory=[" + batchDirectory + "]");
            } else {
                LOG.info(oneFile.getName() + " was deleted from directory=[" + batchDirectory + "]");
            }
        }
    }

    /**
     * deactivate a business object of the type clazz with the given primary keys. If the business object is not MutableInactivatable, do
     * nothing on it.
     *
     * @param <T> the type of the business object to be deactivated
     * @param clazz the class type of the business object to be deactivated
     * @param primaryKeys the primary keys of the business object to be deactivated
     */
    private <T extends PersistableBusinessObject> void deactivate(Class<T> clazz, Map<String, Object> primaryKeys) {
        PersistableBusinessObject bo = businessObjectService.findByPrimaryKey(clazz, primaryKeys);

        if (bo instanceof MutableInactivatable) {
            MutableInactivatable mutableInactivatable = (MutableInactivatable) bo;
            mutableInactivatable.setActive(false);
            businessObjectService.save(bo);
        }
    }
}
