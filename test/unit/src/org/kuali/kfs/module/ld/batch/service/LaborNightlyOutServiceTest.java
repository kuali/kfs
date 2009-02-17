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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ld.businessobject.LaborLedgerPendingEntry;
import org.kuali.kfs.module.ld.businessobject.LaborOriginEntry;
import org.kuali.kfs.module.ld.testdata.LaborTestDataPropertyConstants;
import org.kuali.kfs.module.ld.util.LaborOriginEntryForTesting;
import org.kuali.kfs.module.ld.util.PendingLedgerEntryForTesting;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.kfs.sys.TestDataPreparator;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.bo.DocumentHeader;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.PersistenceService;

@ConfigureContext
public class LaborNightlyOutServiceTest extends KualiTestBase {
    private Properties properties;
    private String fieldNames, documentFieldNames;
    private String deliminator;

    private Map fieldValues;

    private BusinessObjectService businessObjectService;
    private LaborNightlyOutService laborNightlyOutService;
    private PersistenceService persistenceService;

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
        persistenceService = SpringContext.getBean(PersistenceService.class);

        int numberOfDocuments = Integer.valueOf(properties.getProperty("document.numOfData"));
        List inputDataList = TestDataPreparator.buildTestDataList(DocumentHeader.class, properties, "document.testData", documentFieldNames, deliminator, numberOfDocuments);
        businessObjectService.save(inputDataList);

        LaborOriginEntry cleanup = new LaborOriginEntry();
        ObjectUtil.populateBusinessObject(cleanup, properties, "dataCleanup", fieldNames, deliminator);
        fieldValues = ObjectUtil.buildPropertyMap(cleanup, Arrays.asList(StringUtils.split(fieldNames, deliminator)));
        businessObjectService.deleteMatching(LaborOriginEntry.class, fieldValues);
        businessObjectService.deleteMatching(LaborLedgerPendingEntry.class, fieldValues);
    }

    public void testCopyApprovedPendingLedgerEntries() throws Exception {
        String testTarget = "copyApprovedPendingLedgerEntries.";
        int numberOfTestData = Integer.valueOf(properties.getProperty(testTarget + "numOfData"));
        int expectedNumOfData = Integer.valueOf(properties.getProperty(testTarget + "expectedNumOfData"));

        List inputDataList = TestDataPreparator.buildTestDataList(LaborLedgerPendingEntry.class, properties, testTarget + "testData", numberOfTestData);
        businessObjectService.save(inputDataList);

        laborNightlyOutService.copyApprovedPendingLedgerEntries();

        List expectedDataList = TestDataPreparator.buildExpectedValueList(LaborOriginEntryForTesting.class, properties, testTarget + "expected", expectedNumOfData);
        Collection originEntries = businessObjectService.findMatching(LaborLedgerPendingEntry.class, fieldValues);
        for (Object entry : originEntries) {
            LaborOriginEntryForTesting originEntryForTesting = new LaborOriginEntryForTesting();
            ObjectUtil.buildObject(originEntryForTesting, entry);
            assertTrue("Cannot find the expected entry", expectedDataList.contains(originEntryForTesting));
        }
        assertEquals(expectedNumOfData, originEntries.size());
    }

    public void testCopyPendingLedgerEntries() throws Exception {
        String testTarget = "copyPendingLedgerEntries.";
        int numberOfTestData = Integer.valueOf(properties.getProperty(testTarget + "numOfData"));
        int expectedNumOfData = Integer.valueOf(properties.getProperty(testTarget + "expectedNumOfData"));

        List inputDataList = TestDataPreparator.buildTestDataList(LaborLedgerPendingEntry.class, properties, testTarget + "testData", numberOfTestData);
        businessObjectService.save(inputDataList);

        laborNightlyOutService.copyApprovedPendingLedgerEntries();

        List expectedDataList = TestDataPreparator.buildExpectedValueList(LaborOriginEntryForTesting.class, properties, testTarget + "expected", expectedNumOfData);
        Collection originEntries = businessObjectService.findMatching(LaborOriginEntry.class, fieldValues);
        for (Object entry : originEntries) {
            LaborOriginEntryForTesting originEntryForTesting = new LaborOriginEntryForTesting();
            ObjectUtil.buildObject(originEntryForTesting, entry);
            assertTrue("Cannot find the expected entry", expectedDataList.contains(originEntryForTesting));
        }
        
        //TODO: Shawn - need to change it using file
        //assertEquals(expectedNumOfData, originEntries.size());
    }

    public void testDeleteCopiedPendingLedgerEntries() throws Exception {
        String testTarget = "deleteCopiedPendingLedgerEntries.";
        int numberOfTestData = Integer.valueOf(properties.getProperty(testTarget + "numOfData"));
        int expectedNumOfData = Integer.valueOf(properties.getProperty(testTarget + "expectedNumOfData"));

        List inputDataList = TestDataPreparator.buildTestDataList(LaborLedgerPendingEntry.class, properties, testTarget + "testData", numberOfTestData);
        businessObjectService.save(inputDataList);

        laborNightlyOutService.deleteCopiedPendingLedgerEntries();

        Collection originEntries = businessObjectService.findMatching(LaborOriginEntry.class, fieldValues);

        assertEquals(expectedNumOfData, originEntries.size());
    }

    public void testDeletePendingLedgerEntries() throws Exception {
        String testTarget = "deletePendingLedgerEntries.";
        int numberOfTestData = Integer.valueOf(properties.getProperty(testTarget + "numOfData"));
        int expectedNumOfData = Integer.valueOf(properties.getProperty(testTarget + "expectedNumOfData"));

        List inputDataList = TestDataPreparator.buildTestDataList(LaborLedgerPendingEntry.class, properties, testTarget + "testData", numberOfTestData);
        businessObjectService.save(inputDataList);

        laborNightlyOutService.deleteCopiedPendingLedgerEntries();

        List expectedDataList = TestDataPreparator.buildExpectedValueList(LaborOriginEntryForTesting.class, properties, testTarget + "expected", expectedNumOfData);
        Collection originEntries = businessObjectService.findMatching(LaborLedgerPendingEntry.class, fieldValues);
        for (Object entry : originEntries) {
            PendingLedgerEntryForTesting pendingEntryForTesting = new PendingLedgerEntryForTesting();
            ObjectUtil.buildObject(pendingEntryForTesting, entry);
            assertTrue("Cannot find the expected entry", expectedDataList.contains(pendingEntryForTesting));
        }
        assertEquals(expectedNumOfData, originEntries.size());
    }
}
