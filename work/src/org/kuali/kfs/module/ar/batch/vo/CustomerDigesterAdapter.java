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

import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.converters.SqlDateConverter;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.batch.report.CustomerLoadBatchErrors;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.businessobject.CustomerAddress;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kns.service.MaintenanceDocumentDictionaryService;

/**
 * 
 * This class converts a CustomerDigesterVO object to a standard 
 * Customer object.
 */
public class CustomerDigesterAdapter {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerDigesterAdapter.class);

    private static final Class<Customer> BO_CLASS = Customer.class;
    private static final String DD_ENTRY_NAME = BO_CLASS.getName();
    
    private DateTimeService dateTimeService;
    private MaintenanceDocumentDictionaryService maintDocDDService;
    
    private Customer customer;
    private String customerName;
    private CustomerLoadBatchErrors errors;
    private CustomerDigesterVO customerDigesterVO;
    
    public CustomerDigesterAdapter() {
        if (dateTimeService == null) dateTimeService = SpringContext.getBean(DateTimeService.class);
        if (maintDocDDService == null) maintDocDDService = SpringContext.getBean(MaintenanceDocumentDictionaryService.class);
    }
    
    /**
     * 
     * Converts a CustomerDigesterVO to a real Customer BO.  Tries to do intelligent type 
     * conversions where the types arent Strings.  
     * 
     * NOTE that conversion exceptions will be swallowed! and converted to errors in the 
     * parameter errorMap.  
     * 
     * @param customerDigesterVO The VO full of String values to convert from.
     * @param errorMap An empty MessageMap collection to add errors to.  Only new errors will be added.
     * @return A populated Customer object, from the VO.
     */
    public Customer convert(CustomerDigesterVO customerDigesterVO, CustomerLoadBatchErrors errors) {
        
        if (customerDigesterVO == null) {
            throw new IllegalArgumentException("Parameter customerDigesterVO may not be null.");
        }
        this.customerDigesterVO = customerDigesterVO;
        
        //  the whole error system is keyed of customerName, so if we dont get one of those, we cant even proceed.
        if (StringUtils.isBlank(customerDigesterVO.getCustomerName())) {
            LOG.error("CustomerName can never be empty-string or null.");
            addError("customerName", String.class, customerDigesterVO.getCustomerName(), "CustomerName can never be empty-string or null.");
            return null;
        }
        
        customer = new Customer();
        customerName = this.customerDigesterVO.getCustomerName();
        
        if (errors == null) {
            LOG.error("Passed in CustomerLoadBatchErrors must not be null.");
            throw new IllegalArgumentException("Passed in CustomerLoadBatchErrors must not be null.");
        }
        this.errors = errors;
        
        convertCustomerStringProperties();
        convertCustomerDateProperties();
        convertCustomerKualiDecimalProperties();
        convertCustomerBooleanProperties();
        convertCustomerAddresses();
        
        return customer;
    }
    
    private void convertCustomerStringProperties() {
        
        //  these are String to String conversions, so they will always work
        customer.setCustomerNumber(applyDefaultValue("customerNumber", customerDigesterVO.getCustomerNumber()));
        customer.setCustomerName(applyDefaultValue("customerName", customerDigesterVO.getCustomerName()));
        customer.setCustomerParentCompanyNumber(applyDefaultValue("customerParentCompanyNumber", customerDigesterVO.getCustomerParentCompanyNumber()));
        customer.setCustomerTypeCode(applyDefaultValue("customerTypeCode", customerDigesterVO.getCustomerTypeCode()));
        customer.setCustomerTaxTypeCode(applyDefaultValue("customerTaxTypeCode", customerDigesterVO.getCustomerTaxTypeCode()));
        customer.setCustomerTaxNbr(applyDefaultValue("customerTaxNbr", customerDigesterVO.getCustomerTaxNbr()));
        customer.setCustomerPhoneNumber(applyDefaultValue("customerPhoneNumber", customerDigesterVO.getCustomerPhoneNumber()));
        customer.setCustomer800PhoneNumber(applyDefaultValue("customer800PhoneNumber", customerDigesterVO.getCustomer800PhoneNumber()));
        customer.setCustomerContactName(applyDefaultValue("customerContactName", customerDigesterVO.getCustomerContactName()));
        customer.setCustomerContactPhoneNumber(applyDefaultValue("customerContactPhoneNumber", customerDigesterVO.getCustomerContactPhoneNumber()));
        customer.setCustomerFaxNumber(applyDefaultValue("customerFaxNumber", customerDigesterVO.getCustomerFaxNumber()));
        customer.setCustomerCreditApprovedByName(applyDefaultValue("customerCreditApprovedByName", customerDigesterVO.getCustomerCreditApprovedByName()));
        customer.setCustomerEmailAddress(applyDefaultValue("customerEmailAddress", customerDigesterVO.getCustomerEmailAddress()));
        return;
    }

    private void convertCustomerDateProperties() {
        java.sql.Date todayDate = dateTimeService.getCurrentSqlDate();
        
        customer.setCustomerAddressChangeDate(todayDate);
        customer.setCustomerRecordAddDate(todayDate);
        customer.setCustomerLastActivityDate(todayDate);
        
        customer.setCustomerBirthDate(convertToJavaSqlDate("customerBirthDate", applyDefaultValue("customerBirthDate", customerDigesterVO.getCustomerBirthDate())));
        return;
    }
    
    private void convertCustomerKualiDecimalProperties() {
        customer.setCustomerCreditLimitAmount(convertToKualiDecimal("customerCreditLimitAmount", applyDefaultValue("customerCreditLimitAmount", customerDigesterVO.getCustomerCreditLimitAmount())));
        return;
    }
    
    private void convertCustomerBooleanProperties() {
        
        customer.setActive(convertToLittleBoolean("customerActiveIndicator", applyDefaultValue("customerActiveIndicator", customerDigesterVO.getCustomerActiveIndicator())));
        customer.setCustomerTaxExemptIndicator(convertToLittleBoolean("customerTaxExemptIndicator", applyDefaultValue("customerTaxExemptIndicator", customerDigesterVO.getCustomerTaxExemptIndicator())));
        return;
    }

    private void convertCustomerAddresses() {
        CustomerAddress customerAddress = null;
        for (CustomerAddressDigesterVO addressVO : customerDigesterVO.getCustomerAddresses()) {
            customerAddress = convertCustomerAddress(addressVO, customer.getCustomerNumber());
            customer.getCustomerAddresses().add(customerAddress);
        }
        return;
    }

    private CustomerAddress convertCustomerAddress(CustomerAddressDigesterVO customerAddressDigesterVO, String customerNumber) {
        CustomerAddress customerAddress = new CustomerAddress();
        
        //  link the customerAddress to the parent customer
        customerAddress.setCustomerNumber(customerNumber);
        
        customerAddress.setCustomerAddressName(applyDefaultValue("customerAddressName", customerAddressDigesterVO.getCustomerAddressName()));
        customerAddress.setCustomerLine1StreetAddress(applyDefaultValue("customerLine1StreetAddress", customerAddressDigesterVO.getCustomerLine1StreetAddress()));
        customerAddress.setCustomerLine2StreetAddress(applyDefaultValue("customerLine2StreetAddress", customerAddressDigesterVO.getCustomerLine2StreetAddress()));
        customerAddress.setCustomerCityName(applyDefaultValue("customerCityName", customerAddressDigesterVO.getCustomerCityName()));
        customerAddress.setCustomerStateCode(applyDefaultValue("customerStateCode", customerAddressDigesterVO.getCustomerStateCode()));
        customerAddress.setCustomerZipCode(applyDefaultValue("customerZipCode", customerAddressDigesterVO.getCustomerZipCode()));
        customerAddress.setCustomerCountryCode(applyDefaultValue("customerCountryCode", customerAddressDigesterVO.getCustomerCountryCode()));
        customerAddress.setCustomerAddressInternationalProvinceName(applyDefaultValue("customerAddressInternationalProvinceName", customerAddressDigesterVO.getCustomerAddressInternationalProvinceName()));
        customerAddress.setCustomerInternationalMailCode(applyDefaultValue("customerInternationalMailCode", customerAddressDigesterVO.getCustomerInternationalMailCode()));
        customerAddress.setCustomerEmailAddress(applyDefaultValue("customerEmailAddress", customerAddressDigesterVO.getCustomerEmailAddress()));
        customerAddress.setCustomerAddressTypeCode(applyDefaultValue("customerAddressTypeCode", customerAddressDigesterVO.getCustomerAddressTypeCode()));
        
        customerAddress.setCustomerAddressEndDate(convertToJavaSqlDate("customerAddressEndDate", applyDefaultValue("customerAddressEndDate", customerAddressDigesterVO.getCustomerAddressEndDate())));

        return customerAddress;
    }
    
    /**
     * 
     * This method converts a string value that may represent a date into a java.sql.Date.
     * If the value is blank (whitespace, empty, null) then a null Date object is returned.  If 
     * the value cannot be converted to a java.sql.Date, then a RuntimException or ConversionException 
     * will be thrown.
     * 
     * @param propertyName Name of the field whose value is being converted.
     * @param dateValue The value being converted.
     * @param errorMap The errorMap to add conversion errors to.
     * @return A valid java.sql.Date with the converted value, if possible.
     */
    private java.sql.Date convertToJavaSqlDate(String propertyName, String dateValue) {
        
        if (StringUtils.isBlank(dateValue)) {
            return null;
        }
        
        java.sql.Date date = null;
        SqlDateConverter converter = new SqlDateConverter();
        Object obj = null;
        try {
            obj = converter.convert(java.sql.Date.class, dateValue); 
        }
        catch (ConversionException e) {
            LOG.error("Failed to convert the value [" + dateValue + "] from field [" + propertyName + "] to a java.sql.Date.");
            addError(propertyName, java.sql.Date.class, dateValue, "Could not convert value to target type.");
            return null;
        }
        try {
            date = (java.sql.Date) obj;
        }
        catch (Exception e) {
            LOG.error("Failed to cast the converters results to a java.sql.Date.");
            addError(propertyName, java.sql.Date.class, dateValue, "Could not convert value to target type.");
            return null;
        }
        
        if (!(obj instanceof java.sql.Date)) {
            LOG.error("Failed to convert the value [" + dateValue + "] from field [" + propertyName + "] to a java.sql.Date.");
            addError(propertyName, java.sql.Date.class, dateValue, "Could not convert value to target type.");
            return null;
        }
        return date;
    }
    
    /**
     * 
     * This method converts a string, which may be blank, null or whitespace, into a KualiDecimal, if possible.  
     * 
     * A null, blank, or whitespace value passed in will result in a Null KualiDecimal value returned.  A value passed in 
     * which is not blank, but cannot otherwise be converted to a KualiDecimal, will throw a ValueObjectConverterException.  
     * Otherwise, the value will be converted to a KualiDecimal and returned.
     * 
     * @param propertyName The name of the property being converted (used for exception handling).
     * @param stringDecimal The value being passed in, which will be converted to a KualiDecimal.
     * @param errorMap The errorMap to add conversion errors to.
     * @return A valid KualiDecimal value.  If the method returns a value, then it will be a legitimate value.
     */
    private KualiDecimal convertToKualiDecimal(String propertyName, String stringDecimal) {
        if (StringUtils.isBlank(stringDecimal)) {
            return null;
        }
        KualiDecimal kualiDecimal = null;
        try {
            kualiDecimal = new KualiDecimal(stringDecimal);
        }
        catch (NumberFormatException e) {
            LOG.error("Failed to convert the value [" + stringDecimal + "] from field [" + propertyName + "] to a KualiDecimal.");
            addError(propertyName, KualiDecimal.class, stringDecimal, "Could not convert value to target type.");
            return null;
        }
        return kualiDecimal;
    }
    
    /**
     * 
     * This method converts a String into a boolean.  
     * 
     * It will return 
     * @param propertyName
     * @param stringBoolean
     * @param errorMap The errorMap to add conversion errors to.
     * @return
     */
    private static final boolean convertToLittleBoolean(String propertyName, String stringBoolean) {
        if (StringUtils.isBlank(stringBoolean)) {
            return false;
        }
        if ("Y".equalsIgnoreCase(stringBoolean)) {
            return true;
        }
        if ("YES".equalsIgnoreCase(stringBoolean)) {
            return true;
        }
        if ("TRUE".equalsIgnoreCase(stringBoolean)) {
            return true;
        }
        if ("T".equalsIgnoreCase(stringBoolean)) {
            return true;
        }
        if ("1".equalsIgnoreCase(stringBoolean)) {
            return true;
        }
        return false;
    }

    /**
     * 
     * This method is used to apply any DataDictionary default value rules, if appropriate.
     * 
     * If the incoming value isnt blank, empty, or null, then nothing happens, and the method 
     * returns the incoming value.
     * 
     * If the value is empty/blank/null, then the MaintenanceDocumentDictionaryService is consulted, 
     * and if there is a defaultValue applied to this field, then its used in place of the empty/blank/null.
     * 
     * @param propertyName The propertyName of the field (must match case exactly to work).
     * @param batchValue The invoming value from the batch file, which at this point is always still a string.
     * @return If the original value is null/blank/empty, then if a default value is configured, that default value 
     *         is returned.  If no default value is configured, then the original value (trimmed) is returned.  Otherwise, 
     *         if the original incoming value is not empty/null/blank, then the original value is immediately returned.
     */
    private String applyDefaultValue(String propertyName, String batchValue) {
        
        //  short-circuit if value isnt empty/blank/null, as default wouldnt apply anyway
        if (StringUtils.isNotBlank(batchValue)) {
            return batchValue;
        }
        
        //  if its a string, we try to normalize it to null if empty/blank
        String incomingValue;
        if (StringUtils.isBlank(batchValue)) {
            incomingValue = null;
        }
        else {
            incomingValue = StringUtils.trimToNull(batchValue);
        }
        
        //  apply the default value from DD if exists
        String defaultValue = maintDocDDService.getFieldDefaultValue(BO_CLASS, propertyName);
        if (incomingValue == null && StringUtils.isNotBlank(defaultValue)) {
            LOG.info("Applied DD default value of '" + defaultValue + "' to field [" + propertyName + "].");
            return defaultValue;
        }
        else {
            return ((incomingValue == null) ? "" : incomingValue);
        }
    }
    
    private void addError(String propertyName, Class<?> propertyClass, String origValue, String description) {
        LOG.error("Failed conversion on field [" + propertyName + "] with value: '" + origValue + "': " + description);
        errors.addError(customerName, propertyName, propertyClass, origValue, description);
    }
    
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setMaintDocDDService(MaintenanceDocumentDictionaryService maintDocDDService) {
        this.maintDocDDService = maintDocDDService;
    }

}
