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
package org.kuali.kfs.module.ar.fixture;

import java.sql.Date;

import org.kuali.kfs.module.ar.businessobject.CustomerAddress;

/**
 * Fixture class for InvoiceAddressDetail
 */
public enum CustomerAddressFixture {
    CUSTOMER_ADDRESS_VALID_US_PRIMARY1("1", 1, "primary US address", "123 Main St", null, "Ithaca", "NY", "14850", "US", null, null, "test@kuali.org", "P", null),
    CUSTOMER_ADDRESS_VALID_US_ALTERNATE1("1", 2, "alternate US address", "123 Hill Ave", null, "Ithaca", "NY", "14853", "US", null, null, "test@kuali.org", "A", null),
    CUSTOMER_ADDRESS_VALID_FOREIGN_PRIMARY1("2", 1, "primary foreign address", "123 FOREIGN ADDRESS", null, "FOREIGNCITY", null, null, "AS", "INT'LPROVINCE", "INT'L12345", "test@kuali.org", "P", null),
    CUSTOMER_ADDRESS_VALID_FOREIGN_ALTERNATE1("2", 2, "alternate foreign address", "456 FOREIGN ADDRESS", null, "BALI", null, null, "AS", "PROVINCE", "L12345", "test@kuali.org", "A", null),
    CUSTOMER_ADDRESS_INVALID_US_PRIMARY1("1", 1, "primary US address", "123 Main St", null, "Ithaca", null, null, "US", null, null, "test@kuali.org", "P", null),
    CUSTOMER_ADDRESS_INVALID_US_ALTERNATE1("1", 2, "alternate US address", "123 Hill Ave", null, "Ithaca", null, null, "US", null, null, "test@kuali.org", "A", null),
    CUSTOMER_ADDRESS_INVALID_FOREIGN_PRIMARY1("2", 1, "primary foreign address", "123 FOREIGN ADDRESS", null, "FOREIGNCITY", null, null, "AS", null, null, "test@kuali.org", "P", null),
    CUSTOMER_ADDRESS_INVALID_FOREIGN_ALTERNATE1("2", 2, "alternate foreign address", "456 FOREIGN ADDRESS", null, "BALI", null, null, "AS", null, null, "test@kuali.org", "A", null);

    private String customerNumber;
    private Integer customerAddressIdentifier;
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
    private Date customerAddressEndDate;

    private CustomerAddressFixture(String customerNumber, Integer customerAddressIdentifier, String customerAddressName, String customerLine1StreetAddress, String customerLine2StreetAddress, String customerCityName, String customerStateCode, String customerZipCode, String customerCountryCode, String customerAddressInternationalProvinceName, String customerInternationalMailCode, String customerEmailAddress, String customerAddressTypeCode, Date customerAddressEndDate) {
        this.customerNumber = customerNumber;
        this.customerAddressIdentifier = customerAddressIdentifier;
        this.customerAddressName = customerAddressName;
        this.customerLine1StreetAddress = customerLine1StreetAddress;
        this.customerLine2StreetAddress = customerLine2StreetAddress;
        this.customerCityName = customerCityName;
        this.customerStateCode = customerStateCode;
        this.customerZipCode = customerZipCode;
        this.customerCountryCode = customerCountryCode;
        this.customerAddressInternationalProvinceName = customerAddressInternationalProvinceName;
        this.customerInternationalMailCode = customerInternationalMailCode;
        this.customerEmailAddress = customerEmailAddress;
        this.customerAddressTypeCode = customerAddressTypeCode;
        this.customerAddressEndDate = customerAddressEndDate;
    }

    public CustomerAddress createCustomerAddress() {
        CustomerAddress customerAddress = new CustomerAddress();

        customerAddress.setCustomerNumber(customerNumber);
        customerAddress.setCustomerAddressIdentifier(customerAddressIdentifier);
        customerAddress.setCustomerAddressName(customerAddressName);
        customerAddress.setCustomerLine1StreetAddress(customerLine1StreetAddress);
        customerAddress.setCustomerLine2StreetAddress(customerLine2StreetAddress);
        customerAddress.setCustomerCityName(customerCityName);
        customerAddress.setCustomerStateCode(customerStateCode);
        customerAddress.setCustomerZipCode(customerZipCode);
        customerAddress.setCustomerCountryCode(customerCountryCode);
        customerAddress.setCustomerAddressInternationalProvinceName(customerAddressInternationalProvinceName);
        customerAddress.setCustomerInternationalMailCode(customerInternationalMailCode);
        customerAddress.setCustomerEmailAddress(customerEmailAddress);
        customerAddress.setCustomerAddressTypeCode(customerAddressTypeCode);
        customerAddress.setCustomerAddressEndDate(customerAddressEndDate);

        return customerAddress;
    }
}
