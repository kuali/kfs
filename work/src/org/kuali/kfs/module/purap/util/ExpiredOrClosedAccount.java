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
package org.kuali.module.purap.util;

import org.kuali.core.util.ObjectUtils;

public class ExpiredOrClosedAccount {

    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;
    private boolean closedIndicator;
    private boolean expiredIndicator;
    private boolean continuationAccountMissing;
    
    public ExpiredOrClosedAccount(){}
    
    public ExpiredOrClosedAccount(String chartOfAccountsCode, String accountNumber, String subAccountNumber){
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
     * This is a helper method to return the account as a string in
     * the format chart-account-subaccount.
     * 
     * @return
     */
    public String getAccountString(){
        StringBuffer accountStr = new StringBuffer("");
        
        if(getChartOfAccountsCode() != null){
            accountStr.append(getChartOfAccountsCode());            
        }
        
        if(getAccountNumber() != null){
            accountStr.append("-");
            accountStr.append(getAccountNumber());            
        }
        
        if(getSubAccountNumber() != null){
            accountStr.append("-");
            accountStr.append(getSubAccountNumber());
        }
        
        return accountStr.toString();
    }
}
