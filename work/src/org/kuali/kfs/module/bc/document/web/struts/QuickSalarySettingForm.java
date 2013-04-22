/*
 * Copyright 2006-2008 The Kuali Foundation
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
