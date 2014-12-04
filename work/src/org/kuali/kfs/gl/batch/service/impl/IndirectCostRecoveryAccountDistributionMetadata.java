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
