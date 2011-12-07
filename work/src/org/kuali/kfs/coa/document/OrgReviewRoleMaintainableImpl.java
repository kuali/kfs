/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.kfs.coa.document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.kuali.kfs.coa.identity.KfsKimDocRoleMember;
import org.kuali.kfs.coa.identity.KfsKimDocumentAttributeData;
import org.kuali.kfs.coa.identity.OrgReviewRole;
import org.kuali.kfs.coa.identity.OrgReviewRoleLookupableHelperServiceImpl;
import org.kuali.kfs.coa.service.OrgReviewRoleService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.core.api.membership.MemberType;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.common.delegate.DelegateMember;
import org.kuali.rice.kim.api.common.delegate.DelegateMemberContract;
import org.kuali.rice.kim.api.common.delegate.DelegateType;
import org.kuali.rice.kim.api.common.delegate.DelegateTypeContract;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.group.GroupService;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.role.RoleMember;
import org.kuali.rice.kim.api.role.RoleResponsibility;
import org.kuali.rice.kim.api.role.RoleResponsibilityAction;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.api.services.IdentityManagementService;
import org.kuali.rice.kim.api.type.KimType;
import org.kuali.rice.kim.api.type.KimTypeInfoService;
import org.kuali.rice.kim.bo.types.dto.AttributeDefinitionMap;
import org.kuali.rice.kim.util.KimCommonUtils;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.Maintainable;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.kns.web.ui.Section;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.kuali.rice.krad.document.MaintenanceLock;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;


public class OrgReviewRoleMaintainableImpl extends FinancialSystemMaintainable {

    private transient static RoleService roleManagementService;
    private transient static GroupService groupService;
    private transient static IdentityManagementService identityManagementService;
    private transient static KimTypeInfoService typeInfoService;
    private transient static OrgReviewRoleService orgReviewRoleService;
    
    @Override
    public boolean isExternalBusinessObject(){
        return true;
    }
    
    @Override
    public List<MaintenanceLock> generateMaintenanceLocks() {
        return Collections.emptyList();
    }
    
    public void prepareBusinessObject(BusinessObject businessObject){
        OrgReviewRole orr = (OrgReviewRole)businessObject;
        //Assuming that this is the condition when the document is loaded on edit or copy
        if((KRADConstants.MAINTENANCE_EDIT_METHOD_TO_CALL.equals(orr.getMethodToCall()) ||
                KRADConstants.MAINTENANCE_COPY_METHOD_TO_CALL.equals(orr.getMethodToCall())) &&
                (StringUtils.isNotEmpty(orr.getODelMId()) || StringUtils.isNotEmpty(orr.getORMId()))){
            Map<String, String> criteria;
            if(StringUtils.isNotEmpty(orr.getODelMId()) && !orr.isCreateDelegation()){
                getOrgReviewRoleService().populateOrgReviewRoleFromDelegationMember(orr, orr.getODelMId());
                
                orr.setDelegate(true);
                orr.setODelMId("");
            } else if(StringUtils.isNotEmpty(orr.getORMId())){
                getOrgReviewRoleService().populateOrgReviewRoleFromRoleMember(orr, orr.getORMId());
                
                if(orr.isCreateDelegation()) {
                    orr.setDelegate(true);
                    orr.setKimDocumentRoleMember(null);
                } else {
                    orr.setDelegate(false);
                }
                orr.setORMId("");
            }
            if(orr.isCreateDelegation()){
                orr.setPrincipalMemberPrincipalId(null);
                orr.setPrincipalMemberPrincipalName(null);
                orr.setRoleMemberRoleId(null);
                orr.setRoleMemberRoleNamespaceCode(null);
                orr.setRoleMemberRoleName(null);
                orr.setGroupMemberGroupId(null);
                orr.setGroupMemberGroupNamespaceCode(null);
                orr.setGroupMemberGroupName(null);
            }
        }
        super.setBusinessObject(orr);
    }
    
    public List<RoleResponsibilityAction> getRoleRspActions(String roleMemberId){
        return getRoleService().getRoleMemberResponsibilityActions(roleMemberId);
    }
    
    @Override
    public void processAfterEdit(MaintenanceDocument document, Map<String,String[]> parameters){
        super.processAfterEdit(document, parameters);
        OrgReviewRole orr = (OrgReviewRole)document.getOldMaintainableObject().getBusinessObject();
        orr.setEdit(true);
    }

    @Override
    public void processAfterCopy(MaintenanceDocument document, Map<String,String[]> parameters){
        OrgReviewRole orr = (OrgReviewRole)document.getOldMaintainableObject().getBusinessObject();
        if(orr.isDelegate() || orr.isCreateDelegation())
            orr.setDelegationMemberId("");
        else
            orr.setRoleMemberId("");
        orr.setCopy(true);
    }

    /**
     * 
     * @see org.kuali.rice.kns.maintenance.Maintainable#prepareForSave()
     */
    public void prepareForSave() {
        super.prepareForSave();        
    }

    private Boolean hasOrganizationHierarchy = null;
    private Boolean hasAccountingOrganizationHierarchy = null;
    private String closestOrgReviewRoleParentDocumentTypeName = null;
    private Boolean shouldReviewTypesFieldBeReadOnly = null;
    
    /**
     * Override the getSections method on this maintainable so that the document type name field
     * can be set to read-only for 
     * 
     * KRAD Conversion: Inquirable performs conditionally preparing the fields for different role modes
     * or to display/hide fields on the inquiry.
     * The field definitions are NOT declared in data dictionary.
     */
    @Override
    public List getSections(MaintenanceDocument document, Maintainable oldMaintainable) {
        List<Section> sections = super.getSections(document, oldMaintainable);
        //If oldMaintainable is null, it means we are trying to get sections for the old part
        //If oldMaintainable is not null, it means we are trying to get sections for the new part
        //Refer to KualiMaintenanceForm lines 288-294
        if(oldMaintainable!=null){
            OrgReviewRole orr = (OrgReviewRole)document.getNewMaintainableObject().getBusinessObject();
            shouldReviewTypesFieldBeReadOnly(document);
            for (Section section : sections) {
                for (Row row : section.getRows()) {
                    for (Field field : row.getFields()) {
                        if(orr.isCreateRoleMember() || orr.isCopyRoleMember()){
                            prepareFieldsForCreateRoleMemberMode(field);
                        } else if(orr.isCreateDelegation() || orr.isCopyDelegation()){
                            prepareFieldsForCreateDelegationMode(field);
                        } else if(orr.isEditRoleMember()){
                            prepareFieldsForEditRoleMember(field, orr);
                        } else if(orr.isEditDelegation()){
                            prepareFieldsForEditDelegation(field, orr);
                        }
                        prepareFieldsCommon(field);
                    }
                }
            }
        } else{
            for (Section section : sections) {
                for (Row row : section.getRows()) {
                    for (Field field : row.getFields()) {
                        OrgReviewRole orr = (OrgReviewRole)document.getNewMaintainableObject().getBusinessObject();
                        if(orr.isCreateRoleMember() || orr.isCopyRoleMember() || orr.isEditRoleMember()){
                            //If the member being edited is not a delegate, do not show the delegation type code
                            if(OrgReviewRole.DELEGATION_TYPE_CODE.equals(field.getPropertyName())){
                                field.setFieldType(Field.HIDDEN);
                            }
                        }
                    }
                }
            }
        }
        return sections;
    }

    private void prepareFieldsCommon(Field field){
        if(OrgReviewRole.REVIEW_ROLES_INDICATOR_FIELD_NAME.equals(field.getPropertyName())) {
            if((shouldReviewTypesFieldBeReadOnly!=null && shouldReviewTypesFieldBeReadOnly))
                field.setReadOnly(true);
        } else if(OrgReviewRole.FROM_AMOUNT_FIELD_NAME.equals(field.getPropertyName()) ||
                OrgReviewRole.TO_AMOUNT_FIELD_NAME.equals(field.getPropertyName()) ||
                OrgReviewRole.OVERRIDE_CODE_FIELD_NAME.equals(field.getPropertyName())) {
            if((hasAccountingOrganizationHierarchy==null || !hasAccountingOrganizationHierarchy) &&  
                    (shouldReviewTypesFieldBeReadOnly!=null && shouldReviewTypesFieldBeReadOnly)){
                field.setReadOnly(true);
            }
        }
    }

    private void prepareFieldsForEditRoleMember(Field field, OrgReviewRole orr){
        if(OrgReviewRole.CHART_CODE_FIELD_NAME.equals(field.getPropertyName()) ||
                OrgReviewRole.ORG_CODE_FIELD_NAME.equals(field.getPropertyName()) ||
                OrgReviewRole.DOC_TYPE_NAME_FIELD_NAME.equals(field.getPropertyName()) ||
                OrgReviewRole.REVIEW_ROLES_INDICATOR_FIELD_NAME.equals(field.getPropertyName()) ||
                OrgReviewRole.PRINCIPAL_NAME_FIELD_NAME.equals(field.getPropertyName()) ||
                OrgReviewRole.ROLE_NAME_FIELD_NAME.equals(field.getPropertyName()) ||
                OrgReviewRole.ROLE_NAME_FIELD_NAMESPACE_CODE.equals(field.getPropertyName()) ||
                OrgReviewRole.GROUP_NAME_FIELD_NAME.equals(field.getPropertyName()) ||
                OrgReviewRole.GROUP_NAME_FIELD_NAMESPACE_CODE.equals(field.getPropertyName())){
            field.setReadOnly(true);
        }
        //If the member being edited is not a delegate, do not show the delegation type code
        if(OrgReviewRole.DELEGATION_TYPE_CODE.equals(field.getPropertyName())){
            field.setFieldType(Field.HIDDEN);
        }
    }

    private void prepareFieldsForEditDelegation(Field field, OrgReviewRole orr){
        if(OrgReviewRole.CHART_CODE_FIELD_NAME.equals(field.getPropertyName()) ||
                OrgReviewRole.ORG_CODE_FIELD_NAME.equals(field.getPropertyName()) ||
                OrgReviewRole.DOC_TYPE_NAME_FIELD_NAME.equals(field.getPropertyName()) ||
                OrgReviewRole.REVIEW_ROLES_INDICATOR_FIELD_NAME.equals(field.getPropertyName()) ||
                OrgReviewRole.PRINCIPAL_NAME_FIELD_NAME.equals(field.getPropertyName()) ||
                OrgReviewRole.ROLE_NAME_FIELD_NAME.equals(field.getPropertyName()) ||
                OrgReviewRole.ROLE_NAME_FIELD_NAMESPACE_CODE.equals(field.getPropertyName()) ||
                OrgReviewRole.GROUP_NAME_FIELD_NAME.equals(field.getPropertyName()) ||
                OrgReviewRole.GROUP_NAME_FIELD_NAMESPACE_CODE.equals(field.getPropertyName()) ||
                OrgReviewRole.ACTION_POLICY_CODE_FIELD_NAME.equals(field.getPropertyName()) || 
                OrgReviewRole.ACTION_TYPE_CODE_FIELD_NAME.equals(field.getPropertyName()) || 
                OrgReviewRole.PRIORITY_CODE_FIELD_NAME.equals(field.getPropertyName()) || 
                OrgReviewRole.FORCE_ACTION_FIELD_NAME.equals(field.getPropertyName())){
            field.setReadOnly(true);
        }
    }
    
    private void prepareFieldsForCreateRoleMemberMode(Field field){
        //If a role member (i.e. not a delegate) is being created, do not show the delegation type code
        if(OrgReviewRole.DELEGATION_TYPE_CODE.equals(field.getPropertyName())){
            field.setFieldType(Field.HIDDEN);
        }
    }

    private void prepareFieldsForCreateDelegationMode(Field field){
        //TODO: in prepareBusinessObject, populate these fields for create delegation
        if(OrgReviewRole.CHART_CODE_FIELD_NAME.equals(field.getPropertyName()) ||
                OrgReviewRole.ORG_CODE_FIELD_NAME.equals(field.getPropertyName()) ||
                OrgReviewRole.DOC_TYPE_NAME_FIELD_NAME.equals(field.getPropertyName()) || 
                OrgReviewRole.REVIEW_ROLES_INDICATOR_FIELD_NAME.equals(field.getPropertyName()) ||
                OrgReviewRole.ACTION_POLICY_CODE_FIELD_NAME.equals(field.getPropertyName()) || 
                OrgReviewRole.ACTION_TYPE_CODE_FIELD_NAME.equals(field.getPropertyName()) || 
                OrgReviewRole.PRIORITY_CODE_FIELD_NAME.equals(field.getPropertyName()) || 
                OrgReviewRole.FORCE_ACTION_FIELD_NAME.equals(field.getPropertyName())){
            field.setReadOnly(true);    
        }
    }
    
    private boolean shouldReviewTypesFieldBeReadOnly(MaintenanceDocument document){
        OrgReviewRole orr = (OrgReviewRole)document.getNewMaintainableObject().getBusinessObject();
        if(StringUtils.isEmpty(orr.getFinancialSystemDocumentTypeCode()))
            return false;
        OrgReviewRoleLookupableHelperServiceImpl orrLHSI = new OrgReviewRoleLookupableHelperServiceImpl();
        hasOrganizationHierarchy = orrLHSI.hasOrganizationHierarchy(orr.getFinancialSystemDocumentTypeCode());
        closestOrgReviewRoleParentDocumentTypeName = orrLHSI.getClosestOrgReviewRoleParentDocumentTypeName(orr.getFinancialSystemDocumentTypeCode());
        boolean isFSTransDoc = orr.getFinancialSystemDocumentTypeCode().equals(KFSConstants.FINANCIAL_SYSTEM_TRANSACTIONAL_DOCUMENT) || KFSConstants.FINANCIAL_SYSTEM_TRANSACTIONAL_DOCUMENT.equals(closestOrgReviewRoleParentDocumentTypeName);
        hasAccountingOrganizationHierarchy = orrLHSI.hasAccountingOrganizationHierarchy(orr.getFinancialSystemDocumentTypeCode()) || isFSTransDoc;
        return shouldReviewTypesFieldBeReadOnly = isFSTransDoc || hasOrganizationHierarchy || hasAccountingOrganizationHierarchy || 
          (StringUtils.isNotEmpty(closestOrgReviewRoleParentDocumentTypeName) && closestOrgReviewRoleParentDocumentTypeName.equals(KFSConstants.FINANCIAL_SYSTEM_COMPLEX_MAINTENANCE_DOCUMENT));
    }

    /***
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#refresh(java.lang.String, java.util.Map, org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    public void refresh(String refreshCaller, Map fieldValues, MaintenanceDocument document){
        super.refresh(refreshCaller, fieldValues, document);
    }
    
    /**
     * 
     * @see org.kuali.rice.kns.maintenance.Maintainable#saveBusinessObject()
     */
    @Override
    public void saveBusinessObject() {
        OrgReviewRole orr = (OrgReviewRole)getBusinessObject();
        if(orr.isDelegate() || orr.isCreateDelegation()){
            // Save delegation(s)
            List<DelegateTypeContract> objectsToSave = getDelegations(orr);
            if(objectsToSave!=null){
                for(DelegateTypeContract delegateInfo: objectsToSave){
                    for(DelegateMemberContract delegateMemberInfo: delegateInfo.getMembers()){
                        java.sql.Date fromDate = null;
                        java.sql.Date toDate = null;
                        if ( delegateMemberInfo.getActiveFromDate() != null ) {
                            fromDate = new java.sql.Date( delegateMemberInfo.getActiveFromDate().getMillis() ); 
                        }
                        if ( delegateMemberInfo.getActiveToDate() != null ) {
                            toDate = new java.sql.Date( delegateMemberInfo.getActiveToDate().getMillis() ); 
                        }
                        getRoleService().saveDelegationMemberForRole(delegateMemberInfo.getDelegationMemberId(),
                            delegateMemberInfo.getRoleMemberId(), delegateMemberInfo.getMemberId(), 
                            delegateMemberInfo.getType().getCode(), delegateInfo.getDelegationType().getCode(), 
                            delegateInfo.getRoleId(), delegateMemberInfo.getAttributes(), 
                            fromDate, toDate);
                    }
                }
            }
        } else{
            // Save role member(s)
            List<RoleMember> objectsToSave = getRoleMembers(orr);
            RoleMember savedRoleMember;
            if(objectsToSave!=null){
                for(RoleMember roleMember: objectsToSave){
                    java.sql.Date fromDate = null;
                    java.sql.Date toDate = null;
                    if ( roleMember.getActiveFromDate() != null ) {
                        fromDate = new java.sql.Date( roleMember.getActiveFromDate().getMillis() ); 
                    }
                    if ( roleMember.getActiveToDate() != null ) {
                        toDate = new java.sql.Date( roleMember.getActiveToDate().getMillis() ); 
                    }
                    savedRoleMember = getRoleService().saveRoleMemberForRole(roleMember.getId(),
                            roleMember.getMemberId(), roleMember.getType().getCode(), roleMember.getRoleId(), 
                            roleMember.getAttributes(), fromDate, toDate);
                    List<RoleResponsibilityAction> roleRspActionsToSave = getRoleRspActions(orr, roleMember);
                    if(roleRspActionsToSave!=null){
                        for(RoleResponsibilityAction rspActionInfo: roleRspActionsToSave){
                            getRoleService().saveRoleRspActions(
                                    rspActionInfo.getId(), roleMember.getRoleId(), 
                                    rspActionInfo.getRoleResponsibilityId(), savedRoleMember.getId(), 
                                    rspActionInfo.getActionTypeCode(), rspActionInfo.getActionPolicyCode(), 
                                    rspActionInfo.getPriorityNumber(), new Boolean(rspActionInfo.isForceAction()));
                        }
                    }
                }
            }
        }
    }
    
    protected List<DelegateTypeContract> getDelegations(OrgReviewRole orr){
        List<DelegateMember> delegationMembers = new ArrayList<DelegateMember>();
        List<String> roleNamesToSaveFor = getRolesToSaveFor(orr.getRoleNamesToConsider(), orr.getReviewRolesIndicator());
        List<DelegateTypeContract> roleDelegations = new ArrayList<DelegateTypeContract>();
        DelegateTypeContract roleDelegation;
        Role roleInfo;
        if(roleNamesToSaveFor!=null){
            for(String roleName: roleNamesToSaveFor){
                roleDelegation = DelegateType.Builder.create();
                roleInfo = getRoleService().getRoleByNameAndNamespaceCode(
                        KFSConstants.SysKimApiConstants.ORGANIZATION_REVIEWER_ROLE_NAMESPACECODE, roleName);
                roleDelegation.setRoleId(roleInfo.getId());
                orr.setKimTypeId(roleInfo.getKimTypeId());
                delegationMembers.addAll(getDelegationMembersToSave(orr));
                roleDelegation.setMembers(delegationMembers);
                roleDelegations.add(roleDelegation);
            }
        }
        return roleDelegations;
    }

    private List<DelegateMember> getDelegationMembersToSave(OrgReviewRole orr){
        List<DelegateMember> objectsToSave = new ArrayList<DelegateMember>();
        String memberId;
        DelegateMember delegationMember = null;
        Map<String, Object> criteria;
        if(orr.isEdit() && !orr.isCreateDelegation()){
            delegationMember = (DelegateMember)getRoleService().getDelegationMemberById(orr.getDelegationMemberId());
        }
        if(StringUtils.isNotEmpty(orr.getRoleMemberRoleNamespaceCode()) && StringUtils.isNotEmpty(orr.getRoleMemberRoleName())){
            if(delegationMember==null){
                memberId = getRoleService().getRoleIdByName(orr.getRoleMemberRoleNamespaceCode(), orr.getRoleMemberRoleName());
                delegationMember = new DelegateMember();
                delegationMember.setMemberId(memberId);
            }
            delegationMember.setDelegationTypeCode(orr.getDelegationTypeCode());
            delegationMember.setMemberTypeCode(KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE);
            delegationMember.setQualifier(getAttributes(orr, orr.getKimTypeId()));
            delegationMember.setActiveFromDate(orr.getActiveFromDate());
            delegationMember.setActiveToDate(orr.getActiveToDate());
            delegationMember.setRoleMemberId(orr.getRoleMemberId());
            objectsToSave.add(delegationMember);
            delegationMember = null;
        }
        if(StringUtils.isNotEmpty(orr.getGroupMemberGroupNamespaceCode()) && StringUtils.isNotEmpty(orr.getGroupMemberGroupName())){
            if(delegationMember==null){
                Group groupInfo = getGroupService().getGroupByName(orr.getGroupMemberGroupNamespaceCode(), orr.getGroupMemberGroupName());
                memberId = groupInfo.getGroupId();
                delegationMember = new DelegateMember();
                delegationMember.setMemberId(memberId);
            }
            delegationMember.setDelegationTypeCode(orr.getDelegationTypeCode());
            delegationMember.setMemberTypeCode(KimConstants.KimUIConstants.MEMBER_TYPE_GROUP_CODE);
            delegationMember.setQualifier(getAttributes(orr, orr.getKimTypeId()));
            delegationMember.setActiveFromDate(orr.getActiveFromDate());
            delegationMember.setActiveToDate(orr.getActiveToDate());
            delegationMember.setRoleMemberId(orr.getRoleMemberId());
            objectsToSave.add(delegationMember);
            delegationMember = null;
        }
        if(StringUtils.isNotEmpty(orr.getPrincipalMemberPrincipalName())){
            if(delegationMember==null){
                Principal principal = getIdentityManagementService().getPrincipalByPrincipalName(orr.getPrincipalMemberPrincipalName());
                delegationMember = new DelegateMember();
                delegationMember.setMemberId(principal.getPrincipalId());
            }
            delegationMember.setDelegationTypeCode(orr.getDelegationTypeCode());
            delegationMember.setMemberTypeCode(KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL_CODE);
            delegationMember.setQualifier(getAttributes(orr, orr.getKimTypeId()));
            delegationMember.setActiveFromDate(orr.getActiveFromDate());
            delegationMember.setActiveToDate(orr.getActiveToDate());
            delegationMember.setRoleMemberId(orr.getRoleMemberId());
            objectsToSave.add(delegationMember);
            delegationMember = null;
        }
        return objectsToSave;
    }
    
    private List<KfsKimDocRoleMember> getRoleMembersToSave(Role roleInfo, OrgReviewRole orr){
        KfsKimDocRoleMember roleMember = null;
        if(StringUtils.isNotEmpty(orr.getRoleMemberRoleNamespaceCode()) && StringUtils.isNotEmpty(orr.getRoleMemberRoleName())){
            String memberId = getRoleService().getRoleIdByNameAndNamespaceCode(orr.getRoleMemberRoleNamespaceCode(), orr.getRoleMemberRoleName());
            roleMember = KfsKimDocRoleMember.create(roleInfo.getId(), null, memberId, MemberType.ROLE, null, null, null);
        }
        if(roleMember==null){
            if(StringUtils.isNotEmpty(orr.getGroupMemberGroupNamespaceCode()) && StringUtils.isNotEmpty(orr.getGroupMemberGroupName())){
                Group groupInfo = getGroupService().getGroupByNameAndNamespaceCode(orr.getGroupMemberGroupNamespaceCode(), orr.getGroupMemberGroupName());
                roleMember = KfsKimDocRoleMember.create(roleInfo.getId(), null, groupInfo.getId(), MemberType.GROUP, null, null, null);
            }
        }
        if(roleMember==null){
            if(StringUtils.isNotEmpty(orr.getPrincipalMemberPrincipalName())){
                Principal principal = getIdentityManagementService().getPrincipalByPrincipalName(orr.getPrincipalMemberPrincipalName());
                roleMember = KfsKimDocRoleMember.create(roleInfo.getId(), null, principal.getPrincipalId(), MemberType.GROUP, null, null, null);
            }
        }
        if(orr.isEdit()){
            roleMember.setId(orr.getRoleMemberId());
        }
        roleMember.setQualifier(getAttributes(orr, roleMember, roleInfo.getKimTypeId()));
        roleMember.setActiveFromDate( new DateTime( orr.getActiveFromDate().getTime() ) );
        roleMember.setActiveToDate( new DateTime( orr.getActiveToDate().getTime() ) );
        return Collections.singletonList(roleMember);
    }
    
    private List<String> getRolesToSaveFor(List<String> roleNamesToConsider, String reviewRolesIndicator){
        List<String> roleToSaveFor = new ArrayList<String>();
        if(roleNamesToConsider!=null){
            if(KFSConstants.COAConstants.ORG_REVIEW_ROLE_ORG_ACC_ONLY_CODE.equals(reviewRolesIndicator)){
                roleToSaveFor.add(KFSConstants.SysKimApiConstants.ACCOUNTING_REVIEWER_ROLE_NAME);
            } else if(KFSConstants.COAConstants.ORG_REVIEW_ROLE_ORG_ONLY_CODE.equals(reviewRolesIndicator)){
                roleToSaveFor.add(KFSConstants.SysKimApiConstants.ORGANIZATION_REVIEWER_ROLE_NAME);
            } else{
                roleToSaveFor.addAll(roleNamesToConsider);
            }
        }
        return roleToSaveFor;
    }
    
    protected List<KfsKimDocRoleMember> getRoleMembers(OrgReviewRole orr){
        List<KfsKimDocRoleMember> objectsToSave = new ArrayList<KfsKimDocRoleMember>();
        List<String> roleNamesToSaveFor = getRolesToSaveFor(orr.getRoleNamesToConsider(), orr.getReviewRolesIndicator());
        String roleId;
        String memberId;
        RoleMember roleMember = null;
        Role roleInfo;
        Map<String, Object> criteria;
        if(roleNamesToSaveFor!=null){
            for(String roleName: roleNamesToSaveFor){
                roleId = getRoleService().getRoleIdByNameAndNamespaceCode(
                        KFSConstants.SysKimApiConstants.ORGANIZATION_REVIEWER_ROLE_NAMESPACECODE, roleName);
                roleInfo = getRoleService().getRole(roleId);
                objectsToSave.addAll(getRoleMembersToSave(roleInfo, orr));
            }
        }
        return objectsToSave;
    }
    
    public String getKimAttributeDefnId(AttributeDefinition definition){
        if (definition instanceof KimAttributeDefinition) {
            return ((KimAttributeDefinition)definition).getKimAttrDefnId();
        } else {
            return ((KimAttributeDefinition)definition).getKimAttrDefnId();
        }
    }

    public String getKimAttributeDefnId(AttributeDefinitionMap attributeDefinitions, String attributeName){
        if(attributeDefinitions.values()!=null){
            for(AttributeDefinition definition: attributeDefinitions.values()){
                if(definition.getName().equals(attributeName))
                    return getKimAttributeDefnId(definition);
            }
        }
        return null;
    }
    
    //protected String getKimAttributeId()
    protected Map<String,String> getAttributes(
            OrgReviewRole orr, RoleMember roleMember, String kimTypeId){
        KimType kimType = getTypeInfoService().getKimType(kimTypeId);
        KimTypeInfoService typeService = KimCommonUtils.getKimTypeInfoService(kimType);
        AttributeDefinitionMap attributeDefinitions = typeService.getKimType(kimTypeId).getAttributeDefinitions();
        List<KfsKimDocumentAttributeData> attributeDataList = new ArrayList<KfsKimDocumentAttributeData>();
        KfsKimDocumentAttributeData attributeData = new KfsKimDocumentAttributeData();
        //chart code
        String attributeDefnId = getKimAttributeDefnId(attributeDefinitions, KfsKimAttributes.CHART_OF_ACCOUNTS_CODE);
        if(StringUtils.isNotEmpty(attributeDefnId) && orr.getFinancialSystemDocumentTypeCode()!=null){
            attributeData.setKimTypId(kimTypeId);
            attributeData.setAttrVal(orr.getChartOfAccountsCode());
            attributeData.setKimAttrDefnId(attributeDefnId);
            attributeData.setKimAttribute(kimType.getAttributeDefinitionById(attributeDefnId));
            attributeDataList.add(attributeData);
        }
        
        //org code
        attributeDefnId = getKimAttributeDefnId(attributeDefinitions, KfsKimAttributes.ORGANIZATION_CODE);
        if(StringUtils.isNotEmpty(attributeDefnId) && orr.getFinancialSystemDocumentTypeCode()!=null){
            attributeData = new KfsKimDocumentAttributeData();
            attributeData.setKimTypId(kimTypeId);
            attributeData.setAttrVal(orr.getOrganizationCode());
            attributeData.setKimAttrDefnId(attributeDefnId);
            attributeData.setKimAttribute(kimType.getAttributeDefinitionById(attributeDefnId));
            attributeDataList.add(attributeData);
        }
        
        //document type
        attributeDefnId = getKimAttributeDefnId(attributeDefinitions, KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME);
        if(StringUtils.isNotEmpty(attributeDefnId) && orr.getFinancialSystemDocumentTypeCode()!=null){
            attributeData = new KfsKimDocumentAttributeData();
            attributeData.setKimTypId(kimTypeId);
            attributeData.setAttrVal(orr.getFinancialSystemDocumentTypeCode());
            attributeData.setKimAttrDefnId(attributeDefnId);
            attributeData.setKimAttribute(kimType.getAttributeDefinitionById(attributeDefnId));
            attributeDataList.add(attributeData);
        }

        //override code
        attributeDefnId = getKimAttributeDefnId(attributeDefinitions, KfsKimAttributes.ACCOUNTING_LINE_OVERRIDE_CODE);
        if(StringUtils.isNotEmpty(attributeDefnId) && orr.getOverrideCode()!=null){
            attributeData = new KfsKimDocumentAttributeData();
            attributeData.setKimTypId(kimTypeId);
            attributeData.setAttrVal(orr.getOverrideCode());
            attributeData.setKimAttrDefnId(attributeDefnId);
            attributeData.setKimAttribute(kimType.getAttributeDefinitionById(attributeDefnId));
            attributeDataList.add(attributeData);
        }
        
        //from amount
        attributeDefnId = getKimAttributeDefnId(attributeDefinitions, KfsKimAttributes.FROM_AMOUNT);
        if(StringUtils.isNotEmpty(attributeDefnId)){
            attributeData = new KfsKimDocumentAttributeData();
            attributeData.setKimTypId(kimTypeId);
            attributeData.setAttrVal(orr.getFromAmountStr());
            attributeData.setKimAttrDefnId(attributeDefnId);
            attributeData.setKimAttribute(kimType.getAttributeDefinitionById(attributeDefnId));
            attributeDataList.add(attributeData);
        }
        
        //to amount
        attributeDefnId = getKimAttributeDefnId(attributeDefinitions, KfsKimAttributes.TO_AMOUNT);
        if(StringUtils.isNotEmpty(attributeDefnId)){
            attributeData = new KfsKimDocumentAttributeData();
            attributeData.setKimTypId(kimTypeId);
            attributeData.setAttrVal(orr.getToAmountStr());
            attributeData.setKimAttrDefnId(attributeDefnId);
            attributeData.setKimAttribute(kimType.getAttributeDefinitionById(attributeDefnId));
            attributeDataList.add(attributeData);
        }
        
        return orr.getQualifierAsAttributeSet(attributeDataList);
    }
    
    protected Map<String,String> getAttributes(OrgReviewRole orr, String kimTypeId){
        if(StringUtils.isEmpty(kimTypeId)) return null;
        
        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("kimTypeId", kimTypeId);
        KimType kimType = getTypeInfoService().getKimType(kimTypeId);
        KimTypeInfoService typeService = KimCommonUtils.getKimTypeInfoService(kimType);
        AttributeDefinitionMap attributeDefinitions = typeService.getKimType(kimTypeId).getAttributeDefinitions();
        List<KfsKimDocumentAttributeData> attributeDataList = new ArrayList<KfsKimDocumentAttributeData>();
        KfsKimDocumentAttributeData attributeData = new KfsKimDocumentAttributeData();
        String attributeDefnId = getKimAttributeDefnId(attributeDefinitions, KfsKimAttributes.CHART_OF_ACCOUNTS_CODE);
        if(StringUtils.isNotEmpty(attributeDefnId) && orr.getFinancialSystemDocumentTypeCode()!=null){
            attributeData.setKimTypId(kimTypeId);
            attributeData.setAttrVal(orr.getChartOfAccountsCode());
            attributeData.setKimAttrDefnId(attributeDefnId);
            attributeData.setKimAttribute(kimType.getAttributeDefinitionById(attributeDefnId));
            attributeDataList.add(attributeData);
        }
        
        //org code
        attributeDefnId = getKimAttributeDefnId(attributeDefinitions, KfsKimAttributes.ORGANIZATION_CODE);
        if(StringUtils.isNotEmpty(attributeDefnId) && orr.getFinancialSystemDocumentTypeCode()!=null){
            attributeData = new KfsKimDocumentAttributeData();
            attributeData.setKimTypId(kimTypeId);
            attributeData.setAttrVal(orr.getOrganizationCode());
            attributeData.setKimAttrDefnId(attributeDefnId);
            attributeData.setKimAttribute(kimType.getAttributeDefinitionById(attributeDefnId));
            attributeDataList.add(attributeData);
        }

        //document type
        attributeDefnId = getKimAttributeDefnId(attributeDefinitions, KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME);
        if(StringUtils.isNotEmpty(attributeDefnId) && orr.getFinancialSystemDocumentTypeCode()!=null){
            attributeData = new KfsKimDocumentAttributeData();
            attributeData.setKimTypId(kimTypeId);
            attributeData.setAttrVal(orr.getFinancialSystemDocumentTypeCode());
            attributeData.setKimAttrDefnId(attributeDefnId);
            attributeData.setKimAttribute(kimType.getAttributeDefinitionById(attributeDefnId));
            attributeDataList.add(attributeData);
        }

        //override code
        attributeDefnId = getKimAttributeDefnId(attributeDefinitions, KfsKimAttributes.ACCOUNTING_LINE_OVERRIDE_CODE);
        if(StringUtils.isNotEmpty(attributeDefnId) && orr.getOverrideCode()!=null){
            attributeData = new KfsKimDocumentAttributeData();
            attributeData.setKimTypId(kimTypeId);
            attributeData.setAttrVal(orr.getOverrideCode());
            attributeData.setKimAttrDefnId(attributeDefnId);
            attributeData.setKimAttribute(kimType.getAttributeDefinitionById(attributeDefnId));
            attributeDataList.add(attributeData);
        }
        
        //from amount
        attributeDefnId = getKimAttributeDefnId(attributeDefinitions, KfsKimAttributes.FROM_AMOUNT);
        if(StringUtils.isNotEmpty(attributeDefnId) && orr.getFromAmount()!=null){
            attributeData = new KfsKimDocumentAttributeData();
            attributeData.setKimTypId(kimTypeId);
            attributeData.setAttrVal(orr.getFromAmountStr());
            attributeData.setKimAttrDefnId(attributeDefnId);
            attributeData.setKimAttribute(kimType.getAttributeDefinitionById(attributeDefnId));
            attributeDataList.add(attributeData);
        }
        
        //to amount
        attributeDefnId = getKimAttributeDefnId(attributeDefinitions, KfsKimAttributes.TO_AMOUNT);
        if(StringUtils.isNotEmpty(attributeDefnId) && orr.getToAmount()!=null){
            attributeData = new KfsKimDocumentAttributeData();
            attributeData.setKimTypId(kimTypeId);
            attributeData.setAttrVal(orr.getToAmountStr());
            attributeData.setKimAttrDefnId(attributeDefnId);
            attributeData.setKimAttribute(kimType.getAttributeDefinitionById(attributeDefnId));
            attributeDataList.add(attributeData);
        }
        
        return orr.getQualifierAsAttributeSet(attributeDataList);
    }
    
    protected List<RoleResponsibilityAction.Builder> getRoleRspActions(OrgReviewRole orr, RoleMember roleMember){
        List<RoleResponsibilityAction.Builder> roleRspActions = new ArrayList<RoleResponsibilityAction.Builder>();
        RoleResponsibilityAction.Builder roleRspAction;
        //Assuming that there is only one responsibility for an org role
        //Get it now given the role id
        List<RoleResponsibility> roleResponsibilityInfos = ((List<RoleResponsibility>)getRoleService().getRoleResponsibilities(roleMember.getRoleId()));
        //Assuming that there is only 1 responsibility for both the org review roles
        if(roleResponsibilityInfos!=null && roleResponsibilityInfos.size()<1)
            throw new RuntimeException("The Org Review Role id:"+roleMember.getRoleId()+" does not have any responsibility associated with it");

        List<RoleResponsibilityAction> origRoleRspActions = ((List<RoleResponsibilityAction>)getRoleService().getRoleMemberResponsibilityActions(roleMember.getId()));
        
        roleRspAction = RoleResponsibilityAction.Builder.create();
        if(origRoleRspActions!=null && origRoleRspActions.size()>0){
            RoleResponsibilityAction origActionInfo = origRoleRspActions.get(0);
            roleRspAction.setId(origActionInfo.getId());
        } 
        roleRspAction.setRoleMemberId(roleMember.getId());
        RoleResponsibility roleResponsibilityInfo = roleResponsibilityInfos.get(0);
        roleRspAction.setRoleResponsibilityId(roleResponsibilityInfo.getRoleResponsibilityId());
        roleRspAction.setActionTypeCode(orr.getActionTypeCode());
        roleRspAction.setActionPolicyCode(orr.getActionPolicyCode());
        if(StringUtils.isNotBlank(orr.getPriorityNumber())){
            try{
                roleRspAction.setPriorityNumber(Integer.parseInt(orr.getPriorityNumber()));
            } catch(Exception nfx){
                //ignore
            }
        }
        roleRspAction.setForceAction(orr.isForceAction());
        roleRspActions.add(roleRspAction);
        return roleRspActions;
    }
    
    protected OrgReviewRoleService getOrgReviewRoleService(){
        if(orgReviewRoleService==null){
            orgReviewRoleService = SpringContext.getBean( OrgReviewRoleService.class );
        }
        return orgReviewRoleService;
    }
    
    protected RoleService getRoleService(){
        if(roleManagementService==null){
            roleManagementService = SpringContext.getBean( RoleService.class ); 
        }
        return roleManagementService;
    }

    protected GroupService getGroupService(){
        if(groupService==null){
            groupService = SpringContext.getBean( GroupService.class );
        }
        return groupService;
    }

    protected IdentityManagementService getIdentityManagementService(){
        if(identityManagementService==null){
            identityManagementService = SpringContext.getBean( IdentityManagementService.class ); 
        }
        return identityManagementService;
    }

    protected KimTypeInfoService getTypeInfoService(){
        if(typeInfoService==null){
            typeInfoService = SpringContext.getBean( KimTypeInfoService.class ); 
        }
        return typeInfoService;
    }
    
    private DelegateMember getDelegateMemberFromList(List<DelegateMember> delegateMembers, String memberId, String memberTypeCode){
        if(StringUtils.isEmpty(memberId) || StringUtils.isEmpty(memberTypeCode))
            return null;
        if(delegateMembers!=null){
            for(DelegateMember info: delegateMembers){
                if(StringUtils.equals(info.getMemberId(), memberId) || 
                        StringUtils.equals(info.getType().getCode(), memberTypeCode)){
                    return info;
                }
            }
        }
        return null;
    }

    @Override
    public Map populateBusinessObject(Map<String, String> fieldValues, MaintenanceDocument maintenanceDocument, String methodToCall) {
        String docTypeName = "";
        OrgReviewRoleLookupableHelperServiceImpl orrLHSI = new OrgReviewRoleLookupableHelperServiceImpl();
        if(fieldValues.containsKey(OrgReviewRole.DOC_TYPE_NAME_FIELD_NAME)){
            docTypeName = (String)fieldValues.get(OrgReviewRole.DOC_TYPE_NAME_FIELD_NAME);
        }
        if(KFSConstants.RETURN_METHOD_TO_CALL.equals(methodToCall) &&
           fieldValues.containsKey(OrgReviewRole.DOC_TYPE_NAME_FIELD_NAME) &&
           orrLHSI.isValidDocumentTypeForOrgReview(docTypeName) == false){
            
            GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(KFSConstants.MAINTENANCE_NEW_MAINTAINABLE + OrgReviewRole.DOC_TYPE_NAME_FIELD_NAME, KFSKeyConstants.ERROR_DOCUMENT_ORGREVIEW_INVALID_DOCUMENT_TYPE, docTypeName);            
            return new HashMap();
            
        }else{
            return super.populateBusinessObject(fieldValues, maintenanceDocument, methodToCall);
        }
    }
}
