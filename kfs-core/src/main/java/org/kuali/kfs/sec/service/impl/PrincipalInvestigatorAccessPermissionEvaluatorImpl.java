/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
