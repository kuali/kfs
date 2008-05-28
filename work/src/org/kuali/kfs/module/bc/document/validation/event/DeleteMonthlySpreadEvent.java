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
package org.kuali.module.budget.rule.event;

import org.kuali.core.document.Document;
import org.kuali.core.rule.BusinessRule;
import org.kuali.core.rule.event.KualiDocumentEventBase;
import org.kuali.module.budget.BCConstants.MonthSpreadDeleteType;
import org.kuali.module.budget.document.BudgetConstructionDocument;
import org.kuali.module.budget.rule.DeleteMonthlySpreadRule;
import org.kuali.module.budget.rule.DeletePendingBudgetGeneralLedgerLineRule;

/**
 * This class...
 */
public class DeleteMonthlySpreadEvent extends KualiDocumentEventBase {

    private BudgetConstructionDocument budgetConstructionDocument;
    private MonthSpreadDeleteType monthSpreadDeleteType;
    
    public DeleteMonthlySpreadEvent(Document document, MonthSpreadDeleteType monthSpreadDeleteType){
        super("", "", document);
        this.budgetConstructionDocument = (BudgetConstructionDocument) document;
        this.monthSpreadDeleteType = monthSpreadDeleteType;
    
    }

    /**
     * @see org.kuali.core.rule.event.KualiDocumentEvent#getRuleInterfaceClass()
     */
    public Class getRuleInterfaceClass() {
        // TODO Auto-generated method stub
        return DeleteMonthlySpreadRule.class;
    }

    /**
     * @see org.kuali.core.rule.event.KualiDocumentEvent#invokeRuleMethod(org.kuali.core.rule.BusinessRule)
     */
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((DeleteMonthlySpreadRule) rule).processDeleteMonthlySpreadRules(budgetConstructionDocument, monthSpreadDeleteType);
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
