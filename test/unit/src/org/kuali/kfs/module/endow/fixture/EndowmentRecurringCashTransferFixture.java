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


