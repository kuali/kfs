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
package org.kuali.module.financial.rules;


import static org.kuali.module.financial.rules.AccountingDocumentRuleTestUtils.testAddAccountingLineRule_ProcessAddAccountingLineBusinessRules;
import static org.kuali.module.financial.rules.AccountingDocumentRuleTestUtils.testGenerateGeneralLedgerPendingEntriesRule_ProcessGenerateGeneralLedgerPendingEntries;
import static org.kuali.module.financial.rules.AccountingDocumentRuleTestUtils.testRouteDocumentRule_processRouteDocument;
import static org.kuali.module.financial.rules.AccountingDocumentRuleTestUtils.testSaveDocumentRule_ProcessSaveDocument;
import static org.kuali.module.financial.rules.IsDebitTestUtils.Amount.NEGATIVE;
import static org.kuali.module.financial.rules.IsDebitTestUtils.Amount.POSITIVE;
import static org.kuali.test.fixtures.AccountingLineFixture.EXPENSE_LINE;
import static org.kuali.test.fixtures.AccountingLineFixture.FLEXIBLE_EXPENSE_LINE;
import static org.kuali.test.fixtures.AccountingLineFixture.LINE10;
import static org.kuali.test.fixtures.AccountingLineFixture.LINE11;
import static org.kuali.test.fixtures.AccountingLineFixture.LINE12;
import static org.kuali.test.fixtures.AccountingLineFixture.LINE13;
import static org.kuali.test.fixtures.AccountingLineFixture.LINE8;
import static org.kuali.test.fixtures.AccountingLineFixture.LINE9;
import static org.kuali.test.fixtures.GeneralLedgerPendingEntryFixture.EXPECTED_EXPLICIT_SOURCE_PENDING_ENTRY_FOR_EXPENSE;
import static org.kuali.test.fixtures.GeneralLedgerPendingEntryFixture.EXPECTED_EXPLICIT_TARGET_PENDING_ENTRY_FOR_EXPENSE;
import static org.kuali.test.fixtures.GeneralLedgerPendingEntryFixture.EXPECTED_FLEXIBLE_EXPLICIT_SOURCE_PENDING_ENTRY_FOR_EXPENSE;
import static org.kuali.test.fixtures.GeneralLedgerPendingEntryFixture.EXPECTED_FLEXIBLE_EXPLICIT_SOURCE_PENDING_ENTRY_FOR_EXPENSE2;
import static org.kuali.test.fixtures.GeneralLedgerPendingEntryFixture.EXPECTED_FLEXIBLE_OFFSET_SOURCE_PENDING_ENTRY;
import static org.kuali.test.fixtures.GeneralLedgerPendingEntryFixture.EXPECTED_FLEXIBLE_OFFSET_SOURCE_PENDING_ENTRY_MISSING_OFFSET_DEFINITION;
import static org.kuali.test.fixtures.GeneralLedgerPendingEntryFixture.EXPECTED_OFFSET_SOURCE_PENDING_ENTRY;
import static org.kuali.test.fixtures.GeneralLedgerPendingEntryFixture.EXPECTED_OFFSET_TARGET_PENDING_ENTRY;
import static org.kuali.test.fixtures.UserNameFixture.KHUNTLEY;
import static org.kuali.test.util.KualiTestAssertionUtils.assertGlobalErrorMapContains;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.DocumentTypeService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.bo.TargetAccountingLine;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.context.TestUtils;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.validation.AccountingLineValueAllowedValidation;
import org.kuali.kfs.validation.Validation;
import org.kuali.module.chart.bo.OffsetDefinition;
import org.kuali.module.financial.document.TransferOfFundsDocument;
import org.kuali.test.ConfigureContext;
import org.kuali.test.DocumentTestUtils;
import org.kuali.test.fixtures.GeneralLedgerPendingEntryFixture;
import org.kuali.test.suite.AnnotationTestSuite;
import org.kuali.test.suite.CrossSectionSuite;

@ConfigureContext(session = KHUNTLEY)
public class TransferOfFundsDocumentRuleTest extends KualiTestBase {
    public static final Class<TransferOfFundsDocument> DOCUMENT_CLASS = TransferOfFundsDocument.class;

    private static final String NON_MANDATORY_TRANSFER_OBJECT_CODE = "1669";


    @AnnotationTestSuite(CrossSectionSuite.class)
    public void testProcessGenerateGeneralLedgerPendingEntries_validSourceExpenseFlexibleOffset() throws Exception {
        TestUtils.setSystemParameter(OffsetDefinition.class, KFSConstants.SystemGroupParameterNames.FLEXIBLE_OFFSET_ENABLED_FLAG, "Y");
        testGenerateGeneralLedgerPendingEntriesRule_ProcessGenerateGeneralLedgerPendingEntries(createDocument(), FLEXIBLE_EXPENSE_LINE.createTargetAccountingLine(), EXPECTED_FLEXIBLE_EXPLICIT_SOURCE_PENDING_ENTRY_FOR_EXPENSE2, EXPECTED_FLEXIBLE_OFFSET_SOURCE_PENDING_ENTRY);
    }

    public void testProcessGenerateGeneralLedgerPendingEntries_validSourceExpenseMissingOffsetDefinition() throws Exception {
        TestUtils.setSystemParameter(OffsetDefinition.class, KFSConstants.SystemGroupParameterNames.FLEXIBLE_OFFSET_ENABLED_FLAG, "Y");
        TransferOfFundsDocument document = DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), TransferOfFundsDocument.class);
        document.setPostingYear(2000); // because our test database has no offset definitions (GL_OFFSET_DEFN_T) for
        // UNIV_FISCAL_YR=2000.
        document.setPostingPeriodCode("06"); // because this BO reveals no change when the year is set by itself.
        AccountingLine accountingLine = FLEXIBLE_EXPENSE_LINE.createSourceAccountingLine();
        GeneralLedgerPendingEntryFixture expectedExplicit = EXPECTED_FLEXIBLE_EXPLICIT_SOURCE_PENDING_ENTRY_FOR_EXPENSE;
        GeneralLedgerPendingEntryFixture expectedOffset = EXPECTED_FLEXIBLE_OFFSET_SOURCE_PENDING_ENTRY_MISSING_OFFSET_DEFINITION;

        boolean ruleResult = testGenerateGeneralLedgerPendingEntriesRule_ProcessGenerateGeneralLedgerPendingEntries(document, accountingLine, expectedExplicit, expectedOffset);
        assertEquals(false, ruleResult);
        assertGlobalErrorMapContains(KFSConstants.GENERAL_LEDGER_PENDING_ENTRIES_TAB_ERRORS, KFSKeyConstants.ERROR_DOCUMENT_NO_OFFSET_DEFINITION);
    }

    public void testIsDebit_source_income_positveAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(accountingDocument, SourceAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebit(SpringContext.getBean(DocumentTypeService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_source_income_negativeAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(accountingDocument, SourceAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(SpringContext.getBean(DocumentTypeService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_source_income_zeroAmount() throws Exception {

        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(accountingDocument, SourceAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(SpringContext.getBean(DocumentTypeService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_source_expense_positveAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(accountingDocument, SourceAccountingLine.class, POSITIVE);
        assertTrue(IsDebitTestUtils.isDebit(SpringContext.getBean(DocumentTypeService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_source_expense_negativeAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(accountingDocument, SourceAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(SpringContext.getBean(DocumentTypeService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_source_expense_zeroAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(accountingDocument, SourceAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(SpringContext.getBean(DocumentTypeService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_source_asset_positveAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(accountingDocument, SourceAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(SpringContext.getBean(DocumentTypeService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_source_asset_negativeAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(accountingDocument, SourceAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(SpringContext.getBean(DocumentTypeService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_source_asset_zeroAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(accountingDocument, SourceAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(SpringContext.getBean(DocumentTypeService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_source_liability_positveAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(accountingDocument, SourceAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(SpringContext.getBean(DocumentTypeService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_source_liability_negativeAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(accountingDocument, SourceAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(SpringContext.getBean(DocumentTypeService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_source_liability_zeroAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(accountingDocument, SourceAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(SpringContext.getBean(DocumentTypeService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_target_income_positveAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(accountingDocument, TargetAccountingLine.class, POSITIVE);

        assertFalse(IsDebitTestUtils.isDebit(SpringContext.getBean(DocumentTypeService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_target_income_negativeAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(accountingDocument, TargetAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(SpringContext.getBean(DocumentTypeService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_target_income_zeroAmount() throws Exception {

        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(accountingDocument, TargetAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(SpringContext.getBean(DocumentTypeService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_target_expense_positveAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(accountingDocument, TargetAccountingLine.class, POSITIVE);

        assertFalse(IsDebitTestUtils.isDebit(SpringContext.getBean(DocumentTypeService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_target_expense_negativeAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(accountingDocument, TargetAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(SpringContext.getBean(DocumentTypeService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_target_expense_zeroAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(accountingDocument, TargetAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(SpringContext.getBean(DocumentTypeService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_target_asset_positveAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(accountingDocument, TargetAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(SpringContext.getBean(DocumentTypeService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_target_asset_negativeAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(accountingDocument, TargetAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(SpringContext.getBean(DocumentTypeService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_target_asset_zeroAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(accountingDocument, TargetAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(SpringContext.getBean(DocumentTypeService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_target_liability_positveAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(accountingDocument, TargetAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(SpringContext.getBean(DocumentTypeService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_target_liability_negativeAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(accountingDocument, TargetAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(SpringContext.getBean(DocumentTypeService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_target_liability_zeroAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(accountingDocument, TargetAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(SpringContext.getBean(DocumentTypeService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_errorCorrection_source_income_negativeAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getErrorCorrectionDocument(SpringContext.getBean(DocumentService.class), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(accountingDocument, SourceAccountingLine.class, NEGATIVE);

        assertFalse(IsDebitTestUtils.isDebit(SpringContext.getBean(DocumentTypeService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_errorCorrection_source_expense_negativeAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getErrorCorrectionDocument(SpringContext.getBean(DocumentService.class), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(accountingDocument, SourceAccountingLine.class, NEGATIVE);

        assertFalse(IsDebitTestUtils.isDebit(SpringContext.getBean(DocumentTypeService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_errorCorrection_source_asset_positveAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getErrorCorrectionDocument(SpringContext.getBean(DocumentService.class), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(accountingDocument, SourceAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(SpringContext.getBean(DocumentTypeService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_errorCorrection_source_asset_negativeAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getErrorCorrectionDocument(SpringContext.getBean(DocumentService.class), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(accountingDocument, SourceAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(SpringContext.getBean(DocumentTypeService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_errorCorrection_source_asset_zeroAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getErrorCorrectionDocument(SpringContext.getBean(DocumentService.class), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(accountingDocument, SourceAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(SpringContext.getBean(DocumentTypeService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_errorCorrection_source_liability_positveAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getErrorCorrectionDocument(SpringContext.getBean(DocumentService.class), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(accountingDocument, SourceAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(SpringContext.getBean(DocumentTypeService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_errorCorrection_source_liability_negativeAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getErrorCorrectionDocument(SpringContext.getBean(DocumentService.class), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(accountingDocument, SourceAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(SpringContext.getBean(DocumentTypeService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_errorCorrection_source_liability_zeroAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getErrorCorrectionDocument(SpringContext.getBean(DocumentService.class), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(accountingDocument, SourceAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(SpringContext.getBean(DocumentTypeService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_errorCorrection_target_income_negativeAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getErrorCorrectionDocument(SpringContext.getBean(DocumentService.class), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(accountingDocument, TargetAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebit(SpringContext.getBean(DocumentTypeService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));

    }

    public void testIsDebit_errorCorrection_target_expense_negativeAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getErrorCorrectionDocument(SpringContext.getBean(DocumentService.class), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(accountingDocument, TargetAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebit(SpringContext.getBean(DocumentTypeService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_errorCorrection_target_asset_positveAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getErrorCorrectionDocument(SpringContext.getBean(DocumentService.class), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(accountingDocument, TargetAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(SpringContext.getBean(DocumentTypeService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_errorCorrection_target_asset_negativeAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getErrorCorrectionDocument(SpringContext.getBean(DocumentService.class), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(accountingDocument, TargetAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(SpringContext.getBean(DocumentTypeService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_errorCorrection_target_asset_zeroAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getErrorCorrectionDocument(SpringContext.getBean(DocumentService.class), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(accountingDocument, TargetAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(SpringContext.getBean(DocumentTypeService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_errorCorrection_target_liability_positveAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getErrorCorrectionDocument(SpringContext.getBean(DocumentService.class), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(accountingDocument, TargetAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(SpringContext.getBean(DocumentTypeService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_errorCorrection_target_liability_negativeAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getErrorCorrectionDocument(SpringContext.getBean(DocumentService.class), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(accountingDocument, TargetAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(SpringContext.getBean(DocumentTypeService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_errorCorrection_target_liability_zeroAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getErrorCorrectionDocument(SpringContext.getBean(DocumentService.class), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(accountingDocument, TargetAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(SpringContext.getBean(DocumentTypeService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsObjectTypeAllowed_InvalidObjectType() throws Exception {
        testAddAccountingLineRule_IsObjectTypeAllowed(getInvalidObjectTypeSourceLine(), false);
    }

    public void testIsObjectTypeAllowed_Valid() throws Exception {
        testAddAccountingLineRule_IsObjectTypeAllowed(getValidObjectTypeSourceLine(), true);
    }

    public void testIsObjectCodeAllowed_Valid() throws Exception {
        testAddAccountingLineRule_IsObjectCodeAllowed(getValidObjectCodeSourceLine(), true);
    }

    public void testIsObjectCodeAllowed_InvalidObjectCode() throws Exception {
        testAddAccountingLineRule_IsObjectCodeAllowed(getInvalidObjectCodeSourceLine(), false);
    }

    public void testAddAccountingLine_InvalidObjectSubType() throws Exception {
        AccountingDocument doc = createDocumentWithInvalidObjectSubType();
        // make sure we are using a valid object code for this type of doc
        for (int i = 0; i < doc.getSourceAccountingLines().size(); i++) {
            SourceAccountingLine sourceAccountingLine = (SourceAccountingLine) doc.getSourceAccountingLines().get(i);
            sourceAccountingLine.setFinancialObjectCode(NON_MANDATORY_TRANSFER_OBJECT_CODE);
        }

        for (int i = 0; i < doc.getTargetAccountingLines().size(); i++) {
            TargetAccountingLine sourceAccountingLine = (TargetAccountingLine) doc.getTargetAccountingLines().get(i);
            sourceAccountingLine.setFinancialObjectCode(NON_MANDATORY_TRANSFER_OBJECT_CODE);
        }
        // todo: is this correct? a test of an invalid object sub type expects an outcome of true
        testAddAccountingLineRule_ProcessAddAccountingLineBusinessRules(doc, true);
    }

    public void testAddAccountingLine_Valid() throws Exception {
        AccountingDocument doc = createDocumentWithValidObjectSubType();
        testAddAccountingLineRule_ProcessAddAccountingLineBusinessRules(doc, true);
    }

    public void testIsObjectSubTypeAllowed_InvalidSubType() throws Exception {
        testAddAccountingLine_IsObjectSubTypeAllowed(getInvalidObjectSubTypeTargetLine(), false);
    }

    public void testIsObjectSubTypeAllowed_ValidSubType() throws Exception {
        testAddAccountingLine_IsObjectSubTypeAllowed(getValidObjectSubTypeTargetLine(), true);
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
        testGenerateGeneralLedgerPendingEntriesRule_ProcessGenerateGeneralLedgerPendingEntries(createDocument(), EXPENSE_LINE.createTargetAccountingLine(), EXPECTED_EXPLICIT_TARGET_PENDING_ENTRY_FOR_EXPENSE, EXPECTED_OFFSET_TARGET_PENDING_ENTRY);
    }

    public void testProcessGenerateGeneralLedgerPendingEntries_validSourceExpense() throws Exception {

        testGenerateGeneralLedgerPendingEntriesRule_ProcessGenerateGeneralLedgerPendingEntries(createDocument(), EXPENSE_LINE.createSourceAccountingLine(), EXPECTED_EXPLICIT_SOURCE_PENDING_ENTRY_FOR_EXPENSE, EXPECTED_OFFSET_SOURCE_PENDING_ENTRY);
    }

    private TransferOfFundsDocument createDocumentValidForRouting() throws Exception {
        TransferOfFundsDocument doc = createDocument();

        KualiDecimal balance = new KualiDecimal("21.12");

        SourceAccountingLine sourceLine = new SourceAccountingLine();
        sourceLine.setChartOfAccountsCode("BL");
        sourceLine.setAccountNumber("1031400");
        sourceLine.setFinancialObjectCode("1663");
        sourceLine.setAmount(balance);
        sourceLine.refresh();
        List<SourceAccountingLine> sourceLines = new ArrayList<SourceAccountingLine>();
        sourceLines.add(sourceLine);

        TargetAccountingLine targetLine = new TargetAccountingLine();
        targetLine.setChartOfAccountsCode("BL");
        targetLine.setAccountNumber("1031400");
        targetLine.setFinancialObjectCode("5163");
        targetLine.setAmount(balance);
        targetLine.refresh();
        List<TargetAccountingLine> targetLines = new ArrayList<TargetAccountingLine>();
        targetLines.add(targetLine);

        doc.setSourceAccountingLines(sourceLines);
        doc.setTargetAccountingLines(targetLines);

        return doc;
    }

    private TransferOfFundsDocument createDocumentInvalidForSave() throws Exception {
        return getDocumentParameterNoDescription();
    }

    private TransferOfFundsDocument getDocumentParameterNoDescription() throws Exception {
        TransferOfFundsDocument document = DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), TransferOfFundsDocument.class);
        document.getDocumentHeader().setFinancialDocumentDescription(null);
        return document;
    }

    private TransferOfFundsDocument createDocument() throws Exception {
        return DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), TransferOfFundsDocument.class);
    }

    private TransferOfFundsDocument createDocumentWithValidObjectSubType() throws Exception {
        TransferOfFundsDocument retval = createDocument();
        retval.setSourceAccountingLines(getValidObjectSubTypeSourceLines());
        retval.setTargetAccountingLines(getValidObjectSubTypeTargetLines());
        return retval;
    }

    private TargetAccountingLine getValidObjectSubTypeTargetLine() throws Exception {
        return (TargetAccountingLine) makeObjectTypeAndSubTypeValid(LINE11.createTargetAccountingLine());
    }

    private AccountingLine makeObjectTypeAndSubTypeValid(AccountingLine line) {
        line.setFinancialObjectCode("1698"); // IN type and MT sub-type on UA chart
        line.refresh();
        return line;
    }

    private TargetAccountingLine getInvalidObjectSubTypeTargetLine() throws Exception {
        return LINE13.createTargetAccountingLine();
    }

    private List<SourceAccountingLine> getValidObjectSubTypeSourceLines() throws Exception {
        List<SourceAccountingLine> retval = new ArrayList<SourceAccountingLine>();
        retval.add(getValidObjectCodeSourceLine());
        return retval;
    }

    private List<SourceAccountingLine> getInvalidObjectSubTypeSourceLines() throws Exception {
        List<SourceAccountingLine> retval = new ArrayList<SourceAccountingLine>();
        retval.add(LINE12.createSourceAccountingLine());
        retval.add(LINE12.createSourceAccountingLine());
        return retval;
    }

    private List<TargetAccountingLine> getInvalidObjectSubTypeTargetLines() throws Exception {
        List<TargetAccountingLine> retval = new ArrayList<TargetAccountingLine>();
        retval.add(LINE11.createTargetAccountingLine());
        retval.add(getInvalidObjectSubTypeTargetLine());
        return retval;
    }

    private List<TargetAccountingLine> getValidObjectSubTypeTargetLines() throws Exception {
        List<TargetAccountingLine> retval = new ArrayList<TargetAccountingLine>();
        retval.add(LINE11.createTargetAccountingLine());
        retval.add(LINE11.createTargetAccountingLine());
        return retval;
    }

    private SourceAccountingLine getValidObjectTypeSourceLine() throws Exception {
        return LINE8.createSourceAccountingLine();
    }

    private SourceAccountingLine getInvalidObjectTypeSourceLine() throws Exception {
        SourceAccountingLine line = LINE9.createSourceAccountingLine();
        line.setFinancialObjectCode("9889");
        line.refresh();
        assertEquals("need FB obj type because it is invalid", "FB", line.getObjectCode().getFinancialObjectTypeCode());
        return line;
    }

    private SourceAccountingLine getInvalidObjectCodeSourceLine() throws Exception {
        return LINE10.createSourceAccountingLine();
    }

    private SourceAccountingLine getValidObjectCodeSourceLine() throws Exception {

        SourceAccountingLine line = LINE11.createSourceAccountingLine();

        // make sure that financial object type code is IN
        line.getObjectCode().setFinancialObjectTypeCode("IN");
        line.setObjectTypeCode("IN");
        return line;
    }

    private TransferOfFundsDocument createDocumentWithInvalidObjectSubType() throws Exception {
        TransferOfFundsDocument retval = createDocument();
        retval.setSourceAccountingLines(getInvalidObjectSubTypeSourceLines());
        retval.setTargetAccountingLines(getInvalidObjectSubTypeTargetLines());
        return retval;
    }

    private TransferOfFundsDocument createDocumentUnbalanced() throws Exception {
        TransferOfFundsDocument retval = createDocument();
        retval.addSourceAccountingLine((SourceAccountingLine) makeObjectTypeAndSubTypeValid(getValidObjectCodeSourceLine()));
        retval.addSourceAccountingLine((SourceAccountingLine) makeObjectTypeAndSubTypeValid(getValidObjectCodeSourceLine()));
        retval.addTargetAccountingLine(getValidObjectSubTypeTargetLine());
        return retval;
    }
    
    private boolean testAddAccountingLineRule_IsObjectCodeAllowed(AccountingLine accountingLine, boolean expected) throws Exception {
        Map<String, Validation> validations = SpringContext.getBeansOfType(Validation.class);
        boolean result = true;
        TransferOfFundsDocument document = createDocument();
        
        // do general validation
        AccountingLineValueAllowedValidation validation = (AccountingLineValueAllowedValidation)validations.get("AccountingDocument-IsObjectCodeAllowed-DefaultValidation"); 
        if (validation == null) throw new IllegalStateException("No object code value allowed validation");
        validation.setAccountingDocumentForValidation(document);
        validation.setAccountingLineForValidation(accountingLine);
        result = validation.validate(null);
        // do TF version validation
        validation = (AccountingLineValueAllowedValidation)validations.get("TransferOfFunds-objectCodeValueAllowedValidation"); 
        if (validation == null) throw new IllegalStateException("No TF specific object code value allowed validation");
        validation.setAccountingDocumentForValidation(document);
        validation.setAccountingLineForValidation(accountingLine);
        result = validation.validate(null);
        
        return result;
    }
    
    private void testAddAccountingLineRule_IsObjectTypeAllowed(AccountingLine accountingLine, boolean expected) throws Exception  {
        Map<String, Validation> validations = SpringContext.getBeansOfType(Validation.class);
        boolean result = true;
        TransferOfFundsDocument document = createDocument();
        AccountingLineValueAllowedValidation validation = (AccountingLineValueAllowedValidation)validations.get("AccountingDocument-IsObjectTypeAllowed-DefaultValidation"); 
        if (validation == null) throw new IllegalStateException("No object type value allowed validation");
        validation.setAccountingDocumentForValidation(document);
        validation.setAccountingLineForValidation(accountingLine);
        result = validation.validate(null);
        assertEquals(expected, result);
    }
    
    private void testAddAccountingLine_IsObjectSubTypeAllowed(AccountingLine accountingLine, boolean expected) throws Exception  {
        Map<String, Validation> validations = SpringContext.getBeansOfType(Validation.class);
        boolean result = true;
        TransferOfFundsDocument document = createDocument();
        AccountingLineValueAllowedValidation validation = (AccountingLineValueAllowedValidation)validations.get("TransferOfFunds-objectSubTypeValueAllowedValidation");
        if (validation == null) throw new IllegalStateException("No object sub type value allowed validation");
        validation.setAccountingDocumentForValidation(document);
        validation.setAccountingLineForValidation(accountingLine);
        result = validation.validate(null);
        assertEquals(expected, result);
    }
}
