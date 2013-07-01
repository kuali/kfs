/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.sys.document;

import static org.kuali.kfs.sys.KualiTestAssertionUtils.assertEquality;
import static org.kuali.kfs.sys.KualiTestAssertionUtils.assertInequality;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.AccountingPeriod;
import org.kuali.kfs.coa.service.AccountingPeriodService;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.businessobject.TargetAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.datadictionary.FinancialSystemTransactionalDocumentEntry;
import org.kuali.kfs.sys.document.workflow.WorkflowTestUtils;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.kfs.sys.monitor.ChangeMonitor;
import org.kuali.kfs.sys.monitor.DocumentVersionMonitor;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.TransactionalDocumentDictionaryService;
import org.kuali.rice.krad.bo.AdHocRouteRecipient;
import org.kuali.rice.krad.datadictionary.DataDictionary;
import org.kuali.rice.krad.document.Copyable;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.ErrorMessage;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.workflow.service.WorkflowDocumentService;

public final class AccountingDocumentTestUtils {
    private static final Logger LOG = Logger.getLogger(AccountingDocumentTestUtils.class);

    protected static final int ROUTE_STATUS_CHANGE_WAIT_TIME_SECONDS = 30;
    protected static final int ROUTE_STATUS_CHANGE_INITIAL_WAIT_TIME_SECONDS = 5;

    public static void testAddAccountingLine(AccountingDocument document, List<SourceAccountingLine> sourceLines, List<TargetAccountingLine> targetLines, int expectedSourceTotal, int expectedTargetTotal) throws Exception {
        Assert.assertTrue("expected count should be > 0", (expectedSourceTotal + expectedTargetTotal) > 0);
        Assert.assertTrue("no lines found", (targetLines.size() + sourceLines.size()) > 0);

        Assert.assertEquals("Document should have had no source accounting lines.  Had: " + document.getSourceAccountingLines(), 0, document.getSourceAccountingLines().size());
        Assert.assertEquals("Document should have had no target accounting lines.  Had: " + document.getTargetAccountingLines(), 0, document.getTargetAccountingLines().size());

        // add source lines
        for (SourceAccountingLine sourceLine : sourceLines) {
            document.addSourceAccountingLine(sourceLine);
        }
        // add target lines
        for (TargetAccountingLine targetLine : targetLines) {
            document.addTargetAccountingLine(targetLine);
        }

        Assert.assertEquals("source line count mismatch", expectedSourceTotal, document.getSourceAccountingLines().size());
        Assert.assertEquals("target line count mismatch", expectedTargetTotal, document.getTargetAccountingLines().size());
    }

    public static <T extends AccountingDocument> void testGetNewDocument_byDocumentClass(Class<T> documentClass, DocumentService documentService) throws Exception {
        T document = (T) documentService.getNewDocument(documentClass);
        // verify document was created
        Assert.assertNotNull("document was null",document);
        Assert.assertNotNull("document header was null",document.getDocumentHeader());
        Assert.assertNotNull("document number was null",document.getDocumentHeader().getDocumentNumber());
    }

    public static void testConvertIntoCopy_copyDisallowed(AccountingDocument document, DataDictionaryService dataDictionaryService) throws Exception {
        // change the dataDictionary to disallow copying
        DataDictionary d = dataDictionaryService.getDataDictionary();
        Class documentClass = document.getClass();
        boolean originalValue = d.getDocumentEntry(documentClass.getName()).getAllowsCopy();
        try {
            d.getDocumentEntry(documentClass.getName()).setAllowsCopy(false);

            boolean failedAsExpected = false;
            try {
                ((Copyable) document).toCopy();
            }
            catch (IllegalStateException e) {
                failedAsExpected = true;
            }

            Assert.assertTrue("copy operation should have failed", failedAsExpected);
        }
        finally {
            d.getDocumentEntry(documentClass.getName()).setAllowsCopy(originalValue);
        }
    }

    public static void testConvertIntoErrorCorrection_documentAlreadyCorrected(AccountingDocument document, TransactionalDocumentDictionaryService dictionaryService) throws Exception {

        if (((FinancialSystemTransactionalDocumentEntry)SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getDocumentEntry(document.getClass().getName())).getAllowsErrorCorrection()) {
            document.getFinancialSystemDocumentHeader().setCorrectedByDocumentId("1");

            boolean failedAsExpected = false;
            try {
                ((Correctable) document).toErrorCorrection();
            }
            catch (IllegalStateException e) {
                failedAsExpected = true;
            }

            Assert.assertTrue("error correction should have failed", failedAsExpected);
        }
    }

    public static void testConvertIntoErrorCorrection_errorCorrectionDisallowed(AccountingDocument document, DataDictionaryService dataDictionaryService) throws Exception {
        // change the dataDictionary to disallow errorCorrection
        DataDictionary d = dataDictionaryService.getDataDictionary();
        Class documentClass = document.getClass();
        boolean originalValue = ((FinancialSystemTransactionalDocumentEntry) d.getDocumentEntry(documentClass.getName())).getAllowsErrorCorrection();
        try {
            ((FinancialSystemTransactionalDocumentEntry) d.getDocumentEntry(documentClass.getName())).setAllowsErrorCorrection(false);

            boolean failedAsExpected = false;
            try {
                ((Correctable) document).toErrorCorrection();
            }
            catch (IllegalStateException e) {
                failedAsExpected = true;
            }

            Assert.assertTrue("error correction should have failed",failedAsExpected);
        }
        finally {
            ((FinancialSystemTransactionalDocumentEntry) d.getDocumentEntry(documentClass.getName())).setAllowsErrorCorrection(originalValue);
        }
    }

    public static void testConvertIntoErrorCorrection_invalidYear(AccountingDocument document, TransactionalDocumentDictionaryService dictionaryService, AccountingPeriodService accountingPeriodService) throws Exception {
        if (((FinancialSystemTransactionalDocumentEntry)SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getDocumentEntry(document.getClass().getName())).getAllowsErrorCorrection()) {
            // change to non-current posting year
            Integer postingYear = document.getPostingYear();
            AccountingPeriod accountingPeriod = accountingPeriodService.getByPeriod(document.getAccountingPeriod().getUniversityFiscalPeriodCode(), postingYear - 1);
            Assert.assertNotNull("accounting period invalid for test", accountingPeriod);
            Assert.assertTrue("accounting period invalid (same as current year)", postingYear != accountingPeriod.getUniversityFiscalYear());
            Assert.assertEquals("accounting period invalid. period codes must remain the same", document.getAccountingPeriod().getUniversityFiscalPeriodCode(), accountingPeriod.getUniversityFiscalPeriodCode());
            document.setAccountingPeriod(accountingPeriod);

            boolean failedAsExpected = false;
            try {
                ((Correctable) document).toErrorCorrection();
                Assert.fail("converted into error correction for an invalid year");
            }
            catch (IllegalStateException e) {
                failedAsExpected = true;
            }
            Assert.assertTrue(failedAsExpected);
        }
    }

    /**
     * @ShouldCommitTransactions needed for this test
     * @see ShouldCommitTransactions
     */
    public static void testRouteDocument(FinancialSystemTransactionalDocument document, DocumentService documentService) throws Exception {
        document.prepareForSave();

        Assert.assertFalse("Document was not in proper status for routing.  Was: " + document.getDocumentHeader().getWorkflowDocument().getStatus(),
                DocumentStatus.ENROUTE.equals(document.getDocumentHeader().getWorkflowDocument().getStatus()));
        routeDocument(document, "saving copy source document", null, documentService);

        WorkflowDocument workflowDocument = SpringContext.getBean(WorkflowDocumentService.class).loadWorkflowDocument(document.getDocumentNumber(), UserNameFixture.kfs.getPerson() );
        if (!workflowDocument.isApproved()) {
            WorkflowTestUtils.waitForStatusChange(document.getDocumentNumber(), DocumentStatus.ENROUTE);
        }
    }

    /**
     * @ShouldCommitTransactions needed for this test
     * @see ShouldCommitTransactions
     */

    public static void testConvertIntoErrorCorrection(AccountingDocument document, int expectedPrePECount, DocumentService documentService, TransactionalDocumentDictionaryService dictionaryService) throws Exception {
        if (((FinancialSystemTransactionalDocumentEntry)SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getDocumentEntry(document.getClass().getName())).getAllowsErrorCorrection()) {
            String documentNumber = document.getDocumentNumber();
            LOG.info("Submitting and blanket approving documentNumber to final to test error correction: " + documentNumber);
            // route the original doc, wait for status change
            blanketApproveDocument(document, "blanket approving errorCorrection source document", null, documentService);
            WorkflowTestUtils.waitForDocumentApproval(document.getDocumentNumber());
            // re-pull the document to get any updates made by KEW and the post-processor
            document = (AccountingDocument) documentService.getByDocumentHeaderId(documentNumber);

            // collect some preCorrect data
            String preCorrectId = document.getDocumentNumber();
            String preCorrectCorrectsId = document.getFinancialSystemDocumentHeader().getFinancialDocumentInErrorNumber();

            int preCorrectPECount = document.getGeneralLedgerPendingEntries().size();
            // int preCorrectNoteCount = document.getDocumentHeader().getNotes().size();

            List<? extends SourceAccountingLine> preCorrectSourceLines = (List<? extends SourceAccountingLine>) ObjectUtils.deepCopy(new ArrayList<SourceAccountingLine>(document.getSourceAccountingLines()));
            List<? extends TargetAccountingLine> preCorrectTargetLines = (List<? extends TargetAccountingLine>) ObjectUtils.deepCopy(new ArrayList<TargetAccountingLine>(document.getTargetAccountingLines()));
            // validate preCorrect state
            Assert.assertNotNull(preCorrectId);
            Assert.assertNull(preCorrectCorrectsId);

            Assert.assertEquals(expectedPrePECount, preCorrectPECount);
            // assertEquals(0, preCorrectNoteCount);

            // do the error correction
            ((Correctable) document).toErrorCorrection();
            // compare to preCorrect state
            String postCorrectId = document.getDocumentNumber();
            LOG.info("postcorrect documentHeaderId = " + postCorrectId);
            Assert.assertFalse(postCorrectId.equals(preCorrectId));
            // pending entries should be cleared
            int postCorrectPECount = document.getGeneralLedgerPendingEntries().size();
            LOG.info("postcorrect PE count = " + postCorrectPECount);
            Assert.assertEquals(0, postCorrectPECount);
            // TODO: revisit this is it still needed
            // // count 1 note, compare to "correction" text
            // int postCorrectNoteCount = document.getDocumentHeader().getNotes().size();
            // assertEquals(1, postCorrectNoteCount);
            // DocumentNote note = document.getDocumentHeader().getNote(0);
            // LOG.debug("postcorrect note text = " + note.getFinancialDocumentNoteText());
            // assertTrue(note.getFinancialDocumentNoteText().indexOf("correction") != -1);
            // correctsId should be equal to old id
            String correctsId = document.getFinancialSystemDocumentHeader().getFinancialDocumentInErrorNumber();
            LOG.info("postcorrect correctsId = " + correctsId);
            Assert.assertEquals(preCorrectId, correctsId);
            // accounting lines should have sign reversed on amounts
            List<SourceAccountingLine> postCorrectSourceLines = document.getSourceAccountingLines();
            Assert.assertEquals(preCorrectSourceLines.size(), postCorrectSourceLines.size());
            for (int i = 0; i < preCorrectSourceLines.size(); ++i) {
                SourceAccountingLine preCorrectLine = preCorrectSourceLines.get(i);
                SourceAccountingLine postCorrectLine = postCorrectSourceLines.get(i);

                LOG.info("postcorrect line(docId,amount) = " + i + "(" + postCorrectId + "," + postCorrectLine.getAmount());
                assertEquality(postCorrectId, postCorrectLine.getDocumentNumber());
                assertEquality(preCorrectLine.getAmount().negated(), postCorrectLine.getAmount());
            }

            List<? extends TargetAccountingLine> postCorrectTargetLines = document.getTargetAccountingLines();
            Assert.assertEquals(preCorrectTargetLines.size(), postCorrectTargetLines.size());
            for (int i = 0; i < preCorrectTargetLines.size(); ++i) {
                TargetAccountingLine preCorrectLine = preCorrectTargetLines.get(i);
                TargetAccountingLine postCorrectLine = postCorrectTargetLines.get(i);

                LOG.info("postcorrect line(docId,amount) = " + i + "(" + postCorrectId + "," + postCorrectLine.getAmount());
                assertEquality(postCorrectId, postCorrectLine.getDocumentNumber());
                assertEquality(preCorrectLine.getAmount().negated(), postCorrectLine.getAmount());
            }
        }
    }

    /**
     * @ShouldCommitTransactions needed for this test
     * @see ShouldCommitTransactions
     */
    public static void testSaveDocument(FinancialSystemTransactionalDocument document, DocumentService documentService) throws Exception {
        // get document parameter
        document.prepareForSave();

        // save
        saveDocument(document, documentService);

        // retrieve
        FinancialSystemTransactionalDocument result = (FinancialSystemTransactionalDocument) documentService.getByDocumentHeaderId(document.getDocumentNumber());

        // verify
        assertMatch(document, result);
    }

    /**
     * @ShouldCommitTransactions needed for this test
     * @see ShouldCommitTransactions
     */
    public static void testConvertIntoCopy(AccountingDocument document, DocumentService documentService, int expectedPrePECount) throws Exception {
        // save the original doc, wait for status change
        document.prepareForSave();
        routeDocument(document, "saving copy source document", null, documentService);
        if (!document.getDocumentHeader().getWorkflowDocument().isApproved()) {
            WorkflowTestUtils.waitForStatusChange(document.getDocumentNumber(), DocumentStatus.ENROUTE);
        }
        // collect some preCopy data
        String preCopyId = document.getDocumentNumber();
        String preCopyCopiedFromId = document.getDocumentHeader().getDocumentTemplateNumber();

        int preCopyPECount = document.getGeneralLedgerPendingEntries().size();
        // int preCopyNoteCount = document.getDocumentHeader().getNotes().size();
        DocumentStatus preCopyStatus = document.getDocumentHeader().getWorkflowDocument().getStatus();

        List<? extends SourceAccountingLine> preCopySourceLines = (List<? extends SourceAccountingLine>) ObjectUtils.deepCopy(new ArrayList(document.getSourceAccountingLines()));
        List<? extends TargetAccountingLine> preCopyTargetLines = (List<? extends TargetAccountingLine>) ObjectUtils.deepCopy(new ArrayList(document.getTargetAccountingLines()));
        // validate preCopy state
        Assert.assertNotNull(preCopyId);
        Assert.assertNull(preCopyCopiedFromId);

        Assert.assertEquals(expectedPrePECount, preCopyPECount);

        // do the copy
        ((Copyable) document).toCopy();
        // compare to preCopy state

        String postCopyId = document.getDocumentNumber();
        Assert.assertFalse(postCopyId.equals(preCopyId));
        // verify that docStatus has changed
        DocumentStatus postCopyStatus = document.getDocumentHeader().getWorkflowDocument().getStatus();
        Assert.assertFalse(postCopyStatus.equals(preCopyStatus));
        // pending entries should be cleared
        int postCopyPECount = document.getGeneralLedgerPendingEntries().size();
        Assert.assertEquals(0, postCopyPECount);

        // TODO: revisit this is it still needed
        // count 1 note, compare to "copied" text
        // int postCopyNoteCount = document.getDocumentHeader().getNotes().size();
        // assertEquals(1, postCopyNoteCount);
        // DocumentNote note = document.getDocumentHeader().getNote(0);
        // assertTrue(note.getFinancialDocumentNoteText().indexOf("copied from") != -1);
        // copiedFrom should be equal to old id
        String copiedFromId = document.getDocumentHeader().getDocumentTemplateNumber();
        Assert.assertEquals(preCopyId, copiedFromId);
        // accounting lines should be have different docHeaderIds but same
        // amounts
        List<? extends SourceAccountingLine> postCopySourceLines = document.getSourceAccountingLines();
        Assert.assertEquals(preCopySourceLines.size(), postCopySourceLines.size());
        for (int i = 0; i < preCopySourceLines.size(); ++i) {
            SourceAccountingLine preCopyLine = preCopySourceLines.get(i);
            SourceAccountingLine postCopyLine = postCopySourceLines.get(i);

            assertInequality(preCopyLine.getDocumentNumber(), postCopyLine.getDocumentNumber());
            assertEquality(preCopyLine.getAmount(), postCopyLine.getAmount());
        }

        List<? extends TargetAccountingLine> postCopyTargetLines = document.getTargetAccountingLines();
        Assert.assertEquals(preCopyTargetLines.size(), postCopyTargetLines.size());
        for (int i = 0; i < preCopyTargetLines.size(); ++i) {
            TargetAccountingLine preCopyLine = preCopyTargetLines.get(i);
            TargetAccountingLine postCopyLine = postCopyTargetLines.get(i);

            assertInequality(preCopyLine.getDocumentNumber(), postCopyLine.getDocumentNumber());
            assertEquality(preCopyLine.getAmount(), postCopyLine.getAmount());
        }
    }

    // helper methods
    public static void routeDocument(FinancialSystemTransactionalDocument document, String annotation, List<AdHocRouteRecipient> adHocRoutingRecipients, DocumentService documentService) throws WorkflowException {
        try {
            documentService.routeDocument(document, annotation, adHocRoutingRecipients);
        }
        catch (ValidationException e) {
            // If the business rule evaluation fails then give us more info for debugging this test.
            Assert.fail(e.getMessage() + ", " + dumpMessageMapErrors());
        }
    }

    // helper methods
    public static void blanketApproveDocument(Document document, String annotation, List<AdHocRouteRecipient> adHocRoutingRecipients, DocumentService documentService) throws WorkflowException {
        LOG.info( "Blanket Approving Document: " + document.getDocumentNumber() + " / " + annotation );
        try {
            documentService.blanketApproveDocument(document, annotation, adHocRoutingRecipients);
        } catch (ValidationException e) {
            // If the business rule evaluation fails then give us more info for debugging this test.
            Assert.fail(e.getMessage() + ", " + dumpMessageMapErrors());
            LOG.error( "Blanket Approval failed: " + document, e );
        }
    }

    public static void approveDocument(AccountingDocument document, DocumentService documentService) throws Exception {
        Long initialVersion = document.getVersionNumber();
        documentService.approveDocument(document, "approving test doc", null);

        DocumentVersionMonitor vm = new DocumentVersionMonitor(documentService, document.getDocumentNumber(), initialVersion);
        Assert.assertTrue("Document did not complete routing to the expected status (" + vm + ") within the time limit",ChangeMonitor.waitUntilChange(vm, ROUTE_STATUS_CHANGE_WAIT_TIME_SECONDS, ROUTE_STATUS_CHANGE_INITIAL_WAIT_TIME_SECONDS));
    }

    public static void routeDocument(AccountingDocument document, DocumentService documentService) throws WorkflowException {
        Assert.assertFalse("Document not in correct state before routing. Was: " + document.getDocumentHeader().getWorkflowDocument().getStatus(), DocumentStatus.ENROUTE.equals(document.getDocumentHeader().getWorkflowDocument().getStatus()));
        documentService.routeDocument(document, "routing test doc", null);

        WorkflowTestUtils.waitForStatusChange(document.getDocumentNumber(), DocumentStatus.ENROUTE);
    }

    public static void blanketApproveDocument(AccountingDocument document, DocumentService documentService) throws WorkflowException {
        Assert.assertFalse("Document not in correct state before routing. Was: " + document.getDocumentHeader().getWorkflowDocument().getStatus(), DocumentStatus.ENROUTE.equals(document.getDocumentHeader().getWorkflowDocument().getStatus()));
        documentService.blanketApproveDocument(document, "routing test doc", null);

        WorkflowTestUtils.waitForDocumentApproval(document.getDocumentNumber());
    }

    public static void saveDocument(FinancialSystemTransactionalDocument document, DocumentService documentService) throws WorkflowException {
        try {
            documentService.saveDocument(document);
        }
        catch (ValidationException e) {
            // If the business rule evaluation fails then give us more info for debugging this test.
            Assert.fail("Document save failed with ValidationException: " + e.getMessage() + ", " + dumpMessageMapErrors());
        }
    }

    public static void approve(String docHeaderId, UserNameFixture user, String expectedNode, DocumentService documentService) throws Exception {
        WorkflowTestUtils.waitForApproveRequest(docHeaderId, GlobalVariables.getUserSession().getPerson());
        Document document = documentService.getByDocumentHeaderId(docHeaderId);
        Assert.assertTrue("Document should be at routing node " + expectedNode, WorkflowTestUtils.isAtNode(document, expectedNode));
        Assert.assertTrue("Document should be enroute.", document.getDocumentHeader().getWorkflowDocument().isEnroute());
        Assert.assertTrue(user + " should have an approve request.", document.getDocumentHeader().getWorkflowDocument().isApprovalRequested());
        documentService.approveDocument(document, "Test approving as " + user, null);
    }

    public static <T extends Document> void assertMatch(T document1, T document2) {
        Assert.assertEquals("Document number does not match", document1.getDocumentNumber(), document2.getDocumentNumber());
        Assert.assertEquals("Document type does not match", document1.getDocumentHeader().getWorkflowDocument().getDocumentTypeName(), document2.getDocumentHeader().getWorkflowDocument().getDocumentTypeName());


        AccountingDocument d1 = (AccountingDocument) document1;
        AccountingDocument d2 = (AccountingDocument) document2;
        if (StringUtils.isNotBlank(d1.getPostingPeriodCode()) && StringUtils.isNotBlank(d2.getPostingPeriodCode())) {
            // some documents just plain old don't store this b/c the GLPEs get generated with "getCurrentAccountingPeriod()"
            Assert.assertEquals("Posting Period does not match", d1.getPostingPeriodCode(), d2.getPostingPeriodCode());
        }
        Assert.assertEquals("Posting year does not match", d1.getPostingYear(), d2.getPostingYear());
        Assert.assertEquals("Number of source accounting lines does not match", d1.getSourceAccountingLines().size(), d2.getSourceAccountingLines().size());

        for (int i = 0; i < d1.getSourceAccountingLines().size(); i++) {
            d1.getSourceAccountingLine(i).isLike(d2.getSourceAccountingLine(i));
        }
        Assert.assertEquals("Number of target accounting lines does not match", d1.getTargetAccountingLines().size(), d2.getTargetAccountingLines().size());
        for (int i = 0; i < d1.getTargetAccountingLines().size(); i++) {
            d1.getTargetAccountingLine(i).isLike(d2.getTargetAccountingLine(i));
        }
    }

    protected static String dumpMessageMapErrors() {
        if (GlobalVariables.getMessageMap().hasNoErrors()) {
            return "";
        }

        StringBuilder message = new StringBuilder();
        for ( String key : GlobalVariables.getMessageMap().getErrorMessages().keySet() ) {
            List<ErrorMessage> errorList = GlobalVariables.getMessageMap().getErrorMessages().get(key);

            for ( ErrorMessage em : errorList ) {
                message.append(key).append(" = ").append( em.getErrorKey() );
                if (em.getMessageParameters() != null) {
                    message.append( " : " );
                    String delim = "";
                    for ( String parm : em.getMessageParameters() ) {
                        message.append(delim).append("'").append(parm).append("'");
                        if ("".equals(delim)) {
                            delim = ", ";
                        }
                    }
                }
            }
            message.append( '\n' );
        }
        return message.toString();
    }
}

