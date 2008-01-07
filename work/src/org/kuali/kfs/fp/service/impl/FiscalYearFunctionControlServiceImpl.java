/*
 * Copyright 2006-2007 The Kuali Foundation.
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
import java.util.Iterator;
import java.util.List;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.KFSConstants;
import org.kuali.module.financial.bo.FiscalYearFunctionControl;
import org.kuali.module.financial.service.FiscalYearFunctionControlService;

/**
 * 
 * This is the default implementation of the FiscalyearFunctionControlService interface.
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
     * @param postingYear The posting year associated with the fiscal year function control being retrieved.
     * @param financialSystemFunctionControlCode The function control code associated with the fiscal year function control being retrieved.
     * @return Returns the value of the active indicator; returns null if PK is not found
     */
    private boolean getActiveIndByPrimaryId(Integer postingYear, String financialSystemFunctionControlCode) {
        HashMap<String, Object> keys = new HashMap();
        keys.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, postingYear);
        keys.put(KFSPropertyConstants.FINANCIAL_SYSTEM_FUNCTION_CONTROL_CODE, financialSystemFunctionControlCode);
        FiscalYearFunctionControl control = (FiscalYearFunctionControl) businessObjectService.findByPrimaryKey(FiscalYearFunctionControl.class, keys);
        return (control != null) && control.isFinancialSystemFunctionActiveIndicator();
    }

    /**
     * Retrieves list of FiscalYearFunctionControls by its function control code.
     * 
     * @param financialSystemFunctionControlCode The function control code to search by.
     * @param financialSystemFunctionActiveIndicator An active indicator used as a search parameter.
     * @return The list of FiscalYearFunctionControls matching the search criteria provided.
     */
    private List getByFunctionControlCodeAndActiveInd(String financialSystemFunctionControlCode, String financialSystemFunctionActiveIndicator) {
        HashMap values = new HashMap();
        values.put(KFSPropertyConstants.FINANCIAL_SYSTEM_FUNCTION_CONTROL_CODE, financialSystemFunctionControlCode);
        values.put(KFSPropertyConstants.FINANCIAL_SYSTEM_FUNCTION_ACTIVE_INDICATOR, financialSystemFunctionActiveIndicator);
        Collection controls = businessObjectService.findMatching(FiscalYearFunctionControl.class, values);
        return new ArrayList(controls);
    }

    /**
     * Retrieves a collection of FiscalYearFunctionControls allowed for use in a budget adjustment.
     * 
     * @return A collection of FiscalYearFunctionControls representing the years allowed in a budget adjustment.
     * 
     * @see FiscalYearFunctionControlService#getBudgetAdjustmentAllowedYears(String)
     */
    public List getBudgetAdjustmentAllowedYears() {
        return getByFunctionControlCodeAndActiveInd(FY_FUNCTION_CONTROL_BA_ALLOWED, "Y");
    }

    /**
     * This method retrieves the value of the active indicator for a FiscalYearFunctionControl instance for the 
     * given posting year.
     * 
     * @param postingYear The posting year used as a search parameter.
     * @return True if the active indicator for the retrieved FiscalYearFunctionControl value retrieved is true, false otherwise.
     * 
     * @see FiscalYearFunctionControlService#isBaseAmountChangeAllowed(Integer, String)
     */
    public boolean isBaseAmountChangeAllowed(Integer postingYear) {
        return getActiveIndByPrimaryId(postingYear, FY_FUNCTION_CONTROL_BASE_AMT_ALLOWED);
    }
    /**
     * 
     * @see org.kuali.module.financial.service.FiscalYearFunctionControlService#getActiveBudgetYear()
     */
    public List<Integer> getActiveBudgetYear()
    {
        ArrayList<FiscalYearFunctionControl> activeYearObjects = (ArrayList<FiscalYearFunctionControl>) getByFunctionControlCodeAndActiveInd(KFSConstants.BudgetConstructionConstants.BUDGET_CONSTRUCTION_ACTIVE,KFSConstants.ACTIVE_INDICATOR);
        ArrayList<Integer> activeYears = new ArrayList<Integer>(activeYearObjects.size());
        Iterator<FiscalYearFunctionControl> activeYearObjectIterator = activeYearObjects.iterator();
        int nextSlot = 0;
        while (activeYearObjectIterator.hasNext())
        {
            activeYears.add(nextSlot++,activeYearObjectIterator.next().getUniversityFiscalYear());
        }
        return activeYears;
    }


    /**
     * 
     * @see org.kuali.module.financial.service.FiscalYearFunctionControlService#isApplicationUpdateFromHumanResourcesAllowed(java.lang.Integer)
     */
    public boolean isApplicationUpdateFromHumanResourcesAllowed(Integer universityFiscalYear)
    {
        return getActiveIndByPrimaryId(universityFiscalYear, KFSConstants.BudgetConstructionConstants.BUDGET_ON_LINE_SYNCHRONIZATION_OK);
    }
  
    /**
     * 
     * @see org.kuali.module.financial.service.FiscalYearFunctionControlService#isBatchUpdateFromHumanResourcesAllowed(java.lang.Integer)
     */
    public boolean isBatchUpdateFromHumanResourcesAllowed(Integer universityFiscalYear)
    {
        return getActiveIndByPrimaryId(universityFiscalYear, KFSConstants.BudgetConstructionConstants.BUDGET_BATCH_SYNCHRONIZATION_OK);
    }
    
    /**
     * 
     * @see org.kuali.module.financial.service.FiscalYearFunctionControlService#isBatchUpdateFromPayrollAllowed(java.lang.Integer)
     */
    public boolean isBatchUpdateFromPayrollAllowed (Integer universityFiscalYear)
    {
        return getActiveIndByPrimaryId(universityFiscalYear, KFSConstants.BudgetConstructionConstants.CSF_UPDATES_OK);
    }
    

    public boolean isBudgetConstructionActive(Integer universityFiscalYear)
    {
        return getActiveIndByPrimaryId(universityFiscalYear, KFSConstants.BudgetConstructionConstants.BUDGET_CONSTRUCTION_ACTIVE);
    }

    /**
     * 
     * @see org.kuali.module.financial.service.FiscalYearFunctionControlService#isBudgetGeneralLedgerUpdateAllowed(java.lang.Integer)
     */
    public boolean isBudgetGeneralLedgerUpdateAllowed(Integer universityFiscalYear)
    {
        return getActiveIndByPrimaryId(universityFiscalYear, KFSConstants.BudgetConstructionConstants.BASE_BUDGET_UPDATES_OK);
    }
    
    public boolean isBudgetUpdateAllowed(Integer universityFiscalYear)
    {
        return getActiveIndByPrimaryId(universityFiscalYear, KFSConstants.BudgetConstructionConstants.BUDGET_CONSTRUCTION_UPDATES_OK);
    }
    
    /**
     * 
     * Gets the value of the businessObjectService attribute.
     * @return An instance of the businessObjectService attribute.
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * 
     * Sets the businessObjectService attribute.
     * @param businessObjectService The businessObjectService instance to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
