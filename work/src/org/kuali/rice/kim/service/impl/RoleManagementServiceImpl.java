/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.kim.service.impl;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.kuali.rice.kim.api.common.delegate.DelegateMember;
import org.kuali.rice.kim.api.common.delegate.DelegateType;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.role.RoleManagementService;
import org.kuali.rice.kim.api.role.RoleMember;
import org.kuali.rice.kim.api.role.RoleMembership;
import org.kuali.rice.kim.api.role.RoleResponsibility;
import org.kuali.rice.kim.api.role.RoleResponsibilityAction;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.api.role.RoleUpdateService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;

import java.sql.Date;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class RoleManagementServiceImpl implements RoleManagementService {
    private static final Logger LOG = Logger.getLogger(RoleManagementServiceImpl.class);

    private RoleService roleService;
    private RoleUpdateService roleUpdateService;

    @Override
    public void flushRoleCaches() {
        flushInternalRoleCache();
        flushInternalRoleMemberCache();
        flushInternalDelegationCache();
        flushInternalDelegationMemberCache();
    }

    @Override
    public void flushRoleMemberCaches() {
        flushInternalRoleMemberCache();
    }

    @Override
    public void flushDelegationCaches() {
        flushInternalDelegationCache();
        flushInternalDelegationMemberCache();
    }

    @Override
    public void flushDelegationMemberCaches() {
        flushInternalDelegationMemberCache();
    }

    @Override
    public void removeCacheEntries(String roleId, String principalId) {

    }

    @Override
    public Collection<String> getRoleMemberPrincipalIds(String namespaceCode, String roleName, Map<String, String> qualification) {
        return getRoleService().getRoleMemberPrincipalIds(namespaceCode, roleName, qualification);
    }

    @Override
    public Role getRole(String roleId) {
        return getRoleService().getRole(roleId);
    }

    @Override
    public Role getRoleByName(String namespaceCode, String roleName) {
        return getRoleService().getRoleByName(namespaceCode, roleName);
    }

    @Override
    public String getRoleIdByName(String namespaceCode, String roleName) {
        Role role = getRoleByName(namespaceCode, roleName);
        if (role == null) {
            return null;
        }
        return role.getId();
    }

    @Override
    public List<Role> getRoles(List<String> roleIds) {
        return getRoleService().getRoles(roleIds);
    }

    @Override
    public List<RoleMembership> getRoleMembers(List<String> roleIds, Map<String, String> qualification) {
        return getRoleService().getRoleMembers(roleIds, qualification);
    }

    @Override
    public List<Map<String, String>> getRoleQualifiersForPrincipal(String principalId, List<String> roleIds, Map<String, String> qualification) {
        return getRoleService().getRoleQualifiersForPrincipal(principalId, roleIds, qualification);
    }

    @Override
    public List<Map<String, String>> getRoleQualifiersForPrincipal(String principalId, String namespaceCode, String roleName, Map<String, String> qualification) {
        return getRoleService().getRoleQualifiersForPrincipal(principalId, namespaceCode, roleName, qualification);
    }

    @Override
    public boolean isRoleActive(String roleId) {
        Role role = getRole(roleId);
        return role != null && role.isActive();
    }

    @Override
    public boolean principalHasRole(String principalId, List<String> roleIds, Map<String, String> qualification) {
        if (LOG.isDebugEnabled()) {
            logPrincipalHasRoleCheck(principalId, roleIds, qualification);
        }
        boolean hasRole =  getRoleService().principalHasRole(principalId, roleIds, qualification);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Result: " + hasRole);
            }
        return hasRole;
    }

    @Override
    public List<String> getPrincipalIdSubListWithRole(
            List<String> principalIds, String roleNamespaceCode,
            String roleName, Map<String, String> qualification) {
        return getRoleService().getPrincipalIdSubListWithRole(principalIds,
                roleNamespaceCode, roleName, qualification);
    }

    @Override
    public List<Map<String, String>> getNestedRoleQualifiersForPrincipal(String principalId, List<String> roleIds, Map<String, String> qualification) {
        return getRoleService().getNestedRoleQualifiersForPrincipal(principalId, roleIds, qualification);
    }

    @Override
    public List<Map<String, String>> getNestedRoleQualifiersForPrincipal(String principalId, String namespaceCode, String roleName, Map<String, String> qualification) {
        return getRoleService().getNestedRoleQualifiersForPrincipal(principalId, namespaceCode, roleName, qualification);
    }

    @Override
    public void assignGroupToRole(String groupId, String namespaceCode, String roleName,
                                  Map<String, String> qualifications) {
        getRoleUpdateService().assignGroupToRole(groupId, namespaceCode, roleName, qualifications);
        Role role = getRoleByName(namespaceCode, roleName);
        removeCacheEntries(role.getId(), null);
    }

    @Override
    public void assignPrincipalToRole(String principalId, String namespaceCode, String roleName,
                                      Map<String, String> qualifications) {
        Role role = getRoleByName(namespaceCode, roleName);
        getRoleUpdateService().assignPrincipalToRole(principalId, namespaceCode, roleName, qualifications);
        removeCacheEntries(role.getId(), principalId);
    }

    @Override
    public void removeGroupFromRole(String groupId, String namespaceCode, String roleName,
                                    Map<String, String> qualifications) {
        getRoleUpdateService().removeGroupFromRole(groupId, namespaceCode, roleName, qualifications);
        Role role = getRoleByName(namespaceCode, roleName);
        removeCacheEntries(role.getId(), null);
    }

    @Override
    public void removePrincipalFromRole(String principalId, String namespaceCode, String roleName,
                                        Map<String, String> qualifications) {
        Role role = getRoleByName(namespaceCode, roleName);
        getRoleUpdateService().removePrincipalFromRole(principalId, namespaceCode, roleName, qualifications);
        removeCacheEntries(role.getId(), principalId);
    }

    @Override
    public List<Role> getRolesSearchResults(
            Map<String, String> fieldValues) {
        return getRoleService().getRolesSearchResults(fieldValues);
    }

    protected void logPrincipalHasRoleCheck(String principalId, List<String> roleIds, Map<String, String> roleQualifiers) {
        StringBuilder sb = new StringBuilder();
        sb.append('\n');
        sb.append("Has Role     : ").append(roleIds).append('\n');
        if (roleIds != null) {
            for (String roleId : roleIds) {
                Role role = getRole(roleId);
                if (role != null) {
                    sb.append("        Name : ").append(role.getNamespaceCode()).append('/').append(role.getName());
                    sb.append(" (").append(roleId).append(')');
                    sb.append('\n');
                }
            }
        }
        sb.append("   Principal : ").append(principalId);
        sb.append('\n');
        sb.append("     Details :\n");
        if (roleQualifiers != null) {
            sb.append(new HashMap<String, String>(roleQualifiers));
        } else {
            sb.append("               [null]\n");
        }
        if (LOG.isTraceEnabled()) {
            LOG.trace(sb.append(ExceptionUtils.getStackTrace(new Throwable())));
        } else {
            LOG.debug(sb.toString());
        }
    }

    @Override
    public void principalInactivated(String principalId) {
        getRoleService().principalInactivated(principalId);
        removeCacheEntries(null, principalId);
    }

    @Override
    public void roleInactivated(String roleId) {
        getRoleService().roleInactivated(roleId);
        removeCacheEntries(roleId, null);
    }

    @Override
    public void groupInactivated(String groupId) {
        getRoleService().groupInactivated(groupId);
    }

    @Override
    public List<RoleMembership> getFirstLevelRoleMembers(List<String> roleIds) {
        return getRoleService().getFirstLevelRoleMembers(roleIds);
    }

    @Override
    public List<RoleMember> findRoleMembers(Map<String, String> fieldValues) {
        return getRoleService().findRoleMembers(fieldValues);
    }

    @Override
    public List<RoleMembership> findRoleMemberships(Map<String, String> fieldValues) {
        return getRoleService().findRoleMemberships(fieldValues);
    }

    @Override
    public void assignRoleToRole(String roleId, String namespaceCode, String roleName,
                                 Map<String, String> qualifications) {
        getRoleUpdateService().assignRoleToRole(
                roleId, namespaceCode, roleName, qualifications);
        Role role = getRoleByName(namespaceCode, roleName);
        removeCacheEntries(role.getId(), null);
    }

    @Override
    public void saveDelegationMemberForRole(String delegationMemberId, String roleMemberId, String memberId, String memberTypeCode,
                                            String delegationTypeCode, String roleId, Map<String, String> qualifications,
                                            Date activeFromDate, Date activeToDate) throws UnsupportedOperationException {
        getRoleUpdateService().saveDelegationMemberForRole(delegationMemberId, roleMemberId, memberId, memberTypeCode, delegationTypeCode, roleId, qualifications, activeFromDate, activeToDate);
        Role role = getRole(roleId);
        removeCacheEntries(role.getId(), null);
    }

    @Override
    public RoleMember saveRoleMemberForRole(String roleMemberId, String memberId, String memberTypeCode,
                                            String roleId, Map<String, String> qualifications, Date activeFromDate, Date activeToDate) throws UnsupportedOperationException {
        Role role = getRole(roleId);
        RoleMember roleMember = getRoleUpdateService().saveRoleMemberForRole(roleMemberId, memberId, memberTypeCode, roleId, qualifications, activeFromDate, activeToDate);
        removeCacheEntries(role.getId(), memberId);
        return roleMember;
    }

    @Override
    public void removeRoleFromRole(String roleId, String namespaceCode, String roleName,
                                   Map<String, String> qualifications) {
        getRoleUpdateService().removeRoleFromRole(roleId, namespaceCode, roleName, qualifications);
        Role role = getRoleByName(namespaceCode, roleName);
        removeCacheEntries(role.getId(), null);
    }

    @Override
    public List<DelegateMember> findDelegateMembers(Map<String, String> fieldValues) {
        return getRoleService().findDelegateMembers(fieldValues);
    }

    @Override
    public List<DelegateMember> getDelegationMembersByDelegationId(String delegationId) {
        return getRoleService().getDelegationMembersByDelegationId(delegationId);
    }

    @Override
    public DelegateMember getDelegationMemberByDelegationAndMemberId(String delegationId, String memberId) {
        return getRoleService().getDelegationMemberByDelegationAndMemberId(delegationId, memberId);
    }

    @Override
    public DelegateMember getDelegationMemberById(String delegationMemberId) {
        return getRoleService().getDelegationMemberById(delegationMemberId);
    }

    @Override
    public List<RoleResponsibilityAction> getRoleMemberResponsibilityActions(String roleMemberId) {
        return getRoleService().getRoleMemberResponsibilityActions(roleMemberId);
    }

    @Override
    public DelegateType getDelegateTypeInfo(String roleId, String delegationTypeCode) {
        return getRoleService().getDelegateTypeInfo(roleId, delegationTypeCode);
    }

    @Override
    public DelegateType getDelegateTypeInfoById(String delegationId) {
        return getRoleService().getDelegateTypeInfoById(delegationId);
    }

    @Override
    public void saveRoleRspActions(String roleResponsibilityActionId, String roleId, String roleResponsibilityId, String roleMemberId,
                                   String actionTypeCode, String actionPolicyCode, Integer priorityNumber, Boolean forceAction) {
        getRoleUpdateService().saveRoleRspActions(roleResponsibilityActionId, roleId, roleResponsibilityId, roleMemberId, actionTypeCode, actionPolicyCode, priorityNumber, forceAction);
        removeCacheEntries(roleId, null);
    }

    @Override
    public List<RoleResponsibility> getRoleResponsibilities(String roleId) {
        return getRoleService().getRoleResponsibilities(roleId);
    }

    @Override
    public void applicationRoleMembershipChanged(String roleId) {
        removeCacheEntries(roleId, null);
        getRoleService().applicationRoleMembershipChanged(roleId);
    }

    // Spring and injection methods

    public RoleService getRoleService() {
        if (roleService == null) {
            roleService = KimApiServiceLocator.getRoleService();
        }
        return roleService;
    }

    public RoleUpdateService getRoleUpdateService() {
        try {
            if (roleUpdateService == null) {
                roleUpdateService = KimApiServiceLocator.getRoleUpdateService();
                if (roleUpdateService == null) {
                    throw new UnsupportedOperationException("null returned for RoleUpdateService, unable to update role data");
                }
            }
        } catch (Exception ex) {
            throw new UnsupportedOperationException("unable to obtain a RoleUpdateService, unable to update role data", ex);
        }
        return roleUpdateService;
    }

    /**
     * This overridden method looks up roles based on criteria.  If you want
     * to return all roles pass in an empty map.
     */
    @Override
    public List<Role> lookupRoles(Map<String, String> searchCriteria) {
        return getRoleService().lookupRoles(searchCriteria);
    }

    @Override
    public void flushInternalRoleCache() {
        getRoleService().flushInternalRoleCache();
    }

    @Override
    public void flushInternalRoleMemberCache() {
        getRoleService().flushInternalRoleMemberCache();
    }

    @Override
    public void flushInternalDelegationCache() {
        getRoleService().flushInternalDelegationCache();
    }

    @Override
    public void flushInternalDelegationMemberCache() {
        getRoleService().flushInternalDelegationMemberCache();
    }

    @Override
    public void assignPermissionToRole(String permissionId, String roleId) throws UnsupportedOperationException {
        getRoleUpdateService().assignPermissionToRole(permissionId, roleId);
    }

    @Override
    public String getNextAvailableRoleId() throws UnsupportedOperationException {
        return getRoleUpdateService().getNextAvailableRoleId();
    }

    @Override
    public void saveRole(String roleId, String roleName, String roleDescription, boolean active, String kimTypeId, String namespaceCode) throws UnsupportedOperationException {
        getRoleUpdateService().saveRole(roleId, roleName, roleDescription, active, kimTypeId, namespaceCode);
    }

    @Override
    public List<String> getMemberParentRoleIds(String memberType,
                                               String memberId) {
        return getRoleService().getMemberParentRoleIds(memberType, memberId);
    }
}
