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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.module.bc.BCKeyConstants;
import org.kuali.kfs.module.bc.BCPropertyConstants;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.businessobject.SalarySettingExpansion;
import org.kuali.kfs.module.bc.document.service.SalarySettingService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;

/**
 * the base struts action for the detail salary setting
 */
public abstract class DetailSalarySettingAction extends SalarySettingBaseAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DetailSalarySettingAction.class);

    private SalarySettingService salarySettingService = SpringContext.getBean(SalarySettingService.class);

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
        UniversalUser universalUser = GlobalVariables.getUserSession().getUniversalUser();
        salarySettingForm.releaseLocks(salarySettingForm.getLockedPositions(), salarySettingForm.getAppointmentFundings(), universalUser);

        // return to caller if the current salary setting is in the budget by account mode
        if (salarySettingForm.isBudgetByAccountMode()) {
            return this.returnToCaller(mapping, form, request, response);
        }

        salarySettingForm.setOrgSalSetClose(true);
        return closeActionForward;
    }

    /**
     * @see org.kuali.core.web.struts.action.KualiAction#refresh(org.apache.struts.action.ActionMapping,
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
        List<PendingBudgetConstructionAppointmentFunding> appointmentFundings = salarySettingForm.getAppointmentFundings();

        Set<SalarySettingExpansion> salarySettingExpansionSet = new HashSet<SalarySettingExpansion>();
        for (PendingBudgetConstructionAppointmentFunding fundingLine : appointmentFundings) {
            SalarySettingExpansion salarySettingExpansion = salarySettingService.retriveSalarySalarySettingExpansion(fundingLine);

            if (salarySettingExpansion != null) {
                salarySettingExpansionSet.add(salarySettingExpansion);
            }
        }

        salarySettingService.saveAppointmentFundings(appointmentFundings);

        for (SalarySettingExpansion salarySettingExpansion : salarySettingExpansionSet) {
            salarySettingExpansion.refreshReferenceObject(BCPropertyConstants.PENDING_BUDGET_CONSTRUCTION_APPOINTMENT_FUNDING);
            salarySettingService.saveSalarySetting(salarySettingExpansion);
        }

        GlobalVariables.getMessageList().add(BCKeyConstants.MESSAGE_SALARY_SETTING_SAVED);
        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * adds an appointment funding line to the set of existing funding lines
     */
    public ActionForward insertSalarySettingLine(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DetailSalarySettingForm salarySettingForm = (DetailSalarySettingForm) form;
        List<PendingBudgetConstructionAppointmentFunding> appointmentFundings = salarySettingForm.getAppointmentFundings();

        PendingBudgetConstructionAppointmentFunding newAppointmentFunding = salarySettingForm.getNewBCAFLine();
        this.applyDefaultValuesIfEmpty(newAppointmentFunding);
        appointmentFundings.add(newAppointmentFunding);

        salarySettingForm.populateBCAFLines();
        salarySettingForm.setNewBCAFLine(salarySettingForm.createNewAppointmentFundingLine());

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    // apply the default values to the certain fields when the fields are empty
    private void applyDefaultValuesIfEmpty(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        if (StringUtils.isBlank(appointmentFunding.getSubAccountNumber())) {
            appointmentFunding.setSubAccountNumber(KFSConstants.getDashSubAccountNumber());
        }

        if (StringUtils.isBlank(appointmentFunding.getFinancialSubObjectCode())) {
            appointmentFunding.setFinancialSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
        }
    }
}
