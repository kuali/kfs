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
public class BudgetConstructionOrgSynchronizationProblemsReport {

    // Header parts
    private String fiscalYear;

    private String chartOfAccountsCode;
    private String chartOfAccountDescription;
    private String organizationCode;
    private String organizationName;

    
    
    // Body parts
    private String bodyChartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;
    
    private String financialObjectCode;
    private String financialSubObjectCode;
    private String positionNumber;
    
    private String emplid;
    private String name;
    
    private String positionObjectChangeIndicator;
    private String positionSalaryChangeIndicator;
    private String positionEffectiveStatus;
    private String budgetedPosition;
    
    
    
    
    
    
    
    
    
    
    
    
    
    public String getAccountNumber() {
        return accountNumber;
    }
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    public String getBudgetedPosition() {
        return budgetedPosition;
    }
    public void setBudgetedPosition(String budgetedPosition) {
        this.budgetedPosition = budgetedPosition;
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
    public String getFinancialObjectCode() {
        return financialObjectCode;
    }
    public void setFinancialObjectCode(String financialObjectCode) {
        this.financialObjectCode = financialObjectCode;
    }
    public String getFinancialSubObjectCode() {
        return financialSubObjectCode;
    }
    public void setFinancialSubObjectCode(String financialSubObjectCode) {
        this.financialSubObjectCode = financialSubObjectCode;
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
    public String getPositionEffectiveStatus() {
        return positionEffectiveStatus;
    }
    public void setPositionEffectiveStatus(String positionEffectiveStatus) {
        this.positionEffectiveStatus = positionEffectiveStatus;
    }
    public String getPositionNumber() {
        return positionNumber;
    }
    public void setPositionNumber(String positionNumber) {
        this.positionNumber = positionNumber;
    }
    public String getPositionObjectChangeIndicator() {
        return positionObjectChangeIndicator;
    }
    public void setPositionObjectChangeIndicator(String positionObjectChangeIndicator) {
        this.positionObjectChangeIndicator = positionObjectChangeIndicator;
    }
    public String getPositionSalaryChangeIndicator() {
        return positionSalaryChangeIndicator;
    }
    public void setPositionSalaryChangeIndicator(String positionSalaryChangeIndicator) {
        this.positionSalaryChangeIndicator = positionSalaryChangeIndicator;
    }
    public String getSubAccountNumber() {
        return subAccountNumber;
    }
    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }
    public String getBodyChartOfAccountsCode() {
        return bodyChartOfAccountsCode;
    }
    public void setBodyChartOfAccountsCode(String bodyChartOfAccountsCode) {
        this.bodyChartOfAccountsCode = bodyChartOfAccountsCode;
    }
    public String getEmplid() {
        return emplid;
    }
    public void setEmplid(String emplid) {
        this.emplid = emplid;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    
    
    
    
   

}

