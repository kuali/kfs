/*
 * Copyright 2007-2008 The Kuali Foundation
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
package org.kuali.rice.kim.api.role;

/**
 * This service adds caching on top of the RoleService. 
 * 
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public interface RoleManagementService extends RoleService, RoleUpdateService {

	/**
	 * Flush all of the role-related caches.
	 */
	void flushRoleCaches();
	
	/**
	 * Flush all of the role-member-related caches.
	 */
	void flushRoleMemberCaches();
	
	/**
	 * Flush all of the delegation-related caches.
	 */
	void flushDelegationCaches();

	/**
	 * Flush all of the delegation-member-related caches.
	 */
	void flushDelegationMemberCaches();
	
	/**
	 * Remove all cache entries for the given roleId and principalId combination.
	 */
	void removeCacheEntries( String roleId, String principalId );
}
