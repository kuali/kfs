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

import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class KEMIDCurrentBalance extends PersistableBusinessObjectBase {

    private String kemid;
    private BigDecimal incomeAtMarket;
    private BigDecimal principalAtMarket;
    private BigDecimal totalMarketValue;
    private BigDecimal annualEstimatedIncome;
    private BigDecimal remainderOfFYEstimatedIncome;
    private BigDecimal nextFYEstimatedIncome;

    private KEMID kemidObj;

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(EndowPropertyConstants.KEMID, this.kemid);
        return m;
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
     * Gets the Balance Date which is the Current/System Process date
     * 
     * @return the Balance Date
     */
    public Date getBalanceDate() {

        return SpringContext.getBean(KEMService.class).getCurrentDate();
    }

}
