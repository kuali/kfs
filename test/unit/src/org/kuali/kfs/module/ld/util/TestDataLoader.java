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
package org.kuali.module.labor.util;

import static org.kuali.module.gl.bo.OriginEntrySource.LABOR_SCRUBBER_VALID;
import static org.kuali.module.gl.bo.OriginEntrySource.SCRUBBER_VALID;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.PersistenceService;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.gl.web.TestDataGenerator;
import org.kuali.module.labor.bo.LaborGeneralLedgerEntry;
import org.kuali.module.labor.bo.LaborOriginEntry;
import org.kuali.module.labor.bo.LedgerEntry;
import org.kuali.module.labor.bo.PendingLedgerEntry;
import org.kuali.module.labor.service.LaborGeneralLedgerEntryService;
import org.kuali.module.labor.service.LaborOriginEntryService;
import org.kuali.module.labor.service.LaborPosterService;
import org.kuali.module.labor.util.testobject.PendingLedgerEntryForTesting;
import org.springframework.beans.factory.BeanFactory;

public class TestDataLoader {
    private Properties properties;
    private String fieldNames;
    private String fieldLength;
    private String deliminator;
    
    private List<String> keyFieldList;
    private List<String> fieldLengthList;

    private static BeanFactory beanFactory;
    private BusinessObjectService businessObjectService;
    private OriginEntryGroupService originEntryGroupService;
    private LaborOriginEntryService laborOriginEntryService;
    private PersistenceService persistenceService;

    public TestDataLoader() {
        String messageFileName = "test/src/org/kuali/module/labor/testdata/message.properties";
        String propertiesFileName = "test/src/org/kuali/module/labor/testdata/laborTransaction.properties";

        properties = (new TestDataGenerator(propertiesFileName, messageFileName)).getProperties();
        fieldNames = properties.getProperty("fieldNames");
        fieldLength = properties.getProperty("fieldLength");
        deliminator = properties.getProperty("deliminator");

        keyFieldList = Arrays.asList(StringUtils.split(fieldNames, deliminator));
        fieldLengthList = Arrays.asList(StringUtils.split(fieldLength, deliminator));

        SpringServiceLocator.initializeDDGeneratorApplicationContext();
        beanFactory = SpringServiceLocator.getBeanFactory();
        businessObjectService = (BusinessObjectService) beanFactory.getBean("businessObjectService");

        laborOriginEntryService = (LaborOriginEntryService) beanFactory.getBean("laborOriginEntryService");
        originEntryGroupService = (OriginEntryGroupService) beanFactory.getBean("glOriginEntryGroupService");
        persistenceService = (PersistenceService) beanFactory.getBean("persistenceService");
    }

    public int loadTransactionIntoPendingEntryTable() {
        int numberOfInputData = Integer.valueOf(properties.getProperty("numOfData"));
        int[] fieldLength = this.getFieldLength(fieldLengthList);
        return this.loadInputData("data", numberOfInputData, keyFieldList, fieldLength);
    }

    public int loadTransactionIntoOriginEntryTable() {
        int numberOfInputData = Integer.valueOf(properties.getProperty("numOfData"));
           
        Date today = ((DateTimeService) beanFactory.getBean("dateTimeService")).getCurrentSqlDate();
        OriginEntryGroup groupToPost = originEntryGroupService.createGroup(today, LABOR_SCRUBBER_VALID, true, true, false);

        int[] fieldLength = this.getFieldLength(fieldLengthList);
        List<LaborOriginEntry> originEntries = this.loadInputData(LaborOriginEntry.class, "data", numberOfInputData, keyFieldList, fieldLength);
        for(LaborOriginEntry entry : originEntries){
            entry.setEntryGroupId(groupToPost.getId());
        }
        
        businessObjectService.save(originEntries);       
        return originEntries.size();
    }
    
    public int loadTransactionIntoGLOriginEntryTable() {
        int numberOfInputData = Integer.valueOf(properties.getProperty("numOfData"));
           
        Date today = ((DateTimeService) beanFactory.getBean("dateTimeService")).getCurrentSqlDate();
        OriginEntryGroup groupToPost = originEntryGroupService.createGroup(today, SCRUBBER_VALID, true, true, false);

        int[] fieldLength = this.getFieldLength(fieldLengthList);
        List<OriginEntry> originEntries = this.loadInputData(OriginEntry.class, "data", numberOfInputData, keyFieldList, fieldLength);
        for(OriginEntry entry : originEntries){
            entry.setEntryGroupId(groupToPost.getId());
        }
        
        businessObjectService.save(originEntries);       
        return originEntries.size();
    }
    
    public void generateLedgerEntryTestData() {
        int numberOfInputData = Integer.valueOf(properties.getProperty("numOfData"));

        int[] fieldLength = this.getFieldLength(fieldLengthList);
        List<LedgerEntry> entries = this.loadInputData(LedgerEntry.class, "data", 2, keyFieldList, fieldLength);
        System.out.println(StringUtils.deleteWhitespace("a  a   a"));
        for(LedgerEntry entry : entries){
            System.out.print("data = ");
            for(String field : keyFieldList){
                try{
                    Object propertyValue = PropertyUtils.getProperty(entry, field);
                    String value = (propertyValue==null) ? ";" : (propertyValue + ";");
                    System.out.println(field + ":" + StringUtils.deleteWhitespace(value));
                }
                catch(Exception e){e.printStackTrace();}
            }
            System.out.println();
        }
    }    

    private int loadInputData(String propertyKeyPrefix, int numberOfInputData, List<String> keyFieldList, int[] fieldLength) {
        int count = 0;
        for (int i = 1; i <= numberOfInputData; i++) {
            String propertyKey = propertyKeyPrefix + i;
            PendingLedgerEntryForTesting inputData = new PendingLedgerEntryForTesting();
            ObjectUtil.populateBusinessObject(inputData, properties, propertyKey, fieldLength, keyFieldList);

            if (businessObjectService.countMatching(PendingLedgerEntry.class, inputData.getPrimaryKeyMap()) <= 0) {
                businessObjectService.save(inputData);
                count++;
            }
        }
        return count;
    }

    private List loadInputData(Class clazz, String propertyKeyPrefix, int numberOfInputData, List<String> keyFieldList, int[] fieldLength) {
        List inputDataList = new ArrayList();
        for (int i = 1; i <= numberOfInputData; i++) {
            String propertyKey = propertyKeyPrefix + i;
            try {
                Object inputData = clazz.newInstance();
                ObjectUtil.populateBusinessObject(inputData, properties, propertyKey, fieldLength, keyFieldList);

                inputDataList.add(inputData);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return inputDataList;
    }

    private int[] getFieldLength(List<String> fieldLengthList) {
        int[] fieldLengthArray = new int[fieldLengthList.size()];
        for (int i = 0; i < fieldLengthArray.length; i++) {
            fieldLengthArray[i] = Integer.valueOf(fieldLengthList.get(i).trim());
        }
        return fieldLengthArray;
    }

    public static void main(String[] args) {
        TestDataLoader testDataLoader = new TestDataLoader();
        for(int i=0; i<1; i++){
            int numOfData = testDataLoader.loadTransactionIntoPendingEntryTable();
            System.out.println("Number of Data Loaded = " + numOfData);
        }
        //testDataLoader.generateLedgerEntryTestData();
        System.exit(0);
    }
}
