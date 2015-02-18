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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.membership.MemberType;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.group.GroupMember;
import org.kuali.rice.kim.api.group.GroupService;
import org.kuali.rice.kim.api.role.RoleMembership;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kns.kim.role.RoleTypeServiceBase;

/**
 * Base role type for roles which will exclude their members based on certain criteria.  This base provides methods to
 * remove a single approver for any members
 */
public class ExclusionRoleTypeServiceBase extends RoleTypeServiceBase {
    protected RoleService roleManagementService;
    protected GroupService groupService;

    /**
     * Remove the excluded principal from any matching memberships in the role
     * @param excludedPrincipalId the principal id to exclude
     * @param qualification the qualification for the role
     * @param membershipInfos the role members
     * @return the filtered role memberships
     */
    protected List<RoleMembership> excludePrincipalAsNeeded(String excludedPrincipalId, Map<String, String> qualification, List<RoleMembership> membershipInfos) {
        if (StringUtils.isBlank(excludedPrincipalId)) {
            return membershipInfos;
        }

        String topLevelRoleId = null;
        if (!membershipInfos.isEmpty()) {
            topLevelRoleId = membershipInfos.get(0).getRoleId();
        }

        Set<String> checkedMembers = new HashSet<String>();
        final String documentId = new String(qualification.get(KimConstants.AttributeConstants.DOCUMENT_NUMBER));

        List<RoleMembership> qualifiedRoleMembers = new ArrayList<RoleMembership>();
        for (RoleMembership member : membershipInfos) {
            String topLevelRoleMemberId = member.getId();
            if(MemberType.PRINCIPAL.equals(member.getType())) {
                if(!excludedPrincipalId.equals(member.getMemberId())) {
                    qualifiedRoleMembers.add(member);
                }
            }
            else if (MemberType.ROLE.equals(member.getType()) ) {
                // get members of role
                RoleMembership.Builder membership = RoleMembership.Builder.create(member.getRoleId(), member.getId(), member.getMemberId(), member.getType(), qualification);
                checkRoleMemberShip(excludedPrincipalId,membership.build(), qualification, qualifiedRoleMembers, checkedMembers,  documentId, topLevelRoleId, topLevelRoleMemberId);

            }
            else if (MemberType.GROUP.equals(member.getType())) {
                RoleMembership.Builder membership = RoleMembership.Builder.create(member.getRoleId(), member.getId(), member.getMemberId(), member.getType(), qualification);
                checkGroupMemberShip(excludedPrincipalId, membership.build(), qualification, qualifiedRoleMembers, checkedMembers,  documentId, topLevelRoleId, topLevelRoleMemberId);
            }
        }
        return qualifiedRoleMembers;
    }

    /**
     *
     * This method is to check role members of given role and skip the single approver if exists from the role.
     * @param approverOrInitiator
     * @param member
     * @param qualification
     * @param qualifiedRoleMembers
     * @param checkedRoletypeMember
     * @param documentId
     * @param topLevelRoleId
     * @param topLevelRoleMemberId
     */
    protected void checkRoleMemberShip(String excludedPrincipalId ,RoleMembership member, Map<String, String> qualification,List<RoleMembership> qualifiedRoleMembers, Set<String> checkedRoletypeMember, String documentId, String topLevelRoleId, String topLevelRoleMemberId) {
       String key = member.getType().getCode()+ "~" + member.getMemberId();
       if(!checkedRoletypeMember.contains(key)) {
           checkedRoletypeMember.add(key);
           List<RoleMembership> roleMembers = getRoleManagementService().getRoleMembers(Collections.singletonList(member.getMemberId()), qualification);
           for(RoleMembership membershipInfo : roleMembers) {
               if(MemberType.PRINCIPAL.getCode().equals(membershipInfo.getType().getCode())) {
                   if(!excludedPrincipalId.equals(membershipInfo.getMemberId())) {
                       RoleMembership.Builder updatedMembershipInfo = RoleMembership.Builder.create(topLevelRoleId, membershipInfo.getId(), membershipInfo.getMemberId(), MemberType.PRINCIPAL, qualification);
                       qualifiedRoleMembers.add(updatedMembershipInfo.build());
                   }
               }
               else if (MemberType.ROLE.getCode().equals(membershipInfo.getType().getCode())) {
                   checkRoleMemberShip(excludedPrincipalId,membershipInfo, qualification, qualifiedRoleMembers, checkedRoletypeMember,  documentId, topLevelRoleId, topLevelRoleMemberId);
               }
               else if (MemberType.GROUP.getCode().equals(membershipInfo.getType().getCode())) {
                   checkGroupMemberShip(excludedPrincipalId ,membershipInfo, qualification, qualifiedRoleMembers, checkedRoletypeMember,  documentId, topLevelRoleId, topLevelRoleMemberId);
               }
           }
       }
    }

    /**
     *
     * This method is to check group members of given group and skip the single approver
     * member from the group.
     *
     * @param approverOrInitiator
     * @param member
     * @param qualification
     * @param qualifiedRoleMembers
     * @param checkedMembers
     * @param documentId
     * @param topLevelRoleId
     * @param topLevelRoleMemberId
     */
    protected void checkGroupMemberShip(String excludedPrincipalId,RoleMembership member, Map<String, String> qualification,List<RoleMembership> qualifiedRoleMembers, Set<String> checkedMembers, String documentId, String topLevelRoleId, String topLevelRoleMemberId) {
        String key = member.getType().getCode()+ "~" + member.getMemberId();
        if(!checkedMembers.contains(key)) {
            checkedMembers.add(key);
             List<GroupMember> GroupMembers = (List<GroupMember>)getGroupService().getMembersOfGroup(member.getMemberId());
             for(GroupMember membershipInfo : GroupMembers) {
                 if(MemberType.PRINCIPAL.getCode().equals(membershipInfo.getType().getCode())) {
                     if(!excludedPrincipalId.equals(membershipInfo.getMemberId())) {
                         RoleMembership.Builder updatedMembershipInfo = RoleMembership.Builder.create(topLevelRoleId, membershipInfo.getId(), membershipInfo.getMemberId(), MemberType.PRINCIPAL, qualification);
                         qualifiedRoleMembers.add(updatedMembershipInfo.build());
                     }
                 }
                 else if (MemberType.GROUP.getCode().equals(membershipInfo.getType().getCode())) {
                    checkGroupMemberShip(excludedPrincipalId, member, qualification, qualifiedRoleMembers, checkedMembers, documentId, topLevelRoleId, topLevelRoleMemberId);
                 }
             }
        }
    }

    protected RoleService getRoleManagementService() {
        if (roleManagementService == null) {
            roleManagementService = KimApiServiceLocator.getRoleService();
        }
        return roleManagementService;
    }

    protected GroupService getGroupService(){
        if (groupService == null) {
            groupService = KimApiServiceLocator.getGroupService();
        }
        return groupService;
    }
}
