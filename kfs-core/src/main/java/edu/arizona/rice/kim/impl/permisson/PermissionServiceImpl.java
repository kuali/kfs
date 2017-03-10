/*
 * Copyright 2014 The Kuali Foundation
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

package edu.arizona.rice.kim.impl.permisson;

import java.util.Map;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.exception.RiceIllegalArgumentException;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.IdentityService;
import org.kuali.rice.kim.api.identity.entity.Entity;

// UAF-6.0 upgrade
public class PermissionServiceImpl extends org.kuali.rice.kim.impl.permission.PermissionServiceImpl {
    private IdentityService identityService;
    private ConfigurationService configurationService;
    @Override
    public boolean isAuthorized(String principalId, String namespaceCode, String permissionName, Map<String, String> qualification) throws RiceIllegalArgumentException {
        boolean retval = false;
        // UA UPGRADE - if logging in and user is active employee the let them in
        if (KimConstants.PermissionNames.LOG_IN.equals(permissionName)) {
            retval = isAuthorizedToLogin(principalId);
        } else {
            retval = super.isAuthorized(principalId, namespaceCode, permissionName, qualification);
        }

        return retval;
    }

    private boolean isAuthorizedToLogin(String principalId) {
        Entity e = identityService.getEntityByPrincipalId(principalId);
        return (e != null);
    }
    
    public void setIdentityService(IdentityService identityService) {
        this.identityService = identityService;
    }

    public ConfigurationService getConfigurationService() {
        if (configurationService == null) {
            configurationService = SpringContext.getBean(ConfigurationService.class);
        }
        
        return configurationService;
    }
}    
