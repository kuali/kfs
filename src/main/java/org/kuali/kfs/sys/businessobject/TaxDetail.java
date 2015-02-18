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
package org.kuali.kfs.sys.businessobject;

import java.math.BigDecimal;
import java.util.LinkedHashMap;

import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.TransientBusinessObjectBase;

public class TaxDetail extends TransientBusinessObjectBase {

    private String rateCode; //(e.g., state code or district code)
    private String rateName; //(e.g., state name or tax district name)
    private BigDecimal taxRate; //(a rate between 0 and 1)
    private String typeCode; //type code based on tax region type code (POST, ST, CNTY)
    private KualiDecimal taxAmount;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String financialObjectCode;

    public TaxDetail() {
        taxRate = BigDecimal.ZERO;
        taxAmount = KualiDecimal.ZERO;
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

    public String getRateCode() {
        return rateCode;
    }

    public void setRateCode(String rateCode) {
        this.rateCode = rateCode;
    }

    public String getRateName() {
        return rateName;
    }

    public void setRateName(String rateName) {
        this.rateName = rateName;
    }

    public BigDecimal getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    public KualiDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(KualiDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }
    
    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }    

    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        // TODO Auto-generated method stub
        return null;
    }

    public String toString() {
        return typeCode + "-" + rateCode + "-" + rateName + " " + taxRate.toString() + ":" + taxAmount.toString();
    }
    
}
