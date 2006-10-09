package org.kuali.module.financial.document;

/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kuali.core.document.Document;
import org.kuali.core.document.TransactionalDocumentTestBase;
import static org.kuali.core.util.SpringServiceLocator.getDocumentService;
import org.kuali.test.DocumentTestUtils;
import org.kuali.test.TestsWorkflowViaDatabase;
import org.kuali.test.WithTestSpringContext;
import org.kuali.test.fixtures.AccountingLineFixture;
import static org.kuali.test.fixtures.AccountingLineFixture.LINE2;
import static org.kuali.test.fixtures.UserNameFixture.KHUNTLEY;

/**
 * This class is used to test DistributionOfIncomeAndExpenseDocument.
 * 
 * 
 */
@WithTestSpringContext(session = KHUNTLEY)
public class DistributionOfIncomeAndExpenseDocumentTest extends TransactionalDocumentTestBase {
    private static final Log LOG = LogFactory.getLog(DistributionOfIncomeAndExpenseDocumentTest.class);

    /**
     * @see org.kuali.core.document.DocumentTestBase#getDocumentParameterFixture()
     */
    @Override
    public Document getDocumentParameterFixture() throws Exception{
        return DocumentTestUtils.createDocument(getDocumentService(), DistributionOfIncomeAndExpenseDocument.class);
    }

    /**
     * 
     * @see org.kuali.core.document.TransactionalDocumentTestBase#getTargetAccountingLineParametersFromFixtures()
     */
    @Override
    public List<AccountingLineFixture> getTargetAccountingLineParametersFromFixtures() {
        List<AccountingLineFixture> list = new ArrayList<AccountingLineFixture>();
        list.add(LINE2);
        return list;
    }

    /**
     * 
     * @see org.kuali.core.document.TransactionalDocumentTestBase#getSourceAccountingLineParametersFromFixtures()
     */
    @Override
    public List<AccountingLineFixture> getSourceAccountingLineParametersFromFixtures() {
	List<AccountingLineFixture> list = new ArrayList<AccountingLineFixture>();
        list.add(LINE2);
        return list;
    }


    // START TEST METHODS
    /**
     * Overrides the parent to do nothing since the DofI&E doesn't set the posting period in the record it stores. This test doesn't
     * apply to this type of document.
     */
    @Override
    public final void testConvertIntoCopy_invalidYear() throws Exception {
        // do nothing to pass
    }

    /**
     * Overrides the parent to do nothing since the DofI&E doesn't set the posting period in the record it stores. This test doesn't
     * apply to this type of document.
     */
    @Override
    public final void testConvertIntoErrorCorrection_invalidYear() throws Exception {
        // do nothing to pass
    }

    /* Removing this test until the following Phase 2 issue is closed.
     * https://test.kuali.org/jira/browse/KULEDOCS-1662
     */
    /*
    @TestsWorkflowViaDatabase
    public void testKULEDOCS_1401() throws Exception {
        String testDocId = null;

        try {
            {
                // create a DIE doc
                DistributionOfIncomeAndExpenseDocument createdDoc = (DistributionOfIncomeAndExpenseDocument) getDocumentService().getNewDocument(DistributionOfIncomeAndExpenseDocument.class);
                testDocId = createdDoc.getFinancialDocumentNumber();

                // populate and save it
                createdDoc.getDocumentHeader().setFinancialDocumentDescription("created by testKULEDOCS_1401");
                createdDoc.setExplanation("reproducing KULEDOCS_1401");

                createdDoc.addSourceAccountingLine(getSourceAccountingLineAccessibleAccount());
                createdDoc.addTargetAccountingLine(getTargetAccountingLineAccessibleAccount());

                getDocumentService().saveDocument(createdDoc);
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
    */
}
