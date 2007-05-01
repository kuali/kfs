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
package org.kuali.module.financial.service;

import static org.kuali.kfs.util.SpringServiceLocator.getCashDrawerService;
import static org.kuali.kfs.util.SpringServiceLocator.getCashManagementService;
import static org.kuali.kfs.util.SpringServiceLocator.getCashReceiptService;
import static org.kuali.rice.KNSServiceLocator.getBusinessObjectService;
import static org.kuali.rice.KNSServiceLocator.getDocumentService;
import static org.kuali.test.fixtures.UserNameFixture.KHUNTLEY;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.core.document.Document;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.exceptions.ValidationException;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.financial.bo.BankAccount;
import org.kuali.module.financial.bo.CashDrawer;
import org.kuali.module.financial.bo.Deposit;
import org.kuali.module.financial.document.CashManagementDocument;
import org.kuali.module.financial.document.CashReceiptDocument;
import org.kuali.module.financial.exceptions.CashDrawerStateException;
import org.kuali.module.financial.exceptions.InvalidCashReceiptState;
import org.kuali.module.financial.util.CashReceiptFamilyTestUtil;
import org.kuali.test.KualiTestBase;
import org.kuali.test.TestsWorkflowViaDatabase;
import org.kuali.test.WithTestSpringContext;
import org.kuali.test.fixtures.UserNameFixture;
import org.kuali.test.monitor.ChangeMonitor;
import org.kuali.test.monitor.DocumentWorkflowStatusMonitor;

import edu.iu.uis.eden.exception.WorkflowException;

@WithTestSpringContext(session = KHUNTLEY)
public class CashManagementServiceTest extends KualiTestBase {
    static final String CMST_WORKGROUP = "CashManagementServiceTest";
    
    final public void testCreateCashManagementDocument_blankUnitName() throws Exception {
        boolean failedAsExpected = false;

        try {
            getCashManagementService().createCashManagementDocument("    ", "foo", "cmst1");
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    final public void testCreateCashManagementDocument_blankDocDescription() throws Exception {
        boolean failedAsExpected = false;

        try {
            getCashManagementService().createCashManagementDocument(CMST_WORKGROUP, null, "cmst2");
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    @TestsWorkflowViaDatabase
    final public void testCreateCashManagementDocument_valid() throws Exception {
        String testDocumentId = null;

        try {
            deleteIfExists(CMST_WORKGROUP);
            CashDrawer preDocCD = getCashDrawerService().getByWorkgroupName(CMST_WORKGROUP, true);
            assertTrue(preDocCD.isClosed());

            CashManagementDocument createdDoc = getCashManagementService().createCashManagementDocument(CMST_WORKGROUP, "CMST_testCreate_valid", "cmst3");
            assertNotNull(createdDoc);
            testDocumentId = createdDoc.getDocumentNumber();

            // save it separately
            getCashDrawerService().openCashDrawer(CMST_WORKGROUP, testDocumentId);
            saveDocument(createdDoc);

            // verify that the doc was saved
            CashManagementDocument retrievedDoc = (CashManagementDocument) getDocumentService().getByDocumentHeaderId(testDocumentId);
            assertEquals("S", retrievedDoc.getDocumentHeader().getWorkflowDocument().getRouteHeader().getDocRouteStatus());
        }
        finally {
            // cancel the document
            cleanupCancel(testDocumentId);

            // delete the cashDrawer which was created as a side-effect above
            deleteIfExists(CMST_WORKGROUP);
        }
    }

    @TestsWorkflowViaDatabase
    final public void testCreateCashManagementDocument_cashDrawerAlreadyOpen() throws Exception {
        
        String testDocumentId = null;
        try {
            deleteIfExists(CMST_WORKGROUP);
            CashDrawer preDocCD = getCashDrawerService().getByWorkgroupName(CMST_WORKGROUP, true);
            assertTrue(preDocCD.isClosed());

            CashManagementDocument createdDoc = getCashManagementService().createCashManagementDocument(CMST_WORKGROUP, "CMST_testCreate_cashDrawerAlreadyOpen", "cmst3");
            assertNotNull(createdDoc);
            testDocumentId = createdDoc.getDocumentNumber();
            
            // force the drawer open
            getCashDrawerService().openCashDrawer(CMST_WORKGROUP, testDocumentId);
            saveDocument(createdDoc);

            boolean failedAsExpected = false;
            // fail creating the document since the CashDrawer is already open
            try {
                getCashManagementService().createCashManagementDocument(CMST_WORKGROUP, "CMST_testCreate_validCollision 2", "cmst5");
                fail("created a CMD while the CashDrawer is already open");
            }
            catch (CashDrawerStateException e) {
                // good
                failedAsExpected = true;
            }
            
            assertEquals(failedAsExpected, true);
            
            //
            // cancel empty CMDoc
            //
            getCashManagementService().cancelCashManagementDocument(createdDoc);
            
        }
        finally {
            // cancel the document
            cleanupCancel(testDocumentId);
            
            // delete the cashDrawer you created
            deleteIfExists(CMST_WORKGROUP);
        }
    }

    @TestsWorkflowViaDatabase
    final public void testCancelCashManagementDocument_validEmpty() throws Exception {
        String testDocumentId = null;

        try {
            //
            // create empty CMDoc
            //
            deleteIfExists(CMST_WORKGROUP);
            CashDrawer preDocCD = getCashDrawerService().getByWorkgroupName(CMST_WORKGROUP, false);
            assertNull(preDocCD);

            CashManagementDocument createdDoc = getCashManagementService().createCashManagementDocument(CMST_WORKGROUP, "CMST_testCreate_valid", "cmst3");
            assertNotNull(createdDoc);
            testDocumentId = createdDoc.getDocumentNumber();

            // save it separately
            getCashDrawerService().openCashDrawer(CMST_WORKGROUP, testDocumentId);
            saveDocument(createdDoc);

            // verify it actually got saved
            CashManagementDocument retrievedDoc = (CashManagementDocument) getDocumentService().getByDocumentHeaderId(testDocumentId);
            assertEquals("S", retrievedDoc.getDocumentHeader().getWorkflowDocument().getRouteHeader().getDocRouteStatus());


            //
            // cancel empty CMDoc
            //
            getCashManagementService().cancelCashManagementDocument(createdDoc);

            // verify that the cancellation closed the cash drawer
            CashDrawer postCancelCD = getCashDrawerService().getByWorkgroupName(CMST_WORKGROUP, false);
            assertNotNull(postCancelCD);
            assertEquals(postCancelCD.getStatusCode(), KFSConstants.CashDrawerConstants.STATUS_CLOSED);
        }
        finally {
            // cancel the document
            cleanupCancel(testDocumentId);

            // delete the cashDrawer which was created as a side-effect above
            deleteIfExists(CMST_WORKGROUP);
        }
    }


    @TestsWorkflowViaDatabase
    final public void testCancelCashManagementDocument_valid_interimOnly() throws Exception {
        String testDocumentId = null;

        try {
            //
            // create a valid, empty CashManagementDocument
            deleteIfExists(CMST_WORKGROUP);
            CashDrawer preDocCD = getCashDrawerService().getByWorkgroupName(CMST_WORKGROUP, true);
            assertTrue(preDocCD.isClosed());

            CashManagementDocument createdDoc = getCashManagementService().createCashManagementDocument(CMST_WORKGROUP, "CMST_testAddID_valid", null);
            testDocumentId = createdDoc.getDocumentNumber();

            // save it separately
            getCashDrawerService().openCashDrawer(CMST_WORKGROUP, testDocumentId);
            saveDocument(createdDoc);

            //
            // create Interim Deposit

            // create CashReceipts
            changeCurrentUser(UserNameFixture.INEFF);
            CashReceiptDocument cr1 = buildCashReceiptDoc(CMST_WORKGROUP, "CMST CR1", KFSConstants.DocumentStatusCodes.CashReceipt.VERIFIED, new KualiDecimal("25.00"), KualiDecimal.ZERO);
            CashReceiptDocument cr2 = buildCashReceiptDoc(CMST_WORKGROUP, "CMST CR2", KFSConstants.DocumentStatusCodes.CashReceipt.VERIFIED, KualiDecimal.ZERO, new KualiDecimal("25.00"));
            CashReceiptDocument cr3 = buildCashReceiptDoc(CMST_WORKGROUP, "CMST CR3", KFSConstants.DocumentStatusCodes.CashReceipt.VERIFIED, new KualiDecimal("27.00"), new KualiDecimal("23.00"));

            List crList = new ArrayList();
            crList.add(cr1);
            crList.add(cr2);
            crList.add(cr3);

            // add interim deposit
            changeCurrentUser(KHUNTLEY);
            CashManagementDocument interimDoc = (CashManagementDocument) getDocumentService().getByDocumentHeaderId(testDocumentId);
            getCashManagementService().addDeposit(interimDoc, VALID_DEPOSIT_TICKET, lookupBankAccount(), crList, false);


            //
            // verify addition
            CashManagementDocument depositedDoc = (CashManagementDocument) getDocumentService().getByDocumentHeaderId(testDocumentId);
            {

                // 1 deposit in document
                List deposits = depositedDoc.getDeposits();
                assertEquals(1, deposits.size());

                // deposit exists in database
                Map depositPK = new HashMap();
                depositPK.put(KFSPropertyConstants.DOCUMENT_NUMBER, testDocumentId);
                depositPK.put("financialDocumentDepositLineNumber", new Integer(0));

                assertEquals(1, getBusinessObjectService().countMatching(Deposit.class, depositPK));

                // deposit contains 3 CRs
                Deposit deposit = depositedDoc.getDeposit(0);
                List depositedReceiptControls = deposit.getDepositCashReceiptControl();
                assertEquals(3, depositedReceiptControls.size());

                // CRs are in appropriate state
                assertEquals(KFSConstants.DocumentStatusCodes.CashReceipt.INTERIM, lookupCR(cr1.getDocumentNumber()).getDocumentHeader().getFinancialDocumentStatusCode());
                assertEquals(KFSConstants.DocumentStatusCodes.CashReceipt.INTERIM, lookupCR(cr2.getDocumentNumber()).getDocumentHeader().getFinancialDocumentStatusCode());
                assertEquals(KFSConstants.DocumentStatusCodes.CashReceipt.INTERIM, lookupCR(cr3.getDocumentNumber()).getDocumentHeader().getFinancialDocumentStatusCode());
            }

            //
            // cancel document
            getDocumentService().cancelDocument(depositedDoc, "testing CMS.cancel");

            DocumentWorkflowStatusMonitor m = new DocumentWorkflowStatusMonitor(getDocumentService(), testDocumentId, "X");
            assertTrue(ChangeMonitor.waitUntilChange(m, 300, 5));


            {
                //
                // verify cancellation
                CashManagementDocument postCanceledDoc = (CashManagementDocument) getDocumentService().getByDocumentHeaderId(testDocumentId);


                // document state is canceled
                assertEquals("X", postCanceledDoc.getDocumentHeader().getWorkflowDocument().getRouteHeader().getDocRouteStatus());


                // 0 deposits in document
                List deposits = postCanceledDoc.getDeposits();
                assertEquals(0, deposits.size());

                // deposit doesn't exist in database
                Map depositPK = new HashMap();
                depositPK.put(KFSPropertyConstants.DOCUMENT_NUMBER, testDocumentId);
                depositPK.put("financialDocumentDepositLineNumber", new Integer(0));

                assertEquals(0, getBusinessObjectService().countMatching(Deposit.class, depositPK));

                // cash receipts have been restored to appropriate state
                assertEquals(KFSConstants.DocumentStatusCodes.CashReceipt.VERIFIED, lookupCR(cr1.getDocumentNumber()).getDocumentHeader().getFinancialDocumentStatusCode());
                assertEquals(KFSConstants.DocumentStatusCodes.CashReceipt.VERIFIED, lookupCR(cr2.getDocumentNumber()).getDocumentHeader().getFinancialDocumentStatusCode());
                assertEquals(KFSConstants.DocumentStatusCodes.CashReceipt.VERIFIED, lookupCR(cr3.getDocumentNumber()).getDocumentHeader().getFinancialDocumentStatusCode());
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
            getCashManagementService().addDeposit(null, VALID_DEPOSIT_TICKET, lookupBankAccount(), null, false);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    @TestsWorkflowViaDatabase
    final public void testAddInterimDeposit_nullCashReceiptList() throws Exception {
        boolean failedAsExpected = false;

        String docId = null;
        try {
            CashManagementDocument createdDoc = getCashManagementService().createCashManagementDocument(CMST_WORKGROUP, "CMST_testAddID_nCRL", null);
            docId = createdDoc.getDocumentNumber();

            getCashDrawerService().openCashDrawer(CMST_WORKGROUP, docId);
            saveDocument(createdDoc);

            getCashManagementService().addDeposit(createdDoc, VALID_DEPOSIT_TICKET, lookupBankAccount(), null, false);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }
        finally {
            if (docId != null) {
                Document testDoc = getDocumentService().getByDocumentHeaderId(docId);
                getDocumentService().cancelDocument(testDoc, "CMST cleanup");
            }

            // delete the cashDrawer which was created as a side-effect above
            deleteIfExists(CMST_WORKGROUP);
        }

        assertTrue(failedAsExpected);
    }

    @TestsWorkflowViaDatabase
    final public void testAddInterimDeposit_emptyCashReceiptList() throws Exception {
        boolean failedAsExpected = false;

        String docId = null;
        try {
            CashManagementDocument createdDoc = getCashManagementService().createCashManagementDocument(CMST_WORKGROUP, "CMST_testAddID_eCRL", null);
            docId = createdDoc.getDocumentNumber();

            getCashDrawerService().openCashDrawer(CMST_WORKGROUP, docId);
            saveDocument(createdDoc);

            getCashManagementService().addDeposit(createdDoc, VALID_DEPOSIT_TICKET, lookupBankAccount(), new ArrayList(), false);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }
        finally {
            if (docId != null) {
                Document testDoc = getDocumentService().getByDocumentHeaderId(docId);
                getDocumentService().cancelDocument(testDoc, "CMST cleanup");
            }

            // delete the cashDrawer which was created as a side-effect above
            deleteIfExists(CMST_WORKGROUP);
        }

        assertTrue(failedAsExpected);
    }

    @TestsWorkflowViaDatabase
    final public void testAddInterimDeposit_nullBank() throws Exception {
        boolean failedAsExpected = false;

        String docId = null;
        try {
            CashManagementDocument createdDoc = getCashManagementService().createCashManagementDocument(CMST_WORKGROUP, "CMST_testAddID_eCRL", null);
            docId = createdDoc.getDocumentNumber();

            getCashDrawerService().openCashDrawer(CMST_WORKGROUP, docId);
            saveDocument(createdDoc);

            getCashManagementService().addDeposit(createdDoc, VALID_DEPOSIT_TICKET, null, new ArrayList(), false);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }
        finally {
            if (docId != null) {
                Document testDoc = getDocumentService().getByDocumentHeaderId(docId);
                getDocumentService().cancelDocument(testDoc, "CMST cleanup");
            }

            // delete the cashDrawer which was created as a side-effect above
            deleteIfExists(CMST_WORKGROUP);
        }

        assertTrue(failedAsExpected);
    }

    @TestsWorkflowViaDatabase
    final public void testAddInterimDeposit_nonverifiedCashReceipt() throws Exception {
        boolean failedAsExpected = false;

        String testDocumentId = null;

        try {
            //
            // create a valid, empty CashManagementDocument
            deleteIfExists(CMST_WORKGROUP);
            CashDrawer preDocCD = getCashDrawerService().getByWorkgroupName(CMST_WORKGROUP, true);
            assertTrue(preDocCD.isClosed());

            CashManagementDocument createdDoc = getCashManagementService().createCashManagementDocument(CMST_WORKGROUP, "CMST_testAddID_nonverified", null);
            testDocumentId = createdDoc.getDocumentNumber();

            getCashDrawerService().openCashDrawer(CMST_WORKGROUP, testDocumentId);
            saveDocument(createdDoc);

            // retrieve the document, for future use
            CashManagementDocument retrievedDoc = (CashManagementDocument) getDocumentService().getByDocumentHeaderId(testDocumentId);

            //
            // create Interim Deposit

            // create CashReceipt
            changeCurrentUser(UserNameFixture.INEFF);
            CashReceiptDocument cr = buildCashReceiptDoc(CMST_WORKGROUP, "CMST nonverified CR", KFSConstants.DocumentStatusCodes.CashReceipt.INTERIM, new KualiDecimal("25.00"), new KualiDecimal("75.00"));
            changeCurrentUser(KHUNTLEY);

            List crList = new ArrayList();
            crList.add(cr);

            // add invalid interim deposit
            getCashManagementService().addDeposit(retrievedDoc, VALID_DEPOSIT_TICKET, lookupBankAccount(), crList, false);
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

    @TestsWorkflowViaDatabase
    final public void testAddInterimDeposit_unsavedCMDoc() throws Exception {
        boolean failedAsExpected = false;

        String testDocumentId = null;

        try {
            //
            // create a valid, empty CashManagementDocument
            deleteIfExists(CMST_WORKGROUP);
            CashDrawer preDocCD = getCashDrawerService().getByWorkgroupName(CMST_WORKGROUP, true);
            assertTrue(preDocCD.isClosed());

            changeCurrentUser(KHUNTLEY);
            CashManagementDocument createdDoc = getCashManagementService().createCashManagementDocument(CMST_WORKGROUP, "CMST_testAddID_nonverified", null);
            testDocumentId = createdDoc.getDocumentNumber();

            //
            // create Interim Deposit

            // create CashReceipt
            changeCurrentUser(UserNameFixture.INEFF);
            CashReceiptDocument cr = buildCashReceiptDoc(CMST_WORKGROUP, "CMST noncheck CR", KFSConstants.DocumentStatusCodes.CashReceipt.VERIFIED, new KualiDecimal("25.00"), KualiDecimal.ZERO);
            changeCurrentUser(KHUNTLEY);
            changeCurrentUser(KHUNTLEY);

            List crList = new ArrayList();
            crList.add(cr);

            // add interim deposit
            getCashManagementService().addDeposit(createdDoc, VALID_DEPOSIT_TICKET, lookupBankAccount(), crList, false);
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


    @TestsWorkflowViaDatabase
    final public void testAddInterimDeposit_valid() throws Exception {
        String testDocumentId = null;

        try {
            //
            // create a valid, empty CashManagementDocument
            deleteIfExists(CMST_WORKGROUP);
            CashDrawer preDocCD = getCashDrawerService().getByWorkgroupName(CMST_WORKGROUP, true);
            assertTrue(preDocCD.isClosed());

            CashManagementDocument createdDoc = getCashManagementService().createCashManagementDocument(CMST_WORKGROUP, "CMST_testAddID_valid", null);
            testDocumentId = createdDoc.getDocumentNumber();

            // save it
            getCashDrawerService().openCashDrawer(CMST_WORKGROUP, testDocumentId);
            saveDocument(createdDoc);

            // retrieve the document, for future use
            CashManagementDocument retrievedDoc = (CashManagementDocument) getDocumentService().getByDocumentHeaderId(testDocumentId);


            //
            // create Interim Deposit

            // create CashReceipts
            changeCurrentUser(UserNameFixture.INEFF);
            CashReceiptDocument cr1 = buildCashReceiptDoc(CMST_WORKGROUP, "CMST CR1", KFSConstants.DocumentStatusCodes.CashReceipt.VERIFIED, new KualiDecimal("25.00"), KualiDecimal.ZERO);
            CashReceiptDocument cr2 = buildCashReceiptDoc(CMST_WORKGROUP, "CMST CR2", KFSConstants.DocumentStatusCodes.CashReceipt.VERIFIED, KualiDecimal.ZERO, new KualiDecimal("25.00"));
            CashReceiptDocument cr3 = buildCashReceiptDoc(CMST_WORKGROUP, "CMST CR3", KFSConstants.DocumentStatusCodes.CashReceipt.VERIFIED, new KualiDecimal("27.00"), new KualiDecimal("23.00"));

            List crList = new ArrayList();
            crList.add(cr1);
            crList.add(cr2);
            crList.add(cr3);

            // add interim deposit
            changeCurrentUser(KHUNTLEY);
            getCashManagementService().addDeposit(retrievedDoc, VALID_DEPOSIT_TICKET, lookupBankAccount(), crList, false);


            //
            // validate results
            CashManagementDocument depositedDoc = (CashManagementDocument) getDocumentService().getByDocumentHeaderId(testDocumentId);

            // 1 deposit
            List deposits = depositedDoc.getDeposits();
            assertEquals(1, deposits.size());

            // deposit exists in database
            Map depositPK = new HashMap();
            depositPK.put(KFSPropertyConstants.DOCUMENT_NUMBER, testDocumentId);
            depositPK.put("financialDocumentDepositLineNumber", new Integer(0));

            assertEquals(1, getBusinessObjectService().countMatching(Deposit.class, depositPK));

            // deposit is interim, not final
            Deposit deposit = (Deposit) deposits.get(0);
            assertEquals(KFSConstants.DepositConstants.DEPOSIT_TYPE_INTERIM, deposit.getDepositTypeCode());

            // deposit contains 3 CRs
            List depositedReceiptControls = deposit.getDepositCashReceiptControl();
            assertEquals(3, depositedReceiptControls.size());

            // CRs are in appropriate state
            assertEquals(KFSConstants.DocumentStatusCodes.CashReceipt.INTERIM, lookupCR(cr1.getDocumentNumber()).getDocumentHeader().getFinancialDocumentStatusCode());
            assertEquals(KFSConstants.DocumentStatusCodes.CashReceipt.INTERIM, lookupCR(cr2.getDocumentNumber()).getDocumentHeader().getFinancialDocumentStatusCode());
            assertEquals(KFSConstants.DocumentStatusCodes.CashReceipt.INTERIM, lookupCR(cr3.getDocumentNumber()).getDocumentHeader().getFinancialDocumentStatusCode());

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
            getCashManagementService().cancelDeposit(null);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }


    @TestsWorkflowViaDatabase
    public void testCancelDeposit_cancelSingleInterim() throws Exception {
        String testDocumentId = null;

        try {
            //
            // create a valid, empty CashManagementDocument
            deleteIfExists(CMST_WORKGROUP);
            CashDrawer preDocCD = getCashDrawerService().getByWorkgroupName(CMST_WORKGROUP, true);
            assertTrue(preDocCD.isClosed());

            CashManagementDocument createdDoc = getCashManagementService().createCashManagementDocument(CMST_WORKGROUP, "CMST_testAddID_valid", null);
            testDocumentId = createdDoc.getDocumentNumber();

            // save it
            getCashDrawerService().openCashDrawer(CMST_WORKGROUP, testDocumentId);
            saveDocument(createdDoc);

            //
            // add Interim Deposit

            // create CashReceipts
            changeCurrentUser(UserNameFixture.INEFF);
            CashReceiptDocument cr1 = buildCashReceiptDoc(CMST_WORKGROUP, "CMST CR1", KFSConstants.DocumentStatusCodes.CashReceipt.VERIFIED, new KualiDecimal("25.00"), KualiDecimal.ZERO);
            CashReceiptDocument cr2 = buildCashReceiptDoc(CMST_WORKGROUP, "CMST CR2", KFSConstants.DocumentStatusCodes.CashReceipt.VERIFIED, KualiDecimal.ZERO, new KualiDecimal("25.00"));
            CashReceiptDocument cr3 = buildCashReceiptDoc(CMST_WORKGROUP, "CMST CR3", KFSConstants.DocumentStatusCodes.CashReceipt.VERIFIED, new KualiDecimal("27.00"), new KualiDecimal("23.00"));

            List crList = new ArrayList();
            crList.add(cr1);
            crList.add(cr2);
            crList.add(cr3);

            // add interim deposit
            changeCurrentUser(KHUNTLEY);
            CashManagementDocument interimDoc = (CashManagementDocument) getDocumentService().getByDocumentHeaderId(testDocumentId);
            getCashManagementService().addDeposit(interimDoc, VALID_DEPOSIT_TICKET, lookupBankAccount(), crList, false);


            //
            // verify addition

            CashManagementDocument depositedDoc = (CashManagementDocument) getDocumentService().getByDocumentHeaderId(testDocumentId);
            {
                // 1 deposit in document
                List deposits = depositedDoc.getDeposits();
                assertEquals(1, deposits.size());

                // deposit exists in database
                Map depositPK = new HashMap();
                depositPK.put(KFSPropertyConstants.DOCUMENT_NUMBER, testDocumentId);
                depositPK.put("financialDocumentDepositLineNumber", new Integer(0));

                assertEquals(1, getBusinessObjectService().countMatching(Deposit.class, depositPK));

                // deposit contains 3 CRs
                Deposit deposit = depositedDoc.getDeposit(0);
                List depositedReceiptControls = deposit.getDepositCashReceiptControl();
                assertEquals(3, depositedReceiptControls.size());

                // CRs are in appropriate state
                assertEquals(KFSConstants.DocumentStatusCodes.CashReceipt.INTERIM, lookupCR(cr1.getDocumentNumber()).getDocumentHeader().getFinancialDocumentStatusCode());
                assertEquals(KFSConstants.DocumentStatusCodes.CashReceipt.INTERIM, lookupCR(cr2.getDocumentNumber()).getDocumentHeader().getFinancialDocumentStatusCode());
                assertEquals(KFSConstants.DocumentStatusCodes.CashReceipt.INTERIM, lookupCR(cr3.getDocumentNumber()).getDocumentHeader().getFinancialDocumentStatusCode());
            }


            {
                //
                // cancel deposit
                Deposit deposit = depositedDoc.getDeposit(0);
                getCashManagementService().cancelDeposit(deposit);

                //
                // verify cancellation
                CashManagementDocument postCanceledDoc = (CashManagementDocument) getDocumentService().getByDocumentHeaderId(testDocumentId);

                // 0 deposits in document
                List deposits = postCanceledDoc.getDeposits();
                assertEquals(0, deposits.size());

                // deposit doesn't exist in database
                Map depositPK = new HashMap();
                depositPK.put(KFSPropertyConstants.DOCUMENT_NUMBER, testDocumentId);
                depositPK.put("financialDocumentDepositLineNumber", new Integer(0));

                assertEquals(0, getBusinessObjectService().countMatching(Deposit.class, depositPK));

                // cash receipts have been restored to appropriate state
                assertEquals(KFSConstants.DocumentStatusCodes.CashReceipt.VERIFIED, lookupCR(cr1.getDocumentNumber()).getDocumentHeader().getFinancialDocumentStatusCode());
                assertEquals(KFSConstants.DocumentStatusCodes.CashReceipt.VERIFIED, lookupCR(cr2.getDocumentNumber()).getDocumentHeader().getFinancialDocumentStatusCode());
                assertEquals(KFSConstants.DocumentStatusCodes.CashReceipt.VERIFIED, lookupCR(cr3.getDocumentNumber()).getDocumentHeader().getFinancialDocumentStatusCode());
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

            CashDrawer forcedOpen = getCashDrawerService().getByWorkgroupName(CMST_WORKGROUP, true);
            forcedOpen.setStatusCode(KFSConstants.CashDrawerConstants.STATUS_OPEN);
            forcedOpen.setReferenceFinancialDocumentNumber(null);
            getBusinessObjectService().save(forcedOpen);

            // try create a new CM doc
            CashManagementDocument createdDoc = getCashManagementService().createCashManagementDocument(CMST_WORKGROUP, "CMST_testAddID_valid", null);
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

            CashDrawer forcedLocked = getCashDrawerService().getByWorkgroupName(CMST_WORKGROUP, true);
            forcedLocked.setStatusCode(KFSConstants.CashDrawerConstants.STATUS_LOCKED);
            forcedLocked.setReferenceFinancialDocumentNumber("0");
            getBusinessObjectService().save(forcedLocked);

            // try create a new CM doc
            CashManagementDocument createdDoc = getCashManagementService().createCashManagementDocument(CMST_WORKGROUP, "CMST_testAddID_valid", null);
            assertNotNull(createdDoc);
        }
        finally {
            deleteIfExists(CMST_WORKGROUP);
        }
    }

    @TestsWorkflowViaDatabase
    public void testKULEDOCS_1475_existentDocument() throws Exception {
        boolean failedAsExpected = false;

        String testDocumentId = null;
        try {

            //
            // create a valid, empty CashManagementDocument
            deleteIfExists(CMST_WORKGROUP);
            CashDrawer preDocCD = getCashDrawerService().getByWorkgroupName(CMST_WORKGROUP, true);
            assertTrue(preDocCD.isClosed());

            CashManagementDocument createdDoc = getCashManagementService().createCashManagementDocument(CMST_WORKGROUP, "CMST_testAddID_valid", null);
            testDocumentId = createdDoc.getDocumentNumber();

            // save it
            getCashDrawerService().openCashDrawer(CMST_WORKGROUP, testDocumentId);
            saveDocument(createdDoc);

            // try create a new CM doc
            getCashManagementService().createCashManagementDocument(CMST_WORKGROUP, "CMST_testAddID_valid", null);
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
        CashReceiptDocument crDoc = (CashReceiptDocument) getDocumentService().getByDocumentHeaderId(documentId);

        return crDoc;
    }


    private void deleteIfExists(String workgroupName) {
        Map deleteCriteria = new HashMap();
        deleteCriteria.put("workgroupName", workgroupName);
        getBusinessObjectService().deleteMatching(CashDrawer.class, deleteCriteria);
    }

    private static final String[] BOTH_STATII = { KFSConstants.DocumentStatusCodes.CashReceipt.VERIFIED, KFSConstants.DocumentStatusCodes.CashReceipt.INTERIM };

    private void denatureCashReceipts(String workgroupName) {
        List verifiedReceipts = getCashReceiptService().getCashReceipts(workgroupName, BOTH_STATII);

        for (Iterator i = verifiedReceipts.iterator(); i.hasNext();) {
            CashReceiptDocument receipt = (CashReceiptDocument) i.next();
            receipt.getDocumentHeader().setFinancialDocumentStatusCode("Z");
            getDocumentService().updateDocument(receipt);
        }
    }

    private CashReceiptDocument buildCashReceiptDoc(String workgroupName, String description, String status, KualiDecimal cashAmount, KualiDecimal checkAmount) throws WorkflowException {
        CashReceiptDocument crDoc = (CashReceiptDocument) getDocumentService().getNewDocument(CashReceiptDocument.class);

        crDoc.getDocumentHeader().setFinancialDocumentDescription(description);
        crDoc.getDocumentHeader().setFinancialDocumentStatusCode(status);

        crDoc.setCheckEntryMode(CashReceiptDocument.CHECK_ENTRY_TOTAL);
        crDoc.setTotalCashAmount(cashAmount);
        crDoc.setTotalCheckAmount(checkAmount);

        crDoc.setCampusLocationCode(getCashReceiptService().getCampusCodeForCashReceiptVerificationUnit(workgroupName));

        crDoc.addSourceAccountingLine(CashReceiptFamilyTestUtil.buildSourceAccountingLine(crDoc.getDocumentNumber(), crDoc.getPostingYear(), crDoc.getNextSourceLineNumber()));
        saveDocument(crDoc);

        CashReceiptDocument persistedDoc = (CashReceiptDocument) getDocumentService().getByDocumentHeaderId(crDoc.getDocumentNumber());
        return persistedDoc;
    }

    private void saveDocument(Document doc)
        throws WorkflowException
    {
        try {
            getDocumentService().saveDocument(doc);
        }
        catch(ValidationException e) {
            // If the business rule evaluation fails then give us more info for debugging this test.
            fail(e.getMessage() + ", " + GlobalVariables.getErrorMap());
        }
    }

    private BankAccount lookupBankAccount() throws GeneralSecurityException {
        Map keyMap = new HashMap();
        keyMap.put("financialDocumentBankCode", "TEST");
        keyMap.put("finDocumentBankAccountNumber", "1111");

        BankAccount bankAccount = (BankAccount) getBusinessObjectService().findByPrimaryKey(BankAccount.class, keyMap);

        assertNotNull("invalid bank account for test",bankAccount);
        return bankAccount;
    }

    private void cleanupCancel(String documentId)
        throws WorkflowException, UserNotFoundException {
        if (documentId != null) {
            Document testDoc = getDocumentService().getByDocumentHeaderId(documentId);

            if (!testDoc.getDocumentHeader().getWorkflowDocument().stateIsCanceled()) {
                final String initiatorNetworkId = testDoc.getDocumentHeader().getWorkflowDocument().getInitiatorNetworkId();
                final String previousNetworkId = GlobalVariables.getUserSession().getNetworkId();
                if (!previousNetworkId.equals(initiatorNetworkId)) {
                    changeCurrentUser(UserNameFixture.valueOf(initiatorNetworkId.toUpperCase()));
                    // Only the initiator can cancel an initiated or saved document.
                    testDoc = getDocumentService().getByDocumentHeaderId(documentId);
                }
                getDocumentService().cancelDocument(testDoc, "CMST cleanup cancel");
                changeCurrentUser(UserNameFixture.valueOf(previousNetworkId.toUpperCase()));
            }
        }
    }
}
