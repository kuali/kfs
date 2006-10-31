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

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.core.util.KualiInteger;

/**
 * 
 */
public class BudgetModularPeriod extends BusinessObjectBase {

    // Stored values
    private String researchDocumentNumber;
    private Integer budgetPeriodSequenceNumber;
    private KualiInteger budgetAdjustedModularDirectCostAmount;

    // Derived values
    private KualiInteger actualDirectCostAmount;
    private KualiInteger consortiumAmount;
    private KualiInteger totalPeriodDirectCostAmount;

    /**
     * Default no-arg constructor.
     */
    public BudgetModularPeriod() {
    }

    public BudgetModularPeriod(String researchDocumentNumber, Integer budgetPeriodSequenceNumber) {
        this.researchDocumentNumber = researchDocumentNumber;
        this.budgetPeriodSequenceNumber = budgetPeriodSequenceNumber;
    }

    /**
     * Gets the researchDocumentNumber attribute.
     * 
     * @return - Returns the researchDocumentNumber
     * 
     */
    public String getResearchDocumentNumber() {
        return researchDocumentNumber;
    }

    /**
     * Sets the researchDocumentNumber attribute.
     * 
     * @param researchDocumentNumber The researchDocumentNumber to set.
     * 
     */
    public void setResearchDocumentNumber(String researchDocumentNumber) {
        this.researchDocumentNumber = researchDocumentNumber;
    }

    /**
     * Gets the budgetPeriodSequenceNumber attribute.
     * 
     * @return - Returns the budgetPeriodSequenceNumber
     * 
     */
    public Integer getBudgetPeriodSequenceNumber() {
        return budgetPeriodSequenceNumber;
    }

    /**
     * Sets the budgetPeriodSequenceNumber attribute.
     * 
     * @param budgetPeriodSequenceNumber The budgetPeriodSequenceNumber to set.
     * 
     */
    public void setBudgetPeriodSequenceNumber(Integer budgetPeriodSequenceNumber) {
        this.budgetPeriodSequenceNumber = budgetPeriodSequenceNumber;
    }

    /**
     * Gets the budgetAdjustedModularDirectCostAmount attribute.
     * 
     * @return - Returns the budgetAdjustedModularDirectCostAmount
     * 
     */
    public KualiInteger getBudgetAdjustedModularDirectCostAmount() {
        return budgetAdjustedModularDirectCostAmount;
    }

    /**
     * Sets the budgetAdjustedModularDirectCostAmount attribute.
     * 
     * @param budgetAdjustedModularDirectCostAmount The budgetAdjustedModularDirectCostAmount to set.
     * 
     */
    public void setBudgetAdjustedModularDirectCostAmount(KualiInteger budgetAdjustedModularDirectCostAmount) {
        this.budgetAdjustedModularDirectCostAmount = budgetAdjustedModularDirectCostAmount;
    }

    public void setActualDirectCostAmount(KualiInteger actualDirectCostAmount) {
        this.actualDirectCostAmount = actualDirectCostAmount;
    }

    public KualiInteger getActualDirectCostAmount() {
        return this.actualDirectCostAmount;
    }

    public KualiInteger getConsortiumAmount() {
        return consortiumAmount;
    }

    public void setConsortiumAmount(KualiInteger consortiumAmount) {
        this.consortiumAmount = consortiumAmount;
    }

    public KualiInteger getTotalPeriodDirectCostAmount() {
        return totalPeriodDirectCostAmount;
    }

    public void setTotalPeriodDirectCostAmount(KualiInteger totalPeriodDirectCostAmount) {
        this.totalPeriodDirectCostAmount = totalPeriodDirectCostAmount;
    }

    public KualiInteger getModularVarianceAmount() {
        if (this.getBudgetAdjustedModularDirectCostAmount() != null && this.getActualDirectCostAmount() != null) {
            return this.getBudgetAdjustedModularDirectCostAmount().subtract(this.getActualDirectCostAmount());
        }
        return new KualiInteger(0);
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        // m.put("<unique identifier 1>", this.<UniqueIdentifier1>());
        // m.put("<unique identifier 2>", this.<UniqueIdentifier2>());

        return m;
    }
}
