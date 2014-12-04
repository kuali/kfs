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

import org.kuali.kfs.module.endow.businessobject.EndowmentRecurringCashTransferGLTarget;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public enum EndowmentRecurringCashTransferGlTargetFixture {
    VALID_GL_TARGET_1("1", // Target Sequence Number     
            "BL",                   // Target ChartOfAccountsCode
            "1023200",              // Target Accounts Number
            null,                   // Target SubAccount Number
            "1800",                 // Target Financial ObjectCode
            null,                   // Target SubObject Code                
            null,                   // Target ProjectCode:
            null,                   // Org Ref Id
            null,                   // Target Fdoc Line Amount
            new KualiDecimal(0.1),  // Target Percent
            null,                   // Target Use Etran Code
            true                    // Active Indicator
    ),
    
    VALID_GL_TARGET_2("2", // Target Sequence Number     
            "BL",                   // Target ChartOfAccountsCode
            "1024600",              // Target Accounts Number
            null,                   // Target SubAccount Number
            "1800",                 // Target Financial ObjectCode
            null,                   // Target SubObject Code                
            null,                   // Target ProjectCode:
            null,                   // Org Ref Id
            null,                   // Target Fdoc Line Amount
            new KualiDecimal(0.1),  // Target Percent
            null,                   // Target Use Etran Code
            true                    // Active Indicator
    ),
    
    VALID_GL_TARGET_3("1", // Target Sequence Number     
            "BL",                   // Target ChartOfAccountsCode
            "2924608",              // Target Accounts Number
            null,                   // Target SubAccount Number
            "1800",                 // Target Financial ObjectCode
            null,                   // Target SubObject Code                
            null,                   // Target ProjectCode:
            null,                   // Org Ref Id
            new KualiDecimal(20.00),// Target Fdoc Line Amount
            null,                     // Target Percent
            null,                   // Target Use Etran Code
            true                    // Active Indicator
    );
    
    private String targetSequenceNumber;
    private String targetChartOfAccountsCode;
    private String targetAccountsNumber;
    private String targetFinancialObjectCode;
    private KualiDecimal targetFdocLineAmount;
    private String targetSubAccountNumber;
    private String targetFinancialSubObjectCode;
    private String targetProjectCode;
    private String targetOrgReferenceId; 
    private KualiDecimal targetPercent;
    private String targetUseEtranCode;
    private boolean active;
    
    private EndowmentRecurringCashTransferGlTargetFixture(String targetSequenceNumber, String targetChartOfAccountsCode, String targetAccountsNumber, String targetSubAccountNumber, String targetFinancialObjectCode, String targetFinancialSubObjectCode, String targetProjectCode, String targetOrgReferenceId,  KualiDecimal targetFdocLineAmount, KualiDecimal targetPercent, String targetUseEtranCode, boolean active) {
        this.targetSequenceNumber = targetSequenceNumber;
        this.targetChartOfAccountsCode = targetChartOfAccountsCode;
        this.targetAccountsNumber = targetAccountsNumber;
        this.targetFinancialObjectCode = targetFinancialObjectCode;
        this.targetFdocLineAmount = targetFdocLineAmount;
        this.targetSubAccountNumber = targetSubAccountNumber;
        this.targetFinancialSubObjectCode = targetFinancialSubObjectCode;
        this.targetProjectCode = targetProjectCode;
        this.targetOrgReferenceId = targetOrgReferenceId;
        this.targetPercent = targetPercent;
        this.targetUseEtranCode = targetUseEtranCode;
        this.active = active;
    }
    
    public EndowmentRecurringCashTransferGLTarget createEndowmentRecurringCashTransferGlTarget(){
        EndowmentRecurringCashTransferGLTarget glTarget = new EndowmentRecurringCashTransferGLTarget();
        glTarget.setTargetSequenceNumber(this.targetSequenceNumber);
        glTarget.setTargetChartOfAccountsCode(this.targetChartOfAccountsCode);
        glTarget.setTargetAccountsNumber(this.targetAccountsNumber);
        glTarget.setTargetFinancialObjectCode(this.targetFinancialObjectCode);
        glTarget.setTargetFdocLineAmount(this.targetFdocLineAmount);
        glTarget.setTargetSubAccountNumber(this.targetSubAccountNumber);
        glTarget.setTargetFinancialSubObjectCode(this.targetFinancialSubObjectCode);
        glTarget.setTargetProjectCode(this.targetProjectCode);
        glTarget.setTargetOrgReferenceId(this.targetOrgReferenceId);
        glTarget.setTargetPercent(this.targetPercent);
        glTarget.setTargetUseEtranCode(this.targetUseEtranCode);
        glTarget.setActive(this.active);
        return glTarget;
    }
    
}


