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

import org.kuali.core.bo.SourceAccountingLine;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.KualiInteger;


/**
 * Special case <code>{@link SourceAccountingLine}</code> type for
 * <code>{@link org.kuali.module.financial.document.BudgetAdjustmentDocument}</code>
 * 
 * @author Kuali Financial Transactions Team (kualidev@oncourse.iu.edu)
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

    /**
     * This constructor needs to initialize the ojbConcreteClass attribute such that it sets it to its class name. This is how OJB
     * knows what grouping of objects to work with.
     */
    public BudgetAdjustmentSourceAccountingLine() {
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

}