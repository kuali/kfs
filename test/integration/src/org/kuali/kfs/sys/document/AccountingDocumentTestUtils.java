/*
 * Copyright 2006 The Kuali Foundation.
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

import static org.kuali.test.util.KualiTestAssertionUtils.assertEquality;
import static org.kuali.test.util.KualiTestAssertionUtils.assertInequality;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.Constants;
import org.kuali.core.bo.AdHocRouteRecipient;
import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.bo.DocumentNote;
import org.kuali.core.datadictionary.DataDictionary;
import org.kuali.core.document.Copyable;
import org.kuali.core.document.Correctable;
import org.kuali.core.document.Document;
import org.kuali.core.exceptions.ValidationException;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.TransactionalDocumentDictionaryService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.bo.TargetAccountingLine;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.module.chart.bo.AccountingPeriod;
import org.kuali.module.chart.service.AccountingPeriodService;
import org.kuali.test.KualiTestBase;
import org.kuali.test.fixtures.UserNameFixture;
import org.kuali.test.monitor.ChangeMonitor;
import org.kuali.test.monitor.DocumentVersionMonitor;
import org.kuali.test.monitor.DocumentWorkflowStatusMonitor;
import org.kuali.workflow.WorkflowTestUtils;

import edu.iu.uis.eden.exception.WorkflowException;

public final class AccountingDocumentTestUtils extends KualiTestBase {
    private static Logger LOG = Logger.getLogger(AccountingDocumentTestUtils.class);
    
    public void testPlaceholder() {
        assertTrue("Test needs to have at least one test.", true);
    }

    public static void testAddAccountingLine(AccountingDocument document, List<SourceAccountingLine> sourceLines, List<TargetAccountingLine> targetLines, int expectedSourceTotal, int expectedTargetTotal) throws Exception {
        assertTrue("expected count should be > 0", (expectedSourceTotal + expectedTargetTotal) > 0);
        assertTrue("no lines found", (targetLines.size() + sourceLines.size()) > 0);

        assertEquals(0, document.getSourceAccountingLines().size());
        assertEquals(0, document.getTargetAccountingLines().size());

        // add source lines
        for (SourceAccountingLine sourceLine : sourceLines) {
            document.addSourceAccountingLine(sourceLine);
        }
        // add target lines
        for (TargetAccountingLine targetLine : targetLines) {
            document.addTargetAccountingLine(targetLine);
        }

        assertEquals("source line count mismatch", expectedSourceTotal, document.getSourceAccountingLines().size());
        assertEquals("target line count mismatch", expectedTargetTotal, document.getTargetAccountingLines().size());
    }

    public static <T extends AccountingDocument> void testGetNewDocument_byDocumentClass(Class<T> documentClass, DocumentService documentService) throws Exception {
        T document = (T) documentService.getNewDocument(documentClass);
        // verify document was created
        assertNotNull(document);
        assertNotNull(document.getDocumentHeader());
        assertNotNull(document.getDocumentHeader().getDocumentNumber());
    }

    public static void testConvertIntoCopy_copyDisallowed(AccountingDocument document, DataDictionaryService dataDictionaryService) throws Exception {
        // change the dataDictionary to disallow copying
        DataDictionary d = dataDictionaryService.getDataDictionary();
        Class documentClass = document.getClass();
        boolean originalValue = d.getTransactionalDocumentEntry(documentClass).getAllowsCopy();
        try {
            d.getTransactionalDocumentEntry(documentClass).setAllowsCopy(false);

            boolean failedAsExpected = false;
            try {
                ((Copyable) document).toCopy();
            }
            catch (IllegalStateException e) {
                failedAsExpected = true;
            }

            assertTrue(failedAsExpected);
        }
        finally {
            d.getTransactionalDocumentEntry(documentClass).setAllowsCopy(originalValue);
        }
    }

    public static void testConvertIntoErrorCorrection_documentAlreadyCorrected(AccountingDocument document, TransactionalDocumentDictionaryService dictionaryService) throws Exception {

        if (dictionaryService.getAllowsErrorCorrection(document).booleanValue()) {
            DocumentHeader header = document.getDocumentHeader();
            header.setCorrectedByDocumentId("1");

            boolean failedAsExpected = false;
            try {
                ((Correctable) document).toErrorCorrection();
            }
            catch (IllegalStateException e) {
                failedAsExpected = true;
            }

            assertTrue(failedAsExpected);
        }
    }
    
    public static void testConvertIntoErrorCorrection_errorCorrectionDisallowed(AccountingDocument document, DataDictionaryService dataDictionaryService) throws Exception {
        // change the dataDictionary to disallow errorCorrection
        DataDictionary d = dataDictionaryService.getDataDictionary();
        Class documentClass = document.getClass();
        boolean originalValue = d.getTransactionalDocumentEntry(documentClass).getAllowsErrorCorrection();
        try {
            d.getTransactionalDocumentEntry(documentClass).setAllowsErrorCorrection(false);

            boolean failedAsExpected = false;
            try {
                ((Correctable) document).toErrorCorrection();
            }
            catch (IllegalStateException e) {
                failedAsExpected = true;
            }

            assertTrue(failedAsExpected);
        }
        finally {
            d.getTransactionalDocumentEntry(documentClass).setAllowsErrorCorrection(originalValue);
        }
    }

    public static void testConvertIntoErrorCorrection_invalidYear(AccountingDocument document, TransactionalDocumentDictionaryService dictionaryService, AccountingPeriodService accountingPeriodService) throws Exception {
        if (dictionaryService.getAllowsErrorCorrection(document).booleanValue()) {
            // change to non-current posting year
            Integer postingYear = document.getPostingYear();
            AccountingPeriod accountingPeriod = accountingPeriodService.getByPeriod(document.getAccountingPeriod().getUniversityFiscalPeriodCode(), postingYear - 5);
            assertNotNull("accounting period invalid for test", accountingPeriod);
            assertTrue("accounting period invalid (same as current year)", postingYear != accountingPeriod.getUniversityFiscalYear());
            assertEquals("accounting period invalid. period codes must remain the same", document.getAccountingPeriod().getUniversityFiscalPeriodCode(), accountingPeriod.getUniversityFiscalPeriodCode());
            document.setAccountingPeriod(accountingPeriod);

            boolean failedAsExpected = false;
            try {
                ((Correctable) document).toErrorCorrection();
                fail("converted into error correction for an invalid year");
            }
            catch (IllegalStateException e) {
                failedAsExpected = true;
            }
            assertTrue(failedAsExpected);
        }
    }

    /**
     * @TestsWorkflowViaDatabase needed for this test
     * @see TestsWorkflowViaDatabase
     */
    public static void testRouteDocument(AccountingDocument document, DocumentService documentService) throws Exception {
        document.prepareForSave();

        assertFalse("R".equals(document.getDocumentHeader().getWorkflowDocument().getRouteHeader().getDocRouteStatus()));
        routeDocument(document, "saving copy source document", null, documentService);
        DocumentWorkflowStatusMonitor am = new DocumentWorkflowStatusMonitor(documentService, document.getDocumentNumber(), "R");
        assertTrue(ChangeMonitor.waitUntilChange(am, 240, 5));
        assertEquals("R", document.getDocumentHeader().getWorkflowDocument().getRouteHeader().getDocRouteStatus());
    }

    /**
     * @TestsWorkflowViaDatabase needed for this test
     * @see TestsWorkflowViaDatabase
     * 
     */

    public static void testConvertIntoErrorCorrection(AccountingDocument document, int expectedPrePECount, DocumentService documentService, TransactionalDocumentDictionaryService dictionaryService) throws Exception {
        if (dictionaryService.getAllowsErrorCorrection(document).booleanValue()) {
            String documentHeaderId = document.getDocumentNumber();
            LOG.debug("documentHeaderId = " + documentHeaderId);
            // route the original doc, wait for status change
            routeDocument(document, "saving errorCorrection source document", null, documentService);
            DocumentWorkflowStatusMonitor routeMonitor = new DocumentWorkflowStatusMonitor(documentService, documentHeaderId, "R");
            assertTrue(ChangeMonitor.waitUntilChange(routeMonitor, 240, 5));
            document = (AccountingDocument) documentService.getByDocumentHeaderId(documentHeaderId);

            // mock a fully approved document
            document.getDocumentHeader().getWorkflowDocument().getRouteHeader().setDocRouteStatus(Constants.DocumentStatusCodes.APPROVED);

            // collect some preCorrect data
            String preCorrectId = document.getDocumentNumber();
            String preCorrectCorrectsId = document.getDocumentHeader().getFinancialDocumentInErrorNumber();

            int preCorrectPECount = document.getGeneralLedgerPendingEntries().size();
            int preCorrectNoteCount = document.getDocumentHeader().getNotes().size();

            List<? extends SourceAccountingLine> preCorrectSourceLines = (List<? extends SourceAccountingLine>) ObjectUtils.deepCopy(new ArrayList(document.getSourceAccountingLines()));
            List<? extends TargetAccountingLine> preCorrectTargetLines = (List<? extends TargetAccountingLine>) ObjectUtils.deepCopy(new ArrayList(document.getTargetAccountingLines()));
            // validate preCorrect state
            assertNotNull(preCorrectId);
            assertNull(preCorrectCorrectsId);

            assertEquals(expectedPrePECount, preCorrectPECount);
            assertEquals(0, preCorrectNoteCount);

            // do the error correction
            ((Correctable) document).toErrorCorrection();
            // compare to preCorrect state
            String postCorrectId = document.getDocumentNumber();
            LOG.debug("postcorrect documentHeaderId = " + postCorrectId);
            assertFalse(postCorrectId.equals(preCorrectId));
            // pending entries should be cleared
            int postCorrectPECount = document.getGeneralLedgerPendingEntries().size();
            LOG.debug("postcorrect PE count = " + postCorrectPECount);
            assertEquals(0, postCorrectPECount);
            // count 1 note, compare to "correction" text
            int postCorrectNoteCount = document.getDocumentHeader().getNotes().size();
            assertEquals(1, postCorrectNoteCount);
            DocumentNote note = document.getDocumentHeader().getNote(0);
            LOG.debug("postcorrect note text = " + note.getFinancialDocumentNoteText());
            assertTrue(note.getFinancialDocumentNoteText().indexOf("correction") != -1);
            // correctsId should be equal to old id
            String correctsId = document.getDocumentHeader().getFinancialDocumentInErrorNumber();
            LOG.debug("postcorrect correctsId = " + correctsId);
            assertEquals(preCorrectId, correctsId);
            // accounting lines should have sign reversed on amounts
            List<SourceAccountingLine> postCorrectSourceLines = document.getSourceAccountingLines();
            assertEquals(preCorrectSourceLines.size(), postCorrectSourceLines.size());
            for (int i = 0; i < preCorrectSourceLines.size(); ++i) {
                SourceAccountingLine preCorrectLine = preCorrectSourceLines.get(i);
                SourceAccountingLine postCorrectLine = postCorrectSourceLines.get(i);

                LOG.debug("postcorrect line(docId,amount) = " + i + "(" + postCorrectId + "," + postCorrectLine.getAmount());
                assertEquality(postCorrectId, postCorrectLine.getDocumentNumber());
                assertEquality(preCorrectLine.getAmount().negated(), postCorrectLine.getAmount());
            }

            List<? extends TargetAccountingLine> postCorrectTargetLines = document.getTargetAccountingLines();
            assertEquals(preCorrectTargetLines.size(), postCorrectTargetLines.size());
            for (int i = 0; i < preCorrectTargetLines.size(); ++i) {
                TargetAccountingLine preCorrectLine = preCorrectTargetLines.get(i);
                TargetAccountingLine postCorrectLine = postCorrectTargetLines.get(i);

                LOG.debug("postcorrect line(docId,amount) = " + i + "(" + postCorrectId + "," + postCorrectLine.getAmount());
                assertEquality(postCorrectId, postCorrectLine.getDocumentNumber());
                assertEquality(preCorrectLine.getAmount().negated(), postCorrectLine.getAmount());
            }
        }
    }

    /**
     * @TestsWorkflowViaDatabase needed for this test
     * @see TestsWorkflowViaDatabase
     */
    public static void testSaveDocument(AccountingDocument document, DocumentService documentService) throws Exception {
        // get document parameter
        document.prepareForSave();

        // save
        saveDocument(document, documentService);

        // retrieve
        AccountingDocument result = (AccountingDocument) documentService.getByDocumentHeaderId(document.getDocumentNumber());
        // verify
        assertMatch(document, result);
    }

    /**
     * @TestsWorkflowViaDatabase needed for this test
     * @see TestsWorkflowViaDatabase
     */
    public static void testConvertIntoCopy(AccountingDocument document, DocumentService documentService, int expectedPrePECount) throws Exception {
        // save the original doc, wait for status change
        document.prepareForSave();
        routeDocument(document, "saving copy source document", null, documentService);
        DocumentWorkflowStatusMonitor am = new DocumentWorkflowStatusMonitor(documentService, document.getDocumentNumber(), "R");
        assertTrue(ChangeMonitor.waitUntilChange(am, 240, 5));
        // collect some preCopy data
        String preCopyId = document.getDocumentNumber();
        String preCopyCopiedFromId = document.getDocumentHeader().getFinancialDocumentTemplateNumber();

        int preCopyPECount = document.getGeneralLedgerPendingEntries().size();
        int preCopyNoteCount = document.getDocumentHeader().getNotes().size();
        String preCopyStatus = document.getDocumentHeader().getWorkflowDocument().getRouteHeader().getDocRouteStatus();

        List<? extends SourceAccountingLine> preCopySourceLines = (List<? extends SourceAccountingLine>) ObjectUtils.deepCopy((ArrayList) document.getSourceAccountingLines());
        List<? extends TargetAccountingLine> preCopyTargetLines = (List<? extends TargetAccountingLine>) ObjectUtils.deepCopy((ArrayList) document.getTargetAccountingLines());
        // validate preCopy state
        assertNotNull(preCopyId);
        assertNull(preCopyCopiedFromId);

        assertEquals(expectedPrePECount, preCopyPECount);
        assertEquals(0, preCopyNoteCount);
        assertEquals("R", preCopyStatus);
        // do the copy
        ((Copyable) document).toCopy();
        // compare to preCopy state

        String postCopyId = document.getDocumentNumber();
        assertFalse(postCopyId.equals(preCopyId));
        // verify that docStatus has changed
        String postCopyStatus = document.getDocumentHeader().getWorkflowDocument().getRouteHeader().getDocRouteStatus();
        assertFalse(postCopyStatus.equals(preCopyStatus));
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
        // accounting lines should be have different docHeaderIds but same
        // amounts
        List<? extends SourceAccountingLine> postCopySourceLines = document.getSourceAccountingLines();
        assertEquals(preCopySourceLines.size(), postCopySourceLines.size());
        for (int i = 0; i < preCopySourceLines.size(); ++i) {
            SourceAccountingLine preCopyLine = preCopySourceLines.get(i);
            SourceAccountingLine postCopyLine = postCopySourceLines.get(i);

            assertInequality(preCopyLine.getDocumentNumber(), postCopyLine.getDocumentNumber());
            assertEquality(preCopyLine.getAmount(), postCopyLine.getAmount());
        }

        List<? extends TargetAccountingLine> postCopyTargetLines = document.getTargetAccountingLines();
        assertEquals(preCopyTargetLines.size(), postCopyTargetLines.size());
        for (int i = 0; i < preCopyTargetLines.size(); ++i) {
            TargetAccountingLine preCopyLine = preCopyTargetLines.get(i);
            TargetAccountingLine postCopyLine = postCopyTargetLines.get(i);

            assertInequality(preCopyLine.getDocumentNumber(), postCopyLine.getDocumentNumber());
            assertEquality(preCopyLine.getAmount(), postCopyLine.getAmount());
        }
    }

    // helper methods
    public static void routeDocument(AccountingDocument document, String annotation, List<AdHocRouteRecipient> adHocRoutingRecipients, DocumentService documentService) throws WorkflowException {
        try {
            documentService.routeDocument(document, annotation, adHocRoutingRecipients);
        }
        catch (ValidationException e) {
            // If the business rule evaluation fails then give us more info for debugging this test.
            fail(e.getMessage() + ", " + GlobalVariables.getErrorMap());
        }
    }

    public static void approveDocument(AccountingDocument document, DocumentService documentService) throws Exception {
        Long initialVersion = document.getVersionNumber();
        Long nextVersion = new Long(initialVersion.longValue() + 1);
        documentService.approveDocument(document, "approving test doc", null);

        DocumentVersionMonitor vm = new DocumentVersionMonitor(documentService, document.getDocumentNumber(), initialVersion);
        assertTrue(ChangeMonitor.waitUntilChange(vm, 120, 10));
        assertEquals(nextVersion, document.getVersionNumber());
    }

    public static void routeDocument(AccountingDocument document, DocumentService documentService) throws Exception {
        final String STATUS = "R";

        assertFalse(STATUS.equals(document.getDocumentHeader().getWorkflowDocument().getRouteHeader().getDocRouteStatus()));
        documentService.routeDocument(document, "routing test doc", null);

        DocumentWorkflowStatusMonitor am = new DocumentWorkflowStatusMonitor(documentService, document.getDocumentNumber(), STATUS);
        assertTrue(ChangeMonitor.waitUntilChange(am, 120, 10));
        assertEquals(STATUS, document.getDocumentHeader().getWorkflowDocument().getRouteHeader().getDocRouteStatus());
    }

    public static void saveDocument(AccountingDocument document, DocumentService documentService) throws WorkflowException {
        try {
            documentService.saveDocument(document);
        }
        catch (ValidationException e) {
            // If the business rule evaluation fails then give us more info for debugging this test.
            fail(e.getMessage() + ", " + GlobalVariables.getErrorMap());
        }
    }

    public static void approve(String docHeaderId, UserNameFixture user, String expectedNode, DocumentService documentService) throws Exception {
        WorkflowTestUtils.waitForApproveRequest(Long.valueOf(docHeaderId), GlobalVariables.getUserSession().getUniversalUser());
        Document document = documentService.getByDocumentHeaderId(docHeaderId);
        assertTrue("Document should be at routing node " + expectedNode, WorkflowTestUtils.isAtNode(document, expectedNode));
        assertTrue("Document should be enroute.", document.getDocumentHeader().getWorkflowDocument().stateIsEnroute());
        assertTrue(user + " should have an approve request.", document.getDocumentHeader().getWorkflowDocument().isApprovalRequested());
        documentService.approveDocument(document, "Test approving as " + user, null);
    }

    public static <T extends Document> void assertMatch(T document1, T document2) {
        Assert.assertEquals(document1.getDocumentHeader().getFinancialDocumentDescription(), document2.getDocumentHeader().getFinancialDocumentDescription());
        Assert.assertEquals(document1.getDocumentHeader().getExplanation(), document2.getDocumentHeader().getExplanation());
        Assert.assertEquals(document1.getDocumentNumber(), document2.getDocumentNumber());
        Assert.assertEquals(document1.getDocumentHeader().getWorkflowDocument().getDocumentType(), document2.getDocumentHeader().getWorkflowDocument().getDocumentType());


        AccountingDocument d1 = (AccountingDocument) document1;
        AccountingDocument d2 = (AccountingDocument) document2;
        if (StringUtils.isNotBlank(d1.getPostingPeriodCode()) && StringUtils.isNotBlank(d2.getPostingPeriodCode())) {
            // some documents just plain old don't store this b/c the GLPEs get generated with "getCurrentAccountingPeriod()"
            Assert.assertEquals(d1.getPostingPeriodCode(), d2.getPostingPeriodCode());
        }
        Assert.assertEquals(d1.getPostingYear(), d2.getPostingYear());
        Assert.assertEquals(d1.getSourceAccountingLines().size(), d2.getSourceAccountingLines().size());

        for (int i = 0; i < d1.getSourceAccountingLines().size(); i++) {
            d1.getSourceAccountingLine(i).isLike(d2.getSourceAccountingLine(i));
        }
        Assert.assertEquals(d2.getTargetAccountingLines().size(), d2.getTargetAccountingLines().size());
        for (int i = 0; i < d1.getTargetAccountingLines().size(); i++) {
            d1.getTargetAccountingLine(i).isLike(d2.getTargetAccountingLine(i));
        }
    }
}
