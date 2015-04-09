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
package org.kuali.kfs.module.tem.identity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.businessobject.TemProfile;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.core.api.membership.MemberType;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.role.RoleMembership;
import org.kuali.rice.kns.kim.role.DerivedRoleTypeServiceBase;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.KRADConstants;

@SuppressWarnings("deprecation")
public class TemProfileDerivedRoleTypeServiceImpl extends DerivedRoleTypeServiceBase {
    protected BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.rice.kns.kim.type.DataDictionaryTypeServiceBase#getRequiredAttributes()
     */
    @Override
    protected List<String> getRequiredAttributes() {
        final List<String> attrs = new ArrayList<String>(super.getRequiredAttributes());
        attrs.add(KfsKimAttributes.PROFILE_PRINCIPAL_ID);
        return Collections.unmodifiableList(attrs);
    }

    /**
     * @see org.kuali.rice.kns.kim.role.DerivedRoleTypeServiceBase#getRoleMembersFromDerivedRole(java.lang.String, java.lang.String, java.util.Map)
     */
    @Override
    public List<RoleMembership> getRoleMembersFromDerivedRole(String namespaceCode, String roleName, Map<String, String> qualification) {
        validateRequiredAttributesAgainstReceived(qualification);
        final List<RoleMembership> members = new ArrayList<RoleMembership>(1);
        if (qualification!=null && !qualification.isEmpty()) {
            // This is to allow users to create/edit their own profile. If the principalId from the profile
            // matches, the profile fields are unmasked. Otherwise the other roles will handle the appropriate
            // masking/unmasking of fields (see TemProfileOrganizationHierarchyRoleTypeServiceImpl).
            final String principalId = qualification.get(KfsKimAttributes.PROFILE_PRINCIPAL_ID);
            if (StringUtils.isNotBlank(principalId)) {
                // does this profile principal id have an actual profile?
                if (hasProfile(principalId) || isCreatingProfile(qualification)) {
                    members.add(RoleMembership.Builder.create("", "", principalId, MemberType.PRINCIPAL, null).build());
                }
            }
        }
        return members;
    }

    /**
     * Determines if the user with the given principal id has a profile record
     * @param profilePrincipalId the principal id to check for a profile for
     * @return true if a profile was found, false otherwise
     */
    protected boolean hasProfile(String profilePrincipalId) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(KFSPropertyConstants.PRINCIPAL_ID, profilePrincipalId);
        fieldValues.put(KFSPropertyConstants.ACTIVE, Boolean.TRUE);
        final int profileCount = getBusinessObjectService().countMatching(TemProfile.class, fieldValues);
        return profileCount > 0;
    }

    /**
     * Determines if the document requesting routing is currently creating a profile - ie, it's a TTP doc with a new maintenance action
     * @param qualification the qualification to find document type and maintenance action in
     * @return true if the qualifiers suggest a profile is being created, false otherwise
     */
    protected boolean isCreatingProfile(Map<String, String> qualification) {
        if (qualification.containsKey(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME) && qualification.containsKey(KRADConstants.MAINTENANCE_ACTN)) {
            final String documentTypeName = qualification.get(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME);
            final String maintenanceAction = qualification.get(KRADConstants.MAINTENANCE_ACTN);
            return StringUtils.equals(documentTypeName, TemConstants.TravelDocTypes.TRAVEL_PROFILE_DOCUMENT) && StringUtils.equals(maintenanceAction, KRADConstants.MAINTENANCE_NEW_ACTION);
        }
        return false;
    }

    @Override
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
