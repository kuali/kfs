/*
 * Copyright 2006-2007 The Kuali Foundation.
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

import org.kuali.module.kra.budget.bo.BudgetNonpersonnel;

/**
 * This interface defines methods that a service must provide
 */
public interface BudgetNonpersonnelService {
    /**
     * Refreshes the NonpersonnelObjectCode reference objects in the nonpersonnelItems list passed in.
     * 
     * @param nonpersonnelItems of items to have NonpersonnelObjectCode refreshed.
     */
    public void refreshNonpersonnelObjectCode(List nonpersonnelItems);

    /**
     * Finds a BudgetNonpersonnel with a certain sequence number in a provided list of nonpersonnelItems. If the item isn't found,
     * null is returned.
     * 
     * @param budgetNonpersonnelSequenceNumber sequence number of item to be found
     * @param nonpersonnelItems Budget.nonpersonnelItem to look for the item in
     * @return item found or null if it is not found
     */
    public BudgetNonpersonnel findBudgetNonpersonnel(Integer budgetNonpersonnelSequenceNumber, List nonpersonnelItems);

    /**
     * Returns all nonpersonnel categories
     * 
     * @throws Exception
     */
    public List getAllNonpersonnelCategories();
}