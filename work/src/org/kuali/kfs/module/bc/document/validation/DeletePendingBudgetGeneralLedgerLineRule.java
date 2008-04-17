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
package org.kuali.module.budget.rule;

import org.kuali.module.budget.bo.PendingBudgetConstructionGeneralLedger;
import org.kuali.module.budget.document.BudgetConstructionDocument;

/**
 * Defines a rule for deleting PendingBudgetGeneralLedgerLine rows from a Budget Construction Document.
 * Invoked when a user clicks the Delete button on the expenditure or revenue tab existing line row.
 */
public interface DeletePendingBudgetGeneralLedgerLineRule<D extends BudgetConstructionDocument, P extends PendingBudgetConstructionGeneralLedger> {
    
    /**
     * Processes all the rules for this event and returns true if all rules passed
     * 
     * @param budgetConstructionDocument
     * @param pendingBudgetConstructionGeneralLedger
     * @param isRevenue
     * @return
     */
    public boolean processDeletePendingBudgetGeneralLedgerLineRules(D budgetConstructionDocument, P pendingBudgetConstructionGeneralLedger, boolean isRevenue);

}
