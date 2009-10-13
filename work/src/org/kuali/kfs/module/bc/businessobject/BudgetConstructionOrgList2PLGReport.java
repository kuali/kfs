/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.module.bc.businessobject;


/**
 * Budget Construction Organization Account Funding Detail Report Business Object.
 */
public class BudgetConstructionOrgList2PLGReport {

    //Header parts
    private String fiscalYear;
    private String orgChartOfAccountsCode;
    private String orgChartOfAccountDescription;
    private String chartOfAccountsCode;
    private String chartOfAccountDescription;
    private String organizationCode;
    private String organizationName;
    private String reqFy;
    
    //Body parts
    private String accountNumber;
    private String subAccountNumber;
    private String accountSubAccountName;
    private Integer reqAmount;
    
    
    
    
    public String getAccountNumber() {
        return accountNumber;
    }
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    public String getAccountSubAccountName() {
        return accountSubAccountName;
    }
    public void setAccountSubAccountName(String accountSubAccountName) {
        this.accountSubAccountName = accountSubAccountName;
    }
    public String getChartOfAccountDescription() {
        return chartOfAccountDescription;
    }
    public void setChartOfAccountDescription(String chartOfAccountDescription) {
        this.chartOfAccountDescription = chartOfAccountDescription;
    }
    public String getChartOfAccountsCode() {
        return chartOfAccountsCode;
    }
    public void setChartOfAccountsCode(String chartOfAccountsCode) {
        this.chartOfAccountsCode = chartOfAccountsCode;
    }
    public String getFiscalYear() {
        return fiscalYear;
    }
    public void setFiscalYear(String fiscalYear) {
        this.fiscalYear = fiscalYear;
    }
    public String getOrganizationCode() {
        return organizationCode;
    }
    public void setOrganizationCode(String organizationCode) {
        this.organizationCode = organizationCode;
    }
    public String getOrganizationName() {
        return organizationName;
    }
    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }
    public String getOrgChartOfAccountDescription() {
        return orgChartOfAccountDescription;
    }
    public void setOrgChartOfAccountDescription(String orgChartOfAccountDescription) {
        this.orgChartOfAccountDescription = orgChartOfAccountDescription;
    }
    public String getOrgChartOfAccountsCode() {
        return orgChartOfAccountsCode;
    }
    public void setOrgChartOfAccountsCode(String orgChartOfAccountsCode) {
        this.orgChartOfAccountsCode = orgChartOfAccountsCode;
    }
    public Integer getReqAmount() {
        return reqAmount;
    }
    public void setReqAmount(Integer reqAmount) {
        this.reqAmount = reqAmount;
    }
    public String getReqFy() {
        return reqFy;
    }
    public void setReqFy(String reqFy) {
        this.reqFy = reqFy;
    }
    public String getSubAccountNumber() {
        return subAccountNumber;
    }
    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }
   

}
