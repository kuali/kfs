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
package org.kuali.module.kra.budget.service;

import java.util.List;

import org.kuali.core.util.KualiInteger;
import org.kuali.module.cg.bo.Agency;
import org.kuali.module.kra.budget.bo.Budget;

/**
 * This interface defines methods that a BudgetModular service must provide
 */
public interface BudgetModularService {

    /**
     * Populate the derived Modular Budget values based on the given Budget object.
     * 
     * @param Budget budget
     */
    public void generateModularBudget(Budget budget);

    /**
     * Populate the derived Modular Budget values based on the given Budget object and nonpersonnelCategories.
     * 
     * @param Budget budget
     * @param List nonpersonnelCategories
     */
    public void generateModularBudget(Budget budget, List nonpersonnelCategories);

    /**
     * Recalculate certain Modular Budget values based on the given Budget object.
     * 
     * @param Budget budget
     * @param List nonpersonnelCategories
     */
    public void resetModularBudget(Budget budget);

    /**
     * Determine whether the given agency supports modular budgets.
     * 
     * @param Agency agency
     * @return boolean
     */
    public boolean agencySupportsModular(Agency agency);

    /**
     * Determine the maximum total direct cost amount allowed per period for the given agency.
     * 
     * @param Agency agency
     * @return boolean
     */
    public KualiInteger determineBudgetPeriodMaximumAmount(Agency agency);
}
