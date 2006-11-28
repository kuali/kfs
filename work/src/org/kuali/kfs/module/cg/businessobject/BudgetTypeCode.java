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

/**
 * 
 */
public class BudgetTypeCode extends BusinessObjectBase {

    private String budgetTypeCode;
    private String budgetTypeDescription;

    /**
     * Default no-arg constructor.
     */
    public BudgetTypeCode() {

    }

    /**
     * Gets the budgetTypeCode attribute.
     * 
     * @return Returns the budgetTypeCode
     * 
     */
    public String getBudgetTypeCode() {
        return budgetTypeCode;
    }

    /**
     * Sets the budgetTypeCode attribute.
     * 
     * @param budgetTypeCode The budgetTypeCode to set.
     * 
     */
    public void setBudgetTypeCode(String budgetTypeCode) {
        this.budgetTypeCode = budgetTypeCode;
    }

    /**
     * Gets the budgetTypeDescription attribute.
     * 
     * @return Returns the budgetTypeDescription
     * 
     */
    public String getBudgetTypeDescription() {
        return budgetTypeDescription;
    }

    /**
     * Sets the budgetTypeDescription attribute.
     * 
     * @param budgetTypeDescription The budgetTypeDescription to set.
     * 
     */
    public void setBudgetTypeDescription(String budgetTypeDescription) {
        this.budgetTypeDescription = budgetTypeDescription;
    }

    /**
     * @see org.kuali.bo.BusinessObjectType#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        // m.put("<unique identifier 1>", this.<UniqueIdentifier1>());
        // m.put("<unique identifier 2>", this.<UniqueIdentifier2>());

        return m;
    }
}
