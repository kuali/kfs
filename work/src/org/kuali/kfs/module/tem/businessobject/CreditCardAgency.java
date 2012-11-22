/*
 * Copyright 2012 The Kuali Foundation.
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
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

@Entity
@Table(name = "TEM_CREDIT_CARD_AGENCY_T")
public class CreditCardAgency extends PersistableBusinessObjectBase implements MutableInactivatable {

    private Integer id;
    private String creditCardOrAgencyCode;
    private TravelCardType travelCardType;
    private String travelCardTypeCode;
    private Boolean paymentIndicator;
    private String creditCardOrAgencyName;
    private String address1;
    private String address2;
    private String city;
    private String state;
    private String zipCode;
    private String email;
    private String phone;
    private String contactName;
    private String typeCode;
    private Boolean preReconciled;
    private Boolean enableNonReimbursable;
    private String vendorNumber;
    private Boolean foreignCompany = Boolean.TRUE;
    private Boolean active = Boolean.TRUE;


    /**
     * Gets the id attribute.
     * @return Returns the id.
     */
    @Id
    @GeneratedValue(generator = "TEM_CREDIT_CARD_AGENCY_ID_SEQ")
    @SequenceGenerator(name = "TEM_CREDIT_CARD_AGENCY_ID_SEQ", sequenceName = "TEM_CREDIT_CARD_AGENCY_ID_SEQ", allocationSize = 5)
    public Integer getId() {
        return id;
    }


    /**
     * Sets the id attribute value.
     * @param id The id to set.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    @Column(name = "CREDIT_CARD_AGENCY_CODE", nullable = false)
    public String getCreditCardOrAgencyCode() {
        return creditCardOrAgencyCode;
    }


    public void setCreditCardOrAgencyCode(String creditCardOrAgencyCode) {
        this.creditCardOrAgencyCode = creditCardOrAgencyCode;
    }


    @ManyToOne
    @JoinColumn(name = "TRAVEL_CARD_TYPE_CD")
    public TravelCardType getTravelCardType() {
        return travelCardType;
    }


    public void setTravelCardType(TravelCardType travelCardType) {
        this.travelCardType = travelCardType;
    }

    @Column(name = "TRAVEL_CARD_TYPE_CD", nullable = false, length = 2)
    public String getTravelCardTypeCode() {
        return travelCardTypeCode;
    }


    public void setTravelCardTypeCode(String travelCardTypeCode) {
        this.travelCardTypeCode = travelCardTypeCode;
    }

    @Column(name = "PAYMENT_IND", nullable = false, length = 1)
    public Boolean getPaymentIndicator() {
        return paymentIndicator;
    }
    @Column(name = "PAYMENT_IND", nullable = false, length = 1)
    public Boolean isPaymentIndicator() {
        return paymentIndicator;
    }


    public void setPaymentIndicator(Boolean paymentIndicator) {
        this.paymentIndicator = paymentIndicator;
    }


    @Column(name = "CREDIT_CARD_AGENCY_NAME", nullable = false, length = 45)
    public String getCreditCardOrAgencyName() {
        return creditCardOrAgencyName;
    }


    public void setCreditCardOrAgencyName(String creditCardOrAgencyName) {
        this.creditCardOrAgencyName = creditCardOrAgencyName;
    }


    @Column(name = "ADDRESS1", nullable = true, length = 45)
    public String getAddress1() {
        return address1;
    }


    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    @Column(name = "ADDRESS2", nullable = true, length = 45)
    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }


    @Column(name = "CITY", nullable = true, length = 45)
    public String getCity() {
        return city;
    }


    public void setCity(String city) {
        this.city = city;
    }


    @Column(name = "STATE", nullable = true, length = 2)
    public String getState() {
        return state;
    }


    public void setState(String state) {
        this.state = state;
    }


    @Column(name = "ZIPCODE", nullable = true, length = 20)
    public String getZipCode() {
        return zipCode;
    }


    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }


    @Column(name = "EMAIL", nullable = true, length = 90)
    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }


    @Column(name = "PHONE", nullable = true, length = 40)
    public String getPhone() {
        return phone;
    }


    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Column(name = "FOREIGN_COMPANY", nullable = false, length = 1)
    public Boolean getForeignCompany() {
        return foreignCompany;
    }


    public void setForeignCompany(Boolean foreignCompany) {
        this.foreignCompany = foreignCompany;
    }




    @Column(name = "CONTACT_NAME", nullable = true, length = 45)
    public String getContactName() {
        return contactName;
    }


    public void setContactName(String contactName) {
        this.contactName = contactName;
    }


    @Column(name = "TYPE_CODE", nullable = true, length = 10)
    public String getTypeCode() {
        return typeCode;
    }


    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    @Column(name = "PRE_RECONCILED", nullable = true, length = 1)
    public Boolean getPreReconciled() {
        return preReconciled;
    }
    @Column(name = "PRE_RECONCILED", nullable = true, length = 1)
    public Boolean isPreReconciled() {
        return preReconciled;
    }


    public void setPreReconciled(Boolean preReconciled) {
        this.preReconciled = preReconciled;
    }

    @Column(name = "ENABLE_NON_REIMBURSABLE", nullable = true, length = 1)
    public Boolean getEnableNonReimbursable() {
        return enableNonReimbursable;
    }
    @Column(name = "ENABLE_NON_REIMBURSABLE", nullable = true, length = 1)
    public Boolean isEnableNonReimbursable() {
        return enableNonReimbursable;
    }


    public void setEnableNonReimbursable(Boolean enableNonReimbursable) {
        this.enableNonReimbursable = enableNonReimbursable;
    }


    @Column(name = "VENDOR_NUMBER", nullable = true, length = 20)
    public String getVendorNumber() {
        return vendorNumber;
    }


    public void setVendorNumber(String vendorNumber) {
        this.vendorNumber = vendorNumber;
    }

    @Column(name="ACTV_IND",length=1, nullable=false)
    public Boolean getActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
       this.active = active;
    }

    @SuppressWarnings("rawtypes")
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap map = new LinkedHashMap();
        map.put("creditCardOrAgencyCode", this.creditCardOrAgencyCode);
        map.put("travelCardType", this.travelCardType);
        map.put("creditCardOrAgencyName", this.creditCardOrAgencyName);
        return map;
    }


    @Override
    @Column(name="ACTV_IND",length=1, nullable=false)
    public boolean isActive() {
        return getActive();
    }


}
