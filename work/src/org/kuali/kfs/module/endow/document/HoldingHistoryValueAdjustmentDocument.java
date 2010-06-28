/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document;

import org.kuali.kfs.sys.document.Correctable;
import org.kuali.rice.kns.document.Copyable;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.businessobject.SecurityValuationMethod;
import org.kuali.kfs.module.endow.businessobject.MonthEndDate;

/**
 * This is the transactional document that is used to record a modification to the 
 * market value of a record or group of records in the holding history table
 */
public class HoldingHistoryValueAdjustmentDocument extends EndowmentTransactionalDocumentBase implements Copyable, Correctable{

    protected String securityId;
    protected String securityClassCode;
    protected String securityValuationMethod;
    protected long holdingMonthEndDate;
    protected long securityUnitValue;
    protected long securityMarketValue;
    
    protected Security security;
    protected SecurityValuationMethod securityValuation;
    protected MonthEndDate monthEndDate;
    
    public HoldingHistoryValueAdjustmentDocument() {
        super();
    }
    
    /**
     * This method will get securityId
     */
    public String getSecurityId() {
        return securityId;
    }

    /**
     * This method will set securityId
     * @param securityId
     */
    public void setSecurityId(String securityId) {
        this.securityId = securityId;
    }
    
    /**
     * This method will get securityClassCode
     */    
    public String getSecurityClassCode() {
        return securityClassCode;
    }

    /**
     * This method will set securityClassCode
     * @param securityClassCode
     */    
    public void setSecurityClassCode(String securityClassCode) {
        this.securityClassCode = securityClassCode;
    }
    
    /**
     * This method will get securityValuationMethod
     */
    public String getSecurityValuationMethod() {
        return securityValuationMethod;
    }

    /**
     * This method will set securityValuationMethod
     * @param securityValuationMethod
     */
    public void setSecurityValuationMethod(String securityValuationMethod) {
        this.securityValuationMethod = securityValuationMethod;
    }

    /**
     * This method will get holdingMonthEndDate
     */    
    public long getHoldingMonthEndDate() {
        return holdingMonthEndDate;
    }

    /**
     * This method will set holdingMonthEndDate
     * @param holdingMonthEndDate
     */
    public void setHoldingMonthEndDate(long holdingMonthEndDate) {
        this.holdingMonthEndDate = holdingMonthEndDate;
    }

    /**
     * This method will get securityUnitValue
     */    
    public long getSecurityUnitValue() {
        return securityUnitValue;
    }

    /**
     * This method will set securityMarketValue
     * @param securityMarketValue
     */
    public void setSecurityUnitValue(long securityUnitValue) {
        this.securityUnitValue = securityUnitValue;
    }

    /**
     * This method will get securityMarketValue
     */    
    public long getSecurityMarketValue() {
        return securityMarketValue;
    }

    /**
     * This method will set securityMarketValue
     * @param securityMarketValue
     */
    public void setSecurityMarketValue(long securityMarketValue) {
        this.securityMarketValue = securityMarketValue;
    }

    public Security getSecurity() {
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    public SecurityValuationMethod getSecurityValuation() {
        return securityValuation;
    }

    public void setSecurityValuation(SecurityValuationMethod securityValuation) {
        this.securityValuation = securityValuation;
    }

    public MonthEndDate getMonthEndDate() {
        return monthEndDate;
    }

    public void setMonthEndDate(MonthEndDate monthEndDate) {
        this.monthEndDate = monthEndDate;
    }
}
