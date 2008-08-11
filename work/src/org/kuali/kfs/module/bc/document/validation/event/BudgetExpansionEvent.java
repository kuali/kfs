/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.bc.document.validation.event;

import org.kuali.core.document.Document;
import org.kuali.core.rule.BusinessRule;
import org.kuali.core.rule.event.KualiDocumentEventBase;
import org.kuali.kfs.module.bc.document.BudgetConstructionDocument;
import org.kuali.kfs.module.bc.document.validation.BudgetExpansionRule;

/**
 * Base class for expansion rule events. 
 */
public abstract class BudgetExpansionEvent extends KualiDocumentEventBase {
    private BudgetConstructionDocument budgetConstructionDocument;

    /**
     * Constructs a BudgetExpansionEvent.java.
     */
    public BudgetExpansionEvent(String description, String errorPathPrefix, Document document) {
        super(description, errorPathPrefix, document);
        this.budgetConstructionDocument = (BudgetConstructionDocument) document;
    }
    
    /**
     * Constructs a BudgetExpansionEvent.java.
     * @param errorPathPrefix
     */
    public BudgetExpansionEvent(String errorPathPrefix) {
        this("", errorPathPrefix, new BudgetConstructionDocument());
    }

    /**
     * @see org.kuali.core.rule.event.KualiDocumentEvent#getRuleInterfaceClass()
     */
    public Class getRuleInterfaceClass() {
        return BudgetExpansionRule.class;
    }

    /**
     * @see org.kuali.core.rule.event.KualiDocumentEvent#invokeRuleMethod(org.kuali.core.rule.BusinessRule)
     */
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((BudgetExpansionRule) rule).processExpansionRule(this);
    }

    public abstract Class getExpansionRuleInterfaceClass();

    public abstract boolean invokeExpansionRuleMethod(BusinessRule rule);

    /**
     * Gets the budgetConstructionDocument attribute.
     * 
     * @return Returns the budgetConstructionDocument.
     */
    public BudgetConstructionDocument getBudgetConstructionDocument() {
        return budgetConstructionDocument;
    }

    /**
     * Sets the budgetConstructionDocument attribute value.
     * 
     * @param budgetConstructionDocument The budgetConstructionDocument to set.
     */
    public void setBudgetConstructionDocument(BudgetConstructionDocument budgetConstructionDocument) {
        this.budgetConstructionDocument = budgetConstructionDocument;
    }

}
