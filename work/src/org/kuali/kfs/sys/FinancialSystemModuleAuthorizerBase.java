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
package org.kuali.kfs.sys;

import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.kfs.sys.service.ParameterService;
import org.kuali.rice.kew.util.KEWConstants;
import org.kuali.rice.kns.authorization.AuthorizationType;
import org.kuali.rice.kns.authorization.KualiModuleAuthorizerBase;
import org.kuali.rice.kim.bo.Person;

public class FinancialSystemModuleAuthorizerBase extends KualiModuleAuthorizerBase implements FinancialSystemModuleAuthorizer {

    protected PersonService personService;
    protected ParameterService parameterService;
    
    public PersonService getPersonService() {
        return personService;
    }

    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    public ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
    
    /** Check whether the user can access the module at all.  Default implementation just checks the user's active status on KFSUser */
    public boolean canAccessModule( Person user ) {
        return org.kuali.kfs.sys.context.SpringContext.getBean(org.kuali.kfs.sys.service.FinancialSystemUserService.class).isActiveFinancialSystemUser(user);
    }
    
    @Override
    public boolean isAuthorized(Person user, AuthorizationType authorizationType) {
        if (authorizingForAdHocApproveRequest(authorizationType) || authorizingForDocumentInitiation(authorizationType)) {
            return org.kuali.kfs.sys.context.SpringContext.getBean(org.kuali.kfs.sys.service.FinancialSystemUserService.class).isActiveFinancialSystemUser(user);
        }
        return super.isAuthorized(user, authorizationType);
    }

    private boolean authorizingForAdHocApproveRequest(AuthorizationType authorizationType) {
        return (authorizationType instanceof AuthorizationType.AdHocRequest) && KEWConstants.ACTION_REQUEST_APPROVE_REQ.equals(((AuthorizationType.AdHocRequest) authorizationType).getActionRequested());
    }

    private boolean authorizingForDocumentInitiation(AuthorizationType authorizationType) {
        return (authorizationType instanceof AuthorizationType.Document) && ((((AuthorizationType.Document) authorizationType).getDocument() == null) || ((AuthorizationType.Document) authorizationType).getDocument().getDocumentHeader().getWorkflowDocument().stateIsInitiated() || ((AuthorizationType.Document) authorizationType).getDocument().getDocumentHeader().getWorkflowDocument().stateIsSaved());
    }
}

