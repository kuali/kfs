/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.coa.service.impl;

import java.util.Collections;
import java.util.List;

import org.kuali.kfs.coa.identity.KfsKimDocumentAttributeData;
import org.kuali.kfs.coa.identity.OrgReviewRole;
import org.kuali.kfs.coa.service.OrgReviewRoleService;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.core.api.criteria.PredicateUtils;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.common.delegate.DelegateMember;
import org.kuali.rice.kim.api.common.delegate.DelegateType;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.role.RoleMember;
import org.kuali.rice.kim.api.role.RoleMemberQueryResults;
import org.kuali.rice.kim.api.role.RoleResponsibilityAction;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.api.type.KimType;

public class OrgReviewRoleServiceImpl implements OrgReviewRoleService {

    public void populateOrgReviewRoleFromRoleMember(OrgReviewRole orr, String roleMemberId) {
        RoleMemberQueryResults roleMembers = KimApiServiceLocator.getRoleService().findRoleMembers(QueryByCriteria.Builder.fromPredicates( PredicateUtils.convertMapToPredicate(Collections.singletonMap(KimConstants.PrimaryKeyConstants.ROLE_MEMBER_ID, roleMemberId)));
        RoleMember roleMember = new RoleMemberCompleteInfo();
        if(roleMembers!=null && roleMembers.size()>0){
            roleMember = roleMembers.get(0);
        }
        orr.setRoleMemberId(roleMember.getId());
        orr.setKimDocumentRoleMember(roleMember);

        Role roleInfo = KimApiServiceLocator.getRoleService().getRole(roleMember.getRoleId());
        KimType typeInfo = KimApiServiceLocator.getKimTypeInfoService().getKimType(roleInfo.getKimTypeId());
        List<KfsKimDocumentAttributeData> attributes = orr.getAttributeSetAsQualifierList(typeInfo, roleMember.getAttributes());
        orr.setAttributes(attributes);
        orr.setRoleRspActions(getRoleRspActions(roleMember.getId()));
        orr.setRoleId(roleMember.getRoleId());
        if ( roleMember.getActiveFromDate() != null ) {
            orr.setActiveFromDate(roleMember.getActiveFromDate().toDate());
        } else {
            orr.setActiveFromDate( null );
        }
        if ( roleMember.getActiveToDate() != null ) {
            orr.setActiveToDate(roleMember.getActiveToDate().toDate());
        } else {
            orr.setActiveToDate( null );
        }
        populateObjectExtras(orr);
    }

    public void populateOrgReviewRoleFromDelegationMember(OrgReviewRole orr, String delegationMemberId) {
        DelegateMember delegationMember = KimApiServiceLocator.getRoleService().getDelegationMemberById(delegationMemberId);
        DelegateType delegation = KimApiServiceLocator.getRoleService().getDelegateTypeByDelegationId(delegationMember.getDelegationId());
        Role roleInfo = KimApiServiceLocator.getRoleService().getRole(delegation.getRoleId());
        KimType typeInfo = KimApiServiceLocator.getKimTypeInfoService().getKimType(roleInfo.getKimTypeId());
        orr.setDelegationMemberId(delegationMember.getDelegationMemberId());
        orr.setRoleMemberId(delegationMember.getRoleMemberId());
        orr.setRoleRspActions(getRoleRspActions(delegationMember.getRoleMemberId()));
        orr.setAttributes(orr.getAttributeSetAsQualifierList(typeInfo, delegationMember.getAttributes()));
        orr.setRoleId(delegation.getRoleId());
        orr.setDelegationTypeCode(delegationMember.getDelegationTypeCode());
        orr.setRoleDocumentDelegationMember(delegationMember);
        populateObjectExtras(orr);
    }

    protected void populateObjectExtras( OrgReviewRole orr ) {
        Role role = orr.getRole();
        //Set the role details
        orr.setRoleName(role.getName());
        orr.setNamespaceCode(role.getNamespaceCode());
        
        orr.setChartOfAccountsCode(orr.getAttributeValue(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE));
        orr.setOrganizationCode(orr.getAttributeValue(KfsKimAttributes.ORGANIZATION_CODE));
        orr.setOverrideCode(orr.getAttributeValue(KfsKimAttributes.ACCOUNTING_LINE_OVERRIDE_CODE));
        orr.setFromAmount(orr.getAttributeValue(KfsKimAttributes.FROM_AMOUNT));
        orr.setToAmount(orr.getAttributeValue(KfsKimAttributes.TO_AMOUNT));
        orr.setFinancialSystemDocumentTypeCode(orr.getAttributeValue(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME));
        
        orr.getChart().setChartOfAccountsCode(orr.getChartOfAccountsCode());
        orr.getOrganization().setOrganizationCode(orr.getOrganizationCode());

        if(orr.getRoleRspActions()!=null && orr.getRoleRspActions().size()>0){
            orr.setActionTypeCode(orr.getRoleRspActions().get(0).getActionTypeCode());
            orr.setPriorityNumber(orr.getRoleRspActions().get(0).getPriorityNumber()==null?"":orr.getRoleRspActions().get(0).getPriorityNumber()+"");
            orr.setActionPolicyCode(orr.getRoleRspActions().get(0).getActionPolicyCode());
            orr.setForceAction(orr.getRoleRspActions().get(0).isForceAction());
        }
        if(orr.getPerson()!=null){
            orr.setPrincipalMemberPrincipalId(orr.getPerson().getPrincipalId());
            orr.setPrincipalMemberPrincipalName(orr.getPerson().getPrincipalName());
        }
        // RICE20
        // FIXME : this is using the wrong role
        if(orr.getRole()!=null){
            orr.setRoleMemberRoleId(orr.getRole().getId());
            orr.setRoleMemberRoleNamespaceCode(orr.getRole().getNamespaceCode());
            orr.setRoleMemberRoleName(orr.getRole().getName());
        }
        if(orr.getGroup()!=null){
            orr.setGroupMemberGroupId(orr.getGroup().getId());
            orr.setGroupMemberGroupNamespaceCode(orr.getGroup().getNamespaceCode());
            orr.setGroupMemberGroupName(orr.getGroup().getName());
        }
    }
    
//    protected List<RoleResponsibilityAction> getRoleRspActions(String roleMemberId){
//        return KimApiServiceLocator.getRoleService().getRoleMemberResponsibilityActions(roleMemberId);
//    }
    
}
