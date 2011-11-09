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
package org.kuali.kfs.module.ld.batch.service;

import static org.kuali.kfs.gl.businessobject.OriginEntrySource.LABOR_YEAR_END_BALANCE_FORWARD;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ld.businessobject.LaborOriginEntry;
import org.kuali.kfs.module.ld.businessobject.LedgerBalance;
import org.kuali.kfs.module.ld.service.LaborOriginEntryGroupService;
import org.kuali.kfs.module.ld.testdata.LaborTestDataPropertyConstants;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.kfs.sys.TestDataPreparator;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.PersistenceService;

@ConfigureContext
public class LaborYearEndBalanceForwardServiceTest extends KualiTestBase {
    private Properties properties;
    private String fieldNames, transactionFieldNames;
    private String deliminator;
    private Integer fiscalYear;

    private Map<String, Object> fieldValues;
    private Map<String, String> groupFieldValues;
    private LaborOriginEntryGroupService originEntryGroupService;
    private BusinessObjectService businessObjectService;
    private LaborYearEndBalanceForwardService laborYearEndBalanceForwardService;
    private PersistenceService persistenceService;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        String messageFileName = LaborTestDataPropertyConstants.TEST_DATA_PACKAGE_NAME + "/message.properties";
        String propertiesFileName = LaborTestDataPropertyConstants.TEST_DATA_PACKAGE_NAME + "/laborYearEndBalanceForwardService.properties";

        properties = TestDataPreparator.loadPropertiesFromClassPath(propertiesFileName);
        
        fieldNames = properties.getProperty("fieldNames");
        transactionFieldNames = properties.getProperty("transactionFieldNames");
        deliminator = properties.getProperty("deliminator");
        fiscalYear = Integer.valueOf(properties.getProperty("oldFiscalYear"));

        originEntryGroupService = SpringContext.getBean(LaborOriginEntryGroupService.class);
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        laborYearEndBalanceForwardService = SpringContext.getBean(LaborYearEndBalanceForwardService.class);
        persistenceService = SpringContext.getBean(PersistenceService.class);

        groupFieldValues = new HashMap<String, String>();
        groupFieldValues.put(KFSPropertyConstants.SOURCE_CODE, LABOR_YEAR_END_BALANCE_FORWARD);
        //businessObjectService.deleteMatching(OriginEntryGroup.class, groupFieldValues);

        LaborOriginEntry cleanup = new LaborOriginEntry();
        ObjectUtil.populateBusinessObject(cleanup, properties, "dataCleanup", fieldNames, deliminator);
        fieldValues = ObjectUtil.buildPropertyMap(cleanup, Arrays.asList(StringUtils.split(fieldNames, deliminator)));
        //businessObjectService.deleteMatching(LaborOriginEntry.class, fieldValues);
        businessObjectService.deleteMatching(LedgerBalance.class, fieldValues);
    }

    public void testPostIntoOriginEntry() throws Exception {
//        String testTarget = "postIntoOriginEntry.";
//        int numberOfTestData = Integer.valueOf(properties.getProperty(testTarget + "numOfData"));
//        int expectedNumOfData = Integer.valueOf(properties.getProperty(testTarget + "expectedNumOfOriginEntry"));
//
//        List inputDataList = TestDataPreparator.buildTestDataList(LedgerBalance.class, properties, testTarget + "testData", numberOfTestData);
//        businessObjectService.save(inputDataList);
//
//        for (Object entry : inputDataList) {
//            persistenceService.retrieveNonKeyFields(entry);
//        }
//
//        laborYearEndBalanceForwardService.forwardBalance(fiscalYear, fiscalYear);
//
//        List expectedDataList = TestDataPreparator.buildExpectedValueList(LaborOriginEntryForTesting.class, properties, testTarget + "expected", transactionFieldNames, deliminator, expectedNumOfData);
//        Collection originEntries = businessObjectService.findMatching(LaborOriginEntry.class, fieldValues);
//        for (Object entry : originEntries) {
//            LaborOriginEntryForTesting originEntryForTesting = new LaborOriginEntryForTesting();
//            ObjectUtil.buildObject(originEntryForTesting, entry);
//            assertTrue("Cannot find the expected entry", expectedDataList.contains(originEntryForTesting));
//        }
//        assertEquals(expectedNumOfData, originEntries.size());
    }

    public void testNotPostableBalance() throws Exception {
//        String testTarget = "notPostableBalance.";
//        int numberOfTestData = Integer.valueOf(properties.getProperty(testTarget + "numOfData"));
//        int expectedNumOfData = Integer.valueOf(properties.getProperty(testTarget + "expectedNumOfOriginEntry"));
//
//        List inputDataList = TestDataPreparator.buildTestDataList(LedgerBalance.class, properties, testTarget + "testData", numberOfTestData);
//        businessObjectService.save(inputDataList);
//
//        for (Object entry : inputDataList) {
//            persistenceService.retrieveNonKeyFields(entry);
//        }
//
//        laborYearEndBalanceForwardService.forwardBalance(fiscalYear, fiscalYear);
//
//        assertEquals(expectedNumOfData, businessObjectService.countMatching(LaborOriginEntry.class, fieldValues));
    }
}
