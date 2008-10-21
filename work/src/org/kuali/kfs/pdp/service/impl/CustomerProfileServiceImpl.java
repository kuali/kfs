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
package org.kuali.kfs.pdp.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.pdp.businessobject.CustomerBank;
import org.kuali.kfs.pdp.businessobject.CustomerProfile;
import org.kuali.kfs.pdp.service.CustomerProfileService;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.springframework.transaction.annotation.Transactional;


/**
 * @author jsissom
 */
@Transactional
public class CustomerProfileServiceImpl implements CustomerProfileService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerProfileServiceImpl.class);

    private BusinessObjectService businessObjectService;
    
    public List getAll() {
        return (List) this.businessObjectService.findAll(CustomerProfile.class);
    }

    public CustomerProfile get(Integer id) {
        Map primaryKeys = new HashMap();
        primaryKeys.put("id", id);
        return (CustomerProfile) this.businessObjectService.findByPrimaryKey(CustomerProfile.class, primaryKeys);
    }

    public CustomerProfile get(String chartCode, String orgCode, String subUnitCode) {
        Map fieldValues = new HashMap();
        
        fieldValues.put("chartCode", chartCode);
        fieldValues.put("orgCode", orgCode);
        fieldValues.put("subUnitCode", subUnitCode);
        
        return (CustomerProfile) this.businessObjectService.findMatching(CustomerProfile.class, fieldValues);
    }

    public void save(CustomerProfile cp) {
        this.businessObjectService.save(cp);
    }

    public void saveCustomerBank(CustomerBank cb) {
        this.businessObjectService.save(cb);
    }

    public void deleteCustomerBank(CustomerBank cb) {
        this.businessObjectService.delete(cb);
    }

    /**
     * Gets the business object service
     * 
     * @return
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }
    
    /**
     * Sets the business object service
     * 
     * @param businessObjectService
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
