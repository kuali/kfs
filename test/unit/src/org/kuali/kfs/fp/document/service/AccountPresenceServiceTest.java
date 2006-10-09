/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.financial.service;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.test.KualiTestBase;
import org.kuali.test.WithTestSpringContext;
import static org.kuali.test.fixtures.AccountFixture.ACCOUNT_NON_PRESENCE_ACCOUNT;
import static org.kuali.test.fixtures.AccountFixture.ACCOUNT_PRESENCE_ACCOUNT;
import static org.kuali.test.fixtures.ObjectCodeFixture.OBJECT_CODE_BUDGETED_OBJECT_CODE;
import static org.kuali.test.fixtures.ObjectCodeFixture.OBJECT_CODE_NON_BUDGET_OBJECT_CODE;

/**
 * This class tests the AccountPresenceService.
 * 
 * 
 */
@WithTestSpringContext
public class AccountPresenceServiceTest extends KualiTestBase {
    private AccountPresenceService accountPresenceService;
    private BusinessObjectService businessObjectService;

    protected void setUp() throws Exception {
        super.setUp();

        accountPresenceService = SpringServiceLocator.getAccountPresenceService();
        businessObjectService = SpringServiceLocator.getBusinessObjectService();
    }


    /**
     * Tests non budgeted object code for account with presence control fails service method.
     * 
     * @throws Exception
     */
    public void testAccountPresenceNonBudgetedObject() throws Exception {
        assertFalse("Non budgeded object code passed ", accountPresenceService.isObjectCodeBudgetedForAccountPresence(ACCOUNT_PRESENCE_ACCOUNT.createAccount(businessObjectService), OBJECT_CODE_NON_BUDGET_OBJECT_CODE.createObjectCode(businessObjectService)));

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
        assertTrue("non budgeted object code failed on account without presence control ", accountPresenceService.isObjectCodeBudgetedForAccountPresence(ACCOUNT_NON_PRESENCE_ACCOUNT.createAccount(businessObjectService), OBJECT_CODE_NON_BUDGET_OBJECT_CODE.createObjectCode(businessObjectService)));

    }

    /**
     * Tests budgeted object code passes for account without presence control.
     * 
     * @throws Exception
     */
    public void testAccountNonPresenceBudgetedObject() throws Exception {
        assertTrue("budgeted object code failed on account without presence control ", accountPresenceService.isObjectCodeBudgetedForAccountPresence(ACCOUNT_NON_PRESENCE_ACCOUNT.createAccount(businessObjectService), OBJECT_CODE_BUDGETED_OBJECT_CODE.createObjectCode(businessObjectService)));

    }
}
