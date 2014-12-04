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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.api.membership.MemberType;
import org.kuali.rice.kim.api.identity.IdentityService;
import org.kuali.rice.kim.api.identity.entity.EntityDefault;
import org.kuali.rice.kim.api.role.RoleMembership;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.impl.KIMPropertyConstants;
import org.kuali.rice.kns.kim.role.DerivedRoleTypeServiceBase;

public class EmployeeDerivedRoleTypeServiceImpl extends DerivedRoleTypeServiceBase {

    protected IdentityService identityService;
    protected RoleService roleService;

    protected static final String ACTIVE_EMPLOYEE_STATUS_CODE = "A";
    protected static final String ON_LEAVE_EMPLOYEE_STATUS_CODE = "L";
    protected static final String PENDING_EMPLOYEE_STATUS_CODE = "P";
    protected static final Set<String> ACTIVE_EMPLOYEE_STATUSES = new HashSet<String>();
    static {
        ACTIVE_EMPLOYEE_STATUSES.add(ACTIVE_EMPLOYEE_STATUS_CODE);
        ACTIVE_EMPLOYEE_STATUSES.add(ON_LEAVE_EMPLOYEE_STATUS_CODE);
        ACTIVE_EMPLOYEE_STATUSES.add(PENDING_EMPLOYEE_STATUS_CODE);
    }
    protected static final String PROFESSIONAL_EMPLOYEE_TYPE_CODE = "P";

    @Override
    public List<RoleMembership> getRoleMembersFromDerivedRole(String namespaceCode, String roleName, Map<String,String> qualification) {
        List<RoleMembership> members = new ArrayList<RoleMembership>();
        if (qualification!=null && StringUtils.isNotBlank(qualification.get(KIMPropertyConstants.Person.PRINCIPAL_ID)) && hasDerivedRole(qualification.get(KIMPropertyConstants.Person.PRINCIPAL_ID), null, namespaceCode, roleName, qualification)) {
            members.add(RoleMembership.Builder.create(null, null, qualification.get(KIMPropertyConstants.Person.PRINCIPAL_ID), MemberType.PRINCIPAL, null).build());
        }
        return members;
    }

    @Override
    public boolean hasDerivedRole(String principalId, List<String> groupIds, String namespaceCode, String roleName, Map<String,String> qualification) {
        if (StringUtils.isBlank(principalId)) {
            return false;
        }

        EntityDefault entity = getIdentityService().getEntityDefaultByPrincipalId(principalId);
        if ((entity == null) || (entity.getEmployment() == null)) {
            return false;
        }
        if (!entity.isActive() || !entity.getEmployment().isActive() || !ACTIVE_EMPLOYEE_STATUSES.contains(entity.getEmployment().getEmployeeStatus().getCode())) {
            return false;
        }
        if ((KFSConstants.SysKimApiConstants.ACTIVE_PROFESSIONAL_EMPLOYEE_KIM_ROLE_NAME.equals(roleName) || KFSConstants.SysKimApiConstants.ACTIVE_PROFESSIONAL_EMPLOYEE_AND_KFS_USER_KIM_ROLE_NAME.equals(roleName)) && !PROFESSIONAL_EMPLOYEE_TYPE_CODE.equals(entity.getEmployment().getEmployeeType().getCode())) {
            return false;
        }
        if ((KFSConstants.SysKimApiConstants.ACTIVE_PROFESSIONAL_EMPLOYEE_AND_KFS_USER_KIM_ROLE_NAME.equals(roleName) || KFSConstants.SysKimApiConstants.ACTIVE_EMPLOYEE_AND_KFS_USER_KIM_ROLE_NAME.equals(roleName))) {
            List<String> roleIds = new ArrayList<String>(1);

            roleIds.add(getRoleService().getRoleIdByNamespaceCodeAndName(KFSConstants.CoreModuleNamespaces.KFS, KFSConstants.SysKimApiConstants.KFS_USER_ROLE_NAME));
             if (!getRoleService().principalHasRole(principalId, roleIds, null)) {
                return false;
            }
        }
        return true;
    }

    protected IdentityService getIdentityService() {
        if (identityService == null) {
            identityService = KimApiServiceLocator.getIdentityService();
        }
        return identityService;
    }

    protected RoleService getRoleService() {
        if (roleService == null) {
            roleService = KimApiServiceLocator.getRoleService();
        }
        return roleService;
    }
}
