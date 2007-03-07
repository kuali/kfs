/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.purap.bo;

import java.math.BigDecimal;

import org.kuali.kfs.bo.AccountingLineBase;
import org.kuali.module.chart.bo.Account;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.SubAccount;

public class PurApAccountingLineBase extends AccountingLineBase implements PurApAccountingLine {

    private Chart chartOfAccounts;
    private Account account;
    private SubAccount subAccount;
    protected Integer accountIdentifier;
    private Integer itemIdentifier;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;
    private String financialObjectCode;
    private String financialSubObjectCode;
    private String projectCode;
    private String organizationReferenceId;
    private BigDecimal accountLinePercent;
    
    
    /**
     * Gets the chartOfAccounts attribute.
     * 
     * @return Returns the chartOfAccounts
     * 
     */
    public Chart getChartOfAccounts() { 
        return chartOfAccounts;
    }

    /**
     * Sets the chartOfAccounts attribute.
     * 
     * @param chartOfAccounts The chartOfAccounts to set.
     * @deprecated
     */
    public void setChartOfAccounts(Chart chartOfAccounts) {
        this.chartOfAccounts = chartOfAccounts;
    }

    /**
     * Gets the account attribute.
     * 
     * @return Returns the account
     * 
     */
    public Account getAccount() { 
        return account;
    }

    /**
     * Sets the account attribute.
     * 
     * @param account The account to set.
     * @deprecated
     */
    public void setAccount(Account account) {
        this.account = account;
    }

    /**
     * Gets the subAccount attribute. 
     * @return Returns the subAccount.
     */
    public SubAccount getSubAccount() {
        return subAccount;
    }

    /**
     * Sets the subAccount attribute value.
     * @param subAccount The subAccount to set.
     * @deprecated
     */
    public void setSubAccount(SubAccount subAccount) {
        this.subAccount = subAccount;
    }

    public Integer getAccountIdentifier() { 
    	return accountIdentifier;
    }

    public void setAccountIdentifier(Integer requisitionAccountIdentifier) {
    	this.accountIdentifier = requisitionAccountIdentifier;
    }

    public Integer getItemIdentifier() { 
    	return itemIdentifier;
    }

    public void setItemIdentifier(Integer requisitionItemIdentifier) {
    	this.itemIdentifier = requisitionItemIdentifier;
    }

    public String getChartOfAccountsCode() { 
    	return chartOfAccountsCode;
    }

    public void setChartOfAccountsCode(String chartOfAccountsCode) {
    	this.chartOfAccountsCode = chartOfAccountsCode;
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

    public String getProjectCode() { 
    	return projectCode;
    }

    public void setProjectCode(String projectCode) {
    	this.projectCode = projectCode;
    }

    public String getOrganizationReferenceId() { 
    	return organizationReferenceId;
    }

    public void setOrganizationReferenceId(String organizationReferenceId) {
    	this.organizationReferenceId = organizationReferenceId;
    }

    public BigDecimal getAccountLinePercent() { 
    	return accountLinePercent;
    }

    public void setAccountLinePercent(BigDecimal accountLinePercent) {
    	this.accountLinePercent = accountLinePercent;
    }
    
    
}
