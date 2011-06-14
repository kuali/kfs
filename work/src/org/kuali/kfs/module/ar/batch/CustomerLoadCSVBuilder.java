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
package org.kuali.kfs.module.ar.batch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.batch.vo.CustomerAddressCSV;
import org.kuali.kfs.module.ar.batch.vo.CustomerAddressDigesterVO;
import org.kuali.kfs.module.ar.batch.vo.CustomerDigesterVO;

/**
 * CSVBuilder convert the parsed data values into CustomerDigesterVO list, which is validated by CustomerLoad service
 * and docs creation
 * 
 */
public class CustomerLoadCSVBuilder {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerLoadCSVBuilder.class);

    /**
     * Convert the parseData object into CustomerDigesterVO 
     * 
     * @param parseDataList
     * @return
     */
    public static List<CustomerDigesterVO> buildCustomerDigestVO(List<Map<String, String>> parseDataList) {
        List<CustomerDigesterVO> customerVOs = new ArrayList<CustomerDigesterVO>();

        CustomerDigesterVO customer = null, dataCustomer;
        CustomerAddressDigesterVO dataAddress;
        for (Map<String, String> rowDataMap : parseDataList) {

            dataCustomer = buildCustomerFromDataMap(rowDataMap);
            dataAddress = buildAddressFromDataMap(rowDataMap);

            // if it is not a continuing customer with alternate address or it is the first iteration
            if (!isContinuingCustomer(customer, dataCustomer) || customer == null) {
                if (customer != null) {
                    customerVOs.add(customer);
                }
                // instantiate customer as new customer
                customer = dataCustomer;
            }
            // update the address
            customer.addCustomerAddress(dataAddress);
        }
        // add the last customer
        customerVOs.add(customer);

        return customerVOs;
    }

    /**
     * Continuing customer from CSV is defined by customer name field is empty
     * 
     * @param customer
     * @param dataCustomer
     * @return
     */
    private static boolean isContinuingCustomer(CustomerDigesterVO customer, CustomerDigesterVO dataCustomer) {
        return StringUtils.isEmpty(dataCustomer.getCustomerName());
    }

    /**
     * build the CustomerAddressDigesterVO from data row
     * 
     * @param rowDataMap
     * @return
     */
    private static CustomerAddressDigesterVO buildAddressFromDataMap(Map<String, String> rowDataMap) {

        CustomerAddressDigesterVO address = new CustomerAddressDigesterVO();
        address.setCustomerAddressName(rowDataMap.get(CustomerAddressCSV.customerAddressName.name()));
        address.setCustomerLine1StreetAddress(rowDataMap.get(CustomerAddressCSV.customerLine1StreetAddress.name()));
        address.setCustomerLine2StreetAddress(rowDataMap.get(CustomerAddressCSV.customerLine2StreetAddress.name()));
        address.setCustomerCityName(rowDataMap.get(CustomerAddressCSV.customerCityName.name()));
        address.setCustomerStateCode(rowDataMap.get(CustomerAddressCSV.customerStateCode.name()));
        address.setCustomerZipCode(rowDataMap.get(CustomerAddressCSV.customerZipCode.name()));
        address.setCustomerCountryCode(rowDataMap.get(CustomerAddressCSV.customerCountryCode.name()));
        address.setCustomerAddressInternationalProvinceName(rowDataMap.get(CustomerAddressCSV.customerAddressInternationalProvinceName.name()));
        address.setCustomerInternationalMailCode(rowDataMap.get(CustomerAddressCSV.customerInternationalMailCode.name()));
        address.setCustomerEmailAddress(rowDataMap.get(CustomerAddressCSV.customerAddressEmail.name()));
        address.setCustomerAddressTypeCode(rowDataMap.get(CustomerAddressCSV.customerAddressTypeCode.name()));

        return address;
    }

    /**
     * build the CustomerDigesterVO from the data row
     * 
     * @param rowDataMap
     * @return
     */
    private static CustomerDigesterVO buildCustomerFromDataMap(Map<String, String> rowDataMap) {

        CustomerDigesterVO customer = new CustomerDigesterVO();
        customer.setCustomerNumber(rowDataMap.get(CustomerAddressCSV.customerNumber.name()));
        customer.setCustomerName(rowDataMap.get(CustomerAddressCSV.customerName.name()));
        customer.setCustomerParentCompanyNumber(rowDataMap.get(CustomerAddressCSV.customerParentCompanyNumber.name()));
        customer.setCustomerTypeCode(rowDataMap.get(CustomerAddressCSV.customerTypeCode.name()));
        customer.setCustomerLastActivityDate(rowDataMap.get(CustomerAddressCSV.customerLastActivityDate.name()));
        customer.setCustomerTaxTypeCode(rowDataMap.get(CustomerAddressCSV.customerTaxTypeCode.name()));
        customer.setCustomerTaxNbr(rowDataMap.get(CustomerAddressCSV.customerTaxNbr.name()));
        customer.setCustomerActiveIndicator(rowDataMap.get(CustomerAddressCSV.customerActiveIndicator.name()));
        customer.setCustomerPhoneNumber(rowDataMap.get(CustomerAddressCSV.customerPhoneNumber.name()));
        customer.setCustomer800PhoneNumber(rowDataMap.get(CustomerAddressCSV.customer800PhoneNumber.name()));
        customer.setCustomerContactName(rowDataMap.get(CustomerAddressCSV.customerContactName.name()));
        customer.setCustomerContactPhoneNumber(rowDataMap.get(CustomerAddressCSV.customerContactPhoneNumber.name()));
        customer.setCustomerFaxNumber(rowDataMap.get(CustomerAddressCSV.customerFaxNumber.name()));
        customer.setCustomerBirthDate(rowDataMap.get(CustomerAddressCSV.customerBirthDate.name()));
        customer.setCustomerTaxExemptIndicator(rowDataMap.get(CustomerAddressCSV.customerTaxExemptIndicator.name()));
        customer.setCustomerCreditLimitAmount(rowDataMap.get(CustomerAddressCSV.customerCreditLimitAmount.name()));
        customer.setCustomerCreditApprovedByName(rowDataMap.get(CustomerAddressCSV.customerCreditApprovedByName.name()));
        customer.setCustomerEmailAddress(rowDataMap.get(CustomerAddressCSV.customerEmailAddress.name()));
        return customer;
    }

}
