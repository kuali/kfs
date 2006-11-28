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
import org.kuali.core.util.KualiDecimal;

/**
 * 
 */
public class IndirectCostLookup extends BusinessObjectBase {

    private boolean budgetOnCampusIndicator;
    private String budgetPurposeCode;
    private KualiDecimal budgetIndirectCostRate;
    private BudgetPurposeCode budgetPurpose;

    /**
     * Default no-arg constructor.
     */
    public IndirectCostLookup() {

    }
    
    public IndirectCostLookup(boolean budgetOnCampusIndicator, String budgetPurposeCode) {
        this();
        this.budgetOnCampusIndicator = budgetOnCampusIndicator;
        this.budgetPurposeCode = budgetPurposeCode;
    }

    /**
     * Gets the budgetOnCampusIndicator attribute.
     * 
     * @return Returns the budgetOnCampusIndicator
     * 
     */
    public boolean getBudgetOnCampusIndicator() {
        return budgetOnCampusIndicator;
    }

    /**
     * Sets the budgetOnCampusIndicator attribute.
     * 
     * @param budgetOnCampusIndicator The budgetOnCampusIndicator to set.
     * 
     */
    public void setBudgetOnCampusIndicator(boolean budgetOnCampusIndicator) {
        this.budgetOnCampusIndicator = budgetOnCampusIndicator;
    }

    /**
     * Gets the budgetPurposeCode attribute.
     * 
     * @return Returns the budgetPurposeCode
     * 
     */
    public String getBudgetPurposeCode() {
        return budgetPurposeCode;
    }

    /**
     * Sets the budgetPurposeCode attribute.
     * 
     * @param budgetPurposeCode The budgetPurposeCode to set.
     * 
     */
    public void setBudgetPurposeCode(String budgetPurposeCode) {
        this.budgetPurposeCode = budgetPurposeCode;
    }

    /**
     * Gets the budgetIndirectCostRate attribute.
     * 
     * @return Returns the budgetIndirectCostRate
     * 
     */
    public KualiDecimal getBudgetIndirectCostRate() {
        return budgetIndirectCostRate;
    }

    /**
     * Sets the budgetIndirectCostRate attribute.
     * 
     * @param budgetIndirectCostRate The budgetIndirectCostRate to set.
     * 
     */
    public void setBudgetIndirectCostRate(KualiDecimal budgetIndirectCostRate) {
        this.budgetIndirectCostRate = budgetIndirectCostRate;
    }
    
    /**
     * Gets the budgetPurpose attribute. 
     * @return Returns the budgetPurpose.
     */
    public BudgetPurposeCode getBudgetPurpose() {
        return budgetPurpose;
    }

    /**
     * Sets the budgetPurpose attribute value.
     * @param budgetPurpose The budgetPurpose to set.
     */
    public void setBudgetPurpose(BudgetPurposeCode budgetPurpose) {
        this.budgetPurpose = budgetPurpose;
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
