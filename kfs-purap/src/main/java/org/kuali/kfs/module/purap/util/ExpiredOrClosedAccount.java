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
package org.kuali.kfs.module.purap.util;

/**
 * 
 * Expired or Closed Account
 */
public class ExpiredOrClosedAccount {

    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;
    private boolean closedIndicator;
    private boolean expiredIndicator;
    private boolean continuationAccountMissing;
    
    /**
     * 
     * Default constructor
     */
    public ExpiredOrClosedAccount() {
    }
    
    /**
     * 
     * Constructs an Expired Or Closed Account consisting of the following attributes.
     * @param chartOfAccountsCode chart
     * @param accountNumber account
     * @param subAccountNumber subAccount
     */
    public ExpiredOrClosedAccount(String chartOfAccountsCode, String accountNumber, String subAccountNumber) {
        setChartOfAccountsCode(chartOfAccountsCode);
        setAccountNumber(accountNumber);
        setSubAccountNumber(subAccountNumber);
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

    public boolean isClosedIndicator() {
        return closedIndicator;
    }

    public void setClosedIndicator(boolean closedIndicator) {
        this.closedIndicator = closedIndicator;
    }

    public boolean isContinuationAccountMissing() {
        return continuationAccountMissing;
    }

    public void setContinuationAccountMissing(boolean continuationAccountMissing) {
        this.continuationAccountMissing = continuationAccountMissing;
    }

    public boolean isExpiredIndicator() {
        return expiredIndicator;
    }

    public void setExpiredIndicator(boolean expiredIndicator) {
        this.expiredIndicator = expiredIndicator;
    }

    public String getSubAccountNumber() {
        return subAccountNumber;
    }

    public void setSubAccountNumber(String subAccountNumber) {
        this.subAccountNumber = subAccountNumber;
    }

    /**
     * This is a helper method to return the account as a string in the format chart-account-subaccount.
     * 
     * @return account string representation
     */
    public String getAccountString() {
        StringBuffer accountStr = new StringBuffer("");

        if (getChartOfAccountsCode() != null) {
            accountStr.append(getChartOfAccountsCode());
        }

        if (getAccountNumber() != null) {
            accountStr.append("-");
            accountStr.append(getAccountNumber());
        }

        if (getSubAccountNumber() != null) {
            accountStr.append("-");
            accountStr.append(getSubAccountNumber());
        }

        return accountStr.toString();
    }
}
