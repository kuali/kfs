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
package org.kuali.kfs.module.ar.document.validation.impl;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.businessobject.CustomerAddress;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.document.validation.MaintenanceRuleTestBase;

@ConfigureContext(session = khuntley)
public class CustomerRuleTest extends MaintenanceRuleTestBase {

    private Customer customer;
    private CustomerAddress customerAddress;

    private static final String CUSTOMER_ADDRESS_NAME = "Address Name";
    private static final String CUSTOMER_ADDRESS_COUNTRY_CODE_US = "US";
    private static final String CUSTOMER_ADDRESS_COUNTRY_CODE_RO = "RO";
    private static final String CUSTOMER_ADDRESS_STATE_CODE = "NY";
    private static final String CUSTOMER_ADDRESS_ZIP_CODE = "14850";
    private static final String CUSTOMER_ADDRESS_PROVINCE = "Iasi";

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        customer = new Customer();
        customerAddress = new CustomerAddress();
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        customer = null;
        customerAddress = null;
        super.tearDown();
    }

    /**
     * This method checks if checkCustomerHasAddress returns true when customer has an address.
     */
    public void testCheckCustomerHasAddress_True() {
        customer.getCustomerAddresses().add(customerAddress);
        CustomerRule rule = (CustomerRule) setupMaintDocRule(newMaintDoc(customer), CustomerRule.class);
        boolean result = rule.checkCustomerHasAddress(customer);
        assertEquals("When customer has an address checkCustomerHasAddress should return true. ", true, result);

    }

    /**
     * This method checks if checkCustomerHasAddress returns false when customer does not have an address.
     */
    public void testCheckCustomerHasAddress_False() {
        customer.getCustomerAddresses().clear();
        CustomerRule rule = (CustomerRule) setupMaintDocRule(newMaintDoc(customer), CustomerRule.class);
        boolean result = rule.checkCustomerHasAddress(customer);
        assertEquals("When customer does not have an address checkCustomerHasAddress should return false. ", false, result);
    }

    /**
     * This method if checkAddressIsValid returns true when country code is US and state code and zip code are set.
     */
    public void testCheckAddressIsValid_CountryUS_True() {
        customerAddress.setCustomerAddressName(CUSTOMER_ADDRESS_NAME);
        customerAddress.setCustomerCountryCode(CUSTOMER_ADDRESS_COUNTRY_CODE_US);
        customerAddress.setCustomerStateCode(CUSTOMER_ADDRESS_STATE_CODE);
        customerAddress.setCustomerZipCode(CUSTOMER_ADDRESS_ZIP_CODE);

        CustomerRule rule = (CustomerRule) setupMaintDocRule(newMaintDoc(customer), CustomerRule.class);
        boolean result = rule.checkAddressIsValid(customerAddress);
        assertEquals("When customer address has country code " + CUSTOMER_ADDRESS_COUNTRY_CODE_US + " and state code and zip code are not empty checkAddressIsValid should return true. ", true, result);
    }

    /**
     * This method if checkAddressIsValid returns false when country code is US and state code and zip code are empty
     */
    public void testCheckAddressIsValid_CountryUS_False() {
        customerAddress.setCustomerAddressName(CUSTOMER_ADDRESS_NAME);
        customerAddress.setCustomerCountryCode(CUSTOMER_ADDRESS_COUNTRY_CODE_US);
        customerAddress.setCustomerStateCode("");
        customerAddress.setCustomerZipCode("");

        CustomerRule rule = (CustomerRule) setupMaintDocRule(newMaintDoc(customer), CustomerRule.class);
        boolean result = rule.checkAddressIsValid(customerAddress);
        assertEquals("When customer address has country code " + CUSTOMER_ADDRESS_COUNTRY_CODE_US + " and state code and zip code are empty checkAddressIsValid should return false. ", false, result);
    }

    /**
     * This method checks that checkAddressIsValid returns true when country code is not US and InternationalProvinceName and InternationalMailCode are set.
     */
//    public void testCheckAddressIsValid_CountryNonUS_True() {
//        customerAddress.setCustomerAddressName(CUSTOMER_ADDRESS_NAME);
//        customerAddress.setCustomerCountryCode(CUSTOMER_ADDRESS_COUNTRY_CODE_RO);
//        customerAddress.setCustomerAddressInternationalProvinceName(CUSTOMER_ADDRESS_PROVINCE);
//        customerAddress.setCustomerZipCode(CUSTOMER_ADDRESS_ZIP_CODE);
//
//        CustomerRule rule = (CustomerRule) setupMaintDocRule(newMaintDoc(customer), CustomerRule.class);
//        boolean result = rule.checkAddressIsValid(customerAddress);
//        assertEquals("When customer address has country code " + CUSTOMER_ADDRESS_COUNTRY_CODE_RO + " and province and International Mail Code are not empty checkAddressIsValid should return true. ", true, result);
//    }

    /**
     * This method checks that checkAddressIsValid returns false when country code is not US and InternationalProvinceName and InternationalMailCode are not set.
     */
    public void testCheckAddressIsValid_CountryNonUS_False() {
        customerAddress.setCustomerAddressName(CUSTOMER_ADDRESS_NAME);
        customerAddress.setCustomerCountryCode(CUSTOMER_ADDRESS_COUNTRY_CODE_RO);
        customerAddress.setCustomerAddressInternationalProvinceName("");
        customerAddress.setCustomerZipCode("");

        CustomerRule rule = (CustomerRule) setupMaintDocRule(newMaintDoc(customer), CustomerRule.class);
        boolean result = rule.checkAddressIsValid(customerAddress);
        assertEquals("When customer address has country code " + CUSTOMER_ADDRESS_COUNTRY_CODE_RO + " and province and International Mail Code are empty checkAddressIsValid should return false. ", false, result);
    }
    
    /**
     * This method checks if checkAddresses returns true when customer has only one primary address.
     */
    public void testCheckAddresses_True()
    {
        customerAddress.setCustomerAddressTypeCode(ArKeyConstants.CustomerConstants.CUSTOMER_ADDRESS_TYPE_CODE_PRIMARY);
        customer.getCustomerAddresses().add(customerAddress);


        CustomerRule rule = (CustomerRule) setupMaintDocRule(newMaintDoc(customer), CustomerRule.class);
        boolean result = rule.checkAddresses(customer);
        assertEquals("When customer has one primary address checkAddresses should return true. ", true, result);
    }
    
    /**
     * This method checks if checkAddresses returns false when customer has more than one primary address.
     */
    public void testCheckAddresses_HasMore_False()
    {
        customerAddress.setCustomerAddressTypeCode(ArKeyConstants.CustomerConstants.CUSTOMER_ADDRESS_TYPE_CODE_PRIMARY);
        customer.getCustomerAddresses().add(customerAddress);
        customer.getCustomerAddresses().add(customerAddress);

        CustomerRule rule = (CustomerRule) setupMaintDocRule(newMaintDoc(customer), CustomerRule.class);
        boolean result = rule.checkAddresses(customer);
        assertEquals("When customer has more than one primary address checkAddresses should return false. ", false, result);
    }
    
    /**
     * This method checks if checkAddresses returns false when customer has no primary address.
     */
    public void testCheckAddresses_HasNone_False()
    {
        customerAddress.setCustomerAddressTypeCode(ArKeyConstants.CustomerConstants.CUSTOMER_ADDRESS_TYPE_CODE_ALTERNATE);
        customer.getCustomerAddresses().add(customerAddress);

        CustomerRule rule = (CustomerRule) setupMaintDocRule(newMaintDoc(customer), CustomerRule.class);
        boolean result = rule.checkAddresses(customer);
        assertEquals("When customer has no primary address checkAddresses should return false. ", false, result);
    }

}

