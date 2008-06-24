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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.kuali.kfs.module.bc.BCConstants;
import org.kuali.kfs.module.bc.BCPropertyConstants;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.businessobject.SalarySettingExpansion;
import org.kuali.kfs.module.bc.document.authorization.BudgetConstructionDocumentAuthorizer;
import org.kuali.kfs.sys.KFSPropertyConstants;


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
     * @see org.kuali.core.web.struts.form.KualiForm#populate(javax.servlet.http.HttpServletRequest)
     */
    @Override
    public void populate(HttpServletRequest request) {
        super.populate(request);

        this.populateBCAFLines();

        this.populateAuthorizationFields(new BudgetConstructionDocumentAuthorizer());
    }

    /**
     * Populates the dependent fields of objects contained within the BCAF line
     */
    public void populateBCAFLine(PendingBudgetConstructionAppointmentFunding appointmentFunding) {
        appointmentFunding.refreshNonUpdateableReferences();
        appointmentFunding.refreshReferenceObject(BCPropertyConstants.BUDGET_CONSTRUCTION_INTENDED_INCUMBENT);
        appointmentFunding.refreshReferenceObject(BCPropertyConstants.BUDGET_CONSTRUCTION_POSITION);
        appointmentFunding.refreshReferenceObject(BCPropertyConstants.BUDGET_CONSTRUCTION_CALCULATED_SALARY_FOUNDATION_TRACKER);
    }

    /**
     * get the key map for the salary setting expension
     * 
     * @return the key map for the salary setting expension
     */
    public Map<String, Object> getKeyMapOfSalarySettingItem() {
        Map<String, Object> keyMap = new HashMap<String, Object>();

        keyMap.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.getDocumentNumber());
        keyMap.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, this.getUniversityFiscalYear());
        keyMap.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, this.getChartOfAccountsCode());
        keyMap.put(KFSPropertyConstants.ACCOUNT_NUMBER, this.getAccountNumber());
        keyMap.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, this.getSubAccountNumber());
        keyMap.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, this.getFinancialObjectCode());
        keyMap.put(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE, this.getFinancialSubObjectCode());
        keyMap.put(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE, this.getFinancialBalanceTypeCode());
        keyMap.put(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE, this.getFinancialObjectTypeCode());

        return keyMap;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.web.struts.SalarySettingForm#getAppointmentFundings()
     */
    @Override
    public List<PendingBudgetConstructionAppointmentFunding> getAppointmentFundings() {
        return this.getSalarySettingExpansion().getPendingBudgetConstructionAppointmentFunding();
    }

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
