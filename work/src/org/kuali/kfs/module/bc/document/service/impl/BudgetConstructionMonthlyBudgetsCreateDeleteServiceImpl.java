/*
 * Copyright 2008 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.bc.document.service.impl;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.kuali.kfs.module.bc.document.dataaccess.BudgetConstructionMonthlyBudgetsCreateDeleteDao;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionMonthlyBudgetsCreateDeleteService;
import org.kuali.kfs.module.bc.util.BudgetConstructionUtils;
import org.kuali.rice.krad.service.PersistenceService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @see org.kuali.module.budget.service.BudgetConctructionMonthlyBudgetsCreateDeleteService
 */

@Transactional
public class BudgetConstructionMonthlyBudgetsCreateDeleteServiceImpl implements BudgetConstructionMonthlyBudgetsCreateDeleteService {
    private static Logger LOG = org.apache.log4j.Logger.getLogger(BudgetConstructionMonthlyBudgetsCreateDeleteService.class);


    protected BudgetConstructionMonthlyBudgetsCreateDeleteDao budgetConstructionMonthlyBudgetsCreateDeleteDao;
    protected PersistenceService persistenceServiceOjb; 

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetConstructionMonthlyBudgetsCreateDeleteService#deleteBudgetConstructionMonthlyBudgetsRevenue(java.lang.String,
     *      java.lang.Integer, java.lang.String, java.lang.String, java.lang.String)
     */
    public void deleteBudgetConstructionMonthlyBudgetsRevenue(String documentNumber, Integer fiscalYear, String chartCode, String accountNumber, String subAccountNumber) throws IOException, NoSuchFieldException {
        String revenueINList = BudgetConstructionUtils.getRevenueINList();
        budgetConstructionMonthlyBudgetsCreateDeleteDao.deleteBudgetConstructionMonthlyBudgetsRevenue(documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber, revenueINList);

        // force OJB to go to DB since it is populated using JDBC
        persistenceServiceOjb.clearCache();
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetConstructionMonthlyBudgetsCreateDeleteService#deleteBudgetConstructionMonthlyBudgetsExpenditure(java.lang.String,
     *      java.lang.Integer, java.lang.String, java.lang.String, java.lang.String)
     */
    public void deleteBudgetConstructionMonthlyBudgetsExpenditure(String documentNumber, Integer fiscalYear, String chartCode, String accountNumber, String subAccountNumber) throws IOException, NoSuchFieldException {
        String expenditureINList = BudgetConstructionUtils.getExpenditureINList();
        budgetConstructionMonthlyBudgetsCreateDeleteDao.deleteBudgetConstructionMonthlyBudgetsExpenditure(documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber, expenditureINList);

        // force OJB to go to DB since it is populated using JDBC
        persistenceServiceOjb.clearCache();
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetConstructionMonthlyBudgetsCreateDeleteService#spreadBudgetConstructionMonthlyBudgetsRevenue(java.lang.String,
     *      java.lang.Integer, java.lang.String, java.lang.String, java.lang.String)
     */
    public void spreadBudgetConstructionMonthlyBudgetsRevenue(String documentNumber, Integer fiscalYear, String chartCode, String accountNumber, String subAccountNumber) throws IOException, NoSuchFieldException {
        String revenueINList = BudgetConstructionUtils.getRevenueINList();
        budgetConstructionMonthlyBudgetsCreateDeleteDao.spreadBudgetConstructionMonthlyBudgetsRevenue(documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber, revenueINList);

        // force OJB to go to DB since it is populated using JDBC
        persistenceServiceOjb.clearCache();
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetConstructionMonthlyBudgetsCreateDeleteService#spreadBudgetConstructionMonthlyBudgetsExpenditure(java.lang.String,
     *      java.lang.Integer, java.lang.String, java.lang.String, java.lang.String)
     */
    public boolean spreadBudgetConstructionMonthlyBudgetsExpenditure(String documentNumber, Integer fiscalYear, String chartCode, String accountNumber, String subAccountNumber) throws IOException, NoSuchFieldException {
        String expenditureINList = BudgetConstructionUtils.getExpenditureINList();
        boolean retVal = (budgetConstructionMonthlyBudgetsCreateDeleteDao.spreadBudgetConstructionMonthlyBudgetsExpenditure(documentNumber, fiscalYear, chartCode, accountNumber, subAccountNumber, expenditureINList));

        // force OJB to go to DB since it is populated using JDBC
        persistenceServiceOjb.clearCache();
        
        return retVal;
    }

    /**
     * set method for the Dao
     * 
     * @param budgetConstructionMonthlyBudgetsCreateDeleteDao
     */
    public void setBudgetConstructionMonthlyBudgetsCreateDeleteDao(BudgetConstructionMonthlyBudgetsCreateDeleteDao budgetConstructionMonthlyBudgetsCreateDeleteDao) {
        this.budgetConstructionMonthlyBudgetsCreateDeleteDao = budgetConstructionMonthlyBudgetsCreateDeleteDao;
    }

    /**
     * Gets the persistenceServiceOjb attribute.
     * 
     * @return Returns the persistenceServiceOjb
     */
    
    public PersistenceService getPersistenceServiceOjb() {
        return persistenceServiceOjb;
    }

    /**	
     * Sets the persistenceServiceOjb attribute.
     * 
     * @param persistenceServiceOjb The persistenceServiceOjb to set.
     */
    public void setPersistenceServiceOjb(PersistenceService persistenceServiceOjb) {
        this.persistenceServiceOjb = persistenceServiceOjb;
    }
}
