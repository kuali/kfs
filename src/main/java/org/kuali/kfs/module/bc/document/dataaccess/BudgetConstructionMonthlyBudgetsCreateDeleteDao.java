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
package org.kuali.kfs.module.bc.document.dataaccess;


/**
 * provides the data access methods to distribute a set of budget construction general ledger amounts among tweleve monthly periods
 * in a budget construction monthly budget row with the same key. any rounding errors are added/subtracted from the first monthly
 * period, so the total of the monthly periods equals the original amount distributed
 */
public interface BudgetConstructionMonthlyBudgetsCreateDeleteDao {

    /**
     * remove the existing revenue monthly budgets for this key
     * 
     * @param documentNumber the budget construction document number
     * @param fiscalYear the fiscal year for which the budget is being built
     * @param chartCode
     * @param accountNumber
     * @param subAccountNumber
     * @param revenueINList a SQL IN list containing the budget construction revenue object types
     */
    public void deleteBudgetConstructionMonthlyBudgetsRevenue(String documentNumber, Integer fiscalYear, String chartCode, String accountNumber, String subAccountNumber, String revenueINList);

    /**
     * remove the existing expenditure monthly budgets for this key
     * 
     * @param documentNumber the budget construction document number
     * @param fiscalYear the fiscal year for which the budget is being built
     * @param chartCode
     * @param accountNumber
     * @param subAccountNumber
     * @param expenditureINList a SQL IN list containing the budget construction expenditure object types
     */
    public void deleteBudgetConstructionMonthlyBudgetsExpenditure(String documentNumber, Integer fiscalYear, String chartCode, String accountNumber, String subAccountNumber, String expenditureINList);

    /**
     * spread the revenue for this key evenly over 12 months, with any remainder mod 12 added to the first month
     * 
     * @param documentNumber the budget construction document number
     * @param fiscalYear the fiscal year for which the budget is being built
     * @param chartCode
     * @param accountNumber
     * @param subAccountNumber
     * @param revenueINList a SQL IN list containing the budget construction revenue object types
     */
    public void spreadBudgetConstructionMonthlyBudgetsRevenue(String documentNumber, Integer fiscalYear, String chartCode, String accountNumber, String subAccountNumber, String revenueINList);

    /**
     * spread the expenditures for this key evenly over 12 months, with any reaminder mod 12 added to the first month
     * 
     * @param documentNumber the budget construction document number
     * @param fiscalYear the fiscal year for which the budget is being built
     * @param chartCode
     * @param accountNumber
     * @param subAccountNumber
     * @param expenditureINList a SQL IN list containing the budget construction expenditure object types
     * @return true benefits need to be recomputed, false otherwise
     */
    public boolean spreadBudgetConstructionMonthlyBudgetsExpenditure(String documentNumber, Integer fiscalYear, String chartCode, String accountNumber, String subAccountNumber, String expenditureINList);

}
