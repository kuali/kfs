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
import org.kuali.kfs.coa.identity.OrgReviewRole;
import org.kuali.kfs.coa.identity.OrgReviewRoleLookupableHelperServiceImpl;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.entity.KimPrincipal;
import org.kuali.rice.kim.bo.group.dto.GroupInfo;
import org.kuali.rice.kim.bo.role.dto.KimRoleInfo;
import org.kuali.rice.kim.bo.role.impl.KimDelegationImpl;
import org.kuali.rice.kim.bo.role.impl.KimDelegationMemberAttributeDataImpl;
import org.kuali.rice.kim.bo.role.impl.KimDelegationMemberImpl;
import org.kuali.rice.kim.bo.role.impl.RoleMemberAttributeDataImpl;
import org.kuali.rice.kim.bo.role.impl.RoleMemberImpl;
import org.kuali.rice.kim.bo.role.impl.RoleResponsibilityActionImpl;
import org.kuali.rice.kim.bo.role.impl.RoleResponsibilityImpl;
import org.kuali.rice.kim.bo.types.dto.AttributeDefinitionMap;
import org.kuali.rice.kim.bo.types.dto.KimTypeInfo;
import org.kuali.rice.kim.bo.types.impl.KimAttributeDataImpl;
import org.kuali.rice.kim.bo.types.impl.KimTypeImpl;
import org.kuali.rice.kim.bo.ui.KimDocumentRoleMember;
import org.kuali.rice.kim.bo.ui.RoleDocumentDelegationMember;
import org.kuali.rice.kim.service.GroupService;
import org.kuali.rice.kim.service.IdentityManagementService;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.KimTypeInfoService;
import org.kuali.rice.kim.service.RoleService;
import org.kuali.rice.kim.service.UiDocumentService;
import org.kuali.rice.kim.service.support.KimTypeService;
import org.kuali.rice.kim.util.KimCommonUtils;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.datadictionary.AttributeDefinition;
import org.kuali.rice.kns.datadictionary.KimDataDictionaryAttributeDefinition;
import org.kuali.rice.kns.datadictionary.KimNonDataDictionaryAttributeDefinition;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.document.MaintenanceLock;
import org.kuali.rice.kns.exception.KualiException;
import org.kuali.rice.kns.maintenance.Maintainable;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.SequenceAccessorService;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.kns.web.ui.Section;

import edu.emory.mathcs.backport.java.util.Collections;

public class OrgReviewRoleMaintainableImpl extends FinancialSystemMaintainable {

    private transient static SequenceAccessorService sequenceAccessorService;
    private transient static RoleService roleService;
    private transient static GroupService groupService;
    private transient static IdentityManagementService identityManagementService;
    private transient static UiDocumentService uiDocumentService;
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
                criteria = new HashMap<String, String>();
                criteria.put("delegationMemberId", orr.getODelMId());
                KimDelegationMemberImpl delegationMember = (KimDelegationMemberImpl)
                    KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(KimDelegationMemberImpl.class, criteria);

                criteria = new HashMap<String, String>();
                criteria.put("delegationId", delegationMember.getDelegationId());
                KimDelegationImpl delegation = (KimDelegationImpl)
                KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(KimDelegationImpl.class, criteria);

                List<KimAttributeDataImpl> attributes = new ArrayList<KimAttributeDataImpl>();
                KimAttributeDataImpl attribute;
                RoleDocumentDelegationMember member = getUIDocumentService().getRoleDocumentDelegationMember(
                        delegationMember.getMemberTypeCode(), delegationMember.getMemberId(), delegation.getRoleId(), delegation.getDelegationTypeCode());
                orr.setDelegationMemberId(delegationMember.getDelegationMemberId());
                orr.setRoleMemberId(delegationMember.getRoleMemberId());
                for(KimDelegationMemberAttributeDataImpl delegationMemberAttribute: (delegationMember).getAttributes()){
                    attribute = new KimAttributeDataImpl();
                    KimCommonUtils.copyProperties(attribute, delegationMemberAttribute);
                    attributes.add(attribute);
                }
                orr.setAttributes(attributes);
                orr.setRoleId(delegation.getRoleId());
                orr.setDelegationTypeCode(delegation.getDelegationTypeCode());
                orr.setRoleDocumentDelegationMember(member, delegationMember.getMemberTypeCode());
                orr.setActiveFromDate(delegationMember.getActiveFromDate());
                orr.setActiveToDate(delegationMember.getActiveToDate());
                orr.setDelegate(true);
                orr.setODelMId("");
            } else if(StringUtils.isNotEmpty(orr.getORMId())){
                criteria = new HashMap<String, String>();
                criteria.put("roleMemberId", orr.getORMId());
                RoleMemberImpl roleMember = (RoleMemberImpl)
                    KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(RoleMemberImpl.class, criteria);

                KimDocumentRoleMember member = getUIDocumentService().getKimDocumentRoleMember(
                        roleMember.getMemberTypeCode(), roleMember.getMemberId(), roleMember.getRoleId());
                orr.setRoleMemberId(roleMember.getRoleMemberId());
                /*orr.setRoleMemberRoleId(member.getMemberId());
                orr.setRoleMemberRoleName(member.getMemberName());
                orr.setRoleMemberRoleNamespaceCode(member.getMemberNamespaceCode());*/
                List<KimAttributeDataImpl> attributes = new ArrayList<KimAttributeDataImpl>();
                KimAttributeDataImpl attribute;
                for(RoleMemberAttributeDataImpl roleMemberAttribute: roleMember.getAttributes()){
                    attribute = new KimAttributeDataImpl();
                    KimCommonUtils.copyProperties(attribute, roleMemberAttribute);
                    attributes.add(attribute);
                }
                orr.setAttributes(attributes);
                orr.setRoleRspActions(getRoleRspActions(roleMember));
                orr.setRoleId(roleMember.getRoleId());
                orr.setActiveFromDate(roleMember.getActiveFromDate());
                orr.setActiveToDate(roleMember.getActiveToDate());
                if(orr.isCreateDelegation())
                    orr.setDelegate(true);
                else{
                    orr.setDelegate(false);
                    orr.setKimDocumentRoleMember(member, roleMember.getMemberTypeCode());
                }
                orr.setORMId("");
            }
            KimRoleInfo roleInfo = getRoleService().getRole(orr.getRoleId());
            //Set the role details
            orr.setRoleName(roleInfo.getRoleName());
            orr.setNamespaceCode(roleInfo.getNamespaceCode());
            
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
                orr.setPrincipalMemberPrincipalId(orr.getPerson().getPrincipalId());
                orr.setPrincipalMemberPrincipalName(orr.getPerson().getPrincipalName());
                orr.setRoleMemberRoleId(orr.getRole().getRoleId());
                orr.setRoleMemberRoleNamespaceCode(orr.getRole().getNamespaceCode());
                orr.setRoleMemberRoleName(orr.getRole().getRoleName());
                orr.setGroupMemberGroupId(orr.getGroup().getGroupId());
                orr.setGroupMemberGroupNamespaceCode(orr.getGroup().getNamespaceCode());
                orr.setGroupMemberGroupName(orr.getGroup().getGroupName());
            }
            orr.setActionTypeCode(orr.getRoleRspActions().get(0).getActionTypeCode());
            orr.setPriorityNumber(orr.getRoleRspActions().get(0).getPriorityNumber()==null?"":orr.getRoleRspActions().get(0).getPriorityNumber()+"");
            orr.setActionPolicyCode(orr.getRoleRspActions().get(0).getActionPolicyCode());
            orr.setForceAction(orr.getRoleRspActions().get(0).isForceAction());
            //orr.setReviewRolesIndicator(orr.getReviewRolesIndicator());
        }
        //super.setBusinessObject(orr);
    }
    
    private List getRoleRspActions(RoleMemberImpl roleMemberImpl){
        Map<String, String> criteria = new HashMap<String,String>(1);
        criteria.put(KimConstants.PrimaryKeyConstants.ROLE_ID, roleMemberImpl.getRoleId());
        List<RoleResponsibilityImpl> roleResponsibilities = ((List<RoleResponsibilityImpl>)getBusinessObjectService().findMatching(RoleResponsibilityImpl.class, criteria));
        //Assuming that there is only 1 responsibility for both the org review roles
        if(roleResponsibilities!=null && roleResponsibilities.size()>0){
            return getUIDocumentService().getRoleMemberResponsibilityActionImpls(roleMemberImpl.getRoleMemberId());
        }
        return null;
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
        List<PersistableBusinessObject> objectsToSave = new ArrayList<PersistableBusinessObject>();
        if(orr.isDelegate() || orr.isCreateDelegation()){
            // Save delegation(s)
            objectsToSave = getDelegations(orr);
        } else{
            // Save role member(s)
            objectsToSave = getRoleMembers(orr);
        }
        //System.out.println("Save Org Review Role here!"+businessObject);
        for(PersistableBusinessObject objectToSave: objectsToSave)
            getBusinessObjectService().linkAndSave(objectToSave);
    }
    
    protected List<PersistableBusinessObject> getDelegations(OrgReviewRole orr){
        List<PersistableBusinessObject> objectsToSave = new ArrayList<PersistableBusinessObject>();
        List<String> roleNamesToSaveFor = getRolesToSaveFor(orr.getRoleNamesToConsider(), orr.getReviewRolesIndicator());
        String roleId;
        KimDelegationImpl delegation = null;
        KimDelegationMemberImpl delegationMember;
        List<KimDelegationImpl> roleDelegations;
        KimRoleInfo roleInfo;
        for(String roleName: roleNamesToSaveFor){
            roleId = getRoleService().getRoleIdByName(
                    KFSConstants.SysKimConstants.ORGANIZATION_REVIEWER_ROLE_NAMESPACECODE, roleName);
            roleInfo = getRoleService().getRole(roleId);
            roleDelegations = KIMServiceLocator.getUiDocumentService().getRoleDelegations(roleId);
            for(KimDelegationImpl delegationTemp: roleDelegations){
                if(delegationTemp.getDelegationTypeCode().equals(orr.getDelegationTypeCode()))
                    delegation = delegationTemp;
            }
            if(delegation==null || StringUtils.isEmpty(delegation.getDelegationId())){
                // Create a new delegation for this delegation type code
                delegation = getDelegationOfType(roleDelegations, orr.getDelegationTypeCode());
                delegation.setKimTypeId(roleInfo.getKimTypeId());
                delegation.setRoleId(roleId);
                objectsToSave.add(delegation);
                // Save this delegation also
            }
            objectsToSave.addAll(getDelegationMembersToSave(delegation, orr));
        }
        return objectsToSave;
    }

    private List<PersistableBusinessObject> getDelegationMembersToSave(KimDelegationImpl delegation, OrgReviewRole orr){
        List<PersistableBusinessObject> objectsToSave = new ArrayList<PersistableBusinessObject>();
        String memberId;
        KimDelegationMemberImpl delegationMember = null;
        KimDelegationMemberImpl origDelegationMember = null;
        Map<String, Object> criteria;
        /*if(!orr.isCopy() && !orr.isCreateDelegation() && StringUtils.isNotEmpty(orr.getDelegationMemberId())){
            criteria = new HashMap<String, Object>();
            criteria.put(KimConstants.PrimaryKeyConstants.DELEGATION_MEMBER_ID, orr.getDelegationMemberId());
            delegationMember = (KimDelegationMemberImpl)getBusinessObjectService().findByPrimaryKey(KimDelegationMemberImpl.class, criteria);
        }*/
        if(orr.isEdit() && !orr.isCreateDelegation()){
            criteria = new HashMap<String, Object>();
            criteria.put(KimConstants.PrimaryKeyConstants.DELEGATION_MEMBER_ID, orr.getDelegationMemberId());
            origDelegationMember = (KimDelegationMemberImpl)getBusinessObjectService().findByPrimaryKey(KimDelegationMemberImpl.class, criteria);
        }
        if(StringUtils.isNotEmpty(orr.getRoleMemberRoleNamespaceCode()) && StringUtils.isNotEmpty(orr.getRoleMemberRoleName())){
            if(delegationMember==null){
                memberId = getRoleService().getRoleIdByName(orr.getRoleMemberRoleNamespaceCode(), orr.getRoleMemberRoleName());
                delegationMember = new KimDelegationMemberImpl();
                delegationMember.setMemberId(memberId);
            }
            delegationMember.setMemberTypeCode(KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE);
            delegationMember.setDelegationId(delegation.getDelegationId());
            if(orr.isEdit() && !orr.isCreateDelegation()){
                delegationMember.setDelegationMemberId(orr.getDelegationMemberId());
                delegationMember.setVersionNumber(origDelegationMember.getVersionNumber());
            }
            if(StringUtils.isEmpty(delegationMember.getDelegationMemberId()))
                delegationMember.setDelegationMemberId(getDelegationMemberId());
            //objectsToSave.addAll(getRoleRspActions(orr, delegationMember));
            delegationMember.setAttributes(getAttributes(orr, delegation.getKimTypeId()));
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
                delegationMember = new KimDelegationMemberImpl();
                delegationMember.setMemberId(memberId);
            }
            delegationMember.setMemberTypeCode(KimConstants.KimUIConstants.MEMBER_TYPE_GROUP_CODE);
            delegationMember.setDelegationId(delegation.getDelegationId());
            if(orr.isEdit() && !orr.isCreateDelegation()){
                delegationMember.setDelegationMemberId(orr.getDelegationMemberId());
                delegationMember.setVersionNumber(origDelegationMember.getVersionNumber());
            }
            if(StringUtils.isEmpty(delegationMember.getDelegationMemberId()))
                delegationMember.setDelegationMemberId(getDelegationMemberId());
            //objectsToSave.addAll(getRoleRspActions(orr, delegationMember));
            delegationMember.setAttributes(getAttributes(orr, delegation.getKimTypeId()));
            delegationMember.setActiveFromDate(orr.getActiveFromDate());
            delegationMember.setActiveToDate(orr.getActiveToDate());
            delegationMember.setRoleMemberId(orr.getRoleMemberId());
            objectsToSave.add(delegationMember);
            delegationMember = null;
        }
        if(StringUtils.isNotEmpty(orr.getPrincipalMemberPrincipalName())){
            if(delegationMember==null){
                KimPrincipal principal = getIdentityManagementService().getPrincipalByPrincipalName(orr.getPrincipalMemberPrincipalName());
                delegationMember = new KimDelegationMemberImpl();
                delegationMember.setMemberId(principal.getPrincipalId());
            }
            delegationMember.setMemberTypeCode(KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL_CODE);
            delegationMember.setDelegationId(delegation.getDelegationId());
            if(orr.isEdit() && !orr.isCreateDelegation()){
                delegationMember.setDelegationMemberId(orr.getDelegationMemberId());
                delegationMember.setVersionNumber(origDelegationMember.getVersionNumber());
            }
            if(StringUtils.isEmpty(delegationMember.getDelegationMemberId()))
                delegationMember.setDelegationMemberId(getDelegationMemberId());
            //objectsToSave.addAll(getRoleRspActions(orr, delegationMember));
            delegationMember.setAttributes(getAttributes(orr, delegation.getKimTypeId()));
            delegationMember.setActiveFromDate(orr.getActiveFromDate());
            delegationMember.setActiveToDate(orr.getActiveToDate());
            delegationMember.setRoleMemberId(orr.getRoleMemberId());
            objectsToSave.add(delegationMember);
            delegationMember = null;
        }
        return objectsToSave;
    }
    
    private List<PersistableBusinessObject> getRoleMembersToSave(KimRoleInfo roleInfo, OrgReviewRole orr){
        List<PersistableBusinessObject> objectsToSave = new ArrayList<PersistableBusinessObject>();
        String memberId;
        RoleMemberImpl roleMember = null;
        RoleMemberImpl origRoleMember = null;
        Map<String, Object> criteria;
        /*if(!orr.isCopy() && StringUtils.isNotEmpty(orr.getRoleMemberId())){
            criteria = new HashMap<String, Object>();
            criteria.put(KimConstants.PrimaryKeyConstants.ROLE_MEMBER_ID, orr.getRoleMemberId());
            roleMember = (RoleMemberImpl)getBusinessObjectService().findByPrimaryKey(RoleMemberImpl.class, criteria);
        }*/
        if(orr.isEdit()){
            criteria = new HashMap<String, Object>();
            criteria.put(KimConstants.PrimaryKeyConstants.ROLE_MEMBER_ID, orr.getRoleMemberId());
            origRoleMember = (RoleMemberImpl)getBusinessObjectService().findByPrimaryKey(RoleMemberImpl.class, criteria);
        }
        if(StringUtils.isNotEmpty(orr.getRoleMemberRoleNamespaceCode()) && StringUtils.isNotEmpty(orr.getRoleMemberRoleName())){
            if(roleMember==null){
                memberId = getRoleService().getRoleIdByName(orr.getRoleMemberRoleNamespaceCode(), orr.getRoleMemberRoleName());
                roleMember = new RoleMemberImpl();
                roleMember.setMemberId(memberId);
            }                
            roleMember.setMemberTypeCode(KimConstants.KimUIConstants.MEMBER_TYPE_ROLE_CODE);
            roleMember.setRoleId(roleInfo.getRoleId());
            if(orr.isEdit()){
                roleMember.setRoleMemberId(orr.getRoleMemberId());
                roleMember.setVersionNumber(origRoleMember.getVersionNumber());
            }
            if(StringUtils.isEmpty(roleMember.getRoleMemberId()))
                roleMember.setRoleMemberId(getRoleMemberId());
            objectsToSave.addAll(getRoleRspActions(orr, roleMember));
            roleMember.setAttributes(getAttributes(orr, roleMember, roleInfo.getKimTypeId()));
            roleMember.setActiveFromDate(orr.getActiveFromDate());
            roleMember.setActiveToDate(orr.getActiveToDate());
            objectsToSave.add(roleMember);
            roleMember = null;
        }
        if(StringUtils.isNotEmpty(orr.getGroupMemberGroupNamespaceCode()) && StringUtils.isNotEmpty(orr.getGroupMemberGroupName())){
            if(roleMember==null){
                GroupInfo groupInfo = getGroupService().getGroupInfoByName(orr.getGroupMemberGroupNamespaceCode(), orr.getGroupMemberGroupName());
                memberId = groupInfo.getGroupId();
                roleMember = new RoleMemberImpl();
                roleMember.setMemberId(memberId);
            }
            roleMember.setMemberTypeCode(KimConstants.KimUIConstants.MEMBER_TYPE_GROUP_CODE);
            roleMember.setRoleId(roleInfo.getRoleId());
            if(orr.isEdit()){
                roleMember.setRoleMemberId(orr.getRoleMemberId());
                roleMember.setVersionNumber(origRoleMember.getVersionNumber());
            }
            if(StringUtils.isEmpty(roleMember.getRoleMemberId()))
                roleMember.setRoleMemberId(getRoleMemberId());
            objectsToSave.addAll(getRoleRspActions(orr, roleMember));
            roleMember.setAttributes(getAttributes(orr, roleMember, roleInfo.getKimTypeId()));
            roleMember.setActiveFromDate(orr.getActiveFromDate());
            roleMember.setActiveToDate(orr.getActiveToDate());
            objectsToSave.add(roleMember);
            roleMember = null;
        }
        if(StringUtils.isNotEmpty(orr.getPrincipalMemberPrincipalName())){
            if(roleMember==null){
                KimPrincipal principal = getIdentityManagementService().getPrincipalByPrincipalName(orr.getPrincipalMemberPrincipalName());
                roleMember = new RoleMemberImpl();
                roleMember.setMemberId(principal.getPrincipalId());
            }
            roleMember.setMemberTypeCode(KimConstants.KimUIConstants.MEMBER_TYPE_PRINCIPAL_CODE);
            roleMember.setRoleId(roleInfo.getRoleId());
            if(orr.isEdit()){
                roleMember.setRoleMemberId(orr.getRoleMemberId());
                roleMember.setVersionNumber(origRoleMember.getVersionNumber());
            }
            if(StringUtils.isEmpty(roleMember.getRoleMemberId()))
                roleMember.setRoleMemberId(getRoleMemberId());
            objectsToSave.addAll(getRoleRspActions(orr, roleMember));
            roleMember.setAttributes(getAttributes(orr, roleMember, roleInfo.getKimTypeId()));
            roleMember.setActiveFromDate(orr.getActiveFromDate());
            roleMember.setActiveToDate(orr.getActiveToDate());
            objectsToSave.add(roleMember);
            roleMember = null;
        }
        return objectsToSave;
    }
    
    private KimDelegationImpl getDelegationOfType(List<KimDelegationImpl> roleDelegations, String delegationTypeCode){
        if(isDelegationPrimary(delegationTypeCode))
            return getPrimaryDelegation(roleDelegations);
        else
            return getSecondaryDelegation(roleDelegations);
    }
    
    private KimDelegationImpl getPrimaryDelegation(List<KimDelegationImpl> roleDelegations){
        KimDelegationImpl primaryDelegation = null;
        for(KimDelegationImpl delegation: roleDelegations){
            if(isDelegationPrimary(delegation.getDelegationTypeCode()))
                primaryDelegation = delegation;
        }
        if(primaryDelegation==null){
            primaryDelegation = new KimDelegationImpl();
            primaryDelegation.setDelegationId(getDelegationId());
            primaryDelegation.setDelegationTypeCode(KEWConstants.DELEGATION_PRIMARY);
            roleDelegations.add(primaryDelegation);
        }
        return primaryDelegation;
    }

    private String getDelegationId(){
    	SequenceAccessorService sas = getSequenceAccessorService();
    	Long nextSeq = sas.getNextAvailableSequenceNumber(
                KimConstants.SequenceNames.KRIM_DLGN_ID_S,
                KimDelegationImpl.class);
        return nextSeq.toString();
    }

    public boolean isDelegationPrimary(String delegationTypeCode){
        return KEWConstants.DELEGATION_PRIMARY.equals(delegationTypeCode);
    }

    public boolean isDelegationSecondary(String delegationTypeCode){
        return KEWConstants.DELEGATION_SECONDARY.equals(delegationTypeCode);
    }

    private KimDelegationImpl getSecondaryDelegation(List<KimDelegationImpl> roleDelegations){
        KimDelegationImpl secondaryDelegation = null;
        for(KimDelegationImpl delegation: roleDelegations){
            if(isDelegationSecondary(delegation.getDelegationTypeCode()))
                secondaryDelegation = delegation;
        }
        if(secondaryDelegation==null){
            secondaryDelegation = new KimDelegationImpl();
            secondaryDelegation.setDelegationId(getDelegationId());
            secondaryDelegation.setDelegationTypeCode(KEWConstants.DELEGATION_SECONDARY);
        }
        return secondaryDelegation;
    }
    
    private String getRoleMemberId(){
        SequenceAccessorService sas = getSequenceAccessorService();
        Long nextSeq = sas.getNextAvailableSequenceNumber(
        		KimConstants.SequenceNames.KRIM_ROLE_MBR_ID_S,
                KimDocumentRoleMember.class);
        return nextSeq.toString();
    }

    private String getDelegationMemberId(){
        SequenceAccessorService sas = getSequenceAccessorService();
        Long nextSeq = sas.getNextAvailableSequenceNumber(
        		KimConstants.SequenceNames.KRIM_DLGN_MBR_ID_S,
        		KimDelegationMemberImpl.class);
        return nextSeq.toString();
    }

    private String getRoleRspActionId(){
        SequenceAccessorService sas = getSequenceAccessorService();
        Long nextSeq = sas.getNextAvailableSequenceNumber(
        		KimConstants.SequenceNames.KRIM_ROLE_RSP_ACTN_ID_S,
        		RoleResponsibilityActionImpl.class);
        return nextSeq.toString();        
    }
    
    private String getRoleMemberAttributeDataId(){
        SequenceAccessorService sas = getSequenceAccessorService();
        Long nextSeq = sas.getNextAvailableSequenceNumber(
        		KimConstants.SequenceNames.KRIM_ATTR_DATA_ID_S,
                RoleMemberAttributeDataImpl.class);
        return nextSeq.toString(); 
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
    
    protected List<PersistableBusinessObject> getRoleMembers(OrgReviewRole orr){
        List<PersistableBusinessObject> objectsToSave = new ArrayList<PersistableBusinessObject>();
        List<String> roleNamesToSaveFor = getRolesToSaveFor(orr.getRoleNamesToConsider(), orr.getReviewRolesIndicator());
        String roleId;
        String memberId;
        RoleMemberImpl roleMember = null;
        KimRoleInfo roleInfo;
        Map<String, Object> criteria;
        for(String roleName: roleNamesToSaveFor){
            roleId = getRoleService().getRoleIdByName(
                    KFSConstants.SysKimConstants.ORGANIZATION_REVIEWER_ROLE_NAMESPACECODE, roleName);
            roleInfo = getRoleService().getRole(roleId);
            objectsToSave.addAll(getRoleMembersToSave(roleInfo, orr));
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
        for(AttributeDefinition definition: attributeDefinitions.values()){
            if(definition.getName().equals(attributeName))
                return getKimAttributeDefnId(definition);
        }
        return null;
    }
    
    //protected String getKimAttributeId()
    protected List<RoleMemberAttributeDataImpl> getAttributes(
            OrgReviewRole orr, RoleMemberImpl roleMember, String kimTypeId){
        KimTypeInfo kimType = getTypeInfoService().getKimType(kimTypeId);
        KimTypeService typeService = KimCommonUtils.getKimTypeService(kimType);
        AttributeDefinitionMap attributeDefinitions = typeService.getAttributeDefinitions(kimTypeId);
        List<RoleMemberAttributeDataImpl> attributeDataList = new ArrayList<RoleMemberAttributeDataImpl>();
        RoleMemberAttributeDataImpl attributeData = new RoleMemberAttributeDataImpl();
        //chart code
        attributeData.setAttributeDataId(getRoleMemberAttributeDataId());
        attributeData.setKimTypeId(kimTypeId);
        attributeData.setAttributeValue(orr.getChartOfAccountsCode());
        attributeData.setKimAttributeId(getKimAttributeDefnId(attributeDefinitions, KfsKimAttributes.CHART_OF_ACCOUNTS_CODE));
        attributeDataList.add(attributeData);
        
        //org code
        attributeData = new RoleMemberAttributeDataImpl();
        attributeData.setKimTypeId(kimTypeId);
        attributeData.setAttributeDataId(getRoleMemberAttributeDataId());
        attributeData.setAttributeValue(orr.getOrganizationCode());
        attributeData.setKimAttributeId(getKimAttributeDefnId(attributeDefinitions, KfsKimAttributes.ORGANIZATION_CODE));
        attributeDataList.add(attributeData);

        //document type
        String attributeDefnId = getKimAttributeDefnId(attributeDefinitions, KfsKimAttributes.DOCUMENT_TYPE_NAME);
        if(StringUtils.isNotEmpty(attributeDefnId) && orr.getFinancialSystemDocumentTypeCode()!=null){
            attributeData = new RoleMemberAttributeDataImpl();
            attributeData.setAttributeDataId(getRoleMemberAttributeDataId());
            attributeData.setKimTypeId(kimTypeId);
            attributeData.setAttributeValue(orr.getFinancialSystemDocumentTypeCode());
            attributeData.setKimAttributeId(attributeDefnId);
            attributeDataList.add(attributeData);
        }

        //override code
        attributeDefnId = getKimAttributeDefnId(attributeDefinitions, KfsKimAttributes.ACCOUNTING_LINE_OVERRIDE_CODE);
        if(StringUtils.isNotEmpty(attributeDefnId) && orr.getOverrideCode()!=null){
            attributeData = new RoleMemberAttributeDataImpl();
            attributeData.setAttributeDataId(getRoleMemberAttributeDataId());
            attributeData.setKimTypeId(kimTypeId);
            attributeData.setAttributeValue(orr.getOverrideCode());
            attributeData.setKimAttributeId(attributeDefnId);
            attributeDataList.add(attributeData);
        }
        
        //from amount
        attributeDefnId = getKimAttributeDefnId(attributeDefinitions, KfsKimAttributes.FROM_AMOUNT);
        if(StringUtils.isNotEmpty(attributeDefnId) && orr.getFromAmount()!=null){
            attributeData = new RoleMemberAttributeDataImpl();
            attributeData.setAttributeDataId(getRoleMemberAttributeDataId());
            attributeData.setKimTypeId(kimTypeId);
            attributeData.setAttributeValue(orr.getFromAmount());
            attributeData.setKimAttributeId(attributeDefnId);
            attributeDataList.add(attributeData);
        }
        
        //to amount
        attributeDefnId = getKimAttributeDefnId(attributeDefinitions, KfsKimAttributes.TO_AMOUNT);
        if(StringUtils.isNotEmpty(attributeDefnId) && orr.getToAmount()!=null){
            attributeData = new RoleMemberAttributeDataImpl();
            attributeData.setAttributeDataId(getRoleMemberAttributeDataId());
            attributeData.setKimTypeId(kimTypeId);
            attributeData.setAttributeValue(orr.getToAmount());
            attributeData.setKimAttributeId(attributeDefnId);
            attributeDataList.add(attributeData);
        }
        
        return attributeDataList;
    }
    
    protected List<KimDelegationMemberAttributeDataImpl> getAttributes(OrgReviewRole orr, String kimTypeId){
        if(StringUtils.isEmpty(kimTypeId)) return null;
        
        Map<String, String> criteria = new HashMap<String, String>();
        criteria.put("kimTypeId", kimTypeId);
        KimTypeInfo kimType = getTypeInfoService().getKimType(kimTypeId);
        KimTypeService typeService = KimCommonUtils.getKimTypeService(kimType);
        AttributeDefinitionMap attributeDefinitions = typeService.getAttributeDefinitions(kimTypeId);
        List<KimDelegationMemberAttributeDataImpl> attributeDataList = new ArrayList<KimDelegationMemberAttributeDataImpl>();
        KimDelegationMemberAttributeDataImpl attributeData = new KimDelegationMemberAttributeDataImpl();
        //chart code
        attributeData.setAttributeDataId(getRoleMemberAttributeDataId());
        attributeData.setKimTypeId(kimTypeId);
        attributeData.setAttributeValue(orr.getChartOfAccountsCode());
        attributeData.setKimAttributeId(getKimAttributeDefnId(attributeDefinitions, KfsKimAttributes.CHART_OF_ACCOUNTS_CODE));
        attributeDataList.add(attributeData);
        
        //org code
        attributeData = new KimDelegationMemberAttributeDataImpl();
        attributeData.setKimTypeId(kimTypeId);
        attributeData.setAttributeDataId(getRoleMemberAttributeDataId());
        attributeData.setAttributeValue(orr.getOrganizationCode());
        attributeData.setKimAttributeId(getKimAttributeDefnId(attributeDefinitions, KfsKimAttributes.ORGANIZATION_CODE));
        attributeDataList.add(attributeData);

        //document type
        String attributeDefnId = getKimAttributeDefnId(attributeDefinitions, KfsKimAttributes.DOCUMENT_TYPE_NAME);
        if(StringUtils.isNotEmpty(attributeDefnId) && orr.getFinancialSystemDocumentTypeCode()!=null){
            attributeData = new KimDelegationMemberAttributeDataImpl();
            attributeData.setAttributeDataId(getRoleMemberAttributeDataId());
            attributeData.setKimTypeId(kimTypeId);
            attributeData.setAttributeValue(orr.getFinancialSystemDocumentTypeCode());
            attributeData.setKimAttributeId(attributeDefnId);
            attributeDataList.add(attributeData);
        }

        //override code
        attributeDefnId = getKimAttributeDefnId(attributeDefinitions, KfsKimAttributes.ACCOUNTING_LINE_OVERRIDE_CODE);
        if(StringUtils.isNotEmpty(attributeDefnId) && orr.getOverrideCode()!=null){
            attributeData = new KimDelegationMemberAttributeDataImpl();
            attributeData.setAttributeDataId(getRoleMemberAttributeDataId());
            attributeData.setKimTypeId(kimTypeId);
            attributeData.setAttributeValue(orr.getOverrideCode());
            attributeData.setKimAttributeId(attributeDefnId);
            attributeDataList.add(attributeData);
        }
        
        //from amount
        attributeDefnId = getKimAttributeDefnId(attributeDefinitions, KfsKimAttributes.FROM_AMOUNT);
        if(StringUtils.isNotEmpty(attributeDefnId) && orr.getFromAmount()!=null){
            attributeData = new KimDelegationMemberAttributeDataImpl();
            attributeData.setAttributeDataId(getRoleMemberAttributeDataId());
            attributeData.setKimTypeId(kimTypeId);
            attributeData.setAttributeValue(orr.getFromAmount());
            attributeData.setKimAttributeId(attributeDefnId);
            attributeDataList.add(attributeData);
        }
        
        //to amount
        attributeDefnId = getKimAttributeDefnId(attributeDefinitions, KfsKimAttributes.TO_AMOUNT);
        if(StringUtils.isNotEmpty(attributeDefnId) && orr.getToAmount()!=null){
            attributeData = new KimDelegationMemberAttributeDataImpl();
            attributeData.setAttributeDataId(getRoleMemberAttributeDataId());
            attributeData.setKimTypeId(kimTypeId);
            attributeData.setAttributeValue(orr.getToAmount());
            attributeData.setKimAttributeId(attributeDefnId);
            attributeDataList.add(attributeData);
        }
        
        return attributeDataList;
    }
    
    protected List<RoleResponsibilityActionImpl> getRoleRspActions(OrgReviewRole orr, RoleMemberImpl roleMember){
        List<RoleResponsibilityActionImpl> roleRspActions = new ArrayList<RoleResponsibilityActionImpl>();
        RoleResponsibilityActionImpl roleRspAction;
        //Assuming that there is only one responsibility for an org role
        //Get it now given the role id
        Map<String, String> criteria = new HashMap<String,String>(1);
        criteria.put(KimConstants.PrimaryKeyConstants.ROLE_ID, roleMember.getRoleId());
        List<RoleResponsibilityImpl> roleResponsibilities = (List<RoleResponsibilityImpl>)getBusinessObjectService().findMatching(RoleResponsibilityImpl.class, criteria);
        if(roleResponsibilities==null || roleResponsibilities.size()<1)
            throw new KualiException("The Org Review Role id:"+roleMember.getRoleId()+" does not have any responsibility associated with it");

        List<RoleResponsibilityActionImpl> origRoleRspActions = getUIDocumentService().getRoleMemberResponsibilityActionImpls(roleMember.getRoleMemberId());
        if(origRoleRspActions!=null && origRoleRspActions.size()>0)
            roleRspAction = origRoleRspActions.get(0);
        else{
            roleRspAction = new RoleResponsibilityActionImpl();
            roleRspAction.setRoleResponsibilityActionId(getRoleRspActionId());
        }
        roleRspAction.setRoleMemberId(roleMember.getRoleMemberId());
        roleRspAction.setRoleResponsibilityId(roleResponsibilities.get(0).getRoleResponsibilityId());
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
    
    protected SequenceAccessorService getSequenceAccessorService(){
        if(sequenceAccessorService==null){
            sequenceAccessorService = KNSServiceLocator.getSequenceAccessorService();
        }
        return sequenceAccessorService;
    }

    protected RoleService getRoleService(){
        if(roleService==null){
            roleService = KIMServiceLocator.getRoleService();
        }
        return roleService;
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

    protected UiDocumentService getUIDocumentService(){
        if(uiDocumentService==null){
            uiDocumentService = KIMServiceLocator.getUiDocumentService();
        }
        return uiDocumentService;
    }
    protected KimTypeInfoService getTypeInfoService(){
        if(typeInfoService==null){
            typeInfoService = KIMServiceLocator.getTypeInfoService();
        }
        return typeInfoService;
    }
    
}