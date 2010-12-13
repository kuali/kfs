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

import org.kuali.kfs.module.endow.businessobject.EndowmentRecurringCashTransfer;

public enum EndowmentRecurringCashTransferFixture {
    VALID_ECT_SOURCE_1("12",  // Transfer Number
            "037O009148",   // Source Kemid
            "ECT",          // Transaction Type
            "42000",        // Source Etran Code
            "Unit Test Source",         // Source Line Description 
            "I",                        // Source Income Or Principal
            "D",                        // Frequency Code
            Date.valueOf("2010-01-02"), // Next Process Date
            Date.valueOf("2010-01-01"), // Last Process Date   
            true                        // Active Indicator
    ),
    
    VALID_ECT_SOURCE_2("34",  // Transfer Number
            "038D008041",   // Source Kemid
            "ECT",          // Transaction Type
            "45000",        // Source Etran Code
            "Unit Test Source",         // Source Line Description 
            "I",                        // Source Income Or Principal
            "D",                        // Frequency Code
            Date.valueOf("2010-01-02"), // Next Process Date
            Date.valueOf("2010-11-07"), // Last Process Date   
            true                        // Active Indicator
    ),
    
    VALID_EGLT_SOURCE_1("16",  // Transfer Number
            "038AS06237",   // Source Kemid
            "EGLT",          // Transaction Type
            "42020",        // Source Etran Code
            "Unit Test Source",         // Source Line Description 
            "I",                        // Source Income Or Principal
            "D",                        // Frequency Code
            Date.valueOf("2010-01-02"), // Next Process Date
            Date.valueOf("2010-11-07"), // Last Process Date   
            true                        // Active Indicator
    ),
    
    VALID_EGLT_SOURCE_2("17",  // Transfer Number
            "037E009727",   // Source Kemid
            "EGLT",          // Transaction Type
            "42020",        // Source Etran Code
            "Unit Test Source",         // Source Line Description 
            "P",                        // Source Income Or Principal
            "D",                        // Frequency Code
            Date.valueOf("2010-01-02"), // Next Process Date
            Date.valueOf("2010-11-07"), // Last Process Date   
            true                        // Active Indicator
    );

    
    
    private String transferNumber;
    private String sourceKemid;
    private String transactionType;
    private String sourceEtranCode;
    private String sourceLineDescription;
    private String sourceIncomeOrPrincipal;
    private String frequencyCode;
    private Date nextProcessDate;
    private Date lastProcessDate;
    private boolean active;
    
    private EndowmentRecurringCashTransferFixture(String transferNumber, String sourceKemid, String transactionType, String sourceEtranCode, String sourceLineDescription, String sourceIncomeOrPrincipal, String frequencyCode, Date nextProcessDate,  Date lastProcessDate, boolean active) {
        this.transferNumber = transferNumber;
        this.sourceKemid = sourceKemid;
        this.transactionType = transactionType;
        this.sourceEtranCode = sourceEtranCode;
        this.sourceLineDescription = sourceLineDescription;
        this.sourceIncomeOrPrincipal = sourceIncomeOrPrincipal;
        this.frequencyCode = frequencyCode;
        this.nextProcessDate = nextProcessDate;
        this.lastProcessDate = lastProcessDate;
        this.active = active;
        
    }
    
    public EndowmentRecurringCashTransfer createEndowmentRecurringCashTransfer() {
        EndowmentRecurringCashTransfer endowmentRecurringCashTransfer = new EndowmentRecurringCashTransfer();
        endowmentRecurringCashTransfer.setTransferNumber(this.transferNumber);
        endowmentRecurringCashTransfer.setSourceKemid(this.sourceKemid);
        endowmentRecurringCashTransfer.setTransactionType(this.transactionType);
        endowmentRecurringCashTransfer.setSourceEtranCode(this.sourceEtranCode);
        endowmentRecurringCashTransfer.setSourceLineDescription(this.sourceLineDescription);
        endowmentRecurringCashTransfer.setSourceIncomeOrPrincipal(this.sourceIncomeOrPrincipal);
        endowmentRecurringCashTransfer.setFrequencyCode(this.frequencyCode);
        endowmentRecurringCashTransfer.setNextProcessDate(this.nextProcessDate);
        endowmentRecurringCashTransfer.setLastProcessDate(this.lastProcessDate);
        endowmentRecurringCashTransfer.setActive(this.active);
        
        return endowmentRecurringCashTransfer;
    }

}


