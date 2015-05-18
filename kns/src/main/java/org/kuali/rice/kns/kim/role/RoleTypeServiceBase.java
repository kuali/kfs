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
import org.kuali.rice.kim.api.role.Role;
import org.kuali.rice.kim.api.role.RoleMembership;
import org.kuali.rice.kim.framework.common.delegate.DelegationTypeService;
import org.kuali.rice.kim.framework.role.RoleTypeService;
import org.kuali.rice.kns.kim.type.DataDictionaryTypeServiceBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @deprecated A krad integrated type service base class will be provided in the future.
 */
@Deprecated
public class RoleTypeServiceBase extends DataDictionaryTypeServiceBase implements RoleTypeService, DelegationTypeService {
	
	/**
	 * Performs a simple check that the qualifier on the role matches the qualification.
	 * Extra qualification attributes are ignored.
	 */
    @Override
	public boolean doesRoleQualifierMatchQualification(Map<String, String> qualification, Map<String, String> roleQualifier) {
		if (qualification == null) {
            throw new RiceIllegalArgumentException("qualification was null or blank");
        }

        if (roleQualifier == null) {
            throw new RiceIllegalArgumentException("roleQualifier was null or blank");
        }

        Map<String, String> translatedQualification = translateInputAttributes(qualification);
		validateRequiredAttributesAgainstReceived(translatedQualification);
		return performMatch(translatedQualification, roleQualifier);
	}
	
    @Override
	public List<RoleMembership> getMatchingRoleMemberships(Map<String, String> qualification,
            List<RoleMembership> roleMemberList) {
		if (qualification == null) {
            throw new RiceIllegalArgumentException("qualification was null or blank");
        }

        if (roleMemberList == null) {
            throw new RiceIllegalArgumentException("roleMemberList was null or blank");
        }

        Map<String, String> translatedQualification = translateInputAttributes(qualification);
		validateRequiredAttributesAgainstReceived(translatedQualification);
		List<RoleMembership> matchingMemberships = new ArrayList<RoleMembership>();
		for ( RoleMembership roleMembership : roleMemberList ) {
			if ( performMatch( translatedQualification, roleMembership.getQualifier() ) ) {
				matchingMemberships.add( roleMembership );
			}
		}
		return Collections.unmodifiableList(matchingMemberships);
	}

	/**
	 * Return an empty list since this method should not be called by the role service for this service type.
	 * Subclasses which are application role types should override this method.
	 *
	 */
    @Override
	public List<RoleMembership> getRoleMembersFromDerivedRole(String namespaceCode, String roleName, Map<String, String> qualification) {

        if (StringUtils.isBlank(namespaceCode)) {
            throw new RiceIllegalArgumentException("namespaceCode was null or blank");
        }

        if (StringUtils.isBlank(roleName)) {
            throw new RiceIllegalArgumentException("roleName was null or blank");
        }

        if (qualification == null) {
            throw new RiceIllegalArgumentException("qualification was null or blank");
        }

        validateRequiredAttributesAgainstReceived(qualification);
		if ( !isDerivedRoleType() ) {
			throw new UnsupportedOperationException( this.getClass().getName() + " is not an application role." );
		} else {
			throw new UnsupportedOperationException( this.getClass().getName() + " is an application role type but has not overridden this method." );
		}
	}

	/**
	 * This simple initial implementation just calls  
	 * {@link #getRoleMembersFromApplicationRole(String, String, Map<String, String>)} and checks the results.
	 *
	 */
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
            throw new RiceIllegalArgumentException("qualification was null or blank");
        }

        if ( !isDerivedRoleType() ) {
			throw new UnsupportedOperationException( this.getClass().getName() + " is not an application role." );
		}
		// if principal ID given, check if it is in the list generated from the getPrincipalIdsFromApplicationRole method
		if ( StringUtils.isNotBlank( principalId ) ) {
		    List<RoleMembership> members = getRoleMembersFromDerivedRole(namespaceCode, roleName, qualification);
		    for ( RoleMembership rm : members ) {
		    	if ( StringUtils.isBlank( rm.getMemberId() ) ) {
		    		continue;
		    	}
		        if ( MemberType.PRINCIPAL.equals(rm.getType()) ) {
		            if ( rm.getMemberId().equals( principalId ) ) {
		                return true;
		            }
		        } else { // groups
		            if ( groupIds != null 
		                    && groupIds.contains(rm.getMemberId())) {
		                return true;
		            }
		        }
			}
		}
		return false;
	}
	
	/**
	 * Default to not being a derived role type.  Always returns false.
	 * 
	 * @see RoleTypeService#isDerivedRoleType()
	 */
    @Override
	public boolean isDerivedRoleType() {
		return false;
	}
		
	/**
	 * This base implementation simply returns the passed in Attributes.
	 * 
	 * @see RoleTypeService#convertQualificationForMemberRoles(String, String, String, String, Map<String, String>)
	 */
    @Override
	public Map<String, String> convertQualificationForMemberRoles(String namespaceCode, String roleName, String memberRoleNamespaceCode, String memberRoleName, Map<String, String> qualification) {

        if (StringUtils.isBlank(namespaceCode)) {
            throw new RiceIllegalArgumentException("namespaceCode was null or blank");
        }

        if (StringUtils.isBlank(roleName)) {
            throw new RiceIllegalArgumentException("roleName was null or blank");
        }

        if (StringUtils.isBlank(memberRoleNamespaceCode)) {
            throw new RiceIllegalArgumentException("memberRoleNamespaceCode was null or blank");
        }

        if (StringUtils.isBlank(memberRoleName)) {
            throw new RiceIllegalArgumentException("memberRoleName was null or blank");
        }

        if (qualification == null) {
            throw new RiceIllegalArgumentException("qualification was null or blank");
        }

        return Collections.unmodifiableMap(new HashMap<String, String>(qualification));
	}
		
	/**
	 * Performs a simple check that the qualifier on the delegation matches the qualification.
	 * Extra qualification attributes are ignored.
	 *
	 */
    @Override
	public boolean doesDelegationQualifierMatchQualification(Map<String, String> qualification, Map<String, String> roleQualifier) {
		if (qualification == null) {
            throw new RiceIllegalArgumentException("qualification was null or blank");
        }

        if (roleQualifier == null) {
            throw new RiceIllegalArgumentException("roleQualifier was null or blank");
        }

        Map<String, String> translatedQualification = translateInputAttributes(qualification);
		validateRequiredAttributesAgainstReceived(translatedQualification);
		return performMatch(translatedQualification, roleQualifier);
	}

	/**
	 * Returns false as a default
	 * 
	 * @see RoleTypeService#dynamicRoleMembership(String, String)
	 */
    @Override
	public boolean dynamicRoleMembership(String namespaceCode, String roleName) {
		if (StringUtils.isBlank(namespaceCode)) {
            throw new RiceIllegalArgumentException("namespaceCode was null or blank");
        }

        if (StringUtils.isBlank(roleName)) {
            throw new RiceIllegalArgumentException("roleName was null or blank");
        }

        return false;
	}

    @Override
	public List<String> getQualifiersForExactMatch() {    
		return Collections.emptyList();
	}

    /**
     * Base implementation: no sorting.  Just returns the input list.
     */
	@Override
	public List<RoleMembership> sortRoleMembers(List<RoleMembership> roleMembers)
			throws RiceIllegalArgumentException {
		return roleMembers;
	}

    @Override
    public boolean shouldValidateQualifiersForMemberType(MemberType memberType) {
        return false;
    }
}
