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
