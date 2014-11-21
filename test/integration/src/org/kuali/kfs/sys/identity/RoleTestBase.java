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
