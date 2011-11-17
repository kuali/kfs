/*
 * Copyright 2008 The Kuali Foundation
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.identity.FinancialSystemUserRoleTypeServiceImpl;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.identity.entity.EntityDefault;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.role.RoleMembership;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.api.services.IdentityManagementService;
import org.kuali.rice.kim.impl.KIMPropertyConstants;
import org.kuali.rice.kns.kim.role.DerivedRoleTypeServiceBase;

public class EmployeeDerivedRoleTypeServiceImpl extends DerivedRoleTypeServiceBase {

    private IdentityManagementService identityManagementService;
    private RoleService roleManagementService;

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
    public List<RoleMembership> getRoleMembersFromApplicationRole(String namespaceCode, String roleName, Map<String,String> qualification) {
        List<RoleMembership> members = new ArrayList<RoleMembership>();
        if (qualification!=null && StringUtils.isNotBlank(qualification.get(KIMPropertyConstants.Person.PRINCIPAL_ID)) && hasApplicationRole(qualification.get(KIMPropertyConstants.Person.PRINCIPAL_ID), null, namespaceCode, roleName, qualification)) {
            members.add(new RoleMembership(null, null, qualification.get(KIMPropertyConstants.Person.PRINCIPAL_ID), Role.PRINCIPAL_MEMBER_TYPE, null));
        }
        return members;
    }

    @Override
    public boolean hasApplicationRole(String principalId, List<String> groupIds, String namespaceCode, String roleName, Map<String,String> qualification) {
        if (StringUtils.isBlank(principalId)) {
            return false;
        }
        
        EntityDefault entity = getIdentityManagementService().getEntityDefaultInfoByPrincipalId(principalId);
        if ((entity == null) || (entity.getEmployment() == null)) {
            return false;
        }
        if (!entity.isActive() || !entity.getEmployment().isActive() || !ACTIVE_EMPLOYEE_STATUSES.contains(entity.getEmployment().getEmployeeStatus().toString())) {
            return false;
        }
        if ((KFSConstants.SysKimApiConstants.ACTIVE_PROFESSIONAL_EMPLOYEE_KIM_ROLE_NAME.equals(roleName) || KFSConstants.SysKimApiConstants.ACTIVE_PROFESSIONAL_EMPLOYEE_AND_KFS_USER_KIM_ROLE_NAME.equals(roleName)) && !PROFESSIONAL_EMPLOYEE_TYPE_CODE.equals(entity.getEmployment().getEmployeeType().toString())) {
            return false;
        }
        if ((KFSConstants.SysKimApiConstants.ACTIVE_PROFESSIONAL_EMPLOYEE_AND_KFS_USER_KIM_ROLE_NAME.equals(roleName) || KFSConstants.SysKimApiConstants.ACTIVE_EMPLOYEE_AND_KFS_USER_KIM_ROLE_NAME.equals(roleName))) {
            List<String> roleIds = new ArrayList<String>(1);

            roleIds.add(getRoleService().getRoleIdByNameAndNamespaceCode(KFSConstants.ParameterNamespaces.KFS, FinancialSystemUserRoleTypeServiceImpl.FINANCIAL_SYSTEM_USER_ROLE_NAME));
             if (!getRoleService().principalHasRole(principalId, roleIds, null)) {
                return false;
            }
        }
        return true;
    }

    protected IdentityManagementService getIdentityManagementService() {
        if (identityManagementService == null) {
            identityManagementService = SpringContext.getBean(IdentityManagementService.class);
        }
        return identityManagementService;
    }

    protected RoleService getRoleService() {
        if (roleManagementService == null) {
            roleManagementService = SpringContext.getBean(RoleService.class);
        }
        return roleManagementService;
    }
}
