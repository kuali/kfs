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
package org.kuali.module.kra.routingform.web.struts.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.kra.KraConstants;
import org.kuali.module.kra.budget.bo.BudgetPeriod;
import org.kuali.module.kra.budget.document.BudgetDocument;
import org.kuali.module.kra.budget.web.struts.form.BudgetIndirectCostFormHelper;
import org.kuali.module.kra.budget.web.struts.form.BudgetOverviewFormHelper;
import org.kuali.module.kra.routingform.rules.event.RoutingFormBudgetLinkEvent;
import org.kuali.module.kra.routingform.service.RoutingFormService;
import org.kuali.module.kra.routingform.web.struts.form.RoutingForm;

import edu.iu.uis.eden.exception.WorkflowException;

public class RoutingFormLinkAction extends RoutingFormAction {
    
    public ActionForward linkBudget(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RoutingForm routingForm = (RoutingForm)form;
    
        List selectedBudgetPeriodsList = new ArrayList();
        List<BudgetOverviewFormHelper> linkedPeriods = new ArrayList();
        Integer i = 0;
        for (BudgetOverviewFormHelper budgetOverviewFormHelper : routingForm.getPeriodBudgetOverviewFormHelpers()) {
            if (budgetOverviewFormHelper.isSelected() || routingForm.getAllPeriodsSelected()) {
                selectedBudgetPeriodsList.add(i.toString());
                linkedPeriods.add(budgetOverviewFormHelper);
            }
            i++;
        }
        routingForm.setAllPeriodsSelected(false);
        
        Collections.sort(selectedBudgetPeriodsList);
        
        String[] selectedBudgetPeriods = (String[])selectedBudgetPeriodsList.toArray(new String[selectedBudgetPeriodsList.size()]);

        boolean isBudgetValidForLink = SpringServiceLocator.getKualiRuleService().applyRules(new RoutingFormBudgetLinkEvent(routingForm.getRoutingFormDocument(), selectedBudgetPeriods, routingForm.getAllPeriodsSelected(), true));
        
        //if no errors, proceed with the link and return to the RF Main Page
        if (isBudgetValidForLink) {
            String budgetDocumentHeaderId = routingForm.getRoutingFormDocument().getRoutingFormBudgetNumber();
            
            //load RF Document data
            super.load(mapping, form, request, response);
            
            routingForm.getRoutingFormDocument().setRoutingFormBudgetNumber(budgetDocumentHeaderId);
            routingForm.getRoutingFormDocument().getRoutingFormBudget().setRoutingFormBudgetMinimumPeriodNumber(linkedPeriods.get(0).getBudgetPeriod().getBudgetPeriodSequenceNumber());
            routingForm.getRoutingFormDocument().getRoutingFormBudget().setRoutingFormBudgetMaximumPeriodNumber(linkedPeriods.get(linkedPeriods.size() - 1).getBudgetPeriod().getBudgetPeriodSequenceNumber());

            
            //service method to link budget data
            SpringServiceLocator.getRoutingFormService().linkImportBudgetDataToRoutingForm(routingForm.getRoutingFormDocument(), routingForm.getRoutingFormDocument().getRoutingFormBudgetNumber(), routingForm.getPeriodBudgetOverviewFormHelpers());

            //save the new budget data into the RF
            super.save(mapping, form, request, response);
            
            
            //check for errors saving
            if (GlobalVariables.getErrorMap().isEmpty()) {
                
                super.load(mapping, form, request, response);
                //return to the main page
                return super.mainpage(mapping, form, request, response);
            }
        }

        //if we're refreshing this page, it's because there was some problem with the data or saving.  re-setup the budget data so the use doesn't have to re-load the budget themselves.
        this.loadBudget(mapping, form, request, response);

        i = 0;
        for (BudgetOverviewFormHelper budgetOverviewFormHelper : routingForm.getPeriodBudgetOverviewFormHelpers()) {
            budgetOverviewFormHelper.setSelected(selectedBudgetPeriodsList.contains(i.toString()));
            i++;
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    public ActionForward loadBudget(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RoutingForm routingForm = (RoutingForm)form;

        if (routingForm.getRoutingFormDocument().getRoutingFormBudgetNumber() != null) {
            boolean isBudgetValidForLink = SpringServiceLocator.getKualiRuleService().applyRules(new RoutingFormBudgetLinkEvent(routingForm.getRoutingFormDocument(), null, false, false));
            if (isBudgetValidForLink) {
                setupBudgetPeriodData(routingForm);
            } else {
                routingForm.setPeriodBudgetOverviewFormHelpers(null);
            }
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward deleteBudget(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RoutingForm routingForm = (RoutingForm)form;

        Integer subcontractorNextSequenceNumber = routingForm.getRoutingFormDocument().getSubcontractorNextSequenceNumber();
        Integer institutionCostShareNextSequenceNumber = routingForm.getRoutingFormDocument().getInstitutionCostShareNextSequenceNumber();
        Integer otherCostShareNextSequenceNumber = routingForm.getRoutingFormDocument().getOtherCostShareNextSequenceNumber();

        
        super.load(mapping, form, request, response);

        routingForm.getRoutingFormDocument().setRoutingFormBudgetNumber(null);
        routingForm.getRoutingFormDocument().getRoutingFormBudget().setRoutingFormBudgetMinimumPeriodNumber(null);
        routingForm.getRoutingFormDocument().getRoutingFormBudget().setRoutingFormBudgetMaximumPeriodNumber(null);

        super.save(mapping, form, request, response);
        
        return super.mainpage(mapping, form, request, response);
    }

    
    /**
     * This method...
     * @param routingForm
     * @throws WorkflowException
     */
    protected void setupBudgetPeriodData(RoutingForm routingForm) throws WorkflowException {
        RoutingFormService routingFormService = SpringServiceLocator.getRoutingFormService();
   
        BudgetDocument budgetDocument = routingFormService.retrieveBudgetForLinking(routingForm.getRoutingFormDocument().getRoutingFormBudgetNumber());
   
        if (budgetDocument != null) {
            SpringServiceLocator.getBudgetIndirectCostService().refreshIndirectCost(budgetDocument);
      
            List allNonpersonnelCategories = SpringServiceLocator.getBudgetNonpersonnelService().getAllNonpersonnelCategories();
      
            BudgetIndirectCostFormHelper budgetIndirectCostFormHelper = new BudgetIndirectCostFormHelper(budgetDocument.getBudget());
      
            BudgetPeriod summaryPeriod = new BudgetPeriod();
            summaryPeriod.setBudgetPeriodSequenceNumber(0);
      
            List<BudgetOverviewFormHelper> periodBudgetOverviewFormHelpers = new ArrayList();
            for (BudgetPeriod budgetPeriod : budgetDocument.getBudget().getPeriods()) {
      
                if (summaryPeriod.getBudgetPeriodBeginDate() == null) {
                    summaryPeriod.setBudgetPeriodBeginDate(budgetPeriod.getBudgetPeriodBeginDate());
                }
      
                BudgetOverviewFormHelper budgetOverviewFormHelper = new BudgetOverviewFormHelper();
                budgetOverviewFormHelper.recalculate(KraConstants.TASK_SUMMATION, budgetPeriod.getBudgetPeriodSequenceNumber(), allNonpersonnelCategories, budgetIndirectCostFormHelper, budgetDocument.getBudget());
                budgetOverviewFormHelper.setBudgetPeriod(budgetPeriod);
      
                periodBudgetOverviewFormHelpers.add(budgetOverviewFormHelper);
      
                summaryPeriod.setBudgetPeriodEndDate(budgetPeriod.getBudgetPeriodEndDate());
            }
      
            BudgetOverviewFormHelper summaryBudgetOverviewFormHelper = new BudgetOverviewFormHelper();
            summaryBudgetOverviewFormHelper.recalculate(KraConstants.TASK_SUMMATION, KraConstants.PERIOD_SUMMATION, allNonpersonnelCategories, budgetIndirectCostFormHelper, budgetDocument.getBudget());
            summaryBudgetOverviewFormHelper.setBudgetPeriod(summaryPeriod);
            
            routingForm.setPeriodBudgetOverviewFormHelpers(periodBudgetOverviewFormHelpers);
            routingForm.setSummaryBudgetOverviewFormHelper(summaryBudgetOverviewFormHelper);
        }
    }

    /**
     * Save shouldn't do anything on this page.  The only action that should cause the budget linking to occur is the actual "link" button.
     * 
     * @see org.kuali.module.kra.routingform.web.struts.action.RoutingFormAction#save(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        RoutingForm routingForm = (RoutingForm)form;
        routingForm.setPeriodBudgetOverviewFormHelpers(null);
        
        return super.load(mapping, form, request, response);
    }
}
