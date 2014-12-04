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
package org.kuali.kfs.module.endow.fixture;

import java.math.BigDecimal;
import java.sql.Date;

import org.kuali.kfs.module.endow.businessobject.CurrentTaxLotBalance;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.krad.service.BusinessObjectService;

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
    ), 
    
    CURRENT_TAX_LOT_BALANCE_RECORD_2("TESTKEMID", //kemid
            "99PETTY12", //securityId
            "0AI", //registrationCode
            new KualiInteger(2), //lotNumber
            "I", //incomePrincipalIndicator
            BigDecimal.valueOf(282586.00), // units
            BigDecimal.valueOf(282586.00), // cost
            BigDecimal.valueOf(0.00), // annualEstimatedIncome
            BigDecimal.valueOf(0.00), //remainderOfFYEstimatedIncome
            BigDecimal.valueOf(0.00), // nextFYEstimatedIncome
            BigDecimal.valueOf(1.00), //securityUnitVal
            Date.valueOf("2009-11-23"), //acquiredDate
            BigDecimal.valueOf(0.00), // currentAccrual
            BigDecimal.valueOf(0.00), // priorAccrual
            Date.valueOf("2009-11-23"), //lastTransactionDate
            BigDecimal.valueOf(282586.00) // holdingMarketValue
    ),
    
    CURRENT_TAX_LOT_BALANCE_RECORD_3("TESTKEMID", //kemid
            "99PETTY12", //securityId
            "0AI", //registrationCode
            new KualiInteger(3), //lotNumber
            "P", //incomePrincipalIndicator
            BigDecimal.valueOf(23123.00), // units
            BigDecimal.valueOf(23123.00), // cost
            BigDecimal.valueOf(0.00), // annualEstimatedIncome
            BigDecimal.valueOf(0.00), //remainderOfFYEstimatedIncome
            BigDecimal.valueOf(0.00), // nextFYEstimatedIncome
            BigDecimal.valueOf(1.00), //securityUnitVal
            Date.valueOf("2009-12-10"), //acquiredDate
            BigDecimal.valueOf(1.20), // currentAccrual
            BigDecimal.valueOf(0.00), // priorAccrual
            Date.valueOf("2009-12-10"), //lastTransactionDate
            BigDecimal.valueOf(23123.00) // holdingMarketValue
    ), 

    CURRENT_TAX_LOT_BALANCE_RECORD_4("TESTKEMID", //kemid
            "99PETTY12", //securityId
            "REI", //registrationCode
            new KualiInteger(4), //lotNumber
            "P", //incomePrincipalIndicator
            BigDecimal.valueOf(10000.00), // units
            BigDecimal.valueOf(10000.00), // cost
            BigDecimal.valueOf(0.00), // annualEstimatedIncome
            BigDecimal.valueOf(0.00), //remainderOfFYEstimatedIncome
            BigDecimal.valueOf(0.00), // nextFYEstimatedIncome
            BigDecimal.valueOf(1.00), //securityUnitVal
            Date.valueOf("2009-09-30"), //acquiredDate
            BigDecimal.valueOf(0.00), // currentAccrual
            BigDecimal.valueOf(0.00), // priorAccrual
            Date.valueOf("2008-09-30"), //lastTransactionDate
            BigDecimal.valueOf(10000.00) // holdingMarketValue
    ),
    
    CURRENT_TAX_LOT_BALANCE_RECORD_FOR_FEE_PROCESSING("TESTKEMID", //kemid
            "TESTSECID", //securityId
            "TST1", //registrationCode
            new KualiInteger(1), //lotNumber
            "P", //incomePrincipalIndicator
            BigDecimal.valueOf(10000.00), // units
            BigDecimal.valueOf(10000.00), // cost
            BigDecimal.valueOf(0.00), // annualEstimatedIncome
            BigDecimal.valueOf(0.00), //remainderOfFYEstimatedIncome
            BigDecimal.valueOf(0.00), // nextFYEstimatedIncome
            BigDecimal.valueOf(1.00), //securityUnitVal
            Date.valueOf("2009-09-30"), //acquiredDate
            BigDecimal.valueOf(0.00), // currentAccrual
            BigDecimal.valueOf(0.00), // priorAccrual
            Date.valueOf("2008-09-30"), //lastTransactionDate
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
     * Method to save the business object....
     */
    private void saveHoldingTaxLotRebalanceRecord(CurrentTaxLotBalance currentTaxLotBalance) {
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        businessObjectService.save(currentTaxLotBalance);
    }
}

