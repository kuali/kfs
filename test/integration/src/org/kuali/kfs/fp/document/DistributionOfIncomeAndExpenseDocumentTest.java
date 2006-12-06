/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/test/integration/src/org/kuali/kfs/fp/document/DistributionOfIncomeAndExpenseDocumentTest.java,v $
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

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.core.bo.SourceAccountingLine;
import org.kuali.core.bo.TargetAccountingLine;
import org.kuali.core.document.Document;
import static org.kuali.core.util.SpringServiceLocator.getDataDictionaryService;
import static org.kuali.core.util.SpringServiceLocator.getDocumentService;
import static org.kuali.core.util.SpringServiceLocator.getTransactionalDocumentDictionaryService;
import static org.kuali.module.financial.document.TransactionalDocumentTestUtils.testGetNewDocument_byDocumentClass;
import org.kuali.test.DocumentTestUtils;
import org.kuali.test.KualiTestBase;
import org.kuali.test.TestsWorkflowViaDatabase;
import org.kuali.test.WithTestSpringContext;
import org.kuali.test.fixtures.AccountingLineFixture;
import static org.kuali.test.fixtures.AccountingLineFixture.LINE2;
import static org.kuali.test.fixtures.UserNameFixture.KHUNTLEY;
import org.kuali.test.suite.AnnotationTestSuite;
import org.kuali.test.suite.OftenUsefulSuite;
import org.kuali.test.suite.RelatesTo;
import static org.kuali.test.suite.RelatesTo.JiraIssue.KULRNE1612;

/**
 * This class is used to test DistributionOfIncomeAndExpenseDocument.
 * 
 * 
 */
@WithTestSpringContext(session = KHUNTLEY)
public class DistributionOfIncomeAndExpenseDocumentTest extends KualiTestBase {
    private static final Log LOG = LogFactory.getLog(DistributionOfIncomeAndExpenseDocumentTest.class);
    public static final Class<DistributionOfIncomeAndExpenseDocument> DOCUMENT_CLASS = DistributionOfIncomeAndExpenseDocument.class;

    private Document getDocumentParameterFixture() throws Exception {
        return DocumentTestUtils.createDocument(getDocumentService(), DistributionOfIncomeAndExpenseDocument.class);
    }

    private List<AccountingLineFixture> getTargetAccountingLineParametersFromFixtures() {
        List<AccountingLineFixture> list = new ArrayList<AccountingLineFixture>();
        list.add(LINE2);
        return list;
    }

    private List<AccountingLineFixture> getSourceAccountingLineParametersFromFixtures() {
        List<AccountingLineFixture> list = new ArrayList<AccountingLineFixture>();
        list.add(LINE2);
        return list;
    }


    /*
     * This test fails related to https://test.kuali.org/jira/browse/KULEDOCS-1662
     */
    @RelatesTo(KULRNE1612)
    @AnnotationTestSuite(OftenUsefulSuite.class)
    @TestsWorkflowViaDatabase
    public final void testKULEDOCS_1401() throws Exception {
        String testDocId = null;

        try {
            {
                // create a DIE doc
                DistributionOfIncomeAndExpenseDocument createdDoc = (DistributionOfIncomeAndExpenseDocument) getDocumentService().getNewDocument(DistributionOfIncomeAndExpenseDocument.class);
                testDocId = createdDoc.getDocumentNumber();

                // populate and save it
                createdDoc.getDocumentHeader().setFinancialDocumentDescription("created by testKULEDOCS_1401");
                createdDoc.setExplanation("reproducing KULEDOCS_1401");

                createdDoc.addSourceAccountingLine(getSourceAccountingLineAccessibleAccount());
                createdDoc.addTargetAccountingLine(getTargetAccountingLineAccessibleAccount());

                getDocumentService().saveDocument(createdDoc, null, null);
            }

            {
                // change the accountingLine totals (by adding new lines)
                DistributionOfIncomeAndExpenseDocument savedDoc = (DistributionOfIncomeAndExpenseDocument) getDocumentService().getByDocumentHeaderId(testDocId);
                savedDoc.addSourceAccountingLine(getSourceAccountingLineAccessibleAccount());
                savedDoc.addTargetAccountingLine(getTargetAccountingLineAccessibleAccount());

                // blanketapprove updated doc
                getDocumentService().blanketApproveDocument(savedDoc, "blanketapproving updated doc", null);
            }
        }
        finally {
            // clean things up, if possible
            if (testDocId != null) {
                DistributionOfIncomeAndExpenseDocument cancelingDoc = (DistributionOfIncomeAndExpenseDocument) getDocumentService().getByDocumentHeaderId(testDocId);
                if (cancelingDoc != null) {
                    try {
                        getDocumentService().cancelDocument(cancelingDoc, "cleaning up after test");
                    }
                    catch (RuntimeException e) {
                        LOG.error("caught RuntimeException canceling document: " + e.getMessage());
                    }
                }
            }
        }
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

    public final void testConvertIntoCopy_copyDisallowed() throws Exception {
        TransactionalDocumentTestUtils.testConvertIntoCopy_copyDisallowed(buildDocument(), getDataDictionaryService());

    }

    public final void testConvertIntoErrorCorrection_documentAlreadyCorrected() throws Exception {
        TransactionalDocumentTestUtils.testConvertIntoErrorCorrection_documentAlreadyCorrected(buildDocument(), getTransactionalDocumentDictionaryService());
    }

    public final void testConvertIntoErrorCorrection_errorCorrectionDisallowed() throws Exception {
        TransactionalDocumentTestUtils.testConvertIntoErrorCorrection_errorCorrectionDisallowed(buildDocument(), getDataDictionaryService());
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
    public void testConvertIntoCopy() throws Exception {
        TransactionalDocumentTestUtils.testConvertIntoCopy(buildDocument(), getDocumentService(), getExpectedPrePeCount());
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

    private DistributionOfIncomeAndExpenseDocument buildDocument() throws Exception {
        // put accounting lines into document parameter for later
        DistributionOfIncomeAndExpenseDocument document = (DistributionOfIncomeAndExpenseDocument) getDocumentParameterFixture();

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

    private SourceAccountingLine getSourceAccountingLineAccessibleAccount() throws Exception {
        return LINE2.createSourceAccountingLine();
    }

    private TargetAccountingLine getTargetAccountingLineAccessibleAccount() throws Exception {
        return LINE2.createTargetAccountingLine();
    }

}
