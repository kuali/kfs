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

import static org.kuali.kfs.module.bc.BCConstants.AppointmentFundingDurationCodes.NONE;

import java.text.MessageFormat;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.bc.BCKeyConstants;
import org.kuali.kfs.module.bc.BCPropertyConstants;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.document.BudgetConstructionDocument;
import org.kuali.kfs.module.bc.document.service.BudgetDocumentService;
import org.kuali.kfs.module.bc.document.service.SalarySettingService;
import org.kuali.kfs.module.bc.document.validation.event.AddAppointmentFundingEvent;
import org.kuali.kfs.module.bc.document.validation.event.BudgetExpansionEvent;
import org.kuali.kfs.module.bc.document.validation.event.SaveSalarySettingEvent;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.util.GlobalVariables;

/**
 * the base struts action for the detail salary setting
 */
public abstract class DetailSalarySettingAction extends SalarySettingBaseAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DetailSalarySettingAction.class);

    private SalarySettingService salarySettingService = SpringContext.getBean(SalarySettingService.class);
    private BudgetDocumentService budgetDocumentService = SpringContext.getBean(BudgetDocumentService.class);

    /**
     * @see org.kuali.kfs.module.bc.document.web.struts.SalarySettingBaseAction#execute(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward executeAction = null;

        try {
            executeAction = super.execute(mapping, form, request, response);
        }
        catch (Exception e) {
            // release all locks when encountering runtime exception
            DetailSalarySettingForm salarySettingForm = (DetailSalarySettingForm) form;
            if (!salarySettingForm.isViewOnlyEntry()) {
                salarySettingForm.releaseTransactionLocks();
                salarySettingForm.releasePositionAndFundingLocks();
            }
            
            LOG.fatal("Unexpected errors occurred. " + e.getMessage());
        }

        return executeAction;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.web.struts.BudgetExpansionAction#close(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward close(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward closeActionForward = super.close(mapping, form, request, response);

        // ask the question unless it has not been answered
        String question = request.getParameter(KFSConstants.QUESTION_INST_ATTRIBUTE_NAME);
        if (StringUtils.isBlank(question)) {
            return closeActionForward;
        }

        DetailSalarySettingForm salarySettingForm = (DetailSalarySettingForm) form;

        // release all locks before closing the current expansion screen
        if (!salarySettingForm.isViewOnlyEntry()) {
            salarySettingForm.releasePositionAndFundingLocks();
        }

        salarySettingForm.setOrgSalSetClose(true);
        return closeActionForward;
    }

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiAction#refresh(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward refresh(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        super.refresh(mapping, form, request, response);

        DetailSalarySettingForm salarySettingForm = (DetailSalarySettingForm) form;
        String refreshCaller = request.getParameter(KFSConstants.REFRESH_CALLER);

        if (refreshCaller != null && refreshCaller.endsWith(KFSConstants.LOOKUPABLE_SUFFIX)) {
            salarySettingForm.refreshBCAFLine(salarySettingForm.getNewBCAFLine());
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.web.struts.SalarySettingBaseAction#save(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DetailSalarySettingForm salarySettingForm = (DetailSalarySettingForm) form;
        List<PendingBudgetConstructionAppointmentFunding> savableAppointmentFundings = salarySettingForm.getSavableAppointmentFundings();
        List<PendingBudgetConstructionAppointmentFunding> appointmentFundings = salarySettingForm.getAppointmentFundings();

        if (savableAppointmentFundings == null || savableAppointmentFundings.isEmpty()) {
            GlobalVariables.getMessageList().add(BCKeyConstants.MESSAGE_SALARY_SETTING_SAVED);
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        for (PendingBudgetConstructionAppointmentFunding savableFunding : savableAppointmentFundings) {
            String errorKeyPrefix = this.getErrorKeyPrefixOfAppointmentFundingLine(appointmentFundings, savableFunding);
            
            // retrieve corresponding document in advance in order to use the rule framework
            BudgetConstructionDocument document = budgetDocumentService.getBudgetConstructionDocument(savableFunding);
            if (document == null) {
                GlobalVariables.getErrorMap().putError(errorKeyPrefix, BCKeyConstants.ERROR_BUDGET_DOCUMENT_NOT_FOUND, savableFunding.getAppointmentFundingString());
                return mapping.findForward(KFSConstants.MAPPING_BASIC);
            }

            // validate the savable appointment funding lines
            boolean isValid = this.invokeRules(new SaveSalarySettingEvent("", errorKeyPrefix, document, savableFunding));
            if (!isValid) {
                return mapping.findForward(KFSConstants.MAPPING_BASIC);
            }
        }

        // acquire transaction locks for all funding lines
        boolean transactionLocked = salarySettingForm.acquireTransactionLocks();
        if (!transactionLocked) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        salarySettingService.saveSalarySetting(savableAppointmentFundings);

        // release all transaction locks
        salarySettingForm.releaseTransactionLocks();

        GlobalVariables.getMessageList().add(BCKeyConstants.MESSAGE_SALARY_SETTING_SAVED);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * adds an appointment funding line to the set of existing funding lines
     */
    public ActionForward addAppointmentFundingLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DetailSalarySettingForm salarySettingForm = (DetailSalarySettingForm) form;
        List<PendingBudgetConstructionAppointmentFunding> appointmentFundings = salarySettingForm.getAppointmentFundings();

        PendingBudgetConstructionAppointmentFunding newAppointmentFunding = salarySettingForm.getNewBCAFLine();
        salarySettingForm.refreshBCAFLine(newAppointmentFunding);

        // setup a working appointment funding so that the default values can be applied
        PendingBudgetConstructionAppointmentFunding workingAppointmentFunding = new PendingBudgetConstructionAppointmentFunding();
        ObjectUtil.buildObject(workingAppointmentFunding, newAppointmentFunding);
        this.applyDefaultValuesIfEmpty(workingAppointmentFunding);

        // retrieve corresponding document in advance in order to use the rule framework
        BudgetConstructionDocument document = budgetDocumentService.getBudgetConstructionDocument(workingAppointmentFunding);
        if (document == null) {
            GlobalVariables.getErrorMap().putError(BCPropertyConstants.NEW_BCAF_LINE, BCKeyConstants.ERROR_BUDGET_DOCUMENT_NOT_FOUND, workingAppointmentFunding.getAppointmentFundingString());
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        // validate the new appointment funding line
        BudgetExpansionEvent addAppointmentFundingEvent = new AddAppointmentFundingEvent(KFSConstants.EMPTY_STRING, BCPropertyConstants.NEW_BCAF_LINE, document, appointmentFundings, workingAppointmentFunding);
        boolean isValid = this.invokeRules(addAppointmentFundingEvent);        
        if (!isValid) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        // acquire a lock for the new appointment funding line
        boolean gotLocks = salarySettingForm.acquirePositionAndFundingLocks(workingAppointmentFunding);
        if (!gotLocks) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        
        // have no permission to do salary setting on the new line
        if(workingAppointmentFunding.isDisplayOnlyMode()) {
            GlobalVariables.getErrorMap().putError(BCPropertyConstants.NEW_BCAF_LINE, BCKeyConstants.ERROR_NO_SALARY_SETTING_PERMISSION, workingAppointmentFunding.getAppointmentFundingString());
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }
        
        appointmentFundings.add(workingAppointmentFunding);
        salarySettingForm.setNewBCAFLine(salarySettingForm.createNewAppointmentFundingLine());

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    // determine whether any active funding line is invloved leave
    protected boolean hasFundingLineInvolvedLeave(List<PendingBudgetConstructionAppointmentFunding> activeAppointmentFundings) {
        for (PendingBudgetConstructionAppointmentFunding appointmentFunding : activeAppointmentFundings) {
            String leaveDurationCode = appointmentFunding.getAppointmentFundingDurationCode();

            if (!StringUtils.equals(leaveDurationCode, NONE.durationCode)) {
                return true;
            }
        }

        return false;
    }

    // apply the default values to the certain fields when the fields are empty
    protected void applyDefaultValuesIfEmpty(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        if (StringUtils.isBlank(appointmentFunding.getSubAccountNumber())) {
            appointmentFunding.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
        }

        if (StringUtils.isBlank(appointmentFunding.getFinancialSubObjectCode())) {
            appointmentFunding.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        }
    }
}
