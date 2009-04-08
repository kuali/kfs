/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.coa.document.validation.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kuali.kfs.coa.identity.OrgReviewRole;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.rice.kim.bo.role.dto.RoleMembershipInfo;
import org.kuali.rice.kim.bo.role.impl.KimDelegationImpl;
import org.kuali.rice.kim.bo.role.impl.KimDelegationMemberImpl;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleMember;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.document.Document;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;

/**
 * This class represents the business rules for the maintenance of {@link AccountGlobal} business objects
 */
public class OrgReviewRoleRule extends MaintenanceDocumentRuleBase {

    private static Logger LOG = Logger.getLogger(OrgReviewRoleRule.class);

    @Override
    public boolean processRouteDocument(Document document) {
        boolean valid = super.processRouteDocument(document);
        OrgReviewRole orr = (OrgReviewRole)((MaintenanceDocument)document).getNewMaintainableObject().getBusinessObject();
        if(!orr.hasAnyMember()){
            valid = false;
            putFieldError("principal.principalName", KFSKeyConstants.NO_MEMBER_SELECTED);
        } else if(orr.isDelegate()){
            // Save delegation(s)
            validateDelegation(document);
        } else{
            // Save role member(s)
            validateRoleMember(document);
        }

        return valid;
    }
    
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        boolean valid = super.processCustomSaveDocumentBusinessRules(document);
        OrgReviewRole orr = (OrgReviewRole)document.getNewMaintainableObject().getBusinessObject();
        if(!orr.hasAnyMember()){
            valid = false;
            putFieldError("principal.principalName", KFSKeyConstants.NO_MEMBER_SELECTED);
        } else if(orr.isDelegate()){
            // Save delegation(s)
            validateDelegation(document);
        } else{
            // Save role member(s)
            validateRoleMember(document);
        }

        return valid;
    }

    protected boolean validateDelegation(Document document){
        boolean valid = true;
        String roleId;
        OrgReviewRole orr = (OrgReviewRole)((MaintenanceDocument)document).getNewMaintainableObject().getBusinessObject();
        if(!((MaintenanceDocument)document).isEdit()){
            for(String roleName: orr.getRoleNamesToConsider()){
                roleId = KIMServiceLocator.getRoleService().getRoleIdByName(
                        KFSConstants.SysKimConstants.ORGANIZATION_REVIEWER_ROLE_NAMESPACECODE, roleName);
                List<KimDelegationImpl> roleDelegations = KIMServiceLocator.getUiDocumentService().getRoleDelegations(roleId);
                
                KimDocumentRoleMember member;
                //validate if the newly entered delegation members are already assigned to the role
                for(KimDelegationImpl delegation: roleDelegations){
                    for(KimDelegationMemberImpl delegationMember: delegation.getMembers()){
                        member = orr.getMemberOfType(delegationMember.getMemberTypeCode());
                        if(member!=null && member.getMemberId().equals(delegationMember.getMemberId())){
                           putFieldError(orr.getMemberFieldName(member), KFSKeyConstants.ALREADY_ASSIGNED_MEMBER);
                           valid = false;
                        }
                    }
                }
            }
        }        
        //putFieldError("bankOfficeCode", KFSKeyConstants.ERROR_DOCUMENT_ACHBANKMAINT_INVALID_OFFICE_CODE);
        return valid;
    }

    protected boolean validateRoleMember(Document document){
        boolean valid = true;
        String roleId;
        OrgReviewRole orr = (OrgReviewRole)((MaintenanceDocument)document).getNewMaintainableObject().getBusinessObject();
        if(!((MaintenanceDocument)document).isEdit()){
            for(String roleName: orr.getRoleNamesToConsider()){
                roleId = KIMServiceLocator.getRoleService().getRoleIdByName(
                        KFSConstants.SysKimConstants.ORGANIZATION_REVIEWER_ROLE_NAMESPACECODE, roleName);
                //validate if the newly entered role members are already assigned to the role
                Map<String, Object> criteria = new HashMap<String, Object>();
                criteria.put(KimConstants.PrimaryKeyConstants.ROLE_ID, roleId);
                List<String> roleIds = new ArrayList<String>();
                roleIds.add(roleId);
                List<RoleMembershipInfo> roleMembershipInfoList = getRoleService().getFirstLevelRoleMembers(roleIds);
                KimDocumentRoleMember member;
                for(RoleMembershipInfo roleMembershipInfo: roleMembershipInfoList){
                    member = orr.getMemberOfType(roleMembershipInfo.getMemberTypeCode());
                    if(member!=null && member.getMemberId().equals(roleMembershipInfo.getMemberId())){
                       putFieldError(orr.getMemberFieldName(member), KFSKeyConstants.ALREADY_ASSIGNED_MEMBER);
                       valid = false;
                    }
                }
            }
        }
        return valid;
    }

    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        return super.processCustomRouteDocumentBusinessRules(document);
    }

}