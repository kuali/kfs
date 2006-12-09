/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.financial.service;

import org.kuali.module.financial.document.BudgetAdjustmentDocument;

/**
 * Service interface for implementing methods to generate labor benefit budget adjustment accounting lines.
 * 
 * 
 */
public interface BudgetAdjustmentLaborBenefitsService {

    /**
     * Checks the object codes from the document accounting lines against the labor object code table.
     * 
     * @param budgetDocument
     * @return true if any labor object codes were found
     */
    public boolean hasLaborObjectCodes(BudgetAdjustmentDocument budgetDocument);

    /**
     * Generates labor benefit accounting lines for the budget document.
     * 
     * @param budgetDocument
     */
    public void generateLaborBenefitsAccountingLines(BudgetAdjustmentDocument budgetDocument);

}
