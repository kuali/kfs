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

import static org.kuali.module.gl.bo.OriginEntrySource.LABOR_YEAR_END_BALANCE_FORWARD;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.PersistenceService;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.gl.web.TestDataGenerator;
import org.kuali.module.labor.bo.LaborOriginEntry;
import org.kuali.module.labor.bo.LedgerBalance;
import org.kuali.module.labor.util.ObjectUtil;
import org.kuali.module.labor.util.TestDataPreparator;
import org.kuali.module.labor.util.testobject.LaborOriginEntryForTesting;
import org.kuali.test.KualiTestBase;
import org.kuali.test.WithTestSpringContext;
import org.springframework.beans.factory.BeanFactory;

@WithTestSpringContext
public class LaborYearEndBalanceForwardServiceTest extends KualiTestBase {
    private Properties properties;
    private String fieldNames, transactionFieldNames;
    private String deliminator;
    private Integer fiscalYear;
    
    private Map fieldValues, groupFieldValues;
    private LaborOriginEntryService laborOriginEntryService;
    private OriginEntryGroupService originEntryGroupService;
    private BusinessObjectService businessObjectService;
    private LaborYearEndBalanceForwardService laborYearEndBalanceForwardService;
    private PersistenceService persistenceService;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        String messageFileName = "test/src/org/kuali/module/labor/testdata/message.properties";
        String propertiesFileName = "test/src/org/kuali/module/labor/testdata/laborYearEndBalanceForwardService.properties";

        properties = (new TestDataGenerator(propertiesFileName, messageFileName)).getProperties();
        fieldNames = properties.getProperty("fieldNames");
        transactionFieldNames = properties.getProperty("transactionFieldNames");
        deliminator = properties.getProperty("deliminator");
        fiscalYear = Integer.valueOf(properties.getProperty("oldFiscalYear"));

        BeanFactory beanFactory = SpringServiceLocator.getBeanFactory();
        laborOriginEntryService = (LaborOriginEntryService) beanFactory.getBean("laborOriginEntryService");
        originEntryGroupService = (OriginEntryGroupService) beanFactory.getBean("glOriginEntryGroupService");
        businessObjectService = (BusinessObjectService) beanFactory.getBean("businessObjectService");
        laborYearEndBalanceForwardService = (LaborYearEndBalanceForwardService) beanFactory.getBean("laborYearEndBalanceForwardService");
        persistenceService = (PersistenceService) beanFactory.getBean("persistenceService");

        groupFieldValues = new HashMap();
        groupFieldValues.put(KFSPropertyConstants.SOURCE_CODE, LABOR_YEAR_END_BALANCE_FORWARD);
        originEntryGroupService.deleteOlderGroups(0);
        businessObjectService.deleteMatching(OriginEntryGroup.class, groupFieldValues);

        LaborOriginEntry cleanup = new LaborOriginEntry();
        ObjectUtil.populateBusinessObject(cleanup, properties, "dataCleanup", fieldNames, deliminator);
        fieldValues = ObjectUtil.buildPropertyMap(cleanup, Arrays.asList(StringUtils.split(fieldNames, deliminator)));
        businessObjectService.deleteMatching(LaborOriginEntry.class, fieldValues);
        businessObjectService.deleteMatching(LedgerBalance.class, fieldValues);
    }

    public void testPostIntoOriginEntry() throws Exception {
        String testTarget = "postIntoOriginEntry.";
        int numberOfTestData = Integer.valueOf(properties.getProperty(testTarget + "numOfData"));
        int expectedNumOfData = Integer.valueOf(properties.getProperty(testTarget + "expectedNumOfOriginEntry"));

        List inputDataList = TestDataPreparator.buildTestDataList(LedgerBalance.class, properties, testTarget + "testData", numberOfTestData);
        businessObjectService.save(inputDataList);

        for (Object entry : inputDataList) {
            persistenceService.retrieveNonKeyFields(entry);
        }

        laborYearEndBalanceForwardService.forwardBalance(fiscalYear);
       
        List expectedDataList = TestDataPreparator.buildExpectedValueList(LaborOriginEntryForTesting.class, properties, testTarget + "expected", transactionFieldNames, deliminator, expectedNumOfData);       
        Collection originEntries = businessObjectService.findMatching(LaborOriginEntry.class, fieldValues);       
        for (Object entry : originEntries) {
            LaborOriginEntryForTesting originEntryForTesting = new LaborOriginEntryForTesting();
            ObjectUtil.buildObject(originEntryForTesting, entry);
            assertTrue("Cannot find the expected entry", expectedDataList.contains(originEntryForTesting));
        }
        assertEquals(expectedNumOfData, originEntries.size());
    }
    
    public void testNotPostableBalance() throws Exception {
        String testTarget = "notPostableBalance.";
        int numberOfTestData = Integer.valueOf(properties.getProperty(testTarget + "numOfData"));
        int expectedNumOfData = Integer.valueOf(properties.getProperty(testTarget + "expectedNumOfOriginEntry"));

        List inputDataList = TestDataPreparator.buildTestDataList(LedgerBalance.class, properties, testTarget + "testData", numberOfTestData);
        businessObjectService.save(inputDataList);

        for (Object entry : inputDataList) {
            persistenceService.retrieveNonKeyFields(entry);
        }

        laborYearEndBalanceForwardService.forwardBalance(fiscalYear);

        assertEquals(expectedNumOfData, businessObjectService.countMatching(LaborOriginEntry.class, fieldValues));
    }
}
