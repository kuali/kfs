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
package org.kuali.kfs.fp.document.service;

import static org.kuali.kfs.sys.fixture.AccountFixture.ACCOUNT_NON_PRESENCE_ACCOUNT;
import static org.kuali.kfs.sys.fixture.AccountFixture.ACCOUNT_PRESENCE_ACCOUNT;
import static org.kuali.kfs.sys.fixture.ObjectCodeFixture.OBJECT_CODE_BUDGETED_OBJECT_CODE;
import static org.kuali.kfs.sys.fixture.ObjectCodeFixture.OBJECT_CODE_NON_BUDGET_OBJECT_CODE;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.service.AccountPresenceService;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * This class tests the AccountPresenceService.
 */
@ConfigureContext
public class AccountPresenceServiceTest extends KualiTestBase {

    /**
     * Tests non budgeted object code for account with presence control fails service method.
     * 
     * @throws Exception
     */
    public void testAccountPresenceNonBudgetedObject() throws Exception {
        assertFalse("Non budgeded object code passed ", SpringContext.getBean(AccountPresenceService.class).isObjectCodeBudgetedForAccountPresence(ACCOUNT_PRESENCE_ACCOUNT.createAccount(SpringContext.getBean(BusinessObjectService.class)), OBJECT_CODE_NON_BUDGET_OBJECT_CODE.createObjectCode(SpringContext.getBean(BusinessObjectService.class))));

    }

    /**
     * Tests budgeted object code for account with presence control passes service method.
     * 
     * @throws Exception
     */
    // public void testAccountPresenceBudgetedObject() throws Exception {
    // assertTrue("Budgeted object code failed ",
    // accountPresenceService.isObjectCodeBudgetedForAccountPresence(getAccountWithPresenceControl(),
    // getBudgetedObjectCode()));
    //
    // }
    /**
     * Tests non budgeted object code passes for account without presence control.
     * 
     * @throws Exception
     */
    public void testAccountNonPresenceNonBudgetedObject() throws Exception {
        assertTrue("non budgeted object code failed on account without presence control ", SpringContext.getBean(AccountPresenceService.class).isObjectCodeBudgetedForAccountPresence(ACCOUNT_NON_PRESENCE_ACCOUNT.createAccount(SpringContext.getBean(BusinessObjectService.class)), OBJECT_CODE_NON_BUDGET_OBJECT_CODE.createObjectCode(SpringContext.getBean(BusinessObjectService.class))));

    }

    /**
     * Tests budgeted object code passes for account without presence control.
     * 
     * @throws Exception
     */
    public void testAccountNonPresenceBudgetedObject() throws Exception {
        assertTrue("budgeted object code failed on account without presence control ", SpringContext.getBean(AccountPresenceService.class).isObjectCodeBudgetedForAccountPresence(ACCOUNT_NON_PRESENCE_ACCOUNT.createAccount(SpringContext.getBean(BusinessObjectService.class)), OBJECT_CODE_BUDGETED_OBJECT_CODE.createObjectCode(SpringContext.getBean(BusinessObjectService.class))));

    }
}
