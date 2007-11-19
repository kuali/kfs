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
import org.kuali.kfs.bo.TargetAccountingLine;

/**
 * This class represents a target accounting line for budget adjustment
 */
public class BudgetAdjustmentTargetAccountingLine extends TargetAccountingLine implements BudgetAdjustmentAccountingLine {

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

    public BudgetAdjustmentTargetAccountingLine() {
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

    public KualiInteger getBaseBudgetAdjustmentAmount() {
        return baseBudgetAdjustmentAmount;
    }

    public void setBaseBudgetAdjustmentAmount(KualiInteger baseBudgetAdjustmentAmount) {
        if (baseBudgetAdjustmentAmount != null) {
            this.baseBudgetAdjustmentAmount = baseBudgetAdjustmentAmount;
        }
    }

    public String getBudgetAdjustmentPeriodCode() {
        return budgetAdjustmentPeriodCode;
    }

    public void setBudgetAdjustmentPeriodCode(String budgetAdjustmentPeriodCode) {
        this.budgetAdjustmentPeriodCode = budgetAdjustmentPeriodCode;
    }

    public KualiDecimal getCurrentBudgetAdjustmentAmount() {
        return currentBudgetAdjustmentAmount;
    }

    public void setCurrentBudgetAdjustmentAmount(KualiDecimal currentBudgetAdjustmentAmount) {
        if (currentBudgetAdjustmentAmount != null) {
            this.currentBudgetAdjustmentAmount = currentBudgetAdjustmentAmount;
        }
    }

    /**
     * Gets the financialDocumentMonth1LineAmount attribute.
     * 
     * @return Returns the financialDocumentMonth1LineAmount
     */
    public KualiDecimal getFinancialDocumentMonth1LineAmount() {
        return financialDocumentMonth1LineAmount;
    }

    /**
     * Sets the financialDocumentMonth1LineAmount attribute.
     * 
     * @param financialDocumentMonth1LineAmount The financialDocumentMonth1LineAmount to set.
     */
    public void setFinancialDocumentMonth1LineAmount(KualiDecimal financialDocumentMonth1LineAmount) {
        if (financialDocumentMonth1LineAmount != null) {
            this.financialDocumentMonth1LineAmount = financialDocumentMonth1LineAmount;
        }
    }


    /**
     * Gets the financialDocumentMonth2LineAmount attribute.
     * 
     * @return Returns the financialDocumentMonth2LineAmount
     */
    public KualiDecimal getFinancialDocumentMonth2LineAmount() {
        return financialDocumentMonth2LineAmount;
    }

    /**
     * Sets the financialDocumentMonth2LineAmount attribute.
     * 
     * @param financialDocumentMonth2LineAmount The financialDocumentMonth2LineAmount to set.
     */
    public void setFinancialDocumentMonth2LineAmount(KualiDecimal financialDocumentMonth2LineAmount) {
        if (financialDocumentMonth2LineAmount != null) {
            this.financialDocumentMonth2LineAmount = financialDocumentMonth2LineAmount;
        }
    }


    /**
     * Gets the financialDocumentMonth3LineAmount attribute.
     * 
     * @return Returns the financialDocumentMonth3LineAmount
     */
    public KualiDecimal getFinancialDocumentMonth3LineAmount() {
        return financialDocumentMonth3LineAmount;
    }

    /**
     * Sets the financialDocumentMonth3LineAmount attribute.
     * 
     * @param financialDocumentMonth3LineAmount The financialDocumentMonth3LineAmount to set.
     */
    public void setFinancialDocumentMonth3LineAmount(KualiDecimal financialDocumentMonth3LineAmount) {
        if (financialDocumentMonth3LineAmount != null) {
            this.financialDocumentMonth3LineAmount = financialDocumentMonth3LineAmount;
        }
    }


    /**
     * Gets the financialDocumentMonth4LineAmount attribute.
     * 
     * @return Returns the financialDocumentMonth4LineAmount
     */
    public KualiDecimal getFinancialDocumentMonth4LineAmount() {
        return financialDocumentMonth4LineAmount;
    }

    /**
     * Sets the financialDocumentMonth4LineAmount attribute.
     * 
     * @param financialDocumentMonth4LineAmount The financialDocumentMonth4LineAmount to set.
     */
    public void setFinancialDocumentMonth4LineAmount(KualiDecimal financialDocumentMonth4LineAmount) {
        if (financialDocumentMonth4LineAmount != null) {
            this.financialDocumentMonth4LineAmount = financialDocumentMonth4LineAmount;
        }
    }


    /**
     * Gets the financialDocumentMonth5LineAmount attribute.
     * 
     * @return Returns the financialDocumentMonth5LineAmount
     */
    public KualiDecimal getFinancialDocumentMonth5LineAmount() {
        return financialDocumentMonth5LineAmount;
    }

    /**
     * Sets the financialDocumentMonth5LineAmount attribute.
     * 
     * @param financialDocumentMonth5LineAmount The financialDocumentMonth5LineAmount to set.
     */
    public void setFinancialDocumentMonth5LineAmount(KualiDecimal financialDocumentMonth5LineAmount) {
        if (financialDocumentMonth5LineAmount != null) {
            this.financialDocumentMonth5LineAmount = financialDocumentMonth5LineAmount;
        }
    }


    /**
     * Gets the financialDocumentMonth6LineAmount attribute.
     * 
     * @return Returns the financialDocumentMonth6LineAmount
     */
    public KualiDecimal getFinancialDocumentMonth6LineAmount() {
        return financialDocumentMonth6LineAmount;
    }

    /**
     * Sets the financialDocumentMonth6LineAmount attribute.
     * 
     * @param financialDocumentMonth6LineAmount The financialDocumentMonth6LineAmount to set.
     */
    public void setFinancialDocumentMonth6LineAmount(KualiDecimal financialDocumentMonth6LineAmount) {
        if (financialDocumentMonth6LineAmount != null) {
            this.financialDocumentMonth6LineAmount = financialDocumentMonth6LineAmount;
        }
    }


    /**
     * Gets the financialDocumentMonth7LineAmount attribute.
     * 
     * @return Returns the financialDocumentMonth7LineAmount
     */
    public KualiDecimal getFinancialDocumentMonth7LineAmount() {
        return financialDocumentMonth7LineAmount;
    }

    /**
     * Sets the financialDocumentMonth7LineAmount attribute.
     * 
     * @param financialDocumentMonth7LineAmount The financialDocumentMonth7LineAmount to set.
     */
    public void setFinancialDocumentMonth7LineAmount(KualiDecimal financialDocumentMonth7LineAmount) {
        if (financialDocumentMonth7LineAmount != null) {
            this.financialDocumentMonth7LineAmount = financialDocumentMonth7LineAmount;
        }
    }


    /**
     * Gets the financialDocumentMonth8LineAmount attribute.
     * 
     * @return Returns the financialDocumentMonth8LineAmount
     */
    public KualiDecimal getFinancialDocumentMonth8LineAmount() {
        return financialDocumentMonth8LineAmount;
    }

    /**
     * Sets the financialDocumentMonth8LineAmount attribute.
     * 
     * @param financialDocumentMonth8LineAmount The financialDocumentMonth8LineAmount to set.
     */
    public void setFinancialDocumentMonth8LineAmount(KualiDecimal financialDocumentMonth8LineAmount) {
        if (financialDocumentMonth8LineAmount != null) {
            this.financialDocumentMonth8LineAmount = financialDocumentMonth8LineAmount;
        }
    }


    /**
     * Gets the financialDocumentMonth9LineAmount attribute.
     * 
     * @return Returns the financialDocumentMonth9LineAmount
     */
    public KualiDecimal getFinancialDocumentMonth9LineAmount() {
        return financialDocumentMonth9LineAmount;
    }

    /**
     * Sets the financialDocumentMonth9LineAmount attribute.
     * 
     * @param financialDocumentMonth9LineAmount The financialDocumentMonth9LineAmount to set.
     */
    public void setFinancialDocumentMonth9LineAmount(KualiDecimal financialDocumentMonth9LineAmount) {
        if (financialDocumentMonth9LineAmount != null) {
            this.financialDocumentMonth9LineAmount = financialDocumentMonth9LineAmount;
        }
    }


    /**
     * Gets the financialDocumentMonth10LineAmount attribute.
     * 
     * @return Returns the financialDocumentMonth10LineAmount
     */
    public KualiDecimal getFinancialDocumentMonth10LineAmount() {
        return financialDocumentMonth10LineAmount;
    }

    /**
     * Sets the financialDocumentMonth10LineAmount attribute.
     * 
     * @param financialDocumentMonth10LineAmount The financialDocumentMonth10LineAmount to set.
     */
    public void setFinancialDocumentMonth10LineAmount(KualiDecimal financialDocumentMonth10LineAmount) {
        if (financialDocumentMonth10LineAmount != null) {
            this.financialDocumentMonth10LineAmount = financialDocumentMonth10LineAmount;
        }
    }

    /**
     * Gets the financialDocumentMonth11LineAmount attribute.
     * 
     * @return Returns the financialDocumentMonth11LineAmount
     */
    public KualiDecimal getFinancialDocumentMonth11LineAmount() {
        return financialDocumentMonth11LineAmount;
    }

    /**
     * Sets the financialDocumentMonth11LineAmount attribute.
     * 
     * @param financialDocumentMonth11LineAmount The financialDocumentMonth11LineAmount to set.
     */
    public void setFinancialDocumentMonth11LineAmount(KualiDecimal financialDocumentMonth11LineAmount) {
        if (financialDocumentMonth11LineAmount != null) {
            this.financialDocumentMonth11LineAmount = financialDocumentMonth11LineAmount;
        }
    }

    /**
     * Gets the financialDocumentMonth12LineAmount attribute.
     * 
     * @return Returns the financialDocumentMonth12LineAmount
     */
    public KualiDecimal getFinancialDocumentMonth12LineAmount() {
        return financialDocumentMonth12LineAmount;
    }

    /**
     * Sets the financialDocumentMonth12LineAmount attribute.
     * 
     * @param financialDocumentMonth12LineAmount The financialDocumentMonth12LineAmount to set.
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
        financialDocumentMonth1LineAmount = new KualiDecimal(0);
        financialDocumentMonth2LineAmount = new KualiDecimal(0);
        financialDocumentMonth3LineAmount = new KualiDecimal(0);
        financialDocumentMonth4LineAmount = new KualiDecimal(0);
        financialDocumentMonth5LineAmount = new KualiDecimal(0);
        financialDocumentMonth6LineAmount = new KualiDecimal(0);
        financialDocumentMonth7LineAmount = new KualiDecimal(0);
        financialDocumentMonth8LineAmount = new KualiDecimal(0);
        financialDocumentMonth9LineAmount = new KualiDecimal(0);
        financialDocumentMonth10LineAmount = new KualiDecimal(0);
        financialDocumentMonth11LineAmount = new KualiDecimal(0);
        financialDocumentMonth12LineAmount = new KualiDecimal(0);
    }
}