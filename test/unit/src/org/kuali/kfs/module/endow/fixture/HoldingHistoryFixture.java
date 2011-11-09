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

import org.kuali.kfs.module.endow.businessobject.HoldingHistory;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.krad.service.BusinessObjectService;

public enum HoldingHistoryFixture {
    // Security Record FIXTURE
    HOLDING_HISTORY_RECORD("TESTKEMID", //kemid
            "TESTSECID", //securityId
            "0NI", //registrationCode
            new KualiInteger(1), //lotNumber
            "I", // incomePrincipalIndicator
            Date.valueOf("2006-01-01"), // acquiredDate 
            BigDecimal.valueOf(200L), // units
            BigDecimal.valueOf(1L), // cost
            BigDecimal.valueOf(1L), // currentAccrual
            BigDecimal.valueOf(1L), // priorAccrual
            Date.valueOf("2006-01-01"), // lastTransactionDate
            new KualiInteger(1), //monthEndDateId
            BigDecimal.valueOf(1L), // estimatedIncome 
            BigDecimal.valueOf(1L), // securityUnitVal
            BigDecimal.valueOf(1L), // marketValue
            BigDecimal.valueOf(1L), // averageMarketValue
            BigDecimal.valueOf(1L), // remainderOfFYEstimatedIncome
            BigDecimal.valueOf(1L) // nextFYEstimatedIncome
            ), 
            
            HOLDING_HISTORY_RECORD2("TESTKEMID", //kemid
                    "TESTSECID", //securityId
                    "0NI", //registrationCode
                    new KualiInteger(1), //lotNumber
                    "I", // incomePrincipalIndicator
                    Date.valueOf("2006-01-01"), // acquiredDate 
                    BigDecimal.valueOf(200L), // units
                    BigDecimal.valueOf(1L), // cost
                    BigDecimal.valueOf(1L), // currentAccrual
                    BigDecimal.valueOf(1L), // priorAccrual
                    Date.valueOf("2006-01-01"), // lastTransactionDate
                    new KualiInteger(0), //monthEndDateId
                    BigDecimal.valueOf(1L), // estimatedIncome 
                    BigDecimal.valueOf(1L), // securityUnitVal
                    BigDecimal.valueOf(1L), // marketValue
                    BigDecimal.valueOf(1L), // averageMarketValue
                    BigDecimal.valueOf(1L), // remainderOfFYEstimatedIncome
                    BigDecimal.valueOf(1L) // nextFYEstimatedIncome
            ),
                    
            HOLDING_HISTORY_RECORD1_FOR_HOLDING_HISTORY_VALUE_ADJUSTMENT("TESTKEMID", //kemid
                            "TESTSECID", //securityId
                            "TST1", //registrationCode
                            new KualiInteger(1), //lotNumber
                            "I", // incomePrincipalIndicator
                            Date.valueOf("2006-01-01"), // acquiredDate 
                            BigDecimal.valueOf(200L), // units
                            BigDecimal.valueOf(1L), // cost
                            BigDecimal.valueOf(1L), // currentAccrual
                            BigDecimal.valueOf(1L), // priorAccrual
                            Date.valueOf("2006-01-01"), // lastTransactionDate
                            new KualiInteger(1), //monthEndDateId
                            BigDecimal.valueOf(1L), // estimatedIncome 
                            BigDecimal.valueOf(1L), // securityUnitVal
                            BigDecimal.valueOf(1L), // marketValue
                            BigDecimal.valueOf(1L), // averageMarketValue
                            BigDecimal.valueOf(1L), // remainderOfFYEstimatedIncome
                            BigDecimal.valueOf(1L) // nextFYEstimatedIncome
            ),
            
            HOLDING_HISTORY_RECORD1_FOR_PROCESS_FEE_TRANSACTIONS("TESTKEMID", //kemid
                    "TESTSECID", //securityId
                    "TST1", //registrationCode
                    new KualiInteger(1), //lotNumber
                    "I", // incomePrincipalIndicator
                    Date.valueOf("2006-01-01"), // acquiredDate 
                    BigDecimal.valueOf(200L), // units
                    BigDecimal.valueOf(1L), // cost
                    BigDecimal.valueOf(1L), // currentAccrual
                    BigDecimal.valueOf(1L), // priorAccrual
                    Date.valueOf("2006-01-01"), // lastTransactionDate
                    new KualiInteger(0), //monthEndDateId
                    BigDecimal.valueOf(1L), // estimatedIncome 
                    BigDecimal.valueOf(1L), // securityUnitVal
                    BigDecimal.valueOf(1L), // marketValue
                    BigDecimal.valueOf(1L), // averageMarketValue
                    BigDecimal.valueOf(1L), // remainderOfFYEstimatedIncome
                    BigDecimal.valueOf(1L) // nextFYEstimatedIncome
    ),

            HOLDING_HISTORY_RECORD2_FOR_HOLDING_HISTORY_VALUE_ADJUSTMENT("TESTKEMID", //kemid
                                    "TESTSECID", //securityId
                                    "TST2", //registrationCode
                                    new KualiInteger(1), //lotNumber
                                    "I", // incomePrincipalIndicator
                                    Date.valueOf("2006-01-01"), // acquiredDate 
                                    BigDecimal.valueOf(200L), // units
                                    BigDecimal.valueOf(1L), // cost
                                    BigDecimal.valueOf(1L), // currentAccrual
                                    BigDecimal.valueOf(1L), // priorAccrual
                                    Date.valueOf("2006-01-01"), // lastTransactionDate
                                    new KualiInteger(1), //monthEndDateId
                                    BigDecimal.valueOf(1L), // estimatedIncome 
                                    BigDecimal.valueOf(1L), // securityUnitVal
                                    BigDecimal.valueOf(1L), // marketValue
                                    BigDecimal.valueOf(1L), // averageMarketValue
                                    BigDecimal.valueOf(1L), // remainderOfFYEstimatedIncome
                                    BigDecimal.valueOf(1L) // nextFYEstimatedIncome
            ),
            
            HOLDING_HISTORY_RECORD3_FOR_HOLDING_HISTORY_VALUE_ADJUSTMENT("TESTKEMID", //kemid
                    "TESTSEC2", //securityId
                    "TST1", //registrationCode
                    new KualiInteger(1), //lotNumber
                    "I", // incomePrincipalIndicator
                    Date.valueOf("2006-01-01"), // acquiredDate 
                    BigDecimal.valueOf(200L), // units
                    BigDecimal.valueOf(1L), // cost
                    BigDecimal.valueOf(1L), // currentAccrual
                    BigDecimal.valueOf(1L), // priorAccrual
                    Date.valueOf("2006-01-01"), // lastTransactionDate
                    new KualiInteger(1), //monthEndDateId
                    BigDecimal.valueOf(1L), // estimatedIncome 
                    BigDecimal.valueOf(1L), // securityUnitVal
                    BigDecimal.valueOf(1L), // marketValue
                    BigDecimal.valueOf(1L), // averageMarketValue
                    BigDecimal.valueOf(1L), // remainderOfFYEstimatedIncome
                    BigDecimal.valueOf(1L) // nextFYEstimatedIncome
            ),
            
            HOLDING_HISTORY_RECORD4_FOR_HOLDING_HISTORY_VALUE_ADJUSTMENT("TESTKEMID", //kemid
                    "TESTSEC2", //securityId
                    "TST2", //registrationCode
                    new KualiInteger(1), //lotNumber
                    "I", // incomePrincipalIndicator
                    Date.valueOf("2006-01-01"), // acquiredDate 
                    BigDecimal.valueOf(200L), // units
                    BigDecimal.valueOf(1L), // cost
                    BigDecimal.valueOf(1L), // currentAccrual
                    BigDecimal.valueOf(1L), // priorAccrual
                    Date.valueOf("2006-01-01"), // lastTransactionDate
                    new KualiInteger(1), //monthEndDateId
                    BigDecimal.valueOf(1L), // estimatedIncome 
                    BigDecimal.valueOf(1L), // securityUnitVal
                    BigDecimal.valueOf(1L), // marketValue
                    BigDecimal.valueOf(1L), // averageMarketValue
                    BigDecimal.valueOf(1L), // remainderOfFYEstimatedIncome
                    BigDecimal.valueOf(1L) // nextFYEstimatedIncome
    );
    
    public final String kemid;
    public final String securityId;
    public final String registrationCode;
    public final KualiInteger lotNumber;
    public final String incomePrincipalIndicator;
    public final Date acquiredDate;
    public final BigDecimal units;
    public final BigDecimal cost;
    public final BigDecimal currentAccrual;
    public final BigDecimal priorAccrual;
    public final Date lastTransactionDate;
    public final KualiInteger monthEndDateId;
    public final BigDecimal estimatedIncome;
    public final BigDecimal securityUnitVal;
    public final BigDecimal marketValue;
    public final BigDecimal averageMarketValue;
    public final BigDecimal remainderOfFYEstimatedIncome;
    public final BigDecimal nextFYEstimatedIncome;

    private HoldingHistoryFixture(String kemid, String securityId, String registrationCode,
                                  KualiInteger lotNumber, String incomePrincipalIndicator,
                                  Date acquiredDate,BigDecimal units,BigDecimal cost,
                                  BigDecimal currentAccrual, BigDecimal priorAccrual,
                                  Date lastTransactionDate, KualiInteger monthEndDateId,
                                  BigDecimal estimatedIncome, BigDecimal securityUnitVal,
                                  BigDecimal marketValue, BigDecimal averageMarketValue,
                                  BigDecimal remainderOfFYEstimatedIncome, BigDecimal nextFYEstimatedIncome) {
        this.kemid = kemid;
        this.securityId = securityId;
        this.registrationCode = registrationCode;
        this.lotNumber = lotNumber;
        this.incomePrincipalIndicator = incomePrincipalIndicator;
        this.acquiredDate = acquiredDate;
        this.units = units;
        this.cost = cost;
        this.currentAccrual = currentAccrual;
        this.priorAccrual = priorAccrual;
        this.lastTransactionDate = lastTransactionDate;
        this.monthEndDateId = monthEndDateId;
        this.estimatedIncome = estimatedIncome;
        this.securityUnitVal = securityUnitVal;
        this.marketValue = marketValue;
        this.averageMarketValue = averageMarketValue;
        this.remainderOfFYEstimatedIncome = remainderOfFYEstimatedIncome;
        this.nextFYEstimatedIncome = nextFYEstimatedIncome;        
    }

    /**
     * This method creates a Security record and saves it to table
     * @return holdingHistory record
     */
    public HoldingHistory createHoldingHistoryRecord() {
        HoldingHistory holdingHistoryRecord = new HoldingHistory();

        holdingHistoryRecord.setKemid(this.kemid);
        holdingHistoryRecord.setSecurityId(this.securityId);
        holdingHistoryRecord.setRegistrationCode(this.registrationCode);
        holdingHistoryRecord.setLotNumber(this.lotNumber);
        holdingHistoryRecord.setIncomePrincipalIndicator(this.incomePrincipalIndicator);
        holdingHistoryRecord.setAcquiredDate(this.acquiredDate);
        holdingHistoryRecord.setUnits(this.units);
        holdingHistoryRecord.setCost(this.cost);
        holdingHistoryRecord.setCurrentAccrual(this.currentAccrual);
        holdingHistoryRecord.setPriorAccrual(this.priorAccrual);
        holdingHistoryRecord.setLastTransactionDate(this.lastTransactionDate);
        holdingHistoryRecord.setMonthEndDateId(this.monthEndDateId);
        holdingHistoryRecord.setEstimatedIncome(this.estimatedIncome);
        holdingHistoryRecord.setSecurityUnitVal(this.securityUnitVal);
        holdingHistoryRecord.setMarketValue(this.marketValue);
        holdingHistoryRecord.setAverageMarketValue(this.averageMarketValue);
        holdingHistoryRecord.setRemainderOfFYEstimatedIncome(this.remainderOfFYEstimatedIncome);
        holdingHistoryRecord.setNextFYEstimatedIncome(this.nextFYEstimatedIncome);        

        saveHoldingHistoryRecord(holdingHistoryRecord);        
        
        return holdingHistoryRecord;
    }

    /**
     * This method creates a Security record and saves it to table
     * @return holdingHistory record
     */
    public HoldingHistory createHoldingHistoryRecord(String kemid, String securityId, String registrationCode,
                                                     KualiInteger lotNumber, String incomePrincipalIndicator,
                                                     Date acquiredDate,BigDecimal units,BigDecimal cost,
                                                     BigDecimal currentAccrual, BigDecimal priorAccrual,
                                                     Date lastTransactionDate, KualiInteger monthEndDateId,
                                                     BigDecimal estimatedIncome, BigDecimal securityUnitVal,
                                                     BigDecimal marketValue, BigDecimal averageMarketValue,
                                                     BigDecimal remainderOfFYEstimatedIncome, BigDecimal nextFYEstimatedIncome) {
        HoldingHistory holdingHistoryRecord = new HoldingHistory();

        holdingHistoryRecord.setKemid(kemid);
        holdingHistoryRecord.setSecurityId(securityId);
        holdingHistoryRecord.setRegistrationCode(registrationCode);
        holdingHistoryRecord.setLotNumber(lotNumber);
        holdingHistoryRecord.setIncomePrincipalIndicator(incomePrincipalIndicator);
        holdingHistoryRecord.setAcquiredDate(acquiredDate);
        holdingHistoryRecord.setUnits(units);
        holdingHistoryRecord.setCost(cost);
        holdingHistoryRecord.setCurrentAccrual(currentAccrual);
        holdingHistoryRecord.setPriorAccrual(priorAccrual);
        holdingHistoryRecord.setLastTransactionDate(lastTransactionDate);
        holdingHistoryRecord.setMonthEndDateId(monthEndDateId);
        holdingHistoryRecord.setEstimatedIncome(estimatedIncome);
        holdingHistoryRecord.setSecurityUnitVal(securityUnitVal);
        holdingHistoryRecord.setMarketValue(marketValue);
        holdingHistoryRecord.setAverageMarketValue(averageMarketValue);
        holdingHistoryRecord.setRemainderOfFYEstimatedIncome(remainderOfFYEstimatedIncome);
        holdingHistoryRecord.setNextFYEstimatedIncome(nextFYEstimatedIncome);        

        saveHoldingHistoryRecord(holdingHistoryRecord);        
        return holdingHistoryRecord;
    }

    /**
     * Method to save the business object....
     */
    private void saveHoldingHistoryRecord(HoldingHistory holdingHistoryRecord) {
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        businessObjectService.save(holdingHistoryRecord);
    }
}

