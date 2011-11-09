/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.bc.document.service;

import java.util.HashMap;

import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPullup;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * This class...
 */
@ConfigureContext
public class BudgetOrganizationTreeServiceTest extends KualiTestBase {
    private BudgetOrganizationTreeService budgetOrganizationTreeService;
    private BusinessObjectService businessObjectService;

    private String principalId = "1111111111";
    private String chartOfAccountsCode = "UA";
    private String organizationCode = "UA";

    private boolean runTests() { // change this to return false to prevent running tests
        return true;
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {

        super.setUp();
        budgetOrganizationTreeService = SpringContext.getBean(BudgetOrganizationTreeService.class);
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
    }

//  used to compare OJB version with JDBC version - leave out for now
//    @ConfigureContext(shouldCommitTransactions = true)
//    public void testBuildPullup() throws Exception {
//
//        if (!runTests())
//            return;
//
//        budgetOrganizationTreeService.buildPullup(principalId, chartOfAccountsCode, organizationCode);
//        HashMap map = new HashMap();
//        map.put("principalId", principalId);
//        map.put("chartOfAccountsCode", chartOfAccountsCode);
//        map.put("organizationCode", organizationCode);
//
//        // verify that the root is in the tree at the least
//        BudgetConstructionPullup bcPullup = (BudgetConstructionPullup) businessObjectService.findByPrimaryKey(BudgetConstructionPullup.class, map);
//        assertTrue(bcPullup.getChartOfAccountsCode().equalsIgnoreCase(chartOfAccountsCode));
//        assertTrue(bcPullup.getOrganizationCode().equalsIgnoreCase(organizationCode));
//        assertTrue(bcPullup.getPrincipalId().equalsIgnoreCase(principalId));
//
//        budgetOrganizationTreeService.cleanPullup(principalId);
//        bcPullup = (BudgetConstructionPullup) businessObjectService.findByPrimaryKey(BudgetConstructionPullup.class, map);
//        assertTrue(bcPullup == null);
//    }

    @ConfigureContext(shouldCommitTransactions = true)
    public void testBuildPullupSql() throws Exception {

        if (!runTests())
            return;

        budgetOrganizationTreeService.buildPullupSql(principalId, chartOfAccountsCode, organizationCode);
        HashMap map = new HashMap();
        map.put("principalId", principalId);
        map.put("chartOfAccountsCode", chartOfAccountsCode);
        map.put("organizationCode", organizationCode);

        BudgetConstructionPullup bcPullup = (BudgetConstructionPullup) businessObjectService.findByPrimaryKey(BudgetConstructionPullup.class, map);
        assertTrue(bcPullup.getChartOfAccountsCode().equalsIgnoreCase(chartOfAccountsCode));
        assertTrue(bcPullup.getOrganizationCode().equalsIgnoreCase(organizationCode));
        assertTrue(bcPullup.getPrincipalId().equalsIgnoreCase(principalId));

        budgetOrganizationTreeService.cleanPullup(principalId);
        bcPullup = (BudgetConstructionPullup) businessObjectService.findByPrimaryKey(BudgetConstructionPullup.class, map);
        assertTrue(bcPullup == null);
    }
}

