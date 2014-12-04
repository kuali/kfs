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
package org.kuali.kfs.coa.service;

import org.kuali.kfs.coa.businessobject.A21SubAccount;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.fixture.SubAccountFixture;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class tests the SubAccount service.
 */
@ConfigureContext
public class SubAccountServiceTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SubAccountServiceTest.class);

    private final static SubAccount subAccount = SubAccountFixture.VALID_SUB_ACCOUNT.createSubAccount();


    public void testA21SubAccount() {
        SubAccount sa = SpringContext.getBean(SubAccountService.class).getByPrimaryId(subAccount.getChartOfAccountsCode(), subAccount.getAccountNumber(), subAccount.getSubAccountNumber());

        assertTrue("expect to find this sub account: " + subAccount.getChartOfAccountsCode() + "/" + subAccount.getAccountNumber() + "/" + subAccount.getSubAccountNumber(), ObjectUtils.isNotNull(sa));
        A21SubAccount a21 = sa.getA21SubAccount();
        assertTrue("expect this to have a21subaccount", ObjectUtils.isNotNull(a21));
        //remove reference to the obsolete account object - and there is not real test being done
        //a21.getIndirectCostRecoveryAcct();
    }

    public void testGetByPrimaryId() throws Exception {
        SubAccount sa = new SubAccount();
        sa.setAccountNumber(subAccount.getAccountNumber());
        sa.setChartOfAccountsCode(subAccount.getChartOfAccountsCode());
        sa.setSubAccountNumber(subAccount.getSubAccountNumber());

        SubAccount retrieved = SpringContext.getBean(SubAccountService.class).getByPrimaryId(subAccount.getChartOfAccountsCode(), subAccount.getAccountNumber(), subAccount.getSubAccountNumber());
        assertTrue("Didn't retrieve sub account", ObjectUtils.isNotNull(retrieved));
        assertEquals("Wrong chart", subAccount.getChartOfAccountsCode(), retrieved.getChartOfAccountsCode());
        assertEquals("Wrong account", subAccount.getAccountNumber(), retrieved.getAccountNumber());
        assertEquals("Wrong Sub account number", subAccount.getSubAccountNumber(), retrieved.getSubAccountNumber());
    }

    public void testGetByPrimaryIdWithCaching() throws Exception {
        SubAccount sa = new SubAccount();
        sa.setAccountNumber(subAccount.getAccountNumber());
        sa.setChartOfAccountsCode(subAccount.getChartOfAccountsCode());
        sa.setSubAccountNumber(subAccount.getSubAccountNumber());

        SubAccount retrieved = SpringContext.getBean(SubAccountService.class).getByPrimaryIdWithCaching(subAccount.getChartOfAccountsCode(), subAccount.getAccountNumber(), subAccount.getSubAccountNumber());
        assertTrue("Didn't retrieve sub account", ObjectUtils.isNotNull(retrieved));
        assertEquals("Wrong chart", subAccount.getChartOfAccountsCode(), retrieved.getChartOfAccountsCode());
        assertEquals("Wrong account", subAccount.getAccountNumber(), retrieved.getAccountNumber());
        assertEquals("Wrong Sub account number", subAccount.getSubAccountNumber(), retrieved.getSubAccountNumber());
    }


}
