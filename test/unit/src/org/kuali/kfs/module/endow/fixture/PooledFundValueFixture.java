/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.fixture;

import java.math.BigDecimal;
import java.sql.Date;

import org.kuali.kfs.module.endow.businessobject.PooledFundValue;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.BusinessObjectService;

public enum PooledFundValueFixture {

    CAPITAL_GAIN_LOSS_DISTRIBUTION_TRANSACTION_COMMITTED("DUMMYID", //pooledSecurityID
            Date.valueOf("2010-02-01"), //valueEffectiveDate
            Date.valueOf("2010-01-31"), //valuationDate
            BigDecimal.valueOf(0.0905) , //unitValue
            BigDecimal.ZERO, //incomeDistributionPerUnit
            Date.valueOf("2010-01-31"), //distributeIncomeOnDate
            false, //incomeDistributionComplete
            BigDecimal.ZERO, //longTermGainLossDistributionPerUnit
            Date.valueOf("2010-01-31"), //distributeLongTermGainLossOnDate
            false, //longTermGainLossDistributionComplete
            BigDecimal.valueOf(100.00), //shortTermGainLossDistributionPerUnit
            Date.valueOf("2010-01-31"), //distributeShortTermGainLossOnDate
            false //shortTermGainLossDistributionComplete
      );
    
    // Pooled Fund Value 
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
    
    private PooledFundValueFixture() {};
    
    private PooledFundValueFixture(    
            String pooledSecurityID,           
            Date valueEffectiveDate,
            Date valuationDate,
            BigDecimal unitValue,
            BigDecimal incomeDistributionPerUnit,
            Date distributeIncomeOnDate,
            boolean incomeDistributionComplete,
            BigDecimal longTermGainLossDistributionPerUnit,
            Date distributeLongTermGainLossOnDate,
            boolean longTermGainLossDistributionComplete,
            BigDecimal shortTermGainLossDistributionPerUnit,
            Date distributeShortTermGainLossOnDate,
            boolean shortTermGainLossDistributionComplete) {
        
        this.pooledSecurityID = pooledSecurityID;
        this.valueEffectiveDate = valueEffectiveDate;
        this.valuationDate = valuationDate;
        this.unitValue = unitValue;
        this.incomeDistributionPerUnit = incomeDistributionPerUnit;
        this.distributeIncomeOnDate = distributeIncomeOnDate;
        this.incomeDistributionComplete = incomeDistributionComplete;
        this.longTermGainLossDistributionPerUnit = longTermGainLossDistributionPerUnit;
        this.distributeLongTermGainLossOnDate = distributeLongTermGainLossOnDate;
        this.longTermGainLossDistributionComplete = longTermGainLossDistributionComplete;
        this.shortTermGainLossDistributionPerUnit = shortTermGainLossDistributionPerUnit;
        this.distributeShortTermGainLossOnDate = distributeShortTermGainLossOnDate;
        this.shortTermGainLossDistributionComplete = shortTermGainLossDistributionComplete;
    }

    public PooledFundValue createPooledFundValue() {
    
        PooledFundValue pooledFundValue = new PooledFundValue();
        pooledFundValue.setPooledSecurityID(pooledSecurityID);
        pooledFundValue.setValueEffectiveDate(valueEffectiveDate);
        pooledFundValue.setValuationDate(valuationDate);
        pooledFundValue.setUnitValue(unitValue);
        pooledFundValue.setIncomeDistributionPerUnit(incomeDistributionPerUnit);
        pooledFundValue.setDistributeIncomeOnDate(distributeIncomeOnDate);
        pooledFundValue.setIncomeDistributionComplete(incomeDistributionComplete);
        pooledFundValue.setLongTermGainLossDistributionPerUnit(longTermGainLossDistributionPerUnit);
        pooledFundValue.setDistributeLongTermGainLossOnDate(distributeLongTermGainLossOnDate);
        pooledFundValue.setLongTermGainLossDistributionComplete(longTermGainLossDistributionComplete);
        pooledFundValue.setShortTermGainLossDistributionPerUnit(shortTermGainLossDistributionPerUnit);
        pooledFundValue.setDistributeShortTermGainLossOnDate(distributeShortTermGainLossOnDate);
        pooledFundValue.setShortTermGainLossDistributionComplete(shortTermGainLossDistributionComplete);
              
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        businessObjectService.save(pooledFundValue);
        
        return pooledFundValue;
    }
}
