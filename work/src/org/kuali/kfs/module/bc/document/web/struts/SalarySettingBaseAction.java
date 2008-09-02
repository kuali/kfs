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
package org.kuali.kfs.module.bc.document.web.struts;

import java.text.MessageFormat;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.BCKeyConstants;
import org.kuali.kfs.module.bc.BCPropertyConstants;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.document.BudgetConstructionDocument;
import org.kuali.kfs.module.bc.document.authorization.BudgetConstructionDocumentAuthorizer;
import org.kuali.kfs.module.bc.document.service.BudgetDocumentService;
import org.kuali.kfs.module.bc.document.service.SalarySettingService;
import org.kuali.kfs.module.bc.document.validation.event.AdjustSalarySettingLinePercentEvent;
import org.kuali.kfs.module.bc.document.validation.event.BudgetExpansionEvent;
import org.kuali.kfs.module.bc.document.validation.event.NormalizePayrateAndAmountEvent;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.authorization.AuthorizationType;
import org.kuali.rice.kns.bo.user.UniversalUser;
import org.kuali.rice.kns.exception.AuthorizationException;
import org.kuali.rice.kns.exception.ModuleAuthorizationException;
import org.kuali.rice.kns.question.ConfirmationQuestion;
import org.kuali.rice.kns.rule.event.KualiDocumentEvent;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.KualiRuleService;
import org.kuali.rice.kns.service.ModuleService;
import org.kuali.rice.kns.util.ErrorMap;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KualiDecimal;
import org.kuali.rice.kns.util.ObjectUtils;

/**
 * the base action class for salary setting, which provides the implementations of common actions of the salary setting screens
 */
public abstract class SalarySettingBaseAction extends BudgetExpansionAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SalarySettingBaseAction.class);

    private SalarySettingService salarySettingService = SpringContext.getBean(SalarySettingService.class);
    private BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
    private KualiConfigurationService kualiConfiguration = SpringContext.getBean(KualiConfigurationService.class);
    private BudgetDocumentService budgetDocumentService = SpringContext.getBean(BudgetDocumentService.class);
    private KualiRuleService kualiRuleService = SpringContext.getBean(KualiRuleService.class);

    private List<String> messageList = GlobalVariables.getMessageList();
    private ErrorMap errorMap = GlobalVariables.getErrorMap();

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
        salarySettingForm.populateAuthorizationFields(new BudgetConstructionDocumentAuthorizer());

        ActionForward forward = super.execute(mapping, form, request, response);

        boolean isSuccessfullyProcessed = salarySettingForm.postProcessBCAFLines();
        if (!isSuccessfullyProcessed) {
            return this.returnToCaller(mapping, form, request, response);
        }

        return forward;
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiAction#checkAuthorization(org.apache.struts.action.ActionForm,
     *      java.lang.String)
     */
    @Override
    public void checkAuthorization(ActionForm form, String methodToCall) throws AuthorizationException {
        UniversalUser currentUser = GlobalVariables.getUserSession().getUniversalUser();
        AuthorizationType bcAuthorizationType = new AuthorizationType.Default(this.getClass());

        if (!getKualiModuleService().isAuthorized(currentUser, bcAuthorizationType)) {
            LOG.error("User not authorized to use this action: " + this.getClass().getName());

            ModuleService module = getKualiModuleService().getResponsibleModuleService(this.getClass());
            throw new ModuleAuthorizationException(currentUser.getPersonUserIdentifier(), bcAuthorizationType, module);
        }
    }

    /**
     * save the information in the current form into underlying data store
     */
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        errorMap.putError(KFSConstants.GLOBAL_MESSAGES, KFSKeyConstants.ERROR_UNIMPLEMENTED, "Save For Salary Setting by Incumbent");
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.web.struts.BudgetExpansionAction#close(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward close(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        SalarySettingBaseForm salarySettingForm = (SalarySettingBaseForm) form;
        
        // return to the calller directly if the current user just can have view-only access
        if (salarySettingForm.isViewOnlyEntry()) {
            messageList.add(BCKeyConstants.MESSAGE_BUDGET_SUCCESSFUL_CLOSE);
            return this.returnToCaller(mapping, form, request, response);
        }

        // ask a question before closing unless it has been answered
        String question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);
        if (StringUtils.isBlank(question)) {
            String questionText = kualiConfiguration.getPropertyString(KFSKeyConstants.QUESTION_SAVE_BEFORE_CLOSE);
            return this.performQuestionWithoutInput(mapping, form, request, response, KFSConstants.DOCUMENT_SAVE_BEFORE_CLOSE_QUESTION, questionText, KFSConstants.CONFIRMATION_QUESTION, KFSConstants.MAPPING_CLOSE, "");
        }

        // save the salary setting if the user answers to the question with "Yes" (save and close)
        String buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);
        if (StringUtils.equals(KFSConstants.DOCUMENT_SAVE_BEFORE_CLOSE_QUESTION, question) && StringUtils.equals(ConfirmationQuestion.YES, buttonClicked)) {
            ActionForward saveAction = this.save(mapping, form, request, response);

            if (!messageList.contains(BCKeyConstants.MESSAGE_SALARY_SETTING_SAVED)) {
                messageList.add(BCKeyConstants.MESSAGE_SALARY_SETTING_SAVED);
            }
            
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        messageList.add(BCKeyConstants.MESSAGE_BUDGET_SUCCESSFUL_CLOSE);
        return this.returnToCaller(mapping, form, request, response);
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

        return this.adjustSalarySettingLinePercent(mapping, salarySettingForm, appointmentFunding);
    }

    /**
     * adjust the salary amounts of all funding lines
     */
    public ActionForward adjustAllSalarySettingLinesPercent(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        SalarySettingBaseForm salarySettingForm = (SalarySettingBaseForm) form;
        List<PendingBudgetConstructionAppointmentFunding> appointmentFundings = salarySettingForm.getAppointmentFundings();

        KualiDecimal adjustmentAmount = salarySettingForm.getAdjustmentAmount();
        String adjustmentMeasurement = salarySettingForm.getAdjustmentMeasurement();

        // the adjustment measurement and amount must be provided
        if (StringUtils.isBlank(adjustmentMeasurement)) {
            errorMap.putError(BCPropertyConstants.ADJUSTMENT_MEASUREMENT, BCKeyConstants.ERROR_ADJUSTMENT_PERCENT_REQUIRED);
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        if (ObjectUtils.isNull(adjustmentAmount)) {
            errorMap.putError(BCPropertyConstants.ADJUSTMENT_AMOUNT, BCKeyConstants.ERROR_ADJUSTMENT_AMOUNT_REQUIRED);
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        for (PendingBudgetConstructionAppointmentFunding appointmentFunding : appointmentFundings) {
            appointmentFunding.setAdjustmentAmount(adjustmentAmount);
            appointmentFunding.setAdjustmentMeasurement(adjustmentMeasurement);

            ActionForward adjustAction = this.adjustSalarySettingLinePercent(mapping, salarySettingForm, appointmentFunding);
            if (!errorMap.isEmpty()) {
                return adjustAction;
            }
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * adjust the requested salary amount of the given appointment funding line by pecent or given amount
     */
    public ActionForward adjustSalarySettingLinePercent(ActionMapping mapping, ActionForm form, PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        SalarySettingBaseForm salarySettingForm = (SalarySettingBaseForm) form;
        List<PendingBudgetConstructionAppointmentFunding> appointmentFundings = salarySettingForm.getAppointmentFundings();
        String errorKeyPrefix = this.getErrorKeyPrefixOfAppointmentFundingLine(appointmentFundings, appointmentFunding);

        // retrieve corresponding document in advance in order to use the rule framework
        BudgetConstructionDocument document = budgetDocumentService.getBudgetConstructionDocument(appointmentFunding);
        if (document == null) {
            errorMap.putError(errorKeyPrefix, BCKeyConstants.ERROR_BUDGET_DOCUMENT_NOT_FOUND, appointmentFunding.getAppointmentFundingString());
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

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
            errorMap.putError(errorKeyPrefix, BCKeyConstants.ERROR_BUDGET_DOCUMENT_NOT_FOUND, appointmentFunding.getAppointmentFundingString());
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
     * get the name of object that holds a set of appointment funding lines
     */
    protected abstract String getFundingAwareObjectName();
}
