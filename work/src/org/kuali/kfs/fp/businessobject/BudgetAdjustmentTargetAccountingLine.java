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
package org.kuali.module.financial.bo;

import org.kuali.core.bo.TargetAccountingLine;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.KualiInteger;


/**
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
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

    /**
     * This constructor needs to initialize the ojbConcreteClass attribute such that it sets it to its class name. This is how OJB
     * knows what grouping of objects to work with.
     */
    public BudgetAdjustmentTargetAccountingLine() {
        super();
        super.ojbConcreteClass = this.getClass().getName();
        this.currentBudgetAdjustmentAmount = new KualiDecimal(0);
        this.baseBudgetAdjustmentAmount = new KualiInteger(0);
        this.financialDocumentMonth1LineAmount = new KualiDecimal(0);
        this.financialDocumentMonth2LineAmount = new KualiDecimal(0);
        this.financialDocumentMonth3LineAmount = new KualiDecimal(0);
        this.financialDocumentMonth4LineAmount = new KualiDecimal(0);
        this.financialDocumentMonth5LineAmount = new KualiDecimal(0);
        this.financialDocumentMonth6LineAmount = new KualiDecimal(0);
        this.financialDocumentMonth7LineAmount = new KualiDecimal(0);
        this.financialDocumentMonth8LineAmount = new KualiDecimal(0);
        this.financialDocumentMonth9LineAmount = new KualiDecimal(0);
        this.financialDocumentMonth10LineAmount = new KualiDecimal(0);
        this.financialDocumentMonth11LineAmount = new KualiDecimal(0);
        this.financialDocumentMonth12LineAmount = new KualiDecimal(0);
        this.fringeBenefitIndicator = false;
    }

    /**
     * @see org.kuali.module.financial.bo.BudgetAdjustmentAccountingLine#getMonthlyLinesTotal()
     */
    public KualiDecimal getMonthlyLinesTotal() {
        KualiDecimal total = new KualiDecimal(0);
        if (getFinancialDocumentMonth1LineAmount() != null) {
            total = total.add(getFinancialDocumentMonth1LineAmount());
        }
        if (getFinancialDocumentMonth2LineAmount() != null) {
            total = total.add(getFinancialDocumentMonth2LineAmount());
        }
        if (getFinancialDocumentMonth3LineAmount() != null) {
            total = total.add(getFinancialDocumentMonth3LineAmount());
        }
        if (getFinancialDocumentMonth4LineAmount() != null) {
            total = total.add(getFinancialDocumentMonth4LineAmount());
        }
        if (getFinancialDocumentMonth5LineAmount() != null) {
            total = total.add(getFinancialDocumentMonth5LineAmount());
        }
        if (getFinancialDocumentMonth6LineAmount() != null) {
            total = total.add(getFinancialDocumentMonth6LineAmount());
        }
        if (getFinancialDocumentMonth7LineAmount() != null) {
            total = total.add(getFinancialDocumentMonth7LineAmount());
        }
        if (getFinancialDocumentMonth8LineAmount() != null) {
            total = total.add(getFinancialDocumentMonth8LineAmount());
        }
        if (getFinancialDocumentMonth9LineAmount() != null) {
            total = total.add(getFinancialDocumentMonth9LineAmount());
        }
        if (getFinancialDocumentMonth10LineAmount() != null) {
            total = total.add(getFinancialDocumentMonth10LineAmount());
        }
        if (getFinancialDocumentMonth11LineAmount() != null) {
            total = total.add(getFinancialDocumentMonth11LineAmount());
        }
        if (getFinancialDocumentMonth12LineAmount() != null) {
            total = total.add(getFinancialDocumentMonth12LineAmount());
        }
        return total;
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
     * @return - Returns the financialDocumentMonth1LineAmount
     * 
     */
    public KualiDecimal getFinancialDocumentMonth1LineAmount() {
        return financialDocumentMonth1LineAmount;
    }

    /**
     * Sets the financialDocumentMonth1LineAmount attribute.
     * 
     * @param financialDocumentMonth1LineAmount The financialDocumentMonth1LineAmount to set.
     * 
     */
    public void setFinancialDocumentMonth1LineAmount(KualiDecimal financialDocumentMonth1LineAmount) {
        if (financialDocumentMonth1LineAmount != null) {
            this.financialDocumentMonth1LineAmount = financialDocumentMonth1LineAmount;
        }
    }


    /**
     * Gets the financialDocumentMonth2LineAmount attribute.
     * 
     * @return - Returns the financialDocumentMonth2LineAmount
     * 
     */
    public KualiDecimal getFinancialDocumentMonth2LineAmount() {
        return financialDocumentMonth2LineAmount;
    }

    /**
     * Sets the financialDocumentMonth2LineAmount attribute.
     * 
     * @param financialDocumentMonth2LineAmount The financialDocumentMonth2LineAmount to set.
     * 
     */
    public void setFinancialDocumentMonth2LineAmount(KualiDecimal financialDocumentMonth2LineAmount) {
        if (financialDocumentMonth2LineAmount != null) {
            this.financialDocumentMonth2LineAmount = financialDocumentMonth2LineAmount;
        }
    }


    /**
     * Gets the financialDocumentMonth3LineAmount attribute.
     * 
     * @return - Returns the financialDocumentMonth3LineAmount
     * 
     */
    public KualiDecimal getFinancialDocumentMonth3LineAmount() {
        return financialDocumentMonth3LineAmount;
    }

    /**
     * Sets the financialDocumentMonth3LineAmount attribute.
     * 
     * @param financialDocumentMonth3LineAmount The financialDocumentMonth3LineAmount to set.
     * 
     */
    public void setFinancialDocumentMonth3LineAmount(KualiDecimal financialDocumentMonth3LineAmount) {
        if (financialDocumentMonth3LineAmount != null) {
            this.financialDocumentMonth3LineAmount = financialDocumentMonth3LineAmount;
        }
    }


    /**
     * Gets the financialDocumentMonth4LineAmount attribute.
     * 
     * @return - Returns the financialDocumentMonth4LineAmount
     * 
     */
    public KualiDecimal getFinancialDocumentMonth4LineAmount() {
        return financialDocumentMonth4LineAmount;
    }

    /**
     * Sets the financialDocumentMonth4LineAmount attribute.
     * 
     * @param financialDocumentMonth4LineAmount The financialDocumentMonth4LineAmount to set.
     * 
     */
    public void setFinancialDocumentMonth4LineAmount(KualiDecimal financialDocumentMonth4LineAmount) {
        if (financialDocumentMonth4LineAmount != null) {
            this.financialDocumentMonth4LineAmount = financialDocumentMonth4LineAmount;
        }
    }


    /**
     * Gets the financialDocumentMonth5LineAmount attribute.
     * 
     * @return - Returns the financialDocumentMonth5LineAmount
     * 
     */
    public KualiDecimal getFinancialDocumentMonth5LineAmount() {
        return financialDocumentMonth5LineAmount;
    }

    /**
     * Sets the financialDocumentMonth5LineAmount attribute.
     * 
     * @param financialDocumentMonth5LineAmount The financialDocumentMonth5LineAmount to set.
     * 
     */
    public void setFinancialDocumentMonth5LineAmount(KualiDecimal financialDocumentMonth5LineAmount) {
        if (financialDocumentMonth5LineAmount != null) {
            this.financialDocumentMonth5LineAmount = financialDocumentMonth5LineAmount;
        }
    }


    /**
     * Gets the financialDocumentMonth6LineAmount attribute.
     * 
     * @return - Returns the financialDocumentMonth6LineAmount
     * 
     */
    public KualiDecimal getFinancialDocumentMonth6LineAmount() {
        return financialDocumentMonth6LineAmount;
    }

    /**
     * Sets the financialDocumentMonth6LineAmount attribute.
     * 
     * @param financialDocumentMonth6LineAmount The financialDocumentMonth6LineAmount to set.
     * 
     */
    public void setFinancialDocumentMonth6LineAmount(KualiDecimal financialDocumentMonth6LineAmount) {
        if (financialDocumentMonth6LineAmount != null) {
            this.financialDocumentMonth6LineAmount = financialDocumentMonth6LineAmount;
        }
    }


    /**
     * Gets the financialDocumentMonth7LineAmount attribute.
     * 
     * @return - Returns the financialDocumentMonth7LineAmount
     * 
     */
    public KualiDecimal getFinancialDocumentMonth7LineAmount() {
        return financialDocumentMonth7LineAmount;
    }

    /**
     * Sets the financialDocumentMonth7LineAmount attribute.
     * 
     * @param financialDocumentMonth7LineAmount The financialDocumentMonth7LineAmount to set.
     * 
     */
    public void setFinancialDocumentMonth7LineAmount(KualiDecimal financialDocumentMonth7LineAmount) {
        if (financialDocumentMonth7LineAmount != null) {
            this.financialDocumentMonth7LineAmount = financialDocumentMonth7LineAmount;
        }
    }


    /**
     * Gets the financialDocumentMonth8LineAmount attribute.
     * 
     * @return - Returns the financialDocumentMonth8LineAmount
     * 
     */
    public KualiDecimal getFinancialDocumentMonth8LineAmount() {
        return financialDocumentMonth8LineAmount;
    }

    /**
     * Sets the financialDocumentMonth8LineAmount attribute.
     * 
     * @param financialDocumentMonth8LineAmount The financialDocumentMonth8LineAmount to set.
     * 
     */
    public void setFinancialDocumentMonth8LineAmount(KualiDecimal financialDocumentMonth8LineAmount) {
        if (financialDocumentMonth8LineAmount != null) {
            this.financialDocumentMonth8LineAmount = financialDocumentMonth8LineAmount;
        }
    }


    /**
     * Gets the financialDocumentMonth9LineAmount attribute.
     * 
     * @return - Returns the financialDocumentMonth9LineAmount
     * 
     */
    public KualiDecimal getFinancialDocumentMonth9LineAmount() {
        return financialDocumentMonth9LineAmount;
    }

    /**
     * Sets the financialDocumentMonth9LineAmount attribute.
     * 
     * @param financialDocumentMonth9LineAmount The financialDocumentMonth9LineAmount to set.
     * 
     */
    public void setFinancialDocumentMonth9LineAmount(KualiDecimal financialDocumentMonth9LineAmount) {
        if (financialDocumentMonth9LineAmount != null) {
            this.financialDocumentMonth9LineAmount = financialDocumentMonth9LineAmount;
        }
    }


    /**
     * Gets the financialDocumentMonth10LineAmount attribute.
     * 
     * @return - Returns the financialDocumentMonth10LineAmount
     * 
     */
    public KualiDecimal getFinancialDocumentMonth10LineAmount() {
        return financialDocumentMonth10LineAmount;
    }

    /**
     * Sets the financialDocumentMonth10LineAmount attribute.
     * 
     * @param financialDocumentMonth10LineAmount The financialDocumentMonth10LineAmount to set.
     * 
     */
    public void setFinancialDocumentMonth10LineAmount(KualiDecimal financialDocumentMonth10LineAmount) {
        if (financialDocumentMonth10LineAmount != null) {
            this.financialDocumentMonth10LineAmount = financialDocumentMonth10LineAmount;
        }
    }


    /**
     * Gets the financialDocumentMonth11LineAmount attribute.
     * 
     * @return - Returns the financialDocumentMonth11LineAmount
     * 
     */
    public KualiDecimal getFinancialDocumentMonth11LineAmount() {
        return financialDocumentMonth11LineAmount;
    }

    /**
     * Sets the financialDocumentMonth11LineAmount attribute.
     * 
     * @param financialDocumentMonth11LineAmount The financialDocumentMonth11LineAmount to set.
     * 
     */
    public void setFinancialDocumentMonth11LineAmount(KualiDecimal financialDocumentMonth11LineAmount) {
        if (financialDocumentMonth11LineAmount != null) {
            this.financialDocumentMonth11LineAmount = financialDocumentMonth11LineAmount;
        }
    }


    /**
     * Gets the financialDocumentMonth12LineAmount attribute.
     * 
     * @return - Returns the financialDocumentMonth12LineAmount
     * 
     */
    public KualiDecimal getFinancialDocumentMonth12LineAmount() {
        return financialDocumentMonth12LineAmount;
    }

    /**
     * Sets the financialDocumentMonth12LineAmount attribute.
     * 
     * @param financialDocumentMonth12LineAmount The financialDocumentMonth12LineAmount to set.
     * 
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

}