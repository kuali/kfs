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

import org.kuali.kfs.module.bc.document.validation.event.BudgetExpansionEvent;
import org.kuali.rice.krad.rules.rule.BusinessRule;

/**
 * Defines rule methods for handling expansion events.
 */
public interface BudgetExpansionRule extends BusinessRule {

    /**
     * Process an expansion event.
     * 
     * @param budgetExpansionEvent <code>event</code> to process
     * @return <code>boolean</code> true if validation was successful, false if errors were encountered
     */
    public boolean processExpansionRule(BudgetExpansionEvent budgetExpansionEvent);
}
