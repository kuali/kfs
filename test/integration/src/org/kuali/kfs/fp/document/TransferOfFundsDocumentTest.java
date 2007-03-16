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
import static org.kuali.kfs.util.SpringServiceLocator.getAccountingPeriodService;
import static org.kuali.module.financial.document.AccountingDocumentTestUtils.testGetNewDocument_byDocumentClass;
import static org.kuali.module.financial.document.AccountingDocumentTestUtils.routeDocument;
import static org.kuali.test.fixtures.AccountingLineFixture.LINE1;
import static org.kuali.test.fixtures.UserNameFixture.CSWINSON;
import static org.kuali.test.fixtures.UserNameFixture.DFOGLE;
import static org.kuali.test.fixtures.UserNameFixture.KHUNTLEY;
import static org.kuali.test.fixtures.UserNameFixture.RORENFRO;
import static org.kuali.test.fixtures.UserNameFixture.RRUFFNER;
import static org.kuali.test.fixtures.UserNameFixture.SEASON;
import static org.kuali.test.fixtures.UserNameFixture.VPUTMAN;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.document.Document;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.bo.TargetAccountingLine;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.test.DocumentTestUtils;
import org.kuali.test.KualiTestBase;
import org.kuali.test.TestsWorkflowViaDatabase;
import org.kuali.test.WithTestSpringContext;
import org.kuali.test.fixtures.AccountingLineFixture;
import org.kuali.test.fixtures.UserNameFixture;
import org.kuali.workflow.WorkflowTestUtils;
import org.kuali.test.suite.RelatesTo;

import edu.iu.uis.eden.EdenConstants;

/**
 * This class is used to test TransferOfFundsDocument.
 * 
 * 
 */
@WithTestSpringContext(session = KHUNTLEY)
public class TransferOfFundsDocumentTest extends KualiTestBase {
    public static final Class<TransferOfFundsDocument> DOCUMENT_CLASS = TransferOfFundsDocument.class;

    // The set of Route Nodes that the test document will progress through
    private static final String ACCOUNT_REVIEW = "Account Review";
    private static final String ORG_REVIEW = "Org Review";


    private Document getDocumentParameterFixture() throws Exception {
        return DocumentTestUtils.createDocument(getDocumentService(), TransferOfFundsDocument.class);
    }

    private List<AccountingLineFixture> getTargetAccountingLineParametersFromFixtures() {
        List<AccountingLineFixture> list = new ArrayList<AccountingLineFixture>();
        list.add(LINE1);
        // list.add( LINE2 );
        // list.add( LINE3 );
        return list;
    }

    private List<AccountingLineFixture> getSourceAccountingLineParametersFromFixtures() {
        List<AccountingLineFixture> list = new ArrayList<AccountingLineFixture>();
        list.add(LINE1);
        // list.add( LINE2 );
        // list.add( LINE3 );
        return list;
    }


    @TestsWorkflowViaDatabase
    @RelatesTo(RelatesTo.JiraIssue.KULUT10)
    public void testWorkflowRouting() throws Exception {
        // save and route the document
        AccountingDocument document = (AccountingDocument) buildDocumentForWorkflowRoutingTest();
        final String docHeaderId = document.getDocumentNumber();
        routeDocument(document, getDocumentService());

        // the document should now be routed to VPUTMAN and RORENFRO as Fiscal Officers
        WorkflowTestUtils.waitForNodeChange(document.getDocumentHeader().getWorkflowDocument(), ACCOUNT_REVIEW);
        approve(docHeaderId, VPUTMAN, ACCOUNT_REVIEW);
        approve(docHeaderId, RORENFRO, ACCOUNT_REVIEW);

        // now doc should be in Org Review routing to CSWINSON, RRUFFNER, SEASON, and DFOGLE
        WorkflowTestUtils.waitForNodeChange(document.getDocumentHeader().getWorkflowDocument(), ORG_REVIEW);
        approve(docHeaderId, CSWINSON, ORG_REVIEW);
        approve(docHeaderId, RRUFFNER, ORG_REVIEW);
        approve(docHeaderId, SEASON, ORG_REVIEW);
        approve(docHeaderId, DFOGLE, ORG_REVIEW);

        // TODO once the sub fund node has been added, add code here to test it...

        WorkflowTestUtils.waitForStatusChange(document.getDocumentHeader().getWorkflowDocument(), EdenConstants.ROUTE_HEADER_FINAL_CD);

        changeCurrentUser(VPUTMAN);
        document = (AccountingDocument) getDocumentService().getByDocumentHeaderId(docHeaderId);
        assertTrue("Document should now be final.", document.getDocumentHeader().getWorkflowDocument().stateIsFinal());
    }

    private void approve(String docHeaderId, UserNameFixture user, String expectedNode) throws Exception {
        changeCurrentUser(user);
        AccountingDocumentTestUtils.approve(docHeaderId, user, expectedNode, getDocumentService());
    }

    private Document buildDocumentForWorkflowRoutingTest() throws Exception {
        AccountingDocument document = buildDocument();
        AccountingLineFixture.LINE2_TOF.addAsSourceTo(document);
        AccountingLineFixture.LINE2_TOF.addAsTargetTo(document);
        return document;
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

    public final void testConvertIntoErrorCorrection_invalidYear() throws Exception {
        AccountingDocumentTestUtils.testConvertIntoErrorCorrection_invalidYear(buildDocument(), getTransactionalDocumentDictionaryService(), getAccountingPeriodService());
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

    private TransferOfFundsDocument buildDocument() throws Exception {
        // put accounting lines into document parameter for later
        TransferOfFundsDocument document = (TransferOfFundsDocument) getDocumentParameterFixture();

        // set accountinglines to document
        for (AccountingLineFixture sourceFixture : getSourceAccountingLineParametersFromFixtures()) {
            sourceFixture.addAsSourceTo(document);
        }

        for (AccountingLineFixture targetFixture : getTargetAccountingLineParametersFromFixtures()) {
            targetFixture.addAsTargetTo(document);
        }

        return document;
    }

    private int getExpectedPrePeCount() {
        return 4;
    }
}
