/*
 * Copyright 2008 The Kuali Foundation.
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.KualiModule;
import org.kuali.core.authorization.AuthorizationType;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.AuthorizationException;
import org.kuali.core.exceptions.ModuleAuthorizationException;
import org.kuali.core.question.ConfirmationQuestion;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.KualiInteger;
import org.kuali.core.util.UrlFactory;
import org.kuali.core.web.struts.action.KualiAction;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.budget.BCConstants;
import org.kuali.module.budget.BCKeyConstants;
import org.kuali.module.budget.bo.BudgetConstructionDetail;
import org.kuali.module.budget.bo.PendingBudgetConstructionAppointmentFunding;
import org.kuali.module.budget.document.authorization.BudgetConstructionDocumentAuthorizer;
import org.kuali.module.budget.service.SalarySettingService;
import org.kuali.module.budget.web.struts.form.DetailSalarySettingForm;

/**
 * the base struts action for the salary setting
 */
public abstract class DetailSalarySettingAction extends KualiAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DetailSalarySettingAction.class);

    /**
     * loads the data for the expansion screen based on the passed in url parameters
     */
    public abstract ActionForward loadExpansionScreen(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception;

    /**
     * @see org.kuali.core.web.struts.action.KualiAction#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward forward = super.execute(mapping, form, request, response);

        DetailSalarySettingForm salarySettingForm = (DetailSalarySettingForm) form;

        // TODO should not need to handle optimistic lock exception here (like KualiDocumentActionBase)
        // since BC sets locks up front, but need to verify this

        // TODO form.populateAuthorizationFields(budgetConstructionDocumentAuthorizer) would normally happen here
        // but each line needs to be authorized, so need to figure out how to implement this

        // TODO should probably use service locator and call
        // DocumentAuthorizer documentAuthorizer =
        // SpringContext.getBean(DocumentAuthorizationService.class).getDocumentAuthorizer("<BCDoctype>");
        BudgetConstructionDocumentAuthorizer budgetConstructionDocumentAuthorizer = new BudgetConstructionDocumentAuthorizer();
        salarySettingForm.populateAuthorizationFields(budgetConstructionDocumentAuthorizer);

        return forward;
    }

    /**
     * @see org.kuali.core.web.struts.action.KualiAction#checkAuthorization(org.apache.struts.action.ActionForm, java.lang.String)
     */
    @Override
    protected void checkAuthorization(ActionForm form, String methodToCall) throws AuthorizationException {
        UniversalUser currentUser = GlobalVariables.getUserSession().getUniversalUser();
        AuthorizationType bcAuthorizationType = new AuthorizationType.Default(this.getClass());

        if (!getKualiModuleService().isAuthorized(currentUser, bcAuthorizationType)) {
            LOG.error("User not authorized to use this action: " + this.getClass().getName());

            KualiModule module = getKualiModuleService().getResponsibleModule(this.getClass());
            throw new ModuleAuthorizationException(currentUser.getPersonUserIdentifier(), bcAuthorizationType, module);
        }
    }

    /**
     * save the information in the current form into underlying data store
     */
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DetailSalarySettingForm salarySettingForm = (DetailSalarySettingForm) form;

        GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_MESSAGES, KFSKeyConstants.ERROR_UNIMPLEMENTED, "Save For Salary Setting by Incumbent");

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * close the salary setting and return to the caller
     */
    public ActionForward close(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);
        KualiConfigurationService kualiConfiguration = SpringContext.getBean(KualiConfigurationService.class);

        // ask the question unless it has not been answered
        if (StringUtils.isBlank(question)) {
            String questionText = kualiConfiguration.getPropertyString(KFSKeyConstants.QUESTION_SAVE_BEFORE_CLOSE);
            return this.performQuestionWithoutInput(mapping, form, request, response, KFSConstants.DOCUMENT_SAVE_BEFORE_CLOSE_QUESTION, questionText, KFSConstants.CONFIRMATION_QUESTION, KFSConstants.MAPPING_CLOSE, "");
        }

        // save the salary setting if the user answers to the question with "Yes"
        String buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);
        if (StringUtils.equals(KFSConstants.DOCUMENT_SAVE_BEFORE_CLOSE_QUESTION, question) && StringUtils.equals(ConfirmationQuestion.YES, buttonClicked)) {
            ActionForward saveAction = this.save(mapping, form, request, response);

            GlobalVariables.getMessageList().add(KFSKeyConstants.MESSAGE_SAVED);
        }

        // return to caller if the current salary setting is in the budget by account mode
        DetailSalarySettingForm salarySettingForm = (DetailSalarySettingForm) form;
        if (salarySettingForm.isBudgetByAccountMode()) {
            return this.returnToCaller(mapping, form, request, response);
        }

        salarySettingForm.setOrgSalSetClose(true);
        GlobalVariables.getMessageList().add(BCKeyConstants.MESSAGE_BUDGET_SUCCESSFUL_CLOSE);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * return to the caller screen
     */
    public ActionForward returnToCaller(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DetailSalarySettingForm salarySettingForm = (DetailSalarySettingForm) form;

        // setup the return parms for the document and anchor
        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, BCConstants.BC_DOCUMENT_REFRESH_METHOD);
        parameters.put(KFSConstants.DOC_FORM_KEY, salarySettingForm.getReturnFormKey());
        parameters.put(KFSConstants.ANCHOR, salarySettingForm.getReturnAnchor());
        parameters.put(KFSConstants.REFRESH_CALLER, salarySettingForm.getRefreshCallerName());

        String lookupUrl = UrlFactory.parameterizeUrl("/" + BCConstants.SALARY_SETTING_ACTION, parameters);
        return new ActionForward(lookupUrl, true);
    }

    /**
     * @see org.kuali.core.web.struts.action.KualiAction#refresh(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DetailSalarySettingForm salarySettingForm = (DetailSalarySettingForm) form;

        // Do specific refresh stuff here based on refreshCaller parameter
        // typical refresh callers would be lookupable or reasoncode??
        // need to look at optmistic locking problems since we will be storing the values in the form before hand
        // this locking problem may workout if we store first then put the form in session
        String refreshCaller = request.getParameter(KFSConstants.REFRESH_CALLER);

        // TODO may need to check for reason code called refresh here

        // TODO this should figure out if user is returning to a rev or exp line and refresh just that
        // TODO this should also keep original values of obj, sobj to compare and null out dependencies when needed
        // TODO need a better way to detect return from lookups
        // returning from account lookup sets refreshcaller to accountLookupable, due to setting in account.xml
        // if (refreshCaller != null && refreshCaller.equalsIgnoreCase(KFSConstants.KUALI_LOOKUPABLE_IMPL)){
        if (refreshCaller != null && (refreshCaller.endsWith("Lookupable") || (refreshCaller.endsWith("LOOKUPABLE")))) {
            salarySettingForm.getNewBCAFLine().refreshNonUpdateableReferences();
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * changes the value of the hide field in the user interface so that when the page is rendered, the UI knows to show all of the
     * descriptions and labels for each of the pbgl line values.
     */
    public ActionForward showDetails(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DetailSalarySettingForm salarySettingForm = (DetailSalarySettingForm) form;
        salarySettingForm.setHideDetails(false);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * toggles the value of the hide field in the user interface to "hide" so that when the page is rendered, the UI displays values
     * without all of the descriptions and labels for each of the pbgl lines.
     */
    public ActionForward hideDetails(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DetailSalarySettingForm salarySettingForm = (DetailSalarySettingForm) form;
        salarySettingForm.setHideDetails(true);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * adds an appointment funding line to the set of existing funding lines
     */
    public ActionForward insertSalarySettingLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DetailSalarySettingForm salarySettingForm = (DetailSalarySettingForm) form;

        PendingBudgetConstructionAppointmentFunding newAppointmentFunding = salarySettingForm.getNewBCAFLine();
        BudgetConstructionDetail budgetConstructionDetail = salarySettingForm.getBudgetConstructionDetail();
        List<PendingBudgetConstructionAppointmentFunding> appointmentFundings = budgetConstructionDetail.getPendingBudgetConstructionAppointmentFunding();

        appointmentFundings.add(newAppointmentFunding);
        salarySettingForm.populateBCAFLines();
        salarySettingForm.setNewBCAFLine(this.createNewAppointmentFundingLine(salarySettingForm));

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * creates a new funding line based on the selected line and sets the emplid to vacant then marks the selected line delete.
     */
    public ActionForward vacateSalarySettingLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DetailSalarySettingForm salarySettingForm = (DetailSalarySettingForm) form;
        BudgetConstructionDetail budgetConstructionDetail = salarySettingForm.getBudgetConstructionDetail();

        // retrieve the selected funding line
        int indexOfSelectedLine = this.getSelectedLine(request);
        List<PendingBudgetConstructionAppointmentFunding> appointmentFundings = budgetConstructionDetail.getPendingBudgetConstructionAppointmentFunding();
        PendingBudgetConstructionAppointmentFunding appointmentFunding = appointmentFundings.get(indexOfSelectedLine);

        // associated the vacant funding line with current salary setting expansion
        SalarySettingService salarySettingService = SpringContext.getBean(SalarySettingService.class);
        PendingBudgetConstructionAppointmentFunding vacantAppointmentFunding = salarySettingService.vacateAppointmentFunding(appointmentFunding);
        if (vacantAppointmentFunding != null) {
            appointmentFundings.add(indexOfSelectedLine + 1, vacantAppointmentFunding);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * delete the selected salary setting line 
     */
    public ActionForward purgeSalarySettingLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DetailSalarySettingForm salarySettingForm = (DetailSalarySettingForm) form;
        BudgetConstructionDetail budgetConstructionDetail = salarySettingForm.getBudgetConstructionDetail();
        List<PendingBudgetConstructionAppointmentFunding> appointmentFundings = budgetConstructionDetail.getPendingBudgetConstructionAppointmentFunding();
        
        // remove the slected line
        int indexOfSelectedLine = this.getSelectedLine(request);
        appointmentFundings.remove(indexOfSelectedLine);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * sets the request amount using the CSF amount adjusted by a percent or flat rate
     */
    public ActionForward adjustSalarySettingLinePercent(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DetailSalarySettingForm salarySettingForm = (DetailSalarySettingForm) form;
        BudgetConstructionDetail budgetConstructionDetail = salarySettingForm.getBudgetConstructionDetail();

        // retrieve the selected funding line
        int indexOfSelectedLine = this.getSelectedLine(request);
        List<PendingBudgetConstructionAppointmentFunding> appointmentFundings = budgetConstructionDetail.getPendingBudgetConstructionAppointmentFunding();
        PendingBudgetConstructionAppointmentFunding appointmentFunding = appointmentFundings.get(indexOfSelectedLine);

        this.adjustSalary(appointmentFunding);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    // adjust the requested salary amount of the given appointment funding line
    private void adjustSalary(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        SalarySettingService salarySettingService = SpringContext.getBean(SalarySettingService.class);

        String adjustmentMeasurement = appointmentFunding.getAdjustmentMeasurement();
        if (BCConstants.SalaryAdjustmentMeasurement.PERCENT.measurement.equals(adjustmentMeasurement)) {
            salarySettingService.adjustRequestedSalaryByPercent(appointmentFunding);
        }
        else if (BCConstants.SalaryAdjustmentMeasurement.AMOUNT.measurement.equals(adjustmentMeasurement)) {
            salarySettingService.adjustRequestedSalaryByAmount(appointmentFunding);
        }
    }
     
    /**
     * sets the default fields not setable by the user for added lines and any other required initialization
     * 
     * @param appointmentFunding the given appointment funding line
     */
    protected PendingBudgetConstructionAppointmentFunding createNewAppointmentFundingLine(DetailSalarySettingForm salarySettingForm) {
        PendingBudgetConstructionAppointmentFunding appointmentFunding = new PendingBudgetConstructionAppointmentFunding();
        
        appointmentFunding.setUniversityFiscalYear(salarySettingForm.getUniversityFiscalYear());
        
        appointmentFunding.setAppointmentFundingDeleteIndicator(false);
        appointmentFunding.setAppointmentFundingDurationCode(BCConstants.APPOINTMENT_FUNDING_DURATION_DEFAULT);
        
        appointmentFunding.setAppointmentRequestedAmount(KualiInteger.ZERO);
        appointmentFunding.setAppointmentRequestedFteQuantity(BigDecimal.ZERO.setScale(5, KualiDecimal.ROUND_BEHAVIOR));
        appointmentFunding.setAppointmentRequestedTimePercent(BigDecimal.ZERO.setScale(2, KualiDecimal.ROUND_BEHAVIOR));
        appointmentFunding.setAppointmentRequestedPayRate(BigDecimal.ZERO.setScale(2, KualiDecimal.ROUND_BEHAVIOR));
        
        appointmentFunding.setAppointmentRequestedCsfAmount(KualiInteger.ZERO);
        appointmentFunding.setAppointmentRequestedCsfFteQuantity(BigDecimal.ZERO.setScale(5, KualiDecimal.ROUND_BEHAVIOR));
        appointmentFunding.setAppointmentRequestedCsfTimePercent(BigDecimal.ZERO.setScale(2, KualiDecimal.ROUND_BEHAVIOR));
        
        appointmentFunding.setAppointmentTotalIntendedAmount(KualiInteger.ZERO);
        appointmentFunding.setAppointmentTotalIntendedFteQuantity(BigDecimal.ZERO.setScale(5, KualiDecimal.ROUND_BEHAVIOR));
        
        return appointmentFunding;
    }
}
