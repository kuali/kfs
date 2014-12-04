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
package org.kuali.kfs.sys.fixture;

import java.sql.Date;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.rice.krad.service.BusinessObjectService;

public enum AccountFixture {
    ACTIVE_ACCOUNT(null, null, false, null, null, "2101-09-30"), CLOSED_ACCOUNT(null, null, true, null, null, null), EXPIRIED_ACCOUNT("BL", "1031467", false, "BL", "2331489", "2001-09-30"), EXPIRIED_ACCOUNT_NO_CONTINUATION("BL", "1031467", false, null, null, "2001-09-30"), EXPIRIED_ACCOUNT_EXPIRIED_AND_OPEN_CONTINUATION("BL", "fixture1", false, "BL", "4631644", "2001-09-30"), EXPIRIED_ACCOUNT_EXPIRIED_AND_CLOSED_CONTINUATION("BL", "fixture1", false, "BL", "4031425", "2001-09-30"), ACCOUNT_PRESENCE_ACCOUNT("BL", "4031416", false, null, null, null), ACCOUNT_NON_PRESENCE_ACCOUNT("BA", "6044900", false, null, null, null), ACCOUNT_PRESENCE_ACCOUNT_WITH_EXPIRED("BL", "4831483", false, null, null, "2001-09-30"), ACCOUNT_PRESENCE_ACCOUNT_BUT_CLOSED("BL", "4831483", false, null, null, null), ;

    public final String accountNumber;
    public final String chartOfAccountsCode;
    public final boolean closed;
    private final String accountExpirationDate;
    public final String continuationFinChrtOfAcctCd;
    public final String continuationAccountNumber;

    private AccountFixture(String chartOfAccountsCode, String accountNumber, boolean closed, String continuationFinChrtOfAcctCd, String continuationAccountNumber, String accountExpirationDate) {
        this.accountNumber = accountNumber;
        this.chartOfAccountsCode = chartOfAccountsCode;
        this.closed = closed;
        this.accountExpirationDate = accountExpirationDate;
        this.continuationFinChrtOfAcctCd = continuationFinChrtOfAcctCd;
        this.continuationAccountNumber = continuationAccountNumber;
    }

    public Account createAccount() {
        Account account = new Account();
        account.setAccountNumber(this.accountNumber);
        account.setChartOfAccountsCode(this.chartOfAccountsCode);
        account.setActive(!this.closed);
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

    public Date getAccountExpirationDate() {
        return Date.valueOf(this.accountExpirationDate);
    }

}
