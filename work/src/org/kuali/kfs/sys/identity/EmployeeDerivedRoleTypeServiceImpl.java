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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.identity.FinancialSystemUserRoleTypeServiceImpl;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.bo.entity.dto.KimEntityDefaultInfo;
import org.kuali.rice.kim.bo.role.KimRole;
import org.kuali.rice.kim.bo.role.dto.RoleMembershipInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.RoleManagementService;
import org.kuali.rice.kim.service.support.impl.KimDerivedRoleTypeServiceBase;
import org.kuali.rice.kim.util.KIMPropertyConstants;
import org.kuali.rice.kns.service.BusinessObjectService;

public class EmployeeDerivedRoleTypeServiceImpl extends KimDerivedRoleTypeServiceBase {

    private static IdentityManagementService identityManagementService;
    private static RoleManagementService roleManagementService;
    private static BusinessObjectService businessObjectService;

    protected static final String ACTIVE_EMPLOYEE_STATUS_CODE = "A";
    protected static final String ON_LEAVE_EMPLOYEE_STATUS_CODE = "L";
    protected static final String PENDING_EMPLOYEE_STATUS_CODE = "P";
    protected static final String STAFF_AFFILIATION_TYPE_CODE = "STAFF";
    protected static final String FCLTY_AFFILIATION_TYPE_CODE = "FCLTY";
    protected static final String P_EMPLOYEE_TYPE_CODE = "P";

    /**
     * Requirements: Derived Role: Employee - EmployeeDerivedRoleTypeService - KFS-SYS Active Faculty or Staff: principals where
     * EMP_STAT_CD = A in KRIM_ENTITY_EMP_INFO_T and AFLTN_TYP_CD = STAFF or AFLTN_TYP_CD = FCLTY in KRIM_ENTITY_AFLTN_T - KFS-SYS
     * Active Professional Employee: principals where EMP_STAT_CD = A & EMP_TYPE_CD = P in KRIM_ENTITY_EMP_INFO_T
     * 
     * @see org.kuali.rice.kim.service.support.impl.KimRoleTypeServiceBase#getPrincipalIdsFromApplicationRole(java.lang.String,
     *      java.lang.String, org.kuali.rice.kim.bo.types.dto.AttributeSet)
     */
    @Override
    public List<RoleMembershipInfo> getRoleMembersFromApplicationRole(String namespaceCode, String roleName, AttributeSet qualification) {
        List<RoleMembershipInfo> members = new ArrayList<RoleMembershipInfo>();

        String principalId = qualification.get(KIMPropertyConstants.Person.PRINCIPAL_ID);

        if (StringUtils.isNotBlank(principalId) && hasApplicationRole(principalId, null, namespaceCode, roleName, qualification)) {
            members.add(new RoleMembershipInfo(null, null, principalId, KimRole.PRINCIPAL_MEMBER_TYPE, null));
        }
        return members;
    }

    /***
     * @see org.kuali.rice.kim.service.support.impl.KimRoleTypeServiceBase#hasApplicationRole(java.lang.String, java.util.List,
     *      java.lang.String, java.lang.String, org.kuali.rice.kim.bo.types.dto.AttributeSet)
     */
    @Override
    public boolean hasApplicationRole(String principalId, List<String> groupIds, String namespaceCode, String roleName, AttributeSet qualification) {
        if (StringUtils.isBlank(principalId)) {
            return false;
        }

        if (getIdentityManagementService().getMatchingEntityCount(buildCriteria(roleName, principalId)) != 0) {
            if ((KFSConstants.SysKimConstants.ACTIVE_PROFESSIONAL_EMPLOYEE_AND_KFS_USER_KIM_ROLE_NAME.equals(roleName) || KFSConstants.SysKimConstants.ACTIVE_EMPLOYEE_AND_KFS_USER_KIM_ROLE_NAME.equals(roleName))) {
                List<String> roleIds = new ArrayList<String>(1);
                roleIds.add(getRoleManagementService().getRoleIdByName(KFSConstants.ParameterNamespaces.KFS, FinancialSystemUserRoleTypeServiceImpl.FINANCIAL_SYSTEM_USER_ROLE_NAME));
                if (!getRoleManagementService().principalHasRole(principalId, roleIds, null)) {
                    return false;
                }
            }
        }
        return true;
    }

    protected Map<String, String> buildCriteria(String roleName, String principalId) {
        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("active", KFSConstants.ACTIVE_INDICATOR);
        criteria.put("principals.active", KFSConstants.ACTIVE_INDICATOR);

        criteria.put("employmentInformation.active", KFSConstants.ACTIVE_INDICATOR);
        criteria.put("employmentInformation.employeeStatusCode", ACTIVE_EMPLOYEE_STATUS_CODE + "|" + ON_LEAVE_EMPLOYEE_STATUS_CODE + "|" + PENDING_EMPLOYEE_STATUS_CODE);

        if (KFSConstants.SysKimConstants.ACTIVE_PROFESSIONAL_EMPLOYEE_KIM_ROLE_NAME.equals(roleName)) {
            criteria.put("employmentInformation.employeeTypeCode", P_EMPLOYEE_TYPE_CODE);
        }
        else {
            criteria.put("employmentInformation.affiliation.active", KFSConstants.ACTIVE_INDICATOR);
            criteria.put("employmentInformation.affiliation.affiliationTypeCode", STAFF_AFFILIATION_TYPE_CODE + "|" + FCLTY_AFFILIATION_TYPE_CODE);

        }
        if (StringUtils.isNotBlank(principalId)) {
            criteria.put("principals.principalId", principalId);
        }
        return criteria;
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

    /**
     * Gets the businessObjectService attribute.
     * 
     * @return Returns the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        if (businessObjectService == null) {
            businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        }
        return businessObjectService;
    }
}