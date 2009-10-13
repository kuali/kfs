/*
 * Copyright 2008 The Kuali Foundation
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

/**
 * Provides attributes that describe an employee.
 */
public interface Incumbent {

    /**
     * Gets the emplid attribute.
     * 
     * @return Returns the emplid
     */
    public abstract String getEmplid();

    /**
     * Sets the emplid attribute.
     * 
     * @param emplid The emplid to set.
     */
    public abstract void setEmplid(String emplid);

    /**
     * Gets the name attribute.
     * 
     * @return Returns the name
     */
    public abstract String getName();

    /**
     * Sets the name attribute.
     * 
     * @param name The name to set.
     */
    public abstract void setName(String name);

    /**
     * Gets the setidSalary attribute.
     * 
     * @return Returns the setidSalary
     */
    public abstract String getSetidSalary();

    /**
     * Sets the setidSalary attribute.
     * 
     * @param setidSalary The setidSalary to set.
     */
    public abstract void setSetidSalary(String setidSalary);

    /**
     * Gets the salaryAdministrationPlan attribute.
     * 
     * @return Returns the salaryAdministrationPlan
     */
    public abstract String getSalaryAdministrationPlan();

    /**
     * Sets the salaryAdministrationPlan attribute.
     * 
     * @param salaryAdministrationPlan The salaryAdministrationPlan to set.
     */
    public abstract void setSalaryAdministrationPlan(String salaryAdministrationPlan);

    /**
     * Gets the grade attribute.
     * 
     * @return Returns the grade
     */
    public abstract String getGrade();

    /**
     * Sets the grade attribute.
     * 
     * @param grade The grade to set.
     */
    public abstract void setGrade(String grade);

    /**
     * Gets the iuClassificationLevel attribute.
     * 
     * @return Returns the iuClassificationLevel
     */
    public abstract String getIuClassificationLevel();

    /**
     * Sets the iuClassificationLevel attribute.
     * 
     * @param iuClassificationLevel The iuClassificationLevel to set.
     */
    public abstract void setIuClassificationLevel(String iuClassificationLevel);

}
