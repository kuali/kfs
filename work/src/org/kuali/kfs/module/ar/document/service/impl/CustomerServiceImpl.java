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
package org.kuali.module.ar.service.impl;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.SequenceAccessorService;
import org.kuali.module.ar.bo.Customer;
import org.kuali.module.ar.dao.CustomerDao;
import org.kuali.module.ar.service.CustomerService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class CustomerServiceImpl implements CustomerService {

    private CustomerDao customerDao;
    private SequenceAccessorService sequenceAccessorService;
    private BusinessObjectService businessObjectService;
    private static final String CUSTOMER_NUMBER_SEQUENCE = "CUST_NBR_SEQ";

    /**
     * @see org.kuali.module.ar.service.CustomerService#getByPrimaryKey(java.lang.String)
     */
    public Customer getByPrimaryKey(String customerNumber) {
        return customerDao.getByPrimaryId(customerNumber);
    }

    public CustomerDao getCustomerDao() {
        return customerDao;
    }

    public void setCustomerDao(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    /**
     * @see org.kuali.module.ar.service.CustomerService#getNextCustomerNumber(org.kuali.module.ar.bo.Customer)
     */
    public String getNextCustomerNumber(Customer newCustomer) {
        Long customerNumberSuffix = sequenceAccessorService.getNextAvailableSequenceNumber(CUSTOMER_NUMBER_SEQUENCE);
        String customerNumberPrefix = newCustomer.getCustomerName().substring(0, 3);
        String customerNumber = customerNumberPrefix + String.valueOf(customerNumberSuffix);

        return customerNumber;
    }

    /**
     * This method gets the sequenceAccessorService
     * 
     * @return the sequenceAccessorService
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

    /**
     * @see org.kuali.module.ar.service.CustomerService#getCustomerByName(java.lang.String)
     */
    public Customer getCustomerByName(String customerName) {
        return customerDao.getByName(customerName);
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

}
