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
