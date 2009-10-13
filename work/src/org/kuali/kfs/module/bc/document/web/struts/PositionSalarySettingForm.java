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
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPosition;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.sys.KFSPropertyConstants;

public class PositionSalarySettingForm extends DetailSalarySettingForm {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PositionSalarySettingForm.class);

    private boolean refreshPositionBeforeSalarySetting;

    private BudgetConstructionPosition budgetConstructionPosition;


    /**
     * Constructs a PositionSalarySettingForm.java.
     */
    public PositionSalarySettingForm() {
        super();

        this.setBudgetConstructionPosition(new BudgetConstructionPosition());
    }

    /**
     * @see org.kuali.kfs.module.bc.document.web.struts.DetailSalarySettingForm#createNewAppointmentFundingLine()
     */
    @Override
    public PendingBudgetConstructionAppointmentFunding createNewAppointmentFundingLine() {
        PendingBudgetConstructionAppointmentFunding appointmentFunding = super.createNewAppointmentFundingLine();

        appointmentFunding.setPositionNumber(this.getBudgetConstructionPosition().getPositionNumber());
        appointmentFunding.setFinancialObjectCode(this.getBudgetConstructionPosition().getIuDefaultObjectCode());

        return appointmentFunding;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.web.struts.SalarySettingBaseForm#getKeyMapOfSalarySettingItem()
     */
    @Override
    public Map<String, Object> getKeyMapOfSalarySettingItem() {
        Map<String, Object> keyMap = new HashMap<String, Object>();

        keyMap.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, this.getUniversityFiscalYear());
        keyMap.put(KFSPropertyConstants.POSITION_NUMBER, this.getPositionNumber());

        return keyMap;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.web.struts.DetailSalarySettingForm#getRefreshCallerName()
     */
    @Override
    public String getRefreshCallerName() {
        return BCConstants.POSITION_SALARY_SETTING_REFRESH_CALLER;
    }

    /**
     * @see org.kuali.kfs.module.bc.document.web.struts.SalarySettingForm#getAppointmentFundings()
     */
    @Override
    public List<PendingBudgetConstructionAppointmentFunding> getAppointmentFundings() {
        return this.getBudgetConstructionPosition().getPendingBudgetConstructionAppointmentFunding();
    }

    /**
     * Gets the budgetConstructionPosition attribute.
     * 
     * @return Returns the budgetConstructionPosition.
     */
    public BudgetConstructionPosition getBudgetConstructionPosition() {
        return this.budgetConstructionPosition;
    }

    /**
     * Sets the budgetConstructionPosition attribute value.
     * 
     * @param budgetConstructionPosition The budgetConstructionPosition to set.
     */
    public void setBudgetConstructionPosition(BudgetConstructionPosition budgetConstructionPosition) {
        this.budgetConstructionPosition = budgetConstructionPosition;
    }

    /**
     * checks if at least one active edit-able appointment funding line has a sync flag set
     * 
     * @return true or false
     */
    public boolean isPendingPositionSalaryChange(){

        List<PendingBudgetConstructionAppointmentFunding> activeAppointmentFundings = this.getActiveFundingLines();
        for (PendingBudgetConstructionAppointmentFunding appointmentFunding : activeAppointmentFundings){
            if (!appointmentFunding.isDisplayOnlyMode()){
                if (appointmentFunding.isPositionChangeIndicator()){
                    return true;
                }
            }
        }
        return false;
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
     * Gets the documentTitle
     * @return
     */
    public String getDocumentTitle() {
        return BCConstants.POSITION_SALARY_SETTING_TITLE;
    }
}
