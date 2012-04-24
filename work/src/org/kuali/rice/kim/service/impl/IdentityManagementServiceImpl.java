/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.rice.kim.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.kuali.rice.kim.api.identity.IdentityService;
import org.kuali.rice.kim.api.identity.entity.Entity;
import org.kuali.rice.kim.api.identity.entity.EntityDefault;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.permission.PermissionService;
import org.kuali.rice.kim.api.services.IdentityManagementService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;

@Deprecated
public class IdentityManagementServiceImpl implements IdentityManagementService {
	private static final Logger LOG = Logger.getLogger( IdentityManagementServiceImpl.class );

	protected PermissionService permissionService;
	protected IdentityService identityService;

    // AUTHORIZATION SERVICE
    @Override
    public boolean hasPermission(String principalId, String namespaceCode, String permissionName) {
    	return getPermissionService().hasPermission(principalId, namespaceCode, permissionName );
    }

    @Override
    public boolean isAuthorized(String principalId, String namespaceCode, String permissionName, Map<String, String> qualification ) {
    	if ( qualification == null || qualification.isEmpty() ) {
    		return hasPermission( principalId, namespaceCode, permissionName );
    	}
        return getPermissionService().isAuthorized(principalId, namespaceCode, permissionName, qualification);
    }

    @Override
    public boolean hasPermissionByTemplateName(String principalId, String namespaceCode, String permissionTemplateName, Map<String, String> permissionDetails) {
		return getPermissionService().hasPermissionByTemplate(principalId, namespaceCode, permissionTemplateName, permissionDetails);
    }

    @Override
    public boolean isAuthorizedByTemplateName(String principalId, String namespaceCode, String permissionTemplateName, Map<String, String> permissionDetails, Map<String, String> qualification ) {
    	if ( qualification == null || qualification.isEmpty() ) {
    		return hasPermissionByTemplateName( principalId, namespaceCode, permissionTemplateName, new HashMap<String, String>(permissionDetails) );
    	}
    	return getPermissionService().isAuthorizedByTemplate( principalId, namespaceCode, permissionTemplateName, new HashMap<String, String>(permissionDetails), new HashMap<String, String>(qualification) );
    }

    // IDENTITY SERVICE
    @Override
	public Principal getPrincipal(String principalId) {
		return getIdentityService().getPrincipal(principalId);
	}

    @Override
    public Principal getPrincipalByPrincipalName(String principalName) {
		return getIdentityService().getPrincipalByPrincipalName(principalName);
    }

    @Override
    public EntityDefault getEntityDefaultInfo(String entityId) {
		return getIdentityService().getEntityDefault(entityId);
    }

    @Override
    public EntityDefault getEntityDefaultInfoByPrincipalId(
    		String principalId) {
    	return getIdentityService().getEntityDefaultByPrincipalId(principalId);
    }

    @Override
    public EntityDefault getEntityDefaultInfoByPrincipalName(
    		String principalName) {
    	return getIdentityService().getEntityDefaultByPrincipalName(principalName);
    }

    @Override
    public Entity getEntityByPrincipalId(String principalId) {
        return getIdentityService().getEntityByPrincipalId(principalId);
    }

    	// OTHER METHODS

	public IdentityService getIdentityService() {
		if ( identityService == null ) {
			identityService = KimApiServiceLocator.getIdentityService();
		}
		return identityService;
	}

	public PermissionService getPermissionService() {
		if ( permissionService == null ) {
			permissionService = KimApiServiceLocator.getPermissionService();
		}
		return permissionService;
	}
}
