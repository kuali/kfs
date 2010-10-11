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

import org.kuali.kfs.module.endow.EndowParameterKeyConstants;
import org.kuali.kfs.module.endow.dataaccess.KemidFeeDao;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.kns.dao.jdbc.PlatformAwareDaoBaseJdbc;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.ParameterService;
import org.springframework.dao.DataAccessException;

public class KemidFeeDaoOjb extends PlatformAwareDaoBaseJdbc implements KemidFeeDao {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KemidFeeDaoOjb.class);
    
    protected BusinessObjectService businessObjectService;
    protected KEMService kemService;
    protected ParameterService parameterService;
    
    /** 
     * @see org.kuali.kfs.module.endow.dataaccess.KemidFeeDao#updateKemidFeeWaivedFeeYearToDateToZero()
     */
    public boolean updateKemidFeeWaivedFeeYearToDateToZero() {
        boolean updated = true;
        
        if (!systemParametersForUpdateWaiverFeeAmounts()) {
            return false;
        }
        
        java.sql.Date firstDayAfterFiscalYear = kemService.getFirstDayAfterFiscalYearEndDayAndMonth();
        
        if (firstDayAfterFiscalYear.equals(kemService.getCurrentDate())) {
            try {
                getSimpleJdbcTemplate().update("UPDATE END_KEMID_FEE_T SET WAIVED_FEE_YTD = 0");
                return true;
            }
            catch (DataAccessException dae) {
                return false;
            }
        }
        
        return updated;        
    }

    /**
     * This method checks if the System parameters have been set up for this batch job.
     * @result return true if the system parameters exist, else false
     */
    protected boolean systemParametersForUpdateWaiverFeeAmounts() {
        LOG.info("systemParametersForUpdateWaiverFeeAmounts() started.");
        
        boolean systemParameterExists = true;
        
        // check to make sure the system parameter has been setup...
        if (!getParameterService().parameterExists(KfsParameterConstants.ENDOWMENT_BATCH.class, EndowParameterKeyConstants.FISCAL_YEAR_END_MONTH_AND_DAY)) {
          LOG.warn("FISCAL_YEAR_END_MONTH_AND_DAY System parameter does not exist in the parameters list.  The job can not continue without this parameter");
          return false;
        }
        
        LOG.info("systemParametersForUpdateWaiverFeeAmounts() ended.");
        
        return systemParameterExists;
    }
    
    /**
     * This method gets the businessObjectService.
     * @return businessObjectService
     */
    protected BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * This method sets the businessObjectService
     * @param businessObjectService
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
    
    /**
     * Gets the kemService.
     * @return kemService
     */
    protected KEMService getKemService() {
        return kemService;
    }

    /**
     * Sets the kemService.
     * @param kemService
     */
    public void setKemService(KEMService kemService) {
        this.kemService = kemService;
    }

    /**
     * Gets the parameterService attribute.
     * @return Returns the parameterService.
     */    
    protected ParameterService getParameterService() {
        return parameterService;
    }

    /**
     * Sets the parameterService attribute value.
     * @param parameterService The parameterService to set.
     */    
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
}
