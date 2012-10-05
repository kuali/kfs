/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.module.bc.identity;

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
        List<Map<String,String>> roleQualifiers =
                roleService.getRoleQualifersForPrincipalByNamespaceAndRolename(
                        regionalBudgetManager.getPrincipalId(),
                        BCConstants.BUDGET_CONSTRUCTION_NAMESPACE,
                        BCConstants.KimApiConstants.BC_PROCESSOR_ROLE_NAME,
                        null);
        assertNotNull( "roleQualifiers should not have returned null", roleQualifiers );
        assertFalse( "roleQualifiers should not be empty", roleQualifiers.isEmpty() );
        System.out.println( roleQualifiers );
        assertTrue( "Org EA-EA was not in the list and should have been.", checkForChartOrg( roleQualifiers, "EA", "EA") );
        assertFalse( "Org UA-UA was in the list and should not have been.", checkForChartOrg( roleQualifiers, "UA", "UA") );
    }

    public void testUABudgetManagerQualifications() {
        List<Map<String,String>> roleQualifiers =
                roleService.getRoleQualifersForPrincipalByNamespaceAndRolename(
                        universityAdministrationBudgetManager.getPrincipalId(),
                        BCConstants.BUDGET_CONSTRUCTION_NAMESPACE,
                        BCConstants.KimApiConstants.BC_PROCESSOR_ROLE_NAME,
                        null);
        assertNotNull( "roleQualifiers should not have returned null", roleQualifiers );
        assertFalse( "roleQualifiers should not be empty", roleQualifiers.isEmpty() );
        System.out.println( roleQualifiers );
        assertTrue( "Org UA-UA was not in the list and should have been.", checkForChartOrg( roleQualifiers, "UA", "UA") );
        assertFalse( "Org EA-EA was in the list and should not have been.", checkForChartOrg( roleQualifiers, "EA", "EA") );
    }

    public void testBothBudgetManagerQualifications() {
        List<Map<String,String>> roleQualifiers =
                roleService.getRoleQualifersForPrincipalByNamespaceAndRolename(
                        bothManager.getPrincipalId(),
                        BCConstants.BUDGET_CONSTRUCTION_NAMESPACE,
                        BCConstants.KimApiConstants.BC_PROCESSOR_ROLE_NAME,
                        null);
        assertNotNull( "roleQualifiers should not have returned null", roleQualifiers );
        assertFalse( "roleQualifiers should not be empty", roleQualifiers.isEmpty() );
        System.out.println( roleQualifiers );
        assertTrue( "Org UA-UA was not in the list and should have been.", checkForChartOrg( roleQualifiers, "UA", "UA") );
        assertTrue( "Org BL-BL was not in the list and should have been.", checkForChartOrg( roleQualifiers, "BL", "BL") );
    }

    public void testNonProcessorQualifications() {
        List<Map<String,String>> roleQualifiers =
                roleService.getRoleQualifersForPrincipalByNamespaceAndRolename(
                        nonProcessor.getPrincipalId(),
                        BCConstants.BUDGET_CONSTRUCTION_NAMESPACE,
                        BCConstants.KimApiConstants.BC_PROCESSOR_ROLE_NAME,
                        null);
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
