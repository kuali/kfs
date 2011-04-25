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
