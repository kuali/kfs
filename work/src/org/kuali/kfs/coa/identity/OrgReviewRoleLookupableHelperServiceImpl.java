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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.service.OrgReviewRoleService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.core.api.criteria.PredicateUtils;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.core.api.delegation.DelegationType;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.common.delegate.DelegateMember;
import org.kuali.rice.kim.api.common.delegate.DelegateType;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.role.DelegateMemberQueryResults;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.role.RoleMember;
import org.kuali.rice.kim.api.role.RoleMemberQueryResults;
import org.kuali.rice.kim.api.role.RoleQueryResults;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kns.document.authorization.BusinessObjectRestrictions;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.UrlFactory;
public class OrgReviewRoleLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(OrgReviewRoleLookupableHelperServiceImpl.class);

    protected OrgReviewRoleService orgReviewRoleService;

    protected static final String MEMBER_ID = "memberId";
    protected static final String MEMBER_ATTRIBUTE_NAME_KEY = "attributeDetails.kimAttribute.attributeName";
    protected static final String MEMBER_ATTRIBUTE_VALUE_KEY = "attributeDetails.attributeValue";

    protected static final Map<String,Map<DelegationType,String>> DELEGATION_ID_CACHE = new HashMap<String, Map<DelegationType,String>>(2);

    protected static final String DELEGATE_SEARCH_IND = "delegate";
    protected static final String ACTIVE_SEARCH_IND = "active";

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
            HtmlData createDelegationUrl = getCreateDelegationUrl(businessObject, pkNames);
            //createDelegationUrl.setDisplayText(KFSConstants.COAConstants.ORG_REVIEW_ROLE_CREATE_DELEGATION_DISPLAY_TEXT);
            htmlDataList.add(createDelegationUrl);
        }
        return htmlDataList;
    }

    protected AnchorHtmlData getCreateDelegationUrl(BusinessObject businessObject, List pkNames){
        OrgReviewRole orr = (OrgReviewRole)businessObject;
        Properties parameters = new Properties();
        parameters.put(OrgReviewRole.ORIGINAL_DELEGATION_MEMBER_ID_TO_MODIFY, OrgReviewRole.NEW_DELEGATION_ID_KEY_VALUE);
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KRADConstants.MAINTENANCE_EDIT_METHOD_TO_CALL);
        parameters.put(KRADConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, businessObject.getClass().getName());
        parameters.put(KRADConstants.COPY_KEYS, OrgReviewRole.ORIGINAL_DELEGATION_MEMBER_ID_TO_MODIFY+","+OrgReviewRole.ORIGINAL_ROLE_MEMBER_ID_TO_MODIFY+","+KRADConstants.DISPATCH_REQUEST_PARAMETER);
        parameters.put(OrgReviewRole.ORIGINAL_ROLE_MEMBER_ID_TO_MODIFY, orr.getRoleMemberId());
        String href = UrlFactory.parameterizeUrl(KRADConstants.MAINTENANCE_ACTION, parameters);

        return new AnchorHtmlData(href, KRADConstants.DOC_HANDLER_METHOD, KFSConstants.COAConstants.ORG_REVIEW_ROLE_CREATE_DELEGATION_DISPLAY_TEXT);
    }

    @Override
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

    protected List<String> getOverridePKNamesForActionLinks(OrgReviewRole orr){
        List overridePKNames = new ArrayList<String>();
        if(orr.isDelegate()) {
            overridePKNames.add(KimConstants.PrimaryKeyConstants.DELEGATION_MEMBER_ID);
        } else {
            overridePKNames.add(KimConstants.PrimaryKeyConstants.ROLE_MEMBER_ID);
        }
        return overridePKNames;
    }

    @Override
    protected String getActionUrlTitleText(BusinessObject businessObject, String displayText, List pkNames, BusinessObjectRestrictions businessObjectRestrictions){
        OrgReviewRole orr = (OrgReviewRole)businessObject;
        return super.getActionUrlTitleText(businessObject, displayText, getOverridePKNamesForActionLinks(orr), businessObjectRestrictions);
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
        String delegateSearchIndicator = fieldValues.get(DELEGATE_SEARCH_IND);
        String documentTypeName = fieldValues.get(KfsKimAttributes.FINANCIAL_SYSTEM_DOCUMENT_TYPE_CODE);
        List<KfsKimDocDelegateMember> searchResultsDelegationMembers = new ArrayList<KfsKimDocDelegateMember>();
        List<RoleMember> searchResultsRoleMembers = new ArrayList<RoleMember>();

        boolean delegateBoolean = getBooleanValueForString(delegateSearchIndicator, false);

        if(StringUtils.isBlank(delegateSearchIndicator) || delegateBoolean ) {
            Map<String, String> searchCriteriaDelegations = buildOrgReviewRoleSearchCriteriaForDelegations(documentTypeName, fieldValues);
            if ( searchCriteriaDelegations != null ) {
                searchResultsDelegationMembers.addAll(searchDelegations(searchCriteriaDelegations));
            }
        }
        if(StringUtils.isBlank(delegateSearchIndicator) || !delegateBoolean ) {
            Map<String, String> searchCriteriaRoleMembers = buildOrgReviewRoleSearchCriteria(documentTypeName, fieldValues);
            if ( searchCriteriaRoleMembers != null ) {
                searchResultsRoleMembers.addAll(searchRoleMembers(searchCriteriaRoleMembers));
            }
        }
        List<OrgReviewRole> flattenedSearchResults = new ArrayList<OrgReviewRole>();
        flattenedSearchResults.addAll(flattenToOrgReviewMembers(fieldValues.get(ACTIVE_SEARCH_IND), documentTypeName, searchResultsRoleMembers));
        flattenedSearchResults.addAll(flattenToOrgReviewDelegationMembers(fieldValues.get(ACTIVE_SEARCH_IND), documentTypeName, searchResultsDelegationMembers));
        filterOrgReview(fieldValues, flattenedSearchResults);

        return flattenedSearchResults;
    }

    protected List<Person> getPersonsForWildcardedPrincipalName( String principalName ) {
        if(StringUtils.isNotBlank(principalName)){
            return KimApiServiceLocator.getPersonService().findPeople( Collections.singletonMap(KimConstants.UniqueKeyConstants.PRINCIPAL_NAME, getQueryString(principalName)) );
        }
        return null;
    }

    protected List<String> getGroupIdsForWildcardedGroupName( String namespaceCode, String groupName ) {
        Map<String, String> searchCriteria = new HashMap<String, String>(2);
        if( StringUtils.isNotBlank(namespaceCode) ) {
            searchCriteria.put(KimConstants.UniqueKeyConstants.NAMESPACE_CODE, namespaceCode);
        }
        if( StringUtils.isNotBlank(groupName) ) {
            searchCriteria.put(KimConstants.UniqueKeyConstants.GROUP_NAME, getQueryString(groupName));
        }
        if ( searchCriteria.isEmpty() ) {
            return null;
        }
        return KimApiServiceLocator.getGroupService().findGroupIds( QueryByCriteria.Builder.fromPredicates(PredicateUtils.convertMapToPredicate(searchCriteria) ) );
    }

    protected List<Role> getRolesForWildcardedRoleName( String namespaceCode, String roleName ) {
        Map<String, String> searchCriteria = new HashMap<String, String>(2);
        if( StringUtils.isNotBlank(namespaceCode) ) {
            searchCriteria.put(KimConstants.UniqueKeyConstants.NAMESPACE_CODE, namespaceCode);
        }
        if( StringUtils.isNotBlank(roleName) ) {
            searchCriteria.put(KimConstants.UniqueKeyConstants.ROLE_NAME, getQueryString(roleName));
        }
        if ( searchCriteria.isEmpty() ) {
            return null;
        }
        RoleQueryResults results = KimApiServiceLocator.getRoleService().findRoles( QueryByCriteria.Builder.fromPredicates(PredicateUtils.convertMapToPredicate(searchCriteria) ) );
        if ( results == null || results.getResults().isEmpty() ) {
            return null;
        }
        return results.getResults();
    }

    protected void filterOrgReview(Map<String, String> fieldValues, List<OrgReviewRole> searchResults){
        if ( LOG.isDebugEnabled() ) {
            LOG.debug( "Performing local filtering of search results" );
            LOG.debug( "Criteria: " + fieldValues );
            LOG.debug( "Initial Results: " + searchResults );
        }


        List<Person> principals = getPersonsForWildcardedPrincipalName(fieldValues.get(OrgReviewRole.PRINCIPAL_NAME_FIELD_NAME));

        List<String> groupIds = getGroupIdsForWildcardedGroupName(
                fieldValues.get(OrgReviewRole.GROUP_NAME_FIELD_NAMESPACE_CODE),
                fieldValues.get(OrgReviewRole.GROUP_NAME_FIELD_NAME));

        List<Role> roles = getRolesForWildcardedRoleName(
                fieldValues.get(OrgReviewRole.ROLE_NAME_FIELD_NAMESPACE_CODE),
                fieldValues.get(OrgReviewRole.ROLE_NAME_FIELD_NAME));

        String financialSystemDocumentTypeCode = fieldValues.get(KfsKimAttributes.FINANCIAL_SYSTEM_DOCUMENT_TYPE_CODE);
        String chartOfAccountsCode = fieldValues.get(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        String organizationCode = fieldValues.get(KFSPropertyConstants.ORGANIZATION_CODE);

        //Loop through org review roles and remove rows where necessary
        Iterator<OrgReviewRole> it = searchResults.iterator();

    	while ( it.hasNext() ) {
            OrgReviewRole orgReviewRole = it.next();
            boolean remove = false;

    	    //check member attribute parameters
    	    if(StringUtils.isNotBlank(organizationCode)){
    	        //filter by chart/document type if they exist
    	        if(StringUtils.isNotBlank(chartOfAccountsCode)){
    	            if(!chartOfAccountsCode.equals(orgReviewRole.getChartOfAccountsCode())){
    	                LOG.debug( "Removing RoleMember because chart does not match" );
    	                remove = true;
    	            }
    	        }

    	        if(StringUtils.isNotBlank(financialSystemDocumentTypeCode)){
    	            if(!financialSystemDocumentTypeCode.equals(orgReviewRole.getFinancialSystemDocumentTypeCode())){
    	                LOG.debug( "Removing RoleMember because document type does not match" );
    	                remove = true;
    	            }
    	        }
            }else if(StringUtils.isNotBlank(chartOfAccountsCode)){
                //filter by document type if it exists
                if(StringUtils.isNotBlank(financialSystemDocumentTypeCode)){
                    if(!financialSystemDocumentTypeCode.equals(orgReviewRole.getFinancialSystemDocumentTypeCode())){
                        LOG.debug( "Removing RoleMember because document type does not match" );
                        remove = true;
                    }
                }
            }

    	    List<String> items = new ArrayList<String>();

    	    //check member id parameters, and only if it hasn't already been marked for removal.
    	    if(!remove){
                if(roles!=null){
                    if(groupIds!=null){
                        for(String groupId: groupIds){
                            items.add(groupId);
                        }
                        if(!items.contains(orgReviewRole.getGroupMemberGroupId())){
                            LOG.debug( "Removing RoleMember because group member id does not match" );
                            remove = true;
                        }
                    }
                    if(principals!=null){
                        for(Person principal: principals){
                            items.add(principal.getPrincipalId());
                        }
                        if(!items.contains(orgReviewRole.getPrincipalMemberPrincipalId())){
                            LOG.debug( "Removing RoleMember because principal id does not match" );
                            remove = true;
                        }
                    }

                }else if(groupIds!=null){
                    if(principals!=null){
                        for(Person principal: principals){
                            items.add(principal.getPrincipalId());
                        }
                        if(!items.contains(orgReviewRole.getPrincipalMemberPrincipalId())){
                            LOG.debug( "Removing RoleMember because principal id does not match" );
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

    protected String getDelegationIdByRoleAndDelegationType( String roleId, DelegationType type ) {
        if ( StringUtils.isBlank( roleId ) || type == null ) {
            return null;
        }
        Map<DelegationType,String> tempCache = DELEGATION_ID_CACHE.get(roleId);
        if ( tempCache == null ) {
            tempCache = new HashMap<DelegationType, String>(2);
            DelegateType dt = KimApiServiceLocator.getRoleService().getDelegateTypeByRoleIdAndDelegateTypeCode(roleId, type);
            if ( dt != null ) {
                tempCache.put(type, dt.getDelegationId() );
            }
            synchronized (DELEGATION_ID_CACHE) {
                DELEGATION_ID_CACHE.put(roleId, tempCache);
            }
        } else {
            if ( !tempCache.containsKey(type) ) {
                DelegateType dt = KimApiServiceLocator.getRoleService().getDelegateTypeByRoleIdAndDelegateTypeCode(roleId, type);
                if ( dt != null ) {
                    synchronized (DELEGATION_ID_CACHE) {
                        tempCache.put(type, dt.getDelegationId() );
                    }
                }
            }
        }
        return tempCache.get(type);
    }

    protected void addDelegationsToDelegationMemberSearchCriteria(String documentTypeName, Map<String, String> searchCriteria){
        List<String> roleNamesToSearchInto = orgReviewRoleService.getRolesToConsider(documentTypeName);
        if(roleNamesToSearchInto!=null){
            StringBuilder rolesQueryString = new StringBuilder();
            boolean firstItem = true;
            for( String roleName : roleNamesToSearchInto ) {
                String roleId = KimApiServiceLocator.getRoleService().getRoleIdByNamespaceCodeAndName(KFSConstants.SysKimApiConstants.ORGANIZATION_REVIEWER_ROLE_NAMESPACECODE, roleName);
                if ( roleId != null ) {
                    for ( DelegationType type : DelegationType.values() ) {
                        String delegationId = getDelegationIdByRoleAndDelegationType(roleId, type);
                        if ( delegationId != null ) {
                            if ( !firstItem ) {
                                rolesQueryString.append( KimConstants.KimUIConstants.OR_OPERATOR );
                            } else {
                                firstItem = false;
                            }
                            rolesQueryString.append( delegationId );
                        }
                    }
                }
            }
            if ( rolesQueryString.length() > 0 ) {
                searchCriteria.put("delegationId", rolesQueryString.toString());
            }
        }
    }

    protected void addRolesToRoleMemberSearchCriteria(String documentTypeName, Map<String, String> searchCriteria){
        List<String> roleNamesToSearchInto = orgReviewRoleService.getRolesToConsider(documentTypeName);
        if( roleNamesToSearchInto != null) {
            StringBuilder rolesQueryString = new StringBuilder();
            boolean firstItem = true;
            for ( String roleName : roleNamesToSearchInto ) {
                String roleId = KimApiServiceLocator.getRoleService().getRoleIdByNamespaceCodeAndName(KFSConstants.SysKimApiConstants.ORGANIZATION_REVIEWER_ROLE_NAMESPACECODE, roleName);
                if ( roleId != null ) {
                    if ( !firstItem ) {
                        rolesQueryString.append( KimConstants.KimUIConstants.OR_OPERATOR );
                    } else {
                        firstItem = false;
                    }
                    rolesQueryString.append( roleId );
                }
            }
            if ( rolesQueryString.length() > 0 ) {
                searchCriteria.put("roleId", rolesQueryString.toString());
            }
        }
    }

    protected List<RoleMember> searchRoleMembers(Map<String, String> searchCriteriaRoleMembers){
        if ( searchCriteriaRoleMembers == null ) {
            return Collections.emptyList();
        }
        if ( LOG.isDebugEnabled() ) {
            LOG.debug("About to perform Role lookup.  Criteria: " + searchCriteriaRoleMembers);
        }
        RoleMemberQueryResults results = KimApiServiceLocator.getRoleService().findRoleMembers( QueryByCriteria.Builder.fromPredicates( PredicateUtils.convertMapToPredicate(searchCriteriaRoleMembers)));
        if ( LOG.isDebugEnabled() ) {
            LOG.debug( "Role Member Lookup Results from KIM: " + results );
        }
        if ( results == null ) {
            return Collections.emptyList();
        }
        return results.getResults();
    }

    protected List<KfsKimDocDelegateMember> searchDelegations(Map<String, String> searchCriteriaDelegateMembers){
        DelegateMemberQueryResults queryResults = KimApiServiceLocator.getRoleService().findDelegateMembers(QueryByCriteria.Builder.fromPredicates( PredicateUtils.convertMapToPredicate(searchCriteriaDelegateMembers)));
        List<DelegateMember> tmpResults = queryResults.getResults();
        Integer totalRowCount = tmpResults.size();
        ArrayList<KfsKimDocDelegateMember> results = new ArrayList<KfsKimDocDelegateMember>(totalRowCount);
        if ( totalRowCount > 0 ) {
            for ( DelegateMember dm : tmpResults ) {
                results.add( new KfsKimDocDelegateMember( dm ) );
            }
        }
        return results;
    }




    private static final String[] TRUE_VALUES = new String[] { "true", "yes", "t", "y" };
    public static boolean getBooleanValueForString(String value, boolean defaultValue) {
        if (!StringUtils.isBlank(value)) {
            for (String trueValue : TRUE_VALUES) {
                if (value.equalsIgnoreCase(trueValue)) {
                    return true;
                }
            }
            return false;
        }
        return defaultValue;
    }

    protected List<OrgReviewRole> flattenToOrgReviewMembers(String active, String documentTypeName, List<RoleMember> members){
        LOG.debug( "\"flattening\" RoleMember objects to OrgReviewRole object" );
        if(members==null || members.isEmpty() ) {
            return Collections.emptyList();
        }

        boolean activeInd = getBooleanValueForString(active, true);
        List<OrgReviewRole> orgReviewRoles = new ArrayList<OrgReviewRole>();
        for(RoleMember member: members){
            if ( LOG.isDebugEnabled() ) {
                LOG.debug( "Converting Role Member: " + member );
            }
            if(StringUtils.isBlank(active) || activeInd == member.isActive() ) {
                OrgReviewRole orgReviewRole = new OrgReviewRole();
                orgReviewRole.setRoleMember(member);

                if ( LOG.isDebugEnabled() ) {
                    LOG.debug( "Converted To OrgReviewRole: " + orgReviewRole );
                }
                orgReviewRoles.add(orgReviewRole);
            } else {
                LOG.debug( "RoleMember skipped because it did not match the active flag on the lookup" );
            }
        }
        return orgReviewRoles;
    }

    protected List<OrgReviewRole> flattenToOrgReviewDelegationMembers(String active, String documentTypeName, List<KfsKimDocDelegateMember> delegationMembers){
        if( delegationMembers == null ) {
            return Collections.emptyList();
        }

        List<OrgReviewRole> orgReviewRoles = new ArrayList<OrgReviewRole>();
        boolean activeInd = getBooleanValueForString(active, true);
        for(KfsKimDocDelegateMember member: delegationMembers){
            if ( LOG.isDebugEnabled() ) {
                LOG.debug( "Converting Delegation Member: " + member );
            }
            if( activeInd != member.isActive() ) {
                OrgReviewRole orgReviewRole = new OrgReviewRole();
                orgReviewRole.setDelegateMember(member);
                if ( LOG.isDebugEnabled() ) {
                    LOG.debug( "Converted To OrgReviewRole: " + orgReviewRole );
                }
                orgReviewRoles.add(orgReviewRole);
            }
        }
        return orgReviewRoles;
    }

    protected String getQueryString(String parameter){
        if(StringUtils.isBlank(parameter)) {
            return KFSConstants.WILDCARD_CHARACTER;
        } else {
            return KFSConstants.WILDCARD_CHARACTER+parameter+KFSConstants.WILDCARD_CHARACTER;
        }
    }

    protected Map<String, String> buildOrgReviewRoleSearchCriteria(String documentTypeName, Map<String, String> fieldValues){

        String principalName = fieldValues.get(OrgReviewRole.PRINCIPAL_NAME_FIELD_NAME);

        List<Person> principals = getPersonsForWildcardedPrincipalName(principalName);
        if( StringUtils.isNotBlank(principalName) && (principals == null || principals.isEmpty()) ) {
            return null;
        }

        List<String> groupIds = getGroupIdsForWildcardedGroupName(
                fieldValues.get(OrgReviewRole.GROUP_NAME_FIELD_NAMESPACE_CODE),
                fieldValues.get(OrgReviewRole.GROUP_NAME_FIELD_NAME));
        if( StringUtils.isNotBlank(fieldValues.get(OrgReviewRole.GROUP_NAME_FIELD_NAME)) && (groupIds == null || groupIds.isEmpty()) ) {
            return null;
        }

        List<Role> roles = getRolesForWildcardedRoleName(
                fieldValues.get(OrgReviewRole.ROLE_NAME_FIELD_NAMESPACE_CODE),
                fieldValues.get(OrgReviewRole.ROLE_NAME_FIELD_NAME));
        if( StringUtils.isNotBlank(fieldValues.get(OrgReviewRole.ROLE_NAME_FIELD_NAME)) && (roles == null || roles.isEmpty()) ) {
            return null;
        }

        String financialSystemDocumentTypeCode = fieldValues.get(KfsKimAttributes.FINANCIAL_SYSTEM_DOCUMENT_TYPE_CODE);
        String chartOfAccountsCode = fieldValues.get(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        String organizationCode = fieldValues.get(KFSPropertyConstants.ORGANIZATION_CODE);

        Map<String, String> searchCriteriaMain = new HashMap<String, String>();

        // FIXME: This is horribly broken, as these attributes are overriding each other
        // this needs to be completely refactored
        if(StringUtils.isNotBlank(financialSystemDocumentTypeCode)){
            searchCriteriaMain.put(MEMBER_ATTRIBUTE_NAME_KEY, KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME);
            searchCriteriaMain.put(MEMBER_ATTRIBUTE_VALUE_KEY, financialSystemDocumentTypeCode);
        }
        if(StringUtils.isNotBlank(chartOfAccountsCode)){
            searchCriteriaMain.put(MEMBER_ATTRIBUTE_NAME_KEY, KfsKimAttributes.CHART_OF_ACCOUNTS_CODE);
            searchCriteriaMain.put(MEMBER_ATTRIBUTE_VALUE_KEY, chartOfAccountsCode);
        }
        if(StringUtils.isNotBlank(organizationCode)){
            searchCriteriaMain.put(MEMBER_ATTRIBUTE_NAME_KEY, KfsKimAttributes.ORGANIZATION_CODE);
            searchCriteriaMain.put(MEMBER_ATTRIBUTE_VALUE_KEY, organizationCode);
        }

        String memberIdString = buildMemberIdLookupString(principals, groupIds, roles);
        if ( StringUtils.isNotBlank(memberIdString) ) {
            searchCriteriaMain.put(MEMBER_ID, memberIdString );
        }
        addRolesToRoleMemberSearchCriteria(documentTypeName, searchCriteriaMain);
        return searchCriteriaMain;
    }

    protected String buildMemberIdLookupString( List<Person> principals, List<String> groupIds, List<Role> roles ) {
        StringBuilder memberQueryString = new StringBuilder();
        boolean firstMember = true;
        if(principals!=null){
            memberQueryString = new StringBuilder();
            for(Person principal: principals){
                if ( !firstMember ) {
                    memberQueryString.append(KimConstants.KimUIConstants.OR_OPERATOR);
                } else {
                    firstMember = false;
                }
                memberQueryString.append(principal.getPrincipalId());
            }
        }
        if(groupIds!=null){
            for(String groupId: groupIds){
                if ( !firstMember ) {
                    memberQueryString.append(KimConstants.KimUIConstants.OR_OPERATOR);
                } else {
                    firstMember = false;
                }
                memberQueryString.append(groupId);
            }
        }
        if(roles!=null){
            for(Role role: roles){
                if ( !firstMember ) {
                    memberQueryString.append(KimConstants.KimUIConstants.OR_OPERATOR);
                } else {
                    firstMember = false;
                }
                memberQueryString.append(role.getId());
            }
        }
        return memberQueryString.toString();
    }

    protected Map<String, String> buildOrgReviewRoleSearchCriteriaForDelegations(String documentTypeName, Map<String, String> fieldValues){
        String principalName = fieldValues.get(OrgReviewRole.PRINCIPAL_NAME_FIELD_NAME);

        // if a principal ID was given, and no match, short-circuit and return null
        List<Person> principals = getPersonsForWildcardedPrincipalName(principalName);
        if( StringUtils.isNotBlank(principalName) && (principals == null || principals.isEmpty()) ) {
            return null;
        }

        // if a group was given, and no matches, short-circuit and return null
        List<String> groupIds = getGroupIdsForWildcardedGroupName(
                fieldValues.get(OrgReviewRole.GROUP_NAME_FIELD_NAMESPACE_CODE),
                fieldValues.get(OrgReviewRole.GROUP_NAME_FIELD_NAME));
        if( StringUtils.isNotBlank(fieldValues.get(OrgReviewRole.GROUP_NAME_FIELD_NAME)) && (groupIds == null || groupIds.isEmpty()) ) {
            return null;
        }

        // if a role was given, and no matches, short-circuit and return null
        List<Role> roles = getRolesForWildcardedRoleName(
                fieldValues.get(OrgReviewRole.ROLE_NAME_FIELD_NAMESPACE_CODE),
                fieldValues.get(OrgReviewRole.ROLE_NAME_FIELD_NAME));
        if( StringUtils.isNotBlank(fieldValues.get(OrgReviewRole.ROLE_NAME_FIELD_NAME)) && (roles == null || roles.isEmpty()) ) {
            return null;
        }

        // pull the other main three search criteria
        String financialSystemDocumentTypeCode = fieldValues.get(KfsKimAttributes.FINANCIAL_SYSTEM_DOCUMENT_TYPE_CODE);
        String chartOfAccountsCode = fieldValues.get(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        String organizationCode = fieldValues.get(KFSPropertyConstants.ORGANIZATION_CODE);

        // FIXME: the code below only uses one of the critieria specified (the entries in the map are overwritten if more than one is used)
        Map<String, String> searchCriteriaMain = new HashMap<String, String>();
        if(StringUtils.isNotBlank(chartOfAccountsCode)){
            searchCriteriaMain.put(MEMBER_ATTRIBUTE_NAME_KEY, KfsKimAttributes.CHART_OF_ACCOUNTS_CODE);
            searchCriteriaMain.put(MEMBER_ATTRIBUTE_VALUE_KEY, chartOfAccountsCode);
        }
        if(StringUtils.isNotBlank(financialSystemDocumentTypeCode)){
            searchCriteriaMain.put(MEMBER_ATTRIBUTE_NAME_KEY, KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME);
            searchCriteriaMain.put(MEMBER_ATTRIBUTE_VALUE_KEY, financialSystemDocumentTypeCode);
        }
        if(StringUtils.isNotBlank(organizationCode)){
            searchCriteriaMain.put(MEMBER_ATTRIBUTE_NAME_KEY, KfsKimAttributes.ORGANIZATION_CODE);
            searchCriteriaMain.put(MEMBER_ATTRIBUTE_VALUE_KEY, organizationCode);
        }


        searchCriteriaMain.put(MEMBER_ID, buildMemberIdLookupString(principals, groupIds, roles));
        addDelegationsToDelegationMemberSearchCriteria(documentTypeName, searchCriteriaMain);
        return searchCriteriaMain;
    }

    @Override
    public void validateSearchParameters(Map fieldValues) {
        if ( fieldValues != null ) {
            orgReviewRoleService.validateDocumentType((String)fieldValues.get(KfsKimAttributes.FINANCIAL_SYSTEM_DOCUMENT_TYPE_CODE));
        }
        super.validateSearchParameters(fieldValues);
    }

    public void setOrgReviewRoleService(OrgReviewRoleService orgReviewRoleService) {
        this.orgReviewRoleService = orgReviewRoleService;
    }


}
