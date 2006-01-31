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
package org.kuali.module.financial.web.struts.form;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.core.bo.SourceAccountingLine;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.web.format.CurrencyFormatter;
import org.kuali.core.web.struts.form.KualiTransactionalDocumentFormBase;
import org.kuali.module.chart.bo.AccountingPeriod;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.SubObjCd;
import org.kuali.module.chart.bo.codes.BalanceTyp;
import org.kuali.module.chart.service.BalanceTypService;
import org.kuali.module.financial.bo.JournalVoucherAccountingLineHelper;
import org.kuali.module.financial.document.JournalVoucherDocument;

/**
 * This class is the Struts specific form object that works in conjunction with the pojo utilities to build the UI for the Journal
 * Voucher Document. This class is unique in that it leverages a helper data structure called the JournalVoucherAccountingLineHelper
 * because the Journal Voucher, under certain conditions, presents the user with a debit and credit column for amount entry. In
 * addition, this form class must keep track of the changes between the old and new balance type selection so that the corresponding
 * action class and make decisions based upon the differences. New accounting lines use specific credit and debit amount fields b/c
 * the new line is explicitly known; however, already existing accounting lines need to exist within a list with ordering that
 * matches the accounting lines source list.
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
 */
public class JournalVoucherForm extends KualiTransactionalDocumentFormBase {
    private static final long serialVersionUID = 1L;
    private List balanceTypes;
    private String originalBalanceType;
    private BalanceTyp selectedBalanceType;
    private List accountingPeriods;
    private String selectedAccountingPeriod;
    private KualiDecimal newSourceLineDebit;
    private KualiDecimal newSourceLineCredit;
    private List journalLineHelpers;

    /**
     * Constructs a JournalVoucherForm instance.
     */
    public JournalVoucherForm() {
        super();
        setDocument(new JournalVoucherDocument());
        selectedBalanceType = new BalanceTyp();
        selectedAccountingPeriod = "";
        originalBalanceType = "";
        setNewSourceLineCredit(new KualiDecimal(0));
        setNewSourceLineDebit(new KualiDecimal(0));
        setJournalLineHelpers(new ArrayList());
    }

    /**
     * Overrides the parent to call super.populate and then to call the two methods that are specific to loading the two select
     * lists on the page.  In addition, this also makes sure that the credit and debit amounts are filled in for situations where 
     * validation errors occur and the page reposts.
     * 
     * @see org.kuali.core.web.struts.pojo.PojoForm#populate(javax.servlet.http.HttpServletRequest)
     */
    public void populate(HttpServletRequest request) {
        super.populate(request);
        
        // populate the drop downs
        populateAccountingPeriodListForRendering();
        populateBalanceTypeListForRendering();
        
        // we don't want to do this if we are just reloading the document
        if(StringUtils.isBlank(getMethodToCall()) || !getMethodToCall().equals(Constants.RELOAD_METHOD_TO_CALL)) {
            // make sure the amount fields are populated appropriately when in debit/credit amount mode
            populateCreditAndDebitAmounts();
        }
    }

    /**
     * Override the parent, to push the chosen accounting period and balance type down into the source accounting line object. In
     * addition, check the balance type to see if it's the "External Encumbrance" balance and alter the encumbrance update code on
     * the accounting line appropriately.
     * 
     * @see org.kuali.core.web.struts.form.KualiTransactionalDocumentFormBase#populateSourceAccountingLine(org.kuali.core.bo.SourceAccountingLine)
     */
    public void populateSourceAccountingLine(SourceAccountingLine sourceLine) {
        super.populateSourceAccountingLine(sourceLine);

        // set the chosen accounting period into the line
        String selectedAccountingPeriod = getSelectedAccountingPeriod();

        if (StringUtils.isNotBlank(selectedAccountingPeriod)) {
            Integer postingYear = new Integer(StringUtils.right(selectedAccountingPeriod, 4));
            sourceLine.setPostingYear(postingYear);
            
            if(ObjectUtils.isNull(sourceLine.getObjectCode())) {
                sourceLine.setObjectCode(new ObjectCode());
            }
            sourceLine.getObjectCode().setUniversityFiscalYear(postingYear);
            
            if(ObjectUtils.isNull(sourceLine.getSubObjectCode())) {
                sourceLine.setSubObjectCode(new SubObjCd());
            }
            sourceLine.getSubObjectCode().setUniversityFiscalYear(postingYear);
        }

        // set the chosen balance type into the line
        BalanceTyp selectedBalanceType = getSelectedBalanceType();

        if (selectedBalanceType != null && StringUtils.isNotBlank(selectedBalanceType.getCode())) {
            sourceLine.setBalanceTyp(selectedBalanceType);
            sourceLine.setBalanceTypeCode(selectedBalanceType.getCode());

            // set the encumbrance update code appropriately
            if (Constants.BALANCE_TYPE_EXTERNAL_ENCUMBRANCE.equals(selectedBalanceType.getCode())) {
                sourceLine
                        .setEncumbranceUpdateCode(Constants.JOURNAL_VOUCHER_ENCUMBRANCE_UPDATE_CODE_BALANCE_TYPE_EXTERNAL_ENCUMBRANCE);
            }
            else {
                sourceLine.setEncumbranceUpdateCode(null);
            }
        }
        else {
            // it's the first time in, the form will be empty the first time in
            // set up default selection
            selectedBalanceType = SpringServiceLocator.getBalanceTypService().getBalanceTypByCode(Constants.BALANCE_TYPE_ACTUAL); // default
            // value
            setSelectedBalanceType(selectedBalanceType);
            setOriginalBalanceType(selectedBalanceType.getCode());

            sourceLine.setEncumbranceUpdateCode(null);
        }
    }

    /**
     * This method retrieves the list of valid accounting periods to display.
     * 
     * @return List
     */
    public List getAccountingPeriods() {
        return accountingPeriods;
    }

    /**
     * This method sets the list of valid accounting periods to display.
     * 
     * @param accountingPeriods
     */
    public void setAccountingPeriods(List accountingPeriods) {
        this.accountingPeriods = accountingPeriods;
    }

    /**
     * This method retrieves the list of valid balance types to display.
     * 
     * @return List
     */
    public List getBalanceTypes() {
        return balanceTypes;
    }

    /**
     * This method sets the selected balance type.
     * 
     * @return BalanceTyp
     */
    public BalanceTyp getSelectedBalanceType() {
        return selectedBalanceType;
    }

    /**
     * This method retrieves the selected balance type.
     * 
     * @param selectedBalanceType
     */
    public void setSelectedBalanceType(BalanceTyp selectedBalanceType) {
        this.selectedBalanceType = selectedBalanceType;
    }

    /**
     * This method sets the list of valid balance types to display.
     * 
     * @param balanceTypes
     */
    public void setBalanceTypes(List balanceTypes) {
        this.balanceTypes = balanceTypes;
    }

    /**
     * This method returns the journal voucher document associated with this form.
     * 
     * @return Returns the journalVoucherDocument.
     */
    public JournalVoucherDocument getJournalVoucherDocument() {
        return (JournalVoucherDocument) getTransactionalDocument();
    }

    /**
     * This method sets the journal voucher document associated with this form.
     * 
     * @param journalVoucherDocument The journalVoucherDocument to set.
     */
    public void setJournalVoucherDocument(JournalVoucherDocument journalVoucherDocument) {
        setDocument(journalVoucherDocument);
    }

    /**
     * This method retrieves the selectedAccountingPeriod.
     * 
     * @return String
     */
    public String getSelectedAccountingPeriod() {
        return selectedAccountingPeriod;
    }

    /**
     * @return AccountingPeriod associated with the currently selected period
     */
    public AccountingPeriod getAccountingPeriod() {
        AccountingPeriod period = null;

        String selectedPeriod = getSelectedAccountingPeriod();
        if (!StringUtils.isBlank(selectedPeriod)) {
            String periodCode = StringUtils.left(selectedPeriod, selectedPeriod.length() - 4);
            Integer periodYear = new Integer(StringUtils.right(selectedPeriod, 4));

            period = SpringServiceLocator.getAccountingPeriodService().getByPeriod(periodCode, periodYear);
        }

        return period;
    }

    /**
     * This method sets the selectedAccountingPeriod.
     * 
     * @param selectedAccountingPeriod
     */
    public void setSelectedAccountingPeriod(String selectedAccountingPeriod) {
        this.selectedAccountingPeriod = selectedAccountingPeriod;
    }

    /**
     * This method retrieves the proper journal helper line data structure at the passed in list index so that it matches up with
     * the correct accounting line at that index.
     * 
     * @param index
     * @return JournalVoucherAccountingLineHelper
     */
    public JournalVoucherAccountingLineHelper getJournalLineHelper(int index) {
        while (this.journalLineHelpers.size() <= index) {
            this.journalLineHelpers.add(new JournalVoucherAccountingLineHelper());
        }
        return (JournalVoucherAccountingLineHelper) this.journalLineHelpers.get(index);
    }

    /**
     * This method retrieves the list of helper line objects for the form.
     * 
     * @return List
     */
    public List getJournalLineHelpers() {
        return journalLineHelpers;
    }

    /**
     * This method sets the list of helper lines for the form.
     * 
     * @param journalLineHelpers
     */
    public void setJournalLineHelpers(List journalLineHelpers) {
        this.journalLineHelpers = journalLineHelpers;
    }

    /**
     * This method retrieves the credit amount of the new accounting line that was added.
     * 
     * @return KualiDecimal
     */
    public KualiDecimal getNewSourceLineCredit() {
        return newSourceLineCredit;
    }

    /**
     * This method sets the credit amount of the new accounting line that was added.
     * 
     * @param newSourceLineCredit
     */
    public void setNewSourceLineCredit(KualiDecimal newSourceLineCredit) {
        this.newSourceLineCredit = newSourceLineCredit;
    }

    /**
     * This method retrieves the debit amount of the new accounting line that was added.
     * 
     * @return KualiDecimal
     */
    public KualiDecimal getNewSourceLineDebit() {
        return newSourceLineDebit;
    }

    /**
     * This method sets the debit amount of the new accounting line that was added.
     * 
     * @param newSourceLineDebit
     */
    public void setNewSourceLineDebit(KualiDecimal newSourceLineDebit) {
        this.newSourceLineDebit = newSourceLineDebit;
    }

    /**
     * This method retrieves the originalBalanceType attribute.
     * 
     * @return String
     */
    public String getOriginalBalanceType() {
        return originalBalanceType;
    }

    /**
     * This method sets the originalBalanceType attribute.
     * 
     * @param changedBalanceType
     */
    public void setOriginalBalanceType(String changedBalanceType) {
        this.originalBalanceType = changedBalanceType;
    }

    /**
     * This method retrieves the JV's debit total formatted as currency.
     * 
     * @return String
     */
    public String getCurrencyFormattedDebitTotal() {
        return (String) new CurrencyFormatter().format(getJournalVoucherDocument().getDebitTotal());
    }

    /**
     * This method retrieves the JV's credit total formatted as currency.
     * 
     * @return String
     */
    public String getCurrencyFormattedCreditTotal() {
        return (String) new CurrencyFormatter().format(getJournalVoucherDocument().getCreditTotal());
    }

    /**
     * This method retrieves the JV's total formatted as currency.
     * 
     * @return String
     */
    public String getCurrencyFormattedTotal() {
        return (String) new CurrencyFormatter().format(getJournalVoucherDocument().getTotal());
    }

    // Helper Methods for Populating the Accounting Period and Balance Type Select Lists
    /**
     * This method retrieves all of the balance types in the system and prepares them to be rendered in a dropdown UI component.
     */
    private void populateBalanceTypeListForRendering() {
        // grab the list of valid balance types
        ArrayList balanceTypes = new ArrayList(SpringServiceLocator.getBalanceTypService().getAllBalanceTyps());

        // set into the form for rendering
        this.setBalanceTypes(balanceTypes);

        // set the chosen balance type into the form
        BalanceTyp selectedBalanceType = getSelectedBalanceType();

        if (selectedBalanceType != null && StringUtils.isNotBlank(selectedBalanceType.getCode())) {
            selectedBalanceType = getPopulatedBalanceTypeInstance(selectedBalanceType.getCode());
            setSelectedBalanceType(selectedBalanceType);
            getJournalVoucherDocument().setBalanceTypeCode(selectedBalanceType.getCode());
        }
        else { // it's the first time in, the form will be empty the first time in
            // set up default selection
            selectedBalanceType = SpringServiceLocator.getBalanceTypService().getBalanceTypByCode(Constants.BALANCE_TYPE_ACTUAL); // default
            // value
            setSelectedBalanceType(selectedBalanceType);
            setOriginalBalanceType(selectedBalanceType.getCode());
        }
    }

    /**
     * This method retrieves all of the "open for posting" accounting periods and prepares them to be rendered in a dropdown UI
     * component.
     */
    private void populateAccountingPeriodListForRendering() {
        // grab the list of valid accounting periods
        ArrayList accountingPeriods = new ArrayList(SpringServiceLocator.getAccountingPeriodService().getOpenAccountingPeriods());

        // set into the form for rendering
        setAccountingPeriods(accountingPeriods);

        // set the chosen accounting period into the form
        String selectedAccountingPeriod = getSelectedAccountingPeriod();

        if (StringUtils.isNotBlank(selectedAccountingPeriod)) {
            AccountingPeriod ap = new AccountingPeriod();
            ap.setUniversityFiscalPeriodCode(StringUtils.left(selectedAccountingPeriod, 2));
            ap.setUniversityFiscalYear(new Integer(StringUtils.right(selectedAccountingPeriod, 4)));
            getJournalVoucherDocument().setPostingPeriodCode(ap.getUniversityFiscalPeriodCode());
            getJournalVoucherDocument().setPostingYear(ap.getUniversityFiscalYear());
        }
    }

    /**
     * This method will fully populate a balance type given the passed in code, by calling the business object service that
     * retrieves the rest of the instances' information.
     * 
     * @param balanceTypeCode
     * @return BalanceTyp
     */
    private BalanceTyp getPopulatedBalanceTypeInstance(String balanceTypeCode) {
        // now we have to get the code and the name of the original and new balance types
        BalanceTypService bts = SpringServiceLocator.getBalanceTypService();
        return bts.getBalanceTypByCode(balanceTypeCode);
    }
    
    /**
     * If the balance type is an offset generation balance type, then the user is able to enter the amount
     * as either a debit or a credit, otherwise, they only need to deal with the amount field
     * in this case we always need to update the underlying bo so that the debit/credit code along with 
     * the amount, is properly set.
     */
    private void populateCreditAndDebitAmounts() {
        if (isSelectedBalanceTypeFinancialOffsetGenerationIndicator()) {
            processDebitAndCreditForNewSourceLine();
            processDebitAndCreditForAllSourceLines();
        }
    }
    
    /**
     * This is a convenience helper method that is used several times throughout this action class to determine if the selected
     * balance type contained within the form instance is a financial offset generation balance type or not.
     * 
     * @return boolean True if it is an offset generation balance type, false otherwise.
     */
    private boolean isSelectedBalanceTypeFinancialOffsetGenerationIndicator() {
        return getPopulatedBalanceTypeInstance(getSelectedBalanceType().getCode()).isFinancialOffsetGenerationIndicator();
    }
    
    /**
     * This method uses the newly entered debit and credit amounts to populate the new source line that is to be added to the JV
     * document.
     * 
     * @return boolean True if the processing was successful, false otherwise.
     */
    private boolean processDebitAndCreditForNewSourceLine() {
        // using debits and credits supplied, populate the new source accounting line's amount and debit/credit code appropriately
        if (!processDebitAndCreditForSourceLine(newSourceLine, newSourceLineDebit, newSourceLineCredit, Constants.NEGATIVE_ONE)) {
            return false;
        }
        else {
            return true;
        }
    }
    
    /**
     * This method iterates through all of the source accounting lines associated with the JV doc and accounts for any changes to
     * the credit and debit amounts, populate the source lines' amount and debit/credit code fields appropriately, so that they can
     * be persisted accurately. This accounts for the fact that users may change the amounts and/or flip-flop the credit debit
     * amounts on any accounting line after the initial add of the accounting line.
     * 
     * @return boolean
     */
    private boolean processDebitAndCreditForAllSourceLines() {
        JournalVoucherDocument jvDoc = getJournalVoucherDocument();

        // iterate through all of the source accounting lines
        boolean validProcessing = true;
        for (int i = 0; i < jvDoc.getSourceAccountingLines().size(); i++) {
            // retrieve the proper business objects from the form
            SourceAccountingLine sourceLine = jvDoc.getSourceAccountingLine(i);
            JournalVoucherAccountingLineHelper journalLineHelper = getJournalLineHelper(i);

            // now process the amounts and the line
            // we want to process all lines, some may be invalid b/c of dual amount values, but this method will handle
            // only processing the valid ones, that way we are guaranteed that values in the valid lines carry over through the
            // post and invalid ones do not alter the underlying business object
            validProcessing &= processDebitAndCreditForSourceLine(sourceLine, journalLineHelper.getDebit(), journalLineHelper
                    .getCredit(), i);
        }
        return validProcessing;
    }
    
    /**
     * This method checks the debit and credit attributes passed in, figures out which one has a value, and sets the source
     * accounting line's amount and debit/credit attribute appropriately. It assumes that if it finds something in the debit field,
     * it's a debit entry, otherwise it's a credit entry. If a user enters a value into both fields, it will assume the debit value,
     * then when the br eval framework applies the "add" rule, it will bomb out. If first checks to make sure that there isn't a
     * value in both the credit and debit columns.
     * 
     * @param sourceLine
     * @param debitAmount
     * @param creditAmount
     * @param index if -1, then its a new line, if not -1 then it's an existing line
     * @return boolean True if the processing was successful, false otherwise.
     */
    private boolean processDebitAndCreditForSourceLine(SourceAccountingLine sourceLine, KualiDecimal debitAmount,
            KualiDecimal creditAmount, int index) {
        // check to make sure that the
        if (!validateCreditAndDebitAmounts(debitAmount, creditAmount, index)) {
            return false;
        }

        // check to see which amount field has a value - credit or debit field?
        // and set the values of the appropriate fields
        KualiDecimal ZERO = new KualiDecimal("0.00");
        if (debitAmount != null && debitAmount.compareTo(ZERO) != 0) { // a value entered into the debit field? if so it's a debit
            // create a new instance w/out reference
            KualiDecimal tmpDebitAmount = new KualiDecimal(debitAmount.toString());
            sourceLine.setDebitCreditCode(Constants.GL_DEBIT_CODE);
            sourceLine.setAmount(tmpDebitAmount);
        }
        else if (creditAmount != null && !creditAmount.equals(ZERO)) { // assume credit, if both are set the br eval framework will
            // catch it
            KualiDecimal tmpCreditAmount = new KualiDecimal(creditAmount.toString());
            sourceLine.setDebitCreditCode(Constants.GL_CREDIT_CODE);
            sourceLine.setAmount(tmpCreditAmount);
        } else {  //explicitly set to zero, let br eval framework pick it up
            sourceLine.setDebitCreditCode(null);
            sourceLine.setAmount(ZERO);
        }

        return true;
    }
    
    /**
     * This method checks to make sure that there isn't a value in both the credit and debit columns for a given accounting line.
     * 
     * @param creditAmount
     * @param debitAmount
     * @param index if -1, it's a new line, if not -1, then its an existing line
     * @return boolean False if both the credit and debit fields have a value, true otherwise.
     */
    private boolean validateCreditAndDebitAmounts(KualiDecimal debitAmount, KualiDecimal creditAmount, int index) {
        KualiDecimal ZERO = new KualiDecimal(0);
        if (null != creditAmount && null != debitAmount) {
            if (ZERO.compareTo(creditAmount) != 0 && ZERO.compareTo(debitAmount) != 0) { // there's a value in both fields
                if(Constants.NEGATIVE_ONE == index) {  //it's a new line
                    GlobalVariables.getErrorMap().putWithoutFullErrorPath(Constants.DEBIT_AMOUNT_PROPERTY_NAME, 
                            KeyConstants.ERROR_DOCUMENT_JV_AMOUNTS_IN_CREDIT_AND_DEBIT_FIELDS);
                    GlobalVariables.getErrorMap().putWithoutFullErrorPath(Constants.CREDIT_AMOUNT_PROPERTY_NAME, 
                            KeyConstants.ERROR_DOCUMENT_JV_AMOUNTS_IN_CREDIT_AND_DEBIT_FIELDS);
                } else {
                    String errorKeyPath = Constants.JOURNAL_LINE_HELPER_PROPERTY_NAME + Constants.SQUARE_BRACKET_LEFT + Integer.toString(index) + Constants.SQUARE_BRACKET_RIGHT;
                    GlobalVariables.getErrorMap().putWithoutFullErrorPath(errorKeyPath + Constants.JOURNAL_LINE_HELPER_DEBIT_PROPERTY_NAME, 
                            KeyConstants.ERROR_DOCUMENT_JV_AMOUNTS_IN_CREDIT_AND_DEBIT_FIELDS);
                    GlobalVariables.getErrorMap().putWithoutFullErrorPath(errorKeyPath + Constants.JOURNAL_LINE_HELPER_CREDIT_PROPERTY_NAME, 
                            KeyConstants.ERROR_DOCUMENT_JV_AMOUNTS_IN_CREDIT_AND_DEBIT_FIELDS);
                }
                return false;
            }
            else {
                return true;
            }
        } else {
            return true;
        }
    }
}