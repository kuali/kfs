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
package org.kuali.module.budget.service.impl;

import java.util.List;

import org.kuali.kfs.service.ParameterService;
import org.kuali.module.budget.BCParameterKeyConstants;
import org.kuali.module.budget.BCConstants.AccountSalarySettingOnlyCause;
import org.kuali.module.budget.document.BudgetConstructionDocument;
import org.kuali.module.budget.service.BudgetParameterService;

/**
 * See BudgetParameterService. This implements value added methods associated with ParameterService
 * that are specific to the budget module.
 */
public class BudgetParameterServiceImpl implements BudgetParameterService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetParameterService.class);
    
    private ParameterService parameterService;

    /**
     * @see org.kuali.module.budget.service.BudgetParameterService#getParameterValues(java.lang.Class, java.lang.String)
     */
    public List getParameterValues(Class componentClass, String parameterName) {
        List paramValues = null;

        if (parameterService.parameterExists(componentClass, parameterName)) {
            paramValues = parameterService.getParameterValues(componentClass, parameterName);
        }
        else {
            LOG.info("Can't find system parameter " + parameterName);
        }
        return paramValues;
    }

    /**
     * 
     * @see org.kuali.module.budget.service.BudgetParameterService#isSalarySettingOnlyAccount(org.kuali.module.budget.document.BudgetConstructionDocument)
     */
    public AccountSalarySettingOnlyCause isSalarySettingOnlyAccount(BudgetConstructionDocument bcDoc){
        AccountSalarySettingOnlyCause retVal = AccountSalarySettingOnlyCause.MISSING_PARAM;
        Class docClass = bcDoc.getClass();
        List salarySettingFundGroupsParamValues = this.getParameterValues(docClass, BCParameterKeyConstants.SALARY_SETTING_FUND_GROUPS);
        List salarySettingSubFundGroupsParamValues = this.getParameterValues(docClass, BCParameterKeyConstants.SALARY_SETTING_SUB_FUND_GROUPS);

        if (salarySettingFundGroupsParamValues != null) {
            if (salarySettingSubFundGroupsParamValues != null) {
                retVal = AccountSalarySettingOnlyCause.NONE;
                
                // is the account in a salary setting only fund group or sub-fund group, if neither calc benefits
                String fundGroup = bcDoc.getAccount().getSubFundGroup().getFundGroupCode();
                String subfundGroup = bcDoc.getAccount().getSubFundGroup().getSubFundGroupCode();
                if (salarySettingFundGroupsParamValues.contains(fundGroup) && salarySettingSubFundGroupsParamValues.contains(subfundGroup)){
                    retVal = AccountSalarySettingOnlyCause.FUND_AND_SUBFUND;
                } else {
                    if (salarySettingFundGroupsParamValues.contains(fundGroup)){
                        retVal = AccountSalarySettingOnlyCause.FUND;
                    }
                    if (salarySettingSubFundGroupsParamValues.contains(subfundGroup)){
                        retVal = AccountSalarySettingOnlyCause.SUBFUND;
                    }
                }
            }
        }
        
        return retVal;
        
    }


    /**
     * Sets the parameterService attribute value.
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

}
