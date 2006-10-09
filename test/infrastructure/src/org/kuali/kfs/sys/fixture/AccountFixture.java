/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
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
