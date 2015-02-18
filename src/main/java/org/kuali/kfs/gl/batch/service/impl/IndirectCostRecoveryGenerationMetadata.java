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

import java.util.ArrayList;
import java.util.List;

public class IndirectCostRecoveryGenerationMetadata {
    private String indirectCostRecoveryTypeCode;
    private String financialIcrSeriesIdentifier;
    
    private List<IndirectCostRecoveryAccountDistributionMetadata> accountLists = new ArrayList<IndirectCostRecoveryAccountDistributionMetadata>();
    
    //keep these COA and Acct fields for service use
    private String indirectCostRcvyFinCoaCode;
    private String indirectCostRecoveryAcctNbr;

    public IndirectCostRecoveryGenerationMetadata(String indirectCostRecoveryTypeCode, String financialIcrSeriesIdentifier) {
        this.indirectCostRecoveryTypeCode = indirectCostRecoveryTypeCode;
        this.financialIcrSeriesIdentifier = financialIcrSeriesIdentifier;
    }
    public String getIndirectCostRecoveryTypeCode() {
        return indirectCostRecoveryTypeCode;
    }
    public void setIndirectCostRecoveryTypeCode(String indirectCostRecoveryTypeCode) {
        this.indirectCostRecoveryTypeCode = indirectCostRecoveryTypeCode;
    }
    public String getFinancialIcrSeriesIdentifier() {
        return financialIcrSeriesIdentifier;
    }
    public void setFinancialIcrSeriesIdentifier(String financialIcrSeriesIdentifier) {
        this.financialIcrSeriesIdentifier = financialIcrSeriesIdentifier;
    }
    public String getIndirectCostRcvyFinCoaCode() {
        return indirectCostRcvyFinCoaCode;
    }
    public void setIndirectCostRcvyFinCoaCode(String indirectCostRcvyFinCoaCode) {
        this.indirectCostRcvyFinCoaCode = indirectCostRcvyFinCoaCode;
    }
    public String getIndirectCostRecoveryAcctNbr() {
        return indirectCostRecoveryAcctNbr;
    }
    public void setIndirectCostRecoveryAcctNbr(String indirectCostRecoveryAcctNbr) {
        this.indirectCostRecoveryAcctNbr = indirectCostRecoveryAcctNbr;
    }

    public List<IndirectCostRecoveryAccountDistributionMetadata> getAccountLists() {
        return accountLists;
    }
    public void setAccountLists(List<IndirectCostRecoveryAccountDistributionMetadata> accountLists) {
        this.accountLists = accountLists;
    }
}
