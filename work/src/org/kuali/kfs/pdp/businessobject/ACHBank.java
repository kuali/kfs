/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.pdp.businessobject;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.location.api.LocationConstants;
import org.kuali.rice.location.framework.postalcode.PostalCodeEbo;
import org.kuali.rice.location.framework.state.StateEbo;

public class ACHBank extends PersistableBusinessObjectBase implements MutableInactivatable {

    protected String bankRoutingNumber;
    protected String bankOfficeCode;
    protected String bankServiceNumber;
    protected String bankTypeCode;
    protected String bankNewRoutingNumber;
    protected String bankName;
    protected String bankStreetAddress;
    protected String bankCityName;
    protected String bankStateCode;
    protected String bankZipCode;
    protected String bankPhoneAreaCode;
    protected String bankPhonePrefixNumber;
    protected String bankPhoneSuffixNumber;
    protected String bankInstitutionStatusCode;
    protected String bankDataViewCode;
    protected boolean active;

    protected StateEbo bankState;
    protected PostalCodeEbo postalCode;

    /**
     * Default constructor.
     */
    public ACHBank() {

    }

    /**
     * This constructor takes a line of data from https://www.fededirectory.frb.org/FedACHdir.txt and populates the object
     *
     * @param fileData
     */
    public ACHBank(String fileData) {
        // 074914274O0710003011020207000000000UNITED COMMERCE BANK 211 SOUTH COLLEGE AVENUE BLOOMINGTON IN474040000812336226511
        // Routing Number 9 1-9 The institution's routing number
        // Office Code 1 10 Main office or branch O=main B=branch
        // Servicing FRB Number 9 11-19 Servicing Fed's main office routing number
        // Record Type Code 1 20 The code indicating the ABA number to be used to route or send ACH items to the RFI
        // 0 = Institution is a Federal Reserve Bank
        // 1 = Send items to customer routing number
        // 2 = Send items to customer using new routing number field
        // Change Date 6 21-26 Date of last change to CRF information (MMDDYY)
        // New Routing Number 9 27-35 Institution's new routing number resulting from a merger or renumber
        // Customer Name 36 36-71 Commonly used abbreviated name
        // Address 36 72-107 Delivery address
        // City 20 108-127 City name in the delivery address
        // State Code 2 128-129 State code of the state in the delivery address
        // Zipcode 5 130-134 Zip code in the delivery address
        // Zipcode Extension 4 135-138 Zip code extension in the delivery address
        // Telephone Area Code 3 139-141 Area code of the CRF contact telephone number
        // Telephone Prefix Number 3 142-144 Prefix of the CRF contact telephone number
        // Telephone Suffix Number 4 145-148 Suffix of the CRF contact telephone number
        // Institution Status Code 1 149 Code is based on the customers receiver code
        // 1=Receives Gov/Comm
        // Data View Code 1 150 1=Current view
        // Filler 5 151-155 Spaces

        setBankRoutingNumber(getField(fileData, 1, 9));
        setBankOfficeCode(getField(fileData, 10, 1));
        setBankServiceNumber(getField(fileData, 11, 9));
        setBankTypeCode(getField(fileData, 20, 1));
        setBankNewRoutingNumber(getField(fileData, 27, 9));
        setBankName(getField(fileData, 36, 36));
        setBankStreetAddress(getField(fileData, 72, 36));
        setBankCityName(getField(fileData, 108, 20));
        setBankStateCode(getField(fileData, 128, 2));
        setBankZipCode(getField(fileData, 130, 5));
        setBankPhoneAreaCode(getField(fileData, 139, 3));
        setBankPhonePrefixNumber(getField(fileData, 142, 3));
        setBankPhoneSuffixNumber(getField(fileData, 145, 4));
        setBankInstitutionStatusCode(getField(fileData, 149, 1));
        setBankDataViewCode(getField(fileData, 150, 1));
        setActive(true);
    }

    protected String getField(String data, int startChar, int length) {
        return data.substring(startChar - 1, startChar + length - 1).trim();
    }

    /**
     * Gets the bankRoutingNumber attribute.
     *
     * @return Returns the bankRoutingNumber
     */
    public String getBankRoutingNumber() {
        return bankRoutingNumber;
    }

    /**
     * Sets the bankRoutingNumber attribute.
     *
     * @param bankRoutingNumber The bankRoutingNumber to set.
     */
    public void setBankRoutingNumber(String bankRoutingNumber) {
        this.bankRoutingNumber = bankRoutingNumber;
    }


    /**
     * Gets the bankOfficeCode attribute.
     *
     * @return Returns the bankOfficeCode
     */
    public String getBankOfficeCode() {
        return bankOfficeCode;
    }

    /**
     * Sets the bankOfficeCode attribute.
     *
     * @param bankOfficeCode The bankOfficeCode to set.
     */
    public void setBankOfficeCode(String bankOfficeCode) {
        this.bankOfficeCode = bankOfficeCode;
    }


    /**
     * Gets the bankServiceNumber attribute.
     *
     * @return Returns the bankServiceNumber
     */
    public String getBankServiceNumber() {
        return bankServiceNumber;
    }

    /**
     * Sets the bankServiceNumber attribute.
     *
     * @param bankServiceNumber The bankServiceNumber to set.
     */
    public void setBankServiceNumber(String bankServiceNumber) {
        this.bankServiceNumber = bankServiceNumber;
    }


    /**
     * Gets the bankTypeCode attribute.
     *
     * @return Returns the bankTypeCode
     */
    public String getBankTypeCode() {
        return bankTypeCode;
    }

    /**
     * Sets the bankTypeCode attribute.
     *
     * @param bankTypeCode The bankTypeCode to set.
     */
    public void setBankTypeCode(String bankTypeCode) {
        this.bankTypeCode = bankTypeCode;
    }


    /**
     * Gets the bankNewRoutingNumber attribute.
     *
     * @return Returns the bankNewRoutingNumber
     */
    public String getBankNewRoutingNumber() {
        return bankNewRoutingNumber;
    }

    /**
     * Sets the bankNewRoutingNumber attribute.
     *
     * @param bankNewRoutingNumber The bankNewRoutingNumber to set.
     */
    public void setBankNewRoutingNumber(String bankNewRoutingNumber) {
        this.bankNewRoutingNumber = bankNewRoutingNumber;
    }


    /**
     * Gets the bankName attribute.
     *
     * @return Returns the bankName
     */
    public String getBankName() {
        return bankName;
    }

    /**
     * Sets the bankName attribute.
     *
     * @param bankName The bankName to set.
     */
    public void setBankName(String bankName) {
        this.bankName = bankName;
    }


    /**
     * Gets the bankStreetAddress attribute.
     *
     * @return Returns the bankStreetAddress
     */
    public String getBankStreetAddress() {
        return bankStreetAddress;
    }

    /**
     * Sets the bankStreetAddress attribute.
     *
     * @param bankStreetAddress The bankStreetAddress to set.
     */
    public void setBankStreetAddress(String bankStreetAddress) {
        this.bankStreetAddress = bankStreetAddress;
    }


    /**
     * Gets the bankCityName attribute.
     *
     * @return Returns the bankCityName
     */
    public String getBankCityName() {
        return bankCityName;
    }

    /**
     * Sets the bankCityName attribute.
     *
     * @param bankCityName The bankCityName to set.
     */
    public void setBankCityName(String bankCityName) {
        this.bankCityName = bankCityName;
    }


    /**
     * Gets the bankStateCode attribute.
     *
     * @return Returns the bankStateCode
     */
    public String getBankStateCode() {
        return bankStateCode;
    }

    /**
     * Sets the bankStateCode attribute.
     *
     * @param bankStateCode The bankStateCode to set.
     */
    public void setBankStateCode(String bankStateCode) {
        this.bankStateCode = bankStateCode;
    }


    /**
     * Gets the bankZipCode attribute.
     *
     * @return Returns the bankZipCode
     */
    public String getBankZipCode() {
        return bankZipCode;
    }

    /**
     * Sets the bankZipCode attribute.
     *
     * @param bankZipCode The bankZipCode to set.
     */
    public void setBankZipCode(String bankZipCode) {
        this.bankZipCode = bankZipCode;
    }

    /**
     * Gets the bankPhoneAreaCode attribute.
     *
     * @return Returns the bankPhoneAreaCode
     */
    public String getBankPhoneAreaCode() {
        return bankPhoneAreaCode;
    }

    /**
     * Sets the bankPhoneAreaCode attribute.
     *
     * @param bankPhoneAreaCode The bankPhoneAreaCode to set.
     */
    public void setBankPhoneAreaCode(String bankPhoneAreaCode) {
        this.bankPhoneAreaCode = bankPhoneAreaCode;
    }


    /**
     * Gets the bankPhonePrefixNumber attribute.
     *
     * @return Returns the bankPhonePrefixNumber
     */
    public String getBankPhonePrefixNumber() {
        return bankPhonePrefixNumber;
    }

    /**
     * Sets the bankPhonePrefixNumber attribute.
     *
     * @param bankPhonePrefixNumber The bankPhonePrefixNumber to set.
     */
    public void setBankPhonePrefixNumber(String bankPhonePrefixNumber) {
        this.bankPhonePrefixNumber = bankPhonePrefixNumber;
    }


    /**
     * Gets the bankPhoneSuffixNumber attribute.
     *
     * @return Returns the bankPhoneSuffixNumber
     */
    public String getBankPhoneSuffixNumber() {
        return bankPhoneSuffixNumber;
    }

    /**
     * Sets the bankPhoneSuffixNumber attribute.
     *
     * @param bankPhoneSuffixNumber The bankPhoneSuffixNumber to set.
     */
    public void setBankPhoneSuffixNumber(String bankPhoneSuffixNumber) {
        this.bankPhoneSuffixNumber = bankPhoneSuffixNumber;
    }


    /**
     * Gets the bankInstitutionStatusCode attribute.
     *
     * @return Returns the bankInstitutionStatusCode
     */
    public String getBankInstitutionStatusCode() {
        return bankInstitutionStatusCode;
    }

    /**
     * Sets the bankInstitutionStatusCode attribute.
     *
     * @param bankInstitutionStatusCode The bankInstitutionStatusCode to set.
     */
    public void setBankInstitutionStatusCode(String bankInstitutionStatusCode) {
        this.bankInstitutionStatusCode = bankInstitutionStatusCode;
    }


    /**
     * Gets the bankDataViewCode attribute.
     *
     * @return Returns the bankDataViewCode
     */
    public String getBankDataViewCode() {
        return bankDataViewCode;
    }

    /**
     * Sets the bankDataViewCode attribute.
     *
     * @param bankDataViewCode The bankDataViewCode to set.
     */
    public void setBankDataViewCode(String bankDataViewCode) {
        this.bankDataViewCode = bankDataViewCode;
    }

    /**
     * Gets the bankState attribute.
     *
     * @return Returns the bankState.
     */
    public StateEbo getBankState() {
        if ( StringUtils.isBlank(bankStateCode) || StringUtils.isBlank(KFSConstants.COUNTRY_CODE_UNITED_STATES ) ) {
            bankState = null;
        } else {
            if ( bankState == null || !StringUtils.equals( bankState.getCode(),bankStateCode) || !StringUtils.equals(bankState.getCountryCode(), KFSConstants.COUNTRY_CODE_UNITED_STATES ) ) {
                ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(StateEbo.class);
                if ( moduleService != null ) {
                    Map<String,Object> keys = new HashMap<String, Object>(2);
                    keys.put(LocationConstants.PrimaryKeyConstants.COUNTRY_CODE, KFSConstants.COUNTRY_CODE_UNITED_STATES);/*RICE20_REFACTORME*/
                    keys.put(LocationConstants.PrimaryKeyConstants.CODE, bankStateCode);
                    bankState = moduleService.getExternalizableBusinessObject(StateEbo.class, keys);
                } else {
                    throw new RuntimeException( "CONFIGURATION ERROR: No responsible module found for EBO class.  Unable to proceed." );
                }
            }
        }
        return bankState;
    }

    /**
     * Gets the postalCode attribute.
     *
     * @return Returns the postalCode.
     */
    public PostalCodeEbo getPostalCode() {
        if ( StringUtils.isBlank(bankZipCode) || StringUtils.isBlank(KFSConstants.COUNTRY_CODE_UNITED_STATES ) ) {
            postalCode = null;
        } else {
            if ( postalCode == null || !StringUtils.equals( postalCode.getCode(),bankZipCode) || !StringUtils.equals(postalCode.getCountryCode(), KFSConstants.COUNTRY_CODE_UNITED_STATES ) ) {
                ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(PostalCodeEbo.class);
                if ( moduleService != null ) {
                    Map<String,Object> keys = new HashMap<String, Object>(2);
                    keys.put(LocationConstants.PrimaryKeyConstants.COUNTRY_CODE, KFSConstants.COUNTRY_CODE_UNITED_STATES);/*RICE20_REFACTORME*/
                    keys.put(LocationConstants.PrimaryKeyConstants.CODE, bankZipCode);
                    postalCode = moduleService.getExternalizableBusinessObject(PostalCodeEbo.class, keys);
                } else {
                    throw new RuntimeException( "CONFIGURATION ERROR: No responsible module found for EBO class.  Unable to proceed." );
                }
            }
        }
        return postalCode;
    }

    /**
     *
     * @see org.kuali.rice.core.api.mo.common.active.MutableInactivatable#isActive()
     */
    @Override
    public boolean isActive() {
        return active;
    }

    /**
     *
     * @see org.kuali.rice.core.api.mo.common.active.MutableInactivatable#setActive(boolean)
     */
    @Override
    public void setActive(boolean active) {
        this.active = active;
    }
}
