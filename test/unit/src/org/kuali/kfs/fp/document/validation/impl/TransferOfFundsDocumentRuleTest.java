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
import static org.kuali.module.financial.rules.IsDebitTestUtils.Amount.NEGATIVE;
import static org.kuali.module.financial.rules.IsDebitTestUtils.Amount.POSITIVE;
import static org.kuali.test.MockServiceUtils.mockConfigurationServiceForFlexibleOffsetEnabled;
import static org.kuali.test.fixtures.AccountingLineFixture.FLEXIBLE_EXPENSE_LINE;
import static org.kuali.test.fixtures.AccountingLineFixture.LINE10;
import static org.kuali.test.fixtures.AccountingLineFixture.LINE11;
import static org.kuali.test.fixtures.AccountingLineFixture.LINE12;
import static org.kuali.test.fixtures.AccountingLineFixture.LINE13;
import static org.kuali.test.fixtures.AccountingLineFixture.LINE8;
import static org.kuali.test.fixtures.AccountingLineFixture.LINE9;
import static org.kuali.test.fixtures.AccountingLineFixture.SOURCE_LINE;
import static org.kuali.test.fixtures.GeneralLedgerPendingEntryFixture.EXPECTED_EXPLICIT_SOURCE_PENDING_ENTRY;
import static org.kuali.test.fixtures.GeneralLedgerPendingEntryFixture.EXPECTED_EXPLICIT_TARGET_PENDING_ENTRY;
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
import java.util.ListIterator;

import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.core.bo.AccountingLine;
import org.kuali.core.bo.SourceAccountingLine;
import org.kuali.core.bo.TargetAccountingLine;
import org.kuali.core.document.Document;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.rule.TransactionalDocumentRuleTestBase;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.financial.document.TransferOfFundsDocument;
import org.kuali.module.gl.bo.GeneralLedgerPendingEntry;
import org.kuali.test.DocumentTestUtils;
import org.kuali.test.WithTestSpringContext;
/**
 * This class tests the Transfer of Funds Document's persistence, routing, and PE generation.
 * 
 * 
 */
@WithTestSpringContext(session = KHUNTLEY)
public class TransferOfFundsDocumentRuleTest extends TransactionalDocumentRuleTestBase {

    private static final String NON_MANDATORY_TRANSFER_OBJECT_CODE="1669";
    private static final String KNOWN_DOCUMENT_TYPENAME = "KualiTransferOfFundsDocument";

    // /////////////////////////////////////////////////////////////////////////
    // Fixture Methods Start Here //
    // /////////////////////////////////////////////////////////////////////////
    @Override
    protected final String getDocumentTypeName() {
        return KNOWN_DOCUMENT_TYPENAME;
    }

    /**
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#getAssetTargetLine()
     */
    @Override
    public final TargetAccountingLine getAssetTargetLine() throws Exception {
        return LINE11.createTargetAccountingLine();
    }

    @Override
    protected final TargetAccountingLine getValidObjectSubTypeTargetLine() throws Exception {
        return (TargetAccountingLine) makeObjectTypeAndSubTypeValid(LINE11.createTargetAccountingLine());
    }
    
    private AccountingLine makeObjectTypeAndSubTypeValid(AccountingLine line) {
        line.setFinancialObjectCode("1698"); // IN type and MT sub-type on UA chart
        line.refresh();
        return line;
    }

    @Override
    protected final TargetAccountingLine getInvalidObjectSubTypeTargetLine() throws Exception {
        return getTargetLineParameter3();
    }

    @Override
    protected final List<SourceAccountingLine> getValidObjectSubTypeSourceLines() throws Exception {
        List<SourceAccountingLine> retval = new ArrayList<SourceAccountingLine>();
        retval.add(LINE11.createSourceAccountingLine());
        return retval;
    }

    @Override
    protected final List<SourceAccountingLine> getInvalidObjectSubTypeSourceLines() throws Exception {
        List<SourceAccountingLine> retval = new ArrayList<SourceAccountingLine>();
        retval.add(LINE12.createSourceAccountingLine());
        retval.add(LINE12.createSourceAccountingLine());
        return retval;
    }

    @Override
    protected final List<TargetAccountingLine> getInvalidObjectSubTypeTargetLines() throws Exception {
        List<TargetAccountingLine> retval = new ArrayList<TargetAccountingLine>();
        retval.add(LINE11.createTargetAccountingLine());
        retval.add(getTargetLineParameter3());
        return retval;
    }

    @Override
    protected final List<TargetAccountingLine> getValidObjectSubTypeTargetLines() throws Exception {
        List<TargetAccountingLine> retval = new ArrayList<TargetAccountingLine>();
        retval.add(LINE11.createTargetAccountingLine());
        retval.add(LINE11.createTargetAccountingLine());
        return retval;
    }

    @Override
    protected final SourceAccountingLine getValidObjectTypeSourceLine() throws Exception {
        return LINE8.createSourceAccountingLine();
    }

    @Override
    protected final SourceAccountingLine getInvalidObjectTypeSourceLine() throws Exception {
        SourceAccountingLine line = LINE9.createSourceAccountingLine();
        line.setFinancialObjectCode("9889");
        line.refresh();
        assertEquals("need FB obj type because it is invalid", "FB", line.getObjectCode().getFinancialObjectTypeCode());
        return line;
    }

    @Override
    protected final SourceAccountingLine getInvalidObjectCodeSourceLine() throws Exception {
        return LINE10.createSourceAccountingLine();
    }

    @Override
    protected final SourceAccountingLine getValidObjectCodeSourceLine() throws Exception {
        return LINE11.createSourceAccountingLine();
    }

    @Override
    public final SourceAccountingLine getAssetSourceLine() throws Exception {
        return SOURCE_LINE.createSourceAccountingLine();
    }

    @Override
    protected final Document createDocument() throws Exception {
        return DocumentTestUtils.createTransactionalDocument(getDocumentService(), TransferOfFundsDocument.class, 2005, "01");
    }

    @Override
    protected final TransactionalDocument createDocument5() throws Exception {
        return DocumentTestUtils.createTransactionalDocument(getDocumentService(), TransferOfFundsDocument.class, 2007, "06");
    }

    @Override
    protected final Document createDocumentValidForRouting() throws Exception {
        TransferOfFundsDocument doc = (TransferOfFundsDocument) createDocument();

        KualiDecimal balance = new KualiDecimal("21.12");

        SourceAccountingLine sourceLine = new SourceAccountingLine();
        sourceLine.setChartOfAccountsCode("BL");
        sourceLine.setAccountNumber("1031400");
        sourceLine.setFinancialObjectCode("1663");
        sourceLine.setAmount(balance);
        sourceLine.refresh();
        ArrayList sourceLines = new ArrayList();
        sourceLines.add(sourceLine);

        TargetAccountingLine targetLine = new TargetAccountingLine();
        targetLine.setChartOfAccountsCode("BL");
        targetLine.setAccountNumber("1031400");
        targetLine.setFinancialObjectCode("5163");
        targetLine.setAmount(balance);
        targetLine.refresh();
        ArrayList targetLines = new ArrayList();
        targetLines.add(targetLine);

        doc.setSourceAccountingLines(sourceLines);
        doc.setTargetAccountingLines(targetLines);

        return doc;
    }

    @Override
    protected final Document createDocumentInvalidForSave() throws Exception {
        return getDocumentParameterNoDescription();
    }

    @Override
    protected final TransactionalDocument createDocumentWithInvalidObjectSubType() throws Exception {
        TransferOfFundsDocument retval = (TransferOfFundsDocument) createDocument();
        retval.setSourceAccountingLines(getInvalidObjectSubTypeSourceLines());
        retval.setTargetAccountingLines(getInvalidObjectSubTypeTargetLines());
        return retval;
    }

    @Override
    protected final TransactionalDocument createDocumentUnbalanced() throws Exception {
        TransferOfFundsDocument retval = (TransferOfFundsDocument) createDocument();
        retval.addSourceAccountingLine((SourceAccountingLine) makeObjectTypeAndSubTypeValid(getValidObjectCodeSourceLine()));
        retval.addSourceAccountingLine((SourceAccountingLine) makeObjectTypeAndSubTypeValid(getValidObjectCodeSourceLine()));
        retval.addTargetAccountingLine(getValidObjectSubTypeTargetLine());
        return retval;
    }

    @Override
    protected final Document createDocumentInvalidDescription() throws Exception {
        return getDocumentParameterNoDescription();
    }

    @Override
    protected final TransactionalDocument createDocumentWithValidObjectSubType() throws Exception {
        TransferOfFundsDocument retval = (TransferOfFundsDocument) createDocument();
        retval.setSourceAccountingLines(getValidObjectSubTypeSourceLines());
        retval.setTargetAccountingLines(getValidObjectSubTypeTargetLines());
        return retval;
    }

    /**
     * Accessor for fixture 'targetLine3'
     * 
     * @return AccountingLineParameter
     */
    public final TargetAccountingLine getTargetLineParameter3()throws Exception {
        return LINE13.createTargetAccountingLine();
    }


    public final Document getDocumentParameterNoDescription()throws Exception {
        TransferOfFundsDocument document = DocumentTestUtils.createTransactionalDocument(getDocumentService(), TransferOfFundsDocument.class, 2005, "01");
        document.getDocumentHeader().setFinancialDocumentDescription(null);
        return document;
    }

    /**
     * Accessor method for Explicit Source fixture used for testProcessGeneralLedgerPendingEntries test methods.
     * 
     * @return GeneralLedgerPendingEntry pending entry fixture
     */
    @Override
    public final GeneralLedgerPendingEntry getExpectedExplicitSourcePendingEntry() {
        return EXPECTED_EXPLICIT_SOURCE_PENDING_ENTRY.createGeneralLedgerPendingEntry();
    }

    /**
     * Accessor method for Explicit Target fixture used for testProcessGeneralLedgerPendingEntries test methods.
     * 
     * @return e pending entry fixture
     */
    @Override
    public final GeneralLedgerPendingEntry getExpectedExplicitTargetPendingEntry() {
        return EXPECTED_EXPLICIT_TARGET_PENDING_ENTRY.createGeneralLedgerPendingEntry();
    }

    /**
     * Accessor method for Offset Target fixture used for testProcessGeneralLedgerPendingEntries test methods.
     * 
     * @return GeneralLedgerPendingEntry pending entry fixture
     */
    @Override
    public final GeneralLedgerPendingEntry getExpectedOffsetTargetPendingEntry() {
        return EXPECTED_OFFSET_TARGET_PENDING_ENTRY.createGeneralLedgerPendingEntry();
    }

    /**
     * Accessor method for Offset Source fixture used for testProcessGeneralLedgerPendingEntries test methods.
     * 
     * @return GeneralLedgerPendingEntry pending entry fixture
     */
    @Override
    public final GeneralLedgerPendingEntry getExpectedOffsetSourcePendingEntry() {
        return EXPECTED_OFFSET_SOURCE_PENDING_ENTRY.createGeneralLedgerPendingEntry();
    }

    // /////////////////////////////////////////////////////////////////////////
    // Fixture Methods End Here //
    // /////////////////////////////////////////////////////////////////////////


    // /////////////////////////////////////////////////////////////////////////
    // Test Methods Start Here //
    // /////////////////////////////////////////////////////////////////////////

    /**
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#testAddAccountingLine_InvalidObjectSubType()
     */
    @Override
    public void testAddAccountingLine_InvalidObjectSubType() throws Exception {
        TransactionalDocument doc = createDocumentWithInvalidObjectSubType();
        // make sure we are using a valid object code for this type of doc
        for (int i = 0; i < doc.getSourceAccountingLines().size(); i++) {
            SourceAccountingLine sourceAccountingLine = (SourceAccountingLine) doc.getSourceAccountingLines().get(i);
            sourceAccountingLine.setFinancialObjectCode(NON_MANDATORY_TRANSFER_OBJECT_CODE);
        }

        for (int i = 0; i < doc.getTargetAccountingLines().size(); i++) {
            TargetAccountingLine sourceAccountingLine = (TargetAccountingLine) doc.getTargetAccountingLines().get(i);
            sourceAccountingLine.setFinancialObjectCode(NON_MANDATORY_TRANSFER_OBJECT_CODE);
        }

        testAddAccountingLine(doc, true);
    }

    public void testProcessGenerateGeneralLedgerPendingEntries_validSourceExpenseFlexibleOffset() throws Exception {
        mockConfigurationServiceForFlexibleOffsetEnabled(true);
        testProcessGenerateGeneralLedgerPendingEntries(FLEXIBLE_EXPENSE_LINE.createTargetAccountingLine(), EXPECTED_FLEXIBLE_EXPLICIT_SOURCE_PENDING_ENTRY_FOR_EXPENSE2.createGeneralLedgerPendingEntry(), EXPECTED_FLEXIBLE_OFFSET_SOURCE_PENDING_ENTRY.createGeneralLedgerPendingEntry());
    }

    public void testProcessGenerateGeneralLedgerPendingEntries_validSourceExpenseMissingOffsetDefinition() throws Exception {
        mockConfigurationServiceForFlexibleOffsetEnabled(true);
        testProcessGenerateGeneralLedgerPendingEntries(DocumentTestUtils.createTransactionalDocument(getDocumentService(), TransferOfFundsDocument.class, 2000, "06"), FLEXIBLE_EXPENSE_LINE.createSourceAccountingLine(), EXPECTED_FLEXIBLE_EXPLICIT_SOURCE_PENDING_ENTRY_FOR_EXPENSE.createGeneralLedgerPendingEntry(),EXPECTED_FLEXIBLE_OFFSET_SOURCE_PENDING_ENTRY_MISSING_OFFSET_DEFINITION.createGeneralLedgerPendingEntry(), false);
        assertGlobalErrorMapContains(Constants.GENERAL_LEDGER_PENDING_ENTRIES_TAB_ERRORS, KeyConstants.ERROR_DOCUMENT_NO_OFFSET_DEFINITION);
    }


    // public void testProcessSourceAccountingLineSufficientFundsCheckingPreparation_isExpense_postive_lineAmount() {
    // SourceAccountingLine line = (SourceAccountingLine) getFixtureEntryFromCollection(COLLECTION_NAME,
    // SUFF_FUNDS_CHECKING_SOURCE_EXPENSE).createObject();
    // line.setAmount(new KualiDecimal("3.0"));
    //
    // TransferOfFundsDocumentRule rule = new TransferOfFundsDocumentRule();
    // SufficientFundsItem item = rule.processSourceAccountingLineSufficientFundsCheckingPreparation(null, line);
    //
    // assertEquals(SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue(TransferOfFundsDocumentRuleConstants.KUALI_TRANSACTION_PROCESSING_TRANSFER_OF_FUNDS_SECURITY_GROUPING,
    // TransferOfFundsDocumentRuleConstants.TRANSFER_OF_FUNDS_EXPENSE_OBJECT_TYPE_CODE), item.getFinancialObjectTypeCode());
    // assertEquals(Constants.GL_CREDIT_CODE, item.getDebitCreditCode());
    // assertTrue(line.getAmount().equals(item.getAmount()));
    // }
    //
    // public void testProcessSourceAccountingLineSufficientFundsCheckingPreparation_isAsset__negative_lineAmount() {
    // SourceAccountingLine line = (SourceAccountingLine) getFixtureEntryFromCollection(COLLECTION_NAME,
    // SUFF_FUNDS_CHECKING_SOURCE_ASSET).createObject();
    // line.setAmount(new KualiDecimal("-3.0"));
    //
    // TransferOfFundsDocumentRule rule = new TransferOfFundsDocumentRule();
    // SufficientFundsItem item = rule.processSourceAccountingLineSufficientFundsCheckingPreparation(null, line);
    //
    // assertEquals(line.getObjectTypeCode(), item.getFinancialObjectTypeCode());
    // assertEquals(Constants.GL_DEBIT_CODE, item.getDebitCreditCode());
    // assertFalse(line.getAmount().equals(item.getAmount()));
    // assertTrue(line.getAmount().abs().equals(item.getAmount()));
    // }
    //
    // public void testProcessSourceAccountingLineSufficientFundsCheckingPreparation_isIncome_postive_lineAmount() {
    // SourceAccountingLine line = (SourceAccountingLine) getFixtureEntryFromCollection(COLLECTION_NAME,
    // SUFF_FUNDS_CHECKING_SOURCE_INCOME).createObject();
    // line.setAmount(new KualiDecimal("3.0"));
    //
    // TransferOfFundsDocumentRule rule = new TransferOfFundsDocumentRule();
    // SufficientFundsItem item = rule.processSourceAccountingLineSufficientFundsCheckingPreparation(null, line);
    //
    // assertEquals(SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue(TransferOfFundsDocumentRuleConstants.KUALI_TRANSACTION_PROCESSING_TRANSFER_OF_FUNDS_SECURITY_GROUPING,
    // TransferOfFundsDocumentRuleConstants.TRANSFER_OF_FUNDS_INCOME_OBJECT_TYPE_CODE), item.getFinancialObjectTypeCode());
    // assertEquals(Constants.GL_CREDIT_CODE, item.getDebitCreditCode());
    // assertTrue(line.getAmount().equals(item.getAmount()));
    // }
    //
    // public void testProcessSourceAccountingLineSufficientFundsCheckingPreparation_isLiability__negative_lineAmount() {
    // SourceAccountingLine line = (SourceAccountingLine) getFixtureEntryFromCollection(COLLECTION_NAME,
    // SUFF_FUNDS_CHECKING_SOURCE_LIABILITY).createObject();
    // line.setAmount(new KualiDecimal("-3.0"));
    //
    // TransferOfFundsDocumentRule rule = new TransferOfFundsDocumentRule();
    // SufficientFundsItem item = rule.processSourceAccountingLineSufficientFundsCheckingPreparation(null, line);
    //
    // assertEquals(line.getObjectTypeCode(), item.getFinancialObjectTypeCode());
    // assertEquals(Constants.GL_DEBIT_CODE, item.getDebitCreditCode());
    // assertFalse(line.getAmount().equals(item.getAmount()));
    // assertTrue(line.getAmount().abs().equals(item.getAmount()));
    // }
    //
    // public void testProcessTargetAccountingLineSufficientFundsCheckingPreparation_isExpense_postive_lineAmount() {
    // TargetAccountingLine line = (TargetAccountingLine) getFixtureEntryFromCollection(COLLECTION_NAME,
    // SUFF_FUNDS_CHECKING_TARGET_EXPENSE).createObject();
    // line.setAmount(new KualiDecimal("3.0"));
    //
    // TransferOfFundsDocumentRule rule = new TransferOfFundsDocumentRule();
    // SufficientFundsItem item = rule.processTargetAccountingLineSufficientFundsCheckingPreparation(null, line);
    //
    // assertEquals(SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue(TransferOfFundsDocumentRuleConstants.KUALI_TRANSACTION_PROCESSING_TRANSFER_OF_FUNDS_SECURITY_GROUPING,
    // TransferOfFundsDocumentRuleConstants.TRANSFER_OF_FUNDS_EXPENSE_OBJECT_TYPE_CODE), item.getFinancialObjectTypeCode());
    // assertEquals(Constants.GL_DEBIT_CODE, item.getDebitCreditCode());
    // assertTrue(line.getAmount().equals(item.getAmount()));
    // }
    //
    // public void testProcessTargetAccountingLineSufficientFundsCheckingPreparation_isAsset__negative_lineAmount() {
    // TargetAccountingLine line = (TargetAccountingLine) getFixtureEntryFromCollection(COLLECTION_NAME,
    // SUFF_FUNDS_CHECKING_TARGET_ASSET).createObject();
    // line.setAmount(new KualiDecimal("-3.0"));
    //
    // TransferOfFundsDocumentRule rule = new TransferOfFundsDocumentRule();
    // SufficientFundsItem item = rule.processTargetAccountingLineSufficientFundsCheckingPreparation(null, line);
    //
    // assertEquals(line.getObjectTypeCode(), item.getFinancialObjectTypeCode());
    // assertEquals(Constants.GL_CREDIT_CODE, item.getDebitCreditCode());
    // assertFalse(line.getAmount().equals(item.getAmount()));
    // assertTrue(line.getAmount().abs().equals(item.getAmount()));
    // }
    //
    // public void testProcessTargetAccountingLineSufficientFundsCheckingPreparation_isIncome_postive_lineAmount() {
    // TargetAccountingLine line = (TargetAccountingLine) getFixtureEntryFromCollection(COLLECTION_NAME,
    // SUFF_FUNDS_CHECKING_TARGET_INCOME).createObject();
    // line.setAmount(new KualiDecimal("3.0"));
    //
    // TransferOfFundsDocumentRule rule = new TransferOfFundsDocumentRule();
    // SufficientFundsItem item = rule.processTargetAccountingLineSufficientFundsCheckingPreparation(null, line);
    //
    // assertEquals(SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue(TransferOfFundsDocumentRuleConstants.KUALI_TRANSACTION_PROCESSING_TRANSFER_OF_FUNDS_SECURITY_GROUPING,
    // TransferOfFundsDocumentRuleConstants.TRANSFER_OF_FUNDS_INCOME_OBJECT_TYPE_CODE), item.getFinancialObjectTypeCode());
    // assertEquals(Constants.GL_DEBIT_CODE, item.getDebitCreditCode());
    // assertTrue(line.getAmount().equals(item.getAmount()));
    // }
    //
    // public void testProcessTargetAccountingLineSufficientFundsCheckingPreparation_isLiability__negative_lineAmount() {
    // TargetAccountingLine line = (TargetAccountingLine) getFixtureEntryFromCollection(COLLECTION_NAME,
    // SUFF_FUNDS_CHECKING_TARGET_LIABILITY).createObject();
    // line.setAmount(new KualiDecimal("-3.0"));
    //
    // TransferOfFundsDocumentRule rule = new TransferOfFundsDocumentRule();
    // SufficientFundsItem item = rule.processTargetAccountingLineSufficientFundsCheckingPreparation(null, line);
    //
    // assertEquals(line.getObjectTypeCode(), item.getFinancialObjectTypeCode());
    // assertEquals(Constants.GL_CREDIT_CODE, item.getDebitCreditCode());
    // assertFalse(line.getAmount().equals(item.getAmount()));
    // assertTrue(line.getAmount().abs().equals(item.getAmount()));
    // }
    //
    public void testProcessCustomRouteDocumentBusinessRules_accountingLines_notMatching_budgetYear() throws Exception {
        TransferOfFundsDocument document = (TransferOfFundsDocument) createDocumentValidForRouting();
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
        TransferOfFundsDocument document = (TransferOfFundsDocument) createDocumentValidForRouting();
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

    /**
     * tests true is returned for a positive income
     * 
     * @throws Exception
     */
    public void testIsDebit_source_income_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(transactionalDocument, SourceAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a negative income
     * 
     * @throws Exception
     */
    public void testIsDebit_source_income_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(transactionalDocument, SourceAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a zero income
     * 
     * @throws Exception
     */
    public void testIsDebit_source_income_zeroAmount() throws Exception {

        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(transactionalDocument, SourceAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests true is returned for a positive expense
     * 
     * @throws Exception
     */
    public void testIsDebit_source_expense_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(transactionalDocument, SourceAccountingLine.class, POSITIVE);
        assertTrue(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a negative expense
     * 
     * @throws Exception
     */
    public void testIsDebit_source_expense_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(transactionalDocument, SourceAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a zero expense
     * 
     * @throws Exception
     */
    public void testIsDebit_source_expense_zeroAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(transactionalDocument, SourceAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a positive asset
     * 
     * @throws Exception
     */
    public void testIsDebit_source_asset_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(transactionalDocument, SourceAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a negative asset
     * 
     * @throws Exception
     */
    public void testIsDebit_source_asset_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(transactionalDocument, SourceAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a zero asset
     * 
     * @throws Exception
     */
    public void testIsDebit_source_asset_zeroAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(transactionalDocument, SourceAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a positive liability
     * 
     * @throws Exception
     */
    public void testIsDebit_source_liability_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(transactionalDocument, SourceAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a negative liability
     * 
     * @throws Exception
     */
    public void testIsDebit_source_liability_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(transactionalDocument, SourceAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a zero liability
     * 
     * @throws Exception
     */
    public void testIsDebit_source_liability_zeroAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(transactionalDocument, SourceAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests false is returned for a positive income
     * 
     * @throws Exception
     */
    public void testIsDebit_target_income_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(transactionalDocument, TargetAccountingLine.class, POSITIVE);

        assertFalse(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a negative income
     * 
     * @throws Exception
     */
    public void testIsDebit_target_income_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(transactionalDocument, TargetAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a zero income
     * 
     * @throws Exception
     */
    public void testIsDebit_target_income_zeroAmount() throws Exception {

        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(transactionalDocument, TargetAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests false is returned for a positive expense
     * 
     * @throws Exception
     */
    public void testIsDebit_target_expense_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(transactionalDocument, TargetAccountingLine.class, POSITIVE);

        assertFalse(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a negative expense
     * 
     * @throws Exception
     */
    public void testIsDebit_target_expense_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(transactionalDocument, TargetAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a zero expense
     * 
     * @throws Exception
     */
    public void testIsDebit_target_expense_zeroAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(transactionalDocument, TargetAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a positive asset
     * 
     * @throws Exception
     */
    public void testIsDebit_target_asset_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(transactionalDocument, TargetAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a negative asset
     * 
     * @throws Exception
     */
    public void testIsDebit_target_asset_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(transactionalDocument, TargetAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a zero asset
     * 
     * @throws Exception
     */
    public void testIsDebit_target_asset_zeroAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(transactionalDocument, TargetAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a positive liability
     * 
     * @throws Exception
     */
    public void testIsDebit_target_liability_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(transactionalDocument, TargetAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a negative liability
     * 
     * @throws Exception
     */
    public void testIsDebit_target_liability_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(transactionalDocument, TargetAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a zero liability
     * 
     * @throws Exception
     */
    public void testIsDebit_target_liability_zeroAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(transactionalDocument, TargetAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a positive income
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_source_income_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(transactionalDocument, SourceAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests false is returned for a negative income
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_source_income_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(transactionalDocument, SourceAccountingLine.class, NEGATIVE);

        assertFalse(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a zero income
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_source_income_zeroAmount() throws Exception {

        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(transactionalDocument, SourceAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for positive expense
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_source_expense_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(transactionalDocument, SourceAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests false is returned for a negative expense
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_source_expense_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(transactionalDocument, SourceAccountingLine.class, NEGATIVE);

        assertFalse(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a zero expense
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_source_expense_zeroAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(transactionalDocument, SourceAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a positive asset
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_source_asset_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(transactionalDocument, SourceAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a negative asset
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_source_asset_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(transactionalDocument, SourceAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a zero asset
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_source_asset_zeroAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(transactionalDocument, SourceAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a positive liability
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_source_liability_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(transactionalDocument, SourceAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a negative liability
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_source_liability_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(transactionalDocument, SourceAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a zero liability
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_source_liability_zeroAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(transactionalDocument, SourceAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> for a positive income
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_target_income_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(transactionalDocument, TargetAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests true is returned for a negative income
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_target_income_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(transactionalDocument, TargetAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));

    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a zero income
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_target_income_zeroAmount() throws Exception {

        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(transactionalDocument, TargetAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a positive expense
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_target_expense_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(transactionalDocument, TargetAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests true is returned for a zero expense
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_target_expense_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(transactionalDocument, TargetAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a zero expense
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_target_expense_zeroAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(transactionalDocument, TargetAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a positive asset
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_target_asset_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(transactionalDocument, TargetAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a negative asset
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_target_asset_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(transactionalDocument, TargetAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a zero asset
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_target_asset_zeroAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(transactionalDocument, TargetAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a positive liability
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_target_liability_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(transactionalDocument, TargetAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a negative liability
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_target_liability_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(transactionalDocument, TargetAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a zero liability
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_target_liability_zeroAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), TransferOfFundsDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(transactionalDocument, TargetAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }
    
    @Override
    public void testProcessGenerateGeneralLedgerPendingEntries_validSourceAsset() throws Exception {
        // Override this test to not run; it's invalid because TOF doesn't allow assets.  (TOF allows income.)
        // todo: stop inheriting test methods
    }

    @Override
    public void testProcessGenerateGeneralLedgerPendingEntries_validTargetAsset() throws Exception {
        // Override this test to not run; it's invalid because TOF doesn't allow assets.  (TOF allows income.)
        // todo: stop inheriting test methods
    }

    // /////////////////////////////////////////////////////////////////////////
    // Test Methods End Here //
    // /////////////////////////////////////////////////////////////////////////
}
