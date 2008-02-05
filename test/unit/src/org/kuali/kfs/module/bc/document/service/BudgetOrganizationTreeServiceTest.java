/*
 * Copyright 2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.module.budget.service;

import java.util.HashMap;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.budget.bo.BudgetConstructionPullup;
import org.kuali.test.ConfigureContext;

/**
 * This class...
 */
@ConfigureContext
public class BudgetOrganizationTreeServiceTest extends KualiTestBase {
    private BudgetOrganizationTreeService budgetOrganizationTreeService;
    private BusinessObjectService businessObjectService;

    private String personUniversalIdentifier = "1111111111";
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
//        budgetOrganizationTreeService.buildPullup(personUniversalIdentifier, chartOfAccountsCode, organizationCode);
//        HashMap map = new HashMap();
//        map.put("personUniversalIdentifier", personUniversalIdentifier);
//        map.put("chartOfAccountsCode", chartOfAccountsCode);
//        map.put("organizationCode", organizationCode);
//
//        // verify that the root is in the tree at the least
//        BudgetConstructionPullup bcPullup = (BudgetConstructionPullup) businessObjectService.findByPrimaryKey(BudgetConstructionPullup.class, map);
//        assertTrue(bcPullup.getChartOfAccountsCode().equalsIgnoreCase(chartOfAccountsCode));
//        assertTrue(bcPullup.getOrganizationCode().equalsIgnoreCase(organizationCode));
//        assertTrue(bcPullup.getPersonUniversalIdentifier().equalsIgnoreCase(personUniversalIdentifier));
//
//        budgetOrganizationTreeService.cleanPullup(personUniversalIdentifier);
//        bcPullup = (BudgetConstructionPullup) businessObjectService.findByPrimaryKey(BudgetConstructionPullup.class, map);
//        assertTrue(bcPullup == null);
//    }

    @ConfigureContext(shouldCommitTransactions = true)
    public void testBuildPullupSql() throws Exception {

        if (!runTests())
            return;

        budgetOrganizationTreeService.buildPullupSql(personUniversalIdentifier, chartOfAccountsCode, organizationCode);
        HashMap map = new HashMap();
        map.put("personUniversalIdentifier", personUniversalIdentifier);
        map.put("chartOfAccountsCode", chartOfAccountsCode);
        map.put("organizationCode", organizationCode);

        BudgetConstructionPullup bcPullup = (BudgetConstructionPullup) businessObjectService.findByPrimaryKey(BudgetConstructionPullup.class, map);
        assertTrue(bcPullup.getChartOfAccountsCode().equalsIgnoreCase(chartOfAccountsCode));
        assertTrue(bcPullup.getOrganizationCode().equalsIgnoreCase(organizationCode));
        assertTrue(bcPullup.getPersonUniversalIdentifier().equalsIgnoreCase(personUniversalIdentifier));

        budgetOrganizationTreeService.cleanPullup(personUniversalIdentifier);
        bcPullup = (BudgetConstructionPullup) businessObjectService.findByPrimaryKey(BudgetConstructionPullup.class, map);
        assertTrue(bcPullup == null);
    }
}
