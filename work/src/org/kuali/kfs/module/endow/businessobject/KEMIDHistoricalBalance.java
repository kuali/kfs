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

import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class KEMIDHistoricalBalance extends PersistableBusinessObjectBase {

    private String kemid;
    private KualiInteger historyBalanceDateId;
    private BigDecimal incomeAtMarket;
    private BigDecimal principalAtMarket;
    private BigDecimal totalMarketValue;
    private BigDecimal annualEstimatedIncome; // next 12 months estimated income
    private BigDecimal remainderOfFYEstimatedIncome;
    private BigDecimal nextFYEstimatedIncome;

    private KEMID kemidObj;
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
     * Gets the incomeAtMarket.
     * 
     * @return incomeAtMarket
     */
    public BigDecimal getIncomeAtMarket() {
        return incomeAtMarket;
    }

    /**
     * Sets the incomeAtMarket.
     * 
     * @param incomeAtMarket
     */
    public void setIncomeAtMarket(BigDecimal incomeAtMarket) {
        this.incomeAtMarket = incomeAtMarket;
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
     * Gets the principalAtMarket.
     * 
     * @return principalAtMarket
     */
    public BigDecimal getPrincipalAtMarket() {
        return principalAtMarket;
    }

    /**
     * Sets the principalAtMarket.
     * 
     * @param principalAtMarket
     */
    public void setPrincipalAtMarket(BigDecimal principalAtMarket) {
        this.principalAtMarket = principalAtMarket;
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
    public void setRemainderOfFYEstimatedIncome(BigDecimal remainderFYEstimatedIncome) {
        this.remainderOfFYEstimatedIncome = remainderFYEstimatedIncome;
    }

    /**
     * Gets the totalMarketValue.
     * 
     * @return totalMarketValue
     */
    public BigDecimal getTotalMarketValue() {
        return totalMarketValue;
    }

    /**
     * Sets the totalMarketValue.
     * 
     * @param totalMarketValue
     */
    public void setTotalMarketValue(BigDecimal totalMarketValue) {
        this.totalMarketValue = totalMarketValue;
    }

}
