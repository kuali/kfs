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
package org.kuali.kfs.module.ar.document.service.impl;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.businessobject.CustomerAddress;
import org.kuali.kfs.module.ar.dataaccess.CustomerAddressDao;
import org.kuali.kfs.module.ar.document.service.CustomerAddressService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.SequenceAccessorService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class CustomerAddressServiceImpl implements CustomerAddressService {
    private CustomerAddressDao customerAddressDao;

    private BusinessObjectService businessObjectService;
    private SequenceAccessorService sequenceAccessorService;
    private DateTimeService dateTimeService;
    protected static final String CUST_ADDR_ID_SEQ = "CUST_ADDR_ID_SEQ";

    @SuppressWarnings("unchecked")
    public CustomerAddress getByPrimaryKey(String customerNumber, Integer customerAddressIdentifier) {

        CustomerAddress customerAddress = null;
        if (StringUtils.isNotBlank(customerNumber) && ObjectUtils.isNotNull(customerAddressIdentifier)) {
            Map criteria = new HashMap();
            criteria.put("customerNumber", customerNumber);
            criteria.put("customerAddressIdentifier", customerAddressIdentifier);

            customerAddress = (CustomerAddress) businessObjectService.findByPrimaryKey(CustomerAddress.class, criteria);
        }
        return customerAddress;
    }
    
    @SuppressWarnings("unchecked")
    public CustomerAddress getPrimaryAddress(String customerNumber) {
        CustomerAddress primaryAddress = null;
        if (StringUtils.isNotBlank(customerNumber)) {
            Map criteria = new HashMap();
            criteria.put("customerNumber", customerNumber);
            criteria.put("customerAddressTypeCode", "P");
            
            primaryAddress = (CustomerAddress)businessObjectService.findMatching(CustomerAddress.class, criteria).iterator().next();
        }
        
        return primaryAddress;
    }

    /**
     * This method returns true if customer address is active
     * 
     * @param customerNumber
     * @param customerAddressIdentifier
     * @return
     */
    public boolean customerAddressActive(String customerNumber, Integer customerAddressIdentifier) {
        
        CustomerAddress customerAddress = getByPrimaryKey(customerNumber, customerAddressIdentifier);

        if (ObjectUtils.isNotNull(customerAddress)) {
            if (ObjectUtils.isNotNull(customerAddress.getCustomerAddressEndDate())) {
                Timestamp currentDateTimestamp = new Timestamp(dateTimeService.getCurrentDate().getTime());
                Timestamp addressEndDateTimestamp = new Timestamp(customerAddress.getCustomerAddressEndDate().getTime());
                if (addressEndDateTimestamp.before(currentDateTimestamp)) {
                    return false;
                }
            }
        }
        return true;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerAddressService#customerAddressExists(java.lang.String, java.lang.Integer)
     */
    public boolean customerAddressExists(String customerNumber, Integer customerAddressIdentifier) {
        return ObjectUtils.isNotNull(getByPrimaryKey(customerNumber, customerAddressIdentifier));
    }


    /**
     * This method sets customer address dao
     * 
     * @return
     */
    public CustomerAddressDao getCustomerAddressDao() {
        return customerAddressDao;
    }

    /**
     * This method gets customer address dao
     * 
     * @param customerAddressDao
     */
    public void setCustomerAddressDao(CustomerAddressDao customerAddressDao) {
        this.customerAddressDao = customerAddressDao;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerAddressService#getNextCustomerAddressIdentifier()
     */
    public Integer getNextCustomerAddressIdentifier() {

        Long nextId = sequenceAccessorService.getNextAvailableSequenceNumber(
                CUST_ADDR_ID_SEQ, CustomerAddress.class);

        return nextId.intValue();

    }

    /**
     * This method gets the sequenceAccessorService
     * 
     * @return
     */
    public SequenceAccessorService getSequenceAccessorService() {
        return sequenceAccessorService;
    }

    /**
     * This method sets the sequenceAccessorService
     * 
     * @param sequenceAccessorService
     */
    public void setSequenceAccessorService(SequenceAccessorService sequenceAccessorService) {
        this.sequenceAccessorService = sequenceAccessorService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }
}
