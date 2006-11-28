/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source$
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

package org.kuali.module.kra.budget.bo;

import java.util.List;

/**
 * 
 */
public class BudgetThirdPartyCostShare extends BudgetAbstractCostShare {

    private String budgetCostShareSourceName;

    /**
     * Default no-arg constructor.
     */
    public BudgetThirdPartyCostShare() {
        super();
    }

    public BudgetThirdPartyCostShare(BudgetThirdPartyCostShare budgetThirdPartyCostShare) {
        this.documentNumber = budgetThirdPartyCostShare.getDocumentNumber();
        this.budgetCostShareSequenceNumber = budgetThirdPartyCostShare.getBudgetCostShareSequenceNumber();
        this.budgetCostShareDescription = budgetThirdPartyCostShare.getBudgetCostShareDescription();
        this.budgetPeriodCostShare = budgetThirdPartyCostShare.getBudgetPeriodCostShare();
    }
    
    /**
     * Gets the budgetThirdPartyCostShareSourceName attribute.
     * 
     * @return Returns the budgetThirdPartyCostShareSourceName
     * 
     */
    public String getBudgetCostShareSourceName() {
        return budgetCostShareSourceName;
    }

    /**
     * Sets the budgetThirdPartyCostShareSourceName attribute.
     * 
     * @param budgetThirdPartyCostShareSourceName The budgetThirdPartyCostShareSourceName to set.
     * 
     */
    public void setBudgetCostShareSourceName(String budgetThirdPartyCostShareSourceName) {
        this.budgetCostShareSourceName = budgetThirdPartyCostShareSourceName;
    }

    /**
     * Gets the budgetPeriodThirdPartyCostShare attribute.
     * 
     * @return Returns the budgetPeriodThirdPartyCostShare
     * 
     */
    public List<BudgetPeriodThirdPartyCostShare> getBudgetPeriodCostShare() {
        return budgetPeriodCostShare;
    }

    public BudgetPeriodThirdPartyCostShare getBudgetPeriodCostShareItem(int index) {
        while (getBudgetPeriodCostShare().size() <= index) {
            getBudgetPeriodCostShare().add(new BudgetPeriodThirdPartyCostShare());
        }
        return (BudgetPeriodThirdPartyCostShare) getBudgetPeriodCostShare().get(index);
    }

    /**
     * Sets the budgetPeriodThirdPartyCostShare attribute.
     * 
     * @param budgetPeriodThirdPartyCostShare The budgetPeriodThirdPartyCostShare to set.
     * 
     */
    public void setBudgetPeriodCostShare(List budgetPeriodCostShare) {
        this.budgetPeriodCostShare = budgetPeriodCostShare;
    }
}