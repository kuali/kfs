/*
 * Copyright 2006-2008 The Kuali Foundation
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
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.BCKeyConstants;
import org.kuali.kfs.module.bc.BCPropertyConstants;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionIntendedIncumbent;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPosition;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.businessobject.SalarySettingExpansion;
import org.kuali.kfs.module.bc.document.BudgetConstructionDocument;
import org.kuali.kfs.module.bc.document.service.BudgetDocumentService;
import org.kuali.kfs.module.bc.document.service.SalarySettingService;
import org.kuali.kfs.module.bc.document.validation.event.QuickSaveSalarySettingEvent;
import org.kuali.kfs.module.bc.util.BudgetUrlUtil;
import org.kuali.kfs.sys.DynamicCollectionComparator;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.kns.web.struts.form.KualiForm;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.util.UrlFactory;

/**
 * the struts action for the quick salary setting
 */
public class QuickSalarySettingAction extends SalarySettingBaseAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(QuickSalarySettingAction.class);

    private BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
    private SalarySettingService salarySettingService = SpringContext.getBean(SalarySettingService.class);
    private BudgetDocumentService budgetDocumentService = SpringContext.getBean(BudgetDocumentService.class);

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiAction#refresh(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        LOG.debug("refresh() started");

        super.refresh(mapping, form, request, response);

        QuickSalarySettingForm salarySettingForm = (QuickSalarySettingForm) form;
        salarySettingForm.setRefreshIncumbentBeforeSalarySetting(false);
        salarySettingForm.setRefreshPositionBeforeSalarySetting(false);

        return this.loadExpansionScreen(mapping, form, request, response);
    }

    /**
     * Forwards to budget incumbent lookup passing parameters for new funding line.
     */
    public ActionForward addIncumbent(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        QuickSalarySettingForm salarySettingForm = (QuickSalarySettingForm) form;

        if (!salarySettingForm.isViewOnlyEntry()) {
            boolean isSaved = this.save(form);
            if (!isSaved) {
                return mapping.findForward(KFSConstants.MAPPING_BASIC);
            }
        }

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, salarySettingForm.getChartOfAccountsCode());
        parameters.put(KFSPropertyConstants.ACCOUNT_NUMBER, salarySettingForm.getAccountNumber());
        parameters.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, salarySettingForm.getSubAccountNumber());
        parameters.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, salarySettingForm.getFinancialObjectCode());
        parameters.put(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, salarySettingForm.getFinancialSubObjectCode());

        parameters.put(BCConstants.SHOW_SALARY_BY_INCUMBENT_ACTION, Boolean.TRUE.toString());
        parameters.put(BCPropertyConstants.BUDGET_BY_ACCOUNT_MODE, Boolean.TRUE.toString());
        parameters.put(BCPropertyConstants.ADD_LINE, Boolean.TRUE.toString());
        parameters.put(BCPropertyConstants.MAIN_WINDOW, (salarySettingForm.isMainWindow() ? "true" : "false"));

        // anchor, if it exists
        if (form instanceof KualiForm && StringUtils.isNotEmpty(salarySettingForm.getAnchor())) {
            parameters.put(BCConstants.RETURN_ANCHOR, salarySettingForm.getAnchor());
        }

        // the form object is retrieved and removed upon return by KualiRequestProcessor.processActionForm()
        parameters.put(BCConstants.RETURN_FORM_KEY, GlobalVariables.getUserSession().addObjectWithGeneratedKey(form, BCConstants.FORMKEY_PREFIX));

        String lookupUrl = BudgetUrlUtil.buildTempListLookupUrl(mapping, salarySettingForm, BCConstants.TempListLookupMode.INTENDED_INCUMBENT, BudgetConstructionIntendedIncumbent.class.getName(), parameters);

        this.cleanupAnySessionForm(mapping, request);
        return new ActionForward(lookupUrl, true);
    }

    /**
     * Forwards to budget position lookup passing parameters for new funding line.
     */
    public ActionForward addPosition(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        QuickSalarySettingForm salarySettingForm = (QuickSalarySettingForm) form;

        if (!salarySettingForm.isViewOnlyEntry()) {
            boolean isSaved = this.save(form);
            if (!isSaved) {
                return mapping.findForward(KFSConstants.MAPPING_BASIC);
            }
        }

        Map<String, String> parameters = new HashMap<String, String>();
        parameters.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, salarySettingForm.getChartOfAccountsCode());
        parameters.put(KFSPropertyConstants.ACCOUNT_NUMBER, salarySettingForm.getAccountNumber());
        parameters.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, salarySettingForm.getSubAccountNumber());
        parameters.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, salarySettingForm.getFinancialObjectCode());
        parameters.put(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, salarySettingForm.getFinancialSubObjectCode());

        parameters.put(BCConstants.SHOW_SALARY_BY_POSITION_ACTION, Boolean.toString(salarySettingForm.isBudgetByAccountMode()));
        parameters.put(BCPropertyConstants.BUDGET_BY_ACCOUNT_MODE, Boolean.TRUE.toString());
        parameters.put(BCPropertyConstants.ADD_LINE, Boolean.TRUE.toString());
        parameters.put(BCPropertyConstants.MAIN_WINDOW, (salarySettingForm.isMainWindow() ? "true" : "false"));

        // anchor, if it exists
        if (form instanceof KualiForm && StringUtils.isNotEmpty(salarySettingForm.getAnchor())) {
            parameters.put(BCConstants.RETURN_ANCHOR, salarySettingForm.getAnchor());
        }

        // the form object is retrieved and removed upon return by KualiRequestProcessor.processActionForm()
        parameters.put(BCConstants.RETURN_FORM_KEY, GlobalVariables.getUserSession().addObjectWithGeneratedKey(form, BCConstants.FORMKEY_PREFIX));

        String lookupUrl = BudgetUrlUtil.buildTempListLookupUrl(mapping, salarySettingForm, BCConstants.TempListLookupMode.BUDGET_POSITION_LOOKUP, BudgetConstructionPosition.class.getName(), parameters);

        this.cleanupAnySessionForm(mapping, request);
        return new ActionForward(lookupUrl, true);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.web.struts.SalarySettingBaseAction#loadExpansionScreen(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward loadExpansionScreen(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        QuickSalarySettingForm salarySettingForm = (QuickSalarySettingForm) form;

        Map<String, Object> keyMap = salarySettingForm.getKeyMapOfSalarySettingItem();
        SalarySettingExpansion salarySettingExpansion = (SalarySettingExpansion) businessObjectService.findByPrimaryKey(SalarySettingExpansion.class, keyMap);

        if (salarySettingExpansion == null) {
            GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_MESSAGES, BCKeyConstants.ERROR_SALARY_SETTING_EXPANSION_NOT_FOUND);
            return this.returnToCaller(mapping, form, request, response);
        }

        salarySettingForm.setSalarySettingExpansion(salarySettingExpansion);

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * perform salary setting by incumbent with the specified funding line
     */
    public ActionForward performIncumbentSalarySetting(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        QuickSalarySettingForm salarySettingForm = (QuickSalarySettingForm) form;

        if (!salarySettingForm.isViewOnlyEntry()) {
            boolean isSaved = this.save(form);
            if (!isSaved) {
                return mapping.findForward(KFSConstants.MAPPING_BASIC);
            }
        }

        String salarySettingURL = this.buildDetailSalarySettingURL(mapping, form, request, BCConstants.INCUMBENT_SALARY_SETTING_ACTION);
        this.cleanupAnySessionForm(mapping, request);
        return new ActionForward(salarySettingURL, true);
    }

    /**
     * perform salary setting by position with the specified funding line
     */
    public ActionForward performPositionSalarySetting(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        QuickSalarySettingForm salarySettingForm = (QuickSalarySettingForm) form;

        if (!salarySettingForm.isViewOnlyEntry()) {
            boolean isSaved = this.save(form);
            if (!isSaved) {
                return mapping.findForward(KFSConstants.MAPPING_BASIC);
            }
        }

        String salarySettingURL = this.buildDetailSalarySettingURL(mapping, form, request, BCConstants.POSITION_SALARY_SETTING_ACTION);
        this.cleanupAnySessionForm(mapping, request);
        return new ActionForward(salarySettingURL, true);
    }

    /**
     * adjust the salary amounts of all funding lines
     */
    public ActionForward adjustAllSalarySettingLinesPercent(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        QuickSalarySettingForm salarySettingForm = (QuickSalarySettingForm) form;
        SalarySettingExpansion salarySettingExpansion = salarySettingForm.getSalarySettingExpansion();

        List<PendingBudgetConstructionAppointmentFunding> appointmentFundings = salarySettingForm.getAppointmentFundings();

        KualiDecimal adjustmentAmount = salarySettingForm.getAdjustmentAmount();
        String adjustmentMeasurement = salarySettingForm.getAdjustmentMeasurement();

        // the adjustment measurement and amount must be provided
        if (StringUtils.isBlank(adjustmentMeasurement)) {
            GlobalVariables.getMessageMap().putError(BCPropertyConstants.ADJUSTMENT_MEASUREMENT, BCKeyConstants.ERROR_ADJUSTMENT_PERCENT_REQUIRED);
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        if (ObjectUtils.isNull(adjustmentAmount)) {
            GlobalVariables.getMessageMap().putError(BCPropertyConstants.ADJUSTMENT_AMOUNT, BCKeyConstants.ERROR_ADJUSTMENT_AMOUNT_REQUIRED);
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        // retrieve corresponding document in advance in order to use the rule framework
        BudgetConstructionDocument document = budgetDocumentService.getBudgetConstructionDocument(salarySettingExpansion);
        if (document == null) {
            GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, BCKeyConstants.ERROR_BUDGET_DOCUMENT_NOT_FOUND, salarySettingExpansion.getSalarySettingExpansionString());
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        for (PendingBudgetConstructionAppointmentFunding appointmentFunding : appointmentFundings) {
            appointmentFunding.setAdjustmentAmount(adjustmentAmount);
            appointmentFunding.setAdjustmentMeasurement(adjustmentMeasurement);

            String errorKeyPrefix = this.getErrorKeyPrefixOfAppointmentFundingLine(appointmentFundings, appointmentFunding);

            ActionForward adjustAction = this.adjustSalarySettingLinePercent(mapping, salarySettingForm, appointmentFunding, document, errorKeyPrefix);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
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
     * @see org.kuali.kfs.module.bc.document.web.struts.SalarySettingBaseAction#close(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward close(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        QuickSalarySettingForm salarySettingForm = (QuickSalarySettingForm) form;

        ActionForward closeActionForward;
        if (salarySettingForm.isViewOnlyEntry() || salarySettingForm.isSalarySettingClosed()) {
            closeActionForward = this.returnAfterClose(salarySettingForm, mapping, request, response);
        }
        else {
            closeActionForward = super.close(mapping, salarySettingForm, request, response);
        }
        return closeActionForward;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.web.struts.SalarySettingBaseAction#save(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        boolean isSaved = this.save(form);
        if (isSaved) {
            KNSGlobalVariables.getMessageList().add(BCKeyConstants.MESSAGE_SALARY_SETTING_SAVED);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    // save the savable appointment funding lines
    private boolean save(ActionForm form) {
        QuickSalarySettingForm salarySettingForm = (QuickSalarySettingForm) form;
        SalarySettingExpansion salarySettingExpansion = salarySettingForm.getSalarySettingExpansion();

        BudgetConstructionDocument document = budgetDocumentService.getBudgetConstructionDocument(salarySettingExpansion);
        if (document == null) {
            GlobalVariables.getMessageMap().putError(KFSConstants.GLOBAL_ERRORS, BCKeyConstants.ERROR_BUDGET_DOCUMENT_NOT_FOUND, salarySettingExpansion.getSalarySettingExpansionString());
            return false;
        }

        // get budget allowed flag which is used to deny any updates other than delete
        boolean isBudgetAllowed = budgetDocumentService.isBudgetableDocument(document);

        List<PendingBudgetConstructionAppointmentFunding> savableAppointmentFundings = salarySettingForm.getAppointmentFundings();
        List<PendingBudgetConstructionAppointmentFunding> appointmentFundings = salarySettingForm.getAppointmentFundings();

        for (PendingBudgetConstructionAppointmentFunding savableFunding : savableAppointmentFundings) {
            String errorKeyPrefix = this.getErrorKeyPrefixOfAppointmentFundingLine(appointmentFundings, savableFunding);

            // added 12/3/2008 - gwp
            // no rules check for marked delete line
            if (savableFunding.isAppointmentFundingDeleteIndicator()) {
                continue;
            }

            salarySettingService.recalculateDerivedInformation(savableFunding);

            // any other update must be against a budget allowed document
            if (!isBudgetAllowed) {
                GlobalVariables.getMessageMap().addToErrorPath(errorKeyPrefix);
                GlobalVariables.getMessageMap().putError(BCPropertyConstants.APPOINTMENT_REQUESTED_AMOUNT, BCKeyConstants.ERROR_BUDGET_DOCUMENT_NOT_BUDGETABLE, salarySettingExpansion.getSalarySettingExpansionString());
                return false;
            }

            // validate the savable appointment funding lines
            boolean isValid = this.invokeRules(new QuickSaveSalarySettingEvent(KFSConstants.EMPTY_STRING, errorKeyPrefix, document, savableFunding));
            if (!isValid) {
                return false;
            }
        }

        salarySettingService.saveSalarySetting(salarySettingExpansion);
        salarySettingExpansion.refresh();

        return true;
    }

    // build the URL for the specified salary setting method
    private String buildDetailSalarySettingURL(ActionMapping mapping, ActionForm form, HttpServletRequest request, String salarySettingAction) {
        QuickSalarySettingForm salarySettingForm = (QuickSalarySettingForm) form;

        int indexOfSelectedLine = this.getSelectedLine(request);
        List<PendingBudgetConstructionAppointmentFunding> appointmentFundings = salarySettingForm.getAppointmentFundings();
        DynamicCollectionComparator.sort(appointmentFundings, KFSPropertyConstants.POSITION_NUMBER, KFSPropertyConstants.EMPLID);

        PendingBudgetConstructionAppointmentFunding appointmentFunding = appointmentFundings.get(indexOfSelectedLine);

        // get the base action
        Map<String, String> salarySettingMethodAction = this.getSalarySettingMethodActionInfo();
        String basePath = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.APPLICATION_URL_KEY);
        String baseAction = basePath + "/" + salarySettingAction;
        String methodToCall = salarySettingMethodAction.get(salarySettingAction);

        // build the query strings with the information of the selected line
        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, methodToCall);
        parameters.put(KFSConstants.BACK_LOCATION, basePath + mapping.getPath() + KFSConstants.ACTION_EXTENSION_DOT_DO);

        parameters.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, appointmentFunding.getUniversityFiscalYear().toString());
        parameters.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, appointmentFunding.getChartOfAccountsCode());
        parameters.put(KFSPropertyConstants.ACCOUNT_NUMBER, appointmentFunding.getAccountNumber());
        parameters.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, appointmentFunding.getSubAccountNumber());
        parameters.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, appointmentFunding.getFinancialObjectCode());
        parameters.put(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, appointmentFunding.getFinancialSubObjectCode());
        parameters.put(KFSPropertyConstants.POSITION_NUMBER, appointmentFunding.getPositionNumber());
        parameters.put(KFSPropertyConstants.EMPLID, appointmentFunding.getEmplid());

        parameters.put(BCPropertyConstants.REFRESH_INCUMBENT_BEFORE_SALARY_SETTING, Boolean.valueOf(salarySettingForm.isRefreshIncumbentBeforeSalarySetting()).toString());
        parameters.put(BCPropertyConstants.REFRESH_POSITION_BEFORE_SALARY_SETTING, Boolean.valueOf(salarySettingForm.isRefreshPositionBeforeSalarySetting()).toString());

        parameters.put(BCPropertyConstants.BUDGET_BY_ACCOUNT_MODE, Boolean.TRUE.toString());
        parameters.put(BCPropertyConstants.ADD_LINE, Boolean.FALSE.toString());
        parameters.put(BCPropertyConstants.MAIN_WINDOW, (salarySettingForm.isMainWindow() ? "true" : "false"));

        // anchor, if it exists
        if (form instanceof KualiForm && StringUtils.isNotEmpty(salarySettingForm.getAnchor())) {
            parameters.put(BCConstants.RETURN_ANCHOR, salarySettingForm.getAnchor());
        }

        // the form object is retrieved and removed upon return by KualiRequestProcessor.processActionForm()
        parameters.put(BCConstants.RETURN_FORM_KEY, GlobalVariables.getUserSession().addObjectWithGeneratedKey(form, BCConstants.FORMKEY_PREFIX));

        return UrlFactory.parameterizeUrl(baseAction, parameters);
    }

    // get the pairs of the method and action of salary settings
    private Map<String, String> getSalarySettingMethodActionInfo() {
        Map<String, String> salarySettingMethodAction = new HashMap<String, String>();
        salarySettingMethodAction.put(BCConstants.INCUMBENT_SALARY_SETTING_ACTION, BCConstants.INCUMBENT_SALARY_SETTING_METHOD);
        salarySettingMethodAction.put(BCConstants.POSITION_SALARY_SETTING_ACTION, BCConstants.POSITION_SALARY_SETTING_METHOD);

        return salarySettingMethodAction;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.web.struts.SalarySettingBaseAction#getFundingAwareObjectName()
     */
    @Override
    protected String getFundingAwareObjectName() {
        return BCPropertyConstants.SALARY_SETTING_EXPANSION;
    }
}
