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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.gl.businessobject.BalanceHistory;
import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.gl.businessobject.EntryHistory;
import org.kuali.kfs.gl.businessobject.OriginEntryInformation;
import org.kuali.kfs.gl.dataaccess.LedgerBalanceBalancingDao;
import org.kuali.kfs.gl.dataaccess.LedgerBalancingDao;
import org.kuali.kfs.gl.dataaccess.LedgerEntryBalancingDao;
import org.kuali.kfs.gl.dataaccess.LedgerEntryHistoryBalancingDao;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.kfs.sys.service.impl.ReportWriterTextServiceImpl;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Is a Base class for GL and LD BalancingService test cases
 */
public abstract class BalancingServiceImplTestBase extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BalancingServiceImplTestBase.class);
    
    protected static final String CHART_OF_ACCOUNTS_CODE = "BL";
    protected static final String FINANCIAL_OBJECT_CODE = "5772";
    
    protected BalancingServiceBaseImpl<Entry, Balance> balancingService;
    protected LedgerBalancingDao ledgerBalancingDao;
    protected LedgerEntryHistoryBalancingDao ledgerEntryHistoryBalancingDao;
    protected BusinessObjectService businessObjectService;
    protected DateTimeService dateTimeService;
    protected UniversityDateService universityDateService;
    
    protected Integer startUniversityFiscalYear;
    protected Integer obsoleteUniversityFiscalYear;
    protected String[] INPUT_TRANSACTIONS = this.getInputTransactions();
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        dateTimeService = SpringContext.getBean(DateTimeService.class);
        universityDateService = SpringContext.getBean(UniversityDateService.class);
        
        // Due to how WrappingBatchService works we need to manually initialize report writing
        ((ReportWriterTextServiceImpl) balancingService.reportWriterService).initialize();
        
        // Make the last two of INPUT_TRANSACTIONS out of date range
        Integer currentFiscalYear = universityDateService.getCurrentFiscalYear();
        startUniversityFiscalYear = currentFiscalYear - balancingService.getPastFiscalYearsToConsider();
        obsoleteUniversityFiscalYear = startUniversityFiscalYear - 1;
        for (int i = 0; i < INPUT_TRANSACTIONS.length; i++) {
            if (i < INPUT_TRANSACTIONS.length - 2) {
                INPUT_TRANSACTIONS[i] = currentFiscalYear + INPUT_TRANSACTIONS[i];
            } else {
                // Want the last two to be out of range
                INPUT_TRANSACTIONS[i] = obsoleteUniversityFiscalYear + INPUT_TRANSACTIONS[i];
            }
        }
        
        // And to make sure that the posters don't complain we need to generate data in the obsolete fiscal year
        this.generateObsoleteYearData(obsoleteUniversityFiscalYear);
    }
    
    @Override
    protected void tearDown() throws Exception {
        // KualiTestBase cleans added files up. Hence we need to make sure BalancingService doesn't use cache when looking for files
        balancingService.clearPosterFileCache();
        
        // Due to how WrappingBatchService works we need to manually destroy report writing
        ((ReportWriterTextServiceImpl) balancingService.reportWriterService).destroy();
        
        super.tearDown();
    }
    
    public void testBasicGetters() {
        // Here we test a set of basic getters. There really isn't much that should be done here because they vary between GL and Labor plus
        // these are pretty simple methods. Just checking that they return values should be enough.
        assertNotNull(balancingService.getPastFiscalYearsToConsider());
        assertNotNull(balancingService.getComparisonFailuresToPrintPerReport());
        
        // These should at minimum return Entry and Balance plus Histories for each GL and Labor
        assertNotNull(balancingService.getShortTableLabel((Entry.class).getSimpleName()));        
        assertNotNull(balancingService.getShortTableLabel((EntryHistory.class).getSimpleName()));
        assertNotNull(balancingService.getShortTableLabel((Balance.class).getSimpleName()));
        assertNotNull(balancingService.getShortTableLabel((BalanceHistory.class).getSimpleName()));
        
        // Whether files are ready or not depends on if the poster ran before this. We are just executing the code here and not checking return value.
        balancingService.getPosterInputFile();
        balancingService.getPosterErrorOutputFile();
    }
    
    public void testGetOriginEntry() {
        LOG.debug("Basic test that getting GL or Labor OriginEntryInformation works. Since the parsing method isn't part of Balancing we don't run a full test.");
        OriginEntryInformation originEntry = balancingService.getOriginEntry(INPUT_TRANSACTIONS[0], 0);
        assertNotNull(originEntry);
        assertEquals(TestUtils.getFiscalYearForTesting().intValue(), originEntry.getUniversityFiscalYear().intValue());
    }
    
    public void testIsFilesReady() {
        // Delete the files since we don't know whether the poster run before this test case or not
        TestUtils.deleteFilesInDirectory(this.getBatchFileDirectoryName());
        
        LOG.debug("No file data present scenario");
        assertFalse(balancingService.isFilesReady());
        
        LOG.debug("Files ready scenario");
        this.postTestCaseEntries(INPUT_TRANSACTIONS);
        assertTrue(balancingService.isFilesReady());
    }
    
    public abstract void testRunBalancingPopulateData();

    public abstract void testRunBalancingDeleteObsoleteUniversityFiscalYearData();
    
    public abstract void testRunBalancingHistoryUpdate();
    
    /**
     * Fix Test
     */
    public void PATCHFIX_testRunBalancingComparisionFailure() {
        // Execute exactly the same as testRunBalancingPopulateData. This serves to populate the tables
        this.testRunBalancingPopulateData();
        
        // Now run balancing again, in the same accounting cycle
        assertTrue(balancingService.runBalancing());
        
        // This caused comparison failures because the update was run twice for the same accounting cycle, verify that was the case
        this.assertCompareHistoryFailure();
    }

    
    /**
     * Test helper since PosterService and LaborPosterService are exclusive interfaces.
     */
    protected abstract void postTestCaseEntries(String[] inputTransactions);
    
    /**
     * Test helper to get GL or Labor sample transactions. This isn't in fixtures because these are written to a file, not used as objects
     */
    protected abstract String[] getInputTransactions();
    
    /**
     * Test helper to compare history data to live data, expecting comparison success
     */
    protected void assertCompareHistorySuccess() {
        assertEquals(0, balancingService.compareEntryHistory().intValue());
        assertEquals(0, balancingService.compareBalanceHistory().intValue());
        
        Map<String, Integer> countCustomComparisionFailures = balancingService.customCompareHistory();
        
        if (ObjectUtils.isNotNull(countCustomComparisionFailures)) {
            for (Iterator<String> names = countCustomComparisionFailures.keySet().iterator(); names.hasNext();) {
                String name = names.next();
                assertEquals(0, countCustomComparisionFailures.get(name).intValue());
            }
        }
    }
    
    /**
     * Test helper to compare history data to live data, expecting comparison failure
     */
    protected void assertCompareHistoryFailure() {
        assertNotSame(0, balancingService.compareEntryHistory().intValue());
        assertNotSame(0, balancingService.compareBalanceHistory().intValue());
        
        Map<String, Integer> countCustomComparisionFailures = balancingService.customCompareHistory();
        
        if (ObjectUtils.isNotNull(countCustomComparisionFailures)) {
            for (Iterator<String> names = countCustomComparisionFailures.keySet().iterator(); names.hasNext();) {
                String name = names.next();
                assertNotSame(0, countCustomComparisionFailures.get(name).intValue());
            }
        }
    }
    
     /**
     * Test helper to generate obsolete fiscal year data if it does not exist. This method is messy. What it is doing is to check if the entries exist in
     * case there was an FY rollover but data wasn't updated yet. If not, it makes copies of the current. This corresponds to the entries getInputTransactions
     * @param obsoleteYear
     */
    protected void generateObsoleteYearData(Integer obsoleteYear) {
        Map<String, Object> obsoleteChartOfAccountFieldValues = new HashMap<String, Object>();
        obsoleteChartOfAccountFieldValues.put(KFSConstants.UNIVERSITY_FISCAL_YEAR_PROPERTY_NAME, obsoleteYear);
        obsoleteChartOfAccountFieldValues.put(KFSConstants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME, CHART_OF_ACCOUNTS_CODE);
        obsoleteChartOfAccountFieldValues.put(KFSConstants.FINANCIAL_OBJECT_CODE_PROPERTY_NAME, FINANCIAL_OBJECT_CODE);
        ObjectCode obsoleteYearObjectCode = (ObjectCode) businessObjectService.findByPrimaryKey(ObjectCode.class, obsoleteChartOfAccountFieldValues);
        if (ObjectUtils.isNull(obsoleteYearObjectCode)) {
            Map<String, Object> fieldValues = new HashMap<String, Object>();
            fieldValues.put(KFSConstants.UNIVERSITY_FISCAL_YEAR_PROPERTY_NAME, TestUtils.getFiscalYearForTesting());
            fieldValues.put(KFSConstants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME, CHART_OF_ACCOUNTS_CODE);
            fieldValues.put(KFSConstants.FINANCIAL_OBJECT_CODE_PROPERTY_NAME, FINANCIAL_OBJECT_CODE);
            ObjectCode objectCode = (ObjectCode) businessObjectService.findByPrimaryKey(ObjectCode.class, fieldValues);
            objectCode.setUniversityFiscalYear(obsoleteYear);
            objectCode.setVersionNumber(null);
            objectCode.setObjectId(null);
            businessObjectService.save(objectCode);
        }
    }
    
    /**
     * Test helper to expose protected method on BalancingService. Quick and dirty way to make the test case easy to write and avoid exposing (making public) production code unnecessarily.
     */
    protected int getHistoryCount(Integer fiscalYear, Class<? extends PersistableBusinessObjectBase> persistentClass) {
        return balancingService.getHistoryCount(fiscalYear, persistentClass);
    }
    
    /**
     * Test helper to expose protected method on BalancingService. Quick and dirty way to make the test case easy to write and avoid exposing (making public) production code unnecessarily.
     */
    protected LedgerEntryBalancingDao getLedgerEntryBalancingDao() {
        return balancingService.ledgerEntryBalancingDao;
    }
    
    /**
     * Test helper to expose protected method on BalancingService. Quick and dirty way to make the test case easy to write and avoid exposing (making public) production code unnecessarily.
     */
    protected LedgerBalanceBalancingDao getLedgerBalanceBalancingDao() {
        return balancingService.ledgerBalanceBalancingDao;
    }
    
    /**
     * Test helper to expose protected method on BalancingService. Quick and dirty way to make the test case easy to write and avoid exposing (making public) production code unnecessarily.
     */
    protected String getBatchFileDirectoryName() {
        return balancingService.batchFileDirectoryName;
    }
}
