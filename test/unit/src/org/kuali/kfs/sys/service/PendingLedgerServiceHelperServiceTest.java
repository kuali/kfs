/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
