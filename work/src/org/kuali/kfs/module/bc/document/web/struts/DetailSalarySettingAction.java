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

import static org.kuali.kfs.module.bc.BCConstants.AppointmentFundingDurationCodes.LWPA;
import static org.kuali.kfs.module.bc.BCConstants.AppointmentFundingDurationCodes.LWPF;
import static org.kuali.kfs.module.bc.BCConstants.AppointmentFundingDurationCodes.NONE;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.BCKeyConstants;
import org.kuali.kfs.module.bc.BCPropertyConstants;
import org.kuali.kfs.module.bc.BCConstants.SynchronizationCheckType;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.document.BudgetConstructionDocument;
import org.kuali.kfs.module.bc.document.service.BudgetDocumentService;
import org.kuali.kfs.module.bc.document.service.LockService;
import org.kuali.kfs.module.bc.document.service.SalarySettingService;
import org.kuali.kfs.module.bc.document.validation.event.AddAppointmentFundingEvent;
import org.kuali.kfs.module.bc.document.validation.event.BudgetExpansionEvent;
import org.kuali.kfs.module.bc.document.validation.event.SaveSalarySettingEvent;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.question.ConfirmationQuestion;
import org.kuali.rice.kns.service.BusinessObjectDictionaryService;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;

/**
 * the base struts action for the detail salary setting
 */
public abstract class DetailSalarySettingAction extends SalarySettingBaseAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DetailSalarySettingAction.class);

    private SalarySettingService salarySettingService = SpringContext.getBean(SalarySettingService.class);
    private BudgetDocumentService budgetDocumentService = SpringContext.getBean(BudgetDocumentService.class);
    private Person currentUser = GlobalVariables.getUserSession().getPerson();

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

            LOG.fatal("Unexpected errors occurred.", e);

            // re-throw the exception
            throw new ServletException(e);
        }

        return executeAction;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.web.struts.BudgetExpansionAction#close(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward close(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DetailSalarySettingForm salarySettingForm = (DetailSalarySettingForm) form;
        String buttonClicked = request.getParameter(KFSConstants.QUESTION_CLICKED_BUTTON);
        boolean isClose = StringUtils.equals(ConfirmationQuestion.YES, buttonClicked) || StringUtils.equals(ConfirmationQuestion.NO, buttonClicked);

        ActionForward closeActionForward;
        if (salarySettingForm.isViewOnlyEntry() || salarySettingForm.isSalarySettingClosed()) {
            closeActionForward = this.returnAfterClose(salarySettingForm, mapping, request, response);
        }
        else {
            closeActionForward = super.close(mapping, salarySettingForm, request, response);
        }

        // release all locks before closing the current expansion screen
        if (isClose && !salarySettingForm.isViewOnlyEntry() && salarySettingForm.isSalarySettingClosed()) {
            salarySettingForm.releasePositionAndFundingLocks();
            if (form instanceof PositionSalarySettingForm) {
                // handle case where there are no funding lines attached to position
                this.unlockPositionOnly((PositionSalarySettingForm) form);
            }
        }

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
            // user could return from a lookup using a different fiscal year - do not allow it to change the funding line
            if (!salarySettingForm.getNewBCAFLine().getUniversityFiscalYear().equals(salarySettingForm.getUniversityFiscalYear())){
                salarySettingForm.getNewBCAFLine().setUniversityFiscalYear(salarySettingForm.getUniversityFiscalYear());
            }
            if (salarySettingForm instanceof PositionSalarySettingForm){
                // check that the object code is consistent with the position default object
                PositionSalarySettingForm ssPosForm = (PositionSalarySettingForm) salarySettingForm;
                if (!ssPosForm.getNewBCAFLine().getFinancialObjectCode().equals(ssPosForm.getBudgetConstructionPosition().getIuDefaultObjectCode())){
                    ssPosForm.getNewBCAFLine().setFinancialObjectCode(ssPosForm.getBudgetConstructionPosition().getIuDefaultObjectCode());
                }
            }
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
            KNSGlobalVariables.getMessageList().add(BCKeyConstants.MESSAGE_SALARY_SETTING_SAVED);
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        MessageMap errorMap = GlobalVariables.getMessageMap();
        for (PendingBudgetConstructionAppointmentFunding savableFunding : savableAppointmentFundings) {
            String errorKeyPrefix = this.getErrorKeyPrefixOfAppointmentFundingLine(appointmentFundings, savableFunding);

            // retrieve corresponding document in advance in order to use the rule framework
            BudgetConstructionDocument document = budgetDocumentService.getBudgetConstructionDocument(savableFunding);
            if (document == null) {
                errorMap.putError(errorKeyPrefix, BCKeyConstants.ERROR_BUDGET_DOCUMENT_NOT_FOUND, savableFunding.getAppointmentFundingString());
                return mapping.findForward(KFSConstants.MAPPING_BASIC);
            }

            // bypass validation rules if the funding line has been marked as purged or deleted
            if (savableFunding.isPurged() || savableFunding.isAppointmentFundingDeleteIndicator()) {
                continue;
            }

            salarySettingService.recalculateDerivedInformation(savableFunding);

            // validate the savable appointment funding lines
            boolean isValid = this.invokeRules(new SaveSalarySettingEvent(KFSConstants.EMPTY_STRING, errorKeyPrefix, document, savableFunding, this.getSynchronizationCheckType()));
            if (!isValid) {
                return mapping.findForward(KFSConstants.MAPPING_BASIC);
            }
        }

        // acquire transaction locks for all funding lines
        boolean transactionLocked = salarySettingForm.acquireTransactionLocks(GlobalVariables.getMessageMap());
        if (!transactionLocked) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        // test if Form is IncumbentSS and call saveSalarySetting() with a isSalarySettingByIncumbent true
        // so it knows when to remove purged funding position locks when it is the last line for the position
        if (form instanceof IncumbentSalarySettingForm) {
            salarySettingService.saveSalarySetting(savableAppointmentFundings, Boolean.TRUE);
        }
        else {
            salarySettingService.saveSalarySetting(savableAppointmentFundings, Boolean.FALSE);
        }

        // release all transaction locks
        salarySettingForm.releaseTransactionLocks();

        this.clearPurgedAppointmentFundings(appointmentFundings);

        KNSGlobalVariables.getMessageList().add(BCKeyConstants.MESSAGE_SALARY_SETTING_SAVED);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * adds an appointment funding line to the set of existing funding lines
     */
    public ActionForward addAppointmentFundingLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DetailSalarySettingForm salarySettingForm = (DetailSalarySettingForm) form;
        List<PendingBudgetConstructionAppointmentFunding> appointmentFundings = salarySettingForm.getAppointmentFundings();

        PendingBudgetConstructionAppointmentFunding newAppointmentFunding = salarySettingForm.getNewBCAFLine();
        SpringContext.getBean(BusinessObjectDictionaryService.class).performForceUppercase(newAppointmentFunding);

        salarySettingForm.refreshBCAFLine(newAppointmentFunding);

        // setup a working appointment funding so that the default values can be applied
        PendingBudgetConstructionAppointmentFunding workingAppointmentFunding = new PendingBudgetConstructionAppointmentFunding();
        ObjectUtil.buildObject(workingAppointmentFunding, newAppointmentFunding);
        this.applyDefaultValuesIfEmpty(workingAppointmentFunding);

        MessageMap errorMap = GlobalVariables.getMessageMap();

        // retrieve corresponding document in advance in order to use the rule framework
        BudgetConstructionDocument document = budgetDocumentService.getBudgetConstructionDocument(workingAppointmentFunding);
        if (document == null) {
            errorMap.putError(BCPropertyConstants.NEW_BCAF_LINE, BCKeyConstants.ERROR_BUDGET_DOCUMENT_NOT_FOUND, workingAppointmentFunding.getAppointmentFundingString());
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        // check special case where emplid is vacant and force funding duration to none
        String emplid = workingAppointmentFunding.getEmplid();
        if (StringUtils.isNotEmpty(emplid) && StringUtils.equals(emplid, BCConstants.VACANT_EMPLID)) {
            workingAppointmentFunding.setAppointmentFundingDurationCode(BCConstants.AppointmentFundingDurationCodes.NONE.durationCode);
        }
        salarySettingService.recalculateDerivedInformation(workingAppointmentFunding);

        // validate the new appointment funding line
        BudgetExpansionEvent addAppointmentFundingEvent = new AddAppointmentFundingEvent(KFSConstants.EMPTY_STRING, BCPropertyConstants.NEW_BCAF_LINE, document, appointmentFundings, workingAppointmentFunding, this.getSynchronizationCheckType());
        boolean isValid = this.invokeRules(addAppointmentFundingEvent);
        if (!isValid) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        // set remaining flags
        boolean vacatable = salarySettingService.canBeVacant(appointmentFundings, workingAppointmentFunding);
        workingAppointmentFunding.setVacatable(vacatable);

        Integer fiscalYear = workingAppointmentFunding.getUniversityFiscalYear();
        String chartCode = workingAppointmentFunding.getChartOfAccountsCode();
        String objectCode = workingAppointmentFunding.getFinancialObjectCode();
        boolean hourlyPaid = salarySettingService.isHourlyPaidObject(fiscalYear, chartCode, objectCode);
        workingAppointmentFunding.setHourlyPaid(hourlyPaid);

        // update the access flags of the current funding line
        boolean accessModeUpdated = salarySettingForm.updateAccessMode(workingAppointmentFunding, errorMap);
        if (!accessModeUpdated) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        // have no permission to do salary setting on the new line
        if (workingAppointmentFunding.isDisplayOnlyMode()) {
            errorMap.putError(BCPropertyConstants.NEW_BCAF_LINE, BCKeyConstants.ERROR_NO_SALARY_SETTING_PERMISSION, workingAppointmentFunding.getAppointmentFundingString());
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        // acquire a lock for the new appointment funding line
        boolean gotLocks = salarySettingForm.acquirePositionAndFundingLocks(workingAppointmentFunding, errorMap);
        if (!gotLocks) {
            return mapping.findForward(KFSConstants.MAPPING_BASIC);
        }

        appointmentFundings.add(workingAppointmentFunding);
        salarySettingForm.setNewBCAFLine(salarySettingForm.createNewAppointmentFundingLine());

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    // determine whether any active funding line is involved leave
    protected boolean hasFundingLineInvolvedLeave(List<PendingBudgetConstructionAppointmentFunding> activeAppointmentFundings) {
        for (PendingBudgetConstructionAppointmentFunding appointmentFunding : activeAppointmentFundings) {
            String leaveDurationCode = appointmentFunding.getAppointmentFundingDurationCode();

            if (!StringUtils.equals(leaveDurationCode, NONE.durationCode)) {
                return true;
            }
        }
        return false;
    }

    // determine whether any active funding line is involved in leave without pay
    protected boolean hasFundingLineInvolvedLeaveWithoutPay(List<PendingBudgetConstructionAppointmentFunding> activeAppointmentFundings) {
        for (PendingBudgetConstructionAppointmentFunding appointmentFunding : activeAppointmentFundings) {
            String leaveDurationCode = appointmentFunding.getAppointmentFundingDurationCode();

            if (StringUtils.equals(leaveDurationCode, LWPA.durationCode) || StringUtils.equals(leaveDurationCode, LWPF.durationCode)) {
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

        if (appointmentFunding.getAppointmentTotalIntendedAmount() == null) {
            appointmentFunding.setAppointmentTotalIntendedAmount(KualiInteger.ZERO);
            appointmentFunding.setAppointmentTotalIntendedFteQuantity(BigDecimal.ZERO);
        }

        if (appointmentFunding.getAppointmentTotalIntendedFteQuantity() == null) {
            appointmentFunding.setAppointmentTotalIntendedFteQuantity(BigDecimal.ZERO);
        }
    }

    // clear the appointment funding lines that have been purged
    protected void clearPurgedAppointmentFundings(List<PendingBudgetConstructionAppointmentFunding> appointmentFundings) {
        List<PendingBudgetConstructionAppointmentFunding> purgedAppointmentFundings = new ArrayList<PendingBudgetConstructionAppointmentFunding>();
        for (PendingBudgetConstructionAppointmentFunding appointmentFunding : appointmentFundings) {
            if (appointmentFunding.isPurged()) {
                purgedAppointmentFundings.add(appointmentFunding);
            }
        }
        appointmentFundings.removeAll(purgedAppointmentFundings);
    }

    /**
     * unlock the position only, called as last action before a close or exit handling the case where there are no funding lines
     * attached yet.
     */
    protected void unlockPositionOnly(PositionSalarySettingForm positionSalarySettingForm) {

        Integer universityFiscalYear = positionSalarySettingForm.getBudgetConstructionPosition().getUniversityFiscalYear();
        String positionNumber = positionSalarySettingForm.getBudgetConstructionPosition().getPositionNumber();
        String principalId = GlobalVariables.getUserSession().getPerson().getPrincipalId();

        // unlock position
        SpringContext.getBean(LockService.class).unlockPosition(positionNumber, universityFiscalYear, principalId);

    }

    /**
     * This should return the SynchronizationCheckType based on the context. SynchronizationCheckType.POSN is used in
     * IncumbentSalarySetting and SynchronizationCheckType.EID is used in PositionSalarySetting
     * 
     * @return
     */
    public abstract SynchronizationCheckType getSynchronizationCheckType();
}
