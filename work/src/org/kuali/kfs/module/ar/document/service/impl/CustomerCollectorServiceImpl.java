/*
 * Copyright 2012 The Kuali Foundation.
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

import org.kuali.kfs.module.ar.businessobject.CustomerCollector;
import org.kuali.kfs.module.ar.dataaccess.CustomerCollectorDao;
import org.kuali.kfs.module.ar.document.service.CustomerCollectorService;

/**
 * Implementation class for CustomerCollectorService.
 */
public class CustomerCollectorServiceImpl implements CustomerCollectorService {

    private CustomerCollectorDao customerCollectorDao;

    /**
     * Gets the customerCollectorDao attribute.
     * 
     * @return Returns the customerCollectorDao object.
     */
    public CustomerCollectorDao getCustomerCollectorDao() {
        return customerCollectorDao;
    }

    /**
     * Sets the customerCollectorDao attribute.
     * 
     * @param customerCollectorDao The customerCollectorDao to set.
     */
    public void setCustomerCollectorDao(CustomerCollectorDao customerCollectorDao) {
        this.customerCollectorDao = customerCollectorDao;
    }

    /**
     * @see org.kuali.kfs.module.ar.document.service.CustomerCollectorService#createCustomerCollector(org.kuali.kfs.module.ar.businessobject.CustomerCollector)
     */
    @Override
    public void createCustomerCollector(CustomerCollector customerCollector) {
        customerCollectorDao.save(customerCollector);
    }

}
