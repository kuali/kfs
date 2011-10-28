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
package org.kuali.kfs.sec.identity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sec.SecConstants;
import org.kuali.kfs.sec.businessobject.options.SecurityDefinitionDocumentTypeFinder;
import org.kuali.rice.kew.dto.DocumentTypeDTO;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kew.service.WorkflowInfo;
import org.kuali.rice.kim.bo.role.dto.KimPermissionInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.util.KimCommonUtils;


/**
 * Type service for Access Security Permissions that restrict based on property name and document type
 */
public class SecurityAttributeDocTypePermissionTypeServiceImpl extends SecurityAttributePermissionTypeServiceImpl {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SecurityAttributeDocTypePermissionTypeServiceImpl.class);

/* RICE_20_DELETE */    {
/* RICE_20_DELETE */        requiredAttributes.add(SecKimAttributes.DOCUMENT_TYPE_NAME);
/* RICE_20_DELETE */        checkRequiredAttributes = false;
/* RICE_20_DELETE */    }

    /**
     * @see org.kuali.rice.kim.service.support.impl.KimPermissionTypeServiceBase#performPermissionMatches(org.kuali.rice.kim.bo.types.dto.AttributeSet, java.util.List)
     */
    @Override
    protected List<KimPermissionInfo> performPermissionMatches(AttributeSet requestedDetails, List<KimPermissionInfo> permissionsList) {
        List<KimPermissionInfo> matchingPermissions = new ArrayList<KimPermissionInfo>();

        for (KimPermissionInfo kpi : permissionsList) {
            String documentTypeNameMatch = requestedDetails.get(SecKimAttributes.DOCUMENT_TYPE_NAME);
            String documentTypeName = kpi.getDetails().get(SecKimAttributes.DOCUMENT_TYPE_NAME);

            boolean docTypeMatch = false;
            if (SecConstants.ALL_DOCUMENT_TYPE_NAME.equals(documentTypeName)) {
                docTypeMatch = true;
            }
            else if (StringUtils.equals(documentTypeNameMatch, documentTypeName)) {
                docTypeMatch = true;
            }
            else {
                docTypeMatch = isParentDocType(documentTypeNameMatch, documentTypeName);
            }

            if (docTypeMatch && isDetailMatch(requestedDetails, kpi.getDetails())) {
                matchingPermissions.add(kpi);
            }
        }

        return matchingPermissions;
    }

    /**
     * Determines if a document type is a parent of another
     * 
     * @param docTypeName potential child doc type name
     * @param potientialParentDocTypeName potential parent doc type name
     * @return boolean true if the first document type is a child of the second
     */
    protected boolean isParentDocType(String docTypeName, String potentialParentDocTypeName) {
        WorkflowInfo workflowInfo = new WorkflowInfo();

        DocumentTypeDTO documentType = null;
        try {
            documentType = workflowInfo.getDocType(docTypeName);
            String parentDocTypeName = documentType.getDocTypeParentName();

            if (StringUtils.equals(docTypeName, parentDocTypeName)) {
                return true;
            }
            else if ((documentType.getDocTypeParentId() == null) || documentType.getDocTypeParentId().equals(documentType.getDocTypeId())) {
                return false;
            }
            else {
                return isParentDocType(parentDocTypeName, potentialParentDocTypeName);
            }
        }
        catch (WorkflowException e) {
            LOG.error("Invalid document type found " + docTypeName);
            throw new RuntimeException("Invalid document type found: " + docTypeName);
        }
    }

}
