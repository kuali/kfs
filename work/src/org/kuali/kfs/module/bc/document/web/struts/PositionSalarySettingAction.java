/*
 * Copyright 2007 The Kuali Foundation.
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
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.bc.BCKeyConstants;
import org.kuali.kfs.module.bc.BCPropertyConstants;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionLockStatus;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPosition;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.document.service.LockService;
import org.kuali.kfs.module.bc.service.BudgetConstructionPositionService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.KFSConstants.BudgetConstructionConstants;
import org.kuali.kfs.sys.KFSConstants.BudgetConstructionConstants.LockStatus;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.GlobalVariables;

/**
 * the struts action for the salary setting for position
 */
public class PositionSalarySettingAction extends DetailSalarySettingAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PositionSalarySettingAction.class);

    private BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
    private BudgetConstructionPositionService budgetConstructionPositionService = SpringContext.getBean(BudgetConstructionPositionService.class);

    /**
     * @see org.kuali.kfs.module.bc.document.web.struts.SalarySettingBaseAction#loadExpansionScreen(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward loadExpansionScreen(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PositionSalarySettingForm positionSalarySettingForm = (PositionSalarySettingForm) form;

        // update the position record if required
        ActionForward resyncAction = this.resyncPositionBeforeSalarySetting(mapping, form, request, response);
        if (resyncAction != null) {
            return resyncAction;
        }

        Map<String, Object> fieldValues = positionSalarySettingForm.getKeyMapOfSalarySettingItem();
        BudgetConstructionPosition budgetConstructionPosition = (BudgetConstructionPosition) businessObjectService.findByPrimaryKey(BudgetConstructionPosition.class, fieldValues);
        if (budgetConstructionPosition == null) {
            String positionNumber = (String) fieldValues.get(KFSPropertyConstants.POSITION_NUMBER);
            String fiscalYear = (String) fieldValues.get(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
            GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_MESSAGES, BCKeyConstants.ERROR_POSITION_NOT_FOUND, positionNumber, fiscalYear);

            return this.returnToCaller(mapping, form, request, response);
        }

        positionSalarySettingForm.setBudgetConstructionPosition(budgetConstructionPosition);
        if (positionSalarySettingForm.isSingleAccountMode()) {
            positionSalarySettingForm.pickAppointmentFundingsForSingleAccount();
        }

        // acquire position and funding locks for the associated funding lines
        if (!positionSalarySettingForm.isViewOnlyEntry()) {
            positionSalarySettingForm.postProcessBCAFLines();
            positionSalarySettingForm.setNewBCAFLine(positionSalarySettingForm.createNewAppointmentFundingLine());

            boolean gotLocks = positionSalarySettingForm.acquirePositionAndFundingLocks();
            if (!gotLocks) {
                return this.returnToCaller(mapping, form, request, response);
            }
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }

    /**
     * enable to send warning after saving
     * 
     * @see org.kuali.kfs.module.bc.document.web.struts.DetailSalarySettingAction#save(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward save(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ActionForward saveAction = super.save(mapping, form, request, response);

        PositionSalarySettingForm positionSalarySettingForm = (PositionSalarySettingForm) form;
        this.sendWarnings(positionSalarySettingForm, GlobalVariables.getMessageList());

        return saveAction;
    }

    /**
     * send warning messsages back to the caller
     * 
     * @param positionSalarySettingForm the given position salary setting form
     * @param warnings the warning list that can hold the warning messages if any
     */
    public void sendWarnings(PositionSalarySettingForm positionSalarySettingForm, List<String> warnings) {
        List<PendingBudgetConstructionAppointmentFunding> activeAppointmentFundings = positionSalarySettingForm.getActiveFundingLines();
        if (activeAppointmentFundings == null || activeAppointmentFundings.isEmpty()) {
            return;
        }

        boolean hasFundingLineInvolveLeave = this.hasFundingLineInvolvedLeave(activeAppointmentFundings);
        BudgetConstructionPosition budgetConstructionPosition = positionSalarySettingForm.getBudgetConstructionPosition();

        BigDecimal positionFte = budgetConstructionPosition.getPositionFullTimeEquivalency();
        BigDecimal requestedFteQuantityTotal = positionSalarySettingForm.getAppointmentRequestedFteQuantityTotal();
        if (!hasFundingLineInvolveLeave && requestedFteQuantityTotal.compareTo(positionFte) != 0) {
            warnings.add(BCKeyConstants.WARNING_FTE_NOT_EQUAL);
        }

        BigDecimal positionStandardHours = budgetConstructionPosition.getPositionStandardHoursDefault();
        BigDecimal requestedStandardHoursTotal = positionSalarySettingForm.getAppointmentRequestedStandardHoursTotal();
        if (!hasFundingLineInvolveLeave && requestedStandardHoursTotal.compareTo(positionStandardHours) != 0) {
            warnings.add(BCKeyConstants.WARNING_WORKING_HOUR_NOT_EQUAL);
        }

        for (PendingBudgetConstructionAppointmentFunding activeFunding : activeAppointmentFundings) {
            if (activeFunding.isPositionSalaryChangeIndicator()) {
                warnings.add(BCKeyConstants.WARNING_RECALCULATE_NEEDED);
            }
        }
    }

    /**
     * @see org.kuali.kfs.module.bc.document.web.struts.SalarySettingBaseAction#getFundingAwareObjectName()
     */
    @Override
    protected String getFundingAwareObjectName() {
        return BCPropertyConstants.BUDGET_CONSTRUCTION_POSITION;
    }

    /**
     * resyn position brefore performing salary setting
     */
    private ActionForward resyncPositionBeforeSalarySetting(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PositionSalarySettingForm positionSalarySettingForm = (PositionSalarySettingForm) form;

        if (!positionSalarySettingForm.isRefreshPositionBeforeSalarySetting()) {
            return null;
        }

        Integer universityFiscalYear = positionSalarySettingForm.getUniversityFiscalYear();
        String positionNumber = positionSalarySettingForm.getPositionNumber();
        String personUniversalIdentifier = GlobalVariables.getUserSession().getUniversalUser().getPersonUniversalIdentifier();

        // attempt to lock position and associated funding
        BudgetConstructionLockStatus bcLockStatus = SpringContext.getBean(LockService.class).lockPositionAndActiveFunding(universityFiscalYear, positionNumber, personUniversalIdentifier);
        if (!bcLockStatus.getLockStatus().equals(BudgetConstructionConstants.LockStatus.SUCCESS)) {
            GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_ERRORS, BCKeyConstants.ERROR_POSITION_LOCK_NOT_OBTAINED, new String[] { universityFiscalYear.toString(), positionNumber });
            return this.returnToCaller(mapping, form, request, response);
        }

        try {
            budgetConstructionPositionService.refreshPositionFromExternal(universityFiscalYear, positionNumber);
        }
        finally {
            // release locks
            LockStatus lockStatus = SpringContext.getBean(LockService.class).unlockPositionAndActiveFunding(universityFiscalYear, positionNumber, personUniversalIdentifier);
            if (!lockStatus.equals(BudgetConstructionConstants.LockStatus.SUCCESS)) {
                LOG.error(String.format("unable to unlock position and active funding records: %s, %s, %s", universityFiscalYear, positionNumber, personUniversalIdentifier));
                throw new RuntimeException(String.format("unable to unlock position and active funding records: %s, %s, %s", universityFiscalYear, positionNumber, personUniversalIdentifier));
            }
        }

        return null;
    }
}
