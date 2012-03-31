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

import static org.kuali.kfs.sys.KualiTestAssertionUtils.assertEquality;
import static org.kuali.kfs.sys.KualiTestAssertionUtils.assertInequality;
import static org.kuali.kfs.sys.fixture.AccountingLineFixture.LINE5;
import static org.kuali.kfs.sys.fixture.UserNameFixture.dfogle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.service.AccountingPeriodService;
import org.kuali.kfs.fp.businessobject.VoucherSourceAccountingLine;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.DocumentTestUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.businessobject.TargetAccountingLine;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.AccountingDocumentTestUtils;
import org.kuali.kfs.sys.document.Correctable;
import org.kuali.kfs.sys.document.workflow.WorkflowTestUtils;
import org.kuali.kfs.sys.fixture.AccountingLineFixture;
import org.kuali.kfs.sys.monitor.ChangeMonitor;
import org.kuali.kfs.sys.monitor.FinancialSystemDocumentStatusMonitor;
import org.kuali.kfs.sys.monitor.DocumentWorkflowStatusMonitor;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.TransactionalDocumentDictionaryService;
import org.kuali.rice.krad.document.Copyable;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class is used to test JournalVoucherDocument.
 */
@ConfigureContext(session = dfogle)
public class JournalVoucherDocumentTest extends KualiTestBase {

    public static final Class<JournalVoucherDocument> DOCUMENT_CLASS = JournalVoucherDocument.class;

    private JournalVoucherDocument buildDocument() throws Exception {
        // put accounting lines into document parameter for later
        JournalVoucherDocument document = (JournalVoucherDocument) getDocumentParameterFixture();
        StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        document.getDocumentHeader().setDocumentDescription(StringUtils.abbreviate("Unit Test doc for "+trace[3].getMethodName(), SpringContext.getBean(DataDictionaryService.class).getAttributeMaxLength(document.getDocumentHeader().getClass(), "documentDescription")));
        document.getDocumentHeader().setExplanation(StringUtils.abbreviate("Unit test created document for "+trace[3].getClassName()+"."+trace[3].getMethodName(), SpringContext.getBean(DataDictionaryService.class).getAttributeMaxLength(document.getDocumentHeader().getClass(), "explanation")));

        // set accountinglines to document
        for (AccountingLineFixture sourceFixture : getSourceAccountingLineParametersFromFixtures()) {
            sourceFixture.addAsVoucherSourceTo(document);
        }

        document.setBalanceTypeCode(KFSConstants.BALANCE_TYPE_ACTUAL);
        return document;
    }

    /**
     * Had to override b/c there are too many differences between the JV and the standard document structure (i.e. GLPEs generate
     * differently, routing isn't standard, etc).
     *
     * @see org.kuali.rice.krad.document.AccountingDocumentTestBase#testConvertIntoCopy()
     */
    @ConfigureContext(session = dfogle, shouldCommitTransactions = true)
    public void testConvertIntoCopy() throws Exception {
        // save the original doc, wait for status change
        AccountingDocument document = buildDocument();
        SpringContext.getBean(DocumentService.class).routeDocument(document, "saving copy source document", null);
        // collect some preCopy data
        String preCopyId = document.getDocumentNumber();
        String preCopyCopiedFromId = document.getDocumentHeader().getDocumentTemplateNumber();

        int preCopyPECount = document.getGeneralLedgerPendingEntries().size();
        // int preCopyNoteCount = document.getDocumentHeader().getNotes().size();

        List preCopySourceLines = (List) ObjectUtils.deepCopy((Serializable)document.getSourceAccountingLines());
        List preCopyTargetLines = (List) ObjectUtils.deepCopy((Serializable)document.getTargetAccountingLines());
        // validate preCopy state
        assertNotNull(preCopyId);
        assertNull(preCopyCopiedFromId);

        assertEquals(1, preCopyPECount);
        // assertEquals(0, preCopyNoteCount);

        // do the copy
        ((Copyable) document).toCopy();
        // compare to preCopy state

        String postCopyId = document.getDocumentNumber();
        assertFalse(postCopyId.equals(preCopyId));
        // verify that docStatus has changed
        // pending entries should be cleared
        int postCopyPECount = document.getGeneralLedgerPendingEntries().size();
        assertEquals(0, postCopyPECount);
        // TODO: revisit this is it still needed
        // count 1 note, compare to "copied" text
        // int postCopyNoteCount = document.getDocumentHeader().getNotes().size();
        // assertEquals(1, postCopyNoteCount);
        // DocumentNote note = document.getDocumentHeader().getNote(0);
        // assertTrue(note.getFinancialDocumentNoteText().indexOf("copied from") != -1);
        // copiedFrom should be equal to old id
        String copiedFromId = document.getDocumentHeader().getDocumentTemplateNumber();
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
     * @see org.kuali.rice.krad.document.AccountingDocumentTestBase#testConvertIntoErrorCorrection()
     */
    @ConfigureContext(session = dfogle, shouldCommitTransactions = true)
    public void testConvertIntoErrorCorrection() throws Exception {
        AccountingDocument document = buildDocument();

        // replace the broken sourceLines with one that lets the test succeed
        KualiDecimal balance = new KualiDecimal("21.12");
        ArrayList sourceLines = new ArrayList();
        {
            VoucherSourceAccountingLine sourceLine = new VoucherSourceAccountingLine();
            sourceLine.setDocumentNumber(document.getDocumentNumber());
            sourceLine.setSequenceNumber(new Integer(1));
            sourceLine.setChartOfAccountsCode("BL");
            sourceLine.setAccountNumber("1031400");
            sourceLine.setFinancialObjectCode("1663");
            sourceLine.setAmount(balance);
            sourceLine.setObjectTypeCode("AS");
            sourceLine.setBalanceTypeCode("AC");
            sourceLine.setDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
            sourceLine.refresh();
            sourceLines.add(sourceLine);
        }
        document.setSourceAccountingLines(sourceLines);


        String documentHeaderId = document.getDocumentNumber();
        // route the original doc, wait for status change
        SpringContext.getBean(DocumentService.class).routeDocument(document, "saving errorCorrection source document", null);
        // jv docs go straight to final
        WorkflowTestUtils.waitForDocumentApproval(documentHeaderId);
        document = (AccountingDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(documentHeaderId);
        // collect some preCorrect data
        String preCorrectId = document.getDocumentNumber();
        String preCorrectCorrectsId = document.getFinancialSystemDocumentHeader().getFinancialDocumentInErrorNumber();

        int preCorrectPECount = document.getGeneralLedgerPendingEntries().size();
        // int preCorrectNoteCount = document.getDocumentHeader().getNotes().size();
        DocumentStatus preCorrectStatus = document.getDocumentHeader().getWorkflowDocument().getStatus();

        ArrayList preCorrectSourceLines = (ArrayList) ObjectUtils.deepCopy(new ArrayList(document.getSourceAccountingLines()));
        ArrayList preCorrectTargetLines = (ArrayList) ObjectUtils.deepCopy(new ArrayList(document.getTargetAccountingLines()));
        // validate preCorrect state
        assertNotNull("Pre-correct document number should not be null.",preCorrectId);
        assertNull("preCorrectCorrectsId should have been null",preCorrectCorrectsId);

        // assertEquals(0, preCorrectNoteCount);
        assertEquals("Document status is incorrect", DocumentStatus.FINAL, preCorrectStatus);
        // do the copy
        ((Correctable) document).toErrorCorrection();
        // compare to preCorrect state

        String postCorrectId = document.getDocumentNumber();
        assertFalse("document number should be different before and after correction.  Both were: " + postCorrectId, postCorrectId.equals(preCorrectId));
        // pending entries should be cleared
        int postCorrectPECount = document.getGeneralLedgerPendingEntries().size();
        assertEquals("pending entry count should have been zero after correction",0, postCorrectPECount);
        // TODO: revisit this is it still needed
        // count 1 note, compare to "correction" text
        // int postCorrectNoteCount = document.getDocumentHeader().getNotes().size();
        // assertEquals(1, postCorrectNoteCount);
        // DocumentNote note = document.getDocumentHeader().getNote(0);
        // assertTrue(note.getFinancialDocumentNoteText().indexOf("correction") != -1);
        // correctsId should be equal to old id
        String correctsId = document.getFinancialSystemDocumentHeader().getFinancialDocumentInErrorNumber();
        assertEquals(preCorrectId, correctsId);
        // accounting lines should have sign reversed on amounts
        List postCorrectSourceLines = document.getSourceAccountingLines();
        assertEquals(preCorrectSourceLines.size(), postCorrectSourceLines.size());
        for (int i = 0; i < preCorrectSourceLines.size(); ++i) {
            SourceAccountingLine preCorrectLine = (SourceAccountingLine) preCorrectSourceLines.get(i);
            SourceAccountingLine postCorrectLine = (SourceAccountingLine) postCorrectSourceLines.get(i);

            assertEquality(postCorrectId, postCorrectLine.getDocumentNumber());
            assertEquality(preCorrectLine.getAmount(), postCorrectLine.getAmount());
            assertEquality(postCorrectLine.getDebitCreditCode(), KFSConstants.GL_CREDIT_CODE);
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
     * @see org.kuali.rice.krad.document.DocumentTestBase#testRouteDocument()
     */
    // @RelatesTo(JiraIssue.KULRNE4926)
    @ConfigureContext(session = dfogle, shouldCommitTransactions = true)
    public void testRouteDocument() throws Exception {
        // save the original doc, wait for status change
        Document document = buildDocument();
        assertFalse(DocumentStatus.ENROUTE.equals(document.getDocumentHeader().getWorkflowDocument().getStatus()));
        SpringContext.getBean(DocumentService.class).routeDocument(document, "saving copy source document", null);
        // jv docs go sttraight to final
        WorkflowTestUtils.waitForDocumentApproval(document.getDocumentNumber());
        // also check the Kuali (not Workflow) document status
        FinancialSystemDocumentStatusMonitor statusMonitor = new FinancialSystemDocumentStatusMonitor(SpringContext.getBean(DocumentService.class), document.getDocumentHeader().getDocumentNumber(), KFSConstants.DocumentStatusCodes.APPROVED);
        assertTrue(ChangeMonitor.waitUntilChange(statusMonitor, 30, 5));
    }

    private Document getDocumentParameterFixture() throws Exception {
        return DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), JournalVoucherDocument.class);
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
        JournalVoucherDocument document = DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), DOCUMENT_CLASS);
        document.setBalanceTypeCode(KFSConstants.BALANCE_TYPE_ACTUAL);

        AccountingDocumentTestUtils.testAddAccountingLine(document, sourceLines, targetLines, expectedSourceTotal, expectedTargetTotal);
    }

    public final void testGetNewDocument() throws Exception {
        AccountingDocumentTestUtils.testGetNewDocument_byDocumentClass(DOCUMENT_CLASS, SpringContext.getBean(DocumentService.class));
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

    public final void testConvertIntoErrorCorrection_invalidYear() throws Exception {
        AccountingDocumentTestUtils.testConvertIntoErrorCorrection_invalidYear(buildDocument(), SpringContext.getBean(TransactionalDocumentDictionaryService.class), SpringContext.getBean(AccountingPeriodService.class));
    }

    @ConfigureContext(session = dfogle, shouldCommitTransactions = true)
    public final void testSaveDocument() throws Exception {
        AccountingDocumentTestUtils.testSaveDocument(buildDocument(), SpringContext.getBean(DocumentService.class));
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

