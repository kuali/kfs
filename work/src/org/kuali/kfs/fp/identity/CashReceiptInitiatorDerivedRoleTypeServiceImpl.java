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
package org.kuali.kfs.fp.identity;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kns.kim.role.DerivedRoleTypeServiceBase;

/**
 * A role which determines membership in role for people who can initiate Cash Receipts.  Members are those
 * users which belong to KFS-SYS User but NOT KFS-FP Cash Manager.
 */
public class CashReceiptInitiatorDerivedRoleTypeServiceImpl extends DerivedRoleTypeServiceBase {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CashReceiptInitiatorDerivedRoleTypeServiceImpl.class);

    private RoleService roleManagementService;

    protected static final String SYS_USER_ROLE_NAMESPACE = KFSConstants.CoreModuleNamespaces.KFS;
    protected static final String SYS_USER_ROLE_NAME = KFSConstants.SysKimApiConstants.KFS_USER_ROLE_NAME;
    protected static final String CASH_MANAGER_ROLE_NAMESPACE = KFSConstants.CoreModuleNamespaces.FINANCIAL;
    protected static final String CASH_MANAGER_ROLE_NAME = "Cash Manager";

    /**
     * Overridden to check principal id is in KFS-SYS User and not in KFS-FP Cash Manager
     * @see org.kuali.rice.kns.kim.role.RoleTypeServiceBase#hasDerivedRole(java.lang.String, java.util.List, java.lang.String, java.lang.String, org.kuali.rice.kim.bo.types.dto.AttributeSet)
     */
    @Override
    public boolean hasDerivedRole(String principalId, List<String> groupIds, String namespaceCode, String roleName, Map<String,String> qualification) {
        if (principalMemberOfSysUsers(principalId)) {
            if ( LOG.isDebugEnabled() ) {
                LOG.debug("User " + principalId + " IS member of KFS-SYS / Users, continuing check.");
            }
            if (!principalMemberOfCashManagers(principalId, qualification)) {
                if ( LOG.isDebugEnabled() ) {
                    LOG.debug("User IS NOT member of KFS-FP / Cash Manager - is member of this role.");
                }
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
    protected boolean principalMemberOfSysUsers(String principalId) {
        return hasRoleMembership(principalId, null, SYS_USER_ROLE_NAMESPACE, SYS_USER_ROLE_NAME);
    }

    /**
     * Determines if the given principal with the given qualification is a member of KFS-FP Cash Manager
     * @param principalId the principal idea of the user attempting to initiate a cash receipt
     * @param qualification the qualification of said principal
     * @return true if principal is a member of KFS-FP Cash Manager, false otherwise
     */
    protected boolean principalMemberOfCashManagers(String principalId, Map<String,String> qualification) {
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
    protected boolean hasRoleMembership(String principalId, Map<String,String> qualification, String namespaceCode, String roleName) {
        String roleId = getRoleService().getRoleIdByNamespaceCodeAndName(namespaceCode, roleName);
        return getRoleService().principalHasRole(principalId, Collections.singletonList(roleId), qualification);
    }

    /**
     * Gets the roleManagementService attribute.
     * @return Returns the roleManagementService.
     */
    protected RoleService getRoleService() {
        if ( roleManagementService == null) {
            roleManagementService = KimApiServiceLocator.getRoleService();
        }
        return roleManagementService;
    }
}
