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

import org.springframework.transaction.annotation.Transactional;

import org.apache.log4j.Logger;

import org.kuali.module.budget.dao.BudgetConstructionMonthlyBudgetsCreateDeleteDao;
import org.kuali.module.budget.service.BudgetConstructionMonthlyBudgetsCreateDeleteService;

@Transactional
public class BudgetConstructionMonthlyBudgetsCreateDeleteServiceImpl implements BudgetConstructionMonthlyBudgetsCreateDeleteService {

    private static Logger LOG = org.apache.log4j.Logger.getLogger(BudgetConstructionMonthlyBudgetsCreateDeleteService.class);

    
    private BudgetConstructionMonthlyBudgetsCreateDeleteDao budgetConstructionMonthlyBudgetsCreateDeleteDao;
    
    /**
     * 
     * @see org.kuali.module.budget.service.BudgetConstructionMonthlyBudgetsCreateDeleteService#BudgetConstructionMonthlyBudgetsDeleteRevenue(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public void BudgetConstructionMonthlyBudgetsDeleteRevenue(String documentNumber, Integer fiscalYear, String chartCode, String accountNumber, String subAccountNumber) {
        budgetConstructionMonthlyBudgetsCreateDeleteDao.BudgetConstructionMonthlyBudgetsDeleteRevenue(documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber);

    }

    /**
     * 
     * @see org.kuali.module.budget.service.BudgetConstructionMonthlyBudgetsCreateDeleteService#BudgetConstructionMonthlyBudgetsDeleteExpenditure(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public void BudgetConstructionMonthlyBudgetsDeleteExpenditure(String documentNumber, Integer fiscalYear, String chartCode, String accountNumber, String subAccountNumber) {
        budgetConstructionMonthlyBudgetsCreateDeleteDao.BudgetConstructionMonthlyBudgetsDeleteExpenditure(documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber);
    }

    /**
     * 
     * @see org.kuali.module.budget.service.BudgetConstructionMonthlyBudgetsCreateDeleteService#BudgetConstructionMonthlyBudgetsSpreadRevenue(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public boolean BudgetConstructionMonthlyBudgetsSpreadRevenue(String documentNumber, Integer fiscalYear, String chartCode, String accountNumber, String subAccountNumber) {
        budgetConstructionMonthlyBudgetsCreateDeleteDao.BudgetConstructionMonthlyBudgetsSpreadRevenue(documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber);
        return false;
    }

    /**
     * 
     * @see org.kuali.module.budget.service.BudgetConstructionMonthlyBudgetsCreateDeleteService#BudgetConstructionMonthlyBudgetsSpreadExpenditure(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    public boolean BudgetConstructionMonthlyBudgetsSpreadExpenditure(String documentNumber, Integer fiscalYear, String chartCode, String accountNumber, String subAccountNumber) {
        budgetConstructionMonthlyBudgetsCreateDeleteDao.BudgetConstructionMonthlyBudgetsSpreadExpenditure(documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber);
        return (budgetConstructionMonthlyBudgetsCreateDeleteDao.BudgetConstructionMonthlyBudgetContainsBenefitsExpenditure(documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber));
    }

    /**
     * 
     * set method for the Dao
     * @param budgetConstructionMonthlyBudgetsCreateDeleteDao
     */
    public void setBudgetConstructionMonthlyBudgetsCreateDeleteDao(BudgetConstructionMonthlyBudgetsCreateDeleteDao budgetConstructionMonthlyBudgetsCreateDeleteDao)
    {
        this.budgetConstructionMonthlyBudgetsCreateDeleteDao = budgetConstructionMonthlyBudgetsCreateDeleteDao;
    }
    
    /**
     * 
     * @@TODO remove this method
     */
    public void testMethod(String document, Integer fiscalYear, String chartCode, String accountNumber, String subAccountNumber)
    {
        
       LOG.warn("\n\nBudgetConstructionMonthlyBudgetsDeleteExpenditure\n\n"); 
       BudgetConstructionMonthlyBudgetsDeleteExpenditure(document, fiscalYear, chartCode, accountNumber, subAccountNumber);
       LOG.warn("\n\nBudgetConstructionMonthlyBudgetsDeleteRevenue\n\n"); 
       BudgetConstructionMonthlyBudgetsDeleteRevenue(document, fiscalYear, chartCode, accountNumber, subAccountNumber);
       LOG.warn("\n\nBudgetConstructionMonthlyBudgetsSpreadRevenue\n\n"); 
       BudgetConstructionMonthlyBudgetsSpreadRevenue(document, fiscalYear, chartCode, accountNumber, subAccountNumber);
       LOG.warn(String.format("\n\nBudgetConstructionMonthlyBudgetsSpreadExpenditure returned %b for (%s,%d,%s,%s,%s): ",
                               BudgetConstructionMonthlyBudgetsSpreadExpenditure(document, fiscalYear, chartCode, accountNumber, subAccountNumber),
                               document, fiscalYear, chartCode, accountNumber, subAccountNumber));
    }
    

}
