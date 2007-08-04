/*
 * Copyright 2005-2007 The Kuali Foundation.
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
package org.kuali.module.chart.service;

import static org.kuali.kfs.util.SpringServiceLocator.getSubAccountService;

import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.module.chart.bo.A21SubAccount;
import org.kuali.module.chart.bo.SubAccount;
import org.kuali.test.ConfigureContext;
import org.kuali.test.fixtures.SubAccountFixture;

/**
 * This class tests the SubAccount service.
 */
@ConfigureContext
public class SubAccountServiceTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SubAccountServiceTest.class);

    private final static SubAccount subAccount = SubAccountFixture.VALID_SUB_ACCOUNT.createSubAccount();


    public void testA21SubAccount() {
        SubAccount sa = getSubAccountService().getByPrimaryId(subAccount.getChartOfAccountsCode(), subAccount.getAccountNumber(), subAccount.getSubAccountNumber());

        assertTrue("expect to find this sub account: " + subAccount.getChartOfAccountsCode() + "/" + subAccount.getAccountNumber() + "/" + subAccount.getSubAccountNumber(), ObjectUtils.isNotNull(sa));
        A21SubAccount a21 = sa.getA21SubAccount();
        assertTrue("expect this to have a21subaccount", ObjectUtils.isNotNull(a21));
        a21.getIndirectCostRecoveryAccount();
    }

    public void testGetByPrimaryId() throws Exception {
        SubAccount sa = new SubAccount();
        sa.setAccountNumber(subAccount.getAccountNumber());
        sa.setChartOfAccountsCode(subAccount.getChartOfAccountsCode());
        sa.setSubAccountNumber(subAccount.getSubAccountNumber());

        SubAccount retrieved = getSubAccountService().getByPrimaryId(subAccount.getChartOfAccountsCode(), subAccount.getAccountNumber(), subAccount.getSubAccountNumber());
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

        SubAccount retrieved = getSubAccountService().getByPrimaryIdWithCaching(subAccount.getChartOfAccountsCode(), subAccount.getAccountNumber(), subAccount.getSubAccountNumber());
        assertTrue("Didn't retrieve sub account", ObjectUtils.isNotNull(retrieved));
        assertEquals("Wrong chart", subAccount.getChartOfAccountsCode(), retrieved.getChartOfAccountsCode());
        assertEquals("Wrong account", subAccount.getAccountNumber(), retrieved.getAccountNumber());
        assertEquals("Wrong Sub account number", subAccount.getSubAccountNumber(), retrieved.getSubAccountNumber());
    }


}
