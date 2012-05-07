/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.kfs.module.ar.businessobject;

import java.sql.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.document.service.CustomerAddressService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.location.api.LocationConstants;
import org.kuali.rice.location.framework.country.CountryEbo;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class CustomerAddress extends PersistableBusinessObjectBase implements Comparable<CustomerAddress> {

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

    private CustomerAddressType customerAddressType;
    private Customer customer;
    private CountryEbo customerCountry;

    /**
     * Default constructor.
     */
    public CustomerAddress() {

    }

    /**
     * Gets the customerNumber attribute.
     * 
     * @return Returns the customerNumber
     */
    public String getCustomerNumber() {
        return customerNumber;
    }

    /**
     * Sets the customerNumber attribute.
     * 
     * @param customerNumber The customerNumber to set.
     */
    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }


    /**
     * Gets the customerAddressIdentifier attribute.
     * 
     * @return Returns the customerAddressIdentifier
     */
    public Integer getCustomerAddressIdentifier() {
        return customerAddressIdentifier;
    }

    /**
     * Sets the customerAddressIdentifier attribute.
     * 
     * @param customerAddressIdentifier The customerAddressIdentifier to set.
     */
    public void setCustomerAddressIdentifier(Integer customerAddressIdentifier) {
        this.customerAddressIdentifier = customerAddressIdentifier;
    }


    /**
     * Gets the customerAddressName attribute.
     * 
     * @return Returns the customerAddressName
     */
    public String getCustomerAddressName() {
        return customerAddressName;
    }

    /**
     * Sets the customerAddressName attribute.
     * 
     * @param customerAddressName The customerAddressName to set.
     */
    public void setCustomerAddressName(String customerAddressName) {
        this.customerAddressName = customerAddressName;
    }


    /**
     * Gets the customerLine1StreetAddress attribute.
     * 
     * @return Returns the customerLine1StreetAddress
     */
    public String getCustomerLine1StreetAddress() {
        return customerLine1StreetAddress;
    }

    /**
     * Sets the customerLine1StreetAddress attribute.
     * 
     * @param customerLine1StreetAddress The customerLine1StreetAddress to set.
     */
    public void setCustomerLine1StreetAddress(String customerLine1StreetAddress) {
        this.customerLine1StreetAddress = customerLine1StreetAddress;
    }


    /**
     * Gets the customerLine2StreetAddress attribute.
     * 
     * @return Returns the customerLine2StreetAddress
     */
    public String getCustomerLine2StreetAddress() {
        return customerLine2StreetAddress;
    }

    /**
     * Sets the customerLine2StreetAddress attribute.
     * 
     * @param customerLine2StreetAddress The customerLine2StreetAddress to set.
     */
    public void setCustomerLine2StreetAddress(String customerLine2StreetAddress) {
        this.customerLine2StreetAddress = customerLine2StreetAddress;
    }


    /**
     * Gets the customerCityName attribute.
     * 
     * @return Returns the customerCityName
     */
    public String getCustomerCityName() {
        return customerCityName;
    }

    /**
     * Sets the customerCityName attribute.
     * 
     * @param customerCityName The customerCityName to set.
     */
    public void setCustomerCityName(String customerCityName) {
        this.customerCityName = customerCityName;
    }


    /**
     * Gets the customerStateCode attribute.
     * 
     * @return Returns the customerStateCode
     */
    public String getCustomerStateCode() {
        return customerStateCode;
    }

    /**
     * Sets the customerStateCode attribute.
     * 
     * @param customerStateCode The customerStateCode to set.
     */
    public void setCustomerStateCode(String customerStateCode) {
        this.customerStateCode = customerStateCode;
    }


    /**
     * Gets the customerZipCode attribute.
     * 
     * @return Returns the customerZipCode
     */
    public String getCustomerZipCode() {
        return customerZipCode;
    }

    /**
     * Sets the customerZipCode attribute.
     * 
     * @param customerZipCode The customerZipCode to set.
     */
    public void setCustomerZipCode(String customerZipCode) {
        this.customerZipCode = customerZipCode;
    }

    /**
     * Gets the customerAddressInternationalProvinceName attribute.
     * 
     * @return Returns the customerAddressInternationalProvinceName.
     */
    public String getCustomerAddressInternationalProvinceName() {
        return customerAddressInternationalProvinceName;
    }

    /**
     * Sets the customerAddressInternationalProvinceName attribute value.
     * 
     * @param customerAddressInternationalProvinceName The customerAddressInternationalProvinceName to set.
     */
    public void setCustomerAddressInternationalProvinceName(String customerAddressInternationalProvinceName) {
        this.customerAddressInternationalProvinceName = customerAddressInternationalProvinceName;
    }

    /**
     * Gets the customerCountryCode attribute.
     * 
     * @return Returns the customerCountryCode.
     */
    public String getCustomerCountryCode() {
        return customerCountryCode;
    }

    /**
     * Sets the customerCountryCode attribute value.
     * 
     * @param customerCountryCode The customerCountryCode to set.
     */
    public void setCustomerCountryCode(String customerCountryCode) {
        this.customerCountryCode = customerCountryCode;
    }

    /**
     * Gets the customerInternationalMailCode attribute.
     * 
     * @return Returns the customerInternationalMailCode
     */
    public String getCustomerInternationalMailCode() {
        return customerInternationalMailCode;
    }

    /**
     * Sets the customerInternationalMailCode attribute.
     * 
     * @param customerInternationalMailCode The customerInternationalMailCode to set.
     */
    public void setCustomerInternationalMailCode(String customerInternationalMailCode) {
        this.customerInternationalMailCode = customerInternationalMailCode;
    }

    /**
     * Gets the customerEmailAddress attribute.
     * 
     * @return Returns the customerEmailAddress
     */
    public String getCustomerEmailAddress() {
        return customerEmailAddress;
    }

    /**
     * Sets the customerEmailAddress attribute.
     * 
     * @param customerEmailAddress The customerEmailAddress to set.
     */
    public void setCustomerEmailAddress(String customerEmailAddress) {
        this.customerEmailAddress = customerEmailAddress;
    }


    /**
     * Gets the customerAddressTypeCode attribute.
     * 
     * @return Returns the customerAddressTypeCode
     */
    public String getCustomerAddressTypeCode() {
        return customerAddressTypeCode;
    }

    /**
     * Sets the customerAddressTypeCode attribute.
     * 
     * @param customerAddressTypeCode The customerAddressTypeCode to set.
     */
    public void setCustomerAddressTypeCode(String customerAddressTypeCode) {
        this.customerAddressTypeCode = customerAddressTypeCode;
    }


    /**
     * Gets the customerAddressEndDate attribute.
     * 
     * @return Returns the customerAddressEndDate
     */
    public Date getCustomerAddressEndDate() {
        return customerAddressEndDate;
    }

    /**
     * Sets the customerAddressEndDate attribute.
     * 
     * @param customerAddressEndDate The customerAddressEndDate to set.
     */
    public void setCustomerAddressEndDate(Date customerAddressEndDate) {
        this.customerAddressEndDate = customerAddressEndDate;
    }

    /**
     * Gets the customerAddressType attribute.
     * 
     * @return Returns the customerAddressType
     */
    public CustomerAddressType getCustomerAddressType() {
        return customerAddressType;
    }

    /**
     * Sets the customerAddressType attribute.
     * 
     * @param customerAddressType The customerAddressType to set.
     * @deprecated
     */
    public void setCustomerAddressType(CustomerAddressType customerAddressType) {
        this.customerAddressType = customerAddressType;
    }

    /**
     * Gets the customer attribute.
     * 
     * @return Returns the customer.
     */
    public Customer getCustomer() {
        return customer;
    }

    /**
     * Sets the customer attribute value.
     * 
     * @param customer The customer to set.
     * @deprecated
     */
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    /**
     * Gets the customerCountry attribute.
     * 
     * @return Returns the customerCountry.
     */
    public CountryEbo getCustomerCountry() {
        if (StringUtils.isBlank(customerCountryCode)) {
            customerCountry = null;
        } else {
            if (customerCountry == null || !StringUtils.equals(customerCountry.getCode(), customerCountryCode)) {
                ModuleService moduleService = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(CountryEbo.class);
                if ( moduleService != null ) {
                    Map<String,Object> keys = new HashMap<String, Object>(1);
                    keys.put(LocationConstants.PrimaryKeyConstants.CODE, customerCountryCode);
                    customerCountry = moduleService.getExternalizableBusinessObject(CountryEbo.class, keys);
                } else {
                    throw new RuntimeException( "CONFIGURATION ERROR: No responsible module found for EBO class.  Unable to proceed." );
                }
            }
        }
        return customerCountry;    
    }

    /**
     * Sets the customerCountry attribute value.
     * 
     * @param customerCountry The customerCountry to set.
     * @deprecated
     */
    public void setCustomerCountry(CountryEbo customerCountry) {
        this.customerCountry = customerCountry;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    @SuppressWarnings("unchecked")
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put("customerNumber", this.customerNumber);
        if (this.customerAddressIdentifier != null) {
            m.put("customerAddressIdentifier", this.customerAddressIdentifier.toString());
        }
        return m;
    }


    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(CustomerAddress address) {
        if (this.getCustomerNumber() != null && address.getCustomerNumber() != null && !this.getCustomerNumber().equalsIgnoreCase(address.getCustomerNumber())) {
            return -1;
        }
        if (this.getCustomerAddressName() != null && address.getCustomerAddressName() != null && !this.getCustomerAddressName().equalsIgnoreCase(address.getCustomerAddressName())) {
            return -1;
        }
        if (this.getCustomerLine1StreetAddress() != null && address.getCustomerLine1StreetAddress() != null && !this.getCustomerLine1StreetAddress().equalsIgnoreCase(address.getCustomerLine1StreetAddress())) {
            return -1;
        }
        if (this.getCustomerLine2StreetAddress() != null && address.getCustomerLine2StreetAddress() != null && !this.getCustomerLine2StreetAddress().equalsIgnoreCase(address.getCustomerLine2StreetAddress())
            || (this.getCustomerLine2StreetAddress() == null && address.getCustomerLine2StreetAddress() != null) || (this.getCustomerLine2StreetAddress() != null && address.getCustomerLine2StreetAddress() == null)) {
            return -1;
        }
        if (this.getCustomerCityName() != null && address.getCustomerCityName() != null && !this.getCustomerCityName().equalsIgnoreCase(address.getCustomerCityName())) {
            return -1;
        }
        if (this.getCustomerStateCode() != null && address.getCustomerStateCode() != null && !this.getCustomerStateCode().equalsIgnoreCase(address.getCustomerStateCode())
            || (this.getCustomerStateCode() == null && address.getCustomerStateCode() != null) || (this.getCustomerStateCode() != null && address.getCustomerStateCode() == null)) {
            return -1;
        }
        if (this.getCustomerZipCode() != null && address.getCustomerZipCode() != null && !this.getCustomerZipCode().equalsIgnoreCase(address.getCustomerZipCode())
            || (this.getCustomerZipCode() == null && address.getCustomerZipCode() != null) || (this.getCustomerZipCode() != null && address.getCustomerZipCode() == null)) {
            return -1;
        }
        if (this.getCustomerCountryCode() != null && address.getCustomerCountryCode() != null && !this.getCustomerCountryCode().equalsIgnoreCase(address.getCustomerCountryCode())) {
            return -1;
        }
        if (this.getCustomerAddressInternationalProvinceName() != null && address.getCustomerAddressInternationalProvinceName() != null && !this.getCustomerAddressInternationalProvinceName().equalsIgnoreCase(address.getCustomerAddressInternationalProvinceName())
            || (this.getCustomerAddressInternationalProvinceName() == null && address.getCustomerAddressInternationalProvinceName() != null) || (this.getCustomerAddressInternationalProvinceName() != null && address.getCustomerAddressInternationalProvinceName() == null)) {
            return -1;
        }
        if (this.getCustomerEmailAddress() != null && address.getCustomerEmailAddress() != null && !this.getCustomerEmailAddress().equalsIgnoreCase(address.getCustomerEmailAddress())
            || (this.getCustomerEmailAddress() == null && address.getCustomerEmailAddress() != null) || (this.getCustomerEmailAddress() != null && address.getCustomerEmailAddress() == null)) {
            return -1;
        }
        if (this.getCustomerAddressTypeCode() != null && address.getCustomerAddressTypeCode() != null && !this.getCustomerAddressTypeCode().equalsIgnoreCase(address.getCustomerAddressTypeCode())
            || (this.getCustomerAddressTypeCode() == null && address.getCustomerAddressTypeCode() != null) || (this.getCustomerAddressTypeCode() != null && address.getCustomerAddressTypeCode() == null)) {
            return -1;
        }
        if (this.getCustomerAddressIdentifier() != null && address.getCustomerAddressIdentifier() != null && this.getCustomerAddressIdentifier().compareTo(address.getCustomerAddressIdentifier())!= 0
            || (this.getCustomerAddressIdentifier() == null && address.getCustomerAddressIdentifier() != null) || (this.getCustomerAddressIdentifier() != null && address.getCustomerAddressIdentifier() == null)) {
            return -1;
        }
        return 0;
    }

    @Override 
    protected void prePersist() {
        super.prePersist();
        CustomerAddressService customerAddressService = SpringContext.getBean(CustomerAddressService.class);
        int customerAddressIdentifier = customerAddressService.getNextCustomerAddressIdentifier();
        this.setCustomerAddressIdentifier(customerAddressIdentifier);
    }

}
