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
package org.kuali.kfs.module.cg.businessobject;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.kfs.integration.ar.AccountsReceivableCustomer;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomerAddressType;
import org.kuali.kfs.integration.cg.ContractsAndGrantsAgencyAddress;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.cg.CGConstants;
import org.kuali.kfs.module.cg.CGPropertyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.location.framework.country.CountryEbo;

public class AgencyAddress extends PersistableBusinessObjectBase implements Primaryable, ContractsAndGrantsAgencyAddress {

    private String agencyNumber;
    private Long agencyAddressIdentifier;
    private String agencyAddressName;
    private String agencyContactName;
    private String agencyLine1StreetAddress;
    private String agencyLine2StreetAddress;
    private String agencyLine3StreetAddress;
    private String agencyLine4StreetAddress;
    private String agencyCityName;
    private String agencyStateCode;
    private String agencyZipCode;
    private String agencyCountryCode;
    private String agencyPhoneNumber;
    private String agencyFaxNumber;
    private String agencyAddressInternationalProvinceName;
    private String agencyInternationalMailCode;
    private String agencyContactEmailAddress;
    private String customerAddressTypeCode;
    private Date agencyAddressEndDate;

    private AccountsReceivableCustomerAddressType customerAddressType;
    private Agency agency;
    private CountryEbo agencyCountry;

     /**
     * Gets the agencyNumber attribute.
     *
     * @return Returns the agencyNumber
     */
    @Override
    public String getAgencyNumber() {
        return agencyNumber;
    }

    /**
     * Sets the agencyNumber attribute.
     *
     * @param agencyNumber The agencyNumber to set.
     */
    public void setAgencyNumber(String agencyNumber) {
        this.agencyNumber = agencyNumber;
    }


    /**
     * Gets the agencyAddressIdentifier attribute.
     *
     * @return Returns the agencyAddressIdentifier
     */
    @Override
    public Long getAgencyAddressIdentifier() {
        return agencyAddressIdentifier;
    }

    /**
     * Sets the agencyAddressIdentifier attribute.
     *
     * @param agencyAddressIdentifier The agencyAddressIdentifier to set.
     */
    public void setAgencyAddressIdentifier(Long agencyAddressIdentifier) {
        this.agencyAddressIdentifier = agencyAddressIdentifier;
    }


    /**
     * Gets the agencyAddressName attribute.
     *
     * @return Returns the agencyAddressName
     */
    @Override
    public String getAgencyAddressName() {
        return agencyAddressName;
    }

    /**
     * Sets the agencyAddressName attribute.
     *
     * @param agencyAddressName The agencyAddressName to set.
     */
    public void setAgencyAddressName(String agencyAddressName) {
        this.agencyAddressName = agencyAddressName;
    }


    /**
     * Gets the agencyLine1StreetAddress attribute.
     *
     * @return Returns the agencyLine1StreetAddress
     */
    @Override
    public String getAgencyLine1StreetAddress() {
        return agencyLine1StreetAddress;
    }

    /**
     * Sets the agencyLine1StreetAddress attribute.
     *
     * @param agencyLine1StreetAddress The agencyLine1StreetAddress to set.
     */
    public void setAgencyLine1StreetAddress(String agencyLine1StreetAddress) {
        this.agencyLine1StreetAddress = agencyLine1StreetAddress;
    }


    /**
     * Gets the agencyLine2StreetAddress attribute.
     *
     * @return Returns the agencyLine2StreetAddress
     */
    @Override
    public String getAgencyLine2StreetAddress() {
        return agencyLine2StreetAddress;
    }

    /**
     * Sets the agencyLine2StreetAddress attribute.
     *
     * @param agencyLine2StreetAddress The agencyLine2StreetAddress to set.
     */
    public void setAgencyLine2StreetAddress(String agencyLine2StreetAddress) {
        this.agencyLine2StreetAddress = agencyLine2StreetAddress;
    }

    /**
     * Gets the agencyLine3StreetAddress attribute.
     *
     * @return Returns the agencyLine3StreetAddress
     */
    @Override
    public String getAgencyLine3StreetAddress() {
        return agencyLine3StreetAddress;
    }

    /**
     * Sets the agencyLine3StreetAddress attribute.
     *
     * @param agencyLine3StreetAddress The agencyLine3StreetAddress to set.
     */
    public void setAgencyLine3StreetAddress(String agencyLine3StreetAddress) {
        this.agencyLine3StreetAddress = agencyLine3StreetAddress;
    }

    /**
     * Gets the agencyLine4StreetAddress attribute.
     *
     * @return Returns the agencyLine4StreetAddress
     */
    @Override
    public String getAgencyLine4StreetAddress() {
        return agencyLine4StreetAddress;
    }

    /**
     * Sets the agencyLine4StreetAddress attribute.
     *
     * @param agencyLine4StreetAddress The agencyLine4StreetAddress to set.
     */
    public void setAgencyLine4StreetAddress(String agencyLine4StreetAddress) {
        this.agencyLine4StreetAddress = agencyLine4StreetAddress;
    }

    /**
     * Gets the agencyCityName attribute.
     *
     * @return Returns the agencyCityName
     */
    @Override
    public String getAgencyCityName() {
        return agencyCityName;
    }

    /**
     * Sets the agencyCityName attribute.
     *
     * @param agencyCityName The agencyCityName to set.
     */
    public void setAgencyCityName(String agencyCityName) {
        this.agencyCityName = agencyCityName;
    }


    /**
     * Gets the agencyStateCode attribute.
     *
     * @return Returns the agencyStateCode
     */
    @Override
    public String getAgencyStateCode() {
        return agencyStateCode;
    }

    /**
     * Sets the agencyStateCode attribute.
     *
     * @param agencyStateCode The agencyStateCode to set.
     */
    public void setAgencyStateCode(String agencyStateCode) {
        this.agencyStateCode = agencyStateCode;
    }


    /**
     * Gets the agencyZipCode attribute.
     *
     * @return Returns the agencyZipCode
     */
    @Override
    public String getAgencyZipCode() {
        return agencyZipCode;
    }

    /**
     * Sets the agencyZipCode attribute.
     *
     * @param agencyZipCode The agencyZipCode to set.
     */
    public void setAgencyZipCode(String agencyZipCode) {
        this.agencyZipCode = agencyZipCode;
    }

    /**
     * Gets the agencyAddressInternationalProvinceName attribute.
     *
     * @return Returns the agencyAddressInternationalProvinceName.
     */
    @Override
    public String getAgencyAddressInternationalProvinceName() {
        return agencyAddressInternationalProvinceName;
    }

    /**
     * Sets the agencyAddressInternationalProvinceName attribute value.
     *
     * @param agencyAddressInternationalProvinceName The agencyAddressInternationalProvinceName to set.
     */
    public void setAgencyAddressInternationalProvinceName(String agencyAddressInternationalProvinceName) {
        this.agencyAddressInternationalProvinceName = agencyAddressInternationalProvinceName;
    }

    /**
     * Gets the agencyCountryCode attribute.
     *
     * @return Returns the agencyCountryCode.
     */
    @Override
    public String getAgencyCountryCode() {
        return agencyCountryCode;
    }

    /**
     * Sets the agencyCountryCode attribute value.
     *
     * @param agencyCountryCode The agencyCountryCode to set.
     */
    public void setAgencyCountryCode(String agencyCountryCode) {
        this.agencyCountryCode = agencyCountryCode;
    }

    /**
     * Gets the agencyInternationalMailCode attribute.
     *
     * @return Returns the agencyInternationalMailCode
     */
    @Override
    public String getAgencyInternationalMailCode() {
        return agencyInternationalMailCode;
    }

    /**
     * Sets the agencyInternationalMailCode attribute.
     *
     * @param agencyInternationalMailCode The agencyInternationalMailCode to set.
     */
    public void setAgencyInternationalMailCode(String agencyInternationalMailCode) {
        this.agencyInternationalMailCode = agencyInternationalMailCode;
    }


    /**
     * Gets the agencyContactEmailAddress attribute.
     *
     * @return Returns the agencyContactEmailAddress.
     */
    @Override
    public String getAgencyContactEmailAddress() {
        return agencyContactEmailAddress;
    }

    /**
     * Sets the agencyContactEmailAddress attribute value.
     *
     * @param agencyContactEmailAddress The agencyContactEmailAddress to set.
     */
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

    /**
     * Gets the agencyAddressEndDate attribute.
     *
     * @return Returns the agencyAddressEndDate
     */
    @Override
    public Date getAgencyAddressEndDate() {
        return agencyAddressEndDate;
    }

    /**
     * Sets the agencyAddressEndDate attribute.
     *
     * @param agencyAddressEndDate The agencyAddressEndDate to set.
     */
    public void setAgencyAddressEndDate(Date agencyAddressEndDate) {
        this.agencyAddressEndDate = agencyAddressEndDate;
    }

    /**
     * Gets the customerAddressType attribute.
     *
     * @return Returns the customerAddressType
     */
    public AccountsReceivableCustomerAddressType getCustomerAddressType() {
        return customerAddressType;
    }

    /**
     * Sets the customerAddressType attribute.
     *
     * @param customerAddressType The customerAddressType to set.
     * @deprecated
     */
    @Deprecated
    public void setCustomerAddressType(AccountsReceivableCustomerAddressType customerAddressType) {
        this.customerAddressType = customerAddressType;
    }


    /**
     * Gets the agency attribute.
     *
     * @return Returns the agency.
     */
    @Override
    public Agency getAgency() {
        return agency;
    }

    /**
     * Sets the agency attribute value.
     *
     * @param agency The agency to set.
     */
    public void setAgency(Agency agency) {
        this.agency = agency;
    }

    /**
     * Gets the agencyCountry attribute.
     *
     * @return Returns the agencyCountry.
     */
    public CountryEbo getAgencyCountry() {
        return agencyCountry = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(AccountsReceivableCustomer.class).retrieveExternalizableBusinessObjectIfNecessary(this, agencyCountry, CGPropertyConstants.AgencyAddressFields.AGENCY_COUNTRY);
    }

    /**
     * Sets the agencyCountry attribute value.
     *
     * @param agencyCountry The agencyCountry to set.
     * @deprecated
     */
    @Deprecated
    public void setAgencyCountry(CountryEbo agencyCountry) {
        this.agencyCountry = agencyCountry;
    }

    /**
     * Gets the agencyContactName attribute.
     *
     * @return Returns the agencyContactName.
     */
    @Override
    public String getAgencyContactName() {
        return agencyContactName;
    }

    /**
     * Sets the agencyContactName attribute value.
     *
     * @param agencyContactName The agencyContactName to set.
     */
    public void setAgencyContactName(String agencyContactName) {
        this.agencyContactName = agencyContactName;
    }

    /**
     * Gets the agencyPhoneNumber attribute.
     *
     * @return Returns the agencyPhoneNumber.
     */
    @Override
    public String getAgencyPhoneNumber() {
        return agencyPhoneNumber;
    }

    /**
     * Sets the agencyPhoneNumber attribute value.
     *
     * @param agencyPhoneNumber The agencyPhoneNumber to set.
     */
    public void setAgencyPhoneNumber(String agencyPhoneNumber) {
        this.agencyPhoneNumber = agencyPhoneNumber;
    }

    /**
     * Gets the agencyFaxNumber attribute.
     *
     * @return Returns the agencyFaxNumber.
     */
    @Override
    public String getAgencyFaxNumber() {
        return agencyFaxNumber;
    }

    /**
     * Sets the agencyFaxNumber attribute value.
     *
     * @param agencyFaxNumber The agencyFaxNumber to set.
     */
    public void setAgencyFaxNumber(String agencyFaxNumber) {
        this.agencyFaxNumber = agencyFaxNumber;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    @SuppressWarnings("unchecked")
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.AGENCY_NUMBER, this.agencyNumber);
        if (this.agencyAddressIdentifier != null) {
            m.put(ArPropertyConstants.ContractsAndGrantsAgencyAddressFields.AGENCY_ADDRESS_IDENTIFIER, this.agencyAddressIdentifier.toString());
        }
        return m;
    }

    @Override
    public boolean isPrimary() {
        if (ObjectUtils.isNotNull(this.customerAddressTypeCode) && this.customerAddressTypeCode.equals(CGConstants.AGENCY_PRIMARY_ADDRESSES_TYPE_CODE)) {
            return true;
        }
        return false;
    }
}
