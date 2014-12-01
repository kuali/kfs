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

import java.util.List;

import org.kuali.kfs.module.bc.businessobject.BudgetConstructionOrganizationReports;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;

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
