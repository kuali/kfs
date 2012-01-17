/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.kfs.sys.identity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.permission.Permission;
import org.kuali.rice.krad.kim.DocumentTypePermissionTypeServiceImpl;

/**
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * 
 */
public class FinancialSystemDocumentTypePermissionTypeServiceImpl extends DocumentTypePermissionTypeServiceImpl {

    /**
     * @see org.kuali.rice.krad.service.impl.DocumentTypePermissionTypeServiceImpl#performPermissionMatches(org.kuali.rice.kim.bo.types.dto.AttributeSet, java.util.List)
     */
    @Override
    protected List<Permission> performPermissionMatches(Map<String,String> requestedDetails,
            List<Permission> permissionsList) {
        String documentTypeName = requestedDetails.get(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME);
        // loop over the permissions, checking the non-document-related ones
        for ( Permission kpi : permissionsList ) {
            // special handling when the permission is "Claim Electronic Payment"
            // if it is present and matches, then it takes priority
            if(KFSConstants.PermissionTemplate.CLAIM_ELECTRONIC_PAYMENT.name.equals(kpi.getTemplate().getName())){
                String qualifierDocumentTypeName = kpi.getAttributes().get(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME);
                if ( documentTypeName==null && qualifierDocumentTypeName==null || 
                        (StringUtils.isNotEmpty(documentTypeName) && StringUtils.isNotEmpty(qualifierDocumentTypeName) 
                                && documentTypeName.equals(qualifierDocumentTypeName))
    
                        ) {
                    List<Permission> matchingPermissions = new ArrayList<Permission>();
                    matchingPermissions.add( kpi );
                    return matchingPermissions;
                }           
            }
        }
        // now, filter the list to just those for the current document
        return super.performPermissionMatches( requestedDetails, permissionsList );
    }

}
