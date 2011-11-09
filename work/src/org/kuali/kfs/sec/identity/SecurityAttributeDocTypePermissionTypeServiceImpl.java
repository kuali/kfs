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
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sec.SecConstants;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.permission.Permission;


/**
 * Type service for Access Security Permissions that restrict based on property name and document type
 */
public class SecurityAttributeDocTypePermissionTypeServiceImpl extends SecurityAttributePermissionTypeServiceImpl {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SecurityAttributeDocTypePermissionTypeServiceImpl.class);






    /**
     * @see org.kuali.rice.kns.kim.permission.PermissionTypeServiceBase#performPermissionMatches(org.kuali.rice.kim.bo.types.dto.AttributeSet, java.util.List)
     */
    @Override
    protected List<Permission> performPermissionMatches(Map<String,String> requestedDetails, List<Permission> permissionsList) {
        List<Permission> matchingPermissions = new ArrayList<Permission>();

        for (Permission kpi : permissionsList) {
            String documentTypeNameMatch = requestedDetails.get(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME);
            String documentTypeName = kpi.getAttributes().get(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME);

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

            if (docTypeMatch && isDetailMatch(requestedDetails, kpi.getAttributes())) {
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
//        WorkflowInfo workflowInfo = new WorkflowInfo();

        DocumentType documentType = null;
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
