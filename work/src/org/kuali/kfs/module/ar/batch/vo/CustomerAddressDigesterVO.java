/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.ar.batch.vo;


public class CustomerAddressDigesterVO {

    private String customerNumber;
    private String customerAddressName;
    private String customerLine1StreetAddress;
    private String customerLine2StreetAddress;
    private String customerCityName;
    private String customerStateCode;
    private String customerZipCode;
    private String customerCountryCode;
    private String customerAddressInternationalProvinceName;
    private String customerInternationalMailCode;
    private String customerEmailAddress;
    private String customerAddressTypeCode;
    private String customerAddressEndDate;

    public CustomerAddressDigesterVO() {}

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getCustomerAddressName() {
        return customerAddressName;
    }

    public void setCustomerAddressName(String customerAddressName) {
        this.customerAddressName = customerAddressName;
    }

    public String getCustomerLine1StreetAddress() {
        return customerLine1StreetAddress;
    }

    public void setCustomerLine1StreetAddress(String customerLine1StreetAddress) {
        this.customerLine1StreetAddress = customerLine1StreetAddress;
    }

    public String getCustomerLine2StreetAddress() {
        return customerLine2StreetAddress;
    }

    public void setCustomerLine2StreetAddress(String customerLine2StreetAddress) {
        this.customerLine2StreetAddress = customerLine2StreetAddress;
    }

    public String getCustomerCityName() {
        return customerCityName;
    }

    public void setCustomerCityName(String customerCityName) {
        this.customerCityName = customerCityName;
    }

    public String getCustomerStateCode() {
        return customerStateCode;
    }

    public void setCustomerStateCode(String customerStateCode) {
        this.customerStateCode = customerStateCode;
    }

    public String getCustomerZipCode() {
        return customerZipCode;
    }

    public void setCustomerZipCode(String customerZipCode) {
        this.customerZipCode = customerZipCode;
    }

    public String getCustomerCountryCode() {
        return customerCountryCode;
    }

    public void setCustomerCountryCode(String customerCountryCode) {
        this.customerCountryCode = customerCountryCode;
    }

    public String getCustomerAddressInternationalProvinceName() {
        return customerAddressInternationalProvinceName;
    }

    public void setCustomerAddressInternationalProvinceName(String customerAddressInternationalProvinceName) {
        this.customerAddressInternationalProvinceName = customerAddressInternationalProvinceName;
    }

    public String getCustomerInternationalMailCode() {
        return customerInternationalMailCode;
    }

    public void setCustomerInternationalMailCode(String customerInternationalMailCode) {
        this.customerInternationalMailCode = customerInternationalMailCode;
    }

    public String getCustomerEmailAddress() {
        return customerEmailAddress;
    }

    public void setCustomerEmailAddress(String customerEmailAddress) {
        this.customerEmailAddress = customerEmailAddress;
    }

    public String getCustomerAddressTypeCode() {
        return customerAddressTypeCode;
    }

    public void setCustomerAddressTypeCode(String customerAddressTypeCode) {
        this.customerAddressTypeCode = customerAddressTypeCode;
    }

    public String getCustomerAddressEndDate() {
        return customerAddressEndDate;
    }

    public void setCustomerAddressEndDate(String customerAddressEndDate) {
        this.customerAddressEndDate = customerAddressEndDate;
    }

}
