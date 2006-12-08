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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.core.authorization.AuthorizationConstants;
import org.kuali.core.bo.AdHocRoutePerson;
import org.kuali.core.bo.AdHocRouteWorkgroup;
import org.kuali.core.bo.user.AuthenticationUserId;
import org.kuali.core.question.ConfirmationQuestion;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.web.struts.action.KualiDocumentActionBase;
import org.kuali.module.kra.budget.KraConstants;
import org.kuali.module.kra.budget.bo.BudgetAdHocPermission;
import org.kuali.module.kra.budget.bo.BudgetAdHocWorkgroup;
import org.kuali.module.kra.budget.rules.event.EnterModularEvent;
import org.kuali.module.kra.budget.rules.event.RunAuditEvent;
import org.kuali.module.kra.budget.web.struts.form.BudgetCostShareFormHelper;
import org.kuali.module.kra.budget.web.struts.form.BudgetForm;
import org.kuali.module.kra.budget.web.struts.form.BudgetIndirectCostFormHelper;
import org.kuali.module.kra.budget.web.struts.form.BudgetNonpersonnelFormHelper;
import org.kuali.module.kra.budget.web.struts.form.BudgetOverviewFormHelper;

import edu.iu.uis.eden.clientapp.IDocHandler;

/**
 * This class handles Actions for Research Administration.
 */

public class BudgetAction extends KualiDocumentActionBase {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetAction.class);


    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward forward = super.execute(mapping, form, request, response);

        BudgetForm budgetForm = (BudgetForm) form;

        if (budgetForm.isAuditActivated()) {
            SpringServiceLocator.getKualiRuleService().applyRules(new RunAuditEvent(budgetForm.getBudgetDocument()));
        }

        if (!GlobalVariables.getErrorMap().isEmpty() && !allowsNavigate(GlobalVariables.getErrorMap())) {
            return mapping.findForward(Constants.MAPPING_BASIC);
        }

        budgetForm.sortCollections();

        return forward;
    }
    
    public ActionForward route(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        
        BudgetForm budgetForm = (BudgetForm) form;

        Object question = request.getParameter(Constants.QUESTION_INST_ATTRIBUTE_NAME);
        KualiConfigurationService kualiConfiguration = SpringServiceLocator.getKualiConfigurationService();

        // Logic for DocCancelQuestion.
        if (question == null) {
            // Ask for confirmation.
            return this.performQuestionWithoutInput(mapping, form, request, response, Constants.DOCUMENT_DELETE_QUESTION, KraConstants.QUESTION_ROUTE_DOCUMENT_TO_COMPLETE, Constants.CONFIRMATION_QUESTION, "route", "0");
        }

        Object buttonClicked = request.getParameter(Constants.QUESTION_CLICKED_BUTTON);

        if ((Constants.DOCUMENT_DELETE_QUESTION.equals(question)) && ConfirmationQuestion.YES.equals(buttonClicked)) {
            budgetForm.setAdHocRoutePersons(convertToAdHocRoutePersons(budgetForm.getBudgetDocument().getBudget().getAdHocPermissions()));
            budgetForm.setAdHocRouteWorkgroups(convertToAdHocRouteWorkgroups(budgetForm.getBudgetDocument().getBudget().getAdHocWorkgroups()));
            return super.route(mapping, form, request, response);
        }
        
        return mapping.findForward(Constants.MAPPING_BASIC);

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
        budgetForm.setMethodToCall(Constants.SAVE_METHOD);

        // Check if user has permission to save
        budgetForm.populateAuthorizationFields(SpringServiceLocator.getDocumentAuthorizationService().getDocumentAuthorizer(budgetForm.getBudgetDocument()));
        if (!"TRUE".equals(budgetForm.getEditingMode().get(AuthorizationConstants.EditMode.VIEW_ONLY))) {
            super.save(mapping, form, request, response);
        }

        if (budgetForm.isAuditActivated()) {
            budgetForm.newTabState(true, true);
            return mapping.findForward("auditmode");
        }
        return mapping.findForward(Constants.MAPPING_BASIC);
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

        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    public ActionForward docHandler(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ActionForward forward = super.docHandler(mapping, form, request, response);
        BudgetForm budgetForm = (BudgetForm) form;

        if (IDocHandler.INITIATE_COMMAND.equals(budgetForm.getCommand())) {
            budgetForm.getBudgetDocument().getBudget().setDocumentNumber(budgetForm.getBudgetDocument().getDocumentNumber());
            SpringServiceLocator.getBudgetService().initializeBudget(budgetForm.getBudgetDocument());
        }
        return forward;
    }

    public ActionForward update(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    public ActionForward overview(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        this.load(mapping, form, request, response);

        BudgetForm budgetForm = (BudgetForm) form;

        // This is so that tab states are not shared between pages.
        budgetForm.newTabState(true, true);

        budgetForm.setBudgetOverviewFormHelper(new BudgetOverviewFormHelper(budgetForm));

        return mapping.findForward("overview");
    }

    public ActionForward parameters(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        this.load(mapping, form, request, response);

        BudgetForm budgetForm = (BudgetForm) form;

        // This is so that tab states are not shared between pages.
        budgetForm.newTabState(true, true);

        // Set default task name
        String DEFAULT_BUDGET_TASK_NAME = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue(KraConstants.KRA_DEVELOPMENT_GROUP, "defaultBudgetTaskName");
        budgetForm.getNewTask().setBudgetTaskName(DEFAULT_BUDGET_TASK_NAME + " " + (budgetForm.getBudgetDocument().getTaskListSize() + 1));
        
//      New task defaults to on campus
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

        // This is so that tab states are not shared between pages.
        budgetForm.newTabState(true, true);

        return mapping.findForward("template");
    }

    public ActionForward notes(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        this.load(mapping, form, request, response);

        BudgetForm budgetForm = (BudgetForm) form;

        // This is so that tab states are not shared between pages.
        budgetForm.newTabState(true, true);

        return mapping.findForward("notes");
    }
    
    public ActionForward personnel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        this.load(mapping, form, request, response);
        BudgetForm budgetForm = (BudgetForm) form;

        // This is so that tab states are not shared between pages.
        budgetForm.newTabState(true, true);

        budgetForm.setDeleteValues(new String[budgetForm.getBudgetDocument().getBudget().getPersonnel().size()]);

        SpringServiceLocator.getBudgetPersonnelService().reconcileAndCalculatePersonnel(budgetForm.getBudgetDocument());

        return mapping.findForward("personnel");
    }

    public ActionForward nonpersonnel(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        this.load(mapping, form, request, response);
        BudgetForm budgetForm = (BudgetForm) form;

        // This is so that tab states are not shared between pages.
        budgetForm.newTabState(true, true);

        setupNonpersonnelCategories(budgetForm);
        budgetForm.setBudgetNonpersonnelFormHelper(new BudgetNonpersonnelFormHelper(budgetForm));

        return mapping.findForward("nonpersonnel");
    }

    public ActionForward modular(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        this.load(mapping, form, request, response);
        BudgetForm budgetForm = (BudgetForm) form;

        // This is so that tab states are not shared between pages.
        budgetForm.newTabState(true, true);

        SpringServiceLocator.getBudgetModularService().generateModularBudget(budgetForm.getBudgetDocument().getBudget(), budgetForm.getNonpersonnelCategories());

        SpringServiceLocator.getKualiRuleService().applyRules(new EnterModularEvent(budgetForm.getDocument()));

        return mapping.findForward("modular");
    }

    public ActionForward indirectcost(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        this.load(mapping, form, request, response);

        BudgetForm budgetForm = (BudgetForm) form;

        // This is so that tab states are not shared between pages.
        budgetForm.newTabState(true, true);

        // Make sure our IDC object is properly formed. This will also perform initial calculations for BudgetTaskPeriodIndirectCost
        // objects.
        SpringServiceLocator.getBudgetIndirectCostService().refreshIndirectCost(budgetForm.getBudgetDocument());

        // This will populate task and period totals in HashMaps so they can be pulled in the view.
        budgetForm.setBudgetIndirectCostFormHelper(new BudgetIndirectCostFormHelper(budgetForm));

        return mapping.findForward("indirectcost");
    }

    public ActionForward costshare(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        this.load(mapping, form, request, response);

        BudgetForm budgetForm = (BudgetForm) form;

        // This is so that tab states are not shared between pages.
        budgetForm.newTabState(true, true);
        
        setupBudgetCostSharePermissionDisplay(budgetForm);

        SpringServiceLocator.getBudgetIndirectCostService().refreshIndirectCost(budgetForm.getBudgetDocument());
        budgetForm.setBudgetIndirectCostFormHelper(new BudgetIndirectCostFormHelper(budgetForm));
        budgetForm.setBudgetCostShareFormHelper(new BudgetCostShareFormHelper(budgetForm));
        budgetForm.getNewInstitutionCostShare().setPermissionIndicator(true);

        return mapping.findForward("costshare");
    }

    public ActionForward output(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        this.load(mapping, form, request, response);

        BudgetForm budgetForm = (BudgetForm) form;

        // This is so that tab states are not shared between pages.
        budgetForm.newTabState(true, true);

        return mapping.findForward("output");
    }

    public ActionForward auditmode(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        this.load(mapping, form, request, response);

        BudgetForm budgetForm = (BudgetForm) form;

        // This is so that tab states are not shared between pages.
        budgetForm.newTabState(true, true);

        return mapping.findForward("auditmode");
    }


    public ActionForward permissions(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        this.load(mapping, form, request, response);

        BudgetForm budgetForm = (BudgetForm) form;
        
        budgetForm.setInitiator(SpringServiceLocator.getUniversalUserService().getUniversalUser(
                new AuthenticationUserId(budgetForm.getDocument().getDocumentHeader().getWorkflowDocument().getInitiatorNetworkId())));
        
        budgetForm.getBudgetDocument().populateDocumentForRouting();
        budgetForm.getBudgetDocument().getDocumentHeader().getWorkflowDocument().saveRoutingData();

        // This is so that tab states are not shared between pages.
        budgetForm.newTabState(true, true);

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
        return StringUtils.replace(kualiConfiguration.getPropertyString(KeyConstants.QUESTION_BUDGET_DELETE_CONFIRMATION), "{0}", confirmationContext);
    }

    /**
     * This method...
     * 
     * @param budgetForm
     * @throws Exception
     */
    public static void setupNonpersonnelCategories(BudgetForm budgetForm) throws Exception {
        List allNonpersonnelCategories = SpringServiceLocator.getBudgetNonpersonnelService().getAllNonpersonnelCategories();
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
        budgetForm.setSupportsModular(SpringServiceLocator.getBudgetModularService().agencySupportsModular(budgetForm.getBudgetDocument().getBudget().getBudgetAgency()));

        List allBudgetTypes = (List) SpringServiceLocator.getBudgetTypeCodeService().getDefaultBudgetTypeCodes();
        budgetForm.setBudgetTypeCodes(allBudgetTypes);
    }

    /**
     * This method gets the names of the academic year subdivisions and sets them in the BudgetForm
     * 
     * @param budgetForm
     * @throws Exception
     */
    protected static void setupAcademicYearSubdivisionConstants(BudgetForm budgetForm) throws Exception {
        String[] academicYearSubdivisionNames = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValues(KraConstants.KRA_DEVELOPMENT_GROUP, KraConstants.KRA_BUDGET_ACADEMIC_YEAR_SUBDIVISION_NAMES);
        budgetForm.setAcademicYearSubdivisionNames(Arrays.asList(academicYearSubdivisionNames));
        budgetForm.setNumberOfAcademicYearSubdivisions(Integer.parseInt(SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue(KraConstants.KRA_DEVELOPMENT_GROUP, KraConstants.KRA_BUDGET_NUMBER_OF_ACADEMIC_YEAR_SUBDIVISIONS)));
    }
    
    private static List<AdHocRoutePerson> convertToAdHocRoutePersons(List<BudgetAdHocPermission> adHocPermissions) {
        List<AdHocRoutePerson> adHocRoutePersons = new ArrayList<AdHocRoutePerson>();
        for (BudgetAdHocPermission adHocPermission: adHocPermissions) {
            SpringServiceLocator.getPersistenceService().refreshAllNonUpdatingReferences(adHocPermission);
            AdHocRoutePerson adHocRoutePerson = new AdHocRoutePerson();
            adHocRoutePerson.setId(adHocPermission.getUser().getPersonUserIdentifier());
            adHocRoutePerson.setActionRequested("F");
            adHocRoutePersons.add(adHocRoutePerson);
        }
        return adHocRoutePersons;
    }
    
    private static List<AdHocRouteWorkgroup> convertToAdHocRouteWorkgroups(List<BudgetAdHocWorkgroup> adHocWorkgroups) {
        List<AdHocRouteWorkgroup> adHocRouteWorkgroups = new ArrayList<AdHocRouteWorkgroup>();
        for (BudgetAdHocWorkgroup adHocWorkgroup: adHocWorkgroups) {
            SpringServiceLocator.getPersistenceService().refreshAllNonUpdatingReferences(adHocWorkgroup);
            AdHocRouteWorkgroup adHocRouteWorkgroup = new AdHocRouteWorkgroup();
            adHocRouteWorkgroup.setId(adHocWorkgroup.getWorkgroupName());
            adHocRouteWorkgroup.setActionRequested("F");
            adHocRouteWorkgroups.add(adHocRouteWorkgroup);
        }
        return adHocRouteWorkgroups;
    }
    
    protected static void setupBudgetCostSharePermissionDisplay(BudgetForm budgetForm) {
        String costSharePermissionCode = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue(
                KraConstants.KRA_ADMIN_GROUP_NAME, KraConstants.BUDGET_COST_SHARE_PERMISSION_CODE);
        if (costSharePermissionCode.equals(KraConstants.COST_SHARE_PERMISSION_CODE_OPTIONAL)) {
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