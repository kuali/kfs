package org.kuali.module.kra.budget.web.struts.action;

/*
 * Copyright (c) 2004, 2005 The National Association of College and University 
 * Business Officers, Cornell University, Trustees of Indiana University, 
 * Michigan State University Board of Trustees, Trustees of San Joaquin Delta 
 * College, University of Hawai'i, The Arizona Board of Regents on behalf of the 
 * University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); 
 * By obtaining, using and/or copying this Original Work, you agree that you 
 * have read, understand, and will comply with the terms and conditions of the 
 * Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,  DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
 */

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.module.kra.budget.web.struts.form.BudgetForm;

/**
 * Action for BudgetNonpersonnelCopyOver page.
 * 
 * @author KRA (era_team@indiana.edu)
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
