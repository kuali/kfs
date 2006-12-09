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

import org.kuali.module.financial.bo.OffsetAccount;

public enum OffsetAccountFixture {
    OFFSET_ACCOUNT1("BL", "2231401", "8000", "UA", "1912201"),
    ;

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

        return account;
    }
}
