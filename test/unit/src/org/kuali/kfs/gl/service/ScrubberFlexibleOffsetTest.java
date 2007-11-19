/*
 * Copyright 2006-2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.gl.service;

import java.util.Calendar;

import org.kuali.core.util.Guid;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.bo.OffsetDefinition;
import org.kuali.module.gl.OriginEntryTestBase;
import org.kuali.module.gl.bo.OriginEntrySource;
import org.kuali.test.ConfigureContext;

/**
 * Test Flexible Offset in the scrubber
 */
@ConfigureContext
public class ScrubberFlexibleOffsetTest extends OriginEntryTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ScrubberFlexibleOffsetTest.class);

    private ScrubberService scrubberService;

    /**
     * Sets up the services neede for this test and also sets the date/time service's date time to one minute before midnight January 1, 2006
     * @see org.kuali.module.gl.OriginEntryTestBase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        scrubberService = SpringContext.getBean(ScrubberService.class);
        scrubberService.setDateTimeService(dateTimeService);
        // Get the test date time service so we can specify the date/time of the run
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.MONTH, Calendar.JANUARY);
        c.set(Calendar.YEAR, 2006);

        // since the cutoff time is set to 10am (KFSP1/Scrubber+cutoff+time+configuration)
        // we want to ensure that the time is always after that time so the cutoff algorithm is not invoked
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);

        date = c.getTime();
        dateTimeService.setCurrentDate(date);
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

        String[] input = new String[] { 
                "2007BA9120656-----4190---ACEX02DI  01NOFLEX001     00000TEST FLEXIBLE OFFSET - NO FLEX                    2000.00D2006-01-01          ----------                                       ", 
                "2007BA6044900-----4190---ACEX02DI  01NOFLEX002     00000TEST FLEXIBLE OFFSET - FLEX                       1000.00D2006-01-01          ----------                                       ", 
                "2007BL1023200-----4190---ACEX02DI  01NOFLEX003     00000TEST FLEXIBLE OFFSET - FLEX                       3000.00D2006-01-01          ----------                                       ", 
                "2007BL1023200-----7030---ACEX02DI  01NOFLEX004     00000TEST FLEXIBLE OFFSET - FLEX                       3500.00D2006-01-01          ----------                                       ", 
                "2007BL2331473-----4190---ACEX02DI  01NOFLEX005     00000TEST FLEXIBLE OFFSET - FLEX                       4000.00D2006-01-01          ----------                                       ", };

        EntryHolder[] output = new EntryHolder[] { 
                new EntryHolder(OriginEntrySource.BACKUP, "2007BA9120656-----4190---ACEX02DI  01NOFLEX001     00000TEST FLEXIBLE OFFSET - NO FLEX                    2000.00D2006-01-01          ----------                                       "), 
                new EntryHolder(OriginEntrySource.BACKUP, "2007BA6044900-----4190---ACEX02DI  01NOFLEX002     00000TEST FLEXIBLE OFFSET - FLEX                       1000.00D2006-01-01          ----------                                       "), 
                new EntryHolder(OriginEntrySource.BACKUP, "2007BL1023200-----4190---ACEX02DI  01NOFLEX003     00000TEST FLEXIBLE OFFSET - FLEX                       3000.00D2006-01-01          ----------                                       "), 
                new EntryHolder(OriginEntrySource.BACKUP, "2007BL1023200-----7030---ACEX02DI  01NOFLEX004     00000TEST FLEXIBLE OFFSET - FLEX                       3500.00D2006-01-01          ----------                                       "),
                new EntryHolder(OriginEntrySource.BACKUP, "2007BL2331473-----4190---ACEX02DI  01NOFLEX005     00000TEST FLEXIBLE OFFSET - FLEX                       4000.00D2006-01-01          ----------                                       "), 
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA9120656-----4190---ACEX02DI  01NOFLEX001     00000TEST FLEXIBLE OFFSET - NO FLEX                    2000.00D2006-01-01          ----------                                       "), 
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA9120656-----8000---ACAS02DI  01NOFLEX001     00000GENERATED OFFSET                                  2000.00C2006-01-01          ----------                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044900-----4190---ACEX02DI  01NOFLEX002     00000TEST FLEXIBLE OFFSET - FLEX                       1000.00D2006-01-01          ----------                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044900-----8000---ACAS02DI  01NOFLEX002     00000GENERATED OFFSET                                  1000.00C2006-01-01          ----------                                       "), 
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL1023200-----4190---ACEX02DI  01NOFLEX003     00000TEST FLEXIBLE OFFSET - FLEX                       3000.00D2006-01-01          ----------                                       "), 
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL1023200-----8000---ACAS02DI  01NOFLEX003     00000GENERATED OFFSET                                  3000.00C2006-01-01          ----------                                       "), 
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL9520004-----8611---ACAS02DI  01NOFLEX004     00000GENERATED CAPITALIZATION                          3500.00D2006-01-01          ----------                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL9520004-----9899---ACFB02DI  01NOFLEX004     00000GENERATED CAPITALIZATION                          3500.00C2006-01-01          ----------                                       "), 
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL1023200-----7030---ACEX02DI  01NOFLEX004     00000TEST FLEXIBLE OFFSET - FLEX                       3500.00D2006-01-01          ----------                                       "), 
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL1023200-----8000---ACAS02DI  01NOFLEX004     00000GENERATED OFFSET                                  3500.00C2006-01-01          ----------                                       "), 
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL2331473-----4190---ACEX02DI  01NOFLEX005     00000TEST FLEXIBLE OFFSET - FLEX                       4000.00D2006-01-01          ----------                                       "),
                new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL2331473-----8000---ACAS02DI  01NOFLEX005     00000GENERATED OFFSET                                  4000.00C2006-01-01          ----------                                       "), };

        scrub(input);
        assertOriginEntries(4, output);
    }

    ///**
    // * Test it when the flag is on
    // * 
    // * @throws Exception thrown if any exception is encountered for any reason
    // */
    // This test works in Eclipse, but not in Anthil
    // public void testFlexibleOffsetGeneration() throws Exception {
    //
    // resetFlexibleOffsetEnableFlag(true);
    //
    // updateDocTypeForScrubberOffsetGeneration();
    // setOffsetAccounts();
    //
    // String[] input = new String[] {
    // "2007BA9120656-----4190---ACEX02DI  01NOFLEX00100000TEST FLEXIBLE OFFSET - NO FLEX 2000.00D2006-01-01 ---------- ",
    // "2007BA6044900-----4190---ACEX02DI  01NOFLEX00200000TEST FLEXIBLE OFFSET - FLEX 1000.00D2006-01-01 ---------- ",
    // "2007BL1023200-----4190---ACEX02DI  01NOFLEX00300000TEST FLEXIBLE OFFSET - FLEX 3000.00D2006-01-01 ---------- ",
    // "2007BL1023200-----7030---ACEX02DI  01NOFLEX00400000TEST FLEXIBLE OFFSET - FLEX 3500.00D2006-01-01 ---------- ",
    // "2007BL2331473-----4190---ACEX02DI  01NOFLEX00500000TEST FLEXIBLE OFFSET - FLEX 4000.00D2006-01-01 ---------- ",
    // };
    //
    // EntryHolder[] output = new EntryHolder[] {
    // new EntryHolder(OriginEntrySource.BACKUP, "2007BA9120656-----4190---ACEX02DI  01NOFLEX00100000TEST FLEXIBLE OFFSET - NO FLEX
    // 2000.00D2006-01-01 ---------- "),
    // new EntryHolder(OriginEntrySource.BACKUP, "2007BA6044900-----4190---ACEX02DI  01NOFLEX00200000TEST FLEXIBLE OFFSET - FLEX
    // 1000.00D2006-01-01 ---------- "),
    // new EntryHolder(OriginEntrySource.BACKUP, "2007BL1023200-----4190---ACEX02DI  01NOFLEX00300000TEST FLEXIBLE OFFSET - FLEX
    // 3000.00D2006-01-01 ---------- "),
    // new EntryHolder(OriginEntrySource.BACKUP, "2007BL1023200-----7030---ACEX02DI  01NOFLEX00400000TEST FLEXIBLE OFFSET - FLEX
    // 3500.00D2006-01-01 ---------- "),
    // new EntryHolder(OriginEntrySource.BACKUP, "2007BL2331473-----4190---ACEX02DI  01NOFLEX00500000TEST FLEXIBLE OFFSET - FLEX
    // 4000.00D2006-01-01 ---------- "),
    // new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA9120656-----4190---ACEX02DI  01NOFLEX00100000TEST FLEXIBLE OFFSET -
    // NO FLEX 2000.00D2006-01-01 ---------- "),
    // new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA9120656-----8000---ACAS02DI  01NOFLEX00100000GENERATED OFFSET
    // 2000.00C2006-01-01 ---------- "),
    // new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA6044900-----4190---ACEX02DI  01NOFLEX00200000TEST FLEXIBLE OFFSET -
    // FLEX 1000.00D2006-01-01 ---------- "),
    // new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL2231402-----8000---ACAS02DI  01NOFLEX00200000GENERATED OFFSET
    // 1000.00C2006-01-01 ---------- "),
    // new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL1023200-----4190---ACEX02DI  01NOFLEX00300000TEST FLEXIBLE OFFSET -
    // FLEX 3000.00D2006-01-01 ---------- "),
    // new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL1023200-----8000---ACAS02DI  01NOFLEX00300000GENERATED OFFSET
    // 3000.00C2006-01-01 ---------- "),
    // new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL9520004-----8611---ACAS02DI  01NOFLEX00400000GENERATED
    // CAPITALIZATION 3500.00D2006-01-01 ---------- "),
    // new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL2231419-----9899---ACFB02DI  01NOFLEX00400000GENERATED
    // CAPITALIZATION 3500.00C2006-01-01 ---------- "),
    // new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL1023200-----7030---ACEX02DI  01NOFLEX00400000TEST FLEXIBLE OFFSET -
    // FLEX 3500.00D2006-01-01 ---------- "),
    // new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL1023200-----8000---ACAS02DI  01NOFLEX00400000GENERATED OFFSET
    // 3500.00C2006-01-01 ---------- "),
    // new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BL2331473-----4190---ACEX02DI  01NOFLEX00500000TEST FLEXIBLE OFFSET -
    // FLEX 4000.00D2006-01-01 ---------- "),
    // new EntryHolder(OriginEntrySource.SCRUBBER_VALID, "2007BA9120657-----8000---ACAS02DI  01NOFLEX00500000GENERATED OFFSET
    // 4000.00C2006-01-01 ---------- "),
    // };
    //
    // scrub(input);
    // assertOriginEntries(4, output);
    // }
    /**
     * Updates the DI   doc type, so that scrubber offsets are generated
     */
    private void updateDocTypeForScrubberOffsetGeneration() {
        unitTestSqlDao.sqlCommand("update fp_doc_type_t set TRN_SCRBBR_OFST_GEN_IND = 'Y' where fdoc_typ_cd = 'DI'");
    }

    /**
     * Creates offset account fixtures for the test
     */
    private void setOffsetAccounts() {
        unitTestSqlDao.sqlCommand("insert into FP_OFST_ACCT_T (FIN_COA_CD,OBJ_ID,ACCOUNT_NBR,FIN_OFST_OBJ_CD,FIN_OFST_COA_CD,FIN_OFST_ACCT_NBR) values ('BL','" + new Guid().toString() + "','2331473','8000','BA','9120657')");
        unitTestSqlDao.sqlCommand("insert into FP_OFST_ACCT_T (FIN_COA_CD,OBJ_ID,ACCOUNT_NBR,FIN_OFST_OBJ_CD,FIN_OFST_COA_CD,FIN_OFST_ACCT_NBR) values ('BA','" + new Guid().toString() + "','6044900','8000','BL','2231402')");
        unitTestSqlDao.sqlCommand("insert into FP_OFST_ACCT_T (FIN_COA_CD,OBJ_ID,ACCOUNT_NBR,FIN_OFST_OBJ_CD,FIN_OFST_COA_CD,FIN_OFST_ACCT_NBR) values ('BL','" + new Guid().toString() + "','1023200','9040','BL','2231419')");
        unitTestSqlDao.sqlCommand("insert into FP_OFST_ACCT_T (FIN_COA_CD,OBJ_ID,ACCOUNT_NBR,FIN_OFST_OBJ_CD,FIN_OFST_COA_CD,FIN_OFST_ACCT_NBR) values ('BL','" + new Guid().toString() + "','9520004','9899','BL','2231419')");
    }

    /**
     * Runs the scrubber on a given array of transactions
     * 
     * @param inputTransactions String-formatted entries
     */
    private void scrub(String[] inputTransactions) {
        clearOriginEntryTables();
        loadInputTransactions(OriginEntrySource.BACKUP, inputTransactions, date);
        persistenceService.clearCache();
        scrubberService.scrubEntries();
    }
}
