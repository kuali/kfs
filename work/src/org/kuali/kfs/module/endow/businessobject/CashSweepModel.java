/*
 * Copyright 2009 The Kuali Foundation.
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
import java.sql.Date;
import java.util.LinkedHashMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class CashSweepModel extends PersistableBusinessObjectBase implements MutableInactivatable {
    
    private Integer cashSweepModelID;
    private String  cashSweepModelName;
    private BigDecimal sweepIncomeCashLimit;
    private String incomeSweepInvestment;
    private String incomeSweepRegistrationCode;    
    private BigDecimal sweepPrincipleCashLimit;
    private String principleSweepInvestment;
    private String principleSweepRegistrationCode;
    private String cashSweepFrequencyCode;
    private Date cashSweepNextDueDate;
    private Date dateOfLastSweepModelChange;    
    private boolean active;
    
    private PooledFundControl incomePooledFundControl;
    private RegistrationCode incomeRegistrationCode;
    private PooledFundControl principlePooledFundControl;
    private RegistrationCode principleRegistrationCode;
    private FrequencyCode cashSweepFrequencyCodeObj;
    
    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(EndowPropertyConstants.CASH_SWEEP_MODEL_ID, this.cashSweepModelID);
        return m;
    }
    
    /**
     * @see org.kuali.rice.core.api.mo.common.active.MutableInactivatable#isActive()
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @see org.kuali.rice.core.api.mo.common.active.MutableInactivatable#setActive(boolean)
     */
    public void setActive(boolean active) {
        this.active = active;
    }
    
    /**
     * Gets the cashSweepModelID
     * 
     * @return cashSweepModelID
     */
    public Integer getCashSweepModelID() {
        return cashSweepModelID;
    }
    /**
     * Sets the cashSweepModelID
     * 
     * @param cashSweepModelID
     */
    public void setCashSweepModelID(Integer cashSweepModelID) {
        this.cashSweepModelID = cashSweepModelID;
    }
    
    /**
     * Gets the cashSweepModelName
     * 
     * @return cashSweepModelName
     */
    public String getCashSweepModelName() {
        return cashSweepModelName;
    }
    /**
     * Sets the cashSweepModelName
     * 
     * @param cashSweepModelName
     */
    public void setCashSweepModelName(String cashSweepModelName) {
        this.cashSweepModelName = cashSweepModelName;
    }
    
    /**
     * Gets the sweepIncomeCashLimit
     * 
     * @return sweepIncomeCashLimit
     */
    public BigDecimal getSweepIncomeCashLimit() {
        return sweepIncomeCashLimit;
    }

    /**
     * Sets the sweepIncomeCashLimit
     * 
     * @param sweepIncomeCashLimit
     */
    public void setSweepIncomeCashLimit(BigDecimal sweepIncomeCashLimit) {
        this.sweepIncomeCashLimit = sweepIncomeCashLimit;
    }
    
    /**
     * Gets the incomeSweepInvestment
     * 
     * @return incomeSweepInvestment
     */
    public String getIncomeSweepInvestment() {
        return incomeSweepInvestment;
    }
    /**
     * Sets the incomeSweepInvestment
     * 
     * @param incomeSweepInvestment
     */
    public void setIncomeSweepInvestment(String incomeSweepInvestment) {
        this.incomeSweepInvestment = incomeSweepInvestment;
    }

    /**
     * Gets the incomeSweepRegistrationCode
     * 
     * @return incomeSweepRegistrationCode
     */
    public String getIncomeSweepRegistrationCode() {
        return incomeSweepRegistrationCode;
    }
    /**
     * Sets the incomeSweepRegistrationCode
     * 
     * @param incomeSweepRegistrationCode
     */
    public void setIncomeSweepRegistrationCode(String incomeSweepRegistrationCode) {
        this.incomeSweepRegistrationCode = incomeSweepRegistrationCode;
    }
    
    /**
     * Gets the sweepPrincipleCashLimit
     * 
     * @return sweepPrincipleCashLimit
     */
    public BigDecimal getSweepPrincipleCashLimit() {
        return sweepPrincipleCashLimit;
    }

    /**
     * Sets the sweepPrincipleCashLimit
     * 
     * @param sweepPrincipleCashLimit
     */
    public void setSweepPrincipleCashLimit(BigDecimal sweepPrincipleCashLimit) {
        this.sweepPrincipleCashLimit = sweepPrincipleCashLimit;
    }
    
    /**
     * Gets the principleSweepInvestment
     * 
     * @return principleSweepInvestment
     */
    public String getPrincipleSweepInvestment() {
        return principleSweepInvestment;
    }
    /**
     * Sets the principleSweepInvestment
     * 
     * @param principleSweepInvestment
     */
    public void setPrincipleSweepInvestment(String principleSweepInvestment) {
        this.principleSweepInvestment = principleSweepInvestment;
    }

    /**
     * Gets the principleSweepRegistrationCode
     * 
     * @return principleSweepRegistrationCode
     */
    public String getPrincipleSweepRegistrationCode() {
        return principleSweepRegistrationCode;
    }
    /**
     * Sets the principleSweepRegistrationCode
     * 
     * @param principleSweepRegistrationCode
     */
    public void setPrincipleSweepRegistrationCode(String principleSweepRegistrationCode) {
        this.principleSweepRegistrationCode = principleSweepRegistrationCode;
    }
 
    /**
     * Gets the cashSweepNextDueDate
     * 
     * @return cashSweepNextDueDate
     */
    public Date getCashSweepNextDueDate() {
        return cashSweepNextDueDate;
    }
    /**
     * Sets the cashSweepNextDueDate
     * 
     * @param cashSweepNextDueDate
     */
    public void setCashSweepNextDueDate(Date cashSweepNextDueDate) {
        this.cashSweepNextDueDate = cashSweepNextDueDate;
    }
    
    /**
     * Gets the dateOfLastSweepModelChange
     * 
     * @return dateOfLastSweepModelChange
     */
    public Date getDateOfLastSweepModelChange() {
        return dateOfLastSweepModelChange;
    }
    /**
     * Sets the dateOfLastSweepModelChange
     * 
     * @param dateOfLastSweepModelChange
     */
    public void setDateOfLastSweepModelChange(Date dateOfLastSweepModelChange) {
        this.dateOfLastSweepModelChange = dateOfLastSweepModelChange;
    }

    /**
     * Gets the incomePooledFundControl
     * 
     * @return incomePooledFundControl
     */
    public PooledFundControl getIncomePooledFundControl() {
        return incomePooledFundControl;
    }
    /**
     * Sets the incomePooledFundControl
     * 
     * @param incomePooledFundControl
     */
    public void setIncomePooledFundControl(PooledFundControl incomePooledFundControl) {
        this.incomePooledFundControl = incomePooledFundControl;
    }

    /**
     * Gets the principlePooledFundControl
     * 
     * @return principlePooledFundControl
     */
    public PooledFundControl getPrinciplePooledFundControl() {
        return principlePooledFundControl;
    }
    /**
     * Sets the principlePooledFundControl
     * 
     * @param principlePooledFundControl
     */
    public void setPrinciplePooledFundControl(PooledFundControl principlePooledFundControl) {
        this.principlePooledFundControl = principlePooledFundControl;
    }

    /**
     * Gets the incomeRegistrationCode
     * 
     * @return incomeRegistrationCode
     */
    public RegistrationCode getIncomeRegistrationCode() {
        return incomeRegistrationCode;
    }
    /**
     * Sets the incomeRegistrationCode
     * 
     * @param incomeRegistrationCode
     */
    public void setIncomeRegistrationCode(RegistrationCode incomeRegistrationCode) {
        this.incomeRegistrationCode = incomeRegistrationCode;
    }

    /**
     * Gets the principleRegistrationCode
     * 
     * @return principleRegistrationCode
     */
    public RegistrationCode getPrincipleRegistrationCode() {
        return principleRegistrationCode;
    }
    /**
     * Sets the principleRegistrationCode
     * 
     * @param principleRegistrationCode
     */
    public void setPrincipleRegistrationCode(RegistrationCode principleRegistrationCode) {
        this.principleRegistrationCode = principleRegistrationCode;
    }    
 
    /**
     * Gets the cashSweepFrequencyCode Object
     * 
     * @return cashSweepFrequencyCodeObj
     */
    public FrequencyCode getCashSweepFrequencyCodeObj() {
        return cashSweepFrequencyCodeObj;
    }
    /**
     * Sets the cashSweepFrequencyCode Object
     * 
     * @param cashSweepFrequencyCodeObj
     */
    public void setCashSweepFrequencyCodeObj(FrequencyCode cashSweepFrequencyCodeObj) {
        this.cashSweepFrequencyCodeObj = cashSweepFrequencyCodeObj;
    }  
    
    /**
     * Gets the cashSweepFrequencyCode 
     * 
     * @return cashSweepFrequencyCode
     */
    public String getCashSweepFrequencyCode() {
        return cashSweepFrequencyCode;
    }
    /**
     * Sets the cashSweepFrequencyCode 
     * 
     * @param cashSweepFrequencyCode
     */
    public void setCashSweepFrequencyCode(String cashSweepFrequencyCode) {
        this.cashSweepFrequencyCode = cashSweepFrequencyCode;
    } 

    /**
     * @return Returns the code and description in format: xx - xxxxxxxxxxxxxxxx
     */    
    public String getCodeAndDescription() {
        if (StringUtils.isEmpty(this.cashSweepModelID.toString())) {
            return KFSConstants.EMPTY_STRING;
        }
        String theString = this.getCashSweepModelID().toString() + " - " + this.getCashSweepModelName();
        
        return theString;
    }
}
