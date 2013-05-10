/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.sys.identity;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.rice.core.api.membership.MemberType;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.role.RoleMembership;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;

public abstract class RoleTestBase extends KualiTestBase {


    protected Collection<RoleMembership> getRoleMembers(String roleNamespace, String roleName, Map<String,String> roleQualifications) {
        Role roleInfo = KimApiServiceLocator.getRoleService().getRoleByNamespaceCodeAndName(roleNamespace, roleName);
        return KimApiServiceLocator.getRoleService().getRoleMembers(Arrays.asList(new String[] { roleInfo.getId() }), roleQualifications);
    }

    protected String getPrincipalIdByName(String principalName) {
        return KimApiServiceLocator.getPersonService().getPersonByPrincipalName(principalName).getPrincipalId();
    }


    protected void assertUserIsRoleMember(String principalId, String roleNamespace, String roleName, Map<String,String> roleQualifications) {
        final Collection<RoleMembership> roleMembers = getRoleMembers(roleNamespace, roleName, roleQualifications);

        for (RoleMembership roleMember : roleMembers) {
            if (roleMember.getType().equals(MemberType.PRINCIPAL) && roleMember.getMemberId().equals(principalId)) {
                return;
            }
        }
        fail("Principal "+KimApiServiceLocator.getPersonService().getPerson(principalId).getPrincipalName()+" not found in role: "+roleNamespace+" / "+roleName);
    }

    protected void assertUserIsNotRoleMember(String principalId, String roleNamespace, String roleName, Map<String,String> roleQualifications) {
        final Collection<RoleMembership> roleMembers = getRoleMembers(roleNamespace, roleName, roleQualifications);

        for (RoleMembership roleMember : roleMembers) {
            if (roleMember.getType().equals(MemberType.PRINCIPAL) && roleMember.getMemberId().equals(principalId)) {
                fail("Principal "+KimApiServiceLocator.getPersonService().getPerson(principalId).getPrincipalName()+" found in role: "+roleNamespace+" / "+roleName + "\n" + roleMember);
            }
        }
    }

    protected void assertUserIsSingleMemberInRole(String principalId, String roleNamespace, String roleName, Map<String,String> roleQualifications) {
        Collection<RoleMembership> roleMembers = getRoleMembers(roleNamespace, roleName, roleQualifications);

        assertTrue("Only one role member returned", roleMembers.size() == 1);

        RoleMembership roleMember = roleMembers.iterator().next();
        assertTrue("Role member "+roleMember+" does not match expected principal id: "+principalId, (roleMember.getType().equals(MemberType.PRINCIPAL) && roleMember.getMemberId().equals(principalId)));
    }

}
