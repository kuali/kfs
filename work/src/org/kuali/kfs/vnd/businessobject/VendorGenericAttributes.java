/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.vendor.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;


/**
 * Attribute Reference Dummy Business Object for Vendor attributes
 */
public class VendorGenericAttributes extends PersistableBusinessObjectBase {

    private String line1Address;
    private String line2Address;
    private String cityName;
    private String zipCode;
    private String stateCode;
    private String countryCode;
    private String internationalProvinceName;
    private String attentionName;
    private String campusCode;
    private String yesNoWithBlankIndicator;
    private String yesNoWithoutBlankIndicator;
    private boolean hiddenIndicator;
    private String phoneNumberNoValidation;
    private String phoneNumberWithValidation;
    private String genericUrlAddress;
    private String vendorHeaderGeneratedIdentifier; // " sourceClassName="org.kuali.module.vendor.bo.VendorDetail"
                                                    // sourceAttributeName="vendorNumber"/>
    private String vendorDetailAssignedIdentifier; // " sourceClassName="org.kuali.module.vendor.bo.VendorDetail"
                                                    // sourceAttributeName="vendorNumber"/>

    /**
     * Constructs a VendorAttributeReferenceDummy.
     */
    public VendorGenericAttributes() {
    }

    /*
     * WORKFLOW LABEL ATTRIBUTES BEGIN HERE
     */

    public String getVendorDetailAssignedIdentifier() {

        return vendorDetailAssignedIdentifier;
    }

    public void setVendorDetailAssignedIdentifier(String vendorDetailAssignedIdentifier) {
        this.vendorDetailAssignedIdentifier = vendorDetailAssignedIdentifier;
    }

    public String getVendorHeaderGeneratedIdentifier() {

        return vendorHeaderGeneratedIdentifier;
    }

    public void setVendorHeaderGeneratedIdentifier(String vendorHeaderGeneratedIdentifier) {
        this.vendorHeaderGeneratedIdentifier = vendorHeaderGeneratedIdentifier;
    }

    /*
     * WORKFLOW LABEL ATTRIBUTES END HERE
     */

    public String getCampusCode() {

        return campusCode;
    }

    public void setCampusCode(String campusCode) {
        this.campusCode = campusCode;
    }

    public boolean isHiddenIndicator() {

        return hiddenIndicator;
    }

    public void setHiddenIndicator(boolean hiddenIndicator) {
        this.hiddenIndicator = hiddenIndicator;
    }

    public String getInternationalProvinceName() {

        return internationalProvinceName;
    }

    public void setInternationalProvinceName(String internationalProvinceName) {
        this.internationalProvinceName = internationalProvinceName;
    }

    public String getAttentionName() {

        return attentionName;
    }

    public void setAttentionName(String attentionName) {
        this.attentionName = attentionName;
    }

    public String getCityName() {

        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getCountryCode() {

        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getLine1Address() {

        return line1Address;
    }

    public void setLine1Address(String line1Address) {
        this.line1Address = line1Address;
    }

    public String getLine2Address() {

        return line2Address;
    }

    public void setLine2Address(String line2Address) {
        this.line2Address = line2Address;
    }

    public String getZipCode() {

        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getStateCode() {

        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public String getYesNoWithBlankIndicator() {

        return yesNoWithBlankIndicator;
    }

    public void setYesNoWithBlankIndicator(String yesNoWithBlankIndicator) {
        this.yesNoWithBlankIndicator = yesNoWithBlankIndicator;
    }

    public String getYesNoWithoutBlankIndicator() {

        return yesNoWithoutBlankIndicator;
    }

    public void setYesNoWithoutBlankIndicator(String yesNoWithoutBlankIndicator) {
        this.yesNoWithoutBlankIndicator = yesNoWithoutBlankIndicator;
    }

    public String getPhoneNumberNoValidation() {

        return phoneNumberNoValidation;
    }

    public void setPhoneNumberNoValidation(String phoneNumberNoValidation) {
        this.phoneNumberNoValidation = phoneNumberNoValidation;
    }

    public String getPhoneNumberWithValidation() {

        return phoneNumberWithValidation;
    }

    public void setPhoneNumberWithValidation(String phoneNumberWithValidation) {
        this.phoneNumberWithValidation = phoneNumberWithValidation;
    }

    public String getGenericUrlAddress() {

        return genericUrlAddress;
    }

    public void setGenericUrlAddress(String genericUrlAddress) {
        this.genericUrlAddress = genericUrlAddress;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put("hashCode", Integer.toHexString(hashCode()));

        return m;
    }

}