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
