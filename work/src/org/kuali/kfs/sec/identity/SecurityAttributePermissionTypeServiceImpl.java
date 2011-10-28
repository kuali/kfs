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

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kim.bo.role.dto.KimPermissionInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.support.impl.KimPermissionTypeServiceBase;

/**
 * Type service for Access Security Permissions that restrict based on property name
 */
public class SecurityAttributePermissionTypeServiceImpl extends KimPermissionTypeServiceBase {

/* RICE_20_DELETE */    {
/* RICE_20_DELETE */        requiredAttributes.add(SecKimAttributes.PROPERTY_NAME);
/* RICE_20_DELETE */        checkRequiredAttributes = false;
/* RICE_20_DELETE */    }

    /**
     * @see org.kuali.rice.kim.service.support.impl.KimPermissionTypeServiceBase#performPermissionMatches(org.kuali.rice.kim.bo.types.dto.AttributeSet,
     *      java.util.List)
     */
    @Override
    protected List<KimPermissionInfo> performPermissionMatches(AttributeSet requestedDetails, List<KimPermissionInfo> permissionsList) {
        List<KimPermissionInfo> matchingPermissions = new ArrayList<KimPermissionInfo>();

        for (KimPermissionInfo kpi : permissionsList) {
            if (isDetailMatch(requestedDetails, kpi.getDetails())) {
                matchingPermissions.add(kpi);
            }
        }

        return matchingPermissions;
    }

    /**
     * Performs match on property name
     * 
     * @param requestedDetails AttributeSet containing details to match on
     * @param permissionDetails AttributeSet containing details associated with permission
     * @return boolean true if details match, false otherwise
     */
    protected boolean isDetailMatch(AttributeSet requestedDetails, AttributeSet permissionDetails) {
        String propertyNameMatch = requestedDetails.get(SecKimAttributes.PROPERTY_NAME);
        String propertyName = permissionDetails.get(SecKimAttributes.PROPERTY_NAME);

        if (StringUtils.equals(propertyNameMatch, propertyName)) {
            return true;
        }

        return false;
    }

}
