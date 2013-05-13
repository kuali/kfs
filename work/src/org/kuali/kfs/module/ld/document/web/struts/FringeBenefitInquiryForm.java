/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.ld.document.web.struts;

import java.util.List;

import org.kuali.rice.kns.web.struts.form.KualiForm;
import org.kuali.kfs.module.ld.businessobject.BenefitInquiry;

public class FringeBenefitInquiryForm extends KualiForm {
    
    private List<BenefitInquiry> benefitInquiry;
    private String chartOfAccountsCode;
    private String amount;
    private String payrollEndDateFiscalYear;
    private String financialObjectCode;
    private String accountNumber;
    private String subAccountNumber;
   
    
   
    public List<BenefitInquiry> getBenefitInquiry() {
        return benefitInquiry;
    }
    public void setBenefitInquiry(List<BenefitInquiry> benefitInquiry) {
        this.benefitInquiry = benefitInquiry;
    }
    
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }
   
    public String getPayrollEndDateFiscalYear() {
        return payrollEndDateFiscalYear;
    }
    public void setPayrollEndDateFiscalYear(String  payrollEndDateFiscalYear) {
        this.payrollEndDateFiscalYear = payrollEndDateFiscalYear;
    }
    public String getFinancialObjectCode() {
        return financialObjectCode;
    }
    
    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }
    
    public String getAmount() {
        return amount;
    }
    public void setAmount(String amount) {
        this.amount = amount;
    }
    public String getAccountNumber() {
        return accountNumber;
    }
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    public String getSubAccountNumber() {
        return subAccountNumber;
    }
    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }    
    
}
