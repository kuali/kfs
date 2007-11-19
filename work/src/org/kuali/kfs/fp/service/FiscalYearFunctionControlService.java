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
package org.kuali.module.financial.service;

import java.util.List;

/**
 * 
 * This interface defines methods that a FiscalYearFunctionControl Service must provide.
 * 
 */
public interface FiscalYearFunctionControlService {

    /**
     * Retrieves list of fiscal years that the BudgetAdjustment is allowed in.
     * 
     * @return A list of fiscal years that the current user is authorized to perform a budget adjustment against.
     */
    public List getBudgetAdjustmentAllowedYears();

    /**
     * Checks the fiscal year against the control tables to see if BudgetAdjustment to base amount is allowed.
     * 
     * @param Posting year that the base amount is being changed for.
     * @return True if the current user is authorized to edit the base amount, false otherwise.
     */
    public boolean isBaseAmountChangeAllowed(Integer postingYear);

}
