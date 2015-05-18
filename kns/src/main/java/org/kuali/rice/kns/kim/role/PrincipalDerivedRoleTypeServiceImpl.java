/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kns.kim.role;


import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.core.api.membership.MemberType;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.IdentityService;
import org.kuali.rice.kim.api.identity.entity.EntityDefault;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.role.RoleMembership;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kns.kim.role.DerivedRoleTypeServiceBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * This is a description of what this class does - kellerj don't forget to fill this in. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 * @deprecated A krad integrated type service base class will be provided in the future.
 */
@Deprecated
public class PrincipalDerivedRoleTypeServiceImpl extends DerivedRoleTypeServiceBase {
	
	private IdentityService identityService;

    @Override
    protected List<String> getRequiredAttributes() {
        final List<String> attrs = new ArrayList<String>(super.getRequiredAttributes());
        attrs.add(KimConstants.AttributeConstants.PRINCIPAL_ID);
        return Collections.unmodifiableList(attrs);
    }

    @Override
    protected boolean isCheckRequiredAttributes() {
        return false;
    }

	@Override
	public boolean performMatch(Map<String, String> inputAttributes, Map<String, String> storedAttributes) {
		if (inputAttributes == null) {
            throw new RiceIllegalArgumentException("inputAttributes was null");
        }

		if (storedAttributes == null) {
            throw new RiceIllegalArgumentException("storedAttributes was null");
        }

        return true;
	}

	/**
	 * Since this is potentially the entire set of users, just check the qualification for the user we are interested in and return it.
	 */
	@Override
    public List<RoleMembership> getRoleMembersFromDerivedRole(String namespaceCode, String roleName, Map<String, String> qualification) {
		if (StringUtils.isBlank(namespaceCode)) {
            throw new RiceIllegalArgumentException("namespaceCode was null or blank");
        }

        if (roleName == null) {
            throw new RiceIllegalArgumentException("roleName was null");
        }

		if ( qualification == null || qualification.isEmpty() ) {
			return Collections.emptyList();
		}
        ArrayList<RoleMembership> tempIdList = new ArrayList<RoleMembership>();
		qualification = translateInputAttributes(qualification);
		// check that the principal ID is not null
		String principalId = qualification.get( KimConstants.AttributeConstants.PRINCIPAL_ID );
		if ( hasDerivedRole(principalId, null, namespaceCode, roleName, qualification)) {
	        tempIdList.add( RoleMembership.Builder.create(null/*roleId*/, null, principalId, MemberType.PRINCIPAL, null).build());
		}
		return tempIdList;
	}
	
	@Override
	public boolean hasDerivedRole(String principalId, List<String> groupIds, String namespaceCode, String roleName, Map<String, String> qualification) {
        if (StringUtils.isBlank(principalId)) {
            throw new RiceIllegalArgumentException("principalId was null or blank");
        }

        if (groupIds == null) {
            throw new RiceIllegalArgumentException("groupIds was null or blank");
        }

        if (StringUtils.isBlank(namespaceCode)) {
            throw new RiceIllegalArgumentException("namespaceCode was null or blank");
        }

        if (StringUtils.isBlank(roleName)) {
            throw new RiceIllegalArgumentException("roleName was null or blank");
        }

        if (qualification == null) {
            throw new RiceIllegalArgumentException("qualification was null");
        }

        // check that the principal exists and is active
        Principal principal = getIdentityService().getPrincipal( principalId );
        if ( principal == null || !principal.isActive() ) {
            return false;
        }
        // check that the identity is active
        EntityDefault entity = getIdentityService().getEntityDefault( principal.getEntityId() );
        return entity != null && entity.isActive();
	}
	
	protected IdentityService getIdentityService() {
		if ( identityService == null ) {
			identityService = KimApiServiceLocator.getIdentityService();
		}
		return identityService;
	}
}
