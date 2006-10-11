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

import static org.kuali.core.util.SpringServiceLocator.getDataDictionaryService;
import static org.kuali.core.util.SpringServiceLocator.getDocumentService;
import static org.kuali.core.util.SpringServiceLocator.getDocumentTypeService;
import static org.kuali.module.financial.rules.TransactionalDocumentRuleTestUtils.testAddAccountingLineRule_IsObjectCodeAllowed;
import static org.kuali.module.financial.rules.TransactionalDocumentRuleTestUtils.testAddAccountingLineRule_IsObjectTypeAllowed;
import static org.kuali.module.financial.rules.TransactionalDocumentRuleTestUtils.testAddAccountingLineRule_ProcessAddAccountingLineBusinessRules;
import static org.kuali.module.financial.rules.TransactionalDocumentRuleTestUtils.testAddAccountingLine_IsObjectSubTypeAllowed;
import static org.kuali.module.financial.rules.TransactionalDocumentRuleTestUtils.testGenerateGeneralLedgerPendingEntriesRule_ProcessGenerateGeneralLedgerPendingEntries;
import static org.kuali.module.financial.rules.TransactionalDocumentRuleTestUtils.testRouteDocumentRule_processRouteDocument;
import static org.kuali.module.financial.rules.TransactionalDocumentRuleTestUtils.testSaveDocumentRule_ProcessSaveDocument;
import static org.kuali.module.financial.rules.IsDebitTestUtils.Amount.NEGATIVE;
import static org.kuali.module.financial.rules.IsDebitTestUtils.Amount.POSITIVE;
import static org.kuali.test.fixtures.AccountingLineFixture.ACCRUED_INCOME_LINE;
import static org.kuali.test.fixtures.AccountingLineFixture.CASH_LINE;
import static org.kuali.test.fixtures.AccountingLineFixture.EXPENSE_GEC_LINE;
import static org.kuali.test.fixtures.AccountingLineFixture.LINE10;
import static org.kuali.test.fixtures.AccountingLineFixture.LINE16;
import static org.kuali.test.fixtures.AccountingLineFixture.LINE8;
import static org.kuali.test.fixtures.AccountingLineFixture.LOSSS_ON_RETIRE_LINE;
import static org.kuali.test.fixtures.GeneralLedgerPendingEntryFixture.EXPECTED_GEC_EXPLICIT_SOURCE_PENDING_ENTRY;
import static org.kuali.test.fixtures.GeneralLedgerPendingEntryFixture.EXPECTED_GEC_EXPLICIT_SOURCE_PENDING_ENTRY_FOR_EXPENSE;
import static org.kuali.test.fixtures.GeneralLedgerPendingEntryFixture.EXPECTED_GEC_EXPLICIT_TARGET_PENDING_ENTRY;
import static org.kuali.test.fixtures.GeneralLedgerPendingEntryFixture.EXPECTED_GEC_EXPLICIT_TARGET_PENDING_ENTRY_FOR_EXPENSE;
import static org.kuali.test.fixtures.GeneralLedgerPendingEntryFixture.EXPECTED_GEC_OFFSET_SOURCE_PENDING_ENTRY;
import static org.kuali.test.fixtures.GeneralLedgerPendingEntryFixture.EXPECTED_GEC_OFFSET_TARGET_PENDING_ENTRY;
import static org.kuali.test.fixtures.UserNameFixture.KHUNTLEY;

import java.util.ArrayList;
import java.util.List;

import org.kuali.Constants;
import org.kuali.core.bo.AccountingLine;
import org.kuali.core.bo.SourceAccountingLine;
import org.kuali.core.bo.TargetAccountingLine;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.financial.document.GeneralErrorCorrectionDocument;
import org.kuali.module.gl.bo.GeneralLedgerPendingEntry;
import org.kuali.test.DocumentTestUtils;
import org.kuali.test.KualiTestBase;
import org.kuali.test.WithTestSpringContext;

@WithTestSpringContext(session = KHUNTLEY)
public class GeneralErrorCorrectionDocumentRuleTest extends KualiTestBase {
    public static final Class<GeneralErrorCorrectionDocument> DOCUMENT_CLASS = GeneralErrorCorrectionDocument.class;

    public void testIsDebit_source_income_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(transactionalDocument, SourceAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    public void testIsDebit_source_income_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(transactionalDocument, SourceAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    public void testIsDebit_source_income_zeroAmount() throws Exception {

        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(transactionalDocument, SourceAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    public void testIsDebit_source_expense_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(transactionalDocument, SourceAccountingLine.class, POSITIVE);

        assertFalse(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    public void testIsDebit_source_expense_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(transactionalDocument, SourceAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    public void testIsDebit_source_expense_zeroAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(transactionalDocument, SourceAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    public void testIsDebit_source_asset_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(transactionalDocument, SourceAccountingLine.class, POSITIVE);

        assertFalse(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    public void testIsDebit_source_asset_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(transactionalDocument, SourceAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    public void testIsDebit_source_asset_zeroAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(transactionalDocument, SourceAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    public void testIsDebit_source_liability_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(transactionalDocument, SourceAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    public void testIsDebit_source_liability_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(transactionalDocument, SourceAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    public void testIsDebit_source_liability_zeroAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(transactionalDocument, SourceAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    public void testIsDebit_target_income_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(transactionalDocument, TargetAccountingLine.class, POSITIVE);

        assertFalse(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    public void testIsDebit_target_income_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(transactionalDocument, TargetAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    public void testIsDebit_target_income_zeroAmount() throws Exception {

        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(transactionalDocument, TargetAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    public void testIsDebit_target_expense_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(transactionalDocument, TargetAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    public void testIsDebit_target_expense_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(transactionalDocument, TargetAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    public void testIsDebit_target_expense_zeroAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(transactionalDocument, TargetAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    public void testIsDebit_target_asset_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(transactionalDocument, TargetAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    public void testIsDebit_target_asset_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(transactionalDocument, TargetAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    public void testIsDebit_target_asset_zeroAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(transactionalDocument, TargetAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    public void testIsDebit_target_liability_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(transactionalDocument, TargetAccountingLine.class, POSITIVE);

        assertFalse(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    public void testIsDebit_target_liability_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(transactionalDocument, TargetAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    public void testIsDebit_target_liability_zeroAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(transactionalDocument, TargetAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    public void testIsDebit_errorCorrection_source_income_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(transactionalDocument, SourceAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    public void testIsDebit_errorCorrection_source_income_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(transactionalDocument, SourceAccountingLine.class, NEGATIVE);

        assertFalse(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    public void testIsDebit_errorCorrection_source_income_zeroAmount() throws Exception {

        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(transactionalDocument, SourceAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    public void testIsDebit_errorCorrection_source_expense_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(transactionalDocument, SourceAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    public void testIsDebit_errorCorrection_source_expense_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(transactionalDocument, SourceAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    public void testIsDebit_errorCorrection_source_expense_zeroAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(transactionalDocument, SourceAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    public void testIsDebit_errorCorrection_source_asset_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(transactionalDocument, SourceAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    public void testIsDebit_errorCorrection_source_asset_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(transactionalDocument, SourceAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    public void testIsDebit_errorCorrection_source_asset_zeroAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(transactionalDocument, SourceAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    public void testIsDebit_errorCorrection_source_liability_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(transactionalDocument, SourceAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    public void testIsDebit_errorCorrection_source_liability_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(transactionalDocument, SourceAccountingLine.class, NEGATIVE);

        assertFalse(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    public void testIsDebit_errorCorrection_source_liability_zeroAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(transactionalDocument, SourceAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    public void testIsDebit_errorCorrection_target_income_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(transactionalDocument, TargetAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    public void testIsDebit_errorCorrection_target_income_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(transactionalDocument, TargetAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    public void testIsDebit_errorCorrection_target_income_zeroAmount() throws Exception {

        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(transactionalDocument, TargetAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    public void testIsDebit_errorCorrection_target_expense_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(transactionalDocument, TargetAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    public void testIsDebit_errorCorrection_target_expense_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(transactionalDocument, TargetAccountingLine.class, NEGATIVE);

        assertFalse(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    public void testIsDebit_errorCorrection_target_expense_zeroAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(transactionalDocument, TargetAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    public void testIsDebit_errorCorrection_target_asset_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(transactionalDocument, TargetAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    public void testIsDebit_errorCorrection_target_asset_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(transactionalDocument, TargetAccountingLine.class, NEGATIVE);

        assertFalse(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    public void testIsDebit_errorCorrection_target_asset_zeroAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(transactionalDocument, TargetAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    public void testIsDebit_errorCorrection_target_liability_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(transactionalDocument, TargetAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    public void testIsDebit_errorCorrection_target_liability_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(transactionalDocument, TargetAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    public void testIsDebit_errorCorrection_target_liability_zeroAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(transactionalDocument, TargetAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    public void testIsObjectTypeAllowed_InvalidObjectType() throws Exception {
        testAddAccountingLineRule_IsObjectTypeAllowed(DOCUMENT_CLASS, getInvalidObjectTypeSourceLine(), false);
    }

    public void testIsObjectTypeAllowed_Valid() throws Exception {
        testAddAccountingLineRule_IsObjectTypeAllowed(DOCUMENT_CLASS, getValidObjectTypeSourceLine(), true);
    }

    public void testIsObjectCodeAllowed_Valid() throws Exception {
        testAddAccountingLineRule_IsObjectCodeAllowed(DOCUMENT_CLASS, getValidObjectCodeSourceLine(), true);
    }

    public void testIsObjectCodeAllowed_InvalidObjectCode() throws Exception {
        testAddAccountingLineRule_IsObjectCodeAllowed(DOCUMENT_CLASS, getInvalidObjectCodeSourceLine(), false);
    }

    public void testAddAccountingLine_InvalidObjectSubType() throws Exception {
        TransactionalDocument doc = createDocumentWithInvalidObjectSubType();
        testAddAccountingLineRule_ProcessAddAccountingLineBusinessRules(doc, false);
    }

    public void testAddAccountingLine_Valid() throws Exception {
        TransactionalDocument doc = createDocumentWithValidObjectSubType();
        testAddAccountingLineRule_ProcessAddAccountingLineBusinessRules(doc, true);
    }

    public void testIsObjectSubTypeAllowed_InvalidSubType() throws Exception {
        testAddAccountingLine_IsObjectSubTypeAllowed(DOCUMENT_CLASS, getInvalidObjectSubTypeTargetLine(), false);
    }

    public void testIsObjectSubTypeAllowed_ValidSubType() throws Exception {
        testAddAccountingLine_IsObjectSubTypeAllowed(DOCUMENT_CLASS, getValidObjectSubTypeTargetLine(), true);
    }

    public void testProcessSaveDocument_Valid() throws Exception {
        testSaveDocumentRule_ProcessSaveDocument(createDocument(), true);
    }

    public void testProcessSaveDocument_Invalid() throws Exception {
        testSaveDocumentRule_ProcessSaveDocument(createDocumentInvalidForSave(), false);
    }

    public void testProcessSaveDocument_Invalid1() throws Exception {
        try {
            testSaveDocumentRule_ProcessSaveDocument(null, false);
            fail("validated null doc");
        }
        catch (Exception e) {
            assertTrue(true);
        }
    }

    public void testProcessRouteDocument_Valid() throws Exception {
        testRouteDocumentRule_processRouteDocument(createDocumentValidForRouting(), true);
    }

    public void testProcessRouteDocument_Invalid() throws Exception {
        testRouteDocumentRule_processRouteDocument(createDocument(), false);
    }

    public void testProcessRouteDocument_NoAccountingLines() throws Exception {
        testRouteDocumentRule_processRouteDocument(createDocument(), false);
    }

    public void testProcessRouteDocument_Unbalanced() throws Exception {
        testRouteDocumentRule_processRouteDocument(createDocumentUnbalanced(), false);
    }

    public void testProcessGenerateGeneralLedgerPendingEntries_validTargetExpense() throws Exception {
        testGenerateGeneralLedgerPendingEntriesRule_ProcessGenerateGeneralLedgerPendingEntries(createDocument5(), getExpenseTargetLine(), getExpectedExplicitTargetPendingEntryForExpense(), getExpectedOffsetTargetPendingEntry(), 2);
    }

    public void testProcessGenerateGeneralLedgerPendingEntries_validSourceExpense() throws Exception {

        testGenerateGeneralLedgerPendingEntriesRule_ProcessGenerateGeneralLedgerPendingEntries(createDocument5(), getExpenseSourceLine(), getExpectedExplicitSourcePendingEntryForExpense(), getExpectedOffsetSourcePendingEntry(), 2);
    }

    public void testProcessGenerateGeneralLedgerPendingEntries_validSourceAsset() throws Exception {
        testGenerateGeneralLedgerPendingEntriesRule_ProcessGenerateGeneralLedgerPendingEntries(createDocument5(), getAssetSourceLine(), getExpectedExplicitSourcePendingEntry(), getExpectedOffsetSourcePendingEntry(), 2);
    }

    public void testProcessGenerateGeneralLedgerPendingEntries_validTargetAsset() throws Exception {
        testGenerateGeneralLedgerPendingEntriesRule_ProcessGenerateGeneralLedgerPendingEntries(createDocument5(), getAssetTargetLine(), getExpectedExplicitTargetPendingEntry(), getExpectedOffsetTargetPendingEntryForTargetAsset(), 2);
    }

    private GeneralErrorCorrectionDocument createDocument() throws Exception {
        return DocumentTestUtils.createDocument(getDocumentService(), GeneralErrorCorrectionDocument.class);
    }

    private GeneralErrorCorrectionDocument createDocumentValidForRouting() throws Exception {
        return createDocumentWithValidObjectSubType();
    }

    private GeneralErrorCorrectionDocument createDocumentInvalidForSave() throws Exception {
        return getDocumentParameterNoDescription();
    }

    private GeneralErrorCorrectionDocument createDocumentWithValidObjectSubType() throws Exception {
        GeneralErrorCorrectionDocument retval = createDocument();
        retval.setSourceAccountingLines(getValidObjectSubTypeSourceLines());
        retval.setTargetAccountingLines(getValidObjectSubTypeTargetLines());
        return retval;
    }

    private GeneralErrorCorrectionDocument getDocumentParameterNoDescription() throws Exception {
        GeneralErrorCorrectionDocument document = DocumentTestUtils.createDocument(getDocumentService(), GeneralErrorCorrectionDocument.class);
        document.getDocumentHeader().setFinancialDocumentDescription(null);
        return document;
    }

    private SourceAccountingLine getExpenseSourceLine() throws Exception {
        return EXPENSE_GEC_LINE.createAccountingLine(SourceAccountingLine.class, Constants.GL_DEBIT_CODE);
    }

    private TargetAccountingLine getExpenseTargetLine() throws Exception {
        return EXPENSE_GEC_LINE.createAccountingLine(TargetAccountingLine.class, Constants.GL_CREDIT_CODE);
    }

    private TargetAccountingLine getAssetTargetLine() throws Exception {
        return getAccruedIncomeTargetLineParameter();
    }

    private TargetAccountingLine getValidObjectSubTypeTargetLine() throws Exception {
        return getAccruedIncomeTargetLineParameter();
    }

    private TargetAccountingLine getInvalidObjectSubTypeTargetLine() throws Exception {
        return CASH_LINE.createTargetAccountingLine();
    }

    private List<SourceAccountingLine> getValidObjectSubTypeSourceLines() throws Exception {
        List<SourceAccountingLine> retval = new ArrayList<SourceAccountingLine>();
        retval.add(getAccruedIncomeSourceLineParameter());
        retval.add(getAccruedIncomeSourceLineParameter());
        return retval;
    }

    private List<SourceAccountingLine> getInvalidObjectSubTypeSourceLines() throws Exception {
        List<SourceAccountingLine> retval = new ArrayList<SourceAccountingLine>();
        retval.add(CASH_LINE.createSourceAccountingLine());
        retval.add(LOSSS_ON_RETIRE_LINE.createSourceAccountingLine());
        return retval;
    }

    private List<TargetAccountingLine> getInvalidObjectSubTypeTargetLines() throws Exception {
        List<TargetAccountingLine> retval = new ArrayList<TargetAccountingLine>();
        retval.add(CASH_LINE.createTargetAccountingLine());
        retval.add(LOSSS_ON_RETIRE_LINE.createTargetAccountingLine());
        return retval;
    }

    private List<TargetAccountingLine> getValidObjectSubTypeTargetLines() throws Exception {
        List<TargetAccountingLine> retval = new ArrayList<TargetAccountingLine>();
        retval.add(getAccruedIncomeTargetLineParameter());
        retval.add(getAccruedIncomeTargetLineParameter());
        return retval;
    }

    private SourceAccountingLine getValidObjectTypeSourceLine() throws Exception {
        return LINE8.createSourceAccountingLine();
    }

    private SourceAccountingLine getInvalidObjectTypeSourceLine() throws Exception {
        return LINE16.createSourceAccountingLine();
    }

    private SourceAccountingLine getInvalidObjectCodeSourceLine() throws Exception {
        return LINE10.createSourceAccountingLine();
    }

    private SourceAccountingLine getValidObjectCodeSourceLine() throws Exception {
        return ACCRUED_INCOME_LINE.createAccountingLine(SourceAccountingLine.class, Constants.GL_DEBIT_CODE);
    }

    private SourceAccountingLine getAssetSourceLine() throws Exception {
        return ACCRUED_INCOME_LINE.createAccountingLine(SourceAccountingLine.class, Constants.GL_DEBIT_CODE);
    }

    private TransactionalDocument createDocument5() throws Exception {
        return DocumentTestUtils.createDocument(getDocumentService(), GeneralErrorCorrectionDocument.class);
    }

    private GeneralErrorCorrectionDocument createDocumentWithInvalidObjectSubType() throws Exception {
        GeneralErrorCorrectionDocument retval = createDocument();
        retval.setSourceAccountingLines(getInvalidObjectSubTypeSourceLines());
        retval.setTargetAccountingLines(getInvalidObjectSubTypeTargetLines());
        return retval;
    }

    private GeneralErrorCorrectionDocument createDocumentUnbalanced() throws Exception {
        GeneralErrorCorrectionDocument retval = createDocument();
        retval.setSourceAccountingLines(getInvalidObjectSubTypeSourceLines());
        retval.addTargetAccountingLine(getValidObjectSubTypeTargetLine());
        return retval;
    }

    private SourceAccountingLine getAccruedIncomeSourceLineParameter() throws Exception {
        return ACCRUED_INCOME_LINE.createAccountingLine(SourceAccountingLine.class, Constants.GL_DEBIT_CODE);
    }

    private TargetAccountingLine getAccruedIncomeTargetLineParameter() throws Exception {
        return ACCRUED_INCOME_LINE.createAccountingLine(TargetAccountingLine.class, Constants.GL_CREDIT_CODE);
    }

    private GeneralLedgerPendingEntry getExpectedExplicitSourcePendingEntry() {
        return EXPECTED_GEC_EXPLICIT_SOURCE_PENDING_ENTRY.createGeneralLedgerPendingEntry();
    }

    private GeneralLedgerPendingEntry getExpectedExplicitTargetPendingEntry() {
        return EXPECTED_GEC_EXPLICIT_TARGET_PENDING_ENTRY.createGeneralLedgerPendingEntry();
    }

    private GeneralLedgerPendingEntry getExpectedOffsetTargetPendingEntry() {
        return EXPECTED_GEC_OFFSET_TARGET_PENDING_ENTRY.createGeneralLedgerPendingEntry();
    }

    private GeneralLedgerPendingEntry getExpectedOffsetTargetPendingEntryForTargetAsset() {
        GeneralLedgerPendingEntry entry = EXPECTED_GEC_OFFSET_TARGET_PENDING_ENTRY.createGeneralLedgerPendingEntry();
        entry.setTransactionDebitCreditCode(Constants.GL_CREDIT_CODE);
        return entry;
    }

    private GeneralLedgerPendingEntry getExpectedOffsetSourcePendingEntry() {
        return EXPECTED_GEC_OFFSET_SOURCE_PENDING_ENTRY.createGeneralLedgerPendingEntry();
    }

    private GeneralLedgerPendingEntry getExpectedExplicitSourcePendingEntryForExpense() {
        return EXPECTED_GEC_EXPLICIT_SOURCE_PENDING_ENTRY_FOR_EXPENSE.createGeneralLedgerPendingEntry();
    }

    private GeneralLedgerPendingEntry getExpectedExplicitTargetPendingEntryForExpense() {
        return EXPECTED_GEC_EXPLICIT_TARGET_PENDING_ENTRY_FOR_EXPENSE.createGeneralLedgerPendingEntry();
    }
}
