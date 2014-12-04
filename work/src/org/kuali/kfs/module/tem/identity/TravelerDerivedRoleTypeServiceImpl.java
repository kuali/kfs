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
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.core.api.membership.MemberType;
import org.kuali.rice.kim.api.role.RoleMembership;
import org.kuali.rice.kns.kim.role.DerivedRoleTypeServiceBase;

/**
 * Check for Traveler Derived Role base on document traveler (for Travel Document) or proflie (Travel Arranger Document)
 */
@SuppressWarnings("deprecation")
public class TravelerDerivedRoleTypeServiceImpl extends DerivedRoleTypeServiceBase {


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

            final String principalId = qualification.get(KfsKimAttributes.PROFILE_PRINCIPAL_ID);
            if ( StringUtils.isNotBlank( principalId ) ) {
                members.add(RoleMembership.Builder.create("", "", principalId, MemberType.PRINCIPAL, null).build());
            }
        }
        return members;
    }

}
