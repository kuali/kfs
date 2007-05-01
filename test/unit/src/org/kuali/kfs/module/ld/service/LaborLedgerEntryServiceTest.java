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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.gl.web.TestDataGenerator;
import org.kuali.module.labor.bo.LedgerEntry;
import org.kuali.module.labor.util.ObjectUtil;
import org.kuali.module.labor.util.TestDataPreparator;
import org.kuali.module.labor.util.testobject.LedgerEntryForTesting;
import org.kuali.test.KualiTestBase;
import org.kuali.test.WithTestSpringContext;
import org.springframework.beans.factory.BeanFactory;

@WithTestSpringContext
public class LaborLedgerEntryServiceTest extends KualiTestBase {

    private Properties properties;
    private String fieldNames;
    private String deliminator;
    private List<String> keyFieldList;
    private Map fieldValues;

    private BeanFactory beanFactory;
    private LaborLedgerEntryService laborLedgerEntryService;
    private BusinessObjectService businessObjectService;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        String messageFileName = "test/src/org/kuali/module/labor/testdata/message.properties";
        String propertiesFileName = "test/src/org/kuali/module/labor/testdata/laborLedgerEntryService.properties";

        properties = (new TestDataGenerator(propertiesFileName, messageFileName)).getProperties();
        fieldNames = properties.getProperty("fieldNames");
        deliminator = properties.getProperty("deliminator");
        keyFieldList = Arrays.asList(StringUtils.split(fieldNames, deliminator));

        beanFactory = SpringServiceLocator.getBeanFactory();
        laborLedgerEntryService = (LaborLedgerEntryService) beanFactory.getBean("laborLedgerEntryService");
        businessObjectService = (BusinessObjectService) beanFactory.getBean("businessObjectService");
        
        LedgerEntry cleanup = new LedgerEntry();
        ObjectUtil.populateBusinessObject(cleanup, properties, "dataCleanup", fieldNames, deliminator);
        fieldValues = ObjectUtil.buildPropertyMap(cleanup, Arrays.asList(StringUtils.split(fieldNames, deliminator)));
        businessObjectService.deleteMatching(LedgerEntry.class, fieldValues);
    }

    public void testSave() throws Exception {
        String testTarget = "save.";
        LedgerEntry input1 = new LedgerEntry();
        ObjectUtil.populateBusinessObject(input1, properties, testTarget + "testData1", fieldNames, deliminator);

        LedgerEntry expected1 = new LedgerEntry();
        ObjectUtil.populateBusinessObject(expected1, properties, testTarget + "expected1", fieldNames, deliminator);
        Map fieldValues = ObjectUtil.buildPropertyMap(expected1, keyFieldList);

        businessObjectService.deleteMatching(LedgerEntry.class, fieldValues);
        assertEquals(0, businessObjectService.countMatching(LedgerEntry.class, fieldValues));

        laborLedgerEntryService.save(input1);
        assertEquals(1, businessObjectService.countMatching(LedgerEntry.class, fieldValues));

        LedgerEntry input2 = new LedgerEntry();
        ObjectUtil.populateBusinessObject(input2, properties, testTarget + "testData2", fieldNames, deliminator);
        try {
            laborLedgerEntryService.save(input2);
            fail();
        }
        catch (Exception e) {
        }
    }

    public void testGetMaxSequenceNumber() throws Exception {
        String testTarget = "maxSeqNumber.";
        LedgerEntry input1 = new LedgerEntry();
        ObjectUtil.populateBusinessObject(input1, properties, testTarget + "testData1", fieldNames, deliminator);

        Map fieldValues = ObjectUtil.buildPropertyMap(input1, keyFieldList);
        fieldValues.remove(KFSPropertyConstants.TRANSACTION_ENTRY_SEQUENCE_NUMBER);
        businessObjectService.deleteMatching(LedgerEntry.class, fieldValues);

        Integer maxSeqNumber = laborLedgerEntryService.getMaxSequenceNumber(input1);
        assertEquals(Integer.valueOf(0), maxSeqNumber);

        LedgerEntry ledgerEntryExpected1 = new LedgerEntry();
        String expectedSeqNumber1 = properties.getProperty(testTarget + "expected1");

        laborLedgerEntryService.save(input1);
        maxSeqNumber = laborLedgerEntryService.getMaxSequenceNumber(input1);
        assertEquals(Integer.valueOf(expectedSeqNumber1), maxSeqNumber);

        LedgerEntry input2 = new LedgerEntry();
        ObjectUtil.populateBusinessObject(input2, properties, testTarget + "testData2", fieldNames, deliminator);

        LedgerEntry expected2 = new LedgerEntry();
        String expectedSeqNumber2 = properties.getProperty(testTarget + "expected2");

        laborLedgerEntryService.save(input2);
        maxSeqNumber = laborLedgerEntryService.getMaxSequenceNumber(input1);
        assertEquals(Integer.valueOf(expectedSeqNumber2), maxSeqNumber);

        maxSeqNumber = laborLedgerEntryService.getMaxSequenceNumber(input2);
        assertEquals(Integer.valueOf(expectedSeqNumber2), maxSeqNumber);
    }
    
    public void testFind() throws Exception {
        String testTarget = "find.";
        int numberOfTestData = Integer.valueOf(properties.getProperty(testTarget + "numOfData"));
        int expectedNumOfData = Integer.valueOf(properties.getProperty(testTarget + "expectedNumOfData"));

        List inputDataList = TestDataPreparator.buildTestDataList(LedgerEntry.class, properties, testTarget + "testData", numberOfTestData);
        businessObjectService.save(inputDataList);

        Iterator<LedgerEntry> ledgerEntries = laborLedgerEntryService.find(fieldValues);
        int counter = 0;
        List expectedDataList = TestDataPreparator.buildExpectedValueList(LedgerEntryForTesting.class, properties, testTarget + "expected", fieldNames, deliminator, expectedNumOfData);       
        while (ledgerEntries != null && ledgerEntries.hasNext()) {
            LedgerEntry entry = ledgerEntries.next();
            LedgerEntryForTesting ledgerEntryForTesting = new LedgerEntryForTesting();
            ObjectUtil.buildObject(ledgerEntryForTesting, entry);
            assertTrue(expectedDataList.contains(ledgerEntryForTesting));
            counter++;
        }
        assertEquals(expectedNumOfData, counter);
    }    
}