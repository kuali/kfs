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
