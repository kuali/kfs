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
