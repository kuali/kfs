/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source$
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
import org.kuali.module.kra.budget.bo.BudgetUser;
import org.kuali.module.kra.budget.rules.budget.BudgetDocumentRule;

/**
 * Class capturing an insert personnel event.
 * 
 * @author Kuali Research Administration Team (kualidev@oncourse.iu.edu)
 */
public class InsertPersonnelEventBase extends KualiDocumentEventBase {

    private final BudgetUser newBudgetUser;

    /**
     * Constructs a UpdateNonpersonnelEventBase.java.
     * 
     * @param description
     * @param errorPathPrefix
     * @param document
     */
    public InsertPersonnelEventBase(String errorPathPrefix, Document document, BudgetUser newBudgetUser) {
        super("adding periodLine to document " + getDocumentId(document), errorPathPrefix, document);

        this.newBudgetUser = newBudgetUser;
    }

    public InsertPersonnelEventBase(Document document, BudgetUser newBudgetUser) {
        this("", document, newBudgetUser);
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
        return ((BudgetDocumentRule) rule).processInsertPersonnelBusinessRules(document, newBudgetUser);
    }
}