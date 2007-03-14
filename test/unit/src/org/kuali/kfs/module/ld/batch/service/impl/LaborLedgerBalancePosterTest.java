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
package org.kuali.module.labor.batch.poster;

import static org.kuali.module.gl.bo.OriginEntrySource.LABOR_MAIN_POSTER_VALID;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.PropertyConstants;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DateTimeService;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.gl.batch.poster.PostTransaction;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.Transaction;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.gl.web.TestDataGenerator;
import org.kuali.module.labor.LaborConstants;
import org.kuali.module.labor.bo.LaborOriginEntry;
import org.kuali.module.labor.bo.LedgerBalance;
import org.kuali.module.labor.bo.LedgerEntry;
import org.kuali.module.labor.service.LaborGeneralLedgerEntryService;
import org.kuali.module.labor.service.LaborLedgerEntryService;
import org.kuali.module.labor.util.ObjectUtil;
import org.kuali.test.KualiTestBase;
import org.kuali.test.WithTestSpringContext;
import org.springframework.beans.factory.BeanFactory;

@WithTestSpringContext
public class LaborLedgerBalancePosterTest extends KualiTestBase {
    
    private Properties properties;
    private String fieldNames;
    private String deliminator;
    private List<String> keyFieldList;
    private Map fieldValues;
    private OriginEntryGroup group1;
    private Date today;

    private BusinessObjectService businessObjectService;
    private PostTransaction laborLedgerBalancePoster;
    private OriginEntryGroupService originEntryGroupService;

    public void setUp() throws Exception {
        super.setUp();
        String messageFileName = "test/src/org/kuali/module/labor/testdata/message.properties";
        String propertiesFileName = "test/src/org/kuali/module/labor/testdata/laborLedgerBalancePoster.properties";

        properties = (new TestDataGenerator(propertiesFileName, messageFileName)).getProperties();
        fieldNames = properties.getProperty("fieldNames");
        deliminator = properties.getProperty("deliminator");
        keyFieldList = Arrays.asList(StringUtils.split(fieldNames, deliminator));
        
        BeanFactory beanFactory = SpringServiceLocator.getBeanFactory();       
        laborLedgerBalancePoster = (PostTransaction) beanFactory.getBean("laborLedgerBalancePoster");
        businessObjectService = (BusinessObjectService) beanFactory.getBean("businessObjectService");
        originEntryGroupService = (OriginEntryGroupService) beanFactory.getBean("glOriginEntryGroupService");
        DateTimeService dateTimeService = (DateTimeService)beanFactory.getBean("dateTimeService");
        
        group1 = originEntryGroupService.createGroup(dateTimeService.getCurrentSqlDate(), LABOR_MAIN_POSTER_VALID, false, false, false);        
        today = dateTimeService.getCurrentDate();
        
        LedgerBalance cleanup = new LedgerBalance();
        ObjectUtil.populateBusinessObject(cleanup, properties, "dataCleanup", fieldNames, deliminator);
        fieldValues = ObjectUtil.buildPropertyMap(cleanup, Arrays.asList(StringUtils.split(fieldNames, deliminator)));
        businessObjectService.deleteMatching(LedgerBalance.class, fieldValues);
    }
    
    public void testPost() throws Exception {       
        int numberOfTestData = Integer.valueOf(properties.getProperty("post.numOfData"));
        int expectedInsertion = Integer.valueOf(properties.getProperty("post.expectedInsertion")); 
        int expectedUpdate = Integer.valueOf(properties.getProperty("post.expectedUpdate"));
        int expectedNumberOfRecords = Integer.valueOf(properties.getProperty("post.expectedNumberOfRecords")); 
        int expectedNumberOfOperation = Integer.valueOf(properties.getProperty("post.expectedNumberOfOperation"));
        
        List<LaborOriginEntry> transactionList = getInputDataList("post.testData", numberOfTestData, group1);
        Map<String, Integer> operationType = new HashMap<String, Integer>();
        
        for(LaborOriginEntry transaction : transactionList){
            String operation = laborLedgerBalancePoster.post(transaction, 0, today);
            Integer currentNumber = operationType.get(operation);
            Integer numberOfOperation = currentNumber != null ? currentNumber + 1 : 1;
            operationType.put(operation, numberOfOperation);
        }
        
        Collection returnValues = businessObjectService.findMatching(LedgerBalance.class, fieldValues);
        assertEquals(expectedNumberOfRecords, returnValues.size());
        
        assertEquals(expectedNumberOfOperation, operationType.size());
        assertEquals(expectedInsertion, operationType.get(LaborConstants.OperationType.INSERT).intValue());
        assertEquals(expectedUpdate, operationType.get(LaborConstants.OperationType.UPDATE).intValue());
        
        LedgerBalance expected1 = new LedgerBalance();
        ObjectUtil.populateBusinessObject(expected1, properties, "post.expected1", fieldNames, deliminator);
    }
    
    private List<LaborOriginEntry> getInputDataList(String propertyKeyPrefix, int numberOfInputData, OriginEntryGroup group) {
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
}

