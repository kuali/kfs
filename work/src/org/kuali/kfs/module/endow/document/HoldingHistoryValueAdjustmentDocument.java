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

import java.math.BigDecimal;
import org.kuali.rice.kns.util.KualiInteger;

import org.kuali.kfs.sys.document.Correctable;
import org.kuali.rice.kns.document.Copyable;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.businessobject.SecurityValuationMethod;
import org.kuali.kfs.module.endow.businessobject.MonthEndDate;

/**
 * This is the transactional document that is used to record a modification to the 
 * market value of a record or group of records in the holding history table
 */
public class HoldingHistoryValueAdjustmentDocument extends EndowmentTransactionalDocumentBase implements Copyable {

    protected String securityId;
    protected KualiInteger holdingMonthEndDate;
    protected BigDecimal securityUnitValue;
    protected BigDecimal securityMarketValue;
    
    protected Security security;
    protected SecurityValuationMethod securityValuation;
    protected MonthEndDate monthEndDate;
    
    public HoldingHistoryValueAdjustmentDocument() {
        super();
    }
    
    /**
     * This method will get securityId
     * @return securityId
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
     * This method will get holdingMonthEndDate
     * @return holdingMonthEndDate
     */    
    public KualiInteger getHoldingMonthEndDate() {
        return holdingMonthEndDate;
    }

    /**
     * This method will set holdingMonthEndDate
     * @param holdingMonthEndDate
     */
    public void setHoldingMonthEndDate(KualiInteger holdingMonthEndDate) {
        this.holdingMonthEndDate = holdingMonthEndDate;
    }

    /**
     * This method will get securityUnitValue
     * @return securityUnitValue
     */    
    public BigDecimal getSecurityUnitValue() {
        return securityUnitValue;
    }

    /**
     * This method will set securityUnitValue
     * @param securityUnitValue
     */
    public void setSecurityUnitValue(BigDecimal securityUnitValue) {
        this.securityUnitValue = securityUnitValue;
    }

    /**
     * This method will get securityMarketValue
     * @return securityMarketValue
     */    
    public BigDecimal getSecurityMarketValue() {
        return securityMarketValue;
    }

    /**
     * This method will set securityMarketValue
     * @param securityMarketValue
     */
    public void setSecurityMarketValue(BigDecimal securityMarketValue) {
        this.securityMarketValue = securityMarketValue;
    }

    /**
     * This method will get security
     * @return security
     */    
    public Security getSecurity() {
        return security;
    }

    /**
     * This method will set security
     * @param security
     */    
    public void setSecurity(Security security) {
        this.security = security;
    }

    /**
     * This method will get securityValuation
     * @return securityValuation
     */    
    public SecurityValuationMethod getSecurityValuation() {
        return securityValuation;
    }

    /**
     * This method will set securityValuation
     * @param securityValuation
     */    
    public void setSecurityValuation(SecurityValuationMethod securityValuation) {
        this.securityValuation = securityValuation;
    }

    /**
     * This method will get monthEndDate
     * @return monthEndDate
     */    
    public MonthEndDate getMonthEndDate() {
        return monthEndDate;
    }

    /**
     * This method will set monthEndDate
     * @param monthEndDate
     */
    public void setMonthEndDate(MonthEndDate monthEndDate) {
        this.monthEndDate = monthEndDate;
    }
}
