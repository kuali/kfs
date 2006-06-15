/*
 * Copyright (c) 2004, 2005 The National Association of College and University 
 * Business Officers, Cornell University, Trustees of Indiana University, 
 * Michigan State University Board of Trustees, Trustees of San Joaquin Delta 
 * College, University of Hawai'i, The Arizona Board of Regents on behalf of the 
 * University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); 
 * By obtaining, using and/or copying this Original Work, you agree that you 
 * have read, understand, and will comply with the terms and conditions of the 
 * Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,  DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
 */

package org.kuali.module.financial.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class FiscalYearFunctionControl extends BusinessObjectBase {

    private Integer universityFiscalYear;
    private String financialSystemFunctionControlCode;
    private boolean financialSystemFunctionActiveIndicator;

    private FunctionControlCode functionControl;

    /**
     * Default constructor.
     */
    public FiscalYearFunctionControl() {

    }

    /**
     * Gets the universityFiscalYear attribute.
     * 
     * @return - Returns the universityFiscalYear
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
     * @return - Returns the financialSystemFunctionControlCode
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
     * @return - Returns the financialSystemFunctionActiveIndicator
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

}
