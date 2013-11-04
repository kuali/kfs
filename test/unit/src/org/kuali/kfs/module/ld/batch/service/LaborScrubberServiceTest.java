/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.ld.batch.service;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.batch.LaborScrubberStep;
import org.kuali.kfs.module.ld.businessobject.LaborOriginEntry;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.Message;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;


@ConfigureContext
public class LaborScrubberServiceTest extends LaborOriginEntryTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborScrubberServiceTest.class);

    private LaborScrubberService laborScrubberService = null;

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        laborScrubberService = SpringContext.getBean(LaborScrubberService.class);
        laborScrubberService.setDateTimeService(dateTimeService);

        dateTimeService.setCurrentDate(new java.util.Date());
    }

    public void testDemerger() throws Exception {
        String[] inputTransactions = {
                "2026BA6044900-----2400---ACEX06BT  PLM01010207     0000100009529----------KUALI TEST DESCRIPTION                  +00000000000003493.50D2008-12-22                                                 2008-12-222008-12-31000168.002007060000149952 001REGS12PAE 11 M001010207                     IU IUBLA",
                "2009BA6044900-----2400---ACEX06BT  PLM01010207     0000100009529----------KUALI TEST DESCRIPTION                  +00000000000003493.50C2008-12-22                                                 2008-12-222008-12-31000168.002007060000149952 001REGS12PAE 11 M001010207                     IU IUBLA",
                "2026BA6044900-----2400---ACEX06ST  PLM01010207     0000100009529----------KUALI TEST DESCRIPTION                  +00000000000003493.50C2008-12-22                                                 2008-12-222008-12-31000168.002007060000149952 001REGS12PAE 11 M001010207                     IU IUBLA",
                "2009BA6044900-----2400---ACEX06ST  PLM01010207     0000100009529----------KUALI TEST DESCRIPTION                  +00000000000003493.50C2008-12-22                                                 2008-12-222008-12-31000168.002007060000149952 001REGS12PAE 11 M001010207                     IU IUBLA"
        };

        EntryHolder[] outputTransactions = {
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[0]),
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[1]),
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[2]),
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[3]),
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[0]),
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[1]),
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[2]),
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[3])
        };

        scrub(inputTransactions);
        assertOriginEntries(4, outputTransactions);
    }

    public void testValidEntries() throws Exception {
        String[] inputTransactions = {
                "2009BA6044900-----2400---ACEX06BT  PLM01010207     0000100009529----------KUALI TEST DESCRIPTION                  +00000000000003493.50D2008-12-22                                                 2008-12-222008-12-31000168.002009060000149952 001REGS12PAE 11 M001010207                     IU IUBLA",
                "2009BA6044900-----2400---ACEX06BT  PLM01010207     0000200014789----------KUALI TEST DESCRIPTION                  +00000000000003329.27D2008-12-22                                                 2008-12-222008-12-31000082.322009060000649044 000REGS12PAE 16 M001010207                     IU IUBLA",
                "2009BA6044900-----2400---ACEX06BT  PLM01010207     0000300015213----------KUALI TEST DESCRIPTION                  +00000000000002716.89D2008-12-22                                                 2008-12-222008-12-31000084.862009060000683206 000REGS12PAE 13 M001010207                     IU IUBLA",
                "2009BA6044900-----2400---ACEX06BT  PLM01010207     0000400017659----------KUALI TEST DESCRIPTION                  +00000000000001620.08D2008-12-22                                                 2008-12-222008-12-31000041.382009060001316908 000REGS12PAE 13 M001010207                     IU IUBLA",
                "2009BA6044900-----2400---ACEX06BT  PLM01010207     0000500019196----------KUALI TEST DESCRIPTION                  +00000000000005106.15D2008-12-22                                                 2008-12-222008-12-31000104.932009060001368813 000REGS12PAE 19 M001010207                     IU IUBLA",
                "2009BA6044900-----2400---ACEX06BT  PLM01010207     0000600022120----------KUALI TEST DESCRIPTION                  +00000000000003071.10D2008-12-22                                                 2008-12-222008-12-31000106.752009060001773996 000REGS12PAE 12 M001010207                     IU IUBLA",
                "2009BA6044900-----2400---ACEX07BT  PLM04013107     0000100009529----------KUALI TEST DESCRIPTION                  +00000000000003493.50D2009-01-25                                                 2009-01-252009-01-31000184.002009070000149952 001REGS12PAE 11 M004013107                     IU IUBLA",
                "2009BA6044900-----2400---ACEX07BT  PLM04013107     0000200014789----------KUALI TEST DESCRIPTION                  +00000000000003329.27D2009-01-25                                                 2009-01-252009-01-31000090.162009070000649044 000REGS12PAE 16 M004013107                     IU IUBLA",
                "2009BA6044900-----2400---ACEX07BT  PLM04013107     0000300015213----------KUALI TEST DESCRIPTION                  +00000000000002716.89D2009-01-25                                                 2009-01-252009-01-31000092.942009070000683206 000REGS12PAE 13 M004013107                     IU IUBLA",
                "2009BA6044900-----2400---ACEX07BT  PLM04013107     0000400017659----------KUALI TEST DESCRIPTION                  +00000000000001620.08D2009-01-25                                                 2009-01-252009-01-31000045.322009070001316908 000REGS12PAE 13 M004013107                     IU IUBLA"
        };

        EntryHolder[] outputTransactions = {
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[0]),
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[1]),
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[2]),
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[3]),
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[4]),
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[5]),
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[6]),
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[7]),
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[8]),
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[9]),
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, inputTransactions[0]),
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, inputTransactions[1]),
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, inputTransactions[2]),
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, inputTransactions[3]),
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, inputTransactions[4]),
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, inputTransactions[5]),
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, inputTransactions[6]),
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, inputTransactions[7]),
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, inputTransactions[8]),
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, inputTransactions[9])
        };

        scrub(inputTransactions);
        assertOriginEntries(4, outputTransactions);
    }

    public void testBlankFiscalYear() throws Exception {
        String[] inputTransactions = {
                "    BA6044900-----2400---ACEX06BT  PLBLANKFISC     0000100009529----------KUALI TEST DESCRIPTION                  +00000000000003493.50D2008-12-22                                                 2008-12-222008-12-31000168.002009060000149952 001REGS12PAE 11 M001010207                     IU IUBLA"
        };

        String expectedOutput = this.testingYear + StringUtils.substring(inputTransactions[0], 4, 29) + this.testingPeriodCode + StringUtils.substring(inputTransactions[0], 31);
        EntryHolder[] outputTransactions = {
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[0]),
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, expectedOutput)
        };

        scrub(inputTransactions);
        assertOriginEntries(4, outputTransactions);
    }

    public void testInvalidObjectCode() throws Exception {
        String[] inputTransactions = {
                "2000BA6044906-----2400---ACEX06BT  PLCLOSEFISC     0000300015213----------KUALI TEST DESCRIPTION                  +00000000000002716.89D2006-12-22                                                 2006-12-222006-12-31000084.862007060000683206 000REGS12PAE 13 M001010207                     IU IUBLA",
                "2000BA6044906-----2400---ACEX06BT  PLCLOSEFISC     0000400017659----------KUALI TEST DESCRIPTION                  +00000000000001620.08D2006-12-22                                                 2006-12-222006-12-31000041.382007060001316908 000REGS12PAE 13 M001010207                     IU IUBLA"
        };

        EntryHolder[] outputTransactions = {
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[0]),
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[1]),
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[0]),
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[1])
        };

        scrub(inputTransactions);
        assertOriginEntries(4, outputTransactions);

    }

    public void testInvalidFiscalYear() throws Exception {
        String[] inputTransactions = {
               "2026BA6044913-----2400---ACEX06BT  PLINVALFISC     0000500019196----------KUALI TEST DESCRIPTION                  +00000000000005106.15D2006-12-22                                                 2006-12-222006-12-31000104.932007060001368813 000REGS12PAE 19 M001010207                     IU IUBLA",
               "2026BA6044913-----2400---ACEX06BT  PLINVALFISC     0000600022120----------KUALI TEST DESCRIPTION                  +00000000000003071.10D2006-12-22                                                 2006-12-222006-12-31000106.752007060001773996 000REGS12PAE 12 M001010207                     IU IUBLA"
        };

        EntryHolder[] outputTransactions = {
               new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[0]),
               new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[1]),
               new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[0]),
               new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[1])
        };

        scrub(inputTransactions);
        assertOriginEntries(4, outputTransactions);
   }

   public void testInvalid() throws Exception {
       String[] inputTransactions = {
               "2009  1031400-----2400---ACEX07BT  PLBLANKCHAR     0000100009529----------KUALI TEST DESCRIPTION                  +00000000000003493.50D2009-01-25                                                 2009-01-252009-01-31000184.002009070000149952 001REGS12PAE 11 M004013107                     IU IUBLA",
               "2009  1031400-----2400---ACEX07BT  PLBLANKCHAR     0000200014789----------KUALI TEST DESCRIPTION                  +00000000000003329.27D2009-01-25                                                 2009-01-252009-01-31000090.162009070000649044 000REGS12PAE 16 M004013107                     IU IUBLA"
       };

       EntryHolder[] outputTransactions = {
               new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[0]),
               new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[1]),
               new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[0]),
               new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[1])
       };

       scrub(inputTransactions);
       assertOriginEntries(4, outputTransactions);
   }

   public void testInvalidDebitCreditCode() throws Exception {
       String[] inputTransactions = {
               "2009BA6044900-----2400---ACEX06BT  PLM01010207     0000100009529----------KUALI TEST DESCRIPTION                  +00000000000003493.50X2008-12-22                                                 2008-12-222008-12-31000168.002009060000149952 001REGS12PAE 11 M001010207                     IU IUBLA",
               "2009BA6044900-----2400---ACEX06BT  PLM01010207     0000100009529----------KUALI TEST DESCRIPTION                  +00000000000003495.50X2008-12-25                                                 2008-12-222008-12-31000168.002009060000149952 001REGS12PAE 11 M001010207                     IU IUBLA"
       };

       EntryHolder[] outputTransactions = {
               new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[0]),
               new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[1]),
               new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[0]),
               new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[1])
       };

       scrub(inputTransactions);
       assertOriginEntries(4, outputTransactions);
    }

    public void testA2balanceTypeAcceptClosedFiscalPeriod() throws Exception {
        String[] inputTransactions = {
                "2008BL1031400-----5772---A2EX06ST  PLPRENC-07      00002MTFRING ----------KUALI TEST DESCRIPTION                  +00000000000028988.60C2008-12-14                                                D2008-12-14          000000.00200906-----------000             M037113006                             "
        };

        EntryHolder[] outputTransactions = {
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[0]),
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, inputTransactions[0])
        };

        scrub(inputTransactions);
        assertOriginEntries(4, outputTransactions);
    }

    public void testValidateAccountWithAllParametersOff() throws Exception {
        TestUtils tu = new TestUtils();
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.SUBFUND_WAGE_EXCLUSION_PARAMETER, "N");
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.ACCOUNT_FRINGE_EXCLUSION_PARAMETER, "N");
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.CONTINUATION_ACCOUNT_LOGIC_PARAMETER, "N");
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.SUSPENSE_ACCOUNT_LOGIC_PARAMETER, "N");

        String[] inputTransactions = {
                "2008BL2131401-----5772---ACEX08ST  PLPRENC-07      00002MTFRING ----------KUALI TEST DESCRIPTION                  +00000000000028988.60C2008-12-14                                                D2008-12-14          000000.00200906-----------000             M037113006                             "
        };

        EntryHolder[] outputTransactions = {
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[0]),
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, inputTransactions[0]),
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_EXPIRED_OUTPUT_FILE, inputTransactions[0])
        };

        scrub(inputTransactions);
        assertOriginEntries(4, outputTransactions);
    }

    // All parameters are off, except a parameter for continuation account.
    // Test with closed account and special origination code that has override ability
    public void testInvalidateAccountWithOnlyContinuationAccountOn() throws Exception {
        TestUtils tu = new TestUtils();
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.SUBFUND_WAGE_EXCLUSION_PARAMETER, "N");
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.ACCOUNT_FRINGE_EXCLUSION_PARAMETER, "N");
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.CONTINUATION_ACCOUNT_LOGIC_PARAMETER, "Y");
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.SUSPENSE_ACCOUNT_LOGIC_PARAMETER, "N");

        String[] inputTransactions = {
                "2008BL2131401-----5772---ACEX08ST  PLPRENC-07      00002MTFRING ----------AUTO FR BL2631476KUALI TEST DESCRIPTION +00000000000028988.60C2008-12-14                                                D2008-12-14          000000.00200906-----------000             M037113006                             "
        };

        EntryHolder[] outputTransactions = {
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[0]),
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[0]),
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_EXPIRED_OUTPUT_FILE, inputTransactions[0])
        };

        scrub(inputTransactions);
        assertOriginEntries(4, outputTransactions);
    }

    // All parameters are off, except a parameter for continuation account. Test with closed account.
    public void testValidateAccountWithOnlyContinuationAccountOn() throws Exception {
        TestUtils tu = new TestUtils();
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.SUBFUND_WAGE_EXCLUSION_PARAMETER, "N");
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.ACCOUNT_FRINGE_EXCLUSION_PARAMETER, "N");
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.CONTINUATION_ACCOUNT_LOGIC_PARAMETER, "Y");
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.SUSPENSE_ACCOUNT_LOGIC_PARAMETER, "N");

        // This entry is expired or closed. This entry will be saved in LSCV.
        String[] inputTransactions = {
                "2008BL2131401-----5772---ACEX08ST  99PRENC-07      00002MTFRING ----------KUALI TEST DESCRIPTION                  +00000000000028988.60C2008-12-14                                                D2008-12-14          000000.00200906-----------000             M037113006                             "
        };

        String expectedOutput = "2008BL2331489-----5772---ACEX08ST  99PRENC-07      00002MTFRING ----------AUTO FR BL2131401KUALI TEST DESCRIPTION +00000000000028988.60C2008-12-14                                                D2008-12-14          000000.00200906-----------000             M037113006                             ";
        EntryHolder[] outputTransactions = {
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[0]),
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, expectedOutput),
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_EXPIRED_OUTPUT_FILE, expectedOutput)
        };

        scrub(inputTransactions);
        assertOriginEntries(4, outputTransactions);
    }

    public void testNotSubfundWageExclusion() throws Exception {

        TestUtils tu = new TestUtils();

        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.SUBFUND_WAGE_EXCLUSION_PARAMETER, "Y");
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.ACCOUNT_FRINGE_EXCLUSION_PARAMETER, "Y");
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.CONTINUATION_ACCOUNT_LOGIC_PARAMETER, "Y");
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.SUSPENSE_ACCOUNT_LOGIC_PARAMETER, "Y");
        // This entry is expired or closed, but it won't use alternative continuation account
        // because there is no Expiration Date.
        // This entry accepts sunfundwage, so will not run subfundWageExclusion logic.
        // This entry will not run accountFringeExclusion logic.
        // This entry will be saved in LSCX and LSCV.

        String[] inputTransactions = {
                "2008BL2131401-----5772---ACEX08ST  99PRENC-07      00002MTFRING ----------KUALI TEST DESCRIPTION                  +00000000000028988.60C2008-12-14                                                D2008-12-14          000000.00200906-----------000             M037113006                             "
        };

        String expectedOutput = "2008BL2331489-----5772---ACEX08ST  99PRENC-07      00002MTFRING ----------AUTO FR BL2131401KUALI TEST DESCRIPTION +00000000000028988.60C2008-12-14                                                D2008-12-14          000000.00200906-----------000             M037113006                             ";
        EntryHolder[] outputTransactions = {
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[0]),
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, expectedOutput),
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_EXPIRED_OUTPUT_FILE, expectedOutput)
        };

        scrub(inputTransactions);
        assertOriginEntries(4, outputTransactions);
    }

    public void testSubfundWageExclusion() throws Exception {
        TestUtils tu = new TestUtils();
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.SUBFUND_WAGE_EXCLUSION_PARAMETER, "Y");
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.ACCOUNT_FRINGE_EXCLUSION_PARAMETER, "Y");
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.CONTINUATION_ACCOUNT_LOGIC_PARAMETER, "Y");
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.SUSPENSE_ACCOUNT_LOGIC_PARAMETER, "Y");

        // This entry is expired or closed.
        // This entry doensn't accept sunfundwage, so will run subfundWageExclusion logic and use suspense account.
        // This entry will be saved in LSCX and LSCV.

        String[] inputTransactions = {
                "2008EA6867070-----5772---ACEX08ST  99PRENC-07      00002MTFRING ----------KUALI TEST DESCRIPTION                  +00000000000028988.60C2008-12-14                                                D2008-12-14          000000.00200906-----------000             M037113006                             "
        };

        String expectedOutput = "2008UA6812756-----5772---ACEX08ST  99PRENC-07      00002MTFRING ----------KUALI TEST DESCRIPTION                  +00000000000028988.60C2008-12-14                                                D2008-12-14          000000.00200906-----------000             M037113006                             ";
        EntryHolder[] outputTransactions = {
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[0]),
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, expectedOutput),
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_EXPIRED_OUTPUT_FILE, expectedOutput)
        };

        scrub(inputTransactions);
        assertOriginEntries(4, outputTransactions);
    }

    public void testSubfundWageExclusionWithSubfundParameterOff() throws Exception {
        TestUtils tu = new TestUtils();
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.SUBFUND_WAGE_EXCLUSION_PARAMETER, "N");
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.ACCOUNT_FRINGE_EXCLUSION_PARAMETER, "Y");
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.CONTINUATION_ACCOUNT_LOGIC_PARAMETER, "Y");
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.SUSPENSE_ACCOUNT_LOGIC_PARAMETER, "Y");

        // This entry is expired or closed.
        // This account doesn't have closed indicator, so continuation account logic won't run and it is stored only in LSCV.
        // This entry not accept sunfundwage, but the parameter is off, so subfundWageExclusion logic won't run.

        String[] inputTransactions = {
                "2008UA6812756-----5772---ACEX08ST  99PRENC-07      00002MTFRING ----------KUALI TEST DESCRIPTION                  +00000000000028988.60C2008-12-14                                                D2008-12-14          000000.00200906-----------000             M037113006                             "
        };

        EntryHolder[] outputTransactions = {
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[0]),
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, inputTransactions[0])
        };

        scrub(inputTransactions);
        assertOriginEntries(4, outputTransactions);
    }


    /**
     * Test non-fringe, non-alt, non-closed
     * @throws Exception
     */
    public void testNonFringeNonAltNonClosed() throws Exception {
        TestUtils tu = new TestUtils();
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.SUBFUND_WAGE_EXCLUSION_PARAMETER, "Y");
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.ACCOUNT_FRINGE_EXCLUSION_PARAMETER, "Y");
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.CONTINUATION_ACCOUNT_LOGIC_PARAMETER, "Y");
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.SUSPENSE_ACCOUNT_LOGIC_PARAMETER, "Y");

        // This entry is expired or closed, but, this account doesn't have closed indicator, so it is stored in LSCV.
        // This entry accept sunfundwage, and not accept Fringe
        // Chart code: BL, Object code: 5760, Fiscal Year: 2008
        // It doesn't have alternative fringe account, so it uses suspense account.
        // This entry will be changed   2008UA6812756-----5760---ACEX08ST  99PRENC-07      00002MTFRING ----------KUALI TEST DESCRIPTION                             28988.60C2008-12-14                                                 2008-12-14          0        200906-----------0               M037113006
        //      "2008BL2631476-----5760---ACEX08ST  99PRENC-07      00002MTFRING ----------KUALI TEST DESCRIPTION                  +0000000000028988.60C2008-12-14                                                D2008-12-14          000000.00200906-----------000             M037113006                             "

        // Test entries provided by Damon Dorsey on 5/10/11
        String[] inputTransactions = {
            "2011UA6812757-----2000---ACEX06ST  LDNOWAGE1            00000001----------NO WAGE ACCOUNT                         +00000000000001000.00D2011-09-01                                                 2011-10-232011-08-090000000102011010001537923 01 RGNS12",
            "2011UA6812757-----5625---ACEX06ST  LDNOWAGE2            00000001----------NO WAGE ACCOUNT                         +00000000000000125.00D2011-09-01                                                 2011-10-232011-08-090000000102011010001537923 01 RGNS12",
            "2011EA0366595-----5625---ACEX06ST  LDNOFRINGE2          00000001----------NO FRINGE ACCOUNT                       +00000000000003500.00D2011-09-01                                                 2011-10-232011-08-090000000102011010001537923 01 RGNS12"
        };

        String[] expectedOutput = {
                "2011UA6812756-----2000---ACEX06ST  LDNOWAGE1       0000000000001----------NO WAGE ACCOUNT                         +00000000000001000.00D2011-09-01                                                 2011-10-232011-08-09       102011010001537923   1RGNS12",
                "2011UA6812756-----5625---ACEX06ST  LDNOWAGE2       0000000000001----------NO WAGE ACCOUNT                         +00000000000000125.00D2011-09-01                                                 2011-10-232011-08-09       102011010001537923   1RGNS12",
                "2011UA6612160-----5625---ACEX06ST  LDNOFRINGE2     0000000000001----------NO FRINGE ACCOUNT                       +00000000000003500.00D2011-09-01                                                 2011-10-232011-08-09       102011010001537923   1RGNS12"



//                "2011UA6812757-----2000---ACEX  ST  LDNOWAGE1            00000001----------NO WAGE ACCOUNT                         +00000000000001000.00D2011-09-01                                                 2011-10-232011-08-090000000102011010001537923 01 RGNS12",
//                "2011UA6812757-----5625---ACEX  ST  LDNOWAGE2            00000001----------NO WAGE ACCOUNT                         +00000000000000125.00D2011-09-01                                                 2011-10-232011-08-090000000102011010001537923 01 RGNS12",
//                "2011UA6612160-----5625---ACEX11ST  LDNOFRINGE2     0000000000001----------NO FRINGE ACCOUNT                       +00000000000003500.00D2011-09-01                                                 2011-10-232011-08-09       102011010001537923   1RGNS12"
        };



        EntryHolder[] outputTransactions = {
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[0]),
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[1]),
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[2]),
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, expectedOutput[0]),
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, expectedOutput[1]),
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, expectedOutput[2])
        };

        scrub(inputTransactions);
        assertOriginEntries(4, outputTransactions);
    }

    // TODO: the tested account has an continuation account, so the suspense account logic cannot be reached
    public void testNonFringeNonAltNonClosedIndicatorWithSuspenseParameterOff() throws Exception {
        /*TestUtils tu = new TestUtils();
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.SUBFUND_WAGE_EXCLUSION_PARAMETER, "Y");
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.ACCOUNT_FRINGE_EXCLUSION_PARAMETER, "Y");
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.CONTINUATION_ACCOUNT_LOGIC_PARAMETER, "Y");
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.SUSPENSE_ACCOUNT_LOGIC_PARAMETER, "N");

        // This entry is expired or closed, but, this account doesn't have closed indicator, so it is stored in LSCV.
        // This entry accept sunfundwage, and not accept Fringe Chart code: BL, Object code: 5760, Fiscal Year: 2008
        // It doesn't have alternative fringe account, so it uses suspense account.
        // But, since the parameter for suspense account indicator is off, this entry will be saved in LSCE.

        String[] inputTransactions = {
                "2008BL2631476-----5760---ACEX08ST  99PRENC-07      00002MTFRING ----------KUALI TEST DESCRIPTION                  +0000000000028988.60C2008-12-14                                                D2008-12-14          000000.00200906-----------000             M037113006                             "
        };

        EntryHolder[] outputTransactions = {
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[0]),
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE, inputTransactions[0])
        };

        scrub(inputTransactions);
        assertOriginEntries(4, outputTransactions); */
    }

    public void testNonFringeNonAltNonClosedIndicatorWithFringeParameterOff() throws Exception {
        TestUtils tu = new TestUtils();
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.SUBFUND_WAGE_EXCLUSION_PARAMETER, "Y");
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.ACCOUNT_FRINGE_EXCLUSION_PARAMETER, "N");
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.CONTINUATION_ACCOUNT_LOGIC_PARAMETER, "Y");
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.SUSPENSE_ACCOUNT_LOGIC_PARAMETER, "Y");

        String[] inputTransactions = {
                "2008BL2631476-----5760---ACEX08ST  99PRENC-07      00002MTFRING ----------KUALI TEST DESCRIPTION                  +00000000000028988.60C2008-12-14                                                D2008-12-14          000000.00200906-----------000             M037113006                             "
        };

        String expectedOutput = "2008BL2331489-----5760---ACEX08ST  99PRENC-07      00002MTFRING ----------AUTO FR BL2631476KUALI TEST DESCRIPTION +00000000000028988.60C2008-12-14                                                D2008-12-14          000000.00200906-----------000             M037113006                             ";
        EntryHolder[] outputTransactions = {
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[0]),
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_EXPIRED_OUTPUT_FILE, expectedOutput),
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, expectedOutput)
        };

        scrub(inputTransactions);
        assertOriginEntries(4, outputTransactions);

    }

    public void testClosedAccountWithAllParemetersOff() throws Exception {
        TestUtils tu = new TestUtils();
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.SUBFUND_WAGE_EXCLUSION_PARAMETER, "N");
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.ACCOUNT_FRINGE_EXCLUSION_PARAMETER, "N");
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.CONTINUATION_ACCOUNT_LOGIC_PARAMETER, "N");
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.SUSPENSE_ACCOUNT_LOGIC_PARAMETER, "N");

        String[] inputTransactions = {
                "2008EA6867070-----5760---ACEX08ST  99PRENC-07      00002MTFRING ----------KUALI TEST DESCRIPTION                  +00000000000028988.60C2008-12-14                                                D2008-12-14          000000.00200906-----------000             M037113006                             "
        };

        EntryHolder[] outputTransactions = {
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[0]),
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, inputTransactions[0]),
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_EXPIRED_OUTPUT_FILE, inputTransactions[0])
        };

        scrub(inputTransactions);
        assertOriginEntries(4, outputTransactions);
    }

    public void testClosedAccountWithAllParemetersOffOnlyContinuationOn() throws Exception {
        TestUtils tu = new TestUtils();
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.SUBFUND_WAGE_EXCLUSION_PARAMETER, "N");
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.ACCOUNT_FRINGE_EXCLUSION_PARAMETER, "N");
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.CONTINUATION_ACCOUNT_LOGIC_PARAMETER, "Y");
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.SUSPENSE_ACCOUNT_LOGIC_PARAMETER, "N");

        String[] inputTransactions = {
                "2008EA6867070-----5760---ACEX08ST  99PRENC-07      00002MTFRING ----------KUALI TEST DESCRIPTION                  +00000000000028988.60C2008-12-14                                                D2008-12-14          000000.00200906-----------000             M037113006                             "
        };

        String expectedOutput =  "2008EA9567000-----5760---ACEX08ST  99PRENC-07      00002MTFRING ----------AUTO FR EA6867070KUALI TEST DESCRIPTION +00000000000028988.60C2008-12-14                                                D2008-12-14          000000.00200906-----------000             M037113006                             ";
        EntryHolder[] outputTransactions = {
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions[0]),
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, expectedOutput),
                new EntryHolder(LaborConstants.BatchFileSystem.SCRUBBER_EXPIRED_OUTPUT_FILE, expectedOutput)
        };

        scrub(inputTransactions);
        assertOriginEntries(4, outputTransactions);
    }

    /**
     * Loads an array of String-formatted entries into the database, and then runs the scrubber on those entries
     *
     * @param inputTransactions an array of String-formatted entries to scrub
     */
    private void scrub(String[] inputTransactions) {
        clearBatchFiles();

        int count = 1;
        for (String transaction: inputTransactions) {
            OriginEntryFull entry = new LaborOriginEntry();
            List<Message> messages = entry.setFromTextFileForBatch(transaction, count);
            if (messages.size() > 0) {
                LOG.warn("Transaction "+transaction+"could not be parsed correctly");
                for (Message message : messages) {
                    LOG.warn(message.getMessage());
                }
            }
            count += 1;
        }

        loadInputTransactions(LaborConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions);
        laborScrubberService.scrubEntries();
    }

}
