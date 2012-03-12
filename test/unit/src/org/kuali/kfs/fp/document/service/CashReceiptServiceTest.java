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

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.fp.document.CashReceiptDocument;
import org.kuali.kfs.fp.document.CashReceiptFamilyTestUtil;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.GlobalVariables;

@ConfigureContext(session = khuntley)
public class CashReceiptServiceTest extends KualiTestBase {
    // TODO: once we stop returning default campusCode for unknown verificationUnit, need a test for unknown verificationUnit
    private static final String TEST_CAMPUS_CD = "KO";

    private static final String DEFAULT_CAMPUS_CD = "BL";

    private static final String UNKNOWN_CAMPUS_CODE = "ZZ";

    // TODO fix this so it doesn't use constants built into the non-test classes
    /*
     * public final void testGetCampusCodeForCashReceiptVerificationUnit_knownVerificationUnit() { String returnedCode =
     * SpringContext.getBean(CashReceiptService.class).getCampusCodeForCashReceiptVerificationUnit(TEST_UNIT_NAME);
     * assertNotNull(returnedCode); assertEquals(TEST_CAMPUS_CD, returnedCode); }
     */

    // TODO: once we stop returning default campusCode for unknown verificationUnit, need a test for unknown verificationUnit
    /*
     * public final void testGetCashReceiptVerificationUnitForCampusCode_knownCampusCode() { String returnedUnit =
     * SpringContext.getBean(CashReceiptService.class).getCashReceiptVerificationUnitForCampusCode(TEST_CAMPUS_CD);
     * assertNotNull(returnedUnit); assertEquals(TEST_UNIT_NAME, returnedUnit); }
     */


    public final void testGetCashReceiptVerificationUnit_nullUser() {
        boolean failedAsExpected = false;

        try {
            SpringContext.getBean(CashReceiptService.class).getCashReceiptVerificationUnitForUser(null);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    public final void testGetCashReceiptVerificationUnit_validUser() {
        String expectedUnit = DEFAULT_CAMPUS_CD;

        String unit = SpringContext.getBean(CashReceiptService.class).getCashReceiptVerificationUnitForUser(GlobalVariables.getUserSession().getPerson());
        assertEquals(expectedUnit, unit);
    }

    // TODO: once we stop returning default campus code for every user, need a test for a user who is not in a verification unit

    public final void testGetCashReceipts1_blankUnitName() {
        boolean failedAsExpected = false;

        try {
            SpringContext.getBean(CashReceiptService.class).getCashReceipts("   ", (String) null);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    public final void testGetCashReceipts1_blankStatusCode() {
        boolean failedAsExpected = false;

        try {
            SpringContext.getBean(CashReceiptService.class).getCashReceipts(TEST_CAMPUS_CD, "");
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    // TODO: once we stop returning default campus code for unknown unit, need tests for unknown unit

    public final void testGetCashReceipts1_knownVerificationUnit_noVerifiedReceipts() {
        final String workgroup = TEST_CAMPUS_CD;

        // clean up before testing
        denatureCashReceipts(workgroup);

        List receipts = SpringContext.getBean(CashReceiptService.class).getCashReceipts(workgroup, KFSConstants.DocumentStatusCodes.CashReceipt.VERIFIED);
        assertEquals(0, receipts.size());
    }

    public final void testGetCashReceipts1_knownVerificationUnit_noInterimReceipts() {
        final String workgroup = TEST_CAMPUS_CD;

        // clean up before testing
        denatureCashReceipts(workgroup);

        List receipts = SpringContext.getBean(CashReceiptService.class).getCashReceipts(workgroup, KFSConstants.DocumentStatusCodes.CashReceipt.INTERIM);
        assertEquals(0, receipts.size());
    }


    @ConfigureContext(session = khuntley, shouldCommitTransactions = true)
    public final void testGetCashReceipts1_knownVerificationUnit_interimReceipts() throws Exception {
        final String workgroup = TEST_CAMPUS_CD;

        // clean up before testing
        denatureCashReceipts(workgroup);

        // create some CRs
        changeCurrentUser(UserNameFixture.ineff);
        CashReceiptDocument cr1 = buildCashReceiptDoc(workgroup, "ww2 CRST cr1", KFSConstants.DocumentStatusCodes.CashReceipt.INTERIM, new KualiDecimal("101.01"), new KualiDecimal("898.99"));

        CashReceiptDocument cr2 = buildCashReceiptDoc(workgroup, "ww2 CRST cr2", KFSConstants.DocumentStatusCodes.CashReceipt.INTERIM, new KualiDecimal("212.12"), new KualiDecimal("787.87"));


        // verify that there are only interim CRs
        List vreceipts = SpringContext.getBean(CashReceiptService.class).getCashReceipts(workgroup, KFSConstants.DocumentStatusCodes.CashReceipt.VERIFIED);
        assertEquals(0, vreceipts.size());

        List ireceipts = SpringContext.getBean(CashReceiptService.class).getCashReceipts(workgroup, KFSConstants.DocumentStatusCodes.CashReceipt.INTERIM);
        assertEquals(2, ireceipts.size());

        // clean up afterwards
        denatureCashReceipts(workgroup);
    }

    @ConfigureContext(session = khuntley, shouldCommitTransactions = true)
    public final void testGetCashReceipts1_knownVerificationUnit_verifiedReceipts() throws Exception {
        final String workgroup = TEST_CAMPUS_CD;

        // clean up before testing
        denatureCashReceipts(workgroup);

        // create some CRs
        changeCurrentUser(UserNameFixture.ineff);
        CashReceiptDocument cr1 = buildCashReceiptDoc(workgroup, "ww2 CRST cr1", KFSConstants.DocumentStatusCodes.CashReceipt.VERIFIED, new KualiDecimal("101.01"), new KualiDecimal("898.99"));

        CashReceiptDocument cr2 = buildCashReceiptDoc(workgroup, "ww2 CRST cr2", KFSConstants.DocumentStatusCodes.CashReceipt.VERIFIED, new KualiDecimal("212.12"), new KualiDecimal("787.87"));


        // verify that there are only verified CRs
        List ireceipts = SpringContext.getBean(CashReceiptService.class).getCashReceipts(workgroup, KFSConstants.DocumentStatusCodes.CashReceipt.INTERIM);
        assertEquals(0, ireceipts.size());

        List vreceipts = SpringContext.getBean(CashReceiptService.class).getCashReceipts(workgroup, KFSConstants.DocumentStatusCodes.CashReceipt.VERIFIED);
        assertEquals(2, vreceipts.size());

        // clean up afterwards
        denatureCashReceipts(workgroup);
    }

    @ConfigureContext(session = khuntley, shouldCommitTransactions = true)
    public final void testGetCashReceipts1_knownVerificationUnit_mixedReceipts() throws Exception {
        final String workgroup = TEST_CAMPUS_CD;

        // clean up before testing
        denatureCashReceipts(workgroup);

        // create some CRs
        changeCurrentUser(UserNameFixture.ineff);
        CashReceiptDocument cr1 = buildCashReceiptDoc(workgroup, "ww2 CRST cr1", KFSConstants.DocumentStatusCodes.CashReceipt.INTERIM, new KualiDecimal("101.01"), new KualiDecimal("898.99"));

        CashReceiptDocument cr2 = buildCashReceiptDoc(workgroup, "ww2 CRST cr2", KFSConstants.DocumentStatusCodes.CashReceipt.VERIFIED, new KualiDecimal("212.12"), new KualiDecimal("787.87"));


        // verify that there are some of each
        List ireceipts = SpringContext.getBean(CashReceiptService.class).getCashReceipts(workgroup, KFSConstants.DocumentStatusCodes.CashReceipt.INTERIM);
        assertEquals(1, ireceipts.size());

        List vreceipts = SpringContext.getBean(CashReceiptService.class).getCashReceipts(workgroup, KFSConstants.DocumentStatusCodes.CashReceipt.VERIFIED);
        assertEquals(1, vreceipts.size());


        // clean up afterwards
        denatureCashReceipts(workgroup);
    }


    private static final String[] BOTH_STATII = { KFSConstants.DocumentStatusCodes.CashReceipt.VERIFIED, KFSConstants.DocumentStatusCodes.CashReceipt.INTERIM };
    private static final String[] ISTATII = { KFSConstants.DocumentStatusCodes.CashReceipt.INTERIM };
    private static final String[] VSTATII = { KFSConstants.DocumentStatusCodes.CashReceipt.VERIFIED };


    public final void testGetCashReceipts2_blankUnitName() {
        boolean failedAsExpected = false;

        try {
            SpringContext.getBean(CashReceiptService.class).getCashReceipts("   ", (String[]) null);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    public final void testGetCashReceipts2_nullStatii() {
        boolean failedAsExpected = false;

        try {
            SpringContext.getBean(CashReceiptService.class).getCashReceipts(TEST_CAMPUS_CD, (String[]) null);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    public final void testGetCashReceipts2_emptyStatii() {
        boolean failedAsExpected = false;

        String[] emptyStatii = {};
        try {
            SpringContext.getBean(CashReceiptService.class).getCashReceipts(TEST_CAMPUS_CD, emptyStatii);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    public final void testGetCashReceipts2_blankStatii() {
        boolean failedAsExpected = false;

        String[] blankStatii = { "  " };
        try {
            SpringContext.getBean(CashReceiptService.class).getCashReceipts(TEST_CAMPUS_CD, blankStatii);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    // TODO: once we stop returning default campus code for unknown unit, need tests for unknown unit

    public final void testGetCashReceipts2_knownVerificationUnit_noVerifiedReceipts() {
        final String workgroup = TEST_CAMPUS_CD;

        // clean up before testing
        denatureCashReceipts(workgroup);

        List receipts = SpringContext.getBean(CashReceiptService.class).getCashReceipts(workgroup, VSTATII);
        assertEquals(0, receipts.size());
    }

    public final void testGetCashReceipts2_knownVerificationUnit_noInterimReceipts() {
        final String workgroup = TEST_CAMPUS_CD;

        // clean up before testing
        denatureCashReceipts(workgroup);

        List receipts = SpringContext.getBean(CashReceiptService.class).getCashReceipts(workgroup, ISTATII);
        assertEquals(0, receipts.size());
    }


    @ConfigureContext(session = khuntley, shouldCommitTransactions = true)
    public final void testGetCashReceipts2_knownVerificationUnit_interimReceipts() throws Exception {
        final String workgroup = TEST_CAMPUS_CD;

        // clean up before testing
        denatureCashReceipts(workgroup);

        // create some CRs
        changeCurrentUser(UserNameFixture.ineff);
        CashReceiptDocument cr1 = buildCashReceiptDoc(workgroup, "ww2 CRST cr1", KFSConstants.DocumentStatusCodes.CashReceipt.INTERIM, new KualiDecimal("101.01"), new KualiDecimal("898.99"));

        CashReceiptDocument cr2 = buildCashReceiptDoc(workgroup, "ww2 CRST cr2", KFSConstants.DocumentStatusCodes.CashReceipt.INTERIM, new KualiDecimal("212.12"), new KualiDecimal("787.87"));


        // verify that there are only interim CRs
        List vreceipts = SpringContext.getBean(CashReceiptService.class).getCashReceipts(workgroup, VSTATII);
        assertEquals(0, vreceipts.size());

        List ireceipts = SpringContext.getBean(CashReceiptService.class).getCashReceipts(workgroup, ISTATII);
        assertEquals(2, ireceipts.size());

        // clean up afterwards
        denatureCashReceipts(workgroup);
    }

    @ConfigureContext(session = khuntley, shouldCommitTransactions = true)
    public final void testGetCashReceipts2_knownVerificationUnit_verifiedReceipts() throws Exception {
        final String workgroup = TEST_CAMPUS_CD;

        // clean up before testing
        denatureCashReceipts(workgroup);

        // create some CRs
        changeCurrentUser(UserNameFixture.ineff);
        CashReceiptDocument cr1 = buildCashReceiptDoc(workgroup, "ww2 CRST cr1", KFSConstants.DocumentStatusCodes.CashReceipt.VERIFIED, new KualiDecimal("101.01"), new KualiDecimal("898.99"));

        CashReceiptDocument cr2 = buildCashReceiptDoc(workgroup, "ww2 CRST cr2", KFSConstants.DocumentStatusCodes.CashReceipt.VERIFIED, new KualiDecimal("212.12"), new KualiDecimal("787.87"));


        // verify that there are only verified CRs
        List ireceipts = SpringContext.getBean(CashReceiptService.class).getCashReceipts(workgroup, ISTATII);
        assertEquals(0, ireceipts.size());

        List vreceipts = SpringContext.getBean(CashReceiptService.class).getCashReceipts(workgroup, VSTATII);
        assertEquals(2, vreceipts.size());

        // clean up afterwards
        denatureCashReceipts(workgroup);
    }

    @ConfigureContext(session = khuntley, shouldCommitTransactions = true)
    public final void testGetCashReceipts2_knownVerificationUnit_mixedReceipts() throws Exception {
        final String workgroup = TEST_CAMPUS_CD;

        // clean up before testing
        denatureCashReceipts(workgroup);

        // create some CRs
        changeCurrentUser(UserNameFixture.ineff);
        CashReceiptDocument cr1 = buildCashReceiptDoc(workgroup, "ww2 CRST cr1", KFSConstants.DocumentStatusCodes.CashReceipt.INTERIM, new KualiDecimal("101.01"), new KualiDecimal("898.99"));

        CashReceiptDocument cr2 = buildCashReceiptDoc(workgroup, "ww2 CRST cr2", KFSConstants.DocumentStatusCodes.CashReceipt.VERIFIED, new KualiDecimal("212.12"), new KualiDecimal("787.87"));


        // verify that there are some of each
        List ireceipts = SpringContext.getBean(CashReceiptService.class).getCashReceipts(workgroup, ISTATII);
        assertEquals(1, ireceipts.size());

        List vreceipts = SpringContext.getBean(CashReceiptService.class).getCashReceipts(workgroup, VSTATII);
        assertEquals(1, vreceipts.size());

        List mixedReceipts = SpringContext.getBean(CashReceiptService.class).getCashReceipts(workgroup, BOTH_STATII);
        assertEquals(2, mixedReceipts.size());

        // clean up afterwards
        denatureCashReceipts(workgroup);
    }


    private void denatureCashReceipts(String campusCode) {
        List verifiedReceipts = SpringContext.getBean(CashReceiptService.class).getCashReceipts(campusCode, BOTH_STATII);

        for (Iterator i = verifiedReceipts.iterator(); i.hasNext();) {
            CashReceiptDocument receipt = (CashReceiptDocument) i.next();
            receipt.getFinancialSystemDocumentHeader().setFinancialDocumentStatusCode("Z");
            SpringContext.getBean(DocumentService.class).updateDocument(receipt);
        }
    }

    private CashReceiptDocument buildCashReceiptDoc(String campusCode, String description, String status, KualiDecimal cashAmount, KualiDecimal checkAmount) throws WorkflowException {
        CashReceiptDocument crDoc = (CashReceiptDocument) SpringContext.getBean(DocumentService.class).getNewDocument(CashReceiptDocument.class);

        crDoc.getDocumentHeader().setDocumentDescription(description);
        crDoc.getFinancialSystemDocumentHeader().setFinancialDocumentStatusCode(status);

        crDoc.setCheckEntryMode(CashReceiptDocument.CHECK_ENTRY_TOTAL);
        crDoc.setTotalCashAmount(cashAmount);
        crDoc.setTotalCheckAmount(checkAmount);

        crDoc.setCampusLocationCode(campusCode);

        crDoc.addSourceAccountingLine(CashReceiptFamilyTestUtil.buildSourceAccountingLine(crDoc.getDocumentNumber(), crDoc.getPostingYear(), crDoc.getNextSourceLineNumber()));

        try {
            SpringContext.getBean(DocumentService.class).saveDocument(crDoc);
        }
        catch (ValidationException e) {
            // If the business rule evaluation fails then give us more info for debugging this test.
            fail(e.getMessage() + ", " + GlobalVariables.getMessageMap());
        }
        return crDoc;
    }
}

