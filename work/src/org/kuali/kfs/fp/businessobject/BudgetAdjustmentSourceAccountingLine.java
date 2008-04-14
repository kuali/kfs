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
package org.kuali.module.financial.bo;

import java.util.Map;

import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.KualiInteger;
import org.kuali.kfs.bo.AccountingLine;
import org.kuali.kfs.bo.SourceAccountingLine;


/**
 * Special case <code>{@link SourceAccountingLine}</code> type for
 * <code>{@link org.kuali.module.financial.document.BudgetAdjustmentDocument}</code>
 */
public class BudgetAdjustmentSourceAccountingLine extends SourceAccountingLine implements BudgetAdjustmentAccountingLine {

    private String budgetAdjustmentPeriodCode;
    private KualiDecimal currentBudgetAdjustmentAmount;
    private KualiInteger baseBudgetAdjustmentAmount;
    private KualiDecimal financialDocumentMonth1LineAmount;
    private KualiDecimal financialDocumentMonth2LineAmount;
    private KualiDecimal financialDocumentMonth3LineAmount;
    private KualiDecimal financialDocumentMonth4LineAmount;
    private KualiDecimal financialDocumentMonth5LineAmount;
    private KualiDecimal financialDocumentMonth6LineAmount;
    private KualiDecimal financialDocumentMonth7LineAmount;
    private KualiDecimal financialDocumentMonth8LineAmount;
    private KualiDecimal financialDocumentMonth9LineAmount;
    private KualiDecimal financialDocumentMonth10LineAmount;
    private KualiDecimal financialDocumentMonth11LineAmount;
    private KualiDecimal financialDocumentMonth12LineAmount;
    private boolean fringeBenefitIndicator;

    public BudgetAdjustmentSourceAccountingLine() {
        super();
        BudgetAdjustmentAccountingLineUtil.init(this);
    }

    /**
     * @see org.kuali.core.bo.AccountingLineBase#getValuesMap()
     */
    @Override
    public Map getValuesMap() {
        Map simpleValues = super.getValuesMap();
        BudgetAdjustmentAccountingLineUtil.appendToValuesMap(simpleValues, this);
        return simpleValues;
    }

    /**
     * @see org.kuali.core.bo.AccountingLineBase#copyFrom(org.kuali.core.bo.AccountingLine)
     */
    @Override
    public void copyFrom(AccountingLine other) {
        super.copyFrom(other);
        BudgetAdjustmentAccountingLineUtil.copyFrom(this, other);
    }

    /**
     * @see org.kuali.module.financial.bo.BudgetAdjustmentAccountingLine#getMonthlyLinesTotal()
     */
    public KualiDecimal getMonthlyLinesTotal() {
        return BudgetAdjustmentAccountingLineUtil.getMonthlyLinesTotal(this);
    }

    /**
     * @see org.kuali.module.financial.bo.BudgetAdjustmentAccountingLine#getBaseBudgetAdjustmentAmount()
     */
    public KualiInteger getBaseBudgetAdjustmentAmount() {
        return baseBudgetAdjustmentAmount;
    }

    /**
     * @see org.kuali.module.financial.bo.BudgetAdjustmentAccountingLine#setBaseBudgetAdjustmentAmount(org.kuali.core.util.KualiDecimal)
     */
    public void setBaseBudgetAdjustmentAmount(KualiInteger baseBudgetAdjustmentAmount) {
        if (baseBudgetAdjustmentAmount != null) {
            this.baseBudgetAdjustmentAmount = baseBudgetAdjustmentAmount;
        }
    }

    public void setBaseBudgetAdjustmentAmount(KualiDecimal baseBudgetAdjustmentAmount) {
        if (baseBudgetAdjustmentAmount != null) {
            this.baseBudgetAdjustmentAmount = new KualiInteger(baseBudgetAdjustmentAmount.bigDecimalValue());
        }
    }

    /**
     * @see org.kuali.module.financial.bo.BudgetAdjustmentAccountingLine#getBudgetAdjustmentPeriodCode()
     */
    public String getBudgetAdjustmentPeriodCode() {
        return budgetAdjustmentPeriodCode;
    }

    /**
     * @see org.kuali.module.financial.bo.BudgetAdjustmentAccountingLine#setBudgetAdjustmentPeriodCode(java.lang.String)
     */
    public void setBudgetAdjustmentPeriodCode(String budgetAdjustmentPeriodCode) {
        this.budgetAdjustmentPeriodCode = budgetAdjustmentPeriodCode;
    }

    /**
     * @see org.kuali.module.financial.bo.BudgetAdjustmentAccountingLine#getCurrentBudgetAdjustmentAmount()
     */
    public KualiDecimal getCurrentBudgetAdjustmentAmount() {
        return currentBudgetAdjustmentAmount;
    }

    /**
     * @see org.kuali.module.financial.bo.BudgetAdjustmentAccountingLine#setCurrentBudgetAdjustmentAmount(org.kuali.core.util.KualiDecimal)
     */
    public void setCurrentBudgetAdjustmentAmount(KualiDecimal currentBudgetAdjustmentAmount) {
        if (currentBudgetAdjustmentAmount != null) {
            this.currentBudgetAdjustmentAmount = currentBudgetAdjustmentAmount;
        }
    }

    /**
     * @see org.kuali.module.financial.bo.BudgetAdjustmentAccountingLine#getFinancialDocumentMonth1LineAmount()
     */
    public KualiDecimal getFinancialDocumentMonth1LineAmount() {
        return financialDocumentMonth1LineAmount;
    }

    /**
     * @see org.kuali.module.financial.bo.BudgetAdjustmentAccountingLine#setFinancialDocumentMonth1LineAmount(org.kuali.core.util.KualiDecimal)
     */
    public void setFinancialDocumentMonth1LineAmount(KualiDecimal financialDocumentMonth1LineAmount) {
        if (financialDocumentMonth1LineAmount != null) {
            this.financialDocumentMonth1LineAmount = financialDocumentMonth1LineAmount;
        }
    }


    /**
     * @see org.kuali.module.financial.bo.BudgetAdjustmentAccountingLine#getFinancialDocumentMonth2LineAmount()
     */
    public KualiDecimal getFinancialDocumentMonth2LineAmount() {
        return financialDocumentMonth2LineAmount;
    }

    /**
     * @see org.kuali.module.financial.bo.BudgetAdjustmentAccountingLine#setFinancialDocumentMonth2LineAmount(org.kuali.core.util.KualiDecimal)
     */
    public void setFinancialDocumentMonth2LineAmount(KualiDecimal financialDocumentMonth2LineAmount) {
        if (financialDocumentMonth2LineAmount != null) {
            this.financialDocumentMonth2LineAmount = financialDocumentMonth2LineAmount;
        }
    }


    /**
     * @see org.kuali.module.financial.bo.BudgetAdjustmentAccountingLine#getFinancialDocumentMonth3LineAmount()
     */
    public KualiDecimal getFinancialDocumentMonth3LineAmount() {
        return financialDocumentMonth3LineAmount;
    }

    /**
     * @see org.kuali.module.financial.bo.BudgetAdjustmentAccountingLine#setFinancialDocumentMonth3LineAmount(org.kuali.core.util.KualiDecimal)
     */
    public void setFinancialDocumentMonth3LineAmount(KualiDecimal financialDocumentMonth3LineAmount) {
        if (financialDocumentMonth3LineAmount != null) {
            this.financialDocumentMonth3LineAmount = financialDocumentMonth3LineAmount;
        }
    }


    /**
     * @see org.kuali.module.financial.bo.BudgetAdjustmentAccountingLine#getFinancialDocumentMonth4LineAmount()
     */
    public KualiDecimal getFinancialDocumentMonth4LineAmount() {
        return financialDocumentMonth4LineAmount;
    }

    /**
     * @see org.kuali.module.financial.bo.BudgetAdjustmentAccountingLine#setFinancialDocumentMonth4LineAmount(org.kuali.core.util.KualiDecimal)
     */
    public void setFinancialDocumentMonth4LineAmount(KualiDecimal financialDocumentMonth4LineAmount) {
        if (financialDocumentMonth4LineAmount != null) {
            this.financialDocumentMonth4LineAmount = financialDocumentMonth4LineAmount;
        }
    }


    /**
     * @see org.kuali.module.financial.bo.BudgetAdjustmentAccountingLine#getFinancialDocumentMonth5LineAmount()
     */
    public KualiDecimal getFinancialDocumentMonth5LineAmount() {
        return financialDocumentMonth5LineAmount;
    }

    /**
     * @see org.kuali.module.financial.bo.BudgetAdjustmentAccountingLine#setFinancialDocumentMonth5LineAmount(org.kuali.core.util.KualiDecimal)
     */
    public void setFinancialDocumentMonth5LineAmount(KualiDecimal financialDocumentMonth5LineAmount) {
        if (financialDocumentMonth5LineAmount != null) {
            this.financialDocumentMonth5LineAmount = financialDocumentMonth5LineAmount;
        }
    }

    /**
     * @see org.kuali.module.financial.bo.BudgetAdjustmentAccountingLine#getFinancialDocumentMonth6LineAmount()
     */
    public KualiDecimal getFinancialDocumentMonth6LineAmount() {
        return financialDocumentMonth6LineAmount;
    }

    /**
     * @see org.kuali.module.financial.bo.BudgetAdjustmentAccountingLine#setFinancialDocumentMonth6LineAmount(org.kuali.core.util.KualiDecimal)
     */
    public void setFinancialDocumentMonth6LineAmount(KualiDecimal financialDocumentMonth6LineAmount) {
        if (financialDocumentMonth6LineAmount != null) {
            this.financialDocumentMonth6LineAmount = financialDocumentMonth6LineAmount;
        }
    }

    /**
     * @see org.kuali.module.financial.bo.BudgetAdjustmentAccountingLine#getFinancialDocumentMonth7LineAmount()
     */
    public KualiDecimal getFinancialDocumentMonth7LineAmount() {
        return financialDocumentMonth7LineAmount;
    }

    /**
     * @see org.kuali.module.financial.bo.BudgetAdjustmentAccountingLine#setFinancialDocumentMonth7LineAmount(org.kuali.core.util.KualiDecimal)
     */
    public void setFinancialDocumentMonth7LineAmount(KualiDecimal financialDocumentMonth7LineAmount) {
        if (financialDocumentMonth7LineAmount != null) {
            this.financialDocumentMonth7LineAmount = financialDocumentMonth7LineAmount;
        }
    }

    /**
     * @see org.kuali.module.financial.bo.BudgetAdjustmentAccountingLine#getFinancialDocumentMonth8LineAmount()
     */
    public KualiDecimal getFinancialDocumentMonth8LineAmount() {
        return financialDocumentMonth8LineAmount;
    }

    /**
     * @see org.kuali.module.financial.bo.BudgetAdjustmentAccountingLine#setFinancialDocumentMonth8LineAmount(org.kuali.core.util.KualiDecimal)
     */
    public void setFinancialDocumentMonth8LineAmount(KualiDecimal financialDocumentMonth8LineAmount) {
        if (financialDocumentMonth8LineAmount != null) {
            this.financialDocumentMonth8LineAmount = financialDocumentMonth8LineAmount;
        }
    }

    /**
     * @see org.kuali.module.financial.bo.BudgetAdjustmentAccountingLine#getFinancialDocumentMonth9LineAmount()
     */
    public KualiDecimal getFinancialDocumentMonth9LineAmount() {
        return financialDocumentMonth9LineAmount;
    }

    /**
     * @see org.kuali.module.financial.bo.BudgetAdjustmentAccountingLine#setFinancialDocumentMonth9LineAmount(org.kuali.core.util.KualiDecimal)
     */
    public void setFinancialDocumentMonth9LineAmount(KualiDecimal financialDocumentMonth9LineAmount) {
        if (financialDocumentMonth9LineAmount != null) {
            this.financialDocumentMonth9LineAmount = financialDocumentMonth9LineAmount;
        }
    }

    /**
     * @see org.kuali.module.financial.bo.BudgetAdjustmentAccountingLine#getFinancialDocumentMonth10LineAmount()
     */
    public KualiDecimal getFinancialDocumentMonth10LineAmount() {
        return financialDocumentMonth10LineAmount;
    }

    /**
     * @see org.kuali.module.financial.bo.BudgetAdjustmentAccountingLine#setFinancialDocumentMonth10LineAmount(org.kuali.core.util.KualiDecimal)
     */
    public void setFinancialDocumentMonth10LineAmount(KualiDecimal financialDocumentMonth10LineAmount) {
        if (financialDocumentMonth10LineAmount != null) {
            this.financialDocumentMonth10LineAmount = financialDocumentMonth10LineAmount;
        }
    }

    /**
     * @see org.kuali.module.financial.bo.BudgetAdjustmentAccountingLine#getFinancialDocumentMonth11LineAmount()
     */
    public KualiDecimal getFinancialDocumentMonth11LineAmount() {
        return financialDocumentMonth11LineAmount;
    }

    /**
     * @see org.kuali.module.financial.bo.BudgetAdjustmentAccountingLine#setFinancialDocumentMonth11LineAmount(org.kuali.core.util.KualiDecimal)
     */
    public void setFinancialDocumentMonth11LineAmount(KualiDecimal financialDocumentMonth11LineAmount) {
        if (financialDocumentMonth11LineAmount != null) {
            this.financialDocumentMonth11LineAmount = financialDocumentMonth11LineAmount;
        }
    }


    /**
     * @see org.kuali.module.financial.bo.BudgetAdjustmentAccountingLine#getFinancialDocumentMonth12LineAmount()
     */
    public KualiDecimal getFinancialDocumentMonth12LineAmount() {
        return financialDocumentMonth12LineAmount;
    }

    /**
     * @see org.kuali.module.financial.bo.BudgetAdjustmentAccountingLine#setFinancialDocumentMonth12LineAmount(org.kuali.core.util.KualiDecimal)
     */
    public void setFinancialDocumentMonth12LineAmount(KualiDecimal financialDocumentMonth12LineAmount) {
        if (financialDocumentMonth12LineAmount != null) {
            this.financialDocumentMonth12LineAmount = financialDocumentMonth12LineAmount;
        }
    }

    /**
     * Gets the fringeBenefitIndicator attribute.
     * 
     * @return Returns the fringeBenefitIndicator.
     */
    public boolean isFringeBenefitIndicator() {
        return fringeBenefitIndicator;
    }

    /**
     * Sets the fringeBenefitIndicator attribute value.
     * 
     * @param fringeBenefitIndicator The fringeBenefitIndicator to set.
     */
    public void setFringeBenefitIndicator(boolean fringeBenefitIndicator) {
        this.fringeBenefitIndicator = fringeBenefitIndicator;
    }

    /**
     * @see org.kuali.module.financial.bo.BudgetAdjustmentAccountingLine#clearFinancialDocumentMonthLineAmounts()
     */
    public void clearFinancialDocumentMonthLineAmounts() {
        financialDocumentMonth1LineAmount = KualiDecimal.ZERO;
        financialDocumentMonth2LineAmount = KualiDecimal.ZERO;
        financialDocumentMonth3LineAmount = KualiDecimal.ZERO;
        financialDocumentMonth4LineAmount = KualiDecimal.ZERO;
        financialDocumentMonth5LineAmount = KualiDecimal.ZERO;
        financialDocumentMonth6LineAmount = KualiDecimal.ZERO;
        financialDocumentMonth7LineAmount = KualiDecimal.ZERO;
        financialDocumentMonth8LineAmount = KualiDecimal.ZERO;
        financialDocumentMonth9LineAmount = KualiDecimal.ZERO;
        financialDocumentMonth10LineAmount = KualiDecimal.ZERO;
        financialDocumentMonth11LineAmount = KualiDecimal.ZERO;
        financialDocumentMonth12LineAmount = KualiDecimal.ZERO;
    }

    /**
     * @see org.kuali.kfs.bo.AccountingLineBase#isSourceAccountingLine()
     */
    @Override
    public boolean isSourceAccountingLine() {
        return true;
    }

    /**
     * @see org.kuali.kfs.bo.AccountingLineBase#isTargetAccountingLine()
     */
    @Override
    public boolean isTargetAccountingLine() {
        return false;
    }
    
    
}