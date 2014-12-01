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
