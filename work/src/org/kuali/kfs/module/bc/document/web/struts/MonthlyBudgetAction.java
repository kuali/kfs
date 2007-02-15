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
//        String tacct = (String)request.getAttribute("accountNumber");
//        if (tacct == null){
//            tacct = "9999999";
//        }
//        monthlyBudgetForm.getBudgetConstructionMonthly().setAccountNumber(tacct);
        return forward;
    }
    
    public ActionForward view(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        MonthlyBudgetForm monthlyBudgetForm = (MonthlyBudgetForm) form;
        
        // parse out the important strings from our methodToCall parameter
//        String fullParameter = (String) request.getAttribute(Constants.METHOD_TO_CALL_ATTRIBUTE);
        
        String bcMonthParmsKey = request.getParameter("bcMonthParmsKey");
        Properties parameters = (Properties) GlobalVariables.getUserSession().retrieveObject(bcMonthParmsKey);
        if (parameters == null){
            return mapping.findForward(Constants.MAPPING_PORTAL);
        }
        GlobalVariables.getUserSession().removeObject(bcMonthParmsKey);

        String tacct = parameters.getProperty("accountNumber");
        
        Map fieldValues = new HashMap();
        fieldValues.put("documentNumber", parameters.getProperty("documentNumber"));
        fieldValues.put("universityFiscalYear", parameters.getProperty("universityFiscalYear"));
        fieldValues.put("chartOfAccountsCode", parameters.getProperty("chartOfAccountsCode"));
        fieldValues.put("accountNumber", parameters.getProperty("accountNumber"));
        fieldValues.put("subAccountNumber", parameters.getProperty("subAccountNumber"));
        fieldValues.put("financialObjectCode", parameters.getProperty("financialObjectCode"));
        fieldValues.put("financialSubObjectCode", parameters.getProperty("financialSubObjectCode"));
        fieldValues.put("financialBalanceTypeCode", parameters.getProperty("financialBalanceTypeCode"));
        fieldValues.put("financialObjectTypeCode", parameters.getProperty("financialObjectTypeCode"));
        BudgetConstructionMonthly budgetConstructionMonthly = (BudgetConstructionMonthly) SpringServiceLocator.getBusinessObjectService().findByPrimaryKey(BudgetConstructionMonthly.class,fieldValues);
        if (budgetConstructionMonthly == null){
            budgetConstructionMonthly = new BudgetConstructionMonthly();
            budgetConstructionMonthly.setDocumentNumber(parameters.getProperty("documentNumber"));
            budgetConstructionMonthly.setUniversityFiscalYear(Integer.parseInt(parameters.getProperty("universityFiscalYear")));
            budgetConstructionMonthly.setChartOfAccountsCode(parameters.getProperty("chartOfAccountsCode"));
            budgetConstructionMonthly.setAccountNumber(parameters.getProperty("accountNumber"));
            budgetConstructionMonthly.setSubAccountNumber(parameters.getProperty("subAccountNumber"));
            budgetConstructionMonthly.setFinancialObjectCode(parameters.getProperty("financialObjectCode"));
            budgetConstructionMonthly.setFinancialSubObjectCode(parameters.getProperty("financialSubObjectCode"));
            budgetConstructionMonthly.setFinancialBalanceTypeCode(parameters.getProperty("financialBalanceTypeCode"));
            budgetConstructionMonthly.setFinancialObjectTypeCode(parameters.getProperty("financialObjectTypeCode"));
            budgetConstructionMonthly.refreshReferenceObject("pendingBudgetConstructionGeneralLedger");
        }
        monthlyBudgetForm.setBudgetConstructionMonthly(budgetConstructionMonthly);


//        String tacct = request.getParameter("accountNumber");
//        String tacct = (String)request.getAttribute("accountNumber");
        if (tacct == null){
            tacct = "9999999";
        }
//        monthlyBudgetForm.getBudgetConstructionMonthly().setAccountNumber(tacct);
//        monthlyBudgetForm.getBudgetConstructionMonthly().setDocumentNumber("0101");
        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    public ActionForward returnToDocument(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        MonthlyBudgetForm monthlyBudgetForm = (MonthlyBudgetForm) form;
        BudgetConstructionMonthly budgetConstructionMonthly = monthlyBudgetForm.getBudgetConstructionMonthly(); 

        Properties parameters = new Properties();
        parameters.put("methodToCall", "returnFromMonthly");
        parameters.put("documentNumber", budgetConstructionMonthly.getDocumentNumber());
            
        String lookupUrl = UrlFactory.parameterizeUrl("/" + "budgetBudgetConstruction.do", parameters);
        return new ActionForward(lookupUrl, true);
    }
    
}
