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
import static org.kuali.module.financial.rules.TransactionalDocumentRuleTestUtils.testAddAccountingLineRule_IsObjectCodeAllowed;
import static org.kuali.module.financial.rules.TransactionalDocumentRuleTestUtils.testAddAccountingLineRule_IsObjectTypeAllowed;
import static org.kuali.module.financial.rules.TransactionalDocumentRuleTestUtils.testAddAccountingLineRule_ProcessAddAccountingLineBusinessRules;
import static org.kuali.module.financial.rules.TransactionalDocumentRuleTestUtils.testAddAccountingLine_IsObjectSubTypeAllowed;
import static org.kuali.module.financial.rules.TransactionalDocumentRuleTestUtils.testGenerateGeneralLedgerPendingEntriesRule_ProcessGenerateGeneralLedgerPendingEntries;
import static org.kuali.module.financial.rules.TransactionalDocumentRuleTestUtils.testRouteDocumentRule_processRouteDocument;
import static org.kuali.module.financial.rules.TransactionalDocumentRuleTestUtils.testSaveDocumentRule_ProcessSaveDocument;
import static org.kuali.test.fixtures.AccountingLineFixture.ACCRUED_INCOME_LINE;
import static org.kuali.test.fixtures.AccountingLineFixture.ACCRUED_SICK_PAY_LINE;
import static org.kuali.test.fixtures.AccountingLineFixture.EXPENSE_GEC_LINE;
import static org.kuali.test.fixtures.AccountingLineFixture.EXPENSE_LINE;
import static org.kuali.test.fixtures.AccountingLineFixture.FUND_BALANCE_LINE;
import static org.kuali.test.fixtures.AccountingLineFixture.LINE15;
import static org.kuali.test.fixtures.AccountingLineFixture.LINE8;
import static org.kuali.test.fixtures.UserNameFixture.KHUNTLEY;

import java.util.ArrayList;
import java.util.List;

import org.kuali.Constants;
import org.kuali.core.bo.AccountingLine;
import org.kuali.core.bo.SourceAccountingLine;
import org.kuali.core.bo.TargetAccountingLine;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.module.financial.document.AuxiliaryVoucherDocument;
import org.kuali.module.gl.bo.GeneralLedgerPendingEntry;
import org.kuali.test.DocumentTestUtils;
import org.kuali.test.KualiTestBase;
import org.kuali.test.WithTestSpringContext;
import org.kuali.test.fixtures.AccountingLineFixture;
import org.kuali.test.fixtures.GeneralLedgerPendingEntryFixture;

@WithTestSpringContext(session = KHUNTLEY)
public class AuxiliaryVoucherDocumentRuleTest extends KualiTestBase {

    public static final Class<AuxiliaryVoucherDocument> DOCUMENT_CLASS = AuxiliaryVoucherDocument.class;

    public void testIsDebit_debitCode() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), AuxiliaryVoucherDocument.class);
        AccountingLine accountingLine = (AccountingLine) transactionalDocument.getSourceAccountingLineClass().newInstance();
        accountingLine.setDebitCreditCode(GL_DEBIT_CODE);

        assertTrue(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    public void testIsDebit_creditCode() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), AuxiliaryVoucherDocument.class);
        AccountingLine accountingLine = (AccountingLine) transactionalDocument.getSourceAccountingLineClass().newInstance();
        accountingLine.setDebitCreditCode(GL_CREDIT_CODE);

        assertFalse(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    public void testIsDebit_blankValue() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), AuxiliaryVoucherDocument.class);
        AccountingLine accountingLine = (AccountingLine) transactionalDocument.getSourceAccountingLineClass().newInstance();
        accountingLine.setDebitCreditCode(" ");

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }


    public void testIsDebit_errorCorrection_debitCode() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), AuxiliaryVoucherDocument.class);
        AccountingLine accountingLine = (AccountingLine) transactionalDocument.getSourceAccountingLineClass().newInstance();
        accountingLine.setDebitCreditCode(GL_DEBIT_CODE);

        assertTrue(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    public void testIsDebit_errorCorrection_creditCode() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), AuxiliaryVoucherDocument.class);
        AccountingLine accountingLine = (AccountingLine) transactionalDocument.getSourceAccountingLineClass().newInstance();
        accountingLine.setDebitCreditCode(GL_CREDIT_CODE);

        assertFalse(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    public void testIsDebit_errorCorrection_blankValue() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), AuxiliaryVoucherDocument.class);
        AccountingLine accountingLine = (AccountingLine) transactionalDocument.getSourceAccountingLineClass().newInstance();
        accountingLine.setDebitCreditCode(" ");

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

    public void testAddAccountingLine_InvalidObjectSubType() throws Exception {
        TransactionalDocument doc = createDocumentWithInvalidObjectSubType();
        testAddAccountingLineRule_ProcessAddAccountingLineBusinessRules(doc, false);
    }

    public void testAddAccountingLine_Valid() throws Exception {
        TransactionalDocument doc = createDocumentWithValidObjectSubType();
        testAddAccountingLineRule_ProcessAddAccountingLineBusinessRules(doc, true);
    }

    public void testIsObjectSubTypeAllowed_InvalidSubType() throws Exception {
        testAddAccountingLine_IsObjectSubTypeAllowed(DOCUMENT_CLASS, AccountingLineFixture.LINE17.createSourceAccountingLine(), false);
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
        testGenerateGeneralLedgerPendingEntriesRule_ProcessGenerateGeneralLedgerPendingEntries(createDocument5(), getExpenseTargetLine(), getExpectedExplicitTargetPendingEntryForExpense(), getExpectedOffsetTargetPendingEntry(), 1);
    }

    public void testProcessGenerateGeneralLedgerPendingEntries_validSourceExpense() throws Exception {

        testGenerateGeneralLedgerPendingEntriesRule_ProcessGenerateGeneralLedgerPendingEntries(createDocument5(), getExpenseSourceLine(), getExpectedExplicitSourcePendingEntryForExpense(), getExpectedOffsetSourcePendingEntry(), 1);
    }

    public void testProcessGenerateGeneralLedgerPendingEntries_validSourceAsset() throws Exception {
        testGenerateGeneralLedgerPendingEntriesRule_ProcessGenerateGeneralLedgerPendingEntries(createDocument5(), getAssetSourceLine(), getExpectedExplicitSourcePendingEntry(), getExpectedOffsetSourcePendingEntry(), 1);
    }

    public void testProcessGenerateGeneralLedgerPendingEntries_validTargetAsset() throws Exception {
        testGenerateGeneralLedgerPendingEntriesRule_ProcessGenerateGeneralLedgerPendingEntries(createDocument5(), getAssetTargetLine(), getExpectedExplicitTargetPendingEntry(), getExpectedOffsetTargetPendingEntryForTargetAsset(), 1);
    }

    private AuxiliaryVoucherDocument createDocument() throws Exception {
        // AV document has a restriction on accounting period cannot be more than 2 periods behind current
        return DocumentTestUtils.createDocument(getDocumentService(), AuxiliaryVoucherDocument.class);

    }

    private AuxiliaryVoucherDocument createDocumentValidForRouting() throws Exception {
        return createDocumentWithValidObjectSubType();
    }

    private AuxiliaryVoucherDocument createDocumentInvalidForSave() throws Exception {
        return createDocumentInvalidDescription();
    }

    private AuxiliaryVoucherDocument createDocumentInvalidDescription() throws Exception {
        AuxiliaryVoucherDocument document = DocumentTestUtils.createDocument(getDocumentService(), AuxiliaryVoucherDocument.class);

        document.getDocumentHeader().setFinancialDocumentDescription(null);
        return document;
    }

    private AuxiliaryVoucherDocument createDocumentWithValidObjectSubType() throws Exception {
        AuxiliaryVoucherDocument retval = createDocument();
        retval.setSourceAccountingLines(getValidObjectSubTypeSourceLines());
        return retval;
    }

    private SourceAccountingLine getExpenseSourceLine() throws Exception {
        return EXPENSE_GEC_LINE.createAccountingLine(SourceAccountingLine.class, Constants.GL_DEBIT_CODE);
    }

    private TargetAccountingLine getExpenseTargetLine() throws Exception {
        return EXPENSE_LINE.createTargetAccountingLine();
    }

    private GeneralLedgerPendingEntry getExpectedOffsetTargetPendingEntryForTargetAsset() {
        return getExpectedOffsetTargetPendingEntry();
    }

    private TargetAccountingLine getAssetTargetLine() throws Exception {
        return ACCRUED_INCOME_LINE.createAccountingLine(TargetAccountingLine.class, Constants.GL_DEBIT_CODE);
    }

    private TargetAccountingLine getValidObjectSubTypeTargetLine() throws Exception {
        return new TargetAccountingLine();
    }

    private List<SourceAccountingLine> getValidObjectSubTypeSourceLines() throws Exception {
        List<SourceAccountingLine> retval = new ArrayList<SourceAccountingLine>();
        retval.add(LINE15.createAccountingLine(SourceAccountingLine.class, Constants.GL_CREDIT_CODE));
        retval.add(LINE15.createAccountingLine(SourceAccountingLine.class, Constants.GL_DEBIT_CODE));
        return retval;
    }

    private List<SourceAccountingLine> getInvalidObjectSubTypeSourceLines() throws Exception {
        List<SourceAccountingLine> retval = new ArrayList<SourceAccountingLine>();
        retval.add(getAccruedSickPaySourceLineParameter());
        retval.add(getAccruedSickPaySourceLineParameter());
        return retval;
    }


    private SourceAccountingLine getValidObjectTypeSourceLine() throws Exception {
        return LINE8.createSourceAccountingLine();
    }

    private SourceAccountingLine getInvalidObjectTypeSourceLine() throws Exception {
        return FUND_BALANCE_LINE.createSourceAccountingLine();
    }

    private SourceAccountingLine getValidObjectCodeSourceLine() throws Exception {
        return getAccruedIncomeSourceLineParameter();
    }

    private SourceAccountingLine getAssetSourceLine() throws Exception {
        return getAccruedIncomeSourceLineParameter();
    }

    private TransactionalDocument createDocument5() throws Exception { // AV document has a restriction on accounting
        return DocumentTestUtils.createDocument(getDocumentService(), AuxiliaryVoucherDocument.class);

    }

    private AuxiliaryVoucherDocument createDocumentWithInvalidObjectSubType() throws Exception {
        AuxiliaryVoucherDocument retval = createDocument();
        retval.setSourceAccountingLines(getInvalidObjectSubTypeSourceLines());
        return retval;
    }

    private AuxiliaryVoucherDocument createDocumentUnbalanced() throws Exception {
        AuxiliaryVoucherDocument retval = createDocument();
        retval.setSourceAccountingLines(getInvalidObjectSubTypeSourceLines());
        return retval;
    }

    private SourceAccountingLine getAccruedIncomeSourceLineParameter() throws Exception {
        return ACCRUED_INCOME_LINE.createAccountingLine(SourceAccountingLine.class, Constants.GL_DEBIT_CODE);
    }

    private SourceAccountingLine getAccruedSickPaySourceLineParameter() throws Exception {
        return ACCRUED_SICK_PAY_LINE.createAccountingLine(SourceAccountingLine.class, Constants.GL_DEBIT_CODE);
    }

    private GeneralLedgerPendingEntry getExpectedExplicitSourcePendingEntry() {
        return GeneralLedgerPendingEntryFixture.EXPECTED_AV_EXPLICIT_SOURCE_PENDING_ENTRY.createGeneralLedgerPendingEntry();
    }

    private GeneralLedgerPendingEntry getExpectedExplicitTargetPendingEntry() {
        return GeneralLedgerPendingEntryFixture.EXPECTED_AV_EXPLICIT_TARGET_PENDING_ENTRY.createGeneralLedgerPendingEntry();
    }

    private GeneralLedgerPendingEntry getExpectedOffsetTargetPendingEntry() {
        return new GeneralLedgerPendingEntry();
    }

    private GeneralLedgerPendingEntry getExpectedOffsetSourcePendingEntry() {
        return new GeneralLedgerPendingEntry();
    }

    private GeneralLedgerPendingEntry getExpectedExplicitSourcePendingEntryForExpense() {
        return GeneralLedgerPendingEntryFixture.EXPECTED_AV_EXPLICIT_SOURCE_PENDING_ENTRY_FOR_EXPENSE.createGeneralLedgerPendingEntry();
    }

    private GeneralLedgerPendingEntry getExpectedExplicitTargetPendingEntryForExpense() {
        return GeneralLedgerPendingEntryFixture.EXPECTED_AV_EXPLICIT_TARGET_PENDING_ENTRY_FOR_EXPENSE.createGeneralLedgerPendingEntry();
    }
}
