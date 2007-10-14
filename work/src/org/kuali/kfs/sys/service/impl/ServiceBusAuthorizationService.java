/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.authorization;

import javax.servlet.http.HttpServletRequest;

import org.kuali.bus.auth.AuthorizationService;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.service.UniversalUserService;
import org.kuali.core.service.WebAuthenticationService;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ParameterService;
import org.kuali.kfs.service.impl.ParameterConstants;

public class ServiceBusAuthorizationService implements AuthorizationService {
    private ParameterService parameterService;

    public boolean isAdministrator(HttpServletRequest request) {
        String networkId = SpringContext.getBean(WebAuthenticationService.class).getNetworkId(request);
        try {
            UniversalUser user = SpringContext.getBean(UniversalUserService.class).getUniversalUserByAuthenticationUserId(networkId);
            return user.isMember(getParameterService().getParameterValue(ParameterConstants.FINANCIAL_SYSTEM_ALL.class, KFSConstants.CoreApcParms.SERVICE_BUS_ACCESS_GROUP_PARM));
        }
        catch (UserNotFoundException e) {
            throw new RuntimeException("Failed to fetch user " + networkId, e);
        }
    }

    private ParameterService getParameterService() {
        if (parameterService == null) {
            parameterService = SpringContext.getBean(ParameterService.class);
        }
        return parameterService;
    }
}
