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

