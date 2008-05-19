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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.ar.bo.CustomerAddress;
import org.kuali.module.ar.service.CustomerAddressService;

public class CustomerAddressServiceImpl implements CustomerAddressService {

    private BusinessObjectService businessObjectService;
    
    public CustomerAddress getByPrimaryKey(String customerNumber, Integer customerAddressIdentifier) {
        
        CustomerAddress customerAddress = null;
        if (StringUtils.isNotBlank(customerNumber) && ObjectUtils.isNotNull(customerAddressIdentifier)) {
            Map criteria = new HashMap();
            criteria.put("customerNumber", customerNumber);
            criteria.put("customerAddressIdentifier", customerAddressIdentifier);

            customerAddress = (CustomerAddress) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(CustomerAddress.class, criteria);
        }        
        return customerAddress;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

}
