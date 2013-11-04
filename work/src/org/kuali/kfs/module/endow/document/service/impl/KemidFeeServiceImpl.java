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

import java.util.ArrayList;
import java.util.Collection;

import org.kuali.kfs.module.endow.EndowParameterKeyConstants;
import org.kuali.kfs.module.endow.businessobject.KemidFee;
import org.kuali.kfs.module.endow.document.service.KEMService;
import org.kuali.kfs.module.endow.document.service.KemidFeeService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * This class is the service implementation for the KemidFeeService. This is the default, Kuali provided implementation.
 */
public class KemidFeeServiceImpl implements KemidFeeService {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KemidFeeServiceImpl.class);

    private BusinessObjectService businessObjectService;
    protected KEMService kemService;
    protected ParameterService parameterService;
    
    /**
     * @see org.kuali.kfs.module.endow.document.service.KemidFeeService#getAllKemIdFee()
     */
    public Collection <KemidFee> getAllKemIdFee() {
        Collection <KemidFee> kemIdFee = new ArrayList();
        
        kemIdFee = businessObjectService.findAll(KemidFee.class);
        
        return kemIdFee;
    }
    
    /**
     * Updates the END_KEMID_FEE_T table records by setting the amounts in WAIVE_FEE_YTD to zero
     * if current date is the first day of the institution's fiscal year
     * @see org.kuali.kfs.module.endow.document.service.KemidFeeService#updateWaiverFeeYearToDateTotals()
     */
    public boolean updateWaiverFeeYearToDateTotals() {
        LOG.debug("updateWaiverFeeYearToDateTotals() process started");
       
       boolean updated = true;
       
       if (!systemParametersForUpdateWaiverFeeAmounts()) {
           return false;
       }
       
       try {
           java.sql.Date firstDayAfterFiscalYear = kemService.getFirstDayAfterFiscalYearEndDayAndMonth();
           
           if (firstDayAfterFiscalYear.equals(kemService.getCurrentDate())) {
               //update END_KEMID_FEE_T:
               Collection <KemidFee> kemidFeeRecords = getAllKemIdFee();
                  
               for (KemidFee kemidFee : kemidFeeRecords) {
                    kemidFee.setTotalWaivedFees(KualiDecimal.ZERO);
                    businessObjectService.save(kemidFee);
               }
           }
           
           return updated;
       }
       catch (Exception exception) {
           return false;
       }
    }
    
    /**
     * This method checks if the System parameters have been set up for this batch job.
     * @result return true if the system parameters exist, else false
     */
    protected boolean systemParametersForUpdateWaiverFeeAmounts() {
        LOG.debug("systemParametersForUpdateWaiverFeeAmounts() started.");
        
        boolean systemParameterExists = true;
        
        // check to make sure the system parameter has been setup...
        if (!getParameterService().parameterExists(KfsParameterConstants.ENDOWMENT_BATCH.class, EndowParameterKeyConstants.FISCAL_YEAR_END_MONTH_AND_DAY)) {
          LOG.warn("FISCAL_YEAR_END_MONTH_AND_DAY System parameter does not exist in the parameters list.  The job can not continue without this parameter");
          return false;
        }
        
        return systemParameterExists;
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
