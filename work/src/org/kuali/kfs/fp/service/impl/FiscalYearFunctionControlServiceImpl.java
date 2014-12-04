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
package org.kuali.kfs.fp.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.fp.businessobject.FiscalYearFunctionControl;
import org.kuali.kfs.fp.service.FiscalYearFunctionControlService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.krad.service.BusinessObjectService;

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
    protected boolean getActiveIndByPrimaryId(Integer postingYear, String financialSystemFunctionControlCode) {
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
    protected List getByFunctionControlCodeAndActiveInd(String financialSystemFunctionControlCode, String financialSystemFunctionActiveIndicator) {
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
     * @see org.kuali.kfs.fp.service.FiscalYearFunctionControlService#getActiveBudgetYear()
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
     * @see org.kuali.kfs.fp.service.FiscalYearFunctionControlService#isApplicationUpdateFromHumanResourcesAllowed(java.lang.Integer)
     */
    public boolean isApplicationUpdateFromHumanResourcesAllowed(Integer universityFiscalYear)
    {
        return getActiveIndByPrimaryId(universityFiscalYear, KFSConstants.BudgetConstructionConstants.BUDGET_ON_LINE_SYNCHRONIZATION_OK);
    }
  
    /**
     * 
     * @see org.kuali.kfs.fp.service.FiscalYearFunctionControlService#isBatchUpdateFromHumanResourcesAllowed(java.lang.Integer)
     */
    public boolean isBatchUpdateFromHumanResourcesAllowed(Integer universityFiscalYear)
    {
        return getActiveIndByPrimaryId(universityFiscalYear, KFSConstants.BudgetConstructionConstants.BUDGET_BATCH_SYNCHRONIZATION_OK);
    }
    
    /**
     * 
     * @see org.kuali.kfs.fp.service.FiscalYearFunctionControlService#isBatchUpdateFromPayrollAllowed(java.lang.Integer)
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
     * @see org.kuali.kfs.fp.service.FiscalYearFunctionControlService#isBudgetGeneralLedgerUpdateAllowed(java.lang.Integer)
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
