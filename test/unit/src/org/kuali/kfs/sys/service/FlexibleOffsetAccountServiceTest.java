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

import java.lang.reflect.InvocationTargetException;

import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.financial.bo.OffsetAccount;
import org.kuali.test.KualiTestBaseWithFixtures;
import org.kuali.test.WithTestSpringContext;

/**
 * This class...
 * 
 * @author Kuali Financial Transactions Team ()
 */
@WithTestSpringContext
public class FlexibleOffsetAccountServiceTest extends KualiTestBaseWithFixtures {
    private FlexibleOffsetAccountService service;

    protected void setUp() throws Exception {
        super.setUp();
        service = SpringServiceLocator.getFlexibleOffsetAccountService();
    }

    public void testGetByPrimaryId_valid() throws NoSuchMethodException, InvocationTargetException {
        service.setKualiConfigurationService(createMockConfigurationService(true));
        OffsetAccount offsetAccount = service.getByPrimaryIdIfEnabled(getFixtureString("blChartOfAccounts"), getFixtureString("blFlexAccountNumber"), getFixtureString("tofOffsetObjectCode"));
        assertSparselyEqualFixture("offsetAccount1", offsetAccount);
        assertEquals(getFixtureString("blChartOfAccounts"), offsetAccount.getChart().getChartOfAccountsCode());
        assertEquals(getFixtureString("blFlexAccountNumber"), offsetAccount.getAccount().getAccountNumber());
        assertEquals(getFixtureString("uaChartOfAccounts"), offsetAccount.getFinancialOffsetChartOfAccount().getChartOfAccountsCode());
        assertEquals(getFixtureString("uaAccountNumber1"), offsetAccount.getFinancialOffsetAccount().getAccountNumber());
    }

    public void testGetByPrimaryId_validDisabled() throws NoSuchMethodException, InvocationTargetException {
        service.setKualiConfigurationService(createMockConfigurationService(false));
        assertNull(service.getByPrimaryIdIfEnabled(getFixtureString("blChartOfAccounts"), getFixtureString("blFlexAccountNumber"), getFixtureString("tofOffsetObjectCode")));
    }

    public void testGetByPrimaryId_invalid() {
        service.setKualiConfigurationService(createMockConfigurationService(true));
        assertNull(service.getByPrimaryIdIfEnabled("XX", "XX", "XX"));
    }

    public void testSingletonService() {
        assertSame(service, SpringServiceLocator.getFlexibleOffsetAccountService());
        SpringServiceLocator.getFlexibleOffsetAccountService().setKualiConfigurationService(createMockConfigurationService(true));
        assertEquals(true, SpringServiceLocator.getFlexibleOffsetAccountService().getEnabled());
        SpringServiceLocator.getFlexibleOffsetAccountService().setKualiConfigurationService(createMockConfigurationService(false));
        assertEquals(false, SpringServiceLocator.getFlexibleOffsetAccountService().getEnabled());
    }

    /**
     * Integration test to the database parameter table.
     */
    public void testGetEnabled() {
        // This tests that no RuntimeException is thrown because the parameter is missing from the database
        // or contains a value other than Y or N.
        service.getEnabled();
    }

    private String getFixtureString(String fixtureName) {
        return (String) getFixtureEntry(fixtureName).createObject();
    }
}
