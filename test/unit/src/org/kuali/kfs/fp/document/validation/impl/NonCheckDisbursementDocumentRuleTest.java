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

import org.kuali.core.bo.AccountingLine;
import org.kuali.core.bo.SourceAccountingLine;
import org.kuali.core.bo.TargetAccountingLine;
import org.kuali.core.document.Document;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.rule.TransactionalDocumentRuleTestBase;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.financial.document.NonCheckDisbursementDocument;
import org.kuali.module.gl.bo.GeneralLedgerPendingEntry;
import org.kuali.test.parameters.AccountingLineParameter;
import org.kuali.test.parameters.TransactionalDocumentParameter;


/**
 * This class tests the <code>{@link NonCheckDisbursementDocument}</code>'s rules and PE generation. This is not currently
 * implemented properly. When we get to building this document, we would need to extend TransactionalDocumentRuleTestBase. For now
 * it contains commented out old fixtures code that will need to be fitted to the new xml based fixtures framework.
 * 
 * @author Kuali Transaction Processing Team (kualidev@oncourse.iu.edu)
 */
public class NonCheckDisbursementDocumentRuleTest extends TransactionalDocumentRuleTestBase {

    private static final String COLLECTION_NAME = "NonCheckDisbursementDocumentRuleTest.collection1";
    private static final String KNOWN_DOCUMENT_TYPENAME = "KualiNonCheckDisbursementDocument";

    private static final String[] FIXTURE_COLLECTION_NAMES = { COLLECTION_NAME };

    private TransactionalDocumentParameter _docParam2;
    private TransactionalDocumentParameter _docParam1;
    private TransactionalDocumentParameter _ncdParam;
    private AccountingLineParameter _sourceLine1;
    private AccountingLineParameter _sourceLine2;
    private AccountingLineParameter _sourceLine3;
    private AccountingLineParameter _sourceLine4;
    private AccountingLineParameter _sourceLine5;
    private AccountingLineParameter _sourceLine6;
    private AccountingLineParameter _sickPaySourceLine;
    private AccountingLineParameter _assetSourceLine;
    private AccountingLineParameter _targetLine1;
    private AccountingLineParameter _targetLine2;
    private AccountingLineParameter _targetLine3;
    private AccountingLineParameter _sickPayTargetLine;
    private GeneralLedgerPendingEntry _expectedExpSourceGlEntryExpense;
    private GeneralLedgerPendingEntry _expectedExpTargetGlEntryExpense;
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
        return (SourceAccountingLine) getAccruedSickPaySourceLineParameter().createLine();
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
        return getNonCheckDisbursementDocument().createDocument(getDocumentService());
    }

    /**
     * 
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#createDocument5()
     */
    @Override
    protected final TransactionalDocument createDocument5() throws Exception {
        return (TransactionalDocument) getNonCheckDisbursementDocument().createDocument(getDocumentService());
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
        return getDocumentParameterNoDescription().createDocument(getDocumentService());
    }

    /**
     * 
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#createDocumentWithInvalidObjectSubType()
     */
    @Override
    protected final TransactionalDocument createDocumentWithInvalidObjectSubType() throws Exception {
        NonCheckDisbursementDocument retval = (NonCheckDisbursementDocument) createDocument();
        retval.setSourceAccountingLines(getInvalidObjectSubTypeSourceLines());
        retval.setTargetAccountingLines(getInvalidObjectSubTypeTargetLines());
        return retval;
    }

    /**
     * 
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#createDocumentUnbalanced()
     */
    @Override
    protected final TransactionalDocument createDocumentUnbalanced() throws Exception {
        NonCheckDisbursementDocument retval = (NonCheckDisbursementDocument) createDocument();
        retval.setSourceAccountingLines(getInvalidObjectSubTypeSourceLines());
        retval.addTargetAccountingLine(getValidObjectSubTypeTargetLine());
        return retval;
    }

    /**
     * 
     * @see org.kuali.core.rule.DocumentRuleTestBase#createDocumentInvalidDescription()
     */
    @Override
    protected final Document createDocumentInvalidDescription() throws Exception {
        return getDocumentParameterNoDescription().createDocument(getDocumentService());
    }

    /**
     * 
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#createDocumentWithValidObjectSubType()
     */
    @Override
    protected final TransactionalDocument createDocumentWithValidObjectSubType() throws Exception {
        NonCheckDisbursementDocument retval = (NonCheckDisbursementDocument) createDocument();
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
     * 
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

    /**
     * Fixture method to get a <code>{@link TransactionalDocumentParameter}</code> instance for
     * <code>{@link NonCheckDisbursementDocument}</code>.
     * 
     * @return TransactionalDocumentParameter
     */
    public final TransactionalDocumentParameter getNonCheckDisbursementDocument() {
        return _ncdParam;
    }

    /**
     * Fixture method to assign to the test a <code>{@link TransactionalDocumentParameter}</code> instance for
     * <code>{@link NonCheckDisbursementDocument}</code>.
     * 
     * @param p
     */
    public final void setNonCheckDisbursementDocument(TransactionalDocumentParameter p) {
        _ncdParam = p;
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
     * Accessor method for Explicit Target fixture used for testProcessGeneralLedgerPendingEntriese> test methods.
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
     * @return e pending entry fixture
     */
    @Override
    public final GeneralLedgerPendingEntry getExpectedOffsetTargetPendingEntry() {
        return getExpectedGECOffsetTargetPendingEntry();
    }

    /**
     * Accessor method for Offset Target fixture used for testProcessGeneralLedgerPendingEntries test methods.
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
     * @see TransactionalDocumentRuleTestBase#getExpenseSourceLine()
     */
    @Override
    protected AccountingLine getExpenseSourceLine() {
        return createLineFromFixture("expenseGECSourceLine");
    }

    /**
     * @see TransactionalDocumentRuleTestBase#getExpenseTargetLine()
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

    // These methods will need to be fixed in the future to yield more meaningful
    // results
    /**
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#testProcessGenerateGeneralLedgerPendingEntries_validSourceAsset()
     */
    @Override
    public void testProcessGenerateGeneralLedgerPendingEntries_validSourceAsset() {
    }

    /**
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#testProcessGenerateGeneralLedgerPendingEntries_validTargetAsset()
     */
    @Override
    public void testProcessGenerateGeneralLedgerPendingEntries_validTargetAsset() {
    }

    /**
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#testProcessGenerateGeneralLedgerPendingEntries_validSourceExpense()
     */
    @Override
    public void testProcessGenerateGeneralLedgerPendingEntries_validSourceExpense() {
    }

    /**
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#testProcessGenerateGeneralLedgerPendingEntries_validTargetExpense()
     */
    @Override
    public void testProcessGenerateGeneralLedgerPendingEntries_validTargetExpense() {
    }

    /**
     * tests true is returned for a positive income
     * 
     * @throws Exception
     */
    public void testIsDebit_income_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), NonCheckDisbursementDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(transactionalDocument, SourceAccountingLine.class, POSITIVE);
        
        assertTrue(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateExcpetion</code> is thrown for a negative income
     * 
     * @throws Exception
     */
    public void testIsDebit_income_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), NonCheckDisbursementDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(transactionalDocument, SourceAccountingLine.class, NEGATIVE);
        
       assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a zero income
     * 
     * @throws Exception
     */
    public void testIsDebit_income_zeroAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), NonCheckDisbursementDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(transactionalDocument, SourceAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests true is returned for a positive expense
     * 
     * @throws Exception
     */
    public void testIsDebit_expense_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), NonCheckDisbursementDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(transactionalDocument, SourceAccountingLine.class, POSITIVE);
        
        assertTrue(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a negative expense
     * 
     * @throws Exception
     */
    public void testIsDebit_expense_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), NonCheckDisbursementDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(transactionalDocument, SourceAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a zero expense
     * 
     * @throws Exception
     */
    public void testIsDebit_expense_zeroAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), NonCheckDisbursementDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(transactionalDocument, SourceAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests true is returned for a positive asset
     * 
     * @throws Exception
     */
    public void testIsDebit_asset_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), NonCheckDisbursementDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(transactionalDocument, SourceAccountingLine.class, POSITIVE);
      
        assertTrue(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a negative asset
     * 
     * @throws Exception
     */
    public void testIsDebit_asset_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), NonCheckDisbursementDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(transactionalDocument, SourceAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a zero asset
     * 
     * @throws Exception
     */
    public void testIsDebit_asset_zeroAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), NonCheckDisbursementDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(transactionalDocument, SourceAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests true is returned for a positive liability
     * 
     * @throws Exception
     */
    public void testIsDebit_liability_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), NonCheckDisbursementDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(transactionalDocument, SourceAccountingLine.class, POSITIVE);
        
        assertTrue(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a negative liability
     * 
     * @throws Exception
     */
    public void testIsDebit_liability_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), NonCheckDisbursementDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(transactionalDocument, SourceAccountingLine.class, NEGATIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a zero liability
     * 
     * @throws Exception
     */
    public void testIsDebit_liability_zeroAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getDocument(getDocumentService(), NonCheckDisbursementDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(transactionalDocument, SourceAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests true is returned for a positive income
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_income_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), NonCheckDisbursementDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(transactionalDocument, SourceAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateExcpetion</code> is thrown for a negative income
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_income_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), NonCheckDisbursementDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(transactionalDocument, SourceAccountingLine.class, NEGATIVE);
        
        assertFalse(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));

    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a zero income
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_income_zeroAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), NonCheckDisbursementDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getIncomeLine(transactionalDocument, SourceAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests true is returned for a positive expense
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_expense_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), NonCheckDisbursementDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(transactionalDocument, SourceAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a negative expense
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_expense_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), NonCheckDisbursementDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(transactionalDocument, SourceAccountingLine.class, NEGATIVE);
        
        assertFalse(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a zero expense
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_expense_zeroAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), NonCheckDisbursementDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getExpenseLine(transactionalDocument, SourceAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests true is returned for a positive asset
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_asset_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), NonCheckDisbursementDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(transactionalDocument, SourceAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a negative asset
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_asset_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), NonCheckDisbursementDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(transactionalDocument, SourceAccountingLine.class, NEGATIVE);
        
        assertFalse(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a zero asset
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_asset_zeroAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), NonCheckDisbursementDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getAssetLine(transactionalDocument, SourceAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests true is returned for a positive liability
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_liability_positveAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), NonCheckDisbursementDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(transactionalDocument, SourceAccountingLine.class, POSITIVE);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a negative liability
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_liability_negativeAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), NonCheckDisbursementDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(transactionalDocument, SourceAccountingLine.class, NEGATIVE);
        
        assertFalse(IsDebitTestUtils.isDebit(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }

    /**
     * tests an <code>IllegalStateException</code> is thrown for a zero liability
     * 
     * @throws Exception
     */
    public void testIsDebit_errorCorrection_liability_zeroAmount() throws Exception {
        TransactionalDocument transactionalDocument = IsDebitTestUtils.getErrorCorrectionDocument(getDocumentService(), NonCheckDisbursementDocument.class);
        AccountingLine accountingLine = IsDebitTestUtils.getLiabilityLine(transactionalDocument, SourceAccountingLine.class, KualiDecimal.ZERO);

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(getDocumentTypeService(), getDataDictionaryService(), transactionalDocument, accountingLine));
    }
    // ////////////////////////////////////////////////////////////////////////
    // Test methods end here //
    // ////////////////////////////////////////////////////////////////////////
}
