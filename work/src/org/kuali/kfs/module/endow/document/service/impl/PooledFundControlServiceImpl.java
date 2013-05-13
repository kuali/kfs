/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.endow.businessobject.PooledFundControl;
import org.kuali.kfs.module.endow.document.service.PooledFundControlService;
import org.kuali.rice.krad.service.BusinessObjectService;

public class PooledFundControlServiceImpl implements PooledFundControlService {
    
    private BusinessObjectService businessObjectService;

    /**
     * @see org.kuali.kfs.module.endow.document.service.PooledFundControlService#getByPrimaryKey(java.lang.String)
     */
    public PooledFundControl getByPrimaryKey(String id) {
        PooledFundControl pooledFundControl = null;
        if (StringUtils.isNotBlank(id)) {
            Map criteria = new HashMap();
            criteria.put("pooledSecurityID", id);

            pooledFundControl = (PooledFundControl) businessObjectService.findByPrimaryKey(PooledFundControl.class, criteria);
        }
        return pooledFundControl;
    }

    /**
     * This method gets the businessObjectService.
     * 
     * @return businessObjectService
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * This method sets the businessObjectService
     * 
     * @param businessObjectService
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}

