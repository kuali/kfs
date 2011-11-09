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
package org.kuali.kfs.module.ld.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.kuali.kfs.gl.businessobject.OriginEntryGroup;
import org.kuali.kfs.module.ld.businessobject.LaborOriginEntry;
import org.kuali.kfs.module.ld.testdata.LaborTestDataPropertyConstants;
import org.kuali.kfs.module.ld.util.LaborTestDataPreparator;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.TestDataPreparator;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.service.BusinessObjectService;

@ConfigureContext
public class LaborOriginEntryServiceTest extends KualiTestBase {

    private Properties properties;
    private String fieldNames;
    private String deliminator;
    private OriginEntryGroup group1, group2;
    private Map fieldValues;

    private LaborOriginEntryService laborOriginEntryService;
    private LaborOriginEntryGroupService originEntryGroupService;
    private BusinessObjectService businessObjectService;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        String messageFileName = LaborTestDataPropertyConstants.TEST_DATA_PACKAGE_NAME + "/message.properties";
        String propertiesFileName = LaborTestDataPropertyConstants.TEST_DATA_PACKAGE_NAME + "/laborOriginEntryService.properties";

        properties = TestDataPreparator.loadPropertiesFromClassPath(propertiesFileName);
        
        fieldNames = properties.getProperty("fieldNames");
        deliminator = properties.getProperty("deliminator");

        laborOriginEntryService = SpringContext.getBean(LaborOriginEntryService.class);
        originEntryGroupService = SpringContext.getBean(LaborOriginEntryGroupService.class);
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);

        Date today = (SpringContext.getBean(DateTimeService.class)).getCurrentSqlDate();
        
        //TODO:- commented out
        //group1 = originEntryGroupService.createGroup(today, LABOR_MAIN_POSTER_VALID, false, false, false);
        //group2 = originEntryGroupService.createGroup(today, LABOR_MAIN_POSTER_VALID, false, false, false);

//        LaborOriginEntry cleanup = new LaborOriginEntry();
//        ObjectUtil.populateBusinessObject(cleanup, properties, "dataCleanup", fieldNames, deliminator);
//        fieldValues = ObjectUtil.buildPropertyMap(cleanup, Arrays.asList(StringUtils.split(fieldNames, deliminator)));
//        fieldValues.remove(KFSPropertyConstants.TRANSACTION_ENTRY_SEQUENCE_NUMBER);
//        fieldValues.remove(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
//        fieldValues.remove(KFSPropertyConstants.TRANSACTION_DEBIT_CREDIT_CODE);
//        businessObjectService.deleteMatching(LaborOriginEntry.class, fieldValues);
    }

    public void testGetEntriesByGroup() throws Exception {
        int numberOfTestData = Integer.valueOf(properties.getProperty("getEntriesByGroup.numOfData"));
        int expectedNumber = Integer.valueOf(properties.getProperty("getEntriesByGroup.expectedNumOfData"));
        //TODO:- do it later
//
//        businessObjectService.save(getInputDataList("getEntriesByGroup.testData", numberOfTestData, group1));
//        List<LaborOriginEntry> entries = convertIteratorAsList(laborOriginEntryService.getEntriesByGroup(group1));
//        assertEquals(expectedNumber, entries.size());
//
//        businessObjectService.save(getInputDataList("getEntriesByGroup.testData", numberOfTestData - 1, group2));
//        entries = convertIteratorAsList(laborOriginEntryService.getEntriesByGroup(group1));
//        assertEquals(expectedNumber, entries.size());
    }

    public void testGetEntriesByGroups() throws Exception {
        //TODO:- do it later
//        int numberOfTestData = Integer.valueOf(properties.getProperty("getEntriesByGroups.numOfData"));
//        int expectedNumber = Integer.valueOf(properties.getProperty("getEntriesByGroups.expectedNumOfData"));
//
//        List<OriginEntryGroup> groups = new ArrayList<OriginEntryGroup>();
//        groups.add(group1);
//        businessObjectService.save(getInputDataList("getEntriesByGroups.testData", numberOfTestData, group1));
//
//        List<LaborOriginEntry> entries = convertIteratorAsList(laborOriginEntryService.getEntriesByGroups(groups));
//        assertEquals(expectedNumber, entries.size());
//
//        groups.add(group2);
//        businessObjectService.save(getInputDataList("getEntriesByGroups.testData", numberOfTestData - 1, group2));
//        entries = convertIteratorAsList(laborOriginEntryService.getEntriesByGroups(groups));
//        assertEquals(expectedNumber + expectedNumber - 1, entries.size());
    }

    public void testGetConsolidatedEntriesByGroup() throws Exception {
        //TODO:- do it later
//        int numberOfTestData = Integer.valueOf(properties.getProperty("getConsolidatedEntriesByGroup.numOfData"));
//        int expectedNumber = Integer.valueOf(properties.getProperty("getConsolidatedEntriesByGroup.expectedNumOfData"));
//        KualiDecimal expectedTotal1 = new KualiDecimal(properties.getProperty("getConsolidatedEntriesByGroup.expectedTotal1"));
//        KualiDecimal expectedTotal2 = new KualiDecimal(properties.getProperty("getConsolidatedEntriesByGroup.expectedTotal2"));
//
//        businessObjectService.save(getInputDataList("getConsolidatedEntriesByGroup.testData", numberOfTestData, group1));
//        List<LaborOriginEntry> entries = convertIteratorAsList(laborOriginEntryService.getEntriesByGroup(group1, true));
//        assertEquals(expectedNumber, entries.size());
//        assertEquals(expectedTotal1, entries.get(0).getTransactionLedgerEntryAmount());
//        assertEquals(expectedTotal2, entries.get(1).getTransactionLedgerEntryAmount());
//
//        businessObjectService.save(getInputDataList("getConsolidatedEntriesByGroup.testData", numberOfTestData, group2));
//        entries = convertIteratorAsList(laborOriginEntryService.getEntriesByGroup(group1, true));
//        assertEquals(expectedNumber, entries.size());
//        assertEquals(expectedTotal1, entries.get(0).getTransactionLedgerEntryAmount());
//        assertEquals(expectedTotal2, entries.get(1).getTransactionLedgerEntryAmount());
    }

    public void testGetSummariedEntriesByGroups() throws Exception {
        int numberOfTestData = Integer.valueOf(properties.getProperty("getSummariedEntriesByGroups.numOfData"));
        int expectedNumber = Integer.valueOf(properties.getProperty("getSummariedEntriesByGroups.expectedNumOfData"));

        List<OriginEntryGroup> groups = new ArrayList<OriginEntryGroup>();
//        LedgerEntryHolder ledgerEntryHolder = laborOriginEntryService.getSummariedEntriesByGroups(groups);
//        assertTrue(ledgerEntryHolder.getLedgerEntries().isEmpty());
//
//        groups.add(group1);
//        businessObjectService.save(getInputDataList("getSummariedEntriesByGroups.testData", numberOfTestData, group1));
//        ledgerEntryHolder = laborOriginEntryService.getSummariedEntriesByGroups(groups);
//        assertEquals(expectedNumber, ledgerEntryHolder.getLedgerEntries().size());
//
//        groups.add(group2);
//        businessObjectService.save(getInputDataList("getSummariedEntriesByGroups.testData", numberOfTestData, group2));
//        ledgerEntryHolder = laborOriginEntryService.getSummariedEntriesByGroups(groups);
//        assertEquals(expectedNumber, ledgerEntryHolder.getLedgerEntries().size());
    }

    public void testGetPosterOutputSummaryByGroups() throws Exception {
        int numberOfTestData = Integer.valueOf(properties.getProperty("getPosterOutputSummaryByGroups.numOfData"));
        int expectedNumber = Integer.valueOf(properties.getProperty("getPosterOutputSummaryByGroups.expectedNumOfData"));

        List<OriginEntryGroup> groups = new ArrayList<OriginEntryGroup>();
        //TODO:- do it later
//        Map<String, PosterOutputSummaryEntry> outputSummary = laborOriginEntryService.getPosterOutputSummaryByGroups(groups);
//        assertTrue(outputSummary.isEmpty());
//
//        groups.add(group1);
//        businessObjectService.save(getInputDataList("getPosterOutputSummaryByGroups.testData", numberOfTestData, group1));
//        outputSummary = laborOriginEntryService.getPosterOutputSummaryByGroups(groups);
//        assertEquals(expectedNumber, outputSummary.size());
//
//        groups.add(group2);
//        businessObjectService.save(getInputDataList("getPosterOutputSummaryByGroups.testData", numberOfTestData, group2));
//        outputSummary = laborOriginEntryService.getPosterOutputSummaryByGroups(groups);
//        assertEquals(expectedNumber, outputSummary.size());
    }

    public void testGetCountOfEntriesInGroups() throws Exception {
        int numberOfTestData = Integer.valueOf(properties.getProperty("getCountOfEntriesInGroups.numOfData"));
        int expectedNumber = Integer.valueOf(properties.getProperty("getCountOfEntriesInGroups.expectedNumOfData"));

        List<OriginEntryGroup> groups = new ArrayList<OriginEntryGroup>();
        
        //TODO:- do it later
//        int count = laborOriginEntryService.getCountOfEntriesInGroups(groups);
//        assertEquals(0, count);
//
//        groups.add(group1);
//        businessObjectService.save(getInputDataList("getCountOfEntriesInGroups.testData", numberOfTestData, group1));
//        count = laborOriginEntryService.getCountOfEntriesInGroups(groups);
//        assertEquals(expectedNumber, count);
//
//        groups.add(group2);
//        businessObjectService.save(getInputDataList("getCountOfEntriesInGroups.testData", numberOfTestData, group2));
//        count = laborOriginEntryService.getCountOfEntriesInGroups(groups);
//        assertEquals(expectedNumber * 2, count);
    }

    private List getInputDataList(String propertyKeyPrefix, int numberOfInputData, OriginEntryGroup group) {
        return LaborTestDataPreparator.getLaborOriginEntryList(properties, propertyKeyPrefix, numberOfInputData, group);
    }

    private List<LaborOriginEntry> convertIteratorAsList(Iterator<LaborOriginEntry> entries) {
        List<LaborOriginEntry> entryList = new ArrayList<LaborOriginEntry>();
        while (entries.hasNext()) {
            entryList.add(entries.next());
        }
        return entryList;
    }
}
