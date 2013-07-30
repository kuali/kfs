/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.tem.identity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.TEMProfile;
import org.kuali.kfs.module.tem.service.TemProfileService;
import org.kuali.rice.core.api.membership.MemberType;
import org.kuali.rice.kim.api.role.RoleMembership;
import org.kuali.rice.kns.kim.role.DerivedRoleTypeServiceBase;

/**
 * Check for Traveler Derived Role base on document traveler (for Travel Document) or proflie (Travel Arranger Document)
 */
@SuppressWarnings("deprecation")
public class TravelerDerivedRoleTypeServiceImpl extends DerivedRoleTypeServiceBase {

    private TemProfileService temProfileService;

    /**
     * @see org.kuali.rice.kns.kim.type.DataDictionaryTypeServiceBase#getRequiredAttributes()
     */
    @Override
    protected List<String> getRequiredAttributes() {
        final List<String> attrs = new ArrayList<String>(super.getRequiredAttributes());
        attrs.add("profileId");
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

            final String profileId = qualification.get(TemPropertyConstants.TEMProfileProperties.PROFILE_ID);
            if ( StringUtils.isNotBlank( profileId ) ) {

                TEMProfile profile = temProfileService.findTemProfileById(Integer.valueOf(profileId));
                if (profile != null) {
                    String memberId = "";
                    if (profile.getTravelerTypeCode().equals(TemConstants.EMP_TRAVELER_TYP_CD)) {
                        memberId = profile.getPrincipalId();
                    }

                    if (!StringUtils.isBlank(memberId)) {
                        members.add(RoleMembership.Builder.create("", "", memberId, MemberType.PRINCIPAL, null).build());
                    }

                }
            }
        }
        return members;
    }

    /**
     * Sets the profileService attribute.
     *
     * @param profileService The profileService to set.
     */
    public void setTemProfileService(TemProfileService temProfileService) {
        this.temProfileService = temProfileService;
    }



}
