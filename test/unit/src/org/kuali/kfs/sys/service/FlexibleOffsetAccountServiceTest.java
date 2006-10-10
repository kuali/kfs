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

import static org.kuali.core.util.SpringServiceLocator.*;
import org.kuali.module.financial.bo.OffsetAccount;
import org.kuali.test.KualiTestBase;
import static org.kuali.test.MockServiceUtils.mockConfigurationServiceForFlexibleOffsetEnabled;
import org.kuali.test.WithTestSpringContext;
import static org.kuali.test.fixtures.OffsetAccountFixture.OFFSET_ACCOUNT1;

/**
 * This class...
 * 
 * 
 */
@WithTestSpringContext
public class FlexibleOffsetAccountServiceTest extends KualiTestBase {

    public void testGetByPrimaryId_valid() throws NoSuchMethodException, InvocationTargetException {
        mockConfigurationServiceForFlexibleOffsetEnabled(true);
        OffsetAccount offsetAccount = getFlexibleOffsetAccountService().getByPrimaryIdIfEnabled(OFFSET_ACCOUNT1.chartOfAccountsCode, OFFSET_ACCOUNT1.accountNumber, OFFSET_ACCOUNT1.financialOffsetObjectCode);
        assertSparselyEqualBean(OFFSET_ACCOUNT1.createOffsetAccount(), offsetAccount);
        assertEquals(OFFSET_ACCOUNT1.chartOfAccountsCode, offsetAccount.getChart().getChartOfAccountsCode());
        assertEquals(OFFSET_ACCOUNT1.accountNumber, offsetAccount.getAccount().getAccountNumber());
        assertEquals(OFFSET_ACCOUNT1.financialOffsetChartOfAccountCode, offsetAccount.getFinancialOffsetChartOfAccount().getChartOfAccountsCode());
        assertEquals(OFFSET_ACCOUNT1.financialOffsetAccountNumber, offsetAccount.getFinancialOffsetAccount().getAccountNumber());
    }

    public void testGetByPrimaryId_validDisabled() throws NoSuchMethodException, InvocationTargetException {
        mockConfigurationServiceForFlexibleOffsetEnabled(false);
        assertNull(getFlexibleOffsetAccountService().getByPrimaryIdIfEnabled(OFFSET_ACCOUNT1.chartOfAccountsCode, OFFSET_ACCOUNT1.accountNumber, OFFSET_ACCOUNT1.financialOffsetAccountNumber));
    }

    public void testGetByPrimaryId_invalid() {
        mockConfigurationServiceForFlexibleOffsetEnabled(true);
        assertNull(getFlexibleOffsetAccountService().getByPrimaryIdIfEnabled("XX", "XX", "XX"));
    }

    public void testMockService() {
        assertSame(getFlexibleOffsetAccountService(), getFlexibleOffsetAccountService());
        mockConfigurationServiceForFlexibleOffsetEnabled(true);
        assertEquals(true, getFlexibleOffsetAccountService().getEnabled());
        restoreServicesIfMocked();
        mockConfigurationServiceForFlexibleOffsetEnabled(false);
        assertEquals(false, getFlexibleOffsetAccountService().getEnabled());
    }

    /**
     * Integration test to the database parameter table (not the mock configuration service).
     */
    public void testGetEnabled() {
        // This tests that no RuntimeException is thrown because the parameter is missing from the database
        // or contains a value other than Y or N.
        getFlexibleOffsetAccountService().getEnabled();
    }
}
