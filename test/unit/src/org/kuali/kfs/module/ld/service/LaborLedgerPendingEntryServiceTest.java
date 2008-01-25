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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.gl.web.TestDataGenerator;
import org.kuali.module.labor.bo.LaborLedgerPendingEntry;
import org.kuali.test.ConfigureContext;
import org.kuali.test.util.TestDataPreparator;

@ConfigureContext
public class LaborLedgerPendingEntryServiceTest extends KualiTestBase {

    private Properties properties;
    private String fieldNames;
    private String deliminator;
    private List<String> keyFieldList;
    private Map<String, Object> fieldValues;

    private LaborLedgerPendingEntryService laborLedgerPendingEntryService;
    private BusinessObjectService businessObjectService;

    public LaborLedgerPendingEntryServiceTest() {
        super();
        String messageFileName = "test/src/org/kuali/module/labor/testdata/message.properties";
        String propertiesFileName = "test/src/org/kuali/module/labor/testdata/laborLedgerPendingEntryService.properties";

        properties = (new TestDataGenerator(propertiesFileName, messageFileName)).getProperties();
        fieldNames = properties.getProperty("fieldNames");
        deliminator = properties.getProperty("deliminator");
        keyFieldList = Arrays.asList(StringUtils.split(fieldNames, deliminator));
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        laborLedgerPendingEntryService = SpringContext.getBean(LaborLedgerPendingEntryService.class);
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);

        fieldValues = TestDataPreparator.buildCleanupCriteria(LaborLedgerPendingEntry.class, properties, "dataCleanup");
        businessObjectService.deleteMatching(LaborLedgerPendingEntry.class, fieldValues);
    }

    public void testHasPendingLaborLedgerEntryWithAccount() throws Exception {
        String testTarget = "hasPendingLaborLedgerEntryWithAccount";
        int numberOfTestData = Integer.valueOf(properties.getProperty(testTarget + ".numOfData"));
        String accountFieldNames = properties.getProperty(testTarget + ".accountFieldNames");

        // prepare test data -- put the test data into data store
        String prefixForInput = testTarget + ".testData";
        List<LaborLedgerPendingEntry> inputDataList = TestDataPreparator.buildTestDataList(LaborLedgerPendingEntry.class, properties, prefixForInput, numberOfTestData);
        businessObjectService.save(inputDataList);

        // test primary scenarios -- everything is correct and the expected results can be retrieved
        String prefixForValidAccount = testTarget + ".accountWithResults";
        int numOfValidAccounts = Integer.valueOf(properties.getProperty(prefixForValidAccount + ".numOfData"));
        List<Account> validAccounts = TestDataPreparator.buildTestDataList(Account.class, properties, prefixForValidAccount, accountFieldNames, deliminator, numOfValidAccounts);
        for (Account account : validAccounts) {
            assertTrue("At least one record can be found.", laborLedgerPendingEntryService.hasPendingLaborLedgerEntry(account));
        }

        // test secondary scenarios -- the input is not correct and nothing can be returned
        String prefixForInvalidAccount = testTarget + ".accountWithoutResults";
        int numOfInvalidAccounts = Integer.valueOf(properties.getProperty(prefixForInvalidAccount + ".numOfData"));
        List<Account> invalidAccounts = TestDataPreparator.buildTestDataList(Account.class, properties, prefixForInvalidAccount, accountFieldNames, deliminator, numOfInvalidAccounts);
        for (Account account : invalidAccounts) {
            assertFalse("Must not find anything.", laborLedgerPendingEntryService.hasPendingLaborLedgerEntry(account));
        }
    }
}
