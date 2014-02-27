/*
 * Copyright 2014 The Kuali Foundation.
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
package org.kuali.kfs.module.external.kc.businessobject;

import java.sql.Date;

import org.kuali.kfs.integration.cg.ContractsAndGrantsAgencyAddress;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.external.kc.dto.RolodexDTO;

public class AgencyAddress implements ContractsAndGrantsAgencyAddress {

    private String agencyNumber;
    private Long agencyAddressIdentifier;
    private String agencyAddressName;
    private String agencyLine1StreetAddress;
    private String agencyLine2StreetAddress;
    private String agencyLine3StreetAddress;
    private String agencyLine4StreetAddress;
    private String agencyCityName;
    private String agencyStateCode;
    private String agencyZipCode;
    private String agencyAddressInternationalProvinceName;
    private String agencyCountryCode;
    private String agencyInternationalMailCode;
    private String agencyContactEmailAddress;
    private String customerAddressTypeCode;
    private Date agencyAddressEndDate;
    private String agencyContractName;
    private String agencyPhoneNumber;
    private String agencyFaxNumber;
    private String agencyContactName;
    private Agency agency;
    private boolean primary;

    public AgencyAddress() { }

    public AgencyAddress(Agency agency, RolodexDTO kcAddress) {
        this.agency = agency;
        this.agencyNumber = agency.getAgencyNumber();
        this.agencyAddressIdentifier = kcAddress.getRolodexId().longValue();
        this.agencyAddressName = kcAddress.getFullName();
        this.agencyLine1StreetAddress = kcAddress.getAddressLine1();
        this.agencyLine2StreetAddress = kcAddress.getAddressLine2();
        this.agencyLine3StreetAddress = kcAddress.getAddressLine3();
        this.agencyCityName = kcAddress.getCity();
        this.agencyZipCode = kcAddress.getPostalCode();
        this.agencyAddressInternationalProvinceName = kcAddress.getState();
        this.agencyCountryCode = kcAddress.getCountryCode();
        this.agencyInternationalMailCode = kcAddress.getPostalCode();
        this.agencyContactEmailAddress = kcAddress.getEmailAddress();
        this.agencyContactName = kcAddress.getFullName();
        this.agencyPhoneNumber = kcAddress.getPhoneNumber();
        this.agencyFaxNumber = kcAddress.getFaxNumber();
        this.customerAddressTypeCode = ArConstants.AGENCY_PRIMARY_ADDRESSES_TYPE_CODE;
        this.primary = true;
    }

    @Override
    public String getAgencyNumber() {
        return agencyNumber;
    }


    public void setAgencyNumber(String agencyNumber) {
        this.agencyNumber = agencyNumber;
    }


    @Override
    public Long getAgencyAddressIdentifier() {
        return agencyAddressIdentifier;
    }


    public void setAgencyAddressIdentifier(Long agencyAddressIdentifier) {
        this.agencyAddressIdentifier = agencyAddressIdentifier;
    }


    @Override
    public String getAgencyAddressName() {
        return agencyAddressName;
    }


    public void setAgencyAddressName(String agencyAddressName) {
        this.agencyAddressName = agencyAddressName;
    }


    @Override
    public String getAgencyLine1StreetAddress() {
        return agencyLine1StreetAddress;
    }


    public void setAgencyLine1StreetAddress(String agencyLine1StreetAddress) {
        this.agencyLine1StreetAddress = agencyLine1StreetAddress;
    }


    @Override
    public String getAgencyLine2StreetAddress() {
        return agencyLine2StreetAddress;
    }


    public void setAgencyLine2StreetAddress(String agencyLine2StreetAddress) {
        this.agencyLine2StreetAddress = agencyLine2StreetAddress;
    }


    @Override
    public String getAgencyLine3StreetAddress() {
        return agencyLine3StreetAddress;
    }


    public void setAgencyLine3StreetAddress(String agencyLine3StreetAddress) {
        this.agencyLine3StreetAddress = agencyLine3StreetAddress;
    }


    @Override
    public String getAgencyCityName() {
        return agencyCityName;
    }


    public void setAgencyCityName(String agencyCityName) {
        this.agencyCityName = agencyCityName;
    }


    @Override
    public String getAgencyStateCode() {
        return agencyStateCode;
    }


    public void setAgencyStateCode(String agencyStateCode) {
        this.agencyStateCode = agencyStateCode;
    }


    @Override
    public String getAgencyZipCode() {
        return agencyZipCode;
    }


    public void setAgencyZipCode(String agencyZipCode) {
        this.agencyZipCode = agencyZipCode;
    }


    @Override
    public String getAgencyAddressInternationalProvinceName() {
        return agencyAddressInternationalProvinceName;
    }


    public void setAgencyAddressInternationalProvinceName(String agencyAddressInternationalProvinceName) {
        this.agencyAddressInternationalProvinceName = agencyAddressInternationalProvinceName;
    }


    @Override
    public String getAgencyCountryCode() {
        return agencyCountryCode;
    }


    public void setAgencyCountryCode(String agencyCountryCode) {
        this.agencyCountryCode = agencyCountryCode;
    }


    @Override
    public String getAgencyInternationalMailCode() {
        return agencyInternationalMailCode;
    }


    public void setAgencyInternationalMailCode(String agencyInternationalMailCode) {
        this.agencyInternationalMailCode = agencyInternationalMailCode;
    }


    @Override
    public String getAgencyContactEmailAddress() {
        return agencyContactEmailAddress;
    }


    public void setAgencyContactEmailAddress(String agencyContactEmailAddress) {
        this.agencyContactEmailAddress = agencyContactEmailAddress;
    }


    @Override
    public String getCustomerAddressTypeCode() {
        return customerAddressTypeCode;
    }


    public void setCustomerAddressTypeCode(String customerAddressTypeCode) {
        this.customerAddressTypeCode = customerAddressTypeCode;
    }


    @Override
    public Date getAgencyAddressEndDate() {
        return agencyAddressEndDate;
    }


    public void setAgencyAddressEndDate(Date agencyAddressEndDate) {
        this.agencyAddressEndDate = agencyAddressEndDate;
    }


    public String getAgencyContractName() {
        return agencyContractName;
    }


    public void setAgencyContractName(String agencyContractName) {
        this.agencyContractName = agencyContractName;
    }


    @Override
    public String getAgencyPhoneNumber() {
        return agencyPhoneNumber;
    }


    public void setAgencyPhoneNumber(String agencyPhoneNumber) {
        this.agencyPhoneNumber = agencyPhoneNumber;
    }


    @Override
    public String getAgencyFaxNumber() {
        return agencyFaxNumber;
    }


    public void setAgencyFaxNumber(String agencyFaxNumber) {
        this.agencyFaxNumber = agencyFaxNumber;
    }


    @Override
    public Agency getAgency() {
        return agency;
    }


    public void setAgency(Agency agency) {
        this.agency = agency;
    }


    @Override
    public boolean isPrimary() {
        return primary;
    }


    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    @Override
    public void refresh() { }


    @Override
    public String getAgencyLine4StreetAddress() {
        return agencyLine4StreetAddress;
    }


    public void setAgencyLine4StreetAddress(String agencyLine4StreetAddress) {
        this.agencyLine4StreetAddress = agencyLine4StreetAddress;
    }


    @Override
    public String getAgencyContactName() {
        return agencyContactName;
    }


    public void setAgencyContactName(String agencyContactName) {
        this.agencyContactName = agencyContactName;
    }

}
