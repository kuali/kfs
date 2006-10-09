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
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.kuali.Constants;
import static org.kuali.Constants.GL_CREDIT_CODE;
import static org.kuali.Constants.GL_DEBIT_CODE;
import org.kuali.KeyConstants;
import org.kuali.PropertyConstants;
import org.kuali.core.bo.AccountingLine;
import org.kuali.core.bo.SourceAccountingLine;
import org.kuali.core.bo.TargetAccountingLine;
import org.kuali.core.document.Document;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.rule.TransactionalDocumentRuleTestBase;
import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.core.util.GlobalVariables;
import static org.kuali.core.util.SpringServiceLocator.getDataDictionaryService;
import static org.kuali.core.util.SpringServiceLocator.getDocumentService;
import static org.kuali.core.util.SpringServiceLocator.getDocumentTypeService;
import org.kuali.module.financial.document.JournalVoucherDocument;
import org.kuali.module.gl.bo.GeneralLedgerPendingEntry;
import org.kuali.test.DocumentTestUtils;
import org.kuali.test.WithTestSpringContext;
import static org.kuali.test.fixtures.AccountingLineFixture.EXPENSE_LINE;
import static org.kuali.test.fixtures.AccountingLineFixture.EXPENSE_LINE2;
import static org.kuali.test.fixtures.AccountingLineFixture.EXTERNAL_ENCUMBRANCE_LINE;
import static org.kuali.test.fixtures.AccountingLineFixture.LINE10;
import static org.kuali.test.fixtures.AccountingLineFixture.LINE11;
import static org.kuali.test.fixtures.AccountingLineFixture.LINE13;
import static org.kuali.test.fixtures.AccountingLineFixture.LINE8;
import static org.kuali.test.fixtures.AccountingLineFixture.LINE9;
import static org.kuali.test.fixtures.AccountingLineFixture.SOURCE_LINE;
import static org.kuali.test.fixtures.GeneralLedgerPendingEntryFixture.EXPECTED_JV_EXPLICIT_SOURCE_PENDING_ENTRY;
import static org.kuali.test.fixtures.GeneralLedgerPendingEntryFixture.EXPECTED_JV_EXPLICIT_SOURCE_PENDING_ENTRY_FOR_EXPENSE;
import static org.kuali.test.fixtures.GeneralLedgerPendingEntryFixture.EXPECTED_JV_EXPLICIT_TARGET_PENDING_ENTRY;
import static org.kuali.test.fixtures.GeneralLedgerPendingEntryFixture.EXPECTED_JV_EXPLICIT_TARGET_PENDING_ENTRY_FOR_EXPENSE;
import static org.kuali.test.fixtures.GeneralLedgerPendingEntryFixture.EXPECTED_OFFSET_SOURCE_PENDING_ENTRY;
import static org.kuali.test.fixtures.GeneralLedgerPendingEntryFixture.EXPECTED_OFFSET_TARGET_PENDING_ENTRY;
import static org.kuali.test.fixtures.UserNameFixture.DFOGLE;

/**
 * This class tests the JournalVoucherDocument's persistence, routing, and PE generation.
 * 
 * 
 */
@WithTestSpringContext(session = DFOGLE)
public class JournalVoucherDocumentRuleTest extends TransactionalDocumentRuleTestBase {

    private static final String KNOWN_DOCUMENT_TYPENAME = "KualiJournalVoucherDocument";

    // /////////////////////////////////////////////////////////////////////////
    // Fixture Methods Start Here //
    // ////////////////////////////////////////////////////////////////

    @Override
    protected final String getDocumentTypeName() {
        return KNOWN_DOCUMENT_TYPENAME;
    }

    /**
     * 
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#getAssetTargetLine()
     */
    @Override
    public final TargetAccountingLine getAssetTargetLine() throws Exception {
        return LINE11.createTargetAccountingLine();
    }

    /**
     * 
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#getValidObjectSubTypeTargetLine()
     */
    @Override
    protected final TargetAccountingLine getValidObjectSubTypeTargetLine() throws Exception {
        return LINE11.createTargetAccountingLine();
    }

    /**
     * 
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#getInvalidObjectSubTypeTargetLine()
     */
    @Override
    protected final TargetAccountingLine getInvalidObjectSubTypeTargetLine() throws Exception {
        return getTargetLineParameter3();
    }

    /**
     * 
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#getValidObjectSubTypeSourceLines()
     */
    @Override
    protected final List<SourceAccountingLine> getValidObjectSubTypeSourceLines() throws Exception {
        List<SourceAccountingLine> retval = new ArrayList<SourceAccountingLine>();
        retval.add(LINE11.createSourceAccountingLine());
        return retval;
    }

    /**
     * 
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#getInvalidObjectSubTypeSourceLines()
     */
    @Override
    protected final List<SourceAccountingLine> getInvalidObjectSubTypeSourceLines() throws Exception {
        throw new RuntimeException("this doesnt apply to this document");
    }

    /**
     * 
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#getInvalidObjectSubTypeTargetLines()
     */
    @Override
    protected final List<TargetAccountingLine> getInvalidObjectSubTypeTargetLines() throws Exception {
        throw new RuntimeException("this doesnt apply to this document");
    }

    /**
     * 
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#getValidObjectSubTypeTargetLines()
     */
    @Override
    protected final List<TargetAccountingLine> getValidObjectSubTypeTargetLines() throws Exception {
        List<TargetAccountingLine> retval = new ArrayList<TargetAccountingLine>();
        retval.add(LINE11.createTargetAccountingLine());
        retval.add(LINE11.createTargetAccountingLine());
        return retval;
    }

    /**
     * 
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#getValidObjectTypeSourceLine()
     */
    @Override
    protected final SourceAccountingLine getValidObjectTypeSourceLine() throws Exception {
        return LINE8.createSourceAccountingLine();
    }

    /**
     * 
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#getInvalidObjectTypeSourceLine()
     */
    @Override
    protected final SourceAccountingLine getInvalidObjectTypeSourceLine() throws Exception {
        SourceAccountingLine retval = LINE9.createSourceAccountingLine();
        retval.setObjectTypeCode(new String());
        return retval;
    }

    /**
     * 
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#getInvalidObjectCodeSourceLine()
     */
    @Override
    protected final SourceAccountingLine getInvalidObjectCodeSourceLine() throws Exception {
        return LINE10.createSourceAccountingLine();
    }

    /**
     * 
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#getValidObjectCodeSourceLine()
     */
    @Override
    protected final SourceAccountingLine getValidObjectCodeSourceLine() throws Exception {
        return LINE11.createSourceAccountingLine();
    }

    /**
     * 
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#getAssetSourceLine()
     */
    @Override
    public final SourceAccountingLine getAssetSourceLine() throws Exception {
        return SOURCE_LINE.createSourceAccountingLine();
    }

    /**
     * 
     * @see org.kuali.core.rule.DocumentRuleTestBase#createDocument()
     */
    @Override
    protected final Document createDocument() throws Exception {
        return buildDocument();
    }

    /**
     * 
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#createDocument5()
     */
    @Override
    protected final TransactionalDocument createDocument5() throws Exception {

        // return (TransactionalDocument)getDocumentParameter5()
        // .createDocument( getDocumentService() );
        return (TransactionalDocument) buildDocument();
    }

    /**
     * 
     * @see org.kuali.core.rule.DocumentRuleTestBase#createDocumentValidForRouting()
     */
    @Override
    protected final Document createDocumentValidForRouting() throws Exception {
        return createDocumentWithValidObjectSubType();
    }

    /**
     * 
     * @see org.kuali.core.rule.DocumentRuleTestBase#createDocumentInvalidForSave()
     */
    @Override
    protected final Document createDocumentInvalidForSave() throws Exception {
        return createDocumentInvalidDescription();
    }

    /**
     * 
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#createDocumentWithInvalidObjectSubType()
     */
    @Override
    protected final TransactionalDocument createDocumentWithInvalidObjectSubType() throws Exception {
       throw new RuntimeException("this doesnt apply to this document");
    }

    /**
     * This is for unbalanced <code>{@link JournalVoucherDocument}</code>, but <code>{@link JournalVoucherDocument}</code>
     * doesn't need to be balanced, so to substitute, I'm using a <code>{@link Document}</code> without a balance at all because
     * that's semantically the same.
     * 
     * @return TransactionalDocument
     * @see TransactionalDocumentRuleTestBase#createDocumentUnbalanced()
     */
    @Override
    protected final TransactionalDocument createDocumentUnbalanced() throws Exception {
        TransactionalDocument retval = (TransactionalDocument) createDocument();
        return retval;
    }

    /**
     * 
     * @see org.kuali.core.rule.DocumentRuleTestBase#createDocumentInvalidDescription()
     */
    @Override
    protected final Document createDocumentInvalidDescription() throws Exception {
        Document retval = 
             DocumentTestUtils.createTransactionalDocument(getDocumentService(), JournalVoucherDocument.class, 2007, "06");
        
        retval.getDocumentHeader().setFinancialDocumentDescription(new String());
        return retval;
    }

    /**
     * 
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#createDocumentWithValidObjectSubType()
     */
    @Override
    protected final TransactionalDocument createDocumentWithValidObjectSubType() throws Exception {
        TransactionalDocument retval = (TransactionalDocument) createDocument();
        retval.setSourceAccountingLines(getValidObjectSubTypeSourceLines());
        retval.setTargetAccountingLines(getValidObjectSubTypeTargetLines());
        return retval;
    }

    /**
     * Accessor for fixture 'targetLine3'
     * 
     * @return AccountingLineParameter
     */
    public final TargetAccountingLine getTargetLineParameter3() throws Exception{
        return LINE13.createTargetAccountingLine();
    }

    /**
     * Accessor method for Explicit Source fixture used for testProcessGeneralLedgerPendingEntries test methods.
     * 
     * @return GeneralLedgerPendingEntry pending entry fixture
     */
    @Override
    public final GeneralLedgerPendingEntry getExpectedExplicitSourcePendingEntry() {
        return EXPECTED_JV_EXPLICIT_SOURCE_PENDING_ENTRY.createGeneralLedgerPendingEntry();
    }

    /**
     * Accessor method for Explicit Target fixture used for testProcessGeneralLedgerPendingEntries test methods.
     * 
     * @return GeneralLedgerPendingEntry pending entry fixture
     */
    @Override
    public final GeneralLedgerPendingEntry getExpectedExplicitTargetPendingEntry() {
        return EXPECTED_JV_EXPLICIT_TARGET_PENDING_ENTRY.createGeneralLedgerPendingEntry();
    }

    /**
     * Accessor method for Explicit Source fixture used for testProcessGeneralLedgerPendingEntries test methods.
     * 
     * @return GeneralLedgerPendingEntry pending entry fixture
     */
    @Override
    public final GeneralLedgerPendingEntry getExpectedExplicitSourcePendingEntryForExpense() {
        return EXPECTED_JV_EXPLICIT_SOURCE_PENDING_ENTRY_FOR_EXPENSE.createGeneralLedgerPendingEntry();
    }

    /**
     * Accessor method for Explicit Target fixture used for testProcessGeneralLedgerPendingEntries test methods.
     * 
     * @return GeneralLedgerPendingEntry pending entry fixture
     */
    @Override
    public final GeneralLedgerPendingEntry getExpectedExplicitTargetPendingEntryForExpense() {
        return EXPECTED_JV_EXPLICIT_TARGET_PENDING_ENTRY_FOR_EXPENSE.createGeneralLedgerPendingEntry();
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

    

    /**
     * Fixture method used to handle special case <code>{@link TransactionalDocumentParameter}</code> instances.
     * 
     * <p>
     * This is probably not the best way to do this. Matt suggested that a specific <code>{@link DocumentParameter}</code>
     * definition be made to handle the fixtures and the <code>{@link Document}</code> creation if more fields are added and
     * <code>{@link JournalVoucherDocument}</code> becomes more complex and demanding. Something like a
     * <code>JournalVoucherDocumentParameter</code> would probably work to remedy this. For specific details look at the
     * <code>{@link DisbursementVoucherDocumentTest}</code> that Matt worked on.
     * </p>
     * 
     * @param parameter <code>{@link DocumentParameter}</code> instance to use.
     * @return Document newly created <code>{@link Document}</code>
     * @throws Exception
     * @see org.kuali.test.parameters.DisbursementVoucherDocumentParameter
     */
    private Document buildDocument() throws Exception {
        JournalVoucherDocument retval = DocumentTestUtils.createTransactionalDocument(getDocumentService(), JournalVoucherDocument.class, 2007, "06");
        retval.setBalanceTypeCode(Constants.BALANCE_TYPE_ACTUAL);
        return retval;
    }

    // /////////////////////////////////////////////////////////////////////////
    // Fixture Methods End Here //
    // /////////////////////////////////////////////////////////////////////////


    // /////////////////////////////////////////////////////////////////////////
    // Test Methods Start Here //
    // /////////////////////////////////////////////////////////////////////////
    
    /**
     * method overridden from <code>{@link TransactionalDocumentRuleTestBase}</code> because all object codes are allowed in
     * <code>{@link JournalVoucherDocument}</code>
     * 
     */
    @Override
    public void testIsObjectCodeAllowed_InvalidObjectCode() {
    }

    /**
     * Tests ObjectSubType validation of <code>{@link AccountingLine}</code> from <code>{@link AddAccountingLineEvent}</code>
     * 
     * method overridden from <code>{@link TransactionalDocumentRuleTestBase}</code> because all object sub type codes are allowed
     * in <code>{@link JournalVoucherDocument}</code>
     */
    @Override
    public void testIsObjectSubTypeAllowed_InvalidSubType() {
    }

    /**
     * all obect sub types are alloed on the JV
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#testAddAccountingLine_InvalidObjectSubType()
     */
    @Override
    public void testAddAccountingLine_InvalidObjectSubType() throws Exception {
        
    }


    /**
     * This test is overriddden becaues <code>{@link JournalVoucherDocumentRule}</code> doesn't add offset entries.
     * 
     * @param line
     * @param expectedExplicitEntry
     * @param expectedOffsetEntry
     * @throws Exception
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#testProcessGenerateGeneralLedgerPendingEntries(org.kuali.core.bo.AccountingLine,
     *      org.kuali.module.gl.bo.GeneralLedgerPendingEntry, org.kuali.module.gl.bo.GeneralLedgerPendingEntry)
     */
    @Override
    protected void testProcessGenerateGeneralLedgerPendingEntries(AccountingLine line, GeneralLedgerPendingEntry expectedExplicitEntry, GeneralLedgerPendingEntry expectedOffsetEntry) throws Exception {
        TransactionalDocument document = createDocument5();
        assertEquals(0, document.getGeneralLedgerPendingEntries().size());
        getGenerateGeneralLedgerPendingEntriesRule().processGenerateGeneralLedgerPendingEntries(document, line, new GeneralLedgerPendingEntrySequenceHelper());
        assertEquals(1, document.getGeneralLedgerPendingEntries().size());
        assertSparselyEqualBean(expectedExplicitEntry, document.getGeneralLedgerPendingEntry(0));

    }

    /**
     * 
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#testProcessGenerateGeneralLedgerPendingEntries_validSourceExpense()
     */
    @Override
    public void testProcessGenerateGeneralLedgerPendingEntries_validSourceExpense() throws Exception {
        testProcessGenerateGeneralLedgerPendingEntries(EXPENSE_LINE.createSourceAccountingLine(), getExpectedExplicitSourcePendingEntryForExpense(), getExpectedOffsetSourcePendingEntry());
    }


    /**
     * JV Docs do not use target lines. So this test doesn't apply. However, we need to override from the base test class and always
     * assert true so that we aren't flagged incorrectly with this as an error.
     * 
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#testProcessGenerateGeneralLedgerPendingEntries_validTargetExpense()
     */
    @Override
    public void testProcessGenerateGeneralLedgerPendingEntries_validTargetExpense() throws Exception {
        assertTrue(true);
    }

    /**
     * JV Docs do not use target lines. So this test doesn't apply. However, we need to override from the base test class and always
     * assert true so that we aren't flagged incorrectly with this as an error.
     * 
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#testProcessGenerateGeneralLedgerPendingEntries_validTargetAsset()
     */
    @Override
    public void testProcessGenerateGeneralLedgerPendingEntries_validTargetAsset() throws Exception {
        assertTrue(true);
    }

    /**
     * @throws Exception
     */
    public void testProcessAddAccountingLineBusinessRules_irrelevantReferenceOriginCode() throws Exception {
        testProcessAddAccountingLineBusinessRules(EXPENSE_LINE2.createSourceAccountingLine(), null, null);
    }

    /**
     * @throws Exception
     */
    public void testProcessAddAccountingLineBusinessRules_emptyReferenceOriginCode() throws Exception {
        AccountingLine line = EXTERNAL_ENCUMBRANCE_LINE.createSourceAccountingLine();
        line.setReferenceOriginCode("");
        testProcessAddAccountingLineBusinessRules(line, PropertyConstants.REFERENCE_ORIGIN_CODE, KeyConstants.ERROR_REQUIRED);
    }

    /**
     * @throws Exception
     */
    public void testProcessAddAccountingLineBusinessRules_emptyReferences() throws Exception {
        AccountingLine line = EXTERNAL_ENCUMBRANCE_LINE.createSourceAccountingLine();
        line.setReferenceOriginCode("");
        line.setReferenceNumber("");
        line.setReferenceTypeCode("");
        testProcessAddAccountingLineBusinessRules(line, PropertyConstants.REFERENCE_ORIGIN_CODE, KeyConstants.ERROR_REQUIRED);
        assertGlobalErrorMapContains(PropertyConstants.REFERENCE_NUMBER, KeyConstants.ERROR_REQUIRED);
        assertGlobalErrorMapContains(PropertyConstants.REFERENCE_TYPE_CODE, KeyConstants.ERROR_REQUIRED);
    }

    /**
     * @throws Exception
     */
    public void testProcessAddAccountingLineBusinessRules_validReferences() throws Exception {
        testProcessAddAccountingLineBusinessRules(EXTERNAL_ENCUMBRANCE_LINE.createSourceAccountingLine(), null, null);
    }

    /**
     * @throws Exception
     */
    public void testProcessAddAccountingLineBusinessRules_invalidReferenceOriginCode() throws Exception {
        AccountingLine line = EXTERNAL_ENCUMBRANCE_LINE.createSourceAccountingLine();
        line.setReferenceOriginCode("42");
        testProcessAddAccountingLineBusinessRules(line, PropertyConstants.REFERENCE_ORIGIN_CODE, KeyConstants.ERROR_EXISTENCE);
    }

    /**
     * @throws Exception
     */
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
        boolean wasValid = getAddAccountingLineRule().processAddAccountingLineBusinessRules(createDocumentUnbalanced(), line);
        if (expectedErrorFieldName == null) {
            assertGlobalErrorMapEmpty(line.toString()); // fail printing error map for debugging before failing on simple result
            assertEquals("wasValid " + line, true, wasValid);
        }
        else {
            assertGlobalErrorMapContains(line.toString(), expectedErrorFieldName, expectedErrorKey);
            assertEquals("wasValid " + line, false, wasValid);
        }
    }

    /**
     * 
     */
    // public void testProcessTargetAccountingLineSufficientFundsCheckingPreparation_line_notNull() {
    // boolean failedAsExpected = false;
    // JournalVoucherDocumentRule rule = new JournalVoucherDocumentRule();
    // try {
    // TargetAccountingLine line = LINE11.createTargetAccountingLine();
    //
    // rule.processTargetAccountingLineSufficientFundsCheckingPreparation(null, line);
    // }
    // catch (IllegalArgumentException e) {
    // failedAsExpected = true;
    // }
    // assertTrue(failedAsExpected);
    // }
    //
    // /**
    // *
    // */
    // public void testProcessTargetAccountingLineSufficientFundsCheckingPreparation_line_null() {
    // boolean failedAsExpected = false;
    // JournalVoucherDocumentRule rule = new JournalVoucherDocumentRule();
    //
    // SufficientFundsItem item = rule.processTargetAccountingLineSufficientFundsCheckingPreparation(null, null);
    //
    // assertNull(item);
    // }
    //
    // /**
    // * @throws Exception
    // */
    // public void testProcessSourceAccountingLineSufficientFundsCheckingPreparation_balanceTypeCode_notAllowed() throws Exception {
    // JournalVoucherDocument document = (JournalVoucherDocument) createDocument();
    // document.setBalanceTypeCode("BB");
    // SourceAccountingLine line = LINE12.createSourceAccountingLine();
    //
    // JournalVoucherDocumentRule rule = new JournalVoucherDocumentRule();
    //
    // SufficientFundsItem item = rule.processSourceAccountingLineSufficientFundsCheckingPreparation(document, line);
    //
    // assertNull(item);
    // }
    /**
     * @throws Exception
     */
    // public void
    // testProcessSourceAccountingLineSufficientFundsCheckingPreparation_balanceTypeCode_Actual_accountSufficientFundsCode_cashAtAccount_financialObjectCode_not_CashInBankCode()
    // throws Exception {
    // JournalVoucherDocument document = (JournalVoucherDocument) createDocument();
    // document.setBalanceTypeCode(Constants.BALANCE_TYPE_ACTUAL);
    //
    // SourceAccountingLine line = (SourceAccountingLine) LINE12.createSourceAccountingLine();
    // line.setFinancialObjectCode("0");
    // line.getAccount().setAccountSufficientFundsCode(Constants.SF_TYPE_CASH_AT_ACCOUNT);
    //
    // JournalVoucherDocumentRule rule = new JournalVoucherDocumentRule();
    //
    // SufficientFundsItem item = rule.processSourceAccountingLineSufficientFundsCheckingPreparation(document, line);
    //
    // assertNull(item);
    // }
    /**
     * @throws Exception
     */
    // public void
    // testProcessSourceAccountingLineSufficientFundsCheckingPreparation_balanceTypeCode_Actual_accountSufficientFundsCode_cashAtAccount_financialObjectCode_cashInBank()
    // throws Exception {
    // JournalVoucherDocument document = (JournalVoucherDocument) createDocument();
    // document.setBalanceTypeCode(Constants.BALANCE_TYPE_ACTUAL);
    //
    // SourceAccountingLine line = (SourceAccountingLine) LINE12.createSourceAccountingLine();
    // line.setFinancialObjectCode(line.getChart().getFinancialCashObjectCode());
    // line.getAccount().setAccountSufficientFundsCode(Constants.SF_TYPE_CASH_AT_ACCOUNT);
    // // used arbitrary value to ensure what goes in is what goes out. if the 'standard' C/D values were used there is now way of
    // // knowing if the code changed it.
    // String debitCreditCode = "M";
    // line.setDebitCreditCode(debitCreditCode);
    //
    // JournalVoucherDocumentRule rule = new JournalVoucherDocumentRule();
    //
    // SufficientFundsItem item = rule.processSourceAccountingLineSufficientFundsCheckingPreparation(document, line);
    //
    // assertNotNull(item);
    // assertEquals(debitCreditCode, item.getDebitCreditCode());
    // }
    /**
     * @throws Exception
     */
    // public void
    // testProcessSourceAccountingLineSufficientFundsCheckingPreparation_balanceTypeCode_Actual_accountSufficientFundsCode_not_cashAtAccount_debitCreditCode_D()
    // throws Exception {
    // JournalVoucherDocument document = (JournalVoucherDocument) createDocument();
    // document.setBalanceTypeCode(Constants.BALANCE_TYPE_ACTUAL);
    //
    // SourceAccountingLine line = (SourceAccountingLine) LINE12.createSourceAccountingLine();
    // line.getAccount().setAccountSufficientFundsCode(Constants.SF_TYPE_ACCOUNT);
    // line.setDebitCreditCode(GL_DEBIT_CODE);
    //
    // JournalVoucherDocumentRule rule = new JournalVoucherDocumentRule();
    //
    // SufficientFundsItem item = rule.processSourceAccountingLineSufficientFundsCheckingPreparation(document, line);
    //
    // assertNotNull(item);
    // assertEquals(GL_CREDIT_CODE, item.getDebitCreditCode());
    // }
    /**
     * @throws Exception
     */
    // public void
    // testProcessSourceAccountingLineSufficientFundsCheckingPreparation_balanceTypeCode_Actual_accountSufficientFundsCode_not_cashAtAccount_debitCreditCode_C()
    // throws Exception {
    // JournalVoucherDocument document = (JournalVoucherDocument) createDocument();
    // document.setBalanceTypeCode(Constants.BALANCE_TYPE_ACTUAL);
    //
    // SourceAccountingLine line = (SourceAccountingLine) LINE12.createSourceAccountingLine();
    // line.getAccount().setAccountSufficientFundsCode(Constants.SF_TYPE_ACCOUNT);
    // line.setDebitCreditCode(GL_CREDIT_CODE);
    //
    // JournalVoucherDocumentRule rule = new JournalVoucherDocumentRule();
    //
    // SufficientFundsItem item = rule.processSourceAccountingLineSufficientFundsCheckingPreparation(document, line);
    //
    // assertNotNull(item);
    // assertEquals(GL_DEBIT_CODE, item.getDebitCreditCode());
    // }
    /**
     * @throws Exception
     */
    public void testProcessCustomRouteDocumentBusinessRules_accountingLines_notMatching_budgetYear() throws Exception {
        JournalVoucherDocument document = (JournalVoucherDocument) createDocumentValidForRouting();
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

    /**
     * @throws Exception
     */
    public void testProcessCustomRouteDocumentBusinessRules_accountingLines_matching_budgetYear() throws Exception {
        JournalVoucherDocument document = (JournalVoucherDocument) createDocumentValidForRouting();
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
     * tests that true is returned for a debit code
     * 
     * @throws Exception
     */
    public void testIsDebit_debitCode() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), JournalVoucherDocument.class);
        AccountingLine accountingLine = (AccountingLine) transactionalDocument.getSourceAccountingLineClass().newInstance();
        accountingLine.setDebitCreditCode(GL_DEBIT_CODE);

        assertTrue(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests that false is retured for a credit code
     * 
     * @throws Exception
     */
    public void testIsDebit_creditCode() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), JournalVoucherDocument.class);
        AccountingLine accountingLine = (AccountingLine) transactionalDocument.getSourceAccountingLineClass().newInstance();
        accountingLine.setDebitCreditCode(GL_CREDIT_CODE);

        assertFalse(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests that true is returned for a blank value
     * 
     * @throws Exception
     */
    public void testIsDebit_blankValue() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), JournalVoucherDocument.class);
        AccountingLine accountingLine = (AccountingLine) transactionalDocument.getSourceAccountingLineClass().newInstance();
        accountingLine.setDebitCreditCode(" ");

        assertTrue(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));

    }

    /**
     * tests that an error correction document returns false for a credit code
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_crediCode() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), JournalVoucherDocument.class);
        AccountingLine accountingLine = (AccountingLine) transactionalDocument.getSourceAccountingLineClass().newInstance();
        accountingLine.setDebitCreditCode(GL_CREDIT_CODE);

        assertFalse(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests that true is returned for an error correction document for a debit code
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_debitCode() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), JournalVoucherDocument.class);
        AccountingLine accountingLine = (AccountingLine) transactionalDocument.getSourceAccountingLineClass().newInstance();
        accountingLine.setDebitCreditCode(GL_DEBIT_CODE);

        assertTrue(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests that true is returned for a blank value error correction
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_blankValue() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), JournalVoucherDocument.class);
        AccountingLine accountingLine = (AccountingLine) transactionalDocument.getSourceAccountingLineClass().newInstance();
        accountingLine.setDebitCreditCode(" ");

        assertTrue(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));

    }
    // /////////////////////////////////////////////////////////////////////////
    // Test Methods End Here //
    // /////////////////////////////////////////////////////////////////////////
}
