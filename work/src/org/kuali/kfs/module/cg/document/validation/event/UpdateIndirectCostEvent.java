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
/**
 * 
 */
package org.kuali.module.kra.budget.rules.event;

import org.kuali.core.document.Document;
import org.kuali.core.rule.BusinessRule;
import org.kuali.core.rule.event.KualiDocumentEvent;
import org.kuali.core.rule.event.KualiDocumentEventBase;
import org.kuali.core.util.ObjectUtils;
import org.kuali.module.kra.budget.bo.BudgetIndirectCost;
import org.kuali.module.kra.budget.rules.budget.BudgetDocumentRule;

/**
 * Class capturing an update indirect cost event.
 */
public class UpdateIndirectCostEvent extends KualiDocumentEventBase implements KualiDocumentEvent {

    private BudgetIndirectCost idc;

    /**
     * Constructs an event with the given errorPathPrefix, document, and indirectCost object.
     * 
     * @param errorPathPrefix
     * @param document
     * @param accountingLine
     */
    public UpdateIndirectCostEvent(String errorPathPrefix, Document document, BudgetIndirectCost budgetIndirectCost) {
        super("adding indirectCost to document " + getDocumentId(document), errorPathPrefix, document);

        this.idc = budgetIndirectCost;
    }

    /**
     * Constructs an event for the given document and budget.
     * 
     * @param document
     * @param accountingLine
     */
    public UpdateIndirectCostEvent(Document document, BudgetIndirectCost budgetIndirectCost) {
        this("document.budget.indirectCost", document, budgetIndirectCost);
    }

    public BudgetIndirectCost getIndirectCost() {
        return idc;
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
        return ((BudgetDocumentRule) rule).processSaveIndirectCostBusinessRules(idc);
    }
}
