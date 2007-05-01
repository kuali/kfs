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
package org.kuali.module.kra.budget.web.struts.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.kra.budget.bo.BudgetIndirectCost;
import org.kuali.module.kra.budget.bo.BudgetIndirectCostLookup;
import org.kuali.module.kra.budget.rules.event.RecalculateIndirectCostEvent;
import org.kuali.module.kra.budget.rules.event.UpdateIndirectCostEvent;
import org.kuali.module.kra.budget.web.struts.form.BudgetForm;
import org.kuali.module.kra.budget.web.struts.form.BudgetIndirectCostFormHelper;

/**
 * This class handles Actions for Research Administration.
 * 
 * 
 */

public class BudgetIndirectCostAction extends BudgetAction {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetIndirectCostAction.class);

    /**
     * Save indirect Cost.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        BudgetForm budgetForm = (BudgetForm) form;
        
        SpringServiceLocator.getBudgetIndirectCostService().refreshIndirectCost(budgetForm.getBudgetDocument());
        budgetForm.setBudgetIndirectCostFormHelper(new BudgetIndirectCostFormHelper(budgetForm));

        // Check to make sure our rules are passed.
        boolean rulePassed = SpringServiceLocator.getKualiRuleService().applyRules(new UpdateIndirectCostEvent(budgetForm.getDocument(), budgetForm.getBudgetDocument().getBudget().getIndirectCost()));

        // If our rule failed, reload the current page.
        if (!rulePassed) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        BudgetIndirectCost indirectCost = new BudgetIndirectCost(budgetForm.getBudgetDocument().getBudget().getIndirectCost());
        indirectCost.setDocumentNumber(budgetForm.getBudgetDocument().getDocumentNumber());

        List<BudgetIndirectCostLookup> budgetIndirectCostLookups = new ArrayList(budgetForm.getBudgetDocument().getBudget().getBudgetIndirectCostLookups());
        
        // we are only saving indirect cost items, so load the doc and
        // set the indirect cost items to the proper ones.
        this.load(mapping, form, request, response);
        budgetForm.getBudgetDocument().getBudget().setIndirectCost(indirectCost);
        budgetForm.getBudgetDocument().getBudget().setBudgetIndirectCostLookups(budgetIndirectCostLookups);
        
        super.save(mapping, form, request, response);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }


    /**
     * Recalculate values based on updated IDC parameters. If there are errors in the new parameters, do not do the recalculations.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return ActionForward
     * @throws Exception
     */
    public ActionForward recalculate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        BudgetForm budgetForm = (BudgetForm) form;

        // Check to make sure our rules are passed.
        boolean rulePassed = SpringServiceLocator.getKualiRuleService().applyRules(new RecalculateIndirectCostEvent(budgetForm.getDocument()));

        // If our rule passed, update all our values. Otherwise, reload the page.
        if (rulePassed) {

            // Make sure our IDC object is properly formed. This will also perform initial calculations for
            // BudgetTaskPeriodIndirectCost objects.
            SpringServiceLocator.getBudgetIndirectCostService().refreshIndirectCost(budgetForm.getBudgetDocument());

            // This will populate task and period totals in HashMaps so they can be pulled in the view.
            budgetForm.setBudgetIndirectCostFormHelper(new BudgetIndirectCostFormHelper(budgetForm));
        }

        return this.update(mapping, form, request, response);
    }
    
    
    public ActionForward reload(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward forward = super.reload(mapping, form, request, response);
        
        BudgetForm budgetForm = (BudgetForm) form;
        SpringServiceLocator.getBudgetIndirectCostService().refreshIndirectCost(budgetForm.getBudgetDocument());
        budgetForm.setBudgetIndirectCostFormHelper(new BudgetIndirectCostFormHelper(budgetForm));
        
        return forward;
    }
}
