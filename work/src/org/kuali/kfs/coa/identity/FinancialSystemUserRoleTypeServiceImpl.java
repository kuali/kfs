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
package org.kuali.kfs.coa.identity;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kim.bo.impl.KimAttributes;
import org.kuali.rice.kim.bo.role.dto.RoleMembershipInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeDefinitionMap;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.bo.types.dto.KimTypeAttributeInfo;
import org.kuali.rice.kim.bo.types.dto.KimTypeInfo;
import org.kuali.rice.kim.bo.types.impl.KimAttributeImpl;
import org.kuali.rice.kim.service.support.impl.KimRoleTypeServiceBase;
import org.kuali.rice.kns.datadictionary.AttributeDefinition;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.RiceKeyConstants;



/**
 * This role type service was designed to be attached to the KFS-SYS User role
 */
public class FinancialSystemUserRoleTypeServiceImpl extends KimRoleTypeServiceBase {
    public static final String FINANCIAL_SYSTEM_USER_ROLE_NAME = "User";
    public static final String PERFORM_QUALIFIER_MATCH = "performQualifierMatch";
    
    {
        requiredAttributes.add(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE);
        requiredAttributes.add(KfsKimAttributes.ORGANIZATION_CODE);
        checkRequiredAttributes = false; // can't check - used in too many places where a chart/org is not present
    }

    @Override
    protected boolean performMatch(AttributeSet qualification, AttributeSet roleQualifier) {
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
        if (StringUtils.equals(roleQualifier.get(KfsKimAttributes.NAMESPACE_CODE), qualification.get(KfsKimAttributes.NAMESPACE_CODE)) || StringUtils.equals(KFSConstants.ParameterNamespaces.KFS, roleQualifier.get(KfsKimAttributes.NAMESPACE_CODE))) {
            return true;
        }
        return false;
    }

    @Override
    public List<RoleMembershipInfo> doRoleQualifiersMatchQualification(AttributeSet qualification, List<RoleMembershipInfo> roleMemberList) {
        AttributeSet translatedQualification = translateInputAttributeSet(qualification);
        validateRequiredAttributesAgainstReceived(translatedQualification);
        // if we can not find the qualifier which tells us to perform the match, just return all rows
        if (translatedQualification == null || !Boolean.parseBoolean(translatedQualification.get(PERFORM_QUALIFIER_MATCH))) {
            return roleMemberList;
        }
        // perform special matching to make a match that includes the namespace take priority
        RoleMembershipInfo namespaceMatch = null;
        RoleMembershipInfo nonNamespaceMatch = null;
        // look for match
        for (RoleMembershipInfo rmi : roleMemberList) {
            // if chart and org aren't on the assignment we don't care, since we are only matching to get the default chart/org for
            // a user in FinancialSystemUserServiceImpl
            if (!(StringUtils.isBlank(rmi.getQualifier().get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE)) || StringUtils.isBlank(rmi.getQualifier().get(KfsKimAttributes.ORGANIZATION_CODE)))) {
                if (StringUtils.equals(translatedQualification.get(KfsKimAttributes.NAMESPACE_CODE), rmi.getQualifier().get(KfsKimAttributes.NAMESPACE_CODE))) {
                    namespaceMatch = rmi;
                }
                else if (StringUtils.equals(KFSConstants.ParameterNamespaces.KFS, rmi.getQualifier().get(KfsKimAttributes.NAMESPACE_CODE))) {
                    nonNamespaceMatch = rmi;
                }
            }
        }
        List<RoleMembershipInfo> matchingMemberships = new ArrayList<RoleMembershipInfo>(1);
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
     * @see org.kuali.rice.kim.service.support.impl.KimTypeServiceBase#validateAttributes(org.kuali.rice.kim.bo.types.dto.AttributeSet)
     */
    @Override
    public AttributeSet validateAttributes(String kimTypeId, AttributeSet attributes) {
        AttributeSet errorMap = super.validateAttributes(kimTypeId, attributes);
        String chartCode = attributes.get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE);
        String organizationCode = attributes.get(KfsKimAttributes.ORGANIZATION_CODE);
        String namespaceCode = attributes.get(KfsKimAttributes.NAMESPACE_CODE);
        if(StringUtils.isEmpty(chartCode) && StringUtils.isEmpty(organizationCode)){
            //remove chartofAccountCode, organizationCode and namespaceCode errors
            //Object results = GlobalVariables.getMessageMap().getErrorMessagesForProperty(attributeName);
            //RiceKeyConstants.ERROR_REQUIRED
            errorMap.remove(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE);
            errorMap.remove(KfsKimAttributes.ORGANIZATION_CODE);
            errorMap.remove(KfsKimAttributes.NAMESPACE_CODE);
        } //- if chart or org are specified, chart, org, and namespace are all required 
          //- none are required if not 
        else if (StringUtils.isNotEmpty(chartCode) || StringUtils.isNotEmpty(organizationCode)){
            if(StringUtils.isEmpty(chartCode))
                errorMap.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, KFSKeyConstants.ERROR_CHART_OR_ORG_NOTEMPTY_ALL_REQUIRED);
            if(StringUtils.isEmpty(organizationCode))
                errorMap.put(KfsKimAttributes.ORGANIZATION_CODE, KFSKeyConstants.ERROR_CHART_OR_ORG_NOTEMPTY_ALL_REQUIRED);
            if(StringUtils.isEmpty(namespaceCode))
                errorMap.put(KfsKimAttributes.NAMESPACE_CODE, KFSKeyConstants.ERROR_CHART_OR_ORG_NOTEMPTY_ALL_REQUIRED);
        }

        return errorMap;
    }

    @Override
    public boolean validateUniqueAttributes(String kimTypeId, AttributeSet newAttributes, AttributeSet oldAttributes){
        if(areAllAttributeValuesEmpty(newAttributes)){
            return false;
        } else
            return super.validateUniqueAttributes(kimTypeId, newAttributes, oldAttributes);
    }

    @Override
    public List<String> getUniqueAttributes(String kimTypeId){
        List<String> uniqueAttributes = new ArrayList<String>();
        //uniqueAttributes.add(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE);
        //uniqueAttributes.add(KfsKimAttributes.ORGANIZATION_CODE);
        uniqueAttributes.add(KimAttributes.NAMESPACE_CODE);
        return uniqueAttributes;
    }

    @Override
    public AttributeSet validateAttributesAgainstExisting(String kimTypeId, AttributeSet newAttributes, AttributeSet oldAttributes){
        AttributeSet errorMap = new AttributeSet();
        if(!areAllAttributeValuesEmpty(newAttributes) && !validateUniqueAttributes(kimTypeId, newAttributes, oldAttributes)){
            KimTypeInfo kimType = getTypeInfoService().getKimType(kimTypeId);
            KimTypeAttributeInfo attributeInfo = kimType.getAttributeDefinitionByName(KfsKimAttributes.NAMESPACE_CODE);
            errorMap = getErrorAttributeSet(KfsKimAttributes.NAMESPACE_CODE, RiceKeyConstants.ERROR_DUPLICATE_ENTRY, 
                    new String[] {getDataDictionaryService().getAttributeLabel(attributeInfo.getComponentName(), KfsKimAttributes.NAMESPACE_CODE)});
        }
        return errorMap;
    }

    /**
     * @see org.kuali.rice.kim.service.support.impl.KimTypeServiceBase#getAttributeDefinitions(java.lang.String)
     */
    @Override
    public AttributeDefinitionMap getAttributeDefinitions(String kimTypeId) {
        AttributeDefinitionMap map = super.getAttributeDefinitions(kimTypeId);
        for (AttributeDefinition definition : map.values()) {
            if (KfsKimAttributes.NAMESPACE_CODE.equals(definition.getName()) || KfsKimAttributes.CHART_OF_ACCOUNTS_CODE.equals(definition.getName())
                    || KfsKimAttributes.ORGANIZATION_CODE.equals(definition.getName())) {
                definition.setRequired(Boolean.FALSE);
            }
        }
        return map;
    }
    
    
}
