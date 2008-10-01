/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.batch.rule;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.batch.CustomerLoadBatchErrors;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.businessobject.CustomerAddress;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.MaintenanceDocumentDictionaryService;
import org.kuali.rice.kns.util.KualiDecimal;

/**
 * 
 * This class is used to validate a Customer object that has been created from a 
 * batch import, agains the DataDictionary rules, including both the BO DataDictionary 
 * rules and the MaintenanceDocument DataDictionary rules.
 * 
 */
public class CustomerLoadDDValidator {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerLoadDDValidator.class);

    private static final Class<Customer> BO_CLASS = Customer.class;
    private static final String DD_ENTRY_NAME = BO_CLASS.getName();
    
    private MaintenanceDocumentDictionaryService maintDocDDService;
    private DataDictionaryService ddService;

    private Customer customer;
    private String customerName;
    private CustomerLoadBatchErrors batchErrors;
    
    /**
     * 
     * Default constructor, requires no parameters.
     */
    public CustomerLoadDDValidator() {
        if (ddService == null) ddService = SpringContext.getBean(DataDictionaryService.class);
        if (maintDocDDService == null) maintDocDDService = SpringContext.getBean(MaintenanceDocumentDictionaryService.class);
    }
    
    /**
     * 
     * This method applies the DataDictionary validations against the passed in Customer
     * object.  
     * 
     * Any errors encountered will be added to the CustomerLoadBatchErrors parameter.
     * 
     * True will be returned if no errors or validation failures were encountered, otherwise 
     * False will be returned.
     * 
     * @param customer Non-null, populated Customer instance to be validated.
     * @param batchErrors Non-null, empty or populated CustomerLoadBatchErrors instance that will have 
     *        errors or failures added to it when/if encountered.
     * @return True if no errors are encountered, False if any are encountered.
     */
    public boolean validate(Customer customer, CustomerLoadBatchErrors batchErrors) {
        if (maintDocDDService == null || ddService == null) {
            throw new IllegalArgumentException("One or both services have not been injected.");
        }
        if (customer == null) {
            throw new IllegalArgumentException("Customer parameters passed in must be non-null.");
        }
        if (batchErrors == null) {
            throw new IllegalArgumentException("BatchErrors parameters passed in must be non-null.");
        }
        this.customer = customer;
        this.customerName = customer.getCustomerName();
        this.batchErrors = batchErrors;
        
        return doValidation();
    }
    
    private boolean doValidation() {
        boolean result = true;
        
        result &= applyDDToField("customerNumber", String.class, customer.getCustomerNumber());
        result &= applyDDToField("customerName", String.class, customer.getCustomerName());
        result &= applyDDToField("customerParentCompanyNumber", String.class, customer.getCustomerParentCompanyNumber());
        result &= applyDDToField("customerTypeCode", String.class, customer.getCustomerTypeCode());
        result &= applyDDToField("customerAddressChangeDate", java.sql.Date.class, customer.getCustomerAddressChangeDate());
        result &= applyDDToField("customerRecordAddDate", java.sql.Date.class, customer.getCustomerRecordAddDate());
        result &= applyDDToField("customerLastActivityDate", java.sql.Date.class, customer.getCustomerLastActivityDate());
        result &= applyDDToField("customerTaxTypeCode", String.class, customer.getCustomerTaxTypeCode());
        result &= applyDDToField("customerTaxNbr", String.class, customer.getCustomerTaxNbr());
        result &= applyDDToField("customerActiveIndicator", boolean.class, customer.isCustomerActiveIndicator());
        result &= applyDDToField("customerPhoneNumber", String.class, customer.getCustomerPhoneNumber());
        result &= applyDDToField("customer800PhoneNumber", String.class, customer.getCustomer800PhoneNumber());
        result &= applyDDToField("customerContactName", String.class, customer.getCustomerContactName());
        result &= applyDDToField("customerContactPhoneNumber", String.class, customer.getCustomerContactPhoneNumber());
        result &= applyDDToField("customerFaxNumber", String.class, customer.getCustomerFaxNumber());
        result &= applyDDToField("customerBirthDate", java.sql.Date.class, customer.getCustomerBirthDate());
        result &= applyDDToField("customerTaxExemptIndicator", boolean.class, customer.isCustomerTaxExemptIndicator());
        result &= applyDDToField("customerCreditLimitAmount", KualiDecimal.class, customer.getCustomerCreditLimitAmount());
        result &= applyDDToField("customerCreditApprovedByName", String.class, customer.getCustomerCreditApprovedByName());
        result &= applyDDToField("customerEmailAddress", String.class, customer.getCustomerEmailAddress());

        for (CustomerAddress address : customer.getCustomerAddresses()) {
            result &= applyDDToField("customerAddressName", String.class, address.getCustomerAddressName());
            result &= applyDDToField("customerLine1StreetAddress", String.class, address.getCustomerLine1StreetAddress());
            result &= applyDDToField("customerLine2StreetAddress", String.class, address.getCustomerLine2StreetAddress());
            result &= applyDDToField("customerCityName", String.class, address.getCustomerCityName());
            result &= applyDDToField("customerStateCode", String.class, address.getCustomerStateCode());
            result &= applyDDToField("customerZipCode", String.class, address.getCustomerZipCode());
            result &= applyDDToField("customerCountryCode", String.class, address.getCustomerCountryCode());
            result &= applyDDToField("customerAddressInternationalProvinceName", String.class, address.getCustomerAddressInternationalProvinceName());
            result &= applyDDToField("customerInternationalMailCode", String.class, address.getCustomerInternationalMailCode());
            result &= applyDDToField("customerEmailAddress", String.class, address.getCustomerEmailAddress());
            result &= applyDDToField("customerAddressTypeCode", String.class, address.getCustomerAddressTypeCode());
            result &= applyDDToField("customerAddressEndDate", java.sql.Date.class, address.getCustomerAddressEndDate());
        }
        
        return result;
    }
    
    private boolean applyDDToField(String propertyName, Class<?> targetClass, Object value) {
        
        boolean result = true;
        
        //  normalize string values to null if empty/blank
        String incomingValue;
        if (value == null) {
            incomingValue = null;
        }
        else if (value instanceof String) {
            incomingValue = StringUtils.trimToNull((String)value);
        }
        else {
            incomingValue = value.toString();
        }
        
        //  fail if blank, no default, and field is required to have a value
        boolean isRequired = ddService.isAttributeRequired(BO_CLASS, propertyName);
        if (incomingValue == null && isRequired) {
            LOG.error("Field [" + propertyName + "] is required, but no value was passed in.");
            addError(propertyName, targetClass, incomingValue, 
                    "Field was required, but no value supplied, and no default value available.");
            result &= false;
        }

        //  validate for min/max lengths
        Integer maxLength = ddService.getAttributeMaxLength(BO_CLASS, propertyName);
        if (maxLength != null && maxLength.intValue() > 0) {
            if (incomingValue.length() > maxLength.intValue()) {
                LOG.error("Field [" + propertyName + "] was " + incomingValue.length() + " digits long, but is only allowed a max length of " + maxLength.toString() + ".");
                addError(propertyName, targetClass, incomingValue, 
                        "Field was required, but no value supplied, and no default value available.");
                result &= false;
            }
        }

        //  validate against a pattern
        Pattern validatingPattern = ddService.getAttributeValidatingExpression(BO_CLASS, propertyName);
        if (!Pattern.matches(validatingPattern.pattern(), incomingValue)) {
            LOG.error("Field [" + propertyName + "] was " + incomingValue.length() + " digits long, but is only allowed a max length of " + maxLength.toString() + ".");
            addError(propertyName, targetClass, incomingValue, 
                    "Field was required, but no value supplied, and no default value available.");
            result &= false;
        }

        //  key values finder
        Class<? extends KeyValuesFinder> valueFinderClass = ddService.getAttributeValuesFinderClass(BO_CLASS, propertyName);
        KeyValuesFinder keyValuesFinder = null;
        if (valueFinderClass != null) {
            try {
                keyValuesFinder = valueFinderClass.newInstance();
            }
            catch (InstantiationException e) {
                throw new RuntimeException("InstantiationException while trying to create a KeyValuesFinder from DD.", e);
            }
            catch (IllegalAccessException e) {
                throw new RuntimeException("IllegalAccessException while trying to create a KeyValuesFinder from DD.", e);
            }
        }
        if (!keyValuesFinder.getKeyValues().contains(incomingValue)) {
            LOG.error("Field [" + propertyName + "] value '" + incomingValue + "' wasnt valid according to KeyValuesFinder [" + valueFinderClass.toString() + "].");
            addError(propertyName, targetClass, incomingValue, 
                    "Field value wasnt one of the allowed values [" + keyValuesFinder.getKeyValues() + "].");
            result &= false;
        }
        
        return result;
    }

    private void addError(String propertyName, Class<?> propertyClass, String origValue, String description) {
        batchErrors.addError(customerName, propertyName, propertyClass, origValue, description);
    }

    public void setMaintDocDDService(MaintenanceDocumentDictionaryService maintDocDDService) {
        this.maintDocDDService = maintDocDDService;
    }

    public void setDdService(DataDictionaryService ddService) {
        this.ddService = ddService;
    }
    
}
