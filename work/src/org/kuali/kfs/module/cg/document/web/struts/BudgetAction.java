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
package org.kuali.kfs.module.cg.document.web.struts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.cg.CGConstants;
import org.kuali.kfs.module.cg.CGKeyConstants;
import org.kuali.kfs.module.cg.businessobject.AdhocPerson;
import org.kuali.kfs.module.cg.businessobject.Budget;
import org.kuali.kfs.module.cg.businessobject.BudgetNonpersonnel;
import org.kuali.kfs.module.cg.document.BudgetDocument;
import org.kuali.kfs.module.cg.document.service.BudgetIndirectCostService;
import org.kuali.kfs.module.cg.document.service.BudgetModularService;
import org.kuali.kfs.module.cg.document.service.BudgetNonpersonnelService;
import org.kuali.kfs.module.cg.document.service.BudgetPersonnelService;
import org.kuali.kfs.module.cg.document.service.BudgetTypeCodeService;
import org.kuali.kfs.module.cg.document.validation.event.EnterModularEvent;
import org.kuali.kfs.module.cg.document.validation.event.RunAuditEvent;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.bo.AdHocRoutePerson;
import org.kuali.rice.kns.question.ConfirmationQuestion;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.KualiRuleService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.service.PersistenceService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;

/**
 * This class handles Actions for Research Administration.
 */

public class BudgetAction extends ResearchDocumentActionBase {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetAction.class);


    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward forward = super.execute(mapping, form, request, response);

        BudgetForm budgetForm = (BudgetForm) form;

        if (budgetForm.isAuditActivated()) {
            SpringContext.getBean(KualiRuleService.class).applyRules(new RunAuditEvent(budgetForm.getBudgetDocument()));
        }

        if (!GlobalVariables.getErrorMap().isEmpty() && !allowsNavigate(GlobalVariables.getErrorMap())) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        budgetForm.sortCollections();

        return forward;
    }


    @Override
    /**
     * Overriding headerTab to customize how clearing tab state works on Budget. Specifically, additional attributes (selected task
     * and period) should be cleared any time header navigation occurs.
     */
    public ActionForward headerTab(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        BudgetForm budgetForm = (BudgetForm) form;

        // This is so that tab states are not shared between pages.
        budgetForm.newTabState(true, true);


        return super.headerTab(mapping, form, request, response);
    }


    public ActionForward route(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        BudgetForm budgetForm = (BudgetForm) form;

        Object question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);
        KualiConfigurationService kualiConfiguration = SpringContext.getBean(KualiConfigurationService.class);

        // Logic for DocCancelQuestion.
        if (question == null) {
            // Ask for confirmation.
            return this.performQuestionWithoutInput(mapping, form, request, response, KFSConstants.DOCUMENT_DELETE_QUESTION, CGConstants.QUESTION_ROUTE_DOCUMENT_TO_COMPLETE, KFSConstants.CONFIRMATION_QUESTION, "route", "0");
        }

        Object buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);

        if ((KFSConstants.DOCUMENT_DELETE_QUESTION.equals(question)) && ConfirmationQuestion.YES.equals(buttonClicked)) {
            budgetForm.setAdHocRoutePersons(convertToAdHocRoutePersons(budgetForm.getBudgetDocument().getAdhocPersons()));
            return super.route(mapping, form, request, response);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);

    }

    /**
     * This method will save the document, but only after being successfully validated.
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @throws Exception
     */
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        BudgetForm budgetForm = (BudgetForm) form;
        budgetForm.setMethodToCall(KFSConstants.SAVE_METHOD);

        // Check if user has permission to save
        Map documentActions = budgetForm.getDocumentActions();       
        if (documentActions.containsKey(KNSConstants.KUALI_ACTION_CAN_EDIT)) {
            super.save(mapping, form, request, response);
        }

        if (budgetForm.isAuditActivated()) {
            budgetForm.newTabState(true, true);
            return mapping.findForward("auditmode");
        }
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * This method will load the document, which was previously saved
     * 
     * @param mapping
     * @param form
     * @param request
     * @param response
     * @return
     * @throws Exception
     */
    public ActionForward load(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        BudgetForm budgetForm = (BudgetForm) form;
        budgetForm.setDocId(budgetForm.getBudgetDocument().getDocumentNumber());
        this.loadDocument(budgetForm);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward update(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward overview(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        this.load(mapping, form, request, response);

        BudgetForm budgetForm = (BudgetForm) form;

        budgetForm.setBudgetOverviewFormHelper(new BudgetOverviewFormHelper(budgetForm));

        return mapping.findForward("overview");
    }

    public ActionForward parameters(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        this.load(mapping, form, request, response);

        BudgetForm budgetForm = (BudgetForm) form;

        // Set default task name
        String DEFAULT_BUDGET_TASK_NAME = SpringContext.getBean(ParameterService.class).getParameterValue(BudgetDocument.class, CGConstants.DEFAULT_BUDGET_TASK_NAME);
        budgetForm.getNewTask().setBudgetTaskName(DEFAULT_BUDGET_TASK_NAME + " " + (budgetForm.getBudgetDocument().getTaskListSize() + 1));

        // New task defaults to on campus
        budgetForm.getNewTask().setBudgetTaskOnCampus(true);

        // Set default budget types
        setupBudgetTypes(budgetForm);

        // pre-fetch academic year subdivision names for later use
        setupAcademicYearSubdivisionConstants(budgetForm);

        return mapping.findForward("parameters");
    }

    public ActionForward template(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        this.load(mapping, form, request, response);

        BudgetForm budgetForm = (BudgetForm) form;

        return mapping.findForward("template");
    }

    public ActionForward personnel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        this.load(mapping, form, request, response);
        BudgetForm budgetForm = (BudgetForm) form;

        // This is so that tab states are not shared between pages.
        budgetForm.newTabState(true, true);

        budgetForm.setDeleteValues(new String[budgetForm.getBudgetDocument().getBudget().getPersonnel().size()]);

        SpringContext.getBean(BudgetPersonnelService.class).reconcileAndCalculatePersonnel(budgetForm.getBudgetDocument());

        return mapping.findForward("personnel");
    }

    public ActionForward nonpersonnel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        this.load(mapping, form, request, response);
        BudgetForm budgetForm = (BudgetForm) form;

        setupNonpersonnelCategories(budgetForm);
        PersistenceService persistenceService = SpringContext.getBean(PersistenceService.class);
        persistenceService.retrieveReferenceObject(budgetForm.getBudgetDocument().getBudget(), "nonpersonnelItems");
        budgetForm.setBudgetNonpersonnelFormHelper(new BudgetNonpersonnelFormHelper(budgetForm));

        return mapping.findForward("nonpersonnel");
    }

    public ActionForward modular(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        this.load(mapping, form, request, response);
        BudgetForm budgetForm = (BudgetForm) form;
        Budget budget = budgetForm.getBudgetDocument().getBudget();

        // TODO Shouldn't be necessary but was added to fix KULERA-945: "Navigating from Nonpersonnel to Modular may cause
        // exception"
        for (BudgetNonpersonnel budgetNonpersonnel : budget.getNonpersonnelItems()) {
            budgetNonpersonnel.refreshReferenceObject("nonpersonnelObjectCode");
        }

        SpringContext.getBean(BudgetModularService.class).generateModularBudget(budget, budgetForm.getNonpersonnelCategories());

        SpringContext.getBean(KualiRuleService.class).applyRules(new EnterModularEvent(budgetForm.getDocument()));

        return mapping.findForward("modular");
    }

    public ActionForward indirectcost(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        this.load(mapping, form, request, response);

        BudgetForm budgetForm = (BudgetForm) form;

        // Make sure our IDC object is properly formed. This will also perform initial calculations for BudgetTaskPeriodIndirectCost
        // objects.
        SpringContext.getBean(BudgetIndirectCostService.class).refreshIndirectCost(budgetForm.getBudgetDocument());

        // This will populate task and period totals in HashMaps so they can be pulled in the view.
        budgetForm.setBudgetIndirectCostFormHelper(new BudgetIndirectCostFormHelper(budgetForm));

        return mapping.findForward("indirectcost");
    }

    public ActionForward costshare(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        this.load(mapping, form, request, response);

        BudgetForm budgetForm = (BudgetForm) form;

        setupBudgetCostSharePermissionDisplay(budgetForm);

        SpringContext.getBean(BudgetIndirectCostService.class).refreshIndirectCost(budgetForm.getBudgetDocument());
        budgetForm.setBudgetIndirectCostFormHelper(new BudgetIndirectCostFormHelper(budgetForm));
        budgetForm.setBudgetCostShareFormHelper(new BudgetCostShareFormHelper(budgetForm));
        budgetForm.getNewInstitutionCostShare().setPermissionIndicator(true);

        return mapping.findForward("costshare");
    }

    public ActionForward output(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        this.load(mapping, form, request, response);

        BudgetForm budgetForm = (BudgetForm) form;

        return mapping.findForward("output");
    }

    public ActionForward auditmode(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        this.load(mapping, form, request, response);

        BudgetForm budgetForm = (BudgetForm) form;

        return mapping.findForward("auditmode");
    }


    public ActionForward permissions(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        this.load(mapping, form, request, response);

        BudgetForm budgetForm = (BudgetForm) form;

        budgetForm.setInitiator(SpringContext.getBean(org.kuali.rice.kim.service.PersonService.class).getPersonByPrincipalName(budgetForm.getDocument().getDocumentHeader().getWorkflowDocument().getInitiatorNetworkId()));

        budgetForm.getBudgetDocument().populateDocumentForRouting();
        budgetForm.getBudgetDocument().getDocumentHeader().getWorkflowDocument().saveRoutingData();

        return mapping.findForward("permissions");
    }

    /**
     * This method will setup the message that will get displayed to the user when they are asked to confirm a delete.
     * 
     * @param confirmationContext
     * @param kualiConfiguration
     * @return The message to display to the user in the question prompt window.
     * @throws Exception
     */
    protected String buildBudgetConfirmationQuestion(String confirmationContext, KualiConfigurationService kualiConfiguration) throws Exception {
        return StringUtils.replace(kualiConfiguration.getPropertyString(CGKeyConstants.QUESTION_KRA_DELETE_CONFIRMATION), "{0}", confirmationContext);
    }

    /**
     * This method...
     * 
     * @param budgetForm
     * @throws Exception
     */
    public static void setupNonpersonnelCategories(BudgetForm budgetForm) throws Exception {
        List allNonpersonnelCategories = SpringContext.getBean(BudgetNonpersonnelService.class).getAllNonpersonnelCategories();
        budgetForm.setNonpersonnelCategories(allNonpersonnelCategories);
    }

    /**
     * This method gets the default budget types from the reference table and sets it in the BudgetForm
     * 
     * @param budgetForm
     * @throws Exception
     */
    protected static void setupBudgetTypes(BudgetForm budgetForm) throws Exception {

        budgetForm.getBudgetDocument().getBudget().refreshReferenceObject("budgetAgency");
        budgetForm.setSupportsModular(SpringContext.getBean(BudgetModularService.class).agencySupportsModular(budgetForm.getBudgetDocument().getBudget().getBudgetAgency()));

        List allBudgetTypes = (List) SpringContext.getBean(BudgetTypeCodeService.class).getDefaultBudgetTypeCodes();
        budgetForm.setBudgetTypeCodes(allBudgetTypes);
    }

    /**
     * This method gets the names of the academic year subdivisions and sets them in the BudgetForm
     * 
     * @param budgetForm
     * @throws Exception
     */
    protected static void setupAcademicYearSubdivisionConstants(BudgetForm budgetForm) throws Exception {
        List<String> academicYearSubdivisionNames = SpringContext.getBean(ParameterService.class).getParameterValues(BudgetDocument.class, CGConstants.KRA_BUDGET_ACADEMIC_YEAR_SUBDIVISION_NAMES);
        budgetForm.setAcademicYearSubdivisionNames(academicYearSubdivisionNames);
        budgetForm.setNumberOfAcademicYearSubdivisions(Integer.parseInt(SpringContext.getBean(ParameterService.class).getParameterValue(BudgetDocument.class, CGConstants.KRA_BUDGET_NUMBER_OF_ACADEMIC_YEAR_SUBDIVISIONS)));
    }

    private static List<AdHocRoutePerson> convertToAdHocRoutePersons(List<AdhocPerson> adHocPermissions) {
        List<AdHocRoutePerson> adHocRoutePersons = new ArrayList<AdHocRoutePerson>();
        for (AdhocPerson adHocPermission : adHocPermissions) {
            SpringContext.getBean(PersistenceService.class).refreshAllNonUpdatingReferences(adHocPermission);
            AdHocRoutePerson adHocRoutePerson = new AdHocRoutePerson();
            adHocRoutePerson.setId(adHocPermission.getUser().getPrincipalName());
            adHocRoutePerson.setActionRequested("F");
            adHocRoutePersons.add(adHocRoutePerson);
        }
        return adHocRoutePersons;
    }

    protected static void setupBudgetCostSharePermissionDisplay(BudgetForm budgetForm) {
        String costSharePermissionCode = SpringContext.getBean(ParameterService.class).getParameterValue(BudgetDocument.class, CGConstants.BUDGET_COST_SHARE_PERMISSION_CODE);
        if (costSharePermissionCode.equals(CGConstants.COST_SHARE_PERMISSION_CODE_OPTIONAL)) {
            budgetForm.setDisplayCostSharePermission(true);
        }
    }

    /**
     * We want to allow navigation for a couple of specific errors. So find these here.
     * 
     * @param map
     * @return boolean
     */
    protected static boolean allowsNavigate(Map map) {
        int counter = 0;
        if (map.containsKey("document.budget.modular.tooLarge")) {
            counter += 1;
        }
        return map.size() == counter;
    }
}

