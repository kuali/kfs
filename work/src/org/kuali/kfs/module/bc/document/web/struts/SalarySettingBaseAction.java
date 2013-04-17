/*
 * Copyright 2008 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.bc.document.web.struts;

import java.text.MessageFormat;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.fp.service.FiscalYearFunctionControlService;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.BCKeyConstants;
import org.kuali.kfs.module.bc.BCPropertyConstants;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionAuthorizationStatus;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.document.BudgetConstructionDocument;
import org.kuali.kfs.module.bc.document.service.BudgetDocumentService;
import org.kuali.kfs.module.bc.document.service.SalarySettingService;
import org.kuali.kfs.module.bc.document.validation.event.AdjustSalarySettingLinePercentEvent;
import org.kuali.kfs.module.bc.document.validation.event.BudgetExpansionEvent;
import org.kuali.kfs.module.bc.document.validation.event.NormalizePayrateAndAmountEvent;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.services.IdentityManagementService;
import org.kuali.rice.kns.question.ConfirmationQuestion;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.krad.exception.AuthorizationException;
import org.kuali.rice.krad.rules.rule.event.KualiDocumentEvent;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.KualiRuleService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * the base action class for salary setting, which provides the implementations of common actions of the salary setting screens
 */
public abstract class SalarySettingBaseAction extends BudgetExpansionAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SalarySettingBaseAction.class);

    protected SalarySettingService salarySettingService = SpringContext.getBean(SalarySettingService.class);
    protected BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
    protected ConfigurationService kualiConfiguration = SpringContext.getBean(ConfigurationService.class);
    protected BudgetDocumentService budgetDocumentService = SpringContext.getBean(BudgetDocumentService.class);
    protected KualiRuleService kualiRuleService = SpringContext.getBean(KualiRuleService.class);

    /**
     * loads the data for the expansion screen based on the passed in url parameters
     */
    public abstract ActionForward loadExpansionScreen(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception;

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiAction#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        SalarySettingBaseForm salarySettingForm = (SalarySettingBaseForm) form;

        // if org sal setting we need to initialize authorization
        if (!salarySettingForm.isBudgetByAccountMode() && salarySettingForm.getMethodToCall() != null && salarySettingForm.getMethodToCall().equals(BCConstants.LOAD_EXPANSION_SCREEN_METHOD)) {
            initAuthorization(salarySettingForm);
        }

        populateAuthorizationFields(salarySettingForm);

        ActionForward forward = super.execute(mapping, form, request, response);
        salarySettingForm.postProcessBCAFLines();

        // re-init the session form if session scoped
        if (salarySettingForm.getMethodToCall().equals("refresh")) {
            if (BCConstants.MAPPING_SCOPE_SESSION.equals(mapping.getScope())) {
                HttpSession sess = request.getSession(Boolean.FALSE);
                String formName = mapping.getAttribute();
                sess.setAttribute(formName, salarySettingForm);
            }
        }
        return forward;
    }

    protected void populateAuthorizationFields(SalarySettingBaseForm salarySettingForm) {
        BudgetConstructionAuthorizationStatus authorizationStatus = (BudgetConstructionAuthorizationStatus) GlobalVariables.getUserSession().retrieveObject(BCConstants.BC_DOC_AUTHORIZATION_STATUS_SESSIONKEY);

        if (authorizationStatus == null) {
            // just return, BudgetExpansionAction.execute() will see the session time out
            // and redirect back to BudgetConstructionSelection
            return;
        }

        salarySettingForm.setDocumentActions(authorizationStatus.getDocumentActions());
        salarySettingForm.setEditingMode(authorizationStatus.getEditingMode());
    }

    protected void initAuthorization(SalarySettingBaseForm salarySettingForm) {
        Person user = GlobalVariables.getUserSession().getPerson();

        boolean isAuthorized = SpringContext.getBean(IdentityManagementService.class).isAuthorized(user.getPrincipalId(), BCConstants.BUDGET_CONSTRUCTION_NAMESPACE, BCConstants.KimApiConstants.USE_ORG_SALARY_SETTING_PERMISSION_NAME, null);
        if (isAuthorized) {
            salarySettingForm.getDocumentActions().put(KRADConstants.KUALI_ACTION_CAN_EDIT, KRADConstants.KUALI_DEFAULT_TRUE_VALUE);
        }
        else {
            throw new AuthorizationException(user.getName(), "view", salarySettingForm.getAccountNumber() + ", " + salarySettingForm.getSubAccountNumber());
        }

        if (!SpringContext.getBean(FiscalYearFunctionControlService.class).isBudgetUpdateAllowed(salarySettingForm.getUniversityFiscalYear())) {
            salarySettingForm.getEditingMode().put(BCConstants.EditModes.SYSTEM_VIEW_ONLY, KRADConstants.KUALI_DEFAULT_TRUE_VALUE);
        }

        BudgetConstructionAuthorizationStatus editStatus = new BudgetConstructionAuthorizationStatus();
        editStatus.setDocumentActions(salarySettingForm.getDocumentActions());
        editStatus.setEditingMode(salarySettingForm.getEditingMode());

        GlobalVariables.getUserSession().addObject(BCConstants.BC_DOC_AUTHORIZATION_STATUS_SESSIONKEY, editStatus);
    }

    /**
     * save the information in the current form into underlying data store
     */
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_MESSAGES, KFSKeyConstants.ERROR_UNIMPLEMENTED, "Save For Salary Setting by Incumbent");
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.web.struts.BudgetExpansionAction#close(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward close(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        SalarySettingBaseForm salarySettingForm = (SalarySettingBaseForm) form;

        // ask a question before closing unless it has been answered
        String question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);
        if (StringUtils.isBlank(question)) {
            String questionText = kualiConfiguration.getPropertyValueAsString(KFSKeyConstants.QUESTION_SAVE_BEFORE_CLOSE);
            return this.performQuestionWithoutInput(mapping, salarySettingForm, request, response, KFSConstants.DOCUMENT_SAVE_BEFORE_CLOSE_QUESTION, questionText, KFSConstants.CONFIRMATION_QUESTION, KFSConstants.MAPPING_CLOSE, "");
        }

        // save the salary setting if the user answers to the question with "Yes" (save and close)
        String buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);
        if (StringUtils.equals(KFSConstants.DOCUMENT_SAVE_BEFORE_CLOSE_QUESTION, question) && StringUtils.equals(ConfirmationQuestion.YES, buttonClicked)) {
            ActionForward saveAction = this.save(mapping, salarySettingForm, request, response);

            return saveAction;
        }

        // indicate the salary setting has been closed
        salarySettingForm.setSalarySettingClosed(true);

        return this.returnAfterClose(salarySettingForm, mapping, request, response);
    }

    /**
     * vacate the specified appointment funding line
     */
    public ActionForward vacateSalarySettingLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        SalarySettingBaseForm salarySettingForm = (SalarySettingBaseForm) form;
        List<PendingBudgetConstructionAppointmentFunding> appointmentFundings = salarySettingForm.getAppointmentFundings();
        PendingBudgetConstructionAppointmentFunding appointmentFunding = this.getSelectedFundingLine(request, salarySettingForm);

        salarySettingService.vacateAppointmentFunding(appointmentFundings, appointmentFunding);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * mark the selected salary setting line as purged
     */
    public ActionForward purgeSalarySettingLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        SalarySettingBaseForm salarySettingForm = (SalarySettingBaseForm) form;

        this.getSelectedFundingLine(request, salarySettingForm).setPurged(true);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * restore the selected salary setting line if it is marked as purged
     */
    public ActionForward restorePurgedSalarySettingLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        SalarySettingBaseForm salarySettingForm = (SalarySettingBaseForm) form;

        this.getSelectedFundingLine(request, salarySettingForm).setPurged(false);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * mark the selected salary setting line as deleted
     */
    public ActionForward deleteSalarySettingLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        SalarySettingBaseForm salarySettingForm = (SalarySettingBaseForm) form;
        PendingBudgetConstructionAppointmentFunding appointmentFunding = this.getSelectedFundingLine(request, salarySettingForm);

        salarySettingService.markAsDelete(appointmentFunding);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * unmark the selected salary setting line that has been marked as deleted
     */
    public ActionForward undeleteSalarySettingLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        SalarySettingBaseForm salarySettingForm = (SalarySettingBaseForm) form;

        this.getSelectedFundingLine(request, salarySettingForm).setAppointmentFundingDeleteIndicator(false);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * revert the selected salary setting line that just has been marked as deleted
     */
    public ActionForward revertSalarySettingLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        SalarySettingBaseForm salarySettingForm = (SalarySettingBaseForm) form;
        List<PendingBudgetConstructionAppointmentFunding> appointmentFundings = salarySettingForm.getAppointmentFundings();
        PendingBudgetConstructionAppointmentFunding appointmentFunding = this.getSelectedFundingLine(request, salarySettingForm);

        salarySettingService.revert(appointmentFundings, appointmentFunding);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * adjust the salary amount of the specified funding line
     */
    public ActionForward adjustSalarySettingLinePercent(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        SalarySettingBaseForm salarySettingForm = (SalarySettingBaseForm) form;
        PendingBudgetConstructionAppointmentFunding appointmentFunding = this.getSelectedFundingLine(request, salarySettingForm);
        List<PendingBudgetConstructionAppointmentFunding> appointmentFundings = salarySettingForm.getAppointmentFundings();

        String errorKeyPrefix = this.getErrorKeyPrefixOfAppointmentFundingLine(appointmentFundings, appointmentFunding);

        // retrieve corresponding document in advance in order to use the rule framework
        BudgetConstructionDocument document = budgetDocumentService.getBudgetConstructionDocument(appointmentFunding);
        if (document == null) {
            GlobalVariables.getMessageMap().putError(errorKeyPrefix, BCKeyConstants.ERROR_BUDGET_DOCUMENT_NOT_FOUND, appointmentFunding.getAppointmentFundingString());
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        return this.adjustSalarySettingLinePercent(mapping, salarySettingForm, appointmentFunding, document, errorKeyPrefix);
    }

    /**
     * adjust the requested salary amount of the given appointment funding line by pecent or given amount
     */
    public ActionForward adjustSalarySettingLinePercent(ActionMapping mapping, ActionForm form, PendingBudgetConstructionAppointmentFunding appointmentFunding, BudgetConstructionDocument document, String errorKeyPrefix) {
        SalarySettingBaseForm salarySettingForm = (SalarySettingBaseForm) form;

        // validate the new appointment funding line
        BudgetExpansionEvent adjustPercentEvent = new AdjustSalarySettingLinePercentEvent(KFSConstants.EMPTY_STRING, errorKeyPrefix, document, appointmentFunding);
        boolean isValid = this.invokeRules(adjustPercentEvent);
        if (!isValid) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        this.adjustSalary(appointmentFunding);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * adjust the requested salary amount of the given appointment funding line
     */
    protected void adjustSalary(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        String adjustmentMeasurement = appointmentFunding.getAdjustmentMeasurement();
        if (BCConstants.SalaryAdjustmentMeasurement.PERCENT.measurement.equals(adjustmentMeasurement)) {
            salarySettingService.adjustRequestedSalaryByPercent(appointmentFunding);
        }
        else if (BCConstants.SalaryAdjustmentMeasurement.AMOUNT.measurement.equals(adjustmentMeasurement)) {
            salarySettingService.adjustRequestedSalaryByAmount(appointmentFunding);
        }
    }

    /**
     * normalize the hourly pay rate and annual pay amount
     */
    public ActionForward normalizePayRateAndAmount(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        SalarySettingBaseForm salarySettingForm = (SalarySettingBaseForm) form;
        PendingBudgetConstructionAppointmentFunding appointmentFunding = this.getSelectedFundingLine(request, salarySettingForm);
        List<PendingBudgetConstructionAppointmentFunding> appointmentFundings = salarySettingForm.getAppointmentFundings();
        String errorKeyPrefix = this.getErrorKeyPrefixOfAppointmentFundingLine(appointmentFundings, appointmentFunding);

        // retrieve corresponding document in advance in order to use the rule framework
        BudgetConstructionDocument document = budgetDocumentService.getBudgetConstructionDocument(appointmentFunding);
        if (document == null) {
            GlobalVariables.getMessageMap().putError(errorKeyPrefix, BCKeyConstants.ERROR_BUDGET_DOCUMENT_NOT_FOUND, appointmentFunding.getAppointmentFundingString());
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        // validate the new appointment funding line
        BudgetExpansionEvent normalizePayRateAndAmountEvent = new NormalizePayrateAndAmountEvent(KFSConstants.EMPTY_STRING, errorKeyPrefix, document, appointmentFunding);
        boolean isValid = this.invokeRules(normalizePayRateAndAmountEvent);
        if (!isValid) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        salarySettingService.normalizePayRateAndAmount(appointmentFunding);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * get the selected appointment funding line
     */
    protected PendingBudgetConstructionAppointmentFunding getSelectedFundingLine(HttpServletRequest request, SalarySettingBaseForm salarySettingForm) {
        List<PendingBudgetConstructionAppointmentFunding> appointmentFundings = salarySettingForm.getAppointmentFundings();

        int indexOfSelectedLine = this.getSelectedLine(request);
        return appointmentFundings.get(indexOfSelectedLine);
    }

    /**
     * execute the rules associated with the given event
     *
     * @param event the event that just occured
     * @return true if the rules associated with the given event pass; otherwise, false
     */
    protected boolean invokeRules(KualiDocumentEvent event) {
        return kualiRuleService.applyRules(event);
    }

    /**
     * build the error key prefix based on the given information
     *
     * @param fundingAwareObjectName the name of object that holds the given set of appointment funding lines
     * @param appointmentFundings the given set of appointment funding lines
     * @param appointmentFunding the given appointment funding line
     * @return the error key prefix built from the given information
     */
    protected String getErrorKeyPrefixOfAppointmentFundingLine(List<PendingBudgetConstructionAppointmentFunding> appointmentFundings, PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        int indexOfFundingLine = appointmentFundings.indexOf(appointmentFunding);
        String pattern = "{0}.{1}[{2}]";

        return MessageFormat.format(pattern, this.getFundingAwareObjectName(), BCPropertyConstants.PENDING_BUDGET_CONSTRUCTION_APPOINTMENT_FUNDING, indexOfFundingLine);
    }

    /**
     * return after salary setting is closed
     */
    protected ActionForward returnAfterClose(SalarySettingBaseForm salarySettingForm, ActionMapping mapping, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (salarySettingForm.isBudgetByAccountMode()) {
            salarySettingForm.getCallBackMessages().add(BCKeyConstants.MESSAGE_BUDGET_SUCCESSFUL_CLOSE);
            return this.returnToCaller(mapping, salarySettingForm, request, response);
        }

        this.cleanupAnySessionForm(mapping, request);
        KNSGlobalVariables.getMessageList().add(BCKeyConstants.MESSAGE_BUDGET_SUCCESSFUL_CLOSE);
        return mapping.findForward(BCConstants.MAPPING_ORGANIZATION_SALARY_SETTING_RETURNING);
    }

    /**
     * get the name of object that holds a set of appointment funding lines
     */
    protected abstract String getFundingAwareObjectName();
}
