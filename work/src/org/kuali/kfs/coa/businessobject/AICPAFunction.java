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
 * Business object for American Institute of Certified Public Accountants (AICPA) function
 */
public class AICPAFunction extends PersistableBusinessObjectBase implements MutableInactivatable {

    private String financialAicpaFunctionCode;
    private String financialAicpaFunctionName;
    private boolean active;

    /**
     * Default constructor.
     */
    public AICPAFunction() {

    }

    /**
     * Gets the financialAicpaFunctionCode attribute.
     * 
     * @return Returns the financialAicpaFunctionCode
     */
    public String getFinancialAicpaFunctionCode() {
        return financialAicpaFunctionCode;
    }

    /**
     * Sets the financialAicpaFunctionCode attribute.
     * 
     * @param financialAicpaFunctionCode The financialAicpaFunctionCode to set.
     */
    public void setFinancialAicpaFunctionCode(String financialAicpaFunctionCode) {
        this.financialAicpaFunctionCode = financialAicpaFunctionCode;
    }


    /**
     * Gets the financialAicpaFunctionName attribute.
     * 
     * @return Returns the financialAicpaFunctionName
     */
    public String getFinancialAicpaFunctionName() {
        return financialAicpaFunctionName;
    }

    /**
     * Sets the financialAicpaFunctionName attribute.
     * 
     * @param financialAicpaFunctionName The financialAicpaFunctionName to set.
     */
    public void setFinancialAicpaFunctionName(String financialAicpaFunctionName) {
        this.financialAicpaFunctionName = financialAicpaFunctionName;
    }


    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("financialAicpaFunctionCode", this.financialAicpaFunctionCode);
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
