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

import static org.kuali.module.gl.bo.OriginEntrySource.LABOR_MAIN_POSTER_VALID;

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
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.gl.util.LedgerEntryHolder;
import org.kuali.module.gl.util.PosterOutputSummaryEntry;
import org.kuali.module.gl.web.TestDataGenerator;
import org.kuali.module.labor.bo.LaborOriginEntry;
import org.kuali.module.labor.util.ObjectUtil;
import org.kuali.test.KualiTestBase;
import org.kuali.test.WithTestSpringContext;
import org.springframework.beans.factory.BeanFactory;

@WithTestSpringContext
public class LaborOriginEntryServiceTest extends KualiTestBase {

    private Properties properties;
    private String fieldNames;
    private String deliminator;
    private OriginEntryGroup group1, group2;
    private Map fieldValues;

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

        BeanFactory beanFactory = SpringServiceLocator.getBeanFactory();
        laborOriginEntryService = (LaborOriginEntryService) beanFactory.getBean("laborOriginEntryService");
        originEntryGroupService = (OriginEntryGroupService) beanFactory.getBean("glOriginEntryGroupService");
        businessObjectService = (BusinessObjectService) beanFactory.getBean("businessObjectService");

        Date today = ((DateTimeService) beanFactory.getBean("dateTimeService")).getCurrentSqlDate();
        group1 = originEntryGroupService.createGroup(today, LABOR_MAIN_POSTER_VALID, false, false, false);
        group2 = originEntryGroupService.createGroup(today, LABOR_MAIN_POSTER_VALID, false, false, false);

        LaborOriginEntry cleanup = new LaborOriginEntry();
        ObjectUtil.populateBusinessObject(cleanup, properties, "dataCleanup", fieldNames, deliminator);
        fieldValues = ObjectUtil.buildPropertyMap(cleanup, Arrays.asList(StringUtils.split(fieldNames, deliminator)));
        fieldValues.remove(PropertyConstants.TRANSACTION_ENTRY_SEQUENCE_NUMBER);
        fieldValues.remove(PropertyConstants.UNIVERSITY_FISCAL_YEAR);
        fieldValues.remove(PropertyConstants.TRANSACTION_DEBIT_CREDIT_CODE);
        businessObjectService.deleteMatching(LaborOriginEntry.class, fieldValues);
    }

    public void testGetEntriesByGroup() throws Exception {
        int numberOfTestData = Integer.valueOf(properties.getProperty("getEntriesByGroup.numOfData"));
        int expectedNumber = Integer.valueOf(properties.getProperty("getEntriesByGroup.expectedNumOfData"));

        businessObjectService.save(getInputDataList("getEntriesByGroup.testData", numberOfTestData, group1));
        List<LaborOriginEntry> entries = convertIteratorAsList(laborOriginEntryService.getEntriesByGroup(group1));
        assertEquals(expectedNumber, entries.size());

        businessObjectService.save(getInputDataList("getEntriesByGroup.testData", numberOfTestData - 1, group2));
        entries = convertIteratorAsList(laborOriginEntryService.getEntriesByGroup(group1));
        assertEquals(expectedNumber, entries.size());
    }

    public void testGetEntriesByGroups() throws Exception {
        int numberOfTestData = Integer.valueOf(properties.getProperty("getEntriesByGroups.numOfData"));
        int expectedNumber = Integer.valueOf(properties.getProperty("getEntriesByGroups.expectedNumOfData"));

        List<OriginEntryGroup> groups = new ArrayList<OriginEntryGroup>();
        groups.add(group1);
        businessObjectService.save(getInputDataList("getEntriesByGroups.testData", numberOfTestData, group1));
        List<LaborOriginEntry> entries = convertIteratorAsList(laborOriginEntryService.getEntriesByGroups(groups));
        assertEquals(expectedNumber, entries.size());

        groups.add(group2);
        businessObjectService.save(getInputDataList("getEntriesByGroups.testData", numberOfTestData - 1, group2));
        entries = convertIteratorAsList(laborOriginEntryService.getEntriesByGroups(groups));
        assertEquals(expectedNumber + expectedNumber - 1, entries.size());
    }

    public void testGetConsolidatedEntriesByGroup() throws Exception {
        int numberOfTestData = Integer.valueOf(properties.getProperty("getConsolidatedEntriesByGroup.numOfData"));
        int expectedNumber = Integer.valueOf(properties.getProperty("getConsolidatedEntriesByGroup.expectedNumOfData"));
        KualiDecimal expectedTotal1 = new KualiDecimal(properties.getProperty("getConsolidatedEntriesByGroup.expectedTotal1"));
        KualiDecimal expectedTotal2 = new KualiDecimal(properties.getProperty("getConsolidatedEntriesByGroup.expectedTotal2"));

        businessObjectService.save(getInputDataList("getConsolidatedEntriesByGroup.testData", numberOfTestData, group1));
        List<LaborOriginEntry> entries = convertIteratorAsList(laborOriginEntryService.getEntriesByGroup(group1, true));
        assertEquals(expectedNumber, entries.size());
        assertEquals(expectedTotal1, entries.get(0).getTransactionLedgerEntryAmount());
        assertEquals(expectedTotal2, entries.get(1).getTransactionLedgerEntryAmount());

        businessObjectService.save(getInputDataList("getConsolidatedEntriesByGroup.testData", numberOfTestData, group2));
        entries = convertIteratorAsList(laborOriginEntryService.getEntriesByGroup(group1, true));
        assertEquals(expectedNumber, entries.size());
        assertEquals(expectedTotal1, entries.get(0).getTransactionLedgerEntryAmount());
        assertEquals(expectedTotal2, entries.get(1).getTransactionLedgerEntryAmount());
    }

    public void testGetSummariedEntriesByGroups() throws Exception {
        int numberOfTestData = Integer.valueOf(properties.getProperty("getSummariedEntriesByGroups.numOfData"));
        int expectedNumber = Integer.valueOf(properties.getProperty("getSummariedEntriesByGroups.expectedNumOfData"));

        List<OriginEntryGroup> groups = new ArrayList<OriginEntryGroup>();
        LedgerEntryHolder ledgerEntryHolder = laborOriginEntryService.getSummariedEntriesByGroups(groups);
        assertTrue(ledgerEntryHolder.getLedgerEntries().isEmpty());

        groups.add(group1);
        businessObjectService.save(getInputDataList("getSummariedEntriesByGroups.testData", numberOfTestData, group1));
        ledgerEntryHolder = laborOriginEntryService.getSummariedEntriesByGroups(groups);
        assertEquals(expectedNumber, ledgerEntryHolder.getLedgerEntries().size());

        groups.add(group2);
        businessObjectService.save(getInputDataList("getSummariedEntriesByGroups.testData", numberOfTestData, group2));
        ledgerEntryHolder = laborOriginEntryService.getSummariedEntriesByGroups(groups);
        assertEquals(expectedNumber, ledgerEntryHolder.getLedgerEntries().size());
    }

    public void testGetPosterOutputSummaryByGroups() throws Exception {
        int numberOfTestData = Integer.valueOf(properties.getProperty("getPosterOutputSummaryByGroups.numOfData"));
        int expectedNumber = Integer.valueOf(properties.getProperty("getPosterOutputSummaryByGroups.expectedNumOfData"));

        List<OriginEntryGroup> groups = new ArrayList<OriginEntryGroup>();
        Map<String, PosterOutputSummaryEntry> outputSummary = laborOriginEntryService.getPosterOutputSummaryByGroups(groups);
        assertTrue(outputSummary.isEmpty());

        groups.add(group1);
        businessObjectService.save(getInputDataList("getPosterOutputSummaryByGroups.testData", numberOfTestData, group1));
        outputSummary = laborOriginEntryService.getPosterOutputSummaryByGroups(groups);
        assertEquals(expectedNumber, outputSummary.size());

        groups.add(group2);
        businessObjectService.save(getInputDataList("getPosterOutputSummaryByGroups.testData", numberOfTestData, group2));
        outputSummary = laborOriginEntryService.getPosterOutputSummaryByGroups(groups);
        assertEquals(expectedNumber, outputSummary.size());
    }
    
    public void testGetCountOfEntriesInGroups() throws Exception {
        int numberOfTestData = Integer.valueOf(properties.getProperty("getCountOfEntriesInGroups.numOfData"));
        int expectedNumber = Integer.valueOf(properties.getProperty("getCountOfEntriesInGroups.expectedNumOfData"));

        List<OriginEntryGroup> groups = new ArrayList<OriginEntryGroup>();
        int count = laborOriginEntryService.getCountOfEntriesInGroups(groups);
        assertEquals(0, count);

        groups.add(group1);
        businessObjectService.save(getInputDataList("getCountOfEntriesInGroups.testData", numberOfTestData, group1));
        count = laborOriginEntryService.getCountOfEntriesInGroups(groups);
        assertEquals(expectedNumber, count);

        groups.add(group2);
        businessObjectService.save(getInputDataList("getCountOfEntriesInGroups.testData", numberOfTestData, group2));
        count = laborOriginEntryService.getCountOfEntriesInGroups(groups);
        assertEquals(expectedNumber * 2, count);
    }

    private List getInputDataList(String propertyKeyPrefix, int numberOfInputData, OriginEntryGroup group) {
        List inputDataList = new ArrayList();
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

    private List<LaborOriginEntry> convertIteratorAsList(Iterator<LaborOriginEntry> entries) {
        List<LaborOriginEntry> entryList = new ArrayList<LaborOriginEntry>();
        while (entries.hasNext()) {
            entryList.add(entries.next());
        }
        return entryList;
    }
}
