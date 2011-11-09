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

import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionTaxLotLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.krad.service.BusinessObjectService;

public enum EndowmentTransactionTaxLotLineFixture {
    
    // Tax Lot Fixture
    TAX_LOT_RECORD1(new KualiInteger(1), //documentLineNumber
            "F", //documentLineTypeCode
            new KualiInteger(1), //transactionHoldingLotNumber
            "TESTKEMID", // kemid
            "TESTSECID", // securityId
            "TEST", // registrationCode
            "I", // incomePrincipalIndicator
            BigDecimal.valueOf(20.00), // lotUnits
            BigDecimal.valueOf(10000.00), // lotHoldingCost
            BigDecimal.valueOf(0.00), // lotLongTermGainLoss
            BigDecimal.valueOf(0.00), // lotShortTermGainLoss
            Date.valueOf("2002-06-27"), // lotAcquiredDate
            true //newLotIndicator
    ),

    TAX_LOT_SOURCE_RECORD1(new KualiInteger(1), //documentLineNumber
            "T", //documentLineTypeCode
            new KualiInteger(1), //transactionHoldingLotNumber
            "TESTKEMID", // kemid
            "TESTSECID", // securityId
            "TEST", // registrationCode
            "I", // incomePrincipalIndicator
            BigDecimal.valueOf(-10.00), // lotUnits
            BigDecimal.valueOf(10000.00), // lotHoldingCost
            BigDecimal.valueOf(0.00), // lotLongTermGainLoss
            BigDecimal.valueOf(0.00), // lotShortTermGainLoss
            Date.valueOf("2002-06-27"), // lotAcquiredDate
            true //newLotIndicator
    ),
    
    TAX_LOT_TARGET_RECORD1(new KualiInteger(1), //documentLineNumber
            "T", //documentLineTypeCode
            new KualiInteger(1), //transactionHoldingLotNumber
            "TESTKEMID", // kemid
            "TESTSECID", // securityId
            "TEST", // registrationCode
            "I", // incomePrincipalIndicator
            BigDecimal.valueOf(10.00), // lotUnits
            BigDecimal.valueOf(10000.00), // lotHoldingCost
            BigDecimal.valueOf(0.00), // lotLongTermGainLoss
            BigDecimal.valueOf(0.00), // lotShortTermGainLoss
            Date.valueOf("2002-06-27"), // lotAcquiredDate
            true //newLotIndicator
    ),
    
    TAX_LOT_RECORD2(new KualiInteger(1), //documentLineNumber
            "F", //documentLineTypeCode
            new KualiInteger(1), //transactionHoldingLotNumber
            "TESTKEMID", // kemid
            "TESTSECID", // securityId
            "TEST", // registrationCode
            "P", // incomePrincipalIndicator
            BigDecimal.valueOf(40.00), // lotUnits
            BigDecimal.valueOf(10000.00), // lotHoldingCost
            BigDecimal.valueOf(0.00), // lotLongTermGainLoss
            BigDecimal.valueOf(0.00), // lotShortTermGainLoss
            Date.valueOf("2002-06-27"), // lotAcquiredDate
            true //newLotIndicator
    );

    public final KualiInteger documentLineNumber;
    public final String documentLineTypeCode;
    public final KualiInteger transactionHoldingLotNumber;
    public final String kemid;
    public final String securityId;
    public final String registrationCode;
    public final String incomePrincipalIndicator;
    public final BigDecimal lotUnits;
    public final BigDecimal lotHoldingCost;
    public final BigDecimal lotLongTermGainLoss;
    public final BigDecimal lotShortTermGainLoss;
    public final Date lotAcquiredDate;
    public final boolean newLotIndicator;

    // default record...
    private EndowmentTransactionTaxLotLineFixture(KualiInteger documentLineNumber, String documentLineTypeCode,
                                                  KualiInteger transactionHoldingLotNumber,
                                                  String kemid, String securityId, String registrationCode, 
                                                  String incomePrincipalIndicator, BigDecimal lotUnits, 
                                                  BigDecimal lotHoldingCost, BigDecimal lotLongTermGainLoss, 
                                                  BigDecimal lotShortTermGainLoss, Date lotAcquiredDate,  
                                                  boolean newLotIndicator) {

        this.documentLineNumber = documentLineNumber;
        this.documentLineTypeCode = documentLineTypeCode;
        this.transactionHoldingLotNumber = transactionHoldingLotNumber;
        this.kemid = kemid;
        this.securityId = securityId;
        this.registrationCode = registrationCode;
        this.incomePrincipalIndicator = incomePrincipalIndicator;
        this.lotUnits = lotUnits;
        this.lotHoldingCost = lotHoldingCost;
        this.lotLongTermGainLoss = lotLongTermGainLoss;
        this.lotShortTermGainLoss = lotShortTermGainLoss;
        this.lotAcquiredDate = lotAcquiredDate;
        this.newLotIndicator = newLotIndicator;
    }

    /**
     * This method creates a default Holding Tax Lot record and saves it to table
     * 
     * @return EndowmentTransactionTaxLotLine record
     */
    public EndowmentTransactionTaxLotLine createEndowmentTransactionTaxLotLineRecord() {
        EndowmentTransactionTaxLotLine endowmentTransactionTaxLotLine = new EndowmentTransactionTaxLotLine();

        endowmentTransactionTaxLotLine.setKemid(this.kemid);
        endowmentTransactionTaxLotLine.setSecurityID(this.securityId);
        endowmentTransactionTaxLotLine.setRegistrationCode(this.registrationCode);
        endowmentTransactionTaxLotLine.setIpIndicator(this.incomePrincipalIndicator);
        endowmentTransactionTaxLotLine.setLotUnits(this.lotUnits);
        endowmentTransactionTaxLotLine.setLotHoldingCost(this.lotHoldingCost);
        endowmentTransactionTaxLotLine.setLotLongTermGainLoss(this.lotLongTermGainLoss);
        endowmentTransactionTaxLotLine.setLotShortTermGainLoss(this.lotShortTermGainLoss);
        endowmentTransactionTaxLotLine.setLotAcquiredDate(this.lotAcquiredDate);
        endowmentTransactionTaxLotLine.setNewLotIndicator(this.newLotIndicator);
    //    saveEndowmentTransactionTaxLotLineRecord(endowmentTransactionTaxLotLine);
        return endowmentTransactionTaxLotLine;
    }

    /**
     * Method to save the business object....
     */
    private void saveEndowmentTransactionTaxLotLineRecord(EndowmentTransactionTaxLotLine endowmentTransactionTaxLotLine) {
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        businessObjectService.save(endowmentTransactionTaxLotLine);
    }
}
