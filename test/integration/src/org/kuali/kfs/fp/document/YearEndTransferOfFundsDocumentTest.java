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

import static org.kuali.kfs.util.SpringServiceLocator.getAccountingPeriodService;
import static org.kuali.module.financial.document.AccountingDocumentTestUtils.testGetNewDocument_byDocumentClass;
import static org.kuali.rice.KNSServiceLocator.getDataDictionaryService;
import static org.kuali.rice.KNSServiceLocator.getDocumentService;
import static org.kuali.rice.KNSServiceLocator.getTransactionalDocumentDictionaryService;
import static org.kuali.test.fixtures.AccountingLineFixture.LINE1;
import static org.kuali.test.fixtures.UserNameFixture.KHUNTLEY;

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.document.Document;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.bo.TargetAccountingLine;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.test.DocumentTestUtils;
import org.kuali.test.ConfigureContext;
import org.kuali.test.fixtures.AccountingLineFixture;

/**
 * This class is used to test YearEndTransferOfFundsDocument. Note that structurally, there is no difference between a
 * YearEndTransferOfFundsDocument and a regular TransferOfFundsDocument other than they have different document types and that this
 * one posts to the year end accouting period.
 * 
 * 
 */
@ConfigureContext(session = KHUNTLEY)
public class YearEndTransferOfFundsDocumentTest extends KualiTestBase {
    public static final Class<YearEndTransferOfFundsDocument> DOCUMENT_CLASS = YearEndTransferOfFundsDocument.class;

    private Document getDocumentParameterFixture() throws Exception {
        return DocumentTestUtils.createDocument(getDocumentService(), YearEndTransferOfFundsDocument.class);
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

    @ConfigureContext(session = KHUNTLEY, shouldCommitTransactions=true)
    public final void testConvertIntoErrorCorrection() throws Exception {
        AccountingDocumentTestUtils.testConvertIntoErrorCorrection(buildDocument(), getExpectedPrePeCount(), getDocumentService(), getTransactionalDocumentDictionaryService());
    }

    @ConfigureContext(session = KHUNTLEY, shouldCommitTransactions=true)
    public final void testRouteDocument() throws Exception {
        AccountingDocumentTestUtils.testRouteDocument(buildDocument(), getDocumentService());
    }

    @ConfigureContext(session = KHUNTLEY, shouldCommitTransactions=true)
    public final void testSaveDocument() throws Exception {
        AccountingDocumentTestUtils.testSaveDocument(buildDocument(), getDocumentService());
    }

    @ConfigureContext(session = KHUNTLEY, shouldCommitTransactions=true)
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
