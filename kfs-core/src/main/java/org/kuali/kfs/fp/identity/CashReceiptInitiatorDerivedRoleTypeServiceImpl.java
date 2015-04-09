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
package org.kuali.kfs.fp.identity;

import java.util.Collections;
import java.util.HashMap;
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
        return hasRoleMembership(principalId, new HashMap<String,String>(), SYS_USER_ROLE_NAMESPACE, SYS_USER_ROLE_NAME);
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
