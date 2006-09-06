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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.kra.budget.bo.AppointmentType;
import org.kuali.module.kra.budget.bo.BudgetAdHocOrg;
import org.kuali.module.kra.budget.bo.BudgetAdHocPermission;
import org.kuali.module.kra.budget.bo.BudgetFringeRate;
import org.kuali.module.kra.budget.document.BudgetDocument;
import org.kuali.module.kra.budget.service.BudgetFringeRateService;
import org.kuali.module.kra.budget.web.struts.form.BudgetForm;

/**
 * This class handles Actions for the Budget Template page.
 * 
 * @author KRA (era_team@indiana.edu)
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
        budgetForm.setDocId(copyDoc.getFinancialDocumentNumber());

        budgetForm.getBudgetDocument().setCleanseBudgetOnSave(false);
        super.save(mapping, form, request, response);
        
        budgetForm.getBudgetDocument().setCleanseBudgetOnSave(true);
        super.load(mapping, form, request, response);

        return super.parameters(mapping, budgetForm, request, response);
    }
}
