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
