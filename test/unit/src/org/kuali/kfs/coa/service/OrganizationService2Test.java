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
package org.kuali.kfs.coa.service;

import java.util.Collections;
import java.util.List;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;

@ConfigureContext
public class OrganizationService2Test extends KualiTestBase {

    private static final String GOOD_CHART = "BL";
    private static final String GOOD_ORG = "PSY";
    private static final String BAD_CHART = "ZZ";
    private static final String BAD_ORG = "ZZZ";
    private static final String GOOD_ORG2 = "BUS";

    public void testGetActiveAccountsByOrg_good() {

        List accounts;

        accounts = SpringContext.getBean(OrganizationService.class).getActiveAccountsByOrg(GOOD_CHART, GOOD_ORG);

        assertFalse("List of Accounts should not contain no elements.", accounts.size() == 0);
        assertFalse("List of Accounts should not be empty.", accounts.isEmpty());

    }

    public void testGetActiveAccountsByOrg_bad() {

        List accounts;

        accounts = SpringContext.getBean(OrganizationService.class).getActiveAccountsByOrg(BAD_CHART, BAD_ORG);

        assertEquals(Collections.EMPTY_LIST, accounts);
        assertTrue("List of Accounts should contain no elements.", accounts.size() == 0);
        assertTrue("List of Accounts should be empty.", accounts.isEmpty());

    }

    public void testGetActiveChildOrgs_good() {

        List orgs;

        orgs = SpringContext.getBean(OrganizationService.class).getActiveChildOrgs(GOOD_CHART, GOOD_ORG2);

        assertFalse("List of Orgs should not contain no elements.", orgs.size() == 0);
        assertFalse("List of Orgs should not be empty.", orgs.isEmpty());

    }

    public void testGetActiveChildOrgs_bad() {

        List orgs;

        orgs = SpringContext.getBean(OrganizationService.class).getActiveChildOrgs(BAD_CHART, BAD_ORG);

        assertEquals(Collections.EMPTY_LIST, orgs);
        assertTrue("List of Orgs should contain no elements.", orgs.size() == 0);
        assertTrue("List of Orgs should be empty.", orgs.isEmpty());

    }

}
