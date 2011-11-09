/*
 * Copyright 2005-2006 The Kuali Foundation
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

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.identity.Person;

/**
 * This class tests the Account service.
 */
@ConfigureContext
public class AccountServiceTest extends KualiTestBase {
    org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountServiceTest.class);

    public void testValidateAccount() {
        Account account = null;
        account = SpringContext.getBean(AccountService.class).getByPrimaryId("BA", "6044900");
        assertNotNull(account);
        // assertNotNull(account.getSubAccounts());
        // assertEquals("sub account list should contain 27 subaccounts", 27, account.getSubAccounts().size());

        account = null;
        account = SpringContext.getBean(AccountService.class).getByPrimaryId("XX", "0000000");
        assertNull(account);

        account = null;
        account = SpringContext.getBean(AccountService.class).getByPrimaryId("KO", "");
        assertNull(account);

        account = null;
        account = SpringContext.getBean(AccountService.class).getByPrimaryId("UA", null);
        assertNull(account);

        account = null;
        account = SpringContext.getBean(AccountService.class).getByPrimaryId(null, "1912610");
        assertNull(account);

        account = null;
        account = SpringContext.getBean(AccountService.class).getByPrimaryId(null, null);
        assertNull(account);
    }

    /**
     * This method tests whether a person has "responsibility" for certain accounts, which in turn determines if said user can edit
     * such accounts later.
     */
    // TODO this test uses hardcoded tests...how do we move to fixtures
    public void testAccountResponsibility() {
            Person rorenfro = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class).getPersonByPrincipalName("rorenfro");
            Person jaraujo = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class).getPersonByPrincipalName("jaraujo");
            Person rmunroe = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class).getPersonByPrincipalName("rmunroe");
            Person kcopley = SpringContext.getBean(org.kuali.rice.kim.api.identity.PersonService.class).getPersonByPrincipalName("kcopley");

            Account bl1031400 = SpringContext.getBean(AccountService.class).getByPrimaryId("BL", "1031400");
            Account ba9021104 = SpringContext.getBean(AccountService.class).getByPrimaryId("BA", "9021104");

            // 1. rorenfro is fiscal officer for BL-1031400, so she has responsibility
            assertTrue(SpringContext.getBean(AccountService.class).hasResponsibilityOnAccount(rorenfro, bl1031400));
            // 2. JARAUJO is account supervisor for BL-1031400...no responsibility
            assertFalse(SpringContext.getBean(AccountService.class).hasResponsibilityOnAccount(jaraujo, bl1031400));
            // 3. RMUNROE is a delegate of BA-901104...has responsibility
            assertTrue(SpringContext.getBean(AccountService.class).hasResponsibilityOnAccount(rmunroe, ba9021104));
            // 4. kcopley is not a delegate or fiscal officer of BL-1031400...no responsibility
            assertFalse(SpringContext.getBean(AccountService.class).hasResponsibilityOnAccount(kcopley, bl1031400));
    }
}

