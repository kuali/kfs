/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.fp.document.service;

import org.kuali.kfs.fp.document.BudgetAdjustmentDocument;

/**
 * This service interface defines methods that a BudgetAdjustmentLaborBenefitService implementation must provide.
 * 
 */
public interface BudgetAdjustmentLaborBenefitsService {

    /**
     * Checks the object codes from the document accounting lines against the labor object code table.
     * 
     * @param budgetDocument The budget document to be analyzed.
     * @return True if any labor object codes were found
     */
    public boolean hasLaborObjectCodes(BudgetAdjustmentDocument budgetDocument);

    /**
     * Generates labor benefit accounting lines for the budget document.
     * 
     * @param budgetDocument The budget document used to generate the labor benefit accounting lines.
     */
    public void generateLaborBenefitsAccountingLines(BudgetAdjustmentDocument budgetDocument);

}
