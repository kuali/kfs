/*
 * Copyright 2006-2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.financial.rules;

import static org.kuali.kfs.KFSConstants.GL_CREDIT_CODE;
import static org.kuali.kfs.KFSConstants.GL_DEBIT_CODE;
import static org.kuali.module.financial.rules.AccountingDocumentRuleTestUtils.testAddAccountingLineRule_IsObjectCodeAllowed;
import static org.kuali.module.financial.rules.AccountingDocumentRuleTestUtils.testAddAccountingLineRule_IsObjectTypeAllowed;
import static org.kuali.module.financial.rules.AccountingDocumentRuleTestUtils.testAddAccountingLineRule_ProcessAddAccountingLineBusinessRules;
import static org.kuali.module.financial.rules.AccountingDocumentRuleTestUtils.testAddAccountingLine_IsObjectSubTypeAllowed;
import static org.kuali.module.financial.rules.AccountingDocumentRuleTestUtils.testGenerateGeneralLedgerPendingEntriesRule_ProcessGenerateGeneralLedgerPendingEntries;
import static org.kuali.module.financial.rules.AccountingDocumentRuleTestUtils.testRouteDocumentRule_processRouteDocument;
import static org.kuali.module.financial.rules.AccountingDocumentRuleTestUtils.testSaveDocumentRule_ProcessSaveDocument;
import static org.kuali.test.fixtures.AccountingLineFixture.ACCRUED_INCOME_LINE;
import static org.kuali.test.fixtures.AccountingLineFixture.ACCRUED_SICK_PAY_LINE;
import static org.kuali.test.fixtures.AccountingLineFixture.EXPENSE_GEC_LINE;
import static org.kuali.test.fixtures.AccountingLineFixture.EXPENSE_LINE;
import static org.kuali.test.fixtures.AccountingLineFixture.FUND_BALANCE_LINE;
import static org.kuali.test.fixtures.AccountingLineFixture.LINE15;
import static org.kuali.test.fixtures.AccountingLineFixture.LINE8;
import static org.kuali.test.fixtures.GeneralLedgerPendingEntryFixture.EXPECTED_AV_EXPLICIT_SOURCE_PENDING_ENTRY;
import static org.kuali.test.fixtures.GeneralLedgerPendingEntryFixture.EXPECTED_AV_EXPLICIT_SOURCE_PENDING_ENTRY_FOR_EXPENSE;
import static org.kuali.test.fixtures.GeneralLedgerPendingEntryFixture.EXPECTED_AV_EXPLICIT_TARGET_PENDING_ENTRY;
import static org.kuali.test.fixtures.GeneralLedgerPendingEntryFixture.EXPECTED_AV_EXPLICIT_TARGET_PENDING_ENTRY_FOR_EXPENSE;
import static org.kuali.test.fixtures.UserNameFixture.KHUNTLEY;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.DocumentTypeService;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.bo.TargetAccountingLine;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocument;
import org.kuali.kfs.validation.AccountingLineValueAllowedValidation;
import org.kuali.kfs.validation.Validation;
import org.kuali.module.chart.bo.AccountingPeriod;
import org.kuali.module.chart.service.AccountingPeriodService;
import org.kuali.module.financial.document.AuxiliaryVoucherDocument;
import org.kuali.module.financial.document.TransferOfFundsDocument;
import org.kuali.test.ConfigureContext;
import org.kuali.test.DocumentTestUtils;
import org.kuali.test.fixtures.AccountingLineFixture;

@ConfigureContext(session = KHUNTLEY)
public class AuxiliaryVoucherDocumentRuleTest extends KualiTestBase {

    public static final Class<AuxiliaryVoucherDocument> DOCUMENT_CLASS = AuxiliaryVoucherDocument.class;

    public void testIsDebit_debitCode() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), AuxiliaryVoucherDocument.class);
        AccountingLine accountingLine = (AccountingLine) accountingDocument.getSourceAccountingLineClass().newInstance();
        accountingLine.setDebitCreditCode(GL_DEBIT_CODE);

        assertTrue(IsDebitTestUtils.isDebit(SpringContext.getBean(DocumentTypeService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_creditCode() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), AuxiliaryVoucherDocument.class);
        AccountingLine accountingLine = (AccountingLine) accountingDocument.getSourceAccountingLineClass().newInstance();
        accountingLine.setDebitCreditCode(GL_CREDIT_CODE);

        assertFalse(IsDebitTestUtils.isDebit(SpringContext.getBean(DocumentTypeService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_blankValue() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getDocument(SpringContext.getBean(DocumentService.class), AuxiliaryVoucherDocument.class);
        AccountingLine accountingLine = (AccountingLine) accountingDocument.getSourceAccountingLineClass().newInstance();
        accountingLine.setDebitCreditCode(" ");

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(SpringContext.getBean(DocumentTypeService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }


    public void testIsDebit_errorCorrection_debitCode() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getErrorCorrectionDocument(SpringContext.getBean(DocumentService.class), AuxiliaryVoucherDocument.class);
        AccountingLine accountingLine = (AccountingLine) accountingDocument.getSourceAccountingLineClass().newInstance();
        accountingLine.setDebitCreditCode(GL_DEBIT_CODE);

        assertTrue(IsDebitTestUtils.isDebit(SpringContext.getBean(DocumentTypeService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_errorCorrection_creditCode() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getErrorCorrectionDocument(SpringContext.getBean(DocumentService.class), AuxiliaryVoucherDocument.class);
        AccountingLine accountingLine = (AccountingLine) accountingDocument.getSourceAccountingLineClass().newInstance();
        accountingLine.setDebitCreditCode(GL_CREDIT_CODE);

        assertFalse(IsDebitTestUtils.isDebit(SpringContext.getBean(DocumentTypeService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }

    public void testIsDebit_errorCorrection_blankValue() throws Exception {
        AccountingDocument accountingDocument = IsDebitTestUtils.getErrorCorrectionDocument(SpringContext.getBean(DocumentService.class), AuxiliaryVoucherDocument.class);
        AccountingLine accountingLine = (AccountingLine) accountingDocument.getSourceAccountingLineClass().newInstance();
        accountingLine.setDebitCreditCode(" ");

        assertTrue(IsDebitTestUtils.isDebitIllegalStateException(SpringContext.getBean(DocumentTypeService.class), SpringContext.getBean(DataDictionaryService.class), accountingDocument, accountingLine));
    }


    public void testIsObjectTypeAllowed_InvalidObjectType() throws Exception {
        testAddAccountingLineRule_IsObjectTypeAllowed(getInvalidObjectTypeSourceLine(), false);
    }

    public void testIsObjectTypeAllowed_Valid() throws Exception {
        testAddAccountingLineRule_IsObjectTypeAllowed(getValidObjectTypeSourceLine(), true);
    }

    public void testIsObjectCodeAllowed_Valid() throws Exception {
        testAddAccountingLineRule_IsObjectCodeAllowed(DOCUMENT_CLASS, getValidObjectCodeSourceLine(), true);
    }

    public void testAddAccountingLine_InvalidObjectSubType() throws Exception {
        AccountingDocument doc = createDocumentWithInvalidObjectSubType();
        testAddAccountingLineRule_ProcessAddAccountingLineBusinessRules(doc, false);
    }

    public void testAddAccountingLine_Valid() throws Exception {
        AccountingDocument doc = createDocumentWithValidObjectSubType();
        testAddAccountingLineRule_ProcessAddAccountingLineBusinessRules(doc, true);
    }

    public void testIsObjectSubTypeAllowed_InvalidSubType() throws Exception {
        testAddAccountingLine_IsObjectSubTypeAllowed(AccountingLineFixture.LINE17.createSourceAccountingLine(), false);
    }

    public void testIsObjectSubTypeAllowed_ValidSubType() throws Exception {
        testAddAccountingLine_IsObjectSubTypeAllowed(AccountingLineFixture.LINE2.createTargetAccountingLine(), true);
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
        testGenerateGeneralLedgerPendingEntriesRule_ProcessGenerateGeneralLedgerPendingEntries(createDocument(), getExpenseTargetLine(), EXPECTED_AV_EXPLICIT_TARGET_PENDING_ENTRY_FOR_EXPENSE, null);
    }

    public void testProcessGenerateGeneralLedgerPendingEntries_validSourceExpense() throws Exception {

        testGenerateGeneralLedgerPendingEntriesRule_ProcessGenerateGeneralLedgerPendingEntries(createDocument(), getExpenseSourceLine(), EXPECTED_AV_EXPLICIT_SOURCE_PENDING_ENTRY_FOR_EXPENSE, null);
    }

    public void testProcessGenerateGeneralLedgerPendingEntries_validSourceAsset() throws Exception {
        testGenerateGeneralLedgerPendingEntriesRule_ProcessGenerateGeneralLedgerPendingEntries(createDocument(), getAssetSourceLine(), EXPECTED_AV_EXPLICIT_SOURCE_PENDING_ENTRY, null);
    }

    public void testProcessGenerateGeneralLedgerPendingEntries_validTargetAsset() throws Exception {
        testGenerateGeneralLedgerPendingEntriesRule_ProcessGenerateGeneralLedgerPendingEntries(createDocument(), getAssetTargetLine(), EXPECTED_AV_EXPLICIT_TARGET_PENDING_ENTRY, null);
    }

    private AuxiliaryVoucherDocument createDocument() throws Exception {
        // AV document has a restriction on accounting period cannot be more than 2 periods behind current
        return DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), AuxiliaryVoucherDocument.class);
    }

    private AuxiliaryVoucherDocument createDocumentValidForRouting() throws Exception {
        return createDocumentWithValidObjectSubType();
    }

    private AuxiliaryVoucherDocument createDocumentInvalidForSave() throws Exception {
        return createDocumentInvalidDescription();
    }

    private AuxiliaryVoucherDocument createDocumentInvalidDescription() throws Exception {
        AuxiliaryVoucherDocument document = DocumentTestUtils.createDocument(SpringContext.getBean(DocumentService.class), AuxiliaryVoucherDocument.class);

        document.getDocumentHeader().setFinancialDocumentDescription(null);
        return document;
    }

    private AuxiliaryVoucherDocument createDocumentWithValidObjectSubType() throws Exception {
        AuxiliaryVoucherDocument retval = createDocument();
        retval.setSourceAccountingLines(getValidObjectSubTypeSourceLines());
        return retval;
    }

    private SourceAccountingLine getExpenseSourceLine() throws Exception {
        return EXPENSE_GEC_LINE.createAccountingLine(SourceAccountingLine.class, KFSConstants.GL_DEBIT_CODE);
    }

    private TargetAccountingLine getExpenseTargetLine() throws Exception {
        return EXPENSE_LINE.createTargetAccountingLine();
    }

    private TargetAccountingLine getAssetTargetLine() throws Exception {
        return ACCRUED_INCOME_LINE.createAccountingLine(TargetAccountingLine.class, KFSConstants.GL_DEBIT_CODE);
    }

    private TargetAccountingLine getValidObjectSubTypeTargetLine() throws Exception {
        return new TargetAccountingLine();
    }

    private List<SourceAccountingLine> getValidObjectSubTypeSourceLines() throws Exception {
        List<SourceAccountingLine> retval = new ArrayList<SourceAccountingLine>();
        retval.add(LINE15.createAccountingLine(SourceAccountingLine.class, KFSConstants.GL_CREDIT_CODE));
        retval.add(LINE15.createAccountingLine(SourceAccountingLine.class, KFSConstants.GL_DEBIT_CODE));
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
        return ACCRUED_INCOME_LINE.createAccountingLine(SourceAccountingLine.class, KFSConstants.GL_DEBIT_CODE);
    }

    private SourceAccountingLine getAccruedSickPaySourceLineParameter() throws Exception {
        return ACCRUED_SICK_PAY_LINE.createAccountingLine(SourceAccountingLine.class, KFSConstants.GL_DEBIT_CODE);
    }

    /**
     * This tests that the rules are calculating if dates are within accounting period grace periods correctly.
     */
    public void testWithinGracePeriod() {
        AccountingPeriod firstPeriod = SpringContext.getBean(AccountingPeriodService.class).getByPeriod("10", new Integer(2007));
        java.util.Calendar firstPeriodInside = new java.util.GregorianCalendar(2007, java.util.Calendar.MAY, 8);
        java.util.Calendar firstPeriodOutside = new java.util.GregorianCalendar(2007, java.util.Calendar.MAY, 23);
        assertTrue(AuxiliaryVoucherDocumentRule.calculateIfWithinGracePeriod(new java.sql.Date(firstPeriodInside.getTimeInMillis()), firstPeriod));
        assertFalse(AuxiliaryVoucherDocumentRule.calculateIfWithinGracePeriod(new java.sql.Date(firstPeriodOutside.getTimeInMillis()), firstPeriod));

        AccountingPeriod secondPeriod = SpringContext.getBean(AccountingPeriodService.class).getByPeriod("13", new Integer(2006));
        java.util.Calendar secondPeriodInside = new java.util.GregorianCalendar(2006, java.util.Calendar.JULY, 20);
        java.util.Calendar secondPeriodOutside = new java.util.GregorianCalendar(2007, java.util.Calendar.JULY, 21);
        assertTrue(AuxiliaryVoucherDocumentRule.calculateIfWithinGracePeriod(new java.sql.Date(secondPeriodInside.getTimeInMillis()), secondPeriod));
        assertFalse(AuxiliaryVoucherDocumentRule.calculateIfWithinGracePeriod(new java.sql.Date(secondPeriodOutside.getTimeInMillis()), secondPeriod));

    }

    /**
     * This tests that comparable dates are being calculated correctly.
     */
    public void testComparableDateForm() {
        java.util.Calendar firstDate = new java.util.GregorianCalendar(2007, java.util.Calendar.MAY, 8);
        assertEquals(new Integer(AuxiliaryVoucherDocumentRule.comparableDateForm(new java.sql.Date(firstDate.getTimeInMillis()))), new Integer(732683));

        java.util.Calendar secondDate = new java.util.GregorianCalendar(1776, java.util.Calendar.JULY, 4);
        assertEquals(new Integer(AuxiliaryVoucherDocumentRule.comparableDateForm(new java.sql.Date(secondDate.getTimeInMillis()))), new Integer(648426));

        java.util.Calendar thirdDate = new java.util.GregorianCalendar(2007, java.util.Calendar.MAY, 7);
        assertEquals(new Integer(AuxiliaryVoucherDocumentRule.comparableDateForm(new java.sql.Date(thirdDate.getTimeInMillis()))), new Integer(732682));
    }

    /**
     * This tests that the first day of months are being correctly calculated.
     */
    public void testCalculateFirstDayOfMonth() {
        java.util.Calendar cal = new java.util.GregorianCalendar();

        cal.set(java.util.Calendar.MONTH, java.util.Calendar.APRIL);
        cal.set(java.util.Calendar.DAY_OF_MONTH, 15);
        doFirstDayOfMonthTest(cal);

        cal.set(java.util.Calendar.DAY_OF_MONTH, 30);
        doFirstDayOfMonthTest(cal);

        cal.set(java.util.Calendar.DAY_OF_MONTH, 1);
        doFirstDayOfMonthTest(cal);

        cal.set(java.util.Calendar.MONTH, java.util.Calendar.MARCH);
        cal.set(java.util.Calendar.DAY_OF_MONTH, 31);
        doFirstDayOfMonthTest(cal);
    }

    /**
     * This method tests if the given calendar date is the first day of the month, after the rule converts it.
     * 
     * @param cal the calendar to check
     */
    private void doFirstDayOfMonthTest(java.util.Calendar cal) {
        java.sql.Date firstOfMonth = AuxiliaryVoucherDocumentRule.calculateFirstDayOfMonth(new java.sql.Date(cal.getTimeInMillis()));
        java.util.Calendar testCal = new java.util.GregorianCalendar();
        testCal.setTime(firstOfMonth);
        assertEquals(new Integer(1), new Integer(testCal.get(java.util.Calendar.DAY_OF_MONTH)));
    }

    private void testAddAccountingLineRule_IsObjectTypeAllowed(AccountingLine accountingLine, boolean expected) throws Exception  {
        Map<String, Validation> validations = SpringContext.getBeansOfType(Validation.class);
        boolean result = true;
        AuxiliaryVoucherDocument document = createDocument();
        AccountingLineValueAllowedValidation validation = (AccountingLineValueAllowedValidation)validations.get("AccountingDocument-IsObjectTypeAllowed-DefaultValidation"); 
        if (validation == null) throw new IllegalStateException("No object type value allowed validation");
        validation.setAccountingDocumentForValidation(document);
        validation.setAccountingLineForValidation(accountingLine);
        result = validation.validate(null);
        assertEquals(expected, result);
    }
    
    private void testAddAccountingLine_IsObjectSubTypeAllowed(AccountingLine accountingLine, boolean expected) throws Exception  {
        Map<String, Validation> validations = SpringContext.getBeansOfType(Validation.class);
        boolean result = true;
        AuxiliaryVoucherDocument document = createDocument();
        AccountingLineValueAllowedValidation validation = (AccountingLineValueAllowedValidation)validations.get("AccountingDocument-IsObjectSubTypeAllowed-DefaultValidation");
        if (validation == null) throw new IllegalStateException("No object sub type value allowed validation");
        validation.setAccountingDocumentForValidation(document);
        validation.setAccountingLineForValidation(accountingLine);
        result = validation.validate(null);
        assertEquals(expected, result);
    }
}
