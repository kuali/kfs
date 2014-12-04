/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.bc.document.validation.event;

import org.kuali.kfs.module.bc.document.BudgetConstructionDocument;
import org.kuali.kfs.module.bc.document.validation.AddBudgetConstructionDocumentRule;
import org.kuali.kfs.module.bc.document.validation.AddPendingBudgetGeneralLedgerLineRule;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.rules.rule.BusinessRule;
import org.kuali.rice.krad.rules.rule.event.KualiDocumentEventBase;

/**
 * Defines the add (create) Budget Construction document event for the BC Selection screen.
 */
public class AddBudgetConstructionDocumentEvent extends KualiDocumentEventBase {
    private BudgetConstructionDocument budgetConstructionDocument;
    
    /**
     * Constructs a AddBudgetConstructionDocumentEvent.java.
     * @param errorPathPrefix
     * @param document
     */
    public AddBudgetConstructionDocumentEvent(String errorPathPrefix, Document document){
        super("", errorPathPrefix, document);
        this.budgetConstructionDocument = (BudgetConstructionDocument) document;
        
    }

    /**
     * @see org.kuali.rice.krad.rule.event.KualiDocumentEvent#getRuleInterfaceClass()
     */
    public Class getRuleInterfaceClass() {
        return AddPendingBudgetGeneralLedgerLineRule.class;
    }

    /**
     * @see org.kuali.rice.krad.rule.event.KualiDocumentEvent#invokeRuleMethod(org.kuali.rice.krad.rule.BusinessRule)
     */
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((AddBudgetConstructionDocumentRule) rule).processAddBudgetConstructionDocumentRules(budgetConstructionDocument);
    }

    /**
     * Gets the budgetConstructionDocument attribute. 
     * @return Returns the budgetConstructionDocument.
     */
    public BudgetConstructionDocument getBudgetConstructionDocument() {
        return budgetConstructionDocument;
    }

    /**
     * Sets the budgetConstructionDocument attribute value.
     * @param budgetConstructionDocument The budgetConstructionDocument to set.
     */
    public void setBudgetConstructionDocument(BudgetConstructionDocument budgetConstructionDocument) {
        this.budgetConstructionDocument = budgetConstructionDocument;
    }

}
