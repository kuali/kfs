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
package org.kuali.kfs.fp.identity;

import java.util.ArrayList;
import java.util.List;

import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.RoleManagementService;
import org.kuali.rice.kim.service.support.impl.KimDerivedRoleTypeServiceBase;

/**
 * A role which determines membership in role for people who can initiate Cash Receipts.  Members are those 
 * users which belong to KFS-SYS User but NOT KFS-FP Cash Manager.
 */
public class CashReceiptInitiatorDerivedRoleTypeServiceImpl extends KimDerivedRoleTypeServiceBase {
    private RoleManagementService roleManagementService;
    
    private static final String SYS_USER_ROLE_NAMESPACE = "KFS-SYS";
    private static final String SYS_USER_ROLE_NAME = "User";
    private static final String CASH_MANAGER_ROLE_NAMESPACE = "KFS-FP";
    private static final String CASH_MANAGER_ROLE_NAME = "Cash Manager";
    
    /**
     * Overridden to check principal id is in KFS-SYS User and not in KFS-FP Cash Manager
     * @see org.kuali.rice.kim.service.support.impl.KimRoleTypeServiceBase#hasApplicationRole(java.lang.String, java.util.List, java.lang.String, java.lang.String, org.kuali.rice.kim.bo.types.dto.AttributeSet)
     */
    @Override
    public boolean hasApplicationRole(String principalId, List<String> groupIds, String namespaceCode, String roleName, AttributeSet qualification) {
        if (principalMemberOfSysUsers(principalId, qualification)) {
            if (!principalMemberOfCashManagers(principalId, qualification)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines if the given principal with the given qualification is a member of KFS-SYS User
     * @param principalId the principal id of the shmoe trying to initiate a cash receipt
     * @param qualification the qualification of said shmoe
     * @return true if the said shmoe is indeed a member of KFS-SYS User; false otherwise
     */
    protected boolean principalMemberOfSysUsers(String principalId, AttributeSet qualification) {
        return hasRoleMembership(principalId, qualification, SYS_USER_ROLE_NAMESPACE, SYS_USER_ROLE_NAME);
    }
    
    /**
     * Determines if the given principal with the given qualification is a member of KFS-FP Cash Manager
     * @param principalId the principal idea of the user attempting to initiate a cash receipt
     * @param qualification the qualification of said principal
     * @return true if principal is a member of KFS-FP Cash Manager, false otherwise
     */
    protected boolean principalMemberOfCashManagers(String principalId, AttributeSet qualification) {
        return hasRoleMembership(principalId, qualification, CASH_MANAGER_ROLE_NAMESPACE, CASH_MANAGER_ROLE_NAME);
    }
    
    /**
     * Determines if a principal has a given role
     * @param principalId the id of the principal
     * @param qualification their qualifications from the permission/responsibility call
     * @param namespaceCode the namespace code of the role
     * @param roleName the name of the role
     * @return true if the principal is a member of the role, false otherwise
     */
    final protected boolean hasRoleMembership(String principalId, AttributeSet qualification, String namespaceCode, String roleName) {
        final String roleId = getRoleManagementService().getRoleIdByName(namespaceCode, roleName);
        List<String> roleIds = new ArrayList<String>();
        roleIds.add(roleId);
        
        return getRoleManagementService().principalHasRole(principalId, roleIds, qualification);
    }

    /**
     * Sets the roleManagementService attribute value.
     * @param roleManagementService The roleManagementService to set.
     */
    public void setRoleManagementService(RoleManagementService roleManagementService) {
        this.roleManagementService = roleManagementService;
    }

    /**
     * Gets the roleManagementService attribute. 
     * @return Returns the roleManagementService.
     */
    public RoleManagementService getRoleManagementService() {
        return roleManagementService;
    }
}
