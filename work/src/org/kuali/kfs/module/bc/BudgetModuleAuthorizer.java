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
package org.kuali.module.budget.authorization;

import org.kuali.core.authorization.AuthorizationType;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.kfs.authorization.KfsModuleAuthorizerBase;
import org.kuali.module.budget.web.struts.action.OrganizationSelectionTreeAction;

/**
 * This class...
 */
public class BudgetModuleAuthorizer extends KfsModuleAuthorizerBase {

    /**
     * @see org.kuali.kfs.authorization.KfsModuleAuthorizerBase#isAuthorized(org.kuali.core.bo.user.UniversalUser, org.kuali.core.authorization.AuthorizationType)
     */
    @Override
    public boolean isAuthorized(UniversalUser user, AuthorizationType authorizationType) {

        if (OrganizationSelectionTreeAction.class.equals(authorizationType.getTargetObjectClass())){
            return user.isActiveForModule(getModule().getModuleId());
        }
        return super.isAuthorized(user, authorizationType);
    }

}
