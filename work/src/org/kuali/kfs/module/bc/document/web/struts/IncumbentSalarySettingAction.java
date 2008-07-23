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
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionIntendedIncumbent;
import org.kuali.kfs.module.bc.service.BudgetConstructionIntendedIncumbentService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;


public class IncumbentSalarySettingAction extends DetailSalarySettingAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(IncumbentSalarySettingAction.class);

    /**
     * @see org.kuali.kfs.module.bc.document.web.struts.SalarySettingBaseAction#loadExpansionScreen(org.apache.struts.action.ActionMapping, org.apache.struts.action.ActionForm, javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public ActionForward loadExpansionScreen(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        IncumbentSalarySettingForm incumbentSalarySettingForm = (IncumbentSalarySettingForm) form;

        // use the passed url parms to get the record from DB
        Map<String, Object> fieldValues = incumbentSalarySettingForm.getKeyMapOfSalarySettingItem();

        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        BudgetConstructionIntendedIncumbent budgetConstructionIntendedIncumbent = (BudgetConstructionIntendedIncumbent) businessObjectService.findByPrimaryKey(BudgetConstructionIntendedIncumbent.class, fieldValues);
        if (budgetConstructionIntendedIncumbent == null) {
            // TODO this is an RI error need to report it
        }
        
        if (incumbentSalarySettingForm.isRefreshIncumbentBeforeSalarySetting()) {
            SpringContext.getBean(BudgetConstructionIntendedIncumbentService.class).refreshIncumbentFromExternal(incumbentSalarySettingForm.getEmplid());
        }

        incumbentSalarySettingForm.setBudgetConstructionIntendedIncumbent(budgetConstructionIntendedIncumbent);
        if(incumbentSalarySettingForm.isSingleAccountMode()) {
            // TODO: Single account mode constrains the funding lines displayed to only those that are associated with the document the user has open.
        }
        
        incumbentSalarySettingForm.populateBCAFLines();
        incumbentSalarySettingForm.setNewBCAFLine(incumbentSalarySettingForm.createNewAppointmentFundingLine());

        return mapping.findForward(KFSConstants.MAPPING_BASIC);
    }
}
