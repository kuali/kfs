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
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.cam.CamsConstants;
import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
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
import org.kuali.rice.kim.bo.impl.RoleImpl;
import org.kuali.rice.kim.bo.role.dto.KimRoleInfo;
import org.kuali.rice.kim.bo.role.impl.KimDelegationImpl;
import org.kuali.rice.kim.bo.role.impl.KimDelegationMemberAttributeDataImpl;
import org.kuali.rice.kim.bo.role.impl.KimDelegationMemberImpl;
import org.kuali.rice.kim.bo.role.impl.RoleMemberAttributeDataImpl;
import org.kuali.rice.kim.bo.role.impl.RoleMemberImpl;
import org.kuali.rice.kim.bo.role.impl.RoleResponsibilityImpl;
import org.kuali.rice.kim.bo.types.impl.KimAttributeDataImpl;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.RoleService;
import org.kuali.rice.kim.service.UiDocumentService;
import org.kuali.rice.kim.util.KimCommonUtils;
import org.kuali.rice.kim.util.KimConstants;
import org.kuali.rice.kns.authorization.BusinessObjectRestrictions;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.exception.ValidationException;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.LookupService;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.util.UrlFactory;
import org.kuali.rice.kns.web.struts.form.LookupForm;

public class OrgReviewRoleLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    private RoleService roleService;
    private LookupService lookupService;
    private DocumentTypeService documentTypeService;
    private UiDocumentService uiDocumentService;
    
    protected static final String WILDCARD = "*";
    protected static final String DOCUMENT_TYPE_NAME = KfsKimAttributes.FINANCIAL_SYSTEM_DOCUMENT_TYPE_CODE;
    public static final String MEMBER_ATTRIBUTE_CHART_OF_ACCOUNTS_CODE = "chart.chartOfAccountsCode";
    public static final String MEMBER_ATTRIBUTE_ORGANIZATION_CODE = "organization.organizationCode";
    protected static final String MEMBER_PRINCIPAL_NAME = "principalMemberPrincipalName";
    protected static final String MEMBER_GROUP_NAMESPACE_CODE = "groupMemberGroupNamespaceCode";
    protected static final String MEMBER_GROUP_NAME = "groupMemberGroupName";
    protected static final String MEMBER_ROLE_NAMESPACE = "roleMemberRoleNamespaceCode";
    protected static final String MEMBER_ROLE_NAME = "roleMemberRoleName";

    protected static final String MEMBER_ID = "memberId";
    protected static final String MEMBER_ATTRIBUTE_NAME_KEY = "attributes.kimAttribute.attributeName";
    protected static final String MEMBER_ATTRIBUTE_VALUE_KEY = "attributes.attributeValue";

    protected static final String DELEGATE = "delegate";
    protected static final String ACTIVE = "active";
    protected static final String ACTIVE_FROM_DATE = "activeFromDate";
    protected static final String ACTIVE_TO_DATE = "activeToDate";
    
    @Override
    public Collection performLookup(LookupForm lookupForm, Collection resultTable, boolean bounded) {
        lookupForm.setShowMaintenanceLinks(true);
        lookupForm.setSuppressActions(false);
        lookupForm.setHideReturnLink(true);
        return super.performLookup(lookupForm, resultTable, bounded);
    }

    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames){
        OrgReviewRole orr = (OrgReviewRole)businessObject;
        List<HtmlData> htmlDataList = super.getCustomActionUrls(businessObject, pkNames);
        if(StringUtils.isNotBlank(getMaintenanceDocumentTypeName()) && allowsMaintenanceEditAction(businessObject) && !orr.isDelegate()) {
            HtmlData createDelegationUrl = getUrlData(businessObject, KNSConstants.MAINTENANCE_EDIT_METHOD_TO_CALL, KFSConstants.COAConstants.ORG_REVIEW_ROLE_CREATE_DELEGATION_DISPLAY_TEXT, pkNames);
            //createDelegationUrl.setDisplayText(KFSConstants.COAConstants.ORG_REVIEW_ROLE_CREATE_DELEGATION_DISPLAY_TEXT);
            htmlDataList.add(createDelegationUrl);
        }
        return htmlDataList;
    }

    @Override
    protected AnchorHtmlData getUrlData(BusinessObject businessObject, String methodToCall, String displayText, List pkNames){
        if(KFSConstants.COAConstants.ORG_REVIEW_ROLE_CREATE_DELEGATION_DISPLAY_TEXT.equals(displayText)){
            return getCreateDelegationUrl(businessObject, pkNames);
        } else
            return super.getUrlData(businessObject, methodToCall, displayText, pkNames);
    }
    
    private AnchorHtmlData getCreateDelegationUrl(BusinessObject businessObject, List pkNames){
        OrgReviewRole orr = (OrgReviewRole)businessObject;
        Properties parameters = new Properties();
        parameters.put(OrgReviewRole.ORIGINAL_DELEGATION_MEMBER_ID_TO_MODIFY, OrgReviewRole.NEW_DELEGATION_ID_KEY_VALUE);
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KNSConstants.MAINTENANCE_EDIT_METHOD_TO_CALL);
        parameters.put(KNSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, businessObject.getClass().getName());
        parameters.put(KNSConstants.COPY_KEYS, OrgReviewRole.ORIGINAL_DELEGATION_MEMBER_ID_TO_MODIFY+","+OrgReviewRole.ORIGINAL_ROLE_MEMBER_ID_TO_MODIFY+","+KNSConstants.DISPATCH_REQUEST_PARAMETER);
        parameters.put(OrgReviewRole.ORIGINAL_ROLE_MEMBER_ID_TO_MODIFY, orr.getRoleMemberId());
        //parameters.putAll(getParametersFromPrimaryKey(businessObject, pkNames));
        String href = UrlFactory.parameterizeUrl(KNSConstants.MAINTENANCE_ACTION, parameters);

        return new AnchorHtmlData(href, KNSConstants.DOC_HANDLER_METHOD, KFSConstants.COAConstants.ORG_REVIEW_ROLE_CREATE_DELEGATION_DISPLAY_TEXT);
    }

    protected String getActionUrlHref(BusinessObject businessObject, String methodToCall, List pkNames){
        OrgReviewRole orr = (OrgReviewRole)businessObject;
        Properties parameters = new Properties();
        parameters.put(KNSConstants.DISPATCH_REQUEST_PARAMETER, methodToCall);
        parameters.put(KNSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, OrgReviewRole.class.getName());

        if(orr.isDelegate()){
            parameters.put(KNSConstants.COPY_KEYS, OrgReviewRole.ORIGINAL_DELEGATION_MEMBER_ID_TO_MODIFY+","+KNSConstants.DISPATCH_REQUEST_PARAMETER);
            parameters.put(OrgReviewRole.ORIGINAL_DELEGATION_MEMBER_ID_TO_MODIFY, orr.getDelegationMemberId());
        } else {
            parameters.put(KNSConstants.COPY_KEYS, OrgReviewRole.ORIGINAL_DELEGATION_MEMBER_ID_TO_MODIFY+","+OrgReviewRole.ORIGINAL_ROLE_MEMBER_ID_TO_MODIFY+","+KNSConstants.DISPATCH_REQUEST_PARAMETER);
            parameters.put(OrgReviewRole.ORIGINAL_ROLE_MEMBER_ID_TO_MODIFY, orr.getRoleMemberId());
        }
        return UrlFactory.parameterizeUrl(KNSConstants.MAINTENANCE_ACTION, parameters);
    }

    private List<String> getOverridePKNames(OrgReviewRole orr){
        List overridePKNames = new ArrayList<String>();
        if(orr.isDelegate())
            overridePKNames.add(KimConstants.PrimaryKeyConstants.DELEGATION_MEMBER_ID);
        else
            overridePKNames.add(KimConstants.PrimaryKeyConstants.ROLE_MEMBER_ID);
        return overridePKNames;
    }
    
    protected String getActionUrlTitleText(BusinessObject businessObject, String displayText, List pkNames, BusinessObjectRestrictions businessObjectRestrictions){
        OrgReviewRole orr = (OrgReviewRole)businessObject;
        return super.getActionUrlTitleText(businessObject, displayText, getOverridePKNames(orr), businessObjectRestrictions);
    }
    
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
        List<String> roleNamesToSearchInto = getRolesToConsider(documentTypeName);
        
        String delegateStr = fieldValues.get(DELEGATE);
        List<Class> classesToSearch = new ArrayList<Class>();
        if(StringUtils.isEmpty(delegateStr)){
            classesToSearch.add(RoleMemberImpl.class);
            classesToSearch.add(KimDelegationMemberImpl.class);
        } else{
            boolean delegate = RiceUtilities.getBooleanValueForString(delegateStr, false);
            if(delegate)
                classesToSearch.add(KimDelegationMemberImpl.class);
            else
                classesToSearch.add(RoleMemberImpl.class);
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
        return flattenToOrgReviewMembers(fieldValues.get(ACTIVE), documentTypeName, searchMembers(searchCriteria, classesToSearch));
    }

    public boolean hasOrganizationHierarchy(String documentTypeName) {
        if(StringUtils.isEmpty(documentTypeName)) return false;
        try {
            return (new WorkflowInfo()).hasRouteNode(documentTypeName, KFSConstants.COAConstants.NODE_NAME_ORGANIZATION_HIERARCHY);
        } catch(WorkflowException wex){
            throw new RuntimeException("Workflow Exception occurred: "+wex);
        }
    }

    public boolean hasAccountingOrganizationHierarchy(String documentTypeName) {
        if(StringUtils.isEmpty(documentTypeName)) return false;
        try{ 
            return (new WorkflowInfo()).hasRouteNode(documentTypeName, KFSConstants.COAConstants.NODE_NAME_ACCOUNTING_ORGANIZATION_HIERARCHY);
        } catch(WorkflowException wex){
            throw new RuntimeException("Workflow Exception occurred: "+wex);
        }
    }

    public String getClosestOrgReviewRoleParentDocumentTypeName(String documentTypeName){
        if(StringUtils.isEmpty(documentTypeName)) return null;
        Set<String> potentialParentDocumentTypeNames = new HashSet<String>();
        potentialParentDocumentTypeNames.add(KFSConstants.COAConstants.FINANCIAL_SYSTEM_TRANSACTIONAL_DOCUMENT);
        potentialParentDocumentTypeNames.add(KFSConstants.COAConstants.FINANCIAL_SYSTEM_COMPLEX_MAINTENANCE_DOCUMENT);
        potentialParentDocumentTypeNames.add(KFSConstants.COAConstants.FINANCIAL_SYSTEM_SIMPLE_MAINTENANCE_DOCUMENT);
        return KimCommonUtils.getClosestParentDocumentTypeName(getDocumentTypeService().findByName(documentTypeName), potentialParentDocumentTypeNames);
    }

    public List<String> getRolesToConsider(String documentTypeName) {
        return getRolesToConsider(documentTypeName, 
                hasOrganizationHierarchy(documentTypeName), 
                hasAccountingOrganizationHierarchy(documentTypeName), 
                getClosestOrgReviewRoleParentDocumentTypeName(documentTypeName));
    }
    
    public List<String> getRolesToConsider(String documentTypeName, boolean hasOrganizationHierarchy, boolean hasAccountingOrganizationHierarchy, String closestParentDocumentTypeName){
        if(StringUtils.isEmpty(documentTypeName)) return null;

        List<String> roleToConsider = new ArrayList<String>();
        if(hasOrganizationHierarchy || hasAccountingOrganizationHierarchy){
            if(hasOrganizationHierarchy) roleToConsider.add(KFSConstants.SysKimConstants.ORGANIZATION_REVIEWER_ROLE_NAME);                
            if(hasAccountingOrganizationHierarchy) roleToConsider.add(KFSConstants.SysKimConstants.ACCOUNTING_REVIEWER_ROLE_NAME);
            //readonly
        } else if(KFSConstants.COAConstants.FINANCIAL_SYSTEM_DOCUMENT.equals(documentTypeName)){
            roleToConsider.add(KFSConstants.SysKimConstants.ORGANIZATION_REVIEWER_ROLE_NAME);                
            roleToConsider.add(KFSConstants.SysKimConstants.ACCOUNTING_REVIEWER_ROLE_NAME);
        } else{
            if(StringUtils.isNotEmpty(closestParentDocumentTypeName)){
                if(closestParentDocumentTypeName.equals(KFSConstants.COAConstants.FINANCIAL_SYSTEM_TRANSACTIONAL_DOCUMENT))
                    roleToConsider.add(KFSConstants.SysKimConstants.ACCOUNTING_REVIEWER_ROLE_NAME);
                else if(closestParentDocumentTypeName.equals(KFSConstants.COAConstants.FINANCIAL_SYSTEM_COMPLEX_MAINTENANCE_DOCUMENT)){
                    roleToConsider.add(KFSConstants.SysKimConstants.ORGANIZATION_REVIEWER_ROLE_NAME);
                    //readonly
                } else if(closestParentDocumentTypeName.equals(KFSConstants.COAConstants.FINANCIAL_SYSTEM_SIMPLE_MAINTENANCE_DOCUMENT))
                    throw new RuntimeException("Invalid document type chosen for Org Review Role: "+KFSConstants.COAConstants.FINANCIAL_SYSTEM_SIMPLE_MAINTENANCE_DOCUMENT);
            }
        }

        return roleToConsider;
    }
    
    private List<OrgReviewRole> flattenToOrgReviewMembers(String active, String documentTypeName, List<? extends BusinessObject> members){
        List<OrgReviewRole> orgReviewRoles = new ArrayList<OrgReviewRole>();
        OrgReviewRole orgReviewRole;
        String memberType;
        KimAbstractMemberImpl absMember;
        KimRoleInfo roleInfo;
        BusinessObject memberImpl;
        Boolean activeInd = null;
        if(StringUtils.isNotEmpty(active)){
            activeInd = new Boolean(RiceUtilities.getBooleanValueForString(active, true));
        }
        for(BusinessObject member: members){
            absMember = (KimAbstractMemberImpl)member;
            if(activeInd==null || (activeInd.booleanValue()==true && absMember.isActive()) || (activeInd.booleanValue()==false && !absMember.isActive())){
                orgReviewRole = new OrgReviewRole();
                memberImpl = getUiDocumentService().getMember(absMember.getMemberTypeCode(), absMember.getMemberId());
                orgReviewRole.setMemberId(absMember.getMemberId());
                orgReviewRole.setMemberTypeCode(absMember.getMemberTypeCode());
                orgReviewRole.setActiveFromDate(absMember.getActiveFromDate());
                orgReviewRole.setActiveToDate(absMember.getActiveToDate());
                orgReviewRole.setFinancialSystemDocumentTypeCode(documentTypeName);
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
                    orgReviewRole.setRoleMemberId(((RoleMemberImpl)member).getRoleMemberId());
                    orgReviewRole.setRoleId(((RoleMemberImpl)member).getRoleId());
                    roleInfo = getRoleService().getRole(((RoleMemberImpl)member).getRoleId());
                    orgReviewRole.setRoleNamespaceCode(roleInfo.getNamespaceCode());
                    orgReviewRole.setRoleName(roleInfo.getRoleName());
                    orgReviewRole.setDelegate(false);
                } else if(member instanceof KimDelegationMemberImpl){
                    for(KimDelegationMemberAttributeDataImpl delegationMemberAttribute: ((KimDelegationMemberImpl)member).getAttributes()){
                        attribute = new KimAttributeDataImpl();
                        KimCommonUtils.copyProperties(attribute, delegationMemberAttribute);
                        attributes.add(attribute);
                    }
                    KimDelegationImpl delegation = getDelegation((KimDelegationMemberImpl)member);
                    orgReviewRole.setDelegationMemberId(((KimDelegationMemberImpl)member).getDelegationMemberId());
                    orgReviewRole.setRoleId(delegation.getRoleId());
                    roleInfo = getRoleService().getRole(delegation.getRoleId());
                    orgReviewRole.setRoleNamespaceCode(roleInfo.getNamespaceCode());
                    orgReviewRole.setRoleName(roleInfo.getRoleName());
                    orgReviewRole.setDelegationTypeCode(delegation.getDelegationTypeCode());
                    orgReviewRole.setDelegate(true);
                }
                orgReviewRole.setAttributes(attributes);
                orgReviewRoles.add(orgReviewRole);
            }
        }
        return orgReviewRoles;
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

    private List<? extends BusinessObject> searchMembers(Map<String, String> searchCriteria, List<Class> classesToSearch){
        List<? extends BusinessObject> members = new ArrayList<BusinessObject>();
        for(Class classToSearch: classesToSearch){
            members.addAll(KNSServiceLocator.getLookupService().findCollectionBySearchHelper(
                classToSearch, searchCriteria, false));
        }
        return members;
    }

    private KimDelegationImpl getDelegation(KimDelegationMemberImpl delegationMember){
        Map<String, String> criteria = new HashMap<String, String>();
        KimDelegationImpl delegation;
        criteria.put(KimConstants.PrimaryKeyConstants.DELEGATION_ID, delegationMember.getDelegationId());
        return (KimDelegationImpl)KNSServiceLocator.getBusinessObjectService().findByPrimaryKey(KimDelegationImpl.class, criteria);
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
            searchCriteria.put("principalName", WILDCARD+principalName+WILDCARD);
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
        List<RoleImpl> roleImpls = null;
        if(StringUtils.isNotEmpty(assignedToRoleNamespaceCode) && StringUtils.isEmpty(assignedToRoleName) ||
                StringUtils.isEmpty(assignedToRoleNamespaceCode) && StringUtils.isNotEmpty(assignedToRoleName) ||
                StringUtils.isNotEmpty(assignedToRoleNamespaceCode) && StringUtils.isNotEmpty(assignedToRoleName)){
            searchCriteria = new HashMap<String, String>();
            searchCriteria.put(KimConstants.UniqueKeyConstants.NAMESPACE_CODE, getQueryString(assignedToRoleNamespaceCode));
            searchCriteria.put(KimConstants.UniqueKeyConstants.ROLE_NAME, getQueryString(assignedToRoleName));
            roleImpls = 
                (List<RoleImpl>)KNSServiceLocator.getLookupService().findCollectionBySearchUnbounded(RoleImpl.class, searchCriteria);
            if(roleImpls==null || roleImpls.size()==0)
                return null;
        }

        String chartOfAccountsCode = fieldValues.get(MEMBER_ATTRIBUTE_CHART_OF_ACCOUNTS_CODE);
        if(StringUtils.isNotBlank(chartOfAccountsCode)){
            searchCriteria.put(MEMBER_ATTRIBUTE_NAME_KEY, KfsKimAttributes.CHART_OF_ACCOUNTS_CODE);
            searchCriteria.put(MEMBER_ATTRIBUTE_VALUE_KEY, chartOfAccountsCode);
        }
        String organizationCode = fieldValues.get(MEMBER_ATTRIBUTE_ORGANIZATION_CODE);
        if(StringUtils.isNotBlank(organizationCode)){
            searchCriteria.put(MEMBER_ATTRIBUTE_NAME_KEY, KfsKimAttributes.ORGANIZATION_CODE);
            searchCriteria.put(MEMBER_ATTRIBUTE_VALUE_KEY, organizationCode);
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
            for(RoleImpl role: roleImpls){
                memberQueryString.append(role.getRoleId()+KimConstants.KimUIConstants.OR_OPERATOR);
            }
            if(memberQueryString.toString().endsWith(KimConstants.KimUIConstants.OR_OPERATOR))
                memberQueryString.delete(memberQueryString.length()-KimConstants.KimUIConstants.OR_OPERATOR.length(), memberQueryString.length());
            searchCriteria.put(MEMBER_ID, memberQueryString.toString());
        }

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

    @Override
    public void validateSearchParameters(Map fieldValues) {
        if (StringUtils.isEmpty((String)fieldValues.get(DOCUMENT_TYPE_NAME))){
            throw new ValidationException("Please select a document type name.");
        }
        super.validateSearchParameters(fieldValues);
    }

}