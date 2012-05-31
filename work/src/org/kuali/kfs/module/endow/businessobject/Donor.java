/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.businessobject;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.kfs.module.endow.EndowPropertyConstants;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

public class Donor extends PersistableBusinessObjectBase implements MutableInactivatable {

    private String donorID;

    // name
    private String firstName;
    private String middleName;
    private String lastName;

    // address
    private String address1;
    private String address2;
    private String address3;
    private String city;
    private String state;
    private String postalCode;
    private String country; // should reference org.kuali.rice.location.framework.country.CountryEbo later

    private String phoneNumber;

    private String individualSalutation;
    private String individualMailLabel;
    private String jointSalutation;
    private String jointMailLabel;

    private String developmentOfficer;

    private Date deceasedDate;

    private String firstCorporateContact;
    private String firstContactTitle;
    private String secondCorporateContact;
    private String secondContactTitle;

    private String comments;

    private boolean active;

    /**
     * Gets the donorID attribute.
     * 
     * @return Returns the donorID.
     */
    public String getDonorID() {
        return donorID;
    }

    /**
     * Sets the donorID attribute value.
     * 
     * @param donorID The donorID to set.
     */
    public void setDonorID(String donorID) {
        this.donorID = donorID;
    }

    /**
     * Gets the firstName attribute.
     * 
     * @return Returns the firstName.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets the firstName attribute value.
     * 
     * @param firstName The firstName to set.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets the middleName attribute.
     * 
     * @return Returns the middleName.
     */
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

    /**
     * Gets the lastName attribute.
     * 
     * @return Returns the lastName.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets the lastName attribute value.
     * 
     * @param lastName The lastName to set.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets the address1 attribute.
     * 
     * @return Returns the address1.
     */
    public String getAddress1() {
        return address1;
    }

    /**
     * Sets the address1 attribute value.
     * 
     * @param address1 The address1 to set.
     */
    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    /**
     * Gets the address2 attribute.
     * 
     * @return Returns the address2.
     */
    public String getAddress2() {
        return address2;
    }

    /**
     * Sets the address2 attribute value.
     * 
     * @param address2 The address2 to set.
     */
    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    /**
     * Gets the address3 attribute.
     * 
     * @return Returns the address3.
     */
    public String getAddress3() {
        return address3;
    }

    /**
     * Sets the address3 attribute value.
     * 
     * @param address3 The address3 to set.
     */
    public void setAddress3(String address3) {
        this.address3 = address3;
    }

    /**
     * Gets the city attribute.
     * 
     * @return Returns the city.
     */
    public String getCity() {
        return city;
    }

    /**
     * Sets the city attribute value.
     * 
     * @param city The city to set.
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Gets the state attribute.
     * 
     * @return Returns the state.
     */
    public String getState() {
        return state;
    }

    /**
     * Sets the state attribute value.
     * 
     * @param state The state to set.
     */
    public void setState(String state) {
        this.state = state;
    }

    /**
     * Gets the postalCode attribute.
     * 
     * @return Returns the postalCode.
     */
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * Sets the postalCode attribute value.
     * 
     * @param postalCode The postalCode to set.
     */
    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * Gets the country attribute.
     * 
     * @return Returns the country.
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets the country attribute value.
     * 
     * @param country The country to set.
     */
    public void setCountry(String country) {
        this.country = country;
    }

    /**
     * Gets the phoneNumber attribute.
     * 
     * @return Returns the phoneNumber.
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the phoneNumber attribute value.
     * 
     * @param phoneNumber The phoneNumber to set.
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Gets the individualSalutation attribute.
     * 
     * @return Returns the individualSalutation.
     */
    public String getIndividualSalutation() {
        return individualSalutation;
    }

    /**
     * Sets the individualSalutation attribute value.
     * 
     * @param individualSalutation The individualSalutation to set.
     */
    public void setIndividualSalutation(String individualSalutation) {
        this.individualSalutation = individualSalutation;
    }

    /**
     * Gets the individualMailLabel attribute.
     * 
     * @return Returns the individualMailLabel.
     */
    public String getIndividualMailLabel() {
        return individualMailLabel;
    }

    /**
     * Sets the individualMailLabel attribute value.
     * 
     * @param individualMailLabel The individualMailLabel to set.
     */
    public void setIndividualMailLabel(String individualMailLabel) {
        this.individualMailLabel = individualMailLabel;
    }

    /**
     * Gets the jointSalutation attribute.
     * 
     * @return Returns the jointSalutation.
     */
    public String getJointSalutation() {
        return jointSalutation;
    }

    /**
     * Sets the jointSalutation attribute value.
     * 
     * @param jointSalutation The jointSalutation to set.
     */
    public void setJointSalutation(String jointSalutation) {
        this.jointSalutation = jointSalutation;
    }

    /**
     * Gets the jointMailLabel attribute.
     * 
     * @return Returns the jointMailLabel.
     */
    public String getJointMailLabel() {
        return jointMailLabel;
    }

    /**
     * Sets the jointMailLabel attribute value.
     * 
     * @param jointMailLabel The jointMailLabel to set.
     */
    public void setJointMailLabel(String jointMailLabel) {
        this.jointMailLabel = jointMailLabel;
    }

    /**
     * Gets the developmentOfficer attribute.
     * 
     * @return Returns the developmentOfficer.
     */
    public String getDevelopmentOfficer() {
        return developmentOfficer;
    }

    /**
     * Sets the developmentOfficer attribute value.
     * 
     * @param developmentOfficer The developmentOfficer to set.
     */
    public void setDevelopmentOfficer(String developmentOfficer) {
        this.developmentOfficer = developmentOfficer;
    }

    /**
     * Gets the deceasedDate attribute.
     * 
     * @return Returns the deceasedDate.
     */
    public Date getDeceasedDate() {
        return deceasedDate;
    }

    /**
     * Sets the deceasedDate attribute value.
     * 
     * @param deceasedDate The deceasedDate to set.
     */
    public void setDeceasedDate(Date deceasedDate) {
        this.deceasedDate = deceasedDate;
    }

    /**
     * Gets the firstCorporateContact attribute.
     * 
     * @return Returns the firstCorporateContact.
     */
    public String getFirstCorporateContact() {
        return firstCorporateContact;
    }

    /**
     * Sets the firstCorporateContact attribute value.
     * 
     * @param firstCorporateContact The firstCorporateContact to set.
     */
    public void setFirstCorporateContact(String firstCorporateContact) {
        this.firstCorporateContact = firstCorporateContact;
    }

    /**
     * Gets the firstContactTitle attribute.
     * 
     * @return Returns the firstContactTitle.
     */
    public String getFirstContactTitle() {
        return firstContactTitle;
    }

    /**
     * Sets the firstContactTitle attribute value.
     * 
     * @param firstContactTitle The firstContactTitle to set.
     */
    public void setFirstContactTitle(String firstContactTitle) {
        this.firstContactTitle = firstContactTitle;
    }

    /**
     * Gets the secondCorporateContact attribute.
     * 
     * @return Returns the secondCorporateContact.
     */
    public String getSecondCorporateContact() {
        return secondCorporateContact;
    }

    /**
     * Sets the secondCorporateContact attribute value.
     * 
     * @param secondCorporateContact The secondCorporateContact to set.
     */
    public void setSecondCorporateContact(String secondCorporateContact) {
        this.secondCorporateContact = secondCorporateContact;
    }

    /**
     * Gets the secondContactTitle attribute.
     * 
     * @return Returns the secondContactTitle.
     */
    public String getSecondContactTitle() {
        return secondContactTitle;
    }

    /**
     * Sets the secondContactTitle attribute value.
     * 
     * @param secondContactTitle The secondContactTitle to set.
     */
    public void setSecondContactTitle(String secondContactTitle) {
        this.secondContactTitle = secondContactTitle;
    }

    /**
     * Gets the comments attribute.
     * 
     * @return Returns the comments.
     */
    public String getComments() {
        return comments;
    }

    /**
     * Sets the comments attribute value.
     * 
     * @param comments The comments to set.
     */
    public void setComments(String comments) {
        this.comments = comments;
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
     * Gets the donor's full name: First name + middle name + last name
     * 
     * @return donor's full name
     */
    public String getFullName() {
        String firstName = getFirstName() == null ? KFSConstants.EMPTY_STRING : getFirstName() + " ";
        String middleName = getMiddleName() == null ? KFSConstants.EMPTY_STRING : getMiddleName();
        String lastName = getLastName() == null ? KFSConstants.EMPTY_STRING : " " + getLastName();

        String fullName = firstName + middleName + lastName;

        return fullName;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        m.put(EndowPropertyConstants.DONR_ID, this.donorID);
        return m;
    }

}
