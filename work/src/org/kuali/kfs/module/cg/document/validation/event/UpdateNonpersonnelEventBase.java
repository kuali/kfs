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

import java.util.ArrayList;
import java.util.List;

import org.kuali.core.document.Document;
import org.kuali.core.rule.BusinessRule;
import org.kuali.core.rule.event.KualiDocumentEventBase;
import org.kuali.module.kra.budget.rules.budget.BudgetDocumentRule;

/**
 * Class capturing an update nonpersonnel event.
 */
public class UpdateNonpersonnelEventBase extends KualiDocumentEventBase implements UpdateNonpersonnelEvent {

    private final List nonpersonnelItems;

    /**
     * Constructs a UpdateNonpersonnelEventBase.java.
     * 
     * @param description
     * @param errorPathPrefix
     * @param document
     */
    public UpdateNonpersonnelEventBase(String errorPathPrefix, Document document, List nonpersonnelItems) {
        super("adding periodLine to document " + getDocumentId(document), errorPathPrefix, document);

        // by doing a deep copy, we are ensuring that the business rule class can't update
        // the original object by reference
        this.nonpersonnelItems = new ArrayList(nonpersonnelItems);
    }

    /**
     * Constructs an AddAccountingLineEvent for the given document and accountingLine
     * 
     * @param document
     * @param accountingLine
     */
    public UpdateNonpersonnelEventBase(Document document, List nonpersonnelItems) {
        this("", document, nonpersonnelItems);
    }

    /**
     * @see org.kuali.module.kra.budget.rules.event.UpdateNonpersonnelEvent#getNewNonpersonnelItems()
     */
    public List getNonpersonnelItems() {
        // TODO Auto-generated method stub
        return this.nonpersonnelItems;
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
        return ((BudgetDocumentRule) rule).processUpdateNonpersonnelBusinessRules(getDocument(), getNonpersonnelItems());
    }

}