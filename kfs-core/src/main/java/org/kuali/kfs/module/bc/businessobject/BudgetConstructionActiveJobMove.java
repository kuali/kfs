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

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * 
 */
public class BudgetConstructionActiveJobMove extends PersistableBusinessObjectBase {

    private String principalId;
    private String emplid;
    private Integer employeeRecord;
    private Date effectiveDate;
    private Integer effectiveSequence;
    private String positionNumber;
    private String employeeStatus;
    private String departmentIdentifier;

    /**
     * Default constructor.
     */
    public BudgetConstructionActiveJobMove() {

    }

    /**
     * Gets the principalId attribute.
     * 
     * @return Returns the principalId
     */
    public String getPrincipalId() {
        return principalId;
    }

    /**
     * Sets the principalId attribute.
     * 
     * @param principalId The principalId to set.
     */
    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
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
     * Gets the employeeRecord attribute.
     * 
     * @return Returns the employeeRecord
     */
    public Integer getEmployeeRecord() {
        return employeeRecord;
    }

    /**
     * Sets the employeeRecord attribute.
     * 
     * @param employeeRecord The employeeRecord to set.
     */
    public void setEmployeeRecord(Integer employeeRecord) {
        this.employeeRecord = employeeRecord;
    }


    /**
     * Gets the effectiveDate attribute.
     * 
     * @return Returns the effectiveDate
     */
    public Date getEffectiveDate() {
        return effectiveDate;
    }

    /**
     * Sets the effectiveDate attribute.
     * 
     * @param effectiveDate The effectiveDate to set.
     */
    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }


    /**
     * Gets the effectiveSequence attribute.
     * 
     * @return Returns the effectiveSequence
     */
    public Integer getEffectiveSequence() {
        return effectiveSequence;
    }

    /**
     * Sets the effectiveSequence attribute.
     * 
     * @param effectiveSequence The effectiveSequence to set.
     */
    public void setEffectiveSequence(Integer effectiveSequence) {
        this.effectiveSequence = effectiveSequence;
    }


    /**
     * Gets the positionNumber attribute.
     * 
     * @return Returns the positionNumber
     */
    public String getPositionNumber() {
        return positionNumber;
    }

    /**
     * Sets the positionNumber attribute.
     * 
     * @param positionNumber The positionNumber to set.
     */
    public void setPositionNumber(String positionNumber) {
        this.positionNumber = positionNumber;
    }


    /**
     * Gets the employeeStatus attribute.
     * 
     * @return Returns the employeeStatus
     */
    public String getEmployeeStatus() {
        return employeeStatus;
    }

    /**
     * Sets the employeeStatus attribute.
     * 
     * @param employeeStatus The employeeStatus to set.
     */
    public void setEmployeeStatus(String employeeStatus) {
        this.employeeStatus = employeeStatus;
    }


    /**
     * Gets the departmentIdentifier attribute.
     * 
     * @return Returns the departmentIdentifier
     */
    public String getDepartmentIdentifier() {
        return departmentIdentifier;
    }

    /**
     * Sets the departmentIdentifier attribute.
     * 
     * @param departmentIdentifier The departmentIdentifier to set.
     */
    public void setDepartmentIdentifier(String departmentIdentifier) {
        this.departmentIdentifier = departmentIdentifier;
    }


    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("principalId", this.principalId);
        m.put("emplid", this.emplid);
        if (this.employeeRecord != null) {
            m.put("employeeRecord", this.employeeRecord.toString());
        }
        if (this.effectiveDate != null) {
            m.put("effectiveDate", this.effectiveDate.toString());
        }
        if (this.effectiveSequence != null) {
            m.put("effectiveSequence", this.effectiveSequence.toString());
        }
        return m;
    }
}

