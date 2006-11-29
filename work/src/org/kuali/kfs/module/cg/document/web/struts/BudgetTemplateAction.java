/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source$
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.kra.budget.bo.BudgetAdHocOrg;
import org.kuali.module.kra.budget.bo.BudgetAdHocPermission;
import org.kuali.module.kra.budget.document.BudgetDocument;
import org.kuali.module.kra.budget.web.struts.form.BudgetForm;

/**
 * This class handles Actions for the Budget Template page.
 * 
 * 
 */
public class BudgetTemplateAction extends BudgetAction {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetTemplateAction.class);

    /**
     * Template the current document and forward to new document parameters page.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @throws Exception
     */
    public ActionForward doTemplate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        // Make sure BudgetForm is fully populated
        super.load(mapping, form, request, response);

        BudgetForm budgetForm = (BudgetForm) form;
        BudgetDocument budgetDoc = budgetForm.getBudgetDocument();

        BudgetDocument copyDoc = (BudgetDocument) budgetDoc.copy();
        
//      Check if ad-hoc permissions to be copied over
        if (!budgetForm.isIncludeAdHocPermissions()) {
            copyDoc.getBudget().setAdHocPermissions(new ArrayList<BudgetAdHocPermission>());
            copyDoc.getBudget().setAdHocOrgs(new ArrayList<BudgetAdHocOrg>());
        }
        
//      Check if budget fringe rates to be copied over
        if (!budgetForm.isIncludeBudgetIdcRates()) {
            copyDoc.getBudget().getIndirectCost().setBudgetManualRateIndicator("N");
            SpringServiceLocator.getBudgetIndirectCostService().setupIndirectCostRates(copyDoc.getBudget());
        }
        
        budgetForm.setBudgetDocument(copyDoc);
        budgetForm.setDocId(copyDoc.getDocumentNumber());

        budgetForm.getBudgetDocument().setCleanseBudgetOnSave(false);
        super.save(mapping, form, request, response);
        
        budgetForm.getBudgetDocument().setCleanseBudgetOnSave(true);
        super.load(mapping, form, request, response);

        return super.parameters(mapping, budgetForm, request, response);
    }
}
