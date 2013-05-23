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
package org.kuali.kfs.module.cg.businessobject;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.kfs.integration.cg.ContractsAndGrantsAgencyAddress;
import org.kuali.kfs.module.cg.CGConstants;
import org.kuali.kfs.module.cg.document.service.AgencyAddressService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.location.api.country.Country;
import org.kuali.rice.location.api.country.CountryService;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class AgencyAddress extends PersistableBusinessObjectBase implements Comparable<AgencyAddress>, Primaryable, ContractsAndGrantsAgencyAddress {

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

    private AgencyAddressType agencyAddressType;
    private Agency agency;
    private Country agencyCountry;

    // Invoice Template link

    private String agencyInvoiceTemplateCode;
    private InvoiceTemplate agencyInvoiceTemplate;

    // Invoice Indicator link

    private String invoiceIndicatorCode;
    private InvoiceIndicator invoiceIndicator;

    private String agencyCopiesToPrint;
    private String agencyPrintEnvelopesNumber;

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
    @Override
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
     * Gets the invoiceIndicator attribute.
     *
     * @return Returns the invoiceIndicator.
     */
    public InvoiceIndicator getInvoiceIndicator() {
        return invoiceIndicator;
    }

    /**
     * Sets the invoiceIndicator attribute value.
     *
     * @param invoiceIndicator The invoiceIndicator to set.
     */
    public void setInvoiceIndicator(InvoiceIndicator invoiceIndicator) {
        this.invoiceIndicator = invoiceIndicator;
    }

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

    /**
     * Gets the agencyInvoiceEmailAddress attribute.
     *
     * @return Returns the agencyInvoiceEmailAddress.
     */
    @Override
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
    @Override
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
     * Gets the agencyAddressType attribute.
     *
     * @return Returns the agencyAddressType
     */
    public AgencyAddressType getAgencyAddressType() {
        return agencyAddressType;
    }

    /**
     * Sets the agencyAddressType attribute.
     *
     * @param agencyAddressType The agencyAddressType to set.
     * @deprecated
     */
    @Deprecated
    public void setAgencyAddressType(AgencyAddressType agencyAddressType) {
        this.agencyAddressType = agencyAddressType;
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
    public Country getAgencyCountry() {
//        agencyCountry = SpringContext.getBean(CountryService.class).getCountryIfNecessary(agencyCountryCode, agencyCountry);
        agencyCountry = SpringContext.getBean(CountryService.class).getCountry(agencyCountryCode);
        return agencyCountry;
    }

    /**
     * Sets the agencyCountry attribute value.
     *
     * @param agencyCountry The agencyCountry to set.
     * @deprecated
     */
    @Deprecated
    public void setAgencyCountry(Country agencyCountry) {
        this.agencyCountry = agencyCountry;
    }


    /**
     * Gets the agencyInvoiceTemplateCode attribute.
     *
     * @return Returns the agencyInvoiceTemplateCode.
     */
    @Override
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
     * Gets the agencyInvoiceTemplate attribute.
     *
     * @return Returns the agencyInvoiceTemplate.
     */
    public InvoiceTemplate getAgencyInvoiceTemplate() {
        return agencyInvoiceTemplate;
    }

    /**
     * Sets the agencyInvoiceTemplate attribute value.
     *
     * @param agencyInvoiceTemplate The agencyInvoiceTemplate to set.
     */
    public void setAgencyInvoiceTemplate(InvoiceTemplate agencyInvoiceTemplate) {
        this.agencyInvoiceTemplate = agencyInvoiceTemplate;
    }

    /**
     * Gets the agencyCopiesToPrint attribute.
     *
     * @return Returns the agencyCopiesToPrint.
     */
    @Override
    public String getAgencyCopiesToPrint() {
        return agencyCopiesToPrint;
    }

    /**
     * Sets the agencyCopiesToPrint attribute value.
     *
     * @param agencyCopiesToPrint The agencyCopiesToPrint to set.
     */
    public void setAgencyCopiesToPrint(String agencyCopiesToPrint) {
        this.agencyCopiesToPrint = agencyCopiesToPrint;
    }

    /**
     * Gets the agencyPrintEnvelopes attribute.
     *
     * @return Returns the agencyPrintEnvelopes.
     */
    @Override
    public String getAgencyPrintEnvelopesNumber() {
        return agencyPrintEnvelopesNumber;
    }

    /**
     * Sets the agencyPrintEnvelopes attribute value.
     *
     * @param agencyPrintEnvelopes The agencyPrintEnvelopes to set.
     */
    public void setAgencyPrintEnvelopesNumber(String agencyPrintEnvelopesNumber) {
        this.agencyPrintEnvelopesNumber = agencyPrintEnvelopesNumber;
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
            m.put("agencyAddressIdentifier", this.agencyAddressIdentifier.toString());
        }
        return m;
    }


    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(AgencyAddress address) {
        if (this.getAgencyNumber() != null && address.getAgencyNumber() != null && !this.getAgencyNumber().equalsIgnoreCase(address.getAgencyNumber())) {
            return -1;
        }
        if (this.getAgencyAddressName() != null && address.getAgencyAddressName() != null && !this.getAgencyAddressName().equalsIgnoreCase(address.getAgencyAddressName())) {
            return -1;
        }
        if (this.getAgencyContactName() != null && address.getAgencyContactName() != null && !this.getAgencyContactName().equalsIgnoreCase(address.getAgencyContactName())) {
            return -1;
        }
        if (this.getAgencyFaxNumber() != null && address.getAgencyFaxNumber() != null && !this.getAgencyFaxNumber().equalsIgnoreCase(address.getAgencyFaxNumber())) {
            return -1;
        }
        if (this.getAgencyPhoneNumber() != null && address.getAgencyPhoneNumber() != null && !this.getAgencyPhoneNumber().equalsIgnoreCase(address.getAgencyPhoneNumber())) {
            return -1;
        }
        if (this.getAgencyLine1StreetAddress() != null && address.getAgencyLine1StreetAddress() != null && !this.getAgencyLine1StreetAddress().equalsIgnoreCase(address.getAgencyLine1StreetAddress())) {
            return -1;
        }
        if (this.getAgencyLine2StreetAddress() != null && address.getAgencyLine2StreetAddress() != null && !this.getAgencyLine2StreetAddress().equalsIgnoreCase(address.getAgencyLine2StreetAddress()) || (this.getAgencyLine2StreetAddress() == null && address.getAgencyLine2StreetAddress() != null) || (this.getAgencyLine2StreetAddress() != null && address.getAgencyLine2StreetAddress() == null)) {
            return -1;
        }
        if (this.getAgencyLine3StreetAddress() != null && address.getAgencyLine3StreetAddress() != null && !this.getAgencyLine3StreetAddress().equalsIgnoreCase(address.getAgencyLine3StreetAddress()) || (this.getAgencyLine3StreetAddress() == null && address.getAgencyLine3StreetAddress() != null) || (this.getAgencyLine3StreetAddress() != null && address.getAgencyLine3StreetAddress() == null)) {
            return -1;
        }
        if (this.getAgencyLine4StreetAddress() != null && address.getAgencyLine4StreetAddress() != null && !this.getAgencyLine4StreetAddress().equalsIgnoreCase(address.getAgencyLine4StreetAddress()) || (this.getAgencyLine4StreetAddress() == null && address.getAgencyLine4StreetAddress() != null) || (this.getAgencyLine4StreetAddress() != null && address.getAgencyLine4StreetAddress() == null)) {
            return -1;
        }
        if (this.getAgencyCityName() != null && address.getAgencyCityName() != null && !this.getAgencyCityName().equalsIgnoreCase(address.getAgencyCityName())) {
            return -1;
        }
        if (this.getAgencyStateCode() != null && address.getAgencyStateCode() != null && !this.getAgencyStateCode().equalsIgnoreCase(address.getAgencyStateCode()) || (this.getAgencyStateCode() == null && address.getAgencyStateCode() != null) || (this.getAgencyStateCode() != null && address.getAgencyStateCode() == null)) {
            return -1;
        }
        if (this.getAgencyZipCode() != null && address.getAgencyZipCode() != null && !this.getAgencyZipCode().equalsIgnoreCase(address.getAgencyZipCode()) || (this.getAgencyZipCode() == null && address.getAgencyZipCode() != null) || (this.getAgencyZipCode() != null && address.getAgencyZipCode() == null)) {
            return -1;
        }
        if (this.getAgencyCountryCode() != null && address.getAgencyCountryCode() != null && !this.getAgencyCountryCode().equalsIgnoreCase(address.getAgencyCountryCode())) {
            return -1;
        }
        if (this.getAgencyAddressInternationalProvinceName() != null && address.getAgencyAddressInternationalProvinceName() != null && !this.getAgencyAddressInternationalProvinceName().equalsIgnoreCase(address.getAgencyAddressInternationalProvinceName()) || (this.getAgencyAddressInternationalProvinceName() == null && address.getAgencyAddressInternationalProvinceName() != null) || (this.getAgencyAddressInternationalProvinceName() != null && address.getAgencyAddressInternationalProvinceName() == null)) {
            return -1;
        }
        if (this.getAgencyContactEmailAddress() != null && address.getAgencyContactEmailAddress() != null && !this.getAgencyContactEmailAddress().equalsIgnoreCase(address.getAgencyContactEmailAddress()) || (this.getAgencyContactEmailAddress() == null && address.getAgencyContactEmailAddress() != null) || (this.getAgencyContactEmailAddress() != null && address.getAgencyContactEmailAddress() == null)) {
            return -1;
        }
        if (this.getAgencyInvoiceEmailAddress() != null && address.getAgencyInvoiceEmailAddress() != null && !this.getAgencyInvoiceEmailAddress().equalsIgnoreCase(address.getAgencyInvoiceEmailAddress()) || (this.getAgencyInvoiceEmailAddress() == null && address.getAgencyInvoiceEmailAddress() != null) || (this.getAgencyInvoiceEmailAddress() != null && address.getAgencyInvoiceEmailAddress() == null)) {
            return -1;
        }
        if (this.getAgencyAddressTypeCode() != null && address.getAgencyAddressTypeCode() != null && !this.getAgencyAddressTypeCode().equalsIgnoreCase(address.getAgencyAddressTypeCode()) || (this.getAgencyAddressTypeCode() == null && address.getAgencyAddressTypeCode() != null) || (this.getAgencyAddressTypeCode() != null && address.getAgencyAddressTypeCode() == null)) {
            return -1;
        }
        if (this.getAgencyAddressIdentifier() != null && address.getAgencyAddressIdentifier() != null && this.getAgencyAddressIdentifier().compareTo(address.getAgencyAddressIdentifier()) != 0 || (this.getAgencyAddressIdentifier() == null && address.getAgencyAddressIdentifier() != null) || (this.getAgencyAddressIdentifier() != null && address.getAgencyAddressIdentifier() == null)) {
            return -1;
        }
        return 0;
    }

    @Override protected void prePersist() {
        super.prePersist();
        AgencyAddressService agencyAddressService = SpringContext.getBean(AgencyAddressService.class);
        long agencyAddressIdentifier = agencyAddressService.getNextAgencyAddressIdentifier();
        this.setAgencyAddressIdentifier(agencyAddressIdentifier);
    }

    @Override
    public boolean isPrimary() {
        if (ObjectUtils.isNotNull(this.agencyAddressTypeCode) && this.agencyAddressTypeCode.equals(CGConstants.AGENCY_PRIMARY_ADDRESSES_TYPE_CODE)) {
            return true;
        }
        return false;
    }
}
