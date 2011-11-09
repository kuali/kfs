/*
 * Copyright 2011 The Kuali Foundation
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
package org.kuali.kfs.gl.batch.service.impl;

import java.math.BigDecimal;

import org.kuali.kfs.coa.businessobject.IndirectCostRecoveryAccount;

public class IndirectCostRecoveryAccountDistributionMetadata {
    
    private String indirectCostRecoveryFinCoaCode;
    private String indirectCostRecoveryAccountNumber;
    private BigDecimal accountLinePercent;
    
    /**
     * @param icrAccount
     */
    public IndirectCostRecoveryAccountDistributionMetadata(IndirectCostRecoveryAccount icrAccount) {
        this.indirectCostRecoveryFinCoaCode = icrAccount.getIndirectCostRecoveryFinCoaCode();
        this.indirectCostRecoveryAccountNumber = icrAccount.getIndirectCostRecoveryAccountNumber();
        this.accountLinePercent = icrAccount.getAccountLinePercent();
    }
    public String getIndirectCostRecoveryFinCoaCode() {
        return indirectCostRecoveryFinCoaCode;
    }
    public String getIndirectCostRecoveryAccountNumber() {
        return indirectCostRecoveryAccountNumber;
    }
    public BigDecimal getAccountLinePercent() {
        return accountLinePercent;
    }
    public void setIndirectCostRecoveryFinCoaCode(String indirectCostRecoveryFinCoaCode) {
        this.indirectCostRecoveryFinCoaCode = indirectCostRecoveryFinCoaCode;
    }
    public void setIndirectCostRecoveryAccountNumber(String indirectCostRecoveryAccountNumber) {
        this.indirectCostRecoveryAccountNumber = indirectCostRecoveryAccountNumber;
    }
    public void setAccountLinePercent(BigDecimal accountLinePercent) {
        this.accountLinePercent = accountLinePercent;
    } 
    
    
}
