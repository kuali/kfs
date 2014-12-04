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
package org.kuali.kfs.sys.service;

import static org.kuali.kfs.sys.KualiTestAssertionUtils.assertSparselyEqualBean;
import static org.kuali.kfs.sys.fixture.OffsetAccountFixture.OFFSET_ACCOUNT1;

import org.kuali.kfs.coa.businessobject.OffsetDefinition;
import org.kuali.kfs.fp.businessobject.OffsetAccount;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

/**
 * This class...
 */
@ConfigureContext
public class FlexibleOffsetAccountServiceTest extends KualiTestBase {

    public void testGetByPrimaryId_valid() throws Exception {
        boolean enabled = SpringContext.getBean(ParameterService.class).getParameterValueAsBoolean(OffsetDefinition.class, KFSConstants.SystemGroupParameterNames.FLEXIBLE_OFFSET_ENABLED_FLAG);

        TestUtils.setSystemParameter(OffsetDefinition.class, KFSConstants.SystemGroupParameterNames.FLEXIBLE_OFFSET_ENABLED_FLAG, "Y");
        OffsetAccount offsetAccount = SpringContext.getBean(FlexibleOffsetAccountService.class).getByPrimaryIdIfEnabled(OFFSET_ACCOUNT1.chartOfAccountsCode, OFFSET_ACCOUNT1.accountNumber, OFFSET_ACCOUNT1.financialOffsetObjectCode);
        if (offsetAccount == null) {
            throw new RuntimeException("Offset Account came back null, cannot perform asserts.");
        }

        assertSparselyEqualBean(OFFSET_ACCOUNT1.createOffsetAccount(), offsetAccount);
        assertEquals(OFFSET_ACCOUNT1.chartOfAccountsCode, offsetAccount.getChart().getChartOfAccountsCode());
        assertEquals(OFFSET_ACCOUNT1.accountNumber, offsetAccount.getAccount().getAccountNumber());
        assertEquals(OFFSET_ACCOUNT1.financialOffsetChartOfAccountCode, offsetAccount.getFinancialOffsetChartOfAccount().getChartOfAccountsCode());
        assertEquals(OFFSET_ACCOUNT1.financialOffsetAccountNumber, offsetAccount.getFinancialOffsetAccount().getAccountNumber());
    }

    public void testGetByPrimaryId_validDisabled() throws Exception {
        TestUtils.setSystemParameter(OffsetDefinition.class, KFSConstants.SystemGroupParameterNames.FLEXIBLE_OFFSET_ENABLED_FLAG, "N");
        assertNull(SpringContext.getBean(FlexibleOffsetAccountService.class).getByPrimaryIdIfEnabled(OFFSET_ACCOUNT1.chartOfAccountsCode, OFFSET_ACCOUNT1.accountNumber, OFFSET_ACCOUNT1.financialOffsetAccountNumber));
    }

    public void testGetByPrimaryId_invalid() throws Exception {
        TestUtils.setSystemParameter(OffsetDefinition.class, KFSConstants.SystemGroupParameterNames.FLEXIBLE_OFFSET_ENABLED_FLAG, "N");
        assertNull(SpringContext.getBean(FlexibleOffsetAccountService.class).getByPrimaryIdIfEnabled("XX", "XX", "XX"));
    }

    /**
     * Integration test to the database parameter table (not the mock configuration service).
     */
    public void testGetEnabled() {
        // This tests that no RuntimeException is thrown because the parameter is missing from the database
        // or contains a value other than Y or N.
        SpringContext.getBean(FlexibleOffsetAccountService.class).getEnabled();
    }
}
