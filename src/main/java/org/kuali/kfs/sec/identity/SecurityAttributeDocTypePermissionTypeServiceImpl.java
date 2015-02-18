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
package org.kuali.kfs.sec.identity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sec.SecConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.doctype.DocumentType;
import org.kuali.rice.kew.api.doctype.DocumentTypeService;
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
        DocumentType documentType = null;
        documentType = SpringContext.getBean(DocumentTypeService.class).getDocumentTypeByName(docTypeName);
        
        if ( documentType == null || StringUtils.isBlank( documentType.getParentId() ) ) {
            return false;
        }
        
        DocumentType parentDocType = SpringContext.getBean(DocumentTypeService.class).getDocumentTypeById(documentType.getParentId());
        if ( parentDocType == null ) {
            return false;
        }            
        return isParentDocType(parentDocType.getName(), potentialParentDocTypeName);
    }

}
