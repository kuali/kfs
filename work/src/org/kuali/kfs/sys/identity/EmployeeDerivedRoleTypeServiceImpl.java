/*
 * Copyright 2008 The Kuali Foundation.
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.identity.FinancialSystemUserRoleTypeServiceImpl;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.bo.Role;
import org.kuali.rice.kim.bo.entity.dto.KimEntityDefaultInfo;
import org.kuali.rice.kim.bo.role.dto.RoleMembershipInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.RoleManagementService;
import org.kuali.rice.kim.service.support.impl.KimDerivedRoleTypeServiceBase;
import org.kuali.rice.kim.util.KIMPropertyConstants;

public class EmployeeDerivedRoleTypeServiceImpl extends KimDerivedRoleTypeServiceBase {

    private IdentityManagementService identityManagementService;
    private RoleManagementService roleManagementService;

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
    public List<RoleMembershipInfo> getRoleMembersFromApplicationRole(String namespaceCode, String roleName, AttributeSet qualification) {
        List<RoleMembershipInfo> members = new ArrayList<RoleMembershipInfo>();
        if (StringUtils.isNotBlank(qualification.get(KIMPropertyConstants.Person.PRINCIPAL_ID)) && hasApplicationRole(qualification.get(KIMPropertyConstants.Person.PRINCIPAL_ID), null, namespaceCode, roleName, qualification)) {
            members.add(new RoleMembershipInfo(null, null, qualification.get(KIMPropertyConstants.Person.PRINCIPAL_ID), Role.PRINCIPAL_MEMBER_TYPE, null));
        }
        return members;
    }

    @Override
    public boolean hasApplicationRole(String principalId, List<String> groupIds, String namespaceCode, String roleName, AttributeSet qualification) {
        if (StringUtils.isBlank(principalId)) {
            return false;
        }
        KimEntityDefaultInfo entity = getIdentityManagementService().getEntityDefaultInfoByPrincipalId(principalId);
        if ((entity == null) || (entity.getPrimaryEmployment() == null)) {
            return false;
        }
        if (!entity.isActive() || !entity.getPrimaryEmployment().isActive() || !ACTIVE_EMPLOYEE_STATUSES.contains(entity.getPrimaryEmployment().getEmployeeStatusCode())) {
            return false;
        }
        if ((KFSConstants.SysKimConstants.ACTIVE_PROFESSIONAL_EMPLOYEE_KIM_ROLE_NAME.equals(roleName) || KFSConstants.SysKimConstants.ACTIVE_PROFESSIONAL_EMPLOYEE_AND_KFS_USER_KIM_ROLE_NAME.equals(roleName)) && !PROFESSIONAL_EMPLOYEE_TYPE_CODE.equals(entity.getPrimaryEmployment().getEmployeeTypeCode())) {
            return false;
        }
        if ((KFSConstants.SysKimConstants.ACTIVE_PROFESSIONAL_EMPLOYEE_AND_KFS_USER_KIM_ROLE_NAME.equals(roleName) || KFSConstants.SysKimConstants.ACTIVE_EMPLOYEE_AND_KFS_USER_KIM_ROLE_NAME.equals(roleName))) {
            List<String> roleIds = new ArrayList<String>(1);
            roleIds.add(getRoleManagementService().getRoleIdByName(KFSConstants.ParameterNamespaces.KFS, FinancialSystemUserRoleTypeServiceImpl.FINANCIAL_SYSTEM_USER_ROLE_NAME));
            if (!getRoleManagementService().principalHasRole(principalId, roleIds, null)) {
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

    protected RoleManagementService getRoleManagementService() {
        if (roleManagementService == null) {
            roleManagementService = SpringContext.getBean(RoleManagementService.class);
        }
        return roleManagementService;
    }
}