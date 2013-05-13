/*
 * Copyright 2010 The Kuali Foundation.
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

import java.math.BigDecimal;
import java.util.LinkedHashMap;

import org.kuali.rice.krad.bo.TransientBusinessObjectBase;

public class AccrualsProcessingTotalReportLine extends TransientBusinessObjectBase {

    protected String accrualMethod;
    protected String securityId;
    protected int recordsUpdated;
    protected BigDecimal totalAccrualAmount = BigDecimal.ZERO;

    /**
     * Constructs a AccrualsProcessingTotalReportLine.java.
     * 
     * @param accrualMethod
     * @param securityId
     */
    public AccrualsProcessingTotalReportLine(String securityId, String accrualMethod) {
        this.securityId = securityId;
        this.accrualMethod = accrualMethod;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Gets the accrualMethod.
     * 
     * @return accrualMethod
     */
    public String getAccrualMethod() {
        return accrualMethod;
    }

    /**
     * Sets the accrualMethod.
     * 
     * @param accrualMethod
     */
    public void setAccrualMethod(String accrualMethod) {
        this.accrualMethod = accrualMethod;
    }

    /**
     * Gets the securityId.
     * 
     * @return securityId
     */
    public String getSecurityId() {
        return securityId;
    }

    /**
     * Sets the securityId.
     * 
     * @param securityId
     */
    public void setSecurityId(String securityId) {
        this.securityId = securityId;
    }

    /**
     * Gets the totalAccrualAmount
     * 
     * @return
     */
    public BigDecimal getTotalAccrualAmount() {
        return totalAccrualAmount;
    }

    /**
     * This method...
     * 
     * @param totalAccrualAmount
     */
    public void setTotalAccrualAmount(BigDecimal totalAccrualAmount) {
        this.totalAccrualAmount = totalAccrualAmount;
    }

    /**
     * Gets the recordsUpdated.
     * 
     * @return recordsUpdated
     */
    public int getRecordsUpdated() {
        return recordsUpdated;
    }

    /**
     * Sets the recordsUpdated.
     * 
     * @param recordsUpdated
     */
    public void setRecordsUpdated(int recordsUpdated) {
        this.recordsUpdated = recordsUpdated;
    }

    /**
     * Adds given accrual amount to the total accrual amount for reporting.
     * 
     * @param accrualAmount
     */
    public void addAccrualAmount(BigDecimal accrualAmount) {
        this.totalAccrualAmount = this.totalAccrualAmount.add(accrualAmount);
        this.recordsUpdated++;
    }

}
