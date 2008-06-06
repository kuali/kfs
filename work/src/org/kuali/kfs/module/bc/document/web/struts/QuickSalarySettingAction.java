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
package org.kuali.module.budget.web.struts.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.authorization.AuthorizationConstants;
import org.kuali.core.authorization.AuthorizationType;
import org.kuali.core.exceptions.AuthorizationException;
import org.kuali.core.exceptions.ModuleAuthorizationException;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.KualiModuleService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.UrlFactory;
import org.kuali.core.web.struts.action.KualiAction;
import org.kuali.core.web.struts.form.KualiForm;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.budget.BCConstants;
import org.kuali.module.budget.bo.PendingBudgetConstructionAppointmentFunding;
import org.kuali.module.budget.bo.SalarySettingExpansion;
import org.kuali.module.budget.document.authorization.BudgetConstructionDocumentAuthorizer;
import org.kuali.module.budget.service.SalarySettingService;
import org.kuali.module.budget.web.struts.form.QuickSalarySettingForm;

public class QuickSalarySettingAction extends KualiAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(QuickSalarySettingAction.class);

    /**
     * @see org.kuali.core.web.struts.action.KualiAction#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward forward = super.execute(mapping, form, request, response);

        QuickSalarySettingForm salarySettingForm = (QuickSalarySettingForm) form;

        // TODO should not need to handle optimistic lock exception here (like KualiDocumentActionBase)
        // since BC sets locks up front, but need to verify this

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

        AuthorizationType bcAuthorizationType = new AuthorizationType.Default(this.getClass());
        if (!SpringContext.getBean(KualiModuleService.class).isAuthorized(GlobalVariables.getUserSession().getUniversalUser(), bcAuthorizationType)) {
            LOG.error("User not authorized to use this action: " + this.getClass().getName());
            throw new ModuleAuthorizationException(GlobalVariables.getUserSession().getUniversalUser().getPersonUserIdentifier(), bcAuthorizationType, getKualiModuleService().getResponsibleModule(this.getClass()));
        }
    }

    /**
     * @see org.kuali.core.web.struts.action.KualiAction#refresh(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        QuickSalarySettingForm salarySettingForm = (QuickSalarySettingForm) form;
        String refreshCaller = request.getParameter(KFSConstants.REFRESH_CALLER);

        if (StringUtils.equals(BCConstants.POSITION_SALARY_SETTING_REFRESH_CALLER, refreshCaller)) {
            // TODO do things specific to returning from Position Salary Setting
        }

        if (StringUtils.equals(BCConstants.INCUMBENT_SALARY_SETTING_REFRESH_CALLER, refreshCaller)) {
            // TODO do things specific to returning from Position Salary Setting
        }
        
        salarySettingForm.populateBCAFLines();

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward performAddIncumbent(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        QuickSalarySettingForm tForm = (QuickSalarySettingForm) form;
        GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_MESSAGES, KFSKeyConstants.ERROR_UNIMPLEMENTED, "Add Incumbent");

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward performAddPosition(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        QuickSalarySettingForm tForm = (QuickSalarySettingForm) form;
        GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_MESSAGES, KFSKeyConstants.ERROR_UNIMPLEMENTED, "Add Position");

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward performAdjustmentSalarySettingLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        QuickSalarySettingForm tForm = (QuickSalarySettingForm) form;
        GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_MESSAGES, KFSKeyConstants.ERROR_UNIMPLEMENTED, "Incumbent Salary Setting");

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward performIncumbentSalarySettingLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        QuickSalarySettingForm tForm = (QuickSalarySettingForm) form;
        GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_MESSAGES, KFSKeyConstants.ERROR_UNIMPLEMENTED, "Incumbent Salary Setting");

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * load the quick salary setting screen
     */
    public ActionForward loadExpansionScreen(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        QuickSalarySettingForm salarySettingForm = (QuickSalarySettingForm) form;

        // use the passed url parms to get the record from DB
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(KFSPropertyConstants.DOCUMENT_NUMBER, salarySettingForm.getDocumentNumber());
        fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, salarySettingForm.getUniversityFiscalYear());
        fieldValues.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, salarySettingForm.getChartOfAccountsCode());
        fieldValues.put(KFSPropertyConstants.ACCOUNT_NUMBER, salarySettingForm.getAccountNumber());
        fieldValues.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, salarySettingForm.getSubAccountNumber());
        fieldValues.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, salarySettingForm.getFinancialObjectCode());
        fieldValues.put(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, salarySettingForm.getFinancialSubObjectCode());
        fieldValues.put(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, salarySettingForm.getFinancialBalanceTypeCode());
        fieldValues.put(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE, salarySettingForm.getFinancialObjectTypeCode());

        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        SalarySettingExpansion pendingBudgetConstructionGeneralLedger = (SalarySettingExpansion) businessObjectService.findByPrimaryKey(SalarySettingExpansion.class, fieldValues);

        if (pendingBudgetConstructionGeneralLedger == null) {
            // TODO need to figure out what to do (if anything) under edit and view mode cases
            // probably nothing, the create new by incumbent or position links would still be shown in edit mode
        }

        salarySettingForm.setPendingBudgetConstructionGeneralLedger(pendingBudgetConstructionGeneralLedger);

        // refresh references and recalulate the totals of the appointment funding lines
        salarySettingForm.populateBCAFLines();

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * return to the caller of the current action
     */
    public ActionForward returnToCaller(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        QuickSalarySettingForm salarySettingForm = (QuickSalarySettingForm) form;

        // setup the return parms for the document and anchor
        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, BCConstants.BC_DOCUMENT_REFRESH_METHOD);
        parameters.put(KFSConstants.DOC_FORM_KEY, salarySettingForm.getReturnFormKey());
        parameters.put(KFSConstants.ANCHOR, salarySettingForm.getReturnAnchor());
        parameters.put(KFSConstants.REFRESH_CALLER, BCConstants.SALARY_SETTING_REFRESH_CALLER);

        String returningURL = UrlFactory.parameterizeUrl("/" + BCConstants.BC_DOCUMENT_ACTION, parameters);
        return new ActionForward(returningURL, true);
    }

    /**
     * vacate the specified appointment funding line
     */
    public ActionForward performVacateSalarySettingLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        QuickSalarySettingForm salarySettingForm = (QuickSalarySettingForm) form;
        SalarySettingExpansion salarySettingExpansion = salarySettingForm.getPendingBudgetConstructionGeneralLedger();

        // retrieve the selected funding line
        int indexOfSelectedLine = this.getSelectedLine(request);
        List<PendingBudgetConstructionAppointmentFunding> appointmentFundings = salarySettingExpansion.getPendingBudgetConstructionAppointmentFunding();
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
     * adjust the salary amount of the specified funding line
     */
    public ActionForward performPercentAdjustmentSalarySettingLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        QuickSalarySettingForm salarySettingForm = (QuickSalarySettingForm) form;
        SalarySettingExpansion salarySettingExpansion = salarySettingForm.getPendingBudgetConstructionGeneralLedger();

        // retrieve the selected funding line
        int indexOfSelectedLine = this.getSelectedLine(request);
        List<PendingBudgetConstructionAppointmentFunding> appointmentFundings = salarySettingExpansion.getPendingBudgetConstructionAppointmentFunding();
        PendingBudgetConstructionAppointmentFunding appointmentFunding = appointmentFundings.get(indexOfSelectedLine);

        this.adjustSalary(appointmentFunding);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * adjust the salary amounts of all funding lines
     */
    public ActionForward performPercentAdjustmentAllSalarySettingLines(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        QuickSalarySettingForm salarySettingForm = (QuickSalarySettingForm) form;
        SalarySettingExpansion salarySettingExpansion = salarySettingForm.getPendingBudgetConstructionGeneralLedger();

        List<PendingBudgetConstructionAppointmentFunding> appointmentFundings = salarySettingExpansion.getPendingBudgetConstructionAppointmentFunding();
        KualiDecimal adjustmentAmount = salarySettingForm.getAdjustmentAmount();
        String adjustmentMeasurement = salarySettingForm.getAdjustmentMeasurement();

        for (PendingBudgetConstructionAppointmentFunding appointmentFunding : appointmentFundings) {
            Object fullEntryEditMode = salarySettingForm.getEditingMode().get(AuthorizationConstants.EditMode.FULL_ENTRY);
            boolean isEditable = fullEntryEditMode != null && Boolean.parseBoolean(fullEntryEditMode.toString());

            if (isEditable) {
                appointmentFunding.setAdjustmentAmount(adjustmentAmount);
                appointmentFunding.setAdjustmentMeasurement(adjustmentMeasurement);

                this.adjustSalary(appointmentFunding);
            }
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * perform salary setting by incumbent with the specified funding line
     */
    public ActionForward performIncumbentSalarySetting(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String salarySettingURL = this.buildSalarySettingURL(form, request, BCConstants.INCUMBENT_SALARY_SETTING_ACTION);

        return new ActionForward(salarySettingURL, true);
    }

    /**
     * perform salary setting by position with the specified funding line
     */
    public ActionForward performPositionSalarySetting(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String salarySettingURL = this.buildSalarySettingURL(form, request, BCConstants.POSITION_SALARY_SETTING_ACTION);

        return new ActionForward(salarySettingURL, true);
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

    // build the URL for the specified salary setting method
    private String buildSalarySettingURL(ActionForm form, HttpServletRequest request, String salarySettingAction) {
        QuickSalarySettingForm salarySettingForm = (QuickSalarySettingForm) form;
        SalarySettingExpansion salarySettingExpansion = salarySettingForm.getPendingBudgetConstructionGeneralLedger();

        Map<String, String> salarySettingMethodAction = this.getSalarySettingMethodActionInfo();

        int indexOfSelectedLine = this.getSelectedLine(request);
        List<PendingBudgetConstructionAppointmentFunding> appointmentFundings = salarySettingExpansion.getPendingBudgetConstructionAppointmentFunding();
        PendingBudgetConstructionAppointmentFunding appointmentFunding = appointmentFundings.get(indexOfSelectedLine);

        // get the base action
        String basePath = SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSConstants.APPLICATION_URL_KEY);
        String baseAction = basePath + "/" + salarySettingAction;
        String methodToCall = salarySettingMethodAction.get(salarySettingAction);

        // build the query strings with the information of the selected line
        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, methodToCall);
        parameters.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, appointmentFunding.getUniversityFiscalYear().toString());
        parameters.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, appointmentFunding.getChartOfAccountsCode());
        parameters.put(KFSPropertyConstants.ACCOUNT_NUMBER, appointmentFunding.getAccountNumber());
        parameters.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, appointmentFunding.getSubAccountNumber());
        parameters.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, appointmentFunding.getFinancialObjectCode());
        parameters.put(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, appointmentFunding.getFinancialSubObjectCode());
        parameters.put(KFSPropertyConstants.POSITION_NUMBER, appointmentFunding.getPositionNumber());
        parameters.put(KFSPropertyConstants.EMPLID, appointmentFunding.getEmplid());
        parameters.put(BCConstants.BUDGET_BY_ACCOUNT_MODE, Boolean.TRUE.toString());
        parameters.put(KFSConstants.ADD_LINE_METHOD, Boolean.FALSE.toString());

        // anchor, if it exists
        if (form instanceof KualiForm && StringUtils.isNotEmpty(salarySettingForm.getAnchor())) {
            parameters.put(BCConstants.RETURN_ANCHOR, salarySettingForm.getAnchor());
        }

        // the form object is retrieved and removed upon return by KualiRequestProcessor.processActionForm()
        parameters.put(BCConstants.RETURN_FORM_KEY, GlobalVariables.getUserSession().addObject(form, BCConstants.FORMKEY_PREFIX));

        return UrlFactory.parameterizeUrl(baseAction, parameters);
    }

    // get the pairs of the method and action of salary settings
    private Map<String, String> getSalarySettingMethodActionInfo() {
        Map<String, String> salarySettingMethodAction = new HashMap<String, String>();
        salarySettingMethodAction.put(BCConstants.INCUMBENT_SALARY_SETTING_ACTION, BCConstants.INCUMBENT_SALARY_SETTING_METHOD);
        salarySettingMethodAction.put(BCConstants.POSITION_SALARY_SETTING_ACTION, BCConstants.POSITION_SALARY_SETTING_METHOD);

        return salarySettingMethodAction;
    }
}
