/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.sys.identity;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.role.RoleMembership;

public abstract class RoleTestBase extends KualiTestBase {

    
    protected Collection<RoleMembership> getRoleMembers(String roleNamespace, String roleName, Map<String,String> roleQualifications) {
        final RoleService roleManagementService = SpringContext.getBean(RoleService.class);
        final Role roleInfo = roleManagementService.getRoleByName(roleNamespace, roleName);
        return roleManagementService.getRoleMembers(Arrays.asList(new String[] { roleInfo.getId() }), roleQualifications);
    }
    
    protected String getPrincipalIdByName(String principalName) {
        return SpringContext.getBean(PersonService.class).getPersonByPrincipalName(principalName).getPrincipalId();
    }
    
    
    protected void assertUserIsRoleMember(String principalId, String roleNamespace, String roleName, Map<String,String> roleQualifications) {
        final Collection<RoleMembership> roleMembers = getRoleMembers(roleNamespace, roleName, roleQualifications);
        
        int memberCount = 0;
        for (RoleMembership roleMember : roleMembers) {
            if (roleMember.getMemberTypeCode().equals("P") && roleMember.getMemberId().equals(principalId)) {
                memberCount += 1;
            }
        }
        assertTrue("Principal "+SpringContext.getBean(PersonService.class).getPerson(principalId).getName()+" not found in role: "+roleNamespace+" "+roleName, memberCount > 0);
    }
    
    protected void assertUserIsNotRoleMember(String principalId, String roleNamespace, String roleName, Map<String,String> roleQualifications) {
        final Collection<RoleMembership> roleMembers = getRoleMembers(roleNamespace, roleName, roleQualifications);
        
        int memberCount = 0;
        for (RoleMembership roleMember : roleMembers) {
            if (roleMember.getMemberTypeCode().equals("P") && roleMember.getMemberId().equals(principalId)) {
                memberCount += 1;
            }
        }
        assertTrue("Principal "+SpringContext.getBean(PersonService.class).getPerson(principalId).getName()+" found in role: "+roleNamespace+" "+roleName, memberCount == 0);
    }
    
    protected void assertUserIsSingleMemberInRole(String principalId, String roleNamespace, String roleName, Map<String,String> roleQualifications) {
        final Collection<RoleMembership> roleMembers = getRoleMembers(roleNamespace, roleName, roleQualifications);
        
        assertTrue("Only one role member returned", roleMembers.size() == 1);
        
        final RoleMembership roleMember = roleMembers.iterator().next();
        roleMembers.iterator().hasNext(); // wind the iterator out, just in case
        assertTrue("Role member "+roleMember.getMemberId()+" does not match expected principal id: "+principalId, (roleMember.getMemberTypeCode().equals("P") && roleMember.getMemberId().equals(principalId)));
    }
    
}
