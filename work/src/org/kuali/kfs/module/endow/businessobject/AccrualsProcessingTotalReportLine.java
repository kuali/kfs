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
