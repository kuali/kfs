/*
 * Copyright 2005-2006 The Kuali Foundation
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

import java.util.Iterator;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * This class tests the GeneralLedgerPending service.
 */
@ConfigureContext
public class GeneralLedgerPendingEntryServiceTest extends KualiTestBase {
    private GeneralLedgerPendingEntryService generalLedgerPendingEntryService;
    private final String docHeaderId = "1003";

    /**
     * Initalizes the services needef or this test; also, since this test creates a fake document, deletes
     * any entries from the real version of that document if they exist
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        if (generalLedgerPendingEntryService == null) {
            generalLedgerPendingEntryService = SpringContext.getBean(GeneralLedgerPendingEntryService.class);
        }
        // Make sure the document doesn't exist before each test
        generalLedgerPendingEntryService.delete(docHeaderId);
    }

    /**
     * Tests that pending entries are saved and retrieved properly
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testSave() throws Exception {
        GeneralLedgerPendingEntry testEntry = this.createGeneralLedgerPendingEntry();
        generalLedgerPendingEntryService.save(testEntry);
        GeneralLedgerPendingEntry generalLedgerPendingEntry = generalLedgerPendingEntryService.getByPrimaryId(testEntry.getTransactionLedgerEntrySequenceNumber(), docHeaderId);
        assertNotNull("Save didn't save this entry", generalLedgerPendingEntry);
        generalLedgerPendingEntryService.delete(docHeaderId);
    }

    /**
     * Covers GeneralPendingLedgerEntryService.getByPrimaryId (though, yeah, testSave does too, technically)
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testGetByPrimaryId() throws Exception {
        GeneralLedgerPendingEntry testEntry = this.createGeneralLedgerPendingEntry();
        generalLedgerPendingEntryService.save(testEntry);
        GeneralLedgerPendingEntry generalLedgerPendingEntry = generalLedgerPendingEntryService.getByPrimaryId(testEntry.getTransactionLedgerEntrySequenceNumber(), docHeaderId);
        assertNotNull("getByPrimaryId didn't get this entry", generalLedgerPendingEntry);
        generalLedgerPendingEntryService.delete(docHeaderId);
    }

    /**
     * Covers GeneralLedgerPendingEntryService.delete
     * 
     * @throws Exception thrown if any exception is encountered for any reason
     */
    public void testDelete() throws Exception {
        GeneralLedgerPendingEntry generalLedgerPendingEntry = this.createGeneralLedgerPendingEntry();
        generalLedgerPendingEntryService.save(generalLedgerPendingEntry);
        generalLedgerPendingEntryService.delete(docHeaderId);
        generalLedgerPendingEntry = generalLedgerPendingEntryService.getByPrimaryId(generalLedgerPendingEntry.getTransactionLedgerEntrySequenceNumber(), docHeaderId);
        assertNull("Delete didn't delete this entry", generalLedgerPendingEntry);
    }

    /**
     * Covers GeneralLedgerPendingEntryService.findApprovedPendingLedgerEntries
     */
    public void testFindApprovedPendingLedgerEntries() {
        try {
            GeneralLedgerPendingEntry generalLedgerPendingEntry = this.createGeneralLedgerPendingEntry();
            generalLedgerPendingEntryService.save(generalLedgerPendingEntry);

            Iterator entries = generalLedgerPendingEntryService.findApprovedPendingLedgerEntries();

            int counter = 0;
            while (entries.hasNext()) {
                generalLedgerPendingEntry = (GeneralLedgerPendingEntry) (entries.next());
                ++counter;

                System.out.println(counter + ":" + generalLedgerPendingEntry.getDocumentNumber());
            }
        }
        catch (Exception e) {
            assertTrue("Failed to fetch all entries", true);
        }
        finally {
            generalLedgerPendingEntryService.delete(docHeaderId);
        }
    }

    /**
     * Creates a pending entry fixture
     * 
     * @return a pending entry to test against
     */
    private GeneralLedgerPendingEntry createGeneralLedgerPendingEntry() {
        GeneralLedgerPendingEntry generalLedgerPendingEntry = new GeneralLedgerPendingEntry();

        generalLedgerPendingEntry.setFinancialSystemOriginationCode("01");
        generalLedgerPendingEntry.setDocumentNumber(docHeaderId);
        generalLedgerPendingEntry.setChartOfAccountsCode("BA");
        generalLedgerPendingEntry.setFinancialObjectCode("1130");
        generalLedgerPendingEntry.setFinancialBalanceTypeCode("AX");
        generalLedgerPendingEntry.setFinancialObjectTypeCode("AS");
        generalLedgerPendingEntry.setUniversityFiscalYear(new Integer(2005));
        generalLedgerPendingEntry.setUniversityFiscalPeriodCode("7");
        generalLedgerPendingEntry.setTransactionLedgerEntryAmount(new KualiDecimal("8.8"));
        generalLedgerPendingEntry.setTransactionLedgerEntryDescription("9");
        generalLedgerPendingEntry.setTransactionDebitCreditCode("D");
        generalLedgerPendingEntry.setTransactionDate(new java.sql.Date(SpringContext.getBean(DateTimeService.class).getCurrentDate().getTime()));
        generalLedgerPendingEntry.setFinancialDocumentTypeCode("12");
        generalLedgerPendingEntry.setTransactionLedgerEntrySequenceNumber(new Integer(1));
        generalLedgerPendingEntry.setFinancialDocumentApprovedCode(KFSConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.APPROVED);
        return generalLedgerPendingEntry;
    }
}
