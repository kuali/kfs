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
package org.kuali.kfs.sys.service;

import java.util.List;

import org.kuali.kfs.gl.businessobject.lookup.AbstractGeneralLedgerLookupableHelperServiceTestBase;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;

/**
 * A test that covers PendingLedgerService
 */
@ConfigureContext
public class PendingLedgerServiceHelperServiceTest extends AbstractGeneralLedgerLookupableHelperServiceTestBase {

    /**
     * Tests that PendingLedgerService is successfully saving and retrieving entries
     * @throws Exception thrown if any exception is encountered for any reason 
     */
    public void testSave() throws Exception {
        testDataGenerator.generateTransactionData(pendingEntry);

        getPendingEntryService().delete("TEST69999");
        getPendingEntryService().save(pendingEntry);

        GeneralLedgerPendingEntry entry = getPendingEntryService().getByPrimaryId(new Integer(9876), "TEST69999");

        assertEquals(pendingEntry.getAccountNumber(), entry.getAccountNumber());
        assertEquals(pendingEntry.getSubAccountNumber(), entry.getSubAccountNumber());
        assertEquals(pendingEntry.getFinancialSubObjectCode(), entry.getFinancialSubObjectCode());
        assertEquals(pendingEntry.getFinancialObjectCode(), entry.getFinancialObjectCode());
        assertEquals(pendingEntry.getFinancialObjectTypeCode(), entry.getFinancialObjectTypeCode());
        assertEquals(pendingEntry.getFinancialBalanceTypeCode(), entry.getFinancialBalanceTypeCode());
        assertEquals(pendingEntry.getUniversityFiscalPeriodCode(), entry.getUniversityFiscalPeriodCode());
        assertEquals(pendingEntry.getUniversityFiscalYear(), entry.getUniversityFiscalYear());
        assertEquals(pendingEntry.getChartOfAccountsCode(), entry.getChartOfAccountsCode());
        assertEquals(pendingEntry.getTransactionLedgerEntrySequenceNumber(), entry.getTransactionLedgerEntrySequenceNumber());
        assertEquals(pendingEntry.getDocumentNumber(), entry.getDocumentNumber());
    }


    /**
     * @see org.kuali.module.gl.web.lookupable.AbstractGLLookupableTestBase#testGetSearchResults()
     */
    public void testGetSearchResults() throws Exception {
        assertTrue("I'm successfully implementing an abstract method", true);
    }

    /**
     * @see org.kuali.module.gl.web.lookupable.AbstractGLLookupableTestBase#getLookupFields(boolean)
     */
    public List getLookupFields(boolean isExtended) {
        return null;
    }

}
