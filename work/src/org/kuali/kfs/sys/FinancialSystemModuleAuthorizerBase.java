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

import org.kuali.core.authorization.AuthorizationType;
import org.kuali.core.authorization.KualiModuleAuthorizerBase;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.kfs.bo.FinancialSystemUser;
import org.kuali.kfs.service.FinancialSystemUserService;
import org.kuali.kfs.service.ParameterService;

import edu.iu.uis.eden.EdenConstants;

public class FinancialSystemModuleAuthorizerBase extends KualiModuleAuthorizerBase implements FinancialSystemModuleAuthorizer {

    protected FinancialSystemUserService financialSystemUserService;
    protected ParameterService parameterService;
    
    public FinancialSystemUserService getFinancialSystemUserService() {
        return financialSystemUserService;
    }

    public void setFinancialSystemUserService(FinancialSystemUserService financialSystemUserService) {
        this.financialSystemUserService = financialSystemUserService;
    }

    public ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
    
    /** Check whether the user can access the module at all.  Default implementation just checks the user's active status on KFSUser */
    public boolean canAccessModule( FinancialSystemUser user ) {
        return user.isActiveFinancialSystemUser();
    }
    
    @Override
    public boolean isAuthorized(UniversalUser user, AuthorizationType authorizationType) {
        if (authorizingForAdHocApproveRequest(authorizationType) || authorizingForDocumentInitiation(authorizationType)) {
            return financialSystemUserService.convertUniversalUserToFinancialSystemUser(user).isActiveFinancialSystemUser();
        }
        return super.isAuthorized(user, authorizationType);
    }

    private boolean authorizingForAdHocApproveRequest(AuthorizationType authorizationType) {
        return (authorizationType instanceof AuthorizationType.AdHocRequest) && EdenConstants.ACTION_REQUEST_APPROVE_REQ.equals(((AuthorizationType.AdHocRequest) authorizationType).getActionRequested());
    }

    private boolean authorizingForDocumentInitiation(AuthorizationType authorizationType) {
        return (authorizationType instanceof AuthorizationType.Document) && ((((AuthorizationType.Document) authorizationType).getDocument() == null) || ((AuthorizationType.Document) authorizationType).getDocument().getDocumentHeader().getWorkflowDocument().stateIsInitiated() || ((AuthorizationType.Document) authorizationType).getDocument().getDocumentHeader().getWorkflowDocument().stateIsSaved());
    }
}
