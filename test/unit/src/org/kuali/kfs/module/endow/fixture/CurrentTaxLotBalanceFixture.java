/*
 * Copyright 2007 The Kuali Foundation
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

import org.kuali.kfs.module.endow.businessobject.CurrentTaxLotBalance;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.KualiInteger;

public enum CurrentTaxLotBalanceFixture {
    // Current Tax Lot Balance Fixture
    CURRENT_TAX_LOT_BALANCE_RECORD("TESTKEMID", //kemid
            "99PETTY12", //securityId
            "0NI", //registrationCode
            new KualiInteger(1), //lotNumber
            "I", //incomePrincipalIndicator
            BigDecimal.valueOf(20.00), // units
            BigDecimal.valueOf(10000.00), // cost
            BigDecimal.valueOf(0.00), // annualEstimatedIncome
            BigDecimal.valueOf(0.00), //remainderOfFYEstimatedIncome
            BigDecimal.valueOf(0.00), // nextFYEstimatedIncome
            BigDecimal.valueOf(500.00), //securityUnitVal
            Date.valueOf("2005-01-01"), //acquiredDate
            BigDecimal.valueOf(0.00), // currentAccrual
            BigDecimal.valueOf(0.00), // priorAccrual
            Date.valueOf("2002-06-01"), //lastTransactionDate
            BigDecimal.valueOf(10000.00) // holdingMarketValue
    );
    
    public final String kemid;
    public final String securityId;
    public final String registrationCode;
    public final String incomePrincipalIndicator; 
    public final KualiInteger lotNumber;
    public final Date acquiredDate;
    public final BigDecimal units;
    public final BigDecimal cost;
    public final BigDecimal currentAccrual;
    public final BigDecimal priorAccrual;
    public final Date lastTransactionDate;
    
    public final BigDecimal annualEstimatedIncome;
    public final BigDecimal remainderOfFYEstimatedIncome;
    public final BigDecimal nextFYEstimatedIncome;
    public final BigDecimal securityUnitVal;
    public final BigDecimal holdingMarketValue;
    
    //default record...
    private CurrentTaxLotBalanceFixture(String kemid, String securityId, String registrationCode, 
                                        KualiInteger lotNumber, String incomePrincipalIndicator,   
                                        BigDecimal units, BigDecimal cost, BigDecimal annualEstimatedIncome, 
                                        BigDecimal remainderOfFYEstimatedIncome, BigDecimal nextFYEstimatedIncome,
                                        BigDecimal securityUnitVal, Date acquiredDate,
                                        BigDecimal currentAccrual, BigDecimal priorAccrual, Date lastTransactionDate,
                                        BigDecimal holdingMarketValue) {
        this.kemid = kemid;
        this.securityId = securityId;
        this.registrationCode = registrationCode;
        this.incomePrincipalIndicator = incomePrincipalIndicator; 
        this.lotNumber = lotNumber;
        this.acquiredDate = acquiredDate;
        this.units = units;
        this.cost = cost;
        this.currentAccrual = currentAccrual;
        this.priorAccrual = priorAccrual;
        this.lastTransactionDate = lastTransactionDate;
        this.annualEstimatedIncome = annualEstimatedIncome;
        this.remainderOfFYEstimatedIncome =remainderOfFYEstimatedIncome;
        this.nextFYEstimatedIncome = nextFYEstimatedIncome;
        this.securityUnitVal = securityUnitVal;
        this.holdingMarketValue = holdingMarketValue;
    } 

    /**
     * This method creates a default Holding Tax Lot record and saves it to table
     * @return HoldingTaxLot record
     */
    public CurrentTaxLotBalance createCurrentTaxLotBalanceRecord() {
        CurrentTaxLotBalance currentTaxLotBalance = new CurrentTaxLotBalance();

        currentTaxLotBalance.setKemid(this.kemid);
        currentTaxLotBalance.setSecurityId(this.securityId);
        currentTaxLotBalance.setRegistrationCode(this.registrationCode);
        currentTaxLotBalance.setIncomePrincipalIndicator(this.incomePrincipalIndicator);
        currentTaxLotBalance.setLotNumber(this.lotNumber);
        currentTaxLotBalance.setUnits(this.units);
        currentTaxLotBalance.setCost(this.cost);
        currentTaxLotBalance.setAcquiredDate(this.acquiredDate);
        currentTaxLotBalance.setCurrentAccrual(this.currentAccrual);
        currentTaxLotBalance.setPriorAccrual(this.priorAccrual);
        currentTaxLotBalance.setLastTransactionDate(this.lastTransactionDate);
        currentTaxLotBalance.setAnnualEstimatedIncome(this.annualEstimatedIncome);
        currentTaxLotBalance.setRemainderOfFYEstimatedIncome(this.remainderOfFYEstimatedIncome);
        currentTaxLotBalance.setNextFYEstimatedIncome(this.nextFYEstimatedIncome);
        currentTaxLotBalance.setSecurityUnitVal(this.securityUnitVal);
        currentTaxLotBalance.setHoldingMarketValue(this.holdingMarketValue);

        saveHoldingTaxLotRebalanceRecord(currentTaxLotBalance);        
        return currentTaxLotBalance;
    }
    
    /**
     * This method creates a Holding Tax Lot record and saves it to table
     * @return HoldingTaxLot record
     */
    public CurrentTaxLotBalance createCurrentTaxLotBalanceRecord(String kemid, String securityId, String registrationCode, 
                                                                 KualiInteger lotNumber, String incomePrincipalIndicator,   
                                                                 BigDecimal units, BigDecimal cost, BigDecimal annualEstimatedIncome, 
                                                                 BigDecimal remainderOfFYEstimatedIncome, BigDecimal nextFYEstimatedIncome,
                                                                 BigDecimal securityUnitVal, Date acquiredDate,
                                                                 BigDecimal currentAccrual, BigDecimal priorAccrual, Date lastTransactionDate,
                                                                 BigDecimal holdingMarketValue) {
        CurrentTaxLotBalance currentTaxLotBalance = new CurrentTaxLotBalance();

        currentTaxLotBalance.setKemid(kemid);
        currentTaxLotBalance.setSecurityId(securityId);
        currentTaxLotBalance.setRegistrationCode(registrationCode);
        currentTaxLotBalance.setIncomePrincipalIndicator(incomePrincipalIndicator);
        currentTaxLotBalance.setLotNumber(lotNumber);
        currentTaxLotBalance.setUnits(units);
        currentTaxLotBalance.setCost(cost);
        currentTaxLotBalance.setAcquiredDate(acquiredDate);        
        currentTaxLotBalance.setCurrentAccrual(currentAccrual);
        currentTaxLotBalance.setPriorAccrual(priorAccrual);
        currentTaxLotBalance.setLastTransactionDate(lastTransactionDate);
        currentTaxLotBalance.setAnnualEstimatedIncome(annualEstimatedIncome);
        currentTaxLotBalance.setRemainderOfFYEstimatedIncome(remainderOfFYEstimatedIncome);
        currentTaxLotBalance.setNextFYEstimatedIncome(nextFYEstimatedIncome);
        currentTaxLotBalance.setSecurityUnitVal(securityUnitVal);
        currentTaxLotBalance.setHoldingMarketValue(holdingMarketValue);

        saveHoldingTaxLotRebalanceRecord(currentTaxLotBalance);        
        return currentTaxLotBalance;
    }

    /**
     * Method to save the business object....
     */
    private void saveHoldingTaxLotRebalanceRecord(CurrentTaxLotBalance currentTaxLotBalance) {
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        businessObjectService.save(currentTaxLotBalance);
    }
}

