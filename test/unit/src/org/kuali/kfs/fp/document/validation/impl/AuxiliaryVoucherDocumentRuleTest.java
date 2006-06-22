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

import org.kuali.core.bo.AccountingLine;
import org.kuali.core.bo.SourceAccountingLine;
import org.kuali.core.bo.TargetAccountingLine;
import org.kuali.core.document.Document;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.rule.TransactionalDocumentRuleTestBase;
import org.kuali.module.financial.document.AuxiliaryVoucherDocument;
import org.kuali.module.financial.rules.TransactionalDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE;
import org.kuali.module.gl.bo.GeneralLedgerPendingEntry;
import org.kuali.test.parameters.AccountingLineParameter;
import org.kuali.test.parameters.TransactionalDocumentParameter;


/**
 * This class tests the <code>{@link AuxiliaryVoucherDocument}</code>'s rules and PE generation.
 * 
 * @author Kuali Transaction Processing Team (kualidev@oncourse.iu.edu)
 */
public class AuxiliaryVoucherDocumentRuleTest extends TransactionalDocumentRuleTestBase {

    private static final String COLLECTION_NAME = "AuxiliaryVoucherDocumentRuleTest.collection1";
    private static final String KNOWN_DOCUMENT_TYPENAME = "KualiAuxiliaryVoucherDocument";

    private static final String[] FIXTURE_COLLECTION_NAMES = { COLLECTION_NAME };

    private TransactionalDocumentParameter _docParam2;
    private TransactionalDocumentParameter _docParam1;
    private TransactionalDocumentParameter _avParam;
    private AccountingLineParameter _sourceLine1;
    private AccountingLineParameter _sourceLine2;
    private AccountingLineParameter _sourceLine3;
    private AccountingLineParameter _sourceLine4;
    private AccountingLineParameter _sourceLine5;
    private AccountingLineParameter _sourceLine6;
    private AccountingLineParameter _sickPaySourceLine;
    private AccountingLineParameter _fundBalanceSourceLine;
    private AccountingLineParameter _targetLine1;
    private AccountingLineParameter _targetLine2;
    private AccountingLineParameter _targetLine3;
    private AccountingLineParameter _sickPayTargetLine;
    private AccountingLineParameter _fundBalanceTargetLine;
    private GeneralLedgerPendingEntry _expectedExpSourceGlEntryExpense;
    private GeneralLedgerPendingEntry _expectedExpTargetGlEntryExpense;
    private GeneralLedgerPendingEntry _expectedExpSourceGlEntry;
    private GeneralLedgerPendingEntry _expectedExpTargetGlEntry;
    private GeneralLedgerPendingEntry _expectedOffSourceGlEntry;
    private GeneralLedgerPendingEntry _expectedOffTargetGlEntry;

    /**
     * 
     * @see org.kuali.test.KualiTestBaseWithFixtures#getFixtureCollectionNames()
     */
    @Override
    public String[] getFixtureCollectionNames() {
        return FIXTURE_COLLECTION_NAMES;
    }

    // ////////////////////////////////////////////////////////////////////////
    // Fixture methods start here //
    // ////////////////////////////////////////////////////////////////////////

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
        return (TargetAccountingLine) getAccruedIncomeTargetLineParameter().createLine();
    }

    /**
     * 
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#getValidObjectSubTypeTargetLine()
     */
    @Override
    protected final TargetAccountingLine getValidObjectSubTypeTargetLine() throws Exception {
        return (TargetAccountingLine) getAccruedIncomeTargetLineParameter().createLine();
    }

    /**
     * 
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#getInvalidObjectSubTypeTargetLine()
     */
    @Override
    protected final TargetAccountingLine getInvalidObjectSubTypeTargetLine() throws Exception {
        return (TargetAccountingLine) getAccruedSickPayTargetLineParameter().createLine();
    }

    /**
     * 
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#getValidObjectSubTypeSourceLines()
     */
    @Override
    protected final List getValidObjectSubTypeSourceLines() throws Exception {
        List retval = new ArrayList();
        retval.add(getAccruedIncomeSourceLineParameter().createLine());
        retval.add(getAccruedIncomeSourceLineParameter().createLine());
        return retval;
    }

    /**
     * 
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#getInvalidObjectSubTypeSourceLines()
     */
    @Override
    protected final List getInvalidObjectSubTypeSourceLines() throws Exception {
        List retval = new ArrayList();
        retval.add(getAccruedSickPaySourceLineParameter().createLine());
        retval.add(getAccruedSickPaySourceLineParameter().createLine());
        return retval;
    }

    /**
     * 
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#getInvalidObjectSubTypeTargetLines()
     */
    @Override
    protected final List getInvalidObjectSubTypeTargetLines() throws Exception {
        List retval = new ArrayList();
        retval.add(getAccruedSickPayTargetLineParameter().createLine());
        retval.add(getAccruedSickPayTargetLineParameter().createLine());
        return retval;
    }

    /**
     * 
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#getValidObjectSubTypeTargetLines()
     */
    @Override
    protected final List getValidObjectSubTypeTargetLines() throws Exception {
        List retval = new ArrayList();
        retval.add(getAccruedIncomeTargetLineParameter().createLine());
        retval.add(getAccruedIncomeTargetLineParameter().createLine());
        return retval;
    }

    /**
     * 
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#getValidObjectTypeSourceLine()
     */
    @Override
    protected final SourceAccountingLine getValidObjectTypeSourceLine() throws Exception {
        return (SourceAccountingLine) getSourceLineParameter4().createLine();
    }

    /**
     * 
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#getInvalidObjectTypeSourceLine()
     */
    @Override
    protected final SourceAccountingLine getInvalidObjectTypeSourceLine() throws Exception {
        return (SourceAccountingLine) getFundBalanceSourceLineParameter().createLine();
    }

    /**
     * 
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#getInvalidObjectCodeSourceLine()
     */
    @Override
    protected final SourceAccountingLine getInvalidObjectCodeSourceLine() throws Exception {
        return (SourceAccountingLine) getSourceLineParameter5().createLine();
    }

    /**
     * 
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#getValidObjectCodeSourceLine()
     */
    @Override
    protected final SourceAccountingLine getValidObjectCodeSourceLine() throws Exception {
        return (SourceAccountingLine) getAccruedIncomeSourceLineParameter().createLine();
    }

    /**
     * 
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#getAssetSourceLine()
     */
    @Override
    public final SourceAccountingLine getAssetSourceLine() {
        return (SourceAccountingLine) getAccruedIncomeSourceLineParameter().createLine();
    }

    /**
     * 
     * @see org.kuali.core.rule.DocumentRuleTestBase#createDocument()
     */
    @Override
    protected final Document createDocument() throws Exception {
        return getAuxiliaryVoucherDocument().createDocument(getDocumentService());
    }

    /**
     * 
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#createDocument5()
     */
    @Override
    protected final TransactionalDocument createDocument5() throws Exception {
        return (TransactionalDocument) getAuxiliaryVoucherDocument().createDocument(getDocumentService());
    }

    /**
     * @see org.kuali.core.rule.DocumentRuleTestBase#createDocumentValidForRouting()
     */
    @Override
    protected final Document createDocumentValidForRouting() throws Exception {
        return createDocumentWithValidObjectSubType();
    }

    /**
     * @see org.kuali.core.rule.DocumentRuleTestBase#createDocumentInvalidForSave()
     */
    @Override
    protected final Document createDocumentInvalidForSave() throws Exception {
        return getDocumentParameterNoDescription().createDocument(getDocumentService());
    }

    /**
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#createDocumentWithInvalidObjectSubType()
     */
    @Override
    protected final TransactionalDocument createDocumentWithInvalidObjectSubType() throws Exception {
        AuxiliaryVoucherDocument retval = (AuxiliaryVoucherDocument) createDocument();
        retval.setSourceAccountingLines(getInvalidObjectSubTypeSourceLines());
        retval.setTargetAccountingLines(getInvalidObjectSubTypeTargetLines());
        return retval;
    }

    /**
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#createDocumentUnbalanced()
     */
    @Override
    protected final TransactionalDocument createDocumentUnbalanced() throws Exception {
        AuxiliaryVoucherDocument retval = (AuxiliaryVoucherDocument) createDocument();
        retval.setSourceAccountingLines(getInvalidObjectSubTypeSourceLines());
        retval.addTargetAccountingLine(getValidObjectSubTypeTargetLine());
        return retval;
    }

    /**
     * @see org.kuali.core.rule.DocumentRuleTestBase#createDocumentInvalidDescription()
     */
    @Override
    protected final Document createDocumentInvalidDescription() throws Exception {
        return getDocumentParameterNoDescription().createDocument(getDocumentService());
    }

    /**
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#createDocumentWithValidObjectSubType()
     */
    @Override
    protected final TransactionalDocument createDocumentWithValidObjectSubType() throws Exception {
        AuxiliaryVoucherDocument retval = (AuxiliaryVoucherDocument) createDocument();
        retval.setSourceAccountingLines(getValidObjectSubTypeSourceLines());
        retval.setTargetAccountingLines(getValidObjectSubTypeTargetLines());
        return retval;
    }

    /**
     * Accessor for fixture 'sourceLine1'
     * 
     * @return AccountingLineParameter
     */
    public final AccountingLineParameter getCashSourceLineParameter() {
        return _sourceLine1;
    }

    /**
     * Accessor for fixture 'sourceLine1'
     * 
     * @param p AccountingLineParameter
     */
    public final void setCashSourceLineParameter(AccountingLineParameter p) {
        _sourceLine1 = p;
    }

    /**
     * Accessor for fixture 'sourceLine2'
     * 
     * @return AccountingLineParameter
     */
    public final AccountingLineParameter getLossOnRetireSourceLineParameter() {
        return _sourceLine2;
    }

    /**
     * Accessor for fixture 'sourceLine2'
     * 
     * @param p AccountingLineParameter
     */
    public final void setLossOnRetireSourceLineParameter(AccountingLineParameter p) {
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
    public final AccountingLineParameter getAccruedIncomeSourceLineParameter() {
        return _sourceLine6;
    }

    /**
     * Accessor for fixture 'sourceLine6'
     * 
     * @param p AccountingLineParameter
     */
    public final void setAccruedIncomeSourceLineParameter(AccountingLineParameter p) {
        _sourceLine6 = p;
    }

    /**
     * Accessor for fixture 'sourceLine6'
     * 
     * @return AccountingLineParameter
     */
    public final AccountingLineParameter getAccruedSickPaySourceLineParameter() {
        return _sickPaySourceLine;
    }

    /**
     * Accessor for fixture 'sourceLine6'
     * 
     * @param p AccountingLineParameter
     */
    public final void setAccruedSickPaySourceLineParameter(AccountingLineParameter p) {
        _sickPaySourceLine = p;
    }

    /**
     * Accessor for fixture 'sourceLine6'
     * 
     * @return AccountingLineParameter
     */
    public final AccountingLineParameter getFundBalanceSourceLineParameter() {
        return _fundBalanceSourceLine;
    }

    /**
     * Accessor for fixture 'sourceLine6'
     * 
     * @param p AccountingLineParameter
     */
    public final void setFundBalanceSourceLineParameter(AccountingLineParameter p) {
        _fundBalanceSourceLine = p;
    }

    /**
     * Accessor for fixture of a <code>{@link TargetAccountingLine}</code> generated from an
     * <code>{@link AccountingLineParameter}</code> instance with a cash object code.
     * 
     * @return AccountingLineParameter
     */
    public final AccountingLineParameter getCashTargetLineParameter() {
        /*
         * return new AccountingLineParameter() .lineClassName(TargetAccountingLine.class.getName()) .chartOfAccounts("BA")
         * .accountNumber("6044900") .projectCode("BOB") .amount(new KualiDecimal("100")) .currentFiscalYear(new Integer(2004))
         * .financialObjectCode("8000");
         */
        return _targetLine1;
    }

    /**
     * Accessor for fixture of a <code>{@link TargetAccountingLine}</code> generated from an
     * <code>{@link AccountingLineParameter}</code> instance with a cash object code.
     * 
     * @param p
     */
    public final void getCashTargetLineParameter(AccountingLineParameter p) {
        _targetLine1 = p;
    }

    /**
     * Accessor for fixture 'targetLine2'
     * 
     * @return AccountingLineParameter
     */
    public final AccountingLineParameter getAccruedIncomeTargetLineParameter() {
        return _targetLine2;
    }

    /**
     * Accessor for fixture 'targetLine2'
     * 
     * @param p AccountingLineParameter
     */
    public final void setAccruedIncomeTargetLineParameter(AccountingLineParameter p) {
        _targetLine2 = p;
    }

    /**
     * Accessor for fixture 'targetLine2'
     * 
     * @return AccountingLineParameter
     */
    public final AccountingLineParameter getAccruedSickPayTargetLineParameter() {
        return _sickPayTargetLine;
    }

    /**
     * Accessor for fixture 'targetLine2'
     * 
     * @param p AccountingLineParameter
     */
    public final void setAccruedSickPayTargetLineParameter(AccountingLineParameter p) {
        _sickPayTargetLine = p;
    }

    /**
     * Accessor for fixture 'targetLine2'
     * 
     * @return AccountingLineParameter
     */
    public final AccountingLineParameter getFundBalanceTargetLineParameter() {
        return _fundBalanceTargetLine;
    }

    /**
     * Accessor for fixture 'targetLine2'
     * 
     * @param p AccountingLineParameter
     */
    public final void setFundBalanceTargetLineParameter(AccountingLineParameter p) {
        _fundBalanceTargetLine = p;
    }

    /**
     * Accessor for fixture 'targetLine3'
     * 
     * @return AccountingLineParameter
     */
    public final AccountingLineParameter getLossOnRetireTargetLineParameter() {
        return _targetLine3;
    }

    /**
     * Accessor for fixture 'targetLine3'
     * 
     * @param p AccountingLineParameter
     */
    public final void setLossOnRetireTargetLineParameter(AccountingLineParameter p) {
        _targetLine3 = p;
    }

    /**
     * @return <code>TransactionalDocumentParameter</code>
     */
    public final TransactionalDocumentParameter getDocumentParameter1() {
        return _docParam1;
    }

    /**
     * @param p
     */
    public final void setDocumentParameter1(TransactionalDocumentParameter p) {
        _docParam1 = p;
    }

    /**
     * @return <code>TransactionalDocumentParameter</code>
     */
    public final TransactionalDocumentParameter getDocumentParameterNoDescription() {
        return _docParam2;
    }

    /**
     * @param p
     */
    public final void setDocumentParameterNoDescription(TransactionalDocumentParameter p) {
        _docParam2 = p;
    }

    /**
     * Fixture method to get a <code>{@link TransactionalDocumentParameter}</code> instance for
     * <code>{@link AuxiliaryVoucherDocument}</code>.
     * 
     * @return TransactionalDocumentParameter
     */
    public final TransactionalDocumentParameter getAuxiliaryVoucherDocument() {
        return _avParam;
    }

    /**
     * Fixture method to assign to the test a <code>{@link TransactionalDocumentParameter}</code> instance for
     * <code>{@link AuxiliaryVoucherDocument}</code>.
     * 
     * @param p
     */
    public final void setAuxiliaryVoucherDocument(TransactionalDocumentParameter p) {
        _avParam = p;
    }

    /**
     * Accessor method for Explicit Source fixture used for testProcessGeneralLedgerPendingEntries test methods.
     * 
     * @return GeneralLedgerPendingEntry pending entry fixture
     */
    @Override
    public final GeneralLedgerPendingEntry getExpectedExplicitSourcePendingEntry() {
        return getExpectedGECExplicitSourcePendingEntry();
    }

    /**
     * Accessor method for Explicit Source fixture used for testProcessGeneralLedgerPendingEntries test methods.
     * 
     * @return GeneralLedgerPendingEntry pending entry fixture
     */
    public final GeneralLedgerPendingEntry getExpectedGECExplicitSourcePendingEntry() {
        return _expectedExpSourceGlEntry;
    }

    /**
     * Accessor method for Explicit Source fixture used for testProcessGeneralLedgerPendingEntries test methods.
     * 
     * @param e pending entry fixture
     */
    public final void setExpectedGECExplicitSourcePendingEntry(GeneralLedgerPendingEntry e) {
        _expectedExpSourceGlEntry = e;
    }

    /**
     * Accessor method for Explicit Source fixture used for testProcessGeneralLedgerPendingEntries test methods.
     * 
     * @return GeneralLedgerPendingEntry pending entry fixture
     */
    public final GeneralLedgerPendingEntry getExpectedGECExplicitSourcePendingEntryForExpense() {
        return _expectedExpSourceGlEntryExpense;
    }

    /**
     * Accessor method for Explicit Source fixture used for testProcessGeneralLedgerPendingEntries test methods.
     * 
     * @param e pending entry fixture
     */
    public final void setExpectedGECExplicitSourcePendingEntryForExpense(GeneralLedgerPendingEntry e) {
        _expectedExpSourceGlEntryExpense = e;
    }

    /**
     * Accessor method for Explicit Target fixture used for testProcessGeneralLedgerPendingEntries test methods.
     * 
     * @return GeneralLedgerPendingEntry pending entry fixture
     */
    @Override
    public final GeneralLedgerPendingEntry getExpectedExplicitTargetPendingEntry() {
        return getExpectedGECExplicitTargetPendingEntry();
    }

    /**
     * Accessor method for Explicit Target fixture used for testProcessGeneralLedgerPendingEntries test methods.
     * 
     * @return GeneralLedgerPendingEntry pending entry fixture
     */
    public final GeneralLedgerPendingEntry getExpectedGECExplicitTargetPendingEntry() {
        return _expectedExpTargetGlEntry;
    }

    /**
     * Accessor method for Explicit Target fixture used for testProcessGeneralLedgerPendingEntries test methods.
     * 
     * @param e pending entry fixture
     */
    public final void setExpectedGECExplicitTargetPendingEntry(GeneralLedgerPendingEntry e) {
        _expectedExpTargetGlEntry = e;
    }


    /**
     * Accessor method for Explicit Target fixture used for testProcessGeneralLedgerPendingEntries test methods.
     * 
     * @return GeneralLedgerPendingEntry pending entry fixture
     */
    public final GeneralLedgerPendingEntry getExpectedGECExplicitTargetPendingEntryForExpense() {
        return _expectedExpTargetGlEntryExpense;
    }

    /**
     * Accessor method for Explicit Target fixture used for testProcessGeneralLedgerPendingEntries test methods.
     * 
     * @param e pending entry fixture
     */
    public final void setExpectedGECExplicitTargetPendingEntryForExpense(GeneralLedgerPendingEntry e) {
        _expectedExpTargetGlEntryExpense = e;
    }

    /**
     * Accessor method for Offset Target fixture used for testProcessGeneralLedgerPendingEntries test methods.
     * 
     * @return GeneralLedgerPendingEntry pending entry fixture
     */
    public final GeneralLedgerPendingEntry getExpectedGECOffsetTargetPendingEntry() {
        return _expectedOffTargetGlEntry;
    }

    /**
     * Accessor method for Offset Target fixture used for testProcessGeneralLedgerPendingEntries test methods.
     * 
     * @return GeneralLedgerPendingEntry pending entry fixture
     */
    @Override
    public final GeneralLedgerPendingEntry getExpectedOffsetTargetPendingEntry() {
        return getExpectedGECOffsetTargetPendingEntry();
    }

    /**
     * Accessor method for Offset Target fixture used for testProcessGeneralLedgerPendingEntries} test methods.
     * 
     * @param e pending entry fixture
     */
    public final void setExpectedGECOffsetTargetPendingEntry(GeneralLedgerPendingEntry e) {
        _expectedOffTargetGlEntry = e;
    }

    /**
     * Accessor method for Offset Source fixture used for testProcessGeneralLedgerPendingEntries test methods.
     * 
     * @param e pending entry fixture
     */
    public final void setExpectedGECOffsetSourcePendingEntry(GeneralLedgerPendingEntry e) {
        _expectedOffSourceGlEntry = e;
    }

    /**
     * Accessor method for Offset Source fixture used for testProcessGeneralLedgerPendingEntries test methods.
     * 
     * @return GeneralLedgerPendingEntry pending entry fixture
     */
    public final GeneralLedgerPendingEntry getExpectedGECOffsetSourcePendingEntry() {
        return _expectedOffSourceGlEntry;
    }

    /**
     * Accessor method for Offset Source fixture used for testProcessGeneralLedgerPendingEntries test methods.
     * 
     * @return GeneralLedgerPendingEntry pending entry fixture
     */
    @Override
    public final GeneralLedgerPendingEntry getExpectedOffsetSourcePendingEntry() {
        return getExpectedGECOffsetSourcePendingEntry();
    }

    /**
     * Accessor method for Explicit Source fixture used for testProcessGeneralLedgerPendingEntries test methods.
     * 
     * @return GeneralLedgerPendingEntry pending entry fixture
     */
    @Override
    public GeneralLedgerPendingEntry getExpectedExplicitSourcePendingEntryForExpense() {
        return getExpectedGECExplicitSourcePendingEntryForExpense();
    }

    /**
     * Accessor method for Explicit Source fixture used for testProcessGeneralLedgerPendingEntries test methods.
     * 
     * @return GeneralLedgerPendingEntry pending entry fixture
     */
    @Override
    public GeneralLedgerPendingEntry getExpectedExplicitTargetPendingEntryForExpense() {
        return getExpectedGECExplicitTargetPendingEntryForExpense();
    }


    /**
     * 
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#getExpenseSourceLine()
     */
    @Override
    protected AccountingLine getExpenseSourceLine() {
        return createLineFromFixture("expenseGECSourceLine");
    }

    /**
     * 
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#getExpenseTargetLine()
     */
    @Override
    protected AccountingLine getExpenseTargetLine() {
        return createLineFromFixture("expenseGECTargetLine");
    }

    /**
     * Fixture method that returns a <code>{@link SourceAccountingLine}</code> for sufficient funds checking of an expense source
     * line
     * 
     * @return AccountingLineParameter
     */
    public final AccountingLineParameter getSufficientFundsCheckingSourceExpense() {
        return getAccruedIncomeSourceLineParameter();
    }

    /**
     * Fixture method that returns a <code>{@link SourceAccountingLine}</code> for sufficient funds checking of an asset source
     * line
     * 
     * @return AccountingLineParameter
     */
    public final AccountingLineParameter getSufficientFundsCheckingSourceAsset() {
        return getAccruedIncomeSourceLineParameter();
    }

    /**
     * Fixture method that returns a <code>{@link SourceAccountingLine}</code> for sufficient funds checking of a liability source
     * line
     * 
     * @return AccountingLineParameter
     */
    public final AccountingLineParameter getSufficientFundsCheckingSourceLiability() {
        return null;
    }

    /**
     * Fixture method that returns a <code>{@link SourceAccountingLine}</code> for sufficient funds checking of an income source
     * line
     * 
     * @return AccountingLineParameter
     */
    public final AccountingLineParameter getSufficientFundsCheckingSourceIncome() {
        return null;
    }

    /**
     * Fixture method that returns a <code>{@link TargetAccountingLine}</code> for sufficient funds checking of an expense source
     * line
     * 
     * @return AccountingLineParameter
     */
    public final AccountingLineParameter getSufficientFundsCheckingTargetExpense() {
        return getAccruedIncomeTargetLineParameter();
    }

    /**
     * Fixture method that returns a <code>{@link TargetAccountingLine}</code> for sufficient funds checking of an asset source
     * line
     * 
     * @return AccountingLineParameter
     */
    public final AccountingLineParameter getSufficientFundsCheckingTargetAsset() {
        return getAccruedIncomeTargetLineParameter();
    }

    /**
     * Fixture method that returns a <code>{@link TargetAccountingLine}</code> for sufficient funds checking of an liability
     * source line
     * 
     * @return AccountingLineParameter
     */
    public final AccountingLineParameter getSufficientFundsCheckingTargetLiability() {
        return null;
    }

    /**
     * Fixture method that returns a <code>{@link TargetAccountingLine}</code> for sufficient funds checking of an income source
     * line
     * 
     * @return AccountingLineParameter
     */
    public final AccountingLineParameter getSufficientFundsCheckingTargetIncome() {
        return null;
    }

    // ////////////////////////////////////////////////////////////////////////
    // Fixture methods end here //
    // ////////////////////////////////////////////////////////////////////////

    // ////////////////////////////////////////////////////////////////////////
    // Test methods start here //
    // ////////////////////////////////////////////////////////////////////////
    /**
     * tests that true is returned for a debit code
     * 
     * @throws Exception
     */
    public void testIsDebit_debitCode() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), AuxiliaryVoucherDocument.class);
        AccountingLine accountingLine = (AccountingLine) transactionalDocument.getSourceAccountingLineClass().newInstance();
        accountingLine.setDebitCreditCode(GENERAL_LEDGER_PENDING_ENTRY_CODE.DEBIT);

        assertTrue(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests that false is retured for a credit code
     * 
     * @throws Exception
     */
    public void testIsDebit_creditCode() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), AuxiliaryVoucherDocument.class);
        AccountingLine accountingLine = (AccountingLine) transactionalDocument.getSourceAccountingLineClass().newInstance();
        accountingLine.setDebitCreditCode(GENERAL_LEDGER_PENDING_ENTRY_CODE.CREDIT);

        assertFalse(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests that an <code>IllegalStateException</code> is thrown for a blank value
     * 
     * @throws Exception
     */
    public void testIsDebit_blankValue() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), AuxiliaryVoucherDocument.class);
        AccountingLine accountingLine = (AccountingLine) transactionalDocument.getSourceAccountingLineClass().newInstance();
        accountingLine.setDebitCreditCode(" ");

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests that an <code>IllegalStateException</code> is thrown for an error correction document
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), AuxiliaryVoucherDocument.class);
        AccountingLine accountingLine = (AccountingLine) transactionalDocument.getSourceAccountingLineClass().newInstance();
        accountingLine.setDebitCreditCode(GENERAL_LEDGER_PENDING_ENTRY_CODE.DEBIT);

        assertTrue(IsDebitTestUtils.isErrorCorrectionIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }
    // ////////////////////////////////////////////////////////////////////////
    // Test methods end here //
    // ////////////////////////////////////////////////////////////////////////
}
