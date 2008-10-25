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
package org.kuali.kfs.sys;

import org.kuali.rice.kim.bo.Person;
import org.kuali.kfs.sys.service.ParameterEvaluator;
import org.kuali.rice.kns.authorization.AuthorizationType;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.util.KNSConstants;

public class ActiveStaffFacultyAffiliateModuleAuthorizer extends FinancialSystemModuleAuthorizerBase {

    /** check that the user is an active staff/faculty/affiliate of the institution */
    @Override
    public boolean canAccessModule(Person user) {
        ParameterEvaluator pe = parameterService.getParameterEvaluator(Person.class, KNSConstants.ALLOWED_EMPLOYEE_STATUS_RULE, user.getEmployeeStatusCode());
        return pe.evaluationSucceeds() && (user.hasAffiliationOfType( org.kuali.rice.kim.util.KimConstants.STAFF_AFFILIATION_TYPE ) || user.hasAffiliationOfType( org.kuali.rice.kim.util.KimConstants.FACULTY_AFFILIATION_TYPE ) || user.hasAffiliationOfType( org.kuali.rice.kim.util.KimConstants.AFFILIATE_AFFILIATION_TYPE ));
    }

    public boolean isAuthorized(Person user, AuthorizationType authorizationType) {
        return canAccessModule(user);
    }
}

