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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.core.api.uif.RemotableAttributeField;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.type.KimAttributeField;
import org.kuali.rice.kim.util.KimCommonUtils;

public class OrganizationHierarchyReviewRoleTypeServiceImpl extends OrganizationHierarchyAwareRoleTypeServiceBase {

    private DocumentTypeService documentTypeService;

    /**
     * Attributes: Chart Code (required) Organization Code Document Type Name Requirement - Traverse the org hierarchy but not the
     * document type hierarchy
     * 
     * @see org.kuali.kfs.coa.identity.OrganizationOptionalHierarchyRoleTypeServiceImpl#performMatch(org.kuali.rice.kim.bo.types.dto.AttributeSet,
     *      org.kuali.rice.kim.bo.types.dto.AttributeSet)
     */
    @Override
    protected boolean performMatch(Map<String,String> qualification, Map<String,String> roleQualifier) {
        if (isParentOrg(qualification.get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE), qualification.get(KfsKimAttributes.ORGANIZATION_CODE), roleQualifier.get(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE), roleQualifier.get(KfsKimAttributes.ORGANIZATION_CODE), true)) {
            Set<String> potentialParentDocumentTypeNames = new HashSet<String>(1);
            if (roleQualifier.containsKey(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME)) {
                potentialParentDocumentTypeNames.add(roleQualifier.get(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME));
            }
            return potentialParentDocumentTypeNames.isEmpty() 
                    || StringUtils.equalsIgnoreCase( qualification.get(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME), roleQualifier.get(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME)) 
                    || (KimCommonUtils.getClosestParentDocumentTypeName(getDocumentTypeService().getDocumentTypeByName(qualification.get(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME)), potentialParentDocumentTypeNames) != null);
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
     * @see org.kuali.rice.kns.kim.type.DataDictionaryTypeServiceBase#getAttributeDefinitions(java.lang.String)
     */
    @Override
    public List<KimAttributeField> getAttributeDefinitions(String kimTypeId) {
        List<KimAttributeField> attributeDefinitionList = new ArrayList<KimAttributeField>();
        for (KimAttributeField definition : super.getAttributeDefinitions(kimTypeId)) {
            RemotableAttributeField attribute = definition.getAttributeField();
            
            //if field is organization code
            if (KfsKimAttributes.ORGANIZATION_CODE.equals(attribute.getName())) {
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
