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

import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.kuali.kfs.coa.businessobject.OffsetDefinition;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.batch.ScrubberStep;
import org.kuali.kfs.gl.batch.service.RunDateService;
import org.kuali.kfs.gl.businessobject.OriginEntryTestBase;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.parameter.ParameterEvaluator;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

/**
 * Test Flexible Offset in the scrubber
 */
@ConfigureContext
public class ScrubberFlexibleOffsetTest extends OriginEntryTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ScrubberFlexibleOffsetTest.class);

    private ScrubberService scrubberService;

    /**
     * Sets up the services neede for this test and also sets the date/time service's date time to one minute before midnight January 1, 2006
     * @see org.kuali.kfs.gl.businessobject.OriginEntryTestBase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        scrubberService = SpringContext.getBean(ScrubberService.class);
       // scrubberService.setDateTimeService(dateTimeService);
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
     * Test to make sure that flexible offset is off when the flag is off
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testNonFlexibleOffsetGeneration() throws Exception {

        super.setApplicationConfigurationFlag(OffsetDefinition.class, KFSConstants.SystemGroupParameterNames.FLEXIBLE_OFFSET_ENABLED_FLAG, false);

        updateDocTypeForScrubberOffsetGeneration();
        setOffsetAccounts();

        final java.sql.Date scrubberDate = new java.sql.Date((SpringContext.getBean(RunDateService.class).calculateRunDate(SpringContext.getBean(DateTimeService.class).getCurrentDate())).getTime());
        final String formattedRunDate = new SimpleDateFormat("yyyy-MM-dd").format(scrubberDate);

        String[] input = new String[] {
                TestUtils.getFiscalYearForTesting()+"BA9120656-----4190---ACEX02DI  01NOFLEX001     00000TEST FLEXIBLE OFFSET - NO FLEX                        2000.00D"+TestUtils.getFiscalYearForTesting()+"-01-01          ----------                                       ",
                TestUtils.getFiscalYearForTesting()+"BA6044900-----4190---ACEX02DI  01NOFLEX002     00000TEST FLEXIBLE OFFSET - FLEX                           1000.00D"+TestUtils.getFiscalYearForTesting()+"-01-01          ----------                                       ",
                TestUtils.getFiscalYearForTesting()+"BL1023200-----4190---ACEX02DI  01NOFLEX003     00000TEST FLEXIBLE OFFSET - FLEX                           3000.00D"+TestUtils.getFiscalYearForTesting()+"-01-01          ----------                                       ",
                TestUtils.getFiscalYearForTesting()+"BL1023200-----7030---ACEX02DI  01NOFLEX004     00000TEST FLEXIBLE OFFSET - FLEX                           3500.00D"+TestUtils.getFiscalYearForTesting()+"-01-01          ----------                                       ",
                TestUtils.getFiscalYearForTesting()+"BL2331473-----4190---ACEX02DI  01NOFLEX005     00000TEST FLEXIBLE OFFSET - FLEX                           4000.00D"+TestUtils.getFiscalYearForTesting()+"-01-01          ----------                                       ", };

        EntryHolder[] output = new EntryHolder[] {
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE,        TestUtils.getFiscalYearForTesting()+"BA9120656-----4190---ACEX02DI  01NOFLEX001     00000TEST FLEXIBLE OFFSET - NO FLEX                        2000.00D"+TestUtils.getFiscalYearForTesting()+"-01-01          ----------                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE,        TestUtils.getFiscalYearForTesting()+"BA6044900-----4190---ACEX02DI  01NOFLEX002     00000TEST FLEXIBLE OFFSET - FLEX                           1000.00D"+TestUtils.getFiscalYearForTesting()+"-01-01          ----------                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE,        TestUtils.getFiscalYearForTesting()+"BL1023200-----4190---ACEX02DI  01NOFLEX003     00000TEST FLEXIBLE OFFSET - FLEX                           3000.00D"+TestUtils.getFiscalYearForTesting()+"-01-01          ----------                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE,        TestUtils.getFiscalYearForTesting()+"BL1023200-----7030---ACEX02DI  01NOFLEX004     00000TEST FLEXIBLE OFFSET - FLEX                           3500.00D"+TestUtils.getFiscalYearForTesting()+"-01-01          ----------                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE,        TestUtils.getFiscalYearForTesting()+"BL2331473-----4190---ACEX02DI  01NOFLEX005     00000TEST FLEXIBLE OFFSET - FLEX                           4000.00D"+TestUtils.getFiscalYearForTesting()+"-01-01          ----------                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, TestUtils.getFiscalYearForTesting()+"BA9120656-----4190---ACEX02DI  01NOFLEX001     00000TEST FLEXIBLE OFFSET - NO FLEX          +00000000000002000.00D"+TestUtils.getFiscalYearForTesting()+"-01-01          ----------                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, TestUtils.getFiscalYearForTesting()+"BA9120656-----8000---ACAS02DI  01NOFLEX001     00000GENERATED OFFSET                        +00000000000002000.00C"+formattedRunDate+"          ----------                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, TestUtils.getFiscalYearForTesting()+"BA6044900-----4190---ACEX02DI  01NOFLEX002     00000TEST FLEXIBLE OFFSET - FLEX             +00000000000001000.00D"+TestUtils.getFiscalYearForTesting()+"-01-01          ----------                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, TestUtils.getFiscalYearForTesting()+"BA6044900-----8000---ACAS02DI  01NOFLEX002     00000GENERATED OFFSET                        +00000000000001000.00C"+formattedRunDate+"          ----------                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, TestUtils.getFiscalYearForTesting()+"BL1023200-----4190---ACEX02DI  01NOFLEX003     00000TEST FLEXIBLE OFFSET - FLEX             +00000000000003000.00D"+TestUtils.getFiscalYearForTesting()+"-01-01          ----------                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, TestUtils.getFiscalYearForTesting()+"BL1023200-----8000---ACAS02DI  01NOFLEX003     00000GENERATED OFFSET                        +00000000000003000.00C"+formattedRunDate+"          ----------                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, TestUtils.getFiscalYearForTesting()+"BL9520004-----8611---ACAS02DI  01NOFLEX004     00000GENERATED CAPITALIZATION                +00000000000003500.00D"+TestUtils.getFiscalYearForTesting()+"-01-01          ----------                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, TestUtils.getFiscalYearForTesting()+"BL9520004-----9899---ACFB02DI  01NOFLEX004     00000GENERATED CAPITALIZATION                +00000000000003500.00C"+TestUtils.getFiscalYearForTesting()+"-01-01          ----------                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, TestUtils.getFiscalYearForTesting()+"BL1023200-----7030---ACEX02DI  01NOFLEX004     00000TEST FLEXIBLE OFFSET - FLEX             +00000000000003500.00D"+TestUtils.getFiscalYearForTesting()+"-01-01          ----------                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, TestUtils.getFiscalYearForTesting()+"BL1023200-----8000---ACAS02DI  01NOFLEX004     00000GENERATED OFFSET                        +00000000000003500.00C"+formattedRunDate+"          ----------                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, TestUtils.getFiscalYearForTesting()+"BL2331473-----4190---ACEX02DI  01NOFLEX005     00000TEST FLEXIBLE OFFSET - FLEX             +00000000000004000.00D"+TestUtils.getFiscalYearForTesting()+"-01-01          ----------                                       "),
                new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, TestUtils.getFiscalYearForTesting()+"BL2331473-----8000---ACAS02DI  01NOFLEX005     00000GENERATED OFFSET                        +00000000000004000.00C"+formattedRunDate+"          ----------                                       "), };

        scrub(input);
        assertOriginEntries(4, output);
    }

    /**
     * Test it when the flag is on
     *
     * @throws Exception thrown if any exception is encountered for any reason
     */
     public void testFlexibleOffsetGeneration() throws Exception {

     resetFlexibleOffsetEnableFlag(true);

     updateDocTypeForScrubberOffsetGeneration();
     setOffsetAccounts();

     final java.sql.Date scrubberDate = new java.sql.Date((SpringContext.getBean(RunDateService.class).calculateRunDate(SpringContext.getBean(DateTimeService.class).getCurrentDate())).getTime());
     final String formattedRunDate = new SimpleDateFormat("yyyy-MM-dd").format(scrubberDate);

     String[] input = new String[] {
             TestUtils.getFiscalYearForTesting()+"BA9120656-----4190---ACEX02DI  01NOFLEX001     00000TEST FLEXIBLE OFFSET - NO FLEX                        2000.00D"+TestUtils.getFiscalYearForTesting()+"-01-01          ----------                                       ",
             TestUtils.getFiscalYearForTesting()+"BA6044900-----4190---ACEX02DI  01NOFLEX002     00000TEST FLEXIBLE OFFSET - FLEX                           1000.00D"+TestUtils.getFiscalYearForTesting()+"-01-01          ----------                                       ",
             TestUtils.getFiscalYearForTesting()+"BL1023200-----4190---ACEX02DI  01NOFLEX003     00000TEST FLEXIBLE OFFSET - FLEX                           3000.00D"+TestUtils.getFiscalYearForTesting()+"-01-01          ----------                                       ",
             TestUtils.getFiscalYearForTesting()+"BL1023200-----7030---ACEX02DI  01NOFLEX004     00000TEST FLEXIBLE OFFSET - FLEX                           3500.00D"+TestUtils.getFiscalYearForTesting()+"-01-01          ----------                                       ",
             TestUtils.getFiscalYearForTesting()+"BL2331473-----4190---ACEX02DI  01NOFLEX005     00000TEST FLEXIBLE OFFSET - FLEX                           4000.00D"+TestUtils.getFiscalYearForTesting()+"-01-01          ----------                                       ",
     };

     EntryHolder[] output = new EntryHolder[] {
     new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE,        TestUtils.getFiscalYearForTesting()+"BA9120656-----4190---ACEX02DI  01NOFLEX001     00000TEST FLEXIBLE OFFSET - NO FLEX                        2000.00D"+TestUtils.getFiscalYearForTesting()+"-01-01          ----------                                       "),
     new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE,        TestUtils.getFiscalYearForTesting()+"BA6044900-----4190---ACEX02DI  01NOFLEX002     00000TEST FLEXIBLE OFFSET - FLEX                           1000.00D"+TestUtils.getFiscalYearForTesting()+"-01-01          ----------                                       "),
     new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE,        TestUtils.getFiscalYearForTesting()+"BL1023200-----4190---ACEX02DI  01NOFLEX003     00000TEST FLEXIBLE OFFSET - FLEX                           3000.00D"+TestUtils.getFiscalYearForTesting()+"-01-01          ----------                                       "),
     new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE,        TestUtils.getFiscalYearForTesting()+"BL1023200-----7030---ACEX02DI  01NOFLEX004     00000TEST FLEXIBLE OFFSET - FLEX                           3500.00D"+TestUtils.getFiscalYearForTesting()+"-01-01          ----------                                       "),
     new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE,        TestUtils.getFiscalYearForTesting()+"BL2331473-----4190---ACEX02DI  01NOFLEX005     00000TEST FLEXIBLE OFFSET - FLEX                           4000.00D"+TestUtils.getFiscalYearForTesting()+"-01-01          ----------                                       "),
     new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, TestUtils.getFiscalYearForTesting()+"BA9120656-----4190---ACEX02DI  01NOFLEX001     00000TEST FLEXIBLE OFFSET - NO FLEX          +00000000000002000.00D"+TestUtils.getFiscalYearForTesting()+"-01-01          ----------                                       "),
     new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, TestUtils.getFiscalYearForTesting()+"BA9120656-----8000---ACAS02DI  01NOFLEX001     00000GENERATED OFFSET                        +00000000000002000.00C"+formattedRunDate+"          ----------                                       "),
     new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, TestUtils.getFiscalYearForTesting()+"BA6044900-----4190---ACEX02DI  01NOFLEX002     00000TEST FLEXIBLE OFFSET - FLEX             +00000000000001000.00D"+TestUtils.getFiscalYearForTesting()+"-01-01          ----------                                       "),
     new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, TestUtils.getFiscalYearForTesting()+"BL2231402-----8000---ACAS02DI  01NOFLEX002     00000GENERATED OFFSET                        +00000000000001000.00C"+formattedRunDate+"          ----------                                       "),
     new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, TestUtils.getFiscalYearForTesting()+"BL1023200-----4190---ACEX02DI  01NOFLEX003     00000TEST FLEXIBLE OFFSET - FLEX             +00000000000003000.00D"+TestUtils.getFiscalYearForTesting()+"-01-01          ----------                                       "),
     new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, TestUtils.getFiscalYearForTesting()+"BL1023200-----8000---ACAS02DI  01NOFLEX003     00000GENERATED OFFSET                        +00000000000003000.00C"+formattedRunDate+"          ----------                                       "),
     new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, TestUtils.getFiscalYearForTesting()+"BL9520004-----8611---ACAS02DI  01NOFLEX004     00000GENERATED CAPITALIZATION                +00000000000003500.00D"+TestUtils.getFiscalYearForTesting()+"-01-01          ----------                                       "),
     new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, TestUtils.getFiscalYearForTesting()+"BL2231419-----9899---ACFB02DI  01NOFLEX004     00000GENERATED CAPITALIZATION                +00000000000003500.00C"+TestUtils.getFiscalYearForTesting()+"-01-01          ----------                                       "),
     new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, TestUtils.getFiscalYearForTesting()+"BL1023200-----7030---ACEX02DI  01NOFLEX004     00000TEST FLEXIBLE OFFSET - FLEX             +00000000000003500.00D"+TestUtils.getFiscalYearForTesting()+"-01-01          ----------                                       "),
     new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, TestUtils.getFiscalYearForTesting()+"BL1023200-----8000---ACAS02DI  01NOFLEX004     00000GENERATED OFFSET                        +00000000000003500.00C"+formattedRunDate+"          ----------                                       "),
     new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, TestUtils.getFiscalYearForTesting()+"BL2331473-----4190---ACEX02DI  01NOFLEX005     00000TEST FLEXIBLE OFFSET - FLEX             +00000000000004000.00D"+TestUtils.getFiscalYearForTesting()+"-01-01          ----------                                       "),
     new EntryHolder(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE, TestUtils.getFiscalYearForTesting()+"BA9120657-----8000---ACAS02DI  01NOFLEX005     00000GENERATED OFFSET                        +00000000000004000.00C"+formattedRunDate+"          ----------                                       "),
     };

     // make sure DI's will generate offsets
     TestUtils.setSystemParameter(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.DOCUMENT_TYPES_REQUIRING_FLEXIBLE_OFFSET_BALANCING_ENTRIES, "DI");

     scrub(input);
     assertOriginEntries(4, output);
     }
    /**
     * Updates the DI   doc type, so that scrubber offsets are generated
     */
    private void updateDocTypeForScrubberOffsetGeneration() {
        String docTypeCode = "DI";
        final ParameterService parameterService = SpringContext.getBean(ParameterService.class);
        final ParameterEvaluator evaluator = /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.OFFSET_DOC_TYPE_CODES, docTypeCode);
        final String parameterValue = evaluator.getValue();
        if (evaluator.constraintIsAllow()) {
            if (!parameterValue.matches("^"+docTypeCode+"$|^"+docTypeCode+";|;"+docTypeCode+";|;"+docTypeCode+"$")) {
                final String newParameterValue = parameterValue + ";" + docTypeCode;
                TestUtils.setSystemParameter(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.OFFSET_DOC_TYPE_CODES, newParameterValue);
            }
        } else {
            if (parameterValue.matches("^"+docTypeCode+"$|^"+docTypeCode+";|;"+docTypeCode+";|;"+docTypeCode+"$")) {
                final String newParameterValue = parameterValue.replaceAll("^"+docTypeCode+"$|^"+docTypeCode+";|;"+docTypeCode+";|;"+docTypeCode+"$", ";");
                TestUtils.setSystemParameter(ScrubberStep.class, GeneralLedgerConstants.GlScrubberGroupRules.OFFSET_DOC_TYPE_CODES, newParameterValue);
            }
        }
    }

    /**
     * Creates offset account fixtures for the test
     */
    private void setOffsetAccounts() {
        unitTestSqlDao.sqlCommand("insert into FP_OFST_ACCT_T (FIN_COA_CD,OBJ_ID,ACCOUNT_NBR,FIN_OFST_OBJ_CD,FIN_OFST_COA_CD,FIN_OFST_ACCT_NBR) values ('BL','" + java.util.UUID.randomUUID().toString() + "','2331473','8000','BA','9120657')");
        unitTestSqlDao.sqlCommand("insert into FP_OFST_ACCT_T (FIN_COA_CD,OBJ_ID,ACCOUNT_NBR,FIN_OFST_OBJ_CD,FIN_OFST_COA_CD,FIN_OFST_ACCT_NBR) values ('BA','" + java.util.UUID.randomUUID().toString() + "','6044900','8000','BL','2231402')");
        unitTestSqlDao.sqlCommand("insert into FP_OFST_ACCT_T (FIN_COA_CD,OBJ_ID,ACCOUNT_NBR,FIN_OFST_OBJ_CD,FIN_OFST_COA_CD,FIN_OFST_ACCT_NBR) values ('BL','" + java.util.UUID.randomUUID().toString() + "','1023200','9040','BL','2231419')");
        unitTestSqlDao.sqlCommand("insert into FP_OFST_ACCT_T (FIN_COA_CD,OBJ_ID,ACCOUNT_NBR,FIN_OFST_OBJ_CD,FIN_OFST_COA_CD,FIN_OFST_ACCT_NBR) values ('BL','" + java.util.UUID.randomUUID().toString() + "','9520004','9899','BL','2231419')");
    }

    /**
     * Runs the scrubber on a given array of transactions
     *
     * @param inputTransactions String-formatted entries
     */
    private void scrub(String[] inputTransactions) {
        this.clearBatchFiles();
        loadInputTransactions(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE, inputTransactions);

        //we do not need to call clearCache() since no dao and jdbc calls mixted in this method.
        //refer to KFSMI-7637
        // persistenceService.clearCache();
        scrubberService.scrubEntries();
    }

    /**
     * Resets the parameter which controls flexible offset generation
     * @param toggle the value to toggle the parameter to
     */
    private void resetFlexibleOffsetEnableFlag(boolean toggle) {
       TestUtils.setSystemParameter(OffsetDefinition.class, KFSConstants.SystemGroupParameterNames.FLEXIBLE_OFFSET_ENABLED_FLAG, (toggle ? "Y" : "N"));
    }
}
