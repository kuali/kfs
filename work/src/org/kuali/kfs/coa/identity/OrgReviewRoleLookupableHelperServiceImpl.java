/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.coa.identity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.core.util.RiceUtilities;
import org.kuali.rice.kew.doctype.service.DocumentTypeService;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kew.service.WorkflowInfo;
import org.kuali.rice.kim.bo.entity.impl.KimPrincipalImpl;
import org.kuali.rice.kim.bo.group.impl.KimGroupImpl;
import org.kuali.rice.kim.bo.impl.KimAbstractMemberImpl;
import org.kuali.rice.kim.bo.role.impl.KimDelegationMemberAttributeDataImpl;
import org.kuali.rice.kim.bo.role.impl.KimDelegationMemberImpl;
import org.kuali.rice.kim.bo.role.impl.KimRoleImpl;
import org.kuali.rice.kim.bo.role.impl.RoleMemberAttributeDataImpl;
import org.kuali.rice.kim.bo.role.impl.RoleMemberImpl;
import org.kuali.rice.kim.bo.role.impl.RolePermissionImpl;
import org.kuali.rice.kim.bo.role.impl.RoleResponsibilityActionImpl;
import org.kuali.rice.kim.bo.role.impl.RoleResponsibilityImpl;
import org.kuali.rice.kim.bo.types.impl.KimAttributeDataImpl;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.RoleService;
import org.kuali.rice.kim.service.UiDocumentService;
import org.kuali.rice.kim.util.KimCommonUtils;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.LookupService;

public class OrgReviewRoleLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    private RoleService roleService;
    private LookupService lookupService;
    private DocumentTypeService documentTypeService;
    private UiDocumentService uiDocumentService;
    
    protected static final String WILDCARD = "*";
    protected static final String DOCUMENT_TYPE_NAME = KfsKimAttributes.FINANCIAL_SYSTEM_DOCUMENT_TYPE_CODE;
    protected static final String MEMBER_ATTRIBUTE_CHART_OF_ACCOUNTS_CODE = "chart.chartOfAccountsCode";
    protected static final String MEMBER_ATTRIBUTE_ORGANIZATION_CODE = "organization.organizationCode";
    protected static final String MEMBER_PRINCIPAL_NAME = "principal.principalName";
    protected static final String MEMBER_GROUP_NAMESPACE_CODE = "group.namespaceCode";
    protected static final String MEMBER_GROUP_NAME = "group.groupName";
    protected static final String MEMBER_ROLE_NAMESPACE = "role.namespaceCode";
    protected static final String MEMBER_ROLE_NAME = "role.roleName";

    protected static final String MEMBER_ID = "memberId";
    protected static final String MEMBER_ATTRIBUTE_KEY = "attributes.kimAttribute.attributeName";

    protected static final String DELEGATE = "delegate";
    protected static final String ACTIVE = "active";
    protected static final String ACTIVE_FROM_DATE = "activeFromDate";
    protected static final String ACTIVE_TO_DATE = "activeToDate";
    
    protected static final String FINANCIAL_SYSTEM_DOCUMENT = "KFS";
    protected static final String FINANCIAL_SYSTEM_TRANSACTIONAL_DOCUMENT = "FinancialSystemTransactionalDocument";
    protected static final String FINANCIAL_SYSTEM_COMPLEX_MAINTENANCE_DOCUMENT = "FinancialSystemComplexMaintenanceDocument";
    protected static final String FINANCIAL_SYSTEM_SIMPLE_MAINTENANCE_DOCUMENT = "FinancialSystemSimpleMaintenanceDocument";
    
    protected static final String NODE_NAME_ORGANIZATION_HIERARCHY = "OrganizationHierarchy";
    protected static final String NODE_NAME_ACCOUNTING_ORGANIZATION_HIERARCHY = "AccountingOrganizationHierarchy";

    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String,String> fieldValues) {
        return getMemberSearchResults(fieldValues);
    }

    /**
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResultsUnbounded(java.util.Map)
     */
    @Override 
    public List<? extends BusinessObject> getSearchResultsUnbounded(
            Map<String, String> fieldValues) {
        return getMemberSearchResults(fieldValues);
    }
    
    private List<? extends BusinessObject> getMemberSearchResults(Map<String, String> fieldValues){
        Map<String, String> searchCriteria = buildOrgReviewRoleSearchCriteria(fieldValues);
        if(searchCriteria == null)
            return new ArrayList<BusinessObject>();
        String documentTypeName = fieldValues.get(DOCUMENT_TYPE_NAME);
        /*
         * Document Type
            1.  Check WorkflowInfo.hasNode(documentTypeName, nodeName) to see if the document type selected
                    has OrganizationHierarchy and/or AccountingOrganizationHierarchy – if it has either or both, 
                    set the Review Types radio group appropriately and make it read only.
            2.  Else, if KFS is the document type selected, set the Review Types radio group to both and 
                    leave it editable.
            3.  Else, if FinancialSystemTransactionalDocument is the closest parent (per KimCommonUtils.getClosestParent), 
                    set the Review Types radio group to Organization Accounting Only and leave it editable.
            4.  Else, if FinancialSystemComplexMaintenanceDocument is the closest parent (per KimCommonUtils.getClosestParent), 
                    set the Review Types radio group to Organization Only and make read-only.
            5.  Else, if FinancialSystemSimpleMaintenanceDocument is the closest parent (per KimCommonUtils.getClosestParent), 
                    this makes no sense and should generate an error.
         */
        List<String> roleNamesToSearchInto = new ArrayList<String>();
        try{
            boolean hasOrganizationHierarchy = (new WorkflowInfo()).hasRouteNode(documentTypeName, NODE_NAME_ORGANIZATION_HIERARCHY);
            boolean hasAccountingOrganizationHierarchy = (new WorkflowInfo()).hasRouteNode(documentTypeName, NODE_NAME_ACCOUNTING_ORGANIZATION_HIERARCHY);
            if(hasOrganizationHierarchy || hasAccountingOrganizationHierarchy){
                if(hasOrganizationHierarchy) roleNamesToSearchInto.add(KFSConstants.SysKimConstants.ORGANIZATION_REVIEWER_ROLE_NAME);                
                if(hasAccountingOrganizationHierarchy) roleNamesToSearchInto.add(KFSConstants.SysKimConstants.ACCOUNTING_REVIEWER_ROLE_NAME);
            } else if(FINANCIAL_SYSTEM_DOCUMENT.equals(documentTypeName)){
                roleNamesToSearchInto.add(KFSConstants.SysKimConstants.ORGANIZATION_REVIEWER_ROLE_NAME);                
                roleNamesToSearchInto.add(KFSConstants.SysKimConstants.ACCOUNTING_REVIEWER_ROLE_NAME);
            } else{
                Set<String> potentialParentDocumentTypeNames = new HashSet<String>();
                potentialParentDocumentTypeNames.add(FINANCIAL_SYSTEM_TRANSACTIONAL_DOCUMENT);
                potentialParentDocumentTypeNames.add(FINANCIAL_SYSTEM_COMPLEX_MAINTENANCE_DOCUMENT);
                potentialParentDocumentTypeNames.add(FINANCIAL_SYSTEM_SIMPLE_MAINTENANCE_DOCUMENT);
                String closestParentDocumentTypeName = 
                    KimCommonUtils.getClosestParentDocumentTypeName(getDocumentTypeService().findByName(documentTypeName), potentialParentDocumentTypeNames);
                if(StringUtils.isNotEmpty(closestParentDocumentTypeName)){
                    if(closestParentDocumentTypeName.equals(FINANCIAL_SYSTEM_TRANSACTIONAL_DOCUMENT))
                        roleNamesToSearchInto.add(KFSConstants.SysKimConstants.ACCOUNTING_REVIEWER_ROLE_NAME);
                    else if(closestParentDocumentTypeName.equals(FINANCIAL_SYSTEM_TRANSACTIONAL_DOCUMENT))
                        roleNamesToSearchInto.add(KFSConstants.SysKimConstants.ORGANIZATION_REVIEWER_ROLE_NAME);
                    else if(closestParentDocumentTypeName.equals(FINANCIAL_SYSTEM_SIMPLE_MAINTENANCE_DOCUMENT))
                        throw new RuntimeException("Invalid document type chosen for Org Review Role: "+FINANCIAL_SYSTEM_SIMPLE_MAINTENANCE_DOCUMENT);
                }
            }
        } catch(WorkflowException wex){
            throw new RuntimeException("Workflow Exception occurred: "+wex);
        }
        boolean delegate = RiceUtilities.getBooleanValueForString(fieldValues.get(DELEGATE), false);
        Class classToSearch = RoleMemberImpl.class;
        if(delegate){
            classToSearch = KimDelegationMemberImpl.class;
        }
        if(searchCriteria==null)
            searchCriteria = new HashMap<String, String>();
        if(roleNamesToSearchInto!=null){
            StringBuffer rolesQueryString = new StringBuffer();
            for(String roleName: roleNamesToSearchInto){
                rolesQueryString.append(
                    getRoleService().getRoleIdByName(KFSConstants.SysKimConstants.ORGANIZATION_REVIEWER_ROLE_NAMESPACECODE, roleName)+
                    KimConstants.KimUIConstants.OR_OPERATOR);
            }
            if(rolesQueryString.toString().endsWith(KimConstants.KimUIConstants.OR_OPERATOR))
                rolesQueryString.delete(rolesQueryString.length()-KimConstants.KimUIConstants.OR_OPERATOR.length(), rolesQueryString.length());
            searchCriteria.put(KimConstants.PrimaryKeyConstants.ROLE_ID, rolesQueryString.toString());
        }
        return flattenToOrgReviewRoleMembers(delegate, searchMembers(searchCriteria, classToSearch));
    }

    private List<OrgReviewRole> flattenToOrgReviewRoleMembers(boolean delegate, List<? extends BusinessObject> members){
        List<OrgReviewRole> orgReviewRoleMembers = new ArrayList<OrgReviewRole>();
        OrgReviewRole orgReviewRole;
        String memberType;
        KimAbstractMemberImpl absMember;
        BusinessObject memberImpl;
        for(BusinessObject member: members){
            absMember = (KimAbstractMemberImpl)member;
            orgReviewRole = new OrgReviewRole();
            memberImpl = getUiDocumentService().getMember(absMember.getMemberTypeCode(), absMember.getMemberId());
            orgReviewRole.setMemberId(absMember.getMemberId());
            orgReviewRole.setMemberTypeCode(absMember.getMemberTypeCode());
            orgReviewRole.setActiveFromDate(absMember.getActiveFromDate());
            orgReviewRole.setActiveToDate(absMember.getActiveToDate());
            orgReviewRole.setMemberName(getUiDocumentService().getMemberName(absMember.getMemberTypeCode(), memberImpl));
            orgReviewRole.setMemberNamespaceCode(getUiDocumentService().getMemberNamespaceCode(absMember.getMemberTypeCode(), memberImpl));
            List<KimAttributeDataImpl> attributes = new ArrayList<KimAttributeDataImpl>();
            KimAttributeDataImpl attribute;
            if(member instanceof RoleMemberImpl){
                for(RoleMemberAttributeDataImpl roleMemberAttribute: ((RoleMemberImpl)member).getAttributes()){
                    attribute = new KimAttributeDataImpl();
                    KimCommonUtils.copyProperties(attribute, roleMemberAttribute);
                    attributes.add(attribute);
                }
                orgReviewRole.setRoleRspActions(getRoleRspActions((RoleMemberImpl)member));
            } else if(member instanceof KimDelegationMemberImpl){
                for(KimDelegationMemberAttributeDataImpl delegationMemberAttribute: ((KimDelegationMemberImpl)member).getAttributes()){
                    attribute = new KimAttributeDataImpl();
                    KimCommonUtils.copyProperties(attribute, delegationMemberAttribute);
                    attributes.add(attribute);
                }
            }
            orgReviewRole.setAttributes(attributes);
            if(delegate){
                //Set delegation type id... dicy
            }
            orgReviewRoleMembers.add(orgReviewRole);
        }
        return orgReviewRoleMembers;
    }
    
    private List getRoleRspActions(RoleMemberImpl roleMemberImpl){
        Map<String, String> criteria = new HashMap<String,String>(1);
        criteria.put(KimConstants.PrimaryKeyConstants.ROLE_ID, roleMemberImpl.getRoleId());
        List<RoleResponsibilityImpl> roleResponsibilities = ((List<RoleResponsibilityImpl>)getBusinessObjectService().findMatching(RoleResponsibilityImpl.class, criteria));
        //Assuming that there is only 1 responsibility for both the org review roles
        if(roleResponsibilities!=null && roleResponsibilities.size()>0){
            return getUiDocumentService().getRoleMemberResponsibilityActionImpls(roleMemberImpl.getRoleMemberId(), roleResponsibilities.get(0).getRoleResponsibilityId());
        }
        return null;
    }

    private List<? extends BusinessObject> searchMembers(Map<String, String> searchCriteria, Class classToSearch){
        return (List<? extends BusinessObject>)KNSServiceLocator.getLookupService().findCollectionBySearchHelper(
                classToSearch, searchCriteria, false);
    }

    protected String getQueryString(String parameter){
        if(StringUtils.isEmpty(parameter))
            return WILDCARD;
        else
            return WILDCARD+parameter+WILDCARD;
    }
    
    protected Map<String, String> buildOrgReviewRoleSearchCriteria(Map<String, String> fieldValues){
        String principalName = fieldValues.get(MEMBER_PRINCIPAL_NAME);
        Map<String, String> searchCriteria;
        List<KimPrincipalImpl> principals = null;
        if(StringUtils.isNotEmpty(principalName)){
            searchCriteria = new HashMap<String, String>();
            searchCriteria.put(KimConstants.PrimaryKeyConstants.PRINCIPAL_ID, WILDCARD+principalName+WILDCARD);
            principals = 
                (List<KimPrincipalImpl>)KNSServiceLocator.getLookupService().findCollectionBySearchUnbounded(KimPrincipalImpl.class, searchCriteria);
            if(principals==null || principals.isEmpty())
                return null;
        }
        String assignedToGroupNamespaceCode = fieldValues.get(MEMBER_GROUP_NAMESPACE_CODE);
        String assignedToGroupName = fieldValues.get(MEMBER_GROUP_NAME);
        List<KimGroupImpl> groupImpls = null;
        if(StringUtils.isNotEmpty(assignedToGroupNamespaceCode) && StringUtils.isEmpty(assignedToGroupName) ||
                StringUtils.isEmpty(assignedToGroupNamespaceCode) && StringUtils.isNotEmpty(assignedToGroupName) ||
                StringUtils.isNotEmpty(assignedToGroupNamespaceCode) && StringUtils.isNotEmpty(assignedToGroupName)){
            searchCriteria = new HashMap<String, String>();
            searchCriteria.put(KimConstants.UniqueKeyConstants.NAMESPACE_CODE, getQueryString(assignedToGroupNamespaceCode));
            searchCriteria.put(KimConstants.UniqueKeyConstants.GROUP_NAME, getQueryString(assignedToGroupName));
            groupImpls = 
                (List<KimGroupImpl>)KNSServiceLocator.getLookupService().findCollectionBySearchUnbounded(KimGroupImpl.class, searchCriteria);
            if(groupImpls==null || groupImpls.size()==0)
                return null;
        }

        String assignedToRoleNamespaceCode = fieldValues.get(MEMBER_ROLE_NAMESPACE);
        String assignedToRoleName = fieldValues.get(MEMBER_ROLE_NAME);

        searchCriteria = new HashMap<String, String>();
        if(StringUtils.isNotEmpty(assignedToRoleNamespaceCode))
            searchCriteria.put(MEMBER_ROLE_NAMESPACE, WILDCARD+assignedToRoleNamespaceCode+WILDCARD);
        if(StringUtils.isNotEmpty(assignedToRoleName))
            searchCriteria.put(MEMBER_ROLE_NAME, WILDCARD+assignedToRoleName+WILDCARD);
        List<KimRoleImpl> roleImpls = null;
        if(StringUtils.isNotEmpty(assignedToRoleNamespaceCode) && StringUtils.isEmpty(assignedToRoleName) ||
                StringUtils.isEmpty(assignedToRoleNamespaceCode) && StringUtils.isNotEmpty(assignedToRoleName) ||
                StringUtils.isNotEmpty(assignedToRoleNamespaceCode) && StringUtils.isNotEmpty(assignedToRoleName)){
            searchCriteria = new HashMap<String, String>();
            searchCriteria.put(KimConstants.UniqueKeyConstants.NAMESPACE_CODE, getQueryString(assignedToRoleNamespaceCode));
            searchCriteria.put(KimConstants.UniqueKeyConstants.ROLE_NAME, getQueryString(assignedToRoleName));
            roleImpls = 
                (List<KimRoleImpl>)KNSServiceLocator.getLookupService().findCollectionBySearchUnbounded(KimRoleImpl.class, searchCriteria);
            if(roleImpls==null || roleImpls.size()==0)
                return null;
        }

        String chartOfAccountsCode = fieldValues.get(MEMBER_ATTRIBUTE_CHART_OF_ACCOUNTS_CODE);
        if(StringUtils.isNotBlank(chartOfAccountsCode)){
            searchCriteria.put(MEMBER_ATTRIBUTE_KEY, getQueryString(assignedToRoleNamespaceCode));
        }
        String organizationCode = fieldValues.get(MEMBER_ATTRIBUTE_ORGANIZATION_CODE);
        if(StringUtils.isNotBlank(organizationCode)){
            searchCriteria.put(MEMBER_ATTRIBUTE_KEY, getQueryString(assignedToRoleNamespaceCode));
        }
        StringBuffer memberQueryString = null;
        if(principals!=null){
            memberQueryString = new StringBuffer();
            for(KimPrincipalImpl principal: principals){
                memberQueryString.append(principal.getPrincipalId()+KimConstants.KimUIConstants.OR_OPERATOR);
            }
            if(memberQueryString.toString().endsWith(KimConstants.KimUIConstants.OR_OPERATOR))
                memberQueryString.delete(memberQueryString.length()-KimConstants.KimUIConstants.OR_OPERATOR.length(), memberQueryString.length());
            searchCriteria.put(MEMBER_ID, memberQueryString.toString());
        }
        if(groupImpls!=null){
            if(memberQueryString==null)
                memberQueryString = new StringBuffer();
            else if(StringUtils.isNotEmpty(memberQueryString.toString()))
                memberQueryString.append(KimConstants.KimUIConstants.OR_OPERATOR);
            for(KimGroupImpl group: groupImpls){
                memberQueryString.append(group.getGroupId()+KimConstants.KimUIConstants.OR_OPERATOR);
            }
            if(memberQueryString.toString().endsWith(KimConstants.KimUIConstants.OR_OPERATOR))
                memberQueryString.delete(memberQueryString.length()-KimConstants.KimUIConstants.OR_OPERATOR.length(), memberQueryString.length());
            searchCriteria.put(MEMBER_ID, memberQueryString.toString());
        }
        if(roleImpls!=null){
            if(memberQueryString==null)
                memberQueryString = new StringBuffer();
            else if(StringUtils.isNotEmpty(memberQueryString.toString()))
                memberQueryString.append(KimConstants.KimUIConstants.OR_OPERATOR);
            for(KimRoleImpl role: roleImpls){
                memberQueryString.append(role.getRoleId()+KimConstants.KimUIConstants.OR_OPERATOR);
            }
            if(memberQueryString.toString().endsWith(KimConstants.KimUIConstants.OR_OPERATOR))
                memberQueryString.delete(memberQueryString.length()-KimConstants.KimUIConstants.OR_OPERATOR.length(), memberQueryString.length());
            searchCriteria.put(MEMBER_ID, memberQueryString.toString());
        }

        /*boolean active = RiceUtilities.getBooleanValueForString(fieldValues.get(ACTIVE), true);
        if(active){
            searchCriteria.put(ACTIVE_FROM_DATE, "<= getDate()");
        }*/
        return searchCriteria;
    }

    /**
     * @return the lookupService
     */
    public LookupService getLookupService() {
        return this.lookupService;
    }

    /**
     * @param lookupService the lookupService to set
     */
    public void setLookupService(LookupService lookupService) {
        this.lookupService = lookupService;
    }

    /**
     * @return the roleService
     */
    public RoleService getRoleService() {
        if(roleService == null){
            roleService = KIMServiceLocator.getRoleService();
        }
        return this.roleService;
    }

    /**
     * @param roleService the roleService to set
     */
    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

    public DocumentTypeService getDocumentTypeService() {
        if ( documentTypeService == null ) {
            documentTypeService = KEWServiceLocator.getDocumentTypeService();
        }
        return this.documentTypeService;
    }

    public UiDocumentService getUiDocumentService() {
        if ( uiDocumentService == null ) {
            uiDocumentService = KIMServiceLocator.getUiDocumentService();
        }
        return uiDocumentService;
    }

}