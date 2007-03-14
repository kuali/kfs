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
package org.kuali.core.service;

import static org.kuali.kfs.util.SpringServiceLocator.getDocumentService;
import static org.kuali.kfs.util.SpringServiceLocator.getAccountingLineService;
import static org.kuali.test.fixtures.AccountingLineFixture.LINE2_TOF;
import static org.kuali.test.fixtures.UserNameFixture.KHUNTLEY;

import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.AccountingLineBase;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.bo.TargetAccountingLine;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.module.financial.document.TransferOfFundsDocument;
import org.kuali.test.DocumentTestUtils;
import org.kuali.test.KualiTestBase;
import org.kuali.test.TestsWorkflowViaDatabase;
import org.kuali.test.WithTestSpringContext;

/**
 * This class tests the AccountingLine service.
 * 
 * 
 */
@WithTestSpringContext(session = KHUNTLEY)
public class AccountingLineServiceTest extends KualiTestBase {

    private SourceAccountingLine sline;
    private TargetAccountingLine tline;
    private AccountingDocument document;

    @TestsWorkflowViaDatabase
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        document = DocumentTestUtils.createDocument(getDocumentService(), TransferOfFundsDocument.class);
        getDocumentService().saveDocument(document);
        LINE2_TOF.addAsSourceTo(document);
        LINE2_TOF.addAsTargetTo(document);

        sline = document.getSourceAccountingLine(0);
        tline = document.getTargetAccountingLine(0);
    }

    /**
     * Tests an accounting line is correctly persisted when the primitives of the line are set.
     * 
     * @throws Exception
     */
    public void testPersistence() throws Exception {


        getAccountingLineService().save(sline);

        List<? extends SourceAccountingLine> sourceLines = getAccountingLineService().getByDocumentHeaderId(SourceAccountingLine.class, document.getDocumentNumber());
        assertTrue(sourceLines.size() > 0);

        AccountingLine line = sourceLines.get(0);

        assertEquals(LINE2_TOF.chartOfAccountsCode, line.getChartOfAccountsCode());
        assertEquals(LINE2_TOF.accountNumber, line.getAccountNumber());
        assertEquals(LINE2_TOF.subAccountNumber, line.getSubAccountNumber());
        assertEquals(LINE2_TOF.financialObjectCode, line.getFinancialObjectCode());
        assertEquals(LINE2_TOF.financialSubObjectCode, line.getFinancialSubObjectCode());

        getAccountingLineService().deleteAccountingLine((AccountingLineBase) line);

    }


    /**
     * Tests reference objects are being corrected refreshed from changed pritive values.
     */
    public void testRefresh() {
        assertEquals(LINE2_TOF.chartOfAccountsCode, sline.getAccount().getChartOfAccountsCode());
        assertEquals(LINE2_TOF.accountNumber, sline.getAccount().getAccountNumber());

        sline.setAccountNumber(TestConstants.Data4.ACCOUNT2);
        sline.refresh();

        assertEquals(LINE2_TOF.chartOfAccountsCode, sline.getAccount().getChartOfAccountsCode());
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
        String docNumber = document.getDocumentNumber();
        // make sure they dont' exist
        assertEquals(0, getAccountingLineService().getByDocumentHeaderId(SourceAccountingLine.class, docNumber).size());
        assertEquals(0, getAccountingLineService().getByDocumentHeaderId(TargetAccountingLine.class, docNumber).size());
        List sourceLines = null;
        List targetLines = null;

        // save 'em
        getAccountingLineService().save(sline);
        getAccountingLineService().save(tline);

        sourceLines = getAccountingLineService().getByDocumentHeaderId(SourceAccountingLine.class, docNumber);
        targetLines = getAccountingLineService().getByDocumentHeaderId(TargetAccountingLine.class, docNumber);

        // make sure they got saved
        assertTrue(sourceLines.size() > 0);
        assertTrue(targetLines.size() > 0);
        // delete 'em
        if (sourceLines != null) {
            for (Iterator i = sourceLines.iterator(); i.hasNext();) {
                getAccountingLineService().deleteAccountingLine((AccountingLineBase) i.next());
            }
        }
        if (targetLines != null) {
            for (Iterator i = targetLines.iterator(); i.hasNext();) {
                getAccountingLineService().deleteAccountingLine((AccountingLineBase) i.next());
            }
        }

        // make sure they got deleted
        assertEquals(0, getAccountingLineService().getByDocumentHeaderId(SourceAccountingLine.class, docNumber).size());
        assertEquals(0, getAccountingLineService().getByDocumentHeaderId(TargetAccountingLine.class, docNumber).size());
    }
}
