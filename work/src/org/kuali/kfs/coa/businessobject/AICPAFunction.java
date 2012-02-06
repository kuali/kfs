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
