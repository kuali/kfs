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
import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Business Object for Pooled Fund Value table.
 */
public class PooledFundValue extends PersistableBusinessObjectBase {

    private String pooledSecurityID;
    private Date valueEffectiveDate;
    private Date valuationDate;
    private BigDecimal unitValue;
    private BigDecimal incomeDistributionPerUnit;
    private Date distributeIncomeOnDate;
    private boolean incomeDistributionComplete;
    private BigDecimal longTermGainLossDistributionPerUnit;
    private Date distributeLongTermGainLossOnDate;
    private boolean longTermGainLossDistributionComplete;
    private BigDecimal shortTermGainLossDistributionPerUnit;
    private Date distributeShortTermGainLossOnDate;
    private boolean shortTermGainLossDistributionComplete;
    
    private PooledFundControl pooledFundControl;

    public PooledFundValue(){
        super();
        incomeDistributionComplete = false;
        longTermGainLossDistributionComplete = false;
        shortTermGainLossDistributionComplete = false;
    }
    
    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(EndowPropertyConstants.POOL_SECURITY_ID, this.pooledSecurityID);
        m.put(EndowPropertyConstants.VALUE_EFFECTIVE_DATE, this.valueEffectiveDate);        
        return m;
    }
    
    /**
     * Gets the pooledSecurityID
     * 
     * @return pooledSecurityID
     */
    public String getPooledSecurityID() {
        return pooledSecurityID;
    }
    /**
     * Sets the pooledSecurityID
     * 
     * @param pooledSecurityID
     */
    public void setPooledSecurityID(String pooledSecurityID) {
        this.pooledSecurityID = pooledSecurityID;
    }
    
    
    /**
     * Gets the valueEffectiveDate
     * 
     * @return valueEffectiveDate
     */
    public Date getValueEffectiveDate() {
        return valueEffectiveDate;
    }

    /**
     * Sets the valueEffectiveDate
     * 
     * @param valueEffectiveDate
     */
    public void setValueEffectiveDate(Date valueEffectiveDate) {
        this.valueEffectiveDate = valueEffectiveDate;
    }
    
    /**
     * Gets the valuationDate
     * 
     * @return valuationDate
     */
    public Date getValuationDate() {
        return valuationDate;
    }

    /**
     * Sets the valuationDate
     * 
     * @param valuationDate
     */
    public void setValuationDate(Date valuationDate) {
        this.valuationDate = valuationDate;
    }
    
    /**
     * Gets the unitValue
     * 
     * @return unitValue
     */
    public BigDecimal getUnitValue() {
        return unitValue;
    }

    /**
     * Sets the unitValue
     * 
     * @param unitValue
     */
    public void setUnitValue(BigDecimal unitValue) {

        if (unitValue != null) {
            this.unitValue = unitValue.setScale(EndowConstants.Scale.SECURITY_UNIT_VALUE, BigDecimal.ROUND_HALF_UP);
        }
        else {
            this.unitValue = unitValue;
        }
    }
    
    /**
     * Gets the incomeDistributionPerUnit
     * 
     * @return incomeDistributionPerUnit
     */
    public BigDecimal getIncomeDistributionPerUnit() {
        return incomeDistributionPerUnit;
    }

    /**
     * Sets the incomeDistributionPerUnit
     * 
     * @param incomeDistributionPerUnit
     */
    public void setIncomeDistributionPerUnit(BigDecimal incomeDistributionPerUnit) {
        if (ObjectUtils.isNotNull(incomeDistributionPerUnit)){
            this.incomeDistributionPerUnit = incomeDistributionPerUnit;
        }
        else
            this.incomeDistributionPerUnit = new BigDecimal(0);
    }
    
    /**
     * Gets the distributeIncomeOnDate
     * 
     * @return distributeIncomeOnDate
     */
    public Date getDistributeIncomeOnDate() {
        return distributeIncomeOnDate;
    }

    /**
     * Sets the distributeIncomeOnDate
     * 
     * @param distributeIncomeOnDate
     */
    public void setDistributeIncomeOnDate(Date distributeIncomeOnDate) {
        this.distributeIncomeOnDate = distributeIncomeOnDate;
    }
    
    /**
     * Gets the incomeDistributionComplete
     * 
     * @return incomeDistributionComplete
     */
    public boolean isIncomeDistributionComplete() {
        return incomeDistributionComplete;
    }

    /**
     * Sets the incomeDistributionComplete
     * 
     * @param incomeDistributionComplete
     */
    public void setIncomeDistributionComplete(boolean incomeDistributionComplete) {
        this.incomeDistributionComplete = incomeDistributionComplete;
    } 
    
    /**
     * Gets the longTermGainLossDistributionPerUnit
     * 
     * @return longTermGainLossDistributionPerUnit
     */
    public BigDecimal getLongTermGainLossDistributionPerUnit() {
        return longTermGainLossDistributionPerUnit;
    }

    /**
     * Sets the longTermGainLossDistributionPerUnit
     * 
     * @param longTermGainLossDistributionPerUnit
     */
    public void setLongTermGainLossDistributionPerUnit(BigDecimal longTermGainLossDistributionPerUnit) {
        if (ObjectUtils.isNotNull(longTermGainLossDistributionPerUnit)){
            this.longTermGainLossDistributionPerUnit = longTermGainLossDistributionPerUnit;
        }
        else {
            this.longTermGainLossDistributionPerUnit = new BigDecimal (0);
        }
    }
    
    /**
     * Gets the distributeLongTermGainLossOnDate
     * 
     * @return distributeLongTermGainLossOnDate
     */
    public Date getDistributeLongTermGainLossOnDate() {
        return distributeLongTermGainLossOnDate;
    }

    /**
     * Sets the distributeLongTermGainLossOnDate
     * 
     * @param distributeLongTermGainLossOnDate
     */
    public void setDistributeLongTermGainLossOnDate(Date distributeLongTermGainLossOnDate) {
        this.distributeLongTermGainLossOnDate = distributeLongTermGainLossOnDate;
    }
    
    /**
     * Gets the longTermGainLossDistributionComplete
     * 
     * @return longTermGainLossDistributionComplete
     */
    public boolean isLongTermGainLossDistributionComplete() {
        return longTermGainLossDistributionComplete;
    }

    /**
     * Sets the longTermGainLossDistributionComplete
     * 
     * @param longTermGainLossDistributionComplete
     */
    public void setLongTermGainLossDistributionComplete(boolean longTermGainLossDistributionComplete) {
        this.longTermGainLossDistributionComplete = longTermGainLossDistributionComplete;
    } 
    
    /**
     * Gets the shortTermGainLossDistributionPerUnit
     * 
     * @return shortTermGainLossDistributionPerUnit
     */
    public BigDecimal getShortTermGainLossDistributionPerUnit() {
        return shortTermGainLossDistributionPerUnit;
    }

    /**
     * Sets the shortTermGainLossDistributionPerUnit
     * 
     * @param shortTermGainLossDistributionPerUnit
     */
    public void setShortTermGainLossDistributionPerUnit(BigDecimal shortTermGainLossDistributionPerUnit) {
        if (ObjectUtils.isNotNull(shortTermGainLossDistributionPerUnit)) {
            this.shortTermGainLossDistributionPerUnit = shortTermGainLossDistributionPerUnit;
        }
        else {
            this.shortTermGainLossDistributionPerUnit = new BigDecimal(0);
        }
    }
    
    /**
     * Gets the distributeShortTermGainLossOnDate
     * 
     * @return distributeShortTermGainLossOnDate
     */
    public Date getDistributeShortTermGainLossOnDate() {
        return distributeShortTermGainLossOnDate;
    }

    /**
     * Sets the distributeShortTermGainLossOnDate
     * 
     * @param distributeLongTermGainLossOnDate
     */
    public void setDistributeShortTermGainLossOnDate(Date distributeShortTermGainLossOnDate) {
        this.distributeShortTermGainLossOnDate = distributeShortTermGainLossOnDate;
    }
    
    /**
     * Gets the shortTermGainLossDistributionComplete
     * 
     * @return shortTermGainLossDistributionComplete
     */
    public boolean isShortTermGainLossDistributionComplete() {
        return shortTermGainLossDistributionComplete;
    }

    /**
     * Sets the shortTermGainLossDistributionComplete
     * 
     * @param shortTermGainLossDistributionComplete
     */
    public void setShortTermGainLossDistributionComplete(boolean shortTermGainLossDistributionComplete) {
        this.shortTermGainLossDistributionComplete = shortTermGainLossDistributionComplete;
    } 
    
    /**
     * Gets the PooledFundControl object
     * 
     * @return pooledFundControl
     */
    public PooledFundControl getPooledFundControl() {
        return pooledFundControl;
    }
    /**
     * Sets the PooledFundControl object
     * 
     * @param pooledFundControl
     */
    public void setPooledFundControl(PooledFundControl pooledFundControl) {
        this.pooledFundControl = pooledFundControl;
    }    
    
    
}
