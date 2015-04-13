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
