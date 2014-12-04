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

import org.kuali.kfs.module.endow.businessobject.HoldingTaxLotRebalance;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.krad.service.BusinessObjectService;

public enum HoldingTaxLotRebalanceFixture {
    // Holding Tax Lot Rebalance Fixture
    HOLDING_TAX_LOT_REBALANCE_RECORD("TESTKEMID", //kemid
            "99PETTY12", //securityId
            "0NI", //registrationCode
            "I", //incomePrincipalIndicator
            new KualiInteger(1), //totalLotNumber
            BigDecimal.valueOf(20.00), // totalUnits
            BigDecimal.valueOf(10000.00) // totalCost
    ), 
    
    HOLDING_TAX_LOT_REBALANCE_RECORD_2("TESTKEMID", //kemid
            "99PETTY12", //securityId
            "0AI", //registrationCode
            "I", //incomePrincipalIndicator
            new KualiInteger(2), //totalLotNumber
            BigDecimal.valueOf(282586.00), // totalUnits
            BigDecimal.valueOf(282586.00) // totalCost
    ), 
    
    HOLDING_TAX_LOT_REBALANCE_RECORD_3("TESTKEMID", //kemid
            "99PETTY12", //securityId
            "0AI", //registrationCode
            "P", //incomePrincipalIndicator
            new KualiInteger(3), //totalLotNumber
            BigDecimal.valueOf(23123.00), // totalUnits
            BigDecimal.valueOf(23123.00) // totalCost
    ), 
    
    HOLDING_TAX_LOT_REBALANCE_RECORD_4("TESTKEMID", //kemid
            "99PETTY12", //securityId
            "REI", //registrationCode
            "P", //incomePrincipalIndicator
            new KualiInteger(4), //totalLotNumber
            BigDecimal.valueOf(10000.00), // totalUnits
            BigDecimal.valueOf(10000.00) // totalCost
    ), 
    
    HOLDING_TAX_LOT_REBALANCE_RECORD_ZERO_UNIT("TESTKEMID", //kemid
            "99PETTY12", //securityId
            "TEST", //registrationCode
            "I", //incomePrincipalIndicator
            new KualiInteger(1), //totalLotNumber
            BigDecimal.ZERO, // totalUnits
            BigDecimal.valueOf(10000.00) // totalCost
    ), 
    
    HOLDING_TAX_LOT_REBALANCE_RECORD_ZERO_COST("TESTKEMID", //kemid
            "99PETTY12", //securityId
            "TEST", //registrationCode
            "P", //incomePrincipalIndicator
            new KualiInteger(1), //totalLotNumber
            BigDecimal.valueOf(10000.00), // totalUnits
            BigDecimal.ZERO // totalCost
    ), 
    
    HOLDING_TAX_LOT_REBALANCE_RECORD_FOR_LIABILITY("TESTKEMID", //kemid
            "TESTSECID", //securityId
            "TEST", //registrationCode
            "P", //incomePrincipalIndicator
            new KualiInteger(1), //totalLotNumber
            BigDecimal.valueOf(20.00), // totalUnits
            BigDecimal.valueOf(10000.00) // totalCost    
    ),
    
    HOLDING_TAX_LOT_REBALANCE_RECORD_FOR_CORPORATE_REORGANIZATION("TESTKEMID", //kemid
            "TESTSECID", //securityId
            "TEST", //registrationCode
            "I", //incomePrincipalIndicator
            new KualiInteger(1), //totalLotNumber
            BigDecimal.valueOf(20.00), // totalUnits
            BigDecimal.valueOf(10000.00) // totalCost    
    ),    
    HOLDING_TAX_LOT_REBALANCE_RECORD_FOR_EAD("TESTKEMID", //kemid
            "TESTSECID", //securityId
            "TEST", //registrationCode
            "P", //incomePrincipalIndicator
            new KualiInteger(1), //totalLotNumber
            BigDecimal.valueOf(20.00), // totalUnits
            BigDecimal.valueOf(10000.00) // totalCost    
    ),
    
    HOLDING_TAX_LOT_REBALANCE_RECORD_FOR_ACCRUAL_PROC("TESTKEMID", //kemid
            "TESTSECID", //securityId
            "TEST", //registrationCode
            "P", //incomePrincipalIndicator
            new KualiInteger(1), //totalLotNumber
            BigDecimal.valueOf(20.00), // totalUnits
            BigDecimal.valueOf(10000.00) // totalCost
    ), 
    
    HOLDING_TAX_LOT_REBALANCE_RECORD_FOR_GAIN_LOSS_COMMITTED("TEST_KEMID", //kemid
            "DUMMYID", //securityId
            "2TST", //registrationCode
            "I", //incomePrincipalIndicator
            new KualiInteger(1), //totalLotNumber
            BigDecimal.valueOf(20.00), // totalUnits
            BigDecimal.valueOf(10000.00) // totalCost
    ), 
    
    HOLDING_TAX_LOT_REBALANCE_RECORD_FOR_CREATE_ACCRUAL_TRANSACTIONS("TESTKEMID", //kemid
            "TESTSECID", //securityId
            "TEST", //registrationCode
            "I", //incomePrincipalIndicator
            new KualiInteger(1), //totalLotNumber
            BigDecimal.valueOf(20.00), // totalUnits
            BigDecimal.valueOf(10000.00) // totalCost    
    ),
    HOLDING_TAX_LOT_REBALANCE_RECORD_FOR_SALE1("TSTKEMID1", //kemid
            "TSTSECID1", //securityId
            "RC1", //registrationCode
            "I", //incomePrincipalIndicator
            new KualiInteger(1), //totalLotNumber
            BigDecimal.valueOf(2501.00), // totalUnits
            BigDecimal.valueOf(10000.00) // totalCost    
    ),
    HOLDING_TAX_LOT_REBALANCE_RECORD_FOR_SALE2("TSTKEMID2", //kemid
            "TSTSECID2", //securityId
            "RC2", //registrationCode
            "P", //incomePrincipalIndicator
            new KualiInteger(1), //totalLotNumber
            BigDecimal.valueOf(2501.00), // totalUnits
            BigDecimal.valueOf(10000.00) // totalCost    
    ),;
    
    public final String kemid;
    public final String securityId;
    public final String registrationCode;
    public final String incomePrincipalIndicator; 
    public final KualiInteger totalLotNumber;
    public final BigDecimal totalUnits;
    public final BigDecimal totalCost;

    //default record...
    private HoldingTaxLotRebalanceFixture(String kemid, String securityId,
                                          String registrationCode, String incomePrincipalIndicator, 
                                          KualiInteger totalLotNumber, BigDecimal totalUnits,
                                          BigDecimal totalCost) {
        this.kemid = kemid;
        this.securityId = securityId;
        this.registrationCode = registrationCode;
        this.incomePrincipalIndicator = incomePrincipalIndicator; 
        this.totalLotNumber = totalLotNumber;
        this.totalUnits = totalUnits;
        this.totalCost = totalCost;

    }

    /**
     * Default record...
     * This method creates a HoldingHistoryRebalance record and saves it to table
     * @return holdingTaxLotRebalance record
     */
    public HoldingTaxLotRebalance createHoldingTaxLotRebalanceRecord() {
        HoldingTaxLotRebalance holdingTaxLotRebalance = new HoldingTaxLotRebalance();

        holdingTaxLotRebalance.setKemid(kemid);
        holdingTaxLotRebalance.setSecurityId(securityId);
        holdingTaxLotRebalance.setRegistrationCode(registrationCode);
        holdingTaxLotRebalance.setIncomePrincipalIndicator(incomePrincipalIndicator);
        holdingTaxLotRebalance.setTotalLotNumber(totalLotNumber);
        holdingTaxLotRebalance.setTotalUnits(totalUnits);
        holdingTaxLotRebalance.setTotalCost(totalCost);

        saveHoldingTaxLotRebalanceRecord(holdingTaxLotRebalance);
        
        return holdingTaxLotRebalance;
    }
    
    /**
     * Method to save the business object....
     */
    private void saveHoldingTaxLotRebalanceRecord(HoldingTaxLotRebalance holdingTaxLotRebalance) {
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        businessObjectService.save(holdingTaxLotRebalance);
    }
}

