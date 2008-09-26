/*
 * Copyright 2008 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.purap.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.util.KualiDecimal;

public class PaymentRequestItemUseTax extends PersistableBusinessObjectBase{
    private  Long useTaxId;
    private  Long itemIdentifier;
    private  String rateCode;
    private  KualiDecimal taxAmount;
    private  String chartOfAccountsCode;
    private  String accountNumber;
    private  String financialObjectCode;
    
    
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


    public Long getItemIdentifier() {
        return itemIdentifier;
    }


    public void setItemIdentifier(Long itemIdentifier) {
        this.itemIdentifier = itemIdentifier;
    }


    
    public Long getUseTaxId() {
        return useTaxId;
    }
    public void setUseTaxId(Long useTaxId) {
        this.useTaxId = useTaxId;
    }


    public String getRateCode() {
        return rateCode;
    }


    public void setRateCode(String rateCode) {
        this.rateCode = rateCode;
    }


    

    protected LinkedHashMap toStringMapper() {
            LinkedHashMap m = new LinkedHashMap();
            m.put("useTaxId", this.useTaxId);
            return m;
        }


    public KualiDecimal getTaxAmount() {
        return taxAmount;
    }


    public void setTaxAmount(KualiDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

}
