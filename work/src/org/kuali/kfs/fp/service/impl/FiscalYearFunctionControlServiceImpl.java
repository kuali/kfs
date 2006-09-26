/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.financial.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.kuali.PropertyConstants;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.module.financial.bo.FiscalYearFunctionControl;
import org.kuali.module.financial.service.FiscalYearFunctionControlService;

/**
 * This class implements FiscalYearFunctionControlService.
 * 
 * @author Kuali Financial Transactions Team ()
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
        keys.put(PropertyConstants.UNIVERSITY_FISCAL_YEAR, postingYear);
        keys.put(PropertyConstants.FINANCIAL_SYSTEM_FUNCTION_CONTROL_CODE, financialSystemFunctionControlCode);
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
        values.put(PropertyConstants.FINANCIAL_SYSTEM_FUNCTION_CONTROL_CODE, financialSystemFunctionControlCode);
        values.put(PropertyConstants.FINANCIAL_SYSTEM_FUNCTION_ACTIVE_INDICATOR, financialSystemFunctionActiveIndicator);
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
