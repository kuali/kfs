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
package org.kuali.kfs.vnd.identity;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.cg.identity.CgKimAttributes;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kew.doctype.service.DocumentTypeService;
import org.kuali.rice.kew.service.KEWServiceLocator;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.support.impl.KimRoleTypeServiceBase;
import org.kuali.rice.kim.util.KimCommonUtils;

public class CommodityReviewRoleTypeServiceImpl extends KimRoleTypeServiceBase {
    DocumentTypeService documentTypeService;
    
    @Override
    protected boolean performMatch(AttributeSet qualification, AttributeSet roleQualifier) {
        if (qualification.containsKey(KfsKimAttributes.PURCHASING_COMMODITY_CODE) && qualification.get(KfsKimAttributes.PURCHASING_COMMODITY_CODE).matches(roleQualifier.get(KfsKimAttributes.PURCHASING_COMMODITY_CODE).replaceAll("\\*", ".*")) && KimCommonUtils.storedValueNotSpecifiedOrInputValueMatches(roleQualifier, qualification, KfsKimAttributes.CAMPUS_CODE)) {
            Set<String> potentialParentDocumentTypeNames = new HashSet<String>(1);
            if (roleQualifier.containsKey(KfsKimAttributes.DOCUMENT_TYPE_NAME)) {
                potentialParentDocumentTypeNames.add(roleQualifier.get(KfsKimAttributes.DOCUMENT_TYPE_NAME));
            }
            return potentialParentDocumentTypeNames.isEmpty() || qualification.get(KfsKimAttributes.DOCUMENT_TYPE_NAME).equalsIgnoreCase(roleQualifier.get(KfsKimAttributes.DOCUMENT_TYPE_NAME)) || (KimCommonUtils.getClosestParentDocumentTypeName(getDocumentTypeService().findByName(qualification.get(KfsKimAttributes.DOCUMENT_TYPE_NAME)), potentialParentDocumentTypeNames) != null);
        }
        return false;
    }
    
    public DocumentTypeService getDocumentTypeService() {
        if (documentTypeService == null) {
            documentTypeService = KEWServiceLocator.getDocumentTypeService();
        }
        return this.documentTypeService;
    }
}
