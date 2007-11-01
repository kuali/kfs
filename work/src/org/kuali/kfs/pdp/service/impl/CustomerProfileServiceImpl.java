/*
 * Copyright 2007 The Kuali Foundation.
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
/*
 * Created on Jul 7, 2004
 *
 */
package org.kuali.module.pdp.service.impl;

import java.util.List;

import org.kuali.module.pdp.bo.CustomerBank;
import org.kuali.module.pdp.bo.CustomerProfile;
import org.kuali.module.pdp.dao.CustomerProfileDao;
import org.kuali.module.pdp.service.CustomerProfileService;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author jsissom
 */
@Transactional
public class CustomerProfileServiceImpl implements CustomerProfileService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerProfileServiceImpl.class);

    private CustomerProfileDao customerProfileDao;

    public List getAll() {
        return customerProfileDao.getAll();
    }

    public void setCustomerProfileDao(CustomerProfileDao c) {
        customerProfileDao = c;
    }

    public CustomerProfile get(Integer id) {
        return customerProfileDao.get(id);
    }

    public CustomerProfile get(String chartCode, String orgCode, String subUnitCode) {
        return customerProfileDao.get(chartCode, orgCode, subUnitCode);
    }

    public void save(CustomerProfile cp) {
        customerProfileDao.save(cp);
    }

    public void saveCustomerBank(CustomerBank cb) {
        customerProfileDao.saveCustomerBank(cb);
    }

    public void deleteCustomerBank(CustomerBank cb) {
        customerProfileDao.deleteCustomerBank(cb);
    }
}
