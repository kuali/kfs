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
import org.kuali.module.kra.budget.rules.event.RunAuditEvent;
import org.kuali.module.kra.budget.web.struts.form.BudgetForm;

/**
 * This class handles Actions for the Budget Audit Mode page.
 * 
 * @author KRA (kualidev@oncourse.iu.edu)
 */
public class BudgetAuditModeAction extends BudgetAction {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetAuditModeAction.class);

    /**
     * Activate audit checks.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @throws Exception
     */
    public ActionForward activate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.load(mapping, form, request, response);

        BudgetForm budgetForm = (BudgetForm) form;
        budgetForm.setAuditActivated(true);

        SpringServiceLocator.getKualiRuleService().applyRules(new RunAuditEvent(budgetForm.getBudgetDocument()));

        return mapping.findForward((Constants.MAPPING_BASIC));
    }

    /**
     * De-activate audit checks.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @throws Exception
     */
    public ActionForward deactivate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ((BudgetForm) form).setAuditActivated(false);
        return mapping.findForward((Constants.MAPPING_BASIC));
    }

    /**
     * Toggles all tabs to open. Overrides KualiAction.showAllTabs b/c we need to load the document to run audit.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward showAllTabs(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (((BudgetForm) form).isAuditActivated()) {
            super.load(mapping, form, request, response);
        }

        return super.showAllTabs(mapping, form, request, response);
    }

    /**
     * Toggles all tabs to closed
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward hideAllTabs(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (((BudgetForm) form).isAuditActivated()) {
            super.load(mapping, form, request, response);
        }

        return super.hideAllTabs(mapping, form, request, response);
    }
}
