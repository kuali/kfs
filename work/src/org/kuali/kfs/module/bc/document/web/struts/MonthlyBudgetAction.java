/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.budget.web.struts.action;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.Constants;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.util.UrlFactory;
import org.kuali.core.web.struts.action.KualiAction;
import org.kuali.module.budget.bo.BudgetConstructionMonthly;
import org.kuali.module.budget.web.struts.form.MonthlyBudgetForm;

public class MonthlyBudgetAction extends KualiAction {

    /**
     * added for testing - remove if not needed
     * @see org.kuali.module.budget.web.struts.action.BudgetConstructionAction#execute(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward forward = super.execute(mapping, form, request, response);

        MonthlyBudgetForm monthlyBudgetForm = (MonthlyBudgetForm) form;
        return forward;
    }
    
    public ActionForward view(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        MonthlyBudgetForm monthlyBudgetForm = (MonthlyBudgetForm) form;
        
        // explicitly use the url parms to get the record from DB 
        // this may eventually be changed to use struts populated form instance fields
        Map fieldValues = new HashMap();
        fieldValues.put("documentNumber", request.getParameter("documentNumber"));
        fieldValues.put("universityFiscalYear", request.getParameter("universityFiscalYear"));
        fieldValues.put("chartOfAccountsCode", request.getParameter("chartOfAccountsCode"));
        fieldValues.put("accountNumber", request.getParameter("accountNumber"));
        fieldValues.put("subAccountNumber", request.getParameter("subAccountNumber"));
        fieldValues.put("financialObjectCode", request.getParameter("financialObjectCode"));
        fieldValues.put("financialSubObjectCode", request.getParameter("financialSubObjectCode"));
        fieldValues.put("financialBalanceTypeCode", request.getParameter("financialBalanceTypeCode"));
        fieldValues.put("financialObjectTypeCode", request.getParameter("financialObjectTypeCode"));
        BudgetConstructionMonthly budgetConstructionMonthly = (BudgetConstructionMonthly) SpringServiceLocator.getBusinessObjectService().findByPrimaryKey(BudgetConstructionMonthly.class,fieldValues);
        if (budgetConstructionMonthly == null){
            budgetConstructionMonthly = new BudgetConstructionMonthly();
            budgetConstructionMonthly.setDocumentNumber(request.getParameter("documentNumber"));
            budgetConstructionMonthly.setUniversityFiscalYear(Integer.parseInt(request.getParameter("universityFiscalYear")));
            budgetConstructionMonthly.setChartOfAccountsCode(request.getParameter("chartOfAccountsCode"));
            budgetConstructionMonthly.setAccountNumber(request.getParameter("accountNumber"));
            budgetConstructionMonthly.setSubAccountNumber(request.getParameter("subAccountNumber"));
            budgetConstructionMonthly.setFinancialObjectCode(request.getParameter("financialObjectCode"));
            budgetConstructionMonthly.setFinancialSubObjectCode(request.getParameter("financialSubObjectCode"));
            budgetConstructionMonthly.setFinancialBalanceTypeCode(request.getParameter("financialBalanceTypeCode"));
            budgetConstructionMonthly.setFinancialObjectTypeCode(request.getParameter("financialObjectTypeCode"));
            budgetConstructionMonthly.refreshReferenceObject("pendingBudgetConstructionGeneralLedger");
        }
        monthlyBudgetForm.setBudgetConstructionMonthly(budgetConstructionMonthly);


        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    public ActionForward returnToDocument(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        MonthlyBudgetForm monthlyBudgetForm = (MonthlyBudgetForm) form;
        BudgetConstructionMonthly budgetConstructionMonthly = monthlyBudgetForm.getBudgetConstructionMonthly();
        
        // TODO validate and store monthly changes

        // setup the return parms for the document and anchor
        Properties parameters = new Properties();
        parameters.put(Constants.DISPATCH_REQUEST_PARAMETER, "refresh");
        parameters.put(Constants.DOC_FORM_KEY, monthlyBudgetForm.getReturnFormKey());
        parameters.put("anchor", monthlyBudgetForm.getReturnAnchor());
        parameters.put(Constants.REFRESH_CALLER, "MonthlyBudget");
        
        String lookupUrl = UrlFactory.parameterizeUrl("/" + "budgetBudgetConstruction.do", parameters);
        return new ActionForward(lookupUrl, true);
    }
    
}
