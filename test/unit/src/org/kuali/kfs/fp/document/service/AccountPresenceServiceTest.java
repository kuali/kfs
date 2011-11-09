/*
 * Copyright 2006 The Kuali Foundation
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
