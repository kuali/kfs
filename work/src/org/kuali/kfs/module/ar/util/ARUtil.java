/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.util;

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.service.AccountsReceivableAuthorizationService;
import org.kuali.rice.kim.bo.Person;

public class ARUtil {
    
    private static AccountsReceivableAuthorizationService arAuthzService;
    
    /**
     * This method checks if the given user is an AR Supervisor
     * @param user
     * @return true is user is AR supervisor, false otherwise
     */
    public static boolean isUserInArSupervisorGroup(Person user) {
        return org.kuali.rice.kim.service.KIMServiceLocator.getIdentityManagementService().isMemberOfGroup(user.getPrincipalId(), org.kuali.rice.kim.service.KIMServiceLocator.getIdentityManagementService().getGroupByName(org.kuali.kfs.sys.KFSConstants.KFS_GROUP_NAMESPACE, ArConstants.AR_SUPERVISOR_GROUP_NAME).getGroupId());
    }

    public static boolean isUserInArBillingOrg(Person user) {
        return getArAuthzService().personBelongsToBillingOrg(user);
    }
    
    public static boolean isUserInArProcessingOrg(Person user) {
        return getArAuthzService().personBelongsToProcessingOrg(user);
    }
    
    //  keep a local static reference cached
    private static AccountsReceivableAuthorizationService getArAuthzService() {
        if (arAuthzService == null) {
            arAuthzService = org.kuali.kfs.sys.context.SpringContext.getBean(AccountsReceivableAuthorizationService.class);
        }
        return arAuthzService;
    }
}

