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

import org.kuali.module.kra.budget.bo.BudgetTask;

/**
 * 
 */
public interface BudgetTaskService {

    public BudgetTask getBudgetTask(String documentNumber, Integer budgetTaskSequenceNumber);

    public BudgetTask getFirstBudgetTask(String documentNumber);

    /**
     * Returns the index (zero based) of a BudgetTask per the passed in budgetTaskSequenceNumber.
     * 
     * @param budgetTaskSequenceNumber BudgetTask to find the index for.
     * @param budgetTaskList Budget.tasks list
     * @return index (zero based). -1 if it isn't found.
     */
    public int getTaskIndex(Integer budgetTaskSequenceNumber, List budgetTaskList);
}
