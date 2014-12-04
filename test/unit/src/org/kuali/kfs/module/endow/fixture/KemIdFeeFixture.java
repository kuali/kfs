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

