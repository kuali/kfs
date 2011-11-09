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

import org.kuali.kfs.module.endow.EndowTestConstants;
import org.kuali.kfs.module.endow.businessobject.HoldingTaxLot;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.krad.service.BusinessObjectService;

public enum HoldingTaxLotFixture {
    // Holding Tax Lot Fixture
    HOLDING_TAX_LOT_RECORD("TESTKEMID", // kemid
            "99PETTY12", // securityId
            "0NI", // registrationCode
            "I", // incomePrincipalIndicator
            new KualiInteger(1), // lotNumber
            Date.valueOf("2005-11-01"), // acquiredDate
            BigDecimal.valueOf(20.00), // units
            BigDecimal.valueOf(10000.00), // cost
            BigDecimal.valueOf(0.00), // currentAccrual
            BigDecimal.valueOf(0.00), // priorAccrual
            Date.valueOf("2002-06-27") // lastTransactionDate
    ),

    HOLDING_TAX_LOT_RECORD_2("TESTKEMID", // kemid
            "99PETTY12", // securityId
            "0AI", // registrationCode
            "I", // incomePrincipalIndicator
            new KualiInteger(2), // lotNumber
            Date.valueOf("2009-11-23"), // acquiredDate
            BigDecimal.valueOf(282586.00), // units
            BigDecimal.valueOf(282586.00), // cost
            BigDecimal.valueOf(0.00), // currentAccrual
            BigDecimal.valueOf(0.00), // priorAccrual
            Date.valueOf("2009-11-23") // lastTransactionDate
    ),

    HOLDING_TAX_LOT_RECORD_3("TESTKEMID", // kemid
            "99PETTY12", // securityId
            "0AI", // registrationCode
            "P", // incomePrincipalIndicator
            new KualiInteger(3), // lotNumber
            Date.valueOf("2009-11-23"), // acquiredDate
            BigDecimal.valueOf(23123.00), // units
            BigDecimal.valueOf(23123.00), // cost
            BigDecimal.valueOf(0.00), // currentAccrual
            BigDecimal.valueOf(1.20), // priorAccrual
            Date.valueOf("2009-11-23") // lastTransactionDate
    ),

    HOLDING_TAX_LOT_RECORD_4("TESTKEMID", // kemid
            "99PETTY12", // securityId
            "REI", // registrationCode
            "P", // incomePrincipalIndicator
            new KualiInteger(4), // lotNumber
            Date.valueOf("2009-11-23"), // acquiredDate
            BigDecimal.valueOf(10000.00), // units
            BigDecimal.valueOf(10000.00), // cost
            BigDecimal.valueOf(0.00), // currentAccrual
            BigDecimal.valueOf(0.00), // priorAccrual
            Date.valueOf("2009-11-23") // lastTransactionDate
    ),
    
    HOLDING_TAX_LOT_RECORD_ALL_ZERO("TESTKEMID", // kemid
            "99PETTY12", // securityId
            "0AI", // registrationCode
            "P", // incomePrincipalIndicator
            new KualiInteger(4), // lotNumber
            Date.valueOf("2009-11-23"), // acquiredDate
            BigDecimal.ZERO, // units
            BigDecimal.ZERO, // cost
            BigDecimal.valueOf(0.00), // currentAccrual
            BigDecimal.valueOf(0.00), // priorAccrual
            Date.valueOf("2009-11-23") // lastTransactionDate
    ),
    
    HOLDING_TAX_LOT_RECORD_ALL_NEGATIVE("TESTKEMID", // kemid
            "99PETTY12", // securityId
            "REI", // registrationCode
            "P", // incomePrincipalIndicator
            new KualiInteger(4), // lotNumber
            Date.valueOf("2009-11-23"), // acquiredDate
            BigDecimal.valueOf(-1), // units
            BigDecimal.valueOf(-1), // cost
            BigDecimal.valueOf(0.00), // currentAccrual
            BigDecimal.valueOf(0.00), // priorAccrual
            Date.valueOf("2009-11-23") // lastTransactionDate
    ),
    

    HOLDING_TAX_LOT_RECORD_ZERO_UNIT("TESTKEMID", // kemid
            "99PETTY12", // securityId
            "TEST", // registrationCode
            "I", // incomePrincipalIndicator
            new KualiInteger(1), // lotNumber
            Date.valueOf("2009-11-23"), // acquiredDate
            BigDecimal.ZERO, // units
            BigDecimal.valueOf(10000.00), // cost
            BigDecimal.valueOf(0.00), // currentAccrual
            BigDecimal.valueOf(0.00), // priorAccrual
            Date.valueOf("2009-11-23") // lastTransactionDate
    ),
    
    HOLDING_TAX_LOT_RECORD_ZERO_COST("TESTKEMID", // kemid
            "99PETTY12", // securityId
            "TEST", // registrationCode
            "P", // incomePrincipalIndicator
            new KualiInteger(4), // lotNumber
            Date.valueOf("2009-11-23"), // acquiredDate
            BigDecimal.valueOf(10000.00), // unit
            BigDecimal.ZERO, // cost
            BigDecimal.valueOf(0.00), // currentAccrual
            BigDecimal.valueOf(0.00), // priorAccrual
            Date.valueOf("2009-11-23") // lastTransactionDate
    ),

    HOLDING_TAX_LOT_RECORD_FOR_LIABILITY("TESTKEMID", // kemid
            "TESTSECID", // securityId
            "TEST", // registrationCode
            "P", // incomePrincipalIndicator
            new KualiInteger(1), // lotNumber
            Date.valueOf("2005-11-01"), // acquiredDate
            BigDecimal.valueOf(20.00), // units
            BigDecimal.valueOf(10000.00), // cost
            BigDecimal.valueOf(0.00), // currentAccrual
            BigDecimal.valueOf(0.00), // priorAccrual
            Date.valueOf("2002-06-27") // lastTransactionDate)
    ),

    HOLDING_TAX_LOT_RECORD_FOR_CORPORATE_REORGANIZATION("TESTKEMID", // kemid
            "TESTSECID", // securityId
            "TEST", // registrationCode
            "I", // incomePrincipalIndicator
            new KualiInteger(1), // lotNumber
            Date.valueOf("2005-11-01"), // acquiredDate
            BigDecimal.valueOf(20.00), // units
            BigDecimal.valueOf(10000.00), // cost
            BigDecimal.valueOf(0.00), // currentAccrual
            BigDecimal.valueOf(0.00), // priorAccrual
            Date.valueOf("2002-06-27") // lastTransactionDate)
    ),
    
    HOLDING_TAX_LOT_RECORD_FOR_EAD("TESTKEMID", // kemid
            "TESTSECID", // securityId
            "TEST", // registrationCode
            "P", // incomePrincipalIndicator
            new KualiInteger(1), // lotNumber
            Date.valueOf("2005-11-01"), // acquiredDate
            BigDecimal.valueOf(20.00), // units
            BigDecimal.valueOf(10000.00), // cost
            BigDecimal.valueOf(0.00), // currentAccrual
            BigDecimal.valueOf(0.00), // priorAccrual
            Date.valueOf("2002-06-27") // lastTransactionDate)
    ),

    HOLDING_TAX_LOT_RECORD_FOR_ACCRUAL_PROC(EndowTestConstants.TEST_KEMID, // kemid
            EndowTestConstants.TEST_SEC_ID, // securityId
            EndowTestConstants.TEST_REGISTRATION_CD, // registrationCode
            "P", // incomePrincipalIndicator
            new KualiInteger(1), // lotNumber
            Date.valueOf("2005-11-01"), // acquiredDate
            EndowTestConstants.HOLDING_UNITS, // units
            BigDecimal.valueOf(10000.00), // cost
            BigDecimal.ZERO, // currentAccrual
            BigDecimal.ZERO, // priorAccrual
            Date.valueOf("2002-06-27") // lastTransactionDate)
    ), 
    
    HOLDING_TAX_LOT_RECORD_FOR_GAIN_LOSS_COMMITTED("TEST_KEMID", // kemid
            "DUMMYID", // securityId
            "2TST", // registrationCode
            "I", // incomePrincipalIndicator
            new KualiInteger(1), // lotNumber
            Date.valueOf("2005-11-01"), // acquiredDate
            EndowTestConstants.HOLDING_UNITS, // units
            BigDecimal.valueOf(10000.00), // cost
            BigDecimal.ZERO, // currentAccrual
            BigDecimal.ZERO, // priorAccrual
            Date.valueOf("2002-06-27") // lastTransactionDate)
    ),    
    HOLDING_TAX_LOT_RECORD_FOR_CREATE_ACCRUAL_TRANSACTIONS(EndowTestConstants.TEST_KEMID, // kemid
            EndowTestConstants.TEST_SEC_ID, // securityId
            EndowTestConstants.TEST_REGISTRATION_CD, // registrationCode
            "I", // incomePrincipalIndicator
            new KualiInteger(1), // lotNumber
            Date.valueOf("2005-11-01"), // acquiredDate
            EndowTestConstants.HOLDING_UNITS, // units
            BigDecimal.valueOf(10000.00), // cost
            BigDecimal.valueOf(1000.00), // currentAccrual
            BigDecimal.ZERO, // priorAccrual
            Date.valueOf("2002-06-27") // lastTransactionDate)    
    ),
    HOLDING_TAX_LOT_RECORD_FOR_SALE1("TSTKEMID1", // kemid
            "TSTSECID1", // securityId
            "RC1", // registrationCode
            "I", // incomePrincipalIndicator
            new KualiInteger(1), // lotNumber
            Date.valueOf("2005-11-01"), // acquiredDate
            BigDecimal.valueOf(2501.00), // units
            BigDecimal.valueOf(10000.00), // cost
            BigDecimal.valueOf(0.00), // currentAccrual
            BigDecimal.valueOf(0.00), // priorAccrual
            Date.valueOf("2002-06-27") // lastTransactionDate)
    ),
    HOLDING_TAX_LOT_RECORD_FOR_SALE2("TSTKEMID2", // kemid
            "TSTSECID2", // securityId
            "RC2", // registrationCode
            "P", // incomePrincipalIndicator
            new KualiInteger(1), // lotNumber
            Date.valueOf("2005-11-01"), // acquiredDate
            BigDecimal.valueOf(2501.00), // units
            BigDecimal.valueOf(10000.00), // cost
            BigDecimal.valueOf(0.00), // currentAccrual
            BigDecimal.valueOf(0.00), // priorAccrual
            Date.valueOf("2002-06-27") // lastTransactionDate)
    ),;

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

    // default record...
    private HoldingTaxLotFixture(String kemid, String securityId, String registrationCode, String incomePrincipalIndicator, KualiInteger lotNumber, Date acquiredDate, BigDecimal units, BigDecimal cost, BigDecimal currentAccrual, BigDecimal priorAccrual, Date lastTransactionDate) {
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
     * 
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
     * Method to save the business object....
     */
    private void saveHoldingTaxLotRecord(HoldingTaxLot holdingTaxLot) {
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        businessObjectService.save(holdingTaxLot);
    }
}
