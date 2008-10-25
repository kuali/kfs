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
package org.kuali.kfs.module.bc;

import org.kuali.kfs.module.bc.document.web.struts.OrganizationSelectionTreeAction;
import org.kuali.kfs.sys.FinancialSystemModuleAuthorizerBase;
import org.kuali.rice.kns.authorization.AuthorizationType;
import org.kuali.rice.kim.bo.Person;

/**
 * This class...
 */
public class BudgetModuleAuthorizer extends FinancialSystemModuleAuthorizerBase {

    /**
     * @see org.kuali.kfs.sys.FinancialSystemModuleAuthorizerBase#isAuthorized(org.kuali.rice.kim.bo.Person,
     *      org.kuali.rice.kns.authorization.AuthorizationType)
     */
    @Override
    public boolean isAuthorized(Person user, AuthorizationType authorizationType) {

        if (OrganizationSelectionTreeAction.class.equals(authorizationType.getTargetObjectClass())) {
            return org.kuali.kfs.sys.context.SpringContext.getBean(org.kuali.kfs.sys.service.KNSAuthorizationService.class).isActive(user);
        }
        return super.isAuthorized(user, authorizationType);
    }

}

