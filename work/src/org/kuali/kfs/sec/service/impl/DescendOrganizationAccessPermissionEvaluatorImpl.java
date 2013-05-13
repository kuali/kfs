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
import org.kuali.kfs.coa.service.OrganizationService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;

/**
 * Custom access permission evaluator that looks at the organization hierarchy when matching values
 */
public class DescendOrganizationAccessPermissionEvaluatorImpl extends AccessPermissionEvaluatorImpl {

    /**
     * Matches org values based on org hierarchy
     * 
     * @see org.kuali.kfs.sec.service.impl.AccessPermissionEvaluatorImpl#isMatch(java.lang.String, java.lang.String)
     */
    @Override
    protected boolean isMatch(String matchValue, String value) {
        boolean match = false;

        if (StringUtils.equalsIgnoreCase(value, matchValue)) {
            match = true;
        }
        else {
            String chartCode = (String) otherKeyFieldValues.get(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
            match = SpringContext.getBean(OrganizationService.class).isParentOrganization(chartCode, value, chartCode, matchValue);
        }

        return match;
    }

}
