/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.gl.web.lookupable;

import java.util.List;

import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.test.ConfigureContext;

/**
 * This class...
 */
@ConfigureContext
public class PendingLedgerServiceHelperServiceTest extends AbstractGLLookupableHelperServiceTestBase {

    public void testSave() throws Exception {
        testDataGenerator.generateTransactionData(pendingEntry);
        // System.out.println(pendingEntry.getAccountNumber());
        // System.out.println(pendingEntry.getSubAccountNumber());
        // System.out.println(pendingEntry.getFinancialSubObjectCode());
        // System.out.println(pendingEntry.getFinancialObjectCode());
        // System.out.println(pendingEntry.getFinancialObjectTypeCode());
        // System.out.println(pendingEntry.getFinancialBalanceTypeCode());
        // System.out.println(pendingEntry.getUniversityFiscalPeriodCode());
        // System.out.println(pendingEntry.getUniversityFiscalYear());
        // System.out.println(pendingEntry.getChartOfAccountsCode());
        // System.out.println(pendingEntry.getTransactionLedgerEntrySequenceNumber());
        // System.out.println(pendingEntry.getDocumentNumber());

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
    }

    /**
     * @see org.kuali.module.gl.web.lookupable.AbstractGLLookupableTestBase#getLookupFields(boolean)
     */
    public List getLookupFields(boolean isExtended) {
        return null;
    }

}
