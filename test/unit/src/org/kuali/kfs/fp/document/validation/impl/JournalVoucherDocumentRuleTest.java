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

import static org.kuali.Constants.GL_CREDIT_CODE;
import static org.kuali.Constants.GL_DEBIT_CODE;
import static org.kuali.core.util.SpringServiceLocator.getDataDictionaryService;
import static org.kuali.core.util.SpringServiceLocator.getDocumentService;
import static org.kuali.core.util.SpringServiceLocator.getDocumentTypeService;
import static org.kuali.module.financial.rules.TransactionalDocumentRuleTestUtils.getBusinessRule;
import static org.kuali.module.financial.rules.TransactionalDocumentRuleTestUtils.testAddAccountingLineRule_IsObjectCodeAllowed;
import static org.kuali.module.financial.rules.TransactionalDocumentRuleTestUtils.testAddAccountingLineRule_IsObjectTypeAllowed;
import static org.kuali.module.financial.rules.TransactionalDocumentRuleTestUtils.testAddAccountingLineRule_ProcessAddAccountingLineBusinessRules;
import static org.kuali.module.financial.rules.TransactionalDocumentRuleTestUtils.testAddAccountingLine_IsObjectSubTypeAllowed;
import static org.kuali.module.financial.rules.TransactionalDocumentRuleTestUtils.testGenerateGeneralLedgerPendingEntriesRule_ProcessGenerateGeneralLedgerPendingEntries;
import static org.kuali.module.financial.rules.TransactionalDocumentRuleTestUtils.testRouteDocumentRule_processRouteDocument;
import static org.kuali.module.financial.rules.TransactionalDocumentRuleTestUtils.testSaveDocumentRule_ProcessSaveDocument;
import static org.kuali.test.fixtures.AccountingLineFixture.EXPENSE_LINE;
import static org.kuali.test.fixtures.AccountingLineFixture.EXPENSE_LINE2;
import static org.kuali.test.fixtures.AccountingLineFixture.EXTERNAL_ENCUMBRANCE_LINE;
import static org.kuali.test.fixtures.AccountingLineFixture.LINE11;
import static org.kuali.test.fixtures.AccountingLineFixture.LINE8;
import static org.kuali.test.fixtures.AccountingLineFixture.LINE9;
import static org.kuali.test.fixtures.AccountingLineFixture.SOURCE_LINE;
import static org.kuali.test.fixtures.GeneralLedgerPendingEntryFixture.EXPECTED_JV_EXPLICIT_SOURCE_PENDING_ENTRY;
import static org.kuali.test.fixtures.GeneralLedgerPendingEntryFixture.EXPECTED_JV_EXPLICIT_SOURCE_PENDING_ENTRY_FOR_EXPENSE;
import static org.kuali.test.fixtures.GeneralLedgerPendingEntryFixture.EXPECTED_OFFSET_SOURCE_PENDING_ENTRY;
import static org.kuali.test.fixtures.UserNameFixture.DFOGLE;
import static org.kuali.test.util.KualiTestAssertionUtils.assertGlobalErrorMapContains;
import static org.kuali.test.util.KualiTestAssertionUtils.assertGlobalErrorMapEmpty;
import static org.kuali.test.util.KualiTestAssertionUtils.assertGlobalErrorMapNotContains;
import static org.kuali.test.util.KualiTestAssertionUtils.assertGlobalErrorMapSize;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.PropertyConstants;
import org.kuali.core.bo.AccountingLine;
import org.kuali.core.bo.SourceAccountingLine;
import org.kuali.core.bo.TargetAccountingLine;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.rule.AddAccountingLineRule;
import org.kuali.core.util.GlobalVariables;
import org.kuali.module.financial.document.JournalVoucherDocument;
import org.kuali.module.gl.bo.GeneralLedgerPendingEntry;
import org.kuali.test.DocumentTestUtils;
import org.kuali.test.KualiTestBase;
import org.kuali.test.WithTestSpringContext;

@WithTestSpringContext(session = DFOGLE)
public class JournalVoucherDocumentRuleTest extends KualiTestBase {

    public static final Class<JournalVoucherDocument> DOCUMENT_CLASS = JournalVoucherDocument.class;

    public void testProcessAddAccountingLineBusinessRules_irrelevantReferenceOriginCode() throws Exception {
        testProcessAddAccountingLineBusinessRules(EXPENSE_LINE2.createSourceAccountingLine(), null, null);
    }

    public void testProcessAddAccountingLineBusinessRules_emptyReferenceOriginCode() throws Exception {
        AccountingLine line = EXTERNAL_ENCUMBRANCE_LINE.createSourceAccountingLine();
        line.setReferenceOriginCode("");
        testProcessAddAccountingLineBusinessRules(line, PropertyConstants.REFERENCE_ORIGIN_CODE, KeyConstants.ERROR_REQUIRED);
    }

    public void testProcessAddAccountingLineBusinessRules_emptyReferences() throws Exception {
        AccountingLine line = EXTERNAL_ENCUMBRANCE_LINE.createSourceAccountingLine();
        line.setReferenceOriginCode("");
        line.setReferenceNumber("");
        line.setReferenceTypeCode("");
        testProcessAddAccountingLineBusinessRules(line, PropertyConstants.REFERENCE_ORIGIN_CODE, KeyConstants.ERROR_REQUIRED);
        assertGlobalErrorMapContains(PropertyConstants.REFERENCE_NUMBER, KeyConstants.ERROR_REQUIRED);
        assertGlobalErrorMapContains(PropertyConstants.REFERENCE_TYPE_CODE, KeyConstants.ERROR_REQUIRED);
    }

    public void testProcessAddAccountingLineBusinessRules_validReferences() throws Exception {
        testProcessAddAccountingLineBusinessRules(EXTERNAL_ENCUMBRANCE_LINE.createSourceAccountingLine(), null, null);
    }

    public void testProcessAddAccountingLineBusinessRules_invalidReferenceOriginCode() throws Exception {
        AccountingLine line = EXTERNAL_ENCUMBRANCE_LINE.createSourceAccountingLine();
        line.setReferenceOriginCode("42");
        testProcessAddAccountingLineBusinessRules(line, PropertyConstants.REFERENCE_ORIGIN_CODE, KeyConstants.ERROR_EXISTENCE);
    }

    public void testProcessAddAccountingLineBusinessRules_invalidReferenceTypeCode() throws Exception {
        AccountingLine line = EXTERNAL_ENCUMBRANCE_LINE.createSourceAccountingLine();
        line.setReferenceTypeCode("42");
        testProcessAddAccountingLineBusinessRules(line, PropertyConstants.REFERENCE_TYPE_CODE, KeyConstants.ERROR_EXISTENCE);
        assertGlobalErrorMapNotContains(line.toString(), PropertyConstants.REFERENCE_TYPE_CODE, KeyConstants.ERROR_REQUIRED);
        assertGlobalErrorMapSize(line.toString(), 1);
    }

    private void testProcessAddAccountingLineBusinessRules(AccountingLine line, String expectedErrorFieldName, String expectedErrorKey) throws Exception {
        line.refresh();
        assertGlobalErrorMapEmpty();
        boolean wasValid = getBusinessRule(DOCUMENT_CLASS, AddAccountingLineRule.class).processAddAccountingLineBusinessRules(createDocumentUnbalanced(), line);
        if (expectedErrorFieldName == null) {
            assertGlobalErrorMapEmpty(line.toString()); // fail printing error map for debugging before failing on simple result
            assertEquals("wasValid " + line, true, wasValid);
        }
        else {
            assertGlobalErrorMapContains(line.toString(), expectedErrorFieldName, expectedErrorKey);
            assertEquals("wasValid " + line, false, wasValid);
        }
    }


    public void testProcessCustomRouteDocumentBusinessRules_accountingLines_notMatching_budgetYear() throws Exception {
        JournalVoucherDocument document = createDocumentValidForRouting();
        int budgetYear = 1990;
        for (ListIterator i = document.getSourceAccountingLines().listIterator(); i.hasNext();) {
            AccountingLine line = (AccountingLine) i.next();
            line.setBudgetYear(Integer.toString(budgetYear + i.nextIndex()));
            i.set(line);
        }
        for (ListIterator i = document.getTargetAccountingLines().listIterator(); i.hasNext();) {
            AccountingLine line = (AccountingLine) i.next();
            line.setBudgetYear(Integer.toString(budgetYear + 2 + i.nextIndex()));
            i.set(line);
        }

        JournalVoucherDocumentRule rule = new JournalVoucherDocumentRule();
        boolean failedAsExpected = !rule.processCustomRouteDocumentBusinessRules(document);

        assertTrue(failedAsExpected);
        assertTrue(GlobalVariables.getErrorMap().containsMessageKey(KeyConstants.ERROR_ACCOUNTING_LINES_DIFFERENT_BUDGET_YEAR));
    }

    public void testProcessCustomRouteDocumentBusinessRules_accountingLines_matching_budgetYear() throws Exception {
        JournalVoucherDocument document = createDocumentValidForRouting();
        int budgetYear = 1990;
        for (ListIterator i = document.getSourceAccountingLines().listIterator(); i.hasNext();) {
            AccountingLine line = (AccountingLine) i.next();
            line.setBudgetYear(Integer.toString(budgetYear));
            i.set(line);
        }
        for (ListIterator i = document.getTargetAccountingLines().listIterator(); i.hasNext();) {
            AccountingLine line = (AccountingLine) i.next();
            line.setBudgetYear(Integer.toString(budgetYear));
            i.set(line);
        }

        JournalVoucherDocumentRule rule = new JournalVoucherDocumentRule();
        boolean passedAsExpected = rule.processCustomRouteDocumentBusinessRules(document);

        assertTrue(passedAsExpected);
        assertFalse(GlobalVariables.getErrorMap().containsMessageKey(KeyConstants.ERROR_ACCOUNTING_LINES_DIFFERENT_BUDGET_YEAR));
    }

    public void testIsDebit_debitCode() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), JournalVoucherDocument.class);
        AccountingLine accountingLine = (AccountingLine) transactionalDocument.getSourceAccountingLineClass().newInstance();
        accountingLine.setDebitCreditCode(GL_DEBIT_CODE);

        assertTrue(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    public void testIsDebit_creditCode() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), JournalVoucherDocument.class);
        AccountingLine accountingLine = (AccountingLine) transactionalDocument.getSourceAccountingLineClass().newInstance();
        accountingLine.setDebitCreditCode(GL_CREDIT_CODE);

        assertFalse(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    public void testIsDebit_blankValue() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), JournalVoucherDocument.class);
        AccountingLine accountingLine = (AccountingLine) transactionalDocument.getSourceAccountingLineClass().newInstance();
        accountingLine.setDebitCreditCode(" ");

        assertTrue(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));

    }

    public void testIsDebit_errorCorrection_crediCode() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), JournalVoucherDocument.class);
        AccountingLine accountingLine = (AccountingLine) transactionalDocument.getSourceAccountingLineClass().newInstance();
        accountingLine.setDebitCreditCode(GL_CREDIT_CODE);

        assertFalse(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    public void testIsDebit_errorCorrection_debitCode() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), JournalVoucherDocument.class);
        AccountingLine accountingLine = (AccountingLine) transactionalDocument.getSourceAccountingLineClass().newInstance();
        accountingLine.setDebitCreditCode(GL_DEBIT_CODE);

        assertTrue(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    public void testIsDebit_errorCorrection_blankValue() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), JournalVoucherDocument.class);
        AccountingLine accountingLine = (AccountingLine) transactionalDocument.getSourceAccountingLineClass().newInstance();
        accountingLine.setDebitCreditCode(" ");

        assertTrue(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));

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

    public void testAddAccountingLine_Valid() throws Exception {
        TransactionalDocument doc = createDocumentWithValidObjectSubType();
        testAddAccountingLineRule_ProcessAddAccountingLineBusinessRules(doc, true);
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

    public void testProcessGenerateGeneralLedgerPendingEntries_validSourceExpense() throws Exception {

        testGenerateGeneralLedgerPendingEntriesRule_ProcessGenerateGeneralLedgerPendingEntries(createDocument5(), EXPENSE_LINE.createSourceAccountingLine(), getExpectedExplicitSourcePendingEntryForExpense(), getExpectedOffsetSourcePendingEntry(), 1);
    }

    public void testProcessGenerateGeneralLedgerPendingEntries_validSourceAsset() throws Exception {
        testGenerateGeneralLedgerPendingEntriesRule_ProcessGenerateGeneralLedgerPendingEntries(createDocument5(), getAssetSourceLine(), getExpectedExplicitSourcePendingEntry(), getExpectedOffsetSourcePendingEntry(), 1);
    }

    private JournalVoucherDocument createDocument() throws Exception {
        return buildDocument();
    }

    private JournalVoucherDocument createDocumentValidForRouting() throws Exception {
        return createDocumentWithValidObjectSubType();
    }

    private JournalVoucherDocument createDocumentInvalidForSave() throws Exception {
        return createDocumentInvalidDescription();
    }

    private JournalVoucherDocument buildDocument() throws Exception {
        JournalVoucherDocument retval = DocumentTestUtils.createDocument(getDocumentService(), JournalVoucherDocument.class);
        retval.setBalanceTypeCode(Constants.BALANCE_TYPE_ACTUAL);
        return retval;
    }

    private JournalVoucherDocument createDocumentWithValidObjectSubType() throws Exception {
        JournalVoucherDocument retval = createDocument();
        retval.setSourceAccountingLines(getValidObjectSubTypeSourceLines());
        retval.setTargetAccountingLines(getValidObjectSubTypeTargetLines());
        return retval;
    }

    private JournalVoucherDocument createDocumentInvalidDescription() throws Exception {
        JournalVoucherDocument retval = DocumentTestUtils.createDocument(getDocumentService(), JournalVoucherDocument.class);

        retval.getDocumentHeader().setFinancialDocumentDescription(new String());
        return retval;
    }

    private TargetAccountingLine getValidObjectSubTypeTargetLine() throws Exception {
        return LINE11.createTargetAccountingLine();
    }

    private List<SourceAccountingLine> getValidObjectSubTypeSourceLines() throws Exception {
        List<SourceAccountingLine> retval = new ArrayList<SourceAccountingLine>();
        retval.add(LINE11.createSourceAccountingLine());
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
        SourceAccountingLine retval = LINE9.createSourceAccountingLine();
        retval.setObjectTypeCode(new String());
        return retval;
    }

    private SourceAccountingLine getValidObjectCodeSourceLine() throws Exception {
        return LINE11.createSourceAccountingLine();
    }

    private SourceAccountingLine getAssetSourceLine() throws Exception {
        return SOURCE_LINE.createSourceAccountingLine();
    }

    private TransactionalDocument createDocument5() throws Exception {
        return buildDocument();
    }

    private JournalVoucherDocument createDocumentUnbalanced() throws Exception {
        JournalVoucherDocument retval = createDocument();
        return retval;
    }

    private GeneralLedgerPendingEntry getExpectedExplicitSourcePendingEntry() {
        return EXPECTED_JV_EXPLICIT_SOURCE_PENDING_ENTRY.createGeneralLedgerPendingEntry();
    }


    private GeneralLedgerPendingEntry getExpectedExplicitSourcePendingEntryForExpense() {
        return EXPECTED_JV_EXPLICIT_SOURCE_PENDING_ENTRY_FOR_EXPENSE.createGeneralLedgerPendingEntry();
    }

    private GeneralLedgerPendingEntry getExpectedOffsetSourcePendingEntry() {
        return EXPECTED_OFFSET_SOURCE_PENDING_ENTRY.createGeneralLedgerPendingEntry();
    }
}
