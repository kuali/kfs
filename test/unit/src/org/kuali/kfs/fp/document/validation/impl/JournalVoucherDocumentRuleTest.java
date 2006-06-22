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
import org.kuali.KeyConstants;
import org.kuali.PropertyConstants;
import org.kuali.core.bo.AccountingLine;
import org.kuali.core.bo.SourceAccountingLine;
import org.kuali.core.bo.TargetAccountingLine;
import org.kuali.core.document.Document;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.rule.TransactionalDocumentRuleTestBase;
import org.kuali.core.rule.event.AddAccountingLineEvent;
import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.financial.document.DisbursementVoucherDocumentTest;
import org.kuali.module.financial.document.JournalVoucherDocument;
import org.kuali.module.financial.rules.TransactionalDocumentRuleBaseConstants.GENERAL_LEDGER_PENDING_ENTRY_CODE;
import org.kuali.module.gl.bo.GeneralLedgerPendingEntry;
import org.kuali.module.gl.util.SufficientFundsItemHelper.SufficientFundsItem;
import org.kuali.test.parameters.AccountingLineParameter;
import org.kuali.test.parameters.DocumentParameter;
import org.kuali.test.parameters.TransactionalDocumentParameter;

/**
 * This class tests the JournalVoucherDocument's persistence, routing, and PE generation.
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class JournalVoucherDocumentRuleTest extends TransactionalDocumentRuleTestBase {

    private static final String COLLECTION_NAME = "JournalVoucherDocumentRuleTest.collection1";
    private static final String KNOWN_DOCUMENT_TYPENAME = "KualiJournalVoucherDocument";

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
    private GeneralLedgerPendingEntry _expectedExpSourceGlEntryE;
    private GeneralLedgerPendingEntry _expectedExpTargetGlEntryE;
    private GeneralLedgerPendingEntry _expectedExpSourceGlEntry;
    private GeneralLedgerPendingEntry _expectedExpTargetGlEntry;
    private GeneralLedgerPendingEntry _expectedOffSourceGlEntry;
    private GeneralLedgerPendingEntry _expectedOffTargetGlEntry;
    private String _balanceTypeCode;
    private String _user;

    /**
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        changeCurrentUser(getTestUserName());
    }

    /**
     * 
     * @see org.kuali.test.KualiTestBaseWithFixtures#getFixtureCollectionNames()
     */
    @Override
    public String[] getFixtureCollectionNames() {
        return FIXTURE_COLLECTION_NAMES;
    }

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
        return (TargetAccountingLine) getTargetLineParameter1().createLine();
    }

    /**
     * 
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#getValidObjectSubTypeTargetLine()
     */
    @Override
    protected final TargetAccountingLine getValidObjectSubTypeTargetLine() throws Exception {
        return (TargetAccountingLine) getTargetLineParameter1().createLine();
    }

    /**
     * 
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#getInvalidObjectSubTypeTargetLine()
     */
    @Override
    protected final TargetAccountingLine getInvalidObjectSubTypeTargetLine() throws Exception {
        return (TargetAccountingLine) getTargetLineParameter3().createLine();
    }

    /**
     * 
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#getValidObjectSubTypeSourceLines()
     */
    @Override
    protected final List getValidObjectSubTypeSourceLines() throws Exception {
        List retval = new ArrayList();
        retval.add(getSourceLineParameter6().createLine());
        return retval;
    }

    /**
     * 
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#getInvalidObjectSubTypeSourceLines()
     */
    @Override
    protected final List getInvalidObjectSubTypeSourceLines() throws Exception {
        List retval = new ArrayList();
        retval.add(getSourceLineParameter1().createLine());
        retval.add(getSourceLineParameter2().createLine());
        return retval;
    }

    /**
     * 
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#getInvalidObjectSubTypeTargetLines()
     */
    @Override
    protected final List getInvalidObjectSubTypeTargetLines() throws Exception {
        List retval = new ArrayList();
        retval.add(getTargetLineParameter1().createLine());
        retval.add(getTargetLineParameter3().createLine());
        return retval;
    }

    /**
     * 
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#getValidObjectSubTypeTargetLines()
     */
    @Override
    protected final List getValidObjectSubTypeTargetLines() throws Exception {
        List retval = new ArrayList();
        retval.add(getTargetLineParameter2().createLine());
        retval.add(getTargetLineParameter2().createLine());
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
        SourceAccountingLine retval = (SourceAccountingLine) getSourceLineParameter3().createLine();
        retval.setObjectTypeCode(new String());
        return retval;
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
        return (SourceAccountingLine) getSourceLineParameter6().createLine();
    }

    /**
     * 
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#getAssetSourceLine()
     */
    @Override
    public final SourceAccountingLine getAssetSourceLine() {
        return (SourceAccountingLine) getAssetSourceLineParameter().createLine();
    }

    /**
     * 
     * @see org.kuali.core.rule.DocumentRuleTestBase#createDocument()
     */
    @Override
    protected final Document createDocument() throws Exception {
        return buildDocument(getJournalVoucherDocumentParameter1());
    }

    /**
     * 
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#createDocument5()
     */
    @Override
    protected final TransactionalDocument createDocument5() throws Exception {

        // return (TransactionalDocument)getDocumentParameter5()
        // .createDocument( getDocumentService() );
        return (TransactionalDocument) buildDocument(getJournalVoucherDocumentParameter1());
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
        TransactionalDocument retval = (TransactionalDocument) createDocument();
        retval.setSourceAccountingLines(getInvalidObjectSubTypeSourceLines());
        retval.setTargetAccountingLines(getInvalidObjectSubTypeTargetLines());
        return retval;
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
        Document retval = getJournalVoucherDocumentParameter1().createDocument(getDocumentService());
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

    /**
     * @return <code>TransactionalDocumentParameter</code>
     */
    public final TransactionalDocumentParameter getJournalVoucherDocumentParameter1() {
        return _docParam1;
    }

    /**
     * @param p
     */
    public final void setJournalVoucherDocumentParameter1(TransactionalDocumentParameter p) {
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
     * @return <code>TransactionalDocumentParameter</code>
     */
    public final TransactionalDocumentParameter getDocumentParameter5() {
        return _documentParameter5;
    }

    /**
     * @param p
     */
    public final void setDocumentParameter5(TransactionalDocumentParameter p) {
        _documentParameter5 = p;
    }

    /**
     * Set the Username fixture set specifically for <code>{@link JournalVoucherDocument}</code> instances
     * 
     * @param u Username.
     */
    public final void setUser_jvdoc(String u) {
        _user = u;
    }

    /**
     * get the Username fixture set specifically for <code>{@link JournalVoucherDocument}</code> instances
     * 
     * @return String username
     */
    public final String getUser_jvdoc() {
        return _user;
    }

    /**
     * Get the username fixture for the tests.
     * 
     * @return String
     */
    protected String getTestUserName() {
        return getUser_jvdoc();
    }

    /**
     * Accessor method for Explicit Source fixture used for testProcessGeneralLedgerPendingEntries test methods.
     * 
     * @return GeneralLedgerPendingEntry pending entry fixture
     */
    @Override
    public final GeneralLedgerPendingEntry getExpectedExplicitSourcePendingEntry() {
        return getExpectedJVExplicitSourcePendingEntry();
    }

    /**
     * Accessor method for Explicit Source fixture used for testProcessGeneralLedgerPendingEntries test methods.
     * 
     * @return GeneralLedgerPendingEntry pending entry fixture
     */
    public final GeneralLedgerPendingEntry getExpectedJVExplicitSourcePendingEntry() {
        return _expectedExpSourceGlEntry;
    }

    /**
     * Accessor method for Explicit Source fixture used for testProcessGeneralLedgerPendingEntries test methods.
     * 
     * @param e pending entry fixture
     */
    public final void setExpectedJVExplicitSourcePendingEntry(GeneralLedgerPendingEntry e) {
        _expectedExpSourceGlEntry = e;
    }

    /**
     * Accessor method for Explicit Target fixture used for testProcessGeneralLedgerPendingEntries test methods.
     * 
     * @return GeneralLedgerPendingEntry pending entry fixture
     */
    @Override
    public final GeneralLedgerPendingEntry getExpectedExplicitTargetPendingEntry() {
        return getExpectedJVExplicitTargetPendingEntry();
    }

    /**
     * Accessor method for Explicit Target fixture used for testProcessGeneralLedgerPendingEntries test methods.
     * 
     * @return GeneralLedgerPendingEntry pending entry fixture
     */
    public final GeneralLedgerPendingEntry getExpectedJVExplicitTargetPendingEntry() {
        return _expectedExpTargetGlEntry;
    }

    /**
     * Accessor method for Explicit Target fixture used for testProcessGeneralLedgerPendingEntries test methods.
     * 
     * @param e pending entry fixture
     */
    public final void setExpectedJVExplicitTargetPendingEntry(GeneralLedgerPendingEntry e) {
        _expectedExpTargetGlEntry = e;
    }

    /**
     * Accessor method for Explicit Source fixture used for testProcessGeneralLedgerPendingEntries test methods.
     * 
     * @return GeneralLedgerPendingEntry pending entry fixture
     */
    @Override
    public final GeneralLedgerPendingEntry getExpectedExplicitSourcePendingEntryForExpense() {
        return getExpectedJVExplicitSourcePendingEntryForExpense();
    }

    /**
     * Accessor method for Explicit Source fixture used for testProcessGeneralLedgerPendingEntries test methods.
     * 
     * @return GeneralLedgerPendingEntry pending entry fixture
     */
    public final GeneralLedgerPendingEntry getExpectedJVExplicitSourcePendingEntryForExpense() {
        return _expectedExpSourceGlEntryE;
    }

    /**
     * Accessor method for Explicit Source fixture used for testProcessGeneralLedgerPendingEntries test methods.
     * 
     * @param e pending entry fixture
     */
    public final void setExpectedJVExplicitSourcePendingEntryForExpense(GeneralLedgerPendingEntry e) {
        _expectedExpSourceGlEntryE = e;
    }

    /**
     * Accessor method for Explicit Target fixture used for testProcessGeneralLedgerPendingEntries test methods.
     * 
     * @return GeneralLedgerPendingEntry pending entry fixture
     */
    @Override
    public final GeneralLedgerPendingEntry getExpectedExplicitTargetPendingEntryForExpense() {
        return getExpectedJVExplicitTargetPendingEntryForExpense();
    }

    /**
     * Accessor method for Explicit Target fixture used for testProcessGeneralLedgerPendingEntries test methods.
     * 
     * @return GeneralLedgerPendingEntry pending entry fixture
     */
    public final GeneralLedgerPendingEntry getExpectedJVExplicitTargetPendingEntryForExpense() {
        return _expectedExpTargetGlEntryE;
    }

    /**
     * Accessor method for Explicit Target fixture used for testProcessGeneralLedgerPendingEntries test methods.
     * 
     * @param e pending entry fixture
     */
    public final void setExpectedJVExplicitTargetPendingEntryForExpense(GeneralLedgerPendingEntry e) {
        _expectedExpTargetGlEntryE = e;
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
        LOG().info("Setting expectedOffsetTargetGlEntry: " + e);
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

    /**
     * Accessor for ACTUAL balance type code.
     * 
     * @param code
     */
    public void setActualBalanceTypeCode(String code) {
        _balanceTypeCode = code;
    }

    /**
     * Accessor for ACTUAL balance type code.
     * 
     * @return String
     */
    public String getActualBalanceTypeCode() {
        return _balanceTypeCode;
    }

    /**
     * Obtain correct BalanceTypeCode for <code>{@link JournalVoucherDocument}</code>
     * 
     * @return String
     */
    protected String getBalanceTypeCodeFixture() {
        return getActualBalanceTypeCode();
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
    @Override
    protected Document buildDocument(DocumentParameter parameter) throws Exception {
        JournalVoucherDocument retval = (JournalVoucherDocument) super.buildDocument(parameter);
        retval.setBalanceTypeCode(getBalanceTypeCodeFixture());
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
        testProcessGenerateGeneralLedgerPendingEntries(createLineFromFixture("expenseSourceLine"), getExpectedExplicitSourcePendingEntryForExpense(), getExpectedOffsetSourcePendingEntry());
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
        testProcessAddAccountingLineBusinessRules("expenseSourceLine2", null, null);
    }

    /**
     * @throws Exception
     */
    public void testProcessAddAccountingLineBusinessRules_emptyReferenceOriginCode() throws Exception {
        AccountingLine line = createLineFromFixture("externalEncumbranceSourceLine");
        line.setReferenceOriginCode("");
        testProcessAddAccountingLineBusinessRules(line, PropertyConstants.REFERENCE_ORIGIN_CODE, KeyConstants.ERROR_REQUIRED);
    }

    /**
     * @throws Exception
     */
    public void testProcessAddAccountingLineBusinessRules_emptyReferences() throws Exception {
        AccountingLine line = createLineFromFixture("externalEncumbranceSourceLine");
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
        testProcessAddAccountingLineBusinessRules("externalEncumbranceSourceLine", null, null);
    }

    /**
     * @throws Exception
     */
    public void testProcessAddAccountingLineBusinessRules_invalidReferenceOriginCode() throws Exception {
        AccountingLine line = createLineFromFixture("externalEncumbranceSourceLine");
        line.setReferenceOriginCode("42");
        testProcessAddAccountingLineBusinessRules(line, PropertyConstants.REFERENCE_ORIGIN_CODE, KeyConstants.ERROR_EXISTENCE);
    }

    /**
     * @throws Exception
     */
    public void testProcessAddAccountingLineBusinessRules_invalidReferenceTypeCode() throws Exception {
        AccountingLine line = createLineFromFixture("externalEncumbranceSourceLine");
        line.setReferenceTypeCode("42");
        testProcessAddAccountingLineBusinessRules(line, PropertyConstants.REFERENCE_TYPE_CODE, KeyConstants.ERROR_EXISTENCE);
        assertGlobalErrorMapNotContains(line.toString(), PropertyConstants.REFERENCE_TYPE_CODE, KeyConstants.ERROR_REQUIRED);
        assertGlobalErrorMapSize(line.toString(), 1);
    }

    private void testProcessAddAccountingLineBusinessRules(String fixtureName, String expectedErrorFieldName, String expectedErrorKey) throws Exception {
        testProcessAddAccountingLineBusinessRules(createLineFromFixture(fixtureName), expectedErrorFieldName, expectedErrorKey);
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
    public void testProcessTargetAccountingLineSufficientFundsCheckingPreparation_line_notNull() {
        boolean failedAsExpected = false;
        JournalVoucherDocumentRule rule = new JournalVoucherDocumentRule();
        try {
            TargetAccountingLine line = (TargetAccountingLine) getTargetLineParameter1().createLine();

            rule.processTargetAccountingLineSufficientFundsCheckingPreparation(null, line);
        }
        catch (IllegalArgumentException e) {
            failedAsExpected = true;
        }
        assertTrue(failedAsExpected);
    }

    /**
     * 
     */
    public void testProcessTargetAccountingLineSufficientFundsCheckingPreparation_line_null() {
        boolean failedAsExpected = false;
        JournalVoucherDocumentRule rule = new JournalVoucherDocumentRule();

        SufficientFundsItem item = rule.processTargetAccountingLineSufficientFundsCheckingPreparation(null, null);

        assertNull(item);
    }

    /**
     * @throws Exception
     */
    public void testProcessSourceAccountingLineSufficientFundsCheckingPreparation_balanceTypeCode_notAllowed() throws Exception {
        JournalVoucherDocument document = (JournalVoucherDocument) createDocument();
        document.setBalanceTypeCode("BB");
        SourceAccountingLine line = (SourceAccountingLine) getSourceLineParameter1().createLine();

        JournalVoucherDocumentRule rule = new JournalVoucherDocumentRule();

        SufficientFundsItem item = rule.processSourceAccountingLineSufficientFundsCheckingPreparation(document, line);

        assertNull(item);
    }

    /**
     * @throws Exception
     */
    public void testProcessSourceAccountingLineSufficientFundsCheckingPreparation_balanceTypeCode_Actual_accountSufficientFundsCode_cashAtAccount_financialObjectCode_not_CashInBankCode() throws Exception {
        JournalVoucherDocument document = (JournalVoucherDocument) createDocument();
        document.setBalanceTypeCode(Constants.BALANCE_TYPE_ACTUAL);

        SourceAccountingLine line = (SourceAccountingLine) getSourceLineParameter1().createLine();
        line.setFinancialObjectCode("0");
        line.getAccount().setAccountSufficientFundsCode(Constants.SF_TYPE_CASH_AT_ACCOUNT);

        JournalVoucherDocumentRule rule = new JournalVoucherDocumentRule();

        SufficientFundsItem item = rule.processSourceAccountingLineSufficientFundsCheckingPreparation(document, line);

        assertNull(item);
    }

    /**
     * @throws Exception
     */
    public void testProcessSourceAccountingLineSufficientFundsCheckingPreparation_balanceTypeCode_Actual_accountSufficientFundsCode_cashAtAccount_financialObjectCode_cashInBank() throws Exception {
        JournalVoucherDocument document = (JournalVoucherDocument) createDocument();
        document.setBalanceTypeCode(Constants.BALANCE_TYPE_ACTUAL);

        SourceAccountingLine line = (SourceAccountingLine) getSourceLineParameter1().createLine();
        line.setFinancialObjectCode(SpringServiceLocator.getSufficientFundsService().getFinancialObjectCodeForCashInBank());
        line.getAccount().setAccountSufficientFundsCode(Constants.SF_TYPE_CASH_AT_ACCOUNT);
        // used arbitrary value to ensure what goes in is what goes out. if the 'standard' C/D values were used there is now way of
        // knowing if the code changed it.
        String debitCreditCode = "M";
        line.setDebitCreditCode(debitCreditCode);

        JournalVoucherDocumentRule rule = new JournalVoucherDocumentRule();

        SufficientFundsItem item = rule.processSourceAccountingLineSufficientFundsCheckingPreparation(document, line);

        assertNotNull(item);
        assertEquals(debitCreditCode, item.getDebitCreditCode());
    }

    /**
     * @throws Exception
     */
    public void testProcessSourceAccountingLineSufficientFundsCheckingPreparation_balanceTypeCode_Actual_accountSufficientFundsCode_not_cashAtAccount_debitCreditCode_D() throws Exception {
        JournalVoucherDocument document = (JournalVoucherDocument) createDocument();
        document.setBalanceTypeCode(Constants.BALANCE_TYPE_ACTUAL);

        SourceAccountingLine line = (SourceAccountingLine) getSourceLineParameter1().createLine();
        line.getAccount().setAccountSufficientFundsCode(Constants.SF_TYPE_ACCOUNT);
        line.setDebitCreditCode(Constants.GL_DEBIT_CODE);

        JournalVoucherDocumentRule rule = new JournalVoucherDocumentRule();

        SufficientFundsItem item = rule.processSourceAccountingLineSufficientFundsCheckingPreparation(document, line);

        assertNotNull(item);
        assertEquals(Constants.GL_CREDIT_CODE, item.getDebitCreditCode());
    }

    /**
     * @throws Exception
     */
    public void testProcessSourceAccountingLineSufficientFundsCheckingPreparation_balanceTypeCode_Actual_accountSufficientFundsCode_not_cashAtAccount_debitCreditCode_C() throws Exception {
        JournalVoucherDocument document = (JournalVoucherDocument) createDocument();
        document.setBalanceTypeCode(Constants.BALANCE_TYPE_ACTUAL);

        SourceAccountingLine line = (SourceAccountingLine) getSourceLineParameter1().createLine();
        line.getAccount().setAccountSufficientFundsCode(Constants.SF_TYPE_ACCOUNT);
        line.setDebitCreditCode(Constants.GL_CREDIT_CODE);

        JournalVoucherDocumentRule rule = new JournalVoucherDocumentRule();

        SufficientFundsItem item = rule.processSourceAccountingLineSufficientFundsCheckingPreparation(document, line);

        assertNotNull(item);
        assertEquals(Constants.GL_DEBIT_CODE, item.getDebitCreditCode());
    }

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
        accountingLine.setDebitCreditCode(GENERAL_LEDGER_PENDING_ENTRY_CODE.DEBIT);

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
        accountingLine.setDebitCreditCode(GENERAL_LEDGER_PENDING_ENTRY_CODE.CREDIT);

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
        accountingLine.setDebitCreditCode(GENERAL_LEDGER_PENDING_ENTRY_CODE.CREDIT);

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
        accountingLine.setDebitCreditCode(GENERAL_LEDGER_PENDING_ENTRY_CODE.DEBIT);

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
