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
