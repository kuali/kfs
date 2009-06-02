/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.sys.identity;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kim.bo.role.KimPermission;
import org.kuali.rice.kim.bo.role.dto.KimPermissionInfo;
import org.kuali.rice.kim.bo.role.impl.KimPermissionImpl;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.KIMServiceLocator;
import org.kuali.rice.kim.service.RoleService;
import org.kuali.rice.kns.service.impl.DocumentTypePermissionTypeServiceImpl;

/**
 * 
 * @author Kuali Rice Team (kuali-rice@googlegroups.com)
 * 
 */
public class FinancialSystemDocumentTypePermissionTypeServiceImpl extends DocumentTypePermissionTypeServiceImpl {

    /**
     * @see org.kuali.rice.kns.service.impl.DocumentTypePermissionTypeServiceImpl#performPermissionMatches(org.kuali.rice.kim.bo.types.dto.AttributeSet, java.util.List)
     */
    @Override
    protected List<KimPermissionInfo> performPermissionMatches(AttributeSet requestedDetails,
            List<KimPermissionInfo> permissionsList) {
        String documentTypeName = requestedDetails.get(KfsKimAttributes.DOCUMENT_TYPE_NAME);
        // loop over the permissions, checking the non-document-related ones
        for ( KimPermissionInfo kpi : permissionsList ) {
            // special handling when the permission is "Claim Electronic Payment"
            // if it is present and matches, then it takes priority
            if(KFSConstants.PermissionTemplate.CLAIM_ELECTRONIC_PAYMENT.name.equals(kpi.getTemplate().getName())){
                String qualifierDocumentTypeName = kpi.getDetails().get(KfsKimAttributes.DOCUMENT_TYPE_NAME);
                if ( documentTypeName==null && qualifierDocumentTypeName==null || 
                        (StringUtils.isNotEmpty(documentTypeName) && StringUtils.isNotEmpty(qualifierDocumentTypeName) 
                                && documentTypeName.equals(qualifierDocumentTypeName))
    
                        ) {
                    List<KimPermissionInfo> matchingPermissions = new ArrayList<KimPermissionInfo>();
                    matchingPermissions.add( kpi );
                    return matchingPermissions;
                }           
            }
        }
        // now, filter the list to just those for the current document
        return super.performPermissionMatches( requestedDetails, permissionsList );
    }

}
