/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/test/unit/src/org/kuali/kfs/fp/document/validation/impl/ServiceBillingDocumentRuleTest.java,v $
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
package org.kuali.module.financial.rules;

import org.kuali.core.bo.AccountingLine;
import org.kuali.core.bo.SourceAccountingLine;
import org.kuali.core.bo.TargetAccountingLine;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.SpringServiceLocator;
import static org.kuali.core.util.SpringServiceLocator.getDataDictionaryService;
import static org.kuali.core.util.SpringServiceLocator.getDocumentService;
import static org.kuali.core.util.SpringServiceLocator.getDocumentTypeService;
import org.kuali.module.financial.document.ServiceBillingDocument;
import static org.kuali.module.financial.rules.IsDebitTestUtils.Amount.NEGATIVE;
import static org.kuali.module.financial.rules.IsDebitTestUtils.Amount.POSITIVE;
import org.kuali.test.WithTestSpringContext;
import org.kuali.test.KualiTestBase;
import static org.kuali.test.fixtures.UserNameFixture.DFOGLE;

/**
 * 
 * This class tests the business rules of the service billing document. This is not implemented yet and needs to extend
 * TransactionalDocumentRuleTestBase.
 * 
 * 
 */
@WithTestSpringContext(session = DFOGLE)
public class ServiceBillingDocumentRuleTest extends KualiTestBase {


    // ////////////////////////////////////////////////////////////////////////
    // Test methods start here //
    // ////////////////////////////////////////////////////////////////////////

    public final void testSave_nullDocument() throws Exception {
        boolean failedAsExpected = false;

        try {
            SpringServiceLocator.getDocumentService().saveDocument(null, null, null);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }

        assertTrue(failedAsExpected);
    }

    /**
     * tests false is returned for a positive income
     * 
     * @throws Exception
     */
    public void testIsDebit_source_income_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), ServiceBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(transactionalDocument, SourceAccountingLine.class, POSITIVE);

        assertFalse(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests true is returned for a negative income
     * 
     * @throws Exception
     */
    public void testIsDebit_source_income_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), ServiceBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(transactionalDocument, SourceAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a zero income
     * 
     * @throws Exception
     */
    public void testIsDebit_source_income_zeroAmount() throws Exception {

        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), ServiceBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(transactionalDocument, SourceAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests false is returned for a positive expense
     * 
     * @throws Exception
     */
    public void testIsDebit_source_expense_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), ServiceBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(transactionalDocument, SourceAccountingLine.class, POSITIVE);
        assertFalse(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a zero expense
     * 
     * @throws Exception
     */
    public void testIsDebit_source_expense_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), ServiceBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(transactionalDocument, SourceAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a zero expense
     * 
     * @throws Exception
     */
    public void testIsDebit_source_expense_zeroAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), ServiceBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(transactionalDocument, SourceAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a positive asset
     * 
     * @throws Exception
     */
    public void testIsDebit_source_asset_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), ServiceBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(transactionalDocument, SourceAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a negative asset
     * 
     * @throws Exception
     */
    public void testIsDebit_source_asset_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), ServiceBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(transactionalDocument, SourceAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a zero asset
     * 
     * @throws Exception
     */
    public void testIsDebit_source_asset_zeroAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), ServiceBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(transactionalDocument, SourceAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a positive liability
     * 
     * @throws Exception
     */
    public void testIsDebit_source_liability_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), ServiceBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(transactionalDocument, SourceAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a negative liability
     * 
     * @throws Exception
     */
    public void testIsDebit_source_liability_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), ServiceBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(transactionalDocument, SourceAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a zero liability
     * 
     * @throws Exception
     */
    public void testIsDebit_source_liability_zeroAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), ServiceBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(transactionalDocument, SourceAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests true is returned for a positive income
     * 
     * @throws Exception
     */
    public void testIsDebit_target_income_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), ServiceBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(transactionalDocument, TargetAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a negative income
     * 
     * @throws Exception
     */
    public void testIsDebit_target_income_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), ServiceBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(transactionalDocument, TargetAccountingLine.class, NEGATIVE);

        assertFalse(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a zero income
     * 
     * @throws Exception
     */
    public void testIsDebit_target_income_zeroAmount() throws Exception {

        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), ServiceBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(transactionalDocument, TargetAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests true is returned for a positive expense
     * 
     * @throws Exception
     */
    public void testIsDebit_target_expense_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), ServiceBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(transactionalDocument, TargetAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests false is returned for a zero expense
     * 
     * @throws Exception
     */
    public void testIsDebit_target_expense_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), ServiceBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(transactionalDocument, TargetAccountingLine.class, NEGATIVE);

        assertFalse(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a zero expense
     * 
     * @throws Exception
     */
    public void testIsDebit_target_expense_zeroAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), ServiceBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(transactionalDocument, TargetAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a positive asset
     * 
     * @throws Exception
     */
    public void testIsDebit_target_asset_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), ServiceBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(transactionalDocument, TargetAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a negative asset
     * 
     * @throws Exception
     */
    public void testIsDebit_target_asset_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), ServiceBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(transactionalDocument, TargetAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a zero asset
     * 
     * @throws Exception
     */
    public void testIsDebit_target_asset_zeroAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), ServiceBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(transactionalDocument, TargetAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a positive liability
     * 
     * @throws Exception
     */
    public void testIsDebit_target_liability_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), ServiceBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(transactionalDocument, TargetAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a negative liability
     * 
     * @throws Exception
     */
    public void testIsDebit_target_liability_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), ServiceBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(transactionalDocument, TargetAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a zero liability
     * 
     * @throws Exception
     */
    public void testIsDebit_target_liability_zeroAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), ServiceBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(transactionalDocument, TargetAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests flase is returned for a positive income
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_source_income_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), ServiceBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(transactionalDocument, SourceAccountingLine.class, POSITIVE);

        assertFalse(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests true is returned for a negative income
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_source_income_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), ServiceBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(transactionalDocument, SourceAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a zero income
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_source_income_zeroAmount() throws Exception {

        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), ServiceBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(transactionalDocument, SourceAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests false is returned for a positive expense
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_source_expense_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), ServiceBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(transactionalDocument, SourceAccountingLine.class, POSITIVE);

        assertFalse(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests true is returned for a zero expense
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_source_expense_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), ServiceBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(transactionalDocument, SourceAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a zero expense
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_source_expense_zeroAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), ServiceBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(transactionalDocument, SourceAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a positive asset
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_source_asset_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), ServiceBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(transactionalDocument, SourceAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a negative asset
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_source_asset_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), ServiceBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(transactionalDocument, SourceAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a zero asset
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_source_asset_zeroAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), ServiceBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(transactionalDocument, SourceAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a positive liability
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_source_liability_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), ServiceBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(transactionalDocument, SourceAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a negative liability
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_source_liability_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), ServiceBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(transactionalDocument, SourceAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a zero liability
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_source_liability_zeroAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), ServiceBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(transactionalDocument, SourceAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests true is returned for a positive income
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_target_income_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), ServiceBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(transactionalDocument, TargetAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests false is returned for a negative income
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_target_income_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), ServiceBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(transactionalDocument, TargetAccountingLine.class, NEGATIVE);

        assertFalse(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));

    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a zero income
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_target_income_zeroAmount() throws Exception {

        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), ServiceBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(transactionalDocument, TargetAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests true is returned for a positive expense
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_target_expense_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), ServiceBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(transactionalDocument, TargetAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests false is returned for a zero expense
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_target_expense_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), ServiceBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(transactionalDocument, TargetAccountingLine.class, NEGATIVE);

        assertFalse(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a zero expense
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_target_expense_zeroAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), ServiceBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(transactionalDocument, TargetAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a positive asset
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_target_asset_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), ServiceBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(transactionalDocument, TargetAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a negative asset
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_target_asset_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), ServiceBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(transactionalDocument, TargetAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a zero asset
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_target_asset_zeroAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), ServiceBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(transactionalDocument, TargetAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a positive liability
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_target_liability_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), ServiceBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(transactionalDocument, TargetAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a negative liability
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_target_liability_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), ServiceBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(transactionalDocument, TargetAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a zero liability
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_target_liability_zeroAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), ServiceBillingDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(transactionalDocument, TargetAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }


    /*
     * protected TransactionalDocument createDocumentWithInvalidObjectSubType() throws Exception { // TODO Auto-generated method
     * stub return null; }
     * 
     * protected TransactionalDocument createDocumentWithValidObjectSubType() throws Exception { // TODO Auto-generated method stub
     * return null; }
     * 
     * protected TransactionalDocument createDocument5() throws Exception { // TODO Auto-generated method stub return null; }
     * 
     * protected TransactionalDocument createDocumentUnbalanced() throws Exception { // TODO Auto-generated method stub return null; }
     * 
     * protected List getValidObjectSubTypeSourceLines() throws Exception { // TODO Auto-generated method stub return null; }
     * 
     * protected List getInvalidObjectSubTypeSourceLines() throws Exception { // TODO Auto-generated method stub return null; }
     * 
     * protected TargetAccountingLine getValidObjectSubTypeTargetLine() throws Exception { // TODO Auto-generated method stub return
     * null; }
     * 
     * protected TargetAccountingLine getInvalidObjectSubTypeTargetLine() throws Exception { // TODO Auto-generated method stub
     * return null; }
     * 
     * protected SourceAccountingLine getValidObjectTypeSourceLine() throws Exception { // TODO Auto-generated method stub return
     * null; }
     * 
     * protected SourceAccountingLine getInvalidObjectTypeSourceLine() throws Exception { // TODO Auto-generated method stub return
     * null; }
     * 
     * protected SourceAccountingLine getValidObjectCodeSourceLine() throws Exception { // TODO Auto-generated method stub return
     * null; }
     * 
     * protected SourceAccountingLine getInvalidObjectCodeSourceLine() throws Exception { // TODO Auto-generated method stub return
     * null; }
     * 
     * protected List getValidObjectSubTypeTargetLines() throws Exception { // TODO Auto-generated method stub return null; }
     * 
     * protected List getInvalidObjectSubTypeTargetLines() throws Exception { // TODO Auto-generated method stub return null; }
     * 
     * public Integer getCount0() { // TODO Auto-generated method stub return null; }
     * 
     * public Integer getCount1() { // TODO Auto-generated method stub return null; }
     * 
     * public Integer getCount2() { // TODO Auto-generated method stub return null; }
     * 
     * public GeneralLedgerPendingEntry getExpectedExplicitSourcePendingEntry() { // TODO Auto-generated method stub return null; }
     * 
     * public GeneralLedgerPendingEntry getExpectedExplicitTargetPendingEntry() { // TODO Auto-generated method stub return null; }
     * 
     * public GeneralLedgerPendingEntry getExpectedOffsetSourcePendingEntry() { // TODO Auto-generated method stub return null; }
     * 
     * public GeneralLedgerPendingEntry getExpectedOffsetTargetPendingEntry() { // TODO Auto-generated method stub return null; }
     * 
     * public SourceAccountingLine getAssetSourceLine() throws Exception { // TODO Auto-generated method stub return null; }
     * 
     * public TargetAccountingLine getAssetTargetLine() throws Exception { // TODO Auto-generated method stub return null; }
     * 
     * protected Document createDocument() throws Exception { // TODO Auto-generated method stub return null; }
     * 
     * protected Document createDocumentValidForRouting() throws Exception { // TODO Auto-generated method stub return null; }
     * 
     * protected Document createDocumentInvalidForSave() throws Exception { // TODO Auto-generated method stub return null; }
     * 
     * protected Document createDocumentInvalidDescription() throws Exception { // TODO Auto-generated method stub return null; }
     * 
     * protected String getDocumentTypeName() { // TODO Auto-generated method stub return "KualiServiceBillingDocument"; }
     */

    /*
     * Commented out until we get data in the db that matches what we need, or we can modify the objects directly (mock-testing)
     * 
     * public final void testApplyAddAccountingLineBusinessRulesInvalidStudentFeeContinueEduc() throws Exception {
     * triggerInvalidAddAccountingLineEvents ( getTransactionalDocument().getSourceAccountingLines() );
     * triggerInvalidAddAccountingLineEvents ( getTransactionalDocument().getTargetAccountingLines() ); }
     * 
     * public final void testApplyAddAccountingLineBusinessRulesValidStudentFeeContinueEduc() throws Exception {
     * triggerValidAddAccountingLineEvents ( getTransactionalDocument().getSourceAccountingLines() );
     * triggerInvalidAddAccountingLineEvents ( getTransactionalDocument().getTargetAccountingLines() ); }
     * 
     * public final void testApplyAddAccountingLineBusinessRulesInvalidCapObjCodes() throws Exception {
     * triggerValidAddAccountingLineEvents ( getTransactionalDocument().getSourceAccountingLines() );
     * triggerInvalidAddAccountingLineEvents ( getTransactionalDocument().getTargetAccountingLines() ); }
     * 
     * public final void testApplyAddAccountingLineBusinessRulesValidCapObjCodes() throws Exception {
     * triggerValidAddAccountingLineEvents( getTransactionalDocument().getSourceAccountingLines() );
     * triggerInvalidAddAccountingLineEvents( getTransactionalDocument().getTargetAccountingLines() ); }
     * 
     * public final void testApplyAddAccountingLineBusinessRulesInvalidObjCodeSubTypes() throws Exception {
     * triggerValidAddAccountingLineEvents( getTransactionalDocument().getSourceAccountingLines() );
     * triggerInvalidAddAccountingLineEvents( getTransactionalDocument().getTargetAccountingLines() ); }
     * 
     * public final void testApplyAddAccountingLineBusinessRulesValidObjCodeSubTypes() throws Exception {
     * triggerValidAddAccountingLineEvents( getTransactionalDocument().getSourceAccountingLines() );
     * triggerInvalidAddAccountingLineEvents( getTransactionalDocument().getTargetAccountingLines() ); }
     * 
     * public final void testApplyAddAccountingLineBusinessRulesInvalidObjLevelCode() throws Exception {
     * triggerValidAddAccountingLineEvents( getTransactionalDocument().getSourceAccountingLines() );
     * triggerInvalidAddAccountingLineEvents( getTransactionalDocument().getTargetAccountingLines() ); }
     * 
     * public final void testApplyAddAccountingLineBusinessRulesValidObjLevelCode() throws Exception {
     * triggerValidAddAccountingLineEvents( getTransactionalDocument().getSourceAccountingLines() );
     * triggerInvalidAddAccountingLineEvents( getTransactionalDocument().getTargetAccountingLines() ); }
     * 
     * public final void testApplyAddAccountingLineBusinessRulesInvalidObjType() throws Exception {
     * triggerValidAddAccountingLineEvents( getTransactionalDocument().getSourceAccountingLines() );
     * triggerInvalidAddAccountingLineEvents( getTransactionalDocument().getTargetAccountingLines() ); }
     * 
     * public final void testApplyAddAccountingLineBusinessRulesValidObjType() throws Exception {
     * triggerValidAddAccountingLineEvents( getTransactionalDocument().getSourceAccountingLines() );
     * triggerInvalidAddAccountingLineEvents( getTransactionalDocument().getTargetAccountingLines() ); }
     * 
     * public final void testApplyAddAccountingLineBusinessRulesInvalidObjSubTypeAssessOrTransOfFunds() throws Exception {
     * triggerValidAddAccountingLineEvents( getTransactionalDocument().getSourceAccountingLines() );
     * triggerInvalidAddAccountingLineEvents( getTransactionalDocument().getTargetAccountingLines() ); }
     * 
     * public final void testApplyAddAccountingLineBusinessRulesValidObjSubTypeAssessOrTransOfFunds() throws Exception {
     * triggerValidAddAccountingLineEvents( getTransactionalDocument().getSourceAccountingLines() );
     * triggerInvalidAddAccountingLineEvents( getTransactionalDocument().getTargetAccountingLines() ); }
     * 
     * public final void testApplyAddAccountingLineBusinessRulesInvalidFundGroup() throws Exception {
     * triggerValidAddAccountingLineEvents( getTransactionalDocument().getSourceAccountingLines() );
     * triggerInvalidAddAccountingLineEvents( getTransactionalDocument().getTargetAccountingLines() ); }
     * 
     * public final void testApplyAddAccountingLineBusinessRulesValidFundGroup() throws Exception {
     * triggerValidAddAccountingLineEvents( getTransactionalDocument().getSourceAccountingLines() );
     * triggerInvalidAddAccountingLineEvents( getTransactionalDocument().getTargetAccountingLines() ); }
     * 
     * public final void testApplyAddAccountingLineBusinessRulesInvalidSubFundGroup() throws Exception {
     * triggerValidAddAccountingLineEvents( getTransactionalDocument().getSourceAccountingLines() );
     * triggerInvalidAddAccountingLineEvents( getTransactionalDocument().getTargetAccountingLines() ); }
     * 
     * public final void testApplyAddAccountingLineBusinessRulesValidSubFundGroup() throws Exception {
     * triggerValidAddAccountingLineEvents( getTransactionalDocument().getSourceAccountingLines() );
     * triggerInvalidAddAccountingLineEvents( getTransactionalDocument().getTargetAccountingLines() ); }
     */

    /*
     * This following block of commented out code contains valuable data that should be used for this rule class when we get to this
     * document. This data needs to be transformed into the new xml based fixtures framework.
     * 
     * public void fixturesDefault() {
     * 
     * addFixture( KualiRuleTestCase.ACCOUNT, "1912610" ); addFixture( KualiRuleTestCase.BALANCE_TYPE,
     * TransactionalDocumentRuleBase.BALANCE_TYPE_CODE.ACTUAL ); addFixture( KualiRuleTestCase.CHART, "UA" ); addFixture(
     * KualiRuleTestCase.OBJECT_TYPE_CODE, TransactionalDocumentRuleBase.OBJECT_TYPE_CODE.CASH_NOT_INCOME ); addFixture(
     * KualiRuleTestCase.POSTING_YEAR, new Integer( 2005 ) ); addFixture( KualiRuleTestCase.PROJECT, "BOB" ); addFixture(
     * KualiRuleTestCase.SUBACCOUNT, "BEER" ); }
     * 
     * public void fixturesApplyAddAccountingLineBusinessRulesInvalidStudentFeeContinueEduc() throws Exception { List sourceLines =
     * new ArrayList(); List targetLines = new ArrayList();
     * 
     * addFixture( KualiRuleTestCase.DOCUMENT_DESCRIPTION, "test" ); addFixture( KualiRuleTestCase.EXPLANATION, "This is a test
     * document, testing invalid object sub-type (Student Fees) " + "with invalid sub-fund group (DCEDU) " + "accounting line
     * business rules." ); AccountingLine sourceLine1 = fixtureSourceAccountingLine( "0967", "1000" );
     * sourceLine1.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.STUDENT_FEES);
     * //sourceLine1.getAccount().setSubFundGroupCode("FAIL");
     * 
     * sourceLines.add( sourceLine1 ); addFixture( KualiRuleTestCase.SOURCE_ACCOUNTING_LINES, sourceLines );
     * 
     * AccountingLine targetLine1 = fixtureSourceAccountingLine( "0967", "1000" );
     * targetLine1.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.STUDENT_FEES);
     * //sourceLine1.getAccount().setSubFundGroupCode("FAIL");
     * 
     * targetLines.add( sourceLine1 ); addFixture( KualiRuleTestCase.TARGET_ACCOUNTING_LINES, targetLines ); }
     * 
     * public void fixturesApplyAddAccountingLineBusinessRulesValidStudentFeeContinueEduc() throws Exception { List sourceLines =
     * new ArrayList(); List targetLines = new ArrayList();
     * 
     * addFixture( KualiRuleTestCase.DOCUMENT_DESCRIPTION, "test" ); addFixture( KualiRuleTestCase.EXPLANATION, "This is a test
     * document, testing invalid object sub-type (Student Fees) " + "with invalid sub-fund group (DCEDU) " + "accounting line
     * business rules." ); AccountingLine sourceLine1 = fixtureSourceAccountingLine( "0967", "1000" );
     * sourceLine1.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.STUDENT_FEES); SubFundGroup sfg = new
     * SubFundGroup(); sfg.setSubFundGroupCode(SUB_FUND_GROUP_CODE.CONTINUE_EDUC); sourceLine1.getAccount().setSubFundGroup(sfg);
     * 
     * sourceLines.add( sourceLine1 ); addFixture( KualiRuleTestCase.SOURCE_ACCOUNTING_LINES, sourceLines );
     * 
     * AccountingLine targetLine1 = fixtureSourceAccountingLine( "0967", "1000" );
     * targetLine1.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.STUDENT_FEES);
     * //sourceLine1.getAccount().setSubFundGroupCode("FAIL");
     * 
     * targetLines.add( sourceLine1 ); addFixture( KualiRuleTestCase.TARGET_ACCOUNTING_LINES, targetLines ); }
     * 
     * public void fixturesApplyAddAccountingLineBusinessRulesInvalidCapObjCodes() throws Exception { List sourceLines = new
     * ArrayList(); List targetLines = new ArrayList();
     * 
     * addFixture( KualiRuleTestCase.DOCUMENT_DESCRIPTION, "test" ); addFixture( KualiRuleTestCase.EXPLANATION, "This is a test
     * document, testing invalid object sub-type (Student Fees) " + "with invalid sub-fund group (DCEDU) " + "accounting line
     * business rules." ); AccountingLine sourceLine1 = fixtureSourceAccountingLine( "1696", "1000" ); AccountingLine sourceLine2 =
     * fixtureSourceAccountingLine( "1696", "1000" ); AccountingLine sourceLine3 = fixtureSourceAccountingLine( "1696", "1000" );
     * AccountingLine sourceLine4 = fixtureSourceAccountingLine( "1696", "1000" ); AccountingLine sourceLine5 =
     * fixtureSourceAccountingLine( "1696", "1000" ); AccountingLine sourceLine6 = fixtureSourceAccountingLine( "1696", "1000" );
     * AccountingLine sourceLine7 = fixtureSourceAccountingLine( "1696", "1000" );
     * 
     * sourceLine1.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.CAP_MOVE_EQUIP);
     * sourceLine2.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.CAP_MOVE_EQUIP_FED_FUND);
     * sourceLine3.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.CAP_MOVE_EQUIP_OTHER_OWN);
     * sourceLine4.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.CONSTRUCTION_IN_PROG);
     * sourceLine5.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.UNIV_CONSTRUCTED);
     * sourceLine6.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.UNIV_CONSTRUCTED_FED_FUND);
     * sourceLine7.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.UNIV_CONSTRUCTED_FED_OWN);
     * 
     * 
     * sourceLines.add( sourceLine1 ); sourceLines.add(sourceLine2); sourceLines.add(sourceLine3); sourceLines.add(sourceLine4);
     * sourceLines.add(sourceLine5); sourceLines.add(sourceLine6); sourceLines.add(sourceLine7); addFixture(
     * KualiRuleTestCase.SOURCE_ACCOUNTING_LINES, sourceLines );
     * 
     * AccountingLine targetLine1 = fixtureTargetAccountingLine( "1696", "1000" ); AccountingLine targetLine2 =
     * fixtureTargetAccountingLine( "1696", "1000" ); AccountingLine targetLine3 = fixtureTargetAccountingLine( "1696", "1000" );
     * AccountingLine targetLine4 = fixtureTargetAccountingLine( "1696", "1000" ); AccountingLine targetLine5 =
     * fixtureTargetAccountingLine( "1696", "1000" ); AccountingLine targetLine6 = fixtureTargetAccountingLine( "1696", "1000" );
     * AccountingLine targetLine7 = fixtureTargetAccountingLine( "1696", "1000" );
     * 
     * targetLine1.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.CAP_MOVE_EQUIP);
     * targetLine2.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.CAP_MOVE_EQUIP_FED_FUND);
     * targetLine3.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.CAP_MOVE_EQUIP_OTHER_OWN);
     * targetLine4.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.CONSTRUCTION_IN_PROG);
     * targetLine5.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.UNIV_CONSTRUCTED);
     * targetLine6.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.UNIV_CONSTRUCTED_FED_FUND);
     * targetLine7.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.UNIV_CONSTRUCTED_FED_OWN);
     * 
     * targetLines.add( targetLine1 ); targetLines.add(targetLine2); targetLines.add(targetLine3); targetLines.add(targetLine4);
     * targetLines.add(targetLine5); targetLines.add(targetLine6); targetLines.add(targetLine7); addFixture(
     * KualiRuleTestCase.TARGET_ACCOUNTING_LINES, targetLines ); }
     * 
     * public void fixturesApplyAddAccountingLineBusinessRulesValidCapObjCodes() throws Exception { }
     * 
     * public void fixturesApplyAddAccountingLineBusinessRulesInvalidObjCodeSubTypes() throws Exception { List sourceLines = new
     * ArrayList(); List targetLines = new ArrayList();
     * 
     * addFixture( KualiRuleTestCase.DOCUMENT_DESCRIPTION, "test" ); addFixture( KualiRuleTestCase.EXPLANATION, "This is a test
     * document, testing invalid object sub-type (Student Fees) " + "with invalid sub-fund group (DCEDU) " + "accounting line
     * business rules." ); AccountingLine sourceLine1 = fixtureSourceAccountingLine( "1696", "1000" ); AccountingLine sourceLine2 =
     * fixtureSourceAccountingLine( "1696", "1000" ); AccountingLine sourceLine3 = fixtureSourceAccountingLine( "1696", "1000" );
     * AccountingLine sourceLine4 = fixtureSourceAccountingLine( "1696", "1000" ); AccountingLine sourceLine5 =
     * fixtureSourceAccountingLine( "1696", "1000" ); AccountingLine sourceLine6 = fixtureSourceAccountingLine( "1696", "1000" );
     * AccountingLine sourceLine7 = fixtureSourceAccountingLine( "1696", "1000" ); AccountingLine sourceLine8 =
     * fixtureSourceAccountingLine( "1696", "1000" ); AccountingLine sourceLine9 = fixtureSourceAccountingLine( "1696", "1000" );
     * AccountingLine sourceLine10 = fixtureSourceAccountingLine( "1696", "1000" );
     * 
     * sourceLine1.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.BUDGET_ONLY);
     * sourceLine2.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.CONSTRUCTION_IN_PROG);
     * sourceLine3.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.HOURLY_WAGES);
     * sourceLine4.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.MANDATORY_TRANSFER);
     * sourceLine5.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.RESERVES);
     * sourceLine6.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.WRITE_OFF);
     * sourceLine7.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.SALARIES_WAGES);
     * sourceLine8.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.STATE_APP);
     * sourceLine9.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.SALARIES);
     * sourceLine10.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.INVEST);
     * 
     * 
     * sourceLines.add( sourceLine1 ); sourceLines.add(sourceLine2); sourceLines.add(sourceLine3); sourceLines.add(sourceLine4);
     * sourceLines.add(sourceLine5); sourceLines.add(sourceLine6); sourceLines.add(sourceLine7); sourceLines.add(sourceLine8);
     * sourceLines.add(sourceLine9); sourceLines.add(sourceLine10); addFixture( KualiRuleTestCase.SOURCE_ACCOUNTING_LINES,
     * sourceLines );
     * 
     * AccountingLine targetLine1 = fixtureTargetAccountingLine( "1696", "1000" ); AccountingLine targetLine2 =
     * fixtureTargetAccountingLine( "1696", "1000" ); AccountingLine targetLine3 = fixtureTargetAccountingLine( "1696", "1000" );
     * AccountingLine targetLine4 = fixtureTargetAccountingLine( "1696", "1000" ); AccountingLine targetLine5 =
     * fixtureTargetAccountingLine( "1696", "1000" ); AccountingLine targetLine6 = fixtureTargetAccountingLine( "1696", "1000" );
     * AccountingLine targetLine7 = fixtureTargetAccountingLine( "1696", "1000" ); AccountingLine targetLine8 =
     * fixtureTargetAccountingLine( "1696", "1000" ); AccountingLine targetLine9 = fixtureTargetAccountingLine( "1696", "1000" );
     * AccountingLine targetLine10 = fixtureTargetAccountingLine( "1696", "1000" );
     * 
     * targetLine1.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.BUDGET_ONLY);
     * targetLine2.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.CONSTRUCTION_IN_PROG);
     * targetLine3.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.HOURLY_WAGES);
     * targetLine4.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.MANDATORY_TRANSFER);
     * targetLine5.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.RESERVES);
     * targetLine6.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.WRITE_OFF);
     * targetLine7.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.SALARIES_WAGES);
     * targetLine8.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.STATE_APP);
     * targetLine9.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.SALARIES);
     * targetLine10.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.INVEST);
     * 
     * targetLines.add( targetLine1 ); targetLines.add(targetLine2); targetLines.add(targetLine3); targetLines.add(targetLine4);
     * targetLines.add(targetLine5); targetLines.add(targetLine6); targetLines.add(targetLine7); targetLines.add(targetLine8);
     * targetLines.add(targetLine9); targetLines.add(targetLine10); addFixture( KualiRuleTestCase.TARGET_ACCOUNTING_LINES,
     * targetLines ); }
     * 
     * public void fixturesApplyAddAccountingLineBusinessRulesValidObjCodeSubTypes() throws Exception { }
     * 
     * public void fixturesApplyAddAccountingLineBusinessRulesInvalidObjLevelCode() throws Exception { List sourceLines = new
     * ArrayList(); List targetLines = new ArrayList();
     * 
     * addFixture( KualiRuleTestCase.DOCUMENT_DESCRIPTION, "test" ); addFixture( KualiRuleTestCase.EXPLANATION, "This is a test
     * document, testing invalid object sub-type (Student Fees) " + "with invalid sub-fund group (DCEDU) " + "accounting line
     * business rules." ); AccountingLine sourceLine1 = fixtureSourceAccountingLine( "1696", "1000" ); ObjLevel level = new
     * ObjLevel(); level.setFinancialObjectLevelCode(OBJECT_LEVEL_CODE.CONTRACT_GRANTS);
     * sourceLine1.getObjectCode().setFinancialObjectLevel(level);
     * 
     * 
     * sourceLines.add( sourceLine1 ); addFixture( KualiRuleTestCase.SOURCE_ACCOUNTING_LINES, sourceLines );
     * 
     * AccountingLine targetLine1 = fixtureTargetAccountingLine( "1696", "1000" );
     * 
     * targetLine1.getObjectCode().setFinancialObjectLevel(level);
     * 
     * 
     * targetLines.add( targetLine1 ); addFixture( KualiRuleTestCase.TARGET_ACCOUNTING_LINES, targetLines ); }
     * 
     * public void fixturesApplyAddAccountingLineBusinessRulesValidObjLevelCode() throws Exception { }
     * 
     * public void fixturesApplyAddAccountingLineBusinessRulesInvalidObjType() throws Exception { List sourceLines = new
     * ArrayList(); List targetLines = new ArrayList();
     * 
     * addFixture( KualiRuleTestCase.DOCUMENT_DESCRIPTION, "test" ); addFixture( KualiRuleTestCase.EXPLANATION, "This is a test
     * document, testing invalid object sub-type (Student Fees) " + "with invalid sub-fund group (DCEDU) " + "accounting line
     * business rules." ); AccountingLine sourceLine1 = fixtureSourceAccountingLine( "1696", "1000" ); AccountingLine sourceLine2 =
     * fixtureSourceAccountingLine( "1696", "1000" );
     * sourceLine1.getObjectCode().setFinancialObjectCode(OBJECT_TYPE_CODE.INCOME_NOT_CASH);
     * sourceLine2.getObjectCode().setFinancialObjectCode(OBJECT_TYPE_CODE.EXPENSE_NOT_EXPENDITURE);
     * 
     * 
     * sourceLines.add( sourceLine1 ); sourceLines.add(sourceLine2); addFixture( KualiRuleTestCase.SOURCE_ACCOUNTING_LINES,
     * sourceLines );
     * 
     * AccountingLine targetLine1 = fixtureTargetAccountingLine( "1696", "1000" ); AccountingLine targetLine2 =
     * fixtureTargetAccountingLine( "1696", "1000" );
     * targetLine1.getObjectCode().setFinancialObjectCode(OBJECT_TYPE_CODE.INCOME_NOT_CASH);
     * targetLine2.getObjectCode().setFinancialObjectCode(OBJECT_TYPE_CODE.EXPENSE_NOT_EXPENDITURE);
     * 
     * 
     * targetLines.add( targetLine1 ); targetLines.add(targetLine2); addFixture( KualiRuleTestCase.TARGET_ACCOUNTING_LINES,
     * targetLines ); }
     * 
     * public void fixturesApplyAddAccountingLineBusinessRulesValidObjType() throws Exception { }
     * 
     * public void fixturesApplyAddAccountingLineBusinessRulesInvalidObjSubTypeAssessOrTransOfFunds() throws Exception { List
     * sourceLines = new ArrayList(); List targetLines = new ArrayList();
     * 
     * addFixture( KualiRuleTestCase.DOCUMENT_DESCRIPTION, "test" ); addFixture( KualiRuleTestCase.EXPLANATION, "This is a test
     * document, testing invalid object sub-type (Student Fees) " + "with invalid sub-fund group (DCEDU) " + "accounting line
     * business rules." ); AccountingLine sourceLine1 = fixtureSourceAccountingLine( "1696", "1000" ); AccountingLine sourceLine2 =
     * fixtureSourceAccountingLine( "1696", "1000" );
     * 
     * sourceLine1.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.ASSESSMENT);
     * sourceLine2.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.TRANSFER_OF_FUNDS);
     * 
     * 
     * sourceLines.add( sourceLine1 ); sourceLines.add(sourceLine2); addFixture( KualiRuleTestCase.SOURCE_ACCOUNTING_LINES,
     * sourceLines );
     * 
     * AccountingLine targetLine1 = fixtureTargetAccountingLine( "1696", "1000" ); AccountingLine targetLine2 =
     * fixtureTargetAccountingLine( "1696", "1000" );
     * 
     * targetLine1.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.ASSESSMENT);
     * targetLine2.getObjectCode().getFinancialObjectSubType().setCode(OBJECT_SUB_TYPE_CODE.TRANSFER_OF_FUNDS);
     * 
     * targetLines.add( targetLine1 ); targetLines.add(targetLine2); addFixture( KualiRuleTestCase.TARGET_ACCOUNTING_LINES,
     * targetLines ); }
     * 
     * public void fixturesApplyAddAccountingLineBusinessRulesValidObjSubTypeAssessOrTransOfFunds() throws Exception { }
     * 
     * public void fixturesApplyAddAccountingLineBusinessRulesInvalidFundGroup() throws Exception { List sourceLines = new
     * ArrayList(); List targetLines = new ArrayList();
     * 
     * //This sub fund group needs to have a fund group //code of FUND_GROUP_CODE.LOAN_FUND This may not be necessary if we have an
     * appropriate account BusinessObjectService boService = SpringServiceLocator.getBusinessObjectService(); String
     * subFundGroupCode = "SOME_CODE"; Map subFundGroupId = new HashMap(); subFundGroupId.put("code", subFundGroupCode);
     * SubFundGroup sfg = (SubFundGroup) boService.findByPrimaryKey(SubFundGroup.class, subFundGroupId);
     * 
     * addFixture( KualiRuleTestCase.DOCUMENT_DESCRIPTION, "test" ); addFixture( KualiRuleTestCase.EXPLANATION, "This is a test
     * document, testing invalid object sub-type (Student Fees) " + "with invalid sub-fund group (DCEDU) " + "accounting line
     * business rules." ); AccountingLine sourceLine1 = fixtureSourceAccountingLine( "1696", "1000" );
     * 
     * sourceLines.add( sourceLine1 ); addFixture( KualiRuleTestCase.SOURCE_ACCOUNTING_LINES, sourceLines );
     * 
     * AccountingLine targetLine1 = fixtureTargetAccountingLine( "1696", "1000" );
     * 
     * targetLines.add( targetLine1 ); addFixture( KualiRuleTestCase.TARGET_ACCOUNTING_LINES, targetLines ); }
     * 
     * public void fixturesApplyAddAccountingLineBusinessRulesValidFundGroup() throws Exception { }
     * 
     * public void fixturesApplyAddAccountingLineBusinessRulesInvalidSubFundGroup() throws Exception { //Doing this test requires
     * grabbing //SUB_FUND_GROUP_CODE.CODE_INVESTMENT_PLANT List sourceLines = new ArrayList(); List targetLines = new ArrayList();
     * 
     * //This sub fund group needs to have a fund group //code of FUND_GROUP_CODE.LOAN_FUND This may not be necessary if we have an
     * appropriate account BusinessObjectService boService = SpringServiceLocator.getBusinessObjectService(); String
     * subFundGroupCode = "SOME_CODE"; Map subFundGroupId = new HashMap(); subFundGroupId.put("code", subFundGroupCode);
     * SubFundGroup sfg = (SubFundGroup) boService.findByPrimaryKey(SubFundGroup.class, subFundGroupId);
     * 
     * addFixture( KualiRuleTestCase.DOCUMENT_DESCRIPTION, "test" ); addFixture( KualiRuleTestCase.EXPLANATION, "This is a test
     * document, testing invalid object sub-type (Student Fees) " + "with invalid sub-fund group (DCEDU) " + "accounting line
     * business rules." ); AccountingLine sourceLine1 = fixtureSourceAccountingLine( "1696", "1000" );
     * 
     * sourceLines.add( sourceLine1 ); addFixture( KualiRuleTestCase.SOURCE_ACCOUNTING_LINES, sourceLines );
     * 
     * AccountingLine targetLine1 = fixtureTargetAccountingLine( "1696", "1000" );
     * 
     * targetLines.add( targetLine1 ); addFixture( KualiRuleTestCase.TARGET_ACCOUNTING_LINES, targetLines ); }
     */
}
