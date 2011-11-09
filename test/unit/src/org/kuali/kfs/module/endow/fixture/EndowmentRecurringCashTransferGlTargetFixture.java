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


