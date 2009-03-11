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

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.bc.BCParameterKeyConstants;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionIntendedIncumbent;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPayRateHolding;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPosition;
import org.kuali.kfs.module.bc.document.BudgetConstructionDocument;
import org.kuali.kfs.module.bc.batch.GenesisBatchStep;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.ParameterService;

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

    /**
     * get the weekly working hours setup in system paremters for extract process
     * 
     * @return the weekly working hours setup in system paremters
     */
    public static Integer getWeeklyWorkingHours() {
        String weeklyWorkingHours = parameterService.getParameterValue(BudgetConstructionDocument.class, BCParameterKeyConstants.WEEKLY_WORKING_HOURS);

        return Integer.valueOf(StringUtils.trim(weeklyWorkingHours));
    }

    /**
     * get the weekly working hours setup in system paremters for extract process
     * 
     * @return the weekly working hours setup in system paremters
     */
    public static BigDecimal getWeeklyWorkingHoursAsDecimal() {
        Integer weeklyWorkingHours = getWeeklyWorkingHours();

        return BigDecimal.valueOf(weeklyWorkingHours);
    }

    /**
     * get the sub fund group codes not allowed 2plg generation setup in system parameters
     * 
     * @return the sub fund group codes not allowed 2plg generation setup in system parameters
     */
    public static List<String> getNotGenerate2PlgSubFundGroupCodes() {
        return parameterService.getParameterValues(BudgetConstructionDocument.class, BCParameterKeyConstants.GENERATE_2PLG_SUB_FUND_GROUPS);
    }

    /**
     * get the biweekly pay object codes setup in system parameters
     * 
     * @return the biweekly pay object codes setup in system parameters
     */
    public static List<String> getBiweeklyPayObjectCodes() {
        return parameterService.getParameterValues(BudgetConstructionPayRateHolding.class, BCParameterKeyConstants.BIWEEKLY_PAY_OBJECT_CODES);
    }

    /**
     * get the revenue object types allowed in budget setup in system parameters
     * 
     * @return the revenue object types allowed in budget setup in system parameters
     */
    public static List<String> getRevenueObjectTypes() {
        return parameterService.getParameterValues(BudgetConstructionDocument.class, BCParameterKeyConstants.REVENUE_OBJECT_TYPES);
    }

    /**
     * get the expenditure object types allowed in budget setup in system parameters
     * 
     * @return the expenditure object types allowed in budget setup in system parameters
     */
    public static List<String> getExpenditureObjectTypes() {
        return parameterService.getParameterValues(BudgetConstructionDocument.class, BCParameterKeyConstants.EXPENDITURE_OBJECT_TYPES);
    }

    /**
     * get the budget aggregation codes setup in system parameters
     * 
     * @return the budget aggregation codes setup in system parameters
     */
    public static List<String> getBudgetAggregationCodes() {
        return parameterService.getParameterValues(BudgetConstructionDocument.class, BCParameterKeyConstants.BUDGET_AGGREGATION_CODES);
    }

    /**
     * get the fringe benefit designator codes setup in system parameters
     * 
     * @return the fringe benefit designator codes setup in system parameters
     */
    public static List<String> getFringeBenefitDesignatorCodes() {
        return parameterService.getParameterValues(BudgetConstructionDocument.class, BCParameterKeyConstants.FRINGE_BENEFIT_DESIGNATOR_CODES);
    }

    /**
     * get the salary setting fund groups setup in system parameters
     * 
     * @return the salary setting fund groups setup in system parameters
     */
    public static List<String> getSalarySettingFundGroups() {
        return parameterService.getParameterValues(BudgetConstructionDocument.class, BCParameterKeyConstants.SALARY_SETTING_FUND_GROUPS);
    }

    /**
     * get the salary setting sub fund groups setup in system parameters
     * 
     * @return the salary setting sub fund groups setup in system parameters
     */
    public static List<String> getSalarySettingSubFundGroups() {
        return parameterService.getParameterValues(BudgetConstructionDocument.class, BCParameterKeyConstants.SALARY_SETTING_SUB_FUND_GROUPS);
    }

    /**
     * indicates whether the data for the budget construction intended incumbent table is populated from an external system or is
     * maintained within the KFS.
     */
    public static boolean getPayrollIncumbentFeedIndictor() {
        return parameterService.getIndicatorParameter(BudgetConstructionIntendedIncumbent.class, BCParameterKeyConstants.EXTERNAL_INCUMBENT_FEED_IND);
    }

    /**
     * Indicates whether the data for the budget construction position table is populated from an external system or is maintained
     * within the KFS.
     */
    public static boolean getPayrollPositionFeedIndicator() {
        return parameterService.getIndicatorParameter(BudgetConstructionPosition.class, BCParameterKeyConstants.EXTERNAL_POSITION_FEED_IND);
    }
    
    /**
     * returns the base fiscal year to use to initialize budget construction
     */
    
    public static Integer getBaseFiscalYear()
    {
        String yearValue = parameterService.getParameterValue(GenesisBatchStep.class, BCParameterKeyConstants.SOURCE_FISCAL_YEAR);
        return (Integer.valueOf(StringUtils.trim(yearValue)));
    }
}
