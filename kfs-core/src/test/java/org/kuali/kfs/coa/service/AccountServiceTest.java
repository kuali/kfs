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

