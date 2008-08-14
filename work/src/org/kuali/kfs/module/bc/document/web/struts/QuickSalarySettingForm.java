/*
 * Copyright 2006-2007 The Kuali Foundation.
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

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.businessobject.SalarySettingExpansion;
import org.kuali.kfs.module.bc.document.authorization.BudgetConstructionDocumentAuthorizer;
import org.kuali.kfs.sys.ObjectUtil;


public class QuickSalarySettingForm extends SalarySettingBaseForm {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(QuickSalarySettingForm.class);

    private SalarySettingExpansion salarySettingExpansion;

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

        this.populateAuthorizationFields(new BudgetConstructionDocumentAuthorizer());
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
}
