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
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.kra.budget.rules.event.RunAuditEvent;
import org.kuali.module.kra.budget.web.struts.form.BudgetForm;

/**
 * This class handles Actions for the Budget Audit Mode page.
 * 
 * 
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

        return mapping.findForward((KFSConstants.MAPPING_BASIC));
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
        return mapping.findForward((KFSConstants.MAPPING_BASIC));
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
