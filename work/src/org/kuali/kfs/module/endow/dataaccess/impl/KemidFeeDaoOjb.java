/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.dataaccess.impl;

import org.kuali.kfs.module.endow.dataaccess.KemidFeeDao;
import org.kuali.rice.kns.dao.jdbc.PlatformAwareDaoBaseJdbc;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.springframework.dao.DataAccessException;

public class KemidFeeDaoOjb extends PlatformAwareDaoBaseJdbc implements KemidFeeDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KemidFeeDaoOjb.class);
    
    private BusinessObjectService businessObjectService;
    
    /** 
     * @see org.kuali.kfs.module.endow.dataaccess.KemidFeeDao#updateKemidFeeWaivedFeeYearToDateToZero()
     */
    public boolean updateKemidFeeWaivedFeeYearToDateToZero() {
        LOG.debug("updateKemidFeeWaivedFeeYearToDateToZero() started"); 
        
        try {
            getSimpleJdbcTemplate().update("UPDATE END_KEMID_FEE_T SET WAIVED_FEE_YTD = 0");
            return true;
        }
        catch (DataAccessException dae) {
            return false;
        }
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
