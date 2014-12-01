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
package org.kuali.kfs.module.bc.batch.service.impl;

import org.kuali.kfs.module.bc.batch.dataaccess.BudgetConstructionHumanResourcesPayrollInterfaceDao;
import org.kuali.kfs.module.bc.batch.service.BudgetConstructionHumanResourcesPayrollInterfaceService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class BudgetConstructionHumanResourcesPayrollInterfaceServiceImpl implements BudgetConstructionHumanResourcesPayrollInterfaceService {

    protected BudgetConstructionHumanResourcesPayrollInterfaceDao budgetConstructionHumanResourcesPayrollInterfaceDao;
    
    
    /**
     * 
     * @see org.kuali.kfs.module.bc.batch.service.BudgetConstructionHumanResourcesPayrollInterfaceService#refreshBudgetConstructionPosition(java.lang.Integer, boolean, boolean)
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
     * @see org.kuali.kfs.module.bc.batch.service.BudgetConstructionHumanResourcesPayrollInterfaceService#refreshBudgetConstructionIntendedIncumbent(java.lang.Integer, boolean, boolean)
     */
    public void refreshBudgetConstructionIntendedIncumbent(Integer baseYear, boolean positionSynchOK, boolean CSFUpdateOK, boolean BCUpdatesAllowed) {
        Integer requestYear = baseYear+1;
        /**
         *  the intended incumbent table is updated when human resources information is still flowing into budget construction.
         *  when this is no longer the case, only the names are updated (unless all of budget construction is no longer in update mode).
         */
        if (positionSynchOK)
        {
            if (CSFUpdateOK)
            {
                // we update the faculty level (full, associate, assistant, etc.) only if base payroll information is still flowing into budget construction.
                // otherwise, we assume that the base payroll is "frozen" as a base-line for salary setting, and we stop allowing people to move between faculty levels.
                // this version builds intended incumbent and updates faculty ranks.
                budgetConstructionHumanResourcesPayrollInterfaceDao.buildBudgetConstructionIntendedIncumbentWithFacultyAttributes(requestYear);
            }
            else
            {
                // this version builds intended incumbent without adding anyone to the faculty levels.
                budgetConstructionHumanResourcesPayrollInterfaceDao.buildBudgetConstructionIntendedIncumbent(requestYear);
            }
        }
        else
        {
           // the name is always updated if the budget is in update mode, even if intended incumbent was not rebuilt because position synchronization was off.
            if (BCUpdatesAllowed)
            {
               budgetConstructionHumanResourcesPayrollInterfaceDao.updateNamesInBudgetConstructionIntendedIncumbent();
            }
        }
    }

    public void setBudgetConstructionHumanResourcesPayrollInterfaceDao (BudgetConstructionHumanResourcesPayrollInterfaceDao budgetConstructionHumanResourcesPayrollInterfaceDao)
    {
        this.budgetConstructionHumanResourcesPayrollInterfaceDao = budgetConstructionHumanResourcesPayrollInterfaceDao;
    }
    
}
