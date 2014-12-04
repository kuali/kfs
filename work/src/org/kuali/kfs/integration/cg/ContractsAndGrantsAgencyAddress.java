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
package org.kuali.kfs.integration.cg;

import java.sql.Date;

import org.kuali.rice.krad.bo.ExternalizableBusinessObject;

/**
 * Integration Interface for Agency Address
 */
public interface ContractsAndGrantsAgencyAddress extends ExternalizableBusinessObject {

    /**
     * Gets the agencyNumber attribute.
     *
     * @return Returns the agencyNumber
     */
    public String getAgencyNumber();

    /**
     * Gets the agencyAddressIdentifier attribute.
     *
     * @return Returns the agencyAddressIdentifier
     */
    public Long getAgencyAddressIdentifier();

    /**
     * Gets the agencyAddressName attribute.
     *
     * @return Returns the agencyAddressName
     */
    public String getAgencyAddressName();

    /**
     * Gets the agencyLine1StreetAddress attribute.
     *
     * @return Returns the agencyLine1StreetAddress
     */
    public String getAgencyLine1StreetAddress();


    /**
     * Gets the agencyLine2StreetAddress attribute.
     *
     * @return Returns the agencyLine2StreetAddress
     */
    public String getAgencyLine2StreetAddress();

    /**
     * Gets the agencyLine3StreetAddress attribute.
     *
     * @return Returns the agencyLine3StreetAddress
     */
    public String getAgencyLine3StreetAddress();

    /**
     * Gets the agencyLine4StreetAddress attribute.
     *
     * @return Returns the agencyLine4StreetAddress
     */
    public String getAgencyLine4StreetAddress();

    /**
     * Gets the agencyCityName attribute.
     *
     * @return Returns the agencyCityName
     */
    public String getAgencyCityName();


    /**
     * Gets the agencyStateCode attribute.
     *
     * @return Returns the agencyStateCode
     */
    public String getAgencyStateCode();


    /**
     * Gets the agencyZipCode attribute.
     *
     * @return Returns the agencyZipCode
     */
    public String getAgencyZipCode();

    /**
     * Gets the agencyAddressInternationalProvinceName attribute.
     *
     * @return Returns the agencyAddressInternationalProvinceName.
     */
    public String getAgencyAddressInternationalProvinceName();

    /**
     * Gets the agencyCountryCode attribute.
     *
     * @return Returns the agencyCountryCode.
     */
    public String getAgencyCountryCode();

    /**
     * Gets the agencyInternationalMailCode attribute.
     *
     * @return Returns the agencyInternationalMailCode
     */
    public String getAgencyInternationalMailCode();


    /**
     * Gets the agencyContactEmailAddress attribute.
     *
     * @return Returns the agencyContactEmailAddress.
     */
    public String getAgencyContactEmailAddress();

    /**
     * Gets the customerAddressTypeCode attribute.
     *
     * @return Returns the customerAddressTypeCode
     */
    public String getCustomerAddressTypeCode();

    /**
     * Gets the agencyAddressEndDate attribute.
     *
     * @return Returns the agencyAddressEndDate
     */
    public Date getAgencyAddressEndDate();

    /**
     * Gets the agencyContactName attribute.
     *
     * @return Returns the agencyContactName.
     */
    public String getAgencyContactName();

    /**
     * Gets the agencyPhoneNumber attribute.
     *
     * @return Returns the agencyPhoneNumber.
     */
    public String getAgencyPhoneNumber();

    /**
     * Gets the agencyFaxNumber attribute.
     *
     * @return Returns the agencyFaxNumber.
     */
    public String getAgencyFaxNumber();

    /**
     * Gets the agency attribute.
     *
     * @return Returns the agency.
     */
    public ContractsAndGrantsBillingAgency getAgency();

    public boolean isPrimary();
}
