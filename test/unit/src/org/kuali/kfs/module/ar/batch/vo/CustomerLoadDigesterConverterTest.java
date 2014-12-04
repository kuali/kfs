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
import java.util.Map;

import org.kuali.kfs.module.ar.batch.report.CustomerLoadBatchErrors;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.businessobject.CustomerAddress;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;

@ConfigureContext
public class CustomerLoadDigesterConverterTest extends KualiTestBase {

    public void testConvertVOToCustomer_ValidBatchFile() {
        
        //  setup a valid VO to test
        List<Map<String,String>> addresses = new ArrayList<Map<String,String>>();
        addresses.add(CustomerLoadVOGenerator.getValidAddressVO1());
        CustomerDigesterVO customerVO = CustomerLoadVOGenerator.generateCustomerVO(CustomerLoadVOGenerator.getValidCustomerVO1(), addresses);
        
        //  shouldnt throw any errors
        boolean errors = false;
        CustomerDigesterAdapter adapter = new CustomerDigesterAdapter();
        CustomerLoadBatchErrors batchErrors = new CustomerLoadBatchErrors();
        Customer customer = null;
        try {
            customer = adapter.convert(customerVO, batchErrors);
        }
        catch (Exception e) {
            errors = true;
        }
        
        //  if any errors were generated, that would count as a fail
        errors |= !batchErrors.isEmpty();
        
        assertFalse("No errors should have occurred on conversion from VO to BO.", errors);
        assertNotNull("Customer object should not be null.", customer);
        
        //  lets make sure the expected fields all got converted
        assertNotNull("Customer name should not be null.", customer.getCustomerName());
        assertNotNull("Parent Company Number should be null.", customer.getCustomerParentCompanyNumber());
        assertNotNull("Customer Type Code should not be null.", customer.getCustomerTypeCode());
        assertNotNull("Customer Last Activity Date should not be null.", customer.getCustomerLastActivityDate());
        assertNotNull("Customer Tax Type Code should not be null.", customer.getCustomerTaxTypeCode());
        assertNotNull("Customer Tax Number should not be null.", customer.getCustomerTaxNbr());
        assertNotNull("Customer Active Indicator should not be null.", customer.isActive());
        assertNotNull("Customer Phone Number should not be null.", customer.getCustomerPhoneNumber());
        assertNotNull("Customer 800 Phone Number should not be null.", customer.getCustomer800PhoneNumber());
        assertNotNull("Customer Contact Name should not be null.", customer.getCustomerContactName());
        assertNotNull("Customer Contact Phone Number should not be null.", customer.getCustomerContactPhoneNumber());
        assertNotNull("Customer Fax Number should not be null.", customer.getCustomerFaxNumber());
        assertNull("Customer Birth Date should be null.", customer.getCustomerBirthDate());
        assertNotNull("Customer Tax Exempt Indicator should not be null.", customer.isCustomerTaxExemptIndicator());
        assertNull("Customer Credit Limit Amount should be null.", customer.getCustomerCreditLimitAmount());
        assertNotNull("Customer Credit Approved By Name should not be null.", customer.getCustomerCreditApprovedByName());
        assertNotNull("Customer Email Address should not be null.", customer.getCustomerEmailAddress());
        
        CustomerAddress address = customer.getCustomerAddresses().get(0);
        
        assertNotNull("The first address should not be null.", address);
        assertNotNull("Customer Address Name should not be null.", address.getCustomerAddressName());
        assertNotNull("Customer Line1 Street Address should not be null.", address.getCustomerLine1StreetAddress());
        assertNotNull("Customer Line2 Street Address should not be null.", address.getCustomerLine2StreetAddress());
        assertNotNull("Customer City Name should not be null.", address.getCustomerCityName());
        assertNotNull("Customer State Code should not be null.", address.getCustomerStateCode());
        assertNotNull("Customer Zip Code should not be null.", address.getCustomerZipCode());
        assertNotNull("Customer Country Code should not be null.", address.getCustomerCountryCode());
        assertNotNull("Customer Address Internaltional Province Name should be null.", address.getCustomerAddressInternationalProvinceName());
        assertEquals("Customer Address International Province Name should be empty string.", "", address.getCustomerAddressInternationalProvinceName());
        assertNotNull("Customer International Mail Code should be null.", address.getCustomerInternationalMailCode());
        assertEquals("Customer International Mail Code should be empty string.", "", address.getCustomerInternationalMailCode());
        assertNotNull("Customer Email Address should not be null.", address.getCustomerEmailAddress());
        assertNotNull("Customer Address Type Code should not be null.", address.getCustomerAddressTypeCode());
        assertNotNull("Customer Address End Date should not be null.", address.getCustomerAddressEndDate());

    }
    
    public void testConvertVOToCustomer_InvalidDates() {
        
        //  setup a valid VO to test
        List<Map<String,String>> addresses = new ArrayList<Map<String,String>>();
        addresses.add(CustomerLoadVOGenerator.getBadAddressVO_InvalidDates_01());
        CustomerDigesterVO customerVO = CustomerLoadVOGenerator.generateCustomerVO(CustomerLoadVOGenerator.getBadCustomerVO_InvalidDates_01(), addresses);
        
        //  shouldnt throw any errors
        boolean exception = false;
        CustomerDigesterAdapter adapter = new CustomerDigesterAdapter();
        CustomerLoadBatchErrors batchErrors = new CustomerLoadBatchErrors();
        Customer customer = null;
        try {
            customer = adapter.convert(customerVO, batchErrors);
        }
        catch (Exception e) {
            exception = true;
        }
        assertFalse("No exceptions should have occurred on conversion from VO to BO.", exception);
        assertNotNull("Customer object should not be null.", customer);
        
        //  if any errors were generated, that would count as a fail
        assertFalse("Batch Errors should not be empty.", batchErrors.isEmpty());
        assertEquals("Should be one companyName with errors.", 1, batchErrors.getCompaniesWithErrors());
        assertEquals("Should be two errors total.", 2, batchErrors.getTotalErrors());
        
        //  the failed date fields should both be null
        assertNull("The failed customer birthDate should be null.", customer.getCustomerBirthDate());
        assertNull("The address should have a null end-date.", customer.getCustomerAddresses().get(0).getCustomerAddressEndDate());
        
    }
    
}
