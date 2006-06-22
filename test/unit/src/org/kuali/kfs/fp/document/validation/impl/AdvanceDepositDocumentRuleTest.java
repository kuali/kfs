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
package org.kuali.module.financial.rules;

import static org.kuali.test.IsDebitTestUtils.Amount.NEGATIVE;
import static org.kuali.test.IsDebitTestUtils.Amount.POSITIVE;

import org.kuali.core.bo.AccountingLine;
import org.kuali.core.bo.SourceAccountingLine;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.service.DocumentTypeService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.financial.document.AdvanceDepositDocument;
import org.kuali.module.financial.rules.TransactionalDocumentRuleBase.IsDebitUtils;
import org.kuali.test.IsDebitTestUtils;
import org.kuali.test.KualiTestBaseWithSession;


/**
 * This class tests the <code>AdvanceDepositDocumentRule</code>s
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class AdvanceDepositDocumentRuleTest extends KualiTestBaseWithSession {

    private DocumentTypeService documentTypeService;

    /**
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        documentTypeService = SpringServiceLocator.getDocumentTypeService();
    }

    /**
     * tests false is returned for a positive income
     * 
     * @throws Exception
     */
    public void testIsDebit_income_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), AdvanceDepositDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(transactionalDocument, SourceAccountingLine.class, POSITIVE);
        assertFalse(IsDebitTestUtils.isDebit(documentTypeService, getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests true is returned for a negative income
     * 
     * @throws Exception
     */
    public void testIsDebit_income_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), AdvanceDepositDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(transactionalDocument, SourceAccountingLine.class, NEGATIVE);
        assertTrue(IsDebitTestUtils.isDebit(documentTypeService, getDataDictionaryService(), transactionalDocument, accountingLine));

    }

    /**
     * tests an <code>IllegalArgumentException</code> is thrown for a zero income
     * 
     * @throws Exception
     */
    public void testIsDebit_income_zeroAmount() throws Exception {
        boolean failedAsExpected = false;
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), AdvanceDepositDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(transactionalDocument, SourceAccountingLine.class, KualiDecimal.ZERO);

        try {
            IsDebitTestUtils.isDebit(documentTypeService, getDataDictionaryService(), transactionalDocument, accountingLine);

        }
        catch (IllegalStateException e) {
            failedAsExpected = IsDebitUtils.isDebitCalculationIllegalStateExceptionMessage.equals(e.getMessage());
        }

        assertTrue(failedAsExpected);
    }

    /**
     * tests true is returned for a positive expense
     * 
     * @throws Exception
     */
    public void testIsDebit_expense_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), AdvanceDepositDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(transactionalDocument, SourceAccountingLine.class, POSITIVE);
        assertTrue(IsDebitTestUtils.isDebit(documentTypeService, getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests false is returned for a negative expense
     * 
     * @throws Exception
     */
    public void testIsDebit_expense_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), AdvanceDepositDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(transactionalDocument, SourceAccountingLine.class, NEGATIVE);
        assertFalse(IsDebitTestUtils.isDebit(documentTypeService, getDataDictionaryService(), transactionalDocument, accountingLine));

    }

    /**
     * tests an <code>IllegalArgumentException</code> is thrown for a zero expense
     * 
     * @throws Exception
     */
    public void testIsDebit_expense_zeroAmount() throws Exception {
        boolean failedAsExpected = false;
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), AdvanceDepositDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(transactionalDocument, SourceAccountingLine.class, KualiDecimal.ZERO);

        try {
            IsDebitTestUtils.isDebit(documentTypeService, getDataDictionaryService(), transactionalDocument, accountingLine);

        }
        catch (IllegalStateException e) {
            failedAsExpected = IsDebitUtils.isDebitCalculationIllegalStateExceptionMessage.equals(e.getMessage());
        }

        assertTrue(failedAsExpected);
    }

    /**
     * tests true is returned for a positive asset
     * 
     * @throws Exception
     */
    public void testIsDebit_asset_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), AdvanceDepositDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(transactionalDocument, SourceAccountingLine.class, POSITIVE);
        assertTrue(IsDebitTestUtils.isDebit(documentTypeService, getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests false is returned for a negative asset
     * 
     * @throws Exception
     */
    public void testIsDebit_asset_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), AdvanceDepositDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(transactionalDocument, SourceAccountingLine.class, NEGATIVE);
        assertFalse(IsDebitTestUtils.isDebit(documentTypeService, getDataDictionaryService(), transactionalDocument, accountingLine));

    }

    /**
     * tests an <code>IllegalArgumentException</code> is thrown for a zero asset
     * 
     * @throws Exception
     */
    public void testIsDebit_asset_zeroAmount() throws Exception {
        boolean failedAsExpected = false;
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), AdvanceDepositDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(transactionalDocument, SourceAccountingLine.class, KualiDecimal.ZERO);

        try {
            IsDebitTestUtils.isDebit(documentTypeService, getDataDictionaryService(), transactionalDocument, accountingLine);

        }
        catch (IllegalStateException e) {
            failedAsExpected = IsDebitUtils.isDebitCalculationIllegalStateExceptionMessage.equals(e.getMessage());
        }

        assertTrue(failedAsExpected);
    }

    /**
     * tests false is returned for a positive liability
     * 
     * @throws Exception
     */
    public void testIsDebit_liability_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), AdvanceDepositDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(transactionalDocument, SourceAccountingLine.class, POSITIVE);
        assertFalse(IsDebitTestUtils.isDebit(documentTypeService, getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests true is returned for a negative liability
     * 
     * @throws Exception
     */
    public void testIsDebit_liability_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), AdvanceDepositDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(transactionalDocument, SourceAccountingLine.class, NEGATIVE);
        assertTrue(IsDebitTestUtils.isDebit(documentTypeService, getDataDictionaryService(), transactionalDocument, accountingLine));

    }

    /**
     * tests an <code>IllegalArgumentException</code> is thrown for a zero liability
     * 
     * @throws Exception
     */
    public void testIsDebit_liability_zeroAmount() throws Exception {
        boolean failedAsExpected = false;
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), AdvanceDepositDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(transactionalDocument, SourceAccountingLine.class, KualiDecimal.ZERO);

        try {
            IsDebitTestUtils.isDebit(documentTypeService, getDataDictionaryService(), transactionalDocument, accountingLine);

        }
        catch (IllegalStateException e) {
            failedAsExpected = IsDebitUtils.isDebitCalculationIllegalStateExceptionMessage.equals(e.getMessage());
        }

        assertTrue(failedAsExpected);
    }

}
