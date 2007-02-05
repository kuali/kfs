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
package org.kuali.module.labor.util;

import java.util.ArrayList;
import java.util.List;

import org.kuali.PropertyConstants;
import org.kuali.module.gl.web.TestDataGenerator;
import org.kuali.module.labor.bo.LaborOriginEntry;

import junit.framework.TestCase;

public class LaborLedgerUnitOfWorkTest extends TestCase {

    private TestDataGenerator testDataGenerator;
    private LaborOriginEntry laborOriginEntry;
    private LaborLedgerUnitOfWork laborLedgerUnitOfWork;

    public LaborLedgerUnitOfWorkTest() throws Exception {
        String messageFileName = "test/src/org/kuali/module/labor/testdata/message.properties";
        String propertiesFileName = "test/src/org/kuali/module/labor/testdata/laborOriginEntry.properties";

        laborOriginEntry = new LaborOriginEntry();
        testDataGenerator = new TestDataGenerator(propertiesFileName, messageFileName);
        testDataGenerator.generateTransactionData(laborOriginEntry);
    }

    public void testLaborLedgerUnitOfWork() throws Exception {
        laborLedgerUnitOfWork = new LaborLedgerUnitOfWork(laborOriginEntry);
        String charAccountsCode = laborLedgerUnitOfWork.getWorkingEntry().getChartOfAccountsCode();
        assertTrue(charAccountsCode.equals(laborOriginEntry.getChartOfAccountsCode()));
    }

    public void testResetLaborLedgerUnitOfWork() throws Exception {
        laborLedgerUnitOfWork = new LaborLedgerUnitOfWork();
        laborLedgerUnitOfWork.resetLaborLedgerUnitOfWork(laborOriginEntry);

        String charAccountsCode = laborLedgerUnitOfWork.getWorkingEntry().getChartOfAccountsCode();
        assertTrue(charAccountsCode.equals(laborOriginEntry.getChartOfAccountsCode()));

        List keyList = new ArrayList();
        keyList.add(PropertyConstants.FINANCIAL_OBJECT_CODE);
        laborLedgerUnitOfWork.resetLaborLedgerUnitOfWork(laborOriginEntry, keyList);

        charAccountsCode = laborLedgerUnitOfWork.getWorkingEntry().getChartOfAccountsCode();
        assertFalse(charAccountsCode.equals(laborOriginEntry.getChartOfAccountsCode()));

        String objectCode = laborLedgerUnitOfWork.getWorkingEntry().getFinancialObjectCode();
        assertTrue(objectCode.equals(laborOriginEntry.getFinancialObjectCode()));
    }

    public void testAddEntryIntoUnit() throws Exception {
        laborLedgerUnitOfWork = new LaborLedgerUnitOfWork(laborOriginEntry);
        assertTrue(laborLedgerUnitOfWork.getNumOfMember() == 1);

        assertTrue(laborLedgerUnitOfWork.addEntryIntoUnit(laborOriginEntry));
        assertTrue(laborLedgerUnitOfWork.getNumOfMember() == 2);

        assertTrue(laborLedgerUnitOfWork.addEntryIntoUnit(laborOriginEntry));
        assertTrue(laborLedgerUnitOfWork.getNumOfMember() == 3);

        laborOriginEntry.setUniversityFiscalYear(1000);
        assertFalse(laborLedgerUnitOfWork.addEntryIntoUnit(laborOriginEntry));
        assertFalse(laborLedgerUnitOfWork.getNumOfMember() == 4);
    }

    public void testCanContain() throws Exception {
        laborLedgerUnitOfWork = new LaborLedgerUnitOfWork();
        assertFalse(laborLedgerUnitOfWork.canContain(laborOriginEntry));

        laborLedgerUnitOfWork.resetLaborLedgerUnitOfWork(laborOriginEntry);
        assertTrue(laborLedgerUnitOfWork.canContain(laborOriginEntry));

        laborOriginEntry.setUniversityFiscalYear(1000);
        assertFalse(laborLedgerUnitOfWork.canContain(laborOriginEntry));
    }

    public void testHasSameKey() throws Exception {
        laborLedgerUnitOfWork = new LaborLedgerUnitOfWork();
        assertFalse(laborLedgerUnitOfWork.hasSameKey(laborOriginEntry));

        laborLedgerUnitOfWork.resetLaborLedgerUnitOfWork(laborOriginEntry);
        assertTrue(laborLedgerUnitOfWork.hasSameKey(laborOriginEntry));

        laborOriginEntry.setUniversityFiscalYear(1000);
        assertFalse(laborLedgerUnitOfWork.hasSameKey(laborOriginEntry));
    }
}