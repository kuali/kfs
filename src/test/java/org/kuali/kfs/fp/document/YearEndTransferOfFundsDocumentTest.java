/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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

