/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/test/integration/src/org/kuali/kfs/fp/document/JournalVoucherDocumentTest.java,v $
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

import static org.kuali.core.util.SpringServiceLocator.getAccountingPeriodService;
import static org.kuali.core.util.SpringServiceLocator.getDataDictionaryService;
import static org.kuali.core.util.SpringServiceLocator.getDocumentService;
import static org.kuali.core.util.SpringServiceLocator.getTransactionalDocumentDictionaryService;
import static org.kuali.module.financial.document.TransactionalDocumentTestUtils.testGetNewDocument_byDocumentClass;
import static org.kuali.test.fixtures.AccountingLineFixture.LINE5;
import static org.kuali.test.fixtures.UserNameFixture.DFOGLE;
import static org.kuali.test.util.KualiTestAssertionUtils.assertEquality;
import static org.kuali.test.util.KualiTestAssertionUtils.assertInequality;

import java.util.ArrayList;
import java.util.List;

import org.kuali.Constants;
import org.kuali.core.bo.SourceAccountingLine;
import org.kuali.core.bo.TargetAccountingLine;
import org.kuali.core.document.Document;
import org.kuali.core.document.DocumentNote;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.test.DocumentTestUtils;
import org.kuali.test.KualiTestBase;
import org.kuali.test.TestsWorkflowViaDatabase;
import org.kuali.test.WithTestSpringContext;
import org.kuali.test.fixtures.AccountingLineFixture;
import org.kuali.test.monitor.ChangeMonitor;
import org.kuali.test.monitor.DocumentStatusMonitor;
import org.kuali.test.monitor.DocumentWorkflowStatusMonitor;
import org.kuali.workflow.WorkflowTestUtils;

import edu.iu.uis.eden.EdenConstants;

/**
 * This class is used to test JournalVoucherDocument.
 * 
 * 
 */
@WithTestSpringContext(session = DFOGLE)
public class JournalVoucherDocumentTest extends KualiTestBase {

    public static final Class<JournalVoucherDocument> DOCUMENT_CLASS = JournalVoucherDocument.class;

    private JournalVoucherDocument buildDocument() throws Exception {
        // put accounting lines into document parameter for later
        JournalVoucherDocument document = (JournalVoucherDocument) getDocumentParameterFixture();

        // set accountinglines to document
        for (AccountingLineFixture sourceFixture : getSourceAccountingLineParametersFromFixtures()) {
            sourceFixture.addAsSourceTo(document);
        }

        for (AccountingLineFixture targetFixture : getTargetAccountingLineParametersFromFixtures()) {
            targetFixture.addAsTargetTo(document);
        }
        document.setBalanceTypeCode(Constants.BALANCE_TYPE_ACTUAL);
        return document;
    }

    /**
     * Had to override b/c there are too many differences between the JV and the standard document structure (i.e. GLPEs generate
     * differently, routing isn't standard, etc).
     * 
     * @see org.kuali.core.document.TransactionalDocumentTestBase#testConvertIntoCopy()
     */
    @TestsWorkflowViaDatabase
    public void testConvertIntoCopy() throws Exception {
        // save the original doc, wait for status change
        TransactionalDocument document = buildDocument();
        getDocumentService().routeDocument(document, "saving copy source document", null);
        // collect some preCopy data
        String preCopyId = document.getDocumentNumber();
        String preCopyCopiedFromId = document.getDocumentHeader().getFinancialDocumentTemplateNumber();

        int preCopyPECount = document.getGeneralLedgerPendingEntries().size();
        int preCopyNoteCount = document.getDocumentHeader().getNotes().size();

        ArrayList preCopySourceLines = (ArrayList) ObjectUtils.deepCopy((ArrayList) document.getSourceAccountingLines());
        ArrayList preCopyTargetLines = (ArrayList) ObjectUtils.deepCopy((ArrayList) document.getTargetAccountingLines());
        // validate preCopy state
        assertNotNull(preCopyId);
        assertNull(preCopyCopiedFromId);

        assertEquals(1, preCopyPECount);
        assertEquals(0, preCopyNoteCount);

        // do the copy
        document.convertIntoCopy();
        // compare to preCopy state

        String postCopyId = document.getDocumentNumber();
        assertFalse(postCopyId.equals(preCopyId));
        // verify that docStatus has changed
        // pending entries should be cleared
        int postCopyPECount = document.getGeneralLedgerPendingEntries().size();
        assertEquals(0, postCopyPECount);
        // count 1 note, compare to "copied" text
        int postCopyNoteCount = document.getDocumentHeader().getNotes().size();
        assertEquals(1, postCopyNoteCount);
        DocumentNote note = document.getDocumentHeader().getNote(0);
        assertTrue(note.getFinancialDocumentNoteText().indexOf("copied from") != -1);
        // copiedFrom should be equal to old id
        String copiedFromId = document.getDocumentHeader().getFinancialDocumentTemplateNumber();
        assertEquals(preCopyId, copiedFromId);
        // accounting lines should be have different docHeaderIds but same amounts
        List postCopySourceLines = document.getSourceAccountingLines();
        assertEquals(preCopySourceLines.size(), postCopySourceLines.size());
        for (int i = 0; i < preCopySourceLines.size(); ++i) {
            SourceAccountingLine preCopyLine = (SourceAccountingLine) preCopySourceLines.get(i);
            SourceAccountingLine postCopyLine = (SourceAccountingLine) postCopySourceLines.get(i);

            assertInequality(preCopyLine.getDocumentNumber(), postCopyLine.getDocumentNumber());
            assertEquality(preCopyLine.getAmount(), postCopyLine.getAmount());
        }

        List postCopyTargetLines = document.getTargetAccountingLines();
        assertEquals(preCopyTargetLines.size(), postCopyTargetLines.size());
        for (int i = 0; i < preCopyTargetLines.size(); ++i) {
            TargetAccountingLine preCopyLine = (TargetAccountingLine) preCopyTargetLines.get(i);
            TargetAccountingLine postCopyLine = (TargetAccountingLine) postCopyTargetLines.get(i);

            assertInequality(preCopyLine.getDocumentNumber(), postCopyLine.getDocumentNumber());
            assertEquality(preCopyLine.getAmount(), postCopyLine.getAmount());
        }
    }

    /**
     * Had to override b/c there are too many differences between the JV and the standard document structure (i.e. GLPEs generate
     * differently, routing isn't standard, etc).
     * 
     * @see org.kuali.core.document.TransactionalDocumentTestBase#testConvertIntoErrorCorrection()
     */
    @TestsWorkflowViaDatabase
    public void testConvertIntoErrorCorrection() throws Exception {
        TransactionalDocument document = buildDocument();

        // replace the broken sourceLines with one that lets the test succeed
        KualiDecimal balance = new KualiDecimal("21.12");
        ArrayList sourceLines = new ArrayList();
        {
            SourceAccountingLine sourceLine = new SourceAccountingLine();
            sourceLine.setDocumentNumber(document.getDocumentNumber());
            sourceLine.setSequenceNumber(new Integer(0));
            sourceLine.setChartOfAccountsCode("BL");
            sourceLine.setAccountNumber("1031400");
            sourceLine.setFinancialObjectCode("1663");
            sourceLine.setAmount(balance);
            sourceLine.setObjectTypeCode("AS");
            sourceLine.setBalanceTypeCode("AC");
            sourceLine.refresh();
            sourceLines.add(sourceLine);
        }
        document.setSourceAccountingLines(sourceLines);


        String documentHeaderId = document.getDocumentNumber();
        // route the original doc, wait for status change
        getDocumentService().routeDocument(document, "saving errorCorrection source document", null);
        // jv docs go straight to final
        DocumentWorkflowStatusMonitor routeMonitor = new DocumentWorkflowStatusMonitor(getDocumentService(), documentHeaderId, "F");
        assertTrue(ChangeMonitor.waitUntilChange(routeMonitor, 240, 5));
        document = (TransactionalDocument) getDocumentService().getByDocumentHeaderId(documentHeaderId);
        // collect some preCorrect data
        String preCorrectId = document.getDocumentNumber();
        String preCorrectCorrectsId = document.getDocumentHeader().getFinancialDocumentInErrorNumber();

        int preCorrectPECount = document.getGeneralLedgerPendingEntries().size();
        int preCorrectNoteCount = document.getDocumentHeader().getNotes().size();
        String preCorrectStatus = document.getDocumentHeader().getWorkflowDocument().getRouteHeader().getDocRouteStatus();

        ArrayList preCorrectSourceLines = (ArrayList) ObjectUtils.deepCopy(new ArrayList(document.getSourceAccountingLines()));
        ArrayList preCorrectTargetLines = (ArrayList) ObjectUtils.deepCopy(new ArrayList(document.getTargetAccountingLines()));
        // validate preCorrect state
        assertNotNull(preCorrectId);
        assertNull(preCorrectCorrectsId);

        assertEquals(1, preCorrectPECount);
        assertEquals(0, preCorrectNoteCount);
        assertEquals("F", preCorrectStatus);
        // do the copy
        document.convertIntoErrorCorrection();
        // compare to preCorrect state

        String postCorrectId = document.getDocumentNumber();
        assertFalse(postCorrectId.equals(preCorrectId));
        // pending entries should be cleared
        int postCorrectPECount = document.getGeneralLedgerPendingEntries().size();
        assertEquals(0, postCorrectPECount);
        // count 1 note, compare to "correction" text
        int postCorrectNoteCount = document.getDocumentHeader().getNotes().size();
        assertEquals(1, postCorrectNoteCount);
        DocumentNote note = document.getDocumentHeader().getNote(0);
        assertTrue(note.getFinancialDocumentNoteText().indexOf("correction") != -1);
        // correctsId should be equal to old id
        String correctsId = document.getDocumentHeader().getFinancialDocumentInErrorNumber();
        assertEquals(preCorrectId, correctsId);
        // accounting lines should have sign reversed on amounts
        List postCorrectSourceLines = document.getSourceAccountingLines();
        assertEquals(preCorrectSourceLines.size(), postCorrectSourceLines.size());
        for (int i = 0; i < preCorrectSourceLines.size(); ++i) {
            SourceAccountingLine preCorrectLine = (SourceAccountingLine) preCorrectSourceLines.get(i);
            SourceAccountingLine postCorrectLine = (SourceAccountingLine) postCorrectSourceLines.get(i);

            assertEquality(postCorrectId, postCorrectLine.getDocumentNumber());
            assertEquality(preCorrectLine.getAmount().negated(), postCorrectLine.getAmount());
        }

        List postCorrectTargetLines = document.getTargetAccountingLines();
        assertEquals(preCorrectTargetLines.size(), postCorrectTargetLines.size());
        for (int i = 0; i < preCorrectTargetLines.size(); ++i) {
            TargetAccountingLine preCorrectLine = (TargetAccountingLine) preCorrectTargetLines.get(i);
            TargetAccountingLine postCorrectLine = (TargetAccountingLine) postCorrectTargetLines.get(i);

            assertEquality(postCorrectId, postCorrectLine.getDocumentNumber());
            assertEquality(preCorrectLine.getAmount().negated(), postCorrectLine.getAmount());
        }
    }

    /**
     * Override b/c the status changing is flakey with this doc b/c routing is special (goes straight to final).
     * 
     * @see org.kuali.core.document.DocumentTestBase#testRouteDocument()
     */
    @TestsWorkflowViaDatabase
    public void testRouteDocument() throws Exception {
        // save the original doc, wait for status change
        Document document = buildDocument();
        assertFalse("R".equals(document.getDocumentHeader().getWorkflowDocument().getRouteHeader().getDocRouteStatus()));
        getDocumentService().routeDocument(document, "saving copy source document", null);
        // jv docs go straight to final
        WorkflowTestUtils.waitForStatusChange(document.getDocumentHeader().getWorkflowDocument(), EdenConstants.ROUTE_HEADER_FINAL_CD);
        // also check the Kuali (not Workflow) document status
        DocumentStatusMonitor statusMonitor = new DocumentStatusMonitor(getDocumentService(), document.getDocumentHeader().getDocumentNumber(), Constants.DocumentStatusCodes.APPROVED);
        assertTrue(ChangeMonitor.waitUntilChange(statusMonitor, 240, 5));
    }

    private Document getDocumentParameterFixture() throws Exception {
        return DocumentTestUtils.createDocument(getDocumentService(), JournalVoucherDocument.class);
    }

    private List<AccountingLineFixture> getTargetAccountingLineParametersFromFixtures() {
        ArrayList list = new ArrayList();
        return list;
    }

    private List<AccountingLineFixture> getSourceAccountingLineParametersFromFixtures() {
        List<AccountingLineFixture> list = new ArrayList<AccountingLineFixture>();
        list.add(LINE5);
        return list;
    }


    public final void testAddAccountingLine() throws Exception {
        List<SourceAccountingLine> sourceLines = generateSouceAccountingLines();
        List<TargetAccountingLine> targetLines = generateTargetAccountingLines();
        int expectedSourceTotal = sourceLines.size();
        int expectedTargetTotal = targetLines.size();
        JournalVoucherDocument document = DocumentTestUtils.createDocument(getDocumentService(), DOCUMENT_CLASS);
        document.setBalanceTypeCode(Constants.BALANCE_TYPE_ACTUAL);

        TransactionalDocumentTestUtils.testAddAccountingLine(document, sourceLines, targetLines, expectedSourceTotal, expectedTargetTotal);
    }

    public final void testGetNewDocument() throws Exception {
        testGetNewDocument_byDocumentClass(DOCUMENT_CLASS, getDocumentService());
    }

    public final void testConvertIntoCopy_invalidYear() throws Exception {
        TransactionalDocumentTestUtils.testConvertIntoCopy_invalidYear(buildDocument(), getAccountingPeriodService());
    }

    public final void testConvertIntoCopy_copyDisallowed() throws Exception {
        TransactionalDocumentTestUtils.testConvertIntoCopy_copyDisallowed(buildDocument(), getDataDictionaryService());

    }

    public final void testConvertIntoErrorCorrection_documentAlreadyCorrected() throws Exception {
        TransactionalDocumentTestUtils.testConvertIntoErrorCorrection_documentAlreadyCorrected(buildDocument(), getTransactionalDocumentDictionaryService());
    }

    public final void testConvertIntoErrorCorrection_errorCorrectionDisallowed() throws Exception {
        TransactionalDocumentTestUtils.testConvertIntoErrorCorrection_errorCorrectionDisallowed(buildDocument(), getDataDictionaryService());
    }

    public final void testConvertIntoErrorCorrection_invalidYear() throws Exception {
        TransactionalDocumentTestUtils.testConvertIntoErrorCorrection_invalidYear(buildDocument(), getTransactionalDocumentDictionaryService(), getAccountingPeriodService());
    }

    @TestsWorkflowViaDatabase
    public final void testSaveDocument() throws Exception {
        TransactionalDocumentTestUtils.testSaveDocument(buildDocument(), getDocumentService());
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


}
