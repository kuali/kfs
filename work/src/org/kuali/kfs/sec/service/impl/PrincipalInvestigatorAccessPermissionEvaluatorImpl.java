/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.sec.service.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.identity.Person;

/**
 * Custom access permission evaluator for principal investigator restrictions
 */
public class PrincipalInvestigatorAccessPermissionEvaluatorImpl extends AccessPermissionEvaluatorImpl {

    /**
     * Matches on accounts for which the person is a principal investigator (project director)
     * 
     * @see org.kuali.kfs.sec.service.impl.AccessPermissionEvaluatorImpl#isMatch(java.lang.String, java.lang.String)
     */
    @Override
    protected boolean isMatch(String matchValue, String value) {
        boolean match = false;

        String chartCode = (String) otherKeyFieldValues.get(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);

        Person principalInvestigator = SpringContext.getBean(ContractsAndGrantsModuleService.class).getProjectDirectorForAccount(chartCode, value);
        if (StringUtils.equals(person.getPrincipalId(), principalInvestigator.getPrincipalId())) {
            match = true;
        }

        return match;
    }

}
