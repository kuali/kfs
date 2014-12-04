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

