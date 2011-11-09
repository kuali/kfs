/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.kfs.gl.batch.service.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.batch.PosterBalancingStep;
import org.kuali.kfs.gl.businessobject.AccountBalance;
import org.kuali.kfs.gl.businessobject.AccountBalanceHistory;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.gl.businessobject.BalanceHistory;
import org.kuali.kfs.gl.businessobject.Encumbrance;
import org.kuali.kfs.gl.businessobject.EncumbranceHistory;
import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.gl.businessobject.EntryHistory;
import org.kuali.kfs.gl.dataaccess.AccountBalanceDao;
import org.kuali.kfs.gl.dataaccess.BalancingDao;
import org.kuali.kfs.gl.dataaccess.EncumbranceDao;
import org.kuali.kfs.gl.dataaccess.LedgerBalancingDao;
import org.kuali.kfs.gl.dataaccess.LedgerEntryHistoryBalancingDao;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.batch.BatchSpringContext;
import org.kuali.kfs.sys.batch.Step;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.rice.krad.service.BusinessObjectService;


/**
 * GL BalancingServiceImpl test cases
 */
@ConfigureContext
public class BalancingServiceImplTest extends BalancingServiceImplTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BalancingServiceImplTest.class);
    
    protected BalancingDao balancingDao;
    protected AccountBalanceDao accountBalanceDao;
    protected EncumbranceDao encumbranceDao;
    
    @Override
    protected void setUp() throws Exception {
        balancingService = (BalancingServiceBaseImpl<Entry, Balance>) TestUtils.getUnproxiedService("glBalancingService");
        ledgerEntryHistoryBalancingDao =  (LedgerEntryHistoryBalancingDao) SpringContext.getService("glEntryHistoryDao");
        ledgerBalancingDao = (LedgerBalancingDao) SpringContext.getService("glLedgerBalancingDao");
        
        balancingDao = (BalancingDao) SpringContext.getService("glBalancingDao");
        accountBalanceDao = SpringContext.getBean(AccountBalanceDao.class);
        encumbranceDao = SpringContext.getBean(EncumbranceDao.class);
        
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);

        // Delete all data so that balancing has an empty table set to work with
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        businessObjectService.deleteMatching(Entry.class, fieldValues);
        businessObjectService.deleteMatching(Balance.class, fieldValues);
        businessObjectService.deleteMatching(Encumbrance.class, fieldValues);
        businessObjectService.deleteMatching(AccountBalance.class, fieldValues);
        businessObjectService.deleteMatching(EntryHistory.class, fieldValues);
        businessObjectService.deleteMatching(BalanceHistory.class, fieldValues);
        businessObjectService.deleteMatching(EncumbranceHistory.class, fieldValues);
        businessObjectService.deleteMatching(AccountBalanceHistory.class, fieldValues);
        
        // Because KULDBA doesn't support FYs more then 1 year back we need to limit our range in order to properly test boundary cases
        TestUtils.setSystemParameter(PosterBalancingStep.class, GeneralLedgerConstants.Balancing.NUMBER_OF_PAST_FISCAL_YEARS_TO_INCLUDE, "0");
        
        // careful: super.setUp needs to happen at the end because of service initialization and NUMBER_OF_PAST_FISCAL_YEARS_TO_INCLUDE
        super.setUp();
    }
    
    @Override
    public void testRunBalancingPopulateData() {
        LOG.debug("No data present scenario, hence process should populate data");
        
        // First confirm tables are empty. If this fails then the test scenario is incorrectly set up
        assertEquals(0, getLedgerEntryBalancingDao().findCountGreaterOrEqualThan(startUniversityFiscalYear).intValue());
        assertEquals(0, getLedgerBalanceBalancingDao().findCountGreaterOrEqualThan(startUniversityFiscalYear).intValue());
        assertEquals(0, accountBalanceDao.findCountGreaterOrEqualThan(startUniversityFiscalYear).intValue());
        assertEquals(0, encumbranceDao.findCountGreaterOrEqualThan(startUniversityFiscalYear).intValue());
        assertEquals(0, this.getHistoryCount(null, EntryHistory.class));
        assertEquals(0, this.getHistoryCount(null, BalanceHistory.class));
        assertEquals(0, this.getHistoryCount(null, AccountBalanceHistory.class));
        assertEquals(0, this.getHistoryCount(null, EncumbranceHistory.class));
        
        // Generate some data and check that poster operated as expected. If this fails then the test scenario is incorrectly set up
        this.postTestCaseEntries(INPUT_TRANSACTIONS);
        assertEquals(12, getLedgerEntryBalancingDao().findCountGreaterOrEqualThan(startUniversityFiscalYear).intValue());
        assertEquals(7, getLedgerBalanceBalancingDao().findCountGreaterOrEqualThan(startUniversityFiscalYear).intValue());
        assertEquals(5, accountBalanceDao.findCountGreaterOrEqualThan(startUniversityFiscalYear).intValue());
        assertEquals(2, encumbranceDao.findCountGreaterOrEqualThan(startUniversityFiscalYear).intValue());
        
        // Balancing should succeed successfully since this is an expected use case where it does an initial population of history tables
        assertTrue(balancingService.runBalancing());
        
        // Once it ran, we expect data in balancing tables. This data is per INPUT_TRANSACTIONS data structure
        assertEquals(12, ledgerEntryHistoryBalancingDao.findSumRowCountGreaterOrEqualThan(startUniversityFiscalYear).intValue());
        assertEquals(9, this.getHistoryCount(null, EntryHistory.class));
        assertEquals(7, this.getHistoryCount(null, BalanceHistory.class));
        assertEquals(5, this.getHistoryCount(null, AccountBalanceHistory.class));
        assertEquals(2, this.getHistoryCount(null, EncumbranceHistory.class));
        
        // Finally make sure there wasn't any comparison failure. We do this by running comparison methods directly (again). Should pass
        // since we just populated the history tables this should hold true
        this.assertCompareHistorySuccess();
    }
    
    @Override
    public void testRunBalancingDeleteObsoleteUniversityFiscalYearData() {
        // Run the poster to pick up the last two entries which are out of range
        this.postTestCaseEntries(INPUT_TRANSACTIONS);
        
        // Manually populate our history tables and force entries to be picked up. This essentially does the same as testRunBalancingPopulateData
        assertTrue("Populate should have copied some data", 0 != ledgerBalancingDao.populateLedgerEntryHistory(obsoleteUniversityFiscalYear));
        assertTrue("Populate should have copied some data", 0 != ledgerBalancingDao.populateLedgerBalanceHistory(obsoleteUniversityFiscalYear));
        assertTrue("Populate should have copied some data", 0 != balancingDao.populateAccountBalancesHistory(obsoleteUniversityFiscalYear));
        assertTrue("Populate should have copied some data", 0 != balancingDao.populateEncumbranceHistory(obsoleteUniversityFiscalYear));
        
        // Pretty silly at this point, but lets double check that it copied the entries
        assertTrue("Found no EntryHistory", 0 != this.getHistoryCount(obsoleteUniversityFiscalYear, EntryHistory.class));
        assertTrue("Found no BalanceHistory", 0 != this.getHistoryCount(obsoleteUniversityFiscalYear, BalanceHistory.class));
        assertTrue("Found no AccountBalanceHistory", 0 != this.getHistoryCount(obsoleteUniversityFiscalYear, AccountBalanceHistory.class));
        assertTrue("Found no EncumbranceHistory", 0 != this.getHistoryCount(obsoleteUniversityFiscalYear, EncumbranceHistory.class));
        
        // Run Balancing, it should hit the case that deletes the obsolete entries. Coincidentally: This will also ignore the two out of range entries in
        // INPUT_TRANSACTIONS (the last two) but we're not actively testing for that here
        assertTrue(balancingService.runBalancing());
        
        // Verify that it deleted the entries
        assertEquals(0, this.getHistoryCount(obsoleteUniversityFiscalYear, EntryHistory.class));
        assertEquals(0, this.getHistoryCount(obsoleteUniversityFiscalYear, BalanceHistory.class));
        assertEquals(0, this.getHistoryCount(obsoleteUniversityFiscalYear, AccountBalanceHistory.class));
        assertEquals(0, this.getHistoryCount(obsoleteUniversityFiscalYear, EncumbranceHistory.class));
    }
    
    @Override
    public void testRunBalancingHistoryUpdate() {
        // First pass is exactly the same as testRunBalancingPopulateData. This serves to populate the tables
        this.postTestCaseEntries(INPUT_TRANSACTIONS);
        assertTrue("Populate should have copied some data", 0 != ledgerBalancingDao.populateLedgerEntryHistory(obsoleteUniversityFiscalYear));
        assertTrue("Populate should have copied some data", 0 != ledgerBalancingDao.populateLedgerBalanceHistory(obsoleteUniversityFiscalYear));
        assertTrue("Populate should have copied some data", 0 != balancingDao.populateAccountBalancesHistory(obsoleteUniversityFiscalYear));
        assertTrue("Populate should have copied some data", 0 != balancingDao.populateEncumbranceHistory(obsoleteUniversityFiscalYear));
        
        try {
            // Briefly sleep to ensure that we get unique timestamps on the rename step. This is obsolete since the input files will
            // be the same but done to avoid future bugs if there are changes to the test setup
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            fail("No reason that this job should have gotten interrupted.");
        }
        
        // Next we post exactly the same entries and verify data exists. Note, now we have more entries
        this.postTestCaseEntries(INPUT_TRANSACTIONS);
        assertEquals(24, getLedgerEntryBalancingDao().findCountGreaterOrEqualThan(startUniversityFiscalYear).intValue());
        assertEquals(7, getLedgerBalanceBalancingDao().findCountGreaterOrEqualThan(startUniversityFiscalYear).intValue());
        assertEquals(5, accountBalanceDao.findCountGreaterOrEqualThan(startUniversityFiscalYear).intValue());
        assertEquals(2, encumbranceDao.findCountGreaterOrEqualThan(startUniversityFiscalYear).intValue());
        
        // And run balancing again. Again, this should succeed
        assertTrue(balancingService.runBalancing());
        
        // Once it ran, we expect data in balancing tables, but different this time since the balancing job ran updates
        assertEquals(24, ledgerEntryHistoryBalancingDao.findSumRowCountGreaterOrEqualThan(startUniversityFiscalYear).intValue());
        assertEquals(9, this.getHistoryCount(null, EntryHistory.class));
        assertEquals(7, this.getHistoryCount(null, BalanceHistory.class));
        assertEquals(5, this.getHistoryCount(null, AccountBalanceHistory.class));
        assertEquals(2, this.getHistoryCount(null, EncumbranceHistory.class));
        
        // Finally make sure there wasn't any comparison failure. We do this by running comparison methods directly (again)
        this.assertCompareHistorySuccess();
    }
    
    /**
     * @see org.kuali.kfs.gl.batch.service.impl.BalancingServiceImplTestBase#postTestCaseEntries(java.lang.String[])
     */
    @Override
    protected void postTestCaseEntries(String[] inputTransactions) {
        // Write test file
        TestUtils.writeFile(this.getBatchFileDirectoryName() + File.separator + GeneralLedgerConstants.BatchFileSystem.POSTER_INPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION, inputTransactions);
        
        try {
            // Run the poster
            Step posterEntriesStep = BatchSpringContext.getStep("posterEntriesStep");
            assertTrue("posterEntriesStep should have succeeded", posterEntriesStep.execute(getClass().getName(), dateTimeService.getCurrentDate()));
            
            // Rename the file because that's what happens before the balancing job runs
            Step fileRenameStep = BatchSpringContext.getStep("fileRenameStep");
            assertTrue("fileRenameStep should have succeeded", fileRenameStep.execute(getClass().getName(), dateTimeService.getCurrentDate()));
        }
        catch (InterruptedException e) {
            fail("posterEntriesStep or fileRenameStep failed: " + e.getMessage());
        }
    }
    
    /**
     * @see org.kuali.kfs.gl.batch.service.impl.BalancingServiceImplTestBase#getInputTransactions()
     */
    @Override
    protected String[] getInputTransactions() {
        // These inputTransactions are missing an initial 4 character FY string. It is added in setUp to test Balancing skipping old entries. Also, first 4
        // of the following array are in error intentionally.
        return new String[] {
                "BL1031420-----    ---EXEX08PO  EP542894        88888Midwest Scientific                                      50.00D2009-02-09568210    ----------        REQSEP568210                  D",
                "BL1031420-----    ---EXFB08PO  EP542894        88888GENERATED OFFSET                                        50.00C2009-03-25          ----------                                       ",
                "BL1031420-----    ---EXEX08PO  EP61063         88888SIEMENS MEDICAL SOLUTIONS USA INC.                   14331.00D2009-02-0999PC192   ----------        REQSEP57895                   D",
                "BL1031420-----    ---EXFB08PO  EP61063         88888GENERATED OFFSET                                     14331.00C2009-03-25          ----------                                       ",
                "BL1031420-----4100---EXEX08PO  EP6797          88888166080043 HPS OFFICE SYSTEMS                           579.84D2009-02-09          ----------                                      D",
                "BL1031420-----9892---EXFB08PO  EP6797          88888GENERATED OFFSET                                       579.84C2009-03-25          ----------                                       ",
                "BL1031420-----4617---ACEX08PREQEP926741        88888Young,Bonnie                                           223.44C2009-02-09          ----------        PO  EP528673                   ",
                "BL1031420-----9041---ACLI08PREQEP926741        88888Young,Bonnie                                           223.44D2009-02-09          ----------        PO  EP528673                   ",
                "BL1031420-----4300---ACEX08PREQEP934559        88888Post Masters                                           539.73D2009-02-09          ----------        PO  EP409297                   ",
                "BL1031420-----9041---ACLI08PREQEP934559        88888Post Masters                                           539.73C2009-02-09          ----------        PO  EP409297                   ",
                "BL1031420-----4300---EXEX08PREQEP934559        88888Post Masters                                           539.73C2009-02-09          ----------        PO  EP409297                  R",
                "BL1031420-----9892---EXFB08PREQEP934559        88888GENERATED OFFSET                                       539.73D2009-03-25          ----------                                       ",
                "BL1031420-----4520---ACEX08PREQEP942275        88888ESG SECURITY INC                                        49.00D2009-02-09BC70226   ----------        PO  EP510147                   ",
                "BL1031420-----9041---ACLI08PREQEP942275        88888ESG SECURITY INC                                        49.00C2009-02-09BC70226   ----------        PO  EP510147                   ",
                "BL1031420-----4520---ACEX08PREQEP942275        88888ESG SECURITY INC                                        49.00D2009-02-09BC70226   ----------        PO  EP510147                   ",
                "BL1031420-----9041---ACLI08PREQEP942275        88888ESG SECURITY INC                                        49.00C2009-02-09BC70226   ----------        PO  EP510147                   ",
                CHART_OF_ACCOUNTS_CODE + "1031420-----" + FINANCIAL_OBJECT_CODE + "---EXEX08PO  EP6797          88888166080043 HPS OFFICE SYSTEMS                           579.84D2009-02-09          ----------                                      D",
                CHART_OF_ACCOUNTS_CODE + "1031420-----" + FINANCIAL_OBJECT_CODE + "---EXFB08PO  EP6797          88888GENERATED OFFSET                                       579.84C2009-03-25          ----------                                       ",
        };
    }
}
