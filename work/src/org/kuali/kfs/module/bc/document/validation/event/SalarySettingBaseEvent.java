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
package org.kuali.kfs.module.bc.document.validation.event;

import org.kuali.kfs.module.bc.document.validation.SalarySettingRule;
import org.kuali.rice.krad.document.Document;

/**
 * Base class for salary setting events. 
 */
public abstract class SalarySettingBaseEvent extends BudgetExpansionEvent {
    /**
     * Constructs a SalarySettingBaseEvent.java.
     * @param description the given description
     * @param errorPathPrefix the given error path prefix
     * @param document the specified document
     */ 
    public SalarySettingBaseEvent(String description, String errorPathPrefix, Document document) {
        super(description, errorPathPrefix, document);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.validation.event.BudgetExpansionEvent#getExpansionRuleInterfaceClass()
     */
    @Override
    @SuppressWarnings("unchecked")
    public Class getExpansionRuleInterfaceClass() {
        return SalarySettingRule.class;
    }
    
    /**
     * @see org.kuali.kfs.module.bc.document.validation.event.BudgetExpansionEvent#getRuleInterfaceClass()
     */
    @Override
    @SuppressWarnings("unchecked")
    public Class getRuleInterfaceClass() {
        return this.getExpansionRuleInterfaceClass();
    }
}
