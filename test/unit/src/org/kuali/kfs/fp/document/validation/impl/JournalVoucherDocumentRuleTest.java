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

import static org.kuali.kfs.sys.KFSConstants.GL_CREDIT_CODE;
import static org.kuali.kfs.sys.KFSConstants.GL_DEBIT_CODE;
import static org.kuali.kfs.sys.KualiTestAssertionUtils.assertGlobalMessageMapContains;
import static org.kuali.kfs.sys.KualiTestAssertionUtils.assertGlobalMessageMapEmpty;
import static org.kuali.kfs.sys.KualiTestAssertionUtils.assertGlobalMessageMapNotContains;
import static org.kuali.kfs.sys.KualiTestAssertionUtils.assertGlobalMessageMapSize;
import static org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleTestUtils.testAddAccountingLineRule_ProcessAddAccountingLineBusinessRules;
import static org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleTestUtils.testGenerateGeneralLedgerPendingEntriesRule_ProcessGenerateGeneralLedgerPendingEntries;
import static org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleTestUtils.testRouteDocumentRule_processRouteDocument;
import static org.kuali.kfs.sys.document.validation.impl.AccountingDocumentRuleTestUtils.testSaveDocumentRule_ProcessSaveDocument;
import static org.kuali.kfs.sys.fixture.AccountingLineFixture.EXPENSE_LINE;
import static org.kuali.kfs.sys.fixture.AccountingLineFixture.EXPENSE_LINE2;
import static org.kuali.kfs.sys.fixture.AccountingLineFixture.EXTERNAL_ENCUMBRANCE_LINE;
import static org.kuali.kfs.sys.fixture.AccountingLineFixture.LINE11;
import static org.kuali.kfs.sys.fixture.AccountingLineFixture.LINE8;
import static org.kuali.kfs.sys.fixture.AccountingLineFixture.LINE9;
import static org.kuali.kfs.sys.fixture.AccountingLineFixture.SOURCE_LINE;
import static org.kuali.kfs.sys.fixture.GeneralLedgerPendingEntryFixture.EXPECTED_JV_EXPLICIT_SOURCE_PENDING_ENTRY;
import static org.kuali.kfs.sys.fixture.GeneralLedgerPendingEntryFixture.EXPECTED_JV_EXPLICIT_SOURCE_PENDING_ENTRY_FOR_EXPENSE;
import static org.kuali.kfs.sys.fixture.UserNameFixture.dfogle;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.fp.businessobject.VoucherSourceAccountingLine;
import org.kuali.kfs.fp.document.JournalVoucherDocument;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.DocumentTestUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.businessobject.TargetAccountingLine;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.kfs.sys.document.validation.Validation;
import org.kuali.kfs.sys.document.validation.event.AddAccountingLineEvent;
import org.kuali.kfs.sys.document.validation.impl.AccountingLineValueAllowedValidation;
import org.kuali.kfs.sys.document.validation.impl.AccountingLineValuesAllowedValidationHutch;
import org.kuali.kfs.sys.service.IsDebitTestUtils;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KualiRuleService;
import org.kuali.rice.krad.util.GlobalVariables;

@ConfigureContext(session = dfogle)
public class JournalVoucherDocumentRuleTest extends KualiTestBase {
    private static final Logger LOG = Logger.getLogger(JournalVoucherDocumentRuleTest.class);

    public static final Class<JournalVoucherDocument> DOCUMENT_CLASS = JournalVoucherDocument.class;

    public void testProcessAddAccountingLineBusinessRules_irrelevantReferenceOriginCode() throws Exception {
        testProcessAddAccountingLineBusinessRules(EXPENSE_LINE2.createVoucherSourceAccountingLine(), null, null);
    }

    public void testProcessAddAccountingLineBusinessRules_emptyReferenceOriginCode() throws Exception {
        AccountingLine line = EXTERNAL_ENCUMBRANCE_LINE.createVoucherSourceAccountingLine();
        line.setEncumbranceUpdateCode(KFSConstants.ENCUMB_UPDT_REFERENCE_DOCUMENT_CD);
        line.setReferenceOriginCode("");
        testProcessAddAccountingLineBusinessRules(line, KFSPropertyConstants.REFERENCE_ORIGIN_CODE, KFSKeyConstants.ERROR_REQUIRED);
    }

    public void testProcessAddAccountingLineBusinessRules_emptyReferences() throws Exception {
        AccountingLine line = EXTERNAL_ENCUMBRANCE_LINE.createVoucherSourceAccountingLine();
        line.setEncumbranceUpdateCode(KFSConstants.ENCUMB_UPDT_REFERENCE_DOCUMENT_CD);
        line.setReferenceOriginCode("");
        line.setReferenceNumber("");
        line.setReferenceTypeCode("");
        testProcessAddAccountingLineBusinessRules(line, KFSPropertyConstants.REFERENCE_ORIGIN_CODE, KFSKeyConstants.ERROR_REQUIRED);
        assertGlobalMessageMapContains(KFSKeyConstants.ERROR_REQUIRED+"."+KFSPropertyConstants.REFERENCE_NUMBER, KFSKeyConstants.ERROR_REQUIRED);
        assertGlobalMessageMapContains(KFSKeyConstants.ERROR_REQUIRED+"."+KFSPropertyConstants.REFERENCE_TYPE_CODE, KFSKeyConstants.ERROR_REQUIRED);
    }

    public void testProcessAddAccountingLineBusinessRules_validReferences() throws Exception {
        testProcessAddAccountingLineBusinessRules(EXTERNAL_ENCUMBRANCE_LINE.createVoucherSourceAccountingLine(), null, null);
    }

    public void testProcessAddAccountingLineBusinessRules_invalidReferenceOriginCode() throws Exception {
        AccountingLine line = EXTERNAL_ENCUMBRANCE_LINE.createVoucherSourceAccountingLine();
        line.setReferenceOriginCode("42");
        testProcessAddAccountingLineBusinessRules(line, KFSPropertyConstants.REFERENCE_ORIGIN_CODE, KFSKeyConstants.ERROR_EXISTING_WITH_IDENTIFYING_ACCOUNTING_LINE);
    }

    public void testProcessAddAccountingLineBusinessRules_invalidReferenceTypeCode() throws Exception {
        AccountingLine line = EXTERNAL_ENCUMBRANCE_LINE.createVoucherSourceAccountingLine();
        line.setReferenceTypeCode("42");
        testProcessAddAccountingLineBusinessRules(line, KFSPropertyConstants.REFERENCE_TYPE_CODE, KFSKeyConstants.ERROR_DOCUMENT_ACCOUNTING_LINE_NON_ACTIVE_CURRENT_ACCOUNTING_DOCUMENT_TYPE);
        assertGlobalMessageMapNotContains(line.toString(), KFSPropertyConstants.REFERENCE_TYPE_CODE, KFSKeyConstants.ERROR_REQUIRED);
        assertGlobalMessageMapSize(line.toString(), 1);
    }

    private void testProcessAddAccountingLineBusinessRules(AccountingLine line, String expectedErrorFieldName, String expectedErrorKey) throws Exception {
        line.refresh();
        assertGlobalMessageMapEmpty();
        boolean wasValid = SpringContext.getBean(KualiRuleService.class).applyRules(new AddAccountingLineEvent(expectedErrorKey, createDocumentUnbalanced(), line));
        if (LOG.isDebugEnabled()) {
            LOG.debug(StringUtils.join(GlobalVariables.getMessageMap().getAllPropertiesWithErrors(),", ")+"; "+StringUtils.join(GlobalVariables.getMessageMap().getPropertiesWithErrors(), ", "));
        }
        if (expectedErrorFieldName == null) {
            assertGlobalMessageMapEmpty(line.toString()); // fail printing error map for debugging before failing on simple result
            assertEquals("wasValid " + line, true, wasValid);
        }
        else {
            assertGlobalMessageMapContains(line.toString(), expectedErrorKey+"."+expectedErrorFieldName, expectedErrorKey);
            assertEquals("wasValid " + line, false, wasValid);
        }
    }


    public void testIsDebit_debitCode() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), JournalVoucherDocument.class);
        AccountingLine accountingLine = (AccountingLine) accountingDocument.getSourceAccountingLineClass().newInstance();
        accountingLine.setDebitCreditCode(GL_DEBIT_CODE);

        assertTrue(IsDebitTestUtils.isDebit(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_creditCode() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), JournalVoucherDocument.class);
        AccountingLine accountingLine = (AccountingLine) accountingDocument.getSourceAccountingLineClass().newInstance();
        accountingLine.setDebitCreditCode(GL_CREDIT_CODE);

        assertFalse(IsDebitTestUtils.isDebit(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_blankValue() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), JournalVoucherDocument.class);
        AccountingLine accountingLine = (AccountingLine) accountingDocument.getSourceAccountingLineClass().newInstance();
        accountingLine.setDebitCreditCode(" ");

        assertTrue(IsDebitTestUtils.isDebit(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));

    }

    public void testIsDebit_errorCorrection_crediCode() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getErrorCorrectionDocument(SpringContext.getBean(DocumentService.class), JournalVoucherDocument.class);
        AccountingLine accountingLine = (AccountingLine) accountingDocument.getSourceAccountingLineClass().newInstance();
        accountingLine.setDebitCreditCode(GL_CREDIT_CODE);

        assertFalse(IsDebitTestUtils.isDebit(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_errorCorrection_debitCode() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getErrorCorrectionDocument(SpringContext.getBean(DocumentService.class), JournalVoucherDocument.class);
        AccountingLine accountingLine = (AccountingLine) accountingDocument.getSourceAccountingLineClass().newInstance();
        accountingLine.setDebitCreditCode(GL_DEBIT_CODE);

        assertTrue(IsDebitTestUtils.isDebit(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_errorCorrection_blankValue() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getErrorCorrectionDocument(SpringContext.getBean(DocumentService.class), JournalVoucherDocument.class);
        AccountingLine accountingLine = (AccountingLine) accountingDocument.getSourceAccountingLineClass().newInstance();
        accountingLine.setDebitCreditCode(" ");

        assertTrue(IsDebitTestUtils.isDebit(SpringContext.getBean(DataDictionaryService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));

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

    public void testAddAccountingLine_Valid() throws Exception {
        AccountingDocument doc = createDocumentWithValidObjectSubType();
        testAddAccountingLineRule_ProcessAddAccountingLineBusinessRules(doc, true);
    }

    public void testIsObjectSubTypeAllowed_ValidSubType() throws Exception {
        boolean result = true;
        JournalVoucherDocument document = buildDocument();
        AccountingLineValueAllowedValidation validation = (AccountingLineValueAllowedValidation)SpringContext.getBean(Validation.class,"AccountingDocument-IsObjectSubTypeAllowed-DefaultValidation");
        if (validation == null) {
            throw new IllegalStateException("No object sub type value allowed validation");
        }
        validation.setAccountingDocumentForValidation(document);
        validation.setAccountingLineForValidation(getValidObjectSubTypeTargetLine());
        result = validation.validate(null);
        assertEquals(true, result);
    }

    public void testProcessSaveDocument_Valid() throws Exception {
        testSaveDocumentRule_ProcessSaveDocument(buildDocument(), true);
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
        testRouteDocumentRule_processRouteDocument(buildDocument(), false);
    }

    public void testProcessRouteDocument_NoAccountingLines() throws Exception {
        testRouteDocumentRule_processRouteDocument(buildDocument(), false);
    }

    public void testProcessRouteDocument_Unbalanced() throws Exception {
        testRouteDocumentRule_processRouteDocument(createDocumentUnbalanced(), false);
    }

    public void testProcessGenerateGeneralLedgerPendingEntries_validSourceExpense() throws Exception {
        testGenerateGeneralLedgerPendingEntriesRule_ProcessGenerateGeneralLedgerPendingEntries(buildDocument(), EXPENSE_LINE.createVoucherSourceAccountingLine(), EXPECTED_JV_EXPLICIT_SOURCE_PENDING_ENTRY_FOR_EXPENSE, null);
    }

    public void testProcessGenerateGeneralLedgerPendingEntries_validSourceAsset() throws Exception {
        testGenerateGeneralLedgerPendingEntriesRule_ProcessGenerateGeneralLedgerPendingEntries(buildDocument(), getAssetSourceLine(), EXPECTED_JV_EXPLICIT_SOURCE_PENDING_ENTRY, null);
    }

    private JournalVoucherDocument createDocumentValidForRouting() throws Exception {
        return createDocumentWithValidObjectSubType();
    }

    private JournalVoucherDocument createDocumentInvalidForSave() throws Exception {
        return createDocumentInvalidDescription();
    }

    private JournalVoucherDocument buildDocument() throws Exception {
        JournalVoucherDocument retval = DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), JournalVoucherDocument.class);
        retval.setBalanceTypeCode(KFSConstants.BALANCE_TYPE_ACTUAL);
        return retval;
    }

    private JournalVoucherDocument createDocumentWithValidObjectSubType() throws Exception {
        JournalVoucherDocument retval = buildDocument();
        retval.setSourceAccountingLines(getValidObjectSubTypeSourceLines());
        return retval;
    }

    private JournalVoucherDocument createDocumentInvalidDescription() throws Exception {
        JournalVoucherDocument retval = DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), JournalVoucherDocument.class);

        retval.getDocumentHeader().setDocumentDescription(new String());
        return retval;
    }

    private TargetAccountingLine getValidObjectSubTypeTargetLine() throws Exception {
        return LINE11.createTargetAccountingLine();
    }

    private List<VoucherSourceAccountingLine> getValidObjectSubTypeSourceLines() throws Exception {
        List<VoucherSourceAccountingLine> retval = new ArrayList<VoucherSourceAccountingLine>();
        retval.add(LINE11.createVoucherSourceAccountingLine());
        return retval;
    }

    private List<TargetAccountingLine> getValidObjectSubTypeTargetLines() throws Exception {
        List<TargetAccountingLine> retval = new ArrayList<TargetAccountingLine>();
        retval.add(LINE11.createTargetAccountingLine());
        retval.add(LINE11.createTargetAccountingLine());
        return retval;
    }

    private SourceAccountingLine getValidObjectTypeSourceLine() throws Exception {
        return LINE8.createVoucherSourceAccountingLine();
    }

    private SourceAccountingLine getInvalidObjectTypeSourceLine() throws Exception {
        VoucherSourceAccountingLine retval = LINE9.createVoucherSourceAccountingLine();
        retval.setObjectTypeCode(new String());
        return retval;
    }

    private SourceAccountingLine getValidObjectCodeSourceLine() throws Exception {
        return LINE11.createVoucherSourceAccountingLine();
    }

    private SourceAccountingLine getAssetSourceLine() throws Exception {
        return SOURCE_LINE.createVoucherSourceAccountingLine();
    }

    private JournalVoucherDocument createDocumentUnbalanced() throws Exception {
        return buildDocument();
    }

    private void testAddAccountingLineRule_IsObjectTypeAllowed(AccountingLine accountingLine, boolean expected) throws Exception  {
        boolean result = true;
        JournalVoucherDocument document = buildDocument();
        AccountingLineValuesAllowedValidationHutch hutch = (AccountingLineValuesAllowedValidationHutch)SpringContext.getBean(Validation.class,"JournalVoucher-accountingLineValuesAllowedValidation");
        AccountingLineValueAllowedValidation validation = (AccountingLineValueAllowedValidation)hutch.getObjectTypeAllowedValidation();
        if (validation != null) {
            validation.setAccountingDocumentForValidation(document);
            validation.setAccountingLineForValidation(accountingLine);
            result = validation.validate(null);
        } else {
            result = true;
        }
        assertEquals(expected, result);
    }

    private boolean testAddAccountingLineRule_IsObjectCodeAllowed(AccountingLine accountingLine, boolean expected) throws Exception {
        boolean result = true;
        JournalVoucherDocument document = buildDocument();
        AccountingLineValuesAllowedValidationHutch hutch = (AccountingLineValuesAllowedValidationHutch)SpringContext.getBean(Validation.class,"JournalVoucher-accountingLineValuesAllowedValidation");
        AccountingLineValueAllowedValidation validation = (AccountingLineValueAllowedValidation)hutch.getObjectCodeAllowedValidation();
        if (validation != null) {
            validation.setAccountingDocumentForValidation(document);
            validation.setAccountingLineForValidation(accountingLine);
            result = validation.validate(null);
        } else {
            result = true;
        }

        return result;
    }
}

