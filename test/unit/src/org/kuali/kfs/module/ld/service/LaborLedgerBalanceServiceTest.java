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

import java.util.List;
import java.util.Properties;

import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.gl.web.TestDataGenerator;
import org.kuali.module.labor.bo.LaborOriginEntry;
import org.kuali.module.labor.bo.LaborTransaction;
import org.kuali.module.labor.bo.LedgerBalance;
import org.kuali.module.labor.util.TestDataPreparator;
import org.kuali.test.ConfigureContext;

@ConfigureContext
public class LaborLedgerBalanceServiceTest extends KualiTestBase {
    private Properties properties;
    private String fieldNames;
    private String transactionFieldNames;
    private String deliminator;
    private LaborLedgerBalanceService laborLedgerBalanceService;

    @Override
    public void setUp() throws Exception {
        super.setUp();
        String messageFileName = "test/src/org/kuali/module/labor/testdata/message.properties";
        String propertiesFileName = "test/src/org/kuali/module/labor/testdata/laborLedgerBalanceService.properties";

        properties = (new TestDataGenerator(propertiesFileName, messageFileName)).getProperties();
        fieldNames = properties.getProperty("fieldNames");
        transactionFieldNames = properties.getProperty("transactionFieldNames");
        deliminator = properties.getProperty("deliminator");
        laborLedgerBalanceService = SpringContext.getBean(LaborLedgerBalanceService.class);
    }

    public void testFindLedgerBalance_Found() throws Exception {
        String testTarget = "findLedgerBalance.";
        int numberOfLedgerBalance = Integer.valueOf(properties.getProperty(testTarget + "numOfLedgerBalance"));
        int numberOfTransaction = Integer.valueOf(properties.getProperty(testTarget + "numOfTransaction"));

        List ledgerBalanceList = TestDataPreparator.buildTestDataList(LedgerBalance.class, properties, testTarget + "ledgerBalance", numberOfLedgerBalance);
        List<LaborTransaction> transactionList = TestDataPreparator.buildTestDataList(LaborOriginEntry.class, properties, testTarget + "transaction", transactionFieldNames, deliminator, numberOfTransaction);
        for (LaborTransaction transaction : transactionList) {
            assertNotNull(laborLedgerBalanceService.findLedgerBalance(ledgerBalanceList, transaction));
        }
    }

    public void testFindLedgerBalance_NotFound() throws Exception {
        String testTarget = "findLedgerBalance.";
        int numberOfLedgerBalance = Integer.valueOf(properties.getProperty(testTarget + "numOfLedgerBalance"));
        int numberOfTransaction = Integer.valueOf(properties.getProperty(testTarget + "numOfNotFoundTransaction"));

        List ledgerBalanceList = TestDataPreparator.buildTestDataList(LedgerBalance.class, properties, testTarget + "ledgerBalance", numberOfLedgerBalance);
        List<LaborTransaction> transactionList = TestDataPreparator.buildTestDataList(LaborOriginEntry.class, properties, testTarget + "notFoundTransaction", transactionFieldNames, deliminator, numberOfTransaction);
        for (LaborTransaction transaction : transactionList) {
            assertNull(laborLedgerBalanceService.findLedgerBalance(ledgerBalanceList, transaction));
        }
    }

    public void testAddLedgerBalance_New() throws Exception {
        String testTarget = "addLedgerBalance.";
        int numberOfLedgerBalance = Integer.valueOf(properties.getProperty(testTarget + "numOfLedgerBalance"));
        int numberOfTransaction = Integer.valueOf(properties.getProperty(testTarget + "numOfNewTransaction"));

        List ledgerBalanceList = TestDataPreparator.buildTestDataList(LedgerBalance.class, properties, testTarget + "ledgerBalance", numberOfLedgerBalance);
        List<LaborTransaction> transactionList = TestDataPreparator.buildTestDataList(LaborOriginEntry.class, properties, testTarget + "newTransaction", transactionFieldNames, deliminator, numberOfTransaction);
        for (LaborTransaction transaction : transactionList) {
            laborLedgerBalanceService.addLedgerBalance(ledgerBalanceList, transaction);
        }

        int expectedNumberOfBalances = numberOfLedgerBalance + numberOfTransaction;
        assertEquals(expectedNumberOfBalances, ledgerBalanceList.size());
    }

    public void testAddLedgerBalance_Existing() throws Exception {
        String testTarget = "addLedgerBalance.";
        int numberOfLedgerBalance = Integer.valueOf(properties.getProperty(testTarget + "numOfLedgerBalance"));
        int numberOfTransaction = Integer.valueOf(properties.getProperty(testTarget + "numOfExistingTransaction"));

        List ledgerBalanceList = TestDataPreparator.buildTestDataList(LedgerBalance.class, properties, testTarget + "ledgerBalance", numberOfLedgerBalance);
        List<LaborTransaction> transactionList = TestDataPreparator.buildTestDataList(LaborOriginEntry.class, properties, testTarget + "existingTransaction", transactionFieldNames, deliminator, numberOfTransaction);
        for (LaborTransaction transaction : transactionList) {
            laborLedgerBalanceService.addLedgerBalance(ledgerBalanceList, transaction);
        }

        int expectedNumberOfBalances = numberOfLedgerBalance;
        assertEquals(expectedNumberOfBalances, ledgerBalanceList.size());
    }

    public void testUpdateLedgerBalance() throws Exception {
        String testTarget = "updateLedgerBalance.";
        int numberOfLedgerBalance = Integer.valueOf(properties.getProperty(testTarget + "numOfLedgerBalance"));
        int numberOfTransaction = Integer.valueOf(properties.getProperty(testTarget + "numOfTransaction"));
        int numberOfExpected = Integer.valueOf(properties.getProperty(testTarget + "numOfExpected"));

        List ledgerBalanceList = TestDataPreparator.buildTestDataList(LedgerBalance.class, properties, testTarget + "ledgerBalance", numberOfLedgerBalance);
        List<LaborTransaction> transactionList = TestDataPreparator.buildTestDataList(LaborOriginEntry.class, properties, testTarget + "transaction", transactionFieldNames, deliminator, numberOfTransaction);
        for (LaborTransaction transaction : transactionList) {
            LedgerBalance ledgerBalance = laborLedgerBalanceService.findLedgerBalance(ledgerBalanceList, transaction);
            if (ledgerBalance != null) {
                laborLedgerBalanceService.updateLedgerBalance(ledgerBalance, transaction);
            }
        }

        List<LaborOriginEntry> expectedList = TestDataPreparator.buildTestDataList(LaborOriginEntry.class, properties, testTarget + "expected", transactionFieldNames, deliminator, numberOfExpected);
        for (LaborOriginEntry expected : expectedList) {
            LedgerBalance ledgerBalance = laborLedgerBalanceService.findLedgerBalance(ledgerBalanceList, expected);
            assertNotNull(ledgerBalance);
            ledgerBalance.getMonth1Amount().equals(expected.getTransactionLedgerEntryAmount());
        }
    }
}
