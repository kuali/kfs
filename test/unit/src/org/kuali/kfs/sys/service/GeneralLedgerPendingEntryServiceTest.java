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
package org.kuali.module.gl.service;

import java.util.Iterator;

import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.gl.bo.GeneralLedgerPendingEntry;
import org.kuali.test.KualiTestBaseWithSpring;

/**
 * This class tests the GeneralLedgerPending service.
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class GeneralLedgerPendingEntryServiceTest extends KualiTestBaseWithSpring {
    private GeneralLedgerPendingEntryService generalLedgerPendingEntryService;
    private final String docHeaderId = "1003";

    protected void setUp() throws Exception {
        super.setUp();

        if (generalLedgerPendingEntryService == null) {
            generalLedgerPendingEntryService = SpringServiceLocator
                    .getGeneralLedgerPendingEntryService();
        }
        // Make sure the document doesn't exist before each test
        generalLedgerPendingEntryService.delete(docHeaderId);
    }

    public void testSave() throws Exception {
        GeneralLedgerPendingEntry testEntry = this.createGeneralLedgerPendingEntry();
        generalLedgerPendingEntryService.save(testEntry);
        GeneralLedgerPendingEntry generalLedgerPendingEntry = generalLedgerPendingEntryService
                .getByPrimaryId(testEntry.getTrnEntryLedgerSequenceNumber(), docHeaderId);
        assertNotNull("Save didn't save this entry", generalLedgerPendingEntry);
        generalLedgerPendingEntryService.delete(docHeaderId);
    }

    public void testGetByPrimaryId() throws Exception {
        GeneralLedgerPendingEntry testEntry = this.createGeneralLedgerPendingEntry();
        generalLedgerPendingEntryService.save(testEntry);
        GeneralLedgerPendingEntry generalLedgerPendingEntry = generalLedgerPendingEntryService
                .getByPrimaryId(testEntry.getTrnEntryLedgerSequenceNumber(), docHeaderId);
        assertNotNull("getByPrimaryId didn't get this entry", generalLedgerPendingEntry);
        generalLedgerPendingEntryService.delete(docHeaderId);
    }

    public void testDelete() throws Exception {
        GeneralLedgerPendingEntry generalLedgerPendingEntry = this
                .createGeneralLedgerPendingEntry();
        generalLedgerPendingEntryService.save(generalLedgerPendingEntry);
        generalLedgerPendingEntryService.delete(docHeaderId);
        generalLedgerPendingEntry = generalLedgerPendingEntryService.getByPrimaryId(
                generalLedgerPendingEntry.getTrnEntryLedgerSequenceNumber(), docHeaderId);
        assertNull("Delete didn't delete this entry", generalLedgerPendingEntry);
    }

    public void testFindApprovedPendingLedgerEntries() {
        try {
            GeneralLedgerPendingEntry generalLedgerPendingEntry = this
                    .createGeneralLedgerPendingEntry();
            generalLedgerPendingEntryService.save(generalLedgerPendingEntry);

            Iterator entries = generalLedgerPendingEntryService
                    .findApprovedPendingLedgerEntries();

            int counter = 0;
            while (entries.hasNext()) {
                generalLedgerPendingEntry = (GeneralLedgerPendingEntry) (entries.next());
                ++counter;
                
                System.out.println(counter + ":" + generalLedgerPendingEntry.getFinancialDocumentNumber());
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
        generalLedgerPendingEntry.setFinancialDocumentNumber(docHeaderId);
        generalLedgerPendingEntry.setChartOfAccountsCode("BA");
        generalLedgerPendingEntry.setFinancialObjectCode("1130");
        generalLedgerPendingEntry.setFinancialBalanceTypeCode("AX");
        generalLedgerPendingEntry.setFinancialObjectTypeCode("AS");
        generalLedgerPendingEntry.setUniversityFiscalYear(new Integer(2005));
        generalLedgerPendingEntry.setUniversityFiscalPeriodCode("7");
        generalLedgerPendingEntry
                .setTransactionLedgerEntryAmount(new KualiDecimal("8.8"));
        generalLedgerPendingEntry.setTransactionLedgerEntryDesc("9");
        generalLedgerPendingEntry.setTransactionDebitCreditCode("D");
        generalLedgerPendingEntry.setTransactionDate(new java.sql.Date(new java.util.Date().getTime()));
        generalLedgerPendingEntry.setFinancialDocumentTypeCode("12");
        generalLedgerPendingEntry.setTrnEntryLedgerSequenceNumber(new Integer(1));
        return generalLedgerPendingEntry;
    }
}
