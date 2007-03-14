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

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.PersistenceService;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.gl.batch.poster.VerifyTransaction;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.gl.util.Message;
import org.kuali.module.gl.web.TestDataGenerator;
import org.kuali.module.labor.bo.LaborOriginEntry;
import org.kuali.module.labor.service.LaborOriginEntryService;
import org.kuali.module.labor.util.ObjectUtil;
import org.kuali.test.KualiTestBase;
import org.kuali.test.WithTestSpringContext;
import org.springframework.beans.factory.BeanFactory;

@WithTestSpringContext
public class LaborPosterTransactionValidatorTest extends KualiTestBase {

    private Properties properties;
    private String fieldNames;
    private String deliminator;
    private OriginEntryGroup group1;
    
    private LaborOriginEntryService laborOriginEntryService;
    private OriginEntryGroupService originEntryGroupService;
    private BusinessObjectService businessObjectService;
    private VerifyTransaction laborPosterTransactionValidator;
    private PersistenceService persistenceService;

    public void setUp() throws Exception {
        super.setUp();
        String messageFileName = "test/src/org/kuali/module/labor/testdata/message.properties";
        String propertiesFileName = "test/src/org/kuali/module/labor/testdata/laborPosterTransactionValidator.properties";

        properties = (new TestDataGenerator(propertiesFileName, messageFileName)).getProperties();
        fieldNames = properties.getProperty("fieldNames");
        deliminator = properties.getProperty("deliminator");

        BeanFactory beanFactory = SpringServiceLocator.getBeanFactory();
        laborPosterTransactionValidator = (VerifyTransaction) beanFactory.getBean("laborPosterTransactionValidator");
        laborOriginEntryService = (LaborOriginEntryService) beanFactory.getBean("laborOriginEntryService");
        originEntryGroupService = (OriginEntryGroupService) beanFactory.getBean("glOriginEntryGroupService");
        businessObjectService = (BusinessObjectService) beanFactory.getBean("businessObjectService");
        persistenceService = (PersistenceService) beanFactory.getBean("persistenceService");
        
        Date today = ((DateTimeService) beanFactory.getBean("dateTimeService")).getCurrentSqlDate();
        group1 = originEntryGroupService.createGroup(today, LABOR_MAIN_POSTER_VALID, true, true, false);
    }

    public void testVerifyTransactionWithForeignReference() throws Exception {
        int numberOfTestData = Integer.valueOf(properties.getProperty("verifyTransaction.numOfData"));

        List<LaborOriginEntry> transactionList = getInputDataList("verifyTransaction.testData", numberOfTestData, group1);
        List<Integer> expectedNumOfErrors = getExpectedDataList("verifyTransaction.expectedNumOfErrors", numberOfTestData);
        
        businessObjectService.save(transactionList);

        for (int i = 0; i < numberOfTestData; i++) {
            LaborOriginEntry transaction = transactionList.get(i);
            persistenceService.retrieveNonKeyFields(transaction);
            List<Message> errorMessage = laborPosterTransactionValidator.verifyTransaction(transaction);
            assertEquals(expectedNumOfErrors.get(i).intValue(), errorMessage.size());
        }
    }

    public void testVerifyTransactionWithoutForeignReference() throws Exception {
        int numberOfTestData = Integer.valueOf(properties.getProperty("verifyTransaction.numOfData"));
        List<LaborOriginEntry> transactionList = getInputDataList("verifyTransaction.testData", numberOfTestData, group1);

        for (int i = 0; i < numberOfTestData-1; i++) {
            LaborOriginEntry transaction = transactionList.get(i);
            List<Message> errorMessage = laborPosterTransactionValidator.verifyTransaction(transaction);
            
            int numOfErrors = errorMessage.size();
            boolean isTrue = (i < numberOfTestData-1) ?  numOfErrors > 0 : numOfErrors == 0;
            assertTrue(isTrue);
        }
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

    private List<Integer> getExpectedDataList(String propertyKeyPrefix, int numberOfInputData) {
        List expectedDataList = new ArrayList();
        for (int i = 1; i <= numberOfInputData; i++) {
            String propertyKey = propertyKeyPrefix + i;
            expectedDataList.add(Integer.valueOf(properties.getProperty(propertyKey)));
        }
        return expectedDataList;
    }
    
    private void printErrorMessage(List<Message> messageList){
        System.out.println("-->" + messageList.size());
        for(Message message : messageList){
            System.out.println(message.getMessage());
        }       
    }
}
