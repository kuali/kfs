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
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.BusinessObjectService;

public enum SecurityFixture {
    // Security Record FIXTURE
    ENDOWMENT_SECURITY_RECORD("TESTSECID", // securityId
            "025", // securityClassCode
            BigDecimal.ONE, // securityUnitValue
            "M01", // securityIncomePayFrequencyCode
            Date.valueOf("2010-01-01"), // securityIncomeNextPayDate
            BigDecimal.valueOf(20L), // securityRate
            true, // rowActiveIndicator
            BigDecimal.valueOf(100.20) // nextFiscalYearDisbursementAmount
    ), ACTIVE_SECURITY(EndowTestConstants.TEST_SEC_ID, // securityId
            "TST", // securityClassCode
            BigDecimal.ONE, // securityUnitValue
            "M01", // securityIncomePayFrequencyCode
            Date.valueOf("2010-01-01"), // securityIncomeNextPayDate
            EndowTestConstants.SECURITY_RATE, // securityRate
            true, // rowActiveIndicator
            BigDecimal.valueOf(100.20) // nextFiscalYearDisbursementAmount
    ), INACTIVE_SECURITY("TESTSECID", // securityId
            "TST", // securityClassCode
            BigDecimal.ONE, // securityUnitValue
            "M01", // securityIncomePayFrequencyCode
            Date.valueOf("2010-01-01"), // securityIncomeNextPayDate
            BigDecimal.valueOf(20L), // securityRate
            false, // rowActiveIndicator
            BigDecimal.valueOf(100.20) // nextFiscalYearDisbursementAmount
    ), 
    
    LIABILITY_INCREASE_ACTIVE_SECURITY("TESTSECID", // securityId
            "AAA", // securityClassCode
            BigDecimal.ONE, // securityUnitValue
            "M01", // securityIncomePayFrequencyCode
            Date.valueOf("2010-01-01"), // securityIncomeNextPayDate
            BigDecimal.valueOf(20L), // securityRate
            true, // rowActiveIndicator
            BigDecimal.valueOf(100.20) // nextFiscalYearDisbursementAmount    
    ),
    
    CORPORATE_REORGANIZATION_SOURCE_SECURITY("TESTSECID", // securityId
            "AAA", // securityClassCode
            BigDecimal.ONE, // securityUnitValue
            "M01", // securityIncomePayFrequencyCode
            Date.valueOf("2010-01-01"), // securityIncomeNextPayDate
            BigDecimal.valueOf(20L), // securityRate
            true, // rowActiveIndicator
            BigDecimal.valueOf(100.20) // nextFiscalYearDisbursementAmount    
    ),
    
    CORPORATE_REORGANIZATION_TARGET_SECURITY("TESTSEC2", // securityId
            "AAA", // securityClassCode
            BigDecimal.ONE, // securityUnitValue
            "M01", // securityIncomePayFrequencyCode
            Date.valueOf("2010-01-01"), // securityIncomeNextPayDate
            BigDecimal.valueOf(20L), // securityRate
            true, // rowActiveIndicator
            BigDecimal.valueOf(100.20) // nextFiscalYearDisbursementAmount    
    ),
    
    ALTERNATIVE_INVEST_ACTIVE_SECURITY("TESTSECID", // securityId
            "TST", // securityClassCode
            BigDecimal.ONE, // securityUnitValue
            "M01", // securityIncomePayFrequencyCode
            Date.valueOf("2010-01-01"), // securityIncomeNextPayDate
            BigDecimal.valueOf(20L), // securityRate
            true, // rowActiveIndicator
            BigDecimal.valueOf(100.20) // nextFiscalYearDisbursementAmount    
    ),
    
    HOLDING_HISTORY_VALUE_ADJUSTMENT_ACTIVE_SECURITY("TESTSECID", // securityId
            "AAA", // securityClassCode
            BigDecimal.ONE, // securityUnitValue
            "M01", // securityIncomePayFrequencyCode
            Date.valueOf("2010-01-01"), // securityIncomeNextPayDate
            BigDecimal.valueOf(20L), // securityRate
            true, // rowActiveIndicator
            BigDecimal.valueOf(100.20) // nextFiscalYearDisbursementAmount
    ), 
    
    HOLDING_HISTORY_VALUE_ADJUSTMENT_ACTIVE_SECURITY_2("TESTSEC2", // securityId
            "ABC", // securityClassCode
            BigDecimal.ONE, // securityUnitValue
            "M01", // securityIncomePayFrequencyCode
            Date.valueOf("2010-01-01"), // securityIncomeNextPayDate
            BigDecimal.valueOf(20L), // securityRate
            true, // rowActiveIndicator
            BigDecimal.valueOf(100.20) // nextFiscalYearDisbursementAmount   
    ),
    
    ACTIVE_SECURITY_COMMITTED("DUMMYID", // securityId
            "CBA", // securityClassCode
            BigDecimal.ONE, // securityUnitValue
            "M01", // securityIncomePayFrequencyCode
            Date.valueOf("2010-01-01"), // securityIncomeNextPayDate
            EndowTestConstants.SECURITY_RATE, // securityRate
            true, // rowActiveIndicator
            BigDecimal.valueOf(100.20) // nextFiscalYearDisbursementAmount
    ),
    ENDOWMENT_ASSET_SECURITY_RECORD("TESTSECID", // securityId
            "EAS", // securityClassCode
            BigDecimal.ONE, // securityUnitValue
            "M01", // securityIncomePayFrequencyCode
            Date.valueOf("2010-01-01"), // securityIncomeNextPayDate
            BigDecimal.valueOf(20L), // securityRate
            true, // rowActiveIndicator
            BigDecimal.valueOf(100.20) // nextFiscalYearDisbursementAmount
    ), 
    ENDOWMENT_ASSET_INCOME_SECURITY_RECORD("TSTSECID1", // securityId
            "EAS", // securityClassCode
            BigDecimal.ONE, // securityUnitValue
            "M01", // securityIncomePayFrequencyCode
            Date.valueOf("2010-01-01"), // securityIncomeNextPayDate
            BigDecimal.valueOf(20L), // securityRate
            true, // rowActiveIndicator
            BigDecimal.valueOf(100.20) // nextFiscalYearDisbursementAmount
    ),
    ENDOWMENT_ASSET_PRINCIPAL_SECURITY_RECORD("TSTSECID2", // securityId
            "EAS", // securityClassCode
            BigDecimal.ONE, // securityUnitValue
            "M01", // securityIncomePayFrequencyCode
            Date.valueOf("2010-01-01"), // securityIncomeNextPayDate
            BigDecimal.valueOf(20L), // securityRate
            true, // rowActiveIndicator
            BigDecimal.valueOf(100.20) // nextFiscalYearDisbursementAmount
    );

    public final String securityId;
    public final String securityClassCode;
    public final BigDecimal securityUnitValue;
    public final String securityIncomePayFrequencyCode;
    public final Date securityIncomeNextPayDate;
    public final BigDecimal securityRate;
    public final Boolean rowActiveIndicator;
    public final BigDecimal nextFiscalYearDisbursementAmount;

    private SecurityFixture(String securityId, String securityClassCode, BigDecimal securityUnitValue, String securityIncomePayFrequencyCode, Date securityIncomeNextPayDate, BigDecimal securityRate, Boolean rowActiveIndicator, BigDecimal nextFiscalYearDisbursementAmount) {
        this.securityId = securityId;
        this.securityClassCode = securityClassCode;
        this.securityUnitValue = securityUnitValue;
        this.securityIncomePayFrequencyCode = securityIncomePayFrequencyCode;
        this.securityIncomeNextPayDate = securityIncomeNextPayDate;
        this.securityRate = securityRate;
        this.rowActiveIndicator = rowActiveIndicator;
        this.nextFiscalYearDisbursementAmount = nextFiscalYearDisbursementAmount;
    }

    /**
     * This method creates a Security record and saves it to table
     * @return security record
     */
    public Security createSecurityRecord() {
        Security securityRecord = new Security();
        securityRecord.setId(this.securityId);
        securityRecord.setSecurityClassCode(this.securityClassCode);
        securityRecord.setUnitValue(this.securityUnitValue);
        securityRecord.setIncomePayFrequency(this.securityIncomePayFrequencyCode);
        securityRecord.setIncomeNextPayDate(this.securityIncomeNextPayDate);
        securityRecord.setIncomeRate(this.securityRate);
        securityRecord.setActive(this.rowActiveIndicator);
        securityRecord.setNextFiscalYearDistributionAmount(this.nextFiscalYearDisbursementAmount);
        securityRecord.refreshReferenceObject("classCode");

        saveSecurityRecord(securityRecord);
        
        return securityRecord;
    }
    
    /**
     * This method creates a Security record and saves it to table
     * @return security record
     */
    public Security createSecurityRecord(String securityId, String securityClassCode, BigDecimal securityUnitValue,
                                         String securityIncomePayFrequencyCode, Date  securityIncomeNextPayDate,
                                         BigDecimal securityRate, Boolean rowActiveIndicator, BigDecimal nextFiscalYearDisbursementAmount) {
        Security securityRecord = new Security();
        securityRecord.setId(securityId);
        securityRecord.setSecurityClassCode(securityClassCode);
        securityRecord.setUnitValue(securityUnitValue);
        securityRecord.setIncomePayFrequency(securityIncomePayFrequencyCode);
        securityRecord.setIncomeNextPayDate(securityIncomeNextPayDate);
        securityRecord.setIncomeRate(securityRate);
        securityRecord.setActive(rowActiveIndicator);
        securityRecord.setNextFiscalYearDistributionAmount(nextFiscalYearDisbursementAmount);
        securityRecord.refreshReferenceObject("classCode");
        
        saveSecurityRecord(securityRecord);
        
        return securityRecord;
    }
    
    /**
     * Method to save the business object....
     */
    private void saveSecurityRecord(Security securityRecord) {
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        businessObjectService.save(securityRecord);
    }

}
