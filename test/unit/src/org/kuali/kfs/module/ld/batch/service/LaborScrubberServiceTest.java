/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.labor;

import java.util.Calendar;
import java.util.List;

import org.kuali.core.service.PersistenceService;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.context.TestUtils;
import org.kuali.kfs.util.Message;
import org.kuali.module.gl.bo.OriginEntrySource;
import org.kuali.module.gl.bo.UniversityDate;
import org.kuali.module.gl.dao.UniversityDateDao;
import org.kuali.module.gl.service.ScrubberValidator;
import org.kuali.module.labor.batch.LaborScrubberStep;
import org.kuali.module.labor.bo.LaborOriginEntry;
import org.kuali.module.labor.service.LaborScrubberService;
import org.kuali.test.ConfigureContext;


@ConfigureContext
public class LaborScrubberServiceTest extends LaborOriginEntryTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborScrubberServiceTest.class);

    private LaborScrubberService laborScrubberService = null;
    private ScrubberValidator laborScrubberValidator = null;
    private UniversityDate universityRunDate = null;
    private UniversityDateDao universityDateDao = null;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        LOG.debug("setUp() started");

        laborScrubberService = SpringContext.getBean(LaborScrubberService.class);
        laborScrubberService.setDateTimeService(dateTimeService);
        persistenceService = SpringContext.getBean(PersistenceService.class);
        laborScrubberValidator = SpringContext.getBean(ScrubberValidator.class);
        universityDateDao = SpringContext.getBean(UniversityDateDao.class);

        // Get the test date time service so we can specify the date/time of the run
        Calendar c = Calendar.getInstance();
        /*c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.MONTH, Calendar.JANUARY);
        c.set(Calendar.YEAR, 2006);

        // since the cutoff time is set to 10am (KFSP1/Scrubber+cutoff+time+configuration)
        // we want to ensure that the time is always after that time so the cutoff algorithm is not invoked
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);*/

        date = c.getTime();
        dateTimeService.setCurrentDate(date);
        

        universityRunDate = universityDateDao.getByPrimaryKey(date);
        
    }


    public void testDemerger() throws Exception {
        String[] inputTransactions = { "2026BA6044900-----2400---ACEX06BT  PLM01010207     0000100009529----------KUALI TEST DESCRIPTION                  +000000000003493.50D2006-12-22                                                 2006-12-222006-12-31000168.002007060000149952 001REGS12PAE 11 M001010207                     IU IUBLA", "2007BA6044900-----2400---ACEX06BT  PLM01010207     0000100009529----------KUALI TEST DESCRIPTION                  +000000000003493.50C2006-12-22                                                 2006-12-222006-12-31000168.002007060000149952 001REGS12PAE 11 M001010207                     IU IUBLA", "2026BA6044900-----2400---ACEX06ST  PLM01010207     0000100009529----------KUALI TEST DESCRIPTION                  +000000000003493.50C2006-12-22                                                 2006-12-222006-12-31000168.002007060000149952 001REGS12PAE 11 M001010207                     IU IUBLA",
                "2007BA6044900-----2400---ACEX06ST  PLM01010207     0000100009529----------KUALI TEST DESCRIPTION                  +000000000003493.50C2006-12-22                                                 2006-12-222006-12-31000168.002007060000149952 001REGS12PAE 11 M001010207                     IU IUBLA" };

        EntryHolder[] outputTransactions = { new EntryHolder(OriginEntrySource.LABOR_BACKUP, inputTransactions[0]), new EntryHolder(OriginEntrySource.LABOR_BACKUP, inputTransactions[1]), new EntryHolder(OriginEntrySource.LABOR_BACKUP, inputTransactions[2]), new EntryHolder(OriginEntrySource.LABOR_BACKUP, inputTransactions[3]), new EntryHolder(OriginEntrySource.LABOR_SCRUBBER_ERROR, inputTransactions[0]), new EntryHolder(OriginEntrySource.LABOR_SCRUBBER_ERROR, inputTransactions[1]), new EntryHolder(OriginEntrySource.LABOR_SCRUBBER_ERROR, inputTransactions[2]), new EntryHolder(OriginEntrySource.LABOR_SCRUBBER_ERROR, inputTransactions[3]) };


        scrub(inputTransactions);
        assertOriginEntries(4, outputTransactions);

    }


    public void testValidEntries() throws Exception {
        String[] inputTransactions = { "2007BA6044900-----2400---ACEX06PAY PLM01010207     0000100009529----------KUALI TEST DESCRIPTION                  +000000000003493.50D2006-12-22                                                 2006-12-222006-12-31000168.002007060000149952 001REGS12PAE 11 M001010207                     IU IUBLA", "2007BA6044900-----2400---ACEX06PAY PLM01010207     0000200014789----------KUALI TEST DESCRIPTION                  +000000000003329.27D2006-12-22                                                 2006-12-222006-12-31000082.322007060000649044 000REGS12PAE 16 M001010207                     IU IUBLA", "2007BA6044900-----2400---ACEX06PAY PLM01010207     0000300015213----------KUALI TEST DESCRIPTION                  +000000000002716.89D2006-12-22                                                 2006-12-222006-12-31000084.862007060000683206 000REGS12PAE 13 M001010207                     IU IUBLA",
                "2007BA6044900-----2400---ACEX06PAY PLM01010207     0000400017659----------KUALI TEST DESCRIPTION                  +000000000001620.08D2006-12-22                                                 2006-12-222006-12-31000041.382007060001316908 000REGS12PAE 13 M001010207                     IU IUBLA", "2007BA6044900-----2400---ACEX06PAY PLM01010207     0000500019196----------KUALI TEST DESCRIPTION                  +000000000005106.15D2006-12-22                                                 2006-12-222006-12-31000104.932007060001368813 000REGS12PAE 19 M001010207                     IU IUBLA", "2007BA6044900-----2400---ACEX06PAY PLM01010207     0000600022120----------KUALI TEST DESCRIPTION                  +000000000003071.10D2006-12-22                                                 2006-12-222006-12-31000106.752007060001773996 000REGS12PAE 12 M001010207                     IU IUBLA",
                "2007BA6044900-----2400---ACEX07PAY PLM04013107     0000100009529----------KUALI TEST DESCRIPTION                  +000000000003493.50D2007-01-25                                                 2007-01-252007-01-31000184.002007070000149952 001REGS12PAE 11 M004013107                     IU IUBLA", "2007BA6044900-----2400---ACEX07PAY PLM04013107     0000200014789----------KUALI TEST DESCRIPTION                  +000000000003329.27D2007-01-25                                                 2007-01-252007-01-31000090.162007070000649044 000REGS12PAE 16 M004013107                     IU IUBLA", "2007BA6044900-----2400---ACEX07PAY PLM04013107     0000300015213----------KUALI TEST DESCRIPTION                  +000000000002716.89D2007-01-25                                                 2007-01-252007-01-31000092.942007070000683206 000REGS12PAE 13 M004013107                     IU IUBLA",
                "2007BA6044900-----2400---ACEX07PAY PLM04013107     0000400017659----------KUALI TEST DESCRIPTION                  +000000000001620.08D2007-01-25                                                 2007-01-252007-01-31000045.322007070001316908 000REGS12PAE 13 M004013107                     IU IUBLA" };

        EntryHolder[] outputTransactions = { new EntryHolder(OriginEntrySource.LABOR_BACKUP, inputTransactions[0]), new EntryHolder(OriginEntrySource.LABOR_BACKUP, inputTransactions[1]), new EntryHolder(OriginEntrySource.LABOR_BACKUP, inputTransactions[2]), new EntryHolder(OriginEntrySource.LABOR_BACKUP, inputTransactions[3]), new EntryHolder(OriginEntrySource.LABOR_BACKUP, inputTransactions[4]), new EntryHolder(OriginEntrySource.LABOR_BACKUP, inputTransactions[5]), new EntryHolder(OriginEntrySource.LABOR_BACKUP, inputTransactions[6]), new EntryHolder(OriginEntrySource.LABOR_BACKUP, inputTransactions[7]), new EntryHolder(OriginEntrySource.LABOR_BACKUP, inputTransactions[8]), new EntryHolder(OriginEntrySource.LABOR_BACKUP, inputTransactions[9]), new EntryHolder(OriginEntrySource.LABOR_SCRUBBER_VALID, inputTransactions[0]), new EntryHolder(OriginEntrySource.LABOR_SCRUBBER_VALID, inputTransactions[1]), new EntryHolder(OriginEntrySource.LABOR_SCRUBBER_VALID, inputTransactions[2]),
                new EntryHolder(OriginEntrySource.LABOR_SCRUBBER_VALID, inputTransactions[3]), new EntryHolder(OriginEntrySource.LABOR_SCRUBBER_VALID, inputTransactions[4]), new EntryHolder(OriginEntrySource.LABOR_SCRUBBER_VALID, inputTransactions[5]), new EntryHolder(OriginEntrySource.LABOR_SCRUBBER_VALID, inputTransactions[6]), new EntryHolder(OriginEntrySource.LABOR_SCRUBBER_VALID, inputTransactions[7]), new EntryHolder(OriginEntrySource.LABOR_SCRUBBER_VALID, inputTransactions[8]), new EntryHolder(OriginEntrySource.LABOR_SCRUBBER_VALID, inputTransactions[9]), };


        scrub(inputTransactions);
        assertOriginEntries(4, outputTransactions);

    }

    public void testBlankFiscalYear() throws Exception {
        String[] inputTransactions = { "    BA6044900-----2400---ACEX06PAY PLBLANKFISC     0000100009529----------KUALI TEST DESCRIPTION                  +000000000003493.50D2006-12-22                                                 2006-12-222006-12-31000168.002007060000149952 001REGS12PAE 11 M001010207                     IU IUBLA" };
        EntryHolder[] outputTransactions = { new EntryHolder(OriginEntrySource.LABOR_BACKUP, inputTransactions[0]), new EntryHolder(OriginEntrySource.LABOR_SCRUBBER_VALID, inputTransactions[0]) };

        scrub(inputTransactions);
        assertOriginEntries(4, outputTransactions);

    }


    public void testInvalidObjectCode() throws Exception {
        String[] inputTransactions = { "2000BA6044906-----2400---ACEX06PAY PLCLOSEFISC     0000300015213----------KUALI TEST DESCRIPTION                  +000000000002716.89D2006-12-22                                                 2006-12-222006-12-31000084.862007060000683206 000REGS12PAE 13 M001010207                     IU IUBLA", "2000BA6044906-----2400---ACEX06PAY PLCLOSEFISC     0000400017659----------KUALI TEST DESCRIPTION                  +000000000001620.08D2006-12-22                                                 2006-12-222006-12-31000041.382007060001316908 000REGS12PAE 13 M001010207                     IU IUBLA" };
        EntryHolder[] outputTransactions = { new EntryHolder(OriginEntrySource.LABOR_BACKUP, inputTransactions[0]), new EntryHolder(OriginEntrySource.LABOR_BACKUP, inputTransactions[1]), new EntryHolder(OriginEntrySource.LABOR_SCRUBBER_ERROR, inputTransactions[0]), new EntryHolder(OriginEntrySource.LABOR_SCRUBBER_ERROR, inputTransactions[1]) };

        scrub(inputTransactions);
        assertOriginEntries(4, outputTransactions);

    }

    public void testInvalidFiscalYear() throws Exception {
        String[] inputTransactions = { "2026BA6044913-----2400---ACEX06PAY PLINVALFISC     0000500019196----------KUALI TEST DESCRIPTION                  +000000000005106.15D2006-12-22                                                 2006-12-222006-12-31000104.932007060001368813 000REGS12PAE 19 M001010207                     IU IUBLA", "2026BA6044913-----2400---ACEX06PAY PLINVALFISC     0000600022120----------KUALI TEST DESCRIPTION                  +000000000003071.10D2006-12-22                                                 2006-12-222006-12-31000106.752007060001773996 000REGS12PAE 12 M001010207                     IU IUBLA" };
        EntryHolder[] outputTransactions = { new EntryHolder(OriginEntrySource.LABOR_BACKUP, inputTransactions[0]), new EntryHolder(OriginEntrySource.LABOR_BACKUP, inputTransactions[1]), new EntryHolder(OriginEntrySource.LABOR_SCRUBBER_ERROR, inputTransactions[0]), new EntryHolder(OriginEntrySource.LABOR_SCRUBBER_ERROR, inputTransactions[1]) };

        scrub(inputTransactions);
        assertOriginEntries(4, outputTransactions);

    }

    public void testInvalid() throws Exception {
        String[] inputTransactions = { "2007  1031400-----2400---ACEX07PAY PLBLANKCHAR     0000100009529----------KUALI TEST DESCRIPTION                  +000000000003493.50D2007-01-25                                                 2007-01-252007-01-31000184.002007070000149952 001REGS12PAE 11 M004013107                     IU IUBLA", "2007  1031400-----2400---ACEX07PAY PLBLANKCHAR     0000200014789----------KUALI TEST DESCRIPTION                  +000000000003329.27D2007-01-25                                                 2007-01-252007-01-31000090.162007070000649044 000REGS12PAE 16 M004013107                     IU IUBLA" };
        EntryHolder[] outputTransactions = { new EntryHolder(OriginEntrySource.LABOR_BACKUP, inputTransactions[0]), new EntryHolder(OriginEntrySource.LABOR_BACKUP, inputTransactions[1]), new EntryHolder(OriginEntrySource.LABOR_SCRUBBER_ERROR, inputTransactions[0]), new EntryHolder(OriginEntrySource.LABOR_SCRUBBER_ERROR, inputTransactions[1]) };

        scrub(inputTransactions);
        assertOriginEntries(4, outputTransactions);

    }

    public void testInvalidDebitCreditCode() throws Exception {
        String[] inputTransactions = { "2007BA6044900-----2400---ACEX06PAY PLM01010207     0000100009529----------KUALI TEST DESCRIPTION                  +000000000003493.50X2006-12-22                                                 2006-12-222006-12-31000168.002007060000149952 001REGS12PAE 11 M001010207                     IU IUBLA", "2007BA6044900-----2400---ACEX06PAY PLM01010207     0000100009529----------KUALI TEST DESCRIPTION                  +000000000003495.50X2006-12-25                                                 2006-12-222006-12-31000168.002007060000149952 001REGS12PAE 11 M001010207                     IU IUBLA" };

        EntryHolder[] outputTransactions = { new EntryHolder(OriginEntrySource.LABOR_BACKUP, inputTransactions[0]), new EntryHolder(OriginEntrySource.LABOR_BACKUP, inputTransactions[1]), new EntryHolder(OriginEntrySource.LABOR_SCRUBBER_ERROR, inputTransactions[0]), new EntryHolder(OriginEntrySource.LABOR_SCRUBBER_ERROR, inputTransactions[1]) };

        scrub(inputTransactions);
        assertOriginEntries(4, outputTransactions);
    }

    public void testA2balanceTypeAcceptClosedFiscalPeriod() throws Exception {
        String[] inputTransactions = { "2005BL1031400-----5772---A2EX06PAYEPLPRENC-07      00002MTFRING ----------KUALI TEST DESCRIPTION                  +000000000028988.60C2006-12-14                                                D2006-12-14          000000.00200706-----------000             M037113006                             " };

        EntryHolder[] outputTransactions = { new EntryHolder(OriginEntrySource.LABOR_BACKUP, inputTransactions[0]), new EntryHolder(OriginEntrySource.LABOR_SCRUBBER_VALID, inputTransactions[0]) };

        scrub(inputTransactions);
        assertOriginEntries(4, outputTransactions);
    }
    
    public void testValidateAccountWithAllParametersOff() throws Exception {

        TestUtils tu = new TestUtils();
        
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.SUBFUND_WAGE_EXCLUSION_PARAMETER, "N");
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.ACCOUNT_FRINGE_EXCLUSION_PARAMETER, "N");
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.CONTINUATION_ACCOUNT_LOGIC_PARAMETER, "N");
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.SUSPENSE_ACCOUNT_LOGIC_PARAMETER, "N");
        // This entry is expired or closed.
        // If all parameters are off then it should be stored in two groups, LSCV and LSCX
        // There should be an error message from continuation account logic, but it is not fatal so the entry will be stored in
        // LSCV.
        String[] inputTransactions = { "2005BL2131401-----5772---ACEX08PAYEPLPRENC-07      00002MTFRING ----------KUALI TEST DESCRIPTION                  +000000000028988.60C2006-12-14                                                D2006-12-14          000000.00200706-----------000             M037113006                             " };

        EntryHolder[] outputTransactions = { new EntryHolder(OriginEntrySource.LABOR_BACKUP, inputTransactions[0]), new EntryHolder(OriginEntrySource.LABOR_SCRUBBER_VALID, inputTransactions[0]), new EntryHolder(OriginEntrySource.LABOR_SCRUBBER_EXPIRED, inputTransactions[0]) };

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
        // This entry is expired or closed.
        // This entry will be saved in LSCE and LSCX because it has special origination codes that have override ability and it cannot be closed
        
        String[] inputTransactions = { "2005BL2131401-----5772---ACEX08PAYEPLPRENC-07      00002MTFRING ----------KUALI TEST DESCRIPTION                  +000000000028988.60C2006-12-14                                                D2006-12-14          000000.00200706-----------000             M037113006                             " };

        EntryHolder[] outputTransactions = { new EntryHolder(OriginEntrySource.LABOR_BACKUP, inputTransactions[0]), new EntryHolder(OriginEntrySource.LABOR_SCRUBBER_ERROR, inputTransactions[0]), new EntryHolder(OriginEntrySource.LABOR_SCRUBBER_EXPIRED, inputTransactions[0]) };

        scrub(inputTransactions);
        assertOriginEntries(4, outputTransactions);
        
    }
    
    // All parameters are off, except a parameter for continuation account.
    // Test with closed account. 
    public void testValidateAccountWithOnlyContinuationAccountOn() throws Exception {

        TestUtils tu = new TestUtils();
        
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.SUBFUND_WAGE_EXCLUSION_PARAMETER, "N");
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.ACCOUNT_FRINGE_EXCLUSION_PARAMETER, "N");
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.CONTINUATION_ACCOUNT_LOGIC_PARAMETER, "Y");
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.SUSPENSE_ACCOUNT_LOGIC_PARAMETER, "N");
        // This entry is expired or closed.
        // This entry will be saved in LSCV. 
        
        String[] inputTransactions = { "2005BL2131401-----5772---ACEX08PAYE99PRENC-07      00002MTFRING ----------KUALI TEST DESCRIPTION                  +000000000028988.60C2006-12-14                                                D2006-12-14          000000.00200706-----------000             M037113006                             " };

        EntryHolder[] outputTransactions = { new EntryHolder(OriginEntrySource.LABOR_BACKUP, inputTransactions[0]), new EntryHolder(OriginEntrySource.LABOR_SCRUBBER_VALID, inputTransactions[0]), new EntryHolder(OriginEntrySource.LABOR_SCRUBBER_EXPIRED, inputTransactions[0]) };

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
        
        String[] inputTransactions = { "2005BL2131401-----5772---ACEX08PAYE99PRENC-07      00002MTFRING ----------KUALI TEST DESCRIPTION                  +000000000028988.60C2006-12-14                                                D2006-12-14          000000.00200706-----------000             M037113006                             " };

        EntryHolder[] outputTransactions = { new EntryHolder(OriginEntrySource.LABOR_BACKUP, inputTransactions[0]), new EntryHolder(OriginEntrySource.LABOR_SCRUBBER_VALID, inputTransactions[0]), new EntryHolder(OriginEntrySource.LABOR_SCRUBBER_EXPIRED, inputTransactions[0])};

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
             
        String[] inputTransactions = { "2005EA6867070-----5772---ACEX08PAYE99PRENC-07      00002MTFRING ----------KUALI TEST DESCRIPTION                  +000000000028988.60C2006-12-14                                                D2006-12-14          000000.00200706-----------000             M037113006                             " };

        EntryHolder[] outputTransactions = { new EntryHolder(OriginEntrySource.LABOR_BACKUP, inputTransactions[0]), new EntryHolder(OriginEntrySource.LABOR_SCRUBBER_VALID, inputTransactions[0]), new EntryHolder(OriginEntrySource.LABOR_SCRUBBER_EXPIRED, inputTransactions[0])};

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
        
        String[] inputTransactions = { "2005UA6812756-----5772---ACEX08PAYE99PRENC-07      00002MTFRING ----------KUALI TEST DESCRIPTION                  +000000000028988.60C2006-12-14                                                D2006-12-14          000000.00200706-----------000             M037113006                             " };

        EntryHolder[] outputTransactions = { new EntryHolder(OriginEntrySource.LABOR_BACKUP, inputTransactions[0]), new EntryHolder(OriginEntrySource.LABOR_SCRUBBER_VALID, inputTransactions[0])};

        scrub(inputTransactions);
        assertOriginEntries(4, outputTransactions);
        
    }
    
    
    
    
    public void testNonFringeNonAltNonClosed() throws Exception {

        TestUtils tu = new TestUtils();
        
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.SUBFUND_WAGE_EXCLUSION_PARAMETER, "Y");
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.ACCOUNT_FRINGE_EXCLUSION_PARAMETER, "Y");
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.CONTINUATION_ACCOUNT_LOGIC_PARAMETER, "Y");
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.SUSPENSE_ACCOUNT_LOGIC_PARAMETER, "Y");
        // This entry is expired or closed, but, this account doesn't have closed indicator, so it is stored in LSCV.
        // This entry accept sunfundwage, and not accept Fringe
        // Chart code: BL, Object code: 5760, Fiscal Year: 2006
        // It doesn't have alternative fringe account, so it uses suspense account. 
        // This entry will be changed   2006UA6812756-----5760---ACEX08PAYE99PRENC-07      00002MTFRING ----------KUALI TEST DESCRIPTION                             28988.60C2006-12-14                                                 2006-12-14          0        200706-----------0               M037113006                             

        String[] inputTransactions = { "2006BL2631476-----5760---ACEX08PAYE99PRENC-07      00002MTFRING ----------KUALI TEST DESCRIPTION                  +000000000028988.60C2006-12-14                                                D2006-12-14          000000.00200706-----------000             M037113006                             " };

        EntryHolder[] outputTransactions = { new EntryHolder(OriginEntrySource.LABOR_BACKUP, inputTransactions[0]), new EntryHolder(OriginEntrySource.LABOR_SCRUBBER_VALID, inputTransactions[0])};

        scrub(inputTransactions);
        assertOriginEntries(4, outputTransactions);
        
    }
    
    public void testNonFringeNonAltNonClosedIndicatorWithSuspenseParameterOff() throws Exception {

        TestUtils tu = new TestUtils();
        
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.SUBFUND_WAGE_EXCLUSION_PARAMETER, "Y");
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.ACCOUNT_FRINGE_EXCLUSION_PARAMETER, "Y");
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.CONTINUATION_ACCOUNT_LOGIC_PARAMETER, "Y");
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.SUSPENSE_ACCOUNT_LOGIC_PARAMETER, "N");
        // This entry is expired or closed, but, this account doesn't have closed indicator, so it is stored in LSCV.
        // This entry accept sunfundwage, and not accept Fringe
        // Chart code: BL, Object code: 5760, Fiscal Year: 2006
        // It doesn't have alternative fringe account, so it uses suspense account.
        // But, since the parameter for suspense account indicator is off, this entry will be saved in LSCE.
        
        String[] inputTransactions = { "2006BL2631476-----5760---ACEX08PAYE99PRENC-07      00002MTFRING ----------KUALI TEST DESCRIPTION                  +000000000028988.60C2006-12-14                                                D2006-12-14          000000.00200706-----------000             M037113006                             " };

        EntryHolder[] outputTransactions = { new EntryHolder(OriginEntrySource.LABOR_BACKUP, inputTransactions[0]), new EntryHolder(OriginEntrySource.LABOR_SCRUBBER_ERROR, inputTransactions[0])};

        scrub(inputTransactions);
        assertOriginEntries(4, outputTransactions);
        
    }
    
    public void testNonFringeNonAltNonClosedIndicatorWithFringeParameterOff() throws Exception {

        TestUtils tu = new TestUtils();
        
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.SUBFUND_WAGE_EXCLUSION_PARAMETER, "Y");
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.ACCOUNT_FRINGE_EXCLUSION_PARAMETER, "N");
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.CONTINUATION_ACCOUNT_LOGIC_PARAMETER, "Y");
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.SUSPENSE_ACCOUNT_LOGIC_PARAMETER, "Y");
        // This entry is expired or closed, but this account doesn't have closed indicator, so it is stored in LSCV.
        // This entry accept sunfundwage, and not accept Fringe
        // Chart code: BL, Object code: 5760, Fiscal Year: 2006
        // Since fringe Parameter is off, this entry uses continuation account. 
        // This entry will be changed to 2006BL2331489-----5760---ACEX08PAYE99PRENC-07      00002MTFRING ----------AUTO FRBL2631476KUALI TEST DESCRIPTION             28988.60C2006-12-14                                                 2006-12-14          0        200706-----------0               M037113006                             
        
        String[] inputTransactions = { "2006BL2631476-----5760---ACEX08PAYE99PRENC-07      00002MTFRING ----------KUALI TEST DESCRIPTION                  +000000000028988.60C2006-12-14                                                D2006-12-14          000000.00200706-----------000             M037113006                             " };

        EntryHolder[] outputTransactions = { new EntryHolder(OriginEntrySource.LABOR_BACKUP, inputTransactions[0]), new EntryHolder(OriginEntrySource.LABOR_SCRUBBER_VALID, inputTransactions[0])};

        scrub(inputTransactions);
        assertOriginEntries(4, outputTransactions);
        
    }
    
    public void testClosedAccountWithAllParemetersOff() throws Exception {

        TestUtils tu = new TestUtils();
        
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.SUBFUND_WAGE_EXCLUSION_PARAMETER, "N");
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.ACCOUNT_FRINGE_EXCLUSION_PARAMETER, "N");
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.CONTINUATION_ACCOUNT_LOGIC_PARAMETER, "N");
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.SUSPENSE_ACCOUNT_LOGIC_PARAMETER, "N");
        // This entry is expired or closed, so this entry will be stored in LSCX.
        // Tested with all parameters off, so this entry will be stored in LSCV.
        String[] inputTransactions = { "2006EA6867070-----5760---ACEX08PAYE99PRENC-07      00002MTFRING ----------KUALI TEST DESCRIPTION                  +000000000028988.60C2006-12-14                                                D2006-12-14          000000.00200706-----------000             M037113006                             " };

        EntryHolder[] outputTransactions = { new EntryHolder(OriginEntrySource.LABOR_BACKUP, inputTransactions[0]), new EntryHolder(OriginEntrySource.LABOR_SCRUBBER_VALID, inputTransactions[0]), new EntryHolder(OriginEntrySource.LABOR_SCRUBBER_EXPIRED, inputTransactions[0])};

        scrub(inputTransactions);
        assertOriginEntries(4, outputTransactions);
        
    }
    
    public void testClosedAccountWithAllParemetersOffOnlyContinuationOn() throws Exception {

        TestUtils tu = new TestUtils();
        
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.SUBFUND_WAGE_EXCLUSION_PARAMETER, "N");
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.ACCOUNT_FRINGE_EXCLUSION_PARAMETER, "N");
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.CONTINUATION_ACCOUNT_LOGIC_PARAMETER, "Y");
        TestUtils.setSystemParameter(LaborScrubberStep.class, LaborConstants.Scrubber.SUSPENSE_ACCOUNT_LOGIC_PARAMETER, "N");
        // This entry is expired or closed, so this entry will be stored in LSCX.
        // Tested with only continuation parameter on, so this entry will be stored in LSCV,
        // and will be changed to       2006EA9567000-----5760---ACEX08PAYE99PRENC-07      00002MTFRING ----------AUTO FREA6867070KUALI TEST DESCRIPTION             28988.60C2006-12-14                                                 2006-12-14          0        200706-----------0               M037113006                             

        String[] inputTransactions = { "2006EA6867070-----5760---ACEX08PAYE99PRENC-07      00002MTFRING ----------KUALI TEST DESCRIPTION                  +000000000028988.60C2006-12-14                                                D2006-12-14          000000.00200706-----------000             M037113006                             " };

        EntryHolder[] outputTransactions = { new EntryHolder(OriginEntrySource.LABOR_BACKUP, inputTransactions[0]), new EntryHolder(OriginEntrySource.LABOR_SCRUBBER_VALID, inputTransactions[0]), new EntryHolder(OriginEntrySource.LABOR_SCRUBBER_EXPIRED, inputTransactions[0])};

        scrub(inputTransactions);
        assertOriginEntries(4, outputTransactions);
        
    }
    


    private void scrub(String[] inputTransactions) {
        clearOriginEntryTables();
        loadInputTransactions(OriginEntrySource.LABOR_BACKUP, inputTransactions, date);
        persistenceService.clearCache();
        laborScrubberService.scrubEntries();
    }
  
}
