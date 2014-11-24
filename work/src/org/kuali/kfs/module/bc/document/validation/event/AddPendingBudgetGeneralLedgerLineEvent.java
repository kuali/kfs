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

import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionGeneralLedger;
import org.kuali.kfs.module.bc.document.BudgetConstructionDocument;
import org.kuali.kfs.module.bc.document.validation.AddPendingBudgetGeneralLedgerLineRule;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.rules.rule.BusinessRule;
import org.kuali.rice.krad.rules.rule.event.KualiDocumentEventBase;

/**
 * Defines the add new line event for revenue or expenditure tab on the Budget Construction Document.
 */
public class AddPendingBudgetGeneralLedgerLineEvent extends KualiDocumentEventBase {
    private BudgetConstructionDocument budgetConstructionDocument;
    private PendingBudgetConstructionGeneralLedger pendingBudgetConstructionGeneralLedger;
    private boolean isRevenue;

    /**
     * Constructs a AddPendingBudgetGeneralLedgerLineEvent.java.
     * 
     * @param errorPathPrefix
     * @param document
     * @param pendingBudgetConstructionGeneralLedger
     * @param isRev
     */
    public AddPendingBudgetGeneralLedgerLineEvent(String errorPathPrefix, Document document, PendingBudgetConstructionGeneralLedger pendingBudgetConstructionGeneralLedger, boolean isRev) {
        super("", errorPathPrefix, document);
        this.pendingBudgetConstructionGeneralLedger = pendingBudgetConstructionGeneralLedger;
        this.budgetConstructionDocument = (BudgetConstructionDocument) document;
        this.isRevenue = isRev;
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
        return ((AddPendingBudgetGeneralLedgerLineRule) rule).processAddPendingBudgetGeneralLedgerLineRules(budgetConstructionDocument, pendingBudgetConstructionGeneralLedger, isRevenue);
    }

    /**
     * @see org.kuali.rice.krad.rule.event.KualiDocumentEventBase#validate()
     */
    @Override
    public void validate() {

        super.validate();
    }

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

    /**
     * Gets the pendingBudgetConstructionGeneralLedger attribute.
     * 
     * @return Returns the pendingBudgetConstructionGeneralLedger.
     */
    public PendingBudgetConstructionGeneralLedger getPendingBudgetConstructionGeneralLedger() {
        return pendingBudgetConstructionGeneralLedger;
    }

    /**
     * Sets the pendingBudgetConstructionGeneralLedger attribute value.
     * 
     * @param pendingBudgetConstructionGeneralLedger The pendingBudgetConstructionGeneralLedger to set.
     */
    public void setPendingBudgetConstructionGeneralLedger(PendingBudgetConstructionGeneralLedger pendingBudgetConstructionGeneralLedger) {
        this.pendingBudgetConstructionGeneralLedger = pendingBudgetConstructionGeneralLedger;
    }


}
