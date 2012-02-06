/*
 * Copyright 2006 The Kuali Foundation
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

package org.kuali.kfs.module.bc.businessobject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class BudgetConstructionIntendedIncumbent extends PersistableBusinessObjectBase implements PendingBudgetConstructionAppointmentFundingAware, Incumbent, MutableInactivatable {

    private String emplid;
    private String name;
    private String setidSalary;
    private String salaryAdministrationPlan;
    private String grade;
    private String iuClassificationLevel;
    private boolean active;

    private List<BudgetConstructionSalarySocialSecurityNumber> budgetConstructionSalarySocialSecurity;
    private List<PendingBudgetConstructionAppointmentFunding> pendingBudgetConstructionAppointmentFunding;

    /**
     * Default constructor.
     */
    public BudgetConstructionIntendedIncumbent() {
        super();

        budgetConstructionSalarySocialSecurity = new ArrayList<BudgetConstructionSalarySocialSecurityNumber>();
        pendingBudgetConstructionAppointmentFunding = new ArrayList<PendingBudgetConstructionAppointmentFunding>();
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
     * Gets the name attribute.
     * 
     * @return Returns the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name attribute.
     * 
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
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
     * Gets the active attribute.
     * 
     * @return Returns the active.
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute value.
     * 
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @see org.kuali.rice.krad.bo.PersistableBusinessObjectBase#buildListOfDeletionAwareLists()
     */
    
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
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    public LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String, Object> mapper = new LinkedHashMap<String, Object>();
        mapper.put(KFSPropertyConstants.EMPLID, this.getEmplid());

        return mapper;
    }
}

