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
package org.kuali.kfs.gl.batch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.filefilter.AndFileFilter;
import org.apache.commons.io.filefilter.PrefixFileFilter;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.batch.service.impl.FileEnterpriseFeederServiceImpl;
import org.kuali.kfs.gl.businessobject.OriginEntryGroup;
import org.kuali.kfs.gl.businessobject.OriginEntryTestBase;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;

/**
 * This class tests the enterprise feeder service. The concept of a file set is pervasive throughout this test. A file set consists
 * of:
 * <ul>
 * <li>an ID value, starting at 1. The next file set is identified with 2, and so on. NOTE: when the ID value is used in a filename
 * (see below), it is left-padded with zero's to make it 3 characters long (e.g. 001, 002, 010, 865). </li>
 * <li>a data file, this file will be named entp_test_file_<id value>.data</li>
 * <li>a reconciliation file, this file will be named entp_test_file<id value>.recon . </li>
 * </ul>
 * The IDs are left-padded so that they can be properly sorted by file name, so that files may be fed in a predictable order. For
 * each particular test case in the code, a list of file sets IDs are selected to be tested. The done files will be generated for
 * each of the selected file set IDs before the feeder is invoked, and they should hopefully be deleted by the feeder. Dependencies:
 * <ul>
 * <li>there are file set in the enterprise feeder staging directory (injected via Spring) that are necessary for the feeder to be
 * tested correctly</li>
 * <li>the staging directory may contain data and reconciliation files not related to this test, but they may NOT include done
 * files. Having done files will result in TEST FAILURE</li>
 * <li> if any file sets are added and/or modified, then the method {@link FileEnterpriseFeederTest#isFilesetValid(int)} must be
 * modified, if necessary.</li>
 * <li>The files for all file sets must be in the directory configured for use by the enterprise feeder step bean. Running the
 * dist-local target in build.xml should place the files in the external configuration directory, or they can be copied from
 * build/externalConfigDirectory/static/staging/GL . See the entp_test_file_readme.txt file for more information about the file
 * sets.</li>
 * </ul>
 */
@ConfigureContext
public class FileEnterpriseFeederTest extends OriginEntryTestBase {
    // to be populated in setUp
    private List<String> prerequisiteDataFiles;
    private List<String> prerequisiteReconFiles;


    public static final String TEST_FILE_PREFIX = "entp_test_file_";
    public static final int NUM_TEST_FILE_SETS = 4;
    public static final int ORIGIN_ENTRY_TEXT_LINE_LENGTH = 173;

    /**
     * Sets up the proper file names needed for the test.
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        prerequisiteDataFiles = new ArrayList<String>();
        prerequisiteReconFiles = new ArrayList<String>();

        for (int i = 1; i <= NUM_TEST_FILE_SETS; i++) {
            prerequisiteDataFiles.add(generateDataFilename(i));
            prerequisiteReconFiles.add(generateReconFilename(i));
        }

        checkNecessaryFilesPresentAndReadable();
        checkNotOnProduction();
    }

    /**
     * Tests to ensure that the feeder will not feed upon anything if no done files exist.
     * 
     * @throws Exception thrown if some vague thing goes wrong
     */
    // @RelatesTo(RelatesTo.JiraIssue.KULUT30)
    public final void testNoDoneFiles() throws Exception {
//        List<Integer> fileSets = Collections.emptyList();
//
//        initializeDatabaseForTest();
//
//        assertNoExtraDoneFilesExistAndCreateDoneFilesForSets(fileSets);
//
//        EnterpriseFeedStep feederStep = SpringContext.getBean(EnterpriseFeedStep.class);
//        feederStep.execute(getClass().getName(), dateTimeService.getCurrentDate());
//
//        assertDoneFilesDeleted(fileSets);
//
//        OriginEntryGroup group = getGroupCreatedByFeed();
//        System.out.println(group);
//        int groupCount = originEntryService.getGroupCount(group.getId());
//        assertTrue("Expected group count of 0, but got group count of " + groupCount, groupCount == 0);
//
//        assertNoExtraTestDoneFilesExistAfterTest();
    }

    /**
     * This method tests that the uploading of a single OK file set
     * 
     * @throws Exception thrown if some vague thing goes wrong
     */
    // @RelatesTo(RelatesTo.JiraIssue.KULUT30)
    public final void testOneOkFileSet() throws Exception {
//        List<Integer> fileSets = new ArrayList<Integer>();
//        fileSets.add(2);
//
//        initializeDatabaseForTest();
//        assertNoExtraDoneFilesExistAndCreateDoneFilesForSets(fileSets);
//
//        EnterpriseFeedStep feederStep = SpringContext.getBean(EnterpriseFeedStep.class);
//        assertTrue("Step should have returned true", feederStep.execute(getClass().getName(), dateTimeService.getCurrentDate()));
//
//        assertDoneFilesDeleted(fileSets);
//
//        OriginEntryGroup group = getGroupCreatedByFeed();
//        System.out.println(group);
//        System.out.println(originEntryService.getGroupCount(group.getId()));
//
//        List<String> expectedEntries = buildVerificationEntries(fileSets, group);
//        assertOriginEntriesLoaded(expectedEntries, group);
//
//        assertNoExtraTestDoneFilesExistAfterTest();
    }

    /**
     * Tests the uploading of two files, one parsable, the other not
     * 
     * @throws Exception thrown if anything goes wrong
     */
    // @RelatesTo(RelatesTo.JiraIssue.KULUT30)
    public final void testOneOkOneBadFileSet() throws Exception {
//        List<Integer> fileSets = new ArrayList<Integer>();
//        fileSets.add(1);
//        fileSets.add(2);
//
//        initializeDatabaseForTest();
//        assertNoExtraDoneFilesExistAndCreateDoneFilesForSets(fileSets);
//
//        EnterpriseFeedStep feederStep = SpringContext.getBean(EnterpriseFeedStep.class);
//        assertTrue("Step should have returned true", feederStep.execute(getClass().getName(), dateTimeService.getCurrentDate()));
//
//        assertDoneFilesDeleted(fileSets);
//
//        OriginEntryGroup group = getGroupCreatedByFeed();
//        System.out.println(group);
//        System.out.println(originEntryService.getGroupCount(group.getId()));
//
//        List<String> expectedEntries = buildVerificationEntries(fileSets, group);
//        assertOriginEntriesLoaded(expectedEntries, group);
//
//        assertNoExtraTestDoneFilesExistAfterTest();
    }

    /**
     * Tests that the enterprise feeder will successfully run, even when fed a bad reconciliation file
     * 
     * @throws Exception
     */
    // @RelatesTo(RelatesTo.JiraIssue.KULUT30)
    public final void testBadReconFileSet() throws Exception {
//        List<Integer> fileSets = new ArrayList<Integer>();
//        fileSets.add(2);
//        fileSets.add(3);
//
//        initializeDatabaseForTest();
//        assertNoExtraDoneFilesExistAndCreateDoneFilesForSets(fileSets);
//
//        EnterpriseFeedStep feederStep = SpringContext.getBean(EnterpriseFeedStep.class);
//        DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
//        assertTrue("Step should have returned true", feederStep.execute(getClass().getName(), dateTimeService.getCurrentDate()));
//
//        assertDoneFilesDeleted(fileSets);
//
//        OriginEntryGroup group = getGroupCreatedByFeed();
//        System.out.println(group);
//        System.out.println(originEntryService.getGroupCount(group.getId()));
//
//        List<String> expectedEntries = buildVerificationEntries(fileSets, group);
//        assertOriginEntriesLoaded(expectedEntries, group);
//
//        assertNoExtraTestDoneFilesExistAfterTest();
    }

    /**
     * Tests that the enterprise feeder will successfully run, even when there's a missing data file
     * 
     * @throws Exception thrown if anything goes wrong
     */
    // @RelatesTo(RelatesTo.JiraIssue.KULUT30)
    public final void testDataFileMissing() throws Exception {
//        List<Integer> fileSets = new ArrayList<Integer>();
//        fileSets.add(2);
//
//        initializeDatabaseForTest();
//        assertNoExtraDoneFilesExistAndCreateDoneFilesForSets(fileSets);
//
//        EnterpriseFeedStep feederStep = SpringContext.getBean(EnterpriseFeedStep.class);
//        DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
//        assertTrue("Step should have returned true", feederStep.execute(getClass().getName(), dateTimeService.getCurrentDate()));
//
//        assertDoneFilesDeleted(fileSets);
//
//        OriginEntryGroup group = getGroupCreatedByFeed();
//        System.out.println(group);
//        System.out.println(originEntryService.getGroupCount(group.getId()));
//
//        List<String> expectedEntries = buildVerificationEntries(fileSets, group);
//        assertOriginEntriesLoaded(expectedEntries, group);
//
//        assertNoExtraTestDoneFilesExistAfterTest();
    }

    /**
     * Clears out the origin entry and origin entry group tables to prepare for the test
     */
    protected void initializeDatabaseForTest() {
        clearBatchFiles();
    }

    /**
     * Makes sure that the data files for this test exist; if not, throws an exception
     */
    // @RelatesTo(RelatesTo.JiraIssue.KULUT30)
    protected void checkNecessaryFilesPresentAndReadable() {
        boolean invalidFiles = false;
        StringBuilder problemFiles = new StringBuilder();

        for (int i = 0; i < prerequisiteDataFiles.size(); i++) {
            File file = new File(prerequisiteDataFiles.get(i));
            if (!file.exists() || !file.canRead()) {
                problemFiles.append(prerequisiteDataFiles.get(i)).append("; ");
            }
        }
        for (int i = 0; i < prerequisiteReconFiles.size(); i++) {
            File file = new File(prerequisiteReconFiles.get(i));
            if (!file.exists() || !file.canRead()) {
                problemFiles.append(prerequisiteReconFiles.get(i)).append("; ");
            }
        }

        if (problemFiles.length() != 0) {
            throw new RuntimeException("The following files required for testing are either missing or not readable: " + problemFiles.toString());
        }
    }

    /**
     * Zero pads an integer to be at least 3 digits long
     * 
     * @param value an integer value
     * @return a left zero padded String
     */
    protected String convertIntToString(int value) {
        if (value < 10) {
            return "00" + Integer.toString(value);
        }
        if (value < 100) {
            return "0" + Integer.toString(value);
        }
        return Integer.toString(value);
    }

    /**
     * Generates the full path and file name of a generated enterprise feed data file
     * 
     * @param fileSetId the integer id of the file that should have been generated
     * @return the full path and file name for the file
     */
    protected String generateDataFilename(int fileSetId) {
        String directoryPrefix = ((FileEnterpriseFeederServiceImpl) SpringContext.getBean(FileEnterpriseFeederServiceImpl.class)).getDirectoryName() + File.separator;
        return directoryPrefix + TEST_FILE_PREFIX + convertIntToString(fileSetId) + FileEnterpriseFeederServiceImpl.DATA_FILE_SUFFIX;
    }

    /**
     * Generates the full path and file name of a generated enterprise feed reconciliation file
     * 
     * @param fileSetId the integer id of the file that should have been generated
     * @return the full path and file name for the file
     */
    protected String generateReconFilename(int fileSetId) {
        String directoryPrefix = ((FileEnterpriseFeederServiceImpl) SpringContext.getBean(FileEnterpriseFeederServiceImpl.class)).getDirectoryName() + File.separator;
        return directoryPrefix + TEST_FILE_PREFIX + convertIntToString(fileSetId) + FileEnterpriseFeederServiceImpl.RECON_FILE_SUFFIX;
    }

    /**
     * Generates the full path and file name of a generated enterprise feed .done file
     * 
     * @param fileSetId the integer id of the file that should have been generated
     * @return the full path and file name for the file
     */
    protected String generateDoneFilename(int fileSetId) {
        String directoryPrefix = ((FileEnterpriseFeederServiceImpl) SpringContext.getBean(FileEnterpriseFeederServiceImpl.class)).getDirectoryName() + File.separator;
        return directoryPrefix + TEST_FILE_PREFIX + convertIntToString(fileSetId) + FileEnterpriseFeederServiceImpl.DONE_FILE_SUFFIX;
    }

    /**
     * This method asserts that there doesn't exist any done files in the enterprise feeder directory that do not begin with
     * DONE_FILE_PREFIX (see constants definition in this class). If there are files that begin w/ that prefix in the directory,
     * they are deleted. After checking/ deleting done files, it will then create a done files listed in the fileSets parameter.
     * 
     * @param fileSets A list of Integers, representing the done files that will be created. (see class description) to see how
     *        these integers map into file names.
     * @throws IOException if a file cannot be successfully read
     */
    protected void assertNoExtraDoneFilesExistAndCreateDoneFilesForSets(List<Integer> fileSets) throws IOException {
        FileFilter fileFilter = new SuffixFileFilter(FileEnterpriseFeederServiceImpl.DONE_FILE_SUFFIX);
        File directory = new File(((FileEnterpriseFeederServiceImpl) SpringContext.getBean(FileEnterpriseFeederServiceImpl.class)).getDirectoryName());
        File[] doneFiles = directory.listFiles(fileFilter);

        StringBuilder sb = new StringBuilder();
        for (File file : doneFiles) {
            if (file.getName().startsWith(TEST_FILE_PREFIX)) {
                // this is a test done file, just delete it
                file.delete();
            }
            else {
                // maybe someone put in files in the staging directory that shouldn't be there
                sb.append(file.getName() + ";");
            }
        }

        assertTrue("Done files exist in the directory ( " + sb.toString() + " ), which will cause this step to produce unexpected results when testing." + "  To run this test, the done files must be removed (do NOT do this if running on a production system).", doneFiles.length == 0);

        for (Integer setNumber : fileSets) {
            File doneFile = new File(generateDoneFilename(setNumber));
            if (!doneFile.exists()) {
                doneFile.createNewFile();
            }
        }
    }

    /**
     * Asserts true if no test done files exist. If so, method removes done files before assert.
     * 
     * @throws IOException thrown if a data file cannot be successfully read
     */
    protected void assertNoExtraTestDoneFilesExistAfterTest() throws IOException {
        FileFilter fileFilter = new AndFileFilter(new PrefixFileFilter(TEST_FILE_PREFIX), new SuffixFileFilter(FileEnterpriseFeederServiceImpl.DONE_FILE_SUFFIX));
        File directory = new File(((FileEnterpriseFeederServiceImpl) SpringContext.getBean(FileEnterpriseFeederServiceImpl.class)).getDirectoryName());
        File[] doneFiles = directory.listFiles(fileFilter);

        StringBuilder buf = new StringBuilder();
        for (File file : doneFiles) {
            buf.append(file.getName() + ";");
            file.delete();
        }

        assertTrue("The following test done files existed ( " + buf.toString() + " ), but shouldn't have.  These test done files have been deleted. ", buf.length() == 0);
    }

    /**
     * Asserts that there are no longer any existing .done files
     * 
     * @param fileSets a List of file sets to check .done files for
     * @throws IOException thrown if one of the files cannot be read for any reason
     */
    protected void assertDoneFilesDeleted(List<Integer> fileSets) throws IOException {
        StringBuilder buf = new StringBuilder();

        for (Integer setNumber : fileSets) {
            File doneFile = new File(generateDoneFilename(setNumber));
            if (doneFile.exists()) {
                buf.append(doneFile.getAbsolutePath()).append("; ");
            }
        }

        assertTrue("The following done files were not deleted: " + buf.toString(), buf.length() == 0);
    }

    /**
     * Converts the entries generated by the enterprise feed to String-formatted entries
     * 
     * @param fileSets the file sets to convert entries for
     * @param group not used as such
     * @return a List of String-formatted generated origin entries
     * @throws IOException thrown if one of the data files cannot be read successfully
     */
    protected List<String> buildVerificationEntries(List<Integer> fileSets, OriginEntryGroup group) throws IOException {
        List<String> entries = new ArrayList<String>();

        for (Integer fileSetId : fileSets) {
            if (isFilesetLoadable(fileSetId)) {
                File file = new File(generateDataFilename(fileSetId));
                BufferedReader buf = new BufferedReader(new FileReader(file));

                String line;
                while ((line = buf.readLine()) != null) {
                    entries.add(line.substring(0, ORIGIN_ENTRY_TEXT_LINE_LENGTH));
                }
            }
        }
        return entries;
    }

    /**
     * Fails if the origin entries in the list do not match the origin entries associated with the passed in group.
     * 
     * @param expectedEntries the entries that were the expected output of the enterprise feed process
     * @param groupOfLoadedEntries the entries that were really the output of the enterprise feed process
     */
    protected void assertOriginEntriesLoaded(List<String> expectedEntries, OriginEntryGroup groupOfLoadedEntries) {
        //TODO:- do it later
//        Collection<OriginEntryFull> actualEntries = originEntryDao.testingGetAllEntries();
//
//        assertEquals("Expected and actual number of loaded origin entries do not match.", expectedEntries.size(), actualEntries.size());
//
//        for (OriginEntryFull actualEntry : actualEntries) {
//            String line = actualEntry.getLine().substring(0, ORIGIN_ENTRY_TEXT_LINE_LENGTH);
//            assertTrue("Unexpected line loaded into origin entry table: " + line, expectedEntries.remove(line));
//        }
//
//        if (!expectedEntries.isEmpty()) {
//            System.err.println("The following expected entries were not loaded into the database: ");
//            for (String expectedEntry : expectedEntries) {
//                System.err.println(expectedEntry);
//            }
//            fail("Some expected entries were not loaded into the database.  See System.err output for details.");
//        }
    }

    /**
     * Determines whether the files in a set are able to be loaded because of the lack of parse errors and the lack of
     * reconciliation errors
     * 
     * @param fileSetId the integer id of the file set
     * @return true if it can be loaded, false otherwise
     */
    protected boolean isFilesetLoadable(int fileSetId) {
        Set<Integer> loadableSets = new HashSet<Integer>();
        loadableSets.add(2);

        return loadableSets.contains(fileSetId);
    }

    /**
     * Throws an exception if running on production
     */
    protected void checkNotOnProduction() {
        ConfigurationService kualiConfigurationService = SpringContext.getBean(ConfigurationService.class);

        if (StringUtils.equals(kualiConfigurationService.getPropertyValueAsString(KFSConstants.PROD_ENVIRONMENT_CODE_KEY), kualiConfigurationService.getPropertyValueAsString(KFSConstants.ENVIRONMENT_KEY))) {
            throw new RuntimeException("Can't run on production");
        }
    }
}
