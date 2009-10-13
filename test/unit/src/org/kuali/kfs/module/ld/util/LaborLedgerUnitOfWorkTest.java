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
package org.kuali.kfs.module.ld.util;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.kuali.kfs.module.ld.businessobject.LaborOriginEntry;
import org.kuali.kfs.module.ld.testdata.LaborTestDataPropertyConstants;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.TestDataPreparator;
import org.kuali.kfs.sys.context.KualiTestBase;

@ConfigureContext(session = khuntley)
public class LaborLedgerUnitOfWorkTest extends KualiTestBase {

    private LaborOriginEntry laborOriginEntry;
    private LaborLedgerUnitOfWork laborLedgerUnitOfWork;

    public void setUp() throws Exception {
        super.setUp();
        
        String messageFileName = LaborTestDataPropertyConstants.TEST_DATA_PACKAGE_NAME + "/message.properties";
        String propertiesFileName = LaborTestDataPropertyConstants.TEST_DATA_PACKAGE_NAME + "/laborLedgerUnitOfWork.properties";

        Properties properties = TestDataPreparator.loadPropertiesFromClassPath(propertiesFileName);
        Properties message = TestDataPreparator.loadPropertiesFromClassPath(propertiesFileName);
        
        laborOriginEntry = TestDataPreparator.buildTestDataObject(LaborOriginEntry.class, properties);
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
        keyList.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
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

