/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/fp/businessobject/NonResidentAlienTaxPercent.java,v $
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

import org.kuali.core.bo.BusinessObjectBase;
import org.kuali.core.util.KualiDecimal;

/**
 * 
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
     * @return Returns the incomeClassCode
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
     * @return Returns the incomeTaxTypeCode
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
     * @return Returns the incomeTaxPercent
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
     * @return Returns the incomeClass
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
