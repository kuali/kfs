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
package org.kuali.kfs.module.bc.identity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.fixture.UserNameFixture;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;

@ConfigureContext
public class BudgetConstructionRoleServiceImplTest extends KualiTestBase {

    protected Person regionalBudgetManager;
    protected Person universityAdministrationBudgetManager;
    protected Person bothManager;
    protected Person nonProcessor;
    protected RoleService roleService;
    @SuppressWarnings("unchecked")
    protected PersonService personService;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        regionalBudgetManager = UserNameFixture.ocmcnall.getPerson();
        universityAdministrationBudgetManager = UserNameFixture.wbrazil.getPerson();
        // hsoucy is a regional budget manager, a role assigned to the BC Processor role
        bothManager = UserNameFixture.hsoucy.getPerson();
        nonProcessor = UserNameFixture.appleton.getPerson();
        roleService = KimApiServiceLocator.getRoleService();
        personService = KimApiServiceLocator.getPersonService();
    }


    public void testRegionalBudgetManagerQualifications() {
        Map<String,String> qualification = new HashMap<String,String>();
        List<Map<String,String>> roleQualifiers =
                roleService.getRoleQualifersForPrincipalByNamespaceAndRolename(
                        regionalBudgetManager.getPrincipalId(),
                        BCConstants.BUDGET_CONSTRUCTION_NAMESPACE,
                        BCConstants.KimApiConstants.BC_PROCESSOR_ROLE_NAME,
                        qualification);
        assertNotNull( "roleQualifiers should not have returned null", roleQualifiers );
        assertFalse( "roleQualifiers should not be empty", roleQualifiers.isEmpty() );
        System.out.println( roleQualifiers );
        assertTrue( "Org EA-EA was not in the list and should have been.", checkForChartOrg( roleQualifiers, "EA", "EA") );
        assertFalse( "Org UA-UA was in the list and should not have been.", checkForChartOrg( roleQualifiers, "UA", "UA") );
    }

    public void testUABudgetManagerQualifications() {
        Map<String,String> qualification = new HashMap<String,String>();
        List<Map<String,String>> roleQualifiers =
                roleService.getRoleQualifersForPrincipalByNamespaceAndRolename(
                        universityAdministrationBudgetManager.getPrincipalId(),
                        BCConstants.BUDGET_CONSTRUCTION_NAMESPACE,
                        BCConstants.KimApiConstants.BC_PROCESSOR_ROLE_NAME,
                        qualification);
        assertNotNull( "roleQualifiers should not have returned null", roleQualifiers );
        assertFalse( "roleQualifiers should not be empty", roleQualifiers.isEmpty() );
        System.out.println( roleQualifiers );
        assertTrue( "Org UA-UA was not in the list and should have been.", checkForChartOrg( roleQualifiers, "UA", "UA") );
        assertFalse( "Org EA-EA was in the list and should not have been.", checkForChartOrg( roleQualifiers, "EA", "EA") );
    }

    public void testBothBudgetManagerQualifications() {
        Map<String,String> qualification = new HashMap<String,String>();
        List<Map<String,String>> roleQualifiers =
                roleService.getRoleQualifersForPrincipalByNamespaceAndRolename(
                        bothManager.getPrincipalId(),
                        BCConstants.BUDGET_CONSTRUCTION_NAMESPACE,
                        BCConstants.KimApiConstants.BC_PROCESSOR_ROLE_NAME,
                        qualification);
        assertNotNull( "roleQualifiers should not have returned null", roleQualifiers );
        assertFalse( "roleQualifiers should not be empty", roleQualifiers.isEmpty() );
        System.out.println( roleQualifiers );
        assertTrue( "Org UA-UA was not in the list and should have been.", checkForChartOrg( roleQualifiers, "UA", "UA") );
        assertTrue( "Org BL-BL was not in the list and should have been.", checkForChartOrg( roleQualifiers, "BL", "BL") );
    }

    public void testNonProcessorQualifications() {
        Map<String,String> qualification = new HashMap<String,String>();
        List<Map<String,String>> roleQualifiers =
                roleService.getRoleQualifersForPrincipalByNamespaceAndRolename(
                        nonProcessor.getPrincipalId(),
                        BCConstants.BUDGET_CONSTRUCTION_NAMESPACE,
                        BCConstants.KimApiConstants.BC_PROCESSOR_ROLE_NAME,
                        qualification);
        assertNotNull( "roleQualifiers should not have returned null", roleQualifiers );
        System.out.println( roleQualifiers );
        assertTrue( "roleQualifiers should have been empty", roleQualifiers.isEmpty() );
    }

    private boolean checkForChartOrg( List<Map<String,String>> roleQualifiers, String chart, String org ) {
        boolean found = false;
        for ( Map<String,String> q : roleQualifiers ) {
            if ( StringUtils.equals( q.get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE), chart )
                    && StringUtils.equals( q.get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE), org ) ) {
                found = true;
            }
        }
        return found;
    }


}
