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
package org.kuali.kfs.module.bc.util;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.bc.BCParameterKeyConstants;
import org.kuali.kfs.module.bc.document.BudgetConstructionDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ParameterService;

/**
 * A convenient utility that can delegate the calling client to retrieve system parameters of budget construction module.
 */
public class BudgetParameterFinder {
    private static ParameterService parameterService = SpringContext.getBean(ParameterService.class);

    /**
     * get the biweekly pay type codes setup in system parameters
     * 
     * @return the biweekly pay type codes setup in system parameters
     */
    public static List<String> getBiweeklyPayTypeCodes() {
        return parameterService.getParameterValues(BudgetConstructionDocument.class, BCParameterKeyConstants.BIWEEKLY_PAY_TYPE_CODES);
    }

    /**
     * get the annual working hours setup in system paremters for extract process
     * 
     * @return the annual working hours setup in system paremters
     */
    public static Integer getAnnualWorkingHours() {
        String annualWorkingHours = parameterService.getParameterValue(BudgetConstructionDocument.class, BCParameterKeyConstants.ANNUAL_WORKING_HOURS);

        return Integer.valueOf(StringUtils.trim(annualWorkingHours));
    }
}
