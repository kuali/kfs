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
import org.kuali.core.rule.event.KualiDocumentEventBase;
import org.kuali.core.util.ObjectUtils;
import org.kuali.module.kra.budget.bo.BudgetPeriod;
import org.kuali.module.kra.budget.rules.budget.BudgetDocumentRule;

/**
 * Class capturing an insert period line event.
 */
public final class InsertPeriodLineEventBase extends KualiDocumentEventBase implements InsertPeriodLineEvent {

    private final BudgetPeriod budgetPeriod;

    /**
     * Constructs an InsertPeriodLineEvent with the given errorPathPrefix, document, and accountingLine.
     * 
     * @param errorPathPrefix
     * @param document
     * @param accountingLine
     */
    public InsertPeriodLineEventBase(String errorPathPrefix, Document document, BudgetPeriod period) {
        super("adding periodLine to document " + getDocumentId(document), errorPathPrefix, document);

        this.budgetPeriod = period;
    }

    /**
     * Constructs an AddAccountingLineEvent for the given document and accountingLine
     * 
     * @param document
     * @param accountingLine
     */
    public InsertPeriodLineEventBase(Document document, BudgetPeriod period) {
        this("", document, period);
    }

    public BudgetPeriod getBudgetPeriod() {
        return budgetPeriod;
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
        return ((BudgetDocumentRule) rule).processAddPeriodBusinessRules(getDocument(), getBudgetPeriod());
    }
}