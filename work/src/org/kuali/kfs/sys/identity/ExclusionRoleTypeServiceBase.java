/*
 * Copyright 2011 The Kuali Foundation.
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
import org.kuali.rice.kim.api.role.RoleMember;
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
    protected List<RoleMember> excludePrincipalAsNeeded(String excludedPrincipalId, Map<String, String> qualification, List<RoleMember> membershipInfos) {
        if (StringUtils.isBlank(excludedPrincipalId)) {
            return membershipInfos;
        }
        
        String topLevelRoleId = null;
        if (!membershipInfos.isEmpty()) {
            topLevelRoleId = membershipInfos.get(0).getRoleId();
        }
        
        Set<String> checkedMembers = new HashSet<String>();
        final String documentId = new String(qualification.get(KimConstants.AttributeConstants.DOCUMENT_NUMBER));

        List<RoleMember> qualifiedRoleMembers = new ArrayList<RoleMember>();
        for (RoleMember member : membershipInfos) {
            String topLevelRoleMemberId = member.getId();
            if(KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL.equals(member.getType().getCode())) {
                if(!excludedPrincipalId.equals(member.getMemberId())) {
                    qualifiedRoleMembers.add(member);
                }
            }
            else if (KimConstants.KimUIConstants.MEMBER_TYPE_ROLE.equals(member.getType().getCode())) {
                // get members of role
                RoleMembership.Builder membership = RoleMembership.Builder.create(member.getRoleId(), member.getId(), member.getMemberId(), member.getType(), qualification);
                checkRoleMemberShip(excludedPrincipalId,membership.build(), qualification, qualifiedRoleMembers, checkedMembers,  documentId, topLevelRoleId, topLevelRoleMemberId, member.getMemberNamespaceCode(), member.getMemberName() ); 
           
            }
            else if (KimConstants.KimUIConstants.MEMBER_TYPE_GROUP.equals(member.getType().getCode())) {
                RoleMembership.Builder membership = RoleMembership.Builder.create(member.getRoleId(), member.getId(), member.getMemberId(), member.getType(), qualification);
                checkGroupMemberShip(excludedPrincipalId, membership.build(), qualification, qualifiedRoleMembers, checkedMembers,  documentId, topLevelRoleId, topLevelRoleMemberId, member.getMemberNamespaceCode(), member.getMemberName() ); 
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
    protected void checkRoleMemberShip(String excludedPrincipalId ,RoleMembership member, Map<String, String> qualification,List<RoleMember> qualifiedRoleMembers, Set<String> checkedRoletypeMember, String documentId, String topLevelRoleId, String topLevelRoleMemberId, String membershipNamespace, String membershipName ) {
       String key = member.getType().getCode()+ "~" + member.getMemberId();
       if(!checkedRoletypeMember.contains(key)) {
           checkedRoletypeMember.add(key);
           List<RoleMembership> roleMembers = getRoleManagementService().getRoleMembers(Collections.singletonList(member.getMemberId()), qualification);
           for(RoleMembership membershipInfo : roleMembers) {
               if(KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL.equals(membershipInfo.getType().getCode())) {
                   if(!excludedPrincipalId.equals(membershipInfo.getMemberId())) {
                       RoleMember.Builder updatedMembershipInfo = RoleMember.Builder.create(topLevelRoleId, member.getId(), topLevelRoleMemberId, MemberType.PRINCIPAL, null, null, qualification, membershipName, membershipNamespace);
                       qualifiedRoleMembers.add(updatedMembershipInfo.build());
                   }
               }
               else if (KimConstants.KimUIConstants.MEMBER_TYPE_ROLE.equals(membershipInfo.getType().getCode())) {
                   checkRoleMemberShip(excludedPrincipalId,membershipInfo, qualification, qualifiedRoleMembers, checkedRoletypeMember,  documentId, topLevelRoleId, topLevelRoleMemberId, membershipNamespace, membershipName );
               }
               else if (KimConstants.KimUIConstants.MEMBER_TYPE_GROUP.equals(membershipInfo.getType().getCode())) {
                   checkGroupMemberShip(excludedPrincipalId ,membershipInfo, qualification, qualifiedRoleMembers, checkedRoletypeMember,  documentId, topLevelRoleId, topLevelRoleMemberId, membershipNamespace, membershipName ); 
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
    protected void checkGroupMemberShip(String excludedPrincipalId,RoleMembership member, Map<String, String> qualification,List<RoleMember> qualifiedRoleMembers, Set<String> checkedMembers, String documentId, String topLevelRoleId, String topLevelRoleMemberId, String membershipNamespace, String membershipName ) {
        String key = member.getType().getCode()+ "~" + member.getMemberId();
        if(!checkedMembers.contains(key)) {
            checkedMembers.add(key);
             List<GroupMember> GroupMembers = (List<GroupMember>)getGroupService().getMembersOfGroup(member.getMemberId());
             for(GroupMember membershipInfo : GroupMembers) {
                 if(KimConstants.KimGroupMemberTypes.PRINCIPAL_MEMBER_TYPE.equals(membershipInfo.getType().getCode())) {
                     if(!excludedPrincipalId.equals(membershipInfo.getMemberId())) {
                         RoleMember.Builder updatedMembershipInfo = RoleMember.Builder.create(topLevelRoleId, member.getId(), topLevelRoleMemberId, MemberType.PRINCIPAL, null, null, qualification, membershipName, membershipNamespace);
                         qualifiedRoleMembers.add(updatedMembershipInfo.build());
                     }
                 }
                 else if (KimConstants.KimGroupMemberTypes.GROUP_MEMBER_TYPE.equals(membershipInfo.getType().getCode())) {
                    checkGroupMemberShip(excludedPrincipalId, member, qualification, qualifiedRoleMembers, checkedMembers, documentId, topLevelRoleId, topLevelRoleMemberId, membershipNamespace, membershipName);
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
