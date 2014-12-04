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
package org.kuali.kfs.coa.identity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.service.ChartService;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.api.role.RoleMembership;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;

@ConfigureContext
public class ChartManagerRoleTest extends KualiTestBase {


    public void testGettingPersonForChartManagers() {
        RoleService rms = KimApiServiceLocator.getRoleService();
        List<String> roleIds = new ArrayList<String>();

        Map<String,String> qualification = new HashMap<String,String>();

        for ( String chart : SpringContext.getBean(ChartService.class).getAllChartCodes() ) {

            qualification.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, chart);

//            System.out.println( chart );
            roleIds.add(rms.getRoleIdByNamespaceCodeAndName(KFSConstants.CoreModuleNamespaces.KFS, KFSConstants.SysKimApiConstants.CHART_MANAGER_KIM_ROLE_NAME));
            Collection<RoleMembership> chartManagers = rms.getRoleMembers(roleIds, qualification);
//            System.out.println( chartManagers );

            assertEquals( "There should be only one chart manager per chart: " + chart, 1, chartManagers.size() );

            for ( RoleMembership rmi : chartManagers ) {
                Person chartManager = SpringContext.getBean(PersonService.class).getPerson( rmi.getMemberId() );
                System.out.println( chartManager );
                assertNotNull( "unable to retrieve person object for principalId: " + rmi.getMemberId(), chartManager );
                assertFalse( "name should not have been blank", chartManager.getName().equals("") );
                assertFalse( "campus code should not have been blank", chartManager.getCampusCode().equals("") );
            }
        }
    }
}
