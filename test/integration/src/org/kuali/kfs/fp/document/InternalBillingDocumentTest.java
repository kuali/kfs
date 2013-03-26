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
package org.kuali.kfs.fp.document;

import static org.kuali.kfs.sys.document.AccountingDocumentTestUtils.approveDocument;
import static org.kuali.kfs.sys.document.AccountingDocumentTestUtils.routeDocument;
import static org.kuali.kfs.sys.document.AccountingDocumentTestUtils.testGetNewDocument_byDocumentClass;
import static org.kuali.kfs.sys.fixture.AccountingLineFixture.LINE2;
import static org.kuali.kfs.sys.fixture.AccountingLineFixture.LINE3;
import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;
import static org.kuali.kfs.sys.fixture.UserNameFixture.rorenfro;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.DocumentTestUtils;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.businessobject.TargetAccountingLine;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.AccountingDocumentTestUtils;
import org.kuali.kfs.sys.fixture.AccountingLineFixture;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.kfs.sys.suite.AnnotationTestSuite;
import org.kuali.kfs.sys.suite.CrossSectionSuite;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.TransactionalDocumentDictionaryService;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.exception.DocumentAuthorizationException;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.PersistenceService;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * This class is used to test InternalBillingDocument.
 */
@ConfigureContext(session = khuntley)
public class InternalBillingDocumentTest extends KualiTestBase {
    protected static final Class<InternalBillingDocument> DOCUMENT_CLASS = InternalBillingDocument.class;

    protected Document getDocumentParameterFixture() throws Exception {
        return DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), InternalBillingDocument.class);
    }

    protected List<AccountingLineFixture> getTargetAccountingLineParametersFromFixtures() {
        List<AccountingLineFixture> list = new ArrayList<AccountingLineFixture>();
        list.add(LINE2);
        list.add(LINE3);
        list.add(LINE2);
        return list;
    }

    protected List<AccountingLineFixture> getSourceAccountingLineParametersFromFixtures() {
        List<AccountingLineFixture> list = new ArrayList<AccountingLineFixture>();
        list.add(LINE2);
        list.add(LINE3);
        list.add(LINE2);
        return list;
    }


    protected int getExpectedPrePeCount() {
        return 12;
    }

    @ConfigureContext(session = khuntley, shouldCommitTransactions = true)
    public final void testApprove_addAccessibleAccount_ChangingTotals() throws Exception {
        AccountingDocument retrieved;
        AccountingDocument original;
        String docId;

        // switch user to WESPRICE, build and route document with
        // accountingLines
        changeCurrentUser(getInitialUserName());
        original = buildDocument();
        routeDocument(original, SpringContext.getBean(DocumentService.class));
        docId = original.getDocumentNumber();

        // switch user to another user, add accountingLines for accounts not
        // controlled by this user
        changeCurrentUser(getTestUserName());
        retrieved = (AccountingDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(docId);
        retrieved.addSourceAccountingLine(getSourceAccountingLineAccessibleAccount());
        retrieved.addTargetAccountingLine(getTargetAccountingLineAccessibleAccount());

        boolean failedAsExpected = false;
        try {
            approveDocument(retrieved, SpringContext.getBean(DocumentService.class));
        }
        catch (ValidationException e) {
            failedAsExpected = true;
        }
        catch (DocumentAuthorizationException dae) {
            // this means that the workflow status didn't change in time for the check, so this is
            // an expected exception
            failedAsExpected = true;
        }
        assertTrue("document should have failed validation: " + dumpMessageMapErrors(),failedAsExpected);
    }

    @ConfigureContext(session = khuntley, shouldCommitTransactions = true)
    public final void testApprove_addInaccessibleAccount_sourceLine() throws Exception {
        // switch user to WESPRICE, build and route document with
        // accountingLines
        AccountingDocument original;
        AccountingDocument retrieved;
        String docId;

        changeCurrentUser(getInitialUserName());
        original = buildDocument();
        routeDocument(original, SpringContext.getBean(DocumentService.class));
        docId = original.getDocumentNumber();

        // switch user to AHORNICK, add sourceAccountingLine for account not controlled by this user
        // (and add a balancing targetAccountingLine for an accessible account)
        changeCurrentUser(getTestUserName());
        retrieved = (AccountingDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(docId);
        retrieved.addSourceAccountingLine(getSourceAccountingLineInaccessibleAccount());
        retrieved.addTargetAccountingLine(getTargetAccountingLineAccessibleAccount());

        // approve document, wait for failure b/c totals have changed
        boolean failedAsExpected = false;
        try {
            approveDocument(retrieved, SpringContext.getBean(DocumentService.class));
        }
        catch (ValidationException e) {
            failedAsExpected = true;
        }
        catch (DocumentAuthorizationException dae) {
            // this means that the workflow status didn't change in time for the check, so this is
            // an expected exception
            failedAsExpected = true;
        }
        assertTrue("document should have failed validation: " + dumpMessageMapErrors(),failedAsExpected);
    }

    @ConfigureContext(session = khuntley, shouldCommitTransactions = true)
    public final void testApprove_addInaccessibleAccount_targetLine() throws Exception {
        AccountingDocument retrieved;
        AccountingDocument original;
        String docId;

        // switch user to WESPRICE, build and route document with
        // accountingLines
        changeCurrentUser(getInitialUserName());
        original = buildDocument();
        routeDocument(original, SpringContext.getBean(DocumentService.class));
        docId = original.getDocumentNumber();

        // switch user to AHORNICK, add targetAccountingLine for accounts not
        // controlled by this user
        // (and add a balancing sourceAccountingLine for an accessible account)
        changeCurrentUser(getTestUserName());
        retrieved = (AccountingDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(docId);
        retrieved.addTargetAccountingLine(getTargetAccountingLineInaccessibleAccount());
        retrieved.addSourceAccountingLine(getSourceAccountingLineAccessibleAccount());

        // approve document, wait for failure
        boolean failedAsExpected = false;
        try {
            approveDocument(retrieved, SpringContext.getBean(DocumentService.class));
        }
        catch (ValidationException e) {
            failedAsExpected = true;
        }
        catch (DocumentAuthorizationException dae) {
            // this means that the workflow status didn't change in time for the check, so this is
            // an expected exception
            failedAsExpected = true;
        }
        assertTrue("document should have failed validation: " + dumpMessageMapErrors(),failedAsExpected);
    }

    /**
     * This method...
     * @throws Exception
     */
    @ConfigureContext(session = khuntley, shouldCommitTransactions = true)
    public final void testApprove_deleteAccessibleAccount() throws Exception {
        // switch user to WESPRICE, build and route document with
        // accountingLines
        AccountingDocument retrieved;
        AccountingDocument original;
        String docId;

        changeCurrentUser(getInitialUserName());
        original = buildDocument();
        routeDocument(original, SpringContext.getBean(DocumentService.class));
        docId = original.getDocumentNumber();

        // switch user to AHORNICK, delete sourceAccountingLine for accounts
        // controlled by this user
        // (and delete matching targetAccountingLine, for balance)
        changeCurrentUser(getTestUserName());
        retrieved = (AccountingDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(docId);
        deleteSourceAccountingLine(retrieved, 0);
        deleteTargetAccountingLine(retrieved, 0);

        // approve document, wait for failure b/c totals have changed
        boolean failedAsExpected = false;
        try {
            approveDocument(retrieved, SpringContext.getBean(DocumentService.class));
        }
        catch (ValidationException e) {
            failedAsExpected = true;
        }
        catch (DocumentAuthorizationException dae) {
            // this means that the workflow status didn't change in time for the check, so this is
            // an expected exception
            failedAsExpected = true;
        }
        assertTrue("document should have failed validation: " + dumpMessageMapErrors(),failedAsExpected);
    }

    @AnnotationTestSuite(CrossSectionSuite.class)
    @ConfigureContext(session = khuntley, shouldCommitTransactions = true)
    public final void testApprove_updateAccessibleAccount() throws Exception {
        // switch user to WESPRICE, build and route document with
        // accountingLines
        AccountingDocument retrieved;
        AccountingDocument original;
        String docId;

        changeCurrentUser(getInitialUserName());
        original = buildDocument();
        routeDocument(original, SpringContext.getBean(DocumentService.class));
        docId = original.getDocumentNumber();

        // switch user to RORENFRO, update sourceAccountingLine for accounts
        // controlled by this user
        // (and delete update targetAccountingLine, for balance)
        changeCurrentUser(getTestUserName());
        retrieved = (AccountingDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(docId);

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
            approveDocument(retrieved, SpringContext.getBean(DocumentService.class));
        }
        catch (DocumentAuthorizationException dae) {
            // this means that the workflow status didn't change in time for the check, so this is
            // an expected exception
        } catch ( ValidationException ex ) {
            fail( "Business Rules Failed: " + dumpMessageMapErrors() );
        }
    }

    @ConfigureContext(session = khuntley, shouldCommitTransactions = true)
    public final void testApprove_updateInaccessibleAccount_sourceLine() throws Exception {
        AccountingDocument retrieved;
        AccountingDocument original;
        String docId;

        // switch user to WESPRICE, build and route document with
        // accountingLines
        changeCurrentUser(getInitialUserName());
        original = buildDocument();
        routeDocument(original, SpringContext.getBean(DocumentService.class));
        docId = original.getDocumentNumber();

        // switch user to AHORNICK, update sourceAccountingLines for accounts
        // not controlled by this user
        // (and update matching accessible targetLine, for balance)
        changeCurrentUser(getTestUserName());
        retrieved = (AccountingDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(docId);
        updateSourceAccountingLine(retrieved, 1, "3.14");
        updateTargetAccountingLine(retrieved, 0, "3.14");

        // clear the OJB cache, otherwise when the original is retrieved later, it matches the updated doc and the
        // system doesn't think an update occurred.
        SpringContext.getBean(PersistenceService.class, "persistenceServiceOjb").clearCache();

        // approve document, wait for failure
        boolean failedAsExpected = false;
        try {
            approveDocument(retrieved, SpringContext.getBean(DocumentService.class));
        }
        catch (ValidationException e) {
            failedAsExpected = GlobalVariables.getMessageMap().containsMessageKey(KFSKeyConstants.ERROR_ACCOUNTINGLINE_INACCESSIBLE_UPDATE);
        }
        assertTrue("document should have failed validation: " + dumpMessageMapErrors(),failedAsExpected);
    }

    @ConfigureContext(session = khuntley, shouldCommitTransactions = true)
    public final void testApprove_updateInaccessibleAccount_targetLine() throws Exception {
        // switch user to WESPRICE, build and route document with
        // accountingLines
        AccountingDocument retrieved;
        AccountingDocument original;
        String docId;

        changeCurrentUser(getInitialUserName());
        original = buildDocument();
        routeDocument(original, SpringContext.getBean(DocumentService.class));
        docId = original.getDocumentNumber();

        // switch user to AHORNICK, update targetAccountingLine for accounts
        // not controlled by this user
        // (and update matching accessible sourceLine, for balance)
        changeCurrentUser(getTestUserName());
        retrieved = (AccountingDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(docId);
        updateTargetAccountingLine(retrieved, 1, "2.81");
        updateSourceAccountingLine(retrieved, 0, "2.81");

        // clear the OJB cache, otherwise when the original is retrieved later, it matches the updated doc and the
        // system doesn't think an update occurred.
        SpringContext.getBean(PersistenceService.class, "persistenceServiceOjb").clearCache();

        // approve document, wait for failure
        boolean failedAsExpected = false;
        try {
            approveDocument(retrieved, SpringContext.getBean(DocumentService.class));
        }
        catch (ValidationException e) {
            failedAsExpected = GlobalVariables.getMessageMap().containsMessageKey(KFSKeyConstants.ERROR_ACCOUNTINGLINE_INACCESSIBLE_UPDATE);
        }
        catch (DocumentAuthorizationException dae) {
            // this means that the workflow status didn't change in time for the check, so this is
            // an expected exception
            failedAsExpected = true;
        }
        assertTrue( "Test did not fail as expected: " + dumpMessageMapErrors(), failedAsExpected);
    }


    public final void testAddAccountingLine() throws Exception {
        List<SourceAccountingLine> sourceLines = generateSouceAccountingLines();
        List<TargetAccountingLine> targetLines = generateTargetAccountingLines();
        int expectedSourceTotal = sourceLines.size();
        int expectedTargetTotal = targetLines.size();
        AccountingDocumentTestUtils.testAddAccountingLine(DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), DOCUMENT_CLASS), sourceLines, targetLines, expectedSourceTotal, expectedTargetTotal);
    }


    public final void testGetNewDocument() throws Exception {
        testGetNewDocument_byDocumentClass(DOCUMENT_CLASS, SpringContext.getBean(DocumentService.class));
    }

    public final void testConvertIntoCopy_copyDisallowed() throws Exception {
        AccountingDocumentTestUtils.testConvertIntoCopy_copyDisallowed(buildDocument(), SpringContext.getBean(DataDictionaryService.class));

    }

    public final void testConvertIntoErrorCorrection_documentAlreadyCorrected() throws Exception {
        AccountingDocumentTestUtils.testConvertIntoErrorCorrection_documentAlreadyCorrected(buildDocument(), SpringContext.getBean(TransactionalDocumentDictionaryService.class));
    }

    public final void testConvertIntoErrorCorrection_errorCorrectionDisallowed() throws Exception {
        AccountingDocumentTestUtils.testConvertIntoErrorCorrection_errorCorrectionDisallowed(buildDocument(), SpringContext.getBean(DataDictionaryService.class));
    }

    @ConfigureContext(session = khuntley, shouldCommitTransactions = true)
    public final void testConvertIntoErrorCorrection() throws Exception {
        AccountingDocumentTestUtils.testConvertIntoErrorCorrection(buildDocument(), getExpectedPrePeCount(), SpringContext.getBean(DocumentService.class), SpringContext.getBean(TransactionalDocumentDictionaryService.class));
    }

    @ConfigureContext(session = khuntley, shouldCommitTransactions = true)
    public final void testRouteDocument() throws Exception {
        AccountingDocumentTestUtils.testRouteDocument(buildDocument(), SpringContext.getBean(DocumentService.class));
    }

    @ConfigureContext(session = khuntley, shouldCommitTransactions = true)
    public final void testSaveDocument() throws Exception {
        AccountingDocumentTestUtils.testSaveDocument(buildDocument(), SpringContext.getBean(DocumentService.class));
    }

    @ConfigureContext(session = khuntley, shouldCommitTransactions = true)
    public final void testConvertIntoCopy() throws Exception {
        AccountingDocumentTestUtils.testConvertIntoCopy(buildDocument(), SpringContext.getBean(DocumentService.class), getExpectedPrePeCount());
    }


    // test util methods
    protected List<SourceAccountingLine> generateSouceAccountingLines() throws Exception {
        List<SourceAccountingLine> sourceLines = new ArrayList<SourceAccountingLine>();
        // set accountinglines to document
        for (AccountingLineFixture sourceFixture : getSourceAccountingLineParametersFromFixtures()) {
            sourceLines.add(sourceFixture.createSourceAccountingLine());
        }

        return sourceLines;
    }

    protected List<TargetAccountingLine> generateTargetAccountingLines() throws Exception {
        List<TargetAccountingLine> targetLines = new ArrayList<TargetAccountingLine>();
        for (AccountingLineFixture targetFixture : getTargetAccountingLineParametersFromFixtures()) {
            targetLines.add(targetFixture.createTargetAccountingLine());
        }

        return targetLines;
    }

    protected InternalBillingDocument buildDocument() throws Exception {
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

    protected void updateSourceAccountingLine(AccountingDocument document, int index, String newAmount) {
        SourceAccountingLine sourceLine = document.getSourceAccountingLine(index);
        sourceLine.setAmount(new KualiDecimal(newAmount));
    }

    protected void updateTargetAccountingLine(AccountingDocument document, int index, String newAmount) {
        TargetAccountingLine targetLine = document.getTargetAccountingLine(index);
        targetLine.setAmount(new KualiDecimal(newAmount));
    }


    protected void deleteSourceAccountingLine(AccountingDocument document, int index) {
        List sourceLines = document.getSourceAccountingLines();
        sourceLines.remove(index);
        document.setSourceAccountingLines(sourceLines);
    }

    protected void deleteTargetAccountingLine(AccountingDocument document, int index) {
        List targetLines = document.getTargetAccountingLines();
        targetLines.remove(index);
        document.setTargetAccountingLines(targetLines);
    }

    protected UserNameFixture getInitialUserName() {
        return khuntley; // replace rjweiss with khuntley
    }

    protected UserNameFixture getTestUserName() {
        return rorenfro;
    }

    protected SourceAccountingLine getSourceAccountingLineAccessibleAccount() throws Exception {
        return LINE2.createSourceAccountingLine();
    }

    protected TargetAccountingLine getTargetAccountingLineInaccessibleAccount() throws Exception {
        return LINE3.createTargetAccountingLine();
    }

    protected TargetAccountingLine getTargetAccountingLineAccessibleAccount() throws Exception {
        return LINE2.createTargetAccountingLine();
    }

    protected SourceAccountingLine getSourceAccountingLineInaccessibleAccount() throws Exception {
        return LINE3.createSourceAccountingLine();
    }

}

