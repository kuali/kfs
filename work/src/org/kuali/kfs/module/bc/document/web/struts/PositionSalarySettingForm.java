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
