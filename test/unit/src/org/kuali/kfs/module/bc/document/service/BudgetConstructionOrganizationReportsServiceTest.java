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

import java.util.List;

import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.budget.bo.BudgetConstructionOrganizationReports;
import org.kuali.test.ConfigureContext;

/**
 * This class...
 */
@ConfigureContext
public class BudgetConstructionOrganizationReportsServiceTest extends KualiTestBase {
    private BudgetConstructionOrganizationReportsService budgetConstructionOrganizationReportsService;
    private List<BudgetConstructionOrganizationReports> bcRptsToOrgs;

    private boolean runTests() { // change this to return false to prevent running tests
        return true;
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        // TODO Auto-generated method stub
        super.setUp();
        bcRptsToOrgs = null;
        budgetConstructionOrganizationReportsService = SpringContext.getBean(BudgetConstructionOrganizationReportsService.class);
    }

    public void testGetActiveChildOrgs() throws Exception {

        if (!runTests())
            return;

        bcRptsToOrgs = budgetConstructionOrganizationReportsService.getActiveChildOrgs("IU", "UNIV");
        assertTrue("Number IU-UNIV reporting orgs should be greater than 1", bcRptsToOrgs.size() > 1);

    }

}
