/*
 * Copyright 2005-2006 The Kuali Foundation.
 * 
 * $Source: /opt/cvs/kfs/work/src/org/kuali/kfs/module/bc/businessobject/BudgetConstructionIntendedIncumbent.java,v $
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

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.bo.BusinessObjectBase;

/**
 * 
 */
public class BudgetConstructionIntendedIncumbent extends BusinessObjectBase {

	private String emplid;
	private String personName;
	private String setidSalary;
	private String salaryAdministrationPlan;
	private String grade;
	private String iuClassificationLevel;

    private List budgetConstructionSalarySocialSecurity;
    private List pendingBudgetConstructionAppointmentFunding;
    
	/**
	 * Default constructor.
	 */
	public BudgetConstructionIntendedIncumbent() {
        budgetConstructionSalarySocialSecurity = new ArrayList();
        pendingBudgetConstructionAppointmentFunding = new ArrayList();
        
	}

	/**
	 * Gets the emplid attribute.
	 * 
	 * @return Returns the emplid
	 * 
	 */
	public String getEmplid() { 
		return emplid;
	}

	/**
	 * Sets the emplid attribute.
	 * 
	 * @param emplid The emplid to set.
	 * 
	 */
	public void setEmplid(String emplid) {
		this.emplid = emplid;
	}


	/**
	 * Gets the personName attribute.
	 * 
	 * @return Returns the personName
	 * 
	 */
	public String getPersonName() { 
		return personName;
	}

	/**
	 * Sets the personName attribute.
	 * 
	 * @param personName The personName to set.
	 * 
	 */
	public void setPersonName(String personName) {
		this.personName = personName;
	}


	/**
	 * Gets the setidSalary attribute.
	 * 
	 * @return Returns the setidSalary
	 * 
	 */
	public String getSetidSalary() { 
		return setidSalary;
	}

	/**
	 * Sets the setidSalary attribute.
	 * 
	 * @param setidSalary The setidSalary to set.
	 * 
	 */
	public void setSetidSalary(String setidSalary) {
		this.setidSalary = setidSalary;
	}


	/**
	 * Gets the salaryAdministrationPlan attribute.
	 * 
	 * @return Returns the salaryAdministrationPlan
	 * 
	 */
	public String getSalaryAdministrationPlan() { 
		return salaryAdministrationPlan;
	}

	/**
	 * Sets the salaryAdministrationPlan attribute.
	 * 
	 * @param salaryAdministrationPlan The salaryAdministrationPlan to set.
	 * 
	 */
	public void setSalaryAdministrationPlan(String salaryAdministrationPlan) {
		this.salaryAdministrationPlan = salaryAdministrationPlan;
	}


	/**
	 * Gets the grade attribute.
	 * 
	 * @return Returns the grade
	 * 
	 */
	public String getGrade() { 
		return grade;
	}

	/**
	 * Sets the grade attribute.
	 * 
	 * @param grade The grade to set.
	 * 
	 */
	public void setGrade(String grade) {
		this.grade = grade;
	}


	/**
	 * Gets the iuClassificationLevel attribute.
	 * 
	 * @return Returns the iuClassificationLevel
	 * 
	 */
	public String getIuClassificationLevel() { 
		return iuClassificationLevel;
	}

	/**
	 * Sets the iuClassificationLevel attribute.
	 * 
	 * @param iuClassificationLevel The iuClassificationLevel to set.
	 * 
	 */
	public void setIuClassificationLevel(String iuClassificationLevel) {
		this.iuClassificationLevel = iuClassificationLevel;
	}

    /**
     * Gets the budgetConstructionSalarySocialSecurity attribute. 
     * @return Returns the budgetConstructionSalarySocialSecurity.
     */
    public List getBudgetConstructionSalarySocialSecurity() {
        return budgetConstructionSalarySocialSecurity;
    }

    /**
     * Sets the budgetConstructionSalarySocialSecurity attribute value.
     * @param budgetConstructionSalarySocialSecurity The budgetConstructionSalarySocialSecurity to set.
     */
    public void setBudgetConstructionSalarySocialSecurity(List budgetConstructionSalarySocialSecurity) {
        this.budgetConstructionSalarySocialSecurity = budgetConstructionSalarySocialSecurity;
    }

    /**
     * Gets the pendingBudgetConstructionAppointmentFunding attribute. 
     * @return Returns the pendingBudgetConstructionAppointmentFunding.
     */
    public List getPendingBudgetConstructionAppointmentFunding() {
        return pendingBudgetConstructionAppointmentFunding;
    }

    /**
     * Sets the pendingBudgetConstructionAppointmentFunding attribute value.
     * @param pendingBudgetConstructionAppointmentFunding The pendingBudgetConstructionAppointmentFunding to set.
     */
    public void setPendingBudgetConstructionAppointmentFunding(List pendingBudgetConstructionAppointmentFunding) {
        this.pendingBudgetConstructionAppointmentFunding = pendingBudgetConstructionAppointmentFunding;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();      
        m.put("emplid", this.emplid);
        return m;
    }
    
    
}
