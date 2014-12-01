/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
