/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.gl.web;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.kuali.module.gl.bo.AccountBalance;
import org.kuali.module.gl.bo.GeneralLedgerPendingEntry;
import org.kuali.test.KualiTestBase;

/**
 * This class...
 * 
 * @author Bin Gao from Michigan State University
 */
public class TestDataGeneratorTest extends KualiTestBase {
    
    private TestDataGenerator testDataGenerator;
    private GeneralLedgerPendingEntry pendingEntry;
    private AccountBalance accountBalance;

    public void setUp() throws Exception {
        super.setUp();
        testDataGenerator = new TestDataGenerator();
        pendingEntry = new GeneralLedgerPendingEntry();
        accountBalance = new AccountBalance();
    }

    // test case for generateTransactionDate method of TestDataGenerator class
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

    // test case for generateTransactionDate method of TestDataGenerator class
    public void testGenerateFieldValues() throws Exception {
        Map fieldValues = new HashMap();

        fieldValues = testDataGenerator.generateLookupFieldValues(pendingEntry);
        assertEquals(testDataGenerator.getProperties().getProperty("accountNumber"), fieldValues.get("accountNumber"));
        assertNull(fieldValues.get("transactionDate"));
        assertNull(fieldValues.get("objectCode"));

        fieldValues = testDataGenerator.generateLookupFieldValues(accountBalance);
        assertEquals(testDataGenerator.getProperties().getProperty("accountNumber"), fieldValues.get("accountNumber"));
        assertEquals(testDataGenerator.getProperties().getProperty("dummyBusinessObject.consolidationOption"), fieldValues
                .get("dummyBusinessObject.consolidationOption"));

        assertNull(fieldValues.get("timestamp"));
        assertNull(fieldValues.get("finacialObjectCode"));
    }
}
