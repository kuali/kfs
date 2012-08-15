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
package org.kuali.kfs.fp.document.service;

import static org.kuali.kfs.sys.fixture.UserNameFixture.twatson;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.fp.businessobject.CashDrawer;
import org.kuali.kfs.fp.businessobject.Deposit;
import org.kuali.kfs.fp.document.CashManagementDocument;
import org.kuali.kfs.fp.document.CashReceiptDocument;
import org.kuali.kfs.fp.document.CashReceiptFamilyTestUtil;
import org.kuali.kfs.fp.document.service.impl.CashManagementServiceImpl;
import org.kuali.kfs.fp.exception.CashDrawerStateException;
import org.kuali.kfs.fp.exception.InvalidCashReceiptState;
import org.kuali.kfs.fp.service.CashDrawerService;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.Bank;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.GlobalVariables;

@ConfigureContext(session = twatson)
public class CashManagementServiceTest extends KualiTestBase {
    static final String CMST_CAMPUS_CODE = "FW";

    final public void testCreateCashManagementDocument_blankUnitName() throws Exception {
        boolean failedAsExpected = false;

        try {
            SpringContext.getBean(CashManagementService.class).createCashManagementDocument("  ", "foo", "cmst1");
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    final public void testCreateCashManagementDocument_blankDocDescription() throws Exception {
        boolean failedAsExpected = false;

        try {
            SpringContext.getBean(CashManagementService.class).createCashManagementDocument(CMST_CAMPUS_CODE, null, "cmst2");
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    @ConfigureContext(session = twatson, shouldCommitTransactions = true)
    final public void testCreateCashManagementDocument_valid() throws Exception {
        String testDocumentId = null;

        try {
            deleteIfExists(CMST_CAMPUS_CODE);
            recreateCashDrawer(CMST_CAMPUS_CODE);
            CashDrawer preDocCD = SpringContext.getBean(CashDrawerService.class).getByCampusCode(CMST_CAMPUS_CODE);
            assertTrue(preDocCD.isClosed());

            CashManagementDocument createdDoc = SpringContext.getBean(CashManagementService.class).createCashManagementDocument(CMST_CAMPUS_CODE, "CMST_testCreate_valid", "cmst3");
            assertNotNull( "document returned from createCashManagementDocument should not have been null", createdDoc);
            testDocumentId = createdDoc.getDocumentNumber();

            // save it separately
            SpringContext.getBean(CashDrawerService.class).openCashDrawer(createdDoc.getCashDrawer(), testDocumentId);
            saveDocument(createdDoc);

            // verify that the doc was saved
            CashManagementDocument retrievedDoc = (CashManagementDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(testDocumentId);
            assertEquals("Document is in incorrect workflow status", DocumentStatus.SAVED, retrievedDoc.getDocumentHeader().getWorkflowDocument().getStatus());
        }
        finally {
            // cancel the document
            cleanupCancel(testDocumentId);

            // delete the cashDrawer which was created as a side-effect above
            deleteIfExists(CMST_CAMPUS_CODE);
        }
    }

    @ConfigureContext(session = twatson, shouldCommitTransactions = true)
    final public void testCreateCashManagementDocument_cashDrawerAlreadyOpen() throws Exception {

        String testDocumentId = null;
        try {
            deleteIfExists(CMST_CAMPUS_CODE);
            recreateCashDrawer(CMST_CAMPUS_CODE);
            CashDrawer preDocCD = SpringContext.getBean(CashDrawerService.class).getByCampusCode(CMST_CAMPUS_CODE);
            assertTrue(preDocCD.isClosed());

            CashManagementDocument createdDoc = SpringContext.getBean(CashManagementService.class).createCashManagementDocument(CMST_CAMPUS_CODE, "CMST_testCreate_cashDrawerAlreadyOpen", "cmst3");
            assertNotNull( "document returned from createCashManagementDocument should not have been null", createdDoc);
            testDocumentId = createdDoc.getDocumentNumber();

            // force the drawer open
            SpringContext.getBean(CashDrawerService.class).openCashDrawer(createdDoc.getCashDrawer(), testDocumentId);
            saveDocument(createdDoc);

            boolean failedAsExpected = false;
            // fail creating the document since the CashDrawer is already open
            try {
                SpringContext.getBean(CashManagementService.class).createCashManagementDocument(CMST_CAMPUS_CODE, "CMST_testCreate_validCollision 2", "cmst5");
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
            SpringContext.getBean(CashManagementService.class).cancelCashManagementDocument(createdDoc);

        }
        finally {
            // cancel the document
            cleanupCancel(testDocumentId);

            // delete the cashDrawer you created
            deleteIfExists(CMST_CAMPUS_CODE);
        }
    }

    @ConfigureContext(session = twatson, shouldCommitTransactions = true)
    final public void testCancelCashManagementDocument_valid_interimOnly() throws Exception {
        String testDocumentId = null;

        try {
            //
            // create a valid, empty CashManagementDocument
            deleteIfExists(CMST_CAMPUS_CODE);
            recreateCashDrawer(CMST_CAMPUS_CODE);
            CashDrawer preDocCD = SpringContext.getBean(CashDrawerService.class).getByCampusCode(CMST_CAMPUS_CODE);
            assertTrue( "cash drawer should have been closed", preDocCD.isClosed());

            CashManagementDocument createdDoc = SpringContext.getBean(CashManagementService.class).createCashManagementDocument(CMST_CAMPUS_CODE, "CMST_testAddID_valid", null);
            testDocumentId = createdDoc.getDocumentNumber();

            // save it separately
            SpringContext.getBean(CashDrawerService.class).openCashDrawer(createdDoc.getCashDrawer(), testDocumentId);
            saveDocument(createdDoc);

            //
            // create Interim Deposit

            // create CashReceipts
            changeCurrentUser(UserNameFixture.ineff);
            CashReceiptDocument cr1 = buildCashReceiptDoc(CMST_CAMPUS_CODE, "CMST CR1", KFSConstants.DocumentStatusCodes.CashReceipt.VERIFIED, new KualiDecimal("25.00"));
            CashReceiptDocument cr2 = buildCashReceiptDoc(CMST_CAMPUS_CODE, "CMST CR2", KFSConstants.DocumentStatusCodes.CashReceipt.VERIFIED, new KualiDecimal("25.00"));
            CashReceiptDocument cr3 = buildCashReceiptDoc(CMST_CAMPUS_CODE, "CMST CR3", KFSConstants.DocumentStatusCodes.CashReceipt.VERIFIED, new KualiDecimal("23.00"));

            List crList = new ArrayList();
            crList.add(cr1);
            crList.add(cr2);
            crList.add(cr3);

            // add interim deposit
            changeCurrentUser(twatson);
            CashManagementDocument interimDoc = (CashManagementDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(testDocumentId);
            SpringContext.getBean(CashManagementService.class).addDeposit(interimDoc, VALID_DEPOSIT_TICKET, lookupBank(), crList, new ArrayList(), false);


            //
            // verify addition
            CashManagementDocument depositedDoc = (CashManagementDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(testDocumentId);
            {

                // 1 deposit in document
                List deposits = depositedDoc.getDeposits();
                assertEquals( "number of deposits incorrect", 1, deposits.size());

                // deposit exists in database
                Map<String,Object> depositPK = new HashMap<String, Object>(2);
                depositPK.put(KFSPropertyConstants.DOCUMENT_NUMBER, testDocumentId);
                depositPK.put("financialDocumentDepositLineNumber", new Integer(0));

                assertEquals( "number of deposits does not match the database", 1, SpringContext.getBean(BusinessObjectService.class).countMatching(Deposit.class, depositPK));

                // deposit contains 3 CRs
                Deposit deposit = depositedDoc.getDeposit(0);
                List depositedReceiptControls = SpringContext.getBean(CashManagementService.class).retrieveCashReceipts(deposit);
                assertEquals( "the number of receipt control records does not match", 3, depositedReceiptControls.size());

                // CRs are in appropriate state
                assertEquals( "cash receipt state is incorrect for CR #1", KFSConstants.DocumentStatusCodes.CashReceipt.INTERIM, lookupCR(cr1.getDocumentNumber()).getFinancialSystemDocumentHeader().getFinancialDocumentStatusCode());
                assertEquals( "cash receipt state is incorrect for CR #2", KFSConstants.DocumentStatusCodes.CashReceipt.INTERIM, lookupCR(cr2.getDocumentNumber()).getFinancialSystemDocumentHeader().getFinancialDocumentStatusCode());
                assertEquals( "cash receipt state is incorrect for CR #3", KFSConstants.DocumentStatusCodes.CashReceipt.INTERIM, lookupCR(cr3.getDocumentNumber()).getFinancialSystemDocumentHeader().getFinancialDocumentStatusCode());
            }

        }
        finally {

            // clean up CRdoc
            denatureCashReceipts(CMST_CAMPUS_CODE);

            // cancel CMDoc; now we can
            cleanupCancel(testDocumentId);

            // delete the cashDrawer which was created as a side-effect above
            deleteIfExists(CMST_CAMPUS_CODE);
        }
    }


    private static final String VALID_DEPOSIT_TICKET = "0 0 0 destruct 0";

    final public void testAddInterimDeposit_nullDoc() throws Exception {
        boolean failedAsExpected = false;

        try {
            SpringContext.getBean(CashManagementService.class).addDeposit(null, VALID_DEPOSIT_TICKET, lookupBank(), null, null, false);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue( "addition of null document for deposit did not fail", failedAsExpected);
    }

    @ConfigureContext(session = twatson, shouldCommitTransactions = true)
    final public void testAddInterimDeposit_nullBank() throws Exception {
        boolean failedAsExpected = false;

        String docId = null;
        try {
            deleteIfExists(CMST_CAMPUS_CODE);
            recreateCashDrawer(CMST_CAMPUS_CODE);
            CashManagementDocument createdDoc = SpringContext.getBean(CashManagementService.class).createCashManagementDocument(CMST_CAMPUS_CODE, "CMST_testAddID_eCRL", null);
            docId = createdDoc.getDocumentNumber();

            SpringContext.getBean(CashDrawerService.class).openCashDrawer(createdDoc.getCashDrawer(), docId);
            saveDocument(createdDoc);

            SpringContext.getBean(CashManagementService.class).addDeposit(createdDoc, VALID_DEPOSIT_TICKET, null, new ArrayList(), new ArrayList(), false);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }
        finally {
            // clean up CRdoc
            denatureCashReceipts(CMST_CAMPUS_CODE);

            if (docId != null) {
                Document testDoc = SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(docId);
                SpringContext.getBean(DocumentService.class).cancelDocument(testDoc, "CMST cleanup");
            }

            // delete the cashDrawer which was created as a side-effect above
            deleteIfExists(CMST_CAMPUS_CODE);
        }

        assertTrue(failedAsExpected);
    }

    @ConfigureContext(session = twatson, shouldCommitTransactions = true)
    final public void testAddInterimDeposit_nonverifiedCashReceipt() throws Exception {
        boolean failedAsExpected = false;

        String testDocumentId = null;

        try {
            //
            // create a valid, empty CashManagementDocument
            deleteIfExists(CMST_CAMPUS_CODE);
            recreateCashDrawer(CMST_CAMPUS_CODE);
            CashDrawer preDocCD = SpringContext.getBean(CashDrawerService.class).getByCampusCode(CMST_CAMPUS_CODE);
            assertTrue(preDocCD.isClosed());

            CashManagementDocument createdDoc = SpringContext.getBean(CashManagementService.class).createCashManagementDocument(CMST_CAMPUS_CODE, "CMST_testAddID_nonverified", null);
            testDocumentId = createdDoc.getDocumentNumber();

            SpringContext.getBean(CashDrawerService.class).openCashDrawer(createdDoc.getCashDrawer(), testDocumentId);
            saveDocument(createdDoc);

            // retrieve the document, for future use
            CashManagementDocument retrievedDoc = (CashManagementDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(testDocumentId);

            //
            // create Interim Deposit

            // create CashReceipt
            changeCurrentUser(UserNameFixture.ineff);
            //CashReceiptDocument cr = buildCashReceiptDoc(CMST_CAMPUS_CODE, "CMST nonverified CR", KFSConstants.DocumentStatusCodes.CashReceipt.INTERIM, new KualiDecimal("75.00"));
            CashReceiptDocument cr = buildCashReceiptDoc(CMST_CAMPUS_CODE, "CMST nonverified CR", KFSConstants.DocumentStatusCodes.CashReceipt.FINAL, new KualiDecimal("75.00"));
            changeCurrentUser(twatson);

            List crList = new ArrayList();
            crList.add(cr);

            // add invalid interim deposit
            SpringContext.getBean(CashManagementService.class).addDeposit(retrievedDoc, VALID_DEPOSIT_TICKET, lookupBank(), crList, new ArrayList(), false);
        }
        catch (InvalidCashReceiptState e) {
            failedAsExpected = true;
        }
        finally {
            // clean up CRdoc
            denatureCashReceipts(CMST_CAMPUS_CODE);

            // cancel CMDoc
            cleanupCancel(testDocumentId);

            // delete the cashDrawer which was created as a side-effect above
            deleteIfExists(CMST_CAMPUS_CODE);
        }

        assertTrue(failedAsExpected);
    }

    @ConfigureContext(session = twatson, shouldCommitTransactions = true)
    final public void testAddInterimDeposit_unsavedCMDoc() throws Exception {
        boolean failedAsExpected = false;

        String testDocumentId = null;

        try {
            //
            // create a valid, empty CashManagementDocument
            deleteIfExists(CMST_CAMPUS_CODE);
            recreateCashDrawer(CMST_CAMPUS_CODE);
            CashDrawer preDocCD = SpringContext.getBean(CashDrawerService.class).getByCampusCode(CMST_CAMPUS_CODE);
            assertTrue( "cash drawer should have been closed", preDocCD.isClosed());

            changeCurrentUser(UserNameFixture.twatson);
            CashManagementDocument createdDoc = SpringContext.getBean(CashManagementService.class).createCashManagementDocument(CMST_CAMPUS_CODE, "CMST_testAddID_nonverified", null);
            testDocumentId = createdDoc.getDocumentNumber();

            //
            // create Interim Deposit

            // create CashReceipt
            changeCurrentUser(UserNameFixture.ineff);
            CashReceiptDocument cr = buildCashReceiptDoc(CMST_CAMPUS_CODE, "CMST noncheck CR", KFSConstants.DocumentStatusCodes.CashReceipt.VERIFIED, new KualiDecimal("25.00"));
            changeCurrentUser(UserNameFixture.twatson);

            List crList = new ArrayList();
            crList.add(cr);

            // add interim deposit
            SpringContext.getBean(CashManagementService.class).addDeposit(createdDoc, VALID_DEPOSIT_TICKET, lookupBank(), crList, new ArrayList(), false);
        }
        catch (IllegalStateException e) {
            failedAsExpected = true;
        }
        finally {
            // clean up CRdoc
            denatureCashReceipts(CMST_CAMPUS_CODE);

            // delete the cashDrawer which was created as a side-effect above
            deleteIfExists(CMST_CAMPUS_CODE);
        }

        assertTrue(failedAsExpected);
    }


    @ConfigureContext(session = twatson, shouldCommitTransactions = true)
    final public void testAddInterimDeposit_valid() throws Exception {
        String testDocumentId = null;

        try {
            //
            // create a valid, empty CashManagementDocument
            deleteIfExists(CMST_CAMPUS_CODE);
            recreateCashDrawer(CMST_CAMPUS_CODE);
            CashDrawer preDocCD = SpringContext.getBean(CashDrawerService.class).getByCampusCode(CMST_CAMPUS_CODE);
            assertTrue( "cash drawer should have been closed", preDocCD.isClosed());

            CashManagementDocument createdDoc = SpringContext.getBean(CashManagementService.class).createCashManagementDocument(CMST_CAMPUS_CODE, "CMST_testAddID_valid", null);
            testDocumentId = createdDoc.getDocumentNumber();

            // save it
            SpringContext.getBean(CashDrawerService.class).openCashDrawer(createdDoc.getCashDrawer(), testDocumentId);
            saveDocument(createdDoc);

            // retrieve the document, for future use
            CashManagementDocument retrievedDoc = (CashManagementDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(testDocumentId);

            //
            // create Interim Deposit

            // create CashReceipts
            changeCurrentUser(UserNameFixture.ineff);
            CashReceiptDocument cr1 = buildCashReceiptDoc(CMST_CAMPUS_CODE, "CMST CR1", KFSConstants.DocumentStatusCodes.CashReceipt.VERIFIED, new KualiDecimal("10.00"));
            CashReceiptDocument cr2 = buildCashReceiptDoc(CMST_CAMPUS_CODE, "CMST CR2", KFSConstants.DocumentStatusCodes.CashReceipt.VERIFIED, new KualiDecimal("25.00"));
            CashReceiptDocument cr3 = buildCashReceiptDoc(CMST_CAMPUS_CODE, "CMST CR3", KFSConstants.DocumentStatusCodes.CashReceipt.VERIFIED, new KualiDecimal("23.00"));

            List<CashReceiptDocument> crList = new ArrayList<CashReceiptDocument>();
            crList.add(cr1);
            crList.add(cr2);
            crList.add(cr3);

            // add interim deposit
            changeCurrentUser(UserNameFixture.twatson);
            SpringContext.getBean(CashManagementService.class).addDeposit(retrievedDoc, VALID_DEPOSIT_TICKET, lookupBank(), crList, Collections.emptyList(), false);


            //
            // validate results
            CashManagementDocument depositedDoc = (CashManagementDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(testDocumentId);

            // 1 deposit
            List deposits = depositedDoc.getDeposits();
            assertEquals( "number of deposits is not correct", 1, deposits.size());

            // deposit exists in database
            Map<String, Object> depositPK = new HashMap<String, Object>();
            depositPK.put(KFSPropertyConstants.DOCUMENT_NUMBER, testDocumentId);
            depositPK.put("financialDocumentDepositLineNumber", new Integer(0));

            assertEquals( "number of deposits does not match database", 1, SpringContext.getBean(BusinessObjectService.class).countMatching(Deposit.class, depositPK));

            // deposit is interim, not final
            Deposit deposit = (Deposit) deposits.get(0);
            assertEquals( "deposit type code is incorrect", KFSConstants.DepositConstants.DEPOSIT_TYPE_INTERIM, deposit.getDepositTypeCode());

            // deposit contains 3 CRs
            List depositedReceiptControls = SpringContext.getBean(CashManagementService.class).retrieveCashReceipts(deposit);
            assertEquals( "number of cash receipts on the deposit is incorrect", 3, depositedReceiptControls.size());

            // CRs are in appropriate state
            assertEquals( "status code on CR #1 is incorrect", KFSConstants.DocumentStatusCodes.CashReceipt.INTERIM, lookupCR(cr1.getDocumentNumber()).getFinancialSystemDocumentHeader().getFinancialDocumentStatusCode());
            assertEquals( "status code on CR #2 is incorrect", KFSConstants.DocumentStatusCodes.CashReceipt.INTERIM, lookupCR(cr2.getDocumentNumber()).getFinancialSystemDocumentHeader().getFinancialDocumentStatusCode());
            assertEquals( "status code on CR #3 is incorrect", KFSConstants.DocumentStatusCodes.CashReceipt.INTERIM, lookupCR(cr3.getDocumentNumber()).getFinancialSystemDocumentHeader().getFinancialDocumentStatusCode());

            // total value of the deposit is the sum of the values of the 3 CRs
            assertEquals( "deposit amount is incorrect", new KualiDecimal("58.00"), deposit.getDepositAmount());
        }
        finally {
            // clean up CRdoc
            denatureCashReceipts(CMST_CAMPUS_CODE);

            // cancel CMDoc
            cleanupCancel(testDocumentId);

            // delete the cashDrawer which was created as a side-effect above
            deleteIfExists(CMST_CAMPUS_CODE);
        }
    }


    public void testCancelDeposit_nullDeposit() throws Exception {
        boolean failedAsExpected = false;

        try {
            SpringContext.getBean(CashManagementService.class).cancelDeposit(null);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }


    @ConfigureContext(session = twatson, shouldCommitTransactions = true)
    public void testCancelDeposit_cancelSingleInterim() throws Exception {
        String testDocumentId = null;

        try {
            //
            // create a valid, empty CashManagementDocument
            deleteIfExists(CMST_CAMPUS_CODE);
            recreateCashDrawer(CMST_CAMPUS_CODE);
            CashDrawer preDocCD = SpringContext.getBean(CashDrawerService.class).getByCampusCode(CMST_CAMPUS_CODE);
            assertTrue(preDocCD.isClosed());

            CashManagementDocument createdDoc = SpringContext.getBean(CashManagementService.class).createCashManagementDocument(CMST_CAMPUS_CODE, "CMST_testAddID_valid", null);
            testDocumentId = createdDoc.getDocumentNumber();

            // save it
            SpringContext.getBean(CashDrawerService.class).openCashDrawer(createdDoc.getCashDrawer(), testDocumentId);
            saveDocument(createdDoc);

            //
            // add Interim Deposit

            // create CashReceipts
            changeCurrentUser(UserNameFixture.ineff);
            CashReceiptDocument cr1 = buildCashReceiptDoc(CMST_CAMPUS_CODE, "CMST CR1", KFSConstants.DocumentStatusCodes.CashReceipt.VERIFIED, new KualiDecimal("25.00"));
            CashReceiptDocument cr2 = buildCashReceiptDoc(CMST_CAMPUS_CODE, "CMST CR2", KFSConstants.DocumentStatusCodes.CashReceipt.VERIFIED, new KualiDecimal("25.00"));
            CashReceiptDocument cr3 = buildCashReceiptDoc(CMST_CAMPUS_CODE, "CMST CR3", KFSConstants.DocumentStatusCodes.CashReceipt.VERIFIED, new KualiDecimal("25.00"));

            List crList = new ArrayList();
            crList.add(cr1);
            crList.add(cr2);
            crList.add(cr3);

            // add interim deposit
            changeCurrentUser(twatson);
            CashManagementDocument interimDoc = (CashManagementDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(testDocumentId);
            SpringContext.getBean(CashManagementService.class).addDeposit(interimDoc, VALID_DEPOSIT_TICKET, lookupBank(), crList, new ArrayList(), false);


            //
            // verify addition

            CashManagementDocument depositedDoc = (CashManagementDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(testDocumentId);
            {
                // 1 deposit in document
                List deposits = depositedDoc.getDeposits();
                assertEquals( "number of deposits is not correct", 1, deposits.size());

                // deposit exists in database
                Map depositPK = new HashMap();
                depositPK.put(KFSPropertyConstants.DOCUMENT_NUMBER, testDocumentId);
                depositPK.put("financialDocumentDepositLineNumber", new Integer(0));

                assertEquals( "number of deposits does not match database", 1, SpringContext.getBean(BusinessObjectService.class).countMatching(Deposit.class, depositPK));

                // deposit contains 3 CRs
                Deposit deposit = depositedDoc.getDeposit(0);
                List depositedReceiptControls = SpringContext.getBean(CashManagementService.class).retrieveCashReceipts(deposit);
                assertEquals( "number of cash receipts on the deposit is incorrect", 3, depositedReceiptControls.size());

                // CRs are in appropriate state
                assertEquals(KFSConstants.DocumentStatusCodes.CashReceipt.INTERIM, lookupCR(cr1.getDocumentNumber()).getFinancialSystemDocumentHeader().getFinancialDocumentStatusCode());
                assertEquals(KFSConstants.DocumentStatusCodes.CashReceipt.INTERIM, lookupCR(cr2.getDocumentNumber()).getFinancialSystemDocumentHeader().getFinancialDocumentStatusCode());
                assertEquals(KFSConstants.DocumentStatusCodes.CashReceipt.INTERIM, lookupCR(cr3.getDocumentNumber()).getFinancialSystemDocumentHeader().getFinancialDocumentStatusCode());
            }


            {
                //
                // cancel deposit
                Deposit deposit = depositedDoc.getDeposit(0);
                SpringContext.getBean(CashManagementService.class).cancelDeposit(deposit);

                //
                // verify cancellation
                CashManagementDocument postCanceledDoc = (CashManagementDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(testDocumentId);

                // 0 deposits in document
                List deposits = postCanceledDoc.getDeposits();
                assertEquals(0, deposits.size());

                // deposit doesn't exist in database
                Map depositPK = new HashMap();
                depositPK.put(KFSPropertyConstants.DOCUMENT_NUMBER, testDocumentId);
                depositPK.put("financialDocumentDepositLineNumber", new Integer(0));

                assertEquals(0, SpringContext.getBean(BusinessObjectService.class).countMatching(Deposit.class, depositPK));

                // cash receipts have been restored to appropriate state
                assertEquals(KFSConstants.DocumentStatusCodes.CashReceipt.VERIFIED, lookupCR(cr1.getDocumentNumber()).getFinancialSystemDocumentHeader().getFinancialDocumentStatusCode());
                assertEquals(KFSConstants.DocumentStatusCodes.CashReceipt.VERIFIED, lookupCR(cr2.getDocumentNumber()).getFinancialSystemDocumentHeader().getFinancialDocumentStatusCode());
                assertEquals(KFSConstants.DocumentStatusCodes.CashReceipt.VERIFIED, lookupCR(cr3.getDocumentNumber()).getFinancialSystemDocumentHeader().getFinancialDocumentStatusCode());
            }
        }
        finally {
            // clean up CRdoc
            denatureCashReceipts(CMST_CAMPUS_CODE);

            // cancel CMDoc
            cleanupCancel(testDocumentId);

            // delete the cashDrawer which was created as a side-effect above
            deleteIfExists(CMST_CAMPUS_CODE);
        }
    }


    public void testKULEDOCS_1475_nullDocument() {
        try {
            // open the Cash Drawer for a null documentId
            deleteIfExists(CMST_CAMPUS_CODE);
            recreateCashDrawer(CMST_CAMPUS_CODE);
            CashDrawer forcedOpen = SpringContext.getBean(CashDrawerService.class).getByCampusCode(CMST_CAMPUS_CODE);
            forcedOpen.setStatusCode(KFSConstants.CashDrawerConstants.STATUS_OPEN);
            forcedOpen.setReferenceFinancialDocumentNumber(null);
            SpringContext.getBean(BusinessObjectService.class).save(forcedOpen);

            // try create a new CM doc
            CashManagementDocument createdDoc = SpringContext.getBean(CashManagementService.class).createCashManagementDocument(CMST_CAMPUS_CODE, "CMST_testAddID_valid", null);
            assertNotNull(createdDoc);
        }
        finally {
            deleteIfExists(CMST_CAMPUS_CODE);
        }
    }

    public void testKULEDOCS_1475_nonexistentDocument() {
        try {
            // open the Cash Drawer for a nonexistent documentId
            deleteIfExists(CMST_CAMPUS_CODE);
            recreateCashDrawer(CMST_CAMPUS_CODE);
            CashDrawer forcedLocked = SpringContext.getBean(CashDrawerService.class).getByCampusCode(CMST_CAMPUS_CODE);
            forcedLocked.setStatusCode(KFSConstants.CashDrawerConstants.STATUS_LOCKED);
            forcedLocked.setReferenceFinancialDocumentNumber("0");
            SpringContext.getBean(BusinessObjectService.class).save(forcedLocked);

            // try create a new CM doc
            CashManagementDocument createdDoc = SpringContext.getBean(CashManagementService.class).createCashManagementDocument(CMST_CAMPUS_CODE, "CMST_testAddID_valid", null);
            assertNotNull(createdDoc);
        }
        finally {
            deleteIfExists(CMST_CAMPUS_CODE);
        }
    }

    @ConfigureContext(session = twatson, shouldCommitTransactions = true)
    public void testKULEDOCS_1475_existentDocument() throws Exception {
        boolean failedAsExpected = false;

        String testDocumentId = null;
        try {

            //
            // create a valid, empty CashManagementDocument
            deleteIfExists(CMST_CAMPUS_CODE);
            recreateCashDrawer(CMST_CAMPUS_CODE);
            CashDrawer preDocCD = SpringContext.getBean(CashDrawerService.class).getByCampusCode(CMST_CAMPUS_CODE);
            assertTrue(preDocCD.isClosed());

            CashManagementDocument createdDoc = SpringContext.getBean(CashManagementService.class).createCashManagementDocument(CMST_CAMPUS_CODE, "CMST_testAddID_valid", null);
            testDocumentId = createdDoc.getDocumentNumber();

            // save it
            SpringContext.getBean(CashDrawerService.class).openCashDrawer(createdDoc.getCashDrawer(), testDocumentId);
            saveDocument(createdDoc);

            // try create a new CM doc
            SpringContext.getBean(CashManagementService.class).createCashManagementDocument(CMST_CAMPUS_CODE, "CMST_testAddID_valid", null);
        }
        catch (CashDrawerStateException e) {
            failedAsExpected = true;
        }
        finally {
            // cancel CMDoc
            cleanupCancel(testDocumentId);

            deleteIfExists(CMST_CAMPUS_CODE);
        }

        assertTrue(failedAsExpected);
    }

    private CashReceiptDocument lookupCR(String documentId) throws WorkflowException {
        CashReceiptDocument crDoc = (CashReceiptDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(documentId);

        return crDoc;
    }

    private void deleteIfExists(String campusCode) {
        Map deleteCriteria = new HashMap();
        deleteCriteria.put("campusCode", campusCode);
        SpringContext.getBean(BusinessObjectService.class).deleteMatching(CashDrawer.class, deleteCriteria);
    }
    
    private void recreateCashDrawer(String campusCode) {
        CashDrawer cd = new CashDrawer();
        cd.setCampusCode(campusCode);
        cd.setStatusCode(KFSConstants.CashDrawerConstants.STATUS_CLOSED);
        SpringContext.getBean(BusinessObjectService.class).save(cd);
    }

    private static final String[] BOTH_STATII = { KFSConstants.DocumentStatusCodes.CashReceipt.VERIFIED, KFSConstants.DocumentStatusCodes.CashReceipt.INTERIM };

    private void denatureCashReceipts(String campusCode) {
        List verifiedReceipts = SpringContext.getBean(CashReceiptService.class).getCashReceipts(campusCode, BOTH_STATII);

        for (Iterator i = verifiedReceipts.iterator(); i.hasNext();) {
            CashReceiptDocument receipt = (CashReceiptDocument) i.next();
            receipt.getFinancialSystemDocumentHeader().setFinancialDocumentStatusCode("Z");
            SpringContext.getBean(DocumentService.class).updateDocument(receipt);
        }
    }

    private CashReceiptDocument buildCashReceiptDoc(String campusCode, String description, String status, KualiDecimal checkAmount) throws WorkflowException {
        CashReceiptDocument crDoc = (CashReceiptDocument) SpringContext.getBean(DocumentService.class).getNewDocument(CashReceiptDocument.class);

        crDoc.getDocumentHeader().setDocumentDescription(description);
        crDoc.getFinancialSystemDocumentHeader().setFinancialDocumentStatusCode(status);

        crDoc.setCheckEntryMode(CashReceiptDocument.CHECK_ENTRY_TOTAL);
        crDoc.setTotalConfirmedCashAmount(KualiDecimal.ZERO); // cash amounts are now calculated differently
        crDoc.setTotalConfirmedCheckAmount(checkAmount);

        crDoc.setCampusLocationCode(campusCode);

        crDoc.addSourceAccountingLine(CashReceiptFamilyTestUtil.buildSourceAccountingLine(crDoc.getDocumentNumber(), crDoc.getPostingYear(), crDoc.getNextSourceLineNumber()));
        saveDocument(crDoc);

        CashReceiptDocument persistedDoc = (CashReceiptDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(crDoc.getDocumentNumber());
        return persistedDoc;
    }

    
    private void saveDocument(Document doc) throws WorkflowException {
        try {
            SpringContext.getBean(DocumentService.class).saveDocument(doc);
        }
        catch (ValidationException e) {
            // If the business rule evaluation fails then give us more info for debugging this test.
            fail(e.getMessage() + ", " + GlobalVariables.getMessageMap());
        }
    }

    private Bank lookupBank() throws GeneralSecurityException {
        Map<String, String> keyMap = new HashMap<String, String>();
        keyMap.put("bankCode", "TEST");

        Bank bank = (Bank) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Bank.class, keyMap);

        assertNotNull("invalid bank for test", bank);
        return bank;
    }

    private void cleanupCancel(String documentId) throws Exception {
        if (documentId != null) {
            Document testDoc = SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(documentId);

            if (testDoc != null && !testDoc.getDocumentHeader().getWorkflowDocument().isCanceled()) {
                final String initiatorNetworkId = SpringContext.getBean(PersonService.class).getPerson(testDoc.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId()).getPrincipalName();
                final String previousNetworkId = GlobalVariables.getUserSession().getPerson().getPrincipalName();
                if (!previousNetworkId.equals(initiatorNetworkId)) {
                    changeCurrentUser(UserNameFixture.valueOf(initiatorNetworkId.toLowerCase()));
                    // Only the initiator can cancel an initiated or saved document.
                    testDoc = SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(documentId);
                }
                SpringContext.getBean(DocumentService.class).cancelDocument(testDoc, "CMST cleanup cancel");
                changeCurrentUser(UserNameFixture.valueOf(previousNetworkId.toLowerCase()));
            }
        }
    }
}

