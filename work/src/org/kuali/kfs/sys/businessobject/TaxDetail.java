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
