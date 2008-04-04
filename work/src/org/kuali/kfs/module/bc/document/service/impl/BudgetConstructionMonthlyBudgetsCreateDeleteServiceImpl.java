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
import org.kuali.module.budget.BCConstants;

import java.io.IOException;

/**
 * 
 * @see org.kuali.module.budget.service.BudgetConctructionMonthlyBudgetsCreateDeleteService
 */

@Transactional
public class BudgetConstructionMonthlyBudgetsCreateDeleteServiceImpl implements BudgetConstructionMonthlyBudgetsCreateDeleteService {
    private static Logger LOG = org.apache.log4j.Logger.getLogger(BudgetConstructionMonthlyBudgetsCreateDeleteService.class);

    
    private BudgetConstructionMonthlyBudgetsCreateDeleteDao budgetConstructionMonthlyBudgetsCreateDeleteDao;

    /**
     * 
     * @see org.kuali.module.budget.service.BudgetConstructionMonthlyBudgetsCreateDeleteService#deleteBudgetConstructionMonthlyBudgetsRevenue(java.lang.String, java.lang.Integer, java.lang.String, java.lang.String, java.lang.String)
     */
    public void deleteBudgetConstructionMonthlyBudgetsRevenue(String documentNumber, Integer fiscalYear, String chartCode, String accountNumber, String subAccountNumber) throws IOException, NoSuchFieldException {
        budgetConstructionMonthlyBudgetsCreateDeleteDao.deleteBudgetConstructionMonthlyBudgetsRevenue(documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber);
    }

    /**
     * 
     * @see org.kuali.module.budget.service.BudgetConstructionMonthlyBudgetsCreateDeleteService#deleteBudgetConstructionMonthlyBudgetsExpenditure(java.lang.String, java.lang.Integer, java.lang.String, java.lang.String, java.lang.String)
     */
    public void deleteBudgetConstructionMonthlyBudgetsExpenditure(String documentNumber, Integer fiscalYear, String chartCode, String accountNumber, String subAccountNumber) throws IOException, NoSuchFieldException {
        budgetConstructionMonthlyBudgetsCreateDeleteDao.deleteBudgetConstructionMonthlyBudgetsExpenditure(documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber);
    }

    /**
     * 
     * @see org.kuali.module.budget.service.BudgetConstructionMonthlyBudgetsCreateDeleteService#spreadBudgetConstructionMonthlyBudgetsRevenue(java.lang.String, java.lang.Integer, java.lang.String, java.lang.String, java.lang.String)
     */
    public void spreadBudgetConstructionMonthlyBudgetsRevenue(String documentNumber, Integer fiscalYear, String chartCode, String accountNumber, String subAccountNumber) throws IOException, NoSuchFieldException {
        budgetConstructionMonthlyBudgetsCreateDeleteDao.spreadBudgetConstructionMonthlyBudgetsRevenue(documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber);
    }

    /**
     * 
     * @see org.kuali.module.budget.service.BudgetConstructionMonthlyBudgetsCreateDeleteService#spreadBudgetConstructionMonthlyBudgetsExpenditure(java.lang.String, java.lang.Integer, java.lang.String, java.lang.String, java.lang.String)
     */
    public boolean spreadBudgetConstructionMonthlyBudgetsExpenditure(String documentNumber, Integer fiscalYear, String chartCode, String accountNumber, String subAccountNumber) throws IOException, NoSuchFieldException {
        return (budgetConstructionMonthlyBudgetsCreateDeleteDao.spreadBudgetConstructionMonthlyBudgetsExpenditure(documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber));
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
          LOG.warn("\n\ndeleteBudgetConstructionMonthlyBudgetsExpenditure\n\n");
          budgetConstructionMonthlyBudgetsCreateDeleteDao.deleteBudgetConstructionMonthlyBudgetsExpenditure(document, fiscalYear, chartCode, accountNumber, subAccountNumber);
          LOG.warn("\n\ndeleteBudgetConstructionMonthlyBudgetsRevenue\n\n"); 
          budgetConstructionMonthlyBudgetsCreateDeleteDao.deleteBudgetConstructionMonthlyBudgetsRevenue(document, fiscalYear, chartCode, accountNumber, subAccountNumber);
          LOG.warn("\n\nspreadBudgetConstructionMonthlyBudgetsRevenue\n\n"); 
          budgetConstructionMonthlyBudgetsCreateDeleteDao.spreadBudgetConstructionMonthlyBudgetsRevenue(document, fiscalYear, chartCode, accountNumber, subAccountNumber);
          LOG.warn(String.format("\n\nspreadBudgetConstructionMonthlyBudgetsExpenditure returned %b for (%s,%b,%s,%s,%s): ",
                  budgetConstructionMonthlyBudgetsCreateDeleteDao.spreadBudgetConstructionMonthlyBudgetsExpenditure(document, fiscalYear, chartCode, accountNumber, subAccountNumber),
                               document, fiscalYear, chartCode, accountNumber, subAccountNumber));
    }
    

}
