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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.service.OrganizationService;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.core.api.uif.RemotableAttributeError;
import org.kuali.rice.core.api.uif.RemotableAttributeError.Builder;
import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.type.KimAttributeField;
import org.kuali.rice.kim.util.KimCommonUtils;
import org.kuali.rice.kns.kim.role.RoleTypeServiceBase;

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
     * @see org.kuali.rice.kns.kim.type.DataDictionaryTypeServiceBase#validateAttributes(java.lang.String, java.util.Map)
     */
    public List<RemotableAttributeError> validateAttributes(String kimTypeId, Map<String,String> attributes) {
        List<RemotableAttributeError> originalErrorMap = super.validateAttributes(kimTypeId, attributes);
        List<RemotableAttributeError> errorMap = new ArrayList<RemotableAttributeError>( originalErrorMap.size() );
        String chartCode = attributes.get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE);
        String organizationCode = attributes.get(KfsKimAttributes.ORGANIZATION_CODE);
        String accountNumber = attributes.get(KfsKimAttributes.ACCOUNT_NUMBER);
        String subAccountNumber = attributes.get(KfsKimAttributes.SUB_ACCOUNT_NUMBER);
        
        if (StringUtils.isEmpty(accountNumber) && StringUtils.isEmpty(organizationCode)) {
            // remove chartofAccountCode, organizationCode and account number and sub-account number errors
            for ( RemotableAttributeError err : originalErrorMap ) {
                if ( err.getAttributeName().equals( KfsKimAttributes.ACCOUNT_NUMBER ) ) {
                    // remove (by not adding) to the new error map
                } else if ( err.getAttributeName().equals( KfsKimAttributes.ORGANIZATION_CODE ) ) {
                    // remove (by not adding) to the new error map                    
                } else if ( err.getAttributeName().equals(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE) && StringUtils.isEmpty(chartCode) ) {
                    // remove (by not adding) to the new error map                                        
                } else if ( err.getAttributeName().equals(KfsKimAttributes.SUB_ACCOUNT_NUMBER) && StringUtils.isEmpty(subAccountNumber) ) {
                    // remove (by not adding) to the new error map                    
                } else {
                    // let it stay in the resulting map
                    errorMap.add( err );
                }
            }
        } else if (StringUtils.isNotEmpty(accountNumber) || StringUtils.isNotEmpty(organizationCode)) {
            errorMap.addAll(originalErrorMap);
            if (StringUtils.isEmpty(chartCode)) {
                Builder caBuilder = RemotableAttributeError.Builder.create(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, KFSKeyConstants.ERROR_CHART_OR_ORG_NOTEMPTY_ALL_REQUIRED);
                errorMap.add(caBuilder.build());
            }
                
            if (StringUtils.isEmpty(subAccountNumber)) {
                Builder caBuilder = RemotableAttributeError.Builder.create(KfsKimAttributes.SUB_ACCOUNT_NUMBER, KFSKeyConstants.ERROR_CHART_OR_ORG_NOTEMPTY_ALL_REQUIRED);
                errorMap.add(caBuilder.build());                
            }
        }
        return errorMap;
    }

    /**
     * @see org.kuali.rice.kns.kim.type.DataDictionaryTypeServiceBase#getAttributeDefinitions(java.lang.String)
     */
    @Override
    public List<KimAttributeField> getAttributeDefinitions(String kimTypeId) {
        List<KimAttributeField> attributeDefinitionList = new ArrayList<KimAttributeField>();
        for (KimAttributeField definition : super.getAttributeDefinitions(kimTypeId)) {
            RemotableAttributeField attribute = definition.getAttributeField();
            
            List<String> attributeList = new ArrayList<String>(){{
                add(KfsKimAttributes.SUB_ACCOUNT_NUMBER);
                add(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE);
                add(KfsKimAttributes.ACCOUNT_NUMBER);
                add(KfsKimAttributes.ORGANIZATION_CODE);
            }};
            
            //for any of the checked attribute fields
            if (attributeList.contains(attribute.getName())) {
                //create a new AttributeField with the existing attribute require set to false
                RemotableAttributeField.Builder nonRequiredAttributeBuilder = RemotableAttributeField.Builder.create(attribute);
                nonRequiredAttributeBuilder.setRequired(Boolean.FALSE);
                
                //setUnique should have been part of the creat() 
                KimAttributeField.Builder nonRequiredAttribute = KimAttributeField.Builder.create(nonRequiredAttributeBuilder, definition.getId());
                nonRequiredAttribute.setUnique(definition.isUnique());
                attributeDefinitionList.add(nonRequiredAttribute.build());
            }else{
                attributeDefinitionList.add(definition);
            }
        }
        return attributeDefinitionList;
    }
}
