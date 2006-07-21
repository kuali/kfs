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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.Constants;
import org.kuali.core.document.Document;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.financial.bo.BankAccount;
import org.kuali.module.financial.bo.CashDrawer;
import org.kuali.module.financial.bo.Deposit;
import org.kuali.module.financial.document.CashManagementDocument;
import org.kuali.module.financial.document.CashReceiptDocument;
import org.kuali.module.financial.exceptions.CashDrawerStateException;
import org.kuali.module.financial.exceptions.InvalidCashReceiptState;
import org.kuali.test.KualiTestBaseWithSession;
import org.kuali.test.monitor.ChangeMonitor;
import org.kuali.test.monitor.DocumentWorkflowStatusMonitor;

import edu.iu.uis.eden.exception.WorkflowException;

public class CashManagementServiceTest extends KualiTestBaseWithSession {
    static final String CMST_WORKGROUP = "CashManagementServiceTest";


    DocumentService documentService;
    CashReceiptService cashReceiptService;
    CashManagementService cashManagementService;
    CashDrawerService cashDrawerService;
    BusinessObjectService businessObjectService;


    @Override
    protected void setUp() throws Exception {
        super.setUp();

        documentService = SpringServiceLocator.getDocumentService();
        cashReceiptService = SpringServiceLocator.getCashReceiptService();
        cashManagementService = SpringServiceLocator.getCashManagementService();
        cashDrawerService = SpringServiceLocator.getCashDrawerService();
        businessObjectService = SpringServiceLocator.getBusinessObjectService();
    }

    /**
     * @see org.kuali.test.KualiTestBaseWithSpring#needsTestTransaction()
     */
    @Override
    protected boolean needsTestTransaction() {
        return false;
    }


    final public void testCreateCashManagementDocument_blankUnitName() throws Exception {
        boolean failedAsExpected = false;

        try {
            cashManagementService.createCashManagementDocument("    ", "foo", "cmst1");
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    final public void testCreateCashManagementDocument_blankDocDescription() throws Exception {
        boolean failedAsExpected = false;

        try {
            cashManagementService.createCashManagementDocument(CMST_WORKGROUP, null, "cmst2");
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    final public void testCreateCashManagementDocument_valid() throws Exception {
        String testDocumentId = null;

        try {
            deleteIfExists(CMST_WORKGROUP);
            CashDrawer preDocCD = cashDrawerService.getByWorkgroupName(CMST_WORKGROUP, true);
            assertTrue(preDocCD.isClosed());

            CashManagementDocument createdDoc = cashManagementService.createCashManagementDocument(CMST_WORKGROUP, "CMST_testCreate_valid", "cmst3");
            assertNotNull(createdDoc);
            testDocumentId = createdDoc.getFinancialDocumentNumber();

            // save it separately
            cashDrawerService.openCashDrawer(CMST_WORKGROUP, testDocumentId);
            documentService.saveDocument(createdDoc);

            // verify that the doc was saved
            CashManagementDocument retrievedDoc = (CashManagementDocument) documentService.getByDocumentHeaderId(testDocumentId);
            assertEquals("S", retrievedDoc.getDocumentHeader().getWorkflowDocument().getRouteHeader().getDocRouteStatus());
        }
        finally {
            // cancel the document
            cleanupCancel(testDocumentId);

            // delete the cashDrawer which was created as a side-effect above
            deleteIfExists(CMST_WORKGROUP);
        }
    }

    final public void testCreateCashManagementDocument_cashDrawerAlreadyOpen() throws Exception {
        try {
            deleteIfExists(CMST_WORKGROUP);
            CashDrawer preDocCD = cashDrawerService.getByWorkgroupName(CMST_WORKGROUP, true);
            assertTrue(preDocCD.isClosed());

            // force the drawer open
            cashDrawerService.openCashDrawer(CMST_WORKGROUP, "foo");

            // fail creating the document since the CashDrawer is already open
            boolean failedAsExpected = false;
            try {
                cashManagementService.createCashManagementDocument(CMST_WORKGROUP, "CMST_testCreate_validCollision 2", "cmst5");
            }
            catch (CashDrawerStateException e) {
                failedAsExpected = true;
            }
        }
        finally {
            // delete the cashDrawer you created
            deleteIfExists(CMST_WORKGROUP);
        }
    }


    final public void testCancelCashManagementDocument_validEmpty() throws Exception {
        String testDocumentId = null;

        try {
            //
            // create empty CMDoc
            //
            deleteIfExists(CMST_WORKGROUP);
            CashDrawer preDocCD = cashDrawerService.getByWorkgroupName(CMST_WORKGROUP, false);
            assertNull(preDocCD);

            CashManagementDocument createdDoc = cashManagementService.createCashManagementDocument(CMST_WORKGROUP, "CMST_testCreate_valid", "cmst3");
            assertNotNull(createdDoc);
            testDocumentId = createdDoc.getFinancialDocumentNumber();

            // save it separately
            cashDrawerService.openCashDrawer(CMST_WORKGROUP, testDocumentId);
            documentService.saveDocument(createdDoc);

            // verify it actually got saved
            CashManagementDocument retrievedDoc = (CashManagementDocument) documentService.getByDocumentHeaderId(testDocumentId);
            assertEquals("S", retrievedDoc.getDocumentHeader().getWorkflowDocument().getRouteHeader().getDocRouteStatus());


            //
            // cancel empty CMDoc
            //
            cashManagementService.cancelCashManagementDocument(createdDoc);

            // verify that the cancellation closed the cash drawer
            CashDrawer postCancelCD = cashDrawerService.getByWorkgroupName(CMST_WORKGROUP, false);
            assertNotNull(postCancelCD);
            assertEquals(postCancelCD.getStatusCode(), Constants.CashDrawerConstants.STATUS_CLOSED);
        }
        finally {
            // cancel the document
            cleanupCancel(testDocumentId);

            // delete the cashDrawer which was created as a side-effect above
            deleteIfExists(CMST_WORKGROUP);
        }
    }


    final public void testCancelCashManagementDocument_valid_interimOnly() throws Exception {
        String testDocumentId = null;

        try {
            //
            // create a valid, empty CashManagementDocument
            deleteIfExists(CMST_WORKGROUP);
            CashDrawer preDocCD = cashDrawerService.getByWorkgroupName(CMST_WORKGROUP, true);
            assertTrue(preDocCD.isClosed());

            CashManagementDocument createdDoc = cashManagementService.createCashManagementDocument(CMST_WORKGROUP, "CMST_testAddID_valid", null);
            testDocumentId = createdDoc.getFinancialDocumentNumber();

            // save it separately
            cashDrawerService.openCashDrawer(CMST_WORKGROUP, testDocumentId);
            documentService.saveDocument(createdDoc);

            //
            // create Interim Deposit

            // create CashReceipts
            changeCurrentUser("INEFF");
            CashReceiptDocument cr1 = buildCashReceiptDoc(CMST_WORKGROUP, "CMST CR1", Constants.DocumentStatusCodes.CashReceipt.VERIFIED, new KualiDecimal("25.00"), KualiDecimal.ZERO);
            CashReceiptDocument cr2 = buildCashReceiptDoc(CMST_WORKGROUP, "CMST CR2", Constants.DocumentStatusCodes.CashReceipt.VERIFIED, KualiDecimal.ZERO, new KualiDecimal("25.00"));
            CashReceiptDocument cr3 = buildCashReceiptDoc(CMST_WORKGROUP, "CMST CR3", Constants.DocumentStatusCodes.CashReceipt.VERIFIED, new KualiDecimal("27.00"), new KualiDecimal("23.00"));

            List crList = new ArrayList();
            crList.add(cr1);
            crList.add(cr2);
            crList.add(cr3);

            // add interim deposit
            changeCurrentUser("KHUNTLEY");
            CashManagementDocument interimDoc = (CashManagementDocument) documentService.getByDocumentHeaderId(testDocumentId);
            cashManagementService.addDeposit(interimDoc, VALID_DEPOSIT_TICKET, lookupBankAccount(), crList, false);


            //
            // verify addition
            CashManagementDocument depositedDoc = (CashManagementDocument) documentService.getByDocumentHeaderId(testDocumentId);
            {

                // 1 deposit in document
                List deposits = depositedDoc.getDeposits();
                assertEquals(1, deposits.size());

                // deposit exists in database
                Map depositPK = new HashMap();
                depositPK.put("financialDocumentNumber", testDocumentId);
                depositPK.put("financialDocumentDepositLineNumber", new Integer(0));

                assertEquals(1, businessObjectService.countMatching(Deposit.class, depositPK));

                // deposit contains 3 CRs
                Deposit deposit = depositedDoc.getDeposit(0);
                List depositedReceiptControls = deposit.getDepositCashReceiptControl();
                assertEquals(3, depositedReceiptControls.size());

                // CRs are in appropriate state
                assertEquals(Constants.DocumentStatusCodes.CashReceipt.INTERIM, lookupCR(cr1.getFinancialDocumentNumber()).getDocumentHeader().getFinancialDocumentStatusCode());
                assertEquals(Constants.DocumentStatusCodes.CashReceipt.INTERIM, lookupCR(cr2.getFinancialDocumentNumber()).getDocumentHeader().getFinancialDocumentStatusCode());
                assertEquals(Constants.DocumentStatusCodes.CashReceipt.INTERIM, lookupCR(cr3.getFinancialDocumentNumber()).getDocumentHeader().getFinancialDocumentStatusCode());
            }

            //
            // cancel document
            documentService.cancelDocument(depositedDoc, "testing CMS.cancel");

            DocumentWorkflowStatusMonitor m = new DocumentWorkflowStatusMonitor(documentService, testDocumentId, "X");
            assertTrue(ChangeMonitor.waitUntilChange(m, 300, 5));


            {
                //
                // verify cancellation
                CashManagementDocument postCanceledDoc = (CashManagementDocument) documentService.getByDocumentHeaderId(testDocumentId);


                // document state is canceled
                assertEquals("X", postCanceledDoc.getDocumentHeader().getWorkflowDocument().getRouteHeader().getDocRouteStatus());


                // 0 deposits in document
                List deposits = postCanceledDoc.getDeposits();
                assertEquals(0, deposits.size());

                // deposit doesn't exist in database
                Map depositPK = new HashMap();
                depositPK.put("financialDocumentNumber", testDocumentId);
                depositPK.put("financialDocumentDepositLineNumber", new Integer(0));

                assertEquals(0, businessObjectService.countMatching(Deposit.class, depositPK));

                // cash receipts have been restored to appropriate state
                assertEquals(Constants.DocumentStatusCodes.CashReceipt.VERIFIED, lookupCR(cr1.getFinancialDocumentNumber()).getDocumentHeader().getFinancialDocumentStatusCode());
                assertEquals(Constants.DocumentStatusCodes.CashReceipt.VERIFIED, lookupCR(cr2.getFinancialDocumentNumber()).getDocumentHeader().getFinancialDocumentStatusCode());
                assertEquals(Constants.DocumentStatusCodes.CashReceipt.VERIFIED, lookupCR(cr3.getFinancialDocumentNumber()).getDocumentHeader().getFinancialDocumentStatusCode());
            }
        }
        finally {
            // cancel CMDoc
            cleanupCancel(testDocumentId);

            // clean up CRdoc
            denatureCashReceipts(CMST_WORKGROUP);

            // delete the cashDrawer which was created as a side-effect above
            deleteIfExists(CMST_WORKGROUP);
        }
    }


    private static final String VALID_DEPOSIT_TICKET = "0 0 0 destruct 0";

    final public void testAddInterimDeposit_nullDoc() throws Exception {
        boolean failedAsExpected = false;

        try {
            cashManagementService.addDeposit(null, VALID_DEPOSIT_TICKET, lookupBankAccount(), null, false);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    final public void testAddInterimDeposit_nullCashReceiptList() throws Exception {
        boolean failedAsExpected = false;

        String docId = null;
        try {
            CashManagementDocument createdDoc = cashManagementService.createCashManagementDocument(CMST_WORKGROUP, "CMST_testAddID_nCRL", null);
            docId = createdDoc.getFinancialDocumentNumber();

            cashDrawerService.openCashDrawer(CMST_WORKGROUP, docId);
            documentService.saveDocument(createdDoc);

            cashManagementService.addDeposit(createdDoc, VALID_DEPOSIT_TICKET, lookupBankAccount(), null, false);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }
        finally {
            if (docId != null) {
                Document testDoc = documentService.getByDocumentHeaderId(docId);
                documentService.cancelDocument(testDoc, "CMST cleanup");
            }

            // delete the cashDrawer which was created as a side-effect above
            deleteIfExists(CMST_WORKGROUP);
        }

        assertTrue(failedAsExpected);
    }

    final public void testAddInterimDeposit_emptyCashReceiptList() throws Exception {
        boolean failedAsExpected = false;

        String docId = null;
        try {
            CashManagementDocument createdDoc = cashManagementService.createCashManagementDocument(CMST_WORKGROUP, "CMST_testAddID_eCRL", null);
            docId = createdDoc.getFinancialDocumentNumber();

            cashDrawerService.openCashDrawer(CMST_WORKGROUP, docId);
            documentService.saveDocument(createdDoc);

            cashManagementService.addDeposit(createdDoc, VALID_DEPOSIT_TICKET, lookupBankAccount(), new ArrayList(), false);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }
        finally {
            if (docId != null) {
                Document testDoc = documentService.getByDocumentHeaderId(docId);
                documentService.cancelDocument(testDoc, "CMST cleanup");
            }

            // delete the cashDrawer which was created as a side-effect above
            deleteIfExists(CMST_WORKGROUP);
        }

        assertTrue(failedAsExpected);
    }

    final public void testAddInterimDeposit_nullBank() throws Exception {
        boolean failedAsExpected = false;

        String docId = null;
        try {
            CashManagementDocument createdDoc = cashManagementService.createCashManagementDocument(CMST_WORKGROUP, "CMST_testAddID_eCRL", null);
            docId = createdDoc.getFinancialDocumentNumber();

            cashDrawerService.openCashDrawer(CMST_WORKGROUP, docId);
            documentService.saveDocument(createdDoc);

            cashManagementService.addDeposit(createdDoc, VALID_DEPOSIT_TICKET, null, new ArrayList(), false);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }
        finally {
            if (docId != null) {
                Document testDoc = documentService.getByDocumentHeaderId(docId);
                documentService.cancelDocument(testDoc, "CMST cleanup");
            }

            // delete the cashDrawer which was created as a side-effect above
            deleteIfExists(CMST_WORKGROUP);
        }

        assertTrue(failedAsExpected);
    }

    final public void testAddInterimDeposit_nonverifiedCashReceipt() throws Exception {
        boolean failedAsExpected = false;

        String testDocumentId = null;

        try {
            //
            // create a valid, empty CashManagementDocument
            deleteIfExists(CMST_WORKGROUP);
            CashDrawer preDocCD = cashDrawerService.getByWorkgroupName(CMST_WORKGROUP, true);
            assertTrue(preDocCD.isClosed());

            CashManagementDocument createdDoc = cashManagementService.createCashManagementDocument(CMST_WORKGROUP, "CMST_testAddID_nonverified", null);
            testDocumentId = createdDoc.getFinancialDocumentNumber();

            cashDrawerService.openCashDrawer(CMST_WORKGROUP, testDocumentId);
            documentService.saveDocument(createdDoc);

            // retrieve the document, for future use
            CashManagementDocument retrievedDoc = (CashManagementDocument) documentService.getByDocumentHeaderId(testDocumentId);

            //
            // create Interim Deposit

            // create CashReceipt
            changeCurrentUser("INEFF");
            CashReceiptDocument cr = buildCashReceiptDoc(CMST_WORKGROUP, "CMST nonverified CR", Constants.DocumentStatusCodes.CashReceipt.INTERIM, new KualiDecimal("25.00"), new KualiDecimal("75.00"));
            changeCurrentUser("KHUNTLEY");

            List crList = new ArrayList();
            crList.add(cr);

            // add invalid interim deposit
            cashManagementService.addDeposit(retrievedDoc, VALID_DEPOSIT_TICKET, lookupBankAccount(), crList, false);
        }
        catch (InvalidCashReceiptState e) {
            failedAsExpected = true;
        }
        finally {
            // cancel CMDoc
            cleanupCancel(testDocumentId);

            // clean up CRdoc
            denatureCashReceipts(CMST_WORKGROUP);

            // delete the cashDrawer which was created as a side-effect above
            deleteIfExists(CMST_WORKGROUP);
        }

        assertTrue(failedAsExpected);
    }

    final public void testAddInterimDeposit_unsavedCMDoc() throws Exception {
        boolean failedAsExpected = false;

        String testDocumentId = null;

        try {
            //
            // create a valid, empty CashManagementDocument
            deleteIfExists(CMST_WORKGROUP);
            CashDrawer preDocCD = cashDrawerService.getByWorkgroupName(CMST_WORKGROUP, true);
            assertTrue(preDocCD.isClosed());

            changeCurrentUser("KHUNTLEY");
            CashManagementDocument createdDoc = cashManagementService.createCashManagementDocument(CMST_WORKGROUP, "CMST_testAddID_nonverified", null);
            testDocumentId = createdDoc.getFinancialDocumentNumber();

            //
            // create Interim Deposit

            // create CashReceipt
            changeCurrentUser("INEFF");
            CashReceiptDocument cr = buildCashReceiptDoc(CMST_WORKGROUP, "CMST noncheck CR", Constants.DocumentStatusCodes.CashReceipt.VERIFIED, new KualiDecimal("25.00"), KualiDecimal.ZERO);
            changeCurrentUser("KHUNTLEY");

            List crList = new ArrayList();
            crList.add(cr);

            // add interim deposit
            cashManagementService.addDeposit(createdDoc, VALID_DEPOSIT_TICKET, lookupBankAccount(), crList, false);
        }
        catch (IllegalStateException e) {
            failedAsExpected = true;
        }
        finally {
            // clean up CRdoc
            denatureCashReceipts(CMST_WORKGROUP);

            // delete the cashDrawer which was created as a side-effect above
            deleteIfExists(CMST_WORKGROUP);
        }

        assertTrue(failedAsExpected);
    }


    final public void testAddInterimDeposit_valid() throws Exception {
        String testDocumentId = null;

        try {
            //
            // create a valid, empty CashManagementDocument
            deleteIfExists(CMST_WORKGROUP);
            CashDrawer preDocCD = cashDrawerService.getByWorkgroupName(CMST_WORKGROUP, true);
            assertTrue(preDocCD.isClosed());

            CashManagementDocument createdDoc = cashManagementService.createCashManagementDocument(CMST_WORKGROUP, "CMST_testAddID_valid", null);
            testDocumentId = createdDoc.getFinancialDocumentNumber();

            // save it
            cashDrawerService.openCashDrawer(CMST_WORKGROUP, testDocumentId);
            documentService.saveDocument(createdDoc);

            // retrieve the document, for future use
            CashManagementDocument retrievedDoc = (CashManagementDocument) documentService.getByDocumentHeaderId(testDocumentId);


            //
            // create Interim Deposit

            // create CashReceipts
            changeCurrentUser("INEFF");
            CashReceiptDocument cr1 = buildCashReceiptDoc(CMST_WORKGROUP, "CMST CR1", Constants.DocumentStatusCodes.CashReceipt.VERIFIED, new KualiDecimal("25.00"), KualiDecimal.ZERO);
            CashReceiptDocument cr2 = buildCashReceiptDoc(CMST_WORKGROUP, "CMST CR2", Constants.DocumentStatusCodes.CashReceipt.VERIFIED, KualiDecimal.ZERO, new KualiDecimal("25.00"));
            CashReceiptDocument cr3 = buildCashReceiptDoc(CMST_WORKGROUP, "CMST CR3", Constants.DocumentStatusCodes.CashReceipt.VERIFIED, new KualiDecimal("27.00"), new KualiDecimal("23.00"));

            List crList = new ArrayList();
            crList.add(cr1);
            crList.add(cr2);
            crList.add(cr3);

            // add interim deposit
            changeCurrentUser("KHUNTLEY");
            cashManagementService.addDeposit(retrievedDoc, VALID_DEPOSIT_TICKET, lookupBankAccount(), crList, false);


            //
            // validate results
            CashManagementDocument depositedDoc = (CashManagementDocument) documentService.getByDocumentHeaderId(testDocumentId);

            // 1 deposit
            List deposits = depositedDoc.getDeposits();
            assertEquals(1, deposits.size());

            // deposit exists in database
            Map depositPK = new HashMap();
            depositPK.put("financialDocumentNumber", testDocumentId);
            depositPK.put("financialDocumentDepositLineNumber", new Integer(0));

            assertEquals(1, businessObjectService.countMatching(Deposit.class, depositPK));

            // deposit is interim, not final
            Deposit deposit = (Deposit) deposits.get(0);
            assertEquals(Constants.DepositConstants.DEPOSIT_TYPE_INTERIM, deposit.getDepositTypeCode());

            // deposit contains 3 CRs
            List depositedReceiptControls = deposit.getDepositCashReceiptControl();
            assertEquals(3, depositedReceiptControls.size());

            // CRs are in appropriate state
            assertEquals(Constants.DocumentStatusCodes.CashReceipt.INTERIM, lookupCR(cr1.getFinancialDocumentNumber()).getDocumentHeader().getFinancialDocumentStatusCode());
            assertEquals(Constants.DocumentStatusCodes.CashReceipt.INTERIM, lookupCR(cr2.getFinancialDocumentNumber()).getDocumentHeader().getFinancialDocumentStatusCode());
            assertEquals(Constants.DocumentStatusCodes.CashReceipt.INTERIM, lookupCR(cr3.getFinancialDocumentNumber()).getDocumentHeader().getFinancialDocumentStatusCode());

            // total value of the deposit is the sum of the values of the 3 CRs
            assertEquals(new KualiDecimal("100.00"), deposit.getDepositAmount());
        }
        finally {
            // cancel CMDoc
            cleanupCancel(testDocumentId);

            // clean up CRdoc
            denatureCashReceipts(CMST_WORKGROUP);

            // delete the cashDrawer which was created as a side-effect above
            deleteIfExists(CMST_WORKGROUP);
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


    public void testCancelDeposit_cancelSingleInterim() throws Exception {
        String testDocumentId = null;

        try {
            //
            // create a valid, empty CashManagementDocument
            deleteIfExists(CMST_WORKGROUP);
            CashDrawer preDocCD = cashDrawerService.getByWorkgroupName(CMST_WORKGROUP, true);
            assertTrue(preDocCD.isClosed());

            CashManagementDocument createdDoc = cashManagementService.createCashManagementDocument(CMST_WORKGROUP, "CMST_testAddID_valid", null);
            testDocumentId = createdDoc.getFinancialDocumentNumber();

            // save it
            cashDrawerService.openCashDrawer(CMST_WORKGROUP, testDocumentId);
            documentService.saveDocument(createdDoc);

            //
            // add Interim Deposit

            // create CashReceipts
            changeCurrentUser("INEFF");
            CashReceiptDocument cr1 = buildCashReceiptDoc(CMST_WORKGROUP, "CMST CR1", Constants.DocumentStatusCodes.CashReceipt.VERIFIED, new KualiDecimal("25.00"), KualiDecimal.ZERO);
            CashReceiptDocument cr2 = buildCashReceiptDoc(CMST_WORKGROUP, "CMST CR2", Constants.DocumentStatusCodes.CashReceipt.VERIFIED, KualiDecimal.ZERO, new KualiDecimal("25.00"));
            CashReceiptDocument cr3 = buildCashReceiptDoc(CMST_WORKGROUP, "CMST CR3", Constants.DocumentStatusCodes.CashReceipt.VERIFIED, new KualiDecimal("27.00"), new KualiDecimal("23.00"));

            List crList = new ArrayList();
            crList.add(cr1);
            crList.add(cr2);
            crList.add(cr3);

            // add interim deposit
            changeCurrentUser("KHUNTLEY");
            CashManagementDocument interimDoc = (CashManagementDocument) documentService.getByDocumentHeaderId(testDocumentId);
            cashManagementService.addDeposit(interimDoc, VALID_DEPOSIT_TICKET, lookupBankAccount(), crList, false);


            //
            // verify addition

            CashManagementDocument depositedDoc = (CashManagementDocument) documentService.getByDocumentHeaderId(testDocumentId);
            {
                // 1 deposit in document
                List deposits = depositedDoc.getDeposits();
                assertEquals(1, deposits.size());

                // deposit exists in database
                Map depositPK = new HashMap();
                depositPK.put("financialDocumentNumber", testDocumentId);
                depositPK.put("financialDocumentDepositLineNumber", new Integer(0));

                assertEquals(1, businessObjectService.countMatching(Deposit.class, depositPK));

                // deposit contains 3 CRs
                Deposit deposit = depositedDoc.getDeposit(0);
                List depositedReceiptControls = deposit.getDepositCashReceiptControl();
                assertEquals(3, depositedReceiptControls.size());

                // CRs are in appropriate state
                assertEquals(Constants.DocumentStatusCodes.CashReceipt.INTERIM, lookupCR(cr1.getFinancialDocumentNumber()).getDocumentHeader().getFinancialDocumentStatusCode());
                assertEquals(Constants.DocumentStatusCodes.CashReceipt.INTERIM, lookupCR(cr2.getFinancialDocumentNumber()).getDocumentHeader().getFinancialDocumentStatusCode());
                assertEquals(Constants.DocumentStatusCodes.CashReceipt.INTERIM, lookupCR(cr3.getFinancialDocumentNumber()).getDocumentHeader().getFinancialDocumentStatusCode());
            }


            {
                //
                // cancel deposit
                Deposit deposit = depositedDoc.getDeposit(0);
                cashManagementService.cancelDeposit(deposit);

                //
                // verify cancellation
                CashManagementDocument postCanceledDoc = (CashManagementDocument) documentService.getByDocumentHeaderId(testDocumentId);

                // 0 deposits in document
                List deposits = postCanceledDoc.getDeposits();
                assertEquals(0, deposits.size());

                // deposit doesn't exist in database
                Map depositPK = new HashMap();
                depositPK.put("financialDocumentNumber", testDocumentId);
                depositPK.put("financialDocumentDepositLineNumber", new Integer(0));

                assertEquals(0, businessObjectService.countMatching(Deposit.class, depositPK));

                // cash receipts have been restored to appropriate state
                assertEquals(Constants.DocumentStatusCodes.CashReceipt.VERIFIED, lookupCR(cr1.getFinancialDocumentNumber()).getDocumentHeader().getFinancialDocumentStatusCode());
                assertEquals(Constants.DocumentStatusCodes.CashReceipt.VERIFIED, lookupCR(cr2.getFinancialDocumentNumber()).getDocumentHeader().getFinancialDocumentStatusCode());
                assertEquals(Constants.DocumentStatusCodes.CashReceipt.VERIFIED, lookupCR(cr3.getFinancialDocumentNumber()).getDocumentHeader().getFinancialDocumentStatusCode());
            }
        }
        finally {
            // cancel CMDoc
            cleanupCancel(testDocumentId);

            // clean up CRdoc
            denatureCashReceipts(CMST_WORKGROUP);

            // delete the cashDrawer which was created as a side-effect above
            deleteIfExists(CMST_WORKGROUP);
        }
    }


    public void testKULEDOCS_1475_nullDocument() {
        try {
            // open the Cash Drawer for a null documentId
            deleteIfExists(CMST_WORKGROUP);

            CashDrawer forcedOpen = cashDrawerService.getByWorkgroupName(CMST_WORKGROUP, true);
            forcedOpen.setStatusCode(Constants.CashDrawerConstants.STATUS_OPEN);
            forcedOpen.setReferenceFinancialDocumentNumber(null);
            businessObjectService.save(forcedOpen);

            // try create a new CM doc
            CashManagementDocument createdDoc = cashManagementService.createCashManagementDocument(CMST_WORKGROUP, "CMST_testAddID_valid", null);
            assertNotNull(createdDoc);
        }
        finally {
            deleteIfExists(CMST_WORKGROUP);
        }
    }

    public void testKULEDOCS_1475_nonexistentDocument() {
        try {
            // open the Cash Drawer for a nonexistent documentId
            deleteIfExists(CMST_WORKGROUP);

            CashDrawer forcedLocked = cashDrawerService.getByWorkgroupName(CMST_WORKGROUP, true);
            forcedLocked.setStatusCode(Constants.CashDrawerConstants.STATUS_LOCKED);
            forcedLocked.setReferenceFinancialDocumentNumber("0");
            businessObjectService.save(forcedLocked);

            // try create a new CM doc
            CashManagementDocument createdDoc = cashManagementService.createCashManagementDocument(CMST_WORKGROUP, "CMST_testAddID_valid", null);
            assertNotNull(createdDoc);
        }
        finally {
            deleteIfExists(CMST_WORKGROUP);
        }
    }

    public void testKULEDOCS_1475_existentDocument() throws Exception {
        boolean failedAsExpected = false;

        String testDocumentId = null;
        try {

            //
            // create a valid, empty CashManagementDocument
            deleteIfExists(CMST_WORKGROUP);
            CashDrawer preDocCD = cashDrawerService.getByWorkgroupName(CMST_WORKGROUP, true);
            assertTrue(preDocCD.isClosed());

            CashManagementDocument createdDoc = cashManagementService.createCashManagementDocument(CMST_WORKGROUP, "CMST_testAddID_valid", null);
            testDocumentId = createdDoc.getFinancialDocumentNumber();

            // save it
            cashDrawerService.openCashDrawer(CMST_WORKGROUP, testDocumentId);
            documentService.saveDocument(createdDoc);

            // try create a new CM doc
            cashManagementService.createCashManagementDocument(CMST_WORKGROUP, "CMST_testAddID_valid", null);
        }
        catch (CashDrawerStateException e) {
            failedAsExpected = true;
        }
        finally {
            // cancel CMDoc
            cleanupCancel(testDocumentId);

            deleteIfExists(CMST_WORKGROUP);
        }

        assertTrue(failedAsExpected);
    }


    private CashReceiptDocument lookupCR(String documentId) throws WorkflowException {
        CashReceiptDocument crDoc = (CashReceiptDocument) documentService.getByDocumentHeaderId(documentId);

        return crDoc;
    }


    private void deleteIfExists(String workgroupName) {
        Map deleteCriteria = new HashMap();
        deleteCriteria.put("workgroupName", workgroupName);
        businessObjectService.deleteMatching(CashDrawer.class, deleteCriteria);
    }

    private static final String[] BOTH_STATII = { Constants.DocumentStatusCodes.CashReceipt.VERIFIED, Constants.DocumentStatusCodes.CashReceipt.INTERIM };

    private void denatureCashReceipts(String workgroupName) {
        List verifiedReceipts = cashReceiptService.getCashReceipts(workgroupName, BOTH_STATII);

        for (Iterator i = verifiedReceipts.iterator(); i.hasNext();) {
            CashReceiptDocument receipt = (CashReceiptDocument) i.next();
            receipt.getDocumentHeader().setFinancialDocumentStatusCode("Z");
            documentService.updateDocument(receipt);
        }
    }

    private CashReceiptDocument buildCashReceiptDoc(String workgroupName, String description, String status, KualiDecimal cashAmount, KualiDecimal checkAmount) throws WorkflowException {
        CashReceiptDocument crDoc = (CashReceiptDocument) documentService.getNewDocument(CashReceiptDocument.class);

        crDoc.getDocumentHeader().setFinancialDocumentDescription(description);
        crDoc.getDocumentHeader().setFinancialDocumentStatusCode(status);

        crDoc.setCheckEntryMode(CashReceiptDocument.CHECK_ENTRY_TOTAL);
        crDoc.setTotalCashAmount(cashAmount);
        crDoc.setTotalCheckAmount(checkAmount);

        crDoc.setCampusLocationCode(cashReceiptService.getCampusCodeForCashReceiptVerificationUnit(workgroupName));

        documentService.saveDocument(crDoc);

        CashReceiptDocument persistedDoc = (CashReceiptDocument) documentService.getByDocumentHeaderId(crDoc.getFinancialDocumentNumber());
        return persistedDoc;
    }

    private BankAccount lookupBankAccount() {
        Map keyMap = new HashMap();
        keyMap.put("financialDocumentBankCode", "TEST");
        keyMap.put("finDocumentBankAccountNumber", "1111");

        BankAccount bankAccount = (BankAccount) businessObjectService.findByPrimaryKey(BankAccount.class, keyMap);

        return bankAccount;
    }

    private void cleanupCancel(String documentId) throws WorkflowException {
        if (documentId != null) {
            Document testDoc = documentService.getByDocumentHeaderId(documentId);

            if (!testDoc.getDocumentHeader().getWorkflowDocument().stateIsCanceled()) {
                documentService.cancelDocument(testDoc, "CMST cleanup cancel");
            }
        }
    }
}
