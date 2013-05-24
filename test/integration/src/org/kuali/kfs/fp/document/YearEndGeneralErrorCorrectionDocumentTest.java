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

import static org.kuali.kfs.sys.document.AccountingDocumentTestUtils.testGetNewDocument_byDocumentClass;
import static org.kuali.kfs.sys.fixture.AccountingLineFixture.GEC_LINE1;
import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.kuali.kfs.coa.service.AccountingPeriodService;
import org.kuali.kfs.fp.businessobject.GECSourceAccountingLine;
import org.kuali.kfs.fp.businessobject.GECTargetAccountingLine;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.DocumentTestUtils;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.businessobject.TargetAccountingLine;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.kfs.sys.document.AccountingDocumentTestUtils;
import org.kuali.kfs.sys.fixture.AccountingLineFixture;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.TransactionalDocumentDictionaryService;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.DocumentService;

/**
 * This class is used to test GeneralErrorCorrectionDocument.
 */
@ConfigureContext(session = khuntley)
public class YearEndGeneralErrorCorrectionDocumentTest extends KualiTestBase {

    public static final Class<YearEndGeneralErrorCorrectionDocument> DOCUMENT_CLASS = YearEndGeneralErrorCorrectionDocument.class;

    private Document getDocumentParameterFixture() throws Exception {
        return DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), YearEndGeneralErrorCorrectionDocument.class);
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

    private YearEndGeneralErrorCorrectionDocument buildDocument() throws Exception {
        // put accounting lines into document parameter for later
        final Integer postingYear = TestUtils.getFiscalYearForTesting();
        YearEndGeneralErrorCorrectionDocument document = (YearEndGeneralErrorCorrectionDocument) getDocumentParameterFixture();

        // set accountinglines to document
        for (AccountingLineFixture sourceFixture : getSourceAccountingLineParametersFromFixtures()) {
            AccountingLine accountingLine = sourceFixture.createAccountingLine(GECSourceAccountingLine.class, document.getDocumentNumber(), document.getPostingYear(), document.getNextSourceLineNumber());
            accountingLine.setPostingYear(postingYear);
            document.addSourceAccountingLine((SourceAccountingLine)accountingLine);
        }

        for (AccountingLineFixture targetFixture : getTargetAccountingLineParametersFromFixtures()) {
            AccountingLine accountingLine = targetFixture.createAccountingLine(GECTargetAccountingLine.class, document.getDocumentNumber(), document.getPostingYear(), document.getNextTargetLineNumber());
            accountingLine.setPostingYear(postingYear);
            document.addTargetAccountingLine((TargetAccountingLine)accountingLine);
        }

        return document;
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

    public final void testConvertIntoErrorCorrection_invalidYear() throws Exception {
        AccountingDocumentTestUtils.testConvertIntoErrorCorrection_invalidYear(buildDocument(), SpringContext.getBean(TransactionalDocumentDictionaryService.class), SpringContext.getBean(AccountingPeriodService.class));
    }

    @ConfigureContext(session = khuntley, shouldCommitTransactions = true)
    public final void testConvertIntoErrorCorrection() throws Exception {
        YearEndGeneralErrorCorrectionDocument document = buildDocument();
        Set<String> persistedObjectCodes = YearEndObjectCodePersistenceUtils.persistPreviousYearObjectCodesForDocument(document);
        AccountingDocumentTestUtils.testConvertIntoErrorCorrection(document, getExpectedPrePeCount(), SpringContext.getBean(DocumentService.class), SpringContext.getBean(TransactionalDocumentDictionaryService.class));
        YearEndObjectCodePersistenceUtils.removePreviousYearObjectCodes(persistedObjectCodes);
    }

    @ConfigureContext(session = khuntley, shouldCommitTransactions = true)
    public final void testRouteDocument() throws Exception {
        YearEndGeneralErrorCorrectionDocument document = buildDocument();
        Set<String> persistedObjectCodes = YearEndObjectCodePersistenceUtils.persistPreviousYearObjectCodesForDocument(document);
        AccountingDocumentTestUtils.testRouteDocument(buildDocument(), SpringContext.getBean(DocumentService.class));
        YearEndObjectCodePersistenceUtils.removePreviousYearObjectCodes(persistedObjectCodes);
    }

    @ConfigureContext(session = khuntley, shouldCommitTransactions = true)
    public final void testSaveDocument() throws Exception {
        YearEndGeneralErrorCorrectionDocument document = buildDocument();
        Set<String> persistedObjectCodes = YearEndObjectCodePersistenceUtils.persistPreviousYearObjectCodesForDocument(document);
        AccountingDocumentTestUtils.testSaveDocument(buildDocument(), SpringContext.getBean(DocumentService.class));
        YearEndObjectCodePersistenceUtils.removePreviousYearObjectCodes(persistedObjectCodes);
    }

    @ConfigureContext(session = khuntley, shouldCommitTransactions = true)
    public final void testConvertIntoCopy() throws Exception {
        YearEndGeneralErrorCorrectionDocument document = buildDocument();
        Set<String> persistedObjectCodes = YearEndObjectCodePersistenceUtils.persistPreviousYearObjectCodesForDocument(document);
        AccountingDocumentTestUtils.testConvertIntoCopy(buildDocument(), SpringContext.getBean(DocumentService.class), getExpectedPrePeCount());
        YearEndObjectCodePersistenceUtils.removePreviousYearObjectCodes(persistedObjectCodes);
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

