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
package org.kuali.module.budget.dao;

public interface BudgetConstructionMonthlyBudgetsCreateDeleteDao {

    /**
     * 
     * remove the existing revenue monthly budgets for this key
     * @param originCode
     * @param documentNumber
     * @param fiscalYear
     * @param chartCode
     * @param accountNumber
     * @param subAccountNumber
     */
      public void BudgetConstructionMonthlyBudgetsDeleteRevenue(String originCode, String documentNumber, String fiscalYear, String chartCode, String accountNumber, String subAccountNumber);
      
     /**
      * 
      * remove the existing expenditure monthly budgets for this key
      * @param originCode
      * @param documentNumber
      * @param fiscalYear
      * @param chartCode
      * @param accountNumber
      * @param subAccountNumber
      */ 
      public void BudgetConstructionMonthlyBudgetsDeleteExpenditure(String originCode, String documentNumber, String fiscalYear, String chartCode, String accountNumber, String subAccountNumber);

      /**
       * 
       * spread the revenue for this key evenly over 12 months, with any remainder mod 12 added to the first month
       * @param originCode
       * @param documentNumber
       * @param fiscalYear
       * @param chartCode
       * @param accountNumber
       * @param subAccountNumber
       */
      public void BudgetConstructionMonthlyBudgetsSpreadRevenue(String originCode, String documentNumber, String fiscalYear, String chartCode, String accountNumber, String subAccountNumber);

      /**
       * 
       * spread the expenditures for this key evenly over 12 months, with any reaminder mod 12 added to the first month 
       * @param originCode
       * @param documentNumber
       * @param fiscalYear
       * @param chartCode
       * @param accountNumber
       * @param subAccountNumber
       */
      public void BudgetConstructionMonthlyBudgetsSpreadExpenditure(String originCode, String documentNumber, String fiscalYear, String chartCode, String accountNumber, String subAccountNumber);
      
      /**
       * 
       * checks to see whether the key contains object codes that fund benefits
       * @param originCode
       * @param documentNumber
       * @param fiscalYear
       * @param chartCode
       * @param accountNumber
       * @param subAccountNumber
       * @return true if benefits are funded by this accounting key, false otherwise
       */
      public boolean BudgetConstructionMonthlyBudgetContainsBenefitsExpenditure(String originCode, String documentNumber, String fiscalYear, String chartCode, String accountNumber, String subAccountNumber);

}
