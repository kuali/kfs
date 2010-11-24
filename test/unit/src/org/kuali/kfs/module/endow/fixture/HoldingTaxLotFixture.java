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

import org.kuali.kfs.module.endow.businessobject.HoldingTaxLot;
import org.kuali.kfs.module.endow.businessobject.HoldingTaxLotRebalance;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.KualiInteger;

public enum HoldingTaxLotFixture {
    // Holding Tax Lot Fixture
    HOLDING_TAX_LOT_RECORD("TESTKEMID", //kemid
            "99PETTY12", //securityId
            "0NI", //registrationCode
            "I", //incomePrincipalIndicator
            new KualiInteger(1), //lotNumber
            Date.valueOf("2005-11-01"), //acquiredDate
            BigDecimal.valueOf(20.00), // units
            BigDecimal.valueOf(10000.00), // cost
            BigDecimal.valueOf(0.00), // currentAccrual
            BigDecimal.valueOf(0.00), // priorAccrual
            Date.valueOf("2002-06-27") //lastTransactionDate
    ), 
    
    HOLDING_TAX_LOT_RECORD_FOR_LIABILITY("TESTKEMID", //kemid
            "TESTSECID", //securityId
            "TEST", //registrationCode
            "P", //incomePrincipalIndicator
            new KualiInteger(1), //lotNumber
            Date.valueOf("2005-11-01"), //acquiredDate
            BigDecimal.valueOf(20.00), // units
            BigDecimal.valueOf(10000.00), // cost
            BigDecimal.valueOf(0.00), // currentAccrual
            BigDecimal.valueOf(0.00), // priorAccrual
            Date.valueOf("2002-06-27") //lastTransactionDate)            
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
    
    //default record...
    private HoldingTaxLotFixture(String kemid, String securityId, String registrationCode, 
                                 String incomePrincipalIndicator, KualiInteger lotNumber,  
                                 Date acquiredDate, BigDecimal units,BigDecimal cost, 
                                 BigDecimal currentAccrual, BigDecimal priorAccrual, Date lastTransactionDate) {
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
    }

    /**
     * This method creates a default Holding Tax Lot record and saves it to table
     * @return HoldingTaxLot record
     */
    public HoldingTaxLot createHoldingTaxLotRecord() {
        HoldingTaxLot holdingTaxLot = new HoldingTaxLot();

        holdingTaxLot.setKemid(this.kemid);
        holdingTaxLot.setSecurityId(this.securityId);
        holdingTaxLot.setRegistrationCode(this.registrationCode);
        holdingTaxLot.setIncomePrincipalIndicator(this.incomePrincipalIndicator);
        holdingTaxLot.setLotNumber(this.lotNumber);
        holdingTaxLot.setAcquiredDate(this.acquiredDate);
        holdingTaxLot.setUnits(this.units);
        holdingTaxLot.setCost(this.cost);
        holdingTaxLot.setCurrentAccrual(this.currentAccrual);
        holdingTaxLot.setPriorAccrual(this.priorAccrual);
        holdingTaxLot.setLastTransactionDate(this.lastTransactionDate);

        saveHoldingTaxLotRecord(holdingTaxLot);        
        return holdingTaxLot;
    }
    
    /**
     * This method creates a Holding Tax Lot record and saves it to table
     * @return HoldingTaxLot record
     */
    public HoldingTaxLot createHoldingTaxLotRecord(String kemid, String securityId, String registrationCode, 
                                                   String incomePrincipalIndicator, KualiInteger lotNumber,  
                                                   Date acquiredDate, BigDecimal units, BigDecimal cost, 
                                                   BigDecimal currentAccrual, BigDecimal priorAccrual, Date lastTransactionDate) {
        HoldingTaxLot holdingTaxLot = new HoldingTaxLot();

        holdingTaxLot.setKemid(kemid);
        holdingTaxLot.setSecurityId(securityId);
        holdingTaxLot.setRegistrationCode(registrationCode);
        holdingTaxLot.setIncomePrincipalIndicator(incomePrincipalIndicator);
        holdingTaxLot.setLotNumber(lotNumber);
        holdingTaxLot.setAcquiredDate(acquiredDate);
        holdingTaxLot.setUnits(units);
        holdingTaxLot.setCost(cost);
        holdingTaxLot.setCurrentAccrual(currentAccrual);
        holdingTaxLot.setPriorAccrual(priorAccrual);
        holdingTaxLot.setLastTransactionDate(lastTransactionDate);

        saveHoldingTaxLotRecord(holdingTaxLot);        
        return holdingTaxLot;
    }

    /**
     * Method to save the business object....
     */
    private void saveHoldingTaxLotRecord(HoldingTaxLot holdingTaxLot) {
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        businessObjectService.save(holdingTaxLot);
    }
}

