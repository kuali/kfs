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

import java.sql.Date;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.Constants;
import org.kuali.core.question.ConfirmationQuestion;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.kra.budget.KraConstants;
import org.kuali.module.kra.budget.bo.AgencyExtension;
import org.kuali.module.kra.budget.bo.AppointmentType;
import org.kuali.module.kra.budget.bo.Budget;
import org.kuali.module.kra.budget.bo.BudgetFringeRate;
import org.kuali.module.kra.budget.bo.BudgetGraduateAssistantRate;
import org.kuali.module.kra.budget.bo.BudgetModular;
import org.kuali.module.kra.budget.bo.BudgetPeriod;
import org.kuali.module.kra.budget.bo.BudgetTask;
import org.kuali.module.kra.budget.bo.GraduateAssistantRate;
import org.kuali.module.kra.budget.rules.event.InsertPeriodLineEventBase;
import org.kuali.module.kra.budget.service.BudgetFringeRateService;
import org.kuali.module.kra.budget.service.BudgetGraduateAssistantRateService;
import org.kuali.module.kra.budget.web.struts.form.BudgetForm;


/**
 * This class handles Actions for Research Administration.
 * 
 * @author KRA (era_team@indiana.edu)
 */

public class BudgetParametersAction extends BudgetAction {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetParametersAction.class);

    /**
     * This method overrides the BudgetAction execute method. It does so for the purpose of recalculating Personnel expenses any
     * time the Personnel page is accessed
     * 
     */
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward superForward = super.execute(mapping, form, request, response);

        BudgetForm budgetForm = (BudgetForm) form;
        
//      On first load, set the default task name for the initial task.
        if (budgetForm.getBudgetDocument().getTaskListSize() == 0) {
            String DEFAULT_BUDGET_TASK_NAME = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue("KraDevelopmentGroup", "defaultBudgetTaskName");
            budgetForm.getNewTask().setBudgetTaskName(DEFAULT_BUDGET_TASK_NAME + " 1");
            budgetForm.getNewTask().setBudgetTaskOnCampus(true);
        }

        // Set default budget types
        setupBudgetTypes(budgetForm);

        // pre-fetch academic year subdivision names for later use
        setupAcademicYearSubdivisionConstants(budgetForm);

        // Disable/enable appropriate navigation tabs
        budgetForm.checkHeaderNavigation();

        return superForward;
    }

    public ActionForward saveParameters(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetForm budgetForm = (BudgetForm) form;

        // Need to retain modular task number, since it's set on this page
        Integer budgetModularTaskNumber = null;
        if (!ObjectUtils.isNull(budgetForm.getBudgetDocument().getBudget().getModularBudget())) {
            budgetModularTaskNumber = budgetForm.getBudgetDocument().getBudget().getModularBudget().getBudgetModularTaskNumber();
        }

        List referenceObjects = new ArrayList();

        referenceObjects.add("budgetAgency");
        referenceObjects.add("federalPassThroughAgency");
        referenceObjects.add("projectDirector");
        referenceObjects.add("nonpersonnelItems");
        referenceObjects.add("personnel");
        referenceObjects.add("modularBudget");
        referenceObjects.add("indirectCost");
        referenceObjects.add("thirdPartyCostShareItems");
        referenceObjects.add("universityCostShareItems");
        referenceObjects.add("universityCostSharePersonnelItems");
        referenceObjects.add("adHocPermissions");
        referenceObjects.add("adHocOrgs");

        SpringServiceLocator.getPersistenceService().retrieveReferenceObjects(budgetForm.getBudgetDocument().getBudget(), referenceObjects);

        if (budgetForm.getBudgetDocument().getBudget().isAgencyModularIndicator()) {
            if (ObjectUtils.isNull(budgetForm.getBudgetDocument().getBudget().getModularBudget())) {
                // Modular budget with no modular data generated. So generate it.
                SpringServiceLocator.getBudgetModularService().generateModularBudget(budgetForm.getBudgetDocument().getBudget());
            }
            budgetForm.getBudgetDocument().getBudget().getModularBudget().setBudgetModularTaskNumber(budgetModularTaskNumber);
        }

        Object question = request.getParameter(Constants.QUESTION_INST_ATTRIBUTE_NAME);
        KualiConfigurationService kualiConfiguration = SpringServiceLocator.getKualiConfigurationService();

        // Logic for Cost Share question.
        String costShareRemoved = SpringServiceLocator.getBudgetService().buildCostShareRemovedCode(budgetForm.getBudgetDocument());
        if (StringUtils.isNotBlank(costShareRemoved)) {
            if (question == null) {

                // Build our confirmation message with proper context.
                StringBuffer confirmationText = new StringBuffer();
                if (costShareRemoved.contains(KraConstants.INSTITUTION_COST_SHARE_CODE)) {
                    confirmationText.append("Institution Cost Share");
                }
                if (costShareRemoved.contains(KraConstants.THIRD_PARTY_COST_SHARE_CODE)) {
                    if (costShareRemoved.indexOf(KraConstants.THIRD_PARTY_COST_SHARE_CODE) != 0) {
                        confirmationText.append(" and ");
                    }
                    confirmationText.append("Third Party Cost Share");
                }
                String confirmationQuestion = super.buildBudgetConfirmationQuestion(confirmationText.toString(), kualiConfiguration);

                // Ask for confirmation.
                return this.performQuestionWithoutInput(mapping, form, request, response, Constants.DOCUMENT_DELETE_QUESTION, confirmationQuestion, Constants.CONFIRMATION_QUESTION, "saveParameters", "");
            }

            Object buttonClicked = request.getParameter(Constants.QUESTION_CLICKED_BUTTON);

            if ((Constants.DOCUMENT_DELETE_QUESTION.equals(question)) && ConfirmationQuestion.YES.equals(buttonClicked)) {
                // If 'yes' button was clicked, save.
                super.save(mapping, form, request, response);
            }
            return mapping.findForward(Constants.MAPPING_BASIC);
        }

        super.save(mapping, form, request, response);

        if (GlobalVariables.getErrorMap().size() == 0) {
            if (budgetForm.isAuditActivated()) {
                return mapping.findForward("auditmode");
            }
            return super.overview(mapping, budgetForm, request, response);
        }

        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    public ActionForward copyFringeRateLines(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // get the form
        BudgetForm budgetForm = (BudgetForm) form;
        // get the fringe rate list
        List budgetFringeRate = budgetForm.getBudgetDocument().getBudget().getFringeRates();
        int i = 0;
        BudgetFringeRateService bfrService = SpringServiceLocator.getBudgetFringeRateService();
        for (Iterator iter = bfrService.getDefaultFringeRates().iterator(); iter.hasNext();) {
            AppointmentType appType = (AppointmentType) iter.next();

            BudgetFringeRate currentFringeRate = budgetForm.getBudgetDocument().getBudget().getFringeRate(i);
            BudgetFringeRate bfr = new BudgetFringeRate(budgetForm.getDocument().getFinancialDocumentNumber(), appType.getAppointmentTypeCode(), appType.getFringeRateAmount(), currentFringeRate.getUniversityCostShareFringeRateAmount(), appType, currentFringeRate.getObjectId(), currentFringeRate.getVersionNumber());

            budgetFringeRate.set(i, bfr);
            i++;
        }
        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    public ActionForward copyUniversityCostShareLines(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        // get the form
        BudgetForm budgetForm = (BudgetForm) form;
        // get the fringe rate list
        List budgetFringeRate = budgetForm.getBudgetDocument().getBudget().getFringeRates();
        int i = 0;
        BudgetFringeRateService bfrService = SpringServiceLocator.getBudgetFringeRateService();
        for (Iterator iter = bfrService.getDefaultFringeRates().iterator(); iter.hasNext();) {
            AppointmentType appType = (AppointmentType) iter.next();

            BudgetFringeRate currentFringeRate = budgetForm.getBudgetDocument().getBudget().getFringeRate(i);
            BudgetFringeRate bfr = new BudgetFringeRate(budgetForm.getDocument().getFinancialDocumentNumber(), appType.getAppointmentTypeCode(), currentFringeRate.getContractsAndGrantsFringeRateAmount(), appType.getCostShareFringeRateAmount(), appType, currentFringeRate.getObjectId(), currentFringeRate.getVersionNumber());

            budgetFringeRate.set(i, bfr);
            i++;
        }
        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    /*
     * A struts action to copy the first academic year subdivision graduate assistance rate system values
     */
    public ActionForward copyPeriod1GraduateAssistantLines(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return copyPeriodGraduateAssistantLines(mapping, form, request, response, 1);
    }

    /*
     * A struts action to copy the second academic year subdivision graduate assistance rate system values
     */
    public ActionForward copyPeriod2GraduateAssistantLines(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return copyPeriodGraduateAssistantLines(mapping, form, request, response, 2);
    }

    /*
     * A struts action to copy the third academic year subdivision graduate assistance rate system values
     */
    public ActionForward copyPeriod3GraduateAssistantLines(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return copyPeriodGraduateAssistantLines(mapping, form, request, response, 3);
    }

    /*
     * A struts action to copy the forth academic year subdivision graduate assistance rate system values
     */
    public ActionForward copyPeriod4GraduateAssistantLines(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return copyPeriodGraduateAssistantLines(mapping, form, request, response, 4);
    }

    /*
     * A struts action to copy the fifth academic year subdivision graduate assistance rate system values
     */
    public ActionForward copyPeriod5GraduateAssistantLines(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return copyPeriodGraduateAssistantLines(mapping, form, request, response, 5);
    }

    /*
     * A struts action to copy the sixth academic year subdivision graduate assistance rate system values
     */
    public ActionForward copyPeriod6GraduateAssistantLines(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return copyPeriodGraduateAssistantLines(mapping, form, request, response, 6);
    }

    /*
     * A struts action to copy all the academic year subdivision graduate assistance rate system values
     */
    public ActionForward copySystemGraduateAssistantLines(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return copyPeriodGraduateAssistantLines(mapping, form, request, response, 0);
    }

    /*
     * A struts action to copy the academic year subdivision graduate assistance rate system values @param periodToCopy - the
     * academic year subdivision number to copy - 0 means copy all
     */
    public ActionForward copyPeriodGraduateAssistantLines(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response, int periodToCopy) throws Exception {
        // get the form
        BudgetForm budgetForm = (BudgetForm) form;
        // get the fringe rate list
        List graduateAssistantRate = budgetForm.getBudgetDocument().getBudget().getGraduateAssistantRates();
        int i = 0;
        BudgetGraduateAssistantRateService bgarService = SpringServiceLocator.getBudgetGraduateAssistantRateService();
        for (Iterator iter = bgarService.getAllGraduateAssistantRates().iterator(); iter.hasNext();) {
            GraduateAssistantRate gar = (GraduateAssistantRate) iter.next();
            BudgetGraduateAssistantRate currentGraduateAssistantRate = budgetForm.getBudgetDocument().getBudget().getGraduateAssistantRate(i);
            KualiDecimal[] periodRates = new KualiDecimal[6];
            for (int j = 0; j < 6; j++) {
                if (periodToCopy == 0 || j + 1 == periodToCopy) {
                    periodRates[j] = gar.getCampusMaximumPeriodRate(j + 1);
                }
                else {
                    periodRates[j] = currentGraduateAssistantRate.getCampusMaximumPeriodRate(j + 1);
                }
            }
            BudgetGraduateAssistantRate budgetGraduateAssistantRate = new BudgetGraduateAssistantRate(budgetForm.getDocument().getFinancialDocumentNumber(), gar.getCampusCode(), periodRates[0], periodRates[1], periodRates[2], periodRates[3], periodRates[4], periodRates[5], gar, currentGraduateAssistantRate.getObjectId(), currentGraduateAssistantRate.getVersionNumber());

            graduateAssistantRate.set(i, budgetGraduateAssistantRate);
            i++;
        }
        return mapping.findForward(Constants.MAPPING_BASIC);
    }


    public ActionForward insertPeriodLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetForm budgetForm = (BudgetForm) form;

        // check any business rules
        boolean rulePassed = SpringServiceLocator.getKualiRuleService().applyRules(new InsertPeriodLineEventBase(budgetForm.getDocument(), budgetForm.getNewPeriod()));

        if (rulePassed) {
            budgetForm.getBudgetDocument().addPeriod(budgetForm.getNewPeriod());
            budgetForm.setNewPeriod(new BudgetPeriod());
        }
        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    public ActionForward deletePeriodLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Object question = request.getParameter(Constants.QUESTION_INST_ATTRIBUTE_NAME);
        KualiConfigurationService kualiConfiguration = SpringServiceLocator.getKualiConfigurationService();

        // Logic for DocCancelQuestion.
        if (question == null) {

            // Build our confirmation message with proper context.
            BudgetForm budgetForm = (BudgetForm) form;
            BudgetPeriod periodToDelete = budgetForm.getBudgetDocument().getBudget().getPeriod(getLineToDelete(request));
            String confirmationQuestion = super.buildBudgetConfirmationQuestion(periodToDelete.getBudgetPeriodLabel(), kualiConfiguration);

            // Ask for confirmation.
            return this.performQuestionWithoutInput(mapping, form, request, response, Constants.DOCUMENT_DELETE_QUESTION, confirmationQuestion, Constants.CONFIRMATION_QUESTION, "deletePeriodLine", Integer.toString(getLineToDelete(request)));
        }

        Object buttonClicked = request.getParameter(Constants.QUESTION_CLICKED_BUTTON);

        if ((Constants.DOCUMENT_DELETE_QUESTION.equals(question)) && ConfirmationQuestion.YES.equals(buttonClicked)) {

            // Remove the period & set the new period start date.
            BudgetForm budgetForm = (BudgetForm) form;
            budgetForm.getBudgetDocument().getBudget().getPeriods().remove(Integer.parseInt(request.getParameter("context")));

            Date defaultNextBeginDate = budgetForm.getBudgetDocument().getBudget().getDefaultNextPeriodBeginDate();
            if (defaultNextBeginDate != null) {
                budgetForm.getNewPeriod().setBudgetPeriodBeginDate(defaultNextBeginDate);
            }
        }

        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    public ActionForward insertTaskLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetForm budgetForm = (BudgetForm) form;
        budgetForm.getBudgetDocument().addTask(budgetForm.getNewTask());
        budgetForm.setNewTask(new BudgetTask());
        budgetForm.getNewTask().setBudgetTaskOnCampus(true);
        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    public ActionForward deleteTaskLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Object question = request.getParameter(Constants.QUESTION_INST_ATTRIBUTE_NAME);
        KualiConfigurationService kualiConfiguration = SpringServiceLocator.getKualiConfigurationService();

        // Logic for DocCancelQuestion.
        if (question == null) {

            // Build our confirmation with proper context.
            BudgetForm budgetForm = (BudgetForm) form;
            BudgetTask taskToDelete = budgetForm.getBudgetDocument().getBudget().getTask(getLineToDelete(request));
            String confirmationQuestion = super.buildBudgetConfirmationQuestion(taskToDelete.getBudgetTaskName(), kualiConfiguration);

            // Ask for confirmation.
            return this.performQuestionWithoutInput(mapping, form, request, response, Constants.DOCUMENT_DELETE_QUESTION, confirmationQuestion, Constants.CONFIRMATION_QUESTION, "deleteTaskLine", Integer.toString(getLineToDelete(request)));
        }
        else {

            Object buttonClicked = request.getParameter(Constants.QUESTION_CLICKED_BUTTON);

            if ((Constants.DOCUMENT_DELETE_QUESTION.equals(question)) && ConfirmationQuestion.NO.equals(buttonClicked)) {
                // If no button clicked, reload the confirmation page.
            }
            else {
                // Remove the task from the task list.
                BudgetForm budgetForm = (BudgetForm) form;
                budgetForm.getBudgetDocument().getBudget().getTasks().remove(Integer.parseInt(request.getParameter("context")));
            }

            return mapping.findForward(Constants.MAPPING_BASIC);
        }
    }

    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.refresh(mapping, form, request, response);
        BudgetForm budgetForm = (BudgetForm) form;
        Budget budget = budgetForm.getBudgetDocument().getBudget();
        if (request.getParameter(Constants.REFRESH_CALLER) != null) {
            String refreshCaller = request.getParameter(Constants.REFRESH_CALLER);
            // check to see if we are coming back from a lookup
            if (refreshCaller.equals(Constants.KUALI_LOOKUPABLE_IMPL)) {
                if ("true".equals(request.getParameter("document.budget.agencyToBeNamedIndicator"))) {
                    // coming back from Agency lookup - To Be Named selected
                    budget.setBudgetAgency(null);
                    budget.setBudgetAgencyNumber(null);
                    BudgetModular modularBudget = budget.getModularBudget() != null ? budget.getModularBudget() : new BudgetModular(budget.getDocumentHeaderId());
                    resetModularBudget(budget, modularBudget);
                    budget.setModularBudget(modularBudget);
                }
                else if (request.getParameter("document.budget.budgetAgencyNumber") != null) {
                    // coming back from an Agnecy lookup - Agency selected
                    budget.setAgencyToBeNamedIndicator(false);
                    BudgetModular modularBudget = budget.getModularBudget() != null ? budget.getModularBudget() : new BudgetModular(budget.getDocumentHeaderId());
                    budget.refreshReferenceObject("budgetAgency");
                    budget.getBudgetAgency().refresh();
                    if (budget.getBudgetAgency().getAgencyExtension() != null) {
                        AgencyExtension agencyExtension = budget.getBudgetAgency().getAgencyExtension();
                        modularBudget.setBudgetModularIncrementAmount(agencyExtension.getBudgetModularIncrementAmount());
                        modularBudget.setBudgetPeriodMaximumAmount(agencyExtension.getBudgetPeriodMaximumAmount());
                    }
                    else {
                        resetModularBudget(budget, modularBudget);
                    }
                    budget.setModularBudget(modularBudget);
                }
                else if (request.getParameter("document.budget.budgetProjectDirectorSystemId") != null) {
                    // Coming back from project director lookup - project director selected
                    budgetForm.getBudgetDocument().getBudget().setProjectDirectorToBeNamedIndicator(false);
                }
                else if ("true".equals(request.getParameter("document.budget.projectDirectorToBeNamedIndicator"))) {
                    // Coming back from project director lookup - Name Later selected
                    budgetForm.getBudgetDocument().getBudget().setProjectDirector(null);
                    budgetForm.getBudgetDocument().getBudget().setBudgetProjectDirectorSystemId(null);
                }
            }
        }
        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    private static void resetModularBudget(Budget budget, BudgetModular modularBudget) {
        modularBudget.setBudgetModularTaskNumber(null);
        budget.setAgencyModularIndicator(false);
    }

    public ActionForward basic(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(Constants.MAPPING_BASIC);
    }
}
