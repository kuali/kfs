/*
 * Copyright 2006 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kuali.kfs.coa.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * 
 */
public class UniversityBudgetOfficeFunction extends PersistableBusinessObjectBase implements MutableInactivatable {

    private String financialUniversityBudgetOfficeFunctionCode;
    private String financialUniversityBudgetOfficeFunctionName;
    private boolean active;

    /**
     * Default constructor.
     */
    public UniversityBudgetOfficeFunction() {

    }

    /**
     * Gets the financialUniversityBudgetOfficeFunctionCode attribute.
     * 
     * @return Returns the financialUniversityBudgetOfficeFunctionCode
     */
    public String getFinancialUniversityBudgetOfficeFunctionCode() {
        return financialUniversityBudgetOfficeFunctionCode;
    }

    /**
     * Sets the financialUniversityBudgetOfficeFunctionCode attribute.
     * 
     * @param financialUniversityBudgetOfficeFunctionCode The financialUniversityBudgetOfficeFunctionCode to set.
     */
    public void setFinancialUniversityBudgetOfficeFunctionCode(String financialUniversityBudgetOfficeFunctionCode) {
        this.financialUniversityBudgetOfficeFunctionCode = financialUniversityBudgetOfficeFunctionCode;
    }


    /**
     * Gets the financialUniversityBudgetOfficeFunctionName attribute.
     * 
     * @return Returns the financialUniversityBudgetOfficeFunctionName
     */
    public String getFinancialUniversityBudgetOfficeFunctionName() {
        return financialUniversityBudgetOfficeFunctionName;
    }

    /**
     * Sets the financialUniversityBudgetOfficeFunctionName attribute.
     * 
     * @param financialUniversityBudgetOfficeFunctionName The financialUniversityBudgetOfficeFunctionName to set.
     */
    public void setFinancialUniversityBudgetOfficeFunctionName(String financialUniversityBudgetOfficeFunctionName) {
        this.financialUniversityBudgetOfficeFunctionName = financialUniversityBudgetOfficeFunctionName;
    }


    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("financialUniversityBudgetOfficeFunctionCode", this.financialUniversityBudgetOfficeFunctionCode);
        return m;
    }

    /**
     * Gets the active attribute. 
     * @return Returns the active.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute value.
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }
}
