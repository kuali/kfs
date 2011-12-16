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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.identity.KfsKimDocumentAttributeData;
import org.kuali.kfs.coa.identity.OrgReviewRole;
import org.kuali.kfs.coa.service.OrgReviewRoleService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.core.api.criteria.PredicateUtils;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.kew.api.doctype.DocumentType;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.common.delegate.DelegateMember;
import org.kuali.rice.kim.api.common.delegate.DelegateType;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.role.RoleMember;
import org.kuali.rice.kim.api.role.RoleMemberQueryResults;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.api.type.KimType;
import org.kuali.rice.kim.util.KimCommonUtils;
import org.kuali.rice.krad.exception.ValidationException;
import org.springframework.cache.annotation.Cacheable;

public class OrgReviewRoleServiceImpl implements OrgReviewRoleService {

    protected static final String DOCUMENT_TYPE_HIERARCHY_CACHE_NAME = "OrgReviewRoleServiceDocTypeHierarchy";
    
    protected DocumentTypeService documentTypeService;
    
    public void populateOrgReviewRoleFromRoleMember(OrgReviewRole orr, String roleMemberId) {
        RoleMemberQueryResults roleMembers = KimApiServiceLocator.getRoleService().findRoleMembers(QueryByCriteria.Builder.fromPredicates( PredicateUtils.convertMapToPredicate(Collections.singletonMap(KimConstants.PrimaryKeyConstants.ROLE_MEMBER_ID, roleMemberId))));
        RoleMember roleMember = null;
        if(roleMembers!=null && !roleMembers.getResults().isEmpty() ){
            roleMember = roleMembers.getResults().iterator().next();
        }
        orr.setRoleMemberId(roleMember.getId());
        orr.setKimDocumentRoleMember(roleMember);

        Role roleInfo = KimApiServiceLocator.getRoleService().getRole(roleMember.getRoleId());
        KimType typeInfo = KimApiServiceLocator.getKimTypeInfoService().getKimType(roleInfo.getKimTypeId());
        List<KfsKimDocumentAttributeData> attributes = orr.getAttributeSetAsQualifierList(typeInfo, roleMember.getAttributes());
        orr.setAttributes(attributes);
        orr.setRoleRspActions(KimApiServiceLocator.getRoleService().getRoleMemberResponsibilityActions(roleMember.getId()));
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
        orr.setRoleRspActions(KimApiServiceLocator.getRoleService().getRoleMemberResponsibilityActions(delegationMember.getRoleMemberId()));
        orr.setAttributes(orr.getAttributeSetAsQualifierList(typeInfo, delegationMember.getAttributes()));
        orr.setRoleId(delegation.getRoleId());
        orr.setDelegationTypeCode(delegation.getDelegationType().getCode());
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
//        if(orr.getPerson()!=null){
//            orr.setPrincipalMemberPrincipalId(orr.getPerson().getPrincipalId());
//            orr.setPrincipalMemberPrincipalName(orr.getPerson().getPrincipalName());
//        }
//        // RICE20
//        // FIXME : this is using the wrong role
//        if(orr.getRole()!=null){
//            orr.setRoleMemberRoleId(orr.getRole().getId());
//            orr.setRoleMemberRoleNamespaceCode(orr.getRole().getNamespaceCode());
//            orr.setRoleMemberRoleName(orr.getRole().getName());
//        }
//        if(orr.getGroup()!=null){
//            orr.setGroupMemberGroupId(orr.getGroup().getId());
//            orr.setGroupMemberGroupNamespaceCode(orr.getGroup().getNamespaceCode());
//            orr.setGroupMemberGroupName(orr.getGroup().getName());
//        }
    }
    
    public boolean isValidDocumentTypeForOrgReview(String documentTypeName){

        boolean isValid = true;
        
        if(StringUtils.isEmpty(documentTypeName)){
            return false;        
        }

        String closestParentDocumentTypeName = getClosestOrgReviewRoleParentDocumentTypeName(documentTypeName);
        boolean hasOrganizationHierarchy = hasOrganizationHierarchy(documentTypeName);
        boolean hasAccountingOrganizationHierarchy = hasAccountingOrganizationHierarchy(documentTypeName);
    
        if(documentTypeName.equals(KFSConstants.FINANCIAL_SYSTEM_TRANSACTIONAL_DOCUMENT) || KFSConstants.FINANCIAL_SYSTEM_TRANSACTIONAL_DOCUMENT.equals(closestParentDocumentTypeName)){
            //valid
        }else if(hasOrganizationHierarchy || hasAccountingOrganizationHierarchy){
          //valid
        } else if(KFSConstants.ROOT_DOCUMENT_TYPE.equals(documentTypeName)){
          //valid
        } else{
            if(documentTypeName.equals(KFSConstants.FINANCIAL_SYSTEM_COMPLEX_MAINTENANCE_DOCUMENT) || KFSConstants.FINANCIAL_SYSTEM_COMPLEX_MAINTENANCE_DOCUMENT.equals(closestParentDocumentTypeName)){
              //valid
            }else if(currentDocTypeAndChildrenHaveZeroOrgAndAccountReviewRoles(documentTypeName)){
                isValid = false;
            }
        }

        return isValid;
    }

    public void validateDocumentType(String documentTypeName) throws ValidationException {
        try{
            getRolesToConsider(documentTypeName);
        } catch(Exception ex){
            throw new ValidationException(ex.getMessage());
        }
    }

    public boolean hasOrganizationHierarchy(final String documentTypeName) {
        if(StringUtils.isBlank(documentTypeName)) {
            return false;
        }
        return documentTypeService.hasRouteNodeForDocumentTypeName(KFSConstants.RouteLevelNames.ORGANIZATION_HIERARCHY, documentTypeName);
    }

    public boolean hasAccountingOrganizationHierarchy(final String documentTypeName) {
        if(StringUtils.isBlank(documentTypeName)) {
            return false;
        }
        return documentTypeService.hasRouteNodeForDocumentTypeName(KFSConstants.RouteLevelNames.ACCOUNTING_ORGANIZATION_HIERARCHY, documentTypeName);
    }

    Set<String> potentialParentDocumentTypeNames = new HashSet<String>();
    {
        potentialParentDocumentTypeNames.add(KFSConstants.FINANCIAL_SYSTEM_TRANSACTIONAL_DOCUMENT);
        potentialParentDocumentTypeNames.add(KFSConstants.FINANCIAL_SYSTEM_COMPLEX_MAINTENANCE_DOCUMENT);
        potentialParentDocumentTypeNames.add(KFSConstants.FINANCIAL_SYSTEM_SIMPLE_MAINTENANCE_DOCUMENT);
        potentialParentDocumentTypeNames = Collections.unmodifiableSet(potentialParentDocumentTypeNames);
    }
    
    public String getClosestOrgReviewRoleParentDocumentTypeName(final String documentTypeName){
        if(StringUtils.isBlank(documentTypeName)) {
            return null;
        }
        return KimCommonUtils.getClosestParentDocumentTypeName(documentTypeService.getDocumentTypeByName(documentTypeName), potentialParentDocumentTypeNames);
    }

    public List<String> getRolesToConsider(String documentTypeName) throws ValidationException {
        return getRolesToConsider(documentTypeName, 
                hasOrganizationHierarchy(documentTypeName), 
                hasAccountingOrganizationHierarchy(documentTypeName), 
                getClosestOrgReviewRoleParentDocumentTypeName(documentTypeName));
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
    public List<String> getRolesToConsider(String documentTypeName, boolean hasOrganizationHierarchy, boolean hasAccountingOrganizationHierarchy, String closestParentDocumentTypeName) throws ValidationException {
        if(StringUtils.isBlank(documentTypeName) || KFSConstants.ROOT_DOCUMENT_TYPE.equals(documentTypeName) ){
            List<String> roleToConsider = new ArrayList<String>();
            roleToConsider.add(KFSConstants.SysKimApiConstants.ORGANIZATION_REVIEWER_ROLE_NAME);                
            roleToConsider.add(KFSConstants.SysKimApiConstants.ACCOUNTING_REVIEWER_ROLE_NAME);
            return roleToConsider;
        }

        List<String> roleToConsider = new ArrayList<String>();
        if(documentTypeName.equals(KFSConstants.FINANCIAL_SYSTEM_TRANSACTIONAL_DOCUMENT) 
                || KFSConstants.FINANCIAL_SYSTEM_TRANSACTIONAL_DOCUMENT.equals(closestParentDocumentTypeName)) {
            roleToConsider.add(KFSConstants.SysKimApiConstants.ACCOUNTING_REVIEWER_ROLE_NAME);
        } else if(hasOrganizationHierarchy || hasAccountingOrganizationHierarchy){
            if(hasOrganizationHierarchy) {
                roleToConsider.add(KFSConstants.SysKimApiConstants.ORGANIZATION_REVIEWER_ROLE_NAME);                
            }
            if(hasAccountingOrganizationHierarchy) {
                roleToConsider.add(KFSConstants.SysKimApiConstants.ACCOUNTING_REVIEWER_ROLE_NAME);
            }
        } else{
            if(documentTypeName.equals(KFSConstants.FINANCIAL_SYSTEM_COMPLEX_MAINTENANCE_DOCUMENT) 
                    || KFSConstants.FINANCIAL_SYSTEM_COMPLEX_MAINTENANCE_DOCUMENT.equals(closestParentDocumentTypeName)) {
                roleToConsider.add(KFSConstants.SysKimApiConstants.ORGANIZATION_REVIEWER_ROLE_NAME);
            } else if(currentDocTypeAndChildrenHaveZeroOrgAndAccountReviewRoles(documentTypeName)){
                throw new ValidationException("Invalid document type chosen for Organization Review: " + documentTypeName);
            }
        }

        return roleToConsider;
    }
    
    @Cacheable(value=DOCUMENT_TYPE_HIERARCHY_CACHE_NAME,key="#p0")
    public boolean currentDocTypeAndChildrenHaveZeroOrgAndAccountReviewRoles(String currentDocumentTypeName){     
        boolean hasZeroQualifyingNodes = true;

        //check current doc type for qualifying nodes
        if(hasOrganizationHierarchy(currentDocumentTypeName) 
                || hasAccountingOrganizationHierarchy(currentDocumentTypeName) ){                       
            hasZeroQualifyingNodes = false;        
        }
        
        //if still has no qualifying nodes, check current nodes children
        if(hasZeroQualifyingNodes){
            DocumentType currentDocType = documentTypeService.getDocumentTypeByName(currentDocumentTypeName);
            List<DocumentType> docTypes = KEWServiceLocator.getDocumentTypeService().getChildDocumentTypes(currentDocType.getId());
            
            for(DocumentType docType : docTypes){
                hasZeroQualifyingNodes &= currentDocTypeAndChildrenHaveZeroOrgAndAccountReviewRoles(docType.getName());
                if(hasZeroQualifyingNodes == false) break;
            }
        }
        
        return hasZeroQualifyingNodes;
    }
    
    
    public void setDocumentTypeService(DocumentTypeService documentTypeService) {
        this.documentTypeService = documentTypeService;
    }
    

}
