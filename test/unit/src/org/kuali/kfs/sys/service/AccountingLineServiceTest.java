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
package org.kuali.core.service;

import java.util.Iterator;
import java.util.List;

import org.kuali.core.bo.AccountingLine;
import org.kuali.core.bo.AccountingLineBase;
import org.kuali.core.bo.SourceAccountingLine;
import org.kuali.core.bo.TargetAccountingLine;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.test.DocumentTestUtils;
import org.kuali.test.KualiTestBaseWithFixtures;
import org.kuali.test.WithTestSpringContext;

/**
 * This class tests the AccountingLine service.
 * 
 * @author Kuali Nervous System Team ()
 */
@WithTestSpringContext
public class AccountingLineServiceTest extends KualiTestBaseWithFixtures {

    private AccountingLineService accountingLineService;
    private SourceAccountingLine sline;
    private TargetAccountingLine tline;

    protected void setUp() throws Exception {
        super.setUp();
        accountingLineService = SpringServiceLocator.getAccountingLineService();

        // setup line
        sline = DocumentTestUtils.createSourceLine(TestConstants.Data4.DOC_HDR_ID, TestConstants.Data4.CHART_CODE, TestConstants.Data4.ACCOUNT, TestConstants.Data4.SUBACCOUNT, TestConstants.Data4.OBJECT_CODE, TestConstants.Data4.SUBOBJECT_CODE, TestConstants.Data4.PROJECT_CODE, TestConstants.Data4.POSTING_YEAR.intValue(), TestConstants.Data4.AMOUNT, TestConstants.Data4.SEQUENCE_NUMBER.intValue(), TestConstants.Data4.REF_NUMBER, TestConstants.Data4.REF_TYPE_CODE, TestConstants.Data4.BALANCE_TYPE_CODE, TestConstants.Data4.REF_ORIGIN_CODE, TestConstants.Data4.DEBIT_CREDIT_CODE, TestConstants.Data4.ENCUMBRANCE_UPDATE_CODE, TestConstants.Data4.OBJECT_TYPE_CODE);

        tline = DocumentTestUtils.createTargetLine(TestConstants.Data4.DOC_HDR_ID, TestConstants.Data4.CHART_CODE, TestConstants.Data4.ACCOUNT, TestConstants.Data4.SUBACCOUNT, TestConstants.Data4.OBJECT_CODE, TestConstants.Data4.SUBOBJECT_CODE, TestConstants.Data4.PROJECT_CODE, TestConstants.Data4.POSTING_YEAR.intValue(), TestConstants.Data4.AMOUNT, TestConstants.Data4.SEQUENCE_NUMBER.intValue(), TestConstants.Data4.REF_NUMBER, TestConstants.Data4.REF_TYPE_CODE, TestConstants.Data4.BALANCE_TYPE_CODE, TestConstants.Data4.REF_ORIGIN_CODE, TestConstants.Data4.DEBIT_CREDIT_CODE, TestConstants.Data4.ENCUMBRANCE_UPDATE_CODE, TestConstants.Data4.OBJECT_TYPE_CODE);
    }

    /**
     * Tests an accounting line is correctly persisted when the primitives of the line are set.
     * 
     * @throws Exception
     */
    public void testPersistence() throws Exception {
        AccountingLine line = null;
        try {
            accountingLineService.save(sline);

            List sourceLines = accountingLineService.getByDocumentHeaderId(SourceAccountingLine.class, TestConstants.Data4.DOC_HDR_ID);
            assertTrue(sourceLines.size() > 0);

            line = (AccountingLine) sourceLines.get(0);

            assertEquals(TestConstants.Data4.CHART_CODE, line.getChartOfAccountsCode());
            assertEquals(TestConstants.Data4.ACCOUNT, line.getAccountNumber());
            assertEquals(TestConstants.Data4.SUBACCOUNT, line.getSubAccountNumber());
            assertEquals(TestConstants.Data4.OBJECT_CODE, line.getFinancialObjectCode());
            assertEquals(TestConstants.Data4.SUBOBJECT_CODE, line.getFinancialSubObjectCode());
        }
        finally {
            accountingLineService.deleteAccountingLine((AccountingLineBase) line);
        }
    }


    /**
     * Tests reference objects are being corrected refreshed from changed pritive values.
     */
    public void testRefresh() {
        assertEquals(TestConstants.Data4.CHART_CODE, sline.getAccount().getChartOfAccountsCode());
        assertEquals(TestConstants.Data4.ACCOUNT, sline.getAccount().getAccountNumber());

        sline.setAccountNumber(TestConstants.Data4.ACCOUNT2);
        sline.refresh();

        assertEquals(TestConstants.Data4.CHART_CODE, sline.getAccount().getChartOfAccountsCode());
        assertEquals(TestConstants.Data4.ACCOUNT2, sline.getAccount().getAccountNumber());

        sline.setChartOfAccountsCode(TestConstants.Data4.CHART_CODE_BA);
        sline.setFinancialObjectCode(TestConstants.Data4.OBJECT_CODE2);
        sline.refresh();

        assertEquals(TestConstants.Data4.CHART_CODE_BA, sline.getObjectCode().getChartOfAccounts().getChartOfAccountsCode());
        assertEquals(TestConstants.Data4.OBJECT_CODE2, sline.getObjectCode().getFinancialObjectCode());

    }


    // no obvious way to test these separately, since we need to create to test save, need to save to (really) test get, and need
    // to delete so future test-runs can recreate
    public void testLifecycle() throws Exception {
        // make sure they dont' exist
        assertFalse(accountingLineService.getByDocumentHeaderId(SourceAccountingLine.class, TestConstants.Data4.DOC_HDR_ID).size() > 0);
        assertFalse(accountingLineService.getByDocumentHeaderId(TargetAccountingLine.class, TestConstants.Data4.DOC_HDR_ID).size() > 0);
        List sourceLines = null;
        List targetLines = null;
        try {
            // save 'em
            accountingLineService.save(sline);
            accountingLineService.save(tline);

            sourceLines = accountingLineService.getByDocumentHeaderId(SourceAccountingLine.class, TestConstants.Data4.DOC_HDR_ID);
            targetLines = accountingLineService.getByDocumentHeaderId(TargetAccountingLine.class, TestConstants.Data4.DOC_HDR_ID);

            // make sure they got saved
            assertTrue(sourceLines.size() > 0);
            assertTrue(targetLines.size() > 0);
        }
        finally {
            // delete 'em
            if (sourceLines != null) {
                for (Iterator i = sourceLines.iterator(); i.hasNext();) {
                    accountingLineService.deleteAccountingLine((AccountingLineBase) i.next());
                }
            }
            if (targetLines != null) {
                for (Iterator i = targetLines.iterator(); i.hasNext();) {
                    accountingLineService.deleteAccountingLine((AccountingLineBase) i.next());
                }
            }

            // make sure they got deleted
            assertTrue(accountingLineService.getByDocumentHeaderId(SourceAccountingLine.class, TestConstants.Data4.DOC_HDR_ID).size() == 0);
            assertTrue(accountingLineService.getByDocumentHeaderId(TargetAccountingLine.class, TestConstants.Data4.DOC_HDR_ID).size() == 0);
        }
    }
}
