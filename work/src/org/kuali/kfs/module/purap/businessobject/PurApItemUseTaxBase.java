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
package org.kuali.kfs.module.purap.businessobject;


import java.util.LinkedHashMap;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public abstract class PurApItemUseTaxBase extends PersistableBusinessObjectBase implements PurApItemUseTax {

    private Integer useTaxId;
    private Integer itemIdentifier;
    private String rateCode;
    private KualiDecimal taxAmount;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String financialObjectCode;

    public PurApItemUseTaxBase() {
        super();
    }

    public PurApItemUseTaxBase(PurApItemUseTax itemUseTax) {
        super();
        setAccountNumber(itemUseTax.getAccountNumber());
        setChartOfAccountsCode(itemUseTax.getChartOfAccountsCode());
        setFinancialObjectCode(itemUseTax.getFinancialObjectCode());
        setRateCode(itemUseTax.getRateCode());
        setTaxAmount(itemUseTax.getTaxAmount());
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }

    public Integer getItemIdentifier() {
        return itemIdentifier;
    }

    public void setItemIdentifier(Integer itemIdentifier) {
        this.itemIdentifier = itemIdentifier;
    }

    public Integer getUseTaxId() {
        return useTaxId;
    }

    public void setUseTaxId(Integer useTaxId) {
        this.useTaxId = useTaxId;
    }

    public String getRateCode() {
        return rateCode;
    }

    public void setRateCode(String rateCode) {
        this.rateCode = rateCode;
    }

    public KualiDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(KualiDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("useTaxId", this.useTaxId);
        return m;
    }
    
    /**
     * Override needed for PURAP GL entry creation (hjs) - please do not add "amount" to this method
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if (!(obj instanceof PurApItemUseTaxBase)) {
            return false;
        }
        PurApItemUseTax purapItemUseTax = (PurApItemUseTax) obj;
        return new EqualsBuilder().append(this.chartOfAccountsCode,purapItemUseTax.getChartOfAccountsCode()).
        append(this.accountNumber,purapItemUseTax.getAccountNumber()).
        append(this.getRateCode(),purapItemUseTax.getRateCode()).
        append(this.financialObjectCode,purapItemUseTax.getFinancialObjectCode()).isEquals();
    }
    
    /**
     * Override needed for PURAP GL entry creation please do not add "taxAmount" to this method
     * 
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return new HashCodeBuilder(55, 97).
            append(this.chartOfAccountsCode).
            append(this.accountNumber).
            append(this.getRateCode()).
            append(this.financialObjectCode).toHashCode();
    }
    
}
