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
package org.kuali.kfs.module.bc.document.web.struts;

import java.math.BigDecimal;
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
import org.kuali.core.util.KualiInteger;
import org.kuali.core.util.UrlFactory;
import org.kuali.core.web.struts.form.KualiForm;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.BCKeyConstants;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.businessobject.SalarySettingExpansion;
import org.kuali.kfs.module.bc.document.service.SalarySettingService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;

public class QuickSalarySettingAction extends BudgetExpansionAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(QuickSalarySettingAction.class);

    private SalarySettingService salarySettingService = SpringContext.getBean(SalarySettingService.class);
    private BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);

    /**
     * @see org.kuali.core.web.struts.action.KualiAction#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward forward = super.execute(mapping, form, request, response);

        // TODO should not need to handle optimistic lock exception here (like KualiDocumentActionBase)
        // since BC sets locks up front, but need to verify this

        // TODO should probably use service locator and call
        // DocumentAuthorizer documentAuthorizer =
        // SpringContext.getBean(DocumentAuthorizationService.class).getDocumentAuthorizer("<BCDoctype>");

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

    public ActionForward addIncumbent(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        QuickSalarySettingForm tForm = (QuickSalarySettingForm) form;
        GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_MESSAGES, KFSKeyConstants.ERROR_UNIMPLEMENTED, "Add Incumbent");

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    public ActionForward addPosition(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {

        QuickSalarySettingForm tForm = (QuickSalarySettingForm) form;
        GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_MESSAGES, KFSKeyConstants.ERROR_UNIMPLEMENTED, "Add Position");

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * load the quick salary setting screen
     */
    public ActionForward loadExpansionScreen(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        QuickSalarySettingForm salarySettingForm = (QuickSalarySettingForm) form;

        // use the passed url parms to get the record from DB
        Map<String, Object> keyMap = salarySettingForm.getKeyMapOfSalarySettingExpension();

        SalarySettingExpansion salarySettingExpansion = (SalarySettingExpansion) businessObjectService.findByPrimaryKey(SalarySettingExpansion.class, keyMap);

        if (salarySettingExpansion == null) {
            // TODO need to figure out what to do (if anything) under edit and view mode cases
            // probably nothing, the create new by incumbent or position links would still be shown in edit mode
        }

        salarySettingForm.setSalarySettingExpansion(salarySettingExpansion);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * vacate the specified appointment funding line
     */
    public ActionForward vacateSalarySettingLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        QuickSalarySettingForm salarySettingForm = (QuickSalarySettingForm) form;
        SalarySettingExpansion salarySettingExpansion = salarySettingForm.getSalarySettingExpansion();

        // retrieve the selected funding line
        int indexOfSelectedLine = this.getSelectedLine(request);
        List<PendingBudgetConstructionAppointmentFunding> appointmentFundings = salarySettingExpansion.getPendingBudgetConstructionAppointmentFunding();
        PendingBudgetConstructionAppointmentFunding appointmentFunding = appointmentFundings.get(indexOfSelectedLine);

        // associated the vacant funding line with current salary setting expansion
        PendingBudgetConstructionAppointmentFunding vacantAppointmentFunding = salarySettingService.vacateAppointmentFunding(appointmentFunding);

        if (vacantAppointmentFunding != null) {
            appointmentFundings.add(indexOfSelectedLine + 1, vacantAppointmentFunding);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * adjust the salary amount of the specified funding line
     */
    public ActionForward adjustSalarySettingLinePercent(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        QuickSalarySettingForm salarySettingForm = (QuickSalarySettingForm) form;
        SalarySettingExpansion salarySettingExpansion = salarySettingForm.getSalarySettingExpansion();

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
    public ActionForward adjustAllSalarySettingLinesPercent(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        QuickSalarySettingForm salarySettingForm = (QuickSalarySettingForm) form;
        SalarySettingExpansion salarySettingExpansion = salarySettingForm.getSalarySettingExpansion();

        List<PendingBudgetConstructionAppointmentFunding> appointmentFundings = salarySettingExpansion.getPendingBudgetConstructionAppointmentFunding();
        KualiDecimal adjustmentAmount = salarySettingForm.getAdjustmentAmount();
        String adjustmentMeasurement = salarySettingForm.getAdjustmentMeasurement();

        Object fullEntryEditMode = salarySettingForm.getEditingMode().get(AuthorizationConstants.EditMode.FULL_ENTRY);
        boolean isEditable = fullEntryEditMode != null && Boolean.parseBoolean(fullEntryEditMode.toString());

        for (PendingBudgetConstructionAppointmentFunding appointmentFunding : appointmentFundings) {
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
        String salarySettingURL = this.buildSalarySettingURL(mapping, form, request, BCConstants.INCUMBENT_SALARY_SETTING_ACTION);

        return new ActionForward(salarySettingURL, true);
    }

    /**
     * perform salary setting by position with the specified funding line
     */
    public ActionForward performPositionSalarySetting(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String salarySettingURL = this.buildSalarySettingURL(mapping, form, request, BCConstants.POSITION_SALARY_SETTING_ACTION);

        return new ActionForward(salarySettingURL, true);
    }

    /**
     * perform salary setting by position with the specified funding line
     */
    public ActionForward toggleAdjustmentMeasurement(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        QuickSalarySettingForm salarySettingForm = (QuickSalarySettingForm) form;

        boolean currentStatus = salarySettingForm.isHideAdjustmentMeasurement();
        salarySettingForm.setHideAdjustmentMeasurement(!currentStatus);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * save the changes for salary setting
     */
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        QuickSalarySettingForm salarySettingForm = (QuickSalarySettingForm) form;
        SalarySettingExpansion salarySettingExpansion = salarySettingForm.getSalarySettingExpansion();

        salarySettingService.saveSalarySetting(salarySettingExpansion);
        salarySettingExpansion.refresh();

        GlobalVariables.getMessageList().add(BCKeyConstants.MESSAGE_SALARY_SETTING_SAVED);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * normalize the hourly pay rate and annual pay amount
     */
    public ActionForward normalizePayRateAndAmount(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        QuickSalarySettingForm salarySettingForm = (QuickSalarySettingForm) form;
        SalarySettingExpansion salarySettingExpansion = salarySettingForm.getSalarySettingExpansion();

        // retrieve the selected funding line
        int indexOfSelectedLine = this.getSelectedLine(request);
        List<PendingBudgetConstructionAppointmentFunding> appointmentFundings = salarySettingExpansion.getPendingBudgetConstructionAppointmentFunding();
        PendingBudgetConstructionAppointmentFunding appointmentFunding = appointmentFundings.get(indexOfSelectedLine);

        this.normalizePayRateAndAmount(appointmentFunding);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * normalize the hourly pay rate and annual pay amount of the given appointment funding
     * 
     * @param appointmentFunding the given appointment funding
     */
    private void normalizePayRateAndAmount(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        KualiInteger currentAnnualPayAmount = appointmentFunding.getAppointmentRequestedAmount();
        if (currentAnnualPayAmount != null && currentAnnualPayAmount.isNonZero()) {
            BigDecimal hourlyPayRate = salarySettingService.calculateHourlyPayRate(appointmentFunding);
            appointmentFunding.setAppointmentRequestedPayRate(hourlyPayRate);
        }

        BigDecimal currentHourlyPayRate = appointmentFunding.getAppointmentRequestedPayRate();
        if (currentHourlyPayRate != null) {
            KualiInteger annualPayAmount = salarySettingService.calculateAnnualPayAmount(appointmentFunding);
            appointmentFunding.setAppointmentRequestedAmount(annualPayAmount);
        }
    }

    // adjust the requested salary amount of the given appointment funding line
    private void adjustSalary(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        String adjustmentMeasurement = appointmentFunding.getAdjustmentMeasurement();
        if (BCConstants.SalaryAdjustmentMeasurement.PERCENT.measurement.equals(adjustmentMeasurement)) {
            salarySettingService.adjustRequestedSalaryByPercent(appointmentFunding);
        }
        else if (BCConstants.SalaryAdjustmentMeasurement.AMOUNT.measurement.equals(adjustmentMeasurement)) {
            salarySettingService.adjustRequestedSalaryByAmount(appointmentFunding);
        }
    }

    // build the URL for the specified salary setting method
    private String buildSalarySettingURL(ActionMapping mapping, ActionForm form, HttpServletRequest request, String salarySettingAction) {
        QuickSalarySettingForm salarySettingForm = (QuickSalarySettingForm) form;
        SalarySettingExpansion salarySettingExpansion = salarySettingForm.getSalarySettingExpansion();

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
        parameters.put(KFSConstants.BACK_LOCATION, basePath + mapping.getPath() + ".do");
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
