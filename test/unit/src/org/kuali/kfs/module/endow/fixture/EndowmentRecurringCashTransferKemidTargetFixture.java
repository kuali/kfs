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

import org.kuali.kfs.module.endow.businessobject.EndowmentRecurringCashTransferKEMIDTarget;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public enum EndowmentRecurringCashTransferKemidTargetFixture {
    VALID_KEMID_TARGET_1("1",     // Target Sequence Number     
            "032A017014",               // Target Kemid
            "42000",                    // Target Etran Code
            "Unit Test Target",         // Target Line Description
            "I",                        // Target Income Or Principal
            new KualiDecimal(10.00),    // Target Amount
            null,                       // Target Percent
            null,                       // Target Use Etran Code
            true                        // Active Indicator
    ),
    
    VALID_KEMID_TARGET_2("1",     // Target Sequence Number     
            "038D008041",               // Target Kemid
            "45000",                    // Target Etran Code
            "Unit Test Target",         // Target Line Description
            "P",                        // Target Income Or Principal
            null,                       // Target Amount
            new KualiDecimal(0.01),      // Target Percent
            null,                       // Target Use Etran Code
            true                        // Active Indicator
    ),
    
    VALID_KEMID_TARGET_3("2",     // Target Sequence Number     
            "037MDER022",               // Target Kemid
            "45000",                    // Target Etran Code
            "Unit Test Target",         // Target Line Description
            "P",                        // Target Income Or Principal
            null,                       // Target Amount
            new KualiDecimal(0.01),      // Target Percent
            null,                       // Target Use Etran Code
            true                        // Active Indicator
    );
    
    private String targetSequenceNumber;
    private String targetKemid;
    private String targetEtranCode;
    private String targetLineDescription;
    private String targetIncomeOrPrincipal;
    private KualiDecimal targetAmount;
    private KualiDecimal targetPercent;
    private String targetUseEtranCode;
    private boolean active;
    
    private EndowmentRecurringCashTransferKemidTargetFixture(String targetSequenceNumber, String targetKemid, String targetEtranCode, String targetLineDescription, String targetIncomeOrPrincipal, KualiDecimal targetAmount, KualiDecimal targetPercent, String targetUseEtranCode, boolean active) {
        this.targetSequenceNumber = targetSequenceNumber;
        this.targetKemid = targetKemid;
        this.targetEtranCode = targetEtranCode;
        this.targetLineDescription = targetLineDescription;
        this.targetIncomeOrPrincipal = targetIncomeOrPrincipal;
        this.targetAmount = targetAmount;
        this.targetPercent = targetPercent;
        this.targetUseEtranCode = targetUseEtranCode;
        this.active = active;
    }

    public EndowmentRecurringCashTransferKEMIDTarget createEndowmentRecurringCashTransferKEMIDTarget(){
        EndowmentRecurringCashTransferKEMIDTarget kemidTarget = new EndowmentRecurringCashTransferKEMIDTarget();
        
        kemidTarget.setTargetSequenceNumber(this.targetSequenceNumber);
        kemidTarget.setTargetKemid(this.targetKemid);
        kemidTarget.setTargetEtranCode(this.targetEtranCode);
        kemidTarget.setTargetLineDescription(this.targetLineDescription);
        kemidTarget.setTargetIncomeOrPrincipal(this.targetIncomeOrPrincipal);
        kemidTarget.setTargetAmount(this.targetAmount);
        kemidTarget.setTargetPercent(this.targetPercent);
        kemidTarget.setTargetUseEtranCode(this.targetUseEtranCode);
        kemidTarget.setActive(this.active);
        
        return kemidTarget;
    }
}


