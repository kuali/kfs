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
import org.kuali.kfs.coa.identity.KfsKimDocDelegateMember;
import org.kuali.kfs.coa.identity.KfsKimDocDelegateType;
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
import org.kuali.rice.core.api.delegation.DelegationType;
import org.kuali.rice.core.api.membership.MemberType;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.common.attribute.KimAttribute;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.group.GroupService;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.role.RoleMember;
import org.kuali.rice.kim.api.role.RoleResponsibility;
import org.kuali.rice.kim.api.role.RoleResponsibilityAction;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.api.services.IdentityManagementService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.api.type.KimType;
import org.kuali.rice.kim.api.type.KimTypeAttribute;
import org.kuali.rice.kim.api.type.KimTypeInfoService;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.Maintainable;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.kns.web.ui.Section;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.document.MaintenanceLock;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;


public class OrgReviewRoleMaintainableImpl extends FinancialSystemMaintainable {

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
        
        // The links on the lookup set barious variables, including the methodToCall on the "bo" class
        // and the delegate or role IDs being edited/copied
        
        if( (KRADConstants.MAINTENANCE_EDIT_METHOD_TO_CALL.equals(orr.getMethodToCall()) ||
                KRADConstants.MAINTENANCE_COPY_METHOD_TO_CALL.equals(orr.getMethodToCall())) 
                &&
                (StringUtils.isNotEmpty(orr.getODelMId()) || StringUtils.isNotEmpty(orr.getORMId())) ){
            // check if we have the information to create a delegation
            if(StringUtils.isNotEmpty(orr.getODelMId()) && !orr.isCreateDelegation()){
                getOrgReviewRoleService().populateOrgReviewRoleFromDelegationMember(orr, orr.getODelMId());
                
                orr.setDelegate(true);
            } else if(StringUtils.isNotEmpty(orr.getORMId())){
                getOrgReviewRoleService().populateOrgReviewRoleFromRoleMember(orr, orr.getORMId());
                
                if(orr.isCreateDelegation()) {
                    orr.setDelegate(true);
                    orr.setKimDocumentRoleMember(null);
                } else {
                    orr.setDelegate(false);
                }
            }
            // blank these out, since it is a flag to init the object
            orr.setORMId("");
            orr.setODelMId(""); 
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
    
//    public List<RoleResponsibilityAction> getRoleRspActions(String roleMemberId){
//        return KimApiServiceLocator.getRoleService().getRoleMemberResponsibilityActions(roleMemberId);
//    }
    
    @Override
    public void processAfterEdit(MaintenanceDocument document, Map<String,String[]> parameters){
        super.processAfterEdit(document, parameters);
        OrgReviewRole orr = (OrgReviewRole)document.getOldMaintainableObject().getBusinessObject();
        orr.setEdit(true);
    }

    @Override
    public void processAfterCopy(MaintenanceDocument document, Map<String,String[]> parameters){
        super.processAfterCopy(document, parameters);
        OrgReviewRole orr = (OrgReviewRole)document.getOldMaintainableObject().getBusinessObject();
        if(orr.isDelegate() || orr.isCreateDelegation()) {
            orr.setDelegationMemberId("");
        } else { 
            orr.setRoleMemberId("");
        }
        orr.setCopy(true);
    }

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
        OrgReviewRole orr = (OrgReviewRole)document.getNewMaintainableObject().getBusinessObject();
        //If oldMaintainable is null, it means we are trying to get sections for the old part
        //If oldMaintainable is not null, it means we are trying to get sections for the new part
        //Refer to KualiMaintenanceForm lines 288-294
        if(oldMaintainable!=null){
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
                        prepareFieldsCommon(orr,field);
                    }
                }
            }
        } else{
            for (Section section : sections) {
                for (Row row : section.getRows()) {
                    for (Field field : row.getFields()) {
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

    private void prepareFieldsCommon(OrgReviewRole orr, Field field){

        boolean hasOrganizationHierarchy = getOrgReviewRoleService().hasOrganizationHierarchy(orr.getFinancialSystemDocumentTypeCode());
        String closestOrgReviewRoleParentDocumentTypeName = getOrgReviewRoleService().getClosestOrgReviewRoleParentDocumentTypeName(orr.getFinancialSystemDocumentTypeCode());
        boolean isFSTransDoc = orr.getFinancialSystemDocumentTypeCode().equals(KFSConstants.FINANCIAL_SYSTEM_TRANSACTIONAL_DOCUMENT) || KFSConstants.FINANCIAL_SYSTEM_TRANSACTIONAL_DOCUMENT.equals(closestOrgReviewRoleParentDocumentTypeName);
        boolean hasAccountingOrganizationHierarchy = getOrgReviewRoleService().hasAccountingOrganizationHierarchy(orr.getFinancialSystemDocumentTypeCode()) || isFSTransDoc;
        boolean shouldReviewTypesFieldBeReadOnly = isFSTransDoc || hasOrganizationHierarchy || hasAccountingOrganizationHierarchy || 
          (StringUtils.isNotEmpty(closestOrgReviewRoleParentDocumentTypeName) && closestOrgReviewRoleParentDocumentTypeName.equals(KFSConstants.FINANCIAL_SYSTEM_COMPLEX_MAINTENANCE_DOCUMENT));
        
        if(OrgReviewRole.REVIEW_ROLES_INDICATOR_FIELD_NAME.equals(field.getPropertyName())) {
            if(shouldReviewTypesFieldBeReadOnly) {
                field.setReadOnly(true);
            }
        } else if(OrgReviewRole.FROM_AMOUNT_FIELD_NAME.equals(field.getPropertyName()) ||
                OrgReviewRole.TO_AMOUNT_FIELD_NAME.equals(field.getPropertyName()) ||
                OrgReviewRole.OVERRIDE_CODE_FIELD_NAME.equals(field.getPropertyName())) {
            if(!hasAccountingOrganizationHierarchy && shouldReviewTypesFieldBeReadOnly){
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
    
    /**
     * 
     * @see org.kuali.rice.kns.maintenance.Maintainable#saveBusinessObject()
     */
    @Override
    public void saveBusinessObject() {
        OrgReviewRole orr = (OrgReviewRole)getBusinessObject();
        if(orr.isDelegate() || orr.isCreateDelegation()){
            // Save delegation(s)
            List<KfsKimDocDelegateType> objectsToSave = getDelegations(orr);
            if(objectsToSave!=null){
                for(KfsKimDocDelegateType delegateInfo: objectsToSave){
                    for(KfsKimDocDelegateMember delegateMemberInfo: delegateInfo.getMembers()){
                        java.sql.Date fromDate = null;
                        java.sql.Date toDate = null;
                        if ( delegateMemberInfo.getActiveFromDate() != null ) {
                            fromDate = new java.sql.Date( delegateMemberInfo.getActiveFromDate().getMillis() ); 
                        }
                        if ( delegateMemberInfo.getActiveToDate() != null ) {
                            toDate = new java.sql.Date( delegateMemberInfo.getActiveToDate().getMillis() ); 
                        }
                        KimApiServiceLocator.getRoleService().saveDelegationMemberForRole(delegateMemberInfo.getDelegationMemberId(),
                            delegateMemberInfo.getRoleMemberId(), delegateMemberInfo.getMemberId(), 
                            delegateMemberInfo.getType().getCode(), delegateInfo.getDelegationType().getCode(), 
                            delegateInfo.getRoleId(), delegateMemberInfo.getAttributes(), 
                            fromDate, toDate);
                    }
                }
            }
        } else{
            // Save role member(s)
            List<KfsKimDocRoleMember> objectsToSave = getRoleMembers(orr);
            RoleMember savedRoleMember;
            if(objectsToSave!=null){
                for(KfsKimDocRoleMember roleMember: objectsToSave){
                    java.sql.Date fromDate = null;
                    java.sql.Date toDate = null;
                    if ( roleMember.getActiveFromDate() != null ) {
                        fromDate = new java.sql.Date( roleMember.getActiveFromDate().getMillis() ); 
                    }
                    if ( roleMember.getActiveToDate() != null ) {
                        toDate = new java.sql.Date( roleMember.getActiveToDate().getMillis() ); 
                    }
                    savedRoleMember = KimApiServiceLocator.getRoleService().saveRoleMemberForRole(roleMember.getId(),
                            roleMember.getMemberId(), roleMember.getType().getCode(), roleMember.getRoleId(), 
                            roleMember.getAttributes(), fromDate, toDate);
                    List<RoleResponsibilityAction> roleRspActionsToSave = getRoleRspActions(orr, roleMember);
                    if(roleRspActionsToSave!=null){
                        for(RoleResponsibilityAction rspActionInfo: roleRspActionsToSave){
                            KimApiServiceLocator.getRoleService().saveRoleRspActions(
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
    
    protected List<KfsKimDocDelegateType> getDelegations(OrgReviewRole orr){
//        List<DelegateMember> delegationMembers = new ArrayList<DelegateMember>();
        List<String> roleNamesToSaveFor = getRolesToSaveFor(orr.getRoleNamesToConsider(), orr.getReviewRolesIndicator());
        if(roleNamesToSaveFor!=null){
            List<KfsKimDocDelegateType> roleDelegations = new ArrayList<KfsKimDocDelegateType>(roleNamesToSaveFor.size());
            for(String roleName: roleNamesToSaveFor){
                Role roleInfo = getRoleInfo( KFSConstants.SysKimApiConstants.ORGANIZATION_REVIEWER_ROLE_NAMESPACECODE, roleName);
                KfsKimDocDelegateType roleDelegation = new KfsKimDocDelegateType(roleInfo);
                roleDelegation.setMembers(getDelegationMembersToSave(orr));
                roleDelegations.add(roleDelegation);
            }
            return roleDelegations;
        }
        return Collections.emptyList();
    }

    // note: this assumes that all use the KFS-SYS namespace
    protected static final Map<String,Role> ROLE_CACHE = new HashMap<String, Role>();
    
    protected Role getRoleInfo( String namespaceCode, String roleName ) {
        if ( roleName == null ) {
            return null;
        }
        Role role = ROLE_CACHE.get(roleName);
        if ( role == null ) {
            role = KimApiServiceLocator.getRoleService().getRoleByNameAndNamespaceCode( KFSConstants.SysKimApiConstants.ORGANIZATION_REVIEWER_ROLE_NAMESPACECODE, roleName);
            synchronized ( ROLE_CACHE ) {
                ROLE_CACHE.put(roleName, role);
            }
        }
        return role;
    }
    
    private List<KfsKimDocDelegateMember> getDelegationMembersToSave(OrgReviewRole orr){
        List<KfsKimDocDelegateMember> objectsToSave = new ArrayList<KfsKimDocDelegateMember>();
        KfsKimDocDelegateMember delegationMember = null;
        if(orr.isEdit() && !orr.isCreateDelegation()){
            delegationMember = new KfsKimDocDelegateMember( KimApiServiceLocator.getRoleService().getDelegationMemberById(orr.getDelegationMemberId()) );
        }

        if(delegationMember==null){
            delegationMember = new KfsKimDocDelegateMember();
            if(StringUtils.isNotEmpty(orr.getRoleMemberRoleNamespaceCode()) && StringUtils.isNotEmpty(orr.getRoleMemberRoleName())){
                String roleId = KimApiServiceLocator.getRoleService().getRoleIdByNameAndNamespaceCode(orr.getRoleMemberRoleNamespaceCode(), orr.getRoleMemberRoleName());                
                delegationMember.setMemberId(roleId);
                delegationMember.setType(MemberType.ROLE);
            } else if(StringUtils.isNotEmpty(orr.getGroupMemberGroupNamespaceCode()) && StringUtils.isNotEmpty(orr.getGroupMemberGroupName())){
                Group groupInfo = KimApiServiceLocator.getGroupService().getGroupByNameAndNamespaceCode(orr.getGroupMemberGroupNamespaceCode(), orr.getGroupMemberGroupName());
                delegationMember.setMemberId(groupInfo.getId());
                delegationMember.setType(MemberType.GROUP);
            } else if(StringUtils.isNotEmpty(orr.getPrincipalMemberPrincipalName())){
                Principal principal = KimApiServiceLocator.getIdentityService().getPrincipalByPrincipalName(orr.getPrincipalMemberPrincipalName());
                delegationMember.setMemberId(principal.getPrincipalId());
                delegationMember.setType(MemberType.PRINCIPAL);
            }
        }
        delegationMember.setDelegationType(DelegationType.fromCode( orr.getDelegationTypeCode() ));
        delegationMember.setAttributes(getAttributes(orr, orr.getKimTypeId()));
        delegationMember.setActiveFromDate( new DateTime( orr.getActiveFromDate() ) );
        delegationMember.setActiveToDate( new DateTime( orr.getActiveToDate() ) );
        delegationMember.setRoleMemberId(orr.getRoleMemberId());
        return Collections.singletonList(delegationMember);
    }
    
    private List<KfsKimDocRoleMember> getRoleMembersToSave(Role roleInfo, OrgReviewRole orr){
        KfsKimDocRoleMember roleMember = null;
        if(StringUtils.isNotEmpty(orr.getRoleMemberRoleNamespaceCode()) && StringUtils.isNotEmpty(orr.getRoleMemberRoleName())){
            String memberId = KimApiServiceLocator.getRoleService().getRoleIdByNameAndNamespaceCode(orr.getRoleMemberRoleNamespaceCode(), orr.getRoleMemberRoleName());
            roleMember = new KfsKimDocRoleMember(roleInfo.getId(), MemberType.ROLE, memberId);
        }
        if(roleMember==null){
            if(StringUtils.isNotEmpty(orr.getGroupMemberGroupNamespaceCode()) && StringUtils.isNotEmpty(orr.getGroupMemberGroupName())){
                Group groupInfo = KimApiServiceLocator.getGroupService().getGroupByNameAndNamespaceCode(orr.getGroupMemberGroupNamespaceCode(), orr.getGroupMemberGroupName());
                roleMember = new KfsKimDocRoleMember(roleInfo.getId(), MemberType.GROUP, groupInfo.getId() );
            }
        }
        if(roleMember==null){
            if(StringUtils.isNotEmpty(orr.getPrincipalMemberPrincipalName())){
                Principal principal = KimApiServiceLocator.getIdentityService().getPrincipalByPrincipalName(orr.getPrincipalMemberPrincipalName());
                roleMember = new KfsKimDocRoleMember(roleInfo.getId(), MemberType.PRINCIPAL, principal.getPrincipalId());
            }
        }
        if(orr.isEdit()){
            roleMember.setId(orr.getRoleMemberId());
        }
        roleMember.setAttributes(getAttributes(orr, roleInfo.getKimTypeId()));
        roleMember.setActiveFromDate( new DateTime( orr.getActiveFromDate().getTime() ) );
        roleMember.setActiveToDate( new DateTime( orr.getActiveToDate().getTime() ) );
        return Collections.singletonList(roleMember);
    }
    
    private List<String> getRolesToSaveFor(List<String> roleNamesToConsider, String reviewRolesIndicator){
        if(roleNamesToConsider!=null){
            List<String> roleToSaveFor = new ArrayList<String>();
            if(KFSConstants.COAConstants.ORG_REVIEW_ROLE_ORG_ACC_ONLY_CODE.equals(reviewRolesIndicator)){
                roleToSaveFor.add(KFSConstants.SysKimApiConstants.ACCOUNTING_REVIEWER_ROLE_NAME);
            } else if(KFSConstants.COAConstants.ORG_REVIEW_ROLE_ORG_ONLY_CODE.equals(reviewRolesIndicator)){
                roleToSaveFor.add(KFSConstants.SysKimApiConstants.ORGANIZATION_REVIEWER_ROLE_NAME);
            } else{
                roleToSaveFor.addAll(roleNamesToConsider);
            }
            return roleToSaveFor;
        } else {
            return Collections.emptyList();
        }
    }
    
    protected List<KfsKimDocRoleMember> getRoleMembers(OrgReviewRole orr){
        List<KfsKimDocRoleMember> objectsToSave = new ArrayList<KfsKimDocRoleMember>();
        List<String> roleNamesToSaveFor = getRolesToSaveFor(orr.getRoleNamesToConsider(), orr.getReviewRolesIndicator());
        if(roleNamesToSaveFor!=null){
            for(String roleName: roleNamesToSaveFor){
                Role roleInfo = getRoleInfo( KFSConstants.SysKimApiConstants.ORGANIZATION_REVIEWER_ROLE_NAMESPACECODE, roleName);
                objectsToSave.addAll(getRoleMembersToSave(roleInfo, orr));
            }
        }
        return objectsToSave;
    }
    
    protected KfsKimDocumentAttributeData getAttribute( String kimTypeId, String attributeName, String attributeValue ) {
        if ( StringUtils.isNotBlank(attributeValue) ) {
            KimAttribute attribute = getAttributeDefinition(kimTypeId, attributeName);
            if( attribute != null ){
                KfsKimDocumentAttributeData attributeData = new KfsKimDocumentAttributeData();
                attributeData.setKimTypId(kimTypeId);
                attributeData.setAttrVal(attributeValue);
                attributeData.setKimAttrDefnId(attribute.getId());
                attributeData.setKimAttribute(attribute);
                return attributeData;
            }
        }
        return null;
    }

    protected static final Map<String,Map<String,KimAttribute>> ATTRIBUTE_CACHE = new HashMap<String, Map<String,KimAttribute>>();
    
    protected KimAttribute getAttributeDefinition( String kimTypeId, String attributeName ) {
        // attempt to pull from cache
        Map<String,KimAttribute> typeAttributes = ATTRIBUTE_CACHE.get(kimTypeId);
        // if type has not been loaded, init
        if ( typeAttributes == null ) {
            KimType kimType = KimApiServiceLocator.getKimTypeInfoService().getKimType(kimTypeId);
            if ( kimType != null ) {
                List<KimTypeAttribute> attributes = kimType.getAttributeDefinitions();
                typeAttributes = new HashMap<String, KimAttribute>();
                if ( attributes != null ) {
                    // build the map and put it into the cache
                    for ( KimTypeAttribute att : attributes ) {
                        typeAttributes.put( att.getKimAttribute().getAttributeName(), att.getKimAttribute() );
                    }
                }
                synchronized ( ATTRIBUTE_CACHE ) {
                    ATTRIBUTE_CACHE.put(kimTypeId, typeAttributes);
                }
            }
        }
        // now, see if the attribute is in there
        if ( typeAttributes != null ) {
            return typeAttributes.get(attributeName);
        }
        return null;
    }
    

    protected Map<String,String> getAttributes(OrgReviewRole orr, String kimTypeId){
        if( StringUtils.isBlank(kimTypeId) ) {
            return Collections.emptyMap();
        }

        List<KfsKimDocumentAttributeData> attributeDataList = new ArrayList<KfsKimDocumentAttributeData>();
        KfsKimDocumentAttributeData attributeData = getAttribute(kimTypeId, KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, orr.getChartOfAccountsCode());
        if ( attributeData != null ) {
            attributeDataList.add(attributeData);
        }

        attributeData = getAttribute(kimTypeId, KfsKimAttributes.ORGANIZATION_CODE, orr.getOrganizationCode());
        if ( attributeData != null ) {
            attributeDataList.add(attributeData);
        }

        attributeData = getAttribute(kimTypeId, KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME, orr.getFinancialSystemDocumentTypeCode());
        if ( attributeData != null ) {
            attributeDataList.add(attributeData);
        }

        attributeData = getAttribute(kimTypeId, KfsKimAttributes.ACCOUNTING_LINE_OVERRIDE_CODE, orr.getOverrideCode());
        if ( attributeData != null ) {
            attributeDataList.add(attributeData);
        }

        attributeData = getAttribute(kimTypeId, KfsKimAttributes.FROM_AMOUNT, orr.getFromAmountStr());
        if ( attributeData != null ) {
            attributeDataList.add(attributeData);
        }

        attributeData = getAttribute(kimTypeId, KfsKimAttributes.TO_AMOUNT, orr.getToAmountStr());
        if ( attributeData != null ) {
            attributeDataList.add(attributeData);
        }

        return orr.getQualifierAsAttributeSet(attributeDataList);
    }
    
    protected List<RoleResponsibilityAction> getRoleRspActions(OrgReviewRole orr, KfsKimDocRoleMember roleMember){
        List<RoleResponsibilityAction> roleRspActions = new ArrayList<RoleResponsibilityAction>();
        RoleResponsibilityAction.Builder roleRspAction;
        //Assuming that there is only one responsibility for an org role
        //Get it now given the role id
        List<RoleResponsibility> roleResponsibilityInfos = ((List<RoleResponsibility>)KimApiServiceLocator.getRoleService().getRoleResponsibilities(roleMember.getRoleId()));
        //Assuming that there is only 1 responsibility for both the org review roles
        if(roleResponsibilityInfos!=null && roleResponsibilityInfos.size()<1) {
            throw new RuntimeException("The Org Review Role id:"+roleMember.getRoleId()+" does not have any responsibility associated with it");
        }

        List<RoleResponsibilityAction> origRoleRspActions = ((List<RoleResponsibilityAction>)KimApiServiceLocator.getRoleService().getRoleMemberResponsibilityActions(roleMember.getId()));
        
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
        roleRspActions.add(roleRspAction.build());
        return roleRspActions;
    }
    
    protected OrgReviewRoleService getOrgReviewRoleService(){
        if(orgReviewRoleService==null){
            orgReviewRoleService = SpringContext.getBean( OrgReviewRoleService.class );
        }
        return orgReviewRoleService;
    }

    private KfsKimDocDelegateMember getDelegateMemberFromList(List<KfsKimDocDelegateMember> delegateMembers, String memberId, String memberTypeCode){
        if(delegateMembers!=null){
            if(StringUtils.isEmpty(memberId) || StringUtils.isEmpty(memberTypeCode)) {
                return null;
            }
            for(KfsKimDocDelegateMember info: delegateMembers){
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
        if(fieldValues.containsKey(OrgReviewRole.DOC_TYPE_NAME_FIELD_NAME)){
            docTypeName = fieldValues.get(OrgReviewRole.DOC_TYPE_NAME_FIELD_NAME);
        }
        if(KFSConstants.RETURN_METHOD_TO_CALL.equals(methodToCall) &&
           fieldValues.containsKey(OrgReviewRole.DOC_TYPE_NAME_FIELD_NAME) &&
           getOrgReviewRoleService().isValidDocumentTypeForOrgReview(docTypeName) == false){
            
            GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(KFSConstants.MAINTENANCE_NEW_MAINTAINABLE + OrgReviewRole.DOC_TYPE_NAME_FIELD_NAME, KFSKeyConstants.ERROR_DOCUMENT_ORGREVIEW_INVALID_DOCUMENT_TYPE, docTypeName);            
            return new HashMap();
            
        }else{
            return super.populateBusinessObject(fieldValues, maintenanceDocument, methodToCall);
        }
    }
}
