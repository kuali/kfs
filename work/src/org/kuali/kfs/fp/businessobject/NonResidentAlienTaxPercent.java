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
import org.kuali.core.util.KualiDecimal;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class NonResidentAlienTaxPercent extends BusinessObjectBase {

    private String incomeClassCode;
    private String incomeTaxTypeCode;
    private KualiDecimal incomeTaxPercent;

    private TaxIncomeClassCode incomeClass;

    /**
     * Default no-arg constructor.
     */
    public NonResidentAlienTaxPercent() {

    }

    /**
     * Gets the incomeClassCode attribute.
     * 
     * @return - Returns the incomeClassCode
     * 
     */
    public String getIncomeClassCode() {
        return incomeClassCode;
    }


    /**
     * Sets the incomeClassCode attribute.
     * 
     * @param incomeClassCode The incomeClassCode to set.
     * 
     */
    public void setIncomeClassCode(String incomeClassCode) {
        this.incomeClassCode = incomeClassCode;
    }

    /**
     * Gets the incomeTaxTypeCode attribute.
     * 
     * @return - Returns the incomeTaxTypeCode
     * 
     */
    public String getIncomeTaxTypeCode() {
        return incomeTaxTypeCode;
    }


    /**
     * Sets the incomeTaxTypeCode attribute.
     * 
     * @param incomeTaxTypeCode The incomeTaxTypeCode to set.
     * 
     */
    public void setIncomeTaxTypeCode(String incomeTaxTypeCode) {
        this.incomeTaxTypeCode = incomeTaxTypeCode;
    }

    /**
     * Gets the incomeTaxPercent attribute.
     * 
     * @return - Returns the incomeTaxPercent
     * 
     */
    public KualiDecimal getIncomeTaxPercent() {
        return incomeTaxPercent;
    }


    /**
     * Sets the incomeTaxPercent attribute.
     * 
     * @param incomeTaxPercent The incomeTaxPercent to set.
     * 
     */
    public void setIncomeTaxPercent(KualiDecimal incomeTaxPercent) {
        this.incomeTaxPercent = incomeTaxPercent;
    }

    /**
     * Gets the incomeClass attribute.
     * 
     * @return - Returns the incomeClass
     * 
     */
    public TaxIncomeClassCode getIncomeClass() {
        return incomeClass;
    }


    /**
     * Sets the incomeClass attribute.
     * 
     * @param incomeClass The incomeClass to set.
     * @deprecated
     */
    public void setIncomeClass(TaxIncomeClassCode incomeClass) {
        this.incomeClass = incomeClass;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("incomeClassCode", this.incomeClassCode);
        m.put("incomeTaxTypeCode", this.incomeTaxTypeCode);
        if (this.incomeTaxPercent != null) {
            m.put("incomeTaxPercent", this.incomeTaxPercent.toString());
        }
        return m;
    }
}
