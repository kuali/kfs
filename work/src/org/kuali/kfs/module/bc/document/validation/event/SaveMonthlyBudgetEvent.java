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

import org.kuali.kfs.module.bc.businessobject.BudgetConstructionMonthly;
import org.kuali.kfs.module.bc.document.BudgetConstructionDocument;
import org.kuali.kfs.module.bc.document.validation.SaveMonthlyBudgetRule;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.rules.rule.BusinessRule;
import org.kuali.rice.krad.rules.rule.event.KualiDocumentEventBase;

/**
 * Defines the save monthly budget event for the monthly budget screen.
 */
public class SaveMonthlyBudgetEvent extends KualiDocumentEventBase {
    
    private BudgetConstructionDocument budgetConstructionDocument;
    private BudgetConstructionMonthly budgetConstructionMonthly;

    public SaveMonthlyBudgetEvent (String errorPathPrefix, Document document, BudgetConstructionMonthly budgetConstructionMonthly){
        super("", errorPathPrefix, document);
        this.budgetConstructionDocument = (BudgetConstructionDocument) document;
        this.budgetConstructionMonthly = budgetConstructionMonthly;
    }

    /**
     * @see org.kuali.rice.krad.rule.event.KualiDocumentEvent#getRuleInterfaceClass()
     */
    public Class getRuleInterfaceClass() {

        return SaveMonthlyBudgetRule.class;
    }

    /**
     * @see org.kuali.rice.krad.rule.event.KualiDocumentEvent#invokeRuleMethod(org.kuali.rice.krad.rule.BusinessRule)
     */
    public boolean invokeRuleMethod(BusinessRule rule) {

        return ((SaveMonthlyBudgetRule<BudgetConstructionDocument, BudgetConstructionMonthly>) rule).processSaveMonthlyBudgetRules(budgetConstructionDocument, budgetConstructionMonthly);
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

    /**
     * Gets the budgetConstructionMonthly attribute. 
     * @return Returns the budgetConstructionMonthly.
     */
    public BudgetConstructionMonthly getBudgetConstructionMonthly() {
        return budgetConstructionMonthly;
    }

    /**
     * Sets the budgetConstructionMonthly attribute value.
     * @param budgetConstructionMonthly The budgetConstructionMonthly to set.
     */
    public void setBudgetConstructionMonthly(BudgetConstructionMonthly budgetConstructionMonthly) {
        this.budgetConstructionMonthly = budgetConstructionMonthly;
    }

}
