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
import static org.kuali.kfs.sys.fixture.AccountingLineFixture.LINE1;
import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.kuali.kfs.coa.service.AccountingPeriodService;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.DocumentTestUtils;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.businessobject.TargetAccountingLine;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocumentTestUtils;
import org.kuali.kfs.sys.fixture.AccountingLineFixture;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.TransactionalDocumentDictionaryService;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.DocumentService;

/**
 * This class is used to test YearEndTransferOfFundsDocument. Note that structurally, there is no difference between a
 * YearEndTransferOfFundsDocument and a regular TransferOfFundsDocument other than they have different document types and that this
 * one posts to the year end accouting period.
 */
@ConfigureContext(session = khuntley)
public class YearEndTransferOfFundsDocumentTest extends KualiTestBase {
    public static final Class<YearEndTransferOfFundsDocument> DOCUMENT_CLASS = YearEndTransferOfFundsDocument.class;

    private Document getDocumentParameterFixture() throws Exception {
        return DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), YearEndTransferOfFundsDocument.class);
    }

    private List<AccountingLineFixture> getTargetAccountingLineParametersFromFixtures() {
        List<AccountingLineFixture> list = new ArrayList<AccountingLineFixture>();
        list.add(LINE1);
        return list;
    }

    private List<AccountingLineFixture> getSourceAccountingLineParametersFromFixtures() {
        List<AccountingLineFixture> list = new ArrayList<AccountingLineFixture>();
        list.add(LINE1);
        return list;
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
        YearEndTransferOfFundsDocument document = buildDocument();
        Set<String> persistedObjectCodes = YearEndObjectCodePersistenceUtils.persistPreviousYearObjectCodesForDocument(document);
        AccountingDocumentTestUtils.testConvertIntoErrorCorrection(document, getExpectedPrePeCount(), SpringContext.getBean(DocumentService.class), SpringContext.getBean(TransactionalDocumentDictionaryService.class));
        YearEndObjectCodePersistenceUtils.removePreviousYearObjectCodes(persistedObjectCodes);
    }

    @ConfigureContext(session = khuntley, shouldCommitTransactions = true)
    public final void testRouteDocument() throws Exception {
        YearEndTransferOfFundsDocument document = buildDocument();
        Set<String> persistedObjectCodes = YearEndObjectCodePersistenceUtils.persistPreviousYearObjectCodesForDocument(document);
        AccountingDocumentTestUtils.testRouteDocument(document, SpringContext.getBean(DocumentService.class));
        YearEndObjectCodePersistenceUtils.removePreviousYearObjectCodes(persistedObjectCodes);
    }

    @ConfigureContext(session = khuntley, shouldCommitTransactions = true)
    public final void testSaveDocument() throws Exception {
        YearEndTransferOfFundsDocument document = buildDocument();
        Set<String> persistedObjectCodes = YearEndObjectCodePersistenceUtils.persistPreviousYearObjectCodesForDocument(document);
        AccountingDocumentTestUtils.testSaveDocument(document, SpringContext.getBean(DocumentService.class));
        YearEndObjectCodePersistenceUtils.removePreviousYearObjectCodes(persistedObjectCodes);
    }

    @ConfigureContext(session = khuntley, shouldCommitTransactions = true)
    public final void testConvertIntoCopy() throws Exception {
        YearEndTransferOfFundsDocument document = buildDocument();
        Set<String> persistedObjectCodes = YearEndObjectCodePersistenceUtils.persistPreviousYearObjectCodesForDocument(document);
        AccountingDocumentTestUtils.testConvertIntoCopy(document, SpringContext.getBean(DocumentService.class), getExpectedPrePeCount());
        YearEndObjectCodePersistenceUtils.removePreviousYearObjectCodes(persistedObjectCodes);
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

    private YearEndTransferOfFundsDocument buildDocument() throws Exception {
        // put accounting lines into document parameter for later
        YearEndTransferOfFundsDocument document = (YearEndTransferOfFundsDocument) getDocumentParameterFixture();

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

