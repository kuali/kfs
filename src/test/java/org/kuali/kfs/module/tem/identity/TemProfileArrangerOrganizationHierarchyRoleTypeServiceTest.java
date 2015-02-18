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
package org.kuali.kfs.module.tem.identity;

import org.apache.log4j.Logger;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;

@ConfigureContext
public class TemProfileArrangerOrganizationHierarchyRoleTypeServiceTest extends KualiTestBase {

    public static Logger LOG = Logger.getLogger(TemProfileArrangerOrganizationHierarchyRoleTypeServiceTest.class);

    private TemProfileArrangerOrganizationHierarchyRoleTypeServiceImpl temProfileArrangerOrganizationHierarchyRoleService;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        temProfileArrangerOrganizationHierarchyRoleService = SpringContext.getBean(TemProfileArrangerOrganizationHierarchyRoleTypeServiceImpl.class);
    }

    public final void testIsParentOrg() {

        // BA ACPR reports to BA ACAC
        // BA ADAF reports to BL DBFA

        LOG.debug("testing no qualifications");
        assertFalse(temProfileArrangerOrganizationHierarchyRoleService.isParentOrg(null, null, "BA", "ACPR", true));

        LOG.debug("testing no roles");
        assertFalse(temProfileArrangerOrganizationHierarchyRoleService.isParentOrg("BA", "ACPR", null, null, true));

        LOG.debug("testing no organization code role");
        assertTrue(temProfileArrangerOrganizationHierarchyRoleService.isParentOrg("BA", "ACPR", "BA", null, true));

        LOG.debug("testing no org code role with chart code child of role chart code");
        assertTrue(temProfileArrangerOrganizationHierarchyRoleService.isParentOrg("BA", "ADAF", "BL", null, true));

        LOG.debug("testing chart and org codes the same as the rold");
        assertTrue(temProfileArrangerOrganizationHierarchyRoleService.isParentOrg("BA", "ACPR", "BA", "ACPR", true));

        LOG.debug("testing the chart and org codes are the children of the role");
        assertTrue(temProfileArrangerOrganizationHierarchyRoleService.isParentOrg("BA", "ADAF", "BL", "DBFA", true));

    }
}

