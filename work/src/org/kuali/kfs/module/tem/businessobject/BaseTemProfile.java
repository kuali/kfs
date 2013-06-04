/*
 * Copyright 2010 The Kuali Foundation.
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

import java.sql.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomer;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.ModuleService;

public abstract class BaseTemProfile extends PersistableBusinessObjectBase {

    protected Integer id;
    protected String documentNumber;
    protected String principalId;
    protected String principalName;
    protected String firstName;
    protected String middleName;
    protected String lastName;
    protected String streetAddressLine1;
    protected String streetAddressLine2;
    protected String cityName;
    protected String stateCode;
    protected String zipCode;
    protected String countryCode;
    protected String citizenship;
    protected String emailAddress;
    protected Date dateOfBirth;
    protected String gender;
    protected String phoneNumber;
    protected String travelerTypeCode;
    protected TravelerType travelerType;
    protected String customerNumber;
    protected AccountsReceivableCustomer customer;
    protected boolean liabilityInsurance;

    protected String driversLicenseNumber;
    protected String driversLicenseState;
    protected Date driversLicenseExpDate;

    // Notification
    protected boolean notifyTAFinal = Boolean.FALSE;
    protected boolean notifyTAStatusChange = Boolean.FALSE;
    protected boolean notifyTERFinal = Boolean.FALSE;
    protected boolean notifyTERStatusChange = Boolean.FALSE;

    protected boolean active = Boolean.TRUE;
    protected boolean nonResidentAlien = Boolean.FALSE;
    protected boolean motorVehicleRecordCheck = Boolean.FALSE;

    /**
     * Default Constructor
     */
    public BaseTemProfile() {
    }

    /**
     * This method returns the document number this TravelerDetail object is associated with
     *
     * @return document number
     */
    @Column(name = "doc_nbr")
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * This method sets the document number this TravelerDetail object will be associated with
     *
     * @param documentNumber
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    @Id
    @GeneratedValue(generator = "TEM_TRAVELER_DTL_ID_SEQ")
    @SequenceGenerator(name = "TEM_TRAVELER_DTL_ID_SEQ", sequenceName = "TEM_TRAVELER_DTL_ID_SEQ", allocationSize = 5)
    @Column(name = "id", nullable = false)
    public Integer getId() {
        return id;
    }


    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "first_nm", length = 40, nullable = false)
    public String getFirstName() {
        return firstName;
    }


    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @Column(name = "last_nm", length = 40, nullable = false)
    public String getLastName() {
        return lastName;
    }


    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the middleName attribute.
     *
     * @return Returns the middleName.
     */
    @Column(length = 40, nullable = true)
    public String getMiddleName() {
        return middleName;
    }

    /**
     * Sets the middleName attribute value.
     *
     * @param middleName The middleName to set.
     */
    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getName() {
        String name = "";
        if(StringUtils.isNotBlank(getFirstName())) {
            name += getFirstName();
        }
        if(StringUtils.isNotBlank(getMiddleName())) {
            name += " " + getMiddleName();
        }
        if(StringUtils.isNotBlank(getLastName())) {
            name += " " + getLastName();
        }
        return name;
    }

    @Column(name = "addr_line_1", length = 50, nullable = false)
    public String getStreetAddressLine1() {
        return streetAddressLine1;
    }


    public void setStreetAddressLine1(String streetAddressLine1) {
        this.streetAddressLine1 = streetAddressLine1;
    }

    @Column(name = "addr_line_2", length = 50, nullable = true)
    public String getStreetAddressLine2() {
        return streetAddressLine2;
    }


    public void setStreetAddressLine2(String streetAddressLine2) {
        this.streetAddressLine2 = streetAddressLine2;
    }

    @Column(name = "city_nm", length = 50, nullable = true)
    public String getCityName() {
        return cityName;
    }


    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    @Column(name = "postal_state_cd", length = 50, nullable = false)
    public String getStateCode() {
        return stateCode;
    }


    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    @Column(name = "postal_cd", length = 50, nullable = false)
    public String getZipCode() {
        return zipCode;
    }


    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    @Column(name = "country_cd", length = 50, nullable = true)
    public String getCountryCode() {
        return countryCode;
    }


    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    @Column(name = "email_addr", length = 50, nullable = true)
    public String getEmailAddress() {
        return emailAddress;
    }


    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    @Column(name = "phone_nbr", length = 20, nullable = true)
    public String getPhoneNumber() {
        return phoneNumber;
    }


    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Column(name = "traveler_typ_cd", length = 3, nullable = false)
    public String getTravelerTypeCode() {
        return travelerTypeCode;
    }


    public void setTravelerTypeCode(String travelerTypeCode) {
        this.travelerTypeCode = travelerTypeCode;
    }

    @ManyToOne
    @JoinColumn(name = "traveler_typ_cd")
    public TravelerType getTravelerType() {
        return travelerType;
    }

    public void setTravelerType(TravelerType travelerType) {
        this.travelerType = travelerType;
    }

    /**
     * Gets the principalId attribute.
     *
     * @return Returns the principalId.
     */
    @Column(name = "EMP_PRINCIPAL_ID")
    public String getPrincipalId() {
        return principalId;
    }

    /**
     * Sets the principalId attribute value.
     *
     * @param principalId The principalId to set.
     */
    public void setPrincipalId(String principalId) {
        this.principalId = principalId;
    }

    /**
     * Gets the principalName attribute.
     *
     * @return Returns the principalName.
     */
    @Column(name = "EMP_PRINCIPAL_ID")
    public String getPrincipalName() {
        return principalName;
    }

    /**
     * Sets the principalName attribute value.
     *
     * @param principalName The principalName to set.
     */
    public void setPrincipalName(String principalName) {
        this.principalName = principalName;
    }

    /**
     * Gets the customerNumber attribute.
     *
     * @return Returns the customerNumber.
     */
    @Column(name = "customer_num", length = 40, nullable = true)
    public String getCustomerNumber() {

        return customerNumber;
    }

    /**
     * Sets the customerNumber attribute value.
     *
     * @param customerNumber The customerNumber to set.
     */
    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public void setCustomer(final AccountsReceivableCustomer customer) {
        this.customer = customer;
    }

    public AccountsReceivableCustomer getCustomer() {
        ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(AccountsReceivableCustomer.class);
        if ( moduleService != null ) {
            Map<String,Object> keys = new HashMap<String, Object>(1);
            keys.put(KFSPropertyConstants.CUSTOMER_NUMBER, customerNumber);
            customer = moduleService.getExternalizableBusinessObject(AccountsReceivableCustomer.class, keys);
        } else {
            throw new RuntimeException( "CONFIGURATION ERROR: No responsible module found for EBO class.  Unable to proceed." );
        }
        return this.customer;
    }

    @SuppressWarnings("rawtypes")
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap map = new LinkedHashMap();
        map.put("id", id);
        map.put("firstName", firstName);
        map.put("lastName", lastName);
        map.put("streetAddressLine1", streetAddressLine1);
        map.put("cityName", cityName);
        return map;
    }

    public boolean isLiabilityInsurance() {
        return liabilityInsurance;
    }

    public void setLiabilityInsurance(boolean liabilityInsurance) {
        this.liabilityInsurance = liabilityInsurance;
    }

    /**
     * Gets the driversLicenseNumber attribute.
     *
     * @return Returns the driversLicenseNumber.
     */
    @Column(name = "drive_lic_num", length = 20, nullable = true)
    public String getDriversLicenseNumber() {
        return driversLicenseNumber;
    }

    /**
     * Sets the driversLicenseNumber attribute value.
     *
     * @param driversLicenseNumber The driversLicenseNumber to set.
     */
    public void setDriversLicenseNumber(String driversLicenseNumber) {
        this.driversLicenseNumber = driversLicenseNumber;
    }

    /**
     * Gets the driversLicenseState attribute.
     *
     * @return Returns the driversLicenseState.
     */
    public String getDriversLicenseState() {
        return driversLicenseState;
    }

    /**
     * Sets the driversLicenseState attribute value.
     *
     * @param driversLicenseState The driversLicenseState to set.
     */
    public void setDriversLicenseState(String driversLicenseState) {
        this.driversLicenseState = driversLicenseState;
    }

    /**
     * Gets the driversLicenseExpDate attribute.
     *
     * @return Returns the driversLicenseExpDate.
     */
    @Column(name = "drive_lic_exp_dt", length = 10)
    public Date getDriversLicenseExpDate() {
        return driversLicenseExpDate;
    }


    /**
     * Sets the driversLicenseExpDate attribute value.
     *
     * @param driversLicenseExpDate The driversLicenseExpDate to set.
     */
    public void setDriversLicenseExpDate(Date driversLicenseExpDate) {
        this.driversLicenseExpDate = driversLicenseExpDate;
    }

    public boolean getNotifyTAFinal() {
        return notifyTAFinal;
    }

    public boolean getNotifyTAStatusChange() {
        return notifyTAStatusChange;
    }

    public boolean getNotifyTERFinal() {
        return notifyTERFinal;
    }

    public boolean getNotifyTERStatusChange() {
        return notifyTERStatusChange;
    }

    /**
     * Gets the citizenship attribute.
     *
     * @return Returns the citizenship.
     */
    @Column(name = "citizenship", length = 40, nullable = true)
    public String getCitizenship() {
        return citizenship;
    }

    /**
     * Sets the citizenship attribute value.
     *
     * @param citizenship The citizenship to set.
     */
    public void setCitizenship(String citizenship) {
        this.citizenship = citizenship;
    }

    /**
     * Gets the active attribute.
     *
     * @return Returns the active.
     */
    @Column(name = "ACTV_IND", nullable = false, length = 1)
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
     * Gets the notifyTAFinal attribute.
     *
     * @return Returns the notifyTAFinal.
     */
    public boolean isNotifyTAFinal() {
        return notifyTAFinal;
    }

    /**
     * Sets the notifyTAFinal attribute value.
     *
     * @param notifyTAFinal The notifyTAFinal to set.
     */
    public void setNotifyTAFinal(boolean notifyTAFinal) {
        this.notifyTAFinal = notifyTAFinal;
    }

    /**
     * Gets the notifyTAStatusChange attribute.
     *
     * @return Returns the notifyTAStatusChange.
     */
    public boolean isNotifyTAStatusChange() {
        return notifyTAStatusChange;
    }

    /**
     * Sets the notifyTAStatusChange attribute value.
     *
     * @param notifyTAStatusChange The notifyTAStatusChange to set.
     */
    public void setNotifyTAStatusChange(boolean notifyTAStatusChange) {
        this.notifyTAStatusChange = notifyTAStatusChange;
    }

    /**
     * Gets the notifyTERFinal attribute.
     *
     * @return Returns the notifyTERFinal.
     */
    public boolean isNotifyTERFinal() {
        return notifyTERFinal;
    }

    /**
     * Sets the notifyTERFinal attribute value.
     *
     * @param notifyTERFinal The notifyTERFinal to set.
     */
    public void setNotifyTERFinal(boolean notifyTERFinal) {
        this.notifyTERFinal = notifyTERFinal;
    }

    /**
     * Gets the notifyTERStatusChange attribute.
     *
     * @return Returns the notifyTERStatusChange.
     */
    public boolean isNotifyTERStatusChange() {
        return notifyTERStatusChange;
    }

    /**
     * Sets the notifyTERStatusChange attribute value.
     *
     * @param notifyTERStatusChange The notifyTERStatusChange to set.
     */
    public void setNotifyTERStatusChange(boolean notifyTERStatusChange) {
        this.notifyTERStatusChange = notifyTERStatusChange;
    }

    public boolean isMotorVehicleRecordCheck() {
        return motorVehicleRecordCheck;
    }

    public void setMotorVehicleRecordCheck(boolean motorVehicleRecordCheck) {
        this.motorVehicleRecordCheck = motorVehicleRecordCheck;
    }

    /**
     * Gets the nonResIdentAlien attribute.
     *
     * @return Returns the nonResIdentAlien.
     */
    @Column(name = "non_res_alien", length = 1, nullable = true)
    public boolean isNonResidentAlien() {
        return nonResidentAlien;
    }

    /**
     * Sets the nonResIdentAlien attribute value.
     *
     * @param nonResIdentAlien The nonResIdentAlien to set.
     */
    public void setNonResidentAlien(boolean nonResidentAlien) {
        this.nonResidentAlien = nonResidentAlien;
    }

    /**
     * Gets the dateOfBirth attribute.
     *
     * @return Returns the dateOfBirth.
     */
    @Column(name = "date_of_birth", length = 10, nullable = false)
    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    /**
     * Sets the dateOfBirth attribute value.
     *
     * @param dateOfBirth The dateOfBirth to set.
     */
    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    /**
     * Gets the gender attribute.
     *
     * @return Returns the gender.
     */
    @Column(name = "gender", length = 1, nullable = false)
    public String getGender() {
        return gender;
    }

    /**
     * Sets the gender attribute value.
     *
     * @param gender The gender to set.
     */
    public void setGender(String gender) {
        this.gender = gender;
    }
}
