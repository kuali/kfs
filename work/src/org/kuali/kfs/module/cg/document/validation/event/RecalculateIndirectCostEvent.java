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
package org.kuali.module.kra.budget.rules.event;

import org.kuali.core.document.Document;
import org.kuali.core.rule.BusinessRule;
import org.kuali.core.rule.event.KualiDocumentEvent;
import org.kuali.core.rule.event.KualiDocumentEventBase;
import org.kuali.module.kra.budget.rules.budget.BudgetDocumentRule;

/**
 * Represents a document event.
 */
public class RecalculateIndirectCostEvent extends KualiDocumentEventBase implements KualiDocumentEvent {

    /**
     * Constructs a RecalculateIndirectCostEvent with the given errorPathPrefix, document.
     * 
     * @param errorPathPrefix
     * @param document
     * @param accountingLine
     */
    public RecalculateIndirectCostEvent(String errorPathPrefix, Document document) {
        super("Running recalculate indirect cost business rules on " + getDocumentId(document), errorPathPrefix, document);
    }

    /**
     * Constructs a RecalculateIndirectCostEvent for the given document
     * 
     * @param document
     * @param accountingLine
     */
    public RecalculateIndirectCostEvent(Document document) {
        this("document.budget.indirectCost", document);
    }

    /**
     * @see org.kuali.core.rule.event.KualiDocumentEvent#getRuleInterfaceClass()
     */
    public Class getRuleInterfaceClass() {
        return BudgetDocumentRule.class;
    }

    /**
     * @see org.kuali.core.rule.event.KualiDocumentEvent#invokeRuleMethod(org.kuali.core.rule.BusinessRule)
     */
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((BudgetDocumentRule) rule).processRecalculateIndirectCostBusinessRules(getDocument());
    }
}
