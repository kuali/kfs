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
package org.kuali.kfs.module.ar.batch.vo;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class CustomerDigesterVO {

    private String customerNumber;
    private String customerName;
    private String customerParentCompanyNumber;
    private String customerTypeCode;
    private String customerLastActivityDate;
    private String customerTaxTypeCode;
    private String customerTaxNbr;
    private String customerActiveIndicator;
    private String customerPhoneNumber;
    private String customer800PhoneNumber;
    private String customerContactName;
    private String customerContactPhoneNumber;
    private String customerFaxNumber;
    private String customerBirthDate;
    private String customerTaxExemptIndicator;
    private String customerCreditLimitAmount;
    private String customerCreditApprovedByName;
    private String customerEmailAddress;

    private List<CustomerAddressDigesterVO> customerAddresses;

    public CustomerDigesterVO() {
        customerAddresses = new ArrayList<CustomerAddressDigesterVO>();
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerParentCompanyNumber() {
        return customerParentCompanyNumber;
    }

    public void setCustomerParentCompanyNumber(String customerParentCompanyNumber) {
        this.customerParentCompanyNumber = customerParentCompanyNumber;
    }

    public String getCustomerTypeCode() {
        return customerTypeCode;
    }

    public void setCustomerTypeCode(String customerTypeCode) {
        this.customerTypeCode = customerTypeCode;
    }

    public String getCustomerLastActivityDate() {
        return customerLastActivityDate;
    }

    public void setCustomerLastActivityDate(String customerLastActivityDate) {
        this.customerLastActivityDate = customerLastActivityDate;
    }

    public String getCustomerTaxTypeCode() {
        return customerTaxTypeCode;
    }

    public void setCustomerTaxTypeCode(String customerTaxTypeCode) {
        this.customerTaxTypeCode = customerTaxTypeCode;
    }

    public String getCustomerTaxNbr() {
        return customerTaxNbr;
    }

    public void setCustomerTaxNbr(String customerTaxNbr) {
        this.customerTaxNbr = customerTaxNbr;
    }

    public String getCustomerActiveIndicator() {
        return customerActiveIndicator;
    }

    public void setCustomerActiveIndicator(String customerActiveIndicator) {
        this.customerActiveIndicator = customerActiveIndicator;
    }

    public String getCustomerPhoneNumber() {
        return customerPhoneNumber;
    }

    public void setCustomerPhoneNumber(String customerPhoneNumber) {
        this.customerPhoneNumber = customerPhoneNumber;
    }

    public String getCustomer800PhoneNumber() {
        return customer800PhoneNumber;
    }

    public void setCustomer800PhoneNumber(String customer800PhoneNumber) {
        this.customer800PhoneNumber = customer800PhoneNumber;
    }

    public String getCustomerContactName() {
        return customerContactName;
    }

    public void setCustomerContactName(String customerContactName) {
        this.customerContactName = customerContactName;
    }

    public String getCustomerContactPhoneNumber() {
        return customerContactPhoneNumber;
    }

    public void setCustomerContactPhoneNumber(String customerContactPhoneNumber) {
        this.customerContactPhoneNumber = customerContactPhoneNumber;
    }

    public String getCustomerFaxNumber() {
        return customerFaxNumber;
    }

    public void setCustomerFaxNumber(String customerFaxNumber) {
        this.customerFaxNumber = customerFaxNumber;
    }

    public String getCustomerBirthDate() {
        return customerBirthDate;
    }

    public void setCustomerBirthDate(String customerBirthDate) {
        this.customerBirthDate = customerBirthDate;
    }

    public String getCustomerTaxExemptIndicator() {
        return customerTaxExemptIndicator;
    }

    public void setCustomerTaxExemptIndicator(String customerTaxExemptIndicator) {
        this.customerTaxExemptIndicator = customerTaxExemptIndicator;
    }

    public String getCustomerCreditLimitAmount() {
        return customerCreditLimitAmount;
    }

    public void setCustomerCreditLimitAmount(String customerCreditLimitAmount) {
        this.customerCreditLimitAmount = customerCreditLimitAmount;
    }

    public String getCustomerCreditApprovedByName() {
        return customerCreditApprovedByName;
    }

    public void setCustomerCreditApprovedByName(String customerCreditApprovedByName) {
        this.customerCreditApprovedByName = customerCreditApprovedByName;
    }

    public String getCustomerEmailAddress() {
        return customerEmailAddress;
    }

    public void setCustomerEmailAddress(String customerEmailAddress) {
        this.customerEmailAddress = customerEmailAddress;
    }

    public List<CustomerAddressDigesterVO> getCustomerAddresses() {
        return customerAddresses;
    }

    public void setCustomerAddresses(List<CustomerAddressDigesterVO> customerAddresses) {
        this.customerAddresses = customerAddresses;
    }

    /**
     * This is a convenience method that adds a populated CustomerAddress object directly 
     * to the contained ArrayList.
     * 
     * It's primarily used by the Customer Load batch process, for each of XML batch file 
     * digesting, though it can be used generally.
     * 
     * NOTE that it will attempt to wire the parent/child relationship by setting the 
     * customerAddress.customerNumber to the customerNumber of 'this', if the number isnt 
     * already set.
     * 
     * @param customerAddress
     */
    public void addCustomerAddress(CustomerAddressDigesterVO customerAddress) {
        //  do nothing if passed-in customerAddress is null
        if (customerAddress == null) {
            return;
        }
        //  wire the CustomerNumber to ensure a valid parent/child relationship
        if (StringUtils.isBlank(customerAddress.getCustomerNumber())) {
            if (StringUtils.isNotBlank(this.customerNumber)) {
                customerAddress.setCustomerNumber(this.customerNumber);
            }
        }
        this.customerAddresses.add(customerAddress);
    }
    
}
