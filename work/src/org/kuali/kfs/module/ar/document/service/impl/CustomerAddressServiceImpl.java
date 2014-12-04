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
package org.kuali.kfs.module.ar.document.service.impl;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.businessobject.CustomerAddress;
import org.kuali.kfs.module.ar.document.service.CustomerAddressService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.SequenceAccessorService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class CustomerAddressServiceImpl implements CustomerAddressService {
    private BusinessObjectService businessObjectService;
    private SequenceAccessorService sequenceAccessorService;
    private DateTimeService dateTimeService;
    protected static final String CUST_ADDR_ID_SEQ = "CUST_ADDR_ID_SEQ";

    @Override
    @SuppressWarnings("unchecked")
    public CustomerAddress getByPrimaryKey(String customerNumber, Integer customerAddressIdentifier) {

        CustomerAddress customerAddress = null;
        if (StringUtils.isNotBlank(customerNumber) && ObjectUtils.isNotNull(customerAddressIdentifier)) {
            Map criteria = new HashMap();
            criteria.put(KFSPropertyConstants.CUSTOMER_NUMBER, customerNumber);
            criteria.put(KFSPropertyConstants.CUSTOMER_ADDRESS_IDENTIFIER, customerAddressIdentifier);

            customerAddress = businessObjectService.findByPrimaryKey(CustomerAddress.class, criteria);
        }
        return customerAddress;
    }

    @Override
    @SuppressWarnings("unchecked")
    public CustomerAddress getPrimaryAddress(String customerNumber) {
        CustomerAddress primaryAddress = null;
        if (StringUtils.isNotBlank(customerNumber)) {
            Map criteria = new HashMap();
            criteria.put(KFSPropertyConstants.CUSTOMER_NUMBER, customerNumber);
            criteria.put(KFSPropertyConstants.CUSTOMER_ADDRESS_TYPE_CODE, "P");

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
    @Override
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
    @Override
    public boolean customerAddressExists(String customerNumber, Integer customerAddressIdentifier) {
        return ObjectUtils.isNotNull(getByPrimaryKey(customerNumber, customerAddressIdentifier));
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerAddressService#getNextCustomerAddressIdentifier()
     */
    @Override
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
