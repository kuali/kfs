/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.coa.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.kuali.kfs.coa.identity.KfsKimDocDelegateMember;
import org.kuali.kfs.coa.identity.KfsKimDocRoleMember;
import org.kuali.kfs.coa.identity.KfsKimDocumentAttributeData;
import org.kuali.kfs.coa.identity.OrgReviewRole;
import org.kuali.kfs.coa.service.OrgReviewRoleService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.core.api.criteria.PredicateUtils;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.delegation.DelegationType;
import org.kuali.rice.core.api.membership.MemberType;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.common.attribute.KimAttribute;
import org.kuali.rice.kim.api.common.delegate.DelegateMember;
import org.kuali.rice.kim.api.common.delegate.DelegateType;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.role.RoleMember;
import org.kuali.rice.kim.api.role.RoleMemberContract;
import org.kuali.rice.kim.api.role.RoleMemberQueryResults;
import org.kuali.rice.kim.api.role.RoleResponsibilityAction;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.api.type.KimType;
import org.kuali.rice.kim.api.type.KimTypeAttribute;
import org.kuali.rice.kim.util.KimCommonUtils;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.util.GlobalVariables;
import org.springframework.cache.annotation.Cacheable;

public class OrgReviewRoleServiceImpl implements OrgReviewRoleService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OrgReviewRoleServiceImpl.class);

    // note: this assumes that all use the KFS-SYS namespace
    protected static final Map<String,Role> ROLE_CACHE = new HashMap<String, Role>();
    protected static final Map<String,Map<String,KimAttribute>> ATTRIBUTE_CACHE = new HashMap<String, Map<String,KimAttribute>>();

    protected Set<String> potentialParentDocumentTypeNames = new HashSet<String>();
    {
        potentialParentDocumentTypeNames.add(KFSConstants.FINANCIAL_SYSTEM_TRANSACTIONAL_DOCUMENT);
        potentialParentDocumentTypeNames.add(KFSConstants.FINANCIAL_SYSTEM_COMPLEX_MAINTENANCE_DOCUMENT);
        potentialParentDocumentTypeNames.add(KFSConstants.FINANCIAL_SYSTEM_SIMPLE_MAINTENANCE_DOCUMENT);
        potentialParentDocumentTypeNames = Collections.unmodifiableSet(potentialParentDocumentTypeNames);
    }
    protected DocumentTypeService documentTypeService;

    @Override
    public RoleMember getRoleMemberFromKimRoleService( String roleMemberId ) {
        if ( StringUtils.isEmpty(roleMemberId) ) {
            throw new IllegalArgumentException( "Role member ID may not be blank." );
        }
        RoleMemberQueryResults roleMembers = KimApiServiceLocator.getRoleService().findRoleMembers(QueryByCriteria.Builder.fromPredicates( PredicateUtils.convertMapToPredicate(Collections.singletonMap(KimConstants.PrimaryKeyConstants.ID, roleMemberId))));
        if ( roleMembers == null || roleMembers.getResults() == null || roleMembers.getResults().isEmpty() ) {
            throw new IllegalArgumentException( "Unknown role member ID passed in - nothing returned from KIM RoleService: " + roleMemberId );
        }
        return roleMembers.getResults().get(0);
    }

    @Override
    public void populateOrgReviewRoleFromRoleMember(OrgReviewRole orr, String roleMemberId) {
        if ( StringUtils.isBlank(roleMemberId) ) {
            throw new IllegalArgumentException( "Role member ID may not be blank" );
        }
        RoleMember roleMember = getRoleMemberFromKimRoleService(roleMemberId);
        orr.setRoleMember(roleMember);

        populateObjectExtras(orr);
    }

    @Override
    public void populateOrgReviewRoleFromDelegationMember(OrgReviewRole orr, String roleMemberId, String delegationMemberId) {
        RoleMember roleMember = null;
        if ( StringUtils.isNotBlank(roleMemberId) ) {
            roleMember = getRoleMemberFromKimRoleService(roleMemberId);
        }
        RoleService roleService = KimApiServiceLocator.getRoleService();
        DelegateMember delegationMember = roleService.getDelegationMemberById(delegationMemberId);
        DelegateType delegation = roleService.getDelegateTypeByDelegationId(delegationMember.getDelegationId());

        orr.setDelegationTypeCode(delegation.getDelegationType().getCode());
        //orr.setRoleMember(roleMember);
        orr.setDelegateMember(roleMember,delegationMember);

        orr.setRoleRspActions(roleService.getRoleMemberResponsibilityActions(delegationMember.getRoleMemberId()));

        populateObjectExtras(orr);
    }

    protected void populateObjectExtras( OrgReviewRole orr ) {
        if( !orr.getRoleRspActions().isEmpty() ){
            orr.setActionTypeCode(orr.getRoleRspActions().get(0).getActionTypeCode());
            orr.setPriorityNumber(orr.getRoleRspActions().get(0).getPriorityNumber()==null?"":String.valueOf(orr.getRoleRspActions().get(0).getPriorityNumber()));
            orr.setActionPolicyCode(orr.getRoleRspActions().get(0).getActionPolicyCode());
            orr.setForceAction(orr.getRoleRspActions().get(0).isForceAction());
        }
    }

    @Override
    @Cacheable(value=OrgReviewRole.CACHE_NAME,key="'{ValidDocumentTypeForOrgReview}'+#p0")
    public boolean isValidDocumentTypeForOrgReview(String documentTypeName){
        if(StringUtils.isEmpty(documentTypeName)){
            return false;
        }

        return !getRolesToConsider(documentTypeName).isEmpty();
    }

    @Override
    public void validateDocumentType(String documentTypeName) throws ValidationException {
        if ( getRolesToConsider(documentTypeName).isEmpty() ) {
            GlobalVariables.getMessageMap().putError(OrgReviewRole.DOC_TYPE_NAME_FIELD_NAME, "error.document.orgReview.invalidDocumentType", documentTypeName);
        }
    }

    @Override
    @Cacheable(value=OrgReviewRole.CACHE_NAME,key="'{hasOrganizationHierarchy}'+#p0")
    public boolean hasOrganizationHierarchy(String documentTypeName) {
        if(StringUtils.isBlank(documentTypeName)) {
            return false;
        }
        return getDocumentTypeService().hasRouteNodeForDocumentTypeName(KFSConstants.RouteLevelNames.ORGANIZATION_HIERARCHY, documentTypeName);
    }

    @Override
    @Cacheable(value=OrgReviewRole.CACHE_NAME,key="'{hasAccountingOrganizationHierarchy}'+#p0")
    public boolean hasAccountingOrganizationHierarchy(String documentTypeName) {
        if(StringUtils.isBlank(documentTypeName)) {
            return false;
        }
        return getDocumentTypeService().hasRouteNodeForDocumentTypeName(KFSConstants.RouteLevelNames.ACCOUNTING_ORGANIZATION_HIERARCHY, documentTypeName);
    }

    @Override
    @Cacheable(value=OrgReviewRole.CACHE_NAME,key="'{ClosestOrgReviewRoleParentDocumentTypeName}'+#p0")
    public String getClosestOrgReviewRoleParentDocumentTypeName(final String documentTypeName){
        if(StringUtils.isBlank(documentTypeName)) {
            return null;
        }
        return KimCommonUtils.getClosestParentDocumentTypeName(getDocumentTypeService().getDocumentTypeByName(documentTypeName), potentialParentDocumentTypeNames);
    }

    /**
     *  1. Check WorkflowInfo.hasNode(documentTypeName, nodeName) to see if the document type selected has
     *  OrganizationHierarchy and/or AccountingOrganizationHierarchy - if it has either or both,
     *  set the Review Types radio group appropriately and make it read only.
     *  2. Else, if KFS is the document type selected, set the Review Types radio group to both and leave it editable.
     *  3. Else, if FinancialSystemTransactionalDocument is the closest parent (per KimCommonUtils.getClosestParent),
     *  set the Review Types radio group to Organization Accounting Only and leave it editable.
     *  4. Else, if FinancialSystemComplexMaintenanceDocument is the closest parent (per KimCommonUtils.getClosestParent),
     *  set the Review Types radio group to Organization Only and make read-only.
     *  5. Else, if FinancialSystemSimpleMaintenanceDocument is the closest parent (per KimCommonUtils.getClosestParent),
     *  this makes no sense and should generate an error.
     * @param documentTypeName
     * @param hasOrganizationHierarchy
     * @param hasAccountingOrganizationHierarchy
     * @param closestParentDocumentTypeName
     * @return
     */
    @Override
    @Cacheable(value=OrgReviewRole.CACHE_NAME,key="'{getRolesToConsider}'+#p0")
    public List<String> getRolesToConsider(String documentTypeName) throws ValidationException {
        List<String> rolesToConsider = new ArrayList<String>(2);
        if(StringUtils.isBlank(documentTypeName) || KFSConstants.ROOT_DOCUMENT_TYPE.equals(documentTypeName) ){
            rolesToConsider.add(KFSConstants.SysKimApiConstants.ORGANIZATION_REVIEWER_ROLE_NAME);
            rolesToConsider.add(KFSConstants.SysKimApiConstants.ACCOUNTING_REVIEWER_ROLE_NAME);
        } else {
            String closestParentDocumentTypeName = getClosestOrgReviewRoleParentDocumentTypeName(documentTypeName);
            if(documentTypeName.equals(KFSConstants.FINANCIAL_SYSTEM_TRANSACTIONAL_DOCUMENT)
                    || KFSConstants.FINANCIAL_SYSTEM_TRANSACTIONAL_DOCUMENT.equals(closestParentDocumentTypeName)) {
                rolesToConsider.add(KFSConstants.SysKimApiConstants.ACCOUNTING_REVIEWER_ROLE_NAME);
            } else {
                boolean hasOrganizationHierarchy = hasOrganizationHierarchy(documentTypeName);
                boolean hasAccountingOrganizationHierarchy = hasAccountingOrganizationHierarchy(documentTypeName);
                if(hasOrganizationHierarchy || documentTypeName.equals(KFSConstants.FINANCIAL_SYSTEM_COMPLEX_MAINTENANCE_DOCUMENT)
                        || KFSConstants.FINANCIAL_SYSTEM_COMPLEX_MAINTENANCE_DOCUMENT.equals(closestParentDocumentTypeName) ) {
                    rolesToConsider.add(KFSConstants.SysKimApiConstants.ORGANIZATION_REVIEWER_ROLE_NAME);
                }
                if(hasAccountingOrganizationHierarchy) {
                    rolesToConsider.add(KFSConstants.SysKimApiConstants.ACCOUNTING_REVIEWER_ROLE_NAME);
                }
            }
        }
        return rolesToConsider;
    }

    @Override
    public void saveOrgReviewRoleToKim( OrgReviewRole orr ) {
        if(orr.isDelegate() || orr.isCreateDelegation()){
            saveDelegateMemberToKim(orr);
        } else{
            saveRoleMemberToKim(orr);
        }
    }

    protected void updateDelegateMemberFromDocDelegateMember( DelegateMember.Builder member, KfsKimDocDelegateMember dm ) {
        member.setMemberId(dm.getMemberId());
        member.setType(dm.getType());
        member.setRoleMemberId(dm.getRoleMemberId());
        member.setAttributes(dm.getAttributes());
        member.setActiveFromDate(dm.getActiveFromDate());
        member.setActiveToDate(dm.getActiveToDate());
    }

    protected void saveDelegateMemberToKim( OrgReviewRole orr ) {
        if ( LOG.isDebugEnabled() ) {
            LOG.debug( "Saving delegate member from OrgReviewRole: " + orr );
        }
        RoleService roleService = KimApiServiceLocator.getRoleService();
        // Save delegation(s)
        List<KfsKimDocDelegateMember> delegationMembers = getDelegationMembersToSave(orr);

        for( KfsKimDocDelegateMember dm : delegationMembers ) {
            // retrieve the delegate type so it can be updated
            DelegationType delegationType = dm.getDelegationType();
            DelegateType delegateType = roleService.getDelegateTypeByRoleIdAndDelegateTypeCode(orr.getRoleId(), delegationType);
            // KIM always returns a non-null value even if it has never been persisted
            if ( delegateType == null || delegateType.getDelegationId() == null ) {
                DelegateType.Builder newDelegateType = DelegateType.Builder.create(orr.getRoleId(), delegationType, new ArrayList<DelegateMember.Builder>(1));
                // ensure this is set (for new delegation types)
                newDelegateType.setKimTypeId( orr.getKimTypeId());
                delegateType = roleService.createDelegateType(newDelegateType.build());
                if ( LOG.isDebugEnabled() ) {
                    LOG.debug("No DelegateType in KIM.  Created new one: " + delegateType);
                }
            } else {
                if ( LOG.isDebugEnabled() ) {
                    LOG.debug("Pulled DelegateType from KIM: " + delegateType);
                }
            }

            boolean foundExistingMember = false;
            DelegateMember addedMember = null;

            // check for an existing delegation member given its unique ID
            // if found, update that record
            if ( StringUtils.isNotBlank(dm.getDelegationMemberId()) ) {
                DelegateMember member = roleService.getDelegationMemberById(dm.getDelegationMemberId());
                if ( member != null ) {
                    foundExistingMember = true;
                    if ( LOG.isDebugEnabled() ) {
                        LOG.debug("Found existing delegate member - updating existing record. " + member);
                    }
                    DelegateMember.Builder updatedMember = DelegateMember.Builder.create(member);
                    // KFSMI-9628 : fixing issue with the delegate switch from primary to secondary
                    // IN this case, we need to delete the member from the "other" delegate type

                    // need to determine what the "existing" type was
                    DelegateType originalDelegateType = roleService.getDelegateTypeByDelegationId(member.getDelegationId());
                    // if they are the same, we can just update the existing record
                    if ( originalDelegateType.getDelegationType().equals(dm.getDelegationType()) ) {
                        updateDelegateMemberFromDocDelegateMember(updatedMember, dm);
                        addedMember = roleService.updateDelegateMember(updatedMember.build());
                    } else {
                        // Otherwise, we need to remove the old one and add a new one
                        // Remove old
                        roleService.removeDelegateMembers(Collections.singletonList(member));
                        // add new
                        DelegateMember.Builder newMember = DelegateMember.Builder.create();
                        newMember.setDelegationId(delegateType.getDelegationId());
                        updateDelegateMemberFromDocDelegateMember(newMember, dm);
                        addedMember = roleService.createDelegateMember(newMember.build());
                    }
                }
            }
            // if we did not find one, then we need to create a new member
            if ( !foundExistingMember ) {
                if ( LOG.isDebugEnabled() ) {
                    LOG.debug("No existing delegate member found, adding as a new delegate: " + dm);
                }
                DelegateMember.Builder newMember = DelegateMember.Builder.create();
                newMember.setDelegationId(delegateType.getDelegationId());
                updateDelegateMemberFromDocDelegateMember(newMember, dm);
                addedMember = roleService.createDelegateMember(newMember.build());
            }

            if ( addedMember != null ) {
                orr.setDelegationMemberId(addedMember.getDelegationMemberId());
            }
        }
    }

    protected void saveRoleMemberToKim( OrgReviewRole orr ) {
        if ( LOG.isDebugEnabled() ) {
            LOG.debug( "Saving role member from OrgReviewRole: " + orr );
        }
        RoleService roleService = KimApiServiceLocator.getRoleService();
        // Save role member(s)
        for( RoleMemberContract roleMember : getRoleMembers(orr) ) {
            List<RoleResponsibilityAction.Builder> roleRspActionsToSave = getRoleResponsibilityActions(orr, roleMember);
            // KFSCNTRB-1391
            RoleMemberQueryResults roleMembers = null;
            if (orr.isEdit()) {
                roleMembers = roleService.findRoleMembers(QueryByCriteria.Builder.fromPredicates( PredicateUtils.convertMapToPredicate(Collections.singletonMap(KimConstants.PrimaryKeyConstants.ID, roleMember.getId()))));
            }
            if ( orr.isEdit() && roleMembers != null && roleMembers.getResults() != null && !roleMembers.getResults().isEmpty() ) {
                RoleMember existingRoleMember = roleMembers.getResults().get(0);
                RoleMember.Builder updatedRoleMember = RoleMember.Builder.create(roleMember);
                updatedRoleMember.setVersionNumber(existingRoleMember.getVersionNumber());
                updatedRoleMember.setObjectId(existingRoleMember.getObjectId());
                roleMember = roleService.updateRoleMember( updatedRoleMember.build() );
            } else {
                RoleMember.Builder newRoleMember = RoleMember.Builder.create(roleMember);
                roleMember = roleService.createRoleMember( newRoleMember.build() );
            }
            for ( RoleResponsibilityAction.Builder rra : roleRspActionsToSave ) {
                // ensure linked to the right record
                rra.setRoleMemberId(roleMember.getId());
                if ( StringUtils.isBlank( rra.getId() ) ) {
                    roleService.createRoleResponsibilityAction(rra.build());
                } else {
                    roleService.updateRoleResponsibilityAction(rra.build());
                }
            }
            orr.setRoleMemberId(roleMember.getId());
            orr.setORMId(roleMember.getId());
        }
    }

    protected Role getRoleInfo( String roleName ) {
        if ( roleName == null ) {
            return null;
        }
        Role role = ROLE_CACHE.get(roleName);
        if ( role == null ) {
            role = KimApiServiceLocator.getRoleService().getRoleByNamespaceCodeAndName( KFSConstants.SysKimApiConstants.ORGANIZATION_REVIEWER_ROLE_NAMESPACECODE, roleName);
            synchronized ( ROLE_CACHE ) {
                ROLE_CACHE.put(roleName, role);
            }
        }
        return role;
    }

    protected List<KfsKimDocDelegateMember> getDelegationMembersToSave(OrgReviewRole orr){
        KfsKimDocDelegateMember delegationMember = null;
        if(orr.isEdit() && StringUtils.isNotBlank(orr.getDelegationMemberId())){
            delegationMember = new KfsKimDocDelegateMember( KimApiServiceLocator.getRoleService().getDelegationMemberById(orr.getDelegationMemberId()) );
        }

        if(delegationMember==null){
            delegationMember = new KfsKimDocDelegateMember();
            if(StringUtils.isNotEmpty(orr.getRoleMemberRoleNamespaceCode()) && StringUtils.isNotEmpty(orr.getRoleMemberRoleName())){
                String roleId = KimApiServiceLocator.getRoleService().getRoleIdByNamespaceCodeAndName(orr.getRoleMemberRoleNamespaceCode(), orr.getRoleMemberRoleName());
                delegationMember.setMemberId(roleId);
                delegationMember.setType(MemberType.ROLE);
            } else if(StringUtils.isNotEmpty(orr.getGroupMemberGroupNamespaceCode()) && StringUtils.isNotEmpty(orr.getGroupMemberGroupName())){
                Group groupInfo = KimApiServiceLocator.getGroupService().getGroupByNamespaceCodeAndName(orr.getGroupMemberGroupNamespaceCode(), orr.getGroupMemberGroupName());
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
        if ( orr.getActiveFromDate() != null ) {
            delegationMember.setActiveFromDate( new DateTime( orr.getActiveFromDate() ) );
        }
        if ( orr.getActiveToDate() != null ) {
            delegationMember.setActiveToDate( new DateTime( orr.getActiveToDate() ) );
        }
        delegationMember.setRoleMemberId(orr.getRoleMemberId());
        return Collections.singletonList(delegationMember);
    }

    protected KfsKimDocRoleMember getRoleMemberToSave(Role role, OrgReviewRole orr){
        KfsKimDocRoleMember roleMember = null;
        if ( orr.getPerson() != null ) {
            roleMember = new KfsKimDocRoleMember(role.getId(), MemberType.PRINCIPAL, orr.getPerson().getPrincipalId());
        } else if ( orr.getGroup() != null ) {
            roleMember = new KfsKimDocRoleMember(role.getId(), MemberType.GROUP, orr.getGroup().getId() );
        } else if( orr.getRole() != null ){
            roleMember = new KfsKimDocRoleMember(role.getId(), MemberType.ROLE, orr.getRole().getId() );
        }
        if ( roleMember != null ) {
            if(orr.isEdit()){
                roleMember.setId(orr.getRoleMemberId());
            }
            roleMember.setAttributes(getAttributes(orr, role.getKimTypeId()));
            if ( orr.getActiveFromDate() != null ) {
                roleMember.setActiveFromDate( new DateTime( orr.getActiveFromDate().getTime() ) );
            }
            if ( orr.getActiveToDate() != null ) {
                roleMember.setActiveToDate( new DateTime( orr.getActiveToDate().getTime() ) );
            }
        }
        return roleMember;
    }

    protected List<String> getRolesToSaveFor(List<String> roleNamesToConsider, String reviewRolesIndicator){
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
        for(String roleName: roleNamesToSaveFor){
            Role roleInfo = getRoleInfo(roleName);
            KfsKimDocRoleMember roleMemberToSave = getRoleMemberToSave(roleInfo, orr);
            if ( roleMemberToSave != null ) {
                objectsToSave.add(roleMemberToSave);
            }
        }
        return objectsToSave;
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

// JHK: Commented out as it is not needed at the moment.  If we decide that we need to link these to the
// exact responsibilities in the future, I didn't want anyone to have to re-invent the wheel.
//    protected List<RoleResponsibility> getResponsibilitiesWithRoleMemberLevelActions( String roleId ) {
//        List<RoleResponsibility> roleResponsibilities = KimApiServiceLocator.getRoleService().getRoleResponsibilities(roleId);
//        //Assuming that there is only 1 responsibility for both the org review roles
//        if ( roleResponsibilities == null || roleResponsibilities.isEmpty() ) {
//            throw new IllegalStateException("The Org Review Role id: " + roleId + " does not have any responsibilities associated with it");
//        }
//        List<RoleResponsibility> respWithRoleMemberActions = new ArrayList<RoleResponsibility>( roleResponsibilities.size() );
//        for ( RoleResponsibility rr : roleResponsibilities ) {
//            Responsibility r = KimApiServiceLocator.getResponsibilityService().getResponsibility(rr.getResponsibilityId());
//            if ( Boolean.parseBoolean( r.getAttributes().get(KimConstants.AttributeConstants.ACTION_DETAILS_AT_ROLE_MEMBER_LEVEL) ) ) {
//                respWithRoleMemberActions.add(rr);
//            }
//        }
//        return respWithRoleMemberActions;
//    }

    protected List<RoleResponsibilityAction.Builder> getRoleResponsibilityActions(OrgReviewRole orr, RoleMemberContract roleMember){
        List<RoleResponsibilityAction.Builder> roleResponsibilityActions = new ArrayList<RoleResponsibilityAction.Builder>(1);

        RoleResponsibilityAction.Builder rra = RoleResponsibilityAction.Builder.create();
        // if this is an existing role member, pull matching role resp action record (and set ID in object) so it can be updated
        // otherwise, it will be left blank and a new one will be created
        if ( StringUtils.isNotBlank( roleMember.getId() ) ) {
            List<RoleResponsibilityAction> origRoleRspActions = KimApiServiceLocator.getRoleService().getRoleMemberResponsibilityActions(roleMember.getId());
            if ( origRoleRspActions!=null && !origRoleRspActions.isEmpty() ) {
                rra.setId(origRoleRspActions.get(0).getId());
                rra.setVersionNumber(origRoleRspActions.get(0).getVersionNumber());
            }
        }

        rra.setRoleMemberId(roleMember.getId());
        rra.setRoleResponsibilityId("*");
        rra.setActionTypeCode(orr.getActionTypeCode());
        rra.setActionPolicyCode(orr.getActionPolicyCode());

        if(StringUtils.isNotBlank(orr.getPriorityNumber())){
            try{
                rra.setPriorityNumber(Integer.valueOf(orr.getPriorityNumber()));
            } catch(Exception nfx){
                rra.setPriorityNumber(null);
            }
        }
        rra.setForceAction(orr.isForceAction());
        roleResponsibilityActions.add(rra);
        return roleResponsibilityActions;
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


    protected DocumentTypeService getDocumentTypeService() {
        if ( documentTypeService == null ) {
            documentTypeService = KewApiServiceLocator.getDocumentTypeService();
        }
        return documentTypeService;
    }

}
