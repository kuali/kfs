/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.module.endow.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.krad.bo.TransientBusinessObjectBase;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public class FeeProcessingTotalsProcessedGrandTotalLine extends TransientBusinessObjectBase {
    private String feeMethodCode;
    private String eDocNumber;
    private int linesGenerated = 0;
    private KualiDecimal totalIncomeAmount = KualiDecimal.ZERO;
    private KualiDecimal totalPrincipalAmount = KualiDecimal.ZERO;
    
    public FeeProcessingTotalsProcessedGrandTotalLine() {
        feeMethodCode = "Grand Totals";
    }
    
    /**
     * Gets the feeMethodCode attribute. 
     * @return Returns the feeMethodCode.
     */
    public String getFeeMethodCode() {
        return feeMethodCode;
    }


    /**
     * Sets the feeMethodCode attribute value.
     * @param feeMethodCode The feeMethodCode to set.
     */
    public void setFeeMethodCode(String feeMethodCode) {
        this.feeMethodCode = feeMethodCode;
    }

    /**
     * Gets the eDocNumber attribute. 
     * @return Returns the eDocNumber.
     */
    public String getEDocNumber() {
        return eDocNumber;
    }

    /**
     * Sets the eDocNumber attribute value.
     * @param eDocNumber The eDocNumber to set.
     */
    public void setEDocNumber(String eDocNumber) {
        this.eDocNumber = eDocNumber;
    }

    /**
     * Gets the linesGenerated attribute. 
     * @return Returns the linesGenerated.
     */
    public int getLinesGenerated() {
        return linesGenerated;
    }

    /**
     * Sets the linesGenerated attribute value.
     * @param linesGenerated The linesGenerated to set.
     */
    public void setLinesGenerated(int linesGenerated) {
        this.linesGenerated = linesGenerated;
    }

    /**
     * Gets the totalIncomeAmount attribute. 
     * @return Returns the totalIncomeAmount.
     */
    public KualiDecimal getTotalIncomeAmount() {
        return totalIncomeAmount;
    }

    /**
     * Sets the totalIncomeAmount attribute value.
     * @param totalIncomeAmount The totalIncomeAmount to set.
     */
    public void setTotalIncomeAmount(KualiDecimal totalIncomeAmount) {
        this.totalIncomeAmount = totalIncomeAmount;
    }

    /**
     * Gets the totalPrincipalAmount attribute. 
     * @return Returns the totalPrincipalAmount.
     */
    public KualiDecimal getTotalPrincipalAmount() {
        return totalPrincipalAmount;
    }

    /**
     * Sets the totalPrincipalAmount attribute value.
     * @param totalPrincipalAmount The totalPrincipalAmount to set.
     */
    public void setTotalPrincipalAmount(KualiDecimal totalPrincipalAmount) {
        this.totalPrincipalAmount = totalPrincipalAmount;
    }

    /**
     * A map of the "keys" of this transient business object
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap pks = new LinkedHashMap<String, Object>();
        pks.put("feeMethodLabel",this.getFeeMethodCode());
        pks.put("eDocNumber",this.getEDocNumber());
        pks.put("linesGeneraged", this.getLinesGenerated());
        pks.put("totalIncomeAmount",this.getTotalIncomeAmount());
        pks.put("totalPrincipalAmount",this.getTotalPrincipalAmount());
        
        return pks;
    }
}
