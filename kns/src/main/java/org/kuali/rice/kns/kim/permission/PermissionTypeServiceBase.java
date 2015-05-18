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
package org.kuali.rice.kns.kim.permission;

import org.apache.commons.lang.StringUtils;
import org.kuali.rice.kim.api.permission.Permission;
import org.kuali.rice.kim.framework.permission.PermissionTypeService;
import org.kuali.rice.kns.kim.type.DataDictionaryTypeServiceBase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @deprecated A krad integrated type service base class will be provided in the future.
 */
@Deprecated
public class PermissionTypeServiceBase extends DataDictionaryTypeServiceBase implements PermissionTypeService {

	@Override
	public final List<Permission> getMatchingPermissions(Map<String, String> requestedDetails, List<Permission> permissionsList) {
		requestedDetails = translateInputAttributes(requestedDetails);
		validateRequiredAttributesAgainstReceived(requestedDetails);
		return Collections.unmodifiableList(performPermissionMatches(requestedDetails, permissionsList));
	}

	/**
	 * Internal method for matching permissions.  Override this method to customize the matching behavior.
	 * 
	 * This base implementation uses the {@link #performMatch(Map, Map)} method
	 * to perform an exact match on the permission details and return all that are equal.
	 */
	protected List<Permission> performPermissionMatches(Map<String, String> requestedDetails, List<Permission> permissionsList) {
		List<Permission> matchingPermissions = new ArrayList<Permission>();
		for (Permission permission : permissionsList) {
			if ( performMatch(requestedDetails, permission.getAttributes()) ) {
				matchingPermissions.add( permission );
			}
		}
		return matchingPermissions;
	}
	
	/**
	 * 
	 * Internal method for checking if property name matches
	 * 
	 * @param requestedDetailsPropertyName name of requested details property
	 * @param permissionDetailsPropertyName name of permission details property
	 * @return boolean 
	 */
	protected boolean doesPropertyNameMatch(
			String requestedDetailsPropertyName,
			String permissionDetailsPropertyName) {
		if (StringUtils.isBlank(permissionDetailsPropertyName)) {
			return true;
		}
		if ( requestedDetailsPropertyName == null ) {
		    requestedDetailsPropertyName = ""; // prevent NPE
		}
		return StringUtils.equals(requestedDetailsPropertyName, permissionDetailsPropertyName)
				|| (requestedDetailsPropertyName.startsWith(permissionDetailsPropertyName+"."));
	}
}
