/*
 * Copyright 2011 The Kuali Foundation.
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

import org.kuali.kfs.module.endow.businessobject.CashSweepModel;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.BusinessObjectService;

public enum CashSweepModelFixture {

    PRINCIPAL_PURCHASE_DATA(
            new Integer(888),
            "Pooled Short Term Investment - Daily",
            new BigDecimal(100),
            "TSTSECID1",
            "RC1",
            new BigDecimal(100),
            "TSTSECID2",
            "RC2",
            "D",
            Date.valueOf("2010-11-10"),
            Date.valueOf("2010-11-10"),
            true
    ),
    PRINCIPAL_SALE_DATA(
            new Integer(999),
            "Pooled Short Term Investment - Daily",
            new BigDecimal(1500),
            "TSTSECID1",
            "RC1",
            new BigDecimal(1500),
            "TSTSECID2",
            "RC2",
            "D",
            Date.valueOf("2010-11-10"),
            Date.valueOf("2010-11-10"),
            true
    );
    
    private Integer cashSweepModelID;
    private String  cashSweepModelName;
    private BigDecimal sweepIncomeCashLimit;
    private String incomeSweepInvestment;
    private String incomeSweepRegistrationCode;    
    private BigDecimal sweepPrincipleCashLimit;
    private String principleSweepInvestment;
    private String principleSweepRegistrationCode;
    private String cashSweepFrequencyCode;
    private Date cashSweepNextDueDate;
    private Date dateOfLastSweepModelChange;    
    private boolean active;
    
    private CashSweepModelFixture(    
            Integer cashSweepModelID,
            String  cashSweepModelName,
            BigDecimal sweepIncomeCashLimit,
            String incomeSweepInvestment,
            String incomeSweepRegistrationCode,    
            BigDecimal sweepPrincipleCashLimit,
            String principleSweepInvestment,
            String principleSweepRegistrationCode,
            String cashSweepFrequencyCode,
            Date cashSweepNextDueDate,
            Date dateOfLastSweepModelChange,    
            boolean active) {
      
        this.cashSweepModelID = cashSweepModelID;
        this.cashSweepModelName = cashSweepModelName;
        this.sweepIncomeCashLimit = sweepIncomeCashLimit;
        this.incomeSweepInvestment = incomeSweepInvestment;
        this.incomeSweepRegistrationCode = incomeSweepRegistrationCode;   
        this.sweepPrincipleCashLimit = sweepPrincipleCashLimit;
        this.principleSweepInvestment = principleSweepInvestment;
        this.principleSweepRegistrationCode = principleSweepRegistrationCode;
        this.cashSweepFrequencyCode = cashSweepFrequencyCode;
        this.cashSweepNextDueDate = cashSweepNextDueDate;
        this.dateOfLastSweepModelChange = dateOfLastSweepModelChange;    
        this.active = active;
    }
    
    public CashSweepModel createCashSweepModel() {
        
        CashSweepModel cashModelSweep = new CashSweepModel();
        
        cashModelSweep.setCashSweepModelID(cashSweepModelID);
        cashModelSweep.setCashSweepModelName(cashSweepModelName);
        cashModelSweep.setSweepIncomeCashLimit(sweepIncomeCashLimit);
        cashModelSweep.setIncomeSweepInvestment(incomeSweepInvestment);
        cashModelSweep.setIncomeSweepRegistrationCode(incomeSweepRegistrationCode);
        cashModelSweep.setSweepPrincipleCashLimit(sweepPrincipleCashLimit);
        cashModelSweep.setPrincipleSweepInvestment(principleSweepInvestment);
        cashModelSweep.setPrincipleSweepRegistrationCode(principleSweepRegistrationCode);
        cashModelSweep.setCashSweepFrequencyCode(cashSweepFrequencyCode);
        cashModelSweep.setCashSweepNextDueDate(cashSweepNextDueDate);
        cashModelSweep.setDateOfLastSweepModelChange(dateOfLastSweepModelChange);
        cashModelSweep.setActive(active);        
        
        saveAutomatedCashInvestmentModel(cashModelSweep);
        
        return cashModelSweep;        
    }
    
    /**
     * Method to save the business object....
     */
    private void saveAutomatedCashInvestmentModel(CashSweepModel cashSweepModel) {
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        businessObjectService.save(cashSweepModel);
    }
    
}
