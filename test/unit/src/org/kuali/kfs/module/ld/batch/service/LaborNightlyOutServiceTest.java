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
import org.kuali.rice.kns.bo.DocumentHeader;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KualiConfigurationService;

@ConfigureContext
public class LaborNightlyOutServiceTest extends KualiTestBase {
    private Properties properties;
    private String fieldNames, documentFieldNames;
    private String deliminator;

    private Map fieldValues;

    private BusinessObjectService businessObjectService;
    private LaborNightlyOutService laborNightlyOutService;
    
    private String batchFileDirectoryName;
    private String nightlyOutputFileName;
    private String nightlyOutputDoneFileName;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        String messageFileName = LaborTestDataPropertyConstants.TEST_DATA_PACKAGE_NAME + "/message.properties";
        String propertiesFileName = LaborTestDataPropertyConstants.TEST_DATA_PACKAGE_NAME + "/laborNightlyOutService.properties";

        properties = TestDataPreparator.loadPropertiesFromClassPath(propertiesFileName);
        
        fieldNames = properties.getProperty("fieldNames");
        documentFieldNames = properties.getProperty("documentFieldNames");
        deliminator = properties.getProperty("deliminator");

        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        laborNightlyOutService = SpringContext.getBean(LaborNightlyOutService.class);

        int numberOfDocuments = Integer.valueOf(properties.getProperty("document.numOfData"));
        List inputDataList = TestDataPreparator.buildTestDataList(DocumentHeader.class, properties, "document.testData", documentFieldNames, deliminator, numberOfDocuments);
        businessObjectService.save(inputDataList);
        
        LaborLedgerPendingEntry cleanup = new LaborLedgerPendingEntry();
        ObjectUtil.populateBusinessObject(cleanup, properties, "dataCleanup", fieldNames, deliminator);
        fieldValues = ObjectUtil.buildPropertyMap(cleanup, Arrays.asList(StringUtils.split(fieldNames, deliminator)));
        businessObjectService.deleteMatching(LaborLedgerPendingEntry.class, fieldValues);
        
        batchFileDirectoryName = SpringContext.getBean(KualiConfigurationService.class).getPropertyString("staging.directory")+"/ld/originEntry";
        nightlyOutputFileName = batchFileDirectoryName + File.separator + LaborConstants.BatchFileSystem.NIGHTLY_OUT_FILE + GeneralLedgerConstants.BatchFileSystem.EXTENSION;
        nightlyOutputDoneFileName = batchFileDirectoryName + File.separator + LaborConstants.BatchFileSystem.NIGHTLY_OUT_FILE + GeneralLedgerConstants.BatchFileSystem.DONE_FILE_EXTENSION;
    }

    public void testCopyApprovedPendingLedgerEntries() throws Exception {
        String testTarget = "copyApprovedPendingLedgerEntries.";
        int numberOfTestData = Integer.valueOf(properties.getProperty(testTarget + "numOfData"));

        List<LaborLedgerPendingEntry> inputDataList = TestDataPreparator.buildTestDataList(LaborLedgerPendingEntry.class, properties, testTarget + "testData", numberOfTestData);
        businessObjectService.save(inputDataList);

        laborNightlyOutService.copyApprovedPendingLedgerEntries();
        
        int expectedNumOfData = Integer.valueOf(properties.getProperty(testTarget + "expectedNumOfData"));
        List<LaborOriginEntryForTesting> expectedDataList = TestDataPreparator.buildExpectedValueList(LaborOriginEntryForTesting.class, properties, testTarget + "expected", expectedNumOfData);
        
        Iterator<LaborOriginEntry> originEntries = new LaborOriginEntryFileIterator(new File(nightlyOutputFileName));
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
        
        Iterator<LaborOriginEntry> originEntries = new LaborOriginEntryFileIterator(new File(nightlyOutputFileName));
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

        List inputDataList = TestDataPreparator.buildTestDataList(LaborLedgerPendingEntry.class, properties, testTarget + "testData", numberOfTestData);
        businessObjectService.save(inputDataList);

        laborNightlyOutService.deleteCopiedPendingLedgerEntries();
        
        int sizeOfPendingEntries = businessObjectService.countMatching(LaborLedgerPendingEntry.class, fieldValues);

        assertEquals(expectedNumOfData, sizeOfPendingEntries);
    }

    public void testDeletePendingLedgerEntries() throws Exception {
        String testTarget = "deletePendingLedgerEntries.";
        int numberOfTestData = Integer.valueOf(properties.getProperty(testTarget + "numOfData"));
        int expectedNumOfData = Integer.valueOf(properties.getProperty(testTarget + "expectedNumOfData"));

        List inputDataList = TestDataPreparator.buildTestDataList(LaborLedgerPendingEntry.class, properties, testTarget + "testData", numberOfTestData);
        businessObjectService.save(inputDataList);

        laborNightlyOutService.deleteCopiedPendingLedgerEntries();

        List<PendingLedgerEntryForTesting> expectedDataList = TestDataPreparator.buildExpectedValueList(PendingLedgerEntryForTesting.class, properties, testTarget + "expected", expectedNumOfData);
        Collection pendingEntries = businessObjectService.findMatching(LaborLedgerPendingEntry.class, fieldValues);
        for (Object pendingEntry : pendingEntries) {
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
        File nightlyOutputFile = new File(nightlyOutputFileName);
        if(nightlyOutputFile.isFile() && nightlyOutputFile.exists()) {
            nightlyOutputFile.delete();
        }
        
        File nightlyOutputDoneFile = new File(nightlyOutputDoneFileName);
        if(nightlyOutputDoneFile.isFile() && nightlyOutputDoneFile.exists()) {
            nightlyOutputDoneFile.delete();
        }
        
        super.tearDown();
    }
}
