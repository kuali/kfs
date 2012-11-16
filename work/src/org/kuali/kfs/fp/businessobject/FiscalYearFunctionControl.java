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

package org.kuali.kfs.fp.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.sys.businessobject.FiscalYearBasedBusinessObject;
import org.kuali.kfs.sys.businessobject.SystemOptions;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * This class is used to represent a fiscal year function control business object.
 */
public class FiscalYearFunctionControl extends PersistableBusinessObjectBase implements FiscalYearBasedBusinessObject {

    private Integer universityFiscalYear;
    private String financialSystemFunctionControlCode;
    private boolean financialSystemFunctionActiveIndicator;

    private FunctionControlCode functionControl;
    private SystemOptions universityFiscal;

    /**
     * Default constructor.
     */
    public FiscalYearFunctionControl() {

    }

    /**
     * Gets the universityFiscalYear attribute.
     * 
     * @return Returns the universityFiscalYear
     */
    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    /**
     * Sets the universityFiscalYear attribute.
     * 
     * @param universityFiscalYear The universityFiscalYear to set.
     */
    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
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
     * Gets the financialSystemFunctionActiveIndicator attribute.
     * 
     * @return Returns the financialSystemFunctionActiveIndicator
     */
    public boolean isFinancialSystemFunctionActiveIndicator() {
        return financialSystemFunctionActiveIndicator;
    }


    /**
     * Sets the financialSystemFunctionActiveIndicator attribute.
     * 
     * @param financialSystemFunctionActiveIndicator The financialSystemFunctionActiveIndicator to set.
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
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        if (this.universityFiscalYear != null) {
            m.put("universityFiscalYear", this.universityFiscalYear.toString());
        }
        m.put("financialSystemFunctionControlCode", this.financialSystemFunctionControlCode);
        return m;
    }

    /**
     * Gets the universityFiscal attribute.
     * 
     * @return Returns the universityFiscal.
     */
    public SystemOptions getUniversityFiscal() {
        return universityFiscal;
    }

    /**
     * Sets the universityFiscal attribute value.
     * 
     * @param universityFiscal The universityFiscal to set.
     */
    public void setUniversityFiscal(SystemOptions universityFiscal) {
        this.universityFiscal = universityFiscal;
    }

}
