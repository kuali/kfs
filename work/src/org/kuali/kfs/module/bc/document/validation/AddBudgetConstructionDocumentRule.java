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
package org.kuali.kfs.module.bc.document.validation;

import org.kuali.kfs.module.bc.document.BudgetConstructionDocument;

/**
 * Defines a rule for adding (creating) a new Budget Construction Document.
 * This rule is invoked from the Budget Construction Selection screen when the
 * user tries to load a BC document that does not exist. 
 */
public interface AddBudgetConstructionDocumentRule<D extends BudgetConstructionDocument> {
    
    /**
     * Processes all the rules for this event and returns true if all rules passed
     * 
     * @param budgetConstructionDocument
     * @return
     */
    public boolean processAddBudgetConstructionDocumentRules(D budgetConstructionDocument);

}
