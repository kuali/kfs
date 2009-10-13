/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.gl.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.kuali.kfs.gl.businessobject.AccountBalance;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.context.KualiTestBase;

/**
 * A test of the TestDataGenerator fixtures.
 */
public class TestDataGeneratorTest extends KualiTestBase {

    private TestDataGenerator testDataGenerator;
    private GeneralLedgerPendingEntry pendingEntry;
    private AccountBalance accountBalance;

    /**
     * Sets up the test by creating a new TestDataGenerator as well as an account balance and pending entry
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    public void setUp() throws Exception {
        super.setUp();
        testDataGenerator = new TestDataGenerator();
        pendingEntry = new GeneralLedgerPendingEntry();
        accountBalance = new AccountBalance();
    }

    /**
     * test case for generateTransactionDate method of TestDataGenerator class
     * @throws Exception thrown if an exception is encountered for any reason
     */
    public void testGenerateTransactionData() throws Exception {
        testDataGenerator.generateTransactionData(pendingEntry);
        assertEquals(pendingEntry.getAccountNumber(), testDataGenerator.getProperties().getProperty("accountNumber"));
        assertNull(pendingEntry.getTransactionDate());
        try {
            Object property = PropertyUtils.getProperty(pendingEntry, "objectCode");
            assertTrue(false);
        }
        catch (Exception e) {
            assertTrue(true);
        }
    }

    /**
     * test case for generateTransactionDate method of TestDataGenerator class
     * @throws Exception thrown if an exception is encountered for any reason
     */
    public void testGenerateFieldValues() throws Exception {
        Map fieldValues = new HashMap();

        // test business object implementing transaction
        fieldValues = testDataGenerator.generateLookupFieldValues(pendingEntry);
        assertEquals(testDataGenerator.getProperties().getProperty("accountNumber"), fieldValues.get("accountNumber"));
        assertNull(fieldValues.get("transactionDate"));
        assertNull(fieldValues.get("objectCode"));

        // test business object not implementing transaction
        fieldValues = testDataGenerator.generateLookupFieldValues(accountBalance);
        assertEquals(testDataGenerator.getProperties().getProperty("accountNumber"), fieldValues.get("accountNumber"));
        assertEquals(testDataGenerator.getProperties().getProperty("dummyBusinessObject.consolidationOption"), fieldValues.get("dummyBusinessObject.consolidationOption"));

        assertNull(fieldValues.get("timestamp"));
        assertNull(fieldValues.get("finacialObjectCode"));
    }

    /**
     * test case for generateTransactionDate method of TestDataGenerator class
     * @param test an unused parameter
     * @throws Exception thrown if an exception is encountered for any reason
     */
    public void testGenerateFieldValues(String test) throws Exception {
        Map fieldValues = new HashMap();

        List lookupFields = getLookupFields(true);

        // test business object implementing transaction
        fieldValues = testDataGenerator.generateLookupFieldValues(pendingEntry, lookupFields);
        assertEquals(testDataGenerator.getProperties().getProperty("accountNumber"), fieldValues.get("accountNumber"));
        assertEquals(testDataGenerator.getProperties().getProperty("dummyBusinessObject.consolidationOption"), fieldValues.get("dummyBusinessObject.consolidationOption"));
        assertNull(fieldValues.get("transactionDate"));
        assertNull(fieldValues.get("objectCode"));

        // test business object not implementing transaction
        fieldValues = testDataGenerator.generateLookupFieldValues(accountBalance, lookupFields);
        assertEquals(testDataGenerator.getProperties().getProperty("accountNumber"), fieldValues.get("accountNumber"));
        assertEquals(testDataGenerator.getProperties().getProperty("dummyBusinessObject.consolidationOption"), fieldValues.get("dummyBusinessObject.consolidationOption"));

        assertNull(fieldValues.get("timestamp"));
        assertNull(fieldValues.get("finacialObjectCode"));
    }

    /**
     * Generates a list of lookup fields
     * 
     * @param isExtended should the lookup fields include extended fields?
     * @return a List of lookup field names
     */
    protected List getLookupFields(boolean isExtended) {
        List lookupFields = new ArrayList();

        lookupFields.add(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        lookupFields.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        lookupFields.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        lookupFields.add(KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE);
        lookupFields.add(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE);
        lookupFields.add("dummyBusinessObject.consolidationOption");
        lookupFields.add("dummyBusinessObject.pendingEntryOption");

        // include the extended fields
        if (isExtended) {
            lookupFields.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
            lookupFields.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
            lookupFields.add(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);

            lookupFields.add(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE);
            lookupFields.add(KFSPropertyConstants.FINANCIAL_SYSTEM_ORIGINATION_CODE);
            lookupFields.add(KFSPropertyConstants.FINANCIAL_DOCUMENT_TYPE_CODE);
            lookupFields.add(KFSPropertyConstants.DOCUMENT_NUMBER);
            lookupFields.add(KFSPropertyConstants.ORGANIZATION_DOCUMENT_NUMBER);
        }
        return lookupFields;
    }
}
