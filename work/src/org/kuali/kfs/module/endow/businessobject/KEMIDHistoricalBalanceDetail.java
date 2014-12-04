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

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Business Object for KEMID Historical Balance Detail View.
 */
public class KEMIDHistoricalBalanceDetail extends PersistableBusinessObjectBase {

    private String kemid;

    private KualiInteger historyBalanceDateId;
    private String incomePrincipalIndicator;
    private String reportingGroupCode;
    private BigDecimal valueAtMarket;
    private BigDecimal annualEstimatedIncome; // next 12 months estimated income
    private BigDecimal remainderOfFYEstimatedIncome;
    private BigDecimal nextFYEstimatedIncome;

    private KEMID kemidObj;
    private MonthEndDate historyBalanceDate;
    private SecurityReportingGroup reportingGroup;
    private IncomePrincipalIndicator ipIndicator;

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(EndowPropertyConstants.KEMID, this.kemid);
        return m;
    }

    /**
     * Gets the incomeAtMarket. If icome principal indicator is 'I' then the valueAtMarket represents the incomeAtMarket. Otherwise
     * the incomeAtMarket will be zero.
     * 
     * @return incomeAtMarket
     */
    public BigDecimal getIncomeAtMarket() {
        BigDecimal incomeAtMarket = BigDecimal.ZERO;
        if (EndowConstants.IncomePrincipalIndicator.INCOME.equalsIgnoreCase(incomePrincipalIndicator)) {
            incomeAtMarket = valueAtMarket;
        }
        return incomeAtMarket;
    }

    /**
     * Gets the principalAtMarket. If icome principal indicator is 'P' then the valueAtMarket represents the principalAtMarket.
     * Otherwise the principalAtMarket will be zero.
     * 
     * @return
     */
    public BigDecimal getPrincipalAtMarket() {
        BigDecimal principalAtMarket = BigDecimal.ZERO;
        if (EndowConstants.IncomePrincipalIndicator.PRINCIPAL.equalsIgnoreCase(incomePrincipalIndicator)) {
            principalAtMarket = valueAtMarket;
        }
        return principalAtMarket;
    }

    /**
     * Gets the annualEstimatedIncome.
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
    public void setHistoryBalanceDateId(KualiInteger historyBalanceDateId) {
        this.historyBalanceDateId = historyBalanceDateId;
    }

    /**
     * Gets the incomePrincipalIndicator.
     * 
     * @return incomePrincipalIndicator
     */
    public String getIncomePrincipalIndicator() {
        return incomePrincipalIndicator;
    }

    /**
     * Sets the incomePrincipalIndicator.
     * 
     * @param incomePrincipalIndicator
     */
    public void setIncomePrincipalIndicator(String incomePrincipalIndicator) {
        this.incomePrincipalIndicator = incomePrincipalIndicator;
    }

    /**
     * Gets the ipIndicator.
     * 
     * @return ipIndicator
     */
    public IncomePrincipalIndicator getIpIndicator() {
        return ipIndicator;
    }

    /**
     * Sets the ipIndicator.
     * 
     * @param ipIndicator
     */
    public void setIpIndicator(IncomePrincipalIndicator ipIndicator) {
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
     * Gets the valueAtMarket.
     * 
     * @return valueAtMarket
     */
    public BigDecimal getValueAtMarket() {
        return valueAtMarket;
    }

    /**
     * Sets the valueAtMarket.
     * 
     * @param valueAtMarket
     */
    public void setValueAtMarket(BigDecimal valueAtMarket) {
        this.valueAtMarket = valueAtMarket;
    }


}
