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

package org.kuali.kfs.fp.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * This class is used to represent a function control code business object.
 */
public class FunctionControlCode extends PersistableBusinessObjectBase {

    private String financialSystemFunctionControlCode;
    private boolean financialSystemFunctionDefaultIndicator;
    private String financialSystemFunctionDescription;

    /**
     * Default constructor.
     */
    public FunctionControlCode() {

    }

    /**
     * Gets the financialSystemFunctionControlCode attribute.
     * 
     * @return Returns the financialSystemFunctionControlCode
     */
    public String getFinancialSystemFunctionControlCode() {
        return financialSystemFunctionControlCode;
    }

    /**
     * Sets the financialSystemFunctionControlCode attribute.
     * 
     * @param financialSystemFunctionControlCode The financialSystemFunctionControlCode to set.
     */
    public void setFinancialSystemFunctionControlCode(String financialSystemFunctionControlCode) {
        this.financialSystemFunctionControlCode = financialSystemFunctionControlCode;
    }


    /**
     * Gets the financialSystemFunctionDefaultIndicator attribute.
     * 
     * @return Returns the financialSystemFunctionDefaultIndicator
     */
    public boolean isFinancialSystemFunctionDefaultIndicator() {
        return financialSystemFunctionDefaultIndicator;
    }


    /**
     * Sets the financialSystemFunctionDefaultIndicator attribute.
     * 
     * @param financialSystemFunctionDefaultIndicator The financialSystemFunctionDefaultIndicator to set.
     */
    public void setFinancialSystemFunctionDefaultIndicator(boolean financialSystemFunctionDefaultIndicator) {
        this.financialSystemFunctionDefaultIndicator = financialSystemFunctionDefaultIndicator;
    }


    /**
     * Gets the financialSystemFunctionDescription attribute.
     * 
     * @return Returns the financialSystemFunctionDescription
     */
    public String getFinancialSystemFunctionDescription() {
        return financialSystemFunctionDescription;
    }

    /**
     * Sets the financialSystemFunctionDescription attribute.
     * 
     * @param financialSystemFunctionDescription The financialSystemFunctionDescription to set.
     */
    public void setFinancialSystemFunctionDescription(String financialSystemFunctionDescription) {
        this.financialSystemFunctionDescription = financialSystemFunctionDescription;
    }


    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("financialSystemFunctionControlCode", this.financialSystemFunctionControlCode);
        return m;
    }
}
