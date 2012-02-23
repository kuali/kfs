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
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.bo.group.dto.GroupMembershipInfo;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.role.dto.RoleMembershipInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.GroupService;
import org.kuali.rice.kim.service.RoleManagementService;
import org.kuali.rice.kim.service.support.impl.KimRoleTypeServiceBase;
import org.kuali.rice.kim.util.KimConstants;

/**
 * Base role type for roles which will exclude their members based on certain criteria.  This base provides methods to
 * remove a single approver for any members
 */
public class ExclusionRoleTypeServiceBase extends KimRoleTypeServiceBase {
    protected RoleManagementService roleManagementService;
    protected GroupService groupService;
    
    /**
     * Remove the excluded principal from any matching memberships in the role
     * @param excludedPrincipalId the principal id to exclude
     * @param qualification the qualification for the role
     * @param membershipInfos the role members
     * @return the filtered role memberships
     */
    protected List<RoleMembershipInfo> excludePrincipalAsNeeded(String excludedPrincipalId, AttributeSet qualification, List<RoleMembershipInfo> membershipInfos) {
        if (StringUtils.isBlank(excludedPrincipalId)) {
            return membershipInfos;
        }
        
        String topLevelRoleId = null;
        if (!membershipInfos.isEmpty()) {
            topLevelRoleId = membershipInfos.get(0).getRoleId();
        }
        
        Set<String> checkedMembers = new HashSet<String>();
        final String documentId = new String(qualification.get(KimAttributes.DOCUMENT_NUMBER));

        List<RoleMembershipInfo> qualifiedRoleMembers = new ArrayList<RoleMembershipInfo>();
        for (RoleMembershipInfo member : membershipInfos) {
            String topLevelRoleMemberId = member.getRoleMemberId();
            if(KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL_CODE.equals(member.getMemberTypeCode())) {
                if(!excludedPrincipalId.equals(member.getMemberId())) {
                    qualifiedRoleMembers.add(member);
                }
            }
            else if (KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE.equals(member.getMemberTypeCode())) {
                // get members of role
                checkRoleMemberShip(excludedPrincipalId,member, qualification, qualifiedRoleMembers, checkedMembers,  documentId, topLevelRoleId, topLevelRoleMemberId ); 
           
            }
            else if (KimConstants.KimUIConstants.MEMBER_TYPE_GROUP_CODE.equals(member.getMemberTypeCode())) {
                checkGroupMemberShip(excludedPrincipalId, member, qualification, qualifiedRoleMembers, checkedMembers,  documentId, topLevelRoleId, topLevelRoleMemberId ); 
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
    protected void checkRoleMemberShip(String excludedPrincipalId ,RoleMembershipInfo member, AttributeSet qualification,List<RoleMembershipInfo> qualifiedRoleMembers, Set<String> checkedRoletypeMember, String documentId, String topLevelRoleId, String topLevelRoleMemberId ) {
       String key = member.getMemberTypeCode()+ "~" + member.getMemberId();
       if(!checkedRoletypeMember.contains(key)) {
           checkedRoletypeMember.add(key);
           List<RoleMembershipInfo> roleMembershipInfos = getRoleManagementService().getRoleMembers(Collections.singletonList(member.getMemberId()), qualification);
           for(RoleMembershipInfo membershipInfo : roleMembershipInfos) {
               if(KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL_CODE.equals(membershipInfo.getMemberTypeCode())) {
                   if(!excludedPrincipalId.equals(membershipInfo.getMemberId())) {
                       membershipInfo.setRoleId(topLevelRoleId);
                       membershipInfo.setRoleMemberId(topLevelRoleMemberId);
                       qualifiedRoleMembers.add(membershipInfo);
                   }
               }
               else if (KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE.equals(membershipInfo.getMemberTypeCode())) {
                   checkRoleMemberShip(excludedPrincipalId,membershipInfo, qualification, qualifiedRoleMembers, checkedRoletypeMember,  documentId, topLevelRoleId, topLevelRoleMemberId );
               }
               else if (KimConstants.KimUIConstants.MEMBER_TYPE_GROUP_CODE.equals(membershipInfo.getMemberTypeCode())) {
                   checkGroupMemberShip(excludedPrincipalId ,membershipInfo, qualification, qualifiedRoleMembers, checkedRoletypeMember,  documentId, topLevelRoleId, topLevelRoleMemberId ); 
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
    protected void checkGroupMemberShip(String excludedPrincipalId,RoleMembershipInfo member, AttributeSet qualification,List<RoleMembershipInfo> qualifiedRoleMembers, Set<String> checkedMembers, String documentId, String topLevelRoleId, String topLevelRoleMemberId ) {
        String key = member.getMemberTypeCode()+ "~" + member.getMemberId();
        if(!checkedMembers.contains(key)) {
            checkedMembers.add(key);
             List<GroupMembershipInfo> groupMembershipInfos = (List<GroupMembershipInfo>)getGroupService().getGroupMembers(Collections.singletonList(member.getMemberId()));
             for(GroupMembershipInfo membershipInfo : groupMembershipInfos) {
                 if(KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL_CODE.equals(membershipInfo.getMemberTypeCode())) {
                     if(!excludedPrincipalId.equals(membershipInfo.getMemberId())) {
                         qualifiedRoleMembers.add( new RoleMembershipInfo(topLevelRoleId, topLevelRoleMemberId, membershipInfo.getMemberId(), KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL_CODE, qualification));
                     }
                 }
                 else if (KimConstants.KimUIConstants.MEMBER_TYPE_GROUP_CODE.equals(membershipInfo.getMemberTypeCode())) {
                    checkGroupMemberShip(excludedPrincipalId, member, qualification, qualifiedRoleMembers, checkedMembers, documentId, topLevelRoleId, topLevelRoleMemberId);
                 }
             }   
        }
    }
    
    protected RoleManagementService getRoleManagementService() {
        if (roleManagementService == null) {
            roleManagementService = SpringContext.getBean(RoleManagementService.class);
        }
        return roleManagementService;
    }
    
    protected GroupService getGroupService(){
        if (groupService == null) {
            groupService = SpringContext.getBean(GroupService.class);
        }
        return groupService;
    }
}
