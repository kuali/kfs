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

import org.kuali.kfs.fp.businessobject.OffsetAccount;

public enum OffsetAccountFixture {
    OFFSET_ACCOUNT1("BL", "2231401", "8000", "UA", "1912201"), ;

    public final String accountNumber;
    public final String chartOfAccountsCode;
    public final String financialOffsetObjectCode;
    public final String financialOffsetChartOfAccountCode;
    public final String financialOffsetAccountNumber;

    private OffsetAccountFixture(String chartOfAccountsCode, String accountNumber, String financialOffsetObjectCode, String financialOffsetChartOfAccountCode, String financialOffsetAccountNumber) {
        this.accountNumber = accountNumber;
        this.chartOfAccountsCode = chartOfAccountsCode;
        this.financialOffsetObjectCode = financialOffsetObjectCode;
        this.financialOffsetChartOfAccountCode = financialOffsetChartOfAccountCode;
        this.financialOffsetAccountNumber = financialOffsetAccountNumber;
    }

    public OffsetAccount createOffsetAccount() {
        OffsetAccount account = new OffsetAccount();
        account.setAccountNumber(this.accountNumber);
        account.setChartOfAccountsCode(this.chartOfAccountsCode);
        account.setFinancialOffsetObjectCode(this.financialOffsetObjectCode);
        account.setFinancialOffsetChartOfAccountCode(this.financialOffsetChartOfAccountCode);
        account.setFinancialOffsetAccountNumber(this.financialOffsetAccountNumber);
        account.setActive(true);

        return account;
    }
}
