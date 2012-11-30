/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.businessobject;

import java.util.LinkedHashMap;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import org.kuali.rice.kim.framework.identity.employment.EntityEmploymentStatusEbo;
import org.kuali.rice.kim.framework.identity.employment.EntityEmploymentTypeEbo;
import org.kuali.rice.kim.impl.identity.employment.EntityEmploymentStatusBo;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.location.framework.campus.CampusEbo;

@SuppressWarnings("restriction")
public class TemProfileFromKimPerson extends PersistableBusinessObjectBase {

    private String principalId;
    private String employeeId;
    private String principalName;
    protected String entityId;
    private String firstName;
    private String middleName;
    private String lastName;
    private String emailAddress;
    private String phoneNumber;
    private String employeeStatusCode;
    protected EntityEmploymentStatusEbo employeeStatus;
    protected String employeeTypeCode;
    protected EntityEmploymentTypeEbo employeeType;
    protected String primaryDepartmentCode;
    protected String campusCode;
    protected CampusEbo campus;
    protected boolean active;


    /**
     * Gets the principalId attribute.
     * @return Returns the principalId.
     */
    @Column(name = "prncpl_id", length = 40, nullable = true)
    public String getPrincipalId() {
        return principalId;
    }


    /**
     * Sets the principalId attribute value.
     * @param principalId The principalId to set.
     */
    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }


    /**
     * Gets the employeeId attribute.
     * @return Returns the employeeId.
     */
    @Column(name = "empl_id", length = 40, nullable = true)
    public String getEmployeeId() {
        return employeeId;
    }


    /**
     * Sets the employeeId attribute value.
     * @param employeeId The employeeId to set.
     */
    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }


    /**
     * Gets the principalName attribute.
     * @return Returns the principalName.
     */
    @Column(name = "prncpl_nm", length = 40, nullable = true)
    public String getPrincipalName() {
        return principalName;
    }


    /**
     * Sets the principalName attribute value.
     * @param principalName The principalName to set.
     */
    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }


    /**
     * Gets the entityId attribute.
     * @return Returns the entityId.
     */
    @Column(name = "entity_id", length = 40, nullable = true)
    public String getEntityId() {
        return entityId;
    }


    /**
     * Sets the entityId attribute value.
     * @param entityId The entityId to set.
     */
    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }


    /**
     * Gets the firstName attribute.
     * @return Returns the firstName.
     */
    @Column(name = "first_nm", length = 40, nullable = true)
    public String getFirstName() {
        return firstName;
    }


    /**
     * Sets the firstName attribute value.
     * @param firstName The firstName to set.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }


    /**
     * Gets the middleName attribute.
     * @return Returns the middleName.
     */
    @Column(name = "middle_nm", length = 40, nullable = true)
    public String getMiddleName() {
        return middleName;
    }


    /**
     * Sets the middleName attribute value.
     * @param middleName The middleName to set.
     */
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }


    /**
     * Gets the lastName attribute.
     * @return Returns the lastName.
     */
    @Column(name = "last_nm", length = 40, nullable = true)
    public String getLastName() {
        return lastName;
    }


    /**
     * Sets the lastName attribute value.
     * @param lastName The lastName to set.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }


    /**
     * Gets the emailAddress attribute.
     * @return Returns the emailAddress.
     */
    @Column(name = "email_addr", length = 40, nullable = true)
    public String getEmailAddress() {
        return emailAddress;
    }


    /**
     * Sets the emailAddress attribute value.
     * @param emailAddress The emailAddress to set.
     */
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }


    /**
     * Gets the phoneNumber attribute.
     * @return Returns the phoneNumber.
     */
    @Column(name = "phone_num", length = 40, nullable = true)
    public String getPhoneNumber() {
        return phoneNumber;
    }


    /**
     * Sets the phoneNumber attribute value.
     * @param phoneNumber The phoneNumber to set.
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }


    /**
     * Gets the employeeStatusCode attribute.
     * @return Returns the employeeStatusCode.
     */
    @Column(name = "emp_status_cd", length = 40, nullable = true)
    public String getEmployeeStatusCode() {
        return employeeStatusCode;
    }


    /**
     * Sets the employeeStatusCode attribute value.
     * @param employeeStatusCode The employeeStatusCode to set.
     */
    public void setEmployeeStatusCode(String employeeStatusCode) {
        this.employeeStatusCode = employeeStatusCode;
    }


    /**
     * Gets the employeeStatus attribute.
     * @return Returns the employeeStatus.
     */
    @ManyToOne
    @JoinColumn(name = "emp_status_cd")
    public EntityEmploymentStatusEbo getEmployeeStatus() {
        return employeeStatus;
    }


    /**
     * Sets the employeeStatus attribute value.
     * @param employeeStatus The employeeStatus to set.
     */
    public void setEmployeeStatus(EntityEmploymentStatusBo employeeStatus) {
        this.employeeStatus = employeeStatus;
    }


    /**
     * Gets the employeeTypeCode attribute.
     * @return Returns the employeeTypeCode.
     */
    @Column(name = "emp_typ_cd", length = 40, nullable = true)
    public String getEmployeeTypeCode() {
        return employeeTypeCode;
    }


    /**
     * Sets the employeeTypeCode attribute value.
     * @param employeeTypeCode The employeeTypeCode to set.
     */
    public void setEmployeeTypeCode(String employeeTypeCode) {
        this.employeeTypeCode = employeeTypeCode;
    }


    /**
     * Gets the employeeType attribute.
     * @return Returns the employeeType.
     */
    @ManyToOne
    @JoinColumn(name = "emp_typ_cd")
    public EntityEmploymentTypeEbo getEmployeeType() {
        return employeeType;
    }


    /**
     * Sets the employeeType attribute value.
     * @param employeeType The employeeType to set.
     */
    public void setEmployeeType(EntityEmploymentTypeEbo employeeType) {
        this.employeeType = employeeType;
    }


    /**
     * Gets the primaryDepartmentCode attribute.
     * @return Returns the primaryDepartmentCode.
     */
    @Column(name = "primary_dept_cd", length = 40, nullable = true)
    public String getPrimaryDepartmentCode() {
        return primaryDepartmentCode;
    }


    /**
     * Sets the primaryDepartmentCode attribute value.
     * @param primaryDepartmentCode The primaryDepartmentCode to set.
     */
    public void setPrimaryDepartmentCode(String primaryDepartmentCode) {
        this.primaryDepartmentCode = primaryDepartmentCode;
    }


    /**
     * Gets the campusCode attribute.
     * @return Returns the campusCode.
     */
    @Column(name = "campus_cd", length = 40, nullable = true)
    public String getCampusCode() {
        return campusCode;
    }


    /**
     * Sets the campusCode attribute value.
     * @param campusCode The campusCode to set.
     */
    public void setCampusCode(String campusCode) {
        this.campusCode = campusCode;
    }


    /**
     * Gets the campus attribute.
     * @return Returns the campus.
     */
    @ManyToOne
    @JoinColumn(name="campus_cd")
    public CampusEbo getCampus() {
        return campus;
    }


    /**
     * Sets the campus attribute value.
     * @param campus The campus to set.
     */
    public void setCampus(CampusEbo campus) {
        this.campus = campus;
    }


    /**
     * Gets the active attribute.
     * @return Returns the active.
     */
    @Column(length = 1, nullable = true)
    public boolean isActive() {
        return active;
    }


    /**
     * Sets the active attribute value.
     * @param active The active to set.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    @SuppressWarnings("rawtypes")
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        return null;
    }

}
