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
package org.kuali.module.financial.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kuali.Constants;
import org.kuali.core.document.DocumentHeader;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.financial.bo.Deposit;
import org.kuali.module.financial.document.CashManagementDocument;
import org.kuali.module.financial.document.CashReceiptDocument;
import org.kuali.module.financial.exceptions.InvalidCashDrawerState;
import org.kuali.module.financial.exceptions.InvalidCashReceiptState;
import org.kuali.test.KualiTestBaseWithSpring;

import edu.iu.uis.eden.exception.WorkflowException;


public class CashManagementServiceTest extends KualiTestBaseWithSpring {
    private final Integer VALID_LINE_NUMBER = new Integer(1);
    private final Integer VALID_LINE_NUMBER2 = new Integer(2);

    private final String UNKNOWN_VERIFICATION_UNIT = "foo";
    private final String KNOWN_VERIFICATION_UNIT = "someWorkgroup";

    private final String KNOWN_CR_STATUS = Constants.CashReceiptConstants.DOCUMENT_STATUS_CD_CASH_RECEIPT_VERIFIED;
    private final String UNKNOWN_CR_STATUS = "foo";


    private CashManagementService cashManagementService;
    private DocumentService docService;
    private BusinessObjectService boService;
    private CashDrawerService cashDrawerService;


    protected void setUp() throws Exception {
        super.setUp();

        cashManagementService = SpringServiceLocator.getCashManagementService();
        docService = SpringServiceLocator.getDocumentService();
        boService = SpringServiceLocator.getBusinessObjectService();
        cashDrawerService = SpringServiceLocator.getCashDrawerService();

        cashDrawerService.openCashDrawer(KNOWN_VERIFICATION_UNIT);
    }


    public void testCreateCashManagementDocument_blankDescription() throws Exception {
        boolean failedAsExpected = false;

        try {
            changeCurrentUser("RJWEISS");
            cashManagementService.createCashManagementDocument("  ", new ArrayList(), KNOWN_VERIFICATION_UNIT);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    public void testCreateCashManagementDocument_nullList() throws Exception {
        boolean failedAsExpected = false;

        try {
            changeCurrentUser("RJWEISS");
            cashManagementService.createCashManagementDocument("testCreateCashManagementDocument_null", null,
                    KNOWN_VERIFICATION_UNIT);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    public void testCreateCashManagementDocument_emptyList() throws Exception {
        boolean failedAsExpected = false;

        try {
            changeCurrentUser("RJWEISS");
            cashManagementService.createCashManagementDocument("testCreateCashManagementDocument_empty", new ArrayList(),
                    KNOWN_VERIFICATION_UNIT);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    public void testCreateCashManagementDocument_blankWorkgroupName() throws Exception {
        CashReceiptDocument cr1 = null;
        CashReceiptDocument cr2 = null;

        try {
            boolean failedAsExpected = false;

            try {
                changeCurrentUser("INEFF");
                List crList = new ArrayList();
                cr1 = buildCashReceiptDoc(KNOWN_VERIFICATION_UNIT, "cr128",
                        Constants.CashReceiptConstants.DOCUMENT_STATUS_CD_CASH_RECEIPT_VERIFIED);
                crList.add(cr1);
                cr2 = buildCashReceiptDoc(KNOWN_VERIFICATION_UNIT, "cr131",
                        Constants.CashReceiptConstants.DOCUMENT_STATUS_CD_CASH_RECEIPT_VERIFIED);
                crList.add(cr2);

                changeCurrentUser("RJWEISS");
                cashManagementService.createCashManagementDocument("testCreateCashManagementDocument_blank", crList, "  ");
            }
            catch (IllegalArgumentException e) {
                failedAsExpected = true;
            }
        }
        finally {
            // cleanup (hide CRs from future tests)
            updateCRDocStatus(cr1, "Z");
            updateCRDocStatus(cr2, "Z");
        }
    }

    public void testCreateCashManagementDocument_invalidReceiptState() throws Exception {
        CashReceiptDocument cr1 = null;
        CashReceiptDocument cr2 = null;

        try {
            boolean failedAsExpected = false;

            try {
                changeCurrentUser("INEFF");
                List crList = new ArrayList();
                cr1 = buildCashReceiptDoc(KNOWN_VERIFICATION_UNIT, "cr159",
                        Constants.CashReceiptConstants.DOCUMENT_STATUS_CD_CASH_RECEIPT_VERIFIED);
                crList.add(cr1);
                cr2 = buildCashReceiptDoc(KNOWN_VERIFICATION_UNIT, "cr162", "Q");
                crList.add(cr2);

                changeCurrentUser("RJWEISS");
                cashManagementService.createCashManagementDocument("testCreateCashManagementDocument_blank", crList,
                        KNOWN_VERIFICATION_UNIT);
            }
            catch (InvalidCashReceiptState e) {
                failedAsExpected = true;
            }
        }
        finally {
            // cleanup (hide CRs from future tests)
            updateCRDocStatus(cr1, "Z");
            updateCRDocStatus(cr2, "Z");
        }
    }

    public void testCreateCashManagementDocument_invalidDrawerState() throws Exception {
        String workgroupName = KNOWN_VERIFICATION_UNIT;
        CashReceiptDocument cr1 = null;
        CashReceiptDocument cr2 = null;

        try {
            boolean failedAsExpected = false;

            try {
                cashDrawerService.closeCashDrawer(workgroupName);

                changeCurrentUser("INEFF");
                List crList = new ArrayList();
                cr1 = buildCashReceiptDoc(workgroupName, "cr193",
                        Constants.CashReceiptConstants.DOCUMENT_STATUS_CD_CASH_RECEIPT_VERIFIED);
                crList.add(cr1);
                cr2 = buildCashReceiptDoc(workgroupName, "cr196",
                        Constants.CashReceiptConstants.DOCUMENT_STATUS_CD_CASH_RECEIPT_VERIFIED);
                crList.add(cr2);

                changeCurrentUser("RJWEISS");
                cashManagementService.createCashManagementDocument("testCreateCashManagementDocument_blank", crList, workgroupName);
            }
            catch (InvalidCashDrawerState e) {
                failedAsExpected = true;
            }
        }
        finally {
            // cleanup (hide CRs from future tests, reopen the drawer)
            updateCRDocStatus(cr1, "Z");
            updateCRDocStatus(cr2, "Z");
            cashDrawerService.openCashDrawer(workgroupName);
        }
    }

    public void testCreateCashManagementDocument() throws Exception {
        String workgroupName = KNOWN_VERIFICATION_UNIT;

        // build the document
        changeCurrentUser("INEFF");
        List crList = new ArrayList();
        CashReceiptDocument cr1 = buildCashReceiptDoc(workgroupName, "cr221",
                Constants.CashReceiptConstants.DOCUMENT_STATUS_CD_CASH_RECEIPT_VERIFIED);
        crList.add(cr1);
        CashReceiptDocument cr2 = buildCashReceiptDoc(workgroupName, "cr224",
                Constants.CashReceiptConstants.DOCUMENT_STATUS_CD_CASH_RECEIPT_VERIFIED);
        crList.add(cr2);

        changeCurrentUser("RJWEISS");
        CashManagementDocument createdDoc = cashManagementService.createCashManagementDocument("testCreateCashManagementDocument",
                crList, workgroupName);

        // retrieve it and look for components
        CashManagementDocument retrievedDoc = (CashManagementDocument) docService.getByDocumentHeaderId(createdDoc
                .getFinancialDocumentNumber());
        assertNotNull(retrievedDoc);
        assertFalse(retrievedDoc.getDeposits().isEmpty());

        Deposit deposit = (Deposit) retrievedDoc.getDeposits().get(0);
        List retrievedCRs = cashManagementService.retrieveCashReceipts(deposit);
        assertEquals(2, retrievedCRs.size());

        // cleanup (hide CRs from future tests, reopen the drawer)
        for (Iterator i = retrievedCRs.iterator(); i.hasNext();) {
            CashReceiptDocument cr = (CashReceiptDocument) i.next();
            updateCRDocStatus(cr, "Z");
        }
        cashDrawerService.openCashDrawer(workgroupName);
    }

    public void testGetCashManagementDocumentByCashReceiptDocument() throws Exception {
        String workgroupName = KNOWN_VERIFICATION_UNIT;

        // build the document
        changeCurrentUser("INEFF");
        List crList = new ArrayList();
        CashReceiptDocument cr1 = buildCashReceiptDoc(workgroupName, "cr256",
                Constants.CashReceiptConstants.DOCUMENT_STATUS_CD_CASH_RECEIPT_VERIFIED);
        crList.add(cr1);

        changeCurrentUser("RJWEISS");
        CashManagementDocument createdDoc = cashManagementService.createCashManagementDocument("testCreateCashManagementDocument",
                crList, workgroupName);

        // retrieve it and look for components
        CashManagementDocument retrievedDoc = cashManagementService.getCashManagementDocumentByCashReceiptDocument(cr1);
        assertNotNull(retrievedDoc);
        assertFalse(retrievedDoc.getDeposits().isEmpty());

        Deposit deposit = (Deposit) retrievedDoc.getDeposits().get(0);
        List retrievedCRs = cashManagementService.retrieveCashReceipts(deposit);
        assertEquals(1, retrievedCRs.size());

        // cleanup (hide CRs from future tests, reopen the drawer)
        for (Iterator i = retrievedCRs.iterator(); i.hasNext();) {
            CashReceiptDocument cr = (CashReceiptDocument) i.next();
            updateCRDocStatus(cr, "Z");
        }
        cashDrawerService.openCashDrawer(workgroupName);
    }


    public void testCreateDeposit_nullDocument() throws Exception {
        boolean failedAsExpected = false;

        try {
            cashManagementService.createDeposit(null, VALID_LINE_NUMBER, new ArrayList(), KNOWN_VERIFICATION_UNIT);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    public void testCreateDeposit_nullLineNumber() throws Exception {
        boolean failedAsExpected = false;

        try {
            cashManagementService.createDeposit(buildCashManagementDoc("testCreateDeposit_nullLineNumber"), null, new ArrayList(),
                    KNOWN_VERIFICATION_UNIT);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    public void testCreateDeposit_nullList() throws Exception {
        boolean failedAsExpected = false;

        try {
            cashManagementService.createDeposit(buildCashManagementDoc("testCreateDeposit_nullList"), VALID_LINE_NUMBER, null,
                    KNOWN_VERIFICATION_UNIT);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    public void testCreateDeposit_emptyList() throws Exception {
        boolean failedAsExpected = false;

        try {
            cashManagementService.createDeposit(buildCashManagementDoc("testCreateDeposit_emptyList"), VALID_LINE_NUMBER,
                    new ArrayList(), KNOWN_VERIFICATION_UNIT);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    public void testCreateDeposit_blankWorkgroupName() throws Exception {
        CashReceiptDocument cr1 = null;

        try {
            boolean failedAsExpected = false;

            changeCurrentUser("INEFF");
            List crList = new ArrayList();
            cr1 = buildCashReceiptDoc(KNOWN_VERIFICATION_UNIT, "cr345",
                    Constants.CashReceiptConstants.DOCUMENT_STATUS_CD_CASH_RECEIPT_VERIFIED);
            crList.add(cr1);

            try {
                changeCurrentUser("RJWEISS");
                cashManagementService.createDeposit(buildCashManagementDoc("testCreateDeposit_blankGroup"), VALID_LINE_NUMBER,
                        crList, " ");
            }
            catch (IllegalArgumentException e) {
                failedAsExpected = true;
            }

            assertTrue(failedAsExpected);
        }
        finally {
            updateCRDocStatus(cr1, "Z");
        }
    }

    public void testCreateDeposit_unverifiedCRDoc() throws Exception {
        CashReceiptDocument cr1 = null;

        try {
            boolean failedAsExpected = false;

            changeCurrentUser("INEFF");
            List crList = new ArrayList();
            cr1 = buildCashReceiptDoc(KNOWN_VERIFICATION_UNIT, "cr373",
                    Constants.CashReceiptConstants.DOCUMENT_STATUS_CD_CASH_RECEIPT_VERIFIED);
            crList.add(cr1);
            CashReceiptDocument cr2 = buildCashReceiptDoc(KNOWN_VERIFICATION_UNIT, "foo", "Z");
            crList.add(cr2);

            try {
                changeCurrentUser("RJWEISS");
                cashManagementService.createDeposit(buildCashManagementDoc("testCreateDeposit_unverifiedCR"), VALID_LINE_NUMBER,
                        crList, " ");
            }
            catch (IllegalArgumentException e) {
                failedAsExpected = true;
            }

            assertTrue(failedAsExpected);
        }
        finally {
            // cleanup (hide CR from future tests)
            updateCRDocStatus(cr1, "Z");
        }
    }

    public void testCreateDeposit() throws Exception {
        CashReceiptDocument cr1 = null;
        CashReceiptDocument cr2 = null;

        try {
            changeCurrentUser("RJWEISS");
            CashManagementDocument cmDoc = buildCashManagementDoc("testCreateDeposit_nullLineNumber");

            changeCurrentUser("INEFF");
            List crList = new ArrayList();
            cr1 = buildCashReceiptDoc(KNOWN_VERIFICATION_UNIT, "cr406",
                    Constants.CashReceiptConstants.DOCUMENT_STATUS_CD_CASH_RECEIPT_VERIFIED);
            crList.add(cr1);
            cr2 = buildCashReceiptDoc(KNOWN_VERIFICATION_UNIT, "cr409",
                    Constants.CashReceiptConstants.DOCUMENT_STATUS_CD_CASH_RECEIPT_VERIFIED);
            crList.add(cr2);

            // verify that it doesn't exist
            List preDeposits = cashManagementService.retrieveDeposits(cmDoc);
            assertTrue(preDeposits.isEmpty());

            // create it
            Deposit createdDeposit = cashManagementService.createDeposit(cmDoc, VALID_LINE_NUMBER, crList, KNOWN_VERIFICATION_UNIT);

            // verify that it exists
            List postDeposits = cashManagementService.retrieveDeposits(cmDoc);
            assertEquals(1, postDeposits.size());
            Deposit retrievedDeposit = (Deposit) postDeposits.get(0);
            assertTrue(createdDeposit.keysEqual(retrievedDeposit));
        }
        finally {
            // cleanup (hide CRs from future tests)
            updateCRDocStatus(cr1, "Z");
            updateCRDocStatus(cr2, "Z");
        }
    }


    public void testRetrieveDeposits_nullDocument() throws Exception {
        boolean failedAsExpected = false;

        try {
            cashManagementService.retrieveDeposits(null);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    public void testRetrieveDeposits_noDeposits() throws Exception {
        CashManagementDocument cmDoc = buildCashManagementDoc("testRetrieveDeposits_noDeposits");

        List deposits = cashManagementService.retrieveDeposits(cmDoc);
        assertTrue(deposits.isEmpty());
    }

    public void testRetrieveDeposits() throws Exception {
        CashReceiptDocument cr1 = null;
        CashReceiptDocument cr2 = null;
        CashReceiptDocument cr3 = null;

        try {
            changeCurrentUser("RJWEISS");
            CashManagementDocument cmDoc = buildCashManagementDoc("testRetrieveDeposits");

            changeCurrentUser("INEFF");
            List crList1 = new ArrayList();
            cr1 = buildCashReceiptDoc(KNOWN_VERIFICATION_UNIT, "cr465",
                    Constants.CashReceiptConstants.DOCUMENT_STATUS_CD_CASH_RECEIPT_VERIFIED);
            crList1.add(cr1);
            cr2 = buildCashReceiptDoc(KNOWN_VERIFICATION_UNIT, "cr468",
                    Constants.CashReceiptConstants.DOCUMENT_STATUS_CD_CASH_RECEIPT_VERIFIED);
            crList1.add(cr2);

            List crList2 = new ArrayList();
            cr3 = buildCashReceiptDoc(KNOWN_VERIFICATION_UNIT, "cr473",
                    Constants.CashReceiptConstants.DOCUMENT_STATUS_CD_CASH_RECEIPT_VERIFIED);
            crList2.add(cr3);

            cashManagementService.createDeposit(cmDoc, VALID_LINE_NUMBER, crList1, KNOWN_VERIFICATION_UNIT);
            cashManagementService.createDeposit(cmDoc, VALID_LINE_NUMBER2, crList2, KNOWN_VERIFICATION_UNIT);

            List deposits = cashManagementService.retrieveDeposits(cmDoc);
            assertEquals(2, deposits.size());
        }
        finally {
            // cleanup (hide CRs from future tests)
            updateCRDocStatus(cr1, "Z");
            updateCRDocStatus(cr2, "Z");
            updateCRDocStatus(cr3, "Z");
        }
    }


    public void testRetrieveCashReceipts_nullDeposit() throws Exception {
        boolean failedAsExpected = false;

        try {
            cashManagementService.retrieveCashReceipts(null);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    public void testRetrieveCashReceipts_blankDeposit() throws Exception {
        boolean failedAsExpected = false;

        try {
            cashManagementService.retrieveCashReceipts(new Deposit());
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    public void testRetrieveCashReceipts() throws Exception {
        CashReceiptDocument cr1 = null;
        CashReceiptDocument cr2 = null;

        try {
            // create a deposit
            changeCurrentUser("RJWEISS");
            CashManagementDocument cmDoc = buildCashManagementDoc("testRetrieveCashReceipts");

            changeCurrentUser("INEFF");
            List createdCRList = new ArrayList();
            cr1 = buildCashReceiptDoc(KNOWN_VERIFICATION_UNIT, "cr529",
                    Constants.CashReceiptConstants.DOCUMENT_STATUS_CD_CASH_RECEIPT_VERIFIED);
            createdCRList.add(cr1);
            cr2 = buildCashReceiptDoc(KNOWN_VERIFICATION_UNIT, "cr532",
                    Constants.CashReceiptConstants.DOCUMENT_STATUS_CD_CASH_RECEIPT_VERIFIED);
            createdCRList.add(cr2);

            Deposit deposit = cashManagementService.createDeposit(cmDoc, VALID_LINE_NUMBER, createdCRList, KNOWN_VERIFICATION_UNIT);

            // verify that it containts the desired cashReceipts
            List retrievedCRList = cashManagementService.retrieveCashReceipts(deposit);
            assertEquals(createdCRList.size(), retrievedCRList.size());
            // UNF: need to compare for equality of list contents, not just size

        }
        finally {
            // cleanup (hide CRs from future tests)
            updateCRDocStatus(cr1, "Z");
            updateCRDocStatus(cr2, "Z");
        }
    }

    public void testCancelDeposit_nullDeposit() throws Exception {
        boolean failedAsExpected = false;

        try {
            cashManagementService.cancelDeposit(null);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    public void testCancelDeposit_blankDeposit() throws Exception {
        cashManagementService.cancelDeposit(new Deposit());
    }

    public void testCancelDeposit_nonexistentDeposit() throws Exception {
        Deposit hackedDeposit = new Deposit();
        hackedDeposit.setFinancialDocumentNumber("-1");
        hackedDeposit.setFinancialDocumentDepositLineNumber(VALID_LINE_NUMBER);

        cashManagementService.cancelDeposit(hackedDeposit);
    }

    public void testCancelDeposit() throws Exception {
        changeCurrentUser("RJWEISS");
        CashManagementDocument cmDoc = buildCashManagementDoc("testCancelDeposit");

        changeCurrentUser("INEFF");
        List crList = new ArrayList();
        CashReceiptDocument cr1 = buildCashReceiptDoc(KNOWN_VERIFICATION_UNIT, "cr582",
                Constants.CashReceiptConstants.DOCUMENT_STATUS_CD_CASH_RECEIPT_VERIFIED);
        crList.add(cr1);
        CashReceiptDocument cr2 = buildCashReceiptDoc(KNOWN_VERIFICATION_UNIT, "cr585",
                Constants.CashReceiptConstants.DOCUMENT_STATUS_CD_CASH_RECEIPT_VERIFIED);
        crList.add(cr2);

        // verify that it doesn't exist
        List preDeposits = cashManagementService.retrieveDeposits(cmDoc);
        assertTrue(preDeposits.isEmpty());

        // create it
        Deposit createdDeposit = cashManagementService.createDeposit(cmDoc, VALID_LINE_NUMBER, crList, KNOWN_VERIFICATION_UNIT);

        // verify that it exists
        List postCreatedDeposits = cashManagementService.retrieveDeposits(cmDoc);
        assertEquals(1, postCreatedDeposits.size());
        Deposit retrievedDeposit = (Deposit) postCreatedDeposits.get(0);
        assertTrue(createdDeposit.keysEqual(retrievedDeposit));

        // cancel it
        cashManagementService.cancelDeposit(retrievedDeposit);

        // verify that it doesn't exist
        List postCancelDeposits = cashManagementService.retrieveDeposits(cmDoc);
        assertTrue(postCancelDeposits.isEmpty());

        // change status of created CRs, so they don't show up in the webapp or other tests
        cr1 = (CashReceiptDocument) docService.getByDocumentHeaderId(cr1.getFinancialDocumentNumber());
        cr2 = (CashReceiptDocument) docService.getByDocumentHeaderId(cr2.getFinancialDocumentNumber());
        updateCRDocStatus(cr1, "Z");
        updateCRDocStatus(cr2, "Z");
    }


    public void testValidateCashReceipts_nullList() throws Exception {
        boolean failedAsExpected = false;

        try {
            cashManagementService.validateVerifiedCashReceipts(null);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    public void testValidateCashReceipts_emptyList() throws Exception {
        boolean failedAsExpected = false;

        try {
            cashManagementService.validateVerifiedCashReceipts(new ArrayList());
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    public void testValidateCashReceipts_noMatching() throws Exception {
        CashReceiptDocument cr1 = null;
        CashReceiptDocument cr2 = null;

        try {
            List crDocs = new ArrayList();

            changeCurrentUser("INEFF");
            cr1 = buildCashReceiptDoc(KNOWN_VERIFICATION_UNIT, "cr645", "Q");
            crDocs.add(cr1);
            cr2 = buildCashReceiptDoc(KNOWN_VERIFICATION_UNIT, "cr647", "Q");
            crDocs.add(cr2);

            boolean succeeded = cashManagementService.validateVerifiedCashReceipts(crDocs);
            assertFalse(succeeded);
        }
        finally {
            updateCRDocStatus(cr1, "Z");
            updateCRDocStatus(cr2, "Z");
        }
    }

    public void testValidateCashReceipts() throws Exception {
        CashReceiptDocument cr1 = null;
        CashReceiptDocument cr2 = null;

        try {
            List crDocs = new ArrayList();

            changeCurrentUser("INEFF");
            cr1 = buildCashReceiptDoc(KNOWN_VERIFICATION_UNIT, "cr667",
                    Constants.CashReceiptConstants.DOCUMENT_STATUS_CD_CASH_RECEIPT_VERIFIED);
            crDocs.add(cr1);
            cr2 = buildCashReceiptDoc(KNOWN_VERIFICATION_UNIT, "cr670",
                    Constants.CashReceiptConstants.DOCUMENT_STATUS_CD_CASH_RECEIPT_VERIFIED);
            crDocs.add(cr2);

            boolean succeeded = cashManagementService.validateVerifiedCashReceipts(crDocs);
            assertTrue(succeeded);
        }
        finally {
            // cleanup (hide CRs from future tests)
            updateCRDocStatus(cr1, "Z");
            updateCRDocStatus(cr2, "Z");
        }
    }


    public void testRetrieveCashReceiptsByVerificationUnit_blankWorkgroup() throws Exception {
        boolean failedAsExpected = false;

        try {
            cashManagementService.retrieveVerifiedCashReceiptsByVerificationUnit("   ");
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    // public void testRetrieveCashReceiptsByVerificationUnit_noMatching() throws Exception {
    // UNF: once getCampusCodeByCashReceiptVerificationUnitWorkgroupName actually does a lookup based on workgroup name, uncomment
    // this and make sure that CRs for groupUnknown don't show up when retrieving for groupKnown
    // fail();
    // }

    public void testRetrieveCashReceiptsByVerificationUnit() throws Exception {
        CashReceiptDocument cr1 = null;

        try {
            // create a cashReceipt
            changeCurrentUser("INEFF");
            cr1 = buildCashReceiptDoc(KNOWN_VERIFICATION_UNIT, "cr710",
                    Constants.CashReceiptConstants.DOCUMENT_STATUS_CD_CASH_RECEIPT_VERIFIED);
            cr1 = (CashReceiptDocument) docService.save(cr1, "saving", null);

            // retrieve it
            List retrievedCRDocs = cashManagementService.retrieveVerifiedCashReceiptsByVerificationUnit(KNOWN_VERIFICATION_UNIT);

            // validate retrieval
            boolean contains = false;
            for (Iterator i = retrievedCRDocs.iterator(); !contains && i.hasNext();) {
                CashReceiptDocument cr = (CashReceiptDocument) i.next();

                String retrievedDocNumber = cr.getFinancialDocumentNumber();
                if (retrievedDocNumber.equals(cr1.getFinancialDocumentNumber())) {
                    contains = true;
                }
            }

            assertTrue(contains);
        }
        finally {
            // cleanup (hide CRs from future tests)
            updateCRDocStatus(cr1, "Z");
        }
    }

    /**
     * This method tests the default return of the single verification unit for now.
     */
    public void testGetCashReceiptVerificationUnitByCampusCode() {
        assertEquals(Constants.CashReceiptConstants.CASH_RECEIPT_VERIFICATION_UNIT, SpringServiceLocator.getCashManagementService()
                .getCashReceiptVerificationUnitWorkgroupNameByCampusCode(Constants.EMPTY_STRING));
    }

    public void testGetCampusCodeByCashReceiptVerificationUnitWorkgroupName_blankWorkgroup() throws Exception {
        boolean failedAsExpected = false;

        try {
            cashManagementService.getCampusCodeByCashReceiptVerificationUnitWorkgroupName("  ");
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    public void testGetCampusCodeByCashReceiptVerificationUnitWorkgroupName_noMatching() throws Exception {
        String campusCode = cashManagementService
                .getCampusCodeByCashReceiptVerificationUnitWorkgroupName(UNKNOWN_VERIFICATION_UNIT);

        assertNull(campusCode);
    }

    // public void testGetCampusCodeByCashReceiptVerificationUnitWorkgroupName() throws Exception {
    // UNF: once getCampusCodeByCashReceiptVerificationUnitWorkgroupName actually does a lookup based on workgroup name, uncomment
    // this and make sure that groupKnown doesn't return a null campusCode
    // String campusCode = cashManagementService.getCampusCodeByCashReceiptVerificationUnitWorkgroupName(KNOWN_VERIFICATION_UNIT);
    //        
    // assertNotNull( campusCode );
    // }

    private CashReceiptDocument buildCashReceiptDoc(String workgroupName, String description, String status)
            throws WorkflowException, UserNotFoundException {
        CashReceiptDocument crDoc = (CashReceiptDocument) docService.getNewDocument(CashReceiptDocument.class);

        crDoc.getDocumentHeader().setFinancialDocumentDescription(description);
        crDoc.getDocumentHeader().setFinancialDocumentStatusCode(status);

        crDoc.setCampusLocationCode(cashManagementService
                .getCampusCodeByCashReceiptVerificationUnitWorkgroupName(KNOWN_VERIFICATION_UNIT));

        docService.save(crDoc, "buildVerifiedCashReceiptDoc", null);

        return crDoc;
    }

    private CashManagementDocument buildCashManagementDoc(String description) throws WorkflowException, UserNotFoundException {
        CashManagementDocument cmDoc = (CashManagementDocument) docService.getNewDocument(CashManagementDocument.class);

        cmDoc.getDocumentHeader().setFinancialDocumentDescription(description);

        return cmDoc;
    }

    private void updateCRDocStatus(CashReceiptDocument crDoc, String status) throws WorkflowException {
        if (crDoc != null) {
            DocumentHeader dh = crDoc.getDocumentHeader();
            dh.setFinancialDocumentStatusCode(status);

            boService.save(dh);
        }
    }
}
