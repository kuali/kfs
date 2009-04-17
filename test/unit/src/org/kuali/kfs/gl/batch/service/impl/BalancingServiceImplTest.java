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
package org.kuali.kfs.gl.batch.service.impl;

import java.io.File;

import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.batch.FileRenameStep;
import org.kuali.kfs.gl.batch.service.PosterService;
import org.kuali.kfs.gl.businessobject.AccountBalanceHistory;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.gl.businessobject.BalanceHistory;
import org.kuali.kfs.gl.businessobject.EncumbranceHistory;
import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.gl.businessobject.EntryHistory;
import org.kuali.kfs.gl.dataaccess.AccountBalanceDao;
import org.kuali.kfs.gl.dataaccess.BalancingDao;
import org.kuali.kfs.gl.dataaccess.EncumbranceDao;
import org.kuali.kfs.gl.dataaccess.LedgerBalancingDao;
import org.kuali.kfs.gl.dataaccess.LedgerEntryHistoryBalancingDao;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.batch.Step;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.kfs.sys.suite.RelatesTo;
import org.kuali.kfs.sys.suite.RelatesTo.JiraIssue;


/**
 * GL BalancingServiceImpl test cases
 */
@ConfigureContext
public class BalancingServiceImplTest extends BalancingServiceImplTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BalancingServiceImplTest.class);
    
    protected PosterService posterService;
    protected BalancingDao balancingDao;
    protected AccountBalanceDao accountBalanceDao;
    protected EncumbranceDao encumbranceDao;
    
    @Override
    protected void setUp() throws Exception {
        balancingService = (BalancingServiceBaseImpl<Entry, Balance>) TestUtils.getUnproxiedService("glBalancingService");
        ledgerEntryHistoryBalancingDao =  (LedgerEntryHistoryBalancingDao) SpringContext.getService("glEntryHistoryDao");
        ledgerBalancingDao = (LedgerBalancingDao) SpringContext.getService("glLedgerBalancingDao");

        posterService = SpringContext.getBean(PosterService.class);
        balancingDao = (BalancingDao) SpringContext.getService("glBalancingDao");
        accountBalanceDao = SpringContext.getBean(AccountBalanceDao.class);
        encumbranceDao = SpringContext.getBean(EncumbranceDao.class);
        
        // careful: super.setUp needs to happen at the end because it requires balancingService being initialized
        super.setUp();
    }
    
    /**
     * @see org.kuali.kfs.gl.batch.service.impl.BalancingServiceImplTest#testGetBalance()
     */
    @Override
    public void testGetBalance() {
        BalanceHistory balanceHistory = new BalanceHistory();
        balanceHistory.setUniversityFiscalYear(2007);
        balanceHistory.setChartOfAccountsCode("BA");
        balanceHistory.setAccountNumber("6044900");
        balanceHistory.setSubAccountNumber("-----");
        balanceHistory.setObjectCode("1464");
        balanceHistory.setSubObjectCode("---");
        balanceHistory.setBalanceTypeCode("AC");
        balanceHistory.setObjectTypeCode("IC");
        
        Balance balance = balancingService.getBalance(balanceHistory);
        
        assertNotNull(balance);
        assertTrue(balance instanceof Balance);
    }
    
    @Override
    @RelatesTo(JiraIssue.KFSMI3345)
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
        // Generate some data
        this.postTestCaseEntries(INPUT_TRANSACTIONS);
        
        int obsoleteYear = startUniversityFiscalYear-1;
        
        // INPUT_TRANSACTIONS contains 2 entries that are in an obsolete year, hence manually populate our history tables and force them to be picked up
        ledgerBalancingDao.populateLedgerEntryHistory(obsoleteYear);
        ledgerBalancingDao.populateLedgerBalanceHistory(obsoleteYear);
        balancingDao.populateAccountBalancesHistory(obsoleteYear);
        balancingDao.populateEncumbranceHistory(obsoleteYear);
        
        // Check that it copied the entries (so we should find at least that many). If it did not, something is wrong with the setup of this test case
        assertTrue(2 <= this.getHistoryCount(obsoleteYear, EntryHistory.class));
        assertTrue(2 <= this.getHistoryCount(obsoleteYear, BalanceHistory.class));
        assertTrue(2 <= this.getHistoryCount(obsoleteYear, AccountBalanceHistory.class));
        assertTrue(2 <= this.getHistoryCount(obsoleteYear, EncumbranceHistory.class));
        
        // Run Balancing, it should hit the case that deletes the obsolete entries
        assertTrue(balancingService.runBalancing());
        
        // Verify that it deleted the entries
        assertEquals(0, this.getHistoryCount(obsoleteYear, EntryHistory.class));
        assertEquals(0, this.getHistoryCount(obsoleteYear, BalanceHistory.class));
        assertEquals(0, this.getHistoryCount(obsoleteYear, AccountBalanceHistory.class));
        assertEquals(0, this.getHistoryCount(obsoleteYear, EncumbranceHistory.class));
    }
    
    @Override
    @RelatesTo(JiraIssue.KFSMI3345)
    public void testRunBalancingHistoryUpdate() {
        // First pass is exactly the same as testRunBalancingPopulateData. This serves to populate the tables
        this.testRunBalancingPopulateData();
        
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
        // Write our test file
        TestUtils.writeFile(this.getBatchFileDirectoryName() + File.separator + GeneralLedgerConstants.BatchFileSystem.POSTER_INPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION, inputTransactions);
        
        // Run the poster
        posterService.postMainEntries();
        
        // Rename the file because that's what happens before the balancing job runs
        Step fileRenameStep = SpringContext.getBean(FileRenameStep.class);
        try {
            assertTrue("Should return true", fileRenameStep.execute(getClass().getName(), dateTimeService.getCurrentDate()));
        }
        catch (InterruptedException e) {
            assertTrue("fileRenameStep failed: " + e.getMessage(), true);
        }
    }
    
    /**
     * @see org.kuali.kfs.gl.batch.service.impl.BalancingServiceImplTest#getInputTransactions()
     */
    @Override
    protected String[] getInputTransactions() {
        // These inputTransactions are missing an initial 4 character FY string. It is added in setUp to test Balancing skipping old entries. Also, first 4
        // of the following array are in error intentionally.
        return new String[] {
                "BL1031420-----    ---EXEX08PO  EP542894        88888Midwest Scientific                                     50.00D2009-02-09568210    ----------        REQSEP568210                  D",
                "BL1031420-----    ---EXFB08PO  EP542894        88888GENERATED OFFSET                                       50.00C2009-03-25          ----------                                       ",
                "BL1031420-----    ---EXEX08PO  EP61063         88888SIEMENS MEDICAL SOLUTIONS USA INC.                  14331.00D2009-02-0999PC192   ----------        REQSEP57895                   D",
                "BL1031420-----    ---EXFB08PO  EP61063         88888GENERATED OFFSET                                    14331.00C2009-03-25          ----------                                       ",
                "BL1031420-----4100---EXEX08PO  EP6797          88888166080043 HPS OFFICE SYSTEMS                          579.84D2009-02-09          ----------                                      D",
                "BL1031420-----9892---EXFB08PO  EP6797          88888GENERATED OFFSET                                      579.84C2009-03-25          ----------                                       ",
                "BL1031420-----4617---ACEX08PREQEP926741        88888Young,Bonnie                                          223.44C2009-02-09          ----------        PO  EP528673                   ",
                "BL1031420-----9041---ACLI08PREQEP926741        88888Young,Bonnie                                          223.44D2009-02-09          ----------        PO  EP528673                   ",
                "BL1031420-----4300---ACEX08PREQEP934559        88888Post Masters                                          539.73D2009-02-09          ----------        PO  EP409297                   ",
                "BL1031420-----9041---ACLI08PREQEP934559        88888Post Masters                                          539.73C2009-02-09          ----------        PO  EP409297                   ",
                "BL1031420-----4300---EXEX08PREQEP934559        88888Post Masters                                          539.73C2009-02-09          ----------        PO  EP409297                  R",
                "BL1031420-----9892---EXFB08PREQEP934559        88888GENERATED OFFSET                                      539.73D2009-03-25          ----------                                       ",
                "BL1031420-----4520---ACEX08PREQEP942275        88888ESG SECURITY INC                                       49.00D2009-02-09BC70226   ----------        PO  EP510147                   ",
                "BL1031420-----9041---ACLI08PREQEP942275        88888ESG SECURITY INC                                       49.00C2009-02-09BC70226   ----------        PO  EP510147                   ",
                "BL1031420-----4520---ACEX08PREQEP942275        88888ESG SECURITY INC                                       49.00D2009-02-09BC70226   ----------        PO  EP510147                   ",
                "BL1031420-----9041---ACLI08PREQEP942275        88888ESG SECURITY INC                                       49.00C2009-02-09BC70226   ----------        PO  EP510147                   ",
                "BL1031420-----4520---ACEX08PREQEP942275        88888ESG SECURITY INC                                       98.00C2009-02-09BC70226   ----------        PO  EP510147                   ",
                "BL1031420-----9041---ACLI08PREQEP942275        88888ESG SECURITY INC                                       98.00D2009-02-09BC70226   ----------        PO  EP510147                   ",
        };
    }
}
