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
package org.kuali.kfs.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.AuthorizationException;
import org.kuali.core.service.UniversalUserService;
import org.kuali.kfs.batch.BatchInputFileType;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.exceptions.FileStorageException;
import org.kuali.module.financial.batch.pcard.ProcurementCardInputFileType;
import org.kuali.module.gl.batch.collector.CollectorInputFileType;
import org.kuali.test.ConfigureContext;
import org.kuali.test.KualiTestConstants.TestConstants.Data4;

/**
 * Tests the BatchInputFileService. TEST DEPENDENCIES The following are external configurations not setup by the test case that are
 * necessary for the test to run correctly: 1) User identified by the constant Data4_USER_ID2 must be a member of the workgroup
 * given by constant Data2.FP_OPERATIONS. 2) User identified by the constant Data4_USER_ID1 must NOT be a member of the workgroup
 * given by constant Data2.FP_OPERATIONS. 3) Five xml files must exist in the PCDO staging directory. Name of files is not important.
 * 4) One Collector files must exist in Collector staging directory. File name should not contain the usernames given by constants
 * Data4_USER_ID2 and Data4_USER_ID1. Note the files in #3 & #4 are created by the project build from files located in project
 * folder buld/configurationFiles/externalConfigDirectory/static/staging/
 * 
 * @see org.kuali.kfs.service.BatchInputFileService Unit tests for this service are also in:
 * @see org.kuali.kfs.service.BatchInputServiceParseTest
 * @see org.kuali.kfs.service.BatchInputServiceSystemParametersTest
 */
@ConfigureContext
public class BatchInputFileServiceTest extends KualiTestBase {
    private static final String TEST_BATCH_XML_DIRECTORY = "org/kuali/kfs/batch/xml/";

    private BatchInputFileService batchInputFileService;

    private String testFileIdentifier;
    private InputStream validPCDOFileContents;
    private InputStream validCollectorFileContents;

    private BatchInputFileType pcdoBatchInputFileType;
    private BatchInputFileType collectorBatchInputFileType;

    private UniversalUser validWorkgroupUser;
    private UniversalUser invalidWorkgroupUser;

    private List<File> createdTestFiles;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        batchInputFileService = SpringContext.getBean(BatchInputFileService.class);
        pcdoBatchInputFileType = SpringContext.getBean(ProcurementCardInputFileType.class);
        collectorBatchInputFileType = SpringContext.getBean(CollectorInputFileType.class);

        testFileIdentifier = "junit" + RandomUtils.nextInt();
        validPCDOFileContents = ClassLoader.getSystemResourceAsStream(TEST_BATCH_XML_DIRECTORY + "BatchInputValidPCDO.xml");
        validCollectorFileContents = ClassLoader.getSystemResourceAsStream(TEST_BATCH_XML_DIRECTORY + "BatchInputValidCollector.xml");

        validWorkgroupUser = SpringContext.getBean(UniversalUserService.class).getUniversalUserByAuthenticationUserId(Data4.USER_ID2);
        invalidWorkgroupUser = SpringContext.getBean(UniversalUserService.class).getUniversalUserByAuthenticationUserId(Data4.USER_ID1);

        createdTestFiles = new ArrayList();
    }

    /**
     * Clean up any files created during test methods that were not removed (possibly because of a failure).
     * 
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();

        if (createdTestFiles != null) {
            for (File createdFile : createdTestFiles) {
                if (createdFile.exists()) {
                    createdFile.delete();
                }
                String doneFileName = StringUtils.substringBeforeLast(createdFile.getPath(), ".") + ".done";
                File doneFile = new File(doneFileName);
                if (doneFile.exists()) {
                    doneFile.delete();
                }
            }
        }
    }


    /**
     * Checks file was created succesfully for valid call to save method.
     */
    public final void testSave_valid() throws Exception {
        String savedFileName = batchInputFileService.save(validWorkgroupUser, pcdoBatchInputFileType, testFileIdentifier, validPCDOFileContents, new ArrayList());

        File expectedFile = new File(savedFileName);
        createdTestFiles.add(expectedFile);

        assertTrue("uploaded pcdo file not found", expectedFile.exists());
        assertTrue("uploaded pcdo file is empty", expectedFile.length() > 0);

        checkForDoneFile(expectedFile);

        // remove file so we can test collector upload
        expectedFile.delete();

        savedFileName = batchInputFileService.save(validWorkgroupUser, collectorBatchInputFileType, testFileIdentifier, validCollectorFileContents, new MockCollectorBatch());

        expectedFile = new File(savedFileName);
        createdTestFiles.add(expectedFile);

        assertTrue("uploaded collector file not found", expectedFile.exists());
        assertTrue("uploaded collector file is empty", expectedFile.length() > 0);

        checkForDoneFile(expectedFile);
    }

    /**
     * Checks for a done file with the same name as the given batch file.
     */
    private final void checkForDoneFile(File batchFile) {
        String doneFileName = StringUtils.substringBeforeLast(batchFile.getPath(), ".") + ".done";
        File doneFile = new File(doneFileName);

        assertTrue("done file " + doneFile.getPath() + " does not exist", doneFile.exists());
    }


    /**
     * Assures AuthorizationException is being thrown when the user does not have permissions on the given batch input type.
     */
    public final void testSave_incorrectUserPermissions() throws Exception {
        boolean failedAsExpected = false;
        try {
            batchInputFileService.save(invalidWorkgroupUser, pcdoBatchInputFileType, testFileIdentifier, validPCDOFileContents, new ArrayList());
        }
        catch (AuthorizationException e) {
            failedAsExpected = true;
        }

        assertTrue("authorization exception not thrown for user with invalid permissions on pcdo batch type", failedAsExpected);

        failedAsExpected = false;
        try {
            batchInputFileService.save(invalidWorkgroupUser, collectorBatchInputFileType, testFileIdentifier, validCollectorFileContents, new MockCollectorBatch());
        }
        catch (AuthorizationException e) {
            failedAsExpected = true;
        }

        assertTrue("authorization exception not thrown for user with invalid permissions on collector batch type", failedAsExpected);
    }

    /**
     * Assures FileStorageException is thrown when a the requested file name is already used by another file on the server.
     */
    public final void DISABLED_testSave_saveFileNameExists() throws Exception {
        String savedFileName = batchInputFileService.save(validWorkgroupUser, pcdoBatchInputFileType, testFileIdentifier, validPCDOFileContents, new ArrayList());

        File expectedFile = new File(savedFileName);
        createdTestFiles.add(expectedFile);

        // assure first attempt was completed successfully
        assertTrue("uploaded pcdo file not found", expectedFile.exists());
        long lastModifiedTime = expectedFile.lastModified();

        // no attemp to a file with the same name
        boolean failedAsExpected = false;
        try {
            batchInputFileService.save(validWorkgroupUser, pcdoBatchInputFileType, testFileIdentifier, validPCDOFileContents, new ArrayList());
        }
        catch (FileStorageException e) {
            failedAsExpected = true;
        }

        assertTrue("exception not thrown for attempt to save a file with existing name", failedAsExpected);
        assertEquals("file was modified even though storage exception was thrown", lastModifiedTime, expectedFile.lastModified());
    }

    /**
     * Checks existing file was removed by delete method.
     */
    public final void testDelete_valid() throws Exception {
        checkDelete_valid(pcdoBatchInputFileType, validPCDOFileContents, new ArrayList());
        checkDelete_valid(collectorBatchInputFileType, validCollectorFileContents, new MockCollectorBatch());
    }

    private final void checkDelete_valid(BatchInputFileType batchInputFileType, InputStream fileContents, Object parsedObject) throws Exception {
        String savedFileName = batchInputFileService.save(validWorkgroupUser, batchInputFileType, testFileIdentifier, fileContents, parsedObject);

        File expectedFile = new File(savedFileName);
        createdTestFiles.add(expectedFile);
        assertTrue("uploaded " + batchInputFileType.getFileTypeIdentifer() + " file not found", expectedFile.exists());

        String fileNameOnly = expectedFile.getName();
        batchInputFileService.delete(validWorkgroupUser, batchInputFileType, fileNameOnly);
        assertFalse("file still exists", expectedFile.exists());

        String doneFileName = StringUtils.substringBeforeLast(expectedFile.getPath(), ".") + ".done";
        File doneFile = new File(doneFileName);
        assertFalse("done file " + doneFileName + " not removed", doneFile.exists());
    }

    /**
     * Assures AuthorizationException is thrown when the user does not have permissions to delete a file with the given batch input
     * type.
     */
    public final void testDelete_incorrectUserPermissions() throws Exception {
        checkDelete_incorrectUserPermissions(pcdoBatchInputFileType, validPCDOFileContents, new ArrayList());
        checkDelete_incorrectUserPermissions(collectorBatchInputFileType, validCollectorFileContents, new MockCollectorBatch());
    }

    private final void checkDelete_incorrectUserPermissions(BatchInputFileType batchInputFileType, InputStream fileContents, Object parsedObject) throws Exception {
        String savedFileName = batchInputFileService.save(validWorkgroupUser, batchInputFileType, testFileIdentifier, fileContents, parsedObject);
        createdTestFiles.add(new File(savedFileName));

        boolean failedAsExpected = false;
        try {
            batchInputFileService.delete(invalidWorkgroupUser, batchInputFileType, testFileIdentifier);
        }
        catch (AuthorizationException e) {
            failedAsExpected = true;
        }

        assertTrue("authorization exception not thrown for user with invalid permissions on " + batchInputFileType.getFileTypeIdentifer() + " batch type", failedAsExpected);
    }

    /**
     * Assures FileNotFound exception is thrown when an invalid file name to delete is given.
     */
    public final void testDelete_fileNotFound() throws Exception {
        checkDelete_fileNotFound(pcdoBatchInputFileType, validPCDOFileContents);
        checkDelete_fileNotFound(collectorBatchInputFileType, validCollectorFileContents);
    }

    private final void checkDelete_fileNotFound(BatchInputFileType batchInputFileType, InputStream fileContents) throws Exception {
        boolean failedAsExpected = false;
        try {
            batchInputFileService.delete(validWorkgroupUser, batchInputFileType, "foo.xml");
        }
        catch (FileNotFoundException e) {
            failedAsExpected = true;
        }

        assertTrue("FileNotFound exception not thrown for user invalid file name on " + batchInputFileType.getFileTypeIdentifer() + " batch type", failedAsExpected);
    }

}
