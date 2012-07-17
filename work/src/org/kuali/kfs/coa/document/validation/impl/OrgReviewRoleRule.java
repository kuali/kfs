/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.kfs.coa.document.validation.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.identity.KfsKimDocDelegateMember;
import org.kuali.kfs.coa.identity.KfsKimDocRoleMember;
import org.kuali.kfs.coa.identity.KfsKimDocumentAttributeData;
import org.kuali.kfs.coa.identity.OrgReviewRole;
import org.kuali.kfs.coa.service.OrgReviewRoleService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.core.api.criteria.PredicateUtils;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.membership.MemberType;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.common.delegate.DelegateMember;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.role.DelegateMemberQueryResults;
import org.kuali.rice.kim.api.role.RoleMember;
import org.kuali.rice.kim.api.role.RoleMemberQueryResults;
import org.kuali.rice.kim.api.role.RoleMembership;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * This class represents the business rules for the maintenance of {@link AccountGlobal} business objects
 */
public class OrgReviewRoleRule extends MaintenanceDocumentRuleBase {
    private static final Logger LOG = Logger.getLogger(OrgReviewRoleRule.class);

    private transient static OrgReviewRoleService orgReviewRoleService;

    /**
     * Need to override to avoid the primary key check which (wrongly) assumes that the object's PKs can be found in the persistence service.
     *
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processGlobalSaveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processGlobalSaveDocumentBusinessRules(MaintenanceDocument document) {
        return dataDictionaryValidate(document);
    }

    @Override
    public boolean processRouteDocument(Document document) {
        boolean valid = super.processRouteDocument(document);
        OrgReviewRole orr = (OrgReviewRole)((MaintenanceDocument)document).getNewMaintainableObject().getBusinessObject();
        if(!orr.hasAnyMember()){
            valid = false;
            putFieldError( OrgReviewRole.PRINCIPAL_NAME_FIELD_NAME, KFSKeyConstants.NO_MEMBER_SELECTED);
        } else{
            getOrgReviewRoleService().validateDocumentType(orr.getFinancialSystemDocumentTypeCode());
            validateRoleMembersToSave(orr);
            if(orr.isDelegate()){
                // Save delegation(s)
                valid = validateDelegation(orr, ((MaintenanceDocument)document).isEdit());
            } else{
                // Save role member(s)
                valid = validateRoleMember(orr, ((MaintenanceDocument)document).isEdit());
            }
        }
        return valid;
    }

    protected void validateRoleMembersToSave(OrgReviewRole orr){
        if(orr==null) return;
        boolean valid = true;
        String memberId;
        if(StringUtils.isNotEmpty(orr.getPrincipalMemberPrincipalName())){
            Principal principal = KimApiServiceLocator.getIdentityService().getPrincipalByPrincipalName(orr.getPrincipalMemberPrincipalName());
            if(principal == null || StringUtils.isEmpty(principal.getPrincipalId())){
                GlobalVariables.getMessageMap().putError(MAINTAINABLE_ERROR_PATH+"."+OrgReviewRole.PRINCIPAL_NAME_FIELD_NAME,
                        KFSKeyConstants.ERROR_DOCUMENT_OBJCODE_MUST_BEVALID, KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL);
                valid = false;
            }
        }
        if(StringUtils.isNotEmpty(orr.getRoleMemberRoleName())){
            memberId = KimApiServiceLocator.getRoleService().getRoleIdByNamespaceCodeAndName(orr.getRoleMemberRoleNamespaceCode(), orr.getRoleMemberRoleName());
            if(memberId == null){
                GlobalVariables.getMessageMap().putError(MAINTAINABLE_ERROR_PATH+"."+OrgReviewRole.ROLE_NAME_FIELD_NAME,
                        KFSKeyConstants.ERROR_DOCUMENT_OBJCODE_MUST_BEVALID, KimConstants.KimUIConstants.MEMBER_TYPE_ROLE);
                valid = false;
            }
        }
        if(StringUtils.isNotEmpty(orr.getGroupMemberGroupName())){
            Group groupInfo = KimApiServiceLocator.getGroupService().getGroupByNamespaceCodeAndName(orr.getGroupMemberGroupNamespaceCode(), orr.getGroupMemberGroupName());
            if(groupInfo == null || StringUtils.isEmpty(groupInfo.getId())){
                GlobalVariables.getMessageMap().putError(MAINTAINABLE_ERROR_PATH+"."+OrgReviewRole.GROUP_NAME_FIELD_NAME,
                        KFSKeyConstants.ERROR_DOCUMENT_OBJCODE_MUST_BEVALID, KimConstants.KimUIConstants.MEMBER_TYPE_GROUP);
                valid = false;
            }
        }
        if(!valid)
            throw new ValidationException("Invalid Role Member Data");
    }

    protected boolean validateDelegation(OrgReviewRole orr, boolean isEdit){
        boolean valid = true;
        String roleId;
        if(!isEdit && orr.getRoleNamesToConsider()!=null){
            for(String roleName: orr.getRoleNamesToConsider()){
                roleId = KimApiServiceLocator.getRoleService().getRoleIdByNamespaceCodeAndName(
                        KFSConstants.SysKimApiConstants.ORGANIZATION_REVIEWER_ROLE_NAMESPACECODE, roleName);
                Map<String, String> criteria = new HashMap<String, String>();
                criteria.put(KimConstants.PrimaryKeyConstants.ROLE_ID, roleId);
                DelegateMemberQueryResults results = KimApiServiceLocator.getRoleService().findDelegateMembers(QueryByCriteria.Builder.fromPredicates( PredicateUtils.convertMapToPredicate(Collections.singletonMap(KimConstants.PrimaryKeyConstants.ROLE_ID, roleId))));
                List<DelegateMember> roleDelegationMembers = results.getResults();

                //validate if the newly entered delegation members are already assigned to the role
                if(roleDelegationMembers!=null){
                    for(DelegateMember delegationMember: roleDelegationMembers){
                        KfsKimDocDelegateMember member = orr.getDelegationMemberOfType(delegationMember.getType().getCode());
                        if(member!=null && StringUtils.isNotBlank(member.getMemberName())){
                            String memberId = getMemberIdByName(member.getType(), member.getMemberNamespaceCode(), member.getMemberName());
                            boolean attributesUnique = areAttributesUnique(orr, delegationMember.getAttributes());
                            if(!attributesUnique && member!=null && StringUtils.isNotBlank(memberId) && memberId.equals(delegationMember.getMemberId())
                                    && (StringUtils.isNotBlank(orr.getRoleMemberId()) && StringUtils.isNotBlank(delegationMember.getRoleMemberId())
                                            && orr.getRoleMemberId().equals(delegationMember.getRoleMemberId()))){
                               putFieldError(orr.getMemberFieldName(member.getType()), KFSKeyConstants.ALREADY_ASSIGNED_MEMBER);
                               valid = false;
                            }
                        }
                    }
                }
            }
        }
        if(StringUtils.isNotEmpty(orr.getRoleMemberId())){
            valid = validateAmounts(orr);
            //TODO: put from and to amounts validation here
            RoleMemberQueryResults roleMemberResults = KimApiServiceLocator.getRoleService().findRoleMembers(QueryByCriteria.Builder.fromPredicates( PredicateUtils.convertMapToPredicate(Collections.singletonMap(KimConstants.PrimaryKeyConstants.ID, orr.getRoleMemberId()))));
            List<RoleMember> roleMembers = roleMemberResults.getResults();
            RoleMember roleMember = null;
            if(roleMembers!=null && !roleMembers.isEmpty() ) {
                roleMember = roleMembers.get(0);
            }
            List<KfsKimDocumentAttributeData> attributes = orr.getAttributeSetAsQualifierList(roleMember.getAttributes());
            if(roleMember!=null && attributes!=null){
                for(KfsKimDocumentAttributeData attribute: attributes){
                    if(KfsKimAttributes.FROM_AMOUNT.equals(attribute.getKimAttribute().getAttributeName())){
                        KualiDecimal roleMemberFromAmount = new KualiDecimal(attribute.getAttrVal());
                        if(orr.getFromAmount()!=null){
                            KualiDecimal inputFromAmount = orr.getFromAmount();
                            if((roleMemberFromAmount!=null && inputFromAmount==null) || (inputFromAmount!=null && inputFromAmount.isLessThan(roleMemberFromAmount))){
                                putFieldError(KfsKimAttributes.FROM_AMOUNT, KFSKeyConstants.FROM_AMOUNT_OUT_OF_RANGE);
                                valid = false;
                            }
                        }
                    }
                    if(KfsKimAttributes.TO_AMOUNT.equals(attribute.getKimAttribute().getAttributeName())){
                        KualiDecimal roleMemberToAmount = new KualiDecimal(attribute.getAttrVal());
                        if(orr.getToAmount()!=null){
                            KualiDecimal inputToAmount = orr.getToAmount();
                            if((roleMemberToAmount!=null && inputToAmount==null) || (inputToAmount!=null && inputToAmount.isGreaterThan(roleMemberToAmount))){
                                putFieldError(KfsKimAttributes.TO_AMOUNT, KFSKeyConstants.TO_AMOUNT_OUT_OF_RANGE);
                                valid = false;
                            }
                        }
                    }
                }
            }
        }
        //putFieldError("bankOfficeCode", KFSKeyConstants.ERROR_DOCUMENT_ACHBANKMAINT_INVALID_OFFICE_CODE);
        return valid;
    }

    protected boolean validateAmounts(OrgReviewRole orr){
        boolean valid = true;
        if(orr.getFromAmount()!=null && orr.getToAmount()!=null && orr.getFromAmount().isGreaterThan(orr.getToAmount())){
            putFieldError(KfsKimAttributes.FROM_AMOUNT, KFSKeyConstants.FROM_AMOUNT_GREATER_THAN_TO_AMOUNT);
            valid = false;
        }
        return valid;
    }

    protected boolean validateRoleMember(OrgReviewRole orr, boolean isEdit){
        boolean valid = true;
        String roleId;
        if(orr==null) return false;
        boolean attributesUnique = true;
        valid = validateAmounts(orr);
        if(!isEdit && orr.getRoleNamesToConsider()!=null){
            for(String roleName: orr.getRoleNamesToConsider()){
                roleId = KimApiServiceLocator.getRoleService().getRoleIdByNamespaceCodeAndName(
                        KFSConstants.SysKimApiConstants.ORGANIZATION_REVIEWER_ROLE_NAMESPACECODE, roleName);
                //validate if the newly entered role members are already assigned to the role
                Map<String, Object> criteria = new HashMap<String, Object>();
                criteria.put(KimConstants.PrimaryKeyConstants.ROLE_ID, roleId);
                List<String> roleIds = new ArrayList<String>();
                roleIds.add(roleId);
                List<RoleMembership> roleMembershipInfoList = KimApiServiceLocator.getRoleService().getFirstLevelRoleMembers(roleIds);
                KfsKimDocRoleMember member = null;
                String memberId = null;
                if(roleMembershipInfoList!=null){
                    for(RoleMembership roleMembershipInfo: roleMembershipInfoList){
                        member = orr.getRoleMemberOfType(roleMembershipInfo.getType().getCode());
                        if(member!=null && StringUtils.isNotEmpty(member.getMemberName())){
                            memberId = getMemberIdByName(member.getType(), member.getMemberNamespaceCode(), member.getMemberName());
                            attributesUnique = areAttributesUnique(orr, roleMembershipInfo.getQualifier());
                            if(!attributesUnique && member!=null && StringUtils.isNotEmpty(memberId) && memberId.equals(roleMembershipInfo.getMemberId()) &&
                                    member.getType().equals(roleMembershipInfo.getType())){
                               putFieldError(orr.getMemberFieldName(member.getType()), KFSKeyConstants.ALREADY_ASSIGNED_MEMBER);
                               valid = false;
                            }
                        }
                    }
                }
            }
        }
        return valid;
    }

    public String getMemberIdByName(MemberType memberType, String memberNamespaceCode, String memberName){
        String memberId = "";
        if(MemberType.PRINCIPAL.equals(memberType)){
            Principal principal = KimApiServiceLocator.getIdentityService().getPrincipalByPrincipalName(memberName);
            if(principal!=null) {
                memberId = principal.getPrincipalId();
            }

       } else if(MemberType.GROUP.equals(memberType)){
            Group groupInfo = getGroupService().getGroupByNamespaceCodeAndName(memberNamespaceCode, memberName);
            if (groupInfo!=null) {
                memberId = groupInfo.getId();
            }

        } else if(MemberType.ROLE.equals(memberType)){
            memberId = KimApiServiceLocator.getRoleService().getRoleIdByNamespaceCodeAndName(memberNamespaceCode, memberName);
        }
        return memberId;
    }

    protected boolean areAttributesUnique(OrgReviewRole orr, Map<String,String> attributeSet){
        String docTypeName = orr.getFinancialSystemDocumentTypeCode();
        String chartOfAccountCode = orr.getChartOfAccountsCode();
        String organizationCode = orr.getOrganizationCode();
        boolean uniqueAttributes =
            !StringUtils.equals(docTypeName, attributeSet.get(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME)) ||
            !StringUtils.equals(chartOfAccountCode, attributeSet.get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE)) ||
            !StringUtils.equals(organizationCode, attributeSet.get(KfsKimAttributes.ORGANIZATION_CODE));
        return uniqueAttributes;
    }

    protected OrgReviewRoleService getOrgReviewRoleService(){
        if(orgReviewRoleService==null){
            orgReviewRoleService = SpringContext.getBean( OrgReviewRoleService.class );
        }
        return orgReviewRoleService;
    }
}
