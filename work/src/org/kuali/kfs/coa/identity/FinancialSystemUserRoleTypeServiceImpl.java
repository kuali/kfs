/*
 * Copyright 2008 The Kuali Foundation
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
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.role.RoleMembership;
import org.kuali.rice.kim.bo.types.dto.AttributeDefinitionMap;
import org.kuali.rice.kns.kim.role.RoleTypeServiceBase;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;



/**
 * This role type service was designed to be attached to the KFS-SYS User role
 */
public class FinancialSystemUserRoleTypeServiceImpl extends RoleTypeServiceBase {
    public static final String FINANCIAL_SYSTEM_USER_ROLE_NAME = "User";
    public static final String PERFORM_QUALIFIER_MATCH = "performQualifierMatch";
    






    @Override
    protected boolean performMatch(Map<String,String> qualification, Map<String,String> roleQualifier) {
        // if we can not find the qualifier which tells us to perform the match, just return true
        if (qualification == null || !Boolean.parseBoolean(qualification.get(PERFORM_QUALIFIER_MATCH))) {
            return true;
        }
        // if chart and org aren't on the assignment we don't care, since we are only matching to get the default chart/org for a
        // user in FinancialSystemUserServiceImpl
        if (StringUtils.isBlank(roleQualifier.get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE)) || StringUtils.isBlank(roleQualifier.get(KfsKimAttributes.ORGANIZATION_CODE))) {
            return false;
        }
        // exact match on namespace or qualifier namespace of kfs-sys works
        if (StringUtils.equals(roleQualifier.get(KimConstants.AttributeConstants.NAMESPACE_CODE), qualification.get(KimConstants.AttributeConstants.NAMESPACE_CODE)) || StringUtils.equals(KFSConstants.ParameterNamespaces.KFS, roleQualifier.get(KimConstants.AttributeConstants.NAMESPACE_CODE))) {
            return true;
        }
        return false;
    }

    @Override
    public List<RoleMembership> doRoleQualifiersMatchQualification(Map<String,String> qualification, List<RoleMembership> roleMemberList) {
        Map<String,String> translatedQualification = translateInputAttributes(qualification);
        validateRequiredAttributesAgainstReceived(translatedQualification);
        // if we can not find the qualifier which tells us to perform the match, just return all rows
        if (translatedQualification == null || translatedQualification.isEmpty() || !Boolean.parseBoolean(translatedQualification.get(PERFORM_QUALIFIER_MATCH))) {
            return roleMemberList;
        }
        // perform special matching to make a match that includes the namespace take priority
        RoleMembership namespaceMatch = null;
        RoleMembership nonNamespaceMatch = null;
        // look for match
        for (RoleMembership rmi : roleMemberList) {
            // if chart and org aren't on the assignment we don't care, since we are only matching to get the default chart/org for
            // a user in FinancialSystemUserServiceImpl
            if (!(StringUtils.isBlank(rmi.getQualifier().get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE)) || StringUtils.isBlank(rmi.getQualifier().get(KfsKimAttributes.ORGANIZATION_CODE)))) {
                if (StringUtils.equals(translatedQualification.get(KimConstants.AttributeConstants.NAMESPACE_CODE), rmi.getQualifier().get(KimConstants.AttributeConstants.NAMESPACE_CODE))) {
                    namespaceMatch = rmi;
                }
                else if (StringUtils.equals(KFSConstants.ParameterNamespaces.KFS, rmi.getQualifier().get(KimConstants.AttributeConstants.NAMESPACE_CODE))) {
                    nonNamespaceMatch = rmi;
                }
            }
        }
        List<RoleMembership> matchingMemberships = new ArrayList<RoleMembership>(1);
        if (nonNamespaceMatch != null || namespaceMatch != null) {
            matchingMemberships.add((namespaceMatch != null) ? namespaceMatch : nonNamespaceMatch);
        }
        return matchingMemberships;
    }
    
    /**
     * note: for validating KFS-SYS User membership
     *   - if chart or org are specified, chart, org, and namespace are all required
     *   - none are required if not
     *   - can only have one assignment to the role for a given namespace - including no namespace 
     *  
     * @see org.kuali.rice.kim.service.support.impl.KimTypeInfoServiceBase#validateAttributes(org.kuali.rice.kim.bo.types.dto.AttributeSet)
     */
    @Override
    public Map<String,String> validateAttributes(String kimTypeId, Map<String,String> attributes) {
        Map<String,String> errorMap = super.validateAttributes(kimTypeId, attributes);
        String chartCode = attributes.get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE);
        String organizationCode = attributes.get(KfsKimAttributes.ORGANIZATION_CODE);
        String namespaceCode = attributes.get(KimConstants.AttributeConstants.NAMESPACE_CODE);
        if(StringUtils.isEmpty(chartCode) && StringUtils.isEmpty(organizationCode)){
            //remove chartofAccountCode, organizationCode and namespaceCode errors
            //Object results = GlobalVariables.getMessageMap().getErrorMessagesForProperty(attributeName);
            //RiceKeyConstants.ERROR_REQUIRED
            errorMap.remove(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE);
            errorMap.remove(KfsKimAttributes.ORGANIZATION_CODE);
            if(StringUtils.isEmpty(namespaceCode))
                errorMap.remove(KimConstants.AttributeConstants.NAMESPACE_CODE);
        } //- if chart or org are specified, chart, org, and namespace are all required 
          //- none are required if not 
        else if (StringUtils.isNotEmpty(chartCode) || StringUtils.isNotEmpty(organizationCode)){
            if(StringUtils.isEmpty(chartCode))
                errorMap.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, KFSKeyConstants.ERROR_CHART_OR_ORG_NOTEMPTY_ALL_REQUIRED);
            if(StringUtils.isEmpty(organizationCode))
                errorMap.put(KfsKimAttributes.ORGANIZATION_CODE, KFSKeyConstants.ERROR_CHART_OR_ORG_NOTEMPTY_ALL_REQUIRED);
            if(StringUtils.isEmpty(namespaceCode))
                errorMap.put(KimConstants.AttributeConstants.NAMESPACE_CODE, KFSKeyConstants.ERROR_CHART_OR_ORG_NOTEMPTY_ALL_REQUIRED);
        }

        return errorMap;
    }

    @Override
    public boolean validateUniqueAttributes(String kimTypeId, Map<String,String> newAttributes, Map<String,String> oldAttributes){

        if(areAllAttributeValuesEmpty(newAttributes)){
            return false;
        } else
            return super.validateUniqueAttributes(kimTypeId, newAttributes, oldAttributes);
    }

    protected boolean areAllAttributeValuesEmpty( Map<String,String> attributes){
        boolean areAllAttributesEmpty = true;
        if(attributes!=null)
            for(String attributeNameKey: attributes.keySet()){
                if(StringUtils.isNotEmpty(attributes.get(attributeNameKey))){
                    areAllAttributesEmpty = false;
                    break;
                }
            }
        return areAllAttributesEmpty;
    }

    @Override
    public List<String> getUniqueAttributes(String kimTypeId){
        List<String> uniqueAttributes = new ArrayList<String>();
        //uniqueAttributes.add(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE);
        //uniqueAttributes.add(KfsKimAttributes.ORGANIZATION_CODE);
        uniqueAttributes.add(KimConstants.AttributeConstants.NAMESPACE_CODE);
        return uniqueAttributes;
    }

    /**
     * @see org.kuali.rice.kim.service.support.impl.KimTypeInfoServiceBase#getAttributeDefinitions(java.lang.String)
     */
    @Override
    public AttributeDefinitionMap getAttributeDefinitions(String kimTypeId) {
        AttributeDefinitionMap map = super.getAttributeDefinitions(kimTypeId);
        for (AttributeDefinition definition : map.values()) {
            if (KimConstants.AttributeConstants.NAMESPACE_CODE.equals(definition.getName()) || KfsKimAttributes.CHART_OF_ACCOUNTS_CODE.equals(definition.getName())
                    || KfsKimAttributes.ORGANIZATION_CODE.equals(definition.getName())) {
                definition.setRequired(Boolean.FALSE);
            }
        }
        return map;
    }
    
    
}
