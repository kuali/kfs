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
package edu.arizona.kfs.coa.identity;

import java.util.HashSet;
import java.util.Set;

import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.kew.doctype.service.DocumentTypeService;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kim.bo.types.dto.AttributeDefinitionMap;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.support.impl.KimRoleTypeServiceBase;
import org.kuali.rice.kim.util.KimCommonUtils;
import org.kuali.rice.kns.datadictionary.AttributeDefinition;

import edu.arizona.kfs.sys.identity.ArizonaKfsKimAttributes;

public class ObjectSubTypeCodeRoleTypeServiceImpl extends KimRoleTypeServiceBase {
    DocumentTypeService documentTypeService;
    
    @Override
    protected boolean performMatch(AttributeSet qualification, AttributeSet roleQualifier) {    
        if (KimCommonUtils.storedValueNotSpecifiedOrInputValueMatches(roleQualifier, qualification, ArizonaKfsKimAttributes.OBJECT_SUB_TYPE_CODE)) {
            Set<String> potentialParentDocumentTypeNames = new HashSet<String>(1);
            if (roleQualifier.containsKey(org.kuali.kfs.sys.identity.KfsKimAttributes.DOCUMENT_TYPE_NAME)) {
                potentialParentDocumentTypeNames.add(roleQualifier.get(org.kuali.kfs.sys.identity.KfsKimAttributes.DOCUMENT_TYPE_NAME));
            }
            return potentialParentDocumentTypeNames.isEmpty() 
                || qualification.get(org.kuali.kfs.sys.identity.KfsKimAttributes.DOCUMENT_TYPE_NAME).equalsIgnoreCase(roleQualifier.get(org.kuali.kfs.sys.identity.KfsKimAttributes.DOCUMENT_TYPE_NAME)) 
                || (KimCommonUtils.getClosestParentDocumentTypeName(getDocumentTypeService().findByName(qualification.get(org.kuali.kfs.sys.identity.KfsKimAttributes.DOCUMENT_TYPE_NAME)), potentialParentDocumentTypeNames) != null);
        }
        return false;
    }
    
    public DocumentTypeService getDocumentTypeService() {
        if (documentTypeService == null) {
            documentTypeService = KEWServiceLocator.getDocumentTypeService();
        }
        return this.documentTypeService;
    }

    /**
     * @see org.kuali.rice.kim.service.support.impl.KimTypeServiceBase#getAttributeDefinitions(java.lang.String)
     */
    @Override
    public AttributeDefinitionMap getAttributeDefinitions(String kimTypId) {
        AttributeDefinitionMap map = super.getAttributeDefinitions(kimTypId);
        
        for (AttributeDefinition definition : map.values()) {
            if (ArizonaKfsKimAttributes.OBJECT_SUB_TYPE_CODE.equals(definition.getName())) {
                definition.setRequired(Boolean.FALSE);
            }
        }
        
        return map;
    }
}

