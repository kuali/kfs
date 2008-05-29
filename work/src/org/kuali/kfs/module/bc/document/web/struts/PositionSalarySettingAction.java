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
package org.kuali.module.budget.web.struts.action;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.budget.bo.BudgetConstructionPosition;
import org.kuali.module.budget.bo.PendingBudgetConstructionAppointmentFunding;
import org.kuali.module.budget.web.struts.form.DetailSalarySettingForm;
import org.kuali.module.budget.web.struts.form.PositionSalarySettingForm;

public class PositionSalarySettingAction extends DetailSalarySettingAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PositionSalarySettingAction.class);

    /**
     * @see org.kuali.module.budget.web.struts.action.DetailSalarySettingAction#loadExpansionScreen(org.apache.struts.action.ActionMapping,
     *      org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward loadExpansionScreen(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        PositionSalarySettingForm positionSalarySettingForm = (PositionSalarySettingForm) form;

        // use the passed url parms to get the record from DB
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, positionSalarySettingForm.getUniversityFiscalYear());
        fieldValues.put(KFSPropertyConstants.POSITION_NUMBER, positionSalarySettingForm.getPositionNumber());

        BudgetConstructionPosition budgetConstructionPosition = (BudgetConstructionPosition) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(BudgetConstructionPosition.class, fieldValues);
        if (budgetConstructionPosition == null) {
            // TODO this is an RI error need to report it
        }

        positionSalarySettingForm.setBudgetConstructionPosition(budgetConstructionPosition);
        positionSalarySettingForm.populateBCAFLines();        
        positionSalarySettingForm.setNewBCAFLine(this.createNewAppointmentFundingLine(positionSalarySettingForm));

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
    
    /**
     * @see org.kuali.module.budget.web.struts.action.DetailSalarySettingAction#createNewAppointmentFundingLine(org.kuali.module.budget.web.struts.form.DetailSalarySettingForm)
     */
    @Override
    public PendingBudgetConstructionAppointmentFunding createNewAppointmentFundingLine(DetailSalarySettingForm salarySettingForm) {
        PositionSalarySettingForm positionSalarySettingForm = (PositionSalarySettingForm) salarySettingForm;
        BudgetConstructionPosition budgetConstructionPosition = positionSalarySettingForm.getBudgetConstructionPosition();
        
        PendingBudgetConstructionAppointmentFunding appointmentFunding = super.createNewAppointmentFundingLine(positionSalarySettingForm);
               
        appointmentFunding.setPositionNumber(budgetConstructionPosition.getPositionNumber());
        appointmentFunding.setAppointmentFundingMonth(budgetConstructionPosition.getIuNormalWorkMonths());
        
        return appointmentFunding;
    }
}
