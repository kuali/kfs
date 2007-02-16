/*
 * Copyright 2006-2007 The Kuali Foundation.
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

package org.kuali.module.financial.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.kfs.bo.Options;

/**
 * 
 */
public class FiscalYearFunctionControl extends PersistableBusinessObjectBase {

    private Integer universityFiscalYear;
    private String financialSystemFunctionControlCode;
    private boolean financialSystemFunctionActiveIndicator;

    private FunctionControlCode functionControl;
    private Options universityFiscal;

    /**
     * Default constructor.
     */
    public FiscalYearFunctionControl() {

    }

    /**
     * Gets the universityFiscalYear attribute.
     * 
     * @return Returns the universityFiscalYear
     * 
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    /**
     * Sets the universityFiscalYear attribute.
     * 
     * @param universityFiscalYear The universityFiscalYear to set.
     * 
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }


    /**
     * Gets the financialSystemFunctionControlCode attribute.
     * 
     * @return Returns the financialSystemFunctionControlCode
     * 
     */
    public String getFinancialSystemFunctionControlCode() {
        return financialSystemFunctionControlCode;
    }

    /**
     * Sets the financialSystemFunctionControlCode attribute.
     * 
     * @param financialSystemFunctionControlCode The financialSystemFunctionControlCode to set.
     * 
     */
    public void setFinancialSystemFunctionControlCode(String financialSystemFunctionControlCode) {
        this.financialSystemFunctionControlCode = financialSystemFunctionControlCode;
    }


    /**
     * Gets the financialSystemFunctionActiveIndicator attribute.
     * 
     * @return Returns the financialSystemFunctionActiveIndicator
     * 
     */
    public boolean isFinancialSystemFunctionActiveIndicator() {
        return financialSystemFunctionActiveIndicator;
    }


    /**
     * Sets the financialSystemFunctionActiveIndicator attribute.
     * 
     * @param financialSystemFunctionActiveIndicator The financialSystemFunctionActiveIndicator to set.
     * 
     */
    public void setFinancialSystemFunctionActiveIndicator(boolean financialSystemFunctionActiveIndicator) {
        this.financialSystemFunctionActiveIndicator = financialSystemFunctionActiveIndicator;
    }


    /**
     * @return Returns the functionControl.
     */
    public FunctionControlCode getFunctionControl() {
        return functionControl;
    }

    /**
     * @param functionControl The functionControl to set.
     * @deprecated
     */
    public void setFunctionControl(FunctionControlCode functionControl) {
        this.functionControl = functionControl;
    }

    /**
     * Implementing equals since I need contains to behave reasonably.
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        boolean equal = false;

        if (obj != null) {
            if (this.getClass().equals(obj.getClass())) {
                FiscalYearFunctionControl fiscalYearFunctionControl = (FiscalYearFunctionControl) obj;

                if (this.getUniversityFiscalYear().equals(fiscalYearFunctionControl.getUniversityFiscalYear())) {
                    equal = true;
                }
            }
        }

        return equal;
    }
    
    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        if (this.universityFiscalYear != null) {
            m.put("universityFiscalYear", this.universityFiscalYear.toString());
        }
        m.put("financialSystemFunctionControlCode", this.financialSystemFunctionControlCode);
        return m;
    }

    /**
     * Gets the universityFiscal attribute. 
     * @return Returns the universityFiscal.
     */
    public Options getUniversityFiscal() {
        return universityFiscal;
    }

    /**
     * Sets the universityFiscal attribute value.
     * @param universityFiscal The universityFiscal to set.
     */
    public void setUniversityFiscal(Options universityFiscal) {
        this.universityFiscal = universityFiscal;
    }

}
