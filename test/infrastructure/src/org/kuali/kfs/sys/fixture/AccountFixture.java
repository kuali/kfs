/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.test.fixtures;

import java.sql.Timestamp;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.module.chart.bo.Account;

public enum AccountFixture {
    ACTIVE_ACCOUNT(null, null, false, null, null, "2101-09-30 00:00:00.000000000"),
    CLOSED_ACCOUNT(null, null, true, null, null, null),
    EXPIRIED_ACCOUNT(null, "1031467", false, "BL", "2331489", "2001-09-30 00:00:00.000000000"),
    EXPIRIED_ACCOUNT_NO_CONTINUATION(null, null, false, null, null, "2001-09-30 00:00:00.000000000"),
    EXPIRIED_ACCOUNT_EXPIRIED_AND_OPEN_CONTINUATION(null, "fixture1", false, "BL", "4631644", "2001-09-30 00:00:00.000000000"),
    EXPIRIED_ACCOUNT_EXPIRIED_AND_CLOSED_CONTINUATION(null, "fixture1", false, "BL", "4031425", "2001-09-30 00:00:00.000000000"),
    ACCOUNT_PRESENCE_ACCOUNT("BL", "4031416", false, null, null, null),
    ACCOUNT_NON_PRESENCE_ACCOUNT("BA", "6044900", false, null, null, null),
    ACCOUNT_PRESENCE_ACCOUNT_WITH_EXPIRED("BL", "4831483", false, null, null, "2001-09-30 00:00:00.000000000"),
    ACCOUNT_PRESENCE_ACCOUNT_BUT_CLOSED("BL", "4831483", false, null, null, null),
    ;

    public final String accountNumber;
    public final String chartOfAccountsCode;
    public final boolean accountClosedIndicator;
    private final String accountExpirationDate;
    public final String continuationFinChrtOfAcctCd;
    public final String continuationAccountNumber;

    private AccountFixture(String chartOfAccountsCode, String accountNumber, boolean accountClosedIndicator, String continuationFinChrtOfAcctCd, String continuationAccountNumber, String accountExpirationDate) {
        this.accountNumber = accountNumber;
        this.chartOfAccountsCode = chartOfAccountsCode;
        this.accountClosedIndicator = accountClosedIndicator;
        this.accountExpirationDate = accountExpirationDate;
        this.continuationFinChrtOfAcctCd = continuationFinChrtOfAcctCd;
        this.continuationAccountNumber = continuationAccountNumber;
    }

    public Account createAccount() {
        Account account = new Account();
        account.setAccountNumber(this.accountNumber);
        account.setChartOfAccountsCode(this.chartOfAccountsCode);
        account.setAccountClosedIndicator(this.accountClosedIndicator);
        account.setContinuationAccountNumber(this.continuationAccountNumber);
        account.setContinuationFinChrtOfAcctCd(this.continuationFinChrtOfAcctCd);
        if (StringUtils.isNotBlank(this.accountExpirationDate)) {
            account.setAccountExpirationDate(getAccountExpirationDate());
        }

        return account;
    }

    public Account createAccount(BusinessObjectService businessObjectService) {
        return (Account) businessObjectService.retrieve(this.createAccount());
    }

    public Timestamp getAccountExpirationDate() {
        return Timestamp.valueOf(this.accountExpirationDate);
    }

}
