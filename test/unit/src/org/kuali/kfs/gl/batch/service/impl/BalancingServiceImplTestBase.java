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
import java.io.FilenameFilter;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.Map;

import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.TextReportHelper;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.kfs.gl.businessobject.BalanceHistory;
import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.gl.businessobject.EntryHistory;
import org.kuali.kfs.gl.businessobject.OriginEntry;
import org.kuali.kfs.gl.dataaccess.LedgerBalanceBalancingDao;
import org.kuali.kfs.gl.dataaccess.LedgerBalancingDao;
import org.kuali.kfs.gl.dataaccess.LedgerEntryBalancingDao;
import org.kuali.kfs.gl.dataaccess.LedgerEntryHistoryBalancingDao;
import org.kuali.kfs.gl.service.impl.ReportServiceImpl;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.kfs.sys.suite.RelatesTo;
import org.kuali.kfs.sys.suite.RelatesTo.JiraIssue;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * Is a Base class for GL and LD BalancingService test cases
 */
public abstract class BalancingServiceImplTestBase extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BalancingServiceImplTestBase.class);
    
    protected BalancingServiceBaseImpl<Entry, Balance> balancingService;
    protected LedgerBalancingDao ledgerBalancingDao;
    protected LedgerEntryHistoryBalancingDao ledgerEntryHistoryBalancingDao;
    protected DateTimeService dateTimeService;
    
    protected Integer startUniversityFiscalYear;
    protected String[] INPUT_TRANSACTIONS = this.getInputTransactions();
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        dateTimeService = SpringContext.getBean(DateTimeService.class);
        
        // Make the last two of INPUT_TRANSACTIONS out of date range
        OptionsService optionsService = SpringContext.getBean(OptionsService.class);
        String currentFiscalYear = optionsService.getCurrentYearOptions().getUniversityFiscalYear() + "";
        startUniversityFiscalYear = optionsService.getCurrentYearOptions().getUniversityFiscalYear() - balancingService.getPastFiscalYearsToConsider();
        for (int i = 0; i < INPUT_TRANSACTIONS.length; i++) {
            if (i < INPUT_TRANSACTIONS.length - 2) {
                INPUT_TRANSACTIONS[i] = currentFiscalYear + INPUT_TRANSACTIONS[i];
            } else {
                // Want the last two to be out of range
                INPUT_TRANSACTIONS[i] = (startUniversityFiscalYear-1 + "") + INPUT_TRANSACTIONS[i];
            }
        }
    }
    
    @Override
    protected void tearDown() throws Exception {
        // Cleanup after test case. Since the files are deleted we need to get BalancingService to forget about them
        TestUtils.deleteFilesInDirectory(this.getBatchFileDirectoryName());
        balancingService.clearPosterFileCache();
        
        super.tearDown();
    }
    
    public void testBasicGetters() {
        // Here we test a set of basic getters. There really isn't much that should be done here because they vary between GL and Labor plus
        // these are pretty simple methods. Just checking that they return values should be enough.
        assertNotNull(balancingService.getReportFilename());
        assertNotNull(balancingService.getReportTitle());
        assertNull("No file should be found", balancingService.getPosterInputFile());
        assertNull("No file should be found", balancingService.getPosterErrorOutputFile());
        assertNotNull(balancingService.getPastFiscalYearsToConsider());
        assertNotNull(balancingService.getComparisonFailuresToPrintPerReport());
        
        // These should at minimum return Entry and Balance plus Histories for each GL and Labor
        assertNotNull(balancingService.getShortTableLabel((Entry.class).getSimpleName()));        
        assertNotNull(balancingService.getShortTableLabel((EntryHistory.class).getSimpleName()));
        assertNotNull(balancingService.getShortTableLabel((Balance.class).getSimpleName()));
        assertNotNull(balancingService.getShortTableLabel((BalanceHistory.class).getSimpleName()));
    }
    
    public void testGetOriginEntry() {
        LOG.debug("Basic test that getting GL or Labor OriginEntry works. Since the parsing method isn't part of Balancing we don't run a full test.");
        OriginEntry originEntry = balancingService.getOriginEntry(INPUT_TRANSACTIONS[0], 0);
        assertNotNull(originEntry);
        assertEquals(2009, originEntry.getUniversityFiscalYear().intValue());
    }
    
    public abstract void testGetBalance();
    
    public void testGetNewestDataFile() {
        final String testFilename = "testGetNewestDataFile";
        final String testFilenameUnmatched = "testUmatchedGetNewestDataFile";
        
        // Use a filename filter matching testFilename. This shouldn't impact the logic as long as we're consistently testing with this one
        FilenameFilter filenameFilter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return (name.startsWith(testFilename) &&
                        name.endsWith(GeneralLedgerConstants.BatchFileSystem.EXTENSION));
            }
        };
        
        assertNull("Shouldn't have found any files of name " + testFilename, balancingService.getNewestDataFile(filenameFilter));
        
        try {
            LOG.debug("Create three test files. Sleeping briefly between each to ensure unique timestamps.");
            TestUtils.writeFile(balancingService.batchFileDirectoryName + File.separator + testFilename + "FileA" + GeneralLedgerConstants.BatchFileSystem.EXTENSION, this.INPUT_TRANSACTIONS);
            Thread.sleep(5);
            TestUtils.writeFile(balancingService.batchFileDirectoryName + File.separator + testFilename + "FileB" + GeneralLedgerConstants.BatchFileSystem.EXTENSION, this.INPUT_TRANSACTIONS);
            Thread.sleep(5);
            TestUtils.writeFile(balancingService.batchFileDirectoryName + File.separator + testFilename + "FileC" + GeneralLedgerConstants.BatchFileSystem.EXTENSION, this.INPUT_TRANSACTIONS);
            Thread.sleep(5);
            TestUtils.writeFile(balancingService.batchFileDirectoryName + File.separator + testFilenameUnmatched + GeneralLedgerConstants.BatchFileSystem.EXTENSION, this.INPUT_TRANSACTIONS);
        } catch (InterruptedException e) {
            assertTrue("No reason that this job should have gotten interrupted.", false);
        }
        
        File newestFile = balancingService.getNewestDataFile(filenameFilter);
        assertNotNull("We just created a few files but none was found, filename=" + testFilename, newestFile);
        assertTrue("Was expecting last file created. Not the case, found: " + newestFile.getName(), newestFile.getName().contains("FileC"));
        
        LOG.debug("Cleanup files after ourselves." + testFilename);
        File directory = new File(balancingService.batchFileDirectoryName);
        File[] directoryListing = directory.listFiles(filenameFilter);
        for (int i = 0; i < directoryListing.length; i++) {
            File file = directoryListing[i];
            assertTrue("Delete failed. Shouldn't.", file.delete());
        }
        assertTrue("Delete failed. Shouldn't.", new File(balancingService.batchFileDirectoryName + File.separator + testFilenameUnmatched + GeneralLedgerConstants.BatchFileSystem.EXTENSION).delete());
    }
    
    public void testIsFilesReady() {
        LOG.debug("No file data present scenario");
        assertFalse(balancingService.isFilesReady());
        
        LOG.debug("Files ready scenario");
        this.postTestCaseEntries(INPUT_TRANSACTIONS);
        assertTrue(balancingService.isFilesReady());
    }
    
    public void testRunBalancingNoFiles() {
        LOG.debug("No files present scenario.");
        assertFalse(balancingService.runBalancing());
    }
    
    public abstract void testRunBalancingPopulateData();

    public abstract void testRunBalancingDeleteObsoleteUniversityFiscalYearData();
    
    public abstract void testRunBalancingHistoryUpdate();
    
    @RelatesTo(JiraIssue.KFSMI3345)
    public void testRunBalancingComparisionFailure() {
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
     * Test helper to get GL or Labor sample transactions
     */
    protected abstract String[] getInputTransactions();
    
    /**
     * Test helper to compare history data to live data, expecting comparison success
     */
    protected void assertCompareHistorySuccess() {
        // Create a TextReportHelper so we can call methods that do printing
        // TODO Would be nice to use mock object for the following. Waiting for TextReportHelper to become a service
        KualiConfigurationService kualiConfigurationService = SpringContext.getBean(KualiConfigurationService.class);
        String reportsDirectory = kualiConfigurationService.getPropertyString(KFSConstants.REPORTS_DIRECTORY_KEY);
        String reportFilename = reportsDirectory + File.separator + "junit_" + balancingService.getReportFilename() + new SimpleDateFormat(ReportServiceImpl.DATE_FORMAT_STRING).format(dateTimeService.getCurrentDate()) + ".txt";
        PrintStream REPORT_ps;
        try {
            REPORT_ps = new PrintStream(reportFilename);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        TextReportHelper textReportHelper = new TextReportHelper("Junit Test Case Dump", REPORT_ps, dateTimeService);
        
        assertEquals(0, balancingService.compareEntryHistory(textReportHelper).intValue());
        assertEquals(0, balancingService.compareBalanceHistory(textReportHelper).intValue());
        
        Map<String, Integer> countCustomComparisionFailures = balancingService.customCompareHistory(textReportHelper);
        
        if (ObjectUtils.isNotNull(countCustomComparisionFailures)) {
            for (Iterator<String> names = countCustomComparisionFailures.keySet().iterator(); names.hasNext();) {
                String name = names.next();
                assertEquals(0, countCustomComparisionFailures.get(name).intValue());
            }
        }
        
        REPORT_ps.close();
    }
    
    /**
     * Test helper to compare history data to live data, expecting comparison failure
     */
    protected void assertCompareHistoryFailure() {
        // Create a TextReportHelper so we can call methods that do printing
        // TODO Would be nice to use mock object for the following. Waiting for TextReportHelper to become a service
        KualiConfigurationService kualiConfigurationService = SpringContext.getBean(KualiConfigurationService.class);
        DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
        String reportsDirectory = kualiConfigurationService.getPropertyString(KFSConstants.REPORTS_DIRECTORY_KEY);
        String reportFilename = reportsDirectory + File.separator + "junit_" + balancingService.getReportFilename() + new SimpleDateFormat(ReportServiceImpl.DATE_FORMAT_STRING).format(dateTimeService.getCurrentDate()) + ".txt";
        PrintStream REPORT_ps;
        try {
            REPORT_ps = new PrintStream(reportFilename);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        TextReportHelper textReportHelper = new TextReportHelper("Junit Test Case Dump", REPORT_ps, dateTimeService);
        
        assertNotSame(0, balancingService.compareEntryHistory(textReportHelper).intValue());
        assertNotSame(0, balancingService.compareBalanceHistory(textReportHelper).intValue());
        
        Map<String, Integer> countCustomComparisionFailures = balancingService.customCompareHistory(textReportHelper);
        
        if (ObjectUtils.isNotNull(countCustomComparisionFailures)) {
            for (Iterator<String> names = countCustomComparisionFailures.keySet().iterator(); names.hasNext();) {
                String name = names.next();
                assertNotSame(0, countCustomComparisionFailures.get(name).intValue());
            }
        }
        
        REPORT_ps.close();
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
