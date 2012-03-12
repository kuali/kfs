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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.module.ld.businessobject.LaborLedgerPendingEntry;
import org.kuali.kfs.module.ld.testdata.LaborTestDataPropertyConstants;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.TestDataPreparator;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.BusinessObjectService;

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
        String messageFileName = LaborTestDataPropertyConstants.TEST_DATA_PACKAGE_NAME + "/message.properties";
        String propertiesFileName = LaborTestDataPropertyConstants.TEST_DATA_PACKAGE_NAME + "/laborLedgerPendingEntryService.properties";

        properties = TestDataPreparator.loadPropertiesFromClassPath(propertiesFileName);
        
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
            assertTrue("At least one record can be found.", laborLedgerPendingEntryService.hasPendingLaborLedgerEntry(account.getChartOfAccountsCode(), account.getAccountNumber()));
        }

        // test secondary scenarios -- the input is not correct and nothing can be returned
        String prefixForInvalidAccount = testTarget + ".accountWithoutResults";
        int numOfInvalidAccounts = Integer.valueOf(properties.getProperty(prefixForInvalidAccount + ".numOfData"));
        List<Account> invalidAccounts = TestDataPreparator.buildTestDataList(Account.class, properties, prefixForInvalidAccount, accountFieldNames, deliminator, numOfInvalidAccounts);
        for (Account account : invalidAccounts) {
            assertFalse("Must not find anything.", laborLedgerPendingEntryService.hasPendingLaborLedgerEntry(account.getChartOfAccountsCode(), account.getAccountNumber()));
        }
    }
}
