/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.module.gl.service;

import java.util.Iterator;

import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.service.GeneralLedgerPendingEntryService;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.test.KualiTestBase;
import org.kuali.test.WithTestSpringContext;

/**
 * This class tests the GeneralLedgerPending service.
 * 
 * 
 */
@WithTestSpringContext
public class GeneralLedgerPendingEntryServiceTest extends KualiTestBase {
    private GeneralLedgerPendingEntryService generalLedgerPendingEntryService;
    private final String docHeaderId = "1003";

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        if (generalLedgerPendingEntryService == null) {
            generalLedgerPendingEntryService = SpringServiceLocator.getGeneralLedgerPendingEntryService();
        }
        // Make sure the document doesn't exist before each test
        generalLedgerPendingEntryService.delete(docHeaderId);
    }

    public void testSave() throws Exception {
        GeneralLedgerPendingEntry testEntry = this.createGeneralLedgerPendingEntry();
        generalLedgerPendingEntryService.save(testEntry);
        GeneralLedgerPendingEntry generalLedgerPendingEntry = generalLedgerPendingEntryService.getByPrimaryId(testEntry.getTransactionLedgerEntrySequenceNumber(), docHeaderId);
        assertNotNull("Save didn't save this entry", generalLedgerPendingEntry);
        generalLedgerPendingEntryService.delete(docHeaderId);
    }

    public void testGetByPrimaryId() throws Exception {
        GeneralLedgerPendingEntry testEntry = this.createGeneralLedgerPendingEntry();
        generalLedgerPendingEntryService.save(testEntry);
        GeneralLedgerPendingEntry generalLedgerPendingEntry = generalLedgerPendingEntryService.getByPrimaryId(testEntry.getTransactionLedgerEntrySequenceNumber(), docHeaderId);
        assertNotNull("getByPrimaryId didn't get this entry", generalLedgerPendingEntry);
        generalLedgerPendingEntryService.delete(docHeaderId);
    }

    public void testDelete() throws Exception {
        GeneralLedgerPendingEntry generalLedgerPendingEntry = this.createGeneralLedgerPendingEntry();
        generalLedgerPendingEntryService.save(generalLedgerPendingEntry);
        generalLedgerPendingEntryService.delete(docHeaderId);
        generalLedgerPendingEntry = generalLedgerPendingEntryService.getByPrimaryId(generalLedgerPendingEntry.getTransactionLedgerEntrySequenceNumber(), docHeaderId);
        assertNull("Delete didn't delete this entry", generalLedgerPendingEntry);
    }

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
        generalLedgerPendingEntry.setTransactionDate(new java.sql.Date(SpringServiceLocator.getDateTimeService().getCurrentDate().getTime()));
        generalLedgerPendingEntry.setFinancialDocumentTypeCode("12");
        generalLedgerPendingEntry.setTransactionLedgerEntrySequenceNumber(new Integer(1));
        return generalLedgerPendingEntry;
    }
}
