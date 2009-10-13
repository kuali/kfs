/*
 * Copyright 2007 The Kuali Foundation
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

import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionIntendedIncumbent;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.sys.KFSPropertyConstants;


public class IncumbentSalarySettingForm extends DetailSalarySettingForm {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(IncumbentSalarySettingForm.class);

    private boolean refreshIncumbentBeforeSalarySetting;

    private BudgetConstructionIntendedIncumbent budgetConstructionIntendedIncumbent;

    /**
     * Constructs a IncumbentSalarySettingForm.java.
     */
    public IncumbentSalarySettingForm() {
        super();

        setBudgetConstructionIntendedIncumbent(new BudgetConstructionIntendedIncumbent());
    }

    /**
     * @see org.kuali.kfs.module.bc.document.web.struts.DetailSalarySettingForm#createNewAppointmentFundingLine()
     */
    @Override
    public PendingBudgetConstructionAppointmentFunding createNewAppointmentFundingLine() {
        PendingBudgetConstructionAppointmentFunding appointmentFunding = super.createNewAppointmentFundingLine();

        appointmentFunding.setEmplid(this.getBudgetConstructionIntendedIncumbent().getEmplid());

        return appointmentFunding;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.web.struts.SalarySettingBaseForm#getKeyMapOfSalarySettingItem()
     */
    @Override
    public Map<String, Object> getKeyMapOfSalarySettingItem() {
        Map<String, Object> keyMap = new HashMap<String, Object>();
        keyMap.put(KFSPropertyConstants.EMPLID, this.getEmplid());

        return keyMap;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.web.struts.DetailSalarySettingForm#getRefreshCallerName()
     */
    @Override
    public String getRefreshCallerName() {
        return BCConstants.INCUMBENT_SALARY_SETTING_REFRESH_CALLER;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.web.struts.SalarySettingForm#getAppointmentFundings()
     */
    @Override
    public List<PendingBudgetConstructionAppointmentFunding> getAppointmentFundings() {
        return this.getBudgetConstructionIntendedIncumbent().getPendingBudgetConstructionAppointmentFunding();
    }

    /**
     * Gets the budgetConstructionIntendedIncumbent attribute.
     * 
     * @return Returns the budgetConstructionIntendedIncumbent.
     */
    public BudgetConstructionIntendedIncumbent getBudgetConstructionIntendedIncumbent() {
        return budgetConstructionIntendedIncumbent;
    }

    /**
     * Sets the budgetConstructionIntendedIncumbent attribute value.
     * 
     * @param budgetConstructionIntendedIncumbent The budgetConstructionIntendedIncumbent to set.
     */
    public void setBudgetConstructionIntendedIncumbent(BudgetConstructionIntendedIncumbent budgetConstructionIntendedIncumbent) {
        this.budgetConstructionIntendedIncumbent = budgetConstructionIntendedIncumbent;
    }

    /**
     * Gets the refreshIncumbentBeforeSalarySetting attribute.
     * 
     * @return Returns the refreshIncumbentBeforeSalarySetting.
     */
    public boolean isRefreshIncumbentBeforeSalarySetting() {
        return refreshIncumbentBeforeSalarySetting;
    }

    /**
     * Sets the refreshIncumbentBeforeSalarySetting attribute value.
     * 
     * @param refreshIncumbentBeforeSalarySetting The refreshIncumbentBeforeSalarySetting to set.
     */
    public void setRefreshIncumbentBeforeSalarySetting(boolean refreshIncumbentBeforeSalarySetting) {
        this.refreshIncumbentBeforeSalarySetting = refreshIncumbentBeforeSalarySetting;
    }

    /**
     * Gets the documentTitle
     * @return
     */
    public String getDocumentTitle() {
        return BCConstants.INCUMBENT_SALARY_SETTING_TITLE;
    }

}
