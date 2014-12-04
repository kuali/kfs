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
package org.kuali.kfs.sys.service;

import static org.kuali.kfs.sys.fixture.AccountingLineFixture.LINE2_TOF;
import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.fp.document.TransferOfFundsDocument;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.DocumentTestUtils;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.businessobject.TargetAccountingLine;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;

/**
 * This class tests the AccountingLine service.
 */
@ConfigureContext(session = khuntley, shouldCommitTransactions = true)
public class AccountingLineServiceTest extends KualiTestBase {

    private SourceAccountingLine sline;
    private TargetAccountingLine tline;
    private AccountingDocument document;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        document = DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), TransferOfFundsDocument.class);
        SpringContext.getBean(DocumentService.class).saveDocument(document);
        LINE2_TOF.addAsSourceTo(document);
        LINE2_TOF.addAsTargetTo(document);

        sline = (SourceAccountingLine) document.getSourceAccountingLine(0);
        tline = (TargetAccountingLine) document.getTargetAccountingLine(0);
    }

    /**
     * Tests an accounting line is correctly persisted when the primitives of the line are set.
     * 
     * @throws Exception
     */
    public void testPersistence() throws Exception {


        SpringContext.getBean(BusinessObjectService.class).save(sline);

        List<? extends SourceAccountingLine> sourceLines = SpringContext.getBean(AccountingLineService.class).getByDocumentHeaderId(SourceAccountingLine.class, document.getDocumentNumber());
        assertTrue(sourceLines.size() > 0);

        AccountingLine line = sourceLines.get(0);

        assertEquals(LINE2_TOF.chartOfAccountsCode, line.getChartOfAccountsCode());
        assertEquals(LINE2_TOF.accountNumber, line.getAccountNumber());
        assertEquals(LINE2_TOF.subAccountNumber, line.getSubAccountNumber());
        assertEquals(LINE2_TOF.financialObjectCode, line.getFinancialObjectCode());
        assertEquals(LINE2_TOF.financialSubObjectCode, line.getFinancialSubObjectCode());

        SpringContext.getBean(BusinessObjectService.class).delete((AccountingLine) line);

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
        assertEquals(0, SpringContext.getBean(AccountingLineService.class).getByDocumentHeaderId(SourceAccountingLine.class, docNumber).size());
        assertEquals(0, SpringContext.getBean(AccountingLineService.class).getByDocumentHeaderId(TargetAccountingLine.class, docNumber).size());
        List sourceLines = null;
        List targetLines = null;

        // save 'em
        SpringContext.getBean(BusinessObjectService.class).save(sline);
        SpringContext.getBean(BusinessObjectService.class).save(tline);

        sourceLines = SpringContext.getBean(AccountingLineService.class).getByDocumentHeaderId(SourceAccountingLine.class, docNumber);
        targetLines = SpringContext.getBean(AccountingLineService.class).getByDocumentHeaderId(TargetAccountingLine.class, docNumber);

        // make sure they got saved
        assertTrue(sourceLines.size() > 0);
        assertTrue(targetLines.size() > 0);
        // delete 'em
        if (sourceLines != null) {
            for (Iterator i = sourceLines.iterator(); i.hasNext();) {
                SpringContext.getBean(BusinessObjectService.class).delete((AccountingLine) i.next());
            }
        }
        if (targetLines != null) {
            for (Iterator i = targetLines.iterator(); i.hasNext();) {
                SpringContext.getBean(BusinessObjectService.class).delete((AccountingLine) i.next());
            }
        }

        // make sure they got deleted
        assertEquals(0, SpringContext.getBean(AccountingLineService.class).getByDocumentHeaderId(SourceAccountingLine.class, docNumber).size());
        assertEquals(0, SpringContext.getBean(AccountingLineService.class).getByDocumentHeaderId(TargetAccountingLine.class, docNumber).size());
    }
}

