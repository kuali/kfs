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
public class FunctionControlCode extends BusinessObjectBase {

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
     * Gets the financialSystemFunctionDefaultIndicator attribute.
     * 
     * @return - Returns the financialSystemFunctionDefaultIndicator
     * 
     */
    public boolean isFinancialSystemFunctionDefaultIndicator() {
        return financialSystemFunctionDefaultIndicator;
    }


    /**
     * Sets the financialSystemFunctionDefaultIndicator attribute.
     * 
     * @param financialSystemFunctionDefaultIndicator The financialSystemFunctionDefaultIndicator to set.
     * 
     */
    public void setFinancialSystemFunctionDefaultIndicator(boolean financialSystemFunctionDefaultIndicator) {
        this.financialSystemFunctionDefaultIndicator = financialSystemFunctionDefaultIndicator;
    }


    /**
     * Gets the financialSystemFunctionDescription attribute.
     * 
     * @return - Returns the financialSystemFunctionDescription
     * 
     */
    public String getFinancialSystemFunctionDescription() {
        return financialSystemFunctionDescription;
    }

    /**
     * Sets the financialSystemFunctionDescription attribute.
     * 
     * @param financialSystemFunctionDescription The financialSystemFunctionDescription to set.
     * 
     */
    public void setFinancialSystemFunctionDescription(String financialSystemFunctionDescription) {
        this.financialSystemFunctionDescription = financialSystemFunctionDescription;
    }


    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("financialSystemFunctionControlCode", this.financialSystemFunctionControlCode);
        return m;
    }
}
