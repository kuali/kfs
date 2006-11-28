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
import org.kuali.module.cg.bo.Agency;

/**
 * 
 */
public class AgencyExtension extends BusinessObjectBase {

    private String agencyNumber;
    private boolean agencyModularIndicator;
    private KualiInteger budgetModularIncrementAmount;
    private KualiInteger budgetPeriodMaximumAmount;
    private boolean agencyNsfOutputIndicator;
    private Agency agency;

    /**
     * Default no-arg constructor.
     */
    public AgencyExtension() {

    }

    /**
     * Gets the agencyModularIndicator attribute.
     * 
     * @return Returns the agencyModularIndicator
     * 
     */
    public boolean isAgencyModularIndicator() {
        return agencyModularIndicator;
    }

    /**
     * Sets the agencyModularIndicator attribute.
     * 
     * @param agencyModularIndicator The agencyModularIndicator to set.
     * 
     */
    public void setAgencyModularIndicator(boolean agencyModularIndicator) {
        this.agencyModularIndicator = agencyModularIndicator;
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

    /**
     * Gets the agencyNsfOutputIndicator attribute.
     * 
     * @return Returns the agencyNsfOutputIndicator
     * 
     */
    public boolean isAgencyNsfOutputIndicator() {
        return agencyNsfOutputIndicator;
    }

    /**
     * Sets the agencyNsfOutputIndicator attribute.
     * 
     * @param agencyNsfOutputIndicator The agencyNsfOutputIndicator to set.
     * 
     */
    public void setAgencyNsfOutputIndicator(boolean agencyNsfOutputIndicator) {
        this.agencyNsfOutputIndicator = agencyNsfOutputIndicator;
    }

    /**
     * Gets the agencyNumber attribute.
     * 
     * @return Returns the agencyNumber
     * 
     */
    public String getAgencyNumber() {
        return agencyNumber;
    }

    /**
     * Sets the agencyNumber attribute.
     * 
     * @param agencyNumber The agencyNumber to set.
     * 
     */
    public void setAgencyNumber(String agencyNumber) {
        this.agencyNumber = agencyNumber;
    }

    /**
     * Gets the agency attribute.
     * 
     * @return Returns the agency
     * 
     */
    public Agency getAgency() {
        return agency;
    }

    /**
     * Sets the agency attribute.
     * 
     * @param agency The agency to set.
     * 
     */
    public void setAgency(Agency agency) {
        this.agency = agency;
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
