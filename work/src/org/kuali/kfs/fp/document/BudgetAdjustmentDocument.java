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

package org.kuali.module.financial.document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.document.AmountTotaling;
import org.kuali.core.document.Copyable;
import org.kuali.core.document.Correctable;
import org.kuali.core.exceptions.InfrastructureException;
import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.KualiInteger;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.web.format.CurrencyFormatter;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.AccountingLineParser;
import org.kuali.kfs.bo.GeneralLedgerPendingEntry;
import org.kuali.kfs.bo.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.bo.Options;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.document.AccountingDocumentBase;
import org.kuali.kfs.service.AccountingDocumentRuleHelperService;
import org.kuali.kfs.service.DebitDeterminerService;
import org.kuali.kfs.service.GeneralLedgerPendingEntryService;
import org.kuali.kfs.service.OptionsService;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.financial.bo.BudgetAdjustmentAccountingLine;
import org.kuali.module.financial.bo.BudgetAdjustmentAccountingLineParser;
import org.kuali.module.financial.bo.BudgetAdjustmentSourceAccountingLine;
import org.kuali.module.financial.bo.BudgetAdjustmentTargetAccountingLine;
import org.kuali.module.financial.bo.FiscalYearFunctionControl;
import org.kuali.module.financial.rules.BudgetAdjustmentDocumentRule;
import org.kuali.module.financial.rules.BudgetAdjustmentDocumentRuleConstants;
import org.kuali.module.financial.rules.TransferOfFundsDocumentRuleConstants;
import org.kuali.module.financial.service.FiscalYearFunctionControlService;
import org.kuali.module.financial.service.UniversityDateService;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This is the business object that represents the BudgetAdjustment document in Kuali.
 */
public class BudgetAdjustmentDocument extends AccountingDocumentBase implements Copyable, Correctable, AmountTotaling {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetAdjustmentDocument.class);

    private Integer nextPositionSourceLineNumber;
    private Integer nextPositionTargetLineNumber;

    /**
     * Default constructor.
     */
    public BudgetAdjustmentDocument() {
        super();
    }


    /*******************************************************************************************************************************
     * BA Documents should only do SF checking on PLEs with a Balance Type of 'CB' - not 'BB' or 'MB'.
     * 
     * @Override
     * @see org.kuali.kfs.document.AccountingDocumentBase#getPendingLedgerEntriesForSufficientFundsChecking()
     */
    public List<GeneralLedgerPendingEntry> getPendingLedgerEntriesForSufficientFundsChecking() {
        List<GeneralLedgerPendingEntry> pendingLedgerEntries = new ArrayList();

        GeneralLedgerPendingEntrySequenceHelper glpeSequenceHelper = new GeneralLedgerPendingEntrySequenceHelper();
        BudgetAdjustmentDocumentRule budgetAdjustmentDocumentRule = new BudgetAdjustmentDocumentRule();

        BudgetAdjustmentDocument copiedBa = (BudgetAdjustmentDocument) ObjectUtils.deepCopy(this);
        copiedBa.getGeneralLedgerPendingEntries().clear();
        for (BudgetAdjustmentAccountingLine fromLine : (List<BudgetAdjustmentAccountingLine>) copiedBa.getSourceAccountingLines()) {
            copiedBa.generateGeneralLedgerPendingEntries(fromLine, glpeSequenceHelper);
        }

        for (GeneralLedgerPendingEntry ple : copiedBa.getGeneralLedgerPendingEntries()) {
            if (!KFSConstants.BALANCE_TYPE_BASE_BUDGET.equals(ple.getFinancialBalanceTypeCode()) && !KFSConstants.BALANCE_TYPE_MONTHLY_BUDGET.equals(ple.getFinancialBalanceTypeCode())) {
                pendingLedgerEntries.add(ple);
            }
        }
        return pendingLedgerEntries;
    }


    /**
     * @see org.kuali.kfs.document.AccountingDocumentBase#getSourceAccountingLineClass()
     */
    @Override
    public Class getSourceAccountingLineClass() {
        return BudgetAdjustmentSourceAccountingLine.class;
    }


    /**
     * @see org.kuali.kfs.document.AccountingDocumentBase#getTargetAccountingLineClass()
     */
    @Override
    public Class getTargetAccountingLineClass() {
        return BudgetAdjustmentTargetAccountingLine.class;
    }


    /**
     * generic, shared logic used to iniate a ba document
     */
    public void initiateDocument() {
        // setting default posting year. Trying to set currentYear first if it's allowed, if it isn't,
        // just set first allowed year. Note: allowedYears will never be empty because then
        // BudgetAdjustmentDocumentAuthorizer.canInitiate would have failed.
        List allowedYears = SpringContext.getBean(FiscalYearFunctionControlService.class).getBudgetAdjustmentAllowedYears();
        Integer currentYearParam = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();

        FiscalYearFunctionControl fiscalYearFunctionControl = new FiscalYearFunctionControl();
        fiscalYearFunctionControl.setUniversityFiscalYear(currentYearParam);

        // use 'this.postingYear =' because setPostingYear has logic we want to circumvent on initiateDocument
        if (allowedYears.contains(fiscalYearFunctionControl)) {
            this.postingYear = currentYearParam;
        }
        else {
            this.postingYear = ((FiscalYearFunctionControl) allowedYears.get(0)).getUniversityFiscalYear();
        }
    }

    /**
     * @return Integer
     */
    public Integer getNextPositionSourceLineNumber() {
        return nextPositionSourceLineNumber;
    }

    /**
     * @param nextPositionSourceLineNumber
     */
    public void setNextPositionSourceLineNumber(Integer nextPositionSourceLineNumber) {
        this.nextPositionSourceLineNumber = nextPositionSourceLineNumber;
    }

    /**
     * @return Integer
     */
    public Integer getNextPositionTargetLineNumber() {
        return nextPositionTargetLineNumber;
    }

    /**
     * @param nextPositionTargetLineNumber
     */
    public void setNextPositionTargetLineNumber(Integer nextPositionTargetLineNumber) {
        this.nextPositionTargetLineNumber = nextPositionTargetLineNumber;
    }

    /**
     * Returns the total current budget amount from the source lines.
     * 
     * @return KualiDecimal
     */
    public KualiDecimal getSourceCurrentBudgetTotal() {
        KualiDecimal currentBudgetTotal = KualiDecimal.ZERO;

        for (Iterator iter = sourceAccountingLines.iterator(); iter.hasNext();) {
            BudgetAdjustmentAccountingLine line = (BudgetAdjustmentAccountingLine) iter.next();
            currentBudgetTotal = currentBudgetTotal.add(line.getCurrentBudgetAdjustmentAmount());
        }

        return currentBudgetTotal;
    }

    /**
     * This method retrieves the total current budget amount formatted as currency.
     * 
     * @return String
     */
    public String getCurrencyFormattedSourceCurrentBudgetTotal() {
        return (String) new CurrencyFormatter().format(getSourceCurrentBudgetTotal());
    }

    /**
     * Returns the total current budget income amount from the source lines.
     * 
     * @return KualiDecimal
     */
    public KualiDecimal getSourceCurrentBudgetIncomeTotal() {
        KualiDecimal total = KualiDecimal.ZERO;

        for (Iterator iter = sourceAccountingLines.iterator(); iter.hasNext();) {
            BudgetAdjustmentAccountingLine line = (BudgetAdjustmentAccountingLine) iter.next();
            AccountingDocumentRuleHelperService accountingDocumentRuleUtil = SpringContext.getBean(AccountingDocumentRuleHelperService.class);
            if (accountingDocumentRuleUtil.isIncome(line)) {
                total = total.add(line.getCurrentBudgetAdjustmentAmount());
            }
        }

        return total;
    }

    /**
     * Returns the total current budget expense amount from the source lines.
     * 
     * @return KualiDecimal
     */
    public KualiDecimal getSourceCurrentBudgetExpenseTotal() {
        KualiDecimal total = KualiDecimal.ZERO;

        for (Iterator iter = sourceAccountingLines.iterator(); iter.hasNext();) {
            BudgetAdjustmentAccountingLine line = (BudgetAdjustmentAccountingLine) iter.next();
            AccountingDocumentRuleHelperService accountingDocumentRuleUtil = SpringContext.getBean(AccountingDocumentRuleHelperService.class);
            if (accountingDocumentRuleUtil.isExpense(line)) {
                total = total.add(line.getCurrentBudgetAdjustmentAmount());
            }
        }

        return total;
    }

    /**
     * Returns the total current budget amount from the target lines.
     * 
     * @return KualiDecimal
     */
    public KualiDecimal getTargetCurrentBudgetTotal() {
        KualiDecimal currentBudgetTotal = KualiDecimal.ZERO;

        for (Iterator iter = targetAccountingLines.iterator(); iter.hasNext();) {
            BudgetAdjustmentAccountingLine line = (BudgetAdjustmentAccountingLine) iter.next();
            currentBudgetTotal = currentBudgetTotal.add(line.getCurrentBudgetAdjustmentAmount());
        }

        return currentBudgetTotal;
    }

    /**
     * This method retrieves the total current budget amount formatted as currency.
     * 
     * @return String
     */
    public String getCurrencyFormattedTargetCurrentBudgetTotal() {
        return (String) new CurrencyFormatter().format(getTargetCurrentBudgetTotal());
    }

    /**
     * Returns the total current budget income amount from the target lines.
     * 
     * @return KualiDecimal
     */
    public KualiDecimal getTargetCurrentBudgetIncomeTotal() {
        KualiDecimal total = KualiDecimal.ZERO;

        AccountingDocumentRuleHelperService accountingDocumentRuleUtil = SpringContext.getBean(AccountingDocumentRuleHelperService.class);
        for (Iterator iter = targetAccountingLines.iterator(); iter.hasNext();) {
            BudgetAdjustmentAccountingLine line = (BudgetAdjustmentAccountingLine) iter.next();
            if (accountingDocumentRuleUtil.isIncome(line)) {
                total = total.add(line.getCurrentBudgetAdjustmentAmount());
            }
        }

        return total;
    }

    /**
     * Returns the total current budget expense amount from the target lines.
     * 
     * @return KualiDecimal
     */
    public KualiDecimal getTargetCurrentBudgetExpenseTotal() {
        KualiDecimal total = KualiDecimal.ZERO;

        AccountingDocumentRuleHelperService accountingDocumentRuleUtil = SpringContext.getBean(AccountingDocumentRuleHelperService.class);
        for (Iterator iter = targetAccountingLines.iterator(); iter.hasNext();) {
            BudgetAdjustmentAccountingLine line = (BudgetAdjustmentAccountingLine) iter.next();
            if (accountingDocumentRuleUtil.isExpense(line)) {
                total = total.add(line.getCurrentBudgetAdjustmentAmount());
            }
        }

        return total;
    }

    /**
     * Returns the total base budget amount from the source lines.
     * 
     * @return KualiDecimal
     */
    public KualiInteger getSourceBaseBudgetTotal() {
        KualiInteger baseBudgetTotal = KualiInteger.ZERO;

        for (Iterator iter = sourceAccountingLines.iterator(); iter.hasNext();) {
            BudgetAdjustmentAccountingLine line = (BudgetAdjustmentAccountingLine) iter.next();
            baseBudgetTotal = baseBudgetTotal.add(line.getBaseBudgetAdjustmentAmount());
        }

        return baseBudgetTotal;
    }


    /**
     * This method retrieves the total base budget amount formatted as currency.
     * 
     * @return String
     */
    public String getCurrencyFormattedSourceBaseBudgetTotal() {
        return (String) new CurrencyFormatter().format(getSourceBaseBudgetTotal());
    }

    /**
     * Returns the total base budget income amount from the source lines.
     * 
     * @return KualiDecimal
     */
    public KualiInteger getSourceBaseBudgetIncomeTotal() {
        KualiInteger total = KualiInteger.ZERO;

        AccountingDocumentRuleHelperService accountingDocumentRuleUtil = SpringContext.getBean(AccountingDocumentRuleHelperService.class);
        for (Iterator iter = sourceAccountingLines.iterator(); iter.hasNext();) {
            BudgetAdjustmentAccountingLine line = (BudgetAdjustmentAccountingLine) iter.next();
            if (accountingDocumentRuleUtil.isIncome(line)) {
                total = total.add(line.getBaseBudgetAdjustmentAmount());
            }
        }

        return total;
    }

    /**
     * Returns the total base budget expense amount from the source lines.
     * 
     * @return KualiDecimal
     */
    public KualiInteger getSourceBaseBudgetExpenseTotal() {
        KualiInteger total = KualiInteger.ZERO;

        AccountingDocumentRuleHelperService accountingDocumentRuleUtil = SpringContext.getBean(AccountingDocumentRuleHelperService.class);
        for (Iterator iter = sourceAccountingLines.iterator(); iter.hasNext();) {
            BudgetAdjustmentAccountingLine line = (BudgetAdjustmentAccountingLine) iter.next();
            if (accountingDocumentRuleUtil.isExpense(line)) {
                total = total.add(line.getBaseBudgetAdjustmentAmount());
            }
        }

        return total;
    }

    /**
     * Returns the total base budget amount from the target lines.
     * 
     * @return KualiDecimal
     */
    public KualiInteger getTargetBaseBudgetTotal() {
        KualiInteger baseBudgetTotal = KualiInteger.ZERO;

        for (Iterator iter = targetAccountingLines.iterator(); iter.hasNext();) {
            BudgetAdjustmentAccountingLine line = (BudgetAdjustmentAccountingLine) iter.next();
            baseBudgetTotal = baseBudgetTotal.add(line.getBaseBudgetAdjustmentAmount());
        }

        return baseBudgetTotal;
    }

    /**
     * This method retrieves the total base budget amount formatted as currency.
     * 
     * @return String
     */
    public String getCurrencyFormattedTargetBaseBudgetTotal() {
        return (String) new CurrencyFormatter().format(getTargetBaseBudgetTotal());
    }

    /**
     * Returns the total base budget income amount from the target lines.
     * 
     * @return KualiDecimal
     */
    public KualiInteger getTargetBaseBudgetIncomeTotal() {
        KualiInteger total = KualiInteger.ZERO;

        AccountingDocumentRuleHelperService accountingDocumentRuleUtil = SpringContext.getBean(AccountingDocumentRuleHelperService.class);
        for (Iterator iter = targetAccountingLines.iterator(); iter.hasNext();) {
            BudgetAdjustmentAccountingLine line = (BudgetAdjustmentAccountingLine) iter.next();
            if (accountingDocumentRuleUtil.isIncome(line)) {
                total = total.add(line.getBaseBudgetAdjustmentAmount());
            }
        }

        return total;
    }

    /**
     * Returns the total base budget expense amount from the target lines.
     * 
     * @return KualiDecimal
     */
    public KualiInteger getTargetBaseBudgetExpenseTotal() {
        KualiInteger total = KualiInteger.ZERO;

        AccountingDocumentRuleHelperService accountingDocumentRuleUtil = SpringContext.getBean(AccountingDocumentRuleHelperService.class);
        for (Iterator iter = targetAccountingLines.iterator(); iter.hasNext();) {
            BudgetAdjustmentAccountingLine line = (BudgetAdjustmentAccountingLine) iter.next();
            if (accountingDocumentRuleUtil.isExpense(line)) {
                total = total.add(line.getBaseBudgetAdjustmentAmount());
            }
        }

        return total;
    }

    /**
     * Same as default implementation but uses getTargetCurrentBudgetTotal and getSourceCurrentBudgetTotal instead.
     * 
     * @see org.kuali.kfs.document.AccountingDocumentBase#getTotalDollarAmount()
     * @return KualiDecimal
     */
    @Override
    public KualiDecimal getTotalDollarAmount() {
        return getTargetCurrentBudgetTotal().equals(KualiDecimal.ZERO) ? getSourceCurrentBudgetTotal() : getTargetCurrentBudgetTotal();
    }

    /**
     * Negate accounting line budget amounts.
     * 
     * @see org.kuali.kfs.document.AccountingDocumentBase#toErrorCorrection()
     */
    @Override
    public void toErrorCorrection() throws WorkflowException {
        super.toErrorCorrection();

        if (this.getSourceAccountingLines() != null) {
            for (Iterator iter = this.getSourceAccountingLines().iterator(); iter.hasNext();) {
                BudgetAdjustmentAccountingLine sourceLine = (BudgetAdjustmentAccountingLine) iter.next();
                sourceLine.setBaseBudgetAdjustmentAmount(sourceLine.getBaseBudgetAdjustmentAmount().negated());
                sourceLine.setCurrentBudgetAdjustmentAmount(sourceLine.getCurrentBudgetAdjustmentAmount().negated());
                sourceLine.setFinancialDocumentMonth1LineAmount(sourceLine.getFinancialDocumentMonth1LineAmount().negated());
                sourceLine.setFinancialDocumentMonth2LineAmount(sourceLine.getFinancialDocumentMonth2LineAmount().negated());
                sourceLine.setFinancialDocumentMonth3LineAmount(sourceLine.getFinancialDocumentMonth3LineAmount().negated());
                sourceLine.setFinancialDocumentMonth4LineAmount(sourceLine.getFinancialDocumentMonth4LineAmount().negated());
                sourceLine.setFinancialDocumentMonth5LineAmount(sourceLine.getFinancialDocumentMonth5LineAmount().negated());
                sourceLine.setFinancialDocumentMonth6LineAmount(sourceLine.getFinancialDocumentMonth6LineAmount().negated());
                sourceLine.setFinancialDocumentMonth7LineAmount(sourceLine.getFinancialDocumentMonth7LineAmount().negated());
                sourceLine.setFinancialDocumentMonth8LineAmount(sourceLine.getFinancialDocumentMonth8LineAmount().negated());
                sourceLine.setFinancialDocumentMonth9LineAmount(sourceLine.getFinancialDocumentMonth9LineAmount().negated());
                sourceLine.setFinancialDocumentMonth10LineAmount(sourceLine.getFinancialDocumentMonth10LineAmount().negated());
                sourceLine.setFinancialDocumentMonth11LineAmount(sourceLine.getFinancialDocumentMonth11LineAmount().negated());
                sourceLine.setFinancialDocumentMonth12LineAmount(sourceLine.getFinancialDocumentMonth12LineAmount().negated());
            }
        }

        if (this.getTargetAccountingLines() != null) {
            for (Iterator iter = this.getTargetAccountingLines().iterator(); iter.hasNext();) {
                BudgetAdjustmentAccountingLine targetLine = (BudgetAdjustmentAccountingLine) iter.next();
                targetLine.setBaseBudgetAdjustmentAmount(targetLine.getBaseBudgetAdjustmentAmount().negated());
                targetLine.setCurrentBudgetAdjustmentAmount(targetLine.getCurrentBudgetAdjustmentAmount().negated());
                targetLine.setFinancialDocumentMonth1LineAmount(targetLine.getFinancialDocumentMonth1LineAmount().negated());
                targetLine.setFinancialDocumentMonth2LineAmount(targetLine.getFinancialDocumentMonth2LineAmount().negated());
                targetLine.setFinancialDocumentMonth3LineAmount(targetLine.getFinancialDocumentMonth3LineAmount().negated());
                targetLine.setFinancialDocumentMonth4LineAmount(targetLine.getFinancialDocumentMonth4LineAmount().negated());
                targetLine.setFinancialDocumentMonth5LineAmount(targetLine.getFinancialDocumentMonth5LineAmount().negated());
                targetLine.setFinancialDocumentMonth6LineAmount(targetLine.getFinancialDocumentMonth6LineAmount().negated());
                targetLine.setFinancialDocumentMonth7LineAmount(targetLine.getFinancialDocumentMonth7LineAmount().negated());
                targetLine.setFinancialDocumentMonth8LineAmount(targetLine.getFinancialDocumentMonth8LineAmount().negated());
                targetLine.setFinancialDocumentMonth9LineAmount(targetLine.getFinancialDocumentMonth9LineAmount().negated());
                targetLine.setFinancialDocumentMonth10LineAmount(targetLine.getFinancialDocumentMonth10LineAmount().negated());
                targetLine.setFinancialDocumentMonth11LineAmount(targetLine.getFinancialDocumentMonth11LineAmount().negated());
                targetLine.setFinancialDocumentMonth12LineAmount(targetLine.getFinancialDocumentMonth12LineAmount().negated());
            }
        }
    }

    /**
     * @see org.kuali.core.document.DocumentBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        return m;
    }

    /**
     * @see org.kuali.kfs.document.AccountingDocumentBase#getAccountingLineParser()
     */
    @Override
    public AccountingLineParser getAccountingLineParser() {
        return new BudgetAdjustmentAccountingLineParser();
    }

    /**
     * The base checks that the posting year is the current year, not a requirement for the ba document.
     * 
     * @see org.kuali.core.document.TransactionalDocumentBase#getAllowsCopy()
     */
    @Override
    public boolean getAllowsCopy() {
        return true;
    }

    /**
     * The base checks that the posting year is the current year, not a requirement for the ba document.
     * 
     * @see org.kuali.core.document.TransactionalDocumentBase#getAllowsErrorCorrection()
     */
    @Override
    public boolean getAllowsErrorCorrection() {
        return true;
    }

    /**
     * @see org.kuali.kfs.document.AccountingDocumentBase#getSourceAccountingLinesSectionTitle()
     */
    @Override
    public String getSourceAccountingLinesSectionTitle() {
        return KFSConstants.BudgetAdjustmentDocumentConstants.SOURCE_BA;
    }

    /**
     * @see org.kuali.kfs.document.AccountingDocumentBase#getTargetAccountingLinesSectionTitle()
     */
    @Override
    public String getTargetAccountingLinesSectionTitle() {
        return KFSConstants.BudgetAdjustmentDocumentConstants.TARGET_BA;
    }

    /**
     * @see org.kuali.core.document.DocumentBase#populateDocumentForRouting()
     */
    @Override
    public void populateDocumentForRouting() {
        super.populateDocumentForRouting();

        // set amount fields of line for routing to current amount field
        for (Iterator iter = this.getSourceAccountingLines().iterator(); iter.hasNext();) {
            BudgetAdjustmentAccountingLine line = (BudgetAdjustmentAccountingLine) iter.next();
            line.setAmount(line.getCurrentBudgetAdjustmentAmount());
        }

        for (Iterator iter = this.getTargetAccountingLines().iterator(); iter.hasNext();) {
            BudgetAdjustmentAccountingLine line = (BudgetAdjustmentAccountingLine) iter.next();
            line.setAmount(line.getCurrentBudgetAdjustmentAmount());
        }
    }

    /**
     * Returns true if accounting line is debit
     * 
     * @param financialDocument submitted financial document
     * @param accountingLine accouting line being evaulated as a debit or not
     * @see org.kuali.core.rule.AccountingLineRule#isDebit(org.kuali.core.document.FinancialDocument,
     *      org.kuali.core.bo.AccountingLine)
     */
    @Override
    public boolean isDebit(GeneralLedgerPendingEntrySourceDetail postable) {
        try {
            DebitDeterminerService isDebitUtils = SpringContext.getBean(DebitDeterminerService.class);
            return isDebitUtils.isDebitConsideringType(this, (AccountingLine)postable);
        }
        catch (IllegalStateException e) {
            // for all accounting lines except the transfer lines, the line amount will be 0 and this exception will be thrown
            return false;
        }
    }

    /**
     * The budget adjustment document creates GL pending entries much differently that common tp-edocs. The glpes are created for
     * BB, CB, and MB balance types. Up to 14 entries per line can be created. Along with this, the BA will create TOF entries if
     * needed to move funding.
     * 
     * @param financialDocument submitted accounting document
     * @param accountingLine validated accounting line
     * @param sequenceHelper helper class for keeping track of sequence number
     * @return true if GLPE entries are successfully created.
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#processGenerateGeneralLedgerPendingEntries(org.kuali.core.document.FinancialDocument,
     *      org.kuali.core.bo.AccountingLine, org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper)
     */
    @Override
    public boolean generateGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        AccountingLine accountingLine = (AccountingLine)glpeSourceDetail;

        // determine if we are on increase or decrease side
        KualiDecimal amountSign = null;
        if (accountingLine instanceof SourceAccountingLine) {
            amountSign = new KualiDecimal(-1);
        }
        else {
            amountSign = new KualiDecimal(1);
        }

        BudgetAdjustmentAccountingLine budgetAccountingLine = (BudgetAdjustmentAccountingLine) glpeSourceDetail;
        Integer currentFiscalYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();
        /* Create Base Budget GLPE if base amount != 0 */
        if (budgetAccountingLine.getBaseBudgetAdjustmentAmount().isNonZero()) {
            GeneralLedgerPendingEntry explicitEntry = new GeneralLedgerPendingEntry();
            getGeneralLedgerPendingEntryService().populateExplicitGeneralLedgerPendingEntry(this, accountingLine, sequenceHelper, explicitEntry);

            /* D/C code is empty for BA, set correct balance type, correct amount */
            explicitEntry.setTransactionDebitCreditCode("");
            explicitEntry.setFinancialBalanceTypeCode(KFSConstants.BALANCE_TYPE_BASE_BUDGET);
            explicitEntry.setTransactionLedgerEntryAmount(budgetAccountingLine.getBaseBudgetAdjustmentAmount().multiply(amountSign).kualiDecimalValue());
            // set fiscal period, if next fiscal year set to 01, else leave to current period
            if (currentFiscalYear.equals(getPostingYear() - 1)) {
                explicitEntry.setUniversityFiscalPeriodCode(BudgetAdjustmentDocumentRuleConstants.MONTH_1_PERIOD_CODE);
            }

            customizeExplicitGeneralLedgerPendingEntry(accountingLine, explicitEntry);

            addPendingEntry(explicitEntry);

            // increment the sequence counter
            sequenceHelper.increment();
        }

        /* Create Current Budget GLPE if current amount != 0 */
        if (budgetAccountingLine.getCurrentBudgetAdjustmentAmount().isNonZero()) {
            GeneralLedgerPendingEntry explicitEntry = new GeneralLedgerPendingEntry();
            getGeneralLedgerPendingEntryService().populateExplicitGeneralLedgerPendingEntry(this, accountingLine, sequenceHelper, explicitEntry);

            /* D/C code is empty for BA, set correct balance type, correct amount */
            explicitEntry.setTransactionDebitCreditCode("");
            explicitEntry.setFinancialBalanceTypeCode(KFSConstants.BALANCE_TYPE_CURRENT_BUDGET);
            explicitEntry.setTransactionLedgerEntryAmount(budgetAccountingLine.getCurrentBudgetAdjustmentAmount().multiply(amountSign));
            // set fiscal period, if next fiscal year set to 01, else leave to current period
            if (currentFiscalYear.equals(getPostingYear() - 1)) {
                explicitEntry.setUniversityFiscalPeriodCode("01");
            }

            customizeExplicitGeneralLedgerPendingEntry(accountingLine, explicitEntry);
            
            addPendingEntry(explicitEntry);

            // create montly lines (MB)
            if (budgetAccountingLine.getFinancialDocumentMonth1LineAmount().isNonZero()) {
                sequenceHelper.increment();
                createMonthlyBudgetGLPE(accountingLine, sequenceHelper, BudgetAdjustmentDocumentRuleConstants.MONTH_1_PERIOD_CODE, budgetAccountingLine.getFinancialDocumentMonth1LineAmount().multiply(amountSign));
            }
            if (budgetAccountingLine.getFinancialDocumentMonth2LineAmount().isNonZero()) {
                sequenceHelper.increment();
                createMonthlyBudgetGLPE(accountingLine, sequenceHelper, BudgetAdjustmentDocumentRuleConstants.MONTH_2_PERIOD_CODE, budgetAccountingLine.getFinancialDocumentMonth2LineAmount().multiply(amountSign));
            }
            if (budgetAccountingLine.getFinancialDocumentMonth3LineAmount().isNonZero()) {
                sequenceHelper.increment();
                createMonthlyBudgetGLPE(accountingLine, sequenceHelper, BudgetAdjustmentDocumentRuleConstants.MONTH_3_PERIOD_CODE, budgetAccountingLine.getFinancialDocumentMonth3LineAmount().multiply(amountSign));
            }
            if (budgetAccountingLine.getFinancialDocumentMonth4LineAmount().isNonZero()) {
                sequenceHelper.increment();
                createMonthlyBudgetGLPE(accountingLine, sequenceHelper, BudgetAdjustmentDocumentRuleConstants.MONTH_4_PERIOD_CODE, budgetAccountingLine.getFinancialDocumentMonth4LineAmount().multiply(amountSign));
            }
            if (budgetAccountingLine.getFinancialDocumentMonth5LineAmount().isNonZero()) {
                sequenceHelper.increment();
                createMonthlyBudgetGLPE(accountingLine, sequenceHelper, BudgetAdjustmentDocumentRuleConstants.MONTH_5_PERIOD_CODE, budgetAccountingLine.getFinancialDocumentMonth5LineAmount().multiply(amountSign));
            }
            if (budgetAccountingLine.getFinancialDocumentMonth6LineAmount().isNonZero()) {
                sequenceHelper.increment();
                createMonthlyBudgetGLPE(accountingLine, sequenceHelper, BudgetAdjustmentDocumentRuleConstants.MONTH_6_PERIOD_CODE, budgetAccountingLine.getFinancialDocumentMonth6LineAmount().multiply(amountSign));
            }
            if (budgetAccountingLine.getFinancialDocumentMonth7LineAmount().isNonZero()) {
                sequenceHelper.increment();
                createMonthlyBudgetGLPE(accountingLine, sequenceHelper, BudgetAdjustmentDocumentRuleConstants.MONTH_7_PERIOD_CODE, budgetAccountingLine.getFinancialDocumentMonth7LineAmount().multiply(amountSign));
            }
            if (budgetAccountingLine.getFinancialDocumentMonth8LineAmount().isNonZero()) {
                sequenceHelper.increment();
                createMonthlyBudgetGLPE(accountingLine, sequenceHelper, BudgetAdjustmentDocumentRuleConstants.MONTH_8_PERIOD_CODE, budgetAccountingLine.getFinancialDocumentMonth8LineAmount().multiply(amountSign));
            }
            if (budgetAccountingLine.getFinancialDocumentMonth9LineAmount().isNonZero()) {
                sequenceHelper.increment();
                createMonthlyBudgetGLPE(accountingLine, sequenceHelper, BudgetAdjustmentDocumentRuleConstants.MONTH_9_PERIOD_CODE, budgetAccountingLine.getFinancialDocumentMonth9LineAmount().multiply(amountSign));
            }
            if (budgetAccountingLine.getFinancialDocumentMonth10LineAmount().isNonZero()) {
                sequenceHelper.increment();
                createMonthlyBudgetGLPE(accountingLine, sequenceHelper, BudgetAdjustmentDocumentRuleConstants.MONTH_10_PERIOD_CODE, budgetAccountingLine.getFinancialDocumentMonth10LineAmount().multiply(amountSign));
            }
            if (budgetAccountingLine.getFinancialDocumentMonth11LineAmount().isNonZero()) {
                sequenceHelper.increment();
                createMonthlyBudgetGLPE(accountingLine, sequenceHelper, BudgetAdjustmentDocumentRuleConstants.MONTH_11_PERIOD_CODE, budgetAccountingLine.getFinancialDocumentMonth11LineAmount().multiply(amountSign));
            }
            if (budgetAccountingLine.getFinancialDocumentMonth12LineAmount().isNonZero()) {
                sequenceHelper.increment();
                createMonthlyBudgetGLPE(accountingLine, sequenceHelper, BudgetAdjustmentDocumentRuleConstants.MONTH_12_PERIOD_CODE, budgetAccountingLine.getFinancialDocumentMonth12LineAmount().multiply(amountSign));
            }
        }
        return true;
    }

    /**
     * Helper method for creating monthly budget pending entry lines.
     * 
     * @param financialDocument submitted accounting document
     * @param accountingLine validated accounting line
     * @param sequenceHelper helper class for keeping track of sequence number
     * @param fiscalPeriod fiscal year period code
     * @param monthAmount ledger entry amount for the month
     */
    protected void createMonthlyBudgetGLPE(AccountingLine accountingLine, GeneralLedgerPendingEntrySequenceHelper sequenceHelper, String fiscalPeriod, KualiDecimal monthAmount) {
        GeneralLedgerPendingEntry explicitEntry = new GeneralLedgerPendingEntry();
        getGeneralLedgerPendingEntryService().populateExplicitGeneralLedgerPendingEntry(this, accountingLine, sequenceHelper, explicitEntry);

        /* D/C code is empty for BA, set correct balance type, correct amount */
        explicitEntry.setTransactionDebitCreditCode("");
        explicitEntry.setFinancialBalanceTypeCode(KFSConstants.BALANCE_TYPE_MONTHLY_BUDGET);
        explicitEntry.setTransactionLedgerEntryAmount(monthAmount);
        explicitEntry.setUniversityFiscalPeriodCode(fiscalPeriod);

        customizeExplicitGeneralLedgerPendingEntry(accountingLine, explicitEntry);

        addPendingEntry(explicitEntry);
    }
    
    /**
     * Returns an implementation of the GeneralLedgerPendingEntryService
     * @return an implementation of the GeneralLedgerPendingEntryService
     */
    public GeneralLedgerPendingEntryService getGeneralLedgerPendingEntryService() {
        return SpringContext.getBean(GeneralLedgerPendingEntryService.class);
    }

    /**
     * Generates any necessary tof entries to transfer funds needed to make the budget adjustments. Based on income chart and
     * accounts. If there is a difference in funds between an income chart and account, a tof entry needs to be created, along with
     * a budget adjustment entry. Object code used is retrieved by a parameter.
     * 
     * @param sequenceHelper helper class for keeping track of sequence number
     * @return true general ledger pending entries are generated without any problems
     * @see org.kuali.core.rule.GenerateGeneralLedgerDocumentPendingEntriesRule#processGenerateDocumentGeneralLedgerPendingEntries(org.kuali.core.document.FinancialDocument,
     *      org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper)
     */
    @Override
    public boolean generateDocumentGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        boolean success = true;

        // check on-off tof flag
        boolean generateTransfer = SpringContext.getBean(ParameterService.class).getIndicatorParameter(BudgetAdjustmentDocument.class, BudgetAdjustmentDocumentRuleConstants.GENERATE_TOF_GLPE_ENTRIES_PARM_NM);
        String transferObjectCode = SpringContext.getBean(ParameterService.class).getParameterValue(BudgetAdjustmentDocument.class, BudgetAdjustmentDocumentRuleConstants.TRANSFER_OBJECT_CODE_PARM_NM);
        Integer currentFiscalYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();

        if (generateTransfer) {
            // map of income chart/accounts with balance as value
            Map incomeStreamMap = buildIncomeStreamBalanceMap();
            GeneralLedgerPendingEntryService glpeService = SpringContext.getBean(GeneralLedgerPendingEntryService.class);
            for (Iterator iter = incomeStreamMap.keySet().iterator(); iter.hasNext();) {
                String chartAccount = (String) iter.next();
                KualiDecimal streamAmount = (KualiDecimal) incomeStreamMap.get(chartAccount);
                if (streamAmount.isNonZero()) {
                    // build dummy accounting line for gl population
                    AccountingLine accountingLine = null;
                    try {
                        accountingLine = (SourceAccountingLine) getSourceAccountingLineClass().newInstance();
                    }
                    catch (IllegalAccessException e) {
                        throw new InfrastructureException("unable to access sourceAccountingLineClass", e);
                    }
                    catch (InstantiationException e) {
                        throw new InfrastructureException("unable to instantiate sourceAccountingLineClass", e);
                    }

                    // set income chart and account in line
                    String[] incomeString = StringUtils.split(chartAccount, BudgetAdjustmentDocumentRuleConstants.INCOME_STREAM_CHART_ACCOUNT_DELIMITER);
                    accountingLine.setChartOfAccountsCode(incomeString[0]);
                    accountingLine.setAccountNumber(incomeString[1]);
                    accountingLine.setFinancialObjectCode(transferObjectCode);

                    // ////////////////// first create current budget entry/////////////////////////////////////////
                    GeneralLedgerPendingEntry explicitEntry = new GeneralLedgerPendingEntry();
                    glpeService.populateExplicitGeneralLedgerPendingEntry(this, accountingLine, sequenceHelper, explicitEntry);

                    /* override and set object type to income */
                    Options options = SpringContext.getBean(OptionsService.class).getCurrentYearOptions();
                    explicitEntry.setFinancialObjectTypeCode(options.getFinObjectTypeIncomecashCode());

                    /* D/C code is empty for BA, set correct balance type, correct amount */
                    explicitEntry.setTransactionDebitCreditCode("");
                    explicitEntry.setFinancialBalanceTypeCode(KFSConstants.BALANCE_TYPE_CURRENT_BUDGET);
                    explicitEntry.setTransactionLedgerEntryAmount(streamAmount);

                    // set fiscal period, if next fiscal year set to 01, else leave to current period
                    if (currentFiscalYear.equals(getPostingYear() - 1)) {
                        explicitEntry.setUniversityFiscalPeriodCode(BudgetAdjustmentDocumentRuleConstants.MONTH_1_PERIOD_CODE);
                    }

                    customizeExplicitGeneralLedgerPendingEntry(accountingLine, explicitEntry);

                    // add the new explicit entry to the document now
                    addPendingEntry(explicitEntry);

                    // increment the sequence counter
                    sequenceHelper.increment();


                    // ////////////////// now create actual TOF entry //////////////////////////////////////////////
                    /* set amount in line so Debit/Credit code can be set correctly */
                    accountingLine.setAmount(streamAmount);
                    explicitEntry = new GeneralLedgerPendingEntry();
                    glpeService.populateExplicitGeneralLedgerPendingEntry(this, accountingLine, sequenceHelper, explicitEntry);

                    /* override and set object type to transfer */
                    explicitEntry.setFinancialObjectTypeCode(options.getFinancialObjectTypeTransferIncomeCd());

                    /* set document type to tof */
                    explicitEntry.setFinancialDocumentTypeCode(getTransferDocumentType());

                    // set fiscal period, if next fiscal year set to 01, else leave to current period
                    if (currentFiscalYear.equals(getPostingYear() - 1)) {
                        explicitEntry.setUniversityFiscalPeriodCode(BudgetAdjustmentDocumentRuleConstants.MONTH_1_PERIOD_CODE);
                    }

                    // add the new explicit entry to the document now
                    addPendingEntry(explicitEntry);

                    customizeExplicitGeneralLedgerPendingEntry(accountingLine, explicitEntry);

                    // increment the sequence counter
                    sequenceHelper.increment();

                    // ////////////////// now create actual TOF offset //////////////////////////////////////////////
                    GeneralLedgerPendingEntry offsetEntry = new GeneralLedgerPendingEntry(explicitEntry);
                    success &= glpeService.populateOffsetGeneralLedgerPendingEntry(getPostingYear(), explicitEntry, sequenceHelper, offsetEntry);
                    customizeOffsetGeneralLedgerPendingEntry(accountingLine, explicitEntry, offsetEntry);
                    addPendingEntry(offsetEntry);

                    // increment the sequence counter
                    sequenceHelper.increment();
                }
            }
        }
        return success;
    }
    
    /**
     * Builds a map used for balancing current adjustment amounts. The map contains income chart and accounts contained on the
     * document as the keys, and transfer amounts as the values. The transfer amount is calculated from (curr_frm_inc -
     * curr_frm_exp) - (curr_to_inc - curr_to_exp)
     * 
     * @param baDocument budget ajdustment document
     * @return Map used to balance current amounts
     */
    public Map buildIncomeStreamBalanceMap() {
        Map incomeStreamBalance = new HashMap();

        List accountingLines = new ArrayList();
        accountingLines.addAll(getSourceAccountingLines());
        accountingLines.addAll(getTargetAccountingLines());
        for (Iterator iter = accountingLines.iterator(); iter.hasNext();) {
            BudgetAdjustmentAccountingLine budgetAccountingLine = (BudgetAdjustmentAccountingLine) iter.next();
            String incomeStreamKey = budgetAccountingLine.getAccount().getIncomeStreamFinancialCoaCode() + BudgetAdjustmentDocumentRuleConstants.INCOME_STREAM_CHART_ACCOUNT_DELIMITER + budgetAccountingLine.getAccount().getIncomeStreamAccountNumber();

            KualiDecimal incomeStreamAmount = new KualiDecimal(0);
            if (incomeStreamBalance.containsKey(incomeStreamKey)) {
                incomeStreamAmount = (KualiDecimal) incomeStreamBalance.get(incomeStreamKey);
            }

            // amounts need reversed for source expense lines and target income lines
            DebitDeterminerService isDebitUtils = SpringContext.getBean(DebitDeterminerService.class);
            if ((budgetAccountingLine instanceof BudgetAdjustmentSourceAccountingLine && isDebitUtils.isExpense((AccountingLine) budgetAccountingLine)) || (budgetAccountingLine instanceof BudgetAdjustmentTargetAccountingLine && isDebitUtils.isIncome((AccountingLine) budgetAccountingLine))) {
                incomeStreamAmount = incomeStreamAmount.subtract(budgetAccountingLine.getCurrentBudgetAdjustmentAmount());
            }
            else {
                incomeStreamAmount = incomeStreamAmount.add(budgetAccountingLine.getCurrentBudgetAdjustmentAmount());
            }

            // place record in balance map
            incomeStreamBalance.put(incomeStreamKey, incomeStreamAmount);
        }

        return incomeStreamBalance;
    }
    
    /**
     * Returns the document type code for the Transfer of Funds document
     * 
     * @return the document type name to be used for the income stream transfer glpe
     */
    protected String getTransferDocumentType() {
        return TransferOfFundsDocumentRuleConstants.TRANSFER_OF_FUNDS_DOC_TYPE_CODE;
    }
}
