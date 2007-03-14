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

import static org.kuali.module.gl.bo.OriginEntrySource.LABOR_SCRUBBER_VALID;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.PropertyConstants;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.PersistenceService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.gl.util.PosterOutputSummaryEntry;
import org.kuali.module.gl.web.TestDataGenerator;
import org.kuali.module.labor.bo.LaborGeneralLedgerEntry;
import org.kuali.module.labor.bo.LaborOriginEntry;
import org.kuali.module.labor.bo.LedgerBalance;
import org.kuali.module.labor.bo.LedgerEntry;
import org.kuali.module.labor.util.ObjectUtil;
import org.kuali.module.labor.util.testobject.LaborGeneralLedgerEntryForTesting;
import org.kuali.module.labor.util.testobject.LedgerBalanceForTesting;
import org.kuali.module.labor.util.testobject.LedgerEntryForTesting;
import org.kuali.module.labor.util.testobject.OriginEntryGroupForTesting;
import org.kuali.module.labor.util.testobject.PendingLedgerEntryForTesting;
import org.kuali.test.KualiTestBase;
import org.kuali.test.WithTestSpringContext;
import org.springframework.beans.factory.BeanFactory;

@WithTestSpringContext
public class LaborPosterServiceTest extends KualiTestBase {

    private Properties properties;
    private String fieldNames;
    private String deliminator;
    private OriginEntryGroup groupToPost;
    private Map fieldValues, groupFieldValues;
    private String reportsDirectory;
    private Date today;

    private LaborOriginEntryService laborOriginEntryService;
    private OriginEntryGroupService originEntryGroupService;
    private BusinessObjectService businessObjectService;
    private LaborPosterService laborPosterService;
    private PersistenceService persistenceService;

    public void setUp() throws Exception {
        super.setUp();
        String messageFileName = "test/src/org/kuali/module/labor/testdata/message.properties";
        String propertiesFileName = "test/src/org/kuali/module/labor/testdata/laborPosterService.properties";

        properties = (new TestDataGenerator(propertiesFileName, messageFileName)).getProperties();
        fieldNames = properties.getProperty("fieldNames");
        deliminator = properties.getProperty("deliminator");

        BeanFactory beanFactory = SpringServiceLocator.getBeanFactory();
        laborOriginEntryService = (LaborOriginEntryService) beanFactory.getBean("laborOriginEntryService");
        originEntryGroupService = (OriginEntryGroupService) beanFactory.getBean("glOriginEntryGroupService");
        businessObjectService = (BusinessObjectService) beanFactory.getBean("businessObjectService");
        laborPosterService = (LaborPosterService) beanFactory.getBean("laborPosterService");
        persistenceService = (PersistenceService) beanFactory.getBean("persistenceService");

        groupFieldValues = new HashMap();
        groupFieldValues.put(PropertyConstants.SOURCE_CODE, LABOR_SCRUBBER_VALID);
        originEntryGroupService.deleteOlderGroups(0);
        businessObjectService.deleteMatching(OriginEntryGroup.class, groupFieldValues);

        today = ((DateTimeService) beanFactory.getBean("dateTimeService")).getCurrentSqlDate();
        groupToPost = originEntryGroupService.createGroup(today, LABOR_SCRUBBER_VALID, true, true, false);

        LaborOriginEntry cleanup = new LaborOriginEntry();
        ObjectUtil.populateBusinessObject(cleanup, properties, "dataCleanup", fieldNames, deliminator);
        fieldValues = ObjectUtil.buildPropertyMap(cleanup, Arrays.asList(StringUtils.split(fieldNames, deliminator)));
        businessObjectService.deleteMatching(LaborOriginEntry.class, fieldValues);
        businessObjectService.deleteMatching(LedgerEntry.class, fieldValues);
        businessObjectService.deleteMatching(LaborGeneralLedgerEntry.class, fieldValues);
        businessObjectService.deleteMatching(LedgerBalance.class, fieldValues);
    }

    public void testPostAsLedgerEntry() throws Exception {
        String testTarget = "postAsLedgerEntry.";
        int numberOfTestData = Integer.valueOf(properties.getProperty(testTarget + "numOfData"));
        int expectedNumOfData = Integer.valueOf(properties.getProperty(testTarget + "expectedNumOfData"));

        List<LaborOriginEntry> inputDataList = getInputDataList(testTarget + "testData", numberOfTestData, groupToPost);
        businessObjectService.save(inputDataList);

        for (LaborOriginEntry entry : inputDataList) {
            persistenceService.retrieveNonKeyFields(entry);
        }

        laborPosterService.postMainEntries();

        Collection ledgerEntries = businessObjectService.findMatching(LedgerEntry.class, fieldValues);
        List<LedgerEntryForTesting> expectedDataList = getExpectedValues(LedgerEntryForTesting.class, testTarget + "expected", fieldNames, expectedNumOfData);        
        for (Object entry : ledgerEntries) {
            LedgerEntryForTesting ledgerEntryForTesting = new LedgerEntryForTesting();
            ObjectUtil.buildObject(ledgerEntryForTesting, entry);
            assertTrue(expectedDataList.contains(ledgerEntryForTesting));
        }
        assertEquals(expectedNumOfData, ledgerEntries.size());
    }

    public void testPostAsLedgerBalance() throws Exception {
        String testTarget = "postAsLedgerBalance.";
        int numberOfTestData = Integer.valueOf(properties.getProperty(testTarget + "numOfData"));
        int expectedNumOfData = Integer.valueOf(properties.getProperty(testTarget + "expectedNumOfData"));
        KualiDecimal expectedMonth7Amount = new KualiDecimal(properties.getProperty(testTarget + "expectedMonth7Amount"));
        KualiDecimal expectedMonth8Amount = new KualiDecimal(properties.getProperty(testTarget + "expectedMonth8Amount"));
        KualiDecimal expectedAnnualBalanceAmount = new KualiDecimal(properties.getProperty(testTarget + "expectedAnnualBalanceAmount"));

        List<LaborOriginEntry> inputDataList = getInputDataList(testTarget + "testData", numberOfTestData, groupToPost);
        businessObjectService.save(inputDataList);

        for (LaborOriginEntry entry : inputDataList) {
            persistenceService.retrieveNonKeyFields(entry);
        }

        laborPosterService.postMainEntries();

        Collection ledgerEntries = businessObjectService.findMatching(LedgerBalance.class, fieldValues);
        List<LedgerBalanceForTesting> expectedDataList = getExpectedValues(LedgerBalanceForTesting.class, testTarget + "expected", fieldNames, expectedNumOfData);        
        for (Object entry : ledgerEntries) {
            LedgerBalanceForTesting ledgerBalanceForTesting = new LedgerBalanceForTesting();
            ObjectUtil.buildObject(ledgerBalanceForTesting, entry);

            assertTrue(expectedDataList.contains(ledgerBalanceForTesting));
            assertEquals(expectedMonth7Amount, ledgerBalanceForTesting.getMonth7AccountLineAmount());
            assertEquals(expectedMonth8Amount, ledgerBalanceForTesting.getMonth8AccountLineAmount());
            assertEquals(expectedAnnualBalanceAmount, ledgerBalanceForTesting.getAccountLineAnnualBalanceAmount());
        }
        assertEquals(expectedNumOfData, ledgerEntries.size());
    }

    public void testPostLaborGLEntries() throws Exception {
        String testTarget = "postLaborGLEntries.";
        int numberOfTestData = Integer.valueOf(properties.getProperty(testTarget + "numOfData"));
        int expectedNumOfData = Integer.valueOf(properties.getProperty(testTarget + "expectedNumOfData"));

        List<LaborOriginEntry> inputDataList = getInputDataList(testTarget + "testData", numberOfTestData, groupToPost);
        businessObjectService.save(inputDataList);

        for (LaborOriginEntry entry : inputDataList) {
            persistenceService.retrieveNonKeyFields(entry);
        }

        laborPosterService.postMainEntries();

        Collection GLEntry = businessObjectService.findMatching(LaborGeneralLedgerEntry.class, fieldValues);
        List<LaborGeneralLedgerEntryForTesting> expectedDataList = getExpectedValues(LaborGeneralLedgerEntryForTesting.class, testTarget + "expected", fieldNames, expectedNumOfData);        
        for (Object entry : GLEntry) {
            LaborGeneralLedgerEntryForTesting GLEntryForTesting = new LaborGeneralLedgerEntryForTesting();
            ObjectUtil.buildObject(GLEntryForTesting, entry);

            assertTrue(expectedDataList.contains(GLEntryForTesting));
        }
        assertEquals(expectedNumOfData, GLEntry.size());
    }
    
    public void testUpdateOriginEntryGroup() throws Exception {
        String testTarget = "updateOriginEntryGroup.";
        int numberOfTestData = Integer.valueOf(properties.getProperty(testTarget + "numOfData"));
        int expectedNumOfData = Integer.valueOf(properties.getProperty(testTarget + "expectedNumOfData"));
        String groupFieldNames = properties.getProperty(testTarget + "fieldNames");

        List<LaborOriginEntry> inputDataList = getInputDataList(testTarget + "testData", numberOfTestData, groupToPost);
        businessObjectService.save(inputDataList);

        for (LaborOriginEntry entry : inputDataList) {
            persistenceService.retrieveNonKeyFields(entry);
        }

        laborPosterService.postMainEntries();

        Collection originEntryGroups = businessObjectService.findAll(OriginEntryGroup.class);
        List<OriginEntryGroupForTesting> expectedDataList = getExpectedValues(OriginEntryGroupForTesting.class, testTarget + "expected", groupFieldNames, expectedNumOfData);        
        for (Object group : originEntryGroups) {
            OriginEntryGroupForTesting originEntryGroupForTesting = new OriginEntryGroupForTesting();
            ObjectUtil.buildObject(originEntryGroupForTesting, group);

            assertTrue(expectedDataList.contains(originEntryGroupForTesting));
        }
        assertEquals(expectedNumOfData, originEntryGroups.size());
    }
    
    public void testNotPostableEntries() throws Exception {
        String testTarget = "notPostableEntries.";
        int numberOfTestData = Integer.valueOf(properties.getProperty(testTarget + "numOfData"));
        int expectedNumOfGLEntry = Integer.valueOf(properties.getProperty(testTarget + "expectedNumOfGLEntry"));
        int expectedNumOfLedgerEntry = Integer.valueOf(properties.getProperty(testTarget + "expectedNumOfLedgerEntry"));
        int expectedNumOfLedgerBalance = Integer.valueOf(properties.getProperty(testTarget + "expectedNumOfLedgerBalance"));
        int expectedNumOfOriginEntry = Integer.valueOf(properties.getProperty(testTarget + "expectedNumOfOriginEntry"));

        List<LaborOriginEntry> inputDataList = getInputDataList(testTarget + "testData", numberOfTestData, groupToPost);
        businessObjectService.save(inputDataList);

        for (LaborOriginEntry entry : inputDataList) {
            persistenceService.retrieveNonKeyFields(entry);
        }

        laborPosterService.postMainEntries();
        
        Collection originEntries = businessObjectService.findMatching(LaborOriginEntry.class, fieldValues);
        assertEquals(expectedNumOfOriginEntry, originEntries.size());
               
        Collection ledgerEntries = businessObjectService.findMatching(LedgerEntry.class, fieldValues);
        assertEquals(expectedNumOfLedgerEntry, ledgerEntries.size());

        Collection ledgerBalances = businessObjectService.findMatching(LedgerBalance.class, fieldValues);
        assertEquals(expectedNumOfLedgerBalance, ledgerBalances.size());
        
        Collection GLEntries = businessObjectService.findMatching(LaborGeneralLedgerEntry.class, fieldValues);
        assertEquals(expectedNumOfGLEntry, GLEntries.size());
    }
    
    public void testNotPostableEntriesToLaborGL() throws Exception {
        String testTarget = "notPostableEntriesToLaborGL.";
        int numberOfTestData = Integer.valueOf(properties.getProperty(testTarget + "numOfData"));
        int expectedNumOfGLEntry = Integer.valueOf(properties.getProperty(testTarget + "expectedNumOfGLEntry"));
        int expectedNumOfLedgerEntry = Integer.valueOf(properties.getProperty(testTarget + "expectedNumOfLedgerEntry"));
        int expectedNumOfOriginEntry = Integer.valueOf(properties.getProperty(testTarget + "expectedNumOfOriginEntry"));

        List<LaborOriginEntry> inputDataList = getInputDataList(testTarget + "testData", numberOfTestData, groupToPost);
        businessObjectService.save(inputDataList);

        for (LaborOriginEntry entry : inputDataList) {
            persistenceService.retrieveNonKeyFields(entry);
        }

        laborPosterService.postMainEntries();
        
        Collection originEntries = businessObjectService.findMatching(LaborOriginEntry.class, fieldValues);
        assertEquals(expectedNumOfOriginEntry, originEntries.size());
               
        Collection ledgerEntries = businessObjectService.findMatching(LedgerEntry.class, fieldValues);
        assertEquals(expectedNumOfLedgerEntry, ledgerEntries.size());

        Collection<LaborGeneralLedgerEntry> GLEntries = businessObjectService.findMatching(LaborGeneralLedgerEntry.class, fieldValues);
        assertEquals(expectedNumOfGLEntry, GLEntries.size());
    }
    
    public void testPosterPerformance() throws Exception{
        String testTarget = "posterPerformance.";
        int numberOfTestData = Integer.valueOf(properties.getProperty(testTarget + "numOfData"));
        int numOfCopy = Integer.valueOf(properties.getProperty(testTarget + "numOfCopy"));
        long expectedMaxExcutionTime = Long.valueOf(properties.getProperty(testTarget + "expectedMaxExcutionTime"));

        long startTime = System.currentTimeMillis();
        List<LaborOriginEntry> inputDataList = new ArrayList<LaborOriginEntry>();
        for(int i=0; i<numOfCopy; i++){
            inputDataList.addAll(getInputDataList(testTarget + "testData", numberOfTestData, groupToPost));
        }
        businessObjectService.save(inputDataList);

        for (LaborOriginEntry entry : inputDataList) {
            persistenceService.retrieveNonKeyFields(entry);
        }
        long elapsedTime = System.currentTimeMillis() - startTime;        
        
        startTime = System.currentTimeMillis();
        laborPosterService.postMainEntries();      
        elapsedTime = System.currentTimeMillis() - startTime;

        assertTrue("It takes too much time to run poster against test data", elapsedTime <= expectedMaxExcutionTime);
    }

    private List<LaborOriginEntry> getInputDataList(String propertyKeyPrefix, int numberOfInputData, OriginEntryGroup group) {
        List<LaborOriginEntry> inputDataList = new ArrayList<LaborOriginEntry>();
        for (int i = 1; i <= numberOfInputData; i++) {
            String propertyKey = propertyKeyPrefix + i;
            LaborOriginEntry inputData = new LaborOriginEntry();
            ObjectUtil.populateBusinessObject(inputData, properties, propertyKey, fieldNames, deliminator);
            inputData.setEntryGroupId(group.getId());
            inputData.setGroup(group);
            inputDataList.add(inputData);
        }
        return inputDataList;
    }

    private List getExpectedValues(Class clazz, String propertyKeyPrefix, String fieldNames, int numberOfInputData) {
        List expectedDataList = new ArrayList();
        for (int i = 1; i <= numberOfInputData; i++) {
            String propertyKey = propertyKeyPrefix + i;
            try {
                Object expectedData = clazz.newInstance();
                ObjectUtil.populateBusinessObject(expectedData, properties, propertyKey, fieldNames, deliminator);

                if (!expectedDataList.contains(expectedData)) {
                    expectedDataList.add(expectedData);
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return expectedDataList;
    }
}
