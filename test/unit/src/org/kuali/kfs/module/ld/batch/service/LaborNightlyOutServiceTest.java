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
package org.kuali.module.labor.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.PersistenceService;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.gl.web.TestDataGenerator;
import org.kuali.module.labor.bo.LaborOriginEntry;
import org.kuali.module.labor.bo.PendingLedgerEntry;
import org.kuali.module.labor.util.ObjectUtil;
import org.kuali.module.labor.util.TestDataPreparator;
import org.kuali.module.labor.util.testobject.LaborOriginEntryForTesting;
import org.kuali.module.labor.util.testobject.PendingLedgerEntryForTesting;
import org.kuali.test.KualiTestBase;
import org.kuali.test.WithTestSpringContext;
import org.springframework.beans.factory.BeanFactory;

@WithTestSpringContext
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
        String messageFileName = "test/src/org/kuali/module/labor/testdata/message.properties";
        String propertiesFileName = "test/src/org/kuali/module/labor/testdata/laborNightlyOutService.properties";

        properties = (new TestDataGenerator(propertiesFileName, messageFileName)).getProperties();
        fieldNames = properties.getProperty("fieldNames");
        documentFieldNames = properties.getProperty("documentFieldNames");
        deliminator = properties.getProperty("deliminator");

        BeanFactory beanFactory = SpringServiceLocator.getBeanFactory();
        businessObjectService = (BusinessObjectService) beanFactory.getBean("businessObjectService");
        laborNightlyOutService = (LaborNightlyOutService)beanFactory.getBean("laborNightlyOutService");
        persistenceService = (PersistenceService) beanFactory.getBean("persistenceService");
        
        int numberOfDocuments = Integer.valueOf(properties.getProperty("document.numOfData"));
        List inputDataList = TestDataPreparator.buildTestDataList(DocumentHeader.class, properties, "document.testData", documentFieldNames, deliminator, numberOfDocuments);
        businessObjectService.save(inputDataList);        

        LaborOriginEntry cleanup = new LaborOriginEntry();
        ObjectUtil.populateBusinessObject(cleanup, properties, "dataCleanup", fieldNames, deliminator);
        fieldValues = ObjectUtil.buildPropertyMap(cleanup, Arrays.asList(StringUtils.split(fieldNames, deliminator)));
        businessObjectService.deleteMatching(LaborOriginEntry.class, fieldValues);
        businessObjectService.deleteMatching(PendingLedgerEntry.class, fieldValues);
    }
    
    public void testCopyApprovedPendingLedgerEntries() throws Exception {
        String testTarget = "copyApprovedPendingLedgerEntries.";
        int numberOfTestData = Integer.valueOf(properties.getProperty(testTarget + "numOfData"));
        int expectedNumOfData = Integer.valueOf(properties.getProperty(testTarget + "expectedNumOfData"));

        List inputDataList = TestDataPreparator.buildTestDataList(PendingLedgerEntry.class, properties, testTarget + "testData", numberOfTestData);
        businessObjectService.save(inputDataList);
        
        laborNightlyOutService.copyApprovedPendingLedgerEntries();
        
        List expectedDataList = TestDataPreparator.buildExpectedValueList(LaborOriginEntryForTesting.class, properties, testTarget + "expected", expectedNumOfData);
        Collection originEntries = businessObjectService.findMatching(LaborOriginEntry.class, fieldValues);       
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

        List inputDataList = TestDataPreparator.buildTestDataList(PendingLedgerEntry.class, properties, testTarget + "testData", numberOfTestData);
        businessObjectService.save(inputDataList);
        
        laborNightlyOutService.copyApprovedPendingLedgerEntries();
       
        List expectedDataList = TestDataPreparator.buildExpectedValueList(LaborOriginEntryForTesting.class, properties, testTarget + "expected", expectedNumOfData);
        Collection originEntries = businessObjectService.findMatching(LaborOriginEntry.class, fieldValues);       
        for (Object entry : originEntries) {
            LaborOriginEntryForTesting originEntryForTesting = new LaborOriginEntryForTesting();
            ObjectUtil.buildObject(originEntryForTesting, entry);
            assertTrue("Cannot find the expected entry", expectedDataList.contains(originEntryForTesting));
        }
        assertEquals(expectedNumOfData, originEntries.size());
    }
    
    public void testDeleteCopiedPendingLedgerEntries() throws Exception {
        String testTarget = "deleteCopiedPendingLedgerEntries.";
        int numberOfTestData = Integer.valueOf(properties.getProperty(testTarget + "numOfData"));
        int expectedNumOfData = Integer.valueOf(properties.getProperty(testTarget + "expectedNumOfData"));

        List inputDataList = TestDataPreparator.buildTestDataList(PendingLedgerEntry.class, properties, testTarget + "testData", numberOfTestData);
        businessObjectService.save(inputDataList);
        
        laborNightlyOutService.deleteCopiedPendingLedgerEntries();
        
        Collection originEntries = businessObjectService.findMatching(LaborOriginEntry.class, fieldValues);       

        assertEquals(expectedNumOfData, originEntries.size());
    }
    
    public void testDeletePendingLedgerEntries() throws Exception {
        String testTarget = "deletePendingLedgerEntries.";
        int numberOfTestData = Integer.valueOf(properties.getProperty(testTarget + "numOfData"));
        int expectedNumOfData = Integer.valueOf(properties.getProperty(testTarget + "expectedNumOfData"));

        List inputDataList = TestDataPreparator.buildTestDataList(PendingLedgerEntry.class, properties, testTarget + "testData", numberOfTestData);
        businessObjectService.save(inputDataList);
        
        laborNightlyOutService.deleteCopiedPendingLedgerEntries();
       
        List expectedDataList = TestDataPreparator.buildExpectedValueList(LaborOriginEntryForTesting.class, properties, testTarget + "expected", expectedNumOfData);
        Collection originEntries = businessObjectService.findMatching(PendingLedgerEntry.class, fieldValues);       
        for (Object entry : originEntries) {
            PendingLedgerEntryForTesting pendingEntryForTesting = new PendingLedgerEntryForTesting();
            ObjectUtil.buildObject(pendingEntryForTesting, entry);
            assertTrue("Cannot find the expected entry", expectedDataList.contains(pendingEntryForTesting));
        }
        assertEquals(expectedNumOfData, originEntries.size());
    }
}
