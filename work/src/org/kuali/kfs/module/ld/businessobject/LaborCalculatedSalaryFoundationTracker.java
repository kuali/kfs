/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.labor.bo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.budget.bo.CalculatedSalaryFoundationTracker;

public class LaborCalculatedSalaryFoundationTracker extends CalculatedSalaryFoundationTracker {

    private KualiDecimal july1BudgetAmount;
    private BigDecimal july1BudgetFteQuantity;
    private BigDecimal july1BudgetTimePercent;

    /**
     * Constructs a LaborCalculatedSalaryFoundationTracker.java.
     */
    public LaborCalculatedSalaryFoundationTracker() {
        super();
        this.setJuly1BudgetAmount(KualiDecimal.ZERO);
        this.setJuly1BudgetFteQuantity(BigDecimal.ZERO);
        this.setJuly1BudgetTimePercent(BigDecimal.ZERO);
    }

    /**
     * Gets the july1BudgetAmount attribute.
     * 
     * @return Returns the july1BudgetAmount.
     */
    public KualiDecimal getJuly1BudgetAmount() {
        return july1BudgetAmount;
    }

    /**
     * Sets the july1BudgetAmount attribute value.
     * 
     * @param july1BudgetAmount The july1BudgetAmount to set.
     */
    public void setJuly1BudgetAmount(KualiDecimal july1BudgetAmount) {
        this.july1BudgetAmount = july1BudgetAmount;
    }

    /**
     * Gets the july1BudgetFteQuantity attribute.
     * 
     * @return Returns the july1BudgetFteQuantity.
     */
    public BigDecimal getJuly1BudgetFteQuantity() {
        return july1BudgetFteQuantity;
    }

    /**
     * Sets the july1BudgetFteQuantity attribute value.
     * 
     * @param july1BudgetFteQuantity The july1BudgetFteQuantity to set.
     */
    public void setJuly1BudgetFteQuantity(BigDecimal july1BudgetFteQuantity) {
        this.july1BudgetFteQuantity = july1BudgetFteQuantity;
    }

    /**
     * Gets the july1BudgetTimePercent attribute.
     * 
     * @return Returns the july1BudgetTimePercent.
     */
    public BigDecimal getJuly1BudgetTimePercent() {
        return july1BudgetTimePercent;
    }

    /**
     * Sets the july1BudgetTimePercent attribute value.
     * 
     * @param july1BudgetTimePercent The july1BudgetTimePercent to set.
     */
    public void setJuly1BudgetTimePercent(BigDecimal july1BudgetTimePercent) {
        this.july1BudgetTimePercent = july1BudgetTimePercent;
    }
    
    /**
     * construct the key list of the business object
     * 
     * @return the key list of the business object
     */
    public List<String> getKeyFieldList() {
        List<String> keyFieldList = new ArrayList<String>();
        keyFieldList.add(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        keyFieldList.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        keyFieldList.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        keyFieldList.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        keyFieldList.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        keyFieldList.add(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
        keyFieldList.add(KFSPropertyConstants.POSITION_NUMBER);
        keyFieldList.add(KFSPropertyConstants.EMPLID);
        return keyFieldList;
    }
}
