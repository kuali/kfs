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
package org.kuali.module.financial.web.struts.form;

import static org.kuali.kfs.KFSConstants.VOUCHER_LINE_HELPER_CREDIT_PROPERTY_NAME;
import static org.kuali.kfs.KFSConstants.VOUCHER_LINE_HELPER_DEBIT_PROPERTY_NAME;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.AmountTotaling;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.web.format.CurrencyFormatter;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.kfs.web.struts.form.KualiAccountingDocumentFormBase;
import org.kuali.module.chart.bo.AccountingPeriod;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.SubObjCd;
import org.kuali.module.financial.bo.VoucherAccountingLineHelper;
import org.kuali.module.financial.bo.VoucherAccountingLineHelperBase;
import org.kuali.module.financial.document.VoucherDocument;

/**
 * This class is the Struts specific form object that works in conjunction with the pojo utilities to build the UI for Voucher
 * Document instances. This class is unique in that it leverages a helper data structure called the
 * <code>{@link VoucherAccountingLineHelper}</code> because Voucher documents, under some/none conditions, presents the user with
 * a debit and credit column for amount entry. New accounting lines use specific credit and debit amount fields b/c the new line is
 * explicitly known; however, already existing accounting lines need to exist within a list with ordering that matches the
 * accounting lines source list.
 */
public class VoucherForm extends KualiAccountingDocumentFormBase {
    private List accountingPeriods;
    private KualiDecimal newSourceLineDebit;
    private KualiDecimal newSourceLineCredit;
    private List voucherLineHelpers;
    protected String selectedAccountingPeriod;

    /**
     * Supplements a constructor for this voucher class
     */
    public VoucherForm() {
        populateDefaultSelectedAccountingPeriod();
        setNewSourceLineCredit(KualiDecimal.ZERO);
        setNewSourceLineDebit(KualiDecimal.ZERO);
        setVoucherLineHelpers(new ArrayList());
    }

    /**
     * sets initial selected accounting period to current period
     * 
     */
    private void populateDefaultSelectedAccountingPeriod() {
        Date date = SpringServiceLocator.getDateTimeService().getCurrentSqlDate();
        AccountingPeriod accountingPeriod = SpringServiceLocator.getAccountingPeriodService().getByDate(date);

        StringBuffer sb = new StringBuffer();
        sb.append(accountingPeriod.getUniversityFiscalPeriodCode());
        sb.append(accountingPeriod.getUniversityFiscalYear());

        setSelectedAccountingPeriod(sb.toString());
    }

    /**
     * Overrides the parent to call super.populate and then to call the two methods that are specific to loading the two select
     * lists on the page. In addition, this also makes sure that the credit and debit amounts are filled in for situations where
     * validation errors occur and the page reposts.
     * 
     * @see org.kuali.core.web.struts.pojo.PojoForm#populate(javax.servlet.http.HttpServletRequest)
     */
    @Override
    public void populate(HttpServletRequest request) {
        super.populate(request);

        // populate the drop downs
        if (KFSConstants.RETURN_METHOD_TO_CALL.equals(getMethodToCall())) {
            String selectedPeriod = (StringUtils.defaultString(request.getParameter(KFSPropertyConstants.UNIVERSITY_FISCAL_PERIOD_CODE)) + StringUtils.defaultString(request.getParameter(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR)));
            if (StringUtils.isNotBlank(selectedPeriod)) {
                setSelectedAccountingPeriod(selectedPeriod);
            }
        }
        populateAccountingPeriodListForRendering();

        // we don't want to do this if we are just reloading the document
        if (StringUtils.isBlank(getMethodToCall()) || !getMethodToCall().equals(KFSConstants.RELOAD_METHOD_TO_CALL)) {
            // make sure the amount fields are populated appropriately when in debit/credit amount mode
            populateCreditAndDebitAmounts();
        }
    }

    /**
     * util method to get postingYear out of selectedAccountingPeriod
     * 
     * @return Integer
     */

    private Integer getSelectedPostingYear() {
        Integer postingYear = null;
        if (StringUtils.isNotBlank(getSelectedAccountingPeriod())) {
            postingYear = new Integer(StringUtils.right(getSelectedAccountingPeriod(), 4));
        }
        return postingYear;
    }

    /**
     * util method to get posting period code out of selectedAccountingPeriod
     * 
     * @return String
     */
    private String getSelectedPostingPeriodCode() {
        String periodCode = null;
        String selectedPeriod = getSelectedAccountingPeriod();
        if (StringUtils.isNotBlank(selectedPeriod)) {
            periodCode = StringUtils.left(selectedPeriod, 2);
        }
        return periodCode;
    }

    /**
     * Helper method to make casting easier
     * 
     * @return VoucherDocument
     */
    public VoucherDocument getVoucherDocument() {
        return (VoucherDocument) getDocument();
    }

    /**
     * Override the parent, to push the chosen accounting period and balance type down into the source accounting line object. In
     * addition, check the balance type to see if it's the "External Encumbrance" balance and alter the encumbrance update code on
     * the accounting line appropriately.
     * 
     * @see org.kuali.core.web.struts.form.KualiTransactionalDocumentFormBase#populateSourceAccountingLine(org.kuali.core.bo.SourceAccountingLine)
     */
    @Override
    public void populateSourceAccountingLine(SourceAccountingLine sourceLine) {
        super.populateSourceAccountingLine(sourceLine);

        // set the chosen accounting period into the line
        String selectedAccountingPeriod = getSelectedAccountingPeriod();

        if (StringUtils.isNotBlank(selectedAccountingPeriod)) {
            Integer postingYear = getSelectedPostingYear();
            sourceLine.setPostingYear(postingYear);

            if (ObjectUtils.isNull(sourceLine.getObjectCode())) {
                sourceLine.setObjectCode(new ObjectCode());
            }
            sourceLine.getObjectCode().setUniversityFiscalYear(postingYear);

            if (ObjectUtils.isNull(sourceLine.getSubObjectCode())) {
                sourceLine.setSubObjectCode(new SubObjCd());
            }
            sourceLine.getSubObjectCode().setUniversityFiscalYear(postingYear);
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
     * This method returns the reversal date in the format MMM d, yyyy.
     * 
     * @return String
     */
    public String getFormattedReversalDate() {
        return formatReversalDate(getVoucherDocument().getReversalDate());
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

        if (!StringUtils.isBlank(getSelectedAccountingPeriod())) {
            period = SpringServiceLocator.getAccountingPeriodService().getByPeriod(getSelectedPostingPeriodCode(), getSelectedPostingYear());
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
     * Accessor to the list of <code>{@link VoucherAccountingLineHelper}</code> instances. This method retrieves the list of
     * helper line objects for the form.
     * 
     * @return List
     */
    public List getVoucherLineHelpers() {
        return voucherLineHelpers;
    }

    /**
     * This method retrieves the proper voucher helper line data structure at the passed in list index so that it matches up with
     * the correct accounting line at that index.
     * 
     * @param index
     * @return VoucherAccountingLineHelper
     */
    public VoucherAccountingLineHelper getVoucherLineHelper(int index) {
        while (getVoucherLineHelpers().size() <= index) {
            getVoucherLineHelpers().add(new VoucherAccountingLineHelperBase());
        }
        return (VoucherAccountingLineHelper) getVoucherLineHelpers().get(index);
    }

    /**
     * This method sets the list of helper lines for the form.
     * 
     * @param voucherLineHelpers
     */
    public void setVoucherLineHelpers(List voucherLineHelpers) {
        this.voucherLineHelpers = voucherLineHelpers;
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
     * This method retrieves the voucher's debit total formatted as currency.
     * 
     * @return String
     */
    public String getCurrencyFormattedDebitTotal() {
        return (String) new CurrencyFormatter().format(getVoucherDocument().getDebitTotal());
    }

    /**
     * This method retrieves the voucher's credit total formatted as currency.
     * 
     * @return String
     */
    public String getCurrencyFormattedCreditTotal() {
        return (String) new CurrencyFormatter().format(getVoucherDocument().getCreditTotal());
    }

    /**
     * This method retrieves the voucher's total formatted as currency.
     * 
     * @return String
     */
    public String getCurrencyFormattedTotal() {
        return (String) new CurrencyFormatter().format(((AmountTotaling) getVoucherDocument()).getTotalDollarAmount());
    }

    /**
     * This method retrieves all of the "open for posting" accounting periods and prepares them to be rendered in a dropdown UI
     * component.
     */
    protected void populateAccountingPeriodListForRendering() {
        // grab the list of valid accounting periods
        ArrayList accountingPeriods = new ArrayList(SpringServiceLocator.getAccountingPeriodService().getOpenAccountingPeriods());
        // set into the form for rendering
        setAccountingPeriods(accountingPeriods);
        // set the chosen accounting period into the form
        populateSelectedVoucherAccountingPeriod();
    }


    /**
     * This method parses the accounting period value from the form and builds a basic AccountingPeriod object so that the voucher
     * is properly persisted with the accounting period set for it.
     */
    protected void populateSelectedVoucherAccountingPeriod() {
        if (StringUtils.isNotBlank(getSelectedAccountingPeriod())) {
            AccountingPeriod ap = new AccountingPeriod();
            ap.setUniversityFiscalPeriodCode(getSelectedPostingPeriodCode());
            ap.setUniversityFiscalYear(getSelectedPostingYear());
            getFinancialDocument().setAccountingPeriod(ap);
        }
    }

    /**
     * If the balance type is an offset generation balance type, then the user is able to enter the amount as either a debit or a
     * credit, otherwise, they only need to deal with the amount field in this case we always need to update the underlying bo so
     * that the debit/credit code along with the amount, is properly set.
     */
    protected void populateCreditAndDebitAmounts() {
        processDebitAndCreditForNewSourceLine();
        processDebitAndCreditForAllSourceLines();
    }

    /**
     * This method uses the newly entered debit and credit amounts to populate the new source line that is to be added to the
     * voucher document.
     * 
     * @return boolean True if the processing was successful, false otherwise.
     */
    protected boolean processDebitAndCreditForNewSourceLine() {
        // using debits and credits supplied, populate the new source accounting line's amount and debit/credit code appropriately
        boolean passed = processDebitAndCreditForSourceLine(getNewSourceLine(), newSourceLineDebit, newSourceLineCredit, KFSConstants.NEGATIVE_ONE);

        return passed;
    }

    /**
     * This method iterates through all of the source accounting lines associated with the voucher doc and accounts for any changes
     * to the credit and debit amounts, populate the source lines' amount and debit/credit code fields appropriately, so that they
     * can be persisted accurately. This accounts for the fact that users may change the amounts and/or flip-flop the credit debit
     * amounts on any accounting line after the initial add of the accounting line.
     * 
     * @return boolean
     */
    protected boolean processDebitAndCreditForAllSourceLines() {
        VoucherDocument vDoc = getVoucherDocument();

        // iterate through all of the source accounting lines
        boolean validProcessing = true;
        for (int i = 0; i < vDoc.getSourceAccountingLines().size(); i++) {
            // retrieve the proper business objects from the form
            SourceAccountingLine sourceLine = vDoc.getSourceAccountingLine(i);
            VoucherAccountingLineHelper voucherLineHelper = getVoucherLineHelper(i);

            // now process the amounts and the line
            // we want to process all lines, some may be invalid b/c of dual amount values, but this method will handle
            // only processing the valid ones, that way we are guaranteed that values in the valid lines carry over through the
            // post and invalid ones do not alter the underlying business object
            validProcessing &= processDebitAndCreditForSourceLine(sourceLine, voucherLineHelper.getDebit(), voucherLineHelper.getCredit(), i);
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
    protected boolean processDebitAndCreditForSourceLine(SourceAccountingLine sourceLine, KualiDecimal debitAmount, KualiDecimal creditAmount, int index) {
        // check to make sure that the
        if (!validateCreditAndDebitAmounts(debitAmount, creditAmount, index)) {
            return false;
        }

        // check to see which amount field has a value - credit or debit field?
        // and set the values of the appropriate fields
        if (debitAmount != null && debitAmount.isNonZero()) { // a value entered into the debit field? if so it's a debit
            // create a new instance w/out reference
            KualiDecimal tmpDebitAmount = new KualiDecimal(debitAmount.toString());
            sourceLine.setDebitCreditCode(KFSConstants.GL_DEBIT_CODE);
            sourceLine.setAmount(tmpDebitAmount);
        }
        else if (creditAmount != null && creditAmount.isNonZero()) { // assume credit, if both are set the br eval framework will
            // catch it
            KualiDecimal tmpCreditAmount = new KualiDecimal(creditAmount.toString());
            sourceLine.setDebitCreditCode(KFSConstants.GL_CREDIT_CODE);
            sourceLine.setAmount(tmpCreditAmount);
        }
        else { // explicitly set to zero, let br eval framework pick it up
            sourceLine.setDebitCreditCode(null);
            sourceLine.setAmount(KualiDecimal.ZERO);
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
    protected boolean validateCreditAndDebitAmounts(KualiDecimal debitAmount, KualiDecimal creditAmount, int index) {
        boolean valid = false;
        if (null != creditAmount && null != debitAmount) {
            if (creditAmount.isNonZero() && debitAmount.isNonZero()) {
                // there's a value in both fields
                if (KFSConstants.NEGATIVE_ONE == index) { // it's a new line
                    GlobalVariables.getErrorMap().putErrorWithoutFullErrorPath(KFSConstants.DEBIT_AMOUNT_PROPERTY_NAME, KFSKeyConstants.ERROR_DOCUMENT_JV_AMOUNTS_IN_CREDIT_AND_DEBIT_FIELDS);
                    GlobalVariables.getErrorMap().putErrorWithoutFullErrorPath(KFSConstants.CREDIT_AMOUNT_PROPERTY_NAME, KFSKeyConstants.ERROR_DOCUMENT_JV_AMOUNTS_IN_CREDIT_AND_DEBIT_FIELDS);
                }
                else {
                    String errorKeyPath = KFSConstants.JOURNAL_LINE_HELPER_PROPERTY_NAME + KFSConstants.SQUARE_BRACKET_LEFT + Integer.toString(index) + KFSConstants.SQUARE_BRACKET_RIGHT;
                    GlobalVariables.getErrorMap().putErrorWithoutFullErrorPath(errorKeyPath + VOUCHER_LINE_HELPER_DEBIT_PROPERTY_NAME, KFSKeyConstants.ERROR_DOCUMENT_JV_AMOUNTS_IN_CREDIT_AND_DEBIT_FIELDS);
                    GlobalVariables.getErrorMap().putErrorWithoutFullErrorPath(errorKeyPath + VOUCHER_LINE_HELPER_CREDIT_PROPERTY_NAME, KFSKeyConstants.ERROR_DOCUMENT_JV_AMOUNTS_IN_CREDIT_AND_DEBIT_FIELDS);
                }
            }
            else {
                valid = true;
            }
        }
        else {
            valid = true;
        }
        return valid;
    }
}