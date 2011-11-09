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

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.service.OrganizationService;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.bo.types.dto.AttributeDefinitionMap;
import org.kuali.rice.kim.util.KimCommonUtils;
import org.kuali.rice.kns.kim.role.RoleTypeServiceBase;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;

public class SubAccountReviewRoleTypeServiceImpl extends RoleTypeServiceBase {
    protected DocumentTypeService documentTypeService;
    protected OrganizationService organizationService;

    @Override
    protected boolean performMatch(Map<String,String> qualification, Map<String,String> roleQualifier) {
        if (StringUtils.isBlank(roleQualifier.get(KfsKimAttributes.ACCOUNT_NUMBER))) {
            if (KimCommonUtils.storedValueNotSpecifiedOrInputValueMatches(roleQualifier, qualification, KfsKimAttributes.CHART_OF_ACCOUNTS_CODE) 
                    && KimCommonUtils.storedValueNotSpecifiedOrInputValueMatches(roleQualifier, qualification, KfsKimAttributes.ORGANIZATION_CODE) 
                    && KimCommonUtils.storedValueNotSpecifiedOrInputValueMatches(roleQualifier, qualification, KfsKimAttributes.SUB_ACCOUNT_NUMBER)) {
                Set<String> potentialParentDocumentTypeNames = new HashSet<String>(1);
                if (roleQualifier.containsKey(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME)) {
                    potentialParentDocumentTypeNames.add(roleQualifier.get(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME));
                }
                return potentialParentDocumentTypeNames.isEmpty() 
                        || qualification.get(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME).equalsIgnoreCase(roleQualifier.get(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME)) 
                        || (KimCommonUtils.getClosestParentDocumentTypeName(getDocumentTypeService().getDocumentTypeByName(qualification.get(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME)), potentialParentDocumentTypeNames) != null);
            }
        }
        else {
            if (KimCommonUtils.storedValueNotSpecifiedOrInputValueMatches(roleQualifier, qualification, KfsKimAttributes.CHART_OF_ACCOUNTS_CODE) 
                    && KimCommonUtils.storedValueNotSpecifiedOrInputValueMatches(roleQualifier, qualification, KfsKimAttributes.ACCOUNT_NUMBER) 
                    && KimCommonUtils.storedValueNotSpecifiedOrInputValueMatches(roleQualifier, qualification, KfsKimAttributes.SUB_ACCOUNT_NUMBER)) {
                Set<String> potentialParentDocumentTypeNames = new HashSet<String>(1);
                if (roleQualifier.containsKey(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME)) {
                    potentialParentDocumentTypeNames.add(roleQualifier.get(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME));
                }
                return potentialParentDocumentTypeNames.isEmpty() 
                        || qualification.get(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME).equalsIgnoreCase(roleQualifier.get(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME)) 
                        || (KimCommonUtils.getClosestParentDocumentTypeName(getDocumentTypeService().getDocumentTypeByName(qualification.get(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME)), potentialParentDocumentTypeNames) != null);
            }
        }
        return false;
    }

    protected DocumentTypeService getDocumentTypeService() {
        if (documentTypeService == null) {
            documentTypeService = SpringContext.getBean(DocumentTypeService.class);
        }
        return this.documentTypeService;
    }

    /**
     * note: for validating Sub-account review role - if acct or org are specified, sub-account and chart are all required
     * 
     * @see org.kuali.rice.kim.service.support.impl.KimTypeInfoServiceBase#validateAttributes(org.kuali.rice.kim.bo.types.dto.AttributeSet)
     */
    @Override
    public Map<String,String> validateAttributes(String kimTypeId, Map<String,String> attributes) {
        Map<String,String> errorMap = super.validateAttributes(kimTypeId, attributes);
        String chartCode = attributes.get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE);
        String organizationCode = attributes.get(KfsKimAttributes.ORGANIZATION_CODE);
        String accountNumber = attributes.get(KfsKimAttributes.ACCOUNT_NUMBER);
        String subAccountNumber = attributes.get(KfsKimAttributes.SUB_ACCOUNT_NUMBER);
        if (StringUtils.isEmpty(accountNumber) && StringUtils.isEmpty(organizationCode)) {
            // remove chartofAccountCode, organizationCode and account number and sub-account number errors
            errorMap.remove(KfsKimAttributes.ACCOUNT_NUMBER);
            errorMap.remove(KfsKimAttributes.ORGANIZATION_CODE);
            if (StringUtils.isEmpty(chartCode))
                errorMap.remove(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE);
            if (StringUtils.isEmpty(subAccountNumber))
                errorMap.remove(KfsKimAttributes.SUB_ACCOUNT_NUMBER);
        }
        else if (StringUtils.isNotEmpty(accountNumber) || StringUtils.isNotEmpty(organizationCode)) {
            if (StringUtils.isEmpty(chartCode))
                errorMap.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, KFSKeyConstants.ERROR_CHART_OR_ORG_NOTEMPTY_ALL_REQUIRED);
            if (StringUtils.isEmpty(subAccountNumber))
                errorMap.put(KfsKimAttributes.SUB_ACCOUNT_NUMBER, KFSKeyConstants.ERROR_CHART_OR_ORG_NOTEMPTY_ALL_REQUIRED);
        }
        return errorMap;
    }

    @Override
    public AttributeDefinitionMap getAttributeDefinitions(String kimTypeId) {
        AttributeDefinitionMap map = super.getAttributeDefinitions(kimTypeId);
        for (AttributeDefinition definition : map.values()) {
            if (KfsKimAttributes.SUB_ACCOUNT_NUMBER.equals(definition.getName()) || KfsKimAttributes.CHART_OF_ACCOUNTS_CODE.equals(definition.getName()) || KfsKimAttributes.ACCOUNT_NUMBER.equals(definition.getName()) || KfsKimAttributes.ORGANIZATION_CODE.equals(definition.getName())) {
                definition.setRequired(Boolean.FALSE);
            }
        }
        return map;
    }
}
