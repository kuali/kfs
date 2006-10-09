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

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.kuali.Constants;
import static org.kuali.Constants.GL_CREDIT_CODE;
import static org.kuali.Constants.GL_DEBIT_CODE;
import org.kuali.core.bo.AccountingLine;
import org.kuali.core.bo.SourceAccountingLine;
import org.kuali.core.bo.TargetAccountingLine;
import org.kuali.core.document.Document;
import org.kuali.core.document.TransactionalDocument;
import org.kuali.core.rule.TransactionalDocumentRuleTestBase;
import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.core.util.SpringServiceLocator;
import static org.kuali.core.util.SpringServiceLocator.getDataDictionaryService;
import static org.kuali.core.util.SpringServiceLocator.getDocumentService;
import static org.kuali.core.util.SpringServiceLocator.getDocumentTypeService;
import org.kuali.module.chart.bo.AccountingPeriod;
import org.kuali.module.financial.document.AuxiliaryVoucherDocument;
import org.kuali.module.gl.bo.GeneralLedgerPendingEntry;
import org.kuali.test.DocumentTestUtils;
import org.kuali.test.WithTestSpringContext;
import org.kuali.test.fixtures.AccountingLineFixture;
import static org.kuali.test.fixtures.AccountingLineFixture.ACCRUED_INCOME_LINE;
import static org.kuali.test.fixtures.AccountingLineFixture.ACCRUED_SICK_PAY_LINE;
import static org.kuali.test.fixtures.AccountingLineFixture.EXPENSE_GEC_LINE;
import static org.kuali.test.fixtures.AccountingLineFixture.FUND_BALANCE_LINE;
import static org.kuali.test.fixtures.AccountingLineFixture.LINE10;
import static org.kuali.test.fixtures.AccountingLineFixture.LINE15;
import static org.kuali.test.fixtures.AccountingLineFixture.LINE8;
import org.kuali.test.fixtures.GeneralLedgerPendingEntryFixture;
import static org.kuali.test.fixtures.UserNameFixture.KHUNTLEY;

/**
 * This class tests the <code>{@link AuxiliaryVoucherDocument}</code>'s rules and PE generation.
 * 
 * 
 */
@WithTestSpringContext(session = KHUNTLEY)
public class AuxiliaryVoucherDocumentRuleTest extends TransactionalDocumentRuleTestBase {

    private static final String KNOWN_DOCUMENT_TYPENAME = "KualiAuxiliaryVoucherDocument";


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
        return ACCRUED_INCOME_LINE.createAccountingLine(TargetAccountingLine.class, Constants.GL_DEBIT_CODE);
    }

    /**
     * 
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#getValidObjectSubTypeTargetLine()
     */
    @Override
    protected final TargetAccountingLine getValidObjectSubTypeTargetLine() throws Exception {
        return new TargetAccountingLine();
    }

    /**
     * 
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#getInvalidObjectSubTypeTargetLine()
     */
    @Override
    protected final TargetAccountingLine getInvalidObjectSubTypeTargetLine() throws Exception {
        return  new TargetAccountingLine();
    }

    /**
     * 
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#getValidObjectSubTypeSourceLines()
     */
    @Override
    protected final List<SourceAccountingLine> getValidObjectSubTypeSourceLines() throws Exception {
        List<SourceAccountingLine> retval = new ArrayList<SourceAccountingLine>();
        retval.add(LINE15.createAccountingLine(SourceAccountingLine.class, Constants.GL_CREDIT_CODE));
        retval.add(LINE15.createAccountingLine(SourceAccountingLine.class, Constants.GL_DEBIT_CODE));
        return retval;
    }

    /**
     * 
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#getInvalidObjectSubTypeSourceLines()
     */
    @Override
    protected final List<SourceAccountingLine> getInvalidObjectSubTypeSourceLines() throws Exception {
        List<SourceAccountingLine> retval = new ArrayList<SourceAccountingLine>();
        retval.add(getAccruedSickPaySourceLineParameter());
        retval.add(getAccruedSickPaySourceLineParameter());
        return retval;
    }

    /**
     * 
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#getInvalidObjectSubTypeTargetLines()
     */
    @Override
    protected final List<TargetAccountingLine> getInvalidObjectSubTypeTargetLines() throws Exception {
        return new ArrayList();
    }

    /**
     * 
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#getValidObjectSubTypeTargetLines()
     */
    @Override
    protected final List<TargetAccountingLine> getValidObjectSubTypeTargetLines() throws Exception {
        return new ArrayList();
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
        return FUND_BALANCE_LINE.createSourceAccountingLine();
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
        return getAccruedIncomeSourceLineParameter();
    }

    /**
     * 
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#getAssetSourceLine()
     */
    @Override
    public final SourceAccountingLine getAssetSourceLine()throws Exception {
        return getAccruedIncomeSourceLineParameter();
    }

    /**
     * 
     * @see org.kuali.core.rule.DocumentRuleTestBase#createDocument()
     */
    @Override
    protected final Document createDocument() throws Exception {
        //AV document has a restriction on accounting period cannot be more than 2 periods behind current
        Date date = SpringServiceLocator.getDateTimeService().getCurrentSqlDate();
        AccountingPeriod accountingPeriod = SpringServiceLocator.getAccountingPeriodService().getByDate(date);
        return DocumentTestUtils.createTransactionalDocument(getDocumentService(), AuxiliaryVoucherDocument.class, accountingPeriod.getUniversityFiscalYear(), accountingPeriod.getUniversityFiscalPeriodCode());

    }

    /**
     * 
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#createDocument5()
     */
    @Override
    protected final TransactionalDocument createDocument5() throws Exception {   //AV document has a restriction on accounting period cannot be more than 2 periods behind current
        Date date = SpringServiceLocator.getDateTimeService().getCurrentSqlDate();
        AccountingPeriod accountingPeriod = SpringServiceLocator.getAccountingPeriodService().getByDate(date);
        return DocumentTestUtils.createTransactionalDocument(getDocumentService(), AuxiliaryVoucherDocument.class, accountingPeriod.getUniversityFiscalYear(), accountingPeriod.getUniversityFiscalPeriodCode());

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
        return createDocumentInvalidDescription();
    }

    /**
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#createDocumentWithInvalidObjectSubType()
     */
    @Override
    protected final TransactionalDocument createDocumentWithInvalidObjectSubType() throws Exception {
        AuxiliaryVoucherDocument retval = (AuxiliaryVoucherDocument) createDocument();
        retval.setSourceAccountingLines(getInvalidObjectSubTypeSourceLines());
        return retval;
    }

    /**
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#createDocumentUnbalanced()
     */
    @Override
    protected final TransactionalDocument createDocumentUnbalanced() throws Exception {
        AuxiliaryVoucherDocument retval = (AuxiliaryVoucherDocument) createDocument();
        retval.setSourceAccountingLines(getInvalidObjectSubTypeSourceLines());
        return retval;
    }

    /**
     * @see org.kuali.core.rule.DocumentRuleTestBase#createDocumentInvalidDescription()
     */
    @Override
    protected final Document createDocumentInvalidDescription() throws Exception {
        //AV document has a restriction on accounting period cannot be more than 2 periods behind current
        Date date = SpringServiceLocator.getDateTimeService().getCurrentSqlDate();
        AccountingPeriod accountingPeriod = SpringServiceLocator.getAccountingPeriodService().getByDate(date);
        Document document= DocumentTestUtils.createTransactionalDocument(getDocumentService(), AuxiliaryVoucherDocument.class, accountingPeriod.getUniversityFiscalYear(), accountingPeriod.getUniversityFiscalPeriodCode());

        document.getDocumentHeader().setFinancialDocumentDescription(null);
        return document;
    }

    /**
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#createDocumentWithValidObjectSubType()
     */
    @Override
    protected final TransactionalDocument createDocumentWithValidObjectSubType() throws Exception {
        AuxiliaryVoucherDocument retval = (AuxiliaryVoucherDocument) createDocument();
        retval.setSourceAccountingLines(getValidObjectSubTypeSourceLines());
        return retval;
    }

    /**
     * Accessor for fixture 'sourceLine6'
     * 
     * @return AccountingLineParameter
     */
    public final SourceAccountingLine getAccruedIncomeSourceLineParameter()throws Exception {
        return ACCRUED_INCOME_LINE.createAccountingLine(SourceAccountingLine.class, Constants.GL_DEBIT_CODE);
    }

    /**
     * Accessor for fixture 'sourceLine6'
     * 
     * @return AccountingLineParameter
     */
    public final SourceAccountingLine getAccruedSickPaySourceLineParameter() throws Exception {
        return ACCRUED_SICK_PAY_LINE.createAccountingLine(SourceAccountingLine.class, Constants.GL_DEBIT_CODE);
    }



    /**
     * Accessor method for Explicit Source fixture used for testProcessGeneralLedgerPendingEntries test methods.
     * 
     * @return GeneralLedgerPendingEntry pending entry fixture
     */
    @Override
    public final GeneralLedgerPendingEntry getExpectedExplicitSourcePendingEntry() {
        return GeneralLedgerPendingEntryFixture.EXPECTED_AV_EXPLICIT_SOURCE_PENDING_ENTRY.createGeneralLedgerPendingEntry();
    }

    /**
     * 
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#getExpectedExplicitTargetPendingEntry()
     */
    @Override
    public final GeneralLedgerPendingEntry getExpectedExplicitTargetPendingEntry() {
        return GeneralLedgerPendingEntryFixture.EXPECTED_AV_EXPLICIT_TARGET_PENDING_ENTRY.createGeneralLedgerPendingEntry();
    }

    /**
     * AV doesnt create offsets
     * Accessor method for Offset Target fixture used for testProcessGeneralLedgerPendingEntries test methods.
     * 
     * @return GeneralLedgerPendingEntry pending entry fixture
     */
    @Override
    public final GeneralLedgerPendingEntry getExpectedOffsetTargetPendingEntry() {
        return new GeneralLedgerPendingEntry();
    }

    /**
     * AV doesnt create offset
     * Accessor method for Offset Source fixture used for testProcessGeneralLedgerPendingEntries test methods.
     * 
     * @return GeneralLedgerPendingEntry pending entry fixture
     */
    @Override
    public final GeneralLedgerPendingEntry getExpectedOffsetSourcePendingEntry() {
        return new GeneralLedgerPendingEntry();
    }

    /**
     * Accessor method for Explicit Source fixture used for testProcessGeneralLedgerPendingEntries test methods.
     * 
     * @return GeneralLedgerPendingEntry pending entry fixture
     */
    @Override
    public GeneralLedgerPendingEntry getExpectedExplicitSourcePendingEntryForExpense() {
        return GeneralLedgerPendingEntryFixture.EXPECTED_AV_EXPLICIT_SOURCE_PENDING_ENTRY_FOR_EXPENSE.createGeneralLedgerPendingEntry();
    }

    /**
     * Accessor method for Explicit Source fixture used for testProcessGeneralLedgerPendingEntries test methods.
     * 
     * @return GeneralLedgerPendingEntry pending entry fixture
     */
    @Override
    public GeneralLedgerPendingEntry getExpectedExplicitTargetPendingEntryForExpense() {
        return GeneralLedgerPendingEntryFixture.EXPECTED_AV_EXPLICIT_TARGET_PENDING_ENTRY_FOR_EXPENSE.createGeneralLedgerPendingEntry();
    }


    /**
     * 
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#getExpenseSourceLine()
     */
    @Override
    protected SourceAccountingLine getExpenseSourceLine() throws Exception{
        return EXPENSE_GEC_LINE.createAccountingLine(SourceAccountingLine.class, Constants.GL_DEBIT_CODE);
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
        accountingLine.setDebitCreditCode(GL_DEBIT_CODE);

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
        accountingLine.setDebitCreditCode(GL_CREDIT_CODE);

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
    // ////////////////////////////////////////////////////////////////////////
    // Test methods end here //
    // ////////////////////////////////////////////////////////////////////////

    /**
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#testIsObjectSubTypeAllowed_InvalidSubType()
     */
    @Override
    public void testIsObjectSubTypeAllowed_InvalidSubType() throws Exception {
        testIsObjectSubTypeAllowed(AccountingLineFixture.LINE17.createSourceAccountingLine(), false);
    }

    /**
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#testIsObjectCodeAllowed_InvalidObjectCode()
     */
    @Override
    public void testIsObjectCodeAllowed_InvalidObjectCode() throws Exception {
        //the way this tests works is not valid because of the way that the AV implements the object code restriction
    }

    /**
     * overriden because the AV doesnt create offset entries
     * @see org.kuali.core.rule.TransactionalDocumentRuleTestBase#testProcessGenerateGeneralLedgerPendingEntries(org.kuali.core.bo.AccountingLine, org.kuali.module.gl.bo.GeneralLedgerPendingEntry, org.kuali.module.gl.bo.GeneralLedgerPendingEntry)
     */
    @Override
    protected void testProcessGenerateGeneralLedgerPendingEntries(AccountingLine line, GeneralLedgerPendingEntry expectedExplicitEntry, GeneralLedgerPendingEntry expectedOffsetEntry) throws Exception {
        TransactionalDocument document = createDocument5();
        assertEquals(0, document.getGeneralLedgerPendingEntries().size());
        getGenerateGeneralLedgerPendingEntriesRule().processGenerateGeneralLedgerPendingEntries(document, line, new GeneralLedgerPendingEntrySequenceHelper());
        assertEquals(1, document.getGeneralLedgerPendingEntries().size());
        assertSparselyEqualBean(expectedExplicitEntry, document.getGeneralLedgerPendingEntry(0));
        assertSparselyEqualBean(expectedOffsetEntry, document.getGeneralLedgerPendingEntry(1));

    }


}
