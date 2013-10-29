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
package org.kuali.kfs.integration.cg;

import java.sql.Date;

import org.kuali.rice.krad.bo.ExternalizableBusinessObject;

/**
 * Integration Interface for Agency Address
 */
public interface ContractsAndGrantsAgencyAddress extends ExternalizableBusinessObject {

    /**
     * Gets the invoiceIndicatorCode attribute.
     * 
     * @return Returns the invoiceIndicatorCode.
     */
    public String getInvoiceIndicatorCode();

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
     * Gets the agencyInvoiceEmailAddress attribute.
     * 
     * @return Returns the agencyInvoiceEmailAddress.
     */
    public String getAgencyInvoiceEmailAddress();

    /**
     * Gets the agencyAddressTypeCode attribute.
     * 
     * @return Returns the agencyAddressTypeCode
     */
    public String getAgencyAddressTypeCode();

    /**
     * Gets the agencyAddressEndDate attribute.
     * 
     * @return Returns the agencyAddressEndDate
     */
    public Date getAgencyAddressEndDate();


    /**
     * Gets the agencyInvoiceTemplateCode attribute.
     * 
     * @return Returns the agencyInvoiceTemplateCode.
     */
    public String getAgencyInvoiceTemplateCode();


    /**
     * Gets the agencyCopiesToPrint attribute.
     * 
     * @return Returns the agencyCopiesToPrint.
     */
    public Integer getAgencyCopiesToPrint();

    /**
     * Gets the agencyPrintEnvelopes attribute.
     * 
     * @return Returns the agencyPrintEnvelopes.
     */
    public Integer getAgencyPrintEnvelopesNumber();

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
