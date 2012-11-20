/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.identity;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.bo.Role;
import org.kuali.rice.kim.bo.role.dto.RoleMembershipInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.support.impl.KimDerivedRoleTypeServiceBase;

public class TemProfileDerivedRoleTypeServiceImpl extends KimDerivedRoleTypeServiceBase {
    
    {
        requiredAttributes.add(TemKimAttributes.PROFILE_PRINCIPAL_ID);
    }
    
    /**
     * 
     * @see org.kuali.rice.kim.service.support.impl.KimRoleTypeServiceBase#getPrincipalIdsFromApplicationRole(java.lang.String, java.lang.String, org.kuali.rice.kim.bo.types.dto.AttributeSet)
     */
    @Override
    public List<RoleMembershipInfo> getRoleMembersFromApplicationRole(String namespaceCode, String roleName, AttributeSet qualification) {
        validateRequiredAttributesAgainstReceived(qualification);
        final List<RoleMembershipInfo> members = new ArrayList<RoleMembershipInfo>(1);
        if (qualification!=null && !qualification.isEmpty()) {
            
            // This is to allow users to create/edit their own profile. If the principalId from the profile 
            // matches, the profile fields are unmasked. Otherwise the other roles will handle the appropriate 
            // masking/unmasking of fields (see TemProfileOrganizationHierarchyRoleTypeServiceImpl).
            String principalId = qualification.get(TemKimAttributes.PROFILE_PRINCIPAL_ID);
            if (StringUtils.isNotBlank(principalId)) {
                members.add( new RoleMembershipInfo(null,null,principalId,Role.PRINCIPAL_MEMBER_TYPE,null) );
            }
        }
        return members;
    }

}
