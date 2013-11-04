/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.sec.identity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.permission.Permission;
import org.kuali.rice.kns.kim.permission.PermissionTypeServiceBase;

/**
 * Type service for Access Security Permissions that restrict based on property name
 */
public class SecurityAttributePermissionTypeServiceImpl extends PermissionTypeServiceBase {






    /**
     * @see org.kuali.rice.kns.kim.permission.PermissionTypeServiceBase#performPermissionMatches(org.kuali.rice.kim.bo.types.dto.AttributeSet,
     *      java.util.List)
     */
    @Override
    protected List<Permission> performPermissionMatches(Map<String,String> requestedDetails, List<Permission> permissionsList) {
        List<Permission> matchingPermissions = new ArrayList<Permission>();

        for (Permission kpi : permissionsList) {
            if (isDetailMatch(requestedDetails, kpi.getAttributes())) {
                matchingPermissions.add(kpi);
            }
        }

        return matchingPermissions;
    }

    /**
     * Performs match on property name
     * 
     * @param requestedDetails Map<String,String> containing details to match on
     * @param permissionDetails Map<String,String> containing details associated with permission
     * @return boolean true if details match, false otherwise
     */
    protected boolean isDetailMatch(Map<String,String> requestedDetails, Map<String,String> permissionDetails) {
        String propertyNameMatch = requestedDetails.get(KimConstants.AttributeConstants.PROPERTY_NAME);
        String propertyName = permissionDetails.get(KimConstants.AttributeConstants.PROPERTY_NAME);

        if (StringUtils.equals(propertyNameMatch, propertyName)) {
            return true;
        }

        return false;
    }

}
