/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.coa.identity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.service.ChartService;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.role.dto.RoleMembershipInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kim.service.RoleManagementService;

@ConfigureContext
public class ChartManagerRoleTest extends KualiTestBase {


    public void testGettingPersonForChartManagers() {
        IdentityManagementService idm = SpringContext.getBean(IdentityManagementService.class);
        RoleManagementService rms = SpringContext.getBean(RoleManagementService.class);
        List<String> roleIds = new ArrayList<String>();

        AttributeSet qualification = new AttributeSet();
        
        for ( String chart : SpringContext.getBean(ChartService.class).getAllChartCodes() ) {
        
            qualification.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, chart);
            
//            System.out.println( chart );
            roleIds.add(rms.getRoleIdByName(KFSConstants.ParameterNamespaces.KFS, "Chart Manager"));
            Collection<RoleMembershipInfo> chartManagers = rms.getRoleMembers(roleIds, qualification);
//            System.out.println( chartManagers );

            assertEquals( "There should be only one chart manager per chart: " + chart, 1, chartManagers.size() );
            
            for ( RoleMembershipInfo rmi : chartManagers ) {
                Person chartManager = SpringContext.getBean(PersonService.class).getPerson( rmi.getMemberId() );
                assertNotNull( "unable to retrieve person object for principalId: " + rmi.getMemberId(), chartManager );
            }
        }
    }
}
