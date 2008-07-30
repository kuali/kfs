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

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.module.bc.BCKeyConstants;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPosition;
import org.kuali.kfs.module.bc.service.BudgetConstructionPositionService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;

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

        Map<String, Object> fieldValues = positionSalarySettingForm.getKeyMapOfSalarySettingItem();
        BudgetConstructionPosition budgetConstructionPosition = (BudgetConstructionPosition) businessObjectService.findByPrimaryKey(BudgetConstructionPosition.class, fieldValues);

        if (budgetConstructionPosition == null) {
            String positionNumber = (String) fieldValues.get(KFSPropertyConstants.POSITION_NUMBER);
            String fiscalYear = (String) fieldValues.get(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
            GlobalVariables.getErrorMap().putError(KFSConstants.GLOBAL_MESSAGES, BCKeyConstants.ERROR_POSITION_NOT_FOUND, positionNumber, fiscalYear);

            return this.returnToCaller(mapping, form, request, response);
        }

        if (positionSalarySettingForm.isRefreshPositionBeforeSalarySetting()) {
            budgetConstructionPositionService.refreshPositionFromExternal(positionSalarySettingForm.getUniversityFiscalYear(), positionSalarySettingForm.getPositionNumber());
        }

        positionSalarySettingForm.setBudgetConstructionPosition(budgetConstructionPosition);
        if (positionSalarySettingForm.isSingleAccountMode()) {
            // TODO: Single account mode constrains the funding lines displayed to only those that are associated with the document
            // the user has open.
        }

        boolean isSuccessfullyProcessed = positionSalarySettingForm.postProcessBCAFLines();
        positionSalarySettingForm.setNewBCAFLine(positionSalarySettingForm.createNewAppointmentFundingLine());
        
        //acquire position and funding locks for the associated funding lines
        boolean gotLocks = positionSalarySettingForm.acquirePositionAndFundingLocks();
        if (!gotLocks) {
            return this.returnToCaller(mapping, form, request, response);
        }

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
}
