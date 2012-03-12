/*
 * Copyright 2005 The Kuali Foundation
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
