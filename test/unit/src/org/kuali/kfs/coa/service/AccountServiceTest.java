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

import static org.kuali.kfs.util.SpringServiceLocator.*;

import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.module.chart.bo.Account;
import org.kuali.test.RequiresSpringContext;

/**
 * This class tests the Account service.
 * 
 * 
 */
@RequiresSpringContext
public class AccountServiceTest extends KualiTestBase {
    org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountServiceTest.class);

    public void testValidateAccount() {
        Account account = null;
        account = getAccountService().getByPrimaryId("BA", "6044900");
        assertNotNull(account);
        // assertNotNull(account.getSubAccounts());
        // assertEquals("sub account list should contain 27 subaccounts", 27, account.getSubAccounts().size());

        account = null;
        account = getAccountService().getByPrimaryId("XX", "0000000");
        assertNull(account);

        account = null;
        account = getAccountService().getByPrimaryId("KO", "");
        assertNull(account);

        account = null;
        account = getAccountService().getByPrimaryId("UA", null);
        assertNull(account);

        account = null;
        account = getAccountService().getByPrimaryId(null, "1912610");
        assertNull(account);

        account = null;
        account = getAccountService().getByPrimaryId(null, null);
        assertNull(account);
    }

    /**
     * 
     * This method tests whether a person has "responsibility" for certain accounts, which
     * in turn determines if said user can edit such accounts later.
     */
    // TODO this test uses hardcoded tests...how do we move to fixtures
    public void testAccountResponsibility() {
        try {
            UniversalUser rorenfro = getUniversalUserService().getUniversalUserByAuthenticationUserId("rorenfro");
            UniversalUser jaraujo = getUniversalUserService().getUniversalUserByAuthenticationUserId("jaraujo");
            UniversalUser rmunroe = getUniversalUserService().getUniversalUserByAuthenticationUserId("rmunroe");
            UniversalUser kcopley = getUniversalUserService().getUniversalUserByAuthenticationUserId("kcopley");
            
            Account bl1031400 = getAccountService().getByPrimaryId("BL", "1031400");
            Account ba9021104 = getAccountService().getByPrimaryId("BA", "9021104");
            
            // 1. RORENFRO is fiscal officer for BL-1031400, so she has responsibility
            assertTrue(getAccountService().hasResponsibilityOnAccount(rorenfro, bl1031400));
            // 2. JARAUJO is account supervisor for BL-1031400...no responsibility
            assertFalse(getAccountService().hasResponsibilityOnAccount(jaraujo, bl1031400));
            // 3. RMUNROE is a delegate of BA-901104...has responsibility
            assertTrue(getAccountService().hasResponsibilityOnAccount(rmunroe, ba9021104));
            // 4. KCOPLEY is not a delegate or fiscal officer of BL-1031400...no responsibility
            assertFalse(getAccountService().hasResponsibilityOnAccount(kcopley, bl1031400));
        }
        catch (UserNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
