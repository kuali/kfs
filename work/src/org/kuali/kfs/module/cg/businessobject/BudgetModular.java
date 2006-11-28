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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.core.util.KualiInteger;

/**
 * 
 */
public class BudgetModular extends BusinessObjectBase {

    // Stored values - Budget Modular table
    private String documentNumber;
    private KualiInteger budgetModularIncrementAmount;
    private Integer budgetModularTaskNumber;
    private String budgetModularConsortiumDescription;
    private String budgetModularPersonnelDescription;
    private String budgetModularVariableAdjustmentDescription;
    private List budgetModularPeriods;

    // Stored values - Agency table
    private KualiInteger budgetPeriodMaximumAmount;

    // Derived values
    private KualiInteger budgetModularDirectCostAmount;
    private KualiInteger totalActualDirectCostAmount;
    private KualiInteger totalModularDirectCostAmount;
    private KualiInteger totalAdjustedModularDirectCostAmount;
    private KualiInteger totalDirectCostAmount;
    private KualiInteger totalConsortiumAmount;
    private List increments;
    private boolean invalidMode;

    /**
     * Default no-arg constructor.
     */
    public BudgetModular() {
        super();
        this.budgetModularPeriods = new ArrayList();
        this.increments = new ArrayList();
    }

    public BudgetModular(String documentNumber) {
        this();
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber
     * 
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute.
     * 
     * @param documentNumber The documentNumber to set.
     * 
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the budgetModularIncrementAmount attribute.
     * 
     * @return Returns the budgetModularIncrementAmount
     * 
     */
    public KualiInteger getBudgetModularIncrementAmount() {
        return budgetModularIncrementAmount;
    }

    /**
     * Sets the budgetModularIncrementAmount attribute.
     * 
     * @param budgetModularIncrementAmount The budgetModularIncrementAmount to set.
     * 
     */
    public void setBudgetModularIncrementAmount(KualiInteger budgetModularIncrementAmount) {
        this.budgetModularIncrementAmount = budgetModularIncrementAmount;
    }

    /**
     * Gets the budgetModularTaskNumber attribute.
     * 
     * @return Returns the budgetModularTaskNumber
     * 
     */
    public Integer getBudgetModularTaskNumber() {
        return budgetModularTaskNumber;
    }

    /**
     * Sets the budgetModularTaskNumber attribute.
     * 
     * @param budgetModularTaskNumber The budgetModularTaskNumber to set.
     * 
     */
    public void setBudgetModularTaskNumber(Integer budgetModularTaskNumber) {
        this.budgetModularTaskNumber = budgetModularTaskNumber;
    }

    /**
     * Gets the budgetModularConsortiumDescription attribute.
     * 
     * @return Returns the budgetModularConsortiumDescription
     * 
     */
    public String getBudgetModularConsortiumDescription() {
        return budgetModularConsortiumDescription;
    }

    /**
     * Sets the budgetModularConsortiumDescription attribute.
     * 
     * @param budgetModularConsortiumDescription The budgetModularConsortiumDescription to set.
     * 
     */
    public void setBudgetModularConsortiumDescription(String budgetModularConsortiumDescription) {
        this.budgetModularConsortiumDescription = budgetModularConsortiumDescription;
    }

    /**
     * Gets the budgetModularPersonnelDescription attribute.
     * 
     * @return Returns the budgetModularPersonnelDescription
     * 
     */
    public String getBudgetModularPersonnelDescription() {
        return budgetModularPersonnelDescription;
    }

    /**
     * Sets the budgetModularPersonnelDescription attribute.
     * 
     * @param budgetModularPersonnelDescription The budgetModularPersonnelDescription to set.
     * 
     */
    public void setBudgetModularPersonnelDescription(String budgetModularPersonnelDescription) {
        this.budgetModularPersonnelDescription = budgetModularPersonnelDescription;
    }

    /**
     * Gets the budgetModularVariableAdjustmentDescription attribute.
     * 
     * @return Returns the budgetModularVariableAdjustmentDescription
     * 
     */
    public String getBudgetModularVariableAdjustmentDescription() {
        return budgetModularVariableAdjustmentDescription;
    }

    /**
     * Sets the budgetModularVariableAdjustmentDescription attribute.
     * 
     * @param budgetModularVariableAdjustmentDescription The budgetModularVariableAdjustmentDescription to set.
     * 
     */
    public void setBudgetModularVariableAdjustmentDescription(String budgetModularVariableAdjustmentDescription) {
        this.budgetModularVariableAdjustmentDescription = budgetModularVariableAdjustmentDescription;
    }

    /**
     * Gets the budgetPeriodMaximumAmount attribute.
     * 
     * @return Returns the budgetPeriodMaximumAmount
     * 
     */
    public KualiInteger getBudgetPeriodMaximumAmount() {
        return budgetPeriodMaximumAmount;
    }

    /**
     * Sets the budgetPeriodMaximumAmount attribute.
     * 
     * @param budgetPeriodMaximumAmount The budgetPeriodMaximumAmount to set.
     * 
     */
    public void setBudgetPeriodMaximumAmount(KualiInteger budgetPeriodMaximumAmount) {
        this.budgetPeriodMaximumAmount = budgetPeriodMaximumAmount;
    }

    public List getBudgetModularPeriods() {
        return budgetModularPeriods;
    }

    public void setBudgetModularPeriods(List budgetModularPeriods) {
        this.budgetModularPeriods = budgetModularPeriods;
    }

    public KualiInteger getBudgetModularDirectCostAmount() {
        return budgetModularDirectCostAmount;
    }

    public void setBudgetModularDirectCostAmount(KualiInteger budgetModularDirectCostAmount) {
        this.budgetModularDirectCostAmount = budgetModularDirectCostAmount;
    }

    public List getIncrements() {
        return increments;
    }

    public void setIncrements(List increments) {
        this.increments = increments;
    }

    public String getIncrementsString() {
        StringBuffer sb = new StringBuffer();
        for (Iterator iter = getIncrements().iterator(); iter.hasNext();) {
            sb.append((String) iter.next());
            if (iter.hasNext()) {
                sb.append("-");
            }
        }
        return sb.toString();
    }

    public void setIncrementsString(String strIncrements) {
        setIncrements(Arrays.asList(strIncrements.split("-")));
    }

    /**
     * Retrieve a particular modularPeriod at a given index in the list of modularPeriods.
     * 
     * @param index
     * @return
     */
    public BudgetModularPeriod getBudgetModularPeriod(int index) {
        while (getBudgetModularPeriods().size() <= index) {
            getBudgetModularPeriods().add(new BudgetModularPeriod());
        }
        return (BudgetModularPeriod) getBudgetModularPeriods().get(index);
    }

    public KualiInteger getTotalActualDirectCostAmount() {
        return totalActualDirectCostAmount;
    }

    public KualiInteger getTotalModularDirectCostAmount() {
        return totalModularDirectCostAmount;
    }

    public KualiInteger getTotalAdjustedModularDirectCostAmount() {
        return totalAdjustedModularDirectCostAmount;
    }

    public KualiInteger getTotalDirectCostAmount() {
        return totalDirectCostAmount;
    }

    public void setTotalActualDirectCostAmount(KualiInteger totalActualDirectCostAmount) {
        this.totalActualDirectCostAmount = totalActualDirectCostAmount;
    }

    public void setTotalAdjustedModularDirectCostAmount(KualiInteger totalAdjustedModularDirectCostAmount) {
        this.totalAdjustedModularDirectCostAmount = totalAdjustedModularDirectCostAmount;
    }

    public void setTotalDirectCostAmount(KualiInteger totalDirectCostAmount) {
        this.totalDirectCostAmount = totalDirectCostAmount;
    }

    public void setTotalModularDirectCostAmount(KualiInteger totalModularDirectCostAmount) {
        this.totalModularDirectCostAmount = totalModularDirectCostAmount;
    }

    public KualiInteger getTotalConsortiumAmount() {
        return totalConsortiumAmount;
    }

    public void setTotalConsortiumAmount(KualiInteger totalConsortiumAmount) {
        this.totalConsortiumAmount = totalConsortiumAmount;
    }

    public KualiInteger getTotalModularVarianceAmount() {
        if (this.getTotalAdjustedModularDirectCostAmount() != null && this.getTotalActualDirectCostAmount() != null) {
            return this.getTotalAdjustedModularDirectCostAmount().subtract(this.getTotalActualDirectCostAmount());
        }
        return new KualiInteger(0);
    }

    public boolean isInvalidMode() {
        return invalidMode;
    }

    public void setInvalidMode(boolean invalidMode) {
        this.invalidMode = invalidMode;
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
