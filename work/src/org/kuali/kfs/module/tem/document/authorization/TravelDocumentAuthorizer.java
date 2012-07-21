/*
 * Copyright 2011 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.tem.document.authorization;

import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.authorization.AccountingDocumentAuthorizerBase;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.PermissionService;
import org.kuali.rice.kim.service.RoleManagementService;
import org.kuali.rice.kim.service.RoleService;

public class TravelDocumentAuthorizer extends AccountingDocumentAuthorizerBase{
    private PermissionService permissionService;
    private RoleService roleService;
    
    public boolean canTaxSelectable(final Person user){
        return getPermissionService().hasPermission(user.getPrincipalId(), TemConstants.PARAM_NAMESPACE, TemConstants.PermissionNames.EDIT_TAXABLE_IND, null);                
    }
    
    protected final PermissionService getPermissionService() {
        if (permissionService == null) {
            permissionService = SpringContext.getBean(PermissionService.class);
        }
        return permissionService;
    }
    
    protected RoleService getRoleService() {
        if ( roleService == null ) {
            roleService = SpringContext.getBean(RoleManagementService.class);
        }

        return roleService;
    }
}
