/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.coa.document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.identity.KfsKimDocumentAttributeData;
import org.kuali.kfs.coa.identity.OrgReviewRole;
import org.kuali.kfs.coa.identity.OrgReviewRoleLookupableHelperServiceImpl;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kim.bo.Role;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.bo.group.dto.GroupInfo;
import org.kuali.rice.kim.bo.role.dto.DelegateInfo;
import org.kuali.rice.kim.bo.role.dto.DelegateMemberCompleteInfo;
import org.kuali.rice.kim.bo.role.dto.DelegateTypeInfo;
import org.kuali.rice.kim.bo.role.dto.KimRoleInfo;
import org.kuali.rice.kim.bo.role.dto.RoleMemberCompleteInfo;
import org.kuali.rice.kim.bo.role.dto.RoleResponsibilityActionInfo;
import org.kuali.rice.kim.bo.role.dto.RoleResponsibilityInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeDefinitionMap;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.bo.types.dto.KimTypeInfo;
import org.kuali.rice.kim.service.GroupService;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.KimTypeInfoService;
import org.kuali.rice.kim.service.RoleManagementService;
import org.kuali.rice.kim.service.support.KimTypeService;
import org.kuali.rice.kim.util.KimCommonUtils;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.datadictionary.AttributeDefinition;
import org.kuali.rice.kns.datadictionary.KimDataDictionaryAttributeDefinition;
import org.kuali.rice.kns.datadictionary.KimNonDataDictionaryAttributeDefinition;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.document.MaintenanceLock;
import org.kuali.rice.kns.exception.KualiException;
import org.kuali.rice.kns.maintenance.Maintainable;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.kns.web.ui.Section;

import edu.emory.mathcs.backport.java.util.Collections;

public class OrgReviewRoleMaintainableImpl extends FinancialSystemMaintainable {

    private transient static RoleManagementService roleManagementService;
    private transient static GroupService groupService;
    private transient static IdentityManagementService identityManagementService;
    private transient static KimTypeInfoService typeInfoService;
    
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
        //TODO: revisit this
        if((KNSConstants.MAINTENANCE_EDIT_METHOD_TO_CALL.equals(orr.getMethodToCall()) ||
                KNSConstants.MAINTENANCE_COPY_METHOD_TO_CALL.equals(orr.getMethodToCall())) &&
                (StringUtils.isNotEmpty(orr.getODelMId()) || StringUtils.isNotEmpty(orr.getORMId()))){
            Map<String, String> criteria;
            if(StringUtils.isNotEmpty(orr.getODelMId()) && !orr.isCreateDelegation()){
                DelegateMemberCompleteInfo delegationMember = getRoleManagementService().getDelegationMemberById(orr.getODelMId());
                DelegateTypeInfo delegation = getRoleManagementService().getDelegateTypeInfoById(delegationMember.getDelegationId());
                KimRoleInfo roleInfo = getRoleManagementService().getRole(delegation.getRoleId());
                KimTypeInfo typeInfo = getTypeInfoService().getKimType(roleInfo.getKimTypeId());
                List<KfsKimDocumentAttributeData> attributes = new ArrayList<KfsKimDocumentAttributeData>();
                KfsKimDocumentAttributeData attribute;
                orr.setDelegationMemberId(delegationMember.getMemberId());
                orr.setRoleMemberId(delegationMember.getRoleMemberId());
                orr.setAttributes(orr.getAttributeSetAsQualifierList(typeInfo, delegationMember.getQualifier()));
                //TODO: figure out how to get these pieces of information
                orr.setRoleId(delegation.getRoleId());
                orr.setDelegationTypeCode(delegationMember.getDelegationTypeCode());
                orr.setRoleDocumentDelegationMember(delegationMember);
                //orr.setActiveFromDate(delegationMember.getActiveFromDate());
                //orr.setActiveToDate(delegationMember.getActiveToDate());
                orr.setDelegate(true);
                orr.setODelMId("");
            } else if(StringUtils.isNotEmpty(orr.getORMId())){
                criteria = new HashMap<String, String>();
                criteria.put(KimConstants.PrimaryKeyConstants.ROLE_MEMBER_ID, orr.getORMId());
                List<RoleMemberCompleteInfo> roleMembers = (List<RoleMemberCompleteInfo>)getRoleManagementService().findRoleMembersCompleteInfo(criteria);
                RoleMemberCompleteInfo roleMember = new RoleMemberCompleteInfo();
                if(roleMembers!=null && roleMembers.size()>0){
                    roleMember = roleMembers.get(0);
                }
                orr.setRoleMemberId(roleMember.getRoleMemberId());
                /*orr.setRoleMemberRoleId(roleMember.getMemberId());
                orr.setRoleMemberRoleName(roleMember.getMemberName());
                orr.setRoleMemberRoleNamespaceCode(roleMember.getMemberNamespaceCode());*/

                KimRoleInfo roleInfo = getRoleManagementService().getRole(roleMember.getRoleId());
                KimTypeInfo typeInfo = getTypeInfoService().getKimType(roleInfo.getKimTypeId());
                List<KfsKimDocumentAttributeData> attributes = orr.getAttributeSetAsQualifierList(typeInfo, roleMember.getQualifier());
                orr.setAttributes(attributes);
                orr.setRoleRspActions(getRoleRspActions(roleMember.getRoleMemberId()));
                orr.setRoleId(roleMember.getRoleId());
                //TODO: figure out how to get dates?
                orr.setActiveFromDate(roleMember.getActiveFromDate());
                orr.setActiveToDate(roleMember.getActiveToDate());
                if(orr.isCreateDelegation())
                    orr.setDelegate(true);
                else{
                    orr.setDelegate(false);
                    orr.setKimDocumentRoleMember(roleMember);
                }
                orr.setORMId("");
            }
            Role role = orr.getRole(orr.getRoleId());
            //Set the role details
            orr.setRoleName(role.getRoleName());
            orr.setNamespaceCode(role.getNamespaceCode());
            
            orr.setChartOfAccountsCode(orr.getAttributeValue(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE));
            orr.setOrganizationCode(orr.getAttributeValue(KfsKimAttributes.ORGANIZATION_CODE));
            orr.setOverrideCode(orr.getAttributeValue(KfsKimAttributes.ACCOUNTING_LINE_OVERRIDE_CODE));
            orr.setFromAmount(orr.getAttributeValue(KfsKimAttributes.FROM_AMOUNT));
            orr.setToAmount(orr.getAttributeValue(KfsKimAttributes.TO_AMOUNT));
            orr.setFinancialSystemDocumentTypeCode(orr.getAttributeValue(KfsKimAttributes.DOCUMENT_TYPE_NAME));
            
            orr.getChart().setChartOfAccountsCode(orr.getChartOfAccountsCode());
            orr.getOrganization().setOrganizationCode(orr.getOrganizationCode());
            //orr.setReviewRolesIndicator
            //orr.setFromAmount()
            //toAmount
            //orr.getOverrideCode()()
            if(!orr.isCreateDelegation()){
                if(orr.getPerson()!=null){
                    orr.setPrincipalMemberPrincipalId(orr.getPerson().getPrincipalId());
                    orr.setPrincipalMemberPrincipalName(orr.getPerson().getPrincipalName());
                }
                if(orr.getRole()!=null){
                    orr.setRoleMemberRoleId(orr.getRole().getRoleId());
                    orr.setRoleMemberRoleNamespaceCode(orr.getRole().getNamespaceCode());
                    orr.setRoleMemberRoleName(orr.getRole().getRoleName());
                }
                if(orr.getGroup()!=null){
                    orr.setGroupMemberGroupId(orr.getGroup().getGroupId());
                    orr.setGroupMemberGroupNamespaceCode(orr.getGroup().getNamespaceCode());
                    orr.setGroupMemberGroupName(orr.getGroup().getGroupName());
                }
            }
            if(orr.getRoleRspActions()!=null && orr.getRoleRspActions().size()>0){
                orr.setActionTypeCode(orr.getRoleRspActions().get(0).getActionTypeCode());
                orr.setPriorityNumber(orr.getRoleRspActions().get(0).getPriorityNumber()==null?"":orr.getRoleRspActions().get(0).getPriorityNumber()+"");
                orr.setActionPolicyCode(orr.getRoleRspActions().get(0).getActionPolicyCode());
                orr.setForceAction(orr.getRoleRspActions().get(0).isForceAction());
            }
            //orr.setReviewRolesIndicator(orr.getReviewRolesIndicator());
        }
        super.setBusinessObject(orr);
    }
    
    public List<RoleResponsibilityActionInfo> getRoleRspActions(String roleMemberId){
        return getRoleManagementService().getRoleMemberResponsibilityActionInfo(roleMemberId);
    }
    
    @Override
    public void processAfterEdit(MaintenanceDocument document, Map<String,String[]> parameters){
        super.processAfterEdit(document, parameters);
        OrgReviewRole orr = (OrgReviewRole)document.getOldMaintainableObject().getBusinessObject();
        orr.setEdit(true);
    }

    @Override
    public void processAfterCopy(MaintenanceDocument document, Map<String,String[]> parameters){
        //super.processAfterCopy(document, parameters);
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
                OrgReviewRole.GROUP_NAME_FIELD_NAMESPACE_CODE.equals(field.getPropertyName())){
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
                OrgReviewRole.REVIEW_ROLES_INDICATOR_FIELD_NAME.equals(field.getPropertyName())){
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
        boolean isFSTransDoc = orr.getFinancialSystemDocumentTypeCode().equals(KFSConstants.COAConstants.FINANCIAL_SYSTEM_TRANSACTIONAL_DOCUMENT) || KFSConstants.COAConstants.FINANCIAL_SYSTEM_TRANSACTIONAL_DOCUMENT.equals(closestOrgReviewRoleParentDocumentTypeName);
        hasAccountingOrganizationHierarchy = orrLHSI.hasAccountingOrganizationHierarchy(orr.getFinancialSystemDocumentTypeCode()) || isFSTransDoc;
        return shouldReviewTypesFieldBeReadOnly = isFSTransDoc || hasOrganizationHierarchy || hasAccountingOrganizationHierarchy || 
          (StringUtils.isNotEmpty(closestOrgReviewRoleParentDocumentTypeName) && closestOrgReviewRoleParentDocumentTypeName.equals(KFSConstants.COAConstants.FINANCIAL_SYSTEM_COMPLEX_MAINTENANCE_DOCUMENT));
    }

    /***
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#refresh(java.lang.String, java.util.Map, org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    public void refresh(String refreshCaller, Map fieldValues, MaintenanceDocument document){
        super.refresh(refreshCaller, fieldValues, document);
        /*OrgReviewRole orr = (OrgReviewRole)document.getNewMaintainableObject().getBusinessObject();
        OrgReviewRoleLookupableHelperServiceImpl orrLHSI = new OrgReviewRoleLookupableHelperServiceImpl();
        orr.setRoleNamesToConsider(orrLHSI.getRolesToConsider(orr.getFinancialSystemDocumentTypeCode()));*/
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
            List<DelegateTypeInfo> objectsToSave = getDelegations(orr);
            if(objectsToSave!=null){
                for(DelegateTypeInfo delegateInfo: objectsToSave){
                    for(DelegateMemberCompleteInfo delegateMemberInfo: delegateInfo.getMembers()){
                        getRoleManagementService().saveDelegationMemberForRole(delegateMemberInfo.getDelegationMemberId(),
                            delegateMemberInfo.getRoleMemberId(), delegateMemberInfo.getMemberId(), 
                            delegateMemberInfo.getMemberTypeCode(), delegateMemberInfo.getDelegationTypeCode(), 
                            delegateInfo.getRoleId(), delegateMemberInfo.getQualifier(), 
                            delegateMemberInfo.getActiveFromDate(), delegateMemberInfo.getActiveToDate());
                    }
                }
            }
        } else{
            // Save role member(s)
            List<RoleMemberCompleteInfo> objectsToSave = getRoleMembers(orr);
            RoleMemberCompleteInfo savedObject;
            if(objectsToSave!=null){
                for(RoleMemberCompleteInfo roleMember: objectsToSave){
                    savedObject = getRoleManagementService().saveRoleMemberForRole(roleMember.getRoleMemberId(),
                            roleMember.getMemberId(), roleMember.getMemberTypeCode(), roleMember.getRoleId(), 
                            roleMember.getQualifier(), roleMember.getActiveFromDate(), roleMember.getActiveToDate());
                    List<RoleResponsibilityActionInfo> roleRspActionsToSave = getRoleRspActions(orr, roleMember);
                    if(roleRspActionsToSave!=null){
                        for(RoleResponsibilityActionInfo rspActionInfo: roleRspActionsToSave){
                            getRoleManagementService().saveRoleRspActions(
                                    rspActionInfo.getRoleResponsibilityActionId(), roleMember.getRoleId(), 
                                    rspActionInfo.getRoleResponsibilityId(), savedObject.getRoleMemberId(), 
                                    rspActionInfo.getActionTypeCode(), rspActionInfo.getActionPolicyCode(), 
                                    rspActionInfo.getPriorityNumber(), new Boolean(rspActionInfo.isForceAction()));
                        }
                    }
                }
            }
        }
        //System.out.println("Save Org Review Role here!"+businessObject);
        //for(PersistableBusinessObject objectToSave: objectsToSave)
            //getBusinessObjectService().linkAndSave(objectToSave);
    }
    
    protected List<DelegateTypeInfo> getDelegations(OrgReviewRole orr){
        List<DelegateMemberCompleteInfo> delegationMembers = new ArrayList<DelegateMemberCompleteInfo>();
        List<String> roleNamesToSaveFor = getRolesToSaveFor(orr.getRoleNamesToConsider(), orr.getReviewRolesIndicator());
        List<DelegateTypeInfo> roleDelegations = new ArrayList<DelegateTypeInfo>();
        DelegateTypeInfo roleDelegation;
        KimRoleInfo roleInfo;
        if(roleNamesToSaveFor!=null){
            for(String roleName: roleNamesToSaveFor){
                roleDelegation = new DelegateTypeInfo();
                roleInfo = getRoleManagementService().getRoleByName(
                        KFSConstants.SysKimConstants.ORGANIZATION_REVIEWER_ROLE_NAMESPACECODE, roleName);
                roleDelegation.setRoleId(roleInfo.getRoleId());
                orr.setKimTypeId(roleInfo.getKimTypeId());
                delegationMembers.addAll(getDelegationMembersToSave(orr));
                roleDelegation.setMembers(delegationMembers);
                roleDelegations.add(roleDelegation);
            }
        }
        return roleDelegations;
    }

    private List<DelegateMemberCompleteInfo> getDelegationMembersToSave(OrgReviewRole orr){
        List<DelegateMemberCompleteInfo> objectsToSave = new ArrayList<DelegateMemberCompleteInfo>();
        String memberId;
        DelegateMemberCompleteInfo delegationMember = null;
        DelegateInfo origDelegationMember = null;
        Map<String, Object> criteria;
        /*if(!orr.isCopy() && !orr.isCreateDelegation() && StringUtils.isNotEmpty(orr.getDelegationMemberId())){
            criteria = new HashMap<String, Object>();
            criteria.put(KimConstants.PrimaryKeyConstants.DELEGATION_MEMBER_ID, orr.getDelegationMemberId());
            delegationMember = (DelegateMemberCompleteInfo)getBusinessObjectService().findByPrimaryKey(DelegateMemberCompleteInfo.class, criteria);
        }*/
        if(orr.isEdit() && !orr.isCreateDelegation()){
            origDelegationMember = (DelegateInfo)getRoleManagementService().getDelegationMemberById(orr.getDelegationMemberId());
        }
        if(StringUtils.isNotEmpty(orr.getRoleMemberRoleNamespaceCode()) && StringUtils.isNotEmpty(orr.getRoleMemberRoleName())){
            if(delegationMember==null){
                memberId = getRoleManagementService().getRoleIdByName(orr.getRoleMemberRoleNamespaceCode(), orr.getRoleMemberRoleName());
                delegationMember = new DelegateMemberCompleteInfo();
                delegationMember.setMemberId(memberId);
            }
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
                GroupInfo groupInfo = getGroupService().getGroupInfoByName(orr.getGroupMemberGroupNamespaceCode(), orr.getGroupMemberGroupName());
                memberId = groupInfo.getGroupId();
                delegationMember = new DelegateMemberCompleteInfo();
                delegationMember.setMemberId(memberId);
            }
            delegationMember.setMemberTypeCode(KimConstants.KimUIConstants.MEMBER_TYPE_GROUP_CODE);
            //delegationMember.setDelegationId(delegation.getDelegationId());
            if(orr.isEdit() && !orr.isCreateDelegation()){
                delegationMember.setDelegationMemberId(orr.getDelegationMemberId());
                //TODO: This should be taken care of in Role Service
                //delegationMember.setVersionNumber(origDelegationMember.getVersionNumber());
            }
            /*if(StringUtils.isEmpty(delegationMember.getDelegationMemberId()))
                delegationMember.setDelegationMemberId(getDelegationMemberId());*/
            //objectsToSave.addAll(getRoleRspActions(orr, delegationMember));
            delegationMember.setQualifier(getAttributes(orr, orr.getKimTypeId()));
            delegationMember.setActiveFromDate(orr.getActiveFromDate());
            delegationMember.setActiveToDate(orr.getActiveToDate());
            delegationMember.setRoleMemberId(orr.getRoleMemberId());
            objectsToSave.add(delegationMember);
            delegationMember = null;
        }
        if(StringUtils.isNotEmpty(orr.getPrincipalMemberPrincipalName())){
            if(delegationMember==null){
                KimPrincipal principal = getIdentityManagementService().getPrincipalByPrincipalName(orr.getPrincipalMemberPrincipalName());
                delegationMember = new DelegateMemberCompleteInfo();
                delegationMember.setMemberId(principal.getPrincipalId());
            }
            delegationMember.setMemberTypeCode(KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL_CODE);
            //TODO: This should be taken care of in Role Service
            //delegationMember.setDelegationId(delegation.getDelegationId());
            if(orr.isEdit() && !orr.isCreateDelegation()){
                delegationMember.setDelegationMemberId(orr.getDelegationMemberId());
                //TODO: This should be taken care of in Role Service
                //delegationMember.setVersionNumber(origDelegationMember.getVersionNumber());
            }
            //TODO: This should be taken care of in Role Service
            /*if(StringUtils.isEmpty(delegationMember.getDelegationMemberId()))
                delegationMember.setDelegationMemberId(getDelegationMemberId());
            */
            //objectsToSave.addAll(getRoleRspActions(orr, delegationMember));
            //TODO: Check if ORR has kim type id populated here
            delegationMember.setQualifier(getAttributes(orr, orr.getKimTypeId()));
            delegationMember.setActiveFromDate(orr.getActiveFromDate());
            delegationMember.setActiveToDate(orr.getActiveToDate());
            delegationMember.setRoleMemberId(orr.getRoleMemberId());
            objectsToSave.add(delegationMember);
            delegationMember = null;
        }
        return objectsToSave;
    }
    
    private List<RoleMemberCompleteInfo> getRoleMembersToSave(KimRoleInfo roleInfo, OrgReviewRole orr){
        List<RoleMemberCompleteInfo> objectsToSave = new ArrayList<RoleMemberCompleteInfo>();
        String memberId;
        RoleMemberCompleteInfo roleMember = null;
        if(StringUtils.isNotEmpty(orr.getRoleMemberRoleNamespaceCode()) && StringUtils.isNotEmpty(orr.getRoleMemberRoleName())){
            if(roleMember==null){
                memberId = getRoleManagementService().getRoleIdByName(orr.getRoleMemberRoleNamespaceCode(), orr.getRoleMemberRoleName());
                roleMember = new RoleMemberCompleteInfo();
                roleMember.setRoleId(roleInfo.getRoleId());
                roleMember.setMemberId(memberId);
                roleMember.setMemberTypeCode(KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE);
                roleMember.setMemberId(memberId);
            }                
            if(orr.isEdit()){
                roleMember.setRoleMemberId(orr.getRoleMemberId());
                //This should be taken care of in the role service impl
                //roleMember.setVersionNumber(origRoleMember.getVersionNumber());
            }
            //TODO: This should be taken care of in Role Service
            /*if(StringUtils.isEmpty(roleMember.getRoleMemberId()))
                roleMember.setRoleMemberId(getRoleMemberId());*/
            roleMember.setQualifier(getAttributes(orr, roleMember, roleInfo.getKimTypeId()));
            roleMember.setActiveFromDate(orr.getActiveFromDate());
            roleMember.setActiveToDate(orr.getActiveToDate());
            objectsToSave.add(roleMember);
            roleMember = null;
        }
        if(StringUtils.isNotEmpty(orr.getGroupMemberGroupNamespaceCode()) && StringUtils.isNotEmpty(orr.getGroupMemberGroupName())){
            if(roleMember==null){
                GroupInfo groupInfo = getGroupService().getGroupInfoByName(orr.getGroupMemberGroupNamespaceCode(), orr.getGroupMemberGroupName());
                memberId = groupInfo.getGroupId();
                roleMember = new RoleMemberCompleteInfo();
                roleMember.setMemberId(memberId);
            }
            roleMember.setMemberTypeCode(KimConstants.KimUIConstants.MEMBER_TYPE_GROUP_CODE);
            roleMember.setRoleId(roleInfo.getRoleId());
            if(orr.isEdit()){
                roleMember.setRoleMemberId(orr.getRoleMemberId());
//              //TODO: This should be taken care of in Role Service
                //roleMember.setVersionNumber(origRoleMember.getVersionNumber());
            }
//          //TODO: This should be taken care of in Role Service
            /*if(StringUtils.isEmpty(roleMember.getRoleMemberId()))
                roleMember.setRoleMemberId(getRoleMemberId());
            */
            List<RoleResponsibilityActionInfo> roleRspActionObjectsToSave = new ArrayList<RoleResponsibilityActionInfo>();
            roleRspActionObjectsToSave.addAll(getRoleRspActions(orr, roleMember));
            roleMember.setQualifier(getAttributes(orr, roleMember, roleInfo.getKimTypeId()));
            roleMember.setActiveFromDate(orr.getActiveFromDate());
            roleMember.setActiveToDate(orr.getActiveToDate());
            objectsToSave.add(roleMember);
            roleMember = null;
        }
        if(StringUtils.isNotEmpty(orr.getPrincipalMemberPrincipalName())){
            if(roleMember==null){
                KimPrincipal principal = getIdentityManagementService().getPrincipalByPrincipalName(orr.getPrincipalMemberPrincipalName());
                roleMember = new RoleMemberCompleteInfo();
                roleMember.setMemberId(principal.getPrincipalId());
            }
            roleMember.setMemberTypeCode(KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL_CODE);
            roleMember.setRoleId(roleInfo.getRoleId());
            if(orr.isEdit()){
                roleMember.setRoleMemberId(orr.getRoleMemberId());
//              TODO: This should be taken care of in Role Service
                //roleMember.setVersionNumber(origRoleMember.getVersionNumber());
            }
//          TODO: This should be taken care of in Role Service
            /*if(StringUtils.isEmpty(roleMember.getRoleMemberId()))
                roleMember.setRoleMemberId(getRoleMemberId());
            */
            List<RoleResponsibilityActionInfo> roleRspActionObjectsToSave = new ArrayList<RoleResponsibilityActionInfo>();
            roleRspActionObjectsToSave.addAll(getRoleRspActions(orr, roleMember));
            roleMember.setQualifier(getAttributes(orr, roleMember, roleInfo.getKimTypeId()));
            roleMember.setActiveFromDate(orr.getActiveFromDate());
            roleMember.setActiveToDate(orr.getActiveToDate());
            objectsToSave.add(roleMember);
            roleMember = null;
        }
        return objectsToSave;
    }
    
    private List<String> getRolesToSaveFor(List<String> roleNamesToConsider, String reviewRolesIndicator){
        List<String> roleToSaveFor = new ArrayList<String>();
        if(roleNamesToConsider!=null){
            if(KFSConstants.COAConstants.ORG_REVIEW_ROLE_ORG_ACC_ONLY_CODE.equals(reviewRolesIndicator)){
                roleToSaveFor.add(KFSConstants.SysKimConstants.ACCOUNTING_REVIEWER_ROLE_NAME);
            } else if(KFSConstants.COAConstants.ORG_REVIEW_ROLE_ORG_ONLY_CODE.equals(reviewRolesIndicator)){
                roleToSaveFor.add(KFSConstants.SysKimConstants.ORGANIZATION_REVIEWER_ROLE_NAME);
            } else{
                roleToSaveFor.addAll(roleNamesToConsider);
            }
        }
        return roleToSaveFor;
    }
    
    protected List<RoleMemberCompleteInfo> getRoleMembers(OrgReviewRole orr){
        List<RoleMemberCompleteInfo> objectsToSave = new ArrayList<RoleMemberCompleteInfo>();
        List<String> roleNamesToSaveFor = getRolesToSaveFor(orr.getRoleNamesToConsider(), orr.getReviewRolesIndicator());
        String roleId;
        String memberId;
        RoleMemberCompleteInfo roleMember = null;
        KimRoleInfo roleInfo;
        Map<String, Object> criteria;
        if(roleNamesToSaveFor!=null){
            for(String roleName: roleNamesToSaveFor){
                roleId = getRoleManagementService().getRoleIdByName(
                        KFSConstants.SysKimConstants.ORGANIZATION_REVIEWER_ROLE_NAMESPACECODE, roleName);
                roleInfo = getRoleManagementService().getRole(roleId);
                objectsToSave.addAll(getRoleMembersToSave(roleInfo, orr));
            }
        }
        return objectsToSave;
    }
    
    public String getKimAttributeDefnId(AttributeDefinition definition){
        if (definition instanceof KimDataDictionaryAttributeDefinition) {
            return ((KimDataDictionaryAttributeDefinition)definition).getKimAttrDefnId();
        } else {
            return ((KimNonDataDictionaryAttributeDefinition)definition).getKimAttrDefnId();
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
    protected AttributeSet getAttributes(
            OrgReviewRole orr, RoleMemberCompleteInfo roleMember, String kimTypeId){
        KimTypeInfo kimType = getTypeInfoService().getKimType(kimTypeId);
        KimTypeService typeService = KimCommonUtils.getKimTypeService(kimType);
        AttributeDefinitionMap attributeDefinitions = typeService.getAttributeDefinitions(kimTypeId);
        List<KfsKimDocumentAttributeData> attributeDataList = new ArrayList<KfsKimDocumentAttributeData>();
        KfsKimDocumentAttributeData attributeData = new KfsKimDocumentAttributeData();
        //chart code
        String attributeDefnId = getKimAttributeDefnId(attributeDefinitions, KfsKimAttributes.CHART_OF_ACCOUNTS_CODE);
        if(StringUtils.isNotEmpty(attributeDefnId) && orr.getFinancialSystemDocumentTypeCode()!=null){
            //TODO: this should be taken care of in Role Service
            //attributeData.setAttrDataId(getRoleMemberAttributeDataId());
            attributeData.setKimTypId(kimTypeId);
            attributeData.setAttrVal(orr.getChartOfAccountsCode());
            attributeData.setKimAttrDefnId(attributeDefnId);
            attributeData.setKimAttribute(kimType.getAttributeDefinition(attributeDefnId));
            attributeDataList.add(attributeData);
        }
        
        //org code
        attributeDefnId = getKimAttributeDefnId(attributeDefinitions, KfsKimAttributes.ORGANIZATION_CODE);
        if(StringUtils.isNotEmpty(attributeDefnId) && orr.getFinancialSystemDocumentTypeCode()!=null){
            attributeData = new KfsKimDocumentAttributeData();
            attributeData.setKimTypId(kimTypeId);
            //TODO: this should be taken care of in Role Service
            //attributeData.setAttrDataId(getRoleMemberAttributeDataId());
            attributeData.setAttrVal(orr.getOrganizationCode());
            attributeData.setKimAttrDefnId(attributeDefnId);
            attributeData.setKimAttribute(kimType.getAttributeDefinition(attributeDefnId));
            attributeDataList.add(attributeData);
        }
        
        //document type
        attributeDefnId = getKimAttributeDefnId(attributeDefinitions, KfsKimAttributes.DOCUMENT_TYPE_NAME);
        if(StringUtils.isNotEmpty(attributeDefnId) && orr.getFinancialSystemDocumentTypeCode()!=null){
            attributeData = new KfsKimDocumentAttributeData();
            //TODO: this should be taken care of in Role Service
            //attributeData.setAttrDataId(getRoleMemberAttributeDataId());
            attributeData.setKimTypId(kimTypeId);
            attributeData.setAttrVal(orr.getFinancialSystemDocumentTypeCode());
            attributeData.setKimAttrDefnId(attributeDefnId);
            attributeData.setKimAttribute(kimType.getAttributeDefinition(attributeDefnId));
            attributeDataList.add(attributeData);
        }

        //override code
        attributeDefnId = getKimAttributeDefnId(attributeDefinitions, KfsKimAttributes.ACCOUNTING_LINE_OVERRIDE_CODE);
        if(StringUtils.isNotEmpty(attributeDefnId) && orr.getOverrideCode()!=null){
            attributeData = new KfsKimDocumentAttributeData();
            //TODO: this should be taken care of in Role Service
            //attributeData.setAttrDataId(getRoleMemberAttributeDataId());
            attributeData.setKimTypId(kimTypeId);
            attributeData.setAttrVal(orr.getOverrideCode());
            attributeData.setKimAttrDefnId(attributeDefnId);
            attributeData.setKimAttribute(kimType.getAttributeDefinition(attributeDefnId));
            attributeDataList.add(attributeData);
        }
        
        //from amount
        attributeDefnId = getKimAttributeDefnId(attributeDefinitions, KfsKimAttributes.FROM_AMOUNT);
        if(StringUtils.isNotEmpty(attributeDefnId)){
            attributeData = new KfsKimDocumentAttributeData();
            //TODO: this should be taken care of in Role Service
            //attributeData.setAttrDataId(getRoleMemberAttributeDataId());
            attributeData.setKimTypId(kimTypeId);
            attributeData.setAttrVal(orr.getFromAmountStr());
            attributeData.setKimAttrDefnId(attributeDefnId);
            attributeData.setKimAttribute(kimType.getAttributeDefinition(attributeDefnId));
            attributeDataList.add(attributeData);
        }
        
        //to amount
        attributeDefnId = getKimAttributeDefnId(attributeDefinitions, KfsKimAttributes.TO_AMOUNT);
        if(StringUtils.isNotEmpty(attributeDefnId)){
            attributeData = new KfsKimDocumentAttributeData();
            //TODO: this should be taken care of in Role Service
            //attributeData.setAttrDataId(getRoleMemberAttributeDataId());
            attributeData.setKimTypId(kimTypeId);
            attributeData.setAttrVal(orr.getToAmountStr());
            attributeData.setKimAttrDefnId(attributeDefnId);
            attributeData.setKimAttribute(kimType.getAttributeDefinition(attributeDefnId));
            attributeDataList.add(attributeData);
        }
        
        return orr.getQualifierAsAttributeSet(attributeDataList);
    }
    
    protected AttributeSet getAttributes(OrgReviewRole orr, String kimTypeId){
        if(StringUtils.isEmpty(kimTypeId)) return null;
        
        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("kimTypeId", kimTypeId);
        KimTypeInfo kimType = getTypeInfoService().getKimType(kimTypeId);
        KimTypeService typeService = KimCommonUtils.getKimTypeService(kimType);
        AttributeDefinitionMap attributeDefinitions = typeService.getAttributeDefinitions(kimTypeId);
        List<KfsKimDocumentAttributeData> attributeDataList = new ArrayList<KfsKimDocumentAttributeData>();
        KfsKimDocumentAttributeData attributeData = new KfsKimDocumentAttributeData();
        String attributeDefnId = getKimAttributeDefnId(attributeDefinitions, KfsKimAttributes.CHART_OF_ACCOUNTS_CODE);
        if(StringUtils.isNotEmpty(attributeDefnId) && orr.getFinancialSystemDocumentTypeCode()!=null){
            //TODO: this should be taken care of in Role Service
            //attributeData.setAttrDataId(getRoleMemberAttributeDataId());
            attributeData.setKimTypId(kimTypeId);
            attributeData.setAttrVal(orr.getChartOfAccountsCode());
            attributeData.setKimAttrDefnId(attributeDefnId);
            attributeData.setKimAttribute(kimType.getAttributeDefinition(attributeDefnId));
            attributeDataList.add(attributeData);
        }
        
        //org code
        attributeDefnId = getKimAttributeDefnId(attributeDefinitions, KfsKimAttributes.ORGANIZATION_CODE);
        if(StringUtils.isNotEmpty(attributeDefnId) && orr.getFinancialSystemDocumentTypeCode()!=null){
            attributeData = new KfsKimDocumentAttributeData();
            attributeData.setKimTypId(kimTypeId);
            //TODO: this should be taken care of in Role Service
            //attributeData.setAttrDataId(getRoleMemberAttributeDataId());
            attributeData.setAttrVal(orr.getOrganizationCode());
            attributeData.setKimAttrDefnId(attributeDefnId);
            attributeData.setKimAttribute(kimType.getAttributeDefinition(attributeDefnId));
            attributeDataList.add(attributeData);
        }

        //document type
        attributeDefnId = getKimAttributeDefnId(attributeDefinitions, KfsKimAttributes.DOCUMENT_TYPE_NAME);
        if(StringUtils.isNotEmpty(attributeDefnId) && orr.getFinancialSystemDocumentTypeCode()!=null){
            attributeData = new KfsKimDocumentAttributeData();
            //TODO: this should be taken care of in Role Service
            //attributeData.setAttrDataId(getRoleMemberAttributeDataId());
            attributeData.setKimTypId(kimTypeId);
            attributeData.setAttrVal(orr.getFinancialSystemDocumentTypeCode());
            attributeData.setKimAttrDefnId(attributeDefnId);
            attributeData.setKimAttribute(kimType.getAttributeDefinition(attributeDefnId));
            attributeDataList.add(attributeData);
        }

        //override code
        attributeDefnId = getKimAttributeDefnId(attributeDefinitions, KfsKimAttributes.ACCOUNTING_LINE_OVERRIDE_CODE);
        if(StringUtils.isNotEmpty(attributeDefnId) && orr.getOverrideCode()!=null){
            attributeData = new KfsKimDocumentAttributeData();
            //TODO: this should be taken care of in Role Service
            //attributeData.setAttrDataId(getRoleMemberAttributeDataId());
            attributeData.setKimTypId(kimTypeId);
            attributeData.setAttrVal(orr.getOverrideCode());
            attributeData.setKimAttrDefnId(attributeDefnId);
            attributeData.setKimAttribute(kimType.getAttributeDefinition(attributeDefnId));
            attributeDataList.add(attributeData);
        }
        
        //from amount
        attributeDefnId = getKimAttributeDefnId(attributeDefinitions, KfsKimAttributes.FROM_AMOUNT);
        if(StringUtils.isNotEmpty(attributeDefnId) && orr.getFromAmount()!=null){
            attributeData = new KfsKimDocumentAttributeData();
            //TODO: this should be taken care of in Role Service
            //attributeData.setAttrDataId(getRoleMemberAttributeDataId());
            attributeData.setKimTypId(kimTypeId);
            attributeData.setAttrVal(orr.getFromAmountStr());
            attributeData.setKimAttrDefnId(attributeDefnId);
            attributeData.setKimAttribute(kimType.getAttributeDefinition(attributeDefnId));
            attributeDataList.add(attributeData);
        }
        
        //to amount
        attributeDefnId = getKimAttributeDefnId(attributeDefinitions, KfsKimAttributes.TO_AMOUNT);
        if(StringUtils.isNotEmpty(attributeDefnId) && orr.getToAmount()!=null){
            attributeData = new KfsKimDocumentAttributeData();
            //TODO: this should be taken care of in Role Service
            //attributeData.setAttrDataId(getRoleMemberAttributeDataId());
            attributeData.setKimTypId(kimTypeId);
            attributeData.setAttrVal(orr.getToAmountStr());
            attributeData.setKimAttrDefnId(attributeDefnId);
            attributeData.setKimAttribute(kimType.getAttributeDefinition(attributeDefnId));
            attributeDataList.add(attributeData);
        }
        
        return orr.getQualifierAsAttributeSet(attributeDataList);
    }
    
    protected List<RoleResponsibilityActionInfo> getRoleRspActions(OrgReviewRole orr, RoleMemberCompleteInfo roleMember){
        List<RoleResponsibilityActionInfo> roleRspActions = new ArrayList<RoleResponsibilityActionInfo>();
        RoleResponsibilityActionInfo roleRspAction;
        //Assuming that there is only one responsibility for an org role
        //Get it now given the role id
        List<RoleResponsibilityInfo> roleResponsibilityInfos = ((List<RoleResponsibilityInfo>)getRoleManagementService().getRoleResponsibilities(roleMember.getRoleId()));
        //Assuming that there is only 1 responsibility for both the org review roles
        if(roleResponsibilityInfos!=null && roleResponsibilityInfos.size()<1)
            throw new KualiException("The Org Review Role id:"+roleMember.getRoleId()+" does not have any responsibility associated with it");

        List<RoleResponsibilityActionInfo> origRoleRspActions = ((List<RoleResponsibilityActionInfo>)getRoleManagementService().getRoleMemberResponsibilityActionInfo(roleMember.getRoleMemberId()));
        roleRspAction = new RoleResponsibilityActionInfo();
        if(origRoleRspActions!=null && origRoleRspActions.size()>0){
            RoleResponsibilityActionInfo origActionInfo = origRoleRspActions.get(0);
            roleRspAction.setRoleResponsibilityActionId(origActionInfo.getRoleResponsibilityActionId());
        } else {
            //TODO: This should be taken care of in Role Service
            //roleRspAction.setRoleResponsibilityActionId(getRoleRspActionId());
        }
        roleRspAction.setRoleMemberId(roleMember.getRoleMemberId());
        RoleResponsibilityInfo roleResponsibilityInfo = roleResponsibilityInfos.get(0);
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

    protected RoleManagementService getRoleManagementService(){
        if(roleManagementService==null){
            roleManagementService = KIMServiceLocator.getRoleManagementService();
        }
        return roleManagementService;
    }

    protected GroupService getGroupService(){
        if(groupService==null){
            groupService = KIMServiceLocator.getGroupService();
        }
        return groupService;
    }

    protected IdentityManagementService getIdentityManagementService(){
        if(identityManagementService==null){
            identityManagementService = KIMServiceLocator.getIdentityManagementService();
        }
        return identityManagementService;
    }

    protected KimTypeInfoService getTypeInfoService(){
        if(typeInfoService==null){
            typeInfoService = KIMServiceLocator.getTypeInfoService();
        }
        return typeInfoService;
    }
    
    private DelegateInfo getDelegateMemberFromList(List<DelegateInfo> delegateMembers, String memberId, String memberTypeCode){
        if(StringUtils.isEmpty(memberId) || StringUtils.isEmpty(memberTypeCode))
            return null;
        if(delegateMembers!=null){
            for(DelegateInfo info: delegateMembers){
                if(StringUtils.equals(info.getMemberId(), memberId) || 
                        StringUtils.equals(info.getMemberTypeCode(), memberTypeCode)){
                    return info;
                }
            }
        }
        return null;
    }

}