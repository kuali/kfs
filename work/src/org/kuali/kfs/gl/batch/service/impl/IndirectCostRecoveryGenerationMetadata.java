/*
 * Copyright 2008 The Kuali Foundation
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

import org.kuali.kfs.coa.businessobject.Account;

public class IndirectCostRecoveryGenerationMetadata {
    private String indirectCostRecoveryTypeCode;
    private String financialIcrSeriesIdentifier;
    private String indirectCostRcvyFinCoaCode;
    private String indirectCostRecoveryAcctNbr;
    private Account indirectCostRecoveryAcct;
    
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
    public Account getIndirectCostRecoveryAcct() {
        return indirectCostRecoveryAcct;
    }
    public void setIndirectCostRecoveryAcct(Account indirectCostRecoveryAcct) {
        this.indirectCostRecoveryAcct = indirectCostRecoveryAcct;
    }
}
