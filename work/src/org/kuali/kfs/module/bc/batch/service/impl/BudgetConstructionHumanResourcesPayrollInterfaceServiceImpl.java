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

import org.kuali.module.budget.service.BudgetConstructionHumanResourcesPayrollInterfaceService;
import org.kuali.module.budget.dao.BudgetConstructionHumanResourcesPayrollInterfaceDao;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class BudgetConstructionHumanResourcesPayrollInterfaceServiceImpl implements BudgetConstructionHumanResourcesPayrollInterfaceService {

    private BudgetConstructionHumanResourcesPayrollInterfaceDao budgetConstructionHumanResourcesPayrollInterfaceDao;
    
    
    /**
     * 
     * @see org.kuali.module.budget.service.BudgetConstructionHumanResourcesPayrollInterfaceService#refreshBudgetConstructionPosition(java.lang.Integer, boolean, boolean)
     */
    public void refreshBudgetConstructionPosition(Integer baseYear, boolean positionSynchOK, boolean CSFUpdateOK) {
     /**
      *  base year positions are built only if current payroll information is still flowing into budget construction
      *  otherwise, the base year positions are frozen
      */
        Integer requestYear = baseYear+1;
        if (positionSynchOK && CSFUpdateOK)
        {
            budgetConstructionHumanResourcesPayrollInterfaceDao.buildBudgetConstructionPositionBaseYear(baseYear);
        }
        /**
         *  request year positions are updated as long as human resources information is still flowing into budget construction
         */
        if (positionSynchOK)
        {
            budgetConstructionHumanResourcesPayrollInterfaceDao.buildBudgetConstructionPositonRequestYear(requestYear);
            budgetConstructionHumanResourcesPayrollInterfaceDao.buildBudgetConstructionAdministrativePosts();
        }
    }

    /**
     * 
     * @see org.kuali.module.budget.service.BudgetConstructionHumanResourcesPayrollInterfaceService#refreshBudgetConstructionIntendedIncumbent(java.lang.Integer, boolean, boolean)
     */
    public void refreshBudgetConstructionIntendedIncumbent(Integer baseYear, boolean positionSynchOK, boolean CSFUpdateOK) {
        Integer requestYear = baseYear+1;
        /**
         *  the intended incumbent table is updates when human resources information is still flowing into budget construction
         */
        if (positionSynchOK)
        {
            budgetConstructionHumanResourcesPayrollInterfaceDao.buildBudgetConstructionIntendedIncumbent(requestYear);
        }
        if (positionSynchOK && CSFUpdateOK)
        {
            budgetConstructionHumanResourcesPayrollInterfaceDao.updateBudgetConstructionIntendedIncumbentAttributes(requestYear);
        }

    }

    public void setBudgetConstructionHumanResourcesPayrollInterfaceDao (BudgetConstructionHumanResourcesPayrollInterfaceDao budgetConstructionHumanResourcesPayrollInterfaceDao)
    {
        this.budgetConstructionHumanResourcesPayrollInterfaceDao = budgetConstructionHumanResourcesPayrollInterfaceDao;
    }
    
}
