/*
 * Copyright 2010 The Kuali Foundation.
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

import org.kuali.kfs.module.endow.businessobject.ClassCode;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.BusinessObjectService;

public enum ClassCodeFixture {

    LIABILITY_CLASS_CODE_CURRENT_TAX_LOT("TST", // code
            "Test Liability Class Code", // desc
            "TST12",// securityReportingGrp;
            "A",// securityAccrualMethod;
            "TST123", // securityEndowmentTransactionCode;
            "TST123", // securityIncomeEndowmentTransactionPostCode;
            true, // taxLotIndicator;
            "O",// classCodeType;
            "M"// valuationMethod;
    ),

    LIABILITY_CLASS_CODE("TST", // code
            "Test Liability Class Code", // desc
            "TST12",// securityReportingGrp;
            "A",// securityAccrualMethod;
            "TST123", // securityEndowmentTransactionCode;
            "TST123", // securityIncomeEndowmentTransactionPostCode;
            true, // taxLotIndicator;
            "L",// classCodeType;
            "M"// valuationMethod;
    ),

    NOT_LIABILITY_CLASS_CODE("TST", // code
            "Test Liability Class Code", // desc
            "TST12",// securityReportingGrp;
            "A",// securityAccrualMethod;
            "TST123", // securityEndowmentTransactionCode;
            "TST123", // securityIncomeEndowmentTransactionPostCode;
            true, // taxLotIndicator;
            "B",// classCodeType;
            "M"// valuationMethod;
    ),

    LIABILITY_INCREASE_LIABILITY_CLASS_CODE_2("AAA", // code dummy record
            "Test Liability Class Code", // desc
            "TST12",// securityReportingGrp;
            "A",// securityAccrualMethod;
            "TST123", // securityEndowmentTransactionCode;
            "TST123", // securityIncomeEndowmentTransactionPostCode;
            true, // taxLotIndicator;
            "L",// classCodeType;
            "M"// valuationMethod;
    ),

    LIABILITY_INCREASE_NOT_LIABILITY_CLASS_CODE_2("ABC", // code dummy record
            "Test Liability Class Code", // desc
            "TST12",// securityReportingGrp;
            "A",// securityAccrualMethod;
            "TST123", // securityEndowmentTransactionCode;
            "TST123", // securityIncomeEndowmentTransactionPostCode;
            true, // taxLotIndicator;
            "B",// classCodeType;
            "M"// valuationMethod;
    ),

    ALTERNATIVE_INVESTMENT_CLASS_CODE("TST", // code dummy record
            "Test Alternative Investments Class Code", // desc
            "TST12",// securityReportingGrp;
            "A",// securityAccrualMethod;
            "TST123", // securityEndowmentTransactionCode;
            "TST123", // securityIncomeEndowmentTransactionPostCode;
            true, // taxLotIndicator;
            "A",// classCodeType;
            "M"// valuationMethod;
    ),

    TEST_CLASS_CODE("TST", // code dummy record
            "Test Class Code", // desc
            "TST12",// securityReportingGrp;
            "A",// securityAccrualMethod;
            "TST123", // securityEndowmentTransactionCode;
            "TST123", // securityIncomeEndowmentTransactionPostCode;
            true, // taxLotIndicator;
            "A",// classCodeType;
            "M"// valuationMethod;
    ),

    HOLDING_HISTORY_VALUE_ADJUSTMENT_CLASS_CODE_2("AAA", // code dummy record
            "Test Liability Class Code", // desc
            "TST12",// securityReportingGrp;
            "A",// securityAccrualMethod;
            "TST123", // securityEndowmentTransactionCode;
            "TST123", // securityIncomeEndowmentTransactionPostCode;
            true, // taxLotIndicator;
            "L",// classCodeType;
            "M"// valuationMethod;
    ),

    HOLDING_HISTORY_VALUE_ADJUSTMENT_NOT_LIABILITY_CLASS_CODE_2("ABC", // code dummy record
            "Test Liability Class Code", // desc
            "TST12",// securityReportingGrp;
            "A",// securityAccrualMethod;
            "TST123", // securityEndowmentTransactionCode;
            "TST123", // securityIncomeEndowmentTransactionPostCode;
            true, // taxLotIndicator;
            "B",// classCodeType;
            "M"// valuationMethod;
    ),

    ACCRUAL_PROCESSING_CLASS_CODE("TST", // code dummy record
            "Test accrual method Class Code for accrual processing", // desc
            "TST12",// securityReportingGrp;
            "A",// securityAccrualMethod;
            "TST123", // securityEndowmentTransactionCode;
            "TST123", // securityIncomeEndowmentTransactionPostCode;
            true, // taxLotIndicator;
            "A",// classCodeType;
            "M"// valuationMethod;
    ),
    
    CAPITAL_GAIN_LOSS_COMMITTED("CBA", // code dummy record
            "Test accrual method Class Code for accrual processing", // desc
            "12TST",// securityReportingGrp;
            "A",// securityAccrualMethod;
            "123TST", // securityEndowmentTransactionCode;
            "123TST", // securityIncomeEndowmentTransactionPostCode;
            true, // taxLotIndicator;
            "A",// classCodeType;
            "M"// valuationMethod;
            
    ),

    ASSET_CLASS_CODE("EAS", // code
            "Test Asset Class Code", // desc
            "TST12",// securityReportingGrp;
            "A",// securityAccrualMethod;
            "TST123", // securityEndowmentTransactionCode;
            "TST123", // securityIncomeEndowmentTransactionPostCode;
            true, // taxLotIndicator;
            "P",// classCodeType;
            "M"// valuationMethod;
    );

    public final String code;
    public final String description;
    public final String securityReportingGrp;
    public final String securityAccrualMethod;
    public final String securityEndowmentTransactionCode;
    public final String securityIncomeEndowmentTransactionPostCode;
    public final boolean taxLotIndicator;
    public final String classCodeType;
    public final String valuationMethod;

    private ClassCodeFixture(String code, String description, String securityReportingGrp, String securityAccrualMethod, String securityEndowmentTransactionCode, String securityIncomeEndowmentTransactionPostCode, boolean taxLotIndicator, String classCodeType, String valuationMethod) {
        this.code = code;
        this.description = description;
        this.securityReportingGrp = securityReportingGrp;
        this.securityAccrualMethod = securityAccrualMethod;
        this.securityEndowmentTransactionCode = securityEndowmentTransactionCode;
        this.securityIncomeEndowmentTransactionPostCode = securityIncomeEndowmentTransactionPostCode;
        this.taxLotIndicator = taxLotIndicator;
        this.classCodeType = classCodeType;
        this.valuationMethod = valuationMethod;
    }

    /**
     * This method creates a ClassCode record and saves it to the DB table.
     * 
     * @return ClassCode record
     */
    public ClassCode createClassCodeRecord() {
        ClassCode classCodeRecord = new ClassCode();

        classCodeRecord.setCode(this.code);
        classCodeRecord.setClassCodeType(this.classCodeType);
        classCodeRecord.setName(this.description);
        classCodeRecord.setSecurityReportingGrp(this.securityReportingGrp);
        classCodeRecord.setSecurityAccrualMethod(this.securityAccrualMethod);
        classCodeRecord.setSecurityEndowmentTransactionCode(this.securityEndowmentTransactionCode);
        classCodeRecord.setSecurityIncomeEndowmentTransactionPostCode(this.securityIncomeEndowmentTransactionPostCode);
        classCodeRecord.setTaxLotIndicator(this.taxLotIndicator);
        classCodeRecord.setClassCodeType(this.classCodeType);
        classCodeRecord.setValuationMethod(this.valuationMethod);

        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        businessObjectService.save(classCodeRecord);

        return classCodeRecord;
    }

}
