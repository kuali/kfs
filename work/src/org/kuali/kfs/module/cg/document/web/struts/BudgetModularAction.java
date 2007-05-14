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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.authorization.AuthorizationConstants;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.kra.budget.bo.BudgetModular;
import org.kuali.module.kra.budget.rules.event.EnterModularEvent;
import org.kuali.module.kra.budget.rules.event.SaveModularEvent;
import org.kuali.module.kra.budget.web.struts.form.BudgetForm;

/**
 * This class handles Actions for the Budget Modular page.
 * 
 * 
 */
public class BudgetModularAction extends BudgetAction {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetModularAction.class);

    /**
     * Save the form, and then reload/recalculate values.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @throws Exception
     */
    public ActionForward saveAndRegenerate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        BudgetForm budgetForm = (BudgetForm) form;

        ActionForward forward = save(mapping, form, request, response);

        SpringServiceLocator.getBudgetModularService().generateModularBudget(budgetForm.getBudgetDocument().getBudget(), budgetForm.getNonpersonnelCategories());

        SpringServiceLocator.getKualiRuleService().applyRules(new EnterModularEvent(budgetForm.getDocument()));

        return forward;
    }

    /**
     * Save the form.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @throws Exception
     */
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        BudgetForm budgetForm = (BudgetForm) form;
        BudgetModular modular = budgetForm.getBudgetDocument().getBudget().getModularBudget();

        this.load(mapping, budgetForm, request, response);
        budgetForm.getBudgetDocument().getBudget().setModularBudget(modular);

        // check business rules
        boolean rulePassed = true;
        budgetForm.populateAuthorizationFields(SpringServiceLocator.getDocumentAuthorizationService().getDocumentAuthorizer(budgetForm.getBudgetDocument()));
        if (!"TRUE".equals(budgetForm.getEditingMode().get(AuthorizationConstants.EditMode.VIEW_ONLY))) {
            rulePassed = SpringServiceLocator.getKualiRuleService().applyRules(new SaveModularEvent(budgetForm.getDocument()));
        }

        ActionForward superForward = mapping.findForward(KFSConstants.MAPPING_BASIC);
        if (rulePassed && !budgetForm.getBudgetDocument().getBudget().getModularBudget().isInvalidMode()) {
            superForward = super.save(mapping, form, request, response);
        }

        return superForward;
    }

    /**
     * Recalculate the form values without saving/reloading.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @throws Exception
     */
    public ActionForward recalculate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetForm budgetForm = (BudgetForm) form;
        BudgetModular modular = budgetForm.getBudgetDocument().getBudget().getModularBudget();

        this.load(mapping, budgetForm, request, response);
        budgetForm.getBudgetDocument().getBudget().setModularBudget(modular);

        SpringServiceLocator.getBudgetModularService().generateModularBudget(budgetForm.getBudgetDocument().getBudget(), budgetForm.getNonpersonnelCategories());
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    /**
     * Reload from the database & recalculate all derived values.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @throws Exception
     */
    public ActionForward reload(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.reload(mapping, form, request, response);
        BudgetForm budgetForm = (BudgetForm) form;

        SpringServiceLocator.getBudgetModularService().generateModularBudget(budgetForm.getBudgetDocument().getBudget(), budgetForm.getNonpersonnelCategories());
        SpringServiceLocator.getKualiRuleService().applyRules(new EnterModularEvent(budgetForm.getDocument()));

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
}
