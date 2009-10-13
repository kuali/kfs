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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;

public class CustomerLoadVOGenerator {

    public static final CustomerDigesterVO generateCustomerVO(Map<String, String> customerFields, Map<String, String> address) {
        List<Map<String,String>> addresses = new ArrayList<Map<String,String>>();
        addresses.add(address);
        return generateCustomerVO(customerFields, addresses);
    }
    
    public static final CustomerDigesterVO generateCustomerVO(Map<String, String> customerFields, List<Map<String, String>> addresses) {
        
        CustomerDigesterVO customerVO = new CustomerDigesterVO();
        
        String propertyValue = null;
        for (String propertyName : customerFields.keySet()) {
            propertyValue = customerFields.get(propertyName);
            try {
                PropertyUtils.setSimpleProperty(customerVO, propertyName, propertyValue);
            }
            catch (Exception e) {
                throw new RuntimeException("Exception trying to set property [" + propertyName + "] to value [" + propertyValue + "]", e);
            }
        }
        
        for (Map<String, String> addressFields : addresses) {
            CustomerAddressDigesterVO addressVO = new CustomerAddressDigesterVO();
            for (String propertyName : addressFields.keySet()) {
                propertyValue = addressFields.get(propertyName);
                try {
                    PropertyUtils.setSimpleProperty(addressVO, propertyName, propertyValue);
                }
                catch (Exception e) {
                    throw new RuntimeException("Exception trying to set property [" + propertyName + "] to value [" + propertyValue + "]", e);
                }
            }
            customerVO.getCustomerAddresses().add(addressVO);
        }
        return customerVO;
    }

    public static final Map<String, String> getValidCustomerVO1() {
        Map<String, String> fields = new HashMap<String, String>();
        
        fields.put("customerName", "Sample Customer One");
        fields.put("customerParentCompanyNumber", "");
        fields.put("customerTypeCode", "05");
        fields.put("customerLastActivityDate", "");
        fields.put("customerTaxTypeCode", "FEIN");
        fields.put("customerTaxNbr", "121231234");
        fields.put("customerActiveIndicator", "Y");
        fields.put("customerPhoneNumber", "520-555-1212");
        fields.put("customer800PhoneNumber", "800-555-1212");
        fields.put("customerContactName", "John Doe");
        fields.put("customerContactPhoneNumber", "520-555-1212");
        fields.put("customerFaxNumber", "520-555-1213");
        fields.put("customerBirthDate", "");
        fields.put("customerTaxExemptIndicator", "Y");
        fields.put("customerCreditLimitAmount", "");
        fields.put("customerCreditApprovedByName", "");
        fields.put("customerEmailAddress", "johndoe@samplecustomerone.com");

        return fields;
    }
    
    public static final Map<String, String> getBadCustomerVO_InvalidDates_01() {
        Map<String, String> fields = new HashMap<String, String>();
        
        fields.put("customerName", "Sample Customer One");
        fields.put("customerParentCompanyNumber", "");
        fields.put("customerTypeCode", "05");
        fields.put("customerLastActivityDate", "");
        fields.put("customerTaxTypeCode", "FEIN");
        fields.put("customerTaxNbr", "121231234");
        fields.put("customerActiveIndicator", "Y");
        fields.put("customerPhoneNumber", "520-555-1212");
        fields.put("customer800PhoneNumber", "800-555-1212");
        fields.put("customerContactName", "John Doe");
        fields.put("customerContactPhoneNumber", "520-555-1212");
        fields.put("customerFaxNumber", "520-555-1213");
        fields.put("customerBirthDate", "hello");
        fields.put("customerTaxExemptIndicator", "Y");
        fields.put("customerCreditLimitAmount", "");
        fields.put("customerCreditApprovedByName", "");
        fields.put("customerEmailAddress", "johndoe@samplecustomerone.com");

        return fields;
    }
    
    public static final Map<String, String> getValidCustomerVOTemplate() {
        Map<String, String> fields = new HashMap<String, String>();
        
        fields.put("customerName", "");
        fields.put("customerParentCompanyNumber", "");
        fields.put("customerTypeCode", "");
        fields.put("customerLastActivityDate", "");
        fields.put("customerTaxTypeCode", "");
        fields.put("customerTaxNbr", "");
        fields.put("customerActiveIndicator", "");
        fields.put("customerPhoneNumber", "");
        fields.put("customer800PhoneNumber", "");
        fields.put("customerContactName", "");
        fields.put("customerContactPhoneNumber", "");
        fields.put("customerFaxNumber", "");
        fields.put("customerBirthDate", "");
        fields.put("customerTaxExemptIndicator", "");
        fields.put("customerCreditLimitAmount", "");
        fields.put("customerCreditApprovedByName", "");
        fields.put("customerEmailAddress", "");

        return fields;
    }
    
    public static final Map<String, String> getValidAddressVO1() {
        Map<String, String> fields = new HashMap<String, String>();
        
        fields.put("customerAddressName", "Headquarters");
        fields.put("customerLine1StreetAddress", "1234 N Any St");
        fields.put("customerLine2StreetAddress", "Suite 440");
        fields.put("customerCityName", "Phoenix");
        fields.put("customerStateCode", "AZ");
        fields.put("customerZipCode", "85828");
        fields.put("customerCountryCode", "US");
        fields.put("customerAddressInternationalProvinceName", "");
        fields.put("customerInternationalMailCode", "");
        fields.put("customerEmailAddress", "support@samplecustomerone.com");
        fields.put("customerAddressTypeCode", "P");
        fields.put("customerAddressEndDate", "2099-01-01");

        return fields;
    }

    public static final Map<String, String> getBadAddressVO_InvalidDates_01() {
        Map<String, String> fields = new HashMap<String, String>();
        
        fields.put("customerAddressName", "Headquarters");
        fields.put("customerLine1StreetAddress", "1234 N Any St");
        fields.put("customerLine2StreetAddress", "Suite 440");
        fields.put("customerCityName", "Phoenix");
        fields.put("customerStateCode", "AZ");
        fields.put("customerZipCode", "85828");
        fields.put("customerCountryCode", "US");
        fields.put("customerAddressInternationalProvinceName", "");
        fields.put("customerInternationalMailCode", "");
        fields.put("customerEmailAddress", "support@samplecustomerone.com");
        fields.put("customerAddressTypeCode", "P");
        fields.put("customerAddressEndDate", "notadate");

        return fields;
    }

    public static final Map<String, String> getValidAddressVOTemplate() {
        Map<String, String> fields = new HashMap<String, String>();
        
        fields.put("customerAddressName", "");
        fields.put("customerLine1StreetAddress", "");
        fields.put("customerLine2StreetAddress", "");
        fields.put("customerCityName", "");
        fields.put("customerStateCode", "");
        fields.put("customerZipCode", "");
        fields.put("customerCountryCode", "");
        fields.put("customerAddressInternationalProvinceName", "");
        fields.put("customerInternationalMailCode", "");
        fields.put("customerEmailAddress", "");
        fields.put("customerAddressTypeCode", "");
        fields.put("customerAddressEndDate", "");

        return fields;
    }

}
