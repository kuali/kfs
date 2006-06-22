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

import static org.kuali.module.financial.rules.IsDebitTestUtils.Amount.NEGATIVE;
import static org.kuali.module.financial.rules.IsDebitTestUtils.Amount.POSITIVE;

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
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.financial.document.TransferOfFundsDocument;
import org.kuali.module.gl.bo.GeneralLedgerPendingEntry;
import org.kuali.module.gl.util.SufficientFundsItemHelper.SufficientFundsItem;
import org.kuali.test.KualiTestBaseWithFixtures;
import org.kuali.test.parameters.AccountingLineParameter;
import org.kuali.test.parameters.TransactionalDocumentParameter;

/**
 * This class tests the Transfer of Funds Document's persistence, routing, and PE generation.
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class TransferOfFundsDocumentRuleTest extends TransactionalDocumentRuleTestBase {

    private static final String COLLECTION_NAME = "TransferOfFundsDocumentRuleTest.collection1";
    private static final String KNOWN_DOCUMENT_TYPENAME = "KualiTransferOfFundsDocument";

    private static final String SUFF_FUNDS_CHECKING_SOURCE_EXPENSE = "TOFSufficientFundsCheckingPreparation_source_Expense_1";
    private static final String SUFF_FUNDS_CHECKING_SOURCE_ASSET = "TOFSufficientFundsCheckingPreparation_source_Asset_1";
    private static final String SUFF_FUNDS_CHECKING_SOURCE_LIABILITY = "TOFSufficientFundsCheckingPreparation_source_Liability_1";
    private static final String SUFF_FUNDS_CHECKING_SOURCE_INCOME = "TOFSufficientFundsCheckingPreparation_source_Income_1";
    private static final String SUFF_FUNDS_CHECKING_TARGET_EXPENSE = "TOFSufficientFundsCheckingPreparation_target_Expense_1";
    private static final String SUFF_FUNDS_CHECKING_TARGET_ASSET = "TOFSufficientFundsCheckingPreparation_target_Asset_1";
    private static final String SUFF_FUNDS_CHECKING_TARGET_LIABILITY = "TOFSufficientFundsCheckingPreparation_target_Liability_1";
    private static final String SUFF_FUNDS_CHECKING_TARGET_INCOME = "TOFSufficientFundsCheckingPreparation_target_Income_1";

    private static final String[] FIXTURE_COLLECTION_NAMES = { COLLECTION_NAME };

    private TransactionalDocumentParameter _docParam1;
    private TransactionalDocumentParameter _docParam2;
    private TransactionalDocumentParameter _documentParameter5;
    private AccountingLineParameter _sourceLine1;
    private AccountingLineParameter _sourceLine2;
    private AccountingLineParameter _sourceLine3;
    private AccountingLineParameter _sourceLine4;
    private AccountingLineParameter _sourceLine5;
    private AccountingLineParameter _sourceLine6;
    private AccountingLineParameter _assetSourceLine;
    private AccountingLineParameter _targetLine1;
    private AccountingLineParameter _targetLine2;
    private AccountingLineParameter _targetLine3;
    private GeneralLedgerPendingEntry _expectedExpSourceGlEntry;
    private GeneralLedgerPendingEntry _expectedExpTargetGlEntry;
    private GeneralLedgerPendingEntry _expectedOffSourceGlEntry;
    private GeneralLedgerPendingEntry _expectedOffTargetGlEntry;


    /**
     * @see org.kuali.test.KualiTestBaseWithFixtures#getFixtureCollectionNames()
     */
    @Override
    public String[] getFixtureCollectionNames() {
        return FIXTURE_COLLECTION_NAMES;
    }

    /**
     * @see KualiTestBaseWithFixtures#isMockConfigurationServiceRequired()
     */
    @Override
    protected boolean isMockConfigurationServiceRequired() {
        return true;
    }

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
        return (TargetAccountingLine) getTargetLineParameter1().createLine();
    }

    @Override
    protected final TargetAccountingLine getValidObjectSubTypeTargetLine() throws Exception {
        return (TargetAccountingLine) getTargetLineParameter1().createLine();
    }

    @Override
    protected final TargetAccountingLine getInvalidObjectSubTypeTargetLine() throws Exception {
        return (TargetAccountingLine) getTargetLineParameter3().createLine();
    }

    @Override
    protected final List getValidObjectSubTypeSourceLines() throws Exception {
        List retval = new ArrayList();
        retval.add(getSourceLineParameter6().createLine());
        return retval;
    }

    @Override
    protected final List getInvalidObjectSubTypeSourceLines() throws Exception {
        List retval = new ArrayList();
        retval.add(getSourceLineParameter1().createLine());
        retval.add(getSourceLineParameter2().createLine());
        return retval;
    }

    @Override
    protected final List getInvalidObjectSubTypeTargetLines() throws Exception {
        List retval = new ArrayList();
        retval.add(getTargetLineParameter1().createLine());
        retval.add(getTargetLineParameter3().createLine());
        return retval;
    }

    @Override
    protected final List getValidObjectSubTypeTargetLines() throws Exception {
        List retval = new ArrayList();
        retval.add(getTargetLineParameter2().createLine());
        retval.add(getTargetLineParameter2().createLine());
        return retval;
    }

    @Override
    protected final SourceAccountingLine getValidObjectTypeSourceLine() throws Exception {
        return (SourceAccountingLine) getSourceLineParameter4().createLine();
    }

    @Override
    protected final SourceAccountingLine getInvalidObjectTypeSourceLine() throws Exception {
        return (SourceAccountingLine) getSourceLineParameter3().createLine();
    }

    @Override
    protected final SourceAccountingLine getInvalidObjectCodeSourceLine() throws Exception {
        return (SourceAccountingLine) getSourceLineParameter5().createLine();
    }

    @Override
    protected final SourceAccountingLine getValidObjectCodeSourceLine() throws Exception {
        return (SourceAccountingLine) getSourceLineParameter6().createLine();
    }

    @Override
    public final SourceAccountingLine getAssetSourceLine() {
        return (SourceAccountingLine) getAssetSourceLineParameter().createLine();
    }

    @Override
    protected final Document createDocument() throws Exception {
        return getDocumentParameter1().createDocument(getDocumentService());
    }

    @Override
    protected final TransactionalDocument createDocument5() throws Exception {
        return (TransactionalDocument) getDocumentParameter5().createDocument(getDocumentService());
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
        return getDocumentParameterNoDescription().createDocument(getDocumentService());
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
        retval.setSourceAccountingLines(getInvalidObjectSubTypeSourceLines());
        retval.addTargetAccountingLine(getValidObjectSubTypeTargetLine());
        return retval;
    }

    @Override
    protected final Document createDocumentInvalidDescription() throws Exception {
        return getDocumentParameterNoDescription().createDocument(getDocumentService());
    }

    @Override
    protected final TransactionalDocument createDocumentWithValidObjectSubType() throws Exception {
        TransferOfFundsDocument retval = (TransferOfFundsDocument) createDocument();
        retval.setSourceAccountingLines(getValidObjectSubTypeSourceLines());
        retval.setTargetAccountingLines(getValidObjectSubTypeTargetLines());
        return retval;
    }

    /**
     * Accessor for fixture 'sourceLine1'
     * 
     * @return AccountingLineParameter
     */
    public final AccountingLineParameter getSourceLineParameter1() {
        return _sourceLine1;
    }

    /**
     * Accessor for fixture 'sourceLine1'
     * 
     * @param p AccountingLineParameter
     */
    public final void setSourceLineParameter1(AccountingLineParameter p) {
        _sourceLine1 = p;
    }

    /**
     * Accessor for fixture 'sourceLine2'
     * 
     * @return AccountingLineParameter
     */
    public final AccountingLineParameter getSourceLineParameter2() {
        return _sourceLine2;
    }

    /**
     * Accessor for fixture 'sourceLine2'
     * 
     * @param p AccountingLineParameter
     */
    public final void setSourceLineParameter2(AccountingLineParameter p) {
        _sourceLine2 = p;
    }

    /**
     * Accessor for fixture 'sourceLine3'
     * 
     * @return AccountingLineParameter
     */
    public final AccountingLineParameter getSourceLineParameter3() {
        return _sourceLine3;
    }

    /**
     * Accessor for fixture 'sourceLine3'
     * 
     * @param p AccountingLineParameter
     */
    public final void setSourceLineParameter3(AccountingLineParameter p) {
        _sourceLine3 = p;
    }

    /**
     * Accessor for fixture 'sourceLine4'
     * 
     * @param p AccountingLineParameter
     */
    public final void setSourceLineParameter4(AccountingLineParameter p) {
        _sourceLine4 = p;
    }

    /**
     * Accessor for fixture 'sourceLine4'
     * 
     * @return AccountingLineParameter
     */
    public final AccountingLineParameter getSourceLineParameter4() {
        return _sourceLine4;
    }

    /**
     * Accessor for fixture 'sourceLine5'
     * 
     * @return AccountingLineParameter
     */
    public final AccountingLineParameter getSourceLineParameter5() {
        return _sourceLine5;
    }

    /**
     * Accessor for fixture 'sourceLine5'
     * 
     * @param p AccountingLineParameter
     */
    public final void setSourceLineParameter5(AccountingLineParameter p) {
        _sourceLine5 = p;
    }

    /**
     * Accessor for fixture 'sourceLine6'
     * 
     * @return AccountingLineParameter
     */
    public final AccountingLineParameter getSourceLineParameter6() {
        return _sourceLine6;
    }

    /**
     * Accessor for fixture 'sourceLine6'
     * 
     * @param p AccountingLineParameter
     */
    public final void setSourceLineParameter6(AccountingLineParameter p) {
        _sourceLine6 = p;
    }

    /**
     * Accessor for fixture 'assetSourceLine'
     * 
     * @return AccountingLineParameter
     */
    public final AccountingLineParameter getAssetSourceLineParameter() {
        return _assetSourceLine;
    }

    /**
     * Accessor for fixture 'assetSourceLine'
     * 
     * @param p AccountingLineParameter
     */
    public final void setAssetSourceLineParameter(AccountingLineParameter p) {
        _assetSourceLine = p;
    }

    /**
     * Accessor for fixture 'targetLine1'
     * 
     * @return AccountingLineParameter
     */
    public final AccountingLineParameter getTargetLineParameter1() {
        return _targetLine1;
    }

    /**
     * Accessor for fixture 'targetLine1'
     * 
     * @param p AccountingLineParameter
     */
    public final void setTargetLineParameter1(AccountingLineParameter p) {
        _targetLine1 = p;
    }

    /**
     * Accessor for fixture 'targetLine2'
     * 
     * @return AccountingLineParameter
     */
    public final AccountingLineParameter getTargetLineParameter2() {
        return _targetLine2;
    }

    /**
     * Accessor for fixture 'targetLine2'
     * 
     * @param p AccountingLineParameter
     */
    public final void setTargetLineParameter2(AccountingLineParameter p) {
        _targetLine2 = p;
    }

    /**
     * Accessor for fixture 'targetLine3'
     * 
     * @return AccountingLineParameter
     */
    public final AccountingLineParameter getTargetLineParameter3() {
        return _targetLine3;
    }

    /**
     * Accessor for fixture 'targetLine3'
     * 
     * @param p AccountingLineParameter
     */
    public final void setTargetLineParameter3(AccountingLineParameter p) {
        _targetLine3 = p;
    }

    public final TransactionalDocumentParameter getDocumentParameter1() {
        return _docParam1;
    }

    public final void setDocumentParameter1(TransactionalDocumentParameter p) {
        _docParam1 = p;
    }

    public final TransactionalDocumentParameter getDocumentParameterNoDescription() {
        return _docParam2;
    }

    public final void setDocumentParameterNoDescription(TransactionalDocumentParameter p) {
        _docParam2 = p;
    }

    public final TransactionalDocumentParameter getDocumentParameter5() {
        return _documentParameter5;
    }

    public final void setDocumentParameter5(TransactionalDocumentParameter p) {
        _documentParameter5 = p;
    }

    /**
     * Accessor method for Explicit Source fixture used for testProcessGeneralLedgerPendingEntries test methods.
     * 
     * @return GeneralLedgerPendingEntry pending entry fixture
     */
    @Override
    public final GeneralLedgerPendingEntry getExpectedExplicitSourcePendingEntry() {
        return _expectedExpSourceGlEntry;
    }

    /**
     * Accessor method for Explicit Source fixture used for testProcessGeneralLedgerPendingEntries test methods.
     * 
     * @param e pending entry fixture
     */
    public final void setExpectedExplicitSourcePendingEntry(GeneralLedgerPendingEntry e) {
        _expectedExpSourceGlEntry = e;
    }

    /**
     * Accessor method for Explicit Target fixture used for testProcessGeneralLedgerPendingEntries test methods.
     * 
     * @return e pending entry fixture
     */
    @Override
    public final GeneralLedgerPendingEntry getExpectedExplicitTargetPendingEntry() {
        return _expectedExpTargetGlEntry;
    }

    /**
     * Accessor method for Explicit Target fixture used for testProcessGeneralLedgerPendingEntries test methods.
     * 
     * @param e pending entry fixture
     */
    public final void setExpectedExplicitTargetPendingEntry(GeneralLedgerPendingEntry e) {
        _expectedExpTargetGlEntry = e;
    }

    /**
     * Accessor method for Offset Target fixture used for testProcessGeneralLedgerPendingEntries test methods.
     * 
     * @return GeneralLedgerPendingEntry pending entry fixture
     */
    @Override
    public final GeneralLedgerPendingEntry getExpectedOffsetTargetPendingEntry() {
        return _expectedOffTargetGlEntry;
    }

    /**
     * Accessor method for Offset Target fixture used for testProcessGeneralLedgerPendingEntries test methods.
     * 
     * @param e pending entry fixture
     */
    public final void setExpectedOffsetTargetPendingEntry(GeneralLedgerPendingEntry e) {
        _expectedOffTargetGlEntry = e;
    }

    /**
     * Accessor method for Offset Source fixture used for testProcessGeneralLedgerPendingEntries test methods.
     * 
     * @param e pending entry fixture
     */
    public final void setExpectedOffsetSourcePendingEntry(GeneralLedgerPendingEntry e) {
        _expectedOffSourceGlEntry = e;
    }

    /**
     * Accessor method for Offset Source fixture used for testProcessGeneralLedgerPendingEntries test methods.
     * 
     * @return GeneralLedgerPendingEntry pending entry fixture
     */
    @Override
    public final GeneralLedgerPendingEntry getExpectedOffsetSourcePendingEntry() {
        return _expectedOffSourceGlEntry;
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
            sourceAccountingLine.setFinancialObjectCode(getFixtureEntry("nonMandatoryTransferObjectCodeForUAChart").getValue());
        }

        for (int i = 0; i < doc.getTargetAccountingLines().size(); i++) {
            TargetAccountingLine sourceAccountingLine = (TargetAccountingLine) doc.getTargetAccountingLines().get(i);
            sourceAccountingLine.setFinancialObjectCode(getFixtureEntry("nonMandatoryTransferObjectCodeForUAChart").getValue());
        }

        testAddAccountingLine(doc, true);
    }

    public void testProcessGenerateGeneralLedgerPendingEntries_validSourceExpenseFlexibleOffset() throws Exception {
        SpringServiceLocator.getFlexibleOffsetAccountService().setKualiConfigurationService(createMockConfigurationService(true));
        testProcessGenerateGeneralLedgerPendingEntries(createLineFromFixture("flexibleExpenseSourceLine"), "expectedFlexibleExplicitSourcePendingEntryForExpense", "expectedFlexibleOffsetSourcePendingEntry");
    }

    public void testProcessGenerateGeneralLedgerPendingEntries_validSourceExpenseMissingOffsetDefinition() throws Exception {
        SpringServiceLocator.getFlexibleOffsetAccountService().setKualiConfigurationService(createMockConfigurationService(true));
        testProcessGenerateGeneralLedgerPendingEntries(createDocumentFromParameter("tof2000documentParameter"), createLineFromFixture("flexibleExpenseSourceLine"), "expectedFlexibleExplicitSourcePendingEntryForExpense", "expectedFlexibleOffsetSourcePendingEntryMissingOffsetDefinition", false);
        assertGlobalErrorMapContains(Constants.ACCOUNTING_LINE_ERRORS, KeyConstants.ERROR_DOCUMENT_NO_OFFSET_DEFINITION);
    }


    public void testProcessSourceAccountingLineSufficientFundsCheckingPreparation_isExpense_postive_lineAmount() {
        SourceAccountingLine line = (SourceAccountingLine) getFixtureEntryFromCollection(COLLECTION_NAME, SUFF_FUNDS_CHECKING_SOURCE_EXPENSE).createObject();
        line.setAmount(new KualiDecimal("3.0"));

        TransferOfFundsDocumentRule rule = new TransferOfFundsDocumentRule();
        SufficientFundsItem item = rule.processSourceAccountingLineSufficientFundsCheckingPreparation(null, line);

        assertEquals(SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue(TransferOfFundsDocumentRuleConstants.KUALI_TRANSACTION_PROCESSING_TRANSFER_OF_FUNDS_SECURITY_GROUPING, TransferOfFundsDocumentRuleConstants.TRANSFER_OF_FUNDS_EXPENSE_OBJECT_TYPE_CODE), item.getFinancialObjectTypeCode());
        assertEquals(Constants.GL_CREDIT_CODE, item.getDebitCreditCode());
        assertTrue(line.getAmount().equals(item.getAmount()));
    }

    public void testProcessSourceAccountingLineSufficientFundsCheckingPreparation_isAsset__negative_lineAmount() {
        SourceAccountingLine line = (SourceAccountingLine) getFixtureEntryFromCollection(COLLECTION_NAME, SUFF_FUNDS_CHECKING_SOURCE_ASSET).createObject();
        line.setAmount(new KualiDecimal("-3.0"));

        TransferOfFundsDocumentRule rule = new TransferOfFundsDocumentRule();
        SufficientFundsItem item = rule.processSourceAccountingLineSufficientFundsCheckingPreparation(null, line);

        assertEquals(line.getObjectTypeCode(), item.getFinancialObjectTypeCode());
        assertEquals(Constants.GL_DEBIT_CODE, item.getDebitCreditCode());
        assertFalse(line.getAmount().equals(item.getAmount()));
        assertTrue(line.getAmount().abs().equals(item.getAmount()));
    }

    public void testProcessSourceAccountingLineSufficientFundsCheckingPreparation_isIncome_postive_lineAmount() {
        SourceAccountingLine line = (SourceAccountingLine) getFixtureEntryFromCollection(COLLECTION_NAME, SUFF_FUNDS_CHECKING_SOURCE_INCOME).createObject();
        line.setAmount(new KualiDecimal("3.0"));

        TransferOfFundsDocumentRule rule = new TransferOfFundsDocumentRule();
        SufficientFundsItem item = rule.processSourceAccountingLineSufficientFundsCheckingPreparation(null, line);

        assertEquals(SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue(TransferOfFundsDocumentRuleConstants.KUALI_TRANSACTION_PROCESSING_TRANSFER_OF_FUNDS_SECURITY_GROUPING, TransferOfFundsDocumentRuleConstants.TRANSFER_OF_FUNDS_INCOME_OBJECT_TYPE_CODE), item.getFinancialObjectTypeCode());
        assertEquals(Constants.GL_CREDIT_CODE, item.getDebitCreditCode());
        assertTrue(line.getAmount().equals(item.getAmount()));
    }

    public void testProcessSourceAccountingLineSufficientFundsCheckingPreparation_isLiability__negative_lineAmount() {
        SourceAccountingLine line = (SourceAccountingLine) getFixtureEntryFromCollection(COLLECTION_NAME, SUFF_FUNDS_CHECKING_SOURCE_LIABILITY).createObject();
        line.setAmount(new KualiDecimal("-3.0"));

        TransferOfFundsDocumentRule rule = new TransferOfFundsDocumentRule();
        SufficientFundsItem item = rule.processSourceAccountingLineSufficientFundsCheckingPreparation(null, line);

        assertEquals(line.getObjectTypeCode(), item.getFinancialObjectTypeCode());
        assertEquals(Constants.GL_DEBIT_CODE, item.getDebitCreditCode());
        assertFalse(line.getAmount().equals(item.getAmount()));
        assertTrue(line.getAmount().abs().equals(item.getAmount()));
    }

    public void testProcessTargetAccountingLineSufficientFundsCheckingPreparation_isExpense_postive_lineAmount() {
        TargetAccountingLine line = (TargetAccountingLine) getFixtureEntryFromCollection(COLLECTION_NAME, SUFF_FUNDS_CHECKING_TARGET_EXPENSE).createObject();
        line.setAmount(new KualiDecimal("3.0"));

        TransferOfFundsDocumentRule rule = new TransferOfFundsDocumentRule();
        SufficientFundsItem item = rule.processTargetAccountingLineSufficientFundsCheckingPreparation(null, line);

        assertEquals(SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue(TransferOfFundsDocumentRuleConstants.KUALI_TRANSACTION_PROCESSING_TRANSFER_OF_FUNDS_SECURITY_GROUPING, TransferOfFundsDocumentRuleConstants.TRANSFER_OF_FUNDS_EXPENSE_OBJECT_TYPE_CODE), item.getFinancialObjectTypeCode());
        assertEquals(Constants.GL_DEBIT_CODE, item.getDebitCreditCode());
        assertTrue(line.getAmount().equals(item.getAmount()));
    }

    public void testProcessTargetAccountingLineSufficientFundsCheckingPreparation_isAsset__negative_lineAmount() {
        TargetAccountingLine line = (TargetAccountingLine) getFixtureEntryFromCollection(COLLECTION_NAME, SUFF_FUNDS_CHECKING_TARGET_ASSET).createObject();
        line.setAmount(new KualiDecimal("-3.0"));

        TransferOfFundsDocumentRule rule = new TransferOfFundsDocumentRule();
        SufficientFundsItem item = rule.processTargetAccountingLineSufficientFundsCheckingPreparation(null, line);

        assertEquals(line.getObjectTypeCode(), item.getFinancialObjectTypeCode());
        assertEquals(Constants.GL_CREDIT_CODE, item.getDebitCreditCode());
        assertFalse(line.getAmount().equals(item.getAmount()));
        assertTrue(line.getAmount().abs().equals(item.getAmount()));
    }

    public void testProcessTargetAccountingLineSufficientFundsCheckingPreparation_isIncome_postive_lineAmount() {
        TargetAccountingLine line = (TargetAccountingLine) getFixtureEntryFromCollection(COLLECTION_NAME, SUFF_FUNDS_CHECKING_TARGET_INCOME).createObject();
        line.setAmount(new KualiDecimal("3.0"));

        TransferOfFundsDocumentRule rule = new TransferOfFundsDocumentRule();
        SufficientFundsItem item = rule.processTargetAccountingLineSufficientFundsCheckingPreparation(null, line);

        assertEquals(SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue(TransferOfFundsDocumentRuleConstants.KUALI_TRANSACTION_PROCESSING_TRANSFER_OF_FUNDS_SECURITY_GROUPING, TransferOfFundsDocumentRuleConstants.TRANSFER_OF_FUNDS_INCOME_OBJECT_TYPE_CODE), item.getFinancialObjectTypeCode());
        assertEquals(Constants.GL_DEBIT_CODE, item.getDebitCreditCode());
        assertTrue(line.getAmount().equals(item.getAmount()));
    }

    public void testProcessTargetAccountingLineSufficientFundsCheckingPreparation_isLiability__negative_lineAmount() {
        TargetAccountingLine line = (TargetAccountingLine) getFixtureEntryFromCollection(COLLECTION_NAME, SUFF_FUNDS_CHECKING_TARGET_LIABILITY).createObject();
        line.setAmount(new KualiDecimal("-3.0"));

        TransferOfFundsDocumentRule rule = new TransferOfFundsDocumentRule();
        SufficientFundsItem item = rule.processTargetAccountingLineSufficientFundsCheckingPreparation(null, line);

        assertEquals(line.getObjectTypeCode(), item.getFinancialObjectTypeCode());
        assertEquals(Constants.GL_CREDIT_CODE, item.getDebitCreditCode());
        assertFalse(line.getAmount().equals(item.getAmount()));
        assertTrue(line.getAmount().abs().equals(item.getAmount()));
    }

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

    // /////////////////////////////////////////////////////////////////////////
    // Test Methods End Here //
    // /////////////////////////////////////////////////////////////////////////
}
