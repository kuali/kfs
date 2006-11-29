/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/test/integration/src/org/kuali/kfs/fp/document/GeneralErrorCorrectionDocumentTest.java,v $
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
import static org.kuali.test.fixtures.AccountingLineFixture.GEC_LINE1;
import static org.kuali.test.fixtures.UserNameFixture.KHUNTLEY;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.bo.SourceAccountingLine;
import org.kuali.core.bo.TargetAccountingLine;
import org.kuali.core.document.Document;
import org.kuali.module.financial.bo.GECSourceAccountingLine;
import org.kuali.module.financial.bo.GECTargetAccountingLine;
import org.kuali.test.DocumentTestUtils;
import org.kuali.test.KualiTestBase;
import org.kuali.test.TestsWorkflowViaDatabase;
import org.kuali.test.WithTestSpringContext;
import org.kuali.test.fixtures.AccountingLineFixture;

/**
 * This class is used to test GeneralErrorCorrectionDocument.
 * 
 * 
 */
@WithTestSpringContext(session = KHUNTLEY)
public class GeneralErrorCorrectionDocumentTest extends KualiTestBase {

    public static final Class<GeneralErrorCorrectionDocument> DOCUMENT_CLASS = GeneralErrorCorrectionDocument.class;

    private Document getDocumentParameterFixture() throws Exception {
        return DocumentTestUtils.createDocument(getDocumentService(), GeneralErrorCorrectionDocument.class);
    }

    private List<AccountingLineFixture> getTargetAccountingLineParametersFromFixtures() {
        List<AccountingLineFixture> list = new ArrayList<AccountingLineFixture>();
        list.add(GEC_LINE1);
        return list;
    }

    private List<AccountingLineFixture> getSourceAccountingLineParametersFromFixtures() {
        List<AccountingLineFixture> list = new ArrayList<AccountingLineFixture>();
        list.add(GEC_LINE1);
        return list;
    }

    private GeneralErrorCorrectionDocument buildDocument() throws Exception {
        // put accounting lines into document parameter for later
        GeneralErrorCorrectionDocument document = (GeneralErrorCorrectionDocument) getDocumentParameterFixture();

        // set accountinglines to document
        for (AccountingLineFixture sourceFixture : getSourceAccountingLineParametersFromFixtures()) {

            document.addSourceAccountingLine(sourceFixture.createAccountingLine(GECSourceAccountingLine.class, document.getDocumentNumber(), document.getPostingYear(), document.getNextSourceLineNumber()));
        }

        for (AccountingLineFixture targetFixture : getTargetAccountingLineParametersFromFixtures()) {
            document.addTargetAccountingLine(targetFixture.createAccountingLine(GECTargetAccountingLine.class, document.getDocumentNumber(), document.getPostingYear(), document.getNextTargetLineNumber()));
        }

        return document;
    }


    public final void testAddAccountingLine() throws Exception {
        List<SourceAccountingLine> sourceLines = generateSouceAccountingLines();
        List<TargetAccountingLine> targetLines = generateTargetAccountingLines();
        int expectedSourceTotal = sourceLines.size();
        int expectedTargetTotal = targetLines.size();
        TransactionalDocumentTestUtils.testAddAccountingLine(DocumentTestUtils.createDocument(getDocumentService(), DOCUMENT_CLASS), sourceLines, targetLines, expectedSourceTotal, expectedTargetTotal);
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
    public final void testConvertIntoErrorCorrection() throws Exception {
        TransactionalDocumentTestUtils.testConvertIntoErrorCorrection(buildDocument(), getExpectedPrePeCount(), getDocumentService(), getTransactionalDocumentDictionaryService());
    }

    @TestsWorkflowViaDatabase
    public final void testRouteDocument() throws Exception {
        TransactionalDocumentTestUtils.testRouteDocument(buildDocument(), getDocumentService());
    }

    @TestsWorkflowViaDatabase
    public final void testSaveDocument() throws Exception {
        TransactionalDocumentTestUtils.testSaveDocument(buildDocument(), getDocumentService());
    }

    @TestsWorkflowViaDatabase
    public final void testConvertIntoCopy() throws Exception {
        TransactionalDocumentTestUtils.testConvertIntoCopy(buildDocument(), getDocumentService(), getExpectedPrePeCount());
    }


    // test util methods
    private List<SourceAccountingLine> generateSouceAccountingLines() throws Exception {
        List<SourceAccountingLine> sourceLines = new ArrayList<SourceAccountingLine>();
        // set accountinglines to document
        for (AccountingLineFixture sourceFixture : getSourceAccountingLineParametersFromFixtures()) {
            sourceLines.add(sourceFixture.createAccountingLine(GECSourceAccountingLine.class, sourceFixture.debitCreditCode));
        }

        return sourceLines;
    }

    private List<TargetAccountingLine> generateTargetAccountingLines() throws Exception {
        List<TargetAccountingLine> targetLines = new ArrayList<TargetAccountingLine>();
        for (AccountingLineFixture targetFixture : getTargetAccountingLineParametersFromFixtures()) {
            targetLines.add(targetFixture.createAccountingLine(GECTargetAccountingLine.class, targetFixture.debitCreditCode));
        }

        return targetLines;
    }

    private int getExpectedPrePeCount() {
        return 4;
    }

}
