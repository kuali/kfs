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

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class KEMIDCurrentBalanceDetail extends PersistableBusinessObjectBase {

    private String kemid;
    private String incomePrincipalIndicator;
    private String reportingGroupCode;
    private BigDecimal valueAtMarket;
    private BigDecimal annualEstimatedIncome;
    private BigDecimal remainderOfFYEstimatedIncome;
    private BigDecimal nextFYEstimatedIncome;

    // not persisted; used to tell whether there will be a drill down on value at market
    private boolean noDrillDownOnMarketVal;

    private KEMID kemidObj;
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
     * Gets the ipIndicator
     * 
     * @return ipIndicator
     */
    public IncomePrincipalIndicator getIpIndicator() {
        return ipIndicator;
    }


    /**
     * Sets the ipIndicator
     * 
     * @param ipIndicator
     */
    public void setIpIndicator(IncomePrincipalIndicator ipIndicator) {
        this.ipIndicator = ipIndicator;
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

    /**
     * Gets the incomeAtMarket. If income principal indicator is 'I' then the valueAtMarket represents the incomeAtMarket. Otherwise
     * the incomeAtMarket will be zero.
     * 
     * @return incomeAtMarket
     */
    public BigDecimal getIncomeAtMarket() {
        BigDecimal incomeAtMarket = BigDecimal.ZERO.setScale(2);
        if (EndowConstants.IncomePrincipalIndicator.INCOME.equalsIgnoreCase(incomePrincipalIndicator)) {
            incomeAtMarket = valueAtMarket;
        }
        return incomeAtMarket;
    }

    /**
     * Gets the principalAtMarket. If income principal indicator is 'P' then the valueAtMarket represents the principalAtMarket.
     * Otherwise the principalAtMarket will be zero.
     * 
     * @return
     */
    public BigDecimal getPrincipalAtMarket() {
        BigDecimal principalAtMarket = BigDecimal.ZERO.setScale(2);
        if (EndowConstants.IncomePrincipalIndicator.PRINCIPAL.equalsIgnoreCase(incomePrincipalIndicator)) {
            principalAtMarket = valueAtMarket;
        }
        return principalAtMarket;
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
     * Gets the Balance Date which is the Current System Process date
     * 
     * @return the Balance Date
     */
    public Date getBalanceDate() {

        return SpringContext.getBean(KEMService.class).getCurrentDate();
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
     * Gets the noDrillDownOnMarketVal.
     * 
     * @return noDrillDownOnMarketVal
     */
    public boolean isNoDrillDownOnMarketVal() {
        return noDrillDownOnMarketVal;
    }


    /**
     * Sets the noDrillDownOnMarketVal.
     * 
     * @param noDrillDownOnMarketVal
     */
    public void setNoDrillDownOnMarketVal(boolean noDrillDownOnMarketVal) {
        this.noDrillDownOnMarketVal = noDrillDownOnMarketVal;
    }

}
