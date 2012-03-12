/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.fp.document.validation.impl;

import static org.kuali.kfs.sys.fixture.UserNameFixture.mhkozlow;
import static org.kuali.kfs.sys.service.IsDebitTestUtils.Amount.NEGATIVE;
import static org.kuali.kfs.sys.service.IsDebitTestUtils.Amount.POSITIVE;

import org.kuali.kfs.fp.document.CashReceiptDocument;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.service.IsDebitTestUtils;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.service.DocumentService;


/**
 * This class tests the <code>CashReciptDocumentRule</code>s
 */
@ConfigureContext(session = mhkozlow)
public class CashReceiptDocumentRuleTest extends KualiTestBase {

    /**
     * test that an <code>IllegalStateException</code> gets thrown for an error correction document
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getErrorCorrectionDocument(SpringContext.getBean(DocumentService.class), CashReceiptDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(accountingDocument, SourceAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isErrorCorrectionIllegalStateException(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    /**
     * tests false is returned for a positive income
     * 
     * @throws Exception
     */
    public void testIsDebit_income_positveAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), CashReceiptDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(accountingDocument, SourceAccountingLine.class, POSITIVE);

        assertFalse(IsDebitTestUtils.isDebit(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    /**
     * tests true is returned for a negative income
     * 
     * @throws Exception
     */
    public void testIsDebit_income_negativeAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), CashReceiptDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(accountingDocument, SourceAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebit(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));

    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a zero income
     * 
     * @throws Exception
     */
    public void testIsDebit_income_zeroAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), CashReceiptDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(accountingDocument, SourceAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    /**
     * tests true is returned for a positive expense
     * 
     * @throws Exception
     */
    public void testIsDebit_expense_positveAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), CashReceiptDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(accountingDocument, SourceAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebit(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    /**
     * tests false is returned for a negative expense
     * 
     * @throws Exception
     */
    public void testIsDebit_expense_negativeAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), CashReceiptDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(accountingDocument, SourceAccountingLine.class, NEGATIVE);

        assertFalse(IsDebitTestUtils.isDebit(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));

    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a zero expense
     * 
     * @throws Exception
     */
    public void testIsDebit_expense_zeroAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), CashReceiptDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(accountingDocument, SourceAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    /**
     * tests true is returned for a positive asset
     * 
     * @throws Exception
     */
    public void testIsDebit_asset_positveAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), CashReceiptDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(accountingDocument, SourceAccountingLine.class, POSITIVE);
        assertTrue(IsDebitTestUtils.isDebit(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    /**
     * tests false is returned for a negative asset
     * 
     * @throws Exception
     */
    public void testIsDebit_asset_negativeAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), CashReceiptDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(accountingDocument, SourceAccountingLine.class, NEGATIVE);
        assertFalse(IsDebitTestUtils.isDebit(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));

    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a zero asset
     * 
     * @throws Exception
     */
    public void testIsDebit_asset_zeroAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), CashReceiptDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(accountingDocument, SourceAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    /**
     * tests false is returned for aity
     * 
     * @throws Exception
     */
    public void testIsDebit_liability_positveAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), CashReceiptDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(accountingDocument, SourceAccountingLine.class, POSITIVE);
        assertFalse(IsDebitTestUtils.isDebit(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    /**
     * tests true is returned for a negative liability
     * 
     * @throws Exception
     */
    public void testIsDebit_liability_negativeAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), CashReceiptDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(accountingDocument, SourceAccountingLine.class, NEGATIVE);
        assertTrue(IsDebitTestUtils.isDebit(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));

    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a zero liability
     * 
     * @throws Exception
     */
    public void testIsDebit_liability_zeroAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), CashReceiptDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(accountingDocument, SourceAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

}

