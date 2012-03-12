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

import java.sql.Date;

import org.kuali.kfs.module.endow.businessobject.KemidFee;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.krad.service.BusinessObjectService;

public enum KemIdFeeFixture {
    // KemId Fee Fixture Record
    
    KEMID_FEE_RECORD1("TESTKEMID", //kemid
            new KualiInteger(1), //feeMethodSeq
            "TESTFEE1", // feeMethodCode
            "TESTKEMID", // chargeFeeToKemid
            new KualiDecimal(0.80), // percentOfFeeChargedToIncome
            new KualiDecimal(0.20), // percentOfFeeChargedToIncome
            true, //accrueFees
            new KualiDecimal(10.00), // totalAccruedFees
            true, //waiveFees
            new KualiDecimal(10.00), // totalWaivedFeesThisFiscalYear
            new KualiDecimal(10.00), // totalWaivedFees
            Date.valueOf("2006-02-01"), // feeStartDate
            Date.valueOf("2007-02-01") // feeEndDate
    );
    
    public final String kemid;
    public final String feeMethodCode;
    public final KualiInteger feeMethodSeq;
    public final String chargeFeeToKemid;
    public final KualiDecimal percentOfFeeChargedToIncome;
    public final KualiDecimal percentOfFeeChargedToPrincipal;
    public final boolean accrueFees;
    public final KualiDecimal totalAccruedFees;
    public final boolean waiveFees;
    public final KualiDecimal totalWaivedFeesThisFiscalYear;
    public final KualiDecimal totalWaivedFees;
    public final Date feeStartDate;
    public final Date feeEndDate;
    
    private KemIdFeeFixture(String kemid, KualiInteger feeMethodSeq, String feeMethodCode, 
                            String chargeFeeToKemid, KualiDecimal percentOfFeeChargedToIncome, 
                            KualiDecimal percentOfFeeChargedToPrincipal, boolean accrueFees, 
                            KualiDecimal totalAccruedFees, boolean waiveFees, KualiDecimal totalWaivedFeesThisFiscalYear, 
                            KualiDecimal totalWaivedFees, Date feeStartDate, Date feeEndDate) {
        this.kemid = kemid;
        this.feeMethodCode = feeMethodCode;
        this.feeMethodSeq = feeMethodSeq;
        this.chargeFeeToKemid = chargeFeeToKemid;
        this.percentOfFeeChargedToIncome = percentOfFeeChargedToIncome;
        this.percentOfFeeChargedToPrincipal = percentOfFeeChargedToPrincipal;
        this.accrueFees = accrueFees;
        this.totalAccruedFees = totalAccruedFees;
        this.waiveFees = waiveFees;
        this.totalWaivedFeesThisFiscalYear = totalWaivedFeesThisFiscalYear;
        this.totalWaivedFees = totalWaivedFees;
        this.feeStartDate = feeStartDate;
        this.feeEndDate = feeEndDate;
    }

    /**
     * This method creates a Security record and saves it to table
     * @return current cash record
     */
    public KemidFee createKemidFeeRecord() {
        KemidFee kemidFee = new KemidFee();

        kemidFee.setKemid(this.kemid);
        kemidFee.setFeeMethodCode(this.feeMethodCode);
        kemidFee.setFeeMethodSeq(this.feeMethodSeq);
        kemidFee.setChargeFeeToKemid(this.chargeFeeToKemid);
        kemidFee.setPercentOfFeeChargedToIncome(this.percentOfFeeChargedToIncome);
        kemidFee.setPercentOfFeeChargedToPrincipal(this.percentOfFeeChargedToPrincipal);
        kemidFee.setAccrueFees(this.accrueFees);
        kemidFee.setTotalAccruedFees(this.totalAccruedFees);
        kemidFee.setWaiveFees(this.waiveFees);
        kemidFee.setTotalWaivedFeesThisFiscalYear(this.totalWaivedFeesThisFiscalYear);
        kemidFee.setTotalWaivedFees(this.totalWaivedFees);
        kemidFee.setFeeStartDate(this.feeStartDate);
        kemidFee.setFeeEndDate(this.feeEndDate);

        saveKemidFeeRecord(kemidFee);
        
        return kemidFee;
    }
    
    /**
     * Method to save the business object....
     */
    private void saveKemidFeeRecord(KemidFee kemidFee) {
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        businessObjectService.save(kemidFee);
    }
}

