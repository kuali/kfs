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

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.businessobject.SalarySettingExpansion;
import org.kuali.kfs.sys.ObjectUtil;


public class QuickSalarySettingForm extends SalarySettingBaseForm {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(QuickSalarySettingForm.class);

    private SalarySettingExpansion salarySettingExpansion;
    private boolean refreshPositionBeforeSalarySetting;
    private boolean refreshIncumbentBeforeSalarySetting;

    /**
     * Constructs a QuickSalarySettingForm.java.
     */
    public QuickSalarySettingForm() {
        super();
        this.setSalarySettingExpansion(new SalarySettingExpansion());
    }

    /**
     * @see org.kuali.rice.kns.web.struts.form.KualiForm#populate(javax.servlet.http.HttpServletRequest)
     */
    @Override
    public void populate(HttpServletRequest request) {

        super.populate(request);
        this.populateBCAFLines();
    }

    /**
     * get the key map for the salary setting expension
     *
     * @return the key map for the salary setting expension
     */
    @Override
    public Map<String, Object> getKeyMapOfSalarySettingItem() {
        return ObjectUtil.buildPropertyMap(this, SalarySettingExpansion.getPrimaryKeyFields());
    }

    /**
     * @see org.kuali.kfs.module.bc.document.web.struts.SalarySettingForm#getAppointmentFundings()
     */
    @Override
    public List<PendingBudgetConstructionAppointmentFunding> getAppointmentFundings() {
        return this.getSalarySettingExpansion().getPendingBudgetConstructionAppointmentFunding();
    }

    /**
     * @see org.kuali.kfs.module.bc.document.web.struts.SalarySettingBaseForm#getRefreshCallerName()
     */
    @Override
    public String getRefreshCallerName() {
        return BCConstants.QUICK_SALARY_SETTING_REFRESH_CALLER;
    }

    /**
     * Gets the salarySettingExpansion attribute.
     *
     * @return Returns the salarySettingExpansion.
     */
    public SalarySettingExpansion getSalarySettingExpansion() {
        return salarySettingExpansion;
    }

    /**
     * Sets the salarySettingExpansion attribute value.
     *
     * @param salarySettingExpansion The salarySettingExpansion to set.
     */
    public void setSalarySettingExpansion(SalarySettingExpansion salarySettingExpansion) {
        this.salarySettingExpansion = salarySettingExpansion;
    }

    /**
     * Gets the refreshPositionBeforeSalarySetting attribute.
     *
     * @return Returns the refreshPositionBeforeSalarySetting.
     */
    public boolean isRefreshPositionBeforeSalarySetting() {
        return refreshPositionBeforeSalarySetting;
    }

    /**
     * Sets the refreshPositionBeforeSalarySetting attribute value.
     *
     * @param refreshPositionBeforeSalarySetting The refreshPositionBeforeSalarySetting to set.
     */
    public void setRefreshPositionBeforeSalarySetting(boolean refreshPositionBeforeSalarySetting) {
        this.refreshPositionBeforeSalarySetting = refreshPositionBeforeSalarySetting;
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
     * @see org.kuali.kfs.module.bc.document.web.struts.SalarySettingBaseForm#isBudgetByAccountMode()
     */
    @Override
    public boolean isBudgetByAccountMode() {
        return true;
    }

    /**
     * Gets the viewOnlyEntry attribute. In the quick salary setting context viewOnlyEntry checks the system view only and account
     * access. Both system view only and full entry can both exist (be true), it just means that the user would have edit access if
     * the system was not in view only mode. These facts are used to determine the effective viewOnlyEntry value
     *
     * @return Returns the viewOnlyEntry.
     */
    @Override
    public boolean isViewOnlyEntry() {
        return super.isViewOnlyEntry() || !isEditAllowed();
    }

}
