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
package org.kuali.module.gl.batch;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.lang.StringUtils;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.UnitTestSqlDao;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.gl.OriginEntryTestBase;
import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.OriginEntrySource;
import org.kuali.module.gl.service.OriginEntryService;
import org.kuali.module.gl.service.impl.FileEnterpriseFeederServiceImpl;
import org.kuali.test.KualiTestBase;
import org.kuali.test.WithTestSpringContext;

/**
 * This class tests the enterprise feeder service.
 * 
 * The concept of a file set is pervasive throughout this test.  A file set consists of:
 * <ul>
 * <li>an ID value, starting at 1.  The next file set is identified with 2, and so on.  NOTE: when the ID  
 * value is used in a filename (see below), it is left-padded with zero's to make it 3 characters long (e.g. 001, 002, 010, 865). </li>
 * <li>a data file, this file will be named entp_test_file_<id value>.data</li>
 * <li>a reconciliation file, this file will be named entp_test_file<id value>.recon . </li>
 * </ul>
 * 
 * The IDs are left-padded so that they can be properly sorted by file name, so that files may be fed in a predictable order.
 * 
 * For each particular test case in the code, a list of file sets IDs are selected to be tested.
 * The done files will be generated for each of the selected file set IDs before the feeder is invoked, and they should hopefully be 
 * deleted by the feeder.
 *   
 * Dependencies: 
 * <ul>
 * <li>there are file set in the enterprise feeder staging directory (injected via Spring)
 *  that are necessary for the feeder to be tested correctly</li>
 * <li>the staging directory may contain data and reconciliation files not related to this test,
 * but they may NOT include done files.  Having done files will result in TEST FAILURE</li>
 * <li> if any file sets are added and/or modified, then the method {@link FileEnterpriseFeederTest#isFilesetValid(int)} must be
 * modified, if necessary.</li>
 * <li>The files for all file sets must be in the directory configured for use by the enterprise feeder step bean.  Running the dist-local
 * target in build.xml should place the files in the external configuration directory, or they can be copied from 
 * build/externalConfigDirectory/static/staging/GL .  See the entp_test_file_readme.txt file for more information about the file sets.</li>
 * </ul>
 * 
 */
@WithTestSpringContext
public class FileEnterpriseFeederTest extends OriginEntryTestBase {
    // to be populated in setUp
    private List<String> prerequisiteDataFiles;
    private List<String> prerequisiteReconFiles;
    
    
    public static final String TEST_FILE_PREFIX = "entp_test_file_";
    public static final int NUM_TEST_FILE_SETS = 4;
    public static final int ORIGIN_ENTRY_TEXT_LINE_LENGTH = 173;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        prerequisiteDataFiles = new ArrayList<String> ();
        prerequisiteReconFiles = new ArrayList<String> ();
        
        for (int i = 1; i <= NUM_TEST_FILE_SETS; i++) {
            prerequisiteDataFiles.add(generateDataFilename(i));
            prerequisiteReconFiles.add(generateReconFilename(i));
        }
        
        checkNecessaryFilesPresentAndReadable();
        checkNotOnProduction();
    }
    
    /**
     * Tests to ensure that the feeder will not feed upon anything if no done files exist.
     * @throws Exception
     */
    public final void testNoDoneFiles() throws Exception {
        List<Integer> fileSets = Collections.emptyList();
        
        initializeDatabaseForTest();
        assertNoExtraDoneFilesExistAndCreateDoneFilesForSets(fileSets);

        FeederStep feederStep = SpringServiceLocator.getOriginEntryFeederStep();
        feederStep.execute(getClass().getName());
        
        assertDoneFilesDeleted(fileSets);
        
        OriginEntryGroup group = getGroupCreatedByFeed();
        System.out.println(group);
        int groupCount = originEntryService.getGroupCount(group.getId());
        assertTrue("Expected group count of 0, but got group count of " + groupCount, groupCount == 0);
    }
    
    /**
     * This method tests that the uploading of a single OK file set
     * 
     * @throws Exception
     */
    public final void testOneOkFileSet() throws Exception {
        List<Integer> fileSets = new ArrayList<Integer>();
        fileSets.add(2);
        
        initializeDatabaseForTest();
        assertNoExtraDoneFilesExistAndCreateDoneFilesForSets(fileSets);

        FeederStep feederStep = SpringServiceLocator.getOriginEntryFeederStep();
        assertTrue("Step should have returned true", feederStep.execute(getClass().getName()));
        
        assertDoneFilesDeleted(fileSets);
        
        OriginEntryGroup group = getGroupCreatedByFeed();
        System.out.println(group);
        System.out.println(originEntryService.getGroupCount(group.getId()));
        
        List<String> expectedEntries = buildVerificationEntries(fileSets, group);
        assertOriginEntriesLoaded(expectedEntries, group);
    }
    
    /**
     * This method...
     * @throws Exception
     */
    public final void testOneOkOneBadFileSet() throws Exception {
        List<Integer> fileSets = new ArrayList<Integer>();
        fileSets.add(1);
        fileSets.add(2);
        
        initializeDatabaseForTest();
        assertNoExtraDoneFilesExistAndCreateDoneFilesForSets(fileSets);

        FeederStep feederStep = SpringServiceLocator.getOriginEntryFeederStep();
        assertTrue("Step should have returned true", feederStep.execute(getClass().getName()));
        
        assertDoneFilesDeleted(fileSets);
        
        OriginEntryGroup group = getGroupCreatedByFeed();
        System.out.println(group);
        System.out.println(originEntryService.getGroupCount(group.getId()));
        
        List<String> expectedEntries = buildVerificationEntries(fileSets, group);
        assertOriginEntriesLoaded(expectedEntries, group);
    }
    
    public final void testBadReconFileSet() throws Exception {
        List<Integer> fileSets = new ArrayList<Integer>();
        fileSets.add(2);
        fileSets.add(3);
        
        initializeDatabaseForTest();
        assertNoExtraDoneFilesExistAndCreateDoneFilesForSets(fileSets);

        FeederStep feederStep = SpringServiceLocator.getOriginEntryFeederStep();
        assertTrue("Step should have returned true", feederStep.execute(getClass().getName()));
        
        assertDoneFilesDeleted(fileSets);
        
        OriginEntryGroup group = getGroupCreatedByFeed();
        System.out.println(group);
        System.out.println(originEntryService.getGroupCount(group.getId()));
        
        List<String> expectedEntries = buildVerificationEntries(fileSets, group);
        assertOriginEntriesLoaded(expectedEntries, group);
    }
    
    public final void testDataFileMissing() throws Exception {
        List<Integer> fileSets = new ArrayList<Integer>();
        fileSets.add(2);
        
        initializeDatabaseForTest();
        assertNoExtraDoneFilesExistAndCreateDoneFilesForSets(fileSets);

        FeederStep feederStep = SpringServiceLocator.getOriginEntryFeederStep();
        assertTrue("Step should have returned true", feederStep.execute(getClass().getName()));
        
        assertDoneFilesDeleted(fileSets);
        
        OriginEntryGroup group = getGroupCreatedByFeed();
        System.out.println(group);
        System.out.println(originEntryService.getGroupCount(group.getId()));
        
        List<String> expectedEntries = buildVerificationEntries(fileSets, group);
        assertOriginEntriesLoaded(expectedEntries, group);
    }
    
    protected void initializeDatabaseForTest() {
        clearOriginEntryTables();
    }
    
    protected OriginEntryGroup getGroupCreatedByFeed() {
        Collection<OriginEntryGroup> groups = originEntryGroupService.getAllOriginEntryGroup();
        assertEquals("Either the initializeDatabaseFOrTest method was not called before " 
                + "running the test, or more than one group was created by the feeder service.", groups.size(), 1);
        OriginEntryGroup group = groups.iterator().next();
        assertEquals("Unexpected origin entry group source code: expected: " + OriginEntrySource.ENTERPRISE_FEED 
                + " actual: " + group.getSourceCode(), OriginEntrySource.ENTERPRISE_FEED, group.getSourceCode());
        
        assertTrue("Valid flag of group should be true.", group.getValid().booleanValue());
        assertTrue("Process flag of group should be true.", group.getProcess().booleanValue());
        assertTrue("Scrub flag of group should be true.", group.getScrub().booleanValue());
        
        return group;
    }
    
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
    
    protected String convertIntToString(int value) {
        if (value < 10) {
            return "00" + Integer.toString(value);
        }
        if (value < 100) {
            return "0" + Integer.toString(value);
        }
        return Integer.toString(value);
    }
    
    protected String generateDataFilename(int fileSetId) {
        String directoryPrefix = ((FileEnterpriseFeederServiceImpl)SpringServiceLocator.getOriginEntryEnterpriseFeederService()).getDirectoryName() + File.separator;
        return directoryPrefix + TEST_FILE_PREFIX + convertIntToString(fileSetId) + FileEnterpriseFeederServiceImpl.DATA_FILE_SUFFIX;
    }
    
    protected String generateReconFilename(int fileSetId) {
        String directoryPrefix = ((FileEnterpriseFeederServiceImpl)SpringServiceLocator.getOriginEntryEnterpriseFeederService()).getDirectoryName() + File.separator;
        return directoryPrefix + TEST_FILE_PREFIX + convertIntToString(fileSetId) + FileEnterpriseFeederServiceImpl.RECON_FILE_SUFFIX;
    }
    
    protected String generateDoneFilename(int fileSetId) {
        String directoryPrefix = ((FileEnterpriseFeederServiceImpl)SpringServiceLocator.getOriginEntryEnterpriseFeederService()).getDirectoryName() + File.separator;
        return directoryPrefix + TEST_FILE_PREFIX + convertIntToString(fileSetId) + FileEnterpriseFeederServiceImpl.DONE_FILE_SUFFIX;
    }
    
    protected void assertNoExtraDoneFilesExistAndCreateDoneFilesForSets(List<Integer> fileSets) throws IOException {
        FileFilter fileFilter = new SuffixFileFilter(FileEnterpriseFeederServiceImpl.DONE_FILE_SUFFIX);
        File directory = new File(((FileEnterpriseFeederServiceImpl)SpringServiceLocator.getOriginEntryEnterpriseFeederService()).getDirectoryName());
        File[] doneFiles = directory.listFiles(fileFilter);
        assertTrue("Done files exist in the directory, which will cause this step to produce unexpected results when testing." + 
                "  To run this test, the done files must be removed (do NOT do this if running on a production system).", doneFiles.length == 0);
        
        for (Integer setNumber : fileSets) {
            File doneFile = new File(generateDoneFilename(setNumber));
            if (!doneFile.exists()) {
                doneFile.createNewFile();
            }
        }
    }
    
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
     * @param expectedEntries
     * @param groupOfLoadedEntries
     */
    protected void assertOriginEntriesLoaded(List<String> expectedEntries, OriginEntryGroup groupOfLoadedEntries) {
        Collection<OriginEntry> actualEntries = originEntryDao.testingGetAllEntries();
        
        assertEquals("Expected and actual number of loaded origin entries do not match.", expectedEntries.size(), actualEntries.size());
        
        for (OriginEntry actualEntry : actualEntries) {
            String line = actualEntry.getLine().substring(0, ORIGIN_ENTRY_TEXT_LINE_LENGTH);
            assertTrue("Unexpected line loaded into origin entry table: " + line, expectedEntries.remove(line));
        }
        
        if (!expectedEntries.isEmpty()) {
            System.err.println("The following expected entries were not loaded into the database: ");
            for (String expectedEntry : expectedEntries) {
                System.err.println(expectedEntry);
            }
            fail("Some expected entries were not loaded into the database.  See System.err output for details.");
        }
    }
    /**
     * Determines whether the files in a set are able to be loaded because of the lack of parse errors and the lack of reconciliation errors
     * 
     * @param fileSetId
     * @return
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
        KualiConfigurationService kualiConfigurationService = SpringServiceLocator.getKualiConfigurationService();
        
        if (StringUtils.equals(kualiConfigurationService.getPropertyString(KFSConstants.PROD_ENVIRONMENT_CODE_KEY), kualiConfigurationService.getPropertyString(KFSConstants.ENVIRONMENT_KEY))) {
            throw new RuntimeException("Can't run on production");
        }
    }
}