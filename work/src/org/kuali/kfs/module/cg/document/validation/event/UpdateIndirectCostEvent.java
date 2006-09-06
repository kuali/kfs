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
 * 
 * @author Kuali Research Administration Team (kualidev@oncourse.iu.edu)
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

        // by doing a deep copy, we are ensuring that the business rule class can't update
        // the original object by reference
        idc = (BudgetIndirectCost) ObjectUtils.deepCopy(budgetIndirectCost);
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
