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

import org.kuali.kfs.module.endow.businessobject.FeeMethod;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.service.BusinessObjectService;

public enum FeeMethodFixture {
    // Fee Methode Fixture
    FEE_METHOD_RECORD1("TESTFEE1", //code
            "Test Fee Method 1", //name
            "D", //feeFrequencyCode
            Date.valueOf("2010-12-15"), // feeNextProcessDate
            Date.valueOf("2010-12-15"), //feeLastProcessDate
            "C", // feeRateDefinitionCode
            BigDecimal.valueOf(0.0050), // firstFeeRate
            new KualiDecimal(999999999999.99), // firstFeeBreakpoint
            BigDecimal.valueOf(0.0050), // secondFeeRate
            new KualiDecimal(999999999999.99), // secondFeeBreakpoint
            BigDecimal.valueOf(0.00), // thirdFeeRate
            new KualiDecimal(0.00), //minimumFeeThreshold
            new KualiDecimal(1.00), // minimumFeeToCharge
            "T", //feeTypeCode
            "B", //feeBaseCode
            "TST124", //feeExpenseETranCode
            true, //feePostPendingIndicator
            new KualiDecimal(1.20), // corpusPctTolerance
            "AU", //feeBalanceTypeCode
            true, // feeByTransactionType
            true, // feeByETranCode
            true //active
    ),

    FEE_METHOD_RECORD2("TESTFEE2", //code
            "Test Fee Method 2", //name
            "D", //feeFrequencyCode
            Date.valueOf("2010-12-15"), // feeNextProcessDate
            Date.valueOf("2010-12-15"), //feeLastProcessDate
            "V", // feeRateDefinitionCode
            BigDecimal.valueOf(0.0050), // firstFeeRate
            new KualiDecimal(999999999999.99), // firstFeeBreakpoint
            BigDecimal.valueOf(0.05), // secondFeeRate
            new KualiDecimal(999999999999.99), // secondFeeBreakpoint
            BigDecimal.valueOf(0.00), // thirdFeeRate
            new KualiDecimal(0.00), //minimumFeeThreshold
            new KualiDecimal(1.00), // minimumFeeToCharge
            "B", //feeTypeCode
            "B", //feeBaseCode
            "TST124", //feeExpenseETranCode
            true, //feePostPendingIndicator
            new KualiDecimal(1.20), // corpusPctTolerance
            "AU", // feeBalanceTypeCode
            true, // feeByTransactionType
            true, // feeByETranCode
            true // active
    );

    public final String code;
    public final String name;
    public final String feeFrequencyCode;
    public final Date feeNextProcessDate;
    public final Date feeLastProcessDate;
    public final String feeRateDefinitionCode;
    public final BigDecimal firstFeeRate;
    public final KualiDecimal firstFeeBreakpoint;
    public final BigDecimal secondFeeRate;
    public final KualiDecimal secondFeeBreakpoint;
    public final BigDecimal thirdFeeRate;
    public final KualiDecimal minimumFeeThreshold;
    public final KualiDecimal minimumFeeToCharge;
    public final String feeTypeCode;
    public final String feeBaseCode;
    public final String feeExpenseETranCode;
    public final boolean feePostPendingIndicator;
    public final KualiDecimal corpusPctTolerance;
    public final String feeBalanceTypeCode;
    public final boolean feeByTransactionType;
    public final boolean feeByETranCode;
    public final boolean active;

    // default record...
    private FeeMethodFixture(String code, String name, String feeFrequencyCode,
                             Date feeNextProcessDate, Date feeLastProcessDate, String feeRateDefinitionCode,
                             BigDecimal firstFeeRate, KualiDecimal firstFeeBreakpoint,
                             BigDecimal secondFeeRate,KualiDecimal secondFeeBreakpoint,
                             BigDecimal thirdFeeRate, KualiDecimal minimumFeeThreshold,
                             KualiDecimal minimumFeeToCharge, String feeTypeCode,
                             String feeBaseCode, String feeExpenseETranCode,
                             boolean feePostPendingIndicator, KualiDecimal corpusPctTolerance,
                             String feeBalanceTypeCode, boolean active, boolean feeByTransactionType, boolean feeByETranCode) {

        this.code = code;
        this.name = name;
        this.feeFrequencyCode = feeFrequencyCode;
        this.feeNextProcessDate = feeNextProcessDate;
        this.feeLastProcessDate = feeLastProcessDate;
        this.feeRateDefinitionCode = feeRateDefinitionCode;
        this.firstFeeRate = firstFeeRate;
        this.firstFeeBreakpoint = firstFeeBreakpoint;
        this.secondFeeRate = secondFeeRate;
        this.secondFeeBreakpoint = secondFeeBreakpoint;
        this.thirdFeeRate = thirdFeeRate;
        this.minimumFeeThreshold = minimumFeeThreshold;
        this.minimumFeeToCharge = minimumFeeToCharge;
        this.feeTypeCode = feeTypeCode;
        this.feeBaseCode = feeBaseCode;
        this.feeExpenseETranCode = feeExpenseETranCode;
        this.feePostPendingIndicator = feePostPendingIndicator;
        this.corpusPctTolerance = corpusPctTolerance;
        this.feeBalanceTypeCode = feeBalanceTypeCode;
        this.active = active;
        this.feeByETranCode = feeByETranCode;
        this.feeByTransactionType = feeByTransactionType;
    }

    /**
     * This method creates a default Fee Method record and saves it to table
     * 
     * @return feeMethod record
     */
    public FeeMethod createFeeMethodRecord() {
        FeeMethod feeMethod = new FeeMethod();

        feeMethod.setCode(this.code);
        feeMethod.setName(this.name);
        feeMethod.setFeeFrequencyCode(this.feeFrequencyCode);
        feeMethod.setFeeNextProcessDate(this.feeNextProcessDate);
        feeMethod.setFeeLastProcessDate(this.feeLastProcessDate);
        feeMethod.setFeeRateDefinitionCode(this.feeRateDefinitionCode);
        feeMethod.setFirstFeeRate(this.firstFeeRate);
        feeMethod.setFirstFeeBreakpoint(this.firstFeeBreakpoint);
        feeMethod.setSecondFeeRate(this.secondFeeRate);
        feeMethod.setSecondFeeBreakpoint(this.secondFeeBreakpoint);
        feeMethod.setThirdFeeRate(this.thirdFeeRate);
        feeMethod.setMinimumFeeThreshold(this.minimumFeeThreshold);
        feeMethod.setMinimumFeeToCharge(this.minimumFeeToCharge);
        feeMethod.setFeeTypeCode(this.feeTypeCode);
        feeMethod.setFeeBaseCode(this.feeBaseCode);
        feeMethod.setFeeExpenseETranCode(this.feeExpenseETranCode);
        feeMethod.setFeePostPendingIndicator(this.feePostPendingIndicator);
        feeMethod.setCorpusPctTolerance(this.corpusPctTolerance);
        feeMethod.setFeeBalanceTypeCode(this.feeBalanceTypeCode);
        feeMethod.setActive(this.active);
        feeMethod.setFeeByETranCode(this.feeByETranCode);
        feeMethod.setFeeByTransactionType(this.feeByTransactionType);
        saveFeeMethodRecord(feeMethod);
        
        return feeMethod;
    }

    /**
     * Method to save the business object....
     */
    private void saveFeeMethodRecord(FeeMethod feeMethod) {
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        businessObjectService.save(feeMethod);
    }
}
