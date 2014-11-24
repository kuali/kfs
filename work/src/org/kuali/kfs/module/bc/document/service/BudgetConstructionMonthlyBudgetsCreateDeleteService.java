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
package org.kuali.kfs.module.bc.document.service;

import java.io.IOException;

/**
 * distribute the request amount for a set of budget construction general ledger rows evenly among twelve monthly periods in a
 * budget construction monthly budget row with the same key. if the amount to be spread is not divisible by 12, the adjustment will
 * be added or subtracted from the amount in the first monthly period.
 */

public interface BudgetConstructionMonthlyBudgetsCreateDeleteService {

    /**
     * remove the existing revenue monthly budgets for this key
     * 
     * @param documentNumber the budget construction document number
     * @param fiscalYear the fiscal year for which the budget is being built. this and the fields below are the key to a budget
     *        construction document
     * @param chartCode
     * @param accountNumber
     * @param subAccountNumber
     * @return FAILED if the SQL could not be built, BENEFITS if benefits need to be recalculated, NO_BENEFITS otherwise
     */
    public void deleteBudgetConstructionMonthlyBudgetsRevenue(String documentNumber, Integer fiscalYear, String chartCode, String accountNumber, String subAccountNumber) throws IOException, NoSuchFieldException;

    /**
     * remove the existing expenditure monthly budgets for this key
     * 
     * @param documentNumber the budget construction document number
     * @param fiscalYear the fiscal year for which the budget is being built. this and the fields below are the key to a budget
     *        construction document
     * @param chartCode
     * @param accountNumber
     * @param subAccountNumber
     * @return FAILED if the SQL could not be built, BENEFITS if benefits need to be recalculated, NO_BENEFITS otherwise
     */
    public void deleteBudgetConstructionMonthlyBudgetsExpenditure(String documentNumber, Integer fiscalYear, String chartCode, String accountNumber, String subAccountNumber) throws IOException, NoSuchFieldException;

    /**
     * spread the revenue for this key evenly over 12 months, with any remainder mod 12 added to the first month
     * 
     * @param documentNumber the budget construction document number
     * @param fiscalYear the fiscal year for which the budget is being built. this and the fields below are the key to a budget
     *        construction document
     * @param chartCode
     * @param accountNumber
     * @param subAccountNumber
     * @return FAILED if the SQL could not be built, BENEFITS if benefits need to be recalculated, NO_BENEFITS otherwise
     */
    public void spreadBudgetConstructionMonthlyBudgetsRevenue(String documentNumber, Integer fiscalYear, String chartCode, String accountNumber, String subAccountNumber) throws IOException, NoSuchFieldException;

    /**
     * spread the expenditures for this key evenly over 12 months, with any reaminder mod 12 added to the first month
     * 
     * @param documentNumber the budget construction document number
     * @param fiscalYear the fiscal year for which the budget is being built. this and the fields below are the key to a budget
     *        construction document
     * @param chartCode
     * @param accountNumber
     * @param subAccountNumber
     * @return FAILED if the SQL could not be built, BENEFITS if benefits need to be recalculated, NO_BENEFITS otherwise
     */
    public boolean spreadBudgetConstructionMonthlyBudgetsExpenditure(String documentNumber, Integer fiscalYear, String chartCode, String accountNumber, String subAccountNumber) throws IOException, NoSuchFieldException;

}
