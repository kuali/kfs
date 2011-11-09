/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.coa.identity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.document.OrgReviewRoleMaintainableImpl;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.identity.KfsKimAttributes; import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.core.util.RiceUtilities;
import org.kuali.rice.kew.doctype.bo.DocumentType;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;
import org.kuali.rice.kew.api.docType.DocumentType;
import org.kuali.rice.kew.api.exception.WorkflowException;

import org.kuali.rice.kim.bo.Group;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.bo.role.dto.DelegateMemberCompleteInfo;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.bo.role.dto.RoleMemberCompleteInfo;
import org.kuali.rice.kim.api.common.delegate.DelegateType;
import org.kuali.rice.kim.api.common.delegate.DelegateMember;
import org.kuali.rice.kim.api.type.KimType;
import org.kuali.rice.kim.api.group.GroupService;
import org.kuali.rice.kim.api.services.IdentityManagementService;
import org.kuali.rice.kim.api.type.KimTypeInfoService;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.util.KimCommonUtils;
import org.kuali.rice.kim.api.KimApiConstants; import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kns.document.authorization.BusinessObjectRestrictions;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.util.UrlFactory;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.ksb.cache.RiceCacheAdministrator;
public class OrgReviewRoleLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    private GroupService groupService;
    private RoleService roleManagementService;
    private IdentityManagementService identityManagementService;
    private DocumentTypeService documentTypeService;
    private KimTypeInfoService typeInfoService;
    private RiceCacheAdministrator cacheAdministrator;
    
//    private KualiInquirableImpl orgReviewRoleInquirable;
    
    protected static final String WILDCARD = "*";
    protected static final String DOCUMENT_TYPE_NAME = KfsKimAttributes.FINANCIAL_SYSTEM_DOCUMENT_TYPE_CODE;
    protected static final String SEARCH_CRITERIA_DOCUMENT_TYPE_NAME = "documentTypeName";
    public static final String MEMBER_ATTRIBUTE_CHART_OF_ACCOUNTS_CODE = "chartOfAccountsCode";
    public static final String MEMBER_ATTRIBUTE_ORGANIZATION_CODE = "organizationCode";
    protected static final String MEMBER_PRINCIPAL_NAME = "principalMemberPrincipalName";
    protected static final String MEMBER_GROUP_NAMESPACE_CODE = "groupMemberGroupNamespaceCode";
    protected static final String MEMBER_GROUP_NAME = "groupMemberGroupName";
    protected static final String MEMBER_ROLE_NAMESPACE = "roleMemberRoleNamespaceCode";
    protected static final String MEMBER_ROLE_NAME = "roleMemberRoleName";

    protected static final String MEMBER_ID = "memberId";
    protected static final String MEMBER_ATTRIBUTE_NAME_KEY = "attributes.kimAttribute.attributeName";
    protected static final String MEMBER_ATTRIBUTE_VALUE_KEY = "attributes.attributeValue";

    protected static final String DELEGATION_MEMBER_ID = "members.memberId";
    protected static final String DELEGATION_MEMBER_ATTRIBUTE_NAME_KEY = "members.attributes.kimAttribute.attributeName";
    protected static final String DELEGATION_MEMBER_ATTRIBUTE_VALUE_KEY = "members.attributes.attributeValue";
    
    protected static final String DELEGATE = "delegate";
    protected static final String ACTIVE = "active";
    protected static final String ACTIVE_FROM_DATE = "activeFromDate";
    protected static final String ACTIVE_TO_DATE = "activeToDate";
    public static final String ORG_ACCT_REVIEW_ROLE_DOC_TYPE_CACHE_GROUP = "OrgAcctReviewRoleDocType";
    public static final String ORG_ACCT_REVIEW_ROLE_DOC_TYPE_CACHE_PREFIX = ORG_ACCT_REVIEW_ROLE_DOC_TYPE_CACHE_GROUP + ":";
    
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
            HtmlData createDelegationUrl = getUrlData(businessObject, KRADConstants.MAINTENANCE_EDIT_METHOD_TO_CALL, KFSConstants.COAConstants.ORG_REVIEW_ROLE_CREATE_DELEGATION_DISPLAY_TEXT, pkNames);
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
    
    protected AnchorHtmlData getCreateDelegationUrl(BusinessObject businessObject, List pkNames){
        OrgReviewRole orr = (OrgReviewRole)businessObject;
        Properties parameters = new Properties();
        parameters.put(OrgReviewRole.ORIGINAL_DELEGATION_MEMBER_ID_TO_MODIFY, OrgReviewRole.NEW_DELEGATION_ID_KEY_VALUE);
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KRADConstants.MAINTENANCE_EDIT_METHOD_TO_CALL);
        parameters.put(KRADConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, businessObject.getClass().getName());
        parameters.put(KRADConstants.COPY_KEYS, OrgReviewRole.ORIGINAL_DELEGATION_MEMBER_ID_TO_MODIFY+","+OrgReviewRole.ORIGINAL_ROLE_MEMBER_ID_TO_MODIFY+","+KRADConstants.DISPATCH_REQUEST_PARAMETER);
        parameters.put(OrgReviewRole.ORIGINAL_ROLE_MEMBER_ID_TO_MODIFY, orr.getRoleMemberId());
        //parameters.putAll(getParametersFromPrimaryKey(businessObject, pkNames));
        String href = UrlFactory.parameterizeUrl(KRADConstants.MAINTENANCE_ACTION, parameters);

        return new AnchorHtmlData(href, KRADConstants.DOC_HANDLER_METHOD, KFSConstants.COAConstants.ORG_REVIEW_ROLE_CREATE_DELEGATION_DISPLAY_TEXT);
    }

    protected String getActionUrlHref(BusinessObject businessObject, String methodToCall, List pkNames){
        OrgReviewRole orr = (OrgReviewRole)businessObject;
        Properties parameters = new Properties();
        parameters.put(KRADConstants.DISPATCH_REQUEST_PARAMETER, methodToCall);
        parameters.put(KRADConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, OrgReviewRole.class.getName());

        if(orr.isDelegate()){
            parameters.put(KRADConstants.COPY_KEYS, OrgReviewRole.ORIGINAL_DELEGATION_MEMBER_ID_TO_MODIFY+","+KRADConstants.DISPATCH_REQUEST_PARAMETER);
            parameters.put(OrgReviewRole.ORIGINAL_DELEGATION_MEMBER_ID_TO_MODIFY, orr.getDelegationMemberId());
        } else {
            parameters.put(KRADConstants.COPY_KEYS, OrgReviewRole.ORIGINAL_DELEGATION_MEMBER_ID_TO_MODIFY+","+OrgReviewRole.ORIGINAL_ROLE_MEMBER_ID_TO_MODIFY+","+KRADConstants.DISPATCH_REQUEST_PARAMETER);
            parameters.put(OrgReviewRole.ORIGINAL_ROLE_MEMBER_ID_TO_MODIFY, orr.getRoleMemberId());
        }
        return UrlFactory.parameterizeUrl(KRADConstants.MAINTENANCE_ACTION, parameters);
    }

    protected List<String> getOverridePKNames(OrgReviewRole orr){
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
    
    protected List<? extends BusinessObject> getMemberSearchResults(Map<String, String> fieldValues){
        String delegateStr = fieldValues.get(DELEGATE);
        String documentTypeName = fieldValues.get(DOCUMENT_TYPE_NAME);
        List<Class> classesToSearch = new ArrayList<Class>();
        List<DelegateMemberCompleteInfo> searchResultsDelegationMembers = new ArrayList<DelegateMemberCompleteInfo>();
        List<RoleMemberCompleteInfo> searchResultsRoleMembers = new ArrayList<RoleMemberCompleteInfo>();
        Map<String, String> searchCriteriaRoleMembers;
        Map<String, String> searchCriteriaDelegations;
        if(StringUtils.isEmpty(delegateStr)){
            //Search for both, role members and delegations
            searchCriteriaRoleMembers = buildOrgReviewRoleSearchCriteria(documentTypeName, fieldValues);
            searchCriteriaDelegations = buildOrgReviewRoleSearchCriteriaForDelegations(documentTypeName, fieldValues);
            if(searchCriteriaRoleMembers == null && searchCriteriaDelegations==null){
                return new ArrayList<BusinessObject>();
            } else if(searchCriteriaRoleMembers!=null && searchCriteriaDelegations!=null){
                searchResultsRoleMembers.addAll((Collection)searchRoleMembers(searchCriteriaRoleMembers));
                searchResultsDelegationMembers.addAll((Collection)searchDelegations(searchCriteriaDelegations));
            } else if(searchCriteriaRoleMembers!=null){
                searchResultsRoleMembers.addAll((Collection)searchRoleMembers(searchCriteriaRoleMembers));
            } else if(searchCriteriaDelegations!=null){
                searchResultsDelegationMembers.addAll((Collection)searchDelegations(searchCriteriaDelegations));
            }
        } else{
            boolean delegate = RiceUtilities.getBooleanValueForString(delegateStr, false);
            if(delegate){
                searchCriteriaDelegations = buildOrgReviewRoleSearchCriteriaForDelegations(documentTypeName, fieldValues);
                searchResultsDelegationMembers.addAll((Collection)searchDelegations(searchCriteriaDelegations));
            } else{
                searchCriteriaRoleMembers = buildOrgReviewRoleSearchCriteria(documentTypeName, fieldValues);
                searchResultsRoleMembers.addAll((Collection)searchRoleMembers(searchCriteriaRoleMembers));
            }
        }
        List<OrgReviewRole> flattenedSearchResults = new ArrayList<OrgReviewRole>();
        flattenedSearchResults.addAll(flattenToOrgReviewMembers(fieldValues.get(ACTIVE), documentTypeName, searchResultsRoleMembers));
        flattenedSearchResults.addAll(flattenToOrgReviewDelegationMembers(fieldValues.get(ACTIVE), documentTypeName, searchResultsDelegationMembers));        
        filterOrgReview(fieldValues, flattenedSearchResults);
        
        return flattenedSearchResults;
    }

    protected void filterOrgReview(Map<String, String> fieldValues, List<OrgReviewRole> searchResults){
    
        String principalName = fieldValues.get(MEMBER_PRINCIPAL_NAME);
        List<Person> principals = null;
        if(StringUtils.isNotEmpty(principalName)){
            Map<String, String> criteria = new HashMap<String, String>();
            criteria.put(KimApiConstants.UniqueKeyConstants.PRINCIPAL_NAME, WILDCARD+principalName+WILDCARD);
            principals = (List<Person>)getPersons(criteria);
        }
        String assignedToGroupNamespaceCode = fieldValues.get(MEMBER_GROUP_NAMESPACE_CODE);
        String assignedToGroupName = fieldValues.get(MEMBER_GROUP_NAME);
        List<Group> groups = null;
        if(StringUtils.isNotEmpty(assignedToGroupNamespaceCode) && StringUtils.isEmpty(assignedToGroupName) ||
                StringUtils.isEmpty(assignedToGroupNamespaceCode) && StringUtils.isNotEmpty(assignedToGroupName) ||
                StringUtils.isNotEmpty(assignedToGroupNamespaceCode) && StringUtils.isNotEmpty(assignedToGroupName)){
            Map<String, String> searchCriteria = new HashMap<String, String>();
            searchCriteria.put(KimApiConstants.UniqueKeyConstants.NAMESPACE_CODE, getQueryString(assignedToGroupNamespaceCode));
            searchCriteria.put(KimApiConstants.UniqueKeyConstants.GROUP_NAME, getQueryString(assignedToGroupName));
            groups = getGroups(searchCriteria);
        }

        String assignedToRoleNamespaceCode = fieldValues.get(MEMBER_ROLE_NAMESPACE);
        String assignedToRoleName = fieldValues.get(MEMBER_ROLE_NAME);

        List<Role> roles = null;
        if(StringUtils.isNotEmpty(assignedToRoleNamespaceCode) && StringUtils.isEmpty(assignedToRoleName) ||
                StringUtils.isEmpty(assignedToRoleNamespaceCode) && StringUtils.isNotEmpty(assignedToRoleName) ||
                StringUtils.isNotEmpty(assignedToRoleNamespaceCode) && StringUtils.isNotEmpty(assignedToRoleName)){
            Map<String, String> searchCriteria = new HashMap<String, String>();
            searchCriteria.put(KimApiConstants.UniqueKeyConstants.NAMESPACE_CODE, getQueryString(assignedToRoleNamespaceCode));
            searchCriteria.put(KimApiConstants.UniqueKeyConstants.ROLE_NAME, getQueryString(assignedToRoleName));
            roles = getRoles(searchCriteria);
        }

        String financialSystemDocumentTypeCode = fieldValues.get(DOCUMENT_TYPE_NAME);
        String chartOfAccountsCode = fieldValues.get(MEMBER_ATTRIBUTE_CHART_OF_ACCOUNTS_CODE);
        String organizationCode = fieldValues.get(MEMBER_ATTRIBUTE_ORGANIZATION_CODE);
        
        //Loop through org review roles and remove rows where necessary
        Iterator<OrgReviewRole> it = searchResults.iterator();
        OrgReviewRole orgReviewRole = null;
        boolean remove = false;
        
    	while(it.hasNext()){
    	
    	    orgReviewRole = it.next();
    	    remove = false;
    	    
    	    //check member attribute parameters
    	    if(StringUtils.isNotBlank(organizationCode)){
    	        //filter by chart/document type if they exist
    	        if(StringUtils.isNotBlank(chartOfAccountsCode)){
    	            if(!chartOfAccountsCode.equals(orgReviewRole.getChartOfAccountsCode())){    	                
    	                remove = true;
    	            }
    	        }
    	        
    	        if(StringUtils.isNotBlank(financialSystemDocumentTypeCode)){
    	            if(!financialSystemDocumentTypeCode.equals(orgReviewRole.getFinancialSystemDocumentTypeCode())){    	                
    	                remove = true;
    	            }
    	        }
            }else if(StringUtils.isNotBlank(chartOfAccountsCode)){
                //filter by document type if it exists
                if(StringUtils.isNotBlank(financialSystemDocumentTypeCode)){
                    if(!financialSystemDocumentTypeCode.equals(orgReviewRole.getFinancialSystemDocumentTypeCode())){                        
                        remove = true;
                    }                    
                }
            }

    	    List<String> items = new ArrayList<String>();
    	    
    	    //check member id parameters, and only if it hasn't already been marked for removal.
    	    if(remove == false){
                if(roles!=null){
                    if(groups!=null){
                        for(Group group: groups){                                                        
                            items.add(group.getGroupId());
                        }
                        if(!items.contains(orgReviewRole.getGroupMemberGroupId())){
                            remove = true;
                        }
                    }
                    if(principals!=null){                                            
                        for(Person principal: principals){
                            items.add(principal.getPrincipalId());                            
                        }
                        if(!items.contains(orgReviewRole.getPrincipalMemberPrincipalId())){
                            remove = true;
                        }                        
                    }

                }else if(groups!=null){
                    if(principals!=null){                    
                        for(Person principal: principals){
                            items.add(principal.getPrincipalId());                            
                        }
                        if(!items.contains(orgReviewRole.getPrincipalMemberPrincipalId())){
                            remove = true;
                        }                        
                    }
                }
    	    }
    	    
    	    //remove if necessary
            if(remove){
                it.remove();
            }
    	}
    }
    
    protected Map<String, String> addRoleToConsiderSearchCriteria(String documentTypeName, Map<String, String> searchCriteria){
        List<String> roleNamesToSearchInto = getRolesToConsider(documentTypeName);
        if(searchCriteria==null)
            searchCriteria = new HashMap<String, String>();
        if(roleNamesToSearchInto!=null){
            StringBuffer rolesQueryString = new StringBuffer();
            for(String roleName: roleNamesToSearchInto){
                rolesQueryString.append(
                    getRoleService().getRoleIdByName(KFSConstants.SysKimApiConstants.ORGANIZATION_REVIEWER_ROLE_NAMESPACECODE, roleName)+
                    KimConstants.KimUIConstants.OR_OPERATOR);
            }
            if(rolesQueryString.toString().endsWith(KimConstants.KimUIConstants.OR_OPERATOR))
                rolesQueryString.delete(rolesQueryString.length()-KimConstants.KimUIConstants.OR_OPERATOR.length(), rolesQueryString.length());
            searchCriteria.put(KimConstants.PrimaryKeyConstants.ROLE_ID, rolesQueryString.toString());
        }
        return searchCriteria;
    }

    protected List<RoleMemberCompleteInfo> searchRoleMembers(Map<String, String> searchCriteriaRoleMembers){
        List<RoleMemberCompleteInfo> members = new ArrayList<RoleMemberCompleteInfo>();
        members.addAll(getRoleService().findRoleMembersCompleteInfo(searchCriteriaRoleMembers));
        return members;
    }

    protected List<DelegateMemberCompleteInfo> searchDelegations(Map<String, String> searchCriteriaDelegateMembers){
        List<DelegateMemberCompleteInfo> members = new ArrayList<DelegateMemberCompleteInfo>();
        members.addAll(getRoleService().findDelegateMembersCompleteInfo(searchCriteriaDelegateMembers));
        return members;
    }

    public boolean hasOrganizationHierarchy(final String documentTypeName) {
        if(StringUtils.isEmpty(documentTypeName)) return false;
        try {
            return (new WorkflowInfo()).hasRouteNode(documentTypeName, KFSConstants.RouteLevelNames.ORGANIZATION_HIERARCHY);
        } catch(WorkflowException wex){
            throw new RuntimeException("Workflow Exception occurred: "+wex);
        }
    }

    public boolean hasAccountingOrganizationHierarchy(final String documentTypeName) {
        if(StringUtils.isEmpty(documentTypeName)) return false;
        try{ 
            return (new WorkflowInfo()).hasRouteNode(documentTypeName, KFSConstants.RouteLevelNames.ACCOUNTING_ORGANIZATION_HIERARCHY);
        } catch(WorkflowException wex){
            throw new RuntimeException("Workflow Exception occurred: "+wex);
        }
    }

    public String getClosestOrgReviewRoleParentDocumentTypeName(final String documentTypeName){
        if(StringUtils.isEmpty(documentTypeName)) return null;
        Set<String> potentialParentDocumentTypeNames = new HashSet<String>();
        potentialParentDocumentTypeNames.add(KFSConstants.FINANCIAL_SYSTEM_TRANSACTIONAL_DOCUMENT);
        potentialParentDocumentTypeNames.add(KFSConstants.FINANCIAL_SYSTEM_COMPLEX_MAINTENANCE_DOCUMENT);
        potentialParentDocumentTypeNames.add(KFSConstants.FINANCIAL_SYSTEM_SIMPLE_MAINTENANCE_DOCUMENT);
        return KimCommonUtils.getClosestParentDocumentTypeName(getDocumentTypeService().findByName(documentTypeName), potentialParentDocumentTypeNames);
    }

    public List<String> getRolesToConsider(String documentTypeName) {
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
    public List<String> getRolesToConsider(String documentTypeName, boolean hasOrganizationHierarchy, boolean hasAccountingOrganizationHierarchy, String closestParentDocumentTypeName){
        if(StringUtils.isEmpty(documentTypeName)){
            List<String> roleToConsider = new ArrayList<String>();
            roleToConsider.add(KFSConstants.SysKimApiConstants.ORGANIZATION_REVIEWER_ROLE_NAME);                
            roleToConsider.add(KFSConstants.SysKimApiConstants.ACCOUNTING_REVIEWER_ROLE_NAME);
            return roleToConsider;
        }

        List<String> roleToConsider = new ArrayList<String>();
        if(documentTypeName.equals(KFSConstants.FINANCIAL_SYSTEM_TRANSACTIONAL_DOCUMENT) || KFSConstants.FINANCIAL_SYSTEM_TRANSACTIONAL_DOCUMENT.equals(closestParentDocumentTypeName))
            roleToConsider.add(KFSConstants.SysKimApiConstants.ACCOUNTING_REVIEWER_ROLE_NAME);
        else if(hasOrganizationHierarchy || hasAccountingOrganizationHierarchy){
            if(hasOrganizationHierarchy) roleToConsider.add(KFSConstants.SysKimApiConstants.ORGANIZATION_REVIEWER_ROLE_NAME);                
            if(hasAccountingOrganizationHierarchy) roleToConsider.add(KFSConstants.SysKimApiConstants.ACCOUNTING_REVIEWER_ROLE_NAME);
            //readonly
        } else if(KFSConstants.ROOT_DOCUMENT_TYPE.equals(documentTypeName)){
            roleToConsider.add(KFSConstants.SysKimApiConstants.ORGANIZATION_REVIEWER_ROLE_NAME);                
            roleToConsider.add(KFSConstants.SysKimApiConstants.ACCOUNTING_REVIEWER_ROLE_NAME);
        } else{
            if(documentTypeName.equals(KFSConstants.FINANCIAL_SYSTEM_COMPLEX_MAINTENANCE_DOCUMENT) || KFSConstants.FINANCIAL_SYSTEM_COMPLEX_MAINTENANCE_DOCUMENT.equals(closestParentDocumentTypeName))
                roleToConsider.add(KFSConstants.SysKimApiConstants.ORGANIZATION_REVIEWER_ROLE_NAME);
            else if(currentDocTypeAndChildrenHaveZeroOrgAndAccountReviewRoles(documentTypeName)){
                throw new ValidationException("Invalid document type chosen for Organization Review: " + documentTypeName);
            }
        }

        return roleToConsider;
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
    
    public boolean currentDocTypeAndChildrenHaveZeroOrgAndAccountReviewRoles(String currentDocumentTypeName){     
        boolean hasZeroQualifyingNodes = true;
        Object hasZeroNodesCache  = null;
        
        //check cache
        hasZeroNodesCache = getCacheAdministrator().getFromCache(ORG_ACCT_REVIEW_ROLE_DOC_TYPE_CACHE_PREFIX + currentDocumentTypeName);        
        if( ObjectUtils.isNotNull(hasZeroNodesCache)){
            return ((Boolean)hasZeroNodesCache).booleanValue();
        }

        //check current doc type for qualifying nodes
        if(hasOrganizationHierarchy(currentDocumentTypeName) || hasAccountingOrganizationHierarchy(currentDocumentTypeName) ){                       
            hasZeroQualifyingNodes = false;        
        }
        
        //if still has no qualifying nodes, check current nodes children
        if(hasZeroQualifyingNodes){
            DocumentType currentDocType = SpringContext.getBean(DocumentTypeService.class).getDocumentTypeVO(currentDocumentTypeName);
            List<DocumentType> docTypes = SpringContext.getBean(DocumentTypeService.class).getChildDocumentTypes(currentDocType.getDocTypeId());
            
            for(DocumentType docType : docTypes){
                hasZeroQualifyingNodes &= currentDocTypeAndChildrenHaveZeroOrgAndAccountReviewRoles(docType.getName());
                if(hasZeroQualifyingNodes == false) break;
            }
        }
        
        //add to cache if there are qualifying nodes
        if(hasZeroQualifyingNodes == false){
            getCacheAdministrator().putInCache(ORG_ACCT_REVIEW_ROLE_DOC_TYPE_CACHE_PREFIX + currentDocumentTypeName, new Boolean(hasZeroQualifyingNodes), ORG_ACCT_REVIEW_ROLE_DOC_TYPE_CACHE_GROUP);
        }
        
        return hasZeroQualifyingNodes;
    }
    
    protected List<OrgReviewRole> flattenToOrgReviewMembers(String active, String documentTypeName, List<RoleMemberCompleteInfo> members){
        List<OrgReviewRole> orgReviewRoles = new ArrayList<OrgReviewRole>();
        if(members==null || members.size()<1) return orgReviewRoles;
        
        OrgReviewRole orgReviewRole;
        String memberType;
        Role roleInfo;
        KimType kimTypeInfo;
        Boolean activeInd = null;
        if(StringUtils.isNotEmpty(active)){
            activeInd = new Boolean(RiceUtilities.getBooleanValueForString(active, true));
        }
        for(RoleMemberCompleteInfo member: members){
            if(activeInd==null || (activeInd.booleanValue()==true && member.isActive()) || (activeInd.booleanValue()==false && !member.isActive())){
                orgReviewRole = new OrgReviewRole();
                OrgReviewRoleMaintainableImpl orgReviewRoleMaintainableImpl = new OrgReviewRoleMaintainableImpl();
                orgReviewRole.setMemberId(member.getMemberId());
                orgReviewRole.setMemberTypeCode(member.getMemberTypeCode());           
                orgReviewRole.setActiveFromDate(member.getActiveFromDate());
                orgReviewRole.setActiveToDate(member.getActiveToDate());
                orgReviewRole.setActive(member.isActive());
                orgReviewRole.setFinancialSystemDocumentTypeCode(documentTypeName);
                orgReviewRole.setMemberName(member.getMemberName());
                orgReviewRole.setMemberNamespaceCode(member.getMemberNamespaceCode());
                
                roleInfo = getRoleService().getRole(member.getRoleId());
                kimTypeInfo = getTypeInfoService().getKimType(roleInfo.getKimTypeId());
                orgReviewRole.setAttributes(orgReviewRole.getAttributeSetAsQualifierList(kimTypeInfo, member.getQualifier()));

                orgReviewRole.setRoleRspActions(orgReviewRoleMaintainableImpl.getRoleRspActions(member.getRoleMemberId()));
                orgReviewRole.setRoleMemberId(member.getRoleMemberId());
                orgReviewRole.setRoleId(member.getRoleId());
                orgReviewRole.setNamespaceCode(roleInfo.getNamespaceCode());
                orgReviewRole.setRoleName(roleInfo.getName());
                orgReviewRole.setDelegate(false);

                orgReviewRole.setChartOfAccountsCode(orgReviewRole.getAttributeValue(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE));
                orgReviewRole.setOrganizationCode(orgReviewRole.getAttributeValue(KfsKimAttributes.ORGANIZATION_CODE));
                orgReviewRole.setOverrideCode(orgReviewRole.getAttributeValue(KfsKimAttributes.ACCOUNTING_LINE_OVERRIDE_CODE));
                orgReviewRole.setFromAmount(orgReviewRole.getAttributeValue(KfsKimAttributes.FROM_AMOUNT));
                orgReviewRole.setToAmount(orgReviewRole.getAttributeValue(KfsKimAttributes.TO_AMOUNT));
                orgReviewRole.setFinancialSystemDocumentTypeCode(orgReviewRole.getAttributeValue(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME));
                
                orgReviewRoles.add(orgReviewRole);
            }
        }
        return orgReviewRoles;
    }
    
    protected List<OrgReviewRole> flattenToOrgReviewDelegationMembers(String active, String documentTypeName, List<DelegateMemberCompleteInfo> delegationMembers){
        List<OrgReviewRole> orgReviewRoles = new ArrayList<OrgReviewRole>();
        if(delegationMembers==null || delegationMembers.size()<1) return orgReviewRoles;
        
        OrgReviewRole orgReviewRole;
        String memberType;
        Role roleInfo;
        Boolean activeInd = null;
        if(StringUtils.isNotEmpty(active)){
            activeInd = new Boolean(RiceUtilities.getBooleanValueForString(active, true));
        }
        KimType kimTypeInfo;
        for(DelegateMemberCompleteInfo member: delegationMembers){
            if(activeInd==null || (activeInd.booleanValue()==true && member.isActive()) || (activeInd.booleanValue()==false && !member.isActive())){
                orgReviewRole = new OrgReviewRole();
                OrgReviewRoleMaintainableImpl orgReviewRoleMaintainableImpl = new OrgReviewRoleMaintainableImpl();
                orgReviewRole.setMemberId(member.getMemberId());
                orgReviewRole.setMemberTypeCode(member.getMemberTypeCode());
                orgReviewRole.setActiveFromDate(member.getActiveFromDate());
                orgReviewRole.setActiveToDate(member.getActiveToDate());
                orgReviewRole.setActive(member.isActive());
                orgReviewRole.setFinancialSystemDocumentTypeCode(documentTypeName);
                orgReviewRole.setMemberName(member.getMemberName());
                orgReviewRole.setMemberNamespaceCode(member.getMemberNamespaceCode());

                roleInfo = getRoleService().getRole(member.getRoleId());
                kimTypeInfo = getTypeInfoService().getKimType(roleInfo.getKimTypeId());
                orgReviewRole.setAttributes(orgReviewRole.getAttributeSetAsQualifierList(kimTypeInfo, member.getQualifier()));

                orgReviewRole.setDelegationMemberId(member.getDelegationMemberId());
                orgReviewRole.setRoleMemberId(member.getRoleMemberId());
                orgReviewRole.setRoleId(member.getRoleId());
                roleInfo = getRoleService().getRole(member.getRoleId());
                orgReviewRole.setNamespaceCode(roleInfo.getNamespaceCode());
                orgReviewRole.setRoleName(roleInfo.getName());
                orgReviewRole.setDelegationTypeCode(member.getDelegationTypeCode());
                orgReviewRole.setDelegate(true);

                orgReviewRole.setChartOfAccountsCode(orgReviewRole.getAttributeValue(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE));
                orgReviewRole.setOrganizationCode(orgReviewRole.getAttributeValue(KfsKimAttributes.ORGANIZATION_CODE));
                orgReviewRole.setOverrideCode(orgReviewRole.getAttributeValue(KfsKimAttributes.ACCOUNTING_LINE_OVERRIDE_CODE));
                orgReviewRole.setFromAmount(orgReviewRole.getAttributeValue(KfsKimAttributes.FROM_AMOUNT));
                orgReviewRole.setToAmount(orgReviewRole.getAttributeValue(KfsKimAttributes.TO_AMOUNT));
                orgReviewRole.setFinancialSystemDocumentTypeCode(orgReviewRole.getAttributeValue(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME));
                
                orgReviewRoles.add(orgReviewRole);
            }
        }
        return orgReviewRoles;
    }
    
    protected DelegateType getDelegation(DelegateMember delegationMember){
        Map<String, String> criteria = new HashMap<String, String>();
        DelegateType delegation;
        criteria.put(KimConstants.PrimaryKeyConstants.DELEGATION_ID, delegationMember.getDelegationId());
        return (DelegateType)SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(DelegateType.class, criteria);
    }

    protected String getQueryString(String parameter){
        if(StringUtils.isEmpty(parameter))
            return WILDCARD;
        else
            return WILDCARD+parameter+WILDCARD;
    }

    public List<Person> getPersons(Map<String, String> fieldValues) {
        return (List<Person>) SpringContext.getBean(PersonService.class).findPeople(fieldValues);
    }
    
    public List<Role> getRoles(Map<String, String> fieldValues) {
        return (List<Role>) SpringContext.getBean(RoleService.class).getRolesSearchResults(fieldValues);
    }

    public List<Group> getGroups(Map<String, String> fieldValues) {
        return (List<Group>) SpringContext.getBean(GroupService.class).lookupGroups(fieldValues);
    }
    
    protected Map<String, String> buildOrgReviewRoleSearchCriteria(String documentTypeName, Map<String, String> fieldValues){
        String principalName = fieldValues.get(MEMBER_PRINCIPAL_NAME);
        List<Person> principals = null;
        if(StringUtils.isNotEmpty(principalName)){
            Map<String, String> criteria = new HashMap<String, String>();
            criteria.put(KimApiConstants.UniqueKeyConstants.PRINCIPAL_NAME, WILDCARD+principalName+WILDCARD);
            principals = (List<Person>)getPersons(criteria);
            if(principals==null || principals.isEmpty())
                return null;
        }
        String assignedToGroupNamespaceCode = fieldValues.get(MEMBER_GROUP_NAMESPACE_CODE);
        String assignedToGroupName = fieldValues.get(MEMBER_GROUP_NAME);
        List<Group> groups = null;
        if(StringUtils.isNotEmpty(assignedToGroupNamespaceCode) && StringUtils.isEmpty(assignedToGroupName) ||
                StringUtils.isEmpty(assignedToGroupNamespaceCode) && StringUtils.isNotEmpty(assignedToGroupName) ||
                StringUtils.isNotEmpty(assignedToGroupNamespaceCode) && StringUtils.isNotEmpty(assignedToGroupName)){
            Map<String, String> searchCriteria = new HashMap<String, String>();
            searchCriteria.put(KimApiConstants.UniqueKeyConstants.NAMESPACE_CODE, getQueryString(assignedToGroupNamespaceCode));
            searchCriteria.put(KimApiConstants.UniqueKeyConstants.GROUP_NAME, getQueryString(assignedToGroupName));
            groups = getGroups(searchCriteria);
            if(groups==null || groups.size()==0)
                return null;
        }

        String assignedToRoleNamespaceCode = fieldValues.get(MEMBER_ROLE_NAMESPACE);
        String assignedToRoleName = fieldValues.get(MEMBER_ROLE_NAME);

        List<Role> roles = null;
        if(StringUtils.isNotEmpty(assignedToRoleNamespaceCode) && StringUtils.isEmpty(assignedToRoleName) ||
                StringUtils.isEmpty(assignedToRoleNamespaceCode) && StringUtils.isNotEmpty(assignedToRoleName) ||
                StringUtils.isNotEmpty(assignedToRoleNamespaceCode) && StringUtils.isNotEmpty(assignedToRoleName)){
            Map<String, String> searchCriteria = new HashMap<String, String>();
            searchCriteria.put(KimApiConstants.UniqueKeyConstants.NAMESPACE_CODE, getQueryString(assignedToRoleNamespaceCode));
            searchCriteria.put(KimApiConstants.UniqueKeyConstants.ROLE_NAME, getQueryString(assignedToRoleName));
            roles = getRoles(searchCriteria);
            if(roles==null || roles.size()==0)
                return null;
        }

        Map<String, String> searchCriteriaMain = new HashMap<String, String>();

        String financialSystemDocumentTypeCode = fieldValues.get(DOCUMENT_TYPE_NAME);
        if(StringUtils.isNotBlank(financialSystemDocumentTypeCode)){
            searchCriteriaMain.put(MEMBER_ATTRIBUTE_NAME_KEY, SEARCH_CRITERIA_DOCUMENT_TYPE_NAME);
            searchCriteriaMain.put(MEMBER_ATTRIBUTE_VALUE_KEY, financialSystemDocumentTypeCode);
        }        
        String chartOfAccountsCode = fieldValues.get(MEMBER_ATTRIBUTE_CHART_OF_ACCOUNTS_CODE);
        if(StringUtils.isNotBlank(chartOfAccountsCode)){
            searchCriteriaMain.put(MEMBER_ATTRIBUTE_NAME_KEY, KfsKimAttributes.CHART_OF_ACCOUNTS_CODE);
            searchCriteriaMain.put(MEMBER_ATTRIBUTE_VALUE_KEY, chartOfAccountsCode);
        }
        String organizationCode = fieldValues.get(MEMBER_ATTRIBUTE_ORGANIZATION_CODE);
        if(StringUtils.isNotBlank(organizationCode)){
            searchCriteriaMain.put(MEMBER_ATTRIBUTE_NAME_KEY, KfsKimAttributes.ORGANIZATION_CODE);
            searchCriteriaMain.put(MEMBER_ATTRIBUTE_VALUE_KEY, organizationCode);
        }
        StringBuffer memberQueryString = null;
        if(principals!=null){
            memberQueryString = new StringBuffer();
            for(Person principal: principals){
                memberQueryString.append(principal.getPrincipalId()+KimConstants.KimUIConstants.OR_OPERATOR);
            }
            if(memberQueryString.toString().endsWith(KimConstants.KimUIConstants.OR_OPERATOR))
                memberQueryString.delete(memberQueryString.length()-KimConstants.KimUIConstants.OR_OPERATOR.length(), memberQueryString.length());
            searchCriteriaMain.put(MEMBER_ID, memberQueryString.toString());
        }
        if(groups!=null){
            if(memberQueryString==null)
                memberQueryString = new StringBuffer();
            else if(StringUtils.isNotEmpty(memberQueryString.toString()))
                memberQueryString.append(KimConstants.KimUIConstants.OR_OPERATOR);
            for(Group group: groups){
                memberQueryString.append(group.getGroupId()+KimConstants.KimUIConstants.OR_OPERATOR);
            }
            if(memberQueryString.toString().endsWith(KimConstants.KimUIConstants.OR_OPERATOR))
                memberQueryString.delete(memberQueryString.length()-KimConstants.KimUIConstants.OR_OPERATOR.length(), memberQueryString.length());
            searchCriteriaMain.put(MEMBER_ID, memberQueryString.toString());
        }
        if(roles!=null){
            if(memberQueryString==null)
                memberQueryString = new StringBuffer();
            else if(StringUtils.isNotEmpty(memberQueryString.toString()))
                memberQueryString.append(KimConstants.KimUIConstants.OR_OPERATOR);
            for(Role role: roles){
                memberQueryString.append(role.getId()+KimConstants.KimUIConstants.OR_OPERATOR);
            }
            if(memberQueryString.toString().endsWith(KimConstants.KimUIConstants.OR_OPERATOR))
                memberQueryString.delete(memberQueryString.length()-KimConstants.KimUIConstants.OR_OPERATOR.length(), memberQueryString.length());
            searchCriteriaMain.put(MEMBER_ID, memberQueryString.toString());
        }
        return addRoleToConsiderSearchCriteria(documentTypeName, searchCriteriaMain);
    }
    
    protected Map<String, String> buildOrgReviewRoleSearchCriteriaForDelegations(String documentTypeName, Map<String, String> fieldValues){
        String principalName = fieldValues.get(MEMBER_PRINCIPAL_NAME);
        Map<String, String> searchCriteriaTemp;
        List<Person> principals = null;
        if(StringUtils.isNotEmpty(principalName)){
            searchCriteriaTemp = new HashMap<String, String>();
            searchCriteriaTemp.put(KimApiConstants.UniqueKeyConstants.PRINCIPAL_NAME, WILDCARD+principalName+WILDCARD);
            principals = (List<Person>)getPersons(searchCriteriaTemp);
            if(principals==null || principals.isEmpty())
                return null;
        }
        String assignedToGroupNamespaceCode = fieldValues.get(MEMBER_GROUP_NAMESPACE_CODE);
        String assignedToGroupName = fieldValues.get(MEMBER_GROUP_NAME);
        List<Group> groups = null;
        if(StringUtils.isNotEmpty(assignedToGroupNamespaceCode) && StringUtils.isEmpty(assignedToGroupName) ||
                StringUtils.isEmpty(assignedToGroupNamespaceCode) && StringUtils.isNotEmpty(assignedToGroupName) ||
                StringUtils.isNotEmpty(assignedToGroupNamespaceCode) && StringUtils.isNotEmpty(assignedToGroupName)){
            Map<String, String> searchCriteria = new HashMap<String, String>();
            searchCriteria.put(KimApiConstants.UniqueKeyConstants.NAMESPACE_CODE, getQueryString(assignedToGroupNamespaceCode));
            searchCriteria.put(KimApiConstants.UniqueKeyConstants.GROUP_NAME, getQueryString(assignedToGroupName));
            groups = getGroups(searchCriteria);

            if(groups==null || groups.size()==0)
                return null;
        }

        String assignedToRoleNamespaceCode = fieldValues.get(MEMBER_ROLE_NAMESPACE);
        String assignedToRoleName = fieldValues.get(MEMBER_ROLE_NAME);

        /*if(StringUtils.isNotEmpty(assignedToRoleNamespaceCode))
            searchCriteria.put(MEMBER_ROLE_NAMESPACE, WILDCARD+assignedToRoleNamespaceCode+WILDCARD);
        if(StringUtils.isNotEmpty(assignedToRoleName))
            searchCriteria.put(MEMBER_ROLE_NAME, WILDCARD+assignedToRoleName+WILDCARD);*/
        List<Role> roles = null;
        if(StringUtils.isNotEmpty(assignedToRoleNamespaceCode) && StringUtils.isEmpty(assignedToRoleName) ||
                StringUtils.isEmpty(assignedToRoleNamespaceCode) && StringUtils.isNotEmpty(assignedToRoleName) ||
                StringUtils.isNotEmpty(assignedToRoleNamespaceCode) && StringUtils.isNotEmpty(assignedToRoleName)){
            Map<String, String> searchCriteria = new HashMap<String, String>();
            searchCriteria.put(KimApiConstants.UniqueKeyConstants.NAMESPACE_CODE, getQueryString(assignedToRoleNamespaceCode));
            searchCriteria.put(KimApiConstants.UniqueKeyConstants.ROLE_NAME, getQueryString(assignedToRoleName));
            roles = getRoles(searchCriteria);
            if(roles==null || roles.size()==0)
                return null;
        }

        final Map<String, String> searchCriteriaMain = new HashMap<String, String>();
        String financialSystemDocumentTypeCode = fieldValues.get(DOCUMENT_TYPE_NAME);
        if(StringUtils.isNotBlank(financialSystemDocumentTypeCode)){
            searchCriteriaMain.put(DELEGATION_MEMBER_ATTRIBUTE_NAME_KEY, SEARCH_CRITERIA_DOCUMENT_TYPE_NAME);
            searchCriteriaMain.put(DELEGATION_MEMBER_ATTRIBUTE_VALUE_KEY, financialSystemDocumentTypeCode);
        }                
        String chartOfAccountsCode = fieldValues.get(MEMBER_ATTRIBUTE_CHART_OF_ACCOUNTS_CODE);
        if(StringUtils.isNotBlank(chartOfAccountsCode)){
            searchCriteriaMain.put(DELEGATION_MEMBER_ATTRIBUTE_NAME_KEY, KfsKimAttributes.CHART_OF_ACCOUNTS_CODE);
            searchCriteriaMain.put(DELEGATION_MEMBER_ATTRIBUTE_VALUE_KEY, chartOfAccountsCode);
        }
        String organizationCode = fieldValues.get(MEMBER_ATTRIBUTE_ORGANIZATION_CODE);
        if(StringUtils.isNotBlank(organizationCode)){
            searchCriteriaMain.put(DELEGATION_MEMBER_ATTRIBUTE_NAME_KEY, KfsKimAttributes.ORGANIZATION_CODE);
            searchCriteriaMain.put(DELEGATION_MEMBER_ATTRIBUTE_VALUE_KEY, organizationCode);
        }
        StringBuffer memberQueryString = null;
        if(principals!=null){
            memberQueryString = new StringBuffer();
            for(Person principal: principals){
                memberQueryString.append(principal.getPrincipalId()+KimConstants.KimUIConstants.OR_OPERATOR);
            }
            if(memberQueryString.toString().endsWith(KimConstants.KimUIConstants.OR_OPERATOR))
                memberQueryString.delete(memberQueryString.length()-KimConstants.KimUIConstants.OR_OPERATOR.length(), memberQueryString.length());
            searchCriteriaMain.put(DELEGATION_MEMBER_ID, memberQueryString.toString());
        }
        if(groups!=null){
            if(memberQueryString==null)
                memberQueryString = new StringBuffer();
            else if(StringUtils.isNotEmpty(memberQueryString.toString()))
                memberQueryString.append(KimConstants.KimUIConstants.OR_OPERATOR);
            for(Group group: groups){
                memberQueryString.append(group.getGroupId()+KimConstants.KimUIConstants.OR_OPERATOR);
            }
            if(memberQueryString.toString().endsWith(KimConstants.KimUIConstants.OR_OPERATOR))
                memberQueryString.delete(memberQueryString.length()-KimConstants.KimUIConstants.OR_OPERATOR.length(), memberQueryString.length());
            searchCriteriaMain.put(DELEGATION_MEMBER_ID, memberQueryString.toString());
        }
        if(roles!=null){
            if(memberQueryString==null)
                memberQueryString = new StringBuffer();
            else if(StringUtils.isNotEmpty(memberQueryString.toString()))
                memberQueryString.append(KimConstants.KimUIConstants.OR_OPERATOR);
            for(Role role: roles){
                memberQueryString.append(role.getId()+KimConstants.KimUIConstants.OR_OPERATOR);
            }
            if(memberQueryString.toString().endsWith(KimConstants.KimUIConstants.OR_OPERATOR))
                memberQueryString.delete(memberQueryString.length()-KimConstants.KimUIConstants.OR_OPERATOR.length(), memberQueryString.length());
            searchCriteriaMain.put(DELEGATION_MEMBER_ID, memberQueryString.toString());
        }
        return addRoleToConsiderSearchCriteria(documentTypeName, searchCriteriaMain);
    }

    /**
     * @return the roleService
     */
    public RoleService getRoleService() {
        if(roleManagementService == null){
            roleManagementService = SpringContext.getBean(RoleService.class);
        }
        return this.roleManagementService;
    }

    /**
     * @param roleService the roleService to set
     */
    public void setRoleService(RoleService roleManagementService) {
        this.roleManagementService = roleManagementService;
    }

    public DocumentTypeService getDocumentTypeService() {
        if ( documentTypeService == null ) {
            documentTypeService = SpringContext.getBean(DocumentTypeService.class);
        }
        return this.documentTypeService;
    }

    public void validateDocumentType(String documentTypeName){
        try{
            getRolesToConsider(documentTypeName);
        } catch(Exception ex){
            throw new ValidationException(ex.getMessage());
        }
    }
    
    @Override
    public void validateSearchParameters(Map fieldValues) {
        String documentTypeName = (String)fieldValues.get(DOCUMENT_TYPE_NAME);
        /*if (StringUtils.isEmpty(documentTypeName)){
            throw new ValidationException("Please select a document type name.");
        }*/
        validateDocumentType(documentTypeName);
        super.validateSearchParameters(fieldValues);
    }

    /**
     * Gets the groupService attribute. 
     * @return Returns the groupService.
     */
    public GroupService getGroupService() {
        if(groupService==null){
            groupService = SpringContext.getBean(GroupService.class);
        }
        return groupService;
    }

    /**
     * Sets the groupService attribute value.
     * @param groupService The groupService to set.
     */
    public void setGroupService(GroupService groupService) {
        this.groupService = groupService;
    }

    /**
     * Gets the identityManagementService attribute. 
     * @return Returns the identityManagementService.
     */
    public IdentityManagementService getIdentityManagementService() {
        if(identityManagementService==null){
            identityManagementService = SpringContext.getBean(IdentityManagementService.class);
        }
        return identityManagementService;
    }

    /**
     * Sets the identityManagementService attribute value.
     * @param identityManagementService The identityManagementService to set.
     */
    public void setIdentityManagementService(IdentityManagementService identityManagementService) {
        this.identityManagementService = identityManagementService;
    }

    protected KimTypeInfoService getTypeInfoService(){
        if(typeInfoService==null){
            typeInfoService = SpringContext.getBean(KimTypeInfoService.class);
        }
        return typeInfoService;
    }
    protected RiceCacheAdministrator getCacheAdministrator(){
        if(cacheAdministrator==null){
            cacheAdministrator = SpringContext.getBean(RiceCacheAdministrator.class);   
        }
        return cacheAdministrator;
    }
}
