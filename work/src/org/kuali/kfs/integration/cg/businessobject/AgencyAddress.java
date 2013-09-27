/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.kfs.integration.cg.businessobject;

import java.sql.Date;

import org.kuali.kfs.integration.cg.ContractsAndGrantsAgencyAddress;
import org.kuali.kfs.integration.cg.ContractsAndGrantsCGBAgency;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Integration Class for AgencyAddress
 */
public class AgencyAddress implements ContractsAndGrantsAgencyAddress {

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
    private String agencyInvoiceEmailAddress;
    private String agencyAddressTypeCode;
    private Date agencyAddressEndDate;

    // Invoice Template link

    private String agencyInvoiceTemplateCode;

    // Invoice Indicator link

    private String invoiceIndicatorCode;

    private Integer agencyCopiesToPrint;
    private Integer agencyPrintEnvelopesNumber;

    private ContractsAndGrantsCGBAgency agency;

    /**
     * Default constructor.
     */
    public AgencyAddress() {

    }

    /**
     * Gets the invoiceIndicatorCode attribute.
     * 
     * @return Returns the invoiceIndicatorCode.
     */
    public String getInvoiceIndicatorCode() {
        return invoiceIndicatorCode;
    }

    /**
     * Sets the invoiceIndicatorCode attribute value.
     * 
     * @param invoiceIndicatorCode The invoiceIndicatorCode to set.
     */
    public void setInvoiceIndicatorCode(String invoiceIndicatorCode) {
        this.invoiceIndicatorCode = invoiceIndicatorCode;
    }

    /**
     * Gets the agencyNumber attribute.
     * 
     * @return Returns the agencyNumber
     */
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

    /**
     * Gets the agencyInvoiceEmailAddress attribute.
     * 
     * @return Returns the agencyInvoiceEmailAddress.
     */
    public String getAgencyInvoiceEmailAddress() {
        return agencyInvoiceEmailAddress;
    }

    /**
     * Sets the agencyInvoiceEmailAddress attribute value.
     * 
     * @param agencyInvoiceEmailAddress The agencyInvoiceEmailAddress to set.
     */
    public void setAgencyInvoiceEmailAddress(String agencyInvoiceEmailAddress) {
        this.agencyInvoiceEmailAddress = agencyInvoiceEmailAddress;
    }

    /**
     * Gets the agencyAddressTypeCode attribute.
     * 
     * @return Returns the agencyAddressTypeCode
     */
    public String getAgencyAddressTypeCode() {
        return agencyAddressTypeCode;
    }

    /**
     * Sets the agencyAddressTypeCode attribute.
     * 
     * @param agencyAddressTypeCode The agencyAddressTypeCode to set.
     */
    public void setAgencyAddressTypeCode(String agencyAddressTypeCode) {
        this.agencyAddressTypeCode = agencyAddressTypeCode;
    }


    /**
     * Gets the agencyAddressEndDate attribute.
     * 
     * @return Returns the agencyAddressEndDate
     */
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
     * Gets the agencyInvoiceTemplateCode attribute.
     * 
     * @return Returns the agencyInvoiceTemplateCode.
     */
    public String getAgencyInvoiceTemplateCode() {

        return agencyInvoiceTemplateCode;
    }

    /**
     * Sets the agencyInvoiceTemplateCode attribute value.
     * 
     * @param agencyInvoiceTemplateCode The agencyInvoiceTemplateCode to set.
     */
    public void setAgencyInvoiceTemplateCode(String agencyInvoiceTemplateCode) {
        this.agencyInvoiceTemplateCode = agencyInvoiceTemplateCode;
    }


    /**
     * Gets the agencyCopiesToPrint attribute.
     * 
     * @return Returns the agencyCopiesToPrint.
     */
    public Integer getAgencyCopiesToPrint() {
        return agencyCopiesToPrint;
    }

    /**
     * Sets the agencyCopiesToPrint attribute value.
     * 
     * @param agencyCopiesToPrint The agencyCopiesToPrint to set.
     */
    public void setAgencyCopiesToPrint(Integer agencyCopiesToPrint) {
        this.agencyCopiesToPrint = agencyCopiesToPrint;
    }

    /**
     * Gets the agencyPrintEnvelopes attribute.
     * 
     * @return Returns the agencyPrintEnvelopes.
     */
    public Integer getAgencyPrintEnvelopesNumber() {
        return agencyPrintEnvelopesNumber;
    }

    /**
     * Sets the agencyPrintEnvelopes attribute value.
     * 
     * @param agencyPrintEnvelopes The agencyPrintEnvelopes to set.
     */
    public void setAgencyPrintEnvelopesNumber(Integer agencyPrintEnvelopesNumber) {
        this.agencyPrintEnvelopesNumber = agencyPrintEnvelopesNumber;
    }

    /**
     * Gets the agencyContactName attribute.
     * 
     * @return Returns the agencyContactName.
     */
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


    public boolean isPrimary() {
        if (ObjectUtils.isNotNull(this.agencyAddressTypeCode) && this.agencyAddressTypeCode.equals(KFSConstants.ContractsGrantsModuleDocumentTypeCodes.AGENCY_PRIMARY_ADDRESSES_TYPE_CODE))
            return true;
        return false;
    }


    /**
     * Gets the agency attribute.
     * 
     * @return Returns the agency.
     */
    public ContractsAndGrantsCGBAgency getAgency() {
        return agency;
    }

    /**
     * Sets the agency attribute value.
     * 
     * @param agency The agency to set.
     */
    public void setAgency(ContractsAndGrantsCGBAgency agency) {
        this.agency = agency;
    }

    public void prepareForWorkflow() {
    }


    public void refresh() {
    }


}
