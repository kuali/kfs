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

import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.BusinessObjectService;

public enum SecurityFixture {
    // Security Record FIXTURE
    ENDOWMENT_SECURITY_RECORD("TESTSECID", //securityId 
            "025", //securityClassCode
            BigDecimal.ONE, //securityUnitValue
            "M01", //securityIncomePayFrequencyCode
            Date.valueOf("2010-01-01"), //securityIncomeNextPayDate
            BigDecimal.valueOf(20L), //securityRate
            true, //rowActiveIndicator
            BigDecimal.valueOf(100.20) //nextFiscalYearDisbursementAmount
    );
   
    public final String securityId;
    public final String securityClassCode;
    public final BigDecimal securityUnitValue;
    public final String securityIncomePayFrequencyCode;
    public final Date  securityIncomeNextPayDate;
    public final BigDecimal securityRate;
    public final Boolean rowActiveIndicator;
    public final BigDecimal nextFiscalYearDisbursementAmount;

    private SecurityFixture(String securityId, String securityClassCode, BigDecimal securityUnitValue,
                            String securityIncomePayFrequencyCode, Date  securityIncomeNextPayDate,
                            BigDecimal securityRate, Boolean rowActiveIndicator, BigDecimal nextFiscalYearDisbursementAmount) {
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
     * @param clazz
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
        
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        businessObjectService.save(securityRecord);
        
        return securityRecord;
    }
}

