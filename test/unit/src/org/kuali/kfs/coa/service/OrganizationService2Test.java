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
package org.kuali.module.chart.service;

import java.util.Collections;
import java.util.List;

import org.kuali.core.util.SpringServiceLocator;
import org.kuali.test.KualiTestBaseWithFixtures;
import org.kuali.test.WithTestSpringContext;

@WithTestSpringContext
public class OrganizationService2Test extends KualiTestBaseWithFixtures {

    private static final String GOOD_CHART = "BL";
    private static final String GOOD_ORG = "PSY";
    private static final String BAD_CHART = "ZZ";
    private static final String BAD_ORG = "ZZZ";
    private static final String GOOD_ORG2 = "BUS";

    private OrganizationService organizationService;

    protected void setUp() throws Exception {
        super.setUp();
        organizationService = SpringServiceLocator.getOrganizationService();
    }

    public void testGetActiveAccountsByOrg_good() {

        List accounts;

        accounts = organizationService.getActiveAccountsByOrg(GOOD_CHART, GOOD_ORG);

        assertFalse("List of Accounts should not contain no elements.", accounts.size() == 0);
        assertFalse("List of Accounts should not be empty.", accounts.isEmpty());

    }

    public void testGetActiveAccountsByOrg_bad() {

        List accounts;

        accounts = organizationService.getActiveAccountsByOrg(BAD_CHART, BAD_ORG);

        assertEquals(Collections.EMPTY_LIST, accounts);
        assertTrue("List of Accounts should contain no elements.", accounts.size() == 0);
        assertTrue("List of Accounts should be empty.", accounts.isEmpty());

    }

    public void testGetActiveChildOrgs_good() {

        List orgs;

        orgs = organizationService.getActiveChildOrgs(GOOD_CHART, GOOD_ORG2);

        assertFalse("List of Orgs should not contain no elements.", orgs.size() == 0);
        assertFalse("List of Orgs should not be empty.", orgs.isEmpty());

    }

    public void testGetActiveChildOrgs_bad() {

        List orgs;

        orgs = organizationService.getActiveChildOrgs(BAD_CHART, BAD_ORG);

        assertEquals(Collections.EMPTY_LIST, orgs);
        assertTrue("List of Orgs should contain no elements.", orgs.size() == 0);
        assertTrue("List of Orgs should be empty.", orgs.isEmpty());

    }

}
