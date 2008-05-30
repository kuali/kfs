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

package org.kuali.module.budget.bo;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiInteger;
import org.kuali.core.util.TypedArrayList;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.budget.util.SalarySettingCalculator;

public class BudgetConstructionIntendedIncumbent extends PersistableBusinessObjectBase implements PendingBudgetConstructionAppointmentFundingAware {

    private String emplid;
    private String personName;
    private String setidSalary;
    private String salaryAdministrationPlan;
    private String grade;
    private String iuClassificationLevel;

    private List<BudgetConstructionSalarySocialSecurityNumber> budgetConstructionSalarySocialSecurity;
    private List<PendingBudgetConstructionAppointmentFunding> pendingBudgetConstructionAppointmentFunding;

    /**
     * Default constructor.
     */
    public BudgetConstructionIntendedIncumbent() {
        super();
        
        budgetConstructionSalarySocialSecurity = new TypedArrayList(BudgetConstructionSalarySocialSecurityNumber.class);
        pendingBudgetConstructionAppointmentFunding = new TypedArrayList(PendingBudgetConstructionAppointmentFunding.class);
    }

    /**
     * Gets the emplid attribute.
     * 
     * @return Returns the emplid
     */
    public String getEmplid() {
        return emplid;
    }

    /**
     * Sets the emplid attribute.
     * 
     * @param emplid The emplid to set.
     */
    public void setEmplid(String emplid) {
        this.emplid = emplid;
    }


    /**
     * Gets the personName attribute.
     * 
     * @return Returns the personName
     */
    public String getPersonName() {
        return personName;
    }

    /**
     * Sets the personName attribute.
     * 
     * @param personName The personName to set.
     */
    public void setPersonName(String personName) {
        this.personName = personName;
    }


    /**
     * Gets the setidSalary attribute.
     * 
     * @return Returns the setidSalary
     */
    public String getSetidSalary() {
        return setidSalary;
    }

    /**
     * Sets the setidSalary attribute.
     * 
     * @param setidSalary The setidSalary to set.
     */
    public void setSetidSalary(String setidSalary) {
        this.setidSalary = setidSalary;
    }


    /**
     * Gets the salaryAdministrationPlan attribute.
     * 
     * @return Returns the salaryAdministrationPlan
     */
    public String getSalaryAdministrationPlan() {
        return salaryAdministrationPlan;
    }

    /**
     * Sets the salaryAdministrationPlan attribute.
     * 
     * @param salaryAdministrationPlan The salaryAdministrationPlan to set.
     */
    public void setSalaryAdministrationPlan(String salaryAdministrationPlan) {
        this.salaryAdministrationPlan = salaryAdministrationPlan;
    }


    /**
     * Gets the grade attribute.
     * 
     * @return Returns the grade
     */
    public String getGrade() {
        return grade;
    }

    /**
     * Sets the grade attribute.
     * 
     * @param grade The grade to set.
     */
    public void setGrade(String grade) {
        this.grade = grade;
    }


    /**
     * Gets the iuClassificationLevel attribute.
     * 
     * @return Returns the iuClassificationLevel
     */
    public String getIuClassificationLevel() {
        return iuClassificationLevel;
    }

    /**
     * Sets the iuClassificationLevel attribute.
     * 
     * @param iuClassificationLevel The iuClassificationLevel to set.
     */
    public void setIuClassificationLevel(String iuClassificationLevel) {
        this.iuClassificationLevel = iuClassificationLevel;
    }


    /**
     * Gets the budgetConstructionSalarySocialSecurity attribute.
     * 
     * @return Returns the budgetConstructionSalarySocialSecurity.
     */
    public List<BudgetConstructionSalarySocialSecurityNumber> getBudgetConstructionSalarySocialSecurity() {
        return budgetConstructionSalarySocialSecurity;
    }

    /**
     * Sets the budgetConstructionSalarySocialSecurity attribute value.
     * 
     * @param budgetConstructionSalarySocialSecurity The budgetConstructionSalarySocialSecurity to set.
     */
    @Deprecated
    public void setBudgetConstructionSalarySocialSecurity(List<BudgetConstructionSalarySocialSecurityNumber> budgetConstructionSalarySocialSecurity) {
        this.budgetConstructionSalarySocialSecurity = budgetConstructionSalarySocialSecurity;
    }

    /**
     * Gets the pendingBudgetConstructionAppointmentFunding attribute.
     * 
     * @return Returns the pendingBudgetConstructionAppointmentFunding.
     */
    public List<PendingBudgetConstructionAppointmentFunding> getPendingBudgetConstructionAppointmentFunding() {
        return pendingBudgetConstructionAppointmentFunding;
    }

    /**
     * Sets the pendingBudgetConstructionAppointmentFunding attribute value.
     * 
     * @param pendingBudgetConstructionAppointmentFunding The pendingBudgetConstructionAppointmentFunding to set.
     */
    @Deprecated
    public void setPendingBudgetConstructionAppointmentFunding(List<PendingBudgetConstructionAppointmentFunding> pendingBudgetConstructionAppointmentFunding) {
        this.pendingBudgetConstructionAppointmentFunding = pendingBudgetConstructionAppointmentFunding;
    }

    /**
     * @see org.kuali.core.bo.PersistableBusinessObjectBase#buildListOfDeletionAwareLists()
     */
    @Override
    public List buildListOfDeletionAwareLists() {

        List managedLists = super.buildListOfDeletionAwareLists();
        managedLists.add(getPendingBudgetConstructionAppointmentFunding());
        return managedLists;
    }

    /**
     * Returns a map with the primitive field names as the key and the primitive values as the map value.
     * 
     * @return Map
     */
    public Map<String, Object> getValuesMap() {
        Map<String, Object> simpleValues = new HashMap<String, Object>();
        simpleValues.put(KFSPropertyConstants.EMPLID, this.getEmplid());

        return simpleValues;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    public LinkedHashMap toStringMapper() {
        LinkedHashMap<String, Object> mapper = new LinkedHashMap<String, Object>();
        mapper.put(KFSPropertyConstants.EMPLID, this.getEmplid());
        
        return mapper;
    }

    /**
     * Gets the appointmentRequestedCsfAmountTotal.
     * 
     * @return Returns the appointmentRequestedCsfAmountTotal.
     */
    public KualiInteger getAppointmentRequestedCsfAmountTotal() {
        return SalarySettingCalculator.getAppointmentRequestedCsfAmountTotal(this.getPendingBudgetConstructionAppointmentFunding());
    }

    /**
     * Gets the appointmentRequestedCsfTimePercentTotal.
     * 
     * @return Returns the appointmentRequestedCsfTimePercentTotal.
     */
    public BigDecimal getAppointmentRequestedCsfTimePercentTotal() {
        return SalarySettingCalculator.getAppointmentRequestedCsfTimePercentTotal(this.getPendingBudgetConstructionAppointmentFunding());
    }

    /**
     * Gets the appointmentRequestedCsfStandardHoursTotal.
     * 
     * @return Returns the appointmentRequestedCsfStandardHoursTotal.
     */
    public BigDecimal getAppointmentRequestedCsfStandardHoursTotal() {
        return SalarySettingCalculator.getAppointmentRequestedCsfStandardHoursTotal(this.getPendingBudgetConstructionAppointmentFunding());
    }

    /**
     * Gets the appointmentRequestedCsfFteQuantityTotal.
     * 
     * @return Returns the appointmentRequestedCsfFteQuantityTotal.
     */
    public BigDecimal getAppointmentRequestedCsfFteQuantityTotal() {
        return SalarySettingCalculator.getAppointmentRequestedCsfFteQuantityTotal(this.getPendingBudgetConstructionAppointmentFunding());
    }

    /**
     * Gets the appointmentRequestedAmountTotal.
     * 
     * @return Returns the appointmentRequestedAmountTotal.
     */
    public KualiInteger getAppointmentRequestedAmountTotal() {
        return SalarySettingCalculator.getAppointmentRequestedAmountTotal(this.getPendingBudgetConstructionAppointmentFunding());
    }

    /**
     * Gets the appointmentRequestedTimePercentTotal.
     * 
     * @return Returns the appointmentRequestedTimePercentTotal.
     */
    public BigDecimal getAppointmentRequestedTimePercentTotal() {
        return SalarySettingCalculator.getAppointmentRequestedTimePercentTotal(this.getPendingBudgetConstructionAppointmentFunding());
    }

    /**
     * Gets the appointmentRequestedStandardHoursTotal.
     * 
     * @return Returns the appointmentRequestedStandardHoursTotal.
     */
    public BigDecimal getAppointmentRequestedStandardHoursTotal() {
        return SalarySettingCalculator.getAppointmentRequestedStandardHoursTotal(this.getPendingBudgetConstructionAppointmentFunding());
    }

    /**
     * Gets the appointmentRequestedFteQuantityTotal.
     * 
     * @return Returns the appointmentRequestedFteQuantityTotal.
     */
    public BigDecimal getAppointmentRequestedFteQuantityTotal() {
        return SalarySettingCalculator.getAppointmentRequestedFteQuantityTotal(this.getPendingBudgetConstructionAppointmentFunding());
    }

    /**
     * Gets the csfAmountTotal.
     * 
     * @return Returns the csfAmountTotal.
     */
    public KualiInteger getCsfAmountTotal() {
        return SalarySettingCalculator.getCsfAmountTotal(this.getPendingBudgetConstructionAppointmentFunding());
    }

    /**
     * Gets the csfTimePercentTotal.
     * 
     * @return Returns the csfTimePercentTotal.
     */
    public BigDecimal getCsfTimePercentTotal() {
        return SalarySettingCalculator.getCsfTimePercentTotal(this.getPendingBudgetConstructionAppointmentFunding());
    }

    /**
     * Gets the csfStandardHoursTotal.
     * 
     * @return Returns the csfStandardHoursTotal.
     */
    public BigDecimal getCsfStandardHoursTotal() {
        return SalarySettingCalculator.getCsfStandardHoursTotal(this.getPendingBudgetConstructionAppointmentFunding());
    }

    /**
     * Gets the csfFullTimeEmploymentQuantityTotal.
     * 
     * @return Returns the csfFullTimeEmploymentQuantityTotal.
     */
    public BigDecimal getCsfFullTimeEmploymentQuantityTotal() {
        return SalarySettingCalculator.getCsfFullTimeEmploymentQuantityTotal(this.getPendingBudgetConstructionAppointmentFunding());
    }
}
