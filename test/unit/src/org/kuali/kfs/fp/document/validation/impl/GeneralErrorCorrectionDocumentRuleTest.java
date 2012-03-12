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

import static org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleTestUtils.testAddAccountingLineRule_ProcessAddAccountingLineBusinessRules;
import static org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleTestUtils.testGenerateGeneralLedgerPendingEntriesRule_ProcessGenerateGeneralLedgerPendingEntries;
import static org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleTestUtils.testRouteDocumentRule_processRouteDocument;
import static org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleTestUtils.testSaveDocumentRule_ProcessSaveDocument;
import static org.kuali.kfs.sys.fixture.AccountingLineFixture.ACCRUED_INCOME_LINE;
import static org.kuali.kfs.sys.fixture.AccountingLineFixture.CASH_LINE;
import static org.kuali.kfs.sys.fixture.AccountingLineFixture.EXPENSE_GEC_LINE;
import static org.kuali.kfs.sys.fixture.AccountingLineFixture.LINE10;
import static org.kuali.kfs.sys.fixture.AccountingLineFixture.LINE16;
import static org.kuali.kfs.sys.fixture.AccountingLineFixture.LINE8;
import static org.kuali.kfs.sys.fixture.AccountingLineFixture.LOSSS_ON_RETIRE_LINE;
import static org.kuali.kfs.sys.fixture.GeneralLedgerPendingEntryFixture.EXPECTED_GEC_EXPLICIT_SOURCE_PENDING_ENTRY;
import static org.kuali.kfs.sys.fixture.GeneralLedgerPendingEntryFixture.EXPECTED_GEC_EXPLICIT_SOURCE_PENDING_ENTRY_FOR_EXPENSE;
import static org.kuali.kfs.sys.fixture.GeneralLedgerPendingEntryFixture.EXPECTED_GEC_EXPLICIT_TARGET_PENDING_ENTRY;
import static org.kuali.kfs.sys.fixture.GeneralLedgerPendingEntryFixture.EXPECTED_GEC_EXPLICIT_TARGET_PENDING_ENTRY_FOR_EXPENSE;
import static org.kuali.kfs.sys.fixture.GeneralLedgerPendingEntryFixture.EXPECTED_GEC_OFFSET_SOURCE_PENDING_ENTRY;
import static org.kuali.kfs.sys.fixture.GeneralLedgerPendingEntryFixture.EXPECTED_GEC_OFFSET_TARGET_PENDING_ENTRY;
import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;
import static org.kuali.kfs.sys.service.IsDebitTestUtils.Amount.NEGATIVE;
import static org.kuali.kfs.sys.service.IsDebitTestUtils.Amount.POSITIVE;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.fp.document.GeneralErrorCorrectionDocument;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.DocumentTestUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.businessobject.TargetAccountingLine;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.service.IsDebitTestUtils;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.service.DocumentService;

@ConfigureContext(session = khuntley)
public class GeneralErrorCorrectionDocumentRuleTest extends KualiTestBase {
    public static final Class<GeneralErrorCorrectionDocument> DOCUMENT_CLASS = GeneralErrorCorrectionDocument.class;

    public void testIsDebit_source_income_positveAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(accountingDocument, SourceAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebit(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_source_income_negativeAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(accountingDocument, SourceAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_source_income_zeroAmount() throws Exception {

        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(accountingDocument, SourceAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_source_expense_positveAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(accountingDocument, SourceAccountingLine.class, POSITIVE);

        assertFalse(IsDebitTestUtils.isDebit(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_source_expense_negativeAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(accountingDocument, SourceAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_source_expense_zeroAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(accountingDocument, SourceAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_source_asset_positveAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(accountingDocument, SourceAccountingLine.class, POSITIVE);

        assertFalse(IsDebitTestUtils.isDebit(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_source_asset_negativeAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(accountingDocument, SourceAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_source_asset_zeroAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(accountingDocument, SourceAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_source_liability_positveAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(accountingDocument, SourceAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebit(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_source_liability_negativeAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(accountingDocument, SourceAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_source_liability_zeroAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(accountingDocument, SourceAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_target_income_positveAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(accountingDocument, TargetAccountingLine.class, POSITIVE);

        assertFalse(IsDebitTestUtils.isDebit(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_target_income_negativeAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(accountingDocument, TargetAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_target_income_zeroAmount() throws Exception {

        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(accountingDocument, TargetAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_target_expense_positveAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(accountingDocument, TargetAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebit(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_target_expense_negativeAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(accountingDocument, TargetAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_target_expense_zeroAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(accountingDocument, TargetAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_target_asset_positveAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(accountingDocument, TargetAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebit(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_target_asset_negativeAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(accountingDocument, TargetAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_target_asset_zeroAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(accountingDocument, TargetAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_target_liability_positveAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(accountingDocument, TargetAccountingLine.class, POSITIVE);

        assertFalse(IsDebitTestUtils.isDebit(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_target_liability_negativeAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(accountingDocument, TargetAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_target_liability_zeroAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(accountingDocument, TargetAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_errorCorrection_source_income_negativeAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getErrorCorrectionDocument(SpringContext.getBean(DocumentService.class), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(accountingDocument, SourceAccountingLine.class, NEGATIVE);

        assertFalse(IsDebitTestUtils.isDebit(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_errorCorrection_source_expense_negativeAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getErrorCorrectionDocument(SpringContext.getBean(DocumentService.class), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(accountingDocument, SourceAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebit(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_errorCorrection_source_asset_negativeAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getErrorCorrectionDocument(SpringContext.getBean(DocumentService.class), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(accountingDocument, SourceAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebit(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_errorCorrection_source_liability_negativeAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getErrorCorrectionDocument(SpringContext.getBean(DocumentService.class), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(accountingDocument, SourceAccountingLine.class, NEGATIVE);

        assertFalse(IsDebitTestUtils.isDebit(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_errorCorrection_target_income_negativeAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getErrorCorrectionDocument(SpringContext.getBean(DocumentService.class), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(accountingDocument, TargetAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebit(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_errorCorrection_target_expense_negativeAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getErrorCorrectionDocument(SpringContext.getBean(DocumentService.class), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(accountingDocument, TargetAccountingLine.class, NEGATIVE);

        assertFalse(IsDebitTestUtils.isDebit(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_errorCorrection_target_asset_negativeAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getErrorCorrectionDocument(SpringContext.getBean(DocumentService.class), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(accountingDocument, TargetAccountingLine.class, NEGATIVE);

        assertFalse(IsDebitTestUtils.isDebit(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_errorCorrection_target_liability_negativeAmount() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getErrorCorrectionDocument(SpringContext.getBean(DocumentService.class), GeneralErrorCorrectionDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(accountingDocument, TargetAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebit(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

  //  public void testIsObjectTypeAllowed_InvalidObjectType() throws Exception {
//        GeneralErrorCorrectionObjectTypeValidation validation = new GeneralErrorCorrectionObjectTypeValidation();
//        AccountingDocument accountingDocument = IsDebitTestUtils.getErrorCorrectionDocument(SpringContext.getBean(DocumentService.class), GeneralErrorCorrectionDocument.class);
//        validation.setAccountingLineForValidation(getInvalidObjectTypeSourceLine());
//        validation.setParameterService(SpringContext.getBean(ParameterService.class));
//        assertEquals(false, validation.validate(new AddAccountingLineEvent(null, accountingDocument, getInvalidObjectTypeSourceLine())));
//        testAddAccountingLineRule_IsObjectTypeAllowed(DOCUMENT_CLASS,  getInvalidObjectTypeSourceLine(), false);
 //       ObjectCode code = getInvalidObjectCodeSourceLine().getObjectCode();
  //      GeneralErrorCorrectionDocumentRule rule = new GeneralErrorCorrectionDocumentRule();
  //     assertEquals(false,  rule.isObjectTypeAndObjectSubTypeAllowed(code));
  //  }

//    public void testIsObjectTypeAllowed_Valid() throws Exception {
//        GeneralErrorCorrectionObjectTypeValidation validation = new GeneralErrorCorrectionObjectTypeValidation();
//        AccountingDocument accountingDocument = IsDebitTestUtils.getErrorCorrectionDocument(SpringContext.getBean(DocumentService.class), GeneralErrorCorrectionDocument.class);
//        validation.setAccountingLineForValidation(getValidObjectTypeSourceLine());
//        validation.setParameterService(SpringContext.getBean(ParameterService.class));
//        assertEquals(true, validation.validate(new AddAccountingLineEvent(null, accountingDocument, getValidObjectTypeSourceLine())));
//    }
//
//    public void testIsObjectCodeAllowed_Valid() throws Exception {
//        testAddAccountingLineRule_IsObjectCodeAllowed(DOCUMENT_CLASS, getValidObjectCodeSourceLine(), true);
//    }
//
//    public void testIsObjectCodeAllowed_InvalidObjectCode() throws Exception {
//        GeneralErrorCorrectionObjectTypeValidation validation = new GeneralErrorCorrectionObjectTypeValidation();
//        AccountingDocument accountingDocument = IsDebitTestUtils.getErrorCorrectionDocument(SpringContext.getBean(DocumentService.class), GeneralErrorCorrectionDocument.class);
//        validation.setAccountingLineForValidation(getInvalidObjectTypeSourceLine());
//        validation.setParameterService(SpringContext.getBean(ParameterService.class));
//        assertEquals(false, validation.validate(new AddAccountingLineEvent(null, accountingDocument, getInvalidObjectCodeSourceLine())));
//      //  testAddAccountingLineRule_IsObjectCodeAllowed(DOCUMENT_CLASS, getInvalidObjectCodeSourceLine(), false);
//    }
//
//    public void testAddAccountingLine_InvalidObjectSubType() throws Exception {
//        AccountingDocument doc = createDocumentWithInvalidObjectSubType();
//        testAddAccountingLineRule_ProcessAddAccountingLineBusinessRules(doc, false);
//    }

    public void testAddAccountingLine_Valid() throws Exception {
        AccountingDocument doc = createDocumentWithValidObjectSubType();
        testAddAccountingLineRule_ProcessAddAccountingLineBusinessRules(doc, true);
    }

//    public void testIsObjectSubTypeAllowed_InvalidSubType() throws Exception {
//        GeneralErrorCorrectionObjectTypeValidation validation = new GeneralErrorCorrectionObjectTypeValidation();
//        AccountingDocument accountingDocument = IsDebitTestUtils.getErrorCorrectionDocument(SpringContext.getBean(DocumentService.class), GeneralErrorCorrectionDocument.class);
//        validation.setAccountingLineForValidation(getInvalidObjectTypeSourceLine());
//        validation.setParameterService(SpringContext.getBean(ParameterService.class));
//        assertEquals(false, validation.validate(new AddAccountingLineEvent(null, accountingDocument, getInvalidObjectSubTypeTargetLine())));
//        //testAddAccountingLine_IsObjectSubTypeAllowed(DOCUMENT_CLASS, getInvalidObjectSubTypeTargetLine(), false);
//    }

   // public void testIsObjectSubTypeAllowed_ValidSubType() throws Exception {
    //    testAddAccountingLine_IsObjectSubTypeAllowed(DOCUMENT_CLASS, getValidObjectSubTypeTargetLine(), true);
   // }

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
        testGenerateGeneralLedgerPendingEntriesRule_ProcessGenerateGeneralLedgerPendingEntries(createDocument(), getExpenseTargetLine(), EXPECTED_GEC_EXPLICIT_TARGET_PENDING_ENTRY_FOR_EXPENSE, EXPECTED_GEC_OFFSET_TARGET_PENDING_ENTRY);
    }

    public void testProcessGenerateGeneralLedgerPendingEntries_validSourceExpense() throws Exception {

        testGenerateGeneralLedgerPendingEntriesRule_ProcessGenerateGeneralLedgerPendingEntries(createDocument(), getExpenseSourceLine(), EXPECTED_GEC_EXPLICIT_SOURCE_PENDING_ENTRY_FOR_EXPENSE, EXPECTED_GEC_OFFSET_SOURCE_PENDING_ENTRY);
    }

    public void testProcessGenerateGeneralLedgerPendingEntries_validSourceAsset() throws Exception {
        testGenerateGeneralLedgerPendingEntriesRule_ProcessGenerateGeneralLedgerPendingEntries(createDocument(), getAssetSourceLine(), EXPECTED_GEC_EXPLICIT_SOURCE_PENDING_ENTRY, EXPECTED_GEC_OFFSET_SOURCE_PENDING_ENTRY);
    }

    public void testProcessGenerateGeneralLedgerPendingEntries_validTargetAsset() throws Exception {
        testGenerateGeneralLedgerPendingEntriesRule_ProcessGenerateGeneralLedgerPendingEntries(createDocument(), getAssetTargetLine(), EXPECTED_GEC_EXPLICIT_TARGET_PENDING_ENTRY, EXPECTED_GEC_OFFSET_TARGET_PENDING_ENTRY);
    }

    private GeneralErrorCorrectionDocument createDocument() throws Exception {
        return DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), GeneralErrorCorrectionDocument.class);
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
        GeneralErrorCorrectionDocument document = DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), GeneralErrorCorrectionDocument.class);
        document.getDocumentHeader().setDocumentDescription(null);
        return document;
    }

    private SourceAccountingLine getExpenseSourceLine() throws Exception {
        return EXPENSE_GEC_LINE.createAccountingLine(SourceAccountingLine.class, KFSConstants.GL_DEBIT_CODE);
    }

    private TargetAccountingLine getExpenseTargetLine() throws Exception {
        return EXPENSE_GEC_LINE.createAccountingLine(TargetAccountingLine.class, KFSConstants.GL_CREDIT_CODE);
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
        return ACCRUED_INCOME_LINE.createAccountingLine(SourceAccountingLine.class, KFSConstants.GL_DEBIT_CODE);
    }

    private SourceAccountingLine getAssetSourceLine() throws Exception {
        return ACCRUED_INCOME_LINE.createAccountingLine(SourceAccountingLine.class, KFSConstants.GL_DEBIT_CODE);
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
        return ACCRUED_INCOME_LINE.createAccountingLine(SourceAccountingLine.class, KFSConstants.GL_DEBIT_CODE);
    }

    private TargetAccountingLine getAccruedIncomeTargetLineParameter() throws Exception {
        return ACCRUED_INCOME_LINE.createAccountingLine(TargetAccountingLine.class, KFSConstants.GL_CREDIT_CODE);
    }

  
}
