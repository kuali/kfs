/*
 * Copyright 2006 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kuali.kfs.fp.document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.fp.businessobject.BudgetAdjustmentAccountingLine;
import org.kuali.kfs.fp.businessobject.BudgetAdjustmentSourceAccountingLine;
import org.kuali.kfs.fp.businessobject.BudgetAdjustmentTargetAccountingLine;
import org.kuali.kfs.fp.businessobject.FiscalYearFunctionControl;
import org.kuali.kfs.fp.document.validation.impl.BudgetAdjustmentDocumentRuleConstants;
import org.kuali.kfs.fp.document.validation.impl.TransferOfFundsDocumentRuleConstants;
import org.kuali.kfs.fp.service.FiscalYearFunctionControlService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.AccountResponsibility;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySourceDetail;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.AccountingDocumentBase;
import org.kuali.kfs.sys.document.AmountTotaling;
import org.kuali.kfs.sys.document.Correctable;
import org.kuali.kfs.sys.document.service.AccountingDocumentRuleHelperService;
import org.kuali.kfs.sys.document.service.DebitDeterminerService;
import org.kuali.kfs.sys.service.GeneralLedgerPendingEntryService;
import org.kuali.kfs.sys.service.OptionsService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.core.web.format.CurrencyFormatter;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.document.Copyable;
import org.kuali.rice.krad.exception.InfrastructureException;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This is the business object that represents the BudgetAdjustment document in Kuali.
 */
public class BudgetAdjustmentDocument extends AccountingDocumentBase implements Copyable, Correctable, AmountTotaling {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetAdjustmentDocument.class);
    
    protected static final String REQUIRES_FULL_APPROVAL_SPLIT_NODE_NAME = "RequiresFullApproval";

    protected Integer nextPositionSourceLineNumber;
    protected Integer nextPositionTargetLineNumber;

    /**
     * Default constructor.
     */
    public BudgetAdjustmentDocument() {
        super();
    }


    /*******************************************************************************************************************************
     * BA Documents should only do SF checking on PLEs with a Balance Type of 'CB' - not 'BB' or 'MB'.
     * 
     * 
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#getPendingLedgerEntriesForSufficientFundsChecking()
     */
    public List<GeneralLedgerPendingEntry> getPendingLedgerEntriesForSufficientFundsChecking() {
        List<GeneralLedgerPendingEntry> pendingLedgerEntries = new ArrayList();

        GeneralLedgerPendingEntrySequenceHelper glpeSequenceHelper = new GeneralLedgerPendingEntrySequenceHelper();

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
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#getTotalDollarAmount()
     * @return KualiDecimal
     */
    @Override
    public KualiDecimal getTotalDollarAmount() {
        return getTargetCurrentBudgetTotal().equals(KualiDecimal.ZERO) ? getSourceCurrentBudgetTotal().abs() : getTargetCurrentBudgetTotal().abs();
    }

    /**
     * Negate accounting line budget amounts.
     * 
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#toErrorCorrection()
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
     * The base checks that the posting year is the current year, not a requirement for the ba document.
     * 
     * @see org.kuali.rice.krad.document.TransactionalDocumentBase#getAllowsCopy()
     */
    @Override
    public boolean getAllowsCopy() {
        return true;
    }

    /**
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#getSourceAccountingLinesSectionTitle()
     */
    @Override
    public String getSourceAccountingLinesSectionTitle() {
        return KFSConstants.BudgetAdjustmentDocumentConstants.SOURCE_BA;
    }

    /**
     * @see org.kuali.kfs.sys.document.AccountingDocumentBase#getTargetAccountingLinesSectionTitle()
     */
    @Override
    public String getTargetAccountingLinesSectionTitle() {
        return KFSConstants.BudgetAdjustmentDocumentConstants.TARGET_BA;
    }

    /**
     * @see org.kuali.rice.krad.document.DocumentBase#populateDocumentForRouting()
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
     * @param accountingLine accounting line being evaluated as a debit or not
     * @see org.kuali.rice.krad.rule.AccountingLineRule#isDebit(org.kuali.rice.krad.document.FinancialDocument,
     *      org.kuali.rice.krad.bo.AccountingLine)
     */
    @Override
    public boolean isDebit(GeneralLedgerPendingEntrySourceDetail postable) {
        try {
            DebitDeterminerService isDebitUtils = SpringContext.getBean(DebitDeterminerService.class);
            return isDebitUtils.isDebitConsideringType(this, (AccountingLine) postable);
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
     * @see org.kuali.module.financial.rules.FinancialDocumentRuleBase#processGenerateGeneralLedgerPendingEntries(org.kuali.rice.krad.document.FinancialDocument,
     *      org.kuali.rice.krad.bo.AccountingLine, org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper)
     */
    @Override
    public boolean generateGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySourceDetail glpeSourceDetail, GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        AccountingLine accountingLine = (AccountingLine) glpeSourceDetail;

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

            // create monthly lines (MB)
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
     * 
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
     * @see org.kuali.rice.krad.rule.GenerateGeneralLedgerDocumentPendingEntriesRule#processGenerateDocumentGeneralLedgerPendingEntries(org.kuali.rice.krad.document.FinancialDocument,
     *      org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntrySequenceHelper)
     */
    @Override
    public boolean generateDocumentGeneralLedgerPendingEntries(GeneralLedgerPendingEntrySequenceHelper sequenceHelper) {
        boolean success = true;

        // check on-off tof flag
        boolean generateTransfer = SpringContext.getBean(ParameterService.class).getParameterValueAsBoolean(BudgetAdjustmentDocument.class, BudgetAdjustmentDocumentRuleConstants.GENERATE_TOF_GLPE_ENTRIES_PARM_NM);
        String transferObjectCode = SpringContext.getBean(ParameterService.class).getParameterValueAsString(BudgetAdjustmentDocument.class, BudgetAdjustmentDocumentRuleConstants.TRANSFER_OBJECT_CODE_PARM_NM);
        Integer currentFiscalYear = SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear();

        if (generateTransfer) {
            // map of income chart/accounts with balance as value
            Map<String, KualiDecimal> incomeStreamMap = buildIncomeStreamBalanceMapForTransferOfFundsGeneration();
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
                    SystemOptions options = SpringContext.getBean(OptionsService.class).getCurrentYearOptions();
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
     * document as the keys, and transfer amounts as the values.  The transfer amount is calculated from (curr_frm_inc - curr_frm_exp) - (curr_to_inc - curr_to_exp)
     * 
     * @param baDocument budget adjustment document
     * @return Map used to balance current amounts
     */
    public Map buildIncomeStreamBalanceMapForTransferOfFundsGeneration() {
        Map<String, KualiDecimal> incomeStreamBalance = new HashMap<String, KualiDecimal>();

        List<BudgetAdjustmentAccountingLine> accountingLines = new ArrayList<BudgetAdjustmentAccountingLine>();
        accountingLines.addAll(getSourceAccountingLines());
        accountingLines.addAll(getTargetAccountingLines());
        
        ParameterEvaluatorService parameterEvaluatorService = SpringContext.getBean(ParameterEvaluatorService.class);
        
        for (BudgetAdjustmentAccountingLine budgetAccountingLine : accountingLines) {
            Account baAccount = budgetAccountingLine.getAccount();
            
            if(parameterEvaluatorService.getParameterEvaluator(BudgetAdjustmentDocument.class, KFSConstants.BudgetAdjustmentDocumentConstants.CROSS_INCOME_STREAM_GLPE_TRANSFER_GENERATING_FUND_GROUPS, baAccount.getSubFundGroup().getFundGroupCode()).evaluationSucceeds() &&
               parameterEvaluatorService.getParameterEvaluator(BudgetAdjustmentDocument.class, KFSConstants.BudgetAdjustmentDocumentConstants.CROSS_INCOME_STREAM_GLPE_TRANSFER_GENERATING_SUB_FUND_GROUPS, baAccount.getSubFundGroupCode()).evaluationSucceeds()) {
                
                String incomeStreamKey = baAccount.getIncomeStreamFinancialCoaCode() + BudgetAdjustmentDocumentRuleConstants.INCOME_STREAM_CHART_ACCOUNT_DELIMITER + baAccount.getIncomeStreamAccountNumber();
    
                // place record in balance map
                incomeStreamBalance.put(incomeStreamKey, getIncomeStreamAmount(budgetAccountingLine, incomeStreamBalance.get(incomeStreamKey)));
            }
        }

        return incomeStreamBalance;
    }

    /**
     * Builds a map used for balancing current adjustment amounts. The map contains income chart and accounts contained on the
     * document as the keys, and transfer amounts as the values. The transfer amount is calculated from (curr_frm_inc - curr_frm_exp) - (curr_to_inc - curr_to_exp)
     * 
     * @param baDocument budget adjustment document
     * @return Map used to balance current amounts
     */
    public Map buildIncomeStreamBalanceMapForDocumentBalance() {
        Map<String, KualiDecimal> incomeStreamBalance = new HashMap<String, KualiDecimal>();

        List<BudgetAdjustmentAccountingLine> accountingLines = new ArrayList<BudgetAdjustmentAccountingLine>();
        accountingLines.addAll(getSourceAccountingLines());
        accountingLines.addAll(getTargetAccountingLines());
        for (BudgetAdjustmentAccountingLine budgetAccountingLine : accountingLines) {
            
            String incomeStreamKey = budgetAccountingLine.getAccount().getIncomeStreamFinancialCoaCode() + BudgetAdjustmentDocumentRuleConstants.INCOME_STREAM_CHART_ACCOUNT_DELIMITER + budgetAccountingLine.getAccount().getIncomeStreamAccountNumber();

            // place record in balance map
            incomeStreamBalance.put(incomeStreamKey, getIncomeStreamAmount(budgetAccountingLine, incomeStreamBalance.get(incomeStreamKey)));
        }

        return incomeStreamBalance;
    }

    /**
     * 
     * This method calculates the appropriate income stream amount for an account using the value provided and the provided accounting line.
     * 
     * @param budgetAccountingLine
     * @param incomeStreamAmount
     * @return
     */
    protected KualiDecimal getIncomeStreamAmount(BudgetAdjustmentAccountingLine budgetAccountingLine, KualiDecimal incomeStreamAmount) {
        if(incomeStreamAmount == null) {
            incomeStreamAmount = new KualiDecimal(0);
        }

        // amounts need to be reversed for source expense lines and target income lines
        DebitDeterminerService isDebitUtils = SpringContext.getBean(DebitDeterminerService.class);
        if ((budgetAccountingLine instanceof BudgetAdjustmentSourceAccountingLine && isDebitUtils.isExpense((AccountingLine) budgetAccountingLine)) || (budgetAccountingLine instanceof BudgetAdjustmentTargetAccountingLine && isDebitUtils.isIncome((AccountingLine) budgetAccountingLine))) {
            incomeStreamAmount = incomeStreamAmount.subtract(budgetAccountingLine.getCurrentBudgetAdjustmentAmount());
        }
        else {
            incomeStreamAmount = incomeStreamAmount.add(budgetAccountingLine.getCurrentBudgetAdjustmentAmount());
        }

        return incomeStreamAmount;
    }
    
    /**
     * Returns the document type code for the Transfer of Funds document
     * 
     * @return the document type name to be used for the income stream transfer glpe
     */
    protected String getTransferDocumentType() {
        return TransferOfFundsDocumentRuleConstants.TRANSFER_OF_FUNDS_DOC_TYPE_CODE;
    }


    /**
     * @see org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase#answerSplitNodeQuestion(java.lang.String)
     */
    @Override
    public boolean answerSplitNodeQuestion(String nodeName) throws UnsupportedOperationException {
        if (nodeName.equals(BudgetAdjustmentDocument.REQUIRES_FULL_APPROVAL_SPLIT_NODE_NAME)) {
            return requiresFullApproval();
        }
        return super.answerSplitNodeQuestion(nodeName);
    }

    /**
     * Determines if this document can be auto-approved or not. The conditions for auto-approval are: 1) Single account used on document 2) Initiator is
     * fiscal officer or primary delegate for the account 3) Only current adjustments are being made 4) The fund group for the account
     * is not contract and grants 5) current income/expense decrease amount must equal increase amount
     * @return false if auto-approval can occur (and therefore, full approval is not required); true if a full approval is required
     */
    //MSU Contribution DTT-3059, DTT-3235 KFSMI-8689 KFSCNTRB-941 - Re-implemented this method to execute the rules correctly
    protected boolean requiresFullApproval() {
        List<BudgetAdjustmentAccountingLine> accountingLines = new ArrayList<BudgetAdjustmentAccountingLine>();
        accountingLines.addAll(getSourceAccountingLines());
        accountingLines.addAll(getTargetAccountingLines());

        HashSet<String> distinctAccts = new HashSet<String>();
        HashSet<String> distinctObjs = new HashSet<String>();
        String accountKey = "";
        String objCdKey = "";

        for (BudgetAdjustmentAccountingLine account : accountingLines) {
            if(account.getBaseBudgetAdjustmentAmount().isNonZero()){
                return true;
            }
            accountKey = account.getChartOfAccountsCode() + "-" + account.getAccountNumber();
            objCdKey = account.getPostingYear() + "-" + account.getChartOfAccountsCode() + "-" + account.getFinancialObjectCode();
            distinctAccts.add(accountKey);
            distinctObjs.add(objCdKey);
            if (distinctAccts.size() > 1 || distinctObjs.size() > 1) {
                return true;
            }
        }

        String chart = "";
        String accountNumber = "";

        // check remaining conditions
        // initiator should be fiscal officer or primary delegate for account
        Person initiator = KimApiServiceLocator.getPersonService().getPersonByPrincipalName(getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId());
        List userAccounts = SpringContext.getBean(AccountService.class).getAccountsThatUserIsResponsibleFor(initiator);
        //DTT:3059-Loop over all the accounts present on the document and see if user account responsibility includes them
        for (Iterator iter1 = accountingLines.iterator(); iter1.hasNext();) {

            BudgetAdjustmentAccountingLine line = (BudgetAdjustmentAccountingLine) iter1.next();
            chart = line.getChartOfAccountsCode();
            accountNumber = line.getAccountNumber();

            Account userAccount = null;
            for (Iterator iter2 = userAccounts.iterator(); iter2.hasNext();) {
                AccountResponsibility account = (AccountResponsibility) iter2.next();
                if (chart.equals(account.getAccount().getChartOfAccountsCode()) && accountNumber.equals(account.getAccount().getAccountNumber())) {
                    userAccount = account.getAccount();
                    break;
                }
            }

            if (userAccount == null) {
                return true;
            }
            else {
                // fund group should not be CG
                if (userAccount.isForContractsAndGrants()) {
                    return true;
                }

                // current income/expense decrease amount must equal increase amount
                if (!getSourceCurrentBudgetIncomeTotal().equals(getTargetCurrentBudgetIncomeTotal()) || !getSourceCurrentBudgetExpenseTotal().equals(getTargetCurrentBudgetExpenseTotal())) {
                    return true;
                }
            }// End of else block.

        }// End of for loop

        return false;

    }
}
