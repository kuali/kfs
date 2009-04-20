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
package org.kuali.kfs.module.ld.batch.service.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.batch.service.impl.BalancingServiceBaseImpl;
import org.kuali.kfs.gl.batch.service.impl.BalancingServiceImplTestBase;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.gl.dataaccess.LedgerBalancingDao;
import org.kuali.kfs.gl.dataaccess.LedgerEntryHistoryBalancingDao;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.batch.LaborFileRenameStep;
import org.kuali.kfs.module.ld.batch.LaborPosterStep;
import org.kuali.kfs.module.ld.businessobject.LaborBalanceHistory;
import org.kuali.kfs.module.ld.businessobject.LaborEntryHistory;
import org.kuali.kfs.module.ld.businessobject.LedgerBalance;
import org.kuali.kfs.module.ld.businessobject.LedgerEntry;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.batch.Step;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.rice.kns.service.BusinessObjectService;


/**
 * LD LaborBalancingServiceImpl test cases
 */
@ConfigureContext
public class LaborBalancingServiceImplTest extends BalancingServiceImplTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborBalancingServiceImplTest.class);
    
    @Override
    protected void setUp() throws Exception {
        balancingService = (BalancingServiceBaseImpl<Entry, Balance>) TestUtils.getUnproxiedService("laborBalancingService");
        ledgerEntryHistoryBalancingDao =  (LedgerEntryHistoryBalancingDao) SpringContext.getService("laborLedgerEntryHistoryDao");
        ledgerBalancingDao = (LedgerBalancingDao) SpringContext.getService("laborBalancingDao");

        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        businessObjectService.deleteMatching(LedgerEntry.class, fieldValues);
        
        // careful: super.setUp needs to happen at the end because it requires balancingService being initialized
        super.setUp();
    }
    
    /**
     * @see org.kuali.kfs.gl.batch.service.impl.BalancingServiceImplTest#testGetBalance()
     */
    @Override
    public void testGetBalance() {
        LaborBalanceHistory laborBalanceHistory = new LaborBalanceHistory();
        laborBalanceHistory.setUniversityFiscalYear(2007);
        laborBalanceHistory.setChartOfAccountsCode("BA");
        laborBalanceHistory.setAccountNumber("6044900");
        laborBalanceHistory.setSubAccountNumber("-----");
        laborBalanceHistory.setFinancialObjectCode("2400");
        laborBalanceHistory.setFinancialSubObjectCode("---");
        laborBalanceHistory.setFinancialBalanceTypeCode("A2");
        laborBalanceHistory.setFinancialObjectTypeCode("EX");
        laborBalanceHistory.setPositionNumber("00014789");
        laborBalanceHistory.setEmplid("0000000331");
        
        Balance balance = balancingService.getBalance(laborBalanceHistory);
        
        assertNotNull(balance);
        assertTrue(balance instanceof LedgerBalance);
    }
    
    @Override
    public void testRunBalancingPopulateData() {
        LOG.debug("No data present scenario, hence process should populate data");
                
        // First confirm tables are empty. If this fails then the test scenario is incorrectly set up
        assertEquals(0, getLedgerEntryBalancingDao().findCountGreaterOrEqualThan(startUniversityFiscalYear).intValue());
        assertEquals(0, getLedgerBalanceBalancingDao().findCountGreaterOrEqualThan(startUniversityFiscalYear).intValue());
        assertEquals(0, this.getHistoryCount(null, LaborEntryHistory.class));
        assertEquals(0, this.getHistoryCount(null, LaborBalanceHistory.class));
        
        // Generate some data and check that poster operated as expected. If this fails then the test scenario is incorrectly set up
        this.postTestCaseEntries(INPUT_TRANSACTIONS);
        assertEquals(12, getLedgerEntryBalancingDao().findCountGreaterOrEqualThan(startUniversityFiscalYear).intValue());
        assertEquals(3, getLedgerBalanceBalancingDao().findCountGreaterOrEqualThan(startUniversityFiscalYear).intValue());
        
        // Balancing should succeed successfully since this is an expected use case where it does an initial population of history tables
        assertTrue(balancingService.runBalancing());
        
        // Once it ran, we expect data in balancing tables. This data is per INPUT_TRANSACTIONS data structure
        assertEquals(12, ledgerEntryHistoryBalancingDao.findSumRowCountGreaterOrEqualThan(startUniversityFiscalYear).intValue());
        assertEquals(10, this.getHistoryCount(null, LaborEntryHistory.class));
        assertEquals(3, this.getHistoryCount(null, LaborBalanceHistory.class));
        
        // Finally make sure there isn't any comparison failure. Since we just populated the history tables this should hold true
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
        
        // Check that it copied the entries (so we should find at least that many). If it did not, something is wrong with the setup of this test case
        assertTrue(2 <= this.getHistoryCount(obsoleteYear, LaborEntryHistory.class));
        assertTrue(2 <= this.getHistoryCount(obsoleteYear, LaborBalanceHistory.class));
        
        // Run Balancing, it should hit the case that deletes the obsolete entries
        assertTrue(balancingService.runBalancing());
        
        // Verify that it deleted the entries
        assertEquals(0, this.getHistoryCount(obsoleteYear, LaborEntryHistory.class));
        assertEquals(0, this.getHistoryCount(obsoleteYear, LaborBalanceHistory.class));
    }
    
    @Override
    public void testRunBalancingHistoryUpdate() {
        // First pass is exactly the same as testRunBalancingPopulateData. This serves to populate the tables
        this.testRunBalancingPopulateData();
        
        // Next we post exactly the same entries and verify data exists. Note, now we have more entries
        this.postTestCaseEntries(INPUT_TRANSACTIONS);
        assertEquals(24, getLedgerEntryBalancingDao().findCountGreaterOrEqualThan(startUniversityFiscalYear).intValue());
        assertEquals(7, getLedgerBalanceBalancingDao().findCountGreaterOrEqualThan(startUniversityFiscalYear).intValue());
        
        // And run balancing again. Again, this should succeed
        assertTrue(balancingService.runBalancing());
        
        // Once it ran, we expect data in balancing tables, but different this time since the balancing job ran updates
        assertEquals(24, ledgerEntryHistoryBalancingDao.findSumRowCountGreaterOrEqualThan(startUniversityFiscalYear).intValue());
        assertEquals(10, this.getHistoryCount(null, LaborEntryHistory.class));
        assertEquals(3, this.getHistoryCount(null, LaborBalanceHistory.class));
        
        // Finally make sure there wasn't any comparison failure. We do this by running comparison methods directly (again)
        this.assertCompareHistorySuccess();
    }
    
    /**
     * @see org.kuali.kfs.gl.batch.service.impl.BalancingServiceImplTestBase#postTestCaseEntries(java.lang.String[])
     */
    @Override
    protected void postTestCaseEntries(String[] inputTransactions) {
        // Write test file
        TestUtils.writeFile(this.getBatchFileDirectoryName() + File.separator + LaborConstants.BatchFileSystem.POSTER_INPUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION, inputTransactions);
        
        try {
            // Run the poster
            Step laborPosterStep = SpringContext.getBean(LaborPosterStep.class);
            assertTrue("laborPosterStep should have succeeded", laborPosterStep.execute(getClass().getName(), dateTimeService.getCurrentDate()));
            
            // Rename the file because that's what happens before the balancing job runs
            Step laborFileRenameStep = SpringContext.getBean(LaborFileRenameStep.class);
            assertTrue("laborFileRenameStep should have succeeded", laborFileRenameStep.execute(getClass().getName(), dateTimeService.getCurrentDate()));
        }
        catch (InterruptedException e) {
            assertTrue("laborPosterStep or laborFileRenameStep failed: " + e.getMessage(), true);
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
                "BL1031400-----    ---A2EX05BT  01LP2837509     88888------------------TEST DESCRIPTION                                      619.90D2009-02-05                                                                     0.00     200905000000000010                                                      ",
                "BL1031400-----    ---A2EX05BT  01LP2837509     88888------------------TEST DESCRIPTION                                      276.47D2009-02-05                                                                     0.00     200905000000000010                                                      ",
                "BL1031400-----    ---A2EX05BT  01LP2837509     88888------------------TEST DESCRIPTION                                      448.77D2009-02-05                                                                     0.00     200905000000000010                                                      ",
                "BL1031400-----    ---A2EX05BT  01LP2837509     88888------------------TEST DESCRIPTION                                      619.90C2009-02-05                                                                     0.00     200905000000000010                                                      ",
                "BL1031400-----5760---A2EX05BT  01LP2837509     88888------------------TEST DESCRIPTION                                      276.47C2009-02-05                                                                     0.00     200905000000000010                                                      ",
                "BL1031400-----5772---A2EX05BT  01LP2837509     88888------------------TEST DESCRIPTION                                      448.77C2009-02-05                                                                     0.00     200905000000000010                                                      ",
                "BL1031400-----5625---A2EX06BT  01LP2837509     88888------------------TEST DESCRIPTION                                      619.90D2009-02-05                                                                     0.00     200906000000000010                                                      ",
                "BL1031400-----5760---A2EX06BT  01LP2837509     88888------------------TEST DESCRIPTION                                      276.47D2009-02-05                                                                     0.00     200906000000000010                                                      ",
                "BL1031400-----5772---A2EX06BT  01LP2837509     88888------------------TEST DESCRIPTION                                      448.77D2009-02-05                                                                     0.00     200906000000000010                                                      ",
                "BL1031400-----5625---A2EX06BT  01LP2837509     88888------------------TEST DESCRIPTION                                      619.90C2009-02-05                                                                     0.00     200906000000000010                                                      ",
                "BL1031400-----5760---A2EX06BT  01LP2837509     88888------------------TEST DESCRIPTION                                      276.47C2009-02-05                                                                     0.00     200906000000000010                                                      ",
                "BL1031400-----5772---A2EX06BT  01LP2837509     88888------------------TEST DESCRIPTION                                      448.77C2009-02-05                                                                     0.00     200906000000000010                                                      ",
                "BL1031400-----5625---A2EX08BT  01LP2837509     88888------------------TEST DESCRIPTION                                      619.90C2009-02-05                                                                     0.00     200905000000000010                                                      ",
                "BL1031400-----5625---A2EX08BT  01LP2837509     88888------------------TEST DESCRIPTION                                      619.90C2009-02-05                                                                     0.00     200906000000000010                                                      ",
                "BL1031400-----5760---A2EX08BT  01LP2837509     88888------------------TEST DESCRIPTION                                      276.47C2009-02-05                                                                     0.00     200905000000000010                                                      ",
                "BL1031400-----5760---A2EX08BT  01LP2837509     88888------------------TEST DESCRIPTION                                      276.47C2009-02-05                                                                     0.00     200906000000000010                                                      ",
                "BL1031400-----5772---A2EX08BT  01LP2837509     88888------------------TEST DESCRIPTION                                      448.77C2009-02-05                                                                     0.00     200905000000000010                                                      ",
                "BL1031400-----5772---A2EX08BT  01LP2837509     88888------------------TEST DESCRIPTION                                      448.77C2009-02-05                                                                     0.00     200906000000000010                                                      ",

        };
    }
}
