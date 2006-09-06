/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.kra.budget.web.struts.action;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.Constants;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.kra.budget.bo.BudgetModular;
import org.kuali.module.kra.budget.rules.event.EnterModularEvent;
import org.kuali.module.kra.budget.rules.event.SaveModularEvent;
import org.kuali.module.kra.budget.web.struts.form.BudgetForm;

/**
 * This class handles Actions for the Budget Modular page.
 * 
 * @author KRA (kualidev@oncourse.iu.edu)
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
        boolean rulePassed = SpringServiceLocator.getKualiRuleService().applyRules(new SaveModularEvent(budgetForm.getDocument()));

        ActionForward superForward = mapping.findForward(Constants.MAPPING_BASIC);
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
        return mapping.findForward(Constants.MAPPING_BASIC);
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

        return mapping.findForward(Constants.MAPPING_BASIC);
    }
}
