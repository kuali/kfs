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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.service.OrgReviewRoleService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.core.api.criteria.PredicateUtils;
import org.kuali.rice.core.api.criteria.QueryByCriteria;
import org.kuali.rice.kew.api.doctype.DocumentType;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.group.Group;
import org.kuali.rice.kim.api.group.GroupQueryResults;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.role.DelegateMemberQueryResults;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.role.RoleMember;
import org.kuali.rice.kim.api.role.RoleMemberQueryResults;
import org.kuali.rice.kim.api.role.RoleQueryResults;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.api.type.KimType;
import org.kuali.rice.kim.util.KimCommonUtils;
import org.kuali.rice.kns.document.authorization.BusinessObjectRestrictions;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.UrlFactory;
public class OrgReviewRoleLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

//    protected DocumentTypeService documentTypeService;
    protected transient OrgReviewRoleService orgReviewRoleService;
    
    protected static final String WILDCARD = "*";
//    protected static final String DOCUMENT_TYPE_NAME = KfsKimAttributes.FINANCIAL_SYSTEM_DOCUMENT_TYPE_CODE;
//    protected static final String SEARCH_CRITERIA_DOCUMENT_TYPE_NAME = "documentTypeName"; 
    public static final String MEMBER_ATTRIBUTE_CHART_OF_ACCOUNTS_CODE = KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE;
    public static final String MEMBER_ATTRIBUTE_ORGANIZATION_CODE = KFSPropertyConstants.ORGANIZATION_CODE;

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
        String documentTypeName = fieldValues.get(KfsKimAttributes.FINANCIAL_SYSTEM_DOCUMENT_TYPE_CODE);
        List<Class> classesToSearch = new ArrayList<Class>();
        List<KfsKimDocDelegateMember> searchResultsDelegationMembers = new ArrayList<KfsKimDocDelegateMember>();
        List<RoleMember> searchResultsRoleMembers = new ArrayList<RoleMember>();
        Map<String, String> searchCriteriaRoleMembers;
        Map<String, String> searchCriteriaDelegations;
        if(StringUtils.isEmpty(delegateStr)){
            //Search for both, role members and delegations
            searchCriteriaRoleMembers = buildOrgReviewRoleSearchCriteria(documentTypeName, fieldValues);
            searchCriteriaDelegations = buildOrgReviewRoleSearchCriteriaForDelegations(documentTypeName, fieldValues);
            if(searchCriteriaRoleMembers == null && searchCriteriaDelegations==null){
                return new ArrayList<BusinessObject>();
            } else if(searchCriteriaRoleMembers!=null && searchCriteriaDelegations!=null){
                searchResultsRoleMembers.addAll(searchRoleMembers(searchCriteriaRoleMembers));
                searchResultsDelegationMembers.addAll(searchDelegations(searchCriteriaDelegations));
            } else if(searchCriteriaRoleMembers!=null){
                searchResultsRoleMembers.addAll(searchRoleMembers(searchCriteriaRoleMembers));
            } else if(searchCriteriaDelegations!=null){
                searchResultsDelegationMembers.addAll(searchDelegations(searchCriteriaDelegations));
            }
        } else{
            boolean delegate = getBooleanValueForString(delegateStr, false);
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
    
        String principalName = fieldValues.get(OrgReviewRole.PRINCIPAL_NAME_FIELD_NAME);
        List<Person> principals = null;
        if(StringUtils.isNotEmpty(principalName)){
            Map<String, String> criteria = new HashMap<String, String>();
            criteria.put(KimConstants.UniqueKeyConstants.PRINCIPAL_NAME, WILDCARD+principalName+WILDCARD);
            principals = (List<Person>)getPersons(criteria);
        }
        String assignedToGroupNamespaceCode = fieldValues.get(OrgReviewRole.GROUP_NAME_FIELD_NAMESPACE_CODE);
        String assignedToGroupName = fieldValues.get(OrgReviewRole.GROUP_NAME_FIELD_NAME);
        List<Group> groups = null;
        if(StringUtils.isNotEmpty(assignedToGroupNamespaceCode) && StringUtils.isEmpty(assignedToGroupName) ||
                StringUtils.isEmpty(assignedToGroupNamespaceCode) && StringUtils.isNotEmpty(assignedToGroupName) ||
                StringUtils.isNotEmpty(assignedToGroupNamespaceCode) && StringUtils.isNotEmpty(assignedToGroupName)){
            Map<String, String> searchCriteria = new HashMap<String, String>();
            searchCriteria.put(KimConstants.UniqueKeyConstants.NAMESPACE_CODE, getQueryString(assignedToGroupNamespaceCode));
            searchCriteria.put(KimConstants.UniqueKeyConstants.GROUP_NAME, getQueryString(assignedToGroupName));
            groups = getGroups(searchCriteria);
        }

        String assignedToRoleNamespaceCode = fieldValues.get(OrgReviewRole.ROLE_NAME_FIELD_NAMESPACE_CODE);
        String assignedToRoleName = fieldValues.get(OrgReviewRole.ROLE_NAME_FIELD_NAME);

        List<Role> roles = null;
        if(StringUtils.isNotEmpty(assignedToRoleNamespaceCode) && StringUtils.isEmpty(assignedToRoleName) ||
                StringUtils.isEmpty(assignedToRoleNamespaceCode) && StringUtils.isNotEmpty(assignedToRoleName) ||
                StringUtils.isNotEmpty(assignedToRoleNamespaceCode) && StringUtils.isNotEmpty(assignedToRoleName)){
            Map<String, String> searchCriteria = new HashMap<String, String>();
            searchCriteria.put(KimConstants.UniqueKeyConstants.NAMESPACE_CODE, getQueryString(assignedToRoleNamespaceCode));
            searchCriteria.put(KimConstants.UniqueKeyConstants.ROLE_NAME, getQueryString(assignedToRoleName));
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
                            items.add(group.getId());
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
        List<String> roleNamesToSearchInto = orgReviewRoleService.getRolesToConsider(documentTypeName);
        if(searchCriteria==null)
            searchCriteria = new HashMap<String, String>();
        if(roleNamesToSearchInto!=null){
            StringBuilder rolesQueryString = new StringBuilder();
            for(String roleName: roleNamesToSearchInto){
                rolesQueryString.append(
                        KimApiServiceLocator.getRoleService().getRoleIdByNameAndNamespaceCode(KFSConstants.SysKimApiConstants.ORGANIZATION_REVIEWER_ROLE_NAMESPACECODE, roleName)+
                    KimConstants.KimUIConstants.OR_OPERATOR);
            }
            if(rolesQueryString.toString().endsWith(KimConstants.KimUIConstants.OR_OPERATOR))
                rolesQueryString.delete(rolesQueryString.length()-KimConstants.KimUIConstants.OR_OPERATOR.length(), rolesQueryString.length());
            searchCriteria.put(KimConstants.PrimaryKeyConstants.ROLE_ID, rolesQueryString.toString());
        }
        return searchCriteria;
    }

    protected List<RoleMember> searchRoleMembers(Map<String, String> searchCriteriaRoleMembers){        
        RoleMemberQueryResults results = KimApiServiceLocator.getRoleService().findRoleMembers( QueryByCriteria.Builder.fromPredicates( PredicateUtils.convertMapToPredicate(searchCriteriaRoleMembers)));
        return results.getResults();
    }

    protected List<KfsKimDocDelegateMember> searchDelegations(Map<String, String> searchCriteriaDelegateMembers){
        DelegateMemberQueryResults tempResults = KimApiServiceLocator.getRoleService().findDelegateMembers(QueryByCriteria.Builder.fromPredicates( PredicateUtils.convertMapToPredicate(searchCriteriaDelegateMembers)));
        ArrayList<KfsKimDocDelegateMember> results = new ArrayList<KfsKimDocDelegateMember>( tempResults.getTotalRowCount() );
        // RICE20: Need to add missing primary/secondary delegate information
        // may need to then get all for role and match up on delegation ID
        throw new UnsupportedOperationException( "Rice delegation member data does not contain primary/secondary information" );
//        for ( DelegateMember dm : tempResults.getResults() ) {
//            results.add( new KfsKimDocDelegateMember( dm ) );
//        }
//        return results;
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
        List<OrgReviewRole> orgReviewRoles = new ArrayList<OrgReviewRole>();
        if(members==null || members.size()<1) return orgReviewRoles;
        
        OrgReviewRole orgReviewRole;
        String memberType;
        Role roleInfo;
        KimType kimTypeInfo;
        Boolean activeInd = null;
        if(StringUtils.isNotEmpty(active)){
            activeInd = new Boolean(getBooleanValueForString(active, true));
        }
        for(RoleMember member: members){
            if(activeInd==null || (activeInd.booleanValue()==true && member.isActive()) || (activeInd.booleanValue()==false && !member.isActive())){
                orgReviewRole = new OrgReviewRole();
                orgReviewRole.setMemberId(member.getMemberId());
                orgReviewRole.setMemberTypeCode(member.getType().getCode());           
                orgReviewRole.setActiveFromDate(member.getActiveFromDate().toDate());
                orgReviewRole.setActiveToDate(member.getActiveToDate().toDate());
                orgReviewRole.setActive(member.isActive());
                orgReviewRole.setFinancialSystemDocumentTypeCode(documentTypeName);
                
                roleInfo = KimApiServiceLocator.getRoleService().getRole(member.getRoleId());
                kimTypeInfo = KimApiServiceLocator.getKimTypeInfoService().getKimType(roleInfo.getKimTypeId());
                orgReviewRole.setAttributes(orgReviewRole.getAttributeSetAsQualifierList(kimTypeInfo, member.getAttributes()));

                orgReviewRole.setRoleRspActions(KimApiServiceLocator.getRoleService().getRoleMemberResponsibilityActions(member.getId()));
                orgReviewRole.setRoleMemberId(member.getId());
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
    
    protected List<OrgReviewRole> flattenToOrgReviewDelegationMembers(String active, String documentTypeName, List<KfsKimDocDelegateMember> delegationMembers){
        if( delegationMembers == null ) { 
            return Collections.emptyList();
        }
        
        List<OrgReviewRole> orgReviewRoles = new ArrayList<OrgReviewRole>();
        boolean activeInd = getBooleanValueForString(active, true);
        for(KfsKimDocDelegateMember member: delegationMembers){
            if( activeInd != member.isActive() ) {
                OrgReviewRole orgReviewRole = new OrgReviewRole();
                orgReviewRole.setMemberId(member.getMemberId());
                orgReviewRole.setMemberTypeCode(member.getType().getCode());
                if ( member.getActiveFromDate() != null ) {
                    orgReviewRole.setActiveFromDate(member.getActiveFromDate().toDate());
                }
                if ( member.getActiveToDate() != null ) {
                    orgReviewRole.setActiveToDate(member.getActiveToDate().toDate());
                }
                orgReviewRole.setActive(member.isActive());
                orgReviewRole.setFinancialSystemDocumentTypeCode(documentTypeName);

                // this is the role for which this is a delegation
                Role roleInfo = KimApiServiceLocator.getRoleService().getRole(member.getRoleMemberId());
                KimType kimTypeInfo = KimApiServiceLocator.getKimTypeInfoService().getKimType(roleInfo.getKimTypeId());
                orgReviewRole.setAttributes(orgReviewRole.getAttributeSetAsQualifierList(kimTypeInfo, member.getAttributes()));

                orgReviewRole.setDelegationMemberId(member.getDelegationMemberId());
                orgReviewRole.setRoleMemberId(member.getRoleMemberId());
                orgReviewRole.setRoleId(member.getRoleMemberId());
                orgReviewRole.setNamespaceCode(roleInfo.getNamespaceCode());
                orgReviewRole.setRoleName(roleInfo.getName());
                orgReviewRole.setDelegationTypeCode(member.getDelegationType().getCode());
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
    
//    protected DelegateType getDelegation(DelegateMember delegationMember){
//        Map<String, String> criteria = new HashMap<String, String>();
//        DelegateType delegation;
//        criteria.put(KimConstants.PrimaryKeyConstants.DELEGATION_ID, delegationMember.getDelegationId());
//        return (DelegateType)SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(DelegateType.class, criteria);
//    }

    protected String getQueryString(String parameter){
        if(StringUtils.isEmpty(parameter))
            return WILDCARD;
        else
            return WILDCARD+parameter+WILDCARD;
    }

    public List<Person> getPersons(Map<String, String> fieldValues) {
        return (List<Person>) KimApiServiceLocator.getPersonService().findPeople(fieldValues);
    }
    
    public List<Role> getRoles(Map<String, String> fieldValues) {        
        QueryByCriteria crit = QueryByCriteria.Builder.fromPredicates(PredicateUtils.convertMapToPredicate(fieldValues));
        RoleQueryResults results = KimApiServiceLocator.getRoleService().findRoles(crit); 
        return results.getResults();
    }

    public List<Group> getGroups(Map<String, String> fieldValues) {
        QueryByCriteria crit = QueryByCriteria.Builder.fromPredicates(PredicateUtils.convertMapToPredicate(fieldValues));
        GroupQueryResults results = KimApiServiceLocator.getGroupService().findGroups(crit);
        return results.getResults();
    }
    
    protected Map<String, String> buildOrgReviewRoleSearchCriteria(String documentTypeName, Map<String, String> fieldValues){
        String principalName = fieldValues.get(OrgReviewRole.PRINCIPAL_NAME_FIELD_NAME);
        List<Person> principals = null;
        if(StringUtils.isNotEmpty(principalName)){
            Map<String, String> criteria = new HashMap<String, String>();
            criteria.put(KimConstants.UniqueKeyConstants.PRINCIPAL_NAME, WILDCARD+principalName+WILDCARD);
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
            searchCriteria.put(KimConstants.UniqueKeyConstants.NAMESPACE_CODE, getQueryString(assignedToGroupNamespaceCode));
            searchCriteria.put(KimConstants.UniqueKeyConstants.GROUP_NAME, getQueryString(assignedToGroupName));
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
            searchCriteria.put(KimConstants.UniqueKeyConstants.NAMESPACE_CODE, getQueryString(assignedToRoleNamespaceCode));
            searchCriteria.put(KimConstants.UniqueKeyConstants.ROLE_NAME, getQueryString(assignedToRoleName));
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
        StringBuilder memberQueryString = new StringBuilder();
        boolean firstMember = true;
        if(principals!=null){
            for(Person principal: principals){
                if ( !firstMember ) {
                    memberQueryString.append(KimConstants.KimUIConstants.OR_OPERATOR);
                } else {
                    firstMember = false;
                }
                memberQueryString.append(principal.getPrincipalId());
            }
        }
        if(groups!=null){
            for(Group group: groups){
                if ( !firstMember ) {
                    memberQueryString.append(KimConstants.KimUIConstants.OR_OPERATOR);
                } else {
                    firstMember = false;
                }
                memberQueryString.append(group.getId());
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
        searchCriteriaMain.put(MEMBER_ID, memberQueryString.toString());
        return addRoleToConsiderSearchCriteria(documentTypeName, searchCriteriaMain);
    }
    
    protected Map<String, String> buildOrgReviewRoleSearchCriteriaForDelegations(String documentTypeName, Map<String, String> fieldValues){
        String principalName = fieldValues.get(MEMBER_PRINCIPAL_NAME);
        Map<String, String> searchCriteriaTemp;
        List<Person> principals = null;
        if(StringUtils.isNotEmpty(principalName)){
            searchCriteriaTemp = new HashMap<String, String>();
            searchCriteriaTemp.put(KimConstants.UniqueKeyConstants.PRINCIPAL_NAME, WILDCARD+principalName+WILDCARD);
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
            searchCriteria.put(KimConstants.UniqueKeyConstants.NAMESPACE_CODE, getQueryString(assignedToGroupNamespaceCode));
            searchCriteria.put(KimConstants.UniqueKeyConstants.GROUP_NAME, getQueryString(assignedToGroupName));
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
            searchCriteria.put(KimConstants.UniqueKeyConstants.NAMESPACE_CODE, getQueryString(assignedToRoleNamespaceCode));
            searchCriteria.put(KimConstants.UniqueKeyConstants.ROLE_NAME, getQueryString(assignedToRoleName));
            roles = getRoles(searchCriteria);
            if(roles==null || roles.size()==0)
                return null;
        }

        final Map<String, String> searchCriteriaMain = new HashMap<String, String>();
        String financialSystemDocumentTypeCode = fieldValues.get(KfsKimAttributes.FINANCIAL_SYSTEM_DOCUMENT_TYPE_CODE);
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
        StringBuilder memberQueryString = null;
        if(principals!=null){
            memberQueryString = new StringBuilder();
            for(Person principal: principals){
                memberQueryString.append(principal.getPrincipalId()+KimConstants.KimUIConstants.OR_OPERATOR);
            }
            if(memberQueryString.toString().endsWith(KimConstants.KimUIConstants.OR_OPERATOR))
                memberQueryString.delete(memberQueryString.length()-KimConstants.KimUIConstants.OR_OPERATOR.length(), memberQueryString.length());
            searchCriteriaMain.put(DELEGATION_MEMBER_ID, memberQueryString.toString());
        }
        if(groups!=null){
            if(memberQueryString==null)
                memberQueryString = new StringBuilder();
            else if(StringUtils.isNotEmpty(memberQueryString.toString()))
                memberQueryString.append(KimConstants.KimUIConstants.OR_OPERATOR);
            for(Group group: groups){
                memberQueryString.append(group.getId()+KimConstants.KimUIConstants.OR_OPERATOR);
            }
            if(memberQueryString.toString().endsWith(KimConstants.KimUIConstants.OR_OPERATOR))
                memberQueryString.delete(memberQueryString.length()-KimConstants.KimUIConstants.OR_OPERATOR.length(), memberQueryString.length());
            searchCriteriaMain.put(DELEGATION_MEMBER_ID, memberQueryString.toString());
        }
        if(roles!=null){
            if(memberQueryString==null)
                memberQueryString = new StringBuilder();
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

    @Override
    public void validateSearchParameters(Map fieldValues) {
        orgReviewRoleService.validateDocumentType((String)fieldValues.get(KfsKimAttributes.FINANCIAL_SYSTEM_DOCUMENT_TYPE_CODE));
        super.validateSearchParameters(fieldValues);
    }

    public void setOrgReviewRoleService(OrgReviewRoleService orgReviewRoleService) {
        this.orgReviewRoleService = orgReviewRoleService;
    }


}
