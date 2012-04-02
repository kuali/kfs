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
package org.kuali.kfs.gl.businessobject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.OffsetDefinition;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.batch.service.AccountingCycleCachingService;
import org.kuali.kfs.gl.service.OriginEntryService;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.kfs.sys.dataaccess.UnitTestSqlDao;
import org.kuali.kfs.sys.service.ConfigurableDateService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.krad.service.PersistenceService;

/**
 * OriginEntryTestBase...the uberpowerful base of a lot of GL tests.  Basically, this class provides
 * many convenience methods for writing tests that test against large batches of origin entries.
 */
@ConfigureContext
public class OriginEntryTestBase extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OriginEntryTestBase.class);

    protected ConfigurableDateService dateTimeService;
    protected PersistenceService persistenceService;
    protected UnitTestSqlDao unitTestSqlDao = null;
    protected ConfigurationService kualiConfigurationService = null;
    protected OriginEntryService originEntryService = null;
    protected AccountingCycleCachingService accountingCycleCachingService = null;

    protected Date date;
    protected String batchDirectory;
    protected String builtDirectory;

    protected String testingYear;
    protected String testingPeriodCode;

    /**
     * Constructs a OriginEntryTestBase instance
     */
    public OriginEntryTestBase() {
        super();
    }

    /**
     * Sets up this test base; that means getting some services from Spring and reseting the
     * enhancement flags.
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        if (LOG.isDebugEnabled()) {
            LOG.debug("setUp() starting");
        }

        dateTimeService = SpringContext.getBean(ConfigurableDateService.class);
        date = dateTimeService.getCurrentDate();

        // Other objects needed for the tests
        persistenceService = SpringContext.getBean(PersistenceService.class, "persistenceServiceOjb");
        unitTestSqlDao = SpringContext.getBean(UnitTestSqlDao.class);
        kualiConfigurationService = SpringContext.getBean(ConfigurationService.class);
        originEntryService = SpringContext.getBean(OriginEntryService.class);

        batchDirectory = this.getBatchDirectoryName();
        buildBatchDirectory(batchDirectory);

        accountingCycleCachingService = SpringContext.getBean(AccountingCycleCachingService.class);
        accountingCycleCachingService.initialize();

        // Set all enhancements to off
        resetAllEnhancementFlags();

        testingYear = TestUtils.getFiscalYearForTesting().toString();
        testingPeriodCode = TestUtils.getPeriodCodeForTesting();
    }

    /**
     * Removes any build batch directory
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        removeBatchDirectory(batchDirectory);

        if (accountingCycleCachingService != null) {
            accountingCycleCachingService.destroy();
        }
    }

    /**
     * get the name of the batch directory
     * @return the name of the batch directory
     */
    protected String getBatchDirectoryName() {
        return SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString("staging.directory")+"/gl/test_directory/originEntry";
    }

    /**
     * Recursively ensures that the whole of the path of the batch directory exists
     */
    protected void buildBatchDirectory(String batchDirectory) {
        String[] directoryPieces = batchDirectory.split("/");
        StringBuilder builtDirectorySoFar = new StringBuilder();
        StringBuilder directoryToRemoveSoFar = new StringBuilder();

        for (String directoryPiece : directoryPieces) {
            if (!StringUtils.isBlank(directoryPiece)) {
                builtDirectorySoFar.append('/');
                builtDirectorySoFar.append(directoryPiece);

                File dir = new File(builtDirectorySoFar.toString());
                if (!dir.exists()) {
                    directoryToRemoveSoFar.append('/');
                    directoryToRemoveSoFar.append(directoryPiece);
                    dir.mkdir();
                }
            }
        }

        builtDirectory = directoryToRemoveSoFar.toString();
    }

    /**
     * Removes any directories added as part of building the batch directory
     */
    protected void removeBatchDirectory(String batchDirectory) {
        String unbuiltDirectory = batchDirectory.substring(0, batchDirectory.length()-builtDirectory.length());

        String pathToUnbuild = new String(batchDirectory);
        while (!unbuiltDirectory.equals(pathToUnbuild)) {
            File pathToUnbuildFile = new File(pathToUnbuild);
            clearAllFilesInDirectory(pathToUnbuildFile);
            pathToUnbuildFile.delete();

            int lastSeperator = pathToUnbuild.lastIndexOf('/');
            pathToUnbuild = pathToUnbuild.substring(0, lastSeperator);
        }
    }

    /**
     * Removes all the files within the given directory
     * @param dir the directory to delete files from
     */
    protected void clearAllFilesInDirectory(File dir) {
        for (File f : dir.listFiles()) {
            f.delete();
        }
    }

    /**
     * An inner class to point to a specific entry in a group
     */
    protected class EntryHolder {
        private String baseFileName;
        private String transactionLine;

        /**
         * Constructs a OriginEntryTestBase.EntryHolder
         * @param baseFileName the group that the entry to point to is in
         * @param transactionLine the line number of the entry
         */
        public EntryHolder(String baseFileName, String transactionLine) {
            this.baseFileName = baseFileName;
            this.transactionLine = transactionLine;
        }

        /**
         * @return the base file name
         */
        public String getBaseFileName() {
            return this.baseFileName;
        }

        /**
         * @return the transaction line
         */
        public String getTransactionLine() {
            return this.transactionLine;
        }
    }

    /**
     * An inner class to filter only the files for this batch run
     */

    class BatchFilenameFilter implements FilenameFilter {

        /**
         * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
         */
        @Override
        public boolean accept(File dir, String name) {
            return name.endsWith(GeneralLedgerConstants.BatchFileSystem.EXTENSION);
        }
    }

    /**
     * Given a type code and a bunch of transactions, creates a new backup file and adds all
     * the transactions to that file
     *
     * @param baseFileName the type code of the new file
     * @param transactions an array of String-formatted entries to save into the file
     */
    protected void loadInputTransactions(String fileName, String[] transactions) {
        final String fullFileName = batchDirectory + "/" + fileName + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        try {
            PrintStream ps = new PrintStream(fullFileName);
            for (int i = 0; i < transactions.length; i++) {
                ps.println(transactions[i]);
            }
            ps.close();
        }
        catch (FileNotFoundException fnfe) {
            throw new RuntimeException("Could not open file "+fullFileName);
        }
    }

    /**
     * Deletes everything in the expenditure transaction table
     */
    protected void clearExpenditureTable() {
        unitTestSqlDao.sqlCommand("delete from GL_EXPEND_TRN_MT");
    }

    /**
     * Deletes everything in the sufficient fund balance table
     */
    protected void clearSufficientFundBalanceTable() {
        unitTestSqlDao.sqlCommand("delete from GL_SF_BALANCES_T");
    }

    /**
     * Deletes all entries in the entry table with the given chart code and account number
     *
     * @param fin_coa_cd the chart code of entries to delete
     * @param account_nbr the account number of entries to delete
     */
    protected void clearGlEntryTable(String fin_coa_cd, String account_nbr) {
        unitTestSqlDao.sqlCommand("delete from GL_ENTRY_T where fin_coa_cd = '" + fin_coa_cd + "' and account_nbr = '" + account_nbr + "'");
    }

    /**
     * Deletes everything in the gl reversal table
     */
    protected void clearReversalTable() {
        unitTestSqlDao.sqlCommand("delete from GL_REVERSAL_T");
    }

    /**
     * Deletes everything in the gl balance table
     */
    protected void clearGlBalanceTable() {
        unitTestSqlDao.sqlCommand("delete from GL_BALANCE_T");
    }

    /**
     * Deletes everything in the gl encumbrance table.
     */
    protected void clearEncumbranceTable() {
        unitTestSqlDao.sqlCommand("delete from GL_ENCUMBRANCE_T");
    }

    /**
     * Deletes everything in the gl account balance table
     */
    protected void clearGlAccountBalanceTable() {
        unitTestSqlDao.sqlCommand("delete from GL_ACCT_BALANCES_T");
    }

    /**
     * Deletes all files in the batch test directory
     */
    protected void clearBatchFiles() {
        //I didn't see a deleteAll method, so...
        for (File file : new File(batchDirectory).listFiles(new BatchFilenameFilter())) {
            file.delete();
        }
    }

    /**
     * Check all the entries in the file against the data passed in EntryHolder[]. If any of them are different, assert an
     * error.
     *
     * @param fileCount the expected number of files
     * @param requiredEntries an array of expected String-formatted entries to check against
     */
    protected void assertOriginEntries(int fileCount, EntryHolder[] requiredEntries) {
        //we do not need to call clearCache() since no dao and jdbc calls mixted in this method.
        //refer to KFSMI-7637
        // persistenceService.clearCache();

        File[] files = new File(batchDirectory).listFiles(new BatchFilenameFilter());
        assertEquals("Number of groups is wrong: " + Arrays.toString(files), fileCount, files.length);

        List<EntryHolder> sortedEntryTransactions = new ArrayList<EntryHolder>();
        for (File file : files) {
            BufferedReader br = null;
            try {
                br = new BufferedReader(new FileReader(file));
                String type = file.getName().replaceFirst(GeneralLedgerConstants.BatchFileSystem.EXTENSION, "");
                // FIXME have to do something to bring types and filenames into sync, and probably have to remove separator character too
                String line = null;

                while ((line = br.readLine()) != null) {
                    sortedEntryTransactions.add(new EntryHolder(type,line));
                }
            }
            catch (FileNotFoundException fnfe) {
                throw new RuntimeException(fnfe);
            }
            catch (IOException ioe) {
                throw new RuntimeException(ioe);
            }
        }

        // now, sort the lines here to avoid any DB sorting issues
        //in the origin entry group version, this would sort the groups in order of creation, but in the files version,
        //it will sort the files in alphabetical order by type; entries within the groups (files) should be sorted the same
        //I don't foresee a problem with this...
        Comparator<EntryHolder> entryHolderComparator = new Comparator<EntryHolder>() {
            @Override
            public int compare(EntryHolder o1, EntryHolder o2) {
                int groupCompareResult = o1.baseFileName.compareTo(o2.baseFileName);
                if (groupCompareResult == 0) {
                    return o1.transactionLine.compareTo(o2.transactionLine);
                }
                else {
                    return groupCompareResult;
                }
            }
        };
        Collections.sort(sortedEntryTransactions, entryHolderComparator);
        Arrays.sort(requiredEntries, entryHolderComparator);

        // This is for debugging purposes - change to true for output
        System.err.println("Files:");
        for (File file : files) {
            System.err.println("F:" + file.getName());
        }

        System.err.println("Transactions:");
        for (EntryHolder element : sortedEntryTransactions) {
            System.err.println("L:" + element.baseFileName + " " + element.transactionLine);
        }
        System.err.println("Expected Transactions:");
        for (EntryHolder element : requiredEntries) {
            System.err.println("L:" + element.baseFileName + " " + element.transactionLine);
        }

        assertEquals("Wrong number of transactions in Origin Entry\nExpected: " + requiredEntries + "\nActual: " + sortedEntryTransactions, requiredEntries.length, sortedEntryTransactions.size());

        int count = 0;
        for (EntryHolder foundTransaction : sortedEntryTransactions) {

            // Check file type
            assertEquals("Group for transaction " + count + " is wrong", requiredEntries[count].baseFileName, foundTransaction.baseFileName);

            // Check transaction - this is done this way so that Anthill prints the two transactions to make
            // resolving the issue easier.

            String expected = requiredEntries[count].transactionLine.substring(0, 173);// trim();
            String found = foundTransaction.transactionLine.substring(0, 173);// trim();

            if (!found.equals(expected)) {
                System.err.println("Expected transaction: " + expected);
                System.err.println("Found transaction:    " + found);

                assertEquals("Transaction " + count + " doesn't match expected output", expected, found);
            }
            count++;
        }
    }

    protected static Object[] FLEXIBLE_OFFSET_ENABLED_FLAG = { OffsetDefinition.class, KFSConstants.SystemGroupParameterNames.FLEXIBLE_OFFSET_ENABLED_FLAG };

    /**
     * Resets the flexible offset and flexible claim on cash parameters, so that processes running as unit tests have consistent behaviors
     * @throws Exception if the parameters could not be reset for some reason
     */
    protected void resetAllEnhancementFlags() throws Exception {
        setApplicationConfigurationFlag((Class<?>) FLEXIBLE_OFFSET_ENABLED_FLAG[0], (String) FLEXIBLE_OFFSET_ENABLED_FLAG[1], false);
    }

    /**
     * Resets a parameter for the sake of the unit test
     *
     * @param componentClass the module class of the parameter
     * @param name the name of the parameter to reset
     * @param value the new value for the parameter
     * @throws Exception thrown if some vague thing goes wrong
     */
    protected void setApplicationConfigurationFlag(Class<?> componentClass, String name, boolean value) throws Exception {
        TestUtils.setSystemParameter(componentClass, name, value ? "Y" : "N");
    }

}
