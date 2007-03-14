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
package org.kuali.module.financial.document;

import static org.kuali.kfs.util.SpringServiceLocator.getDataDictionaryService;
import static org.kuali.kfs.util.SpringServiceLocator.getDocumentService;
import static org.kuali.kfs.util.SpringServiceLocator.getTransactionalDocumentDictionaryService;
import static org.kuali.module.financial.document.AccountingDocumentTestUtils.approveDocument;
import static org.kuali.module.financial.document.AccountingDocumentTestUtils.routeDocument;
import static org.kuali.module.financial.document.AccountingDocumentTestUtils.testGetNewDocument_byDocumentClass;
import static org.kuali.test.fixtures.AccountingLineFixture.LINE2;
import static org.kuali.test.fixtures.AccountingLineFixture.LINE3;
import static org.kuali.test.fixtures.UserNameFixture.KHUNTLEY;
import static org.kuali.test.fixtures.UserNameFixture.RJWEISS;
import static org.kuali.test.fixtures.UserNameFixture.RORENFRO;

import java.util.ArrayList;
import java.util.List;

import org.kuali.KeyConstants;
import org.kuali.core.document.Document;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.exceptions.DocumentAuthorizationException;
import org.kuali.core.exceptions.ValidationException;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.bo.TargetAccountingLine;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.test.DocumentTestUtils;
import org.kuali.test.KualiTestBase;
import org.kuali.test.TestsWorkflowViaDatabase;
import org.kuali.test.WithTestSpringContext;
import org.kuali.test.fixtures.AccountingLineFixture;
import org.kuali.test.fixtures.UserNameFixture;
import org.kuali.test.suite.AnnotationTestSuite;
import org.kuali.test.suite.CrossSectionSuite;

/**
 * This class is used to test InternalBillingDocument.
 * 
 * 
 */
@WithTestSpringContext(session = KHUNTLEY)
public class InternalBillingDocumentTest extends KualiTestBase {
    public static final Class<InternalBillingDocument> DOCUMENT_CLASS = InternalBillingDocument.class;

    private Document getDocumentParameterFixture() throws Exception {
        return DocumentTestUtils.createDocument(getDocumentService(), InternalBillingDocument.class);
    }

    private List<AccountingLineFixture> getTargetAccountingLineParametersFromFixtures() {
        List<AccountingLineFixture> list = new ArrayList<AccountingLineFixture>();
        list.add(LINE2);
        list.add(LINE3);
        list.add(LINE2);
        return list;
    }

    private List<AccountingLineFixture> getSourceAccountingLineParametersFromFixtures() {
        List<AccountingLineFixture> list = new ArrayList<AccountingLineFixture>();
        list.add(LINE2);
        list.add(LINE3);
        list.add(LINE2);
        return list;
    }


    private int getExpectedPrePeCount() {
        return 12;
    }

    @TestsWorkflowViaDatabase
    public final void testApprove_addAccessibleAccount_ChangingTotals() throws Exception {
        AccountingDocument retrieved;
        AccountingDocument original;
        String docId;

        // switch user to WESPRICE, build and route document with
        // accountingLines
        changeCurrentUser(getInitialUserName());
        original = buildDocument();
        routeDocument(original, getDocumentService());
        docId = original.getDocumentNumber();

        // switch user to another user, add accountingLines for accounts not
        // controlled by this user
        changeCurrentUser(getTestUserName());
        retrieved = (AccountingDocument) getDocumentService().getByDocumentHeaderId(docId);
        retrieved.addSourceAccountingLine(getSourceAccountingLineAccessibleAccount());
        retrieved.addTargetAccountingLine(getTargetAccountingLineAccessibleAccount());

        boolean failedAsExpected = false;
        try {
            approveDocument(retrieved, getDocumentService());
        }
        catch (ValidationException e) {
            failedAsExpected = true;
        }
        catch (DocumentAuthorizationException dae) {
            // this means that the workflow status didn't change in time for the check, so this is
            // an expected exception
            failedAsExpected = true;
        }
        assertTrue(failedAsExpected);
    }

    @TestsWorkflowViaDatabase
    public final void testApprove_addInaccessibleAccount_sourceLine() throws Exception {
        // switch user to WESPRICE, build and route document with
        // accountingLines
        AccountingDocument original;
        AccountingDocument retrieved;
        String docId;

        changeCurrentUser(getInitialUserName());
        original = buildDocument();
        routeDocument(original, getDocumentService());
        docId = original.getDocumentNumber();

        // switch user to AHORNICK, add sourceAccountingLine for account not controlled by this user
        // (and add a balancing targetAccountingLine for an accessible account)
        changeCurrentUser(getTestUserName());
        retrieved = (AccountingDocument) getDocumentService().getByDocumentHeaderId(docId);
        retrieved.addSourceAccountingLine(getSourceAccountingLineInaccessibleAccount());
        retrieved.addTargetAccountingLine(getTargetAccountingLineAccessibleAccount());

        // approve document, wait for failure b/c totals have changed
        boolean failedAsExpected = false;
        try {
            approveDocument(retrieved, getDocumentService());
        }
        catch (ValidationException e) {
            failedAsExpected = true;
        }
        catch (DocumentAuthorizationException dae) {
            // this means that the workflow status didn't change in time for the check, so this is
            // an expected exception
            failedAsExpected = true;
        }
        assertTrue(failedAsExpected);
    }

    @TestsWorkflowViaDatabase
    public final void testApprove_addInaccessibleAccount_targetLine() throws Exception {
        AccountingDocument retrieved;
        AccountingDocument original;
        String docId;

        // switch user to WESPRICE, build and route document with
        // accountingLines
        changeCurrentUser(getInitialUserName());
        original = buildDocument();
        routeDocument(original, getDocumentService());
        docId = original.getDocumentNumber();

        // switch user to AHORNICK, add targetAccountingLine for accounts not
        // controlled by this user
        // (and add a balancing sourceAccountingLine for an accessible account)
        changeCurrentUser(getTestUserName());
        retrieved = (AccountingDocument) getDocumentService().getByDocumentHeaderId(docId);
        retrieved.addTargetAccountingLine(getTargetAccountingLineInaccessibleAccount());
        retrieved.addSourceAccountingLine(getSourceAccountingLineAccessibleAccount());

        // approve document, wait for failure
        boolean failedAsExpected = false;
        try {
            approveDocument(retrieved, getDocumentService());
        }
        catch (ValidationException e) {
            failedAsExpected = true;
        }
        catch (DocumentAuthorizationException dae) {
            // this means that the workflow status didn't change in time for the check, so this is
            // an expected exception
            failedAsExpected = true;
        }
        assertTrue(failedAsExpected);
    }

    @TestsWorkflowViaDatabase
    public final void testApprove_deleteAccessibleAccount() throws Exception {
        // switch user to WESPRICE, build and route document with
        // accountingLines
        AccountingDocument retrieved;
        AccountingDocument original;
        String docId;

        changeCurrentUser(getInitialUserName());
        original = buildDocument();
        routeDocument(original, getDocumentService());
        docId = original.getDocumentNumber();

        // switch user to AHORNICK, delete sourceAccountingLine for accounts
        // controlled by this user
        // (and delete matching targetAccountingLine, for balance)
        changeCurrentUser(getTestUserName());
        retrieved = (AccountingDocument) getDocumentService().getByDocumentHeaderId(docId);
        deleteSourceAccountingLine(retrieved, 0);
        deleteTargetAccountingLine(retrieved, 0);

        // approve document, wait for failure b/c totals have changed
        boolean failedAsExpected = false;
        try {
            approveDocument(retrieved, getDocumentService());
        }
        catch (ValidationException e) {
            failedAsExpected = true;
        }
        catch (DocumentAuthorizationException dae) {
            // this means that the workflow status didn't change in time for the check, so this is
            // an expected exception
            failedAsExpected = true;
        }
        assertTrue(failedAsExpected);
    }

    @TestsWorkflowViaDatabase
    public final void testApprove_deleteInaccessibleAccount_sourceLine() throws Exception {
        // switch user to WESPRICE, build and route document with
        // accountingLines
        AccountingDocument retrieved;
        AccountingDocument original;
        String docId;

        changeCurrentUser(getInitialUserName());
        original = buildDocument();
        routeDocument(original, getDocumentService());
        docId = original.getDocumentNumber();

        // switch user to AHORNICK, delete sourceAccountingLines for accounts
        // not controlled by this user
        // (and delete matching accessible targetLine, for balance)
        changeCurrentUser(getTestUserName());
        retrieved = (AccountingDocument) getDocumentService().getByDocumentHeaderId(docId);
        deleteSourceAccountingLine(retrieved, 1);
        deleteTargetAccountingLine(retrieved, 0);

        // approve document, wait for failure
        boolean failedAsExpected = false;
        try {
            approveDocument(retrieved, getDocumentService());
        }
        catch (ValidationException e) {
            failedAsExpected = GlobalVariables.getErrorMap().containsMessageKey(KeyConstants.ERROR_ACCOUNTINGLINE_INACCESSIBLE_DELETE);
        }
        catch (DocumentAuthorizationException dae) {
            // this means that the workflow status didn't change in time for the check, so this is
            // an expected exception
            failedAsExpected = true;
        }
        assertTrue(failedAsExpected);
    }

    @TestsWorkflowViaDatabase
    public final void testApprove_deleteInaccessibleAccount_targetLine() throws Exception {
        AccountingDocument retrieved;
        AccountingDocument original;
        String docId;

        // switch user to WESPRICE, build and route document with
        // accountingLines
        changeCurrentUser(getInitialUserName());
        original = buildDocument();
        routeDocument(original, getDocumentService());
        docId = original.getDocumentNumber();

        // switch user to AHORNICK, delete targetAccountingLine for accounts not controlled by this user
        // (and delete matching accessible sourceLine, for balance)
        changeCurrentUser(getTestUserName());
        retrieved = (AccountingDocument) getDocumentService().getByDocumentHeaderId(docId);
        deleteTargetAccountingLine(retrieved, 1);
        deleteSourceAccountingLine(retrieved, 0);

        // approve document, wait for failure
        boolean failedAsExpected = false;
        try {
            approveDocument(retrieved, getDocumentService());
        }
        catch (ValidationException e) {
            failedAsExpected = GlobalVariables.getErrorMap().containsMessageKey(KeyConstants.ERROR_ACCOUNTINGLINE_INACCESSIBLE_DELETE);
        }
        catch (DocumentAuthorizationException dae) {
            // this means that the workflow status didn't change in time for the check, so this is
            // an expected exception
            failedAsExpected = true;
        }
        assertTrue(failedAsExpected);
    }

    @TestsWorkflowViaDatabase
    public final void testApprove_deleteLastAccessibleAccount() throws Exception {
        // switch user to WESPRICE, build and route document with
        // accountingLines
        AccountingDocument retrieved;
        AccountingDocument original;
        String docId;

        changeCurrentUser(getInitialUserName());
        original = buildDocument();
        routeDocument(original, getDocumentService());
        docId = original.getDocumentNumber();

        // switch user to AHORNICK, delete all accountingLines for that user
        changeCurrentUser(getTestUserName());
        retrieved = (AccountingDocument) getDocumentService().getByDocumentHeaderId(docId);
        deleteSourceAccountingLine(retrieved, 2);
        deleteSourceAccountingLine(retrieved, 0);

        deleteTargetAccountingLine(retrieved, 2);
        deleteTargetAccountingLine(retrieved, 0);

        // approve document, wait for failure
        boolean failedAsExpected = false;
        try {
            approveDocument(retrieved, getDocumentService());
        }
        catch (ValidationException e) {
            failedAsExpected = GlobalVariables.getErrorMap().containsMessageKey(KeyConstants.ERROR_ACCOUNTINGLINE_LASTACCESSIBLE_DELETE);
        }
        catch (DocumentAuthorizationException dae) {
            // this means that the workflow status didn't change in time for the check, so this is
            // an expected exception
            failedAsExpected = true;
        }
        assertTrue(failedAsExpected);
    }


    @AnnotationTestSuite(CrossSectionSuite.class)
    @TestsWorkflowViaDatabase
    public final void testApprove_updateAccessibleAccount() throws Exception {
        // switch user to WESPRICE, build and route document with
        // accountingLines
        AccountingDocument retrieved;
        AccountingDocument original;
        String docId;

        changeCurrentUser(getInitialUserName());
        original = buildDocument();
        routeDocument(original, getDocumentService());
        docId = original.getDocumentNumber();

        // switch user to AHORNICK, update sourceAccountingLine for accounts
        // controlled by this user
        // (and delete update targetAccountingLine, for balance)
        changeCurrentUser(getTestUserName());
        retrieved = (AccountingDocument) getDocumentService().getByDocumentHeaderId(docId);

        // make sure totals don't change
        KualiDecimal originalSourceLineAmt = retrieved.getSourceAccountingLine(0).getAmount();
        KualiDecimal originalTargetLineAmt = retrieved.getTargetAccountingLine(0).getAmount();
        KualiDecimal newSourceLineAmt = originalSourceLineAmt.divide(new KualiDecimal(2));
        KualiDecimal newTargetLineAmt = originalTargetLineAmt.divide(new KualiDecimal(2));

        // update existing lines with new amount which equals orig divided by 2
        updateSourceAccountingLine(retrieved, 0, newSourceLineAmt.toString());
        updateTargetAccountingLine(retrieved, 0, newTargetLineAmt.toString());

        // add in new lines and change amounts
        TargetAccountingLine newTargetLine = getTargetAccountingLineAccessibleAccount();
        newTargetLine.setAmount(newTargetLineAmt);
        SourceAccountingLine newSourceLine = getSourceAccountingLineAccessibleAccount();
        newSourceLine.setAmount(newSourceLineAmt);
        retrieved.addTargetAccountingLine(newTargetLine);
        retrieved.addSourceAccountingLine(newSourceLine);

        try {
            approveDocument(retrieved, getDocumentService());
        }
        catch (DocumentAuthorizationException dae) {
            // this means that the workflow status didn't change in time for the check, so this is
            // an expected exception
        }
    }

    @TestsWorkflowViaDatabase
    public final void testApprove_updateInaccessibleAccount_sourceLine() throws Exception {
        AccountingDocument retrieved;
        AccountingDocument original;
        String docId;

        // switch user to WESPRICE, build and route document with
        // accountingLines
        changeCurrentUser(getInitialUserName());
        original = buildDocument();
        routeDocument(original, getDocumentService());
        docId = original.getDocumentNumber();

        // switch user to AHORNICK, update sourceAccountingLines for accounts
        // not controlled by this user
        // (and update matching accessible targetLine, for balance)
        changeCurrentUser(getTestUserName());
        retrieved = (AccountingDocument) getDocumentService().getByDocumentHeaderId(docId);
        updateSourceAccountingLine(retrieved, 1, "3.14");
        updateTargetAccountingLine(retrieved, 0, "3.14");

        // approve document, wait for failure
        boolean failedAsExpected = false;
        try {
            approveDocument(retrieved, getDocumentService());
        }
        catch (ValidationException e) {
            failedAsExpected = GlobalVariables.getErrorMap().containsMessageKey(KeyConstants.ERROR_ACCOUNTINGLINE_INACCESSIBLE_UPDATE);
        }
        catch (DocumentAuthorizationException dae) {
            // this means that the workflow status didn't change in time for the check, so this is
            // an expected exception
            failedAsExpected = true;
        }
        assertTrue(failedAsExpected);
    }

    @TestsWorkflowViaDatabase
    public final void testApprove_updateInaccessibleAccount_targetLine() throws Exception {
        // switch user to WESPRICE, build and route document with
        // accountingLines
        AccountingDocument retrieved;
        AccountingDocument original;
        String docId;

        changeCurrentUser(getInitialUserName());
        original = buildDocument();
        routeDocument(original, getDocumentService());
        docId = original.getDocumentNumber();

        // switch user to AHORNICK, update targetAccountingLine for accounts
        // not controlled by this user
        // (and update matching accessible sourceLine, for balance)
        changeCurrentUser(getTestUserName());
        retrieved = (AccountingDocument) getDocumentService().getByDocumentHeaderId(docId);
        updateTargetAccountingLine(retrieved, 1, "2.81");
        updateSourceAccountingLine(retrieved, 0, "2.81");

        // approve document, wait for failure
        boolean failedAsExpected = false;
        try {
            approveDocument(retrieved, getDocumentService());
        }
        catch (ValidationException e) {
            failedAsExpected = GlobalVariables.getErrorMap().containsMessageKey(KeyConstants.ERROR_ACCOUNTINGLINE_INACCESSIBLE_UPDATE);
        }
        catch (DocumentAuthorizationException dae) {
            // this means that the workflow status didn't change in time for the check, so this is
            // an expected exception
            failedAsExpected = true;
        }
        assertTrue(failedAsExpected);
    }


    public final void testAddAccountingLine() throws Exception {
        List<SourceAccountingLine> sourceLines = generateSouceAccountingLines();
        List<TargetAccountingLine> targetLines = generateTargetAccountingLines();
        int expectedSourceTotal = sourceLines.size();
        int expectedTargetTotal = targetLines.size();
        AccountingDocumentTestUtils.testAddAccountingLine(DocumentTestUtils.createDocument(getDocumentService(), DOCUMENT_CLASS), sourceLines, targetLines, expectedSourceTotal, expectedTargetTotal);
    }


    public final void testGetNewDocument() throws Exception {
        testGetNewDocument_byDocumentClass(DOCUMENT_CLASS, getDocumentService());
    }

    public final void testConvertIntoCopy_copyDisallowed() throws Exception {
        AccountingDocumentTestUtils.testConvertIntoCopy_copyDisallowed(buildDocument(), getDataDictionaryService());

    }

    public final void testConvertIntoErrorCorrection_documentAlreadyCorrected() throws Exception {
        AccountingDocumentTestUtils.testConvertIntoErrorCorrection_documentAlreadyCorrected(buildDocument(), getTransactionalDocumentDictionaryService());
    }

    public final void testConvertIntoErrorCorrection_errorCorrectionDisallowed() throws Exception {
        AccountingDocumentTestUtils.testConvertIntoErrorCorrection_errorCorrectionDisallowed(buildDocument(), getDataDictionaryService());
    }

    @TestsWorkflowViaDatabase
    public final void testConvertIntoErrorCorrection() throws Exception {
        AccountingDocumentTestUtils.testConvertIntoErrorCorrection(buildDocument(), getExpectedPrePeCount(), getDocumentService(), getTransactionalDocumentDictionaryService());
    }

    @TestsWorkflowViaDatabase
    public final void testRouteDocument() throws Exception {
        AccountingDocumentTestUtils.testRouteDocument(buildDocument(), getDocumentService());
    }

    @TestsWorkflowViaDatabase
    public final void testSaveDocument() throws Exception {
        AccountingDocumentTestUtils.testSaveDocument(buildDocument(), getDocumentService());
    }

    @TestsWorkflowViaDatabase
    public final void testConvertIntoCopy() throws Exception {
        AccountingDocumentTestUtils.testConvertIntoCopy(buildDocument(), getDocumentService(), getExpectedPrePeCount());
    }


    // test util methods
    private List<SourceAccountingLine> generateSouceAccountingLines() throws Exception {
        List<SourceAccountingLine> sourceLines = new ArrayList<SourceAccountingLine>();
        // set accountinglines to document
        for (AccountingLineFixture sourceFixture : getSourceAccountingLineParametersFromFixtures()) {
            sourceLines.add(sourceFixture.createSourceAccountingLine());
        }

        return sourceLines;
    }

    private List<TargetAccountingLine> generateTargetAccountingLines() throws Exception {
        List<TargetAccountingLine> targetLines = new ArrayList<TargetAccountingLine>();
        for (AccountingLineFixture targetFixture : getTargetAccountingLineParametersFromFixtures()) {
            targetLines.add(targetFixture.createTargetAccountingLine());
        }

        return targetLines;
    }

    private InternalBillingDocument buildDocument() throws Exception {
        // put accounting lines into document parameter for later
        InternalBillingDocument document = (InternalBillingDocument) getDocumentParameterFixture();

        // set accountinglines to document
        for (AccountingLineFixture sourceFixture : getSourceAccountingLineParametersFromFixtures()) {
            sourceFixture.addAsSourceTo(document);
        }

        for (AccountingLineFixture targetFixture : getTargetAccountingLineParametersFromFixtures()) {
            targetFixture.addAsTargetTo(document);
        }

        return document;
    }

    private void updateSourceAccountingLine(AccountingDocument document, int index, String newAmount) {
        SourceAccountingLine sourceLine = document.getSourceAccountingLine(index);
        sourceLine.setAmount(new KualiDecimal(newAmount));
    }

    private void updateTargetAccountingLine(AccountingDocument document, int index, String newAmount) {
        TargetAccountingLine targetLine = document.getTargetAccountingLine(index);
        targetLine.setAmount(new KualiDecimal(newAmount));
    }


    private void deleteSourceAccountingLine(AccountingDocument document, int index) {
        List sourceLines = document.getSourceAccountingLines();
        sourceLines.remove(index);
        document.setSourceAccountingLines(sourceLines);
    }

    private void deleteTargetAccountingLine(AccountingDocument document, int index) {
        List targetLines = document.getTargetAccountingLines();
        targetLines.remove(index);
        document.setTargetAccountingLines(targetLines);
    }

    private UserNameFixture getInitialUserName() {
        return RJWEISS;
    }

    protected UserNameFixture getTestUserName() {
        return RORENFRO;
    }

    private SourceAccountingLine getSourceAccountingLineAccessibleAccount() throws Exception {
        return LINE2.createSourceAccountingLine();
    }

    private TargetAccountingLine getTargetAccountingLineInaccessibleAccount() throws Exception {
        return LINE3.createTargetAccountingLine();
    }

    private TargetAccountingLine getTargetAccountingLineAccessibleAccount() throws Exception {
        return LINE2.createTargetAccountingLine();
    }

    private SourceAccountingLine getSourceAccountingLineInaccessibleAccount() throws Exception {
        return LINE3.createSourceAccountingLine();
    }

}
