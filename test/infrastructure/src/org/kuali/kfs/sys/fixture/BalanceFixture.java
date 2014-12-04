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
