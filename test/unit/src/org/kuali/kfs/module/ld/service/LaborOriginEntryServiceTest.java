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

import static org.kuali.module.gl.bo.OriginEntrySource.MAIN_POSTER_VALID;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.PropertyConstants;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.gl.web.TestDataGenerator;
import org.kuali.module.labor.bo.LaborOriginEntry;
import org.kuali.module.labor.bo.LedgerEntry;
import org.kuali.module.labor.util.ObjectUtil;
import org.kuali.test.KualiTestBase;
import org.kuali.test.WithTestSpringContext;
import org.springframework.beans.factory.BeanFactory;

@WithTestSpringContext
public class LaborOriginEntryServiceTest extends KualiTestBase {

    private Properties properties;
    private String fieldNames;
    private String deliminator;
    private List<String> keyFieldList;
    private Date today;

    private BeanFactory beanFactory;
    private LaborOriginEntryService laborOriginEntryService;
    private OriginEntryGroupService originEntryGroupService;
    private BusinessObjectService businessObjectService;

    public void setUp() throws Exception {
        super.setUp();
        String messageFileName = "test/src/org/kuali/module/labor/testdata/message.properties";
        String propertiesFileName = "test/src/org/kuali/module/labor/testdata/laborOriginEntryService.properties";

        properties = (new TestDataGenerator(propertiesFileName, messageFileName)).getProperties();
        fieldNames = properties.getProperty("fieldNames");
        deliminator = properties.getProperty("deliminator");
        keyFieldList = Arrays.asList(StringUtils.split(fieldNames, deliminator));

        beanFactory = SpringServiceLocator.getBeanFactory();
        laborOriginEntryService = (LaborOriginEntryService) beanFactory.getBean("laborOriginEntryService");
        originEntryGroupService = (OriginEntryGroupService) beanFactory.getBean("glOriginEntryGroupService");
        businessObjectService = (BusinessObjectService) beanFactory.getBean("businessObjectService");
        today = ((DateTimeService) beanFactory.getBean("dateTimeService")).getCurrentSqlDate();
    }

    public void testGetEntriesByGroup() throws Exception {
        int numberOfTestData = Integer.valueOf(properties.getProperty("getEntriesByGroup.numOfData"));
        
        OriginEntryGroup group1 = originEntryGroupService.createGroup(today, MAIN_POSTER_VALID, false, false, false);
        Integer entryGroupId1 = group1.getId();

        OriginEntryGroup group2 = originEntryGroupService.createGroup(today, MAIN_POSTER_VALID, false, false, false);
        Integer entryGroupId2 = group2.getId();
        
        LaborOriginEntry expected1 = new LaborOriginEntry();
        ObjectUtil.populateBusinessObject(expected1, properties, "getEntriesByGroup.expected1", fieldNames, deliminator);
        Map fieldValues = ObjectUtil.buildPropertyMap(expected1, keyFieldList);
        fieldValues.remove(PropertyConstants.TRANSACTION_ENTRY_SEQUENCE_NUMBER);
        businessObjectService.deleteMatching(LaborOriginEntry.class, fieldValues);
        assertEquals(businessObjectService.countMatching(LedgerEntry.class, fieldValues), 0);
          
        businessObjectService.save(getInputDataList("getEntriesByGroup.testData", numberOfTestData, entryGroupId1));
        List<LaborOriginEntry> entries = convertIteratorAsList(laborOriginEntryService.getEntriesByGroup(group1));
        assertEquals(numberOfTestData, entries.size());
        
        businessObjectService.save(getInputDataList("getEntriesByGroup.testData", numberOfTestData-1, entryGroupId2));
        entries = convertIteratorAsList(laborOriginEntryService.getEntriesByGroup(group1));
        assertEquals(numberOfTestData, entries.size());
    }
    
    public void testGetEntriesByGroups() throws Exception {
        int numberOfTestData = Integer.valueOf(properties.getProperty("getEntriesByGroups.numOfData"));
        
        OriginEntryGroup group1 = originEntryGroupService.createGroup(today, MAIN_POSTER_VALID, false, false, false);
        Integer groupId1 = group1.getId();

        OriginEntryGroup group2 = originEntryGroupService.createGroup(today, MAIN_POSTER_VALID, false, false, false);
        Integer groupId2 = group2.getId();
        
        LaborOriginEntry expected1 = new LaborOriginEntry();
        ObjectUtil.populateBusinessObject(expected1, properties, "getEntriesByGroups.expected1", fieldNames, deliminator);
        Map fieldValues = ObjectUtil.buildPropertyMap(expected1, keyFieldList);
        fieldValues.remove(PropertyConstants.TRANSACTION_ENTRY_SEQUENCE_NUMBER);
        businessObjectService.deleteMatching(LaborOriginEntry.class, fieldValues);
        assertEquals(businessObjectService.countMatching(LedgerEntry.class, fieldValues), 0);
        
        List<OriginEntryGroup> groups = new ArrayList<OriginEntryGroup>();
        groups.add(group1);          
        businessObjectService.save(getInputDataList("getEntriesByGroups.testData", numberOfTestData, groupId1));
        List<LaborOriginEntry> entries = convertIteratorAsList(laborOriginEntryService.getEntriesByGroups(groups));
        assertEquals(numberOfTestData, entries.size());
        
        groups.add(group2);
        businessObjectService.save(getInputDataList("getEntriesByGroups.testData", numberOfTestData-1, groupId2));
        entries = convertIteratorAsList(laborOriginEntryService.getEntriesByGroups(groups));
        assertEquals(numberOfTestData + numberOfTestData - 1, entries.size());
    }
    
    public void testGetConsolidatedEntriesByGroup() throws Exception {
        int numberOfTestData = Integer.valueOf(properties.getProperty("getConsolidatedEntriesByGroup.numOfData"));
        int expectedNumber = Integer.valueOf(properties.getProperty("getConsolidatedEntriesByGroup.expectedNumOfData"));
        KualiDecimal expectedTotal = new KualiDecimal(properties.getProperty("getConsolidatedEntriesByGroup.expectedTotal"));
        
        OriginEntryGroup group1 = originEntryGroupService.createGroup(today, MAIN_POSTER_VALID, false, false, false);
        Integer groupId1 = group1.getId();

        OriginEntryGroup group2 = originEntryGroupService.createGroup(today, MAIN_POSTER_VALID, false, false, false);
        Integer groupId2 = group2.getId();
        
        LaborOriginEntry expected1 = new LaborOriginEntry();
        ObjectUtil.populateBusinessObject(expected1, properties, "getConsolidatedEntriesByGroup.expected1", fieldNames, deliminator);
        Map fieldValues = ObjectUtil.buildPropertyMap(expected1, keyFieldList);
        fieldValues.remove(PropertyConstants.TRANSACTION_ENTRY_SEQUENCE_NUMBER);
        businessObjectService.deleteMatching(LaborOriginEntry.class, fieldValues);
        assertEquals(businessObjectService.countMatching(LedgerEntry.class, fieldValues), 0);
                  
        businessObjectService.save(getInputDataList("getConsolidatedEntriesByGroup.testData", numberOfTestData, groupId1));
        List<LaborOriginEntry> entries = convertIteratorAsList(laborOriginEntryService.getEntriesByGroup(group1, true));
        assertEquals(expectedNumber, entries.size());
        assertEquals(expectedTotal, entries.get(0).getTransactionLedgerEntryAmount());
        
        businessObjectService.save(getInputDataList("getConsolidatedEntriesByGroup.testData", numberOfTestData, groupId2));
        entries = convertIteratorAsList(laborOriginEntryService.getEntriesByGroup(group1, true));
        assertEquals(expectedNumber, entries.size());
        assertEquals(expectedTotal, entries.get(0).getTransactionLedgerEntryAmount());
    }
    
    private List getInputDataList(String propertyKeyPrefix, int numberOfInputData, Integer groupId){
        List inputDataList = new ArrayList();
        for (int i = 1; i <= numberOfInputData; i++) {
            String propertyKey = propertyKeyPrefix + i;
            LaborOriginEntry inputData = new LaborOriginEntry();
            ObjectUtil.populateBusinessObject(inputData, properties, propertyKey, fieldNames, deliminator);
            inputData.setEntryGroupId(groupId);
            inputDataList.add(inputData);
        }
        return inputDataList;
    }
    
    private List<LaborOriginEntry> convertIteratorAsList(Iterator<LaborOriginEntry> entries){
        List<LaborOriginEntry> entryList = new ArrayList<LaborOriginEntry>();
        while (entries.hasNext()) {
            entryList.add(entries.next());
        } 
        return entryList;
    }
}
