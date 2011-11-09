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
package org.kuali.kfs.module.ld.batch.service;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.businessobject.LaborLedgerPendingEntry;
import org.kuali.kfs.module.ld.businessobject.LaborOriginEntry;
import org.kuali.kfs.module.ld.testdata.LaborTestDataPropertyConstants;
import org.kuali.kfs.module.ld.util.LaborOriginEntryFileIterator;
import org.kuali.kfs.module.ld.util.LaborOriginEntryForTesting;
import org.kuali.kfs.module.ld.util.PendingLedgerEntryForTesting;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.kfs.sys.TestDataPreparator;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.service.BusinessObjectService;

@ConfigureContext
public class LaborNightlyOutServiceTest extends KualiTestBase {
    private Properties properties;
    private Map<String, Object> fieldValues;

    private BusinessObjectService businessObjectService;
    private LaborNightlyOutService laborNightlyOutService;

    private File nightlyOutputFile = null;
    private File nightlyOutputDoneFile = null;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        String messageFileName = LaborTestDataPropertyConstants.TEST_DATA_PACKAGE_NAME + "/message.properties";
        String propertiesFileName = LaborTestDataPropertyConstants.TEST_DATA_PACKAGE_NAME + "/laborNightlyOutService.properties";

        properties = TestDataPreparator.loadPropertiesFromClassPath(propertiesFileName);

        String fieldNames = properties.getProperty("fieldNames");
        String documentFieldNames = properties.getProperty("documentFieldNames");
        String deliminator = properties.getProperty("deliminator");

        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        laborNightlyOutService = SpringContext.getBean(LaborNightlyOutService.class);

        int numberOfDocuments = Integer.valueOf(properties.getProperty("document.numOfData"));
        List<DocumentHeader> inputDataList = TestDataPreparator.buildTestDataList(DocumentHeader.class, properties, "document.testData", documentFieldNames, deliminator, numberOfDocuments);
        businessObjectService.save(inputDataList);

        LaborLedgerPendingEntry cleanup = new LaborLedgerPendingEntry();
        ObjectUtil.populateBusinessObject(cleanup, properties, "dataCleanup", fieldNames, deliminator);
        fieldValues = ObjectUtil.buildPropertyMap(cleanup, Arrays.asList(StringUtils.split(fieldNames, deliminator)));
        businessObjectService.deleteMatching(LaborLedgerPendingEntry.class, fieldValues);

        String stagingDirectory = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString("staging.directory");
        String batchFileDirectoryName = stagingDirectory + File.separator + "ld" + File.separator + "originEntry";
        String nightlyOutputFileName = batchFileDirectoryName + File.separator + LaborConstants.BatchFileSystem.NIGHTLY_OUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        String nightlyOutputDoneFileName = batchFileDirectoryName + File.separator + LaborConstants.BatchFileSystem.NIGHTLY_OUT_FILE + GeneralLedgerConstants.BatchFileSystem.DONE_FILE_EXTENSION;

        nightlyOutputFile = new File(nightlyOutputFileName);
        nightlyOutputDoneFile = new File(nightlyOutputDoneFileName);
    }

    public void testCopyApprovedPendingLedgerEntries() throws Exception {
        String testTarget = "copyApprovedPendingLedgerEntries.";
        int numberOfTestData = Integer.valueOf(properties.getProperty(testTarget + "numOfData"));

        List<LaborLedgerPendingEntry> inputDataList = TestDataPreparator.buildTestDataList(LaborLedgerPendingEntry.class, properties, testTarget + "testData", numberOfTestData);
        businessObjectService.save(inputDataList);

        laborNightlyOutService.copyApprovedPendingLedgerEntries();

        int expectedNumOfData = Integer.valueOf(properties.getProperty(testTarget + "expectedNumOfData"));
        List<LaborOriginEntryForTesting> expectedDataList = TestDataPreparator.buildExpectedValueList(LaborOriginEntryForTesting.class, properties, testTarget + "expected", expectedNumOfData);

        Iterator<LaborOriginEntry> originEntries = new LaborOriginEntryFileIterator(nightlyOutputFile);
        int sizeOfOriginEntries = 0;
        while (originEntries.hasNext()) {
            LaborOriginEntryForTesting originEntryForTesting = new LaborOriginEntryForTesting();
            ObjectUtil.buildObject(originEntryForTesting, originEntries.next());

            sizeOfOriginEntries++;
            assertTrue("Cannot find the expected entry", expectedDataList.contains(originEntryForTesting));
        }

        assertEquals(expectedNumOfData, sizeOfOriginEntries);
    }

    public void testCopyPendingLedgerEntries() throws Exception {
        String testTarget = "copyPendingLedgerEntries.";
        int numberOfTestData = Integer.valueOf(properties.getProperty(testTarget + "numOfData"));
        int expectedNumOfData = Integer.valueOf(properties.getProperty(testTarget + "expectedNumOfData"));

        List<LaborLedgerPendingEntry> inputDataList = TestDataPreparator.buildTestDataList(LaborLedgerPendingEntry.class, properties, testTarget + "testData", numberOfTestData);
        businessObjectService.save(inputDataList);

        laborNightlyOutService.copyApprovedPendingLedgerEntries();

        List<LaborOriginEntryForTesting> expectedDataList = TestDataPreparator.buildExpectedValueList(LaborOriginEntryForTesting.class, properties, testTarget + "expected", expectedNumOfData);

        Iterator<LaborOriginEntry> originEntries = new LaborOriginEntryFileIterator(nightlyOutputFile);
        int sizeOfOriginEntries = 0;
        while (originEntries.hasNext()) {
            LaborOriginEntryForTesting originEntryForTesting = new LaborOriginEntryForTesting();
            ObjectUtil.buildObject(originEntryForTesting, originEntries.next());

            sizeOfOriginEntries++;
            assertTrue("Cannot find the expected entry", expectedDataList.contains(originEntryForTesting));
        }

        assertEquals(expectedNumOfData, sizeOfOriginEntries);
    }

    public void testDeleteCopiedPendingLedgerEntries() throws Exception {
        String testTarget = "deleteCopiedPendingLedgerEntries.";
        int numberOfTestData = Integer.valueOf(properties.getProperty(testTarget + "numOfData"));
        int expectedNumOfData = Integer.valueOf(properties.getProperty(testTarget + "expectedNumOfData"));

        List<LaborLedgerPendingEntry> inputDataList = TestDataPreparator.buildTestDataList(LaborLedgerPendingEntry.class, properties, testTarget + "testData", numberOfTestData);
        businessObjectService.save(inputDataList);

        laborNightlyOutService.deleteCopiedPendingLedgerEntries();

        int sizeOfPendingEntries = businessObjectService.countMatching(LaborLedgerPendingEntry.class, fieldValues);

        assertEquals(expectedNumOfData, sizeOfPendingEntries);
    }

    public void testDeletePendingLedgerEntries() throws Exception {
        String testTarget = "deletePendingLedgerEntries.";
        int numberOfTestData = Integer.valueOf(properties.getProperty(testTarget + "numOfData"));
        int expectedNumOfData = Integer.valueOf(properties.getProperty(testTarget + "expectedNumOfData"));

        List<LaborLedgerPendingEntry> inputDataList = TestDataPreparator.buildTestDataList(LaborLedgerPendingEntry.class, properties, testTarget + "testData", numberOfTestData);
        businessObjectService.save(inputDataList);

        laborNightlyOutService.deleteCopiedPendingLedgerEntries();

        List<PendingLedgerEntryForTesting> expectedDataList = TestDataPreparator.buildExpectedValueList(PendingLedgerEntryForTesting.class, properties, testTarget + "expected", expectedNumOfData);
        Collection<LaborLedgerPendingEntry> pendingEntries = businessObjectService.findMatching(LaborLedgerPendingEntry.class, fieldValues);
        for (LaborLedgerPendingEntry pendingEntry : pendingEntries) {
            PendingLedgerEntryForTesting pendingEntryForTesting = new PendingLedgerEntryForTesting();
            ObjectUtil.buildObject(pendingEntryForTesting, pendingEntry);

            assertTrue("Cannot find the expected entry", expectedDataList.contains(pendingEntryForTesting));
        }

        assertEquals(expectedNumOfData, pendingEntries.size());
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        if (nightlyOutputFile != null && nightlyOutputFile.exists() && nightlyOutputFile.isFile()) {
            nightlyOutputFile.delete();
        }

        if (nightlyOutputDoneFile != null && nightlyOutputDoneFile.exists() && nightlyOutputDoneFile.isFile()) {
            nightlyOutputDoneFile.delete();
        }

        super.tearDown();
    }
}
