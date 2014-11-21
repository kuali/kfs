/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.fp.businessobject;

import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiInteger;

/**
 * This class represents a budget adjustment accounting line
 */
public interface BudgetAdjustmentAccountingLine extends AccountingLine {

    public abstract KualiDecimal getMonthlyLinesTotal();

    public abstract KualiInteger getBaseBudgetAdjustmentAmount();

    public abstract void setBaseBudgetAdjustmentAmount(KualiInteger baseBudgetAdjustmentAmount);

    public abstract String getBudgetAdjustmentPeriodCode();

    public abstract void setBudgetAdjustmentPeriodCode(String budgetAdjustmentPeriodCode);

    public abstract KualiDecimal getCurrentBudgetAdjustmentAmount();

    public abstract void setCurrentBudgetAdjustmentAmount(KualiDecimal currentBudgetAdjustmentAmount);

    /**
     * Gets the financialDocumentMonth1LineAmount attribute.
     * 
     * @return Returns the financialDocumentMonth1LineAmount
     */
    public abstract KualiDecimal getFinancialDocumentMonth1LineAmount();

    /**
     * Sets the financialDocumentMonth1LineAmount attribute.
     * 
     * @param financialDocumentMonth1LineAmount The financialDocumentMonth1LineAmount to set.
     */
    public abstract void setFinancialDocumentMonth1LineAmount(KualiDecimal financialDocumentMonth1LineAmount);

    /**
     * Gets the financialDocumentMonth2LineAmount attribute.
     * 
     * @return Returns the financialDocumentMonth2LineAmount
     */
    public abstract KualiDecimal getFinancialDocumentMonth2LineAmount();

    /**
     * Sets the financialDocumentMonth2LineAmount attribute.
     * 
     * @param financialDocumentMonth2LineAmount The financialDocumentMonth2LineAmount to set.
     */
    public abstract void setFinancialDocumentMonth2LineAmount(KualiDecimal financialDocumentMonth2LineAmount);

    /**
     * Gets the financialDocumentMonth3LineAmount attribute.
     * 
     * @return Returns the financialDocumentMonth3LineAmount
     */
    public abstract KualiDecimal getFinancialDocumentMonth3LineAmount();

    /**
     * Sets the financialDocumentMonth3LineAmount attribute.
     * 
     * @param financialDocumentMonth3LineAmount The financialDocumentMonth3LineAmount to set.
     */
    public abstract void setFinancialDocumentMonth3LineAmount(KualiDecimal financialDocumentMonth3LineAmount);

    /**
     * Gets the financialDocumentMonth4LineAmount attribute.
     * 
     * @return Returns the financialDocumentMonth4LineAmount
     */
    public abstract KualiDecimal getFinancialDocumentMonth4LineAmount();

    /**
     * Sets the financialDocumentMonth4LineAmount attribute.
     * 
     * @param financialDocumentMonth4LineAmount The financialDocumentMonth4LineAmount to set.
     */
    public abstract void setFinancialDocumentMonth4LineAmount(KualiDecimal financialDocumentMonth4LineAmount);

    /**
     * Gets the financialDocumentMonth5LineAmount attribute.
     * 
     * @return Returns the financialDocumentMonth5LineAmount
     */
    public abstract KualiDecimal getFinancialDocumentMonth5LineAmount();

    /**
     * Sets the financialDocumentMonth5LineAmount attribute.
     * 
     * @param financialDocumentMonth5LineAmount The financialDocumentMonth5LineAmount to set.
     */
    public abstract void setFinancialDocumentMonth5LineAmount(KualiDecimal financialDocumentMonth5LineAmount);

    /**
     * Gets the financialDocumentMonth6LineAmount attribute.
     * 
     * @return Returns the financialDocumentMonth6LineAmount
     */
    public abstract KualiDecimal getFinancialDocumentMonth6LineAmount();

    /**
     * Sets the financialDocumentMonth6LineAmount attribute.
     * 
     * @param financialDocumentMonth6LineAmount The financialDocumentMonth6LineAmount to set.
     */
    public abstract void setFinancialDocumentMonth6LineAmount(KualiDecimal financialDocumentMonth6LineAmount);

    /**
     * Gets the financialDocumentMonth7LineAmount attribute.
     * 
     * @return Returns the financialDocumentMonth7LineAmount
     */
    public abstract KualiDecimal getFinancialDocumentMonth7LineAmount();

    /**
     * Sets the financialDocumentMonth7LineAmount attribute.
     * 
     * @param financialDocumentMonth7LineAmount The financialDocumentMonth7LineAmount to set.
     */
    public abstract void setFinancialDocumentMonth7LineAmount(KualiDecimal financialDocumentMonth7LineAmount);

    /**
     * Gets the financialDocumentMonth8LineAmount attribute.
     * 
     * @return Returns the financialDocumentMonth8LineAmount
     */
    public abstract KualiDecimal getFinancialDocumentMonth8LineAmount();

    /**
     * Sets the financialDocumentMonth8LineAmount attribute.
     * 
     * @param financialDocumentMonth8LineAmount The financialDocumentMonth8LineAmount to set.
     */
    public abstract void setFinancialDocumentMonth8LineAmount(KualiDecimal financialDocumentMonth8LineAmount);

    /**
     * Gets the financialDocumentMonth9LineAmount attribute.
     * 
     * @return Returns the financialDocumentMonth9LineAmount
     */
    public abstract KualiDecimal getFinancialDocumentMonth9LineAmount();

    /**
     * Sets the financialDocumentMonth9LineAmount attribute.
     * 
     * @param financialDocumentMonth9LineAmount The financialDocumentMonth9LineAmount to set.
     */
    public abstract void setFinancialDocumentMonth9LineAmount(KualiDecimal financialDocumentMonth9LineAmount);

    /**
     * Gets the financialDocumentMonth10LineAmount attribute.
     * 
     * @return Returns the financialDocumentMonth10LineAmount
     */
    public abstract KualiDecimal getFinancialDocumentMonth10LineAmount();

    /**
     * Sets the financialDocumentMonth10LineAmount attribute.
     * 
     * @param financialDocumentMonth10LineAmount The financialDocumentMonth10LineAmount to set.
     */
    public abstract void setFinancialDocumentMonth10LineAmount(KualiDecimal financialDocumentMonth10LineAmount);

    /**
     * Gets the financialDocumentMonth11LineAmount attribute.
     * 
     * @return Returns the financialDocumentMonth11LineAmount
     */
    public abstract KualiDecimal getFinancialDocumentMonth11LineAmount();

    /**
     * Sets the financialDocumentMonth11LineAmount attribute.
     * 
     * @param financialDocumentMonth11LineAmount The financialDocumentMonth11LineAmount to set.
     */
    public abstract void setFinancialDocumentMonth11LineAmount(KualiDecimal financialDocumentMonth11LineAmount);

    /**
     * Gets the financialDocumentMonth12LineAmount attribute.
     * 
     * @return Returns the financialDocumentMonth12LineAmount
     */
    public abstract KualiDecimal getFinancialDocumentMonth12LineAmount();

    /**
     * Sets the financialDocumentMonth12LineAmount attribute.
     * 
     * @param financialDocumentMonth12LineAmount The financialDocumentMonth12LineAmount to set.
     */
    public abstract void setFinancialDocumentMonth12LineAmount(KualiDecimal financialDocumentMonth12LineAmount);

    /**
     * Gets the fringeBenefitIndicator attribute.
     * 
     * @return Returns the fringeBenefitIndicator
     */
    public abstract boolean isFringeBenefitIndicator();

    /**
     * Sets the fringeBenefitIndicator attribute.
     * 
     * @param fringeBenefitIndicator The fringeBenefitIndicator to set.
     */
    public abstract void setFringeBenefitIndicator(boolean fringeBenefitIndicator);

    /**
     * Clears financialDocumentMonth1LineAmounts through 1inancialDocumentMonth12LineAmounts.
     */
    public abstract void clearFinancialDocumentMonthLineAmounts();
}
