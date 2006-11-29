/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/fp/document/BudgetAdjustmentDocument.java,v $
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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.Constants;
import org.kuali.PropertyConstants;
import org.kuali.core.bo.AccountingLineParser;
import org.kuali.core.document.TransactionalDocumentBase;
import org.kuali.core.util.GeneralLedgerPendingEntrySequenceHelper;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.KualiInteger;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.web.format.CurrencyFormatter;
import org.kuali.module.financial.bo.BudgetAdjustmentAccountingLine;
import org.kuali.module.financial.bo.BudgetAdjustmentAccountingLineParser;
import org.kuali.module.financial.bo.FiscalYearFunctionControl;
import org.kuali.module.financial.rules.BudgetAdjustmentDocumentRule;
import org.kuali.module.financial.rules.TransactionalDocumentRuleUtil;
import org.kuali.module.gl.bo.GeneralLedgerPendingEntry;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This is the business object that represents the BudgetAdjustment document in Kuali.
 * 
 * 
 */
public class BudgetAdjustmentDocument extends TransactionalDocumentBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetAdjustmentDocument.class);

    private Integer nextPositionSourceLineNumber;
    private Integer nextPositionTargetLineNumber;

    /**
     * Default constructor.
     */
    public BudgetAdjustmentDocument() {
        super();
    }

    
    /***
     *BA Documents should only do SF checking on PLEs with a Balance Type of 'CB' - not 'BB' or 'MB'.
     *
     * @Override
     * @see org.kuali.core.document.TransactionalDocumentBase#getPendingLedgetEntriesForSufficientFundsChecking()
     */
    public List<GeneralLedgerPendingEntry> getPendingLedgerEntriesForSufficientFundsChecking() {
        List <GeneralLedgerPendingEntry> pendingLedgerEntries = new ArrayList();

        GeneralLedgerPendingEntrySequenceHelper glpeSequenceHelper = new GeneralLedgerPendingEntrySequenceHelper();
        BudgetAdjustmentDocumentRule budgetAdjustmentDocumentRule = new BudgetAdjustmentDocumentRule();
        
        BudgetAdjustmentDocument copiedBa = (BudgetAdjustmentDocument) ObjectUtils.deepCopy(this);
        copiedBa.getGeneralLedgerPendingEntries().clear();
        for (BudgetAdjustmentAccountingLine fromLine : (List<BudgetAdjustmentAccountingLine>)copiedBa.getSourceAccountingLines()) {
            budgetAdjustmentDocumentRule.processGenerateGeneralLedgerPendingEntries(copiedBa, fromLine, glpeSequenceHelper);    
        }
        
        
        for (GeneralLedgerPendingEntry ple : copiedBa.getGeneralLedgerPendingEntries()) {
            if (!Constants.BALANCE_TYPE_BASE_BUDGET.equals(ple.getFinancialBalanceTypeCode()) && !Constants.BALANCE_TYPE_MONTHLY_BUDGET.equals(ple.getFinancialBalanceTypeCode())) {
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
        List allowedYears = SpringServiceLocator.getKeyValuesService().getBudgetAdjustmentAllowedYears();
        Integer currentYearParam = SpringServiceLocator.getDateTimeService().getCurrentFiscalYear();
        
        FiscalYearFunctionControl fiscalYearFunctionControl = new FiscalYearFunctionControl();
        fiscalYearFunctionControl.setUniversityFiscalYear(currentYearParam);
        
        // use 'this.postingYear =' because setPostingYear has logic we want to circumvent on initiateDocument
        if(allowedYears.contains(fiscalYearFunctionControl)) {
            this.postingYear = currentYearParam;
        } else {
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
        KualiDecimal currentBudgetTotal = new KualiDecimal(0);

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
        KualiDecimal total = new KualiDecimal(0);

        for (Iterator iter = sourceAccountingLines.iterator(); iter.hasNext();) {
            BudgetAdjustmentAccountingLine line = (BudgetAdjustmentAccountingLine) iter.next();
            if (TransactionalDocumentRuleUtil.isIncome(line)) {
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
        KualiDecimal total = new KualiDecimal(0);

        for (Iterator iter = sourceAccountingLines.iterator(); iter.hasNext();) {
            BudgetAdjustmentAccountingLine line = (BudgetAdjustmentAccountingLine) iter.next();
            if (TransactionalDocumentRuleUtil.isExpense(line)) {
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
        KualiDecimal currentBudgetTotal = new KualiDecimal(0);

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
        KualiDecimal total = new KualiDecimal(0);

        for (Iterator iter = targetAccountingLines.iterator(); iter.hasNext();) {
            BudgetAdjustmentAccountingLine line = (BudgetAdjustmentAccountingLine) iter.next();
            if (TransactionalDocumentRuleUtil.isIncome(line)) {
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
        KualiDecimal total = new KualiDecimal(0);

        for (Iterator iter = targetAccountingLines.iterator(); iter.hasNext();) {
            BudgetAdjustmentAccountingLine line = (BudgetAdjustmentAccountingLine) iter.next();
            if (TransactionalDocumentRuleUtil.isExpense(line)) {
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
        KualiInteger baseBudgetTotal = new KualiInteger(0);

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
        KualiInteger total = new KualiInteger(0);

        for (Iterator iter = sourceAccountingLines.iterator(); iter.hasNext();) {
            BudgetAdjustmentAccountingLine line = (BudgetAdjustmentAccountingLine) iter.next();
            if (TransactionalDocumentRuleUtil.isIncome(line)) {
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
        KualiInteger total = new KualiInteger(0);

        for (Iterator iter = sourceAccountingLines.iterator(); iter.hasNext();) {
            BudgetAdjustmentAccountingLine line = (BudgetAdjustmentAccountingLine) iter.next();
            if (TransactionalDocumentRuleUtil.isExpense(line)) {
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
        KualiInteger baseBudgetTotal = new KualiInteger(0);

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
        KualiInteger total = new KualiInteger(0);

        for (Iterator iter = targetAccountingLines.iterator(); iter.hasNext();) {
            BudgetAdjustmentAccountingLine line = (BudgetAdjustmentAccountingLine) iter.next();
            if (TransactionalDocumentRuleUtil.isIncome(line)) {
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
        KualiInteger total = new KualiInteger(0);

        for (Iterator iter = targetAccountingLines.iterator(); iter.hasNext();) {
            BudgetAdjustmentAccountingLine line = (BudgetAdjustmentAccountingLine) iter.next();
            if (TransactionalDocumentRuleUtil.isExpense(line)) {
                total = total.add(line.getBaseBudgetAdjustmentAmount());
            }
        }

        return total;
    }

    /**
     * @see org.kuali.core.document.TransactionalDocumentBase#getTotalDollarAmount()
     */
    @Override
    public KualiDecimal getTotalDollarAmount() {
        return super.getTotalDollarAmount();
    }

    /**
     * Negate accounting line budget amounts.
     * 
     * @see org.kuali.core.document.TransactionalDocumentBase#convertIntoErrorCorrection()
     */
    @Override
    public void convertIntoErrorCorrection() throws WorkflowException {
        super.convertIntoErrorCorrection();

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
     * 
     * @see org.kuali.core.document.DocumentBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(PropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        return m;
    }

    /**
     * @see org.kuali.core.document.TransactionalDocumentBase#getAccountingLineParser()
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
     * @see org.kuali.core.document.TransactionalDocumentBase#getNullOrReasonNotToCopy(java.lang.String, boolean)
     */
    @Override
    protected String getNullOrReasonNotToCopy(String actionGerund, boolean ddAllows) {
        return null;
    }

    /**
     * @see org.kuali.core.document.TransactionalDocumentBase#getNullOrReasonNotToErrorCorrect()
     */
    @Override
    protected String getNullOrReasonNotToErrorCorrect() {
        return null;
    }
    
    /**
     * @see org.kuali.core.document.TransactionalDocumentBase#getSourceAccountingLinesSectionTitle()
     */
    @Override
    public String getSourceAccountingLinesSectionTitle() {
        return Constants.BudgetAdjustmentDocumentConstants.SOURCE_BA;
    }

    /**
     * @see org.kuali.core.document.TransactionalDocumentBase#getTargetAccountingLinesSectionTitle()
     */
    @Override
    public String getTargetAccountingLinesSectionTitle() {
        return Constants.BudgetAdjustmentDocumentConstants.TARGET_BA;
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
    
    
}
