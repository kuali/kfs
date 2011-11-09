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
package org.kuali.kfs.module.ld.batch.service.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.kuali.kfs.gl.batch.service.VerifyTransaction;
import org.kuali.kfs.gl.businessobject.OriginEntryGroup;
import org.kuali.kfs.module.ld.service.LaborOriginEntryGroupService;
import org.kuali.kfs.module.ld.service.LaborOriginEntryService;
import org.kuali.kfs.module.ld.testdata.LaborTestDataPropertyConstants;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.TestDataPreparator;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.PersistenceService;

@ConfigureContext
public class LaborPosterTransactionValidatorTest extends KualiTestBase {

    private Properties properties;
    private String fieldNames;
    private String deliminator;
    private OriginEntryGroup group1;

    private LaborOriginEntryService laborOriginEntryService;
    private LaborOriginEntryGroupService originEntryGroupService;
    private BusinessObjectService businessObjectService;
    private VerifyTransaction laborPosterTransactionValidator;
    private PersistenceService persistenceService;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        String messageFileName = LaborTestDataPropertyConstants.TEST_DATA_PACKAGE_NAME + "/message.properties";
        String propertiesFileName = LaborTestDataPropertyConstants.TEST_DATA_PACKAGE_NAME + "/laborPosterTransactionValidator.properties";

        properties = TestDataPreparator.loadPropertiesFromClassPath(propertiesFileName);
        
        fieldNames = properties.getProperty("fieldNames");
        deliminator = properties.getProperty("deliminator");

        laborPosterTransactionValidator = SpringContext.getBean(VerifyTransaction.class,"laborPosterTransactionValidator");
        laborOriginEntryService = SpringContext.getBean(LaborOriginEntryService.class);
        originEntryGroupService = SpringContext.getBean(LaborOriginEntryGroupService.class);
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        persistenceService = SpringContext.getBean(PersistenceService.class);

        Date today = (SpringContext.getBean(DateTimeService.class)).getCurrentSqlDate();
        //TODO:- commented out
        //group1 = originEntryGroupService.createGroup(today, LABOR_MAIN_POSTER_VALID, true, true, false);
    }

    public void testVerifyTransactionWithForeignReference() throws Exception {
        //TODO:- need to change using file
//        int numberOfTestData = Integer.valueOf(properties.getProperty("verifyTransaction.numOfData"));
//
//        List<LaborOriginEntry> transactionList = LaborTestDataPreparator.getLaborOriginEntryList(properties, "verifyTransaction.testData", numberOfTestData, group1);
//        List<Integer> expectedNumOfErrors = getExpectedDataList("verifyTransaction.expectedNumOfErrors", numberOfTestData);
//
//        businessObjectService.save(transactionList);
//
//        for (int i = 0; i < numberOfTestData; i++) {
//            LaborOriginEntry transaction = transactionList.get(i);
//            persistenceService.retrieveNonKeyFields(transaction);
//            List<Message> errorMessage = laborPosterTransactionValidator.verifyTransaction(transaction);
//            assertEquals(expectedNumOfErrors.get(i).intValue(), errorMessage.size());
//        }
    }

    public void testVerifyTransactionWithoutForeignReference() throws Exception {
//        int numberOfTestData = Integer.valueOf(properties.getProperty("verifyTransaction.numOfData"));
//        List<LaborOriginEntry> transactionList = LaborTestDataPreparator.getLaborOriginEntryList(properties, "verifyTransaction.testData", numberOfTestData, group1);
//
//        for (int i = 0; i < numberOfTestData - 1; i++) {
//            LaborOriginEntry transaction = transactionList.get(i);
//            List<Message> errorMessage = laborPosterTransactionValidator.verifyTransaction(transaction);
//
//            int numOfErrors = errorMessage.size();
//            boolean isTrue = (i < numberOfTestData - 1) ? numOfErrors > 0 : numOfErrors == 0;
//            assertTrue(isTrue);
//        }
    }

    private List<Integer> getExpectedDataList(String propertyKeyPrefix, int numberOfInputData) {
        List<Integer> expectedDataList = new ArrayList<Integer>();
        for (int i = 1; i <= numberOfInputData; i++) {
            String propertyKey = propertyKeyPrefix + i;
            expectedDataList.add(Integer.valueOf(properties.getProperty(propertyKey)));
        }
        return expectedDataList;
    }
}
