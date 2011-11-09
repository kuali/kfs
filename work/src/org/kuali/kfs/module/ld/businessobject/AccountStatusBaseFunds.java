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

package org.kuali.kfs.module.ld.businessobject;

import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Labor business object for Account Status (Base Funds).
 */
public class AccountStatusBaseFunds extends LedgerBalance {
    private KualiDecimal csfAmount;
    private KualiDecimal baseBudgetAmount;
    private KualiDecimal baseCSFVarianceAmount;

    /**
     * Default constructor.
     */
    public AccountStatusBaseFunds() {
        super();
        this.setBaseCSFVarianceAmount(KualiDecimal.ZERO);
        this.setCsfAmount(KualiDecimal.ZERO);
        this.setBaseBudgetAmount(KualiDecimal.ZERO);
    }

    /**
     * Gets the baseBudgetAmount attribute.
     * 
     * @return Returns the baseBudgetAmount.
     */
    public KualiDecimal getBaseBudgetAmount() {
        return this.getAccountLineAnnualBalanceAmount().add(this.getFinancialBeginningBalanceLineAmount()).add(this.getContractsGrantsBeginningBalanceAmount());
    }

    /**
     * Sets the baseBudgetAmount attribute value.
     * 
     * @param baseBudgetAmount The baseBudgetAmount to set.
     */
    public void setBaseBudgetAmount(KualiDecimal baseBudgetAmount) {
        this.baseBudgetAmount = baseBudgetAmount;
    }

    /**
     * Gets the baseCSFVarianceAmount attribute.
     * 
     * @return Returns the baseCSFVarianceAmount.
     */
    public KualiDecimal getBaseCSFVarianceAmount() {
        return this.getBaseBudgetAmount().subtract(this.getCsfAmount());
    }

    /**
     * Sets the baseCSFVarianceAmount attribute value.
     * 
     * @param baseCSFVarianceAmount The baseCSFVarianceAmount to set.
     */
    public void setBaseCSFVarianceAmount(KualiDecimal baseCSFVarianceAmount) {
        this.baseCSFVarianceAmount = baseCSFVarianceAmount;
    }

    /**
     * Gets the csfAmount attribute.
     * 
     * @return Returns the csfAmount.
     */
    public KualiDecimal getCsfAmount() {
        return csfAmount;
    }

    /**
     * Sets the csfAmount attribute value.
     * 
     * @param csfAmount The csfAmount to set.
     */
    public void setCsfAmount(KualiDecimal csfAmount) {
        this.csfAmount = csfAmount;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((getAccountNumber() == null) ? 0 : getAccountNumber().hashCode());
        result = PRIME * result + ((getChartOfAccountsCode() == null) ? 0 : getChartOfAccountsCode().hashCode());
        result = PRIME * result + ((getFinancialObjectCode() == null) ? 0 : getFinancialObjectCode().hashCode());
        result = PRIME * result + ((getFinancialSubObjectCode() == null) ? 0 : getFinancialSubObjectCode().hashCode());
        result = PRIME * result + ((getSubAccountNumber() == null) ? 0 : getSubAccountNumber().hashCode());
        result = PRIME * result + ((getUniversityFiscalYear() == null) ? 0 : getUniversityFiscalYear().hashCode());

        return result;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        final AccountStatusBaseFunds other = (AccountStatusBaseFunds) obj;
        if (getAccountNumber() == null) {
            if (other.getAccountNumber() != null)
                return false;
        }
        else if (!getAccountNumber().equals(other.getAccountNumber())) {
            return false;
        }

        if (getChartOfAccountsCode() == null) {
            if (other.getChartOfAccountsCode() != null)
                return false;
        }
        else if (!getChartOfAccountsCode().equals(other.getChartOfAccountsCode())) {
            return false;
        }

        if (getFinancialObjectCode() == null) {
            if (other.getFinancialObjectCode() != null)
                return false;
        }
        else if (!getFinancialObjectCode().equals(other.getFinancialObjectCode())) {
            return false;
        }

        if (getFinancialSubObjectCode() == null) {
            if (other.getFinancialSubObjectCode() != null)
                return false;
        }
        else if (!getFinancialSubObjectCode().equals(other.getFinancialSubObjectCode())) {
            return false;
        }

        if (getSubAccountNumber() == null) {
            if (other.getSubAccountNumber() != null)
                return false;
        }
        else if (!getSubAccountNumber().equals(other.getSubAccountNumber())) {
            return false;
        }

        if (getUniversityFiscalYear() == null) {
            if (other.getUniversityFiscalYear() != null)
                return false;
        }
        else if (!getUniversityFiscalYear().equals(other.getUniversityFiscalYear())) {
            return false;
        }

        return true;
    }
}
