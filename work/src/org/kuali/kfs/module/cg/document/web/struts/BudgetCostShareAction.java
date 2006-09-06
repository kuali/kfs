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
package org.kuali.module.kra.budget.web.struts.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.Constants;
import org.kuali.module.kra.budget.bo.Budget;
import org.kuali.module.kra.budget.bo.BudgetPeriod;
import org.kuali.module.kra.budget.bo.BudgetThirdPartyCostShare;
import org.kuali.module.kra.budget.bo.BudgetUniversityCostShare;
import org.kuali.module.kra.budget.web.struts.form.BudgetCostShareFormHelper;
import org.kuali.module.kra.budget.web.struts.form.BudgetForm;

/**
 * Action for BudgetCostShare page.
 * 
 * @author Kuali Research Administration Team (kualidev@oncourse.iu.edu)
 */
public class BudgetCostShareAction extends BudgetAction {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetParametersAction.class);

    /**
     * Data preparation for Cost Share page.
     * 
     * @see org.apache.struts.action.Action#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm,
     *      javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetForm budgetForm = (BudgetForm) form;
        Budget budget = budgetForm.getBudgetDocument().getBudget();

        // Partial re-load to avoid lots of hidden variables.
        budget.refreshReferenceObject("personnel");
        budget.refreshReferenceObject("nonpersonnelItems");
        budget.refreshReferenceObject("indirectCost");
        
        // super.execute has to be called before re-creating BudgetCostShareFormHelper because the super call may
        // completly reload data such as for example for the reload button
        ActionForward forward = super.execute(mapping, form, request, response);
        
        budgetForm.setBudgetCostShareFormHelper(new BudgetCostShareFormHelper(budgetForm));

        return forward;
    }

    /**
     * Recalculate button.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward recalculate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        // execute method already does this work.

        return super.update(mapping, form, request, response);
    }

    /**
     * Insert University Cost Share button.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward insertUniversityCostShareDirect(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetForm budgetForm = (BudgetForm) form;

        // Add the new university cost share item and create a new one
        budgetForm.getBudgetDocument().addUniversityCostShare(budgetForm.getBudgetDocument().getBudget().getPeriods(), budgetForm.getNewUniversityCostShare());
        budgetForm.setNewUniversityCostShare(new BudgetUniversityCostShare());

        // Make sure new values are taken into account for calculations.
        budgetForm.setBudgetCostShareFormHelper(new BudgetCostShareFormHelper(budgetForm));

        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     * Delete University Cost Share button.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward deleteUniversityCostShareDirect(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetForm budgetForm = (BudgetForm) form;

        budgetForm.getBudgetDocument().getBudget().getUniversityCostShareItems().remove(getLineToDelete(request));

        // Make sure new values are taken into account for calculations.
        budgetForm.setBudgetCostShareFormHelper(new BudgetCostShareFormHelper(budgetForm));

        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     * Insert Third Party Cost Share button.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward insertThirdPartyCostShareDirect(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetForm budgetForm = (BudgetForm) form;

        // Add the new third party cost share item and create a new one
        budgetForm.getBudgetDocument().addThirdPartyCostShare(budgetForm.getBudgetDocument().getBudget().getPeriods(), budgetForm.getNewThirdPartyCostShare());
        budgetForm.setNewThirdPartyCostShare(new BudgetThirdPartyCostShare());

        // Make sure new values are taken into account for calculations.
        budgetForm.setBudgetCostShareFormHelper(new BudgetCostShareFormHelper(budgetForm));

        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     * Delete Third Party Cost Share button.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward deleteThirdPartyCostShare(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetForm budgetForm = (BudgetForm) form;

        budgetForm.getBudgetDocument().getBudget().getThirdPartyCostShareItems().remove(getLineToDelete(request));

        // Make sure new values are taken into account for calculations.
        budgetForm.setBudgetCostShareFormHelper(new BudgetCostShareFormHelper(budgetForm));

        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /**
     * Save button.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward saveBudgetCostShare(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetForm budgetForm = (BudgetForm) form;
        Budget budget = budgetForm.getBudgetDocument().getBudget();
        List<BudgetPeriod> periods = budgetForm.getBudgetDocument().getBudget().getPeriods();

        Integer universityCostShareNextSequenceNumber = budgetForm.getBudgetDocument().getUniversityCostShareNextSequenceNumber();
        Integer thirdPartyCostShareNextSequenceNumber = budgetForm.getBudgetDocument().getThirdPartyCostShareNextSequenceNumber();
        List universityCostShare = new ArrayList(budget.getUniversityCostShareItems());
        List universityCostSharePersonnel = new ArrayList(budget.getUniversityCostSharePersonnelItems());
        List thirdPartyCostShare = new ArrayList(budget.getThirdPartyCostShareItems());

        this.load(mapping, form, request, response);

        // Only set the objects that potentially changed. The interface doesn't keep hiddens for fields that arn't used. Careful
        // not to use "budget" variable above, that doesn't effect budgetForm.
        if (budget.isUniversityCostShareIndicator()) {
            budgetForm.getBudgetDocument().setUniversityCostShareNextSequenceNumber(universityCostShareNextSequenceNumber);
            budgetForm.getBudgetDocument().getBudget().setUniversityCostShareItems(universityCostShare);
            budgetForm.getBudgetDocument().getBudget().setUniversityCostSharePersonnelItems(universityCostSharePersonnel);
        }
        if (budget.isBudgetThirdPartyCostShareIndicator()) {
            budgetForm.getBudgetDocument().setThirdPartyCostShareNextSequenceNumber(thirdPartyCostShareNextSequenceNumber);
            budgetForm.getBudgetDocument().getBudget().setThirdPartyCostShareItems(thirdPartyCostShare);
        }

        return super.save(mapping, form, request, response);
    }
}
