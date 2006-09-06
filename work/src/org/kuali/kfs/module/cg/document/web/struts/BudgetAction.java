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
import org.kuali.core.bo.user.AuthenticationUserId;
import org.kuali.core.question.ConfirmationQuestion;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.web.struts.action.KualiDocumentActionBase;
import org.kuali.module.kra.budget.KraConstants;
import org.kuali.module.kra.budget.bo.BudgetPeriod;
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
 * 
 * @author KRA (era_team@indiana.edu)
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

        Object question = request.getParameter(Constants.QUESTION_INST_ATTRIBUTE_NAME);
        KualiConfigurationService kualiConfiguration = SpringServiceLocator.getKualiConfigurationService();

        // Logic for DocCancelQuestion.
        if (question == null) {
            // Ask for confirmation.
            return this.performQuestionWithoutInput(mapping, form, request, response, Constants.DOCUMENT_DELETE_QUESTION, KraConstants.QUESTION_ROUTE_DOCUMENT_TO_COMPLETE, Constants.CONFIRMATION_QUESTION, "route", "0");
        }

        Object buttonClicked = request.getParameter(Constants.QUESTION_CLICKED_BUTTON);

        if ((Constants.DOCUMENT_DELETE_QUESTION.equals(question)) && ConfirmationQuestion.YES.equals(buttonClicked)) {
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
        budgetForm.setDocId(budgetForm.getBudgetDocument().getFinancialDocumentNumber());
        this.loadDocument(budgetForm);

        return mapping.findForward(Constants.MAPPING_BASIC);
    }

    public ActionForward docHandler(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        ActionForward forward = super.docHandler(mapping, form, request, response);
        BudgetForm budgetForm = (BudgetForm) form;

        if (IDocHandler.INITIATE_COMMAND.equals(budgetForm.getCommand())) {
            budgetForm.getBudgetDocument().getBudget().setDocumentHeaderId(budgetForm.getBudgetDocument().getFinancialDocumentNumber());
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
        String DEFAULT_BUDGET_TASK_NAME = SpringServiceLocator.getKualiConfigurationService().getApplicationParameterValue("KraDevelopmentGroup", "defaultBudgetTaskName");
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

        SpringServiceLocator.getBudgetIndirectCostService().refreshIndirectCost(budgetForm.getBudgetDocument());
        budgetForm.setBudgetIndirectCostFormHelper(new BudgetIndirectCostFormHelper(budgetForm));
        budgetForm.setBudgetCostShareFormHelper(new BudgetCostShareFormHelper(budgetForm));

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
        
        budgetForm.setInitiator(SpringServiceLocator.getKualiUserService().getUser(
                new AuthenticationUserId(budgetForm.getDocument().getDocumentHeader().getWorkflowDocument().getInitiatorNetworkId())));

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
