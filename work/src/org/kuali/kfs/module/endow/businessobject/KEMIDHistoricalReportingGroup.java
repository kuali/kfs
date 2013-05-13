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
import java.util.LinkedHashMap;

import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class KEMIDHistoricalReportingGroup extends PersistableBusinessObjectBase {

    private String kemid;
    private String securityId;
    private String reportingGroupCode;
    private String registrationCode;
    private String ipIndicator;
    private BigDecimal units;
    private BigDecimal carryVal;
    private BigDecimal marketVal;
    private BigDecimal nextFYEstimatedIncome;
    private BigDecimal remainderOfFYEstimatedIncome;
    private BigDecimal annualEstimatedIncome;
    private KualiInteger historyBalanceDateId;

    private KEMID kemidObj;
    private Security security;
    private SecurityReportingGroup reportingGroup;
    private RegistrationCode registration;
    private IncomePrincipalIndicator incomePrincipalIndicator;
    private MonthEndDate historyBalanceDate;

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(EndowPropertyConstants.KEMID, this.kemid);
        return m;
    }

    /**
     * Gets the annualEstimatedIncome
     * 
     * @return annualEstimatedIncome
     */
    public BigDecimal getAnnualEstimatedIncome() {
        return annualEstimatedIncome;
    }

    /**
     * Sets the annualEstimatedIncome.
     * 
     * @param annualEstimatedIncome
     */
    public void setAnnualEstimatedIncome(BigDecimal annualEstimatedIncome) {
        this.annualEstimatedIncome = annualEstimatedIncome;
    }

    /**
     * Gets the carryVal.
     * 
     * @return carryVal
     */
    public BigDecimal getCarryVal() {
        return carryVal;
    }

    /**
     * Sets the carryVal.
     * 
     * @param carryVal
     */
    public void setCarryVal(BigDecimal carryVal) {
        this.carryVal = carryVal;
    }

    /**
     * Gets the incomePrincipalIndicator.
     * 
     * @return
     */
    public IncomePrincipalIndicator getIncomePrincipalIndicator() {
        return incomePrincipalIndicator;
    }

    /**
     * Sets the incomePrincipalIndicator.
     * 
     * @param incomePrincipalIndicator
     */
    public void setIncomePrincipalIndicator(IncomePrincipalIndicator incomePrincipalIndicator) {
        this.incomePrincipalIndicator = incomePrincipalIndicator;
    }

    /**
     * Gets the ipIndicator.
     * 
     * @return ipIndicator
     */
    public String getIpIndicator() {
        return ipIndicator;
    }

    /**
     * Sets the ipIndicator.
     * 
     * @param ipIndicator
     */
    public void setIpIndicator(String ipIndicator) {
        this.ipIndicator = ipIndicator;
    }

    /**
     * Gets the kemid.
     * 
     * @return kemid
     */
    public String getKemid() {
        return kemid;
    }

    /**
     * Sets the kemid.
     * 
     * @param kemid
     */
    public void setKemid(String kemid) {
        this.kemid = kemid;
    }

    /**
     * Gets the kemidObj.
     * 
     * @return kemidObj
     */
    public KEMID getKemidObj() {
        return kemidObj;
    }

    /**
     * Sets the kemidObj.
     * 
     * @param kemidObj
     */
    public void setKemidObj(KEMID kemidObj) {
        this.kemidObj = kemidObj;
    }

    /**
     * Gets the marketVal.
     * 
     * @return marketVal
     */
    public BigDecimal getMarketVal() {
        return marketVal;
    }

    /**
     * Sets the marketVal.
     * 
     * @param marketVal
     */
    public void setMarketVal(BigDecimal marketVal) {
        this.marketVal = marketVal;
    }

    /**
     * Gets the historyBalanceDateId.
     * 
     * @return historyBalanceDateId
     */
    public KualiInteger getHistoryBalanceDateId() {
        return historyBalanceDateId;
    }

    /**
     * Sets the historyBalanceDateId.
     * 
     * @param historyBalanceDateId
     */
    public void setHistoryBalanceDateId(KualiInteger monthEndDateId) {
        this.historyBalanceDateId = monthEndDateId;
    }

    /**
     * Gets the nextFYEstimatedIncome.
     * 
     * @return nextFYEstimatedIncome
     */
    public BigDecimal getNextFYEstimatedIncome() {
        return nextFYEstimatedIncome;
    }

    /**
     * Sets the nextFYEstimatedIncome.
     * 
     * @param nextFYEstimatedIncome
     */
    public void setNextFYEstimatedIncome(BigDecimal nextFYEstimatedIncome) {
        this.nextFYEstimatedIncome = nextFYEstimatedIncome;
    }

    /**
     * Gets the registration.
     * 
     * @return registration
     */
    public RegistrationCode getRegistration() {
        return registration;
    }

    /**
     * Sets the registration.
     * 
     * @param registration
     */
    public void setRegistration(RegistrationCode registration) {
        this.registration = registration;
    }

    /**
     * Gets the registrationCode.
     * 
     * @return registrationCode
     */
    public String getRegistrationCode() {
        return registrationCode;
    }

    /**
     * Sets the registrationCode.
     * 
     * @param registrationCode
     */
    public void setRegistrationCode(String registrationCode) {
        this.registrationCode = registrationCode;
    }

    /**
     * Gets the remainderOfFYEstimatedIncome.
     * 
     * @return remainderOfFYEstimatedIncome
     */
    public BigDecimal getRemainderOfFYEstimatedIncome() {
        return remainderOfFYEstimatedIncome;
    }

    /**
     * Sets the remainderOfFYEstimatedIncome.
     * 
     * @param remainderOfFYEstimatedIncome
     */
    public void setRemainderOfFYEstimatedIncome(BigDecimal remainderOfFYEstimatedIncome) {
        this.remainderOfFYEstimatedIncome = remainderOfFYEstimatedIncome;
    }

    /**
     * Gets the reportingGroup.
     * 
     * @return reportingGroup
     */
    public SecurityReportingGroup getReportingGroup() {
        return reportingGroup;
    }

    /**
     * Sets the reportingGroup.
     * 
     * @param reportingGroup
     */
    public void setReportingGroup(SecurityReportingGroup reportingGroup) {
        this.reportingGroup = reportingGroup;
    }

    /**
     * Gets the reportingGroupCode.
     * 
     * @return reportingGroupCode
     */
    public String getReportingGroupCode() {
        return reportingGroupCode;
    }

    /**
     * Sets the reportingGroupCode.
     * 
     * @param reportingGroupCode
     */
    public void setReportingGroupCode(String reportingGroupCode) {
        this.reportingGroupCode = reportingGroupCode;
    }

    /**
     * Gets the security.
     * 
     * @return security
     */
    public Security getSecurity() {
        return security;
    }

    /**
     * Sets the security.
     * 
     * @param security
     */
    public void setSecurity(Security security) {
        this.security = security;
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
     * Gets the units.
     * 
     * @return units
     */
    public BigDecimal getUnits() {
        return units;
    }

    /**
     * Sets the units.
     * 
     * @param units
     */
    public void setUnits(BigDecimal units) {
        this.units = units;
    }

    /**
     * Gets the historyBalanceDate.
     * 
     * @return historyBalanceDate
     */
    public MonthEndDate getHistoryBalanceDate() {
        return historyBalanceDate;
    }

    /**
     * Sets the historyBalanceDate.
     * 
     * @param historyBalanceDate
     */
    public void setHistoryBalanceDate(MonthEndDate historyBalanceDate) {
        this.historyBalanceDate = historyBalanceDate;
    }
}
