/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
