/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.financial.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.financial.bo.FiscalYearFunctionControl;
import org.kuali.module.financial.service.FiscalYearFunctionControlService;

/**
 * This class implements FiscalYearFunctionControlService.
 * 
 * 
 */
public class FiscalYearFunctionControlServiceImpl implements FiscalYearFunctionControlService {

    public static String FY_FUNCTION_CONTROL_BA_ALLOWED = "BAACTV";
    public static String FY_FUNCTION_CONTROL_BASE_AMT_ALLOWED = "BASEAD";

    private BusinessObjectService businessObjectService;

    /**
     * Retrieves the FiscalYearFunctionControl by its composite primary key (all passed in as parameters) and returns the active
     * indicator.
     * 
     * @param postingYear
     * @param financialSystemFunctionControlCode
     * @return boolean. Returns the value of the active indicator; returns null if PK is not found
     */
    private boolean getActiveIndByPrimaryId(Integer postingYear, String financialSystemFunctionControlCode) {
        HashMap keys = new HashMap();
        keys.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, postingYear);
        keys.put(KFSPropertyConstants.FINANCIAL_SYSTEM_FUNCTION_CONTROL_CODE, financialSystemFunctionControlCode);
        FiscalYearFunctionControl control = (FiscalYearFunctionControl) businessObjectService.findByPrimaryKey(FiscalYearFunctionControl.class, keys);
        return (control != null) && control.isFinancialSystemFunctionActiveIndicator();
    }

    /**
     * Retrieves list of FiscalYearFunctionControls by its function control code.
     * 
     * @param financialSystemFunctionControlCode
     * @return List. Returns the list of FiscalYearFunctionControls
     */
    private List getByFunctionControlCodeAndActiveInd(String financialSystemFunctionControlCode, String financialSystemFunctionActiveIndicator) {
        HashMap values = new HashMap();
        values.put(KFSPropertyConstants.FINANCIAL_SYSTEM_FUNCTION_CONTROL_CODE, financialSystemFunctionControlCode);
        values.put(KFSPropertyConstants.FINANCIAL_SYSTEM_FUNCTION_ACTIVE_INDICATOR, financialSystemFunctionActiveIndicator);
        Collection controls = businessObjectService.findMatching(FiscalYearFunctionControl.class, values);
        return new ArrayList(controls);
    }

    /**
     * @see FiscalYearFunctionControlService#getBudgetAdjustmentAllowedYears(String)
     */
    public List getBudgetAdjustmentAllowedYears() {
        return getByFunctionControlCodeAndActiveInd(FY_FUNCTION_CONTROL_BA_ALLOWED, "Y");
    }

    /**
     * @see FiscalYearFunctionControlService#isBaseAmountChangeAllowed(Integer, String)
     */
    public boolean isBaseAmountChangeAllowed(Integer postingYear) {
        return getActiveIndByPrimaryId(postingYear, FY_FUNCTION_CONTROL_BASE_AMT_ALLOWED);
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
