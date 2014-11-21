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
package org.kuali.rice.kim.api.services;

import java.util.Map;

import org.kuali.rice.kim.api.identity.entity.Entity;
import org.kuali.rice.kim.api.identity.entity.EntityDefault;
import org.kuali.rice.kim.api.identity.principal.Principal;

/**
 * This is the front end for the KIM module.  Clients of KIM should access this service from
 * their applications.  If KIM is not running on the same machine (VM) as the application
 * (as would be the case with a standalone Rice server), then this service should be implemented
 * locally within the application and access the core KIM services
 * (Authentication/Authorization/Identity/Group) via the service bus.
 *
 *  For efficiency, implementations of this interface should add appropriate caching of
 *  the information retrieved from the core services for load and performance reasons.
 *
 *  Most of the methods on this interface are straight pass-thrus to methods on the four core services.
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 *
 */
@Deprecated
public interface IdentityManagementService {

	// *******************************
	// IdentityService
	// *******************************

	Principal getPrincipal( String principalId);
	Principal getPrincipalByPrincipalName( String principalName);

	EntityDefault getEntityDefaultInfo( String entityId);
	EntityDefault getEntityDefaultInfoByPrincipalId( String principalId);
	EntityDefault getEntityDefaultInfoByPrincipalName( String principalName);

	Entity getEntityByPrincipalId( String principalId);

    // --------------------
    // Authorization Checks
    // --------------------

    boolean hasPermission(
             String principalId,
             String namespaceCode,
             String permissionName
    );

    boolean isAuthorized(
             String principalId,
             String namespaceCode,
             String permissionName,
              Map<String, String> qualification
    );

    boolean hasPermissionByTemplateName(
             String principalId,
             String namespaceCode,
             String permissionTemplateName,
              Map<String, String> permissionDetails
    );

    boolean isAuthorizedByTemplateName(
             String principalId,
             String namespaceCode,
             String permissionTemplateName,
             Map<String, String> permissionDetails,
             Map<String, String> qualification
    );

}
