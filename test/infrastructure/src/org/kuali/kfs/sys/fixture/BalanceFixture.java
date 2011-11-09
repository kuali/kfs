/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.sys.fixture;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.SubFundGroup;
import org.kuali.kfs.gl.businessobject.Balance;
import org.kuali.rice.krad.service.BusinessObjectService;

public enum BalanceFixture {
    BALANCE_PASS1("GF", "ABCD", "1111", "ABCDEF", "", ""), BALANCE_FAIL1("AB", "ABCD", "1111", "ABCDEF", "", ""), BALANCE_FAIL2("GF", "BALS", "1111", "ABCDEF", "", ""), BALANCE_FAIL3("GF", "ABCD", "9890", "ABCDEF", "", ""), BALANCE_FAIL4("GF", "ABCD", "1111", "MPRACT", "", ""), ;

    public final String fundGroupCode;
    public final String organizationCode;
    public final String objectCode;
    public final String subFundGroupCode;
    public final String chartOfAccountsCode;
    public final String accountNumber;

    private BalanceFixture(String fundGroupCode, String organizationCode, String objectCode, String subFundGroupCode, String chartOfAccountsCode, String accountNumber) {
        this.fundGroupCode = fundGroupCode;
        this.organizationCode = organizationCode;
        this.objectCode = objectCode;
        this.subFundGroupCode = subFundGroupCode;
        this.chartOfAccountsCode = chartOfAccountsCode;
        this.accountNumber = accountNumber;
    }

    @SuppressWarnings("deprecation")
    public Balance createBalance() {
        Balance balance = new Balance();

        balance.setAccount(new Account());
        balance.getAccount().setSubFundGroup(new SubFundGroup());

        balance.getAccount().getSubFundGroup().setFundGroupCode(this.fundGroupCode);
        balance.getAccount().setOrganizationCode(this.organizationCode);
        balance.setObjectCode(this.objectCode);
        balance.getAccount().setSubFundGroupCode(this.subFundGroupCode);
        balance.getAccount().setChartOfAccountsCode(this.chartOfAccountsCode);
        balance.getAccount().setAccountNumber(this.accountNumber);

        return balance;
    }

    public Balance createBalance(BusinessObjectService businessObjectService) {
        return (Balance) businessObjectService.retrieve(this.createBalance());
    }
}
