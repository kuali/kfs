/*
 * Copyright 2008 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.bc.document.validation.event;

import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionGeneralLedger;
import org.kuali.kfs.module.bc.document.BudgetConstructionDocument;
import org.kuali.kfs.module.bc.document.validation.DeletePendingBudgetGeneralLedgerLineRule;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.rules.rule.BusinessRule;
import org.kuali.rice.krad.rules.rule.event.KualiDocumentEventBase;

/**
 * Defines the delete line event for revenue or expenditure tab rows on the Budget Construction Document.
 */
public class DeletePendingBudgetGeneralLedgerLineEvent extends KualiDocumentEventBase {

    private BudgetConstructionDocument budgetConstructionDocument;
    private PendingBudgetConstructionGeneralLedger pendingBudgetConstructionGeneralLedger;
    private boolean isRevenue;

    /**
     * Constructs a DeletePendingBudgetGeneralLedgerLineEvent.java.
     * @param description
     * @param errorPathPrefix
     */
    public DeletePendingBudgetGeneralLedgerLineEvent(String errorPathPrefix, Document document, PendingBudgetConstructionGeneralLedger pendingBudgetConstructionGeneralLedger, boolean isRev) {
        super("", errorPathPrefix, document);
        this.pendingBudgetConstructionGeneralLedger = pendingBudgetConstructionGeneralLedger;
        this.budgetConstructionDocument = (BudgetConstructionDocument) document;
        this.isRevenue = isRev;
    }

    /**
     * @see org.kuali.rice.krad.rule.event.KualiDocumentEvent#getRuleInterfaceClass()
     */
    public Class getRuleInterfaceClass() {
        return DeletePendingBudgetGeneralLedgerLineRule.class;
    }

    /**
     * @see org.kuali.rice.krad.rule.event.KualiDocumentEvent#invokeRuleMethod(org.kuali.rice.krad.rule.BusinessRule)
     */
    public boolean invokeRuleMethod(BusinessRule rule) {
        return ((DeletePendingBudgetGeneralLedgerLineRule) rule).processDeletePendingBudgetGeneralLedgerLineRules(budgetConstructionDocument, pendingBudgetConstructionGeneralLedger, isRevenue);
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
     * Gets the isRevenue attribute. 
     * @return Returns the isRevenue.
     */
    public boolean isRevenue() {
        return isRevenue;
    }

    /**
     * Sets the isRevenue attribute value.
     * @param isRevenue The isRevenue to set.
     */
    public void setRevenue(boolean isRevenue) {
        this.isRevenue = isRevenue;
    }

    /**
     * Gets the pendingBudgetConstructionGeneralLedger attribute. 
     * @return Returns the pendingBudgetConstructionGeneralLedger.
     */
    public PendingBudgetConstructionGeneralLedger getPendingBudgetConstructionGeneralLedger() {
        return pendingBudgetConstructionGeneralLedger;
    }

    /**
     * Sets the pendingBudgetConstructionGeneralLedger attribute value.
     * @param pendingBudgetConstructionGeneralLedger The pendingBudgetConstructionGeneralLedger to set.
     */
    public void setPendingBudgetConstructionGeneralLedger(PendingBudgetConstructionGeneralLedger pendingBudgetConstructionGeneralLedger) {
        this.pendingBudgetConstructionGeneralLedger = pendingBudgetConstructionGeneralLedger;
    }

}
