/*
 * Copyright 2006-2007 The Kuali Foundation.
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

package org.kuali.module.budget.bo;

import org.kuali.core.util.KualiInteger;

/**
 * 
 */
public class BudgetConstructionAccountSalaryDetailReport{

    // header
    private Integer universityFiscalYear;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;

    private String accountName;
    private String subAccountName;
    
    //body
    private String financialObjectCode;
    private String financialSubObjectCode;
    private String financialObjectCodeShortName;
    
    //line break
    private String objCodeSubObjCode;
    private Integer annualAmount;
    private Integer financialDocumentMonth1LineAmount;
    private Integer financialDocumentMonth2LineAmount;
    private Integer financialDocumentMonth3LineAmount;
    private Integer financialDocumentMonth4LineAmount;
    private Integer financialDocumentMonth5LineAmount;
    private Integer financialDocumentMonth6LineAmount;
    private Integer financialDocumentMonth7LineAmount;
    private Integer financialDocumentMonth8LineAmount;
    private Integer financialDocumentMonth9LineAmount;
    private Integer financialDocumentMonth10LineAmount;
    private Integer financialDocumentMonth11LineAmount;
    private Integer financialDocumentMonth12LineAmount;

    /**
     * Default constructor.
     */
    public BudgetConstructionAccountSalaryDetailReport() {

    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public Integer getAnnualAmount() {
        return annualAmount;
    }

    public void setAnnualAmount(Integer annualAmount) {
        this.annualAmount = annualAmount;
    }

    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }

    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }

    public Integer getFinancialDocumentMonth10LineAmount() {
        return financialDocumentMonth10LineAmount;
    }

    public void setFinancialDocumentMonth10LineAmount(Integer financialDocumentMonth10LineAmount) {
        this.financialDocumentMonth10LineAmount = financialDocumentMonth10LineAmount;
    }

    public Integer getFinancialDocumentMonth11LineAmount() {
        return financialDocumentMonth11LineAmount;
    }

    public void setFinancialDocumentMonth11LineAmount(Integer financialDocumentMonth11LineAmount) {
        this.financialDocumentMonth11LineAmount = financialDocumentMonth11LineAmount;
    }

    public Integer getFinancialDocumentMonth12LineAmount() {
        return financialDocumentMonth12LineAmount;
    }

    public void setFinancialDocumentMonth12LineAmount(Integer financialDocumentMonth12LineAmount) {
        this.financialDocumentMonth12LineAmount = financialDocumentMonth12LineAmount;
    }

    public Integer getFinancialDocumentMonth1LineAmount() {
        return financialDocumentMonth1LineAmount;
    }

    public void setFinancialDocumentMonth1LineAmount(Integer financialDocumentMonth1LineAmount) {
        this.financialDocumentMonth1LineAmount = financialDocumentMonth1LineAmount;
    }

    public Integer getFinancialDocumentMonth2LineAmount() {
        return financialDocumentMonth2LineAmount;
    }

    public void setFinancialDocumentMonth2LineAmount(Integer financialDocumentMonth2LineAmount) {
        this.financialDocumentMonth2LineAmount = financialDocumentMonth2LineAmount;
    }

    public Integer getFinancialDocumentMonth3LineAmount() {
        return financialDocumentMonth3LineAmount;
    }

    public void setFinancialDocumentMonth3LineAmount(Integer financialDocumentMonth3LineAmount) {
        this.financialDocumentMonth3LineAmount = financialDocumentMonth3LineAmount;
    }

    public Integer getFinancialDocumentMonth4LineAmount() {
        return financialDocumentMonth4LineAmount;
    }

    public void setFinancialDocumentMonth4LineAmount(Integer financialDocumentMonth4LineAmount) {
        this.financialDocumentMonth4LineAmount = financialDocumentMonth4LineAmount;
    }

    public Integer getFinancialDocumentMonth5LineAmount() {
        return financialDocumentMonth5LineAmount;
    }

    public void setFinancialDocumentMonth5LineAmount(Integer financialDocumentMonth5LineAmount) {
        this.financialDocumentMonth5LineAmount = financialDocumentMonth5LineAmount;
    }

    public Integer getFinancialDocumentMonth6LineAmount() {
        return financialDocumentMonth6LineAmount;
    }

    public void setFinancialDocumentMonth6LineAmount(Integer financialDocumentMonth6LineAmount) {
        this.financialDocumentMonth6LineAmount = financialDocumentMonth6LineAmount;
    }

    public Integer getFinancialDocumentMonth7LineAmount() {
        return financialDocumentMonth7LineAmount;
    }

    public void setFinancialDocumentMonth7LineAmount(Integer financialDocumentMonth7LineAmount) {
        this.financialDocumentMonth7LineAmount = financialDocumentMonth7LineAmount;
    }

    public Integer getFinancialDocumentMonth8LineAmount() {
        return financialDocumentMonth8LineAmount;
    }

    public void setFinancialDocumentMonth8LineAmount(Integer financialDocumentMonth8LineAmount) {
        this.financialDocumentMonth8LineAmount = financialDocumentMonth8LineAmount;
    }

    public Integer getFinancialDocumentMonth9LineAmount() {
        return financialDocumentMonth9LineAmount;
    }

    public void setFinancialDocumentMonth9LineAmount(Integer financialDocumentMonth9LineAmount) {
        this.financialDocumentMonth9LineAmount = financialDocumentMonth9LineAmount;
    }

    public String getFinancialObjectCode() {
        return financialObjectCode;
    }

    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }

    public String getFinancialObjectCodeShortName() {
        return financialObjectCodeShortName;
    }

    public void setFinancialObjectCodeShortName(String financialObjectCodeShortName) {
        this.financialObjectCodeShortName = financialObjectCodeShortName;
    }

    public String getFinancialSubObjectCode() {
        return financialSubObjectCode;
    }

    public void setFinancialSubObjectCode(String financialSubObjectCode) {
        this.financialSubObjectCode = financialSubObjectCode;
    }

    public String getObjCodeSubObjCode() {
        return objCodeSubObjCode;
    }

    public void setObjCodeSubObjCode(String objCodeSubObjCode) {
        this.objCodeSubObjCode = objCodeSubObjCode;
    }

    public String getSubAccountName() {
        return subAccountName;
    }

    public void setSubAccountName(String subAccountName) {
        this.subAccountName = subAccountName;
    }

    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }

    public Integer getUniversityFiscalYear() {
        return universityFiscalYear;
    }

    public void setUniversityFiscalYear(Integer universityFiscalYear) {
        this.universityFiscalYear = universityFiscalYear;
    }
}
