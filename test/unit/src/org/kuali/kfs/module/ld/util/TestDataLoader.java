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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.PropertyConstants;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.web.TestDataGenerator;
import org.kuali.module.labor.bo.LaborGeneralLedgerEntry;
import org.kuali.module.labor.bo.PendingLedgerEntry;
import org.kuali.module.labor.service.LaborGeneralLedgerEntryService;
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

    public TestDataLoader(){
        String messageFileName = "test/src/org/kuali/module/labor/testdata/message.properties";
        String propertiesFileName = "test/src/org/kuali/module/labor/testdata/laborPendingEntry.properties";

        properties = (new TestDataGenerator(propertiesFileName, messageFileName)).getProperties();
        fieldNames = properties.getProperty("fieldNames");
        fieldLength = properties.getProperty("fieldLength");
        deliminator = properties.getProperty("deliminator");
        
        keyFieldList = Arrays.asList(StringUtils.split(fieldNames, deliminator));
        fieldLengthList = Arrays.asList(StringUtils.split(fieldLength, deliminator));
        
        SpringServiceLocator.initializeDDGeneratorApplicationContext();
        beanFactory = SpringServiceLocator.getBeanFactory();
        businessObjectService = (BusinessObjectService) beanFactory.getBean("businessObjectService");
    }
    
    public void loadData(){
        int numberOfInputData = Integer.valueOf(properties.getProperty("numOfData"));        
        int[] fieldLength = this.getFieldLength(fieldLengthList);
        
        //List<PendingLedgerEntry> pendingLedgerEntryList = getInputDataList("data", numberOfInputData, keyFieldList, fieldLength);
        int count = this.loadInputData("data", numberOfInputData, keyFieldList, fieldLength);
        System.out.println("COUNT = " + count);
    }
    
    private int loadInputData(String propertyKeyPrefix, int numberOfInputData, List<String> keyFieldList, int[] fieldLength) {
        int count = 0;
        for (int i = 1; i <= numberOfInputData; i++) {
            String propertyKey = propertyKeyPrefix + i;
            PendingLedgerEntryForTesting inputData = new PendingLedgerEntryForTesting();
            ObjectUtil.populateBusinessObject(inputData, properties, propertyKey, fieldLength, keyFieldList);
            
            if(businessObjectService.countMatching(PendingLedgerEntry.class, inputData.getPrimaryKeyMap())<=0){
                businessObjectService.save(inputData);
                count++;
            }
            //System.out.println("I = " + i + "; count = " + count);
        }
        return count;
    }
    
    private List<PendingLedgerEntry> getInputDataList(String propertyKeyPrefix, int numberOfInputData, List<String> keyFieldList, int[] fieldLength) {
        List inputDataList = new ArrayList();
        for (int i = 1; i <= numberOfInputData; i++) {
            String propertyKey = propertyKeyPrefix + i;
            PendingLedgerEntryForTesting inputData = new PendingLedgerEntryForTesting();
            ObjectUtil.populateBusinessObject(inputData, properties, propertyKey, fieldLength, keyFieldList);
            
            if(!inputDataList.contains(inputData)){
                inputDataList.add(inputData);
            }
        }
        return inputDataList;
    }
    
    private int[] getFieldLength(List<String> fieldLengthList){
        int[] fieldLengthArray = new int[fieldLengthList.size()]; 
        for(int i=0; i< fieldLengthArray.length; i++){
            fieldLengthArray[i] = Integer.valueOf(fieldLengthList.get(i).trim());
        }
        return fieldLengthArray;
    }
    
    public static void main(String[] args){
        TestDataLoader testDataLoader = new TestDataLoader();       
        testDataLoader.loadData();        
    }
}
