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
import org.kuali.module.kra.budget.web.struts.form.BudgetForm;

/**
 * Action for BudgetNonpersonnelCopyOver page.
 */
public class BudgetNonpersonnelCopyOverAction extends BudgetAction {

    public ActionForward update(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetForm budgetForm = (BudgetForm) form;

        // ensures that origin item checkboxes and totals are displaied correctly
        budgetForm.getBudgetNonpersonnelCopyOverFormHelper().refresh(budgetForm.getBudgetDocument().getBudget().getPeriods().size());

        return super.update(mapping, form, request, response);
    }

    /**
     * Handling the return to nonpersonnel page. It deconstructs the BudgetNonpersonnelCopyOverFormHelper and resets the tab states.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward returnNonpersonnel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetForm budgetForm = (BudgetForm) form;

        // deconstruct BudgetNonpersonnelCopyOverFormHelper
        budgetForm.getBudgetNonpersonnelCopyOverFormHelper().deconstruct(budgetForm);

        // This is so that tab states are not shared between pages.
        budgetForm.newTabState(false, false);

        return mapping.findForward("nonpersonnel");
    }

    /**
     * Handling the return to nonpersonnel page. It only resets the tab states.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward cancel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetForm budgetForm = (BudgetForm) form;

        // user cancelled, so no deconstruction necessary because data is junked

        // This is so that tab states are not shared between pages.
        budgetForm.newTabState(false, false);

        return mapping.findForward("nonpersonnel");
    }
}
