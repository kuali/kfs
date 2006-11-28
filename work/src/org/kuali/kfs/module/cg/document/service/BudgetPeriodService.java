/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source$
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
package org.kuali.module.kra.budget.service;

import java.util.List;

import org.kuali.module.kra.budget.bo.BudgetPeriod;

/**
 * 
 * This interface defines methods that a BudgetPeriod service must provide
 * 
 * 
 */
public interface BudgetPeriodService {

    public BudgetPeriod getBudgetPeriod(String documentNumber, Integer budgetPeriodSequenceNumber);

    public BudgetPeriod getFirstBudgetPeriod(String documentNumber);

    /**
     * Returns the index (zero based) of a BudgetPeriod per the passed in budgetPeriodSequenceNumber.
     * 
     * @param budgetPeriodSequenceNumber BudgetPeriod to find the index for.
     * @param budgetPeriodList Budget.periods list
     * @return index (zero based). -1 if it isn't found.
     */
    public int getPeriodIndex(Integer budgetPeriodSequenceNumber, List budgetPeriodList);

    /**
     * Finds the range between two BudgetPeriods.
     * 
     * @param budgetPeriodSequenceNumberA First BudgetPeriod to find the range for.
     * @param budgetPeriodSequenceNumberB Second BudgetPeriod to find the range for.
     * @param budgetPeriodList Budget.periods list
     * @return range. -1 if it isn't found.
     */
    public int getPeriodsRange(Integer budgetPeriodSequenceNumberA, Integer budgetPeriodSequenceNumberB, List budgetPeriodList);

    /**
     * Finds a BudgetPeriod with an offset to the budgetPeriodSequenceNumber's BudgetPeriod.
     * 
     * @param budgetPeriodSequenceNumber BudgetPeriod to find the index for.
     * @param offset may be positive or negative
     * @param budgetPeriodList Budget.periods list
     * @return in relation to budgetPeriodSequenceNumber's BudgetPeriod with offset
     */
    public BudgetPeriod getPeriodAfterOffset(Integer budgetPeriodSequenceNumber, int offset, List budgetPeriodList);
}
