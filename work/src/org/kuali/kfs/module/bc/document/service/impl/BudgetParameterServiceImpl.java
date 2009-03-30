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
package org.kuali.kfs.module.bc.document.service.impl;

import java.util.List;

import org.kuali.kfs.module.bc.BCParameterKeyConstants;
import org.kuali.kfs.module.bc.BCConstants.AccountSalarySettingOnlyCause;
import org.kuali.kfs.module.bc.document.BudgetConstructionDocument;
import org.kuali.kfs.module.bc.document.service.BudgetParameterService;
import org.kuali.kfs.module.bc.util.BudgetParameterFinder;
import org.kuali.rice.kns.service.ParameterService;

/**
 * See BudgetParameterService. This implements value added methods associated with ParameterService that are specific to the budget
 * module.
 */
public class BudgetParameterServiceImpl implements BudgetParameterService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetParameterService.class);

    private ParameterService parameterService;

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetParameterService#getParameterValues(java.lang.Class, java.lang.String)
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
     * @see org.kuali.kfs.module.bc.document.service.BudgetParameterService#isSalarySettingOnlyAccount(org.kuali.kfs.module.bc.document.BudgetConstructionDocument)
     */
    public AccountSalarySettingOnlyCause isSalarySettingOnlyAccount(BudgetConstructionDocument bcDoc) {
        AccountSalarySettingOnlyCause retVal = AccountSalarySettingOnlyCause.MISSING_PARAM;
        
        List<String> salarySettingFundGroupsParamValues = BudgetParameterFinder.getSalarySettingFundGroups();
        List<String> salarySettingSubFundGroupsParamValues = BudgetParameterFinder.getSalarySettingSubFundGroups();

        if (salarySettingFundGroupsParamValues != null && salarySettingSubFundGroupsParamValues != null) {
            retVal = AccountSalarySettingOnlyCause.NONE;

            // is the account in a salary setting only fund group or sub-fund group, if neither calc benefits
            String fundGroup = bcDoc.getAccount().getSubFundGroup().getFundGroupCode();
            String subfundGroup = bcDoc.getAccount().getSubFundGroup().getSubFundGroupCode();
            if (salarySettingFundGroupsParamValues.contains(fundGroup) && salarySettingSubFundGroupsParamValues.contains(subfundGroup)) {
                retVal = AccountSalarySettingOnlyCause.FUND_AND_SUBFUND;
            }
            else {
                if (salarySettingFundGroupsParamValues.contains(fundGroup)) {
                    retVal = AccountSalarySettingOnlyCause.FUND;
                }
                if (salarySettingSubFundGroupsParamValues.contains(subfundGroup)) {
                    retVal = AccountSalarySettingOnlyCause.SUBFUND;
                }
            }
        }

        return retVal;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetParameterService#getLookupObjectTypes(boolean)
     * this implementation returns a string where the values are separated by the OR symbol. 
     */
    public String getLookupObjectTypes(boolean isRevenue) {

        List<String> objectTypes;
        if (isRevenue) {
            objectTypes = BudgetParameterFinder.getRevenueObjectTypes();
        }
        else {
            objectTypes = BudgetParameterFinder.getExpenditureObjectTypes();
        }
        StringBuffer lookupBuilder = new StringBuffer(150);

        if (objectTypes.isEmpty()) {
            // for an empty list, return an empty string
            return new String("");
        }
        else {
            lookupBuilder.append(objectTypes.get(0));
            for (int idx = 1; idx < objectTypes.size(); idx++) {
                lookupBuilder.append("|");
                lookupBuilder.append(objectTypes.get(idx));
            }
        }
        return lookupBuilder.toString();
    }

    /**
     * Sets the parameterService attribute value.
     * 
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
}
